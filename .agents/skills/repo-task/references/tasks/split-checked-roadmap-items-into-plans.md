# Split Checked Roadmap Items Into Plans

Category: Planning
Slug: `split-checked-roadmap-items-into-plans`
Placeholders: <topic>

Create one or more `.agents/plans/PLAN_<topic>.md` files from every checklist item marked `[x]` in `ROADMAP.md`.

Split only genuinely disjoint workstreams that can later execute in parallel without overlapping source ownership, contract artifacts, rollout order, or validation.
If the checked items form only one coherent plan, stop and say that the single-plan planning task should be used instead.
Record requirement gaps, fallback assumptions, and any cross-plan dependency notes in each created plan.
Set the lifecycle state for each plan from `.agents/references/planning.md`.
Update `ROADMAP.md` in the same change so every resulting active roadmap item points to its plan path and current status.
