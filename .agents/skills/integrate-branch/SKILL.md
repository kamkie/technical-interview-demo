---
name: integrate-branch
description: Integrate completed repository branches, worktrees, or worker outputs into the local integration state. Use when a Coordinator must merge or cherry-pick accepted work, resolve conflicts, validate after integration, and update plan or workflow state.
---

# Integrate Branch

## Overview

Use this skill for Coordinator-owned integration.
`.agents/references/workflow.md` remains authoritative for branch, worktree, sidecar, and handoff mechanics.

## Read Set

- always: `AGENTS.md`, `.agents/references/workflow.md`, and the active plan or handoff state
- when validation is required: `.agents/references/testing.md`
- when conflicts touch docs or guidance: `.agents/references/documentation.md`
- before final handoff: `.agents/references/reviews.md`

## Inputs

- source branch, worktree, commit, or worker output
- integration target, normally `main`
- changed files and validation evidence from the worker
- accepted gate decisions and known blockers

## Workflow

1. Confirm the worker output is terminal and ready for integration.
2. Check current `git status --short` and identify unrelated local changes.
3. Prefer normal merge for accepted branches; use cherry-pick only when accepting less than the full branch, the user asks, or merge is not viable.
4. Integrate one output at a time unless outputs are proven independent.
5. Resolve conflicts without discarding unrelated user changes.
6. Run proportional validation after integration.
7. Update plan or workflow state with commit, validation, blockers, and final status.
8. Leave release sequencing out unless explicitly requested.

## Stop Conditions

- source work is not ready for integration
- conflicts change behavior beyond the approved plan
- validation fails and cannot be isolated
- release preparation is requested but release preconditions are not met

## Output

Report integration method, source, target, changed files, conflict handling, validation, resulting commit or checkpoint, and remaining risk.
