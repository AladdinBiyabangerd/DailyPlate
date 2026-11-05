# Current task — unified nutrition plan 2.0; next implementation K13

## Completed
- Merged docs/nutrition-plan-design.md with canonical research-data.json.
  Preserved §1–19 and T01–T19; added §20–25 (U/R/C/D/B/A requirement IDs).
- One canonical JSON now generates MD/TXT/canvas; refreshed 39-page PDF.
- Resolved default calorie/PAL/macro conflicts without inventing clinical
  approval. No dietitian currently; G2/G3 remain closed, diary path retained.
- Researched WHO/EFSA/FDC/NIDDK/current DGA/OFF licences/Firebase/Play/Circular33.
  DGA 2020–2025 no longer labelled current. Source verification scope explicit.
- Preserved existing K01–K12 implementation; queue corrected to next K13.
  K12 code/acceptance history is in docs/qa/android-k12-acceptance.txt and commits
  06df3ae, 1ebe9ea, 35968ba and final share-display fix 01c3589 (main ee22394).
  Unknown cooked grams remain unknown; actual batch share is displayed.
  Prior K12 verification: 103 JVM/Room/Compose tests at main d2d44c6; later
  matching-head CI supersedes it. This task changes planning/export tooling only.

## Current state
- Finish this docs/nutrition-plan-design branch through workflow.py finish.
  Completion requires .local-ci/logs/latest.json passed=true with matching HEAD.
  After successful finish, main contains plan 2.0; no remote operations.

## Decisions
- JSON is authoritative; MD binding instructions point to the same content.
- Unsupported calorie floors, deficit/surplus/PAL and macro defaults are not
  active policy. Specialist approval cannot be replaced by source links or CI.
- AZ/EN/RU, four goals, 3/4/5 slots, guest/offline and T01–T19 remain required.
- Spark-only + Go/PostgreSQL is an integration target; K25/K26 must document
  actual configuration and authoritative storage. Current Room flow remains.
- No application feature or clinical gate activated; no APK generated.

## Remaining work
- G2 clinical policy review; G3 content/rights/translations/yield; G4 local prices;
  G5 real backend/photo/data lifecycle; G6 monetization; G7 device/user pilot.
- Current DGA full nutrient mapping and regional datasets/legal scope unverified.
- Canvas runtime rendering not verified; embedded data regenerated, layout kept.

## Relevant files
- planning/research-data.json; docs/nutrition-plan-design.md (generated)
- planning/nutrition-science-implementation-plan.txt; coding-tasks.txt
- planning/meal-customization-requirements.txt; library/tools/update_project_plan.py
- planning/build_plan_pdf.py; qidalanma-elmi-esas-ve-icra-plani.pdf
- docs/qa/android-k12-acceptance.txt; domain/policy/ (K08 regressions if changed)

## Checks performed
- PDF rendered: 39 pages; overview sheets inspected, changed source-page layout
  re-rendered and inspected. Source-only orphan pages corrected.
- Existing token contrast: primary 4.71, muted 5.36, text 12.70 on white.
- git diff --check; PDF marked binary to avoid treating its streams as text.
- Mandatory post-merge CI is the final check; inspect matching-head log for
  actual test counts/results. Local CI now includes MD export parity too.
- Clinical approval, new app behavior, APK/device and canvas runtime unverified.

## Exact next step
- In a new chat read this handoff, AGENTS.md, local workflow, plan §20–25 and
  scoped K13 sections (§3/6/7/8/11/12/15). Implement K13 only; retain G2/G3
  fail-closed filtering, UNKNOWN semantics and production/test separation.
