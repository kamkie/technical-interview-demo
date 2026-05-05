# Plan: External Smoke And Benchmark Tasks

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Closed |
| Status | Released |

## Summary
- Add a dedicated `externalTest` source set whose smoke tests exercise supported endpoints through HTTP against an externally running application, while keeping those tests out of the standard `test`, `check`, and `build` lifecycle.
- Replace the PowerShell-driven Gatling flow with Gradle tasks implemented in `buildSrc` that start the packaged Docker image plus PostgreSQL, run the selected benchmarks, and keep benchmark execution separate from the default build.
- Require the Gradle-owned benchmark flow to emit clear progress logs so long-running Docker startup, readiness waiting, and simulation execution are visible while the task runs.
- Align the current container smoke validation with the packaged-image release path so CI and tag-based release verification use the same Gradle-owned smoke path against the real Docker image.
- Success is measured by: a new external smoke test source set, Gradle-owned Docker-backed smoke and benchmark tasks, unchanged public API contract artifacts, updated workflow/docs references, and passing validation including `.\gradlew.bat build`.

## Scope
- In scope:
  - a new `externalTest` source set and dedicated Gradle smoke-test task
  - `buildSrc` plugin and task implementation for Docker-backed smoke and benchmark execution
  - replacing `scripts/run-phase-9-benchmarks.ps1` as the source of truth with Gradle tasks
  - reviewing and aligning the existing container smoke validation with the packaged Docker image used in CI and releases
  - updating CI/release automation and repo guidance that currently points at the PowerShell scripts
  - benchmark-environment updates needed so OAuth-related benchmark coverage works without real GitHub credentials
  - benchmark task logging that shows startup, readiness, simulation, and teardown progress during long-running runs
  - intentional benchmark baseline and benchmark-note updates if the runtime environment for captured results changes
- Out of scope:
  - changing public endpoint semantics, response shapes, security requirements, REST Docs content, OpenAPI surface, or reviewer HTTP examples
  - adding a real test OAuth provider service or a full browser-login automation flow
  - adding authenticated write benchmarks that depend on an interactive session cookie bootstrap
  - moving smoke tests or benchmarks into the normal `build` lifecycle
  - Kubernetes, Helm, or monitoring behavior changes beyond updating any references to the new smoke/benchmark commands

## Current State
- Current behavior:
  - `build.gradle.kts` applies the Gatling Gradle plugin and defines `dockerBuild`, but it does not define a separate external smoke-test source set or a Gradle-owned Docker orchestration flow for smoke and benchmark runs.
  - `scripts/run-phase-9-benchmarks.ps1` is the current benchmark entry point. It starts PostgreSQL with `docker-compose`, builds a boot jar, launches the app directly with `java -jar`, injects dummy GitHub OAuth credentials, runs `gatlingRun`, and writes `performance/baselines/phase-9-local.json`.
  - `scripts/ci/smoke-container.ps1` is the current packaged-image smoke validator. It starts PostgreSQL and the app image in Docker, waits for readiness, and verifies Flyway ran, but it sits outside Gradle and is only wired in CI.
  - `.github/workflows/ci.yml` runs `.\gradlew.bat build` and then calls `.\scripts\ci\smoke-container.ps1 -ImageName technical-interview-demo`.
  - `.github/workflows/release.yml` builds the tagged image and publishes it, but it does not reuse the current smoke validation before push.
  - The public smoke-friendly endpoints already exist and are contract-defined by current specs: `GET /`, `GET /hello`, `GET /docs`, and the documented actuator health/readiness endpoints.
- Current constraints:
  - the repo is intentionally small and direct, so the solution should stay in plain Gradle/buildSrc code plus straightforward JUnit/Gatling usage instead of adding a large test harness framework
  - the public contract must stay stable because the user did not request API changes
  - smoke and benchmark runs must exercise a real packaged application running in Docker, not an in-process Spring test context
  - the benchmark path must either disable auth or use a fake OAuth provider; the current repo already proves the redirect benchmark can run with dummy GitHub client registration values because it only checks the redirect start
  - `README.md`, `AGENTS.md`, `CONTRIBUTING.md`, `SETUP.md`, and `ai/WORKFLOW.md` currently reference the PowerShell benchmark path, so those instructions will drift if the Gradle task becomes authoritative without doc updates
- Relevant existing specs and code:
  - roadmap driver: `ROADMAP.md`
  - planning rules: `ai/PLAN.md`
  - human-facing quality and CI/release contract: `README.md`
  - AI-facing rules and verification expectations: `AGENTS.md`, `ai/WORKFLOW.md`, `ai/RELEASES.md`
  - setup and local reproduction guidance: `SETUP.md`
  - current release history for the packaged-image smoke work: `CHANGELOG.md`
  - current build and plugin surface: `build.gradle.kts`, `buildSrc/build.gradle.kts`, `buildSrc/src/main/kotlin/team/jit/technicalinterviewdemo/build/JacocoCoverageConventionsPlugin.kt`
  - current smoke and benchmark runtime entry points: `scripts/ci/smoke-container.ps1`, `scripts/run-phase-9-benchmarks.ps1`
  - current benchmark executable specs: `src/gatling/java/team/jit/technicalinterviewdemo/performance/PublicApiSimulation.java`, `src/gatling/java/team/jit/technicalinterviewdemo/performance/AuthenticationRedirectSimulation.java`, `src/gatling/java/team/jit/technicalinterviewdemo/performance/AuthenticatedUserProfileSimulation.java`, `src/gatling/java/team/jit/technicalinterviewdemo/performance/PerformanceEnvironment.java`
  - current public smoke contract anchors: `src/test/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewControllerIntegrationTests.java`, `src/docs/asciidoc/technical-overview-controller.adoc`, `src/docs/asciidoc/technical-endpoints.adoc`, `src/test/resources/http/technical-overview-controller.http`, `src/test/resources/http/technical-endpoints.http`, `src/test/resources/openapi/approved-openapi.json`
  - current runtime/security config relevant to Docker and OAuth startup: `src/main/resources/application.properties`, `src/main/resources/application-local.properties`, `src/main/resources/application-oauth.properties`, `src/main/resources/application-prod.properties`, `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecurityConfiguration.java`, `Dockerfile`, `docker-compose.yml`
  - current tracked baseline artifact: `performance/baselines/phase-9-local.json`

## Locked Decisions And Assumptions
- User decisions:
  - plan the roadmap work for external smoke tests, dedicated Gradle benchmarks, and packaged-image smoke-validation alignment only
  - create the plan in `ai/archive/PLAN_external_tests.md`
- Planning assumptions that the executor should not revisit:
  - preserve the current public API contract; this is internal verification and build-workflow work, not an API redesign
  - use a new `externalTest` source set for the external smoke tests
  - keep the new external smoke tests out of `test`, `check`, and `build`; CI/release flows must invoke them explicitly
  - make Gradle the source of truth for smoke and benchmark execution; any remaining PowerShell scripts should either be removed or reduced to thin wrappers that delegate to Gradle
  - run the application under test from the packaged Docker image plus a Dockerized PostgreSQL instance for both smoke and benchmark tasks
  - keep the test code itself external-facing: the smoke tests should talk to a `baseUrl` over HTTP and must not depend on Spring test context internals
  - prefer fake OAuth configuration over a new auth-disabling profile for benchmark runs, because that keeps the benchmark closer to the packaged image and current redirect contract while avoiding real GitHub credentials
  - continue to exclude `AuthenticatedUserProfileSimulation` from the automated benchmark baseline unless a non-interactive session bootstrap is deliberately added as separate work
  - benchmark baseline rewrites should be explicit, not automatic on every benchmark run
  - benchmark tasks should log visible progress at each major phase so local runs and CI logs make stalled or slow execution diagnosable
  - CI should validate the default local image name, and the release workflow should validate the exact tagged image coordinates before publish

## Affected Artifacts
- Tests:
  - new external smoke tests under `src/externalTest/java/...`
  - likely new external-test support under `src/externalTest/java/.../support/...`
  - existing internal integration/REST Docs/OpenAPI tests should remain unchanged unless a contract drift bug is found while implementing the smoke coverage
- Docs:
  - `README.md`
  - `SETUP.md`
  - `AGENTS.md`
  - `CONTRIBUTING.md`
  - `ai/WORKFLOW.md`
  - likely `ai/RELEASES.md` if the release verification steps need to mention the packaged-image smoke task explicitly
  - `ROADMAP.md` once these roadmap items are completed in implementation
- OpenAPI:
  - no change expected; do not refresh `src/test/resources/openapi/approved-openapi.json`
- HTTP examples:
  - no change expected; keep `src/test/resources/http/` unchanged unless an existing example is proven inaccurate
- REST Docs / Asciidoc:
  - no behavior-driven update expected; the existing docs remain the governing contract for which public endpoints the smoke tests should cover
- Source files:
  - `build.gradle.kts`
  - `buildSrc/build.gradle.kts`
  - likely new plugin classes under `buildSrc/src/main/kotlin/team/jit/technicalinterviewdemo/build/`, for example a focused external-test plugin plus small Docker orchestration/task helpers
  - likely new external smoke test sources under `src/externalTest/java/team/jit/technicalinterviewdemo/...`
  - likely small updates to the Gatling support classes under `src/gatling/java/team/jit/technicalinterviewdemo/performance/`
  - `scripts/run-phase-9-benchmarks.ps1`
  - `scripts/ci/smoke-container.ps1`
  - `performance/baselines/phase-9-local.json`
  - `.github/workflows/ci.yml`
  - `.github/workflows/release.yml`
- Build or benchmark checks:
  - `.\gradlew.bat build`
  - new dedicated Gradle smoke task
  - new dedicated Gradle benchmark task
  - tracked Gatling reports under `build/reports/gatling`
  - benchmark baseline metadata under `performance/baselines/phase-9-local.json`

## Execution Milestones
### Milestone 1: Add Docker-Backed External Smoke Tests
- Goal:
  - create a separate source set and Gradle entry point for smoke tests that validate the packaged app over HTTP without joining the normal test lifecycle
- Files to update:
  - `build.gradle.kts`
  - `buildSrc/build.gradle.kts`
  - new plugin/task sources under `buildSrc/src/main/kotlin/team/jit/technicalinterviewdemo/build/`
  - new tests under `src/externalTest/java/team/jit/technicalinterviewdemo/...`
  - possibly `scripts/ci/smoke-container.ps1` if it becomes a wrapper or is retired
- Behavior to preserve:
  - `.\gradlew.bat build` continues to run the current standard lifecycle only
  - public contract artifacts remain unchanged
  - the smoke tests target an externally running application via HTTP instead of loading Spring inside the same JVM
- Exact deliverables:
  - define the `externalTest` source set and a dedicated smoke-test task that is not attached to `test`, `check`, or `build`
  - add buildSrc-owned Docker orchestration that:
    - builds or reuses the packaged image
    - starts PostgreSQL in Docker
    - starts the app image in Docker with the required `prod` runtime env vars
    - waits for readiness and fails with useful logs on timeout
    - tears everything down reliably on success and failure
  - preserve the current packaged-image smoke guarantees from `scripts/ci/smoke-container.ps1`, including readiness and Flyway-success validation, while also running endpoint-level smoke assertions through the new `externalTest` test task
  - cover a small, stable smoke slice of the documented public contract, centered on the current technical/public endpoints such as `/`, `/hello`, `/docs`, and readiness or a representative public API read
  - keep the external smoke tests implementation lightweight, ideally JUnit plus the JDK HTTP client rather than a new heavy dependency

### Milestone 2: Replace The PowerShell Benchmark Runner With Gradle Tasks
- Goal:
  - make Gradle the benchmark entry point and run benchmarks against the real packaged Docker app instead of a locally started boot jar
- Files to update:
  - `build.gradle.kts`
  - new or updated buildSrc plugin/task sources under `buildSrc/src/main/kotlin/team/jit/technicalinterviewdemo/build/`
  - `src/gatling/java/team/jit/technicalinterviewdemo/performance/PerformanceEnvironment.java`
  - possibly `src/gatling/java/team/jit/technicalinterviewdemo/performance/*.java` if task/environment assumptions need small cleanup
  - `scripts/run-phase-9-benchmarks.ps1`
  - `performance/baselines/phase-9-local.json`
- Behavior to preserve:
  - the current automated benchmark scope remains focused on the public API reads and OAuth redirect startup path
  - benchmark execution stays separate from the normal `build`
  - benchmark reports are still generated under `build/reports/gatling`
- Exact deliverables:
  - add a dedicated Gradle benchmark task in buildSrc that provisions PostgreSQL plus the packaged Docker image, waits for readiness, and runs the selected Gatling simulations against the resolved `baseUrl`
  - emit progress logs for benchmark phases including Docker provisioning, application startup, readiness waiting, each Gatling simulation start/finish, baseline write decisions, and teardown
  - run the benchmark app with fake OAuth client registration values instead of disabling auth, so `AuthenticationRedirectSimulation` can keep asserting the redirect-start behavior without real GitHub credentials
  - keep `AuthenticatedUserProfileSimulation` outside the automated baseline unless the implementation also introduces a deliberate non-interactive session bootstrap
  - move report discovery and optional baseline-writing logic out of the PowerShell script and into the Gradle-owned path
  - update `performance/baselines/phase-9-local.json` metadata and notes if the captured environment changes from `java -jar` plus `docker-compose` to the packaged Docker image plus Dockerized PostgreSQL
  - remove the old script as the authoritative path or reduce it to a wrapper that simply calls the new Gradle task

### Milestone 3: Align CI And Release Smoke Validation With The Packaged Image
- Goal:
  - keep smoke validation aligned with the actual image that CI and releases build and publish
- Files to update:
  - `.github/workflows/ci.yml`
  - `.github/workflows/release.yml`
  - `README.md`
  - `SETUP.md`
  - `AGENTS.md`
  - `CONTRIBUTING.md`
  - `ai/WORKFLOW.md`
  - likely `ai/RELEASES.md`
  - `scripts/ci/smoke-container.ps1` if it remains as a wrapper or compatibility shim
- Behavior to preserve:
  - CI still runs the standard `build` first
  - release publication stays tag-driven and only happens after verification
  - the smoke task remains outside the default Gradle lifecycle even though CI and release workflows call it explicitly
- Exact deliverables:
  - update CI to call the new Gradle smoke task instead of treating the PowerShell script as the primary implementation path
  - update the release workflow to validate the exact tagged image coordinates with the same Gradle-owned smoke path before image publication completes
  - make image-name handling consistent between `dockerBuild`, CI smoke validation, and release smoke validation so the task always tests the image that will actually be shipped
  - update repo guidance so benchmark and smoke instructions point to the new Gradle tasks rather than the old script names
  - remove or trim the completed roadmap items in `ROADMAP.md` once the implementation lands

## Edge Cases And Failure Modes
- Docker port, network, or container-name collisions can leave false-negative smoke or benchmark failures; the Gradle tasks need deterministic cleanup and clear failure output.
- If the external smoke tests accidentally use Spring test infrastructure, they will stop proving the real packaged app behavior; they must stay pure HTTP clients.
- If the new smoke task is wired into `check` or `build`, the task will violate the explicit requirement to stay out of the normal test lifecycle.
- The packaged `prod` image needs explicit `DATABASE_*` and `SESSION_COOKIE_SECURE` env vars; the Gradle orchestration must supply them consistently or the tasks will fail before they prove anything useful.
- The OAuth redirect benchmark must not require live GitHub access beyond generating the redirect URL; if the benchmark follows redirects or expects a callback, it becomes flaky and environment-dependent.
- The release workflow can drift if it validates one image name and publishes another; image coordinates must come from the same Gradle/property source.
- Benchmark baseline files can churn if every run rewrites them by default; baseline refresh should be opt-in.
- If the old PowerShell scripts remain full implementations in parallel with Gradle, repo guidance will drift and maintenance cost will double.
- Docker-unavailable environments must fail fast with a direct error instead of hanging on readiness polling.

## Validation Plan
- Commands to run:
  - `docker version`
  - `.\gradlew.bat build`
  - `.\gradlew.bat externalSmokeTest`
  - `.\gradlew.bat gatlingBenchmark`
  - `.\gradlew.bat build -PdockerImageName=technical-interview-demo:external-tests`
  - `.\gradlew.bat externalSmokeTest -PdockerImageName=technical-interview-demo:external-tests`
- Tests to add or update:
  - add new external smoke tests under `src/externalTest/java/...` that validate the external HTTP contract against the Dockerized app
  - keep the existing internal integration, REST Docs, and OpenAPI tests green without contract edits
  - preserve the current Gatling simulation coverage for public reads and OAuth redirect startup
- Docs or contract checks:
  - update `README.md`, `SETUP.md`, `AGENTS.md`, `CONTRIBUTING.md`, and `ai/WORKFLOW.md` to reference the Gradle smoke and benchmark tasks instead of `scripts/run-phase-9-benchmarks.ps1`
  - update `ai/RELEASES.md` if the release verification checklist must now mention the packaged-image smoke task explicitly
  - no REST Docs page changes expected
  - no approved OpenAPI baseline refresh expected
  - no HTTP example refresh expected
  - benchmark artifacts are affected: rerun the benchmark path intentionally and refresh baseline notes only if the tracked benchmark environment changes as part of this work
- Manual verification steps:
  - confirm the new smoke task leaves `test`, `check`, and `build` untouched unless invoked explicitly
  - inspect the smoke-test report output and verify the assertions are against the external base URL, not an in-process Spring test context
  - inspect the Gatling reports and confirm the benchmark task ran against the Dockerized packaged app
  - inspect the benchmark console output and confirm progress is logged throughout the run instead of appearing idle until completion
  - verify the release workflow now smoke-tests the exact tagged image coordinates before publish
  - verify the old script paths are either removed or obviously delegated to the Gradle tasks so there is one source of truth

## Better Engineering Notes
- Keep the buildSrc implementation small and direct. A focused plugin plus a few task/helper classes is enough; do not build a mini deployment framework inside Gradle.
- Prefer plain JUnit plus `java.net.http.HttpClient` for the external smoke tests. That keeps the repo small and avoids adding a large HTTP-test dependency just to hit a handful of endpoints.
- Reuse the current Gatling simulations unless a concrete mismatch with Dockerized packaged-image execution is discovered. The goal is execution-path change, not scenario churn.
- Preserve the current benchmark decision to leave authenticated-session benchmarking manual. Automating that path would be a separate design problem and should not be hidden inside this plan.
- If `scripts/run-phase-9-benchmarks.ps1` or `scripts/ci/smoke-container.ps1` remain for contributor ergonomics, make them thin wrappers around Gradle so task behavior lives in one place.

## Validation Results
- `2026-05-01`: `docker version` passed on the integration workstation before the Docker-backed tasks were exercised.
- `2026-05-01`: `.\gradlew.bat help --task externalSmokeTest --no-daemon` passed and confirmed the dedicated smoke task exists outside the standard lifecycle.
- `2026-05-01`: `.\gradlew.bat help --task gatlingBenchmark --no-daemon` passed and confirmed the dedicated benchmark task is registered from `buildSrc`.
- `2026-05-01`: `.\gradlew.bat externalSmokeTest -PexternalSmokeImageName=technical-interview-demo -PdockerImageName=technical-interview-demo --no-daemon` passed against the packaged Docker image plus Dockerized PostgreSQL.
- `2026-05-01`: `.\gradlew.bat gatlingBenchmark -Pbenchmark.updateBaseline=true --no-daemon` passed, refreshed `performance/baselines/phase-9-local.json`, and logged provisioning, readiness, simulation, baseline, and teardown progress.
- `2026-05-01`: `.\gradlew.bat build --no-daemon` passed on `main`.
- `2026-05-01`: `.\gradlew.bat externalSmokeTest -PexternalSmokeImageName=technical-interview-demo -PdockerImageName=technical-interview-demo --no-daemon` passed again on `main` as final integrated smoke validation.
- `2026-05-01`: `.\gradlew.bat gatlingBenchmark --no-daemon` passed on `main`, kept the refreshed baseline within tolerance, and wrote the latest result to `build/performance/phase-9-latest.json`.
- `2026-05-01`: `.\gradlew.bat build --no-daemon` passed again on `main` after release metadata preparation and before tagging.

## User Validation
- Run `.\gradlew.bat externalSmokeTest` and confirm it starts Dockerized PostgreSQL plus the packaged app, then produces a separate smoke-test report without changing the normal `build` lifecycle.
- Run `.\gradlew.bat gatlingBenchmark` and confirm it starts the Dockerized packaged app, executes the public-read and OAuth-redirect simulations, and writes Gatling reports.
- Inspect the CI and release workflow changes and confirm both now point at the Gradle-owned smoke path for the packaged image rather than a separate script implementation.
