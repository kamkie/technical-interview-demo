# Lifecycle Phase Activities And Loops Specification

This is the on-demand reference that defines the phase activity and loop vocabulary for AI agents working in this repository.
It is a vocabulary specification, not a workflow policy.
Owner guides under `.agents/references/` may adopt these names to make context switches explicit, but they remain the source of truth for their own rules.

Use this file when:

- adding activity tags to an owner guide such as `.agents/references/execution.md`, `.agents/references/planning.md`, `.agents/references/testing.md`, or `.agents/references/reviews.md`
- discussing where a recurring activity belongs in the lifecycle
- diagnosing wrong-activity-at-wrong-time failures during a task
- proposing new owner guides for currently uncovered phases

Do not use this file as a substitute for the owner guides.
Activity names here are descriptive shortcuts; the binding rules live in the owning guide.

## Definitions

- **Activity**: a single mental mode an agent adopts while doing one kind of work. An activity has a question it answers, a primary owner guide, and an exit condition.
- **Switch**: an explicit transition between two phase activities. A switch always implies dropping the previous activity's working set per the `Context Hygiene` rule in `AGENTS.md`.
- **Trigger**: a signal that requires a switch. Triggers can be planned (the next step in a loop) or conditional (e.g. a security-relevant change, a discovered plan gap).
- **Loop**: a sequence of phase activities that iterates until an exit condition is met. Loops can nest.
- **Phase**: a coarse lifecycle stage that contains one or more phase activities, mapped to the `Phase` enum in `.agents/references/planning.md`.

## Activity Catalogue

Each activity lists its question, primary owner guide, and typical exit condition.

### Discovery and Framing

- `Scan` — what artifacts and code already define this area? owner: `AGENTS.md` onboarding map, `docs/ARCHITECTURE.md`. Exit: relevant artifacts identified.
- `Frame` — what is the actual change being requested? owner: `AGENTS.md` Task Interpretation, `.agents/references/planning.md`. Exit: scope and ambiguity surfaced.
- `Clarify?` — does the user need to resolve a material ambiguity? owner: `.agents/references/planning.md` planning rules. Exit: ambiguity resolved or recorded as a fallback assumption.
- `Capture` — does this surface a durable repo lesson? owner: `.agents/references/LEARNINGS.md`. Conditional.

### Requirements And Roadmap

- `Intake` — capture the requested work. owner: `ROADMAP.md`. Exit: roadmap entry exists or the request is rejected as not ready.
- `Refine` — is the request specific enough to plan? owner: `ROADMAP.md`, `docs/DESIGN.md`. Exit: an actionable description.
- `Prioritize` — does this work belong in the current phase? owner: `ROADMAP.md` `## Current Project State`.
- `Sequence` — what active work does this depend on or block? owner: `ROADMAP.md`.
- `Sync` — is the active-work tracking aligned with reality? owner: `ROADMAP.md`. Cross-cutting.

### Design, Spec, And Planning

- `Design` — decide product or contract behavior. owner: `docs/DESIGN.md`, governing specs.
- `Spec` — record the decided behavior in the governing spec artifact. owner: `.agents/references/documentation.md` for routing, individual spec files for content.
- `Decompose` — split into commit-sized milestone checkpoints; pick a workflow shape. owner: `.agents/references/planning.md`, `.agents/references/workflow.md`.
- `Validate-Plan` — run the plan readiness checklist before approval. owner: `.agents/references/planning.md` `Final Check`.
- `Replan?` — revise an approved plan when execution reality disagrees with it. owner: `.agents/references/planning.md`. Cross-cutting.

### Implementation

- `Code` — write the smallest implementation that satisfies the spec. owner: `.agents/references/code-style.md`, source files.
- `Docs` — route artifact updates per the change type. owner: `.agents/references/documentation.md`.
- `Commit` — produce a milestone-shaped checkpoint with the required tracking-artifact updates. owner: `.agents/references/execution.md` Milestone Commit Rules, `.agents/references/workflow.md`.
- `Handoff` — report status, blockers, and any required push or pull request. owner: `.agents/references/workflow.md`, `.agents/references/execution.md` Completion Criteria.

### Testing And Verification

- `Plan-Tests` — choose the smallest sufficient validation for this change type. owner: `.agents/references/testing.md`.
- `Author-Tests` — write or update the executable spec or reproduction. owner: existing test packages.
- `Run` — execute the chosen validation. owner: `.agents/references/testing.md`, `.agents/references/environment-quick-ref.md`.
- `Diagnose?` — interpret a failure before changing anything. owner: `.agents/references/troubleshooting.md`.
- `Fix?` — apply the smallest correction to the implementation, test, or spec. owner: `.agents/references/execution.md`.
- `Re-run` — confirm the previously failing validation now passes. owner: `.agents/references/testing.md`.
- `Record` — write the actual outcome into `Validation Results` or worker log. owner: `.agents/references/testing.md` Recording Validation, `.agents/references/workflow.md`.

### Review

- `Self-Review` — first pass against `.agents/references/reviews.md` Review Priorities. owner: `.agents/references/reviews.md`.
- `Code Review` — peer-style review of the validated diff. owner: `.agents/references/reviews.md`.
- `Security Review?` — apply only when `.agents/references/reviews.md` security triggers fire. owner: `.agents/references/reviews.md`.
- `Docs Review?` — apply when the change is documentation-heavy. owner: `.agents/references/reviews.md`, `.agents/references/documentation.md`.
- `Decide` — approve or request changes; loop back to `Code` or `Validation` if the latter. owner: `.agents/references/reviews.md`.

### Integration

- `Re-validate` — confirm the merge target still passes after rebase or conflict resolution. owner: `.agents/references/testing.md`.
- `Resolve-Conflicts?` — merge conflict handling. owner: `.agents/references/workflow.md`.
- `Merge` — land the work on `main`. owner: `AGENTS.md` Branch And Worktree Expectations.
- `Post-Merge-Verify` — confirm `main` still builds and matches the plan's `Implemented` state. owner: `.agents/references/execution.md` Completion Criteria.

### Release

- `Gate` — confirm release preconditions. owner: `.agents/references/releases.md`.
- `Tag` — produce the release artifact. owner: `.agents/references/releases.md`.
- `Notes` — generate or update `CHANGELOG.md`. owner: `.agents/references/releases.md`.
- `Publish` — execute the release. owner: `.agents/references/releases.md`.
- `Post-Release-Cleanup` — archive plans, refresh `ROADMAP.md` active-work entries, close the loop into Continuous Improvement. owner: `.agents/references/releases.md`, `.agents/references/planning.md`, `ROADMAP.md`.

### Deployment And Operations

These phase activities are listed for completeness; they currently have **no AI owner guide** in this repository. Treat them as aspirational until a guide owns them.

- `Stage` — promote the artifact into a non-production environment.
- `Smoke` — run the smoke or external verification suite.
- `Promote` — promote to the next environment.
- `Verify` — confirm the deployed behavior in the target environment.
- `Rollback?` — back out a failed deployment.
- `Observe` — read production signals.
- `Triage` — classify an incident or defect.
- `Hotfix?` — produce a minimal fix outside the normal plan flow.
- `Patch?` — produce a normal-flow corrective change.
- `Backport?` — apply a fix to an older supported line.
- `Deprecate?` — schedule removal of a behavior.

### Continuous Improvement

- `Retrospect` — review what worked and what did not after a release or major plan.
- `Capture-Learning` — record a durable repo-wide lesson. owner: `.agents/references/LEARNINGS.md`.
- `Refactor?` — schedule structural improvement work as its own plan.
- `Tech-Debt-Plan?` — convert recurring pain into a planned roadmap item.
- `Sync` — feed outcomes back into `ROADMAP.md`.

## Phase Activity Sequence

The repository uses the `Phase` enum from `.agents/references/planning.md`. This table maps each phase to its in-order activity sequence; `?` marks conditional phase activities.

| Phase | In-order phase activities | Primary owner guides |
| --- | --- | --- |
| Discovery | `Scan` → `Frame` → `Clarify?` → `Capture?` | `AGENTS.md`, `docs/ARCHITECTURE.md`, `.agents/references/LEARNINGS.md` |
| Roadmap intake | `Intake` → `Refine` → `Prioritize` → `Sequence` → `Sync` | `ROADMAP.md`, `docs/DESIGN.md` |
| Planning | `Frame` → `Design` → `Spec` → `Decompose` → `Validate-Plan` → `Sync` → `Replan?` | `.agents/references/planning.md`, `docs/DESIGN.md`, `.agents/templates/plan-template.md` |
| Implementation | `Spec` → `Code` → `Docs` → `Run` → `Replan?` → `Self-Review` → `Code Review` → `Security Review?` → `Commit` → `Handoff` | `.agents/references/execution.md`, `.agents/references/documentation.md`, `.agents/references/code-style.md`, `.agents/references/workflow.md` |
| Testing | `Plan-Tests` → `Author-Tests` → `Run` → `Diagnose?` → `Fix?` → `Re-run` → `Record` | `.agents/references/testing.md`, `.agents/references/troubleshooting.md` |
| Review | `Self-Review` → `Code Review` → `Security Review?` → `Docs Review?` → `Decide` | `.agents/references/reviews.md` |
| Integration | `Re-validate` → `Resolve-Conflicts?` → `Merge` → `Post-Merge-Verify` | `.agents/references/workflow.md`, `AGENTS.md` |
| Release | `Gate` → `Tag` → `Notes` → `Publish` → `Post-Release-Cleanup` | `.agents/references/releases.md`, `CHANGELOG.md` |
| Deployment | `Stage` → `Smoke` → `Promote` → `Verify` → `Rollback?` | none yet (gap) |
| Operations | `Observe` → `Triage` → `Hotfix?` → `Patch?` → `Backport?` → `Deprecate?` | partial: `CHANGELOG.md`, `ROADMAP.md` |
| Continuous improvement | `Retrospect` → `Capture-Learning` → `Refactor?` → `Tech-Debt-Plan?` → `Sync` | `.agents/references/LEARNINGS.md`, `ROADMAP.md` |
| Closed | none | `.agents/references/planning.md`, `.agents/archive/` |

The `Implementation` row deliberately includes review and validation phase activities because the existing milestone loop in `.agents/references/execution.md` interleaves them. Do not split that loop apart on the basis of this table; the table is descriptive.

## Loops

Lifecycle work happens in six nested or parallel loops. Each loop has a distinct cadence and exit condition.

```
Outer Product Loop                              [per release]
  ├── Operate-and-Improve Loop                  [continuous, post-release]
  └── Plan Loop                                 [per plan]
        └── Milestone Execution Loop            [per milestone]
              ├── Red-Green Loop                [per failing validation]
              └── Review Loop                   [per diff before merge]
```

### 1. Outer Product Loop

- Phase Activities: `Sync` (roadmap) → planning → implementation → release → `Retrospect` → `Capture-Learning` → `Sync` (roadmap).
- Cadence: per release.
- Owner: `ROADMAP.md` plus `.agents/references/releases.md` and `.agents/references/LEARNINGS.md`.
- Exit: a release ships and its outcomes feed the next intake.

### 2. Plan Loop

- Phase Activities: `Frame` → `Design` → `Spec` → `Decompose` → `Validate-Plan` → `Replan?` → `Validate-Plan`.
- Cadence: per plan, until decision-complete.
- Owner: `.agents/references/planning.md`.
- Exit: plan reaches `Status=Ready`.

### 3. Milestone Execution Loop

- Phase Activities: the existing numbered loop in `.agents/references/execution.md` Common Milestone Loop, expressed as `Spec` → `Code` → `Docs` → `Run` → `Replan?` → `Self-Review` → `Code Review` → `Security Review?` → `Commit` → `Handoff`.
- Cadence: per milestone within an approved plan.
- Owner: `.agents/references/execution.md`.
- Exit: milestone commit lands and tracking artifacts are updated.

### 4. Red-Green Loop

- Phase Activities: `Run` → `Diagnose` → `Fix` → `Re-run`.
- Cadence: per failing validation, inside a milestone.
- Owner: `.agents/references/testing.md`, with `.agents/references/troubleshooting.md` loaded on demand.
- Exit: previously failing validation passes; if it cannot pass, exit through `Replan?`.

### 5. Review Loop

- Phase Activities: `Self-Review` → `Code Review` → `Security Review?` → `Docs Review?` → `Decide` → loop back to `Code` or `Run` if changes are requested.
- Cadence: per diff before merge.
- Owner: `.agents/references/reviews.md`.
- Exit: an `Approve` decision; otherwise re-enter the milestone loop.

### 6. Operate-and-Improve Loop

- Phase Activities: `Observe` → `Triage` → (`Hotfix?` or `Patch?`) → `Capture-Learning` → `Sync`.
- Cadence: continuous, post-release.
- Owner: gap. Currently scattered across `.agents/references/LEARNINGS.md`, `ROADMAP.md`, and `CHANGELOG.md`.
- Exit: the signal is resolved or scheduled as planned work.

## Cross-Cutting Triggers

These triggers can fire from any phase and force a switch.

- `Replan` — execution-time gap, contradicted locked decision, or scope drift. Owner: `.agents/references/planning.md`.
- `Security Review` — change touches an item in the `.agents/references/reviews.md` security triggers list.
- `Sync` — any change that affects active-work tracking or contract artifacts. Owner: `ROADMAP.md`, `.agents/references/documentation.md`.
- `Capture-Learning` — recurring repo-wide lesson surfaces. Owner: `.agents/references/LEARNINGS.md`.
- `Docs-Routing` — any artifact touch that changes a contract or maintainer-facing document. Owner: `.agents/references/documentation.md`.
- `Context-Hygiene` — fires between any two phase activities; agent must drop the prior activity's working set before loading the next. Owner: `AGENTS.md`.

## Adoption Guidance For Owner Guides

When an owner guide adopts activity names from this spec:

- list only the phase activities that fire in the loop or section that guide actually owns
- tag the existing prose with the activity name in square brackets, for example `[Code]`, `[Validation]`, `[Replan?]`
- do not copy activity definitions from this file; link to this file for the definition and keep policy in the owner guide
- do not introduce a new activity name without adding it here first
- keep `Context-Hygiene` implicit in prose; do not annotate every sentence with it

When this spec changes:

- update the dependent owner guides only if an activity name they reference was renamed or removed
- routine vocabulary additions in this file do not force changes elsewhere
- record any rename in `CHANGELOG.md` only if at least one owner guide adopted the old name in a released revision

## Known Gaps In Current Repo Guidance

Items the phase activity sequence exposes that this repository does not yet cover. None of these block adoption of the spec; they are recorded so future planning can address them.

- no AI owner guide for `Deployment` or `Operations` phase activities; `infra/` and `src/externalTest/` exist but are not bound to phase activities
- the `Red-Green Loop` is not named in `.agents/references/testing.md`, even though it is where most validation work happens
- the `Outer Product Loop` is not summarized in any single guide; readers must compose it from `ROADMAP.md`, `.agents/references/releases.md`, and `.agents/references/LEARNINGS.md`
- the `Review Loop` exit back into the milestone loop is implicit in `.agents/references/reviews.md` and `.agents/references/execution.md`

## Non-Goals

- this file does not change any policy
- this file does not define new validation, review, or release rules
- this file does not introduce a new `Phase` value
- this file does not replace the workflow mechanics in `.agents/references/workflow.md`
- this file is not a plan and must not be moved under `.agents/plans/PLAN_*.md`

## Cross-References

- `AGENTS.md` for the phase owner map and `Context Hygiene` rule
- `.agents/references/planning.md` and `.agents/references/plan-detailed-guide.md` for the `Phase` and `Status` enums
- `.agents/references/execution.md` for the canonical milestone loop
- `.agents/references/testing.md` and `.agents/references/troubleshooting.md` for the validation and red-green details
- `.agents/references/reviews.md` for review priorities and security triggers
- `.agents/references/releases.md` for release phase activities
- `.agents/references/workflow.md` for execution shapes that constrain who owns which artifact during a loop
