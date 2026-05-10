# Developer Setup Guide

This guide is the fastest path to a working local environment for `technical-interview-demo`.

## Table Of Contents

- [Choose A Workflow](#choose-a-workflow)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Environment Variables](#environment-variables)
- [Optional Git Commit Template](#optional-git-commit-template)
- [Operations And Deployment Runbooks](#operations-and-deployment-runbooks)
- [IDE Setup](#ide-setup)
- [Database Modes](#database-modes)
- [Running The Application](#running-the-application)
- [Running Tests And Quality Checks](#running-tests-and-quality-checks)
- [Reproducing CI Locally](#reproducing-ci-locally)
- [Troubleshooting](#troubleshooting)

## Choose A Workflow

Use one of these paths:

- **Local shell + JDK 25** for the shortest feedback loop
- **VS Code dev container** if you want a prebuilt toolchain with Docker, PostgreSQL, and Prometheus

Both paths end up using the same Gradle wrapper and the same application code.

## Prerequisites

Install the tools that match your workflow:

- Java 25
- Git
- PowerShell 7+ (`pwsh`) for the repo wrapper commands
- Docker Desktop if you want PostgreSQL, container builds, the VS Code dev container, or to run the integration test and build lifecycles
- IntelliJ IDEA or VS Code if you want IDE support

### AI Workflow Helper Tools

AI agents should use the fastest relevant local tool and report expected or useful tools that are unavailable when that affects workflow speed, validation, or fallback quality.
Agents should not probe every optional tool up front; they should check tools when the current task needs them.

Useful tools by workflow:

- search and navigation: `rg` and `git`
- shell and wrapper commands: PowerShell 7+ (`pwsh`) and `./build.ps1`
- build and validation: Java 25, the Gradle wrapper, and Docker Desktop for Testcontainers, image builds, and full build lifecycles
- skill and AI-guidance checks: `./scripts/ai/validate-skills.ps1`; Python with PyYAML only when using external YAML-based skill validators
- GitHub diagnostics: `gh` for GitHub Actions, code-scanning, Dependabot, attestation, or release-artifact checks
- API and contract checks: OpenAPI and REST Docs tasks through `./build.ps1`; `ijhttp` only when manual HTTP regression suites are requested
- deployment and release checks: Helm, `kubectl`, Cosign, and Trivy-related wrapper tasks only when deployment, image verification, or release work is in scope

Quickly check the local toolchain from the repository root:

```powershell
pwsh ./scripts/check-local-tools.ps1
```

The helper loads the root `.env` before Java-sensitive probes, reports required, conditional, recommended, and diagnostic tools, and exits non-zero only when required checks fail. Use `-RequiredOnly` for the fastest core check or `-Strict` when conditional and recommended tool gaps should fail the command.

When a relevant tool is missing, the AI should name the missing command or dependency, name the fallback it used, and state any remaining risk.
Examples: "`rg` is unavailable, using PowerShell search instead" or "Python with PyYAML is unavailable, so the dependency-free PowerShell skill validator ran instead."

## Quick Start

### PowerShell

```powershell
# If Java 25 is not already on PATH, copy .env.example to .env and set JAVA_HOME once.

docker-compose up -d
./build.ps1 bootRun
```

The default `local` profile expects PostgreSQL on `localhost:5432`. The included `docker-compose.yml` starts that database for you. After startup, open:

- `http://localhost:8080/`
- `http://localhost:8080/hello`
- `http://localhost:8080/api/books`
- `http://localhost:8080/docs`
- `http://localhost:8080/actuator/health`

## Environment Variables

`.env.example` contains the supported shell variables for local work.
The repository wrapper script `build.ps1` auto-loads a root `.env` file before invoking Gradle.
The wrapper also classifies the current uncommitted change set for `./build.ps1 build` and skips Gradle when only lightweight files changed.
Direct `gradlew` commands, IDE run configurations, and non-Gradle shell commands still need the variables exported in the usual way.

1. Copy `.env.example` to `.env` if you want a private local reference file.
2. Fill in your actual paths, especially `JAVA_HOME`.
3. Use `./build.ps1` for Gradle commands, or export the values in your shell, IDE run configuration, or Docker Compose environment when you bypass the wrapper.

**Easiest Gradle path for PowerShell:**

Run the wrapper from the repository root:

```powershell
./build.ps1 build
```

Use `./build.ps1 -FullBuild build` when you want to force the full Gradle build even for lightweight-only changes.

**Details:**

- The wrapper uses `scripts/load-dotenv.ps1` internally and works with both normal and escaped Windows paths.
- For Windows-style paths, the helper accepts both normal forms such as `C:\Users\kamki\.jdks\azul-25.0.3` and escaped forms such as `C:\\Users\\kamki\\.jdks\\azul-25.0.3`.
- Placeholder values such as `<path-to-jdk-25>` in `.env.example` should be replaced with paths from your own machine.

Variables you are most likely to need:

- `JAVA_HOME` for Gradle and the toolchain
- `SPRING_PROFILES_ACTIVE` if you want to override the default `local` profile
- `DATABASE_*` variables when overriding the default PostgreSQL connection
- `GITHUB_CLIENT_ID` and `GITHUB_CLIENT_SECRET` for the built-in GitHub provider when enabling the optional `oauth` profile
- `OIDC_CLIENT_ID`, `OIDC_CLIENT_SECRET`, and `OIDC_ISSUER_URI` for the built-in issuer-driven OIDC provider when enabling `oauth`
- `APP_BOOTSTRAP_INITIAL_ADMIN_IDENTITIES` when you want to bootstrap the first persisted `ADMIN` role from one or more `provider:externalLogin` identities
- `APP_BOOTSTRAP_SEED_DEMO_DATA` when you want to override demo-data seeding for categories, books, and localization messages
- `SESSION_COOKIE_SECURE` when you want to override the `prod` profile session-cookie default of `true` for local HTTP testing or a specific deployment environment

## Optional Git Commit Template

The repository includes `.gitmessage` with a Conventional Commits style subject, optional body, required project metadata footers for AI-created commits, and repo-supported type guidance.
Enable it for this repository if you want Git to prefill commit messages:

```powershell
git config commit.template .gitmessage
```

## Operations And Deployment Runbooks

This guide focuses on local setup and local troubleshooting.
Use [docs/OPERATIONS.md](docs/OPERATIONS.md) for deployment contract, runtime environment variables, Docker image operation, container smoke, post-deploy smoke, healthy runtime expectations, upgrade and rollback, Kubernetes, Helm, monitoring, OAuth runtime setup, and deployment troubleshooting.

Use [docs/FRONTEND_AI_CONTRACT.md](docs/FRONTEND_AI_CONTRACT.md) when wiring or reviewing a separate first-party UI against the backend contract.

## IDE Setup

### IntelliJ IDEA

Recommended baseline:

1. Import the project as a Gradle project
2. Set the project SDK to Java 25
3. Set Gradle JVM to Java 25
4. Use `./build.ps1 format` when you want to normalize repository formatting from the same formatter configuration CI uses.
5. Keep IntelliJ project code-style settings enabled; AsciiDoc sources stay formatter-managed, so nested lists must use explicit AsciiDoc marker depth such as `*` for parents and `**` for children.
6. Keep Flyway migration SQL under `src/main/resources/db/migration/` hand-formatted; those scripts are excluded from IntelliJ reformatting because SQL formatter churn makes migration review harder.

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
./build.ps1 bootRun
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
./build.ps1 bootRun
./build.ps1 build
```

Useful endpoints once the app is running:

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

Use the wrapper command from the repository root. Docker Desktop must also be running because the `test` task starts PostgreSQL through Testcontainers and a full `build` now also performs the Docker image build.

Quick implementation-loop checks:

```powershell
./build.ps1 compileJava
./build.ps1 -SkipTests -SkipChecks build
```

Use the full build task for final verification:

```powershell
./build.ps1 build
```

`./build.ps1 build` first checks the current uncommitted files and exits successfully with manual-review guidance when the change set is lightweight-only. Use `./build.ps1 -FullBuild build` to force the full Gradle build. Use `-SkipTests` and `-SkipChecks` only for local loops, not signoff.
`-SkipChecks` skips formatting, PMD, SpotBugs, Error Prone, coverage verification, the build-wired dependency vulnerability scan, explicit vulnerability scan tasks, and SBOM checks.

A full `build` covers Palantir/Spotless formatting, PMD, SpotBugs plus FindSecBugs via `staticSecurityScan`, dependency vulnerability scanning, CycloneDX SBOM generation, tests, Asciidoctor generation, boot jar creation, and the Docker image build.
The container image vulnerability scan is intentionally explicit instead of part of the `build` target; run `./build.ps1 imageVulnerabilityScan` or `./build.ps1 vulnerabilityScan` when image-scan evidence is required.
Use focused commands such as `test`, `asciidoctor`, or `dockerBuild` only when you intentionally want a narrower loop.

Documentation health workflow:

- run `pwsh ./scripts/docs/audit-docs.ps1` after changing user-facing Markdown or AsciiDoc, especially top-level docs, `docs/`, REST Docs sources, manual-regression README files, edge reference docs, or `.devcontainer` docs
- the check audits local documentation links, stale generated-document signals, stable-version agreement, frontend-contract OpenAPI summary counts, and supported-language summaries
- CI runs the same check before either the lightweight-only shortcut or the heavy Gradle validation path

Security scan shortcuts:

- run `./build.ps1 staticSecurityScan` when you want the code-focused SpotBugs plus FindSecBugs gate directly
- run `./build.ps1 vulnerabilityScan` when you want only the dependency and container image Trivy gates
- run `./build.ps1 sbom` when you want only the CycloneDX SBOM outputs for the packaged app and image
- review suppressions in `tooling/security/trivy.ignore`, `tooling/security/spotbugs-security-include.xml`, and `tooling/security/spotbugs-security-exclude.xml`

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
- for a docs or support-file-only workflow, the `./build.ps1 build` shortcut handles the local classifier check; run `pwsh ./scripts/classify-changed-files.ps1` directly only for a different diff boundary

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

The hosted `CI` workflow also uploads `build/reports/jacoco/test/jacocoTestReport.xml` to Codecov plus artifact bundles from `build/reports/security/`, `build/reports/pmd/`, `build/reports/security/static/`, and `build/reports/sbom/`. Local reproduction normally stops at generating those files; it does not publish them unless you intentionally run the same GitHub Action path in CI.

## Troubleshooting

Use this section for local setup and local development failures.
Use [docs/OPERATIONS.md](docs/OPERATIONS.md) for container smoke, deployed OAuth, Kubernetes, Helm, monitoring, rollback, and deployment troubleshooting.

For local PostgreSQL startup, use `docker-compose` plus `./build.ps1 bootRun`.

### Gradle fails because Java 11 is active

Symptom:

- build errors mention an unsupported Java version

Fix:

```powershell
$env:JAVA_HOME = '<path-to-jdk-25>'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
java -version
```

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
