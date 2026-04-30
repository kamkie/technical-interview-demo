# Project TODO & Roadmap

This file tracks active and upcoming work for the technical-interview-demo Spring Boot application.
Completed work has been moved to `COMPLETED_TASKS.md` so this file stays focused on execution order and next steps.

## High-Level Roadmap

The roadmap below is ordered to respect cross-phase dependencies.

| Order | Theme | Status | Why it comes next |
| --- | --- | --- | --- |
| 1 | Phase 9: Coverage and performance testing | Ready | Better value now that the near-term API and auth work have stabilized |
| 2 | Phase 10: CI/CD and deployment assets | Ready after Phase 9 starts to settle | Depends on stable quality gates and build outputs |
| 3 | Phase 11: Optional future enhancements | Deferred | These are stretch items after the core demo roadmap is complete |

## Current Priorities

1. Start Phase 9 coverage hardening now that the secured API surface and OpenAPI workflow are in place.
2. Revisit broader Phase 10 CI/CD and deployment work after the Phase 9 verification targets are clearer.
3. Keep Phase 11 explicitly deferred until the core roadmap is complete.

## Active Detailed Plan

### Phase 9: Testing & Quality

Status: Ready

#### 9.1 Increase Test Coverage Target

Depends on: Core feature work stabilizing

Tasks:
- [x] Run and review JaCoCo reports regularly
- [x] Add tests for uncovered service and exception paths
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

See `COMPLETED_TASKS.md` for archived completed phases, including finished Phase 4 language negotiation work, the completed Phase 5 security and audit work, the completed Phase 6 book API expansion, the completed Phase 8 documentation and OpenAPI work, the completed release-versioning workflow, the completed caching/metrics phase, and the completed architecture hardening phase.

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
