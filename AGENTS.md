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

## AI Document Set

The `ai/` directory is the AI-facing working set for non-contract repository knowledge.

Use these files deliberately:

- `ai/ARCHITECTURE.md`: descriptive repository snapshot, codebase map, package responsibilities, and structural guidance
- `ai/BUSINESS_MODULES.md`: descriptive business-feature package map and ownership guide
- `ai/CODE_STYLE.md`: AI-facing code-style and change-shaping guidance for repo edits
- `ai/DESIGN.md`: intended design direction, product tradeoffs, and open design decisions
- `ai/DOCUMENTATION.md`: AI-facing documentation ownership and update guidance
- `ai/EXECUTION.md`: AI-facing implementation workflow for executing plan files, updating validation results, and handling unreleased work
- `ai/LEARNINGS.md`: durable repo-wide engineering lessons that should survive refactors
- `ai/PLAN.md`: instructions for producing execution plans
- `ai/PLAN_*.md`: task-specific execution plans and milestone breakdowns
- `ai/PROMPTS.md`: lean reusable prompt starters whose `###` titles act as reusable commands and point to the standing guidance in the other AI documents
- `ai/skills/`: repo-local reusable skills for repetitive planning, validation, CI-triage, and GitHub security-triage entry workflows that still defer to the owner guides:
  - `repo-plan-author`: focused entry point for creating or revising `ai/PLAN_*.md`
  - `repo-validation-gate`: focused entry point for changed-file classification, validation selection, and contract-impact triage
  - `gh-fix-ci`: focused entry point for GitHub PR-check inspection, GitHub Actions log triage, and approval-first CI fix planning
  - `gh-fix-security-quality`: focused entry point for GitHub Security tab inspection, code-scanning and Dependabot alert triage, and approval-first security fix planning
  - `security-best-practices`: curated backend and frontend hardening references for AI-assisted security implementation work
- `ai/REVIEWS.md`: AI-facing code-review and security-review guidance
- `ai/archive/`: archived AI execution plans that have already been released or otherwise completed
- `ai/RELEASES.md`: AI-facing release workflow for intentional post-implementation releases
- `ai/TESTING.md`: AI-facing testing and validation guidance
- `ai/WORKFLOW.md`: AI-facing execution workflow for single-branch, delegation, worktree usage, integration, and release handoff

Rules for maintaining the `ai/` documents:

- keep the role of each file distinct; do not collapse architecture, code style, design, documentation ownership, execution, planning, release workflow, review guidance, testing guidance, workflow guidance, and learnings into one document
- keep AI instruction markdown files under `ai/` by default; `AGENTS.md` is the only standing exception
- update the relevant `ai/` file in the same change when architecture, code-style expectations, design intent, documentation ownership, durable engineering guidance, release workflow, review/security review guidance, testing/validation guidance, workflow guidance, or an execution plan materially changes
- keep `ai/PROMPTS.md` lean; put standing workflow rules in the best owning AI document instead of restating them inside prompts
- treat the `###` prompt titles in `ai/PROMPTS.md` as reusable commands for the full starters under those headings, following `ai/PROMPTS.md` for exact-match, placeholder, and ambiguity rules
- keep repo-local skills narrow and workflow-oriented; use them to accelerate repeated entry tasks, not to replace the owner guides
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

## Branch And Worktree Expectations

- treat `main` as the integration branch for completed work
- if you implement a multi-step plan in a git worktree or another branch, keep the work there until the whole plan is finished, then push that branch and open a pull request instead of trying to integrate the changes directly onto `main` from the worktree
- consider worktree-based execution complete only when the finished branch has been pushed and the pull request is open or already merged onto `main`
- prefer merging accepted branches or pull requests when integrating their changes; use cherry-pick only when the user asks for it, when integrating less than the whole branch or pull request, or when a normal merge is not viable, and record the reason
- do not cut a release from a worktree-only branch tip or from changes that have not yet landed on `main`

## Local Environment Discovery

- when discovering `JAVA_HOME` or preparing to run Java or Gradle commands from PowerShell, dot-source `.\scripts\load-dotenv.ps1 -Quiet` first so `.env` can provide the repository-local toolchain path before falling back to machine-wide environment inspection

## Spec Priority

When resolving truth, use this order:

1. explicit user request in the current task
2. executable specs: integration tests, REST Docs tests, OpenAPI compatibility tests, benchmark checks
3. published contract docs: `README.md`, `src/docs/asciidoc/`, HTTP example collections
4. active planning in `ROADMAP.md`
5. historical release notes in `CHANGELOG.md`

## Authoritative Spec Artifacts

Use these artifacts deliberately:

- `src/test/java/`: executable behavior specs
- `src/docs/asciidoc/`: published REST Docs structure
- `src/test/resources/openapi/approved-openapi.json`: approved machine-readable public API contract
- `src/test/resources/http/`: reviewer-facing runnable request examples
- `README.md`: supported human-facing contract summary
- `ROADMAP.md`: active roadmap only, not historical archive
- `CHANGELOG.md`: release history only
- `SETUP.md`: local environment and troubleshooting only

The `ai/` documents are guidance and planning aids. They are not higher-priority truth than executable specs or published contract docs.

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

- update `SETUP.md`
- only touch `README.md` or `AGENTS.md` when the high-level contract or rules changed, not for walkthrough duplication

### Roadmap change

- update `ROADMAP.md`
- keep `ROADMAP.md` `## Current Project State` aligned with the active release phase, breaking-change policy, and next target version whenever roadmap sequencing or release targeting changes
- treat `[x]` items in `ROADMAP.md` as selected for active planning or development, not as completed history
- remove completed items instead of archiving them elsewhere
- do not recreate a second human history file; released history belongs in `CHANGELOG.md`

## Architecture Constraints

`ai/ARCHITECTURE.md` owns the descriptive repository snapshot, codebase map, business-module map, current API shape, and structural guidance for this repository.

When making architecture-sensitive changes:

- follow `ai/ARCHITECTURE.md` and `ai/BUSINESS_MODULES.md`
- preserve the demo nature of the project and prefer direct code over abstraction
- keep `AGENTS.md` aligned only when repo-level architectural rules or AI-document ownership changed

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
- `./gradlew build` passes, unless `pwsh ./scripts/classify-changed-files.ps1 -Uncommitted` reports `skipHeavyValidation=true` for the current uncommitted changes and manual consistency review is sufficient for that lightweight-only change
