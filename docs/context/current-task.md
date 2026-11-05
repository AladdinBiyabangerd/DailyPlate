# Current task — current nutrition onboarding/profile integration

## Completed
- 437b803 connects onboarding review/confirm and profile preview/confirm/save to
  NutritionPolicyEngine.assessCurrent; 37da6f3 fixes AZ resource parity; 4186980
  corrects source/estimated-target labels and adds 14-day in-app review reminder.
- Main 38fd034 passed post-merge APK-free CI: 145 JVM tests, lint, compilation,
  tokens/contrast, 15 library contracts, Python/JSON and canonical export parity.
- Current modules/tests use Nutrition names. No null-policy calls in onboarding,
  profile preview or repository validation. Four activity categories plus unsure,
  explicit ten health answers, confirmed measurements and fresh age are required.
- Literal legacy profile/Mifflin assessment payloads survive confirmation/reopen
  byte-identically; FoodLog/old plans/history remain unchanged. Only new confirmed
  profile/assessment revisions are written. Diary chooses date-effective revisions.

## Current state
- Integration is merged locally; final evidence commit updates QA, requires the
  new CI suites, hides activity-model implementation text in profile differences,
  and aligns screenshot Configuration.fontScale with 1.5x rendering density.
- That commit must complete its own workflow finish and merged-main CI before
  reporting completion. Check .local-ci/logs/latest.json for the actual final HEAD.
- No APK/device tests or remote actions authorized or performed.

## Decisions
- One current direct NASEM EER + max/remainder macro path. Mifflin is historical
  compatibility/testing only. Failed screening has no usable planning target.
- §30 user-reported specialist review is recorded with its canonical hash;
  unknown reviewer/date stay null. No catalog/menu production gates are opened.
- Explicit activity-model reconfirmation includes the overlapping legacy active
  label. Sex changes preserve risks. New measurements invalidate confirmation.
- Future revisions require preview/reason/confirm; no retrospective recomputation.
- A successful target is not a fully balanced or feasible real menu.

## Remaining work
- No further feature should be stacked in this chat after final CI/visual QA.
- Separate future scope: actual 3-voluntary-measurement trend consumer, optional
  notification scheduling, self-report pilot/device acceptance and full K15
  catalog/portion/nutrient feasibility. No automatic calorie titration.

## Relevant files
- docs/qa/android-k08-v2-implementation.md (current integration appendix)
- docs/nutrition-plan-design.md §26–30; planning/research-data.json (canonical)
- planning/nutrition-science-implementation-plan.txt
- android/app/src/main/java/com/aladdin/dailyplate/domain/policy/
  CurrentNutritionAssessment.kt, NutritionEnergy*, NutritionPlanning*, DiaryTarget.kt
- domain/onboarding/OnboardingDraft.kt, OnboardingPreview.kt; domain/profile/ProfileEdit.kt
- data/local/OnboardingStore.kt, ProfileEditRepository.kt; ui/onboarding, ui/profile
- android/app/src/test/resources/nutrition/legacy-*.json and CurrentNutrition*Test.kt

## Checks performed
- Main 38fd034: all 145 JVM tests passed, no failures/errors/skips; lint,
  compilation, token/contrast (minimum 4.71:1), library and export checks passed.
- Inspected AZ/EN/RU current target/bounds, activity and health images plus AZ
  1.5x onboarding image. Final screenshot configuration correction needs final
  post-merge render inspection; .local-ci/reports/nutrition-integration holds it.
- APK/device/clinical/pilot/full-menu behavior unverified; no APK created.

## Exact next step
- Finish the evidence branch, confirm green merged-main CI and inspect final
  large-font renders. Then report commits/checks and use a new chat for any
  additional product work, reading this handoff and the QA appendix first.
