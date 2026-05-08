# Plan Authoring Guide For AI Agents

This is the on-demand companion for creating strong `.agents/plans/PLAN_*.md` files.
It does not own planning rules or required plan shape.

Source of truth:

- `.agents/references/planning.md` owns lifecycle vocabulary, readiness rules, milestone rules, roadmap synchronization, and final checks.
- `.agents/templates/plan-template.md` owns the canonical plan skeleton and required sections.
- `.agents/references/documentation.md` owns artifact routing.
- `.agents/references/testing.md` owns validation scope.
- `.agents/references/plan-execution.md` and `.agents/references/execution.md` own execution after a plan is approved.

Use this guide only when the compact planning guide and template are not enough to decide how to fill a plan, review readiness, or shape milestones.
If this guide conflicts with `planning.md` or `plan-template.md`, follow those files and update this guide later.

## Planning Posture

A good plan is a handoff document, not a narrative of the agent's research.
It should let another agent execute without inventing missing product, contract, validation, or ownership decisions.

Prefer plans that are:

- spec-driven before implementation-driven
- narrow enough to preserve the demo character of the repository
- explicit about public contract impact
- milestone-based, with each milestone small enough to validate and commit
- clear about unresolved questions, accepted fallbacks, and decisions that should not be reopened during execution

Do not use `ROADMAP.md` as a substitute for a plan.
The roadmap tracks active work at a high level; the plan owns detailed milestones, progress, validation, blockers, and handoff context.

## Planning Flow

Use this sequence when filling the template:

1. Identify the behavior or workflow being changed.
2. Find the governing spec or contract artifacts before proposing implementation.
3. Resolve what repo truth already answers.
4. Record remaining questions in the open-question table, with blocking status.
5. Lock answered questions and fallback assumptions in the decision log.
6. Choose the execution shape and shared-file boundaries.
7. Define milestone checkpoints that can each be validated and committed.
8. Define validation and user verification in concrete terms.
9. Check that roadmap state, readiness, progress tracking, and required artifacts line up.

Ask the user only when ambiguity changes product intent, compatibility, rollout, acceptance criteria, validation, or another material tradeoff.
For non-blocking preference gaps, pick a conservative fallback and record it.

## Filling The Template

### Lifecycle And Readiness

Use `Lifecycle` for the plan's coarse state and immediate execution state.
Use `Planning Readiness` to make the decision state scannable.

`Status | Ready` means no blocking open question remains unresolved.
If any open question has `Blocks Ready? | Yes`, the lifecycle status should be `Needs Input`.
If a blocking question is deferred, the fallback must be explicit in both `Requirement Gaps And Open Questions` and `Decision Log And Assumptions`.

Good readiness rows are mechanical and current:

```md
| Decision Complete | Yes |
| Blocking Open Questions | None |
| Accepted Fallbacks | D2 |
| Ready For Execution | Yes |
| Last Updated | 2026-05-08 |
```

### Summary, Scope, And Current State

Keep `Summary` short: what changes, why it matters, and how success will be measured.
Use `Scope` to prevent quiet expansion during implementation.
Use `Current State` for facts discovered from specs, docs, source, tests, roadmap, or user input.

Avoid implementation guesses in `Current State`.
If a fact is inferred rather than directly observed, say so and record whether the inference matters to readiness.

### Open Questions

Every material question gets a stable `Q` ID.
Use the table to show what is missing, why it matters, who owns the answer, and whether the plan can proceed.

Example:

```md
| ID | Question / Gap | Why It Matters | Owner | Status | Fallback / Decision | Blocks Ready? |
| --- | --- | --- | --- | --- | --- | --- |
| Q1 | Should the new filter be public API or internal admin-only behavior? | Changes tests, docs, OpenAPI, and compatibility risk. | User | Open | Pending | Yes |
| Q2 | Should existing examples be updated? | Affects reviewer workflow but not runtime behavior. | Agent | Deferred | D2: update examples only if contract docs move. | No |
```

Do not delete answered questions during planning.
Change their status to `Answered` or `Deferred` and point to the decision log.

### Decisions And Assumptions

Use `Decision Log And Assumptions` for answers that execution should not reopen casually.
Good decision rows name the source and revisit trigger:

```md
| ID | Decision / Assumption | Source | Date | Revisit Trigger |
| --- | --- | --- | --- | --- |
| D1 | Preserve existing response shape; add no new error envelope. | Approved OpenAPI baseline and user request | 2026-05-08 | Revisit only if user asks for breaking API cleanup. |
```

Use decision rows for:

- explicit user decisions
- repo-truth conclusions
- accepted fallback assumptions
- non-obvious compatibility constraints

### Execution Shape And Shared Files

Default to one local branch.
Choose delegated one-plan work only when milestones can be split into disjoint worker-owned slices.
Choose coordinated multi-plan work only when separate active plans can move independently.

When delegation is realistic, name shared files early.
Coordinator-owned files commonly include the canonical plan, `ROADMAP.md`, `CHANGELOG.md`, generated contract baselines, and shared docs.

### Affected Artifacts

List likely artifacts by ownership, not by vague area.
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

Use these statuses only: `Not Started`, `In Progress`, `Blocked`, `Done`, `Skipped`.
Use `Pending` for commit and validation before work starts.
During planning, do not invent commit SHAs.

### Milestones

Each milestone should be a commit-sized checkpoint.
Repeat the template's fixed field set for every milestone.

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

### Blockers And Replan Triggers

Use this table for execution-time events, not initial planning questions.
Examples:

- validation reveals public behavior already differs from docs
- a generated OpenAPI baseline changes outside the planned endpoint
- a worker-owned file becomes a shared-file conflict
- the smallest coherent milestone is larger than expected

The response should say whether to pause, ask the user, revise the plan, narrow scope, or split work.

### Validation Plan And Results

`Validation Plan` names intended proof.
`Validation Results` records actual proof gathered during execution.

Good validation entries are exact:

```md
| Date | Command | Scope | Result | Notes |
| --- | --- | --- | --- | --- |
| 2026-05-08 | `./build.ps1 test` | Automated tests | Passed | Contract tests and docs snippets regenerated. |
```

If a required command cannot run, record the command, result as `Skipped` or `Failed`, and the reason.

### User Validation

Use this section for the shortest practical walkthrough the user can perform.
For API work, name the endpoint or HTTP example.
For docs or AI-guidance changes, name the file section to inspect and the expected behavior for the next agent.

## Planning Frames

### Public API Change

A public API plan should:

- start from integration tests, REST Docs tests, and approved OpenAPI baseline
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
- validate with documentation review and `git diff --check`

### Delegated One-Plan Work

A delegated one-plan work shape should:

- keep one canonical plan as the coordination source
- name coordinator-owned files before workers start
- split worker ownership by file or package, not by vague responsibility
- require each worker milestone to produce standalone validation evidence
- leave shared-file integration to the coordinator

## Readiness Review Shape

When reviewing whether a plan is ready, lead with blockers.

Use this shape:

```md
Readiness: Needs Input / Ready / Not Ready

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

## Final Detailed Check

Before handing off a plan, confirm:

- `Lifecycle` and `Planning Readiness` agree with the open-question and decision tables
- unresolved questions have owners, status values, fallback or decision fields, and blocking status
- accepted fallbacks are visible in the decision log
- scope, non-goals, compatibility promises, and edge cases are explicit
- affected artifacts follow `.agents/references/documentation.md`
- milestone context fields name the smallest useful read set
- milestones are commit-sized and use the template's fixed field set
- `Progress Tracker` mirrors the milestone list
- execution-time blockers are separate from planning questions
- validation plan and user validation are concrete
- roadmap state is updated only for concrete active plans
