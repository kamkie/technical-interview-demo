# Plan: Code Review Issue Fixes

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Integration |
| Status | Implemented |

## Planning Readiness
| Field | Value |
| --- | --- |
| Decision Complete | Yes |
| Blocking Open Questions | None |
| Accepted Fallbacks | D1, D3, D4, D7, D8 |
| Ready For Execution | Yes |
| Last Updated | 2026-05-08 |

## Summary
- Fix the five findings from the 2026-05-08 whole-application code review: role-grant duplication, weak book write validation, category case-insensitive uniqueness, generic integrity-error wording, and auth-failure alert coverage.
- Treat this as a `2.x` maintenance plan: valid published behavior must keep working, and invalid or operationally misleading behavior should become better specified.
- Success means each finding has an executable spec or infrastructure check, implementation matches the spec, affected published contract artifacts are aligned, and `./build.ps1 -FullBuild build` passes.

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
- `BookSearchCriteria` already treats ISBN filters as max 32 characters and publication-year filters as `0..3000`; write validation should align with those published read-side policies unless execution discovers a stricter executable spec.
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
  - `src/test/java/team/jit/technicalinterviewdemo/technical/docs/OpenApiCompatibilityIntegrationTests.java`
  - `src/test/resources/openapi/approved-openapi.json`
  - migration SQL and sidecar metadata under `src/main/resources/db/migration/`
  - `infra/k8s/monitoring/prometheus-rule.yaml`

## Requirement Gaps And Open Questions
| ID | Question / Gap | Why It Matters | Owner | Status | Fallback / Decision | Blocks Ready? |
| --- | --- | --- | --- | --- | --- | --- |
| Q1 | Is user input needed to choose behavior for the five review findings? | Open product choices would make implementation invent behavior. | Agent | Answered | No user input is needed; current specs, review findings, and roadmap maintenance policy define the intended behavior. | No |
| Q2 | How should invalid-request tightening be handled on the stable `2.x` line? | Validation constraints can affect OpenAPI compatibility and client behavior. | Agent | Answered | Preserve valid existing API behavior; treat invalid input and misleading operational behavior as maintenance fixes. | No |
| Q3 | What if OpenAPI compatibility flags added validation constraints as breaking? | A breaking contract change is disallowed on the stable `2.x` line without a later major-version plan. | Agent | Answered | Stop before refreshing the approved baseline and either narrow documentation/schema changes or replan the affected validation work. | No |
| Q4 | What if a deployed database already contains case-variant category duplicates? | A unique lower-name index could fail to apply during rollout. | Agent | Answered | Make the migration fail clearly or include an explicit duplicate-detection precondition; do not silently merge categories. | No |
| Q5 | Is there an existing automated infrastructure-manifest test for Prometheus rules? | The alert change needs evidence, but the repository may not already test Kubernetes manifests. | Agent | Answered | Add or update an existing focused test if present; otherwise record manifest review evidence in `Validation Results`. | No |

## Decision Log And Assumptions
| ID | Decision / Assumption | Source | Date | Revisit Trigger |
| --- | --- | --- | --- | --- |
| D1 | Target this as a candidate `v2.0.1` maintenance fix set unless the roadmap selects a different version. | `ROADMAP.md` current project state | 2026-05-08 | `ROADMAP.md` selects another post-`2.0` workstream or target version. |
| D2 | Do not remove bootstrap ADMIN grants through the managed-role endpoint; requested roles already present as bootstrap grants are satisfied and must not be reinserted as managed grants. | Current user-role model and admin-management contract | 2026-05-08 | A future admin-management redesign changes bootstrap grant semantics. |
| D3 | Book publication years should use the existing query-filter range policy: `0..3000`. | `BookSearchCriteria` | 2026-05-08 | Existing read-side year policy changes or a stricter executable write spec is discovered. |
| D4 | Book title and author should align with the `Book` column length of 255; ISBN should stay at the existing filter maximum of 32 unless a spec review chooses a stricter ISBN format. | `Book`, `BookSearchCriteria`, current write DTOs | 2026-05-08 | Persistence lengths, filter policy, or public ISBN format policy changes. |
| D5 | Category case-insensitive uniqueness should be enforced by a new Flyway migration, not by changing historical migrations. | Migration history and repository migration rules | 2026-05-08 | Migration version conflicts with another accepted branch. |
| D6 | Generic integrity errors should remain non-sensitive and localized through the existing `error.data.integrity_violation` key. | `ApiExceptionHandlerTests`, localization seed data | 2026-05-08 | Error-contract redesign is explicitly planned. |
| D7 | OpenAPI baseline refresh is allowed only after intentional compatibility review; a breaking compatibility result blocks the affected schema update. | `.agents/references/testing.md`, `ROADMAP.md` breaking-change policy | 2026-05-08 | Stable-line breaking-change policy changes. |
| D8 | Execute on one local branch in commit-sized milestones; use delegation only if later execution explicitly splits ownership. | Plan execution shape review | 2026-05-08 | User requests delegated execution or concurrent branches create shared-file conflicts. |

## Execution Shape And Shared Files
- Recommended shape: one local branch, implemented in commit-sized milestones.
- The work is connected through public contract validation, migration sequencing, OpenAPI review, and one final full build, so a single executor is simpler than delegated parallel work.
- Coordinator-owned shared files if later delegation is required:
  - `ROADMAP.md`
  - this plan
  - `src/test/resources/openapi/approved-openapi.json`
  - final validation notes
- Candidate worker boundaries if later delegation is required:
  - Worker A can own user-role behavior and tests.
  - Worker B can own book validation and docs/OpenAPI.
  - Worker C can own category migration and integrity-error handling.
  - Coordinator should integrate shared contract artifacts and final validation.

## Affected Artifacts
- Tests:
  - `AdminUserManagementApiIntegrationTests`
  - `BookApiIntegrationTests`
  - `ApiDocumentationTests` and any feature-specific REST Docs tests affected by book/category docs
  - `OpenApiCompatibilityIntegrationTests` through normal build validation
  - `CategoryApiIntegrationTests` or a focused migration/integration test
  - `ApiExceptionHandlerTests`
  - monitoring validation tests if an existing infrastructure-manifest check exists
- Docs and contract:
  - REST Docs snippets generated by tests
  - `src/docs/asciidoc/book-controller.adoc` only if the human-facing validation description must change
  - `src/docs/asciidoc/category-controller.adoc` only if the case-insensitive uniqueness description must change
  - `src/test/resources/openapi/approved-openapi.json` if schema constraints intentionally change
  - `README.md` only if the supported contract summary starts describing the tightened validation contract
  - `src/manualTests/http/examples/` or `src/manualTests/http/suites/` only if reviewer workflows should exercise the new negative cases
- Source:
  - `business.user`
  - `business.book`
  - `business.category`
  - `technical.api`
  - `infra/k8s/monitoring/prometheus-rule.yaml`
  - new `V11__*.sql` migration plus `metadata/V11__*.json`
- Owning AI-guide updates:
  - None expected unless execution discovers a durable repo-wide lesson or changes standing workflow, testing, documentation, or migration rules.
- Build or benchmark checks:
  - Targeted tests during milestones.
  - `./build.ps1 refreshOpenApiBaseline` only after intentional contract review.
  - Final `./build.ps1 build`.
  - No benchmark by default unless implementation changes book list/search query behavior, localization lookup behavior, or OAuth/session startup behavior.

## Progress Tracker
| Milestone | Status | Owner | Commit | Validation | Notes |
| --- | --- | --- | --- | --- | --- |
| 1: Role-Grant Replacement Safety | Done | Agent | `fix(user): keep bootstrap role grants idempotent` | `./build.ps1 test --tests *AdminUserManagementApiIntegrationTests` passed | Bootstrap ADMIN self-replacement now keeps the bootstrap grant and writes only missing managed roles. |
| 2: Book Write Validation And Contract | Done | Agent | `fix(books): validate write field bounds` | `./build.ps1 test --tests *BookApiIntegrationTests --tests *ApiDocumentationTests --tests *OpenApiCompatibilityIntegrationTests` passed; `./build.ps1 refreshOpenApiBaseline` passed; `./build.ps1 test --tests *OpenApiCompatibilityIntegrationTests` passed | Create/update requests now enforce title/author length, ISBN create length, and publication-year range; REST Docs and OpenAPI baseline are aligned. |
| 3: Category Case-Insensitive Database Uniqueness | Done | Agent | `fix(categories): enforce case-insensitive uniqueness` | `./build.ps1 test --tests *CategoryApiIntegrationTests` passed | Added `V11` preconditioned unique lower-name index plus PostgreSQL integration coverage for direct case-variant duplicates. |
| 4: Integrity Error Wording And Alert Coverage | Done | Agent | `fix(api): neutralize integrity wording and alert books` | `./build.ps1 test --tests *ApiExceptionHandlerTests --tests *PrometheusRuleManifestTests` passed | Generic integrity detail is feature-neutral; auth-failure alert now includes protected book writes and has focused manifest coverage. |
| 5: Contract Refresh And Full Validation | Done | Agent | `chore(plan): complete code review issue fixes` | Combined targeted suite passed; `./build.ps1 -FullBuild build` passed | Final validation and tracking are recorded; release and tagging remain out of scope. |

## Execution Milestones

### Milestone 1: Role-Grant Replacement Safety
| Field | Value |
| --- | --- |
| Status | Done |
| Goal | Make ADMIN role replacement idempotent when requested roles overlap with bootstrap grants. |
| Owned Files Or Packages | `src/main/java/team/jit/technicalinterviewdemo/business/user/`, `src/test/java/team/jit/technicalinterviewdemo/business/user/` |
| Coordinator-Owned Shared Files | None |
| Context Required | `AGENTS.md`, `.agents/references/execution.md`, this plan, `AdminUserManagementApiIntegrationTests`, `UserAccount`, `UserRoleGrant`, `AdminUserManagementService` |
| Behavior To Preserve | Bootstrap grants remain immutable through managed-role replacement; every managed role replacement still requires `USER`, an ADMIN actor, CSRF, and audit logging. |
| Deliverables | 1. Add an integration or service test that replaces roles for the bootstrap admin with `["USER", "ADMIN"]` and verifies no duplicate grant/constraint failure.<br>2. Adjust `replaceManagedRoleGrants()` so roles already satisfied by bootstrap grants are not added again as managed grants.<br>3. Preserve existing provenance behavior for non-bootstrap managed role changes. |
| Validation Checkpoint | `./build.ps1 test --tests *AdminUserManagementApiIntegrationTests` passes, or a narrower user-role service test also passes if added. |
| Commit Checkpoint | `fix(user): keep bootstrap role grants idempotent` |

### Milestone 2: Book Write Validation And Contract
| Field | Value |
| --- | --- |
| Status | Done |
| Goal | Define and enforce public validation for book create/update field lengths and publication-year range. |
| Owned Files Or Packages | `src/main/java/team/jit/technicalinterviewdemo/business/book/`, book API integration/docs tests, OpenAPI baseline if intentionally changed |
| Coordinator-Owned Shared Files | `src/test/resources/openapi/approved-openapi.json` |
| Context Required | `AGENTS.md`, `.agents/references/execution.md`, `.agents/references/documentation.md`, `.agents/references/testing.md`, this plan, `BookApiIntegrationTests`, `ApiDocumentationTests`, `OpenApiCompatibilityIntegrationTests`, `BookCreateRequest`, `BookUpdateRequest`, `Book`, `BookSearchCriteria` |
| Behavior To Preserve | Valid book create/update payloads continue to return the current response shape; ISBN remains immutable on update; duplicate ISBN remains 409; existing read-side filter behavior remains unchanged. |
| Deliverables | 1. Add executable specs for too-long title/author/isbn and out-of-range publication years returning 400 validation problems.<br>2. Add validation annotations or equivalent service validation that matches persistence limits and the existing `0..3000` year policy.<br>3. Update REST Docs snippets and approved OpenAPI only when schema constraints intentionally change.<br>4. Stop and replan the schema/documentation piece if OpenAPI compatibility flags the published constraint change as breaking. |
| Validation Checkpoint | `./build.ps1 test --tests *BookApiIntegrationTests --tests *ApiDocumentationTests --tests *OpenApiCompatibilityIntegrationTests` passes, with approved baseline refresh only after review. |
| Commit Checkpoint | `fix(books): validate write field bounds` |

### Milestone 3: Category Case-Insensitive Database Uniqueness
| Field | Value |
| --- | --- |
| Status | Done |
| Goal | Move category case-insensitive uniqueness from service-only behavior to database-enforced behavior. |
| Owned Files Or Packages | Category tests, Flyway migration SQL, migration metadata, category repository/service only if needed |
| Coordinator-Owned Shared Files | Migration version naming if other migrations are being added concurrently |
| Context Required | `AGENTS.md`, `.agents/references/execution.md`, `.agents/references/documentation.md`, this plan, `src/main/resources/db/migration/metadata/README.md`, `CategoryApiIntegrationTests`, `CategoryService`, current migrations |
| Behavior To Preserve | Duplicate category creates still return the documented 400 problem; existing valid category names remain valid; category lookup performance from the lower-name index is not intentionally regressed. |
| Deliverables | 1. Add `V11__*.sql` to enforce case-insensitive uniqueness, likely by replacing the non-unique `lower(name)` index with a unique equivalent or by using another PostgreSQL mechanism that preserves lookup behavior.<br>2. Add the required migration metadata sidecar.<br>3. Add an integration test proving the database rejects case-variant duplicates or that concurrent/out-of-band duplicate persistence cannot bypass the invariant.<br>4. Preserve or improve API-level handling so ordinary duplicate API requests still return the existing 400 duplicate-category problem rather than a raw 500. |
| Validation Checkpoint | `./build.ps1 test --tests *CategoryApiIntegrationTests` passes, plus any focused migration test added for the invariant. |
| Commit Checkpoint | `fix(categories): enforce case-insensitive uniqueness` |

### Milestone 4: Integrity Error Wording And Alert Coverage
| Field | Value |
| --- | --- |
| Status | Done |
| Goal | Remove misleading book-specific generic integrity wording and ensure auth-failure monitoring covers protected book writes. |
| Owned Files Or Packages | `src/main/java/team/jit/technicalinterviewdemo/technical/api/`, `infra/k8s/monitoring/`, related tests |
| Coordinator-Owned Shared Files | None |
| Context Required | `AGENTS.md`, `.agents/references/execution.md`, `.agents/references/testing.md`, this plan, `ApiExceptionHandlerTests`, `infra/k8s/monitoring/prometheus-rule.yaml`, `SecurityConfiguration`, targeted search for existing infrastructure-manifest tests |
| Behavior To Preserve | Generic integrity errors remain 409 with no sensitive database details in the public response; alert threshold math and existing protected endpoint coverage remain intact. |
| Deliverables | 1. Update the generic data-integrity detail to feature-neutral wording and update tests.<br>2. Extend `TechnicalInterviewDemoAuthenticationFailuresElevated` numerator and denominator to include protected book writes.<br>3. Add or update an infrastructure-manifest test if the repo has one; otherwise document the manifest diff in validation notes. |
| Validation Checkpoint | `./build.ps1 test --tests *ApiExceptionHandlerTests` passes; monitoring manifest is tested or reviewed and recorded in `Validation Results`. |
| Commit Checkpoint | `fix(api): neutralize integrity wording and alert books` |

### Milestone 5: Contract Refresh And Full Validation
| Field | Value |
| --- | --- |
| Status | Done |
| Goal | Integrate all changes, refresh generated contract artifacts intentionally, and prove the maintenance set is green. |
| Owned Files Or Packages | Generated REST Docs/OpenAPI artifacts, validation notes |
| Coordinator-Owned Shared Files | `src/test/resources/openapi/approved-openapi.json`, `ROADMAP.md`, this plan |
| Context Required | `AGENTS.md`, `.agents/references/execution.md`, `.agents/references/testing.md`, `.agents/references/documentation.md`, this plan |
| Behavior To Preserve | Stable `2.x` valid requests stay backward-compatible; invalid-request tightening remains documented as maintenance. |
| Deliverables | 1. Run the targeted tests from previous milestones.<br>2. Run `./build.ps1 refreshOpenApiBaseline` only after reviewing intentional OpenAPI changes.<br>3. Run final `./build.ps1 -FullBuild build`.<br>4. Fill `## Validation Results` with exact commands and outcomes.<br>5. Update the progress tracker with commit and validation evidence. |
| Validation Checkpoint | Final wrapper build passes. |
| Commit Checkpoint | `chore(plan): complete code review issue fixes` |

## Blockers And Replan Triggers
| Trigger / Blocker | Response | Owner | Status |
| --- | --- | --- | --- |
| OpenAPI compatibility reports validation schema tightening as breaking. | Stop before refreshing `approved-openapi.json`; decide whether to keep runtime validation without schema tightening, document the exception as maintenance, or defer the schema piece to a later major-version plan. | Agent/User if policy decision is needed | Resolved; compatibility passed before baseline refresh. |
| A new migration version already exists or another branch reserves `V11`. | Re-scan current migrations, choose the next available version, and update this plan before adding migration files. | Agent | Not triggered; `V11` was available. |
| Category duplicate data already exists in an environment targeted by the migration. | Add a clear precondition/failure message or operator-facing remediation note; do not silently merge or delete categories. | Agent/User if rollout policy is needed | Mitigated by migration precondition; not triggered in local validation. |
| Implementing book validation changes book list/search behavior, localization lookup behavior, or OAuth/session startup. | Revisit benchmark scope and include `./build.ps1 gatlingBenchmark` if the touched path matches `.agents/references/testing.md`. | Agent | Not triggered; benchmark scope stayed out of scope. |
| No suitable automated Prometheus-manifest test home exists. | Record focused manifest review evidence in `Validation Results` and avoid inventing a broad infrastructure test framework for this narrow fix. | Agent | Resolved; focused Prometheus manifest test was added. |

## Edge Cases And Failure Modes
- Bootstrap ADMIN role replacement must not remove the only admin path or create duplicate role-grant keys.
- Book validation must reject invalid input before database persistence and should not convert existing conflict cases into validation failures.
- Category migration can fail if a deployed database already contains case-variant duplicates. The migration should either fail clearly or include an explicit duplicate-detection precondition; do not silently merge categories.
- Adding OpenAPI constraints may be treated as a request-schema tightening. If the compatibility gate reports a breaking change, stop and decide whether the stable-line policy allows it as a bug fix or whether to avoid publishing new schema constraints.
- Prometheus `uri` labels may depend on Micrometer templating; preserve the current label style and only extend endpoint coverage.

## Validation Plan
- Targeted loop commands:
  - `./build.ps1 test --tests *AdminUserManagementApiIntegrationTests`
  - `./build.ps1 test --tests *BookApiIntegrationTests --tests *ApiDocumentationTests --tests *OpenApiCompatibilityIntegrationTests`
  - `./build.ps1 test --tests *CategoryApiIntegrationTests`
  - `./build.ps1 test --tests *ApiExceptionHandlerTests`
- Contract refresh when intentional:
  - `./build.ps1 refreshOpenApiBaseline`
- Final proof:
  - `./build.ps1 -FullBuild build`
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
  - No manual smoke or benchmark required unless execution changes startup/session behavior, reviewer HTTP workflows, or benchmark-covered read paths.
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
| Date | Command | Scope | Result | Notes |
| --- | --- | --- | --- | --- |
| 2026-05-08 | `./build.ps1 build` | Plan and roadmap revision validation | Passed | Wrapper detected only lightweight files changed, skipped Gradle, and reported manual consistency review as sufficient. |
| 2026-05-08 | `./build.ps1 test --tests *AdminUserManagementApiIntegrationTests` | Milestone 1 role-grant replacement safety | Passed | Executed 7 `AdminUserManagementApiIntegrationTests`, including bootstrap admin self-replacement regression. |
| 2026-05-08 | `./build.ps1 test --tests *BookApiIntegrationTests --tests *ApiDocumentationTests --tests *OpenApiCompatibilityIntegrationTests` | Milestone 2 book write validation and pre-refresh compatibility | Passed | Executed 76 tests; new create/update validation specs and REST Docs checks passed; compatibility gate did not classify the schema constraints as breaking. |
| 2026-05-08 | `./build.ps1 refreshOpenApiBaseline` | Milestone 2 approved OpenAPI baseline refresh | Passed | Regenerated `src/test/resources/openapi/approved-openapi.json` after intentional request-schema constraint review. |
| 2026-05-08 | `./build.ps1 test --tests *OpenApiCompatibilityIntegrationTests` | Milestone 2 post-refresh OpenAPI compatibility | Passed | Executed 1 compatibility test against the refreshed baseline. |
| 2026-05-08 | `./build.ps1 test --tests *CategoryApiIntegrationTests` | Milestone 3 category database uniqueness | Passed | Executed 16 category integration tests; Flyway applied `V11` and direct case-variant duplicate persistence was rejected by the database. |
| 2026-05-08 | `./build.ps1 test --tests *ApiExceptionHandlerTests --tests *PrometheusRuleManifestTests` | Milestone 4 integrity wording and alert coverage | Passed | Executed 5 tests; generic integrity detail is feature-neutral and the Prometheus auth-failure alert covers protected book writes. |
| 2026-05-08 | `./build.ps1 test --tests *AdminUserManagementApiIntegrationTests --tests *BookApiIntegrationTests --tests *ApiDocumentationTests --tests *OpenApiCompatibilityIntegrationTests --tests *CategoryApiIntegrationTests --tests *ApiExceptionHandlerTests --tests *PrometheusRuleManifestTests` | Milestone 5 combined targeted regression suite | Passed | Executed 104 tests covering all milestone-specific suites. |
| 2026-05-08 | `./build.ps1 -FullBuild build` | Milestone 5 full validation first attempt | Failed | `spotlessJavaCheck` reported formatting-only violations in `BookApiIntegrationTests`, `AdminUserManagementApiIntegrationTests`, and `ApiDocumentationTests`; no behavioral test failure was reported before the formatting gate. |
| 2026-05-08 | `./build.ps1 spotlessApply` | Milestone 5 formatting remediation | Passed | Applied Palantir/Spotless formatting to the three affected test files. |
| 2026-05-08 | `./build.ps1 -FullBuild build` | Milestone 5 final full validation | Passed | Executed 270 tests; coverage, REST Docs generation, static checks, SBOM, Trivy dependency scan, Docker image build, and Spotless checks passed. |

## User Validation
- Confirm `PUT /api/admin/users/{bootstrapAdminId}/roles` with `["USER", "ADMIN"]` succeeds without duplicate grants.
- Confirm invalid book payloads return 400 validation problems.
- Confirm case-variant category duplicates cannot be stored.
- Confirm generic integrity errors no longer mention books unless the failing operation is actually book-specific.
- Confirm the auth-failure alert expression includes protected book writes.

## Required Content Checklist
- Behavior changing: yes, all five review findings are covered.
- Decision-complete and ready for execution: yes; blocking open questions are `None`.
- Roadmap entry: `ROADMAP.md` Active Release Track row for `Code review issue fixes`.
- Out of scope: documented in `## Scope`.
- Governing specs and contract artifacts: documented in `## Current State` and per milestone.
- Likely source files and packages: documented in `## Affected Artifacts`.
- Compatibility promise: stable `2.x` valid behavior remains backward-compatible; invalid behavior may tighten as maintenance.
- Edge cases, migration, rollout, benchmark risks: documented.
- Requirement gaps: tracked in `## Requirement Gaps And Open Questions`; none block readiness.
- Locked decisions and fallback assumptions: tracked in `## Decision Log And Assumptions`.
- Execution shape: single local branch, with optional split boundaries.
- Coordinator-owned shared files if delegation is realistic: documented in `## Execution Shape And Shared Files` and per milestone.
- Progress tracking: `## Progress Tracker` is present with milestone status, owner, commit, validation, and notes columns.
- Execution-time blockers and replan triggers: documented separately from planning questions.
- Context per milestone: documented with targeted read sets.
- Tests/docs/OpenAPI/HTTP examples/AI guides: documented in `## Affected Artifacts`, milestones, and validation plan.
- Testing strategy: includes unit, integration, contract, smoke/benchmark, and negative-scenario scope.
- Validation proof: targeted tests plus `./build.ps1 -FullBuild build`.
- Validation results location: `## Validation Results`.
- User verification: documented.
