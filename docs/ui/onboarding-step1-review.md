# Step 1 technical and visual review — not an acceptance waiver

Production source checkpoint: `fa6fbb3` (on top of assets/copy `980997d`, base
`7f3e5c6`). This records a native implementation review, NOT merged-main completion,
a device test, user approval, or a pixel-perfect claim.

## References and comparisons

Compared the supplied original-style reference contact sheet with actual native
Robolectric renders: welcome, selected goal, filled/error body, activity and help,
both health pages and warning, allergy Yes/No/unanswered warning, selected meal
preferences, incomplete checklist, account/provider/privacy, ready/demo/diary and
revised Today/demo/diary. Representative normal and 150%/Russian/compact/large
phone top/bottom views were inspected. Not every generated image has received an
individual human acceptance record; the visual gate remains pending.

Observed parity: original turquoise token palette, white illustrated cards, four
goals/activity choices, measurement/avatar groups, ten explicit health questions,
allergen art grid, three preference groups, dynamic checklist, Google-only offer,
food hero/summary, revised Today hero/white add action/meal circles and five-tab
shell. Final refinements restore the exact checklist group titles, dashed meal
circle outlines and warning CTA without an invented arrow.

Known platform differences / limits:
- HTML refs contain fake status/home bars and often full scroll-height PNGs;
  native output is a viewport with real system/IME inset handling and scrolling.
- No bundled rounded font was supplied. Native system sans glyph metrics and
  wrapping differ from browser font fallback. Native elevation shadows and flat
  token background do not reproduce every CSS shadow/radial-glow pixel.
- Transparent art from the second approved commit deliberately replaces the older
  matte illustrations used in the first HTML renders. Bytes/hashes and proportions
  are preserved; all 27 alpha channels were checked. Two corner antialias values
  are 1/255 and wheat maximum opacity is 254/255; supplied art was not altered.
- Compact measurement rows and large-font illustrated cards reflow; preference
  chips wrap. No observed clipping in the selected inspected views, but this is
  not exhaustive TalkBack, keyboard, hardware or 200% font acceptance.
- No tablet design or backend integration. Release launcher intentionally remains
  the existing app; debug Default Activity is the new offline preview.

## Checks

- Final command: `./gradlew --offline --no-daemon --console=plain :app:compileDebugKotlin :app:lintDebug :app:testDebugUnitTest` (JDK 21 / SDK 36): PASS.
- 233 JVM tests across 49 suites, zero failures/errors/skips; the new preview suite is 11/11.
- Lint: zero reported issues. Token/contrast and complete AZ/EN/RU resource parity pass.
- 492 fresh native PNGs: all 408 registered step1 cases present, plus compact/large phone views and their bottom captures.
- Phone render dimensions: 320x700, 390x844, 430x932; 100/150% font scales.
- Debug merged manifest and original asset SHA-256 checks pass; no APK output exists.
- Git whitespace/explicit-path/secret-ignore checks pass. Merged-main CI intentionally not invoked.

The earlier run interrupted by Mac sleep/offline reported one legacy
WeeklyMenuUiTest AppNotIdle timeout. Fresh full re-execution after reconnect passed
without changing that test or weakening checks. No retry/exclusion was baked into
the test suite.

Merged manifest checks: debug Application is android.app.Application;
FirebaseInitProvider is absent; exactly the preview activity has MAIN/LAUNCHER.
The complete UI-flow test asserts zero initialized FirebaseApps before and after
navigation/provider/shell interactions. No real auth, purchases, Remote Config,
Firestore, backend, AI or nutrition algorithm is invoked by this UI path.

Render output: `android/app/build/reports/onboarding-preview/`. All render files
are freshly generated actual production composables. Separate dialog decor is
composited with CENTER gravity because Robolectric does not position windows.
This is explicitly JVM/native-render evidence, NOT emulator/device screenshots.

No merge/push/workflow finish/review-visual; no acceptance/baseline auto-approval.
