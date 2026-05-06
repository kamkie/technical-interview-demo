# Project TODO & Roadmap

This file tracks active and upcoming work for the technical-interview-demo Spring Boot application.
Keep this file focused on work that is still planned or in progress.

## Current Project State

| Status | Current |
| --- | --- |
| Release Phase | Prerelease |
| Breaking Change Policy | Disallowed |
| Next Target Version | `v2.0.0` |

## How To Use This File

- Keep only active or planned work here.
- Use `[ ]` for planned work that is not yet selected.
- Use `[x]` for work explicitly selected for current planning or development.
- Do not use `[x]` to mean completed; remove an item once it is no longer active roadmap work.
- Keep items short so they are easy to reorder and edit.
- Remove completed items instead of turning this file into a historical archive.
- Use `CHANGELOG.md` for released history, not `ROADMAP.md`.

## Current Priorities

- Clear the current GitHub Security tab Dependabot alerts before stable `v2.0.0`: PostgreSQL JDBC high severity, Netty HTTP medium severity, and JRuby medium severity.
- Release stable `v2.0.0`, update `CHANGELOG.md`, and remove the completed `2.0` track from `ROADMAP.md` only after the security alert batch is resolved and the `v2.0.0-RC2` line is accepted.

## Not Yet Refined

Keep rough candidate tasks here for manual editing before they are promoted into real roadmap entries.

- Use this section as an intake list for ideas that are not yet specific enough for `Current Priorities` or `Ordered Plan`.
- Rewrite an item into a concrete roadmap entry before moving it below.
- Remove an item from this section once it is promoted, merged into another entry, or intentionally dropped.

### Rough Tasks

Add new rough tasks below.

## Ordered Plan

### Pre-`2.0` Security And Quality Alert Sweep

Status: Planned

Goal: clear the open GitHub Security tab dependency alerts before the stable `v2.0.0` release.

#### Dependabot Alert Batch
- [ ] Resolve Dependabot alert #6 by upgrading or constraining `org.postgresql:postgresql` to `42.7.11` or newer; this high-severity JDBC driver alert covers CVE-2026-42198 / GHSA-98qh-xjc8-98pq and should block stable release until cleared.
- [ ] Resolve Dependabot alert #5 by upgrading or constraining `io.netty:netty-codec-http` to `4.2.13.Final` or newer; this medium-severity HTTP parser alert covers CVE-2026-41417 / GHSA-v8h7-rr48-vmmv and can share the same dependency-validation pass.
- [ ] Recheck and clear Dependabot alert #1 for `org.jruby:jruby`; the build currently pins Asciidoctor JRuby to `9.4.12.1`, so verify the resolved dependency graph and add the missing override or constraint only if Dependabot still observes a vulnerable JRuby path for CVE-2025-46551 / GHSA-72qj-48g4-5xgx.

### Moving to `2.0`

Status: Planned

Goal: finish the stable `2.0` line after the security alert batch is resolved by validating the frozen `v2.0.0-RC2` contract and then releasing `v2.0.0`.

#### Release Confidence
- [ ] Release stable `v2.0.0`, update `CHANGELOG.md`, and remove the completed `2.0` track from `ROADMAP.md`.

## Deferred

### Post-`2.0` Formatter Configuration Ownership

Status: Deferred until stable `2.0` is released

- [ ] Evaluate deterministic Spotless Java formatting options, preferring `eclipse().configFile("config/eclipse-style.xml")` from a checked-in formatter profile if it preserves current style with acceptable churn; compare `palantirJavaFormat()` only if the team is willing to accept an opinionated style reset.
- [ ] Migrate Java formatting away from the local IntelliJ binary dependency, keep `.editorconfig` for IDE alignment, and update `SETUP.md` or contributor guidance when formatter invocation changes.

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
