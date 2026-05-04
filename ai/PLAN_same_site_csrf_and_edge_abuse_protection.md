# Plan: Post-`1.x` Same-Site CSRF And Edge Abuse-Protection Posture

## Lifecycle
| Field | Value |
| --- | --- |
| Phase | Planning |
| Status | Needs Input |

## Summary
- Replace the current reviewer-oriented `csrf.enabled=false` browser-write posture with a production-grade same-site CSRF contract for the separate first-party UI behind one public origin.
- Define the repo-owned deployment contract for abuse protection so OAuth login bootstrap and internet-public write paths explicitly depend on edge or gateway controls instead of implied future in-app rate limiting.
- Success is measured by: executable specs for CSRF-protected writes, an updated `/api/session` bootstrap contract, aligned README/REST Docs/OpenAPI/HTTP examples, and deployment/post-release validation that no longer treats `csrfEnabled=false` as the documented production posture.

## Scope
- In scope:
  - enabling CSRF protection for unsafe browser-session operations under the supported `/api/**` surface
  - extending the same-site session bootstrap contract so the separate UI can discover and refresh the CSRF handshake it must use
  - preserving the current localized `401` versus `403` API contract where no real authenticated session exists
  - adding a dedicated localized CSRF failure contract instead of treating token failures as generic forbidden errors
  - updating internal runtime posture metadata, operator/runtime docs, external smoke checks, scheduled post-deploy checks, and restore-drill expectations from `csrfEnabled=false` to the new posture
  - documenting the required edge-owned abuse-protection capabilities for:
    - OAuth login bootstrap at `/api/session/oauth2/authorization/{registrationId}`
    - internet-public unsafe `/api/**` operations
  - aligning reviewer-facing HTTP examples with the new CSRF request requirements on write paths
- Out of scope:
  - adding repo-owned rate limiting, CAPTCHA, WAF logic, bot scoring, or gateway-specific enforcement code
  - changing the one-origin same-site deployment model or adding CORS support
  - moving the first-party UI into this repository
  - switching to bearer tokens or JWTs
  - broad authorization redesign outside the CSRF and abuse-protection slice
  - vendor-specific ingress or gateway manifests

## Current State
- Current behavior:
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecurityConfiguration.java` disables CSRF globally, so unsafe browser-session writes currently succeed with only the authenticated session cookie.
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SessionService.java` and `SessionResponse.java` publish `csrf.enabled=false` through `GET /api/session`.
  - `src/test/java/team/jit/technicalinterviewdemo/technical/security/SessionApiIntegrationTests.java`, `SessionApiOauthIntegrationTests.java`, and `SessionApiDocumentationTests.java` all lock the current session contract to `csrf.enabled=false` and no CSRF token metadata.
  - `src/test/resources/http/authentication.http`, `book-controller.http`, `category-controller.http`, `localization-controller.http`, and `user-account-controller.http` currently model unsafe requests with only the session cookie and no CSRF header.
  - `src/main/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewService.java` hard-codes `configuration.security.csrfEnabled=false`, and `TechnicalOverviewControllerIntegrationTests.java` plus `ApiDocumentationTests.java` assert that value.
  - `README.md`, `SETUP.md`, `src/docs/asciidoc/index.adoc`, and `src/docs/asciidoc/session-controller.adoc` all describe CSRF-disabled browser writes as the current same-site contract.
  - `src/externalTest/java/team/jit/technicalinterviewdemo/external/ExternalSmokeTests.java`, `.github/workflows/post-deploy-smoke.yml`, and `scripts/release/invoke-restore-drill.ps1` treat `csrfEnabled=false` as part of the documented production runtime posture.
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/ApiAccessDeniedHandler.java` maps all security access-denied cases to the generic `error.request.forbidden` localization contract; there is no CSRF-specific message key or detail.
  - `src/test/java/team/jit/technicalinterviewdemo/testing/SecurityTestSupport.java` provides authenticated request helpers, but there is no shared CSRF-aware test helper for the many current write-path integration and docs tests.
  - `README.md` and `SETUP.md` already say only `/api/**` is internet-reachable through `waf -> frontend -> this application`, but they do not yet define the required edge controls for login bootstrap and write-heavy routes.
- Current constraints:
  - the repository is intentionally small, so this slice should use Spring Security built-ins and a small amount of repo-owned glue rather than a new security framework inside the app
  - the separate UI remains same-site and single-origin, so the CSRF contract must work without CORS, bearer tokens, or server-rendered forms
  - the current API contract distinguishes `401 Unauthorized` from `403 Forbidden`, and this hardening slice should not casually collapse those two outcomes for missing-session writes
  - technical overview and operator surfaces are internal or devops-only, but they are already the repo-owned runtime posture source for smoke, release, and restore checks
  - the repo does not own ingress or WAF manifests, so abuse protection must be expressed as a deployment capability contract rather than fake in-repo enforcement
- Relevant existing specs and code:
  - roadmap source: `ROADMAP.md`
  - planning and artifact-routing guidance: `ai/PLAN.md`, `ai/DOCUMENTATION.md`, `ai/TESTING.md`
  - prior prerequisite slices: `ai/archive/PLAN_same_site_ui_session_contract.md`, `ai/archive/PLAN_post_1_x_proxy_boundary_and_headers.md`
  - session/security runtime code: `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecurityConfiguration.java`, `SessionController.java`, `SessionService.java`, `SessionResponse.java`, `ApiAccessDeniedHandler.java`
  - internal runtime posture: `src/main/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewService.java`, `TechnicalOverviewResponse.java`
  - internal operator surface: `src/main/java/team/jit/technicalinterviewdemo/technical/operator/OperatorSurfaceService.java`, `OperatorSurfaceResponse.java`
  - security and write-path specs: `src/test/java/team/jit/technicalinterviewdemo/technical/security/`, `src/test/java/team/jit/technicalinterviewdemo/business/book/BookApiIntegrationTests.java`, `business/category/CategoryApiIntegrationTests.java`, `business/localization/LocalizationApiIntegrationTests.java`, `business/user/UserManagementIntegrationTests.java`
  - generated docs and contract artifacts: `README.md`, `SETUP.md`, `src/docs/asciidoc/`, `src/test/resources/http/`, `src/test/resources/openapi/approved-openapi.json`
  - deployment/post-release validation: `src/externalTest/java/team/jit/technicalinterviewdemo/external/ExternalSmokeTests.java`, `.github/workflows/post-deploy-smoke.yml`, `scripts/release/invoke-restore-drill.ps1`

## Requirement Gaps And Open Questions
- CSRF token transport for the separate UI is a material design choice.
  - Why it matters: the UI is in a separate repository and cannot depend on server-rendered hidden form fields.
  - Fallback assumption used by this plan: adopt a same-site SPA-style CSRF contract with a dedicated readable CSRF cookie plus a required request header on unsafe browser writes. `GET /api/session` remains the canonical bootstrap path that publishes the header and cookie names and triggers token issuance or refresh.
- Naive CSRF enablement would change many current missing-session write failures from localized `401` into `403`.
  - Why it matters: the current API contract and reviewer docs intentionally distinguish missing authentication from forbidden access.
  - Blocking question: should `v2.0.0-M2` preserve the current localized `401` semantics for unsafe writes when there is no real current application session, or is it acceptable in this breaking phase to let those requests fail as CSRF-driven `403` responses?
  - Recommended option: preserve the current `401` semantics. It keeps the auth contract easier to reason about and limits the breaking surface to the new CSRF handshake itself.
- Edge-owned abuse protection can be documented too vaguely to be actionable.
  - Why it matters: "deployment-owned" is not enough unless the repo names both the protected path families and the minimum expected capabilities.
  - Fallback assumption used by this plan: document two required edge-protection groups:
    - login-bootstrap protection for `/api/session/oauth2/authorization/{registrationId}` with burst limiting plus challenge or block capability for suspicious clients
    - per-client throttling and rejection visibility for unsafe internet-public `/api/**` operations, including logout and the write paths under books, categories, localizations, and account-language update
- Logout CSRF semantics need an explicit contract choice.
  - Why it matters: `POST /api/session/logout` is currently public and idempotent, while a production-grade CSRF posture usually protects authenticated logout.
  - Blocking question: when an authenticated session exists, should logout require a valid CSRF token, or should logout remain fully exempt from CSRF to keep the current simpler client contract?
  - Recommended option: require CSRF when a current session exists, but keep no-session logout idempotent and `204` so the separate UI can still call it safely after local state drift.
- The breaking-change rollout label is now explicit.
  - Why it matters: the plan should not pretend this is preserving the frozen `1.x` browser-write contract.
  - User decision recorded here: treat this work as `v2.0.0-M2` planning, and assume breaking public-contract changes are allowed in this phase.

## Locked Decisions And Assumptions
- Treat the two checked roadmap items as one coherent plan because they both modify the same browser security contract, runtime posture reporting, and deployment guidance.
- This plan targets the breaking-change prerelease line `v2.0.0-M2`.
- Preserve the current architectural direction already locked by earlier roadmap decisions:
  - separate first-party UI repository
  - one public origin behind `waf -> frontend -> this application`
  - session-backed auth under `/api/session/**`
  - no cross-origin browser support and no bearer-token contract
- Adopt a same-site browser CSRF contract with:
  - `csrf.enabled=true`
  - a dedicated readable CSRF cookie for the browser UI
  - a required request header on unsafe browser writes
  - `GET /api/session` as the bootstrap and refresh endpoint that publishes the CSRF cookie and header names
- Do not expose the raw CSRF token in the JSON body. The UI contract is cookie plus header, not JSON token transport.
- Keep the authenticated session cookie contract intact. Protected API access still uses the Spring Session cookie; the change in this slice is the added CSRF handshake for unsafe browser writes.
- Unsafe request rules after this slice:
  - active-session unsafe writes without a valid CSRF token fail with localized `403 ProblemDetail`
  - requests without a real current app session continue using the existing localized auth contract instead of failing early with a generic CSRF error
  - safe `GET` endpoints remain CSRF-free
  - `POST /api/session/logout` requires CSRF only when a current app session exists, so no-session logout remains idempotent and safe
- Add a dedicated localized CSRF error key named `error.request.csrf_invalid` instead of reusing `error.request.forbidden`, so clients can distinguish role failures from anti-CSRF failures.
- Extend the existing internal technical overview posture instead of inventing a new operator-only runtime endpoint. The root overview remains the source of truth for release and restore validation.
- Keep the abuse-protection contract vendor-neutral. Document capabilities and protected path families, not a specific cloud, WAF, or gateway product.
- Required edge-owned abuse-protection capabilities for the documented deployment contract are:
  - login-bootstrap burst limiting plus challenge or block capability on `/api/session/oauth2/authorization/{registrationId}`
  - per-client throttling for unsafe internet-public `/api/**` operations
  - edge or gateway request-size enforcement and rejection visibility for write-heavy routes
- Do not add repo-owned rate limiting, CAPTCHA flows, or placeholder gateway configuration knobs that the application itself does not enforce.
- Reuse Spring Security test support and one repo-owned helper layer for CSRF-aware MockMvc requests rather than scattering raw token/header plumbing across feature tests.
- Because Spring Security's SPA-oriented CSRF flow clears the token on authentication and logout, the UI contract after login success and after logout is: call `GET /api/session` before the next unsafe write so the browser receives the current CSRF cookie again.
- Refresh `src/test/resources/openapi/approved-openapi.json` intentionally because the public session/bootstrap contract and documented unsafe-write requirements are changing as part of the post-`1.x` breaking track.

## Affected Artifacts
- Tests:
  - `src/test/java/team/jit/technicalinterviewdemo/technical/security/SessionApiIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/security/SessionApiOauthIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/security/SessionApiDocumentationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/security/SecurityIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/security/ApiSecurityErrorHandlerTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/testing/SecurityTestSupport.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/book/BookApiIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/category/CategoryApiIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/localization/LocalizationApiIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/user/UserManagementIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewControllerIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/operator/OperatorSurfaceApiIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/docs/ApiDocumentationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/category/CategoryApiDocumentationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/localization/LocalizationApiDocumentationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/operator/OperatorSurfaceApiDocumentationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/docs/OpenApiIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/docs/OpenApiCompatibilityIntegrationTests.java`
  - `src/externalTest/java/team/jit/technicalinterviewdemo/external/ExternalSmokeTests.java`
- Docs and examples:
  - `README.md`
  - `SETUP.md`
  - `src/docs/asciidoc/index.adoc`
  - `src/docs/asciidoc/session-controller.adoc`
  - `src/docs/asciidoc/book-controller.adoc`
  - `src/docs/asciidoc/category-controller.adoc`
  - `src/docs/asciidoc/localization-controller.adoc`
  - `src/docs/asciidoc/technical-overview-controller.adoc`
  - `src/docs/asciidoc/operator-surface-controller.adoc`
  - `src/test/resources/http/authentication.http`
  - `src/test/resources/http/book-controller.http`
  - `src/test/resources/http/category-controller.http`
  - `src/test/resources/http/localization-controller.http`
  - `src/test/resources/http/user-account-controller.http`
- OpenAPI and contract artifacts:
  - `src/main/java/team/jit/technicalinterviewdemo/technical/docs/OpenApiConfiguration.java`
  - `src/test/resources/openapi/approved-openapi.json`
- Source files:
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecurityConfiguration.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SessionController.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SessionService.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SessionResponse.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/ApiAccessDeniedHandler.java`
  - new focused `technical/security` helper type(s) for current-session detection and CSRF request matching
  - `src/main/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewService.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewResponse.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/localization/seed/LocalizationSeedData.java`
- Deployment and release posture checks:
  - `.github/workflows/post-deploy-smoke.yml`
  - `scripts/release/invoke-restore-drill.ps1`
- Build or benchmark checks:
  - `.\gradlew.bat refreshOpenApiBaseline`
  - `.\gradlew.bat gatlingBenchmark`
  - `.\gradlew.bat externalSmokeTest`
  - `.\gradlew.bat build`

## Execution Milestones
### Milestone 1: Lock The New Browser-Security Contract In Specs
- goal
  - define the post-`1.x` CSRF and abuse-protection contract in executable specs and published docs before runtime changes
- files to update
  - `src/test/java/team/jit/technicalinterviewdemo/technical/security/SessionApiIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/security/SessionApiOauthIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/security/SessionApiDocumentationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/security/ApiSecurityErrorHandlerTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/book/BookApiIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/category/CategoryApiIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/localization/LocalizationApiIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/user/UserManagementIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewControllerIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/operator/OperatorSurfaceApiIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/docs/ApiDocumentationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/category/CategoryApiDocumentationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/localization/LocalizationApiDocumentationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/operator/OperatorSurfaceApiDocumentationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/docs/OpenApiIntegrationTests.java`
  - `README.md`
  - `SETUP.md`
  - `src/docs/asciidoc/index.adoc`
  - `src/docs/asciidoc/session-controller.adoc`
  - `src/docs/asciidoc/book-controller.adoc`
  - `src/docs/asciidoc/category-controller.adoc`
  - `src/docs/asciidoc/localization-controller.adoc`
  - `src/docs/asciidoc/technical-overview-controller.adoc`
  - `src/docs/asciidoc/operator-surface-controller.adoc`
  - `src/test/resources/http/authentication.http`
  - `src/test/resources/http/book-controller.http`
  - `src/test/resources/http/category-controller.http`
  - `src/test/resources/http/localization-controller.http`
  - `src/test/resources/http/user-account-controller.http`
- behavior to preserve
  - same-site, one-origin browser contract
  - `/api/account` remains the authenticated persisted-profile endpoint
  - `GET /api/session` remains the supported session/bootstrap endpoint
  - login bootstrap stays under `/api/session/oauth2/authorization/{registrationId}`
  - no CORS or bearer-token semantics appear in docs or tests
- exact deliverables
  - spec coverage that `GET /api/session` reports `csrf.enabled=true` plus explicit CSRF header and cookie names
  - spec coverage that `GET /api/session` issues or refreshes the CSRF cookie for the browser contract
  - spec coverage that unsafe writes succeed with a valid CSRF token and fail with localized `403 ProblemDetail` when the token is missing or invalid for an active session
  - spec coverage that requests without a real current app session continue using the existing localized `401` or role-based `403` behavior rather than a generic CSRF failure
  - docs and examples for all unsafe browser writes updated to show the required CSRF request header
  - internal runtime posture docs updated from `csrfEnabled=false` to the new posture, including edge-owned abuse-protection ownership and required path families

### Milestone 2: Implement Runtime CSRF And Error-Handling Changes
- goal
  - add the smallest coherent runtime changes that enforce the same-site CSRF contract without weakening the existing auth model
- files to update
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecurityConfiguration.java`
  - new focused `technical/security` helper type(s) for current-session detection and CSRF request matching
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SessionController.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SessionService.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SessionResponse.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/ApiAccessDeniedHandler.java`
  - `src/test/java/team/jit/technicalinterviewdemo/testing/SecurityTestSupport.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/localization/seed/LocalizationSeedData.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewService.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewResponse.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/docs/OpenApiConfiguration.java`
- behavior to preserve
  - session-backed auth model and existing `/api/session/**` route family
  - current localized `ProblemDetail` shape
  - current session-cookie security posture, including HttpOnly session cookie handling
  - local and test profile usability over HTTP
- exact deliverables
  - CSRF enabled for unsafe browser-session writes
  - a dedicated readable CSRF cookie plus required request-header contract for unsafe writes
  - `GET /api/session` bootstraps and refreshes the CSRF contract without exposing the raw token in JSON
  - CSRF enforcement applied only when a real current app session exists, preserving current missing-session `401` behavior
  - dedicated localized CSRF failure mapping through `error.request.csrf_invalid`
  - shared test support for valid and invalid CSRF write requests
  - internal technical overview fields updated so release and restore validation can assert the new production posture

### Milestone 3: Align Published Contract And Deployment Validation
- goal
  - make the new browser-write contract and edge-owned abuse-protection expectations visible everywhere the repo publishes or validates runtime posture
- files to update
  - `README.md`
  - `SETUP.md`
  - `src/docs/asciidoc/index.adoc`
  - `src/docs/asciidoc/session-controller.adoc`
  - `src/docs/asciidoc/book-controller.adoc`
  - `src/docs/asciidoc/category-controller.adoc`
  - `src/docs/asciidoc/localization-controller.adoc`
  - `src/docs/asciidoc/technical-overview-controller.adoc`
  - `src/docs/asciidoc/operator-surface-controller.adoc`
  - `src/test/resources/http/authentication.http`
  - `src/test/resources/http/book-controller.http`
  - `src/test/resources/http/category-controller.http`
  - `src/test/resources/http/localization-controller.http`
  - `src/test/resources/http/user-account-controller.http`
  - `src/test/resources/openapi/approved-openapi.json`
  - `src/externalTest/java/team/jit/technicalinterviewdemo/external/ExternalSmokeTests.java`
  - `.github/workflows/post-deploy-smoke.yml`
  - `scripts/release/invoke-restore-drill.ps1`
- behavior to preserve
  - same-site browser story stays explicit and relative-path based
  - internal overview and operator surfaces remain internal or devops-only
  - abuse protection stays documented as deployment-owned rather than app-owned runtime enforcement
- exact deliverables
  - README and setup guidance updated from CSRF-disabled demo posture to CSRF-protected same-site browser posture
  - session, write-endpoint, and OpenAPI docs aligned on the required CSRF header for unsafe requests
  - post-deploy smoke and restore-drill checks no longer treat `csrfEnabled=false` as the required runtime posture
  - internal runtime posture docs and operator diagnostics clearly state that abuse protection for login bootstrap and unsafe internet-public writes is owned by edge or gateway controls
  - approved OpenAPI refreshed after intentional contract review

## Edge Cases And Failure Modes
- `GET /api/session` must stay honest when the `oauth` profile is inactive while still bootstrapping the CSRF contract for the same-site UI.
- CSRF token refresh after authentication and logout must be explicit; a stale pre-login token should not be documented as reusable after the browser session changes.
- Enabling CSRF must not silently turn current missing-session writes into generic `403` responses.
- Missing or invalid CSRF token failures must keep the repo's localized `ProblemDetail` shape and must not fall back to framework HTML or plain-text error output.
- `POST /api/session/logout` must remain safe and idempotent when no application session exists, while still requiring CSRF when a current session is present.
- The readable CSRF cookie must not weaken the existing session cookie posture. The session cookie stays HttpOnly; only the dedicated CSRF cookie is readable by the UI.
- Local and test flows must remain workable over HTTP even though production keeps secure cookie posture.
- Reviewer HTTP examples and OpenAPI descriptions must not imply cross-origin token fetches, CORS support, or bearer-token semantics.
- The new abuse-protection fields or docs must not pretend the application can verify real WAF or gateway enforcement; they describe required deployment capabilities, not in-app guarantees.
- External smoke, scheduled post-deploy checks, and restore-drill scripts must all move together so the repo does not keep stale `csrfEnabled=false` assertions after the contract changes.

## Validation Plan
- Commands to run during implementation:
  - `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.technical.security.SessionApiIntegrationTests --tests team.jit.technicalinterviewdemo.technical.security.SessionApiOauthIntegrationTests --tests team.jit.technicalinterviewdemo.technical.security.SessionApiDocumentationTests --tests team.jit.technicalinterviewdemo.technical.security.SecurityIntegrationTests --tests team.jit.technicalinterviewdemo.technical.security.ApiSecurityErrorHandlerTests --tests team.jit.technicalinterviewdemo.business.book.BookApiIntegrationTests --tests team.jit.technicalinterviewdemo.business.category.CategoryApiIntegrationTests --tests team.jit.technicalinterviewdemo.business.localization.LocalizationApiIntegrationTests --tests team.jit.technicalinterviewdemo.business.user.UserManagementIntegrationTests --tests team.jit.technicalinterviewdemo.technical.info.TechnicalOverviewControllerIntegrationTests --tests team.jit.technicalinterviewdemo.technical.operator.OperatorSurfaceApiIntegrationTests --tests team.jit.technicalinterviewdemo.technical.docs.ApiDocumentationTests --tests team.jit.technicalinterviewdemo.business.category.CategoryApiDocumentationTests --tests team.jit.technicalinterviewdemo.business.localization.LocalizationApiDocumentationTests --tests team.jit.technicalinterviewdemo.technical.operator.OperatorSurfaceApiDocumentationTests --tests team.jit.technicalinterviewdemo.technical.docs.OpenApiIntegrationTests --tests team.jit.technicalinterviewdemo.technical.docs.OpenApiCompatibilityIntegrationTests`
  - `.\gradlew.bat refreshOpenApiBaseline`
  - `.\gradlew.bat gatlingBenchmark`
  - `.\gradlew.bat externalSmokeTest`
  - `.\gradlew.bat build`
- Tests to add or update:
  - valid and invalid CSRF coverage for every unsafe supported browser write family
  - dedicated CSRF error-handler coverage for localized `403 ProblemDetail`
  - session-bootstrap coverage for CSRF metadata and cookie refresh
  - technical-overview and operator-surface coverage for the new runtime posture fields
  - external smoke coverage updated from `csrfEnabled=false` to the new contract
- Docs or contract checks:
  - review the public session contract intentionally before refreshing `approved-openapi.json`
  - confirm every unsafe reviewer HTTP example now shows the CSRF request header
  - confirm README, REST Docs, HTTP examples, and internal posture docs use the same abuse-protection ownership language
- Manual verification steps:
  - start the app with `oauth` active and call `GET /api/session`; confirm the response reports the CSRF cookie and header names and the browser receives the CSRF cookie
  - authenticate through the browser flow, call `GET /api/session` again, and confirm the UI has the refreshed CSRF cookie for the authenticated session
  - perform one unsafe write with a valid CSRF token and confirm success, then repeat without the token and confirm a localized `403`
  - call `POST /api/session/logout` with an active session and valid CSRF token and confirm `204`, then verify the session is gone and the next unsafe write requires a fresh bootstrap
  - inspect `GET /` and `GET /api/operator/surface` from the trusted internal path and confirm the new CSRF plus abuse-protection posture is visible there

## Better Engineering Notes
- Reuse Spring Security's SPA-oriented CSRF support instead of inventing a second custom token endpoint.
- Keep `GET /api/session` as the single browser bootstrap endpoint. Do not create a parallel `/api/csrf` contract unless implementation proves the current session endpoint cannot carry the responsibility.
- Use one small helper for "does this request currently have a real app session?" and reuse it for both CSRF matching and session-state reporting. Do not duplicate cookie and session-repository decoding logic.
- Treat abuse protection as a vendor-neutral capability contract. Adding fake app settings for rate limits the repo does not enforce would make the docs less honest, not more complete.
- This is not a hidden refactor. It is a public contract change because session bootstrap fields, write requirements, reviewer HTTP examples, OpenAPI, and deployment posture checks all move together.

## Validation Results
- To be filled in during execution.

## User Validation
- Open the generated session docs and confirm `GET /api/session` now describes a CSRF-enabled same-site browser contract instead of `csrf.enabled=false`.
- Verify one authenticated unsafe write succeeds only when the UI sends the documented CSRF header alongside the session cookie.
- Verify a missing or invalid CSRF token now returns a localized `403 ProblemDetail` that is distinct from the existing generic forbidden case.
- Check the internal `GET /` overview and `GET /api/operator/surface` outputs and confirm they now describe the new CSRF posture plus edge-owned abuse-protection ownership for login bootstrap and unsafe internet-public writes.
