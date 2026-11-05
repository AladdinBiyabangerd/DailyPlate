# Current task — K14 full weekly shopping needs

## Completed
- Removed numeric home input from production/test ShoppingPricing APIs and estimator.
- Removed home/buy gram result fields; packages use full aggregated weekly need.
- Updated eight pricing tests and K14 acceptance/status notes for revised §17/19.

## Current state
- K14 arithmetic aligned with canonical planning/research-data.json §17.8/14,
  §19.8/18 and customization §6. No UI changes.
- Final execution evidence is matching-HEAD .local-ci/logs/latest.json passed=true.

## Decisions
- Optional “Evdə var” is not a pricing input and cannot imply free purchases.
- G4 gate remains; no real prices or budget acceptance invented.
- Persistence reviewed: schema-1 snapshots decode strictly and writes compare exact
  JSON for idempotency. Legacy ShoppingItem.homeQuantity is unchanged to preserve
  historical payloads. Pricing does not consume this model/field.

## Remaining work
- Backward-compatible ShoppingItem.homeAvailable transition and shopping mark
  preservation remain for K17; boolean model contract is not yet implemented.
- K15 generator and K17 UI; real prices/G4 remain unverified.

## Relevant files
- planning/research-data.json §17/19; planning/coding-tasks.txt K14/K17
- android/app/src/main/java/com/aladdin/dailyplate/domain/pricing/ShoppingPricing.kt
- android/app/src/test/java/com/aladdin/dailyplate/domain/pricing/ShoppingPricingTest.kt
- android/app/src/main/java/com/aladdin/dailyplate/domain/PersonalRecords.kt
- android/app/src/main/java/com/aladdin/dailyplate/data/local/SnapshotJson.kt
- android/app/src/main/java/com/aladdin/dailyplate/data/local/UserSnapshotRepository.kt
- docs/qa/android-k14-acceptance.md

## Checks performed
- Initial clean tree and .local-ci/hooks verified; local workflow start used.
- Scoped usage/persistence review and git diff --check.
- Mandatory checks run only by workflow finish on merged main; consult matching
  latest.json for result. No APK or device tests authorized or run.

## Exact next step
- After green merged-main CI, choose the next separately scoped implementation
  unit from planning/coding-tasks.txt; do not treat K17 persistence/UI as complete.
