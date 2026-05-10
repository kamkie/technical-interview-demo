# Planning Guide For AI Agents

`.agents/references/planning.md` owns the standing rules for creating, revising, and reviewing executable plans under `.agents/plans/`.
Detailed examples and fill guidance are on demand in `.agents/references/plan-authoring-guide.md`.
Use `.agents/templates/plan-template.md` when you need the full skeleton.
Use `docs/specs/application-lifecycle-spec.md` for lifecycle phase vocabulary and `docs/specs/lifecycle-phase-activities.md` for activity and loop names.

Use this file when the user asks for a plan, milestone breakdown, readiness review, or detailed change strategy.
Use `.agents/references/plan-execution.md` after the user asks to execute a whole active plan, `.agents/references/execution.md` for ad hoc tasks or one plan milestone, `.agents/references/workflow.md` for branch/worktree/delegation mechanics, `.agents/references/documentation.md` for artifact routing, and `.agents/references/testing.md` for validation scope.

## Planning Entry Modes

Choose the planning mode that matches the user request:

- `Create Plan`: turn a concrete request into a new `.agents/plans/PLAN_<topic>.md`
- `Plan From Roadmap`: turn one roadmap item into a plan and keep `ROADMAP.md` aligned
- `Revise Plan`: update an existing active plan for new information, constraints, or changed scope
- `Review Readiness`: decide whether a plan is `Ready`, `Needs Input`, `Blocked`, or still draft
- `Choose Execution Shape`: decide whether one branch, delegated one-plan work, or coordinated multi-plan work fits

Use `.agents/skills/repo-task/` task titles or slugs when the user wants a reusable task starter instead of direct plan authoring.

## Planning Read Set

Load only what the request needs:

- always: `AGENTS.md` and this file
- new plan or substantial revision: `.agents/templates/plan-template.md`
- detailed fill examples or readiness review shape: `.agents/references/plan-authoring-guide.md`
- plan revision or readiness review: the target `.agents/plans/PLAN_*.md`
- roadmap-driven work or concrete active plans: `ROADMAP.md`
- lifecycle wording changes: `docs/specs/application-lifecycle-spec.md` and `docs/specs/lifecycle-phase-activities.md`
- artifact routing: `.agents/references/documentation.md`
- validation selection: `.agents/references/testing.md`
- delegation, branch, worktree, shared-file, or integration planning: `.agents/references/workflow.md`
- behavior-sensitive work: the governing specs, contract docs, source files, tests, OpenAPI baseline, or HTTP examples named by the task or discovered by targeted search
- product, contract, or package framing: `README.md`, `docs/DESIGN.md`, or `docs/ARCHITECTURE.md` only when directly relevant

Do not bulk-load active plans, archived plans, templates, task files, or reference guides as a pre-flight default.

## Lifecycle Metadata

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
Valid plan phase values mirror the eleven phases in `docs/specs/application-lifecycle-spec.md`:

- `Discovery`: a request, idea, or signal is being scanned and framed
- `Roadmap Intake`: a promoted discovery item is being refined, prioritized, sequenced, and synced into active-work tracking
- `Planning`: a roadmap item is being turned into a decision-complete plan
- `Implementation`: an approved plan or bounded task is being built as the smallest spec-driven change
- `Testing`: locally complete implementation is being validated
- `Review`: a validated change is being reviewed for approval or requested changes
- `Integration`: an approved change is landing on the integration branch and post-merge checks are being confirmed
- `Release`: an integrated change is being prepared, tagged, published, and cleaned up as a release
- `Deployment`: a released artifact is being promoted and verified in a target environment
- `Operations`: a live artifact is being observed, triaged, patched, or scheduled for follow-up
- `Continuous Improvement`: release outcomes or recurring signals are being captured and fed back into active-work tracking

`Closed` is not a phase.
It is a terminal plan status for work whose lifecycle is complete and archived or intentionally retired.
When a plan uses `Status | Closed`, keep `Phase` set to the lifecycle phase that closed the work, normally `Release` or `Continuous Improvement`.

Use `Status` for immediate state:

- `Draft`
- `Needs Input`
- `Ready`
- `In Progress`
- `Blocked`
- `Implemented`
- `Released`
- `Closed`

Keep the lifecycle block current as the plan moves.
Every phase transition is explicit: update the plan and any active-work owner artifact in the same change.
Keep `Planning Readiness` aligned with the open-question table and decision log; it is a scan-friendly summary, not a parallel lifecycle scheme.

## Planning Rules

A plan is ready only when it is decision-complete for another agent to execute without inventing missing choices.
Planning follows the Planning phase activity sequence from `docs/specs/lifecycle-phase-activities.md`: `Frame` -> `Design` -> `Spec` -> `Decompose` -> `Validate-Plan` -> `Sync`.
When the plan is not decision-complete or execution reality later disagrees with it, the Plan Loop uses `Replan?` and returns to `Validate-Plan` until the plan is ready again.

For every plan creation, revision, or readiness pass:

1. Identify the behavior or workflow being planned.
2. Identify the governing spec, contract, roadmap, or AI-guidance artifact before proposing implementation.
3. Decide whether the work is still `Discovery`, belongs in `Roadmap Intake`, is ready for `Planning`, or is blocked by missing input.
4. Resolve what repo truth already answers before asking the user.
5. Record remaining material questions as stable `Q` rows with owner, status, fallback or decision, and `Blocks Ready?`.
6. Record answered questions, repo-truth conclusions, and accepted fallback assumptions in `Decision Log And Assumptions`.
7. Fill or update `Planning Readiness` so it matches the question and decision tables.
8. Choose an execution shape and name coordinator-owned or shared files when delegation is realistic.
9. Build commit-sized milestones using the template's fixed fields and smallest useful context read set.
10. Add or refresh the `Progress Tracker`, blocker or replan triggers, validation plan, validation ledger, and user validation.
11. Keep `ROADMAP.md` aligned when creating or materially revising a concrete active plan.
12. Run this guide's final check before handoff.

Plan from repo truth first.
Ask the user only when ambiguity affects product intent, scope, compatibility, rollout, acceptance criteria, validation, or another material tradeoff.
Record requirement gaps, fallback assumptions, and locked decisions explicitly.
Do not leave material open questions as loose prose.
Give each question or gap a stable `Q` ID, owner, status, fallback or decision, and `Blocks Ready?` value in the plan's `Requirement Gaps And Open Questions` table.

A plan with any `Open` question where `Blocks Ready?` is `Yes` must use lifecycle `Status | Needs Input`.
Use `Status | Ready` only when every blocking question is answered or has an explicit accepted fallback recorded in both the open-question table and the decision log.
Keep answered or deferred questions visible instead of deleting them, so later executors can see how readiness was reached.

## Roadmap Synchronization

Creating or materially revising a concrete active plan must update `ROADMAP.md` in the same change.
This is the planning `Sync` activity: active-work tracking must match the plan's current lifecycle state.

Use `ROADMAP.md` for active-work tracking only:

- if the plan came from an existing roadmap item, add or refresh the plan path and concise lifecycle/status note there
- if the plan came from an ad hoc request that is now real planned work, add a concise roadmap entry in the appropriate active section with the plan path
- keep detailed milestones, progress tracking, validation, and implementation notes in the plan file, not the roadmap
- do not add a roadmap entry for discarded ideas, roadmap-only cleanup, or work that is not ready to become an execution plan

When a plan is later released or no longer active, follow the execution and release guides to update or remove the roadmap entry instead of leaving stale active work behind.

## Required Plan Content

Every concrete plan must be decision-complete enough for another agent to execute without inventing missing behavior.

At minimum, identify the behavior, governing specs or contract artifacts, scope and non-goals, affected files, compatibility promises, planning readiness, requirement gaps, locked decisions and assumptions, execution shape and shared-file boundaries, per-milestone context requirements, milestone checkpoints, progress tracking, validation, and user verification.
Use `.agents/templates/plan-template.md` for the full skeleton and required-content checklist.

## Milestone Rules

Milestones should be commit-sized checkpoints.
Each milestone should name:

- status
- goal
- owned files or packages
- files reserved to the coordinator, if any
- context required before execution
- behavior to preserve
- exact deliverables
- validation checkpoint
- commit checkpoint

For context requirements, name the smallest useful read set for that milestone.
Use `none beyond AGENTS.md, .agents/references/execution.md, and this plan` when no extra context is needed; otherwise name the exact guide, reference, plan section, source package, or spec artifact to load before starting the milestone.
Do not list broad descriptive docs as defensive context.

For execution shape, prefer a single local branch unless the work clearly benefits from explicitly owned worker slices.
If delegation may be useful, state which files remain coordinator-owned and which files could be worker-owned.
If multiple plans must move together, say which plan coordinates the work and how validation and changelog evidence will be rolled up.

Every concrete plan should include a top-level `Progress Tracker` before detailed milestones.
Use milestone statuses `Not Started`, `In Progress`, `Blocked`, `Done`, and `Skipped`.
Update the tracker whenever a milestone starts, blocks, completes, or is intentionally skipped.
For completed milestones, record the commit SHA or other checkpoint evidence and the validation result that proves the milestone.

Use fixed fields for each milestone instead of loose bullets.
The repeated field set should match the template and include status, goal, ownership, context required, behavior to preserve, deliverables, validation checkpoint, and commit checkpoint.

Track execution-time blockers and conditions that require replanning in `Blockers And Replan Triggers`.
Keep that table separate from `Requirement Gaps And Open Questions`: open questions decide whether the plan is ready; blockers and replan triggers describe what to do if execution later stops matching the plan.
Record validation history in the `Validation Results` ledger with date, command, scope, result, and notes.

## Repo-Specific Rules

- Route public behavior, internal refactor, setup, AI-guidance, roadmap, and release-history changes through `.agents/references/documentation.md` instead of copying artifact lists into each plan.
- Use `.agents/references/testing.md` to name validation, benchmark, compatibility, and documentation-review proof for the planned change type.
- Internal refactors should preserve existing specs and avoid OpenAPI, REST Docs, or README churn unless behavior actually changes.
- Setup or environment changes belong in `SETUP.md`; AI-facing command-wrapper guidance belongs in `.agents/references/environment-quick-ref.md`.
- Durable architecture, design, code-style, testing, review, release, workflow, or engineering lessons belong in their focused AI owner files, not only in a temporary plan.
- Roadmap-only work updates `ROADMAP.md`; do not create an execution plan when work is not ready to implement.

## Plan Output Format

Create concrete plans under `.agents/plans/` as `PLAN_<topic>.md` with lowercase underscore topic names.
Use `.agents/templates/plan-template.md` for the full structure.

## Plan Creation And Revision

When creating a plan:

- prefer one coherent `.agents/plans/PLAN_<topic>.md` unless the work is genuinely disjoint
- use lowercase underscore topic names
- start from `.agents/templates/plan-template.md`
- keep the plan self-contained enough for another agent to execute without inventing product, contract, validation, or ownership decisions
- add a roadmap entry or refresh an existing one only for concrete active work
- set lifecycle `Status | Needs Input` when any open blocking question remains unresolved
- set lifecycle `Status | Ready` only when all blocking questions are answered or have explicit accepted fallbacks

When revising a plan:

- update only sections affected by the new requirement, constraint, or discovered repo truth
- preserve milestone history, validation evidence, and completed-progress notes unless the user explicitly asks for cleanup
- keep `Planning Readiness`, open questions, decision log, progress tracker, milestone details, blockers, and validation ledger mutually consistent
- if execution reality contradicts the plan, revise the plan before coding beyond approved scope
- update `ROADMAP.md` only when active-work status, scope, or plan path materially changes

## Readiness Review Output

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

## Final Check

Before presenting a plan, verify that it:

- is self-contained
- uses lifecycle `Phase` and `Status` values exactly as this guide defines them
- identifies the governing specs
- confirms `ROADMAP.md` reflects the plan path and current active-work status
- includes a readiness snapshot that matches the open-question and decision tables
- separates scope from non-goals
- records unresolved gaps and fallback assumptions explicitly
- uses stable question IDs with owner, status, fallback or decision, and blocking status
- keeps answered or deferred questions visible in the plan
- names likely files to change
- identifies execution shape and shared-file boundaries
- includes a progress tracker with milestone status, owner, commit, validation, and notes columns
- defines per-milestone context requirements without broad just-in-case reads
- defines specific, validatable milestones
- records execution-time blockers or replan triggers separately from planning questions
- includes repo-specific validation
- uses a validation results ledger for actual command and outcome evidence
- respects the demo scope of the application
- does not hide compatibility, rollout, or benchmark consequences
