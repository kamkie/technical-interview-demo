# Planning Guide For AI Agents

`.agents/references/planning.md` owns the standing rules for creating, revising, and reviewing executable plans under `.agents/plans/`.
It also owns plan fill guidance, readiness review shape, and plan-task quality rules.
Use `.agents/references/plan-template.md` only when you need the full skeleton.
Use `.agents/references/application-lifecycle.md` for lifecycle phase, activity, and loop vocabulary.

Use this file when the user asks for a plan, plan-task breakdown, readiness review, execution-shape decision, or detailed change strategy.
Use `.agents/references/plan-execution.md` after the user asks to execute a whole active plan, `.agents/references/execution.md` for ad hoc tasks or one plan task, `.agents/references/workflow.md` for branch, worktree, delegation, shared-file, or integration mechanics, `.agents/references/documentation.md` for artifact routing and pre-planning artifacts, and `.agents/references/testing.md` for validation scope.
Use `.agents/tasks/` only when the user asks for a named repository-specific task prompt that is more specific than direct plan authoring.

## Modes And Read Set

Choose the mode that matches the request:

- `Create Plan`: turn a concrete request into a new `.agents/plans/PLAN_<topic>.md`
- `Plan From Roadmap`: turn one triaged roadmap item into a plan and keep `ROADMAP.md` aligned
- `Revise Plan`: update an existing active plan for new information, constraints, or changed scope
- `Review Readiness`: decide whether a plan is `Ready`, `Needs Input`, `Blocked`, or still draft
- `Choose Execution Shape`: decide which `M0` through `M4` workflow mode fits the planned work before execution starts

Load only what the request needs:

- always: `AGENTS.md` and this file
- new plan or substantial revision: `.agents/references/plan-template.md`
- plan revision or readiness review: the target `.agents/plans/PLAN_*.md`
- triage-driven roadmap work or concrete active plans: `ROADMAP.md`
- lifecycle wording changes: `.agents/references/application-lifecycle.md`
- artifact routing: `.agents/references/documentation.md`
- validation selection: `.agents/references/testing.md`
- delegation, branch, worktree, shared-file, or integration planning: `.agents/references/workflow.md`
- behavior-sensitive work: the governing specs, contract docs, source files, tests, OpenAPI baseline, or HTTP examples named by the task or discovered by targeted search
- product, contract, or package framing: `README.md`, `docs/DESIGN.md`, or `.agents/references/architecture.md` only when directly relevant
- referenced tickets, PRs, examples, documents, or web pages before planning from them

Do not bulk-load active plans, archived plans, templates, task prompts, or reference guides as a pre-flight default.
Do not create or retain a separate descriptive companion for plan shape when this guide and the template already cover the behavior.

## Lifecycle And Readiness

Every active plan starts with a lifecycle block followed by a planning readiness snapshot:

```md
## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Planning |
| Status | Draft |

## Planning Readiness
| Field | Value |
| --- | --- |
| Decision Complete | No |
| Blocking Open Questions | Q1, Q2, or None |
| Accepted Fallbacks | None |
| Ready For Execution | No |
| Last Updated | YYYY-MM-DD |
```

Use `Phase` for the coarse lifecycle stage.
Valid plan phase values mirror the twelve phases in `.agents/references/application-lifecycle.md`: `Conceptualization`, `Analysis`, `Triage`, `Planning`, `Implementation`, `Verification`, `Review`, `Integration`, `Release`, `Deployment`, `Operations`, and `Maintenance`.

`Closed` is not a phase.
It is a terminal plan status for work whose lifecycle is complete and archived or intentionally retired.
When a plan uses `Status | Closed`, keep `Phase` set to the lifecycle phase that closed the work, normally `Release` or `Maintenance`.

Use these `Status` values only: `Draft`, `Needs Input`, `Ready`, `In Progress`, `Blocked`, `Implemented`, `Released`, and `Closed`.
Every phase transition is explicit: update the plan and any active-work owner artifact in the same change.

Keep `Planning Readiness` aligned with `Requirement Gaps And Open Questions` and `Decision Log And Assumptions`; it is a scan-friendly summary, not a parallel lifecycle scheme.
A plan with any `Open` question where `Blocks Ready?` is `Yes` must use `Status | Needs Input`.
Use `Status | Ready` only when every blocking question is answered or has an explicit accepted fallback recorded in both the open-question table and the decision log.
Keep answered or deferred questions visible so later executors can see how readiness was reached.

## Planning Workflow

A plan is ready only when it is decision-complete for another agent to execute without inventing missing product, contract, validation, or ownership choices.
Planning follows `Frame` -> `Elicit` -> `Analyze` -> `Define-Requirements` -> `Spec` -> `Decompose` -> `Validate-Plan` -> `Sync`, skipping earlier Analysis activities only when existing artifacts already answer them.
When the plan is not decision-complete or execution reality later disagrees with it, the Plan Loop uses `Replan?` and returns to `Validate-Plan` until the plan is ready again.

For every plan creation, revision, or readiness pass:

1. Identify the behavior or workflow being planned.
2. Identify the governing spec, contract, roadmap, ADR, PRD, standalone spec, or AI-guidance artifact before proposing implementation.
3. Decide whether the work is still `Conceptualization`, needs `Analysis`, belongs in `Triage`, is ready for `Planning`, or is blocked by missing input.
4. Resolve what repo truth already answers before asking the user.
5. Record remaining material questions as stable `Q` rows with owner, status, fallback or decision, and `Blocks Ready?`.
6. Record answered questions, repo-truth conclusions, and accepted fallback assumptions in `Decision Log And Assumptions`.
7. Choose an execution shape, linked pre-planning artifact set, plan tasks, validation, and user verification.
8. Keep `ROADMAP.md` aligned when creating or materially revising a concrete active plan.
9. Run this guide's final check before handoff.

Plan from repo truth first.
Ask the user only when ambiguity affects product intent, scope, compatibility, rollout, acceptance criteria, validation, or another material tradeoff.
For non-blocking preference gaps, choose a conservative fallback and record it.
Do not leave material open questions as loose prose.
Use an ADR, PRD, or standalone spec only when it resolves ambiguity that would otherwise make the plan invent product, behavior, contract, or process truth.
Route rough idea capture to `Conceptualization`, structured requirements or decision work to `Analysis`, active-work selection to `Triage`, and executable `.agents/plans/PLAN_*.md` creation to `Planning`.

## Plan Files

Create concrete plans under `.agents/plans/` as `PLAN_<topic>.md` with lowercase underscore topic names.
Prefer one coherent plan unless the work is genuinely disjoint.
Start new plans from `.agents/references/plan-template.md`.

Every concrete plan must identify the behavior, linked ADRs, PRDs, and standalone specs when they exist, governing executable specs or contract artifacts, scope and non-goals, affected files, compatibility promises, planning readiness, requirement gaps, locked decisions and assumptions, execution shape and shared-file boundaries, per-plan-task context requirements, plan-task checkpoints, progress tracking, validation, and user verification.
Keep the plan self-contained enough for another agent to execute without inventing missing decisions.
Use `Current State` for observed repo facts; when a fact is inferred, say so and record whether that inference affects readiness.

When revising a plan:

- update only sections affected by the new requirement, constraint, or discovered repo truth
- preserve plan-task history, validation evidence, and completed-progress notes unless the user explicitly asks for cleanup
- keep readiness, open questions, decisions, progress, plan tasks, blockers, and validation mutually consistent
- revise the plan before coding beyond approved scope when execution reality contradicts the plan

Creating or materially revising a concrete active plan must update `ROADMAP.md` in the same change.
Add or refresh the plan path and concise lifecycle/status note there, but keep detailed plan tasks, progress tracking, validation, and implementation notes in the plan file.
Do not add a roadmap entry for discarded ideas, roadmap-only cleanup, or work that is not ready to become an execution plan.
When a plan is later released or no longer active, follow the execution and release guides to update or remove the roadmap entry.

## Plan Tasks And Tracking

Plan tasks should be commit-sized checkpoints.
Each plan task should use fixed fields from the template and name status, goal, owned files or packages, coordinator-owned shared files if any, context required before execution, behavior to preserve, deliverables, validation checkpoint, and commit checkpoint.
Avoid vague plan-task instructions such as "update tests as needed" or leaving compatibility, rollout, validation, or acceptance decisions for the executor.

For context requirements, name the smallest useful read set for that plan task.
Use `none beyond AGENTS.md, .agents/references/execution.md, and this plan` when no extra context is needed.
Do not list broad descriptive docs as defensive context.

For execution shape, make the workflow decision during planning and record a concrete `M0` through `M4` mode.
Do not leave a concrete active plan's workflow mode for the executor to infer later.
For one coherent plan, prefer `M0` through `M2` unless the work clearly benefits from explicitly owned parallel worker slices or sidecar gates.
For a large single plan with plan tasks or slices separated enough for parallel ownership, suggest `M4: gated` and identify the worker slices plus review, verification, or specialist gates.
If delegation may be useful, state which files remain coordinator-owned and which files could be worker-owned.
If multiple plans must move together, strongly prefer `M3: parallel`; say which plan coordinates the work and how validation and changelog evidence will be rolled up.
Use a lower mode for multi-plan execution only when dependencies or shared artifacts force serial work, and use `M4: gated` only when sidecar gates are part of the planned workflow.

Every concrete plan should include a top-level `Progress Tracker` before detailed plan tasks.
Use plan-task statuses `Not Started`, `In Progress`, `Blocked`, `Done`, and `Skipped`.
Update the tracker whenever a plan task starts, blocks, completes, or is intentionally skipped.
For completed plan tasks, record the commit SHA or other checkpoint evidence and the validation result that proves the task.

Track execution-time blockers and replanning conditions in `Blockers And Replan Triggers`.
Keep that table separate from `Requirement Gaps And Open Questions`: open questions decide whether the plan is ready, while blockers and replan triggers describe what to do if execution later stops matching the plan.
Record validation history in the `Validation Results` ledger with date, command, scope, result, and notes.

## Readiness Review

When reviewing readiness, lead with concrete gaps.
Report:

- readiness result: `Ready`, `Needs Input`, `Blocked`, or `Draft`
- blocking open questions by `Q` ID
- missing fallbacks or decisions
- lifecycle and readiness metadata accuracy
- plan-task quality and whether progress tracking matches plan-task details
- validation completeness
- roadmap alignment when the plan is concrete active work

If no blocking gap remains, say so directly and name any residual non-blocking risk.

## Routing Rules

- Route public behavior, internal refactor, setup, AI-guidance, roadmap, and release-history changes through `.agents/references/documentation.md` instead of copying artifact lists into each plan.
- Use `.agents/references/testing.md` to name validation, benchmark, compatibility, and documentation-review proof for the planned change type.
- Internal refactors should preserve existing specs and avoid OpenAPI, REST Docs, or README churn unless behavior actually changes.
- Environment setup changes belong in `SETUP.md`; human-facing local command workflows belong in `docs/LOCAL_DEVELOPMENT.md`; AI-facing command-wrapper guidance belongs in `.agents/references/command-wrapper.md`.
- Durable architecture, design, code-style, testing, review, release, workflow, or engineering lessons belong in their focused AI owner files, not only in a temporary plan.
- Triage-only or roadmap-only work updates `ROADMAP.md`; do not create an execution plan when work is not ready to implement.

## Final Check

Before presenting a plan, verify that it:

- uses valid lifecycle `Phase` and `Status` values
- identifies linked ADR, PRD, or standalone spec artifacts when they exist, plus governing specs and likely changed files
- keeps `Planning Readiness`, open questions, decisions, progress, blockers, and validation aligned
- separates scope, non-goals, compatibility promises, unresolved gaps, and accepted fallbacks
- confirms `ROADMAP.md` reflects the plan path and active-work status when applicable
- identifies a concrete execution shape and shared-file boundaries
- defines commit-sized plan tasks with narrow context requirements and concrete validation
- keeps execution-time blockers separate from planning questions
- respects the demo scope of the application
- does not hide compatibility, rollout, validation, or benchmark consequences
