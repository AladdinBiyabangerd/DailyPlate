# Açıq dizayn uyğunsuzluqları — 5 noyabr 2026

Əsas: `93fe1cb` main və əvvəlki kod/render baxışı. Bu sənəd dizayn təsdiqi və ya
cari bütün ekranların yeni renderlə qəbulu deyil. `visual-policy.json` bu ekranları
təsdiqlənmiş baseline etmir. Agent siyahını düzəliş və faktiki qəbul sübutu ilə yeniləyir.
İstinadlar `design-concepts/simple-turquoise/` daxilindədir; plan §18–19 məcburidir.

## Hazır funksiyalarda açıq fərqlər

- **D03 · Həftəlik menyu — açıq.** `ui/menu/MenuScreen.kt` daxilində DatedMenu yeddi
  tam enli düymə və mətn kartları göstərir. `04-menyu-ve-alisveris.png`/00 istinadındakı
  yığcam həftə seçicisi, şəkilli yemək kartları, enerji/vaxt və hərəkət iyerarxiyası
  tam tətbiq edilməyib. Böyük şrift üçün əsaslandırılmış alternativ düzülüş saxlanır.
- **D04 · Resept detalları — açıq.** `ui/menu/RecipeDetailScreen.kt` mətn əsaslıdır;
  04/07 istinadlarındakı əsas yemək şəkli, şəkilli inqrediyent təqdimatı və vizual
  addım iyerarxiyası çatışmır. Foto və redaktorun gələcək funksiyaları ayrıca scope-dur.
- **D05 · Profil — açıq.** `ui/profile/ProfileScreen.kt` hazır redaktə/izah
  funksiyalarını göstərir, amma `05-irelileyis-ve-profil.png` profilinin illüstrasiyalı
  seçim sətirləri və ümumi iyerarxiyası tam tətbiq edilməyib. Hazır olmayan hesab,
  abunəlik və bildiriş keçidləri işlək kimi təqdim olunmamalıdır.
- **D06 · Əsas naviqasiya — qismən uyğun.** `ui/BootstrapScreen.kt` beş bölmə,
  ikonlar və seçilməni saxlayır. 00 istinadının qaldırılmış mərkəzi Əlavə et forması
  əvəzinə normal axındakı düymə istifadə olunur. Normal ölçüdə uyğunlaşdırma və
  böyük şriftdə əlçatanlıq ayrıca renderlə qiymətləndirilməlidir.

## Bağlanmış hissə — D02

- **D02 · Porsiya quruluşu — bağlı (5 noyabr 2026).** İstifadəçi B yeni dizaynını
  “okeydir yeni” ilə təsdiqlədi. İstinad:
  `design-concepts/simple-turquoise/approved/d02-portions.png` və uyğunlaşdırma qeydi `.txt`.
  Manual/axtarış qidasında dəqiq miqdar/vahid; əvvəlki qeyddə saxlanmış porsiyanın qatı;
  reseptdə hazırlanmış qazan və yeyilən şəxsi pay ayrıdır. Qazan sayı tərkibi və məlum
  çıxımı proporsional miqyaslayır; şəxsi nutrient hesabı saxlanır. Qram çevirməsi yalnız
  ölçülmüş bişmiş çıxım olduqda işləyir. Naməlum dəyər sıfır deyil; illüstrasiya ölçü fotosu deyil.
- İcra commitləri: `397e96e`, `42f6d29`, `3716691`, `785ba67`, `102ea0f`;
  render əhatəsi `a10c556`, qəbul xəritəsi `2b6822a`. Qəbul edilmiş main:
  `feca156eca1e5a380bed6679e4403a05ab92f1b7`.
- Dəqiq sübut:
  `.local-ci/visual/feca156eca1e5a380bed6679e4403a05ab92f1b7/review.json`;
  eyni qovluqdakı `manifest.json` və `renders/k10/d02-*.png`.
  AZ/EN/RU × 100%/150%, 192 təzə APK-sız native renderin yuxarı/aşağı və uzun resept
  formasında orta hissəsi faktiki müqayisə edilib. Böyük şrift və rus seçimi tam enli
  siyahı ilə uyğunlaşdırılıb; preview/təsdiq əsas, naviqasiya ikinci dərəcəlidir.
- Texniki qəbul ayrıca: 217 JVM testi, lint, Kotlin kompilyasiyası, token/kontrast,
  plan/kitabxana müqavilələri keçib. Yazı xətası və eyni hadisə ilə təkrar cəhd,
  önizləmənin qeyd yaratmaması, qazan/pay miqyası və naməlum nutrient ssenariləri yoxlanıb.
  Real istehsal kataloqundan resept axını, fiziki cihaz, klaviatura və TalkBack yoxlanmayıb;
  APK hazırlanmayıb. Bu qəbul digər ekranları və G3/G4 məzmun qapılarını təsdiqləmir.

## Yenilənmiş və yenidən yoxlanmalı hissələr

- **D01 · Əlavə et — əvvəlki mənfi hökm köhnəlib.** Main artıq EntryLanding-də
  üç illüstrasiyalı giriş kartını və şəkilli FoodResultCard nəticələrini daşıyır
  (`e69f263`, `aa658fc`). Kamera yolu məhdudiyyətini açıq deyir və axtarışa qaytarır.
  `93fe1cb` lokalizə olunmuş layout matrisinin kataloq gözləməsini düzəldib. Bu
  tapşırıqda yeni tam vizual qəbul verilməyib; sonrakı dəyişiklik yeni gate-dən
  keçməlidir. Əvvəlki “birbaşa axtarış forması açılır” hökmü cari koda aid deyil.

- **D07 · Bu gün — əvvəlki mənfi hökm köhnəlib.** Main `93fe1cb` artıq illüstrasiyalı
  TodayDashboard/TodayMeals və kliklənən plan/qeyd kartları daşıyır. Sübut və qəbul
  əhatəsi: `android-today-design-acceptance.md`. Ümumi illüstrasiya real yemək fotosu
  deyil; qalan enerji gündəliyin tamlığı olmadan göstərilmir. Bunlar sənədləşdirilmiş
  məhsul məhdudiyyətləridir. Bu gate tapşırığında cari renderlərə yeni vizual qəbul
  verilməyib; əvvəlki auditin mətnəsaslı ekran hökmü artıq cari main-ə aid deyil.
- **D08 · Başlanğıc/onboarding — əvvəlki renderlərdə əsasən uyğun.** 00/01/02 və
  12–14 əlavələri ilə uyğunlaşdırma `android-ui-png-parity.txt`-dədir. Əvvəlki
  nümayəndə renderlərə baxılıb; bütün cari vəziyyətlər üçün yeni qəbul iddiası yoxdur.
  Növbəti dəyişiklik gate-dən keçməlidir; köhnə PNG cari sübut deyil.

## Hazır olmayan əhatə — hazır UI-nin dizayn fərqi ilə qarışdırılmır

İrəliləyişin tam trend/çəki tarixçəsi UI-si, kamera/foto axınları, alış-veriş UI-si,
resept fərdiləşdirmə/dəyişmə ekranları, hesab/sinxronizasiya, xatırlatmalar və abunəlik
bütöv qəbul edilməyib. Domen nüvəsi və boş vəziyyət ekranı tam funksional ekranın
hazır olduğunu göstərmir. Öz mərhələlərində uyğun istinad və yeni render tələb olunur.

## Bəndi bağlama şərti

Düzəliş commitini, istinad ekranını, yoxlanmış tələbləri və dəqiq main commitinə bağlı
`.local-ci/visual/<head>/review.json` sübutunu qeyd et. Bütün tələb olunan renderlərə
baxılmadan və açıq fərqlər həll edilmədən “bağlı” yazma. İstifadəçi təsdiqli quruluş
istisnası varsa konkret təsdiqə istinad et. Ümumi “CI yaşıl” bu siyahını bağlamır.
