# Current task — plan → PNG → missing design state map

## Completed
- All 255 numbered canonical plan items plus T01–T19 mapped: 274 rows.
- Opened downscaled copies of all 17 concept PNGs (00–14, approved D02/D03); original hashes, exact source text, panel references and missing-state categories recorded.
- 208 partial PNG references; 66 explicitly classified nonvisual obligations. No row marked implemented or visually accepted.
- Repeatable JSON/Markdown generation rejects unmapped items. Git-excluded local post-merge CI checks export freshness.

## Current state
- Work branch: docs/plan-png-state-map; documentation/generator only, application and design assets unchanged.
- Commit/merge/CI outcome: read Git and .local-ci/logs/latest.json; this handoff precedes finish.
- Main had failed technical CI and pending visual acceptance at 518a404 before this task. The map does not close that debt.

## Decisions
- Canonical nutrition plan unchanged. PNG presence means design context, never implementation evidence.
- Missing concept panels and missing runtime functionality are different findings; runtime not audited here.
- Section 30 reported specialist review/energy-macro activation supersedes older status text; catalog/release evidence remains separate.
- Sections 17.4/17.8 remove onboarding meal coverage/equipment/home inventory and exact home quantity; old PNG 12 must not reintroduce them.
- Section 26 requires four EER activity categories and reconfirmation of older three choices.
- No APK requested/created; no automatic visual acceptance.

## Remaining work
- Finish documentation branch with merged-main CI. Report inherited app failures/visual debt explicitly if they persist.
- Future UI work: expand selected requirement gaps into concrete visual-review-plan cases; implement one surface, render APK-free, inspect before acceptance.
- Separate mock-menu repair stays unmerged on fix/mock-existing-menu (7fa11b6). It feeds seven fixture meals into real menu/recipe flows. Previous user requested no CI for that repair; do not silently merge it into this audit.

## Relevant files
- docs/qa/design-requirement-map.md and .json
- planning/build_design_requirement_map.py
- planning/research-data.json; planning/meal-customization-requirements.txt
- design-concepts/simple-turquoise PNGs and implementation/new-flows/ui-extension guides
- docs/qa/design-discrepancies.md; visual-review-plan.json; visual-acceptance.md

## Checks performed
- Generation resolves all 274 IDs and PNG paths; rejects duplicate/orphan/unmapped dispositions.
- All 17 relevant concept images opened at inventory resolution; original hashes recorded.
- git diff --check passed before commit. No branch CI/JVM tests run.
- Runtime behavior, full-resolution pixel/contrast, device/TalkBack and new hash-bound visual acceptance unverified.

## Exact next step
- Read Git/latest CI result. Run workflow.py finish for this map if not yet merged. Keep inherited app/visual repair separately scoped; map supplies requirement IDs and missing design states for that work.
