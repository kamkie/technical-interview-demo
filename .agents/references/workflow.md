# Workflow Guide For AI Agents

`.agents/references/workflow.md` owns branch, worktree, delegation, multi-agent state, sidecar, integration, and remote-handoff mechanics.
It does not own plan creation, whole-plan execution loops, ad hoc task execution, validation scope, review policy, or release sequencing.

Use this file only when work needs collaboration mechanics beyond one local agent editing one working tree, or when an approved plan names workflow state, delegation, worktrees, sidecars, integration queues, or remote handoff.
Use `.agents/references/plan-execution.md` for whole-plan execution, `.agents/references/execution.md` for ad hoc or one-plan-task work, `.agents/references/testing.md` for validation scope, `.agents/references/reviews.md` for review expectations, and `.agents/references/releases.md` only after implementation is integrated and release work is in scope.

## Mode Decision

- Choose the most optimal workflow that keeps ownership clear.
- Report the selected workflow mode when execution starts and in interim execution updates.
- For work without a created plan, infer any `M0` through `M4` mode from the request, write-scope boundaries, risk, stop conditions, validation targets, sidecar gates, and execution environment; when those inputs are unclear, route to planning instead of clamping to `M0`.
- For planned work, use the execution shape chosen during planning; revise the plan before splitting work if that shape is missing, stale, or contradicted by execution reality.
- For multiple approved plans selected for one execution run, strongly prefer `M3: parallel`; use a lower mode only when plans must run serially or cannot be safely split, and use `M4: gated` only when sidecar gates are part of the planned workflow.
- Use delegation only when the current user request, inferred no-plan mode or approved plan, and execution environment allow it.
- When the runtime requires explicit authorization before spawning sub-agents, such as Codex sessions, treat delegation and parallel agent work as unavailable until the user explicitly asks for it.
- Keep release sequencing out of workflow execution.
- Keep `M0` through `M4` as stable identifiers; the readable labels are vocabulary only and do not change the mode semantics.

| Mode | Use When | Concurrency |
| --- | --- | --- |
| `M0: direct` | Delegation is not useful or not allowed. | One agent. |
| `M1: assisted` | A read-only scan, review, or verification can help without editing files. | Coordinator plus read-only sidecar. |
| `M2: delegated` | One worker can edit one clear write scope. | Coordinator plus one worker. |
| `M3: parallel` | Multiple disjoint edit scopes can run in parallel. | Coordinator plus workers. |
| `M4: gated` | M3 plus independent review, verification, or specialist gates. | Coordinator plus workers and sidecars. |

## Ownership Boundaries

Exactly one **Coordinator** owns workflow shape, plan routing, shared files, integration order, conflict resolution, final validation, and final reporting.
Each delegated activity has one accountable **Worker**, **Reviewer**, **Verifier**, or **Specialist**.
Two editing agents must not own the same write scope at the same time.
Name shared files before workers start, and keep them coordinator-owned unless the plan explicitly assigns them.

Split work only when the benefit is concrete and the inferred no-plan mode or approved plan allows delegation, parallelism, or a worktree-backed handoff.
Good boundaries follow package, contract, artifact, or plan ownership, such as `business.book`, `business.category`, `business.localization`, `business.user`, `technical.security`, one bounded public API change with its contract artifacts, documentation-only slices that do not overlap source edits, or one approved plan per worker.
Do not split work when workers would overlap on the same controller, service, integration test, REST Docs or OpenAPI artifact, roadmap row, changelog row, plan status row, unresolved decision, or indivisible plan task.

## Role Identities And Read Sets

Use these role identities for multi-agent execution:

| Role | Responsibility |
| --- | --- |
| Coordinator | Owns workflow shape, routing, shared files, integration order, conflict resolution, final validation, and final reporting. |
| Planner | Authors or revises execution plans, decision logs, readiness reviews, and plan-task boundaries. |
| Worker | Implements one approved write scope and reports changed files, validation, risks, and integration readiness. |
| Reviewer | Performs read-only diff, contract, documentation, maintainability, or gate review. |
| Verifier | Runs validation or independent checks without editing files. |
| Specialist | Performs role-scoped gates such as security review, documentation review, architecture review, or release readiness when explicitly in scope. |

Load the smallest role-shaped context that can complete the delegated activity:

| Role | Always loaded | Conditional |
| --- | --- | --- |
| Coordinator | `AGENTS.md`, `.agents/references/workflow.md`, `.agents/references/documentation.md` | active plan; `.agents/references/releases.md` only for explicit release work |
| Planner | `AGENTS.md`, `.agents/references/planning.md`, `.agents/references/plan-template.md` | `.agents/references/documentation.md`, `.agents/references/architecture.md`, linked ADR, PRD, spec, or roadmap item when referenced |
| Worker | `AGENTS.md`, `.agents/references/execution.md`, `.agents/references/code-style.md` | `.agents/references/testing.md` on validation; `.agents/references/documentation.md` on docs or contract artifacts |
| Reviewer | `AGENTS.md`, `.agents/references/reviews.md`, `.agents/references/documentation.md` | `.agents/references/code-style.md`, affected specs, contract docs, or source files |
| Verifier | `AGENTS.md`, `.agents/references/testing.md`, `.agents/references/troubleshooting.md` | `.agents/references/command-wrapper.md`, `.agents/references/gradle-task-graph.md`, affected test or benchmark docs |
| Specialist | `AGENTS.md`, `.agents/references/reviews.md` | `.agents/references/releases.md`, `.agents/references/release-checklist.md`, `.agents/references/documentation.md`, `.agents/references/architecture.md`, or another focused owner guide matching the specialist gate |

## Delegation Contract

Before delegated work starts, keep current work committed or stashed, use unique worker branches, and never check out the same branch in more than one worktree at a time.
Every delegated activity receives a handoff packet that names the objective, source request or plan, assigned role, required context, read scope, write scope, out-of-scope files, expected output, validation target, stop conditions, branch or worktree context, allowed skills, reporting format, and state path when durable state is used.
If the handoff cannot name clear inputs, outputs, write scope, and stop conditions, route to `Replan?` instead of delegating.

Use `.agents/context/*` only for durable workflow state that must survive context switches, integration, or remote handoff; otherwise short-lived handoffs may stay in conversation.
Use the narrowest useful file under `.agents/context/handoffs/`, `.agents/context/workers/`, `.agents/context/reviews/`, `.agents/context/verifications/`, or `.agents/context/specialists/`, with stable names such as `<plan_stem_or_topic>__<agent_name>.md`.
State records the target plan or topic, role, branch/worktree/sandbox context, read and write scopes, shared exclusions, expected output, validation result, changed files, proposed changelog text when needed, checkpoint or commit SHA, blockers, risks, coordinator decisions, and integration readiness.

Each delegated agent reports completed activity, changed files, decisions, validation result, risks, follow-up, and whether it is ready for integration.
Failed validation reports include the failing command, summary, and recommended next activity: `Diagnose?`, `Fix?`, or `Replan?`.
Completed, blocked, failed, and user-cancelled are terminal states; record blocked, failed, or cancelled work in the relevant state file or plan before reporting final status.
Workers are still active while implementing, validating, pushing, opening a pull request, or waiting on unrecorded follow-up.

## Coordination Patterns

Use single-plan delegation when one approved active plan remains the canonical source of truth and worker-safe slices are disjoint.
The plan must be approved, decision-complete, and explicit about worker-safe ownership boundaries.
The coordinator normally owns the active plan file, `ROADMAP.md`, `CHANGELOG.md`, shared specs, contract artifacts, shared docs, workflow state, integration notes, final validation, and final review; workers avoid those files unless explicitly assigned.
The coordinator keeps the plan current, reviews worker results as they finish, integrates accepted branches in deliberate order, resolves conflicts without discarding unrelated changes, runs final validation, updates durable tracking, and reports every worker's terminal state.

Use multi-plan coordination when separate approved active plans move in parallel and later need one integration pass.
Each selected plan must be approved and decision-complete; each worker owns exactly one plan or explicitly bounded plan slice; selected plans must not compete for source files, specs, public contract artifacts, roadmap rows, changelog entries, or release decisions.
Workers do not edit canonical `CHANGELOG.md` unless assigned; proposed unreleased text lives in `.agents/context/workers/` or an explicitly assigned temporary root `CHANGELOG_<topic>.md`, then the coordinator folds accepted text into `CHANGELOG.md` and removes temporary changelog files during integration.

Use sidecars for independent read-only review, verification, or specialist checks.
Sidecars may inspect active work, but they must not edit worker-owned files unless the plan changes and ownership is reassigned.
`M4: gated` includes all `M3: parallel` requirements plus review, verification, or specialist queues; a gate decision table; conflict handling; sidecar stop conditions; and approval authority for each gate.
Common gates are `Code Review`, `Verification`, `Security Review?`, `Docs Review?`, and `Release/Operations Gate?`.
Sidecar output never replaces coordinator integration responsibility; the coordinator records gate outcomes in the owning plan or state file.

## Integration And Handoff

`main` remains the integration target for completed work.
Worktree branches are temporary execution branches, not release branches; release-target and tagging preconditions live in `AGENTS.md` and `.agents/references/releases.md`.
Prefer merging accepted local branches or worktrees into the integration branch.
Use cherry-pick only when the user asks for it, when accepting less than the full branch, or when a normal merge is not viable; record the reason.
Remove temporary worktrees and branches only after their changes are integrated and no longer needed for local review.

The coordinator reviews every agent result before local integration, integrates one result at a time unless outputs are known independent, validates proportional affected surfaces after integration steps, re-briefs affected agents when integration changes assumptions, routes behavior conflicts to `Replan?`, runs final validation from the integrated repository state, and checks `git status --short` before delegation and after integration.
Finish local validation before push or pull-request handoff unless the user explicitly asks for a remote-first diagnostic flow.
If no build or test command ran, record the narrow checks that actually ran and why broader validation was skipped for the handoff report.

Use remote handoff only after local integration has produced the intended handoff state.
Before pushing, opening a pull request, or handing work to a remote reviewer, confirm the integrated local state, record skipped/failed/blocked/cancelled slices, include validation evidence and gaps, include workflow state paths when relevant, and avoid release sequencing unless requested.
For delegated or integrated work, report the workflow mode, owned plan or slice, changed files, validation run, commit SHA or checkpoint, blockers, and final status or ready-for-integration status.
Interim progress updates must say they are interim and include the current workflow mode.
Do not give a final success summary while any worker or sidecar is still active.

## Replan Triggers

Route to `Replan?` when two agents need the same write scope; a worker finds missing, stale, or contradictory specs; user-owned changes exist inside a write scope; validation failure cannot be isolated to one scope; integration needs an unplanned design, security, release, architecture, documentation-ownership, or product decision; an agent edits outside scope; outputs conflict; review or verification findings affect another active scope; gate decisions conflict; a shared file must move from coordinator ownership to a worker; or actual work expands beyond the approved plan, plan task, or user request.
