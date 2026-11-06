QİDA — YEKUN VAHİD DİZAYN PAKETİ
6 noyabr 2026 · AZ · 49 dəst / 165 ekran

BAŞLAMAQ
index.html faylını brauzerdə aç. İnternet tələb olunmur. Hər dəstin PNG və
HTML keçidi var. HTML statik dizayn mənbəyidir; tətbiq/prototip deyil.

ƏSAS VERSİYA
Bu paketdə 00–18 köhnə firuzəyi dəstlərin, onboarding və inqrediyent xəta
panellərinin, 19–30 əlavələrinin vahid komponentlərlə yeni versiyaları var.
31–48 əlavə validasiya, açılmış iki-gün önbaxışı və nəticə hallarıdır.
replacement-map.json bütün əvvəlki seçilmiş firuzəyi dəstlərin qarşılığını göstərir.
Əvvəlki natural/warm/dark istiqamətləri seçilmiş firuzəyi kitabxanaya daxil deyil.
Köhnə rasterlər repoda tarixçə kimi qalır; yekun paketdə aktiv ekran kimi yoxdur.
references/ ilkin 00, təsdiqli D02/D03 və son 31/32 axın istinadlarıdır; yeni setlə qarışdırma.
Yeni maketlərin istifadəçi tərəfindən təsdiqləndiyi iddia edilmir.

VAHİD QAYDALAR
Bir telefon çərçivəsi, QİDA başlığı, geri dönüş, 16 px əsas mətn, ağ kartlar,
firuzəyi əsas hərəkət və şaftalı xəbərdarlıqlar. Seçim çərçivə və ✓ ilə ayrılır.
Rənglər dəyişdirilməyib: design-concepts/tokens.css-dən mövcud simple-* tokenləri.
Aralıqlar 8/16/24/32; kart radiusu 24; mətn/idarələr üçün mövcud ölçü qaydaları.
Alt menyu: Bu gün / Menyu / Əlavə et / İrəliləyiş / Profil. Yalnız aid bölmə aktivdir.
Tapşırıq/formalarda görünən geri dönüş var. Allergiya əvvəl Bəli/Xeyr sualıdır; cavabsızlıq Xeyr deyil. Bəli siyahını açır.
Planın hazırdır → Başla → Bu gün keçidi faktiki qeyd yaratmır.
Onboarding beş addımdır; sağlamlıq 3/5-dədir.
kkal / q / kq / sm, onluq vergül, 6 noyabr 2026 / 2–8 noyabr 2026 istifadə edilir.
Əvvəl–sonra = faktiki qəbul + qalan planlı hissə; yeyilmiş hissə təkrar sayılmır.
Bişmiş qram və 100 q üçün nəticə yalnız ölçülmüş çıxım əsasında göstərilir.
Məlum olmayan nutrient sıfır sayılmır. Plan dəyişikliyi faktiki qeyd deyil.
Qazan/porsiya ayrımı D02, yeddi tarix və statuslar D03 quruluşunu saxlayır.
D03-ün hazır tətbiqdə açılmayan imkanları gizlətmə qaydası qüvvədə qalır;
bu paket gələcək tam dizaynı göstərir, həmin funksiyaları tətbiqdə açmır.
Uzun forma və redaktorlar tam məzmun çəkilişidir; real cihazda sürüşür.

ƏHATƏ VƏ RƏQƏMLƏR
coverage.json sadalanmış 11 dizayn qrupunu konkret ekranlara bağlayır.
Ölçmə və qida nümunələri istifadəçiyə verilmiş plan deyil. 500 kkal / 300 q,
30 q zülal / 50 q karbohidrat / 20 q yağ sintetik dizayn fixture-idir.
Redaktə nümunəsi 500 → 555 kkal-dir; DB reseptinin yenidən hesablandığı iddiası deyil.
Miqdarlar və illüstrasiyalar real porsiya ölçmə sübutu deyil.
Klinik hədd, mağaza qiyməti, cavab müddəti və foto saxlama müddəti uydurulmayıb.
Qiymət, xidmət tələbi və saxlanma siyasəti asılılıqları maketdə şərti göstərilir;
onların real mətnləri inteqrasiya zamanı faktiki konfiqurasiyadan gəlməlidir.

YOXLAMA
49 PNG agent tərəfindən açılıb istinadlarla müqayisə olunub; visual-review.json
konkret şəkil hash-lərini və baxış qeydlərini saxlayır. Bu native qəbul sənədi deyil.
render-report.json hər dəst üçün təzə Chrome ölçməsini, şəkil və daşma nəticəsini verir.
Kontrast: simple-text/simple-bg 11,95; simple-muted/simple-bg 5,04;
ağ/simple-primary 4,71. Hamısı 4,5 həddindən yuxarıdır. Kiçik link, ikinci hərəkət və aktiv naviqasiya
mətni açıq fonda simple-text istifadə edir (11,95); simple-primary həmin fonda
yalnız böyük +/− idarələrində və dekorativ/UI xətlərində işlənir (4,43 > 3).
Native AZ/EN/RU × 100/150%, klaviatura, TalkBack, cihaz və backend yoxlanmayıb.
Əvvəlki native vizual qəbul borcu ayrıca açıqdır; dizaynla avtomatik bağlanmır.
Tətbiq kodu dəyişməyib, APK yaradılmayıb.

MƏNBƏ VƏ TƏKRAR YARATMA
screens.json bütün ekran mətnlərinin/markup-un yekun çıxışıdır.
components.py, legacy.py, states.py, build.py və style.py redaktə olunan mənbələrdir.
source-inputs/completion-screens.json 19–30 dəstlərinin ilkin mətn mənbəyidir.
assets/ bütün istifadə edilən mövcud illüstrasiyaları ehtiva edir.
HTML-lərdə üslub/tokenlər daxilidir, şəkil yolları paketə nisbidir.
Python 3: python3 build.py
Node + quraşdırılmış macOS Chrome: node render.mjs
Yeni renderdən sonra şəkilləri yenidən açıb bax; review hash-lərini avtomatik təsdiqləmə.
MANIFEST.json fayl siyahısı və SHA-256 yoxlamasıdır; dizayn təsdiqi deyil.
