# Current task — K08-v2 slice 2 (isolated, not integrated)

## Completed
- Slice 1 internal NASEM/Mifflin formula dispatch retained.
- Slice 2 internal serializable research energy contract v2, provenance, typed
  RuleResults and pure screening/energy preview implemented (NP-07–12/NP-28).
- Distinct model/product age scope, unrounded BMI/goal checks, ten explicit
  health answers, measurement/activity confirmation, sex floors and no clamp.
- Added boundary, exclusion, input, floor and serialization tests; not executed.

## Current state
- Worktree /private/tmp/callmood-k08-v2; branch feat/k08-v2-formula.
- Explicit user instruction: retain isolated branch; no merge, finish, CI or APK.
- Existing task branch continued; hooksPath=.local-ci/hooks; initial tree clean.

## Decisions
- Candidate remains RESEARCH_PROPOSAL; no approval/G2/G3 or production activation.
- Pure preview is an intermediate research energy result, not a complete plan.
- No v1 payload/schema, storage, FoodLog, specialist or downstream changes.
- New research input requires all ten explicit health responses, including
  reproductive responses; it does not reuse legacy male onboarding omissions.
- Date freshness/reconfirmation migration belongs to slice 4, not this preview.

## Remaining work
- Slice 3 macro max/remainder feasibility and typed goal-specific menu bounds.
- Slice 4 persisted legacy fixtures/migration, freshness, preview/confirm revisions.
- Slice 5 integration/regressions; authorization required before integration.

## Relevant files
- android/app/src/main/java/com/aladdin/dailyplate/domain/policy/
  ResearchEnergyContractV2.kt, ResearchEnergyEligibilityV2.kt, EnergyFormula.kt
- android/app/src/test/java/com/aladdin/dailyplate/domain/policy/ResearchEnergyV2Test.kt
- docs/qa/android-k08-v2-implementation.md
- planning/nutrition-policy-candidate.json; docs/nutrition-plan-design.md §26–29
- planning/nutrition-science-implementation-plan.txt

## Checks performed
- Source review and git diff --check only.
- No compilation, test execution, lint, CI, APK, device or clinical validation.
- Serialization assertions are new-object tests, not legacy migration evidence.

## Exact next step
- Read QA slice plan and design NP-13–23; implement only slice 3 on this isolated
  branch. Preserve v1, production denial and the no-merge/CI/APK instruction.
