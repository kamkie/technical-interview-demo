# Project TODO & Roadmap

This file tracks active and upcoming work for the technical-interview-demo Spring Boot application.
Completed work has been moved to `COMPLETED_TASKS.md` so this file stays focused on execution order and next steps.

## High-Level Roadmap

The roadmap below is ordered to respect cross-phase dependencies.

| Order | Theme | Status | Why it comes next |
| --- | --- | --- | --- |
| 1 | Phase 8.5: OpenAPI and compatibility gates | Ready after Phase 5 | The machine-readable contract should reflect the secured API surface |
| 2 | Phase 8.1: API and operations documentation gaps | Ready after Phases 5 and 8.5 | The remaining docs should follow the persisted user model and final OpenAPI contract |
| 3 | Phase 9: Coverage and performance testing | Ready after core APIs stabilize | Better value once the near-term API and auth work are settled |
| 4 | Phase 10: CI/CD and deployment assets | Ready | Depends mostly on the current quality gates and stable build outputs |
| 5 | Phase 11: Optional future enhancements | Deferred | These are stretch items after the core demo is complete |

## Current Priorities

1. Start Phase 8.5 OpenAPI support and breaking-change compatibility checks now that the secured API surface and audit trail are in place.
2. Close the remaining Phase 8.1 documentation follow-up once the OpenAPI contract and compatibility workflow are in place.
3. Revisit Phase 9 coverage and performance work after the near-term API-shape changes settle.
4. Start Phase 10 CI/CD and deployment work once the API contract stabilizes.

## Active Detailed Plan

### Phase 5: Security & OAuth Integration

Status: Completed

Completed in archive:
- 5.1 Add Spring Security with OAuth 2.0
- 5.2 Add User Entity & Management
- 5.3 Secure Audit Logging

---

### Phase 6: Enhanced Book API

Status: Partially complete

Completed in archive:
- 6.1 Add Search & Filtering to Books

### Phase 8: Documentation & Developer Experience

Status: Mostly complete

Completed in archive:
- 8.2 Create Developer Setup Guide
- 8.3 Add Contribution Guidelines

#### 8.1 Update API Documentation

Depends on: 8.5 for the remaining contract-focused follow-up

Goal:
Keep generated API docs and written setup guidance aligned with the implemented surface area.

Remaining tasks:
- [x] Add documentation for the persisted user model, roles, and user-facing profile endpoints added in Phase 5.2
- [x] Add security warnings and reviewer-facing setup guidance for the OAuth/session flow
- [ ] Add documentation for the OpenAPI contract and compatibility-check workflow

Definition of done:
- New behavior is documented from tests where practical
- Security and localization flows are understandable to reviewers
- Generated HTML docs build cleanly

#### 8.5 Add OpenAPI & Compatibility Gates

Depends on: 5.1

Goal:
Describe the API in a machine-readable way and fail the build when an unapproved breaking change is introduced.

Tasks:
- [ ] Add OpenAPI support and expose the generated specification
- [ ] Document public and secured endpoints, schemas, pagination, and auth requirements in the OpenAPI contract
- [ ] Decide how the approved OpenAPI baseline is stored and versioned
- [ ] Add a compatibility test that compares the current OpenAPI contract to the approved baseline and fails on breaking changes
- [ ] Document how to intentionally refresh the approved contract
- [ ] Add the compatibility check to local verification and CI

Definition of done:
- The application publishes or generates an OpenAPI contract for the implemented API
- Unapproved breaking API changes fail the compatibility check
- Reviewers can inspect endpoint and auth expectations without reverse-engineering controller code

---

### Phase 9: Testing & Quality

Status: Ready after the next API-shape changes settle

#### 9.1 Increase Test Coverage Target

Depends on: Core feature work stabilizing

Tasks:
- [ ] Run and review JaCoCo reports regularly
- [ ] Add tests for uncovered service and exception paths
- [ ] Add coverage thresholds to Gradle or CI
- [ ] Document coverage expectations in `CONTRIBUTING.md`

#### 9.2 Add Load & Performance Testing

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

See `COMPLETED_TASKS.md` for archived completed phases, including finished Phase 4 language negotiation work, the completed Phase 5 security and audit work, the completed release-versioning workflow, the completed category/tag enhancement phase, the completed caching/metrics phase, and the completed architecture hardening phase.

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
