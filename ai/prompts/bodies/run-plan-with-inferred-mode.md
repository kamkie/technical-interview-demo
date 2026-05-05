Execute `<plan_file>` using the workflow rules.

Use `ai/WORKFLOW.md` and `ai/EXECUTION.md`.
Infer `Single Branch` or `Shared Plan` from the plan file, especially `Execution Mode Fit`, milestone boundaries, and shared-file ownership.
If `Shared Plan` is selected, keep the coordinator active until every worker reaches a terminal state; do not finish the run when only some workers are done.
If the plan file is not clear enough to choose safely, stop and explain the ambiguity instead of guessing.
If the work actually needs multiple plan files and `Parallel Plans`, stop and say that explicitly instead of guessing.