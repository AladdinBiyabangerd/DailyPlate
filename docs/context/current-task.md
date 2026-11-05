# Current task — K16 recipe detail and confirmed intake

## Completed
- Existing K16 production generation/explicit acceptance/future preview and Today projection retained.
- Menu recipe entry resolves exact library/customization revision; localized names, batch ingredients/preparation state, instructions and verification status.
- Personal portion input → nutrient/gram preview → explicit Yedim confirmation; cancellation/viewing does not log.
- Durable exact consumption command saved before atomic log/remaining-plan write; restart and post-commit retry reuse event and snapshot.

## Current state
- Scoped implementation and tests committed for mandatory merged-main CI; consult `.local-ci/logs/latest.json` and chat for actual result.
- G3/catalog and G4/pricing remain closed; no fixture bypass in production. No APK authorized.

## Decisions
- Base batch conversion requires stored cooked yield and personal grams; missing basis blocks intake here.
- Customized ingredients/instructions come from exact saved revision; cooked yield/verification are not inherited.
- Stale uncommitted commands are cleared only on repository RevisionConflict; require reload/new confirmation. Temporary failures retain the exact command.
- One generated serving denotes a personal portion, distinct from a full batch.

## Remaining work
- Production acceptance/recalculation/detail E2E with authoritative catalog/prices and device coverage.
- K18 ingredient editor and other later milestones remain separate.

## Relevant files
- ui/menu/{RecipeDetailData,RecipeDetailScreen,RecipeWorkflow,MenuScreen}.kt
- data/local/PlanConsumptionRepository.kt; domain/planning/PlanConsumption.kt
- ui/{RecipeDetailTest,RecipeWorkflowTest,MenuUiTest}.kt
- docs/qa/android-k16-generation-acceptance.md
- planning/research-data.json §8/18–19/22; planning/meal-customization-requirements.txt §2/7

## Checks performed
- Read lifecycle, organization, canonical scoped requirements, nutrition design, design references and ui-craft; inspected recipe reference image.
- Added APK-free JVM behavioral tests and native Compose rendering for AZ/EN/RU at font scale 1.5.
- First merged CI caught missing AZ override keys (fixed); second compiled and ran 197 tests, exposing test harness thread/scroll assumptions (repaired).
- Inspected native AZ portion preview at font scale 1.5; text wraps and confirmation is visible. Final CI and remaining render inspection are recorded in chat.

## Exact next step
- Check merged-main CI outcome and screenshot evidence, then plan the next scoped unit using planning/coding-tasks.txt. Do not claim production/device acceptance without evidence.
