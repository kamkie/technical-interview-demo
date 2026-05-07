# Plan: Manual Regression Execution For Stable 2.0 Transition

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Planning |
| Status | Ready |

## Summary
- Create and execute a manual regression pass against the final `2.0` release candidate, currently `v2.0.0-RC5`, that touches the app's supported user-facing functionality without trying to duplicate every automated edge-case test.
- The manual pass focuses on business functionality, session behavior, admin flows, and deployment-visible health/docs surfaces.
- This plan is required to move from the `v2.0.0-RC*` phase to stable `v2.0.0`; it is not merely another RC-preparation checklist.
- Roadmap tracking: `ROADMAP.md` tracks this under `Ordered Plan` / `Moving to 2.0` as selected stable-transition confidence work for the current final release candidate.
- Success means a human can set up the app locally, run the grouped suites below, capture pass/fail notes, and identify any release-blocking functional regressions before stable `v2.0.0` is prepared.

## Scope
- In scope:
  - local manual setup for the Spring Boot app with PostgreSQL, demo data, OAuth login, and first-admin bootstrap
  - tool recommendations for HTTP execution, browser login, cookie/CSRF capture, database inspection, and result capture
  - grouped manual suites that touch public reads, authentication/session state, authenticated account behavior, book writes, admin category/localization/user/audit/operator flows, documentation, and operational endpoints
  - concise expected results for each suite, with enough detail to execute without reading the automated tests first
  - a lightweight manual result log produced during execution
- Out of scope:
  - replacing automated tests, OpenAPI compatibility checks, REST Docs tests, Gatling benchmarks, security scans, or CI
  - exhaustive validation errors, malformed JSON, invalid sort aliases, optimistic-lock race cases, cache internals, logging sanitization, and every ProblemDetail edge case already covered by automated tests
  - browser UI testing for a separate frontend repository
  - production cluster, Helm, Kubernetes, or post-deploy smoke workflow validation unless the user explicitly expands this plan
  - changing application behavior, API contracts, OpenAPI baseline, REST Docs, HTTP examples, or setup docs

## Current State
- `README.md` defines the included app scope as public read APIs for books, categories, and localization data; authenticated account and session endpoints; admin-only audit, operator-surface, and user-management APIs; PostgreSQL runtime profiles; REST Docs; OpenAPI compatibility checks; and Gatling benchmark coverage.
- `SETUP.md` documents the local flow with Docker-backed PostgreSQL, `./build.ps1 bootRun`, OAuth setup, first-admin bootstrap through `APP_BOOTSTRAP_INITIAL_ADMIN_IDENTITIES`, CSRF handling through `GET /api/session`, and useful local endpoints.
- `ai/ARCHITECTURE.md` lists the API families that need manual coverage: `/`, `/hello`, `/docs`, `/api/books`, `/api/categories`, `/api/localizations`, `/api/account`, `/api/admin/audit-logs`, `/api/admin/operator-surface`, `/api/admin/users`, actuator endpoints, and OpenAPI docs.
- `ai/BUSINESS_MODULES.md` maps the functional owners: books, categories, localization, user accounts, and audit logs.
- `src/test/resources/http/` already contains IntelliJ HTTP Client request collections for the relevant endpoint families and should be reused as the primary manual execution harness.
- Manual execution needs at least one authenticated admin session. A second non-admin user session is strongly recommended to verify representative `403` paths without mutating the only admin account.

## Requirement Gaps And Open Questions
- Which OAuth provider should be used for manual execution of the target release candidate: GitHub, OIDC, or both?
  Fallback: use GitHub because `SETUP.md` and the HTTP examples name it first.
- Will the executor have two OAuth identities available, one admin bootstrap identity and one regular user?
  Fallback: execute all admin and authenticated-user happy paths with the admin identity, and record non-admin `403` checks as blocked or covered by automated tests.
- Should this manual pass be local-only, or should it also be repeated against the target RC container/deployment candidate?
  Fallback: local-only, because deployment smoke and release workflows already own production-like validation.
- Is `v2.0.0-RC5` still the final release candidate, or did another plan require a later RC before stable `v2.0.0`?
  Fallback: if another RC is prepared before stable release, revise this plan to target that next RC and rerun readiness review before executing the manual suites.
- Preferred result artifact location is not specified.
  Fallback: create an uncommitted or committed run log under `ai/tmp/manual-regression/v2_0_0_rc5.md` during execution, then summarize blockers in the plan's `Validation Results`.

## Locked Decisions And Assumptions
- Target release candidate is `v2.0.0-RC5`.
- This manual regression pass is the gate for transitioning from the RC phase to stable `v2.0.0`.
- If another RC is prepared for other plans before stable `v2.0.0`, this plan must be replanned for that next RC before execution; do not use stale RC5 manual results as stable-release evidence.
- Use local PostgreSQL through `docker-compose up -d` and run the app with `SPRING_PROFILES_ACTIVE=local,oauth`.
- Use demo data seeding for predictable book, category, and localization reads.
- Use `src/test/resources/http/*.http` as the primary request source. IntelliJ HTTP Client is preferred because the checked-in examples already capture `XSRF-TOKEN` into `csrfToken`.
- Use a browser for the OAuth redirect/login flow and browser developer tools to copy the `technical-interview-demo-session` cookie into the HTTP client.
- Use unique names and ISBN/message keys for write tests so repeat runs do not collide with previous manual data.
- Keep manual regression focused on representative happy paths plus a few high-value access-control failures. Automated tests remain the authority for dense edge cases.

## Execution Mode Fit
- Recommended mode: `Linear Plan`.
- This is a documentation/planning and manual validation activity. Worker fanout would add coordination overhead and make the human result log harder to interpret.
- Coordinator-owned files:
  - `ai/PLAN_manual_regression_execution.md`
  - `ROADMAP.md`
  - optional execution result log under `ai/tmp/manual-regression/v2_0_0_rc5.md`
- If delegation becomes necessary, use read-only reviewers only after the manual result log exists.

## Affected Artifacts
- Planning/roadmap:
  - add this plan file
  - update `ROADMAP.md` to track the manual regression plan for the target release candidate
- Existing manual execution inputs:
  - `src/test/resources/http/authentication.http`
  - `src/test/resources/http/book-controller.http`
  - `src/test/resources/http/category-controller.http`
  - `src/test/resources/http/localization-controller.http`
  - `src/test/resources/http/user-account-controller.http`
  - `src/test/resources/http/admin-user-management-controller.http`
  - `src/test/resources/http/audit-log-controller.http`
  - `src/test/resources/http/operator-surface-controller.http`
  - `src/test/resources/http/technical-overview-controller.http`
  - `src/test/resources/http/documentation.http`
  - `src/test/resources/http/technical-endpoints.http`
- Optional execution evidence:
  - `ai/tmp/manual-regression/v2_0_0_rc5.md`
- Contract docs/OpenAPI/HTTP examples:
  - no contract artifact changes expected
- Tests:
  - no automated tests should be added or changed for this manual regression plan

## Execution Milestones
### Milestone 1: Prepare Manual Environment
- goal: start the app in a predictable local state with OAuth and admin access
- owned files or packages:
  - optional `ai/tmp/manual-regression/v2_0_0_rc5.md`
- shared files that a `Single-Plan Fanout` worker must leave to the coordinator:
  - this plan and roadmap
- behavior to preserve:
  - no application or contract changes
- exact deliverables:
  - confirm required tools are installed:
    - PowerShell 7+
    - Java 25
    - Docker Desktop
    - Git
    - IntelliJ IDEA with HTTP Client, or Postman/Insomnia/curl plus manual cookie and CSRF handling
    - browser with developer tools
    - optional `psql`, DBeaver, or DataGrip for database inspection
    - optional `jq` for JSON inspection; PowerShell `ConvertFrom-Json` is sufficient
  - clean or prepare the local database:

```powershell
docker-compose down -v
docker-compose up -d
```

  - set OAuth and bootstrap variables, using GitHub as the default provider:

```powershell
$env:GITHUB_CLIENT_ID='<github-client-id>'
$env:GITHUB_CLIENT_SECRET='<github-client-secret>'
$env:APP_BOOTSTRAP_INITIAL_ADMIN_IDENTITIES='github:<admin-login>'
$env:SPRING_PROFILES_ACTIVE='local,oauth'
```

  - start the app:

```powershell
./build.ps1 bootRun
```

  - confirm readiness:
    - `GET http://localhost:8080/actuator/health/readiness` returns `UP`
    - `GET http://localhost:8080/api/session` returns `csrf.enabled=true`, `csrf.cookieName=XSRF-TOKEN`, and at least one login provider
  - sign in through `http://localhost:8080/api/session/oauth2/authorization/github`
  - copy the `technical-interview-demo-session` cookie into the HTTP client variables
  - run the authenticated `GET /api/session` request from `authentication.http` to capture the CSRF token
- validation checkpoint:
  - local app is running
  - admin session cookie and CSRF token are available
  - result log records tool versions, provider used, admin identity, and whether a regular-user identity is available
- commit checkpoint:
  - documentation-only checkpoint if a result log is committed; otherwise no commit required for manual execution setup

### Milestone 2: Execute Public And Technical Read Suites
- goal: verify anonymous public reads and trusted internal/devops surfaces
- owned files or packages:
  - optional `ai/tmp/manual-regression/v2_0_0_rc5.md`
- shared files that a `Single-Plan Fanout` worker must leave to the coordinator:
  - this plan and roadmap
- behavior to preserve:
  - read-only requests should not mutate persisted business data
- exact deliverables:
  - Suite A: public overview and docs:
    - `GET /` returns build/runtime/configuration overview JSON
    - `GET /hello` returns a plain smoke response
    - `GET /docs` redirects or resolves to generated docs
    - `GET /docs/index.html` loads generated REST Docs HTML
    - `GET /v3/api-docs` returns OpenAPI JSON
    - `GET /v3/api-docs.yaml` returns OpenAPI YAML
  - Suite B: actuator and observability:
    - `GET /actuator/info` returns application info
    - `GET /actuator/health`, `/actuator/health/liveness`, and `/actuator/health/readiness` return healthy status
    - `GET /actuator/prometheus` returns text metrics for trusted local inspection
  - Suite C: public book and category reads:
    - list books with pagination and sorting
    - filter books by title, author, category, and publication year range
    - load one book by id and confirm categories are included
    - list categories and confirm seeded categories are ordered/readable
  - Suite D: public localization reads:
    - list localization messages with pagination and sorting
    - filter by `messageKey` and `language`
    - get one localization by id
    - request at least one non-English language such as `pl` or `es`
- validation checkpoint:
  - each suite records pass/fail and any unexpected response shape, missing seed data, or runtime error
- commit checkpoint:
  - result-log checkpoint if committing evidence

### Milestone 3: Execute Authenticated Business Suites
- goal: verify normal authenticated user behavior and core business write workflows
- owned files or packages:
  - optional `ai/tmp/manual-regression/v2_0_0_rc5.md`
- shared files that a `Single-Plan Fanout` worker must leave to the coordinator:
  - this plan and roadmap
- behavior to preserve:
  - write tests use unique data and clean up created records when possible
  - do not rely on or mutate production data
- exact deliverables:
  - Suite E: session and account:
    - anonymous `GET /api/session` reports unauthenticated state, login providers, cookie metadata, and CSRF metadata
    - authenticated `GET /api/session` reports authenticated state and account path
    - `GET /api/account` returns the persisted current user profile and roles
    - `PUT /api/account/language` changes preferred language, and a follow-up `GET /api/account` reflects it
    - `POST /api/session/logout` returns `204`; a follow-up account request is unauthorized
  - Suite F: book lifecycle:
    - create a uniquely named book with a unique ISBN and existing categories
    - read the created book by id
    - update title, publication year, and category assignment using the returned version
    - delete the created book
    - confirm the deleted book no longer loads
  - Suite G: category lifecycle with ADMIN:
    - create a unique category
    - confirm it appears in `GET /api/categories`
    - update the category name
    - delete the unused category
    - run one representative protected failure: category creation without a session returns `401`, or with a regular user returns `403` when a regular-user identity is available
  - Suite H: localization lifecycle with ADMIN:
    - create a unique localization message key in one supported language
    - list/filter the created localization by key and language
    - update message text and description
    - delete the created localization
    - run one representative protected failure: localization creation without a session returns `401`, or with a regular user returns `403` when a regular-user identity is available
- validation checkpoint:
  - all created books, categories, and localization messages are cleaned up or intentionally recorded for cleanup
  - business happy paths pass without server errors
  - representative auth failure returns localized ProblemDetail rather than HTML or an unhandled exception
- commit checkpoint:
  - result-log checkpoint if committing evidence

### Milestone 4: Execute Admin And Audit Suites
- goal: verify admin-only operational and governance functionality
- owned files or packages:
  - optional `ai/tmp/manual-regression/v2_0_0_rc5.md`
- shared files that a `Single-Plan Fanout` worker must leave to the coordinator:
  - this plan and roadmap
- behavior to preserve:
  - do not remove the only admin grant
  - restore any test user role changes before ending the run
- exact deliverables:
  - Suite I: admin user management:
    - `GET /api/admin/users` returns persisted users with roles and role-grant provenance
    - if a regular user exists, use `PUT /api/admin/users/{id}/roles` to grant `USER, ADMIN`, confirm the response, then restore the intended role set
    - unauthenticated `GET /api/admin/users` returns `401`
    - regular-user `GET /api/admin/users` returns `403` when a regular-user identity is available
  - Suite J: audit log review:
    - `GET /api/admin/audit-logs?page=0&size=20&sort=id,desc` returns recent audit entries
    - filter audit logs by at least one action or target type produced during this manual pass, such as book, localization, category, role update, login, or logout activity
    - confirm audit entries include actor identity, action, target type/id where applicable, and timestamp
  - Suite K: operator surface:
    - `GET /api/admin/operator-surface` returns audit, runtime, and operational sections
    - unauthenticated request returns `401`
    - regular-user request returns `403` when a regular-user identity is available
- validation checkpoint:
  - admin endpoints are reachable only with admin session
  - audit log contains records for manual write actions from this run
  - operator surface gives coherent runtime/operational information
- commit checkpoint:
  - result-log checkpoint if committing evidence

### Milestone 5: Close Out Regression Evidence
- goal: produce a concise release-confidence summary and identify any blocking questions or defects
- owned files or packages:
  - optional `ai/tmp/manual-regression/v2_0_0_rc5.md`
  - this plan's `Validation Results` if the manual run is executed in this branch
- shared files that a `Single-Plan Fanout` worker must leave to the coordinator:
  - `CHANGELOG.md` unless release work is explicitly requested
- behavior to preserve:
  - release history is not updated by this manual planning pass
- exact deliverables:
  - record environment:
    - commit SHA
    - Java version
    - Docker version
    - app profile
    - OAuth provider used
    - whether admin and regular-user identities were both available
  - record suite summary:
    - Passed
    - Failed
    - Blocked
    - Not Run with reason
  - list cleanup performed and any manual data left behind
  - list release blockers separately from non-blocking observations
  - ask the user whether any additional manual surfaces should be added before final release-candidate signoff
- validation checkpoint:
  - result log exists and is understandable without reading console history
  - release-blocking failures are captured with endpoint, request file, expected result, actual result, and repro notes
- commit checkpoint:
  - `test: record rc5 manual regression results` if the user wants the evidence committed

## Edge Cases And Failure Modes
- OAuth setup may block manual execution if provider credentials or callback URLs are missing. Resolve through `SETUP.md` OAuth setup before marking suites failed.
- A clean database is recommended. Reusing old manual data can produce duplicate ISBN, duplicate category, or duplicate localization-key conflicts that are not regression failures.
- Losing the CSRF token after login or logout can cause expected `403` responses. Refresh with authenticated `GET /api/session` before unsafe writes.
- If only an admin identity is available, non-admin `403` checks are blocked rather than failed.
- If the manual pass is run against a deployed environment instead of local, do not use destructive write/delete suites unless the target data policy permits it.
- Some technical endpoints are internal/trusted surfaces, not internet-public contract. Validate them locally or through trusted deployment access only.
- Manual regression should not become an exhaustive replacement for automated tests. Keep edge-case failures in automated specs unless the issue is easiest to observe manually.

## Validation Plan
- Plan/documentation validation:
  - `git diff --check`
  - `./build.ps1 build`
- Manual execution validation:
  - complete Milestones 1 through 5
  - record pass/fail/block status for Suites A through K
  - rerun `./build.ps1 build` only if manual execution changes tracked non-lightweight files or the user requests full release signoff in the same branch

## Better Engineering Notes
- If the manual pass becomes repeated release ceremony, promote the suite list into a stable human-facing `docs/manual-regression.md` or `SETUP.md` section only after confirming maintainers want it outside AI planning files.
- If cookie/CSRF handling is painful in Postman or curl, keep IntelliJ HTTP Client as the canonical manual harness because the repo already maintains `.http` examples.
- If regular-user setup repeatedly blocks execution, add a small documented local identity-provider recipe or a manual admin role reset note in a later setup-doc plan.
- Do not expand this manual pass into benchmark, security scan, or deployment validation; those have repo-owned automated workflows.
- Replan this file when the final RC changes; update the target RC, result-log path, roadmap wording, and any changed supported-surface assumptions together.

## Validation Results
- 2026-05-06 plan creation:
  - `git diff --check` passed.
  - `./build.ps1 build` passed through the lightweight-file shortcut, reporting that only `ai/PLAN_manual_regression_execution.md` and `ROADMAP.md` changed and that the Gradle build was skipped.
- Manual execution results are not run yet.

## User Validation
- Review this plan and answer the open questions if the fallback assumptions are not acceptable.
- During execution, follow Milestones 1 through 5 and use the suite names as the manual regression checklist.
- A release-ready manual pass has no failed suites, no unresolved release blockers, and clear notes for any blocked non-admin or provider-specific checks.
