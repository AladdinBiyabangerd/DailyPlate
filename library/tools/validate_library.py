"""Validate provenance, units, missing-data propagation and safe release state."""
import csv, hashlib, io, json, sqlite3, unittest, zipfile
from decimal import Decimal
from pathlib import Path

ROOT=Path(__file__).resolve().parents[1]
FOODS=json.loads((ROOT/'foods.json').read_text());RECIPES=json.loads((ROOT/'recipes.json').read_text())
BY_ID={f['id']:f for f in FOODS}
MANIFEST=json.loads((ROOT/'manifest.json').read_text())

class LibraryContract(unittest.TestCase):
    def test_exact_counts_and_unique_source_mappings(self):
        self.assertEqual(len(FOODS),500);self.assertEqual(len(RECIPES),224)
        self.assertEqual(len({f['fdc_id'] for f in FOODS}),500)
        self.assertEqual(len({r['id'] for r in RECIPES}),224)

    def test_archives_have_recorded_integrity(self):
        for source in MANIFEST['source_files']:
            p=ROOT/'sources'/source['file']
            self.assertEqual(hashlib.sha256(p.read_bytes()).hexdigest(),source['sha256'])

    def test_source_values_match_original_archive(self):
        z=zipfile.ZipFile(ROOT/'sources/usda-sr-legacy-2018-04.zip')
        n=next(n for n in z.namelist() if n.endswith('/food_nutrient.csv'))
        by_fdc={f['fdc_id']:f for f in FOODS}
        checked=0
        for r in csv.DictReader(io.TextIOWrapper(z.open(n),encoding='utf-8-sig')):
            f=by_fdc.get(int(r['fdc_id']))
            if f:
                for key,ref in f['nutrient_lineage'].items():
                    if ref['nutrient_id']==int(r['nutrient_id']):
                        self.assertEqual(Decimal(str(f['nutrients_per_100g'][key])),Decimal(r['amount']))
                        checked+=1
        self.assertGreater(checked,2500)

    def test_raw_and_cooked_rice_are_not_interchangeable(self):
        self.assertEqual(BY_ID['rice']['nutrients_per_100g']['energy_kcal'],365)
        self.assertEqual(BY_ID['rice_cooked']['nutrients_per_100g']['energy_kcal'],130)
        self.assertNotEqual(BY_ID['rice']['fdc_id'],BY_ID['rice_cooked']['fdc_id'])

    def test_all_ingredients_resolve_and_have_edible_mass(self):
        for r in RECIPES:
            self.assertGreater(r['declared_servings'],0)
            self.assertGreaterEqual(len(r['steps']['az']),3)
            for i in r['ingredients']:
                self.assertIn(i['food_id'],BY_ID)
                self.assertGreater(i['edible_grams'],0)
                self.assertEqual(i['fdc_id'],BY_ID[i['food_id']]['fdc_id'])
            self.assertEqual(sum(i['edible_grams'] for i in r['ingredients']),r['raw_input_total_g'])

    def test_energy_arithmetic_and_servings(self):
        for r in RECIPES:
            terms=[Decimal(str(BY_ID[i['food_id']]['nutrients_per_100g']['energy_kcal']))*Decimal(str(i['edible_grams']))/100 for i in r['ingredients']]
            expected=float(sum(terms));calc=r['nutrient_estimate']
            self.assertAlmostEqual(expected,calc['total_recipe']['energy_kcal'],places=9)
            self.assertAlmostEqual(expected/r['declared_servings'],calc['per_declared_serving']['energy_kcal'],places=9)
        # Hand-check: 300g apple at 52 kcal/100g + 30g walnut at 654 kcal/100g, divided by 2.
        snack=next(r for r in RECIPES if r['id']=='N01')
        self.assertAlmostEqual(snack['nutrient_estimate']['per_declared_serving']['energy_kcal'],176.1)

    def test_unknown_is_not_zero_and_sugars_not_conflated(self):
        self.assertIsNone(BY_ID['rice']['nutrients_per_100g']['trans_fat_g'])
        for r in RECIPES:
            for key in ['free_sugars_g','added_sugars_g']:
                self.assertIsNone(r['nutrient_estimate']['total_recipe'][key])
            for key,missing in r['nutrient_estimate']['missing_ingredient_data'].items():
                self.assertTrue(missing)
                self.assertIsNone(r['nutrient_estimate']['total_recipe'][key])

    def test_release_is_not_falsely_approved(self):
        for f in FOODS:
            self.assertFalse(f['publishable']);self.assertIsNone(f['reviewed_by'])
        for r in RECIPES:
            self.assertFalse(r['publishable']);self.assertFalse(r['dietitian_reviewed'])
            self.assertFalse(r['eligible_for_live_personalized_planning'])
            self.assertIsNone(r['measured_final_yield_g'])
            self.assertIsNone(r['nutrient_estimate']['per_100g_cooked'])
            self.assertFalse(r['portion_photos'])
            self.assertIsNone(r['price_estimate'])

    def test_allergen_propagation_and_no_raw_kidney_bean_recipe(self):
        for r in RECIPES:
            inferred={a for i in r['ingredients'] for a in BY_ID[i['food_id']]['allergens_inferred_present']}
            self.assertEqual(inferred,set(r['allergens_inferred_present']))
            self.assertNotIn('red_beans',{i['food_id'] for i in r['ingredients']})
        self.assertIn('gluten_oats',BY_ID['oats']['allergens_inferred_present'])

    def test_sqlite_foreign_keys_integrity_and_foundation_import(self):
        with sqlite3.connect(ROOT/'nutrition-library.sqlite') as db:
            self.assertEqual(db.execute('PRAGMA integrity_check').fetchone()[0],'ok')
            self.assertFalse(db.execute('PRAGMA foreign_key_check').fetchall())
            self.assertEqual(db.execute('SELECT COUNT(*) FROM source_foods').fetchone()[0],8217)
            self.assertEqual(db.execute("SELECT COUNT(*) FROM source_foods WHERE data_type='Foundation Foods'").fetchone()[0],424)

    def test_weekly_references_and_non_prescriptive_status(self):
        weeks=json.loads((ROOT/'weekly-ideas.json').read_text());rids={r['id'] for r in RECIPES}
        self.assertEqual(len(weeks),8)
        for w in weeks:
            self.assertFalse(w['publishable']);self.assertFalse(w['personalized'])
            self.assertIsNone(w['energy_target_kcal']);self.assertEqual(len(w['days']),7)
            for day in w['days']:
                for k in ['breakfast','lunch','dinner','optional_snack']:self.assertIn(day[k],rids)

    def test_no_duplicate_recipe_formulas(self):
        sigs={tuple(sorted((i['food_id'],i['edible_grams']) for i in r['ingredients'])) for r in RECIPES}
        self.assertEqual(len(sigs),224)

    def test_new_raw_animal_ingredients_have_safety_rules(self):
        wanted={'duck':'raw_poultry','chicken_heart':'raw_poultry','chicken_gizzard':'raw_poultry',
                'beef_stew':'whole_cut_beef_lamb_veal','lamb_cubes':'whole_cut_beef_lamb_veal',
                'carp':'raw_fish','haddock':'raw_fish','halibut':'raw_fish','scallop':'raw_scallop','mussel':'raw_mussel'}
        for r in RECIPES:
            keys={i['food_id'] for i in r['ingredients']};rules={x['rule'] for x in r['safety']['rules']}
            for k,rule in wanted.items():
                if k in keys:self.assertIn(rule,rules)

    def test_declared_serving_estimate_has_conditional_yield_gate(self):
        for r in RECIPES:
            self.assertNotIn('kitchen_yield_measurement',r['release_blockers'])
            self.assertEqual(r['feature_gates']['per_100g_cooked'],'documented_measured_or_estimated_cooked_yield_required')
            self.assertEqual(r['nutrient_estimate']['display_scope'],'estimated_per_declared_recipe_serving')

    def test_world_discovery_is_not_price_or_clinical_approval(self):
        world=[r for r in RECIPES if r['id'].startswith('G')]
        self.assertEqual(len(world),24)
        from collections import Counter
        self.assertEqual(Counter(r['discovery']['budget_tier_candidate'] for r in world),
                         {'low':14,'medium':6,'high':4})
        plan=json.loads((ROOT.parent/'planning/research-data.json').read_text())
        sources={s['id'] for s in plan['sources']}
        for r in world:
            info=r['discovery']
            self.assertTrue(set(info['evidence_refs']).issubset(sources))
            self.assertEqual(info['budget_status'],'editorial_hypothesis_requires_region_price_check')
            self.assertEqual(info['goal_matching'],'evaluate_portion_and_complete_day_under_approved_policy')
            self.assertIsNone(r['price_estimate'])
            self.assertFalse(r['eligible_for_live_personalized_planning'])
        self.assertTrue(all(r['discovery'] is None for r in RECIPES if not r['id'].startswith('G')))

if __name__=='__main__':
    suite=unittest.defaultTestLoader.loadTestsFromTestCase(LibraryContract)
    result=unittest.TextTestRunner(verbosity=2).run(suite)
    report={'version':MANIFEST['version'],'tested_at':'2026-11-04',
        'test_command':'python3 library/tools/validate_library.py',
        'result':'passed' if result.wasSuccessful() else 'failed','tests_run':result.testsRun,
        'scope':'source_provenance_data_contracts_and_calculation_not_clinical_validation',
        'food_count':len(FOODS),'recipe_count':len(RECIPES),
        'weekly_ideas':MANIFEST['weekly_idea_count']}
    (ROOT/'validation-report.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n')
    raise SystemExit(0 if result.wasSuccessful() else 1)

