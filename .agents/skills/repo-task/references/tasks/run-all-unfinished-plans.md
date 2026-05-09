# Run All Unfinished Plans

Category: Workflow Execution
Slug: `run-all-unfinished-plans`
Placeholders: <topic>

Select every unfinished plan file under `.agents/plans/`, then execute the selected set using the same flow as `Coordinate Multiple Plans`.

Treat unfinished plans as the non-archived `.agents/plans/PLAN_*.md` files.
If there are no unfinished plans, stop and say so explicitly.
If only one unfinished plan exists, stop and say that a single-plan execution task should be used instead.
Do not silently skip an unfinished plan just to force a smaller parallel-safe set.
If any unfinished plans are too coupled for safe parallel execution, stop and explain why instead of forcing a split.
After selection, use `.agents/references/plan-execution.md`, `.agents/references/workflow.md`, and `.agents/references/workflow-coordinated-plans.md` as coordinator.
Do not release unless I ask.
