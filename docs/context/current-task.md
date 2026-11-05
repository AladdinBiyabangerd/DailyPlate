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

## Separate nutrition-policy research handoff (not integrated)
- This worktree/branch adds plan §26–29 and exact disabled policy proposal.
- Detailed Completed/State/Decisions/Remaining/Files/Checks/Next step:
  docs/context/nutrition-policy-research.md.
- User requires a separate branch; no main merge, no CI, no app code activation.
  Continue K14 in its own worktree using main's plan until integration is requested.
