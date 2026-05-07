# Task Library For This Repository

`ai/TASK_LIBRARY.md` is the repository-local catalog of reusable task starters.
It is not standing policy and it is not a task runner. Use owner guides for rules, and use the task sections below only after a task title is invoked or a user asks for a reusable starter.

## Design Decision

- `ai/task-library/bodies/` was removed because separate body files added maintenance churn without improving task execution.
- `ai/task-library/index.json` and `scripts/ai/get-task.ps1` were removed because a custom index and loader were efficient enough computationally but unnecessary for this repository-sized catalog.
- The source of truth is now this file: task titles, placeholders, and task text live together under stable Markdown headings.
- Codex-native reusable workflows should become plugin-packaged skills when they need installable discovery, scripts, references, or reliable implicit invocation; `.agents/plugins/marketplace.json` is only for registering a repo-scoped plugin marketplace.

## Owner Guides

This file does not own standing policy. Use the owner guides instead:

- planning: `ai/PLANNING.md`
- whole-plan execution: `ai/PLAN_EXECUTION.md`
- ad hoc task or one-milestone execution: `ai/EXECUTION.md`
- workflow delegation and integration: `ai/WORKFLOW.md`
- validation: `ai/TESTING.md`
- artifact routing: `ai/DOCUMENTATION.md`
- review: `ai/REVIEWS.md`
- release: `ai/RELEASES.md`
- local command wrapper usage: `ai/ENVIRONMENT_QUICK_REF.md`

## Using A Task

- the task titles listed below are reusable commands
- use the exact title or an unmistakably close reference
- include required placeholders such as `<topic>`, `<plan_file>`, `<milestone_name>`, `<task>`, or `<constraint>`
- if the title or required context is ambiguous, ask a targeted clarification question
- load only the matching task section, using heading search instead of opening unrelated task text

Useful local searches:

```powershell
rg -n "^## Task Index|^### " ai/TASK_LIBRARY.md
rg -n "^### Create Plan$" ai/TASK_LIBRARY.md
```

## Task Index

### Discovery And Roadmap
- `Clarify Roadmap Decisions`
- `Pick Next Roadmap Workstream`
- `Prioritize Open Security And Quality Issues In Roadmap`
- `Refine Roadmap Intake`
- `Review Roadmap Item` (<task>)

### Planning
- `Create Plan` (<topic>)
- `Plan Checked Roadmap Items` (<topic>)
- `Plan Dependency And Build Tool Upgrade` (<item 1>, <item 2>, <topic>)
- `Plan From Roadmap` (<task 1>, <task 2>, <topic>)
- `Plan Repository-Wide Dependency And Toolchain Upgrade Sweep` (<topic>)
- `Revise Plan` (<constraint>, <plan_file>)
- `Split Checked Roadmap Items Into Plans` (<topic>)

### Plan Verification
- `Choose Execution Shape` (<plan_file>)
- `Review Plan Readiness` (<plan_file>)

### Implementation
- `Implement Milestone` (<milestone_name>, <plan_file>)
- `Implement Plan` (<plan_file>)

### Workflow Execution
- `Check Active Workers` (<topic>)
- `Check Worker Status` (<topic>, <worker name or agent id>)
- `Coordinate Multiple Plans` (<plan_file_1>, <plan_file_2>, <plan_file_3>, <topic>)
- `Delegate Plan Slices` (<plan_file>, <plan_stem_or_topic>, <worker_name>)
- `Execute Plan Locally` (<plan_file>)
- `Run All Ready Plans` (<topic>)
- `Run All Unfinished Plans` (<topic>)
- `Run Plan With Chosen Shape` (<plan_file>)

### Implementation Integration
- `Integrate All Open PRs` (<topic>)
- `Integrate Delegated Plan Work` (<plan_file>)
- `Integrate Multiple Plan Workstreams` (<topic>)

### Implementation Verification
- `Check Contract Impact` (<change>)
- `Review Diff Risks`
- `Run Required Validation` (<change>, <plan_file>)
- `Verify Milestone` (<milestone_name>, <plan_file>)

### Preparing Release
- `Check Release Readiness` (<plan_file>)
- `Prepare Release`

### Releasing
- `Check Published Release` (<plan_file>)
- `Push Prepared Release`
- `Release All Merged Work`

### Lifecycle And Maintenance
- `Clean Worktrees And Stale Local Branches`
- `Compact AI Docs`
- `Context Report`
- `Deep AI Guidelines Assessment`
- `Evaluate AI Guidelines`
- `Implement Then Release` (<plan_file>)
- `Summarize Lifecycle State` (<plan_file>)
- `Triage Validation Failure` (<change>, <plan_file>)
- `Upgrade Dependencies And Build Tools` (<item 1>, <item 2>)

## Task Definitions

### Clarify Roadmap Decisions

Category: Discovery And Roadmap
Slug: `clarify-roadmap-decisions`
Placeholders: none

Review `ROADMAP.md` for project-framing decisions that must be made before roadmap items can be planned or sequenced confidently.

Read `AGENTS.md`, `README.md`, `ROADMAP.md`, `ai/PLANNING.md`, and `ai/DESIGN.md` first.
Use repo truth to resolve what you can before asking anything.
Focus on decisions that shape product direction, client/auth posture, compatibility promises, rollout model, release posture, or other roadmap-defining project choices.
Ask the user only the remaining high-value framing questions, with concrete options and a recommended option for each one.
Explain why each decision matters, which roadmap items it blocks or reshapes, and what fallback would be used if the user does not decide yet.
If the roadmap is already sufficiently framed, say so explicitly and do not manufacture questions.
After the interactive decisions are answered, update `ROADMAP.md` to reflect the resolved direction and summarize the decisions that were locked.
Do not create an execution plan unless I ask.

### Pick Next Roadmap Workstream

Category: Discovery And Roadmap
Slug: `pick-next-roadmap-workstream`
Placeholders: none

Review `ROADMAP.md` and select the next coherent workstream to move toward planning.

Use `ai/PLANNING.md` lifecycle vocabulary.
If the candidate work is still only `Discovery`, say so explicitly and explain what is still missing before a real plan should be written.
If work is ready to move forward, name the exact roadmap items that should feed the next plan and explain why they belong together.
Do not create the plan yet unless I ask.

### Prioritize Open Security And Quality Issues In Roadmap

Category: Discovery And Roadmap
Slug: `prioritize-open-security-and-quality-issues-in-roadmap`
Placeholders: none

Use the repo-local skill `gh-fix-security-quality` to inspect all open GitHub Security and quality issues for this repository, then update `ROADMAP.md` to capture and prioritize them.

Read `AGENTS.md`, `ROADMAP.md`, `ai/PLANNING.md`, and `ai/skills/gh-fix-security-quality/SKILL.md` first.
Use the skill to inspect every open code-scanning and Dependabot alert, summarize the actionable issue families, and turn them into concrete roadmap entries.
Prioritize the roadmap by severity, exploitability, release risk, and batching efficiency.
Group repeated alert families into one roadmap batch when that is the better execution unit than one item per alert.
Say explicitly which alerts were grouped together, which stayed separate, and why.
Do not create an execution plan or implement fixes unless I ask.

### Refine Roadmap Intake

Category: Discovery And Roadmap
Slug: `refine-roadmap-intake`
Placeholders: none

Refine the items under `## Not Yet Refined` in `ROADMAP.md` into concrete roadmap entries.

Treat that section as rough intake only.
Move refined tasks only when the resulting roadmap wording is concrete enough to sequence.
Leave still-ambiguous tasks in place and explain what is missing.

### Review Roadmap Item

Category: Discovery And Roadmap
Slug: `review-roadmap-item`
Placeholders: <task>

Review the roadmap item `<task>` before planning implementation.

Explain what behavior would change, which current specs govern it, what is still ambiguous, and whether the task should remain in `Discovery`, move into `Planning`, or be treated as `Needs Input` under `ai/PLANNING.md`.

### Create Plan

Category: Planning
Slug: `create-plan`
Placeholders: <topic>

Create `ai/plans/PLAN_<topic>.md` for <topic>.

Follow `ai/PLANNING.md`.
Set the plan lifecycle using `ai/PLANNING.md` instead of guessing.
Update `ROADMAP.md` in the same change so active work points to the new plan path and current status.

### Plan Checked Roadmap Items

Category: Planning
Slug: `plan-checked-roadmap-items`
Placeholders: <topic>

Create one coherent `ai/plans/PLAN_<topic>.md` from every checklist item marked `[x]` in `ROADMAP.md`.

Use only the checked items unless the roadmap text makes a dependency explicit.
Restate exactly which checked items were included.
If the checked items do not form one coherent executable plan, stop and explain the gap instead of guessing.
Record unresolved requirement gaps and fallback assumptions explicitly.
Set the lifecycle state from `ai/PLANNING.md` based on actual planning readiness.
Update `ROADMAP.md` in the same change so the checked roadmap items point to the new plan path and current status.

### Plan Dependency And Build Tool Upgrade

Category: Planning
Slug: `plan-dependency-and-build-tool-upgrade`
Placeholders: <item 1>, <item 2>, <topic>

Create `ai/plans/PLAN_<topic>.md` for this dependency or build-tool upgrade batch:
- <item 1>
- <item 2>

Read `AGENTS.md`, `ai/PLANNING.md`, `ai/DOCUMENTATION.md`, `ai/TESTING.md`, the relevant build files, and the exact alert, version target, or tool output that motivates the upgrade first.
Identify where each version is actually owned before planning changes, including direct dependencies, transitive overrides, Gradle plugins, wrapper updates, `buildSrc`, workflow actions, or other build tooling.
Keep the plan narrow, call out compatibility and validation risk explicitly, name the resolved-version proof expected during execution, and say whether the work should stay one reviewable batch or be split into smaller upgrade plans.

### Plan From Roadmap

Category: Planning
Slug: `plan-from-roadmap`
Placeholders: <task 1>, <task 2>, <topic>

Create `ai/plans/PLAN_<topic>.md` from this roadmap input:
- <task 1>
- <task 2>

Treat `ROADMAP.md` only as roadmap input and follow `ai/PLANNING.md`.
Set the lifecycle state from `ai/PLANNING.md` based on the actual readiness of the work.
Update `ROADMAP.md` in the same change so the source roadmap item points to the new plan path and current status.

### Plan Repository-Wide Dependency And Toolchain Upgrade Sweep

Category: Planning
Slug: `plan-repository-wide-dependency-and-toolchain-upgrade-sweep`
Placeholders: <topic>

Create `ai/plans/PLAN_<topic>.md` for a repository-wide dependency and toolchain upgrade sweep.

Read `AGENTS.md`, `ai/PLANNING.md`, `ai/DOCUMENTATION.md`, `ai/TESTING.md`, the relevant build files, Dockerfiles, workflow files, and any current alert, scan, or dependency-report output first.
Inventory all directly owned version surfaces before planning changes, including application dependencies, Gradle plugins, the Gradle wrapper, `buildSrc`, Docker base images, GitHub Actions, and other checked-in build or packaging tools.
Propose the smallest reviewable execution shape that still upgrades the full owned surface, and say explicitly if the work is too broad for one plan and should be split into multiple `ai/plans/PLAN_*.md` files.
For each planned batch, call out compatibility risk, rollback or migration concerns, the exact resolved-version proof expected during execution, and the validation needed to keep the upgraded repo release-ready.

### Revise Plan

Category: Planning
Slug: `revise-plan`
Placeholders: <constraint>, <plan_file>

Revise `<plan_file>` for this new requirement or constraint:
<constraint>

Keep the plan self-contained and follow `ai/PLANNING.md`.
Update the lifecycle state only as far as the revision justifies.
If the revision changes active-work status, scope, sequencing, or plan path ownership, update `ROADMAP.md` in the same change.

### Split Checked Roadmap Items Into Plans

Category: Planning
Slug: `split-checked-roadmap-items-into-plans`
Placeholders: <topic>

Create one or more `ai/plans/PLAN_<topic>.md` files from every checklist item marked `[x]` in `ROADMAP.md`.

Split only genuinely disjoint workstreams that can later execute in parallel without overlapping source ownership, contract artifacts, rollout order, or validation.
If the checked items form only one coherent plan, stop and say that the single-plan planning task should be used instead.
Record requirement gaps, fallback assumptions, and any cross-plan dependency notes in each created plan.
Set the lifecycle state for each plan from `ai/PLANNING.md`.
Update `ROADMAP.md` in the same change so every resulting active roadmap item points to its plan path and current status.

### Choose Execution Shape

Category: Plan Verification
Slug: `choose-execution-shape`
Placeholders: <plan_file>

Review `<plan_file>` and decide which execution shape fits best.

Use `ai/PLANNING.md`; add `ai/WORKFLOW.md` only if delegation, worktrees, worker logs, or integration mechanics are relevant.
Base the decision on the plan's `Execution Shape And Shared Files`, milestone boundaries, shared-file ownership, and whether the work should stay one active plan or coordinate multiple active plans.
Say explicitly whether the work should use one local branch, delegated one-plan work, or coordinated multi-plan work.
If the plan is not clear enough to choose safely, stop and name the ambiguity instead of guessing.

### Review Plan Readiness

Category: Plan Verification
Slug: `review-plan-readiness`
Placeholders: <plan_file>

Review `<plan_file>` against `ai/PLANNING.md`.

List concrete gaps or ambiguities first.
Check whether the lifecycle phase and status are accurate.
Say explicitly if the plan is ready.

### Implement Milestone

Category: Implementation
Slug: `implement-milestone`
Placeholders: <milestone_name>, <plan_file>

Implement only `<milestone_name>` from `<plan_file>`.

Follow `ai/EXECUTION.md`.
Do not start later milestones, push, open a PR, or release unless I ask.

### Implement Plan

Category: Implementation
Slug: `implement-plan`
Placeholders: <plan_file>

Implement `<plan_file>`.

Follow `ai/PLAN_EXECUTION.md`.
Do not push, open a PR, or release unless I ask.
### Check Active Workers

Category: Workflow Execution
Slug: `check-active-workers`
Placeholders: <topic>

Check the status of the active workers in the current workflow execution.

Use `ai/WORKFLOW.md`.
For each worker, report workflow shape, owned plan or slice, branch and worktree, current task, changed files, validations, commit SHA(s), blockers, ready-for-integration status, PR status, and the worker-log path.
For delegated one-plan work, include any shared files intentionally left to the coordinator.
For coordinated multi-plan work, include the `CHANGELOG_<topic>.md` path.
Keep the report concise and factual.

### Check Worker Status

Category: Workflow Execution
Slug: `check-worker-status`
Placeholders: <topic>, <worker name or agent id>

Check the status of worker `<worker name or agent id>` in the current workflow execution.

Report workflow shape, owned plan or slice, branch and worktree, current progress, changed files, validations run with results, commit SHA(s), blockers, ready-for-integration status, PR status, and the worker-log path.
For delegated one-plan work, also note which shared files were intentionally left to the coordinator.
For coordinated multi-plan work, also include the `CHANGELOG_<topic>.md` path.
If the worker has stalled or completed, state that clearly.

### Coordinate Multiple Plans

Category: Workflow Execution
Slug: `coordinate-multiple-plans`
Placeholders: <plan_file_1>, <plan_file_2>, <plan_file_3>, <topic>

Coordinate these active plan files using git worktrees:
- <plan_file_1>
- <plan_file_2>
- <plan_file_3>

Use `ai/PLAN_EXECUTION.md`, `ai/WORKFLOW.md`, and `ai/references/WORKFLOW_COORDINATED_PLANS.md`.
Track per-plan branch, validation, private `CHANGELOG_<topic>.md`, worker log, and PR status.
Treat each worker branch as complete only when local validation is done and the branch has been pushed with a PR open or already merged, matching `ai/WORKFLOW.md` and `AGENTS.md`.
Keep the coordinator active until every worker reaches a terminal state and summarize all worker outcomes together; do not finish when only one worker is done.
If any listed plans are too coupled for safe parallel execution, stop and explain why instead of forcing the split.
Do not release unless I ask.

### Delegate Plan Slices

Category: Workflow Execution
Slug: `delegate-plan-slices`
Placeholders: <plan_file>, <plan_stem_or_topic>, <worker_name>

Execute delegated worker-owned slices for `<plan_file>`.

Use `ai/PLAN_EXECUTION.md`, `ai/WORKFLOW.md`, and `ai/references/WORKFLOW_DELEGATED_PLAN.md`.
Act as coordinator, create worker branches or worktrees only for disjoint slices, keep shared files coordinator-owned, and require committed worker logs at `ai/tmp/workflow/<plan_stem_or_topic>__<worker_name>.md`.
Keep the coordinator active until every worker reaches a terminal state and summarize all workers at the end; progress reports before that are interim only.
Push only the finished coordinator branch and open one final PR unless I explicitly ask otherwise.

### Execute Plan Locally

Category: Workflow Execution
Slug: `execute-plan-locally`
Placeholders: <plan_file>

Execute `<plan_file>` on one local branch.

Use `ai/PLAN_EXECUTION.md`; add `ai/WORKFLOW.md` only if a branch or remote handoff decision is needed.
Treat the canonical plan file and `CHANGELOG.md` as directly editable on the active branch.
Do not push, open a PR, or release unless I ask.

### Run All Ready Plans

Category: Workflow Execution
Slug: `run-all-ready-plans`
Placeholders: <topic>

Select every ready plan file under `ai/plans/`, then execute the selected set using the same flow as `Coordinate Multiple Plans`.

Treat ready plans as the non-archived `ai/plans/PLAN_*.md` files whose `Lifecycle` status is `Ready`.
Restate exactly which ready plan files were selected and which non-archived plan files were skipped because they were not `Ready`.
If there are no ready plans, stop and say so explicitly.
If only one ready plan exists, stop and say that a single-plan execution task should be used instead.
Do not silently skip a `Ready` plan just to force a smaller parallel-safe set.
If any ready plans are too coupled for safe parallel execution, stop and explain why instead of forcing a split.
Then execute the selected plan files using coordinated multi-plan worktrees.
Use `ai/WORKFLOW.md` and `ai/EXECUTION.md`.
Track per-plan branch, validation, private `CHANGELOG_<topic>.md`, worker log, and PR status.
Treat each worker branch as complete only when local validation is done and the branch has been pushed with a PR open or already merged, matching `ai/WORKFLOW.md` and `AGENTS.md`.
Keep the coordinator active until every worker reaches a terminal state and summarize all worker outcomes together; do not finish when only one worker is done.
Do not release unless I ask.

### Run All Unfinished Plans

Category: Workflow Execution
Slug: `run-all-unfinished-plans`
Placeholders: <topic>

Select every unfinished plan file under `ai/plans/`, then execute the selected set using the same flow as `Coordinate Multiple Plans`.

Treat unfinished plans as the non-archived `ai/plans/PLAN_*.md` files.
If there are no unfinished plans, stop and say so explicitly.
If only one unfinished plan exists, stop and say that a single-plan execution task should be used instead.
Do not silently skip an unfinished plan just to force a smaller parallel-safe set.
If any unfinished plans are too coupled for safe parallel execution, stop and explain why instead of forcing a split.
Then execute the selected plan files using coordinated multi-plan worktrees.
Use `ai/WORKFLOW.md` and `ai/EXECUTION.md`.
Track per-plan branch, validation, private `CHANGELOG_<topic>.md`, worker log, and PR status.
Treat each worker branch as complete only when local validation is done and the branch has been pushed with a PR open or already merged, matching `ai/WORKFLOW.md` and `AGENTS.md`.
Keep the coordinator active until every worker reaches a terminal state and summarize all worker outcomes together; do not finish when only one worker is done.
Do not release unless I ask.

### Run Plan With Chosen Shape

Category: Workflow Execution
Slug: `run-plan-with-chosen-shape`
Placeholders: <plan_file>

Execute `<plan_file>` using the workflow rules.

Use `ai/PLAN_EXECUTION.md`; add `ai/WORKFLOW.md` when the plan requires delegation, worktrees, worker logs, or integration handoff.
Infer one local branch or delegated one-plan work from the plan file, especially `Execution Shape And Shared Files`, milestone boundaries, and shared-file ownership.
If delegated work is selected, keep the coordinator active until every worker reaches a terminal state; do not finish the run when only some workers are done.
If the plan file is not clear enough to choose safely, stop and explain the ambiguity instead of guessing.
If the work actually needs coordinated multi-plan execution, stop and say that explicitly instead of guessing.

### Integrate All Open PRs

Category: Implementation Integration
Slug: `integrate-all-open-prs`
Placeholders: <topic>

Integrate all open implementation PRs that are ready.

Use `AGENTS.md`, `ai/WORKFLOW.md`, `ai/EXECUTION.md`, `ai/DOCUMENTATION.md`, `ai/TESTING.md`, and `ai/REVIEWS.md` as coordinator.
Discover every currently open PR with the GitHub CLI or repository remote, then review each PR branch, plan reference, changed files, checks, review state, conflicts, and relationship to the active unreleased work.
Say which open PRs are in scope, which are not implementation PRs, which are not ready, and why.
For ready in-scope PRs, merge the accepted output onto the integration branch in dependency order by default, using cherry-pick only when accepting less than a full PR, when I ask for it, or when a normal merge is not viable.
Fold accepted `CHANGELOG_<topic>.md` entries into `CHANGELOG.md`, update any canonical plan files or shared artifacts the accepted PRs require, clean consumed local worker branches or worktrees under `ai/WORKFLOW.md`, and delete consumed private changelog files and worker logs before the final push or PR unless I explicitly want them retained.
Run the required integration validation, then summarize what landed, what was skipped, what remains open, any cherry-pick reason, and any follow-up PRs or blockers.
If there are no in-scope open PRs, say so explicitly and stop.

### Integrate Delegated Plan Work

Category: Implementation Integration
Slug: `integrate-delegated-plan-work`
Placeholders: <plan_file>

Integrate completed worker output for `<plan_file>`.

Use `ai/WORKFLOW.md` and `ai/references/WORKFLOW_DELEGATED_PLAN.md` as coordinator.
Merge ready worker branches by default, using cherry-pick only when accepting less than a full worker branch, when I ask for it, or when a normal merge is not viable.
Fold accepted worker-log content into the canonical plan and `CHANGELOG.md`, clean consumed local worker branches or worktrees under `ai/WORKFLOW.md`, run the required integration validation, delete consumed worker logs before the final push or PR unless I explicitly want them retained, and summarize what landed, what remains, and any cherry-pick reason.

### Integrate Multiple Plan Workstreams

Category: Implementation Integration
Slug: `integrate-multiple-plan-workstreams`
Placeholders: <topic>

Integrate completed worker output for all coordinated plan branches.

Use `ai/WORKFLOW.md` and `ai/references/WORKFLOW_COORDINATED_PLANS.md` as coordinator.
Review which merged plans are in scope, say explicitly which merged plans were included and which were skipped, integrate any accepted worker output that is not yet on the integration branch, fold accepted `CHANGELOG_<topic>.md` entries into `CHANGELOG.md`, update any canonical plan files or shared artifacts the accepted merged plans require, clean consumed local worker branches or worktrees under `ai/WORKFLOW.md`, delete consumed private changelog files and worker logs before the final push or PR unless I explicitly want them retained, run the required integration validation, and summarize what landed, what was skipped, and what remains.
If there are no in-scope coordinated plan branches, say so explicitly and stop.

### Check Contract Impact

Category: Implementation Verification
Slug: `check-contract-impact`
Placeholders: <change>

Review `<change>` for contract and artifact-routing impact.

Use `AGENTS.md`, `ai/DOCUMENTATION.md`, and `ai/TESTING.md`.
State whether it requires updates to governing tests, published contract docs, approved OpenAPI, HTTP examples, `README.md`, maintainer docs, roadmap or release artifacts, or benchmark and compatibility checks.
If the change is internal-only, say that explicitly and explain why.

### Review Diff Risks

Category: Implementation Verification
Slug: `review-diff-risks`
Placeholders: none

Review this change with a code review mindset.

Use `ai/REVIEWS.md`.
List findings first, ordered by severity, with file references.
Keep the summary brief.

### Run Required Validation

Category: Implementation Verification
Slug: `run-required-validation`
Placeholders: <change>, <plan_file>

Run only the required validation for `<plan_file>` or `<change>`.
Do not edit files.

Use `ai/TESTING.md` and `ai/DOCUMENTATION.md`.
Use `./build.ps1 compileJava` or a similarly focused task for quick loop checks, and use `./build.ps1 build` for final verification.
Do not run overlapping Gradle validation tasks in parallel, including `build` with `gatlingBenchmark`, `externalSmokeTest`, `externalDeploymentCheck`, or `scheduledExternalCheck`.
Use `pwsh ./scripts/classify-changed-files.ps1` directly only when another diff boundary is explicit.
Summarize what ran, what passed, what failed, what was skipped, and what artifacts would likely need updates.

### Verify Milestone

Category: Implementation Verification
Slug: `verify-milestone`
Placeholders: <milestone_name>, <plan_file>

Verify `<milestone_name>` from `<plan_file>` after implementation.

Use `ai/TESTING.md`, `ai/DOCUMENTATION.md`, and `ai/REVIEWS.md`.
Check validation coverage, artifact updates, milestone commit completeness, and any remaining blocker before the next milestone or integration step.
List blockers first.

### Check Release Readiness

Category: Preparing Release
Slug: `check-release-readiness`
Placeholders: <plan_file>

Review release readiness for `<plan_file>`.

Use `ai/RELEASES.md` and `ai/DOCUMENTATION.md`.
List blockers first.
Say explicitly if the repository is release-ready.

### Prepare Release

Category: Preparing Release
Slug: `prepare-release`
Placeholders: none

Prepare the release candidate for all merged but unreleased work currently on `main`.

Follow `ai/RELEASES.md` and `ai/DOCUMENTATION.md`.
Only proceed if the approved implementation PR is already merged onto `main`.
Include every merged and executed `ai/plans/PLAN_*.md` file that belongs to the unreleased change set.
If any included work is not release-ready, stop and list blockers first instead of preparing a partial release.
If the merged work is ready, prepare the release commit, archive every included executed plan under `ai/archive/`, create the annotated tag locally, and summarize exactly which merged PRs and executed plan files were included.
Do not push unless I ask.

### Check Published Release

Category: Releasing
Slug: `check-published-release`
Placeholders: <plan_file>

Verify the already pushed release for `<plan_file>`.

Follow `ai/RELEASES.md` for the post-push checks.
Summarize exactly what was published.

### Push Prepared Release

Category: Releasing
Slug: `push-prepared-release`
Placeholders: none

Push the current release commit and annotated tag to the remote.

Follow `ai/RELEASES.md` for push and post-push verification.

### Release All Merged Work

Category: Releasing
Slug: `release-all-merged-work`
Placeholders: none

Verify and release all merged but unreleased work currently on `main`.

Use `ai/RELEASES.md`.
Include every merged PR and executed `ai/plans/PLAN_*.md` file that belongs to the unreleased change set.
If any included work is not release-ready, stop and list blockers first instead of preparing a partial release.
If the merged work is ready, prepare the release commit, archive every included executed plan under `ai/archive/`, create the annotated tag, push the release commit and tag, and verify remote publication.
Summarize exactly which merged PRs and executed plan files were included.

### Clean Worktrees And Stale Local Branches

Category: Lifecycle And Maintenance
Slug: `clean-worktrees-and-stale-local-branches`
Placeholders: none

Clean local git worktrees and stale local branches after completed or abandoned workflow execution.

Use `ai/WORKFLOW.md`.
Review the current local worktrees and branches first.
Remove temporary worktrees that are no longer needed, delete stale local worker branches that are not needed for an open PR or other requested follow-up, and leave any retained branch or worktree in a clean local state.
Do not delete remote branches, close PRs, or remove any branch or worktree that still has uncommitted or unmerged work unless I explicitly ask.
Summarize what was removed, what was retained, and any follow-up cleanup that is still blocked.

### Compact AI Docs

Category: Lifecycle And Maintenance
Slug: `compact-ai-docs`
Placeholders: none

Compact the standing AI instruction files.

#### Scope

- Read `AGENTS.md`, `ai/DOCUMENTATION.md`, `ai/TASK_LIBRARY.md`, and standing top-level owner guides under `ai/` first; exclude active `ai/plans/PLAN_*.md` files unless they are relevant to the compaction target.
- Do not bulk-load `ai/archive/`, `ai/references/`, `ai/templates/`, or `ai/skills/`; open them only when a cross-reference or overlapping policy points there.
- Check `WORKING_WITH_AI.md` only when human-facing workflow wording overlaps with the AI guidance being changed.

#### Compaction Targets

Look for any of the following, not only verbatim duplicates:

- exact or near-duplicate sentences across standing files
- overlapping or restated policies (same intent, different wording)
- guidance placed outside its owning file (per `ai/DOCUMENTATION.md` ownership rules)
- stale references (renamed/moved files, archived plans, retired tasks)
- verbose phrasing, redundant lists, or examples that belong in `ai/references/` or `ai/templates/`
- orphaned cross-references and broken anchors
- stale workflow references and anchors after delegated-work mechanics move between `ai/WORKFLOW.md` and `ai/references/`, checked with targeted `rg` searches rather than bulk-loading all references
- accumulated history inside standing guideline files: changelog notes, "previously…/now…" wording, migration narratives, dated decisions, deprecation traces, user-request history, prior directive wording, or rationale about past states rather than current rules
- misplaced historical context: released history belongs in `CHANGELOG.md`, durable lessons in `ai/LEARNINGS.md`, completed plans in `ai/archive/`, and still-useful guideline history in an on-demand reference

Use targeted project searches, preferably `rg`, with 1–3 distinctive keywords per policy before opening large files.

#### Rules

- Keep each file role-distinct; do not collapse roles.
- Move guidance to its single best owning file and replace the other locations with a short cross-reference.
- Preserve normative wording (MUST/SHOULD-style rules); compact only structure, examples, and restatements.
- Do not delete guidance whose owner is unclear — flag it in the summary instead.
- When removing accumulated history, keep only the current rule; relocate any still-useful lesson to `ai/LEARNINGS.md`, released-history wording to `CHANGELOG.md`, and guideline-history context to an on-demand reference instead of dropping it silently.
- Propose new files only if the current owners cannot stay role-distinct without them.
- Do not touch `ai/archive/` content.

#### Deliverables

- Tightened standing files with overlap removed and cross-references updated.
- A short summary listing: files changed, guidance relocated (from → to), references fixed, and any flagged-but-not-changed items with rationale.

### Context Report

Category: Lifecycle And Maintenance
Slug: `context-report`
Placeholders: none

Prepare a report measuring how much context the repository AI instructions consume over time.

Use a temporary git worktree. Analyze the diff between the last commit and current HEAD, unless a specific commit range is provided as input (e.g., `START..END` or `START~1..HEAD`). Analyze commits in chronological first-parent order.

Measure context size in:

- characters
- bytes
- lines
- words
- estimated tokens using `ceiling(chars / 4)`

Count only repository AI instruction material:

- `AGENTS.md`
- files under `ai/`

Categorize measurements into these broad buckets:

- standing root and AI top-level files
- active plan files, meaning `ai/plans/PLAN_*.md`
- archived plans
- archived report artifacts
- on-demand tasks
- on-demand references
- on-demand templates
- repo skill entrypoints
- repo skill references

Exclude archived material under `ai/archive/` from default and generic task loads unless explicitly loaded by a task. Still include archive size in total AI instruction inventory reporting.

For each commit, measure these scenarios:

1. Default load: `AGENTS.md` only.
2. Short request: use this exact request text: `Briefly summarize the current project state from AGENTS.md`.
3. Generic lifecycle tasks: measure these exact task starters for each phase:
   - discovery: `[DISCOVERY]: Research the current implementation of business logic and identify governing specs.`
   - planning: `[PLAN]: Create a detailed execution plan for a new business feature following AGENTS.md rules.`
   - implementation: `[EXECUTE]: Implement the core logic and tests for the feature defined in an active PLAN_*.md file.`
   - testing: `[TEST]: Verify the implementation with integration tests and negative scenarios based on TESTING.md.`
   - review: `[REVIEW]: Conduct a security and maintainability review of recent changes using REVIEWS.md.`
   - integration: `[INTEGRATE]: Merge the completed implementation, update the roadmap, and perform final validation.`
   - release: `[RELEASE]: Prepare the release artifacts and update the roadmap and changelog per RELEASES.md.`

Use the relevant owner-map and heading-search rules from the repo docs when deciding which AI files each task would load.

Create two markdown tables.

Table 1: Summary by commit

Columns:

- commit
- commit date
- subject
- default load size
- short request size
- discovery task size
- planning task size
- implementation task size
- testing task size
- review task size
- integration task size
- release task size
- total AI instruction size

Include both character and estimated-token values. Use compact formatting if the table would otherwise become too wide.

Table 2: File/directory size by commit

Rows should include every AI file or directory that exists in any analyzed commit.

Columns should be one column per commit plus a final `total` column.

Use empty values where a file or directory did not exist on that commit.

For directories, report recursive totals.

Add a summary section before the tables.

The summary section should briefly state:

- the oldest commit and newest commit analyzed
- the overall standing-load trend
- the total measured AI markdown inventory trend over the analyzed range
- the most important finding in one or two sentences

Keep the summary practical and focused on actionable insights regarding context efficiency.

Add a statistics section after the summary.

The statistics section should include:

- **Baseline Metrics**: Smallest and biggest context use for each measured scenario.
- **Improvement Trend**: Oldest-to-newest improvement for each scenario and total inventory, reported as absolute character delta, absolute estimated-token delta, and percentage change.
- **Context Density**: Ratio of Standing Context (`AGENTS.md` + top-level `ai/*.md`) to Total AI Inventory.
- **Growth Velocity**: Average character and token change per analyzed commit.
- **Bloat Factor**: The percentage overhead added by active plans and on-demand tasks relative to the default load.

When reporting smallest and biggest context use, include the commit hash, commit date, subject, character count, and estimated-token count.

Add an interpretation section after the statistics section.

The interpretation section should explain:

- whether standing context is increasing, decreasing, or mostly stable
- which files or directories are the largest contributors to context use
- which commits caused the largest context increases or reductions
- whether the repo is moving toward better on-demand loading or toward larger default context
- any caveats in the measurement method, especially the approximate token estimate and inferred task-load behavior

Add a recommendations section after the interpretation section.

The recommendations section should provide concrete next actions, such as:

- files that should be compacted, split, archived, or moved to on-demand references
- task-catalog or owner-map changes that would reduce default context
- task-library or AI-documentation changes that would make future context use easier to measure
- thresholds or guardrails worth adding for future AI instruction size growth

Do not run the build, tests, or validation checks.

Write the final report under a temporary directory outside the worktree, for example:

`temp/context-report-<date>.md`

After the report is written, delete the temporary git worktree. Leave the report file in place.

### Deep AI Guidelines Assessment

Category: Lifecycle And Maintenance
Slug: `deep-ai-guidelines-assessment`
Placeholders: none

Deeply analyze, evaluate, and grade the current repository AI guideline set, then provide prioritized recommendations for improvement.

Use this as an evidence-based assessment task, not as an implementation request for the recommendations it discovers.
Base the assessment on the current `Context Report` measurement method and the latest archived AI-guideline evaluation under `ai/archive/reports/`, when one exists.

#### Scope

- Read `AGENTS.md`, `ai/DOCUMENTATION.md`, `ai/TASK_LIBRARY.md`, and, if present, the latest matching archived AI-guideline evaluation under `ai/archive/reports/` first.
- Generate a fresh context report using the `Context Report` task method, or reuse a same-session context report only if it clearly covers the current HEAD and uncommitted AI-guidance changes.
- Read top-level owner guides under `ai/` because the task grades the current guideline set as a whole.
- Inspect active plans only for lifecycle state, context-load impact, stale references, and active-read-set hygiene.
- Inspect on-demand references, templates, task sections, and repo-local skills only when the context report, archived evaluation report, or targeted searches identify them as important load, drift, duplication, or stale-reference contributors.
- Do not bulk-load `ai/archive/`; read only matching report artifacts under `ai/archive/reports/`, and sample archived plans only when the assessment has a specific historical or stale-reference question.

#### Assessment Tasks

1. Recompute or verify the context-load evidence from the context report:
   - default load
   - phase-specific practical read sets
   - task-catalog load
   - active-plan inventory
   - archived inventory
   - on-demand references, templates, and skills
   - total tracked AI instruction inventory
2. Compare the current evidence with the latest archived AI-guideline evaluation report, when one exists.
3. Grade the guideline set using a consistent rubric:
   - owner clarity and single-source ownership
   - context efficiency and default-load discipline
   - on-demand trigger clarity
   - task-library quality and heading-search safety
   - active-plan lifecycle hygiene
   - skill, reference, template, and archive containment
   - duplication, contradiction, and stale-reference risk
   - execution, testing, review, documentation, workflow, and release usefulness
   - recommendation actionability
4. Identify concrete improvement opportunities, including:
   - guidance to compact, split, relocate, archive, or turn into an on-demand reference
   - task sections that are growing into policy dumps
   - owner-map or task-index changes that would reduce accidental loading
   - thresholds or guardrails for future context growth
   - reusable measurement or validation helpers worth adding
   - obsolete recommendations that should be retired
5. Distinguish true blockers from optional cleanup and preference-only edits.

#### Report Requirements

Write a standalone markdown report under a temporary directory outside the worktree, for example:

`temp/deep-ai-guidelines-assessment-<date>.md`

The report must include:

- assessment date, commit range or working-tree boundary, and whether uncommitted changes were included
- executive summary with overall grade
- evidence sources, including the context report path and the evaluation report path
- current size and load-set findings
- scorecard with category grades and concise rationale
- top risks and contradictions, with file references
- comparison with the previous evaluation report
- ranked recommendations with owner file, expected benefit, estimated context impact, implementation risk, and validation needed
- "do now", "defer", and "do not do" sections
- caveats about approximate token estimates and inferred load behavior

#### Guardrails

- Do not implement recommendations unless I explicitly ask.
- Do not move archived reports back into `ai/references/`; use `Evaluate AI Guidelines` only when a fresh evaluation snapshot is explicitly needed.
- Do not edit `CHANGELOG.md` unless a tracked report or AI-guidance file is intentionally updated.
- Keep standing policy in the owning guide named by `ai/DOCUMENTATION.md`; do not copy full policy into the assessment report.
- If a live contradiction makes grading unreliable, list it as a blocker before recommendations instead of smoothing over it.

#### Validation

Do not run the build, tests, or heavyweight validation checks for the assessment itself.
Run `git diff --check` only if the task edits tracked repository files.

In the final response, summarize:

- overall grade
- highest-risk context or guideline issue
- top three recommendations
- report path
- validation commands and results

### Evaluate AI Guidelines

Category: Lifecycle And Maintenance
Slug: `evaluate-ai-guidelines`
Placeholders: none

Evaluate and grade the current repository AI guideline set, then write a timestamped archived evaluation snapshot under `ai/archive/reports/`.

Use this as an evaluation and reporting task, not as an implementation request for the recommendations it discovers.

#### Scope

- Read `AGENTS.md`, `ai/DOCUMENTATION.md`, `ai/TASK_LIBRARY.md`, and standing top-level owner guides under `ai/`.
- Exclude active `ai/plans/PLAN_*.md` files from the standing-guide baseline, but inspect active plans when they are relevant to lifecycle state, roadmap cleanup, or stale-reference checks.
- Read representative large task sections in this file when checking task-policy drift.
- Read on-demand references only when a standing guide points to them, when the latest archived evaluation report names them, or when a targeted search finds a likely stale reference.
- Do not bulk-load `ai/archive/` unless the evaluation specifically needs historical context.

#### Evaluation Tasks

1. Recompute current standing guidance sizes:
   - `AGENTS.md`
   - top-level `ai/*.md` owner guides excluding active `ai/plans/PLAN_*.md`
   - key on-demand references that affect practical load, such as workflow and release references
2. Recompute practical read-set estimates:
   - standing root plus top-level owner guides
   - planning minimum
   - implementation minimum
   - broad implementation conditional set, if still relevant
   - workflow selection
   - verification
   - release policy
   - release policy plus release references
   - descriptive docs only
3. Grade each standing guide using a consistent rubric:
   - owner clarity
   - default-load necessity
   - trigger clarity for on-demand material
   - duplication or policy drift
   - execution usefulness
   - validation and review routing
4. Check for stale or duplicated guidance:
   - retired file names or moved guide references
   - old workflow terminology outside intentional historical notes
   - task sections growing into standing policy dumps
   - repeated artifact-routing rules outside `ai/DOCUMENTATION.md`
   - release, workflow, validation, and planning mechanics duplicated across owners
5. Compare the current state to the latest matching archived AI-guideline evaluation under `ai/archive/reports/`, when one exists.
6. Write the report so it describes current repository truth, not the state at the previous evaluation.

#### Report Requirements

Create or refresh a same-day report at `ai/archive/reports/AI_GUIDELINES_EVALUATION_<YYYY-MM-DD>.md` with:

- evaluation date
- overall grade
- method
- current size baseline
- practical read-set estimates
- rubric findings
- file-by-file grades with short rationale
- realized gains since the previous report
- remaining costs and risks
- obsolete recommendations that should not be repeated
- ranked follow-up recommendations

Keep the report on demand. Do not move the report into `ai/references/` or a standing top-level guide.

#### Guardrails

- Do not implement follow-up compaction recommendations unless the user explicitly asks for implementation.
- If a concrete active-guidance contradiction makes the report inaccurate, fix only the narrow contradiction needed for report accuracy or stop and explain the blocker.
- Keep standing policy in the owning guide named by `ai/DOCUMENTATION.md`; do not copy full policy into the report.
- Keep archived content historical. Do not rewrite `ai/archive/` just to remove old terminology.
- Update `CHANGELOG.md` under `## [Unreleased]` when a tracked archived report is created or refreshed.

#### Validation

Run:

```powershell
git diff --check
./build.ps1 build
```

If the wrapper takes the lightweight-file shortcut, record that the Gradle build was skipped and manual consistency review was the relevant validation.

In the final response, summarize:

- overall grade
- most important size or load-set change
- top follow-up recommendation
- files changed
- validation commands and results

### Implement Then Release

Category: Lifecycle And Maintenance
Slug: `implement-then-release`
Placeholders: <plan_file>

Implement `<plan_file>`.

Use `ai/PLAN_EXECUTION.md` for implementation.
Use `ai/RELEASES.md` only after the approved implementation PR has been merged onto `main`.

### Summarize Lifecycle State

Category: Lifecycle And Maintenance
Slug: `summarize-lifecycle-state`
Placeholders: <plan_file>

Summarize the current lifecycle state for `<plan_file>` or the current change.

Use `ai/PLANNING.md`, `ai/PLAN_EXECUTION.md`, `ai/EXECUTION.md`, and `ai/WORKFLOW.md` as needed.
Report phase, status, active milestone, execution shape, pending validations, integration state, branch or PR state when relevant, and the next recommended step.

### Triage Validation Failure

Category: Lifecycle And Maintenance
Slug: `triage-validation-failure`
Placeholders: <change>, <plan_file>

Triage the failing validation for `<plan_file>` or `<change>`.

Use `ai/TESTING.md` and `ai/REVIEWS.md`.
Identify the first real failure, likely root cause, whether it looks like a spec break or implementation bug, and the smallest next fix.

### Upgrade Dependencies And Build Tools

Category: Lifecycle And Maintenance
Slug: `upgrade-dependencies-and-build-tools`
Placeholders: <item 1>, <item 2>

Upgrade these dependencies or build tools:
- <item 1>
- <item 2>

Read `AGENTS.md`, `ai/EXECUTION.md`, `ai/DOCUMENTATION.md`, `ai/TESTING.md`, the governing plan if one exists, the relevant build files, and the exact alert, version target, or tool output first.
Confirm where each version is actually owned before editing anything, including direct dependencies, transitive constraints, Gradle plugins, wrapper versions, `buildSrc`, workflow actions, or other build tooling.
Prefer the smallest version or constraint change that satisfies the requested upgrade, keep unrelated version churn out of the diff, capture resolved-version evidence, and summarize the exact validation that proves the upgraded build still matches repo rules.
Do not push, open a PR, or release unless I ask.
