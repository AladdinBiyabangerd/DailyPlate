# Current task — K15 weekly generator

## Completed
- Implemented bounded seeded 7-day × 3/4/5-slot domain generation and exact recipe grids.
- Reused K08-v2 screening/energy/menu checks, K13 safety filter and K14 package pricing.
- Added explicit WHO nutrient/group evidence, daily/weekly snapshots, full revalidated
  alternatives, request-session idempotency/conflict handling and acceptance tests (108 cases).
- Production G3/G4 gates, restricted-diet coverage and UNKNOWN wider nutrients remain explicit.

## Current state
- K15 domain preview is implemented; matching-main CI result is in local latest.json.
- No UI/DB writes or activation of real catalogs. No APK created.

## Decisions
- SUCCESS covers the documented healthy-adult subset; never full micronutrient adequacy.
- Budget comparison needs explicit single-person full slot scope; onboarding omits scope,
  so ordinary profile budget is UNKNOWN and cannot produce a publishable plan here.
- Alternatives may be absent, with slot-specific UNKNOWN; never fabricated.
- Search rules are versioned product parameters; exhaustion is TEMPORARY_FAILURE.
- In-memory preview session is bounded; persistent idempotency/version write checks belong to K16.

## Remaining work
- K16 preview/persistence/UI with transactional current-version checks.
- Real catalog/G3, regional pricing/G4 and actual budget coverage remain unverified.
- K17 shopping boolean model/mark preservation remains as earlier handoff.

## Relevant files
- domain/planning/{WeeklyPlanContracts,WeeklyNutrition,WeeklyPlanGenerator,WeeklyPlanSession}.kt
  under android/app/src/main/java/com/aladdin/dailyplate/
- android/app/src/test/java/com/aladdin/dailyplate/domain/planning/WeeklyPlanGeneratorTest.kt
- docs/qa/android-k15-acceptance.md; planning/coding-tasks.txt
- planning/research-data.json §7–8/11–12/17–18/21–22/25, docs/nutrition-plan-design.md

## Checks performed
- Read local workflow/current context/organization rules and scoped canonical requirements.
- Clean initial tree/hooks verified; local task branch used. git diff --check.
- Mandatory APK-free checks only through finish on merged main; see matching-head local log.
- No device, clinical, real-content or real-price acceptance claimed.

## Exact next step
- Read K16 and applicable canonical requirements in a new chat; inspect K15 contracts.
- Resolve preview/publishable semantics for unknown budget scope without inventing coverage.
- Use local workflow start/commit/finish; do not create APK unless explicitly requested.
