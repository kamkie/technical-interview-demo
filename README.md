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

- `GET /hello`
- `GET /api/books`
- H2 console at `/h2-console`

## Docker

Build the image with:

```powershell
docker build -t technical-interview-demo .
```

Run the container with:

```powershell
docker run --rm -p 8080:8080 technical-interview-demo
```

The Docker image builds the Spring Boot fat jar in a separate build stage and runs it on Java 25.

## Project Structure

- `build.gradle.kts`: Gradle build and dependencies
- `src/main/java/team/jit/technicalinterviewdemo/TechnicalInterviewDemoApplication.java`: app entry point
- `src/main/java/team/jit/technicalinterviewdemo/HelloController.java`: hello-world endpoint
- `src/main/java/team/jit/technicalinterviewdemo/book/`: `Book` domain, service, repository, and REST API
- `src/main/java/team/jit/technicalinterviewdemo/api/`: API exception handling and custom exceptions
- `src/main/java/team/jit/technicalinterviewdemo/logging/`: HTTP tracing/logging and service-call logging
- `src/main/resources/application.properties`: runtime configuration
- `src/test/java/team/jit/technicalinterviewdemo/`: application and API tests

## API

### Hello Endpoint

- `GET /hello`

Response:

```text
Hello World!
```

### Book API

- `GET /api/books`
- `GET /api/books/{id}`
- `POST /api/books`
- `PUT /api/books/{id}`
- `DELETE /api/books/{id}`

Example create payload:

```json
{
  "title": "Spring in Action",
  "author": "Craig Walls",
  "isbn": "9781617297571",
  "publicationYear": 2022
}
```

Validation rules:

- `title` is required
- `author` is required
- `isbn` is required
- `publicationYear` is required
- `isbn` must be unique across books

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
- missing books return `404 Not Found`
- unexpected server errors return a generic `500 Internal Server Error`

Client error responses do not expose stack traces or internal implementation details.

## Logging And Tracing

The application includes:

- OpenTelemetry-compatible tracing through Micrometer
- `traceId` and `spanId` in console logs
- `traceparent` response header on HTTP requests when tracing is active
- request start and response completion logs for HTTP traffic
- service-layer AOP logging with method parameters and execution time
- redaction of common sensitive parameters and fields
- explicit logs for successful database-changing operations such as create, update, delete, and seed writes

The HTTP tracing logger intentionally skips `/actuator/health` and its subpaths.

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
.\gradlew.bat test
```

If tests require Java setup first, export `JAVA_HOME` to a compatible JDK in the same shell session.

## Definition Of Done

A change is considered complete when:

- the code remains consistent with the current simple demo architecture
- the application still starts
- `spotlessCheck` passes
- tests pass
- new endpoint or behavior changes are covered by tests when practical

