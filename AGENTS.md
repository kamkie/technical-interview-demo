# AI Project Instructions

`README.md` is the human-facing counterpart of this file. Keep both files aligned when project setup, API behavior, formatter usage, logging/tracing behavior, or quality gates change.

## Project Summary

This repository is a small Spring Boot demo application built with Gradle Kotlin DSL.

The current demo includes:

- `GET /hello` returning `Hello World!`
- A simple REST API for `Book` under `/api/books`
- In-memory H2 database configuration
- Seed data loaded at startup
- Basic integration-style MVC tests

Primary goal: keep the project small, readable, and suitable for technical interview demos.

## Tech Stack

- Java 25 toolchain
- Spring Boot 4.0.6
- Spring Web MVC
- Spring Data JPA
- H2 in-memory database
- Gradle Wrapper
- JUnit 5
- Error Prone
- PMD

## Important Environment Detail

The machine default `JAVA_HOME` may point to Java 11, which is too old for this build.

Use a compatible JDK before running Gradle commands. Example for PowerShell:

```powershell
$env:JAVA_HOME='C:\Users\kamki\.jdks\azul-25.0.3'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
```

Then run commands such as:

```powershell
.\gradlew.bat test
.\gradlew.bat bootRun
```

The repository also includes a `Dockerfile` for containerized builds and local runs:

```powershell
docker build -t technical-interview-demo .
docker run --rm -p 8080:8080 technical-interview-demo
```

For formatting, the build uses Spotless. Java formatting is delegated to IntelliJ IDEA's formatter so the result stays as close as practical to IntelliJ defaults.

If IntelliJ is not configured, Gradle build and test tasks must still pass. In that case, Spotless skips Java formatting tasks instead of failing the build.

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

## Project Structure

- `build.gradle.kts`: Gradle build and dependencies
- `config/pmd/pmd-ruleset.xml`: curated PMD ruleset
- `src/main/java/team/jit/technicalinterviewdemo/TechnicalInterviewDemoApplication.java`: app entry point
- `src/main/java/team/jit/technicalinterviewdemo/HelloController.java`: hello-world endpoint
- `src/main/java/team/jit/technicalinterviewdemo/book/`: `Book` domain and REST API
- `src/main/resources/application.properties`: H2 and JPA configuration
- `src/test/java/team/jit/technicalinterviewdemo/`: application and API tests

## API Overview

### Hello endpoint

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

Example update payload:

```json
{
  "title": "Spring in Action, Second Edition",
  "author": "Craig Walls",
  "publicationYear": 2026
}
```

- `isbn` is immutable after creation and is not updated by `PUT /api/books/{id}`

## Seed Data

On startup, the app inserts sample books if the table is empty:

- `Clean Code`
- `Effective Java`

Do not add heavy bootstrap logic unless explicitly requested.

## Logging And Tracing

The current runtime configuration includes:

- request and error logging that redacts sensitive query parameters before they reach the logs
- optional `org.springframework.web` DEBUG logging as a commented property in `application.properties`
- Hibernate SQL statement logging through `org.hibernate.SQL`
- Hibernate statistics enabled through `hibernate.generate_statistics=true`
- explicit logs for successful database-changing operations such as create, update, delete, and seed writes

## Development Guidelines For AI

- Preserve the demo nature of the project. Prefer simple code over abstractions.
- Keep package naming under `team.jit.technicalinterviewdemo`.
- Use Lombok for routine Java boilerplate such as getters, setters, constructors, and builders when it keeps the code shorter and clearer.
- Keep `.editorconfig` aligned with the intended IntelliJ formatting profile. That file is part of the formatter contract for this repository.
- Keep the code compatible with Error Prone checks that run during Java compilation.
- Keep the code compatible with the curated PMD ruleset in `config/pmd/pmd-ruleset.xml`.
- When returning `ResponseEntity`, assign the response payload to a local variable first so controller breakpoints can inspect it easily before returning.
- Log every operation that changes database state. For CRUD-style endpoints, emit at least one log entry for each successful create, update, delete, or seed write.
- Keep non-trivial business logic in `@Service` beans. Service method calls are logged through Spring AOP, and logged parameters must redact common sensitive values.
- Prefer Spring MVC controllers and Spring Data repositories for new demo endpoints.
- Use H2/in-memory storage unless the task explicitly requires external infrastructure.
- Avoid introducing security, messaging, Docker, or distributed components unless asked.
- Avoid adding unnecessary libraries when Spring Boot already provides the needed feature.
- Keep responses JSON-friendly for REST endpoints.
- When adding new API behavior, add or update tests.
- Do not remove the existing `hello` or `book` demo endpoints unless asked.

## Testing Expectations

Before finishing changes, run:

```powershell
.\gradlew.bat spotlessCheck
.\gradlew.bat --no-problems-report pmdMain
.\gradlew.bat --no-problems-report test
```

If tests require Java setup first, export `JAVA_HOME` to a compatible JDK in the same shell session.

Error Prone runs as part of Java compilation, so `test` and `build` also execute static analysis for Java sources.
PMD runs as part of `check` and `build`. Use `pmdMain` for the main application source set when you want a focused PMD run.

## Common Changes

If extending the `Book` API, prefer this sequence:

1. Update the request or entity model.
2. Update controller behavior.
3. Keep persistence simple through `BookRepository`.
4. Add or update MVC/integration tests.

## Things To Avoid

- Overengineering service layers for trivial CRUD
- Writing large amounts of manual Java boilerplate when Lombok would keep the demo simpler
- Adding DTO mapping frameworks for small examples
- Replacing H2 with an external database without a clear requirement
- Breaking the existing test setup
- Changing Java or Spring Boot versions unless requested

## Definition Of Done

A change is considered complete when:

- The code is consistent with the current simple demo architecture
- The application still starts
- `spotlessCheck` passes
- `pmdMain` passes
- Tests pass
- Any new endpoint or behavior is covered by tests when practical

