# Plan: Code Review Issue Fixes

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Planning |
| Status | Ready |

## Summary
- Fix the five findings from the 2026-05-08 whole-application code review: role-grant duplication, weak book write validation, category case-insensitive uniqueness, generic integrity-error wording, and auth-failure alert coverage.
- Treat this as a `2.x` maintenance plan: valid published behavior must keep working, and invalid or operationally misleading behavior should become better specified.
- Success means each finding has an executable spec or infrastructure check, implementation matches the spec, affected published contract artifacts are aligned, and `./build.ps1 build` passes.

## Scope
- In scope:
  - ADMIN user-role replacement behavior for bootstrap role grants.
  - Book create/update validation constraints and their public error contract.
  - Category case-insensitive uniqueness at the database boundary.
  - Generic data-integrity problem wording.
  - Prometheus alert coverage for protected book writes.
  - Tests, REST Docs snippets, approved OpenAPI updates, migration metadata, and HTTP examples where behavior or reviewer workflows change.
- Out of scope:
  - New roles, new book fields, new category APIs, or broader admin-management redesign.
  - Reworking OAuth/session architecture.
  - Internet-edge rate limiting or WAF implementation.
  - Releasing or tagging after implementation.

## Current State
- `UserAccount.replaceManagedRoleGrants()` keeps `BOOTSTRAP` grants and then adds every requested role as `ADMIN_MANAGED`, which can duplicate an existing bootstrap role before `saveAndFlush`.
- Book write DTOs require only nonblank title/author/isbn and nonnull publication year, while persistence uses default `varchar(255)` string columns and accepts any integer year.
- Category uniqueness is enforced with service checks, but the schema has only a case-sensitive `unique(name)` and a non-unique `lower(name)` index.
- `ApiExceptionHandler.handleDataIntegrityViolation()` is global but returns the public detail `Book data violates a database constraint.`
- `TechnicalInterviewDemoAuthenticationFailuresElevated` omits protected `POST/PUT/DELETE /api/books...` requests even though `SecurityConfiguration` protects those writes.
- Governing specs and contract artifacts:
  - `src/test/java/team/jit/technicalinterviewdemo/business/user/AdminUserManagementApiIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/book/BookApiIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/api/ApiErrorHandlingIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/category/CategoryApiIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/api/ApiExceptionHandlerTests.java`
  - REST Docs tests under `src/test/java/team/jit/technicalinterviewdemo/technical/docs/` and feature documentation tests.
  - `src/test/resources/openapi/approved-openapi.json`
  - migration SQL and sidecar metadata under `src/main/resources/db/migration/`.
  - `infra/k8s/monitoring/prometheus-rule.yaml`.

## Requirement Gaps And Open Questions
- No blocking user input is needed.
- The maintenance-compatible fallback is to preserve all valid existing API behavior and only tighten invalid input handling or operational observability.
- If a proposed OpenAPI update is classified as breaking by the compatibility checker, stop and decide whether to document the validation tightening differently or defer that piece to a later major-version plan.

## Locked Decisions And Assumptions
- Target this as a candidate `v2.0.1` maintenance fix set unless the roadmap later selects a different version.
- Do not remove bootstrap ADMIN grants through the managed-role endpoint. Requested roles already present as bootstrap grants should be treated as satisfied, not reinserted.
- Book publication years should use the existing query-filter range policy: `0..3000`.
- Book title and author should align with the `Book` column length of 255; ISBN should stay at the existing filter maximum of 32 unless a spec review chooses a stricter ISBN format.
- Category case-insensitive uniqueness should be enforced by a new Flyway migration, not by changing historical migrations.
- Generic integrity errors should remain non-sensitive and localized through the existing `error.data.integrity_violation` key.

## Execution Shape And Shared Files
- Recommended shape: one local branch, implemented in commit-sized milestones.
- The work is connected through public contract validation and one final full build, so a single executor is simpler than delegated parallel work.
- If later delegation is required:
  - Worker A can own user-role behavior and tests.
  - Worker B can own book validation and docs/OpenAPI.
  - Worker C can own category migration and integrity-error handling.
  - Coordinator should own `ROADMAP.md`, this plan, `src/test/resources/openapi/approved-openapi.json`, and final validation.

## Affected Artifacts
- Tests:
  - `AdminUserManagementApiIntegrationTests`
  - `BookApiIntegrationTests`
  - `ApiDocumentationTests` and/or `BookApiDocumentationTests`
  - `CategoryApiIntegrationTests` or a focused migration/integration test
  - `ApiExceptionHandlerTests`
  - monitoring validation tests if an existing infrastructure-manifest check exists, otherwise a focused text/template test may be added.
- Docs and contract:
  - REST Docs snippets generated by tests.
  - `src/docs/asciidoc/book-controller.adoc` only if the human-facing validation description must change.
  - `src/test/resources/openapi/approved-openapi.json` if schema constraints intentionally change.
  - `src/manualTests/http/examples/` or `src/manualTests/http/suites/` only if reviewer workflows should exercise the new negative cases.
- Source:
  - `business.user`
  - `business.book`
  - `business.category`
  - `technical.api`
  - `infra/k8s/monitoring/prometheus-rule.yaml`
  - new `V11__*.sql` migration plus `metadata/V11__*.json`.
- No durable AI-guide update is expected unless execution discovers a recurring repo-wide lesson.

## Execution Milestones

### Milestone 1: Role-Grant Replacement Safety
- Goal: make ADMIN role replacement idempotent when requested roles overlap with bootstrap grants.
- Owned files or packages: `src/main/java/team/jit/technicalinterviewdemo/business/user/`, `src/test/java/team/jit/technicalinterviewdemo/business/user/`.
- Shared files reserved to the coordinator: none.
- Context required before execution: `AGENTS.md`, `.agents/references/execution.md`, this plan, `AdminUserManagementApiIntegrationTests`, `UserAccount`, `UserRoleGrant`, `AdminUserManagementService`.
- Behavior to preserve: bootstrap grants remain immutable through managed-role replacement; every managed role replacement still requires `USER`, an ADMIN actor, CSRF, and audit logging.
- Exact deliverables:
  - Add an integration or service test that replaces roles for the bootstrap admin with `["USER", "ADMIN"]` and verifies no duplicate grant/constraint failure.
  - Adjust `replaceManagedRoleGrants()` so roles already satisfied by bootstrap grants are not added again as managed grants.
  - Preserve existing provenance behavior for non-bootstrap managed role changes.
- Validation checkpoint: targeted user-management tests pass.
- Commit checkpoint: commit after targeted validation if this milestone is executed independently.

### Milestone 2: Book Write Validation And Contract
- Goal: define and enforce public validation for book create/update field lengths and publication-year range.
- Owned files or packages: `business.book`, book API integration/docs tests, OpenAPI baseline if intentionally changed.
- Shared files reserved to the coordinator: `src/test/resources/openapi/approved-openapi.json`.
- Context required before execution: `AGENTS.md`, `.agents/references/execution.md`, `.agents/references/documentation.md`, `.agents/references/testing.md`, this plan, `BookApiIntegrationTests`, `ApiDocumentationTests`, `BookCreateRequest`, `BookUpdateRequest`, `Book`.
- Behavior to preserve: valid book create/update payloads continue to return the current response shape; ISBN remains immutable on update; duplicate ISBN remains 409.
- Exact deliverables:
  - Add executable specs for too-long title/author/isbn and out-of-range publication years returning 400 validation problems.
  - Add validation annotations or equivalent service validation that matches persistence limits and the existing `0..3000` year policy.
  - Update REST Docs snippets and approved OpenAPI if schema constraints change.
- Validation checkpoint: targeted book API/docs/OpenAPI tests pass.
- Commit checkpoint: commit after targeted validation if this milestone is executed independently.

### Milestone 3: Category Case-Insensitive Database Uniqueness
- Goal: move category case-insensitive uniqueness from service-only behavior to database-enforced behavior.
- Owned files or packages: category tests, Flyway migration SQL, migration metadata, category repository/service only if needed.
- Shared files reserved to the coordinator: migration version naming if other migrations are being added concurrently.
- Context required before execution: `AGENTS.md`, `.agents/references/execution.md`, `.agents/references/documentation.md`, this plan, `src/main/resources/db/migration/metadata/README.md`, `CategoryApiIntegrationTests`, `CategoryService`, current migrations.
- Behavior to preserve: duplicate category creates still return the documented 400 problem; existing valid category names remain valid.
- Exact deliverables:
  - Add `V11__...sql` to enforce case-insensitive uniqueness, likely through a unique index on `lower(name)` or an equivalent PostgreSQL mechanism.
  - Add required migration metadata sidecar.
  - Add an integration test that proves the database rejects case-variant duplicates or that concurrent/out-of-band duplicate persistence cannot bypass the invariant.
  - Preserve or improve API-level handling so ordinary duplicate API requests still return the existing 400 duplicate-category problem rather than a raw 500.
- Validation checkpoint: targeted category/migration tests pass.
- Commit checkpoint: commit after targeted validation if this milestone is executed independently.

### Milestone 4: Integrity Error Wording And Alert Coverage
- Goal: remove misleading book-specific generic integrity wording and ensure auth-failure monitoring covers protected book writes.
- Owned files or packages: `technical.api`, `infra/k8s/monitoring/`, related tests.
- Shared files reserved to the coordinator: none.
- Context required before execution: `AGENTS.md`, `.agents/references/execution.md`, `.agents/references/testing.md`, this plan, `ApiExceptionHandlerTests`, `prometheus-rule.yaml`, `SecurityConfiguration`.
- Behavior to preserve: generic integrity errors remain 409 with no sensitive database details in the public response; alert threshold math and existing protected endpoint coverage remain intact.
- Exact deliverables:
  - Update the generic data-integrity detail to feature-neutral wording and update tests.
  - Extend `TechnicalInterviewDemoAuthenticationFailuresElevated` numerator and denominator to include protected book writes.
  - Add or update an infrastructure-manifest test if the repo has one; otherwise document the manifest diff in validation notes.
- Validation checkpoint: targeted API tests pass; monitoring manifest is reviewed or tested.
- Commit checkpoint: commit after targeted validation if this milestone is executed independently.

### Milestone 5: Contract Refresh And Full Validation
- Goal: integrate all changes, refresh generated contract artifacts intentionally, and prove the maintenance set is green.
- Owned files or packages: generated REST Docs/OpenAPI artifacts, validation notes.
- Shared files reserved to the coordinator: `src/test/resources/openapi/approved-openapi.json`, `ROADMAP.md`, this plan.
- Context required before execution: `AGENTS.md`, `.agents/references/execution.md`, `.agents/references/testing.md`, `.agents/references/documentation.md`, this plan.
- Behavior to preserve: stable `2.x` valid requests stay backward-compatible; invalid-request tightening is documented as maintenance.
- Exact deliverables:
  - Run the targeted tests from previous milestones.
  - Run `./build.ps1 refreshOpenApiBaseline` only after reviewing intentional OpenAPI changes.
  - Run final `./build.ps1 build`.
  - Fill `## Validation Results` with exact commands and outcomes.
- Validation checkpoint: final wrapper build passes.
- Commit checkpoint: final commit before handoff if prior milestones were not already committed.

## Edge Cases And Failure Modes
- Bootstrap ADMIN role replacement must not remove the only admin path or create duplicate role-grant keys.
- Book validation must reject invalid input before database persistence and should not convert existing conflict cases into validation failures.
- Category migration can fail if a deployed database already contains case-variant duplicates. The migration should either fail clearly or include an explicit duplicate-detection precondition; do not silently merge categories.
- Adding OpenAPI constraints may be treated as a request-schema tightening. If the compatibility gate reports a breaking change, stop and decide whether the stable-line policy allows it as a bug fix or whether to avoid publishing new schema constraints.
- Prometheus `uri` labels may depend on Micrometer templating; preserve the current label style and only extend endpoint coverage.

## Validation Plan
- Targeted loop commands:
  - `./build.ps1 test --tests *AdminUserManagementApiIntegrationTests`
  - `./build.ps1 test --tests *BookApiIntegrationTests --tests *ApiDocumentationTests`
  - `./build.ps1 test --tests *CategoryApiIntegrationTests`
  - `./build.ps1 test --tests *ApiExceptionHandlerTests`
- Contract refresh when intentional:
  - `./build.ps1 refreshOpenApiBaseline`
- Final proof:
  - `./build.ps1 build`
- Benchmark:
  - Not required by default because no book list/search query behavior, localization lookup behavior, or OAuth/session startup behavior is intentionally changed. Reconsider if implementation touches those paths.

## Testing Strategy
- Unit tests:
  - Role-grant normalization/idempotency if service-level coverage is clearer than HTTP-only coverage.
  - Generic integrity-error wording.
- Integration tests:
  - ADMIN role replacement through HTTP.
  - Book invalid payloads through HTTP.
  - Category case-variant duplicate persistence under PostgreSQL/Testcontainers.
- Contract tests:
  - REST Docs tests for changed validation examples.
  - OpenAPI compatibility and approved baseline review for intentional schema changes.
- Smoke/benchmark tests:
  - No manual smoke or benchmark required unless execution changes startup/session behavior or reviewer HTTP workflows.
- Negative scenarios:
  - Duplicate bootstrap/admin role request.
  - Overlong book fields and invalid years.
  - Case-variant category duplicate.
  - Generic integrity failure from a non-book context if practical.

## Better Engineering Notes
- Prefer direct fixes in feature-owned packages over new shared validation frameworks.
- Keep the category migration forward-only and explicit; do not edit historical migrations.
- Avoid broad API error reshaping. This plan only changes a misleading generic detail string.
- Do not add release notes or changelog entries until release work is explicitly in scope.

## Validation Results
- To be filled in during execution.

## User Validation
- Confirm `PUT /api/admin/users/{bootstrapAdminId}/roles` with `["USER", "ADMIN"]` succeeds without duplicate grants.
- Confirm invalid book payloads return 400 validation problems.
- Confirm case-variant category duplicates cannot be stored.
- Confirm generic integrity errors no longer mention books unless the failing operation is actually book-specific.
- Confirm the auth-failure alert expression includes protected book writes.

## Required Content Checklist
- Behavior changing: yes, all five review findings are covered.
- Roadmap entry: `ROADMAP.md` Planned Work row for `Code review issue fixes`.
- Out of scope: documented in `## Scope`.
- Governing specs and contract artifacts: documented in `## Current State` and per milestone.
- Likely source files and packages: documented in `## Affected Artifacts`.
- Compatibility promise: stable `2.x` valid behavior remains backward-compatible; invalid behavior may tighten as maintenance.
- Edge cases, migration, rollout, benchmark risks: documented.
- Requirement gaps: none blocking.
- Execution shape: single local branch, with optional split boundaries.
- Context per milestone: documented.
- Tests/docs/OpenAPI/HTTP examples/AI guides: documented.
- Validation proof: targeted tests plus `./build.ps1 build`.
- User verification: documented.
