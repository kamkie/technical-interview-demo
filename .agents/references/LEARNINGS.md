# Learnings

`.agents/references/LEARNINGS.md` stores durable repo-wide lessons that should still be true after packages move, endpoints expand, or internals are refactored.

Use this scope ladder when deciding where a new insight belongs:

- **Repo-wide and durable:** put it in `.agents/references/LEARNINGS.md`.
- **Current structure or ownership:** put it in `docs/ARCHITECTURE.md`.
- **Product or contract direction:** put it in `docs/DESIGN.md`.
- **Public behavior or payload shape:** put it in the governing specs, published docs, HTTP examples, and approved OpenAPI.
- **Symbol-local behavior:** put it in code near the symbol.
- **Naming or API confusion:** prefer renaming or reshaping the API over adding more prose.

## When To Consider A Learning

Evaluate whether a durable repo-wide lesson should be added during the task, not only during release cleanup, when:

- a command, tool, or validation step fails in a way future agents could avoid
- the user corrects a repo assumption, workflow interpretation, or implementation direction
- a requested capability or workflow does not exist and the gap is likely to recur
- an external API, dependency, build tool, or platform behaves differently than expected
- an assumption is outdated compared with current repo truth, tool behavior, or supported contract
- a better approach is discovered for a recurring task, diagnosis path, or validation loop

Add only lessons that should survive refactors.
Do not accumulate per-incident history, one-off mistakes, or temporary workaround notes here.

## Engineering Habits

- **Spec-first beats code-first.**
- **Inspect repo truth before proposing a fix.**
- **Treat the small demo scope as an asset.** Direct code is usually better than a repo-internal framework.
- **Add abstraction only after repetition is real.**
- **Fix the real prerequisite first.** Do not hide a missing contract, validation rule, or query shape behind a shortcut.

## Boundary Lessons

- **Keep behavior near its owner.** Move code across features or into `technical.*` only when ownership really changed.
- **Prefer explicit flow over hidden indirection.**
- **Normalize and validate near the boundary that owns the rule.**
- **Persisted application state matters.** User roles and language preferences are part of runtime behavior, not just an OAuth detail.

## Contract And Change Safety

- **Public API changes are multi-artifact changes.**
- **The approved OpenAPI file is reviewed contract, not generated noise.**
- **Examples and generated docs are contract evidence.**
- **Error shape is public surface area.**
- **Quality gates are behavior.** Compatibility checks, benchmarks, static analysis, docs generation, and build gates are not optional cleanup.

## Operational Lessons

- **IntelliJ HTTP Client formatting requires an empty line.** Always include an empty line between the request line (or the last header) and a response handler script block (`> {%`). Without it, the client may fail to parse the script or the request correctly.
- **Manual Cookie headers in IntelliJ HTTP Client override the cookie jar.** If you manually specify a `Cookie` header (e.g., to pass a session token from an environment variable), the automatic cookie management is bypassed. For requests requiring CSRF protection, you must manually include both the session cookie and the `XSRF-TOKEN` cookie in the `Cookie` header for the request to be valid.
- **Initialize IntelliJ HTTP variables with file-level defaults.** To avoid "unsubstituted variable" errors when running scripts partially or when preceding handlers fail, use `@varname = default` at the top of the `.http` file for all variables used in `{{varname}}` placeholders.
- **Use "Json" suffix for variables containing raw JSON snippets.** When a variable needs to represent a JSON value that could be `null`, an array, or an object, store it as a stringified JSON in the script (e.g., `client.global.set("myVarJson", JSON.stringify(value))`) and use it without quotes in the request body (e.g., `"field": {{myVarJson}}`). This ensures `null` is sent as literal `null` and not as the string `"null"`. Always provide a valid JSON default (like `null` or `[]` or `"default"`) at the top of the file.
- **`ijhttp` CLI is preferred for CI/CD and automation.** It supports running directories (alphabetical order), automatic environment file discovery, and XML report generation (`--report`).
- **Use Docker for zero-install HTTP testing.** The `jetbrains/intellij-http-client` image allows running `.http` suites in any environment with Docker.
- **PostgreSQL behavior is the truth.**
- **Cached reads need an eviction story.**
- **State-changing operations need auditable evidence.**
- **Sanitize before logging.**
- **Benchmarks are change detectors, not noise.**

## Testing Lessons

- **Integration tests are the default truth for externally visible behavior.**
- **Use lower layers only when they prove something cheaper or clearer.**
- **Technical hardening tests often encode deliberate design constraints.**
- **Deterministic test data is part of readability.**
