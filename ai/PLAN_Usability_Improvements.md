# Plan: Demo API Usability Improvements

## Summary
- Add additive, ADMIN-only usability capabilities without breaking any existing endpoint or payload: a read-only audit log review API and full category maintenance semantics.
- This work matters because audit history is currently only reviewable through the database or integration tests, and category management is create-only even though books already depend on category assignments.
- Success is measured by: reviewers being able to inspect recorded audit entries through the API, admins being able to rename and delete unused categories safely, all existing contract behavior remaining backward-compatible, published contract artifacts staying aligned, and `.\gradlew.bat build` passing.

## Scope
- In scope:
  - add an ADMIN-only `GET /api/audit-logs` endpoint that returns existing audit log records in a paginated JSON response
  - support audit-log review with Spring pagination parameters plus exact-match filters for `targetType`, `action`, and `actorLogin`
  - expose audit-log response fields as `id`, `targetType`, `targetId`, `action`, `actorLogin`, `summary`, and `createdAt`
  - add ADMIN-only `PUT /api/categories/{id}` semantics for renaming an existing category
  - add ADMIN-only `DELETE /api/categories/{id}` semantics for deleting an unused category
  - reject category deletion when the category is still assigned to any book, using a localized API error instead of a raw database failure
  - update tests, REST Docs, OpenAPI, HTTP examples, and human-facing contract docs for any additive supported endpoint surface introduced by this work
- Out of scope:
  - changing the behavior or payload shapes of existing `GET /api/categories`, `POST /api/categories`, `GET /api/books`, `GET /api/localizations`, `GET /api/account`, `/`, `/hello`, or documentation endpoints
  - category merge, bulk reassignment, or automatic book rewrites during category deletion
  - CSV/export-file audit delivery, audit-log mutation endpoints, or audit retention policy work
  - schema migrations unless execution uncovers an unavoidable persistence gap that cannot be handled with the existing tables
  - benchmark work beyond verifying that no benchmark-sensitive behavior changed

## Current State
- Current behavior:
  - audit entries are written for book and localization create/update/delete operations through `AuditLogService`, but they are only verified in tests or by querying the database directly
  - category management is currently create-only: `GET /api/categories` lists ordered categories and `POST /api/categories` creates them; there is no update or delete contract today
  - the category-book relationship is stored in `book_categories`, so deleting an assigned category without explicit validation would fail at the database layer rather than through a deliberate API rule
- Current constraints:
  - `SecurityConfiguration` currently permits `GET /api/**` by default, so any new admin-only audit read path must be matched before that generic public-read rule
  - the repository’s public contract is frozen around small, readable demo APIs, so this work must stay direct and avoid introducing a broader admin platform
  - existing category list and book response payloads should remain unchanged to preserve the current supported contract
- Relevant existing specs and code:
  - roadmap scope anchor: `ROADMAP.md` under `Later: Demo API Usability Improvements`
  - human-facing supported contract: `README.md`
  - generated-doc contract index: `src/docs/asciidoc/index.adoc`
  - current category contract docs: `src/docs/asciidoc/category-controller.adoc`
  - current category HTTP examples: `src/test/resources/http/category-controller.http`
  - current approved machine-readable contract: `src/test/resources/openapi/approved-openapi.json`
  - category executable specs: `src/test/java/team/jit/technicalinterviewdemo/business/category/CategoryApiIntegrationTests.java` and `src/test/java/team/jit/technicalinterviewdemo/business/category/CategoryApiDocumentationTests.java`
  - audit logging executable specs: `src/test/java/team/jit/technicalinterviewdemo/business/audit/AuditLogIntegrationTests.java`
  - OpenAPI contract gates: `src/test/java/team/jit/technicalinterviewdemo/technical/docs/OpenApiIntegrationTests.java` and `src/test/java/team/jit/technicalinterviewdemo/technical/docs/OpenApiCompatibilityIntegrationTests.java`
  - controller/DTO boundary guard: `src/test/java/team/jit/technicalinterviewdemo/technical/ArchitectureHardeningTests.java`
  - current implementation seams: `src/main/java/team/jit/technicalinterviewdemo/business/category/CategoryController.java`, `src/main/java/team/jit/technicalinterviewdemo/business/category/CategoryService.java`, `src/main/java/team/jit/technicalinterviewdemo/business/category/CategoryRepository.java`, `src/main/java/team/jit/technicalinterviewdemo/business/audit/AuditLog.java`, `src/main/java/team/jit/technicalinterviewdemo/business/audit/AuditLogRepository.java`, `src/main/java/team/jit/technicalinterviewdemo/business/audit/AuditLogService.java`, `src/main/java/team/jit/technicalinterviewdemo/business/book/BookRepository.java`, and `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecurityConfiguration.java`

## Locked Decisions And Assumptions
- Preserve the current public contract by keeping all existing endpoints, request parameters, response fields, error shapes, and security expectations backward-compatible.
- Treat this work as additive contract expansion only: new usability endpoints may be added, but no existing supported behavior may be removed or silently redefined.
- Choose a JSON API for audit review instead of an export file because the repository already standardizes on Spring MVC + REST Docs + OpenAPI for reviewer-facing behavior.
- Keep the audit review surface read-only and ADMIN-only. No audit record edits, deletes, or replay semantics belong in this plan.
- Use validation-based category deletion semantics instead of automatic reassignment. If a category is still referenced by any book, deletion must fail cleanly and leave book data unchanged.
- Introduce explicit category not-found and category-in-use API behavior for `PUT`/`DELETE` instead of relying on generic `DataIntegrityViolationException` handling.
- Keep the response boundary DTO-based; controllers must not expose `AuditLog` or `Category` JPA entities directly.
- `README.md`, REST Docs, OpenAPI, and HTTP examples are affected because this plan intentionally adds supported API surface, but the existing documented contract must remain unchanged.
- Benchmarks are not affected. This work does not change book list/search behavior, localization lookup behavior, or OAuth/session startup behavior, so `.\gradlew.bat gatlingBenchmark` is not required.

## Affected Artifacts
- Tests:
  - `src/test/java/team/jit/technicalinterviewdemo/business/category/CategoryApiIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/category/CategoryApiDocumentationTests.java`
  - new `src/test/java/team/jit/technicalinterviewdemo/business/audit/AuditLogApiIntegrationTests.java`
  - new `src/test/java/team/jit/technicalinterviewdemo/business/audit/AuditLogApiDocumentationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/audit/AuditLogIntegrationTests.java` to preserve current write-side audit behavior
  - `src/test/java/team/jit/technicalinterviewdemo/technical/docs/OpenApiIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/docs/OpenApiCompatibilityIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/ArchitectureHardeningTests.java`
- Docs:
  - `README.md` is affected: yes, because the supported endpoint list and security summary must include any approved additive admin endpoints
  - `src/docs/asciidoc/index.adoc` is affected: yes, because it links the generated API pages and summarizes the supported contract tiers
  - `src/docs/asciidoc/category-controller.adoc` is affected: yes, because category management will no longer be create-only
  - new `src/docs/asciidoc/audit-log-controller.adoc` is affected: yes, because the audit review API needs its own generated-doc page
- OpenAPI:
  - `src/test/resources/openapi/approved-openapi.json` is affected: yes, but refresh only after intentional contract review of the additive endpoint diff
- HTTP examples:
  - `src/test/resources/http/category-controller.http` is affected: yes
  - new `src/test/resources/http/audit-log-controller.http` is affected: yes
- Source files:
  - `src/main/java/team/jit/technicalinterviewdemo/business/category/CategoryController.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/category/CategoryService.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/category/CategoryRepository.java`
  - likely new `src/main/java/team/jit/technicalinterviewdemo/business/category/CategoryUpdateRequest.java`
  - likely new `src/main/java/team/jit/technicalinterviewdemo/business/category/CategoryNotFoundException.java`
  - likely new `src/main/java/team/jit/technicalinterviewdemo/business/category/CategoryInUseException.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/book/BookRepository.java`
  - likely new `src/main/java/team/jit/technicalinterviewdemo/business/audit/AuditLogController.java`
  - likely new `src/main/java/team/jit/technicalinterviewdemo/business/audit/AuditLogQueryService.java`
  - likely new `src/main/java/team/jit/technicalinterviewdemo/business/audit/AuditLogResponse.java`
  - likely new `src/main/java/team/jit/technicalinterviewdemo/business/audit/AuditLogSearchRequest.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/audit/AuditLogRepository.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecurityConfiguration.java`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/api/ApiExceptionHandler.java`
- Build or benchmark checks:
  - `.\gradlew.bat build` is required
  - `.\gradlew.bat refreshOpenApiBaseline` is required only after reviewing and accepting the intentional OpenAPI diff
  - `.\gradlew.bat gatlingBenchmark` is not required

## Execution Milestones
### Milestone 1: Specify And Add Audit Log Review API
- goal:
  - define a small, additive audit-review contract that lets admins inspect recorded changes without direct database access
- files to update:
  - new audit API integration and REST Docs tests
  - new audit HTTP example file and Asciidoc page
  - `README.md`, `src/docs/asciidoc/index.adoc`, `src/test/resources/openapi/approved-openapi.json`, and OpenAPI integration tests
  - audit source classes and `SecurityConfiguration`
- behavior to preserve:
  - existing audit-write behavior for books and localizations must stay unchanged
  - public `GET /api/**` behavior must remain unchanged for existing endpoints while the new audit path stays ADMIN-only
  - no JPA entities may leak through controller return types
- exact deliverables:
  - `GET /api/audit-logs` returning a pageable response of audit DTOs with default newest-first ordering
  - exact-match filter semantics for `targetType`, `action`, and `actorLogin`
  - `401` for missing session and `403` for authenticated non-admin access using the existing localized `ProblemDetail` shape
  - generated docs, HTTP examples, and OpenAPI entries for the new endpoint

### Milestone 2: Extend Category Management Beyond Create-Only
- goal:
  - add explicit rename and delete semantics for categories so maintainers no longer need direct database intervention for common cleanup tasks
- files to update:
  - category integration tests, documentation tests, REST Docs page, HTTP examples, and controller/service/repository classes
  - `BookRepository.java` or an equivalent repository seam for category-usage checks
  - `ApiExceptionHandler.java` and any new category exceptions needed for localized `404` or `409` behavior
  - `SecurityConfiguration.java`, `README.md`, OpenAPI integration tests, and the approved baseline
- behavior to preserve:
  - `GET /api/categories` response shape and ordering remain unchanged
  - `POST /api/categories` behavior, auth rules, and duplicate-name handling remain unchanged
  - existing book assignment behavior stays intact; category rename updates the visible category name through existing book reads because the association is by category id
- exact deliverables:
  - `PUT /api/categories/{id}` for ADMIN-only category rename with trimmed, case-insensitive uniqueness validation
  - `DELETE /api/categories/{id}` for ADMIN-only deletion of unused categories
  - localized `404` when the category id does not exist
  - localized `409` when a category is still assigned to one or more books
  - category docs/examples/OpenAPI updates that make the new semantics explicit and keep the current endpoints intact

### Milestone 3: Align Contract Artifacts And Final Validation
- goal:
  - finish the additive contract review, refresh published artifacts, and prove the repository remains green end-to-end
- files to update:
  - `README.md`
  - `src/docs/asciidoc/index.adoc`
  - `src/docs/asciidoc/category-controller.adoc`
  - new `src/docs/asciidoc/audit-log-controller.adoc`
  - `src/test/resources/http/category-controller.http`
  - new `src/test/resources/http/audit-log-controller.http`
  - `src/test/resources/openapi/approved-openapi.json`
  - relevant OpenAPI and architecture hardening tests
- behavior to preserve:
  - existing supported endpoints and payloads remain backward-compatible
  - no benchmark-sensitive behavior changes are introduced incidentally
- exact deliverables:
  - additive supported-contract text in `README.md` and docs index
  - reviewed and refreshed OpenAPI baseline
  - updated validation notes in this plan once execution is complete

## Edge Cases And Failure Modes
- If `SecurityConfiguration` leaves the generic `GET /api/**` matcher ahead of the new audit path matcher, `GET /api/audit-logs` will be exposed publicly. That is a blocking regression.
- If the audit endpoint returns `AuditLog` directly, it can expose lazy-loading behavior and internal fields that are not part of a stable API contract.
- If category rename normalizes whitespace and case differently across create/update flows, the API will become inconsistent. The same normalization and uniqueness rules must apply to both.
- If category deletion relies on raw database constraint failure instead of an explicit usage check, users will get a generic conflict instead of a deliberate, localized rule.
- If category delete mutates books automatically, the work stops being a narrow usability improvement and becomes broader data-management behavior. That is intentionally out of scope.
- If audit filters or sort handling silently ignore unsupported values, reviewers will get misleading results. Unsupported filter or sort inputs should fail with localized client errors.
- OpenAPI compatibility will fail until the approved baseline is refreshed after contract review; that failure is expected during implementation and should not be treated as optional cleanup.

## Validation Plan
- Commands to run during execution:
  - `.\gradlew.bat test --tests "*AuditLogIntegrationTests" --tests "*AuditLogApiIntegrationTests" --tests "*AuditLogApiDocumentationTests" --tests "*CategoryApiIntegrationTests" --tests "*CategoryApiDocumentationTests" --tests "*OpenApiIntegrationTests" --tests "*ArchitectureHardeningTests"`
  - review the intentional OpenAPI diff for the new audit-log and category-management operations
  - `.\gradlew.bat refreshOpenApiBaseline`
  - `.\gradlew.bat test --tests "*OpenApiCompatibilityIntegrationTests"`
  - `.\gradlew.bat build`
- Tests to add or update:
  - add integration tests for admin, non-admin, and unauthenticated access to `GET /api/audit-logs`
  - add integration tests for audit pagination/filter semantics and newest-first ordering
  - update category integration tests to cover rename success, delete success for an unused category, delete rejection for an in-use category, missing-category `404`, and duplicate-name `400` on update
  - update REST Docs tests so the generated snippets exist for the new and changed operations
  - update OpenAPI integration tests to assert the new paths, security requirements, and schemas
  - update architecture hardening tests so the new controllers still return DTOs rather than entities
- Docs or contract checks:
  - verify `README.md`, REST Docs pages, HTTP examples, and OpenAPI all describe the same additive endpoint surface and security posture
  - confirm the approved OpenAPI baseline changes only for the intentional new operations and schemas
- Manual verification steps:
  - call `GET /api/categories` before and after the change and confirm the response field set stays `id` + `name`
  - rename a category through `PUT /api/categories/{id}` and confirm an existing `GET /api/books/{id}` response reflects the new category name without any other payload changes
  - delete an unused category and confirm the API returns the planned success status
  - attempt to delete a category still assigned to a book and confirm a localized `409` problem response
  - call `GET /api/audit-logs` without a session, with a non-admin session, and with an admin session to confirm `401`, `403`, and successful list behavior respectively
  - verify `/docs` and `/v3/api-docs` include the new operations after docs/OpenAPI generation

## Better Engineering Notes
- A read-only JSON audit API is the smallest clean solution here. It reuses the repository’s existing contract machinery and avoids inventing export formats, file-delivery concerns, or admin UI work.
- Rejecting deletion of in-use categories is intentionally smaller and safer than auto-reassignment. If the project later needs category merge or replacement flows, that should be a separate plan with explicit book-update semantics.
- If execution discovers that audit review needs more than exact filters and pagination to be useful, stop and split that into a follow-up plan rather than quietly expanding this one into a reporting subsystem.

## Validation Results
- Milestone 1:
  - `.\gradlew.bat test --tests "*AuditLogIntegrationTests" --tests "*AuditLogApiIntegrationTests" --tests "*AuditLogApiDocumentationTests" --tests "*OpenApiIntegrationTests" --tests "*ArchitectureHardeningTests"`: passed after running Gradle with `JAVA_HOME=C:\Users\kamki\.jdks\azul-25.0.3`
  - Initial attempt to run the same Gradle test command with the workstation-default JVM failed before executing tests because Gradle started on Java 11 and this repository now requires Java 17+ to run Gradle itself.
  - `.\gradlew.bat refreshOpenApiBaseline --args "D:\Projects\Jit\technical-interview-demo-a2\build\tmp\openapi-audit.json"`: passed as a temporary inspection step to confirm the generated audit-log OpenAPI shape without refreshing the approved baseline yet
- Milestone 2:
  - `.\gradlew.bat test --tests "*CategoryApiIntegrationTests" --tests "*CategoryApiDocumentationTests" --tests "*LocalizationApiIntegrationTests" --tests "*ApiExceptionHandlerTests" --tests "*OpenApiIntegrationTests" --tests "*ArchitectureHardeningTests"`: passed after running Gradle with `JAVA_HOME=C:\Users\kamki\.jdks\azul-25.0.3`
  - The first run of the same Milestone 2 command surfaced three implementation gaps and one collateral test drift: category update/delete routes were not yet authenticated in `SecurityConfiguration`, the category tests/docs were seeding categories without books so the in-use delete path never triggered, and the expanded localization seed set exceeded the default page size in one language-filter test.
  - After tightening security, switching the category fixtures to `BookCatalogTestData.seedDefaultCatalog(...)`, and requesting a larger page size in the localization language-filter test, the same validation command passed cleanly.
- Milestone 3:
  - `.\gradlew.bat refreshOpenApiBaseline`: passed after the additive audit-log and category-management contract changes were reviewed, using `JAVA_HOME=C:\Users\kamki\.jdks\azul-25.0.3`
  - `.\gradlew.bat test --tests "*OpenApiCompatibilityIntegrationTests"`: passed with `JAVA_HOME=C:\Users\kamki\.jdks\azul-25.0.3`
  - `.\gradlew.bat build`: passed with `JAVA_HOME=C:\Users\kamki\.jdks\azul-25.0.3`, including the full test suite, Asciidoctor generation, PMD/Spotless checks, vulnerability scans, Docker image build, and overall Gradle `build` verification

## User Validation
- Start the application and authenticate as an admin-capable user.
- Run the category HTTP examples to create, rename, and delete a category, then verify existing category list and book-read payloads remain unchanged apart from the renamed category value.
- Run the audit HTTP example and confirm recent book or localization writes appear with `targetType`, `action`, `actorLogin`, `summary`, and `createdAt`.
- Open `/docs` and confirm the category page now includes update/delete operations and the new audit-log page is linked from the index.


