# Codex Multi-Agent Workflow

`ai/WORKFLOW.md` owns delegation-specific rules.
Anything not overridden here should follow `ai/EXECUTION.md`.

Use this file only when the user explicitly wants delegation, sub-agents, parallel work, or a multi-worktree execution model.

## When To Delegate

Prefer a single agent when:

- the change is small
- workers would touch the same files or public contract artifacts
- the next step is tightly blocked on one coupled change
- coordination cost is higher than the parallelism benefit
- the review, testing, or documentation slice is too small to justify handoff

Use multi-agent work only when the split is defensible in file ownership, validation scope, and integration order.

## Supported Modes

Only two modes are supported:

1. `Parallel Plans`
   - different workers execute different `ai/PLAN_*.md` files
   - each worker follows `ai/EXECUTION.md`, except unreleased changelog text lives in a worker-owned `CHANGELOG_<topic>.md`
2. `Shared Plan`
   - several workers execute disjoint slices of one `ai/PLAN_*.md`
   - workers do not edit the canonical plan file or `CHANGELOG.md` directly while the plan is split

Choose the mode before delegating. Do not mix modes inside one plan unless the boundary is explicit.

## Coordinator Ownership

The coordinator always owns:

- reading the governing docs, specs, and target plans
- deciding whether the work is worth splitting
- choosing the mode
- assigning explicit non-overlapping ownership
- deciding integration order
- final validation from `ai/TESTING.md`
- final review and documentation alignment using `ai/REVIEWS.md` and `ai/DOCUMENTATION.md`
- keeping release work out of scope until the approved PR is merged onto `main`

## Parallel Plans

Use `Parallel Plans` when plan files are genuinely disjoint in source ownership, contract artifacts, rollout order, and validation needs.

Worker rules:

- own one plan file, or one explicitly grouped set of disjoint plan files
- create and maintain a root-level temporary changelog file named `CHANGELOG_<topic>.md`
- update `CHANGELOG_<topic>.md` after each completed milestone instead of editing `CHANGELOG.md`
- keep the temporary changelog committed with the worker branch so review and later release preparation can inspect it

Coordinator rules:

- assign a unique stable `<topic>` token before delegation
- prevent overlapping ownership across workers
- track worker branch, worktree, validation, and PR status
- track which temporary changelog files must later be merged into `CHANGELOG.md`
- resolve cross-plan conflicts only after worker-local execution is complete

## Shared Plan

Use `Shared Plan` when one `ai/PLAN_*.md` can be split into disjoint file-owned slices but should still land as one coordinated execution stream.

Only the coordinator edits these canonical shared files:

- the target `ai/PLAN_*.md`
- `CHANGELOG.md`

Each worker must create and maintain a committed progress file at:

`ai/tmp/workflow/<plan_stem>__<worker_name>.md`

Create the directory if it does not exist.

Each progress file records:

- the target plan file
- the worker branch and worktree
- the exact owned scope
- completed milestones, tasks, or slices
- changed files
- validation commands run and their pass/fail result
- proposed `CHANGELOG.md` bullets
- commit SHA(s)
- blockers, risks, and coordinator decisions still needed
- whether the slice is ready for integration

Worker rules:

- implement only the assigned disjoint slice
- update the worker progress file instead of the canonical plan file's `Validation Results`
- propose changelog bullets in the progress file instead of editing `CHANGELOG.md`
- hand off the completed branch plus progress file to the coordinator

Coordinator integration rules:

- merge or cherry-pick completed worker branches onto the coordinator branch
- integrate accepted progress into the canonical plan file
- integrate accepted changelog text into `CHANGELOG.md`
- delete consumed worker progress files before the final push or PR unless the user explicitly wants them retained for audit

The normal outcome for `Shared Plan` is one coordinator branch and one PR for the full plan. Push worker branches only when the user explicitly wants remote visibility for intermediate worker output.

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

For `Parallel Plans`:

- use one worktree branch per plan or per explicitly grouped disjoint plan set
- each worker branch is complete only when local validation is done and the branch has been pushed and the PR is open or merged, matching `AGENTS.md`

For `Shared Plan`:

- use one coordinator integration branch for the plan
- use temporary worker branches or worktrees for the disjoint slices
- push the coordinator branch and open one PR only after the full plan is integrated and validated, unless the user explicitly asks for another remote handoff model

Optional local-only progress log for any worktree worker:

`ai/tmp/workflow-local/<plan_stem>__<worker_name>.local.md`

Use it for concise in-progress notes for the user. It may stay local-only and does not replace any required committed artifact.

## Reporting Expectations

In both modes, report:

- mode used
- owned plan or slice
- changed files
- validation run
- commit SHA(s)
- blockers
- ready-for-integration or final status

Extra for `Parallel Plans`:

- `CHANGELOG_<topic>.md` path
- branch, worktree, and PR status

Extra for `Shared Plan`:

- worker progress-file path
- which progress files the coordinator has already integrated
