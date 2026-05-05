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

- Use the remaining `2.0` prerelease budget only for unresolved cleanup that still needs to land before the contract freeze.
- Land the selected pre-`v2.0.0-RC1` GitHub security and supply-chain fixes, and defer the remaining maintainability-only cleanup until after stable `2.0`.
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

Goal: finish the stable `2.0` line now that `v2.0.0-M1` through `v2.0.0-M6` established the browser-session contract, deployment boundary, upgrade guide, edge reference, smoke-alignment baseline, UTC instant timestamp contract cleanup, and the current prerelease workflow and internal-cleanup baseline.


#### Selected For `v2.0.0-RC1`
- [x] Pin the third-party GitHub Actions flagged by CodeQL to verified full commit SHAs in `.github/workflows/ci.yml`, `.github/workflows/release.yml`, `.github/workflows/codeql.yml`, and `.github/workflows/post-deploy-smoke.yml` (CodeQL alerts `#22`, `#19`, `#18`, `#17`, `#16`, `#7`, `#6`, `#5`, and `#2`).
- [x] Sanitize user-controlled problem-detail logging in `src/main/java/team/jit/technicalinterviewdemo/technical/api/ApiProblemFactory.java` to clear CodeQL log-injection alerts `#11` and `#10`.
- [x] Sanitize user-controlled request tracing fields in `src/main/java/team/jit/technicalinterviewdemo/technical/logging/HttpTracingLoggingFilter.java` to clear CodeQL log-injection alerts `#13` and `#12`.
- [x] Upgrade the transitive `org.codehaus.plexus:plexus-utils` dependency to `4.0.3` or newer to resolve Dependabot alert `#4` (`GHSA-6fmv-xxpf-w3cw`).
- [x] Upgrade direct `net.sourceforge.pmd:pmd-core` to `7.22.0` or newer to resolve Dependabot alert `#3` (`GHSA-8rr6-2qw5-pc7r`).
- [x] Upgrade the transitive `org.apache.commons:commons-lang3` dependency to `3.18.0` or newer to resolve Dependabot alert `#2` (`GHSA-j288-q9x7-2f5v`).
- [x] Upgrade direct `org.jruby:jruby` to `9.4.12.1` or newer to resolve Dependabot alert `#1` (`GHSA-72qj-48g4-5xgx`).

#### Release Confidence
- [x] Keep the remaining pre-`v2.0.0-RC1` cleanup scope to GitHub security and supply-chain fixes, and defer the maintainability-only CodeQL notes until after stable `2.0`.
- [ ] Freeze the `2.0` published contract and cut `v2.0.0-RC1` from `main` only after the exact candidate passes `.\gradlew.bat build`, required smoke checks, and `.\gradlew.bat gatlingBenchmark` when session-startup behavior changes.
- [ ] Release stable `v2.0.0`, update `CHANGELOG.md`, and remove the completed `2.0` track from `ROADMAP.md`.

## Deferred

### Post-`2.0` Maintainability Cleanup

Status: Deferred until the `v2.0.0-RC1` security and release-freeze backlog is complete

- [ ] Replace the deprecated `HeadersConfigurer.permissionsPolicy` call in `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecurityConfiguration.java` to clear CodeQL maintainability alert `#20`.
- [ ] Replace the deprecated `ObjectMapper.setSerializationInclusion` call in `buildSrc/src/main/kotlin/team/jit/technicalinterviewdemo/build/GatlingBenchmarkTask.kt` to clear CodeQL maintainability alert `#14`.
- [ ] Remove or use the unused `service` parameter in `src/main/java/team/jit/technicalinterviewdemo/technical/logging/ServiceLoggingAspect.java` to clear CodeQL quality alert `#15`.

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
