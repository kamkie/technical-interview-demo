# AI Project Instructions

`README.md` is the human-facing counterpart of this file. `SETUP.md` is the environment and onboarding guide. The `docs/` directory contains shared project knowledge, and `.agents/` contains Codex-specific support documents. Keep these sources aligned when their scopes overlap, but do not duplicate setup/runbook detail from `SETUP.md` here.

## Role Of This File

`AGENTS.md` defines how AI agents should work in this repository.

Use this file for:

- project-specific engineering rules
- architectural constraints
- authoritative spec locations
- required spec updates for different kinds of changes
- quality gates and release rules

Do not use this file for detailed local setup, IDE walkthroughs, Docker onboarding, or troubleshooting steps that already belong in `SETUP.md`.

## Core Approach: Spec-Driven Development

This repository should be changed using Spec-Driven Development.

Principle:

1. Identify the behavior being changed.
2. Identify the spec artifact that defines that behavior.
3. Update or add the spec first.
4. Implement the smallest code change that satisfies the updated spec.
5. Verify the executable and published specs remain aligned.

If the intended behavior is not clear enough to express as a spec, stop and clarify it before implementing.

## Spec Priority

When resolving truth, use this order:

1. explicit user request in the current task
2. executable specs: integration tests, REST Docs tests, OpenAPI compatibility tests, benchmark checks
3. published contract docs: `README.md`, `src/docs/asciidoc/`, HTTP example collections
4. `ROADMAP.md` `## Current Project State` for the active release phase, breaking-change policy, and next target version
5. active planning in `ROADMAP.md` ordered plan sections
6. historical release notes in `CHANGELOG.md`

## Authoritative Repository Artifacts

Use these artifacts deliberately:

- `src/test/java/`: executable behavior specs
- `src/docs/asciidoc/`: published REST Docs structure
- `src/test/resources/openapi/approved-openapi.json`: approved machine-readable public API contract
- `README.md`: supported human-facing contract summary
- `ROADMAP.md`: active release phase, roadmap sequencing, and current project state
- `CHANGELOG.md`: release history only
- `SETUP.md`: local environment, tooling, onboarding, and troubleshooting
- `.agents/references/repository-knowledge-spec.md`: repository knowledge layout and file ownership

The `docs/` and `.agents/` guidance files are planning aids. They are not higher-priority truth than executable specs, published contract docs, or the human-facing artifact that owns the topic.

## Agent Onboarding Quick Start

If you are a new agent entering this repository, follow these steps to ground yourself:

1. **Read `AGENTS.md` first** to understand the engineering rules and spec-driven development philosophy.
2. **Use `README.md` only when needed** for the human-facing project overview or supported contract summary.
3. **Open `SETUP.md` only when needed** for setup, local tooling, Docker, or troubleshooting detail.
4. **Identify the current task's lifecycle phase** (Discovery, Planning, Implementation, Testing, Review, Integration, Release).
5. **Load the relevant owner guide** from the phase owner map below.
6. **Locate or create a task plan** (`.agents/plans/PLAN_*.md`) when the current workflow requires planned execution.
7. **Run a targeted relevance scan** using task terms against `.agents/references/LEARNINGS.md` and active `.agents/plans/PLAN_*.md` files, then open only matches that overlap the current task.

## Phase Owner Map

Start with `AGENTS.md`, then add only the owner guides that match the current lifecycle phase and changed artifacts.
Task titles, skills, templates, and deep references stay on demand until directly invoked or required by the selected workflow.

| Phase | Primary owner guides |
| --- | --- |
| Discovery or roadmap intake | `ROADMAP.md` and `.agents/references/planning.md`; add `README.md` or conditional descriptive guides only when the request needs product, contract, design, or structure framing |
| Planning | `.agents/references/planning.md`, the relevant specs or source artifacts, and `ROADMAP.md` for active-work tracking |
| Whole-plan implementation | `.agents/references/plan-execution.md`, the target `.agents/plans/PLAN_*.md`, and the owner guides for files being changed; keep descriptive guides conditional |
| Ad hoc task or one-milestone implementation | `.agents/references/execution.md`, the user request or target milestone, and the owner guides for files being changed; keep descriptive guides conditional |
| Workflow, delegation, or integration | `.agents/references/workflow.md`; load detailed workflow references only after the work shape requires delegation, worktrees, or multi-plan coordination |
| Testing or review | `.agents/references/testing.md`, `.agents/references/reviews.md`, and `.agents/references/documentation.md` when artifact routing or contract impact is part of the check; keep descriptive guides conditional |
| Release | `.agents/references/releases.md` only after the implementation state is integrated and release work is explicitly in scope |
| Task-skill, skill, or template maintenance | `.agents/skills/repo-task/references/spec.md`, the relevant `.agents/skills/` guide, or the specific template or task file being changed |

Conditional descriptive guide triggers:

- load `docs/ARCHITECTURE.md` only for structural code reading, architecture-sensitive changes, or package ownership questions
- load `docs/DESIGN.md` only when user-visible behavior, supported scope, security posture, or roadmap tradeoffs are touched
- load `.agents/references/LEARNINGS.md` only from the targeted relevance scan or a known recurring repo lesson

## AI Document Set

The `.agents/` directory is the Codex-specific working set for non-contract repository knowledge.

Use these files deliberately:

- `docs/ARCHITECTURE.md`: compact descriptive repository snapshot, codebase map, package responsibilities, and structural guidance
- `.agents/references/code-style.md`: AI-facing code-style and change-shaping guidance for repo edits
- `docs/DESIGN.md`: intended design direction, product tradeoffs, and open design decisions
- `.agents/references/documentation.md`: AI-facing documentation ownership and update guidance
- `.agents/references/environment-quick-ref.md`: AI-facing command wrapper reference for local Gradle execution
- `.agents/references/plan-execution.md`: AI-facing workflow for executing a whole active plan across milestones
- `.agents/references/execution.md`: AI-facing workflow for ad hoc tasks and individual plan milestones
- `.agents/references/LEARNINGS.md`: durable repo-wide engineering lessons that should survive refactors
- `.agents/references/planning.md`: compact instructions for producing execution plans
- `.agents/plans/PLAN_*.md`: task-specific execution plans and milestone breakdowns
- `.agents/skills/repo-task/`: repository-local reusable task skill whose task reference files act as reusable commands; `references/spec.md` owns the dispatcher, index, and task schema
- `.agents/references/reviews.md`: AI-facing code-review and security-review guidance
- `.agents/references/releases.md`: AI-facing release workflow for intentional post-implementation releases
- `.agents/references/testing.md`: AI-facing testing and validation guidance
- `.agents/references/workflow.md`: compact AI-facing owner for branch, worktree, delegation, worker-log, integration, and remote-handoff mechanics
- `.agents/references/workflow-delegated-plan.md`: on-demand detailed mechanics for splitting one active plan into worker-owned slices
- `.agents/references/workflow-coordinated-plans.md`: on-demand detailed mechanics for coordinating multiple active plans
- `.agents/references/`: other on-demand detailed references that should not be part of the default read set
- `.agents/templates/`: on-demand templates for creating new AI artifacts
- `.agents/skills/`: on-demand repo-local workflow skills; read a skill's `SKILL.md` only when that skill is invoked or clearly applies
- `.agents/references/repository-knowledge-spec.md`: repository knowledge layout and file ownership spec; read before adding or moving repository knowledge files
- `.agents/plugins/marketplace.json`: Codex repo-scoped plugin marketplace configuration, only if this repository later promotes a reusable workflow to an installable Codex plugin
- `.agents/reports/`: generated AI analysis reports; read only when a task explicitly asks for report history or evaluation evidence
- `.agents/archive/`: archived AI execution plans and historical report-like analysis artifacts; read only for historical investigation

## AI Instruction Load Policy

Load AI guidance on demand:

- read `AGENTS.md` first
- read only the owning AI guide for the current task
- read active `.agents/plans/PLAN_*.md` files only when planning, executing, verifying, or releasing that plan
- use task-specific search terms for the onboarding relevance scan; do not read every active plan, archived plan or report, task section, reference, template, or skill as a pre-flight default
- read task files under `.agents/skills/repo-task/references/tasks/`, templates, detailed references, skill files, and archived plans or reports only when the task specifically needs them
- do not bulk-load `.agents/archive/`, `.agents/reports/`, `.agents/references/`, `.agents/templates/`, or skill reference material as standing context

### Context Hygiene

Maintain a clean working context to optimize performance and prevent hallucinations:

- **Drop stale context**: Once a milestone or sub-task is complete, stop referencing files that are no longer relevant to the next step.
- **Avoid deep-file bulk loading**: Prefer targeted searches and structure checks over opening every file in a package.
- **Summarize complex state**: If a long investigation concludes, summarize the findings and use that summary as the new grounding instead of re-reading the raw logs.
- **Checkpoint degraded context quality**: If contradiction, unstable assumptions, unjustified hedging, or repeated re-derivation appears, write a short current-state summary before continuing. Put it in the active plan, worker log, or validation notes when such an artifact exists; otherwise use a concise user update or final note.
- **Close completed plans**: When a task is done, the plan moves to `.agents/archive/` and should be removed from the active read set.

### AI Document Maintenance

Detailed AI-document maintenance rules live in `.agents/references/documentation.md`. Load that guide before changing `AGENTS.md`, `.agents/references/*.md`, task-skill files, templates, skills, references, or archived plans or reports.

## Required Updates By Change Type

Detailed artifact routing lives in `.agents/references/documentation.md`.

High-level rules:

- public behavior changes must update the governing specs, implementation, and published contract artifacts together
- internal refactors should preserve existing specs without contract churn
- setup and environment changes route to `SETUP.md` and, when AI command guidance changes, `.agents/references/environment-quick-ref.md`
- roadmap changes route to `ROADMAP.md`; released history belongs in `CHANGELOG.md`
- durable AI guidance changes route to the owning `.agents/references/` guide; update `AGENTS.md` only when repo-level AI rules or document ownership changes

## Architecture Constraints

`docs/ARCHITECTURE.md` owns the compact descriptive repository snapshot, codebase map, current API shape, and structural guidance for this repository.
Use `.agents/references/architecture-detailed-map.md` only when the compact map is not enough.

When making architecture-sensitive changes:

- follow `docs/ARCHITECTURE.md`
- preserve the demo nature of the project and prefer direct code over abstraction
- keep `AGENTS.md` aligned only when repo-level architectural rules or AI-document ownership changed

## Local Environment And Command Execution

Use `SETUP.md` for setup walkthroughs and troubleshooting.
Use `.agents/references/environment-quick-ref.md` for the AI-facing Gradle wrapper reference.

Prefer `./build.ps1` through PowerShell for local and CI Gradle commands.
Use `.agents/references/environment-quick-ref.md` for wrapper behavior and avoid adding setup boilerplate to plans or task starters unless wrapper troubleshooting is in scope.

## Branch And Worktree Expectations

`.agents/references/workflow.md` owns common branch, worktree, coordinator, worker, and integration rules; the on-demand workflow references own detailed delegated-work mechanics.

Repo-level invariants:

- treat `main` as the integration branch for completed work
- keep worktree or side-branch implementation isolated until the planned scope is complete and locally validated
- prefer merging accepted branches or pull requests; use cherry-pick only when the user asks for it, when accepting less than the full branch or pull request, or when a normal merge is not viable, and record the reason
- do not cut releases from unintegrated side branches, worktrees, detached tips, or changes that have not landed on `main`
- when creating a commit, use the Conventional Commits style from `CONTRIBUTING.md`: `<type>[optional scope][!]: <description>`, and include the repository project metadata footers required there, including `Project-Source`, the applicable `Project-*` provenance footer, and `Validation`

## Delegated Agents And Skill Wrappers

`.agents/references/workflow.md` owns delegation mechanics, worker capability expectations, and integration rules; load the matching workflow reference only when the work shape requires it.

Specialized agents and repo-local skills may accelerate repeatable tasks when available and when the task clearly matches their scope.
Treat skills as workflow helpers that point back to the owner guides, not as higher-priority policy.
Read `.agents/skills/<skill>/SKILL.md` only when that skill is invoked or clearly applies.
Use `.agents/skills/repo-task/` for this repository's task starter dispatcher. Use `.agents/plugins/marketplace.json` only for Codex plugin marketplace configuration; Codex skills that need distribution belong inside a plugin bundle, for example `plugins/<plugin-name>/skills/<skill-name>/SKILL.md`.

## Verification Rules

Verification rules and validation commands are owned by `.agents/references/testing.md`.

## Versioning And Releases

Release versioning, tagging, and post-release cleanup rules are owned by `.agents/references/releases.md`.

## Definition Of Done

A change is complete when:

- the intended behavior exists in an appropriate spec artifact
- implementation and specs agree
- public contract artifacts are updated when behavior changed
- if the work was done in a git worktree or non-`main` branch, the finished branch has been pushed and a pull request is open or already merged onto `main`
- the required validation from `.agents/references/testing.md` passes, normally `./build.ps1 build`
