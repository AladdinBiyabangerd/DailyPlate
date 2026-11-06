"""Fail-closed visual evidence gate; semantic acceptance is an explicit reviewer attestation."""
import hashlib
from pathlib import Path

MATRIX = {(language, scale) for language in ('az', 'en', 'ru') for scale in (1.0, 1.5)}


def protected(path):
    return (path.startswith(('android/app/src/main/', 'android/app/src/test/java/com/aladdin/dailyplate/ui/',
                             'design-concepts/')) or path in (
        'android/app/build.gradle.kts', 'android/build.gradle.kts', 'android/gradle.properties',
        'docs/qa/design-archive.json'))


def digest(data):
    return hashlib.sha256(data).hexdigest()


def safe_file(root, relative):
    path = (root / relative).resolve()
    if not path.is_relative_to(root.resolve()) or not path.is_file():
        raise ValueError(f'Missing or unsafe evidence path: {relative}')
    return path



def historical_api(root):
    if not (root / 'docs/qa/design-archive.json').is_file():
        return None
    import importlib.util
    spec = importlib.util.spec_from_file_location('historical_design_archive', root / 'android/tools/design_archive.py')
    module = _archive_modules.get(str(root.resolve()))
    if module is None:
        module = importlib.util.module_from_spec(spec)
        spec.loader.exec_module(module)
        _archive_modules[str(root.resolve())] = module
    return module


_archive_modules = {}


def reference_file(root, relative):
    module = historical_api(root)
    return module.reference_file(root, relative) if module else safe_file(root, relative)


def proposal_sources(plan, references):
    """Map inspected draft assets only; never substitute concepts for native evidence."""
    covered = set()
    native_references = {case['reference'] for surface in plan.get('surfaces', [])
                         for case in surface['cases']}
    native_sources = {source for surface in plan.get('surfaces', []) for source in surface['sources']}
    for proposal in plan.get('design_proposals', []):
        sources = proposal.get('sources', [])
        if not sources:
            continue  # Historical metadata does not grant an exemption.
        if (proposal.get('status') != 'draft-rendered-agent-inspected-not-runtime-acceptance'
                or not all(proposal.get(key) for key in
                           ('requirements', 'states', 'elements', 'references', 'comparison'))):
            raise ValueError('Draft proposal requires explicit inspection and requirements')
        if proposal['output'] not in sources:
            raise ValueError('Proposal output must be mapped')
        for source in sources:
            if (not source.startswith('design-concepts/') or source in native_references
                    or source in native_sources or source.endswith(('.css', '.kt', '.xml'))):
                raise ValueError('Proposal cannot cover runtime inputs, tokens or native references')
            content = reference_file(references, source).read_bytes()
            if proposal.get('source_hashes', {}).get(source) != digest(content):
                raise ValueError('Proposal source hash missing or stale')
        content = reference_file(references, proposal['output']).read_bytes()
        if not content.startswith(b'\x89PNG\r\n\x1a\n') or digest(content) != proposal.get('sha256'):
            raise ValueError('Proposal must identify inspected PNG')
        for reference in proposal['references']:
            reference_file(references, reference)
        covered.update(sources)
    return covered


def validate_plan(plan, changed, artifacts, references):
    """Every changed input must be mapped, every surface needs six fresh variants."""
    surfaces = plan.get('surfaces', [])
    if not surfaces:
        raise ValueError('Visual changes require docs/qa/visual-review-plan.json surfaces')
    covered, cases, ids = proposal_sources(plan, references), [], set()
    module = historical_api(references)
    if module:
        covered.update(module.archived_deletions(references))
    for surface in surfaces:
        if not surface.get('requirements') or not surface.get('states'):
            raise ValueError('Each surface needs concrete requirements and required states')
        covered.update(surface['sources'])
        variants = set()
        states = set()
        for case in surface['cases']:
            if case['id'] in ids:
                raise ValueError('Duplicate visual case id')
            ids.add(case['id'])
            variants.add((case['state'], case['language'], case['font_scale']))
            states.add(case['state'])
            path = safe_file(artifacts, case['image'])
            content = path.read_bytes()
            if not content.startswith(b'\x89PNG\r\n\x1a\n'):
                raise ValueError(f'Not a rendered PNG: {path}')
            reference = reference_file(references, case['reference'])
            if not case['reference'].startswith('design-concepts/'):
                raise ValueError('Reference must be an agreed design-concepts asset/guide')
            cases.append({**case, 'sha256': digest(content),
                          'reference_sha256': digest(reference.read_bytes()),
                          'requirements': surface['requirements']})
        required = {(state, language, scale) for state in surface['states'] for language, scale in MATRIX}
        if not required.issubset(variants) or not set(surface['states']).issubset(states):
            raise ValueError('Each declared state requires AZ/EN/RU at 100%/150%')
    if set(changed) - covered:
        raise ValueError(f'Unmapped changed visual inputs: {sorted(set(changed) - covered)}')
    if len({case['image'] for case in cases}) != len(cases):
        raise ValueError('Each case must have a distinct fresh render')
    return cases


def validate_review(review, manifest, artifacts, references):
    if review.get('head') != manifest['head'] or review.get('source_digest') != manifest['source_digest']:
        raise ValueError('Review belongs to a different commit/source')
    if not review.get('reviewer', '').strip() or review.get('decision') != 'accepted':
        raise ValueError('Named reviewer and explicit accepted decision required')
    reviews = review.get('cases', {})
    if set(reviews) != {case['id'] for case in manifest['cases']}:
        raise ValueError('Every render needs an individual review')
    for case in manifest['cases']:
        record = reviews[case['id']]
        if (record.get('sha256') != case['sha256'] or
                digest(safe_file(artifacts, case['image']).read_bytes()) != case['sha256'] or
                digest(reference_file(references, case['reference']).read_bytes()) != case['reference_sha256']):
            raise ValueError('Render/reference changed or wrong image hash')
        if record.get('reference_sha256') != case['reference_sha256']:
            raise ValueError('Review must identify exact design reference')
        if record.get('viewed') is not True or not record.get('comparison', '').strip():
            raise ValueError('Actually view each image and describe comparison to requirements')
        if record.get('unresolved_differences') != []:
            raise ValueError('Unresolved design differences block acceptance')
        for adaptation in record.get('adaptations', []):
            if not adaptation.get('reason') or not adaptation.get('authorization'):
                raise ValueError('Adaptations require reason and existing authorization reference')
    return True
