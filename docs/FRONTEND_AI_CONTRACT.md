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

## External Skill References

Do not inline skill bodies or copied recommendation blocks into this document.
When a frontend agent needs security or design guidance, load the current skill source by URL reference only:

- https://github.com/agamm/claude-code-owasp/blob/main/.claude/skills/owasp-security/SKILL.md
- https://github.com/openai/skills/blob/main/skills/.curated/security-best-practices/SKILL.md
- https://github.com/anthropics/skills/blob/main/skills/frontend-design/SKILL.md

If the destination frontend repository has local skills or AI instructions, follow them only when they do not weaken this backend contract.

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

## Localization And Errors

The API supports `Accept-Language`, optional `lang`, and cookie `language` fallback.
Supported application languages are currently `en`, `es`, `de`, `fr`, `pl`, `uk`, and `no`.

Error payloads use localized `ProblemDetail` data and include `messageKey`, localized `message`, and resolved `language`.
Render localized feedback, but do not branch on English message text.

## Frontend Copy Instructions

When a concrete frontend repository is available:

1. Inspect that repository's AI-instruction convention first.
2. Add or adapt this file without overwriting unrelated frontend guidance.
3. Keep this backend repository as the source of truth for backend contract details.
4. Adjust local paths so frontend agents can find this file's backend source and the frontend repo's own instructions.
5. Run the smallest available frontend docs, lint, type-check, or generated-client validation.

Do not copy this file into an unrelated sibling directory just to satisfy a plan checkbox.
The destination copy remains a separate task until the frontend repository path and local instruction convention are known.
