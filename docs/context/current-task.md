# Current task — execution queue reconciliation

## Completed
- Reconciled planning/coding-tasks.txt with existing code and acceptance evidence.
- K13 hard filters exist (00a0ca2); K08-v2 integration/activation is already on main
  (20473a9). NP-16 is complete (47cb07d; final acceptance/context 5daf7de).
- Corrected stale K13-next, research-not-integrated and inactive-calculation claims.

## Current state
- Before this documentation update, main 5daf7de had matching green CI: 151 JVM
  tests, lint/compile, tokens/contrast, library contracts and canonical export parity.
- This documentation-only unit uses finish; authoritative final result is
  .local-ci/logs/latest.json with head equal to current main HEAD and passed=true.
- K14 is next in the implementation queue; it has NOT been started in this unit.

## Decisions
- Current automatic energy/macros are explicitly user-enabled under canonical §30.
  Catalog/menu approval remains separate and blocked; no new clinical approval.
- NP-17–23 macro/menu-check core exists, but does not complete K15 generation,
  full nutrient coverage or real published-portion validation.
- Existing models/contracts are preparation, not acceptance of K14–K34.
- Only coding-tasks.txt and this context changed. No app/canonical plan/UI changes.

## Remaining work
- Await a separate instruction before K14 implementation. Inspect pricing contracts
  and plan §17/19.8, §21 R-06, A-07/A-08 and customization §6 first.
- K15 generator and K27 broader weight entry/progress remain separate milestones.
- Device/APK/pilot, clinical effectiveness and real catalog/price acceptance remain
  unverified. No APK or remote actions authorized/performed.

## Relevant files
- planning/coding-tasks.txt; planning/nutrition-science-implementation-plan.txt
- docs/nutrition-plan-design.md §30 (canonical source: planning/research-data.json)
- docs/qa/android-k13-acceptance.txt; android-k08-v2-implementation.md;
  android-np16-acceptance.md (all under docs/qa/)
- android/app/src/main/java/com/aladdin/dailyplate/domain/planning/RecipeCandidateFilter.kt
- android/app/src/main/java/com/aladdin/dailyplate/domain/policy/NutritionPlanning.kt
- android/app/src/main/java/com/aladdin/dailyplate/domain/policy/NutritionMenuChecks.kt

## Checks performed
- Read code, acceptance records and commit history; verified clean initial tree,
  hooksPath=.local-ci/hooks and baseline matching-head green CI.
- Documentation diff/whitespace review; final post-merge check via workflow finish.
- No branch/manual CI, new UI render, APK or device checks.

## Exact next step
- Read this note and coding-tasks.txt. K14 is the next implementation candidate;
  start it only on a separate user instruction. Do not repeat K13 or NP-16.
