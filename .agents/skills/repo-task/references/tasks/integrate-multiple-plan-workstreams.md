# Integrate Multiple Plan Workstreams

Category: Implementation Integration
Slug: `integrate-multiple-plan-workstreams`
Placeholders: <topic>

Integrate completed worker output for all coordinated plan branches.

Use `ai/WORKFLOW.md` and `ai/references/WORKFLOW_COORDINATED_PLANS.md` as coordinator.
Review which merged plans are in scope, say explicitly which merged plans were included and which were skipped, integrate any accepted worker output that is not yet on the integration branch, fold accepted `CHANGELOG_<topic>.md` entries into `CHANGELOG.md`, update any canonical plan files or shared artifacts the accepted merged plans require, clean consumed local worker branches or worktrees under `ai/WORKFLOW.md`, delete consumed private changelog files and worker logs before the final push or PR unless I explicitly want them retained, run the required integration validation, and summarize what landed, what was skipped, and what remains.
If there are no in-scope coordinated plan branches, say so explicitly and stop.
