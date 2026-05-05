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
- `Integration`: implementation is done and validation, merge, or release cleanup remains
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
- read `ROADMAP.md` when roadmap sequencing is involved
- read the owning AI guide when durable architecture, design, code-style, testing, review, release, documentation, workflow, or learning guidance changes
- read referenced tickets, PRs, examples, documents, or web pages before planning from them

Plan from repo truth first.
Ask the user only when ambiguity affects product intent, scope, compatibility, rollout, acceptance criteria, validation, or another material tradeoff.
Record requirement gaps, fallback assumptions, and locked decisions explicitly.

## Required Plan Content

Every concrete plan must answer:

- what behavior is changing and why
- what is out of scope
- which specs or contract artifacts define the behavior
- which source files or packages are likely to change
- what compatibility promises must be preserved
- what edge cases, failure modes, migration, rollout, or benchmark risks matter
- what requirement gaps still need input and whether they block planning
- which execution mode fits: `Single Branch`, `Shared Plan`, or `Parallel Plans`
- which files stay coordinator-owned if worker fanout is realistic
- which tests, docs, OpenAPI, HTTP examples, README, or AI guides must move
- what validation proves completion
- how the user can verify the delivered behavior

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

Prefer `Single Branch`.
Use `Shared Plan` only when one plan can be split into disjoint worker-owned slices.
Use `Parallel Plans` only when separate plan files can move independently with their own validation and temporary changelog copies.

## Repo-Specific Rules

- Public API changes must name affected tests, REST Docs, OpenAPI, HTTP examples, README, and benchmark implications routed through `ai/DOCUMENTATION.md` and `ai/TESTING.md`.
- Internal refactors should preserve existing specs and avoid OpenAPI, REST Docs, HTTP example, or README churn unless behavior actually changes.
- Setup or environment changes belong in `SETUP.md`; AI-facing command-wrapper guidance belongs in `ai/ENVIRONMENT_QUICK_REF.md`.
- Durable architecture, design, code-style, testing, review, release, workflow, or engineering lessons belong in their focused AI owner files, not only in a temporary plan.
- Roadmap-only work updates `ROADMAP.md`; do not create an execution plan when work is not ready to implement.

## Plan Output Format

Create concrete plans directly under `ai/` as `PLAN_<topic>.md` with lowercase underscore topic names.
Use `ai/templates/PLAN_TEMPLATE.md` for the full structure.

Required top-level sections:

- `Lifecycle`
- `Summary`
- `Scope`
- `Current State`
- `Requirement Gaps And Open Questions`
- `Locked Decisions And Assumptions`
- `Execution Mode Fit`
- `Affected Artifacts`
- `Execution Milestones`
- `Edge Cases And Failure Modes`
- `Validation Plan`
- `Better Engineering Notes`
- `Validation Results`
- `User Validation`

## Final Check

Before presenting a plan, verify that it:

- is self-contained
- identifies the governing specs
- separates scope from non-goals
- records unresolved gaps and fallback assumptions explicitly
- names likely files to change
- identifies execution mode and shared-file boundaries
- defines specific, validatable milestones
- includes repo-specific validation
- respects the demo scope of the application
- does not hide compatibility, rollout, or benchmark consequences
