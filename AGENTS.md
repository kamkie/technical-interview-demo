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

## Agent Onboarding Quick Start

If you are a new agent entering this repository, follow these steps to ground yourself:

1. **Read `AGENTS.md` first** to understand the engineering rules and spec-driven development philosophy.
2. **Use `README.md` only when needed** for the human-facing project overview or supported contract summary.
3. **Open `SETUP.md` only when needed** for setup, local tooling, Docker, or troubleshooting detail.
4. **Identify the current task's lifecycle phase** (Discovery, Planning, Implementation, Testing, Review, Integration, Release).
5. **Load the relevant owner guide** from the `ai/` directory (e.g., `ai/PLAN.md` for planning).
6. **Locate or create a task plan** (`ai/PLAN_*.md`) when the current workflow requires planned execution.
7. **Run a targeted relevance scan** using task terms against `ai/LEARNINGS.md` and active `ai/PLAN_*.md` files, then open only matches that overlap the current task.

## AI Document Set

The `ai/` directory is the AI-facing working set for non-contract repository knowledge.

Use these files deliberately:

- `ai/ARCHITECTURE.md`: compact descriptive repository snapshot, codebase map, package responsibilities, and structural guidance
- `ai/BUSINESS_MODULES.md`: descriptive business-feature package map and ownership guide
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

Rules for maintaining the `ai/` documents:

- keep the role of each file distinct; do not collapse architecture, code style, design, documentation ownership, execution, planning, release workflow, review guidance, testing guidance, workflow guidance, and learnings into one document
- keep AI instruction markdown files under `ai/` by default; `AGENTS.md` is the only standing exception
- update the relevant `ai/` file in the same change when architecture, code-style expectations, design intent, documentation ownership, durable engineering guidance, release workflow, review/security review guidance, testing/validation guidance, workflow guidance, or an execution plan materially changes
- keep `ai/PROMPTS.md` lean; put reusable prompt metadata in `ai/prompts/index.json`, raw prompt bodies under `ai/prompts/bodies/`, and standing workflow rules in the best owning AI document
- treat the listed prompt names in `ai/PROMPTS.md` as reusable commands for matching full starters loaded from `ai/prompts/`, following `ai/PROMPTS.md` for exact-match, placeholder, loader, and ambiguity rules
- keep detailed examples, templates, historical explanations, and deep references in `ai/templates/` or `ai/references/` instead of the standing top-level AI files
- write AI-guidance changes as current-state rules; route any still-useful historical context using `ai/DOCUMENTATION.md`
- keep repo-local skills narrow and workflow-oriented; use them to accelerate repeated entry tasks or focused triage, not to replace the owner guides
- keep standing code-style, testing, review, and documentation guidance in their focused owning files instead of redistributing it across prompts or workflow docs
- when a repo-local skill wraps a workflow owned by another guide, update the skill and the owning guide together if that workflow changes
- when AI instruction files accumulate overlap, compact them by moving duplicated guidance into the single best owning file and updating cross-references in the same change
- archive executed `ai/PLAN_*.md` files under `ai/archive/` as part of the release cleanup once that work has been released
- treat `ai/ARCHITECTURE.md`, `ai/BUSINESS_MODULES.md`, `ai/DESIGN.md`, and `ai/LEARNINGS.md` as descriptive guidance, not executable spec authority
- if an interrupted tool or IDE run leaves an `ai/` document incomplete, finish it or clearly mark the gaps instead of leaving misleading partial content
- when moving or renaming AI documents, update references in `AGENTS.md` and other `ai/` files in the same change

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
- `src/test/resources/http/`: reviewer-facing runnable request examples
- `README.md`: supported human-facing contract summary
- `ROADMAP.md`: active release phase, roadmap sequencing, and current project state
- `CHANGELOG.md`: release history only
- `SETUP.md`: local environment, tooling, onboarding, and troubleshooting

The `ai/` documents are guidance and planning aids. They are not higher-priority truth than executable specs, published contract docs, or the human-facing artifact that owns the topic.

## Required Updates By Change Type

### Architecture, design, or AI guidance change

- update the relevant file under `ai/`
- keep `AGENTS.md` aligned when the role or maintenance rules for `ai/` documents change
- do not update `README.md` unless the human-facing contract or project description changed

### Public API change

Update all affected artifacts in the same change:

- controller or service implementation
- integration tests
- REST Docs tests and Asciidoc pages when public behavior is documented there
- approved OpenAPI baseline if the contract intentionally changed
- HTTP example files under `src/test/resources/http/`
- `README.md` if the supported contract changed
- `CHANGELOG.md` only when the change is being released

### Internal refactor with no contract change

- keep existing specs green without unnecessary contract edits
- avoid changing OpenAPI, README, or HTTP examples unless behavior actually changed
- prefer renames and moves that reduce exceptions in naming and packaging

### Setup or environment change

- update `SETUP.md` for human setup, tool, and troubleshooting changes
- update `ai/ENVIRONMENT_QUICK_REF.md` when AI-facing command-wrapper guidance changes
- only touch `README.md` or `AGENTS.md` when the high-level contract or rules changed, not for walkthrough duplication

### Roadmap change

- update `ROADMAP.md`
- keep `ROADMAP.md` `## Current Project State` aligned with the active release phase, breaking-change policy, and next target version whenever roadmap sequencing or release targeting changes
- treat `[x]` items in `ROADMAP.md` as selected for active planning or development, not as completed history
- remove completed items instead of archiving them elsewhere
- do not recreate a second human history file; released history belongs in `CHANGELOG.md`

## Architecture Constraints

`ai/ARCHITECTURE.md` owns the compact descriptive repository snapshot, codebase map, current API shape, and structural guidance for this repository.
Use `ai/references/ARCHITECTURE_DETAILED_MAP.md` only when the compact map is not enough.

When making architecture-sensitive changes:

- follow `ai/ARCHITECTURE.md` and `ai/BUSINESS_MODULES.md`
- preserve the demo nature of the project and prefer direct code over abstraction
- keep `AGENTS.md` aligned only when repo-level architectural rules or AI-document ownership changed

## Branch And Worktree Expectations

`ai/WORKFLOW.md` owns mode selection and common branch, worktree, coordinator, worker, and integration rules; the on-demand workflow references own detailed fanout mechanics.

Repo-level rules:

- treat `main` as the integration branch for completed work
- keep worktree or side-branch implementation isolated until the planned scope is complete and locally validated
- consider worktree or side-branch execution complete only when the finished branch has been pushed and a pull request is open or already merged onto `main`
- prefer merging accepted branches or pull requests; use cherry-pick only when the user asks for it, when accepting less than the full branch or pull request, or when a normal merge is not viable, and record the reason
- do not cut a release from a worktree-only branch tip or from changes that have not landed on `main`

## Local Environment And Command Execution

Use `SETUP.md` for setup walkthroughs and troubleshooting.
Use `ai/ENVIRONMENT_QUICK_REF.md` for the AI-facing Gradle wrapper reference.

Prefer `./build.ps1` through PowerShell for local and CI Gradle commands.
It auto-loads a root `.env` file when present, so plans and prompts should not add upfront `JAVA_HOME` discovery or dotenv boilerplate.

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
- the required validation from `ai/TESTING.md` passes, normally `./build.ps1 build`; the wrapper handles the lightweight-only uncommitted-change shortcut, and `./build.ps1 -FullBuild build` forces the full Gradle build when required
