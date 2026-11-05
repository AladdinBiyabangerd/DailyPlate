# Current task — K08-v2 slice 3 (isolated)

## Completed
- Slices 1–2 merged as c6f8342; post-merge CI passed (129 JVM tests plus lint,
  compilation, tokens, 15 library tests and canonical export checks).
- Slice 3 implemented: max/remainder macro vector with MACRO_INFEASIBLE,
  typed goal-specific menu bounds/strict inequalities and 3/4/5 slot templates.
- Separate source-menu checks for energy, protein, fat, available carbohydrate,
  fibre, catalog feasibility, WHO advisory and unknown remaining coverage.
- Synthetic regression and serialization tests added; slice 3 tests not run.

## Current state
- Workspace /Users/mac/My Workspace/callmood; branch feat/k08-v2-macro-menu.
- User explicitly requests separate branch; no merge, finish, CI or APK.
- Clean main c6f8342 was the base; local hooks configured and workflow start used.

## Decisions
- Frozen runtime contract remains RESEARCH_PROPOSAL; canonical plan separately
  records SPECIALIST_APPROVAL_REPORTED/USER_ATTESTATION. Production stays disabled.
- Planning failure clears both target fields; screening/maintenance stays diagnostic.
- Arithmetic success is a search specification, not proof of a real feasible menu.
- Future catalog adapter supplies verified published-portion feasibility; no solver
  or catalog validation implemented here. Empty catalog/no combination cannot pass.
- Other nutrients/protein quality/B12/restricted diet coverage remain UNKNOWN.
- Eight ULP tolerance only for macro energy conservation; bounds/RI are unrounded.
- NP-16 review/freshness/confirmation workflow stays in slice 4 per QA scope.
- V1 payloads, snapshots, FoodLog, specialist path and K13/K14 unchanged.

## Remaining work
- Execute slice 3 checks only when integration is authorized.
- Slice 4 persisted legacy fixtures/migration, freshness and future-only confirmation.
- Slice 5 downstream regressions, eventual K15 catalog/generator consumption.

## Relevant files
- android/app/src/main/java/com/aladdin/dailyplate/domain/policy/
  ResearchPlanningContractV2.kt, ResearchPlanningV2.kt, ResearchMenuChecksV2.kt
- android/app/src/test/java/com/aladdin/dailyplate/domain/policy/ResearchPlanningV2Test.kt
- docs/qa/android-k08-v2-implementation.md
- docs/nutrition-plan-design.md NP-13–23; planning/nutrition-policy-candidate.json
- planning/nutrition-science-implementation-plan.txt

## Checks performed
- Slice 3 source review and git diff --check only; compile/tests/lint/CI not run.
- No APK/device or clinical validation. New serialization tests do not prove migration.

## Exact next step
- Review slice 3 diff; if user authorizes merge, use workflow finish and APK-free
  post-merge CI. Otherwise implement only separately requested slice 4 after reading
  its QA requirements, legacy snapshot adapters and design NP-06/16/27/28.
