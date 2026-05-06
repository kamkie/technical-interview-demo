# Palantir Spotless Formatter Migration Plan

## Lifecycle

| Status | Current |
| --- | --- |
| Phase | Integration |
| Status | Implemented |

## Summary

Migrate repository Java formatting to Palantir Java Format through Spotless, keep Spotless as the single Gradle formatting entry point, and add Palantir IntelliJ integration so IDE reformat behavior can match CI.

The work must be split into two commits:

1. a preparation commit for build configuration, documentation, cleanup of old formatter configuration, and temporary enforcement staging
2. a separate project reformat commit containing the Java source reformat and final enforcement flip

## Scope

In scope:

- use Palantir Java Format for Java source formatting
- keep Spotless for Java, Gradle Kotlin, and miscellaneous text/properties formatting
- add `com.palantir.java-format-idea` so IntelliJ can recommend/configure the Palantir formatter plugin
- add Palantir Spotless integration, either through `com.palantir.java-format-spotless` or an equivalent explicit Spotless `palantirJavaFormat()` configuration
- remove old Java formatter sources and references:
  - local IntelliJ binary detection from `build.gradle.kts`
  - `IDEA_HOME`, `IDEA_FORMATTER_BINARY`, and `ideaFormatterBinary` documentation
  - root `Default.xml`
  - IntelliJ/Eclipse Java formatter references in setup and contributor docs
  - Java formatter policy from `.editorconfig`, deleting the file entirely unless a minimal neutral editor file is still needed
- document that this project uses Palantir Java Format through Spotless
- recommend installing the Palantir Java Format IntelliJ plugin for local IDE formatting
- keep each commit reviewable and validation-friendly

Out of scope:

- changing Java behavior, public APIs, REST Docs, OpenAPI, HTTP examples, or README contract text
- changing PMD, SpotBugs, Error Prone, test-logger, Spring, or dependency-update policy
- adopting Eclipse formatter XML, Google Java Format, or local IntelliJ command-line formatting
- introducing a second non-Spotless formatting entry point for repository signoff

## Current State

- `build.gradle.kts` applies `com.diffplug.spotless` `8.4.0`.
- Java formatting is conditional on a local IntelliJ executable discovered through `ideaFormatterBinary`, `IDEA_FORMATTER_BINARY`, or `IDEA_HOME`.
- When the IntelliJ executable is absent, the Spotless Java block is not configured, so Java formatting is skipped.
- `Default.xml` is a root Eclipse JDT formatter profile.
- `.editorconfig` contains a large IntelliJ code-style export, including Java-specific `ij_java_*` formatter settings.
- `SETUP.md` documents `IDEA_HOME` and `IDEA_FORMATTER_BINARY` for Spotless Java formatting and has troubleshooting for skipped Java formatting.
- `CONTRIBUTING.md` says Java formatting delegates to IntelliJ IDEA when available.
- `scripts/classify-changed-files.ps1` treats `.editorconfig` and `Default.xml` as lightweight files.
- `ai/CODE_STYLE.md` currently owns general edit-shaping rules but does not name the concrete Java formatter.

## Requirement Gaps And Open Questions

No blocking product requirement gaps remain. The user selected Palantir Java Format plus Spotless and asked for Palantir IntelliJ integration.

Execution-time checks:

- confirm the latest acceptable Palantir formatter plugin version before editing; `2.90.0` was current during planning on 2026-05-06
- decide whether to use explicit `com.palantir.java-format-spotless` plus `com.palantir.java-format-idea`, or the aggregate `com.palantir.java-format` plugin only if it produces cleaner Gradle wiring without unwanted tasks
- decide whether a minimal `.editorconfig` should remain for non-formatter editor defaults; if retained, it must not be presented as a Java formatter configuration source

## Locked Decisions And Assumptions

- Spotless remains the repository formatting entry point.
- Java formatting uses Palantir Java Format.
- IntelliJ users should install the Palantir Java Format plugin; Gradle configuration should recommend/configure it where the Palantir IDEA plugin supports that.
- Remove traces of the old Java formatter stack instead of keeping fallback Java formatters.
- Do not commit generated `.idea/` files unless a later implementation pass proves they are required and maintainers intentionally unignore the specific files.
- Keep the preparation commit build-green by staging enforcement with a temporary ratchet or equivalent mechanism if enabling Palantir Java formatting before the reformat would otherwise fail existing Java files.
- The second commit removes any temporary staging mechanism and makes Palantir formatting apply to all Java sources.

## Execution Mode Fit

Execution mode: `Single Branch`.

Rationale:

- the work is mostly one Gradle file plus documentation and one repository-wide source reformat
- splitting across workers would increase merge conflict risk because the reformat touches many Java files
- the requested commit split is sequential: configuration first, reformat second

Shared files: none, because no worker fanout is planned.

## Affected Artifacts

Likely build and formatter artifacts:

- `build.gradle.kts`
- `gradle.properties`, only if Palantir native formatter or version properties are needed
- `.editorconfig`, likely delete or shrink to neutral editor defaults
- `Default.xml`, delete
- `scripts/classify-changed-files.ps1`, remove deleted formatter config entries from the lightweight allow-list
- Java source files under `src/**/*.java`, including main, test, external test, and Gatling Java sources

Likely documentation and AI guidance:

- `SETUP.md`
- `CONTRIBUTING.md`
- `ai/CODE_STYLE.md`
- `ai/ENVIRONMENT_QUICK_REF.md`, only if AI-facing formatter commands change
- `ROADMAP.md`, remove or update the formatter item after implementation is accepted
- this plan file, especially `Validation Results`

Not expected to change:

- public API tests and REST Docs tests
- `src/docs/asciidoc/`
- `src/test/resources/openapi/approved-openapi.json`
- `src/test/resources/http/`
- `README.md`, unless the human-facing project overview currently claims a formatter behavior that changes
- `CHANGELOG.md`, unless this work is being released

## Execution Milestones

### Milestone 1: Preparation Commit

Goal: add Palantir formatter tooling and documentation while keeping the commit build-green and avoiding the repository-wide Java reformat.

Owned files:

- `build.gradle.kts`
- `.editorconfig`
- `Default.xml`
- `scripts/classify-changed-files.ps1`
- `SETUP.md`
- `CONTRIBUTING.md`
- `ai/CODE_STYLE.md`
- `ai/ENVIRONMENT_QUICK_REF.md`, if command guidance changes

Behavior to preserve:

- `./build.ps1 build` remains the standard validation entry point
- Spotless still formats Gradle Kotlin and misc files
- Java code behavior and public contracts do not change

Deliverables:

- add Palantir Java Format Spotless integration
- add `com.palantir.java-format-idea`
- remove local IntelliJ command-line formatter detection and docs
- remove `Default.xml`
- delete or reduce `.editorconfig` so it is not a competing Java formatter source
- update setup/contributor guidance to say Java formatting is Palantir Java Format through Spotless
- recommend installing the Palantir Java Format IntelliJ plugin
- if needed, temporarily use `spotless.ratchetFrom(...)` or a similar staged-enforcement mechanism so unchanged Java files do not fail before the dedicated reformat commit

Validation checkpoint:

- `./build.ps1 build`
- `./build.ps1 -FullBuild spotlessCheck`
- confirm the prep commit does not contain broad Java reformat churn

Commit checkpoint:

- commit message should make clear this is formatter tooling preparation, not the project reformat

### Milestone 2: Project Reformat Commit

Goal: apply Palantir Java Format to the Java source tree and turn on final all-source enforcement.

Owned files:

- Java source files targeted by Spotless
- `build.gradle.kts`, only to remove temporary ratchet/staging from milestone 1
- `ai/PLAN_palantir_spotless_formatter_migration.md`

Behavior to preserve:

- no intentional Java logic changes
- no public contract changes
- no test fixture semantic changes beyond whitespace/layout

Deliverables:

- run Spotless apply for Java and any affected formatting targets
- remove temporary ratchet/staging so Palantir Java formatting applies to all targeted Java files
- keep the commit focused on mechanical formatter output plus the final enforcement flip
- record validation results in this plan

Validation checkpoint:

- `./build.ps1 build`
- `git diff --check`
- review the Java diff as mechanical formatting only

Commit checkpoint:

- commit message should identify the commit as the Palantir Java reformat

## Edge Cases And Failure Modes

- Enabling Palantir Java formatting in the prep commit without staging may make `spotlessCheck` fail before the reformat commit.
- Palantir formatting can produce large Java diffs; keep that churn isolated in the second commit.
- Removing `.editorconfig` can change IDE defaults for files not covered by Spotless; if that risk is unacceptable, keep only a minimal neutral `.editorconfig` and document that it is not the Java formatter source.
- `com.palantir.java-format-idea` may generate `.idea` files locally; do not commit ignored IDE state unless explicitly approved.
- The Palantir native formatter path may have different startup and throughput tradeoffs; prefer the default Java implementation unless execution-time validation shows a reason to opt in.
- Plugin version drift can change formatter output; pin versions and record them in the plan's validation results.
- A Gradle plugin interaction with existing Spotless `kotlinGradle` or `misc` targets could rename or replace tasks; inspect `./build.ps1 -FullBuild tasks --all` if task availability changes.

## Validation Plan

Preparation commit:

- `./build.ps1 build`
- `./build.ps1 -FullBuild spotlessCheck`
- `git diff --check`

Reformat commit:

- `./build.ps1 build`
- `git diff --check`
- manual review that Java diffs are mechanical formatting only

Optional diagnostics:

- `./build.ps1 -FullBuild spotlessDiagnose`
- `./build.ps1 -FullBuild tasks --all` if Palantir plugins change task wiring

No REST Docs, OpenAPI compatibility, HTTP examples, benchmarks, or external smoke tests are required unless implementation accidentally changes runtime behavior.

## Better Engineering Notes

- Prefer explicit plugin wiring that keeps Spotless in charge of repository formatting. If the aggregate `com.palantir.java-format` plugin hides too much task behavior, use `com.palantir.java-format-spotless` plus `com.palantir.java-format-idea` explicitly.
- Keep formatter plugin versions pinned in `build.gradle.kts`.
- Remove old formatter environment variables instead of leaving them as dead setup knobs.
- The final docs should name one Java formatter path: Palantir Java Format via Spotless.
- IntelliJ plugin installation should be a recommendation for local editor consistency, not a prerequisite for CI formatting.

## Validation Results

Planning validation:

- 2026-05-06: `./build.ps1 build` passed; the wrapper classified the change as lightweight planning/roadmap work and skipped the Gradle build.
- 2026-05-06: `git diff --check` passed.

Implementation validation:

- 2026-05-06: Confirmed Palantir Java Format Gradle plugin line `2.90.0` remained current before implementation.
- 2026-05-06: Preparation implementation used explicit `com.palantir.java-format-spotless` and `com.palantir.java-format-idea` plugin wiring, retained a minimal neutral `.editorconfig`, removed the old local IntelliJ formatter binary path, and staged Java enforcement with temporary `spotless.ratchetFrom("HEAD")`.
- 2026-05-06: `./build.ps1 -FullBuild spotlessApply` passed and only formatted preparation-side Gradle/misc files.
- 2026-05-06: `./build.ps1 -FullBuild spotlessCheck` passed for the preparation checkpoint.
- 2026-05-06: `./build.ps1 build` passed for the preparation checkpoint; it ran the full Gradle build, tests, Spotless, PMD, SpotBugs/static security scan, Trivy dependency and image vulnerability scans, SBOM generation, Asciidoctor, boot jar, and Docker image build.
- 2026-05-06: `git diff --check` passed for the preparation checkpoint.
- 2026-05-06: `./build.ps1 -FullBuild spotlessApply` passed for the reformat checkpoint after removing temporary `spotless.ratchetFrom("HEAD")`; Palantir Java Format rewrote 157 Java files and left `build.gradle.kts` as the only non-Java diff in the reformat commit before tracking-artifact updates.
- 2026-05-06: `./build.ps1 build` passed for the reformat checkpoint; it ran the full Gradle build, 264 tests, Spotless, PMD, SpotBugs/static security scan, Trivy dependency and image vulnerability scans, SBOM generation, Asciidoctor, boot jar, and Docker image build.
- 2026-05-06: `git diff --check` passed for the reformat checkpoint.
- 2026-05-06: Manual review sampled the Java diff and confirmed it is mechanical formatter output: import ordering, annotation layout, line wrapping, blank lines, and empty block compaction.
- 2026-05-06: `./build.ps1 -FullBuild spotlessCheck` and `git diff --check` passed after final plan and roadmap tracking updates.

## User Validation

After implementation, the user can verify:

- `./build.ps1 spotlessApply` formats Java through Palantir without requiring `IDEA_HOME` or `IDEA_FORMATTER_BINARY`
- IntelliJ recommends or uses the Palantir Java Format plugin after Gradle import when the plugin is installed
- the first implementation commit contains tooling/docs cleanup only
- the second implementation commit contains the mechanical Java reformat and final enforcement flip
