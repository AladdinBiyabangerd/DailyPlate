# Current task — K10 implementation; next unit K11

## Completed
- K10 multilingual name/alias search, exact food version/state/source selection,
  clear query, loading/empty/error/retry, manual escape and previous-entry list.
- Manual name, positive g/kg quantity, optional source-backed per-100g nutrients;
  decimal comma accepted; missing nutrients stay null, explicit zero stays zero.
- PortionDraft is a serializable K11 input: name, new consumption instant/zone,
  method, food version, state, source, quantity/unit and nutrient basis.
- Repeat retains the immutable FoodLog snapshot as provenance, uses new time and
  an editable previous-portion multiple. No new log/event/plan identity is copied.
- Manual form and selected draft use saved-instance state. No K10 FoodLog write.
- Feature commit 8830139; resource fix 9e7ed25; verification/history f91d341;
  locale/recreation fix 8d75888; final navigation helper fix follows these.

## Current state
- Local main after finish. Authoritative final result is .local-ci/logs/latest.json:
  require passed=true and head equal to git rev-parse HEAD before starting K11.
- No unrelated user changes were present at start. Local Git only; no remote.

## Decisions
- CatalogRepository.productionFoods remains empty while G3 is closed; do not
  expose draft food/recipe fixtures as real search candidates.
- Search accepts all three names and aliases; translated display preserves IDs.
- Repeat uses the stored nutrient/recipe snapshot, never recalculates old logs
  against a changed catalog. Full previous snapshot is provenance, not the new
  record's consumption date/client event/plan item/photo identity.
- PREVIOUS_PORTION is a multiple of the original portion, including recipe-only
  records without known grams. K11 must preserve/scale actual recipe semantics.
- No implicit item/spoon to grams conversion. Manual nutrients are per 100g.
- K11 owns durable confirmation/recording and any schema extension needed to
  retain manual display names. Historical records without a catalog title show
  a localized fallback with log ID; do not infer a name from a newer catalog.
- G2/G3 remain closed. No clinical thresholds, prices or approvals added.

## Remaining work
- K11 only next: confirm portion and write durable, idempotent FoodLog; inspect
  its task requirements and plan references before editing.
- APK/device/emulator/TalkBack and real published-content acceptance unverified.
- Existing G2 approval and K16 real menu recalculation/application remain future.

## Relevant files
- domain/entry/FoodEntry.kt; ui/entry/{FoodEntryScreen,ManualFoodForm,PortionDraftEditor}.kt
- data/local/{FoodLogRepository,UserDatabase,LibraryDatabase}.kt; domain/FoodLog.kt
- ui/BootstrapScreen.kt; res/values*/food_entry.xml
- tests: domain/entry/FoodEntryTest; ui/FoodEntryUiTest;
  data/local/UserPersistenceTest recent-entry regression
- docs/qa/android-k10-acceptance.txt; planning/coding-tasks.txt
- Canonical plan: planning/research-data.json; derived TXT §4/8/11–12/18–19 for K11

## Checks performed
- Post-merge CI only: library 15-test suite, Python/JSON/canonical export parity,
  lintDebug, compileDebugKotlin, JVM/Room/Compose suites and token/language checks.
- Last test collection: 89 tests; final status is the matching-head CI log above.
- Source fixes addressed explicit AZ resources, actual manifest Activity for
  native UI tests, Compose LocalConfiguration reads, and navigation test scrolling.
- Inspected native JVM search/manual/portion/draft-ready PNGs in AZ/EN/RU,
  including actual 150% font density. Long text wraps; forms scroll.
- Palette required pairs measure 4.71–12.70:1; AZ/EN/RU resource keys match.
- Artifacts: .local-ci/reports/k10; XML: .local-ci/reports/k10-test-results.
- No APK packaging/install/device checks or remote operations.

## Exact next step
- In a new chat, implement only K11 from coding-tasks.txt, using PortionDraft.
  Read this handoff, required workflow/organization rules and scoped plan first.
- Check final latest.json against HEAD; if red, repair K10 before starting K11.
