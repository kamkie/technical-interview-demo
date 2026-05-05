# Plan: 2.0 Pre-RC Security Hardening

## Lifecycle
| Field | Value |
| --- | --- |
| Phase | Implementation |
| Status | In Progress |

## Summary
- Execute the currently checked `ROADMAP.md` items as one pre-`v2.0.0-RC1` security and supply-chain hardening batch: pin flagged GitHub Actions to full SHAs, sanitize the remaining user-controlled log fields in API problem and tracing logs, and clear the selected Dependabot alerts for PMD, `commons-lang3`, `plexus-utils`, and `jruby`.
- Keep the batch intentionally narrow. The checked release-confidence item is a scope guard: land only GitHub security and supply-chain fixes before the RC1 freeze, and leave maintainability-only CodeQL notes plus the actual RC1 and stable-release steps for later work.
- Success is measured by: no floating third-party `uses:` refs in the named workflows, the selected CodeQL and Dependabot alerts cleared without public API drift, logging tests proving raw user input no longer reaches log lines, and a final validation story that leaves the repo ready for the separate RC1 freeze task.

## Scope
- In scope:
  - `Pin the third-party GitHub Actions flagged by CodeQL to verified full commit SHAs in .github/workflows/ci.yml, .github/workflows/release.yml, .github/workflows/codeql.yml, and .github/workflows/post-deploy-smoke.yml (CodeQL alerts #22, #19, #18, #17, #16, #7, #6, #5, and #2).`
  - `Sanitize user-controlled problem-detail logging in src/main/java/team/jit/technicalinterviewdemo/technical/api/ApiProblemFactory.java to clear CodeQL log-injection alerts #11 and #10.`
  - `Sanitize user-controlled request tracing fields in src/main/java/team/jit/technicalinterviewdemo/technical/logging/HttpTracingLoggingFilter.java to clear CodeQL log-injection alerts #13 and #12.`
  - `Upgrade the transitive org.codehaus.plexus:plexus-utils dependency to 4.0.3 or newer to resolve Dependabot alert #4 (GHSA-6fmv-xxpf-w3cw).`
  - `Upgrade direct net.sourceforge.pmd:pmd-core to 7.22.0 or newer to resolve Dependabot alert #3 (GHSA-8rr6-2qw5-pc7r).`
  - `Upgrade the transitive org.apache.commons:commons-lang3 dependency to 3.18.0 or newer to resolve Dependabot alert #2 (GHSA-j288-q9x7-2f5v).`
  - `Upgrade direct org.jruby:jruby to 9.4.12.1 or newer to resolve Dependabot alert #1 (GHSA-72qj-48g4-5xgx).`
  - `Keep the remaining pre-v2.0.0-RC1 cleanup scope to GitHub security and supply-chain fixes, and defer the maintainability-only CodeQL notes until after stable 2.0.`
- Out of scope:
  - cutting `v2.0.0-RC1`, releasing stable `v2.0.0`, updating released-history sections in `CHANGELOG.md`, or removing the `2.0` roadmap track
  - the deferred maintainability-only items in `SecurityConfiguration.java`, `GatlingBenchmarkTask.kt`, and `ServiceLoggingAspect.java`
  - public API, REST Docs, OpenAPI, HTTP example, or README contract changes unless implementation reveals an actual externally visible regression
  - broader CI or release redesign beyond the exact security and supply-chain alerts listed above

## Current State
- `ROADMAP.md` has eight checked items, and they all sit under the same pre-`v2.0.0-RC1` hardening theme: GitHub workflow supply-chain trust, log-injection cleanup, vulnerable build-tool dependency cleanup, and a scope freeze that excludes maintainability-only backlog.
- The four named workflows still use floating third-party `uses:` references such as `actions/checkout@v6`, `actions/setup-java@v5`, `gradle/actions/setup-gradle@v6`, `github/codeql-action@v4`, `docker/login-action@v4`, `actions/upload-artifact@v7`, `azure/setup-helm@v5`, `codecov/codecov-action@v6`, `sigstore/cosign-installer@v4.1.1`, and `actions/attest-build-provenance@v4` instead of full commit SHAs.
- `src/main/java/team/jit/technicalinterviewdemo/technical/api/ApiProblemFactory.java` already sanitizes query parameters via `SensitiveDataSanitizer.sanitizeParameters(...)`, but it still logs raw `title` and `detail` fields that can carry user-controlled exception or validation text.
- `src/main/java/team/jit/technicalinterviewdemo/technical/logging/HttpTracingLoggingFilter.java` logs request and response tracing context and accepts caller-supplied `X-Request-Id`; it does not yet sanitize free-form tracing fields beyond query-parameter maps. Existing tests prove the normal tracing-header contract but do not prove malicious tracing values are neutralized before logging.
- `src/main/java/team/jit/technicalinterviewdemo/technical/logging/SensitiveDataSanitizer.java` only knows how to classify sensitive names and sanitize parameter maps today; there is no shared sanitizer for free-form log fragments or tracing and header values.
- `build.gradle.kts` pins `pmdVersion = "7.17.0"` and does not currently apply any explicit override or constraint for `commons-lang3`, `plexus-utils`, or `jruby`.
- The current published contract already documents `X-Request-Id` and `traceparent` response headers on public endpoints, so tracing hardening must preserve valid-header behavior while removing log-injection exposure.
- The relevant current specs are internal rather than contract-facing:
  - workflow definitions under `.github/workflows/`
  - logging and tracing regression coverage in `src/test/java/team/jit/technicalinterviewdemo/technical/logging/RequestLoggingIntegrationTests.java` and `src/test/java/team/jit/technicalinterviewdemo/technical/logging/HttpTracingIntegrationTests.java`
  - API error-shape unit coverage in `src/test/java/team/jit/technicalinterviewdemo/technical/api/ApiExceptionHandlerTests.java` and `src/test/java/team/jit/technicalinterviewdemo/technical/security/ApiSecurityErrorHandlerTests.java`
  - build and dependency ownership in `build.gradle.kts`
  - release and roadmap boundaries in `CHANGELOG.md`, `ROADMAP.md`, and `ai/RELEASES.md`

## Requirement Gaps And Open Questions
- No blocking user-input gap remains. The checked roadmap items are specific enough to plan as one execution document.
- Non-blocking implementation lookup:
  - the exact trusted full commit SHAs for each third-party action and the exact resolved configurations carrying `plexus-utils`, `commons-lang3`, and `jruby` still need to be confirmed during execution
  - why it matters: the fixes should stay narrow and reviewable instead of turning into broad workflow or plugin churn
  - fallback: keep each workflow on the same approved action release line unless a compatible security update is required, and use the smallest root-build version bump or dependency constraint that clears the selected alerts

## Locked Decisions And Assumptions
- This plan includes exactly the eight checked roadmap items above and no unchecked `ROADMAP.md` work.
- Treat the checked release-confidence item as a scope guard, not as a separate feature milestone. Its effect is that the plan stops after the selected GitHub security, logging, and dependency fixes are implemented and validated.
- Preserve the public `/api/**` contract. No REST Docs, approved OpenAPI, reviewer HTTP examples, or README API behavior changes are intended.
- Preserve valid `X-Request-Id` and `traceparent` behavior for normal callers. Logging hardening may sanitize, normalize, or regenerate unsafe values for logging purposes, but it should not break the documented response-header contract for valid inputs.
- Prefer one shared sanitization path over ad hoc escaping in each log statement. If new helper methods are needed, add them to `SensitiveDataSanitizer` instead of scattering regex cleanup across multiple classes.
- Prefer the smallest build-surface change that clears the selected alerts:
  - bump the direct PMD version in `build.gradle.kts`
  - use targeted dependency constraints or plugin-version adjustments for `commons-lang3`, `plexus-utils`, and `jruby`
  - avoid unrelated plugin replacements or broader build refactors
- Leave the maintainability-only CodeQL backlog untouched:
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecurityConfiguration.java` permissions-policy cleanup
  - `buildSrc/src/main/kotlin/team/jit/technicalinterviewdemo/build/GatlingBenchmarkTask.kt` serialization-inclusion cleanup
  - `src/main/java/team/jit/technicalinterviewdemo/technical/logging/ServiceLoggingAspect.java` unused-parameter cleanup
- Leave the actual `v2.0.0-RC1` cut and stable `v2.0.0` release work for the later unchecked roadmap items and the release flow in `ai/RELEASES.md`.

## Execution Mode Fit
- Recommended default mode: `Single Branch`
- Why that mode fits best:
  - the work is one coherent hardening batch with shared final validation and coupled release-readiness intent
  - the likely change set overlaps on build verification, workflow review, and plan or changelog tracking even though the code touches different files
  - the repository default is `Single Branch`, and this batch is still small enough to execute sequentially without high coordination cost
- Coordinator-owned or otherwise shared files if the work later fans out:
  - `ai/PLAN_2_0_pre_rc_security_hardening.md`
  - `CHANGELOG.md`
  - `ROADMAP.md`
  - `README.md` only if execution uncovers a real need for maintainer-facing security-posture wording changes
- Candidate worker boundaries if later delegation becomes necessary:
  - worker slice 1: GitHub workflow SHA pinning in `.github/workflows/` and `.github/dependabot.yml` if needed
  - worker slice 2: logging hardening in `technical.api` and `technical.logging` plus focused tests
  - worker slice 3: build dependency upgrades in `build.gradle.kts` and any narrowly required build-support file

## Affected Artifacts
- Tests:
  - `src/test/java/team/jit/technicalinterviewdemo/technical/logging/RequestLoggingIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/logging/HttpTracingIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/api/ApiExceptionHandlerTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/security/ApiSecurityErrorHandlerTests.java`
  - likely new focused test coverage such as:
    - `src/test/java/team/jit/technicalinterviewdemo/technical/logging/SensitiveDataSanitizerTests.java`
    - `src/test/java/team/jit/technicalinterviewdemo/technical/api/ApiProblemFactoryLoggingTests.java`
- Docs:
  - no published contract-doc updates are expected during implementation
  - `CHANGELOG.md` remains an execution-tracking artifact only; versioned release history changes belong to the later RC and stable release work
  - `ROADMAP.md` should change only if execution uncovers that the selected scope needs to be re-stated, not as part of the normal implementation path
- OpenAPI:
  - none expected
- HTTP examples:
  - none expected
- Source files:
  - `.github/workflows/ci.yml`
  - `.github/workflows/release.yml`
  - `.github/workflows/codeql.yml`
  - `.github/workflows/post-deploy-smoke.yml`
  - `.github/dependabot.yml` only if GitHub Actions SHA pinning needs update-automation alignment
  - `src/main/java/team/jit/technicalinterviewdemo/technical/api/ApiProblemFactory.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/logging/HttpTracingLoggingFilter.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/logging/SensitiveDataSanitizer.java`
  - `build.gradle.kts`
  - only if the transitive dependency owner cannot be fixed cleanly from the root build: the narrow build-support file that owns that toolchain dependency
- Owning AI guide updates when durable repo guidance changes:
  - none expected
- Build or benchmark checks:
  - `pwsh ./scripts/classify-changed-files.ps1 -Uncommitted`
  - focused test execution for the touched logging and API error-handling classes
  - `.\gradlew.bat build`
  - `.\gradlew.bat gatlingBenchmark` only if the final dependency or tracing changes prove to affect OAuth or session-startup behavior; otherwise it is not expected for this batch
  - optional workflow lint or equivalent manual review for the pinned GitHub Actions YAML because Gradle build validation does not parse workflow files

## Execution Milestones
### Milestone 1: Pin Third-Party GitHub Actions To Verified SHAs
- goal
  - clear the selected CodeQL workflow-supply-chain alerts by replacing floating third-party `uses:` tags with trusted full commit SHAs in the four named workflows
- owned files or packages
  - `.github/workflows/ci.yml`
  - `.github/workflows/release.yml`
  - `.github/workflows/codeql.yml`
  - `.github/workflows/post-deploy-smoke.yml`
  - `.github/dependabot.yml` only if needed so Dependabot keeps proposing SHA-pinned GitHub Actions updates cleanly
- shared files that a `Shared Plan` worker must leave to the coordinator
  - `ai/PLAN_2_0_pre_rc_security_hardening.md`
  - `CHANGELOG.md`
  - `ROADMAP.md`
- behavior to preserve
  - workflow triggers, permissions intent, job dependencies, artifact names, and release or smoke semantics remain unchanged apart from the pinning itself
- exact deliverables
  - replace every third-party `uses:` reference in the four named workflows with a full commit SHA
  - keep each action on the approved release line unless a compatible reviewed security update is required to get to a trustworthy SHA
  - keep GitHub-native update automation workable after the pinning change
  - document the exact action-to-SHA mapping in commit history or plan validation notes so reviewers can audit the trust decision
- validation checkpoint
  - manual diff review of every pinned action reference
  - run workflow lint if available; otherwise record the manual workflow validation explicitly
- commit checkpoint
  - one commit for the pinned-workflow batch and any required Dependabot alignment

### Milestone 2: Sanitize Remaining User-Controlled Log Fields
- goal
  - clear the selected CodeQL log-injection alerts in API problem logging and request tracing without changing API response contracts
- owned files or packages
  - `src/main/java/team/jit/technicalinterviewdemo/technical/api/ApiProblemFactory.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/logging/HttpTracingLoggingFilter.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/logging/SensitiveDataSanitizer.java`
  - the focused logging and error-handling tests listed above, including any new narrow test class added for log output assertions
- shared files that a `Shared Plan` worker must leave to the coordinator
  - `ai/PLAN_2_0_pre_rc_security_hardening.md`
  - `CHANGELOG.md`
  - any README or roadmap file unless execution proves it genuinely must move
- behavior to preserve
  - existing `ProblemDetail` status, title, detail, localized message fields, and public tracing headers for valid requests remain unchanged
  - request and response logs stay useful for method, path, status, duration, and correlation troubleshooting
- exact deliverables
  - extend `SensitiveDataSanitizer` or equivalent shared helper to sanitize free-form log fragments or tracing values, not only parameter maps
  - update `ApiProblemFactory` so raw user-controlled `title`, `detail`, or context strings no longer flow directly into warning or error log lines
  - update `HttpTracingLoggingFilter` so user-controlled tracing fields are sanitized or canonicalized before they are written to logs or MDC-backed correlation fields
  - add regression coverage proving malicious input does not appear raw in captured logs while valid request IDs and tracing behavior still work
- validation checkpoint
  - run the focused logging and error-handling tests
  - manually inspect captured log assertions for both request logging and API-problem logging paths
- commit checkpoint
  - one commit for logging hardening plus its focused regression coverage

### Milestone 3: Upgrade The Selected Vulnerable Build Dependencies
- goal
  - clear the selected Dependabot alerts with the smallest build-only version changes that keep the demo repo stable
- owned files or packages
  - `build.gradle.kts`
  - the narrowest build-support file only if the exact transitive owner cannot be corrected cleanly from the root build
  - any focused test or validation helper needed to prove the resolved versions
- shared files that a `Shared Plan` worker must leave to the coordinator
  - `ai/PLAN_2_0_pre_rc_security_hardening.md`
  - `CHANGELOG.md`
  - `ROADMAP.md`
- behavior to preserve
  - public API, runtime behavior, REST Docs, OpenAPI, HTTP examples, and the current release flow remain unchanged
  - PMD, Gatling, and related build tasks continue to execute on the same repository surfaces after the upgrade
- exact deliverables
  - raise `net.sourceforge.pmd:pmd-core` to `7.22.0` or newer
  - resolve `org.codehaus.plexus:plexus-utils` to `4.0.3` or newer
  - resolve `org.apache.commons:commons-lang3` to `3.18.0` or newer
  - resolve `org.jruby:jruby` to `9.4.12.1` or newer
  - confirm the exact owning configuration for each alert before choosing the narrowest direct version bump or dependency constraint
- validation checkpoint
  - run `dependencyInsight` or equivalent resolved-dependency proof for the affected tool configurations
  - run `.\gradlew.bat build`
  - run `.\gradlew.bat gatlingBenchmark` only if the final dependency path shows session-startup or Gatling-behavior risk that exceeds normal build confidence
- commit checkpoint
  - one commit for the dependency upgrade batch after the resolved versions and full build pass are recorded

## Edge Cases And Failure Modes
- Pinning actions by SHA can accidentally change semantics if the pinned commit does not correspond to the currently intended release line. The implementation must verify provenance and keep behavior constant unless a reviewed compatible update is required.
- GitHub Actions pinning can break automatic update flow if `.github/dependabot.yml` is left incompatible with SHA-pinned action references.
- Log hardening must not remove the useful structured context needed for operations. The fix should sanitize or canonicalize user input, not reduce every log field to opaque placeholders when stable identifiers can still be preserved.
- `X-Request-Id` and `traceparent` are already part of the observable response surface. Any normalization rule must preserve valid inputs and keep the emitted headers syntactically correct.
- `ApiProblemFactory` often logs exception-derived or validation-derived text. Sanitizing the log representation must not accidentally change the actual response payload returned to clients.
- Dependency overrides can destabilize tool-only configurations even when application runtime is unaffected. The implementation must verify the actual owning configuration before forcing a broad root-level override.
- The checked scope guard means deferred maintainability alerts stay untouched even if they are nearby in the same classes or files. Do not let opportunistic cleanup blur the release-candidate boundary.
- The unchecked RC1-freeze and stable-release roadmap items remain separate. This plan should end with the hardening batch validated, not with a tag cut or roadmap cleanup masquerading as implementation.

## Validation Plan
- commands to run
  - `pwsh ./scripts/classify-changed-files.ps1 -Uncommitted`
  - focused test execution covering the touched logging and API error-handling surfaces, for example:
    - `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.technical.logging.RequestLoggingIntegrationTests --tests team.jit.technicalinterviewdemo.technical.logging.HttpTracingIntegrationTests --tests team.jit.technicalinterviewdemo.technical.api.ApiExceptionHandlerTests --tests team.jit.technicalinterviewdemo.technical.security.ApiSecurityErrorHandlerTests`
    - include any new focused logging test class in the same invocation
  - resolved-dependency proof for the selected alerts using `dependencyInsight` on the affected tool configurations before or alongside the final build validation
  - `.\gradlew.bat build`
  - `.\gradlew.bat gatlingBenchmark` only if the final implementation materially affects OAuth or session-startup behavior, or if the build-tool upgrade leaves credible uncertainty about the existing Gatling path
- tests to add or update
  - extend logging coverage so captured logs prove raw malicious request IDs, traceparent values, or problem-detail text do not appear verbatim
  - keep the existing valid tracing-header coverage green
  - add or update narrow dependency-resolution assertions only if the build file changes are otherwise hard to review; prefer resolved-dependency proof over heavy custom tests
- docs or contract checks
  - confirm no unexpected REST Docs, HTTP example, OpenAPI, or README API-contract drift
  - confirm the deferred maintainability-only CodeQL items are still out of scope after the change
  - if `README.md` or release guidance changes unexpectedly become necessary, route them through `ai/DOCUMENTATION.md` and keep the wording limited to the actual security-posture change
- manual verification steps
  - inspect the four workflows and confirm no floating third-party `uses:` refs remain
  - inspect captured log assertions or local output and confirm sanitized log representations still preserve actionable method, path, status, and correlation context
  - inspect the resolved dependency versions after the build changes and confirm they meet the roadmap minimums
  - confirm the repo remains ready for the later unchecked RC1 freeze task rather than absorbing it into this batch

## Better Engineering Notes
- This workstream is intentionally a hardening batch, not a platform rewrite. Prefer direct workflow pinning, one shared sanitization helper, and narrow build constraints over broader CI or build-system redesign.
- Use the existing tests as the contract to preserve. If a fix would force REST Docs, OpenAPI, or HTTP example edits, stop and confirm whether the implementation accidentally changed public behavior.
- Keep the deferred maintainability backlog deferred. Nearby cleanup that does not clear one of the checked alerts should not ride along just because the file is already open.
- Treat the later RC1 cut as a separate plan or release step. This plan should make that step safer, not silently perform it.

## Validation Results
- 2026-05-05: Milestone 1 workflow pinning
  - pinned all third-party `uses:` references in `.github/workflows/ci.yml`, `.github/workflows/release.yml`, `.github/workflows/codeql.yml`, and `.github/workflows/post-deploy-smoke.yml` to full commit SHAs while preserving the existing triggers, permissions, and job flow
  - action-to-SHA mappings used:
    - `actions/checkout@v6` -> `de0fac2e4500dabe0009e67214ff5f5447ce83dd`
    - `actions/setup-java@v5` -> `be666c2fcd27ec809703dec50e508c2fdc7f6654`
    - `gradle/actions/setup-gradle@v6` -> `39fdf500b386709a9a4a769f717dad447ac345b9`
    - `actions/upload-artifact@v7` -> `043fb46d1a93c77aae656e7c1c64a875d1fc6a0a`
    - `azure/setup-helm@v5` -> `f0accbfd55e3332a28f721b8202b1016cecf90d5`
    - `codecov/codecov-action@v6` -> `57e3a136b779b570ffcdbf80b3bdc90e7fab3de2`
    - `docker/login-action@v4` -> `4907a6ddec9925e35a0a9e82d7399ccc52663121`
    - `sigstore/cosign-installer@v4.1.1` -> `cad07c2e89fa2edd6e2d7bab4c1aa38e53f76003`
    - `actions/attest-build-provenance@v4` -> `b3e506e8c389afc651c5bacf2b8f2a1ea0557215`
    - `github/codeql-action@v4` -> `ed410739ba306e4ebe5e123421a6bd694e494a2b`
  - validation:
    - `docker run --rm -v "${PWD}:/repo" -w /repo rhysd/actionlint:latest`: passed
    - manual diff review confirmed no floating third-party `uses:` refs remain in the four scoped workflows
- 2026-05-05: Milestone 2 logging hardening
  - extended `SensitiveDataSanitizer` with shared log-fragment escaping, unsafe-character detection, and nested context sanitization so free-form request, problem-detail, and tracing fields can be logged safely without changing API payloads
  - sanitized the `ApiProblemFactory` warning and error log fields for method, path, title, detail, localized message, and context while preserving the emitted `ProblemDetail` response body
  - sanitized `HttpTracingLoggingFilter` request and response log fields for method, path, and `traceparent`, and regenerate a UUID request id when the inbound `X-Request-Id` contains unsafe log characters
  - added focused regression coverage in `ApiProblemFactoryLoggingTests`, `SensitiveDataSanitizerTests`, and `HttpTracingLoggingFilterTests`; retained the existing request and tracing integration coverage for the valid header contract
  - validation:
    - `. .\scripts\load-dotenv.ps1 -Quiet; .\gradlew.bat test --tests team.jit.technicalinterviewdemo.technical.logging.RequestLoggingIntegrationTests --tests team.jit.technicalinterviewdemo.technical.logging.HttpTracingIntegrationTests --tests team.jit.technicalinterviewdemo.technical.logging.HttpTracingLoggingFilterTests --tests team.jit.technicalinterviewdemo.technical.logging.SensitiveDataSanitizerTests --tests team.jit.technicalinterviewdemo.technical.api.ApiExceptionHandlerTests --tests team.jit.technicalinterviewdemo.technical.api.ApiProblemFactoryLoggingTests --tests team.jit.technicalinterviewdemo.technical.security.ApiSecurityErrorHandlerTests --no-daemon`: passed
    - direct filter unit coverage was used for unsafe `X-Request-Id` handling because `MockMvc` rejects CRLF header values before the filter can observe them
- Record the remaining dependency-resolution proof, focused tests, full build result, and any decision about whether `gatlingBenchmark` was required as the later milestones complete

## User Validation
- Review the finished workflows and confirm the selected GitHub Actions now use full commit SHAs instead of floating version tags.
- Review the logging tests or captured output and confirm raw attacker-controlled tracing or problem-detail text no longer appears in log lines.
- Review the resolved dependency versions and confirm the selected Dependabot alerts are cleared without pulling the deferred maintainability backlog into scope.
- Confirm the repo is left ready for the separate unchecked `v2.0.0-RC1` freeze and stable-release steps, not bundled together with them.
