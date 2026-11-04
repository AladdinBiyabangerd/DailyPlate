"""Rebuild the source-linked food/recipe library without network or API keys."""
from __future__ import annotations
import csv, hashlib, io, json, runpy, sqlite3, zipfile
from collections import Counter
from datetime import date
from decimal import Decimal
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
DATE = '2026-11-04'
VERSION = '0.3.0'
EXPECTED_FOOD_COUNT = 500
EXPECTED_RECIPE_COUNT = 224
SOURCE_URLS = {
    'usda-sr-legacy-2018-04.zip': 'https://fdc.nal.usda.gov/fdc-datasets/FoodData_Central_sr_legacy_food_csv_2018-04.zip',
    'usda-foundation-2026-10.zip': 'https://fdc.nal.usda.gov/fdc-datasets/FoodData_Central_foundation_food_json_2026-10-29.zip',
    'usda-supporting-2022-10.zip': 'https://fdc.nal.usda.gov/fdc-datasets/FoodData_Central_Supporting_Data_csv_2022-10-28.zip',
}
DEFS = {
    'energy_kcal': ([1008, 2048, 2047], 'KCAL'),
    'protein_g': ([1003], 'G'), 'fat_g': ([1004], 'G'),
    'carbohydrate_g': ([1005], 'G'), 'fiber_g': ([1079], 'G'),
    'sugars_total_g': ([2000, 1063], 'G'), 'sodium_mg': ([1093], 'MG'),
    'saturated_fat_g': ([1258], 'G'), 'trans_fat_g': ([1257], 'G'),
    'calcium_mg': ([1087], 'MG'), 'iron_mg': ([1089], 'MG'),
    'potassium_mg': ([1092], 'MG'), 'magnesium_mg': ([1090], 'MG'),
    'vitamin_c_mg': ([1162], 'MG'),
}
WANTED = {i for ids, _ in DEFS.values() for i in ids}

def write_json(path, value):
    path.write_text(json.dumps(value, ensure_ascii=False, indent=2)+'\n')

def csv_from(z, name):
    member = next(n for n in z.namelist() if n == name or n.endswith('/'+name))
    yield from csv.DictReader(io.TextIOWrapper(z.open(member), encoding='utf-8-sig'))

def normalized(nutrients):
    result, lineage = {}, {}
    for key, (ids, unit) in DEFS.items():
        selected = next((i for i in ids if i in nutrients and nutrients[i]['amount'] is not None), None)
        result[key] = None
        if selected is not None:
            n = nutrients[selected]
            if n['unit'].upper() != unit:
                raise ValueError(f'Unit mismatch: {key} {n}')
            result[key] = n['amount']
            lineage[key] = {'nutrient_id': selected, 'unit': unit, 'method': n.get('name')}
    # Total sugars are not free or added sugars. Unknown stays unknown.
    result.update(added_sugars_g=None, free_sugars_g=None)
    return result, lineage

def source_food(fid, desc, dtype, release, nutrients, portions):
    values, lineage = normalized(nutrients)
    return {'fdc_id': fid, 'description_en': desc, 'data_type': dtype,
            'dataset_release': release, 'source_url': f'https://fdc.nal.usda.gov/food-details/{fid}/nutrients',
            'retrieved_at': DATE, 'basis': '100_g_edible_portion_as_described',
            'nutrients_per_100g': values, 'nutrient_lineage': lineage,
            'source_portions': portions, 'reviewed_by': None,
            'status': 'source_imported_not_locally_validated'}

def read_sources():
    z = zipfile.ZipFile(ROOT/'sources/usda-sr-legacy-2018-04.zip')
    nutrient_defs = {int(r['id']): r for r in csv_from(z, 'nutrient.csv')}
    foods = {int(r['fdc_id']): r for r in csv_from(z, 'food.csv')}
    raw_nutrients = {i: {} for i in foods}
    for r in csv_from(z, 'food_nutrient.csv'):
        nid, fid = int(r['nutrient_id']), int(r['fdc_id'])
        if nid in WANTED:
            d = nutrient_defs[nid]
            raw_nutrients[fid][nid] = {'amount': float(r['amount']) if r['amount'] else None,
                                      'unit': d['unit_name'], 'name': d['name']}
    raw_portions = {i: [] for i in foods}
    for r in csv_from(z, 'food_portion.csv'):
        raw_portions[int(r['fdc_id'])].append({
            'source_portion_id': r['id'], 'amount': float(r['amount']) if r['amount'] else None,
            'description': r['portion_description'] or r['modifier'],
            'gram_weight': float(r['gram_weight']) if r['gram_weight'] else None,
            'scope': 'USDA_reference_portion_not_our_plate_photo'})
    output = {fid: source_food(fid,r['description'],'SR Legacy','2018-04',raw_nutrients[fid],raw_portions[fid])
              for fid,r in foods.items()}
    fz = zipfile.ZipFile(ROOT/'sources/usda-foundation-2026-10.zip')
    foundation = json.loads(fz.read(fz.namelist()[0]))['FoundationFoods']
    for r in foundation:
        fid = int(r['fdcId'])
        if fid in output:
            raise ValueError(f'Unexpected duplicate FDC ID {fid}')
        ns = {}
        for n in r['foodNutrients']:
            d = n.get('nutrient', {}); nid = d.get('id')
            if nid in WANTED:
                if nid in ns: raise ValueError(f'Duplicate nutrient {fid}/{nid}')
                ns[nid] = {'amount': n.get('amount'), 'unit': d['unitName'], 'name': d['name']}
        portions = [{'source_portion_id': p.get('id'), 'amount': p.get('amount'),
                     'description': p.get('portionDescription') or p.get('modifier'),
                     'gram_weight': p.get('gramWeight'),
                     'scope': 'USDA_reference_portion_not_our_plate_photo'} for p in r.get('foodPortions', [])]
        output[fid] = source_food(fid,r['description'],'Foundation Foods','2026-10-29',ns,portions)
    return output

def preparation_state(description):
    s = description.lower()
    if 'cooked' in s and 'uncooked' not in s: return 'cooked_as_described'
    if 'canned' in s: return 'canned_as_described'
    if 'raw' in s or 'uncooked' in s: return 'raw_as_described'
    if 'dried' in s or 'dry' in s: return 'dry_as_described'
    return 'as_purchased_or_prepared_see_source'

def curated(source):
    result = []
    group = None
    lines = []
    for filename in ['curated-foods.tsv', 'curated-foods-expansion.tsv']:
        lines.extend((ROOT/filename).read_text().splitlines())
    for line in lines:
        if line.startswith('# '): group = line[2:]; continue
        if not line: continue
        key, fid, az, ru, allergens = line.split('|')
        src = source[int(fid)]
        tags = [a for a in allergens.split(',') if a]
        inferred = [a for a in tags if not a.endswith('_candidate')]
        uncertain = [a.removesuffix('_candidate') for a in tags if a.endswith('_candidate')]
        result.append({
            'id': key, 'fdc_id': int(fid), 'names': {'az': az, 'en': src['description_en'], 'ru': ru},
            'category': group, 'preparation_state': preparation_state(src['description_en']),
            'source': {k: src[k] for k in ['source_url','data_type','dataset_release','retrieved_at','basis']},
            'nutrients_per_100g': src['nutrients_per_100g'], 'nutrient_lineage': src['nutrient_lineage'],
            'source_portions': src['source_portions'],
            'allergens_inferred_present': inferred, 'allergens_possible': uncertain,
            'allergen_status': 'inferred_generic_food_requires_product_and_reviewer_check',
            'cross_contact_status': 'unknown', 'translation_status': 'editorial_draft',
            'local_equivalence_status': 'generic_reference_not_confirmed_local_product',
            'reviewed_by': None, 'approved_at': None, 'publishable': False,
            'status': 'source_linked_draft'})
    assert len(result) == EXPECTED_FOOD_COUNT
    assert len({f['id'] for f in result}) == EXPECTED_FOOD_COUNT
    assert len({f['fdc_id'] for f in result}) == EXPECTED_FOOD_COUNT
    return result

def sum_nutrients(ingredients, foods):
    totals, missing = {}, {}
    for nutrient in next(iter(foods.values()))['nutrients_per_100g']:
        absent = [k for k in ingredients if foods[k]['nutrients_per_100g'][nutrient] is None]
        missing[nutrient] = absent
        totals[nutrient] = None if absent else float(sum(
            Decimal(str(foods[k]['nutrients_per_100g'][nutrient]))*Decimal(str(g))/Decimal('100')
            for k,g in ingredients.items()))
    return totals, missing

SAFETY_SOURCE = 'https://www.foodsafety.gov/food-safety-charts/safe-minimum-internal-temperatures'
STORAGE_SOURCE = 'https://www.gov.uk/government/publications/home-food-fact-checker/home-food-fact-checker'
def safety(ingredients):
    keys = set(ingredients)
    requirements=[]
    if keys & {'chicken','chicken_thigh','chicken_drumstick','turkey','turkey_mince','chicken_liver','chicken_heart','chicken_gizzard','turkey_whole_meat','duck'}:
        requirements.append({'rule':'raw_poultry','minimum_core_c':74,'use_food_thermometer':True})
    if keys & {'beef','beef_10','lamb'}:
        requirements.append({'rule':'ground_beef_or_lamb','minimum_core_c':71,'use_food_thermometer':True})
    if keys & {'beef_stew','beef_round','beef_chuck_lean','beef_ribs_lean','lamb_cubes','lamb_leg','lamb_shoulder','veal'}:
        requirements.append({'rule':'whole_cut_beef_lamb_veal','minimum_core_c':63,'rest_minutes':3,'use_food_thermometer':True,'note_az':'Mənbə minimumudur; tikə yeməyin yumşalması üçün bişmə müddəti ayrıca reseptə görə yoxlanır.'})
    if keys & {'salmon','wild_salmon','trout','cod','mackerel','herring','tilapia','anchovy','carp','catfish','haddock','halibut','perch','pike','pollock','mullet','sea_bass','sturgeon','whiting','smelt'}:
        requirements.append({'rule':'raw_fish','minimum_core_c':63,'use_food_thermometer':True})
    if 'shrimp' in keys:
        requirements.append({'rule':'raw_shrimp','instruction_az':'Krevetka tam qeyri-şəffaf, ağ və ya mirvari rəngli olana qədər bişir.'})
    if 'scallop' in keys:
        requirements.append({'rule':'raw_scallop','instruction_az':'Dəniz darağı ləti ağ və ya mirvari rəngli, qeyri-şəffaf olana qədər bişir.'})
    if 'mussel' in keys:
        requirements.append({'rule':'raw_mussel','instruction_az':'Qabıqlı midiyanı bişdikdə açılana qədər bişir; açılmayanları at. Mənbə qramı yeyilən lətə aiddir.'})
    if 'egg' in keys:
        requirements.append({'rule':'eggs','instruction_az':'Yumurtanın ağı və sarısı tam bərkiməlidir. Qarışıq yumurta yeməyi üçün minimum mərkəz temperaturu 71°C.'})
    return {'source':SAFETY_SOURCE,'rules':requirements,
            'raw_kidney_beans_used':False,
            'storage_source':STORAGE_SOURCE,
            'storage_instructions_status':'recipe_specific_review_required',
            'batch_storage_days':None,'reviewed_by':None}

def recipe_library(foods):
    drafts = runpy.run_path(str(ROOT/'recipe-drafts.py'))['RECIPES']
    drafts += runpy.run_path(str(ROOT/'recipe-drafts-expansion.py'))['RECIPES']
    drafts += runpy.run_path(str(ROOT/'recipe-drafts-world.py'))['RECIPES']
    by_id={f['id']:f for f in foods}; result=[]
    for d in drafts:
        ingredients=d['ingredients']; total,missing=sum_nutrients(ingredients,by_id)
        servings=d['servings']
        allergens=sorted({a for k in ingredients for a in by_id[k]['allergens_inferred_present']})
        possible=sorted({a for k in ingredients for a in by_id[k]['allergens_possible']})
        recipe={
            'id':d['id'],'version':VERSION,'names':d['names'],'category':d['group'],
            'declared_servings':servings,'estimated_minutes':d['estimated_minutes'],
            'time_status':'author_estimate_not_kitchen_tested',
            'ingredients':[{'food_id':k,'fdc_id':by_id[k]['fdc_id'],'edible_grams':g,
                            'input_state':by_id[k]['preparation_state']} for k,g in ingredients.items()],
            'steps':{'az':d['steps_az'],'en':None,'ru':None},
            'authorship':'original_project_draft_not_a_traditional_standard_or_copied_recipe',
            'raw_input_total_g':sum(ingredients.values()),'measured_final_yield_g':None,
            'nutrient_estimate':{'basis':'ingredient_sum_no_cooking_retention_or_loss_adjustment',
                'display_scope':'estimated_per_declared_recipe_serving',
                'total_recipe':total,'per_declared_serving':{k:None if v is None else v/servings for k,v in total.items()},
                'per_100g_cooked':None,'missing_ingredient_data':{k:v for k,v in missing.items() if v},
                'free_sugars_not_derived_from_total_sugars':True,
                'display_decimals':{'energy_kcal':0,'macros_g':1}},
            'allergens_inferred_present':allergens,'allergens_possible':possible,
            'allergen_review_status':'unverified_do_not_claim_allergen_safe',
            'safety':safety(ingredients),'dietitian_reviewed':False,'reviewed_by':None,
            'kitchen_test_runs':[],'portion_photos':[],
            'portion_small_g':None,'portion_medium_g':None,'portion_large_g':None,
            'price_estimate':None,'price_region':None,
            'nutrition_adequacy_status':'not_evaluated_for_person_or_complete_meal',
            'publishable':False,'eligible_for_live_personalized_planning':False,
            'status':'formulated_source_linked_draft',
            'release_blockers':['ingredient_and_calculation_review','allergen_review',
                                'dietitian_review_for_personalized_recommendations','instruction_translation','storage_review'],
            'feature_gates':{
                'estimated_per_declared_serving':'source_match_and_calculation_review_required_not_universally_kitchen_measurement',
                'per_100g_cooked':'documented_measured_or_estimated_cooked_yield_required',
                'visual_small_medium_large_portions':'calibrated_portions_or_explicit_estimates_required'},
            'cooked_yield_estimate_g':None,'cooked_yield_source':None,
            'discovery': d.get('discovery')}
        if d['id'] in {'L05','L06','L07','L13','L14','V35'}:
            recipe['safety']['rules'].append({'rule':'stuffed_mixed_dish_conservative_target',
                'minimum_core_c':74,'use_food_thermometer':True,
                'note':'Project safety target pending recipe review; source covers stuffing/casseroles.'})
        result.append(recipe)
    assert len(result)==EXPECTED_RECIPE_COUNT and len({r['id'] for r in result})==EXPECTED_RECIPE_COUNT
    return result

def weekly_templates(recipes):
    # Editorial rotations, not calorie or nutrient matched individual plans.
    schedules=[
        [('B01','S01','M01'),('B07','V02','M14'),('B03','S03','L08'),('B04','V03','L05'),('B09','S10','L01'),('B15','V04','M15'),('B13','S02','V06')],
        [('B10','V01','M03'),('B08','S07','M07'),('B16','V08','L06'),('B05','S11','V09'),('B11','V13','M13'),('B02','S12','L07'),('B18','V11','M19')],
        [('B06','S04','M02'),('B12','V12','M09'),('B14','S09','L09'),('B17','V14','M16'),('B19','S06','L03'),('B20','V05','M10'),('B01','S14','V07')],
        [('B03','V15','M04'),('B07','S05','M08'),('B10','V10','L10'),('B04','S08','M11'),('B09','V01','L04'),('B15','S13','M18'),('B13','S15','M12')],
        [('B21','S16','M21'),('B22','V16','M30'),('B23','S17','L12'),('B24','V17','L15'),('B25','S18','M24'),('B26','V18','M32'),('B27','S19','V19')],
        [('B28','V20','M22'),('B29','S20','M23'),('B30','V21','L13'),('B21','S21','V22'),('B22','V23','M31'),('B23','S22','L16'),('B24','V24','M33')],
        [('B25','S23','M25'),('B26','V25','M26'),('B27','S24','L17'),('B28','V26','M34'),('B29','S25','L18'),('B30','V27','M35'),('B21','S16','V28')],
        [('B22','V29','M27'),('B23','S17','M28'),('B24','V30','L19'),('B25','S18','M36'),('B26','V31','L20'),('B27','S19','M37'),('B28','V32','M38')]
    ]
    names=['Bazar ertəsi','Çərşənbə axşamı','Çərşənbə','Cümə axşamı','Cümə','Şənbə','Bazar']
    return [{'id':f'W{n+1:02}','name_az':f'{n+1}-ci həftə üçün yemək ideyaları',
             'status':'editorial_rotation_not_nutritionally_approved','publishable':False,
             'personalized':False,'energy_target_kcal':None,
             'note_az':'Tam və fərdi balanslı pəhriz deyil. Porsiya, əlavə qida və klinik uyğunluq ayrıca hesablanmalıdır.',
             'days':[{'day':i+1,'day_az':names[i],'breakfast':b,'lunch':l,'dinner':d,
                      'optional_snack':f'N{(i%5)+1:02}' if n<4 else f'N{((n-4)*7+i)%10+6:02}'} for i,(b,l,d) in enumerate(s)]}
            for n,s in enumerate(schedules)]

def build_db(source, foods, recipes, weeks):
    path=ROOT/'nutrition-library.sqlite'
    with sqlite3.connect(path) as db:
        db.executescript('''
        DROP TABLE IF EXISTS library_meta;
        DROP TABLE IF EXISTS source_foods;
        DROP TABLE IF EXISTS foods;
        DROP TABLE IF EXISTS recipes;
        DROP TABLE IF EXISTS recipe_ingredients;
        DROP TABLE IF EXISTS weekly_templates;
        CREATE TABLE library_meta (key TEXT PRIMARY KEY, value TEXT NOT NULL);
        CREATE TABLE source_foods (fdc_id INTEGER PRIMARY KEY, description_en TEXT NOT NULL, data_type TEXT NOT NULL, data_json TEXT NOT NULL);
        CREATE TABLE foods (id TEXT PRIMARY KEY, fdc_id INTEGER UNIQUE NOT NULL REFERENCES source_foods(fdc_id), name_az TEXT NOT NULL, name_ru TEXT NOT NULL, category TEXT NOT NULL, publishable INTEGER NOT NULL CHECK(publishable IN(0,1)), data_json TEXT NOT NULL);
        CREATE TABLE recipes (id TEXT PRIMARY KEY, name_az TEXT NOT NULL, category TEXT NOT NULL, publishable INTEGER NOT NULL CHECK(publishable IN(0,1)), data_json TEXT NOT NULL);
        CREATE TABLE recipe_ingredients (recipe_id TEXT NOT NULL REFERENCES recipes(id), food_id TEXT NOT NULL REFERENCES foods(id), edible_grams REAL NOT NULL CHECK(edible_grams>0), PRIMARY KEY(recipe_id,food_id));
        CREATE TABLE weekly_templates (id TEXT PRIMARY KEY, data_json TEXT NOT NULL);
        CREATE INDEX source_name_idx ON source_foods(description_en);
        CREATE INDEX foods_category_idx ON foods(category);
        CREATE INDEX recipes_category_idx ON recipes(category);
        ''')
        db.execute('PRAGMA foreign_keys=ON')
        db.executemany('INSERT INTO library_meta VALUES (?,?)',[('version',VERSION),('generated_at',DATE)])
        db.executemany('INSERT INTO source_foods VALUES (?,?,?,?)',[(f['fdc_id'],f['description_en'],f['data_type'],json.dumps(f,ensure_ascii=False)) for f in source.values()])
        db.executemany('INSERT INTO foods VALUES (?,?,?,?,?,?,?)',[(f['id'],f['fdc_id'],f['names']['az'],f['names']['ru'],f['category'],0,json.dumps(f,ensure_ascii=False)) for f in foods])
        db.executemany('INSERT INTO recipes VALUES (?,?,?,?,?)',[(r['id'],r['names']['az'],r['category'],0,json.dumps(r,ensure_ascii=False)) for r in recipes])
        db.executemany('INSERT INTO recipe_ingredients VALUES (?,?,?)',[(r['id'],i['food_id'],i['edible_grams']) for r in recipes for i in r['ingredients']])
        db.executemany('INSERT INTO weekly_templates VALUES (?,?)',[(w['id'],json.dumps(w,ensure_ascii=False)) for w in weeks])
        assert not db.execute('PRAGMA foreign_key_check').fetchall()

def exports(foods, recipes, weeks):
    write_json(ROOT/'foods.json',foods);write_json(ROOT/'recipes.json',recipes);write_json(ROOT/'weekly-ideas.json',weeks)
    with (ROOT/'foods.csv').open('w',encoding='utf-8-sig',newline='') as fh:
        keys=['id','fdc_id','az','en','ru','category','preparation_state']+list(DEFS)+['status','source_url']
        w=csv.DictWriter(fh,fieldnames=keys);w.writeheader()
        for f in foods:
            w.writerow({'id':f['id'],'fdc_id':f['fdc_id'],'az':f['names']['az'],'en':f['names']['en'],'ru':f['names']['ru'],'category':f['category'],'preparation_state':f['preparation_state'],**{k:f['nutrients_per_100g'][k] for k in DEFS},'status':f['status'],'source_url':f['source']['source_url']})
    with (ROOT/'recipes.csv').open('w',encoding='utf-8-sig',newline='') as fh:
        w=csv.writer(fh);w.writerow(['id','az','en','ru','category','servings','estimated_minutes','ingredient_count','ingredient_sum_kcal_per_serving_UNTESTED','status','publishable'])
        for r in recipes:
            w.writerow([r['id'],r['names']['az'],r['names']['en'],r['names']['ru'],r['category'],r['declared_servings'],r['estimated_minutes'],len(r['ingredients']),r['nutrient_estimate']['per_declared_serving']['energy_kcal'],r['status'],False])
    by_id={f['id']:f for f in foods}
    lines=[f'QİDA KİTABXANASI / {len(foods)} ƏRZAQ + {len(recipes)} RESEPT',f'Versiya {VERSION} / 4 noyabr 2026',
        'Rəqəmlər USDA mənbəsindən idxal olunub. Yerli uyğunluq və tərcümə yoxlanmayıb.',
        'Reseptlər müəllif layihəsidir; bişirilib ölçülməyib və dietoloq təsdiqi yoxdur.',
        'Resept rəqəmləri yalnız inqrediyent toplamıdır, bişmə itkisi və nutrient saxlanması ilə düzəldilməyib.',
        'Resept miqdarı və elan edilən porsiya sayına əsaslanan təxmini hesab üçün bütün yeməklərin test bişirməsi universal şərt deyil.',
        'Hazır yeməyin 100 qramı və Az/Orta/Çox porsiyaları üçün sənədləşdirilmiş çıxım və ölçü lazımdır; bunlar uydurulmayıb.\n','ƏRZAQLAR']
    for f in foods:
        n=f['nutrients_per_100g']; lines.append(f"{f['id']} | {f['names']['az']} | FDC {f['fdc_id']} | 100 q: {n['energy_kcal']} kkal; zülal {n['protein_g']} q; yağ {n['fat_g']} q; karbohidrat {n['carbohydrate_g']} q")
    lines+=['\nRESEPTLƏR']
    for r in recipes:
        est=r['nutrient_estimate']['per_declared_serving']['energy_kcal']
        lines += ['\n'+r['id']+' / '+r['names']['az'],f"{r['declared_servings']} nəzərdə tutulan porsiya; təxmini hazırlıq {r['estimated_minutes']} dəq (sınaqdan keçməyib).",
          f"İnqrediyent toplamından ilkin hesab: porsiyaya {est:.0f} kkal. Hazır yemək ölçüsü deyil."]
        lines += [f"- {by_id[i['food_id']]['names']['az']}: {i['edible_grams']} q" for i in r['ingredients']]
        lines += [str(n+1)+'. '+s for n,s in enumerate(r['steps']['az'])]
        lines += ['Allergen namizədləri: '+(', '.join(r['allergens_inferred_present']+r['allergens_possible']) or 'Məhsul və çarpaz təmas yoxlanmayıb.'), 'Status: ölçmə və mütəxəssis təsdiqi gözləyən layihə.']
    lines+=[f'\n{len(weeks)} HƏFTƏLİK İDEYA ROTASİYASI / FƏRDİ PƏHRİZ DEYİL']
    rs={r['id']:r for r in recipes}
    for week in weeks:
        lines.append('\n'+week['name_az'])
        for d in week['days']:
            lines.append(d['day_az']+' | '+ ' | '.join(rs[d[k]]['names']['az'] for k in ['breakfast','lunch','dinner']))
    (ROOT/'catalogue-az.txt').write_text('\n'.join(lines)+'\n')

def main():
    source=read_sources();foods=curated(source);recipes=recipe_library(foods);weeks=weekly_templates(recipes)
    build_db(source,foods,recipes,weeks);exports(foods,recipes,weeks)
    manifest={'version':VERSION,'generated_at':DATE,'source_food_count':len(source),
        'source_types':dict(Counter(f['data_type'] for f in source.values())),
        'curated_food_count':len(foods),'recipe_draft_count':len(recipes),'weekly_idea_count':len(weeks),
        'food_categories':dict(Counter(f['category'] for f in foods)),
        'recipe_categories':dict(Counter(r['category'] for r in recipes)),
        'publishable_food_count':0,'publishable_recipe_count':0,
        'source_files':[{'file':p.name,'url':SOURCE_URLS[p.name],'sha256':hashlib.sha256(p.read_bytes()).hexdigest(),'bytes':p.stat().st_size} for p in sorted((ROOT/'sources').glob('*.zip'))],
        'source_downloads_page':'https://fdc.nal.usda.gov/download-datasets/',
        'license':'USDA FoodData Central data: CC0 1.0; original recipe drafts authored for this project',
        'energy_selection_policy':'1008 Energy, else 2048 Atwater Specific, else 2047 Atwater General; original method ID recorded. Not recalculated with 4/4/9.',
        'limitations':['SR Legacy is historical final 2018 release, not current Azerbaijani products',
            'Foundation Foods 2026-10 imported as reference, not silently substituted for curated SR entries',
            'AZ/RU food names and recipe titles are editorial drafts; only AZ preparation steps are authored',
            'No dietitian sign-off, kitchen measurements, calibrated portion images or verified regional costs',
            'Allergen inference is not product verification or a guarantee against cross-contact',
            'Recipe calories are unadjusted ingredient sums, not measured cooked portions',
            'Weekly rotations are meal ideas, not nutrition-complete personalized plans']}
    write_json(ROOT/'manifest.json',manifest)
    print(json.dumps({k:v for k,v in manifest.items() if k.endswith('count') or k in ['source_types','food_categories','recipe_categories']},ensure_ascii=False,indent=2))

if __name__=='__main__': main()
