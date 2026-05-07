# Planning Guide For AI Agents

`.agents/references/planning.md` owns the minimum standing rules for creating executable plans under `.agents/plans/`.
Detailed examples are on demand in `.agents/references/plan-detailed-guide.md`.
Use `.agents/templates/plan-template.md` when you need the full skeleton.

Use this file when the user asks for a plan, milestone breakdown, readiness review, or detailed change strategy.
Use `.agents/references/plan-execution.md` after the user asks to execute a whole active plan, `.agents/references/execution.md` for ad hoc tasks or one plan milestone, `.agents/references/workflow.md` for branch/worktree/delegation mechanics, `.agents/references/documentation.md` for artifact routing, and `.agents/references/testing.md` for validation scope.

## Lifecycle Metadata

Every active plan starts with:

```md
## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Planning |
| Status | Draft |
```

Use `Phase` for the coarse lifecycle:

- `Discovery`: repo research or framing is still underway
- `Planning`: the plan is being written, reviewed, or finalized
- `Implementation`: approved work is being built
- `Testing`: validation or verification work is the primary focus
- `Review`: security, maintainability, or peer review is the primary focus
- `Integration`: implementation is done and merge or release cleanup remains
- `Release`: release artifacts, tags, and documentation are being prepared
- `Closed`: no active execution remains

Use `Status` for immediate state:

- `Draft`
- `Needs Input`
- `Ready`
- `In Progress`
- `Blocked`
- `Implemented`
- `Released`

Keep the lifecycle block current as the plan moves.

## Planning Rules

A plan is ready only when it is decision-complete for another agent to execute without inventing missing choices.

Before writing the plan:

- read `AGENTS.md`
- inspect the governing specs, tests, docs, OpenAPI baseline, HTTP examples, and source files relevant to the requested behavior
- read `ROADMAP.md` so new or revised plans stay tied to active work and release sequencing
- read the owning AI guide when durable architecture, design, code-style, testing, review, release, documentation, workflow, or learning guidance changes
- read referenced tickets, PRs, examples, documents, or web pages before planning from them

Plan from repo truth first.
Ask the user only when ambiguity affects product intent, scope, compatibility, rollout, acceptance criteria, validation, or another material tradeoff.
Record requirement gaps, fallback assumptions, and locked decisions explicitly.

## Roadmap Synchronization

Creating or materially revising a concrete active plan must update `ROADMAP.md` in the same change.

Use `ROADMAP.md` for active-work tracking only:

- if the plan came from an existing roadmap item, add or refresh the plan path and concise lifecycle/status note there
- if the plan came from an ad hoc request that is now real planned work, add a concise roadmap entry in the appropriate active section with the plan path
- keep detailed milestones, validation, and implementation notes in the plan file, not the roadmap
- do not add a roadmap entry for discarded ideas, roadmap-only cleanup, or work that is not ready to become an execution plan

When a plan is later released or no longer active, follow the execution and release guides to update or remove the roadmap entry instead of leaving stale active work behind.

## Required Plan Content

Every concrete plan must be decision-complete enough for another agent to execute without inventing missing behavior.

At minimum, identify the behavior, governing specs or contract artifacts, scope and non-goals, affected files, compatibility promises, requirement gaps, execution shape and shared-file boundaries, per-milestone context requirements, milestone checkpoints, validation, and user verification.
Use `.agents/templates/plan-template.md` for the full skeleton and required-content checklist.

## Milestone Rules

Milestones should be commit-sized checkpoints.
Each milestone should name:

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

## Repo-Specific Rules

- Route public behavior, internal refactor, setup, AI-guidance, roadmap, and release-history changes through `.agents/references/documentation.md` instead of copying artifact lists into each plan.
- Use `.agents/references/testing.md` to name validation, benchmark, compatibility, and documentation-review proof for the planned change type.
- Internal refactors should preserve existing specs and avoid OpenAPI, REST Docs, HTTP example, or README churn unless behavior actually changes.
- Setup or environment changes belong in `SETUP.md`; AI-facing command-wrapper guidance belongs in `.agents/references/environment-quick-ref.md`.
- Durable architecture, design, code-style, testing, review, release, workflow, or engineering lessons belong in their focused AI owner files, not only in a temporary plan.
- Roadmap-only work updates `ROADMAP.md`; do not create an execution plan when work is not ready to implement.

## Plan Output Format

Create concrete plans under `.agents/plans/` as `PLAN_<topic>.md` with lowercase underscore topic names.
Use `.agents/templates/plan-template.md` for the full structure.

## Final Check

Before presenting a plan, verify that it:

- is self-contained
- identifies the governing specs
- confirms `ROADMAP.md` reflects the plan path and current active-work status
- separates scope from non-goals
- records unresolved gaps and fallback assumptions explicitly
- names likely files to change
- identifies execution shape and shared-file boundaries
- defines per-milestone context requirements without broad just-in-case reads
- defines specific, validatable milestones
- includes repo-specific validation
- respects the demo scope of the application
- does not hide compatibility, rollout, or benchmark consequences
