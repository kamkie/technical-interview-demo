# Design Guide

`ai/DESIGN.md` captures the intended design direction for `technical-interview-demo`.

Use this file when making product-shaping decisions:

- API behavior
- feature scope
- security posture decisions
- user-visible errors
- operational defaults
- roadmap tradeoffs

This file is not the public contract. It explains the intended design logic behind that contract.

## Product Intent

This application is a technical interview demo, not a general-purpose starter platform.

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
- a “best possible abstraction” showcase

If a change pushes the app toward any of those, it needs strong justification.

## Supported Experience

The application currently supports:

- public technical overview and smoke-test endpoints
- CRUD-style book management
- category management
- localization management and localized error responses
- authenticated user profile behavior
- observability, generated docs, and compatibility gates

That mix is deliberate.
It demonstrates several dimensions of backend work in one compact codebase:

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
- a factory/strategy hierarchy for a single use case

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

If you change error handling, treat it as a design and contract change.

## Security Design

The design goal is pragmatic security for a demo application:

- public reads where appropriate
- authenticated writes
- role-based restrictions for privileged operations
- persisted application users and roles
- session-backed behavior realistic enough to discuss in an interview

The design is intentionally not finished.
Pre-`1.0` work still needs to settle:

- final `401` and `403` response consistency
- whether browser-session writes need CSRF reintroduction
- whether `/actuator/prometheus` remains public in production
- how strictly `prod` fails on missing secrets and credentials

Agents should not accidentally freeze those open decisions by quietly coding around them.

## Data Design

Data should stay straightforward:

- relational storage in PostgreSQL
- Flyway-managed schema
- JPA for persistence mapping
- feature-local repositories

The design tolerates some demo-friendly shortcuts today, but the roadmap already identifies one important cleanup direction:

- avoid letting JPA entity shape become the permanent public API contract

That should be addressed deliberately, not by scattered ad hoc DTO churn.

## Localization Design

Localization is not limited to a dedicated CRUD feature.
It also shapes platform behavior:

- request language detection
- user preferred-language fallback
- localized API errors

Design consequence:

- localization changes should be reviewed as product changes, not just data-model edits

## Observability Design

The app should be easy to inspect in development and credible in deployment:

- health, readiness, and liveness endpoints
- Prometheus metrics
- request tracing and request IDs
- explicit domain counters and gauges
- append-only audit logging for write operations

Design preference:

- add a small number of meaningful metrics and logs
- avoid speculative observability complexity

## Documentation Design

Documentation should match the role of each file:

- `README.md`
  - supported contract and high-level project description
- `SETUP.md`
  - onboarding, environment, and troubleshooting
- `AGENTS.md`
  - repo rules for AI agents
- `ai/PLAN.md`
  - how AI agents should plan work
- `ai/ARCHITECTURE.md`
  - structural map of the codebase
- `ai/DESIGN.md`
  - this design-intent document
- `ai/LEARNINGS.md`
  - durable repo-wide engineering guidance that should survive refactors
- `ROADMAP.md`
  - active roadmap only

Do not collapse those roles into one giant meta-document.

## Roadmap Design Direction

Current roadmap pressure points are sensible:

- lock the `1.0` contract
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

- clarifying a request/response contract
- making error behavior more consistent
- reducing persistence leakage in the public API
- adding targeted metrics or audit entries
- documenting real runtime expectations
- adding deployment assets that stay readable and vendor-neutral

Usually bad:

- introducing new feature areas with weak justification
- adding framework-heavy indirection to a small codebase
- letting docs drift from behavior
- pushing roadmap work ahead of unresolved `1.0` decisions
- making production assumptions that the roadmap still marks as open

## Design Review Questions

Before finalizing a design change, ask:

- does this make the app easier or harder to explain in an interview?
- does it improve real behavior or only architecture aesthetics?
- does it preserve a compact and coherent public surface?
- does it keep specs, docs, and implementation aligned?
- is this change finishing the demo, or inflating it?
