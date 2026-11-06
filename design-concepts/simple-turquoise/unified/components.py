"""Shared markup for static design references; no application behavior."""
from html import escape


def card(title, text='', kind=''):
    return f'<section class="card {kind}"><h3>{title}</h3><p>{text}</p></section>'


def note(text):
    return card('Diqqət', text, 'notice')


def field(label, value='', error=None, unit=None):
    content = f'<label>{label}<span class="field">{value or "Daxil et"}{f"<b>{unit}</b>" if unit else ""}</span></label>'
    return content + (f'<p class="inline-error">⚠ {error}</p>' if error else '')


def options(items, selected=None):
    return '<div class="options">' + ''.join(f'<div class="option {"selected" if x==selected else ""}">{"✓ " if x==selected else ("" if x in ["Resept","Yedim","Resepti saxla","İnqrediyentləri dəyiş","İşıq","Qalereyadan seç"] else "○ ")}{x}</div>' for x in items) + '</div>'


def row(title, detail='', art=None, selected=False):
    image = f'<img class="row-art" src="assets/{escape(art)}.png" alt="">' if art else ''
    return f'<section class="card row {"selected" if selected else ""}">{image}<div><h3>{"✓ " if selected else ""}{title}</h3><p>{detail}</p></div></section>'


def stepper(label, value, unit=''):
    return f'<section class="card"><h3>{label}</h3><div class="stepper"><span>−</span><b>{value} {unit}</b><span>+</span></div></section>'


def panel(id, title, body, cta='Davam et', back='Geri qayıt', art=None, tab=None, **kwargs):
    return dict(id=id,title=title,body=body,cta=cta,back=back,art=art,tab=tab,**kwargs)


def sheet(num, slug, title, panels, legacy=None, requirements=None):
    return dict(num=num,slug=slug,title=title,panels=panels,legacy=legacy or [],requirements=requirements or ['18','19'])


def measurements(error=None, values=None):
    values=values or ['28','172','78,4']
    return ''.join(f'<div class="measure"><div class="measure-row"><b>{n} · {u}</b><div class="stepper"><span>−</span><b>{v}</b><span>+</span></div></div>'+(f'<p class="inline-error">⚠ {error[i]}</p>' if error and i in error else '')+'</div>' for i,(n,u,v) in enumerate(zip(['Yaş','Boy','Çəki'],['il','sm','kq'],values)))+f'<p class="check">{"□" if error else "✓"} Ölçüləri və vahidləri təsdiqləyirəm.</p>'+card('Hesablama üçün cins · könüllüdür',options(['Kişi','Qadın','İndi seçmirəm']))+'<p>Cins verilməsə avtomatik hədəf yaradılmır.</p>'


def preference_body():
    return card('Hazırlanma vaxtı', options(['15 dəq','30 dəq','60 dəq'],'30 dəq'))+card('Büdcə seçimi', options(['Qənaətli','Orta','Sərbəst'],'Orta'))+card('Gündə yemək sayı',options(['3','4','5'],'3'))+field('Region','Azərbaycan')+field('Valyuta','AZN')+field('Həftəlik büdcə','100',unit='AZN')+stepper('Neçə nəfər üçün?','1')


def week():
    days=['B.e.','Ç.a.','Ç.','C.a.','C.','Ş.','B.']
    return '<div class="week">'+''.join(f'<div class="day {"selected" if i==4 else ""}">{s}<b>{i+2}</b>{"✓ Bu gün" if i==4 else ""}</div>' for i,s in enumerate(days))+'</div>'


def nutrients(energy='500', protein='30', carb='50', fat='20'):
    return f'<b>{energy} kkal</b><br>Zülal {protein} q · Karbohidrat {carb} q · Yağ {fat} q'


def ingredient(name, amount, state='xam'):
    return f'<section class="card ingredient"><h3>{name} · {state}</h3><div class="stepper"><span>−</span><b>{amount} q</b><span>+</span></div><div class="text-action">Çıxar</div></section>'
