# Application Development Lifecycle Specification

This document specifies how the application development lifecycle should work for a new software repository.
It is **repo-agnostic**: it does not assume any particular file names, directory layout, language, build tool, branch model, or AI-agent guideline set.
A new repository should treat this spec as a normative description of *what* must exist and *how* the parts compose, then map it onto its own naming conventions during adoption.

The spec is organized around three layered models:

1. **Phases** — the coarse stages a piece of work passes through.
2. **Phase Activities** — the focused mental modes used inside each phase.
3. **Loops** — the iteration patterns that connect phases and phase activities.

A repository is conformant with this spec when each phase has an identifiable owner artifact, each activity has an identifiable owner artifact or owner role, and each loop has a defined entry, exit, and cadence.

## 1. Definitions

- **Phase**: a coarse lifecycle stage with explicit entry and exit criteria. A piece of work is in exactly one phase at a time.
- **Activity**: a single focused activity an agent or human performs inside a phase. An activity has a question it answers, an owner artifact, and an exit condition.
- **Switch**: an explicit transition between two phase activities or two phases. Every switch requires the prior working set to be dropped before the next one is loaded (`Context Hygiene`).
- **Trigger**: a signal that mandates a switch. Triggers are either *planned* (the next step in a loop) or *conditional* (e.g. a security-relevant change, a discovered plan gap, a failing validation).
- **Loop**: a sequence of phase activities or phases that iterates until a defined exit condition is met. Loops may nest.
- **Artifact**: a durable file or record that is the source of truth for some behavior, decision, or rule. Artifacts are categorized as *spec*, *contract*, *plan*, *log*, or *guide*.
- **Owner**: the artifact (or, when no artifact applies, the role) that is authoritative for a given activity, phase, or rule. Each rule has exactly one owner.
- **Gate**: a checkpoint that must pass before a phase transition is allowed. Gates are mechanical (executable) where possible, and human-judged otherwise.

## 2. Phases

The lifecycle has eleven phases. Phases are ordered by typical progression, but conditional phases (`Replan`, `Hotfix`, `Rollback`) can fire from anywhere.

| # | Phase | Entry | Exit / Gate |
| --- | --- | --- | --- |
| 1 | Discovery | a request, idea, or signal arrives | scope and ambiguity surfaced; either rejected or promoted to Roadmap |
| 2 | Roadmap Intake | promoted Discovery item | item is sequenced and prioritized in active-work tracking |
| 3 | Planning | a roadmap item is picked up | a *decision-complete* plan exists and is approved |
| 4 | Implementation | an approved plan exists | smallest spec-driven change exists locally and self-validates |
| 5 | Testing | implementation is locally complete | required validation passes |
| 6 | Review | validated change exists | reviewer(s) approve; or change returns to Implementation/Testing |
| 7 | Integration | review is approved | change lands on the integration branch and post-merge checks pass |
| 8 | Release | integrated change is release-ready | versioned artifact is published and release notes exist |
| 9 | Deployment | released artifact exists | artifact runs in the target environment and verifies green |
| 10 | Operations | artifact is live | signals are observed; incidents are triaged or scheduled |
| 11 | Continuous Improvement | release closed or recurring signal observed | learnings captured; next-cycle Roadmap updated |

A `Closed` state exists for plans whose lifecycle is complete and archived; it is not a phase but a terminal status.

### 2.1 Phase Entry And Exit Rules

- A phase transition is **explicit**: the work item's status changes, and any owning artifact is updated in the same change.
- A phase **cannot be skipped** unless the spec change-class explicitly allows it (e.g. a documentation-only change skips Deployment).
- A phase **can be re-entered** when a downstream phase fails its gate (e.g. failed Review re-enters Implementation).
- A phase exit gate is either an **executable check** (build, tests, contract checks, smoke tests) or a **named approval** (reviewer sign-off, release manager gate).

## 3. Phase Activities

Phase activities are grouped by the phase that owns them. An activity may appear in more than one phase when it represents the same activity at different cadences (e.g. `Sync` fires in Roadmap, Planning, and Continuous Improvement).

### 3.1 Discovery And Framing

- `Scan` — what artifacts and code already define this area?
- `Frame` — what is the actual change being requested? what is in and out of scope?
- `Clarify?` — does a material ambiguity require user input? *(conditional)*
- `Capture?` — does this surface a durable repo lesson worth recording? *(conditional)*

### 3.2 Roadmap And Requirements

- `Intake` — record the requested work in active-work tracking.
- `Refine` — restate the request as actionable behavior.
- `Prioritize` — does this belong in the current cycle?
- `Sequence` — what does this depend on or block?
- `Sync` — keep active-work tracking aligned with reality. *(cross-cutting)*

### 3.3 Design, Spec, And Planning

- `Design` — decide product or contract behavior.
- `Spec` — record the decided behavior in a governing executable or published spec artifact.
- `Decompose` — split into commit-sized milestone checkpoints; pick a workflow mode.
- `Validate-Plan` — run the plan readiness checklist before approval.
- `Replan?` — revise an approved plan when execution reality disagrees with it. *(cross-cutting)*

### 3.4 Implementation

- `Code` — write the smallest implementation that satisfies the spec.
- `Docs` — route documentation and contract updates per change-class.
- `Commit` — produce a milestone-shaped checkpoint with required tracking-artifact updates.
- `Handoff` — report status, blockers, and any required push or pull request.

### 3.5 Testing And Verification

- `Plan-Tests` — choose the smallest sufficient validation for this change-class.
- `Author-Tests` — write or update the executable spec or reproduction.
- `Run` — execute the chosen validation.
- `Diagnose?` — interpret a failure before changing anything. *(conditional)*
- `Fix?` — apply the smallest correction to implementation, test, or spec. *(conditional)*
- `Re-run` — confirm the previously failing validation now passes.
- `Record` — write the actual outcome into the plan's validation log.

### 3.6 Review

- `Self-Review` — first pass against the review priority list.
- `Code Review` — peer-style review of the validated diff.
- `Security Review?` — apply when security triggers fire. *(conditional)*
- `Docs Review?` — apply when the change is documentation-heavy. *(conditional)*
- `Decide` — approve or request changes; if changes, loop back.

### 3.7 Integration

- `Re-validate` — confirm the merge target still passes after rebase or conflict resolution.
- `Resolve-Conflicts?` — handle merge conflicts. *(conditional)*
- `Merge` — land the work on the integration branch.
- `Post-Merge-Verify` — confirm the integration branch still builds and matches the plan's intended state.

### 3.8 Release

- `Gate` — confirm release preconditions.
- `Tag` — produce the release artifact.
- `Notes` — generate or update release notes.
- `Publish` — execute the release.
- `Post-Release-Cleanup` — archive plans and refresh active-work tracking.

### 3.9 Deployment

- `Stage` — promote the artifact into a non-production environment.
- `Smoke` — run smoke or external-verification checks.
- `Promote` — promote to the next environment.
- `Verify` — confirm deployed behavior in the target environment.
- `Rollback?` — back out a failed deployment. *(conditional)*

### 3.10 Operations

- `Observe` — read production signals (metrics, logs, traces, user reports).
- `Triage` — classify an incident or defect.
- `Hotfix?` — produce a minimal fix outside the normal plan flow. *(conditional)*
- `Patch?` — produce a normal-flow corrective change. *(conditional)*
- `Backport?` — apply a fix to an older supported line. *(conditional)*
- `Deprecate?` — schedule removal of a behavior. *(conditional)*

### 3.11 Continuous Improvement

- `Retrospect` — review what worked and what did not after a release or major plan.
- `Capture-Learning` — record a durable repo-wide lesson.
- `Refactor?` — schedule structural improvement work as its own plan. *(conditional)*
- `Tech-Debt-Plan?` — convert recurring pain into a planned roadmap item. *(conditional)*
- `Sync` — feed outcomes back into active-work tracking.

## 4. Phase Activity Sequence

Each phase has an in-order primary activity sequence. `?` marks conditional phase activities.

| Phase | In-order phase activities |
| --- | --- |
| Discovery | `Scan` → `Frame` → `Clarify?` → `Capture?` |
| Roadmap Intake | `Intake` → `Refine` → `Prioritize` → `Sequence` → `Sync` |
| Planning | `Frame` → `Design` → `Spec` → `Decompose` → `Validate-Plan` → `Sync` → `Replan?` |
| Implementation | `Spec` → `Code` → `Docs` → `Run` → `Replan?` → `Self-Review` → `Code Review` → `Security Review?` → `Commit` → `Handoff` |
| Testing | `Plan-Tests` → `Author-Tests` → `Run` → `Diagnose?` → `Fix?` → `Re-run` → `Record` |
| Review | `Self-Review` → `Code Review` → `Security Review?` → `Docs Review?` → `Decide` |
| Integration | `Re-validate` → `Resolve-Conflicts?` → `Merge` → `Post-Merge-Verify` |
| Release | `Gate` → `Tag` → `Notes` → `Publish` → `Post-Release-Cleanup` |
| Deployment | `Stage` → `Smoke` → `Promote` → `Verify` → `Rollback?` |
| Operations | `Observe` → `Triage` → `Hotfix?` → `Patch?` → `Backport?` → `Deprecate?` |
| Continuous Improvement | `Retrospect` → `Capture-Learning` → `Refactor?` → `Tech-Debt-Plan?` → `Sync` |

The `Implementation` row deliberately interleaves review and validation phase activities because the milestone execution loop runs them in tight succession; this is descriptive, not a license to skip the dedicated `Testing` and `Review` phases for the overall change.

## 5. Loops

The lifecycle contains six loops. They nest as follows:

```
Outer Product Loop                              [per release]
  ├── Operate-and-Improve Loop                  [continuous, post-release]
  └── Plan Loop                                 [per plan]
        └── Milestone Execution Loop            [per milestone]
              ├── Red-Green Loop                [per failing validation]
              └── Review Loop                   [per diff before merge]
```

### 5.1 Outer Product Loop

- Phase Activities: `Sync` (roadmap) → planning → implementation → release → `Retrospect` → `Capture-Learning` → `Sync`.
- Cadence: per release.
- Exit: a release ships and its outcomes feed the next intake.

### 5.2 Plan Loop

- Phase Activities: `Frame` → `Design` → `Spec` → `Decompose` → `Validate-Plan` → `Replan?` → `Validate-Plan`.
- Cadence: per plan, until the plan is decision-complete.
- Exit: plan is approved and ready for execution.

### 5.3 Milestone Execution Loop

- Phase Activities: `Spec` → `Code` → `Docs` → `Run` → `Replan?` → `Self-Review` → `Code Review` → `Security Review?` → `Commit` → `Handoff`.
- Cadence: per milestone within an approved plan.
- Exit: milestone commit lands and tracking artifacts are updated.

### 5.4 Red-Green Loop

- Phase Activities: `Run` → `Diagnose` → `Fix` → `Re-run`.
- Cadence: per failing validation, inside a milestone.
- Exit: previously failing validation passes; if it cannot pass, exit through `Replan?`.

### 5.5 Review Loop

- Phase Activities: `Self-Review` → `Code Review` → `Security Review?` → `Docs Review?` → `Decide` → loop back to `Code` or `Run` if changes requested.
- Cadence: per diff before merge.
- Exit: an `Approve` decision; otherwise re-enter the milestone loop.

### 5.6 Operate-and-Improve Loop

- Phase Activities: `Observe` → `Triage` → (`Hotfix?` or `Patch?`) → `Capture-Learning` → `Sync`.
- Cadence: continuous, post-release.
- Exit: the signal is resolved or scheduled as planned work.

## 6. Cross-Cutting Triggers

Triggers can fire from any phase and force a switch.

- `Replan` — execution-time gap, contradicted locked decision, scope drift, or design decision discovered mid-execution.
- `Security Review` — change touches authentication, authorization, secrets, sensitive data, deployment-facing config, CI permissions, or release/publish paths.
- `Sync` — any change that affects active-work tracking or contract artifacts.
- `Capture-Learning` — a recurring repo-wide lesson surfaces.
- `Docs-Routing` — a change touches a contract or maintainer-facing document.
- `Context-Hygiene` — fires between any two phase activities; the prior activity's working set must be dropped before the next is loaded.
- `Rollback` — deployed behavior fails verification.
- `Hotfix` — a production incident requires a fix outside the normal plan flow.

## 7. Required Artifact Set

A conformant repository must provide artifacts that own the following responsibilities. Names below are *roles*, not file names; map each role to one or more files in the target repo.

| Role | Owns | Typical realization |
| --- | --- | --- |
| Project Charter | mission, supported scope, public contract summary | a top-level project overview |
| Setup Guide | local environment, tooling, onboarding, troubleshooting | a setup or environment doc |
| Roadmap | active-work tracking, sequencing, current-cycle state | a roadmap doc |
| Release History | shipped changes per version | a changelog |
| Plan | per-task decision-complete handoff document | a plan file per active task |
| Executable Spec | behavior verified by automation | tests, contract checks, benchmarks |
| Published Contract | human-facing API/contract docs | reference docs, OpenAPI/JSON schema, example requests |
| Engineering Rules | spec-driven rules, lifecycle, branch and worktree policy, definition of done | an engineering rules doc |
| Phase Owner Guides | one guide per phase or owning activity group | per-phase guides (planning, execution, testing, review, release, etc.) |
| Learnings | durable engineering lessons that survive refactors | a learnings doc |
| Architecture Snapshot | current codebase map and structural guidance | an architecture doc |

A repository that lacks any of these has a *gap*; the gap should be recorded in active-work tracking with an owner.

## 8. Spec-Driven Development Rule

Behavior changes must be made spec-first:

1. Identify the behavior being changed.
2. Identify the spec artifact that defines that behavior.
3. Update or add the spec **first**.
4. Implement the smallest code change that satisfies the updated spec.
5. Verify the executable and published specs remain aligned.

If the intended behavior is not clear enough to express as a spec, the work returns to `Planning` and stays there until it is.

### 8.1 Truth Priority

When sources of truth disagree, resolve in this order:

1. explicit user request in the current task
2. executable specs (tests, contract checks, compatibility checks, benchmark gates)
3. published contract documents
4. current-cycle state in the roadmap
5. active planning entries
6. release history
7. AI- or human-facing guidance documents

## 9. Definition Of Done

A change is complete when **all** of the following hold:

- the intended behavior exists in an appropriate spec artifact
- implementation and specs agree
- public contract artifacts are updated when behavior changed
- required validation has passed and the result is recorded against the plan
- the change has landed on the integration branch (or, when run from a side branch, has been pushed and either merged or proposed via pull request)
- the active-work tracking entry reflects the post-change state
- if released, the release artifact is published and notes are written

## 10. Branch And Worktree Invariants

- Treat the integration branch as the only source of truth for *completed* work.
- Keep side-branch or worktree implementation isolated until the planned scope is complete and locally validated.
- Prefer merging accepted branches or pull requests; use cherry-pick only when the user asks for it, when accepting less than the full branch, or when a normal merge is not viable, and record the reason.
- Do not cut releases from unintegrated side branches, worktrees, detached tips, or changes that have not landed on the integration branch.

## 11. Roles

Roles can be filled by humans, AI agents, or both. A single person may hold multiple roles.

- **Requester** — provides the task or signal that enters Discovery.
- **Planner** — owns the Plan Loop and produces a decision-complete plan.
- **Implementer** — executes the Milestone Execution Loop.
- **Tester** — owns the Red-Green Loop and validates the change.
- **Reviewer** — owns the Review Loop and decides Approve / Request-Changes.
- **Integrator** — lands the change on the integration branch.
- **Releaser** — runs the Release phase.
- **Operator** — runs Deployment and Operations.
- **Curator** — runs Continuous Improvement; captures learnings; updates active-work tracking.

## 12. Adoption Guide

To adopt this spec in a new repository:

1. **Create the artifact set** named in §7, using whatever file names fit the repo's conventions.
2. **Map each activity to an owner artifact.** Every activity in §3 needs exactly one owner. Where a single artifact is too small, split it; where two artifacts overlap, merge.
3. **Pick the workflow modes** the repo will support (e.g. linear, single-plan parallel, multi-plan parallel) and document them in the engineering-rules artifact.
4. **Adopt activity tags incrementally.** Tag existing prose in owner artifacts with bracketed activity names (e.g. `[Code]`, `[Run]`, `[Replan?]`) rather than rewriting the prose.
5. **Define the change-class table.** For each change-class (public behavior, internal refactor, docs-only, setup, release-history), state which artifacts must move together. This is the routing table the `Docs` activity consumes.
6. **Define the validation table.** For each change-class, state the smallest sufficient validation. This is the table the `Plan-Tests` and `Run` phase activities consume.
7. **Define the gates.** For each phase exit, state whether the gate is executable or named-approval.
8. **Wire up cross-cutting triggers.** Each trigger in §6 must point to the artifact that owns it.
9. **Record gaps.** Anything in the spec that the repo does not (yet) cover becomes a roadmap item with an owner.

## 13. Conformance Levels

A repository declares its conformance level explicitly.

- **L1 — Spec-Driven Core**: §7 artifacts exist, §8 spec-first rule is enforced, §9 definition of done is enforced. Phases 1–7 are owned. This is the minimum.
- **L2 — Released**: L1 plus phases 8 (Release) and 11 (Continuous Improvement).
- **L3 — Operated**: L2 plus phases 9 (Deployment) and 10 (Operations).
- **L4 — Activity-Tagged**: any of the above, plus owner artifacts that have adopted activity tags (§12 step 4) and explicit cross-cutting trigger wiring (§12 step 8).

Conformance level is recorded in the engineering-rules artifact and reviewed at each release.

## 14. Non-Goals

- This spec does **not** mandate a specific branching model (trunk-based, GitFlow, release branches).
- This spec does **not** mandate a specific issue tracker, CI system, or hosting platform.
- This spec does **not** mandate file names or directory layout.
- This spec does **not** define agile/waterfall ceremonies; it defines lifecycle structure.
- This spec does **not** prescribe team size, sprint length, or release cadence.
- This spec does **not** replace product, security, or compliance policies; it gives them owner slots.

## 15. Glossary Of Common Mappings

When mapping the spec onto common stacks, the following are typical (not normative):

- *Executable Spec* → unit tests, integration tests, contract tests, snapshot tests, benchmark gates.
- *Published Contract* → OpenAPI/JSON Schema, gRPC `.proto`, GraphQL schema, public README, reference docs.
- *Roadmap* → a `ROADMAP.md`, a project-board column, or an issue tracker label.
- *Release History* → `CHANGELOG.md`, GitHub Releases, package registry release notes.
- *Phase Owner Guide* → contributor guides, runbooks, or AI-agent guideline files.
- *Active-Work Tracking* → roadmap doc, project board, milestone, or kanban column.

## 16. Versioning Of This Spec

- This spec is itself versioned. Breaking changes increment the major version; additive activity or phase entries increment the minor version; clarifications increment the patch version.
- A repository pins the spec version it conforms to in its engineering-rules artifact.
- Renames of phase activities or phases are breaking changes and must be recorded with the rename map for downstream repos to follow.
