# Testing Guide For AI Agents

`ai/TESTING.md` owns standing AI guidance for choosing tests, contract artifacts, and validation commands in this repository.

Use this file when the task changes code, contract behavior, build validation, benchmarks, or any artifact that affects what verification is required.
Use `ai/DOCUMENTATION.md` when you first need to decide which contract or maintainer artifacts must move.

## Testing Goals

Treat tests and validation as part of the product contract:

- executable specs define real behavior
- generated docs and OpenAPI checks catch contract drift
- benchmark and compatibility gates are not optional cleanup
- the final verification story should be explicit in both the change and the plan's `Validation Results`

## Picking The Right Test Layer

Use the smallest layer that proves the behavior clearly:

- integration tests for externally visible API behavior and end-to-end feature flow
- REST Docs tests when public API documentation snippets change
- OpenAPI compatibility checks when public API behavior or documented schemas change
- service tests for dense rule validation that does not need full HTTP coverage
- technical integration tests when the change touches security, caching, metrics, tracing, or other cross-cutting behavior
- documentation-only work may rely on manual consistency review when no executable behavior changed

## Change-Type Expectations

### Public behavior changes

- validate the changed contract artifacts selected through `ai/DOCUMENTATION.md`
- update or refresh approved OpenAPI only after intentional contract review
- rerun `./gradlew gatlingBenchmark` when changing book list/search behavior, localization lookup behavior, or OAuth/session startup behavior

### Internal refactors

- keep the existing specs green without unnecessary contract edits
- avoid touching OpenAPI, README, or HTTP examples unless behavior actually changed

### Workflow or documentation changes

- prefer manual consistency review of the changed narrative first
- when the only changed files are under `ai/`, treat the work as AI-guidance-only: do manual consistency review only and do not run `./gradlew.bat build` or other validation commands unless the user explicitly asks for them
- for non-`ai/` documentation or workflow edits, finish with the validation required by the affected artifacts and repository rules

## Validation Commands

Standard required validation before calling work complete:

```powershell
.\gradlew.bat build
```

Exception:

- when every changed file is under `ai/`, no build or other validation command is required; manual consistency review is sufficient unless the user explicitly asks for more validation

Additional validation rules:

- refresh the approved OpenAPI baseline only after an intentional contract review with `./gradlew refreshOpenApiBaseline`
- treat failing compatibility or benchmark checks as spec failures

## Recording Validation

When reporting validation:

- name the exact commands that ran
- say what passed and what failed
- call out anything required that could not run
- keep the plan's `Validation Results` aligned with what actually happened

## Cross-References

- use `ai/DOCUMENTATION.md` when validation implications depend on README, REST Docs, HTTP examples, changelog, roadmap, or AI-doc updates
- use `ai/CODE_STYLE.md` when deciding how broad the implementation change should be
- use `ai/REVIEWS.md` for final bug-risk and security review after validation results are in
