# Current task — K06 visual foundation/navigation/languages

## Completed
- K05 was merged on main 0b1cfd8 with green local CI before this task.
- K06 token-based Compose foundation: welcome (screen 01), five main sections,
  empty states, explicit future onboarding/account boundaries, shared button,
  labelled field, card, heading and loading/empty/error components.
- AZ/EN/RU resources; persistent language preference; stable route IDs and saved
  back stack; visible/system Back; selection semantics and non-colour checkmark.
- CSS/Android palette parity and expanded >=4.5 text contrast checks.
- APK-free native Robolectric welcome renders at 150% font in all three languages;
  navigation UI tests cover five sections, 48dp targets, EN/RU, Back and recreation.

## Current state
- Only K06 implemented. No fake profile, calorie, recipe, weight or plan data.
- K07 onboarding and later authentication remain clearly explained boundaries.
- Final commit/main CI evidence: consult .local-ci/logs/latest.json and Git history
  for feat/k06-visual-foundation, after the lifecycle completes for this handoff.

## Decisions
- Simple turquoise light palette; no new dark palette or sampled raster colours.
- Navigation stores enum IDs in saved instance state, not localized labels.
- Language changes only presentation preferences/configuration, no domain writes.
- Unit tests use plain Application to isolate Firebase; production app unchanged.
- JVM render/test dependencies are local/offline; no APK/AAB or device workflow.

## Remaining work
- K07 is the next numbered unit: onboarding/profile draft; read its requirements.
- Physical-device/TalkBack speech, process-death and instrumentation acceptance
  remain unverified. Native JVM images/semantics are not device certification.
- Real G2/G3 approval and production content authorization remain external work.

## Relevant files
- planning/coding-tasks.txt K06/K07; planning/research-data.json canonical
- planning/nutrition-science-implementation-plan.txt §18–19
- design-concepts/tokens.css; design-concepts/simple-turquoise/*guide.txt, 00/01 images
- android/app/src/main/java/com/aladdin/dailyplate/ui/{BootstrapScreen,FoundationComponents,NavigationState}.kt
- android/app/src/main/java/com/aladdin/dailyplate/ui/theme/QidaTheme.kt
- android/app/src/main/res/values{,-az,-en,-ru}/strings.xml; values/colors.xml
- android/app/src/test/java/com/aladdin/dailyplate/ui/; src/test/resources/robolectric.properties
- android/tools/{check_android,verify_tokens}.py
- docs/architecture/android-k06-foundation.txt; docs/qa/android-k06-acceptance.txt

## Checks performed
- Contrast: text/bg 11.95; muted/bg 5.04; white/primary and primary/white 4.71;
  text/surface 12.70; muted/surface 5.36. ui-craft checker also passed core pairs.
- AZ/EN/RU 150% welcome images and final RU profile image inspected. Selected
  chip palette corrected to primary; narrow navigation word breaks fixed and
  visually rechecked using natural-width wrapping buttons.
- Final development check: lint, Kotlin compilation and all 41 JVM tests passed.
  Final lifecycle evidence: local CI logs; images: android/app/build/reports/k06/.
- Development checks exposed and fixed resource-enabled test Firebase isolation,
  unused resources and KTX lint. No checks disabled or weakened.
- No APK/AAB, installation, device/instrumentation or human content approval.

## Exact next step
After K06 branch and merged-main CI are green, continue K07 in a new chat. Read
this context, K07 and plan §5–6/§17–19, then apply ui-craft and the mandatory local
workflow. Do not create APK/AAB unless explicitly requested.
