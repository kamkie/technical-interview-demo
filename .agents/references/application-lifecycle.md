# Application Lifecycle Reference For AI Agents

`.agents/references/application-lifecycle.md` owns the application development lifecycle phase model, activity vocabulary, loop vocabulary, cross-cutting triggers, and repository owner-guide mapping for AI agents.
Lifecycle guidance belongs in this file rather than in separate lifecycle spec or phase-activity companion files.

Use this file when:

- lifecycle phase, activity, loop, gate, trigger, or owner-guide mapping vocabulary changes
- adding activity tags to domain guides such as `.agents/references/planning.md`, `.agents/references/execution.md`, `.agents/references/testing.md`, or `.agents/references/reviews.md`
- deciding where a recurring activity belongs in the lifecycle
- diagnosing wrong-activity-at-wrong-time failures during a task
- proposing new owner guides for currently uncovered phases

Do not use this file as a substitute for the focused owner guides.
Activity names here are shared vocabulary; binding rules live in the guide that owns the work.

## Model Layers

The lifecycle has three layers:

1. **Phases**: coarse stages a piece of work passes through.
2. **Phase Activities**: focused mental modes used inside each phase.
3. **Loops**: iteration patterns that connect phases and phase activities.

A repository lifecycle is coherent when each phase has an identifiable owner artifact, each activity has an owner artifact or owner role, and each loop has a defined entry, exit, and cadence.

## Definitions

- **Phase**: a coarse lifecycle stage with explicit entry and exit criteria. A piece of work is in exactly one phase at a time.
- **Activity**: a focused activity an agent or human performs inside a phase. An activity has a question it answers, an owner artifact or guide, and an exit condition.
- **Switch**: an explicit transition between two activities or phases. Every switch requires dropping the prior working set before loading the next one.
- **Trigger**: a signal that mandates a switch. Triggers are planned, as the next step in a loop, or conditional, such as a security-relevant change, discovered plan gap, or failed validation.
- **Loop**: a sequence of activities or phases that iterates until a defined exit condition is met. Loops may nest.
- **Artifact**: a durable file or record that is the source of truth for behavior, decisions, or rules. Artifacts are categorized as specs, contracts, plans, logs, or guides.
- **Owner**: the artifact or role that is authoritative for an activity, phase, or rule. Each rule has exactly one owner.
- **Gate**: a checkpoint that must pass before a phase transition is allowed. Gates are mechanical where possible and human-judged otherwise.

## Phases

The lifecycle has twelve phases.
Phases are ordered by typical progression, but conditional paths such as `Replan`, `Hotfix`, and `Rollback` can fire from later work and return to an earlier phase.

| # | Phase | Entry | Exit / Gate |
| --- | --- | --- | --- |
| 1 | Conceptualization | a request, idea, TODO, maintenance signal, or rough link arrives | the signal is captured, rejected, or routed to Analysis or Triage |
| 2 | Analysis | rough work needs structured requirements, product intent, behavior definition, or a durable decision | requirements, non-goals, acceptance criteria, constraints, and open questions are resolved enough for Triage or Planning |
| 3 | Triage | candidate work or analyzed requirements are ready for active-work decisioning | work is accepted, deferred, rejected, prioritized, sequenced, or routed to an ADR, PRD, spec, or plan |
| 4 | Planning | selected work is ready for execution planning | a decision-complete executable plan exists and is approved |
| 5 | Implementation | an approved plan exists | the smallest spec-driven change exists locally and self-validates |
| 6 | Verification | implementation is locally complete | required automated, contract, benchmark, manual, or documentation validation passes |
| 7 | Review | validated change exists | reviewer approves, or change returns to Implementation or Verification |
| 8 | Integration | review is approved | change lands on the integration branch and post-merge checks pass |
| 9 | Release | integrated change is release-ready | versioned artifact is published and release notes exist |
| 10 | Deployment | released artifact exists | artifact runs in the target environment and verifies green |
| 11 | Operations | artifact is live | runtime or post-release signals are observed, triaged, remediated, or scheduled |
| 12 | Maintenance | release closed, recurring signal observed, or learning identified | learnings and maintenance signals are captured and routed into the next cycle |

`Closed` is a terminal status for archived or intentionally retired plans.
It is not a phase.

### Migration Map

Use this map only when updating live guidance or interpreting active work that still uses previous labels.

| Previous label | Current phase |
| --- | --- |
| Discovery | Conceptualization |
| Roadmap Intake | Triage |
| Testing | Verification |
| Continuous Improvement | Maintenance |

### Phase Rules

- A phase transition is explicit: the work item's status changes and any owning artifact is updated in the same change.
- A phase cannot be skipped unless the change class explicitly allows it, such as a documentation-only change that skips Deployment.
- A phase can be re-entered when a downstream phase fails its gate.
- A phase exit gate is either an executable check or a named approval.
- `Planning` is reserved for executable implementation planning; use Conceptualization for low-friction capture, Analysis for structured requirements or decision work, and Triage for active-work selection and sequencing.

## Activity Catalogue

Each activity lists the question it answers and its primary owner in this repository.
Activities marked with `?` are conditional.

### Conceptualization

- `Scan`: what artifacts and code already define this area? Owner: `AGENTS.md` loading map, `.agents/references/architecture.md`. Exit: relevant artifacts identified.
- `Frame`: what is the actual signal or change candidate? Owner: `AGENTS.md`, `.agents/references/planning.md`. Exit: scope and ambiguity surfaced.
- `Capture-Idea?`: should a rough idea, TODO, maintenance signal, or link be retained without planning yet? Owner: `ROADMAP.md` or `.agents/references/LEARNINGS.md` depending on whether it is active work or a durable lesson. Exit: signal recorded or declined.
- `Clarify?`: does the user need to resolve a material ambiguity before the work can leave rough capture? Owner: `.agents/references/planning.md`. Exit: ambiguity resolved or recorded as an accepted fallback.

### Analysis

- `Elicit`: what goals, users, requirements, constraints, risks, or acceptance criteria must be surfaced? Owner: `docs/requirements/*.md`, `docs/specs/*.md`, `docs/DESIGN.md`, or the active plan. Exit: missing inputs are named.
- `Analyze`: which inputs affect scope, compatibility, contract behavior, validation, or rollout? Owner: the relevant PRD, spec, ADR, or plan. Exit: material decisions and non-goals are separated from preferences.
- `Define-Requirements`: what product requirements, behavior rules, acceptance criteria, or non-goals should become durable? Owner: `docs/requirements/*.md`, `docs/specs/*.md`, executable specs, or published contract docs. Exit: requirements are written in the owning artifact or explicitly skipped.
- `Validate-Requirements`: are the requirements specific, testable, and aligned with existing contract truth? Owner: `.agents/references/planning.md`, `.agents/references/documentation.md`, and the governing artifact. Exit: ready for Triage or Planning, or open questions remain.
- `Decide-Artifact?`: does the work need an ADR, PRD, standalone spec, or only a plan? Owner: `.agents/references/documentation.md`. Exit: artifact route is chosen and linked.

### Triage

- `Intake`: should this candidate enter active-work tracking? Owner: `ROADMAP.md`. Exit: roadmap entry exists or the request is rejected as not active work.
- `Classify`: is the work a rough idea, product requirement, behavior spec, decision record, execution plan, release task, or maintenance signal? Owner: `ROADMAP.md`, `.agents/references/documentation.md`, `.agents/references/planning.md`. Exit: next artifact or phase is clear.
- `Prioritize`: does this work belong in the current cycle? Owner: `ROADMAP.md` `## Current Project State`. Exit: priority is recorded or deferred.
- `Sequence`: what active work does this depend on or block? Owner: `ROADMAP.md`. Exit: ordering is clear enough for planning or deferral.
- `Sync`: is active-work tracking aligned with reality? Owner: `ROADMAP.md`. Cross-cutting.

### Planning

- `Design`: decide product, contract, workflow, or technical behavior that must be settled before execution. Owner: `docs/DESIGN.md`, ADRs, PRDs, specs, executable specs, and published contract docs.
- `Spec`: record decided behavior in the governing spec artifact. Owner: `.agents/references/documentation.md` for routing and the individual spec file for content.
- `Decompose`: split planned work into commit-sized plan tasks and pick the workflow shape. Owner: `.agents/references/planning.md`, `.agents/references/workflow.md`.
- `Validate-Plan`: run the plan readiness checklist before approval. Owner: `.agents/references/planning.md`.
- `Replan?`: revise an approved plan when execution reality disagrees with it. Owner: `.agents/references/planning.md`. Cross-cutting.

### Implementation

- `Code`: write the smallest implementation that satisfies the spec. Owner: `.agents/references/code-style.md` and source files.
- `Docs`: route documentation and contract updates per change class. Owner: `.agents/references/documentation.md`.
- `Commit`: produce a plan-task checkpoint with required tracking-artifact updates. Owner: `.agents/references/execution.md`, `.agents/references/workflow.md`, and `.gitmessage`.
- `Handoff`: report status, blockers, and any required push or pull request. Owner: `.agents/references/workflow.md`, `.agents/references/execution.md`.

### Verification

- `Plan-Checks`: choose the smallest sufficient validation for this change class. Owner: `.agents/references/testing.md`.
- `Author-Specs?`: write or update the executable spec, documentation check, benchmark, or reproduction when the change needs one. Owner: existing test packages or contract docs.
- `Run`: execute the chosen validation. Owner: `.agents/references/testing.md`, `.agents/references/command-wrapper.md`.
- `Diagnose?`: interpret a failure before changing anything. Owner: `.agents/references/troubleshooting.md`.
- `Fix?`: apply the smallest correction to implementation, test, spec, or documentation. Owner: `.agents/references/execution.md`.
- `Re-run`: confirm the previously failing validation now passes. Owner: `.agents/references/testing.md`.
- `Record`: write the actual outcome into `Validation Results`, roadmap, workflow state, or final response as appropriate. Owner: `.agents/references/testing.md`, `.agents/references/workflow.md`.

### Review

- `Self-Review`: first pass against `.agents/references/reviews.md` review priorities. Owner: `.agents/references/reviews.md`.
- `Code Review`: peer-style review of the validated diff. Owner: `.agents/references/reviews.md`.
- `Security Review?`: apply when `.agents/references/reviews.md` security triggers fire. Owner: `.agents/references/reviews.md`.
- `Docs Review?`: apply when the change is documentation-heavy. Owner: `.agents/references/reviews.md`, `.agents/references/documentation.md`.
- `Decide`: approve or request changes; if changes are requested, loop back. Owner: `.agents/references/reviews.md`.

### Integration

- `Re-validate`: confirm the merge target still passes after rebase or conflict resolution. Owner: `.agents/references/testing.md`.
- `Resolve-Conflicts?`: handle merge conflicts. Owner: `.agents/references/workflow.md`.
- `Merge`: land the work on `main`. Owner: `AGENTS.md`, `.agents/references/workflow.md`.
- `Post-Merge-Verify`: confirm `main` still builds and matches the intended state. Owner: `.agents/references/execution.md`.

### Release

- `Gate`: confirm release preconditions. Owner: `.agents/references/releases.md`.
- `Tag`: produce the release artifact. Owner: `.agents/references/releases.md`.
- `Notes`: generate or update release notes. Owner: `.agents/references/releases.md`, `CHANGELOG.md`.
- `Publish`: execute the release. Owner: `.agents/references/releases.md`.
- `Post-Release-Cleanup`: archive plans and refresh active-work tracking. Owner: `.agents/references/releases.md`, `.agents/references/planning.md`, `ROADMAP.md`.

### Deployment And Operations

Use `.agents/references/operations.md` for binding rules when work enters Deployment or Operations activities.
This file owns the vocabulary; `operations.md` owns the operational routing.

- `Stage`: promote the artifact into a non-production environment. Owner: `.agents/references/operations.md`.
- `Smoke`: run smoke or external-verification checks. Owner: `.agents/references/operations.md`.
- `Promote`: promote to the next environment. Owner: `.agents/references/operations.md`.
- `Verify`: confirm deployed behavior in the target environment. Owner: `.agents/references/operations.md`.
- `Rollback?`: back out a failed deployment. Owner: `.agents/references/operations.md`.
- `Observe`: read production signals. Owner: `.agents/references/operations.md`.
- `Operational-Triage`: classify an incident or defect. Owner: `.agents/references/operations.md`.
- `Hotfix?`: produce a minimal fix outside the normal plan flow. Owner: `.agents/references/operations.md`.
- `Patch?`: produce a normal-flow corrective change. Owner: `.agents/references/operations.md`.
- `Backport?`: apply a fix to an older supported line. Owner: `.agents/references/operations.md`.
- `Deprecate?`: schedule removal of a behavior. Owner: `.agents/references/operations.md`.

### Maintenance

- `Retrospect`: review what worked and what did not after a release or major plan.
- `Capture-Learning`: record a durable repo-wide lesson. Owner: `.agents/references/LEARNINGS.md`.
- `Refactor?`: schedule structural improvement work as its own plan.
- `Tech-Debt-Plan?`: convert recurring pain into a planned roadmap item.
- `Sync`: feed outcomes back into `ROADMAP.md`.

## Phase Activity Sequence

Each phase has an in-order primary activity sequence.
This table is descriptive; owner guides still own their specific rules.

| Phase | In-order phase activities | Primary owner guides |
| --- | --- | --- |
| Conceptualization | `Scan` -> `Frame` -> `Capture-Idea?` -> `Clarify?` | `AGENTS.md`, `.agents/references/architecture.md`, `.agents/references/LEARNINGS.md` |
| Analysis | `Elicit` -> `Analyze` -> `Define-Requirements` -> `Validate-Requirements` -> `Decide-Artifact?` | `.agents/references/documentation.md`, `.agents/references/planning.md`, `docs/requirements/*.md`, `docs/specs/*.md`, `docs/DESIGN.md` |
| Triage | `Intake` -> `Classify` -> `Prioritize` -> `Sequence` -> `Sync` | `ROADMAP.md`, `.agents/references/documentation.md`, `.agents/references/planning.md` |
| Planning | `Design` -> `Spec` -> `Decompose` -> `Validate-Plan` -> `Sync` -> `Replan?` | `.agents/references/planning.md`, `docs/DESIGN.md`, `.agents/references/plan-template.md` |
| Implementation | `Spec` -> `Code` -> `Docs` -> `Run` -> `Replan?` -> `Self-Review` -> `Code Review` -> `Security Review?` -> `Commit` -> `Handoff` | `.agents/references/execution.md`, `.agents/references/documentation.md`, `.agents/references/code-style.md`, `.agents/references/workflow.md` |
| Verification | `Plan-Checks` -> `Author-Specs?` -> `Run` -> `Diagnose?` -> `Fix?` -> `Re-run` -> `Record` | `.agents/references/testing.md`, `.agents/references/troubleshooting.md` |
| Review | `Self-Review` -> `Code Review` -> `Security Review?` -> `Docs Review?` -> `Decide` | `.agents/references/reviews.md` |
| Integration | `Re-validate` -> `Resolve-Conflicts?` -> `Merge` -> `Post-Merge-Verify` | `.agents/references/workflow.md`, `AGENTS.md` |
| Release | `Gate` -> `Tag` -> `Notes` -> `Publish` -> `Post-Release-Cleanup` | `.agents/references/releases.md`, `CHANGELOG.md` |
| Deployment | `Stage` -> `Smoke` -> `Promote` -> `Verify` -> `Rollback?` | `.agents/references/operations.md` |
| Operations | `Observe` -> `Operational-Triage` -> `Hotfix?` -> `Patch?` -> `Backport?` -> `Deprecate?` | `.agents/references/operations.md` |
| Maintenance | `Retrospect` -> `Capture-Learning` -> `Refactor?` -> `Tech-Debt-Plan?` -> `Sync` | `.agents/references/LEARNINGS.md`, `ROADMAP.md` |

The `Implementation` row deliberately interleaves review and validation activities because `.agents/references/execution.md` runs them in a tight plan-task loop.
This is not a license to skip dedicated Verification or Review gates for the overall change.

## Loops

Lifecycle work happens in six nested or parallel loops:

```text
Outer Product Loop                              [per release]
  |-- Operate-and-Maintain Loop                 [continuous, post-release]
  `-- Plan Loop                                 [per plan]
        `-- Plan Task Execution Loop            [per plan task]
              |-- Red-Green Loop                [per failing validation]
              `-- Review Loop                   [per diff before merge]
```

### Outer Product Loop

- Activities: `Sync` -> Conceptualization -> Analysis -> Triage -> Planning -> Implementation -> Verification -> Review -> Integration -> Release -> Deployment -> Operations -> Maintenance -> `Sync`.
- Cadence: per release.
- Owner: `ROADMAP.md`, `.agents/references/releases.md`, `.agents/references/operations.md`, `.agents/references/LEARNINGS.md`.
- Exit: a release ships, deployment and operations signals are handled, and outcomes feed the next cycle.

### Plan Loop

- Activities: `Frame` -> `Elicit` -> `Analyze` -> `Define-Requirements` -> `Spec` -> `Decompose` -> `Validate-Plan` -> `Replan?` -> `Validate-Plan`.
- Cadence: per plan, until decision-complete.
- Owner: `.agents/references/planning.md`.
- Exit: plan reaches `Status | Ready`.

### Plan Task Execution Loop

- Activities: `Spec` -> `Code` -> `Docs` -> `Run` -> `Replan?` -> `Self-Review` -> `Code Review` -> `Security Review?` -> `Commit` -> `Handoff`.
- Cadence: per plan task within an approved plan.
- Owner: `.agents/references/execution.md`.
- Exit: plan-task commit lands and tracking artifacts are updated.

### Red-Green Loop

- Activities: `Run` -> `Diagnose` -> `Fix` -> `Re-run`.
- Cadence: per failing validation, inside a plan task.
- Owner: `.agents/references/testing.md`; load `.agents/references/troubleshooting.md` on demand.
- Exit: the previously failing validation passes, or the work exits through `Replan?`.

### Review Loop

- Activities: `Self-Review` -> `Code Review` -> `Security Review?` -> `Docs Review?` -> `Decide`, then loop back to `Code` or `Run` when changes are requested.
- Cadence: per diff before merge.
- Owner: `.agents/references/reviews.md`.
- Exit: an `Approve` decision.

### Operate-And-Maintain Loop

- Activities: `Observe` -> `Operational-Triage` -> (`Hotfix?` or `Patch?`) -> `Capture-Learning` -> `Sync`.
- Cadence: continuous, post-release.
- Owner: `.agents/references/operations.md`, `.agents/references/LEARNINGS.md`, and `ROADMAP.md`.
- Exit: the signal is resolved or scheduled as planned work.

## Cross-Cutting Triggers

These triggers can fire from any phase and force a switch:

- `Replan`: execution-time gap, contradicted locked decision, scope drift, or design decision discovered mid-execution. Owner: `.agents/references/planning.md`.
- `Requirements-Analysis`: rough or ambiguous work needs structured product, behavior, or acceptance criteria before planning. Owner: `.agents/references/planning.md`, `.agents/references/documentation.md`, PRDs, and specs.
- `Security Review`: authentication, authorization, secrets, sensitive data, deployment-facing config, CI permissions, release paths, or publish paths change. Owner: `.agents/references/reviews.md`.
- `Sync`: active-work tracking or contract artifacts change. Owner: `ROADMAP.md`, `.agents/references/documentation.md`.
- `Capture-Learning`: recurring repo-wide lesson surfaces. Owner: `.agents/references/LEARNINGS.md`.
- `Docs-Routing`: a contract or maintainer-facing document changes. Owner: `.agents/references/documentation.md`.
- `Context-Hygiene`: any switch between activities. Owner: `AGENTS.md`.
- `Rollback`: deployed behavior fails verification. Owner: `.agents/references/operations.md`.
- `Hotfix`: production incident requires a fix outside normal plan flow. Owner: `.agents/references/operations.md`.
- `Patch`: post-release issue should follow normal planned correction. Owner: `.agents/references/operations.md`.
- `Backport`: a fix may need to apply to an older supported line. Owner: `.agents/references/operations.md`.
- `Deprecate`: behavior may need scheduled removal or replacement. Owner: `.agents/references/operations.md`.

## Required Artifact Roles

This table maps lifecycle roles to the current repository artifacts.
When a role has no complete owner, record the gap in active-work tracking before relying on it.

| Role | Owns | Current artifact |
| --- | --- | --- |
| Project Charter | mission, supported scope, public contract summary | `README.md`, `docs/DESIGN.md` |
| Setup Guide | dev-container or local-shell environment setup | `SETUP.md` |
| Local Development Guide | local commands, validation loops, CI reproduction, and local troubleshooting | `docs/LOCAL_DEVELOPMENT.md` |
| Human Operations Runbook | deployment, runtime, smoke, rollback, Kubernetes, Helm, monitoring, OAuth runtime setup, and operations troubleshooting | `docs/OPERATIONS.md` |
| Decision Record | durable architecture, workflow, contract-policy, security, documentation-ownership, or repository-process decision rationale | `docs/decisions/*.md` |
| Product Requirements | product intent, users, goals, non-goals, requirements, acceptance criteria, and product-scope questions | `docs/requirements/*.md` |
| Standalone Spec | exact behavior, contract impact, acceptance criteria, and validation mapping when existing executable or published specs are insufficient | `docs/specs/*.md` |
| Roadmap | active-work tracking, prioritization, sequencing, and current-cycle state | `ROADMAP.md` |
| Release History | shipped changes per version | `CHANGELOG.md` |
| Operations Guide | deployment, post-release verification, rollback, incident response, hotfix, patch, backport, and deprecation routing | `.agents/references/operations.md` |
| Plan | per-task decision-complete execution handoff document | `.agents/plans/PLAN_*.md` |
| Executable Spec | behavior verified by automation | tests, contract checks, benchmarks |
| Published Contract | human-facing API and contract docs | `README.md`, `src/docs/asciidoc/`, `src/test/resources/openapi/approved-openapi.json` |
| Engineering Rules | spec-driven rules, lifecycle loading, definition of done | `AGENTS.md` plus this file |
| Phase Owner Guides | per-phase or activity-group guidance | `.agents/references/*.md` |
| Learnings | durable engineering lessons | `.agents/references/LEARNINGS.md` |
| Architecture Snapshot | current codebase map and structural guidance | `.agents/references/architecture.md` |

## Spec-Driven Rule

Lifecycle work in this repository follows the spec-driven rule in `AGENTS.md`:

1. Identify the behavior being changed.
2. Identify the spec artifact that defines that behavior.
3. Update or add the spec first.
4. Implement the smallest code change that satisfies the updated spec.
5. Verify executable and published specs remain aligned.

When sources disagree, use the truth priority in `AGENTS.md`.
This file defines lifecycle vocabulary; it does not outrank executable specs, published contract docs, active roadmap state, or explicit user requests.

## Owner-Guide Adoption

`.agents/references/operations.md` is the adopted owner guide for the Deployment and Operations activities listed in this file.

When an owner guide adopts activity names from this file:

- list only the activities that fire in the loop or section the guide actually owns
- tag existing prose with the activity name in square brackets, such as `[Code]`, `[Run]`, or `[Replan?]`
- do not copy activity definitions from this file into the owner guide
- do not introduce a new activity name without adding it here first
- keep `Context-Hygiene` implicit in prose instead of annotating every sentence

When this file changes:

- update dependent owner guides only for activity names they reference that are renamed or removed
- routine vocabulary additions here do not force changes elsewhere
- add a `CHANGELOG.md` entry only for activity-name renames that affect released owner-guide vocabulary

## Current Gaps

- no dedicated Analysis owner guide beyond `.agents/references/planning.md`, `.agents/references/documentation.md`, and the ADR, PRD, and spec templates
- the `Red-Green Loop` is not named directly in `.agents/references/testing.md`, even though validation work commonly follows it
- the `Outer Product Loop` is composed from `ROADMAP.md`, `.agents/references/releases.md`, `.agents/references/operations.md`, and `.agents/references/LEARNINGS.md` rather than owned by a single guide
- the `Review Loop` exit back into plan-task execution is implicit in `.agents/references/reviews.md` and `.agents/references/execution.md`

## Non-Goals

- this file does not mandate a branch model, issue tracker, CI system, hosting platform, sprint length, or release cadence
- this file does not make ADRs, PRDs, or standalone specs mandatory for routine maintenance when existing specs, docs, or plans are enough
- this file does not replace product, security, compliance, setup, validation, release, workflow, or review policy
- this file does not define terminal plan statuses beyond identifying `Closed` as not-a-phase; `.agents/references/planning.md` owns status values
- this file does not replace `.agents/references/workflow.md` for branch, worktree, delegation, integration, or remote-handoff mechanics
- this file is not a task plan and must not become task-specific progress history

## Maintenance

Update this file when lifecycle vocabulary, owner-guide mapping, phase rules, activity names, loop names, cross-cutting triggers, or required artifact roles change.
Do not add companion lifecycle files that only restate this reference.
