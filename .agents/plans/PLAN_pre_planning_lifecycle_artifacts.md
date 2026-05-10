# Plan: Pre-Planning Artifact And Lifecycle Vocabulary Adoption

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Implementation |
| Status | In Progress |

## Planning Readiness
| Field | Value |
| --- | --- |
| Decision Complete | Yes |
| Blocking Open Questions | None |
| Accepted Fallbacks | None |
| Ready For Execution | Yes |
| Last Updated | 2026-05-10 |

## Summary
- Implement ADR 0001 and ADR 0002 as one coordinated documentation and AI-guidance change after the decision records are accepted.
- Introduce optional ADR, PRD, spec, and plan artifact roles without making routine maintenance carry extra process.
- Replace the current lifecycle vocabulary with the accepted one-word phase names from ADR 0002 across the lifecycle owner, planning rules, roadmap, and human-facing AI workflow guide.
- Success means accepted ADRs, short artifact templates, aligned owner guidance, updated roadmap tracking, no unresolved old lifecycle wording in live guidance except historical context, and recorded documentation-only validation.

## Scope
- In scope:
  - Record explicit acceptance of `docs/decisions/0001-adopt-pre-planning-artifacts.md` and `docs/decisions/0002-align-lifecycle-vocabulary-with-industry-practice.md`.
  - Add minimal templates or skeletons for ADRs, PRDs, and standalone specs.
  - Add artifact routing and ownership for `docs/decisions/*.md`, `docs/requirements/*.md`, and `docs/specs/*.md`.
  - Update `.agents/references/application-lifecycle.md` to the accepted lifecycle vocabulary: `Conceptualization`, `Analysis`, `Triage`, `Planning`, `Implementation`, `Verification`, `Review`, `Integration`, `Release`, `Deployment`, `Operations`, and `Maintenance`.
  - Update `.agents/references/planning.md` and `.agents/references/plan-template.md` so execution plans can reference ADR, PRD, and spec artifacts and use the accepted lifecycle phase names.
  - Update `AGENTS.md`, `README.md`, `WORKING_WITH_AI.md`, and `ROADMAP.md` where their live guidance names phases, routes artifacts, or describes pre-planning workflow.
  - Keep `ROADMAP.md` pointed at this plan and avoid duplicate loose intake bullets for the same ADR/spec-format work.
- Out of scope:
  - Runtime code, tests, controllers, services, database migrations, OpenAPI baselines, REST Docs snippets, benchmarks, and release work.
  - Rewriting archived plans, changelog history, or historical decision context solely to rename old phase labels.
  - Making PRDs or standalone specs mandatory for every change.
  - Introducing Scrum, enterprise governance, compliance gates, or an external issue tracker.

## Current State
- ADR 0001 and ADR 0002 exist under `docs/decisions/` and are accepted as of 2026-05-10.
- ADR 0001 proposes optional pre-planning artifacts: ADR, PRD, spec, and plan.
- ADR 0002 proposes one-word lifecycle phase names and maps them to common SDLC and product-delivery terminology.
- `.agents/references/application-lifecycle.md` is the live lifecycle vocabulary owner and now uses the accepted lifecycle vocabulary from ADR 0002.
- `.agents/references/planning.md` still says valid plan phase values mirror the current eleven-phase lifecycle model.
- `WORKING_WITH_AI.md` still has human-facing sections for `Discovery`, `Roadmap Intake`, `Testing And Review`, and `Continuous Improvement`.
- `ROADMAP.md` now tracks this work as in-progress plan-backed work and no longer keeps the original ADR/spec-format ideas as loose intake bullets.
- This plan now uses `Phase | Implementation` because the ADRs are accepted and implementation has started.

## Requirement Gaps And Open Questions
| ID | Question / Gap | Why It Matters | Owner | Status | Fallback / Decision | Blocks Ready? |
| --- | --- | --- | --- | --- | --- | --- |
| Q1 | Are ADR 0001 and ADR 0002 accepted as the implementation source for this workflow change? | The plan changes standing AI guidance, human-facing workflow documentation, templates, and roadmap vocabulary; executing it before acceptance would treat proposed decisions as active policy. | User | Answered | ADR 0001 and ADR 0002 accepted by explicit user instruction on 2026-05-10. | No |

## Decision Log And Assumptions
| ID | Decision / Assumption | Source | Date | Revisit Trigger |
| --- | --- | --- | --- | --- |
| D1 | Use one integrated execution plan for ADR 0001 and ADR 0002 because their follow-up changes share the same lifecycle, planning, documentation, template, and roadmap files. | Agent planning review | 2026-05-10 | If the user asks to split artifact-template work from lifecycle-vocabulary work. |
| D2 | Move this plan into execution because ADR 0001 and ADR 0002 are accepted by explicit user instruction and no readiness stopper remains. | User instruction and planning guide readiness rules | 2026-05-10 | If either ADR is revised before implementation completes. |
| D3 | Keep templates short and skip-friendly; the workflow must clarify early decisions without creating mandatory ceremony for routine changes. | ADR 0001 | 2026-05-10 | If implementation guidance starts requiring PRDs/specs for routine maintenance. |
| D4 | Use the one-word lifecycle labels from ADR 0002 as the target vocabulary after acceptance. | ADR 0002 and user terminology decisions | 2026-05-10 | If ADR 0002 is revised before acceptance. |
| D5 | Leave historical changelog entries, archived plans, and historical ADR context unchanged unless live guidance points to stale names. | Documentation guide and ADR 0002 confirmation | 2026-05-10 | If a historical reference is actively used as current guidance. |
| D6 | During the current interactive documentation session, builds, tests, linters, formatters, and generated documentation checks remain skipped unless the user explicitly re-enables them; manual review and `git diff --check` may be used for documentation-only checkpoints. | Prior interactive-session constraint and current validation request | 2026-05-10 | If the user asks for normal validation or ends the constrained documentation session. |

## Execution Shape And Shared Files
- Recommended shape: `M0: solo`.
- This work touches shared policy and routing files, so a single coordinator should sequence the edits and keep terminology consistent.
- Optional read-only sidecar review can be added after drafting the lifecycle and planning guide changes, but no worker should edit the same shared files in parallel.
- Coordinator-owned shared files:
  - `AGENTS.md`
  - `README.md`
  - `WORKING_WITH_AI.md`
  - `ROADMAP.md`
  - `.agents/plans/PLAN_pre_planning_lifecycle_artifacts.md`
  - `.agents/references/application-lifecycle.md`
  - `.agents/references/planning.md`
  - `.agents/references/plan-template.md`
  - `.agents/references/documentation.md`
  - `docs/decisions/0001-adopt-pre-planning-artifacts.md`
  - `docs/decisions/0002-align-lifecycle-vocabulary-with-industry-practice.md`
  - new ADR, PRD, and spec template files

## Affected Artifacts
- Tests: none expected.
- Docs and AI guidance:
  - `docs/decisions/0001-adopt-pre-planning-artifacts.md`
  - `docs/decisions/0002-align-lifecycle-vocabulary-with-industry-practice.md`
  - `docs/decisions/ADR_TEMPLATE.md`
  - `docs/requirements/PRD_TEMPLATE.md`
  - `docs/specs/SPEC_TEMPLATE.md`
  - `.agents/references/application-lifecycle.md`
  - `.agents/references/planning.md`
  - `.agents/references/plan-template.md`
  - `.agents/references/documentation.md`
  - `AGENTS.md`
  - `README.md`
  - `WORKING_WITH_AI.md`
  - `ROADMAP.md`
- OpenAPI: none.
- HTTP examples: none.
- Source files: none.
- Build or benchmark checks: none expected for the documentation-only implementation.

## Progress Tracker
| Task | Status | Owner | Commit | Validation | Notes |
| --- | --- | --- | --- | --- | --- |
| 1: Accept decisions and unlock implementation | Done | User/Agent | `docs(plan): accept pre-planning lifecycle ADRs` | Manual readiness readback passed | Q1 answered by explicit user instruction; plan moved to execution. |
| 2: Add artifact templates and routing | Done | Agent | `docs(workflow): add pre-planning artifact templates` | Manual template/routing review and `git diff --check` passed | Templates are short and skip-friendly; documentation routing owns ADR, PRD, and spec locations. |
| 3: Update lifecycle vocabulary owner | Done | Agent | `docs(lifecycle): adopt accepted phase vocabulary` | Manual lifecycle reference review and `git diff --check` passed | Lifecycle owner now defines the accepted twelve-phase model and artifact roles. |
| 4: Align planning guidance and plan template | Not Started | Agent | Pending | Pending | Depends on Task 3 terminology. |
| 5: Align human-facing guidance, indexes, and roadmap | Not Started | Agent | Pending | Pending | Depends on Tasks 2-4. |
| 6: Review, validate, and close plan implementation | Not Started | Agent | Pending | Pending | Manual review only unless normal validation is re-enabled. |

## Execution Tasks
### Task 1: Accept decisions and unlock implementation
| Field | Value |
| --- | --- |
| Status | Done |
| Goal | Confirm ADR 0001 and ADR 0002 are accepted or revise them before they become active policy. |
| Owned Files Or Packages | `docs/decisions/0001-adopt-pre-planning-artifacts.md`, `docs/decisions/0002-align-lifecycle-vocabulary-with-industry-practice.md`, this plan |
| Coordinator-Owned Shared Files | `ROADMAP.md` |
| Context Required | `AGENTS.md`, `.agents/references/execution.md`, this plan, both ADRs |
| Behavior To Preserve | Proposed ADRs must not silently become policy without an explicit acceptance decision or user instruction. |
| Deliverables | ADR status updates to `Accepted` with acceptance date, or a revised plan/ADR set if the user changes the decision. |
| Validation Checkpoint | Passed: both ADR statuses, this plan's readiness state, and `ROADMAP.md` status agree after acceptance. |
| Commit Checkpoint | `docs(plan): accept pre-planning lifecycle ADRs` |

### Task 2: Add artifact templates and routing
| Field | Value |
| --- | --- |
| Status | Done |
| Goal | Add compact templates for the accepted pre-planning artifacts and route them through the documentation owner. |
| Owned Files Or Packages | `docs/decisions/ADR_TEMPLATE.md`, `docs/requirements/PRD_TEMPLATE.md`, `docs/specs/SPEC_TEMPLATE.md`, `.agents/references/documentation.md` |
| Coordinator-Owned Shared Files | `AGENTS.md`, `WORKING_WITH_AI.md`, `ROADMAP.md`, this plan |
| Context Required | `AGENTS.md`, `.agents/references/execution.md`, this plan, `.agents/references/documentation.md`, `.agents/references/references-rules.md`, ADR 0001 |
| Behavior To Preserve | ADRs, PRDs, and specs remain optional and purpose-driven; plans remain the execution handoff artifact. |
| Deliverables | Minimal templates or skeletons, artifact ownership rules for ADR/PRD/spec locations, and skip rules for routine changes. |
| Validation Checkpoint | Passed: templates are short, current-state, and aligned with ADR 0001; no template requires unnecessary ceremony. |
| Commit Checkpoint | `docs(workflow): add pre-planning artifact templates` |

### Task 3: Update lifecycle vocabulary owner
| Field | Value |
| --- | --- |
| Status | Done |
| Goal | Make `.agents/references/application-lifecycle.md` the accepted owner for the new lifecycle phase and activity vocabulary. |
| Owned Files Or Packages | `.agents/references/application-lifecycle.md` |
| Coordinator-Owned Shared Files | `.agents/references/planning.md`, `.agents/references/plan-template.md`, `WORKING_WITH_AI.md`, `ROADMAP.md`, this plan |
| Context Required | `AGENTS.md`, `.agents/references/execution.md`, this plan, `.agents/references/application-lifecycle.md`, `.agents/references/references-rules.md`, ADR 0002 |
| Behavior To Preserve | The lifecycle guide remains an owner of vocabulary and routing, not a task-specific progress log or replacement for focused owner guides. |
| Deliverables | Updated phases, entries/exits, activity catalogue, phase activity sequence, loops, cross-cutting triggers, required artifact roles, current gaps, non-goals, and maintenance wording. |
| Validation Checkpoint | Passed: manual reference-document review against `.agents/references/references-rules.md`; targeted search confirms live lifecycle tables no longer use old phase names except in the migration map. |
| Commit Checkpoint | `docs(lifecycle): adopt accepted phase vocabulary` |

### Task 4: Align planning guidance and plan template
| Field | Value |
| --- | --- |
| Status | Not Started |
| Goal | Teach plan creation, readiness review, and plan files to use the accepted lifecycle vocabulary and linked pre-planning artifacts. |
| Owned Files Or Packages | `.agents/references/planning.md`, `.agents/references/plan-template.md` |
| Coordinator-Owned Shared Files | `.agents/references/application-lifecycle.md`, `.agents/references/documentation.md`, `AGENTS.md`, this plan |
| Context Required | `AGENTS.md`, `.agents/references/execution.md`, this plan, `.agents/references/planning.md`, `.agents/references/plan-template.md`, `.agents/references/references-rules.md`, ADR 0001, ADR 0002 |
| Behavior To Preserve | Plans stay decision-complete execution artifacts; rough ideas route to `Conceptualization`, requirement definition routes to `Analysis`, sequencing routes to `Triage`, and implementation handoff stays in `Planning`. |
| Deliverables | Updated valid phase values, readiness guidance, plan workflow, artifact-link prompts, and lifecycle placeholders. |
| Validation Checkpoint | Manual plan-guidance review confirms `Planning Readiness`, open questions, decisions, progress tracking, and plan-task quality rules still align. |
| Commit Checkpoint | Commit after planning guide and template alignment. |

### Task 5: Align human-facing guidance, indexes, and roadmap
| Field | Value |
| --- | --- |
| Status | Not Started |
| Goal | Make developer-facing docs and active-work tracking use the accepted lifecycle and artifact roles. |
| Owned Files Or Packages | `AGENTS.md`, `README.md`, `WORKING_WITH_AI.md`, `ROADMAP.md` |
| Coordinator-Owned Shared Files | `.agents/references/application-lifecycle.md`, `.agents/references/planning.md`, `.agents/references/documentation.md`, this plan |
| Context Required | `AGENTS.md`, `.agents/references/execution.md`, this plan, `.agents/references/documentation.md`, `README.md`, `WORKING_WITH_AI.md`, `ROADMAP.md`, both ADRs |
| Behavior To Preserve | `WORKING_WITH_AI.md` stays a navigation guide, `AGENTS.md` stays the repo-level AI entry point, and `ROADMAP.md` keeps active tracking without duplicating plan details. |
| Deliverables | Updated human-facing lifecycle sections, repo-level document map entries, README discoverability text when needed, roadmap headings/status notes, and confirmation that ADR/spec-format work is tracked only once. |
| Validation Checkpoint | Manual documentation review confirms AI-facing and human-facing guidance route to the same owners and old names are retained only where intentional. |
| Commit Checkpoint | Commit after human-facing and roadmap alignment. |

### Task 6: Review, validate, and close plan implementation
| Field | Value |
| --- | --- |
| Status | Not Started |
| Goal | Verify the documentation-only workflow change is internally consistent and record the result. |
| Owned Files Or Packages | this plan, `ROADMAP.md` |
| Coordinator-Owned Shared Files | all files touched by Tasks 1-5 |
| Context Required | `AGENTS.md`, `.agents/references/execution.md`, `.agents/references/testing.md`, `.agents/references/reviews.md`, this plan |
| Behavior To Preserve | No runtime behavior or published API contract changes. |
| Deliverables | Updated progress tracker, validation ledger, final roadmap status, and handoff notes. |
| Validation Checkpoint | Manual documentation/process review during the interactive documentation session; run `./build.ps1 build` only if normal validation is re-enabled before handoff. |
| Commit Checkpoint | Commit final tracking updates after review and validation. |

## Blockers And Replan Triggers
| Trigger / Blocker | Response | Owner | Status |
| --- | --- | --- | --- |
| ADR 0001 or ADR 0002 remains `Proposed`. | Keep this plan `Needs Input`; do not implement policy changes until accepted or explicitly authorized. | User/Agent | Resolved |
| The user revises the accepted lifecycle vocabulary before implementation. | Update ADR 0002 first, then revise this plan and roadmap before changing owner guides. | User/Agent | Open |
| Template paths or names prove inconsistent with repository conventions during execution. | Replan the affected template task, record the chosen paths in the decision log, and keep artifact routing aligned. | Agent | Open |
| Reference-document rules require a standing rule change before editing `.agents/references/*.md`. | Update `.agents/references/references-rules.md` first, then edit the affected owner guides. | Agent | Open |
| Old lifecycle terms appear in live guidance after implementation. | Decide whether the occurrence is historical context or stale guidance; update live guidance or record the intentional exception. | Agent | Open |
| Documentation-only scope expands into runtime behavior or API contract changes. | Stop and create or revise a behavior/spec plan before touching code or contract artifacts. | Agent/User | Open |

## Edge Cases And Failure Modes
- Treating ADRs, PRDs, or standalone specs as mandatory for small maintenance changes.
- Updating phase names in planning guidance before the lifecycle owner guide changes, leaving inconsistent valid values.
- Renaming historical plan or changelog text and creating unnecessary churn.
- Leaving `WORKING_WITH_AI.md` pointing at stale lifecycle sources or nonexistent spec files.
- Keeping both old and new roadmap intake vocabulary as live guidance.
- Adding templates that duplicate standing rules instead of linking to owner guidance.

## Validation Plan
- During the current interactive documentation session:
  - manual review of ADR status, plan readiness, roadmap status, and affected live guidance
  - targeted text search for old lifecycle terms in live guidance after implementation
  - manual reference-document review for every edited `.agents/references/*.md` file against `.agents/references/references-rules.md`
  - manual documentation/process review using `.agents/references/reviews.md`
- Skipped unless the user re-enables normal validation:
  - tests
  - builds
  - generated documentation checks
  - linters and formatters

## Testing Strategy
- Unit tests: not applicable.
- Integration tests: not applicable.
- Contract tests: not applicable because runtime behavior and public API contract should not change.
- Smoke/benchmark tests: not applicable.
- Negative verification: manual search and readback should confirm no live owner guide treats optional pre-planning artifacts as mandatory ceremony and no current guidance uses old lifecycle names unintentionally.

## Better Engineering Notes
- Keep the execution change documentation-only unless a future accepted spec introduces runtime behavior.
- Prefer concise templates with examples of when to skip the artifact.
- The roadmap should track active plan state; detailed task progress and validation history belong in this plan.
- If the user wants separate plans later, split after Q1 is answered: one plan for artifact templates/routing and one plan for lifecycle vocabulary migration.

## Validation Results
| Date | Command | Scope | Result | Notes |
| --- | --- | --- | --- | --- |
| 2026-05-10 | Manual planning review | New plan structure, ADR dependency, task sequencing, and roadmap alignment intent | Passed | Initial pre-acceptance review kept the plan `Needs Input` because ADR 0001 and ADR 0002 were still `Proposed`. |
| 2026-05-10 | Manual readiness readback | ADR acceptance, Q1 resolution, plan readiness, and roadmap status | Passed | ADR 0001 and ADR 0002 are accepted by explicit user instruction; plan is ready and implementation started. |
| 2026-05-10 | Not run | Builds, tests, linters, formatters, and generated documentation checks | Skipped | Normal verification remains skipped for the current interactive documentation session. |
| 2026-05-10 | Manual template and routing review | ADR, PRD, and spec templates plus `.agents/references/documentation.md` routing | Passed | Templates are concise, include skip guidance, and keep ADRs, PRDs, and standalone specs optional unless they resolve real ambiguity. |
| 2026-05-10 | `git diff --check` | Documentation-only task 2 diff | Passed | No whitespace errors. |
| 2026-05-10 | Manual lifecycle reference review | `.agents/references/application-lifecycle.md` against ADR 0002 and `.agents/references/references-rules.md` | Passed | Lifecycle owner now has twelve accepted phases, updated activities, loops, triggers, artifact roles, gaps, non-goals, and maintenance wording. |
| 2026-05-10 | Targeted lifecycle vocabulary search | `.agents/references/application-lifecycle.md` live phase tables and activity headings | Passed | Previous phase labels appear only in the migration map. |
| 2026-05-10 | `git diff --check` | Documentation-only task 3 diff | Passed | No whitespace errors. |

## User Validation
- After implementation, review the updated lifecycle and pre-planning guidance for terminology fit and workflow overhead.
- Confirm that ADR, PRD, spec, and plan roles are understandable and remain optional where ADR 0001 says they should be optional.
