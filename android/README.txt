Qida Android — K01

Minimum Android 7.0 / API 24; compileSdk 36; target Android 16 / API 36.
Paket: az.qida.app (hazırda yerli inkişaf identifikatoru; mağazada rezerv edilməyib).

Tələblər: JDK 21, Android SDK Platform 36, Build Tools 35.0.0 və 36.0.0,
platform-tools, test üçün açılmış API 24+ emulator və ya cihaz.
ANDROID_HOME SDK qovluğuna, JAVA_HOME JDK 21 qovluğuna yönəlməlidir.
Heç bir local.properties və ya sirr Git-ə daxil edilmir.

APK yaratmayan məcburi Android yoxlaması (repo kökündən):
  python3 android/tools/check_android.py
Bu əmr --offline ilə lintDebug, compileDebugKotlin, testDebugUnitTest və
palitra pariteti/kontrast yoxlamalarını işlədir. Emulator tələb etmir.
.local-ci/checks.py vasitəsilə məcburi branch/main CI-yə qoşulub.
.local-ci/ Git xaricindədir; başqa kompüterdə AGENTS.md-ə görə bərpa edilməlidir.

İlk dependency endirməsi üçün APK yaratmayan əmr (android/ daxilində):
  ./gradlew --no-daemon :app:lintDebug :app:compileDebugKotlin :app:testDebugUnitTest
Gradle və Google/Maven Central dependency-lərini endirə bilər.
Versiyalar build.gradle.kts-dədir. Wrapper standart Gradle wrapper-dır.

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
Skelet tam onboarding və ya menyu funksiyası deyil. K02 növbəti kod hissəsidir.
Build/debug imzası yalnız inkişaf üçündür. Remote, mağaza və deployment yoxdur.
