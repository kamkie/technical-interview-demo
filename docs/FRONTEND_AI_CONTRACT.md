# Frontend AI Contract

This file is the backend-owned source instruction for AI agents working in a separate first-party frontend repository for Technical Interview Demo.
It tells frontend agents which backend contract, security, API, and product-design constraints they must preserve.

It is not a new public API contract.
When this file conflicts with executable tests, REST Docs, the approved OpenAPI baseline, or `README.md`, follow those higher-priority backend artifacts and update this file afterward.

## Source Of Truth

Use this priority when deciding frontend behavior:

1. Current frontend task requirements in the frontend repository.
2. Backend executable specs and generated contract artifacts:
   - integration and REST Docs tests under `src/test/java/`
   - REST Docs sources under `src/docs/asciidoc/`
   - approved OpenAPI baseline at `src/test/resources/openapi/approved-openapi.json`
3. Backend published summaries:
   - `README.md`
   - `docs/DESIGN.md`
   - this file
4. Frontend repository conventions, design system, and AI instructions, as long as they do not contradict the backend contract.

For steady-state browser integration, start with:

- `src/docs/asciidoc/index.adoc`
- `src/docs/asciidoc/session-controller.adoc`
- `src/test/resources/openapi/approved-openapi.json`

Use `src/docs/asciidoc/upgrade-1x-to-2-0.adoc` only as migration guidance.

## Supported Browser Shape

The supported frontend is a separate first-party UI that shares one public origin with this backend through reverse-proxy deployment.
Public application traffic should target only `/api/**`.

Do not design the frontend around:

- cross-origin browser calls
- CORS as a promised integration surface
- JWT issuance
- bearer-token authentication
- direct `/login` or provider-specific OAuth paths outside `/api/session/**`
- public access to `/`, `/hello`, `/docs`, `/v3/api-docs*`, or `/actuator/**`

Authenticated browser state is session-cookie based.
The current session cookie name is published as `sessionCookie.name` by `GET /api/session`; the approved OpenAPI example is `technical-interview-demo-session`.

## Session Bootstrap

On initial application load, call:

```http
GET /api/session
```

Treat that response as the supported browser bootstrap contract.
It exposes:

- `authenticated`: whether the current request has an authenticated application session
- `accountPath`: the authenticated persisted-account endpoint, currently `/api/account`
- `loginProviders[]`: configured OAuth login bootstrap options
- `logoutPath`: the same-site logout endpoint, currently `/api/session/logout`
- `sessionCookie`: browser session cookie metadata
- `csrf`: CSRF cookie and header metadata for unsafe browser writes

When `loginProviders[]` is empty, no interactive OAuth provider is currently available.
When providers are present, render choices from the returned provider data and start login from each provider's relative `authorizationPath`.
Do not hard-code `/login`, `/oauth2/authorization/github`, or a single provider assumption.

After login success or logout, call `GET /api/session` again before any unsafe write.
The session and CSRF state may have changed.

## Account And Authorization

Use `GET /api/account` only after a session is established and the UI needs the persisted profile.
Do not use `/api/account` as anonymous session discovery; `GET /api/session` owns that workflow.

The backend remains authoritative for authorization.
Frontend route guards and hidden controls are usability aids only.
Protected endpoints return localized `ProblemDetail` responses for `401`, `403`, validation failures, and domain errors.

Admin-only surfaces include `/api/admin/audit-logs`, `/api/admin/operator-surface`, and `/api/admin/users...`.
Only expose those workflows to users whose persisted account data supports them, and still handle server rejection cleanly.

## CSRF And Unsafe Writes

Unsafe same-site browser writes use the published CSRF handshake:

1. Call `GET /api/session`.
2. Read the browser's `XSRF-TOKEN` cookie and the response metadata at `csrf.cookieName` and `csrf.headerName`.
3. For `POST`, `PUT`, and `DELETE` requests with a real current application session, send the authenticated session cookie and mirror the readable CSRF cookie value into the configured request header.

The current approved names are:

- readable cookie: `XSRF-TOKEN`
- request header: `X-XSRF-TOKEN`

Prefer the metadata from `GET /api/session` over hard-coded names.
The raw CSRF token is not exposed in the JSON response body.

`POST /api/session/logout` invalidates the current same-site browser session when present and clears the session and CSRF cookies.
It is public and idempotent, but when a real authenticated application session exists the request must include the valid CSRF header.

## API Conventions

Respect the backend's compact `/api/**` surface:

- public reads: books, categories, localization lookups, `GET /api/session`, logout, and OAuth bootstrap
- authenticated account behavior: `/api/account...`
- authenticated state-changing book, category, and localization operations
- admin-only audit, operator-surface, and user-management operations

General frontend integration rules:

- Use JSON requests and responses unless an endpoint explicitly documents otherwise.
- Preserve Spring pagination conventions: `page`, `size`, and repeated `sort` parameters.
- Preserve repeated filter parameters where documented, such as repeated `category` filters for books.
- Include the current `version` field when updating books because optimistic locking is part of the contract.
- Treat response messages as localized display content, not stable program logic.
- Use stable fields such as `messageKey`, status code, and endpoint context for branching.
- Expect tracing headers such as `X-Request-Id` or `traceparent` to appear, but do not require them for normal UI behavior.

Do not invent backend endpoints, request fields, auth headers, or alternate transport contracts.
If the frontend needs behavior not published by the backend, create a backend contract task first.

## Localization And Errors

The API supports browser-compatible localization.
Frontend requests may use:

- `Accept-Language`
- optional `lang` query parameter
- cookie `language` fallback

Supported application languages are currently `en`, `es`, `de`, `fr`, `pl`, `uk`, and `no`.

Error payloads use localized `ProblemDetail` data and include:

- `messageKey`
- localized `message`
- resolved `language`

Frontend error handling should render useful localized feedback while preserving technical detail for developer-facing or operator-facing views.
Do not branch on English message text.

## UI State Requirements

The frontend should make these states explicit:

- bootstrapping session state from `GET /api/session`
- unauthenticated with zero or more available login providers
- authenticated session with account data loading separately
- authenticated user without admin privileges
- CSRF/session refresh after login and logout
- rejected unsafe write because the session or CSRF token is stale
- localized validation or domain error
- forbidden admin operation
- backend unavailable or reverse-proxy misrouting

Keep write flows optimistic only where the backend contract supports it.
For destructive or privileged actions, prefer clear confirmation, reversible navigation, and precise failure messages.

## Design Direction For Frontend Agents

Use Anthropic's `frontend-design` skill as inspiration for intentional, high-quality interface work, but adapt it to this product's domain.
This app is an operational technical interview demo, not a marketing site.

The UI should feel:

- compact enough to inspect quickly
- polished enough to show real product judgment
- restrained, work-focused, and scannable
- explicit about security, session, role, and error states
- consistent with a first-party backend administration and catalog-management tool

Before designing a screen, decide:

- the user role and task being served
- the data density the task requires
- the strongest visual signal that makes this app memorable without obscuring the workflow
- the loading, empty, error, forbidden, and stale-session states

Prefer:

- clear information hierarchy over oversized hero sections
- dense but organized tables, filters, forms, and detail panels
- purposeful typography and spacing rather than default UI boilerplate
- cohesive color tokens with restrained accents
- accessible controls, keyboard flow, focus states, and responsive layouts
- small, meaningful motion for state change, not ornamental distraction

Avoid:

- generic AI-generated visuals
- purple-gradient SaaS landing-page patterns
- card-heavy marketing layouts as the primary app experience
- decorative UI that hides API state or role constraints
- one-note color palettes
- hard-coded English-only status copy when backend localization data is available
- visible instructions that explain UI mechanics instead of making the workflow clear

## Frontend Copy Instructions

When a concrete frontend repository is available:

1. Inspect that repository's AI-instruction convention first.
2. Add or adapt this file without overwriting unrelated frontend guidance.
3. Keep this backend repository as the source of truth for backend contract details.
4. Adjust local paths so frontend agents can find this file's backend source and the frontend repo's own instructions.
5. Run the smallest available frontend docs, lint, or type-check validation.
6. If the frontend repository has no AI-instruction convention, use this file as a starting point and name the placement decision in the destination commit or handoff.

Do not copy this file into an unrelated sibling directory just to satisfy a plan checkbox.
The destination copy is intentionally a separate task until the frontend repository path and local instruction convention are known.
