# Integrate Parallel Plan Output

Category: Implementation Integration
Placeholders: topic

## Category Guidance


Use these prompts after worker implementation is already done and the next task is to fold ready output from worker branches or open PRs back into the canonical plan or accepted plan branches, `CHANGELOG.md`, and the integration branch, then clean any consumed local worker git trees under `ai/WORKFLOW.md`.


## Prompt Body

```markdown
Integrate completed worker output for all merged `Parallel Plans`.

Use `ai/WORKFLOW.md` as coordinator in `Parallel Plans` mode.
Review which merged plans are in scope, say explicitly which merged plans were included and which were skipped, integrate any accepted worker output that is not yet on the integration branch, fold accepted `CHANGELOG_<topic>.md` entries into `CHANGELOG.md`, update any canonical plan files or shared artifacts the accepted merged plans require, clean consumed local worker branches or worktrees under `ai/WORKFLOW.md`, delete consumed private changelog files and worker logs before the final push or PR unless I explicitly want them retained, run the required integration validation, and summarize what landed, what was skipped, and what remains.
If there are no in-scope merged `Parallel Plans`, say so explicitly and stop.
```
