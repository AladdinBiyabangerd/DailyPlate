# Current task — K14 price arithmetic

## Completed
- K14 pure shopping/pricing engine and eight JVM acceptance tests implemented.
- Decimal usage/full-package cost, weekly aggregation, home deduction, exact
  state/region/currency matching, explicit versioned freshness, missing reasons.
- Production G4 gate remains closed; test observations explicitly marked.

## Current state
- Implementation complete; authoritative final CI is .local-ci/logs/latest.json:
  require passed=true and head matching main. See chat for final execution result.
- K15 is next; no generator, shopping UI or real price acceptance claimed.

## Decisions
- No invented freshness threshold or cooking yield; exact-state edible grams only.
- Production total null until G4; budgetComplianceVerified always false.
- Existing IngredientPrice snapshot persistence reused, no schema migration.

## Remaining work
- K15 generator; K17 shopping marks/deltas/UI. Real prices and G4 remain separate.
- 108-profile generator matrix, clinical/pilot and device/APK unverified.

## Relevant files
- android/app/src/main/java/com/aladdin/dailyplate/domain/pricing/ShoppingPricing.kt
- android/app/src/test/java/com/aladdin/dailyplate/domain/pricing/ShoppingPricingTest.kt
- docs/qa/android-k14-acceptance.md; planning/coding-tasks.txt
- planning/nutrition-science-implementation-plan.txt §17/19.8/21/25

## Checks performed
- Initial clean status, hooksPath, local workflow and scoped requirements read.
- Diff whitespace review; post-merge CI via workflow finish only (see latest.json).
- No APK, device, remote or UI checks performed.

## Exact next step
- On K15 instruction read its requirements and generator contracts; start a new
  local workflow branch. Do not repeat K14 or infer G4 approval from test prices.
