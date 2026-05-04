# Project TODO & Roadmap

This file tracks active and upcoming work for the technical-interview-demo Spring Boot application.
Keep this file focused on work that is still planned or in progress.

## How To Use This File

- Keep only active or planned work here.
- Use `[ ]` for planned work that is not yet selected.
- Use `[x]` for work explicitly selected for current planning or development.
- Do not use `[x]` to mean completed; remove an item once it is no longer active roadmap work.
- Keep items short so they are easy to reorder and edit.
- Remove completed items instead of turning this file into a historical archive.
- Use `CHANGELOG.md` for released history, not `ROADMAP.md`.

## Current Priorities

- Use the remaining `2.0` prerelease budget for breaking cleanup that lowers production maintenance cost and aligns the app with stronger backend conventions.
- Freeze the `2.0` published contract and cut `v2.0.0-RC1` from `main` only after the exact candidate passes the required validation.

## Not Yet Refined

Keep rough candidate tasks here for manual editing before they are promoted into real roadmap entries.

- Use this section as an intake list for ideas that are not yet specific enough for `Current Priorities` or `Ordered Plan`.
- Rewrite an item into a concrete roadmap entry before moving it below.
- Remove an item from this section once it is promoted, merged into another entry, or intentionally dropped.

### Rough Tasks

Add new rough tasks below.

No unrefined tasks currently.

## Ordered Plan

### Moving to `2.0`

Status: Planned

Goal: finish the stable `2.0` line now that `v2.0.0-M1`, `v2.0.0-M2`, and `v2.0.0-M3` established the browser-session contract, deployment boundary, upgrade guide, edge reference, and smoke-alignment baseline.

#### Prerelease Maintainability Cleanup
- [ ] Replace `ADMIN_LOGINS` env-driven role assignment with managed role mapping plus persisted role provenance.
- [ ] Move demo-friendly defaults such as implicit `local` profile activation, Hibernate statistics, and full tracing sampling out of shared runtime config.
- [ ] Split public, admin, and internal management surfaces more cleanly and reduce mixed-use internal endpoints before the `2.0` contract freezes.
- [ ] Replace public Spring `Page<?>` response leakage with repo-owned paginated collection DTOs across the supported API.
- [ ] Expand audit coverage to all privileged mutations and auth events, store structured change data, and define retention or archival behavior.
- [ ] Migrate persisted timestamps and API serialization to UTC `Instant` plus PostgreSQL `timestamptz`.
- [ ] Add PostgreSQL indexes that match supported search, filter, join, and operational query patterns.
- [ ] Reduce production log noise by removing reflective service-call `INFO` logging and tightening request and error correlation fields.
- [ ] Harden container and Kubernetes runtime defaults with slimmer base images and stronger pod or container security settings.

#### Release Confidence
- [ ] Decide which `2.0` prerelease maintainability-cleanup items land before `v2.0.0-RC1` and explicitly defer the rest.
- [ ] Freeze the `2.0` published contract and cut `v2.0.0-RC1` from `main` only after the exact candidate passes `.\gradlew.bat build`, required smoke checks, and `.\gradlew.bat gatlingBenchmark` when session-startup behavior changes.
- [ ] Release stable `v2.0.0`, update `CHANGELOG.md`, and remove the completed `2.0` track from `ROADMAP.md`.

## Deferred

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
