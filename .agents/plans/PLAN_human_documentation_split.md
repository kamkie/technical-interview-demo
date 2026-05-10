# Plan: Human Documentation Split

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Planning |
| Status | In Progress |

## Planning Readiness
| Field | Value |
| --- | --- |
| Decision Complete | Yes |
| Blocking Open Questions | None |
| Accepted Fallbacks | Operations runbooks moved to `docs/OPERATIONS.md`; root `WORKING_WITH_AI.md` remains a compatibility pointer |
| Ready For Execution | Yes |
| Last Updated | 2026-05-10 |

## Linked Pre-Planning Artifacts
| Artifact | Path | Role | Status |
| --- | --- | --- | --- |
| ADR | `docs/decisions/0006-split-human-documentation-and-ai-workflow-guides.md` | Durable documentation ownership and split decision | Accepted |
| PRD | None | Product intent is not changing | None |
| Spec | None | The ADR and existing documentation ownership rules define the documentation behavior | None |

## Summary
- Split human-facing documentation into a navigable `docs/` tree while keeping `README.md` concise.
- Move AI collaboration guidance to `docs/WORKING_WITH_AI.md`, keep root compatibility, and move deployment/runbook content out of `SETUP.md`.
- Success is measured by the ADR confirmation checklist, passing documentation audit, and aligned roadmap/AI ownership maps.

## Scope
- In scope: `docs/README.md`, `docs/DEVELOPMENT_LIFECYCLE.md`, `docs/WORKING_WITH_AI.md`, `docs/OPERATIONS.md`, root `WORKING_WITH_AI.md` compatibility, `SETUP.md`, `CONTRIBUTING.md`, `README.md`, `docs/DESIGN.md`, `AGENTS.md`, `.agents/references/documentation.md`, `ROADMAP.md`, and this plan.
- In scope: Markdown link migration for moved files and ownership references.
- In scope: generated-or-derived notices for human-only summaries that derive from owner artifacts.
- Out of scope: application behavior, public API contracts, release preparation, deployment execution, and changelog updates.

## Current State
- `docs/decisions/0006-split-human-documentation-and-ai-workflow-guides.md` is accepted and now records `docs/OPERATIONS.md` as the selected operations path.
- `SETUP.md` is focused on local setup, local commands, CI reproduction, and local troubleshooting.
- `docs/OPERATIONS.md` owns deployment contract, release-artifact verification, image build and smoke, post-deploy smoke, runtime expectations, upgrade and rollback, Kubernetes, Helm, monitoring, OAuth runtime setup, and operations troubleshooting.
- `docs/WORKING_WITH_AI.md` owns the human-facing AI guide; root `WORKING_WITH_AI.md` remains a compatibility pointer.
- `docs/README.md` and `docs/DEVELOPMENT_LIFECYCLE.md` exist and link the human-facing documentation tree.
- `ROADMAP.md` tracks this plan as in-progress implementation work.

## Requirement Gaps And Open Questions
| ID | Question / Gap | Why It Matters | Owner | Status | Fallback / Decision | Blocks Ready? |
| --- | --- | --- | --- | --- | --- | --- |
| Q1 | Should operations content live at repository root as `OPERATIONS.md` or under `docs/OPERATIONS.md`? | The selected path must be used consistently by human docs, AI ownership maps, and link migration. | Agent | Answered | Use `docs/OPERATIONS.md` so the human documentation index owns design, lifecycle, AI collaboration, operations, and frontend contract material in one tree. | No |
| Q2 | Should root `WORKING_WITH_AI.md` remain after the move? | Existing links and readers may expect the root path during migration. | Agent | Answered | Keep root `WORKING_WITH_AI.md` as a short compatibility pointer to `docs/WORKING_WITH_AI.md`. | No |

## Decision Log And Assumptions
| ID | Decision / Assumption | Source | Date | Revisit Trigger |
| --- | --- | --- | --- | --- |
| D1 | Operations runbooks move to `docs/OPERATIONS.md`, not root `OPERATIONS.md`. | ADR 0006 accepted fallback decision | 2026-05-10 | If maintainers later want operations outside the docs tree for packaging or release distribution. |
| D2 | Root `WORKING_WITH_AI.md` remains as a short compatibility pointer. | ADR 0006 migration guidance | 2026-05-10 | After downstream links no longer target the root path. |
| D3 | `docs/DEVELOPMENT_LIFECYCLE.md` is human-facing and summary-level; `.agents/references/application-lifecycle.md` remains the detailed AI owner. | ADR 0006 and `.agents/references/documentation.md` | 2026-05-10 | If lifecycle rules change rather than only the human summary. |
| D4 | Execute in `M0: direct` mode. | `.agents/references/workflow.md` and no user request for delegation | 2026-05-10 | If the user explicitly asks for parallel agents or the scope expands into disjoint code/doc streams. |
| D5 | No release, deployment, public API, or changelog work is included. | User request and ADR scope | 2026-05-10 | If the user requests release preparation after integration. |

## Execution Shape And Shared Files
- Recommended shape: `M0: direct`.
- The work is documentation-only but touches shared routing files (`README.md`, `CONTRIBUTING.md`, `AGENTS.md`, `.agents/references/documentation.md`, `ROADMAP.md`, and this plan), so a single local agent should keep ordering and link migration coherent.
- Coordinator-owned shared files if delegation is later requested: `ROADMAP.md`, this plan, `README.md`, `CONTRIBUTING.md`, `AGENTS.md`, `.agents/references/documentation.md`, and root compatibility pointers.
- Candidate worker boundaries if later delegation becomes necessary: one worker could split `SETUP.md` and `docs/OPERATIONS.md`; another could draft `docs/README.md`, `docs/DEVELOPMENT_LIFECYCLE.md`, and `docs/WORKING_WITH_AI.md`; the coordinator would still own link migration, roadmap, validation, and final review.

## Affected Artifacts
- Pre-planning artifacts: `docs/decisions/0006-split-human-documentation-and-ai-workflow-guides.md` remains the governing ADR.
- Docs: `README.md`, `SETUP.md`, `CONTRIBUTING.md`, `WORKING_WITH_AI.md`, `docs/README.md`, `docs/DEVELOPMENT_LIFECYCLE.md`, `docs/WORKING_WITH_AI.md`, `docs/OPERATIONS.md`, `docs/DESIGN.md`, and relevant docs links.
- AI guidance: `AGENTS.md` and `.agents/references/documentation.md`.
- Roadmap and plan tracking: `ROADMAP.md` and `.agents/plans/PLAN_human_documentation_split.md`.
- Tests and source files: none expected.
- Validation: `pwsh ./scripts/docs/audit-docs.ps1` and `./build.ps1 build`.

## Progress Tracker
| Task | Status | Owner | Commit | Validation | Notes |
| --- | --- | --- | --- | --- | --- |
| 1: Documentation Spine And AI Guide Move | Done | Agent | `docs(ai): move human AI guide into docs` | `pwsh ./scripts/docs/audit-docs.ps1` passed | Created the human docs index, lifecycle summary, moved AI collaboration guide, and root compatibility pointer. |
| 2: Operations And Setup Split | Done | Agent | `docs(operations): split runbooks from setup` | `pwsh ./scripts/docs/audit-docs.ps1` passed | Moved deployment/runtime runbooks into `docs/OPERATIONS.md` and narrowed `SETUP.md` to local setup and local troubleshooting. |
| 3: Ownership Map And Link Alignment | Done | Agent | `docs: align human documentation ownership map` | `pwsh ./scripts/docs/audit-docs.ps1` and `git diff --check` passed | Aligned top-level docs, AI ownership maps, ADR links, roadmap state, and operations/AI guide references. |
| 4: Final Validation And Review | Not Started | Agent | Pending | Pending | Run docs audit/build, review drift, and close plan tracking for implementation readiness or completion. |

## Execution Tasks
### Task 1: Documentation Spine And AI Guide Move
| Field | Value |
| --- | --- |
| Status | Done |
| Goal | Establish the human-facing `docs/` entry points required by ADR 0006. |
| Owned Files Or Packages | `docs/README.md`, `docs/DEVELOPMENT_LIFECYCLE.md`, `docs/WORKING_WITH_AI.md`, `WORKING_WITH_AI.md` |
| Coordinator-Owned Shared Files | `README.md`, `CONTRIBUTING.md`, `ROADMAP.md`, this plan |
| Context Required | `AGENTS.md`, `.agents/references/execution.md`, `.agents/references/code-style.md`, `.agents/references/documentation.md`, `.agents/references/testing.md`, this plan, and `docs/decisions/0006-split-human-documentation-and-ai-workflow-guides.md` |
| Behavior To Preserve | Keep human-facing guidance concise; do not copy detailed AI runbooks from `.agents/references/*`. |
| Deliverables | Human docs index, lifecycle summary with derived notice, moved AI collaboration guide, and root compatibility pointer. |
| Validation Checkpoint | `pwsh ./scripts/docs/audit-docs.ps1` or targeted link review if the full split is still mid-migration. |
| Commit Checkpoint | Commit the completed docs spine and AI guide move before starting unrelated implementation work. |

### Task 2: Operations And Setup Split
| Field | Value |
| --- | --- |
| Status | Done |
| Goal | Make `SETUP.md` a local setup guide and move deployment/runtime material into `docs/OPERATIONS.md`. |
| Owned Files Or Packages | `SETUP.md`, `docs/OPERATIONS.md` |
| Coordinator-Owned Shared Files | `README.md`, `CONTRIBUTING.md`, `ROADMAP.md`, this plan |
| Context Required | `AGENTS.md`, `.agents/references/execution.md`, `.agents/references/code-style.md`, `.agents/references/documentation.md`, `.agents/references/testing.md`, this plan, `docs/decisions/0005-adopt-operations-and-deployment-owner-guide.md`, and `docs/decisions/0006-split-human-documentation-and-ai-workflow-guides.md` |
| Behavior To Preserve | Preserve existing deployment, smoke, rollback, Kubernetes, Helm, monitoring, OAuth, and troubleshooting instructions while moving ownership. |
| Deliverables | `docs/OPERATIONS.md` with operations runbooks, a narrowed `SETUP.md`, and short pointers where setup readers need migration help. |
| Validation Checkpoint | `pwsh ./scripts/docs/audit-docs.ps1` or targeted link review if cross-doc alignment is still pending. |
| Commit Checkpoint | Commit the operations/setup split before final ownership-map alignment. |

### Task 3: Ownership Map And Link Alignment
| Field | Value |
| --- | --- |
| Status | Done |
| Goal | Make human-facing docs and AI ownership maps agree on the new document roles. |
| Owned Files Or Packages | `README.md`, `CONTRIBUTING.md`, `docs/DESIGN.md`, `AGENTS.md`, `.agents/references/documentation.md`, `docs/decisions/0006-split-human-documentation-and-ai-workflow-guides.md`, `ROADMAP.md`, this plan |
| Coordinator-Owned Shared Files | All owned files are shared because execution is `M0: direct`. |
| Context Required | `AGENTS.md`, `.agents/references/execution.md`, `.agents/references/code-style.md`, `.agents/references/documentation.md`, `.agents/references/references-rules.md`, `.agents/references/testing.md`, this plan, and ADR 0006 |
| Behavior To Preserve | Keep setup detail out of AI workflow and release guides; keep `README.md` concise and link-oriented. |
| Deliverables | Updated incoming links, ownership maps, ADR link targets, roadmap implementation path, and plan progress entries. |
| Validation Checkpoint | `pwsh ./scripts/docs/audit-docs.ps1`. |
| Commit Checkpoint | Commit ownership-map/link alignment before final validation closeout. |

### Task 4: Final Validation And Review
| Field | Value |
| --- | --- |
| Status | Not Started |
| Goal | Prove the documentation split is internally consistent and record completion evidence. |
| Owned Files Or Packages | `.agents/plans/PLAN_human_documentation_split.md`, `ROADMAP.md` |
| Coordinator-Owned Shared Files | All changed files for final review. |
| Context Required | `AGENTS.md`, `.agents/references/execution.md`, `.agents/references/testing.md`, `.agents/references/reviews.md`, this plan |
| Behavior To Preserve | Do not introduce release work or code changes. |
| Deliverables | Passing validation evidence, completed plan progress, roadmap state, and final review notes. |
| Validation Checkpoint | `pwsh ./scripts/docs/audit-docs.ps1` and `./build.ps1 build`. |
| Commit Checkpoint | Commit final tracking updates if validation or review changes tracked files. |

## Blockers And Replan Triggers
| Trigger / Blocker | Response | Owner | Status |
| --- | --- | --- | --- |
| Documentation audit exposes stale links outside the planned file set. | Update the owning link if it directly references moved files; otherwise record the blocker and replan. | Agent | Open |
| `SETUP.md` contains operational content that cannot be moved without changing command semantics. | Preserve the content in place with a pointer, record the exception, and replan before broader restructuring. | Agent | Open |
| `.agents/references/documentation.md` needs a standing rule change beyond artifact ownership. | Load `.agents/references/references-rules.md`, update the focused rule owner, and keep reference docs compliant. | Agent | Open |
| User requests release preparation or deployment execution. | Stop this plan scope and route through release or operations guidance. | User | Open |

## Edge Cases And Failure Modes
- Root links to `WORKING_WITH_AI.md` may remain in historical ADRs; update current navigation links and only change historical records where the ADR confirmation checklist requires current targets.
- Moving troubleshooting content can break anchors; keep section headings stable where practical and rely on documentation audit for local link proof.
- `docs/DEVELOPMENT_LIFECYCLE.md` can drift from `.agents/references/application-lifecycle.md`; include a derived notice and keep the summary brief.
- `SETUP.md` still needs local troubleshooting; do not move local Java, Docker, Testcontainers, formatter, or port-conflict guidance into operations.

## Validation Plan
- Run `pwsh ./scripts/docs/audit-docs.ps1` after Markdown moves and final link alignment.
- Run `./build.ps1 build` for the repository standard validation; for documentation-only diffs, record whether the wrapper takes the lightweight-only path.
- Manually review the ADR 0006 confirmation checklist against changed files.
- Use `rg` to confirm no current navigation or ownership references still target root `WORKING_WITH_AI.md` except the compatibility pointer and historical context where intentionally preserved.

## Verification Strategy
- Unit tests: not applicable; no code behavior changes.
- Integration tests: not applicable; no public API behavior changes.
- Contract tests: not applicable; no OpenAPI or REST Docs contract change expected.
- Documentation checks: documentation audit plus manual confirmation checklist review.
- Smoke or benchmark tests: skipped unless a later implementation unexpectedly changes executable code or deployment assets.

## Better Engineering Notes
- Keep operations at `docs/OPERATIONS.md` to avoid another root-level human runbook while preserving `README.md`, `SETUP.md`, and `CONTRIBUTING.md` as root entry points.
- Do not add a second human-facing history file; released history stays in `CHANGELOG.md` and active work stays in `ROADMAP.md` plus this plan.
- If the implementation becomes too large for one reviewable pass, complete tasks in separate commits and update the progress tracker after each checkpoint.

## Validation Results
| Date | Command | Scope | Result | Notes |
| --- | --- | --- | --- | --- |
| 2026-05-10 | `pwsh ./scripts/docs/audit-docs.ps1` | Plan creation and roadmap selection checkpoint | Passed | Audited 33 user-facing documents, checked 56 local links, and passed. |
| 2026-05-10 | `./build.ps1 build` | Plan creation and roadmap selection checkpoint | Passed | Wrapper reported only lightweight uncommitted files changed and skipped Gradle; manual consistency review is sufficient for this checkpoint. |
| 2026-05-10 | `pwsh ./scripts/docs/audit-docs.ps1` | Task 1 documentation spine and AI guide move | Passed | Audited 33 user-facing documents, checked 79 local links, and passed. |
| 2026-05-10 | `pwsh ./scripts/docs/audit-docs.ps1` | Task 2 operations and setup split | Passed | Audited 36 user-facing documents, checked 141 local links, and passed. |
| 2026-05-10 | `pwsh ./scripts/docs/audit-docs.ps1` | Task 3 ownership map and link alignment | Passed | Audited 37 user-facing documents, checked 188 local links, and passed. |
| 2026-05-10 | `git diff --check` | Task 3 ownership map and reference-doc consistency review | Passed | No whitespace errors; manual review checked changed `.agents/references/*.md` files against `references-rules.md` owner statements and current-rule style. |
| 2026-05-10 | Pending | Documentation audit and wrapper build for implementation tasks | Pending | Run after implementation tasks complete. |

## User Validation
- Open `README.md` and `docs/README.md`; confirm the project overview and documentation index point to setup, contributing, lifecycle, AI collaboration, operations, design, roadmap, ADRs, PRDs, specs, and frontend contract material.
- Open `SETUP.md`; confirm it reads as a local setup and local troubleshooting guide.
- Open `docs/OPERATIONS.md`; confirm deployment, smoke, runtime, rollback, Kubernetes, Helm, monitoring, OAuth, and operations troubleshooting content is easy to find.
