# Design Guide

`docs/DESIGN.md` captures intended product and contract direction for `technical-interview-demo`.

Use this file when the task changes user-visible behavior, supported scope, security posture, operational defaults, or roadmap tradeoffs.
Use `.agents/references/architecture.md` for AI structural placement and `.agents/references/documentation.md` for artifact routing. This file explains intent behind the contract; it is not the contract itself.

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
- a host for a first-party UI; any first-party UI stays in a separate repository
- an abstraction showcase

If a change pushes the app toward any of those, it needs strong justification.

## Supported Experience

The application intentionally combines:

- a small externally supported `/api/**` surface plus internal or deployment-scoped validation endpoints (see `## Security And Deployment Direction` for the explicit path list)
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

These preferences are binding once an endpoint is published. Avoid overloaded endpoints, inconsistent status handling, or letting persistence shape harden into the long-term public contract.

## Release Phase Direction

Current release-phase intent is explicit:

- breaking published behavior is out of bounds on the current stable line unless a deliberate next-major-version decision changes that policy
- prefer alignment, maintenance, and bug-fix work over further contract reshaping
- release work starts only after intended implementation is integrated on `main` and the release preconditions are met

Current release phase, breaking-change policy, and next target version live in `ROADMAP.md` `## Current Project State`; `DESIGN.md` owns the intent, `ROADMAP.md` owns the version-specific facts.

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

The same supported `/api/**` surface is enumerated in `docs/FRONTEND_AI_CONTRACT.md` for first-party frontend integration; both files must move together when the contract changes.

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
- `README.md`, `CONTRIBUTING.md`, and `SETUP.md` own the project summary, contributor workflow, and local setup path
- `docs/README.md` owns the human-facing documentation index
- `docs/DEVELOPMENT_LIFECYCLE.md` owns the human-facing lifecycle and artifact-routing summary
- `docs/OPERATIONS.md` owns deployment and runtime operations runbooks
- `ROADMAP.md` owns active and planned work; `CHANGELOG.md` owns released history
- `docs/decisions/`, `docs/requirements/`, and `docs/specs/` own durable decisions, product intent, and standalone behavior specs per `docs/decisions/0001-adopt-pre-planning-artifacts.md`
- `docs/WORKING_WITH_AI.md` owns the human-facing AI guide; root `WORKING_WITH_AI.md` is only a compatibility pointer
- `AGENTS.md` and the focused `.agents/references/` guides own AI workflow

See `.agents/references/documentation.md` for the full ownership map and routing rules.

## Roadmap Direction

Lifecycle phase names follow `.agents/references/application-lifecycle.md`; pre-planning artifact roles (ADR, PRD, spec, plan) follow `docs/decisions/0001-adopt-pre-planning-artifacts.md`.

Current design pressure should stay on:

- finishing and clarifying the existing surface
- hardening visible behavior
- improving delivery and operations

Prefer that over adding major new feature areas.

Longer-term framing remains:

- evolution beyond the current stable line keeps browser-oriented auth and session flows instead of pivoting to bearer-token-only public APIs
- any first-party UI stays in a separate repository
- supported browser usage assumes one public origin through reverse-proxy deployment
- moving toward a more production-ready posture is an explicit next-major-version contract review, not a silent extension of the current stable promise

## Design Review Questions

Before finalizing a design change, ask:

- does this make the app easier or harder to explain in an interview?
- does it improve real behavior or only architecture aesthetics?
- does it preserve a compact and coherent supported surface?
- does it keep specs, docs, and implementation aligned?
- is this change finishing the demo, or inflating it?
