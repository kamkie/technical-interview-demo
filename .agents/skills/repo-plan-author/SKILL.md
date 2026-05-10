---
name: repo-plan-author
description: Create or revise repository execution plans under .agents/plans using the repo planning guide. Use when Codex needs a decision-complete PLAN_*.md, roadmap alignment, readiness review, or plan-task decomposition before implementation.
---

# Repo Plan Author

## Overview

Use this skill to create or revise an execution plan.
`.agents/references/planning.md` and `.agents/plans/PLAN_TEMPLATE.md` remain authoritative.

## Read Set

- always: `AGENTS.md`, `.agents/references/planning.md`, `.agents/plans/PLAN_TEMPLATE.md`
- when plan-backed work is selected: `ROADMAP.md`
- when artifact routing matters: `.agents/references/documentation.md`
- when workflow mode matters: `.agents/references/workflow.md`
- when validation scope matters: `.agents/references/testing.md`

## Inputs

- request, roadmap item, ADR, PRD, spec, or existing plan
- intended behavior or workflow change
- known scope, non-goals, blockers, validation needs, and acceptance criteria

## Workflow

1. Identify the behavior or workflow being planned and the governing truth artifact.
2. Decide lifecycle phase and whether a plan is appropriate.
3. Start from `.agents/plans/PLAN_TEMPLATE.md` for new plans.
4. Fill readiness, open questions, decisions, execution shape, affected artifacts, tasks, validation, blockers, and user validation.
5. Choose a concrete `M0: direct` through `M4: gated` workflow mode.
6. Keep `ROADMAP.md` aligned for concrete active plans.
7. Mark the plan `Ready` only when blocking questions are answered or have accepted fallbacks.

## Stop Conditions

- product intent, compatibility, rollout, validation, or ownership remains materially unclear
- the plan would invent behavior not backed by a user request or governing artifact
- two workers would need the same write scope
- release work is requested before integration on `main`

## Output

Create or revise the plan and report readiness, blocking questions, execution mode, validation plan, roadmap changes, and residual risk.
