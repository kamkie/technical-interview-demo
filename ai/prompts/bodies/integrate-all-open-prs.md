# Integrate All Open PRs

Category: Implementation Integration
Placeholders: topic

## Category Guidance


Use these prompts after worker implementation is already done and the next task is to fold ready output from worker branches or open PRs back into the canonical plan or accepted plan branches, `CHANGELOG.md`, and the integration branch, then clean any consumed local worker git trees under `ai/WORKFLOW.md`.


## Prompt Body

```markdown
Integrate all open implementation PRs that are ready.

Use `AGENTS.md`, `ai/WORKFLOW.md`, `ai/EXECUTION.md`, `ai/DOCUMENTATION.md`, `ai/TESTING.md`, and `ai/REVIEWS.md` as coordinator.
Discover every currently open PR with the GitHub CLI or repository remote, then review each PR branch, plan reference, changed files, checks, review state, conflicts, and relationship to the active unreleased work.
Say which open PRs are in scope, which are not implementation PRs, which are not ready, and why.
For ready in-scope PRs, merge the accepted output onto the integration branch in dependency order by default, using cherry-pick only when accepting less than a full PR, when I ask for it, or when a normal merge is not viable.
Fold accepted `CHANGELOG_<topic>.md` entries into `CHANGELOG.md`, update any canonical plan files or shared artifacts the accepted PRs require, clean consumed local worker branches or worktrees under `ai/WORKFLOW.md`, and delete consumed private changelog files and worker logs before the final push or PR unless I explicitly want them retained.
Run the required integration validation, then summarize what landed, what was skipped, what remains open, any cherry-pick reason, and any follow-up PRs or blockers.
If there are no in-scope open PRs, say so explicitly and stop.
```
