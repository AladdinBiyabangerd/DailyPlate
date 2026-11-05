# Current task — K16 reviewed catalog import + DB recompute

## Completed
- Prior reviewed-content model on main 372c89b passed 206 JVM tests and APK-free CI.
- CatalogRepository.importReviewedCatalogJson imports separate ReviewedCatalogPackage into Library DB.
- Exact ingredient DB versions drive RecipeCalculator before atomic catalog selection insertion.
- Hashed calculation evidence supplies component state/retention/loss/yield; coverage/unit/null/total/cooked-100g checks.
- Immutable complete replacements, monotonic catalog/recipe versions, replay without reactivation, sticky exact-version withdrawal.
- Five Room/JVM tests added, including disk reopen, forged values, changed DB values, yield/retention and failed replacement preservation.

## Current state
- Stored reviewed catalog selection is separate from production menu release. G3/G4 remain closed; productionRecipes/Foods stay empty.
- Existing source foods may remain drafts: recomputation does not confer publication or allergen safety approval.
- PlanningInputImporter/CandidateCatalog still reject test EditorialSnapshot. No adapter/conversion added.
- No real reviewed catalog or authenticated remote publisher added. §30 energy/macro activation unchanged.

## Decisions
- Current reviewed catalog is highest immutable Library DB snapshot; no Room schema change.
- Catalog import is a trusted local boundary; hashes check integrity, not authenticity.
- Old ReviewedRecipePayload without calculation remains readable but cannot pass production import recompute.
- AS_DESCRIBED/cooked components cannot receive cooking factors; raw factors require raw/dry DB source.
- New use must check current withdrawal/expiry; currentReviewedCatalog is audit/integration data, not usable menu candidates.

## Remaining work
- Production reviewed-food/source-manifest/authenticity and allergen/mapping evidence; no promotion of draft food rows.
- Version-compatible reviewed catalog → planning transport/application integration while preserving G3/G4.
- Authoritative content/prices and generation/acceptance/recalculation/detail/Today device E2E.
- K18 separate. No APK authorized or created.

## Relevant files
- data/local/{ReviewedCatalogImporter,ReviewedRecipeRecompute,LibraryDatabase}.kt
- domain/editorial/{ReviewedRecipeContent,ReviewedRecipeCalculation}.kt
- data/local/ReviewedCatalogImportTest.kt (test source set)
- domain/planning/CandidateContracts.kt; data/local/PlanningInputImporter.kt
- docs/qa/android-k16-generation-acceptance.md
- planning/research-data.json §3–4/8/21–23/25/30; planning/coding-tasks.txt K16

## Checks performed
- Read lifecycle, context, organization, relevant canonical requirements and binding nutrition design.
- Final merged-main mandatory CI: consult .local-ci/logs/latest.json. No branch/manual CI, APK or device checks.

## Exact next step
- Confirm latest merged-main CI, then integrate stored reviewed catalog with a version-compatible planning transport; keep source-food safety/publication evidence and closed G3/G4 explicit.
