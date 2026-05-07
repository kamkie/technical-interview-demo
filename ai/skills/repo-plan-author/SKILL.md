---
name: repo-plan-author
description: Create or revise repository-local `ai/PLAN_*.md` execution plans, lifecycle metadata, milestone breakdowns, and readiness reviews for this repository. Use when asked to plan work, turn roadmap items into a plan, revise an existing plan, review whether a plan is ready, or decide whether work should remain in `Discovery`, move to `Planning`, or wait for more input.
---

# Repo Plan Author

## Overview

Use this skill to create or revise plan files without redistributing standing policy out of `ai/PLAN.md`.
Keep the skill focused on plan entry work, then defer to the owner guides for execution, workflow, validation, and release rules.

## Read Set

Read only what the request needs:

- always: `AGENTS.md` and `ai/PLAN.md`
- roadmap-driven work: `ROADMAP.md`
- plan revision or readiness review: the target `ai/PLAN_*.md`
- behavior-sensitive work: the governing specs and contract docs named by the task
- add `README.md`, `ai/DESIGN.md`, `ai/ARCHITECTURE.md`, or `ai/BUSINESS_MODULES.md` only when product direction, public contract framing, or package ownership matters

## Workflow

1. State the behavior being planned and the governing spec or contract artifact.
2. Decide whether the work is still `Discovery`, ready for `Planning`, or blocked by missing input.
3. If creating a new plan, prefer one coherent `ai/PLAN_<topic>.md` unless the work is genuinely disjoint.
4. If revising an existing plan, update only the sections affected by the new requirement or constraint and keep milestone history intact.
5. Make every milestone a clean execution checkpoint that can be implemented, validated, and committed without inventing missing behavior.
6. Record requirement gaps, fallback assumptions, non-goals, validation, and execution-mode fit explicitly.
7. When reviewing plan readiness, list concrete gaps first, then say whether the lifecycle metadata is accurate and whether the plan is ready.

## Guardrails

- use the lifecycle vocabulary from `ai/PLAN.md`; do not invent a parallel scheme
- keep the plan spec-driven and narrow enough to preserve the repo's demo character
- do not convert roadmap bullets directly into implementation prose without checking current repo truth
- do not force `Single-Plan Fanout` or `Multi-Plan Fanout` when ownership boundaries are not explicit
- route standing policy back to `ai/PLAN.md`, `ai/WORKFLOW.md`, `ai/DOCUMENTATION.md`, and `ai/TESTING.md` instead of copying them into the plan

## Useful Prompt Titles

- `Create Plan`
- `Plan From Roadmap`
- `Plan Checked Roadmap Items`
- `Split Checked Roadmap Items Into Plans`
- `Revise Plan`
- `Review Plan Readiness`
- `Choose Execution Mode`

Use prompt titles from `ai/PROMPTS.md` when the user wants a reusable starter.
