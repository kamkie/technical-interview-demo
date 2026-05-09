# Frontend AI Contract

This is the backend-owned source instruction for AI agents working in a separate first-party frontend repository for Technical Interview Demo.
It is a handoff guide, not a new public API contract.

If this file conflicts with executable tests, REST Docs, the approved OpenAPI baseline, or `README.md`, follow those higher-priority backend artifacts and update this file afterward.

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

Use `src/docs/asciidoc/upgrade-1x-to-2-0.adoc` only as migration guidance.

## OpenAPI Contract Reference

Do not inline or fork the full OpenAPI JSON/YAML in this file.
The canonical machine-readable contract is `src/test/resources/openapi/approved-openapi.json`.

Current approved baseline summary:

- OpenAPI version: `3.0.1`
- Contract version marker: `APPROVED`
- Path templates: 14
- Operations: 22
- Component schemas: 40
- Security scheme: `sessionCookie` as an API key in cookie `technical-interview-demo-session`

Frontend agents must read the approved JSON before implementing endpoint clients, request/response types, or generated API bindings.
If the frontend repository needs a portable contract copy, generate or copy a separate YAML/JSON file there and record how it will be refreshed; do not paste the full spec into this Markdown guide.

## Browser Boundary

The supported frontend is a separate first-party UI that shares one public origin with this backend through reverse-proxy deployment.
Public application traffic should target only `/api/**`.

Do not design the frontend around:

- cross-origin browser calls
- CORS as a promised integration surface
- JWT issuance or bearer-token authentication
- direct `/login` or provider-specific OAuth paths outside `/api/session/**`
- public access to `/`, `/hello`, `/docs`, `/v3/api-docs*`, or `/actuator/**`

## Session And CSRF

On initial application load, call:

```http
GET /api/session
```

Treat that response as the browser bootstrap contract.
It exposes current authenticated state, `accountPath`, `loginProviders[]`, `logoutPath`, `sessionCookie`, and `csrf` metadata.

Frontend rules:

- render login choices from `loginProviders[]` and each provider's relative `authorizationPath`
- do not hard-code `/login`, `/oauth2/authorization/github`, or a single provider assumption
- use `GET /api/account` only after the session is established and the UI needs the persisted profile
- after login success or logout, call `GET /api/session` again before the next unsafe write
- for unsafe writes with a real current application session, mirror the readable CSRF cookie value into the configured CSRF request header
- prefer `csrf.cookieName` and `csrf.headerName` from `GET /api/session`; the current approved names are `XSRF-TOKEN` and `X-XSRF-TOKEN`

`POST /api/session/logout` is public and idempotent when no session exists.
When a real authenticated session exists, the frontend must include the valid same-site CSRF header.

## API Usage Rules

Respect the compact `/api/**` surface:

- public reads: books, categories, localization lookups, `GET /api/session`, logout, and OAuth bootstrap
- authenticated account behavior: `/api/account...`
- authenticated state-changing book, category, and localization operations
- admin-only audit, operator-surface, and user-management operations

Integration rules:

- use JSON unless an endpoint explicitly documents otherwise
- preserve Spring pagination conventions: `page`, `size`, and repeated `sort`
- preserve repeated filters where documented, such as repeated `category` filters for books
- include the current `version` field when updating books
- treat response messages as localized display content, not stable program logic
- use stable fields such as `messageKey`, status code, and endpoint context for branching
- do not invent backend endpoints, request fields, auth headers, or alternate transports

## Security Defaults

These rules adapt this repository's `security-best-practices` JavaScript/TypeScript web frontend guidance to the first-party UI contract.
If a future frontend repository uses React, Vue, Next.js, or another framework, load the matching framework-specific security guidance there.

- Never put private API keys, OAuth secrets, refresh tokens, session identifiers, or cookies in browser source, storage, logs, telemetry, screenshots, or generated reports.
- Keep session auth server-managed; do not copy the session cookie into JavaScript-accessible storage and do not persist `XSRF-TOKEN` outside the browser cookie/header flow.
- Treat API responses, URL data, route data, browser storage, and `postMessage` payloads as untrusted until validated for their use.
- Avoid DOM XSS sinks: `innerHTML`, `outerHTML`, `insertAdjacentHTML`, `document.write`, event-handler attributes, `eval`, `new Function`, and string-based timers.
- Prefer safe rendering primitives: framework text interpolation, `textContent`, typed components, explicit DOM node creation, and allowlist sanitization only when rich HTML is genuinely required.
- Validate navigation and URL-bearing sinks such as `window.location`, link `href`, iframe `src`, and form `action`; reject `javascript:`, unexpected protocols, and unapproved external origins.
- Use exact-origin validation for `postMessage`, explicit `targetOrigin`, shape checks for `event.data`, and no HTML rendering of message data.
- Use browser storage only for non-sensitive UI preferences, and validate stored values before use.
- Minimize third-party JavaScript; prefer pinned or self-hosted assets, Subresource Integrity for CDN assets, and narrow CSP allowlists.
- Design code to work under a strict CSP: avoid inline scripts, inline event handlers, `unsafe-inline`, and `unsafe-eval`.
- Consider Trusted Types for high-risk DOM rendering, but keep policies small, reviewed, and paired with real sanitization.
- Use module-scoped constants or explicit parsed config instead of security-sensitive `window.*` or `document.*` named properties.

Security headers, TLS termination, frame controls, and edge throttling may live in the frontend host, reverse proxy, or deployment platform.
Verify them in the destination frontend repository or runtime environment.

## Localization And Errors

The API supports `Accept-Language`, optional `lang`, and cookie `language` fallback.
Supported application languages are currently `en`, `es`, `de`, `fr`, `pl`, `uk`, and `no`.

Error payloads use localized `ProblemDetail` data and include `messageKey`, localized `message`, and resolved `language`.
Render localized feedback, but do not branch on English message text.

## UI And Design

The UI should be compact, operational, scannable, and explicit about security, session, role, and error states.
This is an interview-demo administration and catalog-management tool, not a marketing site.

Frontend agents should make these states visible:

- session bootstrap loading
- unauthenticated with zero or more login providers
- authenticated account loading separately from session state
- authenticated non-admin user
- stale session or stale CSRF failure
- localized validation/domain error
- forbidden admin operation
- backend unavailable or reverse-proxy misrouting

Prefer dense but organized tables, filters, forms, and detail panels.
Avoid generic AI visuals, purple-gradient SaaS patterns, card-heavy marketing layouts, decorative UI that hides API state, and hard-coded English-only status copy when backend localization data is available.

## Frontend Copy Instructions

When a concrete frontend repository is available:

1. Inspect that repository's AI-instruction convention first.
2. Add or adapt this file without overwriting unrelated frontend guidance.
3. Keep this backend repository as the source of truth for backend contract details.
4. Adjust local paths so frontend agents can find this file's backend source and the frontend repo's own instructions.
5. Run the smallest available frontend docs, lint, type-check, or generated-client validation.

Do not copy this file into an unrelated sibling directory just to satisfy a plan checkbox.
The destination copy remains a separate task until the frontend repository path and local instruction convention are known.
