# Working notes

## Code organization

When writing, changing or reviewing code, follow
`.cursor/rules/code-organization.mdc`. Place code in the appropriate components,
packages, folders and classes with clear responsibilities; reuse existing
structure, separate UI/business rules/data access, and avoid oversized files,
circular dependencies and unnecessary abstraction.

## Consult the product plan while coding

This applies to all implementation, fixes, refactors and code reviews in this project.

- Before changing code, read `docs/context/current-task.md` if present, then the
  relevant sections of `planning/nutrition-science-implementation-plan.txt`.
  Most product rules live in the plan; this file is not a replacement for it.
- `planning/research-data.json` is the canonical plan source. The TXT, PDF and
  canvas are derived views. Use the current files, not a remembered plan version.
- Read only the sections needed for the current task and their referenced
  requirements. Revisit them when implementation scope changes.
- Section guide: 3–4 food/recipe data and portions; 5–6 age, sex, energy and
  eligibility; 7–8 menu rules; 11–12 contracts and acceptance checks; 16 recipe
  edits, swaps and photo logging; 17 goals/budget; 18 onboarding/account;
  19 screen behavior, reminders, subscriptions and error states.
- For recipe customization, also read
  `planning/meal-customization-requirements.txt`. For visual work, consult the
  relevant design images, `implementation-guide.txt`, `new-flows-guide.txt`
  under `design-concepts/simple-turquoise/`, and `design-concepts/tokens.css`.
- Before implementing a unit, identify its applicable requirements and acceptance
  scenarios. Check the result against them before reporting completion; state
  which checks ran and which remain unverified. A requirement written in the
  plan is not evidence that it has been implemented or tested.
- Do not silently drop or weaken a plan requirement. If code, designs or plan
  sections conflict, identify the conflict and resolve it using current user
  instructions; ask only when the intended behavior remains unclear. Do not
  invent clinical thresholds, prices or approvals to fill missing decisions.

## Visual work

When a task touches how something looks, use the `ui-craft` skill — it carries
the token, contrast and screenshot workflow. Two things hold regardless of it:

- **Find the token file before writing a value.** Inventing a colour, spacing or
  radius next to a palette that already names one is the most common way an
  interface drifts, and it never looks broken enough to get caught in review.
- **Don't report a visual change as working without having rendered it and
  looked at the image.** If rendering wasn't possible, say that plainly instead
  of implying the result was checked.

# Context growth

Optimize for minimum necessary context. One chat = one logical task.

## Warn and hand off when ANY is true

1. Chat completed 2+ distinct subtasks/features
2. Substantial implementation across 3+ files more than once
3. Long multi-step plan continuing in the same chat
4. History full of large diffs/tool output and next step is another big change
5. Topic switch (auth → payments → export → refactor) in the same chat

## Required actions (do not skip)

1. Finish only the current small logical unit if almost done
2. Update `docs/context/current-task.md` with: Completed / Current state / Decisions / Remaining work / Relevant files / Checks performed / Exact next step
3. Tell the user explicitly:
   `Context böyüyür — bu tapşırığı yeni chatdə davam et.`
   Then give a short continuation prompt (goal + key files + plan path if any)
4. Do not stack another large unrelated step in this chat after the warning

## Do not

- Rescan the whole repo when scoped files are enough
- Dump full conversations into context files — keep them short and reusable
- Start implementing a large planned feature in a chat that already did research + planning

## Continuity

- At the start of a continuation chat, read `docs/context/current-task.md` if present, then inspect only the referenced files needed for the task.
- Keep the handoff factual: include checks performed, unresolved issues, and the exact next step; do not invent completed work or context-usage percentages.
- Recommend a new chat without creating one automatically. If the user chooses to continue here, respect that choice and keep the handoff current.

