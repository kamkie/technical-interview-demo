# Development Lifecycle

This is a human-facing summary derived from [application-lifecycle.md](../.agents/references/application-lifecycle.md), [documentation.md](../.agents/references/documentation.md), [planning.md](../.agents/references/planning.md), and [ROADMAP.md](../ROADMAP.md).
Update those owner artifacts first when lifecycle vocabulary, artifact routing, planning rules, or active-work tracking changes.

## Core Rule

Work in this repository is spec-driven:

1. Identify the behavior or documented responsibility being changed.
2. Identify the artifact that owns that truth.
3. Update the governing spec, contract, plan, ADR, PRD, or guide before or alongside implementation.
4. Make the smallest coherent change.
5. Verify the executable and published artifacts still agree.

## Phases

| Phase | Human Meaning | Primary Artifacts |
| --- | --- | --- |
| Conceptualization | Capture a rough idea, TODO, maintenance signal, or user request. | [ROADMAP.md](../ROADMAP.md), [LEARNINGS.md](../.agents/references/LEARNINGS.md) |
| Analysis | Turn broad or ambiguous work into goals, requirements, constraints, and acceptance criteria. | [docs/requirements/](requirements/), [docs/specs/](specs/), [DESIGN.md](DESIGN.md) |
| Triage | Accept, defer, reject, prioritize, or sequence candidate work. | [ROADMAP.md](../ROADMAP.md) |
| Planning | Create a decision-complete execution plan. | [.agents/plans/](../.agents/plans/), [planning.md](../.agents/references/planning.md) |
| Implementation | Make task-sized spec-driven changes. | Source files, tests, docs, and the active plan |
| Verification | Run the validation that matches the change. | [testing.md](../.agents/references/testing.md), executable specs, docs audit, contract checks |
| Review | Inspect for bugs, spec drift, missing validation, and security risk. | [reviews.md](../.agents/references/reviews.md) |
| Integration | Land reviewed work on `main` and re-check the integration state. | [workflow.md](../.agents/references/workflow.md), [ROADMAP.md](../ROADMAP.md) |
| Release | Publish a versioned artifact after integrated work is ready. | [releases.md](../.agents/references/releases.md), [CHANGELOG.md](../CHANGELOG.md) |
| Deployment | Promote a released artifact into an environment and verify it. | [operations.md](../.agents/references/operations.md) |
| Operations | Observe, triage, remediate, or schedule live signals. | [operations.md](../.agents/references/operations.md), [LEARNINGS.md](../.agents/references/LEARNINGS.md) |
| Maintenance | Feed lessons and follow-up work into the next cycle. | [LEARNINGS.md](../.agents/references/LEARNINGS.md), [ROADMAP.md](../ROADMAP.md) |

Small local documentation fixes or typo-level maintenance do not need every phase.
They still need the right owner artifact, validation, and review for their scope.

## Artifact Routing

Use the artifact that owns the truth you are changing:

| Change Type | Usual Owner |
| --- | --- |
| Runtime behavior or public API contract | Tests, REST Docs, OpenAPI baseline, and [README.md](../README.md) when the supported contract changes |
| Product intent, non-goals, and contract direction | [DESIGN.md](DESIGN.md) |
| Durable architecture, workflow, security, documentation-ownership, or process decision | ADR under [docs/decisions/](decisions/) |
| Broad user-facing requirements | PRD under [docs/requirements/](requirements/) |
| Standalone behavior or acceptance criteria not covered elsewhere | Spec under [docs/specs/](specs/) |
| Active work state | [ROADMAP.md](../ROADMAP.md) and the active plan under [.agents/plans/](../.agents/plans/) |
| Local setup and local troubleshooting | [SETUP.md](../SETUP.md) |
| Contributor workflow | [CONTRIBUTING.md](../CONTRIBUTING.md) |
| Human AI collaboration guidance | [WORKING_WITH_AI.md](WORKING_WITH_AI.md) |
| AI execution, workflow, validation, review, release, and operations rules | [AGENTS.md](../AGENTS.md) and [.agents/references/](../.agents/references/) |
| Released history | [CHANGELOG.md](../CHANGELOG.md) |

When ownership is unclear, decide the owner before editing multiple files.

## When To Create An Artifact

Create an ADR when the decision should outlive one implementation task and affects architecture, workflow, contract policy, security, documentation ownership, or repository process.

Create a PRD when broad or ambiguous user-facing work needs product intent, goals, users, non-goals, requirements, acceptance criteria, and open questions in one place.

Create a standalone spec when behavior or contract truth is not clear enough in executable specs, REST Docs, OpenAPI, README, or an approved plan.

Create an execution plan when selected work needs multiple task-sized checkpoints, tracking, validation evidence, or coordinated ownership.

Skip extra pre-planning artifacts for trivial, non-behavioral, or local-only changes when the owning artifact is already clear.

## Readiness Before Implementation

Before implementation, the work should have:

- clear in-scope and out-of-scope boundaries
- a named governing artifact
- resolved blocking questions or accepted fallbacks
- a validation target
- a plan when the work is too large for one bounded task

If implementation reveals a real requirement gap, revise the plan or owner artifact before coding beyond the approved scope.

## Validation And Review

Validation should match the changed surface.
Documentation-only changes usually need [scripts/docs/audit-docs.ps1](../scripts/docs/audit-docs.ps1) plus the standard wrapper build behavior described in [SETUP.md](../SETUP.md).
Public API changes need the executable specs and published contract artifacts that define the changed behavior.

Review should start with bugs, spec drift, contract drift, missing validation, and security-sensitive impact.
Style-only cleanup is secondary.
