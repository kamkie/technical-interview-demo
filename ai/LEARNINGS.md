# Learnings

`ai/LEARNINGS.md` is for durable engineering wisdom that should survive refactors and still apply as this repository grows. Use the scope ladder when deciding where a new insight should live:

- **Repo-wide and durable** (still true after package moves, endpoint additions, and internal rewrites): put it in `ai/LEARNINGS.md`.
- **App-specific architecture or policy** (current package boundaries, feature ownership, runtime flows): put it in `ai/ARCHITECTURE.md`.
- **Public contract specifics** (endpoint behavior, payload shape, examples, docs): put it in executable specs, `README.md`, `src/docs/asciidoc/`, `src/test/resources/http/`, and the approved OpenAPI baseline.
- **Symbol-local contract** (one class, method, or type behavior): put it in code near the symbol.
- **Naming or API smell** (callers keep getting it wrong): prefer renaming or reshaping the API instead of adding more prose.
- Quick test: if the statement remains true after adding new endpoints and refactoring packages, it likely belongs in `ai/LEARNINGS.md`; if it depends on the current API surface or module layout, it belongs in specs or architecture notes.

## Engineering discipline

- **Spec-first beats code-first.** If behavior changes, update the artifact that defines that behavior before or alongside the implementation.
- **Do not infer contract from one code path.** Check tests, generated docs, HTTP examples, and the OpenAPI baseline before declaring how the system behaves.
- **Treat this as a small demo on purpose.** Simpler, direct code is usually better than a reusable framework built inside the repo.
- **Do not add abstraction ahead of repetition.** New layers, base classes, and generic helpers increase the interview/demo cost unless they remove a proven recurring problem.
- **Fix the real prerequisite first.** If a feature needs cleaner validation, query shape, or documentation to be correct, do that work instead of hiding the problem behind a shortcut.
- **Quality gates are product behavior.** Formatter, PMD, Error Prone, compatibility tests, and benchmark checks are not optional cleanup.
- **Inspect reality before proposing a fix.** Read the relevant code, tests, and runtime constraints first; guessing is slower than verifying.

## Contract and documentation

- **Public API changes are multi-artifact changes.** Controller code, integration tests, REST Docs, HTTP examples, OpenAPI, and `README.md` move together when the contract moves.
- **The approved OpenAPI file is a reviewed contract, not generated noise.** Refresh it only after intentionally deciding the contract changed.
- **Examples are part of the contract story.** Runnable HTTP files and REST Docs help reviewers see behavior that code alone hides.
- **Error shape is API surface area.** Changing status codes, `ProblemDetail` fields, or localization behavior is a real contract change.
- **Generated documentation belongs in the delivery pipeline.** If docs depend on tests and build metadata, keep them coupled so drift fails early.

## Spring structure and boundaries

- **Keep controllers thin.** Controllers should translate HTTP into application calls, not accumulate business rules.
- **Put non-trivial rules in services.** Validation, normalization, authorization checks, cache eviction, audit decisions, and orchestration belong there.
- **Keep repositories feature-local and concrete.** Spring Data repositories are already an abstraction; extra repository facades usually add indirection without value here.
- **Separate feature code from cross-cutting code.** Business packages should own feature behavior; `technical.*` should own framework integration and shared infrastructure concerns.
- **Prefer explicit flow over magical wiring.** Small services calling each other directly are easier to reason about than generic hook systems.

## Persistence, caching, and data access

- **PostgreSQL is the behavioral truth.** Runtime behavior and integration testing should stay aligned with PostgreSQL semantics, not an easier in-memory substitute.
- **Normalize and validate before querying.** Clean request data at the service boundary so repositories operate on deliberate inputs.
- **Query shape is part of correctness.** Fetch strategy, entity graphs, and pagination behavior matter when the API depends on associated data being present without N+1 surprises.
- **Use optimistic locking when concurrent edits are user-visible.** Concurrency conflicts should fail explicitly instead of silently overwriting state.
- **Cache only with a clear eviction story.** A cached read path is incomplete until write paths invalidate the affected entries.
- **Cache names and metrics should stay explicit.** Hidden caching makes correctness harder to audit.

## Security, localization, and errors

- **Authentication and authorization are different responsibilities.** The filter chain decides who is signed in; services still need to enforce feature-level permissions where business rules depend on roles.
- **Persisted user state is part of the security model.** External OAuth identity alone is not enough when the application tracks roles and language preferences.
- **Localization is a cross-cutting contract, not presentation garnish.** Request language resolution, user preference fallback, and localized API errors must stay coherent.
- **Machine-readable error fields matter.** Stable keys such as `messageKey` are as important as human-readable error text.
- **Global exception handling should make behavior more consistent, not more surprising.** Keep mapping centralized and deliberate.

## Observability and performance

- **State-changing operations need traceable evidence.** Success logs, audit logs where required, and metrics should make writes explainable after the fact.
- **Sanitize before logging.** Request visibility is useful only if it does not leak sensitive data.
- **Benchmarks are change detectors.** If a feature area has a tracked Gatling baseline, treat regressions as design feedback, not as a test nuisance.
- **Operational endpoints are part of the app contract.** Health, info, Prometheus, docs, and tracing support should remain usable while features evolve.

## Testing and change safety

- **Integration tests are the default source of truth for externally visible behavior.** Read them before assuming how a feature works.
- **Technical tests can encode architecture decisions on purpose.** If a refactor breaks a hardening or compatibility test, the test may be protecting a real design constraint.
- **Use service tests for dense rule validation and integration tests for end-to-end behavior.** Each test layer should prove something the other cannot prove cheaply.
- **Test data should make behavior obvious.** Deterministic, intention-revealing fixtures are worth more than clever builders.
- **Run the full build before calling work complete.** In this repository, docs generation, compatibility checks, packaging, and image build are part of the definition of done.
