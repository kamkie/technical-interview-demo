# Architecture Guide

`ai/ARCHITECTURE.md` is the AI-facing architecture map for `technical-interview-demo`.

Use this file to understand how the codebase is organized before making structural changes.
This file is descriptive, not authoritative. Behavioral truth still lives in the spec artifacts described in `AGENTS.md`.
Use `ai/CODE_STYLE.md` for edit-shaping rules, `ai/TESTING.md` for validation scope, and `ai/DOCUMENTATION.md` for contract-artifact alignment.

## System Purpose

This repository is a small Spring Boot demo application for interview exercises.
It is intentionally narrow:

- public read APIs for books, categories, localization data, technical overview, and generated docs
- authenticated account APIs
- OAuth-backed session security for state-changing actions
- append-only audit logging for writes
- metrics, tracing, caching, and OpenAPI/REST Docs support

Primary architecture goal:

- keep the codebase small, readable, and easy to reason about

That goal matters more than layering purity or abstraction density.

## Top-Level Shape

The codebase is organized under `team.jit.technicalinterviewdemo`:

- `business.*`
  - application features and domain behavior
- `technical.*`
  - cross-cutting platform concerns and framework-facing support code
- root application entry point
  - Spring Boot bootstrap only

The practical split is:

- controllers are thin HTTP adapters
- services own non-trivial behavior and orchestration
- repositories own persistence access
- entities and request/response types stay close to their feature package
- technical packages provide shared infrastructure without becoming a generic framework

## Module Map

### Root

- `TechnicalInterviewDemoApplication`
  - Spring Boot entry point only

### Business Modules

- `business.book`
  - `BookController`
  - `BookService`
  - `BookRepository`
  - `Book`
  - request objects, search support, and domain exceptions
  - owns pagination, filtering, optimistic locking, category assignment, and audit logging for books

- `business.category`
  - `CategoryController`
  - `CategoryService`
  - `CategoryRepository`
  - `Category`
  - request object and startup seed initializer
  - owns category creation, list ordering, cache eviction, and admin-only write control

- `business.localization`
  - `LocalizationController`
  - `LocalizationService`
  - `LocalizationRepository`
  - `Localization`
  - request/response types, supported language policy, seed support, and domain exceptions
  - owns localized message lookup, fallback behavior, filtering, write authorization, and cache eviction

- `business.user`
  - `UserAccountController`
  - `UserAccountService`
  - `CurrentUserAccountService`
  - `UserAccountRepository`
  - `UserAccount`
  - request/response types and role enum
  - owns persisted user profile data, preferred-language updates, and synchronization of authenticated users into application state

- `business.audit`
  - `AuditLogService`
  - `AuditLogRepository`
  - `AuditLog`
  - enums for action and target type
  - owns append-only write auditing for feature services

### Technical Modules

- `technical.api`
  - global exception handling and shared request-validation exceptions
  - centralizes `ProblemDetail` creation and localized error response shape

- `technical.security`
  - Spring Security filter chain
  - session policy
  - authenticated-user synchronization filter
  - bridges external OAuth identity to persisted app users

- `technical.localization`
  - request-language resolution and request-scoped language context
  - used by the localization service and API error handling

- `technical.cache`
  - cache names and Caffeine cache manager configuration

- `technical.metrics`
  - application-specific counters and gauges

- `technical.logging`
  - request tracing and request-logging filter
  - service logging aspect
  - sensitive parameter sanitization

- `technical.docs`
  - OpenAPI configuration
  - generated documentation web configuration

- `technical.info`
  - root technical overview endpoint and smoke-test `hello` endpoint

## Architectural Flow

Typical request flow:

1. request enters Spring MVC
2. technical filters resolve tracing and request language
3. Spring Security applies session and authorization rules
4. controller binds HTTP request into request objects and delegates to a service
5. service validates feature-level rules, calls repositories and other services, updates metrics, writes audit logs, and logs state changes
6. exceptions are normalized by `technical.api.ApiExceptionHandler`
7. responses are returned as JSON-friendly payloads or `ProblemDetail`

Write flow adds:

- authentication and role checks
- audit log creation
- cache eviction where relevant
- success logging for database-changing operations

## Dependency Direction

Preferred dependency direction:

- `technical.*` may depend on feature services or repositories only when the concern is clearly cross-cutting and repo-specific
- `business.*` may depend on other business services when the dependency is concrete and small
- controllers depend on services, not repositories
- repositories should not depend on services
- avoid introducing extra shared layers unless reuse is real and local

This codebase intentionally tolerates direct feature-to-feature service calls when they keep the design simpler.
Example: `BookService` delegates category resolution to `CategoryService` rather than introducing an artificial coordination layer.

## Persistence Model

Persistence uses:

- Spring Data JPA repositories
- PostgreSQL in runtime profiles
- Flyway for schema migration
- Testcontainers-backed PostgreSQL in integration tests

Current persistence style:

- public controllers use feature-local response DTOs instead of exposing JPA entities directly
- request and response types stay feature-local so public contract shaping remains explicit and reviewable
- repositories stay feature-local instead of being abstracted behind extra data-access layers

## Security Model

Security is session-based and optionally OAuth-enabled:

- public reads for most GET endpoints
- authenticated session required for account endpoints and writes
- `ADMIN` required for category writes and localization writes
- OAuth login enabled only when a client registration exists
- authenticated users are synchronized into the app database

Important architectural consequence:

- external identity is not the whole security model
- application roles and preferred-language behavior depend on persisted `UserAccount` state

## Localization Model

Localization is a first-class feature and a cross-cutting concern:

- feature data exists under `business.localization`
- request language is resolved under `technical.localization`
- API errors are localized by the global exception handler
- authenticated-user preferred language participates in fallback behavior

This means localization changes often touch both business and technical packages.

## Observability Model

Observability is built into the main app rather than bolted on later:

- Micrometer application counters and gauges
- tracing and request-logging filter for request correlation
- actuator endpoints for health, readiness, liveness, info, and Prometheus scraping
- write-operation logging and audit logging

## Caching Model

Caching is explicit and small:

- categories and localization lookups use named Caffeine caches
- cache behavior is part of tested technical behavior
- cache eviction happens in services on writes

## Documentation And Spec Model

Published and executable specs are part of the delivery architecture:

- integration tests define runtime behavior
- REST Docs tests define public documentation snippets
- OpenAPI compatibility tests gate backward compatibility
- HTTP example files support reviewability

This is not optional documentation around the code. It is part of the delivered system.

## Test Architecture

Tests are intentionally strong relative to app size:

- feature integration tests for API behavior
- REST Docs tests for public documentation
- OpenAPI compatibility tests for machine-readable contract stability
- technical integration tests for caching, metrics, tracing, security, and error handling
- targeted service tests for validation-heavy logic
- shared test infrastructure under `src/test/java/.../testing`
- test data helpers under `src/test/java/.../testdata`

When changing architecture-sensitive behavior, read the technical tests first.

## Architecture Implications For Changes

When editing structure:

- preserve the demo nature of the app and prefer direct feature-local code over repo-internal frameworks
- keep business behavior near the owning feature package unless the concern is genuinely cross-cutting
- treat public response-shape changes as contract changes that must flow through specs and docs
- do not move setup or workflow process detail into architecture notes

## Common Safe Changes

Usually safe:

- adding a focused service method inside an existing feature package
- adding request validation in a service
- adding integration tests for current behavior
- adding metrics or audit logging for a new write path
- adding a small helper inside an existing package

Usually risky:

- introducing generic base controllers, services, or repositories
- creating a shared abstractions package to clean up a small codebase
- changing security defaults without updating tests and public docs
- changing error shape without treating it as a contract update
- moving public behavior into hidden framework configuration

## Architecture Review Questions

Before making structural changes, ask:

- does this reduce or increase the number of concepts in the repo?
- does it keep behavior near the owning feature package?
- is the change reflected in tests and docs if it affects public behavior?
- does it preserve readability for an interview and demo audience?
- is a new abstraction solving a real repeated problem, or only satisfying style preference?
