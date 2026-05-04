# Learnings

`ai/LEARNINGS.md` stores durable repo-wide lessons that should still be true after packages move, endpoints expand, or internals are refactored.

Use this scope ladder when deciding where a new insight belongs:

- **Repo-wide and durable:** put it in `ai/LEARNINGS.md`.
- **Current structure or ownership:** put it in `ai/ARCHITECTURE.md` or `ai/BUSINESS_MODULES.md`.
- **Product or contract direction:** put it in `ai/DESIGN.md`.
- **Public behavior or payload shape:** put it in the governing specs, published docs, HTTP examples, and approved OpenAPI.
- **Symbol-local behavior:** put it in code near the symbol.
- **Naming or API confusion:** prefer renaming or reshaping the API over adding more prose.

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
