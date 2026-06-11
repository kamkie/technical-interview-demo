# Plan: Admin Account Blocking

## Provenance

| Field | Value |
| --- | --- |
| Created By | Claude Code (Fable 5) |
| Created On | 2026-06-11 |
| Source Request | User instruction to create the execution plan from the approved `docs/specs/SPEC_admin-account-blocking.md`; roadmap workstream "Admin block/unblock for user accounts" accepted at triage on 2026-06-11 for the `2.1.0` feature line |
| Generation Context | `Plan From Roadmap` mode on `main`; loaded `AGENTS.md`, `.agents/references/planning.md`, `.agents/references/testing.md`, `.agents/references/command-wrapper.md`, workflow mode table from `.agents/references/workflow.md`, `PLAN_TEMPLATE.md`, the approved spec, and the source files named in the spec provenance |

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
| Accepted Fallbacks | None (all spec questions Q1–Q4 are answered, none fell back) |
| Ready For Execution | Yes |
| Last Updated | 2026-06-11 |

## Linked Pre-Planning Artifacts
| Artifact | Path | Role | Status |
| --- | --- | --- | --- |
| ADR | `docs/decisions/0001-adopt-pre-planning-artifacts.md` | Artifact routing that selected a standalone spec | Accepted |
| PRD | None | Product intent was already clear at triage | None |
| Spec | `docs/specs/SPEC_admin-account-blocking.md` | Behavior, contract, acceptance criteria AC1–AC11, and validation mapping; the governing truth for this plan | Approved (2026-06-11) |

## Summary
- Add admin block/unblock for persisted user accounts: nullable block columns on `users`, a `PUT /api/admin/users/{id}/status` endpoint with an operator reason, sign-in rejection and per-request active-session rejection for blocked accounts, additive admin API fields, and one seeded blocked demo user.
- Today the strongest admin sanction is role demotion; blocking gives operators a reversible, audited way to cut off access entirely.
- Success is the spec's AC1–AC11 green in their mapped validation homes, an additive-only OpenAPI baseline diff, and the full build plus session benchmark passing.

## Scope
- In scope: everything in the spec's "In scope" list (schema `V12` plus metadata sidecar, entity support, status endpoint, sign-in and session rejection with audit, additive `AdminUserAccountResponse` fields, seeded blocked `demo-user-002`, REST Docs, OpenAPI refresh, HTTP examples, integration tests).
- Out of scope: everything in the spec's "Out of scope" list (eager session deletion at block time, temporary blocks, role-grant changes, `/api/account` surface changes, frontend UI work). Release work (`v2.1.0` tagging, changelog) stays out until the user requests it.

## Current State
- `users` table (Flyway through `V11`) has no account-status columns; `UserAccount` has no block state.
- `AdminUserManagementController` exposes `GET /api/admin/users` and `PUT /api/admin/users/{id}/roles`; the roles endpoint is the contract pattern the status endpoint mirrors (ADMIN + CSRF, reason ≤ 255, `ApiProblemResponse` errors, audit via `AuditLogService`).
- `AuditingAuthenticationSuccessHandler` syncs the user and records `LOGIN_SUCCESS` on every OAuth login; `AuthenticatedUserSynchronizationFilter` runs after `AuthorizationFilter` and short-circuits per session via a session attribute.
- `SessionService.logoutCurrentSession` shows the established session-invalidation mechanics (invalidate `HttpSession`, delete from `SessionRepository`, clear context).
- `UserDataInitializer` idempotently seeds `demo-user-001`–`500` (provider `github`, role `USER`) when `app.bootstrap.seed.demo-data` is enabled.
- `AuditAction` already defines `LOGIN_FAILURE` and `SESSION_REJECTION`; the approved OpenAPI baseline exposes the enum in responses.
- Stable `2.x` line forbids breaking changes; the OpenAPI compatibility test enforces the checked baseline.

## Requirement Gaps And Open Questions
| ID | Question / Gap | Why It Matters | Owner | Status | Fallback / Decision | Blocks Ready? |
| --- | --- | --- | --- | --- | --- | --- |
| — | None; the approved spec is decision-complete (its Q1–Q4 are all answered) | — | — | — | — | No |

## Decision Log And Assumptions
| ID | Decision / Assumption | Source | Date | Revisit Trigger |
| --- | --- | --- | --- | --- |
| D1 | Spec `SPEC_admin-account-blocking.md` approved; it owns behavior, contract, and acceptance truth for this plan | User | 2026-06-11 | Spec marked `Stale` |
| D2 | `PUT /{id}/status` full-replacement endpoint, not block/unblock action endpoints | Spec Q1 | 2026-06-11 | None |
| D3 | No `AuditAction` enum extension; reuse `UPDATE`, `LOGIN_FAILURE`, `SESSION_REJECTION` with `failureReason`/`status` details | Spec Q2 | 2026-06-11 | A future major version revisits audit taxonomy |
| D4 | Per-request session rejection returns 401 problem+json | Spec Q3 | 2026-06-11 | None |
| D5 | Seed exactly one blocked demo user, `demo-user-002`, fresh databases only | User (spec Q4) | 2026-06-11 | None |
| D6 | Per-request blocked check accepts one extra user lookup per authenticated request; no eager session deletion | Spec | 2026-06-11 | Benchmark regression in Task 5 beyond accepted tolerance |
| D7 | Target version is a backward-compatible `v2.1.0`; all contract changes must be additive | Roadmap triage decision | 2026-06-11 | None |
| D8 | Assumption: blocking provenance (`blocked_by_user_id`) may be null for non-operator blocks (seed); the admin API surfaces null `blockedBy` for that case | Spec Seed Data section | 2026-06-11 | None |

## Execution Shape And Shared Files
- Recommended shape: `M0: direct`.
- The five tasks are serially dependent (schema/entity → endpoint → security enforcement → contract artifacts → final verification) and concentrate in one package plus the security package; parallel worker slices would share `AdminUserAccountResponse`, the audit service call sites, and the OpenAPI baseline, so delegation buys nothing here.
- No coordinator-owned shared files; a single agent owns the whole change set.
- If later splitting becomes necessary, the only clean worker boundary is Task 4 (contract artifacts) after Tasks 1–3 land.

## Affected Artifacts
- Pre-planning artifacts: `docs/specs/SPEC_admin-account-blocking.md` (governing; no further edits expected)
- Source: `src/main/resources/db/migration/V12__add_user_account_blocking.sql` (new), `src/main/resources/db/migration/metadata/V12__add_user_account_blocking.json` (new), `UserAccount`, `UserDataInitializer`, `AdminUserManagementController`, `AdminUserManagementService`, `AdminUserAccountResponse`, new `AdminUserAccountStatusUpdateRequest`, `AuditingAuthenticationSuccessHandler`, `AuthenticatedUserSynchronizationFilter`, `SecurityConfiguration` (wiring if the filter needs new collaborators)
- Tests: `UserDataInitializerTests`, `UserManagementIntegrationTests`, `AdminUserManagementApiIntegrationTests`, `AdminUserManagementApiDocumentationTests`, `SecurityIntegrationTests`
- OpenAPI: `src/test/resources/openapi/approved-openapi.json` (refresh after additive-only review)
- HTTP examples: `src/manualTests/http/examples/admin-user-management-controller.http`, `src/manualTests/http/suites/suite-10-admin-user-management.http`
- Docs: `docs/OPERATIONS.md` admin user-management mention; run the docs audit script after Markdown edits
- Build or benchmark checks: full build plus `gatlingBenchmark` (OAuth/session behavior changes per `.agents/references/testing.md`)

## Progress Tracker
| Task | Status | Owner | Commit | Validation | Notes |
| --- | --- | --- | --- | --- | --- |
| 1: Schema, entity, and seed support | Done | Agent | `feat(users): add account blocking schema, entity state, and blocked demo seed` | Passed | 13 tests green including new seed and persistence cases |
| 2: Status endpoint and admin API surface | Not Started | Agent | Pending | Pending | |
| 3: Sign-in and active-session rejection | Not Started | Agent | Pending | Pending | |
| 4: Contract artifacts and docs | Not Started | Agent | Pending | Pending | |
| 5: Final verification and roadmap sync | Not Started | Agent | Pending | Pending | |

## Execution Tasks

### Task 1: Schema, entity, and seed support
| Field | Value |
| --- | --- |
| Status | Done |
| Goal | Persist block state and seed the blocked demo user (spec Persistence and Seed Data sections; AC9, AC11) |
| Owned Files Or Packages | `V12__add_user_account_blocking.sql` + metadata sidecar, `UserAccount`, `UserDataInitializer`, `UserDataInitializerTests`, `UserManagementIntegrationTests` |
| Coordinator-Owned Shared Files | None |
| Context Required | This plan, the spec's Persistence and Seed Data sections, `.agents/references/execution.md`, `.agents/references/code-style.md`, migration metadata rules in `src/main/resources/db/migration/metadata/README.md` |
| Behavior To Preserve | Existing rows stay `ACTIVE` (all new columns nullable); seeding stays idempotent; role-grant behavior unchanged |
| Deliverables | `V12` adds `blocked_at timestamptz`, `blocked_reason varchar(255)`, `blocked_by_user_id bigint` FK to `users (id)` (metadata: `expand`, `db-first`, `rollingCompatible true`, `image-only`); `UserAccount` gains block accessors plus `block(blockedBy, reason)` / `unblock()` honoring `blocked_at is not null` as the only blocked definition; `UserDataInitializer` seeds `demo-user-002` blocked with the fixed reason, deterministic instant, null `blocked_by_user_id`; new seed test case |
| Validation Checkpoint | `./build.ps1 test --tests "team.jit.technicalinterviewdemo.business.user.UserDataInitializerTests" --tests "team.jit.technicalinterviewdemo.business.user.UserManagementIntegrationTests"` |
| Commit Checkpoint | One commit: schema, entity, seed, and their tests |

### Task 2: Status endpoint and admin API surface
| Field | Value |
| --- | --- |
| Status | Not Started |
| Goal | Expose block/unblock to ADMIN operators and surface status in the admin API (spec Status-Change Endpoint, Audit Actions for `USER_ACCOUNT`/`UPDATE`, Admin Users API Surface; AC1–AC5, AC8) |
| Owned Files Or Packages | `AdminUserManagementController`, `AdminUserManagementService`, `AdminUserAccountResponse`, new `AdminUserAccountStatusUpdateRequest`, `AdminUserManagementApiIntegrationTests` |
| Coordinator-Owned Shared Files | None |
| Context Required | This plan, the spec's endpoint/audit/API-surface sections, Task 1 result, the existing `replaceRoles` flow as the pattern for validation, metrics, and audit calls |
| Behavior To Preserve | `GET /api/admin/users` and `PUT /{id}/roles` contracts unchanged except additive response fields; role grants untouched by status changes |
| Deliverables | `PUT /api/admin/users/{id}/status` with `status` (`ACTIVE`/`BLOCKED`) + required `reason` ≤ 255; self-target rejection (400) for both directions; idempotent same-status handling with no audit write; `USER_ACCOUNT`/`UPDATE` audit entries with `targetProvider`, `targetLogin`, `previousStatus`, `status`, `reason`; metrics operation recorded; additive `accountStatus`, `blockedAt`, `blockedBy`, `blockedReason` response fields; integration tests for AC1–AC5 and AC8 |
| Validation Checkpoint | `./build.ps1 test --tests "team.jit.technicalinterviewdemo.business.user.AdminUserManagementApiIntegrationTests"` |
| Commit Checkpoint | One commit: endpoint, service, response surface, and integration tests |

### Task 3: Sign-in and active-session rejection
| Field | Value |
| --- | --- |
| Status | Not Started |
| Goal | Make blocking effective: no new sessions, no continued sessions (spec Sign-In Rejection and Active-Session Rejection; AC6, AC7) |
| Owned Files Or Packages | `AuditingAuthenticationSuccessHandler`, `AuthenticatedUserSynchronizationFilter`, `SecurityConfiguration` (wiring only if needed), `SecurityIntegrationTests` |
| Coordinator-Owned Shared Files | None |
| Context Required | This plan, the spec's rejection sections, `SessionService.logoutCurrentSession` as the session-invalidation pattern, the fake OAuth provider used by existing security tests, the existing max-sessions `SESSION_REJECTION` test as the audit-assertion pattern |
| Behavior To Preserve | Unblocked accounts: unchanged login flow, audit entries, session continuity, and per-session sync short-circuit; max-sessions rejection behavior unchanged |
| Deliverables | Blocked sign-in: no `LOGIN_SUCCESS`, no profile/`last_login_at` sync, `LOGIN_FAILURE` audit with `failureReason: "account_blocked"`, failure-style redirect, unauthenticated `GET /api/session`; blocked active session: 401 problem+json no later than the next authenticated request, session invalidated and deleted from the session repository, context cleared, `SESSION_REJECTION` audit with `failureReason: "account_blocked"`; the session-attribute sync cache must not bypass the blocked check; tests for AC6 and AC7 including the cached-session path |
| Validation Checkpoint | `./build.ps1 test --tests "team.jit.technicalinterviewdemo.technical.security.SecurityIntegrationTests"` |
| Commit Checkpoint | One commit: both rejection paths and their tests |

### Task 4: Contract artifacts and docs
| Field | Value |
| --- | --- |
| Status | Not Started |
| Goal | Move the published contract together with the behavior (spec Contract Impact; AC10) |
| Owned Files Or Packages | `AdminUserManagementApiDocumentationTests`, `src/test/resources/openapi/approved-openapi.json`, `admin-user-management-controller.http`, `suite-10-admin-user-management.http`, `docs/OPERATIONS.md` |
| Coordinator-Owned Shared Files | None |
| Context Required | This plan, the spec's Contract Impact section, `.agents/references/documentation.md` for artifact routing, `.agents/references/testing.md` OpenAPI refresh rule |
| Behavior To Preserve | Existing documented operations and snippets; baseline diff must be additive-only (new path, new schemas/fields) with no `AuditAction` enum change |
| Deliverables | REST Docs coverage for the status endpoint and new response fields; reviewed additive-only baseline refresh via `./build.ps1 refreshOpenApiBaseline`; block/rejected-request/unblock steps in the HTTP example and suite-10; `docs/OPERATIONS.md` admin user-management section mentions block/unblock |
| Validation Checkpoint | `./build.ps1 test --tests "team.jit.technicalinterviewdemo.business.user.AdminUserManagementApiDocumentationTests"` plus the OpenAPI compatibility test in the same invocation scope; `pwsh ./scripts/docs/audit-docs.ps1` for the Markdown edits |
| Commit Checkpoint | One commit: REST Docs, refreshed baseline, HTTP examples, docs |

### Task 5: Final verification and roadmap sync
| Field | Value |
| --- | --- |
| Status | Not Started |
| Goal | Prove the cumulative change and leave durable status in owning artifacts |
| Owned Files Or Packages | This plan (tracker, validation ledger), `ROADMAP.md` |
| Coordinator-Owned Shared Files | None |
| Context Required | This plan, `.agents/references/testing.md`, `.agents/references/reviews.md` for final review routing |
| Behavior To Preserve | — |
| Deliverables | Full verification of the cumulative branch; benchmark evidence for the session-path change (D6); final bug-risk and security review per `.agents/references/reviews.md` (security-sensitive change); plan tracker and `Validation Results` updated with exact commands and results; `ROADMAP.md` row moved to reflect integration state |
| Validation Checkpoint | `./build.ps1 -FullBuild build gatlingBenchmark --no-daemon` (single invocation so shared prerequisites run once) |
| Commit Checkpoint | One commit: plan/roadmap status updates (plus any review fixes in their own commits) |

## Blockers And Replan Triggers
| Trigger / Blocker | Response | Owner | Status |
| --- | --- | --- | --- |
| OpenAPI baseline diff shows any non-additive change | Stop; do not refresh the baseline; rework the contract shape or return to the spec | Agent | Open |
| `gatlingBenchmark` regresses session/OAuth scenarios beyond accepted variance | Pause; D6 is invalidated; revisit per-request enforcement mechanics with the user because the spec pins next-request latency | User | Open |
| Session deletion mechanics do not behave as `SessionService.logoutCurrentSession` suggests for filter-initiated rejection | Adjust implementation within the spec's observable behavior; replan only if 401-plus-deleted-session cannot be met | Agent | Open |
| Self-target rejection conflicts with an existing admin workflow discovered in tests | Stop and ask; AC4 is a spec invariant | User | Open |

## Edge Cases And Failure Modes
- Block committed while the target has an in-flight request: enforcement applies from the next request; AC7 wording allows this.
- Blocked check ordering: the per-request check runs after authorization, so anonymous and public-endpoint traffic is untouched; only authenticated requests pay the lookup (D6).
- Existing `2.0.x` rows and in-flight rolling deploys: new columns are nullable and unread by old instances; `expand`/`db-first` metadata covers the rollout.
- Seeded blocked user is `USER`-role only, so seed data can never reduce admin availability; self-target rejection (AC4) keeps at least one unblocked ADMIN.
- Re-seeding an existing database does not block `demo-user-002` (documented spec caveat, AC11 note).
- Unblock must restore access without touching role grants — assert roles before/after in AC2's test.

## Validation Plan
- Per-task targeted commands as named in each task's Validation Checkpoint.
- New tests: seed case (AC11), endpoint cases (AC1–AC5, AC8), security cases (AC6, AC7), REST Docs coverage (AC10 via baseline test).
- Contract checks: OpenAPI compatibility test against the refreshed baseline; docs audit script for Markdown.
- Final: `./build.ps1 -FullBuild build gatlingBenchmark --no-daemon`.
- Manual verification: extended `suite-10-admin-user-management.http` plus the User Validation walkthrough below.

## Verification Strategy
- Unit/service level: entity block/unblock invariants and validation messages where service tests already exist.
- Integration: endpoint behavior, audit entries, seed state, and both rejection paths against the real database and session store.
- Contract: REST Docs snippets and the approved OpenAPI baseline (additive-only diff).
- Benchmark: Gatling session/OAuth scenarios for the per-request lookup cost.
- Negative scenarios: 400 (payload, self-target), 401 (unauthenticated, rejected session), 403 (non-ADMIN), 404 (unknown id), blocked sign-in.

## Better Engineering Notes
- No prerequisite cleanup identified; the roles endpoint already models the patterns this feature extends.
- Deferred (spec out-of-scope): eager session deletion at block time, temporary/expiring blocks, frontend UI work. Do not let these creep in.

## Validation Results
| Date | Command | Scope | Result | Notes |
| --- | --- | --- | --- | --- |
| 2026-06-11 | `./build.ps1 test --tests "team.jit.technicalinterviewdemo.business.user.UserDataInitializerTests" --tests "team.jit.technicalinterviewdemo.business.user.UserManagementIntegrationTests"` | Task 1 | Passed (13 tests) | New seed case and block/unblock persistence case green |

## User Validation
1. Start the app with demo data and the fake OAuth provider, sign in as the bootstrap admin.
2. `GET /api/admin/users`: confirm `demo-user-002` shows `accountStatus: "BLOCKED"` with the seeded reason and null `blockedBy`; others show `ACTIVE`.
3. Block another demo user via `PUT /api/admin/users/{id}/status` with a reason; confirm the response provenance and a matching audit entry in `/api/admin/audit-logs`.
4. In a second browser session signed in as that user, make any authenticated request: expect 401 and a `SESSION_REJECTION` audit entry; a fresh sign-in attempt then fails with a `LOGIN_FAILURE` (`account_blocked`) entry.
5. Unblock the user with a reason; confirm sign-in works again and prior roles are intact.
6. Try to block your own admin account: expect 400.
