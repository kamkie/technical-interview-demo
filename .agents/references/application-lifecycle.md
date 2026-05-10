# Application Lifecycle Reference For AI Agents

`.agents/references/application-lifecycle.md` owns the application development lifecycle phase model, activity vocabulary, loop vocabulary, cross-cutting triggers, and repository owner-guide mapping for AI agents.
Lifecycle guidance belongs in this file rather than in separate lifecycle spec or phase-activity companion files.

Use this file when:

- lifecycle phase, activity, loop, gate, trigger, or owner-guide mapping vocabulary changes
- adding activity tags to owner guides such as `.agents/references/planning.md`, `.agents/references/execution.md`, `.agents/references/testing.md`, or `.agents/references/reviews.md`
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

The lifecycle has eleven phases. Phases are ordered by typical progression, but conditional paths such as `Replan`, `Hotfix`, and `Rollback` can fire from later work and return to an earlier phase.

| # | Phase | Entry | Exit / Gate |
| --- | --- | --- | --- |
| 1 | Discovery | a request, idea, or signal arrives | scope and ambiguity surfaced; either rejected or promoted to Roadmap Intake |
| 2 | Roadmap Intake | promoted Discovery item | item is sequenced and prioritized in active-work tracking |
| 3 | Planning | a roadmap item is picked up | a decision-complete plan exists and is approved |
| 4 | Implementation | an approved plan exists | smallest spec-driven change exists locally and self-validates |
| 5 | Testing | implementation is locally complete | required validation passes |
| 6 | Review | validated change exists | reviewer approves, or change returns to Implementation or Testing |
| 7 | Integration | review is approved | change lands on the integration branch and post-merge checks pass |
| 8 | Release | integrated change is release-ready | versioned artifact is published and release notes exist |
| 9 | Deployment | released artifact exists | artifact runs in the target environment and verifies green |
| 10 | Operations | artifact is live | signals are observed; incidents are triaged or scheduled |
| 11 | Continuous Improvement | release closed or recurring signal observed | learnings captured; next-cycle roadmap updated |

`Closed` is a terminal status for archived or intentionally retired plans.
It is not a phase.

### Phase Rules

- A phase transition is explicit: the work item's status changes and any owning artifact is updated in the same change.
- A phase cannot be skipped unless the change class explicitly allows it, such as a documentation-only change that skips Deployment.
- A phase can be re-entered when a downstream phase fails its gate.
- A phase exit gate is either an executable check or a named approval.

## Activity Catalogue

Each activity lists the question it answers and its primary owner in this repository.
Activities marked with `?` are conditional.

### Discovery And Framing

- `Scan`: what artifacts and code already define this area? Owner: `AGENTS.md` loading map, `.agents/references/architecture.md`. Exit: relevant artifacts identified.
- `Frame`: what is the actual change being requested? Owner: `AGENTS.md`, `.agents/references/planning.md`. Exit: scope and ambiguity surfaced.
- `Clarify?`: does the user need to resolve a material ambiguity? Owner: `.agents/references/planning.md`. Exit: ambiguity resolved or recorded as an accepted fallback.
- `Capture?`: does this surface a durable repo lesson? Owner: `.agents/references/LEARNINGS.md`. Exit: learning recorded or declined.

### Requirements And Roadmap

- `Intake`: capture the requested work. Owner: `ROADMAP.md`. Exit: roadmap entry exists or the request is rejected as not ready.
- `Refine`: is the request specific enough to plan? Owner: `ROADMAP.md`, `docs/DESIGN.md`. Exit: actionable description exists.
- `Prioritize`: does this work belong in the current phase? Owner: `ROADMAP.md` `## Current Project State`.
- `Sequence`: what active work does this depend on or block? Owner: `ROADMAP.md`.
- `Sync`: is active-work tracking aligned with reality? Owner: `ROADMAP.md`. Cross-cutting.

### Design, Spec, And Planning

- `Design`: decide product or contract behavior. Owner: `docs/DESIGN.md` and governing specs.
- `Spec`: record the decided behavior in the governing spec artifact. Owner: `.agents/references/documentation.md` for routing and the individual spec file for content.
- `Decompose`: split planned work into commit-sized milestones and pick the workflow shape. Owner: `.agents/references/planning.md`, `.agents/references/workflow.md`.
- `Validate-Plan`: run the plan readiness checklist before approval. Owner: `.agents/references/planning.md`.
- `Replan?`: revise an approved plan when execution reality disagrees with it. Owner: `.agents/references/planning.md`. Cross-cutting.

### Implementation

- `Code`: write the smallest implementation that satisfies the spec. Owner: `.agents/references/code-style.md` and source files.
- `Docs`: route documentation and contract updates per change class. Owner: `.agents/references/documentation.md`.
- `Commit`: produce a milestone-shaped checkpoint with required tracking-artifact updates. Owner: `.agents/references/execution.md`, `.agents/references/workflow.md`, and `.gitmessage`.
- `Handoff`: report status, blockers, and any required push or pull request. Owner: `.agents/references/workflow.md`, `.agents/references/execution.md`.

### Testing And Verification

- `Plan-Tests`: choose the smallest sufficient validation for this change class. Owner: `.agents/references/testing.md`.
- `Author-Tests`: write or update the executable spec or reproduction. Owner: existing test packages.
- `Run`: execute the chosen validation. Owner: `.agents/references/testing.md`, `.agents/references/command-wrapper.md`.
- `Diagnose?`: interpret a failure before changing anything. Owner: `.agents/references/troubleshooting.md`.
- `Fix?`: apply the smallest correction to implementation, test, or spec. Owner: `.agents/references/execution.md`.
- `Re-run`: confirm the previously failing validation now passes. Owner: `.agents/references/testing.md`.
- `Record`: write the actual outcome into `Validation Results` or workflow state. Owner: `.agents/references/testing.md`, `.agents/references/workflow.md`.

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

These activities are listed for lifecycle completeness.
They currently have no dedicated AI owner guide in this repository, so treat them as gaps until a guide owns them.

- `Stage`: promote the artifact into a non-production environment.
- `Smoke`: run smoke or external-verification checks.
- `Promote`: promote to the next environment.
- `Verify`: confirm deployed behavior in the target environment.
- `Rollback?`: back out a failed deployment.
- `Observe`: read production signals.
- `Triage`: classify an incident or defect.
- `Hotfix?`: produce a minimal fix outside the normal plan flow.
- `Patch?`: produce a normal-flow corrective change.
- `Backport?`: apply a fix to an older supported line.
- `Deprecate?`: schedule removal of a behavior.

### Continuous Improvement

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
| Discovery | `Scan` -> `Frame` -> `Clarify?` -> `Capture?` | `AGENTS.md`, `.agents/references/architecture.md`, `.agents/references/LEARNINGS.md` |
| Roadmap Intake | `Intake` -> `Refine` -> `Prioritize` -> `Sequence` -> `Sync` | `ROADMAP.md`, `docs/DESIGN.md` |
| Planning | `Frame` -> `Design` -> `Spec` -> `Decompose` -> `Validate-Plan` -> `Sync` -> `Replan?` | `.agents/references/planning.md`, `docs/DESIGN.md`, `.agents/references/plan-template.md` |
| Implementation | `Spec` -> `Code` -> `Docs` -> `Run` -> `Replan?` -> `Self-Review` -> `Code Review` -> `Security Review?` -> `Commit` -> `Handoff` | `.agents/references/execution.md`, `.agents/references/documentation.md`, `.agents/references/code-style.md`, `.agents/references/workflow.md` |
| Testing | `Plan-Tests` -> `Author-Tests` -> `Run` -> `Diagnose?` -> `Fix?` -> `Re-run` -> `Record` | `.agents/references/testing.md`, `.agents/references/troubleshooting.md` |
| Review | `Self-Review` -> `Code Review` -> `Security Review?` -> `Docs Review?` -> `Decide` | `.agents/references/reviews.md` |
| Integration | `Re-validate` -> `Resolve-Conflicts?` -> `Merge` -> `Post-Merge-Verify` | `.agents/references/workflow.md`, `AGENTS.md` |
| Release | `Gate` -> `Tag` -> `Notes` -> `Publish` -> `Post-Release-Cleanup` | `.agents/references/releases.md`, `CHANGELOG.md` |
| Deployment | `Stage` -> `Smoke` -> `Promote` -> `Verify` -> `Rollback?` | none yet |
| Operations | `Observe` -> `Triage` -> `Hotfix?` -> `Patch?` -> `Backport?` -> `Deprecate?` | partial: `CHANGELOG.md`, `ROADMAP.md` |
| Continuous Improvement | `Retrospect` -> `Capture-Learning` -> `Refactor?` -> `Tech-Debt-Plan?` -> `Sync` | `.agents/references/LEARNINGS.md`, `ROADMAP.md` |

The `Implementation` row deliberately interleaves review and validation activities because `.agents/references/execution.md` runs them in a tight milestone loop.
This is not a license to skip dedicated Testing or Review gates for the overall change.

## Loops

Lifecycle work happens in six nested or parallel loops:

```text
Outer Product Loop                              [per release]
  |-- Operate-and-Improve Loop                  [continuous, post-release]
  `-- Plan Loop                                 [per plan]
        `-- Milestone Execution Loop            [per milestone]
              |-- Red-Green Loop                [per failing validation]
              `-- Review Loop                   [per diff before merge]
```

### Outer Product Loop

- Activities: `Sync` -> planning -> implementation -> release -> `Retrospect` -> `Capture-Learning` -> `Sync`.
- Cadence: per release.
- Owner: `ROADMAP.md`, `.agents/references/releases.md`, `.agents/references/LEARNINGS.md`.
- Exit: a release ships and outcomes feed the next intake.

### Plan Loop

- Activities: `Frame` -> `Design` -> `Spec` -> `Decompose` -> `Validate-Plan` -> `Replan?` -> `Validate-Plan`.
- Cadence: per plan, until decision-complete.
- Owner: `.agents/references/planning.md`.
- Exit: plan reaches `Status | Ready`.

### Milestone Execution Loop

- Activities: `Spec` -> `Code` -> `Docs` -> `Run` -> `Replan?` -> `Self-Review` -> `Code Review` -> `Security Review?` -> `Commit` -> `Handoff`.
- Cadence: per milestone within an approved plan.
- Owner: `.agents/references/execution.md`.
- Exit: milestone commit lands and tracking artifacts are updated.

### Red-Green Loop

- Activities: `Run` -> `Diagnose` -> `Fix` -> `Re-run`.
- Cadence: per failing validation, inside a milestone.
- Owner: `.agents/references/testing.md`; load `.agents/references/troubleshooting.md` on demand.
- Exit: the previously failing validation passes, or the work exits through `Replan?`.

### Review Loop

- Activities: `Self-Review` -> `Code Review` -> `Security Review?` -> `Docs Review?` -> `Decide`, then loop back to `Code` or `Run` when changes are requested.
- Cadence: per diff before merge.
- Owner: `.agents/references/reviews.md`.
- Exit: an `Approve` decision.

### Operate-And-Improve Loop

- Activities: `Observe` -> `Triage` -> (`Hotfix?` or `Patch?`) -> `Capture-Learning` -> `Sync`.
- Cadence: continuous, post-release.
- Owner: gap; currently scattered across `.agents/references/LEARNINGS.md`, `ROADMAP.md`, and `CHANGELOG.md`.
- Exit: the signal is resolved or scheduled as planned work.

## Cross-Cutting Triggers

These triggers can fire from any phase and force a switch:

- `Replan`: execution-time gap, contradicted locked decision, scope drift, or design decision discovered mid-execution. Owner: `.agents/references/planning.md`.
- `Security Review`: authentication, authorization, secrets, sensitive data, deployment-facing config, CI permissions, release paths, or publish paths change. Owner: `.agents/references/reviews.md`.
- `Sync`: active-work tracking or contract artifacts change. Owner: `ROADMAP.md`, `.agents/references/documentation.md`.
- `Capture-Learning`: recurring repo-wide lesson surfaces. Owner: `.agents/references/LEARNINGS.md`.
- `Docs-Routing`: a contract or maintainer-facing document changes. Owner: `.agents/references/documentation.md`.
- `Context-Hygiene`: any switch between activities. Owner: `AGENTS.md`.
- `Rollback`: deployed behavior fails verification. Owner: gap until Deployment guidance exists.
- `Hotfix`: production incident requires a fix outside normal plan flow. Owner: gap until Operations guidance exists.

## Required Artifact Roles

This table maps lifecycle roles to the current repository artifacts.
When a role has no complete owner, record the gap in active-work tracking before relying on it.

| Role | Owns | Current artifact |
| --- | --- | --- |
| Project Charter | mission, supported scope, public contract summary | `README.md`, `docs/DESIGN.md` |
| Setup Guide | local environment, tooling, onboarding, troubleshooting | `SETUP.md` |
| Roadmap | active-work tracking, sequencing, current-cycle state | `ROADMAP.md` |
| Release History | shipped changes per version | `CHANGELOG.md` |
| Plan | per-task decision-complete handoff document | `.agents/plans/PLAN_*.md` |
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

- no AI owner guide for Deployment or Operations phase activities
- the `Red-Green Loop` is not named directly in `.agents/references/testing.md`, even though validation work commonly follows it
- the `Outer Product Loop` is composed from `ROADMAP.md`, `.agents/references/releases.md`, and `.agents/references/LEARNINGS.md` rather than owned by a single guide
- the `Review Loop` exit back into milestone execution is implicit in `.agents/references/reviews.md` and `.agents/references/execution.md`

## Non-Goals

- this file does not mandate a branch model, issue tracker, CI system, hosting platform, sprint length, or release cadence
- this file does not replace product, security, compliance, setup, testing, release, workflow, or review policy
- this file does not define terminal plan statuses beyond identifying `Closed` as not-a-phase; `.agents/references/planning.md` owns status values
- this file does not replace `.agents/references/workflow.md` for branch, worktree, delegation, integration, or remote-handoff mechanics
- this file is not a task plan and must not become task-specific progress history

## Maintenance

Update this file when lifecycle vocabulary, owner-guide mapping, phase rules, activity names, loop names, or cross-cutting triggers change.
Do not add companion lifecycle files that only restate this reference.
