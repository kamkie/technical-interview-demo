Execute `<plan_file>` using the workflow rules.

Use `ai/WORKFLOW.md` and `ai/EXECUTION.md`.
Infer `Linear Plan` or `Single-Plan Fanout` from the plan file, especially `Execution Mode Fit`, milestone boundaries, and shared-file ownership.
If `Single-Plan Fanout` is selected, keep the coordinator active until every worker reaches a terminal state; do not finish the run when only some workers are done.
If the plan file is not clear enough to choose safely, stop and explain the ambiguity instead of guessing.
If the work actually needs multiple plan files and `Multi-Plan Fanout`, stop and say that explicitly instead of guessing.
