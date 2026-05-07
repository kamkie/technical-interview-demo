# Coordinated Multi-Plan Workflow Reference

This on-demand reference owns detailed mechanics for coordinating multiple active plans that move in parallel.
Load it only after the user has approved parallel execution of separate plan files.

Use this workflow when each plan is independently approved, the source and contract surfaces are disjoint, and one coordinator will later integrate the accepted outputs.

## Preconditions

- every selected active plan is approved and decision-complete
- each worker owns exactly one plan or one explicitly bounded plan slice
- selected plans do not compete for the same source files, specs, public contract artifacts, roadmap rows, or release decisions
- current work is committed or stashed before worktrees branch
- the coordinator has recorded integration order and shared artifacts

## Changelog Handling

Workers do not edit canonical `CHANGELOG.md`.
Each worker keeps proposed unreleased text in the worker log or in a temporary root file named:

`CHANGELOG_<topic>.md`

The coordinator folds accepted text into `CHANGELOG.md` during integration and removes temporary changelog files in the same integration change.

## Worker Setup

For each worker:

1. Create a unique branch or worktree.
2. Create a worker log under `ai/tmp/workflow/`.
3. Record the plan path, owned files, shared exclusions, validation target, and proposed changelog path.
4. Execute only the assigned plan or slice.
5. Commit completed milestones under that plan's rules.
6. Report changed files, validation, commit SHAs, blockers, and proposed changelog text.

## Coordinator Loop

The coordinator:

1. Tracks all selected plans and worker logs.
2. Waits for every worker to reach a terminal state.
3. Reviews accepted outputs in the planned integration order.
4. Merges or otherwise integrates accepted branches.
5. Folds accepted changelog text into canonical `CHANGELOG.md`.
6. Runs final validation from `ai/TESTING.md`.
7. Updates roadmap and plan lifecycle states.
8. Reports completed, blocked, failed, or cancelled workers explicitly.

## Guardrails

- do not group plans that share unresolved product decisions
- do not let workers update canonical roadmap or changelog files during parallel execution
- do not silently drop a blocked or failed worker from the final status
- do not start release work until accepted outputs are integrated onto `main`
