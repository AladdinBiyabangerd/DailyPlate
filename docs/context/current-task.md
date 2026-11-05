# Current task — K08 eligibility and NutritionPolicy

## Completed
- Implemented typed policy execution v1, pure eligibility/REE/activity/goal/macro core,
  per-nutrient source-row selection and structured reason codes.
- General/specialist paths stay available. Production activation stays closed at G2;
  test calculation entry is internal, snapshots are explicitly testOnly.
- Onboarding now records screening status and atomically saves profile plus assessment.
- Added append-only local assessment history, compatibility and acceptance regressions.
- Updated stale coding queue: K01–K07 implemented; K08 acceptance tracked separately;
  next unit is K09. UI/design files are unchanged.

## Current state
- Task branch: feat/k08-nutrition-policy. Final merge commit and CI execution evidence:
  git log and .local-ci/logs/latest.json (must match current main HEAD and passed=true).
- User's unrelated AGENTS.md edit was stashed separately during workflow and must
  be restored after finish; do not commit or discard it.

## Decisions
- Numeric clinical choices exist only in synthetic src/test fixtures; no defaults invented.
- Integer age/as-of cannot identify a birthday: policy supplies refresh interval,
  stale/future/missing confirmation blocks automatic calculation; no silent age increment.
- Legacy PolicyRule strings are editorial descriptions. New typed execution is required.
- Screening eligible does not mean approved policy/clinical eligibility or active plan.
- No new UI; K09 owns profile edit, explanation and specialist/age-confirmation UI.

## Remaining work
- G2 real clinical policy/authority and trusted production activation provider.
- K09; actual menu recalculation later in K16. Device/APK/TalkBack unverified.

## Relevant files
- android/app/src/main/java/com/aladdin/dailyplate/domain/policy/*.kt
- domain/NutritionPolicy.kt; domain/onboarding/OnboardingDraft.kt
- data/local/{OnboardingStore,UserRepositories}.kt
- src/test/.../domain/policy/ and data/local/NutritionAssessmentPersistenceTest.kt
- docs/qa/android-k08-acceptance.txt; planning/coding-tasks.txt
- planning/research-data.json (canonical); nutrition-science-implementation-plan.txt §5–6/11/14/17–18

## Checks performed
- git diff --check passed before commit.
- Mandatory merged-main CI is run by workflow.py finish; consult its commit-keyed
  result above for actual lint/compilation/JVM/token/Python/library/export evidence.
- No branch CI, APK creation, device test or new visual render is authorized/run.

## Exact next step
- Confirm final main CI record, then start K09 in a new chat using coding-tasks.txt's
  continuation prompt. Read this handoff and scoped K08 contracts first; do not rescan repo.
