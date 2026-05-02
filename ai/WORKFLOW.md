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
- phase-specific review or documentation work is too small to justify a separate handoff

## Coordinator

The coordinator agent owns:

- reading `AGENTS.md`, `README.md`, the target `ai/PLAN_*.md`, and any governing specs
- deciding whether the work is worth splitting
- keeping requirements, plan scope, and task boundaries consistent with the approved plan
- splitting the plan into bounded, non-overlapping tasks
- telling the user which phases or tasks were delegated, which files stay under coordinator ownership, and what completion signal to expect
- keeping `main` as the only integration and release target, while temporary execution branches or worktrees remain staging only
- maintaining shared integration files such as `CHANGELOG.md` and the target plan's `Validation Results`
- reviewing worker output for correctness, security, and documentation alignment before remote handoff
- running final repository validation before any push or PR creation
- pushing the finished branch and opening the PR only if the user asked for remote handoff
- starting release work only after the approved PR has been merged onto `main`

## Phase-Based Ownership

Think in phases even when only some phases are delegated. Do not spawn a dedicated worker for every phase by default. Use phase-specific workers only when the phase has enough independent work to justify the coordination cost.

### Requirements Gathering

Default owner: coordinator.

Delegate only when bounded repo research can clarify scope, compatibility, rollout, acceptance criteria, or validation without editing files.

Deliverables:

- explicit material open questions for the user, or an explicit statement that the request is decision-complete enough to proceed
- a short list of requirement gaps that were resolved from repo truth instead of guessed

### Planning

Default owner: coordinator.

A planning worker may create or revise `ai/PLAN_*.md` when the user explicitly wants multi-agent execution and plan writing is a distinct phase.

Deliverables:

- a plan that follows `ai/PLAN.md`
- locked assumptions, requirement gaps, and unresolved user-input holes recorded explicitly
- clear milestones, file ownership expectations, and validation scope

### Investigation

Good fit for read-only or explorer-style workers.

Use investigation workers for bounded questions such as locating governing tests, identifying current contract behavior, or mapping where a cross-cutting concern is implemented.

Deliverables:

- concise answers tied to exact files or packages
- risks or edge cases that coding, testing, review, or documentation phases must preserve

### Coding

Good fit for worker agents with disjoint write scopes.

Deliverables:

- the smallest spec-driven implementation change for the assigned scope
- required task-local tests or contract-artifact updates for that scope
- a narrow task-level commit and a report of changed files plus validation run

### Testing

Default owner: coordinator for final gates.

A testing worker is worthwhile when validation work can proceed in parallel with remaining coding or when a focused test-artifact slice has its own write scope.

Deliverables:

- added or updated tests when behavior changed
- explicit record of what validation ran, what failed, and what remains for the coordinator's final gate

### Code Review

Default owner: coordinator.

Use a dedicated review worker when an independent bug/regression pass adds value before the PR is opened.

Deliverables:

- findings focused on defects, regressions, spec drift, and missing validation
- exact file references or an explicit statement that no findings were discovered

### Security Review

Default owner: coordinator.

Use a dedicated security-review worker when the change touches authentication, authorization, secrets, logging of sensitive data, workflow permissions, container publication, or externally exposed configuration.

Deliverables:

- findings focused on security regressions, not style preferences
- explicit notes on auth, secret-handling, logging, dependency, workflow, or deployment-risk changes that need maintainer attention

### Documentation

A documentation worker is useful when README, contributing guidance, setup guidance, AI docs, REST Docs, HTTP examples, or OpenAPI wording all need coordinated updates but do not overlap heavily with active code edits.

Deliverables:

- updates in the owning documentation artifacts named by the plan
- cross-reference cleanup so human-facing and AI-facing guidance stay aligned where their scopes overlap

## Task Slicing For This Repository

Good parallel boundaries in this codebase usually follow package, contract, or artifact ownership boundaries, for example:

- `business.book`
- `business.category`
- `business.localization`
- `business.user`
- `technical.security`
- `technical.docs`, REST Docs pages, OpenAPI checks, and HTTP examples when the contract impact is isolated
- documentation-only slices such as `README.md` and `CONTRIBUTING.md` when they do not overlap active source edits

Avoid parallel splits when tasks would overlap on:

- the same controller or service
- the same integration test class
- the same REST Docs or OpenAPI contract artifact
- `CHANGELOG.md`
- the target `ai/PLAN_*.md`
- a single unresolved requirements or rollout decision

Shared files should stay under coordinator ownership unless there is a strong reason to do otherwise.

## Standard Flow

1. The coordinator reads the governing docs and specs.
2. The coordinator confirms the requirement gaps are closed or explicitly queued for user clarification.
3. The coordinator creates or revises the execution plan in `ai/PLAN_*.md` if needed.
4. The coordinator decides whether any requirements, planning, investigation, coding, testing, code review, security review, or documentation phases are worth delegating.
5. The coordinator splits delegated work into bounded tasks with explicit file ownership and reports that split to the user.
6. Workers complete their assigned tasks, run task-local validation, and create task-level commits.
7. The coordinator reviews worker results, updates `CHANGELOG.md` under `## [Unreleased]` as completed milestones land, and keeps the target plan's `Validation Results` current.
8. The coordinator integrates worker results onto the plan's local execution branch or worktree and keeps `main` reserved as the final merge target.
9. The coordinator runs final required validation on the integrated local result, using `ai/TESTING.md` for validation-scope decisions when needed.
10. The coordinator performs final code review, security review, and documentation-alignment checks, using `ai/REVIEWS.md` and `ai/DOCUMENTATION.md` as the owning guides.
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

The release commit is separate from the task commits and is created only after the whole plan is implemented, validated, reviewed, merged onto `main`, and explicitly selected for release work.

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

- the phase or task it completed
- the files it changed
- the validation it ran
- the commit SHA to retain for final plan integration
- any open issue that blocks clean final integration

The coordinator should report progress in terms of completed plan tasks and readiness for the next phase, not just work completed in side branches.

Coordinator feedback must be user-visible and explicit:

- before delegation starts, report the phase split, file ownership boundaries, and the conditions for plan completion
- when a worker finishes, report the completed phase or task, changed files, validation status, and whether the result is ready for integration
- if work stalls, report the blocker instead of waiting silently
- when the plan reaches a terminal state, send a final completion message even if no release was requested

The final completion message should state:

- whether implementation is complete
- whether the finished work is only locally complete, PR-ready, or already merged onto `main`
- whether final validation passed
- whether any release or user decision remains
