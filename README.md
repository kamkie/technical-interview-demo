# Technical Interview Demo

`AGENTS.md` is the AI-facing counterpart of this file. Keep both files aligned when project behavior, public API contracts, formatter usage, logging/tracing behavior, or quality gates change.

## Overview

Small Spring Boot demo application for technical interview exercises.

Current scope:

- `GET /docs` redirects to generated API documentation
- `GET /` returns technical application details including build/git metadata, dependency versions, and important runtime configuration
- `GET /hello` returns `Hello World!`
- `Book` API under `/api/books` with pagination, filtering, optimistic locking, and category assignment
- `Category` API under `/api/categories`
- `Localization` API under `/api/localizations` with CRUD plus collection filtering by `messageKey` and `language`
- authenticated account API under `/api/account`
- OAuth 2.0 protected write endpoints with JDBC-backed HTTP sessions
- append-only audit logging for state-changing `Book` and `Localization` operations
- generated REST Docs and an approved OpenAPI baseline
- PostgreSQL runtime profiles and PostgreSQL-backed integration tests via Testcontainers
- request tracing, structured logging, in-memory caches, application-specific Prometheus metrics, and tracked Gatling baselines

Primary goal: keep the codebase small, readable, and easy to reason about.

## Spec-Driven Development

This project should be changed spec-first, not implementation-first.

Working rule:

1. Define or update the intended behavior in a spec artifact.
2. Implement the smallest code change that satisfies that spec.
3. Verify the executable and published specs stay aligned.

Authoritative spec artifacts:

- integration tests and REST Docs tests under `src/test/java/`
- generated AsciiDoc sources under `src/docs/asciidoc/`
- approved OpenAPI baseline at `src/test/resources/openapi/approved-openapi.json`
- HTTP example collections under `src/test/resources/http/`
- this `README.md` for the supported human-facing API/runtime contract
- `ROADMAP.md` for planned work that is not yet part of the contract
- `CHANGELOG.md` for released history

What that means in practice:

- If a public API changes, update tests, REST Docs, OpenAPI, and HTTP examples in the same change.
- If a refactor should not change behavior, the existing specs should keep passing without contract edits.
- If the intended behavior is unclear, define the missing spec first or stop and clarify it before coding.

## Public API Summary

Endpoints:

- `GET /docs`
- `GET /`
- `GET /hello`
- `GET /api/books`
- `GET /api/books/{id}`
- `POST /api/books`
- `PUT /api/books/{id}`
- `DELETE /api/books/{id}`
- `GET /api/categories`
- `POST /api/categories`
- `GET /api/localizations`
- `GET /api/localizations/{id}`
- `POST /api/localizations`
- `PUT /api/localizations/{id}`
- `DELETE /api/localizations/{id}`
- `GET /api/account`
- `PUT /api/account/language`
- `GET /actuator/info`
- `GET /actuator/health`
- `GET /actuator/health/liveness`
- `GET /actuator/health/readiness`
- `GET /actuator/prometheus`

Security summary:

- public reads: `/`, `/hello`, `/docs`, `GET /api/books/**`, `GET /api/categories`, `GET /api/localizations/**`, actuator health/info/prometheus, and OpenAPI docs
- authenticated session required: account endpoints and all write endpoints
- `ADMIN` role required: category creation and localization create/update/delete
- interactive login starts at `GET /oauth2/authorization/github` when the `oauth` profile is active

Contract notes:

- `GET /api/books` is paginated and supports text, category, and year filters
- `GET /api/localizations` is paginated and supports optional exact `messageKey` and `language` filters
- localized error responses include `messageKey`, localized `message`, and resolved `language`
- authenticated-user preferred language is the last localization fallback before English

## Quality Model

Standard verification command:

```powershell
.\gradlew.bat build
```

Use `SETUP.md` for environment prerequisites and local tooling details.

The standard build includes Spotless, PMD, tests, JaCoCo thresholds, REST Docs generation, OpenAPI compatibility verification, boot jar creation, and Docker image build.

Additional change-sensitive checks:

- refresh the approved OpenAPI baseline intentionally with `./gradlew refreshOpenApiBaseline` only when the reviewed API contract changed
- rerun `./scripts/run-phase-9-benchmarks.ps1` when changing book list/search behavior, localization lookup behavior, or OAuth/session startup behavior

## Release Model

- application version is derived from the nearest reachable annotated git tag
- use semantic version tags in the form `vMAJOR.MINOR.PATCH`
- keep release numbers increasing in `git log --first-parent` order
- record human-facing release history in `CHANGELOG.md`

## CI/CD And Deployment

Supported delivery path:

- GitHub Actions is the repository CI/CD platform
- pull requests and protected branches run the `CI` workflow, which executes the full repository verification flow
- semantic version tags trigger the `Release` workflow, which publishes the Docker image to GitHub Container Registry as `ghcr.io/<owner>/<repo>:<tag>` and `ghcr.io/<owner>/<repo>:sha-<commit>`
- deployment artifacts are provided as:
  - Docker image
  - vendor-neutral Kubernetes manifests under `k8s/`
  - Helm chart under `helm/`
  - monitoring and alerting assets for Prometheus, Grafana, and Alertmanager

Required deployment environment variables:

- `DATABASE_HOST`
- `DATABASE_PORT`
- `DATABASE_NAME`
- `DATABASE_USER`
- `DATABASE_PASSWORD`
- `SESSION_COOKIE_SECURE`

Optional deployment environment variables:

- `GITHUB_CLIENT_ID`
- `GITHUB_CLIENT_SECRET`
- `ADMIN_LOGINS`

Pre-`1.0` production-default blockers still under active roadmap review:

- whether `GET /actuator/prometheus` remains public in deployed environments
- whether the `oauth` profile is enabled by default in deployed environments
- whether the `prod` profile should fail fast on missing database and OAuth secrets instead of using fallback defaults
- whether browser-session write flows need CSRF posture changes before `1.0`

Branch protection recommendation for the default branch:

- require `CI` to pass before merge
- require at least one reviewer on pull requests
- prefer squash merges so release tags map cleanly onto reviewed changes
- restrict `vMAJOR.MINOR.PATCH` tag creation to maintainers who also own release validation

## Project Map

- `SETUP.md`: developer onboarding, local environment setup, OAuth setup, Docker workflow, and troubleshooting
- `AGENTS.md`: AI-facing project rules and spec-driven working contract
- `ROADMAP.md`: active roadmap only
- `CHANGELOG.md`: released history only
- `CONTRIBUTING.md`: contributor workflow and review expectations
- `src/docs/asciidoc/`: generated-doc source pages
- `src/test/resources/openapi/approved-openapi.json`: approved OpenAPI baseline
- `src/test/resources/http/`: runnable HTTP examples grouped by area
- `performance/baselines/phase-9-local.json`: tracked local Gatling baseline

## Local Development

For onboarding, IDE setup, Docker usage, OAuth setup, local PostgreSQL, and troubleshooting, use `SETUP.md`.

## Development Constraints

- preserve the demo nature of the project
- prefer direct Spring MVC, Spring Data JPA, and `@Service` code over extra abstraction
- keep package names under `team.jit.technicalinterviewdemo`
- avoid unnecessary new infrastructure or libraries unless there is a real requirement
- do not remove the existing `hello` or `book` endpoints unless the demo scope is intentionally changing
- add or update tests when API behavior changes

## Definition Of Done

A change is complete when:

- the behavior is captured in the relevant spec artifacts
- implementation, tests, docs, and OpenAPI stay aligned
- the application still starts
- `./gradlew build` passes
