# Current task — hide extra allergen input for no-allergy selection

## Completed
- Latest fix: selecting no allergies hides the custom-allergen field and its
  illustration; deselecting it restores the input. Existing none toggle clears
  previous custom answers. Gluten/dietary restrictions stay independent.
- Household-input removal merged as 62682e7 with green branch/main CI.
- Preserved concurrent turquoise UI alignment merged as 432dec5: larger choice
  artwork, corner selected badges, rounded numeric inputs/circular steppers,
  centered welcome/account action and separate progress row. Tests assert actual
  selected semantics. Its clean worktree remains at /private/tmp/callmood-ui-alignment
  detached at 432dec5; no files were removed or rewritten there.
- Male selection hides pregnancy/breastfeeding, clears their previous draft answers,
  and ignores stale answers at confirmation. Merged as a9594aa with green branch/main CI.
- Removed equipment and home-food inventory forms, add/remove actions, artwork and
  labels from onboarding. Removed obsolete inventory text from the budget note.
- Removed inventory validation and mapping: old incomplete home-food answers cannot
  block confirmation; newly confirmed profiles have empty equipment/homeFoods.
- Updated canonical plan sections 8/17 and regenerated TXT/canvas to match the
  user's 5 November decision. Existing persisted profiles are not rewritten.

## Current state
- Work branch: fix/hide-extra-allergens-for-none. Verify Git history and
  .local-ci/logs/latest.json for final commit/merge and branch/main CI evidence.
- No APK creation, installation or device tests authorized or performed.

## Decisions
- Onboarding no longer asks users to type household equipment or food inventory.
- storedHomeFoods stays in the serialized draft model solely for decoding legacy
  drafts; removed inputs do not influence newly confirmed profiles.
- Budget region, currency, optional amount, people and meal coverage stay unchanged.
- Existing design tokens/styles are reused; no new dimensions or colors.

## Remaining work
- Complete the current branch CI/finish lifecycle if Git/logs do not yet show it.
- Physical-device acceptance and TalkBack remain unverified.
- K08 eligibility/policy remains the next numbered milestone, outside this fix.

## Relevant files
- android/app/src/main/java/com/aladdin/dailyplate/{domain/onboarding/OnboardingDraft,ui/onboarding/OnboardingScreen}.kt
- android/app/src/test/java/com/aladdin/dailyplate/{domain/OnboardingDraftTest,ui/OnboardingUiTest}.kt
- android/app/src/main/res/values{,-az,-en,-ru}/onboarding.xml
- ui/{DesignComponents,FoundationComponents,BootstrapScreen}.kt (concurrent alignment)
- android/tools/extract_design_art.py
- planning/research-data.json (canonical), nutrition-science-implementation-plan.txt
  sections 8, 17–19; library/tools/update_project_plan.py generates TXT/canvas.

## Checks performed
- Latest allergen fix: 54 JVM/UI tests, lint, compilation, resource parity and
  contrast passed. Tested enter custom answer -> none -> hidden -> deselect none
  -> empty restored input, in AZ/EN/RU at normal/150% fonts. AZ normal and RU 150%
  native no-allergy renders inspected. Branch/main final evidence is in local logs.
- First fix: 53 JVM/UI tests, lint, compilation, contrast, branch/main CI passed;
  male/female native renders inspected.
- Simplification: 54 JVM/UI tests, lint, Kotlin compilation, three-language resource
  parity and token contrast passed. AZ normal and RU 150% final preference footer
  renders inspected: removed forms/artwork/inventory note absent; action visible.
- Branch/main lifecycle results are recorded by .local-ci/logs/latest.json after
  finish; consult that and Git rather than assuming success from this handoff.

## Exact next step
Verify final branch/merged-main CI and clean Git state. For any further change, begin a new chat with its specific requested
behavior, read this handoff, and consult current canonical plan requirements.
