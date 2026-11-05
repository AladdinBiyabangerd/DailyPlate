# Current task — K15 handoff after completed K14

## Completed
- Removed numeric home input from production/test ShoppingPricing APIs and estimator.
- Removed home/buy gram result fields; packages use full aggregated weekly need.
- Updated eight pricing tests and K14 acceptance/status notes for revised §17/19.

## Current state
- K14 arithmetic aligned with canonical planning/research-data.json §17.8/14,
  §19.8/18 and customization §6. No UI changes.
- K14 implementation cb5e9b7 merged as ee00ed0; matching-head CI passed:
  159 JVM tests, lint, compilation, tokens, library contracts and canonical exports.
- User requested the next milestone: K15. No K15 code has been written.

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
- Implement K15 in a new chat. Read this file, .local-ci/README.md and
  .cursor/rules/code-organization.mdc; verify status/hooks and start a task branch.
- Read current canonical planning/research-data.json §7–8, §11–12, §17–18,
  §21 R-06, §22 C-01–C-10, §25 A-04/A-06–A-08/A-12 and binding
  docs/nutrition-plan-design.md; consult planning/coding-tasks.txt K15.
- Inspect existing K08/K13 nutrition screening, portion/feasibility and K14 pricing
  components before defining bounded deterministic generation. Reuse them.
- Identify acceptance cases before coding: 7 days × 3/4/5 slots, real portion
  min/max/step, daily/weekly nutrients, variety/time/package budget, explanations
  and alternatives; 4 goals × 3 budget tiers × 3 slot counts × 3 time limits = 108.
- Return an explicit reason when hard rules cannot pass; missing nutrients are
  UNKNOWN, not verified. No silent budget relaxation or goal inflation by slots.
- Use approved test fixtures without claiming real catalog/G3/G4 acceptance.
  Finish through local merge-main CI only. Do not create APKs.
