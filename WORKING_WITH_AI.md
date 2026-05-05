# Working With AI

`WORKING_WITH_AI.md` is the human-facing guide for using AI in this repository across the full application-development lifecycle.

Use this file when you want to direct AI effectively as a developer.
Use `AGENTS.md` and the files under `ai/` as the repository-local instructions that the AI should follow.
Use `README.md` for the short project overview and the AI-document map.
Use `SETUP.md` for local environment setup and `CONTRIBUTING.md` for contributor workflow expectations.

## Core Working Model

This repository uses spec-driven development.
That matters just as much when AI is helping as when a human is working alone.

The normal loop is:

1. Frame the change in terms of behavior, not code.
2. Identify the governing spec or contract artifact.
3. Decide the current lifecycle phase.
4. Give AI the right owner documents for that phase.
5. Keep the work milestone-sized.
6. Review the diff, validation, and contract impact before moving to the next step.

Human responsibilities do not disappear when AI is involved.
The developer still owns scope, product intent, approval of tradeoffs, review of the output, and release decisions.

## What To Read First

Before asking AI to do real work, make sure the session is grounded in these repository files:

- `README.md` for the project overview, implemented scope, and AI document map
- `AGENTS.md` for repository-specific AI rules, spec priority, and required artifact updates
- `SETUP.md` for local tooling, environment, and verification prerequisites
- `ai/ENVIRONMENT_QUICK_REF.md` for AI-friendly local Gradle wrapper commands
- `ai/PROMPTS.md` for reusable prompt titles; load one raw prompt body with `scripts/ai/get-prompt.ps1` only after a title is invoked
- `ai/skills/<skill>/SKILL.md` only when you want a narrower repo-local workflow wrapper

Then add the phase-specific owner guide:

- discovery and roadmap work: `ROADMAP.md` and `ai/PLAN.md`
- planning: `ai/PLAN.md`
- implementation: `ai/EXECUTION.md`
- delegated or worktree execution: `ai/WORKFLOW.md`
- verification: `ai/TESTING.md` and `ai/REVIEWS.md`
- release preparation and release: `ai/RELEASES.md`

## Repo-Local Skills

Repo-local skills live under `ai/skills/`:

- `repo-plan-author`: focused entry point for creating or revising `ai/PLAN_*.md`
- `gh-fix-ci`: focused entry point for GitHub PR-check inspection, GitHub Actions log triage, and approval-first CI fix planning
- `gh-fix-security-quality`: focused entry point for GitHub Security tab inspection, code-scanning and Dependabot alert triage, and approval-first security fix planning

Use them when you want a narrower workflow wrapper than the general prompt library.
Treat them as helpers that point back to the owner guides, not as higher-priority policy.

## Useful Shared Reference Sections

Some AI-facing docs are also readable by human maintainers when you want a short codebase map or workflow summary.
Use these as focused entry points instead of reading every AI guide end to end.
Detailed prompt bodies, templates, deep references, skill references, and archived plans are on-demand material.

Architecture and product direction:

- [System Purpose](ai/ARCHITECTURE.md#system-purpose)
- [API Shape](ai/ARCHITECTURE.md#api-shape)
- [Feature Packages](ai/BUSINESS_MODULES.md#feature-packages)
- [Product Intent](ai/DESIGN.md#product-intent)
- [Non-Goals](ai/DESIGN.md#non-goals)

Planning and execution:

- [Lifecycle Metadata](ai/PLAN.md#lifecycle-metadata)
- [Plan Output Format](ai/PLAN.md#plan-output-format)
- [Common Milestone Loop](ai/EXECUTION.md#common-milestone-loop)
- [Supported Modes](ai/WORKFLOW.md#supported-modes)
- [Preferred Commands](ai/ENVIRONMENT_QUICK_REF.md#preferred-commands)

Validation and release:

- [Change-Type Expectations](ai/TESTING.md#change-type-expectations)
- [Standard Command](ai/TESTING.md#standard-command)
- [Release Preconditions](ai/RELEASES.md#release-preconditions)
- [Release Checklist](ai/RELEASES.md#release-checklist)

## A Good Request To AI

Good requests in this repository are concrete.
At minimum, give AI:

- the goal
- the lifecycle phase
- the target files or plan file
- the constraints or non-goals
- the definition of done

This simple structure works well:

```text
Goal:
Phase:
Target artifacts:
Constraints:
Definition of done:
```

## Recommended Lifecycle

### 1. Discovery

Use AI to turn rough ideas into concrete candidate work without jumping into implementation too early.

Typical inputs:

- `ROADMAP.md`
- `README.md`
- `ai/PLAN.md`
- `ai/DESIGN.md` when product direction matters

Good outcomes:

- clarified roadmap wording
- identified requirement gaps
- a recommendation that work should stay in `Discovery`
- a clear workstream that is ready to move into planning

Useful prompt titles from `ai/PROMPTS.md`:

- `Clarify Roadmap Decisions`
- `Refine Roadmap Intake`
- `Pick Next Roadmap Workstream`
- `Review Roadmap Item`

### 2. Planning

Use AI to create or revise a real execution plan under `ai/PLAN_*.md`.

The plan should be decision-complete enough that implementation does not need to improvise product behavior.
Milestones should be small enough to implement, validate, and commit one checkpoint at a time.

Good outcomes:

- one coherent `ai/PLAN_<topic>.md`
- multiple plan files only when the work is genuinely disjoint
- explicit scope, non-goals, validation, and execution-mode fit

Useful prompt titles:

- `Create Plan`
- `Plan From Roadmap`
- `Plan Checked Roadmap Items`
- `Split Checked Roadmap Items Into Plans`
- `Revise Plan`

### 3. Plan Verification

Before implementing, have AI review the plan as a plan.

The review should answer:

- is the lifecycle state accurate?
- are the requirement gaps explicit?
- are the milestones clean checkpoint boundaries?
- is the execution mode obvious?

Useful prompt titles:

- `Review Plan Readiness`
- `Choose Execution Mode`

Do not skip this step for large or multi-step work.
It is much cheaper to fix the plan than to unwind a bad implementation stream later.

### 4. Implementation

Once the plan is ready, use AI to implement either the whole plan or one milestone.

Important repo rule:

- commit after each completed milestone

That means the milestone should not be treated as done until implementation, validation, tracking artifacts, and the commit checkpoint are all in place.

Useful prompt titles:

- `Implement Plan`
- `Implement Milestone`

### 5. Workflow Execution

This is where workflow mode matters during active execution.
Use the smallest mode that keeps ownership clear.

#### Single plan file

When you pass one plan file, there are three useful ways to work:

- let AI infer the right mode from the plan: `Run Plan With Inferred Mode`
- force direct execution on one branch: `Run Plan On Single Branch`
- force fanout with an orchestrator and workers: `Run Plan As Shared Plan`

#### Multiple plan files

When you pass multiple plan files, use `Parallel Plans`.
Use `Run Plans In Parallel` when you already know the exact plan-file set.
Use `Run All Ready Plans` when you want AI to discover every non-archived ready plan first and then run the same parallel execution flow.
Use `Run All Unfinished Plans` when you want AI to discover every non-archived unfinished plan first and then run the same parallel execution flow.

#### Workflow mode guide

- `Single Branch`: default for one plan and one execution stream
- `Shared Plan`: one current plan file, one coordinator, several worker branches or worktrees, shared files stay coordinator-owned
- `Parallel Plans`: multiple plan files executing in parallel, each worker keeps a private `CHANGELOG_<topic>.md`

For `Shared Plan` and `Parallel Plans`, the coordinator run is complete only when every worker has reached a terminal state.
That means the first finished worker is only progress, not the end of the coordinated run.
If you want an interim snapshot while work is still running, use the worker-status prompts and treat that output as progress reporting rather than completion.

Shared-plan and parallel-plan work also use committed worker logs at:

`ai/tmp/workflow/<plan_stem_or_topic>__<worker_name>.md`

Useful prompt titles:

- `Run Plan With Inferred Mode`
- `Run Plan On Single Branch`
- `Run Plan As Shared Plan`
- `Run Plans In Parallel`
- `Run All Ready Plans`
- `Run All Unfinished Plans`
- `Check Worker Status`
- `Check Active Workers`

### 6. Implementation Integration

Use this after worker implementation is already complete and the next task is to fold ready output from worker branches or open PRs back into the canonical plan or accepted plan branches, `CHANGELOG.md`, and the integration branch, then clean any temporary worker branches or worktrees that are no longer needed.

Useful prompt titles:

- `Integrate Shared Plan Output`
- `Integrate Parallel Plan Output`
- `Integrate All Open PRs`

### 7. Implementation Verification

Verification is a separate phase, not an afterthought.
Use AI to run validation, inspect contract impact, and review the change with a code-review mindset.

Typical outcomes:

- confirmation that the required checks passed
- identification of missing contract or documentation updates
- findings ordered by severity

Useful prompt titles:

- `Run Required Validation`
- `Check Contract Impact`
- `Verify Milestone`
- `Review Diff Risks`
- `Triage Validation Failure`

Repository rule:

- use `./build.ps1 compileJava` or a similarly focused wrapper task for quick implementation-loop checks, then run the standard wrapper build from `ai/ENVIRONMENT_QUICK_REF.md` before finishing; `./build.ps1 -FullBuild build` forces the full Gradle build when required
- do not run overlapping Gradle validation tasks in parallel, including `build` with `gatlingBenchmark`, `externalSmokeTest`, `externalDeploymentCheck`, or `scheduledExternalCheck`

### 8. Preparing Release

Release preparation starts only after the approved implementation PR has been merged onto `main`.

Useful prompt titles:

- `Check Release Readiness`
- `Prepare Release`

Typical preparation work includes:

- verifying the merged work is actually release-ready
- checking changelog, roadmap, and plan cleanup
- preparing the release commit and tag inputs without pushing yet

### 9. Releasing

Releasing is a maintainer step.
Do not ask AI to release unmerged branch work.

Useful prompt titles:

- `Push Prepared Release`
- `Release All Merged Work`
- `Check Published Release`

## How To Choose A Workflow Mode

Use this quick rule set:

- one plan file and no strong reason to split: `Single Branch`
- one plan file with disjoint worker-owned slices and one coordinator: `Shared Plan`
- multiple plan files: `Parallel Plans`

If you are unsure, ask AI to review the plan first with `Choose Execution Mode`.

## Suggested Developer Habits

- ask AI to name the spec artifacts before it edits code
- ask AI to say what is in scope and out of scope
- ask AI to list blockers explicitly instead of hiding them in assumptions
- prefer milestone-sized requests over long open-ended requests
- ask for validation and contract impact before approving the result
- keep release work separate from implementation work
- use the prompt titles in `ai/PROMPTS.md` as reusable commands when you want a consistent repository-local workflow

## A Practical End-To-End Example

For a normal feature or cleanup, a good sequence is:

1. use `Pick Next Roadmap Workstream`
2. use `Create Plan`
3. use `Review Plan Readiness`
4. use `Implement Milestone` or `Implement Plan`
5. use `Run Required Validation`
6. use `Review Diff Risks`
7. if the change is merged to `main`, use `Check Release Readiness` and then the release prompts when appropriate

For coordinated multi-branch work:

1. create or refine the relevant plan files
2. if there is one plan file, use one of the single-plan workflow prompts
3. if there are multiple plan files and you already know the exact set, use `Run Plans In Parallel`
4. if you want AI to discover every ready plan under `ai/`, use `Run All Ready Plans`
5. if you want AI to pick up every unfinished plan under `ai/`, use `Run All Unfinished Plans`
6. use the worker-status prompts while the work is running
7. integrate, verify, and release only after the normal gates are satisfied

## When To Slow Down AI

Ask AI to stop and clarify instead of continuing when:

- the intended behavior is still ambiguous
- public API behavior is changing without clear contract updates
- the plan hides unresolved scope or validation questions
- multiple workers would need to edit the same shared files
- the requested release work is not yet merged onto `main`

In this repository, speed is useful only when the spec, ownership, and validation path are still clear.
