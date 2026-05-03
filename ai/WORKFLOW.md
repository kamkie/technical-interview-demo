# Codex Multi-Agent Workflow

`ai/WORKFLOW.md` explains how Codex should run delegated or parallel execution in this repository.

Use this file when the user explicitly wants delegation, sub-agents, parallel work, or a multi-worktree execution model.
Use `AGENTS.md` for repository rules, `ai/PLAN.md` and `ai/PLAN_*.md` for planning, `ai/EXECUTION.md` for normal single-worker execution discipline, `ai/TESTING.md` for validation scope, `ai/DOCUMENTATION.md` for artifact alignment, and `ai/RELEASES.md` only after an approved implementation PR has been merged onto `main`.

## Default Position

Use a multi-agent workflow only when the user explicitly asks for delegation, sub-agents, or parallel work.

Prefer a single agent when:

- the change is small
- multiple workers would touch the same files or same public contract artifacts
- the next step is tightly blocked on one coupled change
- coordination cost would exceed the parallelism benefit
- the review, testing, or documentation slice is too small to justify a handoff

## Choose One Of Two Modes

Multi-agent execution in this repository has only two supported modes:

1. `Parallel Plans`
   Several workers execute different `ai/PLAN_*.md` files in parallel.
   Each worker should behave like normal single-worker execution on its own branch or worktree, except that unreleased changelog text lives in a worker-owned temporary `CHANGELOG_<topic>.md` file instead of the canonical `CHANGELOG.md`.
2. `Shared Plan`
   Several workers execute different slices of the same `ai/PLAN_*.md`.
   Workers must not edit the canonical plan file or `CHANGELOG.md` directly while the plan is being split across workers.

The coordinator must choose the mode before delegating work. Do not mix the two modes inside the same plan without stating the boundary explicitly.

## Coordinator Responsibilities

The coordinator always owns:

- reading the governing docs, specs, and target plans
- deciding whether the work is worth splitting at all
- choosing `Parallel Plans` or `Shared Plan`
- defining explicit non-overlapping worker ownership
- deciding the integration order
- running final validation from `ai/TESTING.md`
- running final review and documentation-alignment checks using `ai/REVIEWS.md` and `ai/DOCUMENTATION.md`
- keeping release work out of scope until the approved PR is merged onto `main`

Mode-specific coordinator ownership is defined below.

## Mode 1: Parallel Plans

Use `Parallel Plans` when the work already has multiple plan files and those plans are genuinely disjoint in source ownership, contract artifacts, rollout order, and validation needs.

### Worker Contract

Each worker owns one plan file, or one explicitly grouped set of disjoint plan files.

Inside that owned plan scope, the worker should follow `ai/EXECUTION.md` exactly:

- implement the plan locally
- update the owned `ai/PLAN_*.md` file as execution progresses
- create and maintain a root-level worker-owned temporary changelog file named `CHANGELOG_<topic>.md`
- update `CHANGELOG_<topic>.md` after each completed milestone instead of editing `CHANGELOG.md`
- create a normal non-interactive commit after each completed milestone
- run the required validation for that plan
- when code is done and verified in a worktree-based execution, push the worker branch and open the PR as the final execution step

The coordinator must assign a unique `<topic>` token before delegation. Prefer a short stable topic derived from the owned plan or business slice, for example `CHANGELOG_identity_provider_configuration.md`.

`Parallel Plans` is intentionally close to the single-worker model, except changelog work is isolated per worker in `CHANGELOG_<topic>.md` so parallel branches do not fight over `CHANGELOG.md`.

### Coordinator Contract

In `Parallel Plans`, the coordinator owns:

- deciding which plans are safe to run in parallel
- preventing overlapping file ownership across workers
- assigning unique `CHANGELOG_<topic>.md` ownership
- tracking worker status, branch names, validation status, and PR status
- resolving cross-plan conflicts only after workers finish their local execution
- tracking which temporary changelog files must later be merged into `CHANGELOG.md`
- consolidating final status reporting across the separate plan branches

The coordinator should not centralize per-plan changelog or plan-file edits while workers are still executing. Worker changelog edits belong in the worker-owned `CHANGELOG_<topic>.md` file until release preparation on `main`.

### Shared-File Reality

`Parallel Plans` should avoid `CHANGELOG.md` conflicts entirely.

Do not have workers edit the canonical `CHANGELOG.md` while the plans are still executing in parallel. Instead:

- let each worker maintain the unreleased changelog entries required by `ai/EXECUTION.md` in its own `CHANGELOG_<topic>.md`
- keep those temporary files committed with the worker branch so review and integration can inspect them
- merge the accepted temporary changelog files into `CHANGELOG.md` only during the later release flow from `ai/RELEASES.md`
- remove the consumed temporary changelog files as part of that release cleanup

## Mode 2: Shared Plan

Use `Shared Plan` when one `ai/PLAN_*.md` should be executed by multiple workers in parallel.

This mode exists to avoid several workers fighting over the same canonical plan file and the same `CHANGELOG.md`.

### Canonical Shared Files

In `Shared Plan`, only the coordinator edits these canonical shared files:

- the target `ai/PLAN_*.md`
- `CHANGELOG.md`

Workers may edit their assigned source, tests, docs, REST Docs, HTTP examples, OpenAPI artifacts, and README slices when those files are part of their owned scope. They do not edit the canonical plan file or `CHANGELOG.md` unless the coordinator explicitly assigns that exception.

### Worker Progress File

Each worker must create and maintain its own temporary committed progress file at:

`ai/tmp/workflow/<plan_stem>__<worker_name>.md`

Create the directory if it does not exist.

This file is the worker-owned execution-state artifact for `Shared Plan` mode. It replaces direct worker edits to the canonical plan file and `CHANGELOG.md`.

Each progress file must record:

- the target plan file
- the worker branch and worktree
- the exact owned scope
- completed milestones, tasks, or slices
- changed files
- validation commands run and their pass/fail result
- the worker's proposed `CHANGELOG.md` bullets
- commit SHA(s)
- blockers, risks, and coordinator decisions still needed
- whether the slice is ready for integration

Workers must keep the progress file current and commit it together with the code changes it describes.

### Worker Contract

In `Shared Plan`, each worker should:

- implement only the assigned disjoint slice
- commit normally as milestones or bounded slices complete
- update the worker progress file in the same commit series
- run the validation required for the owned slice
- hand off the completed branch plus progress file to the coordinator

Workers do not update the canonical plan file's `Validation Results` or `CHANGELOG.md` directly in this mode.

### Coordinator Integration Contract

In `Shared Plan`, the coordinator owns:

- merging or cherry-picking completed worker branches onto the coordinator branch
- reading each worker progress file
- integrating accepted progress into the canonical plan file
- integrating accepted changelog text into `CHANGELOG.md`
- making any required shared-file conflict-resolution commit
- deleting consumed worker progress files before the final push or PR unless the user explicitly wants them retained for audit

The coordinator should make the canonical plan and changelog edits only after the worker's code and worker-local validation are complete enough to integrate.

The normal outcome for `Shared Plan` is one coordinator branch and one PR for the full plan. Push worker branches only when the user explicitly wants remote visibility for intermediate worker output.

## Task Slicing Rules

Good parallel boundaries in this codebase usually follow package, contract, or artifact ownership boundaries, for example:

- `business.book`
- `business.category`
- `business.localization`
- `business.user`
- `technical.security`
- contract artifacts owned by one bounded public API change
- documentation-only slices when they do not overlap active source edits

Do not split work in parallel when workers would overlap on:

- the same controller or service
- the same integration test class
- the same REST Docs or OpenAPI artifact
- the same unresolved product or rollout decision
- the same single plan milestone that cannot be expressed as disjoint file ownership

If the boundary is not defensible in terms of file ownership and validation scope, do not delegate it.

## Standard Coordinator Flow

1. Read the governing docs, specs, and target plans.
2. Close requirement gaps or queue them for user clarification.
3. Decide whether to stay single-worker or use multi-agent execution.
4. If using multi-agent execution, choose `Parallel Plans` or `Shared Plan`.
5. Define explicit worker ownership, including shared-file rules.
6. Delegate work and track worker progress.
7. Integrate completed worker output according to the chosen mode.
8. Run final validation, review, and documentation-alignment checks.
9. If worktree-based remote handoff is required, push the finished branch and open the PR only after local execution is complete.
10. Start release work only after the approved PR has been merged onto `main` and only if the user asked for release work.

## Worktree And Branch Rules

When using git worktrees:

- keep `main` as the only integration and release target
- treat worktree branches as temporary execution branches, not release branches
- do not cut a release from a worktree branch or detached `HEAD`

For `Parallel Plans`:

- use one worktree branch per plan or per explicitly grouped disjoint plan set
- each worker branch is complete only when local validation is done and the branch has been pushed and the PR is open or merged, matching `AGENTS.md`

For `Shared Plan`:

- use one coordinator integration branch for the plan
- use temporary worker branches or worktrees for the disjoint slices
- integrate completed worker branches onto the coordinator branch after worker-local validation
- push the coordinator branch and open one PR after the full plan is integrated and validated, unless the user explicitly asks for a different remote handoff model

A typical `Parallel Plans` start looks like:

```powershell
git checkout main
git pull --ff-only
git worktree add ..\technical-interview-demo-<plan> -b codex/<plan> main
```

A typical `Shared Plan` start looks like:

```powershell
git checkout main
git pull --ff-only
git worktree add ..\technical-interview-demo-<plan>-coord -b codex/<plan> main
git worktree add ..\technical-interview-demo-<plan>-worker-a -b codex/<plan>-worker-a main
```

After a `Shared Plan` worker finishes:

```powershell
git checkout codex/<plan>
git merge --no-ff codex/<plan>-worker-a
```

Before remote handoff:

```powershell
git status --short
git log --oneline --decorate -n 5
git push -u origin codex/<plan>
gh pr create --base main --head codex/<plan>
```

Use non-interactive git commands. Do not use destructive history rewrites unless the user explicitly asks for recovery work.

## Commit And Reporting Expectations

In both modes:

- keep commits narrow enough that they map cleanly to completed milestones or bounded slices
- do not batch the entire assignment into one final implementation commit
- report changed files, validation run, commit SHA(s), blockers, and readiness for integration

Extra expectations for `Parallel Plans`:

- each worker reports the owned plan file
- each worker reports the owned `CHANGELOG_<topic>.md` path
- each worker reports branch, worktree, validation status, and PR status

Extra expectations for `Shared Plan`:

- each worker reports the path to its progress file
- the coordinator reports which worker progress files have already been integrated into the canonical plan and changelog

The final completion message should state:

- which mode was used
- whether implementation is complete
- whether the finished work is locally complete, PR-open, or already merged onto `main`
- whether final validation passed
- whether any release or user decision remains
