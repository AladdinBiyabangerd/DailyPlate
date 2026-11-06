"""Recomposed 00–18 design sheets with current product contracts."""
from components import card as C, note as N, field as F, options as O, row as R, stepper as S, panel as P, sheet as D, measurements as M, preference_body as B, week as W, nutrients as V, ingredient as I


from onboarding import allergy

def build():
    result=[]
    def add(n,slug,title,panels):
        result.append(D(n,slug,title,panels,legacy=[f'{n:02d}']))
    activity=R('Az hərəkətli','Adi günlük hərəkət','art_sedentary')+R('Yüngül fəal','İzahına bax və təsdiqlə','art_walking',True)+R('Fəal','İzahına bax və təsdiqlə','art_active')+R('Çox fəal','İzahına bax və təsdiqlə','art_active')+'<p>Əmin deyiləm → hədəfsiz gündəlik.</p>'
    goals='<div class="goal-grid">'+''.join(R(t,'Bir seçim et',a,t=='Çəkini saxlamaq') for t,a in [('Arıqlamaq','art_lose'),('Çəkini saxlamaq','art_maintain'),('Çəki almaq','art_gain'),('Sağlam yemək','art_healthy')])+'</div>'
    add(0,'esas-istiqamet','Əsas ekranlar və vahid komponentlər',[
      P('00-A','Salam!',R('Şam · planlı','Toyuq plovu · 1 porsiya','art_recipe')+C('Faktiki qəbul',V('900','50','100','33,3'))+N('Planlı yemək yalnız təsdiqlə gündəliyə yazılır. Gündəlik natamamdırsa dəqiq qalan enerji göstərilmir.'),'Nə yediyini əlavə et','Bugünkü menyunu gör',tab='Bu gün'),
      P('00-B','Nə yediyini necə əlavə edək?',R('Şəkil çək','Yeməyi və porsiyanı sonra təsdiqləyəcəksən','art_entry_camera')+R('Axtar','Adı ilə tap və miqdarı seç','art_entry_search')+R('Əvvəl yediklərim','Saxlanmış porsiyanı yenidən təsdiqlə','art_entry_previous'),'Yemək axtar','Bu günə qayıt',tab='Əlavə et'),
      P('00-C','Məqsədin nədir?',goals,'Davam et','Xoş gəldin ekranına qayıt',step=1),
    ])
    add(1,'baslangic','Başlanğıc və fərdi plan',[
      P('01-A','Xoş gəldin!',R('Yeməyini asan qeyd et','Təsdiqlədiyin yemək gündəlikdə saxlanır','art_log')+R('Menyuna bax','Seçimlərinə uyğun planı nəzərdən keçir','art_menu')+R('Dəyişiklikləri gör','Qeydlər və çəki ölçmələri','art_progress')+C('Dil','Azərbaycan dili · English · Русский'),'Başlayaq','Hesabım var','art_welcome',hero=True),
      P('01-B','Özünü tanıt',M(),'Davam et','Məqsədə qayıt','art_age',step=2),
      P('01-C','Aktivlik və sağlamlıq',activity,'Sağlamlıq suallarına keç','Seçimlərin izahına bax',step=3),
      P('01-D','Planına bax',C('Seçimlərin','Çəkini saxlamaq · gündə 3 yemək')+C('Təxmini enerji hədəfi','Hesab nəticəsi olduqda burada göstərilir; rəqəm uydurulmur.')+R('Nümunə yeməklər','Səhər · Nahar · Şam','art_menu')+N('Plan nəticəsi və gündəlik qəbul ayrı məlumatlardır.'),'Menyumu gör','Seçimləri dəyiş','art_saved',hero=True),
    ])
    add(2,'secimler-ve-hesab','Seçimlər və hesab',[
      P('02-A','Allergiya və seçimlər',allergy(),'Davam et','Sağlamlıq suallarına qayıt','art_milk',step=4),
      P('02-B','Vaxt və büdcə',B(),'Planımı hazırla','Allergiyalara qayıt','art_cooking',step=5),
      P('02-C','Planını saxla',R('Apple ilə davam et','Hesaba giriş','art_account')+R('Google ilə davam et','Hesaba giriş','art_account')+R('E-poçtla qeydiyyat','Yeni hesab yarat','art_account')+N('Başqa cihazda bərpa yalnız uğurlu sinxronizasiyadan sonra mümkündür.')+C('Məxfilik barədə sadə məlumat','Məlumatların saxlanması və silinməsi →'),'E-poçtla qeydiyyat','İndi yox','art_account',hero=True),
      P('02-D','Hesabına daxil ol',F('E-poçt','ad@example.com')+F('Şifrə','•••••••• · Göstər')+R('Şifrəni unutdum','Bərpa linki istə')+R('Yeni hesab yarat','Qeydiyyata keç'),'Daxil ol','Geri qayıt','art_account'),
    ])
    add(3,'yemek-qeydiyyati','Yeməyi tap və əlavə et',[
      P('03-A','Nə yemisən?',F('Yemək axtar','plov · ×')+R('Toyuq plovu','Resept versiyasını və porsiyanı seç','art_recipe')+R('Başqa plov','Hazırlanma vəziyyətini yoxla','art_recipe')+C('Tapmadın?','Özün əlavə et →'),'Nəticəni seç','Əlavə et bölməsinə qayıt',tab='Əlavə et'),
      P('03-B','Yeməyin şəklini çək',C('Foto sahəsi','Boşqabı çərçivəyə yerləşdir.<div class="camera-frame">'+R('Kamera önbaxışı','Canlı görüntü üçün yer','art_entry_camera')+'</div>')+O(['İşıq','Qalereyadan seç'])+N('Foto təxmini namizəd verir. Hələ gündəlik qeydi yaranmır.'),'Şəkil çək','Axtarışa keç','art_entry_camera'),
      P('03-C','Bu yeməkdir?',R('Toyuq plovu','Foto nəticəsi · sən təsdiqlə','art_recipe',True)+R('Başqa yeməkdir','Yenidən axtar')+C('Əlavə yağ və ya sous varmı?',O(['Yox','Var'],'Yox'))+N('Miqdar növbəti addımda seçilir. Reseptdə olan yağ ikinci dəfə əlavə edilmir.'),'Bəli, davam et','Fotoya qayıt'),
      P('03-D','Əlavə olundu!',R('Toyuq plovu · 1 porsiya','Saxlanmış faktiki qeyd','art_recipe')+C('Porsiya üçün',V())+N('Yalnız dayanıqlı yazı tamamlandıqdan sonra bu nəticə göstərilir.')+R('Düzəliş et','Məhz bu qeydi aç'),'Bu günə qayıt','Başqa yemək əlavə et','art_saved',hero=True),
    ])
    add(4,'menyu-ve-alisveris','Menyu, resept və alış-veriş',[
      P('04-A','Həftənin menyusu','<p>2–8 noyabr 2026</p>'+W()+R('Səhər · Planlı','Yumurta və pendir · enerji məlum deyil · 15 dəq','art_menu')+O(['Resept','Yedim'])+R('Nahar · Qismən yeyilib','Mərci şorbası · qalan ½ porsiya','art_recipe')+O(['Resept','Yedim'])+N('Planı dəyişmək yediyini qeyd etmir. Gündəlik qeydi yalnız porsiya təsdiqindən sonra yaranır.'),'Alış-veriş siyahısı','Başqa həftəyə bax',tab='Menyu'),
      P('04-B','Toyuq plovu',C('Reseptin son versiyası','1 porsiya üçün · vaxt məlum deyil<br>'+V())+C('Nə lazımdır?','Toyuq · xam 150 q<br>Düyü · quru 70 q<br>Kök · xam 62,5 q<br>Soğan 37,5 q · Yağ 7,5 q')+R('Necə bişirim?','Bu versiyanın yoxlanmış addımlarına bax')+O(['Resepti saxla','İnqrediyentləri dəyiş']),'Yedim — porsiyanı seç','Yeməyi dəyiş','art_recipe',hero=True),
      P('04-C','Nə ilə dəyişək?',R('Tərəvəzli toyuq','Porsiya üçün 400 kkal · 30 dəq · nümunə','art_menu',True)+R('Başqa yemək','Məlumatı və uyğunluğu yoxlanacaq','art_recipe')+N('Eyni enerji eyni makro demək deyil. Əvvəlcə gün/həftə yoxlanır.'),'Seçimə bax','Reseptə qayıt'),
      P('04-D','Alış-veriş siyahısı',C('2–8 noyabr 2026','Həftəyə lazım olan ümumi miqdar')+R('Tərəvəzlər','Kök · 250 q · □ Evdə var · □ Alınıb','art_ingredient_carrot')+R('Taxıl','Düyü · quru 700 q · □ Evdə var · ✓ Alınıb','art_ingredient_rice')+R('Ət','Toyuq · xam 600 q · ✓ Evdə var · □ Alınıb','art_ingredient_chicken')+N('Evdə var işarəsi həftəlik ümumi miqdarı azaltmır.'),'Paylaşım önbaxışına bax','Menyuya qayıt'),
    ])
    add(5,'irelileyis-ve-profil','İrəliləyiş və şəxsi seçimlər',[
      P('05-A','Çəkini qeyd et',F('Ölçmə tarixi','6 noyabr 2026')+S('Çəki','78,4','kq')+C('Ölçmə qeydi','Mümkünsə eyni şəraitdə ölçül. Bu gün üçün əvvəlki ölçmə varsa ayrıca seçim göstərilir.'),'Çəkini yadda saxla','Əvvəlki qeydlərim','art_weight',hero=True),
      P('05-B','Həftəyə baxaq',C('2–8 noyabr 2026','6 gündə yemək qeydi · 3 çəki ölçməsi')+C('Çəki ölçmələri','2 noyabr · 79,0 kq<br>4 noyabr · 78,7 kq<br>6 noyabr · 78,4 kq')+C('Qeyd davamlılığı',O(['B.e. ✓','Ç.a. ✓','Ç. ✓','C.a. ✓','C. ✓','Ş. ✓','B. ○']))+N('Qeyd olmayan gün 0 qəbul deyil. Çəki azalması hər məqsəd üçün uğur sayılmır.'),'Çəki əlavə et','Əvvəlki həftə',tab='İrəliləyiş',art='art_progress'),
      P('05-C','Profilin',R('Profili redaktə et','Məqsədim · ölçülər · aktivlik','art_account')+R('Yemək seçimlərim','Allergiya, istəksizlik, vaxt və büdcə','art_healthy')+R('Xatırlatmalar','Saatlar və bildiriş icazəsi')+R('Dil','Azərbaycan dili')+R('Abunəliyim','Mağaza və paket məlumatı')+R('Kömək və hesab','FAQ · dəstək · məxfilik · ixrac · silmə'),'Seçimlərimə bax','Bu günə qayıt',tab='Profil'),
      P('05-D','Nə vaxt xatırladaq?',R('Səhər','✓ Açıq · 09:00','art_menu')+R('Nahar','✓ Açıq · 13:00','art_recipe')+R('Şam','✓ Açıq · 19:00','art_recipe')+R('Çəki ölçməsi','○ Bağlı · günlük / həftəlik','art_weight')+C('Sakit saatlar','22:00–08:00 · Asia/Baku')+N('Bildirişlər könüllüdür. İcazə olmasa tətbiqdən istifadə davam edir.'),'Yadda saxla','Profilə qayıt'),
    ])
    add(6,'abunelik-ve-veziyyetler','Abunəlik və ümumi hallar',[
      P('06-A','Abunəliyə bax',R('Həftəlik şəxsi menyu','Yalnız paketdə həqiqətən varsa göstərilir','art_menu')+R('Dəyişmə və alış-veriş','İmkanlar mağaza məhsuluna bağlıdır','art_budget')+O(['Aylıq','İllik'],'Aylıq')+N('Qiymət, valyuta, dövr və yenilənmə şərti mağazadan gəlir. Yüklənməyibsə alış bağlıdır.')+R('Alışı bərpa et','Mağaza yoxlaması açılır'),'Abunə ol','Pulsuz davam et','art_menu',disabled=True),
      P('06-B','Hələ yemək əlavə etməmisən',C('Günün burada görünəcək','İlk yeməyini təsdiqlə və gündəlikdə saxla. Planın olması faktiki qəbul deyil.'),'Yemək əlavə et','Bugünkü menyunu gör','art_empty',tab='Bu gün',hero=True),
      P('06-C','İnternet yoxdur',R('Toyuq plovu','Telefonda saxlanıb · sinxronizasiya gözləyir','art_recipe')+N('Bu mesaj yalnız lokal yazı uğurludursa göstərilir. Təkrar cəhd ikinci qeyd yaratmır.'),'Davam et','Yenidən yoxla','art_log',hero=True),
      P('06-D','Şəkli tanıya bilmədik',R('Yenidən şəkil çək','Daha aydın və işıqlı foto','art_entry_camera')+R('Yeməyin adını axtar','Axtarışla davam et','art_entry_search')+R('Özüm yazım','Əl ilə yemək daxil et','art_log'),'Yenidən şəkil çək','Axtarışa keç','art_entry_camera',hero=True),
    ])
    ingredients=C('1 porsiya üçün','Toyuq · xam 150 q · − / + · Çıxar<br>Düyü · quru 70 q · − / + · Çıxar<br>Kök · xam 62,5 q · − / + · Çıxar<br>Soğan 37,5 q · Yağ 12,5 q')
    add(7,'inqrediyent-redaktesi','Resepti özünə uyğunlaşdır',[
      P('07-A','Toyuq plovu',C('Menyudakı resept','Son saxlanmış versiya · 1 porsiya')+ingredients+R('İnqrediyentləri dəyiş','Əlavə et, çıxar, miqdarı dəyiş')+R('Necə bişirim?','Yoxlanmış addımlara bax'),'Hazırladım — şəkil çək','Yeməyi dəyiş','art_recipe',hero=True),
      P('07-B','İnqrediyentləri dəyiş',S('Neçə porsiya hazırlayırsan?','1')+''.join(I(n,a,st) for n,a,st in [('Toyuq','150','xam'),('Düyü','70','quru'),('Kök','62,5','xam'),('Soğan','37,5','xam'),('Yağ','12,5','mənbə qeydi')])+R('İnqrediyent əlavə et','Mənbəli məhsul axtar')+C('Digər ərzaqlar · açıq','Su · miqdar ayrıca<br>Duz 1 q · Sarıkök 0,5 q<br>Duz hesabdan çıxarılmır.'),'Dəyişikliyə bax','Reseptə qayıt','art_cooking'),
      P('07-C','Nə əlavə edək?',F('Məhsul axtar','göbələk')+R('Ağ şampinyon · xam','Mənbə qeydi və hazırlanma vəziyyətini yoxla','art_healthy',True)+F('Miqdar','50',unit='q')+R('Digər nəticələr','İstiridyə göbələyi · ayrıca mənbə qeydi'),'50 q əlavə et','Redaktəyə qayıt'),
      P('07-D','Dəyişikliklərə bax',C('1 porsiya üçün','Yağ 7,5 → 12,5 q · Göbələk +50 q')+C('Əvvəl → sonra','500 → 555 kkal · inqrediyent hesabı nümunəsi')+R('Yenilənmiş addımlar','Yoxlanmış uyğun təlimata bax')+C('Alış-veriş fərqi','4 porsiyalı qazan: yağ +20 q · göbələk +200 q')+N('Bişmiş çıxım və köhnə porsiya fotosu avtomatik miras qalmır.'),'Gün və həftə önbaxışına bax','Redaktəyə qayıt','art_recipe'),
    ])
    add(8,'yemek-ve-gun-deyisme','Həftəni qoruyaraq yeməyi dəyiş',[
      P('08-A','Nəyi dəyişmək istəyirsən?',R('6 noyabr · Şam','Toyuq plovu · planlı','art_recipe')+R('İnqrediyentləri dəyiş','Əlavə et, çıxar, miqdarı dəyiş')+R('Başqa yemək seç','Həmin günə uyğun namizədlər')+R('Başqa günlə dəyiş','İki yeməyin qarşılıqlı yeri dəyişir')+R('Bu günə köçür','Yalnız boş yemək vaxtına'),'Başqa yemək seç','Menyuya qayıt'),
      P('08-B','Bu günə uyğun yeməklər',R('Tərəvəzli toyuq','Yeni porsiya və bütün gün yoxlanır','art_menu',True)+R('Limonlu balıq','Allergen, vaxt və mənbə ayrıca yoxlanır','art_fish')+R('Toyuq və qarabaşaq','Porsiyanı seç','art_recipe')+N('Uyğunluq nişanı yalnız real yoxlama nəticəsi ilə görünür.'),'Seçimə bax','Başqa dəyişmə yolu seç'),
      P('08-C','Hansı günlə dəyişək?',R('6 noyabr · Şam','Toyuq plovu · seçilmiş yemək','art_recipe')+R('7 noyabr · Şam','Tərəvəzli toyuq · ✓ seçilib','art_menu',True)+R('8 noyabr · Şam','Başqa planlı yemək','art_recipe')+N('Artıq yeyilmiş və keçmiş hissə seçilmir. Boş vaxt üçün ayrıca köçürmə yolu var.'),'İki günü yoxla','Geri qayıt'),
      P('08-D','İki yeməyin yeri dəyişəcək',C('6 noyabr · Şam','Toyuq plovu → Tərəvəzli toyuq')+C('7 noyabr · Şam','Tərəvəzli toyuq → Toyuq plovu')+C('Hər iki günün nəticəsi','Enerji, makro, məhdudiyyət və alış-veriş fərqi açılır.')+N('İki hissə bir əməliyyatla saxlanır. Digər günlər və faktiki qeydlər dəyişmir.'),'İki günün önbaxışına bax','Seçimə qayıt','art_menu'),
    ])
    add(9,'foto-sonrasi-resept','Hazırladığın yeməyi düzgün qeyd et',[
      P('09-A','Reseptdə nəyisə dəyişmisən?',R('Toyuq plovu','Sənə verilmiş son resept versiyası','art_recipe')+R('Xeyr, olduğu kimi hazırladım','Həmin versiya ilə porsiya seç')+R('Bəli, dəyişdim','Faktiki tərkibi düzəlt')+N('Bütün qazanı deyil, yalnız yediyin hissəni təsdiqləyəcəksən.'),'Xeyr — porsiyanı seç','Bəli — tərkibi düzəlt'),
      P('09-B','Hazırladığın yemək',C('Bütün qazan üçün · 4 porsiya','Faktiki tərkibi düzəlt')+''.join(I(n,a,st) for n,a,st in [('Toyuq','600','xam'),('Düyü','280','quru'),('Kök','250','xam'),('Soğan','150','xam'),('Yağ','50','mənbə qeydi'),('Göbələk','200','xam')])+R('Miqdarı dəyiş / çıxar','Məhsulun xam və ya bişmiş vəziyyəti saxlanır')+R('İnqrediyent əlavə et','Mənbəli məhsul seç')+C('Digər ərzaqlar · açıq','Su · Duz · Sarıkök')+N('Burada planın günlərini və başqa yeməyi dəyişmək yoxdur.'),'Nə qədər yediyini seç','Suala qayıt','art_cooking'),
      P('09-C','Nə qədər yedin?',S('Neçə porsiya hazırladın?','4')+C('Neçə porsiya yedin?',O(['Yarım','1 porsiya','2 porsiya'],'1 porsiya'))+C('Qazanın ¼ hissəsi','Yalnız bu hissə gündəliyə yazılacaq.')+R('Dəqiq miqdar daxil et','Qram hesabı üçün ölçülmüş bişmiş çəki lazımdır')+N('İllüstrasiya ölçü nümunəsi deyil.'),'Önbaxışa bax','Tərkibə qayıt','art_cooking',hero=True),
      P('09-D','Gündəliyə əlavə edək?',R('Toyuq plovu · dəyişdirilmiş','1 porsiya · qazanın ¼ hissəsi','art_recipe')+C('Təxmini enerji','555 kkal · təsdiqlədiyin inqrediyentlərdən')+R('Tərkibə yenidən bax','Son faktiki versiyanı aç')+N('Gələcək plan və əsas kitabxana dəyişmir. Hələ gündəlik qeydi yaranmayıb.'),'Gündəliyə əlavə et','Porsiyaya qayıt'),
    ])
    add(10,'deyisiklik-ve-xeta-hallari','Dəyişiklik aydın və geri qaytarılan olsun',[
      P('10-A','Bu dəyişmə uyğun gəlmir',C('7 noyabr · Şam','Seçilmiş porsiya günün şərtlərinə uyğun gəlmədi.')+N('Heç bir yemək dəyişməyib. Hər iki gün birlikdə keçməlidir.'),'Başqa gün seç','Porsiyalara bax','art_menu',hero=True),
      P('10-B','Məlumat çatmır',R('Evdə hazırlanmış sous','30 q · enerji məlum deyil','art_healthy')+C('Hesab natamamdır','Mənbədə olmayan dəyər 0 sayılmır; uyğunluq təsdiqlənmir.')+R('Etiketdəki məlumatı yaz','Mənbəni dəqiqləşdir')+R('Bazadan uyğun məhsul tap','Tərkib və vəziyyəti yoxla'),'Məhsulu dəqiqləşdir','Redaktəyə qayıt','art_portion_scale'),
      P('10-C','Hazırlanma qaydası da dəyişir',C('Kök çıxarıldı · göbələk əlavə edildi','Köhnə addımlar yeni tərkibə olduğu kimi tətbiq olunmur.')+C('Yenilənmiş addımlar','Bu versiyaya uyğun yoxlanmış təlimat açılır. Uyğun təlimat yoxdursa ayrıca xəta ekranına keçilir.')+N('Köhnə porsiya fotosu və bişmiş çıxım yeni variant üçün dəqiq ölçü deyil.'),'Uyğun addımlara bax','Tərkibə qayıt','art_cooking',hero=True),
      P('10-D','Saxlamadan çıxırsan?',C('Qaralama dəyişiklikləri','Plan və alış-veriş hələ dəyişməyib.')+R('Dəyişiklikləri ləğv et','Saxlanmamış düzəlişlər atılır')+R('Qaralama kimi saxla','Kontekst və baza versiyası qorunur')+N('Qaralama təsdiqlənmiş plan və gündəlik deyil.'),'Redaktəyə davam et','Dəyişiklikləri ləğv et','art_write_error'),
    ])
    add(11,'yenilenmis-plan-ve-tesdiq','Nəticə, alış-veriş və gündəlik',[
      P('11-A','Həftənin menyusu',W()+R('6 noyabr · Şam','Tərəvəzli toyuq · 7 noyabrdan dəyişdirildi','art_menu')+R('7 noyabr · Şam','Toyuq plovu · 6 noyabrdan dəyişdirildi','art_recipe')+C('Son dəyişikliyi geri qaytar','Plan və alış-veriş birlikdə əvvəlki vəziyyətə dönür.')+N('Digər günlər və faktiki yeyilmiş qeydlər saxlanıb.'),'Son dəyişikliyi geri qaytar','Menyuya qayıt',tab='Menyu'),
      P('11-B','Alış-veriş yeniləndi',C('4 porsiyalı qazan · fərq','Göbələk +200 q · Yağ +20 q')+R('Düyü · Alınıb','Əvvəlki işarə qorunub','art_ingredient_rice')+R('Toyuq · Evdə var','Ümumi ehtiyacdan avtomatik çıxılmır','art_ingredient_chicken')+N('Artıq alınmış, yeni planda lazım olmayan məhsul ayrıca saxlanır.'),'Siyahıya qayıt','Dəyişikliyə bax','art_budget'),
      P('11-C','Bu yeməklə əvəz edək?',R('Əvvəl · Toyuq plovu','1 porsiya · 500 kkal · nümunə','art_recipe')+R('Sonra · Tərəvəzli toyuq','Yeni porsiya · 400 kkal · nümunə','art_menu')+R('Günün yenilənmiş planı','Faktiki + qalan planlı hissələr')+N('Digər yeməklərin porsiyası ayrıca qəbul edilmədən dəyişmir.'),'Gün və həftə önbaxışına bax','Başqa variant seç'),
      P('11-D','Qeyd olundu!',R('Toyuq plovu · 1 porsiya','Sənin dəyişdirdiyin faktiki tərkib','art_recipe')+C('Təxmini enerji','555 kkal · nümunə')+N('Gələcək günlərin menyusu dəyişməyib. Bu, planı dəyişmə əməliyyatı deyil.'),'Bu günə qayıt','Qeydə düzəliş et','art_saved',hero=True),
    ])
    health=['Hamiləlik','Südvermə','Qidalanma pozuntusu riski','Tibbi pəhriz ehtiyacı','Böyrək xəstəliyi','Qidalanmaya təsir edən dərman','Diabet','Səbəbsiz çəki itkisi','Yüksək performans idmanı','BMI-yə təsir edən bədən tərkibi']
    healthbody='<div class="health-list">'+''.join(f'<div class="health-row"><b>{x}</b><span>○ Bəli &nbsp; ○ Xeyr</span></div>' for x in health)+'</div>'
    add(12,'elave-profil-secimleri','Profil məlumatları · cari müqavilə',[
      P('12-A','Sağlamlıq sualları',healthbody+N('Cavabsız “Xeyr” deyil. Risk cavabında avtomatik fərdi plan verilmir.'),'Davam et','Cavab vermədən hədəfsiz davam et','art_health',step=3,disabled=True),
      P('12-B','Büdcə məlumatları',F('Region','Azərbaycan')+F('Valyuta','AZN')+F('Həftəlik büdcə','100',unit='AZN')+S('Neçə nəfər üçün?','1')+N('Yemək əhatəsi və mətbəx avadanlığı onboarding-də soruşulmur.'),'Davam et','Seçimlərə qayıt','art_budget',step=5),
      P('12-C','Evdə var işarəsi',R('Düyü · quru','700 q həftəlik ehtiyac · ✓ Evdə var','art_ingredient_rice',True)+R('Toyuq · xam','600 q həftəlik ehtiyac · □ Evdə var','art_ingredient_chicken')+N('Evdəki dəqiq miqdar toplanmır və ehtiyacdan çıxılmır. Köhnə inventar forması ləğv edilib.'),'Alış-verişə qayıt','Menyuya qayıt','art_budget'),
      P('12-D','Profil saxlanıb',C('Bu telefonda saxlanır','Məqsəd və seçimlər son versiya ilə saxlanıb. Menyu ayrıca uyğunluq yoxlamasından sonra yaranır.')+R('Məqsədim','Çəkini saxlamaq')+R('Yemək sayı','Gündə 3 yemək'),'Menyuya bax','Seçimləri dəyiş','art_saved',hero=True),
    ])
    add(13,'bos-veziyyetler','Əsas bölmələrin boş halları',[
      P('13-A','Hələ menyu yoxdur',C('Plan hazır deyil','Seçimlərini tamamla və mövcud planın yaradılma vəziyyətinə bax. Gündəlikdən istifadə edə bilərsən.'),'Seçimlərimə bax','Yemək əlavə et','art_empty_menu',tab='Menyu',hero=True),
      P('13-B','İlk yeməyini əlavə et',R('Şəkil çək','Foto və porsiyanı təsdiqlə','art_entry_camera')+R('Axtar','Yeməyi adı ilə tap','art_entry_search')+R('Özüm yazım','Əl ilə daxil et','art_log'),'Yemək axtar','Bu günə qayıt','art_empty_add',tab='Əlavə et',hero=True),
      P('13-C','Hələ irəliləyiş qeydi yoxdur',C('İlk ölçmədən başla','Bir ölçmə trend sayılmır. Yemək qeydi olmayan gün 0 qəbul kimi hesablanmır.'),'Çəki əlavə et','Bu günə qayıt','art_empty_progress',tab='İrəliləyiş',hero=True),
      P('13-D','Hesaba giriş əlçatan deyil',C('Hazırda qoşulmaq alınmır','Lokal məlumatların qalır. Sonra yenidən cəhd edə bilərsən.'),'Yenidən yoxla','Hesabsız davam et','art_account',hero=True),
    ])
    add(14,'elave-formalar-ve-veziyyetler','Əlavə seçimlər və formalar',[
      P('14-A','Digər seçimlər',F('Əlavə allergen','Küncüt')+O(['Qlüten məhdudiyyəti'])+F('Qida məhdudiyyəti','Daxil et')+N('Buğda allergiyası və qlüten məhdudiyyəti ayrı saxlanır. Naməlum allergen uyğun sayılmır.'),'Seçimi saxla','Allergiyalara qayıt','art_other_choices',step=4),
      P('14-B','Sevmədiyim yeməklər',F('Yemədiyin ərzaqlar','Göbələk · ×')+F('Yemədiyin yeməklər','Daxil et')+N('İstəksizlik allergiya deyil. Silmək və dəyişmək mümkündür.'),'Seçimi saxla','Allergiyalara qayıt','art_dislikes'),
      P('14-C','Yüklənir…',C('Seçimlərin yoxlanır','<div class="loader"></div>Plan nəticəsi hazır olduqda göstəriləcək. Hələ heç nə təsdiqlənməyib.'),'Yoxlanır…','Geri qayıt','art_cooking',disabled=True,hero=True),
      P('14-D','Telefona yazmaq alınmadı',C('Cavabların ekranda qalıb','Hələ saxlanıb demirik. Yeri və tətbiq vəziyyətini yoxlayıb yenidən cəhd et.'),'Yenidən yoxla','Redaktəyə qayıt','art_write_error',hero=True),
    ])
    add(15,'aktivlik-ve-hedefsiz-davam','Aktivlik və hədəfsiz davam',[
      P('15-A','Günün necə keçir?',activity,'Seçimi təsdiqlə','Əmin deyiləm','art_active',step=3),
      P('15-B','Yüngül fəal',C('Gündəlik fəaliyyət nümunəsi','Adi günlük işlərə əlavə 60–80 dəq, 5–7 km/saat sürətlə gəzinti.')+N('Bu məşq resepti deyil; adi gününü müqayisə etmək üçün mənbə nümunəsidir.'),'Bu seçim mənə uyğundur','Digər seçimlərə bax','art_walking',hero=True),
      P('15-C','Seçimini yenidən yoxla',C('Əvvəlki cavab','Bir az hərəkət edirəm')+N('Dörd kateqoriya yenilənib. Köhnə cavab avtomatik xəritələnmir; yenidən təsdiq et.'),'Seçimlərə bax','Hədəfsiz davam et','art_other_choices',hero=True),
      P('15-D','Hədəfsiz davam edə bilərsən',C('Aktivlik dəqiqləşməyib','Avtomatik enerji hədəfi yaradılmır.')+R('Yeməklərini qeyd et','Porsiyanı özün təsdiqlə','art_log')+N('Sonra profildən aktivliyi yenidən seçə bilərsən.'),'Gündəliyə keç','Aktivliyi yenidən seç','art_empty_add',hero=True),
    ])
    examples=[('Az hərəkətli','Adi günlük işlər, təxminən 30 dəq gəzinti və 90 dəq yüngül/orta ev işi.','art_sedentary'),('Fəal','Adi günlük işlərə əlavə: 30–50 dəq, 5–7 km/saat gəzinti; 45 dəq orta velosiped; 40 dəq cüt tennis.','art_active'),('Çox fəal','Adi günlük işlərə əlavə: 45 dəq orta velosiped; 25 dəq, 10 km/saat qaçış; 60 dəq cüt tennis.','art_active')]
    add(16,'aktivlik-izahlari','Aktivlik seçimlərinin izahı',[P(f'16-{i}',t,C('Gündəlik fəaliyyət nümunəsi',txt)+N('Məşq tövsiyəsi və dəqiq ekvivalentlik testi deyil. Gününə ən yaxın təsviri təsdiqlə.')+R('Əmin deyiləm','Hədəfsiz gündəliklə davam et'),'Bu seçim mənə uyğundur','Digər seçimlərə bax',art,hero=True) for i,(t,txt,art) in enumerate(examples,1)])
    reasons=[('Məlumatı verməyə bilərsən','Cins məlumatı verilməyib. Formula üçün fərz etmirik. Avtomatik hədəf yaradılmır.','Məlumatı tamamla','art_account'),('Fərdi dəstək daha uyğundur','Sağlamlıq cavablarına görə avtomatik enerji hədəfi və fərdi menyu verilmir. Bu diaqnoz deyil. Mütəxəssis hədəfi sərt istisnanı aşmır.','Cavabları nəzərdən keçir','art_health'),('Hesablamanın əhatəsi məhduddur','Profil məhsulun avtomatik hesab əhatəsinə daxil deyil. Bu modelin elmi sərhədi və ya diaqnoz deyil.','Profilə bax','art_account')]
    add(17,'hedefsiz-davam-sebebleri','Hədəfsiz davamın səbəbləri',[P(f'17-{i}',t,C('Avtomatik hədəf verilmir',txt)+N('Yeməklərini hədəfsiz qeyd edə bilərsən.'),'Gündəliyə keç',back,art,hero=True) for i,(t,txt,back,art) in enumerate(reasons,1)])
    add(18,'hedefsiz-hesablama-sebebleri','Hesablama nəticəsi uyğun deyil',[
      P('18-A','Avtomatik hədəf verilmir',C('Enerji məhsul sərhədindən aşağıdır','Hesablanan nəticə bu tətbiqin enerji alt sərhədinə uyğun gəlmir.')+N('Universal tibbi minimum və ya təhlükəsizlik zəmanəti deyil. Hədəfi süni artırmırıq; uyğun plan yaradılmır.')+R('Mütəxəssislə dəqiqləşdir','Fərdi dəstək al'),'Gündəliyə keç','Məlumatları yoxla','art_progress',hero=True),
      P('18-B','Uyğun bölgü alınmadı',C('Makro bölgüsü uyğun gəlmir','Zülal, yağ və karbohidrat seçilmiş qaydalara uyğun bölünmədi.')+N('Rəqəmlər səssiz dəyişmir və zülal minimumu azaldılmır. Avtomatik enerji/makro hədəfi yaradılmır.'),'Gündəliyə keç','Məlumatları yoxla','art_healthy',hero=True),
    ])
    return result
