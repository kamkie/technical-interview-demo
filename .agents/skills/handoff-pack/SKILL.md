---
name: handoff-pack
description: Create complete repository handoff packets for delegated multi-agent work. Use when a Coordinator needs to brief a Worker, Reviewer, Verifier, Planner, or Specialist with objective, scope, state path, validation target, and stop conditions.
---

# Handoff Pack

## Overview

Use this skill to create a durable handoff packet before delegated work starts.
The authoritative handoff contract lives in `.agents/references/workflow.md`.

## Read Set

- always: `AGENTS.md`, `.agents/references/workflow.md`, and the source request or plan
- if the handoff edits code or docs: `.agents/references/execution.md`
- if the handoff is a plan authoring task: `.agents/references/planning.md`
- if validation is assigned: `.agents/references/testing.md`
- if review is assigned: `.agents/references/reviews.md`

## Inputs

- objective and source request or plan
- assigned role and workflow mode
- read scope, write scope, and out-of-scope files
- shared files owned by the Coordinator
- expected output, validation target, and stop conditions
- branch, worktree, or state path when durable state is needed

## Workflow

1. Confirm the work is safe to delegate under `.agents/references/workflow.md`.
2. Choose the narrowest state path:
   - handoff: `.agents/context/handoffs/`
   - worker report: `.agents/context/workers/`
   - review report: `.agents/context/reviews/`
   - verification report: `.agents/context/verifications/`
   - specialist report: `.agents/context/specialists/`
3. Use file names shaped like `<plan_stem_or_topic>__<role>.md`.
4. Write a packet with these fields: objective, source, role, mode, read scope, write scope, out-of-scope files, shared exclusions, expected output, validation target, stop conditions, reporting format, and integration readiness criteria.
5. Keep policy references as links to owner guides instead of copying standing rules.
6. If a handoff cannot name clear inputs, outputs, write scope, and stop conditions, route to `Replan?`.

## Stop Conditions

- overlapping write scopes
- missing acceptance criteria or validation target
- uncommitted work that should be committed before delegation
- release sequencing without an explicit release request
- a shared file must move from Coordinator ownership to a Worker without replanning

## Output

Create or update the handoff packet and summarize the assigned role, state path, write scope, validation target, and stop conditions.
