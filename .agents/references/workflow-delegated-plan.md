# Delegated One-Plan Workflow Reference

This on-demand reference owns detailed mechanics for splitting one active plan into disjoint worker-owned slices.
Load it only after the user has approved delegation, parallelism, or worktree-backed implementation for one active plan.

Use this workflow when one active plan remains the canonical source of truth and a coordinator owns shared artifacts, final validation, and integration order.

## Preconditions

- the active plan is approved and decision-complete
- the plan identifies worker-safe ownership boundaries
- current work is committed or stashed before workers branch
- the coordinator has assigned each worker an explicit write scope
- shared files are named before workers start

## Coordinator Files

The coordinator normally owns:

- the active plan file
- `CHANGELOG.md`
- `ROADMAP.md`
- shared specs, contract artifacts, and docs unless one worker owns the entire slice
- worker-log integration notes

Workers should avoid these files unless the coordinator explicitly assigns them.

## Worker Setup

For each worker:

1. Create a unique branch or worktree from the coordinator's current base.
2. Create a worker log under `.agents/tmp/workflow/`.
3. Record the exact owned scope, branch, worktree, shared files, and validation target.
4. Implement only the assigned slice.
5. Run slice validation and record results in the worker log.
6. Commit the slice and report the commit SHA, changed files, blockers, and proposed changelog text.

## Coordinator Loop

The coordinator:

1. Keeps the canonical plan current.
2. Reviews worker logs as workers finish.
3. Integrates accepted branches in a deliberate order.
4. Resolves conflicts without discarding unrelated user or worker changes.
5. Runs final validation from `.agents/references/testing.md`.
6. Updates the canonical plan, roadmap, and changelog.
7. Reports every worker's terminal state before declaring the run complete.

## Guardrails

- do not split one milestone when workers cannot own disjoint files
- do not let workers edit shared tracking files unless explicitly assigned
- do not treat partial worker completion as full-plan completion
- do not prepare a release from delegated branches or detached worktrees
