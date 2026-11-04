# Current task — K07 onboarding/profile draft

## Completed
- K06 was on main f5ce580 with green local CI before this task.
- K07 five-step AZ/EN/RU onboarding: four goals; age/as-of, optional equation
  sex, measurements, described activity codes and optional health answers;
  allergies, custom allergens, separate gluten restriction, editable dislikes;
  15/30/60 minutes, 3/4/5 visible slots, budget/region/currency/coverage,
  equipment and multiple home foods with quantity/unit/preparation state.
- Decimal commas and finite positive numeric validation. No sketch defaults.
- Ordered AtomicFile + explicit fsync draft writes, durable reload and Back;
  separate Room profile version 1, idempotent confirmation and saved summary.
- Profile section links to onboarding/saved profile. Confirmed profiles are
  read-only here; K09 owns subsequent edits.

## Current state
- K07 implemented. Snapshot lint/compilation and all 48 JVM tests passed,
  including budget/home-food contracts; final lifecycle evidence is below.
- Branch: feat/k07-onboarding. Final commit/main CI evidence must be read from
  .local-ci/logs/latest.json and Git history after finish completes.
- No menu, energy target, eligibility decision or fake plan is created.

## Decisions
- eligibilityStatus remains not_evaluated; K08 owns policy decisions and G2.
- Unknown allergy = null; explicit none = empty set. Wheat and gluten separate.
- Custom allergens use custom: names; unmatched disliked/home-food names use
  user: references. Later library matching/planning must fail closed, never
  treat these as validated published catalogue IDs.
- Optional missing sex/health answers never get inferred. No activity
  multipliers or clinical thresholds were invented.
- Guest storage is local only; no account/sync/recovery promise.
- Existing turquoise tokens, 48dp choices, labelled decimal inputs; no new palette.

## Remaining work
- K08: eligibility and versioned NutritionPolicy, per coding-tasks.txt.
- K09 owns profile edits; K13+ planning must consume the stored constraints,
  including unmatched free text safely. Content and G2/G3 approval are external.
- Physical-device/TalkBack speech, abrupt OS process kill, disk-full injection,
  catalogue matching, policy/menu/pricing/auth are not verified by K07.
- No APK/AAB, device installation or instrumentation was requested or run.

## Relevant files
- planning/coding-tasks.txt K07/K08; planning/research-data.json canonical
- planning/nutrition-science-implementation-plan.txt §5–6, §11, §14, §17–19
- android/app/src/main/java/com/aladdin/dailyplate/domain/{Profile.kt,onboarding/OnboardingDraft.kt}
- android/app/src/main/java/com/aladdin/dailyplate/data/local/OnboardingStore.kt
- android/app/src/main/java/com/aladdin/dailyplate/ui/{BootstrapScreen.kt,onboarding/OnboardingScreen.kt}
- android/app/src/main/res/values{,-az,-en,-ru}/onboarding.xml
- android/app/src/test/java/com/aladdin/dailyplate/{domain/OnboardingDraftTest.kt,ui/OnboardingUiTest.kt}
- android/tools/{check_android,verify_tokens}.py
- docs/architecture/android-k07-onboarding.txt; docs/qa/android-k07-acceptance.txt

## Checks performed
- Lint, Kotlin compilation, all 48 JVM tests and 15 library tests passed;
  Python/JSON and canonical export parity passed. Final branch/main lifecycle
  status is recorded in .local-ci/logs/latest.json and Git history.
- Native Robolectric top/bottom images of all five steps in AZ/EN/RU at 150%
  text inspected; final measurement spacing and RU long-label/confirmation
  images inspected. Images: android/app/build/reports/k07/.
- Token/resource parity (98 strings each locale); measured contrast 4.71–12.70.
  ui-craft contrast checker passed core text/background pairs.
- Tests cover comma/invalid numbers, allergy semantics, durable reload, Back,
  recreation, full confirmation, same-version retry and Profile access.
- During development: fixed asynchronous test waits, KTX commit-result issue by
  using atomic file storage; rebuilt corrupt local KSP cache. Checks not weakened.

## Exact next step
After final K07 branch and merged-main CI are green, start K08 in a new chat: read this context, K08 requirements and plan §5–6/§11/§14/§17–18. Keep the
mandatory local workflow and do not create APK/AAB without an explicit request.
