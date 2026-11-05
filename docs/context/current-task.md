# Current task — K12 implemented; next K13

## Completed
- K12 Today actual totals by current IANA timezone and local midnight; empty day
  and unknown nutrients stay unknown. No plan dependency; K16 owns forecasts.
- Remaining energy uses only current persisted K08 specialist assessment plus
  complete energy. G2/G3 remain closed; neutral above-target text.
- Success routes to Today or concrete saved ID; portion/nutrients/actual recipe
  ingredients and committed-day summary. Explicit multiple preview/confirm edits
  immutable snapshot; retains event/date/source/basis and actual batch.
- Confirmed deletion appends tombstone; history/event ledger retained, original
  event retry cannot resurrect it. Latest totals/repeat list omit deletions.
- Implementation 06df3ae; verification fix 1ebe9ea; main abb1c81 passed 98 JVM
  tests. Final acceptance 35968ba merged as d2d44c6 passed 103 JVM tests, lint,
  compilation and all data checks. It includes locale renders and ingredient labels.

## Current state
- K12 code is merged into main and green at d2d44c6. This documentation update
  follows; require final .local-ci/logs/latest.json passed=true and head == main.
- Concurrent work switched the primary checkout to docs/nutrition-plan-design.
  K12 final worktree: /private/tmp/callmood-k12-final, with its own local CI logs
  and reports. Do not switch or commit unrelated primary-checkout work.

## Decisions
- Current user/device zone defines Today; original entry instant/zone retained.
- Empty day is not a precise zero intake. Missing/null/unit-mismatched nutrients
  invalidate that total and remaining energy; known zero is retained.
- Edits keep K11 event identity; append-only revisions use optimistic concurrency.
  Deletion is a backward-compatible optional FoodLog.deleted JSON field.
- Recipes without known gram yield show actual batch share, never invented grams.
- No automatic targets, fake content approvals, remote sync or APK generation.

## Remaining work
- K13 only in a new chat after final K12 green finish.
- Real published-food search-to-save E2E blocked by empty production catalog.
- Device/APK/TalkBack, physical disk-full and clinical approval unverified.

## Relevant files
- domain/diary/TodayDiary.kt; domain/FoodLog.kt
- data/local/FoodLogRepository.kt; ui/today/{TodayScreen,RecordedIngredients}.kt
- ui/BootstrapScreen.kt; ui/entry/{FoodEntryScreen,PortionDraftEditor}.kt
- domain/TodayDiaryTest.kt; data/local/UserPersistenceTest.kt;
  ui/FoodEntryUiTest.kt; res/values*/today.xml
- docs/qa/android-k12-acceptance.txt; planning/coding-tasks.txt
- Canonical plan: planning/research-data.json; derived nutrition-science-implementation-plan.txt

## Checks performed
- Green code main d2d44c6: 103 JVM/Room/Compose tests, lint, Kotlin compile, AZ/EN/RU
  key parity/tokens, 15 library tests and canonical export parity.
- Boundary/DST, missing/unit/zero arithmetic; edit/delete/reopen/conflict/event
  dedupe; manual save/recreation/edit/delete Compose flow.
- Native PNGs inspected for AZ/EN/RU 150% text, Today, edit, empty and delete
  confirmation. Some native English captures omit previously drawn text; earlier
  full English renders and final individual labels were also inspected. This
  renderer limitation is not device visual acceptance.
- Contrast required pairs 4.71–12.70:1. No APK or device tests.

## Exact next step
- Check K12 final matching-head CI (worktree local logs), then K13:
  read coding-tasks K13 and its referenced §6–8/17–18 requirements plus K03/K05
  publication and K07/K08 policy contracts. Do not create an APK.
