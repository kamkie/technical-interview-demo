# Prompt Library For This Repository

`ai/PROMPTS.md` is a reusable prompt library for common workflows in this repository.

Use these prompts as starting points, not rigid scripts.
Prefer filling in placeholders such as `<topic>`, `<plan_file>`, `<task>`, and `<constraint>` so the request is concrete enough to execute without guesswork.

These prompts assume the repository rules in:

- `AGENTS.md`
- `ai/PLAN.md`
- `ai/WORKFLOW.md`
- `ai/RELEASES.md`

## How To Use These Prompts

- use planning prompts when the work is not decision-complete yet
- use implementation prompts only after the relevant plan exists
- use release prompts only after the implementation is complete and validated
- use the multi-agent prompts only when you explicitly want delegation or parallel work
- if the task is only roadmap reprioritization, update `ROADMAP.md` instead of asking for a fake execution plan

## Planning

### Create A New Execution Plan

```text
Create a new execution plan for <topic>.

Before writing the plan, read `AGENTS.md`, `README.md`, `ai/PLAN.md`, and the relevant specs, docs, tests, and source files.
Create a new file `ai/PLAN_<topic>.md` using the required structure from `ai/PLAN.md`.

The plan must:
- define the exact behavior being changed
- name the governing spec artifacts
- name the likely files to change
- state what is in scope and out of scope
- preserve the current public contract unless I explicitly asked to change it
- say whether `README.md`, REST Docs, OpenAPI, HTTP examples, and benchmarks are affected
- include exact validation steps, including `.\gradlew.bat build`
```

### Create A Plan From Selected Roadmap Tasks

```text
Create a new execution plan for these selected roadmap tasks:
- <task 1>
- <task 2>
- <task 3>

Use `ROADMAP.md` only as the source of planned work, not as a substitute for a real spec.
Before writing the plan, inspect the current workflows, docs, tests, config, and source files that define the behavior today.
Create `ai/PLAN_<topic>.md`.

Lock assumptions that the executor should not revisit.
Call out edge cases, compatibility promises, and the exact validation needed to finish the work.
```

### Revise An Existing Plan

```text
Revise `<plan_file>` to account for this new requirement or constraint:
<constraint>

Keep the plan self-contained.
Update the summary, scope, current state, locked decisions, affected artifacts, milestones, edge cases, and validation plan as needed.
If the new requirement invalidates the current plan structure, say so clearly and reshape the milestones instead of patching around the problem.
```

### Review Whether A Plan Is Ready

```text
Review `<plan_file>` against `ai/PLAN.md`.

Check whether the plan:
- is self-contained
- names the governing specs
- separates scope from non-goals
- names the likely files to change
- includes repo-specific validation
- respects the demo scope
- does not hide contract, OpenAPI, docs, or benchmark consequences

List concrete gaps or ambiguities first.
If the plan is ready, say that explicitly.
```

## Implementation

### Implement A Plan Without Releasing

```text
Implement `<plan_file>`.

Before editing code, re-read `AGENTS.md`, `ai/PLAN.md`, and `<plan_file>`.
Implement the smallest change that satisfies the plan.

During execution:
- update `CHANGELOG.md` under `## [Unreleased]` after each completed task
- create a commit after each completed task
- update the plan's `Validation Results` section with what you actually ran
- preserve existing contract behavior unless the plan explicitly changes it

Do not create a release or tag unless I ask.
```

### Implement A Plan And Complete The Release Flow

```text
Implement `<plan_file>`.

After the whole plan is complete and validated, follow `ai/RELEASES.md` to prepare the release from `main`.
Do not cut the release from a side branch or worktree branch.
Do not push unless I explicitly ask for remote publication.
```

### Implement Only One Milestone

```text
Implement only `<milestone_name>` from `<plan_file>`.

Do not start later milestones.
Preserve the remaining plan structure.
Update `CHANGELOG.md` under `## [Unreleased]`, create the task commit, and record the validation you ran in the plan's `Validation Results`.
Do not release.
```

## Validation

### Run Required Validation Only

```text
Run the required validation for `<plan_file>` or `<change>`.
Do not edit files.

Use the repository rules to decide what checks are required.
Summarize:
- what you ran
- what passed
- what failed
- what files or contract artifacts would likely need updates
```

### Verify Contract Impact

```text
Review `<change>` for public contract impact.

State whether it requires updates to:
- integration tests
- REST Docs tests
- Asciidoc pages
- `src/test/resources/openapi/approved-openapi.json`
- HTTP examples under `src/test/resources/http/`
- `README.md`
- benchmarks

If the change is internal-only, say that explicitly and explain why.
```

### Verify Release Readiness

```text
Review whether the repository is ready for a release for `<plan_file>`.

Check:
- implementation complete
- intended changes integrated onto `main`
- `CHANGELOG.md` aligned
- plan `Validation Results` updated
- required contract docs updated
- `.\gradlew.bat build` passing

List blockers first.
If release-ready, say so explicitly.
```

## Review

### Review A Diff For Risks

```text
Review this change with a code review mindset.

Focus on:
- bugs
- regressions
- missing validation
- contract drift
- missing docs, OpenAPI, REST Docs, HTTP examples, or benchmark updates

List findings first, ordered by severity, with file references.
Keep the summary brief.
```

### Review A Roadmap Item Before Planning

```text
Review the roadmap item `<task>` before planning implementation.

Explain:
- what behavior would actually change
- which current specs or docs govern that behavior
- what is still ambiguous
- whether the task is ready for a concrete execution plan
- whether a smaller or better-scoped prerequisite should be planned first
```

## Release

### Prepare A Release Only

```text
Prepare a release for the completed plan `<plan_file>`.

Follow `ai/RELEASES.md`.
Read the executed plan, `CHANGELOG.md`, and any changed contract docs first.

Choose the next semantic version deliberately.
Update `CHANGELOG.md`.
Create the release commit using the repository commit message pattern.
Create the annotated tag.

Do not push unless I ask.
```

### Push An Already Prepared Release

```text
Push the current release commit and annotated tag to the remote.

Verify that:
- the current branch is `main`
- the worktree is clean
- the release commit and tag already exist locally
- the remote accepted both the branch update and the tag
```

### Publish The GitHub Release After Local Release Prep

```text
Publish the already prepared release on GitHub.

Assume the release commit and annotated tag already exist locally.
Do not change version numbers or rewrite the release metadata unless a validation issue forces it.
Summarize exactly what was published.
```

## Multi-Agent

### Execute A Plan With Parallel Workers

```text
Use the multi-agent workflow from `ai/WORKFLOW.md` for `<plan_file>`.

Act as coordinator.
Split the plan into bounded tasks with explicit file ownership.
Keep shared integration files such as `CHANGELOG.md` and the plan's `Validation Results` under coordinator ownership.
Integrate completed work back onto `main`.
Run final validation on `main`.
Only create the release after all intended work is integrated and validated.
```

### Decide Whether Multi-Agent Execution Is Worth It

```text
Review `<plan_file>` and decide whether it should be executed with one agent or a multi-agent workflow.

Use `ai/WORKFLOW.md`.
If you recommend parallel execution, propose the task split and file ownership boundaries.
If you recommend a single agent, explain what overlap or coupling makes parallelism a poor fit.
```

## Examples

### Example: Plan Selected Release-Readiness Work

```text
Create a new execution plan for these selected roadmap tasks:
- Remove insecure production-style defaults and fail fast when required secrets or database credentials are missing in `prod`
- Enable the Dependabot workflow and keep dependency update automation aligned with the repository CI/release flow
- Make the `release.yml` GitHub Actions workflow create a GitHub Release with release notes pulled from `CHANGELOG.md` and a link to the published Docker image
- Remove machine-specific local paths and personal workstation details from `SETUP.md` and replace them with portable examples and placeholders

Create `ai/PLAN_selected_release_readiness_tasks.md`.
```

### Example: Implement The Selected Release-Readiness Plan

```text
Implement `ai/PLAN_selected_release_readiness_tasks.md`.
Do not release yet.
```

### Example: Implement And Release In One Go

```text
Implement `ai/PLAN_selected_release_readiness_tasks.md`.

Use the multi-agent workflow from `ai/WORKFLOW.md`.
Act as coordinator.
Keep shared integration files such as `CHANGELOG.md` and the plan's `Validation Results` under coordinator ownership.
Integrate completed work back onto `main`.
Run final validation on `main`.
After the whole plan is complete and validated, follow `ai/RELEASES.md` to prepare the release from `main`.
Do not push unless I explicitly ask for remote publication.
```

### Example: Prepare The Release After Implementation

```text
Prepare a release for `ai/PLAN_selected_release_readiness_tasks.md`.
Do not push.
```
