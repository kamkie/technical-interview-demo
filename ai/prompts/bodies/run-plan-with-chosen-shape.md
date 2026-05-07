Execute `<plan_file>` using the workflow rules.

Use `ai/PLAN_EXECUTION.md`; add `ai/WORKFLOW.md` when the plan requires delegation, worktrees, worker logs, or integration handoff.
Infer one local branch or delegated one-plan work from the plan file, especially `Execution Shape And Shared Files`, milestone boundaries, and shared-file ownership.
If delegated work is selected, keep the coordinator active until every worker reaches a terminal state; do not finish the run when only some workers are done.
If the plan file is not clear enough to choose safely, stop and explain the ambiguity instead of guessing.
If the work actually needs coordinated multi-plan execution, stop and say that explicitly instead of guessing.
