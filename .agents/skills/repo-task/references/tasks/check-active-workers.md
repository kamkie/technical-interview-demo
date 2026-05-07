# Check Active Workers

Category: Workflow Execution
Slug: `check-active-workers`
Placeholders: <topic>

Check the status of the active workers in the current workflow execution.

Use `ai/WORKFLOW.md`.
For each worker, report workflow shape, owned plan or slice, branch and worktree, current task, changed files, validations, commit SHA(s), blockers, ready-for-integration status, PR status, and the worker-log path.
For delegated one-plan work, include any shared files intentionally left to the coordinator.
For coordinated multi-plan work, include the `CHANGELOG_<topic>.md` path.
Keep the report concise and factual.
