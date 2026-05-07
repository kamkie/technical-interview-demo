# Run All Ready Plans

Category: Workflow Execution
Slug: `run-all-ready-plans`
Placeholders: <topic>

Select every ready plan file under `.agents/plans/`, then execute the selected set using the same flow as `Coordinate Multiple Plans`.

Treat ready plans as the non-archived `.agents/plans/PLAN_*.md` files whose `Lifecycle` status is `Ready`.
Restate exactly which ready plan files were selected and which non-archived plan files were skipped because they were not `Ready`.
If there are no ready plans, stop and say so explicitly.
If only one ready plan exists, stop and say that a single-plan execution task should be used instead.
Do not silently skip a `Ready` plan just to force a smaller parallel-safe set.
If any ready plans are too coupled for safe parallel execution, stop and explain why instead of forcing a split.
Then execute the selected plan files using coordinated multi-plan worktrees.
Use `.agents/references/workflow.md` and `.agents/references/execution.md`.
Track per-plan branch, validation, private `CHANGELOG_<topic>.md`, worker log, and PR status.
Treat each worker branch as complete only when local validation is done and the branch has been pushed with a PR open or already merged, matching `.agents/references/workflow.md` and `AGENTS.md`.
Keep the coordinator active until every worker reaches a terminal state and summarize all worker outcomes together; do not finish when only one worker is done.
Do not release unless I ask.
