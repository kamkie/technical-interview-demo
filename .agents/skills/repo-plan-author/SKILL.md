---
name: repo-plan-author
description: Create, revise, or review repository-local `.agents/plans/PLAN_*.md` execution plans, including lifecycle metadata, planning readiness, structured open questions, decision logs, milestone breakdowns, progress tracking, and readiness reviews. Use when asked to plan work, turn roadmap items into a plan, revise an active plan, review whether a plan is ready, or decide whether work should remain in `Discovery`, move to `Planning`, or wait for input.
---

# Repo Plan Author

## Role

Use this skill as the entry workflow for repository planning.
It helps an agent create, revise, or review `.agents/plans/PLAN_*.md` files without turning the skill into a second policy source.

Standing authority stays in:

- `.agents/references/planning.md` for planning rules, lifecycle vocabulary, readiness rules, milestone requirements, roadmap synchronization, and final checks
- `.agents/templates/plan-template.md` for the canonical plan skeleton
- `.agents/references/plan-detailed-guide.md` for examples and fill guidance
- `.agents/references/documentation.md` for artifact routing
- `.agents/references/testing.md` for validation scope
- `.agents/references/workflow.md` only when branch, worktree, delegation, worker-log, or integration mechanics are part of the planning decision

## Operating Modes

Choose the mode that matches the user request:

- `Create Plan`: turn a concrete request into a new `.agents/plans/PLAN_<topic>.md`
- `Plan From Roadmap`: turn one roadmap item into a plan and keep `ROADMAP.md` aligned
- `Revise Plan`: update an existing active plan for new information, constraints, or changed scope
- `Review Readiness`: decide whether a plan is `Ready`, `Needs Input`, blocked, or still draft
- `Choose Execution Shape`: decide whether one branch, delegated one-plan work, or coordinated multi-plan work fits

Use `$repo-task` task titles or slugs when the user wants a reusable task starter instead of direct plan authoring.

## Read Set

Read only what the request needs:

- always: `AGENTS.md`, `.agents/references/planning.md`
- new plan or substantial revision: `.agents/templates/plan-template.md`
- detailed fill examples or readiness review shape: `.agents/references/plan-detailed-guide.md`
- plan revision or readiness review: the target `.agents/plans/PLAN_*.md`
- roadmap-driven work: `ROADMAP.md`
- artifact routing: `.agents/references/documentation.md`
- validation selection: `.agents/references/testing.md`
- delegation or shared-file planning: `.agents/references/workflow.md`
- behavior-sensitive work: the governing specs, contract docs, source files, tests, OpenAPI baseline, or HTTP examples named by the task or discovered by targeted search
- product, contract, or package framing: `README.md`, `docs/DESIGN.md`, or `docs/ARCHITECTURE.md` only when directly relevant

Do not bulk-load active plans, archived plans, templates, task files, or reference guides as a pre-flight default.

## Workflow

1. Identify the behavior or workflow being planned.
2. Identify the governing spec, contract, roadmap, or AI-guidance artifact before proposing implementation.
3. Decide whether the work is still `Discovery`, ready for `Planning`, or blocked by missing input.
4. Resolve what repo truth already answers before asking the user.
5. Record remaining material questions as stable `Q` rows with owner, status, fallback or decision, and `Blocks Ready?`.
6. Record answered questions, repo-truth conclusions, and accepted fallback assumptions in `Decision Log And Assumptions`.
7. Fill or update `Planning Readiness` so it matches the question and decision tables.
8. Choose an execution shape and name coordinator-owned or shared files when delegation is realistic.
9. Build commit-sized milestones using the template's fixed fields and smallest useful context read set.
10. Add or refresh the `Progress Tracker`, blocker/replan triggers, validation plan, validation ledger, and user validation.
11. Keep `ROADMAP.md` aligned when creating or materially revising a concrete active plan.
12. Run the planning final check from `.agents/references/planning.md` before handoff.

## Creation Rules

When creating a plan:

- prefer one coherent `.agents/plans/PLAN_<topic>.md` unless the work is genuinely disjoint
- use lowercase underscore topic names
- start from `.agents/templates/plan-template.md`
- keep the plan self-contained enough for another agent to execute without inventing product, contract, validation, or ownership decisions
- add a roadmap entry or refresh an existing one only for concrete active work
- set lifecycle `Status | Needs Input` when any open blocking question remains unresolved
- set lifecycle `Status | Ready` only when all blocking questions are answered or have explicit accepted fallbacks

## Revision Rules

When revising a plan:

- update only sections affected by the new requirement, constraint, or discovered repo truth
- preserve milestone history, validation evidence, and completed-progress notes unless the user explicitly asks for cleanup
- keep `Planning Readiness`, open questions, decision log, progress tracker, milestone details, blockers, and validation ledger mutually consistent
- if execution reality contradicts the plan, revise the plan before coding beyond approved scope
- update `ROADMAP.md` only when active-work status, scope, or plan path materially changes

## Readiness Review Rules

When reviewing readiness, lead with concrete gaps.

Report:

- readiness result: `Ready`, `Needs Input`, `Blocked`, or `Draft`
- blocking open questions by `Q` ID
- missing fallbacks or decisions
- lifecycle and readiness metadata accuracy
- milestone quality and whether progress tracking matches milestone details
- validation completeness
- roadmap alignment when the plan is concrete active work

If no blocking gap remains, say so directly and name any residual non-blocking risk.

## Guardrails

- do not invent lifecycle values, milestone status values, or a parallel readiness scheme
- do not convert roadmap bullets directly into implementation prose without checking current repo truth
- do not bury material product, compatibility, rollout, acceptance, or validation gaps in assumptions
- do not force delegated or coordinated work when ownership boundaries are not explicit
- do not duplicate setup, workflow, validation, or artifact-routing policy in plans or this skill
- route standing policy changes back to the owner guide named in `.agents/references/documentation.md`
- keep the plan spec-driven, narrow, and consistent with the repository's demo character

## Useful Task Titles

- `Create Plan`
- `Plan From Roadmap`
- `Plan Checked Roadmap Items`
- `Split Checked Roadmap Items Into Plans`
- `Revise Plan`
- `Review Plan Readiness`
- `Choose Execution Shape`
