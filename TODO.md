# Project TODO & Roadmap

This file tracks active and upcoming work for the technical-interview-demo Spring Boot application.
Keep this file focused on work that is still planned or in progress.

## How To Use This File

- Keep only active or planned work here.
- Keep items short so they are easy to reorder and edit.
- Remove completed items instead of turning this file into a historical archive.
- Use `CHANGELOG.md` for released history, not `TODO.md`.

## Current Priorities

1. Complete the CI/CD and deployment assets after the `1.0` release bar is explicit.
2. Finish the pre-`1.0` release-readiness work so the project has a clear, defensible definition of what `1.0` means.
3. Keep the public API stable after the last pre-`1.0` simplification pass unless a release-blocking clarity issue is still found.
4. Keep Milestone 11 explicitly deferred until the core roadmap is complete.

## Ordered Plan

### Next: Milestone 10 CI/CD & Deployment

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

---

## Deferred

### Milestone 11: Optional Future Enhancements

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

## Quick Reference: Quality Gates

Before completing a task, run:

```powershell
.\gradlew.bat build
```

Use `SETUP.md` for environment prerequisites and local verification setup.

## Notes

- Keep the roadmap dependency-ordered so the next implementable task is obvious.
- Keep `TODO.md` focused on active work only.
- Maintain alignment between `README.md`, `AGENTS.md`, and `SETUP.md` when project behavior, working rules, or setup guidance change.
