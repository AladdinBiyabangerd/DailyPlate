# Current task — K08-v2 slice 1 (isolated, not integrated)

## Completed
- Read research 479f7ee context, design §26–29 and candidate JSON; compared main
  K08 arithmetic, snapshots and downstream candidate call sites.
- Internal formula dispatch with eight NASEM coefficient rows/four categories,
  explicit failures and nullable resting estimate. V1 Mifflin delegates arithmetic.
- Added regression tests; none executed. Slice plan/acceptance in
  docs/qa/android-k08-v2-implementation.md.

## Current state
- Worktree /private/tmp/callmood-k08-v2; branch feat/k08-v2-formula.
- Base b775fb2 already included research merge before this task; no merge here.
- Branch retained separately; no finish/CI/APK by explicit user instruction.

## Decisions
- Arithmetic is internal, not a personal target or v2 persistence contract.
- V1 engine still rejects NASEM. No silent legacy activity mapping.
- Candidate remains RESEARCH_PROPOSAL; no G2/G3, approval or production changes.
- Serialization/storage/FoodLog and menu generator unchanged.

## Remaining work
- Slice 2 v2 typed contract/eligibility; slice 3 macros/menu bounds;
  slice 4 backward-compatible migration/reconfirmation; slice 5 integration.
- No claim of full K08-v2 completion or passing runtime acceptance.

## Relevant files
- domain/policy/{EnergyFormula,NutritionPolicyEngine}.kt and EnergyFormulaTest.kt
  under android/app/src/{main,test}/java/com/aladdin/dailyplate/.
- docs/qa/android-k08-v2-implementation.md; planning/nutrition-policy-candidate.json
- docs/nutrition-plan-design.md §26–29; planning/nutrition-science-implementation-plan.txt

## Checks performed
- Source review and git diff --check; no CI, compile, tests, APK, device or clinical checks.

## Exact next step
- Review slice 1 diff, then implement only slice 2 on this isolated branch after
  reading QA slice plan and NP-07–12/NP-28. Preserve v1 payloads and production gate.
