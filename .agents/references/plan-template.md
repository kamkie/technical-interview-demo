# Plan: <title>

`.agents/references/plan-template.md` owns the canonical skeleton for `.agents/plans/PLAN_*.md` files.
When creating a concrete plan, replace placeholders and remove this ownership note from the plan file.

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Planning |
| Status | Draft |

Use one lifecycle phase from `.agents/references/application-lifecycle.md`: `Conceptualization`, `Analysis`, `Triage`, `Planning`, `Implementation`, `Verification`, `Review`, `Integration`, `Release`, `Deployment`, `Operations`, or `Maintenance`.

## Planning Readiness
| Field | Value |
| --- | --- |
| Decision Complete | No |
| Blocking Open Questions | Q1, Q2, or None |
| Accepted Fallbacks | None |
| Ready For Execution | No |
| Last Updated | YYYY-MM-DD |

## Linked Pre-Planning Artifacts
| Artifact | Path | Role | Status |
| --- | --- | --- | --- |
| ADR | None or `docs/decisions/NNNN-<kebab-title>.md` | Durable decision rationale when needed | Proposed/Accepted/None |
| PRD | None or `docs/requirements/PRD_<topic>.md` | Product intent when broad user-facing scope needs definition | Draft/Accepted/None |
| Spec | None or `docs/specs/SPEC_<topic>.md` | Behavior, contract, acceptance criteria, and validation mapping when existing specs are insufficient | Draft/Accepted/None |

## Summary
- What will change
- Why it matters
- How success will be measured

## Scope
- In scope
- Out of scope

## Current State
- Current lifecycle phase/activity, when known
- Current behavior
- Current constraints
- Relevant existing specs and code

## Requirement Gaps And Open Questions
| ID | Question / Gap | Why It Matters | Owner | Status | Fallback / Decision | Blocks Ready? |
| --- | --- | --- | --- | --- | --- | --- |
| Q1 | Material question still requiring input | Why the answer changes scope, behavior, compatibility, rollout, acceptance criteria, or validation | User/Agent | Open/Answered/Deferred | Pending | Yes/No |

## Decision Log And Assumptions
| ID | Decision / Assumption | Source | Date | Revisit Trigger |
| --- | --- | --- | --- | --- |
| D1 | User decision, repo-truth decision, or accepted fallback assumption | User/Spec/Code/Agent fallback | YYYY-MM-DD | When this should be reconsidered, or None |

## Execution Shape And Shared Files
- Recommended shape: `M0: solo`, `M1: sidecar-readonly`, `M2: bounded-worker`, `M3: parallel-sliced`, or `M4: full-sidecar`
- Why that planned shape fits best; do not defer the mode decision to execution
- Coordinator-owned or otherwise shared files if the work is delegated
- Candidate worker boundaries or plan splits if later delegation becomes necessary
- If this coordinates multiple selected plans, use `M3: parallel-sliced` unless the decision log explains serial execution or `M4` sidecar gates

## Affected Artifacts
- Pre-planning artifacts
- Tests
- Docs
- OpenAPI
- HTTP examples
- Source files
- Owning AI guide updates when durable repo guidance changes
- Build or benchmark checks

## Progress Tracker
| Task | Status | Owner | Commit | Validation | Notes |
| --- | --- | --- | --- | --- | --- |
| 1: <name> | Not Started/In Progress/Blocked/Done/Skipped | Agent/Coordinator/Worker | Pending | Pending | Progress note or blocker |

## Execution Tasks
### Task 1: <name>
| Field | Value |
| --- | --- |
| Status | Not Started |
| Goal | ... |
| Owned Files Or Packages | ... |
| Coordinator-Owned Shared Files | ... |
| Context Required | `none beyond AGENTS.md, .agents/references/execution.md, and this plan` for small tasks; otherwise name exact guides, references, plan sections, source packages, or spec artifacts |
| Behavior To Preserve | ... |
| Deliverables | ... |
| Validation Checkpoint | ... |
| Commit Checkpoint | ... |

## Blockers And Replan Triggers
| Trigger / Blocker | Response | Owner | Status |
| --- | --- | --- | --- |
| Execution-time blocker or condition that invalidates the plan | How to pause, ask, replan, or narrow scope | User/Agent/Coordinator | Open/Resolved/Deferred |

## Edge Cases And Failure Modes
- important error cases
- compatibility risks
- migration or rollout concerns

## Validation Plan
- commands to run
- tests to add or update
- docs or contract checks
- manual verification steps

## Verification Strategy
- unit tests (logic, edge cases)
- integration tests (database, external services)
- contract tests (OpenAPI, REST Docs)
- smoke/benchmark tests
- verification of negative scenarios and error handling

## Better Engineering Notes
- prerequisite cleanup included in the plan
- deferred follow-up work that should not be hidden

## Validation Results
| Date | Command | Scope | Result | Notes |
| --- | --- | --- | --- | --- |
| YYYY-MM-DD | Pending | Full build, targeted tests, docs check, or manual verification | Pending/Passed/Failed/Skipped | Workflow state may hold temporary per-task detail until the coordinator integrates it when `.agents/references/workflow.md` says so |

## User Validation
- Short walkthrough for the user to verify the delivered behavior
