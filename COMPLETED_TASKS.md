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
- In-memory caches and application-specific metrics
- Architecture and cache hardening
- OAuth 2.0 protected write endpoints with JDBC-backed sessions
- Persisted users, roles, and authenticated-user profile endpoints
- Append-only audit logging for state-changing operations
- Developer setup and contribution guides
- OpenAPI contract publication and compatibility gating
- Pre-`1.0` endpoint, package, and class naming simplification for account, localization, and technical overview APIs
- Release versioning and changelog workflow

## Archived Completed Work

### Development Container Setup

Status: Completed

Summary:
- Added a VS Code dev container with Java 25, Docker support, PostgreSQL, Prometheus, helper commands, and preconfigured extensions.
- Added `.devcontainer` documentation for quick-start and detailed usage.

Commit: `bef41b9`
Tag: `v0.0.6`

### Phase 1: Infrastructure & Database Migration

#### 1.1 Migrate from H2 to PostgreSQL

Status: Completed

Summary:
- Added PostgreSQL JDBC support and production datasource configuration.
- Added environment-variable-based production settings and local Docker Compose support.
- Verified Flyway compatibility and documented the workflow.

Commit: `4d99e58`
Tag: `v0.2.0`

#### 1.2 Add Testcontainers for Integration Testing

Status: Completed

Summary:
- Added Testcontainers dependencies and PostgreSQL-backed integration testing.
- Introduced shared `@TestcontainersTest` support and PostgreSQL service-connection wiring.
- Updated test configuration and team documentation.

Commit: `b543be1`
Tag: `v0.6.0`

### Phase 2: Configuration Management

#### 2.1 Profile-Based Configuration Split

Status: Completed

Summary:
- Split configuration into `local`, `prod`, and `test` profiles.
- Kept common defaults in the base configuration and documented profile usage.
- Updated Docker behavior to align with the production profile.

Commit: `9e5185c`
Tag: `v0.1.0`

### Phase 3: Internationalization (i18n) & Localization Messages

#### 3.1 Create Localization Entity

Status: Completed

Summary:
- Added `Localization` entity, repository, service, and not-found exception.
- Added Flyway migration for the `localization_messages` table.
- Added integration coverage for lookup and fallback behavior.

Commit: `088b69d`
Tag: `v0.7.0`

#### 3.2 Create Localization REST API

Status: Completed

Summary:
- Added CRUD, list, lookup-by-key/language, and list-by-language endpoints.
- Added request/response DTOs, validation, pagination, sort validation, and error handling.
- Added integration tests and Spring REST Docs coverage.

Commit: `8365b65`
Tag: `v0.8.0`

#### 3.3 Seed Initial Localization Messages

Status: Completed

Summary:
- Defined stable message keys for current error scenarios.
- Seeded localization data for English, Spanish, German, French, Polish, Ukrainian, and Norwegian.
- Centralized seeded keys and added tests to verify coverage across supported languages.

Commit: `104c1ed`
Tag: `v0.9.0`

### Phase 4: Error Response Localization

#### 4.1 Integrate Localization into Exception Handler

Status: Completed

Summary:
- Updated `ApiExceptionHandler` to resolve localized messages through `LocalizationService`.
- Added `RequestLanguageResolver` with `lang` override and `Accept-Language` support.
- Added `messageKey`, localized `message`, and resolved `language` to `ProblemDetail` responses.
- Added and documented tests for header-based negotiation, explicit override, and English fallback.

Commit: `8e99800`
Tag: `v0.10.0`

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
Tag: `v0.11.0`

### Phase 5: Security & OAuth Integration

#### 5.1 Add Spring Security with OAuth 2.0

Status: Completed

Summary:
- Added Spring Security, GitHub OAuth App login, and authorization rules that keep read endpoints public while protecting state-changing API operations.
- Added Spring Session JDBC with PostgreSQL-backed `SPRING_SESSION` tables and secure profile-specific session-cookie settings.
- Added secured-endpoint test support, authentication and CSRF coverage, and an integration test proving the JDBC session repository persists the security context.
- Updated `README.md`, `SETUP.md`, `AGENTS.md`, and generated docs to explain OAuth setup and the secured API surface.

Commit: `b783bf0`
Tag: `v0.16.0`

#### 5.2 Add User Entity & Management

Status: Completed

Summary:
- Added persisted application users, role storage, and authenticated-user synchronization on login.
- Added `GET /api/account` and `PUT /api/account/language`.
- Added admin-role enforcement for category and localization management plus user-specific Micrometer metrics.
- Updated localization fallback so persisted user preference is used when a request does not explicitly choose a language.

Commit: `7f88845`
Tag: `v0.17.0`

#### 5.3 Secure Audit Logging

Status: Completed

Summary:
- Added the append-only `AuditLog` entity, repository, service, and Flyway migration.
- Recorded audit entries for `Book` and `Localization` create, update, and delete operations.
- Stored the acting persisted user, actor login snapshot, summary, and timestamp for each audited change.
- Added integration tests covering audit-log creation for regular-user and admin write flows.

Commit: `2748d3a`
Tag: `v0.18.0`

### Phase 6: Enhanced Book API

#### 6.1 Add Search & Filtering to Books

Status: Completed

Summary:
- Added `BookSearchRequest`, specifications-based filtering, and sort validation.
- Added integration coverage for filtering, sorting, and invalid-request handling.
- Updated generated API docs for the enhanced list endpoint.

Commit: `a94abd0`
Tag: `v0.5.0`

#### 6.2 Add Book Categories/Tags

Status: Completed

Summary:
- Added the `Category` entity, repository, service, controller, migration, and startup seed data.
- Extended book create and update flows to assign existing categories by name.
- Added category filtering to `GET /api/books`, plus integration tests and Spring REST Docs coverage.

Commit: `b38cc60`
Tag: `v0.13.0`

### Phase 7: Performance & Monitoring

#### 7.1 Add Caching Layer

Status: Completed

Summary:
- Added simple in-memory caches for localization lookups, localization language views, category lists, and the category assignment directory.
- Added explicit cache invalidation on localization and category writes.
- Added cache-behavior integration tests and documented the cache strategy.

Commit: `e49c4f6`
Tag: `v0.14.0`

#### 7.2 Enhance Prometheus Metrics

Status: Completed

Summary:
- Added Micrometer counters and gauges for book, category, localization, and cache activity.
- Recorded metrics directly in service methods and documented the custom metric prefix.
- Added metrics-focused integration tests.

Note:
- User-specific metrics now belong with Phase 5.2 when the user model exists.

Commit: `e49c4f6`
Tag: `v0.14.0`

### Phase 8: Documentation & Developer Experience

#### 8.1 Update API Documentation

Status: Completed

Summary:
- Documented the persisted user model, roles, and `/api/account` profile endpoints across the human-facing docs, AI instructions, and generated docs overview.
- Added reviewer-facing OAuth/session guidance together with the OpenAPI contract and baseline refresh workflow.
- Kept the roadmap notes aligned with the now-complete OpenAPI documentation work.

Commit: `5124b37`
Tag: `v0.19.0`

#### 8.2 Create Developer Setup Guide

Status: Completed

Summary:
- Added `SETUP.md` and `.env.example`.
- Documented local H2, local PostgreSQL, IntelliJ, VS Code, and dev-container workflows.
- Added troubleshooting guidance and quick-start commands.

Commit: `4df2e54`
Tag: `v0.3.0`

#### 8.3 Add Contribution Guidelines

Status: Completed

Summary:
- Added `CONTRIBUTING.md` and sample Git hooks.
- Documented contribution expectations, testing standards, and quality gates.
- Declared `AGENTS.md` authoritative for technical constraints when docs diverge.

Commit: `6ab4665`
Tag: `v0.4.0`

#### 8.4 Add Release Versioning & Changelog

Status: Completed

Summary:
- Defined semantic version tags for completed roadmap phases and backfilled annotated tags for previously archived milestones.
- Added `CHANGELOG.md` using a Keep a Changelog-style format.
- Documented how roadmap phases, commits, and release tags align.

Commit: `5439bd2`
Tag: `v0.12.0`

#### 8.5 Add OpenAPI & Compatibility Gates

Status: Completed

Summary:
- Exposed `/v3/api-docs` and `/v3/api-docs.yaml` and documented the implemented controllers, schemas, pagination rules, and session-cookie auth requirements in the generated contract.
- Stored the approved contract baseline in `src/test/resources/openapi/approved-openapi.json`, added the refresh task, and enforced backward compatibility with dedicated integration coverage.
- Added GitHub Actions CI wiring so the compatibility gate now runs on pull requests and pushes to `main`.

Commit: `d582a38`
Tag: `v0.20.0`

#### 8.6 Architecture & Cache Hardening

Status: Completed

Summary:
- Removed entity-level eager fetching and moved the required fetch shape into repository methods through `@EntityGraph`.
- Replaced the previous simple in-memory cache manager with Caffeine-backed caches.
- Moved caching enablement into a dedicated configuration class under the `config` package and added hardening tests for the new conventions.

Commit: `806641f`
Tag: `v0.15.0`

### Phase 9: Testing & Quality

#### 9.1 Increase Test Coverage Target

Status: Completed

Summary:
- Added `jacocoCoverageSummary` so test runs print overall line coverage and the lowest-covered classes from the latest report.
- Added focused service and exception-path tests for `BookService`, `UserAccountService`, and `ApiExceptionHandler`.
- Enforced JaCoCo bundle coverage thresholds of 90% line coverage and 70% branch coverage and documented the workflow in the contributor-facing docs.

Commit: `210df88`
Tag: `v0.21.0`

#### 9.2 Add Load & Performance Testing

Status: Completed

Summary:
- Added Gatling scenarios for public book listing/search, localization lookup, and GitHub OAuth redirect startup.
- Added `scripts/run-phase-9-benchmarks.ps1` to run the local benchmark flow end to end and refresh `performance/baselines/phase-9-local.json`.
- Documented the tracked local baseline together with the regression rule for failed requests, success below 99%, and sustained p95/mean increases above 25%.

Commit: `8bf223a`
Tag: `v0.21.0`

### Pre-1.0 API & Naming Simplification

Status: Completed

Summary:
- Renamed the technical overview surface around `TechnicalOverviewController`, kept `/hello`, and added `/` as the public technical overview endpoint.
- Standardized user-area naming around `UserAccount*` and simplified the singleton authenticated-user API to `GET /api/account` and `PUT /api/account/language`.
- Renamed the localization slice from `LocalizationMessage*` to `Localization*` and flattened localization reads onto `/api/localizations` with `messageKey` and `language` query filters instead of special-case lookup paths.
- Updated generated docs, HTTP examples, OpenAPI baseline, performance scenarios, and supporting tests to match the simplified pre-`1.0` contract.

Commit: release commit tagged `v0.22.0`
Tag: `v0.22.0`

## Roadmap Maintenance Notes

- Keep active work in `TODO.md`.
- Move finished phases or milestones here once they are fully complete.
- Remove completed phases and completed checklist items from `TODO.md` once they are archived here.
- Preserve commit references and release tags when archiving completed work.
