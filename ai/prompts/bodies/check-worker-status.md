Check the status of worker `<worker name or agent id>` in the current workflow execution.

Report workflow shape, owned plan or slice, branch and worktree, current progress, changed files, validations run with results, commit SHA(s), blockers, ready-for-integration status, PR status, and the worker-log path.
For delegated one-plan work, also note which shared files were intentionally left to the coordinator.
For coordinated multi-plan work, also include the `CHANGELOG_<topic>.md` path.
If the worker has stalled or completed, state that clearly.
