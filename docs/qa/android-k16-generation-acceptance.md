# K16 durable generation boundary

Scope: canonical research-data.json §22 C-08–C-10; coding-tasks K16 continuation.

- UserRepositories.planGeneration persists exact request, profile, active policy/catalog/evidence/price bundle and complete K15 result as one schema-versioned receipt.
- Same owner/request ID replays the original outcome even after profile/input changes; changed request or fixture/production mode throws IdempotencyConflict.
- Input activation uses append-only compare-and-set revisions. Orchestration must activate a new `active` revision whenever imported planning inputs change. This local bundle is the authoritative generation selection; it does not claim live cross-database import synchronization or G3/G4 approval.
- Generation captures inputs transactionally, calculates outside the write lock, then compares current profile and complete active bundle before writing. Stale inputs persist STALE_INPUT without a plan.
- Production calls the existing gated K15 generator; fixture mode is internal. Persistence does not create a FoodLog or accepted MealPlan, and does not recalculate historical plans.
- JVM/Room acceptance: successful result and full-input round trip; database close/reopen; original replay after edits; owner isolation; conflicting payload/mode; stale profile/policy/catalog; changes during computation; write-failure rollback/retry; production gate/failure replay.

UI routes 04/13/14, explicit plan acceptance/recalculation, import activation wiring, real G3/G4 catalog/price acceptance and device checks remain unverified. No APK is required or authorized.

## Explicit acceptance boundary

- `UserRepositories.planAcceptance.accept(owner, requestId, newPlanId)` accepts only a persisted successful production generation. Internal fixture acceptance cannot bypass the production API.
- In one user-DB transaction it rechecks the exact profile and active input bundle, creates revision 1 of a new MealPlan including shopping, and stores an immutable owner/request → initial-plan receipt. It refuses an existing plan ID; existing plan history and consumption are untouched.
- Meal dates, slots, exact recipe versions, personal portion grams, nutrients, alternatives and daily energy target come from the stored generation, never from caller-made UI data. One planned serving denotes that generated personal portion. Full alternative portions, catalog, rule/coverage and decimal pricing provenance remain in the linked generation envelope.
- Retry returns the original revision even after profile/input changes; using another plan ID conflicts. Preview and acceptance create no FoodLog. Input staleness requires a new generation request.
- Added Room/JVM scenarios: mapping, production/fixture isolation, owner isolation, stale profile/input rejection, plan-ID collision, transaction rollback/retry, disk reopen and acceptance replay.

This completes the separate initial acceptance persistence flow only. UI routes, future-plan recalculation preview/acceptance, active catalog import integration and device checks remain outside this unit.

## Future-plan recalculation and confirmation

- After a confirmed profile transition, generate a fresh durable request for that exact profile. `futurePlans.preview(owner, FuturePlanCommand)` reads the existing plan revision, generation envelope and confirmed profile-change record. It persists an immutable before/after preview or an explicit rejected-candidate result. It creates no diary entry and changes no accepted plan until `confirm`.
- The existing FuturePlanPreview invariant preserves entire past, consumed, partial and skipped items, including their recipe/portion/nutrient snapshots, remaining servings and consumed log IDs. Only future untouched slots are replaced, added or removed. A partial item's remaining half is retained as part of that immutable item.
- Reuses the initial generated-plan mapper, candidate hard filters, K08 daily nutrition checks, weekly coverage/variety rules and exact-portion shopping/pricing. Checks run on the merged candidate, including preserved portions; a standalone new week's SUCCESS is not copied to the merge. Missing exact historical/customization evidence blocks with INCOMPLETE_DATA. Remaining protected items that violate new filters cannot be endorsed. A failed merged candidate is not proof that another request/seed cannot work.
- Preview includes the new target, actual item differences through before/after snapshots, shopping deltas, recomputed full-week cost and validation details. It is a planned-menu calculation, not a declaration of diary completeness or actual-intake suitability. `dailyPlanProjection` still uses current actual logs plus remaining servings and withholds exact remaining energy for an incomplete diary.
- Full-week shopping needs are recalculated without subtracting home/purchased marks. Marks survive; no-longer-needed marked/purchased products remain as zero-need rows. The updated shopping aggregate is committed with the plan.
- New-week alternatives are cleared and explicitly marked unvalidated for the merged plan; they are not silently inherited. Real catalog/G3 and price/G4 gates are unchanged.
- Confirmation accepts only the exact stored successful preview, compares current plan/profile/full active input bundle inside the write transaction, rejects past effective dates in the plan timezone, then appends plan revision + acceptance receipt atomically. Subsequent plan consumption, input/profile edits or date rollover reject an unaccepted stale preview. Retry returns the original accepted revision, including after restart; a changed command/payload conflicts.
- Existing single confirmed profile transition contract is retained. Skipped intermediate profile changes, missing historical evidence, unavailable production generation and invalid merges fail explicitly, preserving the accepted plan.
- Added JVM/Room scenarios cover protected history and half portions, independent diary projection, shopping marks/deltas, four-meal generation with explicit budget scope, preview cancellation, stale plan/profile/inputs/date, changes during computation, production/fixture and owner isolation, rollback, payload conflicts and on-disk restart/replay.

UI screens 04/13/14 and Today routes, automatic catalog import activation, real catalog/pricing acceptance and device/UI E2E remain unverified. No APK is generated by this unit.

## Menu UI integration

- Menu route loads the confirmed local profile and accepted plan revisions on IO; empty profile and missing authoritative inputs have separate states in AZ/EN/RU.
- Exact generation request, seed, acceptance plan ID and optional future command are saved before computation. Retry resumes the same command after recreation/restart; cancel clears only the pending UI command. Durable repository receipts remain untouched.
- Initial success requires explicit confirmation. Profile changes use a separate future preview and confirmation, with target, changed portions and shopping deltas. Failed/stale requests never accept automatically. All production calls retain G2/G3/G4 gates.
- Accepted menu uses seven real dates in the plan timezone, selected-day marker, and planned/partial/consumed/skipped labels. Today reads accepted-plan actual + remaining projection; diary completeness is not inferred.
- JVM UI render coverage: localized missing-input/profile-summary view at 1.5 font scale; no synthetic success, plan or FoodLog. Repository acceptance/recalculation scenarios remain in their existing suites.
- Remaining K16: complete recipe detail/name/ingredient/step presentation and portion-confirmed Yedim routes; successful production UI end-to-end acceptance/recalculation with approved catalog/prices; dated-menu and successful-preview visual/device coverage. This integration does not complete all K16 acceptance requirements.

## Recipe detail and portion-confirmed intake continuation
- Menu opens an exact recipe/customization revision with localized names, batch ingredient grams/preparation states and instruction verification status.
- Planned personal nutrient snapshot and remaining servings are distinct from full-batch ingredients. Missing cooked yield blocks the base-recipe batch conversion; customized batches use their recorded prepared/user servings. Customized yield is not inherited.
- “Yedim” opens a numeric personal portion, then nutrient/gram preview and explicit diary confirmation. Viewing/cancellation never writes. Null nutrients remain unknown.
- Exact serialized PlanConsumption is durably committed before the atomic log/remaining-plan transaction. Restart/retry reuses its event ID, timestamp and snapshots, including after DB success but before preference cleanup.
- Added RecipeDetailTest (half portion, exact customization, missing yield and invalid inputs), RecipeWorkflowTest (restart and post-commit replay), and MenuUiTest coverage/rendering for AZ/EN/RU at 1.5 font scale.
- Only mandatory post-merge CI may execute checks. Production G3/G4 gates remain closed. Real catalog successful-flow/device acceptance and APK checks remain unverified; no APK authorized.
- First render inspected: AZ portion preview wraps at 1.5 scale and retains full-width confirmation; no clipping observed. Initial CI found test harness thread/scroll assumptions, repaired by worker-thread persistence and explicit title scrolling between locales.
- Revision conflict clears only the uncommitted stale command (repository replay check precedes revision check), requires reload and a new portion confirmation. Temporary/uncertain write failures retain the exact command.
- All 197 JVM tests passed in the third merged run; lint caught the preparation-time string plural candidate (reworded as a unit-labelled field). AZ/EN/RU native previews inspected at 1.5 scale. Save success is set before refreshing details, so a read failure cannot misreport the committed intake as an uncommitted one.
- Restart workflow test also verifies stale uncommitted command rejection/cleanup without adding a second log.

## Planning input transport continuation
- `UserRepositories.planningImports.importJson(owner, payload, expectedRevision)` accepts a complete schema-1 `PlanningInputPackage` with monotonic `planning-import` version, policy and all weekly planning inputs. It is a trusted local transport boundary, not an authenticity/signature or content-approval mechanism.
- Active input revision and exact package receipt commit in one user-DB transaction. Same owner/package version replays the original activation without restoring it over newer inputs; changed content conflicts. Unseen older versions, stale active revisions, unknown schema and malformed payloads leave the current selection unchanged.
- Test-only editorial envelopes and price observations are rejected. Every current editorial envelope is test-only, so this boundary currently supports empty/unavailable catalogs only. It does not turn K05 workflow approval into production publication.
- No library DB is imported or mutated here. No menu, diary or historical snapshot is rewritten. Library import adapter, automatic application invocation, approved production catalog representation and production E2E remain outstanding. G3/G4 remain closed.
- APK-free Room tests cover exact selection/replay, owner isolation, conflict/downgrade/stale/schema rejection, fixture rejection, on-disk restart and receipt-write rollback/retry. Check merged-main CI for the actual result.
