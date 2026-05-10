# Architecture Reference For AI Agents

`.agents/references/architecture.md` owns AI-facing architecture guidance for `technical-interview-demo`: the current codebase map, package ownership, structural placement rules, and architecture-sensitive change prompts.

This file is descriptive, not authoritative.
Behavioral truth lives in the spec artifacts described by `AGENTS.md`.
Use `.agents/references/code-style.md` for edit shape, `.agents/references/testing.md` for validation scope, and `.agents/references/documentation.md` for contract-artifact routing.

Entry points:

- orientation or structural code reading: read `## Purpose And Constraints`, `## System Shape`, and `## Feature Ownership`
- placement, package ownership, or architecture-sensitive edits: continue through `## Flow And Boundaries` and `## Change Guidance`
- security, deployment, localization, observability, cache, or public-contract changes: also read the relevant `## Detailed Maps` subsection

## Purpose And Constraints

This repository is a small Spring Boot demo application for interview exercises.
The architecture should keep the codebase small, readable, and easy to reason about while still showing realistic backend concerns.

Primary constraints:

- preserve the demo character of the project
- keep behavior close to the feature that owns it
- prefer direct feature-local code over repo-internal frameworks
- make public behavior reviewable through executable specs, generated docs, and checked contract baselines
- keep operational concerns visible without turning the app into a platform

Layering purity and abstraction density are secondary.
Add abstraction only when repeated repo reality justifies it.

## System Shape

Current scope includes public overview and smoke-test endpoints; book, category, localization, account, and ADMIN operator APIs; OAuth 2.0 protected writes with JDBC-backed HTTP sessions; generated REST Docs and OpenAPI compatibility; PostgreSQL runtime profiles; Testcontainers-backed integration tests; audit logging; tracing; caches; metrics; deployment manifests; and benchmark baselines.

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

Current endpoint families:

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

## Feature Ownership

The business layer is organized by feature, not by technical role.
Keep each feature's controller, service, repository, entities, request and response types, and feature-local exceptions close together unless the concern is clearly cross-cutting.

- `business.book`: book pagination, filtering, optimistic locking, category assignment, and audit logging.
- `business.category`: category creation, list ordering, cache eviction, and admin-only write control.
- `business.localization`: localized message lookup, fallback behavior, filtering, write authorization, supported-language policy, seed support, and cache eviction.
- `business.user`: persisted user profile data, preferred-language updates, role state, and synchronization of authenticated users into application state.
- `business.audit`: append-only write auditing for feature services.

## Flow And Boundaries

Typical request flow:

1. Spring MVC receives the request.
2. Technical filters resolve tracing and request language.
3. Spring Security applies session and authorization rules.
4. The controller binds HTTP input and delegates to a service.
5. The service validates feature rules, calls repositories or other services, updates metrics, writes audit logs, and logs state changes.
6. `technical.api.ApiExceptionHandler` normalizes errors.
7. The controller returns JSON-friendly payloads or `ProblemDetail`.

Write flows also include authentication, role checks, audit creation, cache eviction, and success logging.

Dependency direction:

- controllers depend on services, not repositories
- repositories should not depend on services
- `business.*` may depend on other business services when the dependency is concrete and small
- `technical.*` may depend on feature services or repositories only when the concern is clearly cross-cutting and repo-specific
- direct feature-to-feature service calls are acceptable when they keep the design simpler

State and platform boundaries:

- persistence uses Spring Data JPA repositories, PostgreSQL runtime profiles, Flyway migrations, and Testcontainers-backed PostgreSQL integration tests
- app-owned persisted timestamps use Java `Instant` and PostgreSQL `timestamptz`
- security is session-based and optionally OAuth-enabled; application roles and preferred-language behavior depend on persisted `UserAccount` state
- localization is both a business feature and a cross-cutting error-response concern
- observability is built into the main app through Micrometer, tracing, request logging, service logging, actuator endpoints, and audit logs
- category and localization caches use named Caffeine caches with service-owned write eviction

## Detailed Maps

### Technical Modules

- `technical.api`: global exception handling, shared request-validation exceptions, `ProblemDetail` creation, and localized error response shape
- `technical.security`: Spring Security filter chain, session policy, authenticated-user synchronization filter, and OAuth identity bridge
- `technical.localization`: request-language resolution and request-scoped language context
- `technical.cache`: cache names and Caffeine cache manager configuration
- `technical.metrics`: application-specific counters and gauges
- `technical.logging`: request tracing, request logging, service logging, and sensitive parameter sanitization
- `technical.docs`: OpenAPI configuration and generated documentation web configuration
- `technical.info`: root technical overview endpoint and smoke-test `hello` endpoint
- `technical.operator`: ADMIN-only operator inspection endpoint for recent audit history, runtime diagnostics, and operational status links

### Deployment And Tooling

- only `/api/**` is intended to be internet-reachable, typically through `waf -> frontend -> this application`
- `/`, `/hello`, `/docs`, OpenAPI docs, and actuator endpoints are internal or deployment-scoped surfaces
- authenticated operator visibility stays under `/api/admin/**`
- edge or deployment layers own abuse protection for login bootstrap and write-heavy internet-public paths
- `infra/` owns checked-in Kubernetes, Helm, monitoring, edge, and log-forwarding assets
- `tooling/` owns PMD, SpotBugs, Trivy, and similar repo verification policy files
- `SETUP.md` owns local setup, tool walkthroughs, and local troubleshooting; `docs/OPERATIONS.md` owns deployment and runtime runbooks

### Public Contract Notes

- `GET /api/books` is paginated and filterable
- `GET /api/localizations` is paginated and supports optional exact `messageKey` and `language` filters
- account endpoints require an authenticated session
- category creation and localization writes require `ADMIN`
- localized errors include `messageKey`, localized `message`, and resolved `language`
- app-owned persisted timestamp fields exposed through the API serialize as UTC instants with a trailing `Z`
- pageable collection endpoints currently expose Spring Data-style page metadata as part of the reviewed `2.0` contract

Do not replace pageable response shape with repo-owned pagination envelopes as cleanup.
That is a public contract decision.

### Spec And Test Architecture

Published and executable specs are part of the delivery architecture:

- integration tests define runtime behavior
- REST Docs tests define public documentation snippets
- OpenAPI compatibility tests gate backward compatibility
- HTTP convenience files support manual reviewability
- `README.md`, `src/docs/asciidoc/`, and `src/test/resources/openapi/approved-openapi.json` move with public behavior
- technical integration tests cover caching, metrics, tracing, security, and error handling

When changing architecture-sensitive behavior, read the relevant technical or feature tests first.

## Change Guidance

Usually safe:

- adding a focused service method inside an existing feature package
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
- moving public behavior into hidden framework configuration

Before structural changes, ask:

- does this reduce or increase the number of concepts in the repo?
- which feature actually owns the behavior?
- does the change keep behavior near the owning feature package?
- is this a real cross-cutting concern, or shared code that still belongs to one feature?
- would the move make public or test-facing behavior harder to trace?
- is a new abstraction solving repeated repo reality, or only style preference?
- does it preserve readability for an interview and demo audience?
