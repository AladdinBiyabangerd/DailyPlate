"""Exercise local gates in a disposable repository, never the project history."""
from pathlib import Path
import shutil
import os
import subprocess
import sys
import tempfile

SOURCE = Path(os.environ.get('CALLMOOD_WORKFLOW_TOOLS', Path(__file__).resolve().parents[2] / '.local-ci'))


with tempfile.TemporaryDirectory(prefix='callmood-workflow-test-') as temp:
    root = Path(temp)
    shutil.copytree(SOURCE, root / '.local-ci', ignore=shutil.ignore_patterns('logs', '__pycache__', 'reports', 'visual', '*screenshots', 'visual-budget'))
    (root / 'android/tools').mkdir(parents=True)
    shutil.copy2(SOURCE.parent / 'android/tools/visual_gate.py', root / 'android/tools/visual_gate.py')
    (root / 'docs/qa').mkdir(parents=True)
    # A deterministic failing check tests orchestration independently of nutrition data.
    (root / '.local-ci/checks.py').write_text(
        'from pathlib import Path\nimport sys\n'
        'sys.exit(1 if (Path(sys.argv[1])/"sample.txt").read_text()=="bad" else 0)\n')

    def run(*args, ok=True):
        result = subprocess.run(args, cwd=root, capture_output=True, text=True, errors='replace')
        assert (result.returncode == 0) == ok, result.stdout + result.stderr
        return result.stdout.strip()

    def workflow(action, *args, ok=True):
        return run(sys.executable, '.local-ci/workflow.py', action, *args, ok=ok)

    run('git', 'init', '-b', 'main')
    run('git', 'config', 'user.name', 'Local CI Test')
    run('git', 'config', 'user.email', 'local-ci@example.invalid')
    (root / '.git/info/exclude').write_text('/.local-ci/\n__pycache__/\n*.pyc\n')
    (root / 'sample.txt').write_text('good')
    run('git', 'add', 'sample.txt')
    run('git', 'commit', '-m', 'baseline')
    anchor = run('git', 'rev-parse', 'HEAD')
    (root / 'docs/qa/visual-policy.json').write_text(__import__('json').dumps(dict(legacy_anchor=anchor, debt='legacy-debt')))
    (root / 'docs/qa/visual-review-plan.json').write_text('{"surfaces": []}')
    run('git', 'add', 'android', 'docs')
    run('git', 'commit', '-m', 'gate fixtures')
    run('git', 'config', 'core.hooksPath', '.local-ci/hooks')
    run('git', 'commit', '--allow-empty', '-m', 'must block main', ok=False)
    workflow('ci', ok=False)
    assert not (root / '.local-ci/logs/latest.json').exists()
    workflow('start', 'feat/workflow-test')
    (root / 'sample.txt').write_text('bad')
    workflow('ci', ok=False)  # Dirty work is refused.
    run('git', 'add', 'sample.txt')
    run('git', 'commit', '-m', 'introduce failing fixture')
    workflow('ci', ok=False)
    assert not (root / '.local-ci/logs/latest.json').exists()
    workflow('finish', ok=False)
    assert run('git', 'branch', '--show-current') == 'main'
    assert run('git', 'show', 'main:sample.txt') == 'bad'
    import json
    report = json.loads((root / '.local-ci/logs/latest.json').read_text())
    assert not report['passed'] and report['branch'] == 'main'
    workflow('ci', ok=False)
    workflow('start', 'fix/workflow-test')
    (root / 'sample.txt').write_text('fixed')
    run('git', 'add', 'sample.txt')
    run('git', 'commit', '-m', 'repair fixture')
    workflow('finish')
    assert run('git', 'branch', '--show-current') == 'main'
    assert run('git', 'show', 'HEAD:sample.txt') == 'fixed'
    assert len(run('git', 'rev-list', '--parents', '-n', '1', 'HEAD').split()) == 3
    import json
    report = json.loads((root / '.local-ci/logs/latest.json').read_text())
    assert report['passed'] and report['branch'] == 'main'
    assert report['head'] == run('git', 'rev-parse', 'HEAD')
    print('PASS: main/manual/branch CI blocked; CI failure detected after merge; '
          'fix/commit/retry succeeds; merge commit and post-merge CI verified.')

    workflow('start', 'feat/visual-missing')
    (root / 'android/app/src/main').mkdir(parents=True)
    (root / 'android/app/src/main/screen.kt').write_text('changed UI')
    run('git', 'add', 'android')
    run('git', 'commit', '-m', 'visual change without evidence')
    workflow('finish', ok=False)
    report = json.loads((root / '.local-ci/logs/latest.json').read_text())
    assert report['checks_passed'] and not report['passed'] and report['visual_status'] == 'pending'
    workflow('review-visual', '/missing/review.json', ok=False)
    workflow('start', 'docs/cannot-bypass')
    (root / 'note.md').write_text('unrelated docs')
    run('git', 'add', 'note.md')
    run('git', 'commit', '-m', 'nonvisual merge cannot bypass pending review')
    workflow('finish', ok=False)
    manifest = json.loads((root / '.local-ci/visual' / run('git', 'rev-parse', 'HEAD') / 'manifest.json').read_text())
    assert 'android/app/src/main/screen.kt' in manifest['changed']
    workflow('start', 'fix/render-coverage')
    (root / 'design-concepts').mkdir()
    (root / 'design-concepts/ref.txt').write_text('agreed reference')
    cases = []
    (root / 'fixtures').mkdir()
    for index, (language, scale) in enumerate(( (lang, scale) for lang in ('az','en','ru') for scale in (1.0,1.5) )):
        name = f'{index}.png'
        (root / 'fixtures' / name).write_bytes(b'\x89PNG\r\n\x1a\n\x00' + bytes([index]))
        cases.append(dict(id=name, image=name, language=language, font_scale=scale, state='normal', reference='design-concepts/ref.txt'))
    plan = dict(surfaces=[dict(sources=['android/app/src/main/screen.kt', 'design-concepts/ref.txt'],
        requirements=['test fixture layout'], states=['normal'], cases=cases)])
    (root / 'docs/qa/visual-review-plan.json').write_text(json.dumps(plan))
    (root / '.local-ci/checks.py').write_text(
        'import os, shutil, sys\nfrom pathlib import Path\n'
        'shutil.copytree(Path(sys.argv[1])/"fixtures", os.environ["CALLMOOD_VISUAL_ARTIFACTS"], dirs_exist_ok=True)\n')
    run('git', 'add', 'docs', 'fixtures', 'design-concepts')
    run('git', 'commit', '-m', 'provide render coverage fixtures')
    workflow('finish', ok=False)
    head = run('git', 'rev-parse', 'HEAD')
    manifest = json.loads((root / '.local-ci/visual' / head / 'manifest.json').read_text())
    assert not manifest.get('error'), manifest
    review = dict(head=head, source_digest=manifest['source_digest'], reviewer='fixture reviewer', decision='accepted',
        cases={c['id']: dict(sha256=c['sha256'], reference_sha256=c['reference_sha256'], viewed=True,
            comparison='test-only fixture comparison', unresolved_differences=[]) for c in manifest['cases']})
    review_path = root / '.local-ci/fixture-review.json'
    review_path.write_text(json.dumps({**review, 'head':'stale'}))
    workflow('review-visual', str(review_path), ok=False)
    review_path.write_text(json.dumps(review))
    report_path = root / '.local-ci/logs/latest.json'
    report = json.loads(report_path.read_text())
    report_path.write_text(json.dumps({**report, 'checks_passed':False}))
    workflow('review-visual', str(review_path), ok=False)
    report_path.write_text(json.dumps(report))
    workflow('review-visual', str(review_path))
    workflow('finish')
    assert json.loads(report_path.read_text())['passed']
    print('PASS: missing coverage/review, stale review, failed technical promotion and later nonvisual bypass blocked; explicit review finalizes without CI rerun.')

    workflow('start', 'docs/matching-render')
    (root / 'note.md').write_text('nonvisual update after explicit acceptance')
    run('git', 'add', 'note.md')
    run('git', 'commit', '-m', 'unchanged accepted render hashes match')
    workflow('finish')
    assert json.loads(report_path.read_text())['visual_status'] == 'matched'
    workflow('start', 'fix/render-drift')
    (root / 'fixtures/0.png').write_bytes(b'\x89PNG\r\n\x1a\n\x00changed')
    run('git', 'add', 'fixtures')
    run('git', 'commit', '-m', 'simulate render drift without source change')
    workflow('finish', ok=False)
    report = json.loads(report_path.read_text())
    assert report['checks_passed'] and report['visual_status'] == 'pending' and not report['passed']
    manifest = json.loads(Path(report['visual_manifest']).read_text())
    assert any(c['image_changed'] for c in manifest['cases'])
    workflow('start', 'fix/no-dropping-baseline')
    (root / 'docs/qa/visual-review-plan.json').write_text('{"surfaces": []}')
    run('git', 'add', 'docs')
    run('git', 'commit', '-m', 'cannot silently drop accepted cases')
    workflow('finish', ok=False)
    print('PASS: accepted images match automatically only when unchanged; drift and removed coverage block completion.')
