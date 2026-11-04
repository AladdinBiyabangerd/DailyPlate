from pathlib import Path
import json, re
from xml.sax.saxutils import escape
from reportlab.pdfgen import canvas
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, PageBreak, Table, TableStyle, KeepTogether
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.colors import HexColor
from reportlab.lib.enums import TA_LEFT
from reportlab.lib.pagesizes import A4
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont

ROOT = Path(__file__).resolve().parent
data = json.loads((ROOT / 'research-data.json').read_text())
css = (ROOT.parent / 'design-concepts/tokens.css').read_text()
def token(name):
    return HexColor(re.search(r'--'+name+r':\s*(#[0-9A-Fa-f]+)', css).group(1))
ink, muted, teal = token('simple-text'), token('simple-muted'), token('simple-primary')
paper, pale = token('simple-surface'), token('simple-bg')
pdfmetrics.registerFont(TTFont('Arial', '/System/Library/Fonts/Supplemental/Arial.ttf'))
pdfmetrics.registerFont(TTFont('ArialBold', '/System/Library/Fonts/Supplemental/Arial Bold.ttf'))
pdfmetrics.registerFontFamily('Arial',normal='Arial',bold='ArialBold',italic='Arial',boldItalic='ArialBold')
styles = getSampleStyleSheet()
styles.add(ParagraphStyle(name='BodyAZ', fontName='Arial', fontSize=10, leading=14, textColor=ink, spaceAfter=9))
styles.add(ParagraphStyle(name='LeadAZ', fontName='ArialBold', fontSize=12, leading=17, textColor=teal, spaceAfter=18))
styles.add(ParagraphStyle(name='TitleAZ', fontName='ArialBold', fontSize=24, leading=30, textColor=ink, spaceAfter=24))
styles.add(ParagraphStyle(name='HeadingAZ', fontName='ArialBold', fontSize=19, leading=25, textColor=ink, spaceAfter=16))
styles.add(ParagraphStyle(name='SmallAZ', fontName='Arial', fontSize=8, leading=11, textColor=muted, spaceAfter=8))
styles.add(ParagraphStyle(name='CellAZ', fontName='Arial', fontSize=8, leading=11, textColor=ink))
styles.add(ParagraphStyle(name='CellBoldAZ', fontName='ArialBold', fontSize=8, leading=11, textColor=paper))
def p(text, style='BodyAZ'):
    return Paragraph(escape(text).replace('\n','<br/>'), styles[style])
def link(source):
    return Paragraph(f'<link href="{escape(source["url"])}" color="{teal.hexval()}">{escape(source["id"]+" - "+source["name"])}</link>',styles['SmallAZ'])
story=[]
story += [Spacer(1,32),p('QİDA / MƏZMUN VƏ HESABLAMA NÜVƏSİ','LeadAZ'),p('Elmi əsasdan\nişlək məhsula','TitleAZ'),p('Mənbələr, yerli reseptlər, fərdi hədəflər və həftəlik menyu üçün real icra planı','LeadAZ'),p('Versiya 1.7 | 4 noyabr 2026 | 500 ərzaq və 224 resept layihəsi','SmallAZ'),Spacer(1,24)]
story += [p('Əsas qərar','HeadingAZ'),p('Hədəf quruluş: mənbəli ərzaq məlumatı + qramla tərkibi və üsulu yoxlanmış reseptlər + fərdi tövsiyələr üçün təsdiqli qaydalar. AI seçimə və izaha kömək edir; hesablamanı və sərt filtrləri yoxlanmış kod aparır.'),Spacer(1,16),p('Kitabxana və seçim tələbləri genişləndirildi','HeadingAZ'),p('500 mənbəli ərzaq qeydi, qramla tərkibi və addımları yazılmış 224 resept layihəsi, 8 həftəlik ideya rotasiyası. SQLite bazasında 8 217 USDA istinad qeydi. JSON və CSV idxalı hazırdır.'),Spacer(1,16),p('Status','HeadingAZ'),p('Resept miqdarı və porsiya sayı ilə təxmini inqrediyent hesabı hazırdır. Bütün reseptləri fiziki bişirmək bunun universal şərti deyil. Hazır yeməyin 100 qramı və şəkilli porsiya üçün sənədləşdirilmiş çıxım ayrıca lazımdır. Redaktor və fərdi tövsiyə qaydalarının təsdiqi hələ tamamlanmayıb.'),p('İnqrediyent redaktəsi, yemək/gün dəyişmə və foto sonrası təsdiq qərarları saxlanılıb.','SmallAZ'),PageBreak()]
story += [p('Planın xəritəsi','HeadingAZ')]
for section in data['sections']:
    story += [p(section['title'])]
story += [PageBreak()]
for section in data['sections']:
    story += [p(section['title'],'HeadingAZ'),p(section['summary'],'LeadAZ')]
    for i,item in enumerate(section['items'],1):
        story.append(Paragraph(f'<b>{i:02d}.</b> '+escape(item),styles['BodyAZ']))
    if section['id']=='hefte':
        headers=['Gün','Səhər ideyası','Nahar ideyası','Şam ideyası']
        rows=[[p(h,'CellBoldAZ') for h in headers]]+[[p(x,'CellAZ') for x in row] for row in data['week']]
        t=Table(rows,colWidths=[83,116,145,145],repeatRows=1,hAlign='LEFT')
        t.setStyle(TableStyle([('BACKGROUND',(0,0),(-1,0),teal),('ROWBACKGROUNDS',(0,1),(-1,-1),[paper,pale]),('VALIGN',(0,0),(-1,-1),'TOP'),('LEFTPADDING',(0,0),(-1,-1),8),('RIGHTPADDING',(0,0),(-1,-1),8),('TOPPADDING',(0,0),(-1,-1),10),('BOTTOMPADDING',(0,0),(-1,-1),10)]))
        story += [Spacer(1,12),t]
    if section['refs']:
        story += [Spacer(1,12),p('Bu bölmənin mənbələri','SmallAZ')]
        story += [link(next(s for s in data['sources'] if s['id']==sid)) for sid in section['refs']]
    story += [PageBreak()]
story += [p('Mənbə reyestri','HeadingAZ'),p('4 noyabr 2026 tarixində yoxlanmış mənbələr. Konkret faktların mənbəsi ilə məhsul üçün təklif etdiyimiz qaydalar sənəddə ayrılıb.','LeadAZ')]
for s in data['sources']:
    story.append(KeepTogether([p(s['id']+' - '+s['name']),p(s['use'],'SmallAZ'),Paragraph(f'<link href="{escape(s["url"])}" color="{teal.hexval()}">{escape(s["url"])}</link>',styles['SmallAZ']),Spacer(1,8)]))
story += [Spacer(1,16),p('İlkin sənəd: qidalanma-app-etrafli-plan.pdf, 3 noyabr 2026. Bu yeni sənəd istifadəçi axını, kalori, foto və menyu bölmələrini konkret mənbə və icra addımları ilə dəqiqləşdirir.','SmallAZ')]
def footer(c,doc):
    w,h=A4
    c.setFillColor(muted); c.setFont('Arial',8)
    c.drawString(52,h-30,'QİDA / ELMİ ƏSAS VƏ İCRA PLANI')
    c.drawString(52,29,'Versiya 1.7 / 04.11.2026')
    c.drawRightString(w-52,29,f'{doc.page:02d}')
out=ROOT/'qidalanma-elmi-esas-ve-icra-plani.pdf'
doc=SimpleDocTemplate(str(out),pagesize=A4,rightMargin=52,leftMargin=52,topMargin=57,bottomMargin=50,title='Qida - Elmi əsas və real icra planı',author='Qida məhsul planı')
doc.build(story,onFirstPage=footer,onLaterPages=footer)
print(out)
