# Current task — K05 editor/publication/audit

## Completed
- K04 nutrition core and later foundation/Firebase work were already merged.
  Starting main 0ab932c had a clean tree and passing merged-main CI record.
- K05 headless test editor for recipes, NutritionPolicy and EvidenceCard:
  draft -> in review -> approved -> published -> archived, trusted test roles,
  independent reviewer, version-specific evidence, append-only Room audit history.
- Edit/rollback creates a new draft content version; rollback clears review and
  metadata. Old editorial revisions and personal FoodLog snapshots are preserved.
- Separate yield and photo calibration gates, rights, AZ/EN/RU checks, sourced
  storage guidance including separate rice safety source, card next-review date.
- New mandatory workflow/persistence JVM suites; architecture/acceptance docs.

## Current state
- Scope is only K05. No APK created; no visual UI change or device test.
- Test publication cannot activate real policies/content. G2/G3 remain closed.
- Final branch/main CI and commit identities: consult .local-ci/logs/latest.json
  and Git history for feat/k05-editorial-audit (recorded after this handoff commit).

## Decisions
- Separate EditorialDatabase schema v1; library/user DB schemas unchanged.
- Atomic INSERT of workflow snapshot+audit; expectedRevision rejects stale writes.
- Workflow revisions increment for all actions; content versions only on edits/
  rollback. Every past contributor is excluded from approving their own work.
- TestOnly envelope carries stage/review; embedded content stays unreviewed DRAFT.
  No production export/activation API; existing production catalog stays closed.
- Completeness checks plus explicit reviewer attestation do not prove clinical,
  legal, translation, kitchen/photo or food-safety correctness.
- Editor is headless. Real identity/role provisioning and production authoring
  interface are not supplied by this local test workflow.

## Remaining work
- K06 visual foundation/navigation/languages is the next numbered unit.
- Real G2/G3 review/authorization and content approval remain external work.
- APK/device/runtime UI integration and visual acceptance remain unverified.

## Relevant files
- planning/coding-tasks.txt K05/K06; planning/research-data.json canonical
- planning/nutrition-science-implementation-plan.txt §2–4/§9–11/§13–15/§17 for K05
- android/app/src/main/java/com/aladdin/dailyplate/domain/editorial/
- android/app/src/main/java/com/aladdin/dailyplate/data/local/{EditorialDatabase,TestEditorRepository}.kt
- android/app/src/test/java/com/aladdin/dailyplate/domain/editorial/
- android/app/src/test/java/com/aladdin/dailyplate/data/local/EditorialPersistenceTest.kt
- android/app/schemas/com.aladdin.dailyplate.data.local.EditorialDatabase/1.json
- android/tools/check_android.py
- docs/architecture/android-k05-editorial.txt; docs/qa/android-k05-acceptance.txt

## Checks performed
- Initial APK-free development lint/compilation and 37 JVM tests passed; an
  additional reviewer-return/command-injection scenario was added afterwards.
- Final mandatory branch and merged-main lifecycle evidence is in local CI logs
  and Git history. No test-only evidence is claimed as human approval.
- No APK, instrumentation, device, visual, real clinical/legal/translation or
  kitchen/photo validation performed.

## Exact next step
In a new chat verify K05 merged-main green, then read K06, plan §18–19 and the
referenced design guides/tokens. Apply ui-craft and implement only K06 with the
mandatory branch/commit/CI/finish workflow. Do not create an APK unless requested.
