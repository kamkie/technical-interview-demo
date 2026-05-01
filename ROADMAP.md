# Project TODO & Roadmap

This file tracks active and upcoming work for the technical-interview-demo Spring Boot application.
Keep this file focused on work that is still planned or in progress.

## How To Use This File

- Keep only active or planned work here.
- Keep items short so they are easy to reorder and edit.
- Remove completed items instead of turning this file into a historical archive.
- Use `CHANGELOG.md` for released history, not `ROADMAP.md`.

## Current Priorities

1. Finish the pre-`1.0` release-readiness work so the project has a clear, defensible definition of what `1.0` means.
2. Keep the public API stable after the last pre-`1.0` simplification pass unless a release-blocking clarity issue is still found.
3. Keep optional future enhancements explicitly deferred until the core roadmap is complete.

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
- [ ] Make the `release.yml` GitHub Actions workflow create a GitHub Release with release notes pulled from `CHANGELOG.md` and a link to the published Docker image
- [ ] Add smoke tests that run from Gradle via a separate source set, target an externally running application, and stay out of the standard test lifecycle
- [ ] Run benchmark tests from a dedicated Gradle task instead of the PowerShell wrapper script
- [ ] Review the new container smoke validation and keep it aligned with release expectations for the packaged image
- [ ] Review the new deployment manifests, Helm chart, and monitoring assets against the final `1.0` security and operational defaults
- [ ] Add a release checklist for Flyway migration review, OpenAPI compatibility, benchmark review, changelog update, and tagging

#### Tighten Operational Readiness
- [ ] Document what healthy runtime behavior looks like for health, readiness, metrics, and audit logging
- [ ] Add deployment-oriented troubleshooting for OAuth setup, PostgreSQL connectivity, and session persistence failures
- [ ] Remove machine-specific local paths and personal workstation details from `SETUP.md` and replace them with portable examples and placeholders
- [ ] Document an upgrade and rollback flow for schema migrations and versioned container releases

---

## Deferred

### Milestone X: Optional Future Enhancements

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
- Keep `ROADMAP.md` focused on active work only.
- Maintain alignment between `README.md`, `AGENTS.md`, and `SETUP.md` when project behavior, working rules, or setup guidance change.
