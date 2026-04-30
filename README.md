# Technical Interview Demo

`AGENTS.md` is the AI-facing counterpart of this file. Keep both files aligned when project setup, API behavior, formatter usage, logging/tracing behavior, or quality gates change.

## Overview

This repository contains a small Spring Boot demo application built with Gradle Kotlin DSL.

The demo currently includes:

- `GET /hello` returning `Hello World!`
- A REST API for `Book` under `/api/books`
- In-memory H2 database configuration
- Seed data loaded at startup
- MVC and integration-style tests
- Request tracing and structured application logging

Primary goal: keep the project small, readable, and suitable for technical interview demos.

## Tech Stack

- Java 25 toolchain
- Spring Boot 4.0.6
- Spring Web MVC
- Spring Data JPA
- H2 in-memory database
- Gradle Wrapper
- JUnit 5
- Lombok
- Spring AOP
- Micrometer tracing with OpenTelemetry
- Spring REST Docs
- Asciidoctor
- Flyway
- Qodana
- Error Prone
- PMD

## Requirements

The machine default `JAVA_HOME` may point to Java 11, which is too old for this build.

Use a compatible JDK before running Gradle commands. Example for PowerShell:

```powershell
$env:JAVA_HOME='C:\Users\kamki\.jdks\azul-25.0.3'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
```

## Run The Application

Start the application with:

```powershell
.\gradlew.bat bootRun
```

Useful local endpoints:

- `GET /docs`
- `GET /hello`
- `GET /api/books`
- `GET /actuator/info`
- `GET /actuator/health`
- `GET /actuator/health/liveness`
- `GET /actuator/health/readiness`
- `GET /actuator/prometheus`
- H2 console at `/h2-console`

## Docker

Build the image with Gradle:

```powershell
.\gradlew.bat dockerBuild
.\gradlew.bat dockerBuild -PdockerImageName=my-app:dev
```

The default image name is `technical-interview-demo`.

You can also build the image directly with Docker:

```powershell
docker build -t technical-interview-demo .
```

Run the container with:

```powershell
docker run --rm -p 8080:8080 technical-interview-demo
```

The Docker image builds the Spring Boot fat jar in a separate build stage and runs it on Java 25.
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
- HTML docs: `build/docs/asciidoc/index.html`

Packaging and runtime behavior:

- `bootJar` bundles the generated HTML documentation into the runnable jar
- the running application serves the documentation at `GET /docs`
- the generated docs include example success and error responses captured from tests

Qodana static analysis is available through Gradle:

```powershell
.\gradlew.bat qodanaScan
```

## Project Structure

- `build.gradle.kts`: Gradle build and dependencies
- `config/pmd/pmd-ruleset.xml`: curated PMD ruleset
- `src/main/java/team/jit/technicalinterviewdemo/TechnicalInterviewDemoApplication.java`: app entry point
- `src/main/java/team/jit/technicalinterviewdemo/HelloController.java`: hello-world endpoint
- `src/main/java/team/jit/technicalinterviewdemo/book/`: `Book` domain, service, repository, and REST API
- `src/main/java/team/jit/technicalinterviewdemo/api/`: API exception handling and custom exceptions
- `src/main/java/team/jit/technicalinterviewdemo/docs/`: documentation endpoint and resource mapping
- `src/main/java/team/jit/technicalinterviewdemo/logging/`: HTTP tracing/logging and service-call logging
- `src/main/resources/db/migration/`: Flyway SQL migrations
- `src/docs/asciidoc/index.adoc`: assembled API documentation source
- `src/main/resources/application.properties`: runtime configuration
- `src/test/java/team/jit/technicalinterviewdemo/`: application and API tests

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

- `GET /api/books?page=0&size=20&sort=id,asc`
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
  "publicationYear": 2022
}
```

Example update payload:

```json
{
  "title": "Spring in Action, Second Edition",
  "author": "Craig Walls",
  "version": 0,
  "publicationYear": 2026
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

Actuator endpoints:

- `GET /actuator/info`
- `GET /actuator/health`
- `GET /actuator/health/liveness`
- `GET /actuator/health/readiness`
- `GET /actuator/prometheus`

## Seed Data

On startup, the app inserts sample books if the table is empty:

- `Clean Code`
- `Effective Java`

## Error Handling

The API uses `ProblemDetail` responses through centralized `@RestControllerAdvice`.

Current behavior:

- expected client errors return sanitized messages
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
- request and error logging redact sensitive query parameters before they reach the logs
- request start and response completion logs for HTTP traffic
- service-layer AOP logging with method parameters and execution time
- redaction of common sensitive parameters and fields
- optional `org.springframework.web` DEBUG logging is available as a commented property in `application.properties`
- Hibernate SQL statement logging through `org.hibernate.SQL`
- Hibernate statistics enabled through `hibernate.generate_statistics=true`
- explicit logs for successful database-changing operations such as create, update, delete, and seed writes
- readiness and liveness health probes through actuator
- Prometheus metrics exposed through `/actuator/prometheus`

The HTTP tracing logger intentionally skips `/actuator/health` and its subpaths.

## Database Schema

Flyway manages the schema from SQL migrations under `src/main/resources/db/migration`.

Hibernate is configured with `spring.jpa.hibernate.ddl-auto=validate`, so the application validates the mapped schema instead of creating or updating it automatically.

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
- Use H2/in-memory storage unless the task explicitly requires external infrastructure.
- Avoid adding unnecessary libraries when Spring Boot already provides the needed feature.
- Keep REST responses JSON-friendly.
- Add or update tests when API behavior changes.
- Do not remove the existing `hello` or `book` demo endpoints unless intentionally changing the demo scope.

## Quality Checks

Before finishing changes, run:

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

If tests require Java setup first, export `JAVA_HOME` to a compatible JDK in the same shell session.

Error Prone runs as part of Java compilation, so `test` and `build` also execute static analysis for Java sources.
PMD runs as part of `check` and `build`. Use `pmdMain` for the main application source set when you want a focused PMD run.

## Definition Of Done

A change is considered complete when:

- the code remains consistent with the current simple demo architecture
- the application still starts
- `spotlessCheck` passes
- `pmdMain` passes
- tests pass
- new endpoint or behavior changes are covered by tests when practical
