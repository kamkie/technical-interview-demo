---
name: select-mode-and-skills
description: Choose the repository workflow mode, role roster, and skill chain for a task or plan. Use when Codex must decide between M0 direct, M1 assisted, M2 delegated, M3 parallel, or M4 gated execution, prepare a multi-agent run, or explain why delegation is not useful.
---

# Select Mode And Skills

## Overview

Use this skill to turn a request or active plan into a concrete workflow shape.
Keep durable policy in `.agents/references/workflow.md`; this skill only guides selection.

## Read Set

- always: `AGENTS.md`, `.agents/references/workflow.md`, and the user request
- if planning or revising a plan: `.agents/references/planning.md` and the active plan
- if validation affects the mode decision: `.agents/references/testing.md`
- if docs, ADRs, skills, or guidance move: `.agents/references/documentation.md`

## Inputs

- request or plan path
- known write scopes, shared files, and stop conditions
- validation target and expected handoff result
- any user constraint on delegation, agents, or release work

## Workflow

1. Classify the work as ad hoc task, single plan task, whole plan, review, validation, or release work.
2. Identify shared files and whether two editing agents would touch the same write scope.
3. Pick the lowest sufficient mode:
   - `M0: direct` when one agent should do the work directly
   - `M1: assisted` when read-only review, verification, or specialist help adds value
   - `M2: delegated` when one Worker can own one bounded write scope
   - `M3: parallel` when multiple disjoint write scopes can run independently
   - `M4: gated` when independent review, verification, security, docs, release, or specialist gates are required
4. Name the role roster: Coordinator, Planner, Worker, Reviewer, Verifier, and Specialist as applicable.
5. Select only the skills needed for the chosen mode.
6. If delegation is used, hand off to `handoff-pack` with the mode, role, write scope, validation target, and stop conditions.
7. If the request is unclear enough that mode selection would invent scope, route to planning instead of guessing.

## Stop Conditions

- unclear product intent, write scope, validation target, or acceptance criteria
- overlapping write scopes for two editing agents
- release work requested before integration on `main`
- a plan-required workflow mode contradicts current execution reality

## Output

Report the selected mode, roles, skills, owned files, shared files, validation target, and any reason delegation was skipped.
