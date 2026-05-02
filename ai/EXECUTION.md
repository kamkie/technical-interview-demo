# Execution Guide For AI Agents

`ai/EXECUTION.md` explains how AI agents should execute an approved plan or a bounded plan milestone in this repository.

Use this file when the user asks to implement `ai/PLAN_*.md`, complete a named milestone, or carry out planned work without releasing it yet.
Use `ai/WORKFLOW.md` when the user explicitly wants delegation or multi-agent execution. Use `ai/RELEASES.md` only after the approved implementation PR has been merged onto `main`.

## Execution Goal

Execution in this repository means:

- implement the smallest spec-driven change that satisfies the target plan
- keep plan state, code, docs, and validation aligned as work lands
- preserve the current public contract unless the plan intentionally changes it
- finish local implementation and validation before any push or PR handoff
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

## Standard Single-Agent Flow

1. Re-read the target scope, locked decisions, and non-goals. Do not silently expand scope.
2. Update the governing spec artifact first when behavior is intentionally changing.
3. Implement the smallest code or documentation change that satisfies the updated spec.
4. Follow `ai/CODE_STYLE.md` for source or build-file edits.
5. Keep contract and maintainer artifacts aligned using `ai/DOCUMENTATION.md`.
6. Keep execution state current as work lands:
   - update `CHANGELOG.md` under `## [Unreleased]` after each completed plan task or milestone
   - create a normal non-interactive commit after each completed plan task or milestone
   - update the plan's `Validation Results` section with what actually ran and what happened
7. Run the required validation from `ai/TESTING.md`.
8. Perform a final local review using `ai/REVIEWS.md`.
9. If the user asked for remote collaboration, push the finished branch and create the PR only after steps 1 through 8 are complete.
10. Stop after reporting execution status. Release work begins only after the approved PR has been merged onto `main`.

## Milestone-Only Execution

When the user asks for only one milestone:

- implement only the named milestone
- preserve the remaining plan structure for later execution
- do not start later milestones or cleanup implicitly
- record only the validation actually run for that milestone in `Validation Results`
- do not prepare or cut a release
- do not push or open a PR unless the user explicitly asked for that remote handoff

## Execution Guardrails

- public API changes must move the full artifact set defined by `AGENTS.md` and scoped through `ai/DOCUMENTATION.md`
- internal refactors should preserve current contract artifacts unless behavior actually changed
- setup changes belong in `SETUP.md`
- roadmap-only changes belong in `ROADMAP.md`
- if required validation cannot run, record that explicitly in the plan and in the final status report
- if the plan stops matching repo reality, revise the plan before continuing instead of improvising new scope

## Completion Criteria

Execution work is complete when:

- the targeted plan scope is implemented
- required specs and documentation artifacts are aligned
- the target plan's `Validation Results` reflects actual execution
- `CHANGELOG.md` under `## [Unreleased]` reflects the unreleased work
- the required validation from `ai/TESTING.md` passed, or any blocker is explicitly recorded
- any requested push or PR happened only after local execution was complete
- release work remains undone unless the user explicitly asked for it
