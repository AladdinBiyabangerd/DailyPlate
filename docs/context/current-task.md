# Current task — sequential application coding backlog

## Completed
- Updated backlog to version 1.1 with the Android-first decision and deferred iOS scope.
- Created planning/coding-tasks.txt: 34 ordered units (K01–K34) with dependencies, source requirements, scope, acceptance checks and completion conditions.
- Mapped plan sections, customization T01–T19 and screens 01–44 to units.
- Separated technical work from clinical/content approvals, regional prices, providers, billing decisions and pilot evidence.

## Current state
- Requirements and execution planning only; no application implementation exists.
- Product source remains planning/research-data.json, version 1.7. Its TXT/PDF/canvas views were not changed.
- Library 0.3.0: 500 foods, 224 unpublished recipe drafts; no verified regional prices.
- All K01–K34 units are not started. Android is confirmed; stack/backend remain undecided.

## Decisions
- One coding unit per chat; split oversized units without dropping acceptance requirements.
- Test fixtures support development while real approvals gate live activation.
- User decision (4 November 2026): Android first. iOS is a separate future phase only if the user likes the Android result and chooses to proceed.
- K01–K34 target Android; do not ask the platform question again. Stack/backend remain to be chosen in K01.
- Preserve domain/UI/storage boundaries for future iOS; no shared framework or iOS skeleton is mandated.
- No invented clinical thresholds, prices or paid feature boundaries.
- Local Git only; remote push, store submission and deployment are outside authorization.

## Remaining work
- Start K01: choose Android stack, bootstrap minimal Android app and add actual app checks to local CI.
- Implement subsequent units in order; record acceptance evidence and unresolved gates.

## Relevant files
- planning/coding-tasks.txt (execution sequence and continuation prompt)
- planning/research-data.json (canonical product requirements)
- planning/nutrition-science-implementation-plan.txt (sections 1–19)
- planning/meal-customization-requirements.txt (T01–T19)
- library/README.md
- design-concepts/simple-turquoise/implementation-guide.txt
- design-concepts/simple-turquoise/new-flows-guide.txt
- design-concepts/tokens.css
- AGENTS.md and .cursor/rules/code-organization.mdc

## Checks performed
- Read product plan, customization scenarios, design behavior guides and local workflow.
- Verified 34 unique sequential IDs, required fields and no forward dependencies using a local assertion script.
- Branch/main CI and commit results are reported in the completion response; no application tests or UI rendering apply to this text-only task.

## Exact next step
Read K01 in planning/coding-tasks.txt and the local workflow. Android is confirmed. Choose the Android stack, create the skeleton and verify Android build/emulator smoke checks; do not begin iOS. Continue in a new chat using the prompt in the backlog.
