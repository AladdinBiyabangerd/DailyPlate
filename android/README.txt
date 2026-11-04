Qida Android — K01/K02

Minimum Android 7.0 / API 24; compileSdk 36; target Android 16 / API 36.
Paket: az.qida.app (hazırda yerli inkişaf identifikatoru; mağazada rezerv edilməyib).

Tələblər: JDK 21, Android SDK Platform 36, Build Tools 35.0.0 və 36.0.0,
Cihaz testi ayrıca istənərsə platform-tools və açıq API 24+ emulator/cihaz.
ANDROID_HOME SDK qovluğuna, JAVA_HOME JDK 21 qovluğuna yönəlməlidir.
Heç bir local.properties və ya sirr Git-ə daxil edilmir.

APK yaratmayan məcburi Android yoxlaması (repo kökündən):
  python3 android/tools/check_android.py
Bu əmr --offline ilə lintDebug, compileDebugKotlin, testDebugUnitTest və
palitra pariteti/kontrast yoxlamalarını işlədir. K02 domen/Room JVM testlərinin
həqiqətən icrasını və buraxılmış test olmadığını da yoxlayır. Emulator tələb etmir.
.local-ci/checks.py vasitəsilə məcburi branch/main CI-yə qoşulub.
.local-ci/ Git xaricindədir; başqa kompüterdə AGENTS.md-ə görə bərpa edilməlidir.

İlk dependency endirməsi üçün APK yaratmayan əmr (android/ daxilində):
  ./gradlew --no-daemon :app:lintDebug :app:compileDebugKotlin :app:testDebugUnitTest
Gradle və Google/Maven Central dependency-lərini endirə bilər.
Versiyalar build.gradle.kts-dədir. Wrapper standart Gradle wrapper-dır.
K02: Kotlin/Compose compiler/serialization plugin 2.4.10, KSP 2.3.13,
Room 2.8.5, serialization 1.12.0, Robolectric 4.17; Java 17 bytecode/JDK 21.
java.time API 24/25 üçün desugar_jdk_libs 2.1.5 ilə dəstəklənir.
JVM testləri API 34 Android fixturundan istifadə edir. Fixtur əvvəl Gradle ilə
hazırlanır; Robolectric runtime-da şəbəkə ilə asılılıq endirmir. Eyni fixtur
lokal Maven keşində varsa yalnız həmin modul üçün oradan oxunur, yoxdursa
rəsmi Google/Maven Central mənbələri işlənir. CI-dən əvvəl ilk əmri oflaynsız işlət.
JVM hesabatları: app/build/test-results/testDebugUnitTest/.
Room sxemləri: app/schemas/; testlər v1 → v2 keçidini də yoxlayır.
Domen/DB qərarları: docs/architecture/android-k02-storage.txt (repo kökündən).
Şəxsi qida qeydləri qida kitabxanasından ayrı qida-user.db-də saxlanır;
kitabxananın qida-library.db oxu repository-si şəxsi yazı API-si vermir.
Repository-ləri worker thread-dən çağır və sahib lifecycle sonunda close et.
Minimal giriş UI-si hələ DB-ləri açmır; ilk istifadə edən funksiya bunu qoşacaq.

Yalnız istifadəçi ayrıca APK istəyəndə (android/ daxilində):
  ./gradlew --no-daemon :app:assembleDebug
APK: android/app/build/outputs/apk/debug/app-debug.apk
Cihaz smoke testi də app/test APK yaratdığı üçün ayrıca sorğu tələb edir:
  ./gradlew --no-daemon :app:connectedDebugAndroidTest
Test nəticəsi: android/app/build/reports/androidTests/connected/debug/index.html
Lint: android/app/build/reports/lint-results-debug.html

CI APK manifestini və cihazda açılışı yoxlamır. Əvvəlki K01 cihaz və vizual
qəbul sübutları docs/qa/android-k01-acceptance.txt-də tarixli olaraq saxlanır.
Smoke: tətbiqi aç → Qida/Xoş gəldin/mətn görünür → Activity recreate → yenə görünür.
K02 domen və DB qatıdır; onboarding, menyu və kitabxana importu hələ UI-yə qoşulmayıb.
Növbəti kod hissəsi K03-dür.
Build/debug imzası yalnız inkişaf üçündür. Remote, mağaza və deployment yoxdur.
