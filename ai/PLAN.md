# Planning Guide For AI Agents

`ai/PLAN.md` owns the minimum standing rules for creating executable `ai/PLAN_*.md` files.
Detailed examples and the previous full guide are on demand in `ai/references/PLAN_DETAILED_GUIDE.md`.
Use `ai/templates/PLAN_TEMPLATE.md` when you need the full skeleton.

Use this file when the user asks for a plan, milestone, execution document, milestone breakdown, or detailed change strategy.
Use `ai/EXECUTION.md` after a plan is approved, `ai/WORKFLOW.md` for delegation or worktree modes, `ai/DOCUMENTATION.md` for artifact routing, and `ai/TESTING.md` for validation scope.

## Lifecycle Metadata

Every `ai/PLAN_*.md` starts with:

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

Creating or materially revising a concrete `ai/PLAN_*.md` file must update `ROADMAP.md` in the same change.

Use `ROADMAP.md` for active-work tracking only:

- if the plan came from an existing roadmap item, add or refresh the plan path and concise lifecycle/status note there
- if the plan came from an ad hoc request that is now real planned work, add a concise roadmap entry in the appropriate active section with the plan path
- keep detailed milestones, validation, and implementation notes in the plan file, not the roadmap
- do not add a roadmap entry for discarded ideas, roadmap-only cleanup, or work that is not ready to become an execution plan

When a plan is later released or no longer active, follow the execution and release guides to update or remove the roadmap entry instead of leaving stale active work behind.

## Required Plan Content

Every concrete plan must be decision-complete enough for another agent to execute without inventing missing behavior.

At minimum, identify the behavior, governing specs or contract artifacts, scope and non-goals, affected files, compatibility promises, requirement gaps, execution mode, shared-file boundaries, milestone checkpoints, validation, and user verification.
Use `ai/templates/PLAN_TEMPLATE.md` for the full skeleton and required-content checklist.

## Milestone Rules

Milestones should be commit-sized checkpoints.
Each milestone should name:

- goal
- owned files or packages
- shared files reserved to the coordinator, if any
- behavior to preserve
- exact deliverables
- validation checkpoint
- commit checkpoint

Prefer `Linear Plan`.
Use `Single-Plan Fanout` only when one plan can be split into disjoint worker-owned slices.
Use `Multi-Plan Fanout` only when separate plan files can move independently with their own validation and temporary changelog copies.

## Repo-Specific Rules

- Public API changes must name affected tests, REST Docs, OpenAPI, HTTP examples, README, and benchmark implications routed through `ai/DOCUMENTATION.md` and `ai/TESTING.md`.
- Internal refactors should preserve existing specs and avoid OpenAPI, REST Docs, HTTP example, or README churn unless behavior actually changes.
- Setup or environment changes belong in `SETUP.md`; AI-facing command-wrapper guidance belongs in `ai/ENVIRONMENT_QUICK_REF.md`.
- Durable architecture, design, code-style, testing, review, release, workflow, or engineering lessons belong in their focused AI owner files, not only in a temporary plan.
- Roadmap-only work updates `ROADMAP.md`; do not create an execution plan when work is not ready to implement.

## Plan Output Format

Create concrete plans directly under `ai/` as `PLAN_<topic>.md` with lowercase underscore topic names.
Use `ai/templates/PLAN_TEMPLATE.md` for the full structure.

## Final Check

Before presenting a plan, verify that it:

- is self-contained
- identifies the governing specs
- confirms `ROADMAP.md` reflects the plan path and current active-work status
- separates scope from non-goals
- records unresolved gaps and fallback assumptions explicitly
- names likely files to change
- identifies execution mode and shared-file boundaries
- defines specific, validatable milestones
- includes repo-specific validation
- respects the demo scope of the application
- does not hide compatibility, rollout, or benchmark consequences
