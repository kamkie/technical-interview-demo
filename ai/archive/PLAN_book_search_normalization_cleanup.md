# Plan: Book Search Normalization Cleanup

## Lifecycle
| Field | Value |
| --- | --- |
| Phase | Closed |
| Status | Released |

## Summary
- Synthetic workflow-fixture plan: clean up the internal search-filter normalization path in `business.book` without changing the `GET /api/books` contract.
- The goal is to make `BookService` and `BookSearchSpecifications` easier to reason about before any later real search changes land.
- Success is measured by unchanged book API behavior, tighter module-local tests for normalization boundaries, and a branch that can execute independently as one `Parallel Plans` worker.

## Scope
- In scope:
  - internal normalization and validation flow for `BookSearchRequest`
  - package-local helper extraction inside `business.book` if it materially reduces split responsibility
  - targeted test additions or reshaping in `business.book`
- Out of scope:
  - new query parameters, new sort aliases, pagination changes, or response-shape changes
  - REST Docs, HTTP examples, README, or OpenAPI edits unless execution uncovers accidental contract drift
  - roadmap, release, or changelog consolidation work

## Current State
- `src/main/java/team/jit/technicalinterviewdemo/business/book/BookSearchRequest.java` exposes raw nullable title, author, ISBN, year, and category filters.
- `src/main/java/team/jit/technicalinterviewdemo/business/book/BookService.java` currently owns filter validation such as ISBN-character checks, reversed year-range rejection, category-count limits, and supported sort aliases.
- `src/main/java/team/jit/technicalinterviewdemo/business/book/BookSearchSpecifications.java` trims and lowercases text filters, deduplicates category names, and assembles the JPA `Specification`.
- `src/test/java/team/jit/technicalinterviewdemo/business/book/BookServiceTests.java` already pins several invalid-input cases, and `src/test/java/team/jit/technicalinterviewdemo/business/book/BookApiIntegrationTests.java` covers end-to-end list behavior.
- The current book contract is already published through `src/docs/asciidoc/book-controller.adoc`, `src/test/resources/http/book-controller.http`, and `src/test/resources/openapi/approved-openapi.json`; those artifacts should remain unchanged for this cleanup.

## Requirement Gaps And Open Questions
- No blocking user-input gaps remain.
- This plan is intentionally synthetic for workflow testing. If execution exposes a real product decision about search semantics, stop and promote that behavior change into a separate non-dummy plan.
- Fallback assumption: blank and duplicate text or category filters keep their current behavior; the cleanup may move code, but it must not reinterpret inputs.

## Locked Decisions And Assumptions
- Keep all `GET /api/books` request parameters, sort aliases, pagination, and current error messages stable.
- Keep validation and normalization inside the book feature package; do not move search parsing into `technical.*`.
- Prefer a small package-private helper or tighter private methods over a new shared abstraction layer.
- Treat `src/docs/asciidoc/book-controller.adoc`, `src/test/resources/http/book-controller.http`, `src/test/resources/openapi/approved-openapi.json`, and `README.md` as unchanged unless execution proves observable behavior drift.
- Because this is a workflow fixture, `ROADMAP.md`, `CHANGELOG.md`, and release artifacts stay untouched during planning.

## Execution Mode Fit
- Recommended default mode: `Parallel Plans`
- Why that mode fits best: the user explicitly wants a four-plan `Parallel Plans` test, and this slice can stay inside `business.book` plus feature-local tests without overlapping the other dummy plans.
- Private changelog token if executed: `book_search_normalization_cleanup`
- Coordinator-owned or otherwise shared files if the four-plan batch later needs them:
  - `CHANGELOG.md`
  - `README.md`
  - `src/test/resources/openapi/approved-openapi.json`
- Candidate worker boundary:
  - worker owns `src/main/java/team/jit/technicalinterviewdemo/business/book/**`
  - worker may update `src/test/java/team/jit/technicalinterviewdemo/business/book/**`
  - worker leaves shared contract artifacts untouched unless the coordinator explicitly reassigns them

## Affected Artifacts
- Tests:
  - `src/test/java/team/jit/technicalinterviewdemo/business/book/BookServiceTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/book/BookApiIntegrationTests.java`
  - optional new `src/test/java/team/jit/technicalinterviewdemo/business/book/BookSearchSpecificationsTests.java`
- Source files:
  - `src/main/java/team/jit/technicalinterviewdemo/business/book/BookService.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/book/BookSearchRequest.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/book/BookSearchSpecifications.java`
  - optional new package-local helper under `src/main/java/team/jit/technicalinterviewdemo/business/book/`
- Likely unchanged contract artifacts:
  - `src/docs/asciidoc/book-controller.adoc`
  - `src/test/resources/http/book-controller.http`
  - `src/test/resources/openapi/approved-openapi.json`
  - `README.md`
- Build and benchmark checks:
  - `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.business.book.BookServiceTests --tests team.jit.technicalinterviewdemo.business.book.BookApiIntegrationTests`
  - `.\gradlew.bat gatlingBenchmark`
  - `.\gradlew.bat build`

## Execution Milestones
### Milestone 1: Lock Current Search Normalization Expectations
- goal
  - make the current normalization and validation boundaries explicit before any refactor lands.
- owned files or packages
  - `src/test/java/team/jit/technicalinterviewdemo/business/book/**`
- shared files that a `Shared Plan` worker must leave to the coordinator
  - none
- behavior to preserve
  - current text-filter trimming, category deduplication, ISBN validation, year-range validation, and sort-alias handling
- exact deliverables
  - tighter tests for blank filters being ignored, repeated categories collapsing cleanly, and current validation messages remaining stable
- validation checkpoint
  - `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.business.book.BookServiceTests --tests team.jit.technicalinterviewdemo.business.book.BookApiIntegrationTests`
- commit checkpoint
  - one test-focused milestone commit

### Milestone 2: Refactor Normalization And Specification Assembly
- goal
  - reduce split responsibility between `BookService` and `BookSearchSpecifications` while keeping query semantics stable.
- owned files or packages
  - `src/main/java/team/jit/technicalinterviewdemo/business/book/**`
  - any new feature-local helper created under the same package
- shared files that a `Shared Plan` worker must leave to the coordinator
  - `README.md`
  - `src/test/resources/openapi/approved-openapi.json`
  - `src/docs/asciidoc/book-controller.adoc`
  - `src/test/resources/http/book-controller.http`
- behavior to preserve
  - the supported `GET /api/books` contract remains unchanged
- exact deliverables
  - one coherent internal normalization path
  - slimmer specification assembly logic
  - no controller, REST Docs, HTTP example, or OpenAPI edits
- validation checkpoint
  - `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.business.book.BookServiceTests --tests team.jit.technicalinterviewdemo.business.book.BookApiIntegrationTests`
  - `.\gradlew.bat gatlingBenchmark`
  - `.\gradlew.bat build`
- commit checkpoint
  - one implementation commit with plan, worker log, and private changelog updates if this plan is executed in `Parallel Plans`

## Edge Cases And Failure Modes
- Blank or whitespace-only text filters must still be ignored instead of producing broad `%` predicates.
- Duplicate or case-varied category filters must still avoid duplicate query semantics.
- Reversed year ranges must still fail before the repository is queried.
- Unsupported sort aliases and invalid ISBN characters must keep the current error text so public behavior does not drift accidentally.
- A no-filter search must still resolve to the same effective "match all" specification path.

## Validation Plan
- Commands to run:
  - `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.business.book.BookServiceTests --tests team.jit.technicalinterviewdemo.business.book.BookApiIntegrationTests`
  - `.\gradlew.bat gatlingBenchmark`
  - `.\gradlew.bat build`
- Tests to add or update:
  - book-local normalization coverage for blank strings, trimmed values, and repeated categories
  - existing `BookServiceTests` assertions for validation failures
  - `BookApiIntegrationTests` only where an end-to-end guard is needed to prove the contract stayed stable
- Docs or contract checks:
  - manually confirm that `src/docs/asciidoc/book-controller.adoc`, `src/test/resources/http/book-controller.http`, and `src/test/resources/openapi/approved-openapi.json` remain unchanged unless execution revealed real contract drift
- Manual verification steps:
  - call `GET /api/books` with and without filters, including repeated `category` parameters and whitespace around text filters, and confirm the response set and error behavior stay unchanged

## Better Engineering Notes
- Keep the cleanup inside `business.book`; this repo does not need a generic query-builder abstraction.
- Use a helper only if it removes a real split responsibility between validation and specification assembly.
- If execution starts to redesign search semantics, stop and write a real public-API plan instead of hiding that scope inside this dummy cleanup.

## Validation Results
- 2026-05-04: Milestone 1 completed. `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.business.book.BookServiceTests --tests team.jit.technicalinterviewdemo.business.book.BookApiIntegrationTests` passed after rerunning with `JAVA_HOME=C:\Users\kamki\.jdks\azul-25.0.3` because the default shell environment was pinned to Java 11 and no local `.env` file was present.
- 2026-05-04: Milestone 2 refactor preserved the focused book behavior checks. `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.business.book.BookServiceTests --tests team.jit.technicalinterviewdemo.business.book.BookApiIntegrationTests` passed with `JAVA_HOME=C:\Users\kamki\.jdks\azul-25.0.3`.
- 2026-05-04: `.\gradlew.bat gatlingBenchmark` failed on the benchmark regression gate. The first real failure was shared baseline drift across `list-books` (p95 `28ms > 20ms`), `search-books` (p95 `25ms > 19ms`), `lookup-localization-message` (p95 `21ms > 14ms`), and `oauth2-github-redirect` (p95 `22ms > 14ms`). Because the failure spans unrelated endpoints outside this worker's owned slice, no benchmark baseline change or follow-up fix was made here.
- 2026-05-04: reran `.\gradlew.bat gatlingBenchmark` with `JAVA_HOME=C:\Users\kamki\.jdks\azul-25.0.3`; the benchmark gate passed on the second run (`list-books` p95 `18ms`, `search-books` p95 `18ms`, `lookup-localization-message` p95 `14ms`, `oauth2-github-redirect` p95 `11ms`), so the temporary blocked status was cleared without code changes.
- 2026-05-04: `.\gradlew.bat build` passed with `JAVA_HOME=C:\Users\kamki\.jdks\azul-25.0.3`.

## User Validation
- Call `GET /api/books` with and without filters, including repeated `category` parameters and whitespace around text filters, and confirm the response shape, sort behavior, and validation messages remain unchanged.
