# Spec: Admin Account Blocking

## Status

Approved on 2026-06-11 by explicit user instruction (Q4 resolved as yes in the same instruction)

## Date

2026-06-11

## Provenance

| Field | Value |
| --- | --- |
| Created By | Claude Code (Fable 5) |
| Created On | 2026-06-11 |
| Source Request | `ROADMAP.md` Triage row "Admin block/unblock for user accounts", accepted on 2026-06-11 for the `2.1.0` feature line |
| Generation Context | Spec drafted from repo truth: `UserAccount`, `AdminUserManagementController`/`Service`, `CurrentUserAccountService`, `AuthenticatedUserSynchronizationFilter`, `SecurityConfiguration`, `SessionService`, `AuditAction`, Flyway migrations `V5`/`V7`/`V10`, and the approved OpenAPI baseline |

## Behavior

An ADMIN can block and unblock any other persisted user account with a short operator-supplied reason.
A blocked account cannot establish a new authenticated session and cannot keep using an existing one.
The admin users API surfaces the account status and block provenance so the first-party frontend can offer Block and Unblock actions.

### Persistence

The `users` table gains three nullable columns via Flyway migration `V12__add_user_account_blocking.sql` (with the required metadata sidecar under `src/main/resources/db/migration/metadata/`):

| Column | Type | Meaning |
| --- | --- | --- |
| `blocked_at` | `timestamptz` null | UTC instant of the block; the account is blocked if and only if this column is not null |
| `blocked_reason` | `varchar(255)` null | Operator-supplied reason recorded at block time |
| `blocked_by_user_id` | `bigint` null, FK to `users (id)` | The ADMIN account that performed the block |

There is no separate boolean flag; `blocked_at is not null` is the single source of truth, so inconsistent flag/provenance states cannot exist.
Unblocking clears all three columns.
Blocking and unblocking never modify role grants; an unblocked account resumes with its prior roles.
Migration metadata: `rolloutCategory` `expand`, `deploymentOrder` `db-first`, `rollingCompatible` `true`, `rollbackPosture` `image-only`.

### Status-Change Endpoint

`PUT /api/admin/users/{id}/status` mirrors the existing `PUT /api/admin/users/{id}/roles` contract: it requires an authenticated session with the ADMIN role plus the same-site CSRF header, and it replaces the full account status.

Request body:

```json
{
  "status": "BLOCKED",
  "reason": "Abusive API usage pending review."
}
```

- `status`: required, one of `ACTIVE` or `BLOCKED`
- `reason`: required for both directions, non-blank, at most 255 characters; on block it is persisted to `blocked_reason`, on unblock it is recorded only in the audit entry

Responses (same problem-detail family as the roles endpoint, `ApiProblemResponse` for errors):

| Code | When |
| --- | --- |
| 200 | Status replaced (or already in the requested state); body is the updated `AdminUserAccountResponse` |
| 400 | Invalid payload, or the authenticated ADMIN targets their own account |
| 401 | Missing or invalid authenticated session |
| 403 | Authenticated user does not have the ADMIN role |
| 404 | Persisted user `{id}` does not exist |

Rules:

- Self-targeting is rejected with 400 regardless of requested status. Because the caller must be an unblocked ADMIN to reach the endpoint, this guarantees at least one unblocked ADMIN always remains; no separate "last admin" guard is needed.
- The endpoint is idempotent: requesting the current status returns 200 with the unchanged resource, writes no audit entry, and does not overwrite existing block provenance.
- Blocking any non-self account is allowed, including other ADMIN accounts.

### Sign-In Rejection

When a blocked account completes the OAuth flow at the provider, the application refuses to establish a usable session:

- no `LOGIN_SUCCESS` audit entry is written; instead a `LOGIN_FAILURE` entry is recorded with `details` containing `failureReason: "account_blocked"` plus the provider and login
- the account row is not updated: `last_login_at`, display name, and email synchronization are skipped
- the browser is redirected as on other authentication failures, and a subsequent `GET /api/session` reports an unauthenticated session

### Active-Session Rejection

Any authenticated request from an account that is blocked is rejected:

- the response is `401` with an `application/problem+json` body (the session is no longer honored; re-authentication then fails per sign-in rejection)
- the backing session is invalidated and deleted from the Spring Session repository, and the security context is cleared
- a `SESSION_REJECTION` audit entry is recorded with `details` containing `failureReason: "account_blocked"` plus the provider and login

Enforcement latency: a block takes effect no later than the blocked account's next authenticated request after the block commits.
This means the per-session synchronization shortcut in `AuthenticatedUserSynchronizationFilter` (session-attribute cache) must not let a blocked account keep an authenticated session alive; the blocked check is evaluated on every authenticated request.
Eager session deletion at block time (looking up the target's sessions by principal name) is out of scope: the principal name indexed by Spring Session is the OAuth provider's name attribute, which is not reliably derivable from the persisted `provider` + `external_login` pair, and per-request rejection already bounds the exposure window.

### Audit Actions

No `AuditAction` enum values are added.
The published OpenAPI baseline (`src/test/resources/openapi/approved-openapi.json`) exposes the enum in responses, and extending a response enum is a compatibility risk the stable `2.x` line does not need to take.
Existing values are reused:

| Event | Target Type | Action | Distinguishing Details |
| --- | --- | --- | --- |
| Block via endpoint | `USER_ACCOUNT` | `UPDATE` | `targetProvider`, `targetLogin`, `previousStatus`, `status`, `reason` |
| Unblock via endpoint | `USER_ACCOUNT` | `UPDATE` | `targetProvider`, `targetLogin`, `previousStatus`, `status`, `reason` |
| Rejected sign-in | `AUTHENTICATION` | `LOGIN_FAILURE` | `failureReason: "account_blocked"`, `provider`, `login` |
| Rejected active session | `AUTHENTICATION` | `SESSION_REJECTION` | `failureReason: "account_blocked"`, `provider`, `login` |

### Admin Users API Surface

`AdminUserAccountResponse` gains additive fields, returned by both `GET /api/admin/users` and the mutation endpoints:

| Field | Type | Meaning |
| --- | --- | --- |
| `accountStatus` | string enum `ACTIVE` / `BLOCKED` | Derived from `blocked_at` |
| `blockedAt` | UTC instant, null when active | Copy of `blocked_at` |
| `blockedBy` | string, null when active | `external_login` of the ADMIN that performed the block |
| `blockedReason` | string, null when active | Persisted operator reason |

### Seed Data

When demo seeding is enabled (`app.bootstrap.seed.demo-data`), `UserDataInitializer` seeds exactly one blocked example user so the first-party frontend can develop against a visible `BLOCKED` row: `demo-user-002` is created with `blocked_at` set to a fixed deterministic instant, `blocked_reason` `"Seeded demo blocked account."`, and `blocked_by_user_id` null (no persisted operator performed the block; `blockedBy` is null in the admin API for this row).
Because seeding skips users that already exist, the blocked state applies only to freshly seeded databases; existing databases keep their current `demo-user-002` row unchanged, which is acceptable for demo data.

## Scope

In scope:

- `users` schema expansion (`V12` migration plus metadata sidecar) and `UserAccount` entity support
- `PUT /api/admin/users/{id}/status` endpoint with validation, self-target rejection, idempotency, and audit
- sign-in rejection and per-request active-session rejection for blocked accounts, with audit entries
- additive `AdminUserAccountResponse` fields
- one seeded blocked example user (`demo-user-002`) in the demo seed data
- REST Docs, OpenAPI baseline refresh, reviewer HTTP examples, and integration-test coverage for all of the above

Out of scope:

- eager deletion of a blocked account's sessions at block time (per-request rejection is the required mechanism)
- temporary or expiring blocks, block schedules, or block history beyond the latest block provenance and audit entries
- changes to role grants, role endpoints, or bootstrap-admin behavior
- surfacing block status on `/api/account` or other self-service endpoints (a blocked account cannot authenticate to read them)
- first-party frontend UI changes (the API additions are the enabler; UI work is a separate workstream)
- blocking-related admin UI rate limits or approval workflows

## Contract Impact

- Public API: one new endpoint `PUT /api/admin/users/{id}/status`; additive response fields on `AdminUserAccountResponse`; new 401-on-blocked-session behavior for authenticated requests. All changes are additive; no existing field, status code, or enum value changes.
- REST Docs: new documented operation in `AdminUserManagementApiDocumentationTests` covering the status endpoint and the new response fields.
- OpenAPI: `src/test/resources/openapi/approved-openapi.json` must be regenerated and reviewed; the diff must show only additive changes (new path, new schema fields, new request schema).
- README or examples: extend `src/manualTests/http/examples/admin-user-management-controller.http` and `src/manualTests/http/suites/suite-10-admin-user-management.http` with block/unblock calls; mention the capability where the admin user-management API is described (`docs/OPERATIONS.md`, `.agents/references/architecture.md` endpoint list if it enumerates operations).

## Acceptance Criteria

| ID | Criterion | Notes |
| --- | --- | --- |
| AC1 | `PUT /api/admin/users/{id}/status` with `{"status":"BLOCKED","reason":...}` by an ADMIN persists `blocked_at`, `blocked_reason`, `blocked_by_user_id`, returns 200 with `accountStatus: "BLOCKED"` and populated block provenance fields, and writes a `USER_ACCOUNT`/`UPDATE` audit entry with `previousStatus`, `status`, and `reason` | Roles are unchanged by the block |
| AC2 | The same endpoint with `{"status":"ACTIVE","reason":...}` on a blocked account clears all three block columns, returns 200 with `accountStatus: "ACTIVE"` and null provenance fields, and writes a `USER_ACCOUNT`/`UPDATE` audit entry | Prior roles are intact after unblock |
| AC3 | Requesting the current status returns 200 with the unchanged resource, writes no audit entry, and preserves existing block provenance | Idempotency |
| AC4 | An ADMIN targeting their own account receives 400 with a problem-detail body, for both `BLOCKED` and `ACTIVE` | Guarantees an unblocked ADMIN always remains |
| AC5 | Missing/blank `reason`, `reason` over 255 characters, missing `status`, or an unknown `status` value return 400; unauthenticated callers get 401; non-ADMIN callers get 403; unknown `{id}` returns 404 | Matches the roles-endpoint error family |
| AC6 | A blocked account completing the OAuth flow gets no usable session: `GET /api/session` reports unauthenticated, a `LOGIN_FAILURE` audit entry with `failureReason: "account_blocked"` is written, no `LOGIN_SUCCESS` entry is written, and `last_login_at` is unchanged | Sign-in rejection |
| AC7 | An authenticated request from an account blocked after sign-in returns 401 problem+json no later than the next request after the block commits, the session is deleted from the session repository, and a `SESSION_REJECTION` audit entry with `failureReason: "account_blocked"` is written | Per-request enforcement; the session-attribute sync cache must not bypass the check |
| AC8 | `GET /api/admin/users` includes `accountStatus` for every user and the three nullable block provenance fields | Additive response change only |
| AC9 | Flyway migration `V12` applies cleanly on an existing `2.0.x` database and carries a metadata sidecar with `rolloutCategory: "expand"`, `rollingCompatible: true` | Old app instances keep working during rollout because all new columns are nullable and unread by `2.0.x` |
| AC10 | The regenerated OpenAPI baseline diff against `approved-openapi.json` contains only additive changes | No `AuditAction` enum extension |
| AC11 | On a freshly seeded database with demo data enabled, `demo-user-002` is blocked with the fixed seed reason, a set `blockedAt`, and null `blockedBy`; all other seeded users are `ACTIVE` | Existing databases keep their current `demo-user-002` row because seeding skips existing users |

## Validation Mapping

| Spec Item | Validation |
| --- | --- |
| AC1–AC5 | `AdminUserManagementApiIntegrationTests` (new cases) plus REST Docs coverage in `AdminUserManagementApiDocumentationTests` |
| AC6, AC7 | `SecurityIntegrationTests` (new cases alongside the existing `SESSION_REJECTION` max-sessions test) |
| AC8 | `AdminUserManagementApiIntegrationTests` list assertion plus REST Docs response-field documentation |
| AC9 | Integration-test application startup against the migrated schema; metadata sidecar reviewed in change review |
| AC10 | Existing OpenAPI compatibility test against the refreshed `src/test/resources/openapi/approved-openapi.json` |
| AC11 | `UserDataInitializerTests` (new case for the seeded blocked user) |
| Manual verification | `suite-10-admin-user-management.http` extended with block, rejected-request, and unblock steps |

## Open Questions

| ID | Question | Owner | Status | Decision Or Fallback |
| --- | --- | --- | --- | --- |
| Q1 | Endpoint shape: single `PUT /{id}/status` replacement vs `POST /{id}/block` + `POST /{id}/unblock` action endpoints | Agent | Answered | `PUT /{id}/status`: mirrors the existing `PUT /{id}/roles` full-replacement convention and keeps idempotency trivial |
| Q2 | Extend `AuditAction` with `ACCOUNT_BLOCKED`/`ACCOUNT_UNBLOCKED` vs reuse existing values | Agent | Answered | Reuse `UPDATE`, `LOGIN_FAILURE`, `SESSION_REJECTION` with distinguishing `details`; extending a response-exposed enum is a needless `2.x` compatibility risk |
| Q3 | Per-request rejection HTTP status: 401 vs 403 | Agent | Answered | 401: the session is no longer honored, matching the "missing or invalid authenticated session" contract wording and prompting re-authentication, which then fails visibly |
| Q4 | Should demo seed data include a blocked example user for frontend development | User | Answered | Yes (user decision, 2026-06-11): seed exactly one blocked example user, `demo-user-002`, on fresh databases; see the Seed Data section |

## Linked Artifacts

- ADRs: `docs/decisions/0001-adopt-pre-planning-artifacts.md` (artifact routing that selected a standalone spec)
- PRDs: none (product intent was already clear at triage)
- Plans: none yet; create `.agents/plans/PLAN_admin_account_blocking.md` once this spec is `Approved`
