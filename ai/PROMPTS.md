# Prompt Library For This Repository

`ai/PROMPTS.md` is a library of short reusable prompt starters for common repository workflows.

Keep prompts lean:

- put standing repository policy in `AGENTS.md`
- put planning rules in `ai/PLAN.md`
- put implementation rules in `ai/EXECUTION.md`
- put multi-agent rules in `ai/WORKFLOW.md`
- put release rules in `ai/RELEASES.md`

Prefer filling in placeholders such as `<topic>`, `<plan_file>`, `<milestone_name>`, `<task>`, and `<constraint>` so the request is concrete.

## Planning

### Create A New Execution Plan

```text
Create `ai/PLAN_<topic>.md` for <topic>.

Read `AGENTS.md`, `README.md`, `ai/PLAN.md`, and the governing specs first.
Follow `ai/PLAN.md`.
Ask targeted clarification questions before locking the plan if scope, compatibility, rollout, acceptance criteria, or validation are still ambiguous.
Record requirement gaps, unresolved user-input holes, and fallback assumptions explicitly.
```

### Create A New Execution Plan From A Roadmap Item

```text
Create plan file for <topic> from ROADMAP.md.

Read `AGENTS.md`, `README.md`, `ai/PLAN.md`, and the governing specs first.
Follow `ai/PLAN.md`.
Ask targeted clarification questions before locking the plan if scope, compatibility, rollout, acceptance criteria, or validation are still ambiguous.
Record requirement gaps, unresolved user-input holes, and fallback assumptions explicitly.
```

### Create A Plan From Selected Roadmap Tasks

```text
Create `ai/PLAN_<topic>.md` for these selected roadmap tasks:
- <task 1>
- <task 2>
- <task 3>

Use `ROADMAP.md` only as roadmap input.
Read `AGENTS.md`, `README.md`, `ROADMAP.md`, `ai/PLAN.md`, and the governing specs first.
Follow `ai/PLAN.md`.
Ask targeted clarification questions before locking the plan if scope, compatibility, rollout, acceptance criteria, or validation are still ambiguous.
Record requirement gaps, unresolved user-input holes, and fallback assumptions explicitly.
```

### Revise An Existing Plan

```text
Revise `<plan_file>` for this new requirement or constraint:
<constraint>

Keep the plan self-contained.
Follow `ai/PLAN.md`.
Record any new requirement gaps, unresolved user-input holes, and fallback assumptions explicitly.
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

### Implement A Plan And Complete The Release Flow

```text
Implement `<plan_file>`.

Read `AGENTS.md`, `ai/EXECUTION.md`, `<plan_file>`, and `ai/RELEASES.md` first.
Follow `ai/EXECUTION.md` for implementation and `ai/RELEASES.md` for the release step after the approved implementation PR has been merged onto `main`.
```

### Implement Only One Milestone

```text
Implement only `<milestone_name>` from `<plan_file>`.

Read `AGENTS.md`, `ai/EXECUTION.md`, and `<plan_file>` first.
Follow `ai/EXECUTION.md`.
Do not start later milestones, push, open a PR, or release unless I ask.
```

## Validation

### Run Required Validation Only

```text
Run only the required validation for `<plan_file>` or `<change>`.
Do not edit files.

Use `AGENTS.md` and the relevant AI workflow documents to decide what checks are required.
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

Use `AGENTS.md`, `ai/EXECUTION.md`, and `ai/RELEASES.md`.
List blockers first.
Say explicitly if the repository is release-ready.
```

## Review

### Review A Diff For Risks

```text
Review this change with a code review mindset.

Focus on bugs, regressions, missing validation, contract drift, and missing contract-artifact updates.
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

### Prepare And Execute Release For Plan File

```text
Prepare and execute the release for `<plan_file>`.

Read `AGENTS.md`, `ai/RELEASES.md`, the executed plan, and the changed contract docs first.
Follow `ai/RELEASES.md`.
Only proceed if the approved implementation PR is already merged onto `main`.
```

### Push An Already Prepared Release

```text
Push the current release commit and annotated tag to the remote.

Follow `ai/RELEASES.md` for the push and post-push verification steps.
```

### Verify The GitHub Release After Push

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
```

### Decide Whether Multi-Agent Execution Is Worth It

```text
Review `<plan_file>` and decide whether it should be executed by one agent or with delegation.

Use `ai/WORKFLOW.md`.
If delegation is worth it, propose the task split and file ownership boundaries.
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
