"""Historical resolver regressions; disposable repositories, no network or real credentials."""
import hashlib
import io
import json
import os
from pathlib import Path
import subprocess
import tempfile
import unittest
from unittest.mock import patch
import zipfile
from design_archive import reference_file, archived_deletions


class ArchiveTests(unittest.TestCase):
    def fixture(self, zipped=False, corrupt_hash=False, unsafe=False):
        temp = tempfile.TemporaryDirectory(prefix='design-archive-test-')
        self.addCleanup(temp.cleanup)
        root = Path(temp.name).resolve()
        def git(*args):
            return subprocess.check_output(['git', '-c', 'user.name=Archive Fixture',
                '-c', 'user.email=archive-fixture@example.invalid', *args], cwd=root,
                stderr=subprocess.PIPE).decode().strip()
        git('init', '-b', 'archive-fixture')
        data = b'\x89PNG\r\n\x1a\nexact historical fixture\n'
        logical = 'design-concepts/legacy/old.png'
        destination = 'current/' + logical + ('.zip' if zipped else '')
        file = root / destination
        file.parent.mkdir(parents=True)
        payload = data
        if zipped:
            stream = io.BytesIO()
            with zipfile.ZipFile(stream, 'w') as archive:
                archive.writestr('old.png', data)
            payload = stream.getvalue()
        file.write_bytes(payload)
        record = dict(original_path=logical, archive_path=destination,
                      size=len(data), sha256='0'*64 if corrupt_hash else hashlib.sha256(data).hexdigest(),
                      git_blob=hashlib.sha1(b'blob ' + str(len(data)).encode() + b'\0' + data).hexdigest())
        if zipped:
            record['archive_member'] = 'old.png'
        if unsafe:
            record['original_path'] = '../escape.png'
        raw = json.dumps(dict(records=[record], current_entries=1)).encode()
        (root / 'MANIFEST.json').write_bytes(raw)
        git('add', 'current', 'MANIFEST.json')
        git('commit', '-m', 'design-only fixture')
        policy = dict(archive_commit=git('rev-parse', 'HEAD'), manifest_sha256=hashlib.sha256(raw).hexdigest())
        p = root / 'docs/qa/design-archive.json'
        p.parent.mkdir(parents=True)
        p.write_text(json.dumps(policy))
        return root, logical, data, p

    def test_exact_source_in_temp_and_deleted_mapping(self):
        root, name, data, _ = self.fixture()
        result = reference_file(root, name)
        self.assertEqual(result.read_bytes(), data)
        self.assertFalse(result.is_relative_to(root))
        self.assertFalse((root / name).exists())
        self.assertEqual(archived_deletions(root), {name})

    def test_current_active_is_never_replaced_and_missing_fails(self):
        root, _, _, _ = self.fixture()
        name = 'design-concepts/simple-turquoise/onboarding-original-style/current.png'
        file = root / name
        file.parent.mkdir(parents=True)
        file.write_bytes(b'current active source')
        self.assertEqual(reference_file(root, name), file)
        file.unlink()
        with self.assertRaises(ValueError):
            reference_file(root, name)

    def test_path_traversal_and_external_symlink_refused(self):
        root, _, _, _ = self.fixture()
        for name in ('../escape', '/tmp/escape', 'design-concepts/../escape'):
            with self.assertRaises(ValueError):
                reference_file(root, name)
        (root / 'outside-link').symlink_to('/tmp')
        with self.assertRaises(ValueError):
            reference_file(root, 'outside-link/x')

    def test_manifest_tamper_and_missing_object_fail_closed(self):
        root, name, _, p = self.fixture()
        policy = json.loads(p.read_text())
        original = dict(policy)
        policy['manifest_sha256'] = '0'*64
        p.write_text(json.dumps(policy))
        with self.assertRaises(ValueError):
            reference_file(root, name)
        original['archive_commit'] = '0'*40
        p.write_text(json.dumps(original))
        with self.assertRaises(ValueError):
            reference_file(root, name)

    def test_blob_and_cached_bytes_tamper_refused(self):
        root, name, _, _ = self.fixture(corrupt_hash=True)
        with self.assertRaises(ValueError):
            reference_file(root, name)
        root, name, _, _ = self.fixture()
        result = reference_file(root, name)
        result.write_bytes(b'tampered cache')
        with self.assertRaises(ValueError):
            reference_file(root, name)

    def test_unmapped_missing_and_unsafe_record_refused(self):
        root, _, _, _ = self.fixture()
        with self.assertRaises(ValueError):
            reference_file(root, 'design-concepts/not-archived.png')
        root, name, _, _ = self.fixture(unsafe=True)
        with self.assertRaises(ValueError):
            reference_file(root, name)

    def test_zip_member_exact_bytes(self):
        root, name, data, _ = self.fixture(zipped=True)
        self.assertEqual(reference_file(root, name).read_bytes(), data)

    def test_ci_snapshot_uses_only_explicit_local_object_repo(self):
        root, name, data, policy = self.fixture()
        temp = tempfile.TemporaryDirectory(prefix='archive-ci-snapshot-')
        self.addCleanup(temp.cleanup)
        snapshot = Path(temp.name)
        dest = snapshot / 'docs/qa/design-archive.json'
        dest.parent.mkdir(parents=True)
        dest.write_bytes(policy.read_bytes())
        with patch.dict(os.environ, {'CALLMOOD_DESIGN_OBJECT_REPO': str(root)}):
            self.assertEqual(reference_file(snapshot, name).read_bytes(), data)
        self.assertFalse((snapshot / name).exists())


if __name__ == '__main__':
    unittest.main()
