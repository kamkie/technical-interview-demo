# Plan: Operator Surface And Bootstrap

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Closed |
| Status | Released |

## Summary
- Complete the checked operational-data roadmap work that shares bootstrap behavior, operator-facing runtime visibility, and deployment guidance ownership.
- Keep backup/restore expectations, the explicit admin or operator surface, and seed-data/bootstrap separation in one plan because they all change the same operational contract and likely touch the same docs, runtime configuration, and admin-facing API surface.
- Success is measured by: a documented recovery and retention posture, a deliberate operator-facing inspection surface, explicit separation between demo seed defaults and production-safe bootstrap, and aligned executable and published specs.

## Scope
- In scope:
  - `Add database backup, retention, and restore expectations for a production-ready sample deployment`
  - `Add an explicit admin or operator surface for inspecting audit history, runtime diagnostics, and operational status without requiring direct database access`
  - `Add seed-data and bootstrap guidance that cleanly separates demo defaults from production-safe initialization behavior`
- Out of scope:
  - implementing automated backup orchestration or a repo-owned database operator
  - replacing the existing audit-log endpoint
  - changing the stable supported read/write business APIs unless the new operator surface is intentionally added alongside them
  - adding a full admin UI

## Current State
- The repo already exposes `GET /api/audit-logs` for ADMIN users, public `/` technical overview data, and public actuator health/info endpoints, but there is no explicit single operator-facing surface that ties audit visibility, runtime diagnostics, and operational status together.
- `BookDataInitializer`, `CategoryDataInitializer`, and `LocalizationDataInitializer` seed demo data automatically when the relevant tables are empty, with no checked-in separation between demo-safe and production-safe bootstrap modes.
- `README.md` and `SETUP.md` already describe migration-bearing rollback caveats, but they stop short of defining backup retention, restore expectations, or a reproducible operator story for recovery.
- The current public contract, REST Docs pages, HTTP examples, and approved OpenAPI baseline include the audit endpoint and the technical/actuator endpoints, so any new operator API is a contract-visible addition that must move through the full artifact set.

## Requirement Gaps And Open Questions
- The roadmap asks for an explicit admin or operator surface but does not define whether that means a new authenticated API endpoint, a documented composition of existing endpoints, or both.
  - Why it matters: a new endpoint changes OpenAPI, REST Docs, HTTP examples, and security rules, while docs-only composition is much smaller.
  - Fallback if the user does not answer: add one ADMIN-only operator API surface that aggregates or links the repo's existing audit and runtime signals, because the roadmap explicitly asks for a surface rather than only new documentation.
- The roadmap asks for backup, retention, and restore expectations but does not prescribe a storage platform or retention period.
  - Why it matters: platform-specific backup automation would explode the scope.
  - Fallback if the user does not answer: keep the plan vendor-neutral by documenting minimum backup/restore expectations, retention guidance, and a reproducible restore drill rather than building a backup system.
- The roadmap asks to separate demo defaults from production-safe bootstrap behavior but does not state whether prod should seed nothing by default or allow opt-in bootstrap.
  - Why it matters: this changes startup behavior and deployment docs.
  - Fallback if the user does not answer: make demo seed data explicit and opt-in outside local/test scenarios, with production-safe defaults that do not auto-seed business data.

## Locked Decisions And Assumptions
- Preserve the existing audit-log endpoint and actuator health/info endpoints; the new operator surface should compose or extend them rather than replace them.
- Treat bootstrap behavior as configuration-driven startup policy, not as ad hoc conditional code scattered across initializers.
- Keep backup guidance repo-owned and deployment-sized; do not introduce a mandatory infrastructure stack for it.
- If a new operator endpoint is added, it should be ADMIN-protected and documented as part of the supported contract.

## Affected Artifacts
- Tests:
  - `src/test/java/team/jit/technicalinterviewdemo/business/audit/AuditLogApiIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/info/TechnicalOverviewControllerIntegrationTests.java`
  - likely new integration and REST Docs tests for any new operator endpoint
  - targeted tests for bootstrap configuration behavior
- Docs:
  - `README.md`
  - `SETUP.md`
  - `src/docs/asciidoc/index.adoc`
  - `src/docs/asciidoc/audit-log-controller.adoc` if the audit docs need operator cross-links
  - likely a new Asciidoc page if a new operator endpoint is added
- OpenAPI and HTTP examples:
  - `src/test/resources/openapi/approved-openapi.json` if a new operator API is intentionally added
  - `src/test/resources/http/audit-log-controller.http`
  - likely a new HTTP example file for the operator surface
- Source files:
  - `src/main/java/team/jit/technicalinterviewdemo/business/audit/AuditLogController.java` only if cross-linking or supporting queries change
  - likely new operator-facing controller/service types under `technical.info` or a new admin-focused package
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecurityConfiguration.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/book/BookDataInitializer.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/category/CategoryDataInitializer.java`
  - `src/main/java/team/jit/technicalinterviewdemo/business/localization/seed/LocalizationDataInitializer.java`
  - likely new typed bootstrap properties under `technical.config` or a similar package
- Build or benchmark checks:
  - `.\gradlew.bat build`
  - no benchmark rerun expected unless execution unexpectedly changes OAuth/session startup behavior

## Execution Milestones
### Milestone 1: Lock The Bootstrap Contract
- Goal:
  - separate demo-friendly seed behavior from production-safe startup defaults.
- Files to update:
  - the three current data initializer classes
  - likely new typed bootstrap properties
  - `README.md`
  - `SETUP.md`
- Behavior to preserve:
  - local and test flows remain easy to exercise
  - supported languages and test fixtures remain available where existing tests rely on them
- Exact deliverables:
  - one explicit bootstrap configuration model
  - clear defaults for local/test versus production-style runs
  - focused tests proving the chosen startup behavior

### Milestone 2: Add The Operator Inspection Surface
- Goal:
  - expose an ADMIN-only operator-facing way to inspect audit history, runtime diagnostics, and operational status without database access.
- Files to update:
  - likely new controller/service classes
  - `src/main/java/team/jit/technicalinterviewdemo/technical/security/SecurityConfiguration.java`
  - integration tests, REST Docs tests, Asciidoc pages, HTTP examples, and OpenAPI baseline if the new endpoint is added
- Behavior to preserve:
  - existing `/api/audit-logs` behavior and filters
  - existing public technical overview and actuator endpoint behavior
- Exact deliverables:
  - one explicit operator surface
  - ADMIN-only auth coverage for that surface
  - aligned docs and examples for how operators use it

### Milestone 3: Document Backup, Retention, And Restore Expectations
- Goal:
  - make the production-ready sample deployment story explicit without pretending the repo already automates backup operations.
- Files to update:
  - `README.md`
  - `SETUP.md`
  - deployment docs or example assets under `k8s/` or `helm/` only if bootstrap config exposure changes there
- Exact deliverables:
  - vendor-neutral backup and restore expectations
  - minimum retention guidance
  - one reproducible restore-drill narrative aligned with the migration-bearing rollout model

## Edge Cases And Failure Modes
- Disabling seed data too aggressively can break local/demo usability or existing test assumptions if test fixtures still rely on runtime initializers.
- A new operator endpoint must not leak secrets, raw credentials, or sensitive database internals while surfacing runtime diagnostics.
- Backup guidance that ignores Flyway migration ordering or schema compatibility would be misleading rather than helpful.
- If the operator surface reuses existing actuator information, it must stay consistent with the current public technical overview and health/info contracts.
- Any new operator API is a public contract addition and must move through REST Docs, HTTP examples, and the approved OpenAPI baseline together.

## Validation Plan
- Commands to run:
  - `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.business.audit.AuditLogApiIntegrationTests --tests team.jit.technicalinterviewdemo.technical.info.TechnicalOverviewControllerIntegrationTests`
  - `.\gradlew.bat build`
- Tests to add or update:
  - integration and REST Docs coverage for the operator surface if a new endpoint is added
  - bootstrap-behavior tests for the new seed/default rules
  - OpenAPI compatibility checks and baseline refresh if the supported API intentionally expands
- Docs or contract checks:
  - keep `README.md`, `SETUP.md`, Asciidoc pages, HTTP examples, and OpenAPI aligned
  - refresh `src/test/resources/openapi/approved-openapi.json` only after intentional contract review if a new operator endpoint is introduced
- Manual verification steps:
  - start the app in a demo-friendly mode and confirm seed data is present where expected
  - start the app in a production-style mode and confirm demo data is not auto-seeded unless explicitly enabled
  - exercise the operator surface and confirm it exposes audit/runtime/operational signals without direct database queries

## Better Engineering Notes
- Keep the bootstrap separation explicit and typed; a hidden profile-only if-statement in each initializer is not a stable contract.
- The operator surface should stay small and repo-owned. One well-scoped ADMIN endpoint is more appropriate here than a full admin subsystem.
- Backup guidance is only valuable if it matches the repo's actual Flyway and deployment posture; avoid generic checklist text that ignores migration-bearing releases.

## Validation Results
- `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.business.book.BookDataInitializerTests --tests team.jit.technicalinterviewdemo.business.category.CategoryDataInitializerTests --tests team.jit.technicalinterviewdemo.business.localization.seed.LocalizationDataInitializerTests`
  - Initial attempt failed because Gradle was running on Java 11.
  - Re-ran with `JAVA_HOME=C:\Users\kamki\.jdks\azul-25.0.3`; passed.
- `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.technical.operator.OperatorSurfaceApiIntegrationTests --tests team.jit.technicalinterviewdemo.technical.operator.OperatorSurfaceApiDocumentationTests --tests team.jit.technicalinterviewdemo.business.audit.AuditLogApiIntegrationTests --tests team.jit.technicalinterviewdemo.technical.info.TechnicalOverviewControllerIntegrationTests --tests team.jit.technicalinterviewdemo.technical.docs.OpenApiIntegrationTests`
  - Re-ran with `JAVA_HOME=C:\Users\kamki\.jdks\azul-25.0.3`; passed.
- `.\gradlew.bat refreshOpenApiBaseline`
  - Re-ran with `JAVA_HOME=C:\Users\kamki\.jdks\azul-25.0.3`; passed and updated `src/test/resources/openapi/approved-openapi.json`.
- `.\gradlew.bat build`
  - Re-ran with `JAVA_HOME=C:\Users\kamki\.jdks\azul-25.0.3`; passed (tests, REST Docs, OpenAPI compatibility, static security scan, vulnerability scans, and docker build all succeeded).

## User Validation
- Run one demo-style boot and one production-style boot and compare the seeded data behavior.
- Review the operator surface response and confirm it gives operators the needed audit and runtime visibility without database access.
- Confirm the documented backup and restore expectations match the way you would actually operate a tagged release.
