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

## 1.0 Promise

`1.0` means a stable interview-demo reference app.

It does **not** mean a production-ready starter platform with every security and deployment concern solved in-app.

The `1.x` compatibility promise applies to the documented supported contract in this file, the generated docs, the HTTP examples, and the approved OpenAPI baseline where applicable.
It does not extend to deployment-specific exposure choices outside that supported contract.

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

## Supported Contract

Stable `1.x` contract:

- user-facing and documentation endpoints:
  - `GET /`
  - `GET /hello`
  - `GET /docs`
  - `GET /v3/api-docs`
  - `GET /v3/api-docs.yaml`
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
- supported operational endpoints:
  - `GET /actuator/info`
  - `GET /actuator/health`
  - `GET /actuator/health/liveness`
  - `GET /actuator/health/readiness`

Deployment-scoped technical surface:

- `GET /actuator/prometheus`
  - supported for trusted deployment scraping and monitoring integration
  - not part of the internet-public contract

Remaining demo-only convenience endpoints:

- none intentionally excluded from the stable `1.x` contract at this stage

Supported technical bootstrap:

- `GET /oauth2/authorization/github`
  - interactive login entry point when the optional `oauth` profile is active

Security summary:

- public supported reads: `/`, `/hello`, `/docs`, OpenAPI docs, `GET /api/books/**`, `GET /api/categories`, `GET /api/localizations/**`, actuator health endpoints, and actuator info
- authenticated session required: account endpoints and all write endpoints
- `ADMIN` role required: category creation and localization create/update/delete
- interactive login starts at `GET /oauth2/authorization/github` when the `oauth` profile is active

Contract notes:

- `GET /api/books` is paginated and supports text, category, and year filters
- `GET /api/localizations` is paginated and supports optional exact `messageKey` and `language` filters
- localized error responses include `messageKey`, localized `message`, and resolved `language`
- protected API `401` and `403` responses use that same localized `ProblemDetail` structure
- authenticated-user preferred language is the last localization fallback before English

## Quality Model

Standard verification command:

```powershell
.\gradlew.bat build
```

Use `SETUP.md` for environment prerequisites and local tooling details.

The standard build includes Spotless, PMD, tests, JaCoCo thresholds, REST Docs generation, OpenAPI compatibility verification, boot jar creation, and Docker image build.

Supply-chain verification is part of the standard build:

- `.\gradlew.bat build` also runs Gradle-owned dependency and Docker-image vulnerability scans
- `.\gradlew.bat vulnerabilityScan` runs the two scan tasks directly when you want the security checks without the full lifecycle
- scan reports are written under `build/reports/security/` as JSON, SARIF, and summary text files
- unsuppressed `HIGH` and `CRITICAL` findings fail the relevant scan task and therefore fail the build
- suppressions must be explicit and reviewable through `config/security/trivy.ignore`

Additional change-sensitive checks:

- refresh the approved OpenAPI baseline intentionally with `./gradlew refreshOpenApiBaseline` only when the reviewed API contract changed
- rerun `./gradlew gatlingBenchmark` when changing book list/search behavior, localization lookup behavior, or OAuth/session startup behavior

## Release Model

- application version is derived from the nearest reachable annotated git tag
- use semantic version tags in the form `vMAJOR.MINOR.PATCH`
- keep release numbers increasing in `git log --first-parent` order
- record human-facing release history in `CHANGELOG.md`
- tag-driven releases publish the matching `CHANGELOG.md` version section as GitHub Release notes
- maintainers prepare releases only from validated `main`
- before tagging, maintainers review new Flyway migrations, confirm whether `gatlingBenchmark` is required, and complete changelog/roadmap/plan cleanup
- after pushing a release tag, maintainers verify the remote workflow published both the semantic image tag and the immutable short-SHA image tag

## CI/CD And Deployment

Supported delivery path:

- GitHub Actions is the repository CI/CD platform
- pull requests to `main` and pushes to `main` run the `CI` workflow, which executes `./gradlew build` and `./gradlew externalSmokeTest`
- Dependabot opens grouped weekly update PRs for Gradle, GitHub Actions, and Docker, and those PRs are expected to pass the same `CI` workflow before merge
- the `CI` workflow uploads the generated vulnerability scan artifacts from `build/reports/security/` so blocked runs remain reviewable
- semantic version tags trigger the `Release` workflow, which builds and scans the tagged image with Gradle, validates it with `./gradlew externalSmokeTest`, publishes it to GitHub Container Registry as `ghcr.io/<owner>/<repo>:<tag>` and `ghcr.io/<owner>/<repo>:sha-<12-char-commit>`, then creates the matching GitHub Release from `CHANGELOG.md`
- deployment artifacts are provided as:
  - Docker image
  - vendor-neutral Kubernetes manifests under `k8s/base` with a local overlay under `k8s/overlays/local`
  - Helm chart under `helm/`
  - monitoring and alerting assets for Prometheus, Grafana, and Alertmanager

Required deployment environment variables:

- `DATABASE_HOST`
- `DATABASE_PORT`
- `DATABASE_NAME`
- `DATABASE_USER`
- `DATABASE_PASSWORD`

Optional deployment environment variables:

- `SESSION_COOKIE_SECURE` with a secure-by-default value of `true`
- `GITHUB_CLIENT_ID`
- `GITHUB_CLIENT_SECRET`
- `ADMIN_LOGINS`

Frozen `1.0` production posture:

- `prod` remains the deployment profile baseline
- OAuth login remains opt-in through the `oauth` profile plus `GITHUB_CLIENT_ID` and `GITHUB_CLIENT_SECRET`
- `ADMIN_LOGINS` remains the environment-driven admin bootstrap mechanism
- `SESSION_COOKIE_SECURE` remains optional with a secure-by-default value of `true`
- browser-session write flows keep CSRF disabled as a deliberate demo tradeoff for reviewer-friendly session-based API exercise flows
- `GET /actuator/prometheus` remains available for trusted deployment scraping, but it is not part of the internet-public contract

Trusted deployment topology assumption:

- health, readiness, and info endpoints are safe to expose as operational endpoints
- Prometheus scraping is expected to happen from trusted internal infrastructure such as cluster-local monitoring, not from arbitrary public clients

Use the raw manifests under `k8s/` when you want explicit repo-owned YAML. Use the Helm chart under `helm/technical-interview-demo` when you want the same deployment contract packaged behind values files.

Monitoring support uses the upstream `kube-prometheus-stack` Helm chart plus repo-owned ServiceMonitor, alert-rule, Grafana dashboard, and Alertmanager example assets under `k8s/monitoring` and `monitoring/`.

## Project Map

- `SETUP.md`: developer onboarding, local environment setup, OAuth setup, Docker workflow, and troubleshooting
- `ai/PROMPTS.md`: reusable prompt library for repository-specific planning, implementation, release, and multi-agent requests
- `ai/WORKFLOW.md`: Codex multi-agent workflow, task integration, and release handoff rules for this repository
- `AGENTS.md`: AI-facing project rules and spec-driven working contract
- `ROADMAP.md`: active roadmap only
- `CHANGELOG.md`: released history only
- `CONTRIBUTING.md`: contributor workflow and review expectations
- `k8s/`: raw Kubernetes deployment and monitoring manifests
- `helm/technical-interview-demo/`: Helm chart for the application deployment contract
- `monitoring/`: upstream-stack values, Grafana dashboards, and Alertmanager examples
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
