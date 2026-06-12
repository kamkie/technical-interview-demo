# Plan: Fix Spring Session Attribute Write Race And Error-Dispatch Contract

## Provenance

| Field | Value |
| --- | --- |
| Created By | Claude Code (claude-fable-5) |
| Created On | 2026-06-12 |
| Source Request | User prompt "implement next task from roadmap"; `ROADMAP.md` Triage row "Fix intermittent 500s from the Spring Session attribute write race" accepted 2026-06-12 |
| Generation Context | Plan From Roadmap mode; loaded `AGENTS.md`, `.agents/references/planning.md`, `.agents/references/plan-execution.md`, `.agents/references/execution.md`, `.agents/references/testing.md`, `.agents/references/command-wrapper.md`; branch `main`, single-agent `M0: direct` |

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Planning |
| Status | Ready |

## Planning Readiness
| Field | Value |
| --- | --- |
| Decision Complete | Yes |
| Blocking Open Questions | None |
| Accepted Fallbacks | D3, D4 |
| Ready For Execution | Yes |
| Last Updated | 2026-06-12 |

## Linked Pre-Planning Artifacts
| Artifact | Path | Role | Status |
| --- | --- | --- | --- |
| ADR | None | No durable architecture decision; maintenance fix follows the documented Spring Session PostgreSQL pattern | None |
| PRD | None | No product-scope change | None |
| Spec | None | Behavior is expressed as new executable integration tests added by this plan | None |

## Summary
- Eliminate intermittent 500s caused by concurrent first requests racing identical `SPRING_SESSION_ATTRIBUTES` inserts (`spring_session_attributes_pk` duplicate key) by making the Spring Session JDBC attribute insert a PostgreSQL upsert.
- Keep failures that escape the servlet filter chain on the localized problem-details contract by replacing Spring Boot's default error body with an `ErrorController` that renders through `ApiProblemFactory`.
- Success: a deterministic two-handle concurrent-save integration test passes only with the upsert; an escaped filter exception returns `application/problem+json` with `messageKey`, localized `message`, and `language`; full build stays green with no OpenAPI baseline churn.

## Scope
- In scope:
  - `SessionRepositoryCustomizer<JdbcIndexedSessionRepository>` bean applying the PostgreSQL `ON CONFLICT` upsert to the session attribute create query.
  - New `@Hidden` `ApiErrorController` implementing `ErrorController`, rendering localized `ProblemDetail` for error dispatches.
  - Integration tests for both behaviors (executable specs).
  - Published contract doc alignment (`README.md` / `src/docs/asciidoc/index.adoc` error-handling wording) and `CHANGELOG.md` `## [Unreleased]` entries.
  - `ROADMAP.md` state transitions for this workstream.
- Out of scope:
  - Changes to `AuthenticatedUserSynchronizationFilter` logic (the change-only write guard already exists on `main`; see D1).
  - Release work (version bump, tagging, release notes) — `v2.1.1` is only the default target recorded in the roadmap.
  - Redis or other session-store alternatives; non-PostgreSQL portability of the session SQL.
  - OpenAPI baseline changes (the error controller is hidden from springdoc).

## Current State
- `AuthenticatedUserSynchronizationFilter.synchronizeUser` already skips the attribute write when `syncedUser` equals the authenticated user key (same-request guard), but concurrent requests that loaded the same fresh session before any of them committed all mark the attribute as added, and `JdbcIndexedSessionRepository` runs plain `INSERT INTO SPRING_SESSION_ATTRIBUTES`, so the losers fail with a duplicate-key error.
- The duplicate-key exception escapes inside Spring Session's `SessionRepositoryFilter` commit, bypasses `ApiExceptionHandler` (`@RestControllerAdvice` only covers MVC dispatch), and Boot's `BasicErrorController` renders the non-contract default error body. No custom `ErrorController` exists in the repo (verified by search).
- Session schema: `src/main/resources/db/migration/V4__create_spring_session_tables.sql` (PK `(SESSION_PRIMARY_ID, ATTRIBUTE_NAME)`, PostgreSQL `BYTEA`, so the schema is already PostgreSQL-specific).
- Session config: `SessionConfiguration` is `@EnableJdbcHttpSession` with no customizers; `spring.session.store-type=jdbc`.
- Problem-details contract: `ApiProblemFactory` resolves `messageKey`/`message`/`language` via `LocalizationService`; `error.server.internal`, `error.request.unauthorized`, `error.request.forbidden`, `error.request.resource_not_found`, and `error.request.invalid` are seeded in all seven languages.
- `RequestLanguageContextFilter` is an `@Order(HIGHEST_PRECEDENCE)` `OncePerRequestFilter` `@Component`; Spring Boot registers `OncePerRequestFilter` beans for all dispatcher types, so the language ThreadLocal is re-populated during the ERROR dispatch and `ApiProblemFactory` localization works there (inference from Spring Boot filter-registration behavior; Task 2's test asserts it, so a wrong inference fails fast without affecting readiness).
- `/error` is already `permitAll` in `SecurityConfiguration`. Integration tests run against PostgreSQL via Testcontainers (`@TestcontainersTest`), so PostgreSQL-specific SQL is fully testable.
- Frontend evidence (triage 2026-06-12): duplicate-key bursts in Postgres logs at 09:58:36, 10:07:56, 10:20:59 UTC matching browser 500s on first-load bursts of `/api/session`, `/api/books`, `/api/categories`, `/api/localizations`.

## Requirement Gaps And Open Questions
| ID | Question / Gap | Why It Matters | Owner | Status | Fallback / Decision | Blocks Ready? |
| --- | --- | --- | --- | --- | --- | --- |
| Q1 | Should non-API (browser/HTML) error dispatches keep an HTML error page? | Replacing `BasicErrorController` removes the whitelabel page for every path | Agent | Answered | D3: render problem details for all error dispatches; this is an API-first demo and a single error contract is simpler | No |

## Decision Log And Assumptions
| ID | Decision / Assumption | Source | Date | Revisit Trigger |
| --- | --- | --- | --- | --- |
| D1 | The triage fix-direction item "write the `syncedUser` attribute only when its value changes" is already satisfied on `main` (`AuthenticatedUserSynchronizationFilter.synchronizeUser` early-returns on equality); no filter change in this plan | Code | 2026-06-12 | None |
| D2 | Use the Spring Session reference-documented PostgreSQL customizer: `setCreateSessionAttributeQuery` with `INSERT ... ON CONFLICT (SESSION_PRIMARY_ID, ATTRIBUTE_NAME) DO UPDATE SET ATTRIBUTE_BYTES = EXCLUDED.ATTRIBUTE_BYTES`; PostgreSQL-only SQL is acceptable because the session schema is already PostgreSQL-specific | Spec/Code | 2026-06-12 | A second database dialect becomes supported |
| D3 | Replace `BasicErrorController` with a `@Hidden` `ApiErrorController` that renders localized problem details for all error dispatches (not just `/api/**`); whitelabel HTML page is intentionally removed | Agent fallback | 2026-06-12 | A real HTML browser error page becomes a requirement |
| D4 | Error-dispatch status mapping uses only seeded message keys: 401→`error.request.unauthorized`, 403→`error.request.forbidden`, 404→`error.request.resource_not_found`, other 4xx→`error.request.invalid`, everything else→`error.server.internal`; 5xx logs through `serverProblem`, 4xx through `clientProblem` | Agent fallback | 2026-06-12 | A new error-dispatch status needs a dedicated key |
| D5 | Set the problem media type explicitly on the `ResponseEntity` so error rendering does not depend on the original request's `Accept` header content negotiation | Code | 2026-06-12 | None |
| D6 | Work lands directly on `main` as `M0: direct` with one commit per plan task; no release work | `.agents/references/workflow.md` invariants, recent repo history | 2026-06-12 | Plan grows beyond a small maintenance fix |
| D7 | The error controller stays out of the OpenAPI contract via springdoc `@Hidden` (existing repo pattern), so the approved OpenAPI baseline must not change | Code | 2026-06-12 | Error contract is intentionally published in OpenAPI |

## Execution Shape And Shared Files
- Recommended shape: `M0: direct` — one agent, one branch (`main`), three commit-sized tasks with no parallelizable slices.
- No coordinator/worker split; no shared-file boundaries needed.
- If later delegation becomes necessary, Task 1 (session upsert) and Task 2 (error dispatch) are independent worker slices; Task 3 must stay with the coordinator.

## Affected Artifacts
- Source: `src/main/java/team/jit/technicalinterviewdemo/technical/security/SessionConfiguration.java`; new `src/main/java/team/jit/technicalinterviewdemo/technical/api/ApiErrorController.java`
- Tests (executable specs): new `src/test/java/team/jit/technicalinterviewdemo/technical/security/SessionAttributeConcurrencyIntegrationTests.java`; new `src/test/java/team/jit/technicalinterviewdemo/technical/api/ErrorDispatchProblemDetailsIntegrationTests.java`
- Docs: `README.md` and `src/docs/asciidoc/index.adoc` error-handling wording (one-line alignment), `CHANGELOG.md` `## [Unreleased]`
- OpenAPI: no change expected (D7); compatibility test guards this
- HTTP examples: no change (no new public endpoint shape; `/error` is an internal dispatch target)
- Roadmap: `ROADMAP.md` workstream row and `Immediate Next Action`

## Progress Tracker
| Task | Status | Owner | Commit | Validation | Notes |
| --- | --- | --- | --- | --- | --- |
| 1: Session attribute upsert | Not Started | Agent | Pending | Pending | |
| 2: Problem-details error dispatch | Not Started | Agent | Pending | Pending | |
| 3: Changelog, roadmap, final validation | Not Started | Agent | Pending | Pending | |

## Execution Tasks
### Task 1: Session attribute upsert
| Field | Value |
| --- | --- |
| Status | Not Started |
| Goal | Make the Spring Session JDBC attribute insert idempotent under concurrent saves of the same fresh session |
| Owned Files Or Packages | `SessionConfiguration.java`; new `SessionAttributeConcurrencyIntegrationTests.java` |
| Coordinator-Owned Shared Files | None |
| Context Required | none beyond AGENTS.md, .agents/references/execution.md, and this plan |
| Behavior To Preserve | Session create/read/expire flows, attribute round-trip, existing security and session integration tests |
| Deliverables | `SessionRepositoryCustomizer<JdbcIndexedSessionRepository>` bean in `SessionConfiguration` applying the D2 upsert query; integration test that loads two handles of one saved session, sets the same new attribute on both, saves both, and asserts no duplicate-key failure plus attribute round-trip (red without the customizer, green with it) |
| Validation Checkpoint | `./build.ps1 test --tests "*SessionAttributeConcurrencyIntegrationTests"` plus existing session/security tests `./build.ps1 test --tests "*SecurityIntegrationTests"` |
| Commit Checkpoint | `fix(security): make session attribute writes idempotent under concurrent saves` |

### Task 2: Problem-details error dispatch
| Field | Value |
| --- | --- |
| Status | Not Started |
| Goal | Keep failures that escape the filter chain on the localized problem-details contract |
| Owned Files Or Packages | New `ApiErrorController.java`; new `ErrorDispatchProblemDetailsIntegrationTests.java`; `README.md`; `src/docs/asciidoc/index.adoc` |
| Coordinator-Owned Shared Files | None |
| Context Required | This plan's D3–D5, D7; `ApiProblemFactory` for the rendering contract |
| Behavior To Preserve | All existing `ApiExceptionHandler` MVC error responses; `ApiAuthenticationEntryPoint`/`ApiAccessDeniedHandler` bodies; OpenAPI baseline unchanged |
| Deliverables | `@Hidden` `@RestController` implementing `ErrorController` mapped to `/error`, resolving status/exception from servlet error attributes, mapping per D4, rendering via `ApiProblemFactory` with explicit `application/problem+json` (D5); random-port integration test with a test-only highest-precedence filter that throws on a marker header, asserting 500 problem-details body (`title`, `messageKey=error.server.internal`, `message`, `language=en`) and a `?lang=pl` localized variant; direct `/error` dispatch sanity assertion; one-line error-handling wording alignment in README and asciidoc |
| Validation Checkpoint | `./build.ps1 test --tests "*ErrorDispatchProblemDetailsIntegrationTests" --tests "*ApiErrorHandlingIntegrationTests"` |
| Commit Checkpoint | `fix(api): render localized problem details for escaped filter-chain failures` |

### Task 3: Changelog, roadmap, final validation
| Field | Value |
| --- | --- |
| Status | Not Started |
| Goal | Record the unreleased change, close the roadmap loop, and prove the cumulative diff |
| Owned Files Or Packages | `CHANGELOG.md`, `ROADMAP.md`, this plan |
| Coordinator-Owned Shared Files | None |
| Context Required | none beyond AGENTS.md, .agents/references/execution.md, and this plan |
| Behavior To Preserve | n/a (docs and validation only) |
| Deliverables | `CHANGELOG.md` `## [Unreleased]` fixed-entries for both behaviors; `ROADMAP.md` workstream row moved to `Integrated` with updated `Immediate Next Action`; plan lifecycle moved to `Phase=Integration`, `Status=Implemented`; validation ledger updated |
| Validation Checkpoint | `./build.ps1 -FullBuild build gatlingBenchmark --no-daemon` (full build proves committed implementation; benchmark covers session-startup-path change per `.agents/references/testing.md`) and `pwsh ./scripts/docs/audit-docs.ps1` |
| Commit Checkpoint | `docs(roadmap): integrate session attribute write race fix` |

## Blockers And Replan Triggers
| Trigger / Blocker | Response | Owner | Status |
| --- | --- | --- | --- |
| `setCreateSessionAttributeQuery` unavailable or behaves differently in the repo's Spring Session version | Stop, check the dependency version, and replan the upsert mechanism before coding around it | Agent | Open |
| Task 1 test cannot reproduce the duplicate-key failure without the fix (red phase fails to be red) | Re-examine `JdbcSession` delta tracking assumptions; if the insert path differs, revise `Current State` and the test design before proceeding | Agent | Open |
| Replacing `BasicErrorController` breaks an existing test or springdoc/OpenAPI output | Revisit D3: narrow the controller to problem-details rendering while restoring any contract the failing spec proves | Agent | Open |
| Error-dispatch localization does not see the request language (filter not re-run on ERROR dispatch) | Resolve language explicitly in `ApiErrorController` via `RequestLanguageResolver` instead of relying on the ThreadLocal | Agent | Open |
| `gatlingBenchmark` cannot run locally | Record the exact failure and remaining risk in `Validation Results`; do not silently skip | Agent | Open |

## Edge Cases And Failure Modes
- Error dispatch with no servlet error attributes (direct `/error` hit): default to 500 / `error.server.internal` rather than failing.
- Original failure is a database outage: the localization lookup in the error controller may also fail; the container's last-resort error page is the accepted fallback (no recursion risk: an exception inside the error dispatch is not re-dispatched).
- Upsert semantics: concurrent writers now last-write-wins on `ATTRIBUTE_BYTES`; for the racing `syncedUser` case both writers carry the identical value, so this is safe. Other session attributes (security context) already exist before concurrency bursts.
- `Accept: text/html` browser requests hitting `/error` get `application/problem+json` (D3/D5) — intentional contract simplification.
- Compatibility: response bodies for all currently-specified endpoints are unchanged; only the previously non-contractual escaped-failure body changes shape, which the triage decision explicitly requires.

## Validation Plan
- Task 1: targeted new test red→green; existing security integration tests stay green.
- Task 2: targeted new test plus existing API error-handling tests.
- Final: `./build.ps1 -FullBuild build gatlingBenchmark --no-daemon` (cumulative proof + session-path benchmark) and `pwsh ./scripts/docs/audit-docs.ps1` for the Markdown/AsciiDoc edits.

## Verification Strategy
- Integration tests (PostgreSQL Testcontainers): deterministic two-handle concurrent attribute save; escaped-filter-failure problem-details contract including localization.
- Contract tests: existing OpenAPI compatibility test proves no baseline drift (D7); existing REST Docs tests stay green.
- Negative scenarios: direct `/error` dispatch default; localized (`lang=pl`) error body.
- Benchmark: `gatlingBenchmark` for the session startup path.

## Better Engineering Notes
- No prerequisite cleanup needed.
- Deferred: if a future requirement adds HTML error pages or publishes the error-dispatch contract in OpenAPI, revisit D3/D7.

## Validation Results
| Date | Command | Scope | Result | Notes |
| --- | --- | --- | --- | --- |
| 2026-06-12 | Pending | Per-task targeted tests, final full build + benchmark, docs audit | Pending | |

## User Validation
- Start the app with the SPA, clear cookies, and load the frontend; the parallel first requests (`/api/session`, `/api/books`, `/api/categories`, `/api/localizations`) must all return 200 with no duplicate-key errors in the Postgres logs.
- `curl -H "X-Simulated-Filter-Failure: true"` is test-only; to see the new error contract manually, hit any failing path that escapes MVC and confirm an `application/problem+json` body with `messageKey`, `message`, `language`.
