# Current task — K16 explicit initial plan acceptance

## Completed
- Durable K15 generation envelope and stale-input transaction checks retained.
- Added explicit owner/request → new MealPlan acceptance using existing repositories/aggregate.
- Acceptance atomically persists initial plan + shopping + receipt, checks current profile/input bundle, rejects existing plan IDs and failed/fixture production results.
- Retry replays initial revision; no FoodLog or changes to existing plan history.

## Current state
- K16 remains partial. Generation and initial acceptance persistence boundaries exist; no UI route yet.
- Production catalog/G3, price/G4 and budget scope remain gated.

## Decisions
- Initial acceptance requires a new plan ID; recalculation is a separate preview/change operation.
- One planned serving is the generated personal portion. Complete provenance and alternative portions remain in linked generation envelope.
- Replay returns original accepted revision even after inputs or plan change.

## Remaining work
- Authoritative catalog/import activation wiring.
- Future-plan recalculation preview/acceptance preserving consumed/history items.
- Screens 04/13/14, dated menu, recipe/portion confirmation and Today E2E.

## Relevant files
- data/local/{PlanAcceptanceRepository,PlanGenerationRepository,MealPlanRepository,UserRepositories}.kt
- data/local/PlanGenerationTest.kt; domain/planning/WeeklyPlanContracts.kt
- docs/qa/android-k16-generation-acceptance.md
- planning/coding-tasks.txt K16; planning/research-data.json §8/18–19/22

## Checks performed
- Prior main 3023709 CI passed; scoped requirements and organization reviewed.
- Current acceptance tests added; final merged-main CI result recorded in task response.
- No UI change/render, APK, device, clinical or real catalog/pricing acceptance claimed.

## Exact next step
- Continue K16 future-plan recalculation as a separate scoped unit, reading its profile-change contracts first.
- Follow start/commit/finish lifecycle; use ui-craft for UI work; no APK without explicit request.
