# Current task — K04 nutrient and portion core

## Completed
- K03 merged main green at 20ad2bb before this unit; G3 remains closed.
- K04 pure domain/nutrition core: ingredient sums, batch scaling vs personal share,
  documented cooked yield, raw-only retention/ingredient loss, g/kg and version-bound
  food measures, compatible nutrient units, unknown propagation and display rounding.
- LibraryImporter stored total/serving validation now reuses this core.
- Ten independent JVM tests added; CI requires them alongside six existing suites.

## Current state
- No K05 work started. No APK was created. Bootstrap UI lifecycle remains deferred.
- K04 code/acceptance evidence are in Git; verify final branch/main green lifecycle
  in .local-ci/logs/latest.json and Git history before starting another unit.

## Decisions
- Per-100g source components are explicit RAW/ALREADY_COOKED/AS_DESCRIBED.
  Only RAW accepts documented CookingAdjustment; no coefficients are invented.
- Base servings, prepared servings and consumed share remain separate. Yield is
  documented for the prepared batch; no raw-weight fallback or automatic inheritance.
- Missing required/unioned nutrients remain null in totals and portions with versioned
  ingredient provenance; energy, sugars and sodium/salt are not inferred.
- Calculation retains Double precision; BigDecimal HALF_UP is display-only.
- No schema migration or publication gate changes; no Android dependencies in core.

## Remaining work
- K05 editor/publication/audit flow per current coding task and canonical plan.
- Later customization/plan workflows own persisted provenance, UI editing and daily/
  weekly aggregation. Clinical factors, kitchen yields and photos are not approved.
- APK/device runtime and UI integration remain unverified/deferred.

## Relevant files
- planning/coding-tasks.txt K05; planning/research-data.json (canonical)
- planning/nutrition-science-implementation-plan.txt K05-referenced sections
- android/app/src/main/java/az/qida/app/domain/nutrition/{Quantities,RecipeCalculator}.kt
- android/app/src/test/java/az/qida/app/domain/nutrition/RecipeCalculatorTest.kt
- android/app/src/main/java/az/qida/app/data/local/LibraryImporter.kt
- docs/architecture/android-k04-nutrition.txt; docs/qa/android-k04-acceptance.txt
- android/tools/check_android.py; existing K02/K03 domain/storage docs as needed

## Checks performed
- Development APK-free lint/compilation and 26 JVM tests passed before final
  integration changes. Final mandatory CI evidence is in local logs/Git history.
- Acceptance fixtures cover 2400/1600/280, batch vs consumed portions, missing fields,
  oil loss, retention, raw/cooked boundary, units, overflow and final-only rounding.
- Real library recipes are checked through the core by existing importer tests.
- No visual/device/APK/clinical validation claimed.

## Exact next step
In a new chat, verify K04 green merged-main CI record, read K05 and its specific plan
sections/files, then implement only K05 following local branch/commit/CI/finish flow.
Do not create an APK without explicit request.
