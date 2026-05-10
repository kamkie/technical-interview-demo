# Durable Learnings

`.agents/references/LEARNINGS.md` stores durable repo-wide lessons that should still be true after packages move, endpoints expand, or internals are refactored.
Use `.agents/references/learning-rules.md` to decide whether an insight belongs here and how to route it.

Keep this file as curated lesson storage: short reusable habits grouped by domain, not learning policy, incident history, or task logs.

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
- **Generated docs are contract evidence.**
- **Error shape is public surface area.**
- **Quality gates are behavior.** Compatibility checks, benchmarks, static analysis, docs generation, and build gates are not optional cleanup.

## Operational Lessons

- **Use `rg --hidden` when hidden paths matter.** `rg --files` omits dotfiles and hidden directories by default, so searches that need `.agents/`, `.github/`, or other hidden paths must include `--hidden`.
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
- **Record skill-validator environment gaps explicitly.** When repo-local skill validation cannot run because the local Python environment lacks validator dependencies such as PyYAML, record the exact blocker and run a deterministic structural fallback instead of treating the skill as fully validator-checked.
- **`.gitmessage` is the AI commit-format spec.** Load `.gitmessage` and the "Commits" rules in `.agents/references/execution.md` before composing any AI-authored commit; `Co-authored-by` alone is not the full requirement. Include every applicable `Project-*` footer (`Project-Source` is mandatory) plus `Validation`, keep the trailer block contiguous, and build commits non-interactively via `git commit -F <file>` or a single final paragraph — chained `-m` arguments insert blank lines that break the trailer block. Do not pattern-match the format from older commits in `git log`; pre-existing drift is not the spec.

## Testing Lessons

- **Integration tests are the default truth for externally visible behavior.**
- **Use lower layers only when they prove something cheaper or clearer.**
- **Technical hardening tests often encode deliberate design constraints.**
- **Deterministic test data is part of readability.**
- **Validation scope can exceed the uncommitted diff.** The `./build.ps1 build` lightweight shortcut only classifies current uncommitted files; use `./build.ps1 -FullBuild build` when proving a cumulative branch, whole plan, release candidate, or implementation work already committed earlier in the task.
