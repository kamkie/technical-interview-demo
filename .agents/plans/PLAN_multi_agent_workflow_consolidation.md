# Plan: Multi-Agent Workflow Consolidation

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
| Accepted Fallbacks | Keep `.agents/references/workflow.md` as the public owner path; adapt external multi-agent rules instead of raw-copying them; use `.agents/context/*` for multi-agent state |
| Ready For Execution | Yes |
| Last Updated | 2026-05-10 |

## Summary
- Consolidate workflow delegation guidance into `.agents/references/workflow.md` using the useful policy model from `D:\Projects\demo\ai-workflow-demo\.agents\references\multi-agent-execution.md`.
- Retire `.agents/references/workflow-delegated-plan.md` and `.agents/references/workflow-coordinated-plans.md` after moving their still-current mechanics into `workflow.md`.
- Success means the live AI guidance tells one consistent story for branch/worktree mechanics, delegation modes, handoff packets, `.agents/context/*` state, sidecars, integration, and replan triggers without stale references to retired workflow companion files.

## Scope
- In scope:
  - Rewrite `.agents/references/workflow.md` as the single workflow owner for branch, worktree, delegation, multi-agent state, sidecar, integration, and remote-handoff mechanics.
  - Adapt external multi-agent concepts such as `M0`-`M4`, coordinator/worker/reviewer/verifier/specialist roles, handoff packets, agent results, integration queues, gates, and replan triggers.
  - Adopt the external `.agents/context/*` state layout for handoffs, workers, reviews, verifications, and specialists while preserving this repo's current branch/worktree rules.
  - Retire `.agents/tmp/workflow/` and `.agents/tmp/workflow-local/` as the required workflow state locations in live guidance.
  - Remove the two retired workflow companion reference files after current rules are moved.
  - Update cross-references in `AGENTS.md`, `.agents/references/*.md`, and `docs/specs/lifecycle-phase-activities.md` where they point at retired files or outdated workflow shapes.
  - Record skipped normal validation caused by the current interactive-session constraint.
- Out of scope:
  - Runtime code, tests, builds, generated docs, OpenAPI, REST Docs, release work, setup docs, and changelog release history.
  - Creating a new `.agents/references/multi-agent-execution.md` owner path unless execution discovers that preserving `workflow.md` would be materially worse.
  - Creating `.agents/references/codex-runtime.md`.
  - Creating task-specific `.agents/context/*` state files for this planning change; the layout will be documented for future delegated or sidecar work.
  - Changing Codex platform instructions or model-selection policy outside repo-owned AI guidance.

## Current State
- `AGENTS.md` routes branch, worktree, delegation, integration, and remote-handoff mechanics to `.agents/references/workflow.md`.
- `.agents/references/documentation.md` currently says detailed delegated-work mechanics live in `.agents/references/workflow-delegated-plan.md` and `.agents/references/workflow-coordinated-plans.md`.
- `.agents/references/workflow.md` owns the shared branch/worktree defaults, split-work criteria, worker-log schema, delegation quality bar, coordinator ownership, and reporting.
- `.agents/references/workflow-delegated-plan.md` owns one-plan split mechanics.
- `.agents/references/workflow-coordinated-plans.md` owns multi-plan coordination mechanics.
- The external `multi-agent-execution.md` provides a stronger unified policy model and uses `.agents/context/*` state paths.
- The user selected `.agents/context/*` as the target state layout for this workflow consolidation.
- The user constrained this interactive documentation session to documentation-only work focused on `AGENTS.md`, `.agents/references`, and `docs/`, with no tests, builds, or normal verification commands.

## Requirement Gaps And Open Questions
| ID | Question / Gap | Why It Matters | Owner | Status | Fallback / Decision | Blocks Ready? |
| --- | --- | --- | --- | --- | --- | --- |
| Q1 | Should the imported workflow become a new `.agents/references/multi-agent-execution.md` file or replace the current workflow owner? | A new file would require broader routing and duplicate the existing workflow owner path. | Agent | Answered | Replace and consolidate into `.agents/references/workflow.md`; do not add a new owner path. | No |
| Q2 | Should retired delegated/coordinated workflow documents be archived or deleted? | Reference lifecycle rules require still-current rules to move before retirement, but do not require preserving thin companion files. | Agent | Answered | Delete the retired companion files after moving current rules into `workflow.md`; no archive is needed for non-report-like superseded guidance. | No |
| Q3 | Should the external `.agents/context/*` state paths replace this repo's `.agents/tmp/workflow/` worker logs? | The chosen state layout affects workflow routing, handoffs, worker status, sidecar queues, and stale-reference cleanup. | User | Answered | Use `.agents/context/handoffs/`, `.agents/context/workers/`, `.agents/context/reviews/`, `.agents/context/verifications/`, and `.agents/context/specialists/`; retire `.agents/tmp/workflow/` and `.agents/tmp/workflow-local/` as canonical workflow state paths. | No |
| Q4 | Should normal documentation validation run during this session? | Repository defaults ask for validation, but the user explicitly disabled tests, builds, and normal verification for this interactive documentation session. | User | Answered | Run no tests, builds, generated-doc checks, or normal verification commands; perform manual consistency review only and record skipped checks. | No |

## Decision Log And Assumptions
| ID | Decision / Assumption | Source | Date | Revisit Trigger |
| --- | --- | --- | --- | --- |
| D1 | Keep `.agents/references/workflow.md` as the stable public workflow owner and import the external policy model there. | Analysis of current cross-references and user request | 2026-05-10 | If the user explicitly asks for a new `multi-agent-execution.md` file. |
| D2 | Consolidate and retire `workflow-delegated-plan.md` and `workflow-coordinated-plans.md` instead of keeping three overlapping workflow files. | `.agents/references/references-rules.md` overlap and compaction rules | 2026-05-10 | If consolidation makes `workflow.md` too broad to satisfy reference-document role clarity. |
| D3 | Adopt `.agents/context/*` for multi-agent workflow state while preserving local branch/worktree mechanics from the current repo guidance. | User request and current `.agents/references/workflow.md` branch/worktree rules | 2026-05-10 | If the repo adopts a different durable state directory convention. |
| D4 | Treat external `M0`-`M4` modes as workflow shape vocabulary, not as a mandate to use delegation. | External multi-agent rules and current repo delegation defaults | 2026-05-10 | If `AGENTS.md` later declares different delegation limits. |
| D5 | Do not run normal validation commands during this interactive documentation session. | User instruction | 2026-05-10 | If the user explicitly authorizes validation or ends the constrained session. |

## Execution Shape And Shared Files
- Recommended shape: one local documentation branch/session with one coordinator.
- Why that shape fits best: the work changes shared AI guidance and cross-references, so parallel editing would create unnecessary ownership conflicts.
- Coordinator-owned shared files if delegation later becomes necessary:
  - `AGENTS.md`
  - `ROADMAP.md`
  - `.agents/plans/PLAN_multi_agent_workflow_consolidation.md`
  - `.agents/references/workflow.md`
  - `.agents/references/documentation.md`
  - `.agents/references/planning.md`
  - `.agents/references/plan-execution.md`
  - `.agents/references/execution.md`
  - `.agents/references/workflow-delegated-plan.md`
  - `.agents/references/workflow-coordinated-plans.md`
  - `docs/specs/lifecycle-phase-activities.md`
- Candidate worker boundaries if later delegation becomes necessary:
  - Read-only stale-reference scan after the coordinator drafts the consolidated guide.
  - Read-only review of the consolidated guide against `references-rules.md`.

## Affected Artifacts
- Tests: none.
- Docs:
  - `AGENTS.md`
  - `.agents/references/workflow.md`
  - `.agents/references/documentation.md`
  - `.agents/references/planning.md`
  - `.agents/references/plan-execution.md`
  - `.agents/references/execution.md`
  - `docs/specs/lifecycle-phase-activities.md`
- Retired docs:
  - `.agents/references/workflow-delegated-plan.md`
  - `.agents/references/workflow-coordinated-plans.md`
- OpenAPI: none.
- HTTP examples: none.
- Source files: none.
- Owning AI guide updates when durable repo guidance changes: `.agents/references/workflow.md` is the primary owner; `AGENTS.md` and `.agents/references/documentation.md` keep routing aligned.
- Build or benchmark checks: explicitly out of scope for this interactive documentation session.

## Progress Tracker
| Milestone | Status | Owner | Commit | Validation | Notes |
| --- | --- | --- | --- | --- | --- |
| 1: Consolidate workflow owner | Done | Agent | Pending | Manual doc consistency review passed | Consolidated `workflow.md`, adopted `.agents/context/*`, preserved branch/worktree rules, and retired companion files. |
| 2: Align routing references | Not Started | Agent | Pending | Manual stale-reference review only | Update `AGENTS.md`, reference guide routing, and lifecycle owner references. |

## Execution Milestones
### Milestone 1: Consolidate workflow owner
| Field | Value |
| --- | --- |
| Status | Done |
| Goal | Rewrite `.agents/references/workflow.md` so it owns the unified multi-agent workflow model and local branch/worktree mechanics. |
| Owned Files Or Packages | `.agents/references/workflow.md`, `.agents/references/workflow-delegated-plan.md`, `.agents/references/workflow-coordinated-plans.md` |
| Coordinator-Owned Shared Files | `AGENTS.md`, `.agents/references/documentation.md`, `.agents/references/planning.md`, `.agents/references/plan-execution.md`, `.agents/references/execution.md`, `docs/specs/lifecycle-phase-activities.md`, `ROADMAP.md`, this plan |
| Context Required | `AGENTS.md`, `.agents/references/execution.md`, `.agents/references/references-rules.md`, `.agents/references/documentation.md`, `.agents/references/workflow.md`, `.agents/references/workflow-delegated-plan.md`, `.agents/references/workflow-coordinated-plans.md`, and `D:\Projects\demo\ai-workflow-demo\.agents\references\multi-agent-execution.md` |
| Behavior To Preserve | Workflow guidance remains focused, current-state, and subordinate to executable specs and published contract docs; `workflow.md` remains the entry point for branch, worktree, delegation, `.agents/context/*` state, integration, and remote handoff. |
| Deliverables | Consolidated `workflow.md`; deleted retired companion files after preserving current rules. |
| Validation Checkpoint | Manual review that the consolidated guide has one opening ownership statement, uses `.agents/context/*` state paths, preserves branch/worktree rules, defines M0-M4 modes without forcing delegation, and contains no stale references to missing `codex-runtime.md` or canonical `.agents/tmp/workflow/` state paths. |
| Commit Checkpoint | Commit after Milestone 1 if this plan is executed outside the current uncommitted interactive session; otherwise leave uncommitted until explicit handoff. |

### Milestone 2: Align routing references
| Field | Value |
| --- | --- |
| Status | Not Started |
| Goal | Update live routing docs so agents load the consolidated workflow owner and no live guidance points to retired companion files. |
| Owned Files Or Packages | `AGENTS.md`, `.agents/references/documentation.md`, `.agents/references/planning.md`, `.agents/references/plan-execution.md`, `.agents/references/execution.md`, `docs/specs/lifecycle-phase-activities.md`, this plan, `ROADMAP.md` |
| Coordinator-Owned Shared Files | `.agents/references/workflow.md` |
| Context Required | `AGENTS.md`, `.agents/references/execution.md`, `.agents/references/references-rules.md`, `.agents/references/documentation.md`, `.agents/references/planning.md`, `.agents/references/plan-execution.md`, `.agents/references/execution.md`, and `docs/specs/lifecycle-phase-activities.md` |
| Behavior To Preserve | Owner-guide loading remains narrow; workflow consolidation does not create recursive load requirements or move plan execution, validation, review, release, architecture, or code-style rules into `workflow.md`. |
| Deliverables | Updated cross-references and plan/roadmap status reflecting the actual non-release state. |
| Validation Checkpoint | Manual stale-reference review for retired workflow filenames and external-only paths; manual consistency review against `references-rules.md`. No tests, builds, generated-doc checks, or normal verification commands during this interactive session. |
| Commit Checkpoint | Commit after Milestone 2 if the user requests handoff and the interactive session permits committing; otherwise record remaining uncommitted work explicitly. |

## Blockers And Replan Triggers
| Trigger / Blocker | Response | Owner | Status |
| --- | --- | --- | --- |
| Consolidated `workflow.md` becomes too broad or unclear for one focused reference owner. | Replan to keep `workflow.md` as router plus one focused companion file, and update `references-rules.md` only if standing reference rules need to change. | Agent | Open |
| Execution discovers a live cross-reference outside `AGENTS.md`, `.agents/references`, or `docs/` that must change for correctness. | Stop and ask whether to expand scope beyond the current interactive-session focus. | User | Open |
| External multi-agent rules conflict with local spec-driven development or lifecycle owner mapping. | Prefer local repo guidance and adapt the imported rule; do not raw-copy conflicting text. | Agent | Open |
| User asks for actual parallel agent work while implementing the workflow guide. | Keep this plan single-coordinator unless the user explicitly revises execution shape and write scopes. | User | Open |
| Normal validation becomes required before handoff. | Record that it is skipped under the current user constraint, or ask for permission if the user ends the constrained session. | User | Open |

## Edge Cases And Failure Modes
- Accidentally replacing `workflow.md` with external text that references missing `codex-runtime.md`.
- Accidentally preserving `.agents/tmp/workflow/` or `.agents/tmp/workflow-local/` as canonical workflow state paths after the user selected `.agents/context/*`.
- Deleting companion files before moving their one-plan, multi-plan, changelog, and worker-log rules into the consolidated owner.
- Leaving `documentation.md`, `planning.md`, `execution.md`, `plan-execution.md`, or lifecycle specs pointing to retired companion files.
- Making `M3`/`M4` sound self-electable when the external rule says higher concurrency requires explicit user request or approved plan.
- Treating sidecar review/verification as a substitute for the coordinator's integration responsibility.
- Expanding into setup, release, source-code, or generated contract work despite the documentation-only session constraint.

## Validation Plan
- Manual review only during this interactive documentation session:
  - Confirm `workflow.md` has a distinct ownership statement and does not collapse unrelated reference domains.
  - Confirm current branch/worktree rules survive consolidation.
  - Confirm `.agents/context/handoffs/`, `.agents/context/workers/`, `.agents/context/reviews/`, `.agents/context/verifications/`, and `.agents/context/specialists/` are the documented state paths.
  - Confirm imported M0-M4 policy, handoff packet, agent result, integration, gate, and replan rules are adapted to local terms.
  - Confirm live references no longer point to retired `workflow-delegated-plan.md` or `workflow-coordinated-plans.md`.
  - Confirm no live guidance refers to external-only `codex-runtime.md`.
  - Confirm `.agents/tmp/workflow/` and `.agents/tmp/workflow-local/` are no longer canonical live workflow state paths.
- Skipped by explicit user instruction for this session:
  - tests
  - builds
  - generated documentation checks
  - normal validation commands

## Testing Strategy
- Unit tests: not applicable.
- Integration tests: not applicable.
- Contract tests: not applicable.
- Smoke/benchmark tests: not applicable.
- Negative verification: manual stale-reference review for retired workflow files, external-only `codex-runtime.md`, and obsolete canonical `.agents/tmp/workflow/` state paths.

## Better Engineering Notes
- Keep the import current-state and repo-local; use the external `.agents/context/*` state layout, but do not preserve external text that names unsupported files just to stay close to the source.
- Prefer one consolidated workflow owner unless readability fails during implementation.
- If readability fails, the fallback should still reduce overlap and update `AGENTS.md` plus `.agents/references/documentation.md` in the same change.

## Validation Results
| Date | Command | Scope | Result | Notes |
| --- | --- | --- | --- | --- |
| 2026-05-10 | Manual planning review | Plan readiness, scope, routing, and validation constraints | Passed | Plan is ready for documentation-only execution; normal validation intentionally skipped under user session constraint. |
| 2026-05-10 | Manual plan revision readback | `.agents/context/*` state-layout requirement in this plan and `ROADMAP.md` | Passed | Plan now adopts `.agents/context/*`; `.agents/tmp/workflow/` appears only as an obsolete canonical path to retire. |
| 2026-05-10 | Manual Milestone 1 consistency review | `.agents/references/workflow.md` and retired companion files | Passed | Consolidated guide has one ownership statement, uses `.agents/context/*` state paths, preserves branch/worktree rules, defines M0-M4 without forcing delegation, and contains no live canonical `.agents/tmp/workflow/` state paths or `codex-runtime.md` references. |

## User Validation
- Review the consolidated workflow guide and confirm it preserves the intended local branch/worktree mechanics while adopting the external multi-agent execution model and `.agents/context/*` state layout.
- Confirm retired workflow companion files are no longer referenced by live docs.
