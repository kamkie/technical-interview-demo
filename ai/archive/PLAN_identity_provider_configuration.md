# Plan: Identity Provider Configuration

## Summary
- Complete the checked identity-hardening roadmap item by moving the app beyond a single optional GitHub OAuth provider toward an explicit provider and issuer configuration model that still fits the current session-backed sample-app posture.
- Keep the current browser-session and Spring Session JDBC flow intact while making provider selection, configuration, and startup validation explicit and production-reviewable.
- Success is measured by: typed provider configuration, fail-fast validation for invalid provider setups, aligned login/docs/runtime metadata, and passing session-sensitive verification.

## Scope
- In scope:
  - `Move beyond the single optional GitHub OAuth provider toward a production-ready identity story suitable for a sample app, including explicit issuer/provider configuration`
- Out of scope:
  - changing the unchecked post-`1.x` client-direction roadmap items
  - replacing browser-session auth with bearer tokens or JWTs
  - re-enabling CSRF for browser writes
  - introducing a hosted IAM platform dependency or a full UI login portal

## Current State
- `application-oauth.properties` defines only a single `github` client registration and mirrors those values into `app.security.oauth.github.*`.
- `SecurityConfiguration` hard-codes `/oauth2/authorization/github` as the login page when any client registration is present.
- `TechnicalOverviewService`, `README.md`, `src/docs/asciidoc/index.adoc`, and `src/test/resources/http/authentication.http` all describe GitHub as the only supported interactive login bootstrap.
- `ProductionSecurityConfigurationValidator` validates GitHub credentials only when the `oauth` profile is active.
- `SecurityIntegrationTests` and `ProductionConfigurationTests` already cover the JDBC session model and prod validation posture, which means this work should extend the existing contract rather than redesign it.

## Requirement Gaps And Open Questions
- The roadmap asks for a production-ready identity story but does not name the non-GitHub providers that must be first-class.
  - Why it matters: a GitHub-plus-generic-OIDC design is materially smaller than shipping multiple bespoke providers.
  - Fallback if the user does not answer: keep GitHub as one optional provider and add typed issuer-driven OIDC support for one or more named providers so the app can be configured explicitly without another code redesign.
- The roadmap does not say whether the supported bootstrap path must remain a single fixed URL.
  - Why it matters: multiple providers may require a chooser page or documented per-provider `/oauth2/authorization/{registrationId}` entry points.
  - Fallback if the user does not answer: keep Spring Security's per-registration authorization endpoints as the supported bootstrap surface and document a default-provider shortcut only if one provider is marked as default in config.
- The roadmap does not define whether provider metadata must be exposed on the public `/` overview endpoint.
  - Why it matters: `/` is part of the stable supported contract, so any new identity metadata there is a public contract addition.
  - Fallback if the user does not answer: keep `/` honest but minimal by exposing only whether OAuth is active plus the configured interactive bootstrap path(s), not raw issuer/client details.

## Locked Decisions And Assumptions
- Preserve the current supported auth model: browser-oriented OAuth login plus a JDBC-backed session cookie for protected API use.
- Treat this as security/configuration work, not as a public API redesign.
- Keep optional OAuth optional; plain `prod` without the `oauth` profile must still start without identity-provider credentials.
- Prefer Spring-native provider registration and issuer validation over custom login abstractions.
- If a default provider is retained for convenience, it must be derived from typed configuration rather than hard-coded to GitHub.

## Affected Artifacts
- Tests:
  - `src/test/java/team/jit/technicalinterviewdemo/technical/security/SecurityIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/ProductionConfigurationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewControllerIntegrationTests.java`
- Docs:
  - `README.md`
  - `SETUP.md`
  - `src/docs/asciidoc/index.adoc`
- OpenAPI and HTTP examples:
  - `src/test/resources/http/authentication.http`
  - OpenAPI baseline refresh is not expected unless the supported API contract itself changes
- Source files:
  - `src/main/resources/application-oauth.properties`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecurityConfiguration.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecuritySettingsProperties.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/ProductionSecurityConfigurationValidator.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewService.java`
  - possibly new typed OAuth/provider properties under `technical.security`
- Build or benchmark checks:
  - `.\gradlew.bat build`
  - `.\gradlew.bat gatlingBenchmark` because OAuth/session startup behavior is in scope

## Execution Milestones
### Milestone 1: Define The Provider Configuration Contract
- Goal:
  - replace the single hard-coded GitHub assumption with typed provider configuration that makes issuer/provider intent explicit.
- Files to update:
  - `src/main/resources/application-oauth.properties`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecuritySettingsProperties.java`
  - possibly new provider configuration classes under `technical.security`
- Behavior to preserve:
  - optional `oauth` profile activation
  - session-backed protected API behavior
- Exact deliverables:
  - one repo-owned provider configuration model
  - explicit rules for GitHub versus issuer-driven OIDC provider configuration
  - documented default-provider behavior, if any

### Milestone 2: Wire Security To The Configured Providers
- Goal:
  - make the login bootstrap and security flow derive from configured providers instead of a GitHub-only code path.
- Files to update:
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecurityConfiguration.java`
  - any supporting security/provider wiring classes introduced in Milestone 1
  - `src/main/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewService.java` only if the supported runtime overview must stay aligned
- Exact deliverables:
  - provider-aware login bootstrap behavior
  - preserved authenticated-session flow for `/api/account`, `/api/audit-logs`, and write endpoints
  - no hard-coded GitHub-only login page assumption in security wiring

### Milestone 3: Tighten Provider Validation And Contract Docs
- Goal:
  - fail fast on invalid provider configuration and align human-facing/runtime-facing docs.
- Files to update:
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/ProductionSecurityConfigurationValidator.java`
  - `README.md`
  - `SETUP.md`
  - `src/docs/asciidoc/index.adoc`
  - `src/test/resources/http/authentication.http`
  - targeted tests under `technical.security` and `technical.info`
- Exact deliverables:
  - explicit validation for required provider settings under the chosen config model
  - updated setup and contract docs for the supported login entry points
  - test coverage for valid and invalid provider setups

## Edge Cases And Failure Modes
- Provider validation must not make OAuth settings mandatory when the `oauth` profile is not active.
- GitHub support must not regress while adding a generic provider path.
- If multiple providers are enabled, the login bootstrap contract must be unambiguous and documented.
- Any change to `/` runtime metadata is contract-visible and requires spec alignment.
- Session-startup changes can affect the existing Gatling baseline; benchmark regressions should be treated as real contract pressure, not cleanup.

## Validation Plan
- Commands to run:
  - `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.technical.ProductionConfigurationTests --tests team.jit.technicalinterviewdemo.technical.security.SecurityIntegrationTests --tests team.jit.technicalinterviewdemo.technical.info.TechnicalOverviewControllerIntegrationTests`
  - `.\gradlew.bat gatlingBenchmark`
  - `.\gradlew.bat build`
- Tests to add or update:
  - prod-validation coverage for valid and invalid provider configurations
  - security-flow coverage for the configured login bootstrap behavior
  - technical overview assertions if exposed auth metadata changes
- Docs or contract checks:
  - keep `README.md`, `SETUP.md`, `index.adoc`, and `authentication.http` aligned
  - do not refresh the OpenAPI baseline unless execution intentionally changes the supported API contract
- Manual verification steps:
  - start the app with one configured provider and confirm the expected login bootstrap path works
  - start the app with an invalid provider setup and confirm startup fails with a targeted configuration error

## Better Engineering Notes
- The smallest coherent improvement is typed provider configuration plus explicit validation, not a full identity-platform abstraction.
- If the unresolved broader auth-direction roadmap item later chooses stateless tokens, that should be planned separately instead of being smuggled into this work.
- Prefer a configuration model that keeps GitHub working while making standards-based OIDC growth possible without another hard-coded branch.

## Validation Results
- `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.technical.ProductionConfigurationTests --tests team.jit.technicalinterviewdemo.technical.security.SecurityIntegrationTests --tests team.jit.technicalinterviewdemo.technical.info.TechnicalOverviewControllerIntegrationTests` (PASS)
  - `17` tests passed.
- `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.technical.docs.OpenApiCompatibilityIntegrationTests --tests team.jit.technicalinterviewdemo.technical.docs.OpenApiIntegrationTests --tests team.jit.technicalinterviewdemo.technical.docs.ApiDocumentationTests` (PASS)
  - `23` tests passed.
- `.\gradlew.bat gatlingBenchmark` (FAIL)
  - benchmark regression gate failed:
    - `list-books` p95 `21ms` > baseline `20ms`
    - `search-books` p95 `21ms` > baseline `19ms`
    - `oauth2-github-redirect` p95 `15ms` > baseline `14ms`
- `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.technical.security.OAuthProviderConfigurationTests` (PASS)
  - `12` tests passed; added to recover JaCoCo thresholds after new provider-configuration branches.
- `.\gradlew.bat build` (PASS)
  - full build succeeded after additional coverage tests (`JaCoCo line 90.9%`, branch gate satisfied).

## User Validation
- Configure the app once with the existing GitHub-style flow and once with the new explicit provider contract.
- Confirm the login bootstrap surface matches the docs in both cases.
- Confirm invalid provider settings fail before the app starts serving traffic.
