"""Two scoped AZ draft sheets; no native/runtime behavior or APK."""
import json
import re
import subprocess
from render import render, HERE, CHROME

def main():
    for num, slug in [(31, 'allergiya-ilkin-veziyyet'), (32, 'plan-hazirdir')]:
        sheet = json.loads((HERE / f'{num}-{slug}.json').read_text())
        render(sheet)
        target = HERE / f'{num}-{slug}.html'
        html = target.read_text()
        count = 4 if num == 31 else 5
        progress = '<div class="progress">' + ''.join('<i class="on"></i>' if n < count else '<i></i>' for n in range(5)) + f'<b>{count}/5</b></div>'
        header = '<header><div class="back">‹</div><b>🥗 QİDA</b></header>'
        html = re.sub(r'<header>.*?</header>', header + progress, html)
        if num == 32:
            # Completion ends onboarding; Today uses ordinary app navigation.
            pos = html.index('<article>', html.index('</article>'))
            html = html[:pos] + html[pos:].replace(header + progress, header, 1)
        css = '''
article{background:var(--simple-surface)}header{justify-content:flex-start;gap:var(--space-lg)}
.progress{display:flex;gap:var(--space-sm);align-items:center;padding:var(--space-md) var(--space-lg)}
.progress i{height:8px;flex:1;border-radius:var(--space-sm);background:var(--simple-bg);border:1px solid var(--simple-muted)}.progress i.on{background:var(--simple-primary);border-color:var(--simple-primary)}
.answers{display:flex;gap:var(--space-md);margin:var(--space-md) 0}.answer{flex:1;border:1px solid var(--simple-muted);border-radius:var(--space-md);padding:var(--space-md);font-weight:bold;min-height:56px}
.selected{background:var(--simple-bg)}.helper{margin:var(--space-md) 0 var(--space-lg);font-size:16px}.allergens{display:grid;grid-template-columns:repeat(3,1fr);gap:var(--space-sm);margin-top:var(--space-md)}.allergen{border:1px solid var(--simple-muted);border-radius:var(--space-md);text-align:center;padding:var(--space-sm)}.allergen img{width:56px;height:56px;object-fit:contain}.allergen b,.allergen span{display:block;font-size:16px}.allergen span{padding:var(--space-sm)}.link{color:var(--simple-primary);font-weight:bold;padding:var(--space-md) 0;min-height:48px}label{margin-top:var(--space-lg)}.lead{margin:0 0 var(--space-lg)}main>.card{background:var(--simple-bg)}main>.notice{background:var(--simple-peach)}
'''
        html = html.replace('</style>', css+'</style>').replace('təsdiqli D02/D03 quruluşu qorunur', '19-onboarding və 00-reference · yeni axına əlavə')
        target.write_text(html)
        width = 64 + len(sheet['panels'])*400 + (len(sheet['panels'])-1)*24
        subprocess.run([CHROME,'--headless','--disable-gpu','--no-sandbox','--hide-scrollbars','--allow-file-access-from-files','--force-device-scale-factor=1', f'--screenshot={target.with_suffix(".png")}',f'--window-size={width},1320',target.as_uri()],check=True,stdout=subprocess.DEVNULL,stderr=subprocess.DEVNULL)

if __name__ == '__main__':
    main()
