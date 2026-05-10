# Plan: Multi-Agent Roles, Skill Catalog, And Workflow Mode Rename

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Planning |
| Status | Needs Input |

## Planning Readiness
| Field | Value |
| --- | --- |
| Decision Complete | No |
| Blocking Open Questions | Q1, Q3 |
| Accepted Fallbacks | None |
| Ready For Execution | No |
| Last Updated | 2026-05-10 |

## Linked Pre-Planning Artifacts
| Artifact | Path | Role | Status |
| --- | --- | --- | --- |
| ADR | `docs/decisions/0004-adopt-skill-first-multi-agent-workflow.md` | Principle ADR: orchestrator-led skill-first operating model | Proposed |
| ADR | `docs/decisions/0003-adopt-multi-agent-roles-and-skill-catalog.md` | Implementation ADR: six-role roster, starter skill catalog, durable state | Proposed |
| PRD | None | Not user-facing | None |
| Spec | None | AI workflow architecture, no runtime contract change | None |

## Summary
- Rename the readable workflow-mode labels to `M0: direct`, `M1: assisted`, `M2: delegated`, `M3: parallel`, and `M4: gated` while keeping the `M0` through `M4` identifiers and existing mode semantics.
- Decide whether ADR 0004 becomes the principle ADR and ADR 0003 becomes its implementation input, then roll out the accepted role roster, a minimal skill catalog, and durable `.agents/context/*` state in phases.
- Success: `workflow.md` is the single owner of the new mode names and roster, the Phase B skills exist and are usable, and one bounded task has been smoke-tested through the orchestrator/worker/reviewer/verifier loop.

## Scope
- In scope:
  - Rename the `M0` through `M4` mode labels in `.agents/references/workflow.md` (primary owner) and downstream guidance while keeping the numeric identifiers.
  - Reconcile ADR 0004 role roster onto ADR 0003's six-role roster (Coordinator, Planner, Worker, Reviewer, Verifier, Specialist).
  - Update both ADRs to use new mode labels and cross-link as principle + implementation after the user decides how to relate them.
  - Materialize `.agents/context/{handoffs,workers,reviews,verifications,specialists}/` with stub `README.md`.
  - Add a per-role read-set table to `workflow.md`.
  - Add Phase B skill bundles: `select-mode-and-skills`, `handoff-pack`, `repo-task-execute`, `run-validation`, `diff-review`.
  - Add Phase C skill bundles and platform alignment in `.junie/AGENTS.md`, `AGENTS.md`, `WORKING_WITH_AI.md`.
  - One smoke-test of the model on a bounded existing task; capture lessons in `LEARNINGS.md`.
- Out of scope:
  - Removing or deprecating the stable `M0` through `M4` identifiers.
  - Moving ADR 0003 or ADR 0004 to `Accepted` without an explicit user decision.
  - `release-cut` skill and Release Agent identity (Definition of Done: leave release work undone unless requested).
  - Application code, public APIs, runtime behavior.
  - Rewriting archived plans.
  - Introducing additional ADRs beyond updates to 0003/0004.

## Current State
- Phase: Planning. Both ADRs are `Proposed`, dated 2026-05-10, and present each other as competing.
- `.agents/references/workflow.md` defines `M0: solo`, `M1: sidecar-readonly`, `M2: bounded-worker`, `M3: parallel-sliced`, `M4: full-sidecar` and references `.agents/context/*` directories that do not exist on disk.
- `.agents/skills/` contains only `gh-fix-ci/` and `gh-fix-security-quality/`.
- ADR 0004 introduces role names (`Orchestrator`, `Explorer`, `Documentation Agent`, `Release Agent`) not present in `workflow.md`, creating vocabulary drift.
- No per-role read-set table exists; agents load the union of references.

## Requirement Gaps And Open Questions
| ID | Question / Gap | Why It Matters | Owner | Status | Fallback / Decision | Blocks Ready? |
| --- | --- | --- | --- | --- | --- | --- |
| Q1 | Confirm role roster: keep ADR 0003's six identities and fold ADR 0004's extras under Specialist? | Determines vocabulary in `workflow.md`, both ADRs, skills, and `.junie/AGENTS.md`. | User | Open | Adopt the six-role roster; `Explorer`/`Documentation Agent`/`Release Agent` become Specialist variants. | Yes |
| Q2 | Confirm mode labels: `M0: direct`, `M1: assisted`, `M2: delegated`, `M3: parallel`, `M4: gated`? | All downstream renames depend on the chosen labels. | User | Answered | Use the listed labels; keep `M0` through `M4` as stable identifiers. | No |
| Q3 | Should ADR 0004 remain a separate principle ADR or be merged into 0003? | Decides whether we ship two ADRs or consolidate. | User | Open | Keep both; 0004 = principle, 0003 = implementation, with `Refines`/`Implemented by` cross-links. | Yes |

## Decision Log And Assumptions
| ID | Decision / Assumption | Source | Date | Revisit Trigger |
| --- | --- | --- | --- | --- |
| D1 | `workflow.md` is the standing owner of mode and role vocabulary; ADRs and skills must align to it. | `AI Guidance Maintenance` rule, `references-rules.md` | 2026-05-10 | If a future ADR is accepted that moves ownership. |
| D2 | Mode rename is a label change only; `M0` through `M4` identifiers, mode definitions, ordering, and integration rules are unchanged. | User request and ADR 0004 revision. | 2026-05-10 | If a mode's semantics actually change. |
| D3 | `release-cut` and the Release Agent identity are deferred until release work is explicitly requested. | Definition Of Done in repository AI guidelines. | 2026-05-10 | When release work is requested. |
| D4 | Phase B skill set (`select-mode-and-skills`, `handoff-pack`, `repo-task-execute`, `run-validation`, `diff-review`) covers the orchestrator → worker → reviewer → verifier loop end-to-end. | ADR 0003 starter catalog, prioritized. | 2026-05-10 | If smoke-test reveals an unmet gap. |
| D5 | Use `M0: direct`, `M1: assisted`, `M2: delegated`, `M3: parallel`, and `M4: gated`; do not use `M4: agentic`. | User discussion on 2026-05-10. | 2026-05-10 | If the user chooses a different label set before execution. |

## Execution Shape And Shared Files
- Recommended shape: `M0: solo` under the current vocabulary for planning edits; target label after implementation is `M0: direct`.
- Why: this plan is documentation- and configuration-only with disjoint phase boundaries; it does not benefit from `parallel` because phases must land in order (rename → state dirs → skills → sweep → smoke-test).
- Coordinator-owned shared files: `.agents/references/workflow.md`, `docs/decisions/0003-adopt-multi-agent-roles-and-skill-catalog.md`, `docs/decisions/0004-adopt-skill-first-multi-agent-workflow.md`, `AGENTS.md`, `.junie/AGENTS.md`, `WORKING_WITH_AI.md`, `ROADMAP.md`, this plan.
- Candidate worker boundaries if later split: one worker per Phase B skill bundle in Phase B Task 5; one worker per Phase C skill bundle in Phase C Task 7.

## Affected Artifacts
- Pre-planning: `docs/decisions/0003-*.md`, `docs/decisions/0004-*.md`.
- Tests: none (documentation-only); no executable spec changes.
- Docs: `AGENTS.md`, `.junie/AGENTS.md`, `WORKING_WITH_AI.md`, `README.md` (only if it references `M0`–`M4`).
- OpenAPI: not affected.
- HTTP examples: not affected.
- Source files: not affected.
- Owning AI guides: `.agents/references/workflow.md` (modes, roster, read-set), `.agents/references/documentation.md` (skill ownership routing), `.agents/references/references-rules.md` (skills are tactics, not governance).
- New files: `.agents/context/{handoffs,workers,reviews,verifications,specialists}/README.md`; `.agents/skills/{select-mode-and-skills,handoff-pack,repo-task-execute,run-validation,diff-review}/SKILL.md` (Phase B); Phase C skills.
- Build/benchmark: not affected.
- ROADMAP: `ROADMAP.md` tracks Phases A–C.

## Progress Tracker
| Task | Status | Owner | Commit | Validation | Notes |
| --- | --- | --- | --- | --- | --- |
| 1: Resolve Q1–Q3 with user | Not Started | Coordinator | Pending | Pending | Blocks all execution. |
| 2: Reconcile ADR 0004 roster + ADR cross-links | Not Started | Worker | Pending | Pending | Phase 0. |
| 3: Rename mode labels in `workflow.md` and update both ADRs | Not Started | Worker | Pending | Pending | Phase A.1. |
| 4: Materialize `.agents/context/*` and add per-role read-set table | Not Started | Worker | Pending | Pending | Phase A.2. |
| 5: Record user acceptance decision for ADR 0004 and ADR 0003 | Not Started | Coordinator | Pending | Pending | Gate before skills. |
| 6: Add Phase B skill bundles | Not Started | Worker | Pending | Pending | Phase B. |
| 7: Smoke-test the loop on one bounded task | Not Started | Coordinator + Worker + Reviewer + Verifier | Pending | Pending | Captures `LEARNINGS.md` entry. |
| 8: Add Phase C skill bundles and platform alignment | Not Started | Worker | Pending | Pending | Phase C. |
| 9: Sweep old mode labels from live guidance | Not Started | Worker | Pending | Pending | After Phase B smoke-test passes. |
| 10: Update `ROADMAP.md` final state | Not Started | Coordinator | Pending | Pending | Closes the plan. |

## Execution Tasks

### Task 1: Resolve Q1–Q3 With User
| Field | Value |
| --- | --- |
| Status | Not Started |
| Goal | Capture explicit user decisions on roster and ADR consolidation; record in Decision Log. |
| Owned Files Or Packages | This plan only. |
| Coordinator-Owned Shared Files | This plan. |
| Context Required | This plan, ADR 0003, ADR 0004, `.agents/references/workflow.md`. |
| Behavior To Preserve | None. |
| Deliverables | Decision Log entries covering Q1 and Q3; Planning Readiness flipped to `Decision Complete: Yes`, `Ready For Execution: Yes`. |
| Validation Checkpoint | User confirmation in chat or commit message. |
| Commit Checkpoint | One commit updating this plan. |

### Task 2: Reconcile ADR 0004 Roster And Cross-Links
| Field | Value |
| --- | --- |
| Status | Not Started |
| Goal | Edit ADR 0004 so its role roster matches ADR 0003's six identities; add `Refines`/`Implemented by` cross-links between 0003 and 0004; drop "competing" framing. |
| Owned Files Or Packages | `docs/decisions/0003-adopt-multi-agent-roles-and-skill-catalog.md`, `docs/decisions/0004-adopt-skill-first-multi-agent-workflow.md`. |
| Coordinator-Owned Shared Files | None beyond owned files. |
| Context Required | Both ADRs, `.agents/references/workflow.md`. |
| Behavior To Preserve | ADR decision content and consequences; only roster vocabulary and cross-links change. |
| Deliverables | Updated ADRs with reconciled roster and explicit cross-links. |
| Validation Checkpoint | Manual diff review confirming no role outside the six-role roster remains as a top-level identity. |
| Commit Checkpoint | One commit. |

### Task 3: Rename Mode Labels In `workflow.md` And Update Both ADRs
| Field | Value |
| --- | --- |
| Status | Not Started |
| Goal | Rename mode labels in `workflow.md` to `M0: direct`, `M1: assisted`, `M2: delegated`, `M3: parallel`, and `M4: gated`; update both ADRs to use the new labels. |
| Owned Files Or Packages | `.agents/references/workflow.md`, both ADRs. |
| Coordinator-Owned Shared Files | `.agents/references/workflow.md`. |
| Context Required | `.agents/references/workflow.md`, both ADRs. |
| Behavior To Preserve | Mode identifiers, definitions, ordering, integration rules, escalation defaults. |
| Deliverables | Renamed mode labels; explicit note that the rename is vocabulary-only; both ADRs updated. |
| Validation Checkpoint | Manual review: `solo`, `sidecar-readonly`, `bounded-worker`, `parallel-sliced`, and `full-sidecar` no longer appear as primary labels in live guidance. |
| Commit Checkpoint | One commit. |

### Task 4: Materialize `.agents/context/*` And Add Per-Role Read-Set Table
| Field | Value |
| --- | --- |
| Status | Not Started |
| Goal | Create the five state directories with stub `README.md` files describing role-to-directory ownership; add the per-role read-set table from ADR 0003 to `workflow.md`. |
| Owned Files Or Packages | `.agents/context/handoffs/README.md`, `.agents/context/workers/README.md`, `.agents/context/reviews/README.md`, `.agents/context/verifications/README.md`, `.agents/context/specialists/README.md`, `.agents/references/workflow.md`. |
| Coordinator-Owned Shared Files | `.agents/references/workflow.md`. |
| Context Required | ADR 0003, `.agents/references/workflow.md`. |
| Behavior To Preserve | Existing `workflow.md` rules. |
| Deliverables | Five directories with `README.md`; read-set table merged into `workflow.md`. |
| Validation Checkpoint | `Get-ChildItem .agents\context -Recurse` lists all five `README.md` files; manual review of read-set table. |
| Commit Checkpoint | One commit. |

### Task 5: Record User Acceptance Decision For ADR 0004 And ADR 0003
| Field | Value |
| --- | --- |
| Status | Not Started |
| Goal | Record the explicit user decision: accept ADR 0004, accept ADR 0003, merge one into the other, or leave either proposed. |
| Owned Files Or Packages | Both ADRs. |
| Coordinator-Owned Shared Files | None. |
| Context Required | Both ADRs after Tasks 2–4. |
| Behavior To Preserve | ADR content; only status changes. |
| Deliverables | ADR status and relationship updated according to the user decision. |
| Validation Checkpoint | Spec Priority compliance: explicit user decision recorded before any ADR is marked `Accepted`. |
| Commit Checkpoint | One commit. |

### Task 6: Add Phase B Skill Bundles
| Field | Value |
| --- | --- |
| Status | Not Started |
| Goal | Add `select-mode-and-skills`, `handoff-pack`, `repo-task-execute`, `run-validation`, `diff-review` under `.agents/skills/`, each in `SKILL.md` shape with frontmatter, declared `Read Set`, `Inputs`, `Workflow`, and stop conditions. Each must reference the new mode names and reference repository rules instead of inlining them. |
| Owned Files Or Packages | `.agents/skills/<skill>/SKILL.md` for each of the five skills (plus optional scripts). |
| Coordinator-Owned Shared Files | None. |
| Context Required | `.agents/skills/gh-fix-ci/SKILL.md` (shape reference), `.agents/references/workflow.md`, `.agents/references/execution.md`, `.agents/references/reviews.md`, `.agents/references/testing.md`. |
| Behavior To Preserve | Existing `gh-fix-ci` and `gh-fix-security-quality` bundles. |
| Deliverables | Five new `SKILL.md` files. |
| Validation Checkpoint | Manual check that each `SKILL.md` declares a `Read Set`, references rules without duplicating them, and uses the new mode names. |
| Commit Checkpoint | One commit per skill or one batched commit. |

### Task 7: Smoke-Test The Loop On One Bounded Task
| Field | Value |
| --- | --- |
| Status | Not Started |
| Goal | Pick one small, already-scoped change (documentation or a bounded code slice) and run it end-to-end through Coordinator → Worker → Reviewer → Verifier using only Phase B skills; capture lessons in `.agents/references/LEARNINGS.md`. |
| Owned Files Or Packages | The chosen smoke-test change's files; `.agents/context/*`; `.agents/references/LEARNINGS.md`. |
| Coordinator-Owned Shared Files | `.agents/references/LEARNINGS.md`. |
| Context Required | Phase B skills, `.agents/references/workflow.md`, target change spec or issue. |
| Behavior To Preserve | The smoke-test change itself, per its own scope. |
| Deliverables | One handoff packet, one worker output, one reviewer note, one verifier result, one `LEARNINGS.md` entry. |
| Validation Checkpoint | All four artifacts present in `.agents/context/*`; `LEARNINGS.md` records at least one durable lesson or "no gaps found". |
| Commit Checkpoint | One commit for the smoke-test change plus its `.agents/context/*` artifacts. |

### Task 8: Add Phase C Skill Bundles And Platform Alignment
| Field | Value |
| --- | --- |
| Status | Not Started |
| Goal | Add `repo-plan-author`, `integrate-branch`, `security-review`, `openapi-contract-check`, `triage-flaky-test` skill bundles; add the mode→role mapping section to `.junie/AGENTS.md`; add cross-references in `AGENTS.md` and `WORKING_WITH_AI.md`. Defer `release-cut`. |
| Owned Files Or Packages | Five new skills under `.agents/skills/`; `.junie/AGENTS.md`; `AGENTS.md`; `WORKING_WITH_AI.md`. |
| Coordinator-Owned Shared Files | `AGENTS.md`, `.junie/AGENTS.md`, `WORKING_WITH_AI.md`. |
| Context Required | Phase B skills, `.agents/references/workflow.md`, `.agents/references/documentation.md`. |
| Behavior To Preserve | Existing developer-facing guidance; do not duplicate `workflow.md` content. |
| Deliverables | Five new skills; updated three top-level docs. |
| Validation Checkpoint | Manual review confirming cross-references do not duplicate content and use the new vocabulary. |
| Commit Checkpoint | One commit per logical group. |

### Task 9: Sweep Old Mode Labels From Live Guidance
| Field | Value |
| --- | --- |
| Status | Not Started |
| Goal | Replace remaining old labels (`solo`, `sidecar-readonly`, `bounded-worker`, `parallel-sliced`, `full-sidecar`) across `.agents/references/*.md`, `.agents/skills/*/SKILL.md`, `AGENTS.md`, `.junie/AGENTS.md`, `WORKING_WITH_AI.md`, and active `.agents/plans/PLAN_*.md` when those terms refer to workflow-mode labels. |
| Owned Files Or Packages | All listed paths. |
| Coordinator-Owned Shared Files | `.agents/references/workflow.md`. |
| Context Required | Output of `search_project "M0"`/`"M1"`/`"M2"`/`"M3"`/`"M4"` scoped to documentation paths. |
| Behavior To Preserve | Mode semantics; only labels change. |
| Deliverables | No primary old-label references remain in live guidance; `M0` through `M4` identifiers remain. |
| Validation Checkpoint | Targeted search for each old label returns zero primary-label hits in the swept paths. |
| Commit Checkpoint | One commit. |

### Task 10: Update `ROADMAP.md` Final State
| Field | Value |
| --- | --- |
| Status | Not Started |
| Goal | Record the rollout outcome in `ROADMAP.md` and archive this plan per `.agents/references/plan-execution.md`. |
| Owned Files Or Packages | `ROADMAP.md`, this plan. |
| Coordinator-Owned Shared Files | `ROADMAP.md`. |
| Context Required | This plan, `ROADMAP.md`. |
| Behavior To Preserve | Existing roadmap structure. |
| Deliverables | Roadmap entry updated; plan moved to `.agents/archive/` if guidance requires. |
| Validation Checkpoint | Manual review. |
| Commit Checkpoint | One commit. |

## Blockers And Replan Triggers
| Trigger / Blocker | Response | Owner | Status |
| --- | --- | --- | --- |
| Q1 or Q3 unanswered | Pause execution; request user decisions; do not proceed past Task 1. | User/Coordinator | Open |
| User rejects the mode-label rename | Skip Task 3; keep existing labels; revise ADRs to drop the rename; restart from Task 2. | User/Coordinator | Open |
| User rejects the six-role roster reconciliation | Replan: choose either ADR 0004's eight-role roster or a custom roster; update Tasks 2–8. | User/Coordinator | Open |
| Phase B smoke-test exposes a missing skill | Add the gap as a Phase C entry or revise Task 6 scope; record in `LEARNINGS.md`. | Coordinator | Open |
| Release work is requested mid-plan | Add a separate plan for `release-cut` and Release Agent; do not extend this plan. | User/Coordinator | Open |

## Edge Cases And Failure Modes
- Two-vocabulary drift: contributors use old labels and new labels simultaneously. Mitigated by Task 9's sweep while preserving `M0` through `M4` identifiers.
- Skill drift: `SKILL.md` files inline rules instead of referencing them. Mitigated by Task 6 validation checkpoint and `references-rules.md`.
- Role collision: ADR 0004's `Orchestrator` term colliding with the `Coordinator` identity. Mitigated by Task 2.
- Empty-state directories pruned by tooling: `.agents/context/*` directories must contain `README.md` so they are tracked.
- Premature Phase C: starting platform docs before the smoke-test passes risks documenting an unproven loop. Gate Task 8 on Task 7.

## Validation Plan
- Documentation-only validation per `.agents/references/testing.md` change-type rules.
- For each task, run `./build.ps1 build`; for documentation-only diffs the wrapper may take the lightweight shortcut and report that manual consistency review is sufficient.
- Pair the wrapper result with the task's manual review checkpoint.
- Smoke-test (Task 7) inherits validation from the chosen target change; if it touches code, run the smallest sufficient `./build.ps1` invocation per `testing.md`.

## Verification Strategy
- Unit tests: not applicable.
- Integration tests: not applicable.
- Contract tests: not applicable.
- Smoke/benchmark tests: only as part of Task 7's chosen target change.
- Negative scenarios: Task 9 search must return zero primary-label hits for the old names in swept paths after the sweep.

## Better Engineering Notes
- Prerequisite cleanup included: materializing `.agents/context/*` (closes the existing `workflow.md`-vs-tree gap) is done in Phase A even though it predates the skill catalog work.
- Deferred follow-up not hidden: `release-cut` skill, Release Agent identity, and any release-checklist alignment are explicitly deferred until release work is requested.
- Deferred follow-up not hidden: lint/CI smoke-test for skill bundles (mentioned in ADR 0003 Costs) is not part of this plan; track separately if Phase B skill drift is observed.

## Validation Results
| Date | Command | Scope | Result | Notes |
| --- | --- | --- | --- | --- |
| 2026-05-10 | `git diff --check` | ADR 0004, plan, and roadmap documentation diff | Passed | No whitespace diagnostics. |
| 2026-05-10 | `./build.ps1 build` | Documentation-only wrapper validation | Passed | Wrapper detected only lightweight uncommitted files and skipped Gradle; manual consistency review is sufficient. |

## User Validation
- Confirm `.agents/references/workflow.md` defines `M0: direct`, `M1: assisted`, `M2: delegated`, `M3: parallel`, and `M4: gated` with unchanged mode semantics.
- Confirm both ADRs reflect the user decision, use the new vocabulary, and cross-link as principle (0004) and implementation input (0003) if that relationship is accepted.
- Confirm `.agents/context/*` exists with `README.md` ownership notes.
- Confirm Phase B skills exist in `SKILL.md` shape and were used end-to-end on one smoke-test task with artifacts visible under `.agents/context/*`.
- Confirm `.junie/AGENTS.md`, `AGENTS.md`, and `WORKING_WITH_AI.md` cross-reference the new vocabulary without duplicating `workflow.md` content.
