# Technical Interview Demo

`AGENTS.md` is the AI-facing counterpart of this file. Keep both files aligned when project setup, API behavior, formatter usage, logging/tracing behavior, or quality gates change.

## Overview

This repository contains a small Spring Boot demo application built with Gradle Kotlin DSL.

The demo currently includes:

- `GET /hello` returning `Hello World!`
- A REST API for `Book` under `/api/books` with pagination, filtering, and category assignment
- A REST API for `Category` under `/api/categories`
- A REST API for `LocalizationMessage` under `/api/localization-messages` with CRUD, pagination, and key/language lookup
- A REST API for the authenticated user profile under `/api/users/me`
- OAuth 2.0 protected write endpoints with JDBC-backed HTTP sessions for reviewer-friendly sign-in
- Append-only audit logging for state-changing `Book` and `LocalizationMessage` operations
- Git tag based application versioning plus a human-readable `CHANGELOG.md`
- PostgreSQL for local and production-style runtime profiles, plus Testcontainers-backed integration tests
- Seed data loaded at startup
- MVC and integration-style tests
- Request tracing, structured application logging, in-memory lookup caches, and application-specific Prometheus metrics

Primary goal: keep the project small, readable, and suitable for technical interview demos.

## Tech Stack

- Java 25 toolchain
- Spring Boot 4.0.6
- Spring Web MVC
- Spring Data JPA
- Spring Cache
- Spring Security
- Spring Security OAuth2 Client
- Spring Session JDBC
- PostgreSQL
- Testcontainers
- Caffeine
- Gradle Wrapper
- JUnit 5
- Lombok
- Spring AOP
- Micrometer tracing with OpenTelemetry
- Spring REST Docs
- Asciidoctor
- Flyway
- Error Prone
- PMD

## Requirements

The machine default `JAVA_HOME` may point to Java 11, which is too old for this build.

Use a compatible JDK before running Gradle commands. Example for PowerShell:

```powershell
$env:JAVA_HOME='C:\Users\kamki\.jdks\azul-25.0.3'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
```

For a full local onboarding flow, see `SETUP.md`. A starter environment template is available in `.env.example`.

Docker Desktop is also required for `.\gradlew.bat test` and `.\gradlew.bat build` because the test suite starts PostgreSQL through Testcontainers and the build lifecycle now includes Docker image creation.

## OAuth And Session Setup

The implemented OAuth provider is a GitHub OAuth App.

The OAuth flow is intentionally isolated behind the optional `oauth` Spring profile so the default local startup stays simple for read-only review.

To enable authenticated write flows locally, provide GitHub credentials and activate the profile:

```powershell
$env:GITHUB_CLIENT_ID='your-github-client-id'
$env:GITHUB_CLIENT_SECRET='your-github-client-secret'
$env:SPRING_PROFILES_ACTIVE='local,oauth'

.\gradlew.bat bootRun
```

After startup, begin the login flow at `GET /oauth2/authorization/github`.

Session behavior:

- authenticated sessions are stored in PostgreSQL through Spring Session JDBC
- local and test profiles keep the session cookie non-secure for HTTP development
- the `prod` profile uses `SESSION_COOKIE_SECURE=true` by default unless explicitly overridden
- the session cookie name is `technical-interview-demo-session`

Role behavior:

- every authenticated GitHub login is persisted as an application user
- every persisted user receives the `USER` role
- logins listed in `ADMIN_LOGINS` also receive the `ADMIN` role
- category and localization-message management are restricted to `ADMIN`

## Spring Profiles

The application uses Spring profiles to manage environment-specific configuration:

### Available Profiles

- **`local`** (default) - Development with PostgreSQL on `localhost`
  - Uses Docker Compose friendly defaults for `localhost:5432/technical_interview_demo`
  - DEBUG logging for Hibernate and Spring Web
  - Flyway manages schema creation and Hibernate validates the mapping
  - Used by default when running `.\gradlew.bat bootRun`

- **`prod`** - Production with PostgreSQL database
  - Minimal logging (WARN level)
  - Schema validation only (Flyway manages migrations)
  - Used in Docker containers automatically

- **`oauth`** - Enables GitHub OAuth client registration
  - Requires `GITHUB_CLIENT_ID` and `GITHUB_CLIENT_SECRET`
  - Enables interactive login for protected write endpoints

- **`test`** - Testing with PostgreSQL via Testcontainers
  - Activated through the shared `@TestcontainersTest` meta-annotation used by integration-style tests
  - Flyway manages schema creation and Hibernate validates the mapping
  - Docker must be available when running the test suite

### Activating Profiles

```powershell
# Start PostgreSQL for local development
docker-compose up -d

# Run with local profile (default)
.\gradlew.bat bootRun

# Run with prod profile
.\gradlew.bat bootRun --args='--spring.profiles.active=prod'

# Run with local database and GitHub OAuth login enabled
$env:GITHUB_CLIENT_ID='your-github-client-id'
$env:GITHUB_CLIENT_SECRET='your-github-client-secret'
$env:ADMIN_LOGINS='your-github-login'
.\gradlew.bat bootRun --args='--spring.profiles.active=local,oauth'

# Build app for prod
.\gradlew.bat bootJar -Dspring.profiles.active=prod

# Docker container automatically uses prod profile
docker run --rm -p 8080:8080 technical-interview-demo
```

### Running PostgreSQL Locally With Docker

Use the included `docker-compose.yml` for local development:

```powershell
# Start PostgreSQL
docker-compose up -d

# Wait for PostgreSQL to be ready (5-10 seconds)
Start-Sleep -Seconds 3

# Run the app with the default local profile
.\gradlew.bat bootRun

# Stop PostgreSQL when done
docker-compose down
```

**Database Connection Details:**
- Host: `localhost`
- Port: `5432`
- Database: `technical_interview_demo`
- User: `postgres`
- Password: `changeme` (change in production!)

**Environment Variables for Custom DB Configuration:**
```powershell
# Override default PostgreSQL configuration
$env:DATABASE_HOST='myhost.com'
$env:DATABASE_PORT='5433'
$env:DATABASE_NAME='my_database'
$env:DATABASE_USER='my_user'
$env:DATABASE_PASSWORD='my_password'

.\gradlew.bat bootRun
```

## Development Container (Dev Containers)

This project includes a preconfigured dev container for VS Code's Remote - Containers extension.

### Quick Start

1. Install VS Code and the "Dev Containers" extension
2. Open the project folder in VS Code
3. Press `Ctrl+Shift+P` and select "Dev Containers: Reopen in Container"
4. VS Code will build and start the container (5-10 minutes on first run)

### What's Included

- Java 25 JDK (official Microsoft dev container image)
- Gradle (via wrapper)
- Docker & Docker Compose (Docker-in-Docker)
- Preconfigured VS Code extensions (Java, Spring Boot, Docker, etc.)
- PostgreSQL service (optional, via Docker Compose)
- Prometheus service (optional, for metrics)

### Benefits

- **Consistent environment:** All developers use identical tooling
- **No local setup required:** Docker handles all dependencies
- **Isolated:** Doesn't interfere with other local projects
- **Easy cleanup:** Just delete the container
- **One-command setup:** Automatic initialization scripts

For detailed dev container documentation, see [.devcontainer/README.md](.devcontainer/README.md).

## Run The Application

Start the application with:

```powershell
docker-compose up -d
.\gradlew.bat bootRun
```

Useful local endpoints:

- `GET /docs`
- `GET /hello`
- `GET /api/books`
- `GET /api/categories`
- `GET /api/users/me` after login
- `GET /oauth2/authorization/github` when the `oauth` profile is active
- `GET /actuator/info`
- `GET /actuator/health`
- `GET /actuator/health/liveness`
- `GET /actuator/health/readiness`
- `GET /actuator/prometheus`

## Docker

Build the image with Gradle:

```powershell
.\gradlew.bat build
.\gradlew.bat dockerBuild
.\gradlew.bat dockerBuild -PdockerImageName=my-app:dev
```

The default image name is `technical-interview-demo`.
`.\gradlew.bat build` now includes the Docker image build by default. Use `.\gradlew.bat build -x dockerBuild` only when you explicitly want to skip the image step.

You can also build the image directly with Docker:

```powershell
.\gradlew.bat bootJar
$jar = (Get-ChildItem build\libs\technical-interview-demo-*-boot.jar | Sort-Object LastWriteTimeUtc -Descending | Select-Object -First 1).Name
docker build --build-arg JAR_FILE="build/libs/$jar" -t technical-interview-demo .
```

Run the container with:

```powershell
docker run --rm -p 8080:8080 technical-interview-demo
```

The Docker image packages the current versioned Spring Boot fat jar produced by Gradle and runs it on Java 25.
It also includes a container health check against `GET /actuator/health/readiness`.
The container uses Microsoft Build of OpenJDK and starts the app with `jaz`.

## Documentation

API documentation is generated from tests using Spring REST Docs and assembled with Asciidoctor.

Generate it with:

```powershell
.\gradlew.bat asciidoctor
```

Generated output:

- snippets: `build/generated-snippets`
- HTML docs: `build/docs/asciidoc/index.html` with linked controller and technical-endpoint pages in the same directory

Packaging and runtime behavior:

- `bootJar` bundles the generated HTML documentation into the runnable jar
- the running application serves the documentation at `GET /docs`
- the generated index page includes build metadata loaded from `/META-INF/build-info.properties`
- the generated docs include example success and error responses captured from tests

## Versioning And Releases

The application version is derived from the nearest reachable annotated git tag through the Gradle build.

Release policy:

- use semantic version tags in the form `vMAJOR.MINOR.PATCH`
- create an annotated git tag when a roadmap phase is completed
- assign the next version number in `git log --first-parent` order so older mainline commits never receive a higher semantic version than newer ones
- record the human-readable summary for that release in `CHANGELOG.md`
- keep release tags aligned with `COMPLETED_TASKS.md`

## Project Structure

- `CHANGELOG.md`: human-readable release notes aligned with roadmap-phase tags
- `CONTRIBUTING.md`: contribution workflow, review expectations, and quality gates
- `SETUP.md`: developer onboarding and troubleshooting guide
- `.env.example`: optional environment variable template for local shells or container tooling
- `build.gradle.kts`: Gradle build and dependencies
- `config/pmd/pmd-ruleset.xml`: curated PMD ruleset
- `src/main/java/team/jit/technicalinterviewdemo/config/`: application configuration classes such as cache enablement
- `src/main/java/team/jit/technicalinterviewdemo/TechnicalInterviewDemoApplication.java`: app entry point
- `src/main/java/team/jit/technicalinterviewdemo/HelloController.java`: hello-world endpoint
- `src/main/java/team/jit/technicalinterviewdemo/book/`: `Book` domain, service, repository, and REST API
- `src/main/java/team/jit/technicalinterviewdemo/cache/`: cache names and related cache constants
- `src/main/java/team/jit/technicalinterviewdemo/category/`: category entity, repository, service, controller, and seed data
- `src/main/java/team/jit/technicalinterviewdemo/localization/`: localization entity, repository, service, and seed data
- `src/main/java/team/jit/technicalinterviewdemo/metrics/`: application-specific Micrometer gauges and counters
- `src/main/java/team/jit/technicalinterviewdemo/audit/`: append-only audit-log entity, repository, and service
- `src/main/java/team/jit/technicalinterviewdemo/user/`: persisted user model, profile endpoints, role handling, and authenticated-user synchronization
- `src/main/java/team/jit/technicalinterviewdemo/api/`: API exception handling and custom exceptions
- `src/main/java/team/jit/technicalinterviewdemo/docs/`: documentation endpoint and resource mapping
- `src/main/java/team/jit/technicalinterviewdemo/logging/`: HTTP tracing/logging and service-call logging
- `src/main/resources/db/migration/`: Flyway SQL migrations
- `src/docs/asciidoc/`: documentation landing page plus per-controller and technical-endpoint Asciidoc sources
- `src/main/resources/application.properties`: runtime configuration
- `src/test/java/team/jit/technicalinterviewdemo/`: application and API tests
- `src/test/java/team/jit/technicalinterviewdemo/TestcontainersTest.java`: shared integration-test annotation for PostgreSQL-backed tests
- `src/test/java/team/jit/technicalinterviewdemo/PostgresTestcontainersConfiguration.java`: shared PostgreSQL Testcontainers configuration

## API

### Documentation Endpoint

- `GET /docs`

Redirects to the generated HTML API documentation served by the application.

### Hello Endpoint

- `GET /hello`

Response:

```text
Hello World!
```

### Book API

- `GET /api/books?page=0&size=20&sort=id,asc&title=clean&category=java&yearFrom=2000&yearTo=2020`
- `GET /api/books/{id}`
- `POST /api/books`
- `PUT /api/books/{id}`
- `DELETE /api/books/{id}`

`GET /api/books` returns a paginated response instead of a raw array.

Example create payload:

```json
{
  "title": "Spring in Action",
  "author": "Craig Walls",
  "isbn": "9781617297571",
  "publicationYear": 2022,
  "categories": ["Java", "Best Practices"]
}
```

Example update payload:

```json
{
  "title": "Spring in Action, Second Edition",
  "author": "Craig Walls",
  "version": 0,
  "publicationYear": 2026,
  "categories": ["Java"]
}
```

Validation rules:

- `title` is required
- `author` is required
- `isbn` is required
- `publicationYear` is required
- `isbn` must be unique across books
- `isbn` is immutable after creation and is not updated by `PUT /api/books/{id}`
- `version` is returned for each book and required on `PUT /api/books/{id}` for optimistic locking
- `GET /api/books` supports optional `title`, `author`, and `isbn` substring filters
- `GET /api/books` supports repeated `category` filters matched case-insensitively against assigned category names
- `GET /api/books` supports `year` for exact publication year matching
- `GET /api/books` supports `yearFrom` and `yearTo` for inclusive publication year ranges
- `GET /api/books` supports repeated `sort` parameters such as `sort=title,asc&sort=year,desc`
- `year` cannot be combined with `yearFrom` or `yearTo`
- `categories` is optional on create and update, but every listed category must already exist

### Category API

- `GET /api/categories`
- `POST /api/categories`

Example create payload:

```json
{
  "name": "Architecture"
}
```

Validation rules:

- `name` is required
- category names are unique ignoring case
- `GET /api/categories` returns categories ordered by `name`

### Localization API

- `GET /api/localization-messages?page=0&size=20&sort=id,asc`
- `GET /api/localization-messages/{id}`
- `GET /api/localization-messages/key/{messageKey}/lang/{language}`
- `GET /api/localization-messages/language/{language}`
- `POST /api/localization-messages`
- `PUT /api/localization-messages/{id}`
- `DELETE /api/localization-messages/{id}`

Example create or update payload:

```json
{
  "messageKey": "error.book.not_found",
  "language": "en",
  "messageText": "The requested book was not found.",
  "description": "English message for missing book errors."
}
```

Validation rules:

- `messageKey` is required and must match `^[a-z0-9._-]+$`
- `language` is required and must be one of the supported two-letter ISO 639-1 codes: `en`, `es`, `de`, `fr`, `pl`, `uk`, `no`
- `messageText` is required
- `description` is optional
- `(messageKey, language)` must be unique

### User Profile API

- `GET /api/users/me`
- `PUT /api/users/me/preferred-language`

Example preferred-language update payload:

```json
{
  "preferredLanguage": "pl"
}
```

Behavior:

- the current authenticated GitHub user is persisted on the first authenticated request
- `GET /api/users/me` returns the persisted application user, roles, and timestamps
- `PUT /api/users/me/preferred-language` stores an optional two-letter supported language code or clears it when the value is blank or null
- when no `lang`, supported `Accept-Language`, or `language` cookie is present, localized error responses fall back to the authenticated user's preferred language before defaulting to English

Actuator endpoints:

- `GET /actuator/info`
- `GET /actuator/health`
- `GET /actuator/health/liveness`
- `GET /actuator/health/readiness`
- `GET /actuator/prometheus`

### Security

Authentication rules:

- public without authentication: `GET /hello`, `GET /docs`, `GET /api/books/**`, `GET /api/categories`, `GET /api/localization-messages/**`, `GET /actuator/health`, `GET /actuator/health/**`, `GET /actuator/info`, and `GET /actuator/prometheus`
- protected with authenticated session: `GET /api/users/me`, `PUT /api/users/me/preferred-language`, `POST /api/books`, `PUT /api/books/{id}`, `DELETE /api/books/{id}`, `POST /api/categories`, `POST /api/localization-messages`, `PUT /api/localization-messages/{id}`, and `DELETE /api/localization-messages/{id}`
- role-restricted to `ADMIN`: category creation and localization-message create, update, and delete operations
- protected browser requests also require a valid CSRF token
- interactive login is available at `GET /oauth2/authorization/github` when the `oauth` profile is active
- authenticated HTTP sessions are persisted in PostgreSQL tables `SPRING_SESSION` and `SPRING_SESSION_ATTRIBUTES`

## Seed Data

On startup, the app inserts sample books if the table is empty:

- `Clean Code`
- `Effective Java`

It also seeds a small category set:

- `Best Practices`
- `Java`
- `Software Engineering`

It also seeds localization messages for the current API error scenarios in `en`, `es`, `de`, `fr`, `pl`, `uk`, and `no`.

## Localization Message Keys

Naming convention:

- Use `error.<domain>.<condition>` for server-managed error messages
- Keep keys stable even if the user-facing text changes
- Prefer extending the existing domain groups (`book`, `localization`, `request`, `data`, `server`) before adding new ones

Current seeded keys:

- `error.book.isbn_duplicate`
- `error.book.not_found`
- `error.book.stale_version`
- `error.data.integrity_violation`
- `error.localization.duplicate`
- `error.localization.not_found`
- `error.request.constraint_violation`
- `error.request.forbidden`
- `error.request.invalid`
- `error.request.invalid_parameter`
- `error.request.malformed_body`
- `error.request.method_not_allowed`
- `error.request.missing_header`
- `error.request.missing_parameter`
- `error.request.resource_not_found`
- `error.request.unsupported_media_type`
- `error.request.validation_failed`
- `error.server.internal`

## Error Handling

The API uses `ProblemDetail` responses through centralized `@RestControllerAdvice`.

Current behavior:

- expected client errors return sanitized messages
- error responses also include `messageKey`, localized `message`, and resolved `language`
- `Accept-Language` is used for browser-compatible language negotiation
- query parameter `lang` overrides the browser preference and accepts values such as `pl` or `pl-PL`
- cookie `language` is used as fallback when neither `lang` nor a supported `Accept-Language` value is available
- authenticated user preference is used as a final localized-error fallback before English when no explicit request preference is present
- request-scoped language resolution is captured once and reused during localized error handling
- validation errors include field-level details
- duplicate ISBN returns `409 Conflict`
- stale update versions return `409 Conflict`
- missing books return `404 Not Found`
- unexpected server errors return a generic `500 Internal Server Error`

Client error responses do not expose stack traces or internal implementation details.

## Logging And Tracing

The application includes:

- OpenTelemetry-compatible tracing through Micrometer
- `traceId` and `spanId` in console logs
- `traceparent` response header on HTTP requests when tracing is active
- Caffeine-backed in-memory caches are used for localization lookups, localization language views, category lists, and the category assignment directory
- request and error logging redact sensitive query parameters before they reach the logs
- request start and response completion logs for HTTP traffic
- service-layer AOP logging with method parameters and execution time
- redaction of common sensitive parameters and fields
- optional `org.springframework.web` DEBUG logging is available as a commented property in `application.properties`
- Hibernate SQL statement logging through `org.hibernate.SQL`
- Hibernate statistics enabled through `hibernate.generate_statistics=true`
- explicit logs for successful database-changing operations such as create, update, delete, and seed writes
- append-only audit records are persisted for book and localization-message create, update, and delete operations with the acting user snapshot when available
- readiness and liveness health probes through actuator
- Prometheus metrics exposed through `/actuator/prometheus`
- custom Micrometer metrics are published under the `technical.interview.demo.*` prefix for book, category, localization, user, and cache activity
- JPA fetch plans are controlled in repositories rather than through `FetchType.EAGER` on entities

The HTTP tracing logger intentionally skips `/actuator/health` and its subpaths.

## Database Schema

Flyway manages the schema from SQL migrations under `src/main/resources/db/migration`.

Hibernate is configured with `spring.jpa.hibernate.ddl-auto=validate`, so the application validates the mapped schema instead of creating or updating it automatically.

The test suite uses the same Flyway migrations against PostgreSQL via Testcontainers to keep integration coverage closer to the production profile.

## Formatting

The build uses Spotless.

Java formatting is delegated to IntelliJ IDEA's formatter so the result stays as close as practical to IntelliJ defaults.

If IntelliJ is not configured, Gradle build and test tasks still pass. In that case, Spotless skips Java formatting tasks instead of failing the build.

If the IntelliJ formatter binary is not already available on `PATH`, provide it through one of:

```powershell
$env:IDEA_FORMATTER_BINARY='C:\Path\To\IntelliJ IDEA\bin\idea64.exe'
```

```powershell
$env:IDEA_HOME='C:\Path\To\IntelliJ IDEA'
```

```powershell
.\gradlew.bat spotlessApply -PideaFormatterBinary='C:\Path\To\IntelliJ IDEA\bin\idea64.exe'
```

Keep `.editorconfig` aligned with the intended IntelliJ formatting profile.

## Development Guidelines

- Preserve the demo nature of the project. Prefer simple code over abstractions.
- Keep package naming under `team.jit.technicalinterviewdemo`.
- Use Lombok for routine Java boilerplate when it keeps the code shorter and clearer.
- Keep the project compatible with Error Prone checks that run during Java compilation.
- Keep the project compatible with the curated PMD ruleset in `config/pmd/pmd-ruleset.xml`.
- When returning `ResponseEntity`, assign the response payload to a local variable first so controller breakpoints can inspect it before the return.
- Keep non-trivial business logic in `@Service` beans.
- Prefer Spring MVC controllers and Spring Data repositories for new demo endpoints.
- Use PostgreSQL for runtime and keep local setup Docker-friendly.
- Avoid adding unnecessary libraries when Spring Boot already provides the needed feature.
- Keep REST responses JSON-friendly.
- Add or update tests when API behavior changes.
- Do not remove the existing `hello` or `book` demo endpoints unless intentionally changing the demo scope.

For contribution workflow expectations, see `CONTRIBUTING.md`.
For release history, see `CHANGELOG.md`.

## Quality Checks

Before finishing changes, run:

```powershell
.\gradlew.bat build
```

If tests require Java setup first, export `JAVA_HOME` to a compatible JDK in the same shell session.

If `test` or `build` fails before application startup, confirm Docker Desktop is running because Testcontainers provisions PostgreSQL for the integration suite and `build` also runs the Docker image creation step.

`build` now covers Spotless, PMD, tests, Asciidoctor generation, boot jar creation, and the Docker image build.
Use focused commands such as `spotlessCheck`, `pmdMain`, `test`, or `asciidoctor` only when you intentionally want a narrower loop.

## Definition Of Done

A change is considered complete when:

- the code remains consistent with the current simple demo architecture
- the application still starts
- `.\gradlew.bat build` passes
- new endpoint or behavior changes are covered by tests when practical
