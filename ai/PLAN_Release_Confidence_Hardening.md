# Plan: Release Confidence Hardening

## Summary
- Harden the release path so it proves more of the already-supported packaged application surface before publish: generated docs assets, OpenAPI endpoints, and one authenticated session-backed account read in addition to the current unauthenticated smoke checks.
- Publish the existing JaCoCo report to Codecov from CI and expose that signal in `README.md`, while simplifying release/helper-script usage so Gradle tasks and GitHub workflows remain the source of truth.
- Success is measured by: expanded `externalSmokeTest` coverage against the packaged image, a production-like session persistence check that exercises real `SPRING_SESSION` tables without adding a new public auth shortcut, working CI coverage publication, simpler release automation, and a passing `.\gradlew.bat build`.

## Scope
- In scope
- Expand `externalSmokeTest` to cover `GET /docs/index.html`, `GET /v3/api-docs`, `GET /v3/api-docs.yaml`, and an authenticated `GET /api/account` request against the packaged container path.
- Prove JDBC-backed Spring Session persistence in the smoke environment by creating and reusing a real persisted session against PostgreSQL, then asserting the packaged app honors that session.
- Keep the smoke/test harness lightweight and repo-owned by extending the existing Gradle external-testing plugin and `src/externalTest` test suite.
- Publish JaCoCo coverage reports from CI to Codecov and add CI/coverage badges to `README.md` once the upload path is live.
- Remove or inline no-longer-justified helper scripts from `scripts/`, especially release-note rendering if the workflow becomes clearer without the PowerShell wrapper.
- Update workflow and setup/release guidance where the CI/release operating model materially changes.
- Out of scope
- Any intentional change to the frozen `1.x` public HTTP contract, documented security model, or endpoint payloads.
- Adding a new public endpoint, smoke-only public endpoint, or alternate public authentication flow just to make external smoke testing easier.
- Broad production-hardening work from later roadmap sections such as CSRF changes, new identity providers, rate limiting, or new operational endpoints.
- Benchmark tuning or performance work beyond rerunning an existing benchmark if implementation unexpectedly touches OAuth/session startup behavior.

## Current State
- Current behavior
- `ROADMAP.md` defines `Release Confidence Hardening` as planned work covering coverage publication, script cleanup, broader smoke validation, docs/OpenAPI smoke coverage, and a production-like Spring Session persistence check.
- `.github/workflows/ci.yml` runs `./gradlew build`, Helm validation, and `./gradlew externalSmokeTest`, but it does not publish coverage externally.
- `.github/workflows/release.yml` rebuilds the tagged image, runs `./gradlew build`, runs `./gradlew externalSmokeTest`, then shells out to `scripts/release/render-release-notes.ps1` before creating the GitHub Release.
- `src/externalTest/java/team/jit/technicalinterviewdemo/external/ExternalSmokeTests.java` currently verifies only five unauthenticated checks: `/`, `/hello`, `/docs`, `/actuator/health/readiness`, and `GET /api/books`.
- `buildSrc/src/main/kotlin/team/jit/technicalinterviewdemo/build/ExternalSmokeEnvironmentUpTask.kt` currently proves Dockerized PostgreSQL startup, application readiness, and Flyway success, but it does not expose database connectivity to the external smoke tests or verify session-backed auth behavior.
- Docs/OpenAPI exposure is already specified elsewhere: `/docs` is served by `DocumentationWebConfiguration`, `/v3/api-docs` and `/v3/api-docs.yaml` are exposed and tested in-process, and the supported contract already lists them in `README.md`, `src/docs/asciidoc/`, HTTP examples, and the approved OpenAPI baseline.
- Authenticated account behavior and Spring Session persistence are already specified in-process by `UserManagementIntegrationTests`, `SecurityIntegrationTests`, `authentication.http`, `user-account-controller.http`, `README.md`, and `SETUP.md`, but the packaged-image smoke path does not currently exercise them.
- Current constraints
- Preserve the demo character of the app and prefer harness/test/workflow changes over new application abstractions.
- Keep `externalSmokeTest` outside the normal `build` lifecycle; CI and release call it explicitly.
- Preserve the current public contract and security rules documented in `README.md`, `src/docs/asciidoc/`, HTTP examples, and `src/test/resources/openapi/approved-openapi.json`.
- Use the same image coordinate consistently through build, smoke validation, and release publication.
- Relevant existing specs and code
- Roadmap intent: `ROADMAP.md`
- Planning rules: `AGENTS.md`, `ai/PLAN.md`
- Public contract docs: `README.md`, `src/docs/asciidoc/index.adoc`, `src/docs/asciidoc/technical-overview-controller.adoc`, `src/docs/asciidoc/technical-endpoints.adoc`
- HTTP examples: `src/test/resources/http/documentation.http`, `src/test/resources/http/authentication.http`, `src/test/resources/http/user-account-controller.http`, `src/test/resources/http/technical-endpoints.http`
- OpenAPI contract: `src/test/resources/openapi/approved-openapi.json`
- Governing executable specs: `src/externalTest/java/team/jit/technicalinterviewdemo/external/ExternalSmokeTests.java`, `src/test/java/team/jit/technicalinterviewdemo/technical/docs/OpenApiIntegrationTests.java`, `src/test/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewControllerIntegrationTests.java`, `src/test/java/team/jit/technicalinterviewdemo/business/user/UserManagementIntegrationTests.java`, `src/test/java/team/jit/technicalinterviewdemo/technical/security/SecurityIntegrationTests.java`
- Workflow and harness implementation: `.github/workflows/ci.yml`, `.github/workflows/release.yml`, `buildSrc/src/main/kotlin/team/jit/technicalinterviewdemo/build/ExternalTestingConventionsPlugin.kt`, `buildSrc/src/main/kotlin/team/jit/technicalinterviewdemo/build/ExternalSmokeEnvironmentUpTask.kt`, `buildSrc/src/main/kotlin/team/jit/technicalinterviewdemo/build/ExternalSmokeEnvironmentDownTask.kt`, `buildSrc/src/main/kotlin/team/jit/technicalinterviewdemo/build/DockerSupport.kt`, `src/externalTest/java/team/jit/technicalinterviewdemo/external/ExternalHttpTestSupport.java`
- Auth/docs implementation to preserve: `src/main/java/team/jit/technicalinterviewdemo/technical/docs/DocumentationWebConfiguration.java`, `src/main/java/team/jit/technicalinterviewdemo/technical/docs/OpenApiConfiguration.java`, `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecurityConfiguration.java`, `src/main/java/team/jit/technicalinterviewdemo/technical/security/SessionConfiguration.java`, `src/main/java/team/jit/technicalinterviewdemo/business/user/UserAccountController.java`, `src/main/java/team/jit/technicalinterviewdemo/business/user/CurrentUserAccountService.java`

## Locked Decisions And Assumptions
- Preserve the current public contract. `README.md`, REST Docs, OpenAPI, and HTTP examples remain contract references to preserve, not surfaces to expand.
- Do not add a fake public login endpoint, smoke-only public endpoint, or other public auth shortcut. The preferred smoke strategy is database-backed session seeding against the real PostgreSQL session tables used by the packaged app.
- Keep the authenticated smoke path narrow: a single persisted-session bootstrap plus `GET /api/account` is sufficient to prove packaged session-backed auth works.
- Treat docs/OpenAPI smoke additions as executable release-confidence specs only; no REST Docs or OpenAPI baseline refresh is expected because the contract itself is not changing.
- Inline release-note rendering into `.github/workflows/release.yml` if the logic stays concise and readable there; otherwise keep a helper only if it provides clear reuse that the workflow alone cannot.
- `scripts/ci/smoke-container.ps1` and `scripts/run-phase-9-benchmarks.ps1` are delete candidates because Gradle tasks are already the documented source of truth. Keep them only if execution finds a still-needed cross-environment use case.
- Assume Codecov is the intended external coverage sink from the roadmap item. If repository onboarding or auth is required outside git, document that prerequisite explicitly rather than inventing a different reporting service.
- Benchmark reruns are not expected for the planned happy path because this work should stay in workflows, external smoke harness code, and docs. If execution is forced to change OAuth/session startup behavior inside the application runtime, rerun `.\gradlew.bat gatlingBenchmark` before completion.

## Affected Artifacts
- Tests
- `src/externalTest/java/team/jit/technicalinterviewdemo/external/ExternalSmokeTests.java`
- `src/externalTest/java/team/jit/technicalinterviewdemo/external/ExternalHttpTestSupport.java`
- likely one new external-test support class for session/bootstrap/database helpers under `src/externalTest/java/team/jit/technicalinterviewdemo/external/`
- governing specs to keep green: `src/test/java/team/jit/technicalinterviewdemo/technical/docs/OpenApiIntegrationTests.java`, `src/test/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewControllerIntegrationTests.java`, `src/test/java/team/jit/technicalinterviewdemo/business/user/UserManagementIntegrationTests.java`, `src/test/java/team/jit/technicalinterviewdemo/technical/security/SecurityIntegrationTests.java`
- Docs
- `README.md` is affected: add CI and Codecov badges once coverage publishing is live, and keep CI/release narrative aligned with any script removal.
- `SETUP.md` is affected because local CI reproduction and release-note dry-run guidance currently reference the script-based release-note flow.
- `ai/RELEASES.md` is affected if release-note rendering or release validation guidance changes materially.
- REST Docs / Asciidoc pages under `src/docs/asciidoc/` are not expected to change because the public contract is unchanged.
- OpenAPI
- `src/test/resources/openapi/approved-openapi.json` is not expected to change.
- OpenAPI compatibility tests remain governing specs and must stay green without a baseline refresh.
- HTTP examples
- `src/test/resources/http/documentation.http`, `src/test/resources/http/authentication.http`, and `src/test/resources/http/user-account-controller.http` are not expected to change because the supported request surface is unchanged.
- Source files
- `.github/workflows/ci.yml`
- `.github/workflows/release.yml`
- possible new Codecov config such as `.github/codecov.yml` only if needed for stable reporting behavior
- `buildSrc/src/main/kotlin/team/jit/technicalinterviewdemo/build/ExternalTestingConventionsPlugin.kt`
- `buildSrc/src/main/kotlin/team/jit/technicalinterviewdemo/build/ExternalSmokeEnvironmentUpTask.kt`
- `buildSrc/src/main/kotlin/team/jit/technicalinterviewdemo/build/ExternalSmokeEnvironmentDownTask.kt`
- `buildSrc/src/main/kotlin/team/jit/technicalinterviewdemo/build/DockerSupport.kt`
- `scripts/ci/smoke-container.ps1`
- `scripts/run-phase-9-benchmarks.ps1`
- `scripts/release/render-release-notes.ps1`
- Build or benchmark checks
- `.\gradlew.bat externalSmokeTest -PexternalSmokeImageName=technical-interview-demo -PdockerImageName=technical-interview-demo`
- `.\gradlew.bat build`
- `.\gradlew.bat gatlingBenchmark` only if execution ends up changing OAuth/session startup behavior instead of keeping the change inside the smoke harness/workflow layer
- Contract impact summary
- `README.md`: yes
- REST Docs / `src/docs/asciidoc/`: no expected change
- OpenAPI baseline: no expected change
- HTTP examples: no expected change
- Benchmarks: no expected change unless runtime startup behavior changes

## Execution Milestones
### Milestone 1: Expand Packaged Smoke Coverage
- goal
- Define the new release-confidence behavior in executable external smoke specs before changing the harness.
- files to update
- `src/externalTest/java/team/jit/technicalinterviewdemo/external/ExternalSmokeTests.java`
- `src/externalTest/java/team/jit/technicalinterviewdemo/external/ExternalHttpTestSupport.java`
- likely one new external support/helper class under `src/externalTest/java/team/jit/technicalinterviewdemo/external/`
- `buildSrc/src/main/kotlin/team/jit/technicalinterviewdemo/build/ExternalTestingConventionsPlugin.kt`
- `buildSrc/src/main/kotlin/team/jit/technicalinterviewdemo/build/ExternalSmokeEnvironmentUpTask.kt`
- `buildSrc/src/main/kotlin/team/jit/technicalinterviewdemo/build/ExternalSmokeEnvironmentDownTask.kt`
- `buildSrc/src/main/kotlin/team/jit/technicalinterviewdemo/build/DockerSupport.kt`
- behavior to preserve
- `externalSmokeTest` remains a dedicated verification task outside `build`.
- The smoke environment still validates the same Docker image coordinate that `dockerBuild` and the release workflow later publish.
- No application controller/security contract changes are introduced just to support smoke auth.
- exact deliverables
- Add smoke assertions for the generated docs index at `/docs/index.html` instead of only the `/docs` redirect.
- Add smoke assertions for `/v3/api-docs` and `/v3/api-docs.yaml`.
- Extend the smoke harness so external tests can connect to the smoke PostgreSQL instance directly, seed a Spring Session-backed authenticated session, and issue a real `GET /api/account` request with the `technical-interview-demo-session` cookie.
- Assert the session bootstrap created rows in `SPRING_SESSION` and `SPRING_SESSION_ATTRIBUTES` and that the packaged app accepts the persisted session.
- Keep readiness and Flyway verification intact as preconditions, not replacements, for the new assertions.

### Milestone 2: Publish Coverage And Simplify Release Tooling
- goal
- Make CI surface coverage externally and remove helper-script indirection that is no longer justified.
- files to update
- `.github/workflows/ci.yml`
- `.github/workflows/release.yml`
- `README.md`
- `SETUP.md`
- `ai/RELEASES.md`
- optional `.github/codecov.yml`
- `scripts/ci/smoke-container.ps1`
- `scripts/run-phase-9-benchmarks.ps1`
- `scripts/release/render-release-notes.ps1`
- exact deliverables
- Upload the existing JaCoCo XML report from CI to Codecov in the same workflow run that already executes `./gradlew build`.
- Add CI and Codecov badges to `README.md` once the upload path is active and stable.
- Inline release-note rendering logic into `.github/workflows/release.yml` if the resulting workflow is shorter and easier to review than the separate PowerShell script.
- Delete script wrappers that no longer provide real reuse, especially if they only forward to Gradle or a single workflow step.
- Keep the release workflow fail-closed behavior when the matching `CHANGELOG.md` section is missing or duplicated.

### Milestone 3: Align Repo Guidance And Final Validation
- goal
- Leave the repository guidance accurate after the workflow and smoke-harness changes.
- files to update
- `README.md`
- `SETUP.md`
- `ai/RELEASES.md`
- `ROADMAP.md` only if the work is later executed and released, not as part of this implementation
- exact deliverables
- Update setup/release instructions to point to the workflow/Gradle source of truth after any script removals.
- Explicitly document the new smoke-test guarantees at the level already described by repo guidance: generated docs surface, OpenAPI endpoints, and session-backed authenticated account verification.
- Keep `README.md` limited to human-facing contract and release/CI summary changes; do not move detailed setup/runbook content out of `SETUP.md`.

## Edge Cases And Failure Modes
- `/docs` can still redirect correctly while `/docs/index.html` is missing or stale in the packaged jar; the new smoke spec must assert the actual generated page content is served.
- `/v3/api-docs` and `/v3/api-docs.yaml` can break independently through packaging, Springdoc config, or security matcher regressions; smoke coverage should hit both.
- A session row can exist while authenticated requests still fail if the serialized security context is malformed, the cookie name changes, or the app cannot deserialize/authenticate the stored context. The smoke path must validate the end-to-end request, not just table counts.
- The smoke auth flow must not require live GitHub OAuth, browser automation, or internet access; otherwise CI/release validation becomes brittle and non-reproducible.
- If database host-port publication is added for smoke support, the task must remain deterministic, configurable, and cleaned up by `externalSmokeEnvironmentDown`.
- If the release-note script is removed, the in-workflow logic must still reject missing or duplicate changelog sections and must continue to include semantic-tag image, immutable short-SHA image, and package-page metadata.
- Codecov onboarding may require repository-level setup outside the repo. The implementation must document that prerequisite clearly instead of leaving CI green but coverage unpublished.
- Do not accidentally pull `externalSmokeTest` into `build` or `check`; that would change the repo’s established validation model.

## Validation Plan
- commands to run
- `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.technical.info.TechnicalOverviewControllerIntegrationTests`
- `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.technical.docs.OpenApiIntegrationTests`
- `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.business.user.UserManagementIntegrationTests`
- `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.technical.security.SecurityIntegrationTests`
- `.\gradlew.bat externalSmokeTest -PexternalSmokeImageName=technical-interview-demo -PdockerImageName=technical-interview-demo`
- `.\gradlew.bat build`
- `.\gradlew.bat gatlingBenchmark` only if implementation changed OAuth/session startup behavior in the application runtime
- tests to add or update
- Update `ExternalSmokeTests` to codify the expanded packaged-image smoke contract.
- Add smoke-test support code needed to seed and verify a JDBC-backed authenticated session without changing application endpoints.
- Keep existing docs, OpenAPI, and session integration tests green as the preserved contract.
- docs or contract checks
- Confirm `README.md` reflects CI/Codecov badges and any updated workflow description.
- Confirm `SETUP.md` and `ai/RELEASES.md` no longer point to deleted scripts and still describe the correct release/smoke reproduction path.
- Confirm REST Docs pages, HTTP examples, and the approved OpenAPI baseline remain unchanged unless execution uncovers an actual contract drift.
- manual verification steps
- Inspect the `externalSmokeTest` logs and confirm they now cover docs index, OpenAPI JSON, OpenAPI YAML, and authenticated account verification in addition to readiness/Flyway checks.
- Inspect the packaged app during the smoke run and confirm the authenticated smoke request uses the existing `technical-interview-demo-session` cookie name.
- In a branch or PR run, verify the CI workflow uploads coverage to Codecov and that the badge target resolves correctly from the rendered `README.md`.
- If release-note logic moves into the workflow, review the rendered notes artifact from a tag-style dry run or equivalent local reproduction and confirm it matches the intended `CHANGELOG.md` section plus image metadata.

## Better Engineering Notes
- Prefer a database-backed session bootstrap in the smoke harness over any production-code auth bypass. That keeps the demo contract frozen and still proves the packaged runtime honors Spring Session JDBC state.
- Keep the new smoke assertions narrow. The goal is release-confidence coverage, not a new end-to-end test suite with browser automation or a mocked OAuth provider stack.
- Delete unneeded scripts rather than keeping thin wrappers that no longer carry unique behavior. The repo already established Gradle tasks as the source of truth for smoke and benchmark flows.
- If Codecov configuration needs repo-specific secrets or onboarding outside code, document that explicitly in the changed guidance so future maintainers can reproduce the setup.

## Validation Results
- 2026-05-02: Ran `.\gradlew.bat externalSmokeVerification --tests team.jit.technicalinterviewdemo.external.ExternalSmokeTests.accountEndpointAcceptsJdbcBackedAuthenticatedSession -PexternalSmokeImageName=technical-interview-demo -PdockerImageName=technical-interview-demo --no-daemon`. Failed first with `401` because the smoke helper sent the raw JDBC session id instead of the base64-encoded `technical-interview-demo-session` cookie value Spring Session expects.
- 2026-05-02: Ran `.\gradlew.bat externalSmokeEnvironmentUp -x test -x asciidoctor -PexternalSmokeImageName=technical-interview-demo -PdockerImageName=technical-interview-demo --no-daemon` to inspect the packaged smoke environment. Confirmed the seeded session rows existed in `SPRING_SESSION` / `SPRING_SESSION_ATTRIBUTES` and isolated the cookie-encoding mismatch against the running container.
- 2026-05-02: Ran `.\gradlew.bat externalSmokeEnvironmentDown -PexternalSmokeImageName=technical-interview-demo -PdockerImageName=technical-interview-demo --no-daemon` after the manual smoke-environment inspection. Passed.
- 2026-05-02: Re-ran `.\gradlew.bat externalSmokeVerification --tests team.jit.technicalinterviewdemo.external.ExternalSmokeTests.accountEndpointAcceptsJdbcBackedAuthenticatedSession -PexternalSmokeImageName=technical-interview-demo -PdockerImageName=technical-interview-demo --no-daemon` after encoding the session cookie. Passed.
- 2026-05-02: Ran `.\gradlew.bat externalSmokeTest -PexternalSmokeImageName=technical-interview-demo -PdockerImageName=technical-interview-demo --no-daemon`. Passed with 9 external smoke assertions, including `/docs/index.html`, `/v3/api-docs`, `/v3/api-docs.yaml`, and the JDBC-backed authenticated `/api/account` check.
- 2026-05-02: Ran `git diff --check` after the CI/release-workflow and doc updates for coverage publication plus script cleanup. Passed.
- 2026-05-02: Ran `rg -n "render-release-notes|smoke-container|run-phase-9-benchmarks" README.md SETUP.md ai/RELEASES.md .github/workflows scripts`. Returned no matches, confirming the deleted wrapper-script references were removed.
- 2026-05-02: Executed the inlined release-note extraction logic locally against `CHANGELOG.md` section `v1.1.0` and wrote then removed `release-notes-smoke.md`. Passed, confirming the workflow logic still fails closed on tag matching and renders the expected section format.
- 2026-05-02: Codecov upload was not runnable from the local shell because it depends on the GitHub Actions environment plus repository Codecov onboarding. The CI workflow was updated to publish `build/reports/jacoco/test/jacocoTestReport.xml` through `codecov/codecov-action@v5` with OIDC authentication.
- 2026-05-02: Ran `.\gradlew.bat build --no-daemon`. Passed, including tests, JaCoCo coverage verification, Asciidoctor generation, PMD, Spotless, dependency and image vulnerability scans, and Docker image build.

## User Validation
- Confirm the new plan execution leaves the public API unchanged by reviewing that REST Docs pages, HTTP examples, and the approved OpenAPI baseline did not move.
- Run `.\gradlew.bat externalSmokeTest -PexternalSmokeImageName=technical-interview-demo -PdockerImageName=technical-interview-demo` and verify the smoke output now includes docs/OpenAPI/account coverage.
- Review the GitHub Actions diff and confirm CI now publishes coverage and the release workflow no longer depends on an unnecessary helper script.
