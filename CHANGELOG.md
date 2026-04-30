# Changelog

All notable changes to this project are documented in this file.

The format is based on Keep a Changelog and the project uses semantic version tags in the form `vMAJOR.MINOR.PATCH`.
Each completed roadmap phase is released with an annotated git tag. Release numbers must increase in `git log --first-parent` order, and the Gradle build version is derived from the nearest reachable git tag.
Historical releases before `v0.1.0` are reconstructed from mainline git history using `v0.0.x` labels because annotated phase tags started at `v0.1.0`.

## [Unreleased]

### Planned
- OAuth-based security and JDBC-backed sessions
- OpenAPI contract generation and breaking-change checks

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
