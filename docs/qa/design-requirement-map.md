# Plan → PNG → çatışmayan vəziyyət

6 noyabr 2026. Sənəd konsept əhatəsinin xəritəsidir; işlək tətbiqin qəbul hesabatı deyil.

Əhatə: **255 plan bəndi + 19 ssenari = 274 sətir**, faktiki açılmış 17 PNG.

Bu fayl `planning/build_design_requirement_map.py` ilə yaradılır. Maşın görünüşü: [design-requirement-map.json](design-requirement-map.json).

## Oxuma qaydası

Hər sətir kanonik bəndin tam mətnini saxlayır. PNG-də əlaqəli səth olması bütün davranışların dizayn edildiyi demək deyil. Boşluq ID-ləri aşağıdakı konkret vəziyyətlərə açılır. Hər bəndin runtime statusu **yoxlanmayıb**dır. PNG=N/A texniki, məzmun və sübut tələbinin atılması deyil; həmin tələb ekranla təsdiqlənə bilməz.

Əlaqəli, qismən istinad: 208; ayrıca ekran tələbi olmayan: 66. Tam dizayn və ya runtime qəbulu verilmiş sətir: 0.

Scope: 30 bölmədəki bütün nömrəli bəndlər və istinad edilən T01–T19. Bəndin bütün mətni saxlanır; mürəkkəb bəndlər bir neçə PNG/boşluqla əlaqələnir.

Mənbə reyestri, xülasələr və week sintetik məzmunu ayrıca ekran tələbi deyil. nutrition_policy_candidate-in UI nəticələri §26–30/NP bəndləri ilə əhatə olunur; obyektin field-by-field runtime auditi aparılmır.

## Üstünlük və köhnəlmiş PNG hissələri

- Cari istifadəçi qərarı → cari kanonik plan → T01–T19 → icra növbəsi → PNG nümunəsi.
- §30 əvvəlki dietoloq yoxdur/deaktiv policy statusunu yeniləyir: SPECIALIST_APPROVAL_REPORTED və production_enabled=true; bu, resept kataloqunun buraxılışı deyil.
- §17.4/17.8: onboarding-də yemək əhatəsi/avadanlıq/ev inventarı soruşulmur; dəqiq ev miqdarı funksiyası tam çıxarılıb.
- §26: köhnə 01/03 panelindəki üç aktivlik dörd EER kateqoriyasının təsdiqi deyil.
- D02/D03 təsdiqli layout istiqamətidir; keçmiş runtime qəbulu yalnız docs/qa/design-discrepancies.md-dəki konkret commitə aiddir.

## PNG reyestri

- **00** — [design-concepts/simple-turquoise/00-reference.png](../../design-concepts/simple-turquoise/00-reference.png): Bu gün; Əlavə et; Porsiya; Menyu; İrəliləyiş; Məqsəd.
- **01** — [design-concepts/simple-turquoise/01-baslangic.png](../../design-concepts/simple-turquoise/01-baslangic.png): 01 Xoş gəldin; 02 Özünü tanıt; 03 Aktivlik; 04 Planın hazırdır.
- **02** — [design-concepts/simple-turquoise/02-secimler-ve-hesab.png](../../design-concepts/simple-turquoise/02-secimler-ve-hesab.png): 05 Allergiya; 06 Vaxt/büdcə/yemək sayı; 07 Planını itirmə; 08 Giriş.
- **03** — [design-concepts/simple-turquoise/03-yemek-qeydiyyati.png](../../design-concepts/simple-turquoise/03-yemek-qeydiyyati.png): 09 Axtarış; 10 Kamera; 11 Foto namizədi/yağ; 12 Əlavə olundu.
- **04** — [design-concepts/simple-turquoise/04-menyu-ve-alisveris.png](../../design-concepts/simple-turquoise/04-menyu-ve-alisveris.png): 13 Menyu; 14 Resept; 15 Alternativ; 16 Alış-veriş.
- **05** — [design-concepts/simple-turquoise/05-irelileyis-ve-profil.png](../../design-concepts/simple-turquoise/05-irelileyis-ve-profil.png): 17 Çəki; 18 Həftəlik baxış; 19 Profil; 20 Xatırlatma.
- **06** — [design-concepts/simple-turquoise/06-abunelik-ve-veziyyetler.png](../../design-concepts/simple-turquoise/06-abunelik-ve-veziyyetler.png): 21 Abunəlik; 22 Boş Bu gün; 23 Offline lokal saxlanma; 24 Tanınmayan foto.
- **07** — [design-concepts/simple-turquoise/07-inqrediyent-redaktesi.png](../../design-concepts/simple-turquoise/07-inqrediyent-redaktesi.png): 25 Resept girişi; 26 Redaktor; 27 Məhsul seçici; 28 Önizləmə.
- **08** — [design-concepts/simple-turquoise/08-yemek-ve-gun-deyisme.png](../../design-concepts/simple-turquoise/08-yemek-ve-gun-deyisme.png): 29 Əməliyyat seçimi; 30 Alternativlər; 31 Gün seçimi; 32 İki günün təsdiqi.
- **09** — [design-concepts/simple-turquoise/09-foto-sonrasi-resept.png](../../design-concepts/simple-turquoise/09-foto-sonrasi-resept.png): 33 Bəli/xeyr; 34 Faktiki tərkib; 35 Qazan/şəxsi pay; 36 Faktiki önizləmə.
- **10** — [design-concepts/simple-turquoise/10-deyisiklik-ve-xeta-hallari.png](../../design-concepts/simple-turquoise/10-deyisiklik-ve-xeta-hallari.png): 37 Uyğunsuz dəyişmə; 38 Natamam nutrient; 39 Yeni addımlar; 40 Qaralama çıxışı.
- **11** — [design-concepts/simple-turquoise/11-yenilenmis-plan-ve-tesdiq.png](../../design-concepts/simple-turquoise/11-yenilenmis-plan-ve-tesdiq.png): 41 Undo; 42 Alış-veriş fərqi; 43 Alternativ önizləmə; 44 Faktiki qeyd.
- **12** — [design-concepts/simple-turquoise/12-elave-profil-secimleri.png](../../design-concepts/simple-turquoise/12-elave-profil-secimleri.png): Sağlamlıq; Büdcə və mətbəx; Evdə nə var; Profil saxlanıb.
- **13** — [design-concepts/simple-turquoise/13-bos-veziyyetler.png](../../design-concepts/simple-turquoise/13-bos-veziyyetler.png): Boş Menyu; Əlavə et bağlı; Boş İrəliləyiş; Hesab hazır deyil.
- **14** — [design-concepts/simple-turquoise/14-elave-formalar-ve-veziyyetler.png](../../design-concepts/simple-turquoise/14-elave-formalar-ve-veziyyetler.png): Əlavə allergen/məhdudiyyət; İstəksizlik; Yüklənir; Lokal yazı xətası.
- **D02** — [design-concepts/simple-turquoise/approved/d02-portions.png](../../design-concepts/simple-turquoise/approved/d02-portions.png): Resept qazan/şəxsi pay; Dəqiq miqdar/naməlum enerji.
- **D03** — [design-concepts/simple-turquoise/approved/d03-menu.png](../../design-concepts/simple-turquoise/approved/d03-menu.png): Tarixli menyu/qismən yeyilib; Boş menyu; Razılaşdırılmış qərarlar.

## Sonrakı konsept əlavələri

- **15-activity** — [design-concepts/simple-turquoise/15-aktivlik-ve-hedefsiz-davam.png](../../design-concepts/simple-turquoise/15-aktivlik-ve-hedefsiz-davam.png): unselected-four-options, low-active-help, legacy-reconfirmation, uncertain-no-target. Tələblər: 26.4, 26.5, 26.6, 5.3. Status: agent-inspected-draft-user-approval-pending; runtime qəbulu deyil.
- **16-activity-help** — [design-concepts/simple-turquoise/16-aktivlik-izahlari.png](../../design-concepts/simple-turquoise/16-aktivlik-izahlari.png): inactive-help, active-help, very-active-help. Tələblər: 26.4, 26.5. Status: agent-inspected-draft-user-approval-pending; runtime qəbulu deyil.
- **17-no-target** — [design-concepts/simple-turquoise/17-hedefsiz-davam-sebebleri.png](../../design-concepts/simple-turquoise/17-hedefsiz-davam-sebebleri.png): missing-sex, health-excluded, model-out-of-scope. Tələblər: 5.3, 21.3, 25.3, 26.7, 26.8. Status: agent-inspected-draft-user-approval-pending; runtime qəbulu deyil.
- **18-no-target-calculation** — [design-concepts/simple-turquoise/18-hedefsiz-hesablama-sebebleri.png](../../design-concepts/simple-turquoise/18-hedefsiz-hesablama-sebebleri.png): energy-below-product-floor, macro-infeasible. Tələblər: 27.3, 28.3. Status: agent-inspected-draft-user-approval-pending; runtime qəbulu deyil.
- **24-ingredient-editor-errors** — [design-concepts/simple-turquoise/24-inqrediyent-redaktoru-xetalar.png](../../design-concepts/simple-turquoise/24-inqrediyent-redaktoru-xetalar.png): empty-recipe, blank-negative-quantity, zero-removal-confirmation, allergen-conflict, unknown-allergen. Tələblər: 16.1, 16.2, 16.9. Status: agent-inspected-draft-user-approval-pending; runtime qəbulu deyil.
- **19-completion** — [design-concepts/simple-turquoise/completion/19-telimat-yoxdur.png](../../design-concepts/simple-turquoise/completion/19-telimat-yoxdur.png): 19-A-Bu variantın təlimatı yoxdur, 19-B-İlkin reseptə qayıt?. Tələblər: 16.3, 19.14. Status: agent-inspected-draft-user-approval-pending; runtime qəbulu deyil.
- **20-completion** — [design-concepts/simple-turquoise/completion/20-gun-hefte-onbaxis.png](../../design-concepts/simple-turquoise/completion/20-gun-hefte-onbaxis.png): 20-A-Günün yenilənmiş planı, 20-B-Həftəyə təsiri. Tələblər: 16.2, 16.6, 19.15. Status: agent-inspected-draft-user-approval-pending; runtime qəbulu deyil.
- **21-completion** — [design-concepts/simple-turquoise/completion/21-deyisme-yollari.png](../../design-concepts/simple-turquoise/completion/21-deyisme-yollari.png): 21-A-Uyğun alternativ tapılmadı, 21-B-Porsiyanı uyğunlaşdıraq?, 21-C-Bu hissə artıq yeyilib, 21-D-Bu günə köçür. Tələblər: 16.4, 16.5, T06, T09. Status: agent-inspected-draft-user-approval-pending; runtime qəbulu deyil.
- **22-completion** — [design-concepts/simple-turquoise/completion/22-plan-konfliktleri.png](../../design-concepts/simple-turquoise/completion/22-plan-konfliktleri.png): 22-A-Faktiki qeyd qorunacaq, 22-B-Plan başqa cihazda dəyişib. Tələblər: 16.6, 16.8, T12. Status: agent-inspected-draft-user-approval-pending; runtime qəbulu deyil.
- **23-completion** — [design-concepts/simple-turquoise/completion/23-foto-deqiqlesdirme.png](../../design-concepts/simple-turquoise/completion/23-foto-deqiqlesdirme.png): 23-A-Bu foto hansı yeməkdir?, 23-B-Bir şeyi dəqiqləşdirək, 23-C-Nə qədər yedin?. Tələblər: 16.9, T18, T13. Status: agent-inspected-draft-user-approval-pending; runtime qəbulu deyil.
- **24-completion** — [design-concepts/simple-turquoise/completion/24-alisveris-hallari.png](../../design-concepts/simple-turquoise/completion/24-alisveris-hallari.png): 24-A-Alınmış məhsul saxlanıb, 24-B-Siyahın hələ boşdur, 24-C-Paylaşılacaq mətn. Tələblər: 16.6, 19.8. Status: agent-inspected-draft-user-approval-pending; runtime qəbulu deyil.
- **25-completion** — [design-concepts/simple-turquoise/completion/25-hesab-formalari.png](../../design-concepts/simple-turquoise/completion/25-hesab-formalari.png): 25-A-Yeni hesab yarat, 25-B-Qeydiyyat alınmadı, 25-C-Bərpa linki işləmir, 25-D-Yeni şifrə seç. Tələblər: 18.9, 18.10. Status: agent-inspected-draft-user-approval-pending; runtime qəbulu deyil.
- **26-completion** — [design-concepts/simple-turquoise/completion/26-komek-formalari.png](../../design-concepts/simple-turquoise/completion/26-komek-formalari.png): 26-A-Foto kalorisi niyə təxminidir?, 26-B-Dəstəyə müraciət. Tələblər: 19.16. Status: agent-inspected-draft-user-approval-pending; runtime qəbulu deyil.
- **27-completion** — [design-concepts/simple-turquoise/completion/27-baxis-ve-bos-siyahilar.png](../../design-concepts/simple-turquoise/completion/27-baxis-ve-bos-siyahilar.png): 27-A-Hədəfə yenidən baxaq, 27-B-Əvvəl yediklərim, 27-C-Saxlanmış reseptlər, 27-D-Foto siyahısı. Tələblər: 27.8, 19.3, 19.16. Status: agent-inspected-draft-user-approval-pending; runtime qəbulu deyil.
- **28-completion** — [design-concepts/simple-turquoise/completion/28-vahid-vizual-qaydalar.png](../../design-concepts/simple-turquoise/completion/28-vahid-vizual-qaydalar.png): 28-A-Bugünkü menyun, 28-B-Qida dəyərlərinin əsası, 28-C-Bişmiş çəki məlum deyil. Tələblər: 18.12, 19.17, 28.4. Status: agent-inspected-draft-user-approval-pending; runtime qəbulu deyil.

17 faylın kiçildilmiş nüsxələri açılıb baxılıb; tam rezolyusiyada piksel/kontrast auditi aparılmayıb. Orijinal PNG və plan SHA-256-ları JSON-da saxlanır. Bu hash-lər runtime vizual qəbulu əvəz etmir.

## Konkret çatışmayan vəziyyətlər

### scope

Əhatə/disclaimer izahı, tibbi vəd verməyən mətn və peşəkar dəstəyə keçid üçün ayrıca panel yoxdur.

Tələblər: 1.1, 5.7, 6.2, 14.4, 20.6, 21.8, 27.2, 27.4, 28.7.

### evidence

Formula/mənbə/versiya/tarix, qeyri-müəyyənlik və faktiki baxış statusunu açan tam detal paneli yoxdur.

Tələblər: 1.2, 4.7, 5.1, 5.8, 5.9, 5.11, 7.1, 8.6, 10.1, 10.2, 10.7, 11.6, 11.7, 17.3, 17.12, 21.1, 21.3, 22.1, 22.3, 22.10, 23.1, 23.7, 26.2, 27.2, 28.1, 28.2, 28.6.

### locale

AZ/EN/RU × 100%/150%, ən uzun etiketlər, yuxarı/orta/aşağı sürüşmə, ekran oxuyucu və klaviatura vəziyyətləri ayrıca göstərilmir.

Tələblər: 1.3, 18.12, 19.1, 19.10, 19.17, 19.19, 20.5.

### region

Region/valyuta seçicisinin açıq vəziyyəti, dəyişməsi və qiymətsiz region nəticəsi yoxdur; dil region əvəzi deyil.

Tələblər: 1.3, 8.1, 17.2, 17.4, 17.7.

### inputs

Boş/mənfi/sonlu olmayan giriş, onluq vergül, vahid anlaşılmazlığı, xəta və cavabları saxlayan geri dönüş paneli yoxdur.

Tələblər: 5.1, 5.10, 5.14, 19.2, 19.19, 26.8.

### no-target

Cins/yaş/aktivlik çatışmır və ya əhatədən kənardır: avtomatik hədəfsiz gündəlik və mənşəli mütəxəssis hədəfi yolları ayrıca çəkilməyib.

Tələblər: 5.3, 5.10, 5.13, 5.14, 6.5, 6.6, 19.2, 19.3, 20.5, 20.9, 21.3, 21.4, 25.1, 25.3, 26.5, 26.7, 28.3.

### activity

Dörd EER seçimi, ayrıca low_active, tam təsvirlər, ACTIVITY_UNCERTAIN və köhnə üç seçimin yenidən təsdiqi yoxdur.

Tələblər: 5.4, 19.2, 26.4, 26.5, 26.6, 29.4.

### health

Bütün tətbiq olunan risk sualları, cavabsız vəziyyət, INELIGIBLE/INCOMPLETE/MODEL_OUT_OF_SCOPE və hədəfsiz davam ayrıca göstərilmir.

Tələblər: 5.12, 5.14, 6.1, 6.2, 6.3, 6.5, 8.1, 20.6, 21.4, 25.1, 25.3, 26.7, 26.8, 29.4.

### allergen

Cavabsız/çoxlu allergiya, yoxdur ilə qarşılıqlı istisna, konkret qoz-fındıq, naməlum allergen və blok səbəbi ayrıca çəkilməyib.

Tələblər: 6.4, 8.2, 16.2, 17.6, 17.11, 18.3, 23.2, 23.5, 25.5, T11.

### dislike

İstəksizliyi əlavə et/dəyiş/sil, hamısına uyğunsuz nəticə və seçimi ləğv etmədən alternativ yolu yoxdur.

Tələblər: 18.4.

### time

Vaxtı naməlum və ya limitə uyğun resepti olmayan nəticə, aktiv/gözləmə vaxtının izahı ayrıca yoxdur.

Tələblər: 7.4, 18.5.

### budget

Məbləğ/nəfər/valyuta validasiyası, köhnə/yox qiymət, naməlum əhatə, büdcə aşılması və istifadəçi qəbul etdiyi alternativ ayrıca yoxdur.

Tələblər: 2.4, 7.4, 8.1, 8.5, 17.4, 17.7, 17.8, 17.9, 17.11, 17.14, 18.6, 18.13, 21.6, 25.7.

### goals

Dörd məqsədin nəticə fərqləri, healthy-də defisitsiz nəticə, GOAL_OUT_OF_SCOPE və məqsəd dəyişməsinin önizləməsi yoxdur.

Tələblər: 5.5, 17.5, 17.14, 18.1, 18.2, 18.13, 20.5, 25.7, 27.1.

### slots

3/4/5 slotlu bütöv menyular və slot sayını dəyişərkən sabit günlük hədəf ayrıca göstərilməyib.

Tələblər: 8.3, 9.1, 18.7, 18.13, 19.3, 19.7, 25.7, 27.7.

### generation

SUCCESS-dən başqa NO_FEASIBLE_PLAN, INCOMPLETE_DATA, POLICY_UNAVAILABLE, STALE_INPUT, TEMPORARY_FAILURE üçün ayrı səbəb/bərpa panelləri yoxdur.

Tələblər: 6.6, 7.4, 7.5, 8.2, 8.5, 14.4, 15.8, 17.10, 18.8, 19.3, 20.9, 21.2, 22.1, 22.8, 22.9, 24.5, 25.3, 25.7, 25.8, 27.5, 27.6, 28.7, 29.4.

### rules

PASS/FAIL/UNKNOWN/NOT_APPLICABLE, nutrient əhatəsi və sərt/yumşaq qaydanı ayıran istifadəçi izahları ayrıca göstərilmir.

Tələblər: 7.1, 7.2, 7.3, 8.4, 8.5, 17.6, 17.10, 22.3, 22.5, 22.9, 25.6, 28.5, T08.

### reassess

Yeni yaş/çəki/aktivlik üçün səbəbli əvvəl/sonra önizləmə, təsdiq/ləğv və yalnız gələcəyə tətbiq yoxdur; 14 günlük baxış/natamam ölçmə və simptom yolu yoxdur.

Tələblər: 5.8, 5.13, 7.6, 8.9, 18.8, 21.7, 26.6, 27.8.

### food-detail

Tam qida mənbəyi, xam/bişmiş variant, nutrient vahidləri, məlum/naməlum sahələr və məhsul/etiket variantının detalı yoxdur.

Tələblər: 3.2, 3.3, 3.5, 15.4, 19.4, 19.13, 20.7, 22.4, 22.6, 23.2, 23.5, 28.4, T08.

### recipe-detail

Tam versiya/allergen/çıxım/saxlanma təlimatı, müəllif/hüquq/status detalı və seçilmişlərə əlavə/çıxarma vəziyyətləri yoxdur.

Tələblər: 4.1, 4.2, 4.7, 8.6, 9.1, 9.2, 10.2, 17.2, 17.12, 18.12, 19.7, 21.6, 23.6.

### portion

Məlum/naməlum çıxım, məhsula məxsus vahid çevirməsi, səhv/çox böyük miqdar və qalan pay sərhədi üçün ayrıca panellər yoxdur.

Tələblər: 4.2, 4.5, 8.4, 8.8, 10.3, 12.6, 16.1, 19.7, 19.13, 22.7, 25.9, 27.5, T04, T17.

### measured

Az/Orta/Çox üçün real çəkilmiş, eyni qab/bucaqda kalibrə olunmuş resept fotoları bu konsept dəstində yoxdur; illüstrasiya ölçü sübutu deyil.

Tələblər: 4.5, 4.8, 10.3, 12.4, 18.11, 20.7, 21.6, 22.7, 22.12.

### search

Axtarışın yüklənmə/boş/xəta və təkrar cəhd vəziyyətləri, tam əl ilə qida forması və əvvəl yediklərim siyahısı yoxdur.

Tələblər: 2.5, 12.6, 18.11, 19.4, 21.1.

### camera

İcazə rəddi, qalereya rəddi/ləğvi, çəkiliş/yükləmə xətası, tanıma gözləməsi və axtarışa dönüş ayrıca yoxdur.

Tələblər: 2.5, 10.4, 12.4, 19.5, 19.19, 24.5.

### oil

Var seçiminin məhsul/miqdar forması, artıq hesablanan yağın iki dəfə sayılmaması və Başqa yeməkdir qolunun tam davamı yoxdur.

Tələblər: 10.4, 12.4, 19.5.

### log

Konkret qeydin düzəliş forması, silmə təsdiqi, hədəfsiz/natamam qalan enerji və neytral aşma nəticəsi ayrıca yoxdur.

Tələblər: 8.8, 12.6, 16.8, 19.6, 19.15, 19.19, 25.10, T09, T11.

### write

Lokal yazı xətası nümunəsi var, amma hər əməliyyatda məzmunu qoruyan retry, iki toxunuş, restart və yalnız dayanıqlı yazıdan sonra uğur vəziyyəti ayrıca göstərilmir.

Tələblər: 8.8, 11.5, 12.5, 18.8, 19.6, 19.12, 19.19, 24.1, 25.10, T10, T19.

### sync

Lokal-only/pending/synced/failed, quota/token xətası, revision konflikti və silinmiş qeydin köhnə cihazdan bərpasının qarşısı üçün UI ayrılmayıb.

Tələblər: 2.2, 11.5, 12.5, 19.12, 23.1, 24.1, 24.4, 24.5, 25.10.

### auth

Qeydiyyat, şifrə sıfırlama, səhv giriş, provayder ləğvi, qonaq məlumatının köçürülməsi və başqa cihazda bərpa/konflikt panelləri yoxdur.

Tələblər: 18.1, 18.9, 18.10, 18.13, 19.19, 24.4.

### weight

Tarix seçici, əvvəlki ölçmələr, düzəliş/silmə, eyni gün əvəzləmə/ayrı ölçmə, boş həftə və tək nöqtə nəticəsi ayrıca yoxdur.

Tələblər: 8.9, 10.5, 18.12, 19.9, 19.18, 19.19, 27.8.

### reminder

Vaxt seçici, 4/5 slot, icazə rəddi, gecə yarısını keçən sakit saatlar, saat qurşağı dəyişməsi və saxlanma xətası yoxdur.

Tələblər: 19.10, 19.18, 19.19.

### billing

Aktiv/bitmiş/ləğvi planlı/pending/uğursuz/bərpa/mağaza xətası, real qiymət yüklənməsi və entitlement yoxlaması ayrıca yoxdur; eskiz qiyməti müqavilə deyil.

Tələblər: 19.11, 19.18, 19.19, 21.6, 25.11.

### privacy

Kömək/FAQ/dəstək/yanlış qida bildirişi, məxfilik, ixrac, çıxış, hesab silmə təsdiqi və nəticə panelləri yoxdur.

Tələblər: 12.7, 18.9, 19.10, 19.16, 21.8, 23.6, 24.6, 24.7, 25.11.

### photo-consent

Fotodan əvvəl məqsəd/razılıq/retention/silmə və yükləməni ləğv etmə paneli yoxdur.

Tələblər: 24.6.

### shopping

Evdə var və Alınıb ayrı idarələr, ümumi həftəlik miqdar, qablaşdırma xərci, naməlum çevirmə və plan artıq tələb etməyən alınmış məhsul ayrıca yoxdur.

Tələblər: 12.2, 13.5, 16.2, 17.8, 17.14, 19.8, 19.15, 19.19, T03.

### share

Sistem paylaşım önbaxışı, ləğv və xəta vəziyyətləri yoxdur; paylaşım avtomatik göndərmə deyil.

Tələblər: 19.8, 19.19.

### editor

0/boş/mənfi/NaN/çox böyük qram, bütün inqrediyentlərin silinməsi, vahid çevirməsinin olmaması və genişlənmiş Digər ərzaqlar ayrıca yoxdur.

Tələblər: 8.7, 16.1, 16.9, 17.6, 19.13, 25.9, T01, T02, T03, T14.

### custom-version

Dəyişən reseptdə əvvəlki təsdiq/çıxım/foto/vaxtın qüvvədən düşməsi, ayrıca istifadəçi versiyası və tarixçənin sabit qalması tam göstərilmir.

Tələblər: 16.6, 22.11, T15, T16.

### instructions

Yeni məhsul üçün yoxlanmış addım olmadıqda natamam təlimat/blok yolu yoxdur; generasiya edilmiş addım təsdiqli təlimat deyil.

Tələblər: 8.10, 9.2, 16.7, 17.11, 19.14, 22.12, T01, T03.

### forecast

Faktiki + yeyilməmiş hissə proqnozu, natamam gündəlik və başqa yeməyin porsiyasına ayrıca razılıq paneli yoxdur.

Tələblər: 7.6, 10.5, 16.2, 16.4, 16.5, 18.11, 19.6, 22.11, T02.

### swap

Tam hər iki günün əvvəl/sonra nutrientləri, yeni qramların razılığı, boş slota ayrıca move və uyğun namizəd olmayan günlər yoxdur.

Tələblər: 8.7, 12.2, 12.6, 16.3, 16.9, 25.9, T05, T06, T09.

### atomic

Versiya konflikti, yarımçıq yazı/retry və atomik iki gün əməliyyatının bərpa vəziyyətləri yoxdur.

Tələblər: 16.3, 16.5, 19.14, 22.10, T05, T10.

### replace

Sıfır alternativ, naməlum allergen/qiymət/vaxt, yeni porsiya və istifadəçi qəbul etdiyi yumşaq şərt dəyişikliyi ayrıca yoxdur.

Tələblər: 8.3, 8.7, 12.2, 13.5, 16.4, 17.9, 17.11, 17.14, 19.15, 22.8, 25.8, T07.

### undo

Undo zamanı sonradan yaranmış FoodLog konflikti, geri qaytarma xətası və alış-verişlə vahid nəticə ayrıca yoxdur.

Tələblər: 8.7, 16.5, 19.15, 19.19, 25.9, T12.

### draft

Qaralamanı yenidən açma, baza versiyası konflikti, köhnəlmiş qaralama və offline/server təsdiqini ayıran nəticə yoxdur.

Tələblər: 19.14, 19.18, 19.19, T12.

### photo-recipe

Xeyr qolunun ayrıca nəticəsi, əvvəldən fərdiləşdirilmiş son versiya, naməlum pay və yarımçıq foto qaralaması ayrıca yoxdur.

Tələblər: 16.8, 16.9, 25.9, T13, T14, T15, T16.

### model-conflict

Foto modeli ilə istifadəçinin dediyi zidd olduqda qısa təsdiq paneli yoxdur; model istifadəçi məlumatını səssiz dəyişə bilməz.

Tələblər: 8.10, 16.8, 22.12, T18.

### withdrawal

Geri çəkilmiş resept/policy üçün xəbərdarlıq, yeni tövsiyənin bloklanması və keçmiş snapshot-ın izahı yoxdur.

Tələblər: 3.6, 11.7, 15.8, 21.7, 23.7, 24.8, 25.12.

### education

12 maarif kartının tam mətn/mənbə/əhatə/istisna/baxış tarixi panelləri yoxdur.

Tələblər: 10.6, 10.7, 11.6, 13.5, 21.1.

### macro

MACRO_INFEASIBLE, natamam karbohidrat əsası, EFSA əsas nəticə ilə WHO advisory fərqi və mikrodata naməlumluğu yoxdur.

Tələblər: 3.3, 5.11, 7.3, 7.5, 15.4, 22.6, 25.6, 28.1, 28.2, 28.3, 28.4, 28.5, 28.6, 28.7, 29.4.

### floor

ENERGY_BELOW_PRODUCT_FLOOR/NO_TARGET, real porsiya həlli yoxdur və clamp etmədən gündəliyə davam ayrıca yoxdur.

Tələblər: 5.5, 27.1, 27.3, 27.4, 27.5, 27.6, 29.3, 29.4.

### obsolete

12-ci PNG-də yemək əhatəsi/avadanlıq/ev inventarı köhnədir: həmin suallar geri gətirilməməlidir; yalnız könüllü Evdə var işarəsi qalır.

Tələblər: 8.1, 17.4, 17.8, 18.6, 19.8.

## Bəndlər üzrə tam xəritə

### 1. Nə qururuq və elmi əsas nə deməkdir?

**1.1** — İlk məhsul sağlam yetkinlərin qidalanmanı planlamasına və qeyd etməsinə kömək edir. Xəstəlik diaqnozu, müalicə dietası, vitamin çatışmazlığı diaqnozu və dərman məsləhəti ilk versiyanın funksiyası deyil.

- PNG → 05 / 19.
- Çatışmayan vəziyyət → [scope](#scope).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**1.2** — Elmi mənbəyə söykənən hesablama, mütəxəssisin yoxladığı resept və klinik təsdiqlənmiş məhsul ayrı anlayışlardır. İstinad vermək tətbiqin effektivliyini avtomatik sübut etmir.

- PNG → 05 / 19.
- Çatışmayan vəziyyət → [evidence](#evidence).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**1.3** — İlk bazar üçün AZ/EN/RU məzmun nəzərdə tutulur. Dil ölkə deyil: ərzaq əlçatanlığı və qiymət üçün ölkə/region ayrıca soruşulur.

- PNG → 01 / 01; 12 / Büdcə.
- Çatışmayan vəziyyət → [locale](#locale), [region](#region).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**1.4** — Versiya 2.0, 5 noyabr 2026: docs/nutrition-plan-design.md ilə əvvəlki 1.7 planı birləşdirilib. Kanonik mənbə planning/research-data.json; MD, TXT, PDF və canvas ondan yaradılır. Bölmə 20–25 ziddiyyət həllərini, yeni müqavilələri və qəbul ssenarilərini tamamlayır; §1–19 və T01–T19 qüvvədədir.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Planın idarəetməsi, resurs/rol qərarı, status və prioritet tarixi: versiya, təsdiq/aktivləşmə izi və tələb olunan release sübutu ilə izlənir; ayrıca ekran deyil.

### 2. Həqiqətən lazım olan resurslar

**2.1** — Hazırkı resurs vəziyyəti: dietoloq yoxdur. Mənbəli gündəlik, texniki hesablama və ümumi maarifləndirmə yolu inkişaf etdirilə bilər. Avtomatik fərdi tövsiyə üçün mövcud G2/G3 klinik/məzmun yoxlaması tələbi saxlanır; mənbə linki, sahibin yoxlaması və AI bu təsdiqin əvəzi deyil. Tərəzi, ölçü qabı, məzmun müəllifi və versiyalı redaktor axını tələb olunur.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Planın idarəetməsi, resurs/rol qərarı, status və prioritet tarixi: versiya, təsdiq/aktivləşmə izi və tələb olunan release sübutu ilə izlənir; ayrıca ekran deyil.

- Cari qərar → Status hissəsini §30.1–30.6 ilə birlikdə oxu: bildirilmiş baxış və enerji/makro aktivləşməsi var; kataloq və faktiki implementasiya qəbulunu əvəz etmir.

**2.2** — Qida bazasının idxalı və ya serverdən API istifadəsi ilə başlayırıq. Mobil tətbiq birbaşa USDA açarı daşımır. Təsdiqlənmiş məlumatın öz nüsxəsi və mənbə tarixi saxlanır; xarici API dayananda gündəlik işləyir.

- PNG → 06 / 23.
- Çatışmayan vəziyyət → [sync](#sync).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**2.3** — Reseptlər üçün öz test mətbəximiz və ya razılaşdığımız aşpaz/məzmun müəllifi. İstənilən saytdan tapılmış resept və foto avtomatik etibarlı qida məlumatı sayılmır. Mətn və şəkillərin istifadə hüququ qeydə alınır.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Mənbə/məzmun paketi və editorial sübut: dataset, mənşə, lisenziya, çıxım və baxış qeydləri tələb olunur; istehlakçı ekranı həmin işi sübut etmir.

**2.4** — Qiymət üçün ilkin mərhələdə seçilmiş mağazalarda sənədləşdirilmiş qiymət müşahidələri: məhsul, qablaşdırma, tarix, region, valyuta. Büdcə kateqoriyası təxmini olur. Canlı mağaza inteqrasiyası və dəqiq səbət qiyməti ayrıca layihədir.

- PNG → 12 / Büdcə; 04 / 16.
- Çatışmayan vəziyyət → [budget](#budget).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**2.5** — Şəkil tanıma modeli yalnız əlavə modul kimi qoşulur. Məhsul foto olmadan da axtarış, əl ilə qeyd, resept və menyu ilə işləməlidir.

- PNG → 00 / Əlavə-et; 03 / 09.
- Çatışmayan vəziyyət → [search](#search), [camera](#camera).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**2.6** — Nəticə: yeni qida API-lərinə abunə almaqdan əvvəl yerli resept və yoxlama işinə resurs ayırırıq.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Planın idarəetməsi, resurs/rol qərarı, status və prioritet tarixi: versiya, təsdiq/aktivləşmə izi və tələb olunan release sübutu ilə izlənir; ayrıca ekran deyil.

### 3. Qida bazasını necə yaradacağıq?

**3.1** — 4 noyabr 2026 yeniləməsi: 500 unikal mənbə qeydi USDA ID-sinə bağlanıb; 224 reseptin qramla inqrediyentləri və AZ hazırlanma addımları yazılıb. SQLite-də 8 217 USDA istinad qeydi var: 7 793 SR Legacy və 424 Foundation Foods. Xam/bişmiş variantlar və məhsul növləri ayrı qeydlərdir.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Mənbə/məzmun paketi və editorial sübut: dataset, mənşə, lisenziya, çıxım və baxış qeydləri tələb olunur; istehlakçı ekranı həmin işi sübut etmir.

**3.2** — Hər ərzaq: daxili ID, mənbə/FDC ID, AZ/EN/RU adları və sinonimlər, yeyilən hissə, xam/bişmiş vəziyyət, 100 qramda enerji, zülal, yağ, karbohidrat; mövcuddursa lif, natrium, doymuş yağ, ümumi/əlavə şəkər; ölçü vahidi, mənbə versiyası və keyfiyyət statusu.

- PNG → 07 / 27.
- Çatışmayan vəziyyət → [food-detail](#food-detail).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**3.3** — Məlumat yoxdursa null saxlanır; 0 yazılmır. Ümumi şəkər, əlavə şəkər və sərbəst şəkər bir-birinin yerinə istifadə olunmur. Natrium və duz ayrılır; çevirmə əmsalı və vahid açıq göstərilir.

- PNG → 10 / 38; D02 / Dəqiq-miqdar.
- Çatışmayan vəziyyət → [food-detail](#food-detail), [macro](#macro).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**3.4** — Qida komponentləri mənbədəki identifikator və vahid ilə xəritələnir. Bir API cavabındakı sahə mövqeyinə bağlanmırıq. İki enerji göstəricisi varsa mənbə üzrə seçmə siyasəti tətbiq olunur; sadəcə birincisi götürülmür.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Hesab/idxal/schema/migration/backend müqaviləsi: kod, DB və sərhəd testləri tələb olunur; ayrıca istehlakçı ekranı tələb edilmir.

**3.5** — Yerli qablaşdırılmış məhsul üçün istehsalçı etiketinin fotosu, barkod varsa barkod, tarix və məhsul variantı saxlanır. Generik bazadakı qatıq bütün yerli qatıqların tam ekvivalenti deyil.

- PNG → 10 / 38.
- Çatışmayan vəziyyət → [food-detail](#food-detail).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**3.6** — Statuslar: qaralama → yoxlanır → təsdiqlənib → dərc olunub → arxiv. Mənbə yenilənəndə keçmiş yemək qeydlərinin rəqəmləri dəyişmir.

- PNG → 07 / 25.
- Çatışmayan vəziyyət → [withdrawal](#withdrawal).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

### 4. Yerli yeməyi ölçülən reseptə necə çeviririk?

**4.1** — Bir resept üçün əvvəl tərkib və üsul sabitləşir: ətin hissəsi, düyünün quru çəkisi, yağ, duz, su, sous və əlavə məhsullar. Toyuq plovunun fərqli üsulları ayrıca resept variantıdır.

- PNG → 07 / 25-26.
- Çatışmayan vəziyyət → [recipe-detail](#recipe-detail).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**4.2** — İnqrediyentlərin yeyilən hissəsinin qramları yazılır; sümük, qabıq və atılan hissə ayrılır. İstifadəçiyə həmin miqdarlar və hazırlanma addımları verilir. Bişmiş 100 qram hesabı lazımdırsa yekun çıxım ölçülür və ya eyni tərkib/üsula uyğun mənbə çıxım əmsalı ilə təxmini sənədləşdirilir.

- PNG → 07 / 25-26; D02 / Qazan.
- Çatışmayan vəziyyət → [recipe-detail](#recipe-detail), [portion](#portion).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**4.3** — Nutrient toplamı = Σ(yeyilən inqrediyent qramı × 100 qram üçün nutrient / 100 × uyğun saxlanma əmsalı). Sonra bişmiş yekun çəkiyə bölünərək 100 qram və porsiya hesablanır. Hazır bişmiş komponent seçilirsə eyni bişmə düzəlişi ikinci dəfə tətbiq edilmir.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Hesab/idxal/schema/migration/backend müqaviləsi: kod, DB və sərhəd testləri tələb olunur; ayrıca istehlakçı ekranı tələb edilmir.

**4.4** — Daxili metodika nümunəsi: bütün qazana hesablanmış 2 400 kkal və ölçülmüş 1 600 q hazır yemək varsa, 100 q = 150 kkal; 280 q porsiya = 420 kkal. Bu arifmetik nümunədir, toyuq plovu üçün təsdiqlənmiş nəticə deyil.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Hesab/idxal/schema/migration/backend müqaviləsi: kod, DB və sərhəd testləri tələb olunur; ayrıca istehlakçı ekranı tələb edilmir.

**4.5** — Hər resept üzrə Az / Orta / Çox real qramlara bağlanır və həmin miqdarların fotoşəkli çəkilir. Məsələn 180/280/380 q yalnız konkret resept testinin namizədidir; bütün yeməklərə tətbiq olunmur. İstifadəçi istəsə qramla yaza bilir.

- PNG → 00 / Porsiya; D02 / Qazan.
- Çatışmayan vəziyyət → [measured](#measured), [portion](#portion).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**4.6** — İnqrediyentlər və elan edilən porsiya sayı ilə təxmini hesab üçün bütün reseptləri özümüz bişirmək universal şərt deyil. Test bişirməsi çıxımın, şəkilli porsiyanın və qeyri-müəyyən reseptlərin dəqiqləşdirilməsi üçün seçilir. Ölçülmüş çəki nutrient laborator analizi demək deyil.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Mənbə/məzmun paketi və editorial sübut: dataset, mənşə, lisenziya, çıxım və baxış qeydləri tələb olunur; istehlakçı ekranı həmin işi sübut etmir.

**4.7** — Dərc kartı: resept ID/versiya, inqrediyentlər, yekun çəki, porsiya aralığı, hesablanmış nutrientlər, allergenlər, hazırlıq vaxtı, qiymət mənbəyi, porsiya fotoları, müəllif və yoxlayan şəxs/tarix.

- PNG → 07 / 25.
- Çatışmayan vəziyyət → [recipe-detail](#recipe-detail), [evidence](#evidence).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**4.8** — Vizual eskizlərdəki hazır yemək şəkilləri ölçü istinadı sayılmır. Porsiya müqayisəsi üçün çəkilmiş real eyni qab/eyni bucaq fotoları hazırlanmalıdır; illüstrativ şəkillər ayrıca işarələnir.

- PNG → D02 / Qazan.
- Çatışmayan vəziyyət → [measured](#measured).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

### 5. Kalori hədəfi və tibbi ölçülər

**5.1** — İstifadəçinin daxil etdiyi yaş, boy və çəki ölçmə/məlumat girişidir. İstirahət enerji sərfi formulla proqnozlanır. Gündəlik enerji ehtiyacı isə aktivlik modeli ilə əlavə təxmin edilir. Ekran “metabolizmini ölçdük” demir.

- PNG → 01 / 02,04.
- Çatışmayan vəziyyət → [inputs](#inputs), [evidence](#evidence).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**5.2** — Namizəd Mifflin–St Jeor formulu, kkal/gün: kişilər üçün 10×çəki(kq)+6,25×boy(sm)−5×yaş+5; qadınlar üçün eyni baza−161. İlkin tədqiqatın əhatəsi və fərdi xəta nəzərə alınır. Bu, TDEE formulu deyil. Konkret yeni namizəd §26–29-da birbaşa NASEM 2023 EER-dir; Mifflin mövcud K08/test snapshot-ları üçündür, yeni policy-yə avtomatik köçürülmür.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Hesab/idxal/schema/migration/backend müqaviləsi: kod, DB və sərhəd testləri tələb olunur; ayrıca istehlakçı ekranı tələb edilmir.

**5.3** — Formulun tələb etdiyi məlumat könüllü buraxılırsa, tətbiq gizli olaraq kişi/qadın fərz etmir. Avtomatik dəqiq hədəf əvəzinə ümumi menyu/gündəlik və ya mütəxəssisin verdiyi hədəfi daxil etmə yolu açılır. Əvvəlki onboarding eskizində bu davranış əlavə edilməlidir.

- PNG → 01 / 02.
- Çatışmayan vəziyyət → [no-target](#no-target).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**5.4** — Aktivlik seçimləri mütəxəssisin təsdiqlədiyi izah və əmsallara bağlanır. İnternetdə yayılmış əmsal cədvəli elmi olaraq bu məhsul üçün yoxlanmış sayılmır. NIDDK-nin PAL rəqəmləri də başqa modelə kor-koranə köçürülmür. §26 dörd EER kateqoriyası üzrə konkret təklif verir; köhnə üç seçim yenidən təsdiqlənir.

- PNG → 01 / 03.
- Çatışmayan vəziyyət → [activity](#activity).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

- Cari qərar → Köhnə üç aktivlik təsviri §26.4–26.6-dakı dörd kateqoriya və yenidən təsdiq ilə tamamlanmalıdır.

**5.5** — Arıqlama/saxlama/çəki artırma üçün enerji düzəlişi, minimum hədlər, üst hədlər, xüsusi yaş qrupları və yenidən hesablama qaydası ayrıca NutritionPolicy-də təsdiqlənir. Bu sənəd hamıya avtomatik −500 kkal və ya universal 1 200/1 500 həddi qoymur. §27 artıq konkret −500/+300 və 1500/1800 namizəd məhsul qaydalarını, dar əhatəni və NO_TARGET davranışını təyin edir; bunlar G2 təsdiqi deyil.

- PNG → 01 / 04.
- Çatışmayan vəziyyət → [goals](#goals), [floor](#floor).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**5.6** — Zülal üçün EFSA-nın sağlam yetkinlərə aid 0,83 q/kq/gün PRI göstəricisi istinaddır. Bu rəqəm hər istifadəçiyə optimal fərdi hədəf və ya idman proqramı deyil. Çəki bazası, məqsəd və aktivlik üzrə hədəf mütəxəssis tərəfindən seçilir. §28 konkret max/remainder makro alqoritmini və cari çəki bazasını təklif edir.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Hesab/idxal/schema/migration/backend müqaviləsi: kod, DB və sərhəd testləri tələb olunur; ayrıca istehlakçı ekranı tələb edilmir.

**5.7** — BMI göstərilərsə çəki/boy² hesablanır və fərdi sağlamlıq diaqnozu kimi verilmir. Bədən yağı faizi, qan şəkəri, insulin və vitamin çatışmazlığı fotodan və adi çəki qeydindən çıxarılmır. Bu ölçülər ilkin versiyada vəd edilmir.

- PNG → 05 / 18.
- Çatışmayan vəziyyət → [scope](#scope).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**5.8** — Hər nəticədə formula_version, policy_version, daxil edilmiş məlumat və tarix saxlanır. Formula dəyişəndə yeni hesablamanın səbəbi göstərilir.

- PNG → 01 / 04; 05 / 19.
- Çatışmayan vəziyyət → [evidence](#evidence), [reassess](#reassess).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**5.9** — Yaş və cinsin elmi rolu: EFSA qida ehtiyaclarının yaş, cins, aktivlik və fizioloji vəziyyətə görə dəyişə bildiyini bildirir; onun referensləri qidalanma mütəxəssislərinin qiymətləndirmə və planlaşdırmasına əsas verir. NIDDK enerji planlayıcısı və USDA-nın mütəxəssislər üçün DRI kalkulyatoru yaş və cinsi boy, çəki və aktivliklə birlikdə soruşur. Bu iki giriş təkbaşına şəxsin pəhrizini müəyyən etmir (S4, S7, S17).

- PNG → 01 / 02.
- Çatışmayan vəziyyət → [evidence](#evidence).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**5.10** — Məhsul qərarı: avtomatik fərdi enerji hədəfi üçün seçilmiş formulun tələb etdiyi yaş, hesablama üçün cins, boy, çəki və aktivlik tam və etibarlı olmalıdır. Kişi/Qadın seçimi və yaş nəticəyə həqiqətən ötürülür; ad, avatar və fotodan fərz edilmir. Cins sahəsini buraxmaq tətbiqdən istifadəni bloklamır, lakin cins tələb edən formula ilə avtomatik hədəf yaradılmır; ümumi gündəlik/menyu və ya mütəxəssisin verdiyi hədəf yolu göstərilir. Bu, məlumatı nəzərə almamaq deyil, məlum olmayan girişi uydurmamaqdır.

- PNG → 01 / 02.
- Çatışmayan vəziyyət → [inputs](#inputs), [no-target](#no-target).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**5.11** — Yaş və cins yalnız kalori üçün deyil: nutrient referensi tətbiq olunursa uyğun yaş/cins/həyat mərhələsi üzrə, həmin nutrientin öz mənbə cədvəlindən seçilir. Bütün nutrientlərə bir ümumi kişi/qadın əmsalı vurulmur; WHO-nun ümumi qaydası ilə fərdi referens qarışdırılmır. Vitamin/mineral məlumatı kitabxanada çatmırsa tam təminat vədi verilmir; çatışmazlıq diaqnozu və əlavə preparatı avtomatik tövsiyə edilmir (S4, S17).

- PNG → 01 / 04.
- Çatışmayan vəziyyət → [macro](#macro), [evidence](#evidence).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**5.12** — Fizioloji vəziyyət və əhatə: hamiləlik və südvermə ayrıca nəzərə alınır, təkcə Qadın seçimi ilə müəyyən edilmir. 18 yaşdan kiçiklər, hamilə/südverənlər üçün ilkin məhsulun avtomatik fərdi plan məhdudiyyəti bölmə 6-da saxlanır. Hər yaşın sadəcə 18+ olması formulun etibarlı əhatəsinə daxil olması demək deyil; modelin tədqiqat əhatəsi və yaş sərhədləri NutritionPolicy-də yoxlanır. Yaşlı şəxsə avtomatik daha sərt defisit verilmir; mənbə dəstəyi olmayan ayrıca yaş qaydası uydurulmur.

- PNG → 12 / Sağlamlıq.
- Çatışmayan vəziyyət → [health](#health).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**5.13** — Məlumat müqaviləsi: UserProfile hesablamada istifadə edilən age_years, age_as_of_date, sex_for_equation(male/female/unspecified), height_cm, weight_kg, activity_level, eligibility_status və profile_version saxlayır. Tam doğum tarixi yalnız ehtiyac varsa toplanır; yaşı yeniləmək üçün təsdiq yolu olur. NutritionPolicy-də formula_required_inputs, model_age_scope və nutrient_reference_mapping mənbə/versiya ilə saxlanır. Formula kateqoriyası uyğun gəlmirsə tətbiq özü kateqoriya təyin etmir, mütəxəssis hədəfi yolu qalır. Dəyişiklik gələcək hədəf/porsiya üçün səbəb və təsdiqlə yeni versiya yaradır; keçmiş qida qeydləri dəyişmir.

- PNG → 01 / 02; 05 / 19.
- Çatışmayan vəziyyət → [reassess](#reassess), [no-target](#no-target).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**5.14** — Qəbul yoxlamaları: eyni boy/çəki/aktivlikdə yaş dəyişəndə seçilmiş formula yenidən işləyir; yalnız cins kateqoriyası dəyişəndə formulun uyğun sabiti seçilir. Mifflin namizədi üçün eyni digər girişlərdə kişi və qadın REE fərqi 166 kkal/gündür; bu, universal gündəlik qəbul fərqi deyil və aktivlik/enerji siyasəti ayrıca tətbiq olunur. Yaş və ya tələb olunan cins boşdursa avtomatik formula işləmir; 18 yaşdan aşağı, model əhatəsindən kənar, hamiləlik/südvermə və məlumat yeniləmə halları ayrıca yoxlanır. Bunlar tətbiqdə hələ icra edilməmiş qəbul ssenariləridir.

- PNG → 01 / 02-04; 12 / Sağlamlıq.
- Çatışmayan vəziyyət → [inputs](#inputs), [no-target](#no-target), [health](#health).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

### 6. Kimə avtomatik plan veriləcək?

**6.1** — İlkin buraxılış üçün məhsul qərarı: 18 yaşdan yuxarı, xüsusi tibbi dietası olmayan sağlam yetkinlər. Yaşın yalnız 18+ olması bütün modellərin həmin şəxs üçün etibarlı olduğunu sübut etmir; mütəxəssis təsdiqli əhatə tətbiq edilir.

- PNG → 12 / Sağlamlıq.
- Çatışmayan vəziyyət → [health](#health).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**6.2** — 18 yaşdan kiçik, hamilə/südverən, qidalanma pozuntusu ilə bağlı risk bildirən və ya xəstəliyə görə xüsusi qidalanma planı tələb edən şəxs üçün avtomatik arıqlama/çəki artırma planı açılmır; mütəxəssis istiqaməti verilir.

- PNG → 12 / Sağlamlıq.
- Çatışmayan vəziyyət → [health](#health), [scope](#scope).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**6.3** — Böyrək xəstəliyi, insulin və ya qidalanmaya təsir edən müalicə kimi hallar üçün ilkin versiyada klinik menyu və makro hədəfi yaratmamaq konservativ məhsul qərarıdır. Bu qısa suallar diaqnoz qoyan test deyil.

- PNG → 12 / Sağlamlıq.
- Çatışmayan vəziyyət → [health](#health).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**6.4** — Allergiya seçimi ayrıca ciddi filtrdir. Reseptdə allergen və ya tərkib məlum deyilsə, həmin allergiyaya uyğun təklif kimi göstərilmir. Şəkil gizli inqrediyent və çarpaz təması təsdiqləyə bilmir.

- PNG → 02 / 05; 14 / Allergen.
- Çatışmayan vəziyyət → [allergen](#allergen).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**6.5** — Sağlamlıq sualları mümkün qədər az, aydın və məqsədə uyğun soruşulur. Cavab verməyən şəxs üçün təhlükəsiz sadə yol və məhdudiyyət açıq göstərilir.

- PNG → 12 / Sağlamlıq.
- Çatışmayan vəziyyət → [health](#health), [no-target](#no-target).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**6.6** — Sərt istisnanı keçmək üçün istifadəçidən daha aşağı təhlükəsizlik seçimi etməsi tələb olunmur. Uyğun plan yoxdursa, səbəb və məqbul alternativ göstərilir.

- PNG → 10 / 37; 13 / Menyu.
- Çatışmayan vəziyyət → [generation](#generation), [no-target](#no-target).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

### 7. Həftəlik menyunun keyfiyyət qaydaları

**7.1** — WHO yetkinlər üçün ümumi çərçivə verir: meyvə-tərəvəz ≥400 q/gün, təbii lif ≥25 q/gün, sərbəst şəkər <10% enerji, doymuş yağ ≤10%, trans yağ ≤1%, duz <5 q/gün. Bunlar əhali referensləridir; fərdi tətbiq və qida qrupu xəritəsi policy-də sənədləşir. Mənbə: 26 yanvar 2026 vərəqi, 5 noyabr 2026-da yenidən yoxlanıb.

- PNG → 04 / 13.
- Çatışmayan vəziyyət → [rules](#rules), [evidence](#evidence).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**7.2** — Bunlar hər istifadəçiyə resept deyil. Kliniki istisnalar və seçilmiş əhali qrupu ayrıca idarə edilir. WHO ilə EFSA-nın müxtəlif referenslərini “ən sərt rəqəmi seç” üsulu ilə birləşdirmirik.

- PNG → 04 / 13.
- Çatışmayan vəziyyət → [rules](#rules).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**7.3** — Qida bazasında lif/duz məlumatı çatmırsa menyu həmin göstərici üzrə “yoxlanıb” sayılmır. Sərbəst şəkəri ümumi şəkərdən avtomatik çıxarmaq olmaz; resept inqrediyentlərinin ayrıca xəritəsi lazımdır.

- PNG → 10 / 38.
- Çatışmayan vəziyyət → [rules](#rules), [macro](#macro).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**7.4** — İlkin mühərrik üçün təklif: eyni əsas yeməyi ardıcıl günlərdə verməmək, həftə ərzində tərəvəz, taxıl və zülal mənbələrini dəyişmək; vaxtı və büdcəni nəzərə almaq. Təkrar sayı və enerji toleransı məhsul parametrləridir, tibbi standart deyil.

- PNG → 04 / 13.
- Çatışmayan vəziyyət → [generation](#generation), [time](#time), [budget](#budget).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**7.5** — Vegan, vegeterian və digər məhdud seçimlər üçün yetərli təsdiqlənmiş resept yoxdursa “tam balanslı fərdi plan” vədi verilmir. Həmin qrup üçün ayrıca kitabxana və mütəxəssis yoxlaması tamamlanır.

- PNG → 02 / 05; 13 / Menyu.
- Çatışmayan vəziyyət → [generation](#generation), [macro](#macro).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**7.6** — Bir günlük menyunu ertəsi gün avtomatik cəza ilə azaltmırıq. İstifadəçinin həqiqətən nə yediyi və gündəliyin tamlığı təsdiqlənmədən kompensasiya hesablanmır.

- PNG → 00 / Bu-gün; 05 / 18.
- Çatışmayan vəziyyət → [forecast](#forecast), [reassess](#reassess).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

### 8. Menyu mühərrikinin konkret axını

**8.1** — 1) Profil: uyğunluq, məqsəd, hədəf, allergiya, yemədikləri, region, büdcə, vaxt və yemək sayı alınır. Onboarding ev avadanlığı və ərzaq inventarı soruşmur.

- PNG → 01 / 02-03; 02 / 05-06; 12 / Büdcə.
- Çatışmayan vəziyyət → [health](#health), [region](#region), [budget](#budget), [obsolete](#obsolete).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

- Cari qərar → §17.4/17.8-in son qərarı üstün gəlir: yemək əhatəsi/avadanlıq/ev inventarı soruşulmur, dəqiq ev miqdarı funksiyası geri gətirilmir.

**8.2** — 2) Sərt filtr: təsdiqlənməmiş reseptlər, məlum olmayan allergen tərkibi, qadağan məhsullar və uyğunsuz porsiyalar çıxarılır.

- PNG → 08 / 30.
- Çatışmayan vəziyyət → [allergen](#allergen), [generation](#generation).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**8.3** — 3) Namizədlər: seçilmiş 3/4/5 yemək slotu üçün uyğun reseptlər seçilir. Hər gün üçün hər yeməyə ən azı bir real alternativ tapılıb-tapılmadığı yoxlanır.

- PNG → D03 / Menyu; 08 / 30.
- Çatışmayan vəziyyət → [slots](#slots), [replace](#replace).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**8.4** — 4) Porsiya: resept üzrə mümkün qram intervalı və addım daxilində günün enerjisi və nutrient qaydalarına yaxın variantlar hesablanır. Bütün tələbləri ödəməyən qida seçimini gizlincə “uyğun” etmirik.

- PNG → D02 / Qazan; 08 / 30.
- Çatışmayan vəziyyət → [portion](#portion), [rules](#rules).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**8.5** — 5) Həftə yoxlaması: enerji/nutrient yoxlamaları, müxtəliflik, vaxt, təxmini büdcə və alış-veriş yükü yenidən hesablanır. Büdcə yumşaq şərti ödənmirsə səbəb deyilir; allergiya heç vaxt yumşaldılmır.

- PNG → 04 / 13,16.
- Çatışmayan vəziyyət → [rules](#rules), [budget](#budget), [generation](#generation).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**8.6** — 6) Nəticə: plan_id, yeddi gün, recipe_version, portion_g, nutrient snapshot, policy_version və başa düşülən səbəb saxlanır: “30 dəqiqəyə hazırdır və seçdiyin məhsullara uyğundur”.

- PNG → D03 / Menyu.
- Çatışmayan vəziyyət → [recipe-detail](#recipe-detail), [evidence](#evidence).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**8.7** — 7) Dəyişmə: inqrediyenti çıxar/əlavə et/miqdarını dəyiş; başqa günün yeməyi ilə qarşılıqlı dəyiş; həmin günə uyğun alternativ seç. Yalnız seçilmiş hissələr, proqnoz toplamları və alış-veriş yenilənir. Qalan həftə və yeyilmiş qeydlər dəyişmir. Ətraflı qaydalar 16-cı bölmədədir.

- PNG → 07 / 26-28; 08 / 29-32; 11 / 41-43.
- Çatışmayan vəziyyət → [editor](#editor), [swap](#swap), [replace](#replace), [undo](#undo).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**8.8** — 8) Gündəlik: yalnız “Yedim” → porsiya təsdiqi → unikal yemək qeydi. Planın görüntülənməsi kalori qəbuluna yazılmır.

- PNG → 04 / 14; D02 / Qazan; 03 / 12.
- Çatışmayan vəziyyət → [portion](#portion), [log](#log), [write](#write).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**8.9** — 9) Həftə sonu: qeydlərin tamlığı və istifadəçi rəyi qiymətləndirilir. Yeni plan seçimlərə uyğunlaşdırılır; çəki hədəfi və kalori dəyişməsi ayrıca izah və təsdiq tələb edir.

- PNG → 05 / 18-19.
- Çatışmayan vəziyyət → [weight](#weight), [reassess](#reassess).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**8.10** — AI üçün icazəli rol: təsdiqli namizədləri tapmaq, AZ/EN/RU sadə izahı hazırlamaq, foto namizədi vermək. Hesablamalar, allergen filtri və tibbi hədlər yoxlanmış kodda icra edilir; modelin sərbəst cavabına buraxılmır.

- PNG → 03 / 11; 10 / 39.
- Çatışmayan vəziyyət → [model-conflict](#model-conflict), [instructions](#instructions).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

### 9. İlk 7 günlük məzmun nüvəsi

**9.1** — Bu menyu hamı üçün hazır pəhriz deyil. Hər sətir test ediləcək reseptlərə bölünür; enerjisi, allergenləri və porsiyası ayrıca hesablanır. Qəlyanaltı, meyvə və süd alternativləri ehtiyaca görə sonradan əlavə edilir.

- PNG → 04 / 13.
- Çatışmayan vəziyyət → [recipe-detail](#recipe-detail), [slots](#slots).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**9.2** — Həftəlik plan 7 günlük saxlanma təlimatı deyil. Əvvəlcədən bişirmə təklif edilirsə hər reseptə soyutma, soyuducu/dondurucu və yenidən isitmə qaydası qoşulur. Düyülü yeməklər üçün ayrıca rəsmi qida təhlükəsizliyi təlimatı tətbiq olunur.

- PNG → 07 / 25.
- Çatışmayan vəziyyət → [recipe-detail](#recipe-detail), [instructions](#instructions).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

### 10. İstifadəçiyə elmi izahı necə verəcəyik?

**10.1** — Hədəf kartı: “Bu, yaşın, boyun, çəkin və hərəkətinə əsaslanan başlanğıc təxminidir.” Detalda formula, tarix, istifadə olunan məlumat və mənbə açılır.

- PNG → 01 / 04; 05 / 19.
- Çatışmayan vəziyyət → [evidence](#evidence).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**10.2** — Yemək kartı: “Bu porsiya üçün təxminən 420 kalori.” Detalda resept versiyası, qram və hesablanmış nutrientlər göstərilir. “Dietoloq yoxlayıb” nişanı yalnız faktiki təsdiq varsa görünür.

- PNG → 07 / 25; D03 / Menyu.
- Çatışmayan vəziyyət → [recipe-detail](#recipe-detail), [evidence](#evidence).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**10.3** — Porsiya köməyi: “Şəkillər bizim ölçülmüş porsiyalardır. Sənin boşqabın fərqli ola bilər; istəsən qramla yaz.”

- PNG → 00 / Porsiya; D02 / Qazan.
- Çatışmayan vəziyyət → [measured](#measured), [portion](#portion).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**10.4** — Foto köməyi: “Yağ, sous və gizli məhsulları şəkildən dəqiq bilmək olmur. Tərkibi və miqdarı yoxla.”

- PNG → 03 / 11.
- Çatışmayan vəziyyət → [oil](#oil), [camera](#camera).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**10.5** — Həftəlik nəticə: “Bu həftə 4 gün qeyd etmisən. Qeyd olunmayan günləri hesaba qatmırıq.” Natamam gündəlikdən xəstəlik, metabolik problem və ya kalori pozuntusu nəticəsi çıxarılmır.

- PNG → 05 / 18.
- Çatışmayan vəziyyət → [weight](#weight), [forecast](#forecast).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**10.6** — İlk 12 maarifləndirici kartın mövzuları: kalori təxmini; porsiya; xam/bişmiş çəki; yağ/sous; zülal; lif; tərəvəz-meyvə; duz/natrium; şəkər növləri; çəki dalğalanması; foto məhdudiyyəti; qidanın saxlanması.

- PNG → 05 / 19.
- Çatışmayan vəziyyət → [education](#education).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**10.7** — Hər maarif kartı üçün: qısa istifadəçi mətni, əhatə və istisnalar, konkret mənbə bölməsi, müəllif, klinik redaktor, yoxlama tarixi və növbəti baxış tarixi. Tərcümə eyni qaydanın ID-sinə bağlıdır.

- PNG → 05 / 19.
- Çatışmayan vəziyyət → [education](#education), [evidence](#evidence).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

### 11. Komandaya veriləcək məlumat müqavilələri

**11.1** — Food: id, names{az,en,ru}, aliases, source_id, source_type, preparation_state, nutrients_per_100g, nutrient_units, missing_fields, allergens, source_version, reviewed_by, status.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Hesab/idxal/schema/migration/backend müqaviləsi: kod, DB və sərhəd testləri tələb olunur; ayrıca istehlakçı ekranı tələb edilmir.

**11.2** — Recipe: id/version, ingredient_food_versions, edible_grams, cooking_method, yield_cooked_g, retention_method, nutrients_total/per_100g, portion_min/max/step_g, portion_photos, allergens, equipment, time, storage_instruction_id, cost_region/date, status.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Hesab/idxal/schema/migration/backend müqaviləsi: kod, DB və sərhəd testləri tələb olunur; ayrıca istehlakçı ekranı tələb edilmir.

**11.3** — NutritionPolicy: version, eligible_population, exclusion_rules, formula/version, required_inputs, activity_mapping, energy_adjustment, energy_bounds, macro_rules, nutrient_checks, reassessment_rules, evidence_refs, reviewer, approved_at. Təsdiq olunmayan siyasət production-da aktiv ola bilməz.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Hesab/idxal/schema/migration/backend müqaviləsi: kod, DB və sərhəd testləri tələb olunur; ayrıca istehlakçı ekranı tələb edilmir.

**11.4** — MealPlan: user_profile_version, week, timezone, policy_version, recipe_versions, portions_g, nutrient_snapshot, constraint_results, alternatives və səbəblər.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Hesab/idxal/schema/migration/backend müqaviləsi: kod, DB və sərhəd testləri tələb olunur; ayrıca istehlakçı ekranı tələb edilmir.

**11.5** — FoodLog: client_event_id, user_id, consumed_at, food/recipe_version, confirmed_portion_g, nutrient_snapshot, entry_method, source_plan_item_id, sync_status. Təkrar cəhd eyni qeydi iki dəfə yaratmır.

- PNG → 03 / 12; 06 / 23.
- Çatışmayan vəziyyət → [write](#write), [sync](#sync).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**11.6** — EvidenceCard: claim_id, sadə mətnlər, dəqiq mənbə linki/bölmə, uyğun əhali, məhdudiyyət, version, reviewed_by/date. “Yoxlanıb” statusu tibbi təsdiqlənmiş məhsul iddiası deyil.

- PNG → 05 / 19.
- Çatışmayan vəziyyət → [education](#education), [evidence](#evidence).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**11.7** — Keyfiyyət qaydası: təsdiq zənciri və audit izi olmayan resept, policy və izah avtomatik dərc edilmir; geri qaytarılma və əvvəlki versiyaya baxış mümkün olur.

- PNG → 07 / 25.
- Çatışmayan vəziyyət → [withdrawal](#withdrawal), [evidence](#evidence).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

### 12. Buraxılışdan əvvəl nəyi yoxlayacağıq?

**12.1** — Hesablama testləri: 100 q → porsiya, quru → bişmiş, q/kq/sm/kkal, null nutrient, çoxporsiyalı resept, yağ itkisi və saxlanma əmsalı. Müstəqil hesabla müqayisə; yuvarlaqlaşdırma yalnız sonda.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Yoxlama protokolu və fixture: müstəqil hesab, test nəticəsi və ya pilot hesabatı tələb olunur; PNG bu sübutun əvəzi deyil.

**12.2** — Menyu testləri: müxtəlif profillər, büdcə/vaxt, allergiya, məlum olmayan tərkib, sıfır uyğun namizəd, real porsiya sərhədləri, yemək dəyişmə və alış-veriş yenilənməsi. Sərt filtr pozuntusu aşkarlanarsa buraxılış bloklanır.

- PNG → 08 / 30-32; 10 / 37; 11 / 42.
- Çatışmayan vəziyyət → [replace](#replace), [swap](#swap), [shopping](#shopping).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**12.3** — Qida doğruluğu: seçilmiş əsas ərzaqların mənbə uyğunluğu və bütün ilkin reseptlərin dietoloq yoxlaması. Test bişirməsi nutrient laborator analizini əvəz etmir; laborator yoxlama yalnız belə iddia veriləcəksə ayrıca planlanır.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Mənbə/məzmun paketi və editorial sübut: dataset, mənşə, lisenziya, çıxım və baxış qeydləri tələb olunur; istehlakçı ekranı həmin işi sübut etmir.

**12.4** — Foto pilotu üçün təklif: ən azı 100 çəkilmiş yemək nümunəsi, həm qarışıq/yerli yeməklər, həm sous və müxtəlif işıq. Doğru namizəd, istifadəçi düzəlişi və porsiya xətası ayrı ölçülür. Kalori xətasının müqayisəsi ölçülmüş resept hesabına qarşıdır; klinik dəqiqlik iddiası deyil.

- PNG → 03 / 10-11.
- Çatışmayan vəziyyət → [camera](#camera), [oil](#oil), [measured](#measured).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**12.5** — Məlumat bütövlüyü: internet olmayanda qeyd, təkrar sinxronlaşma, eyni düyməyə iki toxunuş, resept/policy yenilənməsi və əvvəlki günlərin sabitliyi.

- PNG → 06 / 23; 14 / Yazı-xətası.
- Çatışmayan vəziyyət → [write](#write), [sync](#sync).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**12.6** — Sadə UX üçün daxili pilot: 10–15 müxtəlif rəqəmsal bacarıqlı yetkin iştirakçı köməksiz yemək tapsın, porsiya seçsin, səhv qeydi düzəltsin, yeməyi dəyişsin. Tamamlama vaxtı, səhv və harada kömək istədiyi ölçülür; rəqəmlər məhsul hədəfidir, klinik tədqiqat deyil.

- PNG → 03 / 09; D02 / Qazan; 08 / 29.
- Çatışmayan vəziyyət → [search](#search), [portion](#portion), [log](#log), [swap](#swap).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**12.7** — Buraxılış meyarı: aktiv policy klinik redaktor tərəfindən təsdiqlənib; dərc edilən reseptlər izlenə bilir; kritik allergen pozuntusu və hesab dublikatı testlərdə yoxdur; UI sadə dildə yoxlanıb. İlk buraxılış sonrası insident və yanlış qida məlumatını bildirmə kanalı işləyir.

- PNG → 05 / 19.
- Çatışmayan vəziyyət → [privacy](#privacy).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

### 13. İcra ardıcıllığı və məsul şəxslər

**13.1** — Rol bölgüsü: məhsul sahibi əhatə və prioritetləri seçir; dietoloq/klinik redaktor qaydaları və məzmunu təsdiqləyir; resept əməkdaşı bişirir/çəkir/foto çəkir; proqramçı baza, hesablama və plan motorunu qurur; QA hesablamanı və istifadəçi axınını yoxlayır.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Planın idarəetməsi, resurs/rol qərarı, status və prioritet tarixi: versiya, təsdiq/aktivləşmə izi və tələb olunan release sübutu ilə izlənir; ayrıca ekran deyil.

**13.2** — 1-ci mərhələ: hazır 500 ərzaq uyğunlaşdırmasını redaktorla yoxlamaq; klinik əhatə, istisnalar, formula və aktivlik xəritəsini mütəxəssislə təsdiqləmək. Mənbələr və nutrient rəqəmləri artıq idxal edilib.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Mənbə/məzmun paketi və editorial sübut: dataset, mənşə, lisenziya, çıxım və baxış qeydləri tələb olunur; istehlakçı ekranı həmin işi sübut etmir.

**13.3** — 2-ci mərhələ: hazır 224 resept layihəsinin tərkibi, porsiya sayı və hazırlanma addımlarını yoxlamaq. Bişmiş qram və şəkilli porsiya üçün prioritet reseptlərin çıxımı uyğun mənbədən sənədləşdirilir və ya test bişirməsi ilə ölçülür; bütün reseptləri bişirmək ilkin porsiya hesabının universal şərti deyil.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Mənbə/məzmun paketi və editorial sübut: dataset, mənşə, lisenziya, çıxım və baxış qeydləri tələb olunur; istehlakçı ekranı həmin işi sübut etmir.

**13.4** — 3-cü mərhələ: hazır JSON/CSV/SQLite paketini hesablama moduluna qoşmaq, profil uyğunluğu və policy versiyalarını qurmaq. Hazır 8 həftəlik rotasiya yalnız ideya bazasıdır; fərdi balanslı pəhriz kimi aktivləşdirilmir.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Hesab/idxal/schema/migration/backend müqaviləsi: kod, DB və sərhəd testləri tələb olunur; ayrıca istehlakçı ekranı tələb edilmir.

**13.5** — 4-cü həftə: alternativ yemək, alış-veriş, real gündəlik ayrımı, müəllif/yoxlayıcı paneli və qısa elmi izah kartları.

- PNG → 08 / 29-32; 04 / 16; 05 / 19.
- Çatışmayan vəziyyət → [replace](#replace), [shopping](#shopping), [education](#education).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**13.6** — 5-ci mərhələ: mövcud 500 ərzaq və 224 resepti redaktor yoxlamasından keçirmək; EN/RU hazırlanma addımlarını və region qiymətlərini tamamlamaq. 3 dildə ərzaq adları və resept başlıqları işçi variantda hazırdır.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Mənbə/məzmun paketi və editorial sübut: dataset, mənşə, lisenziya, çıxım və baxış qeydləri tələb olunur; istehlakçı ekranı həmin işi sübut etmir.

**13.7** — 6-cı həftə: hesab və filtr testləri, istifadəçi pilotu, səhvlərin düzəlişi; foto modulu hazırdırsa ayrıca ölçmə. Çıxış: təsdiqli ilkin nüvə və qalan işlər siyahısı.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Yoxlama protokolu və fixture: müstəqil hesab, test nəticəsi və ya pilot hesabatı tələb olunur; PNG bu sübutun əvəzi deyil.

**13.8** — Hazır konkret paket: 500 mənbəli ərzaq qeydi + qram və addımları yazılmış 224 resept layihəsi + 8 həftəlik ideya rotasiyası + 8 217 mənbə qeydi olan SQLite + JSON/CSV idxalı. Yoxlamalar tamamlanmadığı üçün publishable=false saxlanır. Bişmiş 100 qram və şəkilli porsiyanın şərtləri ayrıca feature_gates sahəsindədir.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Mənbə/məzmun paketi və editorial sübut: dataset, mənşə, lisenziya, çıxım və baxış qeydləri tələb olunur; istehlakçı ekranı həmin işi sübut etmir.

### 14. Hansı qərarlar təsdiqlənmədən kodu aktivləşdirmirik?

**14.1** — Klinik redaktorun seçilməsi və təsdiq səlahiyyəti; sağlam yetkinlər üçün dəqiq əhatə/istisnalar; formula və aktivlik xəritəsi; məqsədə görə enerji düzəlişi və sərhədlər; makro qaydaları və yenidən hesablama siyasəti.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Planın idarəetməsi, resurs/rol qərarı, status və prioritet tarixi: versiya, təsdiq/aktivləşmə izi və tələb olunan release sübutu ilə izlənir; ayrıca ekran deyil.

- Cari qərar → Status hissəsini §30.1–30.6 ilə birlikdə oxu: bildirilmiş baxış və enerji/makro aktivləşməsi var; kataloq və faktiki implementasiya qəbulunu əvəz etmir.

**14.2** — Resept protokolu: mənbə uyğunlaşdırması, yeyilən çəki, çıxım/saxlanma metodu, minimum test sayı və məqbul dəyişkənlik, ölçülmüş porsiyalar və foto qaydası.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Mənbə/məzmun paketi və editorial sübut: dataset, mənşə, lisenziya, çıxım və baxış qeydləri tələb olunur; istehlakçı ekranı həmin işi sübut etmir.

**14.3** — Məzmun buraxılışı: dəstəklənən region, allergen taksonomiyası, qida seçimləri üçün kitabxananın yetərliliyi, hüquqları sənədləşmiş foto/mətn və tərcümə yoxlaması.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Mənbə/məzmun paketi və editorial sübut: dataset, mənşə, lisenziya, çıxım və baxış qeydləri tələb olunur; istehlakçı ekranı həmin işi sübut etmir.

**14.4** — Bu qərarlar rəqəm uydurmaqla doldurulmur. Təsdiq gələnədək texniki hissə sınaq məlumatı və deaktiv siyasətlə hazırlana bilər; real istifadəçiyə fərdi tibbi uyğunluq vədi verilmir.

- PNG → 13 / Menyu.
- Çatışmayan vəziyyət → [generation](#generation), [scope](#scope).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

- Cari qərar → Status hissəsini §30.1–30.6 ilə birlikdə oxu: bildirilmiş baxış və enerji/makro aktivləşməsi var; kataloq və faktiki implementasiya qəbulunu əvəz etmir.

### 15. Kitabxana indi genişləndirildi: faktiki nəticə

**15.1** — 500 ərzaq qeydi: 55 süd/yumurta, 55 taxıl/çörək, 35 paxlalı, 70 ət/balıq, 120 tərəvəz/göyərti, 70 meyvə/şirə, 30 çərəz/toxum, 65 yağ/ədviyyat və digər məhsul. Xam və bişmiş variantlar ayrıca mənbə qeydləridir; saylar 500 müxtəlif Azərbaycan məhsulu iddiası deyil.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Mənbə/məzmun paketi və editorial sübut: dataset, mənşə, lisenziya, çıxım və baxış qeydləri tələb olunur; istehlakçı ekranı həmin işi sübut etmir.

**15.2** — 224 resept: 33 səhər yeməyi, 26 şorba, 47 ətli/balıqlı əsas yemək, 45 bitki əsaslı yemək, 30 salat/qarnir, 25 yerli yemək layihəsi, 18 qəlyanaltı. Qram, porsiya sayı və AZ addımlar mövcuddur.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Mənbə/məzmun paketi və editorial sübut: dataset, mənşə, lisenziya, çıxım və baxış qeydləri tələb olunur; istehlakçı ekranı həmin işi sübut etmir.

**15.3** — USDA SR Legacy son tarixi 2018 buraxılışı əsas uyğunlaşdırmadır. 2026-10-29 Foundation Foods əlavə istinad olaraq idxal edilib; seçilmiş ərzaqlar həmin məlumatlarla avtomatik əvəz edilməyib. Arxivlər və SHA-256 izləri saxlanır.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Mənbə/məzmun paketi və editorial sübut: dataset, mənşə, lisenziya, çıxım və baxış qeydləri tələb olunur; istehlakçı ekranı həmin işi sübut etmir.

**15.4** — 500 ərzağın hamısında enerji, zülal, yağ və karbohidrat mövcuddur. Natrium 1, lif 11, ümumi şəkər 72, trans yağ 171 ərzaqda yoxdur; bu sahələr null saxlanıb. Sərbəst və əlavə şəkər ümumi şəkərdən uydurulmayıb.

- PNG → 10 / 38.
- Çatışmayan vəziyyət → [macro](#macro), [food-detail](#food-detail).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**15.5** — 224 reseptin ilkin enerji və makro cəmi hesablanıb. Bu, bişmə itkisi və nutrient saxlanması ilə düzəldilməmiş inqrediyent hesabıdır. Porsiya sayına əsaslanan təxmini hesab bişmiş 100 qram hesabından ayrılır; bütün reseptləri test bişirmək universal buraxılış şərti deyil.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Mənbə/məzmun paketi və editorial sübut: dataset, mənşə, lisenziya, çıxım və baxış qeydləri tələb olunur; istehlakçı ekranı həmin işi sübut etmir.

**15.6** — Hazır formatlar: library/foods.json, recipes.json, weekly-ideas.json; foods.csv və recipes.csv; nutrition-library.sqlite; oxunaqlı catalogue-az.txt. Mənbə paketləri sources/ qovluğundadır.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Mənbə/məzmun paketi və editorial sübut: dataset, mənşə, lisenziya, çıxım və baxış qeydləri tələb olunur; istehlakçı ekranı həmin işi sübut etmir.

**15.7** — 15 avtomatik yoxlama keçib: mənbə rəqəmlərinə uyğunluq, arxiv izləri, qram və porsiya hesabı, xam/bişmiş ayrımı, çatışmayan dəyərin ötürülməsi, allergen yayılması, təkrarsız resept tərkibləri, təhlükəsizlik qaydaları, şərti çıxım qapıları, həftəlik istinadlar və SQLite bütövlüyü.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Yoxlama protokolu və fixture: müstəqil hesab, test nəticəsi və ya pilot hesabatı tələb olunur; PNG bu sübutun əvəzi deyil.

**15.8** — 0 resept və 0 ərzaq klinik/yerli yoxlamadan keçmiş kimi işarələnib. Mənbəli baza hazırdır. Tərkib dəyişəndə yenidən hesab, başqa yemək və günlə dəyişmə, foto sonrası resept redaktəsi üçün 16-cı bölmənin qaydaları tətbiq edilir.

- PNG → 07 / 25; 13 / Menyu.
- Çatışmayan vəziyyət → [withdrawal](#withdrawal), [generation](#generation).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

### 16. Məcburi: inqrediyent və yemək dəyişmə

**16.1** — İnqrediyenti çıxarmaq, əlavə etmək və qramını artırıb-azaltmaq məcburi funksiyadır. Bir porsiya ilə bütün qazan miqdarı aydın ayrılır.

- PNG → 07 / 25-27; D02 / Qazan.
- Çatışmayan vəziyyət → [editor](#editor), [portion](#portion).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**16.2** — Dəyişiklikdə resept/porsiya hesabı, gün və həftə proqnozu, allergen uyğunluğu və alış-veriş yenilənir. Faktiki yeyilən qida toplamı yalnız istifadəçi yeməyi qeyd edəndə dəyişir.

- PNG → 07 / 28; 11 / 42.
- Çatışmayan vəziyyət → [forecast](#forecast), [allergen](#allergen), [shopping](#shopping).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**16.3** — Başqa günün yeməyi ilə qarşılıqlı yer dəyişmək mümkündür. Hər iki gün yoxlanır və iki hissə bir əməliyyatla yenilənir. Qalan həftə səssiz yenidən qurulmur.

- PNG → 08 / 31-32; 11 / 41.
- Çatışmayan vəziyyət → [swap](#swap), [atomic](#atomic).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**16.4** — Həmin günə uyğun başqa yemək seçmək mümkündür. Namizədlər günün yeyilmiş və planlanan qidaları, porsiya, allergiya və aktiv NutritionPolicy-yə görə yoxlanır. Uyğun namizəd yoxdursa səbəb deyilir.

- PNG → 08 / 30; 11 / 43.
- Çatışmayan vəziyyət → [replace](#replace), [forecast](#forecast).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**16.5** — Əvvəl/sonra nəticəsi yadda saxlamadan əvvəl göstərilir. Digər yeməklərin porsiyasını dəyişmək yalnız ayrıca qəbul edilən təklif ola bilər. Plan versiyası və geri qaytarma saxlanır.

- PNG → 07 / 28; 11 / 41,43.
- Çatışmayan vəziyyət → [forecast](#forecast), [undo](#undo), [atomic](#atomic).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**16.6** — Dəyişmiş reseptin ayrıca istifadəçi versiyası yaranır. Əsas kitabxana və keçmiş qeydlər dəyişmir; əvvəlki dietoloq təsdiqi, ölçülmüş çıxım və porsiya fotosu yeni tərkibə avtomatik miras qalmır.

- PNG → 07 / 25-28; 10 / 39.
- Çatışmayan vəziyyət → [custom-version](#custom-version).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**16.7** — İnqrediyent dəyişəndə hazırlanma addımları da uyğunlaşdırılmalıdır. Yeni məhsul üçün yoxlanmış təlimat yoxdursa köhnə resept təlimatı tam uyğun kimi göstərilmir.

- PNG → 10 / 39.
- Çatışmayan vəziyyət → [instructions](#instructions).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**16.8** — Foto bizim verilmiş reseptlə əlaqəlidirsə “Reseptdə nəyisə dəyişmisən?” soruşulur. Xeyr → həmin versiya və yeyilən porsiya təsdiqlənir. Bəli → verilmiş son reseptin inqrediyent redaktoru açılır; çıxar/əlavə et/miqdar dəyiş mümkündür, yemək və gün dəyişmək yoxdur. Faktiki hesab istifadəçinin təsdiqlədiyi tərkibə əsaslanır; foto modelinin təxmini bunu səssiz əvəz etmir. Qeyd konkret bişmiş yeməyə aiddir; digər günlər və əsas resept dəyişmir.

- PNG → 09 / 33-36; 11 / 44.
- Çatışmayan vəziyyət → [photo-recipe](#photo-recipe), [model-conflict](#model-conflict), [log](#log).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**16.9** — Ətraflı axınlar, məlumat müqavilələri və 19 qəbul ssenarisi planning/meal-customization-requirements.txt faylındadır. Status: məcburi tələb kimi yazılıb, tətbiqdə hələ kodlaşdırılmayıb.

- PNG → 07 / 25-28; 08 / 29-32; 09 / 33-36; 10 / 37-40; 11 / 41-44.
- Çatışmayan vəziyyət → [editor](#editor), [swap](#swap), [photo-recipe](#photo-recipe).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

### 17. Məqsəd, büdcə və dünya mətbəxlərinə uyğun kitabxana

**17.1** — Yeni faktiki genişlənmə: 200 → 224 resept layihəsi; 500 ərzaq qeydi dəyişməyib. G01–G24 üçün qram, AZ addımlar, üç dildə işçi başlıq, mətbəx ilhamı və ilkin büdcə kateqoriyası var. 24 layihədən 14-ü az, 6-sı orta, 4-ü yüksək büdcə namizədidir; bu bölgü yerli qiymətlə yoxlanmalıdır.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Mənbə/məzmun paketi və editorial sübut: dataset, mənşə, lisenziya, çıxım və baxış qeydləri tələb olunur; istehlakçı ekranı həmin işi sübut etmir.

**17.2** — Mətbəx Azərbaycanla məhdudlaşmır: Levant, Şimali Afrika, Hindistan, Meksika, İtaliya, Yunanıstan və Şərqi Asiya ilhamlı seçimlər daxildir. Bunlar müəllif layihələridir, orijinal milli resept standartı və klinik sınaqdan keçmiş yemək iddiası deyil. Mətbəx ölkəsi istifadəçinin alış-veriş regionundan ayrıdır.

- PNG → 07 / 25.
- Çatışmayan vəziyyət → [recipe-detail](#recipe-detail), [region](#region).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**17.3** — Elmi əsasın üç səviyyəsi saxlanır: ərzağın nutrient mənbəsi; ümumi qidalanma qaydasının mənbəsi; konkret reseptin redaktor və dietoloq yoxlaması. USDA-ya bağlanmış qram hesabı və WHO/NHS istinadı həmin reseptin insanı arıqlatdığını və ya kökəltdiyini sübut etmir.

- PNG → 07 / 25.
- Çatışmayan vəziyyət → [evidence](#evidence).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**17.4** — Onboarding müqaviləsi: goal=lose|maintain|gain|healthy; yaş, hesablama üçün cins, boy, çəki, aktivlik və uyğunluq məlumatları; region, valyuta, həftəlik büdcə məbləği, büdcənin neçə nəfərə aid olduğu; allergiya, yemədikləri və vaxt. İstifadəçinin 5 noyabr 2026 qərarı: büdcəyə daxil yeməklər ayrıca soruşulmur və boşluğu təsdiqi bloklamır; əvvəlki qaralamadakı bu cavab yeni profilə ötürülmür. Bu giriş toplanmadığından büdcənin yemək əhatəsi müəyyən edilməmiş saxlanır; dəqiq əhatə uydurulmur. Onboarding-də ev avadanlığı və evdəki ərzaqların siyahısı/miqdarı soruşulmur; bu məlumatlar olmadan profil təsdiqlənir. Əvvəlki qaralamadakı həmin sahələr təsdiqi bloklamır və yeni profilə ötürülmür. Sadə az/orta/çox büdcə seçimi məbləğ yoxdursa üstünlükdür, dəqiq limit deyil.

- PNG → 00 / Məqsəd; 01 / 02-03; 02 / 05-06; 12 / Büdcə.
- Çatışmayan vəziyyət → [region](#region), [budget](#budget), [obsolete](#obsolete).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

- Cari qərar → §17.4/17.8-in son qərarı üstün gəlir: yemək əhatəsi/avadanlıq/ev inventarı soruşulmur, dəqiq ev miqdarı funksiyası geri gətirilmir.

**17.5** — Məqsəd məcburi seçim girişidir. Arıqlama üçün təsdiqli enerji çatışmazlığı, saxlama üçün ehtiyac ətrafında hədəf, çəki artırma üçün təsdiqli enerji artığı tətbiq olunur. Rəqəmlər aktiv NutritionPolicy-dən gəlir. Heç bir inqrediyent və resept dəyişməz şəkildə yalnız bir məqsədə aid deyil; porsiya və bütün günün tərkibi yenidən yoxlanır.

- PNG → 00 / Məqsəd; 05 / 19.
- Çatışmayan vəziyyət → [goals](#goals).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**17.6** — Arıqlama namizədləri üçün tərəvəz, paxlalı, lif və uyğun zülal mənbələri ilə real porsiya; çəki artırma üçün eyni bazaya uyğun əlavə taxıl, süd/qatıq, yağ, tahin və ya çərəz seçilə bilər. Allergen, qida seçimi və hesab yenidən yoxlanmadan əlavə edilmir. Saxlama üçün də qida müxtəlifliyi və keyfiyyət qaydaları qüvvədədir.

- PNG → 07 / 26-28; 08 / 30.
- Çatışmayan vəziyyət → [editor](#editor), [allergen](#allergen), [rules](#rules).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**17.7** — Az büdcənin əsas namizədləri mərcimək, noxud, lobya, yumurta, yulaf, bulqur, düyü və mövsümi tərəvəzdir. Orta və yüksək büdcədə balıq, tofu, kinoa və çərəz kimi seçimlər genişlənə bilər. Bunlar universal ucuz/baha siyahısı deyil: region, mövsüm, yeyilən hissə və bişmiş/quru vəziyyət üzrə yoxlanır. Bahalı resept avtomatik daha sağlam və ya daha üstün sayılmır.

- PNG → 08 / 30.
- Çatışmayan vəziyyət → [budget](#budget), [region](#region).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**17.8** — Qiymət modeli: ingredient_price(food_id, region, currency, retailer, observed_at, package_grams, edible_fraction, package_price, preparation_state, source). İnqrediyentin istifadə dəyəri ilə alınmalı tam qablaşdırma xərci ayrı hesablanır. Həftə üzrə eyni məhsul birləşdirilir və həftəyə lazım olan ümumi miqdar göstərilir; paketlər bu miqdar əsasında yuxarı tam ədədə yuvarlaqlaşdırılır. İstifadəçinin 5 noyabr 2026 qərarı: evdəki ərzağın dəqiq miqdarını daxil etmək və həmin miqdarı çıxmaq funksiyası plandan tam çıxarılır, sonrakı mərhələ üçün də saxlanmır. Yalnız könüllü “Evdə var” işarəsi qalır; işarə dəqiq qram məlumatı deyil və həftəlik ümumi ehtiyacı dəyişmir. Quru qiymət bişmiş qramlara birbaşa vurulmur.

- PNG → 04 / 16; 11 / 42.
- Çatışmayan vəziyyət → [shopping](#shopping), [budget](#budget), [obsolete](#obsolete).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

- Cari qərar → §17.4/17.8-in son qərarı üstün gəlir: yemək əhatəsi/avadanlıq/ev inventarı soruşulmur, dəqiq ev miqdarı funksiyası geri gətirilmir.

**17.9** — Qiymət çatmırsa və ya təsdiqli qiymət siyasətinə görə köhnədirsə total_cost=null və səbəb göstərilir; büdcəyə uyğundur nişanı verilmir. Uydurma AZN qiyməti yazılmır. Büdcə çatmırsa daha əlçatan alternativ, istifadəçinin qəbul etdiyi sadə təkrar və ya limit dəyişikliyi təklif edilir; limit səssiz aşılmır.

- PNG → 04 / 16; 08 / 30.
- Çatışmayan vəziyyət → [budget](#budget), [replace](#replace).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**17.10** — Seçim ardıcıllığı: uyğunluq və təsdiqli məzmun → allergiya və qadağalar → region/avadanlıq → hədəfə uyğun mümkün porsiyalar → bütün gün/həftə qida yoxlaması → həftəlik alış-veriş büdcəsi → vaxt, müxtəliflik və zövq üzrə sıralama. Büdcə üçün allergen və klinik sərhəd yumşaldılmır. Uyğun nəticə yoxdursa səbəb açıq verilir.

- PNG → 08 / 30; 10 / 37.
- Çatışmayan vəziyyət → [rules](#rules), [generation](#generation).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**17.11** — Əvəzetmə müqaviləsi: ucuz alternativ sadəcə eyni qramla kopyalanmır. Məsələn kinoa→bulqur qlüten allergiyası olan şəxs üçün uyğun deyil; somon→mərcimək eyni nutrient profili deyil. Tərkib, porsiya, qida hesabı, allergen, hazırlanma addımı, alış-veriş və həftə xərci yenidən hesablanır; istifadəçi qəbul etdikdən sonra plan versiyası yaranır.

- PNG → 07 / 28; 11 / 42-43.
- Çatışmayan vəziyyət → [replace](#replace), [allergen](#allergen), [instructions](#instructions), [budget](#budget).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**17.12** — Resept müqaviləsi: discovery.cuisine_inspiration, budget_tier_candidate, budget_status, goal_matching, evidence_refs, evidence_scope. Mövcud 200 reseptin discovery=null olması uyğunluq təsdiqi deyil; mərhələli redaktor zənginləşdirməsi tələb olunur. price_estimate=null və publishable=false hazırda bütün 224 resept üçün saxlanır.

- PNG → 07 / 25.
- Çatışmayan vəziyyət → [recipe-detail](#recipe-detail), [evidence](#evidence).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**17.13** — Növbəti məzmun mərhələsinin məhsul hədəfi 350 fərqli resept layihəsidir; bu elmi tələb deyil. Əvvəl 224 layihənin təkrarı, real porsiyası, təlimatı və mənbə uyğunluğu yoxlanır; sonra az büdcə, sürətli hazırlıq və bitki zülalı boşluqları doldurulur. Məqsəd×büdcə üzrə 12 profil sınağı resept sayından daha vacibdir; hər uyğun profil üçün həftəlik alternativ və qida keyfiyyəti yoxlanmalıdır.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Mənbə/məzmun paketi və editorial sübut: dataset, mənşə, lisenziya, çıxım və baxış qeydləri tələb olunur; istehlakçı ekranı həmin işi sübut etmir.

**17.14** — Qəbul ssenariləri: 4 məqsəd × 3 büdcə səviyyəsi; eyni reseptin müxtəlif uyğun porsiyası; büdcə məbləği olmadan dəqiq xərc iddiasının olmaması; qiymətsiz region; köhnə qiymət; qablaşdırma yuvarlaqlaşdırması; ümumi həftəlik miqdarın saxlanması və könüllü “Evdə var” işarəsi; allergenli ucuz alternativin rəddi; sıfır uyğun namizəd; məqsəd/büdcə dəyişəndə yenidən yoxlama; keçmiş yemək qeydlərinin dəyişməməsi. Bunlar hələ tətbiqdə icra edilmiş testlər deyil, buraxılış tələbləridir.

- PNG → 02 / 06; 04 / 16; 11 / 42-43.
- Çatışmayan vəziyyət → [budget](#budget), [goals](#goals), [shopping](#shopping), [replace](#replace).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

### 18. Vizual istinadlar: məqsəd, seçimlər və hesab müqaviləsi

**18.1** — İstinadlar: design-concepts/simple-turquoise/00-reference.png — ümumi məhsul məqsədi və əsas axınlar; design-concepts/simple-turquoise/02-secimler-ve-hesab.png — onboarding 4/5 və 5/5, planın saxlanması və hesaba giriş. Bu bölmə istifadəçinin dizayn tələbləridir, elmi mənbə deyil; əvvəlki üçməqsədli müqavilə dörd seçimə genişlənir.

- PNG → 00 / Məqsəd; 02 / 05-08.
- Çatışmayan vəziyyət → [goals](#goals), [auth](#auth).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**18.2** — Məqsəd ekranı: Arıqlamaq (lose), Çəkini saxlamaq (maintain), Çəki almaq (gain), Sağlam yemək (healthy). Bir seçim edilir və sonradan profildən dəyişdirilir. Healthy ayrıca məqsəddir: avtomatik arıqlama defisiti və ya kökəlmə artığı tətbiq olunmur; təsdiqli siyasət üzrə qida müxtəlifliyi və balans əsas götürülür. Məqsəd resept sıralamasına, porsiyaya, gün/həftə planına və irəliləyiş göstəricilərinə ötürülür.

- PNG → 00 / Məqsəd; 05 / 19.
- Çatışmayan vəziyyət → [goals](#goals).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**18.3** — Allergiya ekranı: Süd, Yumurta, Qoz-fındıq, Balıq, Buğda çoxseçimlidir. Allergiyam yoxdur digər seçimlərlə qarşılıqlı istisnadır; cavabsız vəziyyət allergiya yoxdur kimi qəbul edilmir. Şəkildəki beş kart bütün allergenlərin tam siyahısı sayılmır; əlavə allergen daxil etmə yolu olmalıdır. Buğda allergiyası və qlüten məhdudiyyəti ayrı saxlanır; qoz-fındıq qrupunun konkret tərkibi dəqiqləşdirilir. Resept, inqrediyent, əvəzetmə və foto sonrası təsdiq eyni sərt filtri tətbiq edir; məlumatı naməlum məhsul uyğun sayılmır.

- PNG → 02 / 05; 14 / Allergen.
- Çatışmayan vəziyyət → [allergen](#allergen).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**18.4** — Sevmədiyim yeməkləri seç ayrıca seçim ekranını açır. Yemək və inqrediyent istəksizliyi allergiyadan ayrı saxlanır, dəyişdirilə və silinə bilir. Plan yaratma və dəyişmə bu istəksizlikləri nəzərə alır; uyğun seçim qalmadıqda səbəb və istifadəçinin təsdiqləyəcəyi alternativ göstərilir, seçim səssiz ləğv edilmir.

- PNG → 02 / 05; 14 / İstəksizlik.
- Çatışmayan vəziyyət → [dislike](#dislike).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**18.5** — Bişirməyə vaxtın: 15 dəq, 30 dəq, 1 saat; hər yemək üçün ümumi hazırlanma vaxtı limiti kimi saxlanır, aktiv iş və gözləmə vaxtı ayrıca metadata olur. Şəkildə 30 dəq seçilib, bu hamı üçün təsdiqlənmiş cavab deyil. Vaxtı naməlum resept limitə uyğun kimi təqdim edilmir; uyğun resept yoxdursa limit dəyişikliyi təklif olunur.

- PNG → 02 / 06.
- Çatışmayan vəziyyət → [time](#time).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**18.6** — Qida büdcən: Qənaətli, Orta, Sərbəst. Bunlar keyfiyyət üstünlüyü deyil, xərc üstünlüyüdür; Sərbəst avtomatik limitsiz büdcə demək deyil. Bölmə 17-dəki region, valyuta, həftəlik məbləğ, nəfər/yemək əhatəsi və real qiymət müqaviləsi saxlanır. Şəkildə Qənaətli seçilməsi bütün istifadəçilərin seçimi sayılmır.

- PNG → 02 / 06; 12 / Büdcə.
- Çatışmayan vəziyyət → [budget](#budget), [obsolete](#obsolete).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

- Cari qərar → §17.4/17.8-in son qərarı üstün gəlir: yemək əhatəsi/avadanlıq/ev inventarı soruşulmur, dəqiq ev miqdarı funksiyası geri gətirilmir.

**18.7** — Gündə neçə dəfə yeyirsən?: 3 dəfə, 4 dəfə, 5 dəfə. Gündəlik yemək slotları və enerjinin paylanması seçilmiş saya uyğun qurulur; 4/5 seçimində əlavə slotlar açıq göstərilir. Səhər/Nahar/Şam/Qəlyanaltı ekran quruluşu seçilmiş sayı əvəz etmir; yemək sayı artanda günlük hədəf avtomatik artırılmır. Şəkildəki 3 dəfə nümunə seçimidir.

- PNG → 02 / 06; D03 / Menyu.
- Çatışmayan vəziyyət → [slots](#slots).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**18.8** — Planımı hazırla bütün cavabları bir profil versiyası kimi saxlayır və uyğunluq yoxlamasından keçirir. Məqsəd, allergiya, istəksizlik, vaxt, büdcə və yemək sayı həm ilk plan, həm yemək/gün dəyişmə, həm də inqrediyent redaktəsində tətbiq edilir. Profil dəyişəndə təsirlənən gələcək plan yenidən hesablanır və təsdiq olunur; keçmiş yeyilmiş qeydlər qorunur.

- PNG → 02 / 06; 12 / Profil.
- Çatışmayan vəziyyət → [generation](#generation), [reassess](#reassess), [write](#write).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**18.9** — Planını itirmə: Apple ilə davam et, Google ilə davam et, E-poçtla qeydiyyat, İndi yox və Məxfilik barədə sadə məlumat. Qonaq yolu plan yaratmağı bloklamır; lokal saxlanma və cihaz dəyişəndə bərpa məhdudiyyəti izah edilir. Sonradan hesab açılarkən qonaq profili, plan və qeydlər dublikat yaratmadan köçürülür; başqa telefonda davam yalnız uğurlu sinxronizasiyadan sonra vəd edilir.

- PNG → 02 / 07.
- Çatışmayan vəziyyət → [auth](#auth), [privacy](#privacy).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**18.10** — Hesabına daxil ol: e-poçt və şifrə, şifrəni göstər/gizlət, Daxil ol, Şifrəni unutdum, Yeni hesab yarat və geri dönüş. Qeydiyyat/giriş fərqləndirilir; yüklənmə, səhv məlumat, şəbəkəsiz rejim, provayderdən imtina və sinxronizasiya konfliktləri nəzərdə tutulur. Xəta zamanı onboarding cavabları və yaradılmış plan itmir; məxfilik məlumatı qısa və başadüşülən olur.

- PNG → 02 / 08.
- Çatışmayan vəziyyət → [auth](#auth).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**18.11** — 00-reference məhsul axını: Bu gün cari yeməyi, gündəlik qalan enerjini və yemək slotlarının vəziyyətini göstərir. Əlavə et üç yol verir: Şəkil çək, Axtar, Əvvəl yediklərim. Az/Orta/Çox şəkilli porsiya ilə yanaşı Dəqiq qramla yaz yolu var; foto və porsiya hesabı təxmini kimi göstərilir və istifadəçi təsdiqindən sonra qeydə düşür. Şəkildəki 860/420/440 və digər rəqəmlər sabit norma deyil, nümunədir.

- PNG → 00 / Bu-gün,Əlavə-et,Porsiya; D02 / Qazan.
- Çatışmayan vəziyyət → [search](#search), [measured](#measured), [forecast](#forecast).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**18.12** — Menyu axını: yemək şəkli, adı, hesablanan enerji, vaxt, Yedim, Resept, Dəyiş və Alış-veriş siyahısı mövcud planın məlumatından gəlir. İrəliləyiş çəki trendini və qeyd davamlılığını göstərir; healthy məqsədində çəki azalması avtomatik uğur sayılmır. Naviqasiya: Bu gün, Menyu, Əlavə et, İrəliləyiş, Profil. Sadə Azərbaycan dili və özünü izah edən şəkillər qorunur; əsas məna yalnız rəng və şəkildən asılı olmur.

- PNG → D03 / Menyu; 07 / 25; 05 / 18-19.
- Çatışmayan vəziyyət → [recipe-detail](#recipe-detail), [weight](#weight), [locale](#locale).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**18.13** — Qəbul meyarları: 4 məqsəd × 3 büdcə = 12 profil; hər profil üçün 3/4/5 yemək və 15/30/60 dəqiqə seçimlərinin planı dəyişməsi; allergiya yoxdur qarşılıqlı istisnası; cavabsız allergiya; çoxlu allergiya və istəksizlik; vaxtı/qiyməti naməlum resept; uyğun nəticə olmaması; healthy üçün avtomatik defisitin olmaması; qonaqdan hesaba keçid; başqa cihazda bərpa; giriş və sinxronizasiya xətasında məlumatın saxlanması. Bunlar planlaşdırılan yoxlamalardır, işlək tətbiqdə keçmiş test iddiası deyil.

- PNG → 00 / Məqsəd; 02 / 05-08.
- Çatışmayan vəziyyət → [goals](#goals), [budget](#budget), [slots](#slots), [auth](#auth).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

### 19. Digər dizayn ekranları ilə müqayisə və tamamlanan tələblər

**19.1** — Əhatə və üstünlük: 01-baslangic.png (01–04), 03-yemek-qeydiyyati.png (09–12), 04-menyu-ve-alisveris.png (13–16), 05-irelileyis-ve-profil.png (17–20), 06-abunelik-ve-veziyyetler.png (21–24), 07-inqrediyent-redaktesi.png (25–28), 08-yemek-ve-gun-deyisme.png (29–32), 09-foto-sonrasi-resept.png (33–36), 10-deyisiklik-ve-xeta-hallari.png (37–40), 11-yenilenmis-plan-ve-tesdiq.png (41–44) vizual yoxlanıb. Hamısı design-concepts/simple-turquoise/ daxilindədir. 00 və 02 bölmə 18-dədir. 07–11 köhnə resept, dəyişmə və foto ekranlarını tamamlayır; ziddiyyətdə new-flows-guide.txt və meal-customization-requirements.txt funksional qaydaları əsasdır. Nümunə tarix, şəxs, qiymət və kalori real istifadəçi məlumatı deyil.

- PNG → 00 / Ümumi; 01 / 01-04; 02 / 05-08; 03 / 09-12; 04 / 13-16; 05 / 17-20; 06 / 21-24; 07 / 25-28; 08 / 29-32; 09 / 33-36; 10 / 37-40; 11 / 41-44.
- Çatışmayan vəziyyət → [locale](#locale).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**19.2** — 01 / ekran 01–04: Xoş gəldin ekranında dil seçimi, Başlayaq və Hesabım var yolları olmalıdır. Özünü tanıt yaş, boy (sm), çəki (kq), birbaşa rəqəm girişi və artır/azalt idarələrini göstərir; onluq vergül dəstəklənir, boş/mənfi/uyğunsuz giriş izahlı rədd edilir. Hesablama üçün cins könüllüdür və buraxıldıqda bölmə 5-in alternativ yolu işləyir. Aktivlik: Çox otururam, Bir az hərəkət edirəm, Çox hərəkət edirəm — aydın təsvirlə təsdiqli xəritəyə bağlanır. Geri dönüş cavabları saxlayır və addım göstəricisi real ardıcıllığa uyğundur.

- PNG → 01 / 01-03.
- Çatışmayan vəziyyət → [inputs](#inputs), [no-target](#no-target), [activity](#activity).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

- Cari qərar → Köhnə üç aktivlik təsviri §26.4–26.6-dakı dörd kateqoriya və yenidən təsdiq ilə tamamlanmalıdır.

**19.3** — 01 / ekran 04: Planın hazırdır xülasəsi hesablanan təxmini gündəlik hədəfi (əgər hesablamaq mümkündürsə), seçilmiş yemək sayını, məqsədi və nümunə menyunu göstərir. Menyumu gör və Seçimləri dəyiş ayrıca yollar olur. Şəkildəki saxlama məqsədi və 1 900 kalori istifadəçi məqsədini əvəz etmir; xülasə həmişə faktiki profil və plan versiyasından yaranır.

- PNG → 01 / 04; 12 / Profil.
- Çatışmayan vəziyyət → [generation](#generation), [no-target](#no-target), [slots](#slots).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**19.4** — 03 / ekran 09–12: Axtarışda adı yazmaq, mətni təmizləmək, yemək variantını seçmək və Tapmadın? Özün əlavə et yolu var. Əl ilə formaya ad, miqdar/vahid, məlumdursa mənbəli qida dəyəri daxil edilir; bilinməyən nutrient sıfır sayılmır. Əvvəl yediklərim siyahısından təkrar qeyd yeni tarix və təsdiqlənmiş porsiya ilə yaranır. Yüklənmə, boş nəticə və xəta vəziyyətləri aydın ayrılır.

- PNG → 03 / 09.
- Çatışmayan vəziyyət → [search](#search), [food-detail](#food-detail).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**19.5** — 03 / ekran 10–11 və 06 / ekran 24: Kamera çərçivəsi, işıq idarəsi (dəstəklənirsə), qalereyadan seçim, kamera/foto icazəsi rədd ediləndə axtarışa keçid olmalıdır. Sərbəst yemək fotosunda namizəd və Başqa yeməkdir yolu, əlavə yağ/sous üçün Yox/Var və Var üçün məhsul-miqdar girişi verilir; hesabda artıq olan yağ ikinci dəfə əlavə edilmir. Şəkli tanıya bilmədik halında Yenidən şəkil çək, Yeməyin adını axtar, Özüm yazım yolları qalır; heç biri avtomatik qida qeydi yaratmır.

- PNG → 03 / 10-11; 06 / 24.
- Çatışmayan vəziyyət → [camera](#camera), [oil](#oil).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**19.6** — 03 / ekran 12 və 11 / ekran 44: Uğurlu qeyd ekranı faktiki saxlanmış yeməyi, şəxsi porsiyanı, təxmini enerjini və mümkün olduqda qalan gündəlik enerjini göstərir. Bu günə qayıt, Başqa yemək əlavə et və Düzəliş et yolları konkret qeyd ID-si ilə işləyir. Qeydi düzəltmək və silmək günlük toplamları yeniləyir; silmə açıq təsdiqlənir. Hədəf aşılması neytral izah edilir, cəza və yeməyə qadağa yoxdur.

- PNG → 03 / 12; 11 / 44.
- Çatışmayan vəziyyət → [log](#log), [forecast](#forecast), [write](#write).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**19.7** — 04 / ekran 13–16: Həftənin menyusunda həqiqi tarixlər, seçilmiş gün və Bu gün işarəsi, planlı/yeyilmiş vəziyyətlər ayrıdır; Yedim yalnız porsiya təsdiqindən sonra qeyd yaradır. Reseptdə Saxla şəxsi seçilmişlər siyahısına əlavə edir, bu əməl menyuya əlavə və ya yeyilmiş qeyd sayılmır. Vaxt, porsiya əsası, inqrediyent miqdarları və addımlar seçilmiş resept versiyasına bağlıdır. Daha yeni 25-ci ekran bu girişə redaktə və hazırlanmış yemək fotosunu əlavə edir.

- PNG → D03 / Menyu; 04 / 14; 07 / 25.
- Çatışmayan vəziyyət → [slots](#slots), [recipe-detail](#recipe-detail), [portion](#portion).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**19.8** — 04 / ekran 16 və 11 / ekran 42: Alış-veriş siyahısı həftəyə görə kateqoriyalanır, məhsul/vahid/miqdar göstərir. Həftəyə lazım olan ümumi miqdar göstərilir. Evdə var ilə Alınıb ayrı sadə işarələrdir; evdəki dəqiq miqdar daxil edilmir və avtomatik miqdar çıxılmır; hazır məhsul sayğacı unikal məhsul sətirlərindən hesablanır. Eyni ərzaq üzrə çevrilə bilən miqdarlar birləşdirilir; ədəd→qram yalnız məlum çevirmə ilə edilir. Siyahını paylaş sistem paylaşım önbaxışını açır; yalnız siyahı məzmunu daxil edilir və göndərməni istifadəçi seçir. Plan dəyişəndə fərq göstərilir, əvvəlki işarələr və artıq alınmış məhsullar qorunur.

- PNG → 04 / 16; 11 / 42.
- Çatışmayan vəziyyət → [shopping](#shopping), [share](#share), [obsolete](#obsolete).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

- Cari qərar → §17.4/17.8-in son qərarı üstün gəlir: yemək əhatəsi/avadanlıq/ev inventarı soruşulmur, dəqiq ev miqdarı funksiyası geri gətirilmir.

**19.9** — 05 / ekran 17–18: Çəki qeydində tarix seçimi, kq ilə birbaşa giriş/artır-azalt, Yadda saxla və Əvvəlki qeydlərim yolları var; tarixçədə düzəliş/silmə mümkündür. Eyni tarixə təkrar qeyd əlavə ediləndə əvəzləmə və ya ayrıca ölçmə açıq seçilir, səssiz dublikat yaranmır. Həftəlik baxış tarix aralığını, əvvəlki həftəyə keçidi, qeyd edilən günləri, ölçmə sayını və faktiki çəki nöqtələrini göstərir. Qeyd edilməyən gün 0 qəbul kimi hesablanmır, bir ölçmə trend sayılmır; ən çox qeyd edilən yemək yalnız mövcud qeydlərdən çıxarılır. Çəki dəyişikliyi məqsədə uyğun, mühakiməsiz göstərilir.

- PNG → 05 / 17-18; 13 / İrəliləyiş.
- Çatışmayan vəziyyət → [weight](#weight).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**19.10** — 05 / ekran 19–20: Profil redaktəsi, Məqsədim, Yemək seçimlərim, Xatırlatmalar, Dil, Abunəliyim və Kömək bölmələrinin hər birinə işlək keçid tələb olunur. Dil dəyişməsi məzmun ID-lərini və keçmiş rəqəmləri dəyişmir. Yemək xatırlatmaları ayrıca açılıb-söndürülür və vaxtı seçilir; çəki xatırlatması günlük/həftəlik qurulur; Sakit saatlar və Yadda saxla işləyir. Bildirişlər könüllüdür; sistem icazəsi rədd edilərsə tətbiq işləyir. Cədvəl istifadəçinin saat qurşağına və 3/4/5 yemək slotuna uyğun olur, gecə yarısını keçən sakit aralıq nəzərə alınır, səfər/saat dəyişikliyi təkrar bildiriş yaratmır. Şəkildəki 09:00/13:00/19:00 və 22:00–08:00 nümunədir.

- PNG → 05 / 19-20.
- Çatışmayan vəziyyət → [reminder](#reminder), [privacy](#privacy), [locale](#locale).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**19.11** — 06 / ekran 21: Aylıq/İllik seçim, Abunə ol, Pulsuz davam et və Alışı bərpa et yolları nəzərdə tutulur. Həftəlik şəxsi menyu, yemək dəyişmə və alış-veriş vədləri yalnız həqiqi paket imkanlarına bağlanır; pulsuz/ödənişli sərhəd ayrıca məhsul qərarı ilə sənədləşdirilir. 4,99/39,99 USD sabit qiymət deyil: real mağaza qiyməti, valyuta, dövr, yenilənmə və ləğv etmə yolu göstərilir. Aktiv, bitmiş, ləğvi planlaşdırılmış, gözləyən/uğursuz ödəniş və bərpa vəziyyətləri fərqləndirilir; giriş hüququ uğurlu alış yoxlanandan sonra açılır. Ödəniş rəddi mövcud qeydləri silmir. Burada platforma və hüquq uyğunluğunun yoxlandığı iddia edilmir.

- PNG → 06 / 21.
- Çatışmayan vəziyyət → [billing](#billing).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**19.12** — 06 / ekran 22–23: Boş Bu gün ekranı Hələ yemək əlavə etməmisən mesajı, Yemək əlavə et və Bugünkü menyunu gör yollarını verir; planın olması faktiki qəbul olması sayılmır. Şəbəkəsiz ekranda Yeməyin telefonda saxlanıb yalnız dayanıqlı lokal yazı uğurludursa göstərilir. Gözləyən sinxronizasiya, xəta və tamamlanma ayrıdır; Yenidən yoxla və Davam et qeydi təkrarlamır. Lokal yazı da alınmayıbsa saxlanıb demək əvəzinə bərpa yolu göstərilir.

- PNG → 06 / 22-23; 14 / Yazı-xətası.
- Çatışmayan vəziyyət → [write](#write), [sync](#sync).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**19.13** — 07–09 / ekran 25–36: Əsas fərdiləşdirmə axınları bölmə 16-da var. Əlavə detalları: su, duz, sarıkök kimi Digər ərzaqlar açılan bölmədə itmir; duz nutrient hesabı üçün əhəmiyyətlidir. Məhsul seçici xam/quru/bişmiş vəziyyəti və mənbəni ayırır, seçilmiş qramı kontekstə qaytarır. Önizləmədən redaktəyə geri dönüş dəyişiklikləri saxlayır. Qazan hissəsi yarım/1/2 porsiya və ya mümkün dəqiq miqdarla seçilir; foto özü qazan çıxımını ölçmür.

- PNG → 07 / 26-28; 09 / 34-36; D02 / Qazan.
- Çatışmayan vəziyyət → [editor](#editor), [food-detail](#food-detail), [portion](#portion).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**19.14** — 10 / ekran 37–40: Uyğunsuz dəyişmədə səbəb, Başqa gün seç və Porsiyalara bax yolları olur; təsdiqə qədər heç bir yemək dəyişmir. Məlumatı çatmayan məhsulda Etiketdəki məlumatı yaz və Bazadan uyğun məhsul tap alternativləri verilir; natamam hesab uyğunluq nişanı almır. Yenilənmiş addımlara və tərkibə geri baxış mövcuddur. Saxlanmamış redaktədən çıxarkən Redaktəyə davam et, Dəyişiklikləri ləğv et, Qaralama kimi saxla seçilir; qaralama baza resept/plan versiyası və redaktə kontekstini saxlayır, bərpa zamanı versiya konflikti yoxlanır. Qaralama təsdiqlənmiş plan və ya gündəlik sayılmır.

- PNG → 10 / 37-40.
- Çatışmayan vəziyyət → [draft](#draft), [instructions](#instructions), [atomic](#atomic).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**19.15** — 11 / ekran 41–44: Menyuda hansı iki hissənin yer dəyişdiyi göstərilir və Son dəyişikliyi geri qaytar plan/alış-veriş üçün vahid əməliyyatdır; sonradan yaranmış qeydlər silinmir. Alış-veriş fərqi artım/azalma kimi görünür; şəkildəki +200 q göbələk/+20 q yağ yalnız dörd porsiyalı nümunədir. Alternativdə əvvəl/sonra yemək və enerji, açılan gün proqnozu, Bununla əvəz et və Başqa variant seç yolları olur. Qeyd olundu ekranı istifadəçinin dəyişdirdiyi tərkibi göstərir və Qeydə düzəliş et həmin faktiki qeydi açır; gələcək günlərə tətbiq ayrıca seçim olmadan edilmir.

- PNG → 11 / 41-44.
- Çatışmayan vəziyyət → [undo](#undo), [shopping](#shopping), [replace](#replace), [log](#log).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**19.16** — Kömək və hesab idarəsi: Profilin Kömək bölməsi tez-tez verilən suallar, dəstək və yanlış qida məlumatını bildirmə yolunu açır. Məxfilik, məlumat ixracı, hesabdan çıxış və hesab silmə əlçatandır; hesab silmə nəticəsi açıq izah və təsdiq tələb edir. Plan məlumatı, şəxsi qeydlər və şəkillərin saxlanma/silmə qaydası sənədləşdirilir. Bu bəndlər implementation-guide.txt-də qeyd edilib, hər biri ayrıca raster ekranla çəkilməyib.

- PNG → 05 / 19.
- Çatışmayan vəziyyət → [privacy](#privacy).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**19.17** — Əlçatanlıq və vizual müqavilə: design-concepts/tokens.css daxilində simple-* rəngləri, space-* aralıqları və radius-card istifadə olunur; rasterdən təsadüfi rəng götürülmür. implementation-guide.txt-dəki ən azı 16 pt əsas mətn, iOS 44 pt toxunma sahəsi, Android üçün platforma minimumu, sistem mətn böyütməsi, ekran oxuyucu etiketləri, daimi forma adları və rəqəm klaviaturası tətbiq edilir. Seçim rənglə yanaşı işarə/çərçivə ilə göstərilir. İşlək UI-də mətn kontrastı ən azı 4,5 ilə ölçülür; ən uzun AZ/EN/RU mətnlə render və vizual yoxlama aparılır. Bu dəyişiklik yeni UI yaratmır və tətbiqin əlçatanlıq yoxlamasından keçdiyini bildirmir.

- PNG → 00 / Ümumi.
- Çatışmayan vəziyyət → [locale](#locale).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**19.18** — Əlavə məlumat müqavilələri: WeightEntry(id, measured_at, timezone, weight_kg, revision); ReminderSettings(meal_slot, enabled, local_time, timezone, quiet_hours, weight_frequency, permission_state); RecipeBookmark(user_id, recipe_id, saved_version); ShoppingItem(food_id, preparation_state, quantity, unit, home_available, purchased_quantity, plan_version); SubscriptionEntitlement(product_id, store, status, valid_until, verified_at); EditDraft(context, base_version, changes, saved_at). Məxfi ödəniş məlumatı tətbiqin bu modellərinə yazılmır; minimum tələb olunan istinad və status saxlanır.

- PNG → 05 / 17-20; 04 / 16; 06 / 21; 10 / 40.
- Çatışmayan vəziyyət → [weight](#weight), [reminder](#reminder), [billing](#billing), [draft](#draft).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**19.19** — Buraxılış yoxlama siyahısı: ekran 01–44 üçün giriş→hərəkət→nəticə→geri/xəta yolu izlənir. Əlavə ssenarilər: könüllü cins buraxılıb; onluq vergül; plan xülasəsi profillə uyğun; əl ilə naməlum nutrient; kamera icazəsi yoxdur; tanınmayan foto; yağın iki dəfə sayılmaması; seçilmişlərdən təkrar qeyd; tarixli çəki düzəlişi; boş həftə; sakit saatların gecə yarısını keçməsi; bildiriş icazəsi rəddi; aylıq/illik və bərpa/ödəniş xətası; pulsuz davam; lokal yazı xətası; paylaşımın ləğvi; qaralamanın bərpası və konflikti; alış-verişdə evdə/alınıb ayrımı; sonrakı gündəliyi qoruyan undo; dil dəyişməsi və böyük mətn. Bunlar icra edilməli qəbul ssenariləridir, keçmiş tətbiq testləri deyil.

- PNG → 01 / 02-04; 02 / 05-08; 03 / 09-12; 04 / 13-16; 05 / 17-20; 06 / 21-24; 10 / 40; 11 / 41-44.
- Çatışmayan vəziyyət → [locale](#locale), [inputs](#inputs), [auth](#auth), [camera](#camera), [log](#log), [weight](#weight), [reminder](#reminder), [billing](#billing), [write](#write), [share](#share), [draft](#draft), [shopping](#shopping), [undo](#undo).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

### 20. Vahid plan, üstünlük və ziddiyyətlərin həlli

**20.1** — U-01 Mənbə üstünlüyü: cari istifadəçi qərarı → bu kanonik plan və onun törəmə görünüşləri → T01–T19 detalları → coding-tasks icra növbəsi → dizayn nümunələri. Ziddiyyət varsa icra dayandırılıb konkret tələb üzrə həll edilir; rəsmi mənbə dəstəyi olmayan klinik qərar uydurulmur. AGENTS.md-də MD-yə istinad eyni kanonik qaydaları göstərir.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Planın idarəetməsi, resurs/rol qərarı, status və prioritet tarixi: versiya, təsdiq/aktivləşmə izi və tələb olunan release sübutu ilə izlənir; ayrıca ekran deyil.

**20.2** — U-02 docs/nutrition-plan-design.md-nin 5 noyabr ilkin versiyası Git tarixçəsində saxlanır; ayrıca paralel normativ plan kimi davam etmir. Mifflin hesabı, DB-dən nutrient hesabı, mənbə izlənməsi, tibbi istisna, foto təsdiqi və məxfilik prinsipləri vahid plana daxil edilib.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Planın idarəetməsi, resurs/rol qərarı, status və prioritet tarixi: versiya, təsdiq/aktivləşmə izi və tələb olunan release sübutu ilə izlənir; ayrıca ekran deyil.

**20.3** — U-03 Yeni sənəddəki 1,2/1,375/1,55/1,725 əmsalları, −300/−500 defisit, +200/+300 surplus, 1200/1500 minimum, +20% maksimum və 1,2–1,6 q/kq protein default-u təsdiqli universal qayda deyil. §5, §11 və G2 əsasdır: hər biri dəqiq mənbə/əhali/çəki bazası/versiya və faktiki təsdiqlə policy-də müəyyənləşdirilir. Qeyri-müəyyən hədd canlı plana sızmır. Sonrakı §26–29 tədqiqat əlavəsi bu boşluğu konkret, deaktiv policy təklifi ilə tamamlayır; universal qayda kimi aktivləşdirmir.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Planın idarəetməsi, resurs/rol qərarı, status və prioritet tarixi: versiya, təsdiq/aktivləşmə izi və tələb olunan release sübutu ilə izlənir; ayrıca ekran deyil.

- Cari qərar → Status hissəsini §30.1–30.6 ilə birlikdə oxu: bildirilmiş baxış və enerji/makro aktivləşməsi var; kataloq və faktiki implementasiya qəbulunu əvəz etmir.

**20.4** — U-04 20/30/50 makro bölgüsü, 25/35/30/10 slot bölgüsü, 3 gün təkrarsızlıq və ±10% enerji toleransı tibbi standart sayılmır. Bunlar təsdiq tələb edən namizəd məhsul parametrləridir. Günlük enerji 3/4/5 slot arasında bölünür; slot sayına görə artırılmır; feasible həll yoxdursa uğur qaytarılmır. §27–28-də bu branch üçün konkret template/tolerans seçilib və məhsul parametri kimi işarələnib; əvvəlki namizəd siyahısı aktiv default deyil.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Planın idarəetməsi, resurs/rol qərarı, status və prioritet tarixi: versiya, təsdiq/aktivləşmə izi və tələb olunan release sübutu ilə izlənir; ayrıca ekran deyil.

**20.5** — U-05 Cins könüllüdür; formula üçün lazım olan məlumat buraxılıbsa avtomatik hədəf yoxdur. Məqsədlər lose/maintain/gain/healthy saxlanır; healthy avtomatik defisit deyil. AZ/EN/RU ilk dil əhatəsidir; TR sonrakı ayrıca redaktə/tərcümə işidir.

- PNG → 00 / Məqsəd; 01 / 02.
- Çatışmayan vəziyyət → [no-target](#no-target), [goals](#goals), [locale](#locale).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**20.6** — U-06 16+ mağaza auditoriyası 18 yaşdan aşağı şəxslərə fərdi plan icazəsi deyil; mağaza reytinqi konsolda hələ yoxlanmayıb. İlkin fərdiləşdirmə yalnız təsdiqli yetkin model əhatəsindədir. Diabet, böyrək xəstəliyi, hamiləlik/südvermə, qidalanma pozuntusu və tibbi pəhriz ehtiyacı konservativ istisna marşrutuna düşür; bu diaqnoz testi deyil. BMI üçün yeni avtomatik klinik hədd təsdiqsiz əlavə edilmir.

- PNG → 12 / Sağlamlıq.
- Çatışmayan vəziyyət → [health](#health), [scope](#scope).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**20.7** — U-07 Məlumatı DB-dən hesablamaq dəqiqlik zəmanəti deyil: qida uyğunlaşdırması, tərkib variantı, çıxım, saxlanma və porsiya qeyri-müəyyənliyi saxlanır. Bütün mikro rəqəmlər bazada yoxdur; naməlum sıfır olmur. Meyvə şirəsi sərbəst şəkər xəritəsinə daxildir; total/added/free sugar fərqləndirilir.

- PNG → 10 / 38.
- Çatışmayan vəziyyət → [food-detail](#food-detail), [measured](#measured).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**20.8** — U-08 Başdan 30–50 yeni şablon və P0–P4 həftə vədləri köhnə təklifdir. Hazır 500 ərzaq/224 resept layihəsi, K01–K12 işi və K13–K34 növbəsi saxlanır; dərc olunmamış məzmun hazır istehlakçı planı deyil. Hər bitmiş K üçün qəbul sənədi və uyğun main CI sübutu yoxlanır.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Planın idarəetməsi, resurs/rol qərarı, status və prioritet tarixi: versiya, təsdiq/aktivləşmə izi və tələb olunan release sübutu ilə izlənir; ayrıca ekran deyil.

**20.9** — U-09 Hazırda dietoloq olmadığına görə avtomatik fərdi plan G2 bağlı qalır. Mütəxəssis olmadan işləyə bilən mənbəli gündəlik/ümumi maarif rejimi ayrıca R-01 müqaviləsinə tabedir. G2-ni dəyişmək ayrıca açıq məhsul qərarı və əsaslandırma tələb edir; bu birləşdirmə onu səssiz dəyişmir.

- PNG → 13 / Menyu.
- Çatışmayan vəziyyət → [no-target](#no-target), [generation](#generation).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

- Cari qərar → Status hissəsini §30.1–30.6 ilə birlikdə oxu: bildirilmiş baxış və enerji/makro aktivləşməsi var; kataloq və faktiki implementasiya qəbulunu əvəz etmir.

**20.10** — U-10 Yeni backend xətti Spark-only + Go/PostgreSQL kimi hədəf məhdudiyyətidir; hazır inteqrasiya iddiası deyil. Android offline Room və mövcud snapshot müqavilələri qorunur. Cloud Functions/Blaze/Identity Platform asılılığı daxil edilmir; uzaq ADR, əməliyyat büdcəsi və faktiki konfiqurasiya K25/K26-da yoxlanır.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Hesab/idxal/schema/migration/backend müqaviləsi: kod, DB və sərhəd testləri tələb olunur; ayrıca istehlakçı ekranı tələb edilmir.

### 21. Dietoloqsuz rejim və aktivləşdirmə matrisi

**21.1** — R-01 İndiki rejim: mənbəsi və qeyri-müəyyənliyi göstərilən əl ilə qeyd, dəqiq qramla nutrient arifmetikası, istifadəçinin təsdiqlədiyi keçmiş yeməyin təkrarı, tarixçə və ümumi maarif məzmunu. Sadalanan hər funksiya öz Kxx qəbulundan sonra açılır. Klinik redaktor imzası olmayan EvidenceCard “dietoloq təsdiqləyib” nişanı almır; dərc üçün mövcud baxış tələbləri qorunur.

- PNG → 00 / Əlavə-et; 03 / 09; D02 / Dəqiq-miqdar.
- Çatışmayan vəziyyət → [search](#search), [education](#education), [evidence](#evidence).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**21.2** — R-02 Avtomatik fərdi kalori/makro, arıqlama/artırma və balanslı fərdi həftəlik plan yalnız G2 + G3 açıq və bütün tətbiq olunan sərt yoxlamalar keçibsə mümkündür. G2/G3 bağlı olduqda qeyd/göstərmə işləyə bilər; boş katalog və ya təsdiqsiz policy üçün nümunə reseptlə süni uğur yoxdur. §26–29 maşınla təsvir edilmiş, amma təsdiqlənməmiş alternativ model təklifidir; gate-ləri açmır.

- PNG → 13 / Menyu; 01 / 04.
- Çatışmayan vəziyyət → [generation](#generation).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

- Cari qərar → Status hissəsini §30.1–30.6 ilə birlikdə oxu: bildirilmiş baxış və enerji/makro aktivləşməsi var; kataloq və faktiki implementasiya qəbulunu əvəz etmir.

**21.3** — R-03 İstifadəçinin mütəxəssisdən aldığı hədəf ayrıca mənşə və tarixlə saxlanır; tətbiqin hesabladığı və ya təsdiqlədiyi hədəf adlanmır. Bu yol tibbi istisnanı və allergen təhlükəsizliyini aşmaq üçün avtomatik menyu açmır; K08 uyğunluq müqaviləsi saxlanır.

- PNG → 05 / 19.
- Çatışmayan vəziyyət → [no-target](#no-target), [evidence](#evidence).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**21.4** — R-04 Uyğunluq nəticələri: ELIGIBLE, INELIGIBLE, INCOMPLETE, POLICY_UNAVAILABLE, MODEL_OUT_OF_SCOPE. Naməlum cavab “xeyr” olmur; natamam profil/policy avtomatik hədəf yaratmır. İstisna səbəbi minimum məlumatla və mühakiməsiz izah edilir; gündəlik və peşəkar dəstək yolu qalır.

- PNG → 12 / Sağlamlıq.
- Çatışmayan vəziyyət → [health](#health), [no-target](#no-target).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**21.5** — R-05 Məhsul sahibi G1/platforma və funksional prioriteti; klinik redaktor G2 və qidalanma məzmununu; məzmun əməkdaşı ölçü/hüquq/tərcümə sübutunu; QA proqram davranışını yoxlayır. Rəsmi mənbə, insan baxışı, proqram testi və klinik effektivlik fərqli sübutlardır. Sahibi tərəfindən uydurma reviewer/approved_at yazılmır.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Planın idarəetməsi, resurs/rol qərarı, status və prioritet tarixi: versiya, təsdiq/aktivləşmə izi və tələb olunan release sübutu ilə izlənir; ayrıca ekran deyil.

**21.6** — R-06 G3 hər resept və versiya üçün ayrıca açılır. Dərc üçün mənbə/tərkib/allergen/təlimat/hüquq/dil yoxlaması; bişmiş 100 q üçün uyğun çıxım; Az/Orta/Çox üçün kalibrasiya tələb olunur. G4 olmadan qiymət naməlum/təxmini; G5 olmadan uzaq bərpa və foto provayderi vədi yoxdur; G6 olmadan ödəniş yoxdur.

- PNG → 07 / 25; 06 / 21.
- Çatışmayan vəziyyət → [recipe-detail](#recipe-detail), [measured](#measured), [budget](#budget), [billing](#billing).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**21.7** — R-07 Aktiv policy və resept geri çəkilərsə yeni plan/swap/təkrar tövsiyə üçün istifadəsi dayandırılır. Keçmiş faktiki qeydlər snapshot kimi qalır; geri çağırılmış mənbə barədə aydın xəbərdarlıq göstərilir. Plan yeniləməsi istifadəçi təsdiqi ilə ayrıca versiya yaradır.

- PNG → D03 / Menyu; 07 / 25.
- Çatışmayan vəziyyət → [withdrawal](#withdrawal), [reassess](#reassess).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**21.8** — R-08 G7 buraxılış üçün real məzmun, cihaz və istifadəçi pilotu sübutlarını tələb edir. Yaş reytinqi, Play deklarasiyası, məxfilik və faktiki imkanların uyğunluğu təqdimatdan əvvəl yoxlanır. “Tibbi cihaz deyil” mətni zərərli tövsiyəni qəbul edilən etmir; xəstəlik diaqnozu/müalicəsi vədi yoxdur.

- PNG → 05 / 19.
- Çatışmayan vəziyyət → [scope](#scope), [privacy](#privacy).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

### 22. Hesablama, nutrient əhatəsi və generatorun yekun müqaviləsi

**22.1** — C-01 Axın: profil versiyası → uyğunluq → təsdiqli policy → tələb olunan giriş/model əhatəsi → REE → policy-yə uyğun aktivlik/enerji → məqsəd düzəlişi → makro/nutrient qaydaları → dərc edilmiş namizədlər → porsiya axtarışı → gün/həftə validatoru → dəyişməz nəticə. Bir mərhələ uğursuzdursa sonrakı mərhələ təsdiqli nəticə yaratmır.

- PNG → 01 / 04.
- Çatışmayan vəziyyət → [generation](#generation), [evidence](#evidence).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**22.2** — C-02 NutritionPolicy §11-ə əlavə olaraq effective_from/to, evidence_checked_at, source_section, model_age_scope, weight_basis, rule_severity, comparison_operator, unit, evaluation_window, missing_data_action və supersedes saxlayır. Bunlar hədəf müqavilə əlavələridir; mövcud Kotlin modelində hamısının implementasiya edildiyi iddia edilmir. Dəyişiklik geriyə uyğun migration və K08 regresiyası tələb edir.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Hesab/idxal/schema/migration/backend müqaviləsi: kod, DB və sərhəd testləri tələb olunur; ayrıca istehlakçı ekranı tələb edilmir.

**22.3** — C-03 Müxtəlif standartlar ayrı ReferenceSet kimi saxlanır: WHO population guidance, EFSA yaş/cins/həyat mərhələsi üzrə PRI/AI/RI/UL, ABŞ DRI/DGA nəşri. Ən sərt rəqəmi seçib qarışdırmaq olmaz. Eyni qayda üçün seçilmiş mənbə və əhatə açıq olur; ziddiyyət klinik baxışa qaytarılır. UL hədəf deyil; həmin nutrientin qida/fortifikasiya/əlavə formasına və zaman pəncərəsinə aid məhdudiyyəti tətbiq edilir.

- PNG → 05 / 19.
- Çatışmayan vəziyyət → [evidence](#evidence), [rules](#rules).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**22.4** — C-04 NutrientValue: nutrient_id, amount(nullable), unit, basis(per_100g_edible/per_serving/batch), preparation_state, source_food_version, derivation, completeness, uncertainty_note. Qram/mq/mkq, kkal/kJ, natrium/duz və vitaminlərin müxtəlif ifadə formaları açıq xəritələnir. Enerji komponentlərinin 4/4/9 yoxlaması anomaliya siqnalıdır; lif/poliol/alkoqol və mənbə metodunu nəzərə almadan rəsmi enerjini əvəz etmir.

- PNG → 10 / 38; D02 / Dəqiq-miqdar.
- Çatışmayan vəziyyət → [food-detail](#food-detail).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**22.5** — C-05 Hər validator RuleResult(rule_id, policy_version, scope, operator, threshold, observed_value, unit, status=PASS/FAIL/UNKNOWN/NOT_APPLICABLE, evidence_refs, missing_fields, reason) qaytarır. Kritik nutrient/allergen məlum deyilsə UNKNOWN uğur sayılmır. Sərt tələbdə FAIL/UNKNOWN planın dərcini bloklayır; yumşaq keyfiyyət şərtində natamamlıq açıq qalır, “tam balanslı” nişanı verilmir.

- PNG → 10 / 37-38.
- Çatışmayan vəziyyət → [rules](#rules).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**22.6** — C-06 Total/added/free sugar ayrı sahələrdir; naməlum trans yağ və sərbəst şəkər 0 deyil. Natriumdan duz ekvivalenti salt_g=sodium_mg×2,5/1000, sodium_mg=salt_g×1000/2,5 ilə, çevrilmiş mənşə ilə saxlanır. Duz inqrediyenti çıxarılmır və iki dəfə sayılmır. Meyvə/tərəvəz çəkisi uyğun qida qrupu və yeyilən hissə xəritəsindən gəlir; istənilən bitki və ya reseptin tam çəkisi avtomatik 400 q hesabına girmir.

- PNG → 10 / 38.
- Çatışmayan vəziyyət → [food-detail](#food-detail), [macro](#macro).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**22.7** — C-07 Reseptdə çıxım nutrient yaratmır: əvvəl yeyilən inqrediyentlər və uyğun saxlanma ilə batch toplamı, sonra təsdiqli porsiya payı/ölçülmüş çıxım tətbiq edilir. Su itkisi nutrient toplamını artırmır; atılan yağ/bulyon itkisi sənədləşir. Bişmiş komponentə retention ikinci dəfə vurulmur. Məhsula aid sübut yoxdursa qaşıq/ədəd/sıxlıq çevirməsi işləmir.

- PNG → D02 / Qazan.
- Çatışmayan vəziyyət → [portion](#portion), [measured](#measured).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**22.8** — C-08 PlanGenerationRequest(profile_version, policy_version, catalog_version, week_start, timezone, goal, meals_per_day, constraints, seed, request_id). Sərt filtrlər porsiya skorundan əvvəl işləyir. Məhdud axtarış büdcəsi və deterministik seed var; limitsiz yenidən cəhd yoxdur. Hər yemək üçün alternativ yoxdursa ayrıca nəticədir, uydurma alternativ deyil.

- PNG → D03 / Menyu; 08 / 30.
- Çatışmayan vəziyyət → [generation](#generation), [replace](#replace).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**22.9** — C-09 Generator nəticəsi SUCCESS / NO_FEASIBLE_PLAN / INCOMPLETE_DATA / POLICY_UNAVAILABLE / STALE_INPUT / TEMPORARY_FAILURE və rule_results/unmet_constraints qaytarır. Enerji uyğun olsa da allergen/tərkib və ya kritik nutrient məlum deyilsə SUCCESS yoxdur. Büdcə/vaxt yumşaldılması yalnız qaydada icazə varsa səbəb və istifadəçi seçimi ilə olur; allergiya və tibbi istisna yumşalmır.

- PNG → 13 / Menyu; 10 / 37.
- Çatışmayan vəziyyət → [generation](#generation), [rules](#rules).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**22.10** — C-10 SUCCESS-də profile/policy/catalog/recipe versiyaları, porsiyalar, nutrient snapshot və məlumat əhatəsi saxlanır. Eyni request_id və eyni payload eyni nəticəyə çıxır; dəyişmiş payload üçün açıq konflikt var. Profil/plan versiyası köhnədirsə yazıdan əvvəl yenidən yoxlanır. Hesablamalar tam dəqiqliklə, göstərmə yuvarlaqlaşdırması sonda aparılır.

- PNG → D03 / Menyu; 07 / 28.
- Çatışmayan vəziyyət → [atomic](#atomic), [evidence](#evidence).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**22.11** — C-11 Günlük proqnoz faktiki qeydlər + yeyilməmiş plan hissəsidir; plan görüntüləmək qəbul sayılmır. Natamam nutrient/gündəlikdə dəqiq qalan enerji deyilmir. Resept redaktəsi enerji hədəfini dəyişmir; keçmiş snapshot və şəxsin faktiki batch-i qorunur. §16 və T01–T19 dəyişməz məcburi müqavilədir.

- PNG → 00 / Bu-gün; 07 / 28; 09 / 36.
- Çatışmayan vəziyyət → [forecast](#forecast), [custom-version](#custom-version).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**22.12** — C-12 AI yalnız namizəd və redaktə üçün mətn qaralaması verir. Addımların vaxt/temperatur/allergen təhlükəsizlik mətni təsdiqli reseptdən gəlir; LLM təlimatı birbaşa dərc edilmir. Generativ foto porsiya ölçü istinadı deyil; sərbəst yemək fotosunda namizəd, tərkib və miqdar istifadəçi təsdiqindən əvvəl FoodLog yaratmır.

- PNG → 03 / 11; 10 / 39.
- Çatışmayan vəziyyət → [instructions](#instructions), [model-conflict](#model-conflict), [measured](#measured).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

### 23. Mənbə, regional yemək və hüquq protokolu

**23.1** — D-01 USDA əsas tərkib mənbəyidir; Foundation/SR Legacy/FNDDS/Branded məqsədə uyğun seçilir, ad oxşarlığı kifayət deyil. FDC ID, nəşr, alınma tarixi, xam fayl/hash, nutrient ID/vahid və xəritələmə səbəbi saxlanır. API açarı serverdədir; rəsmi API limitinə qarşı keş və rəsmi dump üstün tutulur; servisin dayanması lokal gündəliyi dayandırmır.

- PNG → 06 / 23.
- Çatışmayan vəziyyət → [sync](#sync), [evidence](#evidence).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**23.2** — D-02 Azərbaycan yeməkləri inqrediyentlərə ayrılır; yağ, duz, sous, yeyilən hissə və bişirmə metodu nəzərə alınır. Feta analoqu hər yerli pendiri, generic qatıq hər etiketi əvəz etmir. MappingStatus exact/acceptable_proxy/unresolved, fərq və reviewer saxlanır; unresolved sərt uyğunluqdan keçmir. Resept kitabxanası üçün AZ məzmunun yoxlanması indiki K32 işidir, ayrıca P2-yə kor-koranə təxirə salınmır.

- PNG → 07 / 27.
- Çatışmayan vəziyyət → [food-detail](#food-detail), [allergen](#allergen).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**23.3** — D-03 CARKFOODS şəbəkə adı hazır yüklənə bilən Azərbaycan qida cədvəli/lisenziyası sübutu deyil. TÜBER regional kontekst namizədidir, Azərbaycan üçün təsdiqli nutrient bazası deyil. Yeni regional paketə keçməzdən əvvəl konkret dataset, nəşr, istifadə hüququ və metodologiya qeydə alınır; bu işdə həmin paketlərin istifadəyə hazırlığı təsdiqlənməyib.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Mənbə/məzmun paketi və editorial sübut: dataset, mənşə, lisenziya, çıxım və baxış qeydləri tələb olunur; istehlakçı ekranı həmin işi sübut etmir.

**23.4** — D-04 Open Food Facts barcode üçün gələcək opsional mənbədir; indiki kanonik FDC kitabxanasını əvəz etmir. İnteqrasiyadan əvvəl ODbL/DbCL/CC BY-SA üzrə attribution, törəmə baza/paylaşma və foto hüquqları yoxlanır. Ayrı DB/cədvəl hüquqi istisna zəmanəti deyil; hüquqi uyğunluq göstərilmədən istehsala açılmır.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Mənbə/məzmun paketi və editorial sübut: dataset, mənşə, lisenziya, çıxım və baxış qeydləri tələb olunur; istehlakçı ekranı həmin işi sübut etmir.

**23.5** — D-05 Barcode uyğunluğu, bazar/variant, etiket tarixi və allergen tamlığı ayrıca yoxlanır. OFF-də allergen teqinin olmaması “allergensiz” deyil; etiket və qarışıq tərkib yoxlanmadan allergiyaya uyğun nişanı yoxdur. Çarpaz təmas, “izləri ola bilər”, çölyak və dözümsüzlük ayrı anlayışlardır; istifadəçi paket etiketini yenə yoxlamalıdır.

- PNG → 10 / 38.
- Çatışmayan vəziyyət → [food-detail](#food-detail), [allergen](#allergen).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**23.6** — D-06 İnqrediyent siyahısından ideya götürmək bütün resept mətnini, tərcüməsini, fotonu və DB-ni sərbəst kopyalamaq icazəsi deyil. Öz hazırlanma mətni və hüquqları sənədləşmiş foto istifadə edilir; mənşə URL/müəllif/lisenziya/icazə tarixi editorial qeyddə saxlanır. Attribution tələb olunursa UI-də göstərilir, gizlədilmir. ABŞ Circular 33 digər ölkələr üçün avtomatik hüquqi qərar deyil.

- PNG → 07 / 25; 05 / 19.
- Çatışmayan vəziyyət → [recipe-detail](#recipe-detail), [privacy](#privacy).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**23.7** — D-07 SourceReview(source_id, retrieved_at, publication_version, exact_section, claim_ids, license, reviewer, next_review_at, status) reyestri tutulur. Mənbənin faktiki dəyişməsi və ya geri çəkilməsi həmin claim/policy/recipe-lərin təsir analizini açır; yalnız URL-in işləməsi elmi baxış sayılmır. FDC fayl hash-ları mövcud manifestdən götürülür, yenisi uydurulmur.

- PNG → 05 / 19; 07 / 25.
- Çatışmayan vəziyyət → [evidence](#evidence), [withdrawal](#withdrawal).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**23.8** — D-08 5 noyabr araşdırmasında WHO, EFSA, USDA API, NIDDK, cari DGA keçidi, OFF-in rəsmi lisenziya sənədi, Firebase, Play və Circular 33 oxunub. Əvvəlki S1–S17 reyestrinin bütün alt sənədlərinin bu tarixdə yenidən açıldığı iddia edilmir. Yeni DGA nəşrinin bütün nutrient qaydaları, regional bazalar və konkret AZ hüquqi uyğunluğu ayrıca açıq yoxlamadır.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Mənbə/məzmun paketi və editorial sübut: dataset, mənşə, lisenziya, çıxım və baxış qeydləri tələb olunur; istehlakçı ekranı həmin işi sübut etmir.

### 24. Offline Android, Spark və server sərhədləri

**24.1** — B-01 Mövcud Kotlin/Compose + Room qalır. Room lokal qeydin dayanıqlı saxlanma sərhədidir. Uzaq sync yalnız uğurla təsdiqlənəndə “başqa cihazda bərpa” vədi verir; Auth girişi təkbaşına sync uğuru deyil. Lokal-only, pending, synced və failed statusları ayrıdır.

- PNG → 06 / 23.
- Çatışmayan vəziyyət → [sync](#sync), [write](#write).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**24.2** — B-02 Hədəf inteqrasiya: Firebase Spark Auth/uyğun yüngül servis → Firebase tokenini yoxlayan Go AppBackend → PostgreSQL-də versiyalı məzmun/policy/plan və şəxsi sync məlumatı. Profilin eyni sahəsi Firestore və PostgreSQL-də iki müstəqil həqiqət kimi saxlanmır; K25/K26 ADR-i authoritative store və konflikt qaydasını müəyyən edir. Ağır nutrient/AI işi cihazda API açarı və ya Cloud Functions tələb etmir.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Hesab/idxal/schema/migration/backend müqaviləsi: kod, DB və sərhəd testləri tələb olunur; ayrıca istehlakçı ekranı tələb edilmir.

**24.3** — B-03 Server tokenin imza/issuer/audience/expiry-sini, hər resurs üçün user ownership-i yoxlayır. Test/production katalogu və policy ayrıdır; mobil feature flag backend təsdiqini əvəz etmir. Remote Config klinik təsdiqsiz həddi aktivləşdirmir. Xidmət açarları server sirr saxlanmasına, client konfiqurasiyası isə mühitə bağlıdır; gizli açarlar log/şəkil URL-inə yazılmır.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Hesab/idxal/schema/migration/backend müqaviləsi: kod, DB və sərhəd testləri tələb olunur; ayrıca istehlakçı ekranı tələb edilmir.

**24.4** — B-04 Sync müqaviləsi user_id/client_event_id/revision/base_revision/tombstone və payload uyğunluğunu qoruyur. Təkrar retry dublikat yaratmır; silinmiş qeyd köhnə cihazdan dirilmir; konflikt səssiz last-write-wins ilə faktiki yeməyi itirmir. Giriş və quota xətasında lokal məlumat/qaralama saxlanır.

- PNG → 06 / 23; 02 / 08.
- Çatışmayan vəziyyət → [sync](#sync), [auth](#auth).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**24.5** — B-05 Xarici API üçün timeout, məhdud retry/backoff, keş, quota ölçümü və circuit-breaker tələb olunur. Kvota bitəndə LLM rəqəmi və ya köhnə təsdiqsiz policy ilə uğur yoxdur. Spark kvotası, xarici server/storage/AI xərci və dəstəklənən Auth üsulları real konfiqurasiyada yoxlanır; “tam pulsuz” vədi verilmir.

- PNG → 06 / 23; 06 / 24.
- Çatışmayan vəziyyət → [sync](#sync), [camera](#camera), [generation](#generation).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**24.6** — B-06 Foto serverə göndərilməzdən əvvəl məqsəd/razılıq, saxlanma müddəti və silmə yolu izah edilir; lazımsız metadata çıxarılır. Object storage seçimi, giriş nəzarəti və silmə işi ayrıca G5 qəbuludur; Spark-da foto saxlamanın hazır olduğu fərz edilmir. Foto və tibbi cavablar analitika/crash log-a yazılmır.

- PNG → 03 / 10.
- Çatışmayan vəziyyət → [photo-consent](#photo-consent), [privacy](#privacy).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**24.7** — B-07 K29 əvvəl məlumat inventarı, məqsəd/əsas, minimal toplama, konkret retention cədvəli, ixrac, hesab silmə və backup-dan son silinmə siyasətini sənədləşdirir. Məxfilik səhifəsinin URL-i mövcuddursa da mətnin faktiki data axınına uyğunluğu yoxlanmadan hüquqi hazır sayılmır. Ads yoxdur hədəfi saxlanır; başqa app-in ads mətni DailyPlate-ə daşınmır.

- PNG → 05 / 19.
- Çatışmayan vəziyyət → [privacy](#privacy).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**24.8** — B-08 Monitorinq şəxsi qida/foto olmadan generator nəticə kodları, UNKNOWN/FAIL sayı, mənbə versiyası, sync/retry və quota xətasını ölçür. Allergen/mapping insidenti məzmunu karantinə alır, yeni təklifləri dayandırır və məsul baxışı açır. Geri çağırma/audit/geri qaytarma ssenarisi buraxılışdan əvvəl sınaqdan keçir.

- PNG → D03 / Menyu; 07 / 25.
- Çatışmayan vəziyyət → [withdrawal](#withdrawal).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

### 25. Kodlaşdırma xəritəsi və yekun qəbul ssenariləri

**25.1** — A-01 İcra ardıcıllığı planning/coding-tasks.txt-dir: K01–K12 mövcud iş sübutları ilə qorunur; növbəti K13. §20–24 əlavələri K13 sərt filtr, K14 qiymət, K15 generator, K16 proqnoz, K18–K23 redaktə/foto, K24 maarif, K25–K26 backend/sync, K29 məxfilik, K31–K34 regresiya/pilot/buraxılışda icra edilir. K08-ə təsir edən müqavilə dəyişikliyi ayrıca migration/regresiya ilə aparılır.

- PNG → 12 / Sağlamlıq; 01 / 02-04.
- Çatışmayan vəziyyət → [health](#health), [no-target](#no-target).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**25.2** — A-02 Müstəqil formula fixture-i: 70 kq, 175 sm, 30 yaş kişi üçün Mifflin REE 1648,75 kkal/gün; eyni qadın girişi 1482,75; fərq 166. Bu TDEE və tövsiyə edilən qəbul deyil. Aktivlik nəticəsi yalnız seçilmiş təsdiqli mapping fixture-i ilə yoxlanır. Yaş/cins boş, modeldən kənar və qeyri-sonlu giriş ayrı rədd hallarındadır.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Yoxlama protokolu və fixture: müstəqil hesab, test nəticəsi və ya pilot hesabatı tələb olunur; PNG bu sübutun əvəzi deyil.

**25.3** — A-03 Policy yoxdur/qaralamadır/geri çəkilib və ya uyğunluq natamamdır → avtomatik hədəf və fərdi plan yoxdur. 18 yaşdan aşağı, hamilə/südverən, tibbi pəhriz və xəstəlik risk cavabları düzgün məhdud yola çıxır; mütəxəssis hədəfi sərt istisnanı aşmır. Test policy production-da açıla bilmir.

- PNG → 12 / Sağlamlıq; 13 / Menyu.
- Çatışmayan vəziyyət → [health](#health), [no-target](#no-target), [generation](#generation).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**25.4** — A-04 Sabit source_food_id + release/hash + preparation_state üçün 100 q və porsiya müstəqil hesablanır. “100 q alma” adla sabit kalori fixture-i deyil. mg/mkq/q/kkal/kJ, null/0, total/added/free sugar, duz/natrium və yuvarlaqlaşdırma sərhədləri yoxlanır; bişmə düzəlişi iki dəfə tətbiq edilmir.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Yoxlama protokolu və fixture: müstəqil hesab, test nəticəsi və ya pilot hesabatı tələb olunur; PNG bu sübutun əvəzi deyil.

**25.5** — A-05 Süd allergiyası + pendir; qarışıq tərkib; məlum olmayan allergen; OFF-də teq yoxdur; çapraz təmas; unmapped məhsul → uyğunluq nişanı yoxdur. Allergenə görə sərt blok qiymət/kalori/istifadəçi vaxtına görə yumşalmır. Foto “gizli allergen yoxdur” sübutu deyil.

- PNG → 02 / 05; 08 / 30.
- Çatışmayan vəziyyət → [allergen](#allergen).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**25.6** — A-06 Hər qaydada dəqiq həddə, həddin aşağı/yuxarı tərəfində və UNKNOWN nəticədə operator/pəncərə siyasətə uyğun yoxlanır. Mənbədə olmayan sərbəst şəkər/trans yağ və natamam mikrodata “bütün limitlər keçdi” nəticəsi vermir. UL ilə PRI/AI və gündəlik/habitual pəncərə qarışmır.

- PNG → 10 / 37-38.
- Çatışmayan vəziyyət → [rules](#rules), [macro](#macro).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**25.7** — A-07 4 məqsəd × 3 büdcə × 3 yemək sayı × 3 vaxt = 108 baza profil kombinasiyası. Hər dəstəklənən kombinasiyada filter/portion/day/week nəticələri yoxlanır; məzmun yetmirsə açıq NO_FEASIBLE_PLAN qəbul edilən təhlükəsiz nəticədir, saxta uğur deyil. Allergiya/istisna/region əlavə sərhəd matrisidir; bu say klinik sübut deyil.

- PNG → 00 / Məqsəd; 02 / 06; D03 / Menyu.
- Çatışmayan vəziyyət → [goals](#goals), [budget](#budget), [slots](#slots), [generation](#generation).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**25.8** — A-08 Generator eyni seed/request/versiya ilə təkrarlanan nəticə verir; axtarış vaxtı bitəndə aydın failure var. Tam uyğunsuz katalog, yalnız bir namizəd, alternativ yoxdur, vaxt/qiymət naməlum, kritik nutrient natamam və stale profil ayrıca sınaqdan keçir. ±10% və oxşar namizəd parametrlər təsdiqsiz test oracle-u olmur.

- PNG → 13 / Menyu; 08 / 30.
- Çatışmayan vəziyyət → [generation](#generation), [replace](#replace).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**25.9** — A-09 T01–T19 bütün redaktə/swap/foto/batch/idempotency ssenariləri saxlanır. Yeyilmiş və planlı hissə iki dəfə sayılmır; redaktə/undo sonrakı FoodLog-u silmir. Çıxım məlum deyil → bişmiş 100 q və şəkilli porsiya yox; dəqiq məlum pay üçün batch arifmetikası mümkündür.

- PNG → 07 / 26-28; 08 / 31-32; 09 / 33-36; 11 / 41-44.
- Çatışmayan vəziyyət → [editor](#editor), [swap](#swap), [undo](#undo), [photo-recipe](#photo-recipe), [portion](#portion).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**25.10** — A-10 Lokal yazı xətası, iki toxunuş, retry, restart, DB reopen, timezone/gecə yarısı, edit/delete və köhnə cihazdan tombstone sync sınaqları ayrı sübutlarla aparılır. Simulyasiya fiziki disk-full və real cihazın yoxlanması sayılmır. Sync quota/token xətası məlumatı itirmir.

- PNG → 06 / 23; 03 / 12.
- Çatışmayan vəziyyət → [write](#write), [sync](#sync), [log](#log).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**25.11** — A-11 Hüquq/etiket/attribution, Play deklarasiyası və məxfilik/data inventarı release checklist-də faktiki yoxlanır. K25/K26 ownership/token/replay testləri, K29 retention/delete/export yoxlaması, K30 mağaza entitlement sübutu ayrıdır. Yerli CI bunları avtomatik təsdiqləmir.

- PNG → 05 / 19; 06 / 21.
- Çatışmayan vəziyyət → [privacy](#privacy), [billing](#billing).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**25.12** — A-12 Mənbə və policy versiyası dəyişəndə keçmiş snapshot sabit qalır; geri çəkilmiş resept/policy yeni plan/swap üçün görünmür. Update yarımçıq, hash səhv və schema uyğunsuzdursa katalog atomik rədd edilir; təhlükəsiz əvvəlki versiya və aydın status saxlanır.

- PNG → 07 / 25; D03 / Menyu.
- Çatışmayan vəziyyət → [withdrawal](#withdrawal).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**25.13** — A-13 Bitmə sübutu hər tələb üçün ID → Kxx → test/insan baxışı/cihaz sınağı → artefakt/commit → nəticə → yoxlanmamış məhdudiyyət əlaqəsidir. CI yalnız merge olunmuş main-də APK-sız lint/compile/JVM/data/parity yoxlayır. Tələb yazmaq, mənbə oxumaq və yaşıl CI klinik düzgünlüyə zəmanət deyil.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Yoxlama protokolu və fixture: müstəqil hesab, test nəticəsi və ya pilot hesabatı tələb olunur; PNG bu sübutun əvəzi deyil.

**25.14** — A-14 Bu birləşdirmənin çıxışı tam funksional tətbiqin implementasiya müqaviləsidir. Açıq buraxılış asılılıqları: G2 klinik baxış və konkret policy, G3 məzmun/hüquq/dil/çıxım, G4 region qiyməti, G5 faktiki backend/foto/data lifecycle, G6 monetizasiya sərhədi, G7 cihaz/pilot. Sahibi/kliniki rol/QA bunları sübutla bağlayır; çatışmayan qərarlar məhsulun hazır statusunda gizlədilmir.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Planın idarəetməsi, resurs/rol qərarı, status və prioritet tarixi: versiya, təsdiq/aktivləşmə izi və tələb olunan release sübutu ilə izlənir; ayrıca ekran deyil.

### 26. Konkret enerji modeli və aktivlik seçimi — tədqiqat təklifi

**26.1** — NP-01 Bu əlavənin statusu RESEARCH_PROPOSAL-dır. Dəqiq rəqəmlər və failure davranışı artıq müəyyənləşdirilib; clinical approval isə yoxdur. Kanonik nutrition_policy_candidate obyekti JSON maşın müqaviləsidir; ayrıca nutrition-policy-candidate.json onun törəməsidir. Heç biri hazır Kotlin NutritionPolicy payload-u deyil və production-a import edilmir.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Planın idarəetməsi, resurs/rol qərarı, status və prioritet tarixi: versiya, təsdiq/aktivləşmə izi və tələb olunan release sübutu ilə izlənir; ayrıca ekran deyil.

- Cari qərar → Status hissəsini §30.1–30.6 ilə birlikdə oxu: bildirilmiş baxış və enerji/makro aktivləşməsi var; kataloq və faktiki implementasiya qəbulunu əvəz etmir.

**26.2** — NP-02 Tövsiyə olunan namizəd model: NASEM 2023 adult EER, Health Canada adult cədvəli ilə tutuşdurulub. Birbaşa gündəlik enerji hesabıdır; Mifflin REE×1,2/1,375/1,55/1,725 üsulu seçilmir. Mifflin §5 və köhnə K08 snapshot-larında tarixi/test model kimi qalır; iki nəticə ortalanmır və ya üstünə əlavə PAL vurulmur.

- PNG → 01 / 04.
- Çatışmayan vəziyyət → [evidence](#evidence).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**26.3** — NP-03 A=yaş(il), H=boy(sm), W=cari çəki(kq), nəticə kkal/gün. Kişi: inactive=753,07−10,83A+6,50H+14,10W; low_active=581,47−10,83A+8,30H+14,94W; active=1004,82−10,83A+6,52H+15,91W; very_active=−517,88−10,83A+15,61H+19,11W. Qadın: inactive=584,90−7,01A+5,72H+11,71W; low_active=575,77−7,01A+6,60H+12,14W; active=710,25−7,01A+6,54H+12,34W; very_active=511,83−7,01A+9,07H+12,56W. Əmsallar S23 adult cədvəli və S24 Appendix G §8.3-dəndir.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Hesab/idxal/schema/migration/backend müqaviləsi: kod, DB və sərhəd testləri tələb olunur; ayrıca istehlakçı ekranı tələb edilmir.

**26.4** — NP-04 Dörd activity_code istifadə edilir: inactive/low_active/active/very_active. Rəsmi adult PAL intervalları kateqoriya kontekstidir, vurulan əmsal deyil: [1,00;1,53), [1,53;1,68), [1,68;1,85), [1,85;2,50). İstifadəçi cihazın ölçdüyü kimi PAL rəqəmi daxil etmir; tipik iş günü və məşq daxil olmaqla bütün günlük hərəkətini S23 nümunələrinə baxıb təsdiqləyir.

- PNG → 01 / 03.
- Çatışmayan vəziyyət → [activity](#activity).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

- Cari qərar → Köhnə üç aktivlik təsviri §26.4–26.6-dakı dörd kateqoriya və yenidən təsdiq ilə tamamlanmalıdır.

**26.5** — NP-05 Aktivlik sualı: “Adi günündə iş, ev işi, gediş-gəliş və məşq birlikdə nə qədər hərəkət edirsən?” Konkret seçim köməyi S23 nümunələrinə bağlıdır: inactive — adi günlük işlər, təxminən 30 dəq gəzinti və 90 dəq yüngül/orta ev işi; low_active — adi günlük işlərə əlavə 60–80 dəq 5–7 km/saat gəzinti; active — əlavə 30–50 dəq belə gəzinti, 45 dəq orta velosiped və 40 dəq cüt tennis; very_active — əlavə 45 dəq orta velosiped, 25 dəq 10 km/saat qaçış və 60 dəq cüt tennis. Bunlar tələb olunan məşq resepti və ya dəqiq ekvivalentlik testi deyil, mənbənin gündəlik fəaliyyət nümunələridir. İstifadəçi ən yaxın təsviri təsdiqləyir; uyğun gəlmirsə/əmin deyilsə ACTIVITY_UNCERTAIN və hədəfsiz gündəlik seçilir. Həftəlik məşq sayı təkbaşına kateqoriya yaratmır; bu sadə özünü-hesabat üsulunun ayrıca pilotu tələb olunur.

- PNG → 01 / 03.
- Çatışmayan vəziyyət → [activity](#activity), [no-target](#no-target).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

- Cari qərar → Köhnə üç aktivlik təsviri §26.4–26.6-dakı dörd kateqoriya və yenidən təsdiq ilə tamamlanmalıdır.

**26.6** — NP-06 Köhnə üç sadə aktivlik seçimi dörd kateqoriyaya səssiz xəritələnmir; yenidən təsdiq tələb olunur. Low_active ayrıca seçimdir; “bir az”ı həmişə active saymaq olmaz. Ölçülmüş PAL sonradan dəstəklənərsə yalnız ayrıca metod və versiya ilə gəlir; bu modelin üzərinə wearable kalorisini ikinci dəfə əlavə etmirik.

- PNG → 01 / 03.
- Çatışmayan vəziyyət → [activity](#activity), [reassess](#reassess).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

- Cari qərar → Köhnə üç aktivlik təsviri §26.4–26.6-dakı dörd kateqoriya və yenidən təsdiq ilə tamamlanmalıdır.

**26.7** — NP-07 İlkin pilot üçün 19–64 yaş və 18,5≤BMI<30 məhsul əhatəsi təklif edilir. 19 aşağı sərhədi adult EER sətrinə uyğundur; 64 yuxarı və BMI<30 məhdudiyyəti modelin elmi etibarlılıq sərhədi deyil, ilkin fərdiləşdirmə əhatəsini daraldan məhsul təklifidir. 18 yaş və 65+ üçün bu policy hədəf vermir; həmin qruplar üçün ayrıca policy lazımdır. BMI fərdi diaqnoz deyil; etnik risk fərqi və yüksək əzələ kütləsi avtomatik qiymətləndirilmir.

- PNG → 01 / 02; 12 / Sağlamlıq.
- Çatışmayan vəziyyət → [health](#health), [no-target](#no-target).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**26.8** — NP-08 §6 istisnaları saxlanır və diabet, səbəbsiz çəki itkisi, yüksək performans idmanı/BMI-ni yanıltan bədən tərkibi üçün açıq sual tələb olunur; “xeyr” fərz edilmir. Hamiləlik/südvermə, yemək pozuntusu riski, böyrək xəstəliyi, tibbi pəhriz və qidalanmaya təsir edən müalicədə avtomatik hədəf yoxdur. Boy/çəki/yaş müsbət sonlu və istifadəçi təsdiqli olmalıdır; düzəlişdə vahid qarışıqlığı soruşulur, səssiz çevirmə ilə klinik qərar verilmir.

- PNG → 12 / Sağlamlıq; 01 / 02.
- Çatışmayan vəziyyət → [health](#health), [inputs](#inputs).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

### 27. Məqsədə görə düzəliş, minimum və maksimum davranışı

**27.1** — NP-09 M=§26 ilə hesablanan maintenance EER. maintain və healthy üçün target=M, enerji düzəlişi 0-dır. lose yalnız 25≤BMI<30 və bütün uyğunluq şərtləri ödənəndə target=M−500. gain yalnız 18,5≤BMI<25 və səbəbsiz çəki itkisi/risk yoxdursa target=M+300. Əhatəyə uyğun olmayan məqsəd GOAL_OUT_OF_SCOPE qaytarır; səssiz maintain-ə çevrilmir. BMI sərhədləri yuvarlaqlaşdırılmadan müqayisə edilir.

- PNG → 00 / Məqsəd; 01 / 04.
- Çatışmayan vəziyyət → [goals](#goals), [floor](#floor).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**27.2** — NP-10 −500 S25-dəki klinik defisit variantının, +300 S14 NHS-dəki +300–500 nümunəsinin aşağı ucundan seçilmiş namizəd məhsul parametridir. Mənbə heç birinin bu app-də hamıya təhlükəsiz olmasını sübut etmir; G2 reviewer-i seçim və dəstək/follow-up üsulunu təsdiqləməlidir. −750, −1000 və ya istifadəçinin sürətli deadline-na uyğun daha böyük defisit bu policy-də yoxdur. Çəki dəyişikliyinin sürəti və tarix üzrə nəticə zəmanəti verilmir.

- PNG → 01 / 04.
- Çatışmayan vəziyyət → [scope](#scope), [evidence](#evidence).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**27.3** — NP-11 Namizəd məhsul aşağı sərhədi: qadın üçün 1500, kişi üçün 1800 kkal/gün. Bunlar S25-dəki nümunə diapazonların yuxarı uclarından götürülmüş ehtiyatlı məhsul guardrail təklifidir; yeni universal klinik minimum və ya təhlükəsizlik zəmanəti deyil. Həm target, həm faktiki təklif olunan gün bu həddən aşağıdırsa NO_TARGET/NO_FEASIBLE_PLAN qaytarılır. Target həddə zorla qaldırılmır, əlavə yemək uydurulmur.

- PNG → 01 / 04; 13 / Menyu.
- Çatışmayan vəziyyət → [floor](#floor).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**27.4** — NP-12 NICE §1.16.8-də 800–1200 kkal specialist xidmət kontekstindədir; app bu rejimi dəstəkləmir. NIDDK alətindəki 1000 kkal texniki minimum bu app üçün təhlükəsiz minimum kimi götürülmür. Böyük və kiçik enerji ehtiyaclı insanlara 1200/1500 sabit rasion paylanmır; əvvəl M hesablanır, sonra məqsəd və uyğunluq yoxlanır.

- PNG → 01 / 04.
- Çatışmayan vəziyyət → [floor](#floor), [scope](#scope).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**27.5** — NP-13 Universal elmi gündəlik maksimum tapıldığı iddia edilmir. Bu policy-də klinik “4000/5000” tavanı uydurulmur: maintain/healthy üçün M ətrafında §27-dəki ±5% məhsul toleransı, lose üçün M-dən aşağı və maksimum 500 defisit, gain üçün M+300. Bundan əlavə dərc edilmiş katalogun real porsiya diapazonlarında uyğun gün yoxdursa NO_FEASIBLE_PLAN; həddən böyük süni porsiya yoxdur. Makro və nutrient yoxlamaları bu enerji hədlərindən ayrıca sərtdir.

- PNG → 13 / Menyu.
- Çatışmayan vəziyyət → [floor](#floor), [generation](#generation), [portion](#portion).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**27.6** — NP-14 Menyu axtarışında ±5% enerji toleransı daxili məhsul parametridir. maintain/healthy: L=max(0,95M; sex_floor), U=1,05M. lose: L=max(0,95T; sex_floor; M−500), U=min(1,05T; M) və əlavə E<M. gain: L=max(0,95T; sex_floor; M), U=min(1,05T; M+300) və əlavə E>M. Beləliklə arıqlama defisiti 500-dən, gain surplus-u 300-dən böyümür; maintain üçün real porsiya kombinasiyasına tolerans qalır. L>U və ya heç bir real kombinasiya yoxdursa plan yoxdur; müqayisə yuvarlaqlaşdırmadan aparılır.

- PNG → 13 / Menyu.
- Çatışmayan vəziyyət → [generation](#generation), [floor](#floor).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**27.7** — NP-15 Günlük hədəf 3/4/5 slot arasında bölünür; seçilmiş say target-i artırmır. İlk slot template çəkiləri: 3 yemək 0,30/0,35/0,35; 4 yemək 0,25/0,35/0,30/0,10; 5 yemək 0,25/0,10/0,30/0,10/0,25. Bunlar məhsul template parametridir, klinik norma deyil; günün sərt yoxlamaları əsasdır və slot payı real porsiyaya görə dəyişə bilər.

- PNG → 02 / 06; D03 / Menyu.
- Çatışmayan vəziyyət → [slots](#slots).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**27.8** — NP-16 İlk baxış xatırlatması 14 gün, ən azı 3 könüllü çəki ölçməsi son 14 gündə; bu məhsul workflow parametridir, klinik baxış intervalı deyil. Ölçmə yoxdursa trend/kalori titrasiyası edilmir. Yeni çəki/aktivlik/yaş məlumatı yeni hesab və səbəb önizləməsi yaradır, təsdiqdən sonra gələcəyə tətbiq olunur. Avtomatik həftəlik −100/−200, deadline-a çatdırma və 7700 kkal/kq xətti proqnozu yoxdur. Yeni simptom və səbəbsiz dəyişiklikdə hədəf dayandırılır, peşəkar dəstək yolu verilir.

- PNG → 05 / 17-20; 01 / 04.
- Çatışmayan vəziyyət → [reassess](#reassess), [weight](#weight).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

### 28. Makro vektorunun konkret hesabı və məlumat tərifi

**28.1** — NP-17 Əsas reference set EFSA adult-dur: yağ 20–35% enerji, karbohidrat 45–60%, protein PRI 0,83 q/kq/gün və lif AI 25 q/gün. Bunlar müxtəlif növ referenslərdir; PRI optimal idman/arıqlama dozası və UL deyil. WHO duz/sərbəst şəkər/doymuş/trans yağ qaydaları ayrıca nutrient policy kimi saxlanır; ABŞ AMDR diapazonları ilə qarışıq ən-sərt seçim edilmir.

- PNG → 05 / 19.
- Çatışmayan vəziyyət → [evidence](#evidence), [macro](#macro).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**28.2** — NP-18 T=target kkal, W=cari faktiki kq. Namizəd hədəf: P=max(0,83×W; 0,20×T/4) q; F=0,30×T/9 q; C=(T−4P−9F)/4 q. Protein çəki bazası bu dar BMI əhatəsində cari ölçülmüş çəki olur; “ideal/adjusted weight” gizli əvəz edilmir. 20/30/50 bölgüsü planlama başlanğıcıdır, EFSA-nın məcburi default-u deyil. P referensi 20% paydan böyükdürsə C azalır.

- PNG → 01 / 04.
- Çatışmayan vəziyyət → [macro](#macro), [evidence](#evidence).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**28.3** — NP-19 C-nin enerji payı 45–60%-dən çıxırsa, P/F/C sonlu-müsbət deyilsə və ya komponentlər T-yə yığılmırsa MACRO_INFEASIBLE qaytarılır. Bu seçilmiş split-də P>25% enerji C-ni 45%-dən aşağı salır; 25% burada cəbri feasibility sərhədidir, protein üçün klinik maksimum/UL deyil. Başqa rəqəmlə səssiz düzəltmək, protein minimumunu azaltmaq və high-protein diet açmaq olmaz.

- PNG → 01 / 04.
- Çatışmayan vəziyyət → [macro](#macro), [no-target](#no-target).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**28.4** — NP-20 Protein/fat/carbohydrate üçün 4/9/4 faktorları planlama vektoru üçündür. Məhsulun rəsmi enerji sahəsi onlarla zorla əvəz edilmir. EFSA karbohidrat referensinə uyğun available/glycaemic carbohydrate əsası ayrıca normallaşdırılır; FDC carbohydrate-by-difference və lif eyni tərif kimi götürülmür. Çıxma yalnız mənbə metoduna uyğun sübutlu çevirmə ilə mümkündür; əks halda makro reference yoxlaması UNKNOWN-dur.

- PNG → 10 / 38.
- Çatışmayan vəziyyət → [macro](#macro), [food-detail](#food-detail).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**28.5** — NP-21 Faktiki menyu üçün enerji §27 L/U daxilində; protein ən azı 0,83×W q; yağ 20–35% enerji; uyğun tərifli karbohidrat 45–60%; lif ≥25 q/gün yoxlanır. P/F/C target-lərinə yaxınlıq yumşaq skordur; “optimal protein” vədi yoxdur. Gündə hesablanan qramlar və mənbə nutrientləri ayrıdır. Natamam protein/lif/carbohydrate əsası sərt keçid üçün PASS olmur.

- PNG → D03 / Menyu.
- Çatışmayan vəziyyət → [macro](#macro), [rules](#rules).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**28.6** — NP-22 WHO total fat ≤30% ümumi maarif qaydası ayrıca advisory nəticədir; bu namizədin əsas yağ RI-si EFSA 20–35-dir. F=30% hədəf kimi seçilir; 30%-dən çox faktiki gün WHO üzrə keçdi sayılmır. İstifadəçiyə “bütün təşkilatların qaydaları keçdi” iddiası yoxdur. WHO digər seçilmiş nutrient limitləri öz mənbə operatoru ilə saxlanır; sərbəst şəkər ümumi şəkərdən uydurulmur.

- PNG → 05 / 19.
- Çatışmayan vəziyyət → [macro](#macro), [evidence](#evidence).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**28.7** — NP-23 Vegan/vegetarian və digər məhdud seçimlər eyni rəqəmlə avtomatik tam balanslı sayılmır. Kitabxana, protein keyfiyyəti, B12 və digər nutrient əhatəsi ayrıca yoxlanır; çatışmazlıq diaqnozu və supplement dozası verilmir. İdmançı, xəstəlik və bu pilot əhatəsindən kənar qrup üçün daha yüksək q/kq rəqəmi bu policy-yə əlavə edilmir.

- PNG → 02 / 05; 13 / Menyu.
- Çatışmayan vəziyyət → [macro](#macro), [generation](#generation), [scope](#scope).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

### 29. Nümunə hesablar, implementasiya fərqi və təsdiq protokolu

**29.1** — NP-24 Müstəqil arifmetik nümunələr (ölçmə və klinik təsdiq deyil): 30 yaş, 175 sm, 70 kq inactive kişi M=2552,67; eyni qadın M=2195,30 kkal/gün. Məqsəd maintain/healthy olduqda T=M. Bu birbaşa EER rəqəmləri §25-dəki Mifflin REE 1648,75/1482,75 ilə eyni göstərici deyil. İki hesabın fərqi formula xətası kimi gizlədilmir. Kişi maintain üçün real menyu enerji intervalı 2425,0365–2680,3035-dir; tək bir dəqiq floating-point toplam tələb edilmir.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Yoxlama protokolu və fixture: müstəqil hesab, test nəticəsi və ya pilot hesabatı tələb olunur; PNG bu sübutun əvəzi deyil.

**29.2** — NP-25 30 yaş, 175 sm, 85 kq inactive kişi: BMI≈27,7551; M=2764,17; lose T=2264,17; P=113,2085 q, F≈75,4723 q, C=283,02125 q. Günlük enerji axtarışı L=2264,17; U=2377,3785. 30 yaş, 175 sm, 70 kq inactive kişi gain: T=2852,67; P=142,6335; F=95,089; C=356,58375; L=2710,0365; U=2852,67. Bunlar sintetik tədqiqat fixture-ləridir, insana verilmiş plan deyil.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Yoxlama protokolu və fixture: müstəqil hesab, test nəticəsi və ya pilot hesabatı tələb olunur; PNG bu sübutun əvəzi deyil.

**29.3** — NP-26 Floor nümunəsi: 64 yaş, 145 sm, 55 kq inactive qadın: BMI≈26,1593; M=1609,71; lose raw T=1109,71 → ENERGY_BELOW_PRODUCT_FLOOR, nəticə hədəfsizdir. 1500-ə clamp edilmir. Eyni profil maintain T=1609,71 üçün floor keçilə bilər, amma G2/G3 və bütün digər yoxlamalar yenə tələb olunur.

- PNG → 01 / 04.
- Çatışmayan vəziyyət → [floor](#floor).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**29.4** — NP-27 Sərhəd halları: yaş18/19/64/65; BMI18,5/25/30; cins yoxdur; köhnə activity code; əmin olmayan aktivlik; çox kiçik/NaN/sonsuz giriş; bütün sağlamlıq cavabları boş; gain overweight; lose normal BMI; floor-dan aşağı nəticə; makro remainder uyğunsuz; boş katalog. Hər biri üçün status və heç bir snapshot yazılmaması yoxlanmalıdır. Müsbət halda yalnız yeni versiyalı preview/confirm nəticəsi yaranır.

- PNG → 01 / 02-04; 12 / Sağlamlıq; 13 / Menyu.
- Çatışmayan vəziyyət → [health](#health), [activity](#activity), [floor](#floor), [macro](#macro), [generation](#generation).
- Ayrı sübut → Bu bəndin tam davranışı üçün ayrıca kod/test/məzmun/insan və ya cihaz sübutu lazımdır; PNG bunu təsdiqləmir.

**29.5** — NP-28 Mövcud K08 contractVersion=1 Mifflin-only və activityMapping scalar istifadə edir; bu EER modelini həmin sahələrə zorla yerləşdirmək olmaz. Contract v2 üçün formula dispatch, 4 kateqoriya üzrə əmsal cədvəli, sex-specific floor, goal/BMI scope, əlavə risk cavabları, max/remainder makro ifadəsi və RuleResult-lar lazımdır. Formula və snapshot migration geriyə uyğun olmalı, köhnə tarixçəni dəyişməməlidir. K13/K14 işinə bu branch-dən kod daşınmır.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Hesab/idxal/schema/migration/backend müqaviləsi: kod, DB və sərhəd testləri tələb olunur; ayrıca istehlakçı ekranı tələb edilmir.

**29.6** — NP-29 Təsdiq addımı artıq rəqəm seçmək üçün boş tapşırıq deyil: reviewer konkret NP-01–NP-28 və maşın obyektinə APPROVE/REVISE/REJECT verir. Ayrıca qiymətləndirilən qərarlar: EER modelinin AZ əhali/özünü-hesabat məhdudiyyəti; dar yaş/BMI əhatəsi; −500/+300; 1500/1800 məhsul guardrail-i; 20/30/50+PRI; 14 günlük workflow və real dəstək/follow-up. Reviewer adı, ixtisas/səlahiyyət, tarix, evidence versiyası və dəyişiklik səbəbi qeyd olunur. G2 təsdiqi uydurulmur.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Planın idarəetməsi, resurs/rol qərarı, status və prioritet tarixi: versiya, təsdiq/aktivləşmə izi və tələb olunan release sübutu ilə izlənir; ayrıca ekran deyil.

- Cari qərar → Status hissəsini §30.1–30.6 ilə birlikdə oxu: bildirilmiş baxış və enerji/makro aktivləşməsi var; kataloq və faktiki implementasiya qəbulunu əvəz etmir.

**29.7** — NP-30 Branch tədqiqat statusudur: app kodu dəyişmir, production policy aktivləşmir, main-ə merge yoxdur və branch CI işlədilmir. Bütün derived plan görünüşləri bu branch-də yenilənir. Yalnız istifadəçi sonradan inteqrasiya istəyəndə cari main ilə fərq baxışı, ayrı implementasiya, workflow finish və post-merge CI aparılır; o vaxta qədər əsas işin planı dəyişmir.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Planın idarəetməsi, resurs/rol qərarı, status və prioritet tarixi: versiya, təsdiq/aktivləşmə izi və tələb olunan release sübutu ilə izlənir; ayrıca ekran deyil.

- Cari qərar → Status hissəsini §30.1–30.6 ilə birlikdə oxu: bildirilmiş baxış və enerji/makro aktivləşməsi var; kataloq və faktiki implementasiya qəbulunu əvəz etmir.

**29.8** — NP-31 Yoxlama fərqi: bu işdə rəsmi formullar/istinadlar və sintetik hesab arifmetikası tutuşdurulur; runtime K08-v2, 4 seçimli onboarding, real yemək planı, cihaz/klinik pilot və effektivlik sınağı hələ aparılmır. S25 köhnə guideline-dir, NHS S14 baxış tarixi 2023 və növbəti baxış 2026-dır; NICE və NASEM bəzi hissələri yalnız rəsmi indeks çıxarışı ilə yoxlanıb. Mənbə əlçatanlığı və tarix məhdudiyyəti approval paketində açıq saxlanır.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Yoxlama protokolu və fixture: müstəqil hesab, test nəticəsi və ya pilot hesabatı tələb olunur; PNG bu sübutun əvəzi deyil.

- Cari qərar → Status hissəsini §30.1–30.6 ilə birlikdə oxu: bildirilmiş baxış və enerji/makro aktivləşməsi var; kataloq və faktiki implementasiya qəbulunu əvəz etmir.

### 30. Planın hamısı üzrə bildirilmiş mütəxəssis təsdiqi

**30.1** — Cari status: SPECIALIST_APPROVAL_REPORTED. Əhatə: 2.1-research versiyasındakı bütün yazılı plan, §1–29 və nutrition_policy_candidate hesab qaydaları. Bildiriş 5 noyabr 2026 tarixində bu söhbətdə alınıb; bu, mütəxəssisin yoxlama tarixi deyil.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Planın idarəetməsi, resurs/rol qərarı, status və prioritet tarixi: versiya, təsdiq/aktivləşmə izi və tələb olunan release sübutu ilə izlənir; ayrıca ekran deyil.

**30.2** — Mütəxəssisin adı istifadəçinin istəyi ilə yazılmır. İxtisas və faktiki yoxlama tarixi bildirilməyib; həmin sahələr naməlum qalır, tarix və imza uydurulmur. Qeyd istifadəçi bildirişinə əsaslanır; ayrıca sənəd təqdim olunmayıb və müstəqil yoxlama aparıldığı iddia edilmir.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Planın idarəetməsi, resurs/rol qərarı, status və prioritet tarixi: versiya, təsdiq/aktivləşmə izi və tələb olunan release sübutu ilə izlənir; ayrıca ekran deyil.

**30.3** — Əvvəlki bölmələrdəki “mütəxəssis yoxdur” və “təsdiq alınmayıb” sözləri əvvəlki iş vəziyyətini təsvir edir. Plan üzrə bildirilən mütəxəssis baxışının cari statusu bu bölmədir. Sonradan dəyişdirilən qaydalar əvvəlki təsdiq əhatəsinə səssiz daxil edilmir; təsdiq olunan versiya və hash ayrıca saxlanır.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Planın idarəetməsi, resurs/rol qərarı, status və prioritet tarixi: versiya, təsdiq/aktivləşmə izi və tələb olunan release sübutu ilə izlənir; ayrıca ekran deyil.

**30.4** — Yazılı planın təsdiqi tətbiqin hazır, klinik sınaqdan keçmiş və ya rəsmi tənzimləyici təsdiqli olması demək deyil. Android implementasiyası, migration, hesab testləri, hər reseptin mənbə/allergen/hüquq/çıxım sübutu, məlumat təhlükəsizliyi və real cihaz qəbulunun faktiki statusları ayrıca qalır. Bu qeydlə production_enabled avtomatik açılmır.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Planın idarəetməsi, resurs/rol qərarı, status və prioritet tarixi: versiya, təsdiq/aktivləşmə izi və tələb olunan release sübutu ilə izlənir; ayrıca ekran deyil.

**30.5** — Növbəti texniki işdə mütəxəssis tapılmaması plan üzrə açıq blok kimi göstərilmir; istifadəçinin bildirdiyi baxış statusu nəzərə alınır. Aktivləşdirmə üçün hələ lazım olan implementasiya və yoxlama tələbləri konkret sübutlarla tamamlanır; naməlum reviewer/date sahələri saxta dəyərlə doldurulmur.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Planın idarəetməsi, resurs/rol qərarı, status və prioritet tarixi: versiya, təsdiq/aktivləşmə izi və tələb olunan release sübutu ilə izlənir; ayrıca ekran deyil.

**30.6** — 5 noyabr 2026: istifadəçinin açıq göstərişi ilə nutrition_policy_candidate.production_enabled=true edilir; aktivləşmə cari avtomatik enerji/makro hesabına aiddir. Yaş/BMI, sağlamlıq, aktivlik və ölçmə təsdiqi, minimum enerji və makro feasibility yoxlamaları saxlanır. Bu texniki aktivləşmə yeni klinik təsdiq deyil; əvvəlki təsdiqin hash-i dəyişmir. Katalog/real menyu buraxılışı ayrıca qalır; köhnə snapshot-lar yenidən yazılmır.

- PNG → N/A — ayrıca ekran tələbi deyil.
- Çatışmayan vəziyyət → N/A — proqram/məzmun/sübut tələbi.
- Ayrı sübut → Planın idarəetməsi, resurs/rol qərarı, status və prioritet tarixi: versiya, təsdiq/aktivləşmə izi və tələb olunan release sübutu ilə izlənir; ayrıca ekran deyil.

### Resept fərdiləşdirməsi — T01–T19

**T01** — plovdan kökü çıxar → kök hesabdan və hazırlıq addımlarından çıxır; yalnız həmin plan hissəsi dəyişir.

- PNG → 07 / 26; 10 / 39.
- Çatışmayan vəziyyət → [editor](#editor), [instructions](#instructions).
- Ayrı sübut → Ssenarinin əvvəl/sonra DB, plan, alış-veriş və FoodLog nəticəsi ayrıca testlə təsdiqlənməlidir.

**T02** — yağı 10 q-dan 15 q-a qaldır → mənbə dəyərinə görə resept/porsiya/gün və həftə proqnozu artır; faktiki qəbul dəyişmir.

- PNG → 07 / 26,28.
- Çatışmayan vəziyyət → [forecast](#forecast), [editor](#editor).
- Ayrı sübut → Ssenarinin əvvəl/sonra DB, plan, alış-veriş və FoodLog nəticəsi ayrıca testlə təsdiqlənməlidir.

**T03** — göbələk əlavə et → yeni inqrediyent, təlimat uyğunluğu və alış-veriş miqdarı görünür.

- PNG → 07 / 27-28; 11 / 42.
- Çatışmayan vəziyyət → [editor](#editor), [instructions](#instructions), [shopping](#shopping).
- Ayrı sübut → Ssenarinin əvvəl/sonra DB, plan, alış-veriş və FoodLog nəticəsi ayrıca testlə təsdiqlənməlidir.

**T04** — reseptə dörd porsiya seç → qazan miqdarı böyüyür, istifadəçi bir porsiya yeyirsə dörd porsiya gündəliyə yazılmır.

- PNG → 07 / 26; D02 / Qazan.
- Çatışmayan vəziyyət → [portion](#portion).
- Ayrı sübut → Ssenarinin əvvəl/sonra DB, plan, alış-veriş və FoodLog nəticəsi ayrıca testlə təsdiqlənməlidir.

**T05** — çərşənbə şamını cümə şamı ilə dəyiş → hər iki hissə bir dəfə yer dəyişir; qalan günlər sabitdir.

- PNG → 08 / 31-32; 11 / 41.
- Çatışmayan vəziyyət → [swap](#swap), [atomic](#atomic).
- Ayrı sübut → Ssenarinin əvvəl/sonra DB, plan, alış-veriş və FoodLog nəticəsi ayrıca testlə təsdiqlənməlidir.

**T06** — iki gündən biri uyğun gəlmir → uyğun olmayan yer dəyişmə saxlanmır; səbəb və mümkün porsiya təklifi göstərilir.

- PNG → 10 / 37.
- Çatışmayan vəziyyət → [swap](#swap).
- Ayrı sübut → Ssenarinin əvvəl/sonra DB, plan, alış-veriş və FoodLog nəticəsi ayrıca testlə təsdiqlənməlidir.

**T07** — başqa yemək seç → uyğun porsiya və dəyişiklik əvvəlcədən görünür; yalnız həmin hissə əvəzlənir.

- PNG → 08 / 30; 11 / 43.
- Çatışmayan vəziyyət → [replace](#replace).
- Ayrı sübut → Ssenarinin əvvəl/sonra DB, plan, alış-veriş və FoodLog nəticəsi ayrıca testlə təsdiqlənməlidir.

**T08** — mənbə nutrienti yoxdur → nəticə natamamdır; sıfır və ya “tam uyğundur” yazılmır.

- PNG → 10 / 38.
- Çatışmayan vəziyyət → [food-detail](#food-detail), [rules](#rules).
- Ayrı sübut → Ssenarinin əvvəl/sonra DB, plan, alış-veriş və FoodLog nəticəsi ayrıca testlə təsdiqlənməlidir.

**T09** — artıq yeyilmiş yeməyi köçürməyə çalış → tarixçə dəyişmir; ayrıca düzəliş yolu göstərilir.

- PNG → 08 / 31.
- Çatışmayan vəziyyət → [swap](#swap), [log](#log).
- Ayrı sübut → Ssenarinin əvvəl/sonra DB, plan, alış-veriş və FoodLog nəticəsi ayrıca testlə təsdiqlənməlidir.

**T10** — səhvən iki dəfə yadda saxla → bir əməliyyat yaranır; alış-veriş və yemək dublikatı yoxdur.

- PNG → 07 / 28; 11 / 42.
- Çatışmayan vəziyyət → [write](#write), [atomic](#atomic).
- Ayrı sübut → Ssenarinin əvvəl/sonra DB, plan, alış-veriş və FoodLog nəticəsi ayrıca testlə təsdiqlənməlidir.

**T11** — reseptdə allergen yaranır → plan uyğunluğu rədd edilir və səbəb göstərilir; faktiki yemək qeyd etmək ayrıca qalır.

- PNG → 02 / 05; 07 / 28.
- Çatışmayan vəziyyət → [allergen](#allergen), [log](#log).
- Ayrı sübut → Ssenarinin əvvəl/sonra DB, plan, alış-veriş və FoodLog nəticəsi ayrıca testlə təsdiqlənməlidir.

**T12** — ləğv et və ya geri qaytar → plan və alış-veriş uyğun əvvəlki vəziyyətə qayıdır; sonrakı faktiki qeyd səssiz silinmir.

- PNG → 10 / 40; 11 / 41.
- Çatışmayan vəziyyət → [undo](#undo), [draft](#draft).
- Ayrı sübut → Ssenarinin əvvəl/sonra DB, plan, alış-veriş və FoodLog nəticəsi ayrıca testlə təsdiqlənməlidir.

**T13** — “Xeyr” + yarım porsiya → reseptin yarım porsiyası yazılır; foto təxmini ilə ikinci qeyd yaranmır.

- PNG → 09 / 33,35-36.
- Çatışmayan vəziyyət → [photo-recipe](#photo-recipe).
- Ayrı sübut → Ssenarinin əvvəl/sonra DB, plan, alış-veriş və FoodLog nəticəsi ayrıca testlə təsdiqlənməlidir.

**T14** — “Bəli” + yağ miqdarı artıb → məhz verilmiş resept açılır; yağ dəyişir, faktiki hesab yenilənir, başqa yeməyə keçid yoxdur.

- PNG → 09 / 33-34,36.
- Çatışmayan vəziyyət → [photo-recipe](#photo-recipe), [editor](#editor).
- Ayrı sübut → Ssenarinin əvvəl/sonra DB, plan, alış-veriş və FoodLog nəticəsi ayrıca testlə təsdiqlənməlidir.

**T15** — “Bəli” + kök çıxıb, göbələk əlavə edilib → faktiki variant saxlanır; əsas kitabxana və digər günlər dəyişmir.

- PNG → 09 / 34,36.
- Çatışmayan vəziyyət → [custom-version](#custom-version), [photo-recipe](#photo-recipe).
- Ayrı sübut → Ssenarinin əvvəl/sonra DB, plan, alış-veriş və FoodLog nəticəsi ayrıca testlə təsdiqlənməlidir.

**T16** — əvvəlcədən fərdiləşdirilmiş resept → redaktor baza kitabxanasından deyil, istifadəçiyə son verilən versiyadan başlayır.

- PNG → 09 / 34.
- Çatışmayan vəziyyət → [custom-version](#custom-version), [photo-recipe](#photo-recipe).
- Ayrı sübut → Ssenarinin əvvəl/sonra DB, plan, alış-veriş və FoodLog nəticəsi ayrıca testlə təsdiqlənməlidir.

**T17** — 4 porsiyalı qazan + 1 porsiya yeyilib → yalnız dörddə bir hissə gündəliyə yazılır.

- PNG → 09 / 35-36; D02 / Qazan.
- Çatışmayan vəziyyət → [portion](#portion).
- Ayrı sübut → Ssenarinin əvvəl/sonra DB, plan, alış-veriş və FoodLog nəticəsi ayrıca testlə təsdiqlənməlidir.

**T18** — foto modelinin fərziyyəsi istifadəçinin təsdiqi ilə ziddiyyətlidir → istifadəçi məlumatı səssiz əvəz edilmir; lazım olan qısa təsdiq göstərilir.

- PNG → 09 / 33-36.
- Çatışmayan vəziyyət → [model-conflict](#model-conflict).
- Ayrı sübut → Ssenarinin əvvəl/sonra DB, plan, alış-veriş və FoodLog nəticəsi ayrıca testlə təsdiqlənməlidir.

**T19** — “Gündəliyə əlavə et” düyməsinə təkrar basılır → idempotency_key ilə bir faktiki qeyd yaranır.

- PNG → 09 / 36; 11 / 44.
- Çatışmayan vəziyyət → [write](#write).
- Ayrı sübut → Ssenarinin əvvəl/sonra DB, plan, alış-veriş və FoodLog nəticəsi ayrıca testlə təsdiqlənməlidir.

## Yoxlama və davam

`python3 planning/build_design_requirement_map.py --check` mənbə mətnləri/ID-lər, PNG mövcudluğu və hash-lər, T01–T19 bütövlüyü, annotasiya əlaqələri və törəmə faylların cari olmasını yoxlayır. Bu, dizaynın semantik tamlığı, klinik baxış və tətbiq davranışının testi deyil.

Növbəti implementasiya üçün müvafiq bəndin boşluqlarını ayrıca screen/state renderlərinə ayır, `visual-review-plan.json`-a konkret requirement/state/reference və AZ/EN/RU × 100%/150% halları əlavə et. Hazır ekranın cari qəbulunu `design-discrepancies.md` və main-ə bağlı manifest/review sübutu ilə yoxla. Bu xəritə mövcud vizual borcu bağlamır və avtomatik baseline yaratmır.
