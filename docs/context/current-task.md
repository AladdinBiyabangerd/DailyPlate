# Current task — enable current automatic nutrition calculation

## Completed
- User explicitly requested production_enabled=true on 5 November 2026.
- Canonical candidate and new NutritionEnergyEligibility previews now enable
  current energy/macro calculation. Derived TXT/MD/canvas/PDF updated together.
- Contract decoding retains false as the historical missing-field default;
  new calculations explicitly select true. Research status cannot enable it.
- Prior preview-preservation main 1a6bc01 passed 146 JVM tests and APK-free CI.

## Current state
- Activation commit must finish through workflow.py finish; authoritative final
  result is .local-ci/logs/latest.json matching main HEAD.

## Decisions
- Existing screening, floors and macro feasibility remain enforced.
- No catalog/menu activation, remote actions, APK or fabricated approval metadata.
- Existing assessment payloads are not rewritten; prior approval hash retained.

## Remaining work
- NP-16 voluntary-weight trend consumer and symptom suspension acceptance.
- Real device/pilot and real catalog/portion/nutrient feasibility unverified.

## Relevant files
- planning/research-data.json; docs/nutrition-plan-design.md §26–30
- android/app/src/main/java/com/aladdin/dailyplate/domain/policy/NutritionEnergyContract.kt
- android/app/src/main/java/com/aladdin/dailyplate/domain/policy/NutritionEnergyEligibility.kt
- android/app/src/test/java/com/aladdin/dailyplate/domain/policy/NutritionEnergyTest.kt

## Checks performed
- git diff --check; regenerated all plan views; inspected PDF page 44.
- Activation tests execute only in merged-main CI. No APK/device check.

## Exact next step
- Confirm activation merged-main CI result, then continue remaining NP-16 work
  in a new chat. APK creation remains forbidden.
