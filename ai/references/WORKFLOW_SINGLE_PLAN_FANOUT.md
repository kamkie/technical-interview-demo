# Single-Plan Fanout Workflow Reference

This on-demand reference owns detailed mechanics for `Single-Plan Fanout`.
Load it only after `ai/WORKFLOW.md` selects that mode and the selected mode has been recorded in the plan's `Execution Mode Fit` section.

Use `Single-Plan Fanout` when one current `ai/PLAN_*.md` can be split into disjoint worker-owned slices but should still land as one coordinated stream.

## Topology

- one coordinator integration branch for the current plan
- one worker branch or worktree per disjoint slice
- one canonical plan file remains the source of truth for the full plan
- the coordinator keeps the canonical plan, roadmap, and shared artifacts current

Normal outcome:

- one coordinator branch
- one final PR for the full plan unless the user explicitly asks for intermediate worker PRs

## Shared-File Rules

- workers do not edit shared files directly
- the minimum shared files are the canonical `ai/PLAN_*.md` and `CHANGELOG.md`
- the coordinator may reserve additional shared files when several workers would otherwise collide, such as common integration tests, REST Docs pages, the OpenAPI baseline, or `README.md`
- workers record proposed shared-file edits in their worker log instead of applying those edits directly

## Worker Rules

- implement only the assigned slice or milestone ownership
- keep changed files inside the assigned ownership boundary
- update the committed worker log from `ai/WORKFLOW.md` after each completed milestone
- record proposed plan, roadmap, changelog, or other shared-file edits in the worker log
- commit worker-owned changes and the worker log after each completed milestone
- hand off the completed branch, worktree, and worker log to the coordinator

## Coordinator Rules

- keep the current plan authoritative
- prevent overlapping ownership across workers
- integrate workers in the order that keeps specs, contracts, and validation coherent
- merge completed worker branches onto the coordinator branch by default
- cherry-pick only when accepting less than the full worker branch, when the user asks for it, or when a normal merge is not viable
- record any cherry-pick reason in the canonical plan or worker-log integration notes
- integrate accepted worker-log content into the canonical plan file and `CHANGELOG.md`
- commit each integration checkpoint after the accepted milestone lands on the coordinator branch
- delete consumed worker logs before the final push or PR unless the user explicitly wants them retained for audit

## Worktree And Branch Rules

- use one coordinator branch for the canonical plan
- use temporary worker branches or worktrees for disjoint slices
- start fanout from a committed or stashed state
- push the coordinator branch and open one PR only after the full plan is integrated and validated, unless the user explicitly asks for another remote handoff model
- clean local worker trees after accepted output has been integrated and no further local worker work is pending
- delete no-longer-needed local worker branches when they are not needed for an open PR or other requested follow-up
- if a worker branch or worktree must be retained, leave it in a clean local state with no uncommitted changes
- do not delete remote branches or close PRs unless the user explicitly asks for that cleanup

## Reporting Extras

In addition to the common fields from `ai/WORKFLOW.md`, report:

- worker-log path
- which shared files were intentionally left to the coordinator
- which worker logs the coordinator has already integrated
- integration branch status
- final PR status, when a PR is required or requested
