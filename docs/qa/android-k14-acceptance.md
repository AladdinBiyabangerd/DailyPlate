# K14 — shopping quantities and regional price arithmetic

Implemented in domain/pricing/ShoppingPricing.kt against plan §17.8–9,
§19.8, §21 R-06, §25 A-07/A-08 and customization §6.

- Aggregates edible grams by food ID and exact preparation state, then deducts
  usable home quantities once. Retains needed/home/buy amounts separately.
- Computes edible package capacity from package grams × edible fraction, rounds
  purchase packages upward and separates full-package purchase from usage cost.
  Money arithmetic uses decimal values; no arbitrary currency rounding policy.
- Matches region/currency/state, retains selected price/policy versions and selects
  latest observation deterministically. Requires explicit freshness duration and
  version; no production freshness threshold is invented. Future observations fail.
- Missing/state-mismatched/stale prices produce null total with reasons, including
  when another line is priceable. Count/volume/yield conversions are not inferred;
  callers must supply documented edible grams in the exact purchasing state.
- Test observations and calculateTest are explicitly marked. Production total is
  blocked by G4_REQUIRED; no budget compliance badge is possible. No live prices,
  approval records or catalog publication were added.

Eight JVM tests cover aggregation, 750/500 rounding, 250 g home inventory,
usage/purchase separation, edible fraction, tiny over-boundary, excess inventory,
state/currency/region mismatch, partial missing data, policy absence, freshness
boundary/stale/future dates, deterministic order and fixture/production gating.
Authoritative execution evidence: post-merge .local-ci/logs/latest.json.

K15 generation, K17 shopping UI/status preservation and plan-change deltas remain
separate milestones. This pure estimator does not mutate shopping marks, plans,
profiles or diaries. 108-profile generator matrix, real regional prices/G4,
clinical/pilot, APK/device acceptance remain unverified. No UI changes or APK.
