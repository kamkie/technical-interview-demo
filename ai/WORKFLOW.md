# Workflow Guide For AI Agents

`ai/WORKFLOW.md` owns execution-mode selection, branch/worktree topology, and coordinator or worker integration rules.
Anything not overridden here should follow `ai/EXECUTION.md`.

Use this file to choose the workflow mode before implementation starts.
Load a fanout reference only after the Mode Selection Gate selects that non-default mode:

- `ai/references/WORKFLOW_SINGLE_PLAN_FANOUT.md`
- `ai/references/WORKFLOW_MULTI_PLAN_FANOUT.md`

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

1. `Linear Plan`
   - one agent works on one branch
   - one canonical plan file stays authoritative
   - one canonical `CHANGELOG.md` stays authoritative

2. `Single-Plan Fanout`
   - one current `ai/PLAN_*.md` stays authoritative
   - a coordinator fans out worker branches or worktrees for disjoint slices of that plan
   - workers avoid shared files and report milestone output back through worker logs
   - detailed mechanics are in `ai/references/WORKFLOW_SINGLE_PLAN_FANOUT.md`

3. `Multi-Plan Fanout`
   - different workers execute different `ai/PLAN_*.md` files in parallel
   - each worker keeps a private `CHANGELOG_<topic>.md` copy instead of editing `CHANGELOG.md`
   - the coordinator tracks integration order and later folds accepted changelog text back into `CHANGELOG.md`
   - detailed mechanics are in `ai/references/WORKFLOW_MULTI_PLAN_FANOUT.md`

Do not mix modes inside one plan unless the boundary is explicit and documented in the relevant plan files.

## Mode Selection Gate

Default to `Linear Plan`.
Choose a non-default mode only when the benefit is clear and the user has approved the fanout.

Use this gate before execution starts:

- user-requested fanout, parallelism, sub-agents, or worktree splitting is a candidate for `Single-Plan Fanout` or `Multi-Plan Fanout`, but still requires explicit user approval before the mode is used
- one active plan with at least two disjoint worker-safe slices is a candidate for `Single-Plan Fanout`
- two or more approved disjoint plan files are a candidate for `Multi-Plan Fanout`
- shared controller, service, test, REST Docs, OpenAPI, README, or tightly coupled milestone edits stay in `Linear Plan`
- non-default modes require recording the selected mode in the plan's `Execution Mode Fit` section and loading the matching reference file before proceeding
- if the selected mode changes during execution, stop and amend the plan or plans before continuing

## When To Stay Linear

Prefer `Linear Plan` when:

- the change is small
- workers would touch the same files or public contract artifacts
- the next step is tightly blocked on one coupled change
- coordination cost is higher than the parallelism benefit
- the review, testing, or documentation slice is too small to justify handoff

Use fanout only when the split is defensible in file ownership, validation scope, and integration order.

## Common Mode Rules

All modes use the milestone loop, commit rules, and completion criteria from `ai/EXECUTION.md`.
This file only changes branch layout, artifact ownership, worker coordination, and remote handoff.

- choose the mode before execution starts
- keep mode-specific tracking artifacts current as milestones land
- finish local validation before any push or PR handoff unless the user explicitly chose a remote-first diagnostic flow
- treat worktree or side-branch execution as incomplete until the finished branch has been pushed and a pull request is open or already merged onto `main`, when the selected workflow or repository handoff requires remote completion
- follow `ai/TESTING.md` for Gradle validation batching; do not run shared-output validation tasks concurrently
- in fanout modes, keep the coordinator active until every worker reaches a terminal state; do not treat the overall run as complete while any worker is still implementing, validating, pushing, or opening a PR
- prefer merging accepted branches or pull requests into the integration branch; use cherry-pick only when the user asks for it, when accepting less than the full branch or pull request, or when a normal merge is not viable, and record the reason
- keep release sequencing out of workflow execution; use `ai/RELEASES.md` only after the implementation work is integrated and release is in scope

## Worker Log Schema

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
- waiting for every worker to reach a terminal state before declaring a fanout run complete

## Coordinator Completion Gate

For fanout modes, the coordinator run is not complete when the first worker finishes.
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

## Linear Plan

Use `Linear Plan` as the default execution mode.

Ownership rules:

- one agent owns the active branch
- the canonical `ai/PLAN_*.md` is updated directly
- `CHANGELOG.md` is updated directly under `## [Unreleased]`

Remote handoff:

- push or open a PR only when the user explicitly asks for it or the active repository workflow requires it

## Task Slicing Rules

Good fanout boundaries in this codebase usually follow package, contract, or artifact ownership boundaries, for example:

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

- `main` remains the integration target for completed work
- worktree branches are temporary execution branches, not release branches
- release-target and tagging preconditions live in `AGENTS.md` and `ai/RELEASES.md`
- when fanout begins, start from a committed or stashed state so the new branches or worktrees are comparable and reviewable
- use unique worker branches for forked work; do not try to check out the same branch in multiple worktrees

For `Linear Plan`, one branch is enough unless the user explicitly asks for a worktree-backed thread.

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

Fanout modes also report the mode-specific fields named by their reference files.
