# Historical design archive — NOT current UI requirements

Design-only orphan snapshot, public by explicit user request (2026-11-06).
The authoritative application branch is feat/onboarding-original-style, with
active phone onboarding-original-style and tablet onboarding-tablet designs.
Approved phone26–27 are active, not archived here. Never implement from these
historical alternatives merely because their name says approved/unified/final.

MANIFEST.json records every original path, source branch/commit, Git blob,
SHA-256, size and unique archive path. current/ contains the249 obsolete files
from the audited canonical snapshot. historical/<blob>/ contains148 other
versions (including older guide versions), preserving same-path differences.
No application code/history, local tooling, credential stores or user configs
are included. SECRET-SCAN.json describes the actual redacted scan and limitations.

## Restore a specific historical file without switching the active project

Choose its archive_path and SHA-256 from MANIFEST.json. After fetching ONLY this
archive branch if needed, use git show <verified-archive-commit>:<archive_path>
to a separate scratch directory, then verify SHA-256 before use. Example:

    git show <archive-commit>:current/design-concepts/simple-turquoise/approved/d03-menu.png > /tmp/d03-menu.png
    shasum -a 256 /tmp/d03-menu.png

For an entire archive, git archive <verified-archive-commit> may be unpacked into
an empty scratch directory. Do not restore alternatives into active design
folders or treat historical visual evidence as current design approval.
Historical QA evidence must retain exact source hashes when resolved from here.
The archive has no parent commits, so publishing it does not publish local
application commit history. Only this archive ref is authorized for remote push.

Some entries include archive_member: first retrieve archive_path, then extract that member with a ZIP reader and verify its source SHA-256. This lossless container preserves the original prompt bytes; no Git hook or whitespace rule was disabled.
