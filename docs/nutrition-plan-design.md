# DailyPlate: pəhrizsiz, rəsmi mənbələrə əsaslanan qida planı dizaynı

> **Bu sənəd qida hesablamaları üçün məcburi əsasdır.**
> Bütün nutrition calculation və meal/plan generation kodu `docs/nutrition-plan-design.md` ilə uyğun olmalıdır: rəsmi açıq mənbələr, DB-dən yenidən hesab, tibbi qırmızı bayraqlar, disclaimer. Bu sənədi zəiflətmək və ya nəzərə almamaq olmaz.


> **App:** DailyPlate (Play adı); daxildə Qida / Callmood · **Paket:** `com.aladdin.dailyplate`
> **Auditoriya:** Google Play 16+; fərdiləşdirilmiş plan yalnız 18+ sağlam böyüklər üçün
> **Firebase:** Spark yalnız (Blaze / Cloud Functions / Identity Platform yox)
> **Sirrlər:** API açarları AppBackend-də (Go + PostgreSQL); cihazda açar yoxdur
> **Status:** yalnız dizayn sənədi — kod yoxdur · Hazırlanıb: 5 noyabr 2026 (Asia/Baku)

Bizdə dietoloq yoxdur. Sistem yalnız rəsmi, açıq və yoxlanıla bilən mənbələrə söykənməlidir. İnsanlara səhv məlumat vermək qəbuledilməzdir: hər kalori, makro və mikro rəqəm rəsmi qida bazasından yenidən hesablanır; LLM və ya blog rəqəminə heç vaxt etibar edilmir.

## Mündəricat

1. [Məqsəd və prinsiplər](#1-məqsəd-və-prinsiplər)
2. [Məlumat mənbələri və lisenziyalar](#2-məlumat-mənbələri-və-lisenziyalar)
3. [İstifadəçi profili](#3-istifadəçi-profili-girişlər)
4. [Hesablama boru kəməri](#4-hesablama-boru-kəməri)
5. [Yemək planı generasiyası](#5-yemək-planı-generasiyası)
6. [Resept qəbul siyasəti](#6-resept-qəbul-siyasəti-müəllif-hüququ)
7. [Regional / Azərbaycan yeməkləri](#7-regional--azərbaycan-yeməkləri)
8. [Çoxqatlı yoxlama](#8-çoxqatlı-yoxlama)
9. [Tibbi qırmızı bayraqlar](#9-tibbi-qırmızı-bayraqlar-və-istisnalar)
10. [Disclaimer və Play siyasəti](#10-disclaimer-və-google-play-health-siyasəti)
11. [Spark-dostu arxitektura](#11-spark-dostu-arxitektura)
12. [Fazalı MVP roadmap](#12-fazalı-mvp-roadmap)
13. [Risklər və açıq qərarlar](#13-risklər-və-açıq-qərarlar)
14. [Mənbə URL-ləri](#14-mənbə-url-ləri)

---

## 1. Məqsəd və prinsiplər

### Əsas qayda

Bizdə dietoloq yoxdur. Ona görə sistem yalnız rəsmi, açıq və yoxlanıla bilən mənbələrə söykənməlidir. Hər kalori, makro və mikro rəqəm rəsmi qida bazasından yenidən hesablanır; LLM və ya blog rəqəminə heç vaxt etibar edilmir.

### Prinsiplər

- Adekvatlıq, balans, mülayimlik, müxtəliflik — WHO-nun sağlam qida prinsipləri.
- Rəqəmlər həmişə DB-dən; mətn və təqdimat ayrı təbəqədir.
- Tibbi vəziyyətlərdə plan yox — həkimə yönləndirmə.
- Müəllif hüququ: faktlar və inqredient siyahısı kopyalana bilər, ifadəli mətn və foto — yox; yenidən yazılır, ilham göstərilir.
- AI yemək şəkli tanıma planlanır; plan rəqəmləri AI-dan gəlmir.

---

## 2. Məlumat mənbələri və lisenziyalar

Aşağıdakı mənbələr 5 noyabr 2026-da yoxlanılıb.

| Mənbə | Nə üçün | Lisenziya / şərt | URL |
| --- | --- | --- | --- |
| WHO Healthy diet | Şəkər <10% (ideal <5%), yağ ≤30%, doymuş yağ <10%, trans <1%, duz <5 g/gün, meyvə-tərəvəz ≥400 g | Rəsmi fakt vərəqi; sitat + link | https://www.who.int/news-room/fact-sheets/detail/Healthy-diet |
| EFSA DRV | Avropa üçün makro RI, protein PRI (0,83 g/kg), lif AI 25 g, vitamin/mineral PRI/AI/UL | EFSA elmi rəyləri; DRV Finder | https://www.efsa.europa.eu/en/topics/topic/dietary-reference-values · https://multimedia.efsa.europa.eu/drvs/index.htm |
| USDA FoodData Central | Əsas qida tərkibi DB (Foundation, SR Legacy, FNDDS); kalori/makro/mikro | **CC0 1.0 / public domain** | https://fdc.nal.usda.gov/api-guide/ |
| Open Food Facts | Qablaşdırılmış məhsul (barcode), allergen teqləri, Nutri-Score (istəyə bağlı) | **ODbL** (attribution + share-alike) | https://world.openfoodfacts.org/data |
| DGA 2020–2025 | AMDR: protein 10–35%, yağ 20–35%, karbohidrat 45–65%; Na <2300 mg | ABŞ hökumət nəşri | https://www.dietaryguidelines.gov/ |
| Mifflin–St Jeor (1990) | BMR / REE formulu (böyüklər) | Elmi nəşr; düstur açıq istifadə olunur | https://pubmed.ncbi.nlm.nih.gov/2305711/ |
| IOM/NAP DRI | Enerji EER konteksti, AMDR, protein RDA | National Academies nəşri | https://doi.org/10.17226/10490 |
| FAO/INFOODS (+ CARKFOODS) | Regional/qlobal tərkib cədvəlləri; AZ CARKFOODS şəbəkəsindədir | FAO açıq cədvəllər; mənbə göstər | https://www.fao.org/infoods/infoods/regional-data-centres/carkfoods/en/ |
| TÜBER 2022 | Regional qida nümunələri və mədəni kontekst (Türkiyə; AZ-a yaxın mətbəx) | T.C. Sağlık Bakanlığı nəşri; sitat | https://ekutuphane.saglik.gov.tr/Yayin/683 |

### ODbL diqqəti

Open Food Facts-i digər DB ilə birləşdirib törəmə DB paylaşsanız, ODbL share-alike tələb edir. Tövsiyə: FDC (CC0) əsas qida tərkibi olsun; OFF yalnız barcode/etiket/allergen üçün ayrı cədvəldə saxlanılsın, UI-də `Contains data from Open Food Facts` yazılsın, User-Agent təyin olunsun, bulk üçün nightly dump istifadə olunsun.

---

## 3. İstifadəçi profili (girişlər)

Plan yalnız bu sahələr doldurulduqdan və tibbi ekrandan keçəndən sonra yaradılır.

| Sahə | Məcburi? | Qeyd |
| --- | --- | --- |
| Doğum ili / yaş | Bəli | 18-dən aşağı → plan yox |
| Cins (bioloji, enerji formulu üçün) | Bəli | Mifflin–St Jeor kişi/qadın əmsalı |
| Boy (sm), çəki (kq) | Bəli | BMI yalnız informasiya; diaqnoz deyil |
| Aktivlik səviyyəsi | Bəli | Sedentar / az / aktiv / çox aktiv → PAL əmsalı |
| Məqsəd | Bəli | Saxlama / arıqlama / çəki artırma |
| Allergenlər və istisna | Bəli | Süd, qlüten, fıstıq, balıq, yumurta, susam və s. |
| Pəhriz üstünlükləri | Xeyr | Vegetarian, vegan, halal, az-duz… |
| Yemək sayı / gün | Xeyr | 3–5; default 3+1 qəlyanaltı |
| Regional üstünlük | Xeyr | AZ / TR / beynəlxalq mətbəx |
| Tibbi özünü-hesabat | Bəli | Aşağıdakı qırmızı bayraqlar |

**Gizlilik:** Sağlamlıq məlumatı həssasdır. Privacy policy (terms repo) + Play Health apps declaration + Data safety. Minimum data; API açarları AppBackend-də.

---

## 4. Hesablama boru kəməri

```
Profil → BMR (Mifflin–St Jeor) → TDEE (BMR × PAL)
  → Kalori hədəfi (defisit/surplus + min/max)
  → Makro bölgüsü (EFSA RI + DGA AMDR)
  → Mikro + limitlər (EFSA PRI/AI/UL, WHO Na/şəkər)
  → Gündəlik hədəf vektoru
```

### 4.1 BMR — Mifflin–St Jeor

1. Kişi: `BMR = 10·W + 6,25·H − 5·A + 5`
2. Qadın: `BMR = 10·W + 6,25·H − 5·A − 161`

W = çəki (kq), H = boy (sm), A = yaş (il).
Mənbə: Mifflin MD et al., Am J Clin Nutr. 1990;51:241–247 — https://pubmed.ncbi.nlm.nih.gov/2305711/

### 4.2 TDEE və kalori hədəfi

`TDEE = BMR × PAL`

| Aktivlik | PAL (təxmini) | İzah |
| --- | --- | --- |
| Sedentar | 1,2 | Ofis, az hərəkət |
| Az aktiv | 1,375 | Həftədə 1–3 yüngül məşq |
| Aktiv | 1,55 | Həftədə 3–5 məşq |
| Çox aktiv | 1,725 | Gündəlik ağır iş / idman |

- **Saxlama:** hədəf ≈ TDEE.
- **Arıqlama:** defisit 300–500 kcal/gün (təxminən 0,25–0,5 kq/həftə); heç vaxt 750–1000-dən böyük defisit default olmasın.
- **Çəki artırma:** surplus 200–300 kcal.
- **Aşağı hədd (təhlükəsizlik):** qadın ≥1200 kcal, kişi ≥1500 kcal — bundan aşağı yalnız həkim nəzarəti ilə; app avtomatik verməsin.
- **Yuxarı hədd:** hədəf TDEE-nin ~+20%-indən çox olmasın (nəzarətsiz surplus).

> **Qeyd:** 1200/1500 kcal hədləri klinik təcrübə və ADA/idman qidalanma icmallarında geniş yayılmış ehtiyat ölçüləridir; fərdi tibbi tövsiyə deyil. App bunu təhlükəsizlik kilidi kimi təqdim edir, diaqnoz kimi yox.

### 4.3 Makro bölgüsü

| Makro | Default (sağlam böyük) | İcazəli diapazon | Mənbə |
| --- | --- | --- | --- |
| Protein | 1,2–1,6 g/kg (arıqlamada yuxarı) | EFSA PRI min 0,83 g/kg; AMDR 10–35% enerji | EFSA + DGA |
| Yağ | ~25–30% enerji | EFSA RI 20–35%; WHO total ≤30% | EFSA + WHO |
| Karbohidrat | qalan enerji | EFSA RI 45–60%; DGA AMDR 45–65% | EFSA + DGA |
| Lif | ≥25 g/gün | EFSA AI 25 g | EFSA |

Default split nümunəsi (saxlama): protein 20% · yağ 30% · karbohidrat 50% — hər biri RI/AMDR içində qalmalıdır. Vegetarian/vegan üçün protein qram/kq eyni qalır, mənbə bitki + B12 xəbərdarlığı.

### 4.4 Limitlər və mikro yoxlamalar

| Limit | Hədəf / tavan | Mənbə |
| --- | --- | --- |
| Free sugars | <10% enerji (ideal <5%) | WHO |
| Natrium | <2000 mg Na ≈ <5 g duz | WHO; DGA <2300 mg |
| Doymuş yağ | <10% enerji | WHO / DGA |
| Trans yağ | <1% enerji | WHO |
| Meyvə-tərəvəz | ≥400 g/gün | WHO |
| Vitamin/mineral | EFSA PRI/AI-yə yaxın; UL aşılmasın | EFSA DRV Finder |

---

## 5. Yemək planı generasiyası

### Üç təbəqə

1. Şablon + qayda əsaslı seçici (MVP).
2. Optimizator (sonra).
3. LLM yalnız mətn/ideya — rəqəmləri heç vaxt LLM-dən götürmə.

### Addımlar

1. **Hədəf vektorunu al** — gündəlik kcal + P/F/C qram + Na/şəkər/lif limitləri + allergen maskası.
2. **Yemək slotları** — səhər ~25%, nahar ~35%, axşam ~30%, qəlyanaltı ~10% (konfiqurasiya olunan).
3. **Namizəd yeməklər** — Postgres-dəki curated meal templates: inqredient_id + qram. Hər inqredient FDC (və ya daxili regional) food_id-yə bağlıdır.
4. **Filtrlə** — allergen, pəhriz teqi, mövsüm/region, dislike, təkrarlanmama (3 gün eyni əsas yemək yox).
5. **Skorla / seç** — MVP: qayda əsaslı greedy. Sonra: integer linear / local search optimizer.
6. **Yenidən hesabla** — Σ (qram/100 × nutrient_per_100g) yalnız DB-dən. Heç bir sabit kcal hardcode yox.
7. **Validator** — gündəlik cəm hədəfin ±10% kcal; protein min; Na/şəkər tavan; allergen sıfır. Keçməsə yenidən seç.
8. **Mətn təbəqəsi (opsional LLM)** — yalnız bişirmə addımlarının az/ru/en/tr ifadəsi. Çıxışdakı rəqəmlər ignore edilir; UI DB rəqəmini göstərir.

### Niyə şablon+DB (və niyə təmiz LLM plan yox)

| Şablon+DB üstünlükləri | Təmiz LLM riskləri |
| --- | --- |
| Hər rəqəm izlənilə bilir (food_id, versiya, mənbə) | LLM hallüsinasiya (səhv kcal/allergen) |
| Unit test və regressiya mümkündür | Müəllif hüququ riski (sözbəsöz resept) |
| Allergen və limitlər deterministikdir | Play health misinformation riski |
| Spark + ucuz AppBackend-ə uyğundur | Audit və niyə-bu-rəqəm izahı yoxdur |

---

## 6. Resept qəbul siyasəti (müəllif hüququ)

ABŞ Copyright Office Circular 33: sadə inqredient siyahısı və əsas bişirmə istiqamətləri ümumiyyətlə müəllif hüququ ilə qorunmur; ifadəli mətn, hekayə, foto və orijinal tərtibat qorunur.
https://www.copyright.gov/circs/circ33.pdf

| İcazəli | Qadağan |
| --- | --- |
| İnqredient siyahısından ilham; öz sözlərimizlə yenidən yazmaq | Blog/kitab/video mətnini sözbəsöz kopyalamaq |
| Öz foto / illüstrasiya / AI şəkil (lisenziyalı) | Başqasının yemək fotosunu götürmək |
| Qida dəyərlərini FDC/OFF-dən hesablamaq | Dietoloqun saytındakı hazır kcal rəqəmini olduğu kimi yapışdırmaq |
| UI-də ilham: [ad/mənbə] (könüllü, etik) | Brend/adla yanıltıcı rəsmi-resept iddiası |

### Proses

1. Məşhur dietoloqun açıq paylaşdığı resept → yalnız struktur ilhamı.
2. İnqredientləri FDC food_id-lərinə map et; qramları ölç.
3. Bişirmə mətnini sıfırdan yaz (az/ru/en/tr).
4. Kalori/makro/mikro = DB recompute; mənbə sahəsində FDC release + daxili recipe_version.
5. Orijinal URL yalnız daxili editorial qeyddə; istifadəçiyə kopyalandı təəssüratı yaratma.

---

## 7. Regional / Azərbaycan yeməkləri

### CARKFOODS

FAO/INFOODS CARKFOODS regional mərkəzi Azərbaycanı da əhatə edir (Əfqanıstan, Qazaxıstan, Qırğızıstan, Tacikistan, Türkmənistan, Özbəkistan ilə birlikdə):
https://www.fao.org/infoods/infoods/regional-data-centres/carkfoods/en/

Amma hazırda yüklənə bilən vahid Azerbaijan FCT siyahıda görünmür — yalnız kontakt şəbəkəsidir. Buna görə AZ yeməkləri üçün praktiki yol: FDC-də yaxın analoq + öz ölçülmüş resept + FAO qlobal cədvəllər (paxlalılar, balıq və s.).

- Yerli yemək (plov, dolma, qutab, dushbara…): inqredientlərə parçala → hər biri FDC və ya daxili `foods` cədvəlində → bişmiş çəki (yield) əmsalı tətbiq et (FAO Density DB kömək edir).
- Analoq seçimi: quzu əti bişmiş ↔ FDC lamb; duzlu pendir ↔ white cheese / feta-tipli entry; fərqi `confidence` sahəsində saxla (high/medium/low).
- TÜBER 2022: regional porsiya və qida qrupu nümunələri üçün istinad (Türkiyə); AZ mətbəxinə yaxın, amma rəsmi AZ FCT əvəzi deyil — https://ekutuphane.saglik.gov.tr/Yayin/683
- OFF: Azərbaycan/Türkiyə supermarket barcode-ları üçün yaxşı; tərkib keyfiyyəti dəyişkəndir — plan üçün FDC üstün, OFF əlavə.
- Gələcək: laborator və ya universitetlə kiçik AZ FCT əməkdaşlığı; nəticələr CC0 və ya aydın açıq lisenziya ilə.

---

## 8. Çoxqatlı yoxlama

| Təbəqə | Nə edir | Tezlik |
| --- | --- | --- |
| DB recompute | Hər plan cavabında nutrient-lər food_id×qram ilə yenidən hesablanır | Hər request |
| Rule validator | Allergen, min kcal, max defisit, Na/şəkər, makro diapazon | Hər request |
| Range checks | Hər nutrient üçün fiziki mümkün interval (məs. 100 g alma ≠ 900 kcal) | Ingest + runtime |
| Unit testlər | Referans hallar: bilinen FDC qidaları, Mifflin nümunələri, WHO limit edge | CI hər PR |
| Periodic spot check | Ayda N planı insan (sahib və ya könüllü dietetik tələbə) yoxlayır | Aylıq |
| İstifadəçi feedback | Bu yemək uyğunsuz / allergen → ticket + template disable | Davamlı |
| Mənbə versiyası | FDC release_id, OFF dump tarixi plan meta-da | Hər plan |

### Referans test nümunələri (spesifikasiya)

- (a) 70 kq, 175 sm, 30 yaş kişi, sedentar → BMR və TDEE sabit.
- (b) 100 g USDA raw apple → sabit kcal.
- (c) süd allergeni olan istifadəçiyə pendirli şablon heç vaxt düşməsin.
- (d) arıqlama planı heç vaxt 1200/1500 altındakı hədəf verməsin.

---

## 9. Tibbi qırmızı bayraqlar və istisnalar

### Plan verilmir — həkimə yönləndir

Hamiləlik / laktasiya; 18-dən aşağı yaş; yemək pozuntusu tarixçəsi və ya aktiv şübhə; diabet (tip 1/2); böyrək xəstəliyi; ağır ürək-damar; həkim nəzarətində pəhriz; BMI çox aşağı (məs. <18,5) və ya çox yüksək + komorbid öz-hesabat.

Bu hallarda app yalnız ümumi WHO maarifləndirməsi və həkimə müraciət edin göstərir — fərdiləşdirilmiş kalori planı yox.

- Özünü-hesabat çeki siyahısı onboarding-də; Bəli cavabı plan generatorunu bağlayır.
- Heç bir xəstəlik üçün müalicəvi pəhriz iddiası yoxdur.
- Dərman-qidalanma qarşılıqlı təsiri modelləşdirilmir.
- Allergen: şübhəli OFF etiketi və ya map edilməmiş inqredient → yeməyi namizəddən çıxar.

---

## 10. Disclaimer və Google Play health siyasəti

Play Health Content and Services:
https://support.google.com/googleplay/android-developer/answer/16679511

- Store və app mətni (EN nümunə): *DailyPlate is not a medical device and does not diagnose, treat, cure, or prevent any medical condition. Consult a healthcare professional for medical advice.*
- AZ/RU/TR eyni məna ilə terms səhifəsində: https://aladdinbiyabangerd.github.io/terms/dailyplate/
- Health apps declaration form (Play Console → App content).
- Yanıltıcı iddialar yox (Health misinformation).
- AI şəkil tanıma gələndə cihaz uyğunluğu və xəta ehtimalı açıqlansın.

---

## 11. Spark-dostu arxitektura

```
Android app ──login/sync──► Firebase Spark (Auth, Firestore profil, Remote Config)
     │
     └──Bearer Firebase JWT──► AppBackend (Go + Postgres + API keys)
                                   ├── Nutrient cache (FDC snapshot, recipes, OFF subset)
                                   └── Xarici API (FDC / OFF / LLM mətn) — yalnız server
```

- Firebase Spark: Auth (Google + Email), Firestore-da yüngül profil/flag; Cloud Functions yox.
- AppBackend: plan generate, FDC sync job, OFF dump import, LLM proxy, purchase verify (gələcək).
- Postgres: `foods` (fdc_id, nutrients jsonb, source, license), `ingredients_map`, `meal_templates`, `plans`, `plan_items`.
- FDC: hourly 1000 req limit — nightly/full download + lokal cache əsas yoldur; API yalnız yeniləmə üçündür.
- API açarları yalnız server env-də; `google-services.json` və sirrlər git-də yox.
- AI image recognition: model çağırışı AppBackend-dən; nəticə → food_id candidate → yenə DB nutrient.

---

## 12. Fazalı MVP roadmap

| Faza | Məzmun | Təxmini |
| --- | --- | --- |
| P0 | Hədəf + şablon plan | 4–6 həftə |
| P1 | Barcode + allergen | +3–4 həftə |
| P2 | Regional + LLM mətn | +4 həftə |
| P3 | Şəkil tanıma | sonra |
| P4 | Optimizator | sonra |

1. **P0 — Təhlükəsiz özəyi:** profil + tibbi ekran; Mifflin/TDEE; FDC snapshot Postgres-də; 30–50 şablon yemək; 7 günlük plan API; validator; Play disclaimer; terms link.
2. **P1 — Real dünya məhsulları:** OFF barcode (ODbL attribution); allergen teqləri; istifadəçi feedback; unit testlər.
3. **P2 — Region və dil:** 20–40 AZ/TR yemək şablonu; LLM ilə az/ru/en/tr bişirmə mətni; editorial spot-check.
4. **P3 — AI şəkil:** şəkil → namizəd food_id → istifadəçi təsdiqi → DB nutrient. Plan generatoru dəyişmir.
5. **P4 — Optimizator:** multi-day diversity, qiymət/mövsüm, mikro PRI-yə yaxınlaşdırma; hələ də DB rəqəmləri.

### Checklist (5 noyabr 2026)

- [x] Dizayn sənədi (bu fayl)
- [ ] FDC dump + foods sxemi AppBackend-də
- [ ] BMR/TDEE + validator unit testləri
- [ ] İlk 30 meal template (FDC map)
- [ ] Tibbi qırmızı bayraq UI + plan blokası
- [ ] Play Health declaration + store disclaimer
- [ ] OFF attribution UI (əgər barcode)
- [ ] AZ regional şablonlar

---

## 13. Risklər və açıq qərarlar

| Risk | Təsir | Mitigasiya |
| --- | --- | --- |
| Səhv nutrient / səhv map | Yüksək | Yalnız FDC ID; confidence; spot check; feedback |
| Allergen buraxılması | Yüksək | Strict exclude; naməlum → çıxar; disclaimer |
| Həddindən artıq defisit | Yüksək | Min kcal kilidi; max defisit 500 default |
| ODbL share-alike sürprizi | Orta | OFF-i ayrı saxla; FDC əsas |
| Müəllif hüququ iddiası | Orta | Yenidən yaz; foto özümüz; ilham siyasəti |
| Play health rədd / misinfo | Yüksək | Disclaimer; tibbi iddia yox; declaration |
| AZ FCT boşluğu | Orta | Analoq + yield; TÜBER kontekst; gələcək FCT |
| LLM rəqəm sızması UI-yə | Yüksək | UI yalnız server-computed nutrients render edir |
| Firebase Spark limit | Aşağı | Ağır iş AppBackend + Postgres |

### Aladdindən qərar gözləyən nöqtələr

- Default makro split (20/30/50) və arıqlama defisiti (300 vs 500) — razılaşdırmaq.
- Regional default: AZ şablonları P0-da az sayda, yoxsa P2-yə saxlamaq.
- OFF-i P1-də açmaq (ODbL öhdəliyi ilə) və ya yalnız FDC ilə getmək.
- Spot-check üçün kənar dietetik məsləhət (ödənişli qısa audit) — büdcə varmı.
- Terms-dəki may be rewarded ads sətri ARROW üçündür; DailyPlate-də reklam yoxdur — ayrı qalmalıdır.

### Yekun

DailyPlate plan sistemi dietoloqu əvəz etmir; rəsmi açıq məlumat + deterministik hesab + ciddi təhlükəsizlik kilidləri ilə sağlam böyüklərə ümumi qida planı təklif edir. Hər rəqəmin mənbəyi və versiyası olmalıdır. Bu sənəd dizayndır — implementasiya ayrı növbədə, Aladdinin OK-undan sonra.

---

## 14. Mənbə URL-ləri

1. WHO Healthy diet — https://www.who.int/news-room/fact-sheets/detail/Healthy-diet
2. EFSA Dietary Reference Values — https://www.efsa.europa.eu/en/topics/topic/dietary-reference-values
3. EFSA DRV Finder — https://multimedia.efsa.europa.eu/drvs/index.htm
4. USDA FoodData Central API Guide (CC0 1.0) — https://fdc.nal.usda.gov/api-guide/
5. Open Food Facts data (ODbL) — https://world.openfoodfacts.org/data
6. Dietary Guidelines for Americans 2020–2025 — https://www.dietaryguidelines.gov/
7. Mifflin et al. 1990 (PubMed) — https://pubmed.ncbi.nlm.nih.gov/2305711/
8. IOM/NAP DRI — https://doi.org/10.17226/10490
9. FAO/INFOODS CARKFOODS — https://www.fao.org/infoods/infoods/regional-data-centres/carkfoods/en/
10. TÜBER 2022 — https://ekutuphane.saglik.gov.tr/Yayin/683
11. Google Play Health Content and Services — https://support.google.com/googleplay/android-developer/answer/16679511
12. US Copyright Circular 33 — https://www.copyright.gov/circs/circ33.pdf
13. DailyPlate terms — https://aladdinbiyabangerd.github.io/terms/dailyplate/
