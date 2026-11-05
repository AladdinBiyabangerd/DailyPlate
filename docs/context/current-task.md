# Current task — K16 durable generation boundary

## Completed
- Earlier: confirmed consumption + atomic log/plan/event receipt and actual/remaining projection.
- Added durable K15 generation envelope: exact request, profile, policy/catalog/evidence/prices and full result.
- Current profile and active input bundle checked inside receipt write transaction; stale input cannot persist SUCCESS.
- Owner/request replay survives restart; changed request/mode conflicts. Production gates retained.

## Current state
- K16 remains partial; persistence boundaries have no UI route yet.
- Active planning inputs use user-DB append-only CAS revisions; importer/orchestrator must activate updates.
- No accepted MealPlan or diary log is implicitly created by generation.

## Decisions
- Original request outcome replays even after inputs change; refresh requires a new request ID.
- Full-precision decimal strings and ISO durations preserve pricing snapshots.
- Real catalog/G3, price/G4 and budget scope remain gated.
- Earlier consumption decisions remain in PlanConsumption contracts/tests and QA notes.

## Remaining work
- Wire authoritative catalog/import selection to active planning inputs.
- Explicit generation acceptance into MealPlan and future recalculation preview/acceptance, preserving history/consumed items.
- Screens 04/13/14, dated menu, recipe/portion confirmation and Today E2E.

## Relevant files
- data/local/{PlanGenerationRepository,UserRepositories,MealPlanRepository,PlanConsumptionRepository}.kt
- domain/planning/{WeeklyPlanContracts,WeeklyPlanGenerator,WeeklyPlanSession}.kt
- tests: data/local/PlanGenerationTest.kt; domain/planning/WeeklyPlanningFixture.kt
- docs/qa/android-k16-generation-acceptance.md; planning/coding-tasks.txt K16
- planning/research-data.json §8/18–19/22; docs/nutrition-plan-design.md

## Checks performed
- Scoped plan, local CI, clean tree/hooks and organization requirements read.
- Dedicated branch; merge-main CI result must be checked against final HEAD.
- No UI render, APK, device, clinical or real catalog/pricing acceptance claimed.

## Exact next step
- Verify latest merged-main CI, then continue explicit plan acceptance/recalculation as a separate scoped unit.
- Follow start/commit/finish lifecycle; use ui-craft before UI work; no APK without explicit request.
