# Dependabot Alert Fix Plan

## Lifecycle

| Status | Current |
| --- | --- |
| Phase | Implementation |
| Status | In Progress |

## Summary

Clear the currently open GitHub Dependabot alerts before stable `v2.0.0` by upgrading or constraining the vulnerable Gradle dependency paths for PostgreSQL JDBC, Gatling's Netty HTTP client stack, and AsciidoctorJ's JRuby path.

This is a build and dependency-management change only. It must preserve the frozen prerelease public API contract and avoid unrelated dependency sweeps.

## Scope

In scope:

- close Dependabot alert #6 for `org.postgresql:postgresql` by resolving `42.7.11` or newer
- close Dependabot alert #5 for `io.netty:netty-codec-http` by resolving `4.2.13.Final` or newer on the Gatling runtime path
- close or definitively clear Dependabot alert #1 for `org.jruby:jruby` by making the AsciidoctorJ JRuby path resolve and report `9.4.12.1` or newer
- use the smallest Gradle dependency-management override or direct version pin needed to fix the vulnerable graph
- update `CHANGELOG.md` under `## [Unreleased]`
- remove the completed Dependabot alert batch from `ROADMAP.md` only after the dependency graph and GitHub alerts are confirmed fixed
- record validation evidence in this plan

Out of scope:

- changing Java behavior, public API behavior, REST Docs, OpenAPI, or HTTP examples
- broad Spring Boot, Gatling, Asciidoctor, Gradle, PMD, SpotBugs, Error Prone, or formatter upgrades
- changing security policy, authentication, authorization, session handling, logging, Docker publication, or release workflow
- adding dependency locking unless a later failure proves it is necessary for repeatability
- cutting a release

## Current State

- `ROADMAP.md` lists three open pre-`2.0` Dependabot alerts as stable-release blockers.
- GitHub Dependabot alerts currently report:
  - #6 open: `org.postgresql:postgresql`, vulnerable `>= 42.2.0, < 42.7.11`, first patched `42.7.11`, severity high
  - #5 open: `io.netty:netty-codec-http`, vulnerable `>= 4.2.0.Alpha1, <= 4.2.12.Final`, first patched `4.2.13.Final`, severity medium
  - #1 open: `org.jruby:jruby`, vulnerable `>= 9.3.4.0, < 9.4.12.1`, first patched `9.4.12.1`, severity medium
- Previously opened alerts #2 `commons-lang3`, #3 `pmd-core`, and #4 `plexus-utils` are fixed in GitHub.
- `build.gradle.kts` currently declares `runtimeOnly("org.postgresql:postgresql")` without a version and resolves runtime PostgreSQL JDBC to `42.7.10`.
- `io.netty:netty-codec-http` is not on `runtimeClasspath`, `testRuntimeClasspath`, or `externalTestRuntimeClasspath`.
- `io.netty:netty-codec-http` is on `gatlingRuntimeClasspath` through Gatling and currently resolves to `4.2.12.Final`.
- `asciidoctorj { setJrubyVersion(asciidoctorJrubyVersion) }` sets `asciidoctorJrubyVersion = "9.4.12.1"`, and Gradle currently resolves the internal AsciidoctorJ configuration as `org.jruby:jruby:9.3.8.0 -> org.jruby:jruby-complete:9.4.12.1`.
- The GitHub alert for `org.jruby:jruby` remains open despite the Gradle substitution, so implementation should make the managed `org.jruby:jruby` version explicit enough for GitHub's dependency graph to observe the patched version.

## Requirement Gaps And Open Questions

No blocking product requirement gaps remain.

Execution-time checks:

- confirm the dependency-management override changes the resolved Gradle graph before relying on it
- confirm whether GitHub closes alert #1 after a pushed CI dependency snapshot; if it remains open while local Gradle shows only patched resolved artifacts, record the GitHub dependency-graph mismatch as the remaining blocker
- decide only if needed whether a direct dependency version, dependency-management dependency, Netty BOM import, or narrow configuration constraint is the smallest reliable Gradle mechanism for a specific path

## Locked Decisions And Assumptions

- Prefer a small `build.gradle.kts` dependency-management override over broad platform or plugin upgrades.
- Use a Netty BOM or equivalent all-Netty alignment instead of pinning only `netty-codec-http`, because Gatling resolves several Netty modules together.
- Keep the existing Spring Boot `4.0.6`, Gatling Gradle plugin `3.15.0.1`, and Asciidoctor JVM plugin `4.0.5` unless a targeted version override cannot fix the alert.
- Keep PostgreSQL JDBC version management in Gradle rather than adding runtime configuration or code workarounds.
- Keep JRuby at `9.4.12.1` unless implementation proves GitHub requires a newer patched line.
- Treat GitHub Dependabot alert closure as a post-push or post-merge dependency-graph verification step; local validation can prove the Gradle graph is fixed, but it may not immediately close Security tab alerts.
- Do not update public contract artifacts unless validation reveals an unintended runtime contract drift.

## Execution Mode Fit

Execution mode: `Single Branch`.

Rationale:

- the likely implementation is one Gradle build file plus tracking artifacts
- the dependency paths are coupled by shared Gradle dependency-management behavior
- worker fanout would add coordination cost without meaningful parallelism

Shared files: none, because no worker fanout is planned.

## Affected Artifacts

Likely implementation and tracking artifacts:

- `build.gradle.kts`
- `CHANGELOG.md`
- `ROADMAP.md`
- `ai/PLAN_dependabot_alerts.md`

Likely diagnostics only:

- `build/reports/security/dependencies/` generated by validation
- `build/reports/sbom/application/` generated by validation
- Docker image cache from the full build

Not expected to change:

- Java source files
- integration tests
- REST Docs tests or `src/docs/asciidoc/`
- `src/test/resources/openapi/approved-openapi.json`
- HTTP examples under `src/test/resources/http/`
- `README.md`
- setup, AI workflow, release, or formatter guidance

## Execution Milestones

### Milestone 1: Fix The Gradle Dependency Graph

Goal: update dependency management so the local Gradle graph resolves patched versions for all three open alerts.

Owned files:

- `build.gradle.kts`
- `ai/PLAN_dependabot_alerts.md`

Behavior to preserve:

- no public API or runtime behavior changes except patched dependency versions
- Gatling simulations continue compiling and running with aligned Netty modules
- Asciidoctor generation continues using JRuby `9.4.12.1` or newer

Deliverables:

- add version constants for the patched alert lines if they improve readability, for example `postgresqlVersion`, `nettyVersion`, and the existing `asciidoctorJrubyVersion`
- add a targeted Gradle dependency-management override:
  - manage `org.postgresql:postgresql` at `42.7.11` or newer
  - import `io.netty:netty-bom:4.2.13.Final` or otherwise constrain Gatling Netty modules to `4.2.13.Final`
  - manage `org.jruby:jruby` at `9.4.12.1` or newer in addition to the existing AsciidoctorJ JRuby setting
- keep direct dependency declarations versionless unless dependency-management validation fails
- run dependency insight commands and update this plan with before/after evidence

Validation checkpoint:

- `./build.ps1 dependencyInsight --configuration runtimeClasspath --dependency org.postgresql:postgresql`
- `./build.ps1 dependencyInsight --configuration gatlingRuntimeClasspath --dependency io.netty:netty-codec-http`
- `./build.ps1 dependencyInsight --configuration '__$$asciidoctorj$$___r' --dependency org.jruby:jruby`
- `./build.ps1 dependencies --configuration '__$$asciidoctorj$$___r'`
- `./build.ps1 compileJava`

Commit checkpoint:

- one commit with the Gradle dependency graph fix and the plan validation evidence

### Milestone 2: Validate, Verify Alerts, And Clean Tracking

Goal: prove the patched dependency graph is safe for the repository and close or accurately track the GitHub Dependabot alerts.

Owned files:

- `CHANGELOG.md`
- `ROADMAP.md`
- `ai/PLAN_dependabot_alerts.md`

Behavior to preserve:

- no public API contract changes
- no release work or tag creation
- no unrelated roadmap edits

Deliverables:

- add an `## [Unreleased]` changelog entry for the dependency security fixes
- run the full repository validation required for a build dependency/security change
- run Gatling benchmark validation if Netty is changed through the Gatling runtime path and the local environment can support it
- query GitHub Dependabot alerts after the dependency graph has been updated by CI or an accepted push
- remove completed Dependabot alert items from `ROADMAP.md` only when the relevant GitHub alerts are fixed, or leave a precise remaining blocker if GitHub still reports an alert despite a patched local graph
- update this plan lifecycle and validation results

Validation checkpoint:

- `./build.ps1 build`
- `./build.ps1 gatlingBenchmark`, if feasible, because alert #5 is in the Gatling runtime graph
- `git diff --check`
- `gh api repos/kamkie/technical-interview-demo/dependabot/alerts --jq '.[] | select(.state == "open") | {number, dependency: .dependency.package.name, vulnerable: .security_vulnerability.vulnerable_version_range, patched: .security_vulnerability.first_patched_version.identifier}'`

Commit checkpoint:

- one commit for validation evidence, changelog, roadmap cleanup or blocker tracking, and final plan status

## Edge Cases And Failure Modes

- Spring Boot dependency management may continue to select PostgreSQL JDBC `42.7.10` unless the override is applied in the correct Gradle dependency-management scope.
- Gatling may resolve multiple Netty modules; pinning only `netty-codec-http` can leave an inconsistent Netty stack.
- A Gatling plugin upgrade could bring unrelated behavior changes, benchmark-output differences, or new transitive changes; prefer a targeted Netty alignment first.
- GitHub may keep alert #1 open if its dependency graph sees the requested transitive `org.jruby:jruby:9.3.8.0` even though Gradle substitutes `jruby-complete:9.4.12.1`.
- The internal AsciidoctorJ configuration name `__$$asciidoctorj$$___r` is generated by the plugin; use it for diagnostics, but avoid baking that internal name into production build logic unless no stable public hook works.
- Full build validation runs Docker image build, Trivy scans, and SBOM generation; local Docker availability can block signoff.
- If `gatlingBenchmark` cannot run locally because of environment constraints, record the blocker and rely on `compileGatlingJava`, dependency insight, and full build only if the user accepts that reduced proof.

## Validation Plan

Dependency graph proof:

- `./build.ps1 dependencyInsight --configuration runtimeClasspath --dependency org.postgresql:postgresql`
- `./build.ps1 dependencyInsight --configuration gatlingRuntimeClasspath --dependency io.netty:netty-codec-http`
- `./build.ps1 dependencyInsight --configuration '__$$asciidoctorj$$___r' --dependency org.jruby:jruby`
- `./build.ps1 dependencies --configuration '__$$asciidoctorj$$___r'`

Repository proof:

- `./build.ps1 compileJava`
- `./build.ps1 build`
- `./build.ps1 gatlingBenchmark` when feasible
- `git diff --check`

Remote alert proof:

- use `gh api repos/kamkie/technical-interview-demo/dependabot/alerts` after the pushed or merged dependency graph update
- verify #6, #5, and #1 are no longer open, or record the exact remaining alert state and dependency graph mismatch

No REST Docs, OpenAPI baseline refresh, HTTP example update, README update, external smoke test, or release workflow is required unless validation reveals an unintended behavior or contract change.

## Better Engineering Notes

- Keep patched dependency versions named near the other version constants so future dependency sweeps can retire them cleanly when upstream BOMs catch up.
- Add `because(...)` rationale when using Gradle constraints so reviewers can trace each override back to a Dependabot alert.
- Prefer BOM alignment for Netty over isolated module pins.
- If a direct dependency declaration with a version is required, keep it scoped to the dependency already present instead of adding unused artifacts.
- Avoid suppressing Dependabot alerts unless the GitHub dependency graph is demonstrably wrong and maintainers explicitly choose that route.

## Validation Results

Planning validation:

- 2026-05-06: `gh api repos/kamkie/technical-interview-demo/dependabot/alerts` confirmed open alerts #6 `org.postgresql:postgresql`, #5 `io.netty:netty-codec-http`, and #1 `org.jruby:jruby`; alerts #2, #3, and #4 are fixed.
- 2026-05-06: `./build.ps1 dependencyInsight --configuration runtimeClasspath --dependency org.postgresql:postgresql` passed and showed `org.postgresql:postgresql:42.7.10`.
- 2026-05-06: `./build.ps1 dependencyInsight --configuration runtimeClasspath --dependency io.netty:netty-codec-http` passed and found no application runtime dependency.
- 2026-05-06: `./build.ps1 dependencyInsight --configuration testRuntimeClasspath --dependency io.netty:netty-codec-http` passed and found no test runtime dependency.
- 2026-05-06: `./build.ps1 dependencyInsight --configuration gatlingRuntimeClasspath --dependency io.netty:netty-codec-http` passed and showed `io.netty:netty-codec-http:4.2.12.Final` through `io.gatling:gatling-http-client:3.15.0`.
- 2026-05-06: `./build.ps1 dependencyInsight --configuration externalTestRuntimeClasspath --dependency io.netty:netty-codec-http` passed and found no external test runtime dependency.
- 2026-05-06: `./build.ps1 dependencyInsight --configuration asciidoctorExt --dependency org.jruby` failed because `asciidoctorExt` is not a configuration in this build.
- 2026-05-06: `./build.ps1 resolvableConfigurations` passed and identified the generated internal AsciidoctorJ configuration `__$$asciidoctorj$$___r`.
- 2026-05-06: `./build.ps1 dependencyInsight --configuration '__$$asciidoctorj$$___r' --dependency org.jruby:jruby` passed and showed `org.jruby:jruby:9.3.8.0 -> org.jruby:jruby-complete:9.4.12.1`.
- 2026-05-06: `./build.ps1 dependencies --configuration '__$$asciidoctorj$$___r'` passed and confirmed AsciidoctorJ `2.5.7` requests `org.jruby:jruby:9.3.8.0`, while the build also resolves `org.jruby:jruby-complete:9.4.12.1`.

Implementation validation:

- 2026-05-06: Added targeted Gradle dependency management for `org.postgresql:postgresql:42.7.11`, `io.netty:netty-bom:4.2.13.Final`, and `org.jruby:jruby:9.4.12.1`.
- 2026-05-06: Initial JRuby dependency-management-only validation still showed AsciidoctorJ requesting `org.jruby:jruby:9.3.8.0`, so implementation added a narrow component metadata rewrite for `org.asciidoctor:asciidoctorj` to request `org.jruby:jruby:9.4.12.1`.
- 2026-05-06: `./build.ps1 dependencyInsight --configuration runtimeClasspath --dependency org.postgresql:postgresql` passed and showed `org.postgresql:postgresql:42.7.11`.
- 2026-05-06: `./build.ps1 dependencyInsight --configuration gatlingRuntimeClasspath --dependency io.netty:netty-codec-http` passed and showed `io.netty:netty-codec-http:4.2.13.Final`, with Gatling's `netty-codec-http2` and `netty-handler-proxy` also aligned to `4.2.13.Final`.
- 2026-05-06: `./build.ps1 dependencyInsight --configuration '__$$asciidoctorj$$___r' --dependency org.jruby:jruby` passed and showed `org.jruby:jruby:9.4.12.1 -> org.jruby:jruby-complete:9.4.12.1`.
- 2026-05-06: `./build.ps1 dependencies --configuration '__$$asciidoctorj$$___r'` passed and confirmed AsciidoctorJ `2.5.7` now reports `org.jruby:jruby:9.4.12.1 -> org.jruby:jruby-complete:9.4.12.1`.
- 2026-05-06: `./build.ps1 compileJava` passed.

## User Validation

After implementation, the user can verify:

- `gh api repos/kamkie/technical-interview-demo/dependabot/alerts` no longer reports open alerts #6, #5, or #1 after CI submits an updated dependency graph
- `./build.ps1 dependencyInsight --configuration runtimeClasspath --dependency org.postgresql:postgresql` resolves `42.7.11` or newer
- `./build.ps1 dependencyInsight --configuration gatlingRuntimeClasspath --dependency io.netty:netty-codec-http` resolves `4.2.13.Final` or newer
- `./build.ps1 dependencyInsight --configuration '__$$asciidoctorj$$___r' --dependency org.jruby:jruby` no longer exposes a vulnerable selected JRuby path
- `./build.ps1 build` passes
