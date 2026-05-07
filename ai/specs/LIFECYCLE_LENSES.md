# Lifecycle Lenses And Loops Specification

This is the on-demand reference that defines the lens-and-loop vocabulary for AI agents working in this repository.
It is a vocabulary specification, not a workflow policy.
Owner guides under `ai/` may adopt these names to make context switches explicit, but they remain the source of truth for their own rules.

Use this file when:

- adding lens labels to an owner guide such as `ai/EXECUTION.md`, `ai/PLANNING.md`, `ai/TESTING.md`, or `ai/REVIEWS.md`
- discussing where a recurring activity belongs in the lifecycle
- diagnosing wrong-lens-at-wrong-time failures during a task
- proposing new owner guides for currently uncovered phases

Do not use this file as a substitute for the owner guides.
Lens names here are descriptive shortcuts; the binding rules live in the owning guide.

## Definitions

- **Lens**: a single mental mode an agent adopts while doing one kind of work. A lens has a question it answers, a primary owner guide, and an exit condition.
- **Switch**: an explicit transition between two lenses. A switch always implies dropping the previous lens's working set per the `Context Hygiene` rule in `AGENTS.md`.
- **Trigger**: a signal that requires a switch. Triggers can be planned (the next step in a loop) or conditional (e.g. a security-relevant change, a discovered plan gap).
- **Loop**: a sequence of lenses that iterates until an exit condition is met. Loops can nest.
- **Phase**: a coarse lifecycle stage that contains one or more lenses, mapped to the `Phase` enum in `ai/PLANNING.md`.

## Lens Catalogue

Each lens lists its question, primary owner guide, and typical exit condition.

### Discovery and Framing

- `Scan` — what artifacts and code already define this area? owner: `AGENTS.md` onboarding map, `ai/ARCHITECTURE.md`. Exit: relevant artifacts identified.
- `Frame` — what is the actual change being requested? owner: `AGENTS.md` Task Interpretation, `ai/PLANNING.md`. Exit: scope and ambiguity surfaced.
- `Clarify?` — does the user need to resolve a material ambiguity? owner: `ai/PLANNING.md` planning rules. Exit: ambiguity resolved or recorded as a fallback assumption.
- `Capture` — does this surface a durable repo lesson? owner: `ai/LEARNINGS.md`. Conditional.

### Requirements And Roadmap

- `Intake` — capture the requested work. owner: `ROADMAP.md`. Exit: roadmap entry exists or the request is rejected as not ready.
- `Refine` — is the request specific enough to plan? owner: `ROADMAP.md`, `ai/DESIGN.md`. Exit: an actionable description.
- `Prioritize` — does this work belong in the current phase? owner: `ROADMAP.md` `## Current Project State`.
- `Sequence` — what active work does this depend on or block? owner: `ROADMAP.md`.
- `Sync` — is the active-work tracking aligned with reality? owner: `ROADMAP.md`. Cross-cutting.

### Design, Spec, And Planning

- `Design` — decide product or contract behavior. owner: `ai/DESIGN.md`, governing specs.
- `Spec` — record the decided behavior in the governing spec artifact. owner: `ai/DOCUMENTATION.md` for routing, individual spec files for content.
- `Decompose` — split into commit-sized milestone checkpoints; pick a workflow mode. owner: `ai/PLANNING.md`, `ai/WORKFLOW.md`.
- `Validate-Plan` — run the plan readiness checklist before approval. owner: `ai/PLANNING.md` `Final Check`.
- `Replan?` — revise an approved plan when execution reality disagrees with it. owner: `ai/PLANNING.md`. Cross-cutting.

### Implementation

- `Code` — write the smallest implementation that satisfies the spec. owner: `ai/CODE_STYLE.md`, source files.
- `Docs` — route artifact updates per the change type. owner: `ai/DOCUMENTATION.md`.
- `Commit` — produce a milestone-shaped checkpoint with the required tracking-artifact updates. owner: `ai/EXECUTION.md` Milestone Commit Rules, `ai/WORKFLOW.md`.
- `Handoff` — report status, blockers, and any required push or pull request. owner: `ai/WORKFLOW.md`, `ai/EXECUTION.md` Completion Criteria.

### Testing And Verification

- `Plan-Tests` — choose the smallest sufficient validation for this change type. owner: `ai/TESTING.md`.
- `Author-Tests` — write or update the executable spec or reproduction. owner: existing test packages.
- `Run` — execute the chosen validation. owner: `ai/TESTING.md`, `ai/ENVIRONMENT_QUICK_REF.md`.
- `Diagnose?` — interpret a failure before changing anything. owner: `ai/references/TROUBLESHOOTING.md`.
- `Fix?` — apply the smallest correction to the implementation, test, or spec. owner: `ai/EXECUTION.md`.
- `Re-run` — confirm the previously failing validation now passes. owner: `ai/TESTING.md`.
- `Record` — write the actual outcome into `Validation Results` or worker log. owner: `ai/TESTING.md` Recording Validation, `ai/WORKFLOW.md`.

### Review

- `Self-Review` — first pass against `ai/REVIEWS.md` Review Priorities. owner: `ai/REVIEWS.md`.
- `Code Review` — peer-style review of the validated diff. owner: `ai/REVIEWS.md`.
- `Security Review?` — apply only when `ai/REVIEWS.md` security triggers fire. owner: `ai/REVIEWS.md`.
- `Docs Review?` — apply when the change is documentation-heavy. owner: `ai/REVIEWS.md`, `ai/DOCUMENTATION.md`.
- `Decide` — approve or request changes; loop back to `Code` or `Validation` if the latter. owner: `ai/REVIEWS.md`.

### Integration

- `Re-validate` — confirm the merge target still passes after rebase or conflict resolution. owner: `ai/TESTING.md`.
- `Resolve-Conflicts?` — merge conflict handling. owner: `ai/WORKFLOW.md`.
- `Merge` — land the work on `main`. owner: `AGENTS.md` Branch And Worktree Expectations.
- `Post-Merge-Verify` — confirm `main` still builds and matches the plan's `Implemented` state. owner: `ai/EXECUTION.md` Completion Criteria.

### Release

- `Gate` — confirm release preconditions. owner: `ai/RELEASES.md`.
- `Tag` — produce the release artifact. owner: `ai/RELEASES.md`.
- `Notes` — generate or update `CHANGELOG.md`. owner: `ai/RELEASES.md`.
- `Publish` — execute the release. owner: `ai/RELEASES.md`.
- `Post-Release-Cleanup` — archive plans, refresh `ROADMAP.md` active-work entries, close the loop into Continuous Improvement. owner: `ai/RELEASES.md`, `ai/PLANNING.md`, `ROADMAP.md`.

### Deployment And Operations

These lenses are listed for completeness; they currently have **no AI owner guide** in this repository. Treat them as aspirational until a guide owns them.

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
- `Capture-Learning` — record a durable repo-wide lesson. owner: `ai/LEARNINGS.md`.
- `Refactor?` — schedule structural improvement work as its own plan.
- `Tech-Debt-Plan?` — convert recurring pain into a planned roadmap item.
- `Sync` — feed outcomes back into `ROADMAP.md`.

## Phase To Lens Map

The repository uses the `Phase` enum from `ai/PLANNING.md`. This table maps each phase to its in-order lens sequence; `?` marks conditional lenses.

| Phase | In-order lenses | Primary owner guides |
| --- | --- | --- |
| Discovery | `Scan` → `Frame` → `Clarify?` → `Capture?` | `AGENTS.md`, `ai/ARCHITECTURE.md`, `ai/LEARNINGS.md` |
| Roadmap intake | `Intake` → `Refine` → `Prioritize` → `Sequence` → `Sync` | `ROADMAP.md`, `ai/DESIGN.md` |
| Planning | `Frame` → `Design` → `Spec` → `Decompose` → `Validate-Plan` → `Sync` → `Replan?` | `ai/PLANNING.md`, `ai/DESIGN.md`, `ai/templates/PLAN_TEMPLATE.md` |
| Implementation | `Spec` → `Code` → `Docs` → `Run` → `Replan?` → `Self-Review` → `Code Review` → `Security Review?` → `Commit` → `Handoff` | `ai/EXECUTION.md`, `ai/DOCUMENTATION.md`, `ai/CODE_STYLE.md`, `ai/WORKFLOW.md` |
| Testing | `Plan-Tests` → `Author-Tests` → `Run` → `Diagnose?` → `Fix?` → `Re-run` → `Record` | `ai/TESTING.md`, `ai/references/TROUBLESHOOTING.md` |
| Review | `Self-Review` → `Code Review` → `Security Review?` → `Docs Review?` → `Decide` | `ai/REVIEWS.md` |
| Integration | `Re-validate` → `Resolve-Conflicts?` → `Merge` → `Post-Merge-Verify` | `ai/WORKFLOW.md`, `AGENTS.md` |
| Release | `Gate` → `Tag` → `Notes` → `Publish` → `Post-Release-Cleanup` | `ai/RELEASES.md`, `CHANGELOG.md` |
| Deployment | `Stage` → `Smoke` → `Promote` → `Verify` → `Rollback?` | none yet (gap) |
| Operations | `Observe` → `Triage` → `Hotfix?` → `Patch?` → `Backport?` → `Deprecate?` | partial: `CHANGELOG.md`, `ROADMAP.md` |
| Continuous improvement | `Retrospect` → `Capture-Learning` → `Refactor?` → `Tech-Debt-Plan?` → `Sync` | `ai/LEARNINGS.md`, `ROADMAP.md` |
| Closed | none | `ai/PLANNING.md`, `ai/archive/` |

The `Implementation` row deliberately includes review and validation lenses because the existing milestone loop in `ai/EXECUTION.md` interleaves them. Do not split that loop apart on the basis of this table; the table is descriptive.

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

- Lenses: `Sync` (roadmap) → planning → implementation → release → `Retrospect` → `Capture-Learning` → `Sync` (roadmap).
- Cadence: per release.
- Owner: `ROADMAP.md` plus `ai/RELEASES.md` and `ai/LEARNINGS.md`.
- Exit: a release ships and its outcomes feed the next intake.

### 2. Plan Loop

- Lenses: `Frame` → `Design` → `Spec` → `Decompose` → `Validate-Plan` → `Replan?` → `Validate-Plan`.
- Cadence: per plan, until decision-complete.
- Owner: `ai/PLANNING.md`.
- Exit: plan reaches `Status=Ready`.

### 3. Milestone Execution Loop

- Lenses: the existing numbered loop in `ai/EXECUTION.md` Common Milestone Loop, expressed as `Spec` → `Code` → `Docs` → `Run` → `Replan?` → `Self-Review` → `Code Review` → `Security Review?` → `Commit` → `Handoff`.
- Cadence: per milestone within an approved plan.
- Owner: `ai/EXECUTION.md`.
- Exit: milestone commit lands and tracking artifacts are updated.

### 4. Red-Green Loop

- Lenses: `Run` → `Diagnose` → `Fix` → `Re-run`.
- Cadence: per failing validation, inside a milestone.
- Owner: `ai/TESTING.md`, with `ai/references/TROUBLESHOOTING.md` loaded on demand.
- Exit: previously failing validation passes; if it cannot pass, exit through `Replan?`.

### 5. Review Loop

- Lenses: `Self-Review` → `Code Review` → `Security Review?` → `Docs Review?` → `Decide` → loop back to `Code` or `Run` if changes are requested.
- Cadence: per diff before merge.
- Owner: `ai/REVIEWS.md`.
- Exit: an `Approve` decision; otherwise re-enter the milestone loop.

### 6. Operate-and-Improve Loop

- Lenses: `Observe` → `Triage` → (`Hotfix?` or `Patch?`) → `Capture-Learning` → `Sync`.
- Cadence: continuous, post-release.
- Owner: gap. Currently scattered across `ai/LEARNINGS.md`, `ROADMAP.md`, and `CHANGELOG.md`.
- Exit: the signal is resolved or scheduled as planned work.

## Cross-Cutting Triggers

These triggers can fire from any phase and force a switch.

- `Replan` — execution-time gap, contradicted locked decision, or scope drift. Owner: `ai/PLANNING.md`.
- `Security Review` — change touches an item in the `ai/REVIEWS.md` security triggers list.
- `Sync` — any change that affects active-work tracking or contract artifacts. Owner: `ROADMAP.md`, `ai/DOCUMENTATION.md`.
- `Capture-Learning` — recurring repo-wide lesson surfaces. Owner: `ai/LEARNINGS.md`.
- `Docs-Routing` — any artifact touch that changes a contract or maintainer-facing document. Owner: `ai/DOCUMENTATION.md`.
- `Context-Hygiene` — fires between any two lenses; agent must drop the prior lens's working set before loading the next. Owner: `AGENTS.md`.

## Adoption Guidance For Owner Guides

When an owner guide adopts lens names from this spec:

- list only the lenses that fire in the loop or section that guide actually owns
- tag the existing prose with the lens name in square brackets, for example `[Code]`, `[Validation]`, `[Replan?]`
- do not copy lens definitions from this file; link to this file for the definition and keep policy in the owner guide
- do not introduce a new lens name without adding it here first
- keep `Context-Hygiene` implicit in prose; do not annotate every sentence with it

When this spec changes:

- update the dependent owner guides only if a lens name they reference was renamed or removed
- routine vocabulary additions in this file do not force changes elsewhere
- record any rename in `CHANGELOG.md` only if at least one owner guide adopted the old name in a released revision

## Known Gaps In Current Repo Guidance

Items the lens map exposes that this repository does not yet cover. None of these block adoption of the spec; they are recorded so future planning can address them.

- no AI owner guide for `Deployment` or `Operations` lenses; `infra/` and `src/externalTest/` exist but are not bound to lenses
- the `Red-Green Loop` is not named in `ai/TESTING.md`, even though it is where most validation work happens
- the `Outer Product Loop` is not summarized in any single guide; readers must compose it from `ROADMAP.md`, `ai/RELEASES.md`, and `ai/LEARNINGS.md`
- the `Review Loop` exit back into the milestone loop is implicit in `ai/REVIEWS.md` and `ai/EXECUTION.md`

## Non-Goals

- this file does not change any policy
- this file does not define new validation, review, or release rules
- this file does not introduce a new `Phase` value
- this file does not replace the workflow modes in `ai/WORKFLOW.md`
- this file is not a plan and must not be moved under `ai/plans/active/PLAN_*.md`

## Cross-References

- `AGENTS.md` for the lifecycle owner map and `Context Hygiene` rule
- `ai/PLANNING.md` and `ai/references/PLAN_DETAILED_GUIDE.md` for the `Phase` and `Status` enums
- `ai/EXECUTION.md` for the canonical milestone loop
- `ai/TESTING.md` and `ai/references/TROUBLESHOOTING.md` for the validation and red-green details
- `ai/REVIEWS.md` for review priorities and security triggers
- `ai/RELEASES.md` for release lenses
- `ai/WORKFLOW.md` for execution modes that constrain who owns which artifact during a loop
