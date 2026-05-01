# Plan: 1.0 Contract And Production Posture

## Summary
- Define `1.0` as a stable interview-demo reference application, not a production-ready starter template.
- Freeze the `1.x` compatibility surface by explicitly separating supported contract endpoints from deployment-scoped technical endpoints, while keeping room to mark any truly non-contract convenience endpoints explicitly if they still exist after review.
- Align runtime metadata, published docs, HTTP examples, and deployment assets with that frozen posture so the repository stops advertising unresolved pre-`1.0` choices.
- Success is measured by: one consistent `1.0` promise across repo docs and generated docs, no unresolved pre-`1.0` blockers left in the supported-contract narrative, and passing repo validation.

## Scope
- In scope:
  - locking the `1.0` product promise and the intended `1.x` compatibility promise
  - classifying the current endpoint surface into supported contract, deployment-scoped technical surface, and any remaining demo-only convenience surface
  - reviewing endpoint naming, resource semantics, and response-shape documentation before declaring the surface frozen
  - documenting the intended production posture for CSRF, session cookies, optional OAuth login, admin bootstrap, and trusted deployment topology
  - aligning technical overview metadata, REST Docs pages, HTTP examples, README guidance, setup/deployment guidance, and design guidance with the frozen posture
  - updating deployment manifests and Helm values only where the current checked-in defaults contradict the frozen posture
  - refreshing the approved OpenAPI baseline only if the reviewed `1.0` contract intentionally changes the generated OpenAPI document
- Out of scope:
  - adding new business features or new public endpoints
  - redesigning response bodies beyond small contract-fix corrections found during the freeze review
  - the later roadmap items for release checklist, deployment troubleshooting, and upgrade/rollback documentation
  - new infrastructure such as ingress controllers, service meshes, separate management ports, or secret-management systems
  - changing the benchmark model unless execution ends up changing OAuth/session startup behavior

## Current State
- Current behavior:
  - `ROADMAP.md` still treats the `1.0` promise, supported surface, CSRF decision, and technical endpoint exposure as unresolved.
  - `README.md` currently mixes business APIs, operational endpoints, and demo endpoints into one “Public API Summary”, while also calling out unresolved pre-`1.0` blockers.
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecurityConfiguration.java` disables CSRF globally and permits public access to `GET /actuator/info` and `GET /actuator/prometheus`.
  - `src/main/resources/application.properties` exposes `health`, `info`, and `prometheus` actuator endpoints for every profile.
  - `src/main/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewService.java` hard-codes `csrfEnabled=false` and reports `prometheus` in the exposed-endpoint list; `TechnicalOverviewControllerIntegrationTests` and `ApiDocumentationTests` assert those values.
  - REST Docs and reviewer HTTP examples currently document `GET /actuator/prometheus` as a normal public endpoint under `src/docs/asciidoc/technical-endpoints.adoc` and `src/test/resources/http/technical-endpoints.http`.
  - Deployment assets already lean toward a production-style default of `prod` profile plus internal Prometheus scraping: `k8s/base/configmap.yaml` defaults to `SPRING_PROFILES_ACTIVE=prod`, `helm/technical-interview-demo/values.yaml` defaults `oauth.enabled=false`, and `k8s/monitoring/servicemonitor.yaml` scrapes `/actuator/prometheus` from the in-cluster service.
- Current constraints:
  - the repo is intentionally demo-sized, so the freeze should prefer direct documentation and small config/runtime adjustments over new infrastructure or policy frameworks
  - the supported API contract already depends on integration tests, REST Docs, HTTP examples, and the approved OpenAPI baseline; any intentional contract shift must update those artifacts together
  - technical endpoints such as health/readiness and metrics have both app-level and deployment-level implications, so docs and deployment assets must agree on what is internet-public versus deployment-scoped
  - the existing external smoke validation depends on the application starting in a production-like container and reaching readiness, so posture changes must preserve that path
- Relevant existing specs and code:
  - roadmap source: `ROADMAP.md`
  - planning rules: `ai/PLAN.md`
  - design guidance with unresolved security posture notes: `ai/DESIGN.md`
  - human-facing contract and deployment summary: `README.md`
  - setup/deployment/troubleshooting guide: `SETUP.md`
  - generated docs source: `src/docs/asciidoc/index.adoc`, `src/docs/asciidoc/technical-endpoints.adoc`
  - reviewer HTTP examples: `src/test/resources/http/technical-endpoints.http`, `src/test/resources/http/authentication.http`, `src/test/resources/http/technical-overview-controller.http`
  - technical overview runtime surface: `src/main/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewService.java`, `src/main/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewResponse.java`
  - security posture: `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecurityConfiguration.java`
  - documentation/OpenAPI generation: `src/main/java/team/jit/technicalinterviewdemo/technical/docs/OpenApiConfiguration.java`, `src/test/java/team/jit/technicalinterviewdemo/technical/docs/ApiDocumentationTests.java`, `src/test/java/team/jit/technicalinterviewdemo/technical/docs/OpenApiIntegrationTests.java`, `src/test/resources/openapi/approved-openapi.json`
  - technical exposure tests: `src/test/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewControllerIntegrationTests.java`, `src/test/java/team/jit/technicalinterviewdemo/technical/logging/HttpTracingIntegrationTests.java`
  - deployment assets: `k8s/base/configmap.yaml`, `k8s/base/deployment.yaml`, `k8s/monitoring/servicemonitor.yaml`, `helm/technical-interview-demo/values.yaml`, `helm/technical-interview-demo/templates/deployment.yaml`, `monitoring/kube-prometheus-stack-values.yaml`

## Locked Decisions And Assumptions
- User decisions:
  - `1.0` means a stable interview-demo reference app, not a production-ready starter
  - `GET /actuator/prometheus` is supported for trusted deployment scraping, but not as an internet-public endpoint
  - CSRF remains disabled for browser-session writes in `1.0` and must be documented as a deliberate demo tradeoff
  - `GET /` and `GET /hello` are part of the stable supported `1.x` contract
- Planning assumptions that the executor should not revisit:
  - `1.0` means “stable interview-demo reference app”, not “production-ready starter”; if the user wants starter-grade security/platform guarantees, stop and revise the roadmap scope before implementation
  - the `1.x` compatibility promise applies to the documented supported contract, not to every convenience endpoint that happens to exist in the running demo
  - supported contract for `1.0` includes:
    - business/documentation surface: `/`, `/hello`, `/docs`, `/v3/api-docs`, `/v3/api-docs.yaml`, `/api/books...`, `/api/categories`, `/api/localizations...`, `/api/account...`
    - supported operational surface: `/actuator/health`, `/actuator/health/liveness`, `/actuator/health/readiness`, `/actuator/info`
  - deployment-scoped technical surface includes `/actuator/prometheus`; it remains supported for trusted deployment scraping, but it is not part of the internet-public contract
  - `/oauth2/authorization/github` remains the supported interactive login bootstrap path only when the optional `oauth` profile is active; it is a technical auth bootstrap endpoint, not part of the business API surface
  - CSRF stays disabled for `1.0`, but the docs must explicitly frame that as a deliberate demo tradeoff for reviewer-oriented session workflows rather than leaving it as an unresolved pre-`1.0` question
  - deployed defaults continue to keep OAuth opt-in through the `oauth` profile and environment-provided GitHub secrets; bare `prod` must not start requiring OAuth credentials
  - admin bootstrap remains environment-driven through `ADMIN_LOGINS`; this plan does not add a new admin-management flow
  - prefer doc alignment and targeted runtime metadata/config changes over larger runtime hardening work; if execution uncovers a need for true in-app actuator privatization or CSRF token choreography, stop and spin a separate follow-up plan

## Affected Artifacts
- Tests:
  - `src/test/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewControllerIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/logging/HttpTracingIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/docs/ApiDocumentationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/docs/OpenApiIntegrationTests.java` only if OpenAPI descriptions or security metadata change
  - likely a focused security/configuration integration test if `/actuator/prometheus` exposure becomes profile- or property-sensitive in code
- Docs:
  - `README.md`
  - `SETUP.md`
  - `src/docs/asciidoc/index.adoc`
  - `src/docs/asciidoc/technical-endpoints.adoc`
  - `src/test/resources/http/technical-endpoints.http`
  - `src/test/resources/http/authentication.http`
  - `src/test/resources/http/technical-overview-controller.http`
  - `ai/DESIGN.md`
- OpenAPI:
  - `src/test/resources/openapi/approved-openapi.json` only if the intentional `1.0` review changes generated OpenAPI descriptions or security narrative
- Source files:
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecurityConfiguration.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewService.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewResponse.java` if new posture metadata fields are added
  - `src/main/java/team/jit/technicalinterviewdemo/technical/docs/OpenApiConfiguration.java` if the generated OpenAPI description needs to reflect the frozen contract
  - `src/main/resources/application.properties`
  - `src/main/resources/application-prod.properties` only if the checked-in exposure defaults need tightening to match the frozen posture
- Deployment assets:
  - `k8s/base/configmap.yaml`
  - `k8s/base/deployment.yaml`
  - `k8s/monitoring/servicemonitor.yaml`
  - `helm/technical-interview-demo/values.yaml`
  - `helm/technical-interview-demo/templates/deployment.yaml`
- Build or benchmark checks:
  - `.\gradlew.bat build`
  - `.\gradlew.bat externalSmokeTest -PexternalSmokeImageName=technical-interview-demo -PdockerImageName=technical-interview-demo` if posture changes touch production-like startup or checked-in deployment defaults
  - `helm lint helm/technical-interview-demo` and `helm template technical-interview-demo helm/technical-interview-demo -f helm/technical-interview-demo/values-local.yaml` if Helm files change
  - no benchmark rerun expected unless execution changes OAuth/session startup behavior
  - no OpenAPI baseline refresh unless the reviewed contract intentionally changes the generated OpenAPI document

## Execution Milestones
### Milestone 1: Freeze The 1.0 Promise And Contract Tiers
- Goal:
  - define what `1.0` means and classify the existing endpoint surface so future docs and code changes have a stable target
- Files to update:
  - `README.md`
  - `ai/DESIGN.md`
  - `src/docs/asciidoc/index.adoc`
  - `src/docs/asciidoc/technical-endpoints.adoc`
- Behavior to preserve:
  - all existing endpoints remain available unless a later milestone intentionally changes runtime exposure
  - no business API path, method, request body, or response shape changes in this milestone
- Exact deliverables:
  - add an explicit `1.0` promise statement and `1.x` compatibility promise to `README.md`
  - replace the current flat endpoint summary with a contract-tier breakdown:
    - supported business/documentation endpoints
    - supported operational endpoints
    - deployment-scoped technical endpoints
    - demo-only convenience endpoints, only if any remain after the freeze review
  - update `ai/DESIGN.md` so the design guidance no longer presents the `1.0` identity as unresolved
  - update the generated docs overview pages to use the same classification language instead of implying every endpoint is part of one uniform public contract

### Milestone 2: Review And Freeze The Supported Surface
- Goal:
  - complete the roadmap’s endpoint/semantics review without turning the freeze into an accidental breaking-change batch
- Files to update:
  - `README.md`
  - `src/docs/asciidoc/index.adoc`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/docs/OpenApiConfiguration.java` only if contract wording there is inaccurate
  - `src/test/java/team/jit/technicalinterviewdemo/technical/docs/OpenApiIntegrationTests.java` only if generated OpenAPI wording changes
  - `src/test/resources/openapi/approved-openapi.json` only after intentional contract review
- Behavior to preserve:
  - the currently documented `/api` path structure, pagination/filter behavior, localized `ProblemDetail` error shape, and authenticated-session requirements remain stable unless the review exposes a true contract bug
  - keep `/` and `/hello` in the supported contract and do not turn their review into a breaking-removal exercise
- Exact deliverables:
  - audit the current endpoint names, resource semantics, and response-model labels against the existing contract docs and tests
  - fix only contract/documentation mismatches that are small enough to stay within the freeze scope
  - if the audit reveals a larger public contract problem, stop and spin a separate follow-up plan instead of hiding the redesign inside this freeze plan
  - update OpenAPI descriptions and refresh the approved baseline only if the final reviewed `1.0` wording intentionally changes generated OpenAPI output

### Milestone 3: Freeze Production Posture And Technical Endpoint Exposure
- Goal:
  - make the runtime posture and deployment guidance honest about CSRF, optional OAuth, session-cookie defaults, admin bootstrap, and metrics exposure
- Files to update:
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecurityConfiguration.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewService.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewResponse.java` if new posture metadata is added
  - `src/test/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewControllerIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/logging/HttpTracingIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/docs/ApiDocumentationTests.java`
  - `README.md`
  - `SETUP.md`
  - `src/docs/asciidoc/technical-endpoints.adoc`
  - `src/test/resources/http/technical-endpoints.http`
  - `src/test/resources/http/authentication.http`
  - `src/test/resources/http/technical-overview-controller.http`
- Behavior to preserve:
  - authenticated write endpoints continue to use the current session-based/OAuth-backed model
  - bare `prod` remains deployable without enabling the optional `oauth` profile
  - health/readiness endpoints remain available for smoke checks and deployment probes
- Exact deliverables:
  - remove the “still under active roadmap review” wording from `README.md` and replace it with the locked `1.0` production posture
  - document CSRF as an explicit demo tradeoff rather than an unresolved question
  - document `/actuator/prometheus` as deployment-scoped for trusted scraping, not part of the internet-public contract
  - align technical overview output and its tests with the final posture; prefer adding one or two explicit posture fields over leaving hard-coded misleading values
  - if runtime exposure rules must change to make the docs true, keep those changes targeted and add focused tests for the new behavior

### Milestone 4: Align Deployment Assets With The Frozen Posture
- Goal:
  - ensure the checked-in Kubernetes, Helm, and monitoring assets reinforce the frozen `1.0` defaults instead of contradicting them
- Files to update:
  - `k8s/base/configmap.yaml`
  - `k8s/base/deployment.yaml`
  - `k8s/monitoring/servicemonitor.yaml`
  - `helm/technical-interview-demo/values.yaml`
  - `helm/technical-interview-demo/templates/deployment.yaml`
  - `README.md`
  - `SETUP.md`
- Behavior to preserve:
  - the current deployable path of Docker image plus Kubernetes/Helm assets remains small and readable
  - the monitoring assets continue to support in-cluster Prometheus scraping
- Exact deliverables:
  - confirm the checked-in defaults still express:
    - `prod` as the deployment profile
    - secure session cookies by default
    - optional OAuth enablement
    - environment-driven admin bootstrap
    - trusted internal scraping for Prometheus
  - adjust only the YAML values, comments, or surrounding docs that currently contradict the frozen posture
  - if no YAML behavior change is required after review, still update the surrounding docs so reviewers can see that the deployment assets were intentionally accepted

## Edge Cases And Failure Modes
- If the docs leave `/` or `/hello` outside the stable `1.x` contract after the user explicitly included them, the plan execution will violate a locked product decision.
- If `/actuator/prometheus` is documented as deployment-scoped but tests, examples, or runtime metadata still present it as a normal public endpoint, the contract becomes self-contradictory.
- Any runtime restriction added to `/actuator/prometheus` can break `k8s/monitoring/servicemonitor.yaml` unless the deployment story is updated in the same change.
- Changing health/info access rules would affect smoke checks, readiness assumptions, and generated documentation, so those endpoints should remain stable unless the user explicitly asks for tighter lockdown.
- If OpenAPI descriptions or security-scheme wording change and the approved baseline is not refreshed intentionally, the build will fail on compatibility/documentation checks.
- If execution drifts into re-enabling CSRF instead of documenting the deliberate tradeoff, authenticated write tests, reviewer HTTP examples, and potentially OpenAPI docs all need a larger coordinated update than this plan allows.
- If deployment docs make OAuth look mandatory for production, that would regress the current opt-in deployment model and contradict the checked-in Helm/Kubernetes defaults.

## Validation Plan
- Commands to run:
  - `.\gradlew.bat build`
  - `.\gradlew.bat externalSmokeTest -PexternalSmokeImageName=technical-interview-demo -PdockerImageName=technical-interview-demo` if execution changes checked-in production-like defaults or technical endpoint exposure
  - `helm lint helm/technical-interview-demo` if Helm files change
  - `helm template technical-interview-demo helm/technical-interview-demo -f helm/technical-interview-demo/values-local.yaml` if Helm files change
- Tests to add or update:
  - update `TechnicalOverviewControllerIntegrationTests` for any new posture metadata or revised exposed-endpoint assertions
  - update `ApiDocumentationTests` and generated snippets when technical-endpoint documentation language changes
  - add focused security/config integration coverage if the runtime exposure of `/actuator/prometheus` or related technical endpoints changes
  - update `OpenApiIntegrationTests` and refresh the approved baseline only if the reviewed contract intentionally changes generated OpenAPI output
- Docs or contract checks:
  - `README.md`, `SETUP.md`, `src/docs/asciidoc/`, reviewer HTTP examples, and `ai/DESIGN.md` must all use the same endpoint-tier and production-posture language
  - no benchmark rerun unless execution changes OAuth/session startup behavior
  - no HTTP example omissions for affected technical/auth endpoints
- Manual verification steps:
  - inspect the generated root technical-overview JSON and confirm it reflects the frozen posture instead of the old unresolved-state wording
  - verify that the generated docs and reviewer HTTP examples describe `/actuator/prometheus`, `/`, `/hello`, and `/oauth2/authorization/github` with the intended contract tier
  - verify that the deployment guidance still shows a coherent story for secure session cookies, optional OAuth, admin bootstrap, and trusted Prometheus scraping

## Better Engineering Notes
- This plan deliberately narrows `1.0` to a stable interview-demo reference app. If the real goal is a production-ready starter, stop now and create a separate plan that explicitly covers CSRF reintroduction, in-app actuator hardening, stronger secret/bootstrap workflows, and possibly different deployment guarantees.
- Do not hide endpoint removals inside a “freeze” milestone. Classifying `/` and `/hello` as demo-only is enough unless the user explicitly wants a breaking cleanup.
- Avoid adding ingress, management-port splits, or custom policy machinery just to express “deployment-scoped Prometheus”; the current repo can communicate that posture with small runtime metadata/doc changes and the existing cluster-scoped monitoring assets.
- If the contract review uncovers a real public API redesign need, that should become a separate follow-up plan rather than an unbounded expansion of this freeze task.

## Validation Results
- To be filled in during execution

## User Validation
- Confirm that the delivered repository clearly answers these questions without ambiguity:
  - What does `1.0` mean for this project?
  - Which endpoints are part of the stable supported contract?
  - Which endpoints are deployment-scoped technical surfaces?
  - Which endpoints, if any, remain demo conveniences only?
  - What is the intended `1.0` posture for CSRF, optional OAuth, session cookies, admin bootstrap, and Prometheus scraping?
