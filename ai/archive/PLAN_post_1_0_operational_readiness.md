# Plan: Post-1.0 Operational Readiness

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Closed |
| Status | Released |

## Summary
- Add the missing post-`1.0` operator and maintainer guidance that `ROADMAP.md` still tracks: a concrete release checklist, a documented definition of healthy runtime behavior, deployment-oriented troubleshooting, and an upgrade/rollback runbook.
- Keep the work documentation-first and workflow-aligned. The repository already has release automation, external smoke validation, health/readiness endpoints, metrics, audit logging, Flyway migrations, and session persistence; the gap is that those pieces are not yet documented as one coherent post-`1.0` operating model.
- Success is measured by: one consistent release checklist across maintainer guidance, one clear runbook for healthy runtime signals and troubleshooting, one honest upgrade/rollback flow for Flyway-backed releases, and passing repository validation without changing the supported `1.x` HTTP contract.

## Scope
- In scope:
  - adding a concrete release checklist that covers Flyway migration review, OpenAPI compatibility, benchmark review, changelog preparation, tagging, and post-push verification
  - documenting what healthy runtime behavior looks like for health, readiness, metrics, and audit logging
  - adding deployment-oriented troubleshooting for OAuth setup, PostgreSQL connectivity, and Spring Session JDBC persistence failures
  - documenting an upgrade and rollback flow for versioned container releases with Flyway-managed schema changes
  - aligning AI-facing release workflow guidance and human-facing runbook/docs where their scopes overlap
- Out of scope:
  - changing public endpoint behavior, response shapes, or the frozen `1.x` contract
  - changing the `1.0` production posture for CSRF, Prometheus exposure, optional OAuth, or session cookies
  - adding new runtime automation such as Flyway undo migrations, rollout controllers, backup tooling, or new health endpoints
  - redesigning CI/CD or replacing the current GitHub Actions release model
  - changing benchmark baselines, OpenAPI contract content, or deployment YAML unless execution finds a narrow contradiction that must be fixed

## Current State
- Current behavior:
  - `ROADMAP.md` still tracks four unreleased gaps under post-`1.0` operational readiness: release checklist, healthy runtime documentation, deployment troubleshooting, and upgrade/rollback guidance.
  - `ai/RELEASES.md` already defines the release flow from `main`, changelog/version/tag rules, and remote verification, but it does not yet call out an explicit pre-tag checklist for Flyway migration review, benchmark review, or the exact current immutable image-tag behavior.
  - `README.md` and `CONTRIBUTING.md` document the high-level release model and CI/CD shape, but they stop short of a maintainer-facing release checklist or an operator-focused runtime-health/runbook section.
  - `SETUP.md` already documents CI reproduction, container smoke validation, Kubernetes/Helm deployment, monitoring setup, OAuth setup, and several troubleshooting cases, but it does not yet provide:
    - one consolidated “healthy runtime” definition
    - Spring Session JDBC troubleshooting guidance
    - an upgrade/rollback flow for Flyway-backed releases
  - the current release automation is already live in `.github/workflows/release.yml`: pushing a semantic tag rebuilds the tagged image, runs `externalSmokeTest`, publishes `ghcr.io/<repo>:<tag>` plus a short-SHA immutable tag `sha-${GITHUB_SHA::12}`, and creates the GitHub Release from `CHANGELOG.md` through `scripts/release/render-release-notes.ps1`
  - the current runtime truth is already covered by implementation and tests:
    - `TechnicalOverviewService` and `TechnicalOverviewControllerIntegrationTests` define the exposed configuration metadata for session settings, observability exposure, docs paths, and shutdown behavior
    - `HttpTracingIntegrationTests` verifies `/`, `/hello`, `/docs`, `/actuator/info`, `/actuator/health/liveness`, `/actuator/health/readiness`, and `/actuator/prometheus`
    - `CachingAndMetricsTests` and `ApplicationMetrics` define the application-specific `technical.interview.demo.*` metrics surface
    - `AuditLogIntegrationTests` and `AuditLogService` define the current audit-log behavior for state-changing book and localization operations
    - `SecurityIntegrationTests` verifies JDBC-backed session persistence through `SPRING_SESSION` and `SPRING_SESSION_ATTRIBUTES`
    - `buildSrc` external smoke tasks already verify readiness and Flyway schema-history creation against a production-like container startup
- Current constraints:
  - this repo is still a small demo app, so the roadmap item should land as direct documentation and workflow guidance, not as new operational infrastructure
  - the supported `1.x` HTTP contract is already frozen; this work must not reopen contract-tier decisions from the previous `1.0` plan
  - release guidance must stay aligned with the existing tag-driven release workflow and must not promise steps the workflow does not actually perform
  - operational docs must distinguish between:
    - supported operational endpoints (`/actuator/health`, `/actuator/health/liveness`, `/actuator/health/readiness`, `/actuator/info`)
    - deployment-scoped Prometheus scraping at `/actuator/prometheus`
    - browser-session OAuth troubleshooting for the optional `oauth` profile
  - upgrade/rollback guidance must remain honest about Flyway’s forward-migration model; the repo does not provide undo migrations or automated database rollback tooling
- Relevant existing specs and code:
  - roadmap source: `ROADMAP.md`
  - release workflow source of truth: `ai/RELEASES.md`, `.github/workflows/release.yml`, `scripts/release/render-release-notes.ps1`
  - human-facing release and deployment summary: `README.md`, `CONTRIBUTING.md`, `SETUP.md`
  - generated technical endpoint docs: `src/docs/asciidoc/technical-endpoints.adoc`
  - reviewer HTTP examples: `src/test/resources/http/technical-endpoints.http`, `src/test/resources/http/authentication.http`
  - runtime/config sources: `src/main/resources/application.properties`, `src/main/resources/application-prod.properties`, `src/main/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewService.java`, `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecurityConfiguration.java`, `src/main/java/team/jit/technicalinterviewdemo/technical/metrics/ApplicationMetrics.java`, `src/main/java/team/jit/technicalinterviewdemo/business/audit/AuditLogService.java`
  - migration truth: `src/main/resources/db/migration/V1__*.sql` through `V6__*.sql`
  - executable specs to preserve: `TechnicalOverviewControllerIntegrationTests`, `HttpTracingIntegrationTests`, `CachingAndMetricsTests`, `AuditLogIntegrationTests`, `SecurityIntegrationTests`, plus the external smoke tasks in `buildSrc`

## Locked Decisions And Assumptions
- User decisions:
  - use `ROADMAP.md` only as the source of planned work, not as a substitute for a real spec
  - create a new `ai/PLAN_<topic>.md` file
  - lock assumptions, compatibility promises, edge cases, and exact validation in the plan
- Planning assumptions that the executor should not revisit:
  - this roadmap item is documentation-and-governance work first, not a hidden runtime-hardening project
  - preserve the existing `1.x` contract and the frozen `1.0` posture exactly as currently documented
  - do not add Flyway undo migrations; upgrade/rollback guidance should document the current forward-only migration model and the operational consequences of that choice
  - keep the current release automation model:
    - releases come from `main`
    - annotated tags trigger the GitHub Actions `Release` workflow
    - `externalSmokeTest` remains the production-like packaged-image validation gate
    - the immutable GHCR tag should be documented using the current short-SHA format (`sha-<12-char-commit>`) rather than an invented longer form
  - healthy-runtime documentation should describe current observable signals, not invent new ones:
    - health/liveness/readiness responses
    - Prometheus metrics, including `technical.interview.demo.*`
    - append-only audit-log writes in the database for supported state-changing flows
    - JDBC session persistence tables for authenticated browser sessions
  - no OpenAPI baseline refresh is expected, and no HTTP contract docs should change unless execution uncovers a real contradiction in the existing public documentation

## Affected Artifacts
- Tests and executable specs to preserve:
  - `src/test/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewControllerIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/logging/HttpTracingIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/CachingAndMetricsTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/audit/AuditLogIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/security/SecurityIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/docs/ApiDocumentationTests.java` as the generator for the technical-endpoint docs page
  - `buildSrc/src/main/kotlin/team/jit/technicalinterviewdemo/build/ExternalTestingConventionsPlugin.kt`
  - `buildSrc/src/main/kotlin/team/jit/technicalinterviewdemo/build/ExternalSmokeEnvironmentUpTask.kt`
- Docs and guidance likely to change:
  - `ai/RELEASES.md`
  - `README.md`
  - `SETUP.md`
  - `CONTRIBUTING.md`
  - `src/docs/asciidoc/technical-endpoints.adoc` if healthy-runtime expectations should also appear in generated docs
- HTTP examples:
  - `src/test/resources/http/technical-endpoints.http` only if the runbook needs a clearer reviewer-facing readiness/metrics example
  - `src/test/resources/http/authentication.http` only if the session-persistence troubleshooting guidance needs a clearer example flow
- OpenAPI:
  - `src/test/resources/openapi/approved-openapi.json` should remain unchanged
  - no `refreshOpenApiBaseline` run is expected unless execution accidentally changes published contract wording in generated OpenAPI output
- Source/config files that define the documented behavior today and may need targeted wording alignment only if a contradiction is found:
  - `.github/workflows/ci.yml`
  - `.github/workflows/release.yml`
  - `scripts/release/render-release-notes.ps1`
  - `build.gradle.kts`
  - `src/main/resources/application.properties`
  - `src/main/resources/application-prod.properties`
  - `src/main/resources/db/migration/V1__*.sql` through `V6__*.sql`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewService.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecurityConfiguration.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/metrics/ApplicationMetrics.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/audit/AuditLogService.java`
- Build or benchmark checks:
  - `.\gradlew.bat build`
  - `.\gradlew.bat externalSmokeTest -PexternalSmokeImageName=technical-interview-demo -PdockerImageName=technical-interview-demo`
  - `.\scripts\release\render-release-notes.ps1` dry-run against an existing release section if release-note guidance changes
  - no `.\gradlew.bat gatlingBenchmark` rerun is expected unless execution changes benchmark-triggering behavior instead of only documenting it

## Execution Milestones
### Milestone 1: Freeze The Maintainer Release Checklist
- Goal:
  - turn the current release flow into a decision-complete checklist that maintainers and agents can follow without re-deriving the release gates from multiple files
- Files to update:
  - `ai/RELEASES.md`
  - `README.md`
  - `CONTRIBUTING.md`
  - `SETUP.md` only with a short pointer or local reproduction note, not a duplicated AI-only release transcript
- Behavior to preserve:
  - releases still come from `main`
  - annotated tags remain the trigger for remote publication
  - `CHANGELOG.md` remains the source of release notes
  - external smoke validation remains part of the release flow
- Exact deliverables:
  - add an explicit release checklist section to `ai/RELEASES.md` that includes:
    - review new Flyway migrations under `src/main/resources/db/migration/`
    - confirm OpenAPI compatibility/build status
    - confirm whether `gatlingBenchmark` is required based on changed behavior
    - prepare `CHANGELOG.md`, roadmap cleanup, and plan archival
    - create/verify the annotated tag
    - verify remote workflow success, GitHub Release publication, and both GHCR tags
  - align `README.md` and `CONTRIBUTING.md` with concise release-model wording so they do not contradict the AI release guide
  - make the immutable-image-tag wording match the current short-SHA workflow behavior instead of leaving it ambiguous

### Milestone 2: Document Healthy Runtime Signals
- Goal:
  - document what “healthy” means for the supported operational surface and the existing observability signals without changing runtime behavior
- Files to update:
  - `SETUP.md`
  - `README.md` only if a short high-level summary is needed
  - `src/docs/asciidoc/technical-endpoints.adoc` only if the generated endpoint docs should carry a brief healthy-runtime note
  - `src/test/resources/http/technical-endpoints.http` only if additional readiness or metrics examples are needed
- Behavior to preserve:
  - `/actuator/health`, `/actuator/health/liveness`, `/actuator/health/readiness`, and `/actuator/info` remain the supported operational contract
  - `/actuator/prometheus` remains deployment-scoped for trusted scraping
  - audit logs remain database-backed side effects of state-changing book and localization operations, not a new public endpoint
- Exact deliverables:
  - add a “healthy runtime” section that explains:
    - expected `UP` responses from health/liveness/readiness
    - readiness as the authoritative rollout/smoke-check signal
    - expected Prometheus metrics availability, including `technical.interview.demo.*`
    - what healthy audit logging looks like after supported write operations
    - how the JDBC session tables fit into healthy authenticated-session behavior
  - tie the prose directly to the current test-backed/runtime-backed truth instead of aspirational platform language

### Milestone 3: Expand Troubleshooting And Upgrade/Rollback Runbooks
- Goal:
  - close the remaining operator-facing documentation gaps around OAuth, PostgreSQL, session persistence, and schema-aware releases
- Files to update:
  - `SETUP.md`
  - `README.md` only if a short pointer to the runbook sections is needed
  - `src/test/resources/http/authentication.http` only if the troubleshooting guidance needs a clearer session verification example
- Behavior to preserve:
  - OAuth remains optional through the `oauth` profile
  - JDBC-backed sessions remain the only supported authenticated browser-session mechanism
  - Flyway remains the migration engine and still runs automatically at startup
- Exact deliverables:
  - add explicit troubleshooting coverage for:
    - OAuth profile enabled but misconfigured GitHub client credentials/callback
    - PostgreSQL connectivity failures in local, container-smoke, and cluster deployment paths
    - Spring Session JDBC persistence failures, including expected `SPRING_SESSION` / `SPRING_SESSION_ATTRIBUTES` tables
  - add an upgrade/rollback section that documents:
    - reviewing new Flyway migration files before release
    - validating the target image with `build` and `externalSmokeTest`
    - rolling forward with versioned container tags or Helm values
    - rollback expectations when the application image can be reverted safely versus when database restore/manual intervention is required because migrations are forward-only
  - keep the rollback guidance honest about the absence of automatic down migrations or zero-downtime schema orchestration in this demo repo

## Edge Cases And Failure Modes
- If the new docs imply a different public contract tier for `/actuator/prometheus`, `/oauth2/authorization/github`, or any supported operational endpoint, the plan will reopen the just-frozen `1.0` posture and become self-contradictory.
- If the release checklist documents a different immutable image-tag format than `.github/workflows/release.yml` actually publishes, maintainers will verify the wrong artifact after release.
- If the runbook promises automatic Flyway rollback or reversible schema changes, it will misrepresent the current repo capabilities and create unsafe operational expectations.
- If troubleshooting guidance implies JWTs, bearer tokens, or CSRF token choreography, it will contradict the current browser-session/OAuth demo model documented in `authentication.http` and `SecurityConfiguration`.
- If “healthy runtime” guidance references metrics or audit behavior that are not backed by `ApplicationMetrics`, `CachingAndMetricsTests`, or `AuditLogIntegrationTests`, the docs will drift from executable truth.
- If execution edits generated technical docs or reviewer HTTP examples, it must not trigger accidental OpenAPI or contract-surface changes.
- If the plan drifts into workflow redesign instead of docs/governance alignment, it risks introducing unnecessary CI/CD churn for a roadmap item that is primarily about documenting and standardizing existing behavior.

## Validation Plan
- Commands to run:
  - `.\gradlew.bat build`
  - `.\gradlew.bat externalSmokeTest -PexternalSmokeImageName=technical-interview-demo -PdockerImageName=technical-interview-demo`
  - `.\scripts\release\render-release-notes.ps1 -Tag v1.0.0 -TagImageReference ghcr.io/example-owner/technical-interview-demo:v1.0.0 -ShaImageReference ghcr.io/example-owner/technical-interview-demo:sha-example123456 -PackagePageUrl https://github.com/example-owner/technical-interview-demo/pkgs/container/technical-interview-demo` if release-note helper or release-checklist docs are updated
- Tests to add or update:
  - no new runtime tests are expected for a docs/governance-only execution
  - if execution changes generated AsciiDoc endpoint pages materially, keep `ApiDocumentationTests` and the build green without changing endpoint behavior
  - if execution finds a real release-workflow mismatch and changes `.github/workflows/release.yml` or helper scripts, add/update the smallest focused validation needed for that helper behavior rather than broadening into workflow redesign
- Docs or contract checks:
  - `ai/RELEASES.md`, `README.md`, `CONTRIBUTING.md`, and `SETUP.md` must agree on release flow terminology and the role of `main`, tags, changelog sections, and remote verification
  - `SETUP.md`, `technical-endpoints.adoc`, `technical-endpoints.http`, and `authentication.http` must use the same language for health/readiness, deployment-scoped Prometheus, optional OAuth, CSRF-disabled session flows, and session-cookie troubleshooting
  - no OpenAPI baseline refresh unless execution unintentionally changes generated OpenAPI output and explicitly decides to correct it
  - no benchmark rerun unless execution changes a benchmark-triggering behavior instead of merely documenting current expectations
- Manual verification steps:
  - read the final release checklist top to bottom and confirm that each step maps to an existing repo command, file, or workflow behavior
  - confirm the documented healthy-runtime section matches what the current runtime and tests actually expose:
    - readiness endpoint is the rollout/smoke signal
    - Prometheus metrics include app-specific `technical.interview.demo.*` meters
    - audit logging is database-backed for supported write operations
    - authenticated sessions use JDBC-backed Spring Session tables
  - confirm the upgrade/rollback section does not promise any capability the repo does not implement today

## Better Engineering Notes
- This plan should stay documentation-first. If execution finds that the repo lacks a necessary observable signal for one of the promised runbook steps, stop and spin a separate, narrow implementation plan rather than hiding runtime work inside this documentation task.
- Do not introduce new operational abstractions, management ports, custom health groups, migration tooling, or orchestration logic just to make the docs look more “production-grade.”
- If the current GitHub Actions workflows or release-note helper contain a real factual mismatch that blocks truthful documentation, fix that mismatch narrowly and document why it was necessary.
- When this work is later released, remove the completed roadmap items from `ROADMAP.md` rather than leaving stale operational-readiness tasks active.

## Validation Results
- Milestone 1 completed:
  - updated `ai/RELEASES.md` with a concrete maintainer release checklist
  - aligned release wording in `README.md` and `CONTRIBUTING.md`
  - updated `CHANGELOG.md` under `## [Unreleased]`
  - committed as `43e946c` (`docs: add post-1.0 release checklist guidance`)
- Milestone 2 completed:
  - documented healthy runtime expectations in `SETUP.md`
  - updated `CHANGELOG.md` under `## [Unreleased]`
  - committed as `ac56be7` (`docs: define healthy runtime expectations`)
- Milestone 3 completed:
  - added upgrade/rollback guidance plus OAuth, PostgreSQL, and Spring Session troubleshooting in `SETUP.md`
  - updated `CHANGELOG.md` under `## [Unreleased]`
- Validation commands run on 2026-05-02:
  - `.\scripts\release\render-release-notes.ps1 -Tag v1.0.0 -TagImageReference ghcr.io/example-owner/technical-interview-demo:v1.0.0 -ShaImageReference ghcr.io/example-owner/technical-interview-demo:sha-example123456 -PackagePageUrl https://github.com/example-owner/technical-interview-demo/pkgs/container/technical-interview-demo`
    - result: passed and rendered the `v1.0.0` changelog section plus release metadata with tag image, immutable image, and package-page link
  - `.\gradlew.bat build`
    - result: passed with `JAVA_HOME=C:\Users\kamki\.jdks\azul-25.0.3`
  - `.\gradlew.bat externalSmokeTest -PexternalSmokeImageName=technical-interview-demo -PdockerImageName=technical-interview-demo`
    - result: passed with `JAVA_HOME=C:\Users\kamki\.jdks\azul-25.0.3`; the smoke environment reached readiness, Flyway schema history was verified, and the 5 external smoke tests passed
- Release preparation on 2026-05-02:
  - updated `CHANGELOG.md` with the `v1.0.1` release section and reset `## [Unreleased]`
  - updated `ROADMAP.md` so the completed post-`1.0` operational-readiness work is no longer tracked as active roadmap work
  - archived this executed plan under `ai/archive/` as part of the release change
  - reran `.\gradlew.bat build` from `main` for the release candidate before creating the release commit and annotated tag
    - result: passed with `JAVA_HOME=C:\Users\kamki\.jdks\azul-25.0.3`
- Scope checks:
  - no OpenAPI baseline refresh was needed
  - no `gatlingBenchmark` rerun was needed because execution stayed documentation-only
  - existing contract behavior was preserved

## User Validation
- Confirm that the repository clearly answers these questions without opening the code:
  - What exact checklist must a maintainer follow before tagging a release?
  - What does a healthy runtime look like for health/readiness, metrics, audit logging, and session persistence?
  - How should an operator troubleshoot OAuth setup, PostgreSQL connectivity, and Spring Session persistence issues?
  - What is the supported upgrade path for a new version, and what are the honest rollback limits when Flyway migrations are involved?
