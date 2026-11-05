# Current task — K11 complete; next K12

## Completed
- K11 explicit portion preview/confirmation using K10 PortionDraft; grams/kg
  and exact previous-portion multiples. No implicit household conversion.
- Immutable client event and nutrient snapshot saved by existing Room transaction;
  double tap/retry deduplicates. Saved-instance state retains pending payload.
- Only committed local writes show success; failure retains event for retry.
- FoodLog optional backward-compatible display name, source/state, entered
  quantity/unit and repeatedFrom preserve manual/repeat provenance.
- Recipe repeats retain actual ingredients/batch and scale consumedShare plus
  nutrients; exceeding the original batch is rejected. Fresh date/event, no
  copied plan/photo identity. No edits to historical logs or future menus.
- Feature commit 6138c84; merged-main 11d9993 passed 92 JVM tests. Final small
  localization/success-preview fix follows; latest.json must match current HEAD.

## Current state
- Local main after finish; require .local-ci/logs/latest.json passed=true and
  head matching git rev-parse HEAD. Local Git only, no remote operations.

## Decisions
- G2/G3 closed; production catalog remains empty. No invented clinical rules.
- Az/Orta/Çox unavailable until verified recipe weights/photos exist. Current
  supported measured units: grams/kg, or previous portion multiple.
- SyncStatus.LOCAL explicitly means local-only, no remote-sync promise.
- New optional serialized fields need no Room table migration; old payloads
  decode with defaults. Record transaction/event conflict behavior unchanged.
- K12 owns Today totals, edit/delete and navigation to a concrete saved record.

## Remaining work
- Only K12 next, from planning/coding-tasks.txt and scoped main-plan sections.
- Physical disk-full, device restart, APK/emulator/device/TalkBack unverified;
  IO failure is simulated and file-DB reopen is tested.
- Real published content/photo calibration and clinical approval unverified.

## Relevant files
- domain/entry/{FoodEntry,PortionConfirmation}.kt; domain/FoodLog.kt
- ui/entry/{FoodEntryScreen,PortionDraftEditor}.kt
- data/local/FoodLogRepository.kt; UserDatabase.kt
- domain/entry/PortionConfirmationTest.kt; ui/FoodEntryUiTest.kt;
  data/local/UserPersistenceTest.kt; docs/qa/android-k11-acceptance.txt
- planning/research-data.json (canonical); nutrition-science-implementation-plan.txt

## Checks performed
- Merged-main CI: 15 library tests; Python/JSON/export parity; Android lint,
  Kotlin compilation, 92 JVM/Room/Compose tests; AZ/EN/RU keys and tokens.
- Contrast: required pairs 4.71–12.70:1. No new visual tokens.
- Native JVM preview/error/saved PNGs inspected; localized unit correction
  follows initial inspection. Three-language 150% font previews generated.
- Artifacts: .local-ci/reports/k10 (K11 confirmation-* and ready-* PNGs);
  test XML: .local-ci/reports/k10-test-results.
- No APK packaging, installation or device tests.

## Exact next step
- New chat: read this handoff, local workflow and K12 requirements. Confirm final
  matching-head green CI, then implement K12 only. Do not create APK.
