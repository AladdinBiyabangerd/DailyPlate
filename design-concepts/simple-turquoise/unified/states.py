"""Close the requested design-state gaps with explicit recovery paths."""
from components import card as C, note as N, field as F, options as O, row as R, stepper as S, panel as P, sheet as D, measurements as M, preference_body as B, nutrients as V


from onboarding import allergy

def build():
    data=[]
    def add(n,slug,title,ps,requirements=None):
        data.append(D(n,slug,title,ps,requirements=requirements))
    add(31,'onboarding-tamamlanma','Onboarding · ardıcıllıq və yekun seçimlər',[
      P('31-A','Seçimlərin tamamdır',C('Beş addım tamamlandı','✓ Məqsəd<br>✓ Ölçülər və vahidlər<br>✓ Aktivlik və sağlamlıq<br>✓ Allergiya və seçimlər<br>✓ Vaxt və büdcə')+C('Seçim xülasəsi','Çəkini saxlamaq · yüngül fəal<br>Allergiyam yoxdur · 30 dəq · 3 yemək<br>Azərbaycan · AZN · 100 AZN/həftə · 1 nəfər')+N('Əvvəl uyğunluq yoxlanır. Bu, tibbi məsləhət deyil.'),'Planımı hazırla','Cavabları dəyiş','art_saved',step=5),
      P('31-B','Vahidi dəqiqləşdir',M({1:'1,72 sm yazmısan. 1,72 metr nəzərdə tutursan?'},['28','1,72','78,4'])+O(['Bəli, 172 sm kimi saxla','Xeyr, özüm düzəldim'])+N('Vahid açıq təsdiq olmadan çevrilmir.'),'Vahidi təsdiqlə','Özüm düzəldim','art_height',step=2,disabled=True),
      P('31-C','Vaxt və büdcə',B().replace('>100<','>Daxil et<')+N('Həftəlik büdcə məbləğini tamamla. Digər cavabların saxlanır.'),'Planımı hazırla','Allergiyalara qayıt','art_budget',step=5,disabled=True),
    ],['17.4','18.2','18.8','26.8'])
    add(32,'olcu-giris-serhedleri','Onboarding · boş və yanlış ölçülər',[
      P('32-A','Ölçüləri tamamla',M({0:'Yaşını rəqəmlə daxil et.',1:'Boy sıfırdan böyük olmalıdır.',2:'Çəki sıfırdan böyük olmalıdır.'},['—','0','−5']),'Davam et','Məqsədə qayıt','art_age',step=2,disabled=True),
      P('32-B','Boyu düzəlt',M({1:'Düzgün, sonlu boy rəqəmi daxil et. Məsələn: 172.'},['28','∞','78,4']),'Davam et','Məqsədə qayıt','art_height',step=2,disabled=True),
      P('32-C','Girişi yoxla',M({2:'Bu rəqəm qəbul edilmir. Ölçünü və vahidi yoxla.'},['28','172','1e999']),'Davam et','Məqsədə qayıt','art_weight',step=2,disabled=True),
    ],['19.2','26.8'])
    add(33,'allergiya-ve-budce-xetalari','Onboarding · allergiya və büdcə xətaları',[
      P('33-A','Allergiya seçimini tamamla',allergy(error=True),'Davam et','Sağlamlıq suallarına qayıt','art_milk',step=4,disabled=True),
      P('33-B','Çoxlu allergiya',C('Allergiyan varmı?',O(['Bəli','Xeyr'],'Bəli'))+O(['Süd','Yumurta'],'Süd')+R('Küncüt','✓ Əlavə allergen',None,True)+F('Qoz-fındıq növü','Badam')+N('Yeni allergen seçiləndə “Allergiyam yoxdur” ləğv olunur. Tanınmayan ad dəqiqləşmədən uyğunluq təsdiqlənmir.'),'Seçimləri saxla','Allergiyalara qayıt','art_nuts',step=4),
      P('33-C','Büdcəni düzəlt',F('Region','Azərbaycan')+F('Valyuta','AZN')+F('Həftəlik büdcə','−10','Məbləğ sıfırdan böyük, sonlu rəqəm olmalıdır.','AZN')+F('Neçə nəfər üçün?','0','Ən azı 1 nəfər daxil et.')+N('Digər vaxt, məqsəd və allergiya cavabların qalır.'),'Planımı hazırla','Büdcəyə qayıt','art_budget',step=5,disabled=True),
    ],['17.4','18.3','18.8'])
    add(34,'resept-redaktoru-xetalar','Resept redaktoru · boş, yanlış və sıfır miqdar',[
      P('34-A','İnqrediyent qalmayıb',C('Boş resept saxlanmır','Ən azı bir inqrediyent əlavə et.')+R('Son silinməni geri qaytar','Silinmiş inqrediyenti bərpa et'),'İnqrediyent əlavə et','Son silinməni geri qaytar','art_empty_menu',hero=True),
      P('34-B','Miqdarları düzəlt',F('Toyuq · xam · miqdar','—','Miqdarı daxil et.','q')+F('Düyü · quru · miqdar','−20','Miqdar mənfi ola bilməz.','q')+N('Boş və mənfi miqdar düzələnədək önbaxış bağlıdır.'),'Dəyişikliyə bax','Redaktəyə qayıt','art_ingredient_rice',disabled=True),
      P('34-C','Kökü reseptdən çıxaraq?',R('Kök · xam','0 q daxil etmisən','art_ingredient_carrot')+N('Təsdiqləsən kök reseptdən, hesabdan və uyğun təlimatdan çıxarılacaq. Hələ dəyişiklik saxlanmayıb.'),'Bəli, kökü çıxar','Miqdara qayıt'),
    ],['16.1','16.2','T01'])
    add(35,'resept-allergen-ve-vahid','Resept redaktoru · allergen və ölçü təhlükəsizliyi',[
      P('35-A','Allergen ziddiyyəti var',R('Fıstıq · 20 q','Profilindəki allergiya ilə ziddiyyət','art_nuts')+N('Bu resept plana tətbiq edilə bilməz. Məhsulu çıxar və ya başqa məhsul seç.')+R('Faktiki yediyini qeyd etmək','Ayrı gündəlik axınıdır; plan tövsiyəsi deyil'),'Fıstığı çıxar','Başqa məhsul seç'),
      P('35-B','Allergen məlumatı yoxdur',C('Hazır sous · 30 q','Tərkibi məlum deyil; təhlükəsiz uyğunluq təsdiqlənmir.')+N('Məlumatın olmaması allergenin olmaması deyil.'),'Başqa məhsul seç','Sousu çıxar','art_healthy',hero=True),
      P('35-C','Miqdarı yoxla',F('Yağ · miqdar','NaN','Müsbət və sonlu rəqəm daxil et.','q')+F('Göbələk · miqdar','∞','Sonsuz miqdar qəbul edilmir.','q')+F('Düyü · miqdar','1e999','Bu rəqəm qəbul edilmir; miqdarı yoxla.','q'),'Dəyişikliyə bax','Redaktəyə qayıt',disabled=True),
      P('35-D','Bu vahid çevrilmir',F('Yağ · miqdar','1',unit='qaşıq')+N('Bu məhsul üçün qaşıqdan qrama təsdiqli çevirmə yoxdur. Universal qaşıq çəkisi tətbiq edilmir.')+F('Qramla miqdar','Daxil et',unit='q'),'Qramla davam et','Məhsula qayıt','art_portion_scale',disabled=True),
    ],['16.1','16.9','T08','T11'])
    def day(date,before,after,actual):
        return C(date,f'Faktiki qəbul: {actual} kkal · dəyişmir')+C('Qalan planlı hissə',f'{before} → {after} kkal')
    add(36,'iki-gun-ve-kocurme-onbaxis','İki günün açılmış önbaxışı',[
      P('36-A','Yer dəyişmə · iki gün',day('6 noyabr',500,400,900)+C('6 noyabr · proqnoz','Enerji 1 400 → 1 300 kkal<br>Zülal 80 → 74 q · Karbohidrat 150 → 140 q · Yağ 53,3 → 49,3 q')+day('7 noyabr',400,500,800)+C('7 noyabr · proqnoz','Enerji 1 200 → 1 300 kkal<br>Zülal 74 → 80 q · Karbohidrat 130 → 140 q · Yağ 42,7 → 46,7 q')+N('Nümunə cəmlərdir. Həftənin toplamı dəyişmir; hər iki gün ayrıca yoxlanır.'),'İki yeməyin yerini dəyiş','Seçimə qayıt'),
      P('36-B','Boş vaxta köçürmə',day('6 noyabr · hədəf boş şam',0,400,900)+C('6 noyabr · proqnoz','Enerji 900 → 1 300 kkal<br>Zülal 50 → 74 q · Karbohidrat 100 → 140 q · Yağ 33,3 → 49,3 q')+day('7 noyabr · mənbə şam',400,0,800)+C('7 noyabr · proqnoz','Enerji 1 200 → 800 kkal<br>Zülal 74 → 50 q · Karbohidrat 130 → 90 q · Yağ 42,7 → 26,7 q')+N('Qarşılıqlı dəyişmə deyil. Hər iki gün və həftə yoxlanır; başqa porsiyalar səssiz dəyişmir.'),'Bu günə köçür','Başqa boş vaxt seç'),
      P('36-C','Dəyişiklik saxlanıb',C('İki gün birlikdə yeniləndi','6 noyabr şam və 7 noyabr şam üçün əməliyyat tamamlandı.')+C('Alış-veriş','Fərq tətbiq edilib; əvvəlki Evdə var/Alınıb işarələri saxlanıb.')+N('Tam sinxronlaşma yalnız server təsdiqi ilə göstərilir. Faktiki qeydlər dəyişməyib.'),'Menyuya qayıt','Son dəyişikliyi geri qaytar','art_saved',hero=True),
    ],['16.3','16.5','16.9','T05','T06','T09','T10'])
    add(37,'geri-qaytarma-netice','Geri qaytarma · təsdiq və konflikt',[
      P('37-A','Son dəyişikliyi geri qaytaraq?',C('Plan və alış-veriş','Son əməliyyatın əvvəlki versiyası birlikdə bərpa olunacaq.')+N('Hələ heç nə geri qaytarılmayıb. Faktiki yemək qeydləri bu əməliyyatla silinmir.'),'Bəli, geri qaytar','Dəyişikliyi saxla','art_menu'),
      P('37-B','Plan yenilənib',C('Geri qaytarma tətbiq edilmədi','Son əməliyyatdan sonra planın başqa versiyası yaranıb.')+N('Köhnə vəziyyət yenisinin üzərinə yazılmır. Son planı açıb fərqlərə bax.'),'Son plana bax','Geri qayıt','art_write_error',hero=True),
      P('37-C','Geri qaytarıldı',C('Əvvəlki plan bərpa olundu','Alış-veriş fərqi də geri çevrildi. Sonradan yaranan faktiki qeydlər saxlanıb.'),'Menyuya qayıt','Alış-verişə bax','art_saved',hero=True),
    ],['16.5','16.9','T12'])
    add(38,'foto-icaze-ve-yag','Foto · icazə, razılıq və əlavə tərkib',[
      P('38-A','Kamera icazəsi yoxdur',C('Foto çəkmək üçün icazə lazımdır','Sistem ayarlarında icazə verə bilərsən. Yeməyi axtarışla və ya əl ilə də əlavə etmək mümkündür.'),'Ayarları aç','Yemək axtar','art_entry_camera',hero=True),
      P('38-B','Fotonu göndərək?',C('Nə üçün göndərilir?','Yemək namizədini tapmaq üçün analiz ediləcək. Foto özü gündəlik qeydi yaratmır.')+C('Məxfilik məlumatı','Göndərmədən əvvəl faktiki saxlanma müddəti, silmə yolu və xidmət məlumatı göstərilməlidir. Hazır olmayan siyasətə razılıq alınmır.')+N('Bu eskizdə real xidmət və saxlanma müddəti təsdiqlənməyib.'),'Məxfilik məlumatına bax','Foto göndərmədən davam et','art_entry_camera'),
      P('38-C','Əlavə yağ və sous',F('Məhsul','Zeytun yağı')+F('Əlavə miqdar','5',unit='q')+N('Bu, reseptdəki miqdardan əlavədir? Artıq hesablanan yağ ikinci dəfə əlavə edilmir.')+O(['Bəli, əlavədir','Xeyr, reseptdəki yağdır']),'Seçimi təsdiqlə','Yeməyə qayıt','art_ingredient_oil',disabled=True),
    ],['19.5','24.6','T18'])
    login=lambda e,p: F('E-poçt','adexample.com',e)+F('Şifrə','•••••••• · Göstər',p)
    add(39,'hesab-forma-xetalari','Hesab · giriş və bərpa xətaları',[
      P('39-A','Girişi düzəlt',login('Düzgün e-poçt ünvanı yaz.',None)+N('Planın və onboarding cavabların qalır.'),'Daxil ol','Şifrəni unutdum','art_account',disabled=True),
      P('39-B','Daxil olmaq alınmadı',F('E-poçt','ad@example.com')+F('Şifrə','•••••••• · Göstər','E-poçt və ya şifrə uyğun gəlmir. Yoxlayıb yenidən cəhd et.')+R('Şifrəni unutdum','Yeni bərpa linki istə'),'Yenidən yoxla','Hesabsız davam et','art_account'),
      P('39-C','Giriş dayandırıldı',C('Provayderlə giriş tamamlanmadı','Sən imtina etmisən və ya bağlantı kəsilib. Lokal məlumatlar qalır.')+R('Başqa üsulla giriş','E-poçtla davam et'),'Yenidən cəhd et','İndi yox','art_account',hero=True),
      P('39-D','Bərpa linki istə',F('E-poçt','ad@example.com')+C('Link göndərmək alınmadı','Bağlantını yoxla və yenidən cəhd et. Hesabın olub-olmadığı açıqlanmır.'),'Yenidən göndər','Girişə qayıt','art_write_error'),
    ],['18.9','18.10'])
    add(40,'yeni-sifre-ve-destek','Yeni şifrə və dəstək nəticələri',[
      P('40-A','Yeni şifrəni düzəlt',F('Yeni şifrə','•••• · Göstər','Şifrə xidmətin göstərdiyi tələblərə uyğun deyil.')+F('Yeni şifrəni təkrar yaz','•••••• · Göstər','Şifrələr uyğun deyil.')+N('Rəqəmsal minimum bu dizaynda uydurulmur; real xidmət tələbi göstərilir.'),'Yeni şifrəni saxla','Girişə qayıt','art_account',disabled=True),
      P('40-B','Şifrə yeniləndi',C('Yeni şifrən saxlanıb','Hesabına yeni şifrə ilə daxil ola bilərsən. Plan və qeydlərin dəyişməyib.'),'Girişə keç','Geri qayıt','art_saved',hero=True),
      P('40-C','Müraciəti tamamla',F('Cavab üçün e-poçt','adexample.com','Düzgün e-poçt yaz.')+F('Mövzu','Daxil et','Mövzunu yaz.')+F('Mesajın','Daxil et','Göndərmək istədiyin mesajı yaz.')+N('Şəxsi gündəlik və fotolar avtomatik əlavə edilmir.'),'Müraciət göndər','Qaralamaya qayıt',disabled=True),
      P('40-D','Müraciət göndərildi',C('Müraciətin qəbul olundu','Göndərilən mətnə və cavab üçün verdiyin e-poçta baxa bilərsən. Cavab müddəti vəd edilmir.')+R('Göndərilmə alınmadıqda','Eyni forma və mətn saxlanır; Yenidən yoxla görünür.'),'Köməyə qayıt','Müraciətə bax','art_saved',hero=True),
    ],['18.10','19.16'])
    add(41,'hesab-mexfilik-idare','Hesab və məxfilik idarəsi',[
      P('41-A','Məlumatlarını idarə et',R('Məxfilik','Məlumat axını, saxlanma və silmə qaydası')+R('Məlumat ixracı','Format və məzmun önbaxışı')+R('Hesabdan çıxış','Lokal və sinxron məlumatın vəziyyətinə bax')+R('Hesabı sil','Nəticəni oxu və ayrıca təsdiqlə'),'Məxfilik məlumatına bax','Profilə qayıt','art_account'),
      P('41-B','İxrac önbaxışı',C('Daxil ediləcək məlumat','Profil · plan versiyaları · faktiki qeydlər · çəki ölçmələri')+N('Foto və digər məlumatların daxil olub-olmadığı real ixrac siyasətinə görə ayrıca göstərilir.')+R('Hazırlanma alınmadıqda','Məlumat silinmir; yenidən cəhd yolu qalır.'),'İxracı hazırla','Ləğv et','art_log'),
      P('41-C','Hesabdan çıxaq?',C('Sinxronizasiya tamamlanmayıb','Bu telefondakı qeydlərin bərpa vəziyyətini yoxla. Girişdən çıxmaq uğurlu ehtiyat nüsxə demək deyil.')+N('Lokal məlumatın necə saxlanacağı çıxışdan əvvəl faktiki siyasətə uyğun izah edilir.'),'Sinxronizasiya vəziyyətinə bax','Hesabda qal','art_account'),
      P('41-D','Hesabı silməyi təsdiqlə',C('Nə silinəcək?','Hesab, əlaqəli plan və şəxsi qeydlərin silinmə əhatəsi açıq göstərilir.')+N('Foto, ehtiyat nüsxə və saxlama müddətləri faktiki siyasətdən gəlir. Əməliyyat göndərilənədək heç nə silinmir.')+O(['□ Nəticəni oxudum və başa düşdüm']),'Hesabı sil','Hesabı saxla','art_write_error',disabled=True),
    ],['19.16','24.7'])
    add(42,'abunelik-statuslari','Abunəlik · qiymət və ödəniş vəziyyətləri',[
      P('42-A','Abunəlik aktivdir',C('Paket məlumatı','Mağazanın məhsul adı, qiyməti və dövrü göstərilir.')+R('Yenilənmə / bitmə tarixi','Təsdiqlənmiş mağaza məlumatından')+R('Ləğvi planlaşdırılmış','Cari girişin son tarixi ayrıca göstərilir.'),'Mağazada idarə et','Profilə qayıt','art_menu'),
      P('42-B','Ödəniş gözləyir',C('Alış hələ təsdiqlənməyib','Giriş hüququ mağaza təsdiqi gələnədək açılmır. Yenidən alış başlatma.')+N('Mövcud yemək qeydlərin saxlanır.'),'Vəziyyəti yenilə','Pulsuz davam et','art_budget'),
      P('42-C','Ödəniş alınmadı',C('Alış tamamlanmadı','Mağazanın göstərdiyi səbəbə bax. Heç bir uğurlu ödəniş vədi verilmir.')+N('Qeydlərin və plan tarixçən silinmir.'),'Mağazada yoxla','Pulsuz davam et','art_write_error'),
      P('42-D','Alışı bərpa et',C('Mağaza yoxlaması','Yüklənir → təsdiqlənmiş aktiv alış və ya bərpa ediləcək alış yoxdur nəticəsi göstərilir.')+R('Bərpa alınmadıqda','Şəbəkə/mağaza xətası ayrıca görünür; təkrar yoxla yolu var.')+N('Bərpa uyğun məhsulun faktiki təsdiqinə əsaslanır.'),'Alışı yoxla','Profilə qayıt','art_account'),
    ],['19.11'])
    add(43,'el-ile-ve-tarixce','Əl ilə yemək, tarixçə və dəqiq porsiya',[
      P('43-A','Yeməyi özün əlavə et',F('Yeməyin adı','Ev yeməyi')+F('Miqdar','250',unit='q')+F('Mənbə / etiket','İstəyə bağlı')+C('Qida dəyərləri','Enerji məlum deyil · Zülal məlum deyil<br>Karbohidrat məlum deyil · Yağ məlum deyil')+N('Bilinməyən dəyər sıfır deyil. Əsaslandırılmamış dəqiq nəticə göstərilmir.'),'Önbaxışa bax','Axtarışa qayıt','art_portion_scale'),
      P('43-B','Əvvəl yediklərim',R('Toyuq plovu','Əvvəl saxlanmış 1 porsiya','art_recipe')+S('Saxlanmış porsiyanın misli','0,5')+N('Bu qram deyil. Yeni tarix və porsiya ayrıca təsdiqlənir.'),'Önbaxışa bax','Tarixçəyə qayıt','art_entry_previous',hero=True),
      P('43-C','Dəqiq miqdar daxil et',F('Yediyin miqdar','150',unit='q')+C('Ölçülmüş bişmiş çıxım','Bütün qazan 1 200 q · 4 porsiya<br>150 q = qazanın ⅛ hissəsi = ½ porsiya')+N('Bu nümunədə ölçülmüş çıxım var. Çıxım yoxdursa qram yolu bağlı, məlum pay yolu açıqdır.'),'Önbaxışa bax','Porsiyaya qayıt','art_portion_scale',hero=True),
    ],['19.4','19.13','T04','T17'])
    add(44,'qeyd-ve-ceki-duzelis','Qida və çəki qeydlərinə düzəliş',[
      P('44-A','Qeydə düzəliş et',R('Toyuq plovu','6 noyabr · Şam · saxlanmış faktiki versiya','art_recipe')+S('Yediyin porsiya','0,5')+R('Qeydi sil','Ayrıca təsdiq tələb edir')+N('Yalnız bu faktiki qeyd və günlük toplam yenilənəcək.'),'Düzəlişin önbaxışına bax','Qeydə qayıt'),
      P('44-B','Bu qeydi silək?',C('Toyuq plovu · 6 noyabr','Faktiki qeyd və ona bağlı gündəlik toplam yenilənəcək. Planı silmə əməliyyatı deyil.'),'Bəli, qeydi sil','Qeydi saxla','art_write_error',hero=True),
      P('44-C','Bu tarixdə ölçmə var',C('6 noyabr 2026','Əvvəlki: 78,4 kq · Yeni: 78,5 kq')+R('Əvvəlki ölçməni əvəz et','Aydın düzəliş kimi saxla')+R('Ayrıca ölçmə saxla','Eyni tarixdə ikinci ölçmə olduğunu təsdiqlə')+N('Səssiz dublikat yaranmır.'),'Əvvəlkini əvəz et','Çəkiyə qayıt','art_weight'),
    ],['19.6','19.9'])
    add(45,'qosulma-ve-axtaris','Axtarış və plan nəticəsinin əlavə halları',[
      P('45-A','Nəticə tapılmadı',F('Yemək axtar','axtarılan yemək')+C('Bu adla nəticə yoxdur','Adı dəyiş və ya yeməyi əl ilə daxil et.'),'Özüm yazım','Axtarışı dəyiş','art_entry_search',hero=True),
      P('45-B','Axtarış alınmadı',C('Bağlantını yoxla','Yazdığın ad saxlanıb. Lokal saxlanmış məzmun varsa axtarış davam edir.'),'Yenidən yoxla','Əl ilə daxil et','art_write_error',hero=True),
      P('45-C','Plan hazırlamaq alınmadı',C('Uyğun plan tapılmadı','Məhdudiyyətləri qarşılayan məzmun kifayət etmir. Heç bir saxta plan yaradılmır.')+N('Allergiya və tibbi məhdudiyyət yumşaldılmır; vaxt/büdcə təklifi yalnız sənin təsdiqinlə dəyişir.'),'Seçimlərə bax','Hədəfsiz gündəliyə keç','art_empty_menu',hero=True),
    ],['18.13','19.4','22.9'])
    add(46,'destek-ve-xatirlatma','Dəstək forması xətası və baxış xatırlatması',[
      P('46-A','Göndərmək alınmadı',F('Cavab üçün e-poçt','ad@example.com')+F('Mövzu','Qida məlumatında səhv')+F('Mesajın','Porsiya məlumatını dəqiqləşdirmək istəyirəm…')+N('Bağlantını yoxlayıb yenidən cəhd et. Yazdığın mətn ekranda qalıb; müraciət göndərilməyib.'),'Yenidən göndər','Qaralamanı saxla','art_write_error'),
      P('46-B','Hədəfə yenidən baxaq',C('14 günlük baxış vaxtıdır','Ölçü, aktivlik və məqsəd məlumatlarına nəzər salmaq istəyirsən?')+O(['İndi bax','Sonra xatırlat'])+N('Könüllü məhsul xatırlatmasıdır. Açmaq heç bir hədəfi avtomatik dəyişmir.'),'Baxışı aç','İndi yox','art_progress',hero=True),
      P('46-C','Yaşı yoxla',M({0:'Yaşı tam, sıfırdan böyük rəqəmlə daxil et.'},['28,5','172','78,4']),'Davam et','Məqsədə qayıt','art_age',step=2,disabled=True),
    ],['19.16','19.2','27.8'])
    return data
