# Environment Setup Guide

This guide sets up the local environment for `technical-interview-demo`.
Use one path:

- VS Code dev container
- local shell without a dev container

After the environment is ready, use [docs/LOCAL_DEVELOPMENT.md](docs/LOCAL_DEVELOPMENT.md) for running the app, tests, CI reproduction, and local troubleshooting.

## Table Of Contents

- [Choose A Setup Path](#choose-a-setup-path)
- [Dev Container Setup](#dev-container-setup)
- [Local Shell Setup](#local-shell-setup)
- [Environment Variables](#environment-variables)
- [Local PostgreSQL](#local-postgresql)
- [IDE Setup](#ide-setup)
- [Optional Git Commit Template](#optional-git-commit-template)
- [Next Step](#next-step)

## Choose A Setup Path

Use the dev container when you want VS Code to provision the Java toolchain and containerized development environment for you.
Use the local shell path when you want to run the repository directly from your host machine.

Both paths use the same source tree and Gradle project.

## Dev Container Setup

Prerequisites:

- Git
- VS Code
- Docker Desktop, running before the container starts
- VS Code Dev Containers extension

Install the extension if needed:

```powershell
code --install-extension ms-vscode-remote.remote-containers
```

Open the repository in the container:

1. Open this repository folder in VS Code.
2. Open the command palette.
3. Run `Dev Containers: Reopen in Container`.
4. Wait for the first build to finish.
5. Open a terminal inside the container.

Verify the environment:

```bash
java -version
./gradlew --version
docker ps
```

The full dev-container reference lives in [.devcontainer/README.md](.devcontainer/README.md).
The short command card lives in [.devcontainer/QUICK_START.md](.devcontainer/QUICK_START.md).

### Optional Dev-Container Services

The current dev container uses a standalone image.
Start optional PostgreSQL and Prometheus services from the repository root when you need them inside the dev-container Docker daemon:

```bash
docker compose -f .devcontainer/docker-compose.yml up -d
```

Default optional PostgreSQL settings in the dev-container compose file:

- Host: `localhost`
- Port: `5432`
- Database: `technical_interview_demo`
- User: `demo_user`
- Password: `demo_password`

## Local Shell Setup

Prerequisites:

- Java 25
- Git
- PowerShell 7+ (`pwsh`) for repository wrapper commands
- Docker Desktop when you need local PostgreSQL, Testcontainers, Docker image builds, or the full build lifecycle
- IntelliJ IDEA or VS Code if you want IDE support

Check the local toolchain from the repository root:

```powershell
pwsh ./scripts/check-local-tools.ps1
```

Use `-RequiredOnly` for the fastest core check.
Use `-Strict` when conditional and recommended tool gaps should fail the command.

The helper loads the root `.env` before Java-sensitive probes, reports required, conditional, recommended, and diagnostic tools, and exits non-zero only when required checks fail.

Useful local tools by workflow:

- search and navigation: `rg` and `git`
- shell and wrapper commands: PowerShell 7+ (`pwsh`) and `./build.ps1`
- build and validation: Java 25, the Gradle wrapper, and Docker Desktop
- GitHub diagnostics: `gh` for GitHub Actions, code-scanning, Dependabot, attestation, or release-artifact checks
- API and contract checks: OpenAPI and REST Docs tasks through `./build.ps1`; `ijhttp` only when manual HTTP regression suites are requested
- deployment and release checks: Helm, `kubectl`, Cosign, and Trivy-related wrapper tasks only when deployment, image verification, or release work is in scope

When a relevant tool is missing, record the missing command or dependency, the fallback used, and any remaining risk.

## Environment Variables

`.env.example` contains the supported shell variables for local work.
The repository wrapper script `build.ps1` auto-loads a root `.env` file before invoking Gradle.
Direct `gradlew` commands, IDE run configurations, Docker Compose commands, and non-Gradle shell commands still need variables exported in the usual way.

1. Copy `.env.example` to `.env` if you want a private local reference file.
2. Fill in actual paths and secrets for your machine.
3. Set `JAVA_HOME` to a Java 25 JDK path when Java 25 is not already first on `PATH`.
4. Use `./build.ps1` for Gradle commands, or export the same values in your shell, IDE run configuration, or Docker Compose environment when bypassing the wrapper.

Variables you are most likely to need:

- `JAVA_HOME` for Gradle and the toolchain
- `SPRING_PROFILES_ACTIVE` if you want to override the default `local` profile
- `DATABASE_*` variables when overriding the default PostgreSQL connection
- `GITHUB_CLIENT_ID` and `GITHUB_CLIENT_SECRET` for the built-in GitHub provider when enabling the optional `oauth` profile
- `OIDC_CLIENT_ID`, `OIDC_CLIENT_SECRET`, and `OIDC_ISSUER_URI` for the built-in issuer-driven OIDC provider when enabling `oauth`
- `APP_BOOTSTRAP_INITIAL_ADMIN_IDENTITIES` when you want to bootstrap the first persisted `ADMIN` role from one or more `provider:externalLogin` identities
- `APP_BOOTSTRAP_SEED_DEMO_DATA` when you want to override demo-data seeding for categories, books, and localization messages
- `SESSION_COOKIE_SECURE` when you want to override the `prod` profile session-cookie default of `true` for local HTTP testing or a specific deployment environment

Path notes:

- The wrapper uses `scripts/load-dotenv.ps1` internally and works with both normal and escaped Windows paths.
- For Windows-style paths, the helper accepts normal forms such as `C:\Users\kamki\.jdks\azul-25.0.3` and escaped forms such as `C:\\Users\\kamki\\.jdks\\azul-25.0.3`.
- Placeholder values such as `<path-to-jdk-25>` in `.env.example` should be replaced with paths from your own machine.

## Local PostgreSQL

The default `local` profile expects PostgreSQL on `localhost:5432`.
The included root [docker-compose.yml](docker-compose.yml) starts that database for the local shell path.

Start PostgreSQL:

```powershell
docker-compose up -d
```

Default local PostgreSQL settings:

- Host: `localhost`
- Port: `5432`
- Database: `technical_interview_demo`
- User: `postgres`
- Password: `changeme`

Stop PostgreSQL when done:

```powershell
docker-compose down
```

## IDE Setup

### IntelliJ IDEA

Recommended baseline:

1. Import the project as a Gradle project.
2. Set the project SDK to Java 25.
3. Set Gradle JVM to Java 25.
4. Keep IntelliJ project code-style settings enabled.
5. Keep Flyway migration SQL under `src/main/resources/db/migration/` hand-formatted; those scripts are excluded from IntelliJ reformatting because SQL formatter churn makes migration review harder.

### VS Code

For local VS Code without the dev container, install:

- Extension Pack for Java
- Spring Boot Extension Pack
- Docker

For the containerized path, use [Dev Container Setup](#dev-container-setup).

## Optional Git Commit Template

The repository includes `.gitmessage` with a Conventional Commits style subject, optional body, required project metadata footers for AI-created commits, and repo-supported type guidance.
Enable it for this repository if you want Git to prefill commit messages:

```powershell
git config commit.template .gitmessage
```

## Next Step

Use [docs/LOCAL_DEVELOPMENT.md](docs/LOCAL_DEVELOPMENT.md) after setup for:

- running the application
- running tests and quality checks
- reproducing CI locally
- local documentation, contract, security, and benchmark workflows
- local development troubleshooting
