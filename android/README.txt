Qida Android — K01

Minimum Android 7.0 / API 24; compileSdk 36; target Android 16 / API 36.
Paket: az.qida.app (hazırda yerli inkişaf identifikatoru; mağazada rezerv edilməyib).

Tələblər: JDK 21, Android SDK Platform 36, Build Tools 35.0.0 və 36.0.0,
platform-tools, test üçün açılmış API 24+ emulator və ya cihaz.
ANDROID_HOME SDK qovluğuna, JAVA_HOME JDK 21 qovluğuna yönəlməlidir.
Heç bir local.properties və ya sirr Git-ə daxil edilmir.

Təmiz checkout-dan ilk build (android/ daxilində):
  ./gradlew --no-daemon :app:assembleDebug :app:lintDebug :app:assembleDebugAndroidTest
Emulator açıq olanda test dependency-lərini də ilk dəfə endirmək üçün:
  ./gradlew --no-daemon :app:connectedDebugAndroidTest
İlk build Gradle və Google/Maven Central dependency-lərini endirə bilər.
Sonra eyni əmri --offline ilə işlətmək mümkündür. Versiyalar build.gradle.kts-dədir.
Wrapper standart Gradle wrapper-dır; mövcud yerli Arrowz nüsxəsindən götürülüb.

Emulator açıldıqdan sonra repo kökündən tam Android yoxlaması:
  python3 android/tools/check_android.py
Bu əmr palitra pariteti/kontrast, lint, build və connectedDebugAndroidTest işlədir.
Emulator yoxdursa yoxlama uğursuz olur, test səssiz buraxılmır.
Bu yoxlama .local-ci/checks.py vasitəsilə məcburi branch/main CI-yə də qoşulub.
.local-ci/ Git xaricindədir; başqa kompüterdə AGENTS.md-ə görə bərpa edilməlidir.

APK: android/app/build/outputs/apk/debug/app-debug.apk
Test nəticəsi: android/app/build/reports/androidTests/connected/debug/index.html
Lint: android/app/build/reports/lint-results-debug.html
Build/debug imzası yalnız inkişaf üçündür. Remote, mağaza və deployment yoxdur.

Smoke: tətbiqi aç → Qida/Xoş gəldin/mətn görünür → Activity recreate → yenə görünür.
Skelet tam onboarding və ya menyu funksiyası deyil. K02 növbəti kod hissəsidir.
