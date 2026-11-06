"""Build a portable static AZ design library. Render via render.mjs separately."""
import json
import re
import shutil
from pathlib import Path
from html import escape
from components import panel, sheet, measurements
from legacy import build as legacy
from states import build as states
from style import TOKENS, CSS

HERE=Path(__file__).resolve().parent
ROOT=HERE.parents[2]
NAV=['Bu gün','Menyu','Əlavə et','İrəliləyiş','Profil']

def document(s):
    blocks=[]
    for p in s['panels']:
        art=p.get('art')
        image=f'<img class="art" src="assets/{art}.png" alt="İllüstrasiya">' if art else ''
        if p.get('hero') and art:
            image=f'<div class="hero"><img src="assets/{art}.png" alt=""><p>İllüstrasiya</p></div>'
        step=p.get('step')
        progress='<div class="progress">'+''.join(f'<i class="{"filled" if i<=step else ""}"></i>' for i in range(1,6))+f'<b>{step}/5</b></div>' if step else ''
        nav='<nav aria-label="Əsas bölmələr">'+''.join(f'<div class="{"active" if n==p.get("tab") else ""}"><b>{"✓" if n==p.get("tab") else ["⌂","≡","+","↗","○"][i]}</b>{n}</div>' for i,n in enumerate(NAV))+'</nav>' if p.get('tab') else ''
        body=re.sub(r'file:[^\s"<>]+','',p['body'])
        blocks.append(f'''<section class="panel" data-id="{p['id']}"><article class="phone"><div class="status">9:41 <span>●●● ▰</span></div><header><div class="back" aria-label="Geri">‹</div><div class="brand"><img src="assets/art_healthy.png" alt="">QİDA</div></header>{progress}<main>{image}<h2>{p['title']}</h2>{body}</main><footer><div class="button {'disabled' if p.get('disabled') else ''}">{p['cta']}</div><div class="secondary">{p['back']}</div></footer>{nav}<div class="handle"></div></article><p class="panel-caption">{p['id']} · {p['title']}</p></section>''')
    return f'''<!doctype html><html lang="az"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width"><style>{TOKENS}{CSS}</style><title>{s['num']:02d} · {escape(s['title'])}</title></head><body><h1>{s['num']:02d} · {escape(s['title'])}</h1><p class="subtitle">Vahid firuzəyi dizayn · AZ · 6 noyabr 2026 · <a href="index.html">Bütün dəstlər</a></p><div class="screens">{''.join(blocks)}</div><p class="caption">Dizayn maketi · rəqəm və tarixlər nümunədir · işlək tətbiq və ya klinik uyğunluq sübutu deyil. Əsas mətn 16 px; vahidlər: kkal / q / kq / sm. Ölçülməmiş bişmiş çıxım dəqiq qram hesabı yaratmır.</p></body></html>'''


def build():
    sheets=legacy()
    completion_path=HERE.parent/'completion/screens.json'
    if not completion_path.exists(): completion_path=HERE/'source-inputs/completion-screens.json'
    completion=json.loads(completion_path.read_text())
    for s in completion:
        s['legacy']=[f'completion/{s["num"]}']
        for p in s['panels']:
            if p.get('art') == 'art_ingredient_mushroom': p['art']='art_budget'
            for key in ['title','body','cta','back']:
                p[key]=p[key].replace('Önizləmə','Önbaxış').replace('önizləmə','önbaxış').replace(' · Z ',' · Zülal ').replace(' · K ',' · Karbohidrat ').replace(' · Y ',' · Yağ ')
        if s['num']==30:
            for i,p in enumerate(s['panels']):
                errors=[{0:'Yaşı rəqəmlə yaz. Məsələn: 28.'},{1:'Boyunu sm ilə yaz. Məsələn: 172.'},{2:'Çəki sıfırdan böyük olmalıdır. Məsələn: 78,4.'}]
                values=[['iyirmi səkkiz','172','78,4'],['28','NaN','78,4'],['28','172','−5']]
                p['body']=measurements(errors[i],values[i])+'<p>Digər cavabların saxlanıb.</p>'
                p['step']=2
                p['disabled']=True
        sheets.append(s)
    sheets+=states()
    (HERE/'assets').mkdir(exist_ok=True)
    for p in (ROOT/'android/app/src/main/res/drawable-nodpi').glob('art_*.png'):
        shutil.copyfile(p,HERE/'assets'/p.name)
    for s in sheets:
        filename=f'{s["num"]:02d}-{s["slug"]}'
        s['file']=filename
        (HERE/(filename+'.html')).write_text(document(s))
    (HERE/'screens.json').write_text(json.dumps(sheets,ensure_ascii=False,indent=2)+'\n')
    links=''.join(f'<section class="index-card"><h2>{s["num"]:02d} · {s["title"]}</h2><p>{len(s["panels"])} ekran</p><a href="{s["file"]}.html"><img src="{s["file"]}.png" alt="{s["title"]}"></a><div><a href="{s["file"]}.png">PNG aç</a><a href="{s["file"]}.html">HTML aç</a></div></section>' for s in sheets)
    index_head='<!doctype html><html lang="az"><meta charset="utf-8"><meta name="viewport" content="width=device-width"><title>QİDA · vahid dizayn kitabxanası</title><style>'+TOKENS+CSS+'</style>'
    (HERE/'index.html').write_text(index_head+'<h1>QİDA · vahid dizayn kitabxanası</h1><p>6 noyabr 2026 · '+str(len(sheets))+' dəst · '+str(sum(len(s['panels']) for s in sheets))+' ekran. Bütün aktiv dəstlər eyni komponentlərlə yenidən hazırlanıb.</p><p>Başlamaq üçün PNG və ya HTML seç. Statik maketdir; formalar və düymələr interaktiv tətbiq deyil. Təsdiqli D02/D03 istinadları ayrıca references qovluğundadır.</p><div class="index-grid">'+links+'</div></html>')
    # Every old selected turquoise sheet has a concrete new destination.
    mapping={f'{s["num"]:02d}':s['file']+'.png' for s in sheets if s['num']<=18}
    mapping['19-onboarding-tamamlanmis-axin.png']=['00-esas-istiqamet.png','01-baslangic.png','02-secimler-ve-hesab.png','12-elave-profil-secimleri.png','31-onboarding-tamamlanma.png','32-olcu-giris-serhedleri.png','33-allergiya-ve-budce-xetalari.png']
    mapping['24-inqrediyent-redaktoru-xetalar.png']=['34-resept-redaktoru-xetalar.png','35-resept-allergen-ve-vahid.png']
    for s in sheets:
        if 19<=s['num']<=30: mapping[f'completion/{s["num"]}']=s['file']+'.png'
    (HERE/'replacement-map.json').write_text(json.dumps(mapping,ensure_ascii=False,indent=2)+'\n')
    print(f'{len(sheets)} sheets / {sum(len(s["panels"]) for s in sheets)} panels')

if __name__=='__main__': build()
