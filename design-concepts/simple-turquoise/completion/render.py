"""Render AZ concept sheets with local Chrome, never an APK/runtime acceptance."""
import json
import subprocess
from pathlib import Path
from html import escape

HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[2]
CHROME = '/Applications/Google Chrome.app/Contents/MacOS/Google Chrome'
NAV = ['Bu gün', 'Menyu', 'Əlavə et', 'İrəliləyiş', 'Profil']

def render(sheet):
    panels = []
    for p in sheet['panels']:
        art = ROOT / ('android/app/src/main/res/drawable-nodpi/' + str(p.get('art')) + '.png')
        illustration = f'<img class="art" src="{art.as_uri()}" alt="İllüstrasiya">' if art.exists() else ''
        nav = '<nav>' + ''.join(f'<div class="{"active" if n == p.get("tab") else ""}"><b>{"✓" if n == p.get("tab") else ["⌂", "≡", "+", "↗", "○"][i]}</b>{n}</div>' for i,n in enumerate(NAV)) + '</nav>' if p.get('tab') else ''
        panels.append(f'''<article><div class="status">9:41 <span>●●● ▰</span></div><header><div class="back">‹</div><span>{p['id']}</span></header><main>{illustration}<h2>{p['title']}</h2>{p['body']}</main><footer><div class="button">{p['cta']}</div><div class="secondary">{p['back']}</div></footer>{nav}<div class="handle"></div></article>''')
    title = f"{sheet['num']} · {sheet['title']}"
    css = '''
@import url('../../tokens.css');
*{box-sizing:border-box}body{margin:0;padding:32px;background:var(--simple-bg);color:var(--simple-text);font:16px/1.45 Arial,sans-serif}
h1{font-size:28px;line-height:1.2;margin:0 0 8px;font-weight:800} .subtitle{color:var(--simple-muted);margin:0 0 24px}
.screens{display:flex;gap:24px;align-items:stretch}article{width:400px;flex-shrink:0;display:flex;flex-direction:column;min-height:1016px;border:2px solid var(--simple-muted);border-radius:32px;background:var(--simple-bg);overflow:hidden}
.status{padding:16px 24px 0;font-weight:700}.status span{float:right}header{display:flex;align-items:center;justify-content:space-between;padding:16px 24px 0;color:var(--simple-muted)}.back{background:var(--simple-surface);border-radius:16px;width:48px;height:48px;text-align:center;font-size:32px;color:var(--simple-text)}main{padding:16px 24px;flex:1}h2{font-size:24px;line-height:1.2;margin:0 0 24px;font-weight:800}h3{font-size:16px;line-height:1.35;margin:0 0 8px}p{margin:0;color:var(--simple-muted)}main>p{margin:16px 0 0}b{color:var(--simple-text)}.art{width:80px;height:80px;object-fit:contain;float:right;margin:0 0 16px 16px}.card{background:var(--simple-surface);padding:16px;border-radius:var(--radius-card);margin-bottom:16px;clear:both}.card p{line-height:1.5}.selected{outline:2px solid var(--simple-primary);outline-offset:-2px}.notice{background:var(--simple-peach)}.notice p{color:var(--simple-text)}.chip{display:table;color:var(--simple-primary);background:var(--simple-surface);padding:8px 16px;border-radius:16px;margin-bottom:16px;font-weight:bold;font-size:16px;clear:both}label{display:block;margin:0 0 16px;font-weight:bold;clear:both}.field{border:1px solid var(--simple-muted);border-radius:16px;background:var(--simple-surface);padding:16px;margin-top:8px;font-weight:normal;min-height:56px}.error{background:var(--simple-peach);color:var(--simple-text);padding:16px;border-radius:16px;margin:0 0 16px}footer{padding:8px 24px 0}.button{background:var(--simple-primary);color:var(--simple-surface);border-radius:16px;min-height:56px;padding:16px;text-align:center;font-weight:bold}.secondary{color:var(--simple-primary);min-height:56px;padding:16px 0;text-align:center;font-weight:bold}nav{display:flex;background:var(--simple-surface);padding:8px;gap:4px}nav div{flex:1;font-size:11px;text-align:center;color:var(--simple-muted);padding:8px 0}nav b{display:block;font-size:24px;line-height:1.1;color:inherit}nav .active{color:var(--simple-primary);font-weight:bold;background:var(--simple-bg);border-radius:16px}.handle{height:4px;width:96px;background:var(--simple-text);margin:8px auto;border-radius:4px}.caption{margin-top:16px;color:var(--simple-muted)}
'''
    html = f'<!doctype html><html lang="az"><meta charset="utf-8"><title>{escape(title)}</title><style>{css}</style><h1>{escape(title)}</h1><p class="subtitle">AZ konsept · rəqəmlər və tarixlər nümunədir · işlək tətbiq ekranı deyil</p><div class="screens">'+''.join(panels)+'</div><p class="caption">Vahid istinad: 00-reference · təsdiqli D02/D03 quruluşu qorunur · əsas mətn 16 px · tarix: 6 noyabr 2026</p></html>'
    if sheet['num'] == 30:
        from onboarding_form import align_form
        html = align_form(html)
    target = HERE / (str(sheet['num'])+'-'+sheet['slug']+'.html')
    target.write_text(html)
    width=64+len(panels)*400+(len(panels)-1)*24
    subprocess.run([CHROME,'--headless','--disable-gpu','--no-sandbox','--hide-scrollbars','--allow-file-access-from-files','--force-device-scale-factor=1',f'--screenshot={target.with_suffix(".png")}','--no-pdf-header-footer',f'--window-size={width},1200',target.as_uri()],check=True,stdout=subprocess.DEVNULL,stderr=subprocess.DEVNULL)

if __name__ == '__main__':
    for sheet in json.loads((HERE/'screens.json').read_text()):
        render(sheet)
        print(sheet['num'], sheet['slug'])
