# Workflow Guide For AI Agents

`.agents/references/workflow.md` owns branch, worktree, delegation, multi-agent state, sidecar, integration, and remote-handoff mechanics.
It does not own plan creation, whole-plan execution loops, ad hoc task execution, validation scope, review policy, or release sequencing.

Use this file only when work needs collaboration mechanics beyond one local agent editing one working tree, or when an approved plan names workflow state, delegation, worktrees, sidecars, integration queues, or remote handoff.
Use `.agents/references/plan-execution.md` for whole-plan execution, `.agents/references/execution.md` for ad hoc or one-milestone work, `.agents/references/testing.md` for validation scope, `.agents/references/reviews.md` for review expectations, and `.agents/references/releases.md` only after implementation is integrated and release work is in scope.

## Defaults

- Choose the most optimal workflow that keeps ownership clear.
- For work without a created plan, infer the mode from the request, write-scope boundaries, risk, and execution environment.
- For planned work, use the execution shape chosen during planning; revise the plan before splitting work if that shape is missing, stale, or contradicted by execution reality.
- Use delegation only when the current user request, inferred no-plan mode or approved plan, and execution environment allow it.
- Keep current work committed or stashed before creating side branches or worktrees.
- Use unique worker branches, and never check out the same branch in more than one worktree at a time.
- Name shared files before workers start, and keep them coordinator-owned unless the plan explicitly assigns them.
- Finish local validation before push or pull-request handoff unless the user explicitly asks for a remote-first diagnostic flow.
- Keep release sequencing out of workflow execution.

## Roles And Modes

Exactly one **Coordinator** owns workflow shape, plan routing, shared files, integration order, conflict resolution, final validation, and final reporting.
Each delegated activity has one accountable **Worker**, **Reviewer**, **Verifier**, or **Specialist**.
Two editing agents must not own the same write scope at the same time.

| Mode | Use When | Concurrency |
| --- | --- | --- |
| `M0: solo` | Delegation is not useful or not allowed. | One agent. |
| `M1: sidecar-readonly` | A read-only scan, review, or verification can help without editing files. | Coordinator plus read-only sidecar. |
| `M2: bounded-worker` | One worker can edit one clear write scope. | Coordinator plus one worker. |
| `M3: parallel-sliced` | Multiple disjoint edit scopes can run in parallel. | Coordinator plus workers. |
| `M4: full-sidecar` | M3 plus independent review, verification, or specialist gates. | Coordinator plus workers and sidecars. |

For no-plan work, infer and use any `M0` through `M4` mode when the request, ownership boundaries, stop conditions, validation targets, and any sidecar gates are clear enough to brief agents without inventing scope.
When those inputs are unclear, route the work to planning instead of clamping to `M0`.
For planned work, do not re-infer the mode at execution time; use the plan's execution shape and route to `Replan?` if it no longer fits.
When multiple approved plans are selected for one execution run, strongly prefer `M3: parallel-sliced`; choose a lower mode only when the plans must run serially or cannot be safely split, and choose `M4` only when sidecar gates are part of the planned workflow.

## Splitting Work

Split work only when the benefit is concrete and the inferred no-plan mode or approved plan allows delegation, parallelism, or a worktree-backed handoff.

Good boundaries follow package, contract, artifact, or plan ownership, for example:

- `business.book`, `business.category`, `business.localization`, `business.user`, or `technical.security`
- one bounded public API change and its contract artifacts
- documentation-only slices that do not overlap active source edits
- one approved plan per worker when multiple plans are intentionally coordinated

Do not split work when workers would overlap on the same controller, service, integration test, REST Docs or OpenAPI artifact, roadmap row, changelog row, plan status row, unresolved decision, or indivisible milestone.

## State, Handoffs, And Results

Use `.agents/context/*` for durable workflow state that must survive context switches, integration, or remote handoff; otherwise short-lived handoffs may stay in conversation.
Use the narrowest useful state file under:

- `.agents/context/handoffs/`
- `.agents/context/workers/`
- `.agents/context/reviews/`
- `.agents/context/verifications/`
- `.agents/context/specialists/`

Use stable names such as `<plan_stem_or_topic>__<agent_name>.md`.
State files record the target plan or topic, role, branch/worktree/sandbox context, read and write scopes, shared exclusions, expected output, validation target and result, changed files, proposed changelog text when needed, checkpoint or commit SHA, blockers, risks, coordinator decisions, and integration readiness.

Every delegated activity receives a handoff packet before work starts.
The handoff names the activity, objective, source request or plan, assigned role, state path when used, queue order when ordered, required context, read scope, write scope, out-of-scope files, expected output, validation, stop conditions, branch or worktree context, allowed skills, and reporting format.
If the handoff cannot name clear inputs, outputs, write scope, and stop conditions, route to `Replan?` instead of delegating.

Each delegated agent reports completed activity, changed files, decisions, validation result, risks, follow-up, and whether it is ready for integration.
Failed validation reports include the failing command, summary, and recommended next activity: `Diagnose?`, `Fix?`, or `Replan?`.
Completed, blocked, failed, and user-cancelled are terminal states; record blocked, failed, or cancelled work in the relevant state file or plan before reporting final status.
Workers are still active while implementing, validating, pushing, opening a pull request, or waiting on unrecorded follow-up.

## One-Plan Delegation

Use one-plan delegation when one approved active plan remains the canonical source of truth and worker-safe slices are disjoint.

Preconditions:

- the active plan is approved, decision-complete, and identifies worker-safe ownership boundaries
- current work is committed or stashed before workers branch
- each worker has an explicit write scope and validation target
- coordinator-owned shared files are named before workers start

The coordinator normally owns the active plan file, `ROADMAP.md`, `CHANGELOG.md`, shared specs, contract artifacts, shared docs, workflow state, integration notes, final validation, and final review.
Workers avoid coordinator-owned files unless the coordinator explicitly assigns them.

Coordinator loop:

1. Keep the canonical plan current.
2. Review worker state and agent results as workers finish.
3. Integrate accepted branches in a deliberate order.
4. Resolve conflicts without discarding unrelated user or worker changes.
5. Run final validation from `.agents/references/testing.md`.
6. Update the canonical plan, roadmap, and changelog when applicable.
7. Report every worker's terminal state before declaring the run complete.

## Coordinated Multi-Plan Work

Use coordinated multi-plan work when separate approved active plans move in parallel and later need one integration pass.
Use `M3: parallel-sliced` as the preferred baseline unless dependencies or shared artifacts force serial execution, or planned sidecar gates require `M4`.

Preconditions:

- every selected plan is approved and decision-complete
- each worker owns exactly one plan or one explicitly bounded plan slice
- selected plans do not compete for the same source files, specs, public contract artifacts, roadmap rows, changelog entries, or release decisions
- current work is committed or stashed before worktrees branch
- the coordinator has recorded integration order and shared artifacts

Workers do not edit canonical `CHANGELOG.md` unless the coordinator assigns it.
Workers keep proposed unreleased text in `.agents/context/workers/` or in a temporary root `CHANGELOG_<topic>.md` file when explicitly assigned; the coordinator folds accepted text into `CHANGELOG.md` and removes temporary changelog files during integration.

The coordinator tracks selected plans and workflow state, waits for each worker to reach a terminal state, reviews accepted outputs in planned order, integrates accepted branches, folds changelog text, runs final validation, updates roadmap and plan lifecycle states, and reports completed, blocked, failed, or cancelled workers.

## Sidecar Gates

Use sidecars for independent read-only review, verification, or specialist checks.
Sidecars may inspect active work, but they must not edit worker-owned files unless the plan changes and ownership is reassigned.

`M4` plans include all `M3` requirements plus review, verification, or specialist queues; a gate decision table; conflict handling; sidecar stop conditions; and approval authority for each gate.
Common gates are `Code Review`, `Verification`, `Security Review?`, `Docs Review?`, and `Release/Operations Gate?`.
Sidecar output never replaces the coordinator's integration responsibility; the coordinator records the gate outcome in the owning plan or state file.

## Branches And Integration

- `main` remains the integration target for completed work.
- Worktree branches are temporary execution branches, not release branches.
- Release-target and tagging preconditions live in `AGENTS.md` and `.agents/references/releases.md`.
- Start delegated work from a committed or stashed state so branches or worktrees are comparable and reviewable.
- Prefer merging accepted branches or pull requests into the integration branch.
- Use cherry-pick only when the user asks for it, when accepting less than the full branch or pull request, or when a normal merge is not viable; record the reason.
- Remove temporary worktrees and branches only after their changes are integrated and they are no longer needed for review or remote handoff.

The coordinator reviews every agent result before integration, integrates one result at a time unless outputs are known independent, validates proportional affected surfaces after integration steps, re-briefs affected agents when integration changes assumptions, routes behavior conflicts to `Replan?`, runs final validation from the integrated repository state, and checks `git status --short` before delegation and after integration.
If no build or test command ran, report the narrow checks that actually ran and why broader validation was skipped.
Do not give a final success summary while any worker or sidecar is still active.

## Replan Triggers

Route to `Replan?` when two agents need the same write scope; a worker finds missing, stale, or contradictory specs; user-owned changes exist inside a write scope; validation failure cannot be isolated to one scope; integration needs an unplanned design, security, release, architecture, documentation-ownership, or product decision; an agent edits outside scope; outputs conflict; review or verification findings affect another active scope; gate decisions conflict; a shared file must move from coordinator ownership to a worker; or actual work expands beyond the approved plan, milestone, or user request.

## Remote Handoff

Before pushing, opening a pull request, or handing work to a remote reviewer, confirm the integrated local state is intended, record skipped/failed/blocked/cancelled slices, record validation evidence and gaps, include workflow state paths when relevant, and avoid release sequencing unless requested.

For delegated or integrated work, report the workflow mode, owned plan or slice, changed files, validation run, commit SHA or checkpoint, blockers, and final status or ready-for-integration status.
Interim progress updates must say they are interim.
