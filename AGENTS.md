# AI Project Instructions

`README.md` is the human-facing counterpart of this file. `SETUP.md` is the environment and onboarding guide. The `ai/` directory contains AI-facing support documents. Keep these sources aligned when their scopes overlap, but do not duplicate setup/runbook detail from `SETUP.md` here.

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

The `ai/` documents are guidance and planning aids. They are not higher-priority truth than executable specs, published contract docs, or the human-facing artifact that owns the topic.

## Agent Onboarding Quick Start

If you are a new agent entering this repository, follow these steps to ground yourself:

1. **Read `AGENTS.md` first** to understand the engineering rules and spec-driven development philosophy.
2. **Use `README.md` only when needed** for the human-facing project overview or supported contract summary.
3. **Open `SETUP.md` only when needed** for setup, local tooling, Docker, or troubleshooting detail.
4. **Identify the current task's lifecycle phase** (Discovery, Planning, Implementation, Testing, Review, Integration, Release).
5. **Load the relevant owner guide** from the lifecycle owner map below.
6. **Locate or create a task plan** (`ai/PLAN_*.md`) when the current workflow requires planned execution.
7. **Run a targeted relevance scan** using task terms against `ai/LEARNINGS.md` and active `ai/PLAN_*.md` files, then open only matches that overlap the current task.

## Lifecycle Owner Map

Start with `AGENTS.md`, then add only the owner guides that match the current lifecycle phase and changed artifacts.
Prompt titles, skills, templates, and deep references stay on demand until directly invoked or required by the selected workflow.

| Lifecycle phase | Primary owner guides |
| --- | --- |
| Discovery or roadmap intake | `ROADMAP.md` and `ai/PLAN.md`; add `README.md`, `ai/DESIGN.md`, or `ai/ARCHITECTURE.md` only when product, contract, or structure framing matters |
| Planning | `ai/PLAN.md`, the relevant specs or source artifacts, and `ROADMAP.md` for active-work tracking |
| Implementation | `ai/EXECUTION.md`, the target `ai/PLAN_*.md` when planned, and the owner guides for files being changed |
| Workflow, delegation, or integration | `ai/WORKFLOW.md` and `ai/EXECUTION.md`; load fanout references only after a fanout mode is selected |
| Testing or review | `ai/TESTING.md`, `ai/REVIEWS.md`, and `ai/DOCUMENTATION.md` when artifact routing or contract impact is part of the check |
| Release | `ai/RELEASES.md` only after the implementation state is integrated and release work is explicitly in scope |
| Prompt, skill, or template maintenance | `ai/PROMPTS.md`, the relevant `ai/skills/` guide, or the specific template or prompt metadata being changed |

## AI Document Set

The `ai/` directory is the AI-facing working set for non-contract repository knowledge.

Use these files deliberately:

- `ai/ARCHITECTURE.md`: compact descriptive repository snapshot, codebase map, package responsibilities, and structural guidance
- `ai/CODE_STYLE.md`: AI-facing code-style and change-shaping guidance for repo edits
- `ai/DESIGN.md`: intended design direction, product tradeoffs, and open design decisions
- `ai/DOCUMENTATION.md`: AI-facing documentation ownership and update guidance
- `ai/ENVIRONMENT_QUICK_REF.md`: AI-facing command wrapper reference for local Gradle execution
- `ai/EXECUTION.md`: AI-facing implementation workflow for executing plan files, updating validation results, and handling unreleased work
- `ai/LEARNINGS.md`: durable repo-wide engineering lessons that should survive refactors
- `ai/PLAN.md`: compact instructions for producing execution plans
- `ai/PLAN_*.md`: task-specific execution plans and milestone breakdowns
- `ai/PROMPTS.md`: lean reusable prompt-title index whose listed prompt names act as reusable commands
- `ai/REVIEWS.md`: AI-facing code-review and security-review guidance
- `ai/RELEASES.md`: AI-facing release workflow for intentional post-implementation releases
- `ai/TESTING.md`: AI-facing testing and validation guidance
- `ai/WORKFLOW.md`: compact AI-facing workflow router for `Linear Plan`, `Single-Plan Fanout`, `Multi-Plan Fanout`, delegation, common worktree rules, integration, and release handoff
- `ai/prompts/`: machine-readable prompt index and on-demand raw prompt bodies used only after a prompt title is invoked
- `ai/references/WORKFLOW_SINGLE_PLAN_FANOUT.md`: on-demand detailed mechanics for `Single-Plan Fanout`
- `ai/references/WORKFLOW_MULTI_PLAN_FANOUT.md`: on-demand detailed mechanics for `Multi-Plan Fanout`
- `ai/references/`: other on-demand detailed references that should not be part of the default read set
- `ai/templates/`: on-demand templates for creating new AI artifacts
- `ai/skills/`: on-demand repo-local workflow skills; read a skill's `SKILL.md` only when that skill is invoked or clearly applies
- `ai/archive/`: archived AI execution plans that have already been released or otherwise completed; read only for historical investigation

## AI Instruction Load Policy

Load AI guidance on demand:

- read `AGENTS.md` first
- read only the owning AI guide for the current task
- read active `ai/PLAN_*.md` files only when planning, executing, verifying, or releasing that plan
- use task-specific search terms for the onboarding relevance scan; do not read every active plan, archived plan, prompt body, reference, template, or skill as a pre-flight default
- read prompt bodies, templates, detailed references, skill files, and archived plans only when the task specifically needs them
- do not bulk-load `ai/archive/`, `ai/references/`, `ai/prompts/`, `ai/templates/`, or skill reference material as standing context

### Context Hygiene

Maintain a clean working context to optimize performance and prevent hallucinations:

- **Drop stale context**: Once a milestone or sub-task is complete, stop referencing files that are no longer relevant to the next step.
- **Avoid deep-file bulk loading**: Prefer targeted searches and structure checks over opening every file in a package.
- **Summarize complex state**: If a long investigation concludes, summarize the findings and use that summary as the new grounding instead of re-reading the raw logs.
- **Checkpoint degraded context quality**: If contradiction, unstable assumptions, unjustified hedging, or repeated re-derivation appears, write a short current-state summary before continuing. Put it in the active plan, worker log, or validation notes when such an artifact exists; otherwise use a concise user update or final note.
- **Close completed plans**: When a task is done, the plan moves to `ai/archive/` and should be removed from the active read set.

### AI Document Maintenance

Detailed AI-document maintenance rules live in `ai/DOCUMENTATION.md`. Load that guide before changing `AGENTS.md`, top-level `ai/*.md`, prompts, templates, skills, references, or archived plans.

## Required Updates By Change Type

Detailed artifact routing lives in `ai/DOCUMENTATION.md`.

High-level rules:

- public behavior changes must update the governing specs, implementation, and published contract artifacts together
- internal refactors should preserve existing specs without contract churn
- setup and environment changes route to `SETUP.md` and, when AI command guidance changes, `ai/ENVIRONMENT_QUICK_REF.md`
- roadmap changes route to `ROADMAP.md`; released history belongs in `CHANGELOG.md`
- durable AI guidance changes route to the owning `ai/` guide; update `AGENTS.md` only when repo-level AI rules or document ownership changes

## Architecture Constraints

`ai/ARCHITECTURE.md` owns the compact descriptive repository snapshot, codebase map, current API shape, and structural guidance for this repository.
Use `ai/references/ARCHITECTURE_DETAILED_MAP.md` only when the compact map is not enough.

When making architecture-sensitive changes:

- follow `ai/ARCHITECTURE.md`
- preserve the demo nature of the project and prefer direct code over abstraction
- keep `AGENTS.md` aligned only when repo-level architectural rules or AI-document ownership changed

## Local Environment And Command Execution

Use `SETUP.md` for setup walkthroughs and troubleshooting.
Use `ai/ENVIRONMENT_QUICK_REF.md` for the AI-facing Gradle wrapper reference.

Prefer `./build.ps1` through PowerShell for local and CI Gradle commands.
Use `ai/ENVIRONMENT_QUICK_REF.md` for wrapper behavior and avoid adding setup boilerplate to plans or prompts unless wrapper troubleshooting is in scope.

## Branch And Worktree Expectations

`ai/WORKFLOW.md` owns mode selection and common branch, worktree, coordinator, worker, and integration rules; the on-demand workflow references own detailed fanout mechanics.

Repo-level invariants:

- treat `main` as the integration branch for completed work
- keep worktree or side-branch implementation isolated until the planned scope is complete and locally validated
- prefer merging accepted branches or pull requests; use cherry-pick only when the user asks for it, when accepting less than the full branch or pull request, or when a normal merge is not viable, and record the reason
- do not cut releases from unintegrated side branches, worktrees, detached tips, or changes that have not landed on `main`

## Delegated Agents And Skill Wrappers

`ai/WORKFLOW.md` owns delegation mechanics, worker capability expectations, and integration rules; load the matching workflow reference only when a fanout mode is selected.

Specialized agents and repo-local skills may accelerate repeatable tasks when available and when the task clearly matches their scope.
Treat skills as workflow helpers that point back to the owner guides, not as higher-priority policy.
Read `ai/skills/<skill>/SKILL.md` only when that skill is invoked or clearly applies.

## Verification Rules

Verification rules and validation commands are owned by `ai/TESTING.md`.

## Versioning And Releases

Release versioning, tagging, and post-release cleanup rules are owned by `ai/RELEASES.md`.

## Definition Of Done

A change is complete when:

- the intended behavior exists in an appropriate spec artifact
- implementation and specs agree
- public contract artifacts are updated when behavior changed
- if the work was done in a git worktree or non-`main` branch, the finished branch has been pushed and a pull request is open or already merged onto `main`
- the required validation from `ai/TESTING.md` passes, normally `./build.ps1 build`
