# Changelog

All notable changes to this project are documented in this file.

The format is based on Keep a Changelog and the project uses semantic version tags in the form `vMAJOR.MINOR.PATCH`.
Each completed roadmap phase is released with an annotated git tag. Release numbers must increase in `git log --first-parent` order, and the Gradle build version is derived from the nearest reachable git tag.
Historical `v0.0.x` releases were backfilled as annotated tags from mainline git history to preserve the pre-phase milestones that landed before the roadmap-based release numbering stabilized.

## [Unreleased]

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
- Added append-only audit-log persistence for `Book` and `LocalizationMessage` create, update, and delete operations.
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
- Added the `LocalizationMessage` entity, repository, service, and Flyway migration.

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
