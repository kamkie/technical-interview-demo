# Coordinate Multiple Plans

Category: Workflow Execution
Slug: `coordinate-multiple-plans`
Placeholders: <plan_file_1>, <plan_file_2>, <plan_file_3>, <topic>

Coordinate these active plan files using git worktrees:
- <plan_file_1>
- <plan_file_2>
- <plan_file_3>

Use `.agents/references/plan-execution.md`, `.agents/references/workflow.md`, and `.agents/references/workflow-coordinated-plans.md`.
Track per-plan branch, validation, private `CHANGELOG_<topic>.md`, worker log, and PR status.
Treat each worker branch as complete only when local validation is done and the branch has been pushed with a PR open or already merged, matching `.agents/references/workflow.md` and `AGENTS.md`.
Keep the coordinator active until every worker reaches a terminal state and summarize all worker outcomes together; do not finish when only one worker is done.
If any listed plans are too coupled for safe parallel execution, stop and explain why instead of forcing the split.
Do not release unless I ask.
