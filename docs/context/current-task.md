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
- Final merged-main CI and visual evidence are recorded below after finish.
- K16 reviewed catalog → version-compatible planning integration remains pending; preserve source-food safety and G3/G4.

## Relevant files
- ui/today/{TodayDashboard,TodayMeals,TodayScreen}.kt; ui/BootstrapScreen.kt
- ui/menu/RecipeDetailScreen.kt; TodayDashboardUiTest.kt; FoodEntryUiTest.kt
- docs/qa/android-today-design-acceptance.md
- design-concepts/simple-turquoise/00-reference.png; implementation-guide.txt; tokens.css
- planning/nutrition-science-implementation-plan.txt §18–19; canonical research-data.json

## Checks performed
- Read local lifecycle, context, organization, design guides/tokens and scoped plan/nutrition requirements.
- New UI/navigation renders execute in mandatory post-merge JVM suite. No branch CI, APK or device tests.

## Exact next step
- Finish current local merge, inspect generated native renders and record CI/visual result; then continue K16 separately if requested.
