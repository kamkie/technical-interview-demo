# Changelog

All notable released changes to this project are documented in this file.

The format is based on Keep a Changelog and the project uses semantic version tags in the form `vMAJOR.MINOR.PATCH`.
The Gradle build version is derived from the nearest reachable annotated git tag, and release numbers must increase in `git log --first-parent` order.

## [Unreleased]

## [v1.0.1] - 2026-05-02

### Changed
- Added a concrete post-`1.0` maintainer release checklist across the AI release workflow, contributor guidance, and the human-facing release model, including Flyway review, benchmark-decision guidance, and verification of the immutable short-SHA image tag.
- Documented the current healthy-runtime expectations for readiness, operational metadata, Prometheus metrics, append-only audit logging, and JDBC-backed authenticated sessions.
- Added operator-facing upgrade, rollback, and troubleshooting runbooks for Flyway-backed releases, optional OAuth setup, PostgreSQL connectivity, and Spring Session JDBC persistence.

## [v1.0.0] - 2026-05-02

### Changed
- Clarified the locked `1.0` deployment posture across setup guidance, technical-endpoint docs, reviewer HTTP examples, and monitoring/chart metadata, including the deployment-scoped status of `GET /actuator/prometheus`.
- Updated the technical overview and REST Docs-backed technical coverage so the repository explicitly documents the locked `1.0` posture for CSRF, optional OAuth login, Prometheus scraping, and the stable `/` and `/hello` endpoints.
- Refined the `1.0` contract narrative across AI design guidance, generated docs, and OpenAPI metadata, including the stable `1.x` status of `/` and `/hello` and the deployment-scoped status of Prometheus scraping.
- Reworked `README.md` around the frozen `1.0` promise, the stable `1.x` contract tiers, the deployment-scoped Prometheus surface, and the locked production posture for optional OAuth, secure session cookies, admin bootstrap, and the deliberate CSRF tradeoff.

## [v0.24.2] - 2026-05-01

### Added
- Added external HTTP smoke tests under a separate `externalTest` source set to cover the public overview, hello, docs redirect, readiness, and book-list endpoints against an externally running application.

### Changed
- Switched CI, release validation, and repository guidance to use Gradle-owned `externalSmokeTest` and `gatlingBenchmark` tasks as the source of truth for external smoke and benchmark verification.
- Replaced the script-owned benchmark orchestration with `buildSrc` Gradle tasks that run the packaged Docker image plus Dockerized PostgreSQL, use fake OAuth client settings for redirect coverage, and log progress through provisioning, readiness, simulations, baseline handling, and teardown.

## [v0.24.1] - 2026-05-01

### Changed
- Replaced workstation-specific `.env.example` path examples with portable placeholders for JDK and IntelliJ configuration.
- Extended the tag-driven release workflow to publish a GitHub Release from the matching `CHANGELOG.md` section after container-image publication succeeds.
- Added grouped weekly Dependabot updates for Gradle, GitHub Actions, and Docker while keeping the existing `CI` workflow as the single PR validation path.
- Hardened the `prod` profile so database connection settings must be provided explicitly, while `SESSION_COOKIE_SECURE` remains optional with a secure-by-default value.

## [v0.24.0] - 2026-05-01

### Added
- Added explicit `BookResponse` and `CategoryResponse` API models so public controllers no longer expose JPA entities directly.
- Added a shared `ApiProblemResponse` OpenAPI schema and documented `401`/`403` category-write error responses in REST Docs, HTTP examples, and the approved OpenAPI baseline.
- Added localized seed messages for `error.request.unauthorized`.
- Added focused unit coverage for the API security entry point and access-denied handler.

### Changed
- Standardized API `401` and security-layer `403` responses to use the same localized `ProblemDetail` shape as the rest of the API.
- Moved book and category response schema metadata from persistence entities onto dedicated response DTOs while preserving the existing JSON field names and category ordering.
- Updated localization list tests and contract artifacts to account for the new seeded auth-error localization key.

## [v0.23.0] - 2026-05-01

### Added
- Added a GitHub Actions `CI` workflow that runs the full Gradle build, Helm validation, and container smoke validation.
- Added a tag-based `Release` workflow that publishes container images to GitHub Container Registry with semantic-version and commit-SHA tags.
- Added production-profile container smoke validation for the packaged Docker image.
- Added vendor-neutral Kubernetes deployment manifests under `k8s/` together with a local Kustomize overlay.
- Added a Helm chart under `helm/technical-interview-demo` for the application deployment contract.
- Added monitoring assets for Prometheus, Grafana, and Alertmanager, including a ServiceMonitor, alert rules, dashboard, and upstream stack values.

### Changed
- Reworked the repository docs around a Spec-Driven Development workflow and aligned `README.md`, `SETUP.md`, `CONTRIBUTING.md`, and `AGENTS.md`.
- Made `SETUP.md` the sole detailed onboarding, deployment, and troubleshooting guide.
- Replaced the standalone OpenAPI compatibility workflow with the consolidated `CI` workflow.
- Updated `ROADMAP.md` so completed Milestone 10 work no longer appears as active planned work.

### Removed
- Removed `COMPLETED_TASKS.md` so released human history now lives only in `CHANGELOG.md`.

## [v0.22.0] - 2026-05-01

### Added
- Added `GET /` as the public technical overview endpoint with build, dependency, runtime, and important configuration details.
- Added collection-style localization filtering through `GET /api/localizations?messageKey=...&language=...`.

### Changed
- Renamed `HelloController` to `TechnicalOverviewController` and aligned the technical overview package structure around `technical.info`.
- Renamed the authenticated-user singleton API from `/api/users/me` to `/api/account` and the language update route to `/api/account/language`.
- Renamed the localization slice from `LocalizationMessage*` to `Localization*` across the codebase, tests, docs, and HTTP example collections.
- Refreshed the generated documentation, approved OpenAPI baseline, and Gatling scenarios to match the simplified pre-`1.0` naming conventions.

### Removed
- Removed the specialized localization lookup routes `/api/localizations/key/{messageKey}/lang/{language}` and `/api/localizations/language/{language}` in favor of collection filters on `/api/localizations`.

## [v0.21.0] - 2026-04-30

### Added
- Added OpenAPI JSON and YAML example requests to `src/test/resources/http/documentation.http`.
- Added a JaCoCo coverage summary task, focused service/exception-path tests, and enforced coverage thresholds of 90% line coverage and 70% branch coverage.
- Added Gatling performance scenarios together with a tracked local baseline and a benchmark runner for public reads and OAuth redirect startup.

### Changed
- Updated `OpenApiBaselineGenerator` to use try-with-resources so the PMD-backed `build` verification stays clean.
- Documented the coverage workflow and the local performance regression checks across the project docs.
- Reduced Gatling benchmark log noise and aligned the Gradle build formatting with Spotless.

## [v0.20.0] - 2026-04-30

### Added
- Added a dedicated GitHub Actions workflow that runs the OpenAPI compatibility integration test on pull requests and pushes to `main`.

### Changed
- Updated the roadmap and contributor-facing documentation to reflect that the OpenAPI compatibility gate now runs in CI.

## [v0.19.0] - 2026-04-30

### Added
- Split the HTTP client examples into per-controller files, including a dedicated authentication collection and localized request examples.
- Added reviewer-focused local auth examples for the GitHub OAuth flow plus local PostgreSQL run-configuration support.
- Added OpenAPI contract endpoints at `/v3/api-docs` and `/v3/api-docs.yaml`.
- Added an approved OpenAPI baseline, a contract refresh task, and a backward-compatibility integration test for the published API surface.

### Changed
- Reorganized application packages around `business` and `technical` concerns, including the extraction of localization infrastructure into `technical.localization`.
- Split user-facing business logic from security synchronization code and moved cache configuration into `technical.cache`.
- Removed the obsolete language-resolution helper in favor of `LocalizationContext`.
- Disabled CSRF protection for the demo's session-based authentication flow so authenticated write requests no longer require a CSRF token round-trip.
- Refreshed the HTTP examples for GitHub OAuth placeholders, controller-specific requests, and expected 404 scenarios.
- Documented the OpenAPI contract, baseline refresh workflow, and reviewer-facing auth setup across the project docs.

## [v0.18.0] - 2026-04-30

### Added
- Added append-only audit-log persistence for `Book` and `Localization` create, update, and delete operations.
- Added a Flyway migration for the `audit_logs` table and integration coverage for audited write flows.

### Changed
- Recorded the acting persisted user and actor-login snapshot with each audited state-changing operation.
- Updated roadmap and project documentation to reflect the completed audit-trail phase.

## [v0.17.0] - 2026-04-30

### Added
- Added persisted application users with stored `USER` and `ADMIN` roles.
- Added authenticated-user profile endpoints for reading the current profile and updating preferred language.
- Added user-specific Micrometer metrics and authenticated-user synchronization on login.

### Changed
- Used persisted application roles to authorize category and localization-message management.
- Used the authenticated user's preferred language as the final localized-error fallback when a request does not explicitly specify language.

## [v0.16.0] - 2026-04-30

### Added
- Added Spring Security OAuth 2.0 support with GitHub as the demo-friendly provider.
- Added PostgreSQL-backed Spring Session JDBC storage for authenticated browser sessions.
- Added authentication and CSRF coverage for protected write endpoints, plus an integration test that verifies the JDBC session repository persists the security context.

### Changed
- Protected state-changing `book`, `category`, and `localization-message` endpoints while keeping public read endpoints open.
- Added profile-specific OAuth and session-cookie configuration for local, test, and production-style environments.
- Updated the setup guide, generated API docs, and project instructions to document the new OAuth login flow and secured API surface.

## [v0.15.0] - 2026-04-30

### Changed
- Replaced entity-level eager fetching with repository-controlled fetch plans for books and categories.
- Replaced the previous simple in-memory cache manager with Caffeine-backed caches.
- Moved cache enablement out of `TechnicalInterviewDemoApplication` into a dedicated configuration class under the `config` package.

## [v0.14.0] - 2026-04-30

### Added
- Added application metrics for book, category, and localization operations.
- Added cache-backed category and localization lookups with dedicated cache names and coverage tests.
- Added focused verification for cache behavior and metric emission.

### Changed
- Documented the new caching and metrics behavior in the project docs and technical endpoints documentation.

## [v0.13.0] - 2026-04-30

### Added
- Added the `Category` domain, repository, service, controller, and startup seed data.
- Added category assignment for books and category-based filtering in the book search API.
- Added Flyway migration support for category and book-category tables.
- Added REST Docs and integration tests for category endpoints and category-aware book responses.

### Changed
- Extended book create, update, and search flows to work with categories.

## [v0.12.0] - 2026-04-30

### Added
- Defined the release tagging policy for completed roadmap phases.
- Added this human-readable changelog and backfilled historical releases from the roadmap archive and git history.
- Documented how roadmap phases map to commits, changelog entries, and annotated git tags.

## [v0.11.0] - 2026-04-30

### Added
- Added cookie-based language fallback for localized error responses.
- Added request-scoped language resolution and supported-language validation.

## [v0.10.0] - 2026-04-30

### Added
- Localized `ProblemDetail` responses with `messageKey`, localized `message`, and resolved `language`.
- Added browser-compatible preferred-language handling with `Accept-Language` and `lang`.
- Added seeded localization support for Polish, French, Ukrainian, and Norwegian.

### Changed
- Refactored Spring REST Docs into a multi-page structure with an indexed entry page, per-controller pages, and a dedicated technical-endpoints page.

## [v0.9.0] - 2026-04-30

### Added
- Seeded localization messages for the current API error scenarios.
- Added test coverage to keep seeded keys aligned across supported languages.

## [v0.8.0] - 2026-04-30

### Added
- Added CRUD, lookup, pagination, and documentation coverage for localization messages.

## [v0.7.0] - 2026-04-30

### Added
- Added the `Localization` entity, repository, service, and Flyway migration.

## [v0.6.0] - 2026-04-30

### Added
- Added PostgreSQL-backed integration testing with Testcontainers.
- Introduced shared test infrastructure for containerized database tests.

## [v0.5.0] - 2026-04-30

### Added
- Added book search and filtering by title, author, ISBN, and publication year.
- Documented the expanded list endpoint and invalid-filter behavior.

## [v0.4.0] - 2026-04-30

### Added
- Added `CONTRIBUTING.md` and contribution workflow guidance.
- Documented testing expectations, quality gates, and collaboration rules.

## [v0.3.0] - 2026-04-30

### Added
- Added `SETUP.md` and `.env.example`.
- Documented local, PostgreSQL, IntelliJ, VS Code, and dev-container setup flows.

## [v0.2.0] - 2026-04-30

### Added
- Added PostgreSQL runtime support and production-oriented datasource configuration.
- Added Docker Compose support for local PostgreSQL development.

## [v0.1.0] - 2026-04-30

### Changed
- Split runtime configuration into `local`, `prod`, and `test` profiles.
- Documented default profile behavior and container profile selection.

## [v0.0.6] - 2026-04-30

### Added
- Added Flyway-driven schema migrations and schema validation alignment.
- Added Qodana static-analysis integration to the build workflow.
- Added a VS Code dev container with PostgreSQL and Prometheus services for zero-friction local setup.

### Changed
- Updated the Dockerfile and setup documentation to support the dev-container-based workflow.

## [v0.0.5] - 2026-04-29

### Added
- Added Spring REST Docs and Asciidoctor-based API documentation generation.
- Added the `/docs` endpoint and packaged generated documentation into the runnable application.
- Added optimistic locking for books with version-aware updates and conflict handling.
- Added pagination and richer API documentation for book responses and actuator endpoints.
- Added a Gradle Docker build task and improved git-based version resolution for container builds.

### Changed
- Hardened logging by redacting sensitive query parameters before they reach application logs.
- Refined technical documentation for error examples, health probes, readiness, and Prometheus metrics.

## [v0.0.4] - 2026-04-29

### Added
- Added Error Prone, PMD, and JaCoCo to strengthen static analysis and code-quality enforcement.
- Added request ID generation, propagation, and MDC logging support.
- Added Lombok and Spring Boot devtools to streamline development.

### Changed
- Split book write contracts into dedicated create and update requests and kept ISBN immutable on updates.
- Refactored controllers to consistently build `ResponseEntity` responses from local payload variables.
- Upgraded and tuned the Gradle build, wrapper, test logging, and plugin configuration.

## [v0.0.3] - 2026-04-29

### Added
- Added Spotless formatting with `.editorconfig`-driven conventions.
- Added a comprehensive `README.md` aligned with `AGENTS.md`.
- Added Docker support with a multi-stage image and IntelliJ run configurations.

### Changed
- Improved formatter integration to work cleanly with IntelliJ IDEA when available.
- Standardized newline and formatting behavior across the repository.

## [v0.0.2] - 2026-04-29

### Added
- Added centralized API exception handling with structured error responses.
- Added detailed exception logging, property-path sanitization, and hidden internal exception details.
- Added service-layer logging through `ServiceLoggingAspect`.
- Added HTTP tracing and logging filters with `traceparent` propagation and sensitive-data sanitization.

### Changed
- Moved non-trivial book behavior into a logged service layer to keep controllers small.

## [v0.0.1] - 2026-04-29

### Added
- Initialized the Spring Boot application and Gradle build.
- Added the initial `Book` CRUD API with startup seed data.
- Added unique ISBN enforcement, request validation, and in-memory test/runtime support.
- Added baseline API tests, sample request documentation, and CRUD operation logging.
- Added initial project documentation in `HELP.md` and `AGENTS.md`.
