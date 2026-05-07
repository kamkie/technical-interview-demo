Select every unfinished plan file under `ai/plans/active/`, then execute the selected set using the same flow as `Coordinate Multiple Plans`.

Treat unfinished plans as the non-archived `ai/plans/active/PLAN_*.md` files still present directly under `ai/`.
If there are no unfinished plans, stop and say so explicitly.
If only one unfinished plan exists, stop and say that a single-plan execution prompt should be used instead.
Do not silently skip an unfinished plan just to force a smaller parallel-safe set.
If any unfinished plans are too coupled for safe parallel execution, stop and explain why instead of forcing a split.
Then execute the selected plan files using coordinated multi-plan worktrees.
Use `ai/WORKFLOW.md` and `ai/EXECUTION.md`.
Track per-plan branch, validation, private `CHANGELOG_<topic>.md`, worker log, and PR status.
Treat each worker branch as complete only when local validation is done and the branch has been pushed with a PR open or already merged, matching `ai/WORKFLOW.md` and `AGENTS.md`.
Keep the coordinator active until every worker reaches a terminal state and summarize all worker outcomes together; do not finish when only one worker is done.
Do not release unless I ask.
