# Plan: 2.0 Pre-RC Contract, Edge, And Smoke Alignment

## Lifecycle
| Field | Value |
| --- | --- |
| Phase | Integration |
| Status | Implemented |

## Summary
- Plan the next pre-RC `2.0` batch: formalize first-party UI integration requirements, publish the `1.x` to `2.0` upgrade guide, add checked-in edge reference assets, and extend local plus deployed smoke validation to prove the documented session and CSRF flow.
- Keep the current `2.0` application contract stable; this batch is primarily contract publication, deployment-reference, and validation alignment on top of the already shipped `/api/session/**` and one-public-origin boundary work.
- Success is measured by: one decision-complete upgrade and integration narrative for UI consumers, one repo-owned edge reference for `/api/**`-only public exposure, smoke coverage for `GET /api/session` plus CSRF-backed unsafe writes, and a roadmap slice that is ready to hand off into RC freeze work.

## Scope
- In scope:
  - the next selected roadmap batch under `ROADMAP.md` `### Moving to 2.0`, excluding the RC and stable-release steps
  - documenting first-party UI integration requirements for login bootstrap, logout, session refresh, CSRF refresh, and one-public-origin reverse-proxy deployment
  - publishing a `1.x` to `2.0` upgrade guide in the checked-in published docs set
  - adding checked-in reference edge assets for `/api/**`-only public routing and private non-`/api/**` surfaces
  - extending `externalSmokeTest`, `externalDeploymentCheck`, the `Post-Deploy Smoke` workflow, and the restore-drill helper so they prove `GET /api/session`, CSRF cookie bootstrap, and one authenticated unsafe `/api/**` write
- Out of scope:
  - changing route shapes, response payloads, or auth semantics of the shipped `2.0` `/api/session/**` and `/api/account` contract
  - adding cross-origin browser support or CORS guarantees
  - adding app-owned rate limiting, challenge, or WAF behavior
  - cutting `v2.0.0-RC1`, releasing stable `v2.0.0`, updating `CHANGELOG.md`, or removing the `2.0` roadmap track
  - building or embedding the separate first-party UI in this repository

## Current State
- The current `2.0` browser/session contract is already implemented and published across `README.md`, `SETUP.md`, `src/docs/asciidoc/index.adoc`, `src/docs/asciidoc/session-controller.adoc`, `src/test/resources/http/authentication.http`, and the `technical.security` integration/REST Docs tests. The contract already assumes one public origin, `/api/session` bootstrap, `loginProviders[]`, and same-site CSRF refresh through `GET /api/session`.
- There is no dedicated published `1.x` to `2.0` upgrade guide yet. The migration facts are scattered across README, setup notes, and session docs rather than collected into one reviewer-facing artifact.
- The repo already ships deployment assets under `k8s/` and `helm/`, but there is no checked-in reverse-proxy or ingress reference asset that demonstrates `/api/**`-only public routing while keeping `/`, `/docs`, `/v3/api-docs*`, and `/actuator/**` private or trusted-only.
- `src/externalTest/java/team/jit/technicalinterviewdemo/external/ExternalSmokeTests.java` currently proves root posture, docs/OpenAPI reachability, readiness, Flyway state, and one JDBC-backed authenticated `GET /api/account` path, but it does not yet prove `GET /api/session`, capture the readable `XSRF-TOKEN` cookie, or execute a CSRF-protected unsafe write.
- The deployed smoke path flows through `externalDeploymentCheck`, `.github/workflows/post-deploy-smoke.yml`, and `scripts/release/invoke-restore-drill.ps1`, so any expanded smoke contract has to move through those artifacts together.
- The current public machine-readable and executable contract should remain stable for this batch; no OpenAPI or REST Docs schema changes are required unless repo research during execution reveals a documentation omission rather than a behavior gap.

## Requirement Gaps And Open Questions
- No blocking user-input gaps remain.
- Resolved planning decisions:
  - scope the next batch to the first four unfinished `2.0` items and leave RC/stable release work for a later plan or execution handoff
  - add a small checked-in Kubernetes `Ingress`-style reference under `k8s/` plus focused documentation about private non-`/api/**` surfaces and edge-owned abuse protection, and keep Helm unchanged unless execution proves a second artifact is needed to avoid drift
  - use `PUT /api/account/language` with a dedicated synthetic smoke user and a stable preferred-language value as the authenticated unsafe `/api/**` write proof
  - accept partial deployed-smoke coverage when JDBC-backed session access is unavailable: local packaged smoke and restore drills should prove the full session/bootstrap/write flow, while the scheduled or manual `Post-Deploy Smoke` workflow may remain HTTP-only unless the JDBC secret set is configured
  - publish the `1.x` to `2.0` upgrade guide as a new Asciidoctor page under `src/docs/asciidoc/` and link it from `index.adoc`, with concise README and setup references pointing to the owning guide

## Locked Decisions And Assumptions
- Preserve the current `2.0` public contract. This batch should not introduce new endpoints, new response fields, or new auth models.
- Keep `GET /api/session` as the single browser bootstrap and refresh endpoint. Do not add a parallel `/api/csrf` route or another UI-only helper API.
- Keep the supported first-party browser model same-site and one-public-origin through reverse proxy; do not imply cross-origin browser support anywhere in the new guide or assets.
- Keep `/api/account` as the authenticated persisted-profile resource, with `PUT /api/account/language` as the preferred low-noise proof of an authenticated unsafe write for smoke validation.
- Reuse the existing external test harness and `externalDeploymentCheck` path rather than introducing a second deployed-validation framework.
- Accept partial deployed-smoke coverage when JDBC-backed session access is unavailable. The full write proof is still required for local packaged smoke and for deployed or restore-drill runs where JDBC-backed session access is configured.
- Treat edge abuse protection as deployment owned. The reference assets and docs should show where those controls belong without trying to implement rate limiting or challenge logic inside the application.
- Keep the edge reference asset vendor-neutral enough to be illustrative rather than production-prescriptive. The asset should demonstrate `/api/**` public routing and the private treatment of non-`/api/**` paths without claiming to be a one-size-fits-all production ingress package.
- Leave RC and stable-release sequencing outside this plan. Once this batch lands, the next work should freeze the contract, run the required validation, and cut `v2.0.0-RC1` from `main`.

## Affected Artifacts
- Tests and smoke automation:
  - `src/externalTest/java/team/jit/technicalinterviewdemo/external/ExternalSmokeTests.java`
  - `src/externalTest/java/team/jit/technicalinterviewdemo/external/ExternalHttpTestSupport.java`
  - `src/externalTest/java/team/jit/technicalinterviewdemo/external/ExternalSessionSupport.java`
  - `.github/workflows/post-deploy-smoke.yml`
  - `scripts/release/invoke-restore-drill.ps1`
- Docs and published contract guidance:
  - `README.md`
  - `SETUP.md`
  - `ai/RELEASES.md`
  - `src/docs/asciidoc/index.adoc`
  - `src/docs/asciidoc/session-controller.adoc`
  - `src/docs/asciidoc/technical-overview-controller.adoc`
  - new `src/docs/asciidoc/upgrade-1x-to-2-0.adoc`
  - `src/test/resources/http/authentication.http`
  - `src/test/resources/http/user-account-controller.http`
- Deployment reference assets:
  - new `k8s/edge/public-api-ingress.yaml`
  - new `k8s/edge/README.md`
- Likely unchanged unless execution uncovers a real spec gap:
  - `src/test/resources/openapi/approved-openapi.json`
  - `src/main/java/**`
  - `helm/technical-interview-demo/**`

## Execution Milestones
### Milestone 1: Publish The UI Integration And Upgrade Story
- goal
  - turn the already-shipped `2.0` session and CSRF contract into one explicit UI integration narrative and one explicit `1.x` to `2.0` migration guide.
- files to update
  - `README.md`
  - `SETUP.md`
  - `src/docs/asciidoc/index.adoc`
  - `src/docs/asciidoc/session-controller.adoc`
  - `src/docs/asciidoc/technical-overview-controller.adoc`
  - new `src/docs/asciidoc/upgrade-1x-to-2-0.adoc`
  - `src/test/resources/http/authentication.http`
  - `src/test/resources/http/user-account-controller.http`
- behavior to preserve
  - the current `/api/session/**`, `/api/account`, and same-site CSRF behavior stays unchanged
  - `README.md` remains a contract summary, not a step-by-step setup duplicate of `SETUP.md`
  - no OpenAPI baseline or executable spec churn is introduced unless a real contract inconsistency is discovered
- exact deliverables
  - one explicit first-party UI integration checklist covering login bootstrap, logout, session refresh, CSRF refresh, and one-public-origin routing assumptions
  - one published `1.x` to `2.0` upgrade guide that calls out the `/api/**` boundary, `/api/session/**` auth routes, `loginProviders[]`, and CSRF-protected unsafe writes
  - aligned HTTP examples that show the intended bootstrap-refresh-write sequence clearly enough for reviewers and UI consumers

### Milestone 2: Add Checked-In Edge Reference Assets
- goal
  - add one repo-owned reference edge asset that shows how the documented `2.0` boundary is supposed to be exposed.
- files to update
  - new `k8s/edge/public-api-ingress.yaml`
  - new `k8s/edge/README.md`
  - `README.md`
  - `SETUP.md`
- behavior to preserve
  - the application remains the source of truth for the `/api/**` contract and session posture
  - non-`/api/**` paths stay internal/devops-only by deployment contract rather than by new app-side path blocking
  - Helm remains unchanged unless execution proves that a raw reference asset would drift too far from the main deployment story
- exact deliverables
  - one checked-in edge example that routes `/api/**` publicly to the app service and leaves non-`/api/**` paths private or separately handled
  - one short owner document explaining that abuse protection for login bootstrap and unsafe internet-public writes belongs at the edge
  - cross-references from human-facing docs to the new reference asset without overstating it as turnkey production infrastructure

### Milestone 3: Extend Smoke And Deployed Validation
- goal
  - prove the documented session bootstrap and CSRF write story in both local packaged-image smoke and deployed-environment validation.
- files to update
  - `src/externalTest/java/team/jit/technicalinterviewdemo/external/ExternalSmokeTests.java`
  - `src/externalTest/java/team/jit/technicalinterviewdemo/external/ExternalHttpTestSupport.java`
  - `src/externalTest/java/team/jit/technicalinterviewdemo/external/ExternalSessionSupport.java`
  - `.github/workflows/post-deploy-smoke.yml`
  - `scripts/release/invoke-restore-drill.ps1`
  - `ai/RELEASES.md`
  - `README.md`
  - `SETUP.md`
- behavior to preserve
  - the existing root/readiness/docs/OpenAPI/Flyway/authenticated-account checks remain intact unless they are intentionally superseded by stronger equivalent assertions
  - the deployed smoke path keeps using `externalDeploymentCheck`
  - the scheduled or manual post-deploy workflow may remain HTTP-only when the JDBC secret set is not configured
  - the smoke write remains deterministic and bounded enough for repeated post-deploy runs
- exact deliverables
  - packaged smoke coverage for `GET /api/session`, validation that `XSRF-TOKEN` is issued or refreshed, and one authenticated unsafe `/api/**` write using the mirrored `X-XSRF-TOKEN` header
  - deployed smoke coverage for `GET /api/session` plus session/bootstrap/write proof when JDBC-backed session access is configured, while preserving HTTP-only post-deploy coverage when that JDBC access is intentionally unavailable
  - workflow, restore-drill, and release-guide messaging updated so maintainers know the smoke contract now includes session bootstrap and, when JDBC-backed session access is available, a CSRF-backed write rather than only `GET /api/account`

## Edge Cases And Failure Modes
- The new upgrade guide must distinguish one-time `1.x` to `2.0` migration guidance from the standing `2.0` supported contract; otherwise it will become a second source of truth that drifts from README and REST Docs.
- The edge reference asset must not imply that non-`/api/**` paths are internet-public just because they still exist for trusted internal validation.
- The edge reference asset must not overpromise controller-specific abuse-protection annotations as portable standards; controller- or vendor-specific knobs need to be clearly labeled as examples.
- The smoke write must remain low-noise and repeatable in deployed environments. Using a dedicated synthetic smoke user with a stable preferred-language value is acceptable; mutating shared admin or seed-book data is not.
- `GET /api/session` must be proven before the unsafe write so the smoke harness exercises the documented CSRF bootstrap path rather than bypassing it with an internal test shortcut.
- Post-deploy smoke, restore-drill, and release-guide messaging must move with the new assertions and must clearly distinguish HTTP-only scheduled coverage from JDBC-enabled full-flow coverage; otherwise operators may think the deployed checks still prove only readiness and `GET /api/account`, or they may assume the write proof is always present when it is not.
- Because this batch is not supposed to change the public API, any need to refresh OpenAPI or REST Docs snippets should be treated as a signal to verify whether execution accidentally changed contract behavior.

## Validation Plan
- Commands to run during execution:
  - `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.technical.security.SessionApiIntegrationTests --tests team.jit.technicalinterviewdemo.technical.security.SessionApiDocumentationTests --tests team.jit.technicalinterviewdemo.business.user.UserManagementIntegrationTests`
  - `.\gradlew.bat externalSmokeTest -PexternalSmokeImageName=technical-interview-demo -PdockerImageName=technical-interview-demo`
  - `.\gradlew.bat build`
- Additional validation expectations:
  - manually review the new `k8s/edge/` reference assets against the current service names and `/api/**` routing assumptions because the standard Gradle build does not validate raw ingress manifests
  - if execution changes the deployed smoke workflow summary or restore-drill text, manually inspect those artifacts for consistency with the new smoke assertions
  - if execution unexpectedly changes executable API specs or public docs snippets, revisit whether the batch has drifted into contract change and update the affected contract artifacts intentionally instead of silently
- Docs or contract checks:
  - keep `README.md`, `SETUP.md`, `src/docs/asciidoc/`, and the IntelliJ HTTP examples aligned on the same bootstrap-refresh-write sequence
  - keep the upgrade guide additive to the standing docs instead of repeating setup instructions or release history
- Manual verification steps:
  - render the generated docs and confirm the new upgrade guide and session docs give a coherent `1.x` to `2.0` browser/session story
  - inspect the new edge reference asset and confirm it exposes only `/api/**` publicly while leaving non-`/api/**` surfaces explicitly private/trusted-only
  - run the packaged smoke flow and confirm it now proves `GET /api/session`, captures `XSRF-TOKEN`, and successfully performs the selected authenticated unsafe write

## Better Engineering Notes
- This batch should stay mostly in docs, reference YAML, and smoke automation. If execution starts needing broad `src/main/java` changes, stop and confirm that a real behavior gap exists rather than inventing one.
- Reuse the existing `PUT /api/account/language` contract for the unsafe-write proof. That is smaller and easier to reason about than pushing admin-only or domain-mutating endpoints into smoke validation.
- Keep Helm out of the write set unless a raw reference asset proves too disconnected from the repo’s main deployment story. Shipping both raw ingress YAML and chart templating by default is more surface area than this demo needs.
- Do not fold the RC1 freeze or stable-release work into this batch. Those roadmap items should start only after this plan’s docs, edge references, and smoke proof are complete and validated.

## Validation Results
- 2026-05-04: `git diff --check`
  - result: passed; no whitespace errors were introduced, and the new `k8s/edge/public-api-ingress.yaml` asset was manually reviewed against the current `technical-interview-demo` service name, namespace, and `/api/**` routing assumption
- 2026-05-04: `. .\scripts\load-dotenv.ps1 -Quiet; .\gradlew.bat test --tests team.jit.technicalinterviewdemo.technical.security.SessionApiIntegrationTests --tests team.jit.technicalinterviewdemo.technical.security.SessionApiDocumentationTests --tests team.jit.technicalinterviewdemo.business.user.UserManagementIntegrationTests --no-daemon`
  - result: passed; validated the existing session/bootstrap contract docs and the `PUT /api/account/language` behavior that the external smoke flow now uses for the authenticated unsafe-write proof
- 2026-05-04: `. .\scripts\load-dotenv.ps1 -Quiet; .\gradlew.bat externalSmokeTest -PexternalSmokeImageName=technical-interview-demo -PdockerImageName=technical-interview-demo --no-daemon`
  - result: passed; the packaged smoke environment proved readiness, docs/OpenAPI reachability, the published CSRF and abuse-protection posture on `GET /`, JDBC-backed `GET /api/session` bootstrap with readable `XSRF-TOKEN`, an authenticated `PUT /api/account/language`, a persisted `GET /api/account` readback, and Flyway visibility; release-identity and expected-runtime-posture checks were skipped in this local run because no expected deployed values were configured
- 2026-05-04: `. .\scripts\load-dotenv.ps1 -Quiet; .\gradlew.bat build --no-daemon`
  - result: passed; full repository validation succeeded, including test execution, Asciidoctor rendering for the new upgrade guide, security scans, SBOM generation, and Docker image packaging

## User Validation
- Read the new upgrade guide and confirm it cleanly answers what a `1.x` consumer must change for `2.0`.
- Walk the UI integration notes and confirm they describe the real bootstrap/logout/refresh sequence without requiring unstated assumptions.
- Inspect the checked-in edge reference asset and confirm it matches the intended `/api/**`-only public exposure model.
- Run the local smoke flow and confirm it now proves the session bootstrap plus one CSRF-backed unsafe write, not only the authenticated `GET /api/account` path.
