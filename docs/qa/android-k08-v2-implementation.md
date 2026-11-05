# K08-v2 isolated implementation slices

Baseline: main b775fb2. Research: docs/nutrition-policy-research, 479f7ee,
`docs/nutrition-plan-design.md` §26–29 and candidate JSON. Main already contains
the research merge b775fb2 before this implementation began; this task did not
merge it. Candidate remains RESEARCH_PROPOSAL, production_enabled=false,
approved_by/approved_at=null. No new approval or G2/G3 activation.

## Slices and acceptance

1. **Implemented, execution unverified:** internal formula dispatch and four typed
   NASEM categories (NP-02–04, NP-24–28). Direct EER has nullable resting estimate,
   no PAL factor; wrong activity model, invalid inputs/results and unknown formula
   versions return explicit failures. V1 engine delegates only its existing
   Mifflin arithmetic. V1 assessment deliberately continues rejecting NASEM.
   Tests cover eight independent research examples, changed age/weight/height,
   overflow/invalid inputs, unsupported versions, activity mismatch, legacy exact
   arithmetic and the v1 activation barrier. These are synthetic arithmetic
   regressions, not scientific/clinical validation.
2. **Implemented, execution unverified:** separate serializable research energy contract v2 with research-only status,
   formula-specific inputs, evidence/provenance and typed RuleResults. Add
   NP-07–12 age/BMI/goal/explicit extra health screening and sex floors, no clamp.
   Model age (19+) and product age (19–64) have distinct RuleResults; specialist
   path stays separate. Pure internal preview has no storage/production entry point.
   All ten health answers must explicitly be false; activity and measurements
   require confirmation. Unknown/failed rules produce no candidate target.
   Provenance records canonical source and evidence refs without approval.
   Macro/menu fields remain slice 3; persisted migration remains slice 4.
3. **Remaining:** max/remainder macros with MACRO_INFEASIBLE; typed goal-specific
   menu L/U and strict lose/gain inequalities (NP-13–23). This prepares the
   generator interface; do not implement a menu generator. Unknown nutrient
   definitions/completeness never pass actual-menu checks.
4. **Remaining:** backward-compatible snapshot decoding and migration strategy,
   explicit four-choice activity reconfirmation (even overlapping `active` code),
   preview/confirm future-only revisions and reassessment. Preserve all old
   FoodLog, policy/profile and calculation payloads without recomputation.
5. **Remaining:** integration/regression coverage across K09/K12/K13 and eventual
   K15 consumers. Only after user-authorized integration: workflow finish and
   APK-free merged-main CI. No branch/manual CI.

## Dependency comparison

- PolicyExecutionRules requires contractVersion==1; NutritionPolicy has scalar
  activityMapping; NutritionPolicyEngine explicitly accepts only Mifflin v1.
  Do not encode EER coefficients in activityMapping or relax v1 validation.
- CalculationInputs requires activityFactor and PolicyCalculation requires
  positive restingKcal. EER must use a new versioned representation rather than
  fake resting energy or a factor of one in historical v1 snapshots.
- SnapshotJson rejects unknown fields; UserSnapshotRepository supports payload
  schema 1 and append-only revisions. New serialization/migration requires
  persisted legacy fixtures, not merely round-trip tests of new objects.
- K09 profile changes and K12 diary/history consume versioned profiles/results;
  FoodLog has independent frozen nutrition. Neither persistence path is changed
  by slice 1. Preservation has been inspected, not migration-tested.
- K13 RecipeCandidateFilter calls assess/assessTest and uses current eligibility;
  retain its production denial and add v2 screening regressions during integration.
- K14 pricing and K15 planning depend on candidate/eligibility and energy bounds.
  No pricing/filter/generator code is changed here. K15 requires explicit hard
  failure/unknown results and menu intervals, not a synthetic successful plan.

## Checks and integration acceptance still required

Performed: source/call-site review and git diff --check only. No compilation,
JVM tests, lint, CI, APK or device checks were run under the isolation instruction.
EnergyFormulaTest, ResearchEnergyV2Test and existing NutritionPolicyEngineTest are **not executed**.
Slice 2 adds unexecuted age/BMI/goal boundaries, every missing/positive health answer,
missing sex/activity, confirmation, invalid/tiny inputs, both sex floors, no-clamp
examples, research-only invariant and new-contract serialization regressions.
No legacy payload, persistence adapter, production gate or downstream consumer changed.
At integration run the configured post-merge checks via workflow finish, including
APK-free lintDebug, compileDebugKotlin, testDebugUnitTest, library contract and
canonical export checks; never invoke CI manually or package an APK.

Still unverified: all compiled/runtime assertions; v1 persisted fixture decoding;
FoodLog/history byte preservation through migration; profile reconfirmation and
future-only confirm; NP-27 boundary matrix (18/19/64/65, BMI18.5/25/30, missing
sex/health/activity, tiny/nonfinite values, floor rejection, macro infeasibility,
empty catalog); K13 regressions and K15 contract consumption. Clinical review,
source access gaps, self-report pilot, device acceptance and G2/G3 stay pending.
