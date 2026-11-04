# Qida məzmun kitabxanası

Versiya 0.3.0: 500 mənbəli ərzaq qeydi, qramla tərkibi və AZ hazırlanma addımları yazılmış 224 resept layihəsi, 8 həftəlik yemək ideyası rotasiyası. SQLite-də 8 217 USDA istinad qeydi var: 7 793 SR Legacy + 424 Foundation Foods. Seçilmiş 500 qeyd bu istinad bazasının alt dəstidir; xam və bişmiş vəziyyətlər ayrıca sayılır.

Bu paket proqramlaşdırma və məzmun yoxlaması üçündür. Resept və mənbə uyğunluğunun redaktor yoxlaması tamamlanmadığı üçün `publishable=false`, fərdi tövsiyə qaydaları təsdiqlənmədiyi üçün `eligible_for_live_personalized_planning=false` saxlanır. Bütün reseptləri fiziki bişirmək inqrediyentlərə əsaslanan təxmini porsiya hesabı üçün universal tələb deyil. Bişmiş 100 qram və şəkilli porsiyalar ayrıca çıxım/ölçü sənədi tələb edir; bunlar `feature_gates` sahəsində ayrılıb.

## Fayllar

- `nutrition-library.sqlite`: mənbə ərzaqları, seçilmiş ərzaqlar, reseptlər, inqrediyent əlaqələri və həftəlik ideyalar.
- `foods.json`, `recipes.json`, `weekly-ideas.json`: tətbiq və admin panel üçün UTF-8 JSON.
- `foods.csv`, `recipes.csv`: insanın yoxlaması və başqa alətlərə idxal üçün UTF-8 BOM cədvəlləri; yox olan nutrient CSV-də boş qalır.
- `catalogue-az.txt`: bütün ərzaq və reseptlərin oxunaqlı kataloqu.
- `manifest.json`: saylar, mənbə arxivlərinin SHA-256 izləri, versiya, enerji seçmə siyasəti və məhdudiyyətlər.
- `curated-foods.tsv`: əl ilə seçilmiş FDC uyğunlaşdırmaları və AZ/RU işçi adları.
- `curated-foods-expansion.tsv`: əlavə 250 ərzaq qeydi; əsas 250 qeydin ID-lərini dəyişmir.
- `recipe-drafts.py`: bu layihə üçün yazılmış resept layihələrinin giriş məlumatı.
- `recipe-drafts-expansion.py`: əlavə 100 resept layihəsi; əvvəlki reseptlərin tərkibini dəyişmir.
- `sources/`: dəyişdirilməmiş rəsmi USDA arxivləri və axtarış üçün çıxarılmış ad indeksi.

## Mənbə və lisenziya

USDA yükləmə səhifəsi: https://fdc.nal.usda.gov/download-datasets/

1. SR Legacy CSV, 2018-04 son tarixi buraxılışı. Bu məlumatlar müasir Azərbaycan məhsullarının etiket əvəzi deyil.
2. Foundation Foods JSON, 2026-10-29 buraxılışı. SQLite-də əlavə istinad kimi saxlanır; 500 seçilmiş SR uyğunlaşdırmasını səssiz dəyişmir.
3. Supporting Data CSV, 2022-10-28. Mənbə paketinə daxil edilib; əsas idxal öz arxivindəki nutrient təriflərini istifadə edir.

USDA FoodData Central məlumatı CC0-dır: https://fdc.nal.usda.gov/api-guide/#bkmk-9

Hazırlanma təhlükəsizliyi: https://www.foodsafety.gov/food-safety-charts/safe-minimum-internal-temperatures

Reseptlər bu layihə üçün müəllif layihələridir; ənənəvi yeməyin etalon tərkibi və ya dietoloq təsdiqi kimi təqdim olunmur. AZ/RU ərzaq adları və üç dildə resept başlıqları işçi tərcümələrdir; EN/RU hazırlanma addımları hələ `null`-dur.

## Hesablama

Ərzaq qiymətləri mənbədə təsvir edilmiş yeyilən hissənin 100 qramına aiddir. Xam və bişmiş ayrı qeydlərdir. Enerji üçün 1008, yoxdursa 2048, sonra 2047 seçilir; nutrient ID-si hər nəticə ilə saxlanır. Enerji 4/4/9 ilə yenidən uydurulmur.

Resept üzrə ilkin toplam = hər inqrediyentin 100 q qiyməti × yeyilən qram / 100 cəmi. Nəzərdə tutulan porsiyaya bölünmə ayrıca saxlanır. Bişmə itkisi, nutrient saxlanması və real porsiya çıxımı düzəldilmədiyi üçün bu nəticə `ingredient_sum_no_cooking_retention_or_loss_adjustment` adlanır. `per_100g_cooked` və bişmiş yekun çəki hələ `null`-dur.

Bir inqrediyentin nutrienti yoxdursa bütün reseptin həmin nutrient toplamı `null` olur və çatışmayan inqrediyentlər göstərilir. Ümumi şəkərdən sərbəst və əlavə şəkər çıxarılmır. Mənbə suyunun mineral dəyəri konkret ev suyunun ölçüsü deyil.

Allergenlər generik tərkibdən çıxarılmış namizədlərdir. Məhsul etiketi, çarpaz təmas və resept yoxlanmadan allergiyaya uyğunluq iddiası verilmir. Pendir üçün rennet, tərcümə uyğunluğu və yerli məhsul fərqləri ayrıca yoxlanmalıdır.

## Yenidən qurmaq və yoxlamaq

```sh
python3 library/tools/build_library.py
python3 library/tools/validate_library.py
```

Şəbəkə və API açarı tələb olunmur. Qurucu yalnız bu kitabxananın törəmə cədvəllərini yeniləyir. İstifadəçi məlumatı bu bazada saxlanmamalıdır.

## Növbəti insan yoxlaması

İnqrediyent miqdarı və elan edilən porsiya sayı ilə təxmini hesab göstərmək üçün mənbə uyğunluğu, hesablama və resept təlimatı redaktor tərəfindən yoxlanır. Fərdi menyu tövsiyələri üçün qidalanma qaydaları ayrıca mütəxəssis təsdiqindən keçir. EN/RU addımlar, allergen/etiket, region qiymətləri və saxlanma təlimatı da tamamlanmalıdır.

Hazır yeməyin 100 qramı göstəriləcəksə bişmiş yekun çəki ölçülür və ya uyğun etibarlı mənbədən sənədləşdirilmiş çıxım təxmini istifadə edilir. Şəkilli Az/Orta/Çox porsiyaları ayrıca kalibrasiya və ya açıq təxmin etiketi tələb edir. Hər reseptin `release_blockers` və `feature_gates` sahələri bu iki işi ayırır.

## Genişlənmənin tərkibi

Ərzaqlar: 55 süd/yumurta, 55 taxıl/çörək, 35 paxlalı, 70 ət/balıq/dəniz məhsulu, 120 tərəvəz/göyərti, 70 meyvə/şirə, 30 çərəz/toxum, 65 yağ/ədviyyat və digər məhsul.

Reseptlər: 33 səhər yeməyi, 26 şorba, 47 ətli/balıqlı əsas yemək, 45 bitki əsaslı yemək, 30 salat/qarnir, 25 yerli yemək layihəsi və 18 qəlyanaltı. Bunlar fərdi tam rasion kimi yoxlanmayıb.

## Məqsəd, dünya mətbəxi və büdcə genişlənməsi

`recipe-drafts-world.py`: G01–G24, 24 əlavə beynəlxalq ilhamlı müəllif layihəsi. `discovery` sahəsi mətbəx ilhamını, ilkin büdcə kateqoriyasını və sübutun əhatəsini saxlayır. 14 az, 6 orta, 4 yüksək büdcə namizədidir; yerli qiymət yoxlanmayıb. Əvvəlki 200 reseptdə `discovery=null` saxlanır. 500 ərzağın sayı dəyişməyib; yeni reseptlər mövcud mənbəli ərzaqları istifadə edir.

Məqsəd və həftəlik alış-veriş büdcəsi üzrə məcburi seçim qaydaları əsas planın 17-ci bölməsindədir. Bu paket işləyən fərdi tövsiyə mühərriki deyil. Hazır kateqoriya sayları üçün `manifest.json` əsasdır. v0.1/v0.2 ZIP faylları tarixi nüsxələrdir, cari JSON/CSV/SQLite 0.3.0-dır.
