# Current task — simplify home stock requirements

## Completed
- K14 arithmetic merged previously: 33381f3 / 39e3efe, green CI, 159 JVM tests.
- User removed exact home-stock quantity entry/deduction from the product plan
  entirely, including deferred scope. Weekly total needs remain visible.
- Only optional boolean “Evdə var” mark remains, separate from “Alınıb”.
- Canonical JSON, TXT/MD/canvas/PDF and authored K14/K17/customization requirements
  updated together. ShoppingItem planned contract uses home_available.

## Current state
- Documentation-only revision; existing K14 code still has numeric home deduction.
  This is a known implementation mismatch, not current product acceptance.
- Final merged-main CI evidence: .local-ci/logs/latest.json must match HEAD.

## Decisions
- Home mark never changes displayed weekly total or implies exact grams.
- No invented zero purchase cost from a boolean mark; budget remains subject to G4.
- No app UI, schema, pricing code or old acceptance records rewritten in this unit.

## Remaining work
- Next code unit: remove numeric home-stock input/deduction from ShoppingPricing
  and align tests. Review persisted ShoppingItem.homeQuantity compatibility before
  replacing it with a mark; preserve existing snapshots/history.
- Then K15 generator and K17 shopping UI. G4/real prices remain unverified.

## Relevant files
- planning/research-data.json §17.8/14 and §19.8/18
- planning/coding-tasks.txt K14/K17; planning/meal-customization-requirements.txt §6
- android/app/src/main/java/com/aladdin/dailyplate/domain/pricing/ShoppingPricing.kt
- android/app/src/test/java/com/aladdin/dailyplate/domain/pricing/ShoppingPricingTest.kt
- android/app/src/main/java/com/aladdin/dailyplate/domain/PersonalRecords.kt
- docs/qa/android-k14-acceptance.md (historical implementation evidence)

## Checks performed
- Clean initial tree, hooksPath and local workflow checked; diff whitespace review.
- All canonical views regenerated; PDF changed §17 page rendered and inspected.
- Mandatory CI only through workflow finish after merge. No APK/device checks.

## Exact next step
- Start separate fix branch for the K14 home-stock API/model alignment above;
  read relevant plan requirements and persistence usages before editing code.
