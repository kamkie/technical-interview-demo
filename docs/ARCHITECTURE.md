# Architecture Guide

`docs/ARCHITECTURE.md` is the compact shared architecture map for `technical-interview-demo`.
The detailed architecture reference lives on demand in `ai/references/ARCHITECTURE_DETAILED_MAP.md`.
This file owns package shape, feature ownership, and structural guidance.

This file is descriptive, not authoritative.
Behavioral truth lives in the spec artifacts described by `AGENTS.md`.

Entry points:

- structural code reading only: read `## System Purpose` and `## Package Shape`
- architecture-sensitive edits: continue through the descriptive map, then use `## Change Guidance` and later rule sections

## System Purpose

This repository is a small Spring Boot demo application for interview exercises.
The architecture goal is to keep the codebase small, readable, and easy to reason about.
Prefer direct feature-local code over generic internal frameworks unless repeated complexity justifies an abstraction.

Current scope includes:

- public overview, docs, and smoke-test endpoints
- book, category, localization, authenticated account, and ADMIN operator APIs
- OAuth 2.0 protected writes with JDBC-backed HTTP sessions
- generated REST Docs, approved OpenAPI compatibility, and HTTP examples
- PostgreSQL runtime profiles and Testcontainers-backed integration tests
- audit logging, tracing, caches, metrics, deployment manifests, and tracked benchmark baselines

## Package Shape

Code lives under `team.jit.technicalinterviewdemo`:

- `business.*`: feature and domain behavior
- `technical.*`: cross-cutting platform concerns and framework-facing support
- root application entry point: Spring Boot bootstrap only

Practical rules:

- controllers are thin HTTP adapters
- services own non-trivial behavior and orchestration
- repositories own persistence access
- request and response types stay close to their feature package
- technical packages provide repo-specific infrastructure, not a generic framework
- public controllers use response DTOs rather than exposing JPA entities directly

## Business Feature Ownership

The business layer is organized by feature, not by technical role.
Each feature package should keep its controller, service, repository, entities, request and response types, and feature-local exceptions close together unless the concern is clearly cross-cutting.

- `business.book`: owns book pagination, filtering, optimistic locking, category assignment, and audit logging.
- `business.category`: owns category creation, list ordering, cache eviction, and admin-only write control.
- `business.localization`: owns localized message lookup, fallback behavior, filtering, write authorization, supported-language policy, seed support, and cache eviction.
- `business.user`: owns persisted user profile data, preferred-language updates, role state, and synchronization of authenticated users into application state.
- `business.audit`: owns append-only write auditing for feature services.

## Important Boundaries

- Infrastructure assets live under `infra/`.
- Build and security policy files live under `tooling/`.
- Setup and tool walkthroughs live in `SETUP.md`, not architecture notes.
- Contract and documentation routing lives in `ai/DOCUMENTATION.md`.
- Validation expectations live in `ai/TESTING.md`.

## API Shape

Current endpoint families include:

- `GET /`, `GET /hello`, `GET /docs`
- `/api/books`
- `/api/categories`
- `/api/localizations`
- `/api/account`
- `/api/admin/audit-logs`
- `/api/admin/operator-surface`
- `/api/admin/users`
- actuator health, info, and Prometheus endpoints
- OpenAPI docs at `/v3/api-docs` and `/v3/api-docs.yaml`

Deployment-boundary expectations:

- only `/api/**` is intended to be internet-reachable, typically through `waf -> frontend -> this application`
- `/`, `/hello`, `/docs`, OpenAPI docs, and actuator endpoints are internal or deployment-scoped surfaces
- authenticated operator visibility stays under `/api/admin/**`
- edge or deployment layers own abuse protection for login bootstrap and write-heavy internet-public paths

## Architectural Flow

Typical request flow:

1. Spring MVC receives the request.
2. Technical filters resolve tracing and request language.
3. Spring Security applies session and authorization rules.
4. The controller binds HTTP input and delegates to a service.
5. The service validates feature rules, calls repositories or other services, updates metrics, writes audit logs, and logs state changes.
6. `technical.api.ApiExceptionHandler` normalizes errors.
7. The controller returns JSON-friendly payloads or `ProblemDetail`.

Write flows also include authentication, role checks, audit creation, cache eviction, and success logging.

## Core Models

- Persistence uses Spring Data JPA, PostgreSQL, Flyway, and Testcontainers-backed integration tests.
- App-owned persisted timestamps use Java `Instant` and PostgreSQL `timestamptz`.
- Security is session-based and optionally OAuth-enabled; external identity is synchronized into persisted `UserAccount` state.
- Localization is both a business feature and a cross-cutting error-response concern.
- Observability is built in through Micrometer, tracing, request logging, service logging, actuator endpoints, and audit logs.
- Category and localization caches use named Caffeine caches with service-owned write eviction.

## Change Guidance

Usually safe:

- adding focused service methods inside existing feature packages
- adding feature-level request validation
- adding integration tests for current behavior
- adding metrics or audit logging for a new write path
- adding a small helper inside an existing package

Usually risky:

- introducing generic base controllers, services, repositories, or shared abstraction packages
- changing security defaults without specs and docs
- changing error shape without treating it as a public contract update
- reshaping pageable API responses as stylistic cleanup
- reducing observability logging or correlation fields without operational evidence

Before structural changes, ask whether the change reduces concepts, keeps behavior near the owning feature, preserves public contract artifacts, and remains readable for a demo audience.

Before moving code across business packages, ask:

- which feature actually owns the behavior?
- would the move make the public or test-facing behavior harder to trace?
- is this a real cross-cutting concern, or shared code that still belongs to one feature?
- is a new abstraction solving repeated repo reality, or only style preference?
