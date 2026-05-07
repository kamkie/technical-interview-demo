# Choose Execution Shape

Category: Plan Verification
Slug: `choose-execution-shape`
Placeholders: <plan_file>

Review `<plan_file>` and decide which execution shape fits best.

Use `.agents/references/planning.md`; add `.agents/references/workflow.md` only if delegation, worktrees, worker logs, or integration mechanics are relevant.
Base the decision on the plan's `Execution Shape And Shared Files`, milestone boundaries, shared-file ownership, and whether the work should stay one active plan or coordinate multiple active plans.
Say explicitly whether the work should use one local branch, delegated one-plan work, or coordinated multi-plan work.
If the plan is not clear enough to choose safely, stop and name the ambiguity instead of guessing.
