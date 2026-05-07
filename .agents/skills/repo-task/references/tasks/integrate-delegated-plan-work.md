# Integrate Delegated Plan Work

Category: Implementation Integration
Slug: `integrate-delegated-plan-work`
Placeholders: <plan_file>

Integrate completed worker output for `<plan_file>`.

Use `.agents/references/workflow.md` and `.agents/references/workflow-delegated-plan.md` as coordinator.
Merge ready worker branches by default, using cherry-pick only when accepting less than a full worker branch, when I ask for it, or when a normal merge is not viable.
Fold accepted worker-log content into the canonical plan and `CHANGELOG.md`, clean consumed local worker branches or worktrees under `.agents/references/workflow.md`, run the required integration validation, delete consumed worker logs before the final push or PR unless I explicitly want them retained, and summarize what landed, what remains, and any cherry-pick reason.
