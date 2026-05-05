# Clean Worktrees And Stale Local Branches

Category: Other Useful Lifecycle Prompts

## Prompt Body

```markdown
Clean local git worktrees and stale local branches after completed or abandoned workflow execution.

Use `ai/WORKFLOW.md`.
Review the current local worktrees and branches first.
Remove temporary worktrees that are no longer needed, delete stale local worker branches that are not needed for an open PR or other requested follow-up, and leave any retained branch or worktree in a clean local state.
Do not delete remote branches, close PRs, or remove any branch or worktree that still has uncommitted or unmerged work unless I explicitly ask.
Summarize what was removed, what was retained, and any follow-up cleanup that is still blocked.
```
