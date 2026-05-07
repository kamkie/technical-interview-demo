# Changelog

All notable released changes to this project are documented in this file.

The format is based on Keep a Changelog and the project uses semantic version tags in the form `vMAJOR.MINOR.PATCH` for stable releases or `vMAJOR.MINOR.PATCH-PRERELEASE` for prereleases.
The Gradle build version is derived from the nearest reachable annotated git tag, and release numbers must increase in `git log --first-parent` order.

## [Unreleased]

### Changed

- Restructured AI guidance ownership by moving active plans under `ai/plans/active/`, renaming the planning guide to `ai/PLANNING.md`, adding `ai/PLAN_EXECUTION.md` for whole-plan execution, narrowing `ai/EXECUTION.md` to ad hoc and single-milestone work, and replacing reusable starter storage with `ai/TASK_LIBRARY.md`, `ai/task-library/`, and `scripts/ai/get-task.ps1`.
- Refreshed the on-demand post-compaction AI guideline evaluation report for the new owner-guide and task-library structure.

## [v2.0.0-RC6] - 2026-05-07

### Added

- Added an on-demand post-compaction AI guideline evaluation report with a current grade, size baseline, realized workflow-compaction gains, and ranked follow-up backlog.
- Added a reusable `Evaluate AI Guidelines` prompt for grading the current AI instruction set and refreshing the post-compaction evaluation report.
- Added the manual-regression harness as a new opt-in `manualTests` Gradle source set with twelve ordered JUnit 5 + REST Assured suites, prerequisite-aware gating, lifecycle teardown that asserts `404` after delete, configurable inputs (Gradle property → environment variable → `run.properties` → interactive prompt with hidden secret entry), localhost/private-IP safety rails, and Markdown + JSON per-run reports under `ai/tmp/manual-regression/`. Invoke via `./build.ps1 manualTests`; not wired into the default build.

### Changed

- Folded business-feature ownership guidance from `ai/BUSINESS_MODULES.md` into `ai/ARCHITECTURE.md` and removed the overlapping standalone guide.
- Treat all files under `ai/` as lightweight support-file changes in `scripts/classify-changed-files.ps1`.
- Refreshed the on-demand AI guideline evaluation report with current size baselines, prompt-body drift checks, active-plan inventory risk, and ranked follow-up recommendations.
- Compacted duplicated AI-guidance wording by adding a lifecycle owner map, slimming the human-facing AI workflow guide, and routing prompt, workflow, validation, and release mechanics back to their owner guides.
- Standardized per-milestone context requirements across the plan guide, plan template, execution loop, active plans, and AI-guideline evaluation report.
- Moved the IntelliJ HTTP Client request collection from `src/test/resources/http/` to `src/manualTests/resources/http/` and updated active references in `AGENTS.md`, `CONTRIBUTING.md`, `SETUP.md`, `ai/DOCUMENTATION.md`, `ai/references/PLAN_DETAILED_GUIDE.md`, the Spotless target list, and `.gitignore`.
- Implemented the post-compaction AI-guidance findings by tightening execution read triggers, moving change-type routing detail to `ai/DOCUMENTATION.md`, splitting release runbooks into on-demand references, slimming `ai/PLAN.md`, and clarifying phase-to-guide read sets.
- Implemented Plan C from the AI guideline remix evaluation by adding section-level load triggers for documentation and architecture lookups while keeping descriptive owner guides conditional.
- Reordered `AGENTS.md` into a clearer spec-truth, onboarding, change-routing, and execution-support flow, while compacting branch and worktree expectations and keeping detailed remote-handoff mechanics in `ai/WORKFLOW.md`.
- Refined AI onboarding and execution guidance with targeted relevance scanning, context-quality checkpoints, per-milestone scope checks, and post-validation review triggers.
- Removed implemented AI workflow guidance plan entries from `ROADMAP.md` so the roadmap lists only active or planned work.

## [v2.0.0-RC5] - 2026-05-07

### Added

- Added a `v2.0.0-RC5` manual regression execution plan for supported public, authenticated, admin, and technical surfaces.
- Added a reusable `Context Report` prompt for tracking repository AI-instruction context size over time.

### Changed

- Switched Java formatting authority from the archived Eclipse-profile Spotless setup to Palantir Java Format while retaining Spotless for Kotlin, Gradle Kotlin DSL, and support-file whitespace normalization.
- Extended retained Spotless Kotlin and Gradle Kotlin DSL formatting with KtLint import ordering and unused-import enforcement.
- Aligned `buildSrc` Kotlin imports with retained KtLint ordering so the standard wrapper build passes under the Palantir/Spotless formatter setup.
- Normalized REST Docs AsciiDoc lists to explicit marker depth so IntelliJ formatting preserves structure without excluding AsciiDoc files.
- Left `.properties` files out of the generic Spotless misc formatter and added the IntelliJ EditorConfig blank-line setting so intentional blank-line separators are preserved.
- Kept YAML flow mappings and sequences compact by disabling IntelliJ spaces inside YAML braces and brackets through `.editorconfig`.
- Kept Flyway migration SQL hand-formatted with a narrow IntelliJ formatter exclusion instead of mirroring IntelliJ exclusions to Spotless targets.
- Refined AI guidance, prompt metadata, and workflow references so standing rules stay current-state, detailed prompt bodies remain on demand, and repeated workflow guidance routes to the owning guide.
- Refreshed the Gradle task-graph reference for the current wrapper commands and validation task flow.

## [v2.0.0-RC4] - 2026-05-06

### Changed

- Quoted release-workflow Gradle image-property arguments so GHCR image coordinates containing `:` are forwarded through PowerShell to Gradle as single properties.

## [v2.0.0-RC3] - 2026-05-06

### Added

- Added a draft AI execution plan for moving Java formatting to a CI-owned Spotless formatter after the `2.0` release line is accepted.

### Changed

- Constrained the PostgreSQL JDBC, Gatling Netty, and AsciidoctorJ JRuby Gradle dependency paths to patched versions for the current Dependabot alert batch without changing the public API contract.
- Relaxed the Gatling benchmark p95 regression gate to a 25% tolerance with ceiling rounding, and shared Docker application environment provisioning between the external smoke and Gatling benchmark tasks without changing their separate runtime profiles.
- Clarified AI planning workflow so creating or materially revising `ai/PLAN_*.md` files also updates `ROADMAP.md` with the active plan path and status.
- Forced GitHub Actions reruns to ignore the lightweight-only `skip_heavy_ci` shortcut and execute heavy CI validation.
- Expanded `.aiignore` to cover local OS, secret, IDE, and generated build files while keeping source packages such as `buildSrc/src/main/kotlin/team/jit/technicalinterviewdemo/build` visible.

## [v2.0.0-RC2] - 2026-05-05

### Added

- Added dotenv-aware `build.ps1` and `build.sh` Gradle entry points plus repository-local shell initialization and Gradle Java toolchain validation.
- Added `ai/ENVIRONMENT_QUICK_REF.md` as the AI-facing command wrapper reference for local Gradle execution.

### Changed

- Refreshed repository-owned toolchain pins across the container base image, Trivy, Fluent Bit, GitHub Actions, and Testcontainers PostgreSQL while preserving the published API contract.
- Updated setup, release, validation, and AI workflow guidance to use the wrapper-based command flow and current artifact verification model.
- Tightened and sorted the AI instruction set so branch/worktree rules, authoritative artifact ownership, execution workflow, reusable prompts, skills, and environment guidance each have clearer owners.

### Removed

- Removed the redundant root `AI_ENVIRONMENT_AUTOMATION.md` after integrating its durable guidance into the focused AI and setup documents.

## [v2.0.0-RC1] - 2026-05-05

### Added

- Added reusable prompt starters for planning repository-wide dependency and toolchain upgrade sweeps and for prioritizing open GitHub security and quality issues into the roadmap.

### Changed

- Clarified the `2.0` release state by making `ROADMAP.md` the owner of the release-phase status, adding a compact `Current Project State` block there, and advancing the tracked next target from `v2.0.0-RC1` to stable `v2.0.0` once RC1 was cut.
- Refined the AI guidance set so repository snapshot ownership now lives in `ai/ARCHITECTURE.md`, validation rules live in `ai/TESTING.md`, release rules live in `ai/RELEASES.md`, release-phase direction in `ai/DESIGN.md` reflects the RC1 contract freeze, and lifecycle status tables across plan files use the same `Status | Current` header wording.

## [v2.0.0-M8] - 2026-05-05

### Changed

- Cleared the remaining selected CodeQL maintainability and quality alerts without changing the published contract by replacing the deprecated Spring Security `Permissions-Policy` DSL call and the deprecated Gatling benchmark `ObjectMapper` null-exclusion setter.
- Removed the unused `@Service` advice binding from `ServiceLoggingAspect` and added focused regression coverage that keeps service-only interception pinned.

## [v2.0.0-M7] - 2026-05-05

### Added

- Added a repo-local `security-best-practices` skill with curated backend and frontend hardening references for later AI-assisted implementation work.

### Changed

- Refined the `2.0` roadmap and AI planning guidance so the remaining prerelease scope is limited to the selected pre-`v2.0.0-RC1` security hardening batch, the later `v2.0.0-RC1` freeze, and post-`2.0` follow-up work.
- Pinned the third-party GitHub Actions used by the `CI`, `Release`, `CodeQL`, and `Post-Deploy Smoke` workflows to verified full commit SHAs.
- Hardened API problem and HTTP tracing logging so attacker-controlled control characters are escaped in logs and unsafe request IDs are replaced before reuse.
- Upgraded the build-time PMD, JRuby, `commons-lang3`, and `plexus-utils` dependency paths that were flagged by the selected pre-`v2.0.0-RC1` security alerts.

## [v2.0.0-M6] - 2026-05-05

### Added

- Added a repo-local `gh-fix-ci` skill with a bundled GitHub CLI helper for inspecting failing PR checks, pulling actionable GitHub Actions failure context, and routing the resulting fix through the repository's approval, planning, and validation flow.
- Added a repo-local `gh-fix-security-quality` skill with a bundled GitHub CLI helper for inspecting open code-scanning and Dependabot alerts from the repository Security tab and routing approved fixes through the same repository-local plan and validation flow.

### Changed

- Completed the pre-`2.0` UTC timestamp migration by converting app-owned persisted timestamp storage to PostgreSQL `timestamptz`, moving the corresponding domain and response models to Java `Instant`, and publishing the affected API timestamps as UTC instants with a trailing `Z`.
- Moved checked-in Helm, Kubernetes, and monitoring manifests under `infra/`, keeping infrastructure assets outside the application packages and aligning the repo map with that ownership.
- Moved the tracked Gatling benchmark baseline into `src/gatling/resources`, updated the benchmark task fallback to derive the default baseline path from the `gatling` source set, and renamed repo-owned PMD, SpotBugs, and Trivy policy files from `config/` to `tooling/`.
- Updated setup, contributor, and AI validation guidance to prefer a single Gradle invocation such as `./gradlew build gatlingBenchmark --no-daemon` when both build and benchmark verification are required.
- Refined the human-facing and AI-facing repository guidance so the current prerelease phase, the `v2.0.0-RC1` contract-freeze milestone, UTC instant timestamp direction, durable AI-document ownership, and worker cleanup expectations are aligned across the docs.

## [v2.0.0-M5] - 2026-05-04

### Added

- Added `WORKING_WITH_AI.md` as the human-facing guide for AI-assisted discovery, planning, implementation, verification, and release preparation in this repository.
- Added repo-local `repo-plan-author` and `repo-validation-gate` skills plus `scripts/classify-changed-files.ps1` so AI and CI can classify lightweight support-file changes and skip heavyweight validation when appropriate.
- Added four repo-owned `Parallel Plans` workflow fixture plans for book search normalization, category directory cache cleanup, localization lookup cache cleanup, and operator-surface assembly cleanup.
- Added operator-local `OperatorSurfaceService` tests that pin the ADMIN guard, the nested response sections, and the 10-entry recent-audit paging limit before the internal cleanup refactor.

### Changed

- Refined `README.md`, `CONTRIBUTING.md`, and the AI guidance set under `ai/` so lifecycle phases, artifact ownership, reusable prompt titles, parallel-plan coordination, and release-preparation flow follow one consistent model.
- Expanded the `CI` workflow's lightweight-change detection so markdown-only, AI-skill, and other support-file changes can skip heavyweight build validation when the repo-owned classifier marks them as `skipHeavyValidation=true`.
- Added book-local regression coverage that pins whitespace-only search filters, trimmed text filters, and repeated category filters for `GET /api/books` ahead of the internal normalization cleanup.
- Added focused category-service coverage for normalized assignment lookups, directory-cache reuse, duplicate-name validation, and the current missing-category error text.
- Locked localization-local lookup, fallback-miss, and write-driven cache-eviction coverage ahead of the localization cache-flow cleanup.
- Consolidated book-search request validation and normalization into a feature-local criteria helper so `BookService` and `BookSearchSpecifications` follow one internal search-filter path without changing the `GET /api/books` contract.
- Simplified `CategoryService` directory-cache assembly and normalized assignment lookup flow while keeping cache names, category ordering, and the published category contract unchanged.
- Simplified localization lookup and language-scoped cache access so normalized lookup requests, fallback handling, and supported-language cache reads follow one internal flow without changing the public localization contract.
- Refactored `OperatorSurfaceService` into explicit admin-guard, audit-section, runtime-section, and operations-section assembly steps without changing the `/api/admin/operator-surface` contract.

## [v2.0.0-M4] - 2026-05-04

### Added

- Added persisted role-grant provenance plus an ADMIN-only `/api/admin/users` management API for listing persisted users and replacing managed role grants, together with aligned REST Docs, reviewer HTTP examples, and approved OpenAPI coverage.
- Added structured audit `details` payloads and auth-lifecycle audit events for login success, login failure, logout, and session-cap rejection.

### Changed

- Replaced runtime `ADMIN_LOGINS` role assignment with bootstrap-only `APP_BOOTSTRAP_INITIAL_ADMIN_IDENTITIES`, moved the admin operational APIs under `/api/admin/**`, and refreshed README, setup guidance, REST Docs, HTTP examples, OpenAPI, and monitoring assets to match the `v2.0.0-M4` prerelease contract.
- Added the planned PostgreSQL indexes for the current search and audit query shapes, and hardened the shipped Docker, Kubernetes, and Helm defaults with a pinned JRE image, non-root execution, a read-only root filesystem, a writable `/tmp` mount, a startup probe, and stricter container security contexts.

## [v2.0.0-M3] - 2026-05-04

### Added

- Added a published `1.x` to `2.0` upgrade guide plus checked-in `/api/**` public-edge reference assets for the same-site first-party UI deployment model.

### Changed

- Updated the `CI` workflow so pushes to `main` submit the Gradle dependency snapshot generated by the same validated build, keeping GitHub dependency graph data aligned with the CI-verified dependency set.
- Aligned README, setup guidance, REST Docs, and reviewer HTTP examples on the supported `GET /api/session` bootstrap, provider-aware login bootstrap, logout refresh, and CSRF-backed unsafe-write flow.
- Expanded packaged and deployed smoke guidance so JDBC-enabled runs prove `GET /api/session`, `XSRF-TOKEN` bootstrap, and an authenticated `PUT /api/account/language` write, while HTTP-only post-deploy smoke remains acceptable when JDBC access is intentionally unavailable.

## [v2.0.0-M2] - 2026-05-04

### Added

- Added a same-site browser CSRF contract for authenticated unsafe `/api/**` writes with `GET /api/session` bootstrap metadata, a readable `XSRF-TOKEN` cookie, a required `X-XSRF-TOKEN` header, a dedicated localized CSRF error contract, and shared CSRF-aware test helpers.

### Changed

- Updated unsafe write endpoint specs, REST Docs, reviewer HTTP examples, and the approved OpenAPI baseline so the supported first-party browser contract now documents CSRF-protected writes and conditional CSRF protection for authenticated-session logout.
- Extended the internal technical overview, operator diagnostics, external smoke checks, post-deploy smoke workflow, restore-drill helper output, README, and setup guidance so the documented production posture now reports CSRF enabled plus edge-or-gateway abuse-protection ownership for OAuth login bootstrap and unsafe internet-public writes.

## [v2.0.0-M1] - 2026-05-04

### Added

- Added a reusable PowerShell dotenv loader at `scripts/load-dotenv.ps1` so local shells and repo scripts can source `.env` consistently.

### Changed

- Updated AI execution guidance and setup docs to prefer the shared dotenv loader when PowerShell commands depend on local `.env` values, including Windows paths written with escaped backslashes.
- Hardened the same-site reverse-proxy session contract by moving OAuth bootstrap and callback handling under `/api/session/**`, replacing `loginPath` with provider-aware `loginProviders[]`, and rejecting removed `OAUTH_DEFAULT_PROVIDER` configuration.
- Added production forwarded-header fail-fast validation plus a fixed browser security-header baseline, and refreshed the README, setup guide, REST Docs, HTTP examples, AI design guidance, and approved OpenAPI contract so non-`/api/**` overview, docs, OpenAPI, and actuator paths are documented as internal or deployment-scoped validation surfaces.

## [v1.6.0] - 2026-05-03

### Added

- Added checked-in Flyway migration sidecar metadata guidance under `src/main/resources/db/migration/metadata/` plus a repo-owned `scripts/release/get-release-migration-impact.ps1` helper that classifies release candidates as `none`, `rolling-compatible`, or `restore-sensitive`.
- Added a repo-owned `scripts/release/invoke-restore-drill.ps1` helper for running the tagged image against a restored PostgreSQL instance and reusing `externalDeploymentCheck` for restore validation.

### Changed

- Extended deployed smoke validation and the manual `Post-Deploy Smoke` workflow so promotion-stage checks can assert `build.version`, `git.shortCommitId`, the `prod` profile, JDBC session storage, the documented `15m` session timeout, and `csrfEnabled=false` against the deployed root overview endpoint.
- Updated README, setup, contributor, and AI maintainer release guidance to define the rolling Flyway rollout model, require migration-impact review before tagging, and distinguish image-only rollback from restore-sensitive recovery.

## [v1.5.1] - 2026-05-03

### Added

- Added a dedicated GitHub Actions `CodeQL` workflow with repository-owned configuration and GitHub code-scanning uploads, while keeping Gradle-owned SpotBugs/FindSecBugs, PMD, Trivy, and SBOM checks as separate gates.
- Added keyless signing plus provenance attestation publication for the immutable GHCR image digest produced by the tag-driven `Release` workflow, and updated maintainer guidance to verify release authenticity by digest rather than tag alone.

## [v1.5.0] - 2026-05-03

### Added

- Added an explicit same-site browser session contract with public `GET /api/session` bootstrap/state metadata and public idempotent `POST /api/session/logout`, backed by integration tests, REST Docs coverage, HTTP examples, and refreshed approved OpenAPI artifacts.

### Changed

- Updated the supported auth/session documentation so the separate first-party UI contract now states the one-public-origin reverse-proxy assumption explicitly while keeping `/api/account` as the authenticated persisted-profile endpoint.

## [v1.4.0] - 2026-05-03

### Added

- Added an explicit ADMIN-only operator inspection API at `GET /api/operator/surface` that combines recent audit history, runtime diagnostics, and operational status links, together with integration/REST Docs coverage, HTTP examples, and refreshed OpenAPI contract artifacts.

### Changed

- Introduced typed OAuth provider configuration under `app.security.oauth` with explicit provider type/default-provider handling and prod fail-fast validation for provider credentials, issuer requirements, and multi-provider default selection.
- Replaced hard-coded GitHub login wiring with provider-aware client-registration bootstrapping and default-login resolution derived from configured provider registrations.
- Updated OAuth-facing contract documentation, setup guidance, HTTP examples, and OpenAPI metadata to describe provider-aware `/oauth2/authorization/{registrationId}` login bootstrap semantics.
- Switched the `prod` profile logging contract to structured JSON Lines (`logstash` console format) while preserving the local/test text-pattern defaults and added logging configuration contract coverage for the profile split.
- Updated request-logging integration coverage to run under `prod` JSON logging and assert structured correlation fields (`rid`, `traceId`, `spanId`) plus sensitive-query-parameter redaction in emitted log events.
- Added an optional Fluent Bit forwarding bundle under `k8s/log-forwarding/fluent-bit` and aligned README/setup guidance so multiline Java exception stack traces are recombined before logs are shipped from stdout to centralized collectors.
- Switched the tag-driven `Release` workflow to cumulative GitHub Release notes derived from `CHANGELOG.md` between the new tag section and the previous published GitHub Release tag section, with explicit fail-closed checks when boundaries are missing or ambiguous.
- Added a repo-owned PowerShell helper at `scripts/release/render-release-notes.ps1` so cumulative release-note rendering can be validated locally and reused by workflow automation.
- Added Gradle-owned `applicationSbom`, `imageSbom`, and aggregate `sbom` tasks that generate CycloneDX SBOM outputs for the packaged boot jar and built container image under `build/reports/sbom/`, and wired CI/release workflows to publish those SBOM bundles as run artifacts.
- Added stable `static-analysis-reports` artifact bundles in CI and release workflows so PMD plus SpotBugs/FindSecBugs reports remain downloadable from GitHub Actions even when verification fails.
- Added typed `app.bootstrap.seed.demo-data` configuration to separate demo seed initialization from production-safe startup defaults, wired all startup seed initializers through that toggle, and documented the profile defaults (`local`/`test` enabled, `prod` disabled).
- Documented vendor-neutral backup retention expectations and a reproducible restore drill aligned with migration-bearing release and rollback behavior.

## [v1.3.0] - 2026-05-03

### Added

- Added Gradle-owned `staticSecurityScan` backed by SpotBugs plus FindSecBugs, together with checked-in SpotBugs include/exclude policy files and CI/release artifact handling for the new code-focused security gate.
- Added a scheduled `Post-Deploy Smoke` GitHub Actions workflow plus deployed-environment smoke support that can run HTTP-only checks or deeper JDBC-backed session and Flyway assertions against an already deployed target.
- Added checked-in Kubernetes and Helm autoscaling plus pod disruption budget resources, expanded Prometheus alert coverage, and a broader Grafana dashboard for auth, session, database, Flyway, and error-rate signals.

### Changed

- Hardened the `prod` session posture with a 15 minute timeout, secure-cookie validation, single-session enforcement, typed security settings, and fail-fast validation for optional OAuth credentials and admin bootstrap logins.
- Raised the production root log level to `INFO` while keeping automatic no-TTY ANSI detection and vendor-neutral stdout-plus-OTLP operational guidance.
- Refined the AI execution and workflow guidance so parallel worktree execution, worker status reporting, and release handoff expectations stay aligned with the repository's plan-driven development model.

## [v1.2.2] - 2026-05-03

### Changed

- Clarified the maintainer and AI workflow so local implementation, validation, and review finish before any branch push or PR creation, and release preparation begins only after the approved implementation PR is merged onto `main`.
- Tightened AI planning guidance so plans must ask targeted clarification questions for material gaps and record unresolved requirement holes plus fallback assumptions explicitly instead of silently guessing.
- Split standing AI guidance into focused code-style, testing, review, and documentation guides so policy ownership is explicit and prompts or workflow docs no longer need to absorb those concerns.
- Defined phase-based multi-agent workflow guidance so delegated execution can split requirements, planning, investigation, coding, testing, review, security review, and documentation work only when the coordination cost is justified.

## [v1.2.1] - 2026-05-02

### Changed

- Expanded Docker-backed external smoke coverage to validate the packaged docs HTML, OpenAPI JSON/YAML endpoints, and a JDBC-backed authenticated `GET /api/account` session flow against the release image.
- Added Codecov publication to the `CI` workflow, inlined release-note rendering into the tag-driven `Release` workflow, removed thin PowerShell wrappers that only delegated to Gradle or workflow logic, and updated the maintainer docs to match.
- Updated the human-facing repo guidance so `README.md` and `SETUP.md` describe the expanded packaged smoke guarantees and the CI coverage publication path accurately.

## [v1.2.0] - 2026-05-02

### Added

- Added an ADMIN-only `GET /api/audit-logs` API with pagination, exact-match filtering for `targetType`, `action`, and `actorLogin`, generated REST Docs coverage, reviewer HTTP examples, and aligned README/OpenAPI contract updates.
- Added explicit ADMIN-only category rename and guarded delete operations at `PUT /api/categories/{id}` and `DELETE /api/categories/{id}`, including localized `404` and `409` API errors, REST Docs coverage, reviewer HTTP examples, and aligned README/OpenAPI contract updates.

### Changed

- Refreshed the approved OpenAPI baseline and completed final compatibility/build verification for the new audit-log and category-management APIs.
- Compacted the AI guidance set by moving standing plan-execution rules into `ai/EXECUTION.md`, trimming `ai/PROMPTS.md` down to lean prompt starters, and making AI-instruction compaction an explicit repository maintenance rule.

## [v1.1.0] - 2026-05-02

### Changed

- Cleaned up AI and setup guidance drift so planning, workflow, and prompt docs consistently require `CHANGELOG.md` updates with each completed milestone commit, use the Gradle `gatlingBenchmark` task instead of the removed benchmark script, and no longer describe a manual post-tag GitHub Release flow that the repository does not use.
- Fixed setup and architecture documentation drift by restoring the missing Bash PostgreSQL startup step, correcting the OpenAPI compatibility test command, and updating the architecture guide to reflect the current DTO-backed public controller responses.
- Defined the internal supply-chain scanning contract in `README.md`, including stable report locations under `build/reports/security/`, the unsuppressed `HIGH`/`CRITICAL` failure threshold, CI artifact expectations, and the checked-in Trivy suppression policy file.
- Added Gradle-owned `dependencyVulnerabilityScan`, `imageVulnerabilityScan`, and `vulnerabilityScan` tasks backed by Trivy container runs, stable JSON/SARIF/summary reports under `build/reports/security/`, and automatic build verification wiring through `check`.
- Updated CI and release automation to upload `build/reports/security/` artifacts with `always()` handling, kept release scanning on the exact tagged `dockerImageName` before smoke validation and any image push, and documented the direct `vulnerabilityScan` entry point in `README.md`.

## [v1.0.1] - 2026-05-02

### Changed

- Added a concrete post-`1.0` maintainer release checklist across the AI release workflow, contributor guidance, and the human-facing release model, including Flyway review, benchmark-decision guidance, and verification of the immutable short-SHA image tag.
- Documented the current healthy-runtime expectations for readiness, operational metadata, Prometheus metrics, append-only audit logging, and JDBC-backed authenticated sessions.
- Added operator-facing upgrade, rollback, and troubleshooting runbooks for Flyway-backed releases, optional OAuth setup, PostgreSQL connectivity, and Spring Session JDBC persistence.

## [v1.0.0] - 2026-05-02

### Changed

- Clarified the locked `1.0` deployment posture across setup guidance, technical-endpoint docs, reviewer HTTP examples, and monitoring/chart metadata, including the deployment-scoped status of `GET /actuator/prometheus`.
- Updated the technical overview and REST Docs-backed technical coverage so the repository explicitly documents the locked `1.0` posture for CSRF, optional OAuth login, Prometheus scraping, and the stable `/` and `/hello` endpoints.
- Refined the `1.0` contract narrative across AI design guidance, generated docs, and OpenAPI metadata, including the stable `1.x` status of `/` and `/hello` and the deployment-scoped status of Prometheus scraping.
- Reworked `README.md` around the frozen `1.0` promise, the stable `1.x` contract tiers, the deployment-scoped Prometheus surface, and the locked production posture for optional OAuth, secure session cookies, admin bootstrap, and the deliberate CSRF tradeoff.

## [v0.24.2] - 2026-05-01

### Added

- Added external HTTP smoke tests under a separate `externalTest` source set to cover the public overview, hello, docs redirect, readiness, and book-list endpoints against an externally running application.

### Changed

- Switched CI, release validation, and repository guidance to use Gradle-owned `externalSmokeTest` and `gatlingBenchmark` tasks as the source of truth for external smoke and benchmark verification.
- Replaced the script-owned benchmark orchestration with `buildSrc` Gradle tasks that run the packaged Docker image plus Dockerized PostgreSQL, use fake OAuth client settings for redirect coverage, and log progress through provisioning, readiness, simulations, baseline handling, and teardown.

## [v0.24.1] - 2026-05-01

### Changed

- Replaced workstation-specific `.env.example` path examples with portable placeholders for JDK and IntelliJ configuration.
- Extended the tag-driven release workflow to publish a GitHub Release from the matching `CHANGELOG.md` section after container-image publication succeeds.
- Added grouped weekly Dependabot updates for Gradle, GitHub Actions, and Docker while keeping the existing `CI` workflow as the single PR validation path.
- Hardened the `prod` profile so database connection settings must be provided explicitly, while `SESSION_COOKIE_SECURE` remains optional with a secure-by-default value.

## [v0.24.0] - 2026-05-01

### Added

- Added explicit `BookResponse` and `CategoryResponse` API models so public controllers no longer expose JPA entities directly.
- Added a shared `ApiProblemResponse` OpenAPI schema and documented `401`/`403` category-write error responses in REST Docs, HTTP examples, and the approved OpenAPI baseline.
- Added localized seed messages for `error.request.unauthorized`.
- Added focused unit coverage for the API security entry point and access-denied handler.

### Changed

- Standardized API `401` and security-layer `403` responses to use the same localized `ProblemDetail` shape as the rest of the API.
- Moved book and category response schema metadata from persistence entities onto dedicated response DTOs while preserving the existing JSON field names and category ordering.
- Updated localization list tests and contract artifacts to account for the new seeded auth-error localization key.

## [v0.23.0] - 2026-05-01

### Added

- Added a GitHub Actions `CI` workflow that runs the full Gradle build, Helm validation, and container smoke validation.
- Added a tag-based `Release` workflow that publishes container images to GitHub Container Registry with semantic-version and commit-SHA tags.
- Added production-profile container smoke validation for the packaged Docker image.
- Added vendor-neutral Kubernetes deployment manifests under `k8s/` together with a local Kustomize overlay.
- Added a Helm chart under `helm/technical-interview-demo` for the application deployment contract.
- Added monitoring assets for Prometheus, Grafana, and Alertmanager, including a ServiceMonitor, alert rules, dashboard, and upstream stack values.

### Changed

- Reworked the repository docs around a Spec-Driven Development workflow and aligned `README.md`, `SETUP.md`, `CONTRIBUTING.md`, and `AGENTS.md`.
- Made `SETUP.md` the sole detailed onboarding, deployment, and troubleshooting guide.
- Replaced the standalone OpenAPI compatibility workflow with the consolidated `CI` workflow.
- Updated `ROADMAP.md` so completed Milestone 10 work no longer appears as active planned work.

### Removed

- Removed `COMPLETED_TASKS.md` so released human history now lives only in `CHANGELOG.md`.

## [v0.22.0] - 2026-05-01

### Added

- Added `GET /` as the public technical overview endpoint with build, dependency, runtime, and important configuration details.
- Added collection-style localization filtering through `GET /api/localizations?messageKey=...&language=...`.

### Changed

- Renamed `HelloController` to `TechnicalOverviewController` and aligned the technical overview package structure around `technical.info`.
- Renamed the authenticated-user singleton API from `/api/users/me` to `/api/account` and the language update route to `/api/account/language`.
- Renamed the localization slice from `LocalizationMessage*` to `Localization*` across the codebase, tests, docs, and HTTP example collections.
- Refreshed the generated documentation, approved OpenAPI baseline, and Gatling scenarios to match the simplified pre-`1.0` naming conventions.

### Removed

- Removed the specialized localization lookup routes `/api/localizations/key/{messageKey}/lang/{language}` and `/api/localizations/language/{language}` in favor of collection filters on `/api/localizations`.

## [v0.21.0] - 2026-04-30

### Added

- Added OpenAPI JSON and YAML example requests to `src/test/resources/http/documentation.http`.
- Added a JaCoCo coverage summary task, focused service/exception-path tests, and enforced coverage thresholds of 90% line coverage and 70% branch coverage.
- Added Gatling performance scenarios together with a tracked local baseline and a benchmark runner for public reads and OAuth redirect startup.

### Changed

- Updated `OpenApiBaselineGenerator` to use try-with-resources so the PMD-backed `build` verification stays clean.
- Documented the coverage workflow and the local performance regression checks across the project docs.
- Reduced Gatling benchmark log noise and aligned the Gradle build formatting with Spotless.

## [v0.20.0] - 2026-04-30

### Added

- Added a dedicated GitHub Actions workflow that runs the OpenAPI compatibility integration test on pull requests and pushes to `main`.

### Changed

- Updated the roadmap and contributor-facing documentation to reflect that the OpenAPI compatibility gate now runs in CI.

## [v0.19.0] - 2026-04-30

### Added

- Split the HTTP client examples into per-controller files, including a dedicated authentication collection and localized request examples.
- Added reviewer-focused local auth examples for the GitHub OAuth flow plus local PostgreSQL run-configuration support.
- Added OpenAPI contract endpoints at `/v3/api-docs` and `/v3/api-docs.yaml`.
- Added an approved OpenAPI baseline, a contract refresh task, and a backward-compatibility integration test for the published API surface.

### Changed

- Reorganized application packages around `business` and `technical` concerns, including the extraction of localization infrastructure into `technical.localization`.
- Split user-facing business logic from security synchronization code and moved cache configuration into `technical.cache`.
- Removed the obsolete language-resolution helper in favor of `LocalizationContext`.
- Disabled CSRF protection for the demo's session-based authentication flow so authenticated write requests no longer require a CSRF token round-trip.
- Refreshed the HTTP examples for GitHub OAuth placeholders, controller-specific requests, and expected 404 scenarios.
- Documented the OpenAPI contract, baseline refresh workflow, and reviewer-facing auth setup across the project docs.

## [v0.18.0] - 2026-04-30

### Added

- Added append-only audit-log persistence for `Book` and `Localization` create, update, and delete operations.
- Added a Flyway migration for the `audit_logs` table and integration coverage for audited write flows.

### Changed

- Recorded the acting persisted user and actor-login snapshot with each audited state-changing operation.
- Updated roadmap and project documentation to reflect the completed audit-trail phase.

## [v0.17.0] - 2026-04-30

### Added

- Added persisted application users with stored `USER` and `ADMIN` roles.
- Added authenticated-user profile endpoints for reading the current profile and updating preferred language.
- Added user-specific Micrometer metrics and authenticated-user synchronization on login.

### Changed

- Used persisted application roles to authorize category and localization-message management.
- Used the authenticated user's preferred language as the final localized-error fallback when a request does not explicitly specify language.

## [v0.16.0] - 2026-04-30

### Added

- Added Spring Security OAuth 2.0 support with GitHub as the demo-friendly provider.
- Added PostgreSQL-backed Spring Session JDBC storage for authenticated browser sessions.
- Added authentication and CSRF coverage for protected write endpoints, plus an integration test that verifies the JDBC session repository persists the security context.

### Changed

- Protected state-changing `book`, `category`, and `localization-message` endpoints while keeping public read endpoints open.
- Added profile-specific OAuth and session-cookie configuration for local, test, and production-style environments.
- Updated the setup guide, generated API docs, and project instructions to document the new OAuth login flow and secured API surface.

## [v0.15.0] - 2026-04-30

### Changed

- Replaced entity-level eager fetching with repository-controlled fetch plans for books and categories.
- Replaced the previous simple in-memory cache manager with Caffeine-backed caches.
- Moved cache enablement out of `TechnicalInterviewDemoApplication` into a dedicated configuration class under the `config` package.

## [v0.14.0] - 2026-04-30

### Added

- Added application metrics for book, category, and localization operations.
- Added cache-backed category and localization lookups with dedicated cache names and coverage tests.
- Added focused verification for cache behavior and metric emission.

### Changed

- Documented the new caching and metrics behavior in the project docs and technical endpoints documentation.

## [v0.13.0] - 2026-04-30

### Added

- Added the `Category` domain, repository, service, controller, and startup seed data.
- Added category assignment for books and category-based filtering in the book search API.
- Added Flyway migration support for category and book-category tables.
- Added REST Docs and integration tests for category endpoints and category-aware book responses.

### Changed

- Extended book create, update, and search flows to work with categories.

## [v0.12.0] - 2026-04-30

### Added

- Defined the release tagging policy for completed roadmap phases.
- Added this human-readable changelog and backfilled historical releases from the roadmap archive and git history.
- Documented how roadmap phases map to commits, changelog entries, and annotated git tags.

## [v0.11.0] - 2026-04-30

### Added

- Added cookie-based language fallback for localized error responses.
- Added request-scoped language resolution and supported-language validation.

## [v0.10.0] - 2026-04-30

### Added

- Localized `ProblemDetail` responses with `messageKey`, localized `message`, and resolved `language`.
- Added browser-compatible preferred-language handling with `Accept-Language` and `lang`.
- Added seeded localization support for Polish, French, Ukrainian, and Norwegian.

### Changed

- Refactored Spring REST Docs into a multi-page structure with an indexed entry page, per-controller pages, and a dedicated technical-endpoints page.

## [v0.9.0] - 2026-04-30

### Added

- Seeded localization messages for the current API error scenarios.
- Added test coverage to keep seeded keys aligned across supported languages.

## [v0.8.0] - 2026-04-30

### Added

- Added CRUD, lookup, pagination, and documentation coverage for localization messages.

## [v0.7.0] - 2026-04-30

### Added

- Added the `Localization` entity, repository, service, and Flyway migration.

## [v0.6.0] - 2026-04-30

### Added

- Added PostgreSQL-backed integration testing with Testcontainers.
- Introduced shared test infrastructure for containerized database tests.

## [v0.5.0] - 2026-04-30

### Added

- Added book search and filtering by title, author, ISBN, and publication year.
- Documented the expanded list endpoint and invalid-filter behavior.

## [v0.4.0] - 2026-04-30

### Added

- Added `CONTRIBUTING.md` and contribution workflow guidance.
- Documented testing expectations, quality gates, and collaboration rules.

## [v0.3.0] - 2026-04-30

### Added

- Added `SETUP.md` and `.env.example`.
- Documented local, PostgreSQL, IntelliJ, VS Code, and dev-container setup flows.

## [v0.2.0] - 2026-04-30

### Added

- Added PostgreSQL runtime support and production-oriented datasource configuration.
- Added Docker Compose support for local PostgreSQL development.

## [v0.1.0] - 2026-04-30

### Changed

- Split runtime configuration into `local`, `prod`, and `test` profiles.
- Documented default profile behavior and container profile selection.

## [v0.0.6] - 2026-04-30

### Added

- Added Flyway-driven schema migrations and schema validation alignment.
- Added Qodana static-analysis integration to the build workflow.
- Added a VS Code dev container with PostgreSQL and Prometheus services for zero-friction local setup.

### Changed

- Updated the Dockerfile and setup documentation to support the dev-container-based workflow.

## [v0.0.5] - 2026-04-29

### Added

- Added Spring REST Docs and Asciidoctor-based API documentation generation.
- Added the `/docs` endpoint and packaged generated documentation into the runnable application.
- Added optimistic locking for books with version-aware updates and conflict handling.
- Added pagination and richer API documentation for book responses and actuator endpoints.
- Added a Gradle Docker build task and improved git-based version resolution for container builds.

### Changed

- Hardened logging by redacting sensitive query parameters before they reach application logs.
- Refined technical documentation for error examples, health probes, readiness, and Prometheus metrics.

## [v0.0.4] - 2026-04-29

### Added

- Added Error Prone, PMD, and JaCoCo to strengthen static analysis and code-quality enforcement.
- Added request ID generation, propagation, and MDC logging support.
- Added Lombok and Spring Boot devtools to streamline development.

### Changed

- Split book write contracts into dedicated create and update requests and kept ISBN immutable on updates.
- Refactored controllers to consistently build `ResponseEntity` responses from local payload variables.
- Upgraded and tuned the Gradle build, wrapper, test logging, and plugin configuration.

## [v0.0.3] - 2026-04-29

### Added

- Added Spotless formatting with `.editorconfig`-driven conventions.
- Added a comprehensive `README.md` aligned with `AGENTS.md`.
- Added Docker support with a multi-stage image and IntelliJ run configurations.

### Changed

- Improved formatter integration to work cleanly with IntelliJ IDEA when available.
- Standardized newline and formatting behavior across the repository.

## [v0.0.2] - 2026-04-29

### Added

- Added centralized API exception handling with structured error responses.
- Added detailed exception logging, property-path sanitization, and hidden internal exception details.
- Added service-layer logging through `ServiceLoggingAspect`.
- Added HTTP tracing and logging filters with `traceparent` propagation and sensitive-data sanitization.

### Changed

- Moved non-trivial book behavior into a logged service layer to keep controllers small.

## [v0.0.1] - 2026-04-29

### Added

- Initialized the Spring Boot application and Gradle build.
- Added the initial `Book` CRUD API with startup seed data.
- Added unique ISBN enforcement, request validation, and in-memory test/runtime support.
- Added baseline API tests, sample request documentation, and CRUD operation logging.
- Added initial project documentation in `HELP.md` and `AGENTS.md`.
