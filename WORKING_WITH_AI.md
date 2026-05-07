# Working With AI

`WORKING_WITH_AI.md` is the human-facing guide for using AI in this repository across the application-development lifecycle.

Use this file when you want to direct AI effectively as a developer.
Use `AGENTS.md` and the files under `ai/` as the repository-local instructions that the AI should follow.
Use `README.md` for the short project overview, `SETUP.md` for local environment setup, and `CONTRIBUTING.md` for contributor workflow expectations.

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

## Where Rules Live

Use this guide as a navigation aid, not as a second copy of the AI runbooks.

| Need | Start With |
| --- | --- |
| Project overview and implemented scope | `README.md` |
| Local setup, tools, and troubleshooting | `SETUP.md` |
| Repository-specific AI rules and phase owner map | `AGENTS.md` |
| Reusable task starters | `ai/TASK_LIBRARY.md` |
| Creating or revising execution plans | `ai/PLANNING.md` |
| Executing a whole approved plan | `ai/PLAN_EXECUTION.md` |
| Implementing an ad hoc task or one milestone | `ai/EXECUTION.md` |
| Delegation, worktrees, worker logs, or integration mechanics | `ai/WORKFLOW.md` |
| Validation scope and review activity | `ai/TESTING.md` and `ai/REVIEWS.md` |
| Documentation and artifact routing | `ai/DOCUMENTATION.md` |
| Intentional release preparation after integration | `ai/RELEASES.md` |

Detailed task sections, templates, deep references, skill references, and archived plans are on-demand material.
Load them only when the task title, owner guide, or active work calls for them.

## A Good Request To AI

Good requests in this repository are concrete.
At minimum, give AI:

- the reusable task title, when using one
- the goal
- the lifecycle phase
- the target files or plan file
- the constraints or non-goals
- the definition of done

This simple structure works well:

```text
Task:
Goal:
Phase:
Target artifacts:
Constraints:
Definition of done:
```

Examples:

```text
Create Plan
topic: add candidate search filtering
```

```text
Implement Plan
plan_file: ai/plans/PLAN_CANDIDATE_SEARCH.md
```

```text
Run Required Validation
plan_file: ai/plans/PLAN_CANDIDATE_SEARCH.md
change: candidate search filtering API
```

To inspect task titles locally:

```powershell
rg -n "^## Task Index|^### " ai/TASK_LIBRARY.md
```

To load one task section:

```powershell
rg -n "^### Create Plan$" ai/TASK_LIBRARY.md
```

If the title, placeholder, or target artifact is ambiguous, expect AI to ask a targeted clarification question before it proceeds.

## Lifecycle Guide

### Discovery

Use AI to turn rough ideas into concrete candidate work without jumping into implementation too early.
Useful requests ask AI to inspect `ROADMAP.md`, clarify requirement gaps, or recommend the next workstream while keeping product and contract decisions explicit.

### Planning

Use AI to create or revise an execution plan under `ai/plans/PLAN_*.md`.
The plan should be decision-complete enough that implementation does not need to invent product behavior, and `ROADMAP.md` should point to active planned work without duplicating the plan.

### Plan Verification

Before implementing large or multi-step work, ask AI to review the plan itself.
The useful output is a readiness judgment: lifecycle state, requirement gaps, milestone boundaries, execution shape, validation scope, and unresolved decisions.

### Implementation

Once the plan is ready, use AI to implement either the whole plan or one milestone.
The repository expects milestone-sized checkpoints: implementation, validation evidence, tracking artifacts, and a commit before the milestone is treated as done.
Ask AI to include a commit-message `Trigger:` body line that identifies whether the commit came from plan or spec work, whole-plan implementation, a named milestone, or an ad hoc task or task-library starter.

### Workflow Execution And Integration

Most work should stay in the default linear workflow.
When you want delegation, worktrees, or later integration of worker output, ask AI to use `ai/WORKFLOW.md`; it owns shared-file boundaries, worker logs, and integration mechanics.

### Verification

Use AI to run validation, inspect contract impact, and review the change with a code-review mindset.
`ai/TESTING.md` owns which command or manual check is sufficient, and `ai/REVIEWS.md` owns how findings should be prioritized.

### Release

Release preparation is a maintainer step after the intended implementation has landed on `main`.
Use `ai/RELEASES.md` for release preconditions, versioning, tagging, roadmap cleanup, changelog movement, and published-artifact verification.

## Repo-Local Skills

Repo-local skills live under `ai/skills/`.
Codex-native reusable workflows can be packaged as plugins; `.agents/plugins/marketplace.json` registers a repo-scoped plugin marketplace, and the plugin bundle can contain `skills/<skill-name>/SKILL.md`. Introduce `.agents/` only for a real Codex plugin marketplace, not for ordinary repository guidance.

Use them when you want a narrower workflow wrapper than the general task library.
Treat them as helpers that point back to owner guides, not as higher-priority policy.
Read a skill's `SKILL.md` only when that skill is invoked or clearly applies.

Current focused skills include:

- `repo-plan-author`: creating or revising `ai/plans/PLAN_*.md`
- `gh-fix-ci`: GitHub PR-check inspection and CI failure triage
- `gh-fix-security-quality`: GitHub Security tab, code-scanning, and Dependabot alert triage

## Developer Habits

- ask AI to name the spec artifacts before it edits code
- ask AI to say what is in scope and out of scope
- ask AI to list blockers explicitly instead of hiding them in assumptions
- prefer milestone-sized requests over long open-ended requests
- ask for validation and contract impact before approving the result
- keep release work separate from implementation work
- use the task titles in `ai/TASK_LIBRARY.md` as reusable commands when you want a consistent repository-local workflow

## When To Slow Down AI

Ask AI to stop and clarify instead of continuing when:

- the intended behavior is still ambiguous
- public API behavior is changing without clear contract updates
- the plan hides unresolved scope or validation questions
- multiple workers would need to edit the same shared files
- requested release work is not yet integrated onto `main`

In this repository, speed is useful only when the spec, ownership, and validation path are still clear.
