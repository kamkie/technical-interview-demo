# Plan: Palantir And IntelliJ Formatting

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Planning |
| Status | Ready |

## Summary
- Supersede the partially implemented Eclipse-profile Spotless formatter plan with a Palantir-first formatter setup for `v2.0.0-RC5`.
- Use Gradle plugins `com.palantir.java-format` and `com.palantir.java-format-idea` for Java formatter enforcement and IntelliJ alignment, keep Gradle SpotBugs for static-analysis findings, and keep `.editorconfig` focused on fundamental text settings.
- Use IntelliJ's project code-style file for imports, exclusions, and settings not covered by Palantir; keep non-Palantir file types as close to IntelliJ default code style as practical.
- Remove the Eclipse formatter profile completely from Gradle, tooling, docs, and AI guidance.
- Keep review history readable with two implementation commits: one configuration/guidance change, then one pure code reformatting commit.
- Roadmap tracking: `ROADMAP.md` tracks this under `Ordered Plan` / `Moving to 2.0` as selected `v2.0.0-RC5` release-candidate work.
- Success means the repository no longer contains or references the Eclipse formatter profile, Java formatting is enforced through Palantir in Gradle, IntelliJ project style aligns with the Gradle formatter scope, SpotBugs remains green, and the full wrapper build passes after the reformat commit.

## Scope
- In scope:
  - replace the current Spotless Java `eclipse().configFile(...)` formatter path in `build.gradle.kts` with Palantir Java Format Gradle wiring
  - apply `com.palantir.java-format` and `com.palantir.java-format-idea`
  - keep or adjust existing Gradle SpotBugs configuration as the static-analysis gate for miscellaneous bug/security findings
  - remove `tooling/formatting/intellij-exported-eclipse-java-formatter.xml`
  - remove user-facing and AI-facing references that describe the Eclipse formatter profile as the Java formatter authority
  - align `.idea/codeStyles/Project.xml` with the Gradle formatting scope for included and excluded files, using IntelliJ built-in settings for imports and options not covered by Palantir
  - keep IntelliJ Java import settings configured to avoid wildcard imports
  - keep `.editorconfig` as the fundamental cross-editor baseline for charset, line endings, indentation defaults, final newline, trailing whitespace, and max line length
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
- `SETUP.md`, `CONTRIBUTING.md`, and `ai/CODE_STYLE.md` currently describe Spotless as the CI-owned formatter and the Eclipse formatter profile as the Java formatter input.
- The repository is on `main` and has unreleased local commits ahead of `origin/main`; preserve unrelated commit history and do not rewrite it while executing this plan.

## Requirement Gaps And Open Questions
- The request says `spotbugs` in formatter-scope contexts. SpotBugs does not format files. Fallback for execution: treat literal SpotBugs as the existing Gradle static-analysis gate, and mirror IntelliJ code-style inclusions/exclusions with the Gradle formatter scope instead of SpotBugs filters. If the intended word was `Spotless`, revise this plan before implementation.
- Exact Palantir plugin versions are not selected in this plan. Fallback for execution: pin explicit stable plugin versions in `build.gradle.kts`, using the repository's normal Gradle plugin declaration style, and record the chosen versions in `Validation Results`.
- Palantir Java Format is the Java authority. If `com.palantir.java-format` supports additional file types in the selected version, include only those documented supported types; otherwise do not invent a Gradle formatter for unsupported file types.

## Locked Decisions And Assumptions
- Execute for release target `v2.0.0-RC5`.
- The archived Eclipse-profile Spotless plan must not be resumed.
- Palantir Java Format is authoritative for Java source formatting.
- `com.palantir.java-format-idea` is the IntelliJ integration path for Palantir-aware IDE alignment.
- IntelliJ built-in project code style owns imports and project settings not covered by Palantir.
- `.editorconfig` owns only fundamental cross-editor text settings and lightweight formatting defaults.
- SpotBugs remains a Gradle static-analysis gate; it is not a formatter and must not be described as one.
- The Eclipse formatter is removed completely: no checked-in Eclipse formatter XML, no Gradle `eclipse().configFile(...)`, and no docs that direct maintainers to it.
- The IntelliJ project code-style file must mirror the Gradle formatter's intended file inclusions and exclusions as closely as IntelliJ supports.
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
  - `ai/CODE_STYLE.md`
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
- Formatter/style exclusions to keep aligned between Gradle formatter scope and IntelliJ code style:
  - generated, cache, and local output directories: `.git/**`, `.gradle/**`, `.kotlin/**`, `build/**`, `buildSrc/build/**`, `buildSrc/.gradle/**`, `out/**`, and `temp/**`
  - local IDE/workspace metadata: `.idea/**` except reviewed `.idea/codeStyles/**` files, plus `.run/**`
  - binary or non-text artifacts: `gradle/wrapper/gradle-wrapper.jar`, `**/*.jar`, `**/*.zip`, `**/*.png`, `**/*.jpg`, `**/*.jpeg`, `**/*.gif`, `**/*.ico`, `**/*.pdf`, `**/*.jks`, `**/*.p12`, and `**/*.keystore`
  - local/private environment files: `.env` and `src/test/resources/http/http-client.private.env.json`
  - generated or reviewed baselines where whitespace churn is not useful: `src/test/resources/openapi/approved-openapi.json`, `src/gatling/resources/gatling-benchmark-baseline.json`, generated REST Docs snippets, build reports, SBOMs, and security scan outputs
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
  - update `.idea/codeStyles/Project.xml` so IntelliJ project style mirrors Gradle formatter inclusions/exclusions and owns import behavior not covered by Palantir
  - remove stale IntelliJ exclusions for the removed Eclipse formatter file
  - keep `.editorconfig` focused on fundamental settings
  - update `SETUP.md`, `CONTRIBUTING.md`, and `ai/CODE_STYLE.md` so maintainers use Palantir/IntelliJ/EditorConfig terminology instead of the Eclipse-profile Spotless terminology
  - avoid source reformatting in this commit except unavoidable build file formatting
- validation checkpoint:
  - `./build.ps1 tasks --all --no-daemon`
  - `./build.ps1 checkFormat --no-daemon --continue` after confirming the Palantir plugin exposes `checkFormat`; expected source-format drift is acceptable before Milestone 2, but Gradle configuration errors are not
  - XML parsing or IDE loading check for `.idea/codeStyles/Project.xml`
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
  - no formatter configuration, IntelliJ project style, roadmap, or docs changes in this commit unless Milestone 1 must be corrected first
- exact deliverables:
  - run `./build.ps1 format --no-daemon` after confirming the Palantir plugin exposes `format`
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
- If Palantir plugin task names differ from `format` and `checkFormat`, update this plan before claiming Milestone 1 validation or Milestone 2 execution.
- If the configuration commit causes compilation or static-analysis failures unrelated to formatting drift, fix them before the reformat commit.
- If IntelliJ reformat produces Java output different from Palantir after the project style update, Palantir wins for Java and the IntelliJ setup must be adjusted.
- Do not let `.gitignore` exceptions accidentally expose broader `.idea/` workspace metadata.

## Validation Plan
- Milestone 1:
  - `./build.ps1 tasks --all --no-daemon`
  - `./build.ps1 checkFormat --no-daemon --continue` after verifying the Palantir task name; expected source-format drift only
  - XML parsing or IntelliJ project load check for `.idea/codeStyles/Project.xml`
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
  - confirm `.idea/codeStyles/Project.xml` and Gradle formatter scope stay aligned
  - confirm Java imports do not use wildcard imports after both Gradle formatting and IntelliJ optimize-imports checks

## Better Engineering Notes
- Keep this transition separate from dependency sweeps, PMD tuning, SpotBugs rule-policy changes, and source refactors.
- Do not create a custom formatter or hand-written formatting script.
- If the repository still needs CI-owned formatting for non-Java file types, prefer a narrow, deterministic Gradle formatter configuration over broad root-level globs.
- Record any deviation from the two-commit split in `Validation Results` before committing.

## Validation Results
- 2026-05-06 planning update:
  - archived `ai/PLAN_ci_owned_spotless_reformat.md` as partially implemented and superseded.
  - created this replacement plan for the Palantir/IntelliJ/EditorConfig/SpotBugs direction requested by the user.
  - updated `ROADMAP.md` to point active formatter work at this plan.

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
