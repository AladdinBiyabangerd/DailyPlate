# K15 — bounded deterministic weekly generation

Implemented domain preview: `domain/planning/WeeklyPlanGenerator.kt`, contracts,
nutrient adapter and bounded request session. No UI, DB write, diary event or APK.

## Requirement → evidence

- §8, C-08/A-07: `all108CombinationsUseRealPortionsAndUnchangedTargets` exercises
  all four goals, three budget preferences, 3/4/5 slots and 15/30/60-minute limits.
  Synthetic published editorial envelopes, current K08-v2 screening, exact-version
  K13 ingredient safety, recipe min/max/step and K14 full-package pricing are used.
- NP-13–23/C-05–07: K08 `NutritionMenuChecks` checks every day. Recipe batch totals
  scale by cooked yield; no retention is applied twice and per-100g display values
  are not used. Daily and weekly snapshots propagate missing values and convert
  compatible mass units. Available carbohydrate requires explicit editorial basis.
  WHO free sugar (<10%), saturated fat (≤10%), trans fat (≤1%), salt (<5g via
  sodium conversion) and mapped fruit/vegetables (≥400g) are additional daily checks.
- K08 wider-coverage UNKNOWN remains blocking unless an exact-policy reviewed
  supplemental reference set and exact-recipe protein-quality review are supplied.
  Selected source thresholds keep their own operator, unit and day/week window.
  WHO total-fat advisory remains separate from EFSA RI. SUCCESS means the selected
  reference set passed, **not** universal dietary adequacy. Restricted diets are
  blocked with INCOMPLETE_DATA; no B12/supplement/clinical claim is introduced.
- §7/17/C-09: no recipe repeats on adjacent days; occurrence and distinct mapped
  vegetable/grain/protein source minima are explicit versioned product parameters.
  Preparation time and equipment use K13. No preference or limit is relaxed.
- K14/A-08: full weekly ingredient needs use exact food state and batch portion
  ratios; package cost is compared only for one person, explicit full `slot:0` …
  `slot:N-1` budget coverage and currency. Onboarding deliberately does not collect
  this coverage, so its budget remains UNKNOWN and this conservative generator
  returns no publishable plan. It never invents coverage or a precise budget badge.
  Budget tiers are preferences, not invented numerical prices or limits.
- C-08/A-08: seeded stable order, bounded portion enumeration and visited-node
  search, explicit TEMPORARY_FAILURE on exhaustion (not infeasibility proof).
  A found plan can have zero alternatives; each slot reports missing alternatives.
  Offered alternatives recheck the complete affected day, weekly variety, repetition,
  adjacent-day repetition and full package budget. Nothing is applied automatically.
- C-10: exact request/profile/policy/catalog/recipe refs and nutrient coverage are
  retained. The complete K08 preview retains its actual EER formula/contract/provenance,
  alongside the selected supplemental coverage snapshot (the fixture editorial policy
  ref is not mislabelled as an EER formula). Energy proximity ranks first, macro
  proximity is a soft tie-breaker and never changes a hard threshold. `WeeklyPlanSession` reuses identical request payloads, rejects changed
  payloads under an existing request ID, and has bounded capacity. This is in-memory
  preview idempotency; K16 must persist envelopes and transactionally compare current
  versions before a write. Restart/DB race guarantees are not claimed here.
- A-12: K13 rejects withdrawn editorial envelopes and stale requested versions.
  Historical records are never rewritten. K15 does not replace K03 atomic imports.

## Checks and limits

`WeeklyPlanGeneratorTest` covers the 108 positive combinations, deterministic
reordering, independently checked portion/nutrient properties, variable portions,
unknown fibre/free sugar/salt/trans fat/carbohydrate, package budget failure,
unknown scope/prices, stale versions, empty/withdrawn catalog, restricted diets, source-specific day/week coverage, exact WHO
operators and unknown protein-quality evidence,
production gate, exclusions, zero alternatives, search exhaustion and request conflicts.
K13/K08/K14 regression suites remain mandatory. Local CI runs **only after merge**
via workflow finish: APK-free lint, compile, JVM tests, tokens and data/export checks.
Consult `.local-ci/logs/latest.json` for matching-head execution evidence.

All catalog/mapping/prices in this acceptance are TEST fixtures. Real G3 recipe
coverage, G4 regional price/scope acceptance, real-user nutrient adequacy, production
plan activation, K16 persistence/UI, device/pilot tests remain unverified. No APK
packaging, installation or instrumentation is authorized or performed.
