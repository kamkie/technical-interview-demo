# Plan: 2.0 RC1 Selected Maintainability Cleanup

## Lifecycle
| Field | Value |
| --- | --- |
| Phase | Planning |
| Status | Ready |

## Summary
- Execute the currently selected `ROADMAP.md` cleanup tasks for the next milestone release, which the current roadmap and release model imply is `v2.0.0-RC1`.
- Replace env-driven admin role assignment with persisted managed role grants, split admin or operational APIs under a clearer `/api/admin/**` surface, expand audit coverage and structure, add the missing PostgreSQL indexes for the current query shapes, and harden the shipped image plus Kubernetes defaults.
- Success is measured by: no runtime dependence on `ADMIN_LOGINS`, one explicit persisted admin-role management path with provenance, reviewed contract updates for renamed admin endpoints, structured audit evidence for privileged mutations and auth events, migration metadata for all new SQL files, hardened deployment assets that still pass local validation, and the full required RC1 validation story.

## Scope
- In scope:
  - the selected `ROADMAP.md` items under `### Moving to 2.0 -> Prerelease Maintainability Cleanup`
  - replacing `ADMIN_LOGINS` runtime role calculation with persisted role grants and explicit provenance
  - adding a small admin user-management API that can list persisted application users and replace their role grants
  - moving admin or operational APIs from mixed `/api/**` locations to `/api/admin/**`
  - expanding audit coverage to category writes, role-management changes, and authentication lifecycle events that the app already owns
  - storing structured audit details in PostgreSQL and surfacing them through the admin audit API
  - documenting audit retention or archival posture without adding a cleanup job
  - adding PostgreSQL indexes that match the current supported search, filter, and join behavior
  - hardening the Docker image plus repo-owned Kubernetes and Helm deployment defaults
- Out of scope:
  - the non-selected roadmap items for shared runtime defaults cleanup or global timestamp migration
  - pageable response redesign, logging redesign, or any route cleanup that is not already part of the selected roadmap work
  - moving the application to bearer-token auth, cross-origin browser support, or a separate management port
  - automatic audit cleanup, background archival jobs, or Spring Batch work
  - adding a frontend admin UI
  - cutting `v2.0.0-RC1`, releasing stable `v2.0.0`, or cleaning up released plan files

## Current State
- Admin role assignment is still recalculated from `app.security.admin-logins` during authenticated-user synchronization in `src/main/java/team/jit/technicalinterviewdemo/business/user/CurrentUserAccountService.java`, and production docs plus deployment examples still expose `ADMIN_LOGINS` as the standing admin mechanism in `README.md`, `SETUP.md`, `k8s/base/deployment.yaml`, and `helm/technical-interview-demo/templates/deployment.yaml`.
- Persisted roles currently live in `user_roles` as a bare `(user_id, role)` join table created by `src/main/resources/db/migration/V5__create_users_tables.sql`, with no provenance fields and no first-class management API.
- The current supported admin or operational APIs are `GET /api/audit-logs` and `GET /api/operator/surface`, documented in `README.md`, `src/docs/asciidoc/audit-log-controller.adoc`, `src/docs/asciidoc/operator-surface-controller.adoc`, and the matching HTTP example files under `src/test/resources/http/`.
- Audit coverage is currently limited to `Book` and `Localization` writes. `Category` writes are not audited, auth lifecycle events are not audited, `AuditTargetType` only contains `BOOK` and `LOCALIZATION_MESSAGE`, `AuditAction` only contains `CREATE`, `UPDATE`, and `DELETE`, and `audit_logs` stores only summary text plus actor identifiers.
- Current query shapes already justify more indexes than the schema has:
  - `GET /api/books` uses case-insensitive contains filters for title, author, and isbn plus category joins in `BookSearchSpecifications`
  - category resolution and uniqueness checks rely on lower-cased name lookups in `CategoryService`
  - admin audit review filters by `targetType`, `action`, and `actorLogin` in `AuditLogQueryService`
- The Docker image still uses `mcr.microsoft.com/openjdk/jdk:25-ubuntu` in `Dockerfile`, and the raw Kubernetes plus Helm deployment manifests still lack explicit pod or container security contexts, `readOnlyRootFilesystem`, and a dedicated writable temp mount.
- The current contract and monitoring assets still assume the old admin paths. `README.md`, `SETUP.md`, the AsciiDoc pages, the HTTP examples, `k8s/monitoring/prometheus-rule.yaml`, and `monitoring/grafana/dashboards/technical-interview-demo.json` all refer to `/api/audit-logs` or `/api/operator/surface`.

## Requirement Gaps And Open Questions
- No blocking user-input gaps remain.
- Resolved planning decisions:
  - interpret "next milestone release" as `v2.0.0-RC1`, because `ROADMAP.md` currently prioritizes freezing the `2.0` contract and cutting `RC1`
  - replace `ADMIN_LOGINS` with bootstrap-only `APP_BOOTSTRAP_INITIAL_ADMIN_IDENTITIES`, using normalized `provider:externalLogin` values and applying them only while no persisted `ADMIN` grant exists
  - keep role management in `business.user` with a small admin API instead of adding an external IAM or internal RBAC framework
  - move only the admin or operational APIs to `/api/admin/**` in this milestone; keep `/`, `/hello`, `/docs`, OpenAPI publication, and actuator endpoints as internal or devops surfaces on the current listener
  - remove the old `/api/audit-logs` and `/api/operator/surface` routes instead of keeping aliases, because this is the last intended prerelease cleanup window before RC contract freeze
  - keep audit retention or archival as a documented deployment responsibility rather than implementing an in-app purge or archive worker
  - keep all new SQL migrations additive and accompanied by sidecar metadata under `src/main/resources/db/migration/metadata/`

## Locked Decisions And Assumptions
- `USER` remains a persisted application role in `user_roles` for this milestone so the schema can evolve in place; the table will become a first-class role-grant model by adding provenance columns rather than by replacing it outright.
- `user_roles` gains provenance fields such as grant source, granted-at timestamp, and optional grantor reference while keeping one row per `(user_id, role)`.
- The new bootstrap path uses `APP_BOOTSTRAP_INITIAL_ADMIN_IDENTITIES` and persists an `ADMIN` grant with source `BOOTSTRAP` on first matching login when the system has no persisted admin grant. Once an admin grant exists, that bootstrap property no longer grants additional admins.
- The managed admin API is intentionally small:
  - `GET /api/admin/users` lists persisted users with their current roles and role-grant provenance
  - `PUT /api/admin/users/{id}/roles` replaces the non-bootstrap role set for that user and records provenance plus a short operator-supplied reason
- The admin route cleanup is limited to:
  - `GET /api/admin/audit-logs` replacing `GET /api/audit-logs`
  - `GET /api/admin/operator-surface` replacing `GET /api/operator/surface`
  - the new admin user-management endpoints also living under `/api/admin/**`
- Business CRUD routes for books, categories, localizations, session bootstrap, and self-account management stay under their current `/api/**` paths.
- Structured audit data is exposed through a new `details` object on the audit response instead of remaining storage-only. This is an intentional pre-RC contract change for the admin audit API.
- Auth events covered by this milestone are login success, login failure, logout, and session-cap rejection. The audit payload for those events must exclude secrets, cookies, CSRF tokens, OAuth codes, and raw provider tokens.
- The index set for this milestone is locked unless `EXPLAIN` during execution proves a smaller equivalent set:
  - PostgreSQL `pg_trgm` extension
  - trigram indexes on `lower(books.title)`, `lower(books.author)`, and `lower(books.isbn)`
  - btree index on `book_categories(category_id)`
  - functional index on `lower(categories.name)`
  - btree indexes on `audit_logs(actor_login)` and `audit_logs(action)`
- Container hardening is also locked for this milestone:
  - switch to a JRE-only base image pinned by digest
  - keep non-root execution
  - remove the shell-dependent image `HEALTHCHECK` if the chosen base image cannot support it cleanly
  - add Kubernetes and Helm security context defaults: `runAsNonRoot`, dropped capabilities, `allowPrivilegeEscalation=false`, `seccompProfile=RuntimeDefault`, `readOnlyRootFilesystem=true`, and an `emptyDir` temp mount
  - add a `startupProbe` alongside the existing readiness and liveness probes

## Affected Artifacts
- Tests:
  - `src/test/java/team/jit/technicalinterviewdemo/business/user/UserManagementIntegrationTests.java`
  - likely new admin-user-management integration and REST Docs tests under `src/test/java/team/jit/technicalinterviewdemo/business/user/`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/security/SecurityIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/ProductionConfigurationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/audit/AuditLogIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/audit/AuditLogApiIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/audit/AuditLogApiDocumentationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/operator/OperatorSurfaceApiIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/operator/OperatorSurfaceApiDocumentationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/docs/OpenApiIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/docs/OpenApiCompatibilityIntegrationTests.java`
- Docs and contract artifacts:
  - `README.md`
  - `SETUP.md`
  - `src/docs/asciidoc/index.adoc`
  - `src/docs/asciidoc/audit-log-controller.adoc`
  - `src/docs/asciidoc/operator-surface-controller.adoc`
  - likely a new AsciiDoc page for admin user management
  - `src/test/resources/http/audit-log-controller.http`
  - `src/test/resources/http/operator-surface-controller.http`
  - likely a new HTTP example file for admin user management
  - `src/test/resources/openapi/approved-openapi.json`
- Source files:
  - `src/main/java/team/jit/technicalinterviewdemo/business/user/CurrentUserAccountService.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/user/UserAccount.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/user/UserAccountRepository.java`
  - likely new user-role-grant types under `src/main/java/team/jit/technicalinterviewdemo/business/user/`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/bootstrap/BootstrapSettingsProperties.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecuritySettingsProperties.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/ProductionSecurityConfigurationValidator.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecurityConfiguration.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SessionService.java`
  - likely custom auth success or failure handlers under `src/main/java/team/jit/technicalinterviewdemo/technical/security/`
  - `src/main/java/team/jit/technicalinterviewdemo/business/audit/AuditAction.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/audit/AuditTargetType.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/audit/AuditLog.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/audit/AuditLogRepository.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/audit/AuditLogResponse.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/audit/AuditLogService.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/audit/AuditLogController.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/audit/AuditLogQueryService.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/category/CategoryService.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/operator/OperatorSurfaceController.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/operator/OperatorSurfaceService.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/operator/OperatorSurfaceResponse.java`
- Database and migration metadata:
  - new SQL migrations under `src/main/resources/db/migration/`
  - matching sidecars under `src/main/resources/db/migration/metadata/`
- Deployment and monitoring assets:
  - `Dockerfile`
  - `k8s/base/deployment.yaml`
  - `k8s/base/secret-example.yaml`
  - `helm/technical-interview-demo/values.yaml`
  - `helm/technical-interview-demo/values-local.yaml` only if the hardening defaults need a local override
  - `helm/technical-interview-demo/templates/deployment.yaml`
  - `helm/technical-interview-demo/templates/secret-example.yaml`
  - `k8s/monitoring/prometheus-rule.yaml`
  - `monitoring/grafana/dashboards/technical-interview-demo.json`
- Build, smoke, and benchmark checks:
  - `.\gradlew.bat refreshOpenApiBaseline`
  - `.\gradlew.bat gatlingBenchmark`
  - `.\gradlew.bat externalSmokeTest`
  - `.\gradlew.bat build`
  - `helm template technical-interview-demo helm/technical-interview-demo -f helm/technical-interview-demo/values-local.yaml`
  - `kubectl kustomize k8s/overlays/local`

## Execution Milestones
### Milestone 1: Persist Managed Role Grants
- goal
  - remove runtime `ADMIN_LOGINS` role calculation and replace it with persisted, reviewable role grants plus one bootstrap-only path for the first admin.
- files to update
  - `technical.bootstrap.BootstrapSettingsProperties`
  - `business.user.*` user and role-grant model types
  - `CurrentUserAccountService`
  - `UserAccountRepository`
  - `SecuritySettingsProperties`
  - `ProductionSecurityConfigurationValidator`
  - `README.md`
  - `SETUP.md`
  - deployment secret examples and env wiring
  - new SQL migration plus metadata sidecar for `user_roles`
- behavior to preserve
  - authenticated users are still synchronized into the application database on login
  - `/api/account` still returns the persisted application profile
  - the same-site browser-session and CSRF contract stays unchanged
- exact deliverables
  - `ADMIN_LOGINS` removed from the standing runtime contract, docs, and deployment examples
  - bootstrap-only `APP_BOOTSTRAP_INITIAL_ADMIN_IDENTITIES` documented and wired
  - `user_roles` evolved into a provenance-carrying role-grant model without destructive table replacement
  - new admin user-management API under `/api/admin/users`
  - targeted integration and REST Docs coverage for listing users and replacing their roles

### Milestone 2: Split Admin Routes And Expand Audit Evidence
- goal
  - make the admin surface explicit and make audit evidence useful for production review before the RC1 contract freeze.
- files to update
  - `AuditAction`, `AuditTargetType`, `AuditLog`, `AuditLogResponse`, `AuditLogService`, `AuditLogController`, `AuditLogQueryService`
  - `CategoryService`
  - `SessionService` and the OAuth success or failure handlers
  - `OperatorSurfaceController`, `OperatorSurfaceService`, `OperatorSurfaceResponse`
  - security routing in `SecurityConfiguration`
  - AsciiDoc pages, HTTP examples, README, SETUP, OpenAPI baseline, and monitoring assets that refer to the renamed paths
  - new SQL migration plus metadata sidecar for `audit_logs`
- behavior to preserve
  - `Book`, `Category`, `Localization`, `Session`, and self-account business flows remain at their current functional semantics
  - `GET /api/session`, OAuth callback paths, and CSRF-backed unsafe write behavior do not change
  - operator visibility remains ADMIN-only
- exact deliverables
  - `GET /api/admin/audit-logs` replacing `GET /api/audit-logs`
  - `GET /api/admin/operator-surface` replacing `GET /api/operator/surface`
  - admin docs, examples, OpenAPI, and monitoring assets updated to the new paths
  - structured audit `details` payloads exposed in the admin audit API
  - category writes, role updates, login success, login failure, logout, and session-cap rejection all recorded in audit storage
  - retention or archival ownership documented as deployment-managed rather than implemented as an app job

### Milestone 3: Add Measured Indexes And Harden Runtime Assets
- goal
  - align PostgreSQL and deployment assets with the actual runtime workload before RC1.
- files to update
  - new SQL migration plus metadata sidecar for indexes and `pg_trgm`
  - `Dockerfile`
  - `k8s/base/deployment.yaml`
  - `k8s/base/secret-example.yaml` if env names change
  - `helm/technical-interview-demo/templates/deployment.yaml`
  - `helm/technical-interview-demo/templates/secret-example.yaml`
  - `helm/technical-interview-demo/values.yaml`
  - `helm/technical-interview-demo/values-local.yaml` only if local overrides are needed for `readOnlyRootFilesystem` or temp storage
- behavior to preserve
  - local development remains possible through the current shell, Docker, and Helm local flows
  - the packaged container still boots with `SPRING_PROFILES_ACTIVE=prod`
  - readiness, liveness, and current smoke checks continue to work after the image and manifest changes
- exact deliverables
  - additive SQL for the locked index set plus matching migration sidecars
  - `EXPLAIN` notes recorded during execution if any locked index is replaced by a smaller equivalent
  - a JRE-only pinned base image and non-root container execution retained
  - Kubernetes and Helm security contexts, read-only root filesystem, writable temp mount, and startup probe added and documented where needed

## Edge Cases And Failure Modes
- Bootstrapping the first admin must not silently grant new admins forever. The bootstrap property is only for the zero-admin state, and execution should include a focused test for that cutover.
- Role provenance must survive repeated logins without rewriting manual admin grants or erasing the recorded grant source.
- Because `/api/audit-logs` and `/api/operator/surface` are documented external endpoints today, removing those paths is a deliberate contract break that must move through REST Docs, HTTP examples, OpenAPI, README, and monitoring assets together.
- Auth-event auditing must avoid secrets and unstable provider payloads. Cookies, CSRF tokens, authorization codes, and raw OAuth tokens must never enter audit `details`.
- Structured audit payloads must stay compact and intention-revealing. Dumping full entity snapshots or raw request bodies would make the API noisy and risk sensitive-data leakage.
- The new audit enum values and response shape must remain queryable and documented without turning the demo into a generic event-sourcing framework.
- PostgreSQL hardening changes must stay additive and rolling-compatible. Do not replace or drop existing tables in the same milestone when an additive column or index can satisfy the selected roadmap goal.
- A JRE-only or distroless image change can break the current shell-based image `HEALTHCHECK`. If that happens, the healthcheck should be removed from the image rather than reintroducing shell tooling just to keep the old pattern.
- `readOnlyRootFilesystem=true` can break Java temp usage unless `/tmp` or another writable path is mounted explicitly and covered in local validation.
- Monitoring queries and dashboards keyed to `/api/audit-logs` must move with the route change or they will quietly stop capturing admin-auth failures and operational traffic.

## Validation Plan
- Commands to run during execution:
  - `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.business.user.UserManagementIntegrationTests --tests team.jit.technicalinterviewdemo.technical.security.SecurityIntegrationTests --tests team.jit.technicalinterviewdemo.technical.ProductionConfigurationTests --tests team.jit.technicalinterviewdemo.business.audit.AuditLogIntegrationTests --tests team.jit.technicalinterviewdemo.business.audit.AuditLogApiIntegrationTests --tests team.jit.technicalinterviewdemo.business.audit.AuditLogApiDocumentationTests --tests team.jit.technicalinterviewdemo.technical.operator.OperatorSurfaceApiIntegrationTests --tests team.jit.technicalinterviewdemo.technical.operator.OperatorSurfaceApiDocumentationTests --tests team.jit.technicalinterviewdemo.technical.docs.OpenApiIntegrationTests`
  - `.\gradlew.bat refreshOpenApiBaseline` after intentional contract review of the renamed admin routes, new admin user-management endpoints, and updated audit response schema
  - `.\gradlew.bat gatlingBenchmark` because the milestone changes authenticated-user synchronization and auth lifecycle handling
  - `.\gradlew.bat externalSmokeTest -PexternalSmokeImageName=technical-interview-demo -PdockerImageName=technical-interview-demo`
  - `.\gradlew.bat build`
  - `helm template technical-interview-demo helm/technical-interview-demo -f helm/technical-interview-demo/values-local.yaml`
  - `kubectl kustomize k8s/overlays/local`
- Tests to add or update:
  - bootstrap-only initial-admin behavior
  - persisted role-grant provenance and manual admin role replacement
  - renamed admin route coverage and error behavior
  - audit entries for category writes, role changes, and auth events
  - operator surface response updates if linked paths or audit payloads change
  - container or deployment-template assertions only where the repo already has automated coverage; otherwise rely on template rendering plus smoke tests
- Docs or contract checks:
  - update README, SETUP, AsciiDoc, HTTP examples, and approved OpenAPI together for every renamed admin path and every new admin endpoint
  - update monitoring assets in the same change as the route rename
  - add migration sidecar JSON for every new SQL file
- Manual verification steps:
  - log in using a configured bootstrap admin identity on an empty database and confirm the first `ADMIN` grant becomes persisted with source `BOOTSTRAP`
  - use the new admin user-management API to replace a user's roles and confirm provenance plus audit evidence are visible
  - call the renamed admin audit and operator endpoints and confirm the old paths are no longer documented or accepted
  - inspect a category write and a login event in the audit API and confirm the `details` payload is structured and sanitized
  - run the hardened image locally and confirm startup, readiness, and the packaged smoke path still pass

## Better Engineering Notes
- Keep the role-management implementation feature-local to `business.user`. This repo does not need a generic RBAC subsystem or policy engine.
- Keep the route cleanup focused on the admin or operational APIs. Do not expand this milestone into a broader `/` or actuator topology redesign.
- Use additive schema changes and migration metadata rigorously. The release workflow already treats migration metadata as part of deployment safety.
- Do not hide audit-retention requirements inside a future cleanup promise. If the app will not delete or archive audit rows itself, the docs should say that plainly in the same milestone.
- The container hardening work should stay inside repo-owned image and manifest defaults. Network policies, service mesh policy, and external admission controls are follow-up deployment concerns, not part of this selected roadmap batch.

## Validation Results
- To be filled in during execution.

## User Validation
- Start from an empty database, authenticate as the configured bootstrap admin identity, and confirm that the user becomes an ADMIN without relying on a standing `ADMIN_LOGINS` runtime contract.
- Call `GET /api/admin/users` and `PUT /api/admin/users/{id}/roles` and confirm persisted roles plus provenance are visible and editable.
- Confirm the admin operational routes are now under `/api/admin/**` and that the old `/api/audit-logs` and `/api/operator/surface` paths are no longer the supported contract.
- Trigger a category write and one auth lifecycle event, then inspect `GET /api/admin/audit-logs` and confirm the recorded `details` payload is useful and sanitized.
- Render the Helm chart and Kustomize overlay, then run the packaged smoke path to confirm the hardened image and manifest defaults still boot and pass validation.
