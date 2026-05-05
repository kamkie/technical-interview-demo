# Plan: Operational Observability And Production Logging

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Closed |
| Status | Released |

## Summary
- Complete the selected operational-readiness roadmap work that clusters around monitoring coverage, deployment runtime expectations, synthetic checks, and production logging defaults.
- Keep these checked items in one plan because they overlap on deployment assets, monitoring guidance, logging configuration, and the repository's operational documentation.
- Success is measured by: explicit runtime and deployment expectations for alerts, dashboards, logs, traces, autoscaling, disruption budgets, and packaged smoke or synthetic checks, together with aligned production logging defaults and passing verification.

## Scope
- In scope:
  - `Add alerting and dashboards for authentication failures, session persistence health, Flyway startup failures, database saturation, and application error-rate regressions`
  - `Add log shipping and trace-export guidance that works beyond local inspection and single-node troubleshooting`
  - `Define resource requests, limits, autoscaling expectations, and disruption budgets for Kubernetes deployments`
  - `Add synthetic or scheduled external checks so the app is observed continuously after deployment, not only during release-time smoke validation`
  - `Set the root log level to INFO in the production profile`
  - `Disable ASCII color output automatically when no terminal is detected`
- Out of scope:
  - the unchecked JSON Lines logger item in the roadmap
  - changing the internet-public endpoint contract
  - replacing the current Prometheus/Grafana/Alertmanager stack choice
  - adding vendor-specific log or trace infrastructure as a required dependency

## Current State
- Monitoring assets already exist:
  - alerts in [prometheus-rule.yaml](D:\Projects\Jit\technical-interview-demo\k8s\monitoring\prometheus-rule.yaml)
  - scrape configuration in [servicemonitor.yaml](D:\Projects\Jit\technical-interview-demo\k8s\monitoring\servicemonitor.yaml)
  - Grafana dashboard assets under [monitoring](D:\Projects\Jit\technical-interview-demo\monitoring)
- Kubernetes resource requests and limits already exist in [deployment.yaml](D:\Projects\Jit\technical-interview-demo\k8s\base\deployment.yaml) and [values.yaml](D:\Projects\Jit\technical-interview-demo\helm\technical-interview-demo\values.yaml), but autoscaling and disruption-budget expectations are not yet part of the checked-in deployment contract.
- Packaged smoke validation already exists through [ExternalSmokeTests.java](D:\Projects\Jit\technical-interview-demo\src\externalTest\java\team\jit\technicalinterviewdemo\external\ExternalSmokeTests.java), the external-testing Gradle plugin under `buildSrc`, and the `CI` / `Release` workflows.
- Production logging is still not aligned with the selected roadmap items:
  - [application-prod.properties](D:\Projects\Jit\technical-interview-demo\src\main\resources\application-prod.properties) still sets `logging.level.root=WARN`
  - ANSI handling is global `DETECT` in [application.properties](D:\Projects\Jit\technical-interview-demo\src\main\resources\application.properties), so the checked logging item needs explicit confirmation, test coverage, or a better production contract
- Human-facing docs already cover monitoring and smoke validation in [README.md](D:\Projects\Jit\technical-interview-demo\README.md) and [SETUP.md](D:\Projects\Jit\technical-interview-demo\SETUP.md), but log shipping, trace export, autoscaling, and continuous post-deploy checks are still incomplete as one coherent operating story.

## Requirement Gaps And Open Questions
- The roadmap does not identify the target mechanism for synthetic or scheduled external checks.
  - Why it matters: a GitHub-scheduled workflow, Kubernetes CronJob, or third-party probe service each imply different ownership and rollout assumptions.
  - Fallback if the user does not answer: add a repo-owned scheduled GitHub Actions check that targets a configured deployment URL and reuses the repository's external smoke semantics where possible.
- The roadmap asks for log shipping and trace-export guidance but does not choose a vendor or protocol.
  - Why it matters: a vendor-specific choice would drive manifests, env vars, and docs.
  - Fallback if the user does not answer: keep the repo vendor-neutral by documenting OTLP-style trace export and structured container log shipping expectations without coupling to a specific SaaS backend.
- Some selected alerting/dashboard work may already be partially implemented.
  - Why it matters: execution should finish the missing operational slices instead of duplicating existing assets.
  - Fallback if the user does not answer: treat the current monitoring assets as baseline and extend only the missing auth/session/Flyway/database/error-rate coverage plus the missing documentation.

## Locked Decisions And Assumptions
- Keep the deployment-scoped Prometheus contract and existing monitoring asset layout.
- Keep production logging and operational observability in one plan because they overlap on runtime configuration, deployment docs, and operator expectations.
- Do not pull the unchecked JSON Lines logger work into this plan.
- Treat autoscaling and disruption budgets as deployment-contract work, not as core application code refactoring.

## Affected Artifacts
- Tests and runtime verification:
  - [ExternalSmokeTests.java](D:\Projects\Jit\technical-interview-demo\src\externalTest\java\team\jit\technicalinterviewdemo\external\ExternalSmokeTests.java)
  - [TechnicalOverviewControllerIntegrationTests.java](D:\Projects\Jit\technical-interview-demo\src\test\java\team\jit\technicalinterviewdemo\technical\info\TechnicalOverviewControllerIntegrationTests.java)
  - [HttpTracingIntegrationTests.java](D:\Projects\Jit\technical-interview-demo\src\test\java\team\jit\technicalinterviewdemo\technical\logging\HttpTracingIntegrationTests.java)
- Deployment and monitoring assets:
  - [deployment.yaml](D:\Projects\Jit\technical-interview-demo\k8s\base\deployment.yaml)
  - [values.yaml](D:\Projects\Jit\technical-interview-demo\helm\technical-interview-demo\values.yaml)
  - [servicemonitor.yaml](D:\Projects\Jit\technical-interview-demo\k8s\monitoring\servicemonitor.yaml)
  - [prometheus-rule.yaml](D:\Projects\Jit\technical-interview-demo\k8s\monitoring\prometheus-rule.yaml)
  - dashboard and alerting assets under [monitoring](D:\Projects\Jit\technical-interview-demo\monitoring)
- Runtime configuration:
  - [application.properties](D:\Projects\Jit\technical-interview-demo\src\main\resources\application.properties)
  - [application-prod.properties](D:\Projects\Jit\technical-interview-demo\src\main\resources\application-prod.properties)
  - potentially a new `logback-spring.xml` only if needed to keep production logging behavior explicit
- Workflow and docs:
  - [ci.yml](D:\Projects\Jit\technical-interview-demo\.github\workflows\ci.yml)
  - [release.yml](D:\Projects\Jit\technical-interview-demo\.github\workflows\release.yml) only if packaged validation behavior changes
  - [README.md](D:\Projects\Jit\technical-interview-demo\README.md)
  - [SETUP.md](D:\Projects\Jit\technical-interview-demo\SETUP.md)
- Build or benchmark checks:
  - `.\gradlew.bat externalSmokeTest -PexternalSmokeImageName=technical-interview-demo -PdockerImageName=technical-interview-demo`
  - `.\gradlew.bat build`

## Execution Milestones
### Milestone 1: Finish The Monitoring And Dashboard Contract
- Goal: audit the existing monitoring assets, then extend only the missing alerting/dashboard slices required by the checked roadmap items.
- Files to update:
  - [prometheus-rule.yaml](D:\Projects\Jit\technical-interview-demo\k8s\monitoring\prometheus-rule.yaml)
  - monitoring dashboard assets under [monitoring](D:\Projects\Jit\technical-interview-demo\monitoring)
  - [README.md](D:\Projects\Jit\technical-interview-demo\README.md)
  - [SETUP.md](D:\Projects\Jit\technical-interview-demo\SETUP.md)
- Behavior to preserve:
  - deployment-scoped Prometheus scraping posture
  - existing monitoring asset layout and Helm/Kustomize entry points
- Exact deliverables:
  - explicit auth/session/Flyway/database/error-rate observability coverage
  - aligned operator docs describing what each alert/dashboard slice is intended to prove

### Milestone 2: Define Continuous Post-Deploy Checks
- Goal: add a repo-owned synthetic or scheduled external verification path that complements, rather than replaces, release-time smoke validation.
- Files to update:
  - external test/build logic under `src/externalTest` or `buildSrc`
  - possibly a new scheduled workflow under `.github/workflows/`
  - [README.md](D:\Projects\Jit\technical-interview-demo\README.md)
  - [SETUP.md](D:\Projects\Jit\technical-interview-demo\SETUP.md)
- Exact deliverables:
  - one explicit continuous-check mechanism
  - documented activation requirements and target URL/credential contract
  - clear separation between local smoke, release validation, and scheduled post-deploy checks

### Milestone 3: Finish Deployment Runtime Expectations
- Goal: make autoscaling and disruption expectations part of the checked-in deployment contract.
- Files to update:
  - [deployment.yaml](D:\Projects\Jit\technical-interview-demo\k8s\base\deployment.yaml)
  - [values.yaml](D:\Projects\Jit\technical-interview-demo\helm\technical-interview-demo\values.yaml)
  - likely new HPA/PDB manifests under `k8s/base/` and Helm templates under `helm/technical-interview-demo/templates/`
- Exact deliverables:
  - explicit autoscaling expectations
  - explicit disruption-budget expectations
  - docs aligned with the checked-in manifests and chart values

### Milestone 4: Align Production Logging Defaults
- Goal: finish the selected logging roadmap items without pulling in the unchecked JSON Lines work.
- Files to update:
  - [application-prod.properties](D:\Projects\Jit\technical-interview-demo\src\main\resources\application-prod.properties)
  - [application.properties](D:\Projects\Jit\technical-interview-demo\src\main\resources\application.properties)
  - possibly a new `logback-spring.xml`
  - [README.md](D:\Projects\Jit\technical-interview-demo\README.md)
  - [SETUP.md](D:\Projects\Jit\technical-interview-demo\SETUP.md)
- Exact deliverables:
  - production root log level set to `INFO`
  - explicit no-color behavior when no TTY is present, with tests or documentation proving the contract
  - operator-facing guidance for shipping logs and exporting traces beyond single-node inspection

## Edge Cases And Failure Modes
- Scheduled external checks need a deployment target and credentials story that does not leak secrets into the repo or require unavailable infrastructure during local builds.
- Monitoring changes must not contradict the deployment-scoped Prometheus contract.
- Production logging changes must avoid making local development or test output less diagnosable.
- Adding autoscaling or PDB assets without clear defaults can make the chart/manifests harder to use in the demo environment.
- If the current ANSI `DETECT` behavior already satisfies the checked roadmap item, execution should document and test that instead of adding needless runtime complexity.

## Validation Plan
- Commands to run:
  - `.\gradlew.bat externalSmokeTest -PexternalSmokeImageName=technical-interview-demo -PdockerImageName=technical-interview-demo`
  - `.\gradlew.bat build`
  - `helm lint helm/technical-interview-demo`
  - `helm template technical-interview-demo helm/technical-interview-demo -f helm/technical-interview-demo/values-local.yaml`
  - `kubectl kustomize monitoring/grafana`
- Tests to add or update:
  - packaged smoke or workflow validation for any new scheduled external-check mechanism
  - technical overview or logging tests if runtime configuration semantics change
- Docs or contract checks:
  - keep README, SETUP, monitoring assets, and deployment manifests aligned
- Manual verification steps:
  - inspect the monitoring assets and confirm the documented alert/dashboard coverage matches the checked-in files
  - confirm production logging output uses the intended level and color behavior in a no-TTY environment

## Better Engineering Notes
- A deployment contract is only useful if manifests, charts, docs, and packaged validation all tell the same story.
- Keep vendor-neutral log shipping and trace-export guidance unless the user explicitly wants a specific backend.
- Do not silently absorb the unchecked JSON Lines logger task into this plan; that should remain a separate decision.

## Validation Results
- 2026-05-03: Ran `.\gradlew.bat build --no-daemon` on `codex/unfinished-plans-integration` with Java 25 (`C:\Users\kamki\.jdks\azul-25.0.3`).
- Result: passed.
- 2026-05-03: Ran `.\gradlew.bat externalSmokeTest -PexternalSmokeImageName=technical-interview-demo -PdockerImageName=technical-interview-demo --no-daemon`.
- Result: passed.
- Notes:
  - External smoke assertions covered readiness, docs HTML, OpenAPI JSON/YAML, JDBC-backed authenticated-session access to `GET /api/account`, and Flyway-state inspection.
- 2026-05-03: Ran `.\gradlew.bat gatlingBenchmark --no-daemon` twice because the first run failed on a tolerance-boundary regression for `oauth2-github-redirect` (`15ms` p95 versus a `14ms` threshold).
- Result: second run passed without baseline changes; final recorded `oauth2-github-redirect` p95 was `14ms`.
- 2026-05-03: Ran `helm lint helm/technical-interview-demo`.
- Result: passed.
- 2026-05-03: Ran `helm template technical-interview-demo helm/technical-interview-demo -f helm/technical-interview-demo/values-local.yaml`.
- Result: passed.
- 2026-05-03: Ran `kubectl kustomize k8s/base`, `kubectl kustomize k8s/monitoring`, and `kubectl kustomize monitoring/grafana`.
- Result: passed.

## User Validation
- Review the generated plan artifacts and confirm the three work areas match the intended deployment story:
  - monitoring and dashboards
  - continuous post-deploy checks
  - production logging defaults
- After implementation, verify a packaged smoke run still passes, the deployment assets render cleanly, and the documented logging/monitoring behavior matches what operators would actually see.
