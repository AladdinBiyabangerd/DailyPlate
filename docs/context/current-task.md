# Current task — K16 consumption/projection foundation

## Completed
- Added confirmed PlanConsumption command and atomic plan/log/event/receipt persistence.
- Added daily actual + remaining-plan projection using K15 WeeklyNutrition scale/sum.
- Added acceptance tests for half portions, retries, conflicts, rollback, unknown nutrients and log revisions.

## Current state
- Partial K16 only; no UI route uses these new boundaries yet.
- Full K15 domain generator remains available; real catalogs/G3, pricing/G4 and budget scope remain gated.
- No APK created. Local merged-main CI result is stored in .local-ci/latest.json/logs (check matching head).

## Decisions
- Item nutrientSnapshot means the full plannedServings; remaining scales by remaining/planned.
- Consumption advances plan revision; preserves shopping quantities, other items and historical snapshots.
- Durable receipt keyed by owner + clientEventId binds exact command and original result.
- Log edits/deletion affect actual intake, never silently restore remaining planned portions.
- Projection defaults diaryComplete=false; exact remaining-energy claim suppressed.

## Remaining work
- K16 persistent K15 generation request/result envelope with transactional profile/policy/catalog version checks.
- Future profile recalculation preview + explicit acceptance persistence, preserving past/consumed items.
- Screens 04/13/14, dated menu, recipe/portion confirmation and Today integration/E2E.
- Actual recipe editor must calculate/confirm snapshot before using consumption boundary.

## Relevant files
- domain/planning/{PlanConsumption,DailyPlanProjection,WeeklyNutrition,WeeklyPlanContracts,WeeklyPlanSession}.kt
- data/local/{PlanConsumptionRepository,MealPlanRepository,UserRepositories}.kt
- android/app/src/test/java/com/aladdin/dailyplate/data/local/PlanConsumptionTest.kt
- docs/qa/android-k16-consumption-acceptance.md; planning/coding-tasks.txt
- planning/research-data.json §8/18–19/22, docs/nutrition-plan-design.md

## Checks performed
- Read local workflow, organization, K16 and scoped canonical/customization requirements.
- Clean starting tree and expected hooks verified; dedicated local branch used.
- First merged-main CI passed: 174 JVM/Room tests, lint, Kotlin compilation, tokens, Python/library/export checks.
- Follow-up validates confirmed batch share/basis; inspect final matching-head CI result.
- No UI render, APK, device, real catalog, clinical or pricing acceptance claimed.

## Exact next step
- Continue K16 generation persistence/current-version validation using existing K15 contracts.
- Read this context and scoped requirements; use local start/commit/finish lifecycle.
- Before UI work use ui-craft, tokens and design guides; no APK without explicit request.
