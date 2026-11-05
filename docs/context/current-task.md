# Current task — K06/K07 UI alignment with supplied PNGs

## Completed
- K07 was merged to main df744ad with green main CI before this UI task.
- Replaced plain K06/K07 presentation with artwork extracted from original
  00/01/02/06 references, native illustrated cards, progress bars, compact
  measurement controls, grouped preference segments, language dropdown and icon
  navigation. Goal cards align in height; primary action sits at bottom if it fits.
- Added same-style PNG boards 12 (supplemental profile), 13 (empty/boundary),
  14 (supplemental forms/loading/error), generated with built-in image_gen.
- All implemented sections covered, including custom allergens/dislikes,
  health/budget/home foods, confirmed summary, unavailable Account and states.
- Domain, storage and validation code unchanged; no fake menu/calorie/auth data.

## Current state
- Branch feat/png-ui-parity; local development lint, compilation and all 51 JVM
  tests pass. Branch/main lifecycle evidence: .local-ci/logs/latest.json and Git
  history after finish. Do not infer merge success solely from this document.
- Native normal/150% AZ/EN/RU renders available under android/app/build/reports/
  k06, k07 and ui-parity. Representative final images actually inspected.

## Decisions
- Existing CSS colour/spacing/card tokens remain authoritative; new native
  illustration/component metrics are named in QidaDimensions, not inline values.
- Native controls remain interactive/accessible; entire PNGs are not UI screens.
- >130% font uses one-column grids/segments; overflow scrolls. Supplementary
  questions remain inside the existing five-step sequence. Full plan labels and
  optional/custom-answer paths take precedence over sketch sample text.
- Mockup 13 repeats Today selection; real UI selects the actual route.
- No pixel-diff identity is claimed across native font/platform/translation.

## Remaining work
- User/device visual acceptance and physical TalkBack remain unverified.
- K08 policy/eligibility is still the next numbered product milestone; K09 profile
  editing, later planning/authentication remain outside this visual task.
- No APK/AAB, installation, instrumentation or deployment was requested/run.

## Relevant files
- design-concepts/simple-turquoise/{00–14 PNGs,implementation-guide.txt,
  ui-extension-guide.txt,ui-extension-prompts.txt}; design-concepts/tokens.css
- planning/research-data.json canonical; nutrition-science-implementation-plan.txt
  §18–19; planning/coding-tasks.txt K06/K07/K08
- android/app/src/main/java/com/aladdin/dailyplate/ui/{BootstrapScreen,
  DesignComponents,FoundationComponents,onboarding/OnboardingScreen,theme/QidaTheme}.kt
- android/app/src/main/res/drawable{-nodpi,}/; values{,-az,-en,-ru}/onboarding.xml
- android/tools/extract_design_art.py (manual macOS sips extraction/provenance)
- docs/qa/android-ui-png-parity.txt; ui/{Foundation,NavigationUi,OnboardingUi}Test.kt

## Checks performed
- APK-free lintDebug + compileDebugKotlin + 51 JVM tests passed locally.
- Contrast checker: white/primary 4.71, muted/bg 5.04, text/bg 11.95; full token
  validator passed. Complete resource-key parity in AZ/EN/RU.
- Final images inspected: normal welcome/goals/measurements/preferences,
  RU 150% measurements/dislikes/preference footer, EN extra-allergen/Profile,
  main Menu/Today, saved summary, unavailable Account, loading/write-error cards.
- Fixed screenshot-discovered crop scaling, unequal goal cards, long +/- labels,
  and unwanted illustration fragments. No checks weakened; no APK created.
- Local Kotlin incremental cache fell back to full compilation; build then passed.

## Exact next step
Verify final branch/merged-main CI in local logs and history. For any further UI
revision, start a new chat with the specific screenshot and PNG reference. For
K08, read its plan requirements before changing domain/policy behavior.
