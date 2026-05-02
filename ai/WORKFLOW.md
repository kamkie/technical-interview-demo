# Codex Multi-Agent Workflow

`ai/WORKFLOW.md` explains how Codex should run a multi-agent workflow in this repository.

Use this file when the user wants delegation, parallel agent work, or a multi-worktree execution model for a planned change.
Use `AGENTS.md` for repository rules, `SETUP.md` for environment setup, `ai/PLAN.md` and `ai/PLAN_*.md` for planning, `ai/EXECUTION.md` for single-agent execution rules that still apply within a worker's local scope, and `ai/RELEASES.md` for the final release workflow.

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
- giving the user visible status updates when delegation starts, when a worker finishes, when work is blocked, and when the overall plan reaches a terminal state
- keeping `main` as the integration branch
- holding completed worker changes on execution branches until the whole plan is finished, then integrating the full plan onto `main`
- maintaining shared integration files such as `CHANGELOG.md` and the target plan's `Validation Results`
- running final repository validation on `main`
- creating the release from `main` after the whole plan is complete

### Worker

Each worker agent owns one bounded task with a clear write scope.

Each worker should:

- stay within its assigned files and responsibility
- follow `ai/EXECUTION.md` for task-local execution discipline unless this workflow overrides it
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
4. The coordinator tells the user which tasks were delegated, which files stay under coordinator ownership, and what completion signal to expect.
5. Each worker gets its own git worktree or branch for one task.
6. Each worker completes its task, validates it, and creates a task-level commit.
7. The coordinator reports worker completion back to the user as tasks finish, including validation status and any blocker that affects final integration.
8. The coordinator reviews each worker result, updates the plan state as needed, and keeps progress moving without integrating each task onto `main` immediately.
9. The coordinator updates `CHANGELOG.md` under `## [Unreleased]` on the integration branch as each completed task or milestone commit lands there.
10. After the whole plan is complete, the coordinator updates the plan's `Validation Results`.
11. The coordinator integrates the completed plan onto `main`.
12. The coordinator runs `.\gradlew.bat build` on `main`.
13. The coordinator sends an explicit final status message saying whether the plan is complete, what landed on `main`, what validation passed or failed, and whether any release or user decision remains.
14. The coordinator creates the release from `main` by following `ai/RELEASES.md`, including `ROADMAP.md` cleanup, plan archival, and post-release cleanup of temporary execution worktrees and branches.

## Worktree Rules

When using git worktrees:

- keep `main` as the integration branch
- treat worktree branches as temporary execution branches, not release branches
- do not consider a plan complete until its finished changes are integrated back onto `main`
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

After the whole plan is complete:

```powershell
git checkout main
git merge --ff-only codex/<plan>
```

Use non-interactive git commands. Do not use destructive history rewrites unless the user explicitly asks for recovery work.

## Commit Rules

In this repository:

- each completed task gets its own commit
- do not wait and batch the entire plan into one final implementation commit
- update `CHANGELOG.md` under `## [Unreleased]` as each completed task or milestone commit lands on the integration branch
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
- book list/search behavior, localization lookup behavior, or OAuth/session startup behavior, rerun `.\gradlew.bat gatlingBenchmark`

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
- the commit SHA to retain for final plan integration
- any open issue that blocks clean final integration

The coordinator should report progress in terms of completed plan tasks and readiness for final integration onto `main`, not just work completed in side branches.

Coordinator feedback must be user-visible and explicit:

- before delegation starts, report the task split, file ownership boundaries, and the conditions for plan completion
- when a worker finishes, report the completed task, changed files, validation status, and whether the result is ready for integration
- if work stalls, report the blocker instead of waiting silently
- when the plan reaches a terminal state, send a final completion message even if no release was requested

The final completion message should state:

- whether implementation is complete
- whether the finished work is integrated onto `main`
- whether final validation on `main` passed
- whether the plan is blocked, awaiting release work, or fully done
