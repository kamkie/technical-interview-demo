# Design Guide

`ai/DESIGN.md` captures intended product and contract direction for `technical-interview-demo`.

Use this file when the task changes user-visible behavior, supported scope, security posture, operational defaults, or roadmap tradeoffs.
Use `ai/ARCHITECTURE.md` for structural placement and `ai/DOCUMENTATION.md` for artifact routing. This file explains intent behind the contract; it is not the contract itself.

## Product Intent

This repository is a technical interview demo, not a general-purpose starter platform.

The design target is:

- small enough to understand in one sitting
- rich enough to show meaningful backend decisions
- realistic enough to demonstrate API, persistence, security, observability, and documentation practices

The stable line is an interview-demo reference app, not a promise to become a full production platform.

## Design Priorities

- clarity over cleverness
- explicit, inspectable behavior over hidden magic
- stable public behavior once it is published
- operational realism without platform sprawl
- demo readability over framework ambition

## Non-Goals

This repository is not trying to be:

- a generic enterprise framework
- a microservices example
- a cloud-provider reference architecture
- a frontend-heavy product
- an abstraction showcase

If a change pushes the app toward any of those, it needs strong justification.

## Supported Experience

The application intentionally combines:

- a small externally supported `/api/**` surface plus internal or deployment-scoped validation endpoints
- CRUD-style book, category, and localization behavior
- authenticated account behavior
- localized errors
- observability, generated docs, and compatibility gates

That mix is deliberate. It demonstrates request validation, filtering, pagination, optimistic locking, role-based authorization, session-backed authentication, localization, caching, metrics, audit logging, REST Docs, and OpenAPI in one compact codebase.

## Public Contract Direction

The supported API should remain:

- JSON-friendly
- easy to inspect manually
- stable once documented
- strict enough to show validation discipline

Current preferences:

- paginated collection endpoints
- explicit filter parameters rather than opaque query languages
- predictable `ProblemDetail`-based errors
- localized error metadata with `messageKey`, localized `message`, and `language`
- security rules that are visible in docs and tests

Avoid overloaded endpoints, inconsistent status handling, or letting persistence shape harden into the long-term public contract.

## Security And Deployment Direction

The security posture is pragmatic for a demo application:

- public reads where appropriate
- authenticated writes
- role-based restrictions for privileged operations
- persisted application users and roles
- session-backed behavior that is realistic enough to discuss in an interview

The supported surface is intentionally narrow:

- the externally supported application contract lives under `/api/**`
- `/`, `/hello`, `/docs`, `/v3/api-docs`, `/v3/api-docs.yaml`, `/actuator/health`, `/actuator/health/liveness`, `/actuator/health/readiness`, and `/actuator/info` remain internal or deployment validation paths
- `/actuator/prometheus` is a trusted-scrape technical surface, not part of the internet-public contract
- same-site browser-session bootstrap lives at `/api/session`, with provider-aware login entry points under `/api/session/oauth2/authorization/{registrationId}` and provider callbacks under `/api/session/login/oauth2/code/{registrationId}` when the optional `oauth` profile is active
- unsafe same-site browser writes use the supported CSRF contract: `GET /api/session` bootstraps the readable `XSRF-TOKEN` cookie and clients mirror it into `X-XSRF-TOKEN` when a real current session exists
- abuse protection for login bootstrap and write-heavy internet-public paths is primarily an edge or deployment concern

Preserve those decisions unless an explicit contract review changes them.

## Data, Localization, And Observability Direction

Keep the data model straightforward:

- PostgreSQL runtime
- Flyway-managed schema
- JPA persistence
- feature-local repositories

Keep localization as a product concern, not only a CRUD concern:

- request language detection
- user preferred-language fallback
- localized API errors

Keep observability built into the product:

- health, readiness, liveness, and info endpoints
- Prometheus metrics
- request tracing and request IDs
- explicit domain counters and gauges
- append-only audit logging for writes

Prefer a small number of meaningful metrics and logs over speculative observability complexity.

## Documentation Role Split

Keep documentation roles distinct:

- executable specs and published contract docs own behavior
- `README.md`, `CONTRIBUTING.md`, and `SETUP.md` own human workflow
- `AGENTS.md` and the focused `ai/` guides own AI workflow

## Roadmap Direction

Current design pressure should stay on:

- finishing and clarifying the existing surface
- hardening visible behavior
- improving delivery and operations

Prefer that over adding major new feature areas.

Longer-term framing remains:

- post-`1.x` evolution keeps browser-oriented auth and session flows instead of pivoting to bearer-token-only public APIs
- any first-party UI stays in a separate repository
- supported browser usage assumes one public origin through reverse-proxy deployment
- moving toward a more production-ready posture is an explicit post-`1.x` contract review, not a silent extension of the `1.x` promise

## Design Review Questions

Before finalizing a design change, ask:

- does this make the app easier or harder to explain in an interview?
- does it improve real behavior or only architecture aesthetics?
- does it preserve a compact and coherent supported surface?
- does it keep specs, docs, and implementation aligned?
- is this change finishing the demo, or inflating it?
