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

No active priorities currently.

## Ordered Plan

### Moving to `2.0`

Status: Planned

Goal: finish the stable `2.0` line now that `v2.0.0-M1` and `v2.0.0-M2` already established the new browser-session and deployment-boundary contract.

#### Contract And Integration
- [ ] Define first-party UI integration requirements for login bootstrap, logout, session refresh, CSRF refresh, and one-public-origin reverse-proxy deployment.
- [ ] Publish a `1.x` to `2.0` upgrade guide for the `/api/**` boundary, `/api/session/**` auth routes, `loginProviders[]`, and CSRF-protected unsafe writes.

#### Deployment Assets
- [ ] Add checked-in reverse-proxy or ingress reference assets for `/api/**`-only exposure, private non-`/api/**` surfaces, and edge-owned abuse protection.

#### Release Confidence
- [ ] Extend `externalSmokeTest` and post-deploy smoke to prove `GET /api/session`, CSRF cookie bootstrap, and one authenticated unsafe `/api/**` write.
- [ ] Freeze the `2.0` published contract and cut `v2.0.0-RC1` from `main` only after the exact candidate passes `.\gradlew.bat build`, required smoke checks, and `.\gradlew.bat gatlingBenchmark` when session-startup behavior changes.
- [ ] Release stable `v2.0.0`, update `CHANGELOG.md`, and remove the completed `2.0` track from `ROADMAP.md`.

## Deferred

### Delivery Tooling

Status: Deferred until `2.0` is stable

- [ ] Add GitHub dependency submission for Gradle dependencies so GitHub dependency graph data stays aligned with CI builds.

### Optional Future Enhancements

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
