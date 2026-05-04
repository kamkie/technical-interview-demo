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
- `ai/PROMPTS.md` for reusable prompt starters and shorthand titles
- `ai/skills/<skill>/SKILL.md` only when you want a narrower repo-local workflow wrapper for planning or validation

Then add the phase-specific owner guide:

- discovery and roadmap work: `ROADMAP.md` and `ai/PLAN.md`
- planning: `ai/PLAN.md`
- implementation: `ai/EXECUTION.md`
- delegated or worktree execution: `ai/WORKFLOW.md`
- verification: `ai/TESTING.md` and `ai/REVIEWS.md`
- release preparation and release: `ai/RELEASES.md`

## Repo-Local Skills

Two small repo-local skills live under `ai/skills/`:

- `repo-plan-author`: focused entry point for creating or revising `ai/PLAN_*.md`
- `repo-validation-gate`: focused entry point for changed-file classification, validation selection, and contract-impact triage

Use them when you want a narrower workflow wrapper than the general prompt library.
Treat them as helpers that point back to the owner guides, not as higher-priority policy.

## Useful Shared Reference Sections

Some AI-facing docs are also readable by human maintainers when you want a short codebase map or workflow summary.
Use these as focused entry points instead of reading every AI guide end to end.

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

- `Surface Roadmap Framing Decisions Interactively`
- `Refine Unrefined Roadmap Tasks Into Real Entries`
- `Select The Next Roadmap Workstream For Planning`
- `Review A Roadmap Item Before Planning`

### 2. Planning

Use AI to create or revise a real execution plan under `ai/PLAN_*.md`.

The plan should be decision-complete enough that implementation does not need to improvise product behavior.
Milestones should be small enough to implement, validate, and commit one checkpoint at a time.

Good outcomes:

- one coherent `ai/PLAN_<topic>.md`
- multiple plan files only when the work is genuinely disjoint
- explicit scope, non-goals, validation, and execution-mode fit

Useful prompt titles:

- `Create A New Execution Plan`
- `Create A Plan From Roadmap Input`
- `Create A Plan From Checked Roadmap Tasks`
- `Create Multiple Plans From Disjoint Checked Roadmap Tasks`
- `Revise An Existing Plan`

### 3. Plan Verification

Before implementing, have AI review the plan as a plan.

The review should answer:

- is the lifecycle state accurate?
- are the requirement gaps explicit?
- are the milestones clean checkpoint boundaries?
- is the execution mode obvious?

Useful prompt titles:

- `Review Whether A Plan Is Ready`
- `Decide How One Plan Should Execute`

Do not skip this step for large or multi-step work.
It is much cheaper to fix the plan than to unwind a bad implementation stream later.

### 4. Implementation

Once the plan is ready, use AI to implement either the whole plan or one milestone.

Important repo rule:

- commit after each completed milestone

That means the milestone should not be treated as done until implementation, validation, tracking artifacts, and the commit checkpoint are all in place.

Useful prompt titles:

- `Implement A Plan Without Releasing`
- `Implement Only One Milestone`

### 5. Implementation Integration

This is where workflow mode matters.
Use the smallest mode that keeps ownership clear.

#### Single plan file

When you pass one plan file, there are three useful ways to work:

- let AI infer the right mode from the plan: `Execute One Plan And Infer Workflow Mode`
- force direct execution on one branch: `Execute One Plan On A Single Branch`
- force fanout with an orchestrator and workers: `Execute One Plan As Shared Plan`

#### Multiple plan files

When you pass multiple plan files, use `Parallel Plans`.
Use `Execute Multiple Plans In Parallel` when you already know the exact plan-file set.
Use `Execute ALL Ready Plans In Parallel` when you want AI to select every non-archived `ai/PLAN_*.md` file whose `Lifecycle` status is `Ready`.

#### Workflow mode guide

- `Single Branch`: default for one plan and one execution stream
- `Shared Plan`: one current plan file, one coordinator, several worker branches or worktrees, shared files stay coordinator-owned
- `Parallel Plans`: multiple plan files executing in parallel, each worker keeps a private `CHANGELOG_<topic>.md`

Shared-plan and parallel-plan work also use committed worker logs at:

`ai/tmp/workflow/<plan_stem_or_topic>__<worker_name>.md`

Useful prompt titles:

- `Execute One Plan And Infer Workflow Mode`
- `Execute One Plan On A Single Branch`
- `Execute One Plan As Shared Plan`
- `Execute Multiple Plans In Parallel`
- `Execute ALL Ready Plans In Parallel`
- `Integrate Completed Shared-Plan Worker Output`
- `Check Status On One Worker`
- `Check Status On Active Workers`

### 6. Implementation Verification

Verification is a separate phase, not an afterthought.
Use AI to run validation, inspect contract impact, and review the change with a code-review mindset.

Typical outcomes:

- confirmation that the required checks passed
- identification of missing contract or documentation updates
- findings ordered by severity

Useful prompt titles:

- `Run Required Validation Only`
- `Verify Contract Impact`
- `Verify An Implemented Milestone`
- `Review A Diff For Risks`
- `Triage A Failed Validation Run`

Repository rule:

- run `.\gradlew.bat build` before finishing, unless `pwsh ./scripts/classify-changed-files.ps1 -Uncommitted` reports `skipHeavyValidation=true` for the current uncommitted changes and manual consistency review is sufficient

### 7. Preparing Release

Release preparation starts only after the approved implementation PR has been merged onto `main`.

Useful prompt titles:

- `Verify Release Readiness`
- `Prepare A Release Only`

Typical preparation work includes:

- verifying the merged work is actually release-ready
- checking changelog, roadmap, and plan cleanup
- preparing the release commit and tag inputs without pushing yet

### 8. Releasing

Releasing is a maintainer step.
Do not ask AI to release unmerged branch work.

Useful prompt titles:

- `Push An Already Prepared Release`
- `Verify And Release All Merged PR Work`
- `Verify The Published Release`

## How To Choose A Workflow Mode

Use this quick rule set:

- one plan file and no strong reason to split: `Single Branch`
- one plan file with disjoint worker-owned slices and one coordinator: `Shared Plan`
- multiple plan files: `Parallel Plans`

If you are unsure, ask AI to review the plan first with `Decide How One Plan Should Execute`.

## Suggested Developer Habits

- ask AI to name the spec artifacts before it edits code
- ask AI to say what is in scope and out of scope
- ask AI to list blockers explicitly instead of hiding them in assumptions
- prefer milestone-sized requests over long open-ended requests
- ask for validation and contract impact before approving the result
- keep release work separate from implementation work
- use the prompt titles in `ai/PROMPTS.md` when you want a consistent repository-local workflow

## A Practical End-To-End Example

For a normal feature or cleanup, a good sequence is:

1. use `Select The Next Roadmap Workstream For Planning`
2. use `Create A New Execution Plan`
3. use `Review Whether A Plan Is Ready`
4. use `Implement Only One Milestone` or `Implement A Plan Without Releasing`
5. use `Run Required Validation Only`
6. use `Review A Diff For Risks`
7. if the change is merged to `main`, use `Verify Release Readiness` and then the release prompts when appropriate

For coordinated multi-branch work:

1. create or refine the relevant plan files
2. if there is one plan file, use one of the single-plan workflow prompts
3. if there are multiple plan files and you already know the exact set, use `Execute Multiple Plans In Parallel`
4. if you want AI to discover every ready plan under `ai/`, use `Execute ALL Ready Plans In Parallel`
5. use the worker-status prompts while the work is running
6. integrate, verify, and release only after the normal gates are satisfied

## When To Slow Down AI

Ask AI to stop and clarify instead of continuing when:

- the intended behavior is still ambiguous
- public API behavior is changing without clear contract updates
- the plan hides unresolved scope or validation questions
- multiple workers would need to edit the same shared files
- the requested release work is not yet merged onto `main`

In this repository, speed is useful only when the spec, ownership, and validation path are still clear.
