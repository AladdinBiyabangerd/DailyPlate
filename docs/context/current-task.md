# Current task — illustrated Today dashboard

## Completed
- Approved 00-reference Today layout adapted to native illustrated hero, compact actual-intake summary, plan status shortcuts and clickable meal cards.
- Plan cards open existing RecipeDetailScreen; saved cards open existing LogEditor with nutrients/edit/delete.
- Added localized AZ/EN/RU labels and large-font layouts; generic approved art explicitly labelled illustration.
- Prior K16 reviewed catalog importer/recompute work remains intact; G3/G4 closed.

## Current state
- Dashboard uses real saved entries and accepted plans only; no fabricated meal/calorie/avatar.
- Remaining energy stays unknown without a diary completeness declaration.
- Generic artwork is not a real meal photo/portion reference. Real production content/prices and device E2E remain separate.

## Decisions
- Keep macro detail in existing log editor; opening meal cards never creates intake.
- Reuse existing tokens/art and 48dp accessible native actions. No APK authorized.
- Concurrent Add/search work is outside this task; isolated local worktree preserves it.

## Remaining work
- Real catalog/prices, real meal photography and device/TalkBack acceptance remain unverified.
- K16 reviewed catalog → version-compatible planning integration remains pending; preserve source-food safety and G3/G4.

## Relevant files
- ui/today/{TodayDashboard,TodayMeals,TodayScreen}.kt; ui/BootstrapScreen.kt
- ui/menu/RecipeDetailScreen.kt; TodayDashboardUiTest.kt; FoodEntryUiTest.kt
- docs/qa/android-today-design-acceptance.md
- design-concepts/simple-turquoise/00-reference.png; implementation-guide.txt; tokens.css
- planning/nutrition-science-implementation-plan.txt §18–19; canonical research-data.json

## Checks performed
- Read local lifecycle, context, organization, design guides/tokens and scoped plan/nutrition requirements.
- Native AZ 100%/150%, EN/RU 150%, full-app Today and log detail renders inspected. Plate artwork/card label corrected; narrow EN 150% plan shortcut found visually and changed to full-width at large fonts.
- Prior merged-main 80503a0 passed 214 JVM tests, lint/compile and contrast 4.71–12.70. Final main also includes large-font shortcut fix and system Back refresh; authoritative final pass is latest.json.
- First post-merge run caught explicit AZ string parity; second caught test scroll reuse at locale changes. Both repaired.
- Final mandatory result: compare main HEAD with .local-ci/logs/latest.json (passed=true required). Native render output: .local-ci/reports/k10/today-dashboard-* and today-log-detail-*.
- Snapshot nutrients are displayed in details using both manual and recipe keys without recomputation; navigation tests assert saved energy. No branch CI, APK or device tests.

## Exact next step
- Continue K16 in a new chat only if requested: reviewed catalog → version-compatible planning integration, preserving source-food safety and closed G3/G4.
- Today acceptance reference: docs/qa/android-today-design-acceptance.md; final CI and native image evidence live under .local-ci/.
