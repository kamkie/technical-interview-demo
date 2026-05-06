# Workflow Guide For AI Agents

`ai/WORKFLOW.md` owns execution-mode selection, branch/worktree topology, and coordinator or worker integration rules.
Anything not overridden here should follow `ai/EXECUTION.md`.

Use this file when the user explicitly wants delegation, sub-agents, parallel work, or a multi-worktree execution model.
It also defines the default `Single Branch` mode so planning and execution stay consistent across all approved plans.

## Codex-Aligned Defaults

Use these defaults unless the user explicitly wants another workflow:

- plan first, then execute against named milestones
- keep current work committed or at least stashed before branching or worktree fanout
- use isolated branches or worktrees for background or delegated work
- never rely on the same checked-out branch in more than one worktree at a time
- use `ai/EXECUTION.md` for milestone checkpoints and commit discipline
- prefer the smallest mode that preserves clear ownership and low coordination cost

## Supported Modes

Three execution modes are supported:

1. `Single Branch`
   - one agent works on one branch
   - one canonical plan file stays authoritative
   - one canonical `CHANGELOG.md` stays authoritative

2. `Shared Plan`
   - one current `ai/PLAN_*.md` stays authoritative
   - an orchestrator fans out worker branches or worktrees for disjoint slices of that plan
   - workers avoid shared files and report milestone output back through worker logs

3. `Parallel Plans`
   - different workers execute different `ai/PLAN_*.md` files in parallel
   - each worker keeps a private `CHANGELOG_<topic>.md` copy instead of editing `CHANGELOG.md`
   - the coordinator tracks integration order and later folds accepted changelog text back into `CHANGELOG.md`

Choose the mode before execution starts.
Do not mix modes inside one plan unless the boundary is explicit and documented in the relevant plan files.

## When To Stay Single-Branch

Prefer `Single Branch` when:

- the change is small
- workers would touch the same files or public contract artifacts
- the next step is tightly blocked on one coupled change
- coordination cost is higher than the parallelism benefit
- the review, testing, or documentation slice is too small to justify handoff

Use `Shared Plan` or `Parallel Plans` only when the split is defensible in file ownership, validation scope, and integration order.

## Common Mode Rules

All modes use the milestone loop, commit rules, and completion criteria from `ai/EXECUTION.md`.
This file only changes branch layout, artifact ownership, worker coordination, and remote handoff.

- choose the mode before execution starts
- keep mode-specific tracking artifacts current as milestones land
- finish local validation before any push or PR handoff unless the user explicitly chose a remote-first diagnostic flow
- follow `ai/TESTING.md` for Gradle validation batching; do not run shared-output validation tasks concurrently
- in `Shared Plan` and `Parallel Plans`, keep the coordinator active until every worker reaches a terminal state; do not treat the overall run as complete while any worker is still implementing, validating, pushing, or opening a PR
- prefer merging accepted branches or pull requests into the integration branch; use cherry-pick only when the user asks for it, when accepting less than the full branch or pull request, or when a normal merge is not viable, and record the reason
- keep release work out of scope until the approved PR has been merged onto `main`

For any forked worker, create and maintain a committed temporary worker log, updating it after each completed milestone, at:

`ai/tmp/workflow/<plan_stem_or_topic>__<worker_name>.md`

Create the directory if it does not exist.

Each worker log records:

- execution mode
- target plan file or topic token
- worker branch and worktree
- exact owned scope or milestones
- files intentionally left shared
- changed files
- validation commands run and their pass or fail result
- proposed changelog text
- commit SHA(s)
- blockers, risks, and coordinator decisions still needed
- whether the current milestone or slice is ready for integration

## Delegation Quality Bar

Delegated workers must meet the same repository standards as the coordinator.
Do not lower validation, review, documentation, artifact-update, or security expectations because work was delegated.

Workers inherit the coordinator capability by default.
Use an explicit model or reasoning override only when the user requests it or the task has a clear documented reason.
Do not delegate implementation, review, security, release, or ambiguous design work to a lower-capability worker.

Skills provide task-specific workflow instructions.
Using a skill does not reduce the required repository quality bar.

## Coordinator Ownership

The coordinator or orchestrator always owns:

- reading the governing docs, specs, and target plans
- deciding whether the work is worth splitting
- choosing the mode
- assigning explicit non-overlapping ownership
- deciding integration order
- final validation from `ai/TESTING.md`
- final review and documentation alignment using `ai/REVIEWS.md` and `ai/DOCUMENTATION.md`
- waiting for every worker to reach a terminal state before declaring the coordinated run complete

## Coordinator Completion Gate

For `Shared Plan` and `Parallel Plans`, the coordinator run is not complete when the first worker finishes.
It is complete only when every worker is in a terminal state and that terminal state has been reported.

Treat these as terminal states:

- completed under the mode-specific rules
- blocked with the blocker recorded clearly
- failed with the failure recorded clearly
- cancelled because the user explicitly stopped that worker

Workers are still active while they are implementing, validating, pushing, opening a PR, or waiting on follow-up work that has not been recorded as blocked.

Reporting rules:

- interim progress updates must say they are interim
- do not give a final success summary while any worker is still active
- if any worker ends blocked or failed, keep that in the final coordinator summary instead of quietly dropping it

## Integration Cleanup

After accepted worker output has been integrated, clean the local worker git trees unless the user explicitly wants them retained.

- remove temporary worker worktrees once their accepted output is integrated and no further local worker work is pending
- delete no-longer-needed local worker branches when they are not needed for an open PR or other requested follow-up
- if a worker branch or worktree must be retained, leave it in a clean local state with no uncommitted changes
- do not delete remote branches or close PRs unless the user explicitly asks for that cleanup

## Mode 1: Single Branch

Use `Single Branch` as the default execution mode.

Ownership rules:

- one agent owns the active branch
- the canonical `ai/PLAN_*.md` is updated directly
- `CHANGELOG.md` is updated directly under `## [Unreleased]`

Remote handoff:

- push or open a PR only when the user explicitly asks for it or the active repository workflow requires it

## Mode 2: Shared Plan

Use `Shared Plan` when one current `ai/PLAN_*.md` can be split into disjoint worker-owned slices but should still land as one coordinated stream.

Topology:

- one coordinator integration branch for the current shared plan
- one worker branch or worktree per disjoint slice
- one canonical plan file remains the source of truth for the plan

Shared-file rules:

- workers do not edit shared files directly
- the minimum shared files are the canonical `ai/PLAN_*.md` and `CHANGELOG.md`
- the coordinator may reserve additional shared files when several workers would otherwise collide, such as common integration tests, REST Docs pages, the OpenAPI baseline, or `README.md`

Worker rules:

- implement only the assigned slice or milestone ownership
- record proposed shared-file edits in the worker log instead of editing the shared files directly
- commit the worker-owned changes and worker log after each completed milestone
- hand off the completed branch, worktree, and worker log to the coordinator

Coordinator rules:

- keep the current shared plan authoritative
- merge completed worker branches onto the coordinator branch by default
- cherry-pick only when accepting less than the full worker branch, when the user asks for it, or when a normal merge is not viable; record the reason in the canonical plan or worker-log integration notes
- integrate accepted worker-log content into the canonical plan file and `CHANGELOG.md`
- commit each integration checkpoint after the accepted milestone lands on the coordinator branch
- delete consumed worker logs before the final push or PR unless the user explicitly wants them retained for audit

Normal outcome:

- one coordinator branch
- one final PR for the full shared plan unless the user explicitly asks for intermediate worker PRs

## Mode 3: Parallel Plans

Use `Parallel Plans` when plan files are genuinely disjoint in source ownership, contract artifacts, rollout order, and validation needs.

Topology:

- one branch or worktree per owned plan file or explicitly grouped disjoint plan set
- one worker per plan or plan group
- one coordinator tracking all plan branches, worktrees, validation state, and PR state

Private changelog rules:

- assign a unique stable `<topic>` token before execution starts
- create a private changelog copy named `CHANGELOG_<topic>.md`
- initialize `CHANGELOG_<topic>.md` from the current `CHANGELOG.md`
- update `CHANGELOG_<topic>.md` after each completed milestone instead of editing `CHANGELOG.md`
- keep the private changelog committed with the worker branch so review and later release preparation can inspect it

Worker rules:

- own one plan file, or one explicitly grouped set of disjoint plan files
- update the owned plan `Lifecycle` and `Validation Results`
- update the private `CHANGELOG_<topic>.md` after each completed milestone
- commit the milestone, plan update, private changelog update, and worker log together

Coordinator rules:

- prevent overlapping ownership across workers
- track which private changelog files must later be folded back into `CHANGELOG.md`
- resolve cross-plan conflicts only after worker-local execution is complete
- decide merge order or PR order based on plan dependencies
- integrate accepted changelog text into canonical `CHANGELOG.md` when preparing the combined integration or release branch

Normal outcome:

- multiple worker branches or PRs can move in parallel
- canonical `CHANGELOG.md` stays untouched until the coordinator is ready to consolidate the accepted private changelog entries

## Task Slicing Rules

Good parallel boundaries in this codebase usually follow package, contract, or artifact ownership boundaries, for example:

- `business.book`
- `business.category`
- `business.localization`
- `business.user`
- `technical.security`
- one bounded public API change and its contract artifacts
- documentation-only slices when they do not overlap active source edits

Do not split work when workers would overlap on:

- the same controller or service
- the same integration test class
- the same REST Docs or OpenAPI artifact
- the same unresolved product or rollout decision
- one milestone that cannot be expressed as disjoint file ownership

## Worktree And Branch Rules

- `main` remains the only integration and release target
- worktree branches are temporary execution branches, not release branches
- do not cut a release from a worktree branch or detached `HEAD`
- when fanout begins, start from a committed or stashed state so the new branches or worktrees are comparable and reviewable
- use unique worker branches for forked work; do not try to check out the same branch in multiple worktrees

For `Single Branch`:

- one branch is enough unless the user explicitly asks for a worktree-backed thread

For `Shared Plan`:

- use one coordinator branch for the canonical plan
- use temporary worker branches or worktrees for the disjoint slices
- push the coordinator branch and open one PR only after the full plan is integrated and validated, unless the user explicitly asks for another remote handoff model

For `Parallel Plans`:

- use one worker branch per plan or grouped disjoint plan set
- each worker branch is complete only when local validation is done and the branch has been pushed and the PR is open or merged, matching `AGENTS.md`

Optional local-only progress log for any worktree worker:

`ai/tmp/workflow-local/<plan_stem_or_topic>__<worker_name>.local.md`

Use it for concise in-progress notes for the user.
It may stay local-only and does not replace any required committed worker log.

## Reporting Expectations

In all modes, report:

- mode used
- owned plan or slice
- changed files
- validation run
- commit SHA(s)
- blockers
- final status or ready-for-integration status

Extra for `Shared Plan`:

- worker-log path
- which shared files were intentionally left to the coordinator
- which worker logs the coordinator has already integrated

Extra for `Parallel Plans`:

- `CHANGELOG_<topic>.md` path
- worker-log path
- branch, worktree, and PR status
