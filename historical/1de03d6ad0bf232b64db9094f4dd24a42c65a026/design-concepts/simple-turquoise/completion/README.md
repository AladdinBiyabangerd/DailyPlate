# 19–28 · tamamlayıcı PNG konseptləri

6 noyabr 2026. 10 dəst / 29 AZ ekran. İstifadəçinin bu chatdə sadaladığı doqquz boşluğun vizual təklifidir; tətbiq implementasiyası və ya istifadəçi dizayn təsdiqi deyil.

## Əsas götürülən versiyalar

- Məhsul davranışı: cari istifadəçi qərarı → `planning/research-data.json` və ondan yaradılan plan → T01–T19. Rasterdəki rəqəmlər qayda deyil.
- Ümumi vizual istiqamət: `00-reference.png` və `design-concepts/tokens.css`. Təsdiqli D02 porsiya və D03 menyu quruluşları öz sahələrində üstün qalır.
- Çatışmayan bu vəziyyətlər üçün 19–28 son təklifdir. Eyni vəziyyətə dair köhnə 03–14 panelini təkbaşına son axın saymayın. 07–11-in ziddiyyət yaratmayan əsas axınları saxlanır; 15–18 aktivlik/hədəfsiz davam ekranları dəyişmir.
- Yeni təkliflər istifadəçi tərəfindən təsdiqlənməyib. Köhnə PNG-lər tarixi istinad kimi qalır; hamısının yenidən çəkildiyi və ya native ekranların qəbul edildiyi iddia edilmir. Native D06 naviqasiya borcu ayrıca açıqdır.

## Vahid qaydalar

Alt menyu ardıcıllığı: **Bu gün / Menyu / Əlavə et / İrəliləyiş / Profil**. Aktiv bölmə rəng, fon və ✓ ilə göstərilir. Əvvəl yediklərim və foto Əlavə et-ə, saxlanmış reseptlər Menyuya, hədəf baxışı İrəliləyişə aiddir. Tapşırıq/formalarda alt menyu əvəzinə görünən geri dönüş saxlanır.

Tarix nümunəsi: **6 noyabr 2026, Cümə**; həftə **2–8 noyabr 2026**. Terminlər: faktiki yeyilən / qalan planlı hissə / günün və həftənin proqnozu; enerji vahidi kkal, kütlə q. Z/K/Y = zülal/karbohidrat/yağ. 20-də rəqəmlər gün/həftə toplamlarıdır, 100 q göstəricisi deyil.

Resept dəyərinin əsası hər dəfə porsiya/pay və məlumdursa bişmiş qram ilə yazılır. **100 q üçün** yalnız ölçülmüş bişmiş çıxım məlumdursa göstərilir. Mənbədə çatışmayan nutrient 0 olmur. 21/28-dəki 500 kkal, 300 q və makrolar arifmetik maket nümunəsidir, real reseptə aid DB hesabı deyil; şəkillər illüstrasiyadır. 20-dəki cəmlər də nümayiş üçün bir-birinə uyğun sintetik rəqəmlərdir, istifadəçi planı deyil.

## Fayllar və yoxlama

19 təlimat və geri dönüş; 20 açıq gün/həftə; 21 alternativ/porsiya/yeyilmiş hissə/boş vaxta köçürmə; 22 undo və cihaz konflikti; 23 foto əlaqəsi/ziddiyyət/porsiya; 24 alış-veriş; 25 hesab; 26 FAQ/dəstək; 27 14 günlük baxış/boş siyahılar; 28 vahid naviqasiya və nutrient əsası.

`screens.json` mətn və vəziyyət mənbəyidir; `render.py` tokenləri və mövcud illüstrasiyaları istifadə edib HTML/PNG yaradır. Yenidən render: `python3 design-concepts/simple-turquoise/completion/render.py`. Chrome tələb olunur. Şəbəkə, APK, tətbiq və hesab əməliyyatı edilmir. HTML-lər yalnız PNG mənbəyidir, işlək prototip deyil.

Bütün 10 yekun PNG və 00/10/14/D03 istinadları agent tərəfindən açılıb müqayisə edilir; dəqiq hash və müqayisə `docs/qa/visual-review-plan.json`-dadır. Token kontrastı: mətn/ağ 12,70; köməkçi mətn/fon 5,04; ağ/əsas düymə 4,71. Cihaz, TalkBack, native AZ/EN/RU × 100/150%, backend və klik davranışı yoxlanmayıb. Əvvəlki native qəbul borcu bununla bağlanmır.
