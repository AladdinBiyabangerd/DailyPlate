"""Offline, hash-bound historical QA references. Never restores active source folders."""
from functools import lru_cache
import hashlib
import io
import json
import os
from pathlib import Path, PurePosixPath
import re
import subprocess
import tempfile
import zipfile

POLICY = 'docs/qa/design-archive.json'
ACTIVE = ('design-concepts/simple-turquoise/onboarding-original-style/',
          'design-concepts/simple-turquoise/onboarding-tablet/')
KEEP = {'design-concepts/tokens.css',
        'design-concepts/simple-turquoise/implementation-guide.txt',
        'design-concepts/simple-turquoise/new-flows-guide.txt',
        'design-concepts/simple-turquoise/ui-extension-guide.txt', 'design-concepts/README.md'}


def logical_path(value):
    text = str(value)
    p = PurePosixPath(text)
    if (p.is_absolute() or '..' in p.parts or str(p) != text
            or any(c in text for c in ('\x00', '\n', '\r', ':', '\\'))):
        raise ValueError('Unsafe historical reference path')
    return text


def _git(repo, *args):
    try:
        return subprocess.check_output(['git', '-C', str(repo), *args], stderr=subprocess.PIPE)
    except (OSError, subprocess.CalledProcessError) as error:
        raise ValueError('Pinned archive object unavailable locally; never fetch automatically') from error


def _sha(data):
    return hashlib.sha256(data).hexdigest()


class Archive:
    def __init__(self, root, policy, repo):
        self.root, self.repo = Path(root), Path(repo)
        config = json.loads(policy)
        self.commit = config['archive_commit']
        expected = config['manifest_sha256']
        if not re.fullmatch('[0-9a-f]{40}', self.commit) or not re.fullmatch('[0-9a-f]{64}', expected):
            raise ValueError('Archive requires full pinned commit/manifest hashes')
        parents = _git(repo, 'rev-list', '--parents', '-n', '1', self.commit).split()
        if parents != [self.commit.encode()]:
            raise ValueError('Historical archive must be a design-only root commit')
        raw = _git(repo, 'show', self.commit + ':MANIFEST.json')
        if _sha(raw) != expected:
            raise ValueError('Archive manifest hash mismatch')
        manifest = json.loads(raw)
        self.records = {}
        for record in manifest['records']:
            dest = logical_path(record['archive_path'])
            if not dest.startswith('current/'):
                continue  # Same-path older variants are not canonical QA references.
            path = logical_path(record['original_path'])
            if (not path.startswith('design-concepts/') or path.startswith(ACTIVE) or path in KEEP):
                raise ValueError('Archive cannot substitute active/shared/current references')
            member = record.get('archive_member')
            if dest != 'current/' + path + ('.zip' if member else ''):
                raise ValueError('Archive path does not match its historical logical path')
            if member and (logical_path(member) != Path(path).name):
                raise ValueError('Unsafe archive member')
            if (path in self.records or not re.fullmatch('[0-9a-f]{64}', record['sha256'])
                    or not re.fullmatch('[0-9a-f]{40}', record['git_blob'])
                    or not isinstance(record['size'], int) or not 0 <= record['size'] <= 128 * 1024**2):
                raise ValueError('Invalid or duplicate historical record')
            self.records[path] = record
        if len(self.records) != manifest['current_entries']:
            raise ValueError('Archive current-entry count mismatch')
        self.temp = tempfile.TemporaryDirectory(prefix='dailyplate-historical-qa-')

    def verify(self, path, data):
        r = self.records[path]
        blob = hashlib.sha1(b'blob ' + str(len(data)).encode() + b'\0' + data).hexdigest()
        if len(data) != r['size'] or _sha(data) != r['sha256'] or blob != r['git_blob']:
            raise ValueError('Historical source bytes/hash mismatch')

    def file(self, path):
        if path not in self.records:
            raise ValueError('Missing reference is not in the pinned historical archive')
        r = self.records[path]
        cache = Path(self.temp.name) / (r['sha256'] + Path(path).suffix)
        if not cache.exists():
            raw = _git(self.repo, 'show', self.commit + ':' + r['archive_path'])
            if r.get('archive_member'):
                with zipfile.ZipFile(io.BytesIO(raw)) as z:
                    info = z.getinfo(r['archive_member'])
                    if info.file_size != r['size']:
                        raise ValueError('Historical ZIP member size mismatch')
                    raw = z.read(info)
            self.verify(path, raw)
            cache.write_bytes(raw)
        self.verify(path, cache.read_bytes())
        return cache


@lru_cache(maxsize=16)
def _load(root, policy, repo):
    return Archive(root, policy, repo)


def archive(root):
    root = Path(root).resolve()
    policy = root / POLICY
    if not policy.is_file():
        return None
    if not policy.resolve().is_relative_to(root):
        raise ValueError('Unsafe archive policy')
    # Real worktrees use their own Git objects. Only the existing CI snapshot
    # runner supplies its object repository for snapshots without .git.
    repo = root if (root / '.git').exists() else Path(os.environ.get('CALLMOOD_DESIGN_OBJECT_REPO', root))
    return _load(str(root), policy.read_bytes(), str(repo.resolve()))


def reference_file(root, relative):
    root = Path(root).resolve()
    relative = logical_path(relative)
    path = (root / relative).resolve()
    if not path.is_relative_to(root):
        raise ValueError('Unsafe reference symlink')
    if path.is_file():
        if relative.startswith(ACTIVE) or relative in KEEP:
            return path
        store = archive(root)
        if store and relative in store.records:
            store.verify(relative, path.read_bytes())
        return path
    if relative.startswith(ACTIVE) or relative in KEEP:
        raise ValueError('Missing active/shared reference; archive substitution is forbidden')
    store = archive(root)
    if store is None:
        raise ValueError('Missing historical reference and no pinned archive policy')
    return store.file(relative)


def archived_deletions(root):
    """Cover only exact historical design removals, never runtime code/active UI."""
    store = archive(root)
    if store is None:
        return set()
    deleted = set()
    for path in store.records:
        if not (Path(root) / path).exists():
            store.file(path)  # Verify every removed source, not just a filename exemption.
            deleted.add(path)
    return deleted
