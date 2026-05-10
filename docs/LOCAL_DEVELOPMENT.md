# Local Development Guide

This guide owns local development commands after the environment is set up.
Use [SETUP.md](../SETUP.md) for Java, Docker, `.env`, IDE, dev-container, and local PostgreSQL environment setup.
Use [OPERATIONS.md](OPERATIONS.md) for deployment and runtime operations runbooks.

## Table Of Contents

- [Command Wrapper](#command-wrapper)
- [Running The Application](#running-the-application)
- [Running Tests And Quality Checks](#running-tests-and-quality-checks)
- [Reproducing CI Locally](#reproducing-ci-locally)
- [Documentation Health](#documentation-health)
- [Security, Contract, And Benchmark Workflows](#security-contract-and-benchmark-workflows)
- [Local Troubleshooting](#local-troubleshooting)

## Command Wrapper

Use the wrapper from the repository root for Gradle-backed local development:

```powershell
./build.ps1 build
```

The wrapper:

- loads a root `.env` file before invoking Gradle
- passes Gradle arguments through to the Gradle wrapper
- classifies the current uncommitted change set for `./build.ps1 build`
- exits successfully with manual-review guidance when the uncommitted change set is lightweight-only
- supports `-FullBuild` when you need to force the full Gradle build
- supports `-SkipTests` and `-SkipChecks` for local loops only

Use `./build.ps1 -FullBuild build` when you want to force the full Gradle build even for lightweight-only changes.

Inside the VS Code dev container, the Gradle wrapper is also available directly:

```bash
./gradlew build
```

Prefer `./build.ps1` when PowerShell is available because it mirrors the repository wrapper behavior.

## Running The Application

For the local shell path, make sure PostgreSQL is running as described in [SETUP.md](../SETUP.md#local-postgresql).

Run the app:

```powershell
./build.ps1 bootRun
```

Useful local endpoints after startup:

- `http://localhost:8080/`
- `http://localhost:8080/hello`
- `http://localhost:8080/api/books`
- `http://localhost:8080/docs`
- `http://localhost:8080/actuator/health`

Useful endpoint paths:

- `GET /`
- `GET /hello`
- `GET /api/books`
- `GET /api/admin/operator-surface` with an authenticated `ADMIN` session
- `GET /api/admin/users` with an authenticated `ADMIN` session
- `GET /docs`
- `GET /v3/api-docs`
- `GET /v3/api-docs.yaml`
- `GET /actuator/info`
- `GET /actuator/health`
- `GET /actuator/health/liveness`
- `GET /actuator/health/readiness`
- `GET /actuator/prometheus` for trusted local inspection or deployment scraping, not as an internet-public endpoint

## Running Tests And Quality Checks

Docker Desktop must be running for integration tests because the `test` task starts PostgreSQL through Testcontainers.
A full `build` also performs the Docker image build.

Quick implementation-loop checks:

```powershell
./build.ps1 compileJava
./build.ps1 -SkipTests -SkipChecks build
```

Final verification default:

```powershell
./build.ps1 build
```

`./build.ps1 build` first checks the current uncommitted files and exits successfully with manual-review guidance when the change set is lightweight-only.
Use `./build.ps1 -FullBuild build` to force the full Gradle build.
Use `-SkipTests` and `-SkipChecks` only for local loops, not signoff.

`-SkipChecks` skips formatting, PMD, SpotBugs, Error Prone, coverage verification, the build-wired dependency vulnerability scan, explicit vulnerability scan tasks, and SBOM checks.

A full `build` covers Palantir/Spotless formatting, PMD, SpotBugs plus FindSecBugs via `staticSecurityScan`, dependency vulnerability scanning, CycloneDX SBOM generation, tests, Asciidoctor generation, boot jar creation, and the Docker image build.

Focused commands are useful during local loops:

```powershell
./build.ps1 test
./build.ps1 asciidoctor
./build.ps1 dockerBuild
./build.ps1 checkFormat
./build.ps1 format
```

## Reproducing CI Locally

The `CI` workflow currently validates the repository with these commands and prerequisites:

```powershell
docker version
pwsh ./scripts/docs/audit-docs.ps1
./build.ps1 -FullBuild build imageVulnerabilityScan
helm lint infra/helm/technical-interview-demo
helm template technical-interview-demo infra/helm/technical-interview-demo -f infra/helm/technical-interview-demo/values-local.yaml
./build.ps1 externalSmokeTest -PexternalSmokeImageName=technical-interview-demo -PdockerImageName=technical-interview-demo
```

Exception:

- `./build.ps1 build` performs the local uncommitted-change classifier check and exits successfully with manual-review guidance for lightweight-only changes; the remaining heavyweight CI commands are not needed for that local shortcut
- in hosted CI, the same classifier runs against the push or pull-request range and short-circuits the heavy validation path when it reports `skipHeavyValidation=true`

The hosted `CI` workflow also uploads `build/reports/jacoco/test/jacocoTestReport.xml` to Codecov plus artifact bundles from `build/reports/security/`, `build/reports/pmd/`, `build/reports/security/static/`, and `build/reports/sbom/`.
Local reproduction normally stops at generating those files.

## Documentation Health

Run the documentation health workflow after changing user-facing Markdown or AsciiDoc, especially top-level docs, `docs/`, REST Docs sources, manual-regression README files, edge reference docs, or `.devcontainer` docs:

```powershell
pwsh ./scripts/docs/audit-docs.ps1
```

The check audits local documentation links, stale generated-document signals, stable-version agreement, frontend-contract OpenAPI summary counts, and supported-language summaries.
CI runs the same check before either the lightweight-only shortcut or the heavy Gradle validation path.

For docs or support-file-only workflows, the `./build.ps1 build` shortcut handles the local classifier check.
Run `pwsh ./scripts/classify-changed-files.ps1` directly only for a different diff boundary.

## Security, Contract, And Benchmark Workflows

Security scan shortcuts:

```powershell
./build.ps1 staticSecurityScan
./build.ps1 vulnerabilityScan
./build.ps1 imageVulnerabilityScan
./build.ps1 sbom
```

Use:

- `staticSecurityScan` for the code-focused SpotBugs plus FindSecBugs gate
- `vulnerabilityScan` for the dependency and container image Trivy gates
- `imageVulnerabilityScan` when only container image scan evidence is required
- `sbom` for CycloneDX SBOM outputs for the packaged app and image

Review suppressions in `tooling/security/trivy.ignore`, `tooling/security/spotbugs-security-include.xml`, and `tooling/security/spotbugs-security-exclude.xml`.

OpenAPI contract workflow:

- review the live contract at `GET /v3/api-docs` or `GET /v3/api-docs.yaml`
- the approved baseline is stored at `src/test/resources/openapi/approved-openapi.json`
- normal `test` and `build` runs execute the compatibility gate and fail on breaking changes
- refresh the approved baseline intentionally with:

```powershell
./build.ps1 refreshOpenApiBaseline
```

Benchmark workflow:

- run `./build.ps1 gatlingBenchmark` when changing book list/search behavior, localization lookup behavior, or OAuth/session startup behavior
- when both `build` and `gatlingBenchmark` are required, prefer one invocation such as `./build.ps1 build gatlingBenchmark --no-daemon` so Gradle reuses the same task graph instead of repeating the full build in separate runs
- do not run `build`, `gatlingBenchmark`, `externalSmokeTest`, `externalDeploymentCheck`, `scheduledExternalCheck`, or other overlapping Gradle validation tasks in parallel because they share build outputs and some already depend on packaging or image-build tasks

## Local Troubleshooting

Use this section for local command and development failures.
Use [SETUP.md](../SETUP.md) for environment setup and [OPERATIONS.md](OPERATIONS.md) for container smoke, deployed OAuth, Kubernetes, Helm, monitoring, rollback, and deployment troubleshooting.

### Gradle fails because Java 11 is active

Symptom:

- build errors mention an unsupported Java version

Fix:

```powershell
$env:JAVA_HOME = '<path-to-jdk-25>'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
java -version
```

Persist the Java 25 path in `.env` as described in [SETUP.md](../SETUP.md#environment-variables).

### `GET /docs` is missing or stale

Symptom:

- the docs endpoint redirects to missing content
- generated snippets are out of date

Fix:

```powershell
./build.ps1 asciidoctor
```

### OpenAPI baseline needs intentional refresh

Symptom:

- OpenAPI compatibility tests fail after a reviewed API change

Fix:

```powershell
./build.ps1 refreshOpenApiBaseline
./build.ps1 test --tests team.jit.technicalinterviewdemo.technical.docs.OpenApiCompatibilityIntegrationTests
```

### PostgreSQL local profile will not start

Symptom:

- connection refused errors on startup

Fix:

1. Confirm Docker Desktop is running.
2. Run `docker-compose up -d`.
3. Verify the database is healthy with `docker ps`.
4. Re-run the app with `./build.ps1 bootRun`.

### Tests fail because Testcontainers cannot start PostgreSQL

Symptom:

- `./build.ps1 test` fails before the Spring context loads
- the output mentions Docker, Testcontainers, or PostgreSQL container startup

Fix:

1. Confirm Docker Desktop is running.
2. Verify Docker is reachable with `docker ps`.
3. Re-run `./build.ps1 --no-problems-report test`.
4. If Docker is managed by corporate policy, make sure Linux containers are enabled and the current user can access Docker.

### Java formatting differs from IDE formatting

Symptom:

- IntelliJ reformats Java files differently from `format` or `checkFormat`

Fix:

1. Run `./build.ps1 format` from the repository root.
2. Keep the Palantir Java Format result as the authoritative Gradle-owned Java format.
3. Check that IntelliJ is using the Palantir Java Format plugin and the committed project code-style settings.

### Port 8080 or 5432 is already in use

Symptom:

- the app or PostgreSQL container fails to bind

Fix:

1. Stop the conflicting process or container.
2. Re-run the command.
3. If needed, override the port locally in your run configuration.
