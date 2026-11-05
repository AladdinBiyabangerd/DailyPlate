# Current task — connect planned nutrition calculation to the application

## Completed
- K08 slices 1–3 merged into main: c6f8342 and f7b1755.
- f7b1755 post-merge CI passed: 137 JVM tests, lint, compilation, tokens,
  15 library tests, Python/JSON and canonical export checks. No APK/device tests.
- Pure NASEM energy screening, max/remainder macros, typed menu intervals and
  partial nutrient checks exist internally; no onboarding/profile integration yet.
- Read integration call sites and design §30; this handoff adds no application code.

## Current state
- Workspace /Users/mac/My Workspace/callmood.
- Latest user explicitly requests actual onboarding/profile integration according
  to the plan, one current calculation path, and removal of confusing v2 naming.
- Previous no-merge/CI instruction concerned isolated slice work; slices were
  subsequently merged at user request. Normal local lifecycle applies to new work.
- Do not generate APKs: none requested.

## Decisions
- Implement the plan in the real app, not another disconnected research-only API.
- Rename new runtime modules to clear nutrition-domain names without V2/Research
  branding. Persisted schema/version metadata may remain technical compatibility
  details; do not destroy or recompute old profiles/calculations/FoodLog/history.
- Existing Mifflin snapshot compatibility is a historical decoder concern, not an
  alternative current onboarding/profile algorithm or user-facing choice.
- Plan §30 reports SPECIALIST_APPROVAL_REPORTED/USER_ATTESTATION; do not treat
  finding a specialist as an open blocker or fabricate reviewer/date evidence.
- Activation still requires concrete implementation/migration/runtime checks;
  do not enable content/menu gates by pretending unpublished data passed.
- Retain four-choice activity reconfirmation (including overlapping active code),
  all explicit risk answers, age freshness, no-clamp failures, future-only confirmed
  profile revisions and no retrospective history recomputation.
- Macro/energy success is not a full balanced menu; unknown nutrient/catalog
  coverage must stay explicit. No new clinical thresholds or calorie titration.

## Remaining work
- Read QA slices 4–5 and applicable design §18–19, §26–30, including NP-06/16/27/28.
- Connect onboarding and profile preview/confirm/save to one current engine;
  replace null-policy call sites and repository validation together.
- Add current activity/health inputs and backward-compatible persisted decoding;
  make reconfirmation explicit instead of mapping legacy activity labels silently.
- Verify persisted legacy fixtures, future-only revisions, failure/no-snapshot
  paths and downstream K13/K12 behavior; rename internal modules and tests.
- Use ui-craft for changed UI, existing tokens and rendered visual inspection.

## Relevant files
- docs/qa/android-k08-v2-implementation.md
- docs/nutrition-plan-design.md; planning/nutrition-science-implementation-plan.txt
- planning/research-data.json (canonical); planning/nutrition-policy-candidate.json
- android/app/src/main/java/com/aladdin/dailyplate/domain/policy/
  NutritionPolicyEngine.kt, NutritionAssessment.kt, Research*V2.kt, EnergyFormula.kt
- android/app/src/main/java/com/aladdin/dailyplate/domain/onboarding/OnboardingDraft.kt
- android/app/src/main/java/com/aladdin/dailyplate/domain/profile/ProfileEdit.kt
- android/app/src/main/java/com/aladdin/dailyplate/data/local/
  OnboardingStore.kt, ProfileEditRepository.kt, UserSnapshotRepository.kt, SnapshotJson.kt
- android/app/src/main/java/com/aladdin/dailyplate/ui/onboarding/OnboardingScreen.kt
- android/app/src/main/java/com/aladdin/dailyplate/ui/profile/ProfileEditor.kt
- android/app/src/main/java/com/aladdin/dailyplate/ui/theme/QidaTheme.kt
- design-concepts/tokens.css; design-concepts/simple-turquoise/ guides

## Checks performed
- Confirmed null policy at OnboardingStore.kt:49, ProfileEdit.kt:77 and
  ProfileEditRepository.kt:15. snapshotJson encodes defaults/nulls, rejects unknowns.
- Prior integrated slices: CI PASS f7b1755, 137 JVM tests. Handoff has no app changes.
- Real onboarding/profile NASEM integration, migration and visual acceptance unverified.

## Exact next step
- On a suitable branch started through .local-ci/workflow.py, read scoped QA/plan
  requirements, then inspect profile/assessment payloads and onboarding/profile
  confirmation transactions before implementing the single current calculation path.
