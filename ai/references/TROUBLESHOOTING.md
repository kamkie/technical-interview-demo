# AI Validation Troubleshooting

This on-demand reference is for AI agents after a build, test, benchmark, workflow, or release-gate command fails.
Use it to choose the first recovery step before improvising.

Do not use this file for local install walkthroughs or human environment setup; use `SETUP.md` for that.
If a pattern becomes a durable repo-wide lesson after repeated incidents, promote the rule to `ai/LEARNINGS.md` and keep this playbook focused on symptom handling.

## Recovery Rules

- Start from the first real failure in the log, not the last cleanup or reporting failure.
- Keep the original validation target unless the failure proves the command choice was wrong.
- Prefer a narrow reproducer such as `./build.ps1 test --tests <class>` only while diagnosing; return to the required command before handoff.
- Use the fastest faithful troubleshooting loop for the failing surface: run the failing task, check, or external command directly, and skip unrelated build parts only while diagnosing.
- Diagnostic skips, Gradle exclusions, and wrapper shortcuts are temporary. After the fix, run the full validation required by the target plan, `ai/EXECUTION.md`, and `ai/TESTING.md`.
- Record failed commands and skipped follow-up validation in the plan, worker log, or final report.

## Fast Diagnostic Loops

- Formatting failure: run `./build.ps1 format`, review the diff, then run `./build.ps1 checkFormat`; do not rerun the whole build for each formatting attempt.
- Docker image build failure: once a current boot jar exists and application code has not changed, iterate on `./build.ps1 dockerBuild` or an equivalent `docker build --build-arg JAR_FILE=<existing-boot-jar> ...` command instead of rebuilding the jar and all checks each time.
- Static-analysis failure: run the specific failing check where possible, such as `./build.ps1 pmdMain`, `./build.ps1 spotbugsMain`, or the reported Error Prone compile task.
- Test failure: rerun the failing test class or method while diagnosing, then return to the broader test or build command required for signoff.
- Contract or docs failure: rerun the focused REST Docs, OpenAPI, or Asciidoctor task that failed, then return to full contract validation before handoff.

## Symptom Playbook

| Symptom | Likely Cause | Canonical Action | Do Not |
| --- | --- | --- | --- |
| `./build.ps1` reports Java or toolchain mismatch. | The wrapper loaded no `.env`, a stale `.env`, or a non-JDK-25 `JAVA_HOME`. | Check the wrapper output first, then use `SETUP.md` Java guidance to correct the shell or `.env`; rerun the same command. | Do not bypass the wrapper with ad hoc `gradlew` commands unless troubleshooting the wrapper itself. |
| Compile errors appear after a dependency or build-tool change. | API drift, generated source assumptions, or annotation processor behavior changed. | Fix the source or the dependency selection, then run `./build.ps1 compileJava` or the narrow failing compile task before full validation. | Do not pin or downgrade dependencies blindly without recording why. |
| Palantir or Spotless formatting fails. | Source or support files differ from Gradle-owned formatting. | Run `./build.ps1 format`, review the diff, then run `./build.ps1 checkFormat` or the original validation. | Do not hand-format Java against IDE output when it conflicts with Gradle. |
| PMD, SpotBugs, Error Prone, or static security checks fail. | The change introduced a quality or security finding, or exposed an existing finding in touched code. | Fix the reported finding in the smallest owned scope, then rerun the failing check or `./build.ps1 build`. | Do not use `-SkipChecks` for final signoff. |
| OpenAPI compatibility fails. | Public API shape changed or generated docs drifted from the approved contract. | Decide whether the contract change is intentional; if yes, update specs and refresh the baseline with `./build.ps1 refreshOpenApiBaseline`, then run validation. | Do not delete or manually rewrite `src/test/resources/openapi/approved-openapi.json`. |
| REST Docs snippets or Asciidoctor generation fail. | Controller behavior, documented fields, response headers, or snippets no longer match. | Update the REST Docs test and AsciiDoc page together, then rerun the focused documentation test or full build. | Do not edit generated snippets under `build/` as a fix. |
| Testcontainers or PostgreSQL startup fails. | Docker is stopped, unreachable, or unable to start Linux containers. | Verify Docker with `docker ps`, follow `SETUP.md` Testcontainers guidance, then rerun the same test command. | Do not replace integration tests with mocks to avoid Docker. |
| Docker image build fails. | Dockerfile changes, base-image access, build context, or jar path handling broke image creation. | Reproduce with `./build.ps1 dockerBuild` or an equivalent direct `docker build` using the existing boot jar while diagnosing; rerun the required full validation after the Docker fix. | Do not rebuild the full application on every Dockerfile-only iteration. |
| Tests fail because a local port is already bound. | A previous app, container, or smoke run is still using the expected port. | Identify the owner process or container, stop the stale local process, and rerun; use documented local port overrides only when the runbook allows it. | Do not change committed default ports for a local conflict. |
| A focused integration test is flaky or order-dependent. | Shared state, cache, clock, transaction, or seed data assumptions leaked between tests. | Reproduce with the same class repeatedly, inspect isolation and deterministic data, and fix the test or implementation cause. | Do not add `@Disabled`, sleeps, or broad retries as the first fix. |
| Gatling benchmark validation fails. | A touched path changed query behavior, caching, startup, or response shape enough to break the baseline. | Inspect the benchmark report, confirm whether the regression is expected, and update behavior or the reviewed baseline according to the plan. | Do not treat benchmark failures as noise. |
| `./build.ps1 build` skips heavy validation unexpectedly. | The uncommitted changed-file classifier saw only lightweight files. | If final signoff needs the full Gradle build, rerun with `./build.ps1 -FullBuild build`; otherwise record the shortcut result. | Do not claim a full build ran when the wrapper skipped it. |
| A dirty worktree blocks release, archive, or branch handoff. | Uncommitted implementation, generated output, or unrelated user edits are present. | Inspect `git status --short`, separate owned changes from user changes, commit or document only the completed owned scope. | Do not run destructive cleanup or revert user changes without explicit approval. |
| A plan archive or roadmap cleanup conflicts. | Active work, release cleanup, or archive movement disagrees with lifecycle state. | Re-read `ai/RELEASES.md`, `ai/WORKFLOW.md`, and the target plan lifecycle; update the active owner artifact once. | Do not leave the same completed plan active in `ROADMAP.md` and archived under `ai/archive/`. |

## Forbidden Shortcuts

- Do not use `-DskipTests`, `@Disabled`, weakened assertions, or deleted contract baselines to make validation green.
- Do not use `./build.ps1 -SkipTests`, `./build.ps1 -SkipChecks`, or both as final validation.
- Do not confuse a successful narrow troubleshooting loop with completion; the plan-required validation still has to pass or be reported as blocked.
- Do not delete Gradle caches, Docker volumes, or generated directories before identifying why the command failed.
- Do not broaden the task into environment setup unless the failure is actually environmental and `SETUP.md` is now in scope.
