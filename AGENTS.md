# AI Project Instructions

`README.md` is the human-facing counterpart of this file. Keep both files aligned when project setup, API behavior, formatter usage, logging/tracing behavior, or quality gates change.

## Project Snapshot

Small Spring Boot demo app for technical interview exercises.

Current scope:

- `GET /docs` redirects to generated API documentation
- `GET /hello` returns `Hello World!`
- CRUD-style `Book` API under `/api/books` with pagination, filtering, optimistic locking, and category assignment
- `Category` API under `/api/categories`
- CRUD-style `LocalizationMessage` API under `/api/localization-messages` with pagination and key/language lookup
- authenticated-user profile API under `/api/users/me`
- OAuth 2.0 protected write endpoints with JDBC-backed HTTP sessions
- OpenAPI contract endpoints at `/v3/api-docs` and `/v3/api-docs.yaml` with an approved-baseline compatibility gate
- append-only audit logging for state-changing `Book` and `LocalizationMessage` operations
- git-tag-based application versioning with a human-readable `CHANGELOG.md`
- actuator endpoints for `health`, `info`, liveness/readiness probes, and Prometheus metrics
- PostgreSQL for local and production-style runtime profiles
- PostgreSQL-backed integration tests via Testcontainers
- startup seed data
- MVC/integration-style tests
- request tracing, structured logging, in-memory lookup caches, and application-specific Prometheus metrics
- **Development Container (dev container) for VS Code with zero-friction setup**

Primary goal: keep the codebase small, readable, and easy to reason about.

## Stack

- Java 25
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
- Lombok
- Spring AOP
- Micrometer tracing with OpenTelemetry
- Spring REST Docs
- Asciidoctor
- Flyway
- Error Prone
- PMD

## Environment

Default `JAVA_HOME` on this machine may point to Java 11. Use JDK 25 before running Gradle:

```powershell
$env:JAVA_HOME='C:\Users\kamki\.jdks\azul-25.0.3'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
```

Common commands:

```powershell
.\gradlew.bat build
.\gradlew.bat bootRun
.\gradlew.bat test
.\gradlew.bat asciidoctor
.\gradlew.bat dockerBuild
.\gradlew.bat dockerBuild -PdockerImageName=my-app:dev
docker build -t technical-interview-demo .
docker run --rm -p 8080:8080 technical-interview-demo
```

Developer onboarding lives in `SETUP.md`. An optional shell/env template is provided in `.env.example`.

When building directly with Docker after `.\gradlew.bat bootJar`, prefer passing `--build-arg JAR_FILE=build/libs/<exact-boot-jar-name>` so Docker uses the current boot jar without relying on `clean` to remove older artifacts.

Docker Desktop is required for `.\gradlew.bat test` and `.\gradlew.bat build` because the integration suite starts PostgreSQL through Testcontainers and the build lifecycle now includes Docker image creation.

## OAuth Provider And Sessions

The implemented OAuth provider is a GitHub OAuth App.

Keep that provider choice fixed unless the user explicitly asks to revisit it, and keep GitHub client credentials out of version control.

The OAuth flow is enabled only when the `oauth` profile is active.

Local reviewer setup:

```powershell
$env:GITHUB_CLIENT_ID='your-github-client-id'
$env:GITHUB_CLIENT_SECRET='your-github-client-secret'
$env:SPRING_PROFILES_ACTIVE='local,oauth'

.\gradlew.bat bootRun
```

Interactive login starts at `GET /oauth2/authorization/github`.

Session rules:

- authenticated sessions are stored in PostgreSQL through Spring Session JDBC
- local and test profiles keep the session cookie non-secure for HTTP development
- `prod` uses `SESSION_COOKIE_SECURE=true` by default unless explicitly overridden
- the session cookie name is `technical-interview-demo-session`

Role rules:

- every authenticated GitHub login is persisted as an application user
- every persisted user receives the `USER` role
- logins listed in `ADMIN_LOGINS` also receive the `ADMIN` role
- category management and localization-message management are restricted to `ADMIN`

## Spring Profiles

The application uses Spring profiles for environment-specific configuration.

**Available Profiles:**
- `local` (default) - Development with PostgreSQL on localhost and debug logging
- `prod` - Production with PostgreSQL, minimal logging
- `test` - Testing with PostgreSQL via Testcontainers, activated by `@TestcontainersTest`
- `oauth` - GitHub OAuth client registration for interactive login against protected write endpoints

**Profile Activation:**
- Default: `spring.profiles.active=local` in `application.properties`
- Override: `--spring.profiles.active=prod` on command line
- Docker: Automatically uses `prod` profile (set in `Dockerfile`)
- Tests: Use `@TestcontainersTest` to activate the `test` profile and import the shared PostgreSQL container configuration

**Configuration Files:**
- `src/main/resources/application.properties` - common settings for all profiles
- `src/main/resources/application-local.properties` - local PostgreSQL development config
- `src/main/resources/application-oauth.properties` - GitHub OAuth client configuration
- `src/main/resources/application-prod.properties` - production PostgreSQL config
- `src/test/resources/application-test.properties` - test config

## Formatter Contract

Spotless is the formatter entry point.

Java formatting uses IntelliJ IDEA's formatter when available. If IntelliJ is not configured, Java formatting is skipped rather than failing the build.

Provide the formatter binary through one of:

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

## Generated Docs

API documentation is generated from tests using Spring REST Docs and assembled with Asciidoctor.

```powershell
.\gradlew.bat asciidoctor
```

Outputs:

- snippets in `build/generated-snippets`
- HTML documentation in `build/docs/asciidoc/index.html` with linked controller and technical-endpoint pages in the same directory

Packaging and runtime behavior:

- `bootJar` bundles the generated HTML documentation into the runnable jar
- the running application serves the documentation at `GET /docs`
- the generated index page includes build metadata loaded from `/META-INF/build-info.properties`
- the generated docs include example success and error responses captured from tests
- the container image packages the current versioned Gradle boot jar and includes a health check against `GET /actuator/health/readiness`
- the container image uses Microsoft Build of OpenJDK and starts the app with `jaz`

OpenAPI contract workflow:

- the application exposes JSON at `GET /v3/api-docs` and YAML at `GET /v3/api-docs.yaml`
- the approved baseline is stored at `src/test/resources/openapi/approved-openapi.json`
- `OpenApiCompatibilityIntegrationTests` compares the current normalized contract to that baseline and fails on breaking changes
- GitHub Actions runs the same compatibility test on pushes to `main` and on pull requests
- refresh the approved baseline intentionally with `.\gradlew.bat refreshOpenApiBaseline`

## Versioning & Releases

The Gradle build version is derived from the nearest reachable annotated git tag.

Release policy:

- use semantic version tags in the form `vMAJOR.MINOR.PATCH`
- create an annotated git tag when a roadmap phase is completed
- assign the next version number in `git log --first-parent` order so older mainline commits never receive a higher semantic version than newer ones
- keep `CHANGELOG.md` aligned with release tags
- keep `COMPLETED_TASKS.md` aligned with release tags and completion commits

## Project Map

- `CHANGELOG.md`: human-readable release notes aligned with annotated phase-completion tags
- `CONTRIBUTING.md`: contribution workflow, review expectations, and quality gates
- `SETUP.md`: developer onboarding and troubleshooting guide
- `.env.example`: optional environment variable template for local shells or container tooling
- `build.gradle.kts`: build configuration and dependencies
- `config/pmd/pmd-ruleset.xml`: curated PMD rules
- `src/main/java/team/jit/technicalinterviewdemo/TechnicalInterviewDemoApplication.java`: app entry
- `src/main/java/team/jit/technicalinterviewdemo/HelloController.java`: hello endpoint
- `src/main/java/team/jit/technicalinterviewdemo/business/book/`: book entity, requests, repository, service, controller, seed data
- `src/main/java/team/jit/technicalinterviewdemo/technical/cache/`: cache names, cache enablement, and related cache constants
- `src/main/java/team/jit/technicalinterviewdemo/technical/security/`: security filter-chain, session configuration, and authenticated-user synchronization
- `src/main/java/team/jit/technicalinterviewdemo/business/category/`: category entity, repository, service, controller, and seed data
- `src/main/java/team/jit/technicalinterviewdemo/business/localization/`: localization entity, repository, service, exception, and seed data
- `src/main/java/team/jit/technicalinterviewdemo/technical/metrics/`: application-specific Micrometer gauges and counters
- `src/main/java/team/jit/technicalinterviewdemo/business/audit/`: append-only audit-log entity, repository, and service
- `src/main/java/team/jit/technicalinterviewdemo/business/user/`: persisted user model, profile endpoints, role handling, and authenticated-user synchronization
- `src/main/java/team/jit/technicalinterviewdemo/technical/api/`: exception handling and custom exceptions
- `src/main/java/team/jit/technicalinterviewdemo/technical/docs/`: documentation endpoint and resource mapping
- `src/main/java/team/jit/technicalinterviewdemo/technical/logging/`: HTTP tracing/logging and service-call logging
- `src/main/resources/db/migration/`: Flyway SQL migrations
- `src/docs/asciidoc/`: documentation landing page plus per-controller and technical-endpoint Asciidoc sources
- `src/test/resources/openapi/approved-openapi.json`: approved OpenAPI baseline used by the compatibility gate
- `src/main/resources/application.properties`: runtime configuration
- `src/test/java/team/jit/technicalinterviewdemo/`: application, API, tracing, and documentation tests
- `src/test/java/team/jit/technicalinterviewdemo/TestcontainersTest.java`: shared meta-annotation for PostgreSQL-backed integration tests
- `src/test/java/team/jit/technicalinterviewdemo/PostgresTestcontainersConfiguration.java`: shared PostgreSQL Testcontainers setup

## API Contract

Endpoints:

- `GET /docs`
- `GET /hello`
- `GET /api/books?page=0&size=20&sort=id,asc&title=clean&category=java&yearFrom=2000&yearTo=2020`
- `GET /api/books/{id}`
- `POST /api/books`
- `PUT /api/books/{id}`
- `DELETE /api/books/{id}`
- `GET /api/categories`
- `POST /api/categories`
- `GET /api/localization-messages?page=0&size=20&sort=id,asc`
- `GET /api/localization-messages/{id}`
- `GET /api/localization-messages/key/{messageKey}/lang/{language}`
- `GET /api/localization-messages/language/{language}`
- `POST /api/localization-messages`
- `PUT /api/localization-messages/{id}`
- `DELETE /api/localization-messages/{id}`
- `GET /api/users/me`
- `PUT /api/users/me/preferred-language`
- `GET /actuator/info`
- `GET /actuator/health`
- `GET /actuator/health/liveness`
- `GET /actuator/health/readiness`
- `GET /actuator/prometheus`

Security rules:

- public without authentication: `GET /hello`, `GET /docs`, `GET /api/books/**`, `GET /api/categories`, `GET /api/localization-messages/**`, `GET /actuator/health`, `GET /actuator/health/**`, `GET /actuator/info`, and `GET /actuator/prometheus`
- protected with authenticated session: `GET /api/users/me`, `PUT /api/users/me/preferred-language`, `POST /api/books`, `PUT /api/books/{id}`, `DELETE /api/books/{id}`, `POST /api/categories`, `POST /api/localization-messages`, `PUT /api/localization-messages/{id}`, and `DELETE /api/localization-messages/{id}`
- role-restricted to `ADMIN`: category creation and localization-message create, update, and delete operations
- interactive login is available at `GET /oauth2/authorization/github` when the `oauth` profile is active
- authenticated HTTP sessions are stored in `SPRING_SESSION` and `SPRING_SESSION_ATTRIBUTES`

Book rules:

- `title` is required
- `author` is required
- `isbn` is required on create
- `publicationYear` is required
- `isbn` must be unique
- `isbn` is immutable after creation and is not updated by `PUT /api/books/{id}`
- `GET /api/books` returns a paginated response
- `GET /api/books` supports optional `title`, `author`, and `isbn` substring filters
- `GET /api/books` supports repeated `category` filters matched case-insensitively against assigned category names
- `GET /api/books` supports `year` for exact publication year matching
- `GET /api/books` supports `yearFrom` and `yearTo` for inclusive publication year ranges
- `GET /api/books` supports repeated `sort` parameters such as `sort=title,asc&sort=year,desc`
- `year` cannot be combined with `yearFrom` or `yearTo`
- `version` is returned for each book and is required on `PUT /api/books/{id}` for optimistic locking
- `categories` is optional on create and update, but every listed category must already exist

Category rules:

- `name` is required
- category names are unique ignoring case
- `GET /api/categories` returns categories ordered by `name`

Localization message rules:

- `messageKey` is required and must match `^[a-z0-9._-]+$`
- `language` is required and must be one of the supported two-letter ISO 639-1 codes: `en`, `es`, `de`, `fr`, `pl`, `uk`, `no`
- `messageText` is required
- `description` is optional
- `(messageKey, language)` must be unique
- `GET /api/localization-messages` returns a paginated response
- `GET /api/localization-messages/language/{language}` returns the messages for a single language ordered by `messageKey`

User rules:

- the current authenticated GitHub user is persisted on the first authenticated request
- `GET /api/users/me` returns the persisted application user, roles, and timestamps
- `PUT /api/users/me/preferred-language` stores an optional supported two-letter language code or clears it when blank or null
- localized error handling falls back to the persisted user preference when the request does not specify language through `lang`, `Accept-Language`, or cookie `language`

Seed data:

- `Clean Code`
- `Effective Java`
- `Best Practices`
- `Java`
- `Software Engineering`
- Localization error messages for current handler scenarios in `en`, `es`, `de`, `fr`, `pl`, `uk`, and `no`

Localization message key convention:

- Use `error.<domain>.<condition>` for application-managed error messages
- Keep keys stable even if the localized text changes
- Prefer extending the existing domain groups (`book`, `localization`, `request`, `data`, `server`) before adding new ones

Seeded localization message keys:

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

Do not add heavy bootstrap logic unless explicitly requested.

## Logging And Tracing

Current runtime behavior:

- request and error logs redact sensitive query parameters before they reach the logs
- error responses include `messageKey`, localized `message`, and resolved `language`
- `Accept-Language` drives browser-compatible error-message localization and `lang` query parameter overrides it
- cookie `language` is used as fallback when `lang` and supported `Accept-Language` values are absent
- authenticated user preference is used as the last localized-error fallback before English when no explicit request preference is present
- request-scoped language resolution is captured once and reused during localized error handling
- Caffeine-backed in-memory caches are used for localization lookups, localization language views, category lists, and the category assignment directory
- optional `org.springframework.web` DEBUG logging is available as a commented property in `application.properties`
- Hibernate SQL logging is enabled through `org.hibernate.SQL`
- Hibernate statistics are enabled through `hibernate.generate_statistics=true`
- successful create, update, delete, and seed writes are logged
- append-only audit records are persisted for book and localization-message create, update, and delete operations with the acting user snapshot when available
- actuator exposes readiness and liveness probe endpoints
- actuator exposes Prometheus metrics at `/actuator/prometheus`
- custom Micrometer metrics are published under the `technical.interview.demo.*` prefix for book, category, localization, user, and cache activity
- JPA fetch plans are controlled in repositories rather than through `FetchType.EAGER` on entities
- Flyway owns schema creation from SQL migrations and Hibernate validates the schema with `ddl-auto=validate`
- authenticated browser sessions are persisted through Spring Session JDBC
- `/actuator/health` and subpaths are skipped by the HTTP tracing logger

## AI Development Rules

- Preserve the demo nature of the project. Prefer direct code over abstraction.
- Keep package names under `team.jit.technicalinterviewdemo`.
- Use Lombok for routine boilerplate when it shortens the code clearly.
- Keep code compatible with Error Prone and the curated PMD ruleset.
- When returning `ResponseEntity`, assign the payload to a local variable first.
- Log every successful operation that changes database state.
- Keep non-trivial business logic in `@Service` beans. Service calls are logged and must keep sensitive values redacted.
- Prefer Spring MVC controllers and Spring Data repositories for new demo endpoints.
- Use PostgreSQL for runtime changes unless the user explicitly asks for something else.
- Keep local runtime simple through the included Docker Compose PostgreSQL setup and Testcontainers-backed tests.
- Avoid security, messaging, distributed systems, or extra libraries unless requested.
- Keep REST responses JSON-friendly.
- Add or update tests when API behavior changes.
- Do not remove the existing `hello` or `book` endpoints unless asked.
- After completing a roadmap phase from `TODO.md`, create an annotated git tag for the resulting app version on the phase-completion commit.
- Prefer semantic version tags such as `v0.5.0`; if the correct version bump is unclear, stop and ask before tagging.
- Version numbers must increase in `git log --first-parent` order; never backfill a lower semantic version onto a newer mainline commit or a higher semantic version onto an older one.
- Keep release tags aligned with `COMPLETED_TASKS.md` and the future `CHANGELOG.md` once it exists.

Human contribution workflow expectations live in `CONTRIBUTING.md`.

## Typical Change Flow

For `Book` API changes:

1. Update the request/entity model.
2. Update controller and service behavior.
3. Keep persistence simple through `BookRepository`.
4. Add or update MVC/integration tests.

## Quality Gates

Before finishing, run:

```powershell
.\gradlew.bat build
```

Notes:

- Export `JAVA_HOME` to JDK 25 in the same shell first.
- Docker Desktop must be running for `test` and `build` because Testcontainers provisions PostgreSQL and `build` now includes the Docker image build.
- `build` now covers Spotless, PMD, tests, Asciidoctor generation, boot jar creation, and the Docker image build.
- `test` and `build` also include the OpenAPI compatibility gate against `src/test/resources/openapi/approved-openapi.json`.
- Use focused commands such as `spotlessCheck`, `pmdMain`, `test`, or `asciidoctor` only when you intentionally want a narrower loop.

## Avoid

- overengineered service layers for trivial CRUD
- large amounts of manual boilerplate where Lombok keeps the demo simpler
- DTO mapping frameworks for small examples
- adding extra infrastructure beyond the existing PostgreSQL and Docker-based local setup without a clear requirement
- breaking the current test setup
- changing Java or Spring Boot versions unless requested

## Definition Of Done

A change is complete when:

- the design remains consistent with the simple demo architecture
- the application still starts
- `.\gradlew.bat build` passes
- new behavior is covered by tests when practical
