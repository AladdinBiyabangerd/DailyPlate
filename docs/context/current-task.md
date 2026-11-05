# Current task — K09 completed; next unit K10

## Completed
- K09: isolated profile/body/goal/preference editor, specialist target, explicit age
  confirmation, reason, before/after preview and cancel/confirm flow.
- Atomic append of profile + assessment + pending K16 request; idempotent retry,
  stale-edit conflict, write-failure rollback and legacy profiles without assessments.
- “Nəyə əsasən?” history exposes saved inputs/date/timezone, formula/policy versions
  where present, S6 link for Mifflin v1, readable general/specialist/test-only reasons.
- FuturePlanPreview/Acceptance binds exact revisions and preserves past/consumed
  items, dates/slots and log references. Real menu computation/application is K16.
- Feature commit 77ddc7d; fixes b3ec08a, 7e4d158, 81ae53a, cd85fb7.
  Final acceptance/timestamp commit: 3356617.

## Current state
- Local main; final merge/CI truth is .local-ci/logs/latest.json: passed=true and
  head must match git rev-parse HEAD. Last verified implementation main: 1fdd3ac.
- User's unrelated AGENTS.md edit is preserved separately and restored after the
  final finish; never include it in task commits. No remote/push/deployment.

## Decisions
- Reuse K07 controls/validation without writing edits to the onboarding draft.
- Preserve age-as-of unless explicitly confirmed; never guess birthday/age.
- Store nullable currency without requiring a budget amount; legacy unknown
  currency is entered by the user. Preserve fields outside this editor.
- Profile confirmation leaves existing menus and past logs unchanged; the K16
  request remains pending, and a computed menu needs separate preview/acceptance.
- G2 stays closed; no clinical coefficients, thresholds, prices or approvals invented.

## Remaining work
- K10 next. K16 real recalculation/delta/application integration. G2 approval.
- APK/device/emulator/TalkBack and clinical acceptance unverified. Formula-source
  link interaction and test-only calculation UI are not device-tested.

## Relevant files
- domain/profile/{ProfileEdit,FuturePlanPreview}.kt; domain/Profile.kt
- data/local/{ProfileEditRepository,UserRepositories,UserSnapshotRepository}.kt
- ui/profile/*.kt; ui/onboarding/ProfileAnswerFields.kt; res/values*/profile.xml
- tests: domain/ProfileEditTest, data/local/ProfileEditPersistenceTest, ui/ProfileUiTest
- docs/qa/android-k09-acceptance.txt; planning/coding-tasks.txt
- K10 sources: planning/research-data.json; nutrition-science-implementation-plan.txt §3/18–19
- K10 reuse: data/local/LibraryDatabase.kt (CatalogRepository) and domain/nutrition; inspect scoped files.

## Checks performed
- Implementation main 1fdd3ac: 80 JVM/Room/Compose tests (no failure/error/skip),
  lintDebug, compileDebugKotlin; 15 library tests, Python/JSON/export parity.
- AZ/EN/RU string parity; palette contrast 4.71–12.70:1 (all required pairs >=4.5).
- Inspected native JVM PNGs: profile/editor/editor-bottom/explanation AZ/EN/RU
  at 100%/150%, plus AZ changed-input preview and saved-history flow. They wrap
  and scroll; this does not establish physical-device accessibility acceptance.
- Artifacts: .local-ci/reports/k09; retained XML: .local-ci/reports/k09-test-results.
- Final timestamp display uses local zone and minute precision to avoid splitting
  raw ISO fractions; inspected the final AZ/EN/RU 150% explanation renders.
- No branch CI, APK packaging, installation, device tests or remote operations.

## Exact next step
- In a new chat, implement only K10 using planning/coding-tasks.txt's continuation
  prompt. Read this handoff first. Keep K03/K04 contracts, G3 gates and ui-craft.
- K11 owns actual food-log save/confirmation; do not silently implement it in K10.
