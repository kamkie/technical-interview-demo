# Project TODO & Roadmap

This file tracks active and upcoming work for the technical-interview-demo Spring Boot application.
Completed work has been moved to `COMPLETED_TASKS.md` so this file stays focused on execution order and next steps.

## How To Use This File

- Keep only active or planned work here.
- Keep items short so they are easy to reorder and edit.
- Move completed work to `COMPLETED_TASKS.md`.
- Prefer adding tasks under an existing section instead of creating a new roadmap layer.

## Current Priorities

1. Finish the pre-`1.0` release-readiness work so the project has a clear, defensible definition of what `1.0` means.
2. Complete the CI/CD and deployment assets after the `1.0` release bar is explicit.
3. Apply naming and package cleanup opportunistically when touching nearby code.
4. Keep Phase 11 explicitly deferred until the core roadmap is complete.

## Ordered Plan

### Now: Pre-1.0 Release Readiness

Status: Planned

Goal: make the `1.0` boundary mean a stable, documented, and intentionally scoped demo application rather than only a feature-complete snapshot.

#### Define The 1.0 Contract
- [ ] Decide whether `1.0` means "stable interview-demo reference app" or "production-ready starter"
- [ ] Document which endpoints and behaviors are part of the supported public contract
- [ ] Document which endpoints are technical/demo-only convenience endpoints
- [ ] Define the compatibility promise for future `1.x` releases

#### Stabilize The Public API Surface
- [ ] Review endpoint naming, resource semantics, and response shapes before freezing the `1.0` contract
- [ ] Stop returning JPA entities directly from public controllers where persistence shape leaks into the API
- [ ] Standardize `401` and `403` responses so they follow the same documented `ProblemDetail` style as other API errors
- [ ] Refresh the approved OpenAPI baseline only after the `1.0` surface is reviewed and accepted

#### Revisit The Production Security Posture
- [ ] Remove insecure production-style defaults and fail fast when required secrets or database credentials are missing in `prod`
- [ ] Re-evaluate session-based write security before `1.0`: either re-enable CSRF for browser-session writes or document the deliberate alternative
- [ ] Review whether `/actuator/prometheus` and other technical endpoints should stay public in production or become deployment-specific
- [ ] Document the intended production posture for session cookies, OAuth login, admin bootstrap, and trusted deployment topology

#### Add Release-Grade Runtime Verification
- [ ] Add a smoke test that verifies the packaged container starts successfully and reaches readiness
- [ ] Add focused verification for the `prod` profile so release builds do not only prove the local/test profiles
- [ ] Add a release checklist for Flyway migration review, OpenAPI compatibility, benchmark review, changelog update, and tagging

#### Tighten Operational Readiness
- [ ] Document what healthy runtime behavior looks like for health, readiness, metrics, and audit logging
- [ ] Add deployment-oriented troubleshooting for OAuth setup, PostgreSQL connectivity, and session persistence failures
- [ ] Document an upgrade and rollback flow for schema migrations and versioned container releases

#### Finish The Most Important Convention Fixes Before 1.0
- [ ] Complete the highest-value naming and package-cleanup items that would otherwise become long-lived `1.x` debt
- [ ] Prioritize names and package placements that currently misdescribe responsibilities or mix business and technical concerns
- [ ] Keep the cleanup focused on changes that improve clarity without reopening settled API behavior

---

### Next: Phase 10 CI/CD & Deployment

Status: Ready after the pre-`1.0` release bar is explicit

#### CI/CD Pipeline
- [ ] Choose the CI/CD platform
- [ ] Automate `spotlessCheck`, `pmdMain`, `test`, and `asciidoctor`
- [ ] Build the Docker image in CI
- [ ] Document the pipeline and branch protections

#### Kubernetes Manifests
- [ ] Create base manifests under `k8s/`
- [ ] Externalize config and secrets correctly
- [ ] Add resource requests, limits, and probes
- [ ] Document deployment steps

#### Helm Chart
- [ ] Create the Helm chart structure and values
- [ ] Template deployment resources
- [ ] Validate rendered manifests
- [ ] Document Helm-based deployment

#### Monitoring & Alerting Setup
- [ ] Create a monitoring stack for Prometheus, Grafana, and Alertmanager
- [ ] Add scrape config, dashboards, and alert rules
- [ ] Document monitoring setup and expected alerts

---

## Opportunistic Cleanup

Status: Planned

Use this section when touching nearby code. Do not treat it as a blocker for unrelated feature work.

### Naming And Package Simplification
- [x] Align technical overview naming by renaming `HelloController` to `TechnicalOverviewController` and moving overview classes under `technical.info`
- [x] Move feature-specific exceptions and seed/bootstrap classes closer to their business packages, keeping only cross-cutting infrastructure in `technical.*`
- [x] Standardize user-area naming around `UserAccount*` and rename `AuthenticatedUserSecurityService` to `CurrentUserAccountService`
- [x] Standardize initializer, search/specification, and API integration test naming so similar classes follow one pattern
- [x] Remove empty or unused package placeholders such as `technical.config`

---

## Deferred

### Phase 11: Optional Future Enhancements

Status: Deferred until the core roadmap is complete

#### Batch Processing
- [ ] Add Spring Batch if bulk import/export becomes necessary
- [ ] Add jobs for book import or audit cleanup

#### Async Message Processing
- [ ] Add RabbitMQ or Kafka if event-driven flows become necessary
- [ ] Move notifications or audit fan-out to async processing

#### Full-Text Search
- [ ] Add Elasticsearch if search requirements outgrow the relational model
- [ ] Index books and localization messages
- [ ] Expose advanced search endpoints

#### GraphQL API
- [ ] Add Spring GraphQL only if there is a real client need
- [ ] Define schema for books, users, and localization data
- [ ] Implement queries and mutations

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
