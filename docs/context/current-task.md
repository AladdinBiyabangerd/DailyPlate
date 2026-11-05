# Current task — K13

## Completed
- K13 pure deterministic recipe candidate filter and typed rule results.
- K03 exact ingredient versions, K05 publication completeness and K08 eligibility
  reused; production gate stays closed. Internal fixtures remain test-only.
- Allergy/cross contact/unknown compound or mapping, dietary restrictions,
  region/equipment, dislikes, total time and recipe portion grid are hard checks.

## Current state
- K13 worktree /private/tmp/callmood-k13; primary checkout has unrelated dirty
  nutrition-plan work. Do not commit or switch that work.
- See matching-head .local-ci/logs/latest.json for final main CI result.

## Decisions
- No UI or APK. No fake policy/publication activation.
- Missing equipment is unknown when required; onboarding is not expanded.
- SUCCESS is candidate-stage only; K15 owns day/week/nutrient validation.
- Latest additional requirements read in primary working plan; its pending plan
  changes are not copied or committed as part of K13.

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
