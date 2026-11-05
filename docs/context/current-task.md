# Current task — NP-16 review and symptom suspension

## Completed
- Prior calculation activation is merged at 20473a9; matching main CI passed.
- Added recent voluntary-weight consumer, 14-day review, editor prefill with
  explicit measurement reconfirmation, and immediate target suspension reports.
- Suspension is persisted in append-only profile/assessment revisions, blocks
  automatic and specialist active targets, survives ordinary edits and restart.

## Current state
- Implementation merged at 47cb07dfc944; matching main CI passed all 151 JVM tests,
  lint, compilation, token/contrast, data contracts and export parity.
- This completion-note commit uses the same mandatory finish lifecycle; latest.json
  matching current main remains the authoritative final check.

## Decisions
- Last 14 local calendar days including today; at least 3 measurements, no future
  instants, duplicate instants or superseded revisions. No calorie titration.
- No invented clinical clearance/resume flow; professional support guidance.
- No catalog/menu activation, remote actions or APK creation.

## Remaining work
- No remaining NP-16 work in this scoped unit.
- Device/pilot and broader weight-entry/progress milestones remain unverified.

## Relevant files
- domain/profile/NutritionReview.kt; domain/Profile.kt
- ui/profile/NutritionReviewCard.kt, ProfileScreen.kt, ProfileEditor.kt
- docs/qa/android-np16-acceptance.md
- planning/nutrition-science-implementation-plan.txt §27 NP-16
- Kotlin paths above are under android/app/src/main/java/com/aladdin/dailyplate/.

## Checks performed
- git diff --check. Post-merge domain/Room tests passed; APK-free compilation and
  lint passed. UI test caught retained scroll between locales; fixed by scrolling
  the asserted review label into view. AZ/EN/RU review/suspended PNGs visually
  inspected at 1.5x font; no clipping in the visible text/buttons.
- Explicit values-az locale parity repaired after first CI failure.
- Final repair CI passed 151 JVM tests with no failures/errors/skips.
- PNG evidence: .local-ci/np16-screenshots/np16-{review,suspended}-*-large.png.
- No branch CI, APK or device checks.

## Exact next step
- In a new chat, read this note and planning/coding-tasks.txt, verify the current
  milestone acceptance records before choosing the next menu/progress unit.
  This unit does not implement broader weight-entry UI or menu generation.
