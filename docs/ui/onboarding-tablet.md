# Offline tablet onboarding preview

## Open the correct Android project

In Android Studio, use **Open** and choose
`/Users/mac/My Workspace/callmood-tablet-ui/android` (the directory containing
`settings.gradle.kts`), not the repository root or a different worktree. Use JDK 21
and the existing local Android SDK. Opening/syncing is not device verification.
APK creation, Run/install and instrumentation require a separate explicit request.

Branch: `feat/onboarding-tablet-ui`, based on `87e054b`. Authoritative designs:
`99fa3db`, `design-concepts/simple-turquoise/onboarding-tablet/`. Phone work and
original design files are preserved. No merge or push is part of this stage.

## Native layout and state

All eleven onboarding groups and Today use Compose controls, not a WebView or
whole-screen images. The available safe window chooses tablet composition only
when both dimensions are at least 600dp. Portrait/landscape use actual available
width versus height. IME padding is inside that choice so keyboard appearance
alone cannot swap layouts. Windows narrower than 600dp retain the phone flow. Tablet landscape splits require
at least 1000dp width; narrower tablet windows reuse the approved stacked composition.
Measurement trios reflow when their minimum interactive widths would not fit.

- Welcome/account/ready: constrained portrait stacks and landscape paired panels.
- Goal/activity: image-led portrait 2x2 choices; horizontal landscape 2x2 choices.
- Body: portrait measurement trio; landscape measurements/optional sex split.
- Health: two-column question cards, centered fifth in portrait, separate warning.
- Allergy: portrait 3x2 grid; landscape settings/choices split; detail validation.
- Meal preferences: portrait stack; landscape three-column cards.
- Checklist: portrait grid; landscape overview/actions and missing-answer grid.
- Today: portrait stacked hero/energy; landscape paired cards; five-item bottom shell.

Content grows and scrolls rather than shrinking text at 150%. The existing
PreviewState saver retains drafts, step, dialogs, language and checklist return.
AZ/EN/RU resources are reused. Google/privacy and other destinations are explicit
simulations. The debug launcher remains a plain Application without Firebase's
startup provider; no repositories, network, auth, nutrition, purchase or sync
services are introduced. Release launcher/backend/domain code is unchanged.

The exact supplied transparent assets are reused. TabletArtwork trims empty alpha
margins at paint time, preserves aspect ratio and retains breathing room. It does
not modify or regenerate assets. Phone painters remain unchanged.

## Reference adaptations and limits

The approved prototype's temporary `next`/guest dialogs are replaced with actual
navigation to the next implemented native screen, not copied as dead ends. Provider,
privacy/help/skip confirmations and unimplemented-tab dialogs remain simulations.
The prototype Ready `empty` fixture displays a sample menu despite missing meal
preferences; the connected existing native state intentionally keeps the safer
no-target diary behavior. No eligibility or calorie algorithm is added.

References use a Mac rounded font and simulated status/gesture chrome. No font file
was supplied. Android uses the existing app/system typography and real insets;
Robolectric does not draw actual device chrome. Cross-platform glyph metrics,
shadows, radial gradient and the updated transparent artwork are not pixel-identical.
DailyPlate package identity remains `com.aladdin.dailyplate`; QIDA design terminology
is inherited, not a rename of the app.

See `onboarding-tablet-review.md` for actual check/render results and remaining
visual-review debt. Fresh PNGs are APK-free Robolectric renders, NOT device
screenshots. Automated coverage is not human visual acceptance.
