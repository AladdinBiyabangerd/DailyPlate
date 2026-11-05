# Current task — K13

## Completed
- K13 pure deterministic recipe candidate filter and typed rule results.
- K03 exact ingredient versions, K05 publication completeness and K08 eligibility
  reused; production gate stays closed. Internal fixtures remain test-only.
- Allergy/cross contact/unknown compound or mapping, dietary restrictions,
  region/equipment, dislikes, total time and recipe portion grid are hard checks.

## Current state
- K13 worktree /private/tmp/callmood-k13; canonical plan 2.0 merged from main.
  Preserve its canonical JSON and generated MD/TXT/PDF/canvas.
- See matching-head .local-ci/logs/latest.json for final main CI result.

## Decisions
- No UI or APK. No fake policy/publication activation.
- Missing equipment is unknown when required; onboarding is not expanded.
- SUCCESS is candidate-stage only; K15 owns day/week/nutrient validation.
- Plan 2.0 additions C-05/C-08/C-09, D-02/D-05 and A-05 apply.
  Separate plan task commits remain intact in main history.

## Remaining work
- K14 pricing next; K15 generator follows. Production G2/G3 remain blocked.
- Real catalog, device and clinical acceptance remain unverified.

## Relevant files
- domain/planning/{CandidateContracts,RecipeCandidateFilter}.kt
- domain/planning/RecipeCandidateFilterTest.kt; docs/qa/android-k13-acceptance.txt
- planning/coding-tasks.txt; planning/nutrition-science-implementation-plan.txt

## Checks performed
- See final matching-main CI log for Kotlin compilation/lint, JVM tests, library
  contracts and canonical export checks. No pre-merge CI or APK checks.

## Exact next step
- Verify matching-main CI, then read K14 and its referenced plan before coding.

## Latest nutrition-plan status
- Research proposal 479f7ee merged into main as b775fb2; CI skipped at user request.
- User subsequently reported specialist approval of the entire written plan and
  requested no reviewer name. Plan 2.2-reported-review records USER_ATTESTATION,
  scope/version/hash; qualification and review date remain unknown.
- No fake identity/date/document or independent verification claimed. No Android
  execution flag enabled; implementation/content/device acceptance remains separate.
- Canonical research-data.json and generated MD/TXT/canvas/candidate JSON/PDF updated.
- Exact next step: implement the scoped K08-v2 contract in a separate task using
  plan §26–30; retain snapshots and verify actual implementation before activation.
