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
$env:JAVA_HOME='C:\Users\kamki\.jdks\azul-25.0.3'
$env:Path="$env:JAVA_HOME\bin;$env:Path"

docker-compose up -d
.\gradlew.bat bootRun
```

### Bash

```bash
export JAVA_HOME=/path/to/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"

./gradlew bootRun
```

The default `local` profile expects PostgreSQL on `localhost:5432`. The included `docker-compose.yml` starts that database for you. After startup, open:

- `http://localhost:8080/hello`
- `http://localhost:8080/api/books`
- `http://localhost:8080/docs`
- `http://localhost:8080/actuator/health`

## Environment Variables

`.env.example` contains the supported shell variables for local work. The project does **not** auto-load `.env`, so treat it as a template:

1. Copy `.env.example` to `.env` if you want a private local reference file.
2. Export the values in your shell, IDE run configuration, or Docker Compose environment.

Variables you are most likely to need:

- `JAVA_HOME` for Gradle and the toolchain
- `IDEA_HOME` or `IDEA_FORMATTER_BINARY` for Spotless Java formatting
- `SPRING_PROFILES_ACTIVE` if you want to override the default `local` profile
- `DATABASE_*` variables when overriding the default PostgreSQL connection
- `GITHUB_CLIENT_ID` and `GITHUB_CLIENT_SECRET` when enabling the optional `oauth` profile for authenticated write flows
- `ADMIN_LOGINS` when you want one or more GitHub logins to receive the persisted `ADMIN` role

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

### Default Local Mode: PostgreSQL

Use the included `docker-compose.yml` to run PostgreSQL locally.

Start PostgreSQL:

```powershell
docker-compose up -d
```

Run the app with the default local profile:

```powershell
$env:JAVA_HOME='C:\Users\kamki\.jdks\azul-25.0.3'
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

- `GET /hello`
- `GET /api/books`
- `GET /docs`
- `GET /actuator/info`
- `GET /actuator/health`
- `GET /actuator/health/liveness`
- `GET /actuator/health/readiness`
- `GET /actuator/prometheus`

## Running Tests And Quality Checks

Set Java 25 in the same shell session first. Docker Desktop must also be running because the `test` task starts PostgreSQL through Testcontainers and `build` now also performs the Docker image build.

```powershell
.\gradlew.bat build
```

`build` now covers Spotless, PMD, tests, Asciidoctor generation, boot jar creation, and the Docker image build.
Use focused commands such as `test`, `asciidoctor`, or `dockerBuild` only when you intentionally want a narrower loop.

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

## OAuth Setup

The application supports GitHub OAuth login behind the optional `oauth` profile.

Use it when you want to exercise the protected write endpoints from a browser.

### GitHub OAuth App

Create a GitHub OAuth App with:

- Homepage URL: `http://localhost:8080`
- Authorization callback URL: `http://localhost:8080/login/oauth2/code/github`

Then export the credentials and start the app with the extra profile:

```powershell
$env:GITHUB_CLIENT_ID='your-github-client-id'
$env:GITHUB_CLIENT_SECRET='your-github-client-secret'
$env:ADMIN_LOGINS='your-github-login'
$env:SPRING_PROFILES_ACTIVE='local,oauth'

docker-compose up -d
.\gradlew.bat bootRun
```

Start the login flow at:

- `http://localhost:8080/oauth2/authorization/github`

Protected browser requests also require a CSRF token, so use the generated HTML docs or a normal browser session rather than trying to replay state-changing requests without cookies and headers.

Authenticated sessions are persisted in PostgreSQL through Spring Session JDBC, using tables `SPRING_SESSION` and `SPRING_SESSION_ATTRIBUTES`.

Role behavior:

- every authenticated GitHub login is persisted as an application user with the `USER` role
- logins listed in `ADMIN_LOGINS` also receive the `ADMIN` role
- category creation and localization-message management require `ADMIN`
- the current persisted user profile is available at `GET /api/users/me`
- preferred-language updates are available at `PUT /api/users/me/preferred-language`

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

- `/oauth2/authorization/github` returns an error or redirect loop

Fix:

1. Confirm `SPRING_PROFILES_ACTIVE` includes `oauth`.
2. Confirm `GITHUB_CLIENT_ID` and `GITHUB_CLIENT_SECRET` are exported in the same shell or run configuration.
3. Confirm the GitHub OAuth App callback URL is `http://localhost:8080/login/oauth2/code/github`.
4. Re-run the app with `.\gradlew.bat bootRun`.

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
