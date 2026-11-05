# Current task — NP-16 review and symptom suspension

## Completed
- Prior calculation activation is merged at 20473a9; matching main CI passed.
- Added recent voluntary-weight consumer, 14-day review, editor prefill with
  explicit measurement reconfirmation, and immediate target suspension reports.
- Suspension is persisted in append-only profile/assessment revisions, blocks
  automatic and specialist active targets, survives ordinary edits and restart.

## Current state
- Implementation ready for mandatory finish; final merged-main CI is authoritative.

## Decisions
- Last 14 local calendar days including today; at least 3 measurements, no future
  instants, duplicate instants or superseded revisions. No calorie titration.
- No invented clinical clearance/resume flow; professional support guidance.
- No catalog/menu activation, remote actions or APK creation.

## Remaining work
- Confirm matching-head merged-main CI and inspect generated NP-16 PNGs.
- Device/pilot and broader weight-entry/progress milestones remain unverified.

## Relevant files
- domain/profile/NutritionReview.kt; domain/Profile.kt
- ui/profile/NutritionReviewCard.kt, ProfileScreen.kt, ProfileEditor.kt
- docs/qa/android-np16-acceptance.md
- planning/nutrition-science-implementation-plan.txt §27 NP-16
- Kotlin paths above are under android/app/src/main/java/com/aladdin/dailyplate/.

## Checks performed
- git diff --check. Domain, Room and localized UI acceptance added to post-merge CI.
- No branch CI, APK or device checks.

## Exact next step
- Finish this branch, repair any main CI failures, inspect screenshots; update this
  note with final evidence. Future large milestones belong in a new chat.
