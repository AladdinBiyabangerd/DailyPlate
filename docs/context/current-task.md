# Current task — K03 library import and content gates

## Completed
- K02 merged green before this unit; K03 adds JSON/read-only SQLite adapters for
  current 500 food / 224 recipe package, typed Room mapping and original source rows.
- Atomic immutable/idempotent imports, monotonic package revisions, source/status
  validation, pinned ingredient refs and draft-only G3 production boundary.
- Three real-package Room tests cover repeat JSON/SQLite parity, update/history,
  corruption/conflict/downgrade rejection and SQL-triggered transaction rollback.
- CI requires the new suite plus the existing five JVM suites.

## Current state
- K03 code and acceptance evidence are in Git; consult latest local CI record and
  branch/main history for final lifecycle outcome. No APK was created.
- G3 remains closed. All current food/recipe records are editorial drafts.
- Bootstrap UI still has no catalog lifecycle wiring; no K04 implementation started.

## Decisions
- Package semver maps to Long major*1000000+minor*1000+patch+1 (0.3.0 = 3001).
- Source dataset version remains separate. Original JSON retains lineage, servings,
  gates, inferred allergens and other fields outside K02 typed contracts.
- Current-format adapter rejects publication/live flags and unsupported source/status;
  future reviewed publication requires a separate complete release-chain adapter.
- SQLite contains only version/date metadata, so caller supplies manifest JSON;
  SQLite version must match it. User data never enters the library importer.
- No new DB tables/schema migration; immutable rows use existing library snapshots.

## Remaining work
- K04 nutrient/portion calculation core per coding-tasks and current canonical plan.
- APK/device runtime, UI lifecycle and content approvals remain unverified/deferred.

## Relevant files
- planning/coding-tasks.txt K04; planning/research-data.json (canonical)
- planning/nutrition-science-implementation-plan.txt §3–4/§12
- planning/meal-customization-requirements.txt §1–2 for K04
- android/app/src/main/java/az/qida/app/data/local/Library{Database,Importer}.kt
- android/app/src/main/java/az/qida/app/domain/{Catalog,Values}.kt
- android/app/src/test/java/az/qida/app/data/local/LibraryImportTest.kt
- docs/architecture/android-k03-library-import.txt
- docs/qa/android-k03-acceptance.txt; android/tools/check_android.py

## Checks performed
- New three Room integration tests passed during development; strict lint cursor
  closure findings corrected. Final full CI outcome is recorded in local logs/history.
- Mandatory CI covers library validator, canonical export parity, APK-free Android
  lint/compilation/JVM tests and tokens. No visual or device check claimed.

## Exact next step
Verify K03 merged main CI record, then start K04 in a new chat. Read only the K04
plan sections and referenced domain/import/test files, follow local CI lifecycle,
and do not create an APK without explicit request.
