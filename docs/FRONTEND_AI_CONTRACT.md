# Frontend AI Contract

> Generated document for AI import into a separate first-party frontend repository.
> Do not edit imported copies by hand; refresh them from this backend source document.

Use this document only as frontend-agent instructions for integrating with the Technical Interview Demo backend.
It is not a standalone API contract.

## Required Backend Inputs

Before implementing endpoint clients, request/response types, generated API bindings, authentication flow, or error handling, load these backend artifacts when available:

1. `src/test/resources/openapi/approved-openapi.json`
2. REST Docs sources under `src/docs/asciidoc/`
3. `README.md`
4. this generated document

If those backend artifacts conflict with this generated summary, follow the backend artifacts above and request a refreshed generated copy.
Frontend repository conventions, design systems, and AI instructions apply only when they do not weaken this backend contract.

## External Skill References

When security or design guidance is needed, load the current skill source by URL reference only:

- https://github.com/agamm/claude-code-owasp/blob/main/.claude/skills/owasp-security/SKILL.md
- https://github.com/openai/skills/blob/main/skills/.curated/security-best-practices/SKILL.md
- https://github.com/anthropics/skills/blob/main/skills/frontend-design/SKILL.md

Do not rely on copied skill text embedded in this document.

## Approved OpenAPI Snapshot

Use `src/test/resources/openapi/approved-openapi.json` as the canonical machine-readable contract.

Current approved baseline summary:

- OpenAPI version: `3.0.1`
- Contract version marker: `APPROVED`
- Path templates: `14`
- Operations: `22`
- Component schemas: `40`
- Security scheme: `sessionCookie` as an API key in cookie `technical-interview-demo-session`

If the frontend needs a portable JSON or YAML copy, keep it as a separate generated contract artifact with a documented refresh path.
Do not treat this Markdown summary as the full API specification.

## Browser Boundary

The supported frontend is a separate first-party UI sharing one public origin with this backend through reverse-proxy deployment.
Public application traffic should target only `/api/**`.

Do not design frontend behavior around:

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
When a real authenticated session exists, include the valid same-site CSRF header.

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

## Localization And Errors

The API supports `Accept-Language`, optional `lang`, and cookie `language` fallback.
Supported application languages are currently `en`, `es`, `de`, `fr`, `pl`, `uk`, and `no`.

Error payloads use localized `ProblemDetail` data and include `messageKey`, localized `message`, and resolved `language`.
Render localized feedback, but do not branch on English message text.
