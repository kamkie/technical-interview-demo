# Plan: Release And Migration Safety Hardening

## Summary
- Implement one coherent release-safety plan from these checked `ROADMAP.md` items:
  - `Define a rolling-compatible Flyway rollout model, including expand-and-contract rules, mixed-version compatibility expectations, and schema-first versus app-first ordering by migration type`
  - `Add automated backup-restore verification or at least a reproducible pre-release restore drill for migration-bearing releases`
  - `Add deployment checks that validate the exact published image, runtime configuration, and mixed-version readiness before promotion beyond local or CI environments`
  - `Document and validate a realistic disaster-recovery path instead of only a local rollback narrative`
- Build on the repo's existing release, smoke-test, Flyway, and restore guidance instead of inventing a second deployment platform or backup system.
- Success is measured by: a checked-in rollout model for new Flyway migrations, repo-owned release candidate checks for migration-bearing releases, a deployment-check path that validates the exact published image and expected prod runtime posture, and aligned maintainer docs for restore and disaster recovery.

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

## Locked Decisions And Assumptions
- Include exactly the four checked roadmap items under `Make Releases And Migrations Safer` and no unrelated security-posture or supply-chain work.
- Keep the implementation narrow and maintainer-focused: prefer docs, scripts, workflow wiring, and external checks over application runtime changes unless a deployment validation cannot be expressed otherwise.
- Reuse the existing `externalDeploymentCheck` and `Post-Deploy Smoke` path as the base for promotion-style validation instead of creating a second smoke-test framework.
- Treat the exact published release artifact as the deployment-check subject of truth; do not rely only on a locally built image once the release has been pushed.
- Define mixed-version readiness through an explicit migration rollout model plus repo-owned validation inputs, not through vague human review alone.
- Keep the disaster-recovery story vendor-neutral and PostgreSQL plus Flyway centered; the repository should describe and validate recovery steps, not provision backup infrastructure.
- Preserve the current public API and OpenAPI contract unless a deployment-check endpoint assertion exposes a genuine documentation gap that must be fixed.

## Affected Artifacts
- Tests:
  - `src/externalTest/java/team/jit/technicalinterviewdemo/external/ExternalSmokeTests.java`
  - `src/externalTest/java/team/jit/technicalinterviewdemo/external/ExternalSessionSupport.java`
  - additional external-test helpers under `src/externalTest/java/team/jit/technicalinterviewdemo/external/` if deployment assertions need factoring
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
  - optionally `buildSrc/src/main/kotlin/team/jit/technicalinterviewdemo/build/ExternalSmokeEnvironmentUpTask.kt` if release-candidate provisioning needs tighter validation reuse
  - new helper script(s) under `scripts/release/`, likely for migration-bearing release inspection and restore-drill or deployment-check orchestration
  - optionally new repo-owned metadata or conventions under `src/main/resources/db/migration/` if migration rollout classification is made explicit in checked-in files
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
  - explicit maintainer release checklist additions for migration review, restore-drill evidence, and deployment-check completion before promotion

### Milestone 2: Encode Migration-Bearing Release Review
- goal:
  - replace ad hoc migration review with repo-owned, reviewable release-candidate inputs.
- files to update:
  - new helper script(s) under `scripts/release/`
  - `src/main/resources/db/migration/` conventions or checked-in metadata files if needed
  - `ai/RELEASES.md`
  - optionally `README.md` or `SETUP.md` if the human-facing rollout model needs exact script usage
- behavior to preserve:
  - Flyway remains the migration owner
  - existing historical migrations remain valid and do not require destructive rewrites
  - release preparation stays non-interactive and scriptable
- exact deliverables:
  - one repo-owned way to inspect which migrations are new in a release candidate
  - one checked-in policy for classifying migration-bearing releases by rollout compatibility and deployment ordering
  - fail-closed or clearly blocking guidance when migration metadata or compatibility classification is missing
  - release-check output that tells maintainers whether the release is image-only rollback capable, restore-sensitive, or requires a forward-fix posture

### Milestone 3: Validate The Exact Published Image Before Promotion
- goal:
  - make deployment checks prove that the deployed target matches the intended published artifact and expected prod runtime posture.
- files to update:
  - `.github/workflows/post-deploy-smoke.yml`
  - optionally `.github/workflows/release.yml` if workflow handoff needs image-reference outputs or release summary hints
  - `buildSrc/src/main/kotlin/team/jit/technicalinterviewdemo/build/ExternalTestingConventionsPlugin.kt`
  - `src/externalTest/java/team/jit/technicalinterviewdemo/external/ExternalSmokeTests.java`
  - supporting external-test helpers under `src/externalTest/java/team/jit/technicalinterviewdemo/external/`
  - optionally new helper script(s) under `scripts/release/`
- behavior to preserve:
  - `externalSmokeTest` remains the Docker-backed local packaged-image validation gate
  - `externalDeploymentCheck` remains the deployed-environment validation entry point
  - scheduled smoke remains optional when target secrets are not configured
- exact deliverables:
  - deployment checks can accept expected release identity inputs such as semantic version, short SHA tag, or immutable digest-derived metadata and fail when the target does not expose the intended build
  - deployment checks verify the expected prod runtime contract already documented by the repository, not just generic `200 OK` responses
  - release or promotion automation clearly points maintainers at the exact published image reference to validate
  - mixed-version readiness is validated through the new migration classification and deployment-check inputs instead of remaining implicit tribal knowledge

### Milestone 4: Make Restore And Disaster-Recovery Validation Reproducible
- goal:
  - turn the current restore narrative into a repeatable repo-owned validation flow for migration-bearing releases.
- files to update:
  - `SETUP.md`
  - `README.md`
  - new helper script(s) under `scripts/release/`
  - optionally `.github/workflows/post-deploy-smoke.yml` or a new manual workflow only if the restore validation can stay repo-owned and environment-agnostic
  - `ai/RELEASES.md`
- behavior to preserve:
  - no repository-owned backup capture mechanism is introduced
  - restore validation still works against a separately restored PostgreSQL instance
- exact deliverables:
  - one reproducible restore-drill workflow that can exercise a restored database with the target tagged image or exact release candidate image
  - clear success criteria covering readiness, build metadata, Flyway state, core table presence, and at least one authenticated or operator-facing check
  - explicit guidance for when recovery should prefer image rollback, database restore, or forward-fix release based on migration-bearing status

## Edge Cases And Failure Modes
- A migration may be syntactically valid for Flyway but unsafe for rolling upgrades because old and new application versions cannot both tolerate the intermediate schema state.
- The deployment target may answer health checks from an older image even while a new release is being promoted; deployment checks must bind to expected build metadata rather than trusting only endpoint liveness.
- Migration-bearing releases without JDBC access to the deployed database cannot prove Flyway or session-table expectations deeply; the plan should distinguish HTTP-only checks from deeper database-backed checks.
- Backup guidance without restore validation gives false confidence; the plan must fail closed or stay explicit when no restore evidence is available for a migration-bearing release.
- Disaster recovery for a migration-bearing release cannot be reduced to `kubectl rollout undo` or image retagging when the schema has already moved forward.
- Workflow changes must not assume a single hosting platform beyond GitHub Actions and a reachable deployment URL; cluster-specific deployment orchestration belongs outside this repository.
- Any migration metadata convention must avoid rewriting historical release artifacts unnecessarily; only future migrations or actively changed files should need new classification data unless there is a strong reason to backfill examples.

## Validation Plan
- commands to run:
  - `docker run --rm -v "${PWD}:/repo" -w /repo rhysd/actionlint:latest`
  - `.\gradlew.bat build`
  - targeted deployment validation command(s) introduced by the implementation, likely one of:
    - `.\gradlew.bat externalDeploymentCheck --no-daemon -PexternalCheck.baseUrl=...`
    - `.\gradlew.bat scheduledExternalCheck --no-daemon`
    - a new repo-owned release helper script under `scripts/release/`
- tests to add or update:
  - external smoke or deployment tests that assert expected build metadata and deeper prod-posture checks against the deployed target
  - focused tests for any new helper code or script parsing logic when that logic is complex enough to justify direct validation
- docs or contract checks:
  - confirm `README.md`, `SETUP.md`, and `ai/RELEASES.md` agree on migration-bearing release review, restore expectations, and disaster-recovery posture
  - confirm docs distinguish image-only rollback from restore-required recovery
  - confirm docs describe deployment checks against the exact published release artifact instead of only a local image
  - confirm no OpenAPI, REST Docs, or HTTP example artifacts changed unexpectedly unless an intentional contract change was required
- manual verification steps:
  - inspect the migration-review helper output for a simulated or real migration-bearing diff and verify it classifies rollout compatibility clearly
  - inspect the deployment-check path and verify it can prove the target environment is running the intended release build metadata
  - run the documented restore drill against a disposable PostgreSQL instance and confirm the expected readiness, Flyway, and authenticated-check steps are reproducible
  - after the first real release using the new flow, verify that maintainers can distinguish rollback-safe and restore-sensitive releases without reverse-engineering migration SQL from scratch

## Better Engineering Notes
- The repo already has most of the plumbing for deployed validation through `externalDeploymentCheck`; extend that path instead of adding a parallel deploy-test framework.
- A small, explicit migration classification convention is preferable to a sophisticated SQL analyzer. The goal is reviewable rollout intent, not fake certainty from brittle parsing.
- The roadmap item allows a reproducible restore drill in place of a full automated backup system. Stay within that boundary unless the user later asks for backup-platform integration.
- Do not widen this plan into generalized SRE automation, Helm rollout controllers, or cloud-provider-specific disaster-recovery tooling. Keep the repository focused on a strong sample release process and verifiable maintainer runbooks.

## Validation Results
- To be filled in during execution

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
