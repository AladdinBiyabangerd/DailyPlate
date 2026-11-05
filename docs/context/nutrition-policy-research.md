# Nutrition policy research — isolated branch

## Completed
- Plan 2.1-research adds §26–29, NP-01–NP-31, exact energy/activity/goal/
  bounds/macro/menu/reassessment proposal and source limitations.
- Canonical nutrition_policy_candidate object plus generated candidate JSON,
  MD/TXT/canvas and 47-page PDF. No Android code or live policy changed.
- Primary source comparison: Health Canada adult equations and NASEM Appendix G;
  EFSA protein/fat/carbohydrate; AHA/ACC/TOS 2013; NHS gain; NICE 2025 guidance.
  Some primary sources were accessible only through official indexed excerpts;
  access failures and older source dates are recorded, not hidden.

## Current state
- Worktree /private/tmp/callmood-nutrition-policy.
- Branch docs/nutrition-policy-research from main 12ccd9f.
- User explicitly requires isolation from ongoing work: retain branch, no merge,
  no workflow finish, no branch/manual CI. Main worktree is untouched.

## Decisions
- Proposed direct NASEM-2023 adult EER; 8 coefficient rows, four confirmed
  activity categories; no Mifflin/PAL hybrid or silent three-choice migration.
- Proposed pilot scope 19–64 and BMI [18.5,30), with explicit health screening.
  This is a product scope, not an assertion of scientific model age/BMI limits.
- maintain/healthy 0; lose -500 only BMI [25,30); gain +300 only [18.5,25).
- Proposed product floors female 1500/male 1800; source-range upper endpoints,
  not proven universal safety thresholds. Reject rather than clamp.
- P=max(.83*W,.20*T/4); F=.30*T/9; C=remainder/4. EFSA ranges checked;
  incomplete nutrient definitions or infeasible split cannot pass.
- Goal-specific +/-5% menu windows preserve loss/gain caps; maintenance allows
  actual food portions rather than requiring exact floating-point equality.
- G2/G3 remain closed. All proposed product choices require explicit qualified
  review, follow-up design, new K08 contract and app acceptance before release.

## Remaining work
- Qualified APPROVE/REVISE/REJECT on specific NP decisions and evidence limits.
- If user later requests integration: compare current main first, then implement
  K08 v2 formula dispatch/typed bounds/macros/scope/activity migration separately.
- Source full-text gaps, population applicability, real activity self-report pilot,
  clinical/device tests and canvas runtime remain unverified.

## Relevant files
- planning/research-data.json (canonical, includes nutrition_policy_candidate)
- planning/nutrition-policy-candidate.json (generated research payload, not K08 v1)
- docs/nutrition-plan-design.md §26–29; planning/coding-tasks.txt research note
- library/tools/update_project_plan.py; planning/build_plan_pdf.py
- planning/qidalanma-elmi-esas-ve-icra-plani.pdf
- domain/policy/{NutritionPolicyEngine,PolicyExecutionRules,ProfileEligibility}.kt
  inspected only, unchanged.

## Checks performed
- Decimal research arithmetic for all 8 equation rows at age30/175cm/70kg,
  loss/gain example and low-energy rejection; not app test execution.
- Male inactive 2552.67, female inactive 2195.30; male85kg lose 2264.17;
  female64y/145cm/55kg raw lose 1109.71 rejected. Fixed example transcription.
- Generated views refreshed; JSON export equality and requirement/source IDs
  inspected; git diff --check. Existing token contrast 4.71/5.36/12.70.
- PDF rendered 47 pages; overview and new sections 37–43 inspected, including
  detailed renders of formulas, energy bounds, macros and worked examples.
- No CI, app tests, APK, device or clinical acceptance. No merge/main changes.

## Exact next step
- Review proposal in this worktree, especially NP-07 and NP-09–NP-23 choices;
  do not turn RESEARCH_PROPOSAL into ACTIVE or merge without a new user request.
