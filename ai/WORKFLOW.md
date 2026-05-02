# Codex Multi-Agent Workflow

`ai/WORKFLOW.md` explains how Codex should run delegated or parallel execution in this repository.

Use this file when the user explicitly wants delegation, sub-agents, parallel work, or a multi-worktree execution model.
Use `AGENTS.md` for repository rules, `ai/PLAN.md` and `ai/PLAN_*.md` for planning, `ai/EXECUTION.md` for worker-local execution discipline, `ai/TESTING.md` for validation scope, `ai/DOCUMENTATION.md` for artifact alignment, and `ai/RELEASES.md` only after an approved implementation PR has been merged onto `main`.

## When To Use Multi-Agent Execution

Use a multi-agent workflow only when the user explicitly asks for delegation, sub-agents, or parallel work.

Prefer a single agent when:

- the change is small
- multiple agents would touch the same files
- the next step is tightly blocked on one coupled change
- coordination cost would exceed the parallelism benefit
- the review, testing, or documentation slice is too small to justify a handoff

## Coordinator

The coordinator owns:

- reading the governing docs, specs, and target plan
- deciding whether the work is worth splitting
- keeping requirements, scope, and task boundaries consistent with the approved plan
- splitting work into bounded, non-overlapping tasks with explicit file ownership
- keeping shared integration files such as `CHANGELOG.md` and the target plan's `Validation Results`
- integrating worker output onto the local execution branch or worktree
- running final validation from `ai/TESTING.md`
- running final review and documentation-alignment checks using `ai/REVIEWS.md` and `ai/DOCUMENTATION.md`
- pushing the finished branch and opening the PR only if the user asked for remote handoff
- starting release work only after the approved PR has been merged onto `main`

## Phase-Based Ownership

Think in phases even when only some phases are delegated. Do not spawn a dedicated worker for every phase by default. Use phase-specific workers only when the phase has enough independent work to justify the coordination cost.

### Requirements Gathering

Default owner: coordinator.

Delegate only when bounded repo research can clarify scope, compatibility, rollout, acceptance criteria, or validation without editing files.

Deliverables:

- explicit material open questions for the user, or an explicit statement that the request is decision-complete enough to proceed
- a short list of requirement gaps resolved from repo truth instead of guessed behavior

### Planning

Default owner: coordinator.

A planning worker may create or revise `ai/PLAN_*.md` when the user explicitly wants multi-agent execution and planning is a distinct phase.

Deliverables:

- a plan that follows `ai/PLAN.md`
- locked assumptions, requirement gaps, and unresolved user-input holes recorded explicitly
- clear milestones, validation scope, and file ownership expectations

### Investigation

Good fit for read-only or explorer-style workers.

Use investigation workers for bounded questions such as locating governing tests, identifying current contract behavior, or mapping where a cross-cutting concern is implemented.

Deliverables:

- concise answers tied to exact files or packages
- risks or edge cases that later phases must preserve

### Coding

Good fit for worker agents with disjoint write scopes.

Deliverables:

- the smallest spec-driven implementation change for the assigned scope
- required task-local test or contract-artifact updates for that scope
- a narrow task-level commit and a report of changed files plus validation run

### Testing

Default owner: coordinator for final gates.

Use a testing worker when validation can proceed in parallel with remaining coding or when a focused test-artifact slice has its own write scope.

Deliverables:

- added or updated tests when behavior changed
- explicit record of what validation ran, what failed, and what remains for the coordinator's final gate

### Code Review

Default owner: coordinator.

Use a dedicated review worker when an independent bug and regression pass adds value before the PR is opened.

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

Use a documentation worker when README, contributing guidance, setup guidance, AI docs, REST Docs, HTTP examples, or OpenAPI wording need coordinated updates and the write scopes do not heavily overlap active code edits.

Deliverables:

- updates in the owning artifacts named by the plan
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
- the same REST Docs or OpenAPI artifact
- `CHANGELOG.md`
- the target `ai/PLAN_*.md`
- a single unresolved requirements or rollout decision

Shared files should stay under coordinator ownership unless there is a strong reason to do otherwise.

## Standard Flow

1. The coordinator reads the governing docs and specs.
2. The coordinator confirms requirement gaps are closed or explicitly queued for user clarification.
3. The coordinator creates or revises the execution plan if needed.
4. The coordinator decides which phases, if any, are worth delegating.
5. The coordinator splits delegated work into bounded tasks with explicit file ownership and reports that split to the user.
6. Workers execute their assigned scope, following `ai/EXECUTION.md` inside that scope.
7. The coordinator integrates worker results, keeps `CHANGELOG.md` and the target plan current, and resolves any shared-file edits.
8. The coordinator runs final validation, review, and documentation-alignment checks.
9. If the user asked for remote collaboration, the coordinator pushes the finished branch and opens the PR as the last execution step.
10. After the approved PR is merged onto `main`, release work may begin only if the user explicitly asked for it.
11. The coordinator reports whether the plan is locally complete, PR-ready, merged to `main`, blocked, or waiting on a release decision.

## Worktree Rules

When using git worktrees:

- keep `main` as the only integration and release target
- treat worktree branches as temporary execution branches, not release branches
- do not consider a plan complete until the finished changes are ready for PR merge into `main`
- do not consider release work started until the approved PR has actually been merged
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

## Commit And Reporting Expectations

In this repository:

- each completed task gets its own commit
- keep commits narrow enough that they map cleanly to completed plan tasks
- do not batch the entire plan into one final implementation commit
- worker handoffs must report changed files, validation run, commit SHA, and any blocker to integration
- coordinator updates should report progress in terms of completed plan tasks and readiness for the next phase

The final completion message should state:

- whether implementation is complete
- whether the finished work is locally complete, PR-ready, or already merged onto `main`
- whether final validation passed
- whether any release or user decision remains
