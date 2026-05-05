# Run Plans In Parallel

Category: Workflow Execution
Placeholders: plan_file_1, plan_file_2, plan_file_3, topic

## Category Guidance


Use these prompts when one request should actively execute planned work while also choosing or coordinating the workflow mode from `ai/WORKFLOW.md`.


## Prompt Body

```markdown
Execute these plan files in `Parallel Plans` mode using git worktrees:
- <plan_file_1>
- <plan_file_2>
- <plan_file_3>

Use `ai/WORKFLOW.md` and `ai/EXECUTION.md`.
Track per-plan branch, validation, private `CHANGELOG_<topic>.md`, worker log, and PR status.
Treat each worker branch as complete only when local validation is done and the branch has been pushed with a PR open or already merged, matching `ai/WORKFLOW.md` and `AGENTS.md`.
Keep the coordinator active until every worker reaches a terminal state and summarize all worker outcomes together; do not finish when only one worker is done.
If any listed plans are too coupled for safe parallel execution, stop and explain why instead of forcing a different mode.
Do not release unless I ask.
```
