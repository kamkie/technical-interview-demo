# 0001 Adopt Pre-Planning Artifacts

## Status

Proposed

## Date

2026-05-10

## Context

The repository already uses spec-driven development, roadmap intake, and executable plans under `.agents/plans/`.
That workflow is effective once a change is clear enough to express as behavior, affected artifacts, validation, and plan tasks.

Some candidate work arrives earlier than that.
Examples include architecture choices, repository workflow conventions, product-scope questions, and behavior definitions that affect more than one future plan.
Putting those decisions directly into a plan can make the plan carry too much rationale and can force implementation planning before the decision is ready.

The current roadmap intake includes:

- introducing Architecture Decision Records (ADRs) into the pre-planning workflow
- formalizing ADR and spec formats

The repository also needs to avoid process sprawl.
This is a compact technical interview demo, so new documentation artifacts should clarify decisions without becoming mandatory ceremony for routine maintenance.

## Decision

Adopt a small set of optional pre-planning artifacts:

- **ADR**: records a durable architecture, workflow, contract-policy, security, documentation-ownership, or repository-process decision before or during planning.
- **PRD**: records product intent only when user-facing scope is broad or unclear enough that goals, users, requirements, and acceptance criteria need their own artifact.
- **Spec**: records exact behavior, contract, acceptance criteria, and validation mapping before implementation planning when executable or published specs do not already cover the behavior.
- **Plan**: remains the execution artifact under `.agents/plans/`, decomposing accepted decisions and specs into concrete plan tasks, validation, and tracking.

Use ADRs as the first artifact for process and architecture decisions.
Use PRDs only for product-intent gaps.
Use specs for behavior truth.
Use plans for implementation.

Proposed artifact locations:

- ADRs: `docs/decisions/NNNN-<kebab-title>.md`
- PRDs: `docs/requirements/PRD_<topic>.md`
- specs: `docs/specs/SPEC_<topic>.md`

This ADR does not by itself make ADRs, PRDs, or standalone specs mandatory.
If accepted, the standing workflow guides should be updated to define when each artifact is required, optional, or skipped.

## Consequences

Benefits:

- plans can reference durable rationale instead of copying decision history
- broad product questions can be resolved before implementation planning
- behavior specs can become clearer handoff artifacts for tests, docs, and implementation
- workflow decisions become reviewable and supersedable instead of living only in roadmap prose

Costs:

- new artifact types need routing rules and templates
- agents and humans need clear skip rules so small changes do not require extra documents
- accepted ADRs must stay aligned with `.agents/references/planning.md`, `.agents/references/documentation.md`, `WORKING_WITH_AI.md`, and any future templates

## Alternatives Considered

### Keep Decisions Only In Plans

Plans already have `Decision Log And Assumptions`, so they can capture implementation-relevant decisions.
This is sufficient for small or local work.
It is weaker for decisions that apply to multiple future plans or decide repository workflow before a plan exists.

### Use Only ADRs

ADRs can record the decision to require product or behavior artifacts, but they are not a good substitute for product requirements or detailed behavior specifications.
Using ADRs for every product requirement would blur decision rationale with user-facing acceptance criteria.

### Use PRDs For Everything Before Planning

PRDs are useful for product intent, but this repository also needs non-product decisions such as architecture, security posture, documentation ownership, and AI workflow policy.
For those, ADRs are a better fit.

## Confirmation

This ADR should be considered accepted only after the repository updates the relevant standing guidance and templates.
The acceptance implementation should:

1. Add artifact ownership and routing rules for ADRs, PRDs, and standalone specs.
2. Add minimal templates or skeletons for each artifact type.
3. Update planning guidance so pre-planning artifacts are used only when they answer a real ambiguity.
4. Update human-facing AI workflow guidance if the developer workflow changes.
5. Remove or revise the matching roadmap intake items once the workflow is implemented.

## Links

- ADR overview: https://adr.github.io/
- MADR template family: https://adr.github.io/madr/
- Architecture Decision Record repository: https://github.com/architecture-decision-record/architecture-decision-record
