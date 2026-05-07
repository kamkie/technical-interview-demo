# Workflow Guide For AI Agents

`ai/WORKFLOW.md` owns branch, worktree, delegation, worker-log, integration, and remote-handoff mechanics.
It does not own plan creation, whole-plan execution loops, ad hoc task execution, validation scope, or release sequencing.

Use this file only when the work needs collaboration mechanics beyond one local agent editing one working tree.
Use `ai/PLAN_EXECUTION.md` for whole-plan execution and `ai/EXECUTION.md` for ad hoc or one-milestone work.

## Defaults

Use the smallest workflow that keeps ownership clear:

- one local agent on the current branch is enough for ordinary work
- keep current work committed or stashed before creating side branches or worktrees
- use unique worker branches for delegated work
- never rely on the same checked-out branch in more than one worktree at a time
- finish local validation before push or pull-request handoff unless the user explicitly asks for a remote-first diagnostic flow
- keep release sequencing out of workflow execution; use `ai/RELEASES.md` only after implementation is integrated and release is in scope

## When To Split Work

Split work only when the benefit is concrete and the user has approved delegation, parallelism, or a worktree-backed handoff.

Good split boundaries usually follow package, contract, or artifact ownership boundaries, for example:

- `business.book`
- `business.category`
- `business.localization`
- `business.user`
- `technical.security`
- one bounded public API change and its contract artifacts
- documentation-only slices when they do not overlap active source edits

Do not split work when workers would overlap on:

- the same controller or service
- the same integration test class
- the same REST Docs or OpenAPI artifact
- the same unresolved product or rollout decision
- one milestone that cannot be expressed as disjoint file ownership

## Delegated One-Plan Work

Use `ai/references/WORKFLOW_DELEGATED_PLAN.md` when one active plan has disjoint worker-safe slices but one coordinator still owns the plan, changelog, final validation, and integration order.

Coordinator-owned files usually include:

- the active plan file
- `CHANGELOG.md`
- roadmap entries
- shared contract artifacts unless the worker owns the entire contract slice
- worker-log integration notes

Workers should edit only their assigned files and report results through worker logs.

## Coordinated Multi-Plan Work

Use `ai/references/WORKFLOW_COORDINATED_PLANS.md` when separate approved active plans move in parallel and later need one integration pass.

Each worker owns one plan or one explicitly bounded plan slice.
The coordinator owns integration order, final validation, and folding accepted changelog text back into `CHANGELOG.md`.

## Worker Log Schema

For any delegated worker, create and maintain a committed temporary worker log at:

`ai/tmp/workflow/<plan_stem_or_topic>__<worker_name>.md`

Create the directory if it does not exist.

Each worker log records:

- target plan file or topic token
- worker branch and worktree
- exact owned scope or milestones
- files intentionally left shared
- changed files
- validation commands run and their pass or fail result
- proposed changelog text
- commit SHA(s)
- blockers, risks, and coordinator decisions still needed
- whether the current milestone or slice is ready for integration

Optional local-only progress notes can live at:

`ai/tmp/workflow-local/<plan_stem_or_topic>__<worker_name>.local.md`

Local notes may stay uncommitted and do not replace the committed worker log when one is required.

## Delegation Quality Bar

Delegated workers must meet the same repository standards as the coordinator.
Do not lower validation, review, documentation, artifact-update, or security expectations because work was delegated.

Workers inherit the coordinator capability by default.
Use an explicit model or reasoning override only when the user requests it or the task has a clear documented reason.
Do not delegate implementation, review, security, release, or ambiguous design work to a lower-capability worker.

Skills provide task-specific workflow instructions.
Using a skill does not reduce the required repository quality bar.

## Coordinator Ownership

The coordinator always owns:

- reading the governing docs, specs, and target plans
- deciding whether the work is worth splitting
- assigning explicit non-overlapping ownership
- deciding integration order
- final validation from `ai/TESTING.md`
- final review and documentation alignment using `ai/REVIEWS.md` and `ai/DOCUMENTATION.md`
- waiting for every worker to reach a terminal state before declaring delegated work complete

Treat these as terminal states:

- completed under the assigned workflow
- blocked with the blocker recorded clearly
- failed with the failure recorded clearly
- cancelled because the user explicitly stopped that worker

Workers are still active while they are implementing, validating, pushing, opening a pull request, or waiting on follow-up work that has not been recorded as blocked.

## Branch And Worktree Rules

- `main` remains the integration target for completed work
- worktree branches are temporary execution branches, not release branches
- release-target and tagging preconditions live in `AGENTS.md` and `ai/RELEASES.md`
- start delegated work from a committed or stashed state so branches or worktrees are comparable and reviewable
- use unique branch names for forked work
- prefer merging accepted branches or pull requests into the integration branch
- use cherry-pick only when the user asks for it, when accepting less than the full branch or pull request, or when a normal merge is not viable, and record the reason

## Reporting

For delegated or integrated work, report:

- workflow shape used
- owned plan or slice
- changed files
- validation run
- commit SHA(s)
- blockers
- final status or ready-for-integration status

Interim progress updates must say they are interim.
Do not give a final success summary while any worker is still active.
