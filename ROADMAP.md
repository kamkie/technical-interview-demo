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

1. Define the post-`1.x` security-hardening track as an explicit breaking follow-up that replaces the current reviewer-oriented browser write posture with production-grade same-site protections and explicit reverse-proxy/public-origin assumptions for the separate first-party UI.

## Locked Framing Decisions

- Post-`1.x` production-ready work keeps browser-oriented auth by adding a first-party UI instead of switching this repo to a stateless token-only contract.
- That first-party UI belongs in a separate repository; this repository remains the backend/API, auth, and operational contract.
- The separate first-party UI is expected to share one public origin with the backend through reverse-proxy deployment, so browser flows should target a same-site contract rather than a cross-origin one.
- The production-ready security-hardening track is an explicit post-`1.x` contract shift rather than additive `1.x` work; treat it as `2.0`-style breaking follow-up planning.
- Restricting technical endpoints means keeping Prometheus and other non-public actuator surfaces deployment-scoped and privately reachable, while keeping authenticated operator visibility in the application-owned operator surface instead of turning metrics into an app-auth API.
- Abuse protection for login bootstrap and write-heavy paths is owned primarily by edge or deployment controls; this roadmap should document and enforce those expectations rather than assuming repo-owned in-app rate limiting is required.

## Ordered Plan

### Future: Post-`1.x` Production-Ready Sample App Track

Status: Planned

Goal: evolve the repository into a production-ready backend sample that supports a separate first-party UI behind one public origin deliberately, with explicit contract, rollout, and posture review as a post-`1.x` breaking follow-up instead of treating that shift as a silent extension of the frozen interview-demo `1.x` promise.

#### Revisit The Security Posture
- [ ] Replace the current reviewer-oriented CSRF-disabled browser write posture with production-grade same-site browser protections that work for a reverse-proxied first-party UI
- [ ] Define and enforce reverse-proxy, public-origin, cookie, redirect, and session assumptions for the backend-to-UI boundary
- [ ] Restrict technical endpoints such as Prometheus and non-public actuator surfaces through deployment-scoped private access expectations, while keeping authenticated operator visibility in the application-owned operator surface
- [ ] Add security headers and explicit HTTPS/proxy handling assumptions for real deployments
- [ ] Define the required edge or deployment-owned abuse-protection expectations for login bootstrap and write-heavy paths instead of assuming repo-owned application rate limiting

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
