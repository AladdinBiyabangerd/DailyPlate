# Current task — K16 production reviewed-content model

## Completed
- Previous planning input transport on main 750eb0a passed mandatory APK-free CI.
- Added separate schema-1 ReviewedRecipeContent / ReviewedRecipePayload; no conversion from test EditorialSnapshot.
- Exact UTF-8 payload SHA-256 + recipe version binding, publication identity/time, evidence completeness, chronology, optional review expiry and withdrawal checks.
- Added four JVM contract tests; QA requirements and integration limits documented.

## Current state
- Model only: CandidateCatalog still uses test-only EditorialSnapshot; production import still rejects it.
- No real reviewed recipes, approval evidence, source authentication or automatic library/application integration supplied.
- G3/G4 remain closed. §30 reported specialist plan approval and current energy/macro activation remain unchanged.

## Decisions
- Publisher preserves exact JSON payload bytes for hashing; hash is integrity, not authenticity.
- Production record uses published Recipe + real Review + EditorialMetadata, independently of K05 draft-envelope workflow.
- Withdrawal blocks new use while retaining historical payload; importing boundary must choose latest state.
- DB nutrient recompute, source manifest validation and atomic catalog activation remain separate required integration work.

## Remaining work
- Integrate reviewed records into production catalog/library import and planning transport with schema compatibility.
- Authoritative G3 content/G4 prices; application invocation and successful generation/acceptance/recalculation/detail/Today device E2E.
- K18 separate. No APK authorized or created.

## Relevant files
- domain/editorial/ReviewedRecipeContent.kt; corresponding test source ReviewedRecipeContentTest.kt
- domain/editorial/{EditorialContent,PublicationRequirements}.kt
- domain/planning/CandidateContracts.kt; data/local/PlanningInputImporter.kt
- docs/qa/android-k16-generation-acceptance.md
- planning/research-data.json §8/21/22/25/30; planning/coding-tasks.txt K16

## Checks performed
- Read lifecycle/organization/context and relevant canonical requirements; inspected existing editorial/publication/import boundaries.
- Final mandatory merged-main CI result: consult .local-ci/logs/latest.json. No branch/manual CI; no APK/device checks.

## Exact next step
- Check latest main CI; integrate the separate reviewed model into a version-compatible production catalog transport without converting test snapshots or bypassing G3/G4, including DB recomputation before activation.
