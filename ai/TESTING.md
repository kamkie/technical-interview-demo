# Testing Guide For AI Agents

`ai/TESTING.md` owns validation scope and commands for this repository.

Use this file to decide what proof is required after a change. Use `ai/DOCUMENTATION.md` first if you still need to determine which contract or maintainer artifacts moved.

## Validation Goals

Treat validation as part of the product:

- executable specs define real behavior
- REST Docs and OpenAPI checks catch contract drift
- benchmark and compatibility gates are not optional cleanup
- the final verification story should match the change and the plan's `Validation Results`

## Pick The Smallest Sufficient Proof

- integration tests for externally visible API behavior and end-to-end feature flow
- REST Docs tests when public documentation snippets change
- OpenAPI compatibility checks when public behavior or documented schemas change
- service tests for dense rule validation that does not need full HTTP coverage
- technical integration tests when the change touches security, caching, metrics, tracing, or other cross-cutting behavior
- manual consistency review only when the work is documentation-only

## Change-Type Expectations

### Public behavior changes

- validate the contract artifacts routed through `ai/DOCUMENTATION.md`
- refresh approved OpenAPI only after intentional contract review
- rerun `./gradlew gatlingBenchmark` when changing book list/search behavior, localization lookup behavior, or OAuth/session startup behavior

### Internal refactors

- keep the existing specs green without unnecessary contract edits
- avoid touching OpenAPI, `README.md`, or HTTP examples unless behavior actually changed

### Documentation-only work

- when every changed file matches `*.md`, including AI-guidance-only changes under `ai/`, do manual consistency review only
- skip `.\gradlew.bat build`, benchmarks, external smoke, vulnerability scans, and other heavyweight validation unless the user explicitly asks for more
- if markdown edits accompany any non-markdown change, validate based on the non-markdown artifacts and repo rules

## Standard Command

```powershell
.\gradlew.bat build
```

Exception:

- if every changed file matches `*.md`, manual consistency review is sufficient unless the user explicitly asks for more validation

## Additional Rules

- refresh the approved OpenAPI baseline only with `./gradlew refreshOpenApiBaseline` after intentional contract review
- treat failing compatibility or benchmark checks as spec failures
- if required validation cannot run, report that explicitly

## Recording Validation

- name the exact commands that ran
- say what passed, failed, or was skipped
- keep the plan's `Validation Results` aligned with reality

## Cross-References

- use `ai/DOCUMENTATION.md` when validation depends on which docs or contract artifacts changed
- use `ai/REVIEWS.md` for final bug-risk and security review
