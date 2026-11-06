"""Latest explicit Yes/No allergy and ready-to-Today design decisions."""
from components import card as C, note as N, field as F, options as O, row as R, panel as P, sheet as D


def allergy(answer=None, error=False):
    body=C('Allergiyan varmı?',O(['Bəli','Xeyr'],answer))
    if error:
        body+='<div class="error">⚠ Davam etmək üçün “Bəli” və ya “Xeyr” seç.</div>'
    if answer=='Bəli':
        body+='<div class="allergen-grid">'+''.join(R(t,'☑ Seçilib' if t=='Süd' else '□ Seç',a,t=='Süd') for t,a in [('Süd','art_milk'),('Yumurta','art_egg'),('Qoz-fındıq','art_nuts'),('Balıq','art_fish'),('Buğda','art_wheat')])+'</div>'
        body+=R('Digər allergiya əlavə et','Qoz-fındıq növünü ayrıca dəqiqləşdir.')+N('Bir neçəsini seçə bilərsən. Ən azı bir allergen və ya dəqiqləşdirilmiş əlavə allergiya tələb olunur. Buğda və qlüten məhdudiyyəti ayrıdır.')
    elif answer=='Xeyr':
        body+=R('Allergiyam yoxdur','Allergen seçimin yoxdur.',None,True)
    else:
        body+='<p>Bir cavab seç. “Bəli” seçsən, allergenlərin siyahısı açılacaq.</p>'
    body+=F('Yemədiklərin · könüllü','Məsələn: qırmızı ət, göbələk…')
    return body


def build():
    return [D(47,'allergiya-ilkin-sual','Allergiya · ilkin sual və cavablar',[
        P('47-A','Allergiya və seçimlər',allergy(),'Davam et','Əvvəlki addıma qayıt','art_milk',step=4),
        P('47-B','Allergiya və seçimlər',allergy(error=True),'Davam et','Əvvəlki addıma qayıt','art_milk',step=4),
        P('47-C','Allergiya və seçimlər',allergy('Bəli'),'Davam et','Əvvəlki addıma qayıt','art_milk',step=4),
        P('47-D','Allergiya və seçimlər',allergy('Xeyr'),'Davam et','Əvvəlki addıma qayıt','art_milk',step=4),
    ],legacy=['completion/31'],requirements=['18.3','18.8']),
    D(48,'plan-hazirdir-basla','Planın hazırdır → Başla → Bu gün',[
        P('48-A','Planın hazırdır',C('Seçimlərin nəzərə alınıb','Məqsəd və ölçülər<br>Aktivlik və sağlamlıq<br>Allergiya və seçimlər<br>Vaxt, büdcə və yemək sayı')+C('İlk gününə başlaya bilərsən','Günün menyusuna bax və yediklərini əlavə et. Seçimlərini profildən dəyişə bilərsən.')+N('Nümunə plan · fərdi qidalanma hesabı aparılmayıb.'),'Başla','Cavablarıma bax','art_healthy',step=5,hero=True),
        P('48-B','Bu gün',C('Salam! İlk gününə xoş gəldin','Hələ yemək qeyd etməmisən.')+R('İndi · Səhər yeməyi','Yumurta və tərəvəz · nümunə menyu','art_menu')+C('Günün yeməkləri','○ Səhər · Planlı<br>○ Nahar · Planlı<br>○ Şam · Planlı')+N('Nümunə plan · hədəf və enerji hesablanmayıb. Başla yeməyi yeyilmiş saymır.'),'Menyuya bax','Nə yediyini əlavə et',tab='Bu gün'),
    ],legacy=['completion/32'],requirements=['18.8','18.11'])]
