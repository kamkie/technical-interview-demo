# Delegate Plan Slices

Category: Workflow Execution
Slug: `delegate-plan-slices`
Placeholders: <plan_file>, <plan_stem_or_topic>, <worker_name>

Execute delegated worker-owned slices for `<plan_file>`.

Use `.agents/references/plan-execution.md`, `.agents/references/workflow.md`, and `.agents/references/workflow-delegated-plan.md`.
Act as coordinator, create worker branches or worktrees only for disjoint slices, keep shared files coordinator-owned, and require committed worker logs at `.agents/tmp/workflow/<plan_stem_or_topic>__<worker_name>.md`.
Keep the coordinator active until every worker reaches a terminal state and summarize all workers at the end; progress reports before that are interim only.
Push only the finished coordinator branch and open one final PR unless I explicitly ask otherwise.
