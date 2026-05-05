# Check Worker Status

Category: Workflow Execution
Placeholders: topic, worker name or agent id

## Category Guidance


Use these prompts when one request should actively execute planned work while also choosing or coordinating the workflow mode from `ai/WORKFLOW.md`.


## Prompt Body

```markdown
Check the status of worker `<worker name or agent id>` in the current workflow execution.

Report mode, owned plan or slice, branch and worktree, current progress, changed files, validations run with results, commit SHA(s), blockers, ready-for-integration status, PR status, and the worker-log path.
For `Shared Plan`, also note which shared files were intentionally left to the coordinator.
For `Parallel Plans`, also include the `CHANGELOG_<topic>.md` path.
If the worker has stalled or completed, state that clearly.
```
