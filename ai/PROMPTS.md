# Prompt Library For This Repository

`ai/PROMPTS.md` is a library of short reusable prompt starters for common repository workflows.
It does not own standing policy.

Keep prompts lean:

- repository rules live in `AGENTS.md`
- artifact routing lives in `ai/DOCUMENTATION.md`
- planning rules live in `ai/PLAN.md`
- single-agent execution rules live in `ai/EXECUTION.md`
- delegation rules live in `ai/WORKFLOW.md`
- release rules live in `ai/RELEASES.md`
- validation rules live in `ai/TESTING.md`
- review rules live in `ai/REVIEWS.md`

If a prompt starts reading like policy, move that policy back to the owner guide and keep only the task-specific constraint here.

## Prompt Usage Baseline

Default read set by task:

- planning: `AGENTS.md`, `ai/PLAN.md`, and the governing specs; add `README.md` or `ROADMAP.md` only when relevant
- implementation: `AGENTS.md`, `ai/EXECUTION.md`, and the target `ai/PLAN_*.md`
- validation: `AGENTS.md`, `ai/TESTING.md`, plus any owner guide needed to judge artifact impact
- release: `AGENTS.md`, `ai/RELEASES.md`, the executed plan, and the changed contract docs
- multi-agent: `AGENTS.md`, `ai/WORKFLOW.md`, `ai/EXECUTION.md`, and the relevant plan files

Use the owner guide named by the prompt instead of restating its standing policy in the request.
Prefer filling in placeholders such as `<topic>`, `<plan_file>`, `<milestone_name>`, `<task>`, and `<constraint>` so the request is concrete.

## Title Shorthand

You may invoke a prompt in this file by using its `###` title as shorthand for the full starter under that heading.

Rules:

- use the exact prompt title or an unmistakably close reference to one prompt title in this file
- supply the required placeholders or equivalent concrete context in the same request
- treat section headings such as `## Planning` and `## Validation` as categories, not invocable prompts
- if the title match is ambiguous or required context is missing, stop and ask a targeted clarification question instead of guessing

Example shorthand:

```text
Use `Implement A Plan Without Releasing` for `ai/PLAN_auth_cleanup.md`.
```

## Planning

### Create A New Execution Plan

```text
Create `ai/PLAN_<topic>.md` for <topic>.
Follow `ai/PLAN.md`.
```

### Create A Plan From Roadmap Input

```text
Create `ai/PLAN_<topic>.md` from this roadmap input:
- <task 1>
- <task 2>

Treat `ROADMAP.md` only as roadmap input and follow `ai/PLAN.md`.
```

### Create A Plan From Checked Roadmap Tasks

```text
Create one coherent `ai/PLAN_<topic>.md` from every checklist item marked `[x]` in `ROADMAP.md`.

Use only the checked items unless the roadmap text makes a dependency explicit.
Restate exactly which checked items were included.
If the checked items do not form one coherent executable plan, stop and explain the gap instead of guessing.
Record unresolved requirement gaps and fallback assumptions explicitly.
```

### Create Multiple Plans From Disjoint Checked Roadmap Tasks

```text
Create one or more `ai/PLAN_<topic>.md` files from every checklist item marked `[x]` in `ROADMAP.md`.

Split only genuinely disjoint workstreams that can later execute in parallel without overlapping source ownership, contract artifacts, rollout order, or validation.
If the checked items form only one coherent plan, stop and say that the single-plan prompt should be used instead.
Record requirement gaps, fallback assumptions, and any cross-plan dependency notes in each created plan.
```

### Refine Unrefined Roadmap Tasks Into Real Entries

```text
Refine the items under `## Not Yet Refined` in `ROADMAP.md` into concrete roadmap entries.

Treat that section as rough intake only.
Move refined tasks only when the resulting roadmap wording is concrete enough to sequence.
Leave still-ambiguous tasks in place and explain what is missing.
```

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

### Revise An Existing Plan

```text
Revise `<plan_file>` for this new requirement or constraint:
<constraint>

Keep the plan self-contained and follow `ai/PLAN.md`.
```

### Review Whether A Plan Is Ready

```text
Review `<plan_file>` against `ai/PLAN.md`.

List concrete gaps or ambiguities first.
Say explicitly if the plan is ready.
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

### Implement And Then Release

```text
Implement `<plan_file>`.

Use `ai/EXECUTION.md` for implementation.
Use `ai/RELEASES.md` only after the approved implementation PR has been merged onto `main`.
```

## Validation

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

### Verify Release Readiness

```text
Review release readiness for `<plan_file>`.

Use `ai/RELEASES.md`.
List blockers first.
Say explicitly if the repository is release-ready.
```

## Review

### Review A Diff For Risks

```text
Review this change with a code review mindset.

Use `ai/REVIEWS.md`.
List findings first, ordered by severity, with file references.
Keep the summary brief.
```

### Review A Roadmap Item Before Planning

```text
Review the roadmap item `<task>` before planning implementation.

Explain what behavior would change, which current specs govern it, what is still ambiguous, and whether the task is ready for a concrete plan.
```

## Release

### Prepare A Release Only

```text
Prepare a release for `<plan_file>`.

Follow `ai/RELEASES.md`.
Only proceed if the approved implementation PR is already merged onto `main`.
Do not push unless I ask.
```

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

## Multi-Agent

### Execute A Plan With Parallel Workers

```text
Execute `<plan_file>` with delegation.

Use `ai/WORKFLOW.md` as coordinator.
Prefer `Shared Plan` unless `ai/WORKFLOW.md` justifies `Parallel Plans`.
Assign explicit disjoint ownership boundaries.
Enforce the worker artifact rules from `ai/WORKFLOW.md`.
Push only the finished coordinator branch unless I explicitly ask otherwise.
```

### Implement Multiple Plans In Parallel With Worktrees

```text
Implement these plan files in parallel using git worktrees:
- <plan_file_1>
- <plan_file_2>
- <plan_file_3>

Use `ai/WORKFLOW.md`.
Act as coordinator in `Parallel Plans` mode only when the listed plans are genuinely disjoint in source ownership, contract artifacts, rollout order, and validation needs.
If they are too coupled, stop and explain which plans should stay in one execution stream instead of forcing parallel worktrees.
Track per-plan branch, validation, and PR status.
Do not release unless I ask.
```

### Implement All Unfinished Plans In Parallel With Worktrees

```text
Implement all unfinished plan files under `ai/` in parallel using git worktrees.

Treat unfinished plans as the non-archived `ai/PLAN_*.md` files still present directly under `ai/`.
Use `ai/WORKFLOW.md`.
If there are no unfinished plans, stop and say so explicitly.
Group plans into parallel-safe workstreams only when their source ownership, contract artifacts, rollout order, and validation needs are genuinely disjoint.
Summarize which plans were completed, grouped together, or left blocked, plus the PR and validation status for each one.
Do not release unless I ask.
```

### Check Status on Single Worker In Multi-Agent Execution

```text
Check the status of worker `<worker name or agent id>` in the current multi-agent execution.

Report mode, branch and worktree, current progress, changed files, validations run with results, commit SHA(s), blockers, ready-for-integration status, and the worker progress-file path when applicable.
If the worker has stalled or completed, state that clearly.
```

### Check Status On Workers In Multi-Agent Execution

```text
Check the status of the active workers in the current multi-agent execution.

Use `ai/WORKFLOW.md`.
For each worker, report mode, branch and worktree, current task, changed files, validations, commit SHA(s), blockers, ready-for-integration status, and the worker progress-file path when applicable.
Keep the report concise and factual.
```

### Decide Whether Multi-Agent Execution Is Worth It

```text
Review `<plan_file>` and decide whether it should be executed by one agent or with delegation.

Use `ai/WORKFLOW.md`.
If delegation is worth it, choose `Parallel Plans` or `Shared Plan`, then propose the task split and file-ownership boundaries.
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
