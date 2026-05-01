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

- `ai/ARCHITECTURE.md`: descriptive codebase map, package responsibilities, and structural guidance
- `ai/DESIGN.md`: intended design direction, product tradeoffs, and open design decisions
- `ai/LEARNINGS.md`: durable repo-wide engineering lessons that should survive refactors
- `ai/PLAN.md`: instructions for producing execution plans
- `ai/PLAN_*.md`: task-specific execution plans and milestone breakdowns
- `ai/RELEASES.md`: AI-facing release workflow for intentional post-implementation releases
- `ai/WORKFLOW.md`: AI-facing multi-agent workflow for delegation, worktree usage, integration, and release handoff

Rules for maintaining the `ai/` documents:

- keep the role of each file distinct; do not collapse architecture, design, planning, release workflow, workflow guidance, and learnings into one document
- keep AI instruction markdown files under `ai/` by default; `AGENTS.md` is the only standing exception
- update the relevant `ai/` file in the same change when architecture, design intent, durable engineering guidance, release workflow, workflow guidance, or an execution plan materially changes
- treat `ai/ARCHITECTURE.md`, `ai/DESIGN.md`, and `ai/LEARNINGS.md` as descriptive guidance, not executable spec authority
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
- if you implement changes in a git worktree or another branch, integrate those changes back onto `main` before considering the task complete
- do not cut a release from a worktree-only branch tip or from changes that have not yet landed on `main`

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
- remove completed items instead of archiving them elsewhere
- do not recreate a second human history file; released history belongs in `CHANGELOG.md`

## Project Snapshot

Current implemented scope:

- `GET /docs`
- `GET /`
- `GET /hello`
- `Book` API under `/api/books`
- `Category` API under `/api/categories`
- `Localization` API under `/api/localizations`
- authenticated account API under `/api/account`
- OAuth 2.0 protected writes with JDBC-backed HTTP sessions
- generated REST Docs plus approved OpenAPI compatibility gate
- PostgreSQL runtime profiles and Testcontainers-backed integration tests
- append-only audit logging, tracing, caches, metrics, and tracked Gatling baselines

Primary goal: keep the codebase small, readable, and easy to reason about.

## Architecture Constraints

- Preserve the demo nature of the project. Prefer direct code over abstraction.
- Keep package names under `team.jit.technicalinterviewdemo`.
- Use Lombok when it clearly reduces routine boilerplate.
- Keep code compatible with Error Prone and the curated PMD ruleset.
- Keep non-trivial business logic in `@Service` beans.
- Prefer Spring MVC controllers and Spring Data repositories for new demo endpoints.
- Use PostgreSQL for runtime behavior unless explicitly told otherwise.
- Avoid extra infrastructure, distributed-systems concerns, or heavy libraries unless requested.
- Keep REST responses JSON-friendly.
- Do not remove the existing `hello` or `book` endpoints unless explicitly requested.
- When returning `ResponseEntity`, assign the payload to a local variable first.
- Log successful operations that change database state.

## API Contract Summary

Supported endpoint surface:

- `GET /docs`
- `GET /`
- `GET /hello`
- `GET|POST|PUT|DELETE /api/books...`
- `GET|POST /api/categories`
- `GET|POST|PUT|DELETE /api/localizations...`
- `GET|PUT /api/account...`
- actuator health/info/prometheus endpoints
- OpenAPI docs at `/v3/api-docs` and `/v3/api-docs.yaml`

Contract specifics:

- `GET /api/books` is paginated and filterable
- `GET /api/localizations` is paginated and supports optional exact `messageKey` and `language` filters
- account endpoints require an authenticated session
- category creation and localization writes require `ADMIN`
- localized errors include `messageKey`, localized `message`, and resolved `language`

## Verification Rules

Before finishing, run:

```powershell
.\gradlew.bat build
```

Use `SETUP.md` for environment prerequisites such as Java, Docker, and formatter configuration.

Additional verification expectations:

- refresh the OpenAPI baseline only after an intentional contract review with `./gradlew refreshOpenApiBaseline`
- rerun `./scripts/run-phase-9-benchmarks.ps1` when changing book list/search behavior, localization lookup behavior, or OAuth/session startup behavior
- treat failing compatibility or benchmark checks as spec failures, not optional cleanup

## Versioning And Releases

- use semantic version tags in the form `vMAJOR.MINOR.PATCH`
- keep version numbers increasing in `git log --first-parent` order
- create releases only from `main` after all intended changes are integrated there
- create annotated tags for intentional releases
- keep `CHANGELOG.md` aligned with releases
- do not introduce another human-facing completion archive file

## Definition Of Done

A change is complete when:

- the intended behavior exists in an appropriate spec artifact
- implementation and specs agree
- public contract artifacts are updated when behavior changed
- if the work was done in a git worktree or non-`main` branch, the final changes are integrated onto `main`
- `./gradlew build` passes
