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

## Required Content Checklist
- what behavior is changing and why
- which lifecycle phase/activity owns this work, using `docs/specs/application-lifecycle-spec.md` and `docs/specs/lifecycle-phase-activities.md` wording
- whether the plan is decision-complete and ready for execution
- which `ROADMAP.md` entry tracks this plan, or which new roadmap entry this plan added
- what is out of scope
- which specs or contract artifacts define the behavior
- which source files or packages are likely to change
- what compatibility promises must be preserved
- what edge cases, failure modes, migration, rollout, or benchmark risks matter
- what requirement gaps still need input, who owns each answer, and whether they block readiness
- which answered questions, repo-truth decisions, and fallback assumptions are locked
- which execution shape fits and why
- which files stay coordinator-owned if delegation is realistic
- how milestone progress will be tracked during execution
- which execution-time blockers or replan triggers need explicit handling
- what context each milestone requires before execution, with no-extra-context milestones stated explicitly
- which tests, docs, OpenAPI, HTTP examples, README, or AI guides must move
- what testing strategy applies, including non-applicable layers for docs-only or AI-guidance-only plans
- what validation proves completion
- where validation results will be recorded
- how the user can verify the delivered behavior
