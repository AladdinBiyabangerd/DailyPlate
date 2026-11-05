# Current task — K08-v2 slices 1–2 integration

## Completed
- Slice 1 internal NASEM/Mifflin formula dispatch retained.
- Slice 2 internal serializable research energy contract v2, provenance, typed
  RuleResults and pure screening/energy preview implemented (NP-07–12/NP-28).
- Distinct model/product age scope, unrounded BMI/goal checks, ten explicit
  health answers, measurement/activity confirmation, sex floors and no clamp.
- Added boundary, exclusion, input, floor and serialization tests; not executed.

## Current state
- Worktree /private/tmp/callmood-k08-v2; branch feat/k08-v2-formula.
- User subsequently authorized local merge; mandatory post-merge CI applies. No APK.
- Integration prepared on fix/k08-v2-integration; feat/k08-v2-formula retained.
- Existing task branch continued; hooksPath=.local-ci/hooks; initial tree clean.

## Decisions
- Frozen research contract retains RESEARCH_PROPOSAL; current canonical candidate
  separately records SPECIALIST_APPROVAL_REPORTED. This merge does not promote
  the frozen runtime contract or enable production/G2/G3 execution.
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
- Verify merged-main CI; next implementation unit is slice 3 (NP-13–23).

## Latest nutrition-plan status
- Research proposal 479f7ee merged into main as b775fb2; CI skipped at user request.
- User subsequently reported specialist approval of the entire written plan and
  requested no reviewer name. Plan 2.2-reported-review records USER_ATTESTATION,
  scope/version/hash; qualification and review date remain unknown.
- No fake identity/date/document or independent verification claimed. No Android
  execution flag enabled; implementation/content/device acceptance remains separate.
- Canonical research-data.json and generated MD/TXT/canvas/candidate JSON/PDF updated.
- Exact next step: implement the scoped K08-v2 contract in a separate task using
  plan §26–30; retain snapshots and verify actual implementation before activation.
