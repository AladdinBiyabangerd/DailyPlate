# Current task — missing-state PNG designs, first unit

## Completed
- Previous map: 255 numbered plan items + T01–T19 = 274 rows, 51 missing-state groups. Original 17-PNG inventory remains unchanged.
- First new design sheet generated with built-in image_gen: 15-aktivlik-ve-hedefsiz-davam.png, four full mobile panels.
- Panels: four unselected activity options; low_active example/help; reconfirm old three-choice answer; uncertain activity to no-target diary.
- Generated image opened and compared to 01-baslangic.png. Copy is legible, no automatic target/selection fabricated. Draft proposal, not user-approved or runtime accepted.
- Exact prompt, source requirements, panel/state index, original image hash and inspection notes saved.

## Current state
- Branch docs/missing-png-activity; read Git and .local-ci/logs/latest.json for subsequent commit/finish result.
- Before this unit, main d2d4194 passed technical CI (236 JVM tests, lint, compilation, data/export/token checks) but inherited standalone-demo visual gate was pending.
- Application code, canonical nutrition plan and existing PNG assets unchanged. No APK created.

## Decisions
- Follow existing 00/01 style and named tokens. New file is a sibling concept, never an overwritten reference or app screenshot.
- Canonical sections 26.4–26.6 supply four activity codes and low_active example; uncertainty does not block diary use or fabricate an automatic target.
- The activity example is not prescribed exercise; old three-choice answer is not silently migrated.
- Keep new design_proposals metadata separate from native surface/case acceptance in visual-review-plan.json. Do not use mockups as runtime evidence or approve old demo debt.

## Remaining work
- Other three activity help panels; selected-option and full accessibility/localization variants.
- Separate no-target designs for missing sex, health exclusion, model scope, energy floor and macro infeasibility.
- Remaining map gaps: inputs/health/allergen/budget, generator results, search/manual/history, photo, edits, shopping, progress/reminders, auth/sync/privacy/billing, etc. First sheet does not close all 51 groups.
- Native AZ/EN/RU x 100/150%, pixel contrast, touch targets, device/TalkBack, implementation and user design approval remain unverified.
- Preserve unmerged fix/mock-existing-menu (7fa11b6). Its default demo text differs from current main's explicit values-az; reconcile localized resources if that branch is later merged.

## Relevant files
- docs/qa/design-requirement-map.{md,json}; planning/research-data.json
- design-concepts/simple-turquoise/15-aktivlik-ve-hedefsiz-davam.png
- design-concepts/simple-turquoise/{15-activity-prompt.txt,missing-state-png-prompts.txt,missing-state-pngs.json}
- docs/qa/visual-review-plan.json (design_proposals); docs/qa/design-discrepancies.md
- design-concepts/tokens.css; simple-turquoise implementation/new-flows/ui-extension guides

## Checks performed
- Read canonical NP-04/05/06/08 and style guides/token file; opened reference and generated PNG.
- Checked four visible panels, wording, absent fabricated calorie target and matching component hierarchy.
- Branch CI/tests not run; mandatory checks only follow merge through finish. Read latest CI for final technical result.

## Exact next step
- Inspect latest Git/CI, then continue in a new chat with the remaining activity-help panels and no-target reasons, reading this context and the map. Do not claim all missing PNGs or full visual acceptance are finished.
