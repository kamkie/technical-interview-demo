# Project TODO & Roadmap

This file tracks active and upcoming work for the technical-interview-demo Spring Boot application.
Completed work has been moved to `COMPLETED_TASKS.md` so this file stays focused on execution order and next steps.

## High-Level Roadmap

The roadmap below is ordered to respect cross-phase dependencies.

| Order | Theme | Status | Why it comes next |
| --- | --- | --- | --- |
| 1 | Phase 4: Finish language negotiation | In Progress | Builds directly on the completed localized error-response foundation |
| 2 | Phase 5: Security and user model | Ready | Security should be in place before admin-only management and audit trails |
| 3 | Phase 6.2: Book categories/tags | Ready | Extends the existing Book API after search/filtering is already complete |
| 4 | Phase 7: Caching and application metrics | Blocked by 6.2 for category caching | Best added after the next major read-model features settle |
| 5 | Phase 8.1: API and operations documentation gaps | Partially blocked by Phases 4 and 5 | Documentation should follow the feature set that actually exists |
| 6 | Phase 9: Contract, coverage, and performance testing | Ready after core APIs stabilize | Better value once the near-term API and auth work are settled |
| 7 | Phase 10: CI/CD and deployment assets | Ready | Depends mostly on the current quality gates and stable build outputs |
| 8 | Phase 11: Optional future enhancements | Deferred | These are stretch items after the core demo is complete |

## Current Priorities

1. Finish Phase 4.2 language negotiation details: supported-language validation, optional request-scoped context, and cookie fallback.
2. Close Phase 8.1 language-negotiation documentation after the remaining Phase 4.2 behavior is complete.
3. Start Phase 5.1 Spring Security and OAuth 2.0 with a demo-friendly provider.
4. Add Phase 5.2 user persistence and role handling.
5. Add Phase 5.3 audit logging for state-changing operations.

## Active Detailed Plan

### Phase 4: Error Response Localization

Status: In progress

Completed in archive:
- 4.1 Integrate Localization into Exception Handler

#### 4.2 Add Language Negotiation

Depends on: 4.1

Goal:
Allow clients to request localized error responses using browser-compatible preference negotiation and explicit overrides.

Remaining tasks:
- [ ] Add cookie `language` as fallback after `lang` and `Accept-Language`
- [ ] Add user preference fallback if authentication is introduced later
- [ ] Store the resolved language in request context or `ThreadLocal` if that reduces repeated resolution work cleanly
- [ ] Update `LocalizationMessageService` to use request context language where that improves call sites without hiding behavior
- [ ] Add validation for supported languages (`en`, `es`, `de`, `fr`, `pl`, `uk`, `no`)

Definition of done:
- `Accept-Language: es` returns Spanish messages
- `?lang=de` returns German messages
- Cookie fallback works when header and query override are absent
- Invalid language codes fall back to English gracefully
- Language preference is captured consistently for the full request lifecycle

---

### Phase 5: Security & OAuth Integration

Status: Ready

#### 5.1 Add Spring Security with OAuth 2.0

Depends on: 2.1

Goal:
Protect state-changing endpoints while keeping the demo simple and approachable.

Tasks:
- [ ] Add Spring Security and OAuth 2.0 client dependencies
- [ ] Choose an OAuth provider for the demo
- [ ] Create `SecurityConfig` with OAuth login and authorization rules
- [ ] Add `application-oauth.properties` and document required credentials
- [ ] Protect localization management endpoints (`POST`, `PUT`, `DELETE`)
- [ ] Protect book management endpoints as appropriate for the demo
- [ ] Configure test support for secured endpoints

Definition of done:
- Public read endpoints stay accessible
- Protected endpoints require authentication
- OAuth flow is documented and testable

#### 5.2 Add User Entity & Management

Depends on: 5.1

Goal:
Persist authenticated users and roles for authorization and future audit support.

Tasks:
- [ ] Create `User` entity and Flyway migration
- [ ] Create repository and service for user lifecycle
- [ ] Persist users on first login and update `lastLoginAt`
- [ ] Add role support (`USER`, `ADMIN`)
- [ ] Optionally expose user profile endpoints
- [ ] Add tests for login persistence and role behavior

Definition of done:
- Users are persisted and updated on login
- Roles are stored and enforceable
- Admin and regular-user flows are distinguishable

#### 5.3 Secure Audit Logging

Depends on: 5.2

Goal:
Track who changed what and when for demo-safe accountability.

Tasks:
- [ ] Create `AuditLog` entity, migration, repository, and service
- [ ] Capture create, update, and delete events for `Book` and `LocalizationMessage`
- [ ] Record the acting user and timestamp
- [ ] Optionally add an admin endpoint to browse audit logs
- [ ] Add tests for audit-log creation

Definition of done:
- All state-changing operations are auditable
- Current user context is recorded with each entry
- Audit records are append-only in normal flows

---

### Phase 6: Enhanced Book API

Status: Partially complete

Completed in archive:
- 6.1 Add Search & Filtering to Books

#### 6.2 Add Book Categories/Tags

Depends on: 6.1

Goal:
Improve book organization and discovery without overcomplicating the existing CRUD demo.

Tasks:
- [ ] Create `Category` entity and relationship model for books
- [ ] Add repository and service support
- [ ] Extend create/update flows to assign categories
- [ ] Add category endpoints
- [ ] Add category filtering to `GET /api/books`
- [ ] Seed a small default category set
- [ ] Add tests and docs coverage

Definition of done:
- Books can be assigned to categories
- Category-based filtering works
- API and docs stay consistent with the simple demo architecture

---

### Phase 7: Performance & Monitoring

Status: Blocked until Phase 6.2 is clearer

#### 7.1 Add Caching Layer

Depends on: 3.3 and ideally 6.2

Tasks:
- [ ] Add caching for localization lookups
- [ ] Add caching for category lookups if categories are introduced
- [ ] Add cache invalidation on updates
- [ ] Document the cache strategy
- [ ] Add cache-behavior tests

#### 7.2 Enhance Prometheus Metrics

Depends on: 7.1

Tasks:
- [ ] Add application-specific metrics for books, localization messages, users, and caches
- [ ] Record metrics through Micrometer in service methods
- [ ] Add tests for metric publication
- [ ] Document exposed metrics and optional dashboards

---

### Phase 8: Documentation & Developer Experience

Status: Partially complete

Completed in archive:
- 8.2 Create Developer Setup Guide
- 8.3 Add Contribution Guidelines

#### 8.1 Update API Documentation

Depends on: Phases 4 and 5 for the remaining gaps

Goal:
Keep generated API docs and written setup guidance aligned with the implemented surface area.

Remaining tasks:
- [ ] Add documentation for OAuth 2.0 flow and setup
- [ ] Add documentation for user endpoints
- [ ] Add documentation for language negotiation
- [ ] Add security warnings and best practices
- [ ] Add an internationalization section
- [ ] Add deployment guidance for local and prod profiles

Definition of done:
- New behavior is documented from tests where practical
- Security and localization flows are understandable to reviewers
- Generated HTML docs build cleanly

---

### Phase 9: Testing & Quality

Status: Ready after the next API-shape changes settle

#### 9.1 Add Contract Testing

Depends on: APIs stabilized after Phases 4 and 5

Tasks:
- [ ] Add Spring Cloud Contract support
- [ ] Create contracts for Book, LocalizationMessage, and User APIs
- [ ] Generate and run contract tests
- [ ] Publish or document stubs if useful for consumers

#### 9.2 Increase Test Coverage Target

Depends on: Core feature work stabilizing

Tasks:
- [ ] Run and review JaCoCo reports regularly
- [ ] Add tests for uncovered service and exception paths
- [ ] Add coverage thresholds to Gradle or CI
- [ ] Document coverage expectations in `CONTRIBUTING.md`

#### 9.3 Add Load & Performance Testing

Depends on: 1.2

Tasks:
- [ ] Add JMeter or Gatling scenarios
- [ ] Benchmark list, search, localization lookup, and auth flows
- [ ] Document baseline expectations and regression checks

---

### Phase 10: DevOps & Deployment

Status: Ready

#### 10.1 Add CI/CD Pipeline

Depends on: Current quality gates staying stable

Tasks:
- [ ] Choose CI/CD platform
- [ ] Automate `spotlessCheck`, `pmdMain`, `test`, and `asciidoctor`
- [ ] Optionally run `qodanaScan`
- [ ] Build Docker image in CI
- [ ] Document the pipeline and branch protections

#### 10.2 Add Kubernetes Manifests

Depends on: 10.1

Tasks:
- [ ] Create base Kubernetes manifests under `k8s/`
- [ ] Externalize config and secrets correctly
- [ ] Add resource requests, limits, and probes
- [ ] Document deployment steps

#### 10.3 Add Helm Chart

Depends on: 10.2

Tasks:
- [ ] Create Helm chart structure and values
- [ ] Template deployment resources
- [ ] Validate rendered manifests
- [ ] Document Helm-based deployment

#### 10.4 Add Monitoring & Alerting Setup

Depends on: 7.2

Tasks:
- [ ] Create monitoring stack for Prometheus, Grafana, and Alertmanager
- [ ] Add scrape config, dashboards, and alert rules
- [ ] Document monitoring setup and expected alerts

---

### Phase 11: Optional Future Enhancements

Status: Deferred

#### 11.1 Add Batch Processing
- [ ] Add Spring Batch if bulk import/export becomes necessary
- [ ] Add jobs for book import or audit cleanup

#### 11.2 Add Async Message Processing
- [ ] Add RabbitMQ or Kafka if event-driven flows become necessary
- [ ] Move notifications or audit fan-out to async processing

#### 11.3 Add Full-Text Search
- [ ] Add Elasticsearch if search requirements outgrow the relational model
- [ ] Index books and localization messages
- [ ] Expose advanced search endpoints

#### 11.4 Add GraphQL API
- [ ] Add Spring GraphQL only if there is a real client need
- [ ] Define schema for books, users, and localization data
- [ ] Implement queries and mutations

---

## Completed Work Archive

See `COMPLETED_TASKS.md` for archived completed phases, implementation notes, and commit references.

## Quick Reference: Quality Gates

Before completing a task, run:

```powershell
$env:JAVA_HOME='C:\Users\kamki\.jdks\azul-25.0.3'
$env:Path="$env:JAVA_HOME\bin;$env:Path"

.\gradlew.bat spotlessCheck
.\gradlew.bat --no-problems-report pmdMain
.\gradlew.bat --no-problems-report test
.\gradlew.bat asciidoctor
.\gradlew.bat qodanaScan  # Optional
```

## Notes

- Keep the roadmap dependency-ordered so the next implementable task is obvious.
- Keep `TODO.md` focused on active work; archive finished work instead of letting this file grow indefinitely.
- Maintain alignment between `README.md` and `AGENTS.md` when project behavior, setup, or quality gates change.
