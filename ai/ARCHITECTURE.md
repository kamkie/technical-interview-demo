# Architecture Guide

`ai/ARCHITECTURE.md` is the AI-facing architecture map for `technical-interview-demo`.

Use this file to understand how the codebase is organized before making structural changes.
This file is descriptive, not authoritative. Behavioral truth still lives in the spec artifacts described in `AGENTS.md`.
Use `ai/CODE_STYLE.md` for edit-shaping rules, `ai/TESTING.md` for validation scope, and `ai/DOCUMENTATION.md` for contract-artifact alignment.
Use `ai/BUSINESS_MODULES.md` for the detailed business-feature package map.

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

## API Shape

Current implemented endpoint surface:

- `GET /docs`
- `GET /`
- `GET /hello`
- `GET|POST|PUT|DELETE /api/books...`
- `GET|POST /api/categories`
- `GET|POST|PUT|DELETE /api/localizations...`
- `GET|PUT /api/account...`
- `GET /api/operator/surface`
- actuator health, info, and Prometheus endpoints
- OpenAPI docs at `/v3/api-docs` and `/v3/api-docs.yaml`

Key API-shape expectations:

- `GET /api/books` is paginated and filterable
- `GET /api/localizations` is paginated and supports optional exact `messageKey` and `language` filters
- account endpoints require an authenticated session
- category creation and localization writes require `ADMIN`
- localized errors include `messageKey`, localized `message`, and resolved `language`
- pageable collection endpoints currently expose Spring Data-style page metadata as part of the reviewed `2.0` contract; replacing that shape with repo-owned pagination envelopes is not selected cleanup work unless an explicit contract review chooses it

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

See `ai/BUSINESS_MODULES.md` for the detailed feature-package map and ownership notes.

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

- `technical.operator`
  - ADMIN-only operator inspection endpoint that combines recent audit history, runtime diagnostics, and operational status links

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

## Deployment Boundary

Current deployment-boundary expectations:

- only `/api/**` is intended to be internet-reachable, typically through `waf -> frontend -> this application`
- `/`, `/hello`, `/docs`, OpenAPI docs, and actuator endpoints exist but are internal or deployment-scoped surfaces rather than public app contract
- authenticated operator visibility stays in `/api/operator/surface`; actuator endpoints are not the application-auth operational API
- abuse protection for login bootstrap and write-heavy internet-public paths is primarily an edge or deployment responsibility rather than default in-app rate limiting

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
- service logging aspect for application-service visibility
- actuator endpoints for health, readiness, liveness, info, and Prometheus scraping
- write-operation logging and audit logging

The current `2.0` direction keeps that observability shape in place.
Reducing or redesigning service-call logging and correlation fields is not selected cleanup work unless operational evidence shows the current signal-to-noise ratio is a real problem.

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
- do not reshape pageable API responses or observability logging only as stylistic cleanup; treat both as deliberate contract or operations decisions
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
