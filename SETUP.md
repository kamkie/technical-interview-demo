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
- Docker Desktop if you want PostgreSQL, container builds, or the VS Code dev container
- IntelliJ IDEA or VS Code if you want IDE support

## Quick Start

### PowerShell

```powershell
$env:JAVA_HOME='C:\Users\kamki\.jdks\azul-25.0.3'
$env:Path="$env:JAVA_HOME\bin;$env:Path"

.\gradlew.bat bootRun
```

### Bash

```bash
export JAVA_HOME=/path/to/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"

./gradlew bootRun
```

The default `local` profile starts the app with H2 in-memory storage. After startup, open:

- `http://localhost:8080/hello`
- `http://localhost:8080/api/books`
- `http://localhost:8080/docs`
- `http://localhost:8080/h2-console`
- `http://localhost:8080/actuator/health`

## Environment Variables

`.env.example` contains the supported shell variables for local work. The project does **not** auto-load `.env`, so treat it as a template:

1. Copy `.env.example` to `.env` if you want a private local reference file.
2. Export the values in your shell, IDE run configuration, or Docker Compose environment.

Variables you are most likely to need:

- `JAVA_HOME` for Gradle and the toolchain
- `IDEA_HOME` or `IDEA_FORMATTER_BINARY` for Spotless Java formatting
- `SPRING_PROFILES_ACTIVE` if you want to override the default `local` profile
- `DATABASE_*` variables when running the `prod` profile against PostgreSQL

## IDE Setup

### IntelliJ IDEA

Recommended baseline:

1. Import the project as a Gradle project
2. Set the project SDK to Java 25
3. Set Gradle JVM to Java 25
4. If you want Spotless to delegate Java formatting to IntelliJ, export one of:

```powershell
$env:IDEA_FORMATTER_BINARY='C:\Path\To\IntelliJ IDEA\bin\idea64.exe'
```

```powershell
$env:IDEA_HOME='C:\Path\To\IntelliJ IDEA'
```

### VS Code

For local VS Code use:

- Extension Pack for Java
- Spring Boot Extension Pack
- Docker

For the containerized path, use the dev container instructions in `.devcontainer/README.md` or the short version in `.devcontainer/QUICK_START.md`.

## Database Modes

### Default Local Mode: H2

No external database is required.

- Active profile: `local`
- JDBC URL: `jdbc:h2:mem:demo-db`
- H2 console: `http://localhost:8080/h2-console`

Run locally:

```powershell
.\gradlew.bat bootRun
```

### Production-Like Local Mode: PostgreSQL

Use this when you want to exercise the `prod` profile.

Start PostgreSQL:

```powershell
docker-compose up -d
```

Run the app with the production profile:

```powershell
$env:JAVA_HOME='C:\Users\kamki\.jdks\azul-25.0.3'
$env:Path="$env:JAVA_HOME\bin;$env:Path"

.\gradlew.bat bootRun --args='--spring.profiles.active=prod'
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
.\gradlew.bat bootRun
.\gradlew.bat test
.\gradlew.bat asciidoctor
.\gradlew.bat dockerBuild
```

Useful endpoints once the app is running:

- `GET /hello`
- `GET /api/books`
- `GET /docs`
- `GET /actuator/info`
- `GET /actuator/health`
- `GET /actuator/health/liveness`
- `GET /actuator/health/readiness`
- `GET /actuator/prometheus`

## Running Tests And Quality Checks

Set Java 25 in the same shell session first, then run:

```powershell
.\gradlew.bat spotlessCheck
.\gradlew.bat --no-problems-report pmdMain
.\gradlew.bat --no-problems-report test
.\gradlew.bat asciidoctor
```

Optional:

```powershell
.\gradlew.bat qodanaScan
```

## Building Docker Images

Build with Gradle:

```powershell
.\gradlew.bat dockerBuild
.\gradlew.bat dockerBuild -PdockerImageName=my-app:dev
```

Build directly with Docker:

```powershell
docker build -t technical-interview-demo .
docker run --rm -p 8080:8080 technical-interview-demo
```

The container uses the `prod` profile by default.

## OAuth Setup Status

No OAuth flow is implemented in the current application. There is nothing to register with GitHub, Google, or another provider yet.

If Phase `5.1 Add Spring Security with OAuth 2.0` is implemented later, add the provider credentials through environment variables or a local non-committed properties file instead of hardcoding secrets.

## Troubleshooting

### Gradle fails because Java 11 is active

Symptom:

- build errors mention an unsupported Java version

Fix:

```powershell
$env:JAVA_HOME='C:\Users\kamki\.jdks\azul-25.0.3'
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

### PostgreSQL prod profile will not start

Symptom:

- connection refused errors on startup

Fix:

1. Confirm Docker Desktop is running.
2. Run `docker-compose up -d`.
3. Verify the database is healthy with `docker ps`.
4. Re-run the app with `--spring.profiles.active=prod`.

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
