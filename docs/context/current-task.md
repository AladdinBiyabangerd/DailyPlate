# Current task — K16 future-plan recalculation boundary

## Completed
- Durable generation and explicit initial acceptance retained.
- Added futurePlans.preview/confirm with durable exact before/after records and transactional stale-version checks.
- Uses confirmed profile transition + stored generation; preserves entire past/consumed/partial/skipped items and all FoodLogs.
- Revalidates merged candidate via shared K15 candidate/nutrition/weekly/pricing components.
- Shopping differences and marks retained, including zero-need purchased products; atomic plan/shopping/receipt acceptance.

## Current state
- K16 persistence/orchestration boundaries exist. No UI routes yet.
- Production catalog/G3, price/G4 and budget gates remain closed.

## Decisions
- Only future untouched slots change. Partial items, including remaining portions, stay intact under existing FuturePlanPreview contract.
- Rejected merge is an explicit outcome, not proof all possible menus are infeasible; use a new request/seed for another candidate.
- Missing exact historical/customization evidence blocks rather than inventing nutrition/shopping data.
- Existing single confirmed profile transition contract retained; skipped transitions fail explicitly.
- New-week alternatives are not valid for the merge and are explicitly cleared.
- Preview computes planned-menu totals. Actual/remaining diary projection continues through dailyPlanProjection.

## Remaining work
- Screens 04/13/14, dated menu, recipe/portion confirmation and Today UI E2E.
- Authoritative catalog/import activation wiring and real catalog/pricing acceptance.
- Broader customized historical evidence support and chained profile-change composition if needed.

## Relevant files
- data/local/{FuturePlanRepository,PlanGenerationRepository,PlanAcceptanceRepository,MealPlanRepository,UserRepositories}.kt
- domain/planning/{FuturePlanCalculation,GeneratedMealPlan,WeeklyPlanChecks,WeeklyPlanGenerator,DailyPlanProjection}.kt
- domain/profile/FuturePlanPreview.kt; data/local/FuturePlanRepositoryTest.kt
- docs/qa/android-k16-generation-acceptance.md
- planning/coding-tasks.txt K16; planning/research-data.json §8/18–19/22; docs/nutrition-plan-design.md

## Checks performed
- Prior main f3a642a CI passed; lifecycle, scoped canonical requirements and organization read.
- New Room/JVM acceptance scenarios added; final merged-main CI result recorded in task response.
- No UI change/render, APK, device, clinical or real catalog/pricing acceptance claimed.

## Exact next step
- Continue K16 UI integration as a new scoped chat after reading ui-craft and relevant design references.
- Follow start/commit/finish lifecycle; no APK without explicit request.
