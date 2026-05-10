# Workflow Guide For AI Agents

`.agents/references/workflow.md` owns branch, worktree, delegation, multi-agent state, sidecar, integration, and remote-handoff mechanics.
It does not own plan creation, whole-plan execution loops, ad hoc task execution, validation scope, review policy, or release sequencing.

Use this file only when work needs collaboration mechanics beyond one local agent editing one working tree, or when an approved plan names workflow state, delegation, worktrees, sidecars, integration queues, or remote handoff.
Use `.agents/references/plan-execution.md` for whole-plan execution, `.agents/references/execution.md` for ad hoc or one-milestone work, `.agents/references/testing.md` for validation scope, `.agents/references/reviews.md` for review expectations, and `.agents/references/releases.md` only after implementation is integrated and release work is in scope.

## Defaults

Use the smallest workflow that keeps ownership clear:

- for work without a created plan, infer the mode from the current request, write-scope boundaries, risk, and execution environment
- for planned work, use the execution shape chosen during planning; revise the plan before splitting work if that shape is missing, stale, or contradicted by execution reality
- use actual delegation only when the current user request, inferred no-plan mode or approved plan, and execution environment allow it
- keep current work committed or stashed before creating side branches or worktrees
- use unique worker branches for delegated work
- never rely on the same checked-out branch in more than one worktree at a time
- name shared files before workers start, and keep them coordinator-owned unless the plan explicitly assigns them
- finish local validation before push or pull-request handoff unless the user explicitly asks for a remote-first diagnostic flow
- keep release sequencing out of workflow execution

## Roles

- **Coordinator**: owns workflow shape, plan routing, shared files, integration order, conflict resolution, final validation, and final reporting.
- **Worker Agent**: performs a bounded implementation, test, documentation, or investigation slice within an explicit write scope.
- **Reviewer Agent**: reviews or challenges another output without taking ownership of the original write scope.
- **Verifier Agent**: independently runs or reproduces validation and reports whether evidence is conclusive.
- **Specialist Agent**: reviews a domain such as security, accessibility, performance, documentation, release, migration, or operations.
- **Write Scope**: files, directories, modules, or artifacts an agent may edit.
- **Read Scope**: context an agent may inspect.
- **Handoff Packet**: assignment contract given before delegated work starts.
- **Agent Result**: completion report returned by an agent.
- **Integration Queue**: ordered completed outputs awaiting coordinator review and integration.

Exactly one coordinator owns each multi-agent task.
Each delegated activity has one accountable agent.
Two editing agents must not own the same write scope at the same time.

## Execution Modes

| Mode | Use When | Concurrency |
| --- | --- | --- |
| `M0: solo` | Delegation is not useful or not allowed. | One agent. |
| `M1: sidecar-readonly` | A read-only scan, review, or verification can help without editing files. | Coordinator plus read-only sidecar. |
| `M2: bounded-worker` | One worker can edit one clear write scope. | Coordinator plus one worker. |
| `M3: parallel-sliced` | Multiple disjoint edit scopes can run in parallel. | Coordinator plus workers. |
| `M4: full-sidecar` | M3 plus independent review, verification, or specialist gates. | Coordinator plus workers and sidecars. |

For work without a created plan, infer and use the lowest sufficient mode from the current request.
No-plan inference may select any `M0` through `M4` mode when the request, ownership boundaries, stop conditions, validation targets, and any sidecar gates are clear enough to brief agents without inventing scope; when they are not clear, route the work to planning instead of clamping to `M0`.
For planned work, do not re-infer the mode at execution time: use the execution shape selected during planning and route to `Replan?` if it no longer fits.
When multiple approved plans are selected for one execution run, strongly prefer `M3: parallel-sliced`; choose a lower mode only when the selected plans must run serially or cannot be safely split, and choose `M4` only when sidecar gates are part of the planned workflow.

## When To Split Work

Split work only when the benefit is concrete and the inferred no-plan mode or approved plan allows delegation, parallelism, or a worktree-backed handoff.

Good split boundaries usually follow package, contract, or artifact ownership boundaries, for example:

- `business.book`
- `business.category`
- `business.localization`
- `business.user`
- `technical.security`
- one bounded public API change and its contract artifacts
- documentation-only slices when they do not overlap active source edits
- one approved plan per worker when multiple plans are intentionally coordinated

Do not split work when workers would overlap on:

- the same controller or service
- the same integration test class
- the same REST Docs or OpenAPI artifact
- the same roadmap, changelog, or plan status rows
- the same unresolved product, rollout, security, release, architecture, or ownership decision
- one milestone that cannot be expressed as disjoint file ownership

## State Layout

Use `.agents/context/*` for durable workflow state that must survive context switches, integration, or remote handoff.
Create the narrowest useful state file; short-lived handoffs may stay in conversation when no durable state is needed.

Canonical workflow state paths:

- `.agents/context/handoffs/` for durable handoff packets
- `.agents/context/workers/` for worker status, worker results, and proposed changelog text
- `.agents/context/reviews/` for reviewer sidecars and code-review gates
- `.agents/context/verifications/` for verifier sidecars and validation gates
- `.agents/context/specialists/` for security, docs, release, operations, migration, accessibility, performance, or other specialist gates

Use stable names such as `<plan_stem_or_topic>__<agent_name>.md`.
Committed workflow state is required when another agent, a later coordinator pass, or a remote handoff depends on it.
Conversation-only notes are acceptable only when they are not needed as durable integration evidence.

Each state file should record:

- target plan file or topic token
- assigned role and activity
- branch, worktree, or sandbox context
- read scope and write scope
- shared files intentionally excluded from the agent's write scope
- expected output and validation target
- changed files
- validation commands run and pass, fail, skipped, or inconclusive result
- proposed changelog text when the work would need unreleased history later
- commit SHA or checkpoint when available
- blockers, risks, and coordinator decisions still needed
- readiness for integration or the reason it is not ready

## Handoff Packet

Every delegated activity receives a handoff packet before work starts.
The packet may live in conversation for short-lived local delegation, or under `.agents/context/handoffs/` when durable state is needed.

A handoff packet states:

- activity and objective
- source request, active plan, or governing artifact
- assigned role and state file path
- queue order when integration, review, verification, or specialist gates are ordered
- required context
- read scope, write scope, and out-of-scope files
- expected output and validation
- stop conditions
- branch, worktree, or sandbox context
- allowed skills when a skill is part of the assignment
- reporting format

If the handoff cannot name clear inputs, outputs, write scope, and stop conditions, route to `Replan?` instead of delegating.

## Agent Result

Each delegated agent reports:

- activity completed
- files changed
- decisions made
- validation run and result
- known risks
- follow-up needed
- ready for integration: `yes` or `no`

Failed validation reports include the failing command, summary, and recommended next activity: `Diagnose?`, `Fix?`, or `Replan?`.
Blocked, failed, or cancelled work must be recorded explicitly in the relevant state file or plan before the coordinator reports final status.

## One-Plan Delegation

Use one-plan delegation when one approved active plan remains the canonical source of truth and worker-safe slices are disjoint.

Preconditions:

- the active plan is approved and decision-complete
- the plan identifies worker-safe ownership boundaries
- current work is committed or stashed before workers branch
- each worker has an explicit write scope and validation target
- coordinator-owned shared files are named before workers start

The coordinator normally owns:

- the active plan file
- `ROADMAP.md`
- `CHANGELOG.md`
- shared specs, contract artifacts, and docs unless one worker owns the entire slice
- workflow state and integration notes
- final validation and final review

Workers should avoid coordinator-owned files unless the coordinator explicitly assigns them.

The coordinator loop is:

1. Keep the canonical plan current.
2. Review worker state and agent results as workers finish.
3. Integrate accepted branches in a deliberate order.
4. Resolve conflicts without discarding unrelated user or worker changes.
5. Run final validation from `.agents/references/testing.md`.
6. Update the canonical plan, roadmap, and changelog when applicable.
7. Report every worker's terminal state before declaring the run complete.

## Coordinated Multi-Plan Work

Use coordinated multi-plan work when separate approved active plans move in parallel and later need one integration pass.
When multiple plans are selected for execution together, use `M3: parallel-sliced` as the preferred baseline unless dependencies or shared artifacts force serial execution, or planned sidecar gates require `M4`.

Preconditions:

- every selected active plan is approved and decision-complete
- each worker owns exactly one plan or one explicitly bounded plan slice
- selected plans do not compete for the same source files, specs, public contract artifacts, roadmap rows, changelog entries, or release decisions
- current work is committed or stashed before worktrees branch
- the coordinator has recorded integration order and shared artifacts

Workers do not edit canonical `CHANGELOG.md` unless the coordinator assigns it.
Each worker keeps proposed unreleased text in `.agents/context/workers/` or in a temporary root file named `CHANGELOG_<topic>.md` when the coordinator explicitly assigns that file.
The coordinator folds accepted text into `CHANGELOG.md` during integration and removes temporary changelog files in the same integration change.

The coordinator:

1. Tracks all selected plans and workflow state.
2. Waits for every worker to reach a terminal state.
3. Reviews accepted outputs in the planned integration order.
4. Merges or otherwise integrates accepted branches.
5. Folds accepted changelog text into canonical `CHANGELOG.md`.
6. Runs final validation from `.agents/references/testing.md`.
7. Updates roadmap and plan lifecycle states.
8. Reports completed, blocked, failed, or cancelled workers explicitly.

## Sidecar Gates

Use sidecars for independent read-only review, verification, or specialist checks.
Sidecars may inspect active work, but they must not edit worker-owned files unless the plan changes and ownership is reassigned.

`M4` plans include all `M3` requirements plus:

- review, verification, and specialist queues
- gate decision table
- conflict handling
- sidecar stop conditions
- approval authority for each gate

Common gates:

- `Code Review`: approve or request changes
- `Verification`: pass, fail, or report inconclusive validation
- `Security Review?`: approve, request changes, or record accepted risk when security triggers fire
- `Docs Review?`: approve or request changes for user-facing or contract-heavy docs
- `Release/Operations Gate?`: approve release-facing work when release or deployment paths are affected

Sidecar output never replaces the coordinator's integration responsibility.
The coordinator reviews each sidecar result, decides follow-up, and records the gate outcome in the plan or state file that owns the work.

## Branch And Worktree Rules

- `main` remains the integration target for completed work
- worktree branches are temporary execution branches, not release branches
- release-target and tagging preconditions live in `AGENTS.md` and `.agents/references/releases.md`
- start delegated work from a committed or stashed state so branches or worktrees are comparable and reviewable
- use unique branch names for forked work
- do not check out the same branch in more than one worktree at a time
- prefer merging accepted branches or pull requests into the integration branch
- use cherry-pick only when the user asks for it, when accepting less than the full branch or pull request, or when a normal merge is not viable, and record the reason
- after integration, remove temporary worktrees and branches only when they are no longer needed for review or remote handoff

## Integration Rules

- the coordinator reviews every agent result before integration
- integrate one result at a time unless outputs are known independent
- validate the affected surface after each integration step when the validation cost is proportional
- re-brief affected agents when integration changes their assumptions
- route behavior conflicts to `Replan?`
- run final validation from the integrated repository state
- if no build or test command ran, report the narrow checks that actually ran and why broader validation was skipped
- check `git status --short` before delegation and after integration

Treat these as terminal states:

- completed under the assigned workflow
- blocked with the blocker recorded clearly
- failed with the failure recorded clearly
- cancelled because the user explicitly stopped that worker

Workers are still active while they are implementing, validating, pushing, opening a pull request, or waiting on follow-up work that has not been recorded as blocked.
Do not declare delegated work complete until every worker and sidecar has a terminal state.

## Replan Triggers

Route to `Replan?` when:

- two agents need the same write scope
- a worker finds missing, stale, or contradictory specs
- user-owned changes exist inside a write scope
- validation failure cannot be isolated to one scope
- integration needs a design, security, release, architecture, documentation-ownership, or product decision not in the plan
- an agent edits outside scope
- outputs conflict
- review or verification findings affect another active scope
- gate decisions conflict
- a shared file must move from coordinator ownership to a worker
- actual work expands beyond the approved plan, milestone, or user request

## Remote Handoff

Before pushing, opening a pull request, or handing work to a remote reviewer:

- confirm the integrated local state is the intended handoff state
- record skipped, failed, blocked, or cancelled slices explicitly
- record validation evidence and any known gaps
- include workflow state paths when they are part of the handoff
- avoid release sequencing unless the user requested release work

For delegated or integrated work, report:

- workflow mode used
- owned plan or slice
- changed files
- validation run
- commit SHA or checkpoint
- blockers
- final status or ready-for-integration status

Interim progress updates must say they are interim.
Do not give a final success summary while any worker or sidecar is still active.
