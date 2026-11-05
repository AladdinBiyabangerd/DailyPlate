# Mandatory visual acceptance

Applies to UI work and any change that affects displayed content. Binding references:
planning/nutrition-science-implementation-plan.txt §18–19; design-concepts/tokens.css;
simple-turquoise/{implementation-guide,new-flows-guide,ui-extension-guide}.txt and
relevant PNGs. Product safety rules override example raster numbers.

## Before editing

Use ui-craft. Keep this plan as a persistent registry: retain accepted regression
cases when adding the next task; do not delete them to bypass a changed image. Populate `visual-review-plan.json` with every affected surface and
concrete acceptance requirements, not just “uses turquoise”. Include changed source
paths (deleted files too), reference, and required happy/empty/loading/error/selection
states as applicable. Shared theme/navigation/resources require every affected screen;
a single component render is not whole-screen evidence. Tests must render actual
production composables with realistic longest labels. Fixture content is labelled.

Example surface (six cases shown as a pattern, never copy an old PNG):

```json
{
  "id": "add-search",
  "sources": ["android/app/src/main/java/com/aladdin/dailyplate/ui/entry/FoodEntryScreen.kt"],
  "requirements": ["Agreed illustrated entry cards", "48dp actions; full labels"],
  "states": ["normal", "empty", "error"],
  "cases": [{"id":"add-az-normal-100", "image":"k10/add-az-normal-100.png",
    "language":"az", "font_scale":1.0, "state":"normal",
    "reference":"design-concepts/simple-turquoise/00-reference.png"}]
}
```

Add distinct cases for AZ/EN/RU × 1.0/1.5 for EVERY declared state of each surface. Include top/bottom captures for overflowing forms. Coverage sufficiency and
correct reference/state selection remain the reviewer's responsibility; CI checks
presence/matrix/source bindings, not whether a label accurately describes an image.

## Merge, render, inspect, accept

1. Follow start → focused commits → finish. CI runs once, only after merging main.
   It renders through existing native Robolectric JVM tests, without APK packaging.
2. CI saves ONLY fresh PNGs from its clean temporary snapshot to
   `.local-ci/visual/<merge-head>/renders/`. No branch CI or device install is needed.
3. If Android/design inputs changed since the last accepted source checkpoint,
   technical success alone leaves `passed=false`, `visual_status=pending`.
   Read `.local-ci/visual/<merge-head>/manifest.json`. Missing mappings/PNG/matrix
   require a fix branch, updated tests/plan, commit and finish. A later docs-only
   merge cannot erase pending visual source changes.
4. Open every listed render and agreed reference. Check each declared requirement,
   layout, illustrations, selection, labels, text scaling, scrolling and states.
   Unresolved differences block acceptance. Implement corrections on a fix branch.
5. Write an explicit review JSON outside tracked source, using the manifest's exact
   `head`, `source_digest` and case IDs. For EACH case provide `sha256`,
   `reference_sha256`, `viewed:true`, a substantive `comparison`, and
   `unresolved_differences:[]`. Top-level `reviewer` identifies the inspecting agent
   or person and `decision` must be `accepted`. Any `adaptations` need `reason` and
   `authorization` (existing user instruction or documented platform requirement).
6. Run `python3 .local-ci/workflow.py review-visual /absolute/path/review.json`.
   This validates commit, source/reference/image hashes, every case and successful
   technical CI. It appends acceptance to that CI log and completes its pending
   result; it does not rerun CI. Then `workflow.py finish` on main verifies completion.

Accepted per-case image hashes become regression baselines only AFTER explicit
review. Subsequent CI compares fresh renders to those hashes: missing or changed
images block completion even on a nonvisual merge. The manifest records previous
hashes and image_changed; compare both images before accepting a changed result.
Source changes still require review even if the image bytes match. The legacy
anchor is immutable after its first tracked introduction; it cannot be advanced
to hide unreviewed changes. Hash comparison
is exact PNG identity, not a semantic or tolerance-based pixel comparison.

No automatic approval command or baseline update exists. Reference mismatches must
be corrected or explicitly authorized before attestation. Hashes prove identity,
not visual correctness or honest inspection; that remains an explicit agent duty.
Local acceptance records are not cryptographic proof of a human decision.

## Legacy work and reporting

`visual-policy.json` records a source-change anchor, NOT design approval. Existing
screens remain subject to `design-discrepancies.md`. No approved image baseline is
seeded from them. Future changes require review, including restored design elements.
The initial empty plan is valid only while no protected source has changed.

CI conservatively watches ALL Android main sources/resources (including data/domain
changes that can affect UI), UI tests, app/root build scripts, Gradle properties and
design-concepts. Changes outside this set that affect rendering must also be declared
and reviewed by rule; static path detection cannot infer all behavioral impact.

Final reports must identify commit, technical CI, visual status, inspected evidence,
authorized adaptations and unverified device/TalkBack checks. A green unrelated task
does not certify the whole app. APK/device tests still require explicit user request.
Local tooling stays excluded from Git; back up `.local-ci/` separately.

Gate tests: `android/tools/test_visual_gate.py` validates evidence contracts and
`android/tools/test_visual_workflow.py` exercises actual hooks/finish/review in
isolated disposable repositories. Both run only in mandatory post-merge CI.
Python caches in test fixtures are excluded from Git cleanliness checks.
