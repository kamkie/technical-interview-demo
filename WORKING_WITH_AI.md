# Working With AI

`WORKING_WITH_AI.md` is the human-facing guide for using AI in this repository across the application-development lifecycle.

Use this file when you want to direct AI effectively as a developer.
Use `AGENTS.md` and the files under `.agents/` as the repository-local instructions that the AI should follow.
Use `README.md` for the short project overview, `SETUP.md` for local environment setup, and `CONTRIBUTING.md` for contributor workflow expectations.

## Core Working Model

This repository uses spec-driven development.
That matters just as much when AI is helping as when a human is working alone.

The normal loop is:

1. Frame the change in terms of behavior, not code.
2. Identify the governing spec or contract artifact.
3. Decide the current lifecycle phase.
4. Give AI the right owner documents for that phase.
5. Keep the work task-sized.
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
| Backend contract source for separate frontend AI agents | `docs/FRONTEND_AI_CONTRACT.md` |
| Lifecycle phase and activity vocabulary | `.agents/references/application-lifecycle.md` |
| ADRs for durable decisions | `docs/decisions/` and `docs/decisions/ADR_TEMPLATE.md` |
| PRDs for broad product intent | `docs/requirements/` and `docs/requirements/PRD_TEMPLATE.md` |
| Standalone behavior specs | `docs/specs/` and `docs/specs/SPEC_TEMPLATE.md` |
| Reusable task prompts | `.agents/tasks/README.md` and `.agents/tasks/` |
| Creating or revising execution plans | `.agents/references/planning.md` |
| Executing a whole approved plan | `.agents/references/plan-execution.md` |
| Implementing an ad hoc task or one plan task | `.agents/references/execution.md` |
| Delegation, worktrees, workflow state, or integration mechanics | `.agents/references/workflow.md` |
| Multi-agent modes, roles, read sets, and workflow state | `.agents/references/workflow.md` |
| Validation scope and review activity | `.agents/references/testing.md` and `.agents/references/reviews.md` |
| Documentation and artifact routing | `.agents/references/documentation.md` |
| Intentional release preparation after integration | `.agents/references/releases.md` |

Detailed task files, templates, deep references, generated reports, skill references, and archived plans are on-demand material.
Load them only when the task title, owner guide, or active work calls for them.

Use `docs/FRONTEND_AI_CONTRACT.md` when asking AI to prepare or review instructions for a separate first-party frontend repository.
It is a generated import-ready document with frontend-facing backend constraints, external security and design skill URLs, and an approved OpenAPI snapshot, but backend executable specs, REST Docs, OpenAPI, and `README.md` remain higher-priority contract sources.

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
plan_file: .agents/plans/PLAN_CANDIDATE_SEARCH.md
```

```text
Run Required Validation
plan_file: .agents/plans/PLAN_CANDIDATE_SEARCH.md
change: candidate search filtering API
```

To inspect task titles locally:

```powershell
Get-Content .agents/tasks/README.md
```

To load one task file:

```powershell
Get-Content .agents/tasks/interactive-documentation-session.md
```

If the title, placeholder, or target artifact is ambiguous, expect AI to ask a targeted clarification question before it proceeds.

## Lifecycle Guide

Use `.agents/references/application-lifecycle.md` for the accepted phase names and activity names such as `Frame`, `Elicit`, `Define-Requirements`, `Validate-Plan`, `Run`, and `Replan?`.

Lifecycle phases map to `ROADMAP.md` sections as follows: rough capture lands in `## Conceptualization`, structured requirements work in `## Analysis`, durable decisions in `## Decisions`, accept/defer/prioritize calls in `## Triage`, and committed execution rows in `## Active Release Track` or `## Planned Work`. Skip rules under `ROADMAP.md` `### When To Skip Pre-Planning Artifacts` keep small or local changes from needing pre-planning rows.

### Conceptualization

Use AI to capture rough ideas, TODOs, maintenance signals, links, and early framing without jumping into implementation too early.
Useful requests ask AI to inspect the relevant owner docs, identify ambiguity, and say whether the work should be rejected, captured, analyzed, or triaged.

### Analysis

Use AI to elicit and validate requirements, product intent, behavior rules, constraints, non-goals, and acceptance criteria before execution planning.
Create an ADR when a durable architecture, workflow, contract-policy, security, documentation-ownership, or repository-process decision is needed.
Create a PRD only when broad or ambiguous user-facing scope needs product intent, users, goals, non-goals, requirements, and acceptance criteria in its own artifact.
Create a standalone spec only when behavior or contract truth is not already clear in executable specs, published contract docs, or the target plan.

### Triage

Use AI to move candidate work into active-work tracking, prioritization, sequencing, or deferral.
The useful output is a roadmap entry, a selected next artifact, or a clear reason the idea is not ready for active planning.

### Planning

Use AI to create or revise an execution plan under `.agents/plans/PLAN_*.md`.
The plan should link relevant ADRs, PRDs, and specs, be decision-complete enough that implementation does not need to invent product behavior, and keep `ROADMAP.md` pointed at active planned work without duplicating the plan.

### Planning Validation

Before implementing large or multi-step work, ask AI to review the plan itself.
The useful output is a readiness judgment: lifecycle state, requirement gaps, plan-task boundaries, execution shape, validation scope, and unresolved decisions.

### Implementation

Once the plan is ready, use AI to implement either the whole plan or one plan task.
The repository expects task-sized checkpoints: implementation, validation evidence, tracking artifacts, and a commit before the plan task is treated as done.
Ask AI to write commit messages in Conventional Commits style, such as `feat(scope): summary`, `fix(scope): summary`, or `docs(scope): summary`.
For AI-created commits, `.agents/references/execution.md` owns the required commit-message rules.
Ask AI to include the required project metadata and validation footers from that guide.

#### AI Commit Message Guidance

IntelliJ AI Assistant can be aligned with the repository's AI commit-message rules:

1. Open **Settings** (`Ctrl+Alt+S`).
2. Navigate to **Tools | AI Assistant | Prompt Library**.
3. Select **Commit Message Generation**.
4. Update the prompt to instruct it to:
   "Follow the commit-message rules in `.agents/references/execution.md`; use `.gitmessage` only as the local template shape."
5. Alternatively, when asking the AI to commit, you can explicitly say: "Write a commit message following `.agents/references/execution.md`."

### Verification And Review

Use AI to run validation, inspect contract impact, and review the change with a code-review mindset.
`.agents/references/testing.md` owns which command or manual check is sufficient, and `.agents/references/reviews.md` owns how findings should be prioritized.

### Integration And Workflow Coordination

Most work should stay in the default linear workflow.
When you want delegation, worktrees, or later integration of worker output, ask AI to use `.agents/references/workflow.md`; it owns shared-file boundaries, workflow state, and integration mechanics.
In Codex sessions, ask explicitly for sub-agents, delegation, or parallel agent work before expecting `M2` through `M4` execution.

The workflow modes are:

- `M0: direct`: one agent handles the work directly
- `M1: assisted`: read-only review, verification, or specialist help
- `M2: delegated`: one Worker owns one bounded write scope
- `M3: parallel`: multiple disjoint Worker scopes move in parallel
- `M4: gated`: independent review, verification, security, docs, release, or specialist gates are required

The role vocabulary is Coordinator, Planner, Worker, Reviewer, Verifier, and Specialist.
Use `.agents/context/*` only for durable handoffs, worker reports, reviews, verifications, or specialist outputs that must survive context switches.

### Release

Release preparation is a maintainer step after the intended implementation has landed on `main`.
Use `.agents/references/releases.md` for release preconditions, versioning, tagging, roadmap cleanup, changelog movement, and published-artifact verification.

### Deployment And Operations

This repository does not yet have general AI owner guides for Deployment or Operations.
Use explicit task-specific runbooks, deployment artifacts, and validation guidance, then convert durable gaps or recurring signals into Triage.

### Maintenance

Use AI to capture durable lessons, update active-work tracking after a release or recurring signal, and keep follow-up work separate from completed release history.

## Repo-Local Tasks And Skills

Reusable task prompts live under `.agents/tasks/`.
Use `.agents/tasks/README.md` to resolve task titles, then load only the matching task file.
Repo-local workflow skills live under `.agents/skills/`.
Codex-native reusable workflows can be packaged as plugins; `.agents/plugins/marketplace.json` registers a repo-scoped plugin marketplace, and the plugin bundle can contain `skills/<skill-name>/SKILL.md`.

Use skills when you want a narrower workflow wrapper than the owner guides.
Treat them as helpers that point back to owner guides, not as higher-priority policy.
Read a skill's `SKILL.md` only when that skill is invoked or clearly applies.

Current focused skills include:

- `select-mode-and-skills`: workflow mode, role, and skill-chain selection
- `handoff-pack`: complete delegated-work handoff packets
- `repo-task-execute`: bounded repo task or plan-task execution
- `run-validation`: validation selection, execution, and evidence recording
- `diff-review`: diff review and gate decisions
- `repo-plan-author`: execution plan creation or revision
- `integrate-branch`: accepted branch or worker-output integration
- `security-review`: security-sensitive change review
- `openapi-contract-check`: OpenAPI compatibility and baseline review
- `triage-flaky-test`: intermittent test failure triage
- `gh-fix-ci`: GitHub PR-check inspection and CI failure triage
- `gh-fix-security-quality`: GitHub Security tab, code-scanning, and Dependabot alert triage

## Developer Habits

- ask AI to name the spec artifacts before it edits code
- ask AI to say what is in scope and out of scope
- ask AI to list blockers explicitly instead of hiding them in assumptions
- prefer task-sized requests over long open-ended requests
- ask for validation and contract impact before approving the result
- keep release work separate from implementation work
- use `.agents/tasks/README.md` task titles when you want a consistent repository-local workflow

## When To Slow Down AI

Ask AI to stop and clarify instead of continuing when:

- the intended behavior is still ambiguous
- public API behavior is changing without clear contract updates
- the plan hides unresolved scope or validation questions
- multiple workers would need to edit the same shared files
- requested release work is not yet integrated onto `main`

In this repository, speed is useful only when the spec, ownership, and validation path are still clear.
