# K16 — consumption/projection foundation (partial milestone)

Requirements: canonical research-data.json §8 step 8, §18–19 dated menu and
portion confirmation, §22 C-10/C-11; customization §2/7 and T10/T13.

Implemented boundary: `PlanConsumptionRepository.confirm` atomically appends the
confirmed FoodLog, event ledger, next plan revision and durable retry receipt.
A repeated identical event returns its original result even after another plan
revision; different payload conflicts. Stale plans cannot write. Viewing a domain
projection is read-only. Shopping quantities and all other items stay unchanged;
only the shopping aggregate version follows its owning plan.

`dailyPlanProjection` reuses K15 nutrient scale/sum with unknown propagation and
current, owner-scoped, timezone-filtered log revisions. Each item's nutrient
snapshot represents its full plannedServings; remaining uses the fractional share.
Deleted/edited intake never restores the consumed portion of the plan. Actual
intake remains separate from actual + remaining forecast. Diary completeness
must be explicitly supplied; default false suppresses remaining-energy claims.

Acceptance tests: half portion actual + half planned, null protein, read-only
view, untouched history/other day, retry with a new repository instance,
changed-payload conflict, stale revision, injected receipt failure rollback,
log edits/deletion, invalid amount and foreign owner.

Not implemented/verified here: K16 screens 04/13/14, Today integration, persistent
K15 generation request/result envelope and publication gates, future profile
recalculation/acceptance transaction, recipe presentation and actual recipe
confirmation editor. The caller must compute and confirm the actual recipe
snapshot using the appropriate recipe calculation path; this boundary does not
certify arbitrary supplied ingredient snapshots. No UI, APK or device test.
