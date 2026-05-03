# Plan: Post-`1.x` Reverse-Proxy Boundary And Header Hardening

## Summary
- Make the first post-`1.x` security slice the reverse-proxy and browser-boundary foundation rather than CSRF enablement.
- Turn the current documented same-site assumption into an explicit runtime and deployment contract where only `/api/**` is externally reachable through `waf -> frontend -> this application`, with proxy-aware request handling and a fixed production security-header baseline.
- Move `/`, `/hello`, `/docs`, `/v3/api-docs`, `/v3/api-docs.yaml`, and `/actuator/**` out of the external surface and into internal or devops-only access paths used to validate runtime health and published documentation inside the trusted environment.
- Success is measured by: explicit proxy/header behavior in executable specs, aligned README/REST Docs/HTTP examples/setup guidance, preserved same-site session contract behavior, and passing repository validation for the touched artifacts.

## Scope
- In scope:
  - defining the first executable post-`1.x` security slice as reverse-proxy, public-origin, cookie, redirect, and session-boundary hardening
  - adding proxy-aware runtime handling for production-style deployments behind `waf -> frontend -> this application`
  - adding a conservative production security-header baseline that applies to the app's HTTP responses without breaking the generated docs or same-site session flows
  - shrinking the externally reachable application surface to `/api/**` only
  - making `/`, `/hello`, `/docs`, `/v3/api-docs`, `/v3/api-docs.yaml`, and `/actuator/**` internal or devops-only surfaces
  - keeping technical surfaces deployment-scoped and privately reachable, while preserving the app-owned operator surface under `/api/operator/surface`
  - updating runtime posture metadata, docs, and checked-in deployment assets if they must reflect the hardened boundary contract
- Out of scope:
  - enabling CSRF protection for browser writes
  - adding CORS or cross-origin browser support
  - moving technical endpoints behind application authentication or creating a separate management port
  - adding repo-owned rate limiting or other abuse protection controls
  - moving the first-party UI into this repository
  - switching away from session-backed browser auth to bearer tokens or JWTs

## Current State
- Current behavior:
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecurityConfiguration.java` disables CSRF globally, permits the same-site session endpoints, and leaves `/`, `/hello`, `/docs`, `/v3/api-docs`, `/v3/api-docs.yaml`, and the actuator endpoints directly reachable because the external/internal boundary is currently looser than the target `waf -> frontend -> this application` model.
  - `src/main/resources/application.properties` exposes `health`, `info`, and `prometheus`, sets the session cookie to `SameSite=lax`, and does not define any forwarded-header strategy or browser security-header baseline.
  - `src/main/resources/application-prod.properties` makes the session cookie secure by default in `prod`, but still does not define reverse-proxy/header expectations.
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SessionService.java` already returns only relative paths (`/api/account`, resolved login path, `/api/session/logout`) for the separate first-party UI, which fits the one-public-origin design and avoids needing absolute external URLs today.
  - `src/docs/asciidoc/index.adoc`, `src/test/resources/http/authentication.http`, and `README.md` already say the supported browser contract assumes one public origin via reverse proxy and no CORS support, but they still describe non-`/api` technical and docs surfaces as part of the supported external contract.
  - `README.md`, `SETUP.md`, `src/docs/asciidoc/technical-endpoints.adoc`, and `src/test/resources/http/technical-endpoints.http` already frame `/actuator/prometheus` as trusted deployment scraping rather than internet-public contract, but they do not yet move the whole actuator surface and the reviewer-oriented overview/docs endpoints into an explicitly internal-only posture.
  - `src/main/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewService.java` reports session and CSRF posture but does not expose any proxy/header hardening posture that deployment checks could verify.
  - `k8s/base/deployment.yaml`, `k8s/base/configmap.yaml`, and the Helm chart wire database, profile, OAuth, admin, and cookie-secure settings, but they do not yet express reverse-proxy/header assumptions beyond the secure-cookie default.
- Current constraints:
  - the repository is intentionally demo-sized, so this slice should use direct Spring Boot and Spring Security configuration rather than ingress-controller-specific machinery or heavy new infrastructure
  - the browser contract is explicitly same-site and single-origin, so the implementation should preserve relative paths and avoid implying supported cross-origin browser access
  - the checked-in monitoring assets depend on private in-cluster scraping of `/actuator/prometheus`, so “restrict technical endpoints” must not become accidental application-auth gating for metrics
  - `/`, `/hello`, and `/docs` stay useful for devops validation inside the trusted environment even though they are no longer part of the intended external surface
  - the generated docs page under `/docs` remains an internal/devops surface, so any security-header baseline must not break the generated HTML or its assets
- Relevant existing specs and code:
  - roadmap source: `ROADMAP.md`
  - planning rules: `ai/PLAN.md`
  - design guidance: `ai/DESIGN.md`
  - artifact ownership and validation rules: `ai/DOCUMENTATION.md`, `ai/TESTING.md`
  - human-facing contract and deployment posture: `README.md`, `SETUP.md`
  - same-site session contract: `src/test/java/team/jit/technicalinterviewdemo/technical/security/SessionApiIntegrationTests.java`, `src/test/java/team/jit/technicalinterviewdemo/technical/security/SessionApiDocumentationTests.java`, `src/main/java/team/jit/technicalinterviewdemo/technical/security/SessionController.java`, `src/main/java/team/jit/technicalinterviewdemo/technical/security/SessionService.java`
  - security/runtime config: `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecurityConfiguration.java`, `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecuritySettingsProperties.java`, `src/main/resources/application.properties`, `src/main/resources/application-prod.properties`
  - technical overview and docs: `src/main/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewService.java`, `src/test/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewControllerIntegrationTests.java`, `src/test/java/team/jit/technicalinterviewdemo/technical/docs/ApiDocumentationTests.java`, `src/test/java/team/jit/technicalinterviewdemo/technical/docs/OpenApiIntegrationTests.java`, `src/docs/asciidoc/index.adoc`, `src/docs/asciidoc/technical-endpoints.adoc`
  - deployment and smoke assets: `src/externalTest/java/team/jit/technicalinterviewdemo/external/ExternalSmokeTests.java`, `k8s/base/`, `k8s/monitoring/servicemonitor.yaml`, `helm/technical-interview-demo/`

## Requirement Gaps And Open Questions
- No blocking user-input gap remains after the roadmap framing decisions.
- The roadmap lists CSRF posture before proxy/header assumptions, but repo truth indicates the boundary hardening must come first.
  - Why it matters: CSRF enablement depends on trusted scheme/origin/cookie behavior, while the current repo has no forwarded-header strategy, no browser security-header baseline, and no explicit deployment validation for those assumptions.
  - Fallback assumption used by this plan: keep CSRF disabled in this slice and treat reverse-proxy/header hardening as the prerequisite milestone for any later CSRF work.
- The roadmap asks to define public-origin assumptions, but the current supported browser contract uses relative paths rather than absolute external URLs.
  - Why it matters: inventing a canonical external base-URL property now would add config and rollout complexity that the current supported contract does not obviously need.
  - Fallback assumption used by this plan: enforce a same-site, one-public-origin contract through relative paths, forwarded-header handling, secure-cookie posture, and documentation/tests first; only add an explicit public-base-url property if implementation proves it is required for correct redirect or cookie behavior.
- The roadmap asks to restrict technical endpoints, but the user locked that as deployment-scoped private access rather than application auth.
- The user further narrowed the reachability model so only `/api/**` should be externally reachable, while `/`, `/hello`, `/docs`, and all actuator endpoints are internal/devops-only.
  - Why it matters: this is broader than “Prometheus is private” and changes the supported exposure story for the overview, docs, OpenAPI, and actuator surfaces.
  - Fallback assumption used by this plan: treat every non-`/api` HTTP surface as internal-only unless a later roadmap decision explicitly re-exposes it.
- The roadmap asks to restrict technical endpoints, but the user locked that as deployment-scoped private access rather than application auth.
  - Why it matters: moving Prometheus behind app auth would contradict the chosen framing and the existing ServiceMonitor-based scrape model.
  - Fallback assumption used by this plan: keep metrics scraping app-unauthenticated, but tighten the documented and deployment-owned boundary around it instead of changing the application auth model.

## Locked Decisions And Assumptions
- User decisions already locked in the roadmap:
  - post-`1.x` security hardening is an explicit breaking follow-up rather than additive `1.x` work
  - browser-oriented auth remains the direction, with the first-party UI in a separate repository
  - the backend and UI share one public origin behind a reverse proxy
  - only `/api/**` is externally reachable, through `waf -> frontend -> this application`
  - `/`, `/hello`, `/docs`, `/v3/api-docs`, `/v3/api-docs.yaml`, and `/actuator/**` are internal or devops-only surfaces
  - technical endpoints stay deployment-scoped and privately reachable rather than becoming an application-authenticated API
  - abuse protection stays edge or deployment owned and is not part of this first implementation slice
- Planning assumptions for this plan:
  - the first concrete security slice should be reverse-proxy/public-origin/session-boundary hardening plus conservative browser security headers
  - CSRF enablement is explicitly deferred to the next slice because it depends on the boundary choices this plan locks down
  - the supported same-site session surface at `/api/session` and `/api/session/logout` should stay relative-path-based and should not gain cross-origin semantics
  - `/api/**` is the only externally reachable path family in this slice; non-`/api` endpoints stay available only from trusted internal networks or devops validation paths
  - `/api/operator/surface` remains the authenticated application-owned operational surface reachable through the frontend path
  - `/`, `/hello`, `/docs`, `/v3/api-docs`, `/v3/api-docs.yaml`, and `/actuator/**` should be treated as internal-only and removed from the externally supported contract
  - proxy handling in `prod` should be locked to Spring's framework forwarded-header support (`server.forward-headers-strategy=framework`), trusting standard `Forwarded` and `X-Forwarded-*` headers from the repository's assumed reverse-proxy boundary; `local` and `test` should keep working without requiring forwarded headers
  - this slice should not introduce a canonical public-base-url property unless implementation proves the current relative-path session contract is insufficient
  - production startup should fail fast if deployment configuration explicitly contradicts the single-origin HTTPS proxy contract, specifically if `prod` disables secure session cookies or overrides forwarded-header handling away from the framework strategy
  - Prometheus scraping remains compatible with the checked-in `ServiceMonitor` assets; any restriction in this slice should be expressed as deployment contract, documentation, and checked-in environment expectations rather than application auth
  - the fixed header baseline for this slice is:
    - `X-Content-Type-Options: nosniff`
    - `Referrer-Policy: no-referrer`
    - `X-Frame-Options: DENY`
    - `Permissions-Policy: geolocation=(), microphone=(), camera=()`
    - `Strict-Transport-Security: max-age=31536000; includeSubDomains` on secure requests in `prod` only
  - that header baseline should apply to app responses consistently, including normal API responses, redirects, ProblemDetail errors, generated docs responses, OpenAPI endpoints, session endpoints, supported operational endpoints, and deployment-scoped Prometheus responses, so the documentation helpers can treat them as common response headers
  - use Spring Boot and Spring Security built-ins where possible instead of adding new libraries or vendor-specific deployment logic

## Affected Artifacts
- Tests:
  - new `src/test/java/team/jit/technicalinterviewdemo/technical/security/ReverseProxyBoundaryIntegrationTests.java`
  - new `src/test/java/team/jit/technicalinterviewdemo/technical/security/SecurityHeadersIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/security/SessionApiIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/security/SecurityIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewControllerIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/docs/ApiDocumentationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/security/SessionApiDocumentationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/testing/AbstractDocumentationIntegrationTest.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/docs/OpenApiIntegrationTests.java` if the overview schema or contract descriptions change
  - `src/externalTest/java/team/jit/technicalinterviewdemo/external/ExternalSmokeTests.java` if deployment posture metadata or response-header expectations become part of packaged-image verification
- Docs:
  - `README.md`
  - `SETUP.md`
  - `src/docs/asciidoc/index.adoc`
  - `src/docs/asciidoc/technical-overview-controller.adoc`
  - `src/docs/asciidoc/session-controller.adoc`
  - `src/docs/asciidoc/technical-endpoints.adoc`
  - `src/test/resources/http/authentication.http`
  - `src/test/resources/http/technical-endpoints.http`
- OpenAPI:
  - `src/test/resources/openapi/approved-openapi.json` only if the reviewed API contract or overview schema intentionally changes
- Source files:
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecurityConfiguration.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecuritySettingsProperties.java`
  - likely one or more new focused types under `src/main/java/team/jit/technicalinterviewdemo/technical/security/` for proxy/header policy or startup validation
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SessionService.java` if session posture metadata or cookie/header behavior needs alignment
  - `src/main/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewService.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewResponse.java` if new posture metadata is exposed
  - `src/main/resources/application.properties`
  - `src/main/resources/application-prod.properties`
- Deployment assets:
  - `k8s/base/configmap.yaml`
  - `k8s/base/deployment.yaml`
  - `helm/technical-interview-demo/templates/configmap.yaml`
  - `helm/technical-interview-demo/templates/deployment.yaml`
  - `helm/technical-interview-demo/values.yaml`
- Build or benchmark checks:
  - `.\gradlew.bat build`
  - `.\gradlew.bat externalSmokeTest -PexternalSmokeImageName=technical-interview-demo -PdockerImageName=technical-interview-demo` if runtime posture metadata or production-like defaults change
  - `helm lint helm/technical-interview-demo` and `helm template technical-interview-demo helm/technical-interview-demo -f helm/technical-interview-demo/values-local.yaml` if Helm assets change
  - no `gatlingBenchmark` rerun expected unless execution ends up changing OAuth/session startup behavior rather than only boundary/header posture

## Execution Milestones
### Milestone 1: Lock The Boundary Contract In Specs
- goal
  - define the post-`1.x` reverse-proxy/public-origin/session-boundary expectations in executable specs and docs before runtime changes
- files to update
  - new `src/test/java/team/jit/technicalinterviewdemo/technical/security/ReverseProxyBoundaryIntegrationTests.java`
  - new `src/test/java/team/jit/technicalinterviewdemo/technical/security/SecurityHeadersIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewControllerIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/docs/ApiDocumentationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/security/SessionApiDocumentationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/testing/AbstractDocumentationIntegrationTest.java`
  - `README.md`
  - `SETUP.md`
  - `src/docs/asciidoc/index.adoc`
  - `src/docs/asciidoc/technical-overview-controller.adoc`
  - `src/docs/asciidoc/session-controller.adoc`
  - `src/docs/asciidoc/technical-endpoints.adoc`
  - `src/test/resources/http/authentication.http`
  - `src/test/resources/http/technical-endpoints.http`
- behavior to preserve
  - `GET /api/session` and `POST /api/session/logout` stay public same-site endpoints with relative paths
  - `/api/account` remains authenticated-only
  - `/actuator/prometheus` stays scrape-compatible for trusted private deployment access
  - `/`, `/hello`, `/docs`, `/v3/api-docs`, `/v3/api-docs.yaml`, and `/actuator/**` remain usable from trusted internal or devops paths even though they are removed from the external surface
  - no new cross-origin browser support appears in docs or runtime
- exact deliverables
  - spec coverage that locks the proxy trust model to Spring framework forwarded-header handling in `prod` and confirms non-proxy local/test behavior still works
  - spec coverage for the fixed security-header baseline on `/`, `/docs`, `/api/session`, `/api/operator/surface`, `/actuator/health/readiness`, and one ProblemDetail error path, with the same common-header contract used by REST Docs
  - explicit documentation that the app expects one public origin behind a reverse proxy, exposes only `/api/**` externally, keeps relative paths for UI bootstrap/logout, and uses internal/devops paths for overview, docs, OpenAPI, and actuator validation
  - updated contract language showing that this slice is prerequisite posture-hardening, not CSRF enablement

### Milestone 2: Implement Proxy-Aware Runtime And Header Baseline
- goal
  - add the smallest coherent runtime changes that make the proxy/public-origin/session assumptions enforceable
- files to update
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecurityConfiguration.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecuritySettingsProperties.java`
  - new focused `technical/security` support classes as needed
  - `src/main/resources/application.properties`
  - `src/main/resources/application-prod.properties`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SessionService.java` if required
  - `src/main/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewService.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewResponse.java` if required
- behavior to preserve
  - same-site browser session contract stays relative-path-based
  - CSRF remains disabled in this slice
  - Prometheus scraping remains compatible with private cluster scraping
  - local development and tests stay usable without forcing HTTPS-only behavior into the `local` and `test` paths
- exact deliverables
  - proxy-aware request/header handling for production-style deployments using `server.forward-headers-strategy=framework` in `prod`
  - conservative security headers applied to all app responses in the fixed baseline without breaking `/docs`
  - security rules and documentation posture aligned so only `/api/**` is externally reachable while internal/devops surfaces remain available behind the trusted boundary
  - explicit fail-fast validation that rejects `prod` configurations which disable secure session cookies or override forwarded-header handling away from the framework strategy
  - runtime posture metadata updated if deployment validation needs to assert the new contract

### Milestone 3: Align Deployment Assets And Published Contract
- goal
  - ensure the checked-in deployment path, generated docs, and reviewer examples all describe the same hardened boundary
- files to update
  - `k8s/base/configmap.yaml`
  - `k8s/base/deployment.yaml`
  - `helm/technical-interview-demo/values.yaml`
  - `helm/technical-interview-demo/templates/configmap.yaml`
  - `helm/technical-interview-demo/templates/deployment.yaml`
  - `README.md`
  - `SETUP.md`
  - `src/docs/asciidoc/index.adoc`
  - `src/docs/asciidoc/session-controller.adoc`
  - `src/docs/asciidoc/technical-endpoints.adoc`
  - `src/test/resources/http/authentication.http`
  - `src/test/resources/http/technical-endpoints.http`
  - `src/test/resources/openapi/approved-openapi.json` only if the contract changed intentionally
  - `src/externalTest/java/team/jit/technicalinterviewdemo/external/ExternalSmokeTests.java` if the hardened posture becomes part of packaged-image validation
- behavior to preserve
  - deployment assets still support the checked-in monitoring path
  - docs remain honest about same-site-only browser support, `/api/**`-only external reachability, and internal-only technical/docs surfaces
  - no accidental widening into rate limiting, CSRF choreography, or a new management topology
- exact deliverables
  - checked-in deployment assets aligned with the proxy/header assumptions
  - deployment docs that distinguish `/api/**` as the only external surface from internal/devops overview/docs/OpenAPI/actuator paths
  - generated and human-facing docs aligned with the new posture
  - smoke and overview checks aligned with any newly published runtime posture metadata

## Edge Cases And Failure Modes
- Security headers must not break `/docs` HTML rendering, bundled assets, or the `/docs` redirect.
- The fixed header baseline must be documented once through `AbstractDocumentationIntegrationTest.commonResponseHeaders()` and applied consistently enough that REST Docs snippets do not drift across endpoint groups.
- Header policy must not imply cross-origin browser support or introduce CORS behavior that the repo does not support.
- Any proxy-aware handling must preserve correct behavior in `local` and `test`, where HTTPS termination and forwarded headers may not exist.
- The session contract must keep relative login/logout/account paths so the app does not become dependent on an externally configured absolute origin before that need is proven.
- Internal-only `/`, `/hello`, `/docs`, OpenAPI, and actuator endpoints must remain reachable enough for trusted devops validation without drifting back into the external contract.
- Prometheus scraping must remain compatible with `k8s/monitoring/servicemonitor.yaml`; tightening posture must not silently break monitoring.
- If runtime posture metadata is expanded in `/`, the internal/devops validation contract for the root endpoint must stay aligned across docs, tests, and any internal OpenAPI or smoke-validation checks that still describe it.
- If a strict CSP becomes necessary, it must be validated against the generated documentation assets explicitly rather than guessed.

## Validation Plan
- Run focused tests while implementing:
  - `ReverseProxyBoundaryIntegrationTests`
  - `SecurityHeadersIntegrationTests`
  - `TechnicalOverviewControllerIntegrationTests`
  - `ApiDocumentationTests`
  - `SessionApiDocumentationTests`
- Run repository-required validation before completion:
  - `.\gradlew.bat build`
- Run additional validation when applicable:
  - `.\gradlew.bat externalSmokeTest -PexternalSmokeImageName=technical-interview-demo -PdockerImageName=technical-interview-demo` if runtime posture metadata, production defaults, or packaged technical-surface behavior changes
  - `helm lint helm/technical-interview-demo`
  - `helm template technical-interview-demo helm/technical-interview-demo -f helm/technical-interview-demo/values-local.yaml` if Helm files change
- OpenAPI handling:
  - refresh the approved baseline only after intentional contract review with `.\gradlew.bat refreshOpenApiBaseline`
- Benchmark handling:
  - no benchmark rerun expected for this slice unless implementation changes OAuth/session startup behavior enough to trigger the repo’s gatling rule

## Better Engineering Notes
- Do not start with CSRF enablement. The repo still needs a trustworthy reverse-proxy and browser-boundary story before CSRF tokens or origin checks can be planned safely.
- Keep the first slice small and contract-oriented. This is boundary hardening, not a hidden redesign of auth, ingress, or management topology.
- Avoid adding a canonical public-base-url property unless the existing relative-path contract proves insufficient; the current supported session surface does not require absolute external URLs.
- Prefer a conservative header set over a speculative “max security” header set that risks breaking the docs UI or local reviewer workflow.
- Leave edge-owned abuse protection for its own later slice; do not smuggle rate limiting or WAF-like concerns into this work.

## Validation Results
- To be filled in during execution

## User Validation
- Start the app in a production-like configuration and verify the root overview still reports the documented session posture plus any new proxy/header posture fields added by this slice when accessed from the trusted internal path.
- Request `/api/session` through the frontend path and confirm the documented security headers are present while the session contract still returns relative login/logout/account paths and does not imply cross-origin browser support.
- From a trusted internal or devops path, request `/`, `/hello`, `/docs`, `/v3/api-docs`, and `/actuator/health/readiness` and confirm they remain usable for runtime validation but are no longer described as externally reachable.
- Verify `/actuator/prometheus` still works for trusted internal scraping or deliberate port-forward usage, while the docs clearly keep the whole actuator surface out of the external contract.
