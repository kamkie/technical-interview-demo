# Plan: Localization Lookup Cache Cleanup

## Lifecycle
| Field | Value |
| --- | --- |
| Phase | Integration |
| Status | Implemented |

## Summary
- Synthetic workflow-fixture plan: clean up the cache-backed lookup and fallback flow in `business.localization` without changing the public localization contract.
- The goal is to make `LocalizationService` easier to read before any later real localization behavior changes land, especially around fallback resolution and cache eviction.
- Success is measured by unchanged localized behavior, tighter localization-local tests, and one worker branch that can execute independently inside the four-plan `Parallel Plans` test batch.

## Scope
- In scope:
  - internal cleanup for lookup cache access, list or message-map cache access, and normalization helpers in `LocalizationService`
  - focused localization-local test additions or reshaping
  - keeping cache and fallback logic inside the localization feature package
- Out of scope:
  - new endpoint parameters, new supported languages, changed fallback rules, or changed pagination or sort contract
  - REST Docs, HTTP examples, README, or OpenAPI edits unless execution uncovers accidental contract drift
  - roadmap, release, or changelog consolidation work

## Current State
- `src/main/java/team/jit/technicalinterviewdemo/business/localization/LocalizationService.java` currently mixes pagination normalization, exact lookup, fallback lookup, three cache access paths, CRUD auditing, and cache eviction.
- Cache hit or miss handling is duplicated across `findByMessageKeyAndLanguageWithFallback(...)`, `getAllMessages(...)`, and `findAllByLanguage(...)`.
- Language normalization and supported-language enforcement already live inside `LocalizationService`, and the service is also responsible for preserving current sort-field restrictions.
- `src/test/java/team/jit/technicalinterviewdemo/business/localization/LocalizationServiceTests.java` covers exact lookups, fallback behavior, supported-language validation, and seeded-message completeness.
- `src/test/java/team/jit/technicalinterviewdemo/business/localization/LocalizationApiIntegrationTests.java` and `src/test/java/team/jit/technicalinterviewdemo/technical/localization/RequestLocalizationIntegrationTests.java` cover the executable behavior that must remain stable.

## Requirement Gaps And Open Questions
- No blocking user-input gaps remain.
- This plan is intentionally synthetic for workflow testing. If execution reveals a real product decision about fallback order, supported-language policy, or externally visible sorting, stop and move that behavior change into a separate non-dummy plan.
- Fallback assumption: current language normalization, supported-language validation, fallback resolution, and cache eviction behavior all stay stable.

## Locked Decisions And Assumptions
- Keep the supported-language set and the current `language` validation messages stable.
- Keep the allowed sort fields stable: `id`, `messageKey`, `language`, `createdAt`, and `updatedAt`.
- Keep the existing cache names stable:
  - `CacheNames.LOCALIZATION_LOOKUPS`
  - `CacheNames.LOCALIZATION_LISTS`
  - `CacheNames.LOCALIZATION_MESSAGE_MAPS`
- Keep the cleanup inside `business.localization`; do not create a generic cross-feature cache utility.
- Treat `src/test/java/team/jit/technicalinterviewdemo/technical/CachingAndMetricsTests.java` as coordinator-owned shared coverage if the broader four-plan batch later needs cross-cutting cache assertions.

## Execution Mode Fit
- Recommended default mode: `Parallel Plans`
- Why that mode fits best: the user explicitly wants a four-plan `Parallel Plans` test, and this slice can stay in `business.localization` and localization-local tests while avoiding overlap with the other dummy plans.
- Private changelog token if executed: `localization_lookup_cache_cleanup`
- Coordinator-owned or otherwise shared files if the four-plan batch later needs them:
  - `CHANGELOG.md`
  - `README.md`
  - `src/test/resources/openapi/approved-openapi.json`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/CachingAndMetricsTests.java`
- Candidate worker boundary:
  - worker owns `src/main/java/team/jit/technicalinterviewdemo/business/localization/**`
  - worker may update `src/test/java/team/jit/technicalinterviewdemo/business/localization/**`
  - worker may update `src/test/java/team/jit/technicalinterviewdemo/technical/localization/RequestLocalizationIntegrationTests.java` only if a narrowly scoped compatibility assertion is needed
  - worker leaves shared contract and cross-feature cache artifacts untouched unless the coordinator explicitly reassigns them

## Affected Artifacts
- Tests:
  - `src/test/java/team/jit/technicalinterviewdemo/business/localization/LocalizationServiceTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/business/localization/LocalizationApiIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/localization/RequestLocalizationIntegrationTests.java`
  - optional new localization-local cache-focused test class under `src/test/java/team/jit/technicalinterviewdemo/business/localization/`
- Source files:
  - `src/main/java/team/jit/technicalinterviewdemo/business/localization/LocalizationService.java`
  - optional new feature-local helper under `src/main/java/team/jit/technicalinterviewdemo/business/localization/`
- Likely unchanged contract artifacts:
  - `src/docs/asciidoc/localization-controller.adoc`
  - `src/test/resources/http/localization-controller.http`
  - `src/test/resources/openapi/approved-openapi.json`
  - `README.md`
- Build and benchmark checks:
  - `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.business.localization.LocalizationServiceTests --tests team.jit.technicalinterviewdemo.business.localization.LocalizationApiIntegrationTests --tests team.jit.technicalinterviewdemo.technical.localization.RequestLocalizationIntegrationTests`
  - `.\gradlew.bat gatlingBenchmark`
  - `.\gradlew.bat build`

## Execution Milestones
### Milestone 1: Lock Lookup And Fallback Expectations
- goal
  - make the current lookup, fallback, and cache-eviction expectations explicit before refactoring the service.
- owned files or packages
  - `src/test/java/team/jit/technicalinterviewdemo/business/localization/**`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/localization/RequestLocalizationIntegrationTests.java` if a compatibility assertion is needed
- shared files that a `Shared Plan` worker must leave to the coordinator
  - `src/test/java/team/jit/technicalinterviewdemo/technical/CachingAndMetricsTests.java`
- behavior to preserve
  - current fallback resolution, supported-language enforcement, cache eviction, and sort-field restrictions
- exact deliverables
  - clearer localization-local coverage for requested-language hits, fallback misses, and post-write cache invalidation expectations
- validation checkpoint
  - `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.business.localization.LocalizationServiceTests --tests team.jit.technicalinterviewdemo.business.localization.LocalizationApiIntegrationTests --tests team.jit.technicalinterviewdemo.technical.localization.RequestLocalizationIntegrationTests`
- commit checkpoint
  - one test-focused milestone commit

### Milestone 2: Simplify Cache Access And Normalization Flow
- goal
  - reduce duplicated cache access and normalization branches inside `LocalizationService` while keeping the external contract stable.
- owned files or packages
  - `src/main/java/team/jit/technicalinterviewdemo/business/localization/**`
  - `src/test/java/team/jit/technicalinterviewdemo/business/localization/**`
- shared files that a `Shared Plan` worker must leave to the coordinator
  - `CHANGELOG.md`
  - `README.md`
  - `src/docs/asciidoc/localization-controller.adoc`
  - `src/test/resources/http/localization-controller.http`
  - `src/test/resources/openapi/approved-openapi.json`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/CachingAndMetricsTests.java`
- behavior to preserve
  - the current public localization contract and localized error behavior remain unchanged
- exact deliverables
  - one clearer internal cache-access flow
  - no new cache names or fallback rules
  - no public-doc or OpenAPI edits
- validation checkpoint
  - `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.business.localization.LocalizationServiceTests --tests team.jit.technicalinterviewdemo.business.localization.LocalizationApiIntegrationTests --tests team.jit.technicalinterviewdemo.technical.localization.RequestLocalizationIntegrationTests`
  - `.\gradlew.bat gatlingBenchmark`
  - `.\gradlew.bat build`
- commit checkpoint
  - one implementation commit with plan, worker log, and private changelog updates if this plan is executed in `Parallel Plans`

## Edge Cases And Failure Modes
- Unsupported languages must still fail with the current validation message.
- Requested-language misses must still fall back to the supplied fallback language in the same order as today.
- Cache keys must keep using normalized language and message-key values so case differences do not create accidental misses.
- Create, update, and delete paths must still evict lookup, list, and message-map caches together.
- Pageable sort validation must stay stable so unsupported sort fields do not become silently accepted.

## Validation Plan
- Commands to run:
  - `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.business.localization.LocalizationServiceTests --tests team.jit.technicalinterviewdemo.business.localization.LocalizationApiIntegrationTests --tests team.jit.technicalinterviewdemo.technical.localization.RequestLocalizationIntegrationTests`
  - `.\gradlew.bat gatlingBenchmark`
  - `.\gradlew.bat build`
- Tests to add or update:
  - localization-local coverage for fallback cache behavior and post-write cache eviction
  - existing localization integration tests only where they pin externally visible behavior that must stay stable
- Docs or contract checks:
  - manually confirm that localization REST Docs, HTTP examples, and approved OpenAPI stay unchanged unless execution accidentally changes public behavior
- Manual verification steps:
  - request a localized error in a non-default language, list localizations by language, then create or update a localization and confirm the new value is visible without stale cache behavior

## Better Engineering Notes
- Keep this cleanup inside `business.localization`; the repo does not need a generic localization cache framework.
- Prefer a small helper or well-named private methods over splitting the service into more layers.
- Because this slice touches localization lookup internals, keep the benchmark rerun in scope instead of treating it as optional cleanup.

## Validation Results
- 2026-05-04 Milestone 1 completed:
  - added localization-local service coverage for normalized lookup-cache keys, fallback misses, and full lookup/list/message-map cache eviction on create, update, and delete
  - `.env` was not present in the assigned worktree, so the milestone validation used the local shell fallback with `JAVA_HOME=C:\Users\kamki\.jdks\azul-25.0.3`
  - validation run: `$env:JAVA_HOME='C:\Users\kamki\.jdks\azul-25.0.3'; $env:Path="$env:JAVA_HOME\\bin;$env:Path"; .\gradlew.bat test --tests team.jit.technicalinterviewdemo.business.localization.LocalizationServiceTests --tests team.jit.technicalinterviewdemo.business.localization.LocalizationApiIntegrationTests --tests team.jit.technicalinterviewdemo.technical.localization.RequestLocalizationIntegrationTests`
  - result: passed
- 2026-05-04 Milestone 2 completed:
  - refactored `LocalizationService` so normalized list filters, fallback lookup requests, and language-scoped cache reads use feature-local helper records and shared cache-loading methods
  - added localization-local coverage for normalized message-map and list cache keys on supported-language reads
  - the original worker stalled after Milestone 1, so the coordinator completed Milestone 2 directly in the same worktree and branch
  - validation run: `$env:JAVA_HOME='C:\Users\kamki\.jdks\azul-25.0.3'; $env:Path="$env:JAVA_HOME\\bin;$env:Path"; .\gradlew.bat test --tests team.jit.technicalinterviewdemo.business.localization.LocalizationServiceTests --tests team.jit.technicalinterviewdemo.business.localization.LocalizationApiIntegrationTests --tests team.jit.technicalinterviewdemo.technical.localization.RequestLocalizationIntegrationTests`
  - result: passed
  - validation run: `$env:JAVA_HOME='C:\Users\kamki\.jdks\azul-25.0.3'; $env:Path="$env:JAVA_HOME\\bin;$env:Path"; .\gradlew.bat gatlingBenchmark`
  - result: passed
  - validation run: `$env:JAVA_HOME='C:\Users\kamki\.jdks\azul-25.0.3'; $env:Path="$env:JAVA_HOME\\bin;$env:Path"; .\gradlew.bat build`
  - result: passed

## User Validation
- Trigger a localized error and list localizations by language before and after a localization write, then confirm fallback behavior, visible values, and response shape remain unchanged.
