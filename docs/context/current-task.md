# Current task — K16 menu UI integration

## Completed
- Menu route wired to production generation, explicit acceptance and futurePlans preview/confirm.
- Exact pending request/seed/acceptance IDs saved before compute; retry resumes after recreation/restart.
- Real dated accepted-plan menu, profile goal/slot summary, AZ/EN/RU empty/failure states.
- Today uses accepted-plan actual + remaining projection without declaring diary completeness.

## Current state
- Production catalog/G3, price/G4 and budget gates remain closed.
- No synthetic success or fixture bypass in UI. No APK authorized.

## Decisions
- Existing repository invariants and immutable receipts retained.
- New initial weeks begin after existing accepted weeks to avoid overlapping generated weeks.
- Profile recalculation is separate from initial acceptance, preserves protected history.

## Remaining work
- Complete recipe detail/name/ingredient/step presentation and portion-confirmed Yedim UI.
- Successful production UI acceptance/recalculation E2E with authoritative catalog/pricing.
- Dated-menu/successful-preview visual and device coverage.

## Relevant files
- ui/menu/{MenuWorkflow,MenuScreen,TodayPlanProjection}.kt; ui/BootstrapScreen.kt; ui/today/TodayScreen.kt
- data/local/{PlanGenerationRepository,PlanAcceptanceRepository,FuturePlanRepository,MealPlanRepository}.kt
- ui/MenuUiTest.kt; docs/qa/android-k16-generation-acceptance.md
- planning/coding-tasks.txt K16; planning/research-data.json §8/18–19/22; docs/nutrition-plan-design.md

## Checks performed
- Read lifecycle, organization, canonical scoped requirements, ui-craft, tokens and design references.
- APK-free Kotlin compilation and all six token contrast pairs passed during post-merge checks.
- AZ/EN/RU missing-input screens rendered and inspected at 1.5 font scale.
- Lint found plural-candidate and SharedPreferences extension guidance; repaired without losing synchronous commit failure detection.
- Final full merged-main CI result recorded in chat response.
- No APK/device/clinical/real catalog acceptance claimed.

## Exact next step
- Next scoped unit: recipe detail + idempotent portion-confirmed Yedim, following K16 and customization §2/7.
