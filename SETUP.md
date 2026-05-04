# Developer Setup Guide

This guide is the fastest path to a working local environment for `technical-interview-demo`.

## Choose A Workflow

Use one of these paths:

- **Local shell + JDK 25** for the shortest feedback loop
- **VS Code dev container** if you want a prebuilt toolchain with Docker, PostgreSQL, and Prometheus

Both paths end up using the same Gradle wrapper and the same application code.

## Prerequisites

Install the tools that match your workflow:

- Java 25
- Git
- Docker Desktop if you want PostgreSQL, container builds, the VS Code dev container, or to run the integration test and build lifecycles
- IntelliJ IDEA or VS Code if you want IDE support

## Quick Start

### PowerShell

```powershell
$env:JAVA_HOME='<path-to-jdk-25>'
$env:Path="$env:JAVA_HOME\bin;$env:Path"

docker-compose up -d
.\gradlew.bat bootRun
```

### Bash

```bash
export JAVA_HOME='<path-to-jdk-25>'
export PATH="$JAVA_HOME/bin:$PATH"

docker-compose up -d
./gradlew bootRun
```

The default `local` profile expects PostgreSQL on `localhost:5432`. The included `docker-compose.yml` starts that database for you. After startup, open:

- `http://localhost:8080/`
- `http://localhost:8080/hello`
- `http://localhost:8080/api/books`
- `http://localhost:8080/docs`
- `http://localhost:8080/actuator/health`

## Environment Variables

`.env.example` contains the supported shell variables for local work. The project does **not** auto-load `.env`, so treat it as a template:

1. Copy `.env.example` to `.env` if you want a private local reference file.
2. Export the values in your shell, IDE run configuration, or Docker Compose environment.
3. In PowerShell, you can dot-source the repo helper to load `.env` into the current shell session:

```powershell
. .\scripts\load-dotenv.ps1
```

That helper is intended for interactive PowerShell sessions and AI-agent command shells. Dot-sourcing matters: running the script normally starts a child scope and will not persist environment changes back into your current shell.
For Windows-style paths, the helper accepts both normal forms such as `C:\Users\kamki\.jdks\azul-25.0.3` and escaped forms such as `C:\\Users\\kamki\\.jdks\\azul-25.0.3`.

Placeholder values such as `<path-to-jdk-25>` in `.env.example` should be replaced with paths from your own machine.

Variables you are most likely to need:

- `JAVA_HOME` for Gradle and the toolchain
- `IDEA_HOME` or `IDEA_FORMATTER_BINARY` for Spotless Java formatting
- `SPRING_PROFILES_ACTIVE` if you want to override the default `local` profile
- `DATABASE_*` variables when overriding the default PostgreSQL connection
- `GITHUB_CLIENT_ID` and `GITHUB_CLIENT_SECRET` for the built-in GitHub provider when enabling the optional `oauth` profile
- `OIDC_CLIENT_ID`, `OIDC_CLIENT_SECRET`, and `OIDC_ISSUER_URI` for the built-in issuer-driven OIDC provider when enabling `oauth`
- `ADMIN_LOGINS` when you want one or more external logins to receive the persisted `ADMIN` role
- `APP_BOOTSTRAP_SEED_DEMO_DATA` when you want to override demo-data seeding for categories, books, and localization messages
- `SESSION_COOKIE_SECURE` when you want to override the `prod` profile session-cookie default of `true` for local HTTP testing or a specific deployment environment

## Deployment Contract

The checked-in deployment assets intentionally freeze this posture:

- only `/api/**` is intended to be internet-reachable, through `waf -> frontend -> this application`
- `GET /`, `GET /hello`, `GET /docs`, `GET /v3/api-docs`, `GET /v3/api-docs.yaml`, and `GET /actuator/**` are internal or devops-only validation surfaces
- `prod` is the default deployment profile in the raw manifests and Helm chart
- browser sessions use secure cookies by default through `SESSION_COOKIE_SECURE=true`
- OAuth stays opt-in through the `oauth` profile; bare `prod` does not require identity-provider credentials
- admin bootstrap remains environment-driven through `ADMIN_LOGINS`
- demo data bootstrap defaults to disabled in `prod` and enabled in `local` and `test` through `APP_BOOTSTRAP_SEED_DEMO_DATA`
- CSRF remains disabled as a deliberate demo tradeoff for reviewer-oriented session workflows
- trusted reverse-proxy headers are part of the `prod` runtime contract through `server.forward-headers-strategy=framework`
- `GET /actuator/prometheus` stays supported for trusted deployment scraping, but it is not part of the internet-public endpoint contract

The deployment story is standardized around these artifacts:

- GitHub Actions workflows for CI validation and tag-based release publishing
- a Docker image built from the packaged Spring Boot boot jar
- vendor-neutral Kubernetes manifests under `k8s/`
- a matching Helm chart under `helm/technical-interview-demo`
- monitoring and alerting assets for Prometheus, Grafana, and Alertmanager

Required runtime environment variables for deployed environments:

- `DATABASE_HOST`
- `DATABASE_PORT`
- `DATABASE_NAME`
- `DATABASE_USER`
- `DATABASE_PASSWORD`

Optional runtime environment variables:

- `SESSION_COOKIE_SECURE` with a secure-by-default value of `true`
- `GITHUB_CLIENT_ID` and `GITHUB_CLIENT_SECRET` for the built-in GitHub provider when the `oauth` profile is active
- `OIDC_CLIENT_ID`, `OIDC_CLIENT_SECRET`, and `OIDC_ISSUER_URI` for the built-in issuer-driven OIDC provider when the `oauth` profile is active
- `ADMIN_LOGINS`
- `APP_BOOTSTRAP_SEED_DEMO_DATA` when you need to opt in or out of demo-data seeding outside the profile defaults

CI and release workflow expectations:

- `CI` runs on pull requests to `main` and pushes to `main`
- Dependabot opens grouped weekly pull requests for Gradle, GitHub Actions, and Docker, and those PRs use the same `CI` workflow as human-authored PRs
- `CI` uses JDK 25, Gradle dependency caching, explicit Docker availability checks, `./gradlew build`, uploads `build/reports/jacoco/test/jacocoTestReport.xml` to Codecov, publishes security/static-analysis/SBOM artifact bundles from `build/reports/`, and then runs `./gradlew externalSmokeTest`
- the scheduled `Post-Deploy Smoke` workflow runs `./gradlew scheduledExternalCheck` every six hours and on manual dispatch, using `EXTERNAL_CHECK_BASE_URL` plus optional `EXTERNAL_CHECK_JDBC_URL`, `EXTERNAL_CHECK_JDBC_USER`, and `EXTERNAL_CHECK_JDBC_PASSWORD` secrets when JDBC-backed session and Flyway assertions should be enabled
- manual `Post-Deploy Smoke` dispatch also accepts `expected_build_version`, `expected_short_commit_id`, `expected_active_profile`, `expected_session_store_type`, and `expected_session_timeout` so promotion-stage checks can bind to the deployed root overview metadata instead of only checking liveness
- `Release` runs on stable tags in the form `vMAJOR.MINOR.PATCH` and prerelease tags in the form `vMAJOR.MINOR.PATCH-PRERELEASE`, rebuilds/scans/SBOM-documents the tagged image through Gradle, publishes security/static-analysis/SBOM artifact bundles from `build/reports/`, validates it with `./gradlew externalSmokeTest`, publishes it to GitHub Container Registry, and then creates a cumulative GitHub Release from `CHANGELOG.md` using `scripts/release/render-release-notes.ps1` plus the previous published GitHub Release boundary
- the `Release` workflow step summary records the semantic tag, immutable short-SHA tag, pushed digest reference, and the exact manual post-deploy check inputs maintainers should reuse before promotion
- recommended branch protection requires `CI`, at least one reviewer, and a squash-merge or equivalent linear-history policy

## IDE Setup

### IntelliJ IDEA

Recommended baseline:

1. Import the project as a Gradle project
2. Set the project SDK to Java 25
3. Set Gradle JVM to Java 25
4. If you want Spotless to delegate Java formatting to IntelliJ, export one of:

```powershell
$env:IDEA_FORMATTER_BINARY='<path-to-intellij>\bin\idea64.exe'
```

```powershell
$env:IDEA_HOME='<path-to-intellij>'
```

### VS Code

For local VS Code use:

- Extension Pack for Java
- Spring Boot Extension Pack
- Docker

For the containerized path, use the dev container instructions in `.devcontainer/README.md` or the short version in `.devcontainer/QUICK_START.md`.

## Database Modes

### Default Local Mode: PostgreSQL

Use the included `docker-compose.yml` to run PostgreSQL locally.

Start PostgreSQL:

```powershell
docker-compose up -d
```

Run the app with the default local profile:

```powershell
$env:JAVA_HOME='<path-to-jdk-25>'
$env:Path="$env:JAVA_HOME\bin;$env:Path"

.\gradlew.bat bootRun
```

Default PostgreSQL settings:

- Host: `localhost`
- Port: `5432`
- Database: `technical_interview_demo`
- User: `postgres`
- Password: `changeme`

Stop PostgreSQL when done:

```powershell
docker-compose down
```

## Running The Application

Core commands:

```powershell
docker-compose up -d
.\gradlew.bat bootRun
.\gradlew.bat build
```

Useful endpoints once the app is running:

- `GET /`
- `GET /hello`
- `GET /api/books`
- `GET /api/operator/surface` with an authenticated `ADMIN` session
- `GET /docs`
- `GET /v3/api-docs`
- `GET /v3/api-docs.yaml`
- `GET /actuator/info`
- `GET /actuator/health`
- `GET /actuator/health/liveness`
- `GET /actuator/health/readiness`
- `GET /actuator/prometheus` for trusted local inspection or deployment scraping, not as an internet-public endpoint

## Running Tests And Quality Checks

Set Java 25 in the same shell session first. Docker Desktop must also be running because the `test` task starts PostgreSQL through Testcontainers and `build` now also performs the Docker image build.

```powershell
.\gradlew.bat build
```

`build` now covers Spotless, PMD, SpotBugs plus FindSecBugs via `staticSecurityScan`, dependency/image vulnerability scanning, CycloneDX SBOM generation, tests, Asciidoctor generation, boot jar creation, and the Docker image build.
Use focused commands such as `test`, `asciidoctor`, or `dockerBuild` only when you intentionally want a narrower loop.

Security scan shortcuts:

- run `.\gradlew.bat staticSecurityScan` when you want the code-focused SpotBugs plus FindSecBugs gate directly
- run `.\gradlew.bat vulnerabilityScan` when you want only the dependency and container image Trivy gates
- run `.\gradlew.bat sbom` when you want only the CycloneDX SBOM outputs for the packaged app and image
- review suppressions in `config/security/trivy.ignore`, `config/security/spotbugs-security-include.xml`, and `config/security/spotbugs-security-exclude.xml`

OpenAPI contract workflow:

- review the live contract at `GET /v3/api-docs` or `GET /v3/api-docs.yaml`
- the approved baseline is stored at `src/test/resources/openapi/approved-openapi.json`
- normal `test` and `build` runs execute the compatibility gate and fail on breaking changes
- refresh the approved baseline intentionally with:

```powershell
.\gradlew.bat refreshOpenApiBaseline
```

Benchmark workflow:

- run `.\gradlew.bat gatlingBenchmark` when changing book list/search behavior, localization lookup behavior, or OAuth/session startup behavior

## Reproducing CI Locally

The `CI` workflow currently validates the repository with these commands and prerequisites:

```powershell
docker version
.\gradlew.bat build
helm lint helm/technical-interview-demo
helm template technical-interview-demo helm/technical-interview-demo -f helm/technical-interview-demo/values-local.yaml
.\gradlew.bat externalSmokeTest -PexternalSmokeImageName=technical-interview-demo -PdockerImageName=technical-interview-demo
```

The hosted `CI` workflow also uploads `build/reports/jacoco/test/jacocoTestReport.xml` to Codecov plus artifact bundles from `build/reports/security/`, `build/reports/pmd/`, `build/reports/security/static/`, and `build/reports/sbom/`. Local reproduction normally stops at generating those files; it does not publish them unless you intentionally run the same GitHub Action path in CI.

Deployment-manifest validation to pair with the CI flow:

```powershell
kubectl kustomize k8s/base
kubectl kustomize k8s/overlays/local
kubectl apply --dry-run=client -k k8s/overlays/local
kubectl kustomize k8s/monitoring
kubectl kustomize monitoring/grafana
kubectl apply --dry-run=client -k k8s/monitoring
kubectl apply --dry-run=client -k monitoring/grafana
```

Release-note rendering is implemented by `scripts/release/render-release-notes.ps1` and invoked from `.github/workflows/release.yml`; keep script and workflow updates aligned when release-note policy changes.

## Building Docker Images

Build with Gradle:

```powershell
.\gradlew.bat build
.\gradlew.bat dockerBuild
.\gradlew.bat dockerBuild -PdockerImageName=my-app:dev
```

`.\gradlew.bat build` now includes the Docker image build. If you only want the Gradle artifacts and checks, run `.\gradlew.bat build -x dockerBuild`.

Build directly with Docker:

```powershell
.\gradlew.bat bootJar
$jar = (Get-ChildItem build\libs\technical-interview-demo-*-boot.jar | Sort-Object LastWriteTimeUtc -Descending | Select-Object -First 1).Name
docker build --build-arg JAR_FILE="build/libs/$jar" -t technical-interview-demo .
docker run --rm -p 8080:8080 technical-interview-demo
```

The container uses the `prod` profile by default.

## Container Smoke Validation

The repository CI now performs a production-like container smoke test through the Gradle `externalSmokeTest` task after `./gradlew build`.

Local reproduction command sequence:

```powershell
.\gradlew.bat build
.\gradlew.bat externalSmokeTest -PexternalSmokeImageName=technical-interview-demo -PdockerImageName=technical-interview-demo
```

What the smoke validation proves:

- PostgreSQL starts for the container under test
- the app container starts with `SPRING_PROFILES_ACTIVE=prod`
- the app publishes port `8080`
- `GET /actuator/health/readiness` returns `HTTP 200` with `status=UP`
- Flyway migrations complete against PostgreSQL
- the packaged docs entry page is served at `GET /docs/index.html`
- the packaged OpenAPI endpoints respond at `GET /v3/api-docs` and `GET /v3/api-docs.yaml`
- the smoke harness can seed a JDBC-backed Spring Session record in PostgreSQL and the packaged app accepts that authenticated session for `GET /api/account`

## Scheduled Post-Deploy Smoke

Use the scheduled GitHub Actions `Post-Deploy Smoke` workflow when you want continuous checks against an already deployed environment instead of the locally provisioned Docker smoke path.

Workflow contract:

- required repository secret: `EXTERNAL_CHECK_BASE_URL`
- optional repository secrets for deeper checks: `EXTERNAL_CHECK_JDBC_URL`, `EXTERNAL_CHECK_JDBC_USER`, and `EXTERNAL_CHECK_JDBC_PASSWORD`
- if only `EXTERNAL_CHECK_BASE_URL` is configured, the scheduled run performs HTTP-only smoke assertions
- if the JDBC secrets are configured as a complete set, the scheduled run also verifies JDBC-backed authenticated-session behavior and confirms Flyway history is present
- manual dispatch can override the base URL and assert exact release identity with `expected_build_version` plus `expected_short_commit_id`
- manual dispatch can assert the documented prod posture with `expected_active_profile=prod`, `expected_session_store_type=jdbc`, and `expected_session_timeout=15m`; when those inputs are present the check also expects `configuration.security.csrfEnabled=false`
- the workflow uploads test reports from `build/reports/tests/externalDeploymentCheck/` and `build/test-results/externalDeploymentCheck/`

## Healthy Runtime Expectations

For this repository, a healthy runtime means the existing operational signals line up across health probes, metrics, audit writes, and authenticated session persistence.

Operational endpoint expectations:

- `GET /actuator/health`, `GET /actuator/health/liveness`, and `GET /actuator/health/readiness` should all return `HTTP 200` with `status=UP`
- treat `GET /actuator/health/readiness` as the authoritative rollout and smoke-check signal for container and cluster startup
- `GET /actuator/info` should return build and git metadata for the running build

Metrics expectations:

- `GET /actuator/prometheus` is available for trusted local inspection or deployment scraping
- the metrics payload should include both the default Spring/JVM meters and the application-specific `technical_interview_demo_*` meters
- the Grafana dashboard and Prometheus rules under `monitoring/` and `k8s/monitoring/` assume those metrics stay visible

Audit logging expectations:

- successful create, update, and delete operations for `Book` and `Localization` write an append-only row to `audit_logs`
- healthy audit rows include a target type, target id, action, actor login, summary, and creation timestamp
- if state-changing requests succeed but `audit_logs` stays empty, treat that as a runtime problem rather than a documentation gap

Session persistence expectations:

- authenticated browser sessions use Spring Session JDBC with the `technical-interview-demo-session` cookie
- the `prod` profile tightens the session contract to a 15 minute timeout, secure cookies, and one active session per login with login rejection on concurrent attempts
- healthy authenticated-session behavior means rows appear in `SPRING_SESSION` and `SPRING_SESSION_ATTRIBUTES` after a successful login flow
- if `/api/account` starts returning unauthorized responses for an otherwise valid login, inspect those tables before assuming an OAuth redirect problem

Quick verification sequence:

```powershell
Invoke-WebRequest http://127.0.0.1:8080/actuator/health/readiness
Invoke-WebRequest http://127.0.0.1:8080/actuator/info
Invoke-WebRequest http://127.0.0.1:8080/actuator/prometheus
```

Database checks after authenticated or state-changing exercise flows:

```sql
select count(*) from audit_logs;
select count(*) from spring_session;
select count(*) from spring_session_attributes;
```

## Upgrade And Rollback

This repository treats releases as versioned container-image upgrades backed by forward-only Flyway migrations.

Mixed-version rollout model:

- `expand`: additive schema work, typically `db-first`, intended to keep the previous app version working during rollout
- `contract`: cleanup or constraint-tightening work, typically `app-first` or `out-of-band`, after the new app version is fully deployed
- `backfill`: data movement or population work that often needs an `out-of-band` step even when the schema is additive
- `breaking`: incompatible schema or data changes that are not safe for rolling mixed-version rollout
- every new or modified migration SQL file must carry a JSON sidecar under `src/main/resources/db/migration/metadata/`
- `pwsh ./scripts/release/get-release-migration-impact.ps1` classifies the candidate as `none`, `rolling-compatible`, or `restore-sensitive` by diffing the current ref against the previous release tag and reading those sidecars

Pre-release checks for a versioned upgrade:

1. Review any new files under `src/main/resources/db/migration/` and confirm the schema change is intentional for the target version.
2. For each changed migration SQL file, confirm the matching metadata sidecar under `src/main/resources/db/migration/metadata/` accurately records `summary`, `rolloutCategory`, `deploymentOrder`, `rollingCompatible`, and `rollbackPosture`.
3. Run `pwsh ./scripts/release/get-release-migration-impact.ps1 -PreviousReleaseTag <previous-tag> -CurrentRef HEAD`.
4. Run `.\gradlew.bat build`.
5. Run `.\gradlew.bat externalSmokeTest -PexternalSmokeImageName=technical-interview-demo -PdockerImageName=technical-interview-demo`.
6. If the change touched book search/list behavior, localization lookup behavior, or OAuth/session startup behavior, also run `.\gradlew.bat gatlingBenchmark`.
7. For `restore-sensitive` releases, capture restore-drill evidence before promotion.
8. Confirm the target release notes and deployment values reference the intended semantic version tag.

Backup and retention expectations for versioned upgrades:

1. Take a database backup snapshot before each migration-bearing rollout.
2. Keep at least:
   - the latest 7 daily backups
   - the latest 4 weekly backups
   - one known-good pre-release snapshot for each migration-bearing rollout that is still in your rollback window
3. Verify backup restoration periodically; backup success without restore validation is not sufficient.

Upgrade flow:

1. Build or pull the target image tag, for example `ghcr.io/<owner>/technical-interview-demo:v1.0.0`.
2. Update the Kubernetes manifest image tag or Helm values to the target release.
3. Apply the rollout and watch `GET /actuator/health/readiness` or `kubectl rollout status` until the app reaches `UP`.
4. Run the manual `Post-Deploy Smoke` workflow against the deployed base URL with:
  - `expected_build_version=<semantic-tag-or-prerelease-tag>`
   - `expected_short_commit_id=<12-char-commit>`
   - `expected_active_profile=prod`
   - `expected_session_store_type=jdbc`
   - `expected_session_timeout=15m`
5. Confirm the deployed `GET /` overview reports the expected `build.version`, `git.shortCommitId`, `runtime.activeProfiles`, `configuration.session.storeType`, `configuration.session.timeout`, and `configuration.security.csrfEnabled=false`.
6. Confirm trusted Prometheus scraping still works and that authenticated browser-session flows can create rows in `SPRING_SESSION`.

Rollback expectations:

- If `get-release-migration-impact.ps1` reported `none`, image rollback to the previous known-good version is the normal first response.
- If the migration impact is `rolling-compatible`, image rollback remains the normal first response, but keep the release metadata and migration sidecars with the incident record.
- If the migration impact is `restore-sensitive`, do not assume image-only rollback is safe. The repo does not provide Flyway undo migrations.
- For `restore-sensitive` releases, rollback may require:
   - restoring the database from backup
   - manual database repair
   - or shipping a forward-fix release that restores application compatibility with the migrated schema
- After rollback or forward-fix recovery, re-check readiness, operational metadata, metrics scraping, and any affected authenticated session or write flows.

Restore drill (recommended each release cycle):

1. Restore a recent backup to a separate PostgreSQL instance.
2. Run:

```powershell
pwsh ./scripts/release/invoke-restore-drill.ps1 `
  -ImageReference ghcr.io/<owner>/technical-interview-demo:v1.0.0 `
  -BaseUrl http://127.0.0.1:18080 `
  -JdbcUrl jdbc:postgresql://localhost:5432/technical_interview_demo_restore `
  -JdbcUser postgres `
  -JdbcPassword changeme `
  -ExpectedBuildVersion v1.0.0 `
  -ExpectedShortCommitId <12-char-commit>
```

3. Verify the helper reaches readiness, proves `build.version` plus `git.shortCommitId`, confirms `prod` profile activation, JDBC session storage, the `15m` session timeout, `csrfEnabled=false`, Flyway history, and an authenticated `GET /api/account` request against the restored database.
4. Keep the console output or captured CI log as restore evidence for every `restore-sensitive` release.

## Kubernetes Deployment

The raw Kubernetes baseline lives under `k8s/base`. The local demo overlay lives under `k8s/overlays/local`.

Secret handling:

- `k8s/base/secret-example.yaml` is an example only and is not included in the base Kustomize package
- create a real `technical-interview-demo-secrets` secret before applying the manifests
- the required secret key is `DATABASE_PASSWORD`
- optional secret keys are `GITHUB_CLIENT_ID`, `GITHUB_CLIENT_SECRET`, `OIDC_CLIENT_ID`, `OIDC_CLIENT_SECRET`, `OIDC_ISSUER_URI`, and `ADMIN_LOGINS`

Base deployment defaults:

- `k8s/base/horizontalpodautoscaler.yaml` scales the app between 2 and 6 replicas on CPU and memory utilization
- `k8s/base/poddisruptionbudget.yaml` requires at least one pod to remain available during voluntary disruptions
- patch or replace those resources if your cluster does not provide the metrics APIs needed for HPA or if your rollout policy needs different budgets

Render the manifests:

```powershell
kubectl kustomize k8s/base
kubectl kustomize k8s/overlays/local
```

Prepare the local overlay:

1. Build the image with `.\gradlew.bat dockerBuild -PdockerImageName=technical-interview-demo:local`.
2. Load that image into your local cluster runtime if needed, for example `kind load docker-image technical-interview-demo:local`.
3. Make sure a PostgreSQL service named `postgres` exists in the `technical-interview-demo` namespace, or patch `DATABASE_HOST` in `k8s/overlays/local/patch-configmap.yaml`.
4. Create the deployment secret from `k8s/base/secret-example.yaml` or with `kubectl create secret generic`.

Apply the local overlay:

```powershell
kubectl apply -k k8s/overlays/local
```

Verify readiness after deployment:

```powershell
kubectl -n technical-interview-demo rollout status deployment/technical-interview-demo
kubectl -n technical-interview-demo get pods
kubectl -n technical-interview-demo port-forward service/technical-interview-demo 8080:80
Invoke-WebRequest http://127.0.0.1:8080/actuator/health/readiness
```

## Helm Deployment

Use the raw manifests when you want to review or patch explicit YAML checked into the repo. Use the Helm chart when you want release-style installs driven by values files.

Validate the chart locally:

```powershell
helm lint helm/technical-interview-demo
helm template technical-interview-demo helm/technical-interview-demo -f helm/technical-interview-demo/values-local.yaml
```

Install or upgrade the local chart:

```powershell
helm upgrade --install technical-interview-demo helm/technical-interview-demo --namespace technical-interview-demo --create-namespace -f helm/technical-interview-demo/values-local.yaml
```

The chart mirrors the raw manifest contract:

- image repository and tag are values-driven
- the deployment still expects an existing `technical-interview-demo-secrets` secret
- `values.yaml` enables autoscaling and a pod disruption budget by default for deployment-style installs
- `values-local.yaml` matches the local overlay assumptions: single replica, local image tag, `postgres` service host, non-secure session cookie for HTTP testing, and autoscaling disabled
- OAuth remains opt-in through the `oauth` profile and provider-specific secret keys (`GITHUB_CLIENT_*` and/or `OIDC_*`)
- ServiceMonitor rendering is optional and stays disabled until the monitoring stack is installed

## Monitoring And Alerting

The repository does not vendor the monitoring stack itself. Instead, it assumes the upstream `kube-prometheus-stack` Helm chart and adds repo-owned assets for the app scrape config, alert rules, Grafana dashboard, and Alertmanager examples.

Install the upstream monitoring stack for a local/demo cluster:

```powershell
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update
helm upgrade --install monitoring prometheus-community/kube-prometheus-stack --namespace monitoring --create-namespace -f monitoring/kube-prometheus-stack-values.yaml
```

Apply the application monitoring assets:

```powershell
kubectl apply -k k8s/monitoring
kubectl apply -k monitoring/grafana
```

What the monitoring assets provide:

- `k8s/monitoring/servicemonitor.yaml` scrapes `GET /actuator/prometheus` from inside the cluster as a trusted deployment concern, not an internet-public endpoint
- `k8s/monitoring/prometheus-rule.yaml` adds alerts for target-down, readiness failures, repeated restarts, elevated auth failures, session-backed account 5xxs, database pool saturation, database connection timeouts, Flyway-style startup crash loops, and elevated 5xx rates
- `monitoring/grafana/dashboards/technical-interview-demo.json` adds starter panels for JVM memory, CPU, request throughput/latency, cache events, domain totals, authentication failures, session-backed account health, database pool saturation, and Flyway migration visibility
- `monitoring/alertmanager/config-example.yaml` is a starting Alertmanager route/receiver config that you should adapt before using real notifications

Production logging and tracing posture:

- `application-prod.properties` keeps the root log level at `INFO`
- production profile logs are emitted as structured JSON Lines on stdout via `logging.structured.format.console=logstash`
- trace export stays runtime-configurable through standard OTLP environment variables instead of a repo-mandated vendor backend
- `k8s/log-forwarding/fluent-bit` is an optional forwarder example that recombines multiline Java stack traces before shipping logs to a centralized HTTP endpoint

Optional log-forwarder example apply command:

```powershell
kubectl apply -k k8s/log-forwarding/fluent-bit
```

Before applying to a real cluster, update `k8s/log-forwarding/fluent-bit/configmap.yaml` with your destination host, port, URI, and TLS policy.

Verify the monitoring setup:

1. Confirm the app target appears in Prometheus and reaches `UP`.
2. Port-forward the Grafana service and open the `Technical Interview Demo` dashboard.
3. Confirm the `technical_interview_demo_*` metrics and HTTP metrics are visible.
4. Review the loaded Prometheus rules and make sure Alertmanager shows the configured receivers.

## OAuth Setup

The application supports provider-aware OAuth login behind the optional `oauth` profile.

Use it when you want to exercise the protected write endpoints from a browser.

### Provider Configuration

The built-in provider model supports:

- `github` (OAuth app client credentials)
- `oidc` (issuer-driven OpenID Connect metadata + client credentials)

You can also define additional provider registration ids through `app.security.oauth.providers.<registrationId>.*`.
Expose those choices to the first-party UI through `GET /api/session`, which now returns `loginProviders[]` with `registrationId`, `clientName`, and `authorizationPath`.

### GitHub Example

Create a GitHub OAuth App with:

- Homepage URL: `http://localhost:8080`
- Authorization callback URL: `http://localhost:8080/api/session/login/oauth2/code/github`

Then export credentials and start the app:

```powershell
$env:GITHUB_CLIENT_ID='your-github-client-id'
$env:GITHUB_CLIENT_SECRET='your-github-client-secret'
$env:ADMIN_LOGINS='your-login'
$env:SPRING_PROFILES_ACTIVE='local,oauth'

docker-compose up -d
.\gradlew.bat bootRun
```

Start the login flow at:

- `http://localhost:8080/api/session/oauth2/authorization/github`

### OIDC Example

Provide OIDC issuer metadata plus credentials, then start the app:

```powershell
$env:OIDC_CLIENT_ID='your-oidc-client-id'
$env:OIDC_CLIENT_SECRET='your-oidc-client-secret'
$env:OIDC_ISSUER_URI='https://your-issuer.example.com/realms/demo'
$env:ADMIN_LOGINS='your-login'
$env:SPRING_PROFILES_ACTIVE='local,oauth'

docker-compose up -d
.\gradlew.bat bootRun
```

Start the login flow at:

- `http://localhost:8080/api/session/oauth2/authorization/oidc`

Protected requests use the authenticated session cookie, so you can replay state-changing requests from an HTTP client once you have signed in and captured `technical-interview-demo-session`.

For `1.0`, this reviewer-oriented session flow keeps CSRF disabled as a deliberate demo tradeoff.

Authenticated sessions are persisted in PostgreSQL through Spring Session JDBC, using tables `SPRING_SESSION` and `SPRING_SESSION_ATTRIBUTES`.

Role behavior:

- every authenticated provider login is persisted as an application user with the `USER` role
- logins listed in `ADMIN_LOGINS` also receive the `ADMIN` role
- category creation and localization-message management require `ADMIN`
- the current persisted user profile is available at `GET /api/account`
- preferred-language updates are available at `PUT /api/account/language`

## Troubleshooting

Use these three PostgreSQL troubleshooting paths deliberately:

- local profile startup: `docker-compose` plus `.\gradlew.bat bootRun`
- production-like container smoke: `.\gradlew.bat externalSmokeTest`
- cluster deployment: Kubernetes manifests or Helm plus the target PostgreSQL service/secret wiring

### Gradle fails because Java 11 is active

Symptom:

- build errors mention an unsupported Java version

Fix:

```powershell
$env:JAVA_HOME='<path-to-jdk-25>'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
java -version
```

### `GET /docs` is missing or stale

Symptom:

- the docs endpoint redirects to missing content
- generated snippets are out of date

Fix:

```powershell
.\gradlew.bat asciidoctor
```

### OpenAPI baseline needs intentional refresh

Symptom:

- OpenAPI compatibility tests fail after a reviewed API change

Fix:

```powershell
.\gradlew.bat refreshOpenApiBaseline
.\gradlew.bat test --tests team.jit.technicalinterviewdemo.technical.docs.OpenApiCompatibilityIntegrationTests
```

### PostgreSQL local profile will not start

Symptom:

- connection refused errors on startup

Fix:

1. Confirm Docker Desktop is running.
2. Run `docker-compose up -d`.
3. Verify the database is healthy with `docker ps`.
4. Re-run the app with `.\gradlew.bat bootRun`.

### Tests fail because Testcontainers cannot start PostgreSQL

Symptom:

- `.\gradlew.bat test` fails before the Spring context loads
- the output mentions Docker, Testcontainers, or PostgreSQL container startup

Fix:

1. Confirm Docker Desktop is running.
2. Verify Docker is reachable with `docker ps`.
3. Re-run `.\gradlew.bat --no-problems-report test`.
4. If Docker is managed by corporate policy, make sure Linux containers are enabled and the current user can access Docker.

### OAuth login does not start

Symptom:

- `/api/session/oauth2/authorization/{registrationId}` returns an error or redirect loop

Fix:

1. Confirm `SPRING_PROFILES_ACTIVE` includes `oauth`.
2. Confirm the selected provider credentials are exported in the same shell or run configuration (`GITHUB_CLIENT_*` for `github`, `OIDC_*` for `oidc`).
3. Confirm the provider callback URL matches `http://localhost:8080/api/session/login/oauth2/code/{registrationId}` for your registration id.
4. Use `GET /api/session` to confirm the runtime exposes the expected `loginProviders[].authorizationPath`.
5. Complete the browser login and then verify the app session with `GET /api/account` plus the `technical-interview-demo-session` cookie.
6. Re-run the app with `.\gradlew.bat bootRun`.

### Authenticated session does not persist after login

Symptom:

- OAuth provider login appears to complete, but `GET /api/account` still returns unauthorized
- the app does not retain the `technical-interview-demo-session` state across requests

Fix:

1. Confirm the login flow completed with the optional `oauth` profile enabled.
2. Query `SPRING_SESSION` and `SPRING_SESSION_ATTRIBUTES` and confirm rows are being created.
3. If the tables are missing, confirm Flyway ran successfully and that `V4__create_spring_session_tables.sql` is present in the target build.
4. If the tables exist but stay empty, confirm the app can still reach PostgreSQL with the configured datasource settings.
5. Repeat the login flow and verify `GET /api/account` with the captured `technical-interview-demo-session` cookie from `src/test/resources/http/authentication.http`.

### Container smoke validation fails on database startup

Symptom:

- `.\gradlew.bat externalSmokeTest` fails before readiness succeeds
- the logs mention PostgreSQL startup or authentication

Fix:

1. Confirm Docker Desktop is running and `docker version` succeeds.
2. Confirm the `technical-interview-demo` image exists with `docker image ls technical-interview-demo`.
3. Rebuild the image with `.\gradlew.bat dockerBuild`.
4. Re-run `.\gradlew.bat externalSmokeTest -PexternalSmokeImageName=technical-interview-demo -PdockerImageName=technical-interview-demo`.

### Kubernetes deployment never becomes ready

Symptom:

- `kubectl rollout status deployment/technical-interview-demo` times out
- pod logs show PostgreSQL connection failures or missing secret keys

Fix:

1. Confirm the `technical-interview-demo-secrets` secret exists in the target namespace.
2. Confirm `DATABASE_HOST`, `DATABASE_PORT`, `DATABASE_NAME`, `DATABASE_USER`, and `DATABASE_PASSWORD` match the reachable PostgreSQL service.
3. Confirm the image tag in the manifest or Helm values exists in the cluster runtime or remote registry.
4. Describe the pod with `kubectl -n technical-interview-demo describe pod <pod-name>` and check the readiness probe events.

### Prometheus does not scrape app metrics

Symptom:

- the target is missing or down in Prometheus
- the Grafana dashboard loads but panels stay empty

Fix:

1. Confirm the monitoring stack was installed with `monitoring/kube-prometheus-stack-values.yaml`.
2. Confirm `kubectl apply -k k8s/monitoring` and `kubectl apply -k monitoring/grafana` both succeeded.
3. Confirm the `ServiceMonitor` is in the `monitoring` namespace and the app `Service` is in `technical-interview-demo`.
4. Port-forward the app service and verify `GET /actuator/prometheus` manually.
5. Confirm the monitoring stack is allowed to watch `technical-interview-demo` and `monitoring` namespaces.

### Spotless skips Java formatting

Symptom:

- format checks pass, but Java files are not reformatted

Fix:

Set one of these variables before running `spotlessApply`:

- `IDEA_FORMATTER_BINARY`
- `IDEA_HOME`

### Port 8080 or 5432 is already in use

Symptom:

- the app or PostgreSQL container fails to bind

Fix:

1. Stop the conflicting process or container.
2. Re-run the command.
3. If needed, override the port locally in your run configuration.
