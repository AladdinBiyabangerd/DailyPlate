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
3. **Implemented, execution unverified:** max/remainder macros with MACRO_INFEASIBLE; typed goal-specific
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

## Local integration authorization

User authorized local merge after slice 2. Current main's canonical candidate now
records SPECIALIST_APPROVAL_REPORTED / USER_ATTESTATION (see design §30). The
internal v2 research contract remains a frozen RESEARCH_PROPOSAL representation;
this merge does not import approval metadata or activate a production policy.
The isolation/unchecked statements above describe the original slice work.
Post-merge CI is now required; no APK/device checks are authorized.

## Slice 3 — isolated implementation

Branch: feat/k08-v2-macro-menu, based on c6f8342 (slices 1–2 integration).
That base passed post-merge CI: 129 JVM tests, Android lint/compilation/tokens,
15 library tests and canonical export checks. This is historical evidence only;
it does not verify slice 3. User explicitly requests no merge, CI or APK here.

Implemented acceptance mapping:
- NP-13/14: finite typed L/U, sex floor, ±5% product tolerance and strict E<M/E>M;
  invalid/empty intervals return NO_FEASIBLE_PLAN. No universal maximum.
- NP-15: three/four/five slot templates divide the same target; unsupported slot
  counts fail explicitly. Templates do not prove feasible real portions.
- NP-17–19: actual-weight max(PRI,20%) protein, 30% fat and available-carbohydrate
  remainder. Nonpositive/nonfinite vectors or carbohydrate outside 45–60% fail
  MACRO_INFEASIBLE without a usable planning target or repaired split. Only energy
  conservation uses an eight-ULP arithmetic tolerance; range checks are unrounded.
- NP-20/21: separate source-menu totals and per-nutrient completeness; unknown
  carbohydrate definition is UNKNOWN, not by-difference minus fibre. Source
  energy is never replaced by a 4/9/4 reconstruction. Checks cover protein PRI,
  EFSA fat/carbohydrate RI, fibre AI and the hard menu energy interval.
- NP-22/23: WHO total-fat advisory is separate from the EFSA hard RI. Other WHO
  nutrients, protein quality, B12 and restricted-diet coverage remain UNKNOWN;
  this subset cannot certify full balance or production eligibility.
- Catalog adapter seam has NOT_EVALUATED/EMPTY_CATALOG/NO_REAL_COMBINATION/
  VERIFIED_PUBLISHED_PORTIONS states. Empty/no-combination returns a typed
  NO_FEASIBLE_PLAN reason. The verified state must be supplied by a future real
  catalog/portion validator; slice 3 does not search or validate catalog records.
- NP-16 reassessment/freshness/confirmation remains slice 4 as scoped by this QA
  plan. No automatic titration, reminders, persistence or generator added.

ResearchPlanningV2Test adds synthetic plan examples, max/remainder infeasibility,
strict interval endpoints, slots, blocked screening/floor propagation, per-nutrient
missing coverage, carbohydrate definitions, empty catalog, separate WHO advisory,
actual menu boundaries and serializable new-contract round trips.

Checks: source review and git diff --check only. No slice-3 compile, JVM tests,
lint, CI, APK/device or clinical execution. New tests are unexecuted. Existing
v1 snapshots, FoodLog, downstream K13/K14 and production gates are untouched.
On planning failure both the top-level target and nested candidate energy target
are cleared; screening/maintenance diagnostics remain.
