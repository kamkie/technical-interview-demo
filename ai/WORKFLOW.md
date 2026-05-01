# Codex Multi-Agent Workflow

`ai/WORKFLOW.md` explains how Codex should run a multi-agent workflow in this repository.

Use this file when the user wants delegation, parallel agent work, or a multi-worktree execution model for a planned change.
Use `AGENTS.md` for repository rules, `SETUP.md` for environment setup, `ai/PLAN.md` and `ai/PLAN_*.md` for planning, and `ai/RELEASES.md` for the final release workflow.

## When To Use Multi-Agent Execution

Use a multi-agent workflow only when the user explicitly asks for delegation, sub-agents, or parallel work.

Prefer a single agent when:

- the change is small
- the same files would be edited by multiple agents
- the next step is tightly blocked on one coupled code change
- the coordination overhead would exceed the parallelism benefit

## Roles

### Coordinator

The coordinator agent owns:

- reading `AGENTS.md`, `README.md`, the target `ai/PLAN_*.md`, and any governing specs
- deciding whether the work is worth splitting
- splitting the plan into bounded, non-overlapping tasks
- keeping `main` as the integration branch
- integrating completed worker changes back onto `main`
- maintaining shared integration files such as `CHANGELOG.md` and the target plan's `Validation Results`
- running final repository validation on `main`
- creating the release from `main` after the whole plan is complete

### Worker

Each worker agent owns one bounded task with a clear write scope.

Each worker should:

- stay within its assigned files and responsibility
- implement the smallest spec-driven change for its task
- update task-local tests, docs, OpenAPI artifacts, and HTTP examples when its task requires them
- run the most relevant validation for its assigned task
- create a commit when that task is complete
- report changed files, validation performed, and the resulting commit SHA back to the coordinator

## Task Slicing For This Repository

Good parallel boundaries in this codebase usually follow package and contract boundaries, for example:

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
4. Each worker gets its own git worktree or branch for one task.
5. Each worker completes its task, validates it, and creates a task-level commit.
6. The coordinator reviews the result and integrates that completed task onto `main`.
7. After each integrated task, the coordinator updates `CHANGELOG.md` under `## [Unreleased]` for the newly completed work.
8. After each integrated task, the coordinator updates the plan state as needed and keeps progress moving.
9. After the whole plan is complete, the coordinator updates the plan's `Validation Results`.
10. The coordinator runs `.\gradlew.bat build` on `main`.
11. The coordinator creates the release from `main` by following `ai/RELEASES.md`, including `ROADMAP.md` cleanup, plan archival, and post-release cleanup of temporary execution worktrees and branches.

## Worktree Rules

When using git worktrees:

- keep `main` as the integration branch
- treat worktree branches as temporary execution branches, not release branches
- do not consider a task complete until its changes are integrated back onto `main`
- do not cut a release from a worktree branch or detached `HEAD`

A typical pattern is:

```powershell
git checkout main
git pull --ff-only
git worktree add ..\technical-interview-demo-<task> -b codex/<task> main
```

After a worker finishes:

```powershell
git checkout main
git cherry-pick <worker-commit-sha>
```

Use non-interactive git commands. Do not use destructive history rewrites unless the user explicitly asks for recovery work.

## Commit Rules

In this repository:

- each completed task gets its own commit
- do not wait and batch the entire plan into one final implementation commit
- update `CHANGELOG.md` under `## [Unreleased]` as each task lands
- keep commits narrow enough that they map cleanly to completed plan tasks

The release commit is separate from the task commits and is created only after the whole plan is implemented and validated.

## Validation Rules

Worker-level validation should be scoped to the task when possible, but the coordinator still owns the final repository gate.

Coordinator final validation must run on `main`:

```powershell
.\gradlew.bat build
```

If the plan changes:

- public API behavior, update tests, REST Docs, OpenAPI artifacts, HTTP examples, and contract docs together
- book list/search behavior, localization lookup behavior, or OAuth/session startup behavior, rerun `.\scripts\run-phase-9-benchmarks.ps1`

Treat failing compatibility or benchmark checks as spec failures.

## Release Rules

After the whole plan is complete:

- make sure all intended changes are already on `main`
- keep `CHANGELOG.md` aligned with the completed work
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
- the commit SHA to integrate
- any open issue that blocks clean integration

The coordinator should report progress in terms of integrated tasks on `main`, not just work completed in side branches.
