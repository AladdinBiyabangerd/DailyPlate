"""Export the canonical plan without overwriting authored requirements or source notes."""
import json
from pathlib import Path
ROOT=Path(__file__).resolve().parents[2]
p=ROOT/'planning'
data=json.loads((p/'research-data.json').read_text())
manifest=json.loads((ROOT/'library/manifest.json').read_text())
foods=manifest['curated_food_count'];recipes=manifest['recipe_draft_count']
version='1.7'
heading=f'Versiya {version} | 4 noyabr 2026 | {foods} ərzaq qeydi və {recipes} resept layihəsi'
txt='QIDA — ELMİ ƏSAS, MƏZMUN VƏ REAL İCRA PLANI\n'+heading+'\n\n'
for s in data['sections']:
 txt+=s['title']+'\n'+s['summary']+'\n\n'+'\n\n'.join(f'{i+1}. {x}' for i,x in enumerate(s['items']))
 if s['id']=='hefte':txt+='\n\n'+'\n'.join(' | '.join(row) for row in data['week'])
 txt+='\n\nMənbələr: '+', '.join(s['refs'])+'\n\n'
txt+='MƏNBƏ REYESTRİ\n\n'+'\n\n'.join(f"{s['id']} - {s['name']}\n{s['url']}\n{s['use']}" for s in data['sources'])+'\n'
(p/'nutrition-science-implementation-plan.txt').write_text(txt)
f=p/'nutrition-implementation.canvas.tsx';code=f.read_text();start=code.index('const data = ')+len('const data = ');end=code.index(';\nexport default',start)
f.write_text(code[:start]+json.dumps(data,ensure_ascii=False,indent=2)+code[end:])
print(f'Exported canonical plan {version}: {foods} foods, {recipes} recipe drafts; authored sections preserved.')
