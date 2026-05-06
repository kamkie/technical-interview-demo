# Plan: CI-Owned Spotless Formatting

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Planning |
| Status | Ready |

## Summary
- Replace the current IntelliJ-binary-dependent Spotless Java formatter with a CI-owned formatter configuration that works when IntelliJ IDEA is not installed.
- Move the existing exported Eclipse formatter profile from `Default.xml` to a descriptive path under `tooling/`, and make that profile the Spotless Java formatter input.
- Slim `.editorconfig` so it owns portable editor basics only, not duplicated language-specific code-formatting rules.
- Add project-local IntelliJ code style files under `.idea/codeStyles/` as a final deliverable artifact so IDE formatting is forced to converge with the same style contract used by the Gradle Spotless formatter.
- Adjust the committed IntelliJ project style so optimize imports does not create wildcard imports.
- Keep review history readable with three commits: one CI formatter settings/documentation commit, one repository-wide formatting and import-organization commit, then one IDE code-style artifact commit with manual verification only.
- Roadmap tracking: `ROADMAP.md` tracks this under `Deferred` / `Post-2.0 Formatter Configuration Ownership`; execution remains deferred until stable `v2.0.0` is released unless the user explicitly selects it earlier.
- Success means the Gradle formatter/reformat milestones pass `spotlessCheck` and the full wrapper build without requiring `IDEA_HOME` or `IDEA_FORMATTER_BINARY`, and the final `.idea/codeStyles/` milestone passes manual IntelliJ verification.

## Scope
- In scope:
  - change root Spotless configuration in `build.gradle.kts`
  - move `Default.xml` to `tooling/formatting/intellij-exported-eclipse-java-formatter.xml`
  - reduce `.editorconfig` to non-duplicated, portable editor defaults such as charset, line endings, indentation, final-newline, and max line length
  - generate and track project-local IntelliJ code style files under `.idea/codeStyles/` as the final deliverable artifact
  - set IntelliJ Java import-on-demand thresholds high enough, and the import-on-demand package list empty enough, that IntelliJ does not create wildcard imports
  - adjust `.gitignore` in the final milestone so only `.idea/codeStyles/` is tracked while the rest of `.idea/` remains ignored
  - make Java formatting cover all repository Java source sets under `src/`
  - make Kotlin/Kotlin Gradle formatting cover root Gradle scripts, `gradle/`, and `buildSrc/`
  - make misc formatting cover tracked support/code files where Spotless can safely normalize whitespace and final newlines
  - preserve import organization by keeping Spotless `importOrder` and `removeUnusedImports` in the Java pipeline
  - run IntelliJ `Reformat Code`/`Optimize Imports` only as final manual IDE convergence verification; `spotlessApply` remains the authoritative CI-normalized formatting result
  - update human formatter guidance that currently says Java formatting is skipped without IntelliJ
  - update AI formatter guidance only where durable command, validation, or code-style ownership guidance changes
  - keep this plan and its `ROADMAP.md` entry aligned as lifecycle/status changes
- Out of scope:
  - public REST API behavior, OpenAPI baseline, REST Docs snippets, HTTP examples, benchmark behavior, and release history
  - changing Java package structure or application behavior
  - introducing a new formatter that requires an IDE or developer-local binary in CI
  - formatting ignored/generated directories such as `.gradle/`, `build/`, `.idea/` outside `.idea/codeStyles/`, `out/`, and `temp/`
  - IDE workspace metadata beyond project code-style files
  - cutting or preparing the stable `v2.0.0` release

## Current State
- `ROADMAP.md` now tracks this plan as deferred post-`2.0` work, not as selected current implementation work.
- `build.gradle.kts` currently discovers `ideaFormatterBinary` from `ideaFormatterBinary`, `IDEA_FORMATTER_BINARY`, or `IDEA_HOME`, and only configures `spotless { java { ... idea() ... } }` when the binary exists.
- If IntelliJ is absent, Java formatting is not configured, so CI can pass formatting checks without actually checking Java source formatting.
- Current Java Spotless targets only `src/main/java/**/*.java` and `src/test/java/**/*.java`; it misses `src/externalTest/java/**/*.java` and `src/gatling/java/**/*.java`.
- Current `kotlinGradle` targets root `*.gradle.kts` and `gradle/**/*.gradle.kts`; it misses `buildSrc/build.gradle.kts`.
- Current Spotless configuration does not format `buildSrc/src/main/kotlin/**/*.kt`.
- `.editorconfig` contains exported IntelliJ settings, including Java import layout metadata, which duplicates formatter and IDE code-style configuration.
- `.editorconfig` currently includes IntelliJ Java import-on-demand settings such as `ij_java_class_count_to_use_import_on_demand = 5`; moving style ownership to `.idea/codeStyles/` is the right time to raise those thresholds and prevent wildcard imports.
- Root `Default.xml` is a tracked Eclipse JDT formatter profile exported from IntelliJ tooling. Its root-level path and generic name do not explain its purpose.
- This workspace also has ignored local `.idea/codeStyles/` files, including `codeStyleConfig.xml` and `Default.xml`. Treat local `.idea` files as optional source material only; the deliverable is reviewed, project-local `.idea/codeStyles/` files with no broader IDE metadata.
- `.gitignore` currently ignores `.idea/` broadly and needs a narrow exception before `.idea/codeStyles/` can be versioned.
- `SETUP.md`, `.env.example`, and `CONTRIBUTING.md` still document `IDEA_HOME` or `IDEA_FORMATTER_BINARY` as relevant to Spotless Java formatting.
- `ai/CODE_STYLE.md` does not currently document formatter ownership; update it only if the implementation introduces durable AI-facing formatting guidance.
- The referenced sibling project uses a CI-friendly Spotless Java shape based on `eclipse().configFile(...)`; copy the pattern, not the whole build.

## Requirement Gaps And Open Questions
- No blocking product question remains.
- Roadmap sequencing is not a requirement gap: this plan is ready as a decision-complete execution plan, but it remains deferred until the stable `v2.0.0` release unless the user explicitly selects it earlier.
- Exact IntelliJ `Optimize Imports` behavior is not fully reproducible in CI unless CI installs and configures IntelliJ, which is the failure mode this work is removing. Fallback: keep Spotless `importOrder` and `removeUnusedImports` as the durable CI-enforced import state, then verify IntelliJ convergence manually in the final milestone without committing source changes there.
- If IntelliJ and Spotless disagree during the formatting commit, Spotless wins because CI must be able to reproduce the final state without an IDE.

## Locked Decisions And Assumptions
- Execute after stable `v2.0.0` is released, or earlier only with an explicit user override.
- Rename and move `Default.xml` to `tooling/formatting/intellij-exported-eclipse-java-formatter.xml`.
- Use `tooling/formatting/intellij-exported-eclipse-java-formatter.xml` through Spotless `eclipse().configFile(...)` for Java formatting.
- Keep `.editorconfig` as a portable editor baseline only; remove exported IntelliJ language-formatting keys that duplicate `.idea/codeStyles/` and the Eclipse formatter profile.
- Track `.idea/codeStyles/codeStyleConfig.xml` and `.idea/codeStyles/Project.xml` as the final project-local IntelliJ formatter artifact; do not track ignored local `.idea/codeStyles/Default.xml` unless it is intentionally transformed into the reviewed `Project.xml` deliverable.
- Configure `.idea/codeStyles/Project.xml` to avoid wildcard imports by setting Java class/static import-on-demand thresholds to a high value, for example `999`, and leaving the packages-to-use-import-on-demand list empty.
- Treat Gradle Spotless plus `tooling/formatting/intellij-exported-eclipse-java-formatter.xml` as the CI formatter contract; `.idea/codeStyles/` exists to force IDE convergence with that contract, not to define CI behavior.
- Keep all other `.idea/` workspace files ignored.
- Keep Spotless `importOrder` and `removeUnusedImports` enabled for Java.
- Expand Java target to `src/**/*.java`.
- Add Spotless Kotlin formatting for `buildSrc/src/**/*.kt`.
- Expand Kotlin Gradle target to include `buildSrc/**/*.gradle.kts`.
- Do not require `IDEA_HOME`, `IDEA_FORMATTER_BINARY`, or `ideaFormatterBinary` for formatting.
- Preserve the requested review sequence:
  - commit 1: formatter settings and related guidance only
  - commit 2: reformat and organize imports only
  - commit 3: `.idea/codeStyles/` IDE convergence artifact and manual verification only
- The first commit may not pass `spotlessCheck` in isolation after the formatter is tightened; Milestone 2's Gradle validation proves formatter enforcement, and Milestone 3's manual IntelliJ verification proves IDE convergence.

## Execution Mode Fit
- Recommended mode: `Single Branch`.
- This is a cross-repository formatter transition with one shared build file, one large formatting commit, and one final IDE settings artifact. Parallel worker fanout would increase merge risk and make formatting churn harder to review.
- Coordinator-owned files for the whole task:
  - `build.gradle.kts`
  - `.editorconfig`
  - `.gitignore`
  - `.idea/codeStyles/codeStyleConfig.xml`
  - `.idea/codeStyles/Project.xml`
  - `Default.xml`
  - `tooling/formatting/intellij-exported-eclipse-java-formatter.xml`
  - `SETUP.md`
  - `.env.example`
  - `CONTRIBUTING.md`
  - `ai/CODE_STYLE.md`
  - `ROADMAP.md`
  - this plan file
- If delegation becomes necessary later, use only read-only review/validation workers after the three commits are prepared.

## Affected Artifacts
- Planning/roadmap:
  - `ROADMAP.md` tracks this plan under deferred post-`2.0` work and must stay aligned if execution status or sequencing changes
  - this `ai/PLAN_ci_owned_spotless_reformat.md`
- Build/settings:
  - `build.gradle.kts`
  - `.editorconfig`
  - remove root `Default.xml`
  - add `tooling/formatting/intellij-exported-eclipse-java-formatter.xml`
- IDE convergence artifact:
  - `.gitignore`
  - add `.idea/codeStyles/codeStyleConfig.xml`
  - add `.idea/codeStyles/Project.xml`
- Human docs:
  - `SETUP.md`
  - `.env.example`
  - `CONTRIBUTING.md`
- AI guidance:
  - `ai/CODE_STYLE.md` if formatter ownership guidance changes durably
- Reformat targets:
  - `src/**/*.java`
  - `buildSrc/src/**/*.kt`
  - root `*.gradle.kts`
  - `buildSrc/**/*.gradle.kts`
  - `gradle/**/*.gradle.kts`
  - tracked repo support files already covered by `misc`, plus safe additions such as properties, SQL, YAML, XML, JSON, HTTP examples, Dockerfile/docker-compose files, and Markdown if configured
- Proposed formatter exclusions:
  - generated, cache, and local output directories: `.git/**`, `.gradle/**`, `.kotlin/**`, `build/**`, `buildSrc/build/**`, `buildSrc/.gradle/**`, `out/**`, and `temp/**`
  - local IDE/workspace metadata: `.idea/**` except the tracked `.idea/codeStyles/**` files, plus `.run/**`
  - binary or non-text artifacts: `gradle/wrapper/gradle-wrapper.jar`, `**/*.jar`, `**/*.zip`, `**/*.png`, `**/*.jpg`, `**/*.jpeg`, `**/*.gif`, `**/*.ico`, `**/*.pdf`, `**/*.jks`, `**/*.p12`, and `**/*.keystore`
  - local/private environment files: `.env` and `src/test/resources/http/http-client.private.env.json`
  - generated or reviewed baseline artifacts where whitespace churn is not useful: `src/test/resources/openapi/approved-openapi.json`, `src/gatling/resources/gatling-benchmark-baseline.json`, generated REST Docs snippets, build reports, SBOMs, and security scan outputs
  - formatter and IDE style inputs themselves in the reformat commit: `tooling/formatting/intellij-exported-eclipse-java-formatter.xml` belongs to the settings commit, and `.idea/codeStyles/*.xml` belongs to the final IDE artifact commit
- Contract docs/OpenAPI/HTTP examples:
  - no behavior or API contract updates expected
  - HTTP examples may be whitespace-normalized only if included in misc formatting
- Tests:
  - no behavior tests should be added or changed for formatter-only work

## Execution Milestones
### Milestone 1: Formatter Settings Commit
- goal: make Spotless CI-owned and remove the Java formatter dependency on local IntelliJ
- owned files or packages:
  - `build.gradle.kts`
  - `.editorconfig`
  - `tooling/formatting/intellij-exported-eclipse-java-formatter.xml`
  - root `Default.xml` removal
  - `SETUP.md`
  - `.env.example`
  - `CONTRIBUTING.md`
  - `ai/CODE_STYLE.md` if durable AI formatting guidance changes
  - this plan file lifecycle/status if execution begins
- shared files that a `Shared Plan` worker must leave to the coordinator:
  - all owned files above
- behavior to preserve:
  - no application behavior or public API changes
  - wrapper commands and CI entry points stay the same
  - `-SkipChecks` still skips Spotless checks through the existing wrapper behavior
- exact deliverables:
  - remove `ideaFormatterBinaryCandidate`, `ideaFormatterBinary`, and IntelliJ skip warning from `build.gradle.kts`
  - move `Default.xml` to `tooling/formatting/intellij-exported-eclipse-java-formatter.xml`
  - configure Java Spotless unconditionally with `target("src/**/*.java")`, `importOrder`, `removeUnusedImports`, and `eclipse().configFile("tooling/formatting/intellij-exported-eclipse-java-formatter.xml")`
  - leave `.idea/codeStyles/` and `.gitignore` changes to Milestone 3
  - remove duplicated exported IntelliJ language-formatting keys from `.editorconfig`; keep only portable editor rules and file-type basics that are not duplicated by the formatter/profile files
  - encode the proposed formatter exclusions in Spotless targets or `targetExclude` patterns
  - add Kotlin formatting for `buildSrc/src/**/*.kt`
  - expand Kotlin Gradle formatting to include `buildSrc/**/*.gradle.kts`
  - expand misc targets only to safe tracked files and exclude ignored/generated directories
  - remove `IDEA_HOME`/`IDEA_FORMATTER_BINARY` as Spotless requirements from setup docs and `.env.example`
  - update contributor/AI wording to say Spotless is CI-owned and does not require IntelliJ
  - keep `ROADMAP.md` and this plan lifecycle/status current if implementation starts
- validation checkpoint:
  - run `./build.ps1 tasks --all --no-daemon` to catch Gradle configuration errors without requiring formatting to already be clean
  - optionally run `./build.ps1 spotlessCheck --no-daemon --continue` and confirm any failure is only expected formatting drift, not formatter configuration failure
- commit checkpoint:
  - `build: make spotless formatting ci-owned`

### Milestone 2: Repository-Wide Reformat Commit
- goal: apply the new formatter and organize imports across tracked code and safe support files
- owned files or packages:
  - all tracked files touched by Spotless formatting under the configured targets
- shared files that a `Shared Plan` worker must leave to the coordinator:
  - all formatted files; this milestone should not be split across workers
- behavior to preserve:
  - no hand refactors mixed into formatting
  - no generated or ignored directories committed
  - no OpenAPI baseline, REST Docs snippets, or HTTP examples should change semantically
  - no proposed exclusion path should change in this commit unless the path is part of the settings commit boundary
- exact deliverables:
  - run `./build.ps1 spotlessApply --no-daemon` so Spotless defines the final state
  - review `git diff --stat` and spot-check representative Java, Kotlin, Gradle, HTTP, SQL/YAML/XML/JSON/properties changes for formatting-only churn
  - do not add or change `.idea/codeStyles/`; those files are reserved for Milestone 3
  - keep unrelated edits out of the formatting commit
- validation checkpoint:
  - `git diff --check`
  - `./build.ps1 spotlessCheck --no-daemon`
  - `./build.ps1 build --no-daemon`
- commit checkpoint:
  - `style: apply repository formatting`

### Milestone 3: IDE Code Style Artifact And Manual Verification
- goal: add the project IntelliJ code-style artifact so IDE formatting matches the Gradle Spotless formatter contract
- owned files or packages:
  - `.gitignore`
  - `.idea/codeStyles/codeStyleConfig.xml`
  - `.idea/codeStyles/Project.xml`
  - this plan file lifecycle/status if execution begins
- shared files that a `Shared Plan` worker must leave to the coordinator:
  - all owned files above
- behavior to preserve:
  - no application behavior or public API changes
  - no Java, Kotlin, Gradle, resource, HTTP example, OpenAPI, REST Docs, or benchmark file changes in this milestone
  - no broader `.idea/` workspace metadata committed
  - Gradle Spotless remains the CI formatter authority
- exact deliverables:
  - update `.gitignore` to allow tracking only `.idea/codeStyles/` under `.idea/`
  - add `.idea/codeStyles/codeStyleConfig.xml` with per-project code style enabled
  - add `.idea/codeStyles/Project.xml` as the reviewed IntelliJ project style artifact aligned to `tooling/formatting/intellij-exported-eclipse-java-formatter.xml`
  - configure `.idea/codeStyles/Project.xml` so Java optimize imports does not create wildcard imports, using high import-on-demand thresholds and an empty package-on-demand list
  - manually confirm the IDE style settings match the Gradle Spotless formatter contract for indentation, wrapping, line length, formatter tags, and import behavior
- validation checkpoint:
  - manual verification only: in IntelliJ, confirm the project code style is active, representative Java files already formatted by Milestone 2 do not receive intentional style changes from `Reformat Code`/`Optimize Imports`, and no wildcard imports are introduced
  - no Gradle, Spotless, or wrapper command is required for this milestone unless the user explicitly requests full validation
- commit checkpoint:
  - `chore: add intellij code style settings`

## Edge Cases And Failure Modes
- The moved Eclipse formatter profile may not match every IntelliJ code-style option. Treat `tooling/formatting/intellij-exported-eclipse-java-formatter.xml` as the CI formatter contract for Java and `.idea/codeStyles/` as the IntelliJ convergence aid; do not promise byte-for-byte IntelliJ output for every construct.
- `.editorconfig` cleanup can affect editors that previously consumed IntelliJ-specific `ij_*` keys. This is intentional: IntelliJ should use `.idea/codeStyles/`, while `.editorconfig` stays a portable baseline.
- `.gitignore` exceptions for `.idea/codeStyles/` must not accidentally unignore broader workspace files such as `workspace.xml`, run configurations, or module files.
- Eclipse JDT formatting may move comments or wrap lines differently from the current IntelliJ formatter. Spot-check high-risk Java files with multiline text blocks, annotations, and fluent assertions.
- Spotless `removeUnusedImports` can expose compile failures if unused imports were masking ambiguous simple names or if formatting changes reveal existing issues. Full `build` is required after the reformat commit.
- IntelliJ optimize imports can introduce wildcard imports depending on IDE thresholds. Milestone 3 must set project style thresholds high enough and manually verify that IDE optimize imports does not introduce wildcard imports.
- Overbroad `**/*.xml` or `**/*.json` targets can rewrite IDE metadata and reviewed baseline files. Prefer explicit include globs plus the exclusion list above over broad recursive globs.
- Ignored local `.idea` files may contain user-specific state or IDE-generated defaults. Never bulk-add `.idea/`; add only the reviewed `.idea/codeStyles/` deliverables.
- If final manual IntelliJ verification produces source diffs, treat that as an IDE-settings mismatch. Fix the `.idea/codeStyles/` artifact or rerun the earlier formatter milestone; do not commit source formatting changes in Milestone 3.
- Formatting all tracked support files can create noisy but low-value diffs. Keep misc targets to file types with deterministic whitespace/final-newline normalization and avoid generated artifacts.
- Because commit 1 tightens Spotless before commit 2 applies it, commit 1 may be red for `spotlessCheck` if checked independently. Milestone 2 must be green before the final manual-only IDE artifact milestone is committed.

## Validation Plan
- Milestone 1:
  - `./build.ps1 tasks --all --no-daemon`
  - optional `./build.ps1 spotlessCheck --no-daemon --continue` with expected formatting violations only
- Milestone 2:
  - `git diff --check`
  - `./build.ps1 spotlessCheck --no-daemon`
  - `./build.ps1 build --no-daemon`
- Milestone 3:
  - manual verification only in IntelliJ
  - no Gradle, Spotless, or wrapper command required for this IDE artifact milestone unless the user explicitly requests full validation
- CI parity:
  - ensure the final Gradle validation from Milestone 2 does not depend on `IDEA_HOME`, `IDEA_FORMATTER_BINARY`, or `ideaFormatterBinary`
  - confirm `.github/workflows/ci.yml` can keep running `./build.ps1 -FullBuild build --no-daemon` without adding IntelliJ setup
- Manual review:
  - inspect representative diffs from `src/main/java`, `src/test/java`, `src/externalTest/java`, `src/gatling/java`, `buildSrc/src/main/kotlin`, and Gradle scripts
  - manually confirm version control includes only `.idea/codeStyles/` files from `.idea/`
  - confirm no Java file contains wildcard imports after Milestone 2 Spotless formatting and Milestone 3 manual IntelliJ verification
  - confirm the reformat commit does not touch proposed exclusion paths
  - confirm no API contract files changed except whitespace-only formatting if they are intentionally included

## Better Engineering Notes
- Keep the formatter transition separate from dependency upgrades, PMD/SpotBugs cleanup, and source refactors.
- Do not add a custom string-rewrite formatter unless the first Spotless apply reveals a small, repeatable formatter defect that cannot be handled by the moved Eclipse formatter profile.
- If exact IntelliJ import layout becomes a hard requirement later, that should be a separate decision because it would require either CI-installed IntelliJ or a different import-formatting tool with matching behavior.

## Validation Results
- 2026-05-06 planning revision: repo fact-check and roadmap sync completed.
- 2026-05-06 planning revision validation:
  - `git diff --check` passed.
  - `./build.ps1 build` passed through the lightweight-file shortcut, reporting that only `ai/PLAN_ci_owned_spotless_reformat.md` and `ROADMAP.md` changed and that the Gradle build was skipped.
- 2026-05-06 IDE artifact milestone revision:
  - revised the plan so `.idea/codeStyles/` is a final deliverable artifact milestone with manual IntelliJ verification only.
  - `git diff --check` passed.
  - `./build.ps1 build` passed through the lightweight-file shortcut, reporting that only `ai/PLAN_ci_owned_spotless_reformat.md` and `ROADMAP.md` changed and that the Gradle build was skipped.

## User Validation
- Review the three commits separately:
  - settings commit: only formatter configuration and guidance
  - formatting commit: whitespace/import organization only
  - IDE artifact commit: `.idea/codeStyles/` and `.gitignore` only, with manual IntelliJ verification confirming IDE reformatting matches the Gradle Spotless formatter contract
- For the Gradle formatter/reformat state after Milestone 2, pull the branch in a clean shell or CI-like environment without IntelliJ formatter variables and run:

```powershell
./build.ps1 spotlessCheck --no-daemon
./build.ps1 build --no-daemon
```

- For the final IDE artifact milestone, use IntelliJ manual verification only; no Gradle rerun is required unless full validation is explicitly requested.
