# Plan: Localization HTTP Revalidation (`ETag` Conditional Reads)

## Provenance

| Field | Value |
| --- | --- |
| Created By | Claude Fable 5 (Claude Code CLI) |
| Created On | 2026-06-12 |
| Source Request | User prompt "implement that" selecting the `ROADMAP.md` Analysis row "Conditional-GET revalidation for public localization reads"; originating intake from frontend `M-I18N-003` (`technical-interview-frontend/ROADMAP.md`) |
| Generation Context | `M0: direct` interactive session on `main`; loaded `AGENTS.md`, `.agents/references/planning.md`, `.agents/references/execution.md`, `.agents/references/code-style.md`, `.agents/references/testing.md`, `.agents/references/command-wrapper.md`, `PLAN_TEMPLATE.md`; repo truth from `LocalizationController`, `LocalizationService`, `SecurityConfiguration`, `CachingConfiguration`, localization integration/REST Docs tests, `localization-controller.adoc`, `PublicApiSimulation` |

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Implementation |
| Status | In Progress |

## Planning Readiness
| Field | Value |
| --- | --- |
| Decision Complete | Yes |
| Blocking Open Questions | None |
| Accepted Fallbacks | D2 (response-hash `ETag` instead of version token) |
| Ready For Execution | Yes |
| Last Updated | 2026-06-12 |

## Linked Pre-Planning Artifacts
| Artifact | Path | Role | Status |
| --- | --- | --- | --- |
| ADR | None | Additive header contract on existing endpoints; no durable architecture or process choice beyond this plan's decision log | None |
| PRD | None | Product intent fully framed by frontend `M-I18N-003` acceptance criteria and the roadmap Analysis row | None |
| Spec | None | Behavior lands directly in executable specs (integration + REST Docs tests) and the approved OpenAPI baseline | None |

## Summary
- Add `ETag`/`If-None-Match` conditional-GET revalidation and `Cache-Control: no-cache` to the public localization reads `GET /api/localizations` and `GET /api/localizations/{id}`.
- Today every response carries Spring Security's default `Cache-Control: no-cache, no-store, max-age=0, must-revalidate`, so browsers re-download the full catalog on every page load and language switch; the frontend (`M-I18N-003`) wants cheap revalidation instead.
- Success: matching `If-None-Match` returns `304` with an empty body; an admin create/update/delete changes the `ETag`, so revalidation immediately serves fresh content (no stale bundles); contract artifacts (REST Docs, OpenAPI baseline, AsciiDoc, README) move together.

## Scope
- In scope: response-hash `ETag` filter scoped to public localization GETs, `Cache-Control: no-cache` on those two controller responses, integration tests, REST Docs scenario + header documentation, `localization-controller.adoc`, OpenAPI annotations + approved baseline refresh, README feature note, `architecture.md` package description, manual HTTP suite-04 conditional read example, Gatling benchmark rerun.
- Out of scope: caching for books/categories or any other endpoint, `Last-Modified` support, version-token `ETag` optimization, frontend changes (tracked in the frontend repo), release work.

## Current State
- `GET /api/localizations` (pageable, optional `messageKey`/`language` filters) and `GET /api/localizations/{id}` are public reads in `LocalizationController`; writes are ADMIN-only.
- Spring Security's default `CacheControlHeadersWriter` emits `no-store` on all responses because no endpoint sets its own `Cache-Control` (observed in generated REST Docs snippets); `no-store` would also disable `ShallowEtagHeaderFilter`, so the controller must override it.
- Server-side Caffeine caches (`localization-lists`, `localization-lookups`, `localization-message-maps`) exist and `LocalizationService.evictLocalizationCaches()` clears them on every admin write, keeping recomputation cheap.
- Filters in this repo are plain `@Component` `OncePerRequestFilter` beans; `technical.localization` owns localization cross-cutting web concerns (`RequestLanguageContextFilter`).
- `PublicApiSimulation` benchmarks `lookup-localization-message` against `GET /api/localizations` with a 500 ms p95 assertion.

## Requirement Gaps And Open Questions
| ID | Question / Gap | Why It Matters | Owner | Status | Fallback / Decision | Blocks Ready? |
| --- | --- | --- | --- | --- | --- | --- |
| Q1 | `ETag` source: response-body hash vs catalog version token | Determines code shape and whether recomputation is skipped | Agent | Answered | D2 | No |
| Q2 | Endpoint scope: collection only vs also by-id | Contract breadth | Agent | Answered | D3 | No |
| Q3 | Contract surface for `304`/headers in REST Docs and OpenAPI | Keeps published contract aligned | Agent | Answered | D4 | No |

## Decision Log And Assumptions
| ID | Decision / Assumption | Source | Date | Revisit Trigger |
| --- | --- | --- | --- | --- |
| D1 | Mechanism is `ETag`/`If-None-Match` conditional GET plus `Cache-Control: no-cache`; bare `Cache-Control: max-age` is rejected because admin edits must not serve stale bundles | Roadmap Analysis row + frontend `M-I18N-003` freshness criteria | 2026-06-12 | A real freshness-window requirement appears |
| D2 | `ETag` comes from a `ShallowEtagHeaderFilter` subclass scoped to GET `/api/localizations` paths (response-body hash); saves bandwidth, not recomputation. Version-token `ETag` deferred as an optimization because Caffeine caches already keep list reads cheap at demo scale | Agent fallback from repo truth | 2026-06-12 | Benchmark or production-like load shows list recomputation cost matters |
| D3 | Both public reads get the contract: `GET /api/localizations` and `GET /api/localizations/{id}`; admin writes keep Spring Security default `no-store` headers | Agent decision (same mechanism, zero extra code, consistent public-read contract) | 2026-06-12 | None |
| D4 | Contract surface: `ETag` + `Cache-Control` header descriptors on the four read REST Docs scenarios, one new documented `304` revalidation scenario, OpenAPI `200` header + `304` response annotations on the two GETs with baseline refresh via `./build.ps1 refreshOpenApiBaseline` | Agent decision per `documentation.md` routing | 2026-06-12 | None |
| D5 | User prompt "implement that" is the triage acceptance; roadmap row moves Analysis → Active Release Track without a transient Triage row | User request | 2026-06-12 | None |
| D6 | Additive, backward-compatible change; fits the stable `2.x` no-breaking-change policy and a future `v2.2.0` | `ROADMAP.md` Current Project State | 2026-06-12 | None |

## Execution Shape And Shared Files
- `M0: direct`: one coherent vertical slice, single agent, interactive session on `main`; no delegation or shared-file boundaries needed.

## Affected Artifacts
- Source: `technical/localization/LocalizationEtagHeaderFilter.java` (new), `business/localization/LocalizationController.java`
- Tests: `LocalizationApiIntegrationTests`, `LocalizationApiDocumentationTests`
- Contract docs: `src/docs/asciidoc/localization-controller.adoc`, `src/test/resources/openapi/approved-openapi.json`, `README.md`
- HTTP examples: `src/manualTests/http/suites/suite-04-public-localization-reads.http`
- AI guidance: `.agents/references/architecture.md` (`technical.localization` package description)
- Roadmap: `ROADMAP.md` Active Release Track row
- Benchmark: `./build.ps1 gatlingBenchmark` rerun (localization read path changed)

## Progress Tracker
| Task | Status | Owner | Commit | Validation | Notes |
| --- | --- | --- | --- | --- | --- |
| 1: Conditional-GET slice | Not Started | Agent | Pending | Pending | Implementation + executable specs + contract docs in one commit |
| 2: Verification and closeout | Not Started | Agent | Pending | Pending | Full build + benchmark + docs audit; roadmap/plan closeout |

## Execution Tasks
### Task 1: Conditional-GET slice
| Field | Value |
| --- | --- |
| Status | Not Started |
| Goal | Public localization GETs return `ETag` + `Cache-Control: no-cache` and honor `If-None-Match` with `304`; all contract artifacts move together |
| Owned Files Or Packages | `technical/localization`, `business/localization/LocalizationController.java`, localization tests, `localization-controller.adoc`, OpenAPI baseline, README, `architecture.md`, manual suite-04 |
| Coordinator-Owned Shared Files | None |
| Context Required | none beyond AGENTS.md, .agents/references/execution.md, and this plan |
| Behavior To Preserve | Response payload shapes, pagination, filters, sorting validation, error contracts, admin write authorization, write responses keep `no-store` defaults |
| Deliverables | New `ETag` filter (GET-only, `/api/localizations` paths), controller `cacheControl(noCache())` on both GETs, OpenAPI `200`-header/`304` annotations, integration tests (`ETag` present, `304` on match, fresh `200` + new `ETag` after admin update, by-id conditional read), REST Docs `304` scenario + header descriptors, adoc section, README note, architecture note, manual suite example, refreshed approved OpenAPI baseline |
| Validation Checkpoint | `./build.ps1 build` green (includes localization tests, REST Docs, OpenAPI compatibility); `pwsh ./scripts/docs/audit-docs.ps1` green |
| Commit Checkpoint | `feat(localization): add etag revalidation to public localization reads` |

### Task 2: Verification and closeout
| Field | Value |
| --- | --- |
| Status | Not Started |
| Goal | Prove the committed slice with the benchmark gate and close out tracking |
| Owned Files Or Packages | `ROADMAP.md`, this plan |
| Coordinator-Owned Shared Files | None |
| Context Required | none beyond AGENTS.md, .agents/references/execution.md, and this plan |
| Behavior To Preserve | n/a |
| Deliverables | `./build.ps1 gatlingBenchmark` result recorded; review per `.agents/references/reviews.md`; plan tracker + roadmap row moved to Integrated |
| Validation Checkpoint | Benchmark assertions pass (`lookup-localization-message` p95 < 500 ms, global success ≥ 99%) |
| Commit Checkpoint | `docs(plan): close out localization http revalidation plan` |

## Blockers And Replan Triggers
| Trigger / Blocker | Response | Owner | Status |
| --- | --- | --- | --- |
| Spring Security header writer overrides controller `Cache-Control` despite explicit response header | Replan: disable `cacheControl` writer for localization read matchers in `SecurityConfiguration` and re-run security-sensitive review | Agent | Open |
| Benchmark regression from response buffering on `lookup-localization-message` | Replan toward D2's version-token alternative | Agent | Open |
| OpenAPI compatibility check flags the additive change as breaking | Stop; review diff intentionally before `refreshOpenApiBaseline`; never refresh to silence an unexplained failure | Agent | Open |

## Edge Cases And Failure Modes
- `304` must include the `ETag` and omit the body; request-id/trace headers still present.
- Different pages/filter combinations produce different `ETag`s (per-URL representation hashing) — correct, no shared-token requirement.
- Error responses (`400`, `404`) stay un-`ETag`ged (filter only hashes 2xx GET responses).
- Admin writes (`POST`/`PUT`/`DELETE`) must keep current `no-store` security defaults — filter is GET-scoped.
- `If-None-Match: *` and multi-value lists are handled by Spring's `ShallowEtagHeaderFilter` semantics; no custom parsing.

## Validation Plan
- `./build.ps1 build` (full verification incl. integration, REST Docs, OpenAPI compatibility, static checks)
- `./build.ps1 refreshOpenApiBaseline` once, after intentional contract review of the OpenAPI diff
- `./build.ps1 gatlingBenchmark` (localization read path changed)
- `pwsh ./scripts/docs/audit-docs.ps1` (user-facing Markdown/AsciiDoc changed)

## Verification Strategy
- Integration tests: `ETag` + `Cache-Control: no-cache` on both reads; `304` on matching `If-None-Match`; fresh `200` with changed `ETag` after admin update (no stale bundle); negative: mismatched `If-None-Match` returns `200`.
- Contract tests: REST Docs snippets regenerate with header documentation and the `304` scenario; OpenAPI compatibility test against the refreshed baseline.
- Benchmark: existing `PublicApiSimulation` assertions.

## Better Engineering Notes
- Version-token `ETag` (skip recomputation entirely using the `evictLocalizationCaches()` hook) is a known deferred optimization, captured in D2's revisit trigger; not hidden scope.

## Validation Results
| Date | Command | Scope | Result | Notes |
| --- | --- | --- | --- | --- |
| 2026-06-12 | Pending | Full build | Pending | |

## User Validation
- Run the app (`./build.ps1 bootRun`), then `curl -i http://localhost:8080/api/localizations?language=en` — response shows `ETag` and `Cache-Control: no-cache`.
- Repeat with `curl -i -H 'If-None-Match: <etag>'` — response is `304` with no body.
- Edit any localization as ADMIN, repeat the conditional request — response is `200` with a new `ETag` and the fresh content.
