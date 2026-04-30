# Completed Tasks Archive

This file keeps the historical record for completed roadmap items that were moved out of `TODO.md`.
Use `TODO.md` for active planning and `COMPLETED_TASKS.md` for implementation history, archived scope, and commit references.

## Completed Highlights

- Development Container setup for VS Code Remote development
- PostgreSQL production profile and local Docker workflow
- PostgreSQL-backed integration testing with Testcontainers
- Profile-based configuration split for local, prod, and test
- Localization message entity, service, REST API, and seed data
- Localized error responses with browser-compatible preferred-language support
- Book search and filtering support
- Developer setup and contribution guides

## Archived Completed Work

### Development Container Setup

Status: Completed

Summary:
- Added a VS Code dev container with Java 25, Docker support, PostgreSQL, Prometheus, helper commands, and preconfigured extensions.
- Added `.devcontainer` documentation for quick-start and detailed usage.

### Phase 1: Infrastructure & Database Migration

#### 1.1 Migrate from H2 to PostgreSQL

Status: Completed

Summary:
- Added PostgreSQL JDBC support and production datasource configuration.
- Added environment-variable-based production settings and local Docker Compose support.
- Verified Flyway compatibility and documented the workflow.

Commit: `4d99e58`

#### 1.2 Add Testcontainers for Integration Testing

Status: Completed

Summary:
- Added Testcontainers dependencies and PostgreSQL-backed integration testing.
- Introduced shared `@TestcontainersTest` support and PostgreSQL service-connection wiring.
- Updated test configuration and team documentation.

Commit: `b543be1`

### Phase 2: Configuration Management

#### 2.1 Profile-Based Configuration Split

Status: Completed

Summary:
- Split configuration into `local`, `prod`, and `test` profiles.
- Kept common defaults in the base configuration and documented profile usage.
- Updated Docker behavior to align with the production profile.

Commit: `9e5185c`

### Phase 3: Internationalization (i18n) & Localization Messages

#### 3.1 Create LocalizationMessage Entity

Status: Completed

Summary:
- Added `LocalizationMessage` entity, repository, service, and not-found exception.
- Added Flyway migration for the `localization_messages` table.
- Added integration coverage for lookup and fallback behavior.

Commit: `088b69d`

#### 3.2 Create Localization REST API

Status: Completed

Summary:
- Added CRUD, list, lookup-by-key/language, and list-by-language endpoints.
- Added request/response DTOs, validation, pagination, sort validation, and error handling.
- Added integration tests and Spring REST Docs coverage.

Commit: `8365b65`

#### 3.3 Seed Initial Localization Messages

Status: Completed

Summary:
- Defined stable message keys for current error scenarios.
- Seeded localization data for English, Spanish, German, French, Polish, Ukrainian, and Norwegian.
- Centralized seeded keys and added tests to verify coverage across supported languages.

Commit: `104c1ed`

### Phase 4: Error Response Localization

#### 4.1 Integrate Localization into Exception Handler

Status: Completed

Summary:
- Updated `ApiExceptionHandler` to resolve localized messages through `LocalizationMessageService`.
- Added `RequestLanguageResolver` with `lang` override and `Accept-Language` support.
- Added `messageKey`, localized `message`, and resolved `language` to `ProblemDetail` responses.
- Added and documented tests for header-based negotiation, explicit override, and English fallback.

Commit: `8e99800`

### Phase 6: Enhanced Book API

#### 6.1 Add Search & Filtering to Books

Status: Completed

Summary:
- Added `BookSearchRequest`, specifications-based filtering, and sort validation.
- Added integration coverage for filtering, sorting, and invalid-request handling.
- Updated generated API docs for the enhanced list endpoint.

Commit: `a94abd0`

### Phase 8: Documentation & Developer Experience

#### 8.2 Create Developer Setup Guide

Status: Completed

Summary:
- Added `SETUP.md` and `.env.example`.
- Documented local H2, local PostgreSQL, IntelliJ, VS Code, and dev-container workflows.
- Added troubleshooting guidance and quick-start commands.

Commit: `4df2e54`

#### 8.3 Add Contribution Guidelines

Status: Completed

Summary:
- Added `CONTRIBUTING.md` and sample Git hooks.
- Documented contribution expectations, testing standards, and quality gates.
- Declared `AGENTS.md` authoritative for technical constraints when docs diverge.

Commit: `6ab4665`

## Roadmap Maintenance Notes

- Keep active work in `TODO.md`.
- Move finished phases or milestones here once they are fully complete.
- Preserve commit references when archiving completed work.
