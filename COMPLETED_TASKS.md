# Completed Tasks Archive

This file keeps the historical record for completed roadmap items that were moved out of `TODO.md`.
Use `TODO.md` for active planning and `COMPLETED_TASKS.md` for implementation history, archived scope, commit references, and release tags.

## Completed Highlights

- Development Container setup for VS Code Remote development
- PostgreSQL production profile and local Docker workflow
- PostgreSQL-backed integration testing with Testcontainers
- Profile-based configuration split for local, prod, and test
- Localization message entity, service, REST API, and seed data
- Localized error responses with browser-compatible preferred-language support, cookie fallback, and supported-language validation
- Book search and filtering support
- Book categories and category filtering
- Developer setup and contribution guides
- Release versioning and changelog workflow

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
Tag: `v0.1.0`

#### 1.2 Add Testcontainers for Integration Testing

Status: Completed

Summary:
- Added Testcontainers dependencies and PostgreSQL-backed integration testing.
- Introduced shared `@TestcontainersTest` support and PostgreSQL service-connection wiring.
- Updated test configuration and team documentation.

Commit: `b543be1`
Tag: `v0.2.0`

### Phase 2: Configuration Management

#### 2.1 Profile-Based Configuration Split

Status: Completed

Summary:
- Split configuration into `local`, `prod`, and `test` profiles.
- Kept common defaults in the base configuration and documented profile usage.
- Updated Docker behavior to align with the production profile.

Commit: `9e5185c`
Tag: `v0.3.0`

### Phase 3: Internationalization (i18n) & Localization Messages

#### 3.1 Create LocalizationMessage Entity

Status: Completed

Summary:
- Added `LocalizationMessage` entity, repository, service, and not-found exception.
- Added Flyway migration for the `localization_messages` table.
- Added integration coverage for lookup and fallback behavior.

Commit: `088b69d`
Tag: `v0.4.0`

#### 3.2 Create Localization REST API

Status: Completed

Summary:
- Added CRUD, list, lookup-by-key/language, and list-by-language endpoints.
- Added request/response DTOs, validation, pagination, sort validation, and error handling.
- Added integration tests and Spring REST Docs coverage.

Commit: `8365b65`
Tag: `v0.5.0`

#### 3.3 Seed Initial Localization Messages

Status: Completed

Summary:
- Defined stable message keys for current error scenarios.
- Seeded localization data for English, Spanish, German, French, Polish, Ukrainian, and Norwegian.
- Centralized seeded keys and added tests to verify coverage across supported languages.

Commit: `104c1ed`
Tag: `v0.6.0`

### Phase 4: Error Response Localization

#### 4.1 Integrate Localization into Exception Handler

Status: Completed

Summary:
- Updated `ApiExceptionHandler` to resolve localized messages through `LocalizationMessageService`.
- Added `RequestLanguageResolver` with `lang` override and `Accept-Language` support.
- Added `messageKey`, localized `message`, and resolved `language` to `ProblemDetail` responses.
- Added and documented tests for header-based negotiation, explicit override, and English fallback.

Commit: `8e99800`
Tag: `v0.7.0`

#### 4.2 Add Language Negotiation

Status: Completed

Summary:
- Added cookie `language` fallback after `lang` and `Accept-Language`.
- Added request-scoped localization context so resolved language is captured once per request and reused during error handling.
- Added supported-language validation for managed localization API inputs.
- Added tests for cookie fallback and unsupported-language validation.

Note:
- Authenticated user preference fallback belongs with user persistence and remains a Phase 5 concern.

Commit: `a6708fd`
Tag: `v0.8.0`

### Phase 6: Enhanced Book API

#### 6.1 Add Search & Filtering to Books

Status: Completed

Summary:
- Added `BookSearchRequest`, specifications-based filtering, and sort validation.
- Added integration coverage for filtering, sorting, and invalid-request handling.
- Updated generated API docs for the enhanced list endpoint.

Commit: `a94abd0`
Tag: `v0.9.0`

#### 6.2 Add Book Categories/Tags

Status: Completed

Summary:
- Added the `Category` entity, repository, service, controller, migration, and startup seed data.
- Extended book create and update flows to assign existing categories by name.
- Added category filtering to `GET /api/books`, plus integration tests and Spring REST Docs coverage.

Tag: `v0.13.0`

### Phase 8: Documentation & Developer Experience

#### 8.2 Create Developer Setup Guide

Status: Completed

Summary:
- Added `SETUP.md` and `.env.example`.
- Documented local H2, local PostgreSQL, IntelliJ, VS Code, and dev-container workflows.
- Added troubleshooting guidance and quick-start commands.

Commit: `4df2e54`
Tag: `v0.10.0`

#### 8.3 Add Contribution Guidelines

Status: Completed

Summary:
- Added `CONTRIBUTING.md` and sample Git hooks.
- Documented contribution expectations, testing standards, and quality gates.
- Declared `AGENTS.md` authoritative for technical constraints when docs diverge.

Commit: `6ab4665`
Tag: `v0.11.0`

#### 8.4 Add Release Versioning & Changelog

Status: Completed

Summary:
- Defined semantic version tags for completed roadmap phases and backfilled annotated tags for previously archived milestones.
- Added `CHANGELOG.md` using a Keep a Changelog-style format.
- Documented how roadmap phases, commits, and release tags align.

Tag: `v0.12.0`

## Roadmap Maintenance Notes

- Keep active work in `TODO.md`.
- Move finished phases or milestones here once they are fully complete.
- Preserve commit references and release tags when archiving completed work.
