# Project TODO & Roadmap

This file tracks active and upcoming work for the technical-interview-demo Spring Boot application.
Keep this file focused on work that is still planned or in progress.

## How To Use This File

- Keep only active or planned work here.
- Keep items short so they are easy to reorder and edit.
- Remove completed items instead of turning this file into a historical archive.
- Use `CHANGELOG.md` for released history, not `ROADMAP.md`.

## Current Priorities

1. Tighten post-`1.0` release governance and operational documentation around the now-stable demo contract.
2. Document healthy runtime expectations, deployment troubleshooting, and upgrade/rollback guidance for maintained releases.
3. Keep optional future enhancements explicitly deferred until those operational gaps are closed.

## Ordered Plan

### Now: Post-1.0 Operational Readiness

Status: Planned

Goal: keep the released `1.x` demo contract easy to operate, review, and evolve without reopening the completed `1.0` scope decisions.

#### Finalize Release Governance
- [ ] Add a release checklist for Flyway migration review, OpenAPI compatibility, benchmark review, changelog update, and tagging

#### Document Operational Readiness
- [ ] Document what healthy runtime behavior looks like for health, readiness, metrics, and audit logging
- [ ] Add deployment-oriented troubleshooting for OAuth setup, PostgreSQL connectivity, and session persistence failures
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
