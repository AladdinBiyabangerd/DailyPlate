# K16 durable generation boundary

Scope: canonical research-data.json §22 C-08–C-10; coding-tasks K16 continuation.

- UserRepositories.planGeneration persists exact request, profile, active policy/catalog/evidence/price bundle and complete K15 result as one schema-versioned receipt.
- Same owner/request ID replays the original outcome even after profile/input changes; changed request or fixture/production mode throws IdempotencyConflict.
- Input activation uses append-only compare-and-set revisions. Orchestration must activate a new `active` revision whenever imported planning inputs change. This local bundle is the authoritative generation selection; it does not claim live cross-database import synchronization or G3/G4 approval.
- Generation captures inputs transactionally, calculates outside the write lock, then compares current profile and complete active bundle before writing. Stale inputs persist STALE_INPUT without a plan.
- Production calls the existing gated K15 generator; fixture mode is internal. Persistence does not create a FoodLog or accepted MealPlan, and does not recalculate historical plans.
- JVM/Room acceptance: successful result and full-input round trip; database close/reopen; original replay after edits; owner isolation; conflicting payload/mode; stale profile/policy/catalog; changes during computation; write-failure rollback/retry; production gate/failure replay.

UI routes 04/13/14, explicit plan acceptance/recalculation, import activation wiring, real G3/G4 catalog/price acceptance and device checks remain unverified. No APK is required or authorized.
