# Plan: Same-Site UI Session Contract

## Summary
- Define the post-`1.x` backend contract for a separate first-party UI that shares one public origin with this backend through reverse-proxy deployment.
- Keep the current OAuth login bootstrap plus Spring Session JDBC model, but turn the scattered reviewer-oriented auth story into one explicit browser session contract the UI can depend on.
- Success is measured by: one documented same-site session/bootstrap surface, preserved `/api/account` behavior for authenticated profile access, aligned REST Docs/OpenAPI/HTTP examples, and verification that session-sensitive behavior still passes the required smoke and benchmark gates.

## Scope
- In scope:
  - the roadmap slice under `ROADMAP.md` `#### Define The Backend Contract For The Separate First-Party UI`
  - one additive supported API surface for same-site browser session bootstrap and session state
  - one explicit logout contract for the first-party UI
  - documenting same-site cookie, login bootstrap, reverse-proxy, and public-origin assumptions for the separate UI
  - aligning README, REST Docs, HTTP examples, and approved OpenAPI with the chosen session contract
- Out of scope:
  - cross-origin browser support or general CORS enablement
  - replacing session-backed auth with bearer tokens or JWTs
  - hosting or building the first-party UI inside this repository
  - full production-grade CSRF enforcement, security headers, forwarded-header hardening, or rate limiting
  - changing category/localization/audit authorization rules or the operator surface

## Current State
- The supported protected-app contract is still reviewer-oriented: interactive login starts at `/oauth2/authorization/{registrationId}` when the optional `oauth` profile is active, and protected API requests use the `technical-interview-demo-session` cookie.
- `business.user.UserAccountController` exposes only `GET /api/account` and `PUT /api/account/language`, both authenticated. There is no dedicated supported endpoint for the UI to discover session state, login bootstrap metadata, or logout behavior without relying on `401` handling.
- `technical.security.SecurityConfiguration` disables CSRF globally, permits `/oauth2/**` and `/login/**`, and relies on the session cookie scheme for protected operations.
- `README.md`, `src/docs/asciidoc/index.adoc`, `src/test/resources/http/authentication.http`, and `technical.docs.OpenApiConfiguration` all describe the login-plus-session-cookie model, but the contract is spread across those artifacts instead of being expressed as a deliberate browser-facing API surface.
- The runtime already assumes same-site browser defaults more than cross-origin ones: `server.servlet.session.cookie.same-site=lax`, prod validation allows only `lax` or `strict`, and there is no CORS configuration.
- External smoke verification and restore-drill guidance currently prove the session-backed story through `GET /api/account`, so that endpoint should remain stable even if a new additive session contract is introduced.

## Requirement Gaps And Open Questions
- No blocking user-input gap remains after the roadmap framing decisions. The remaining implementation choice is how to express the additive session contract without breaking the existing `/api/account` semantics.
  - Why it matters: changing `/api/account` from authenticated-only to anonymous-friendly would alter an existing supported contract, while additive session endpoints keep the current profile behavior stable.
  - Fallback assumption used by this plan: add a small `technical.security`-owned session surface at `GET /api/session` plus `POST /api/session/logout`, and keep `/api/account` as the authenticated persisted-profile endpoint.
- The roadmap item calls for CSRF expectations, but the separate `Revisit The Security Posture` roadmap block owns the actual move to production-grade browser protections.
  - Why it matters: trying to define the UI session contract and fully harden CSRF in one change would blur roadmap boundaries and make validation broader than necessary.
  - Fallback assumption used by this plan: make the current CSRF posture explicit in the new session contract and docs, but leave actual CSRF enablement and enforcement to the follow-up security-hardening plan.

## Locked Decisions And Assumptions
- The first-party UI remains in a separate repository.
- The browser sees one public origin via reverse proxy, so this plan targets a same-site browser contract only.
- Preserve the current auth model: provider-aware OAuth login bootstrap plus JDBC-backed Spring Session for protected API use.
- Preserve `/api/account` as an authenticated profile resource and do not repurpose it into an anonymous session-discovery endpoint.
- Expose the additive session/bootstrap contract under `technical.security`, not under `business.user`, because it is framework/security behavior rather than persisted-account business logic.
- Reuse the existing resolved login-path behavior from `SecuritySettingsProperties.OAuth`: the new session contract should surface the same effective bootstrap path, including `/login` when multiple providers are configured without a default.
- Full CSRF enforcement, proxy/header hardening, and rate limiting are explicitly deferred to the later roadmap items that already own them.

## Affected Artifacts
- Tests:
  - new `src/test/java/team/jit/technicalinterviewdemo/technical/security/SessionApiIntegrationTests.java`
  - new `src/test/java/team/jit/technicalinterviewdemo/technical/security/SessionApiDocumentationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/user/UserManagementIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/security/SecurityIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/docs/OpenApiIntegrationTests.java`
  - `src/externalTest/java/team/jit/technicalinterviewdemo/external/ExternalSmokeTests.java` if packaged smoke coverage is intentionally expanded
- Docs:
  - `README.md`
  - `src/docs/asciidoc/index.adoc`
  - new `src/docs/asciidoc/session-controller.adoc`
- OpenAPI and HTTP examples:
  - `src/test/resources/openapi/approved-openapi.json`
  - `src/test/resources/http/authentication.http`
- Source files:
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecurityConfiguration.java`
  - new `src/main/java/team/jit/technicalinterviewdemo/technical/security/SessionController.java`
  - new `src/main/java/team/jit/technicalinterviewdemo/technical/security/SessionService.java`
  - new `src/main/java/team/jit/technicalinterviewdemo/technical/security/SessionResponse.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/docs/OpenApiConfiguration.java`
  - optionally `src/main/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewService.java` and `TechnicalOverviewResponse.java` if the overview endpoint must reference the new supported session surface explicitly
- Build or benchmark checks:
  - `.\gradlew.bat gatlingBenchmark`
  - `.\gradlew.bat externalSmokeTest` if the packaged smoke assertions are expanded or changed
  - `.\gradlew.bat build`

## Execution Milestones
### Milestone 1: Lock The Session Contract In Specs
- Goal:
  - define the additive same-site browser session contract before implementation.
- Files to update:
  - new `technical/security` integration and REST Docs test classes
  - `src/test/java/team/jit/technicalinterviewdemo/technical/docs/OpenApiIntegrationTests.java`
  - `src/test/resources/http/authentication.http`
- Behavior to preserve:
  - unauthenticated `GET /api/account` still returns the existing localized `401` `ProblemDetail`
  - authenticated `GET /api/account` and `PUT /api/account/language` remain supported and unchanged
  - login bootstrap still resolves through `/oauth2/authorization/{registrationId}` or `/login` according to existing provider settings
- Exact deliverables:
  - spec coverage for `GET /api/session` as the supported same-site session/bootstrap endpoint
  - spec coverage for `POST /api/session/logout` as the supported logout contract
  - explicit payload semantics for authenticated versus unauthenticated session state, effective login path, cookie metadata safe to expose, and current CSRF mode

### Milestone 2: Implement The Additive Session Surface
- Goal:
  - add the new browser-facing session surface without regressing the existing account and write-protection contract.
- Files to update:
  - new `SessionController`, `SessionService`, and `SessionResponse`
  - `SecurityConfiguration`
  - `OpenApiConfiguration`
  - any supporting types needed to invalidate the session and emit the session-state payload
- Behavior to preserve:
  - `/api/account` stays authenticated-only
  - existing protected write endpoints keep using the session cookie security scheme
  - existing OAuth provider resolution and optional `oauth` profile behavior remain intact
- Exact deliverables:
  - public `GET /api/session` that returns supported session/bootstrap metadata without leaking secrets
  - explicit `POST /api/session/logout` behavior that invalidates the current session and is safe for same-site UI usage
  - no new general-purpose cross-origin API surface or bearer-token support

### Milestone 3: Align Published Contract And Smoke Story
- Goal:
  - make the new session contract reviewer-visible and release-visible everywhere the public contract is published.
- Files to update:
  - `README.md`
  - `src/docs/asciidoc/index.adoc`
  - new `src/docs/asciidoc/session-controller.adoc`
  - `src/test/resources/http/authentication.http`
  - `src/test/resources/openapi/approved-openapi.json`
  - optionally `src/externalTest/java/team/jit/technicalinterviewdemo/external/ExternalSmokeTests.java`
- Behavior to preserve:
  - the packaged smoke path through `/api/account` remains valid unless the smoke suite is intentionally expanded
  - docs keep the separate-repo, same-site, reverse-proxy story explicit and do not drift into cross-origin promises
- Exact deliverables:
  - updated human-facing contract text for the separate same-site UI
  - REST Docs page coverage for the session endpoints
  - OpenAPI and HTTP examples aligned with the new supported auth/session surface

## Edge Cases And Failure Modes
- When the `oauth` profile is inactive, `GET /api/session` must stay honest about the lack of an interactive login bootstrap instead of inventing a path that will not work.
- When multiple providers are configured without a default shortcut, the session contract must surface the existing `/login` chooser behavior rather than assuming one provider-specific path.
- The new public session endpoint must not expose client secrets, issuer URIs, raw admin configuration, or other sensitive deployment details.
- `POST /api/session/logout` must invalidate the JDBC-backed session cleanly without deleting persisted `UserAccount` data or breaking concurrent-session enforcement.
- The additive contract must not weaken existing `401`/`403` responses on protected endpoints or change the localized `ProblemDetail` shape.
- Because the first-party UI is same-site only, the contract and docs must not accidentally imply supported cross-origin browser calls or CORS guarantees.
- If execution changes OAuth/session startup behavior or packaged smoke assertions, benchmark and external smoke failures must be treated as spec failures, not optional cleanup.

## Validation Plan
- Commands to run:
  - `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.technical.security.SessionApiIntegrationTests --tests team.jit.technicalinterviewdemo.technical.security.SessionApiDocumentationTests --tests team.jit.technicalinterviewdemo.business.user.UserManagementIntegrationTests --tests team.jit.technicalinterviewdemo.technical.security.SecurityIntegrationTests --tests team.jit.technicalinterviewdemo.technical.docs.OpenApiIntegrationTests`
  - `.\gradlew.bat refreshOpenApiBaseline`
  - `.\gradlew.bat gatlingBenchmark`
  - `.\gradlew.bat externalSmokeTest` if the session contract is added to the packaged smoke story
  - `.\gradlew.bat build`
- Tests to add or update:
  - authenticated and anonymous integration coverage for `GET /api/session`
  - logout coverage proving the session is invalidated and `/api/account` returns `401` afterward
  - REST Docs coverage for the new session endpoints
  - OpenAPI assertions for the new paths, tags, security, and response schemas
- Docs or contract checks:
  - review the public API addition intentionally before refreshing `approved-openapi.json`
  - keep `README.md`, `index.adoc`, `authentication.http`, and the new session REST Docs page aligned on same-site versus cross-origin assumptions
- Manual verification steps:
  - start the app with `oauth` active and confirm `GET /api/session` reports the effective login bootstrap path
  - authenticate through a real browser login flow, then confirm `GET /api/session` and `GET /api/account` both reflect the active session correctly
  - call `POST /api/session/logout` and confirm the session is cleared and `GET /api/account` returns the existing localized `401`

## Better Engineering Notes
- The smaller coherent improvement is an additive explicit session contract, not a redesign of `/api/account` or a leap to token-based auth.
- Keep the new surface in `technical.security`; duplicating persisted-account fields or business logic into a generic auth layer would make the codebase harder to explain.
- A docs-only change would not satisfy this roadmap slice, because a first-party UI needs a deliberate supported session/bootstrap/logout contract rather than a collection of README notes and `401` behaviors.
- Do not smuggle the later security-hardening roadmap items into this plan. CSRF enforcement, security headers, forwarded-header hardening, and abuse protection should be planned and validated separately once this contract is locked.

## Validation Results
- To be filled in during execution

## User Validation
- Open the generated docs and confirm the supported UI-facing auth/session story is now explicit instead of inferred.
- Exercise the browser login flow, confirm `GET /api/session` reports the expected state before and after login, and confirm `GET /api/account` still behaves as the authenticated profile endpoint.
- Log out through the supported logout endpoint and confirm the session is gone without changing persisted user/account data.
