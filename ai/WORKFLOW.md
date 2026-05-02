# Codex Multi-Agent Workflow

`ai/WORKFLOW.md` explains how Codex should run a multi-agent workflow in this repository.

Use this file when the user wants delegation, parallel agent work, or a multi-worktree execution model for a planned change.
Use `AGENTS.md` for repository rules, `SETUP.md` for environment setup, `ai/PLAN.md` and `ai/PLAN_*.md` for planning, `ai/EXECUTION.md` for single-agent execution rules that still apply within a worker's local scope, and `ai/RELEASES.md` only after an approved implementation PR has been merged onto `main`.

## When To Use Multi-Agent Execution

Use a multi-agent workflow only when the user explicitly asks for delegation, sub-agents, or parallel work.

Prefer a single agent when:

- the change is small
- the same files would be edited by multiple agents
- the next step is tightly blocked on one coupled code change
- the coordination overhead would exceed the parallelism benefit

## Coordinator

The coordinator agent owns:

- reading `AGENTS.md`, `README.md`, the target `ai/PLAN_*.md`, and any governing specs
- deciding whether the work is worth splitting
- keeping requirements, plan scope, and task boundaries consistent with the approved plan
- splitting the plan into bounded, non-overlapping tasks
- telling the user which tasks were delegated, which files stay under coordinator ownership, and what completion signal to expect
- keeping `main` as the only integration and release target, while temporary execution branches or worktrees remain staging only
- maintaining shared integration files such as `CHANGELOG.md` and the target plan's `Validation Results`
- reviewing worker output for correctness before remote handoff
- running final repository validation before any push or PR creation
- pushing the finished branch and opening the PR only if the user asked for remote handoff
- starting release work only after the approved PR has been merged onto `main`

## Task Slicing For This Repository

Good parallel boundaries in this codebase usually follow package, contract, or artifact ownership boundaries, for example:

- `business.book`
- `business.category`
- `business.localization`
- `business.user`
- `technical.security`
- `technical.docs`, REST Docs pages, OpenAPI checks, and HTTP examples when the contract impact is isolated

Avoid parallel splits when tasks would overlap on:

- the same controller or service
- the same integration test class
- the same REST Docs or OpenAPI contract artifact
- `CHANGELOG.md`
- the target `ai/PLAN_*.md`

Shared files should stay under coordinator ownership unless there is a strong reason to do otherwise.

## Standard Flow

1. The coordinator reads the governing docs and specs.
2. The coordinator creates or confirms the execution plan in `ai/PLAN_*.md`.
3. The coordinator splits the plan into bounded tasks with explicit file ownership.
4. The coordinator tells the user which tasks were delegated, which files stay under coordinator ownership, and what completion signal to expect.
5. Each worker gets its own git worktree or branch for one task.
6. Each worker completes its task, validates it, and creates a task-level commit.
7. The coordinator reports worker completion back to the user as tasks finish, including validation status and any blocker that affects final integration.
8. The coordinator reviews each worker result, updates the plan state as needed, and keeps progress moving without integrating each task onto `main` immediately.
9. The coordinator updates `CHANGELOG.md` under `## [Unreleased]` on the execution branch as each completed task or milestone commit lands there.
10. After the whole plan is complete locally, the coordinator updates the plan's `Validation Results` and runs final repository validation.
11. If the user asked for remote collaboration, the coordinator pushes the finished branch and opens the PR as the last execution step.
12. After the approved PR is merged onto `main`, the coordinator syncs local `main` to that merged state. Only then does release work begin, and only if the user explicitly asked for it.
13. The coordinator sends an explicit final status message saying whether the plan is locally complete, PR-ready, merged to `main`, blocked, or waiting on a release decision.

## Worktree Rules

When using git worktrees:

- keep `main` as the only integration and release target
- treat worktree branches as temporary execution branches, not release branches
- do not consider a plan complete until the finished changes are ready for PR merge into `main`, and do not consider release work started until the approved PR has actually been merged
- do not cut a release from a worktree branch or detached `HEAD`

A typical pattern is:

```powershell
git checkout main
git pull --ff-only
git worktree add ..\technical-interview-demo-<task> -b codex/<task> main
```

After a worker finishes:

```powershell
git checkout codex/<plan>
git cherry-pick <worker-commit-sha>
```

Before remote handoff:

```powershell
git status --short
git log --oneline --decorate -n 5
```

After the approved PR is merged and release work is requested:

```powershell
git checkout main
git pull --ff-only
```

Use non-interactive git commands. Do not use destructive history rewrites unless the user explicitly asks for recovery work.

## Commit Rules

In this repository:

- each completed task gets its own commit
- do not wait and batch the entire plan into one final implementation commit
- update `CHANGELOG.md` under `## [Unreleased]` as each completed task or milestone commit lands on the execution branch
- keep commits narrow enough that they map cleanly to completed plan tasks

The release commit is separate from the task commits and is created only after the whole plan is implemented, validated, merged onto `main`, and explicitly selected for release work.

## Validation Rules

Worker-level validation should be scoped to the task when possible, but the coordinator still owns the final repository gate before any PR is opened.

Coordinator final validation must run on the integrated local result:

```powershell
.\gradlew.bat build
```

If the plan changes:

- public API behavior, update tests, REST Docs, OpenAPI artifacts, HTTP examples, and contract docs together
- book list/search behavior, localization lookup behavior, or OAuth/session startup behavior, rerun `.\gradlew.bat gatlingBenchmark`

Treat failing compatibility or benchmark checks as spec failures.

## Release Rules

After the whole plan is implemented and an approved PR has been merged onto `main`:

- sync local `main` to the merged state
- make sure `CHANGELOG.md` stays aligned with the completed work
- update `ROADMAP.md` so released work is no longer tracked as active roadmap work
- create the release only from `main`
- use semantic version tags in the form `vMAJOR.MINOR.PATCH`
- create an annotated tag
- archive the executed `ai/PLAN_*.md` file under `ai/archive/` as part of the release change
- after the release is pushed and verified, remove temporary execution worktrees and branches that are no longer needed

Follow `ai/RELEASES.md` for the release commit, tag, and final verification steps.

## Reporting Expectations

Each worker handoff should give the coordinator:

- the task it completed
- the files it changed
- the validation it ran
- the commit SHA to retain for final plan integration
- any open issue that blocks clean final integration

The coordinator should report progress in terms of completed plan tasks and readiness for final integration, not just work completed in side branches.

Coordinator feedback must be user-visible and explicit:

- before delegation starts, report the task split, file ownership boundaries, and the conditions for plan completion
- when a worker finishes, report the completed task, changed files, validation status, and whether the result is ready for integration
- if work stalls, report the blocker instead of waiting silently
- when the plan reaches a terminal state, send a final completion message even if no release was requested

The final completion message should state:

- whether implementation is complete
- whether the finished work is only locally complete, PR-ready, or already merged onto `main`
- whether final validation passed
- whether any release or user decision remains
