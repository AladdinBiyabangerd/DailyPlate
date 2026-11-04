# Current task — K02 domain contracts and local Room persistence

## Completed
- K01 foundation and APK-free CI maintenance merged to main before K02.
- K02 serializable domain contracts: Food, Recipe, UserProfile, NutritionPolicy,
  energy calculation provenance, MealPlan/Item, FoodLog, RecipeCustomization,
  PlanChange, PhotoMealDraft, EvidenceCard and §19 supporting personal records.
- Separate Room library/user databases, read-only catalog repository and typed
  personal repositories; immutable revisions, event deduplication and transactional
  optimistic plan/shopping writes. No library-builder user-data input was added.
- Exported library v1 and user v1/v2 schemas; explicit non-destructive v1→v2 migration.
- 13 JVM contract/Room tests, including nullable values, timestamps, migration,
  file reopen, concurrent writers, owner isolation and SQL-triggered rollback.
- Architecture/contract decisions: docs/architecture/android-k02-storage.txt.

## Current state
- K02 implementation and acceptance tests pass. Final branch/main lifecycle is
  recorded in .local-ci/logs/latest.json and Git history; verify those records.
- CI runs APK-free lintDebug, compileDebugKotlin, testDebugUnitTest and tokens;
  runner requires all five K02 suites to execute with no failures/errors/skips.
- Bootstrap UI still has no onboarding or DB lifecycle wiring; K03–K34 not started.
- Canonical product plan remains research-data.json 1.7; library has 500 foods and
  224 unpublished recipe drafts. K02 has not imported or approved those drafts.

## Decisions
- Android first: minSdk 24, compileSdk/targetSdk 36. iOS remains deferred.
- Kotlin/Compose/serialization plugin 2.4.10, KSP 2.3.13, Room 2.8.5,
  serialization 1.12.0, Robolectric 4.17. Dependencies updated to satisfy strict lint.
- java.time desugared with desugar_jdk_libs 2.1.5 for API 24/25.
- Domain has no Android/Room/UI imports. JSON aggregate snapshots deliberately
  preserve exact versions and nulls; future query columns require migrations.
- SQL schema version and JSON payload schema are separate. Unknown personal
  payload schema fails explicitly; no destructive fallback or silent overwrite.
- Repositories do synchronous IO on worker threads; production Room main-thread
  protection stays enabled. First consuming feature will own lifecycle wiring.
- Clinical calculations, menu validation, library publication gates, remote sync
  and store verification are later tasks; structural models do not implement them.
- APK creation/device tests only on explicit user request. JVM tests use API 34
  fixture, resolved before runtime; Robolectric executes offline without APKs.

## Remaining work
- K03: current JSON/SQLite library adapter, version/status gates, idempotent and
  atomic imports/updates; preserve old FoodLog snapshots. G3 remains closed.
- API 24/36 device runtime and APK packaging after dependency updates unverified;
  no APK was created for K02. No visual changes or new screenshots in this unit.
- Later units own actual calculation, customization workflows and UI integration.

## Relevant files
- planning/coding-tasks.txt K03; planning/research-data.json (canonical)
- planning/nutrition-science-implementation-plan.txt §3–4, §11, §15 for K03
- library/README.md, library/manifest.json, library/foods.json, library/recipes.json
- library/tools/ (existing builder/validator; no user data input)
- android/app/src/main/java/az/qida/app/domain/
- android/app/src/main/java/az/qida/app/data/local/
- android/app/src/test/java/az/qida/app/{domain,data/local}/
- android/app/schemas/; android/tools/check_android.py
- android/{build.gradle.kts,settings.gradle.kts,app/build.gradle.kts,README.txt}
- docs/architecture/android-k02-storage.txt; docs/qa/android-k02-acceptance.txt
- .local-ci/README.md, workflow.py and checks.py (ignored local tooling)

## Checks performed
- Kotlin/Java compilation and strict lintDebug passed with final dependencies.
- 13 JVM tests passed: 5 personal persistence, 3 plan transaction, 2 migration,
  1 catalog isolation, 2 domain validation; no failures/errors/skips.
- APK-free/offline runner and mandatory branch/main CI: consult exact commit and
  passing records in .local-ci/logs/latest.json; no remote/push/deployment authorized.
- Existing APK timestamps remain from K01 (4 November 2026, 12:46 Asia/Baku).
- Historical K01 device/visual evidence remains docs/qa/android-k01-acceptance.txt;
  it does not verify the K02 dependency/runtime changes.

## Exact next step
In a new chat, verify K02 is merged with green main CI and read K03 in
planning/coding-tasks.txt, library/README.md, product plan §3–4/§11/§15 and only
needed adapter/domain files. Implement K03 import/content gates with real Room
integration tests; keep G3 closed for unpublished drafts, preserve old log snapshots,
complete AGENTS.md branch/commit/CI/finish flow without APK creation. Do not start K04.
