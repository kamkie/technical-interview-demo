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

### Future: Post-`1.x` Production-Ready Sample App Track

Status: Planned

Goal: plan the next explicit post-`1.x` contract-shaping evolution without silently stretching the frozen interview-demo `1.x` promise.

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
