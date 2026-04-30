# Project TODO & Roadmap

This file tracks active and upcoming work for the technical-interview-demo Spring Boot application.
Completed work has been moved to `COMPLETED_TASKS.md` so this file stays focused on execution order and next steps.

## High-Level Roadmap

The roadmap below is ordered to respect cross-phase dependencies.

| Order | Theme | Status | Why it comes next |
| --- | --- | --- | --- |
| 1 | Phase 10: CI/CD and deployment assets | Ready | Depends on the now-stable quality gates and build outputs from Phase 9 |
| 2 | Phase 11: Optional future enhancements | Deferred | These are stretch items after the core demo roadmap is complete |

## Current Priorities

1. Start Phase 10 CI/CD and deployment work now that the Phase 9 coverage and performance checks are in place.
2. Keep Phase 11 explicitly deferred until the core roadmap is complete.
3. Keep the naming and package-convention cleanup plan ready for opportunistic refactors so new code does not introduce more exceptions.

## Active Detailed Plan

### Phase 10: DevOps & Deployment

Status: Ready

#### 10.1 Add CI/CD Pipeline

Depends on: Current quality gates staying stable

Tasks:
- [ ] Choose CI/CD platform
- [ ] Automate `spotlessCheck`, `pmdMain`, `test`, and `asciidoctor`
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

## Convention Simplification Backlog

Status: Planned

Goal: reduce naming and package-placement exceptions so future code follows one obvious convention.

Execution rule:
- Prefer doing these refactors together with nearby feature work unless a dedicated cleanup pass is explicitly scheduled.
- Keep changes small, behavior-preserving, and covered by the existing build and integration tests.

### 1. Technical Overview Endpoint Naming
- [ ] Rename `HelloController` to `TechnicalOverviewController` so the class name matches both `/` and `/hello`
- [ ] Consider moving technical-overview classes into a focused package such as `technical.info` or `technical.overview`
- [ ] Keep `/hello` as a small smoke-test endpoint without letting the controller name become misleading again

### 2. Feature Exception Placement
- [ ] Move book-specific exceptions out of `technical.api` and into `business.book`
- [ ] Keep only cross-cutting HTTP and web-layer exceptions in `technical.api`
- [ ] Keep `ApiExceptionHandler` responsible for translation to HTTP responses, not for deciding exception ownership

### 3. User Domain Naming Consistency
- [ ] Pick one noun for the user area and apply it consistently across entity, service, controller, request, and response types
- [ ] Prefer `UserAccount*` naming to avoid confusion with Spring Security's generic `User`
- [ ] Rename `UserProfileController`, `UserProfileResponse`, and `UserLanguagePreferenceRequest` if the project standard becomes `UserAccount*`

### 4. Localization Package Boundaries
- [ ] Keep `technical.localization` focused on request-language resolution and context handling
- [ ] Move localization-message seed/bootstrap classes out of `technical.localization` and into `business.localization` or `business.localization.seed`
- [ ] Preserve the split where localization-message CRUD remains business code and request-language plumbing remains technical infrastructure

### 5. Authenticated User Service Naming
- [ ] Rename `AuthenticatedUserSecurityService` to something closer to its actual responsibility, such as `AuthenticatedUserService` or `CurrentUserService`
- [ ] Keep security-specific filters in `technical.security`
- [ ] Consider moving persisted-user synchronization logic closer to the `business.user` package if that reduces cross-package leakage

### 6. Seed/Bootstrap Naming Consistency
- [ ] Standardize initializer naming across features
- [ ] Pick one suffix, preferably either `*DataInitializer` everywhere or `*SeedConfiguration` everywhere
- [ ] Apply the same convention to books, categories, and localization messages

### 7. Search/Specification Naming
- [ ] Rename `BookSpecifications` to a more specific name such as `BookSearchSpecifications` or `BookSearchSpecificationFactory`
- [ ] Keep search/filter construction names tied to the use case rather than generic utility naming

### 8. Test Naming Consistency
- [ ] Standardize MockMvc/integration test naming so API integration tests use the same suffix everywhere
- [ ] Rename `CategoryApiTests` and `LocalizationApiTests` to `*ApiIntegrationTests` if the project keeps that convention
- [ ] Keep documentation tests and pure unit tests clearly distinguishable by suffix

### 9. Package Cleanup
- [ ] Remove empty or unused package placeholders such as `technical.config`
- [ ] Keep package structure shallow enough that placement is obvious without special-case rules

---

## Completed Work Archive

See `COMPLETED_TASKS.md` for archived completed phases, including the completed Phase 4 language negotiation work, the completed Phase 5 security and audit work, the completed Phase 6 book API expansion, the completed Phase 7 caching and metrics work, the completed Phase 8 documentation and OpenAPI work, the completed Phase 9 coverage/performance work, the completed release-versioning workflow, and the completed architecture hardening phase.

## Quick Reference: Quality Gates

Before completing a task, run:

```powershell
$env:JAVA_HOME='C:\Users\kamki\.jdks\azul-25.0.3'
$env:Path="$env:JAVA_HOME\bin;$env:Path"

.\gradlew.bat build
```

## Notes

- Keep the roadmap dependency-ordered so the next implementable task is obvious.
- Keep `TODO.md` focused on active work; archive finished work instead of letting this file grow indefinitely.
- Maintain alignment between `README.md` and `AGENTS.md` when project behavior, setup, or quality gates change.
