# Current task — design gap package

## Completed
Existing onboarding and ingredient-error concepts included with completion 19–28. Added 29: extra actual intake form, explicit preview/confirmation and saved result (3 panels). Updated full ZIP and manifest.

## Current state
Draft AZ design delivery; no native or user acceptance asserted. Existing shared worktree edits preserved via isolated worktree /private/tmp/callmood-design-package.

## Decisions
Extra intake is a separate actual record, never a second copy of the original plan portion. Synthetic nutrients; no fabricated cooked grams. D02 approval remains authoritative for native portion bounds.

## Remaining work
Native implementation, AZ/EN/RU 100/150%, device/TalkBack/backend checks; inherited native visual acceptance remains open. 30 now adds age-format, nonfinite-height and negative-weight error concepts; exhaustive input permutations remain unverified.

## Relevant files
design-concepts/simple-turquoise/completion/29-elave-faktiki-porsiya.{png,html}; completion/screens.json; design-concepts/qida-turquoise-designs-complete.zip.

## Checks performed
Opened new three-panel PNG, D02 reference, onboarding and ingredient-error sheets. ZIP content/hash verification recorded in package manifest. Merge CI result to be reported separately.

## Exact next step
Review draft PNGs; resolve inherited native gate separately before claiming application completion.

## 30 full-form alignment — 6 November
- Reworked all three 30 panels to the existing full onboarding step 2/5; age/height/weight remain together, errors directly under the relevant field, confirmation/optional sex/disabled Continue retained. Simplified technical text.
- Rendered and opened PNG; compared with 19 onboarding reference. No visible clipping; contrast 12.70/5.04/4.71 passed. Package and source hashes refreshed.
- No native code or APK changes. Inherited 756-case native review remains separate and pending.
- Next: inspect post-merge CI outcome; native acceptance requires actual comparison of every current manifest case before review-visual.
