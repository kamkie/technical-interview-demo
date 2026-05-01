# Project TODO & Roadmap

This file tracks active and upcoming work for the technical-interview-demo Spring Boot application.
Keep this file focused on work that is still planned or in progress.

## How To Use This File

- Keep only active or planned work here.
- Keep items short so they are easy to reorder and edit.
- Remove completed items instead of turning this file into a historical archive.
- Use `CHANGELOG.md` for released history, not `ROADMAP.md`.

## Current Priorities

1. Add Gradle-owned vulnerability scanning for application dependencies and the built Docker image.
2. Strengthen release confidence by expanding production-like smoke validation beyond the current five unauthenticated checks.
3. Close the most obvious demo-API maintenance gaps without reopening the frozen `1.x` contract carelessly.

## Ordered Plan

### Now: Supply-Chain And Vulnerability Scanning

Status: Planned

Goal: make dependency and container-image vulnerability findings visible in local Gradle runs, fail CI on serious issues, and stop publishing images without scanning the exact artifact produced by the build.

#### Scan The Application
- [ ] Add Gradle-owned vulnerability scanning for application and transitive dependencies instead of relying only on Dependabot update PRs
- [ ] Produce stable scan artifacts that are usable in local review and CI troubleshooting
- [ ] Define a narrow suppression and review workflow so intentional exceptions are explicit rather than hidden

#### Scan The Docker Image
- [ ] Add a Gradle task that scans the Docker image built by `dockerBuild` for OS and package vulnerabilities
- [ ] Run the image scan in CI and in the tag-driven release flow before GHCR publication
- [ ] Keep the scan tied to the exact built image coordinates so the release workflow does not scan one image and publish another

### Next: Release Confidence Hardening

Status: Planned

Goal: catch production-like regressions that the current release path can still miss, especially around docs exposure and authenticated session behavior.

#### Expand Production-Like Smoke Coverage
- [ ] Extend `externalSmokeTest` beyond `/`, `/hello`, `/docs`, readiness, and the public books list
- [ ] Add smoke coverage for the generated OpenAPI/docs assets so release candidates prove the documentation surface still works when packaged
- [ ] Add at least one authenticated session path in the production-like validation flow instead of relying only on in-process security integration tests

#### Verify Session-Backed Runtime Behavior
- [ ] Add a focused production-like check that proves JDBC-backed Spring Session persistence still works in the packaged container path
- [ ] Keep the new checks narrow so the demo stays easy to run locally and in CI

### Later: Demo API Usability Improvements

Status: Planned

Goal: improve the demo’s day-2 usability for reviewers and maintainers without turning it into a much larger platform.

#### Audit Reviewability
- [ ] Add a read-only admin-facing audit log API or export path so recorded book and localization changes can be reviewed without direct database access

#### Category Maintenance
- [ ] Add explicit category update and delete semantics with clear reassignment or validation rules instead of leaving category management create-only

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
