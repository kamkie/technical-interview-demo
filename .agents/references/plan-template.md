# Plan: <title>

`.agents/references/plan-template.md` owns the canonical skeleton for `.agents/plans/PLAN_*.md` files.
When creating a concrete plan, replace placeholders and remove this ownership note from the plan file.

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Planning |
| Status | Draft |

## Planning Readiness
| Field | Value |
| --- | --- |
| Decision Complete | No |
| Blocking Open Questions | Q1, Q2, or None |
| Accepted Fallbacks | None |
| Ready For Execution | No |
| Last Updated | YYYY-MM-DD |

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
- Recommended shape: one local branch, delegated one-plan work, or coordinated multi-plan work
- Why that shape fits best
- Coordinator-owned or otherwise shared files if the work is delegated
- Candidate worker boundaries or plan splits if later delegation becomes necessary

## Affected Artifacts
- Tests
- Docs
- OpenAPI
- HTTP examples
- Source files
- Owning AI guide updates when durable repo guidance changes
- Build or benchmark checks

## Progress Tracker
| Milestone | Status | Owner | Commit | Validation | Notes |
| --- | --- | --- | --- | --- | --- |
| 1: <name> | Not Started/In Progress/Blocked/Done/Skipped | Agent/Coordinator/Worker | Pending | Pending | Progress note or blocker |

## Execution Milestones
### Milestone 1: <name>
| Field | Value |
| --- | --- |
| Status | Not Started |
| Goal | ... |
| Owned Files Or Packages | ... |
| Coordinator-Owned Shared Files | ... |
| Context Required | `none beyond AGENTS.md, .agents/references/execution.md, and this plan` for small milestones; otherwise name exact guides, references, plan sections, source packages, or spec artifacts |
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

## Testing Strategy
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
| YYYY-MM-DD | Pending | Full build, targeted tests, docs check, or manual verification | Pending/Passed/Failed/Skipped | Worker logs may hold temporary per-milestone detail until the coordinator integrates it when `.agents/references/workflow.md` says so |

## User Validation
- Short walkthrough for the user to verify the delivered behavior
