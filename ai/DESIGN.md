# Design Guide

`ai/DESIGN.md` captures the intended design direction for `technical-interview-demo`.

Use this file when making product-shaping decisions:

- API behavior
- feature scope
- security posture decisions
- user-visible errors
- operational defaults
- roadmap tradeoffs

This file explains the intent behind the contract. It is not the contract itself.
Use `ai/DOCUMENTATION.md` for owning-file selection and cross-document alignment.

## Product Intent

This application is a technical interview demo, not a general-purpose starter platform.

`1.0` means a stable interview-demo reference app.
It does not mean this repository is trying to become a production-ready starter.

The design target is:

- small enough to understand in one sitting
- rich enough to show meaningful backend decisions
- realistic enough to demonstrate API, persistence, security, observability, and documentation practices

The project should feel intentional, not oversized.

## Primary Design Goals

- clarity over cleverness
- explicit behavior over hidden magic
- spec-driven evolution over ad hoc changes
- public API stability once behavior is documented
- PostgreSQL-backed runtime realism without distributed-systems sprawl
- production-aware operational features without pretending to be a full platform product

## Non-Goals

This repository is not trying to be:

- a generic enterprise framework
- a microservices example
- a cloud-provider reference architecture
- a frontend-heavy product
- a best-possible-abstraction showcase

If a change pushes the app toward any of those, it needs strong justification.

## Supported Experience

The application currently supports:

- public technical overview and smoke-test endpoints
- CRUD-style book management
- category management
- localization management and localized error responses
- authenticated user profile behavior
- observability, generated docs, and compatibility gates

That mix is deliberate. It demonstrates several dimensions of backend work in one compact codebase:

- request validation
- filtering and pagination
- optimistic locking
- role-based authorization
- session-backed authentication
- localization
- caching
- metrics
- audit logging
- REST Docs and OpenAPI

## Design Principles

### 1. Small Surface, Real Behavior

Prefer a few endpoints with thoughtful behavior over many thin endpoints.
The app should show real tradeoffs such as filtering, fallback logic, authorization, and write auditing.

### 2. Spec First

Behavior should be designed through:

- integration tests
- REST Docs tests
- OpenAPI compatibility checks
- human-facing docs

If a design change cannot be expressed clearly in those artifacts, it is probably not ready.

### 3. Thin Adapters, Concrete Services

Controllers should stay HTTP-focused.
Services should contain the real behavioral decisions.
Repositories should stay narrow and persistence-focused.

### 4. Demo Readability Beats Architectural Fashion

A direct implementation is usually preferred to:

- a generic base class
- an internal platform layer
- a factory or strategy hierarchy for a single use case

Design consistency matters, but readability matters more.

### 5. Operational Features Are Part Of The Design

Caching, metrics, tracing, health endpoints, and audit logging are not afterthoughts.
They are part of the product being demonstrated.

## API Design Direction

The API should remain:

- JSON-friendly
- easy to inspect manually
- stable once documented
- strict enough to show validation discipline

Current design preferences:

- paginated collection endpoints
- explicit filter parameters rather than opaque query languages
- predictable error responses using `ProblemDetail`
- localized error metadata with `messageKey`, localized `message`, and `language`
- authentication and authorization rules that are visible in docs and tests

Avoid:

- overloaded endpoints with many special modes
- persistence leakage becoming the long-term public contract
- inconsistent status-code behavior across similar operations

## Error Design

Errors are a designed user surface, not incidental exception output.

Current desired properties:

- correct HTTP status
- stable, meaningful titles and details
- localization metadata
- consistent client behavior across validation, not-found, conflict, and forbidden cases

If you change error handling, treat it as both a design and contract change.

## Security Design

The design goal is pragmatic security for a demo application:

- public reads where appropriate
- authenticated writes
- role-based restrictions for privileged operations
- persisted application users and roles
- session-backed behavior realistic enough to discuss in an interview

The `1.x` contract is intentionally narrow:

- supported business and documentation endpoints include `/`, `/hello`, `/docs`, `/v3/api-docs`, `/v3/api-docs.yaml`, `/api/books...`, `/api/categories`, `/api/localizations...`, and `/api/account...`
- supported operational endpoints include `/actuator/health`, `/actuator/health/liveness`, `/actuator/health/readiness`, and `/actuator/info`
- `/actuator/prometheus` is deployment-scoped technical surface for trusted scraping, not part of the internet-public contract
- `/oauth2/authorization/github` is a technical login bootstrap path available only when the optional `oauth` profile is active
- CSRF stays disabled for `1.0` browser-session writes as a deliberate demo tradeoff for reviewer-oriented session workflows

Preserve those decisions unless an explicit follow-up contract review changes them.

## Data Design

Data should stay straightforward:

- relational storage in PostgreSQL
- Flyway-managed schema
- JPA for persistence mapping
- feature-local repositories

One important cleanup direction remains deliberate and explicit:

- avoid letting JPA entity shape become the permanent public API contract

## Localization Design

Localization is not limited to a dedicated CRUD feature. It also shapes:

- request language detection
- user preferred-language fallback
- localized API errors

Review localization changes as product changes, not only data-model edits.

## Observability Design

The app should be easy to inspect in development and credible in deployment:

- health, readiness, and liveness endpoints
- Prometheus metrics
- request tracing and request IDs
- explicit domain counters and gauges
- append-only audit logging for write operations

Prefer a small number of meaningful metrics and logs over speculative observability complexity.

## Documentation Design

Documentation should stay role-distinct:

- contract and reviewer-facing behavior lives in executable specs and published contract docs
- human maintainer workflow lives in `README.md`, `CONTRIBUTING.md`, and `SETUP.md`
- AI workflow and focused standing guidance live in `AGENTS.md` and the owning `ai/` guides

Do not collapse those roles into one meta-document.

## Roadmap Design Direction

Current roadmap pressure points are sensible:

- preserve the frozen `1.0` contract
- tighten production posture
- add deployment and CI/CD assets
- keep optional future scope clearly deferred

That means design work should prefer:

- finishing and clarifying the existing surface
- hardening visible behavior
- improving delivery and operations

over adding major new features.

## Good Design Changes

Usually good:

- clarifying a request or response contract
- making error behavior more consistent
- reducing persistence leakage in the public API
- adding targeted metrics or audit entries
- documenting real runtime expectations
- adding deployment assets that stay readable and vendor-neutral

Usually bad:

- introducing new feature areas with weak justification
- adding framework-heavy indirection to a small codebase
- letting docs drift from behavior
- reopening locked `1.0` decisions without an explicit contract review
- making production assumptions that the documented demo posture does not support

## Design Review Questions

Before finalizing a design change, ask:

- does this make the app easier or harder to explain in an interview?
- does it improve real behavior or only architecture aesthetics?
- does it preserve a compact and coherent public surface?
- does it keep specs, docs, and implementation aligned?
- is this change finishing the demo, or inflating it?
