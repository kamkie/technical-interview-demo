# AI Project Instructions

`README.md` is the human-facing counterpart of this file. Keep both files aligned when project setup, API behavior, formatter usage, logging/tracing behavior, or quality gates change.

## Project Snapshot

Small Spring Boot demo app for technical interview exercises.

Current scope:

- `GET /docs` redirects to generated API documentation
- `GET /hello` returns `Hello World!`
- CRUD-style `Book` API under `/api/books` with pagination, filtering, and optimistic locking on updates
- actuator endpoints for `health`, `info`, liveness/readiness probes, and Prometheus metrics
- H2 in-memory database
- startup seed data
- MVC/integration-style tests
- request tracing and structured logging
- **Development Container (dev container) for VS Code with zero-friction setup**

Primary goal: keep the codebase small, readable, and easy to reason about.

## Stack

- Java 25
- Spring Boot 4.0.6
- Spring Web MVC
- Spring Data JPA
- H2
- Gradle Wrapper
- Lombok
- Spring AOP
- Micrometer tracing with OpenTelemetry
- Spring REST Docs
- Asciidoctor
- Flyway
- Qodana
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
.\gradlew.bat bootRun
.\gradlew.bat test
.\gradlew.bat asciidoctor
.\gradlew.bat qodanaScan
.\gradlew.bat dockerBuild
.\gradlew.bat dockerBuild -PdockerImageName=my-app:dev
docker build -t technical-interview-demo .
docker run --rm -p 8080:8080 technical-interview-demo
```

Developer onboarding lives in `SETUP.md`. An optional shell/env template is provided in `.env.example`.

## Spring Profiles

The application uses Spring profiles for environment-specific configuration.

**Available Profiles:**
- `local` (default) - Development with H2 in-memory database, debug logging
- `prod` - Production with PostgreSQL (after Phase 1.1), minimal logging
- `test` - Testing with isolated H2 database, auto-activated by test framework

**Profile Activation:**
- Default: `spring.profiles.active=local` in `application.properties`
- Override: `--spring.profiles.active=prod` on command line
- Docker: Automatically uses `prod` profile (set in `Dockerfile`)
- Tests: Automatically uses `test` profile (via `@SpringBootTest` annotations)

**Configuration Files:**
- `src/main/resources/application.properties` - common settings for all profiles
- `src/main/resources/application-local.properties` - local development config
- `src/main/resources/application-prod.properties` - production config (Future: PostgreSQL)
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
- HTML documentation in `build/docs/asciidoc/index.html`

Packaging and runtime behavior:

- `bootJar` bundles the generated HTML documentation into the runnable jar
- the running application serves the documentation at `GET /docs`
- the generated docs include example success and error responses captured from tests
- the container image includes a health check against `GET /actuator/health/readiness`
- the container image uses Microsoft Build of OpenJDK and starts the app with `jaz`

## Project Map

- `CONTRIBUTING.md`: contribution workflow, review expectations, and quality gates
- `SETUP.md`: developer onboarding and troubleshooting guide
- `.env.example`: optional environment variable template for local shells or container tooling
- `build.gradle.kts`: build configuration and dependencies
- `config/pmd/pmd-ruleset.xml`: curated PMD rules
- `src/main/java/team/jit/technicalinterviewdemo/TechnicalInterviewDemoApplication.java`: app entry
- `src/main/java/team/jit/technicalinterviewdemo/HelloController.java`: hello endpoint
- `src/main/java/team/jit/technicalinterviewdemo/book/`: book entity, requests, repository, service, controller, seed data
- `src/main/java/team/jit/technicalinterviewdemo/api/`: exception handling and custom exceptions
- `src/main/java/team/jit/technicalinterviewdemo/docs/`: documentation endpoint and resource mapping
- `src/main/java/team/jit/technicalinterviewdemo/logging/`: HTTP tracing/logging and service-call logging
- `src/main/resources/db/migration/`: Flyway SQL migrations
- `src/docs/asciidoc/index.adoc`: assembled API documentation source
- `src/main/resources/application.properties`: runtime configuration
- `src/test/java/team/jit/technicalinterviewdemo/`: application, API, tracing, and documentation tests

## API Contract

Endpoints:

- `GET /docs`
- `GET /hello`
- `GET /api/books?page=0&size=20&sort=id,asc&title=clean&yearFrom=2000&yearTo=2020`
- `GET /api/books/{id}`
- `POST /api/books`
- `PUT /api/books/{id}`
- `DELETE /api/books/{id}`
- `GET /actuator/info`
- `GET /actuator/health`
- `GET /actuator/health/liveness`
- `GET /actuator/health/readiness`
- `GET /actuator/prometheus`

Book rules:

- `title` is required
- `author` is required
- `isbn` is required on create
- `publicationYear` is required
- `isbn` must be unique
- `isbn` is immutable after creation and is not updated by `PUT /api/books/{id}`
- `GET /api/books` returns a paginated response
- `GET /api/books` supports optional `title`, `author`, and `isbn` substring filters
- `GET /api/books` supports `year` for exact publication year matching
- `GET /api/books` supports `yearFrom` and `yearTo` for inclusive publication year ranges
- `GET /api/books` supports repeated `sort` parameters such as `sort=title,asc&sort=year,desc`
- `year` cannot be combined with `yearFrom` or `yearTo`
- `version` is returned for each book and is required on `PUT /api/books/{id}` for optimistic locking

Seed data:

- `Clean Code`
- `Effective Java`

Do not add heavy bootstrap logic unless explicitly requested.

## Logging And Tracing

Current runtime behavior:

- request and error logs redact sensitive query parameters before they reach the logs
- optional `org.springframework.web` DEBUG logging is available as a commented property in `application.properties`
- Hibernate SQL logging is enabled through `org.hibernate.SQL`
- Hibernate statistics are enabled through `hibernate.generate_statistics=true`
- successful create, update, delete, and seed writes are logged
- actuator exposes readiness and liveness probe endpoints
- actuator exposes Prometheus metrics at `/actuator/prometheus`
- Flyway owns schema creation from SQL migrations and Hibernate validates the schema with `ddl-auto=validate`
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
- Use H2/in-memory storage unless external infrastructure is explicitly required.
- Avoid security, messaging, distributed systems, or extra libraries unless requested.
- Keep REST responses JSON-friendly.
- Add or update tests when API behavior changes.
- Do not remove the existing `hello` or `book` endpoints unless asked.

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
.\gradlew.bat spotlessCheck
.\gradlew.bat --no-problems-report pmdMain
.\gradlew.bat --no-problems-report test
.\gradlew.bat asciidoctor
```

Optional additional static analysis:

```powershell
.\gradlew.bat qodanaScan
```

Notes:

- Export `JAVA_HOME` to JDK 25 in the same shell first.
- Error Prone runs during Java compilation.
- PMD also runs as part of `check` and `build`.

## Avoid

- overengineered service layers for trivial CRUD
- large amounts of manual boilerplate where Lombok keeps the demo simpler
- DTO mapping frameworks for small examples
- replacing H2 with an external database without a clear requirement
- breaking the current test setup
- changing Java or Spring Boot versions unless requested

## Definition Of Done

A change is complete when:

- the design remains consistent with the simple demo architecture
- the application still starts
- `spotlessCheck` passes
- `pmdMain` passes
- tests pass
- new behavior is covered by tests when practical
