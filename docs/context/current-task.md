# Current task — remove budget meal coverage input

## Completed
- Removed budget meal coverage input and labels in AZ/EN/RU.
- Missing coverage no longer blocks confirmation; legacy draft coverage is ignored for newly confirmed profiles. Existing persisted profiles are unchanged.
- Updated canonical plan section 17 and derived TXT/canvas; retained budget tier, amount, region, currency and people.
- Domain regression covers empty and legacy coverage; UI checks absence in three languages at normal/150% fonts.

## Current state
- Task branch: fix/remove-budget-meal-coverage. Consult Git and .local-ci/logs/latest.json for final merge and CI evidence.
- Unrelated user AGENTS.md changes preserved separately during finish and restored afterward.

## Decisions
- Meal coverage remains unspecified rather than inventing an answer.
- Reused existing layout and tokens; no new visual values.

## Remaining work
- APK/device acceptance and TalkBack unverified; no APK creation authorized.
- Rendered visual inspection pending unless explicitly recorded below.

## Relevant files
- android/app/src/main/java/com/aladdin/dailyplate/{domain/onboarding/OnboardingDraft,ui/onboarding/OnboardingScreen}.kt
- android/app/src/test/java/com/aladdin/dailyplate/{domain/OnboardingDraftTest,ui/OnboardingUiTest}.kt
- android/app/src/main/res/values{,-az,-en,-ru}/onboarding.xml
- planning/research-data.json section 17; derived nutrition-science-implementation-plan.txt and nutrition-implementation.canvas.tsx

## Checks performed
- git diff --check passed. Main CI evidence: .local-ci/logs/latest.json after finish.

## Exact next step
- Verify merged-main CI record before any new task; follow mandatory local workflow for subsequent changes.
