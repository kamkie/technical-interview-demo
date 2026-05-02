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
4. Define the explicit post-`1.x` work needed to evolve this repo from an interview demo into a production-ready sample application.

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

#### Publish Quality Signals
- [ ] Publish JaCoCo coverage reports from CI to codecov.io
- [ ] Add CI and Codecov badges to `README.md` once the external coverage reporting is live

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

### Future: Production-Ready Sample App Track

Status: Planned after the current `1.x` demo-hardening work

Goal: evolve the repository into a production-ready sample app deliberately, with explicit contract and posture review instead of treating that shift as a silent extension of the frozen interview-demo `1.x` promise.

#### Revisit The Security Posture
- [ ] Replace the current reviewer-oriented CSRF-disabled browser write posture with a production-grade approach and update the supported client-flow model accordingly
- [ ] Restrict technical endpoints such as Prometheus and non-public actuator surfaces behind production-ready network or auth expectations instead of relying on deployment convention alone
- [ ] Add security headers, forwarded-header handling, and explicit HTTPS/proxy assumptions for real deployments
- [ ] Add authenticated abuse protection such as request-rate limiting or similar controls for login bootstrap and write-heavy paths

#### Harden Identity, Sessions, And Secrets
- [ ] Move beyond the single optional GitHub OAuth provider toward a production-ready identity story suitable for a sample app, including explicit issuer/provider configuration
- [ ] Tighten session-management settings for real deployments, including cookie scope, timeout, rotation, and concurrent-session expectations
- [ ] Add stronger secret and credential handling guidance or integration points so deployments do not rely only on raw environment-variable wiring
- [ ] Add startup validation for required auth, session, and outbound-integration settings when the production-ready posture is enabled

#### Strengthen Supply Chain And Artifact Trust
- [ ] Generate and publish an SBOM for the application artifact and container image as part of the build or release flow
- [ ] Sign published container images and attach provenance or attestations so the sample release story covers artifact authenticity, not only version tags
- [ ] Add static application security testing and keep it aligned with the existing Gradle and CI workflows
- [ ] Keep dependency, image, and vulnerability exceptions reviewable with explicit policy files and documented expiration or revalidation expectations

#### Make Releases And Migrations Safer
- [ ] Define a safer production rollout model for Flyway-backed releases, including compatibility expectations for rolling upgrades and schema-first versus app-first ordering
- [ ] Add automated backup-restore verification or at least a reproducible pre-release restore drill for migration-bearing releases
- [ ] Add deployment checks that validate the exact published image and runtime configuration before promotion beyond local or CI environments
- [ ] Document and validate a realistic disaster-recovery path instead of only a local rollback narrative

#### Expand Operational Readiness Into Real SRE Basics
- [ ] Add alerting and dashboards for authentication failures, session persistence health, Flyway startup failures, database saturation, and application error-rate regressions
- [ ] Add log shipping and trace-export guidance that works beyond local inspection and single-node troubleshooting
- [ ] Define resource requests, limits, autoscaling expectations, and disruption budgets for Kubernetes deployments
- [ ] Add synthetic or scheduled external checks so the app is observed continuously after deployment, not only during release-time smoke validation

#### Tighten Data And Admin Operations
- [ ] Add database backup, retention, and restore expectations for a production-ready sample deployment
- [ ] Add an explicit admin or operator surface for inspecting audit history, runtime diagnostics, and operational status without requiring direct database access
- [ ] Add seed-data and bootstrap guidance that cleanly separates demo defaults from production-safe initialization behavior

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
