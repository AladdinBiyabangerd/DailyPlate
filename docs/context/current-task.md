# Current task — no-target reason concepts and gate mapping

## Completed
- Gate now validates explicit hash-bound draft concept sources separately, without allowing Android inputs, tokens or native surface references to bypass native evidence.
- Mapping commit c779d24 merged as 23c9344; post-merge technical CI passed, no unmapped inputs, 756 native cases enumerated. Native acceptance remains pending.
- Sets 17/18: five full AZ no-target reason panels: missing sex, health exclusion, model/product scope, energy product floor, macro infeasibility.
- Opened both final sheets and 15/01 references, read all text; comparison/hash records in design_proposals and missing-state inventory. Historical requirement-map audit retained with concept supplements.

## Current state
- PNG branch docs/no-target-reason-pngs; inspect Git/latest CI for commit/merge outcome.
- Concept-only changes; no app, canonical plan or APK changes. Native visual gate is still pending; do not use concept PNGs to accept it.

## Decisions
- Missing sex never inferred; diary/expert-target entry link remains available.
- Health exclusion is not diagnosis; expert target never bypasses exclusion.
- Product scope is not a scientific model boundary. Floor is not universal clinical minimum; no clamp. Macro minimum not silently reduced.
- Existing fix/mock-existing-menu 7fa11b6 remains unmerged; do not discard.

## Remaining work
- User approval; reason subvariants, expert-target provenance/date entry form, selected and native localization/accessibility states.
- Inherited native demo/source acceptance: 756 manifest cases must actually be compared, unresolved differences fixed; no acceptance written this chat.
- Other historical map groups remain open; concepts do not implement behavior.

## Relevant files
- android/tools/visual_gate.py; android/tools/test_visual_gate.py; docs/qa/visual-acceptance.md
- docs/qa/visual-review-plan.json; docs/qa/design-requirement-map.json; docs/qa/design-discrepancies.md
- design-concepts/simple-turquoise/{17-hedefsiz-davam-sebebleri,18-hedefsiz-hesablama-sebebleri}.png and corresponding prompt files; missing-state-pngs.json
- planning/research-data.json (canonical), nutrition-science-implementation-plan.txt §§5–6,21,25–28; docs/nutrition-plan-design.md

## Checks performed
- Gate regression tests run only in merged-main CI; mapping technical pass, no mapping error.
- Opened five final panels and references; no clipping observed. Token contrast: text/surface 12.70, muted/bg 5.04, white/primary 4.71. Raster pixel contrast not certified.
- First PNG merge b9b9233 failed the derived-map parity check before Android checks. Repair branch fix/derived-concept-map derives supplements from missing-state-pngs.json and regenerates JSON/MD together. Inspect latest CI for repair outcome; native acceptance remains pending.
- APK, device, TalkBack, native AZ/EN/RU × 100/150%, clinical effectiveness and user design approval unverified.

## Exact next step
- Inspect final Git/latest CI. Keep technical and visual statuses separate. Resolve inherited native acceptance using fresh manifest/render/reference comparison, without blessing drafts or unrelated debt; alternatively continue one user-chosen concept gap in a new chat.
