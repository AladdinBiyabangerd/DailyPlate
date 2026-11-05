# Current task — K08 preview preservation acceptance

## Completed
- Prior evidence commit c68c96d is merged in main 844d3a2; its post-merge CI passed
  145 JVM tests, lint/compile/tokens, library contracts and canonical exports.
- Inspected final 1.5x AZ target, RU activity, AZ health and EN bounds PNGs under
  .local-ci/reports/nutrition-integration; visible text wraps inside cards.
- Added blocked/cancelled profile-preview Room regression: no revision writes,
  unchanged literal legacy payloads and FoodLog. See QA appendix for exact scope.

## Current state
- Current acceptance branch must be committed and finished; its post-merge CI
  result is authoritative in .local-ci/logs/latest.json (match main HEAD).
- No APK, device checks, remote or production activation.

## Decisions
- Preview/cancel writes nothing. Explicitly confirmed failed screening may retain
  diagnostics with no usable target; this is separate from successful planning.
- §30 user-reported review is retained; unknown reviewer/date remain null.
- No automatic titration, retrospective recomputation or synthetic menu success.

## Remaining work
- NP-16: assess voluntary-measurement storage and implement the three-measurement
  /14-day consumer; separately verify symptom-triggered suspension semantics.
- Device/self-report pilot, optional notifications and K15 real catalog/portion/
  nutrient feasibility remain open; green local CI does not verify these.

## Relevant files
- docs/qa/android-k08-v2-implementation.md
- docs/nutrition-plan-design.md §26–30
- planning/research-data.json and planning/nutrition-science-implementation-plan.txt
- android/app/src/test/java/com/aladdin/dailyplate/data/local/CurrentNutritionPersistenceTest.kt
- android/app/src/main/java/com/aladdin/dailyplate/domain/profile/ProfileEdit.kt
- android/app/src/main/java/com/aladdin/dailyplate/data/local/ProfileEditRepository.kt

## Checks performed
- Prior main 844d3a2 green CI verified from log; final large-font images inspected.
- Current unit: git diff --check; execution only through post-merge finish.

## Exact next step
- Finish current acceptance branch and verify merged-main CI. Then scope NP-16
  voluntary-weight trend acceptance from canonical requirements, preserving
  frozen history and avoiding invented clinical thresholds. APK creation forbidden.
