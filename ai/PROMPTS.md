# Prompt Library For This Repository

`ai/PROMPTS.md` is a library of short reusable prompt starters for common repository workflows.

Keep prompts lean:

- put standing repository policy in `AGENTS.md`
- put planning rules in `ai/PLAN.md`
- put execution rules in `ai/EXECUTION.md`
- put delegated-work rules in `ai/WORKFLOW.md`
- put release rules in `ai/RELEASES.md`
- use `ai/CODE_STYLE.md`, `ai/TESTING.md`, `ai/REVIEWS.md`, and `ai/DOCUMENTATION.md` only when the task needs those lenses

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

Read `AGENTS.md`, `README.md`, `ai/PLAN.md`, and the governing specs first.
Follow `ai/PLAN.md`.
```

### Create A Plan From Roadmap Input

```text
Create `ai/PLAN_<topic>.md` from this roadmap input:
- <task 1>
- <task 2>

Use `ROADMAP.md` only as roadmap input.
Read `AGENTS.md`, `README.md`, `ROADMAP.md`, `ai/PLAN.md`, and the governing specs first.
Follow `ai/PLAN.md`.
```

### Create A Plan From Checked Roadmap Tasks

```text
Create a new execution plan from every checklist item marked `[x]` in `ROADMAP.md`.

Read `AGENTS.md`, `README.md`, `ROADMAP.md`, `ai/PLAN.md`, and the governing specs first.
Follow `ai/PLAN.md`.
Use only the checked `[x]` roadmap items as the requested work scope unless the roadmap text around them makes a dependency explicit.
Choose an appropriate short lowercase underscore-separated topic name based on the combined checked tasks, then create the new file as `ai/PLAN_<topic>.md`.
In the plan summary and scope, restate exactly which checked roadmap items were included.
If no roadmap items are checked, or the checked items do not describe one coherent executable plan, stop and explain the gap instead of guessing.
Ask targeted clarification questions before locking the plan if scope, compatibility, rollout, acceptance criteria, or validation are still ambiguous.
Record requirement gaps, unresolved user-input holes, and fallback assumptions explicitly.
```

### Create Multiple Plans From Disjoint Checked Roadmap Tasks

```text
Create one or more new execution plans from every checklist item marked `[x]` in `ROADMAP.md`.

Read `AGENTS.md`, `README.md`, `ROADMAP.md`, `ai/PLAN.md`, and `ai/WORKFLOW.md` first.
Follow `ai/PLAN.md`.
Use only the checked `[x]` roadmap items as the requested work scope unless the roadmap text around them makes a dependency explicit.
Split the checked tasks into multiple plan files only when they describe genuinely disjoint work that can be planned and later executed in parallel without hidden coupling.
Treat shared contract artifacts, overlapping source-file ownership, rollout dependencies, or shared validation gates as reasons not to split.
For each disjoint workstream, choose an appropriate short lowercase underscore-separated topic name and create a separate file as `ai/PLAN_<topic>.md`.
In each plan summary and scope, restate exactly which checked roadmap items were included in that file.
If the checked items form only one coherent plan, stop and say that the single-plan checked-roadmap prompt should be used instead of forcing multiple files.
If some checked items are disjoint but others are not, create plan files only for the cleanly separated groups and explain why the remaining items were not split out.
If no roadmap items are checked, or the checked items do not describe one or more executable plans, stop and explain the gap instead of guessing.
Ask targeted clarification questions before locking any plan if scope, compatibility, rollout, acceptance criteria, validation, or cross-plan boundaries are still ambiguous.
Record requirement gaps, unresolved user-input holes, fallback assumptions, and any cross-plan dependency notes explicitly in each created plan.
```

### Refine Unrefined Roadmap Tasks Into Real Entries

```text
Refine the items listed under `## Not Yet Refined` in `ROADMAP.md` into concrete roadmap entries.

Read `AGENTS.md`, `README.md`, `ROADMAP.md`, and `ai/PLAN.md` first.
Treat the `## Not Yet Refined` section as rough intake only, not as already-approved roadmap structure.
For each rough task, identify the intended behavior, missing decisions, likely governing specs, and the smallest roadmap wording that would make it implementation-ready.
Group related tasks when that produces a cleaner roadmap shape, but do not invent scope that is not supported by the rough input or current repo truth.
Update `ROADMAP.md` by moving refined tasks out of `## Not Yet Refined` and into the appropriate roadmap section only when the resulting entries are concrete enough to sequence.
If any task is still too ambiguous to refine safely, leave it in `## Not Yet Refined` and explain exactly what is missing.
Keep the roadmap focused on active planned work, not release history or speculative architecture.
```

### Revise An Existing Plan

```text
Revise `<plan_file>` for this new requirement or constraint:
<constraint>

Keep the plan self-contained.
Follow `ai/PLAN.md`.
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

Read `AGENTS.md`, `ai/EXECUTION.md`, and `<plan_file>` first.
Follow `ai/EXECUTION.md`.
Do not push, open a PR, or release unless I ask.
```

### Implement Only One Milestone

```text
Implement only `<milestone_name>` from `<plan_file>`.

Read `AGENTS.md`, `ai/EXECUTION.md`, and `<plan_file>` first.
Follow `ai/EXECUTION.md`.
Do not start later milestones, push, open a PR, or release unless I ask.
```

### Implement And Then Release

```text
Implement `<plan_file>`.

Read `AGENTS.md`, `ai/EXECUTION.md`, `<plan_file>`, and `ai/RELEASES.md` first.
Follow `ai/EXECUTION.md` for implementation.
Use `ai/RELEASES.md` only after the approved implementation PR has been merged onto `main`.
```

## Validation

### Run Required Validation Only

```text
Run only the required validation for `<plan_file>` or `<change>`.
Do not edit files.

Use `AGENTS.md`, `ai/TESTING.md`, and the relevant workflow docs to decide what checks are required.
Summarize what ran, what passed, what failed, and what artifacts would likely need updates.
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

Use `AGENTS.md`, `ai/TESTING.md`, `ai/DOCUMENTATION.md`, and `ai/RELEASES.md`.
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

Read `AGENTS.md`, `ai/RELEASES.md`, the executed plan, and the changed contract docs first.
Follow `ai/RELEASES.md`.
Only proceed if the approved implementation PR is already merged onto `main`.
Do not push unless I ask.
```

### Push An Already Prepared Release

```text
Push the current release commit and annotated tag to the remote.

Follow `ai/RELEASES.md` for the push and post-push verification steps.
```

### Verify And Release All Merged PR Work

```text
Verify and release all merged but unreleased work currently on `main`.

Read `AGENTS.md`, `CHANGELOG.md`, `ROADMAP.md`, `ai/TESTING.md`, and `ai/RELEASES.md` first.
Identify the merged PRs and executed `ai/PLAN_*.md` files already integrated onto `main` that belong to the unreleased change set.
Use `ai/RELEASES.md`.
Verify that the included merged work is actually release-ready, including plan `Validation Results`, required contract artifacts, roadmap cleanup needs, temporary changelog cleanup if present, and plan archival scope.
If any included merged PR or executed plan is not ready, stop and list blockers first instead of preparing a partial or misleading release.
If the merged work is ready, prepare the release commit, archive every included executed plan under `ai/archive/`, update moved-path references, create the annotated tag, push the release commit and tag, and verify remote publication.
Summarize exactly which merged PRs and executed plan files were included in the release.
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

Use `ai/WORKFLOW.md`.
Act as coordinator.
Use `Shared Plan` mode unless you can justify staying single-worker instead.
Assign explicit disjoint ownership boundaries inside the plan.
Reserve the canonical plan file and `CHANGELOG.md` for the coordinator.
Require each worker to keep a committed progress file at `ai/tmp/workflow/<plan_stem>__<worker_name>.md`.
Integrate worker progress back into the canonical plan file and `CHANGELOG.md` only after the worker slice is ready.
Push only the finished coordinator branch unless I explicitly ask for worker branches to be pushed too.
```

### Implement Multiple Plans In Parallel With Worktrees

```text
Implement these plan files in parallel using git worktrees:
- <plan_file_1>
- <plan_file_2>
- <plan_file_3>

Read `AGENTS.md`, `ai/EXECUTION.md`, `ai/WORKFLOW.md`, and each listed plan file first.
Use `ai/WORKFLOW.md`.
Act as coordinator.
Use `Parallel Plans` mode.
Create separate temporary worktrees only for plans that are genuinely disjoint in source ownership, contract artifacts, rollout order, and validation needs.
Keep `main` as the integration target and do not treat worktree branches as release branches.
Require each worker to follow `ai/EXECUTION.md` inside its owned plan exactly as a single worker would, including plan updates, changelog updates, milestone commits, validation, and final push after verification.
Assign explicit ownership boundaries per plan, including any expected merge-conflict handling for `CHANGELOG.md` or other shared artifacts.
Do not let workers edit the same controller, service, integration test, REST Docs artifact, OpenAPI artifact, or plan file in parallel.
If any listed plans are too coupled to execute safely in parallel, stop and explain which plans should be merged back into one execution stream instead of forcing worktrees.
Track per-plan branch, validation, and PR status.
Run the final repository validation only after the worker branches are complete enough for the requested handoff, then summarize per-plan progress, PR status, validation results, and any remaining blockers.
Do not release unless I ask.
```

### Implement All Unfinished Plans In Parallel With Worktrees

```text
Implement all unfinished plan files under `ai/` in parallel using git worktrees.

Read `AGENTS.md`, `ai/EXECUTION.md`, `ai/WORKFLOW.md`, and every non-archived `ai/PLAN_*.md` file under `ai/` first.
Use `ai/WORKFLOW.md`.
Act as coordinator.
Treat unfinished plans as the `ai/PLAN_*.md` files still present directly under `ai/`, excluding `ai/archive/`.
If there are no unfinished plan files, stop and say so explicitly.
Group the unfinished plans into parallel-safe workstreams only when their source ownership, contract artifacts, rollout order, and validation needs are genuinely disjoint.
Use `Parallel Plans` mode for those disjoint plan files.
Create separate temporary worktrees only for those disjoint workstreams.
If some unfinished plans are too coupled to execute safely in parallel, keep them in the same execution stream and explain the boundary instead of forcing one worktree per file.
Keep `main` as the integration target and do not treat worktree branches as release branches.
Require each worker to follow `ai/EXECUTION.md` inside its owned plan exactly as a single worker would, including plan updates, changelog updates, milestone commits, validation, and final push after verification.
Assign explicit ownership boundaries per workstream, including any expected merge-conflict handling for `CHANGELOG.md` or other shared artifacts.
Do not let workers edit the same controller, service, integration test, REST Docs artifact, OpenAPI artifact, or plan file in parallel.
Track which worker owns each plan file, plus branch, validation, and PR status.
Run the final repository validation only after the worker branches are complete enough for the requested handoff, then summarize which unfinished plans were completed, which were grouped together, which were left blocked or deferred, and what PR and validation status each one has.
Do not release unless I ask.
```

### Check Status on Single Worker In Multi-Agent Execution

In `Multi-Agent Execution`, check the status of worker `<worker name or agent id>`.

Report:
- mode in use: `Parallel Plans` or `Shared Plan`
- branch and worktree
- current task progress
- changed files
- validations run with results
- commit SHA(s)
- blockers
- ready-for-integration status
- worker progress file path when `Shared Plan` mode is in use

If the worker has stalled or completed, state that clearly.

### Check Status On Workers In Multi-Agent Execution

```text
Check the status of the active workers in the current multi-agent execution.

Use `ai/WORKFLOW.md`.
Act as coordinator.
For each active worker, report:
- worker name or agent id
- mode in use
- assigned branch and worktree
- current task or milestone
- changed files so far
- validation run with pass/fail status
- commit SHA(s) already created
- blockers, risks, or coordinator decisions needed
- whether the work is ready for integration onto `main`
- worker progress file path when `Shared Plan` mode is in use

If a worker is stalled, still in analysis, has no edits yet, or is already complete, say that explicitly.
Keep the report concise and factual.
```

### Decide Whether Multi-Agent Execution Is Worth It

```text
Review `<plan_file>` and decide whether it should be executed by one agent or with delegation.

Use `ai/WORKFLOW.md`.
If delegation is worth it, choose `Parallel Plans` or `Shared Plan`, then propose the task split and file ownership boundaries.
```

## Maintenance

### Compact AI Instruction Files

```text
Compact the standing AI instruction files.

Read `AGENTS.md` and the current AI instruction files under `ai/` first.
Keep each file role-distinct.
Move duplicated standing guidance into the best owning AI document.
Tighten wording, remove overlap, update cross-references, and summarize the compaction decisions.
```
