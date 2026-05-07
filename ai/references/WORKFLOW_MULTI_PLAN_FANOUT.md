# Multi-Plan Fanout Workflow Reference

This on-demand reference owns detailed mechanics for `Multi-Plan Fanout`.
Load it only after `ai/WORKFLOW.md` selects that mode and each selected plan records the mode in `Execution Mode Fit`.

Use `Multi-Plan Fanout` when separate approved plan files are genuinely disjoint in source ownership, contract artifacts, rollout order, and validation needs.

## Topology

- one branch or worktree per owned plan file or explicitly grouped disjoint plan set
- one worker per plan or plan group
- one coordinator tracking all plan branches, worktrees, validation state, changelog state, and PR state
- canonical `CHANGELOG.md` stays untouched until accepted private changelog entries are consolidated

Normal outcome:

- multiple worker branches or PRs can move in parallel
- each worker branch is complete only when local validation is done and the branch has been pushed with a PR open or already merged, matching `AGENTS.md`

## Private Changelog Rules

- assign a unique stable `<topic>` token before execution starts
- create a private changelog copy named `CHANGELOG_<topic>.md`
- initialize `CHANGELOG_<topic>.md` from the current `CHANGELOG.md`
- update `CHANGELOG_<topic>.md` after each completed milestone instead of editing `CHANGELOG.md`
- keep the private changelog committed with the worker branch so review and later release preparation can inspect it
- the coordinator folds accepted private changelog text back into canonical `CHANGELOG.md` only during integration or release preparation

## Worker Rules

- own one plan file, or one explicitly grouped set of disjoint plan files
- update the owned plan `Lifecycle` and `Validation Results`
- update the private `CHANGELOG_<topic>.md` after each completed milestone
- update the committed worker log from `ai/WORKFLOW.md` after each completed milestone
- commit the milestone, plan update, private changelog update, and worker log together
- treat the worker branch as incomplete until local validation is done and the branch has been pushed with a PR open or already merged

## Coordinator Rules

- prevent overlapping ownership across workers
- track which private changelog files must later be folded back into `CHANGELOG.md`
- resolve cross-plan conflicts only after worker-local execution is complete
- decide merge order or PR order based on plan dependencies
- integrate accepted worker output without weakening each plan's required validation
- integrate accepted changelog text into canonical `CHANGELOG.md` when preparing the combined integration or release branch
- delete consumed private changelog files and worker logs before the final push or PR unless the user explicitly wants them retained for audit

## Worktree And Branch Rules

- use one worker branch per plan or grouped disjoint plan set
- start fanout from a committed or stashed state
- keep each worker branch tied to its owned plan or plan group
- push each complete worker branch and open a PR unless the user explicitly chose a local-only coordination flow that still satisfies `AGENTS.md`
- clean local worker trees after accepted output has been integrated and no further local worker work is pending
- delete no-longer-needed local worker branches when they are not needed for an open PR or other requested follow-up
- if a worker branch or worktree must be retained, leave it in a clean local state with no uncommitted changes
- do not delete remote branches or close PRs unless the user explicitly asks for that cleanup

## Reporting Extras

In addition to the common fields from `ai/WORKFLOW.md`, report:

- `CHANGELOG_<topic>.md` path
- worker-log path
- branch and worktree status for each plan or plan group
- PR status for each plan or plan group
- private changelog entries still waiting for canonical `CHANGELOG.md` consolidation
