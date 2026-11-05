"""Negative-path tests for visual acceptance (run only by post-merge CI)."""
import copy
import tempfile
import unittest
from pathlib import Path
from visual_gate import MATRIX, protected, validate_plan, validate_review


class VisualGateTest(unittest.TestCase):
    def test_scope_includes_new_resources_domain_and_deleted_ui(self):
        for name in ('android/app/src/main/java/new.kt', 'android/app/src/main/res/values/strings.xml',
                     'android/app/src/test/java/com/aladdin/dailyplate/ui/NewTest.kt', 'design-concepts/tokens.css'):
            self.assertTrue(protected(name))
        self.assertFalse(protected('docs/context/current-task.md'))

    def test_missing_stale_coverage_and_unreviewed_evidence_are_rejected(self):
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp)
            (root / 'design-concepts').mkdir()
            (root / 'design-concepts/reference.png').write_bytes(b'reference')
            cases = []
            for index, (language, scale) in enumerate(sorted(MATRIX)):
                name = f'{index}.png'
                (root / name).write_bytes(b'\x89PNG\r\n\x1a\n' + bytes([index]))
                cases.append(dict(id=name, image=name, language=language, font_scale=scale,
                                  state='normal', reference='design-concepts/reference.png'))
            plan = dict(surfaces=[dict(sources=['ui.kt'], requirements=['illustrated cards'],
                                      states=['normal'], cases=cases)])
            manifest = dict(head='head', source_digest='source', cases=validate_plan(plan, ['ui.kt'], root, root))
            review = dict(head='head', source_digest='source', reviewer='test', decision='accepted',
                          cases={c['id']: dict(sha256=c['sha256'], reference_sha256=c['reference_sha256'],
                                              viewed=True, comparison='Compared illustrated cards',
                                              unresolved_differences=[]) for c in manifest['cases']})
            self.assertTrue(validate_review(review, manifest, root, root))
            for changed_plan, changed in (({}, ['ui.kt']), (plan, ['new.kt'])):
                with self.assertRaises(ValueError): validate_plan(changed_plan, changed, root, root)
            incomplete = copy.deepcopy(plan)
            incomplete['surfaces'][0]['cases'].pop()
            with self.assertRaises(ValueError): validate_plan(incomplete, ['ui.kt'], root, root)
            state_missing = copy.deepcopy(plan)
            state_missing['surfaces'][0]['states'].append('error')
            with self.assertRaises(ValueError): validate_plan(state_missing, ['ui.kt'], root, root)
            missing = copy.deepcopy(plan)
            missing['surfaces'][0]['cases'][0]['image'] = 'missing.png'
            with self.assertRaises(ValueError): validate_plan(missing, ['ui.kt'], root, root)
            unsafe = copy.deepcopy(plan)
            unsafe['surfaces'][0]['cases'][0]['image'] = '../outside.png'
            with self.assertRaises(ValueError): validate_plan(unsafe, ['ui.kt'], root, root)
            for key, value in (('head', 'old'), ('cases', {}), ('decision', 'pending')):
                invalid = {**review, key: value}
                with self.assertRaises(ValueError): validate_review(invalid, manifest, root, root)
            invalid = copy.deepcopy(review)
            invalid['cases'][cases[0]['id']]['unresolved_differences'] = ['missing hero']
            with self.assertRaises(ValueError): validate_review(invalid, manifest, root, root)
            (root / cases[0]['image']).write_bytes(b'changed')
            with self.assertRaises(ValueError): validate_review(review, manifest, root, root)


if __name__ == '__main__':
    unittest.main()
