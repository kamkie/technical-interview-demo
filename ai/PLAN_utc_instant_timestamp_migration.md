# Plan: UTC Instant Timestamp Migration

## Lifecycle
| Field | Value |
| --- | --- |
| Phase | Planning |
| Status | Ready |

## Summary
- Move app-owned persisted timestamps from Java `LocalDateTime` plus PostgreSQL `timestamp` to Java `Instant` plus PostgreSQL `timestamptz`.
- Change the affected public API fields to serialize as UTC instants with a trailing `Z` while keeping endpoint shapes, field names, pagination, and sort keys stable.
- Success is measured by preserved persisted timestamp meaning after migration, updated contract artifacts, and passing repository validation without leaving mixed timestamp semantics across the API.

## Scope
- In scope:
  - Flyway migration work for `localization_messages`, `users`, `user_roles`, and `audit_logs`
  - Java model conversion for `Localization`, `UserAccount`, `UserRoleGrant`, and `AuditLog`
  - API response timestamp conversion for user, admin-user, localization, audit-log, and operator-surface payloads
  - REST Docs, OpenAPI, and README updates required by the intentional contract change
  - focused test updates that prove UTC instant serialization and preserve sort or pagination behavior
- Out of scope:
  - non-persisted timestamps already modeled as `Instant`, such as `TechnicalOverviewResponse`
  - Spring Session JDBC schema or framework-owned session timestamps
  - book or category APIs, which do not currently expose app-owned persisted timestamp fields
  - new endpoints, new timestamp field names, new filters, or changed sort parameter names
  - release tagging, changelog publication, or roadmap cleanup beyond this plan file

## Current State
- Current behavior:
  - `src/main/resources/db/migration/V2__create_localization_messages_table.sql`, `V5__create_users_tables.sql`, `V6__create_audit_logs_table.sql`, and `V7__expand_user_roles_with_provenance.sql` define app-owned persisted timestamp columns as PostgreSQL `timestamp` without time zone.
  - `src/main/java/team/jit/technicalinterviewdemo/business/localization/Localization.java`, `business/user/UserAccount.java`, `business/user/UserRoleGrant.java`, and `business/audit/AuditLog.java` store those values as `LocalDateTime` and populate them with `LocalDateTime.now(ZoneOffset.UTC)`.
  - `LocalizationResponse`, `UserAccountResponse`, `AdminUserAccountResponse`, `AdminUserRoleGrantResponse`, and `AuditLogResponse` expose those timestamps directly as `LocalDateTime`, which currently serializes without a zone suffix.
  - Generated REST Docs snippets and current response examples show timezone-less values such as `2026-05-04T19:14:51.617622`, while `TechnicalOverviewResponse` already shows the target `Instant` style with `Z`.
- Current constraints:
  - the roadmap item explicitly calls for both persistence and API serialization changes, so this is intentional public-contract work rather than an internal cleanup
  - the repo stays intentionally small, so the implementation should prefer direct type conversion and focused tests over custom timestamp abstractions
  - there is no dedicated Asciidoc page for `/api/account`; that contract is mainly enforced by executable tests, OpenAPI, and HTTP examples
  - the OpenAPI compatibility checker compares structural schema compatibility but does not protect timestamp description or example semantics by itself
- Relevant existing specs and code:
  - roadmap source: `ROADMAP.md`
  - planning rules: `ai/PLAN.md`
  - artifact routing: `ai/DOCUMENTATION.md`
  - validation rules: `ai/TESTING.md`
  - executable behavior specs: `src/test/java/team/jit/technicalinterviewdemo/business/user/UserManagementIntegrationTests.java`, `business/localization/LocalizationApiIntegrationTests.java`, `business/audit/AuditLogApiIntegrationTests.java`, `technical/operator/OperatorSurfaceApiIntegrationTests.java`
  - published contract specs: `src/test/java/team/jit/technicalinterviewdemo/business/user/AdminUserManagementApiDocumentationTests.java`, `business/localization/LocalizationApiDocumentationTests.java`, `business/audit/AuditLogApiDocumentationTests.java`, `technical/operator/OperatorSurfaceApiDocumentationTests.java`
  - machine-readable contract: `src/test/resources/openapi/approved-openapi.json`, plus `src/test/java/team/jit/technicalinterviewdemo/technical/docs/OpenApiIntegrationTests.java` and `technical/docs/OpenApiCompatibilityIntegrationTests.java`
  - HTTP examples to review for drift: `src/test/resources/http/user-account-controller.http`, `admin-user-management-controller.http`, `localization-controller.http`, `audit-log-controller.http`, `operator-surface-controller.http`

## Requirement Gaps And Open Questions
- No blocking user-input gaps remain.
- The selected roadmap item already fixes the intended direction: persisted timestamps move to UTC `Instant`, and the public API publishes that same UTC instant semantics.
- Fallback assumption:
  - all currently exposed app-owned persisted timestamp fields move together in one coherent contract update; partial endpoint-by-endpoint rollout would leave the API with mixed semantics and is not part of this plan

## Locked Decisions And Assumptions
- Keep all existing endpoint paths, JSON field names, pagination behavior, and sort parameter names unchanged. Only timestamp storage and serialized values move.
- Use standard Java `Instant` serialization and persistence support rather than adding a custom serializer or a custom timestamp wrapper type.
- Convert existing PostgreSQL `timestamp` values as UTC instants, using `AT TIME ZONE 'UTC'` or an equivalent explicit UTC-preserving expression during the type change. Do not rely on implicit session timezone casting.
- Limit scope to app-owned persisted timestamps in `localization_messages`, `users`, `user_roles`, and `audit_logs`. Leave technical overview build or git timestamps and Spring Session tables alone.
- Add new Flyway migration SQL plus the required metadata sidecar under `src/main/resources/db/migration/metadata/`.
- Classify the migration conservatively in the metadata. Unless implementation proves safe mixed-version behavior, treat this schema rewrite as not rolling-compatible.
- Refresh `src/test/resources/openapi/approved-openapi.json` after intentional contract review even if the backward-compatibility checker would not flag the timestamp semantic change.
- Update `README.md` with a short pre-RC contract note because the supported API timestamp representation is changing before `v2.0.0-RC1`.
- Review the HTTP example files named above for commentary drift, but do not force request-only edits when the files do not describe response timestamps.

## Execution Mode Fit
- Recommended default mode: `Single Branch`
- Why that mode fits best:
  - one schema migration and one contract change cut across the same shared artifacts: response records, REST Docs tests, OpenAPI tests, the approved baseline, and `README.md`
  - splitting this work would create constant coordination on the migration file, the plan file, the OpenAPI baseline, and the shared contract wording
  - the repo is small enough that one coherent branch is lower risk than worker fanout here
- Coordinator-owned or otherwise shared files if the work fans out:
  - `ai/PLAN_utc_instant_timestamp_migration.md`
  - `src/main/resources/db/migration/V10__*.sql`
  - `src/main/resources/db/migration/metadata/V10__*.json`
  - `src/test/resources/openapi/approved-openapi.json`
  - `README.md`
- Candidate worker boundaries if later delegation becomes necessary:
  - there is no strong worker-safe split; at best, one worker could prepare the domain and migration conversion while the coordinator owns contract artifacts, but the overlap is high enough that `Single Branch` remains the intended mode

## Affected Artifacts
- Tests:
  - `src/test/java/team/jit/technicalinterviewdemo/business/user/UserManagementIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/user/UserAccountServiceTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/user/AdminUserManagementApiDocumentationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/localization/LocalizationApiIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/localization/LocalizationApiDocumentationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/audit/AuditLogApiIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/audit/AuditLogApiDocumentationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/operator/OperatorSurfaceApiIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/operator/OperatorSurfaceApiDocumentationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/operator/OperatorSurfaceServiceTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/docs/OpenApiIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/docs/OpenApiCompatibilityIntegrationTests.java`
- Docs:
  - `README.md`
  - `src/docs/asciidoc/admin-user-management-controller.adoc`
  - `src/docs/asciidoc/localization-controller.adoc`
  - `src/docs/asciidoc/audit-log-controller.adoc`
  - `src/docs/asciidoc/operator-surface-controller.adoc`
  - the generated snippets owned by the REST Docs tests above
- OpenAPI:
  - `src/test/resources/openapi/approved-openapi.json`
- HTTP examples:
  - review-only by default: `src/test/resources/http/user-account-controller.http`, `admin-user-management-controller.http`, `localization-controller.http`, `audit-log-controller.http`, `operator-surface-controller.http`
- Source files:
  - new `src/main/resources/db/migration/V10__migrate_persisted_timestamps_to_timestamptz.sql`
  - new `src/main/resources/db/migration/metadata/V10__migrate_persisted_timestamps_to_timestamptz.json`
  - `src/main/java/team/jit/technicalinterviewdemo/business/user/UserAccount.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/user/UserRoleGrant.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/user/CurrentUserAccountService.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/user/UserAccountResponse.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/user/AdminUserAccountResponse.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/user/AdminUserRoleGrantResponse.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/localization/Localization.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/localization/LocalizationResponse.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/audit/AuditLog.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/audit/AuditLogResponse.java`
- Build or benchmark checks:
  - `.\gradlew.bat build`
  - no `gatlingBenchmark` rerun expected because book search, localization lookup behavior, and OAuth or session startup behavior are not changing

## Execution Milestones
### Milestone 1: Convert Persisted Storage And Domain Types
- goal
  - move app-owned persisted timestamp storage and Java domain types to UTC `Instant` and `timestamptz` without changing endpoint names or public field names yet
- owned files or packages
  - `src/main/resources/db/migration/V10__migrate_persisted_timestamps_to_timestamptz.sql`
  - `src/main/resources/db/migration/metadata/V10__migrate_persisted_timestamps_to_timestamptz.json`
  - `src/main/java/team/jit/technicalinterviewdemo/business/user/`
  - `src/main/java/team/jit/technicalinterviewdemo/business/localization/Localization.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/audit/AuditLog.java`
  - directly affected fixtures and service-level tests under `src/test/java/`
- shared files that a `Shared Plan` worker must leave to the coordinator
  - `src/test/resources/openapi/approved-openapi.json`
  - `README.md`
  - REST Docs contract-test files
- behavior to preserve
  - endpoint paths, JSON field names, sort keys, and pagination stay unchanged
  - current API timestamp strings may remain unchanged during this checkpoint if a temporary mapping bridge is needed
  - stored business meaning of existing timestamps must be preserved through the SQL migration
- exact deliverables
  - add a Flyway migration that converts:
    - `localization_messages.created_at`
    - `localization_messages.updated_at`
    - `users.last_login_at`
    - `users.created_at`
    - `users.updated_at`
    - `user_roles.granted_at`
    - `audit_logs.created_at`
    to `timestamptz` using an explicit UTC-preserving conversion
  - add the required migration metadata sidecar with conservative rollout classification
  - convert `Localization`, `UserAccount`, `UserRoleGrant`, and `AuditLog` to store `Instant`
  - convert constructors, `@PrePersist` or `@PreUpdate` hooks, and service or test fixtures that still require `LocalDateTime`
  - keep any temporary DTO mapping intentionally compatible until the contract-facing milestone lands
- validation checkpoint
  - run targeted domain and operator tests to prove the new storage model works without accidental contract drift:
    - `UserManagementIntegrationTests`
    - `UserAccountServiceTests`
    - `LocalizationApiIntegrationTests`
    - `AuditLogApiIntegrationTests`
    - `OperatorSurfaceServiceTests`
    - `OpenApiCompatibilityIntegrationTests`
- commit checkpoint
  - the schema migration, metadata sidecar, and domain-model conversion are complete, and targeted tests pass with the public API still stable at this checkpoint

### Milestone 2: Publish The UTC Instant Contract
- goal
  - switch the affected public responses, docs, and machine-readable contract to explicit UTC instant serialization with trailing `Z`
- owned files or packages
  - `src/main/java/team/jit/technicalinterviewdemo/business/user/UserAccountResponse.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/user/AdminUserAccountResponse.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/user/AdminUserRoleGrantResponse.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/localization/LocalizationResponse.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/audit/AuditLogResponse.java`
  - the REST Docs test files listed in `Affected Artifacts`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/docs/OpenApiIntegrationTests.java`
  - `src/test/resources/openapi/approved-openapi.json`
  - `README.md`
- shared files if any
  - `src/test/resources/openapi/approved-openapi.json`
  - `README.md`
- behavior to preserve
  - endpoint paths, response field names, optionality, pagination metadata, and sort parameter names remain unchanged
  - only the timestamp value representation changes, from timezone-less local date-time strings to UTC instant strings
  - unaffected fields such as `AuditLogResponse.details` remain byte-for-byte equivalent in meaning
- exact deliverables
  - switch the response record timestamp fields to `Instant`
  - update integration tests so the user, localization, audit-log, and operator surfaces assert timestamp values end with `Z`
  - update REST Docs field descriptions so timestamp wording is explicit about UTC instant semantics rather than generic "timestamp" wording
  - regenerate the snippets consumed by the admin-user, localization, audit-log, and operator Asciidoc pages
  - extend `OpenApiIntegrationTests` so timestamp schema descriptions and `date-time` fields are explicitly validated for the new semantics
  - refresh `src/test/resources/openapi/approved-openapi.json` after contract review
  - add a short `README.md` note describing the pre-RC move to UTC instant timestamps on the affected API surfaces
  - review the timestamp-bearing HTTP example files and leave them unchanged unless commentary needs clarification
- validation checkpoint
  - run the targeted API, REST Docs, and OpenAPI tests for the affected surfaces, then run `.\gradlew.bat build`
- commit checkpoint
  - the published contract, generated docs, and approved OpenAPI baseline all reflect UTC instant serialization, and the full repository build passes

## Edge Cases And Failure Modes
- A direct `timestamp` to `timestamptz` cast can reinterpret existing data through the database session timezone and silently shift values. The migration must use an explicit UTC-preserving conversion.
- Mixed old and new application versions may disagree on timestamp semantics after the column-type rewrite. The migration metadata must not claim rolling compatibility without proof.
- `Instant` serialization adds a trailing `Z`. REST Docs snippets, OpenAPI descriptions, and any brittle string assertions need to move together or the repo will publish contradictory contract artifacts.
- The OpenAPI compatibility checker does not compare description text or timestamp semantics deeply enough to guard this change by itself. Relying on compatibility alone would miss contract drift.
- `/api/account` does not have a dedicated Asciidoc page, so that timestamp contract must be protected through integration tests, OpenAPI assertions, and README or HTTP-example review rather than a controller page.
- Sort fields such as `createdAt`, `updatedAt`, and `grantedAt` must stay available under the same names. The migration should not accidentally rename or remove them from pageable APIs.
- The operator surface reuses `AuditLogResponse`. Any partial migration that updates audit logs but misses the operator surface would leave a nested contract inconsistency.

## Validation Plan
- commands to run
  - `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.business.user.UserManagementIntegrationTests --tests team.jit.technicalinterviewdemo.business.user.UserAccountServiceTests --tests team.jit.technicalinterviewdemo.business.user.AdminUserManagementApiDocumentationTests --tests team.jit.technicalinterviewdemo.business.localization.LocalizationApiIntegrationTests --tests team.jit.technicalinterviewdemo.business.localization.LocalizationApiDocumentationTests --tests team.jit.technicalinterviewdemo.business.audit.AuditLogApiIntegrationTests --tests team.jit.technicalinterviewdemo.business.audit.AuditLogApiDocumentationTests --tests team.jit.technicalinterviewdemo.technical.operator.OperatorSurfaceApiIntegrationTests --tests team.jit.technicalinterviewdemo.technical.operator.OperatorSurfaceApiDocumentationTests --tests team.jit.technicalinterviewdemo.technical.operator.OperatorSurfaceServiceTests --tests team.jit.technicalinterviewdemo.technical.docs.OpenApiIntegrationTests --tests team.jit.technicalinterviewdemo.technical.docs.OpenApiCompatibilityIntegrationTests`
  - `.\gradlew.bat refreshOpenApiBaseline`
  - `.\gradlew.bat build`
- tests to add or update
  - update the user-management integration tests to assert serialized `lastLoginAt`, `createdAt`, `updatedAt`, and `grantedAt` values include `Z`
  - update the localization integration tests to assert `createdAt` and `updatedAt` include `Z` on create, get, and list responses
  - update the audit-log and operator-surface integration coverage to assert `createdAt` includes `Z`
  - convert direct-entity and fixture tests from `LocalDateTime` to `Instant`
  - extend `OpenApiIntegrationTests` to assert timestamp field descriptions and `date-time` schema exposure for the affected response models
- docs or contract checks
  - verify the REST Docs tests regenerate user-facing snippet content for `admin-user-management-controller.adoc`, `localization-controller.adoc`, `audit-log-controller.adoc`, and `operator-surface-controller.adoc`
  - review `src/test/resources/openapi/approved-openapi.json` after refresh to confirm the intended timestamp semantics landed
  - review the timestamp-bearing HTTP example files for stale commentary; no edit is required when they remain request-only and accurate
  - confirm `README.md` reflects the intentional pre-RC timestamp contract shift
- manual verification steps
  - inspect generated snippets or `/docs` output and confirm sample timestamp values now include `Z`
  - call `/api/account`, `/api/admin/users`, `/api/localizations`, `/api/admin/audit-logs`, and `/api/admin/operator-surface` locally and confirm the affected timestamp fields serialize as UTC instants
  - review the migration SQL and confirm every existing `timestamp` value is reinterpreted as UTC rather than as a local server time

## Better Engineering Notes
- The smallest coherent implementation is direct type conversion plus one explicit schema migration, not a repository-wide timestamp utility layer.
- Prefer plain `Instant` support from Spring, Jackson, and Hibernate over custom serializers. The only planned custom logic is the SQL migration expression that preserves existing UTC values.
- Because the repo has no historical-schema upgrade harness today, do not invent a heavy migration-testing framework unless implementation reveals a concrete need. Keep proof in focused integration tests plus careful migration SQL review.
- Protect the new API semantics in `OpenApiIntegrationTests` before refreshing the approved baseline. Otherwise future changes could drift while the backward-compatibility checker stays green.
- If the migration metadata ends up `breaking` or otherwise restore-sensitive, surface that fact clearly in later release-readiness work rather than hiding it as an implementation detail.

## Validation Results
- To be filled in during execution.
- Record exactly which targeted tests, baseline refresh steps, and final build ran, plus any skipped checks and the reason.

## User Validation
- Start the app and check one sample response from each affected surface:
  - `GET /api/account`
  - `GET /api/admin/users`
  - `GET /api/localizations/{id}`
  - `GET /api/admin/audit-logs`
  - `GET /api/admin/operator-surface`
- Confirm every persisted timestamp field now looks like `2026-05-05T12:34:56.789Z`, not `2026-05-05T12:34:56.789`.
- Open `/docs` and `/v3/api-docs` and confirm the published contract now describes those fields as UTC instants.
