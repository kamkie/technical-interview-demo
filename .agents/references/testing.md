# Testing Guide For AI Agents

`.agents/references/testing.md` owns validation scope and commands for this repository.

Use this file to decide what proof is required after a change. Use `.agents/references/documentation.md` first if you still need to determine which contract or maintainer artifacts moved.

## Validation Goals

Treat validation as part of the product:

- executable specs define real behavior
- REST Docs and OpenAPI checks catch contract drift
- benchmark and compatibility gates are not optional cleanup
- the final verification story should match the change and the plan's `Validation Results`

When a validation command fails, load `.agents/references/troubleshooting.md` on demand before choosing a recovery path.

## Pick The Smallest Sufficient Proof

- integration tests for externally visible API behavior and end-to-end feature flow
- REST Docs tests when public documentation snippets change
- OpenAPI compatibility checks when public behavior or documented schemas change
- service tests for dense rule validation that does not need full HTTP coverage
- technical integration tests when the change touches security, caching, metrics, tracing, or other cross-cutting behavior
- manual consistency review only when the work is documentation-only

Use `.agents/references/environment-quick-ref.md` for the local Gradle wrapper syntax.
Use `.agents/references/gradle-task-graph.md` when command choice depends on Gradle task overlap or prerequisites.

## Implementation Loop

- use `./build.ps1 compileJava` or a similarly narrow Gradle task for quick checks while editing
- use targeted tests when the touched behavior has focused executable coverage
- `./build.ps1 -SkipTests build`, `./build.ps1 -SkipChecks build`, and `./build.ps1 -SkipTests -SkipChecks build` are local-loop shortcuts only, not final verification
- `-SkipChecks` skips formatting, PMD, SpotBugs, Error Prone, coverage verification, vulnerability scans, and SBOM checks
- use the `build` task for final verification before handoff unless the lightweight-only shortcut applies

## Change-Type Expectations

### Public behavior changes

- validate the contract artifacts routed through `.agents/references/documentation.md`
- refresh approved OpenAPI only after intentional contract review
- rerun `./build.ps1 gatlingBenchmark` when changing book list/search behavior, localization lookup behavior, or OAuth/session startup behavior

### Internal refactors

- keep the existing specs green without unnecessary contract edits
- avoid touching OpenAPI, `README.md`, or HTTP examples unless behavior actually changed

### Documentation-only or lightweight support-file work

- run the standard wrapper build and record whether it performed heavy Gradle validation or took the lightweight-only shortcut
- repo-local skills under `.agents/skills/` count as lightweight support-file work for classifier purposes unless they accompany a non-lightweight change
- skip benchmarks, external smoke, vulnerability scans, and other heavyweight validation unless the user explicitly asks for more
- if lightweight edits accompany any non-lightweight change, validate based on the non-lightweight artifacts and repo rules

## Standard Command

```powershell
./build.ps1 build
```

Use `SETUP.md` for environment prerequisites such as Java, Docker, and formatter configuration.
Use `.agents/references/environment-quick-ref.md` for wrapper behavior.

Record the wrapper result exactly.
Use `.agents/references/environment-quick-ref.md` for wrapper behavior, including the lightweight-only shortcut and `-FullBuild`.

## Additional Rules

- refresh the approved OpenAPI baseline only with the wrapper command, for example `./build.ps1 refreshOpenApiBaseline`, after intentional contract review
- when multiple Gradle targets are required, prefer one wrapper invocation such as `./build.ps1 build gatlingBenchmark --no-daemon` so shared prerequisites run once and validation does not repeat the full build unnecessarily
- do not run overlapping Gradle validations in parallel when one task depends on the other or both write shared `build/` outputs; this includes `build` with `gatlingBenchmark`, `externalSmokeTest`, `externalDeploymentCheck`, `scheduledExternalCheck`, or similar Docker/test/report tasks
- treat failing compatibility or benchmark checks as spec failures
- fix the first real validation failure before weakening commands, tests, assertions, or contract artifacts
- if required validation cannot run, report that explicitly
- the same classifier script also powers CI push or pull-request short-circuit decisions; run it directly only when validating a diff boundary other than the current uncommitted change set

## Recording Validation

- name the exact commands that ran
- say what passed, failed, or was skipped
- keep the plan's `Validation Results` aligned with reality

## Cross-References

- use `.agents/references/documentation.md` when validation depends on which docs or contract artifacts changed
- use `.agents/references/reviews.md` for final bug-risk and security review
