# Technical Interview Demo

Technical Interview Demo is a small, spec-driven Spring Boot application for interview exercises. It keeps the public `/api/**` surface intentionally compact while demonstrating session-based security, PostgreSQL-backed persistence, generated REST Docs, OpenAPI compatibility checks, and integration-test-driven development.

## Included Scope

- public read APIs for books, categories, and localization data
- authenticated account and session endpoints
- admin-only audit, operator-surface, and user-management APIs
- PostgreSQL runtime profiles, Flyway migrations, and Spring Session JDBC
- REST Docs, approved OpenAPI baseline checks, and Gatling benchmark coverage

## Project Map

- [SETUP.md](SETUP.md): local environment, prerequisites, and run commands
- [ROADMAP.md](ROADMAP.md): active and planned work only
- [WORKING_WITH_AI.md](WORKING_WITH_AI.md): how to use AI effectively in this repository
- `src/docs/asciidoc/`: published API documentation sources
- `src/test/resources/openapi/approved-openapi.json`: approved machine-readable API contract

## Notes

- The generated API docs are served by the application at `/docs`.
- Released history belongs in [CHANGELOG.md](CHANGELOG.md).
