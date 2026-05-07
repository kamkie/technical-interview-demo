# Plan: Post-2.0 Maintainability Cleanup

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Closed |
| Status | Released |

## Summary
- Execute the previously deferred maintainability-only backlog as the `v2.0.0-M8` prerelease batch: replace one deprecated Spring Security headers DSL call, replace one deprecated Jackson `ObjectMapper` setter in `buildSrc`, and remove the unused advice binding in `ServiceLoggingAspect`.
- Keep the batch contract-neutral. The published `Permissions-Policy` header, benchmark-result JSON behavior, and service-logging scope must stay unchanged while the targeted CodeQL maintainability and quality alerts are cleared.
- Success is measured by: no targeted deprecated API usage in the scoped files, no unused `service` advice parameter, existing security-header behavior preserved, the repository build green, and no REST Docs, OpenAPI, HTTP example, or README API-contract churn.

## Scope
- In scope:
  - the three `ROADMAP.md` items now selected for `v2.0.0-M8`
  - the smallest implementation and test changes needed to clear CodeQL alerts `#20`, `#14`, and `#15`
  - preserving the current internal and public behavior while removing the targeted deprecated or unused APIs
- Out of scope:
  - cutting `v2.0.0-M8`, `v2.0.0-RC1`, or stable `v2.0.0`
  - REST Docs, approved OpenAPI, HTTP example, or README API-contract changes unless implementation uncovers a real regression
  - broader security-header redesign, benchmark-task refactoring, or logging-system cleanup beyond the three scoped alerts
  - unrelated CodeQL, Dependabot, CI, or documentation backlog items

## Current State
- `ROADMAP.md` previously deferred this cleanup until after stable `2.0`, but the executed release pulled it forward into the `v2.0.0-M8` prerelease batch before the later `v2.0.0-RC1` freeze.
- `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecurityConfiguration.java` still configures the `Permissions-Policy` response header through `headers.permissionsPolicy(...)`. The local Spring Security `7.0.5` sources resolved by this build mark that DSL entry point deprecated in favor of `permissionsPolicyHeader(...)`.
- The current `Permissions-Policy` header behavior is already part of repo truth:
  - `src/test/java/team/jit/technicalinterviewdemo/technical/security/SecurityHeadersIntegrationTests.java` asserts the header and exact value on overview, docs, health, session, and problem-response surfaces
  - `src/test/java/team/jit/technicalinterviewdemo/testing/AbstractDocumentationIntegrationTest.java` keeps the same header in the shared REST Docs response-header contract
- `buildSrc/src/main/kotlin/team/jit/technicalinterviewdemo/build/GatlingBenchmarkTask.kt` builds a shared `OBJECT_MAPPER` with `setSerializationInclusion(JsonInclude.Include.NON_NULL)`. The local Jackson `2.21.1` sources resolved by this repo deprecate that setter in favor of `setDefaultPropertyInclusion(...)`.
- `buildSrc` currently has no dedicated `src/test` coverage for `GatlingBenchmarkTask`, so the task wiring is mainly proven through Gradle loading and the standard repository build.
- `src/main/java/team/jit/technicalinterviewdemo/technical/logging/ServiceLoggingAspect.java` uses `@Around("@within(service)")` and accepts a `Service service` parameter that is never read. There is currently no focused regression test for the service-only advice matching.
- This is internal maintainability work only. If implementation stays behavior-neutral, the published contract artifacts should not need updates.

## Requirement Gaps And Open Questions
- No blocking user-input gap remains.
- No open product or compatibility question remains. The plan is ready for execution as written.

## Locked Decisions And Assumptions
- This plan executes exactly the three `v2.0.0-M8` maintainability items selected from `ROADMAP.md`.
- `v2.0.0-M8` is an internal prerelease cleanup batch inserted before `v2.0.0-RC1`; it does not reopen the published `2.0` contract or re-defer the selected backlog.
- Preserve public behavior and docs. Do not touch REST Docs, approved OpenAPI, HTTP examples, or README API-contract text unless implementation reveals a real regression.
- Replace `headers.permissionsPolicy(...)` with `headers.permissionsPolicyHeader(...)` and keep the policy string exactly `geolocation=(), microphone=(), camera=()`.
- Replace `setSerializationInclusion(JsonInclude.Include.NON_NULL)` with `setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL)` and keep the shared `OBJECT_MAPPER` construction minimal; do not turn this into a broader Jackson builder rewrite.
- Remove the unused aspect binding by changing the advice pointcut to `@within(org.springframework.stereotype.Service)` and dropping the `Service` parameter from `logServiceCall(...)`.
- Add one focused regression test for `ServiceLoggingAspect` so the pointcut cleanup is proved directly. Do not introduce a new `buildSrc` test harness just for the Jackson setter rename unless the standard build stops being sufficient proof.
- Keep adjacent static-analysis cleanup out of scope unless it is required to make the three selected fixes compile cleanly.

## Execution Mode Fit
- Recommended default mode: `Single Branch`
- Why that mode fits best:
  - the work is a small, internal, three-file batch with one shared validation story
  - the repository default is `Single Branch`, and the coordination cost of splitting this work would exceed the benefit
  - final proof depends on one repo-wide `build` pass rather than independent public-contract slices
- Coordinator-owned or otherwise shared files if the work fans out:
  - `ai/archive/PLAN_post_2_0_maintainability_cleanup.md`
  - `ROADMAP.md`
  - `CHANGELOG.md`
- Candidate worker boundaries or plan splits if later delegation becomes necessary:
  - slice 1: `technical.security` header DSL replacement
  - slice 2: `buildSrc` object-mapper setter replacement
  - slice 3: `technical.logging` aspect pointcut cleanup plus its focused regression test

## Affected Artifacts
- Tests:
  - `src/test/java/team/jit/technicalinterviewdemo/technical/security/SecurityHeadersIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/testing/AbstractDocumentationIntegrationTest.java` as the existing shared documentation-header contract to preserve
  - new focused coverage under `src/test/java/team/jit/technicalinterviewdemo/technical/logging/ServiceLoggingAspectTests.java`
- Docs:
  - `ROADMAP.md` already routes this work through `v2.0.0-M8`; no further roadmap edits are expected during implementation unless scope changes
  - `CHANGELOG.md` remains release-owned and should not change until `v2.0.0-M8` is actually prepared or released
- OpenAPI:
  - none expected
- HTTP examples:
  - none expected
- Source files:
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecurityConfiguration.java`
  - `buildSrc/src/main/kotlin/team/jit/technicalinterviewdemo/build/GatlingBenchmarkTask.kt`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/logging/ServiceLoggingAspect.java`
  - the new focused test file for `ServiceLoggingAspect`
- Owning AI guide updates when durable repo guidance changes:
  - none expected
- Build or benchmark checks:
  - `pwsh ./scripts/classify-changed-files.ps1 -Uncommitted`
  - focused milestone checks for `SecurityHeadersIntegrationTests`, `help --task gatlingBenchmark`, and the new `ServiceLoggingAspectTests`
  - `.\gradlew.bat build`
  - `.\gradlew.bat gatlingBenchmark` is not expected because the plan does not change book list/search behavior, localization lookup behavior, or OAuth/session startup behavior; only run it if implementation unexpectedly changes benchmark-task runtime semantics beyond the setter replacement

## Execution Milestones
### Milestone 1: Replace The Deprecated Security Header DSL
- goal
  - clear CodeQL maintainability alert `#20` while preserving the current `Permissions-Policy` header contract
- owned files or packages
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecurityConfiguration.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/security/SecurityHeadersIntegrationTests.java` only if a focused assertion needs adjustment to match the replacement API without changing behavior
- shared files that a `Shared Plan` worker must leave to the coordinator
  - `ai/archive/PLAN_post_2_0_maintainability_cleanup.md`
  - `ROADMAP.md`
  - `CHANGELOG.md`
- behavior to preserve
  - the `Permissions-Policy` response header stays present with the exact value `geolocation=(), microphone=(), camera=()`
  - the current non-prod absence and prod-only presence of `Strict-Transport-Security` remain unchanged
- exact deliverables
  - replace the deprecated `permissionsPolicy(...)` DSL usage with `permissionsPolicyHeader(...)`
  - keep the existing header value and surrounding header configuration unchanged
  - confirm the existing focused security-header spec still matches the runtime behavior
- validation checkpoint
  - `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.technical.security.SecurityHeadersIntegrationTests --no-daemon`
- commit checkpoint
  - one commit for the alert `#20` cleanup and any tightly scoped test adjustment required to preserve the same header contract

### Milestone 2: Replace The Deprecated Gatling ObjectMapper Setter
- goal
  - clear CodeQL maintainability alert `#14` without changing the benchmark baseline or latest-result JSON semantics
- owned files or packages
  - `buildSrc/src/main/kotlin/team/jit/technicalinterviewdemo/build/GatlingBenchmarkTask.kt`
- shared files that a `Shared Plan` worker must leave to the coordinator
  - `ai/archive/PLAN_post_2_0_maintainability_cleanup.md`
  - `ROADMAP.md`
  - `CHANGELOG.md`
- behavior to preserve
  - the benchmark result writer still omits null properties
  - the task registration, baseline comparison flow, and serialized benchmark payload structure remain unchanged
- exact deliverables
  - replace `setSerializationInclusion(JsonInclude.Include.NON_NULL)` with `setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL)`
  - keep the existing shared `OBJECT_MAPPER` instance and read or write flow intact
  - avoid unrelated `buildSrc` refactors or new helper abstractions
- validation checkpoint
  - `.\gradlew.bat help --task gatlingBenchmark --no-daemon`
  - manual diff review confirming the null-exclusion intent remains the same
- commit checkpoint
  - one commit for the alert `#14` cleanup after the task still loads and configures through Gradle

### Milestone 3: Remove The Unused Service Aspect Binding
- goal
  - clear CodeQL quality alert `#15` while preserving service-only advice matching and the current log message shape
- owned files or packages
  - `src/main/java/team/jit/technicalinterviewdemo/technical/logging/ServiceLoggingAspect.java`
  - new focused coverage under `src/test/java/team/jit/technicalinterviewdemo/technical/logging/ServiceLoggingAspectTests.java`
- shared files that a `Shared Plan` worker must leave to the coordinator
  - `ai/archive/PLAN_post_2_0_maintainability_cleanup.md`
  - `ROADMAP.md`
  - `CHANGELOG.md`
- behavior to preserve
  - `@Service` beans remain the only intended advice target
  - argument sanitization, duration logging, success logging, and exception logging keep the current message structure
- exact deliverables
  - replace the bound pointcut expression with `@within(org.springframework.stereotype.Service)`
  - drop the unused `Service` parameter from the advice signature
  - add one focused regression test proving that a service call is logged and a non-service bean is not intercepted
- validation checkpoint
  - `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.technical.logging.ServiceLoggingAspectTests --no-daemon`
- commit checkpoint
  - one commit for the alert `#15` cleanup plus the new focused regression coverage

## Edge Cases And Failure Modes
- Using the wrong Spring Security replacement can silently drop the `Permissions-Policy` header or change the surrounding header chain.
- The Jackson setter replacement must keep null omission intact for benchmark-result files; otherwise benchmark baseline churn would appear even though the task logic did not change.
- The service-aspect pointcut cleanup must not accidentally broaden interception to controllers or components, or stop intercepting proxied `@Service` beans.
- Because there is no existing direct aspect test, the new focused coverage should stay narrow and avoid brittle full-application log assertions.
- This batch is contract-neutral by intent. Any need to refresh REST Docs, HTTP examples, approved OpenAPI, or README API contract text is a sign that implementation changed behavior and should be reviewed before proceeding.
- If additional static-analysis findings appear in the touched files, record them separately unless fixing them is required to keep this plan compiling and behavior-neutral.

## Validation Plan
- commands to run
  - `pwsh ./scripts/classify-changed-files.ps1 -Uncommitted`
  - `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.technical.security.SecurityHeadersIntegrationTests --no-daemon`
  - `.\gradlew.bat help --task gatlingBenchmark --no-daemon`
  - `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.technical.logging.ServiceLoggingAspectTests --no-daemon`
  - `.\gradlew.bat build --no-daemon`
- tests to add or update
  - keep `SecurityHeadersIntegrationTests` green with the same header assertions
  - add focused `ServiceLoggingAspectTests`
  - do not add new OpenAPI, REST Docs, or HTTP example tests unless behavior unexpectedly changes
- docs or contract checks
  - confirm REST Docs, approved OpenAPI, HTTP examples, and README API contract text remain unchanged
  - leave `CHANGELOG.md` for the later `v2.0.0-M8` release-preparation step
- manual verification steps
  - inspect the three scoped source files and confirm the targeted deprecated or unused API usage is gone
  - confirm no unexpected contract-doc drift appears in the diff
  - confirm the final `build` pass is the only heavyweight validation required by repo policy for this internal cleanup

## Better Engineering Notes
- Keep each fix direct. This repo does not need a broader security-header abstraction, Jackson configuration helper, or logging-aspect redesign to clear three narrow code-scanning alerts.
- Prefer exact replacement APIs over broader rewrites so `v2.0.0-M8` stays a small, reviewable internal batch.
- If the `buildSrc` setter change starts forcing benchmark-output or baseline churn, stop and re-check whether the replacement preserved null-exclusion semantics before changing any benchmark artifacts.
- Keep the later `v2.0.0-RC1` contract-freeze work separate. This plan should make that milestone smaller, not blur it together with unrelated cleanup.

## Validation Results
- 2026-05-05 - Milestone 1 completed.
- Replaced the deprecated Spring Security `permissionsPolicy(...)` DSL entry point with `permissionsPolicyHeader(...)` while keeping the `Permissions-Policy` value unchanged.
- Passed: `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.technical.security.SecurityHeadersIntegrationTests --no-daemon`
- Validation used `. ./scripts/load-dotenv.ps1` so Gradle ran with the repo-local `JAVA_HOME=C:\Users\kamki\.jdks\azul-25.0.3`.
- 2026-05-05 - Milestone 2 completed.
- Replaced `setSerializationInclusion(JsonInclude.Include.NON_NULL)` with `setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL)` on the shared Gatling benchmark `OBJECT_MAPPER`.
- Passed: `.\gradlew.bat help --task gatlingBenchmark --no-daemon`
- Manual diff review confirmed the change only preserves the existing null-exclusion intent; task registration, baseline comparison flow, and benchmark payload structure stayed unchanged.
- 2026-05-05 - Milestone 3 completed.
- Replaced the bound `@within(service)` pointcut with `@within(org.springframework.stereotype.Service)`, dropped the unused advice parameter, and added focused proxy-based regression coverage for service-only interception.
- Passed: `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.technical.logging.ServiceLoggingAspectTests --no-daemon`
- 2026-05-05 - Final integration validation completed.
- Passed: `./scripts/classify-changed-files.ps1 -Uncommitted`
- The classifier reported a clean worktree with `skipHeavyValidation=false`, so the standard repository build remained required.
- Passed: `.\gradlew.bat build --no-daemon`
- The full build passed, including REST Docs generation, OpenAPI compatibility checks, PMD, SpotBugs, Trivy vulnerability scans, SBOM generation, Docker image build, and the rest of the standard `build` gates.
- Manual diff review confirmed the implementation stayed limited to the five scoped plan files and produced no REST Docs, approved OpenAPI, HTTP example, README, or `CHANGELOG.md` churn.
- 2026-05-05 - Released as `v2.0.0-M8` and archived under `ai/archive/`.

## User Validation
- After implementation, inspect `SecurityConfiguration.java`, `GatlingBenchmarkTask.kt`, and `ServiceLoggingAspect.java` and confirm the targeted deprecated or unused APIs are gone.
- Run `.\gradlew.bat build` and confirm the repository stays green without REST Docs, OpenAPI, or HTTP example churn.
- Review the eventual `v2.0.0-M8` diff and confirm it stays limited to internal maintainability cleanup ahead of `v2.0.0-RC1`.
