"""Fail-closed visual evidence gate; semantic acceptance is an explicit reviewer attestation."""
import hashlib
from pathlib import Path

MATRIX = {(language, scale) for language in ('az', 'en', 'ru') for scale in (1.0, 1.5)}


def protected(path):
    return (path.startswith(('android/app/src/main/', 'android/app/src/test/java/com/aladdin/dailyplate/ui/',
                             'design-concepts/')) or path in (
        'android/app/build.gradle.kts', 'android/build.gradle.kts', 'android/gradle.properties'))


def digest(data):
    return hashlib.sha256(data).hexdigest()


def safe_file(root, relative):
    path = (root / relative).resolve()
    if not path.is_relative_to(root.resolve()) or not path.is_file():
        raise ValueError(f'Missing or unsafe evidence path: {relative}')
    return path


def validate_plan(plan, changed, artifacts, references):
    """Every changed input must be mapped, every surface needs six fresh variants."""
    surfaces = plan.get('surfaces', [])
    if not surfaces:
        raise ValueError('Visual changes require docs/qa/visual-review-plan.json surfaces')
    covered, cases, ids = set(), [], set()
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
            reference = safe_file(references, case['reference'])
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
                digest(safe_file(references, case['reference']).read_bytes()) != case['reference_sha256']):
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
