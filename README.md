# Technical Interview Demo

[![CI](https://github.com/kamkie/technical-interview-demo/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/kamkie/technical-interview-demo/actions/workflows/ci.yml)
[![Codecov](https://codecov.io/gh/kamkie/technical-interview-demo/graph/badge.svg)](https://codecov.io/gh/kamkie/technical-interview-demo)

`AGENTS.md` is the AI-facing counterpart of this file. Keep both files aligned when project behavior, public API contracts, formatter usage, logging/tracing behavior, or quality gates change.

## Overview

Small Spring Boot demo application for technical interview exercises.

Current scope:

- internal/devops validation surfaces at `GET /`, `GET /hello`, `GET /docs`, `GET /v3/api-docs`, `GET /v3/api-docs.yaml`, and `GET /actuator/**`
- `Book` API under `/api/books` with pagination, filtering, optimistic locking, and category assignment
- `Category` API under `/api/categories` with list, create, rename, and guarded delete semantics
- `Localization` API under `/api/localizations` with CRUD plus collection filtering by `messageKey` and `language`
- same-site browser session/bootstrap API under `/api/session` for a separate first-party UI behind one public origin
- authenticated account API under `/api/account`
- ADMIN audit review API at `/api/audit-logs`
- configuration-driven demo data bootstrap with production-safe defaults
- OAuth 2.0 protected write endpoints with JDBC-backed HTTP sessions
- append-only audit logging for state-changing `Book` and `Localization` operations plus admin review access
- generated REST Docs and an approved OpenAPI baseline
- PostgreSQL runtime profiles and PostgreSQL-backed integration tests via Testcontainers
- request tracing, structured logging, in-memory caches, application-specific Prometheus metrics, and tracked Gatling baselines

Primary goal: keep the codebase small, readable, and easy to reason about.

## Compatibility Model

This repository remains a small interview-demo reference app.

It does **not** try to solve every security and deployment concern in-app.

The current supported contract in this file, the generated docs, the HTTP examples, and the approved OpenAPI baseline describe the app-owned HTTP surface.
Internet reachability outside that contract, including keeping non-`/api/**` paths private, is owned by deployment and reverse-proxy configuration.

## Spec-Driven Development

This project should be changed spec-first, not implementation-first.

Working rule:

1. Define or update the intended behavior in a spec artifact.
2. Implement the smallest code change that satisfies that spec.
3. Verify the executable and published specs stay aligned.

Authoritative spec artifacts:

- integration tests and REST Docs tests under `src/test/java/`
- generated AsciiDoc sources under `src/docs/asciidoc/`
- approved OpenAPI baseline at `src/test/resources/openapi/approved-openapi.json`
- HTTP example collections under `src/test/resources/http/`
- this `README.md` for the supported human-facing API/runtime contract
- `ROADMAP.md` for planned work and explicitly selected active development that is not yet part of the contract; checked `[x]` items are selected work, not completed history
- `CHANGELOG.md` for released history

What that means in practice:

- If a public API changes, update tests, REST Docs, OpenAPI, and HTTP examples in the same change.
- If a refactor should not change behavior, the existing specs should keep passing without contract edits.
- If the intended behavior is unclear, define the missing spec first or stop and clarify it before coding.

## Supported Contract

Supported external application contract:

- public or bootstrap paths under `/api/**`:
  - `GET /api/books`
  - `GET /api/books/{id}`
  - `GET /api/categories`
  - `GET /api/localizations`
  - `GET /api/localizations/{id}`
  - `GET /api/session`
  - `POST /api/session/logout`
  - `GET /api/session/oauth2/authorization/{registrationId}`
- externally reachable authenticated paths under `/api/**`:
  - `GET /api/account`
  - `PUT /api/account/language`
  - `GET /api/audit-logs`
  - `GET /api/operator/surface`
  - `POST /api/books`
  - `PUT /api/books/{id}`
  - `DELETE /api/books/{id}`
  - `POST /api/categories`
  - `PUT /api/categories/{id}`
  - `DELETE /api/categories/{id}`
  - `POST /api/localizations`
  - `PUT /api/localizations/{id}`
  - `DELETE /api/localizations/{id}`
- externally reachable identity-provider callback path:
  - `GET /api/session/login/oauth2/code/{registrationId}`

Internal or devops-only validation surface:

- `GET /`
- `GET /hello`
- `GET /docs`
- `GET /v3/api-docs`
- `GET /v3/api-docs.yaml`
- `GET /actuator/info`
- `GET /actuator/health`
- `GET /actuator/health/liveness`
- `GET /actuator/health/readiness`
- `GET /actuator/prometheus`

Supported technical bootstrap:

- `GET /api/session`
  - public same-site browser session/bootstrap endpoint for the separate first-party UI
  - returns `authenticated`, `accountPath`, `loginProviders[]`, `logoutPath`, `sessionCookie`, and `csrf.enabled`
  - `loginProviders` is `[]` when the optional `oauth` profile is inactive
  - each `loginProviders[]` item exposes `registrationId`, `clientName`, and a relative `authorizationPath`
- `GET /api/session/oauth2/authorization/{registrationId}`
  - interactive login entry point when the optional `oauth` profile is active
  - resolved from configured providers (`github`, `oidc`, or additional configured registration ids)
- `GET /api/session/login/oauth2/code/{registrationId}`
  - reverse-proxy callback path expected from the external identity provider
- `POST /api/session/logout`
  - public idempotent logout endpoint for the same-site browser session contract
  - clears the configured session cookie and invalidates the current server-side session when present
- `APP_BOOTSTRAP_SEED_DEMO_DATA`
  - controls startup seeding for demo categories, books, and localization messages
  - defaults to `true` in `local` and `test`, defaults to `false` in `prod`

Security summary:

- public supported external endpoints: read-only `/api/books/**`, `GET /api/categories`, read-only `/api/localizations/**`, `GET /api/session`, `POST /api/session/logout`, and `GET /api/session/oauth2/authorization/{registrationId}`
- authenticated session required on the external `/api/**` surface: `/api/account`, `PUT /api/account/language`, `GET /api/audit-logs`, `GET /api/operator/surface`, and the state-changing book, category, and localization endpoints
- `ADMIN` role required: audit log review, operator surface access, category create/update/delete, and localization create/update/delete
- interactive login and provider callbacks stay under `/api/session/**` when the `oauth` profile is active
- the supported first-party browser contract assumes one public origin via reverse proxy; cross-origin browser support and CORS guarantees are not part of the supported contract
- conservative browser security headers apply to application responses, with HSTS added on secure `prod` requests

Contract notes:

- `GET /api/audit-logs` is paginated and supports optional exact `targetType`, `action`, and `actorLogin` filters
- `GET /api/operator/surface` returns one ADMIN-only payload that combines recent audit history, runtime diagnostics, and operational status links
- `GET /api/session` is the supported same-site UI bootstrap/state endpoint, while `GET /api/account` remains the authenticated persisted-profile endpoint
- OAuth success redirects to `/` and failures redirect to `/?login=failed` for the separate first-party UI
- `POST /api/session/logout` always returns `204 No Content`, clears the configured session cookie, and is safe to call even when no session exists
- `DELETE /api/categories/{id}` fails with a localized conflict if the category is still assigned to one or more books
- `GET /api/books` is paginated and supports text, category, and year filters
- `GET /api/localizations` is paginated and supports optional exact `messageKey` and `language` filters
- localized error responses include `messageKey`, localized `message`, and resolved `language`
- protected API `401` and `403` responses use that same localized `ProblemDetail` structure
- authenticated-user preferred language is the last localization fallback before English

## Quality Model

Standard verification command:

```powershell
.\gradlew.bat build
```

Exception:

- when every changed file matches `*.md`, manual consistency review is sufficient and `.\gradlew.bat build` or other heavyweight validation commands are not required unless you explicitly want broader validation

Use `SETUP.md` for environment prerequisites and local tooling details.

The standard build includes Spotless, PMD, tests, JaCoCo thresholds, REST Docs generation, OpenAPI compatibility verification, boot jar creation, and Docker image build.

Supply-chain verification is part of the standard build:

- `.\gradlew.bat build` also runs Gradle-owned static application security, dependency vulnerability, Docker-image vulnerability, and SBOM generation tasks
- `.\gradlew.bat staticSecurityScan` runs SpotBugs plus FindSecBugs directly when you want the code-focused security gate without the full lifecycle
- `.\gradlew.bat vulnerabilityScan` runs the two scan tasks directly when you want the security checks without the full lifecycle
- `.\gradlew.bat sbom` runs the CycloneDX SBOM tasks directly when you want SBOM generation without the full lifecycle
- GitHub CodeQL runs separately in GitHub Actions with repository-owned configuration and uploaded code-scanning results; it complements the Gradle-owned SpotBugs plus FindSecBugs and PMD gates instead of replacing them
- PMD reports are written under `build/reports/pmd/` and static application security reports are written under `build/reports/security/static/` as XML and HTML
- dependency and image vulnerability scan reports are written under `build/reports/security/` as JSON, SARIF, and summary text files
- application and image SBOM reports are written under `build/reports/sbom/application/application.cyclonedx.json` and `build/reports/sbom/image/image.cyclonedx.json`
- unsuppressed `HIGH` and `CRITICAL` findings fail the relevant scan task and therefore fail the build
- suppressions must be explicit and reviewable through `config/security/trivy.ignore`, `config/security/spotbugs-security-include.xml`, and `config/security/spotbugs-security-exclude.xml`

Additional change-sensitive checks:

- refresh the approved OpenAPI baseline intentionally with `./gradlew refreshOpenApiBaseline` only when the reviewed API contract changed
- rerun `./gradlew gatlingBenchmark` when changing book list/search behavior, localization lookup behavior, or OAuth/session startup behavior

## Release Model

- application version is derived from the nearest reachable annotated git tag
- use semantic version tags in the form `vMAJOR.MINOR.PATCH` for stable releases or `vMAJOR.MINOR.PATCH-PRERELEASE` for prereleases such as `v2.0.0-M1`, `v2.0.0-ALFA1`, `v2.0.0-BETA2`, or `v2.0.0-RC1`
- keep release numbers increasing in `git log --first-parent` order
- record human-facing release history in `CHANGELOG.md`
- tag-driven releases publish cumulative GitHub Release notes from the new tag section back to the previous published GitHub Release tag section in `CHANGELOG.md`
- release-note rendering fails closed when the previous published GitHub Release boundary or required `CHANGELOG.md` sections cannot be derived unambiguously
- complete local implementation, validation, and review work before pushing a branch or opening an implementation PR
- treat PR creation as the final implementation handoff, not as a substitute for local execution
- maintainers prepare releases only after the approved implementation PR has been merged onto validated `main`
- before tagging, maintainers classify migration impact with `pwsh ./scripts/release/get-release-migration-impact.ps1 -PreviousReleaseTag <previous-tag> -CurrentRef HEAD`, review any changed SQL migrations plus their JSON sidecars under `src/main/resources/db/migration/metadata/`, confirm whether `gatlingBenchmark` is required, and complete changelog/roadmap/plan cleanup
- after pushing a release tag, maintainers verify the remote workflow published both the semantic image tag and the immutable short-SHA image tag, plus a keyless signature and provenance attestation for the immutable published digest
- release-image trust is digest-first: semantic and short-SHA tags are convenience references, but authenticity checks should target the published `ghcr.io/<owner>/<repo>@sha256:...` digest

Checked-in Flyway rollout model:

- `expand`: additive schema changes intended for `db-first` rollout while old and new app instances overlap
- `contract`: cleanup or constraint-tightening work that should happen only after the new app is fully deployed, usually `app-first` or `out-of-band`
- `backfill`: data rewrite or population work that may need an `out-of-band` step even when the schema itself is additive
- `breaking`: incompatible schema or data moves that are not safe for mixed-version rollout
- release impact is `rolling-compatible` only when every changed migration is marked `rollingCompatible=true` and `rollbackPosture=image-only`
- any changed migration marked non-rolling-compatible or `rollbackPosture=forward-fix-or-restore` makes the release `restore-sensitive`

## CI/CD And Deployment

Supported delivery path:

- GitHub Actions is the repository CI/CD platform
- pull requests to `main` and pushes to `main` run the `CI` workflow, which executes `./gradlew build` and `./gradlew externalSmokeTest`
- a dedicated `CodeQL` workflow runs on pull requests, pushes to `main`, and a weekly schedule using repository-owned configuration; its SARIF results appear in GitHub code scanning and are additive to the Gradle-owned SpotBugs plus FindSecBugs and PMD gates
- `externalSmokeTest` now verifies the packaged docs HTML, OpenAPI JSON/YAML endpoints, and one JDBC-backed authenticated `GET /api/account` session path in addition to the existing public/readiness smoke checks
- the scheduled `Post-Deploy Smoke` workflow runs `./gradlew scheduledExternalCheck` every six hours and on manual dispatch, using `EXTERNAL_CHECK_BASE_URL` plus optional JDBC secrets for deeper JDBC-backed session and Flyway checks
- manual `Post-Deploy Smoke` runs can also assert the exact published release identity through `build.version` and `git.shortCommitId`, plus the documented prod runtime posture through `runtime.activeProfiles`, `configuration.session.storeType`, `configuration.session.timeout`, and `configuration.security.csrfEnabled=false`
- the `CI` workflow uploads `build/reports/jacoco/test/jacocoTestReport.xml` to Codecov after the Gradle build, so the repository must be onboarded for Codecov uploads before that signal is expected to pass consistently
- Dependabot opens grouped weekly update PRs for Gradle, GitHub Actions, and Docker, and those PRs are expected to pass the same `CI` workflow before merge
- the `CI` workflow uploads generated vulnerability scan artifacts from `build/reports/security/`, static-analysis artifacts from `build/reports/pmd/` plus `build/reports/security/static/`, and SBOM artifacts from `build/reports/sbom/` so blocked runs remain reviewable
- stable and prerelease semantic version tags trigger the `Release` workflow, which builds, scans, and generates SBOMs for the tagged image with Gradle, uploads security, static-analysis, and SBOM artifact bundles, validates the image with `./gradlew externalSmokeTest`, publishes it to GitHub Container Registry as `ghcr.io/<owner>/<repo>:<tag>` and `ghcr.io/<owner>/<repo>:sha-<12-char-commit>`, then signs the pushed immutable digest and publishes provenance attestation for that same digest before creating cumulative GitHub Release notes from the previous published GitHub Release tag boundary in `CHANGELOG.md`
- when the tag includes a `-PRERELEASE` suffix, the workflow publishes the GitHub release entry as a prerelease while still using the full tag for image, changelog, and post-deploy identity checks
- the `Release` workflow step summary now records the semantic tag, short-SHA tag, digest reference, and the exact manual `Post-Deploy Smoke` inputs maintainers should use before promotion
- deployment artifacts are provided as:
  - Docker image
  - vendor-neutral Kubernetes manifests under `k8s/base` with a local overlay under `k8s/overlays/local`, including a checked-in HPA and pod disruption budget
  - optional Fluent Bit log-forwarding example under `k8s/log-forwarding/fluent-bit` for multiline Java exception shipping
  - Helm chart under `helm/`, with autoscaling and pod disruption budget defaults enabled for deployment-style installs and disabled in `values-local.yaml` for the single-replica local path
  - monitoring and alerting assets for Prometheus, Grafana, and Alertmanager

Required deployment environment variables:

- `DATABASE_HOST`
- `DATABASE_PORT`
- `DATABASE_NAME`
- `DATABASE_USER`
- `DATABASE_PASSWORD`

Optional deployment environment variables:

- `SESSION_COOKIE_SECURE` with a secure-by-default value of `true`
- `GITHUB_CLIENT_ID`
- `GITHUB_CLIENT_SECRET`
- `OIDC_CLIENT_ID`
- `OIDC_CLIENT_SECRET`
- `OIDC_ISSUER_URI`
- `ADMIN_LOGINS`

Current production posture:

- `prod` remains the deployment profile baseline
- OAuth login remains opt-in through the `oauth` profile and requires at least one configured provider (`github` or `oidc`) with client credentials, with `OIDC_ISSUER_URI` required for OIDC
- `ADMIN_LOGINS` remains the environment-driven admin bootstrap mechanism and is validated as a comma-separated list of external login identifiers when present
- demo data bootstrap remains disabled by default in `prod` through `APP_BOOTSTRAP_SEED_DEMO_DATA=false`
- `SESSION_COOKIE_SECURE` remains optional with a secure-by-default value of `true`, but disabling it in `prod` is rejected at startup
- `server.forward-headers-strategy=framework` is part of the supported `prod` posture so trusted reverse-proxy headers drive redirect and scheme handling correctly
- `prod` enforces a 15 minute session timeout, one active session per login, and login rejection when that session cap is already reached
- browser-session write flows keep CSRF disabled as a deliberate demo tradeoff for reviewer-friendly session-based API exercise flows
- production logging uses `INFO` at the root logger and emits structured JSON Lines on stdout (`logging.structured.format.console=logstash`) while keeping trace export runtime-configurable through standard OTLP environment variables
- non-`/api/**` paths remain internal or devops-only; internet reachability for those paths is intentionally owned by deployment and reverse-proxy configuration
- `GET /actuator/prometheus` remains available for trusted deployment scraping, but it is not part of the internet-public contract

Trusted deployment topology assumption:

- only `/api/**` is intended to be internet-reachable, via `waf -> frontend -> this application`
- `/`, `/hello`, `/docs`, OpenAPI publication, and actuator paths are expected to stay private to trusted internal or devops access paths
- Prometheus scraping is expected to happen from trusted internal infrastructure such as cluster-local monitoring, not from arbitrary public clients

## Operational Data Recovery Expectations

The repository does not implement backup orchestration, but operating a production-style deployment of this app requires an explicit backup and restore posture:

- create backup snapshots before each migration-bearing release rollout and on a regular schedule between releases
- keep retention long enough to recover from delayed detection failures; minimum expectation is:
  - last 7 daily backups
  - last 4 weekly backups
  - last known-good pre-release backup for each migration-bearing rollout still within your rollback window
- test restore viability routinely, not only backup creation success

Recovery posture by release class:

- `none`: no migration SQL changed since the previous release tag, so image rollback is normally sufficient when the deployment fails
- `rolling-compatible`: changed migrations are safe for mixed-version rollout and marked `rollbackPosture=image-only`, so image rollback stays the normal first response
- `restore-sensitive`: at least one changed migration is not rolling-compatible or requires `forward-fix-or-restore`, so maintainers should expect database restore evidence or a forward-fix plan before promotion

Restore-drill baseline for this repository:

1. restore a recent backup to a separate PostgreSQL instance
2. run `pwsh ./scripts/release/invoke-restore-drill.ps1 -ImageReference <image> -BaseUrl http://127.0.0.1:18080 -JdbcUrl <jdbc-url> -JdbcUser <user> -JdbcPassword <password> -ExpectedBuildVersion <tag> -ExpectedShortCommitId <12-char-sha>`
3. confirm the drill proves readiness, `build.version`, `git.shortCommitId`, `prod` profile activation, JDBC session storage, the `15m` session timeout, `csrfEnabled=false`, Flyway history, and an authenticated `GET /api/account` check against the restored database
4. keep the restore evidence with the release notes for every `restore-sensitive` rollout

Use the raw manifests under `k8s/` when you want explicit repo-owned YAML. Use the Helm chart under `helm/technical-interview-demo` when you want the same deployment contract packaged behind values files.

Monitoring support uses the upstream `kube-prometheus-stack` Helm chart plus repo-owned ServiceMonitor, alert-rule, Grafana dashboard, and Alertmanager example assets under `k8s/monitoring` and `monitoring/`.
The checked-in monitoring contract now covers auth failures, session-backed account errors, Flyway-style startup crash loops, database pool saturation and timeouts, and elevated 5xx rates.
The optional `k8s/log-forwarding/fluent-bit` bundle shows one deployment-facing path for shipping those stdout JSON lines while recombining multiline Java exception stack traces before forwarding.

## Project Map

- `SETUP.md`: developer onboarding, local environment setup, OAuth setup, Docker workflow, and troubleshooting
- `ai/ARCHITECTURE.md`: AI-facing architecture map, API shape summary, and structural guidance
- `ai/BUSINESS_MODULES.md`: AI-facing business-feature package map and ownership guide
- `ai/PLAN.md`: AI-facing planning rules for execution plans and milestone-ready plan files
- `ai/EXECUTION.md`: AI-facing single-agent execution workflow for local implementation, validation, and PR-ready handoff
- `ai/RELEASES.md`: AI-facing maintainer release workflow after approved work is merged onto `main`
- `ai/PROMPTS.md`: reusable prompt library for repository-specific planning, implementation, release, and multi-agent requests; its prompt entry titles can be used as shorthand when the request includes the needed concrete context
- `ai/WORKFLOW.md`: Codex multi-agent workflow, task integration, and release handoff rules for this repository
- `ai/CODE_STYLE.md`: AI-facing code-style and change-shaping guidance for repo edits
- `ai/TESTING.md`: AI-facing testing and validation guidance
- `ai/REVIEWS.md`: AI-facing code-review and security-review guidance
- `ai/DOCUMENTATION.md`: AI-facing documentation ownership and update guidance
- `AGENTS.md`: AI-facing project rules and spec-driven working contract
- `ROADMAP.md`: active roadmap only; `[x]` marks selected work, not completed history
- `CHANGELOG.md`: released history only
- `CONTRIBUTING.md`: contributor workflow and review expectations
- `k8s/`: raw Kubernetes deployment and monitoring manifests
- `helm/technical-interview-demo/`: Helm chart for the application deployment contract
- `monitoring/`: upstream-stack values, Grafana dashboards, and Alertmanager examples
- `src/docs/asciidoc/`: generated-doc source pages
- `src/test/resources/openapi/approved-openapi.json`: approved OpenAPI baseline
- `src/test/resources/http/`: runnable HTTP examples grouped by area
- `performance/baselines/phase-9-local.json`: tracked local Gatling baseline

## Local Development

For onboarding, IDE setup, Docker usage, OAuth setup, local PostgreSQL, and troubleshooting, use `SETUP.md`.

## Development Constraints

- preserve the demo nature of the project
- prefer direct Spring MVC, Spring Data JPA, and `@Service` code over extra abstraction
- keep package names under `team.jit.technicalinterviewdemo`
- avoid unnecessary new infrastructure or libraries unless there is a real requirement
- do not remove the existing `hello` or `book` endpoints unless the demo scope is intentionally changing
- add or update tests when API behavior changes

## Definition Of Done

A change is complete when:

- the behavior is captured in the relevant spec artifacts
- implementation, tests, docs, and OpenAPI stay aligned
- the application still starts when the change touches executable artifacts
- `./gradlew build` passes, unless every changed file matches `*.md` and manual consistency review is sufficient for that markdown-only change
