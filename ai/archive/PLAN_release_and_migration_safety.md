# Plan: Release And Migration Safety Hardening

## Lifecycle
| Field | Value |
| --- | --- |
| Phase | Closed |
| Status | Released |

## Summary
- Implement one coherent release-safety plan from these checked `ROADMAP.md` items:
  - `Define a rolling-compatible Flyway rollout model, including expand-and-contract rules, mixed-version compatibility expectations, and schema-first versus app-first ordering by migration type`
  - `Add automated backup-restore verification or at least a reproducible pre-release restore drill for migration-bearing releases`
  - `Add deployment checks that validate the exact published image, runtime configuration, and mixed-version readiness before promotion beyond local or CI environments`
  - `Document and validate a realistic disaster-recovery path instead of only a local rollback narrative`
- Build on the repo's existing release, smoke-test, Flyway, and restore guidance instead of inventing a second deployment platform or backup system.
- Success is measured by:
  - a checked-in sidecar metadata convention for newly added or modified Flyway migration SQL files
  - a repo-owned `scripts/release/get-release-migration-impact.ps1` helper that classifies release candidates as non-migration-bearing, rolling-compatible, or restore-sensitive
  - a deployment-check path that verifies `build.version`, `git.shortCommitId`, `prod` profile activation, JDBC session storage, and the documented prod timeout against the deployed target
  - a reproducible `scripts/release/invoke-restore-drill.ps1` flow that runs the tagged image against a restored PostgreSQL instance and reuses `externalDeploymentCheck`
  - aligned maintainer docs for rollout ordering, restore evidence, and disaster-recovery decisions

## Scope
- In scope:
  - maintainer-facing release, restore, and disaster-recovery guidance
  - repo-owned release helper scripts or Gradle workflow glue for migration-bearing release validation
  - deployment-check improvements around exact published image identity, runtime configuration, and release readiness
  - mixed-version readiness policy and enforcement guidance for Flyway migrations
  - post-release or pre-promotion validation workflow changes when they stay GitHub-native and reuse the existing external deployment check path
- Out of scope:
  - introducing a managed backup product, operator, or hosted disaster-recovery service
  - adding Flyway undo migrations or replacing Flyway with a different migration system
  - redesigning the application API, auth contract, or production deployment topology
  - Kubernetes admission control, blue-green orchestration, or cluster-specific rollout controllers
  - automatic rollback of production databases without an explicitly restored snapshot or forward-fix release

## Current State
- `README.md` already documents:
  - tagged releases, semantic versioning, and post-tag verification of published artifacts
  - vendor-neutral backup retention expectations and a restore-drill baseline
  - the existence of `externalSmokeTest`, `scheduledExternalCheck`, and the tag-driven `Release` workflow
- `SETUP.md` already contains an upgrade and rollback section with:
  - a pre-release checklist
  - backup and retention expectations
  - a recommended restore drill
  - rollback warnings for migration-bearing releases
- `.github/workflows/release.yml` currently validates the built tagged image locally with `externalSmokeTest`, publishes signed and attestable GHCR artifacts, and creates the GitHub Release, but it does not gate promotion with a deployed-environment validation of the exact published image.
- `.github/workflows/post-deploy-smoke.yml` already runs `./gradlew scheduledExternalCheck` on a schedule or manual dispatch, and `buildSrc/.../ExternalTestingConventionsPlugin.kt` already provides `externalDeploymentCheck` plus optional JDBC-backed session and Flyway assertions against an already deployed environment.
- `src/externalTest/java/.../ExternalSmokeTests.java` already verifies readiness, docs, OpenAPI endpoints, a public book read, JDBC-backed authenticated `/api/account`, and Flyway history presence, but it does not yet prove that a target environment is running the intended released version or that release-candidate runtime configuration matches the expected prod posture.
- `GET /` already exposes repo-owned build and runtime metadata through `TechnicalOverviewResponse`, including:
  - `build.version`
  - `git.shortCommitId`
  - `runtime.activeProfiles`
  - `configuration.session.storeType`
  - `configuration.session.timeout`
  - `configuration.security.csrfEnabled`
  so deployment checks can bind to documented runtime truth without adding a new operational endpoint.
- `GET /actuator/info` is already part of the supported operational surface and should remain the lighter-weight operational metadata endpoint, but the existing root overview endpoint is the stronger source of runtime posture details for deployment validation.
- Flyway migrations currently live under `src/main/resources/db/migration/` as plain versioned SQL files (`V1` through `V6`) with no checked-in rollout classification or mixed-version compatibility metadata, so migration review still depends on ad hoc human judgment during release preparation.
- `application-prod.properties` already fixes the baseline prod posture around Flyway-owned schema validation, demo data disabled, secure session cookies by default, and the one-session-per-login policy; deployment checks can reuse that published posture instead of inventing a separate runtime contract.

## Requirement Gaps And Open Questions
- No blocking user-input gap remains for planning.
- Non-blocking environment assumption:
  - promotion-stage deployment checks can target one stable base URL and, when deeper database validation is needed, one JDBC connection for the deployed database
  - why it matters: the existing `externalDeploymentCheck` model already assumes one deployment endpoint and optional JDBC access
  - fallback if this assumption does not hold during implementation: keep the repo-owned checks environment-agnostic and document any environment-specific orchestration outside the repository rather than embedding platform-specific logic here
- Non-blocking rollout assumption:
  - the rollout model should optimize for one shared PostgreSQL schema with rolling application instances, not for per-tenant databases or independent schema-per-pod deployment strategies
  - why it matters: the roadmap language is about mixed-version compatibility during upgrades, which fits the current single-schema repo posture
  - fallback if a future deployment model diverges: keep the policy explicit that the checked-in rollout rules apply only to the shared-schema rolling-upgrade path
- Non-blocking backup-system assumption:
  - this repository can validate restores and document recovery, but it does not own backup creation itself
  - why it matters: the roadmap item explicitly allows a reproducible restore drill when full backup-restore automation is not repo-owned
  - fallback: automate only the repo-owned restore-validation steps and keep backup capture vendor-neutral in docs
- Non-blocking artifact-identity assumption:
  - the running application can prove release identity through checked-in build and git metadata, but it cannot directly prove the immutable GHCR digest from inside the JVM
  - why it matters: the roadmap item asks for validation of the exact published image, while the repo-owned application surface currently exposes version and commit metadata rather than image digests
  - fallback: the implementation should treat `build.version` plus `git.shortCommitId` as the in-app identity proof and pair that with the release workflow's published semantic tag, short-SHA tag, and digest-first release metadata instead of inventing platform-specific container-runtime inspection

## Locked Decisions And Assumptions
- Include exactly the four checked roadmap items under `Make Releases And Migrations Safer` and no unrelated security-posture or supply-chain work.
- Keep the implementation narrow and maintainer-focused: prefer docs, scripts, workflow wiring, and external checks over application runtime changes unless a deployment validation cannot be expressed otherwise.
- Reuse the existing `externalDeploymentCheck` and `Post-Deploy Smoke` path as the base for promotion-style validation instead of creating a second smoke-test framework.
- Treat the exact published release artifact as the deployment-check subject of truth; do not rely only on a locally built image once the release has been pushed.
- Implement migration classification through checked-in sidecar metadata files under `src/main/resources/db/migration/metadata/`, one per new or modified migration SQL file in the release candidate, rather than through SQL parsing heuristics or a single handwritten checklist.
- Use a repo-owned PowerShell helper named `scripts/release/get-release-migration-impact.ps1` to compare the release candidate against the previous release tag, discover newly added or modified migration SQL files, require matching metadata sidecars, and emit a fail-closed summary with one of these release-impact states:
  - `none`
  - `rolling-compatible`
  - `restore-sensitive`
- Keep historical migrations untouched unless they are actively modified; the new metadata requirement applies to migration SQL files introduced or changed after the convention lands.
- Extend `externalDeploymentCheck` so it can accept expected release identity and prod-posture inputs, then assert them through the existing public root overview endpoint rather than adding a bespoke deployment-only API.
- Reuse `workflow_dispatch` on `.github/workflows/post-deploy-smoke.yml` for promotion-stage checks by adding explicit expected-version or expected-commit inputs; do not create a second deploy-check workflow unless the existing one proves incapable of carrying those inputs cleanly.
- Implement the reproducible restore drill as a repo-owned PowerShell helper named `scripts/release/invoke-restore-drill.ps1` that starts the target release image against a separately restored PostgreSQL instance, waits for readiness, runs `./gradlew externalDeploymentCheck`, and tears the container down.
- Define mixed-version readiness through an explicit migration rollout model plus repo-owned validation inputs, not through vague human review alone.
- Keep the disaster-recovery story vendor-neutral and PostgreSQL plus Flyway centered; the repository should describe and validate recovery steps, not provision backup infrastructure.
- Preserve the current public API and OpenAPI contract unless a deployment-check endpoint assertion exposes a genuine documentation gap that must be fixed.

## Affected Artifacts
- Tests:
  - `src/externalTest/java/team/jit/technicalinterviewdemo/external/ExternalSmokeTests.java`
  - `src/externalTest/java/team/jit/technicalinterviewdemo/external/ExternalSessionSupport.java`
  - `src/externalTest/java/team/jit/technicalinterviewdemo/external/ExternalHttpTestSupport.java` if deployment identity assertions need shared JSON helpers or parameter parsing
  - additional external-test helpers under `src/externalTest/java/team/jit/technicalinterviewdemo/external/` only if deployment assertions need factoring
- Docs:
  - `README.md`
  - `SETUP.md`
  - `ai/RELEASES.md`
  - optionally `CONTRIBUTING.md` only if maintainer-facing release or recovery expectations overlap materially there
- OpenAPI:
  - none expected unless deployment-check assertions reveal missing public operational metadata that is intentionally promoted into the supported contract
- HTTP examples:
  - none expected
- Source files:
  - `.github/workflows/release.yml`
  - `.github/workflows/post-deploy-smoke.yml`
  - `buildSrc/src/main/kotlin/team/jit/technicalinterviewdemo/build/ExternalTestingConventionsPlugin.kt`
  - optionally `buildSrc/src/main/kotlin/team/jit/technicalinterviewdemo/build/ExternalSmokeEnvironmentUpTask.kt` if restore-drill provisioning or readiness reuse needs tighter factoring
  - new `scripts/release/get-release-migration-impact.ps1`
  - new `scripts/release/invoke-restore-drill.ps1`
  - new metadata sidecars under `src/main/resources/db/migration/metadata/` for future migration SQL files
- Build or benchmark checks:
  - `.\gradlew.bat build`
  - workflow lint for `.github/workflows/*.yml`
  - targeted deployed-environment validation through `.\gradlew.bat externalDeploymentCheck` or `.\gradlew.bat scheduledExternalCheck` with explicit inputs during implementation validation
  - no OpenAPI refresh expected
  - no `gatlingBenchmark` rerun expected unless the implementation unexpectedly changes OAuth or session startup behavior

## Execution Milestones
### Milestone 1: Lock The Rollout And Recovery Contract
- goal:
  - define the release, migration, restore, and disaster-recovery posture in the owning docs before changing scripts or workflows.
- files to update:
  - `README.md`
  - `SETUP.md`
  - `ai/RELEASES.md`
  - optionally `CONTRIBUTING.md` if maintainer expectations overlap there materially
- behavior to preserve:
  - current release tagging and signed-image publication stay tag-driven
  - current backup guidance remains vendor-neutral
  - current public API contract stays unchanged
- exact deliverables:
  - explicit rolling-upgrade Flyway model with migration categories and expand-and-contract sequencing guidance
  - documented distinction between rolling-compatible and non-rolling-compatible migration-bearing releases
  - documented disaster-recovery flow that distinguishes image-only rollback from restore-required recovery
  - explicit maintainer release checklist additions for migration review, restore-drill evidence, deployment-check completion before promotion, and the expected use of `get-release-migration-impact.ps1`

### Milestone 2: Encode Migration-Bearing Release Review
- goal:
  - replace ad hoc migration review with repo-owned, reviewable release-candidate inputs.
- files to update:
  - new `scripts/release/get-release-migration-impact.ps1`
  - new `src/main/resources/db/migration/metadata/` sidecar files or a checked-in metadata README in that directory
  - `ai/RELEASES.md`
  - optionally `README.md` or `SETUP.md` if the human-facing rollout model needs exact script usage
- behavior to preserve:
  - Flyway remains the migration owner
  - existing historical migrations remain valid and do not require destructive rewrites
  - release preparation stays non-interactive and scriptable
- exact deliverables:
  - one repo-owned way to inspect which migrations are new in a release candidate by diffing against the previous release tag
  - one checked-in sidecar metadata schema for future migration SQL files with at least:
    - rollout category such as `expand`, `contract`, `backfill`, or `breaking`
    - deployment order such as `db-first`, `app-first`, or `out-of-band`
    - rolling compatibility flag
    - rollback posture such as `image-only` or `forward-fix-or-restore`
    - short human summary
  - fail-closed or clearly blocking guidance when migration metadata or compatibility classification is missing for a changed migration SQL file
  - release-check output that tells maintainers whether the release is `none`, `rolling-compatible`, or `restore-sensitive`, plus the exact migration files that drove that result

### Milestone 3: Validate The Exact Published Image Before Promotion
- goal:
  - make deployment checks prove that the deployed target matches the intended published artifact and expected prod runtime posture.
- files to update:
  - `.github/workflows/post-deploy-smoke.yml`
  - `.github/workflows/release.yml` if workflow handoff needs release-summary hints for semantic tag, short-SHA tag, and digest reference
  - `buildSrc/src/main/kotlin/team/jit/technicalinterviewdemo/build/ExternalTestingConventionsPlugin.kt`
  - `src/externalTest/java/team/jit/technicalinterviewdemo/external/ExternalSmokeTests.java`
  - supporting external-test helpers under `src/externalTest/java/team/jit/technicalinterviewdemo/external/`
- behavior to preserve:
  - `externalSmokeTest` remains the Docker-backed local packaged-image validation gate
  - `externalDeploymentCheck` remains the deployed-environment validation entry point
  - scheduled smoke remains optional when target secrets are not configured
- exact deliverables:
  - `externalDeploymentCheck` can accept expected release identity inputs such as:
    - `externalCheck.expectedBuildVersion`
    - `externalCheck.expectedShortCommitId`
    - `externalCheck.expectedActiveProfile=prod`
    - `externalCheck.expectedSessionStoreType=jdbc`
    - `externalCheck.expectedSessionTimeout=15m`
  - deployment checks fail when the target root overview endpoint does not expose the expected build version, short commit id, active profile, JDBC session store, prod timeout, or the documented `csrfEnabled=false` posture
  - `.github/workflows/post-deploy-smoke.yml` `workflow_dispatch` exposes those expected identity inputs so promotion-stage validation can be run deliberately against a deployed environment
  - release or promotion automation clearly points maintainers at the exact published image reference to validate, while the deployed app proves the matching tag-plus-commit identity through build metadata
  - mixed-version readiness is validated through the new migration classification and deployment-check inputs instead of remaining implicit tribal knowledge

### Milestone 4: Make Restore And Disaster-Recovery Validation Reproducible
- goal:
  - turn the current restore narrative into a repeatable repo-owned validation flow for migration-bearing releases.
- files to update:
  - `SETUP.md`
  - `README.md`
  - new `scripts/release/invoke-restore-drill.ps1`
  - `ai/RELEASES.md`
- behavior to preserve:
  - no repository-owned backup capture mechanism is introduced
  - restore validation still works against a separately restored PostgreSQL instance
- exact deliverables:
  - one reproducible restore-drill workflow that can exercise a restored database with the target tagged image or exact release candidate image by:
    - accepting an image reference, base URL, JDBC URL, JDBC user, and JDBC password
    - starting the app container against the restored database with the `prod` profile
    - waiting for readiness
    - invoking `./gradlew externalDeploymentCheck` with the expected build and runtime inputs
    - shutting the app container down afterward
  - clear success criteria covering readiness, build metadata, Flyway state, core table presence, and at least one authenticated check through `/api/account`
  - explicit guidance for when recovery should prefer image rollback, database restore, or forward-fix release based on migration-bearing status

## Edge Cases And Failure Modes
- A migration may be syntactically valid for Flyway but unsafe for rolling upgrades because old and new application versions cannot both tolerate the intermediate schema state.
- A release with no changed migration SQL files should skip migration metadata enforcement and restore-sensitive gating cleanly rather than forcing busywork.
- The deployment target may answer health checks from an older image even while a new release is being promoted; deployment checks must bind to expected build metadata rather than trusting only endpoint liveness.
- Migration-bearing releases without JDBC access to the deployed database cannot prove Flyway or session-table expectations deeply; the plan should distinguish HTTP-only checks from deeper database-backed checks.
- Optional prod features such as the `oauth` profile may vary by environment; deployment checks should assert only the invariant prod posture that the repo documents universally, not environment-specific optional-provider settings.
- Backup guidance without restore validation gives false confidence; the plan must fail closed or stay explicit when no restore evidence is available for a migration-bearing release.
- Disaster recovery for a migration-bearing release cannot be reduced to `kubectl rollout undo` or image retagging when the schema has already moved forward.
- Workflow changes must not assume a single hosting platform beyond GitHub Actions and a reachable deployment URL; cluster-specific deployment orchestration belongs outside this repository.
- Any migration metadata convention must avoid rewriting historical release artifacts unnecessarily; only future migrations or actively changed files should need new classification data unless there is a strong reason to backfill examples.

## Validation Plan
- commands to run:
  - `docker run --rm -v "${PWD}:/repo" -w /repo rhysd/actionlint:latest`
  - `.\gradlew.bat build`
  - targeted deployment validation command(s) introduced by the implementation, expected to include:
    - `.\gradlew.bat externalDeploymentCheck --no-daemon -PexternalCheck.baseUrl=... -PexternalCheck.expectedBuildVersion=... -PexternalCheck.expectedShortCommitId=... -PexternalCheck.expectedActiveProfile=prod -PexternalCheck.expectedSessionStoreType=jdbc -PexternalCheck.expectedSessionTimeout=15m`
    - `pwsh ./scripts/release/get-release-migration-impact.ps1 -CurrentRef ... -PreviousReleaseTag ...`
    - `pwsh ./scripts/release/invoke-restore-drill.ps1 -ImageReference ... -BaseUrl ... -JdbcUrl ... -JdbcUser ... -JdbcPassword ...`
- tests to add or update:
  - external smoke or deployment tests that assert expected build metadata and deeper prod-posture checks against the deployed target
  - focused tests for any new helper code or script parsing logic when that logic is complex enough to justify direct validation
- docs or contract checks:
  - confirm `README.md`, `SETUP.md`, and `ai/RELEASES.md` agree on migration-bearing release review, restore expectations, and disaster-recovery posture
  - confirm docs distinguish image-only rollback from restore-required recovery
  - confirm docs describe deployment checks against the exact published release artifact instead of only a local image
  - confirm the checked-in migration metadata convention is documented consistently where maintainers are expected to use it
  - confirm no OpenAPI, REST Docs, or HTTP example artifacts changed unexpectedly unless an intentional contract change was required
- manual verification steps:
  - inspect the migration-review helper output for a simulated or real migration-bearing diff and verify it classifies rollout compatibility clearly and names the exact changed migration files
  - inspect the deployment-check path and verify it can prove the target environment is running the intended release build metadata by checking `build.version` and `git.shortCommitId`
  - run the documented restore drill against a disposable PostgreSQL instance and confirm the expected readiness, Flyway, prod-profile, and authenticated-check steps are reproducible
  - after the first real release using the new flow, verify that maintainers can distinguish rollback-safe and restore-sensitive releases without reverse-engineering migration SQL from scratch

## Better Engineering Notes
- The repo already has most of the plumbing for deployed validation through `externalDeploymentCheck`; extend that path instead of adding a parallel deploy-test framework.
- A small, explicit migration classification convention is preferable to a sophisticated SQL analyzer. The goal is reviewable rollout intent, not fake certainty from brittle parsing.
- The root technical overview endpoint already exposes the build and runtime fields needed for deployment validation. Prefer reusing that contract over creating a new hidden maintenance endpoint.
- The roadmap item allows a reproducible restore drill in place of a full automated backup system. Stay within that boundary unless the user later asks for backup-platform integration.
- Do not widen this plan into generalized SRE automation, Helm rollout controllers, or cloud-provider-specific disaster-recovery tooling. Keep the repository focused on a strong sample release process and verifiable maintainer runbooks.

## Validation Results
- `docker run --rm -v "${PWD}:/repo" -w /repo rhysd/actionlint:latest`
  - passed
- `pwsh -NoLogo -NoProfile -File ./scripts/release/get-release-migration-impact.ps1 -PreviousReleaseTag v1.5.1 -CurrentRef HEAD`
  - passed with `none` because no migration SQL files changed between `v1.5.1` and `HEAD`
- `.\gradlew.bat build --no-daemon` after explicitly loading `.env` into the shell environment for `JAVA_HOME`
  - passed
- `pwsh -NoLogo -NoProfile -File ./scripts/release/invoke-restore-drill.ps1 -ImageReference technical-interview-demo:latest -BaseUrl http://127.0.0.1:18081 -JdbcUrl jdbc:postgresql://localhost:15433/technical_interview_demo_restore -JdbcUser postgres -JdbcPassword changeme -ExpectedBuildVersion v1.5.1-2-g46c4098.dirty -ExpectedShortCommitId 46c4098 -ContainerName technical-interview-demo-restore-drill-app`
  - passed during implementation validation against a disposable `postgres:16-alpine` container mapped to `localhost:15433`
  - confirmed readiness, `build.version`, `git.shortCommitId`, `prod` profile activation, JDBC session storage, `15m` session timeout, `csrfEnabled=false`, Flyway history, and authenticated `GET /api/account` behavior through `externalDeploymentCheck`
  - not rerun for the final `v1.6.0` release candidate because `get-release-migration-impact.ps1` classified the candidate as `none`, so restore-drill evidence was not required by the release checklist

## User Validation
- Open the new plan and confirm it includes exactly these four checked roadmap items and no unrelated security or platform work:
  - rolling-compatible Flyway rollout model
  - restore verification for migration-bearing releases
  - deployment checks against the exact published image and runtime posture
  - realistic disaster-recovery guidance
- After implementation, inspect the release and setup docs plus the deployment-check path and confirm the repo now answers these operational questions explicitly:
  - is this release safe for rolling deployment?
  - what database restore evidence exists for it?
  - how do we validate the exact published artifact in a target environment?
  - when is image rollback sufficient versus restore or forward-fix recovery?
