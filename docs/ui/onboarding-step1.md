# DailyPlate: approved onboarding UI — step 1

## Scope and source

User request: implement eleven groups from `feat/onboarding-original-style`, using
`7096a04` (approved prototype/revised Today) and `7f3e5c6` (transparent art).
Authoritative directory: `design-concepts/simple-turquoise/onboarding-original-style`.
Its HTML/CSS/JS and review-plan define behavior; duplicated render file suffixes do
not necessarily represent distinct states. Old v4/unified boards are not substituted.
All 27 native `dp_art_*.png` files are byte-identical to the transparent manifest;
RGBA dimensions/proportions are retained with ContentScale.Fit, without matte boxes.
Original design files remain untouched.

## Run without backend

Open `/Users/mac/My Workspace/callmood-onboarding-ui/android` in Android Studio.
Choose **app / debug / Default Activity**. The debug manifest selects
`OnboardingPreviewActivity`, uses a plain Application and removes FirebaseInitProvider.
It neither constructs RemoteConfigRepository nor initializes Firebase. No config
file is needed for this preview. Package/identity stay `com.aladdin.dailyplate` /
DailyPlate. Release startup, existing MainActivity, domain/data and Firebase code
are preserved, NOT connected to this demo. Release continues to use the existing
launcher until a separate integration decision. Do not publish this UI-only build.

No network/auth/sync/AI/purchases or nutrition/eligibility algorithms are called.
Only UI validation and a labelled synthetic example (1 900 kcal) exist. Starting
or viewing the plan never records consumed food. Diary/unknown/risk paths hide the
energy and fixture menu. Account offer is Google-only, explicit simulation; no
credentials are requested. Other bottom tabs/actions open next-stage explanations.

## Implemented groups

1. Welcome, language selection (AZ/EN/RU), existing-account explanation.
2. Four single-choice goals, disabled Continue without a choice.
3. Blank age/height/weight, decimal comma, direct entry and +/- controls; optional
   sex, explicit no-target explanation, invalid/blank feedback.
4. Four activity choices, source-bound scrollable help, uncertain/no-target path.
5. Ten explicit health questions across two pages, missing feedback, skip dialog,
   any Yes -> non-diagnostic warning; answer review preserves the draft.
6. Allergy unanswered/yes/no; six illustrated choices; required nut/other details;
   No clears allergen details but not separate gluten; unknown warning/diary path.
7. Time 15/30/60, budget preference, 3/4/5 meals and snack slots; no defaults.
8. Dynamic missing checklist, correct jump-to-group and return, recheck; ready
   screen Change choices also exposes complete groups for editing and answer review.
9. Account offer, skip, simulated Google and truthful local UI-state privacy.
10. Ready summary uses selected goal/count; labelled sample or diary-only, Start.
11. Revised Today with illustrated turquoise hero, white add, energy/meal circles,
    no-consumption disclaimer; five-section shell with explicit scope placeholders.

`PreviewState` is UI-only, saveable through configuration/activity recreation;
no profile/database writes or durable-storage promises. Account/privacy copy adapts
browser-only language to this native, screen-state-only preview.

## Phone/accessibility adaptations (not a redesign)

- Full available phone width, 24dp page padding, native system/IME insets, scrolling.
- Native status/navigation bars replace the HTML fake 9:41/home handle.
- Metric rows reflow at compact width/large font, preserving 48dp +/- touch targets.
- At 150% fonts, illustrated choice cards stack art/text to avoid narrow-word columns.
- Preference chips wrap; long texts/meal labels wrap instead of ellipsis or clipping.
- Dialogs scroll within the window and inherit the selected language/font density.
- Native system font follows the CSS sans fallback: no licensed/bundled rounded
  typeface was supplied. Do not claim identical cross-platform glyph metrics.
- No tablet-specific layout is invented; tablet designs/integration remain separate.

## Verification and acceptance

APK creation was NOT requested. Follow AGENTS: no assemble/package/install or device
instrumentation. Use compileDebugKotlin, lintDebug, testDebugUnitTest and token checks.
`OnboardingPreviewTest` drives the real production composables: offline flow, back,
recreation, missing checklist, mutually exclusive allergy answer, details, risk and
unknown routes, shell actions. It produces fresh **Robolectric native** PNGs, not
mockup screenshots or emulator/device evidence, in build/reports/onboarding-preview.
Matrix: all declared states AZ/EN/RU at 100/150%, top/bottom scroll captures; baseline
phone sizes 390x844, compact 320x700 and large 430x932. Dialog decor is composited
with CENTER gravity because Robolectric itself does not position separate windows.

Visual registry adds explicit source-bound step1 cases, retaining historical debt.
No automatic baseline approval, review-visual, merge or workflow finish is performed.
Native screenshot review and user visual approval remain separate from test success.
Final check results and local commit hashes are in the handoff report; not inferred
from old CI runs. Merged-main CI is intentionally pending: this feature has no merge
permission, and the local workflow only runs CI after an authorized main merge.
