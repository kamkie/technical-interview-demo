# Prompt Starters For This Repository

This is the on-demand library of full reusable prompt starters.
`ai/PROMPTS.md` is the lean standing index whose `###` titles act as reusable commands.
When a title is invoked, read only the matching starter here.
This file does not own standing policy.

Keep prompts lean:

- repository rules live in `AGENTS.md`
- code-shaping rules live in `ai/CODE_STYLE.md`
- artifact routing lives in `ai/DOCUMENTATION.md`
- local command wrapper shortcuts live in `ai/ENVIRONMENT_QUICK_REF.md`
- planning rules live in `ai/PLAN.md`
- single-agent and per-milestone execution rules live in `ai/EXECUTION.md`
- workflow-mode and delegation rules live in `ai/WORKFLOW.md`
- release rules live in `ai/RELEASES.md`
- validation rules live in `ai/TESTING.md`
- review rules live in `ai/REVIEWS.md`

If a prompt starts reading like policy, move that policy back to the owner guide and keep only the task-specific constraint here.

## Table Of Contents

- [Prompt Usage Baseline](#prompt-usage-baseline)
- [Title Shorthand](#title-shorthand)
- [Discovery And Roadmap](#discovery-and-roadmap)
  - [Clarify Roadmap Decisions](#clarify-roadmap-decisions)
  - [Refine Roadmap Intake](#refine-roadmap-intake)
  - [Pick Next Roadmap Workstream](#pick-next-roadmap-workstream)
  - [Prioritize Open Security And Quality Issues In Roadmap](#prioritize-open-security-and-quality-issues-in-roadmap)
  - [Review Roadmap Item](#review-roadmap-item)
- [Planning](#planning)
  - [Create Plan](#create-plan)
  - [Plan From Roadmap](#plan-from-roadmap)
  - [Plan Checked Roadmap Items](#plan-checked-roadmap-items)
  - [Split Checked Roadmap Items Into Plans](#split-checked-roadmap-items-into-plans)
  - [Plan Dependency And Build Tool Upgrade](#plan-dependency-and-build-tool-upgrade)
  - [Plan Repository-Wide Dependency And Toolchain Upgrade Sweep](#plan-repository-wide-dependency-and-toolchain-upgrade-sweep)
  - [Revise Plan](#revise-plan)
- [Plan Verification](#plan-verification)
  - [Review Plan Readiness](#review-plan-readiness)
  - [Choose Execution Mode](#choose-execution-mode)
- [Implementation](#implementation)
  - [Implement Plan](#implement-plan)
  - [Implement Milestone](#implement-milestone)
- [Workflow Execution](#workflow-execution)
  - [Run Plan With Inferred Mode](#run-plan-with-inferred-mode)
  - [Run Plan On Single Branch](#run-plan-on-single-branch)
  - [Run Plan As Shared Plan](#run-plan-as-shared-plan)
  - [Run Plans In Parallel](#run-plans-in-parallel)
  - [Run All Ready Plans](#run-all-ready-plans)
  - [Run All Unfinished Plans](#run-all-unfinished-plans)
  - [Check Worker Status](#check-worker-status)
  - [Check Active Workers](#check-active-workers)
- [Implementation Integration](#implementation-integration)
  - [Integrate Shared Plan Output](#integrate-shared-plan-output)
  - [Integrate Parallel Plan Output](#integrate-parallel-plan-output)
  - [Integrate All Open PRs](#integrate-all-open-prs)
- [Implementation Verification](#implementation-verification)
  - [Run Required Validation](#run-required-validation)
  - [Check Contract Impact](#check-contract-impact)
  - [Verify Milestone](#verify-milestone)
  - [Review Diff Risks](#review-diff-risks)
- [Preparing Release](#preparing-release)
  - [Check Release Readiness](#check-release-readiness)
  - [Prepare Release](#prepare-release)
- [Releasing](#releasing)
  - [Push Prepared Release](#push-prepared-release)
  - [Release All Merged Work](#release-all-merged-work)
  - [Check Published Release](#check-published-release)
- [Other Useful Lifecycle Prompts](#other-useful-lifecycle-prompts)
  - [Implement Then Release](#implement-then-release)
  - [Clean Worktrees And Stale Local Branches](#clean-worktrees-and-stale-local-branches)
  - [Summarize Lifecycle State](#summarize-lifecycle-state)
  - [Triage Validation Failure](#triage-validation-failure)
- [Maintenance](#maintenance)
  - [Upgrade Dependencies And Build Tools](#upgrade-dependencies-and-build-tools)
  - [Compact AI Docs](#compact-ai-docs)

## Prompt Usage Baseline

Default read set by task:

- discovery and roadmap work: `AGENTS.md`, `ROADMAP.md`, `ai/PLAN.md`; add `README.md`, `ai/DESIGN.md`, `ai/ARCHITECTURE.md`, or `ai/BUSINESS_MODULES.md` only when relevant
- planning: `AGENTS.md`, `ai/PLAN.md`, and the governing specs; add `ai/DOCUMENTATION.md`, `README.md`, `ROADMAP.md`, `ai/DESIGN.md`, `ai/ARCHITECTURE.md`, or `ai/BUSINESS_MODULES.md` only when relevant
- plan verification: `AGENTS.md`, `ai/PLAN.md`, and the target plan file
- implementation: `AGENTS.md`, `ai/EXECUTION.md`, and the target `ai/PLAN_*.md`; add `ai/CODE_STYLE.md`, `ai/DOCUMENTATION.md`, `ai/TESTING.md`, and `ai/REVIEWS.md` as the change requires
- workflow execution: `AGENTS.md`, `ai/WORKFLOW.md`, `ai/EXECUTION.md`, and the relevant plan files; add `ai/DOCUMENTATION.md`, `ai/TESTING.md`, and `ai/REVIEWS.md` when routing shared artifacts or final validation
- implementation integration: `AGENTS.md`, `ai/WORKFLOW.md`, `ai/EXECUTION.md`, and the relevant plan files or worker logs; add `ai/DOCUMENTATION.md`, `ai/TESTING.md`, and `ai/REVIEWS.md` before finalizing integration
- implementation verification: `AGENTS.md`, `ai/TESTING.md`, `ai/DOCUMENTATION.md`, `ai/REVIEWS.md`, plus any owner guide needed to judge artifact impact
- preparing release: `AGENTS.md`, `ai/RELEASES.md`, `ai/DOCUMENTATION.md`, the executed plan, and the changed contract docs
- releasing: `AGENTS.md`, `ai/RELEASES.md`, and the current release state

Use the owner guide named by the prompt instead of restating its standing policy in the request.
Prefer filling in placeholders such as `<topic>`, `<plan_file>`, `<milestone_name>`, `<task>`, and `<constraint>` so the request is concrete.

## Title Shorthand

You may invoke a prompt in this file by using its `###` title as a reusable command and shorthand for the full starter under that heading.

Rules:

- use the exact prompt title or an unmistakably close reference to one prompt title in this file
- supply the required placeholders or equivalent concrete context in the same request
- treat section headings such as `## Planning` and `## Implementation Verification` as categories, not invocable prompts
- if the title match is ambiguous or required context is missing, stop and ask a targeted clarification question instead of guessing

Example shorthand:

```markdown
Use `Implement Plan` for `ai/PLAN_auth_cleanup.md`.
```

## Discovery And Roadmap

### Clarify Roadmap Decisions

```markdown
Review `ROADMAP.md` for project-framing decisions that must be made before roadmap items can be planned or sequenced confidently.

Read `AGENTS.md`, `README.md`, `ROADMAP.md`, `ai/PLAN.md`, and `ai/DESIGN.md` first.
Use repo truth to resolve what you can before asking anything.
Focus on decisions that shape product direction, client/auth posture, compatibility promises, rollout model, release posture, or other roadmap-defining project choices.
Ask the user only the remaining high-value framing questions, with concrete options and a recommended option for each one.
Explain why each decision matters, which roadmap items it blocks or reshapes, and what fallback would be used if the user does not decide yet.
If the roadmap is already sufficiently framed, say so explicitly and do not manufacture questions.
After the interactive decisions are answered, update `ROADMAP.md` to reflect the resolved direction and summarize the decisions that were locked.
Do not create an execution plan unless I ask.
```

### Refine Roadmap Intake

```markdown
Refine the items under `## Not Yet Refined` in `ROADMAP.md` into concrete roadmap entries.

Treat that section as rough intake only.
Move refined tasks only when the resulting roadmap wording is concrete enough to sequence.
Leave still-ambiguous tasks in place and explain what is missing.
```

### Pick Next Roadmap Workstream

```markdown
Review `ROADMAP.md` and select the next coherent workstream to move toward planning.

Use `ai/PLAN.md` lifecycle vocabulary.
If the candidate work is still only `Discovery`, say so explicitly and explain what is still missing before a real plan should be written.
If work is ready to move forward, name the exact roadmap items that should feed the next plan and explain why they belong together.
Do not create the plan yet unless I ask.
```

### Prioritize Open Security And Quality Issues In Roadmap

```markdown
Use the repo-local skill `gh-fix-security-quality` to inspect all open GitHub Security and quality issues for this repository, then update `ROADMAP.md` to capture and prioritize them.

Read `AGENTS.md`, `ROADMAP.md`, `ai/PLAN.md`, and `ai/skills/gh-fix-security-quality/SKILL.md` first.
Use the skill to inspect every open code-scanning and Dependabot alert, summarize the actionable issue families, and turn them into concrete roadmap entries.
Prioritize the roadmap by severity, exploitability, release risk, and batching efficiency.
Group repeated alert families into one roadmap batch when that is the better execution unit than one item per alert.
Say explicitly which alerts were grouped together, which stayed separate, and why.
Do not create an execution plan or implement fixes unless I ask.
```

### Review Roadmap Item

```markdown
Review the roadmap item `<task>` before planning implementation.

Explain what behavior would change, which current specs govern it, what is still ambiguous, and whether the task should remain in `Discovery`, move into `Planning`, or be treated as `Needs Input` under `ai/PLAN.md`.
```

## Planning

For planning prompts, use the lifecycle vocabulary from `ai/PLAN.md`.
Do not force a plan into `Phase=Planning` if the work should still be `Discovery` or `Needs Input`.

### Create Plan

```markdown
Create `ai/PLAN_<topic>.md` for <topic>.

Follow `ai/PLAN.md`.
Set the plan lifecycle using `ai/PLAN.md` instead of guessing.
```

### Plan From Roadmap

```markdown
Create `ai/PLAN_<topic>.md` from this roadmap input:
- <task 1>
- <task 2>

Treat `ROADMAP.md` only as roadmap input and follow `ai/PLAN.md`.
Set the lifecycle state from `ai/PLAN.md` based on the actual readiness of the work.
```

### Plan Checked Roadmap Items

```markdown
Create one coherent `ai/PLAN_<topic>.md` from every checklist item marked `[x]` in `ROADMAP.md`.

Use only the checked items unless the roadmap text makes a dependency explicit.
Restate exactly which checked items were included.
If the checked items do not form one coherent executable plan, stop and explain the gap instead of guessing.
Record unresolved requirement gaps and fallback assumptions explicitly.
Set the lifecycle state from `ai/PLAN.md` based on actual planning readiness.
```

### Split Checked Roadmap Items Into Plans

```markdown
Create one or more `ai/PLAN_<topic>.md` files from every checklist item marked `[x]` in `ROADMAP.md`.

Split only genuinely disjoint workstreams that can later execute in parallel without overlapping source ownership, contract artifacts, rollout order, or validation.
If the checked items form only one coherent plan, stop and say that the single-plan prompt should be used instead.
Record requirement gaps, fallback assumptions, and any cross-plan dependency notes in each created plan.
Set the lifecycle state for each plan from `ai/PLAN.md`.
```

### Plan Dependency And Build Tool Upgrade

```markdown
Create `ai/PLAN_<topic>.md` for this dependency or build-tool upgrade batch:
- <item 1>
- <item 2>

Read `AGENTS.md`, `ai/PLAN.md`, `ai/DOCUMENTATION.md`, `ai/TESTING.md`, the relevant build files, and the exact alert, version target, or tool output that motivates the upgrade first.
Identify where each version is actually owned before planning changes, including direct dependencies, transitive overrides, Gradle plugins, wrapper updates, `buildSrc`, workflow actions, or other build tooling.
Keep the plan narrow, call out compatibility and validation risk explicitly, name the resolved-version proof expected during execution, and say whether the work should stay one reviewable batch or be split into smaller upgrade plans.
```

### Plan Repository-Wide Dependency And Toolchain Upgrade Sweep

```markdown
Create `ai/PLAN_<topic>.md` for a repository-wide dependency and toolchain upgrade sweep.

Read `AGENTS.md`, `ai/PLAN.md`, `ai/DOCUMENTATION.md`, `ai/TESTING.md`, the relevant build files, Dockerfiles, workflow files, and any current alert, scan, or dependency-report output first.
Inventory all directly owned version surfaces before planning changes, including application dependencies, Gradle plugins, the Gradle wrapper, `buildSrc`, Docker base images, GitHub Actions, and other checked-in build or packaging tools.
Propose the smallest reviewable execution shape that still upgrades the full owned surface, and say explicitly if the work is too broad for one plan and should be split into multiple `ai/PLAN_*.md` files.
For each planned batch, call out compatibility risk, rollback or migration concerns, the exact resolved-version proof expected during execution, and the validation needed to keep the upgraded repo release-ready.
```

### Revise Plan

```markdown
Revise `<plan_file>` for this new requirement or constraint:
<constraint>

Keep the plan self-contained and follow `ai/PLAN.md`.
Update the lifecycle state only as far as the revision justifies.
```

## Plan Verification

### Review Plan Readiness

```markdown
Review `<plan_file>` against `ai/PLAN.md`.

List concrete gaps or ambiguities first.
Check whether the lifecycle phase and status are accurate.
Say explicitly if the plan is ready.
```

### Choose Execution Mode

```markdown
Review `<plan_file>` and decide which execution mode fits best.

Use `ai/PLAN.md` and `ai/WORKFLOW.md`.
Base the decision on the plan's `Execution Mode Fit`, milestone boundaries, shared-file ownership, and whether the work should stay one plan or be split into multiple plan files.
Say explicitly whether `Single Branch`, `Shared Plan`, or `Parallel Plans` is the right answer.
If the work really needs multiple plan files, say that explicitly instead of forcing a single-plan mode.
```

## Implementation

### Implement Plan

```markdown
Implement `<plan_file>`.

Follow `ai/EXECUTION.md`.
Do not push, open a PR, or release unless I ask.
```

### Implement Milestone

```markdown
Implement only `<milestone_name>` from `<plan_file>`.

Follow `ai/EXECUTION.md`.
Do not start later milestones, push, open a PR, or release unless I ask.
```

## Workflow Execution

Use these prompts when one request should actively execute planned work while also choosing or coordinating the workflow mode from `ai/WORKFLOW.md`.

### Run Plan With Inferred Mode

```markdown
Execute `<plan_file>` using the workflow rules.

Use `ai/WORKFLOW.md` and `ai/EXECUTION.md`.
Infer `Single Branch` or `Shared Plan` from the plan file, especially `Execution Mode Fit`, milestone boundaries, and shared-file ownership.
If `Shared Plan` is selected, keep the coordinator active until every worker reaches a terminal state; do not finish the run when only some workers are done.
If the plan file is not clear enough to choose safely, stop and explain the ambiguity instead of guessing.
If the work actually needs multiple plan files and `Parallel Plans`, stop and say that explicitly instead of guessing.
```

### Run Plan On Single Branch

```markdown
Execute `<plan_file>` in `Single Branch` mode.

Use `ai/WORKFLOW.md` and `ai/EXECUTION.md`.
Treat the canonical plan file and `CHANGELOG.md` as directly editable on the active branch.
Do not push, open a PR, or release unless I ask.
```

### Run Plan As Shared Plan

```markdown
Execute `<plan_file>` in `Shared Plan` mode.

Use `ai/WORKFLOW.md` and `ai/EXECUTION.md`.
Act as orchestrator, fan out worker branches or worktrees only for disjoint slices, keep shared files coordinator-owned, and require committed worker logs at `ai/tmp/workflow/<plan_stem_or_topic>__<worker_name>.md`.
Keep the coordinator active until every worker reaches a terminal state and summarize all workers at the end; progress reports before that are interim only.
Push only the finished coordinator branch and open one final PR unless I explicitly ask otherwise.
```

### Run Plans In Parallel

```markdown
Execute these plan files in `Parallel Plans` mode using git worktrees:
- <plan_file_1>
- <plan_file_2>
- <plan_file_3>

Use `ai/WORKFLOW.md` and `ai/EXECUTION.md`.
Track per-plan branch, validation, private `CHANGELOG_<topic>.md`, worker log, and PR status.
Treat each worker branch as complete only when local validation is done and the branch has been pushed with a PR open or already merged, matching `ai/WORKFLOW.md` and `AGENTS.md`.
Keep the coordinator active until every worker reaches a terminal state and summarize all worker outcomes together; do not finish when only one worker is done.
If any listed plans are too coupled for safe parallel execution, stop and explain why instead of forcing a different mode.
Do not release unless I ask.
```

### Run All Ready Plans

```markdown
Select every ready plan file under `ai/`, then execute the selected set using the same flow as `Run Plans In Parallel`.

Treat ready plans as the non-archived `ai/PLAN_*.md` files still present directly under `ai/` whose `Lifecycle` status is `Ready`.
Restate exactly which ready plan files were selected and which non-archived plan files were skipped because they were not `Ready`.
If there are no ready plans, stop and say so explicitly.
If only one ready plan exists, stop and say that a single-plan execution prompt should be used instead.
Do not silently skip a `Ready` plan just to force a smaller parallel-safe set.
If any ready plans are too coupled for safe parallel execution, stop and explain why instead of forcing `Parallel Plans`.
Then execute the selected plan files in `Parallel Plans` mode using git worktrees.
Use `ai/WORKFLOW.md` and `ai/EXECUTION.md`.
Track per-plan branch, validation, private `CHANGELOG_<topic>.md`, worker log, and PR status.
Treat each worker branch as complete only when local validation is done and the branch has been pushed with a PR open or already merged, matching `ai/WORKFLOW.md` and `AGENTS.md`.
Keep the coordinator active until every worker reaches a terminal state and summarize all worker outcomes together; do not finish when only one worker is done.
Do not release unless I ask.
```

### Run All Unfinished Plans

```markdown
Select every unfinished plan file under `ai/`, then execute the selected set using the same flow as `Run Plans In Parallel`.

Treat unfinished plans as the non-archived `ai/PLAN_*.md` files still present directly under `ai/`.
If there are no unfinished plans, stop and say so explicitly.
If only one unfinished plan exists, stop and say that a single-plan execution prompt should be used instead.
Do not silently skip an unfinished plan just to force a smaller parallel-safe set.
If any unfinished plans are too coupled for safe parallel execution, stop and explain why instead of forcing `Parallel Plans`.
Then execute the selected plan files in `Parallel Plans` mode using git worktrees.
Use `ai/WORKFLOW.md` and `ai/EXECUTION.md`.
Track per-plan branch, validation, private `CHANGELOG_<topic>.md`, worker log, and PR status.
Treat each worker branch as complete only when local validation is done and the branch has been pushed with a PR open or already merged, matching `ai/WORKFLOW.md` and `AGENTS.md`.
Keep the coordinator active until every worker reaches a terminal state and summarize all worker outcomes together; do not finish when only one worker is done.
Do not release unless I ask.
```

### Check Worker Status

```markdown
Check the status of worker `<worker name or agent id>` in the current workflow execution.

Report mode, owned plan or slice, branch and worktree, current progress, changed files, validations run with results, commit SHA(s), blockers, ready-for-integration status, PR status, and the worker-log path.
For `Shared Plan`, also note which shared files were intentionally left to the coordinator.
For `Parallel Plans`, also include the `CHANGELOG_<topic>.md` path.
If the worker has stalled or completed, state that clearly.
```

### Check Active Workers

```markdown
Check the status of the active workers in the current workflow execution.

Use `ai/WORKFLOW.md`.
For each worker, report mode, owned plan or slice, branch and worktree, current task, changed files, validations, commit SHA(s), blockers, ready-for-integration status, PR status, and the worker-log path.
For `Shared Plan`, include any shared files intentionally left to the coordinator.
For `Parallel Plans`, include the `CHANGELOG_<topic>.md` path.
Keep the report concise and factual.
```

## Implementation Integration

Use these prompts after worker implementation is already done and the next task is to fold ready output from worker branches or open PRs back into the canonical plan or accepted plan branches, `CHANGELOG.md`, and the integration branch, then clean any consumed local worker git trees under `ai/WORKFLOW.md`.

### Integrate Shared Plan Output

```markdown
Integrate completed worker output for `<plan_file>`.

Use `ai/WORKFLOW.md` as coordinator in `Shared Plan` mode.
Merge ready worker branches by default, using cherry-pick only when accepting less than a full worker branch, when I ask for it, or when a normal merge is not viable.
Fold accepted worker-log content into the canonical plan and `CHANGELOG.md`, clean consumed local worker branches or worktrees under `ai/WORKFLOW.md`, run the required integration validation, delete consumed worker logs before the final push or PR unless I explicitly want them retained, and summarize what landed, what remains, and any cherry-pick reason.
```

### Integrate Parallel Plan Output

```markdown
Integrate completed worker output for all merged `Parallel Plans`.

Use `ai/WORKFLOW.md` as coordinator in `Parallel Plans` mode.
Review which merged plans are in scope, say explicitly which merged plans were included and which were skipped, integrate any accepted worker output that is not yet on the integration branch, fold accepted `CHANGELOG_<topic>.md` entries into `CHANGELOG.md`, update any canonical plan files or shared artifacts the accepted merged plans require, clean consumed local worker branches or worktrees under `ai/WORKFLOW.md`, delete consumed private changelog files and worker logs before the final push or PR unless I explicitly want them retained, run the required integration validation, and summarize what landed, what was skipped, and what remains.
If there are no in-scope merged `Parallel Plans`, say so explicitly and stop.
```

### Integrate All Open PRs

```markdown
Integrate all open implementation PRs that are ready.

Use `AGENTS.md`, `ai/WORKFLOW.md`, `ai/EXECUTION.md`, `ai/DOCUMENTATION.md`, `ai/TESTING.md`, and `ai/REVIEWS.md` as coordinator.
Discover every currently open PR with the GitHub CLI or repository remote, then review each PR branch, plan reference, changed files, checks, review state, conflicts, and relationship to the active unreleased work.
Say which open PRs are in scope, which are not implementation PRs, which are not ready, and why.
For ready in-scope PRs, merge the accepted output onto the integration branch in dependency order by default, using cherry-pick only when accepting less than a full PR, when I ask for it, or when a normal merge is not viable.
Fold accepted `CHANGELOG_<topic>.md` entries into `CHANGELOG.md`, update any canonical plan files or shared artifacts the accepted PRs require, clean consumed local worker branches or worktrees under `ai/WORKFLOW.md`, and delete consumed private changelog files and worker logs before the final push or PR unless I explicitly want them retained.
Run the required integration validation, then summarize what landed, what was skipped, what remains open, any cherry-pick reason, and any follow-up PRs or blockers.
If there are no in-scope open PRs, say so explicitly and stop.
```

## Implementation Verification

### Run Required Validation

```markdown
Run only the required validation for `<plan_file>` or `<change>`.
Do not edit files.

Use `ai/TESTING.md` and `ai/DOCUMENTATION.md`.
Start with `pwsh ./scripts/classify-changed-files.ps1 -Uncommitted` unless another diff boundary is explicit.
Summarize what ran, what passed, what failed, what was skipped, and what artifacts would likely need updates.
```

### Check Contract Impact

```markdown
Review `<change>` for contract and artifact-routing impact.

Use `AGENTS.md`, `ai/DOCUMENTATION.md`, and `ai/TESTING.md`.
State whether it requires updates to governing tests, published contract docs, approved OpenAPI, HTTP examples, `README.md`, maintainer docs, roadmap or release artifacts, or benchmark and compatibility checks.
If the change is internal-only, say that explicitly and explain why.
```

### Verify Milestone

```markdown
Verify `<milestone_name>` from `<plan_file>` after implementation.

Use `ai/TESTING.md`, `ai/DOCUMENTATION.md`, and `ai/REVIEWS.md`.
Check validation coverage, artifact updates, milestone commit completeness, and any remaining blocker before the next milestone or integration step.
List blockers first.
```

### Review Diff Risks

```markdown
Review this change with a code review mindset.

Use `ai/REVIEWS.md`.
List findings first, ordered by severity, with file references.
Keep the summary brief.
```

## Preparing Release

### Check Release Readiness

```markdown
Review release readiness for `<plan_file>`.

Use `ai/RELEASES.md` and `ai/DOCUMENTATION.md`.
List blockers first.
Say explicitly if the repository is release-ready.
```

### Prepare Release

```markdown
Prepare the release candidate for all merged but unreleased work currently on `main`.

Follow `ai/RELEASES.md` and `ai/DOCUMENTATION.md`.
Only proceed if the approved implementation PR is already merged onto `main`.
Include every merged and executed `ai/PLAN_*.md` file that belongs to the unreleased change set.
If any included work is not release-ready, stop and list blockers first instead of preparing a partial release.
If the merged work is ready, prepare the release commit, archive every included executed plan under `ai/archive/`, create the annotated tag locally, and summarize exactly which merged PRs and executed plan files were included.
Do not push unless I ask.
```

## Releasing

### Push Prepared Release

```markdown
Push the current release commit and annotated tag to the remote.

Follow `ai/RELEASES.md` for push and post-push verification.
```

### Release All Merged Work

```markdown
Verify and release all merged but unreleased work currently on `main`.

Use `ai/RELEASES.md`.
Include every merged PR and executed `ai/PLAN_*.md` file that belongs to the unreleased change set.
If any included work is not release-ready, stop and list blockers first instead of preparing a partial release.
If the merged work is ready, prepare the release commit, archive every included executed plan under `ai/archive/`, create the annotated tag, push the release commit and tag, and verify remote publication.
Summarize exactly which merged PRs and executed plan files were included.
```

### Check Published Release

```markdown
Verify the already pushed release for `<plan_file>`.

Follow `ai/RELEASES.md` for the post-push checks.
Summarize exactly what was published.
```

## Other Useful Lifecycle Prompts

### Implement Then Release

```markdown
Implement `<plan_file>`.

Use `ai/EXECUTION.md` for implementation.
Use `ai/RELEASES.md` only after the approved implementation PR has been merged onto `main`.
```

### Clean Worktrees And Stale Local Branches

```markdown
Clean local git worktrees and stale local branches after completed or abandoned workflow execution.

Use `ai/WORKFLOW.md`.
Review the current local worktrees and branches first.
Remove temporary worktrees that are no longer needed, delete stale local worker branches that are not needed for an open PR or other requested follow-up, and leave any retained branch or worktree in a clean local state.
Do not delete remote branches, close PRs, or remove any branch or worktree that still has uncommitted or unmerged work unless I explicitly ask.
Summarize what was removed, what was retained, and any follow-up cleanup that is still blocked.
```

### Summarize Lifecycle State

```markdown
Summarize the current lifecycle state for `<plan_file>` or the current change.

Use `ai/PLAN.md`, `ai/EXECUTION.md`, and `ai/WORKFLOW.md` as needed.
Report phase, status, active milestone, mode, pending validations, integration state, branch or PR state when relevant, and the next recommended step.
```

### Triage Validation Failure

```markdown
Triage the failing validation for `<plan_file>` or `<change>`.

Use `ai/TESTING.md` and `ai/REVIEWS.md`.
Identify the first real failure, likely root cause, whether it looks like a spec break or implementation bug, and the smallest next fix.
```

## Maintenance

### Upgrade Dependencies And Build Tools

```markdown
Upgrade these dependencies or build tools:
- <item 1>
- <item 2>

Read `AGENTS.md`, `ai/EXECUTION.md`, `ai/DOCUMENTATION.md`, `ai/TESTING.md`, the governing plan if one exists, the relevant build files, and the exact alert, version target, or tool output first.
Confirm where each version is actually owned before editing anything, including direct dependencies, transitive constraints, Gradle plugins, wrapper versions, `buildSrc`, workflow actions, or other build tooling.
Prefer the smallest version or constraint change that satisfies the requested upgrade, keep unrelated version churn out of the diff, capture resolved-version evidence, and summarize the exact validation that proves the upgraded build still matches repo rules.
Do not push, open a PR, or release unless I ask.
```

### Compact AI Docs

```markdown
Compact the standing AI instruction files.

Read `AGENTS.md` and the current AI instruction files under `ai/` first. Check `WORKING_WITH_AI.md` too when the overlapping workflow wording would drift for human readers.
Keep each file role-distinct.
Move duplicated standing guidance into the best owning AI document.
Tighten wording, remove overlap, update cross-references, and summarize the compaction decisions.
Propose creating new files only if the current owners cannot stay role-distinct without them.
```
