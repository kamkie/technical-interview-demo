# Prompt Library For This Repository

`ai/PROMPTS.md` is a library of short reusable prompt starters for common repository workflows.
It does not own standing policy.

Keep prompts lean:

- repository rules live in `AGENTS.md`
- artifact routing lives in `ai/DOCUMENTATION.md`
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
  - [Surface Roadmap Framing Decisions Interactively](#surface-roadmap-framing-decisions-interactively)
  - [Refine Unrefined Roadmap Tasks Into Real Entries](#refine-unrefined-roadmap-tasks-into-real-entries)
  - [Select The Next Roadmap Workstream For Planning](#select-the-next-roadmap-workstream-for-planning)
  - [Review A Roadmap Item Before Planning](#review-a-roadmap-item-before-planning)
- [Planning](#planning)
  - [Create A New Execution Plan](#create-a-new-execution-plan)
  - [Create A Plan From Roadmap Input](#create-a-plan-from-roadmap-input)
  - [Create A Plan From Checked Roadmap Tasks](#create-a-plan-from-checked-roadmap-tasks)
  - [Create Multiple Plans From Disjoint Checked Roadmap Tasks](#create-multiple-plans-from-disjoint-checked-roadmap-tasks)
  - [Revise An Existing Plan](#revise-an-existing-plan)
- [Plan Verification](#plan-verification)
  - [Review Whether A Plan Is Ready](#review-whether-a-plan-is-ready)
  - [Decide How One Plan Should Execute](#decide-how-one-plan-should-execute)
- [Implementation](#implementation)
  - [Implement A Plan Without Releasing](#implement-a-plan-without-releasing)
  - [Implement Only One Milestone](#implement-only-one-milestone)
  - [Execute ALL Ready Plans In Parallel](#execute-all-ready-plans-in-parallel)
  - [Implement All Unfinished Plans In Parallel](#implement-all-unfinished-plans-in-parallel)
- [Workflow Execution](#workflow-execution)
  - [Execute One Plan And Infer Workflow Mode](#execute-one-plan-and-infer-workflow-mode)
  - [Execute One Plan On A Single Branch](#execute-one-plan-on-a-single-branch)
  - [Execute One Plan As Shared Plan](#execute-one-plan-as-shared-plan)
  - [Execute Multiple Plans In Parallel](#execute-multiple-plans-in-parallel)
  - [Check Status On One Worker](#check-status-on-one-worker)
  - [Check Status On Active Workers](#check-status-on-active-workers)
- [Implementation Integration](#implementation-integration)
  - [Integrate Completed Shared-Plan Worker Output](#integrate-completed-shared-plan-worker-output)
- [Implementation Verification](#implementation-verification)
  - [Run Required Validation Only](#run-required-validation-only)
  - [Verify Contract Impact](#verify-contract-impact)
  - [Verify An Implemented Milestone](#verify-an-implemented-milestone)
  - [Review A Diff For Risks](#review-a-diff-for-risks)
- [Preparing Release](#preparing-release)
  - [Verify Release Readiness](#verify-release-readiness)
  - [Prepare A Release Only](#prepare-a-release-only)
- [Releasing](#releasing)
  - [Push An Already Prepared Release](#push-an-already-prepared-release)
  - [Verify And Release All Merged PR Work](#verify-and-release-all-merged-pr-work)
  - [Verify The Published Release](#verify-the-published-release)
- [Other Useful Lifecycle Prompts](#other-useful-lifecycle-prompts)
  - [Implement And Then Release](#implement-and-then-release)
  - [Summarize The Current Lifecycle State](#summarize-the-current-lifecycle-state)
  - [Triage A Failed Validation Run](#triage-a-failed-validation-run)
- [Maintenance](#maintenance)
  - [Compact AI Instruction Files](#compact-ai-instruction-files)

## Prompt Usage Baseline

Default read set by task:

- discovery and roadmap work: `AGENTS.md`, `ROADMAP.md`, `ai/PLAN.md`; add `README.md` or `ai/DESIGN.md` only when relevant
- planning: `AGENTS.md`, `ai/PLAN.md`, and the governing specs; add `README.md` or `ROADMAP.md` only when relevant
- plan verification: `AGENTS.md`, `ai/PLAN.md`, and the target plan file
- implementation: `AGENTS.md`, `ai/EXECUTION.md`, and the target `ai/PLAN_*.md`; add `ai/WORKFLOW.md` when the request fans out across multiple plans or worktrees
- workflow execution: `AGENTS.md`, `ai/WORKFLOW.md`, `ai/EXECUTION.md`, and the relevant plan files
- implementation integration: `AGENTS.md`, `ai/WORKFLOW.md`, `ai/EXECUTION.md`, and the relevant plan files or worker logs
- implementation verification: `AGENTS.md`, `ai/TESTING.md`, `ai/REVIEWS.md`, plus any owner guide needed to judge artifact impact
- preparing release: `AGENTS.md`, `ai/RELEASES.md`, the executed plan, and the changed contract docs
- releasing: `AGENTS.md`, `ai/RELEASES.md`, and the current release state

Use the owner guide named by the prompt instead of restating its standing policy in the request.
Prefer filling in placeholders such as `<topic>`, `<plan_file>`, `<milestone_name>`, `<task>`, and `<constraint>` so the request is concrete.

## Title Shorthand

You may invoke a prompt in this file by using its `###` title as shorthand for the full starter under that heading.

Rules:

- use the exact prompt title or an unmistakably close reference to one prompt title in this file
- supply the required placeholders or equivalent concrete context in the same request
- treat section headings such as `## Planning` and `## Implementation Verification` as categories, not invocable prompts
- if the title match is ambiguous or required context is missing, stop and ask a targeted clarification question instead of guessing

Example shorthand:

```text
Use `Implement A Plan Without Releasing` for `ai/PLAN_auth_cleanup.md`.
```

## Discovery And Roadmap

### Surface Roadmap Framing Decisions Interactively

```text
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

### Refine Unrefined Roadmap Tasks Into Real Entries

```text
Refine the items under `## Not Yet Refined` in `ROADMAP.md` into concrete roadmap entries.

Treat that section as rough intake only.
Move refined tasks only when the resulting roadmap wording is concrete enough to sequence.
Leave still-ambiguous tasks in place and explain what is missing.
```

### Select The Next Roadmap Workstream For Planning

```text
Review `ROADMAP.md` and select the next coherent workstream to move toward planning.

Use `ai/PLAN.md` lifecycle vocabulary.
If the candidate work is still only `Discovery`, say so explicitly and explain what is still missing before a real plan should be written.
If work is ready to move forward, name the exact roadmap items that should feed the next plan and explain why they belong together.
Do not create the plan yet unless I ask.
```

### Review A Roadmap Item Before Planning

```text
Review the roadmap item `<task>` before planning implementation.

Explain what behavior would change, which current specs govern it, what is still ambiguous, and whether the task should remain in `Discovery` or can move into `Planning` under `ai/PLAN.md`.
```

## Planning

For planning prompts, use the lifecycle vocabulary from `ai/PLAN.md`.
Do not force a plan into `Phase=Planning` if the work should still be `Discovery` or `Needs Input`.

### Create A New Execution Plan

```text
Create `ai/PLAN_<topic>.md` for <topic>.

Follow `ai/PLAN.md`.
Set the plan lifecycle using `ai/PLAN.md` instead of guessing.
```

### Create A Plan From Roadmap Input

```text
Create `ai/PLAN_<topic>.md` from this roadmap input:
- <task 1>
- <task 2>

Treat `ROADMAP.md` only as roadmap input and follow `ai/PLAN.md`.
Set the lifecycle state from `ai/PLAN.md` based on the actual readiness of the work.
```

### Create A Plan From Checked Roadmap Tasks

```text
Create one coherent `ai/PLAN_<topic>.md` from every checklist item marked `[x]` in `ROADMAP.md`.

Use only the checked items unless the roadmap text makes a dependency explicit.
Restate exactly which checked items were included.
If the checked items do not form one coherent executable plan, stop and explain the gap instead of guessing.
Record unresolved requirement gaps and fallback assumptions explicitly.
Set the lifecycle state from `ai/PLAN.md` based on actual planning readiness.
```

### Create Multiple Plans From Disjoint Checked Roadmap Tasks

```text
Create one or more `ai/PLAN_<topic>.md` files from every checklist item marked `[x]` in `ROADMAP.md`.

Split only genuinely disjoint workstreams that can later execute in parallel without overlapping source ownership, contract artifacts, rollout order, or validation.
If the checked items form only one coherent plan, stop and say that the single-plan prompt should be used instead.
Record requirement gaps, fallback assumptions, and any cross-plan dependency notes in each created plan.
Set the lifecycle state for each plan from `ai/PLAN.md`.
```

### Revise An Existing Plan

```text
Revise `<plan_file>` for this new requirement or constraint:
<constraint>

Keep the plan self-contained and follow `ai/PLAN.md`.
Update the lifecycle state only as far as the revision justifies.
```

## Plan Verification

### Review Whether A Plan Is Ready

```text
Review `<plan_file>` against `ai/PLAN.md`.

List concrete gaps or ambiguities first.
Check whether the lifecycle phase and status are accurate.
Say explicitly if the plan is ready.
```

### Decide How One Plan Should Execute

```text
Review `<plan_file>` and decide whether it should execute in `Single Branch` or `Shared Plan`.

Use `ai/WORKFLOW.md`.
Base the decision on the plan's `Execution Mode Fit`, milestone boundaries, and shared-file ownership.
If the work really needs multiple plan files and `Parallel Plans`, say that explicitly instead of forcing a single-plan mode.
```

## Implementation

### Implement A Plan Without Releasing

```text
Implement `<plan_file>`.

Follow `ai/EXECUTION.md`.
Do not push, open a PR, or release unless I ask.
```

### Implement Only One Milestone

```text
Implement only `<milestone_name>` from `<plan_file>`.

Follow `ai/EXECUTION.md`.
Do not start later milestones, push, open a PR, or release unless I ask.
```

### Execute ALL Ready Plans In Parallel

```text
Execute every ready plan file under `ai/` in `Parallel Plans` mode using git worktrees.

Treat ready plans as the non-archived `ai/PLAN_*.md` files still present directly under `ai/` whose `Lifecycle` status is `Ready`.
Use `ai/WORKFLOW.md` and `ai/EXECUTION.md`.
Restate exactly which ready plan files were selected and which non-archived plan files were skipped because they were not `Ready`.
If there are no ready plans, stop and say so explicitly.
If only one ready plan exists, stop and say that a single-plan execution prompt should be used instead.
Do not silently skip a `Ready` plan just to force a smaller parallel-safe set.
If any ready plans are too coupled for safe parallel execution, stop and explain why instead of forcing `Parallel Plans`.
Track per-plan branch, validation, private `CHANGELOG_<topic>.md`, worker log, and PR status.
Do not release unless I ask.
```

### Implement All Unfinished Plans In Parallel

```text
Implement all unfinished plan files under `ai/` in parallel using git worktrees.

Treat unfinished plans as the non-archived `ai/PLAN_*.md` files still present directly under `ai/`.
Use `ai/WORKFLOW.md` in `Parallel Plans` mode.
If there are no unfinished plans, stop and say so explicitly.
Group plans into parallel-safe workstreams only when their source ownership, contract artifacts, rollout order, and validation needs are genuinely disjoint.
Summarize which plans were completed, grouped together, or left blocked, plus the PR and validation status for each one.
Do not release unless I ask.
```

## Workflow Execution

Use these prompts when one request should actively execute planned work while also choosing or coordinating the workflow mode from `ai/WORKFLOW.md`.

### Execute One Plan And Infer Workflow Mode

```text
Execute `<plan_file>` using the workflow agent.

Use `ai/WORKFLOW.md` and `ai/EXECUTION.md`.
Infer `Single Branch` or `Shared Plan` from the plan file, especially `Execution Mode Fit`, milestone boundaries, and shared-file ownership.
If the plan file is not clear enough to choose safely, stop and explain the ambiguity instead of guessing.
Do not switch to `Parallel Plans` unless I pass multiple plan files.
```

### Execute One Plan On A Single Branch

```text
Execute `<plan_file>` in `Single Branch` mode.

Use `ai/WORKFLOW.md` and `ai/EXECUTION.md`.
Treat the canonical plan file and `CHANGELOG.md` as directly editable on the active branch.
Do not push, open a PR, or release unless I ask.
```

### Execute One Plan As Shared Plan

```text
Execute `<plan_file>` in `Shared Plan` mode.

Use `ai/WORKFLOW.md` and `ai/EXECUTION.md`.
Act as orchestrator, fan out worker branches or worktrees only for disjoint slices, keep shared files coordinator-owned, and require committed worker logs.
Push only the finished coordinator branch unless I explicitly ask otherwise.
```

### Execute Multiple Plans In Parallel

```text
Execute these plan files in `Parallel Plans` mode using git worktrees:
- <plan_file_1>
- <plan_file_2>
- <plan_file_3>

Use `ai/WORKFLOW.md` and `ai/EXECUTION.md`.
Track per-plan branch, validation, private `CHANGELOG_<topic>.md`, worker log, and PR status.
If any listed plans are too coupled for safe parallel execution, stop and explain why instead of forcing a different mode.
Do not release unless I ask.
```

### Check Status On One Worker

```text
Check the status of worker `<worker name or agent id>` in the current workflow execution.

Report mode, branch and worktree, current progress, changed files, validations run with results, commit SHA(s), blockers, ready-for-integration status, and the worker-log path when applicable.
If the worker has stalled or completed, state that clearly.
```

### Check Status On Active Workers

```text
Check the status of the active workers in the current workflow execution.

Use `ai/WORKFLOW.md`.
For each worker, report mode, branch and worktree, current task, changed files, validations, commit SHA(s), blockers, ready-for-integration status, and the worker-log path when applicable.
Keep the report concise and factual.
```

## Implementation Integration

Use these prompts after worker implementation is already done and the next task is to fold ready output back into the canonical plan, changelog, and integration branch.

### Integrate Completed Shared-Plan Worker Output

```text
Integrate completed worker output for `<plan_file>`.

Use `ai/WORKFLOW.md` as coordinator in `Shared Plan` mode.
Merge or cherry-pick ready worker branches, fold accepted worker-log content into the canonical plan and `CHANGELOG.md`, run the required integration validation, and summarize what landed and what remains.
```

## Implementation Verification

### Run Required Validation Only

```text
Run only the required validation for `<plan_file>` or `<change>`.
Do not edit files.

Use `ai/TESTING.md` and summarize what ran, what passed, what failed, and what artifacts would likely need updates.
```

### Verify Contract Impact

```text
Review `<change>` for public contract impact.

State whether it requires updates to tests, REST Docs, Asciidoc, approved OpenAPI, HTTP examples, `README.md`, or benchmarks.
If the change is internal-only, say that explicitly and explain why.
```

### Verify An Implemented Milestone

```text
Verify `<milestone_name>` from `<plan_file>` after implementation.

Use `ai/TESTING.md` and `ai/REVIEWS.md`.
Check validation coverage, artifact updates, milestone commit completeness, and any remaining blocker before the next milestone or integration step.
List blockers first.
```

### Review A Diff For Risks

```text
Review this change with a code review mindset.

Use `ai/REVIEWS.md`.
List findings first, ordered by severity, with file references.
Keep the summary brief.
```

## Preparing Release

### Verify Release Readiness

```text
Review release readiness for `<plan_file>`.

Use `ai/RELEASES.md`.
List blockers first.
Say explicitly if the repository is release-ready.
```

### Prepare A Release Only

```text
Prepare a release for `<plan_file>`.

Follow `ai/RELEASES.md`.
Only proceed if the approved implementation PR is already merged onto `main`.
Do not push unless I ask.
```

## Releasing

### Push An Already Prepared Release

```text
Push the current release commit and annotated tag to the remote.

Follow `ai/RELEASES.md` for push and post-push verification.
```

### Verify And Release All Merged PR Work

```text
Verify and release all merged but unreleased work currently on `main`.

Use `ai/RELEASES.md`.
Include every merged PR and executed `ai/PLAN_*.md` file that belongs to the unreleased change set.
If any included work is not release-ready, stop and list blockers first instead of preparing a partial release.
If the merged work is ready, prepare the release commit, archive every included executed plan under `ai/archive/`, create the annotated tag, push the release commit and tag, and verify remote publication.
Summarize exactly which merged PRs and executed plan files were included.
```

### Verify The Published Release

```text
Verify the already pushed release for `<plan_file>`.

Follow `ai/RELEASES.md` for the post-push checks.
Summarize exactly what was published.
```

## Other Useful Lifecycle Prompts

### Implement And Then Release

```text
Implement `<plan_file>`.

Use `ai/EXECUTION.md` for implementation.
Use `ai/RELEASES.md` only after the approved implementation PR has been merged onto `main`.
```

### Summarize The Current Lifecycle State

```text
Summarize the current lifecycle state for `<plan_file>` or the current change.

Use `ai/PLAN.md`, `ai/EXECUTION.md`, and `ai/WORKFLOW.md` as needed.
Report phase, status, active milestone, pending validations, integration state, and the next recommended step.
```

### Triage A Failed Validation Run

```text
Triage the failing validation for `<plan_file>` or `<change>`.

Use `ai/TESTING.md` and `ai/REVIEWS.md`.
Identify the first real failure, likely root cause, whether it looks like a spec break or implementation bug, and the smallest next fix.
```

## Maintenance

### Compact AI Instruction Files

```text
Compact the standing AI instruction files.

Read `AGENTS.md` and the current AI instruction files under `ai/` first.
Keep each file role-distinct.
Move duplicated standing guidance into the best owning AI document.
Tighten wording, remove overlap, update cross-references, and summarize the compaction decisions.
Propose creating new files only if the current owners cannot stay role-distinct without them.
```
