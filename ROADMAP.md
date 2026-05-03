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

1. Lock the post-`1.x` client and authentication direction so the production-ready track stops carrying competing API-only versus first-party UI futures.
2. Close the remaining release-safety and artifact-trust gaps around image authenticity, migration rollout, restore verification, and pre-promotion deployment checks.

## Ordered Plan

### Future: Production-Ready Sample App Track

Status: Planned

Goal: evolve the repository into a production-ready sample app deliberately, with explicit contract and posture review instead of treating that shift as a silent extension of the frozen interview-demo `1.x` promise.

#### Lock The Post-`1.x` Client And Auth Direction
- [ ] Decide whether the production-ready track stays API-first and replaces the current session-oriented write flow with a stateless token contract, or instead keeps browser-oriented auth by adding a first-party UI that justifies session-backed flows
- [ ] If a first-party UI remains in scope, decide whether it belongs inside this repository or in a separate repository before adding any frontend build, deployment, or release surface
- [ ] Rewrite the downstream security and identity roadmap items around the chosen direction and remove the rejected alternative once that decision is made

#### Revisit The Security Posture
- [ ] Replace the current reviewer-oriented CSRF-disabled browser write posture with a production-grade approach and update the supported client-flow model accordingly
- [ ] Restrict technical endpoints such as Prometheus and non-public actuator surfaces behind production-ready network or auth expectations instead of relying on deployment convention alone
- [ ] Add security headers, forwarded-header handling, and explicit HTTPS/proxy assumptions for real deployments
- [ ] Add authenticated abuse protection such as request-rate limiting or similar controls for login bootstrap and write-heavy paths

#### Strengthen Supply Chain And Artifact Trust
- [ ] Add GitHub CodeQL scanning in GitHub Actions with repository-owned configuration, uploaded SARIF results, and explicit guidance for how it complements rather than silently duplicates the existing SpotBugs/FindSecBugs and PMD quality gates
- [ ] Sign published container images and attach provenance or attestations so the sample release story covers artifact authenticity, not only version tags

#### Make Releases And Migrations Safer
- [ ] Define a safer production rollout model for Flyway-backed releases, including compatibility expectations for rolling upgrades and schema-first versus app-first ordering
- [ ] Add automated backup-restore verification or at least a reproducible pre-release restore drill for migration-bearing releases
- [ ] Add deployment checks that validate the exact published image and runtime configuration before promotion beyond local or CI environments
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
