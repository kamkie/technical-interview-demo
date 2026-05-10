# Plan Authoring Guide For AI Agents

`.agents/references/plan-authoring-guide.md` owns on-demand examples and fill guidance for `.agents/plans/PLAN_*.md` files.
It does not own planning rules or required plan shape.

Source of truth:

- `.agents/references/planning.md` owns planning modes, read sets, lifecycle vocabulary, readiness rules, roadmap synchronization, milestone rules, routing rules, and final checks.
- `.agents/references/plan-template.md` owns the canonical plan skeleton and required sections.
- `docs/specs/application-lifecycle-spec.md` owns the lifecycle phase model.
- `docs/specs/lifecycle-phase-activities.md` owns activity and loop names used in planning prose.
- `.agents/references/documentation.md` owns artifact routing.
- `.agents/references/testing.md` owns validation scope.
- `.agents/references/plan-execution.md` and `.agents/references/execution.md` own execution after a plan is approved.

Use this guide only when `planning.md` and the template are not enough to decide how to fill a plan, review readiness, or shape milestones.
If this guide conflicts with `planning.md` or `plan-template.md`, follow those files and update this guide later.

## How To Use This Guide

Start with `.agents/references/planning.md`:

- use `Modes And Read Set` to choose the workflow and load only necessary context
- use `Lifecycle And Readiness` and `Planning Workflow` to decide whether the plan can be `Ready`
- use `Plan Files` and `Milestones And Tracking` for required plan content
- use `Routing Rules` for artifact ownership and validation routing
- use `Final Check` before handoff

Then use this guide for examples of what good plan content looks like.
Do not copy this guide into a plan; fill the template with task-specific facts.

## Authoring Posture

A good plan is a handoff document, not a research transcript.
It should let another agent execute without inventing missing product, contract, validation, or ownership decisions.

Prefer plans that are:

- spec-driven before implementation-driven
- narrow enough to preserve the demo character of the repository
- explicit about public contract impact
- milestone-based, with each milestone small enough to validate and commit
- clear about unresolved questions, accepted fallbacks, and decisions that should not be reopened during execution

Do not use `ROADMAP.md` as a substitute for a plan.
The roadmap tracks active work at a high level; the plan owns detailed milestones, progress, validation, blockers, and handoff context.

For non-blocking preference gaps, pick a conservative fallback and record it.
For product, compatibility, rollout, acceptance, or validation gaps, ask the user instead of hiding the gap as an assumption.

## Template Fill Notes

### Lifecycle And Readiness

Use the exact phase and status vocabulary from `planning.md`.
The readiness table should be mechanical and current:

```md
| Decision Complete | Yes |
| Blocking Open Questions | None |
| Accepted Fallbacks | D2 |
| Ready For Execution | Yes |
| Last Updated | 2026-05-10 |
```

If readiness depends on a fallback, the fallback should appear in both `Requirement Gaps And Open Questions` and `Decision Log And Assumptions`.

### Summary, Scope, And Current State

Keep `Summary` short: what changes, why it matters, and how success will be measured.
Use `Scope` to prevent quiet expansion during implementation.
Use `Current State` for facts discovered from specs, docs, source, tests, roadmap, or user input.

Avoid implementation guesses in `Current State`.
If a fact is inferred rather than directly observed, say so and record whether the inference affects readiness.

### Requirement Gaps And Open Questions

Every material question gets a stable `Q` ID.
Use the table to show what is missing, why it matters, who owns the answer, and whether the plan can proceed.

```md
| ID | Question / Gap | Why It Matters | Owner | Status | Fallback / Decision | Blocks Ready? |
| --- | --- | --- | --- | --- | --- | --- |
| Q1 | Should the new filter be public API or internal admin-only behavior? | Changes tests, docs, OpenAPI, and compatibility risk. | User | Open | Pending | Yes |
| Q2 | Should reviewer HTTP examples move? | Affects manual workflow but not runtime behavior. | Agent | Deferred | D2: update examples only if contract docs move. | No |
```

Do not delete answered questions during planning.
Change their status to `Answered` or `Deferred` and point to the decision log.

### Decision Log And Assumptions

Use `Decision Log And Assumptions` for answers that execution should not reopen casually.
Good decision rows name the source and revisit trigger:

```md
| ID | Decision / Assumption | Source | Date | Revisit Trigger |
| --- | --- | --- | --- | --- |
| D1 | Preserve existing response shape; add no new error envelope. | Approved OpenAPI baseline and user request | 2026-05-10 | Revisit only if user asks for breaking API cleanup. |
```

Use decision rows for explicit user decisions, repo-truth conclusions, accepted fallback assumptions, and non-obvious compatibility constraints.

### Execution Shape And Shared Files

Default to one local branch.
Choose delegated one-plan work only when milestones can be split into disjoint worker-owned slices.
Choose coordinated multi-plan work only when separate active plans can move independently.

When delegation is realistic, name shared files early.
Coordinator-owned files commonly include the canonical plan, `ROADMAP.md`, `CHANGELOG.md`, generated contract baselines, and shared docs.
Worker boundaries should be file or package ownership, not vague responsibility.

### Affected Artifacts

List likely artifacts by ownership and path, not by vague area.
For example:

- `src/test/java/...BookControllerTest.java`
- `src/docs/asciidoc/...`
- `src/test/resources/openapi/approved-openapi.json`
- `src/manualTests/http/examples/...`
- `.agents/references/testing.md`

For public API changes, expect specs, REST Docs, OpenAPI, manual HTTP examples, and README impact to be considered.
For internal refactors, say explicitly that OpenAPI, REST Docs, and README should not move unless behavior changes.

### Progress Tracker

The `Progress Tracker` is a dashboard for execution.
It should mirror the detailed milestone status, validation, blockers, and commit checkpoint.

Use `Pending` for commit and validation before work starts.
During planning, do not invent commit SHAs.

### Milestones

Strong milestones:

- include the spec, implementation, docs, and validation needed for one coherent behavior slice
- name exact files or packages where possible
- reserve shared files to the coordinator when delegation is possible
- include the smallest useful context read set
- state preserved behavior, not only new work
- define concrete validation and commit checkpoints

Weak milestones:

- say "update tests as needed"
- split one behavior across unrelated spec, implementation, and docs milestones without a reason
- require multiple workers to edit the same files at the same time
- ask the executor to decide compatibility, rollout, or acceptance criteria later

Example milestone:

```md
### Milestone 1: Add Book Filter Contract
| Field | Value |
| --- | --- |
| Status | Not Started |
| Goal | Define the supported request semantics before implementation. |
| Owned Files Or Packages | `src/test/java/.../BookControllerTest.java`, `src/docs/asciidoc/...` |
| Coordinator-Owned Shared Files | `src/test/resources/openapi/approved-openapi.json`, `CHANGELOG.md` |
| Context Required | `AGENTS.md`, `.agents/references/execution.md`, this plan, relevant book API tests, REST Docs snippets |
| Behavior To Preserve | Existing pagination, sorting, and error behavior remain unchanged. |
| Deliverables | Failing or updated executable contract specs plus published docs updates for the new filter. |
| Validation Checkpoint | Targeted book API tests document the intended request and response behavior. |
| Commit Checkpoint | Commit contract/spec changes before implementation if they are reviewable alone. |
```

### Blockers, Edge Cases, And Better Engineering Notes

Use `Blockers And Replan Triggers` for execution-time events, not initial planning questions.
Examples:

- validation reveals public behavior already differs from docs
- a generated OpenAPI baseline changes outside the planned endpoint
- a worker-owned file becomes a shared-file conflict
- the smallest coherent milestone is larger than expected

Use `Edge Cases And Failure Modes` for compatibility risks, migration concerns, rollout hazards, negative scenarios, and benchmark risks.
Use `Better Engineering Notes` for prerequisite cleanup included in scope or deferred follow-up work that should not be hidden.

### Validation Plan, Testing Strategy, And Results

`Validation Plan` names intended proof.
`Testing Strategy` explains which layers apply and which do not.
`Validation Results` records actual proof gathered during execution.

Good validation entries are exact:

```md
| Date | Command | Scope | Result | Notes |
| --- | --- | --- | --- | --- |
| 2026-05-10 | `./build.ps1 test` | Automated tests | Passed | Contract tests and docs snippets regenerated. |
```

If a required command cannot run, record the command, result as `Skipped` or `Failed`, and the reason.

### User Validation

Use this section for the shortest practical walkthrough the user can perform.
For API work, name the endpoint or HTTP example.
For docs or AI-guidance changes, name the file section to inspect and the expected behavior for the next agent.

## Planning Frames

### Public API Change

A public API plan should:

- start from integration tests, REST Docs tests, and the approved OpenAPI baseline
- define request, response, error, pagination, sorting, filtering, and compatibility semantics before coding
- name all published contract artifacts that must move
- call out benchmark or smoke checks required by `.agents/references/testing.md`
- include manual HTTP example updates when reviewer workflows should mirror the behavior

### Internal Refactor

An internal refactor plan should:

- state the existing specs are the contract to preserve
- avoid OpenAPI, REST Docs, and README churn unless behavior actually changes
- name the classes or packages being moved
- include targeted validation that proves behavior stayed stable
- keep abstraction small and consistent with the repository's demo scope

### AI-Guidance Or Workflow Change

An AI-guidance plan should:

- identify the owning `.agents/references/` file from `.agents/references/documentation.md`
- avoid redistributing standing policy across templates, task starters, or archived plans
- update the template only when the reusable artifact shape changes
- update execution or workflow guides when a planning change creates new execution duties
- validate with documentation review, `git diff --check`, and the standard wrapper command

### Delegated One-Plan Work

A delegated one-plan work shape should:

- keep one canonical plan as the coordination source
- name coordinator-owned files before workers start
- split worker ownership by file or package, not by vague responsibility
- require each worker milestone to produce standalone validation evidence
- leave shared-file integration to the coordinator

## Readiness Review Shape

When reviewing whether a plan is ready, lead with blockers.

```md
Readiness: Draft / Needs Input / Ready / Blocked

Blocking gaps:
- Q1: ...

Non-blocking risks:
- ...

Lifecycle check:
- Phase/Status matches the current state: yes/no
- Planning Readiness matches open questions and decisions: yes/no

Execution check:
- Milestones are commit-sized: yes/no
- Progress tracker matches milestones: yes/no
- Validation is concrete: yes/no
```

If there are no blocking gaps, say that clearly and name any residual risk.

## Companion Check

Before handing off a plan, compare it with `planning.md` `Final Check`.
Use this companion check only for fill quality:

- examples and assumptions are task-specific, not copied placeholder prose
- open questions and decisions are readable without the agent's private context
- affected artifacts are concrete enough for an executor to find them
- milestones are phrased as handoff instructions, not research notes
- validation and user validation are actionable
