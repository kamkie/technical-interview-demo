# Plan: CI-Owned Spotless Formatting

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Planning |
| Status | Draft |

## Summary
- Replace the current IntelliJ-binary-dependent Spotless Java formatter with a CI-owned formatter configuration that works when IntelliJ IDEA is not installed.
- Use the existing exported formatter assets deliberately: `Default.xml` as the Java formatter profile and `.editorconfig` as the IDE/style reference.
- Keep review history readable with two commits: one settings/documentation commit, then one repository-wide formatting and import-organization commit.
- Success means `spotlessCheck` and the full wrapper build pass at the branch tip without requiring `IDEA_HOME` or `IDEA_FORMATTER_BINARY`.

## Scope
- In scope:
  - change root Spotless configuration in `build.gradle.kts`
  - make Java formatting cover all repository Java source sets under `src/`
  - make Kotlin/Kotlin Gradle formatting cover root Gradle scripts, `gradle/`, and `buildSrc/`
  - make misc formatting cover tracked support/code files where Spotless can safely normalize whitespace and final newlines
  - preserve import organization by keeping Spotless `importOrder` and `removeUnusedImports` in the Java pipeline
  - run IntelliJ `Reformat Code`/`Optimize Imports` once during the formatting commit when an executor has IntelliJ available, followed by `spotlessApply` as the authoritative CI-normalized result
  - update human and AI formatter guidance that currently says Java formatting is skipped without IntelliJ
- Out of scope:
  - public REST API behavior, OpenAPI baseline, REST Docs snippets, HTTP examples, benchmark behavior, and release history
  - changing Java package structure or application behavior
  - introducing a new formatter that requires an IDE or developer-local binary in CI
  - formatting ignored/generated directories such as `.gradle/`, `build/`, `.idea/`, `out/`, and `temp/`

## Current State
- `build.gradle.kts` currently discovers `ideaFormatterBinary` from `ideaFormatterBinary`, `IDEA_FORMATTER_BINARY`, or `IDEA_HOME`, and only configures `spotless { java { ... idea() ... } }` when the binary exists.
- If IntelliJ is absent, Java formatting is not configured, so CI can pass formatting checks without actually checking Java source formatting.
- Current Java Spotless targets only `src/main/java/**/*.java` and `src/test/java/**/*.java`; it misses `src/externalTest/java/**/*.java` and `src/gatling/java/**/*.java`.
- Current `kotlinGradle` targets root `*.gradle.kts` and `gradle/**/*.gradle.kts`; it misses `buildSrc/build.gradle.kts`.
- Current Spotless configuration does not format `buildSrc/src/main/kotlin/**/*.kt`.
- `.editorconfig` contains exported IntelliJ settings, including Java import layout metadata.
- `Default.xml` is an Eclipse JDT formatter profile exported from IntelliJ tooling and is the best repo-local CI-friendly Java formatter input.
- `SETUP.md`, `.env.example`, and `CONTRIBUTING.md` still document `IDEA_HOME` or `IDEA_FORMATTER_BINARY` as relevant to Spotless Java formatting.
- The referenced sibling project uses a CI-friendly Spotless Java shape based on `eclipse().configFile(...)`; copy the pattern, not the whole build.

## Requirement Gaps And Open Questions
- No blocking product question remains.
- Exact IntelliJ `Optimize Imports` behavior is not fully reproducible in CI unless CI installs and configures IntelliJ, which is the failure mode this work is removing. Fallback: run IntelliJ optimize imports once in the reformat commit when available, then let Spotless `importOrder` and `removeUnusedImports` define the durable CI-enforced import state.
- If IntelliJ and Spotless disagree during the formatting commit, Spotless wins because CI must be able to reproduce the final state without an IDE.

## Locked Decisions And Assumptions
- Use `Default.xml` through Spotless `eclipse().configFile("Default.xml")` for Java formatting.
- Keep Spotless `importOrder` and `removeUnusedImports` enabled for Java.
- Expand Java target to `src/**/*.java`.
- Add Spotless Kotlin formatting for `buildSrc/src/**/*.kt`.
- Expand Kotlin Gradle target to include `buildSrc/**/*.gradle.kts`.
- Do not require `IDEA_HOME`, `IDEA_FORMATTER_BINARY`, or `ideaFormatterBinary` for formatting.
- Preserve the user's requested commit split:
  - commit 1: formatter settings and related guidance only
  - commit 2: reformat and organize imports only
- The first commit may not pass `spotlessCheck` in isolation after the formatter is tightened; final branch-tip validation is the required proof.

## Execution Mode Fit
- Recommended mode: `Single Branch`.
- This is a cross-repository formatter transition with one shared build file and one large formatting commit. Parallel worker fanout would increase merge risk and make formatting churn harder to review.
- Coordinator-owned files for the whole task:
  - `build.gradle.kts`
  - `.editorconfig`
  - `Default.xml`
  - `SETUP.md`
  - `.env.example`
  - `CONTRIBUTING.md`
  - `ai/CODE_STYLE.md`
  - this plan file
- If delegation becomes necessary later, use only read-only review/validation workers after the two commits are prepared.

## Affected Artifacts
- Build/settings:
  - `build.gradle.kts`
  - possibly `Default.xml` only if the exported profile itself needs normalization, not semantic rewrite
- Human docs:
  - `SETUP.md`
  - `.env.example`
  - `CONTRIBUTING.md`
- AI guidance:
  - `ai/CODE_STYLE.md` if formatter ownership guidance changes durably
  - this `ai/PLAN_ci_owned_spotless_reformat.md`
- Reformat targets:
  - `src/**/*.java`
  - `buildSrc/src/main/kotlin/**/*.kt`
  - root `*.gradle.kts`
  - `buildSrc/**/*.gradle.kts`
  - `gradle/**/*.gradle.kts`
  - tracked repo support files already covered by `misc`, plus safe additions such as properties, SQL, YAML, XML, JSON, HTTP examples, Dockerfile/docker-compose files, and Markdown if configured
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
  - configure Java Spotless unconditionally with `target("src/**/*.java")`, `importOrder`, `removeUnusedImports`, and `eclipse().configFile("Default.xml")`
  - keep or derive import-order grouping from `.editorconfig` where practical, but do not reintroduce an IDEA runtime dependency
  - add Kotlin formatting for `buildSrc/src/main/kotlin/**/*.kt`
  - expand Kotlin Gradle formatting to include `buildSrc/**/*.gradle.kts`
  - expand misc targets only to safe tracked files and exclude ignored/generated directories
  - remove `IDEA_HOME`/`IDEA_FORMATTER_BINARY` as Spotless requirements from setup docs and `.env.example`
  - update contributor/AI wording to say Spotless is CI-owned and does not require IntelliJ
- validation checkpoint:
  - run `./build.ps1 tasks --all --no-daemon` to catch Gradle configuration errors without requiring formatting to already be clean
  - optionally run `./build.ps1 spotlessCheck --no-daemon --continue` and confirm any failure is only expected formatting drift, not formatter configuration failure
- commit checkpoint:
  - `build: make spotless formatting ci-owned`

### Milestone 2: Repository-Wide Reformat Commit
- goal: apply the new formatter and organize imports across tracked code and safe support files
- owned files or packages:
  - all tracked files touched by Spotless/IntelliJ formatting under the configured targets
- shared files that a `Shared Plan` worker must leave to the coordinator:
  - all formatted files; this milestone should not be split across workers
- behavior to preserve:
  - no hand refactors mixed into formatting
  - no generated or ignored directories committed
  - no OpenAPI baseline, REST Docs snippets, or HTTP examples should change semantically
- exact deliverables:
  - run IntelliJ `Reformat Code` with optimize imports enabled over repository code if IntelliJ is available to the executor
  - run `./build.ps1 spotlessApply --no-daemon` after any IntelliJ action so Spotless defines the final state
  - review `git diff --stat` and spot-check representative Java, Kotlin, Gradle, HTTP, SQL/YAML/XML/JSON/properties changes for formatting-only churn
  - keep unrelated edits out of the formatting commit
- validation checkpoint:
  - `git diff --check`
  - `./build.ps1 spotlessCheck --no-daemon`
  - `./build.ps1 build --no-daemon`
- commit checkpoint:
  - `style: apply repository formatting`

## Edge Cases And Failure Modes
- `Default.xml` may not match every IntelliJ `.editorconfig` option. Treat `Default.xml` as the CI formatter contract for Java; keep `.editorconfig` aligned enough for IDE editing, but do not promise byte-for-byte IntelliJ output.
- Eclipse JDT formatting may move comments or wrap lines differently from the current IntelliJ formatter. Spot-check high-risk Java files with multiline text blocks, annotations, and fluent assertions.
- Spotless `removeUnusedImports` can expose compile failures if unused imports were masking ambiguous simple names or if formatting changes reveal existing issues. Full `build` is required after the reformat commit.
- IntelliJ optimize imports can introduce wildcard imports depending on IDE thresholds. If that happens and Spotless does not undo it, either adjust import-order/wildcard policy in Milestone 1 before committing or manually avoid wildcard import churn in Milestone 2.
- Formatting all tracked support files can create noisy but low-value diffs. Keep misc targets to file types with deterministic whitespace/final-newline normalization and avoid generated artifacts.
- Because commit 1 tightens Spotless before commit 2 applies it, commit 1 may be red for `spotlessCheck` if checked independently. The branch tip must be green.

## Validation Plan
- Milestone 1:
  - `./build.ps1 tasks --all --no-daemon`
  - optional `./build.ps1 spotlessCheck --no-daemon --continue` with expected formatting violations only
- Milestone 2:
  - `git diff --check`
  - `./build.ps1 spotlessCheck --no-daemon`
  - `./build.ps1 build --no-daemon`
- CI parity:
  - ensure the final validation does not depend on `IDEA_HOME`, `IDEA_FORMATTER_BINARY`, or `ideaFormatterBinary`
  - confirm `.github/workflows/ci.yml` can keep running `./build.ps1 -FullBuild build --no-daemon` without adding IntelliJ setup
- Manual review:
  - inspect representative diffs from `src/main/java`, `src/test/java`, `src/externalTest/java`, `src/gatling/java`, `buildSrc/src/main/kotlin`, and Gradle scripts
  - confirm no API contract files changed except whitespace-only formatting if they are intentionally included

## Better Engineering Notes
- Keep the formatter transition separate from dependency upgrades, PMD/SpotBugs cleanup, and source refactors.
- Do not add a custom string-rewrite formatter unless the first Spotless apply reveals a small, repeatable formatter defect that cannot be handled by `Default.xml`.
- If exact IntelliJ import layout becomes a hard requirement later, that should be a separate decision because it would require either CI-installed IntelliJ or a different import-formatting tool with matching behavior.

## Validation Results
- To be filled in during execution.

## User Validation
- Review the two commits separately:
  - settings commit: only formatter configuration and guidance
  - formatting commit: whitespace/import organization only
- Pull the branch in a clean shell or CI-like environment without IntelliJ formatter variables and run:

```powershell
./build.ps1 spotlessCheck --no-daemon
./build.ps1 build --no-daemon
```
