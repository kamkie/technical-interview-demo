# Execution Guide For AI Agents

`ai/EXECUTION.md` explains how AI agents should execute an approved plan or a bounded plan milestone in this repository.

Use this file when the user asks to implement `ai/PLAN_*.md`, complete a named milestone, or carry out planned work without releasing it yet.
Use `ai/WORKFLOW.md` when the user explicitly wants delegation or multi-agent execution. Use `ai/RELEASES.md` only after the approved implementation PR has been merged onto `main`.
Use `ai/DOCUMENTATION.md` for artifact ownership and `ai/TESTING.md` for validation scope instead of restating those standing rules here.

## Execution Goal

Execution in this repository means:

- implement the smallest spec-driven change that satisfies the target plan
- keep plan state, code, docs, and validation aligned as work lands
- preserve the current public contract unless the plan intentionally changes it
- finish local implementation and validation before any push or PR handoff
- if execution is happening in a git worktree, push the finished branch and open a PR instead of trying to merge directly onto `main` from that worktree
- stop before release unless the user explicitly asked for release work

## Before You Implement

Read these before editing:

- `AGENTS.md`
- `ai/PLAN.md`
- the target `ai/PLAN_*.md`
- the focused guides needed for the change: `ai/CODE_STYLE.md`, `ai/TESTING.md`, `ai/REVIEWS.md`, and `ai/DOCUMENTATION.md`
- the governing specs, docs, examples, and source files named by the plan

Before writing code or docs:

- confirm the plan is decision-complete enough to execute without inventing product behavior
- confirm whether you are executing the whole plan or only one milestone
- identify any plan gap that now blocks execution, and revise the plan before coding instead of filling the gap ad hoc
- resolve local machine-dependent environment variables before running env-dependent commands:
  - check the local `.env` file first when it exists
  - in PowerShell, prefer dot-sourcing `./scripts/load-dotenv.ps1` into the active shell before env-dependent commands instead of reimplementing ad hoc `.env` parsing
  - source or otherwise load the local `.env` values into the active shell or command environment before running env-dependent commands when that file is available locally
  - do not assume Gradle, Spring Boot, or an ad hoc terminal session auto-loads `.env`; make the load step explicit when the task depends on variables from that file
  - use `.env.example` only as the template for expected variable names, not as proof of the local values
  - prefer `.env` for values such as `JAVA_HOME`, `IDEA_HOME`, `IDEA_FORMATTER_BINARY`, `SPRING_PROFILES_ACTIVE`, and other local runtime or toolchain variables needed by the task
  - if `.env` is missing or incomplete, then inspect the relevant IDE or local machine configuration and report the fallback you used
  - never commit local machine-specific `.env` values unless the user explicitly asks for that

## Standard Single-Agent Flow

1. Re-read the target scope, locked decisions, and non-goals. Do not silently expand scope.
2. Update the governing spec artifact first when behavior is intentionally changing.
3. Implement the smallest code or documentation change that satisfies the updated spec.
4. Follow `ai/CODE_STYLE.md` for source or build-file edits.
5. Keep contract and maintainer artifacts aligned through `ai/DOCUMENTATION.md`.
6. Keep execution state current as work lands:
   - update the plan's `Lifecycle` section so `Phase` and `Status` still reflect reality
   - update `CHANGELOG.md` under `## [Unreleased]` after each completed plan task or milestone, unless `ai/WORKFLOW.md` replaces direct changelog edits with worker-owned temporary artifacts
   - create a normal non-interactive commit after each completed plan task or milestone
   - update the plan's `Validation Results` section with what actually ran and what happened
7. Run the required validation from `ai/TESTING.md`.
8. Perform a final local review using `ai/REVIEWS.md`.
9. If execution is happening in a git worktree, push the finished branch and create the PR only after steps 1 through 8 are complete. Outside worktree-based execution, do that only if the user asked for remote collaboration.
10. Stop after reporting execution status. Release work begins only after the approved PR has been merged onto `main`.

## Milestone-Only Execution

When the user asks for only one milestone:

- implement only the named milestone
- preserve the remaining plan structure for later execution
- do not start later milestones or cleanup implicitly
- update the plan's `Lifecycle` section only as far as the completed milestone justifies
- record only the validation actually run for that milestone in `Validation Results`
- do not prepare or cut a release
- if the milestone is being executed in a git worktree, push the finished branch and open the PR after local validation; otherwise do not push or open a PR unless the user explicitly asked for that remote handoff

## Execution Guardrails

- use `ai/DOCUMENTATION.md` to choose which contract or maintainer artifacts must move
- use `ai/TESTING.md` to choose the required validation and any benchmark or compatibility extras
- if execution is delegated, follow `ai/WORKFLOW.md` for worker-specific plan, changelog, and progress-file deviations
- if required validation cannot run, record that explicitly in the plan and in the final status report
- if the plan stops matching repo reality, revise the plan before continuing instead of improvising new scope

## Completion Criteria

Execution work is complete when:

- the targeted plan scope is implemented
- the target plan's `Lifecycle` section reflects the final non-release execution state
- required specs and documentation artifacts are aligned
- the target plan's `Validation Results` reflects actual execution
- unreleased history is recorded in the correct changelog artifact for the execution mode in use
- the required validation from `ai/TESTING.md` passed, or any blocker is explicitly recorded
- any required or requested push or PR happened only after local execution was complete
- release work remains undone unless the user explicitly asked for it
