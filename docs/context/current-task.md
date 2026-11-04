# Current task — visual reference requirements in plan 1.7

## Completed
- Added a project-wide AGENTS.md rule requiring relevant current plan sections and referenced requirements before coding/review, plus acceptance verification and explicit handling of conflicts.
- Researched EFSA, NIDDK and USDA primary sources on age/sex in energy and nutrient planning; expanded section 5, added source S17 and clarified onboarding inputs in section 17.
- Required valid age and formula-required sex for automatic individualized formula output; missing input retains general logging/expert-target fallback. Added profile/policy contract and acceptance scenarios.
- Compared all remaining design sheets (01, 03–11) visually with plan sections 1–18 and customization requirements.
- Added section 19 with screen 01–44 traceability, missing behavior, reminders, weight history, subscriptions, sharing, bookmarks, drafts, accessibility and acceptance scenarios.
- Updated engine candidate selection to cover configured 3/4/5 meal slots.
- Inspected 00-reference.png and 02-secimler-ve-hesab.png.
- Added section 18: four goals including healthy eating; allergies/dislikes; 15/30/60 minute cooking limits; three budget preferences; 3/4/5 meals; guest/account/sync flows; Today, logging, portions, menu and progress requirements.
- Updated section 17 goal contract and acceptance coverage from 3×3 to 4×3 profiles.
- Synchronized canonical JSON, TXT, embedded canvas data and PDF as version 1.7.

## Current state
- Requirements only; no application implementation exists here.
- Library remains 0.3.0: 500 foods, 224 unpublished recipe drafts; no verified regional prices.

## Decisions
- Healthy eating is a separate goal without automatic weight-loss deficit or weight-gain surplus.
- Screenshot selections and calorie values are examples, not confirmed user answers or universal targets.
- All profile constraints apply to initial planning, substitutions and future plan recalculation; past logs remain intact.

## Remaining work
- Implement profile, account and planning contracts; obtain regional prices and complete content/policy reviews.
- Render interactive canvas in its host if runtime verification is needed; only embedded data synchronization verified here.

## Relevant files
- planning/research-data.json (canonical, sections 16–19)
- planning/nutrition-science-implementation-plan.txt
- planning/nutrition-implementation.canvas.tsx
- planning/build_plan_pdf.py
- planning/qidalanma-elmi-esas-ve-icra-plani.pdf
- library/tools/update_project_plan.py
- design-concepts/simple-turquoise/00-reference.png
- design-concepts/simple-turquoise/02-secimler-ve-hesab.png

## Checks performed
- Plan 1.7 JSON/canvas equality and source references verified; PDF pages 7–8 and final source page 28 rendered and inspected; removed orphan closing-note page.
- Asserted canonical JSON equals embedded canvas data; TXT includes section 18; old three-goal enum removed.
- Rebuilt 28-page PDF with bundled Python; rendered and visually inspected new section 19 on pages 23–25 and TOC page 2, no clipping observed.
- No application tests run: this change specifies requirements only.

## Exact next step
Read plan sections 16–19 and planning/meal-customization-requirements.txt. Choose one implementation unit (e.g. profile/onboarding) and its acceptance scenarios; no application exists yet. Resolve free/paid feature boundaries before subscription implementation.
