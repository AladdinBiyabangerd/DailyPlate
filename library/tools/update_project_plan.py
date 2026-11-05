"""Export the canonical plan without overwriting authored requirements or source notes."""
import json
from pathlib import Path
ROOT=Path(__file__).resolve().parents[2]
p=ROOT/'planning'
data=json.loads((p/'research-data.json').read_text())
manifest=json.loads((ROOT/'library/manifest.json').read_text())
foods=manifest['curated_food_count'];recipes=manifest['recipe_draft_count']
version=data['metadata']['version']
heading=f'Versiya {version} | 5 noyabr 2026 | {foods} ərzaq qeydi və {recipes} resept layihəsi'
txt='QIDA — ELMİ ƏSAS, MƏZMUN VƏ REAL İCRA PLANI\n'+heading+'\n\n'
for s in data['sections']:
 txt+=s['title']+'\n'+s['summary']+'\n\n'+'\n\n'.join(f'{i+1}. {x}' for i,x in enumerate(s['items']))
 if s['id']=='hefte':txt+='\n\n'+'\n'.join(' | '.join(row) for row in data['week'])
 txt+='\n\nMənbələr:'+(' '+', '.join(s['refs']) if s['refs'] else '')+'\n\n'
txt+='MƏNBƏ REYESTRİ\n\n'+'\n\n'.join(f"{s['id']} - {s['name']}\n{s['url']}\n{s['use']}" for s in data['sources'])+'\n'
(p/'nutrition-science-implementation-plan.txt').write_text(txt)
f=p/'nutrition-implementation.canvas.tsx';code=f.read_text();start=code.index('const data = ')+len('const data = ');end=code.index(';\nexport default',start)
f.write_text(code[:start]+json.dumps(data,ensure_ascii=False,indent=2)+code[end:])
print(f'Exported canonical plan {version}: {foods} foods, {recipes} recipe drafts; authored sections preserved.')

md = '# DailyPlate — vahid qidalanma və funksional icra planı\n\n'
md += f"> Versiya {version} · 5 noyabr 2026 (Asia/Baku) · Paket: `com.aladdin.dailyplate`\n"
md += '> Bu fayl `planning/research-data.json`-dan yaradılır. Tələbi əvvəl kanonik JSON-da dəyiş, sonra `python3 library/tools/update_project_plan.py` işlət.\n\n'
md += 'Əvvəlki nutrition-plan-design və məhsul planı birləşdirilib. §1–19 mövcud məhsul qaydalarını, §20–25 ziddiyyət həllərini və müqavilələri, §26–29 konkret enerji/makro policy tədqiqat təklifini əhatə edir. §30 planın hamısı üzrə istifadəçinin bildirdiyi mütəxəssis təsdiqini qeydə alır; tətbiqin implementasiya/test statusu ayrıdır.\n\n'
md += 'İcra növbəsi: [coding-tasks](../planning/coding-tasks.txt). Əlavə məcburi müqavilə: [T01–T19](../planning/meal-customization-requirements.txt).\n\n'
for section in data['sections']:
 md += '## '+section['title']+'\n\n'+section['summary']+'\n\n'
 md += '\n\n'.join(f"{i}. {item}" for i,item in enumerate(section['items'],1))+'\n\n'
 if section['id']=='hefte':
  md += '\n'.join('- '+' · '.join(row) for row in data['week'])+'\n\n'
 if section['refs']:
  md += 'Mənbələr: '+', '.join(f"[{sid}]({next(s['url'] for s in data['sources'] if s['id']==sid)})" for sid in section['refs'])+'\n\n'
md += '## Mənbə reyestri\n\n'
md += '\n\n'.join(f"### {s['id']} — {s['name']}\n\n[{s['name']}]({s['url']})\n\n{s['use']}" for s in data['sources'])+'\n'
(ROOT/'docs/nutrition-plan-design.md').write_text(md)

(p/'nutrition-policy-candidate.json').write_text(json.dumps(data['nutrition_policy_candidate'], ensure_ascii=False, indent=2)+'\n')
