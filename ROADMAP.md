# Project TODO & Roadmap

This file tracks active and upcoming work for the technical-interview-demo Spring Boot application.
Keep this file focused on work that is still planned or in progress.

## Not Yet Refined

Keep rough candidate tasks here for manual editing before they are promoted into real roadmap entries.

- Use this section as an intake list for ideas that are not yet specific enough for `Current Priorities` or `Ordered Plan`.
- Rewrite an item into a concrete roadmap entry before moving it below.
- Remove an item from this section once it is promoted, merged into another entry, or intentionally dropped.

### Rough Tasks

Add new rough tasks below.

No unrefined tasks currently.

## How To Use This File

- Keep only active or planned work here.
- Use `[ ]` for planned work that is not yet selected.
- Use `[x]` for work explicitly selected for current planning or development.
- Do not use `[x]` to mean completed; remove an item once it is no longer active roadmap work.
- Keep items short so they are easy to reorder and edit.
- Remove completed items instead of turning this file into a historical archive.
- Use `CHANGELOG.md` for released history, not `ROADMAP.md`.

## Current Priorities

1. Replace the current reviewer-oriented browser write posture with production-grade same-site protections and explicit reverse-proxy/public-origin assumptions for the separate first-party UI.
2. Close the remaining rolling-release, restore-verification, and pre-promotion deployment gaps around migration compatibility and deployment checks.

## Locked Framing Decisions

- Post-`1.x` production-ready work keeps browser-oriented auth by adding a first-party UI instead of switching this repo to a stateless token-only contract.
- That first-party UI belongs in a separate repository; this repository remains the backend/API, auth, and operational contract.
- The separate first-party UI is expected to share one public origin with the backend through reverse-proxy deployment, so browser flows should target a same-site contract rather than a cross-origin one.
- Release hardening assumes rolling-compatible deployments, so migration and rollout work must preserve mixed-version compatibility during upgrades.

## Ordered Plan

### Future: Production-Ready Sample App Track

Status: Planned

Goal: evolve the repository into a production-ready backend sample that supports a separate first-party UI behind one public origin deliberately, with explicit contract, rollout, and posture review instead of treating that shift as a silent extension of the frozen interview-demo `1.x` promise.

#### Revisit The Security Posture
- [ ] Replace the current reviewer-oriented CSRF-disabled browser write posture with production-grade same-site browser protections that work for a reverse-proxied first-party UI
- [ ] Define and enforce reverse-proxy, public-origin, cookie, redirect, and session assumptions for the backend-to-UI boundary
- [ ] Restrict technical endpoints such as Prometheus and non-public actuator surfaces behind production-ready network or auth expectations instead of relying on deployment convention alone
- [ ] Add security headers and explicit HTTPS/proxy handling assumptions for real deployments
- [ ] Add authenticated abuse protection such as request-rate limiting or similar controls for login bootstrap and write-heavy paths

#### Make Releases And Migrations Safer
- [ ] Define a rolling-compatible Flyway rollout model, including expand-and-contract rules, mixed-version compatibility expectations, and schema-first versus app-first ordering by migration type
- [ ] Add automated backup-restore verification or at least a reproducible pre-release restore drill for migration-bearing releases
- [ ] Add deployment checks that validate the exact published image, runtime configuration, and mixed-version readiness before promotion beyond local or CI environments
- [ ] Document and validate a realistic disaster-recovery path instead of only a local rollback narrative

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
