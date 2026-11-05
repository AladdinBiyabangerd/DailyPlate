# Current task — K09 profile changes and target explanation

## Completed
- Shared K07 form controls now drive an isolated profile editor with body/goal/
  preferences, specialist target, age confirmation, reason and before/after preview.
- Atomic profile + assessment + K16 request append; cancel has no writes, retries
  are idempotent, stale edits conflict, historical logs/plans/assessments stay frozen.
- Profile exposes “Nəyə əsasən?” history with dates, inputs, formula/policy metadata,
  readable reasons and explicit general/specialist/test-only limitations.
- FuturePlanPreview/Acceptance defines separate exact-revision confirmation;
  actual menu calculation/application remains K16. G2 stays closed.

## Current state
- Task branch feat/k09-profile-revisions; merge/CI evidence must be checked in
  git log and .local-ci/logs/latest.json (main HEAD, passed=true).
- User AGENTS.md change is separately stashed as user-agents-k09; restore after finish.

## Decisions
- Reuse onboarding validation without writing to the onboarding draft.
- Keep age-as-of unchanged unless explicitly confirmed; never guess a birthday.
- Persist nullable budget currency even without an amount; unknown legacy currency
  is entered by the user. No prices, clinical rules or activation invented.
- Profile save does not apply a future menu; pending K16 request says so explicitly.

## Remaining work
- K10 next. K16 real recalculation/delta/confirmation integration; G2 clinical approval.
- Device/APK/TalkBack remain unverified. Native JVM render inspection follows CI.

## Relevant files
- domain/profile/{ProfileEdit,FuturePlanPreview}.kt; domain/Profile.kt
- data/local/{ProfileEditRepository,UserRepositories,UserSnapshotRepository}.kt
- ui/profile/*.kt; ui/onboarding/ProfileAnswerFields.kt; res/values*/profile.xml
- src/test/.../{domain/ProfileEditTest,data/local/ProfileEditPersistenceTest,ui/ProfileUiTest}.kt
- docs/qa/android-k09-acceptance.txt; planning/coding-tasks.txt
- planning/research-data.json; nutrition-science-implementation-plan.txt §3/18–19 for K10

## Checks performed
- Source review and git diff --check. Mandatory CI executes only after merge to main;
  consult latest.json for actual result. No branch CI or APK creation.

## Exact next step
- Finish K09 merged-main CI and inspect retained native PNGs, then restore AGENTS.md.
- New chat: execute K10 using the queue continuation prompt; do not start K11.
