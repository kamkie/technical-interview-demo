---
name: repo-task-execute
description: Execute bounded repository tasks or one plan task using the repo's spec-first implementation loop. Use for Worker-owned changes that need scoped edits, validation, review, plan tracking, and an AI-formatted commit.
---

# Repo Task Execute

## Overview

Use this skill for a bounded Worker implementation checkpoint.
It wraps the repo task loop without replacing `.agents/references/execution.md` or an active plan.

## Read Set

- always: `AGENTS.md`, `.agents/references/execution.md`, `.agents/references/code-style.md`, and the source request or plan task
- if executing one plan task: the active plan section for that task
- if validation is needed: `.agents/references/testing.md`
- if docs or AI guidance move: `.agents/references/documentation.md`
- before finalizing: `.agents/references/reviews.md`

## Inputs

- assigned objective and write scope
- governing spec, ADR, PRD, plan task, source file, or doc owner
- validation target
- stop conditions and out-of-scope files
- expected commit checkpoint

## Workflow

1. Confirm the task is bounded enough to execute without new planning.
2. Identify the behavior or documentation truth being changed.
3. Update the governing spec, ADR, plan, or documentation artifact before or alongside implementation.
4. Make the smallest coherent edit inside the assigned write scope.
5. Avoid unrelated refactors and files reserved to the Coordinator or another role.
6. Use `run-validation` or `.agents/references/testing.md` to run the required validation.
7. Use `diff-review` or `.agents/references/reviews.md` before handoff.
8. Update plan progress or workflow state with changed files, validation, risks, and readiness.
9. Commit the completed checkpoint with the repository AI commit-message footers.

## Stop Conditions

- product intent, compatibility, rollout, or validation is unclear
- the implementation needs a file outside the assigned write scope
- executable or published specs contradict the requested behavior
- validation fails and troubleshooting is not yet complete
- release work appears without an explicit release request

## Output

Report changed files, validation run, review result, commit checkpoint, blockers, residual risks, and integration readiness.
