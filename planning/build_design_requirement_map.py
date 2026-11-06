"""Build the plan -> concept PNG -> outstanding state inventory (no app audit).

Run with --check to detect stale source text, references, or derived views.
Numbered plan items remain verbatim; annotations never modify product rules.
"""
import argparse
from collections import Counter
import hashlib
import json
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
DEST = ROOT / 'docs/qa'
DESIGN = 'design-concepts/simple-turquoise/'

# Panel IDs are local to an image. 01–44 are the original numbered screens;
# additional sheets use descriptive panel names rather than invented numbers.
SHEETS = {
    '00': ('00-reference.png', 'Bu gün; Əlavə et; Porsiya; Menyu; İrəliləyiş; Məqsəd'),
    '01': ('01-baslangic.png', '01 Xoş gəldin; 02 Özünü tanıt; 03 Aktivlik; 04 Planın hazırdır'),
    '02': ('02-secimler-ve-hesab.png', '05 Allergiya; 06 Vaxt/büdcə/yemək sayı; 07 Planını itirmə; 08 Giriş'),
    '03': ('03-yemek-qeydiyyati.png', '09 Axtarış; 10 Kamera; 11 Foto namizədi/yağ; 12 Əlavə olundu'),
    '04': ('04-menyu-ve-alisveris.png', '13 Menyu; 14 Resept; 15 Alternativ; 16 Alış-veriş'),
    '05': ('05-irelileyis-ve-profil.png', '17 Çəki; 18 Həftəlik baxış; 19 Profil; 20 Xatırlatma'),
    '06': ('06-abunelik-ve-veziyyetler.png', '21 Abunəlik; 22 Boş Bu gün; 23 Offline lokal saxlanma; 24 Tanınmayan foto'),
    '07': ('07-inqrediyent-redaktesi.png', '25 Resept girişi; 26 Redaktor; 27 Məhsul seçici; 28 Önizləmə'),
    '08': ('08-yemek-ve-gun-deyisme.png', '29 Əməliyyat seçimi; 30 Alternativlər; 31 Gün seçimi; 32 İki günün təsdiqi'),
    '09': ('09-foto-sonrasi-resept.png', '33 Bəli/xeyr; 34 Faktiki tərkib; 35 Qazan/şəxsi pay; 36 Faktiki önizləmə'),
    '10': ('10-deyisiklik-ve-xeta-hallari.png', '37 Uyğunsuz dəyişmə; 38 Natamam nutrient; 39 Yeni addımlar; 40 Qaralama çıxışı'),
    '11': ('11-yenilenmis-plan-ve-tesdiq.png', '41 Undo; 42 Alış-veriş fərqi; 43 Alternativ önizləmə; 44 Faktiki qeyd'),
    '12': ('12-elave-profil-secimleri.png', 'Sağlamlıq; Büdcə və mətbəx; Evdə nə var; Profil saxlanıb'),
    '13': ('13-bos-veziyyetler.png', 'Boş Menyu; Əlavə et bağlı; Boş İrəliləyiş; Hesab hazır deyil'),
    '14': ('14-elave-formalar-ve-veziyyetler.png', 'Əlavə allergen/məhdudiyyət; İstəksizlik; Yüklənir; Lokal yazı xətası'),
    'D02': ('approved/d02-portions.png', 'Resept qazan/şəxsi pay; Dəqiq miqdar/naməlum enerji'),
    'D03': ('approved/d03-menu.png', 'Tarixli menyu/qismən yeyilib; Boş menyu; Razılaşdırılmış qərarlar'),
}

# These are design-state gaps, not declarations that application code is absent.
# Separate runtime evidence is required even when a concept depicts the state.
GAPS = {
 'scope': 'Əhatə/disclaimer izahı, tibbi vəd verməyən mətn və peşəkar dəstəyə keçid üçün ayrıca panel yoxdur.',
 'evidence': 'Formula/mənbə/versiya/tarix, qeyri-müəyyənlik və faktiki baxış statusunu açan tam detal paneli yoxdur.',
 'locale': 'AZ/EN/RU × 100%/150%, ən uzun etiketlər, yuxarı/orta/aşağı sürüşmə, ekran oxuyucu və klaviatura vəziyyətləri ayrıca göstərilmir.',
 'region': 'Region/valyuta seçicisinin açıq vəziyyəti, dəyişməsi və qiymətsiz region nəticəsi yoxdur; dil region əvəzi deyil.',
 'inputs': 'Boş/mənfi/sonlu olmayan giriş, onluq vergül, vahid anlaşılmazlığı, xəta və cavabları saxlayan geri dönüş paneli yoxdur.',
 'no-target': 'Cins/yaş/aktivlik çatışmır və ya əhatədən kənardır: avtomatik hədəfsiz gündəlik və mənşəli mütəxəssis hədəfi yolları ayrıca çəkilməyib.',
 'activity': 'Dörd EER seçimi, ayrıca low_active, tam təsvirlər, ACTIVITY_UNCERTAIN və köhnə üç seçimin yenidən təsdiqi yoxdur.',
 'health': 'Bütün tətbiq olunan risk sualları, cavabsız vəziyyət, INELIGIBLE/INCOMPLETE/MODEL_OUT_OF_SCOPE və hədəfsiz davam ayrıca göstərilmir.',
 'allergen': 'Cavabsız/çoxlu allergiya, yoxdur ilə qarşılıqlı istisna, konkret qoz-fındıq, naməlum allergen və blok səbəbi ayrıca çəkilməyib.',
 'dislike': 'İstəksizliyi əlavə et/dəyiş/sil, hamısına uyğunsuz nəticə və seçimi ləğv etmədən alternativ yolu yoxdur.',
 'time': 'Vaxtı naməlum və ya limitə uyğun resepti olmayan nəticə, aktiv/gözləmə vaxtının izahı ayrıca yoxdur.',
 'budget': 'Məbləğ/nəfər/valyuta validasiyası, köhnə/yox qiymət, naməlum əhatə, büdcə aşılması və istifadəçi qəbul etdiyi alternativ ayrıca yoxdur.',
 'goals': 'Dörd məqsədin nəticə fərqləri, healthy-də defisitsiz nəticə, GOAL_OUT_OF_SCOPE və məqsəd dəyişməsinin önizləməsi yoxdur.',
 'slots': '3/4/5 slotlu bütöv menyular və slot sayını dəyişərkən sabit günlük hədəf ayrıca göstərilməyib.',
 'generation': 'SUCCESS-dən başqa NO_FEASIBLE_PLAN, INCOMPLETE_DATA, POLICY_UNAVAILABLE, STALE_INPUT, TEMPORARY_FAILURE üçün ayrı səbəb/bərpa panelləri yoxdur.',
 'rules': 'PASS/FAIL/UNKNOWN/NOT_APPLICABLE, nutrient əhatəsi və sərt/yumşaq qaydanı ayıran istifadəçi izahları ayrıca göstərilmir.',
 'reassess': 'Yeni yaş/çəki/aktivlik üçün səbəbli əvvəl/sonra önizləmə, təsdiq/ləğv və yalnız gələcəyə tətbiq yoxdur; 14 günlük baxış/natamam ölçmə və simptom yolu yoxdur.',
 'food-detail': 'Tam qida mənbəyi, xam/bişmiş variant, nutrient vahidləri, məlum/naməlum sahələr və məhsul/etiket variantının detalı yoxdur.',
 'recipe-detail': 'Tam versiya/allergen/çıxım/saxlanma təlimatı, müəllif/hüquq/status detalı və seçilmişlərə əlavə/çıxarma vəziyyətləri yoxdur.',
 'portion': 'Məlum/naməlum çıxım, məhsula məxsus vahid çevirməsi, səhv/çox böyük miqdar və qalan pay sərhədi üçün ayrıca panellər yoxdur.',
 'measured': 'Az/Orta/Çox üçün real çəkilmiş, eyni qab/bucaqda kalibrə olunmuş resept fotoları bu konsept dəstində yoxdur; illüstrasiya ölçü sübutu deyil.',
 'search': 'Axtarışın yüklənmə/boş/xəta və təkrar cəhd vəziyyətləri, tam əl ilə qida forması və əvvəl yediklərim siyahısı yoxdur.',
 'camera': 'İcazə rəddi, qalereya rəddi/ləğvi, çəkiliş/yükləmə xətası, tanıma gözləməsi və axtarışa dönüş ayrıca yoxdur.',
 'oil': 'Var seçiminin məhsul/miqdar forması, artıq hesablanan yağın iki dəfə sayılmaması və Başqa yeməkdir qolunun tam davamı yoxdur.',
 'log': 'Konkret qeydin düzəliş forması, silmə təsdiqi, hədəfsiz/natamam qalan enerji və neytral aşma nəticəsi ayrıca yoxdur.',
 'write': 'Lokal yazı xətası nümunəsi var, amma hər əməliyyatda məzmunu qoruyan retry, iki toxunuş, restart və yalnız dayanıqlı yazıdan sonra uğur vəziyyəti ayrıca göstərilmir.',
 'sync': 'Lokal-only/pending/synced/failed, quota/token xətası, revision konflikti və silinmiş qeydin köhnə cihazdan bərpasının qarşısı üçün UI ayrılmayıb.',
 'auth': 'Qeydiyyat, şifrə sıfırlama, səhv giriş, provayder ləğvi, qonaq məlumatının köçürülməsi və başqa cihazda bərpa/konflikt panelləri yoxdur.',
 'weight': 'Tarix seçici, əvvəlki ölçmələr, düzəliş/silmə, eyni gün əvəzləmə/ayrı ölçmə, boş həftə və tək nöqtə nəticəsi ayrıca yoxdur.',
 'reminder': 'Vaxt seçici, 4/5 slot, icazə rəddi, gecə yarısını keçən sakit saatlar, saat qurşağı dəyişməsi və saxlanma xətası yoxdur.',
 'billing': 'Aktiv/bitmiş/ləğvi planlı/pending/uğursuz/bərpa/mağaza xətası, real qiymət yüklənməsi və entitlement yoxlaması ayrıca yoxdur; eskiz qiyməti müqavilə deyil.',
 'privacy': 'Kömək/FAQ/dəstək/yanlış qida bildirişi, məxfilik, ixrac, çıxış, hesab silmə təsdiqi və nəticə panelləri yoxdur.',
 'photo-consent': 'Fotodan əvvəl məqsəd/razılıq/retention/silmə və yükləməni ləğv etmə paneli yoxdur.',
 'shopping': 'Evdə var və Alınıb ayrı idarələr, ümumi həftəlik miqdar, qablaşdırma xərci, naməlum çevirmə və plan artıq tələb etməyən alınmış məhsul ayrıca yoxdur.',
 'share': 'Sistem paylaşım önbaxışı, ləğv və xəta vəziyyətləri yoxdur; paylaşım avtomatik göndərmə deyil.',
 'editor': '0/boş/mənfi/NaN/çox böyük qram, bütün inqrediyentlərin silinməsi, vahid çevirməsinin olmaması və genişlənmiş Digər ərzaqlar ayrıca yoxdur.',
 'custom-version': 'Dəyişən reseptdə əvvəlki təsdiq/çıxım/foto/vaxtın qüvvədən düşməsi, ayrıca istifadəçi versiyası və tarixçənin sabit qalması tam göstərilmir.',
 'instructions': 'Yeni məhsul üçün yoxlanmış addım olmadıqda natamam təlimat/blok yolu yoxdur; generasiya edilmiş addım təsdiqli təlimat deyil.',
 'forecast': 'Faktiki + yeyilməmiş hissə proqnozu, natamam gündəlik və başqa yeməyin porsiyasına ayrıca razılıq paneli yoxdur.',
 'swap': 'Tam hər iki günün əvvəl/sonra nutrientləri, yeni qramların razılığı, boş slota ayrıca move və uyğun namizəd olmayan günlər yoxdur.',
 'atomic': 'Versiya konflikti, yarımçıq yazı/retry və atomik iki gün əməliyyatının bərpa vəziyyətləri yoxdur.',
 'replace': 'Sıfır alternativ, naməlum allergen/qiymət/vaxt, yeni porsiya və istifadəçi qəbul etdiyi yumşaq şərt dəyişikliyi ayrıca yoxdur.',
 'undo': 'Undo zamanı sonradan yaranmış FoodLog konflikti, geri qaytarma xətası və alış-verişlə vahid nəticə ayrıca yoxdur.',
 'draft': 'Qaralamanı yenidən açma, baza versiyası konflikti, köhnəlmiş qaralama və offline/server təsdiqini ayıran nəticə yoxdur.',
 'photo-recipe': 'Xeyr qolunun ayrıca nəticəsi, əvvəldən fərdiləşdirilmiş son versiya, naməlum pay və yarımçıq foto qaralaması ayrıca yoxdur.',
 'model-conflict': 'Foto modeli ilə istifadəçinin dediyi zidd olduqda qısa təsdiq paneli yoxdur; model istifadəçi məlumatını səssiz dəyişə bilməz.',
 'withdrawal': 'Geri çəkilmiş resept/policy üçün xəbərdarlıq, yeni tövsiyənin bloklanması və keçmiş snapshot-ın izahı yoxdur.',
 'education': '12 maarif kartının tam mətn/mənbə/əhatə/istisna/baxış tarixi panelləri yoxdur.',
 'macro': 'MACRO_INFEASIBLE, natamam karbohidrat əsası, EFSA əsas nəticə ilə WHO advisory fərqi və mikrodata naməlumluğu yoxdur.',
 'floor': 'ENERGY_BELOW_PRODUCT_FLOOR/NO_TARGET, real porsiya həlli yoxdur və clamp etmədən gündəliyə davam ayrıca yoxdur.',
 'obsolete': '12-ci PNG-də yemək əhatəsi/avadanlıq/ev inventarı köhnədir: həmin suallar geri gətirilməməlidir; yalnız könüllü Evdə var işarəsi qalır.',
}

# Explicit per-item associations; nonvisual items are classified separately.
# A reference means a relevant surface, never full requirement coverage.
ANNOTATIONS = '''
1.1|05:19|scope
1.2|05:19|evidence
1.3|01:01 12:Büdcə|locale region
2.2|06:23|sync
2.4|12:Büdcə 04:16|budget
2.5|00:Əlavə-et 03:09|search camera
3.2|07:27|food-detail
3.3|10:38 D02:Dəqiq-miqdar|food-detail macro
3.5|10:38|food-detail
3.6|07:25|withdrawal
4.1|07:25-26|recipe-detail
4.2|07:25-26 D02:Qazan|recipe-detail portion
4.5|00:Porsiya D02:Qazan|measured portion
4.7|07:25|recipe-detail evidence
4.8|D02:Qazan|measured
5.1|01:02,04|inputs evidence
5.3|01:02|no-target
5.4|01:03|activity
5.5|01:04|goals floor
5.7|05:18|scope
5.8|01:04 05:19|evidence reassess
5.9|01:02|evidence
5.10|01:02|inputs no-target
5.11|01:04|macro evidence
5.12|12:Sağlamlıq|health
5.13|01:02 05:19|reassess no-target
5.14|01:02-04 12:Sağlamlıq|inputs no-target health
6.1|12:Sağlamlıq|health
6.2|12:Sağlamlıq|health scope
6.3|12:Sağlamlıq|health
6.4|02:05 14:Allergen|allergen
6.5|12:Sağlamlıq|health no-target
6.6|10:37 13:Menyu|generation no-target
7.1|04:13|rules evidence
7.2|04:13|rules
7.3|10:38|rules macro
7.4|04:13|generation time budget
7.5|02:05 13:Menyu|generation macro
7.6|00:Bu-gün 05:18|forecast reassess
8.1|01:02-03 02:05-06 12:Büdcə|health region budget obsolete
8.2|08:30|allergen generation
8.3|D03:Menyu 08:30|slots replace
8.4|D02:Qazan 08:30|portion rules
8.5|04:13,16|rules budget generation
8.6|D03:Menyu|recipe-detail evidence
8.7|07:26-28 08:29-32 11:41-43|editor swap replace undo
8.8|04:14 D02:Qazan 03:12|portion log write
8.9|05:18-19|weight reassess
8.10|03:11 10:39|model-conflict instructions
9.1|04:13|recipe-detail slots
9.2|07:25|recipe-detail instructions
10.1|01:04 05:19|evidence
10.2|07:25 D03:Menyu|recipe-detail evidence
10.3|00:Porsiya D02:Qazan|measured portion
10.4|03:11|oil camera
10.5|05:18|weight forecast
10.6|05:19|education
10.7|05:19|education evidence
11.5|03:12 06:23|write sync
11.6|05:19|education evidence
11.7|07:25|withdrawal evidence
12.2|08:30-32 10:37 11:42|replace swap shopping
12.4|03:10-11|camera oil measured
12.5|06:23 14:Yazı-xətası|write sync
12.6|03:09 D02:Qazan 08:29|search portion log swap
12.7|05:19|privacy
13.5|08:29-32 04:16 05:19|replace shopping education
14.4|13:Menyu|generation scope
15.4|10:38|macro food-detail
15.8|07:25 13:Menyu|withdrawal generation
16.1|07:25-27 D02:Qazan|editor portion
16.2|07:28 11:42|forecast allergen shopping
16.3|08:31-32 11:41|swap atomic
16.4|08:30 11:43|replace forecast
16.5|07:28 11:41,43|forecast undo atomic
16.6|07:25-28 10:39|custom-version
16.7|10:39|instructions
16.8|09:33-36 11:44|photo-recipe model-conflict log
16.9|07:25-28 08:29-32 09:33-36 10:37-40 11:41-44|editor swap photo-recipe
17.2|07:25|recipe-detail region
17.3|07:25|evidence
17.4|00:Məqsəd 01:02-03 02:05-06 12:Büdcə|region budget obsolete
17.5|00:Məqsəd 05:19|goals
17.6|07:26-28 08:30|editor allergen rules
17.7|08:30|budget region
17.8|04:16 11:42|shopping budget obsolete
17.9|04:16 08:30|budget replace
17.10|08:30 10:37|rules generation
17.11|07:28 11:42-43|replace allergen instructions budget
17.12|07:25|recipe-detail evidence
17.14|02:06 04:16 11:42-43|budget goals shopping replace
18.1|00:Məqsəd 02:05-08|goals auth
18.2|00:Məqsəd 05:19|goals
18.3|02:05 14:Allergen|allergen
18.4|02:05 14:İstəksizlik|dislike
18.5|02:06|time
18.6|02:06 12:Büdcə|budget obsolete
18.7|02:06 D03:Menyu|slots
18.8|02:06 12:Profil|generation reassess write
18.9|02:07|auth privacy
18.10|02:08|auth
18.11|00:Bu-gün,Əlavə-et,Porsiya D02:Qazan|search measured forecast
18.12|D03:Menyu 07:25 05:18-19|recipe-detail weight locale
18.13|00:Məqsəd 02:05-08|goals budget slots auth
19.1|00:Ümumi 01:01-04 02:05-08 03:09-12 04:13-16 05:17-20 06:21-24 07:25-28 08:29-32 09:33-36 10:37-40 11:41-44|locale
19.2|01:01-03|inputs no-target activity
19.3|01:04 12:Profil|generation no-target slots
19.4|03:09|search food-detail
19.5|03:10-11 06:24|camera oil
19.6|03:12 11:44|log forecast write
19.7|D03:Menyu 04:14 07:25|slots recipe-detail portion
19.8|04:16 11:42|shopping share obsolete
19.9|05:17-18 13:İrəliləyiş|weight
19.10|05:19-20|reminder privacy locale
19.11|06:21|billing
19.12|06:22-23 14:Yazı-xətası|write sync
19.13|07:26-28 09:34-36 D02:Qazan|editor food-detail portion
19.14|10:37-40|draft instructions atomic
19.15|11:41-44|undo shopping replace log
19.16|05:19|privacy
19.17|00:Ümumi|locale
19.18|05:17-20 04:16 06:21 10:40|weight reminder billing draft
19.19|01:02-04 02:05-08 03:09-12 04:13-16 05:17-20 06:21-24 10:40 11:41-44|locale inputs auth camera log weight reminder billing write share draft shopping undo
20.5|00:Məqsəd 01:02|no-target goals locale
20.6|12:Sağlamlıq|health scope
20.7|10:38|food-detail measured
20.9|13:Menyu|no-target generation
21.1|00:Əlavə-et 03:09 D02:Dəqiq-miqdar|search education evidence
21.2|13:Menyu 01:04|generation
21.3|05:19|no-target evidence
21.4|12:Sağlamlıq|health no-target
21.6|07:25 06:21|recipe-detail measured budget billing
21.7|D03:Menyu 07:25|withdrawal reassess
21.8|05:19|scope privacy
22.1|01:04|generation evidence
22.3|05:19|evidence rules
22.4|10:38 D02:Dəqiq-miqdar|food-detail
22.5|10:37-38|rules
22.6|10:38|food-detail macro
22.7|D02:Qazan|portion measured
22.8|D03:Menyu 08:30|generation replace
22.9|13:Menyu 10:37|generation rules
22.10|D03:Menyu 07:28|atomic evidence
22.11|00:Bu-gün 07:28 09:36|forecast custom-version
22.12|03:11 10:39|instructions model-conflict measured
23.1|06:23|sync evidence
23.2|07:27|food-detail allergen
23.5|10:38|food-detail allergen
23.6|07:25 05:19|recipe-detail privacy
23.7|05:19 07:25|evidence withdrawal
24.1|06:23|sync write
24.4|06:23 02:08|sync auth
24.5|06:23 06:24|sync camera generation
24.6|03:10|photo-consent privacy
24.7|05:19|privacy
24.8|D03:Menyu 07:25|withdrawal
25.1|12:Sağlamlıq 01:02-04|health no-target
25.3|12:Sağlamlıq 13:Menyu|health no-target generation
25.5|02:05 08:30|allergen
25.6|10:37-38|rules macro
25.7|00:Məqsəd 02:06 D03:Menyu|goals budget slots generation
25.8|13:Menyu 08:30|generation replace
25.9|07:26-28 08:31-32 09:33-36 11:41-44|editor swap undo photo-recipe portion
25.10|06:23 03:12|write sync log
25.11|05:19 06:21|privacy billing
25.12|07:25 D03:Menyu|withdrawal
26.2|01:04|evidence
26.4|01:03|activity
26.5|01:03|activity no-target
26.6|01:03|activity reassess
26.7|01:02 12:Sağlamlıq|health no-target
26.8|12:Sağlamlıq 01:02|health inputs
27.1|00:Məqsəd 01:04|goals floor
27.2|01:04|scope evidence
27.3|01:04 13:Menyu|floor
27.4|01:04|floor scope
27.5|13:Menyu|floor generation portion
27.6|13:Menyu|generation floor
27.7|02:06 D03:Menyu|slots
27.8|05:17-20 01:04|reassess weight
28.1|05:19|evidence macro
28.2|01:04|macro evidence
28.3|01:04|macro no-target
28.4|10:38|macro food-detail
28.5|D03:Menyu|macro rules
28.6|05:19|macro evidence
28.7|02:05 13:Menyu|macro generation scope
29.3|01:04|floor
29.4|01:02-04 12:Sağlamlıq 13:Menyu|health activity floor macro generation
'''

# Every N/A is intentional. New plan items must receive an explicit disposition.
NONVISUAL_GROUPS = [
    ('Mənbə/məzmun paketi və editorial sübut: dataset, mənşə, lisenziya, çıxım və baxış qeydləri tələb olunur; istehlakçı ekranı həmin işi sübut etmir.',
     '2.3 3.1 4.6 12.3 13.2 13.3 13.6 13.8 14.2 14.3 15.1 15.2 15.3 15.5 15.6 17.1 17.13 23.3 23.4 23.8'),
    ('Hesab/idxal/schema/migration/backend müqaviləsi: kod, DB və sərhəd testləri tələb olunur; ayrıca istehlakçı ekranı tələb edilmir.',
     '3.4 4.3 4.4 5.2 5.6 11.1 11.2 11.3 11.4 13.4 20.10 22.2 24.2 24.3 26.3 29.5'),
    ('Yoxlama protokolu və fixture: müstəqil hesab, test nəticəsi və ya pilot hesabatı tələb olunur; PNG bu sübutun əvəzi deyil.',
     '12.1 13.7 15.7 25.2 25.4 25.13 29.1 29.2 29.8'),
    ('Planın idarəetməsi, resurs/rol qərarı, status və prioritet tarixi: versiya, təsdiq/aktivləşmə izi və tələb olunan release sübutu ilə izlənir; ayrıca ekran deyil.',
     '1.4 2.1 2.6 13.1 14.1 20.1 20.2 20.3 20.4 20.8 21.5 25.14 26.1 29.6 29.7 30.1 30.2 30.3 30.4 30.5 30.6'),
]

STATUS_NOTES = {
    **{key: 'Status hissəsini §30.1–30.6 ilə birlikdə oxu: bildirilmiş baxış və enerji/makro aktivləşməsi var; kataloq və faktiki implementasiya qəbulunu əvəz etmir.'
       for key in ('2.1', '14.1', '14.4', '20.3', '20.9', '21.2', '26.1', '29.6', '29.7', '29.8')},
    **{key: '§17.4/17.8-in son qərarı üstün gəlir: yemək əhatəsi/avadanlıq/ev inventarı soruşulmur, dəqiq ev miqdarı funksiyası geri gətirilmir.'
       for key in ('8.1', '17.4', '17.8', '18.6', '19.8')},
    **{key: 'Köhnə üç aktivlik təsviri §26.4–26.6-dakı dörd kateqoriya və yenidən təsdiq ilə tamamlanmalıdır.'
       for key in ('5.4', '19.2', '26.4', '26.5', '26.6')},
}

T_REFS = {
 1: ('07:26 10:39', 'editor instructions'),
 2: ('07:26,28', 'forecast editor'),
 3: ('07:27-28 11:42', 'editor instructions shopping'),
 4: ('07:26 D02:Qazan', 'portion'),
 5: ('08:31-32 11:41', 'swap atomic'),
 6: ('10:37', 'swap'),
 7: ('08:30 11:43', 'replace'),
 8: ('10:38', 'food-detail rules'),
 9: ('08:31', 'swap log'),
 10: ('07:28 11:42', 'write atomic'),
 11: ('02:05 07:28', 'allergen log'),
 12: ('10:40 11:41', 'undo draft'),
 13: ('09:33,35-36', 'photo-recipe'),
 14: ('09:33-34,36', 'photo-recipe editor'),
 15: ('09:34,36', 'custom-version photo-recipe'),
 16: ('09:34', 'custom-version photo-recipe'),
 17: ('09:35-36 D02:Qazan', 'portion'),
 18: ('09:33-36', 'model-conflict'),
 19: ('09:36 11:44', 'write'),
}


def sha(path):
    return hashlib.sha256((ROOT / path).read_bytes()).hexdigest()


def refs(spec):
    result = []
    for entry in spec.split():
        sheet, panel = entry.split(':', 1)
        if sheet not in SHEETS:
            raise ValueError(f'Unknown sheet: {sheet}')
        result.append({'sheet': sheet, 'panel': panel})
    return result


def concept_supplements(rows):
    """Derive later draft coverage without rewriting the historical 17-image audit."""
    inventory = json.loads((ROOT / 'design-concepts/simple-turquoise/missing-state-pngs.json').read_text())
    requirements = {row['id'] for row in rows}
    supplements = []
    for entry in inventory['sets']:
        if not set(entry['requirements']).issubset(requirements):
            raise ValueError('Concept references an unknown requirement')
        image = (ROOT / entry['image']).resolve()
        if not image.is_relative_to((ROOT / 'design-concepts').resolve()):
            raise ValueError('Concept image must stay in design-concepts')
        if sha(image) != entry['sha256']:
            raise ValueError('Stale concept image hash')
        supplements.append(dict(id=entry['id'], image=entry['image'], sha256=entry['sha256'],
                                requirements=entry['requirements'],
                                states=[panel['state'] for panel in entry['panels']],
                                status=entry['review_status'],
                                note='Concept coverage only; historical gaps and runtime status remain open.'))
    return supplements


def build():
    source = 'planning/research-data.json'
    customization = 'planning/meal-customization-requirements.txt'
    data = json.loads((ROOT / source).read_text())
    annotations = {}
    for line in ANNOTATIONS.strip().splitlines():
        key, reference, gaps = line.split('|')
        if key in annotations:
            raise ValueError(f'Duplicate annotation: {key}')
        annotations[key] = (reference, gaps.split())
    nonvisual = {}
    for reason, keys in NONVISUAL_GROUPS:
        for key in keys.split():
            if key in nonvisual or key in annotations:
                raise ValueError(f'Duplicate disposition: {key}')
            nonvisual[key] = reason
    rows = []
    for n, section in enumerate(data['sections'], 1):
        for i, text in enumerate(section['items'], 1):
            key = f'{n}.{i}'
            annotation = annotations.pop(key, None)
            nonvisual_reason = nonvisual.pop(key, None)
            if annotation is None and nonvisual_reason is None:
                raise ValueError(f'New/unmapped plan requirement: {key}')
            reference, gaps = annotation if annotation else ('', [])
            row = {
                'id': key, 'source_pointer': f'/sections/{n-1}/items/{i-1}',
                'section_id': section['id'], 'section_title': section['title'],
                'requirement': text, 'requirement_sha256': hashlib.sha256(text.encode()).hexdigest(),
                'png_refs': refs(reference), 'missing_design_states': gaps,
                'design_status': 'partial_reference' if reference else 'not_a_screen_requirement',
                'runtime_status': 'not_audited',
                'precedence_note': STATUS_NOTES.get(key),
                'nonvisual_evidence_required': (
                    'Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.'
                    if reference else
                    nonvisual_reason
                ),
            }
            rows.append(row)
    if annotations or nonvisual:
        raise ValueError(f'Orphan dispositions: {list(annotations) + list(nonvisual)}')
    t_lines = re.findall(r'^(T\d{2}): (.+)$', (ROOT / customization).read_text(), re.M)
    if [key for key, _ in t_lines] != [f'T{i:02}' for i in range(1, 20)]:
        raise ValueError('T01–T19 are missing, reordered or duplicated')
    for key, text in t_lines:
        reference, gaps = T_REFS[int(key[1:])]
        rows.append({
            'id': key, 'source_anchor': f'{customization}#{key}',
            'section_title': 'Resept fərdiləşdirməsi — T01–T19',
            'requirement': text, 'png_refs': refs(reference),
            'missing_design_states': gaps.split(), 'design_status': 'partial_reference',
            'runtime_status': 'not_audited',
            'nonvisual_evidence_required': 'Ssenarinin əvvəl/sonra DB, plan, alış-veriş və FoodLog nəticəsi ayrıca testlə təsdiqlənməlidir.',
        })
    for row in rows:
        if not set(row['missing_design_states']) <= GAPS.keys():
            raise ValueError(f'Unknown gap in {row["id"]}')
    images = {key: {
        'path': DESIGN + filename, 'panels': panels, 'sha256': sha(DESIGN + filename),
        'inspection': '2026-11-06: agent opened a downscaled copy of this exact PNG; concept inventory only',
    } for key, (filename, panels) in SHEETS.items()}
    return {
        'schema_version': 1, 'audit_date': '2026-11-06',
        'scope': '30 bölmədəki bütün nömrəli bəndlər və istinad edilən T01–T19. Bəndin bütün mətni saxlanır; mürəkkəb bəndlər bir neçə PNG/boşluqla əlaqələnir.',
        'exclusions': 'Mənbə reyestri, xülasələr və week sintetik məzmunu ayrıca ekran tələbi deyil. nutrition_policy_candidate-in UI nəticələri §26–30/NP bəndləri ilə əhatə olunur; obyektin field-by-field runtime auditi aparılmır.',
        'source_hashes': {source: sha(source), customization: sha(customization),
                          'design-concepts/tokens.css': sha('design-concepts/tokens.css'),
                          **{DESIGN + name: sha(DESIGN + name) for name in (
                              'implementation-guide.txt', 'new-flows-guide.txt', 'ui-extension-guide.txt',
                          'approved/d02-portions.txt', 'approved/d03-menu.txt')}},
        'precedence': [
            'Cari istifadəçi qərarı → cari kanonik plan → T01–T19 → icra növbəsi → PNG nümunəsi.',
            '§30 əvvəlki dietoloq yoxdur/deaktiv policy statusunu yeniləyir: SPECIALIST_APPROVAL_REPORTED və production_enabled=true; bu, resept kataloqunun buraxılışı deyil.',
            '§17.4/17.8: onboarding-də yemək əhatəsi/avadanlıq/ev inventarı soruşulmur; dəqiq ev miqdarı funksiyası tam çıxarılıb.',
            '§26: köhnə 01/03 panelindəki üç aktivlik dörd EER kateqoriyasının təsdiqi deyil.',
            'D02/D03 təsdiqli layout istiqamətidir; keçmiş runtime qəbulu yalnız docs/qa/design-discrepancies.md-dəki konkret commitə aiddir.',
        ],
        'definitions': {
            'partial_reference': 'Əlaqəli ekran/panel var; bütün bənd və vəziyyətlər üçün yetərli deyil.',
            'not_a_screen_requirement': 'PNG=N/A əsaslandırılıb; bu status tələbin implementasiya olunduğunu bildirmir.',
            'missing_design_states': 'Bu 17 konsept PNG-də ayrıca təsvir edilməyən tələb olunan vəziyyətlər; tətbiqin çatışmazlıq hökmü deyil.',
            'runtime_status': 'Bu işdə implementasiya/CI/cihaz/vizual qəbul yoxlanmayıb. Heç bir bənd hazır kimi bağlanmır.',
        },
        'images': images, 'gap_catalog': GAPS,
        'counts': {'plan_items': len(rows)-19, 'customization_scenarios': 19,
                   'total': len(rows), 'pngs_inspected': len(images),
                   **dict(Counter(row['design_status'] for row in rows))},
        'rows': rows,
        'concept_supplements': concept_supplements(rows),
    }


def markdown(report):
    counts = report['counts']
    out = ['# Plan → PNG → çatışmayan vəziyyət', '',
           '6 noyabr 2026. Sənəd konsept əhatəsinin xəritəsidir; işlək tətbiqin qəbul hesabatı deyil.', '',
           f'Əhatə: **{counts["plan_items"]} plan bəndi + 19 ssenari = {counts["total"]} sətir**, faktiki açılmış 17 PNG.', '',
           'Bu fayl `planning/build_design_requirement_map.py` ilə yaradılır. Maşın görünüşü: [design-requirement-map.json](design-requirement-map.json).', '',
           '## Oxuma qaydası', '',
           'Hər sətir kanonik bəndin tam mətnini saxlayır. PNG-də əlaqəli səth olması bütün davranışların dizayn edildiyi demək deyil. '
           'Boşluq ID-ləri aşağıdakı konkret vəziyyətlərə açılır. Hər bəndin runtime statusu **yoxlanmayıb**dır. '
           'PNG=N/A texniki, məzmun və sübut tələbinin atılması deyil; həmin tələb ekranla təsdiqlənə bilməz.', '',
           f'Əlaqəli, qismən istinad: {counts["partial_reference"]}; ayrıca ekran tələbi olmayan: {counts["not_a_screen_requirement"]}. '
           'Tam dizayn və ya runtime qəbulu verilmiş sətir: 0.', '',
           'Scope: ' + report['scope'], '', report['exclusions'], '', '## Üstünlük və köhnəlmiş PNG hissələri', '']
    out.extend('- '+text for text in report['precedence'])
    out += ['', '## PNG reyestri', '']
    for key, image in report['images'].items():
        out += [f'- **{key}** — [{image["path"]}](../../{image["path"]}): {image["panels"]}.']
    out += ['', '## Sonrakı konsept əlavələri', '']
    for entry in report['concept_supplements']:
        out += [f"- **{entry['id']}** — [{entry['image']}](../../{entry['image']}): "
                + ', '.join(entry['states']) + '. Tələblər: ' + ', '.join(entry['requirements'])
                + '. Status: ' + entry['status'] + '; runtime qəbulu deyil.']
    out += ['', '17 faylın kiçildilmiş nüsxələri açılıb baxılıb; tam rezolyusiyada piksel/kontrast auditi aparılmayıb. '
            'Orijinal PNG və plan SHA-256-ları JSON-da saxlanır. Bu hash-lər runtime vizual qəbulu əvəz etmir.', '',
            '## Konkret çatışmayan vəziyyətlər', '']
    for key, text in report['gap_catalog'].items():
        used = [row['id'] for row in report['rows'] if key in row['missing_design_states']]
        out += [f'### {key}', '', text, '', 'Tələblər: '+', '.join(used)+'.', '']
    out += ['## Bəndlər üzrə tam xəritə', '']
    last = None
    for row in report['rows']:
        if last != row['section_title']:
            last = row['section_title']
            out += ['### '+last, '']
        references = '; '.join(f'{r["sheet"]} / {r["panel"]}' for r in row['png_refs'])
        gap_links = ', '.join(f'[{gap}](#{gap})' for gap in row['missing_design_states'])
        out += [f'**{row["id"]}** — {row["requirement"]}', '',
                f'- PNG → {references or "N/A — ayrıca ekran tələbi deyil"}.',
                f'- Çatışmayan vəziyyət → {gap_links or "N/A — proqram/məzmun/sübut tələbi"}.',
                '- Ayrı sübut → '+row['nonvisual_evidence_required'], '']
        if row.get('precedence_note'):
            out += ['- Cari qərar → '+row['precedence_note'], '']
    out += ['## Yoxlama və davam', '',
            '`python3 planning/build_design_requirement_map.py --check` mənbə mətnləri/ID-lər, PNG mövcudluğu və hash-lər, '
            'T01–T19 bütövlüyü, annotasiya əlaqələri və törəmə faylların cari olmasını yoxlayır. '
            'Bu, dizaynın semantik tamlığı, klinik baxış və tətbiq davranışının testi deyil.', '',
            'Növbəti implementasiya üçün müvafiq bəndin boşluqlarını ayrıca screen/state renderlərinə ayır, '
            '`visual-review-plan.json`-a konkret requirement/state/reference və AZ/EN/RU × 100%/150% halları əlavə et. '
            'Hazır ekranın cari qəbulunu `design-discrepancies.md` və main-ə bağlı manifest/review sübutu ilə yoxla. '
            'Bu xəritə mövcud vizual borcu bağlamır və avtomatik baseline yaratmır.', '']
    return '\n'.join(out)


def main():
    parser = argparse.ArgumentParser(__doc__)
    parser.add_argument('--check', action='store_true')
    args = parser.parse_args()
    report = build()
    outputs = {
        DEST / 'design-requirement-map.json': json.dumps(report, ensure_ascii=False, indent=2)+'\n',
        DEST / 'design-requirement-map.md': markdown(report),
    }
    for path, expected in outputs.items():
        if args.check:
            if path.read_text() != expected:
                raise SystemExit(f'Stale design map: {path.relative_to(ROOT)}')
        else:
            path.write_text(expected)
    print(json.dumps(report['counts'], ensure_ascii=False))


if __name__ == '__main__':
    main()
