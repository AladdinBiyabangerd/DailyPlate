# Current task — D02 approved portion design

## Completed
- Read lifecycle, visual rules, organization, product §16/18–20 and design/token guides.
- User approved new B design (“okeydir yeni”): recipe batch/personal share; manual exact quantity.
- Implemented native portion selection/preview, scaled batch command, source/repeat units preserved; no universal plate calories.

## Current state
- On fix/d02-portion-design; awaiting mandatory post-merge CI and fresh hash-bound visual acceptance. D02 remains open until accepted.
- Prior Today/Add and K16 work preserved. G3/G4/catalog integration remain separate.

## Decisions
- Reuse existing cooking/scale art with explicit illustration disclaimer, existing tokens, scroll and stacked large text controls.
- Prepared-count changes scale all batch ingredients and measured yield, preserving nutrients per personal plan serving; no silent composition changes.
- Exact grams require a measured recipe yield; unknown nutrients stay unknown; preview is separate from durable recording.
- No APK/device checks authorized.

## Remaining work
- Finish branch; inspect all matrix renders and reference, fix differences; review-visual then finish.
- Close D02 with exact accepted commit/evidence. D03–D06 remain independent debt.

## Relevant files
- ui/entry/{PortionDraftEditor,PortionDesign}.kt; ui/menu/{RecipePortionEditor,RecipeDetailScreen,RecipeDetailData}.kt
- PortionDesignUiTest.kt; RecipeDetailTest.kt; FoodEntryUiTest.kt; MenuUiTest.kt
- docs/qa/visual-review-plan.json; design-concepts/simple-turquoise/approved/d02-portions.{png,txt}
- planning/nutrition-science-implementation-plan.txt §16/18–19; docs/nutrition-plan-design.md

## Checks performed
- Initial clean main d9681dd, hooksPath .local-ci/hooks, prior main CI passed.
- Source review only so far; no branch CI, APK or device run. Final checks/evidence still pending.

## Exact next step
- Commit focused D02 changes, run workflow.py finish, inspect fresh .local-ci/visual/<head>/manifest.json and every required render, then write actual comparison review.
