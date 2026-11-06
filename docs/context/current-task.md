# Current task — uniform final design package

## Completed
- User explicitly requested every old selected turquoise design set aligned, all eleven listed design gaps closed, and a final ZIP.
- Recreated 00–18, onboarding/ingredient-error references and completion 19–30 through shared components; added 31–48 state sheets. Total: 49 AZ sheets / 165 panels.
- Every output PNG opened and compared with original sheets and 00/D02/D03 references. Hash-bound static review: unified/visual-review.json. No observed clipping after fixing missing art, cramped age text and error spacing.
- Final ZIP: design-concepts/qida-turquoise-designs-final.zip; 166 entries including MANIFEST. Every archive hash verified. Extracted portable build regenerates identical HTML; all local links/assets resolve.

## Current state
- Work done in isolated /private/tmp/callmood-design-package on docs/unified-design-delivery; original shared branch/user edits preserved.
- Mandatory merge/technical results are recorded in that worktree's .local-ci/logs/latest.json; consult the actual result before claiming lifecycle completion.
- The concept review resolves design artifact coverage only. Inherited native visual acceptance remains pending; docs/qa/design-discrepancies.md is unchanged open debt. No false native review-visual record submitted.

## Decisions
- Unified directory/ZIP is the current complete AZ design delivery. Old raster sets stay as historical repo references; natural/warm/dark alternatives are outside the selected turquoise style.
- D02 prepared/consumed separation and D03 dated selector/status structure retained; new designs do not silently approve/change native runtime references.
- Removed obsolete exact home-inventory/equipment/meal-coverage onboarding forms per current plan. Evdə var is only a simple shopping flag.
- Values are labelled synthetic fixtures, not DB nutrition recompute. Existing token palette unchanged. No invented clinical threshold, store price, retention duration or approval.
- New source generator embeds existing token/style snapshot in HTML for self-contained export. No application code, APK, network publication or remote Git changes.

## Remaining work
- User visual approval of final concepts; actual store/service/privacy strings depend on implementation configuration.
- Native localization, keyboard, TalkBack, device and backend transaction behavior remain unverified. Those are implementation acceptance, not missing AZ concept PNGs.

## Relevant files
- design-concepts/simple-turquoise/unified/{index.html,README.txt,screens.json,coverage.json,replacement-map.json,visual-review.json,render-report.json,MANIFEST.json}
- Shared source: components.py, legacy.py, states.py, style.py, build.py, render.mjs, package.py; assets/ and references/.
- docs/qa/visual-review-plan.json (explicit new concept source/hash mappings); docs/qa/design-discrepancies.md (native debt, unchanged).
- planning/nutrition-science-implementation-plan.txt §§16–19,26–30; planning/meal-customization-requirements.txt T01–T19; docs/nutrition-plan-design.md.

## Checks performed
- All 49 rendered sheets inspected. Chrome reports zero missing images and zero horizontal overflow.
- Contrast: text/background 11.95; muted/background 5.04; white/primary 4.71; all >=4.5.
- 166 ZIP entries CRC and SHA-256 verified, HTML local links/assets checked, extracted portable rebuild byte-identical.
- Post-merge CI is mandatory and its exact fresh outcome lives in .local-ci/logs/latest.json, not inferred from concept rendering.

## Exact next step
- Inspect the current merged-main CI outcome and preserve this delivery. Future native acceptance work must compare actual fresh manifest renders with approved references; do not treat the static ZIP as runtime proof.

## Concurrent onboarding integration
- Main also contains completion/31 allergy Yes/No initial states and completion/32 ready-to-Today flow from another task. Preserved those sources and integrated their decisions as unified/47 (initial Yes/No) and unified/48 (ready → Start → Today), including aligned 02/33 screens.

Final contrast correction: all 49 rendered sheets were reopened after small links, secondary actions and active navigation switched to the existing dark text token. Checked pairs pass WCAG AA (minimum small-text pair 4.71:1); 49 renders have no broken images or horizontal overflow. Previous merged-main 5a8f2a3 technical CI passed, including 222 JVM tests; inherited native visual acceptance remains pending. Final correction merge result is recorded in .local-ci/logs/latest.json.

## Git delivery synchronization — 2026-11-06
User explicitly authorized remote push and merge. Shared checkout was on old docs/missing-flow-pngs; apparent design additions already existed on main 5c4df1a. Preserved complete shared state in stash safety/shared-design-files-before-main-sync-20261106. Kept current main design/QA versions rather than restoring stale copies. Retained useful onboarding reference note in implementation-guide.txt. Final package remains 49 sheets / 165 panels. Technical CI previously passed; native visual acceptance remains pending. Next: push delivery branch, finish local merge/checks, update remote main and verify shared local main equals origin/main. No APK authorized.

## Active draft onboarding — original visual style
- Branch: feat/onboarding-original-style. User requests separate folder, no Git delivery yet; no commits/merge/CI/push performed.
- Folder: design-concepts/simple-turquoise/onboarding-original-style/. Standalone local HTML prototype only; real login/calculation absent.
- User approved 01 welcome and 02 goal in chat. 03 body approved; 04 activity approved; 05 health pages and risk warning approved; 06 allergy approved; 07 meal preferences approved; 08 incomplete answers approved; 09 Google-only account offer approved; 10 ready screen approved; 11 Today approved. User explicitly requested stopping this stage here. Do not proceed screen-by-screen without approval dialog.
- Preserve original attached PNG style (reference.png), existing tokens. Goal uses four canonical choices (§18.2). Activity uses four codes per §26 NP-04–06 (supersedes old three-option §19); uncertain route has no energy target.
- Body has blank initial measurements, decimal comma, +/- controls, optional sex and unspecified path explanation; render fixture 28/172/78,4 is illustrative only.
- Checks: Chrome render opened for 03 filled and empty; no horizontal overflow/missing images; body.js syntax and palette contrast pass. No native/device/clinical checks; no CI run on branch.
- Exact next step: wait for a new user request. All eleven scoped design units approved; preserve separate folder and branch. No commit/merge/push, additional screens or implementation requested. per canonical planning/nutrition-science-implementation-plan.txt and docs/nutrition-plan-design.md. Reuse scoped prototype files. Remaining: health (two screens + ineligible), allergy states, meal choices, incomplete answers, account offer, ready, initial Today with fixture plan/navigation.

Health draft checks: all three 05 renders opened; no horizontal overflow or missing images. health.js syntax passes; isolated JS behavior checks pass for missing answers, page 1→2, risk→warning and all-No→next placeholder. No real medical assessment or native/device checks.

Allergy draft: explicit Yes/No, multi-select five illustrated groups + other, mandatory nut/other detail, separate gluten restriction, unknown warning/diary route. Yes and warning renders opened; no horizontal overflow/missing assets; JS syntax passed. Real allergen filtering not implemented.

Meal preferences: 15/30/60 minutes, budget preference (not exact currency limit), 3/4/5 meals with explicit snack slots. No defaults. Empty and selected renders opened, no horizontal overflow/missing images; JS syntax passed. No real menu/price/energy calculation.

Incomplete answers: dynamic grouped links from local drafts, optional sex and explicit uncertain activity do not count as missing; diary route retained. Render opened, no overflow/missing assets. JS syntax + isolated checks for empty/complete/optional-sex/nut-detail pass. This is not an eligibility validator.

Account offer: Apple/Google/email buttons open explicit not-connected dialog, Not now stores guest mode; privacy dialog explains local-only storage and no account service. No credentials collected, real login/sync absent. Offer/provider/privacy renders inspected, no horizontal overflow/missing assets; JS syntax passed.

Account revision: user explicitly requested Google only; Apple/email removed. Not now guest route retained. Revised account offer awaiting approval; do not advance to ready yet. This user instruction overrides plan §18.9 provider list for current scope.

Ready screen: goal/count from saved answers, no fabricated calories, labelled fixture menu, Start does not create intake records; diary/risk paths hide sample menu. Demo and diary renders opened; no overflow/missing assets; ready.js syntax passed. No actual eligibility/calculation service.

Today draft: synthetic 1900 kkal explicitly labelled non-personal fixture, meal slots follow 3/4/5 preference, no intake records; diary/risk routes hide plan/energy, five-section bottom menu opens future-scope placeholders. Plan/diary/placeholder renders inspected; no horizontal overflow/missing assets; today.js syntax passed. No native runtime, login, nutrition computation, real persistence contract or device acceptance.

Final approval: user approved Today and said this stage should remain here. All 11 design units approved in chat. Local draft only; CI not run because no local merge was performed, and no native implementation completion is claimed.

Today redesign reopened: prior Today approval superseded by user dislike. User requested original 00-reference layout. Revised large turquoise meal hero, white add action, remaining-energy card with unconsumed + slots and raised central add nav now awaiting approval. Other approved screens unchanged. Exact next step: obtain revised Today approval; no Git delivery authorized.

Final Today redesign approval: user explicitly approved revised original-reference-style Today and requested keeping it. This supersedes the earlier Today design. All eleven scoped units approved; stop here. No commits, merge, CI, push, APK or real authentication/nutrition computation performed. Exact next step: await new user request.

Git instruction update: user explicitly requested commit only. Commit approved prototype folder and context on feat/onboarding-original-style; do not merge, run finish/CI or push. Earlier no-commit instruction is superseded for this snapshot.

## Onboarding standalone assets — 2026-11-06
- User clarified fidelity to existing PNG designs matters; any format is acceptable. Approved one generated transparent welcome sample, then authorized continuation.
- Prepared 27 high-resolution RGBA cutouts in onboarding-original-style/assets/transparent/, preserving originals and screen source links. ZIP: onboarding-transparent-assets.zip. Manifest records source/output hashes, dimensions and usage.
- All 27 input/output images opened; Chrome 1440px small-size gallery rendered and opened. No missing images/overflow; all assets have actual transparent pixels and visible content. Text contrast 11.95/12.70. Reconstructed artwork is not pixel-identical; remaining set awaits user review.
- No native integration, APK, commit, merge, CI or push in this asset task. Prior commit-only/no-merge instruction preserved. Native visual debt remains open.
- Exact next step: user reviews preview.png/ZIP; adjust requested assets, then integrate only when requested.

Asset Git delivery: user explicitly requested adding and committing the standalone asset package on the current feat/onboarding-original-style branch. Commit includes 27 PNG assets, preview, manifest/review notes, ZIP and this context. Original screens remain unchanged. Verified all 27 manifest hashes and ZIP CRC/entry parity before commit. No merge, CI, push or APK; native integration remains unverified.


## Native step 1 implementation — isolated worktree, 2026-11-06

- User requested implementation from the two approved design/asset commits. Base:
  `7096a04` + `7f3e5c6`; authoritative onboarding-original-style HTML/CSS/JS/PNG and
  transparent manifest, not the older unified/v4 designs.
- Worktree: `/Users/mac/My Workspace/callmood-onboarding-ui`, branch
  `feat/onboarding-ui-step1`. Original checkout and tablet-design work are untouched.
- Implemented all eleven groups as native Compose, fake/saveable UI state only.
  Debug Default Activity is OnboardingPreviewActivity: plain Application, no
  FirebaseInitProvider or repository startup. Existing release launcher/integrations
  remain unchanged. No backend/domain modules or original designs edited.
- Local commits: 980997d (immutable art/localized copy), fa6fbb3 (native flow).
  QA/tests/handoff are the next focused commit; see git log for its final hash.
- Verification: final-source APK-free check result is recorded in
  `docs/ui/onboarding-step1-review.md`; new tests generate native Robolectric PNGs
  for three locales, 100/150% text and 320/390/430 phone widths. These are not device
  screenshots. No APK/package/install/instrumentation was requested or run.
- Remaining: parent/user visual review; any exact cross-platform font/shadow parity
  refinements; explicit merge permission and subsequent mandatory merged-main CI.
  No merge, push, finish or review-visual was performed. No tablet layout invented.
- Exact next step: inspect fresh render contact sheets and the review/handoff docs;
  open this worktree's android directory to run the debug preview when explicitly
  requesting APK/device verification. Do not switch or alter the tablet checkout.
## Active tablet onboarding design — 2026-11-06

### Completed
- Separate feat/onboarding-tablet-design branch preserves feat/onboarding-original-style. workflow.py start rejected non-main; used git switch -c to honor explicit new-branch/no-merge instruction.
- 01 welcome approved both orientations. 02 goal approved: revised portrait image-led 2×2 cards; landscape approved and hashes preserved. User rejected original portrait stretched-phone composition/excess space.
- 03 body drafted: 10 PNGs, empty/filled/unspecified/invalid/empty-step × both orientations. All ten opened against original body reference.

### Current state
- 03 body portrait and landscape approved by user. Next unit is 04 activity and help.
- Design only. No Android changes, APK, commits, merge, CI or push; explicit user restriction overrides standing delivery lifecycle.

### Decisions
- Both orientations; portrait primary (800×1280), landscape 1280×800. Use tablet grouping, not enlarged phone rows. Reuse original art/copy/tokens.
- Body portrait has 3 measurement cards across and optional sex beneath; landscape measurements left / sex right. Original error copy and no-target unspecified explanation retained. 28/172/78,4 illustrative fixture only.
- Phase 2 transparent assets begins only after all designs approved; reuse existing transparent set, seek sample approval for newly needed art.

### Remaining work
- 04 activity/help, 05 health both pages/medical warning, 06 allergy/warnings, 07 meals, 08 incomplete, 09 Google-only account/Not now, 10 ready, 11 latest approved Today/bottom menu. Preserve existing substates.
- Final separate design and asset packages pending. No new asset generation yet.

### Relevant files
- design-concepts/simple-turquoise/onboarding-tablet/{README.txt,01-welcome.html,02-goal.html,03-body.html,tablet.css,goal.css,body.css,body.js,render-body.mjs,03-body-render-report.json}
- Approved phone sources: onboarding-original-style/; tokens: design-concepts/tokens.css.
- docs/qa/visual-review-plan.json records concept requirements and inspected hashes. Native discrepancy debt unchanged.
- planning/nutrition-science-implementation-plan.txt §§5,18–19; §26 supersedes old activity options. docs/nutrition-plan-design.md binds nutrition behavior.

### Checks performed
- All 16 current tablet PNGs opened across 01–03 (latest goal revision included). No missing images/overflow; action targets >=48px. body.js syntax passed. Contrast minimum 4.71 for checked existing text pairs.
- No technical CI (no merge authorized), native/device/keyboard/TalkBack validation or calculation/auth services.

### Exact next step
- 03 body approved. In continuation, inspect only original 04-activity HTML/CSS/JS/help states plus plan §26 NP-04–06 and approved tablet source; prepare portrait/landscape activity drafts and await approval.

## Tablet 04 activity/help draft — 2026-11-06
- Completed: 04 activity and help drafted on existing feat/onboarding-tablet-design; 14 PNGs (empty, four categories, uncertain, help × both orientations), all opened against phone activity/help and approved tablet goal reference.
- Current state: user approval pending; 01–03 unchanged. No commit, merge, push, CI or APK as explicitly instructed.
- Decisions: portrait primary, image-led 2×2 choices, landscape horizontal 2×2 cards and two-column dialog; exact NP-04–06 copy and source examples preserved. No default, uncertain gives no-target diary explanation. Original art reused, transparent asset stage deferred.
- Relevant files: onboarding-tablet/{04-activity.html,activity.css,activity.js,render-activity.mjs,04-activity-*.png,04-activity-render-report.json}; docs/qa/visual-review-plan.json has source/output hashes. Plan §26 NP-04–06; original activity sources remain unchanged.
- Checks: all 14 renders inspected; no horizontal/vertical page overflow, missing images, overflowing measured elements or targets below 48px. JS syntax passed; contrast 11.95/5.04/4.71. Native/device, localization beyond AZ, text scaling, TalkBack and real calculation unverified; native visual debt unchanged. Next-step button opens a scoped prototype placeholder; no 05 screen yet.
- Exact next step: user reviews 04 portrait/landscape and help. Adjust if requested; do not start 05 until approval and request. Preserve no-commit/no-merge/no-push/no-APK restriction.

## Tablet 05 health draft — 2026-11-06
- Completed: user approved 04 activity/help. Prepared 05 health pages 1/2 and 2/2, support warning, missing-answer errors and no-target skip confirmations. 22 final AZ PNGs opened against three original phone references after fixing fieldset legend placement.
- Current state: 05 awaiting user approval on feat/onboarding-tablet-design. 01–04 sources/PNGs unchanged. No commit, merge, push, CI or APK; user explicitly forbids them.
- Decisions: portrait primary 800×1280 with five question cards; landscape 1280×800 two columns, reading order unchanged. Ten questions from plan §6/§26 NP-08, no default answers, original illustration/copy. Risk and incomplete routes have no automatic target or individual menu. Warning remains a dedicated screen matching approved phone source; skip is a dialog. No new asset generation.
- Remaining: approval/adjustments to 05; then 06 allergy onward only when requested. Native/device/TalkBack, EN/RU, enlarged fonts, medical assessment/calculation unverified. Native visual debt unchanged.
- Relevant files: onboarding-tablet/{05-health-1.html,05-health-2.html,05-health-warning.html,health.css,health.js,render-health.mjs,05-health-*.png,05-health-render-report.json}; docs/qa/visual-review-plan.json records inspected source/output hashes; planning/nutrition-science-implementation-plan.txt §6 and §26 NP-08; docs/nutrition-plan-design.md.
- Checks: 22 renders, no page overflow/missing images/measured element overflow or visible targets below 48px. JS syntax and token contrast pass (minimum 4.71). No CI because no merge authorized. Future allergy routes show a scoped not-yet-built dialog.
- Exact next step: user reviews 05. Continue in a new chat with this file and scoped health sources; preserve no Git delivery/APK restrictions. Do not implement 06 before approval and request.

05 portrait revision: user requested more optimal placement. Questions now left, Yes/No pairs aligned right and vertically centered; note/CTA grouped below. All 11 portrait PNGs reopened, no clipping/overlap observed, DOM checks pass. All landscape hashes unchanged. Warning design unchanged. User approval pending; no commit/merge/push/CI/APK. Exact next step: review revised 05 portrait pages; do not advance to 06.

05 portrait second revision: user rejected full-width rows. Current portrait uses two-column question tiles, answers inside each tile, centered fifth card. All 22 fresh renders opened; no observed clipping, page overflow, missing images or targets below 48px. Landscape/warning layouts retained. Supersedes previous row revision. User approval pending; no commit/merge/push/CI/APK. Exact next step: review two revised portrait pages; do not advance to 06.

05 final approval: user approved revised portrait two-column cards and landscape health pages/warning. 01–05 now approved. Stop here; next unit is 06 allergy and warning states only when requested. No commit/merge/push/CI/APK authorized or performed.

## Tablet 06 allergy draft — 2026-11-06
- Completed: 06 allergy and unknown warning prepared, 18 AZ PNGs: empty/no/yes/selected/details/error/nuts-error/other-error/warning × portrait/landscape. All final PNGs opened against original phone reference.
- Current state: 01–05 approved; 06 pending user review on feat/onboarding-tablet-design. No commit/merge/push/CI/APK; preserve restriction.
- Decisions: portrait six 3×2 compact allergen tiles to fit both detail inputs; landscape choices right/settings left, centered closed-list form. Existing artwork/copy, explicit Yes/No, multiselect, required nut/other detail, separate gluten restriction and unknown diary path preserved. No real filter/clinical calculation. 07 future route is a prototype placeholder.
- Relevant files: onboarding-tablet/{06-allergy.html,allergy.css,allergy.js,render-allergy.mjs,06-allergy-*.png,06-allergy-render-report.json}; docs/qa/visual-review-plan.json hashes; planning/nutrition-science-implementation-plan.txt §6.4/§18; original allergy sources.
- Checks: 18 renders inspected, no page/element overflow or missing art; measured visible actions >=48px; JS syntax and token contrast pass (minimum 4.71). Native/device/keyboard/TalkBack, EN/RU/text scaling and real filtering unverified; native debt unchanged.
- Remaining / exact next step: review 06 designs and adjust if requested. Continue in new chat using this context; do not start 07 before approval and request.

06 user correction: removed “Sənə uyğun yeməkləri seçmək üçün.” in portrait and landscape to clarify Yes/No belongs to allergy question. All 18 refreshed PNGs opened; no overflow/missing images or targets <48px. Approval pending. No commit/merge/push/CI/APK.

06 final approval: user approved allergy portrait/landscape and warning states after subtitle removal. 01–06 now approved. Stop here; next unit is 07 meal preferences only when requested. No commit/merge/push/CI/APK authorized or performed.

## Tablet 07 meal preferences draft — 2026-11-06
- Completed: user requested 07 portrait/landscape. Prepared 14 AZ PNGs: empty/three/four/five/error/partial-error/next × both orientations; all opened against original phone 07 and approved tablet 06.
- Current state: 01–06 approved and unchanged; 07 awaits user review on feat/onboarding-tablet-design. No commit, merge, push, CI or APK.
- Decisions: portrait three stacked illustrated cards; landscape three equal columns. Existing art/tokens/copy; no defaults, 15/30/60 minutes, budget preference without exact-limit claims, 3/4/5 meals and explicit snack slots. No new art generation. 08 remains a clearly labelled dialog placeholder; no real menu/price/energy computation.
- Relevant files: onboarding-tablet/{07-meal-preferences.html,meal-preferences.css,meal-preferences.js,render-meal-preferences.mjs,07-meal-preferences-*.png,07-meal-preferences-render-report.json}; docs/qa/visual-review-plan.json; planning/nutrition-science-implementation-plan.txt §17/§18.5–7.
- Checks: 14 fresh renders, no page/element overflow or missing images; measured visible targets >=48px; JS syntax and token contrast pass (minimum 4.71). No CI because no merge authorized. Native/device/TalkBack, EN/RU/text scaling and real persistence/calculation remain unverified; native visual debt unchanged.
- Exact next step: user reviews 07 portrait/landscape and states; adjust if requested. Do not start 08 without approval and request. Preserve no-commit/no-merge/no-push/no-APK restriction.

07 final approval: user approved portrait/landscape meal preferences in chat. 01–07 now approved. Stop here; next unit is 08 incomplete answers only when requested. No commit/merge/push/CI/APK authorized or performed.

## Tablet 08 incomplete answers draft — 2026-11-06
- Completed: user requested 08 both orientations. 14 AZ PNGs (empty/partial/allergy-detail/complete/optional/diary/next × portrait/landscape) inspected against original phone 08 and approved tablet style.
- Current state: 01–07 approved and unchanged; 08 pending user approval on feat/onboarding-tablet-design. No commit/merge/push/CI/APK.
- Decisions: portrait two-column missing-section cards; landscape explanation/actions left and cards right; complete state centered. Optional sex and uncertain activity are not missing answers. Incomplete profile gives no automatic target/menu; full answers do not imply eligibility. Diary route stored locally; 09 remains an explicit placeholder dialog. Reused existing art/tokens.
- Relevant files: onboarding-tablet/{08-incomplete.html,incomplete.css,incomplete.js,render-incomplete.mjs,08-incomplete-*.png,08-incomplete-render-report.json}; docs/qa/visual-review-plan.json; planning/nutrition-science-implementation-plan.txt §§5–6/18.
- Checks: 14 renders inspected, no page/element overflow/missing images/visible targets below 48px; JS syntax and contrast pass (minimum 4.71). No technical CI because no merge authorized. Native/device/TalkBack, EN/RU/scaling and real eligibility unverified. Existing body/goal/activity tablet screens do not fully persist drafts; render fixtures demonstrate review states only, not end-to-end integration. Native visual debt unchanged.
- Remaining: user review/adjustments to 08; future 09 Google-only account offer only after approval and request.
- Exact next step: review 08 portrait/landscape PNGs; adjust requested differences. Continue in new chat with this section and scoped sources. Preserve no commit/merge/push/APK; do not implement 09 without request.

08 final approval: user approved portrait/landscape incomplete answers designs in chat. 01–08 now approved. Stop here; next unit is 09 Google-only account offer/Not now only when requested. No files in the design were changed for this approval; no commit, merge, push, CI or APK performed. Native acceptance remains unverified.


## Tablet 09 account offer draft — 2026-11-06
- Completed: requested Google-only offer and Not now, both orientations. Eight fresh AZ PNGs (offer/provider/privacy/guest) opened against phone 09 and approved tablet 08 reference.
- Current state: 01–08 approved and unchanged; 09 pending user approval on feat/onboarding-tablet-design. No commit, merge, push, CI or APK.
- Decisions: portrait centered stacked content; landscape illustration/copy left, actions right. Existing artwork/tokens retained. Optional account, local storage/restoration limits and privacy dialog preserved. Google explicitly not connected; Not now stores guest mode without clearing answers, opens future-10 placeholder. No real auth/sync or plan generation. User Google-only instruction overrides plan §18.9 provider list for this draft.
- Relevant files: onboarding-tablet/{09-account.html,account.css,account.js,render-account.mjs,09-account-*.png,09-account-render-report.json,assets/art_account.png}; docs/qa/visual-review-plan.json; planning/nutrition-science-implementation-plan.txt §18.9/13; original phone account sources.
- Checks performed: all eight renders inspected; no page/element overflow, missing images or measured visible targets <48px. JS syntax passed; contrast 11.95/5.04/4.71. Renderer verifies guest mode and existing answer preservation. No native/device/TalkBack, EN/RU/text-scaling, real login/sync or end-to-end onboarding validation; native visual debt unchanged.
- Remaining / exact next step: user reviews 09 portrait/landscape and dialogs; adjust if requested. Do not start 10 without approval and request. Preserve no commit/merge/push/APK restrictions.

09 final approval: user approved Google-only account offer and Not now portrait/landscape designs in chat. 01–09 now approved. Stop here; next unit is 10 ready screen only upon approval and a new request to prepare it. No design source changes for this approval; no commit, merge, push, CI or APK performed. Real authentication/sync and native acceptance remain unverified.


## Tablet 10 ready draft — 2026-11-06
- Completed: 10 ready prepared in both orientations; 14 fresh AZ PNGs (three/four/five/empty/diary/risk/next) opened against phone 10 and approved tablet 09.
- Current state: feat/onboarding-tablet-design; 01–09 approved and unchanged; 10 awaiting user review. No commit, merge, push, CI or APK.
- Decisions: portrait stacked hero/summary/menu/actions; landscape overview left and menu/actions right. Existing art/tokens. Goal/count reflect saved drafts; no fabricated calorie target. Menu explicitly synthetic, not a personalized recommendation. Diary/risk hide menu and retain no-target explanation. Start opens future-11 dialog without changing storage or creating intake records. Existing prior-screen placeholders remain unchanged; not end-to-end integration.
- Relevant files: onboarding-tablet/{10-ready.html,ready.css,ready.js,render-ready.mjs,10-ready-*.png,10-ready-render-report.json,assets/art_recipe.png,assets/art_egg.png,assets/art_healthy.png}; docs/qa/visual-review-plan.json; planning/nutrition-science-implementation-plan.txt §18.7–8/11; docs/nutrition-plan-design.md; original phone ready sources.
- Checks performed: all 14 renders opened; no page/element overflow, missing art or measured visible targets below 48px. JS syntax, contrast (11.95/5.04/4.71) and git diff --check pass. Renderer checks diary/risk menu hiding and Start storage invariance. First overflowing 4/5-slot portrait fixed and freshly rendered. No native/device/TalkBack, EN/RU/scaling, real eligibility/calculation or end-to-end checks; native debt unchanged.
- Remaining: user review/adjustments to 10; 11 latest approved original-style Today only upon approval and request. No new asset generation.
- Exact next step: review 10 portrait/landscape and states. Continue in a new chat with this section and scoped ready sources; preserve no commit/merge/push/APK restriction. Do not begin 11 without request.

10 final approval: user approved ready screen portrait/landscape designs in chat. 01–10 now approved. Stop here; next unit is 11 latest approved original-style Today/bottom navigation only upon a new request. No design source changes for this approval; no commit, merge, push, CI or APK performed. Real nutrition calculation and native acceptance remain unverified. Exact next step: in a new chat read this section, then scoped original Today sources and current tablet references if 11 is requested.


## Tablet 11 Today draft — 2026-11-06
- Completed: requested Today/bottom navigation in portrait and landscape matching last approved original-style Today. All 18 fresh AZ PNGs opened against phone Today/diary and approved tablet 10 reference.
- Current state: feat/onboarding-tablet-design; 01–10 approved and unchanged; 11 pending user review. No commit, merge, push, CI or APK.
- Decisions: portrait stacked hero/energy; landscape paired cards. Existing artwork/tokens and five destinations retained, central Add emphasized. 1 900 kkal is explicitly a synthetic fixture, not calculated/personal target. 3/4/5 slots reflect saved preference; diary/risk hide menu and energy. Other destinations open explicit future-section dialogs; navigation does not create intake records or modify stored answers. Prior-screen placeholders unchanged; no end-to-end onboarding integration.
- Relevant files: onboarding-tablet/{11-today.html,today.css,today.js,render-today.mjs,11-today-*.png,11-today-render-report.json,assets/art_log.png}; docs/qa/visual-review-plan.json contains inspected hashes; original-style/{11-today.html,today.css,today.js,11-today.png,11-today-diary.png}; planning/nutrition-science-implementation-plan.txt §§18.7/18.11–12/19.12; docs/nutrition-plan-design.md.
- Checks: 18 renders inspected, no page/element overflow, missing art or measured visible targets <48px. JS syntax, token contrast (11.95/5.04/4.71) and git diff --check passed. Renderer checks diary/risk hiding and four navigation dialogs with unchanged storage. No native/device/TalkBack, EN/RU, enlarged text, real nutrition/eligibility or end-to-end checks; native visual debt unchanged. No CI because merge is prohibited.
- Remaining / exact next step: user reviews 11 portrait/landscape and states; adjust only requested differences. Do not begin another screen without request. Preserve no commit/merge/push/APK restriction.

11 final approval: user approved Today and bottom navigation portrait/landscape designs in chat. 01–11 now approved. Stop here; further screens or integration only upon a new request. No design source changes for approval; no commit, merge, push, CI or APK performed. Native/device, real calculation and end-to-end acceptance remain unverified. Exact next step: wait for a new user request; preserve no Git delivery/APK restrictions.

## Tablet Git delivery — 2026-11-06
User now authorizes committing all approved 01–11 tablet designs and merging into feat/onboarding-original-style. Preserve existing native onboarding work on target branch. This is a local feature-branch merge only; no main merge, CI, push or APK authorized. Asset audit: all 27 tablet illustrations already have transparent counterparts; no new assets generated. Prior draft-only Git restrictions are superseded for this delivery.

Tablet merge record: approved tablet snapshot 99fa3db merged locally into feat/onboarding-original-style. Context conflict resolved by preserving both native implementation and tablet history; QA proposal registry auto-merged. No Android or original-style files changed by tablet delivery. No main merge, CI, push or APK.

## Phone planned Today — 2026-11-06
- User requested nine further phone design units, approval after each. Only unit 1 drafted; units 2–9 must wait for approval.
- Working branch feat/onboarding-original-style in /Users/mac/My Workspace/callmood-onboarding-parent (shared original checkout remains tablet branch).
- Added 12-today-planned.html, planned.css/js, render-planned.mjs and 12-today-four/five.png under onboarding-original-style. Approved 11 remains unchanged.
- Retained turquoise hero; actual intake/remaining-energy summary separated from planned 4/5 meal cards. Breakfast marked already logged, not counted twice; all numeric/menu fixtures labelled synthetic. No storage writes or real nutrition computation. Navigation follows content in this scrolling concept.
- Both fresh 390px renders opened against approved 11; no missing images, page overflow or visible action targets below 48px. Hero image intentionally clipped within card. JS syntax and contrast checked (minimum 4.71); diff whitespace checked.
- User approval pending. No commit/merge/CI/push/APK; prior feature-only delivery limits preserved. Native/device/TalkBack/localization/scaling and real integration remain unverified.
- Exact next step: review both phone planned Today variants, adjust requested differences; only after approval prepare unit 2 (empty/over-target/no-target).

### Phone Today correction — user rejected expanded design
- Latest instruction supersedes prior expanded plan/list layout: keep approved 11 exactly, only replace three plus slots with five. Removed planned.css and expanded lists; 12 HTML reuses original styles with scoped JS selecting 4/5 slots. Original 11 unchanged.
- Five slots: Səhər / 1-ci qəlyanaltı / Nahar / 2-ci qəlyanaltı / Şam. Snack labels use two lines to fit. Original 1900 empty-day fixture, hero copy and navigation retained.
- Fresh four/five renders opened; zero missing art/overflow/targets below 48px. No native implementation, commit/merge/CI/APK. Await user approval; do not begin unit 2.

## Phone Today unit 2 — special states draft
### Completed
- User approved corrected five-plus Today (unit 1), then explicitly requested starting unit 2. Prepared empty/exceeded/no-target diary only.
- Three fresh 390px PNGs opened and compared against approved 12-today-five.png.
### Current state / Decisions
- feat/onboarding-original-style, /Users/mac/My Workspace/callmood-onboarding-parent. Unit 2 awaiting user approval.
- Empty retains five plus slots and adds menu action; exceeded shows synthetic 2100 actual vs1900 target, 200 above, three confirmed slots, no negative remaining or restrictive language. Diary hides menu/energy, offers add and empty record panel. All fixtures labelled. No actual logs/storage mutation or nutrition services.
- Original approved 11/12 unchanged. No commit/merge/CI/push/APK in design drafting. Native acceptance debt preserved.
### Relevant files
- onboarding-original-style/{13-today-states.html,today-states.css,today-states.js,render-today-states.mjs,13-today-empty.png,13-today-exceeded.png,13-today-diary.png,13-today-render-report.json}; docs/qa/visual-review-plan.json.
- planning/nutrition-science-implementation-plan.txt §19.6/19.12 and docs/nutrition-plan-design.md §5; existing tokens.css and approved 12 reference.
### Checks performed
- All three renders opened; no missing images/horizontal overflow/actions below48px. JS syntax, contrast (minimum4.71) and whitespace pass. Native/device/TalkBack/EN/RU/scaling, persistence/calculation unverified; no technical CI because no merge.
### Remaining / Exact next step
- Obtain user approval for unit2; adjust only requested changes. After approval and request, prepare unit3 weekly menu in a new chat using approved phone style. Do not start units3–9 in this chat.

Unit2 user correction: hero must show the next unlogged meal, not an already confirmed meal. Empty now previews breakfast; three logged slots preview second snack (walnuts). Derived from first slot without logged class; all logged hides hero. Existing approved12 remains unchanged. Refreshed all three renders; opened each, then reopened corrected snack title after sizing fix. No overflow/missing art. Unit2 still awaiting approval; no Git delivery/CI/APK.

Unit2 final approval: user approved special Today states after next-unlogged-meal hero correction ("okeydir"). Units1–2 approved. Preserve these designs; next unit3 weekly menu only upon explicit request, preferably new chat as prior handoff. No design code changes, commit/merge/CI/push/APK performed for approval.

## Phone weekly menu draft — 2026-11-06
- Completed: unit3 requested on feat/onboarding-original-style in callmood-onboarding-parent. Added 14-weekly.html, weekly.css/js, render-weekly.mjs and 11 AZ phone review PNGs. Existing 12/13 and tablet work preserved.
- Current state: design prototype awaiting user approval; no commit/merge/CI/push/APK. workflow start attempted, rejected existing dirty task tree; continued explicitly requested existing branch without discarding prior changes.
- Decisions: approved Today tokens/cards/nav; dated seven-day selector with distinct Today/selection; 3/4/5 slots; unknown energy/time explicit. Yedim opens positive-decimal portion input, confirmation changes in-memory demo status only. Recipe and three change choices are scoped entry previews; real details/edit/swap/calculation not implemented. Prior/empty week, loading/error/diary states remain explicit. No DB, clinical approval, real intake or durable writes claimed.
- Relevant files: onboarding-original-style/{14-weekly.html,weekly.css,weekly.js,render-weekly.mjs,14-weekly-*.png,14-weekly-render-report.json}; docs/qa/visual-review-plan.json; plan §§16/18.12/19.7 and meal-customization-requirements.txt.
- Checks: JS syntax, Chrome APK-free rendering, missing images/overflow/48px target-height checks, token contrast 11.95/5.04/4.71, git diff --check. Native/device/TalkBack, EN/RU/scaling, real data/actions/end-to-end acceptance unverified. Native debt unchanged.
- Exact next step: user reviews weekly menu and entry dialogs; adjust requested differences. Do not start another unit or deliver Git changes without request.

Weekly final review: all 11 refreshed PNGs reopened after Azerbaijani month-name and matching recipe-image corrections; source/image hashes recorded in proposal inspection. Seven day targets are 48px with horizontal scroll. No visual approval inferred.

## Weekly redesign after user rejection — 2026-11-06
- User rejected first weekly design and requested improvement. Same unit/branch; prior Today/tablet sources preserved.
- Revised: white lightweight calendar, turquoise selected date, larger existing transparent meal art, compact cards and one action row (Reseptə bax / Yedim / Dəyiş). Unknown energy/time note consolidated above list. No nutrition or transaction behavior expanded.
- Checks: 11 fresh Chrome phone states opened; no missing images, overflow or targets below48px height; token contrast minimum4.71, JS syntax/whitespace pass. Final polish hides empty meal-count badge and widens consumed-record action. Hashes refreshed after inspection.
- Current state: revised design awaiting user approval. No commit/merge/CI/push/APK; native/device/EN/RU/text-scaling/real data acceptance unverified. Exact next step: user review and requested adjustments only.

Weekly redesign final approval — 2026-11-06: user approved revised phone weekly menu ("okeydir"). Unit3 approved; preserve lighter calendar, compact illustrated cards and single-row actions. No design code changed for approval. No commit/merge/CI/push/APK performed. Real recipe/portion/change services and native acceptance remain unverified. Exact next step: wait for a new user request; do not begin another unit automatically.

Continuation request — 2026-11-06: user said "basla" after weekly approval. Next logical design unit is recipe details reached by Reseptə bax; exact numbered units4–9 were not captured in this file, so do not invent their full order. Prepare only phone recipe-detail design using approved weekly/Today style and plan §19.7/§16, meal-customization requirements. Read existing recipe references and versioned library data; label missing energy/time/yield honestly. Keep drafting on feat/onboarding-original-style; no Git delivery/APK. User reviews before further units. Context handoff requested before another large unit in this chat.


## Phone recipe detail draft — 2026-11-06
- Completed: requested phone “Reseptə bax” standalone design on feat/onboarding-original-style in /Users/mac/My Workspace/callmood-onboarding-parent. Eight AZ Chrome renders opened against approved weekly normal reference.
- Current state: awaiting user approval. Approved weekly/Today/tablet sources preserved; weekly recipe entry still opens its earlier placeholder, new detail is directly accessible via 15-recipe.html. No commit/merge/CI/push/APK.
- Decisions: reuse weekly white cards/turquoise/existing transparent plov art. L01 0.3.0 local library fixture, one serving vs four-serving pot; source quantities/steps and estimated65min. Unreviewed draft explicitly labelled; no clinical/allergen-safe claim, nutrition estimate or cooked-yield invention. Saxla toggles favorites demonstration in memory only, never menu/intake. Edit/photo/eat open clearly labelled separate-flow entry placeholders; no real editor/camera/log/persistence service.
- Relevant files: onboarding-original-style/{15-recipe.html,recipe.css,recipe.js,recipe-fixture.json,render-recipe.mjs,15-recipe-*.png,15-recipe-render-report.json}; docs/qa/visual-review-plan.json. Requirements: plan §16/§19.7, meal-customization-requirements.txt, docs/nutrition-plan-design.md; approved 14-weekly-normal.png.
- Checks: eight fresh390px renders (normal/batch/saved/portion/edit/photo/loading/error), zero overflow/missing images/visible targets below48px; node syntax and contrast passed (minimum4.71). Actual images inspected, hash-bound draft evidence recorded. Native/device/TalkBack/EN/RU/text scaling and actual recipe/edit/photo/intake behavior unverified; native debt unchanged.
- Exact next step: user reviews detail, adjust requested differences only. Wait for approval; no further unit or Git delivery authorized.

Recipe correction: user requests collapsed sections by default. Ingredients, preparation and prepared-photo now independent native details/summary, initially closed. Eleven fresh390px renders opened; zero overflow/missing images/targets below48px. Initial closed state asserted by renderer. Await approval; no commit/merge/CI/APK.

Recipe hero correction: explicit user request implemented: image left, all former below-image text right (slot/name/version/time/servings). Collapsed defaults retained. Eleven refreshed390px renders opened, no overflow/missing images/targets below48px. Await approval; no commit/merge/CI/APK.

Recipe detail final approval — 2026-11-06: user approved latest phone detail ("okeydir"): illustration left, recipe text right; ingredients/preparation/prepared-photo sections collapsed by default and independently expandable. Preserve this approved design on feat/onboarding-original-style. Stop here; next unit only upon new request. No design source change for approval; no commit/merge/CI/push/APK. Separate editor/photo/portion services and native acceptance remain unverified.


## Phone ingredient editor draft — 2026-11-06
- Completed: user requested next screen then explicitly “inqrediyentleri deyis ekranini hazirla goster”. Prepared standalone 16-ingredients.html with approved recipe hero/style, eight source ingredient rows, direct grams and +/-1q, explicit removal confirmation, one-serving/pot switch, prepared-servings scaling, cancel/discard, reset.
- Current state: awaiting user approval on feat/onboarding-original-style at /Users/mac/My Workspace/callmood-onboarding-parent. Approved15 source preserved; its edit entry remains earlier placeholder. No commit/merge/CI/push/APK.
- Decisions: amounts start per serving; pot count scales whole pot, not actual eaten portion. No DB nutrition recompute or production writes. Add search and before/after preview are clearly labelled future-screen entry dialogs. Empty/invalid values block preview. No fabricated clinical limits or compatibility approvals.
- Relevant files: onboarding-original-style/{16-ingredients.html,ingredient-editor.css,ingredient-editor.js,render-ingredients.mjs,16-ingredients-*.png,16-ingredients-render-report.json}; docs/qa/visual-review-plan.json. Plan§16/19.7 and planning/meal-customization-requirements.txt§1–3 apply.
- Checks: nine390px renders opened (normal/batch/changed/invalid/empty/remove/add/preview/cancel), no missing art/overflow/targets below48px. JS syntax and token contrast passed(min4.71); no native/device/TalkBack/EN/RU/scaling or real recompute/integration checks. Chrome first startup failed, retry succeeded.
- Remaining: user approval and any requested design correction. Production validation and atomic persistence not implemented by this concept.
- Exact next step: review editor PNG and adjust only requested differences. New chat advised before another large unit; do not begin next screen without request. Preserve draft-only/no Git delivery restriction.

Editor compact revision requested/implemented: single-row ingredient controls with48px targets, pot count visible only in batch mode, explanations in collapsed Məlumat, sticky preview/cancel footer. Nine states plus390×844 viewport opened. Normal height2246→1248px; viewport scroll travel1402→404px(~71% less). No overflow/missing art/short targets. Await user approval; same branch/no commit/merge/CI/APK. Exact next step: review compact editor; adjust requested differences only.

Ingredient editor compact final approval — 2026-11-06: user approved latest compact16 design ("okeydir"). Preserve single-row controls, batch-only prepared count, collapsed Məlumat and sticky preview/cancel actions. Stop here; further screen only upon new request. No design source changes for approval; no commit/merge/CI/push/APK. Real product search/recompute/preview/persistence and native acceptance remain unverified. Exact next step: await user request; use latest editor section for continuation.

## Phone 17 ingredient search draft — 2026-11-06
- Prepared requested phone “İnqrediyent əlavə et” on existing feat/onboarding-original-style worktree /Users/mac/My Workspace/callmood-onboarding-parent. Approved compact16 preserved unchanged. workflow.py start was attempted and rejected existing uncommitted design drafts; continued task-owned branch per explicit user scope, preserving all earlier work.
- Standalone17 HTML/CSS/JS, render-ingredient-search.mjs; nine AZ PNGs: normal/selected/empty/loading/error/manual/invalid/added/batch. Sources and hashes recorded in visual-review-plan.json before/after review.
- Existing tokens/hero/cards/sticky actions retained. Source IDs reuse local recipe fixture; no live DB. Selection narrows list; query change resets selection. Positive finite decimal grams/comma; separate manual name/state and explicit missing nutrients; single-serving vs whole-pot labels. No universal unit conversions, safety claims or invented nutrition values.
- All nine images opened against approved16; five changed states reopened after compacting. No horizontal overflow, missing images or visible targets below48px. JS syntax, diff whitespace and contrast checks pass (minimum4.71). No commit/merge/CI/push/APK. No native/device/EN/RU/scaling/TalkBack checks.
- Draft only: Add opens explicit concept dialog, does not mutate editor, storage, plan or intake. Loading/error are render fixtures, no network integration. Real search, recompute, allergen/instruction validation, persistence and editor integration remain unimplemented.
- Exact next step: await user visual approval; adjust only requested differences. Do not begin next unit or deliver Git changes before request.

17 user correction: removed visible USDA FoodData Central, source IDs and “mənbəli nümunə” labels from result rows/selection; internal source fixture metadata preserved. Helper now asks only to check product state. All nine PNGs refreshed; normal/selected opened and checked, no clipping. Render metrics: no overflow/missing images/targets under48px; JS syntax passes. Approval pending; no commit/merge/CI/APK.

17 final approval — 2026-11-06: user approved revised ingredient-search design with visible USDA/source labels removed (“okeydir novbetiye kec”). Preserve17; next requested logical unit is phone18 ingredient-change before/after preview, reached from compact16. No exact full numbered roadmap exists in context; this next step follows plan§16.5 and new-flows-guide screen28.
- Next scope: compact original style; before/after recipe/personal portion, day/week projection, shopping delta, ingredient/preparation changes, missing nutrient and allergen/instruction blocking states. Preserve energy target, past logs, other meals and base recipe. No fabricated DB recompute or approvals; fixtures explicitly labelled. No visible USDA labels per latest user preference.
- Exact next step: read current-task latest sections; ui-craft, tokens.css; approved16/17 sources and PNGs; planning/nutrition-science-implementation-plan.txt§16 and planning/meal-customization-requirements.txt§1–3/6/8–9. Record18 visual plan before editing, prepare standalone phone concept, render/open states, show user and wait for approval. Worktree /Users/mac/My Workspace/callmood-onboarding-parent on feat/onboarding-original-style. Preserve all existing uncommitted drafts. No commit/merge/CI/push/APK authorized for design review. Real DB/persistence/native acceptance remains unverified.


## Phone18 ingredient-change preview — 2026-11-06
- Completed: standalone18-change-preview.html, change-preview.css/js, render-change-preview.mjs and ten fresh AZ390px PNGs: normal/batch/details/missing/allergen/instructions/loading/error/conflict/saved (last is explicitly no-write dialog). All opened against approved compact16/17.
- Current state: feat/onboarding-original-style in /Users/mac/My Workspace/callmood-onboarding-parent;18 awaiting user approval. Prior drafts preserved. No commit/merge/CI/push/APK. workflow start rejected dirty task-owned branch; continued existing requested branch.
- Decisions: shared tokens/hero, compact before/after nutrient rows and personal portion, day/week projection, collapsed ingredient/preparation/shopping/preservation sections, sticky save/edit. No visible USDA. All numbers synthetic; no DB recompute/safety approval. Four-serving pot distinct from one-serving personal share. Missing values never zero; blocking states disable save. Enabled save only opens explicit concept/no-write dialog.
- Checks performed: ten Chrome APK-free renders opened, zero overflow/missing art/targets under48px; JS syntax, whitespace, contrast11.95/5.04/4.71 pass. Visual-review-plan records source/render hashes. No native/device/TalkBack/EN/RU/scaling, DB, atomic persistence, undo or end-to-end16→17→18 integration checked/implemented. Native debt unchanged.
- Relevant files: onboarding-original-style/{18-change-preview.html,change-preview.css,change-preview.js,render-change-preview.mjs,18-change-preview-*.png,18-change-preview-render-report.json}; docs/qa/visual-review-plan.json; plan§16 and meal-customization-requirements.txt§1–3/6/8–9.
- Remaining / exact next step: show18 PNG, await user approval and adjust requested differences only. No further screen or Git delivery before request.


18 final approval — 2026-11-06: user approved18 (“okeydir novbetiye kec”) and requested next unit. Preserve18 design. No commit/merge/CI/push/APK performed for approval.
- Next requested unit: phone19 “Yeməyi dəyiş” action chooser, following new-flows-guide.txt screen29: Tərkibi dəyiş / Başqa yemək seç / Başqa günlə dəyiş. Distinguish replacement from reciprocal swap; preserve approved compact16–18 style, no visible USDA labels. Already-eaten/past plan portion cannot silently change history. Alternative/swap destinations remain clearly labelled future units unless explicitly scoped.
- Context handoff:18 is complete as an approved concept;19 not yet implemented. Continue in /Users/mac/My Workspace/callmood-onboarding-parent on feat/onboarding-original-style, preserving all uncommitted drafts. Read ui-craft, tokens.css, approved16–18, planning/nutrition-science-implementation-plan.txt§16, planning/meal-customization-requirements.txt§4–7 and new-flows-guide.txt. Record19 review plan before editing; render/open required phone states; show user and await approval. No Git delivery or APK authorized.


## Phone19 meal change chooser draft — 2026-11-06
- Prepared on existing task branch feat/onboarding-original-style in callmood-onboarding-parent; preserved all earlier drafts. Approved16–18 compact hero/card/token style reused; no visible USDA.
- Three actions: Tərkibi dəyiş opens16; Başqa yemək seç and Başqa günlə dəyiş explain replacement vs reciprocal swap in explicit future-unit dialogs. No plan/intake writes or suitability claims. Past/consumed states block all plan edits and direct history correction to diary in copy.
- Files: onboarding-original-style/{19-meal-change.html,meal-change.css,meal-change.js,render-meal-change.mjs,19-meal-change-*.png,19-meal-change-render-report.json}; docs/qa/visual-review-plan.json records requirements and inspected hashes.
- Checks: five fresh390px Chrome renders opened against16–18; zero horizontal overflow, missing images or targets under48px. JS syntax and git diff whitespace pass. Existing token contrast11.95/5.36/4.71 passes.
- User approval pending. No commit/merge/CI/push/APK; this is a design draft, not native implementation. Native/device/TalkBack/EN/RU/text scaling, real candidate filtering, atomic swap and end-to-end persistence unverified.
- Exact next step: show19 normal phone render and await user approval; adjust only requested differences. Do not implement20 or Git delivery before request.

19 final approval — 2026-11-06: user approved19 and requested next unit. Prepare phone20 alternative selection only; preserve approved19. No Git delivery authorized.

## Phone20 alternative selection — 2026-11-06
- Completed:19 approved by user;20 drafted on existing feat/onboarding-original-style in callmood-onboarding-parent. Approved16–19 compact style/tokens retained; no visible USDA. Earlier sources/drafts preserved.
- Current state:20 awaits user approval. Three explicitly unverified demo recipe choices, outline/checkmark selection, positive finite personal portion with decimal comma, preview placeholder dialog. No actual replacement, candidate filtering, nutrition calculation or persistence. No suitability claims. Empty/loading/error/unknown-allergen states block next; allergy rules remain hard constraints.
- Decisions: scope is alternative choice/personal portion only per plan§16 and meal-customization§5–7; detailed replacement before/after is a later unit. No invented calories/macros/cooking durations.19 stays unchanged; entry integration not claimed.
- Relevant files: onboarding-original-style/{20-alternative-meal.html,alternative-meal.css,alternative-meal.js,render-alternative-meal.mjs,20-alternative-meal-*.png,20-alternative-meal-render-report.json}; docs/qa/visual-review-plan.json contains requirements and inspected hashes.
- Checks performed: seven fresh390px Chrome renders opened; zero horizontal overflow, missing images or targets under48px. JS syntax/diff whitespace pass; existing palette contrast11.95/5.36/4.71. No commit/merge/CI/push/APK; no native/device/EN/RU/text scaling/TalkBack or end-to-end data validation. Native debt unchanged.
- Remaining / exact next step: user reviews20 normal and selected PNGs; adjust requested differences only. After explicit approval/request, continue next logical unit in new chat (likely reciprocal day/meal chooser per new-flows-guide31); re-read current source and plan. Do not silently skip replacement preview requirement. No Git delivery authorized.

20 icon correction — user requested icons left of other meals. Replaced generic glyphs with distinct48px token-coloured SVG bulgur, soup and roast-vegetable dish icons; all seven renders refreshed, changed normal/selected/preview opened with no clipping. Render metrics and JS syntax/diff whitespace pass. Approval pending; no commit/merge/CI/push/APK. Next: await corrected20 approval.

20 final approval — 2026-11-06: user approved corrected alternative selection with distinct food icons on the left (“okeydir”). Preserve20. No next screen requested; wait for explicit continuation. No commit/merge/CI/push/APK performed. Real filtering, calculation, persistence and detailed replacement preview remain unimplemented.

## Phone21 reciprocal day/meal chooser — 2026-11-06
- Completed:20 approved; user requested next. Prepared21-day-swap.html, day-swap.css/js, render-day-swap.mjs and six fresh PNGs on existing feat/onboarding-original-style in callmood-onboarding-parent. Earlier drafts preserved.
- Current state:21 awaiting user approval. Compact approved16–20 style, left dish icons, future date selection and meal-slot cards, selected reciprocal direction summary, sticky both-days-preview action. Default date is an illustrative tomorrow; no meal preselected. Empty/loading/error states block next; unknown-allergen row disabled. Future-only selector excludes past/current source; fixtures contain no consumed selectable targets. Actual status/date policy not implemented. No visible USDA.
- Decisions: reciprocal swap distinct from replacement/move; empty day cannot be swap target. Both-day before/after calculation and atomic confirmation are explicitly future-stage placeholder dialog. No real DB/account nutrition, suitability, writes or history editing. Day8 demo reuses meal fixtures; not a real weekly menu.
- Relevant files: onboarding-original-style/{21-day-swap.html,day-swap.css,day-swap.js,render-day-swap.mjs,21-day-swap-*.png,21-day-swap-render-report.json}; docs/qa/visual-review-plan.json; plan§16, meal-customization§4/6/8, new-flows-guide31–32.
- Checks: all six390px Chrome state images opened against approved20/19; zero horizontal overflow/missing images/measured targets under48px. JS syntax, diff whitespace, existing palette contrast11.95/5.36/4.71 pass. No commit/merge/CI/push/APK, native/device/TalkBack/EN/RU/scaling, real date/status validation, atomic transaction or integration with19. Native visual debt remains open.
- Remaining: user approval of21; real replacement preview from20 and reciprocal before/after preview remain outstanding.
- Exact next step: show21 normal PNG, await approval and adjust requested differences only. Continue in a new chat after this small unit; read this tail and scoped files, do not begin another large unit here unless user explicitly chooses to continue. No Git delivery authorized.

21 final approval — 2026-11-06: user approved day/meal reciprocal swap chooser (“okeydir”). Preserve21 and earlier approved screens. No next unit requested; wait for explicit continuation. Next likely unit is both-day before/after swap preview per new-flows-guide32; replacement preview from20 also remains outstanding. No commit/merge/CI/push/APK performed.


## Phone22 reciprocal swap preview draft — 2026-11-06
- Completed: read21 final approval; prepared22-swap-preview.html, swap-preview.css/js, render-swap-preview.mjs and eight390px AZ PNGs on existing feat/onboarding-original-style in callmood-onboarding-parent. Preserved approved21 and prior drafts. workflow start rejected dirty task-owned branch; continued existing scope. No commit/merge/CI/push/APK.
- Current state:22 pending user approval. Compact approved16–21 style; reciprocal before/after meal cards with personal portions, day/week energy, collapsed macro/shopping/preservation/validation details, sticky atomic confirmation concept. Synthetic numbers only; no DB recompute, suitability claim, actual writes or21 integration. Same-week unchanged servings imply unchanged weekly/shopping fixture. Missing nutrient propagates to incomplete week; allergen/conflict/loading/error block confirmation. Confirmation opens explicit no-write dialog.
- Checks: all eight fresh PNGs opened against approved21/18; no overflow/missing images/targets under48px. JS syntax, diff whitespace and token contrast11.95/5.04/4.71 pass. Source/image hashes in visual-review-plan.json. Native/device/TalkBack/EN/RU/scaling, real validations, atomic persistence, undo and end-to-end acceptance unverified; native debt unchanged.
- Relevant files: onboarding-original-style/{22-swap-preview.html,swap-preview.css,swap-preview.js,render-swap-preview.mjs,22-swap-preview-*.png,22-swap-preview-render-report.json}; plan§16; meal-customization§4/6/8/T05–06/08–12.
- Remaining: user visual approval of22. Detailed replacement preview from20 remains outstanding.
- Exact next step: show22 normal PNG and wait for approval; adjust requested differences only. No further unit or Git delivery before request.

22 final approval — 2026-11-06: user approved reciprocal both-day before/after preview ("okeydir"). Preserve22 and earlier approved screens. No next unit requested; wait for explicit continuation. Detailed replacement preview from20 remains outstanding. No commit/merge/CI/push/APK performed for approval; real DB validation, atomic persistence, undo and native acceptance remain unverified. Exact next step: await new user request.


## Phone23 replacement preview draft — 2026-11-06
### Completed
- User requested next after22 approval. Prepared23-replacement-preview.html, replacement-preview.css/js, render-replacement-preview.mjs and nine390px AZ PNGs. Preserved approved20–22 and all prior drafts. No commit/merge/CI/push/APK.
### Current state
-23 awaiting user approval on feat/onboarding-original-style in /Users/mac/My Workspace/callmood-onboarding-parent. workflow start rejected dirty task-owned branch; continued scoped review draft with earlier no-delivery restriction.
### Decisions
- Next logical unit closes outstanding20 replacement preview (new-flows-guide43), distinct from reciprocal swap22. Approved compact style/shared CSS/tokens reused; fixed illustrative1 personal portion, synthetic meal nutrients544→420kkal and consistent −124 day/week delta; macro and shopping projections in disclosure. No real DB recompute, suitability, recipe source claim, writes or20→23 integration. Existing20 placeholder preserved.
- Missing calories/protein propagate to incomplete day/week; allergen, plan conflict, consumed/past, loading/error block confirmation. Real save/undo/version/idempotency are not implemented; confirm is explicit no-write concept dialog. Remaining meals, targets, intake and base recipes preserved in copy. No visible USDA.
### Remaining work
- User review/approval of23; adjust requested differences only. Real nutrition/persistence/native implementation outside design scope. Further unit only after explicit approval/request; no exact numbered next roadmap established.
### Relevant files
- onboarding-original-style/{23-replacement-preview.html,replacement-preview.css,replacement-preview.js,render-replacement-preview.mjs,23-replacement-preview-*.png,23-replacement-preview-render-report.json}; shared change-preview.css, swap-preview.css; approved20/22 references; docs/qa/visual-review-plan.json; plan§16, meal-customization§2/5–9, new-flows-guide43.
### Checks performed
- All nine fresh Chrome APK-free renders opened against20/22. Zero horizontal overflow, missing images, measured controls under48px. JS syntax, git diff whitespace and existing token contrast11.95/5.04/4.71 pass. Image/source hashes recorded as draft evidence only. Native/device/TalkBack/EN/RU/text scaling, DB validation, persistence, undo and end-to-end integration remain unverified; native visual debt unchanged. CI not run, no merge performed.
### Exact next step
- Show23 normal PNG and wait for user approval. On continuation, read this section and scoped23 sources; preserve no Git delivery/APK restriction. Context warning issued after two design units in this chat; recommend new chat before another large unit.

23 final approval — 2026-11-06: user approved replacement preview ("okeydir"). Preserve23 and all earlier approved designs. Detailed replacement concept preview from20 is now approved; real20→23 integration remains unimplemented. No next unit requested; wait for explicit continuation in a new chat. No commit/merge/CI/push/APK performed for approval. Real DB recompute, validation, persistence, undo and native acceptance remain unverified. Exact next step: read this tail in a new chat, then determine the next scoped unit from current plan and user request; do not assume a numbered roadmap.

## Phone24 updated plan / undo draft — 2026-11-06
- Completed: read23 final approval; prepared next scoped unit per plan§16.5, meal-customization§6/8 T12 and new-flows41. Standalone24-plan-change-result.html, plan-change-result.css/js, render-plan-change-result.mjs; seven fresh390px AZ renders opened against approved22/23. Earlier approved drafts unchanged.
- Current state:24 awaits user approval on feat/onboarding-original-style in callmood-onboarding-parent. workflow start rejected existing uncommitted drafts; continued task-owned branch preserving explicit draft-only/no-delivery instruction. No commit/merge/CI/push/APK.
- Decisions: compact hero/cards/disclosure/sticky undo. Normal replacement and reciprocal two-day result are explicitly synthetic after-state fixtures. Last operation/selected slots shown; shopping and undo must restore together; marks, other meals, target, base recipe and intake preserved. Later FoodLog blocks automatic undo and exposes separate diary review. Actions show no-write concept dialogs; no real persistence, undo, recompute, server sync or22/23→24 integration.
- Relevant files: onboarding-original-style/{24-plan-change-result.html,plan-change-result.css,plan-change-result.js,render-plan-change-result.mjs,24-plan-change-result-*.png,24-plan-change-result-render-report.json}; docs/qa/visual-review-plan.json records requirements/source/image hashes.
- Checks: normal/swap/details/conflict/loading/error/undo all rendered and inspected; zero horizontal overflow, missing art or visible targets below48px. JS syntax, whitespace and token contrast11.95/5.04/4.71 pass. No native/device/TalkBack/EN/RU/text scaling, DB validation, idempotency or persistence verification. Native visual debt unchanged.
- Remaining / exact next step: show24 normal render, await approval; adjust requested differences only. No further screen or Git delivery before approval/request.

24 final approval — 2026-11-06: user approved updated plan / undo design (“okeydir”). Preserve24 and all earlier approved concepts. No next unit requested; wait for explicit continuation. No design source changes, commit/merge/CI/push/APK for approval. Real persistence, atomic undo/shopping restoration, DB recompute, later-FoodLog conflict validation and native acceptance remain unimplemented/unverified. Exact next step: await new user request; determine the next scoped unit from the current plan rather than assuming a numbered roadmap.

## Phone25 recipe-linked photo question — 2026-11-06
- Completed:24 approved; user requested next. Prepared25-recipe-photo-question.html, recipe-photo-question.css/js, render-recipe-photo-question.mjs and eight AZ390px renders. Earlier drafts unchanged; compact style/shared tokens reused per ui-craft.
- Current state:25 awaiting approval on feat/onboarding-original-style in callmood-onboarding-parent. workflow start rejected existing dirty task-owned branch; draft-only/no-delivery restriction preserved. No commit/merge/CI/push/APK.
- Decisions: plan§16.8, meal-customization§10–11/T13–16, new-flows33. Explicit recipe-linked illustration fixture, not uploaded photo; no default answer. No→personal portion; Yes→latest served recipe including prior customization. Both destinations are future-unit dialogs. Unknown recipe blocks continue; different-meal escape and unfinished exit explain no writes. No storage or actual camera/model/nutrition/diary operations.
- Relevant files: onboarding-original-style/{25-recipe-photo-question.html,recipe-photo-question.css,recipe-photo-question.js,render-recipe-photo-question.mjs,25-recipe-photo-question-*.png,25-recipe-photo-question-render-report.json}; docs/qa/visual-review-plan.json. References approved24/23, original15 recipe photo placeholder.
- Checks: normal/yes/no/unknown/next/cancel/details/yes-next rendered and opened; zero horizontal overflow/missing art/targets below48px. JS syntax, whitespace and contrast11.95/5.04/4.71 pass. No native/device/TalkBack/EN/RU/text scaling, real recipe-version binding, photo consent/storage, persistence or end-to-end verification. Native visual debt unchanged.
- Remaining: user approval of25; adjust requested differences only. Next major unit only on approval/request, likely actual prepared-recipe editor or personal portion; do not assume numbering.
- Exact next step: show25 normal PNG and await approval. Context warning after24+25: continue any next large unit in new chat with this section, plan§16 and meal-customization§10–11; preserve all uncommitted drafts/no Git delivery/APK.

## Design snapshot and next branch — 2026-11-06
- User explicitly requested committing all current design work and a new branch from the parent; clarified parent is feat/onboarding-original-style, not main.
- Snapshot scope: existing12–23 drafts/approvals plus approved24, pending25 and review/context evidence.25 is NOT approved by this Git request.
- Continue on codex/recipe-photo-next, created from the committed parent snapshot. No main merge, CI, push or APK requested/performed. This is a commit/branch checkpoint, not implementation completion.
- Exact next step: review25 with user; only after approval/request prepare next scoped photo/recipe unit per plan§16.8 and meal-customization§10–11. Preserve compact style and render-before-approval workflow. Context warning remains: use a new chat for the next large design unit.


## Native tablet UI implementation — 2026-11-06
- Worktree `/Users/mac/My Workspace/callmood-tablet-ui`, branch `feat/onboarding-tablet-ui`, base `87e054b`. Approved tablet sources are `99fa3db`; all 11 groups are present. Other worktrees and original designs remain untouched; no merge/push/APK is authorized at this stage.
- Implemented native portrait/landscape layouts for all eleven groups and Today, reusing the phone PreviewState/saver, localized resources and supplied transparent artwork. Debug Application remains plain/offline with FirebaseInitProvider absent; backend/domain/release code unchanged.
- Constraint-aware composition preserves the phone path below 600dp, uses tablet landscape panels when >=1000dp wide, and reflows measurement cards if their controls would not fit. Actual window orientation, not device identity, drives layout. IME padding is inside mode selection.
- Added 64 stable-state fixtures, 768 orientation/locale/font cases plus 588 scroll/CTA renders; prototype-only next/guest placeholders now navigate to actual implemented screens. Ready with missing meal preferences deliberately retains existing safe diary behavior instead of the prototype's sample-menu fixture. See `docs/ui/onboarding-tablet.md` for exact adaptations and Android Studio folder.
- A local QA-generation script had an append-while-iterating error; Mac rebooted at 15:18:25 Asia/Baku. Its recorded PID and the interrupted test PID no longer existed after reconnect. No unrelated process was killed. Registry remained valid and was repaired using an immutable iteration tuple plus a separate extra_cases list, bounded assertions and atomic replacement.
- Final technical/visual evidence is recorded in `docs/ui/onboarding-tablet-review.md` after checks complete. The interrupted run 893915 is NOT a passing result. Earlier complete run had an unchanged FoodEntryUiTest timeout; no test timeout or gate was weakened.
- Human visual acceptance remains pending; inherited original-style mapping debt must not be erased. No `review-visual` or workflow finish is performed in this stage. Native Robolectric PNGs are not device screenshots.
- Final-source check 887381 passed at 15:44:13 Asia/Baku: Kotlin compilation, lint, 241 JVM tests (51 suites, zero failures/errors/skips), 524 keys per AZ/EN/RU and token/contrast checks. Current 492 phone render hashes still equal the preserved baseline. Visual registry validation passes 2,520 cases including 1,356 tablet cases; inherited 218 original-style mapping debt remains unresolved. No visual acceptance is recorded.
- Native implementation committed locally as `817c1e545cb4eeaf0dd60f7dac14ac8101fbe686`. QA/tests/docs are committed separately with normal hooks. Earlier box sheets are archived as pre-final-refinement evidence, not latest; final-source transfer/comparison remains pending.
- User subsequently authorized commit and a merge ONLY into feat/onboarding-original-style, preserving all target changes. Exact next step: read-only target/ref/worktree audit and preservation hashes; if dirty, concurrent or conflicted STOP. If clean use existing approved `finish --target feat/onboarding-original-style --target-worktree ...`. No main/tablet merge, push, APK, force, stash or gate weakening.

## Authorized parent merge continuation — 2026-11-06
Both histories above are retained: phone 12–25 design snapshot `13e1bf19` and its
ongoing `codex/recipe-photo-next` work, plus native tablet source `c4ea905`
(UI `817c1e5`, QA/tests `c4ea905`). The user now authorizes this merge ONLY into
`feat/onboarding-original-style`, superseding earlier stage-specific no-merge notes
for this operation. Existing design approvals/pending states are unchanged.
Final-source compile/lint/241 JVM tests passed at 15:44:13 Asia/Baku; this is not
post-merge CI or visual acceptance. Inherited visual debt and human review remain
pending. No main merge, push, APK, backend change or loss of either history is authorized.

25 final approval — 2026-11-06: user explicitly approved the recipe-linked photo question ("okeydir") after reviewing the normal render. This supersedes pending25 status above. Preserve25 and all earlier approved concepts on codex/recipe-photo-next. No next unit requested; wait for explicit continuation. No design source changes, commit/merge/CI/push/APK performed for this approval. Real recipe-version binding, photo processing, personal portion/editor destinations, persistence and native acceptance remain unimplemented/unverified. Exact next step: upon a new request, choose one scoped photo/recipe unit from plan§16.8 and meal-customization§10–11, retain approved compact style, render and await user approval.


## Phone26 prepared-recipe editor draft — 2026-11-06
- Completed: after25 approval user requested next. Prepared26-prepared-recipe.html, prepared-recipe.css/js, render-prepared-recipe.mjs and eleven390px AZ state PNGs plus viewport. Reused approved16 ingredient-editor CSS/rows and25 compact illustrated context without changing approved sources.
- Current state:26 awaits user approval on codex/recipe-photo-next in callmood-onboarding-parent. workflow start rejected task-owned dirty context; continued existing task branch. Prior draft-only delivery restriction retained. No commit/merge/CI/push/APK.
- Decisions: plan§16.8, meal-customization§1/10–11 T14–17, new-flows34. Latest served recipe including prior customization represented explicitly by synthetic oil12.5g/recipe portion. Single vs pot switch, prepared servings, remove/add/change grams. Added-product chooser is an explicit illustrative modal, not real DB search. Reset preserves prior customization. Actual meal only; no replacement/day swap. Personal consumed share is next unit, currently no-write dialog. No nutrition recompute, real photo/version binding, diary/plan/shopping writes or25→26 integration.
- Checks: normal/batch/changed/removed/add/invalid/empty/unknown/cancel/next/details PNGs opened against16/25; viewport inspected. Corrected negative input/unknown identity and reopened fresh renders. Zero horizontal overflow/missing images/measured controls under48px. JS syntax, render-state assertions and token contrast11.95/5.04/4.71 pass; source/image hashes in visual-review-plan.json as draft evidence only. Native/device/TalkBack/EN/RU/scaling, real ingredient search, calculations, validation, persistence and end-to-end behavior remain unverified. Native visual debt unchanged.
- Relevant files: onboarding-original-style/{26-prepared-recipe.html,prepared-recipe.css,prepared-recipe.js,render-prepared-recipe.mjs,26-prepared-recipe-*.png,26-prepared-recipe-render-report.json}; shared ingredient-editor.css, recipe.css/style.css; docs/qa/visual-review-plan.json; plan§16 and meal-customization§10–11.
- Remaining / exact next step: show26 normal render and await user approval; adjust requested differences only. No next screen or Git delivery without request. Next likely scoped unit is prepared vs consumed portion, but do not assume a numbered roadmap.

26 final approval — 2026-11-06: user approved prepared-recipe editor and explicitly requested next. Preserve26 and earlier designs; prepare one scoped prepared-versus-consumed portion concept, then render and await approval. No Git delivery/APK authorized.


## Phone27 prepared / consumed portion draft — 2026-11-06
### Completed
- User approved26 and requested next. Prepared27-consumed-portion.html, consumed-portion.css/js, render-consumed-portion.mjs and13 fresh AZ390px state PNGs plus viewport. Earlier approved25/26 sources preserved. Shared tokens and compact UI reused with ui-craft.
### Current state
-27 awaits user approval on codex/recipe-photo-next in callmood-onboarding-parent. workflow start rejected dirty task-owned branch; continued existing scope preserving draft-only/no-delivery restriction. No commit/merge/CI/push/APK.
### Decisions
- Plan§16.8; meal-customization§10–11 T13/T17; new-flows35. Same concept for Yes/No recipe branches. Prepared4 is an explicitly synthetic fixture; eaten amount starts empty. Portion presets/custom decimals or cooked-weight ratio. Weight needs separately measured total cooked yield; raw ingredient total is never cooked yield. Estimated checkbox marks uncertainty without demanding an invented exact value. Prepared count partitions fixed cooked recipe rather than silently scaling ingredient grams. No nutrition computation; future preview is explicit no-write dialog. No actual25/26 integration, storage, plan/diary/shopping writes.
### Remaining work
- User review/approval of27; requested adjustments only. Real served-version binding, calculations, photo/persistence/idempotency and end-to-end integration outside concept scope. Future result preview only after approval/request.
### Relevant files
- onboarding-original-style/{27-consumed-portion.html,consumed-portion.css,consumed-portion.js,render-consumed-portion.mjs,27-consumed-portion-*.png,27-consumed-portion-render-report.json}; shared style.css,recipe.css,ingredient-editor.css,change-preview.css; docs/qa/visual-review-plan.json; plan§16,meal-customization§10–11.
### Checks performed
- Normal/unchanged-half/half/one/grams/weighed/estimated/invalid/excess/unknown/next/cancel/details and viewport rendered and opened. Zero horizontal overflow/missing images/measured visible controls below48px. Renderer assertions verify half→12.5%,one→25%,300/1200g→25%, invalid/empty/unknown gating and dialogs. JS syntax, whitespace, token contrast11.95/5.04/4.71 passed. Draft source/image hashes recorded. Native/device/TalkBack/EN/RU/scaling, real nutrient computation, persistence, repeat-confirm and full integration unverified. Native visual debt unchanged. CI not run, no merge.
### Exact next step
- Show27 normal or one-selected PNG and wait for user approval. Adjust only requested differences. Context warning after26+27: recommend new chat before another large unit; preserve branch/uncommitted drafts/no Git delivery/APK. Continuation: read this tail, follow27 approval state, plan§16 and meal-customization§10–11; retain compact style, render and wait for approval.

27 final approval — 2026-11-06: user explicitly approved the prepared-versus-consumed portion concept ("okeydir"). This supersedes pending27 status above. Preserve27 and earlier approved designs on codex/recipe-photo-next. No next unit requested; wait for explicit continuation, preferably in a new chat following the existing context warning. No design source changes, commit/merge/CI/push/APK performed for approval. Real version binding, nutrition calculation, persistence/idempotency, native acceptance and25/26→27 integration remain unimplemented/unverified. Exact next step: upon a new request, read this tail and choose the next scoped unit per plan§16 and meal-customization§10–11 (likely actual-log result preview); retain compact style, render and await approval.

Design commit checkpoint — 2026-11-06: user explicitly requested commit. Include approved26/27 standalone concepts, render evidence, visual-review plan and25–27 approval/context notes on codex/recipe-photo-next. No merge, CI, push or APK for this checkpoint. Checks: JS/render-script syntax, source/image SHA-256 parity and diff whitespace. Prior runtime/native limitations unchanged. Exact next step: await a new scoped request, preferably in a new chat.

Commit branch compatibility — 2026-11-06: local pre-commit rejected codex/ prefix. Renamed the same current branch to feat/recipe-photo-next, preserving its history and task changes; no hook bypass or main merge. Use feat/recipe-photo-next for continuation.

## Canonical design consolidation — 2026-11-06
Approved phone26–27 and25–27 approval records are promoted from exact owner commit `068193452fb0ce8bf09b5d2c5117afcafa667250`, retaining the native tablet/context history. The active recipe checkout is not changed. This promotion is design content consolidation, not new UI implementation or visual acceptance. Legacy archive/removal follows only after independently verified remote backup.

## Design archive / canonical cleanup handoff — 2026-11-06
- Completed: exact approved26–27 files promoted via5412b690 and canonical merge6fe3f286; both native and ongoing design context/QA histories preserved. All36 new source files match owner0681934 bytes. Owner checkout remains unchanged.
- Promotion post-merge CI: technical checks passed; visual gate pending (no acceptance fabricated). No APK.
- Public archive pushed ONLY to chore/archive-design-variants-2026-11-06: root/orphan commit afd9fa1230b0d32c8c22e4bacb92bcbdbf8fd006, zero code-history parents;397 design-version entries,400 tracked files. GitHub API and normal git ls-remote independently match that SHA; all payload SHA-256 hashes verified.
- Secret scan:385 unique selected blobs,1120 total recursive members,601 PNG metadata payloads,512 text payloads and7 ZIP containers; no pattern alerts. Not OCR or universal secret clearance. One historical prompt's exact original EOF bytes are losslessly ZIP-contained rather than normalized or weakening whitespace hooks.
- Current state: chore/canonical-design-cleanup in callmood-design-consolidation; no legacy files removed yet. Public MANIFEST.json records original path, source refs/commit, blob/SHA-256 and restore instructions. Remote proof is excluded .local-ci/design-archive-remote-proof.json.
- Decisions: keep active phone439/tablet223 files, current shared tokens+3guides; archive249 current legacy files plus148 historical versions. No other existing worktree/branch, Studio config, runtime asset or code module is authorized for removal. Canonical index is design-concepts/README.md.
- Remaining: safely migrate historical-reference IO before removing249 files. Current756 native visual cases reference3 archived PNGs;220 archive candidate files are required by proposal hashes. Update android/tools/visual_gate.py reference resolution (including review validation), planning/build_design_requirement_map.py SHA/inventory IO and legacy extract_design_art.py. Use pinned archive commit+manifest hashes, strict allowed paths and exact source bytes; materialize only in temporary/ignored evidence space, never restore obsolete files into active source. Preserve all review cases, native gates, old debt and human acceptance requirements.
- Exact next step: implement/test that hash-bound resolver on this isolated cleanup branch, update historical-only reference metadata, remove only manifest's249 current candidates after checking the existing remote proof again, run focused tooling regressions, then mandatory full post-merge checks once via sanctioned target workflow. No main/current-code push; archive branch is the sole authorized push. Do not fix unrelated UI or inherited visual debt.

## Canonical design cleanup — latest scope/status,2026-11-06
- Supersedes earlier cleanup handoff/removal limits: user additionally authorized one project folder at /Users/mac/My Workspace/callmood, branch feat/onboarding-original-style; retain every Git branch. Extra CALLMOOD workfolders may be removed only after clean-state, detached-anchor, meaningful ignored/untracked backup and active-writer audits. Never kill another job or overwrite/discard user data.
- Removed exactly249 legacy current design sources after verifying archive afd9fa1230b0d32c8c22e4bacb92bcbdbf8fd006 independently on remote and checking every original byte against committed MANIFEST SHA-256/blob/size. All439 approved phone files (including26–27),223 tablet files, shared tokens+3guides and runtime assets remain unchanged; design-concepts/README.md is the sole current design index.
- Historical-reference resolver is offline and pinned to exact archive+manifest hashes; checks every restored byte and permits only manifest-listed old design sources. Temporary materialization is outside active project sources; active/current missing references never fall back to archive. Visual cases, old proposal hashes, human review requirements and inherited pending debt are retained, not accepted.
- Focused archive regression8 tests, visual-gate3 tests, existing visual workflow and target safety workflows, requirement-map parity and AZ/EN/RU token checks passed before removal. Final post-removal checks and mandatory post-merge CI are still to be recorded from their actual results, not assumed.
- New standing test rule: small changes use focused relevant tests; full verification only at task end, mandatory CI after merge. No new APK, backend action or code-branch push. The design-only orphan archive is the sole authorized public push.
- Earlier sections are historical chronological records, not current design alternatives or permission to resurrect removed variants. One-folder consolidation remains in progress until every removal is audited/backed up; blocked active-writer folders must be reported rather than forced.


## Phone shopping concept — 2026-11-06
- Branch feat/phone-shopping-design started from main in the single authorized folder. Existing Android Studio files preserved and locally excluded.
- Added 28-shopping.html with shopping.css/js and render-shopping.mjs under canonical onboarding-original-style. Seven AZ states: normal, marked, updated, empty, loading, error, share; existing references unchanged.
- Weekly total quantities; separate home/bought flags; unique ready count; explicit menu deltas, retained no-longer-needed purchased milk, demo undo and share preview. Synthetic fixtures only; no domain computation, persistence or system sharing.
- Opened all seven fresh renders against14-weekly/15-recipe. No overflow/missing art/targets below48px. JS syntax and contrast11.95/5.04/4.71 pass. Draft hashes in visual-review-plan.json. User design approval pending; native/device/localization/scaling acceptance unverified, inherited native debt unchanged.
- Exact next step: user reviews normal/updated concepts; adjust requested differences. Mandatory local merge CI result must be read from .local-ci/logs/latest.json; never infer native acceptance from static renders. No APK or remote action.


## Phone budget / unknown-price concepts — 2026-11-06
- Completed: standalone29 budget-no-menu and30 unknown-price, plus stale-price variant on feat/phone-budget-states. Existing approved references untouched.
- Decisions: plan§17.8–10; no silent limit increase, explicit repeat consent, no numeric total/budget badge for missing or stale prices. Synthetic lentil quantity; dialogs disclose prototype limitations.
- Checks: all three fresh390px AZ PNGs opened against14 weekly/28 shopping; zero overflow/missing art/targets below48px. JS syntax and contrast11.95/5.04/4.71 pass. Draft source/image hashes in visual-review-plan.json.
- Relevant: onboarding-original-style/{29-budget-no-menu.html,30-price-unknown.html,budget-states.css/js,render-budget-states.mjs,budget-states-*.png}.
- Remaining: user design approval; native/device/TalkBack/EN/RU/scaling and real generation/prices/persistence unverified. Native debt unchanged. Local CI is explicitly paused in .local-ci/README.md; do not bypass flag. No APK or remote action.
- Exact next step: review the two main concepts and adjust requested differences only.

## Native part 1 already on main — 2026-11-06
- User asked for a new branch from main and the eleven-item onboarding code list (welcome through fake Today, phone/tablet, AZ/EN/RU, tests and visual compare).
- No branch and no code change: that flow is already on main (`e83ffee`) as debug `OnboardingPreviewActivity`. Checkout stayed on `feat/phone-budget-states` (clean). CI remains paused; no APK.
- Debug manifest makes the preview the launcher and skips Firebase. Release `MainActivity` still uses the older K07 shell and real nutrition preview, not this labelled fixture.
- Goal screen keeps the fourth plan choice, Sağlam yemək, in addition to lose/maintain/gain. Ready → Today uses the labelled 1 900 kkal fixture; diary/risk paths hide it.
- Tests already present: `OnboardingPreviewTest`, `TabletOnboardingInteractionTest`, `TabletOnboardingRenderTest`. Native visual acceptance against approved designs remains open debt; this note is not a review.
- Exact next step: only if requested, branch from main and switch the release first-run to this preview without dropping K07–K16 domain code. Do not reimplement the eleven screens.

## Price-design cancellation — 2026-11-06
- User explicitly cancelled product-price display: no unknown/stale-price screens needed. Removed30 and both price PNGs; retained29 budget-no-menu unchanged. This supersedes the price-design scope above. Product decision for future implementation; no domain/price engine change requested or made.

## General-plan price-display cancellation — 2026-11-06
- Completed: canonical research-data.json§17/18/19/21 and coding-tasks K14/K17 now exclude product-price/line-cost/total-cost display, missing/stale-price UI and recovery flows, including sharing. No deferred feature. Budget selection/no-feasible-menu and internal cost safety remain; subscription prices unaffected.
- Derived MD/TXT/canvas/PDF regenerated together. Export rerun byte parity and embedded canvas JSON equality pass; diff whitespace passes. Opened PDF pages20/21/23/25/29 covering changed text. No app code/APK/device checks. CI remains explicitly paused; no merge.
- Relevant: planning/research-data.json, planning/coding-tasks.txt, library/tools/update_project_plan.py, planning/build_plan_pdf.py, docs/nutrition-plan-design.md.
- Remaining: actual runtime alignment/native acceptance unverified; no application changes requested. Existing unrelated native-part1 context edit preserved unstaged.
- Exact next step: await next scoped request; recommend new chat before another substantial unit.

## Phone part2 Add chooser draft — 2026-11-06
- Completed: started feat/phone-add-log-design (workflow start blocked by dirty tree; branch created with git switch -c). Prepared unit1 only: 30-add-meal.html Add chooser. Four AZ390px PNGs (normal/photo/search/previous) plus viewport.
- Current state: awaiting user approval. Approved11–13 Today and later14–29 concepts preserved. Remaining part2 units (search, previous, free photo, portion, success, edit/delete) not started.
- Decisions: plan§18.11/§19.4 three routes; existing tokens/Today nav/transparent art; destinations are explicit no-write future dialogs; no FoodLog writes. Number30 reused after cancelled price screen.
- Relevant files: onboarding-original-style/{30-add-meal.html,add-meal.css,add-meal.js,render-add-meal.mjs,30-add-meal-*.png,30-add-meal-render-report.json}; docs/qa/visual-review-plan.json; plan§18.11/§19.4.
- Checks: all four renders + viewport opened vs13-today-empty; zero overflow/missing art/targets under48px; JS syntax; contrast11.95/5.04/4.71; whitespace pass. CI paused; no commit/merge/push/APK. Native acceptance unverified.
- Exact next step: user reviews Add chooser; adjust if requested. After approval, prepare next unit (yemək axtarışı) preferably in a new chat. Do not start remaining units without request.

30 final approval — 2026-11-06: user approved Add chooser (“okeydir novbetiye kec”). Preserve30. Next unit: phone food search for logging (not ingredient 17).

## Phone part2 food search draft — 2026-11-06
- Completed: unit2 31-food-search on feat/phone-add-log-design. Eight AZ390px PNGs: normal/selected/empty/loading/error/manual/invalid/next + viewport. Approved30 preserved.
- Decisions: plan§19.4 food logging search (not ingredient17); clear query; cooked/raw variants; unknown energy empty≠0; no USDA; select→portion placeholder no FoodLog write; loading/error/empty explicit.
- Relevant: onboarding-original-style/{31-food-search.html,food-search.css,food-search.js,render-food-search.mjs,31-food-search-*.png}; docs/qa/visual-review-plan.json; plan§19.4.
- Checks: eight renders opened vs30; zero overflow/missing art/targets under48px; JS syntax; contrast11.95/5.04/4.71; whitespace pass. CI paused; no commit/merge/push/APK.

31 final approval — 2026-11-06: user continued with next unit “Əvvəl yediklərim” (implicit approval of31). Preserve31. No Git delivery for approval.

## Phone part2 previous meals draft — 2026-11-06
- Completed: unit3 32-previous-meals on feat/phone-add-log-design. Seven AZ390px PNGs: normal/selected/empty/loading/error/invalid/next + viewport. Approved30–31 preserved.
- Current state: awaiting user approval. Remaining part2: free photo, portion, success, edit/delete.
- Decisions: plan§19.4 / K10; dated history rows; selecting drafts a NEW today log with confirmed portion; old date is history-only and not copied; unknown energy never zero; selection/no-write until portion; empty/loading/error distinct; portion is explicit no-write dialog. Synthetic fixtures only. No USDA. No FoodLog writes.
- Relevant: onboarding-original-style/{32-previous-meals.html,previous-meals.css,previous-meals.js,render-previous-meals.mjs,32-previous-meals-*.png}; docs/qa/visual-review-plan.json; plan§19.4; coding-tasks K10.
- Checks: seven renders + viewport opened vs31; zero overflow/missing art/targets under48px; JS syntax; contrast11.95/5.04/4.71; renderer asserts selected/invalid/empty/loading/error/next. CI paused; no commit/merge/push/APK. Native/device/EN/RU/TalkBack/persistence unverified; native debt unchanged.
- Exact next step: user reviews previous-meals; adjust requested differences only. After approval, next likely free-photo route — preferably new chat (context growth after30–32). No Git delivery without request.

32 final approval — 2026-11-06: user approved previous meals (“okeydir”). Preserve32 and approved30–31. Real history query, portion confirmation, FoodLog writes and native acceptance remain unimplemented/unverified.

## Phone part2 free photo draft — 2026-11-06
- Completed: user requested next (“Kecdigerine”). Prepared unit4 33-photo-log on feat/phone-add-log-design. Eight AZ390px PNGs: normal/permission/recognizing/candidate/oil/unrecognized/invalid/next + viewport. Approved30–32 preserved.
- Decisions: plan§19.4 / K23; illustrative frame not user photo; gallery/capture/torch; permission→search; candidate + Başqa yeməkdir; oil/sauce No/Yes with grams; unrecognized retake/search/manual; none auto-create FoodLog; portion no-write dialog. No real camera/model/writes.
- Relevant: onboarding-original-style/{33-photo-log.html,photo-log.css,photo-log.js,render-photo-log.mjs,33-photo-log-*.png}; docs/qa/visual-review-plan.json.
- Checks: eight renders opened vs30; zero overflow/missing art/targets under48px; JS syntax; contrast11.95/5.04/4.71; renderer asserts. CI paused; no commit/merge/push/APK. Native/device/EN/RU/TalkBack/real recognition unverified.

33 final approval — 2026-11-06: user approved free photo log (“okeydir”). Preserve33 and approved30–32. Real camera/model/FoodLog and native acceptance remain unimplemented/unverified.

## Phone part2 portion confirm draft — 2026-11-06
- Completed: after33 approval prepared unit5 34-portion-confirm on feat/phone-add-log-design. Eight AZ390px PNGs: normal/grams/unit/no-photo/unknown/invalid/estimated/next + viewport. Approved30–33 preserved.
- Current state: awaiting user approval. Remaining part2: success, edit/delete.
- Decisions: plan§19.4 / §18.11 and meal-customization portion rules; Az/Orta/Çox only when measured photos exist for that recipe (plov 180/280/380 sample); otherwise grams or known unit conversion (egg 1≈50q); unknown energy never zero; estimate checkbox; confirm opens no-write dialog (success is next unit). Synthetic fixtures only. No FoodLog writes.
- Relevant: onboarding-original-style/{34-portion-confirm.html,portion-confirm.css,portion-confirm.js,render-portion-confirm.mjs,34-portion-confirm-*.png}; docs/qa/visual-review-plan.json.
- Checks: eight renders + viewport opened vs33; zero overflow/missing art/targets under48px; JS syntax; contrast11.95/5.04/4.71; renderer asserts photos/unit/no-photo/unknown/invalid/next. CI paused; no commit/merge/push/APK. Native/device/EN/RU/TalkBack/persistence unverified; native debt unchanged.
- Exact next step: user reviews portion confirm; adjust requested differences only. After approval, next likely success screen — preferably new chat. No Git delivery without request.

34 final approval — 2026-11-06: user approved portion confirm (“okeydir”). Preserve34 and approved30–33. Real FoodLog write, nutrition computation and native acceptance remain unimplemented/unverified.

## Phone part2 log success draft — 2026-11-06
- Completed: user requested next (“novbeti”). Prepared unit6 35-log-success on feat/phone-add-log-design. Eight AZ390px PNGs: normal/exceeded/unknown/local/error/edit/another/retry + viewport. Approved30–34 preserved.
- Current state: awaiting user approval. Remaining part2: edit/delete.
- Decisions: plan§19.4; saved meal + personal portion + estimated energy + remaining day energy when possible; Bu günə qayıt / Başqa yemək əlavə et / Düzəliş et with demo log ID; exceed neutrally (no ban); unknown energy ≠0; write failure is not saved and retry keeps same event; edit/another are concept dialogs (edit/delete next unit). Synthetic fixtures only; no real persistence claimed.
- Relevant: onboarding-original-style/{35-log-success.html,log-success.css,log-success.js,render-log-success.mjs,35-log-success-*.png}; docs/qa/visual-review-plan.json.
- Checks: eight renders + viewport opened vs34; zero overflow/missing art/targets under48px; JS syntax; contrast11.95/5.04/4.71; renderer asserts. CI paused; no commit/merge/push/APK. Native/device/EN/RU/TalkBack/persistence unverified.
- Exact next step: user reviews success screen; adjust requested differences only. After approval, next likely edit/delete — preferably new chat (context growth after30–35). No Git delivery without request.

35 final approval — 2026-11-06: user approved log success (“okey dava,” = okeydir + davam). Preserve35 and approved30–34.

## Phone part2 log edit/delete draft — 2026-11-06
- Completed: after35 approval+continue, prepared unit7 36-log-edit on feat/phone-add-log-design. Eight AZ390px PNGs: normal/changed/invalid/save/delete-confirm/deleted/missing/error + viewport. Approved30–35 preserved.
- Current state: awaiting user approval. Part2 design units from user list now drafted through edit/delete.
- Decisions: plan§19.4; edit grams with synthetic day delta; save/delete are no-write concept dialogs; delete requires explicit confirm; missing/tombstone does not resurrect; write error keeps same log ID without duplicate. Synthetic fixtures only.
- Relevant: onboarding-original-style/{36-log-edit.html,log-edit.css,log-edit.js,render-log-edit.mjs,36-log-edit-*.png}; docs/qa/visual-review-plan.json.
- Checks: eight renders + viewport opened vs35; zero overflow/missing art/targets under48px; JS syntax; contrast11.95/5.04/4.71; renderer asserts. CI paused; no commit/merge/push/APK.
- Exact next step: user reviews edit/delete; adjust requested differences only. Context böyüyür after30–36 — further work preferably new chat. No Git delivery without request.

36 final approval — 2026-11-06: user approved log edit/delete (“Bəyənsən okeydir de”). Preserve36 and approved30–35. Phone part2 Add→edit design units complete on feat/phone-add-log-design; uncommitted. Real FoodLog write/delete, nutrition recompute and native acceptance remain unimplemented/unverified. Exact next step (new chat): commit part2 designs if requested, or next scoped design/native work. CI remains paused; no merge/push/APK.


## Phone part2 delivery — 2026-11-06
- User explicitly authorized commit, remote push, main merge and local main update for approved phone Add→edit concepts 30–36 on feat/phone-add-log-design.
- Delivery includes HTML/CSS/JS, render scripts, AZ390px state PNGs/reports, visual-review-plan hashes and this context. Synthetic fixtures only; no FoodLog write/delete, nutrition recompute, native acceptance, APK or CI (paused).
- Exact next step after delivery: await next scoped request; CI remains paused unless user re-enables it.
