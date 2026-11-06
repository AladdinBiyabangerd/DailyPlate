# Native tablet implementation: technical evidence and pending review

## Scope and provenance

- Branch `feat/onboarding-tablet-ui`; isolated worktree
  `/Users/mac/My Workspace/callmood-tablet-ui`; base
  `87e054b3a9ed6bb030a6e2dc6dd3c09aa73d14ea`.
- Approved tablet design snapshot: `99fa3db7136d556d4df75ad86bf9e45d3b5e66ec`.
  No prototype, original PNG, transparent asset, backend/domain/release file was changed.
- All 11 groups, including both Health pages/warning, Activity help, allergy/detail/error
  variants, checklist return, simulated account/privacy, Ready answers/menu/diary and
  revised Today/navigation shell are implemented with native Compose controls.
- No merge, push, workflow finish/review-visual, APK, emulator or device test.
  These are local feature-branch checks explicitly requested for this implementation,
  not a post-merge CI completion claim.

## Technical checks

Final-source `python3 android/tools/check_android.py` (log 887381) completed
successfully at **15:44:13 Asia/Baku, 2026-11-06**, exit 0: Kotlin compilation,
Android lint and **241 JVM tests across 51 suites, zero failures/errors/skips**;
524 resource keys per AZ/EN/RU locale and all token/contrast checks passed.
No APK was generated. Visual-gate and parent-target workflow regression tests also
passed in disposable test repositories; no project branches were merged by them.

The suite checks 800x1280 portrait and 1280x800 landscape at mdpi, AZ/EN/RU and
100/150% text, top states and scroll-to-CTA positions. Six tablet interaction tests
cover connected offline navigation, provider/privacy/future-shell simulations,
validation/checklist return, health risk/help/diary, rotation/recreation retaining
decimal drafts, 900x650 constrained landscape and 600x900 compact-tablet reflow.
Actual line glyph widths and paragraph heights are asserted; overflow files must
remain empty. No clipping assertion, timeout, lint or visual gate was suppressed.

The inherited phone suite also renders 320x700, 390x844 and 430x932, including
150% fonts and locale variants. Independent box-side SHA-256 comparison of all
492 current phone PNGs against the preserved parent-merge renders found 492/492
byte-identical, no missing/extra files. This is render regression evidence, not a
claim of device verification or prior visual acceptance.

Debug merged-manifest inspection and Firebase-empty assertions verify the demo
uses `android.app.Application`, with FirebaseInitProvider absent. No new service,
authentication, network, AI, calculation, purchase or sync path was introduced.
Release integration is unchanged; all 27 supplied transparent resources remain
byte-identical to their approved counterparts.

## Visual mapping versus acceptance

The unmodified fail-closed `validate_plan` checks all 19 newly changed protected
inputs against 2,520 existing+new render cases. Native tablet coverage is 1,356
PNGs: 768 top-state variants and 588 scroll/CTA variants. All 223 tracked tablet
design inputs are mapped; none is exempted as native acceptance.

The inherited tablet design commit contains 11 plural-output/draft proposal
records incompatible with the gate's single-output exemption schema. Their full
original content is preserved verbatim in `tablet_design_proposal_history`; every
historical source is instead mapped to native surfaces. This corrects registry
schema/coverage rather than relaxing the gate. The other 69 proposal records and
all existing surfaces remain intact. No gate implementation or shared hook changed.

There remain **218 inherited unmapped original-style design inputs** from the
phone-parent merge manifest. They are not waived or silently accepted. These are
separate from the fully mapped tablet additions. No hash-bound acceptance record
was created; the feature remains **pending visual review**, not accepted/completed
under the repository's mandatory visual gate.

## Reference comparisons and known differences

Latest-source evidence is the full 1,356 PNG matrix in this Mac worktree's
`android/app/build/reports/onboarding-tablet/`. These are **Robolectric native
renders, NOT device screenshots**. The earlier box-side paired overviews,
all-64-state contact sheets and localized samples were inspected, then archived
under `/workspace/dailyplate-tablet-ui/review-887379-before-final-refinements/`
because they predate the final tablet-only refinements. Do not send them as latest.
Fresh final-source transfer/comparison is outstanding; `final/STATUS.json` on the
box documents that limitation. Technical pass does not imply visual acceptance.

Initial comparison prompted real fixes: tablet-only body text weight approximates
the approved rounded-bold face; Body validation now precedes the yellow privacy
strip; meal-slot labels use the approved pale pill; landscape preference card
backgrounds stretch together without intrinsic/fixed heights clipping large text.
Other earlier fixes include preference choice text clipping and the Ready emoji
column. Phone drawing defaults were preserved.

Remaining differences/limits are not an acceptance claim:

- No licensed rounded font file was supplied. Existing Android/system bold type
  approximates the Mac rounded face; glyph shape, metrics and some wrapping differ.
- Native shadows, radial background and transparent-art rendering differ from the
  browser reference. Exact supplied alpha assets replace the reference's visible
  matte/low-resolution variants without modifying originals or proportions.
- Robolectric has no real status/navigation chrome or IME; native content can appear
  higher than the mock-status-bar references. Real system insets are supported but
  actual device keyboard, TalkBack, cutouts and GPU rendering remain unverified.
- At 150% text, content grows/scrolls instead of matching a fixed reference canvas.
  Narrow tablet landscape uses approved stacked composition below 1000dp width.
- Prototype next/guest placeholders now navigate to implemented screens. Provider,
  privacy and future tabs remain explicit simulations, not backend integrations.
- Ready's prototype empty-preferences fixture shows a sample menu; connected native
  missing-answer state deliberately keeps the existing safe no-target diary route.
- QIDA wording is inherited design terminology; application/package identity remains
  DailyPlate / `com.aladdin.dailyplate`.

Only the earlier representative/all-state contact-sheet comparisons were inspected, not every
localized/font/scroll image individually. Full human review and any requested
visual refinements remain outstanding. Do not auto-generate acceptance/baselines.

## Interrupted-run and cleanup record

The append-while-iterating QA registry job (recorded PID 10908) and interrupted
Android run ended with the Mac reboot at 15:18:25 Asia/Baku. Both recorded PIDs
were absent after reconnect; no unrelated/reused PID was killed. Registry expansion
was repaired using an immutable tuple, separate `extra_cases`, bounded counts and
atomic replacement. The pre-expansion backup remains Git-excluded under `.local-ci/`.
The interrupted run is not reported as a pass. One earlier complete attempt had an
unchanged FoodEntryUiTest timeout; subsequent full runs passed unchanged. Another
run passed all 241 tests but failed Compose ModifierParameter lint; the signature
was corrected rather than suppressed.

## Next step

The user has now explicitly requested a local merge into `feat/onboarding-original-style`,
preserving all current target work. First audit target cleanliness and concurrency; stop
on dirty/untracked work, conflict or changed target state. Use only the sanctioned
parent-target workflow, with no fabricated visual acceptance. Preserve
inherited pending debt and main/tablet worktrees. Android Studio opens the
`callmood-tablet-ui/android` subdirectory; see `onboarding-tablet.md`.
