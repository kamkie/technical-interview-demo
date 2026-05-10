# Durable Learnings

`docs/LEARNINGS.md` stores durable repo-wide lessons that should still be true after packages move, endpoints expand, or internals are refactored.
Use `.agents/references/learning-rules.md` to decide whether an insight belongs here and how to route it.

Keep this file as curated lesson storage: short reusable lessons grouped by domain, not learning policy, incident history, task logs, or durable rules already owned by focused guides.

## Boundary Lessons

- **Prefer explicit flow over hidden indirection.**
- **Normalize and validate near the boundary that owns the rule.**
- **Persisted application state matters.** User roles and language preferences are part of runtime behavior, not just an OAuth detail.

## Manual HTTP Lessons

- **Manual Cookie headers in IntelliJ HTTP Client override the cookie jar.** If you manually specify a `Cookie` header (e.g., to pass a session token from an environment variable), the automatic cookie management is bypassed. For requests requiring CSRF protection, you must manually include both the session cookie and the `XSRF-TOKEN` cookie in the `Cookie` header for the request to be valid.
- **Initialize IntelliJ HTTP variables with file-level defaults.** To avoid "unsubstituted variable" errors when running scripts partially or when preceding handlers fail, use `@varname = default` at the top of the `.http` file for all variables used in `{{varname}}` placeholders.
- **Use "Json" suffix for variables containing raw JSON snippets.** When a variable needs to represent a JSON value that could be `null`, an array, or an object, store it as a stringified JSON in the script (e.g., `client.global.set("myVarJson", JSON.stringify(value))`) and use it without quotes in the request body (e.g., `"field": {{myVarJson}}`). This ensures `null` is sent as literal `null` and not as the string `"null"`. Always provide a valid JSON default (like `null` or `[]` or `"default"`) at the top of the file.

## Runtime And Data Lessons

- **PostgreSQL behavior is the truth.**
- **Cached reads need an eviction story.**
- **Sanitize before logging.**

## Testing Lessons

- **Technical hardening tests often encode deliberate design constraints.**
- **Deterministic test data is part of readability.**
