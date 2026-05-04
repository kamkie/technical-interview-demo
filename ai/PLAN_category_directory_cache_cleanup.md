# Plan: Category Directory Cache Cleanup

## Lifecycle
| Field | Value |
| --- | --- |
| Phase | Planning |
| Status | Ready |

## Summary
- Synthetic workflow-fixture plan: clean up the internal category-directory caching and name-normalization flow in `business.category` without changing the `/api/categories` contract or book-category assignment semantics.
- The goal is to make `CategoryService` easier to read and easier to test before any later real category behavior changes land.
- Success is measured by unchanged category API behavior, one clearer service-local test seam, and a worker branch that can run independently inside a four-plan `Parallel Plans` batch.

## Scope
- In scope:
  - service-local cleanup for category-directory cache assembly and name normalization
  - focused service tests under `business.category`
  - direct feature-local refactoring only
- Out of scope:
  - endpoint additions, response-shape changes, authorization changes, or new cache names
  - REST Docs, HTTP examples, README, or OpenAPI edits unless execution uncovers accidental contract drift
  - roadmap, release, or changelog consolidation work

## Current State
- `src/main/java/team/jit/technicalinterviewdemo/business/category/CategoryService.java` currently owns list caching, category-directory caching, name normalization, uniqueness checks, audit logging, and category assignment resolution.
- `resolveForAssignment(...)` normalizes incoming names and then lowercases them again when reading from the cached directory, which keeps the current behavior but leaves the flow harder to scan than it needs to be.
- `getCategoryDirectory()` builds the cached normalized-name-to-id map inline, so cache population and business rules live in the same service body.
- `src/test/java/team/jit/technicalinterviewdemo/business/category/CategoryApiIntegrationTests.java` covers the public `/api/categories` contract, and `src/test/java/team/jit/technicalinterviewdemo/business/category/CategoryDataInitializerTests.java` covers seeded category state.
- The published category contract already lives in `src/docs/asciidoc/category-controller.adoc`, `src/test/resources/http/category-controller.http`, and `src/test/resources/openapi/approved-openapi.json`; those artifacts should remain unchanged for this cleanup.

## Requirement Gaps And Open Questions
- No blocking user-input gaps remain.
- This plan is intentionally synthetic for workflow testing. If execution exposes a real product decision about category ordering, duplicate handling, or assignment error text, stop and move that behavior change into a separate non-dummy plan.
- Fallback assumption: current alphabetical ordering, case-insensitive uniqueness, and missing-category error behavior stay stable.

## Locked Decisions And Assumptions
- Keep the existing cache names and cache keys stable:
  - `CacheNames.CATEGORIES`
  - `CacheNames.CATEGORY_DIRECTORY`
  - `ALL_CATEGORIES_CACHE_KEY`
  - `CATEGORY_DIRECTORY_CACHE_KEY`
- Keep all `/api/categories` response fields, status codes, and current error messages stable.
- Keep the cleanup inside `business.category`; do not introduce a cross-feature cache helper package.
- Prefer adding a focused `CategoryServiceTests` class over broadening a shared technical cache test.
- Treat `src/test/java/team/jit/technicalinterviewdemo/technical/CachingAndMetricsTests.java` as coordinator-owned shared coverage if the broader four-plan batch later needs cross-cutting cache assertions.

## Execution Mode Fit
- Recommended default mode: `Parallel Plans`
- Why that mode fits best: the user explicitly wants a four-plan `Parallel Plans` test, and this slice can stay in `business.category` plus category-local tests while leaving shared cache and contract artifacts alone.
- Private changelog token if executed: `category_directory_cache_cleanup`
- Coordinator-owned or otherwise shared files if the four-plan batch later needs them:
  - `CHANGELOG.md`
  - `README.md`
  - `src/test/resources/openapi/approved-openapi.json`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/CachingAndMetricsTests.java`
- Candidate worker boundary:
  - worker owns `src/main/java/team/jit/technicalinterviewdemo/business/category/**`
  - worker may update `src/test/java/team/jit/technicalinterviewdemo/business/category/**`
  - worker leaves cross-cutting technical cache tests and contract docs to the coordinator unless explicitly reassigned

## Affected Artifacts
- Tests:
  - `src/test/java/team/jit/technicalinterviewdemo/business/category/CategoryApiIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/category/CategoryDataInitializerTests.java`
  - new `src/test/java/team/jit/technicalinterviewdemo/business/category/CategoryServiceTests.java`
- Source files:
  - `src/main/java/team/jit/technicalinterviewdemo/business/category/CategoryService.java`
  - optional new feature-local helper under `src/main/java/team/jit/technicalinterviewdemo/business/category/`
- Likely unchanged contract artifacts:
  - `src/docs/asciidoc/category-controller.adoc`
  - `src/test/resources/http/category-controller.http`
  - `src/test/resources/openapi/approved-openapi.json`
  - `README.md`
- Build checks:
  - `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.business.category.CategoryApiIntegrationTests --tests team.jit.technicalinterviewdemo.business.category.CategoryDataInitializerTests --tests team.jit.technicalinterviewdemo.business.category.CategoryServiceTests`
  - `.\gradlew.bat build`

## Execution Milestones
### Milestone 1: Add Category-Local Service Coverage
- goal
  - create a narrow spec for directory-cache population and category-name normalization before refactoring the service.
- owned files or packages
  - `src/test/java/team/jit/technicalinterviewdemo/business/category/**`
- shared files that a `Shared Plan` worker must leave to the coordinator
  - `src/test/java/team/jit/technicalinterviewdemo/technical/CachingAndMetricsTests.java`
- behavior to preserve
  - alphabetical category ordering, case-insensitive uniqueness, cache-key stability, and current invalid-request messages
- exact deliverables
  - a new `CategoryServiceTests` class or equivalent category-local test seam covering normalized lookup and cache-backed directory behavior
- validation checkpoint
  - `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.business.category.CategoryApiIntegrationTests --tests team.jit.technicalinterviewdemo.business.category.CategoryDataInitializerTests --tests team.jit.technicalinterviewdemo.business.category.CategoryServiceTests`
- commit checkpoint
  - one test-focused milestone commit

### Milestone 2: Simplify Directory Cache And Normalization Flow
- goal
  - reduce the amount of cache assembly and normalization logic living inline in `CategoryService` while keeping the public contract stable.
- owned files or packages
  - `src/main/java/team/jit/technicalinterviewdemo/business/category/**`
  - `src/test/java/team/jit/technicalinterviewdemo/business/category/**`
- shared files that a `Shared Plan` worker must leave to the coordinator
  - `CHANGELOG.md`
  - `README.md`
  - `src/docs/asciidoc/category-controller.adoc`
  - `src/test/resources/http/category-controller.http`
  - `src/test/resources/openapi/approved-openapi.json`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/CachingAndMetricsTests.java`
- behavior to preserve
  - the supported `/api/categories` contract and current book-category assignment semantics
- exact deliverables
  - one clearer service-local flow for normalization and directory lookup
  - no endpoint, authorization, or cache-name changes
  - no public contract artifact edits
- validation checkpoint
  - `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.business.category.CategoryApiIntegrationTests --tests team.jit.technicalinterviewdemo.business.category.CategoryDataInitializerTests --tests team.jit.technicalinterviewdemo.business.category.CategoryServiceTests`
  - `.\gradlew.bat build`
- commit checkpoint
  - one implementation commit with plan, worker log, and private changelog updates if this plan is executed in `Parallel Plans`

## Edge Cases And Failure Modes
- Whitespace-only category names must still be rejected.
- Duplicate names that differ only by case must still fail the same uniqueness checks.
- Missing category names in book assignment must still produce the current invalid-request behavior.
- Cache eviction on create, update, and delete must still clear both the list and directory caches.
- Category resolution for book assignment must still return categories in alphabetical order.

## Validation Plan
- Commands to run:
  - `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.business.category.CategoryApiIntegrationTests --tests team.jit.technicalinterviewdemo.business.category.CategoryDataInitializerTests --tests team.jit.technicalinterviewdemo.business.category.CategoryServiceTests`
  - `.\gradlew.bat build`
- Tests to add or update:
  - new category-local service tests for normalized lookup and cache-directory behavior
  - existing category API tests only where they pin visible behavior that must stay stable
- Docs or contract checks:
  - manually confirm that category REST Docs, HTTP examples, and approved OpenAPI stay unchanged unless execution accidentally changes public behavior
- Manual verification steps:
  - call `GET /api/categories`, create a category, rename it, and delete it, then confirm ordering and duplicate-name behavior remain unchanged

## Better Engineering Notes
- Keep the cleanup feature-local. The repo does not need a general-purpose cache directory framework.
- Prefer a small helper or a couple of private methods over introducing a second service.
- Leave cross-cutting cache metrics coverage to the coordinator if the broader `Parallel Plans` test needs one shared assertion point.

## Validation Results
- Not started. Planning-only dummy plan.

## User Validation
- Call `GET /api/categories`, then create, update, and delete a category as an admin and confirm ordering, duplicate-name handling, and error behavior remain unchanged.
