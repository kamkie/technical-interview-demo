# Check Active Workers

Category: Workflow Execution
Placeholders: topic

## Category Guidance


Use these prompts when one request should actively execute planned work while also choosing or coordinating the workflow mode from `ai/WORKFLOW.md`.


## Prompt Body

```markdown
Check the status of the active workers in the current workflow execution.

Use `ai/WORKFLOW.md`.
For each worker, report mode, owned plan or slice, branch and worktree, current task, changed files, validations, commit SHA(s), blockers, ready-for-integration status, PR status, and the worker-log path.
For `Shared Plan`, include any shared files intentionally left to the coordinator.
For `Parallel Plans`, include the `CHANGELOG_<topic>.md` path.
Keep the report concise and factual.
```
