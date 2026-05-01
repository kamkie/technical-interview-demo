# Plan: Stabilize API Response Boundaries And Auth Error Payloads

## Summary
- Replace direct `Book` and `Category` entity responses in public controllers with explicit API response models while preserving the current JSON payload shape.
- Standardize API `401` and security-originated `403` responses so they use the same localized `ProblemDetail` style already used for other API errors.
- Measure success by keeping existing success-path payloads stable, documenting the auth error contract in REST Docs/OpenAPI/HTTP examples, and finishing with a green `.\gradlew.bat build`.

## Scope
- In scope:
  - `BookController` and `CategoryController` response-boundary hardening
  - response DTOs for book/category payloads
  - shared API-problem construction for MVC exceptions and security entry points
  - documented `401` and `403` error responses for protected API endpoints
  - README, REST Docs, HTTP examples, and OpenAPI updates required by the auth-error contract change
- Out of scope:
  - changing book/category field names, pagination semantics, sorting, filtering, or write behavior
  - changing authentication requirements, admin rules, session storage, OAuth flow, or CSRF posture
  - redesigning `Localization` or account responses, which already use dedicated API models
  - benchmark work; this change does not alter book search behavior, localization lookup behavior, or session startup behavior

## Current State
- `BookController` currently returns `Page<Book>` and `Book` directly from public endpoints, and `CategoryController` returns `List<Category>` and `Category` directly. The persistence entities also carry API-facing `@Schema` metadata.
- `LocalizationController` and `UserAccountController` already return dedicated response models (`LocalizationResponse`, `UserAccountResponse`), so the repository already has a preferred API-boundary pattern.
- `ApiExceptionHandler` already produces localized `ProblemDetail` payloads with `messageKey`, `message`, and `language` for MVC-layer errors, including service-thrown `AccessDeniedException` cases.
- `SecurityConfiguration` currently uses `HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)` for `/api/**`, so unauthenticated API requests return a bare `401` instead of the documented problem payload. There is no custom security `AccessDeniedHandler` for security-layer denials.
- Current governing spec artifacts:
  - `src/test/java/team/jit/technicalinterviewdemo/business/book/BookApiIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/category/CategoryApiIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/localization/LocalizationApiIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/user/UserManagementIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/category/CategoryApiDocumentationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/docs/ApiDocumentationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/docs/OpenApiIntegrationTests.java`
  - `src/test/resources/openapi/approved-openapi.json`
  - `src/docs/asciidoc/index.adoc`
  - `src/docs/asciidoc/book-controller.adoc`
  - `src/docs/asciidoc/category-controller.adoc`
  - `src/test/resources/http/category-controller.http`
  - `src/test/resources/http/user-account-controller.http`
  - `README.md`

## Locked Decisions And Assumptions
- Treat the controller/entity separation as an internal hardening change. Preserve the current external JSON fields for book and category responses unless execution discovers an existing contract defect that must be fixed separately.
- Keep the change small and direct: introduce explicit response DTOs with `from(...)` mapping methods. Do not add a generic mapper layer or external mapping library.
- Preserve current OpenAPI schema names and descriptions for book/category success payloads if Springdoc allows it cleanly through DTO schema metadata. Avoid unnecessary public-contract churn from schema renaming.
- Prefer controller-boundary mapping first. If lazy category access breaks during execution, move mapping into the service transaction or fetch the association explicitly; do not switch entity relationships to eager loading as a shortcut.
- Add one new localization key for unauthorized API access, assumed to be `error.request.unauthorized`, with localized seed messages alongside the existing `error.request.forbidden` set.
- Document `401` and `403` using `POST /api/categories` because that endpoint exercises both unauthenticated and authenticated-but-not-admin paths without duplicating multiple controller pages.
- OpenAPI baseline refresh is expected for the auth-error contract update after reviewing the generated diff. It should not be refreshed just because book/category DTOs replace entities internally.
- Benchmark reruns are not required.

## Affected Artifacts
- Tests:
  - `src/test/java/team/jit/technicalinterviewdemo/technical/ArchitectureHardeningTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/book/BookApiIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/category/CategoryApiIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/user/UserManagementIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/category/CategoryApiDocumentationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/api/ApiExceptionHandlerTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/docs/OpenApiIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/docs/OpenApiCompatibilityIntegrationTests.java`
- Docs:
  - `src/docs/asciidoc/index.adoc`
  - `src/docs/asciidoc/category-controller.adoc`
  - `README.md`
- OpenAPI:
  - `src/test/resources/openapi/approved-openapi.json`
- HTTP examples:
  - `src/test/resources/http/category-controller.http`
  - `src/test/resources/http/user-account-controller.http`
- Source files:
  - `src/main/java/team/jit/technicalinterviewdemo/business/book/BookController.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/category/CategoryController.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/book/Book.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/category/Category.java`
  - new response-model files under `src/main/java/team/jit/technicalinterviewdemo/business/book/`
  - new response-model files under `src/main/java/team/jit/technicalinterviewdemo/business/category/`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/api/ApiExceptionHandler.java`
  - new shared problem-construction support under `src/main/java/team/jit/technicalinterviewdemo/technical/api/`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecurityConfiguration.java`
  - new security error handlers under `src/main/java/team/jit/technicalinterviewdemo/technical/security/`
  - `src/main/java/team/jit/technicalinterviewdemo/business/localization/seed/LocalizationSeedData.java`
- Build or benchmark checks:
  - `.\gradlew.bat build`
  - no benchmark rerun expected

## Execution Milestones
### Milestone 1: Lock The Specs First
- Goal: define the intended behavior in executable specs before changing implementation.
- Files to update:
  - `src/test/java/team/jit/technicalinterviewdemo/technical/ArchitectureHardeningTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/category/CategoryApiIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/user/UserManagementIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/category/CategoryApiDocumentationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/docs/OpenApiIntegrationTests.java`
- Behavior to preserve:
  - existing book/category success payload fields and pagination metadata
  - existing forbidden payload semantics for service-thrown role checks
- Exact deliverables:
  - add a structural test that public controller methods no longer expose JPA entity types for book/category responses
  - add an integration test proving unauthenticated API access returns a localized `ProblemDetail` body instead of an empty `401`
  - keep or extend a forbidden integration test so it asserts the standardized problem fields
  - add REST Docs snippets for unauthorized and forbidden category writes
  - add or tighten OpenAPI assertions for documented `401`/`403` responses on secured operations

### Milestone 2: Replace Entity Responses With Explicit API Models
- Goal: decouple public controller signatures and OpenAPI schemas from persistence entities without changing the supported payload shape.
- Files to update:
  - `src/main/java/team/jit/technicalinterviewdemo/business/book/BookController.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/category/CategoryController.java`
  - new response-model files under `src/main/java/team/jit/technicalinterviewdemo/business/book/`
  - new response-model files under `src/main/java/team/jit/technicalinterviewdemo/business/category/`
  - `src/main/java/team/jit/technicalinterviewdemo/business/book/Book.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/category/Category.java`
- Behavior to preserve:
  - field names, category ordering, pagination structure, and status codes
  - current create/update/delete semantics
- Exact deliverables:
  - introduce explicit response types for `Book` and `Category`
  - map controller results to those response types for list/get/create/update flows
  - move API-facing schema documentation from the JPA entities to the response types
  - keep services and repositories as small as possible unless lazy-loading behavior forces a targeted adjustment

### Milestone 3: Unify Security And MVC Error Rendering
- Goal: ensure API `401` and security-originated `403` responses use the same localized problem format as the rest of the API.
- Files to update:
  - `src/main/java/team/jit/technicalinterviewdemo/technical/api/ApiExceptionHandler.java`
  - new shared problem-construction support under `src/main/java/team/jit/technicalinterviewdemo/technical/api/`
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecurityConfiguration.java`
  - new security error handlers under `src/main/java/team/jit/technicalinterviewdemo/technical/security/`
  - `src/main/java/team/jit/technicalinterviewdemo/business/localization/seed/LocalizationSeedData.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/api/ApiExceptionHandlerTests.java`
- Behavior to preserve:
  - current localized error fields and logging discipline for existing MVC exceptions
  - current role-check detail messages such as `Category management requires the ADMIN role.`
  - browser/OAuth behavior outside `/api/**`
- Exact deliverables:
  - extract a shared factory/helper that builds localized `ProblemDetail` responses
  - wire a custom `AuthenticationEntryPoint` for `/api/**` that returns JSON `401` problem payloads
  - wire a custom `AccessDeniedHandler` for `/api/**` so security-layer `403` responses also use the same payload style
  - add localized seed messages for unauthorized access

### Milestone 4: Update Published Contract Artifacts
- Goal: align human-facing and machine-readable docs with the intentional auth-error contract change.
- Files to update:
  - `src/docs/asciidoc/index.adoc`
  - `src/docs/asciidoc/category-controller.adoc`
  - `src/test/resources/http/category-controller.http`
  - `src/test/resources/http/user-account-controller.http`
  - `README.md`
  - `src/test/resources/openapi/approved-openapi.json`
- Exact deliverables:
  - document `401` and `403` examples for `POST /api/categories`
  - mention in the overview docs/README that protected API endpoints return the same localized `ProblemDetail` structure as other errors
  - add reviewer-facing HTTP examples for unauthenticated and forbidden access
  - refresh the approved OpenAPI baseline only after reviewing the intentional additions for auth-error responses

## Edge Cases And Failure Modes
- `Page<Book>` mapping must preserve pageable metadata exactly; only the `content` element type should change internally.
- Book category ordering must remain alphabetical in both list and single-resource responses.
- If DTO mapping touches lazy `categories` after the transaction closes, fix the mapping location or fetch plan. Do not solve this by broad eager-loading.
- Unauthorized responses must stay JSON for `/api/**` even when OAuth login is configured; API callers should not receive HTML redirects.
- Forbidden responses must preserve localized `message`, `messageKey`, and `language`, including the authenticated-user preferred-language fallback already exercised in `UserManagementIntegrationTests`.
- The new `error.request.unauthorized` localization must also work when no authenticated user exists; language resolution then falls back to request overrides/cookies/English.
- OpenAPI schema churn is a compatibility risk. DTO adoption should not rename success schemas unless that rename is consciously accepted and reviewed.

## Validation Plan
- Run targeted tests while implementing:
  - `.\gradlew.bat test --tests "team.jit.technicalinterviewdemo.technical.ArchitectureHardeningTests"`
  - `.\gradlew.bat test --tests "team.jit.technicalinterviewdemo.business.book.BookApiIntegrationTests"`
  - `.\gradlew.bat test --tests "team.jit.technicalinterviewdemo.business.category.CategoryApiIntegrationTests"`
  - `.\gradlew.bat test --tests "team.jit.technicalinterviewdemo.business.user.UserManagementIntegrationTests"`
  - `.\gradlew.bat test --tests "team.jit.technicalinterviewdemo.business.category.CategoryApiDocumentationTests"`
  - `.\gradlew.bat test --tests "team.jit.technicalinterviewdemo.technical.api.ApiExceptionHandlerTests"`
  - `.\gradlew.bat test --tests "team.jit.technicalinterviewdemo.technical.docs.OpenApiIntegrationTests"`
  - `.\gradlew.bat test --tests "team.jit.technicalinterviewdemo.technical.docs.OpenApiCompatibilityIntegrationTests"`
- After reviewing the generated OpenAPI diff, refresh the baseline intentionally with:
  - `.\gradlew.bat refreshOpenApiBaseline`
- Final verification:
  - `.\gradlew.bat build`
- Manual verification steps:
  - call `POST /api/categories` without a session and confirm `401` JSON includes `title`, `status`, `detail`, `messageKey`, `message`, and `language`
  - call `POST /api/categories` with a non-admin session and confirm `403` JSON uses the same field set and preserves the role-specific detail message
  - call `GET /api/books` and `GET /api/categories` and confirm success payload fields are unchanged
- Benchmark/docs notes:
  - no benchmark rerun required
  - REST Docs and OpenAPI compatibility checks are required and treated as contract gates

## Better Engineering Notes
- Add the controller-boundary hardening test as part of this work so the repository does not regress back to exposing JPA entities from public controllers.
- Keep the shared error-rendering helper small and focused on problem creation/logging. Do not turn this into a generic framework layer.
- If execution shows that more controllers still expose persistence entities after book/category are fixed, stop and split that wider cleanup into a follow-up plan instead of expanding this task silently.

## Validation Results
- To be filled in during execution.

## User Validation
- Verify one success-path response and two auth-error responses:
  - `GET /api/books` still returns the same book fields and pagination envelope.
  - `GET /api/categories` still returns `id` and `name` only.
  - `POST /api/categories` without a session now returns a localized `401` problem payload.
  - `POST /api/categories` with a non-admin session returns a localized `403` problem payload with the same field structure.
