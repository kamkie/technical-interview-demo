# AI Project Instructions

`README.md` is the human-facing counterpart of this file. `SETUP.md` is the environment and onboarding guide. Keep all three aligned, but do not duplicate setup/runbook detail from `SETUP.md` here.

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
4. active planning in `TODO.md`
5. historical release notes in `CHANGELOG.md`

## Authoritative Spec Artifacts

Use these artifacts deliberately:

- `src/test/java/`: executable behavior specs
- `src/docs/asciidoc/`: published REST Docs structure
- `src/test/resources/openapi/approved-openapi.json`: approved machine-readable public API contract
- `src/test/resources/http/`: reviewer-facing runnable request examples
- `README.md`: supported human-facing contract summary
- `TODO.md`: active roadmap only, not historical archive
- `CHANGELOG.md`: release history only
- `SETUP.md`: local environment and troubleshooting only

## Required Updates By Change Type

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

- update `TODO.md`
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
- create annotated tags for intentional releases
- keep `CHANGELOG.md` aligned with releases
- do not introduce another human-facing completion archive file

## Definition Of Done

A change is complete when:

- the intended behavior exists in an appropriate spec artifact
- implementation and specs agree
- public contract artifacts are updated when behavior changed
- `./gradlew build` passes
