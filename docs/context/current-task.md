# Current task — K01 Android foundation

## Completed
- Android app skeleton added under android/, using Kotlin + Jetpack Compose.
- User SDK decision: minSdk 24 (Android 7.0), compileSdk/targetSdk 36 (Android 16).
- Token resources, light theme, minimal Azerbaijani entry surface, explicit backup exclusions and launch/recreation instrumentation test added.
- Tracked Android verification runner added and connected to ignored .local-ci/checks.py.
- ADR: docs/architecture/android-foundation.txt; setup: android/README.txt.

## Current state
- K01 implementation and initial acceptance checks are complete; K02–K34 not started. Verify the final branch/main CI record in .local-ci/logs/latest.json and Git history before proceeding.
- Updated Gradle 8.14.6 / Activity 1.13.0 / runner 1.7.0 / JUnit ext 1.3.0 build, lint and API 34 device smoke passed. Normal and 1.5x font screenshots were inspected.
- Canonical product plan remains research-data.json 1.7; library remains 500 foods / 224 unpublished drafts.

## Decisions
- Android first; iOS only after user likes Android and decides to proceed.
- Native Kotlin/Compose; Room selected for K02. No DB implemented yet.
- Remote backend/provider choice deferred to relevant integration units; no server or secrets added.
- Light palette uses existing tokens; primary-on-background text was replaced with text token after measured contrast 4.43 failed 4.5.
- No onboarding/navigation/domain logic implemented in K01; no fake unit tests.

## Remaining work
- Run/verify the mandatory commit and branch/main CI lifecycle; acceptance evidence is docs/qa/android-k01-acceptance.txt.
- API 24 and API 36 runtime compatibility remain unverified without matching emulator/device tests.
- Then start K02 in a new chat, reading its plan requirements first.

## Relevant files
- planning/coding-tasks.txt
- docs/architecture/android-foundation.txt
- android/README.txt
- android/app/build.gradle.kts
- android/app/src/main/java/az/qida/app/
- android/app/src/androidTest/java/az/qida/app/LaunchSmokeTest.kt
- android/tools/check_android.py and verify_tokens.py
- docs/qa/ (acceptance evidence)
- planning/research-data.json and nutrition-science-implementation-plan.txt
- .local-ci/README.md and checks.py (ignored, local-only)

## Checks performed
- Final API 34 emulator smoke: 1 test passed, no failures/errors/skips.
- Measured actual text/background pairs: 11.95 and 5.04; white/primary 4.71.
- SDK 36 installed; no API 24/36 system image present.
- Final dependency-updated assembleDebug/lintDebug/device smoke passed; lint failures fixed without bypasses. APK manifest confirms minSdk 24, targetSdk/compileSdk 36.
- Actual normal/1.5x font screenshots inspected; see docs/qa/android-k01-acceptance.txt.
- Branch/main CI results are recorded in Git and .local-ci/logs/latest.json; these remain the authority for lifecycle completion.

## Exact next step
Verify K01 is merged to main with green local CI. Then continue K02 in a new chat: read planning/coding-tasks.txt K02, plan §5/§11/§16–19 and customization contracts, implement domain models and Room migrations. Preserve minSdk 24 / compileSdk and targetSdk 36; do not start K03 in that chat.
