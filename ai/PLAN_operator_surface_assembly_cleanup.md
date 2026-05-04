# Plan: Operator Surface Assembly Cleanup

## Lifecycle
| Field | Value |
| --- | --- |
| Phase | Implementation |
| Status | In Progress |

## Summary
- Synthetic workflow-fixture plan: clean up the internal response-assembly flow in `technical.operator` without changing the `/api/admin/operator-surface` contract.
- The goal is to make `OperatorSurfaceService` easier to read and easier to unit-test before any later real operator-surface behavior changes land.
- Success is measured by unchanged operator-surface JSON, a new service-local test seam, and a worker branch that can execute independently in the four-plan `Parallel Plans` test batch.

## Scope
- In scope:
  - internal refactoring of `OperatorSurfaceService`
  - optional service-local tests under `technical.operator`
  - keeping the cleanup inside the operator package
- Out of scope:
  - endpoint path changes, response-shape changes, audit schema changes, or security changes
  - REST Docs, HTTP examples, README, or OpenAPI edits unless execution uncovers accidental contract drift
  - roadmap, release, or changelog consolidation work

## Current State
- `src/main/java/team/jit/technicalinterviewdemo/technical/operator/OperatorSurfaceService.java` currently performs the admin-role check, loads recent audit entries, fetches technical overview data, reads health status, and assembles the full nested response inside one method.
- The service depends directly on `AuditLogRepository`, `TechnicalOverviewService`, `HealthEndpoint`, and `ApplicationAvailability`, which makes the current method readable enough today but leaves little test-local structure for future changes.
- `RECENT_AUDIT_LIMIT` and the operator endpoint constants already live inside the service and should remain stable for this cleanup.
- `src/test/java/team/jit/technicalinterviewdemo/technical/operator/OperatorSurfaceApiIntegrationTests.java` already covers the public JSON contract and role enforcement.
- `src/test/java/team/jit/technicalinterviewdemo/technical/operator/OperatorSurfaceApiDocumentationTests.java` already backs the published docs, which should remain unchanged for this cleanup.

## Requirement Gaps And Open Questions
- No blocking user-input gaps remain.
- This plan is intentionally synthetic for workflow testing. If execution exposes a real product decision about operator diagnostics, audit payload shape, or endpoint links, stop and move that behavior change into a separate non-dummy plan.
- Fallback assumption: audit ordering, section names, endpoint links, and admin-only access all stay stable.

## Locked Decisions And Assumptions
- Keep the response schema, nested section names, and endpoint links exactly as they are today.
- Keep `RECENT_AUDIT_LIMIT` at `10`.
- Keep the admin-role guard in the service flow; do not move authorization logic into a new abstraction as part of this cleanup.
- Keep the cleanup inside `technical.operator`; do not introduce a cross-cutting diagnostics framework.
- Treat `src/docs/asciidoc/operator-surface-controller.adoc`, `src/test/resources/http/operator-surface-controller.http`, `src/test/resources/openapi/approved-openapi.json`, and `README.md` as unchanged unless execution proves observable drift.

## Execution Mode Fit
- Recommended default mode: `Parallel Plans`
- Why that mode fits best: the user explicitly wants a four-plan `Parallel Plans` test, and this slice can stay in `technical.operator` plus operator-local tests without overlapping the other dummy plans.
- Private changelog token if executed: `operator_surface_assembly_cleanup`
- Coordinator-owned or otherwise shared files if the four-plan batch later needs them:
  - `CHANGELOG.md`
  - `README.md`
  - `src/test/resources/openapi/approved-openapi.json`
- Candidate worker boundary:
  - worker owns `src/main/java/team/jit/technicalinterviewdemo/technical/operator/**`
  - worker may update `src/test/java/team/jit/technicalinterviewdemo/technical/operator/**`
  - worker leaves shared contract artifacts untouched unless the coordinator explicitly reassigns them

## Affected Artifacts
- Tests:
  - `src/test/java/team/jit/technicalinterviewdemo/technical/operator/OperatorSurfaceApiIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/operator/OperatorSurfaceApiDocumentationTests.java`
  - new `src/test/java/team/jit/technicalinterviewdemo/technical/operator/OperatorSurfaceServiceTests.java`
- Source files:
  - `src/main/java/team/jit/technicalinterviewdemo/technical/operator/OperatorSurfaceService.java`
  - optional small helper or record under `src/main/java/team/jit/technicalinterviewdemo/technical/operator/`
- Likely unchanged contract artifacts:
  - `src/docs/asciidoc/operator-surface-controller.adoc`
  - `src/test/resources/http/operator-surface-controller.http`
  - `src/test/resources/openapi/approved-openapi.json`
  - `README.md`
- Build checks:
  - `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.technical.operator.OperatorSurfaceApiIntegrationTests --tests team.jit.technicalinterviewdemo.technical.operator.OperatorSurfaceApiDocumentationTests --tests team.jit.technicalinterviewdemo.technical.operator.OperatorSurfaceServiceTests`
  - `.\gradlew.bat build`

## Execution Milestones
### Milestone 1: Add Operator-Local Service Coverage
- goal
  - add a narrow service-local test seam for response assembly before refactoring the production method.
- owned files or packages
  - `src/test/java/team/jit/technicalinterviewdemo/technical/operator/**`
- shared files that a `Shared Plan` worker must leave to the coordinator
  - none
- behavior to preserve
  - admin-only access, audit ordering, nested response sections, and current endpoint links
- exact deliverables
  - a new `OperatorSurfaceServiceTests` class or equivalent operator-local coverage for the assembled response sections and recent-audit limit
- validation checkpoint
  - `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.technical.operator.OperatorSurfaceApiIntegrationTests --tests team.jit.technicalinterviewdemo.technical.operator.OperatorSurfaceApiDocumentationTests --tests team.jit.technicalinterviewdemo.technical.operator.OperatorSurfaceServiceTests`
- commit checkpoint
  - one test-focused milestone commit

### Milestone 2: Split Response Assembly Into Clearer Internal Steps
- goal
  - make `OperatorSurfaceService` more readable without changing the published operator-surface contract.
- owned files or packages
  - `src/main/java/team/jit/technicalinterviewdemo/technical/operator/**`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/operator/**`
- shared files that a `Shared Plan` worker must leave to the coordinator
  - `CHANGELOG.md`
  - `README.md`
  - `src/docs/asciidoc/operator-surface-controller.adoc`
  - `src/test/resources/http/operator-surface-controller.http`
  - `src/test/resources/openapi/approved-openapi.json`
- behavior to preserve
  - current `/api/admin/operator-surface` JSON shape, endpoint links, and security behavior
- exact deliverables
  - one clearer internal assembly flow using helper methods or a small operator-local helper type
  - no public contract artifact edits
  - no audit-model or technical-overview contract changes
- validation checkpoint
  - `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.technical.operator.OperatorSurfaceApiIntegrationTests --tests team.jit.technicalinterviewdemo.technical.operator.OperatorSurfaceApiDocumentationTests --tests team.jit.technicalinterviewdemo.technical.operator.OperatorSurfaceServiceTests`
  - `.\gradlew.bat build`
- commit checkpoint
  - one implementation commit with plan, worker log, and private changelog updates if this plan is executed in `Parallel Plans`

## Edge Cases And Failure Modes
- The recent-audit list must stay newest-first and remain capped at `10` entries.
- The admin-only access check must still happen before returning operator data.
- The runtime and operations sections must still expose the current overview and actuator links.
- Health, liveness, and readiness values must still be populated even after the response assembly is split into smaller steps.
- The cleanup must not silently alter the public JSON shape or field names and force unexpected REST Docs or OpenAPI drift.

## Validation Plan
- Commands to run:
  - `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.technical.operator.OperatorSurfaceApiIntegrationTests --tests team.jit.technicalinterviewdemo.technical.operator.OperatorSurfaceApiDocumentationTests --tests team.jit.technicalinterviewdemo.technical.operator.OperatorSurfaceServiceTests`
  - `.\gradlew.bat build`
- Tests to add or update:
  - new operator-local service tests for response assembly and audit-limit handling
  - existing operator integration or documentation tests only where they pin visible behavior that must stay stable
- Docs or contract checks:
  - manually confirm that operator REST Docs, HTTP examples, and approved OpenAPI stay unchanged unless execution accidentally changes public behavior
- Manual verification steps:
  - call `GET /api/admin/operator-surface` as an admin and confirm the audit, runtime, and operations sections look exactly the same as before the refactor

## Better Engineering Notes
- Keep the cleanup local to `technical.operator`; the repo does not need a generic diagnostics orchestration layer.
- Prefer clearer helper methods first. Add a new helper type only if it removes real complexity.
- If execution starts to alter the operator response contract, stop and write a real public-API plan instead of expanding this dummy cleanup.

## Validation Results
- `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.technical.operator.OperatorSurfaceApiIntegrationTests --tests team.jit.technicalinterviewdemo.technical.operator.OperatorSurfaceApiDocumentationTests --tests team.jit.technicalinterviewdemo.technical.operator.OperatorSurfaceServiceTests`
  - Re-ran with `JAVA_HOME=C:\Users\kamki\.jdks\azul-25.0.3`; passed.

## User Validation
- Call `GET /api/admin/operator-surface` as an admin and confirm the audit, runtime, and operations sections, links, and values remain unchanged.
