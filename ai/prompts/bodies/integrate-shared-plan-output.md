Integrate completed worker output for `<plan_file>`.

Use `ai/WORKFLOW.md` as coordinator in `Shared Plan` mode.
Merge ready worker branches by default, using cherry-pick only when accepting less than a full worker branch, when I ask for it, or when a normal merge is not viable.
Fold accepted worker-log content into the canonical plan and `CHANGELOG.md`, clean consumed local worker branches or worktrees under `ai/WORKFLOW.md`, run the required integration validation, delete consumed worker logs before the final push or PR unless I explicitly want them retained, and summarize what landed, what remains, and any cherry-pick reason.