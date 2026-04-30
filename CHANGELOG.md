# Changelog

All notable changes to this project are documented in this file.

The format is based on Keep a Changelog and the project uses semantic version tags in the form `vMAJOR.MINOR.PATCH`.
Each completed roadmap phase is released with an annotated git tag. The Gradle build version is derived from the nearest reachable git tag.

## [Unreleased]

### Planned
- OAuth-based security and JDBC-backed sessions
- OpenAPI contract generation and breaking-change checks

## [v0.15.0] - 2026-04-30

### Changed
- Replaced entity-level eager fetching with repository-controlled fetch plans for books and categories.
- Replaced the previous simple in-memory cache manager with Caffeine-backed caches.
- Moved cache enablement out of `TechnicalInterviewDemoApplication` into a dedicated configuration class under the `config` package.

## [v0.12.0] - 2026-04-30

### Added
- Defined the release tagging policy for completed roadmap phases.
- Added this human-readable changelog and backfilled historical releases from the roadmap archive.
- Documented how roadmap phases map to commits, changelog entries, and annotated git tags.

## [v0.11.0] - 2026-04-30

### Added
- Added `CONTRIBUTING.md` and contribution workflow guidance.
- Documented testing expectations, quality gates, and collaboration rules.

## [v0.10.0] - 2026-04-30

### Added
- Added `SETUP.md` and `.env.example`.
- Documented local, PostgreSQL, IntelliJ, VS Code, and dev-container setup flows.

## [v0.9.0] - 2026-04-30

### Added
- Added book search and filtering by title, author, ISBN, and publication year.
- Documented the expanded list endpoint and invalid-filter behavior.

## [v0.8.0] - 2026-04-30

### Added
- Added cookie-based language fallback for localized error responses.
- Added request-scoped language resolution and supported-language validation.

## [v0.7.0] - 2026-04-30

### Added
- Localized `ProblemDetail` responses with `messageKey`, localized `message`, and resolved `language`.
- Added browser-compatible preferred-language handling with `Accept-Language` and `lang`.

## [v0.6.0] - 2026-04-30

### Added
- Seeded localization messages for the current API error scenarios.
- Added test coverage to keep seeded keys aligned across supported languages.

## [v0.5.0] - 2026-04-30

### Added
- Added CRUD, lookup, pagination, and documentation coverage for localization messages.

## [v0.4.0] - 2026-04-30

### Added
- Added the `LocalizationMessage` entity, repository, service, and Flyway migration.

## [v0.3.0] - 2026-04-30

### Changed
- Split runtime configuration into `local`, `prod`, and `test` profiles.
- Documented default profile behavior and container profile selection.

## [v0.2.0] - 2026-04-30

### Added
- Added PostgreSQL-backed integration testing with Testcontainers.
- Introduced shared test infrastructure for containerized database tests.

## [v0.1.0] - 2026-04-30

### Added
- Added PostgreSQL runtime support and production-oriented datasource configuration.
- Added Docker Compose support for local PostgreSQL development.
