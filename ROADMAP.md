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

- Use `v2.0.0-M8` for the selected internal maintainability cleanup batch that clears the remaining CodeQL maintainability and quality alerts without changing the published contract.
- Freeze the `2.0` published contract and cut `v2.0.0-RC1` from `main` only after the `v2.0.0-M8` candidate passes the required validation.
- Release stable `v2.0.0`, update `CHANGELOG.md`, and remove the completed `2.0` track from `ROADMAP.md` only after the `v2.0.0-RC1` line is accepted.

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

Goal: finish the stable `2.0` line now that `v2.0.0-M1` through `v2.0.0-M7` established the browser-session contract, deployment boundary, upgrade guide, edge reference, smoke-alignment baseline, UTC instant timestamp contract cleanup, and the pre-`v2.0.0-RC1` security hardening baseline, while using `v2.0.0-M8` for the last internal maintainability-only cleanup before the later contract freeze.

#### `v2.0.0-M8` Maintainability Cleanup
- [x] Replace the deprecated `HeadersConfigurer.permissionsPolicy` call in `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecurityConfiguration.java` to clear CodeQL maintainability alert `#20`.
- [x] Replace the deprecated `ObjectMapper.setSerializationInclusion` call in `buildSrc/src/main/kotlin/team/jit/technicalinterviewdemo/build/GatlingBenchmarkTask.kt` to clear CodeQL maintainability alert `#14`.
- [x] Remove or use the unused `service` parameter in `src/main/java/team/jit/technicalinterviewdemo/technical/logging/ServiceLoggingAspect.java` to clear CodeQL quality alert `#15`.

#### Release Confidence
- [ ] Freeze the `2.0` published contract and cut `v2.0.0-RC1` from `main` only after the exact candidate passes `.\gradlew.bat build`, required smoke checks, and `.\gradlew.bat gatlingBenchmark` when session-startup behavior changes.
- [ ] Release stable `v2.0.0`, update `CHANGELOG.md`, and remove the completed `2.0` track from `ROADMAP.md`.

## Deferred

### Post-`2.0` Frontend AI Contract

Status: Deferred until stable `2.0` is released

- [ ] Generate a frontend-contract AI instruction file in this repository using the `frontend-design` skill from Anthropic's `skills` repository as source guidance, then copy it into the frontend repository as source input for the AI agent there to generate that repo's AI instructions.

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
