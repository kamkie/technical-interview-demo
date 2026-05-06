# Plan: Palantir And IntelliJ Formatting

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Integration |
| Status | Implemented |

## Summary
- Supersede the partially implemented Eclipse-profile Spotless formatter plan with a Palantir-first formatter setup for `v2.0.0-RC5`.
- Use Gradle plugins `com.palantir.java-format` and `com.palantir.java-format-idea` for Java formatter enforcement and IntelliJ alignment, keep Gradle SpotBugs for static-analysis findings, and keep `.editorconfig` focused on fundamental text settings.
- Use IntelliJ's project code-style file for imports and settings not covered by Palantir; keep formatter exclusions narrow and risk-based rather than synchronized with Spotless targets.
- Configure IntelliJ project style so Markdown and AsciiDoc files do not automatically wrap prose lines, and audit `.editorconfig` for settings that could override or trigger wrapping for those file types.
- Keep AsciiDoc files formatter-managed; preserve nested lists with explicit AsciiDoc marker depth such as `*` for parents and `**` for children instead of formatter exclusions.
- Configure retained Spotless Kotlin and Gradle Kotlin DSL targets with KtLint import ordering and unused-import enforcement.
- Configure IntelliJ project style so formatting `.properties` files preserves intentional empty lines.
- Remove the Eclipse formatter profile completely from Gradle, tooling, docs, and AI guidance.
- Keep review history readable with two implementation commits: one configuration/guidance change, then one pure code reformatting commit.
- Roadmap tracking: `ROADMAP.md` tracks this under `Ordered Plan` / `Moving to 2.0` as selected `v2.0.0-RC5` release-candidate work.
- Success means the repository no longer contains or references the Eclipse formatter profile, Java formatting is enforced through Palantir in Gradle, IntelliJ project style covers imports and non-Java options where it is reliable, SpotBugs remains green, and the full wrapper build passes after the reformat commit.

## Scope
- In scope:
  - replace the current Spotless Java `eclipse().configFile(...)` formatter path in `build.gradle.kts` with Palantir Java Format Gradle wiring
  - apply `com.palantir.java-format` and `com.palantir.java-format-idea`
  - keep or adjust existing Gradle SpotBugs configuration as the static-analysis gate for miscellaneous bug/security findings
  - configure retained Spotless Kotlin and Gradle Kotlin DSL formatting for import ordering and unused-import cleanup
  - remove `tooling/formatting/intellij-exported-eclipse-java-formatter.xml`
  - remove user-facing and AI-facing references that describe the Eclipse formatter profile as the Java formatter authority
  - use `.idea/codeStyles/Project.xml` for IntelliJ imports, reliable non-Java options, and narrow formatter exclusions for files where IDE reformatting damages review clarity
  - keep IntelliJ Java import settings configured to avoid wildcard imports
  - keep `.editorconfig` as the fundamental cross-editor baseline for charset, line endings, indentation defaults, final newline, trailing whitespace, and max line length
  - ensure IntelliJ does not wrap Markdown or AsciiDoc prose lines, including checking whether `.editorconfig` `max_line_length` or related settings interfere with that goal
  - ensure IntelliJ formatting for `.properties` files does not remove intentional empty lines
  - for files not formatted by Palantir, keep settings close to IntelliJ defaults unless a repository contract already requires otherwise
  - update `SETUP.md`, `CONTRIBUTING.md`, and `ai/CODE_STYLE.md` where formatter ownership guidance changes
  - preserve the requested commit split: configuration/guidance first, code reformatting second
- Out of scope:
  - public REST API behavior, OpenAPI baseline, REST Docs snippets, HTTP examples, benchmark behavior, and release history
  - changing Java package structure or application behavior
  - dependency/toolchain upgrades beyond formatter plugins and the smallest required build changes
  - broad non-Java style redesign
  - tracking wider IntelliJ workspace metadata outside reviewed `.idea/codeStyles/` files
  - cutting or preparing the stable `v2.0.0` release

## Current State
- `ai/archive/PLAN_ci_owned_spotless_reformat.md` is the archived partially implemented plan. It moved the repository from IntelliJ-binary-dependent Java formatting to Spotless with an Eclipse formatter profile, then applied a large formatting pass and added IntelliJ project code-style files.
- `build.gradle.kts` currently applies `com.diffplug.spotless` and configures Java formatting through `eclipse().configFile("tooling/formatting/intellij-exported-eclipse-java-formatter.xml")`.
- `build.gradle.kts` already applies `com.github.spotbugs` and runs `spotbugsMain` as the active SpotBugs task with repository-owned include and exclude filters under `tooling/security/`.
- `.idea/codeStyles/Project.xml` currently contains formatter exclusions built around the superseded Spotless/Eclipse setup, including an exclusion for `tooling/formatting/intellij-exported-eclipse-java-formatter.xml`.
- `.editorconfig` is already slimmed to portable text defaults.
- `.editorconfig` currently applies `max_line_length = 120` globally; implementation must check whether IntelliJ EditorConfig support uses that value to wrap Markdown or AsciiDoc lines despite project code-style no-wrap settings.
- `.idea/codeStyles/Project.xml` currently disables Markdown wrapping, but AsciiDoc no-wrap behavior and `.properties` empty-line preservation still need explicit verification during the IntelliJ style update.
- `SETUP.md`, `CONTRIBUTING.md`, and `ai/CODE_STYLE.md` currently describe Spotless as the CI-owned formatter and the Eclipse formatter profile as the Java formatter input.
- The repository is on `main` and has unreleased local commits ahead of `origin/main`; preserve unrelated commit history and do not rewrite it while executing this plan.

## Requirement Gaps And Open Questions
- The initial request said `spotbugs` in formatter-scope contexts; the user later clarified `Spotless`. Execution treats SpotBugs as the existing static-analysis gate and uses Spotless only for formatter scopes.
- Exact Palantir plugin versions were not selected during planning. Execution selected and pinned `com.palantir.java-format` `2.90.0` and `com.palantir.java-format-idea` `2.90.0` in `build.gradle.kts`.
- Palantir Java Format is the Java authority. If `com.palantir.java-format` supports additional file types in the selected version, include only those documented supported types; otherwise do not invent a Gradle formatter for unsupported file types.
- IntelliJ and EditorConfig precedence for Markdown/AsciiDoc line wrapping must be verified during Milestone 1. If global `.editorconfig` `max_line_length = 120` causes wrapping for Markdown or AsciiDoc, add the narrowest repo-owned override for `*.md`, `*.adoc`, and `*.asciidoc` instead of weakening Java or build-file margins globally.
- The exact IntelliJ project-code-style setting for preserving `.properties` empty lines must be confirmed during Milestone 1. Do not solve this by excluding all `.properties` files unless IntelliJ lacks a precise project setting.

## Locked Decisions And Assumptions
- Execute for release target `v2.0.0-RC5`.
- The archived Eclipse-profile Spotless plan must not be resumed.
- Palantir Java Format is authoritative for Java source formatting.
- `com.palantir.java-format-idea` is the IntelliJ integration path for Palantir-aware IDE alignment.
- IntelliJ built-in project code style owns imports and project settings not covered by Palantir.
- `.editorconfig` owns only fundamental cross-editor text settings and lightweight formatting defaults.
- Markdown and AsciiDoc files must keep author-chosen line breaks; IntelliJ should not automatically wrap prose lines in those files.
- AsciiDoc files must not be excluded from formatting. Nested lists must use AsciiDoc marker depth (`*`, `**`, and deeper markers) because indentation-only nesting is not stable under IntelliJ's AsciiDoc formatter.
- `.properties` files must keep intentional blank-line separators after IntelliJ formatting.
- SpotBugs remains a Gradle static-analysis gate; it is not a formatter and must not be described as one.
- The Eclipse formatter is removed completely: no checked-in Eclipse formatter XML, no Gradle `eclipse().configFile(...)`, and no docs that direct maintainers to it.
- The IntelliJ project code-style file must not mirror Spotless inclusions by default; exclusions should be narrow exceptions for files where IntelliJ reformatting is harmful or misleading.
- For file types outside Palantir support, prefer IntelliJ defaults unless the repo already has an explicit style rule.
- Keep Java wildcard-import thresholds high and on-demand import package lists empty.
- Split implementation into exactly two review commits unless an implementation blocker forces a documented change:
  - commit 1: formatter/static-analysis configuration, IntelliJ project style, `.editorconfig`, and guidance only
  - commit 2: source and support-file reformatting only
- Do not mix refactors, behavior changes, dependency sweeps, or release edits into either commit.

## Execution Mode Fit
- Recommended mode: `Single Branch`.
- This touches shared formatter authority, build configuration, IDE metadata, docs, and then a broad formatting diff. Parallel implementation would increase merge conflicts and obscure review boundaries.
- Coordinator-owned files for the whole task:
  - `build.gradle.kts`
  - `.editorconfig`
  - `.gitignore`
  - `.idea/codeStyles/codeStyleConfig.xml`
  - `.idea/codeStyles/Project.xml`
  - `tooling/formatting/intellij-exported-eclipse-java-formatter.xml`
  - `SETUP.md`
  - `CONTRIBUTING.md`
  - `.devcontainer/` command references if formatter task names changed there
  - `ai/CODE_STYLE.md`
  - `ai/ENVIRONMENT_QUICK_REF.md` if formatter wrapper commands are added there
  - `ai/references/GRADLE_TASK_GRAPH.md` if formatter task graph references changed there
  - `CHANGELOG.md` unreleased entry required by Single Branch execution tracking
  - `ROADMAP.md`
  - this plan file
- If delegation becomes necessary, use only read-only verification workers after the configuration commit is prepared.

## Affected Artifacts
- Planning/roadmap:
  - `ROADMAP.md`
  - this `ai/PLAN_palantir_intellij_formatting.md`
  - archived `ai/archive/PLAN_ci_owned_spotless_reformat.md`
- Build/settings:
  - `build.gradle.kts`
  - `.editorconfig`
  - remove `tooling/formatting/intellij-exported-eclipse-java-formatter.xml`
  - `.idea/codeStyles/codeStyleConfig.xml`
  - `.idea/codeStyles/Project.xml`
  - `.gitignore` only if IntelliJ code-style tracking rules need adjustment
- Human docs:
  - `SETUP.md`
  - `CONTRIBUTING.md`
  - `README.md` only if the supported high-level contract changes, which is not expected
- AI guidance:
  - `ai/CODE_STYLE.md`
  - `ai/ENVIRONMENT_QUICK_REF.md` only if wrapper command guidance changes, which is not expected
- Reformat targets:
  - Java source under `src/**/*.java`
  - Java source under `buildSrc/src/**/*.java` if present or later added
  - any additional file types supported by the selected Palantir formatter plugin
  - non-Palantir file types only if the configuration commit keeps or adds a Gradle-enforced formatter for them
- IntelliJ-only style behavior to verify:
  - Markdown files under repo docs and AI guidance are not line-wrapped by IntelliJ formatting or EditorConfig interactions
  - AsciiDoc files under `src/docs/asciidoc/**/*.adoc` and any future `*.asciidoc` files are not line-wrapped by IntelliJ formatting or EditorConfig interactions
  - `.properties` files such as `gradle.properties`, `gradle/wrapper/gradle-wrapper.properties`, and `src/main/resources/*.properties` preserve empty lines
- IntelliJ formatter exclusions:
  - do not try to synchronize IntelliJ exclusions with Spotless targets
  - keep only reviewed risk-based project exclusions, currently `src/gatling/resources/gatling-benchmark-baseline.json`, `src/main/resources/db/migration/**/*.sql`, and `src/test/resources/openapi/approved-openapi.json`
  - keep Flyway migration SQL hand-formatted because IntelliJ SQL reformatting can obscure PostgreSQL DDL intent and make migration review noisier
- Tests:
  - no behavior tests should be added or changed for formatter-only work

## Execution Milestones
### Milestone 1: Formatter Configuration Commit
- goal: replace the Eclipse-profile Spotless formatter authority with Palantir, align IntelliJ project style, and update guidance
- owned files or packages:
  - `build.gradle.kts`
  - `.editorconfig`
  - `.gitignore` if needed
  - `.idea/codeStyles/codeStyleConfig.xml`
  - `.idea/codeStyles/Project.xml`
  - `tooling/formatting/intellij-exported-eclipse-java-formatter.xml`
  - `SETUP.md`
  - `CONTRIBUTING.md`
  - `ai/CODE_STYLE.md`
  - this plan file lifecycle/status if execution begins
- shared files that a `Shared Plan` worker must leave to the coordinator:
  - all owned files above
- behavior to preserve:
  - no application behavior or public API changes
  - existing wrapper entry points remain the same
  - `-SkipChecks` still skips formatting and static-analysis checks through existing wrapper behavior
  - SpotBugs stays part of the Gradle verification posture
- exact deliverables:
  - add `com.palantir.java-format` and `com.palantir.java-format-idea` to the Gradle plugin set with explicit pinned versions
  - remove Java `eclipse().configFile(...)` formatter configuration
  - remove the Eclipse formatter profile file
  - remove `com.diffplug.spotless` only if no remaining Gradle-enforced non-Java formatting uses it
  - wire Palantir formatter checks into the standard verification flow if the plugin does not do so automatically
  - keep or refine SpotBugs Gradle configuration without presenting it as a formatter
  - update `.idea/codeStyles/Project.xml` so IntelliJ project style owns import behavior and reliable non-Java options not covered by Palantir
  - update IntelliJ Markdown and AsciiDoc project style so formatting does not wrap prose lines
  - audit `.editorconfig` global `max_line_length = 120`; if it overrides or conflicts with Markdown/AsciiDoc no-wrap behavior, add a narrow docs-file override that preserves no-wrap behavior without changing Java formatter expectations
  - update IntelliJ `.properties` code style so formatting preserves intentional empty lines
  - remove stale IntelliJ exclusions for the removed Eclipse formatter file
  - keep `.editorconfig` focused on fundamental settings
  - update `SETUP.md`, `CONTRIBUTING.md`, and `ai/CODE_STYLE.md` so maintainers use Palantir/IntelliJ/EditorConfig terminology instead of the Eclipse-profile Spotless terminology
  - avoid source reformatting in this commit except unavoidable build file formatting
- validation checkpoint:
  - `./build.ps1 tasks --all --no-daemon`
  - `./build.ps1 checkFormat --no-daemon --continue`; execution added repo-owned `checkFormat` and `format` aliases because the Palantir plugin only exposes `formatDiff` directly and otherwise enforces Java formatting through its Spotless bridge
  - XML parsing or IDE loading check for `.idea/codeStyles/Project.xml`
  - IntelliJ style smoke check on representative Markdown, AsciiDoc, and `.properties` files or scratch copies: no Markdown/AsciiDoc line wrapping and no `.properties` empty-line removal
- commit checkpoint:
  - `build: switch java formatting to palantir`

### Milestone 2: Reformat Commit
- goal: apply the new formatter output without mixing in behavior or configuration changes
- owned files or packages:
  - Java source files touched by Palantir formatting
  - any non-Java files touched by a Gradle-enforced formatter retained or added in Milestone 1
- shared files that a `Shared Plan` worker must leave to the coordinator:
  - all formatted files; this milestone should not be split across workers
- behavior to preserve:
  - no hand refactors mixed into formatting
  - no generated or ignored directories committed
  - no OpenAPI baseline, REST Docs snippets, or HTTP examples should change semantically
  - no Markdown or AsciiDoc prose wrapping and no `.properties` empty-line removal caused by retained non-Java formatting
  - no formatter configuration, IntelliJ project style, roadmap, or docs changes in this commit unless Milestone 1 must be corrected first
- exact deliverables:
- run `./build.ps1 format --no-daemon`; execution added the repo-owned `format` alias for the retained Spotless apply task graph
  - run any retained non-Java formatter apply task documented by Milestone 1
  - review `git diff --stat` and spot-check representative formatted Java files for formatter-only churn
  - confirm no Java wildcard imports remain under formatter-managed Java source roots
  - keep unrelated edits out of the formatting commit
- validation checkpoint:
  - `git diff --check`
  - `./build.ps1 checkFormat --no-daemon`
  - `./build.ps1 build --no-daemon`
- commit checkpoint:
  - `style: apply palantir java formatting`

## Edge Cases And Failure Modes
- Palantir Java Format may produce a large Java diff from the current Eclipse-profile Spotless output. Keep that churn isolated in Milestone 2.
- IntelliJ import optimization can still introduce wildcard imports if project thresholds or package lists are wrong. Keep high thresholds and manually verify representative imports.
- `com.palantir.java-format-idea` may configure IDE behavior without committing every IDE-generated file. Commit only reviewed project-level code-style files.
- SpotBugs include/exclude filters are bug-detector policy, not formatter file scope. Do not force IntelliJ formatter exclusions to mirror SpotBugs detector filters.
- Removing Spotless entirely can remove Gradle-enforced non-Java whitespace normalization. If that is unacceptable, keep a narrow non-Java formatter in Gradle and document why it remains.
- The Palantir plugin exposes `formatDiff` directly and contributes Java formatting through Spotless; execution adds repo-owned `format` and `checkFormat` aliases so the planned wrapper commands remain stable.
- If the configuration commit causes compilation or static-analysis failures unrelated to formatting drift, fix them before the reformat commit.
- If IntelliJ reformat produces Java output different from Palantir after the project style update, Palantir wins for Java and the IntelliJ setup must be adjusted.
- If `.editorconfig` global `max_line_length` drives Markdown or AsciiDoc wrapping in IntelliJ, add a narrow file-type override for docs instead of changing the Java right margin contract.
- IntelliJ's AsciiDoc formatter standardizes list structure and may remove leading indentation. If a nested list relies on indentation with the same marker as its parent, it can flatten into one list level; use explicit AsciiDoc marker depth (`*`, `**`, and deeper markers) instead of excluding AsciiDoc files from formatting.
- IntelliJ properties-file formatting can collapse visual section spacing if blank-line preservation is not configured. Verify representative `.properties` files before committing the configuration milestone.
- Do not let `.gitignore` exceptions accidentally expose broader `.idea/` workspace metadata.

## Validation Plan
- Milestone 1:
  - `./build.ps1 tasks --all --no-daemon`
  - `./build.ps1 checkFormat --no-daemon --continue` after verifying the Palantir task name; expected source-format drift only
  - XML parsing or IntelliJ project load check for `.idea/codeStyles/Project.xml`
  - IntelliJ style smoke check confirming Markdown and AsciiDoc files are not wrapped and `.properties` empty lines are preserved
- Milestone 2:
  - `git diff --check`
  - `./build.ps1 checkFormat --no-daemon`
  - `./build.ps1 build --no-daemon`
- CI parity:
  - final `./build.ps1 build --no-daemon` must not depend on IntelliJ IDEA, Eclipse formatter files, `IDEA_HOME`, or `IDEA_FORMATTER_BINARY`
  - SpotBugs reports must remain produced by the Gradle build
- Manual review:
  - review the configuration commit separately from the reformat commit
  - confirm the Eclipse formatter profile is deleted and no references remain
  - confirm `.idea/codeStyles/Project.xml` keeps only narrow reviewed formatter exclusions instead of mirroring Spotless targets
  - confirm AsciiDoc files remain formatter-managed and nested lists use explicit marker depth
  - confirm `.editorconfig` does not override Markdown/AsciiDoc no-wrap intent
  - confirm `.properties` empty lines survive IntelliJ formatting
  - confirm Java imports do not use wildcard imports after both Gradle formatting and IntelliJ optimize-imports checks

## Better Engineering Notes
- Keep this transition separate from dependency sweeps, PMD tuning, SpotBugs rule-policy changes, and source refactors.
- Do not create a custom formatter or hand-written formatting script.
- Prefer narrow Markdown/AsciiDoc EditorConfig overrides over global margin changes if `.editorconfig` conflicts with IntelliJ no-wrap behavior.
- If the repository still needs CI-owned formatting for non-Java file types, prefer a narrow, deterministic Gradle formatter configuration over broad root-level globs.
- Record any deviation from the two-commit split in `Validation Results` before committing.

## Validation Results
- 2026-05-06 planning update:
  - archived `ai/PLAN_ci_owned_spotless_reformat.md` as partially implemented and superseded.
  - created this replacement plan for the Palantir/IntelliJ/EditorConfig/SpotBugs direction requested by the user.
  - updated `ROADMAP.md` to point active formatter work at this plan.
- 2026-05-06 plan refinement:
  - added IntelliJ code-style goals for no automatic Markdown/AsciiDoc line wrapping and preserving empty lines in `.properties` files.
  - checked current `.editorconfig` and recorded global `max_line_length = 120` as an implementation audit point for Markdown/AsciiDoc wrapping.
- 2026-05-06 Milestone 1 configuration:
  - selected and pinned `com.palantir.java-format` `2.90.0` and `com.palantir.java-format-idea` `2.90.0`.
  - retained Spotless for Kotlin, Gradle Kotlin DSL, support-file whitespace normalization, and as the Palantir Java Format task bridge; added repo-owned `format` and `checkFormat` aliases.
  - removed the Eclipse formatter XML and the Gradle `eclipse().configFile(...)` formatter path.
  - added the narrow `.editorconfig` `max_line_length = off` override for Markdown, AsciiDoc, and `.asciidoc` files so the global 120-column baseline does not drive prose wrapping.
  - kept `.idea/palantir-java-format.xml` ignored because the Palantir IDEA task writes machine-local Gradle cache paths into it.
  - `./build.ps1 tasks --all --no-daemon` passed and confirmed `format`, `checkFormat`, `formatDiff`, `updatePalantirJavaFormatXml`, and `updateWorkspaceXml` are available.
  - XML parsing for `.idea/codeStyles/Project.xml` passed.
  - static IntelliJ style smoke check passed for Markdown no-wrap, AsciiDoc one-sentence-per-line disabled, docs `max_line_length = off`, and `.properties` `KEEP_BLANK_LINES=true`.
  - `./build.ps1 checkFormat --no-daemon --continue` failed only at `spotlessJavaCheck` with expected Palantir Java formatting drift across 155 Java files; `spotlessKotlinCheck`, `spotlessKotlinGradleCheck`, and `spotlessMiscCheck` passed.
- 2026-05-06 Milestone 2 reformat:
  - `./build.ps1 format --no-daemon` passed and applied Palantir Java Format output.
  - `git diff --check` passed.
  - spot-checked representative Java output in `ApiExceptionHandler.java`; changes were formatter-only wrapping and indentation.
  - `rg -n "^import .*\\.\\*;|^import static .*\\.\\*;" src buildSrc/src -g "*.java"` found no wildcard imports.
  - `./build.ps1 checkFormat --no-daemon` passed.
  - `./build.ps1 build --no-daemon` passed: 264 tests, JaCoCo line coverage 93.3%, Asciidoctor generation, Docker image build, dependency and image Trivy scans, CycloneDX SBOM generation, Palantir/Spotless formatting, PMD, SpotBugs, and coverage verification all completed successfully.
- 2026-05-06 IntelliJ AsciiDoc follow-up:
  - observed IntelliJ reformat flattening the nested list under `src/docs/asciidoc/index.adoc` despite AsciiDoc no-wrap settings.
  - implementation finding: the IntelliJ AsciiDoc plugin attempts to standardize document structure and may remove leading blanks; AsciiDoc nesting is determined by marker depth, so `**` is a child of `*`, while indentation with the same marker can flatten.
  - removed IntelliJ `DO_NOT_FORMAT` exclusions for `src/docs/asciidoc/**/*.adoc` and `src/docs/asciidoc/**/*.asciidoc`; AsciiDoc files remain formatter-managed.
  - normalized REST Docs AsciiDoc lists to explicit marker depth across `src/docs/asciidoc/**/*.adoc`.
  - configured retained Spotless Kotlin and Gradle Kotlin DSL targets with KtLint import ordering and unused-import enforcement.
  - removed `.properties` files from the generic Spotless misc target after `checkFormat` collapsed intentional blank-line separators in `src/test/resources/application-test.properties`; `.properties` files remain governed by `.editorconfig`, `ij_properties_keep_blank_lines=true`, and IntelliJ's `KEEP_BLANK_LINES=true` setting.
  - recorded the AsciiDoc marker-depth rule in `SETUP.md` and `ai/CODE_STYLE.md`.
  - XML parsing for `.idea/codeStyles/Project.xml`, `git diff --check`, `./build.ps1 checkFormat --no-daemon`, and `./build.ps1 asciidoctor --no-daemon -x test` passed.
- 2026-05-06 IntelliJ Flyway SQL follow-up:
  - dropped the goal of synchronizing IntelliJ formatter exclusions with Spotless inclusions; IntelliJ exclusions are now narrow risk-based exceptions.
  - kept `src/main/resources/**/*.sql` in Spotless misc formatting for trailing-whitespace and final-newline cleanup.
  - restored Flyway migration SQL from IntelliJ SQL formatter churn and excluded only `src/main/resources/db/migration/**/*.sql` from IntelliJ reformatting because the SQL formatter split PostgreSQL DDL clauses such as `alter column` and `create extension` in review-hostile ways.
  - XML parsing for `.idea/codeStyles/Project.xml`, `git diff --check`, `git diff --exit-code -- src/main/resources/db/migration/*.sql`, and `./build.ps1 checkFormat --no-daemon` passed.

## User Validation
- Review the implementation as two commits:
  - configuration commit: Gradle plugins, IntelliJ project style, `.editorconfig`, docs, and AI guidance only
  - formatting commit: formatter output only
- After pulling the final branch in a clean shell, run:

```powershell
./build.ps1 checkFormat --no-daemon
./build.ps1 build --no-daemon
```

- In IntelliJ, confirm the Palantir formatter plugin is active for Java and that project code style does not introduce wildcard imports.
- In IntelliJ, format representative Markdown, AsciiDoc, and `.properties` files or scratch copies and confirm Markdown/AsciiDoc prose is not wrapped and `.properties` empty lines are preserved.
