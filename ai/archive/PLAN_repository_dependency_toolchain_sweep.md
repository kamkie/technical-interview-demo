# Plan: Repository-Wide Dependency And Toolchain Upgrade Sweep

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Closed |
| Status | Released |

## Summary
- Run one conservative repository-wide sweep across the dependency, build, container, CI, release, and packaging toolchain surfaces that are owned in this repository.
- Keep the sweep contract-neutral: no public API behavior, REST Docs, approved OpenAPI baseline, HTTP examples, or runtime feature semantics should change unless an upgrade reveals an unavoidable compatibility fix.
- Success is measured by fresh resolved-version evidence for every owned surface, reviewable milestone commits, green release-grade validation, no new blocking Trivy findings, and clear documentation updates only where human setup or release-tool instructions actually changed.

## Scope
- In scope:
  - root Gradle plugin versions, explicit build-tool versions, buildscript forced versions, and the Spring Boot dependency-management surface in `build.gradle.kts`
  - `buildSrc/build.gradle.kts` dependencies and custom Gradle task compatibility
  - Gradle wrapper version and wrapper distribution metadata under `gradle/wrapper/`
  - Java toolchain and CI Java setup alignment, while preserving Java 25 unless the user explicitly approves a toolchain baseline change
  - Dockerfile syntax version, pinned application base image digest, local PostgreSQL compose image, Trivy scanner image, Fluent Bit image, and deployment packaging metadata
  - pinned GitHub Actions, CodeQL, Codecov, Helm setup, Docker login, cosign installer, cosign release, attestations, artifact upload, and related workflow references
  - `SETUP.md` updates when setup, Java, Gradle, Docker, Trivy, cosign, or CI/release tool instructions change
- Out of scope:
  - public API redesign, request or response shape changes, endpoint behavior changes, database schema feature migrations, or new application functionality
  - changing dependency ownership policy, such as adding direct overrides for Spring Boot BOM-managed runtime dependencies, unless a concrete security or compatibility reason is recorded
  - cutting a release, editing `CHANGELOG.md` for unreleased history, or archiving this plan before release cleanup
  - broad Gradle configuration-cache migration or Gradle 10 migration work unless it is required to keep the upgraded build working
  - adding Trivy suppressions without a concrete vulnerability rationale, scope, review date, reference, and revalidation proof

## Current State
- `README.md` and `SETUP.md` present Java 25, Docker, Gradle, CI, security scans, SBOM generation, external smoke validation, and release signing as supported user-facing workflows.
- Planning-time Gradle resolution needed a Java 25 shell override because the interactive shell initially exposed Java 11. Execution used repository-local environment loading with `.\scripts\load-dotenv.ps1 -Quiet`; `.env` provided `JAVA_HOME=C:\Users\kamki\.jdks\azul-25.0.3`.
- Planning-time command:
  - `$env:JAVA_HOME='C:\Users\kamki\AppData\Local\Programs\IntelliJ IDEA Ultimate\jbr'; $env:Path="$env:JAVA_HOME\bin;$env:Path"; .\gradlew.bat dependencyUpdates --no-daemon --warning-mode all`
  - result: build successful on 2026-05-05
  - generated report: `build/dependencyUpdates/report.txt`
- The dependency update report found the root Gradle plugin and Spring Boot stack mostly current. Later milestone versions were reported for `com.github.ben-manes.caffeine:caffeine` `3.2.3 -> 3.2.4`, `org.flywaydb:flyway-database-postgresql` `11.14.1 -> 12.5.0`, and `org.postgresql:postgresql` `42.7.10 -> 42.7.11`.
- Those three reported candidates are declared without explicit versions in `build.gradle.kts`; their current versions are owned by Spring Boot dependency management, not by local version constants. Execution must prove ownership before deciding whether to defer to the Boot BOM or add a narrowly justified direct override.
- `dependencyUpdates --warning-mode all` also reported Gradle 10 deprecation warnings for `StartParameter.isConfigurationCacheRequested` during project configuration and `Task.project` during `dependencyUpdates` execution. The root cause still needs classification during execution; do not assume it is repo-owned before checking.
- Planning-time Trivy reports already exist:
  - `build/reports/security/dependencies/summary.txt`, generated 2026-05-05 21:05:14, reported zero dependency findings
  - `build/reports/security/image/summary.txt`, generated 2026-05-05 21:05:28, reported 40 image findings, all medium or low, with no high or critical blocking findings
  - `tooling/security/trivy.ignore` currently contains only policy comments and no active suppressions
- Directly owned Gradle and build version surfaces include:
  - root plugins in `build.gradle.kts`: Gatling `3.15.0.1`, git-properties `2.5.7`, SpotBugs Gradle plugin `6.5.4`, Spotless `8.4.0`, Error Prone Gradle plugin `5.1.0`, Spring Boot `4.0.6`, Spring dependency management `1.1.7`, Asciidoctor JVM `4.0.5`, Ben Manes versions `0.54.0`, Palantir git-version `5.0.0`, and test-logger `4.0.0`
  - root constants in `build.gradle.kts`: Error Prone `2.49.0`, FindSecBugs `1.14.0`, PMD `7.22.0`, SpotBugs `4.9.8`, Gradle wrapper `9.5.0`, springdoc `3.0.3`, Asciidoctor JRuby `9.4.12.1`, and Trivy image `aquasec/trivy:0.63.0`
  - buildscript forced artifacts: `org.apache.commons:commons-lang3:3.19.0` and `org.codehaus.plexus:plexus-utils:4.0.3`
  - `buildSrc/build.gradle.kts`: `com.fasterxml.jackson.module:jackson-module-kotlin:2.21.3`
  - Gradle wrapper metadata: `gradle/wrapper/gradle-wrapper.properties` points at `gradle-9.5.0-all.zip`
- Directly owned container and packaging surfaces include:
  - `Dockerfile` syntax `docker/dockerfile:1.7` and base image `eclipse-temurin:25-jre-jammy` pinned by digest
  - `docker-compose.yml` local PostgreSQL image `postgres:16-alpine`
  - `infra/k8s/log-forwarding/fluent-bit/daemonset.yaml` image `cr.fluentbit.io/fluent/fluent-bit:3.1.8`
  - Helm chart metadata in `infra/helm/technical-interview-demo/Chart.yaml`, plus image repository and tag defaults in `values.yaml` and `values-local.yaml`
- Directly owned GitHub workflow surfaces include pinned `actions/checkout`, `actions/setup-java`, `gradle/actions/setup-gradle`, `azure/setup-helm`, `codecov/codecov-action`, `actions/upload-artifact`, `github/codeql-action/init`, `github/codeql-action/analyze`, `docker/login-action`, `sigstore/cosign-installer`, `actions/attest-build-provenance`, and release workflow `cosign-release: v3.0.5`.
- `.github/dependabot.yml` already schedules grouped weekly updates for Gradle, GitHub Actions, and Docker on `main`.

## Requirement Gaps And Open Questions
- No blocking user-input gap remains.
- No current GitHub Dependabot alert, security advisory, or failing CI run was supplied with the request. Fallback: execute the sweep from fresh local resolver, scan, and registry evidence, and record any newly discovered advisory-specific work in the plan validation results.
- Exact latest external versions for Docker images and GitHub Actions were not frozen during planning because they should be resolved immediately before implementation. Fallback: use fresh registry and tag evidence in Milestone 1, then update only stable, non-prerelease targets unless a security alert requires otherwise.
- The current interactive shell has Java 11 as `JAVA_HOME`. Execution is not blocked because a local JBR 25 is available, but the executor must run Gradle with Java 25 or update the local environment according to `SETUP.md`.

## Locked Decisions And Assumptions
- Treat this as one coherent plan, not multiple `ai/PLAN_*.md` files, because the upgraded surfaces share the same build, scan, container, workflow, and release-readiness validation story.
- Preserve the repository's Java 25 baseline. Do not raise or lower the Java toolchain, CI `java-version`, or Docker Java major as part of this sweep without explicit user approval.
- Prefer stable releases only. Keep the existing `dependencyUpdates` non-stable rejection policy unless the user or a concrete advisory requires a prerelease.
- Respect Spring Boot dependency management. For BOM-managed artifacts such as Caffeine, Flyway, and PostgreSQL JDBC, prefer deferring to the current Boot BOM unless there is a documented security, compatibility, or release-readiness reason to add a direct override.
- Keep Gradle wrapper updates synchronized across `build.gradle.kts`, `gradle/wrapper/gradle-wrapper.properties`, and the checked-in wrapper jar when a wrapper change is needed.
- Keep container images pinned or otherwise reproducible where the repo already pins them. Updating a tag is not enough for the application base image; update and record digest proof.
- Keep public API contract artifacts unchanged unless an upgrade intentionally changes behavior and the required contract updates are explicitly added to this plan first.

## Execution Mode Fit
- Recommended default mode: `Single Branch`
- Why that mode fits best:
  - the sweep is broad but the files are tightly coupled by Gradle, Docker, CI, and release validation
  - one branch keeps version decisions and rollback reasoning visible in a single review
  - the initial evidence suggests only a small number of runtime dependency candidates, with most of the stack already current
- Coordinator-owned or otherwise shared files if the work fans out:
  - `ai/archive/PLAN_repository_dependency_toolchain_sweep.md`
  - `build.gradle.kts`
  - `gradle/wrapper/gradle-wrapper.properties`
  - `gradle/wrapper/gradle-wrapper.jar`
  - `.github/workflows/*.yml`
  - `SETUP.md`
  - `CHANGELOG.md`, which should remain untouched unless a release is being cut
- Candidate worker boundaries if later delegation becomes necessary:
  - Gradle-managed dependencies and wrapper: `build.gradle.kts`, `buildSrc/build.gradle.kts`, `gradle/wrapper/`
  - container and deployment tooling: `Dockerfile`, `docker-compose.yml`, `infra/`
  - GitHub Actions and release tooling: `.github/`, `scripts/release/`, and related `SETUP.md` release-tool references
- Split into separate plans only if execution uncovers a major framework migration, Java baseline change, database major-version compatibility issue, or Gradle 10 readiness project that cannot be kept as a version sweep.

## Affected Artifacts
- Tests:
  - existing unit, integration, REST Docs, OpenAPI compatibility, external smoke, and benchmark tests should remain the behavioral specs
  - add or update tests only when an upgrade requires a compatibility shim or changes runtime behavior
- Docs:
  - `SETUP.md` for Java, Gradle, Docker, Trivy, cosign, CI, release, or local runbook changes
  - `README.md` only if the high-level supported setup or project contract changes
  - `ai/ARCHITECTURE.md`, `ai/TESTING.md`, `ai/RELEASES.md`, or `ai/LEARNINGS.md` only if the sweep changes durable repository guidance rather than one-time version pins
- OpenAPI:
  - no update expected
- HTTP examples:
  - no update expected
- REST Docs:
  - no update expected unless an upgrade changes documented headers, security behavior, errors, or response shapes
- Source and build files:
  - `build.gradle.kts`
  - `buildSrc/build.gradle.kts`
  - `gradle/wrapper/gradle-wrapper.properties`
  - `gradle/wrapper/gradle-wrapper.jar`
  - `Dockerfile`
  - `docker-compose.yml`
  - `.github/dependabot.yml`
  - `.github/workflows/*.yml`
  - selected `infra/` manifests and Helm files when their image or chart metadata moves
  - `tooling/security/trivy.ignore` only if a narrow, dated suppression is intentionally added
- Build or benchmark checks:
  - fresh dependency update report, dependency ownership proof, Gradle warning classification, full build, Helm rendering, external smoke, vulnerability scans, SBOM output, and benchmark rerun when upgraded libraries affect book search/list, localization lookup, OAuth/session startup, cache behavior, PostgreSQL/Flyway behavior, or Spring Boot/Spring Framework behavior

## Execution Milestones
### Milestone 1: Resolve Fresh Upgrade Evidence
- goal:
  - create the exact target inventory immediately before editing version pins.
- owned files or packages:
  - `ai/archive/PLAN_repository_dependency_toolchain_sweep.md`
  - generated local reports under `build/` for evidence only; do not commit generated reports unless the repo already tracks the specific file
- shared files that a `Shared Plan` worker must leave to the coordinator:
  - all version-owning files
- behavior to preserve:
  - no repository behavior changes in this milestone
- exact deliverables:
  - run Gradle with Java 25 and regenerate `dependencyUpdates` with `--warning-mode all`
  - classify each candidate as locally owned, Spring Boot BOM-owned, transitive-only, plugin-owned, or external-tool-owned
  - record whether the Gradle 10 deprecation warnings are repo-owned, plugin-owned, or still unresolved
  - resolve current stable tags, immutable digests, or pinned SHAs for Docker images and GitHub Actions
  - identify any active advisory or scan finding that changes the default stable-version policy
- resolved-version proof expected during execution:
  - `.\gradlew.bat dependencyUpdates --no-daemon --warning-mode all`
  - `.\gradlew.bat buildEnvironment --no-daemon`
  - `.\gradlew.bat dependencyInsight --dependency <candidate> --configuration <relevantConfiguration> --no-daemon` for every runtime or test dependency candidate
  - registry digest or tag evidence for every Docker image that will change
  - `git ls-remote` or GitHub API evidence mapping every changed action tag to its pinned commit SHA
- validation checkpoint:
  - evidence is complete enough to decide every target or explicitly defer it
- commit checkpoint:
  - commit only the plan update if target decisions or deferrals materially change the execution instructions

### Milestone 2: Upgrade Gradle-Managed Dependencies And Wrapper
- goal:
  - update the Gradle-owned dependency and build-tool surface without changing public behavior.
- owned files or packages:
  - `build.gradle.kts`
  - `buildSrc/build.gradle.kts`
  - `gradle/wrapper/gradle-wrapper.properties`
  - `gradle/wrapper/gradle-wrapper.jar`
- shared files:
  - `SETUP.md` remains coordinator-owned until Milestone 5 unless a toolchain change cannot be validated without doc alignment
- behavior to preserve:
  - public API contract, security semantics, documentation generation, database migrations, cache semantics, benchmark task behavior, and release packaging behavior
- exact deliverables:
  - update only locally owned plugin, tool, forced-artifact, `buildSrc`, or wrapper versions selected in Milestone 1
  - update BOM-managed runtime dependencies only through Spring Boot if available, or record a narrow justification for any direct override
  - keep Java 25 and Gatling Java compile release behavior aligned with current build constraints
  - rerun dependency ownership proof after edits
- validation checkpoint:
  - `.\gradlew.bat dependencyUpdates --no-daemon --warning-mode all`
  - `.\gradlew.bat classes testClasses --no-daemon`
  - targeted `dependencyInsight` commands for changed runtime, test, plugin, or buildSrc artifacts
- commit checkpoint:
  - commit the Gradle dependency and wrapper batch only after the resolver output is clean or all remaining candidates are intentionally deferred in the plan

### Milestone 3: Upgrade Container, Scanner, And Deployment Tool Images
- goal:
  - refresh checked-in container and scanner versions while preserving local and production-like runtime behavior.
- owned files or packages:
  - `Dockerfile`
  - `docker-compose.yml`
  - `build.gradle.kts` for `trivyContainerImage`
  - `infra/k8s/log-forwarding/fluent-bit/daemonset.yaml`
  - selected Helm or Kustomize files only when image metadata changes
  - `tooling/security/trivy.ignore` only for intentional, dated suppressions
- shared files:
  - `SETUP.md` remains coordinator-owned until Milestone 5
- behavior to preserve:
  - application starts with Java 25, production profile defaults remain intact, local PostgreSQL development still works, Trivy gates still fail on high and critical findings, and log forwarding remains an optional example
- exact deliverables:
  - update Dockerfile syntax and base image digest if a newer stable compatible base exists
  - update local PostgreSQL and Fluent Bit images only when compatibility risk is acceptable and validation can prove it
  - update Trivy image if a stable scanner release is available
  - document any deferral caused by base-image CVEs, incompatible PostgreSQL major changes, or scanner behavior changes
- validation checkpoint:
  - `docker compose config`
  - `.\gradlew.bat dockerBuild --no-daemon`
  - `.\gradlew.bat vulnerabilityScan sbom --no-daemon`
  - `helm lint infra/helm/technical-interview-demo`
  - `helm template technical-interview-demo infra/helm/technical-interview-demo -f infra/helm/technical-interview-demo/values-local.yaml`
  - `kubectl kustomize infra/k8s/base` and any changed overlay or optional logging/monitoring kustomization when `kubectl` is available
- commit checkpoint:
  - commit the container and deployment image batch only after image scan findings are non-blocking or intentionally handled

### Milestone 4: Upgrade GitHub Actions And Release Tooling
- goal:
  - refresh workflow action pins and release helper tool pins while keeping CI, CodeQL, smoke, and release behavior equivalent.
- owned files or packages:
  - `.github/workflows/ci.yml`
  - `.github/workflows/codeql.yml`
  - `.github/workflows/post-deploy-smoke.yml`
  - `.github/workflows/release.yml`
  - `.github/dependabot.yml`
  - `scripts/release/` only if a release helper must change with the workflow
- shared files:
  - `SETUP.md` remains coordinator-owned until Milestone 5
- behavior to preserve:
  - lightweight-change CI short circuit, full Gradle build, dependency graph submission, CodeQL coverage, artifact uploads, external smoke validation, image publication, signing, verification, provenance attestation, and cumulative release-note rendering
- exact deliverables:
  - update pinned actions to current stable major or patch targets selected in Milestone 1
  - update pinned SHA values and adjacent version comments together
  - update `cosign-release` only with release workflow verification proof
  - keep Dependabot grouping behavior unchanged unless a required ecosystem target is missing
- validation checkpoint:
  - `pwsh ./scripts/classify-changed-files.ps1 -Uncommitted`
  - local workflow syntax review, plus `actionlint` if available
  - `git ls-remote` or GitHub API proof for each changed action pin
  - `.\gradlew.bat classes testClasses --no-daemon` if release scripts or Gradle-invoked workflow assumptions changed
- commit checkpoint:
  - commit the workflow batch only after every changed action pin has matching tag and SHA evidence

### Milestone 5: Documentation Alignment And Full Release-Readiness Validation
- goal:
  - align human-facing setup or release docs and prove the upgraded repository is release-ready.
- owned files or packages:
  - `SETUP.md`
  - `README.md` only if the high-level setup or support contract changed
  - owning AI guides only if durable repo guidance changed
  - `ai/archive/PLAN_repository_dependency_toolchain_sweep.md`
- shared files:
  - `CHANGELOG.md` remains out of scope unless this milestone becomes an intentional release handoff
- behavior to preserve:
  - setup guidance matches the actual Java, Gradle, Docker, Trivy, cosign, and CI/release toolchain
- exact deliverables:
  - update `SETUP.md` for any changed local or CI tool versions, commands, or release-verification instructions
  - update this plan's validation results with exact commands and outcomes
  - leave OpenAPI, REST Docs, HTTP examples, and README API contract text unchanged unless a real behavior change was approved
- validation checkpoint:
  - `.\gradlew.bat dependencyUpdates --no-daemon --warning-mode all`
  - `.\gradlew.bat build --no-daemon`
  - `.\gradlew.bat externalSmokeTest -PexternalSmokeImageName=technical-interview-demo -PdockerImageName=technical-interview-demo --no-daemon`
  - `.\gradlew.bat gatlingBenchmark --no-daemon` if Spring Boot/Spring Framework, cache, PostgreSQL/Flyway, OAuth/session, book search/list, or localization lookup behavior may be affected
  - `helm lint infra/helm/technical-interview-demo`
  - `helm template technical-interview-demo infra/helm/technical-interview-demo -f infra/helm/technical-interview-demo/values-local.yaml`
  - manual doc consistency review for `README.md`, `SETUP.md`, and any AI guide touched
- commit checkpoint:
  - commit the final docs and validation-results batch after full validation passes or after any skipped validation is explicitly justified

## Edge Cases And Failure Modes
- Spring Boot BOM-managed candidates may look outdated in `dependencyUpdates` while still being intentionally controlled by the Boot release train. Adding direct overrides can create unsupported combinations, so overrides require explicit compatibility proof.
- Flyway major upgrades can change migration validation, PostgreSQL support, licensing assumptions, or database metadata behavior. Treat a Flyway 12 move as higher risk than a patch-level JDBC driver update.
- PostgreSQL image major upgrades can affect local restore drills, migration behavior, and external smoke assumptions. Keep major database image changes out of the sweep unless validation proves compatibility.
- Docker base-image digest updates can remove expected shell utilities, change CA certificates, or alter healthcheck behavior. Prove the image still builds, scans, starts, and passes readiness.
- Trivy scanner upgrades can change vulnerability databases, severity mapping, output schema, and suppression behavior. Do not treat changed finding counts as noise; review them before suppressing or deferring.
- GitHub Actions major-version moves can change defaults, token permissions, artifact behavior, dependency-graph behavior, CodeQL build behavior, or OIDC signing semantics. Pin and prove every changed SHA.
- Gradle deprecation warnings may be caused by third-party plugins already at latest stable. Record external ownership instead of inventing local refactors.
- If an upgrade changes generated REST Docs, approved OpenAPI, HTTP examples, or API tests, stop and classify whether the change is an accidental regression or an intentional contract change before refreshing artifacts.

## Validation Plan
- Discovery and target proof:
  - `.\gradlew.bat dependencyUpdates --no-daemon --warning-mode all`
  - `.\gradlew.bat buildEnvironment --no-daemon`
  - `.\gradlew.bat dependencyInsight --dependency <candidate> --configuration <configuration> --no-daemon`
  - registry digest checks for Docker images
  - `git ls-remote` or GitHub API checks for action tags and pinned SHAs
- Standard repository proof:
  - `.\gradlew.bat build --no-daemon`
  - `.\gradlew.bat externalSmokeTest -PexternalSmokeImageName=technical-interview-demo -PdockerImageName=technical-interview-demo --no-daemon`
  - `helm lint infra/helm/technical-interview-demo`
  - `helm template technical-interview-demo infra/helm/technical-interview-demo -f infra/helm/technical-interview-demo/values-local.yaml`
- Security and supply-chain proof:
  - `.\gradlew.bat vulnerabilityScan sbom --no-daemon`
  - inspect `build/reports/security/dependencies/summary.txt`
  - inspect `build/reports/security/image/summary.txt`
  - inspect generated SBOM paths under `build/reports/sbom/`
- Extra proof when affected:
  - `.\gradlew.bat gatlingBenchmark --no-daemon` when upgraded libraries can affect book search/list, localization lookup, OAuth/session startup, cache behavior, PostgreSQL/Flyway behavior, or Spring Boot/Spring Framework behavior
  - `kubectl kustomize <changed-kustomization>` when raw Kubernetes manifests change and `kubectl` is available
  - `actionlint` when available for workflow syntax
  - manual GitHub workflow review when local workflow execution is not possible

## Better Engineering Notes
- The current shell's Java 11 `JAVA_HOME` is inconsistent with the repo setup contract. This sweep should not silently fix the user's machine-wide environment, but execution should either run commands with a Java 25 shell override or update setup notes if the documented path is incomplete.
- The root dependency report does not cover every external surface, especially Docker image tags, GitHub Actions pins, release-tool inputs, and the standalone `buildSrc` dependency. Milestone 1 exists to avoid missing these non-Maven surfaces.
- A future Gradle 10 readiness plan may be worthwhile if deprecation warnings remain after all plugins are current, but that should stay separate unless it blocks this upgrade sweep.
- If this sweep discovers many unrelated major upgrades, prefer completing safe patch/minor upgrades first and split major migrations into their own plans with dedicated compatibility validation.

## Validation Results
- Planning-time discovery completed on 2026-05-05:
  - initial `.\gradlew.bat dependencyUpdates --no-daemon` failed because the shell used Java 11
  - rerun with local JBR 25 succeeded
  - `dependencyUpdates --warning-mode all` reported later milestone candidates for Caffeine, Flyway database PostgreSQL support, and PostgreSQL JDBC, plus Gradle 10 deprecation warnings that still need ownership classification
  - existing Trivy dependency scan summary reported zero findings
  - existing Trivy image scan summary reported 40 medium/low findings and no high/critical blocking findings
- Implementation completed on 2026-05-05:
  - execution loaded `.env` through `.\scripts\load-dotenv.ps1 -Quiet` and ran Gradle with Zulu Java 25.0.3 from `C:\Users\kamki\.jdks\azul-25.0.3`
  - `.\gradlew.bat dependencyUpdates --no-daemon --warning-mode all` passed; all directly owned Gradle plugins and build tools remained current
  - Spring Boot BOM-managed candidates were intentionally deferred rather than overridden: Caffeine `3.2.3 -> 3.2.4`, Flyway PostgreSQL support `11.14.1 -> 12.5.0`, and PostgreSQL JDBC `42.7.10 -> 42.7.11`
  - `.\gradlew.bat buildEnvironment --no-daemon`, targeted `dependencyInsight` checks, and `.\gradlew.bat dependencyManagement --no-daemon` passed and confirmed BOM or plugin ownership for those candidates
  - Gradle 10 deprecation warnings remain non-blocking and appear tied to current third-party plugin/task behavior rather than a locally owned upgrade blocker
  - updated container and scanner surfaces: Dockerfile frontend `docker/dockerfile:1.23.0`, application base `eclipse-temurin:25-jre-noble@sha256:b27ca47660a8fa837e47a8533b9b1a3a430295cf29ca28d91af4fd121572dc29`, Trivy `aquasec/trivy:0.70.0@sha256:be1190afcb28352bfddc4ddeb71470835d16462af68d310f9f4bca710961a41e`, Fluent Bit `3.2.10@sha256:d6dec000c4929a439562525728c708f6e99800d7ddc82efd6aa4f45f3a20b562`, and Testcontainers PostgreSQL `postgres:16-alpine`
  - updated workflow pins for `gradle/actions/setup-gradle@v6`, `azure/setup-helm@v5`, `github/codeql-action/*@v4`, and `actions/attest-build-provenance@v4`; a follow-up pin verification script reported no tag/SHA mismatches
  - `docker compose config`, `helm lint infra/helm/technical-interview-demo`, `helm template technical-interview-demo infra/helm/technical-interview-demo -f infra/helm/technical-interview-demo/values-local.yaml`, `kubectl kustomize infra/k8s/base`, and `kubectl kustomize infra/k8s/log-forwarding/fluent-bit` passed
  - `.\gradlew.bat classes testClasses --no-daemon`, `.\gradlew.bat dockerBuild --no-daemon`, and final `.\gradlew.bat build --no-daemon` passed; the final build executed 264 tests and produced 93.5% JaCoCo line coverage
  - `.\gradlew.bat vulnerabilityScan sbom --no-daemon` hit a transient Trivy Java DB download connection reset during `imageSbom`; rerunning `.\gradlew.bat imageSbom --no-daemon` succeeded, and the final `build` also completed security scans and SBOM generation
  - final security summaries reported zero dependency findings and 25 image findings, with zero high or critical findings; generated SBOM files exist under `build/reports/sbom/application/` and `build/reports/sbom/image/`
  - `.\gradlew.bat externalSmokeTest -PexternalSmokeImageName=technical-interview-demo -PdockerImageName=technical-interview-demo --no-daemon` passed against the rebuilt image with 13 external smoke tests executed, 11 passed, and 2 configuration-gated checks skipped
  - `pwsh .\scripts\classify-changed-files.ps1 -Uncommitted` reported `skipHeavyValidation=false`
  - `actionlint` was not installed locally; workflow syntax was reviewed manually and action tag/SHA verification was run locally
  - final formatting and whitespace checks passed with `.\gradlew.bat spotlessMiscCheck --no-daemon`, `git diff --check`, and `git diff --cached --check`
  - `.\gradlew.bat gatlingBenchmark --no-daemon` was skipped because no Spring Boot/Spring Framework, cache, OAuth/session, book search/list, localization lookup, Flyway, or application PostgreSQL JDBC runtime behavior was upgraded; external smoke covered the changed container base and PostgreSQL 16 test image alignment
- Release preparation completed on 2026-05-05:
  - `pwsh ./scripts/release/get-release-migration-impact.ps1 -PreviousReleaseTag v2.0.0-RC1 -CurrentRef HEAD` reported no migration SQL changes and release impact `none`
  - `CHANGELOG.md` was updated for `v2.0.0-RC2`
  - `ROADMAP.md` was updated so the stable `v2.0.0` target now follows acceptance of `v2.0.0-RC2`
  - this executed plan was archived at `ai/archive/PLAN_repository_dependency_toolchain_sweep.md`
  - `git diff --check` passed for the release-preparation edits
  - `pwsh ./scripts/classify-changed-files.ps1 -Uncommitted` reported only lightweight files and `skipHeavyValidation=true`
  - `pwsh ./scripts/release/render-release-notes.ps1 -ChangelogPath CHANGELOG.md -CurrentTag v2.0.0-RC2 -PreviousPublishedTag v2.0.0-RC1 ...` accepted the RC2-only changelog range
  - `pwsh ./scripts/release/render-release-notes.ps1 -ChangelogPath CHANGELOG.md -CurrentTag v2.0.0-RC2 -PreviousPublishedTag v1.6.0 ...` accepted the cumulative range used by the tag-driven release workflow's latest non-prerelease GitHub Release lookup

## User Validation
- Review the final PR or branch diff for version-only intent: dependency and toolchain pins should move, while public API contract files should remain unchanged unless explicitly approved.
- Confirm the final validation results include the fresh dependency report, full build, image scans, SBOM generation, Helm rendering, external smoke, and any required benchmark run.
- For release-tool changes, confirm workflow pins have matching tag and SHA evidence and `SETUP.md` still describes the actual release verification commands.
