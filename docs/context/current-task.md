# Current task — D04 recipe details complete

## Completed
- User approved adapting D04 to the new design on 5 November 2026, retaining
  “Yedim — porsiyanı seç” and hiding unavailable photo/edit controls.
- Labelled dish illustration, personal-share summary, illustrated batch ingredients,
  expandable numbered cooking steps and secondary nutrition/version details implemented.
- Portion selection opens at its heading; existing preview/explicit confirmation,
  remaining-share safeguards and read-only recipe viewing preserved.

## Current state
- Accepted implementation main: 5a6db7c407964a2759ea44a488c773c1afec70d6.
- Technical CI and explicit hash-bound visual acceptance passed. Documentation-only
  completion merge is checked separately; .local-ci/logs/latest.json is authoritative.
- Implementation commits: 9c8510a, b19729e, 17e6379, 53ce8cc.
- All 708 final native renders viewed in 118 pixel-preserving sheets: 312 D04 and
  396 D02/D03 regression images, whose hashes match prior accepted images.

## Decisions
- Approved 07 ingredient/recipe reference supplies artwork and visual hierarchy;
  existing tokens/native components retained. Two ingredient columns at 100%, one
  at 150% keeps translated labels readable. Illustration is explicitly not a photo.
- Personal planned share and entire prepared batch remain distinct. Customized
  ingredients are exact; unverified instructions and unknown time/nutrients stay honest.
- Consumed/skipped/no-basis states do not offer invalid logging. Photo/edit and other
  unavailable actions are omitted. Existing shell navigation remains D06 debt.
- No APK, remote, push or deployment; nutrition/publication policy gates unchanged.

## Remaining work
- D05 profile and D06 navigation remain open separate units. Do not begin automatically.
- Real approved production-catalog integration, actual food photos, device/keyboard
  and TalkBack acceptance remain unverified.
- Earlier D03 FoodEntryUiTest intermittent timeout root cause remains uninvestigated;
  final D04 221-test run passed without failures/errors/skips.

## Relevant files
- android/app/src/main/java/com/aladdin/dailyplate/ui/menu/
  {RecipeDetailScreen,RecipeDetailDesign,RecipeIngredientArt}.kt
- android/app/src/main/res/values{,-az,-en,-ru}/recipe_design.xml
- android/app/src/test/java/com/aladdin/dailyplate/ui/RecipeDetailDesignUiTest.kt
- docs/qa/{design-discrepancies.md,visual-review-plan.json,visual-acceptance.md}
- design-concepts/simple-turquoise/07-inqrediyent-redaktesi.png;
  design-concepts/tokens.css; planning/nutrition-science-implementation-plan.txt
  §16/18–19; docs/nutrition-plan-design.md.

## Checks performed
- Post-merge main: 221 JVM contract/Room tests; APK-free lint/compile/token/contrast,
  canonical exports/library contracts and visual gate contracts passed.
- D04 normal/expanded/customized/no-basis/consumed/skipped/partial at AZ/EN/RU ×
  100%/150%, top/summary/every ingredient/steps/bottom plus expanded step/info views.
- Tests verify modified grams, unknown time/energy, no inherited custom steps,
  absent invalid actions, direct portion heading visibility and preview/cancel safety.
- Evidence: .local-ci/visual/5a6db7c407964a2759ea44a488c773c1afec70d6/{manifest,review}.json.
  Actual comparison completed before review-visual; finish accepted without CI rerun.
- Documentation completion CI exposed an intermittent NavigationUiTest failure:
  Compose delayed press visited a detached node across language/recreation.
  A semantics-only render-test workaround passed 221 tests but was reverted to
  preserve the existing pointer coverage; no application navigation was changed.
  Root cause repair remains separate debt. Final main status is in latest.json.
- Initial lint failure fixed by removing obsolete labels; native review improved hero
  crop, secondary back action and portion-editor scroll entry. No APK/device checks.

## Exact next step
- If requested, inspect D05 in a new chat: read this context, discrepancy inventory,
  visual rules, plan §18–19 and 05-irelileyis-ve-profil.png. Show current and reference
  side by side; present any new design for user approval before implementing it.
