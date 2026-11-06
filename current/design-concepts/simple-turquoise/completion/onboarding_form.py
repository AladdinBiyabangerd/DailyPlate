"""Step 2/5 concept layout, matching the existing full onboarding form."""
import re


def align_form(html):
    progress = '<div class="progress"><i></i><i></i><i></i><i></i><i></i><b>2/5</b></div>'
    html = re.sub(r'<header>.*?</header>', '<header><div class="back">‹</div><b class="brand">🥗 QİDA</b></header>' + progress, html)
    css = '''
article{background:var(--simple-surface)}
header{justify-content:flex-start;gap:var(--space-lg)}
.brand{color:var(--simple-text);font-size:24px}
.progress{display:flex;align-items:center;gap:var(--space-sm);padding:var(--space-md) var(--space-lg) 0}
.progress i{height:8px;flex:1;border-radius:var(--space-sm);background:var(--simple-bg);border:1px solid var(--simple-muted)}
.progress i:nth-child(-n+2){background:var(--simple-primary);border-color:var(--simple-primary)}
.progress b{margin-left:var(--space-sm)}
main{padding-top:var(--space-lg)}h2{min-height:80px;display:flex;align-items:center}
.measurements{clear:both;margin-bottom:var(--space-md)}
.measure{margin-bottom:var(--space-md)}
.measure-row{display:flex;align-items:center;justify-content:space-between;gap:var(--space-sm);padding:var(--space-sm);background:var(--simple-bg);border-radius:var(--space-sm)}
.stepper{display:flex;align-items:center;background:var(--simple-surface);border:1px solid var(--simple-muted);border-radius:var(--space-sm)}
.stepper span{min-width:48px;min-height:48px;display:flex;align-items:center;justify-content:center}
.stepper span:nth-child(2){min-width:96px;font-size:16px}
.inline-error{background:var(--simple-peach);padding:var(--space-sm);border-radius:var(--space-sm);margin-top:var(--space-sm)}
main>.confirm{margin:var(--space-md) 0;color:var(--simple-text)}
.sex{margin-top:var(--space-lg)}small{float:right;font-weight:normal;font-size:16px;color:var(--simple-muted)}
.choices{background:var(--simple-bg);border-radius:var(--space-sm);padding:var(--space-md) var(--space-sm)}
.hint,.retained{font-size:16px}
.button{background:var(--simple-bg);color:var(--simple-muted);border:1px solid var(--simple-muted)}
'''
    return html.replace('</style>', css + '</style>').replace('təsdiqli D02/D03 quruluşu qorunur', '19-onboarding · tam 2/5 forması · səhv sahənin altında')
