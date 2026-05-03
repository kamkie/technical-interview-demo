# Plan: Production Session And Secret Posture

## Summary
- Tighten the selected production-hardening roadmap work around session policy, secret and credential handling, and startup validation.
- Keep the existing demo-sized session-backed OAuth posture intact while making the production expectations explicit, validated, and test-covered.
- Success is measured by: clearer prod-only session policy, fail-fast validation for required auth/session settings, aligned setup and contract docs, and passing build plus session-sensitive validation.

## Scope
- In scope:
  - `Tighten session-management settings for real deployments, including cookie scope, timeout, rotation, and concurrent-session expectations`
  - `Add stronger secret and credential handling guidance or integration points so deployments do not rely only on raw environment-variable wiring`
  - `Add startup validation for required auth, session, and outbound-integration settings when the production-ready posture is enabled`
- Out of scope:
  - changing the supported auth model away from session-backed OAuth
  - re-enabling CSRF or changing the frozen `1.x` reviewer-oriented write-flow posture
  - adding a full external secret manager integration
  - changing public endpoint shapes or protected-route coverage

## Current State
- Session security is still configured centrally in [SecurityConfiguration.java](D:\Projects\Jit\technical-interview-demo\src\main\java\team\jit\technicalinterviewdemo\technical\security\SecurityConfiguration.java), with `SessionCreationPolicy.IF_REQUIRED` and session-fixation migration but no explicit concurrent-session policy.
- Shared session defaults live in [application.properties](D:\Projects\Jit\technical-interview-demo\src\main\resources\application.properties), including a `30m` timeout, cookie name, `HttpOnly=true`, and `SameSite=lax`.
- The prod profile currently adds only secure-cookie and datasource requirements in [application-prod.properties](D:\Projects\Jit\technical-interview-demo\src\main\resources\application-prod.properties).
- Fail-fast prod validation currently covers only required database variables in [ProductionConfigurationTests.java](D:\Projects\Jit\technical-interview-demo\src\test\java\team\jit\technicalinterviewdemo\technical\ProductionConfigurationTests.java).
- Human-facing setup and contract docs already describe optional OAuth, `ADMIN_LOGINS`, and `SESSION_COOKIE_SECURE` in [README.md](D:\Projects\Jit\technical-interview-demo\README.md) and [SETUP.md](D:\Projects\Jit\technical-interview-demo\SETUP.md), but they still rely mostly on raw env-var narrative rather than a tighter validated production contract.

## Requirement Gaps And Open Questions
- Exact concurrent-session policy is not specified in the roadmap text.
  - Why it matters: it changes auth behavior, support expectations, and likely test coverage.
  - Fallback if the user does not answer: use a single active session per authenticated login in the production-ready posture, with explicit rejection semantics documented and tested.
- Exact prod-only auth-setting validation scope is not fully specified.
  - Why it matters: optional OAuth and admin bootstrap should not become unconditional startup requirements.
  - Fallback if the user does not answer: require GitHub OAuth client settings only when the `oauth` profile is active, always validate database and session settings in `prod`, and validate `ADMIN_LOGINS` format only when it is present.
- The roadmap asks for stronger secret/credential handling guidance or integration points, but not for a specific secret backend.
  - Why it matters: choosing Vault, cloud secret stores, or a file-mounted secret contract would materially change implementation size.
  - Fallback if the user does not answer: stay repo-owned and deployment-sized by formalizing typed configuration, fail-fast validation, and Kubernetes/Helm secret contracts without introducing a new secret platform.

## Locked Decisions And Assumptions
- Preserve the current supported model: optional GitHub OAuth login, JDBC-backed sessions, and the existing public-versus-protected endpoint contract.
- Keep the implementation demo-sized and avoid distributed-session or secret-management infrastructure that is not already part of the repository.
- Treat session-policy work as production-profile behavior and documentation hardening, not as a public API redesign.
- Treat `README.md`, `SETUP.md`, and the technical overview contract as affected documentation because `/` already exposes runtime configuration details.

## Affected Artifacts
- Tests:
  - [ProductionConfigurationTests.java](D:\Projects\Jit\technical-interview-demo\src\test\java\team\jit\technicalinterviewdemo\technical\ProductionConfigurationTests.java)
  - [SecurityIntegrationTests.java](D:\Projects\Jit\technical-interview-demo\src\test\java\team\jit\technicalinterviewdemo\technical\security\SecurityIntegrationTests.java)
  - [TechnicalOverviewControllerIntegrationTests.java](D:\Projects\Jit\technical-interview-demo\src\test\java\team\jit\technicalinterviewdemo\technical\info\TechnicalOverviewControllerIntegrationTests.java)
- Docs:
  - [README.md](D:\Projects\Jit\technical-interview-demo\README.md)
  - [SETUP.md](D:\Projects\Jit\technical-interview-demo\SETUP.md)
  - [index.adoc](D:\Projects\Jit\technical-interview-demo\src\docs\asciidoc\index.adoc)
- Source files:
  - [SecurityConfiguration.java](D:\Projects\Jit\technical-interview-demo\src\main\java\team\jit\technicalinterviewdemo\technical\security\SecurityConfiguration.java)
  - [TechnicalOverviewService.java](D:\Projects\Jit\technical-interview-demo\src\main\java\team\jit\technicalinterviewdemo\technical\info\TechnicalOverviewService.java)
  - [application.properties](D:\Projects\Jit\technical-interview-demo\src\main\resources\application.properties)
  - [application-prod.properties](D:\Projects\Jit\technical-interview-demo\src\main\resources\application-prod.properties)
  - [application-oauth.properties](D:\Projects\Jit\technical-interview-demo\src\main\resources\application-oauth.properties)
  - Kubernetes and Helm secret/config surfaces if startup validation or guidance changes:
    - [deployment.yaml](D:\Projects\Jit\technical-interview-demo\k8s\base\deployment.yaml)
    - [values.yaml](D:\Projects\Jit\technical-interview-demo\helm\technical-interview-demo\values.yaml)
- Build or benchmark checks:
  - `.\gradlew.bat build`
  - `.\gradlew.bat gatlingBenchmark` because the roadmap scope can affect OAuth/session startup behavior

## Execution Milestones
### Milestone 1: Lock The Production Session Contract
- Goal: define the exact prod-ready session behavior without changing the repository's supported auth model.
- Files to update:
  - [SecurityConfiguration.java](D:\Projects\Jit\technical-interview-demo\src\main\java\team\jit\technicalinterviewdemo\technical\security\SecurityConfiguration.java)
  - [application.properties](D:\Projects\Jit\technical-interview-demo\src\main\resources\application.properties)
  - [application-prod.properties](D:\Projects\Jit\technical-interview-demo\src\main\resources\application-prod.properties)
  - [TechnicalOverviewService.java](D:\Projects\Jit\technical-interview-demo\src\main\java\team\jit\technicalinterviewdemo\technical\info\TechnicalOverviewService.java)
- Behavior to preserve:
  - optional GitHub OAuth profile
  - current protected-route coverage
  - CSRF-disabled reviewer posture for `1.x`
- Exact deliverables:
  - explicit prod session timeout/cookie policy
  - explicit concurrent-session policy
  - technical overview updates only if configuration visibility must change to stay honest

### Milestone 2: Add Typed Startup Validation For Auth And Session Settings
- Goal: fail fast when required prod auth/session/outbound settings are invalid or missing.
- Files to update:
  - likely new typed configuration classes under `technical.security` or `technical.config`
  - [ProductionConfigurationTests.java](D:\Projects\Jit\technical-interview-demo\src\test\java\team\jit\technicalinterviewdemo\technical\ProductionConfigurationTests.java)
  - [SecurityIntegrationTests.java](D:\Projects\Jit\technical-interview-demo\src\test\java\team\jit\technicalinterviewdemo\technical\security\SecurityIntegrationTests.java)
- Exact deliverables:
  - validated production configuration binding
  - focused tests for missing/invalid session and OAuth settings
  - explicit conditional validation rules for optional OAuth and optional admin bootstrap

### Milestone 3: Align Deployment Guidance And Secret Handling Narrative
- Goal: replace loose env-var guidance with a tighter deployment contract.
- Files to update:
  - [README.md](D:\Projects\Jit\technical-interview-demo\README.md)
  - [SETUP.md](D:\Projects\Jit\technical-interview-demo\SETUP.md)
  - [deployment.yaml](D:\Projects\Jit\technical-interview-demo\k8s\base\deployment.yaml)
  - [values.yaml](D:\Projects\Jit\technical-interview-demo\helm\technical-interview-demo\values.yaml)
- Exact deliverables:
  - documented secret/config expectations for prod deployments
  - clarified rules for `SESSION_COOKIE_SECURE`, GitHub credentials, and `ADMIN_LOGINS`
  - aligned deployment examples that match the validated startup contract

## Edge Cases And Failure Modes
- OAuth settings must not become required when the `oauth` profile is not active.
- Concurrency controls must not break the persisted-user synchronization flow or JDBC session storage.
- Session-policy changes can affect benchmark startup behavior and packaged smoke assumptions.
- Tightened validation must not make the local profile harder to run or silently alter test-profile defaults.
- Changes to `/` runtime metadata are contract-visible and need spec alignment if field values or exposed semantics change.

## Validation Plan
- Commands to run:
  - `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.technical.ProductionConfigurationTests --tests team.jit.technicalinterviewdemo.technical.security.SecurityIntegrationTests --tests team.jit.technicalinterviewdemo.technical.info.TechnicalOverviewControllerIntegrationTests`
  - `.\gradlew.bat gatlingBenchmark` if execution changes OAuth/session startup behavior
  - `.\gradlew.bat build`
- Tests to add or update:
  - missing/invalid prod auth-session configuration coverage
  - concurrent-session or session-policy coverage if behavior changes
  - technical overview assertions if exposed configuration semantics change
- Docs or contract checks:
  - keep `README.md`, `SETUP.md`, and generated technical docs aligned
- Manual verification steps:
  - start `prod` with valid settings and confirm startup succeeds
  - start `prod` with targeted missing/invalid auth-session settings and confirm fail-fast errors are explicit

## Better Engineering Notes
- The smallest coherent improvement is typed binding plus explicit validation, not a new secret platform.
- If concurrent-session enforcement proves too invasive for the current OAuth/session model, the fallback should still document and validate the chosen expectation instead of leaving it implicit.
- If execution changes session startup or login bootstrap behavior materially, a narrower prerequisite refactor may be needed before final hardening.

## Validation Results
- 2026-05-03: Ran `.\gradlew.bat build --no-daemon` on `codex/unfinished-plans-integration` with Java 25 (`C:\Users\kamki\.jdks\azul-25.0.3`).
- Result: passed.
- Notes:
  - This covered the updated `ProductionConfigurationTests`, `SecurityIntegrationTests`, `TechnicalOverviewControllerIntegrationTests`, and the rest of the repository test suite.
  - The build also regenerated docs, verified OpenAPI compatibility, and rebuilt the Docker image with the integrated session-hardening changes.
- 2026-05-03: Ran `.\gradlew.bat externalSmokeTest -PexternalSmokeImageName=technical-interview-demo -PdockerImageName=technical-interview-demo --no-daemon`.
- Result: passed.
- Notes:
  - The packaged smoke flow proved the JDBC-backed authenticated-session path still works against the containerized app and PostgreSQL.
- 2026-05-03: Ran `.\gradlew.bat gatlingBenchmark --no-daemon` twice because the first run failed on a tolerance-boundary regression for `oauth2-github-redirect` (`15ms` p95 versus a `14ms` threshold).
- Result: second run passed without baseline changes; final recorded `oauth2-github-redirect` p95 was `14ms`.

## User Validation
- Run the app in `prod` once with a correct configuration set and once with one intentionally missing auth/session variable.
- Confirm the valid run still supports the current login and protected-session flow.
- Confirm the invalid run fails early with a message that points at the actual missing or invalid setting.
