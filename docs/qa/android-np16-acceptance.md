# NP-16 review and target suspension

Requirements: nutrition-science-implementation-plan.txt §27 NP-16 and §19.9;
nutrition-plan-design.md §27. No clinical thresholds or calorie titration added.

- Profile review reads owner-scoped current WeightEntry revisions; last 14 local
  calendar days including today, excluding future instants. Duplicate instants
  count once. At least three measurements are required for descriptive first-to-last
  change. No missing day is zero, and no deadline/7700 forecast is generated.
- Automatic target review is due 14 calendar days after assessment. Weight is
  voluntary. Review copies the latest recent measurement into the existing editor;
  changed measurements need explicit reconfirmation. Reason, preview, cancellation
  and confirmation use the existing append-only profile transaction.
- New symptom/unexplained change buttons immediately commit a new suspended
  profile and GENERAL assessment with no active energy/macro target, including
  specialist targets. General diary stays available. Historical snapshots remain.
- Suspension survives ordinary edits and restart. Professional support guidance
  is shown. No automatic resumption or invented medical clearance is provided.
- Legacy profiles decode with no suspension. Snapshot storage schema is unchanged.

Automated acceptance: NutritionReviewTest (window/owners/revisions/missing data,
preview and both suspension reasons); CurrentNutritionPersistenceTest (atomic
failure, retry, reopen, frozen history and owner-scoped current measurements);
CurrentNutritionUiTest (AZ/EN/RU, 1.5x font, distinct report actions and PNG renders).
Authoritative check result: .local-ci/logs/latest.json matching main HEAD.

Not verified: APK/device flow, real pilot, clinical outcomes. Weight-entry UI and
broader progress features remain in their existing implementation milestones;
this unit consumes the existing weight repository. Catalog/menu gates unchanged.
