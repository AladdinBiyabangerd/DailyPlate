# Current task — K16 planning input transport

## Completed
- Previous recipe detail/portion-confirmed intake retained; previous main 43c3167 passed 197 JVM tests and APK-free CI.
- Added complete schema-versioned planning input package transport through UserRepositories.planningImports.
- Atomic active-revision + receipt write, monotonic source revisions, exact replay without reactivation, owner isolation and fixture rejection.

## Current state
- This unit is a trusted local transport API, not automatic library/application integration or production approval.
- All current EditorialSnapshot objects are testOnly; production import rejects them. Empty/unavailable catalogs are supported; successful real catalog import requires a separate reviewed representation.
- G3/G4 remain closed. Current energy/macro activation and reported specialist approval follow canonical §30; do not describe absence of specialist as the current blocker.

## Decisions
- Complete replacement includes policy/catalog/evidence/coverage/prices/freshness; no mixing with older selection.
- Package ref uses planning-import; same source version + changed payload conflicts. Old receipt replay never restores old active inputs.
- User DB transaction owns activation + receipt; library DB is outside this API. No authenticity/signature claim.

## Remaining work
- Production reviewed catalog representation and library import/activation orchestration with application invocation.
- Authoritative G3 catalog/G4 prices and successful generation/acceptance/recalculation/detail/Today device E2E.
- K18 remains separate. No APK authorized.

## Relevant files
- data/local/{PlanningInputImporter,PlanGenerationRepository,UserRepositories}.kt
- data/local/PlanningInputImporterTest.kt (test source set)
- domain/editorial/EditorialContent.kt; domain/planning/RecipeCandidateFilter.kt
- docs/qa/android-k16-generation-acceptance.md
- planning/research-data.json §8/22/25/30; planning/coding-tasks.txt K16

## Checks performed
- Read lifecycle/organization/current context and scoped plan/design contracts; inspected library importer and production gates.
- Added five Room/JVM tests, including disk reopen and transactional rollback. Mandatory post-merge CI outcome: consult .local-ci/logs/latest.json; no branch/manual CI and no APK.

## Exact next step
- Confirm latest merged-main CI, then design a production reviewed-content representation without promoting test EditorialSnapshot or bypassing G3/G4. Automatic application wiring remains unimplemented.
