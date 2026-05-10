# Technical Interview Demo

[![CI](https://github.com/kamkie/technical-interview-demo/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/kamkie/technical-interview-demo/actions/workflows/ci.yml)
[![Codecov](https://codecov.io/gh/kamkie/technical-interview-demo/graph/badge.svg)](https://codecov.io/gh/kamkie/technical-interview-demo)

Technical Interview Demo is a small, spec-driven Spring Boot application for interview exercises. It keeps the public `/api/**` surface intentionally compact while demonstrating session-based security, PostgreSQL-backed persistence, generated REST Docs, OpenAPI compatibility checks, and integration-test-driven development.

## Current Release Phase

- [ROADMAP.md](ROADMAP.md) defines the current project release phase.

## Included Scope

- public read APIs for books, categories, and localization data
- authenticated account and session endpoints
- admin-only audit, operator-surface, and user-management APIs
- PostgreSQL runtime profiles, Flyway migrations, and Spring Session JDBC
- REST Docs, approved OpenAPI baseline checks, and Gatling benchmark coverage

See [docs/DESIGN.md](docs/DESIGN.md) for product intent and non-goals.

## Project Map

- [SETUP.md](SETUP.md): local environment, prerequisites, and run commands
- [CONTRIBUTING.md](CONTRIBUTING.md): contributor workflow, validation expectations, and PR or release handoff
- [ROADMAP.md](ROADMAP.md): active and planned work only
- [WORKING_WITH_AI.md](WORKING_WITH_AI.md): how to use AI effectively in this repository
- [docs/decisions/](docs/decisions/): optional ADRs for durable architecture, workflow, contract-policy, security, documentation-ownership, or process decisions
- [docs/requirements/](docs/requirements/): optional PRDs for broad or ambiguous user-facing product intent
- [docs/specs/](docs/specs/): optional standalone specs for behavior or contract truth not already clear in executable specs or published docs
- [docs/FRONTEND_AI_CONTRACT.md](docs/FRONTEND_AI_CONTRACT.md): generated import-ready backend contract, external skill references, OpenAPI source snapshot, and integration rules for AI agents building a separate first-party frontend
- `infra/`: checked-in Helm, Kubernetes, and monitoring assets
- `tooling/`: build-tool rules, static-analysis config, and scan suppressions
- `src/docs/asciidoc/`: published API documentation sources
- `src/test/resources/openapi/approved-openapi.json`: approved machine-readable API contract
- `temp/`: throwaway agent and manual-regression artifacts (not part of the released contract)

## How To Contribute

- start with [SETUP.md](SETUP.md) to get Java 25, Docker, and local commands working
- use [CONTRIBUTING.md](CONTRIBUTING.md) for branch, validation, PR, and release expectations
- make changes spec-first: update the governing tests, docs, or contract artifacts before or alongside implementation
- use [WORKING_WITH_AI.md](WORKING_WITH_AI.md) when you want AI help with planning, implementation, verification, or release preparation
- open an ADR ([docs/decisions/](docs/decisions/)) for durable architecture, workflow, contract, security, or process decisions; a PRD ([docs/requirements/](docs/requirements/)) for ambiguous user-facing product intent; or a standalone spec ([docs/specs/](docs/specs/)) when behavior is not already covered by executable specs — see [WORKING_WITH_AI.md](WORKING_WITH_AI.md) and [ROADMAP.md](ROADMAP.md) for lifecycle routing

## AI Guidance Files

Use these entry points when you want to work with AI in this repository or understand what the AI is expected to follow:

- [WORKING_WITH_AI.md](WORKING_WITH_AI.md): human-facing guide for using AI across the lifecycle
- [AGENTS.md](AGENTS.md): repository-local AI rules, spec priority, definition of done, and the full Documents Map
- [.agents/skills/README.md](.agents/skills/README.md): repo-local skills catalog and the standard M0–M4 workflow chain
- [docs/FRONTEND_AI_CONTRACT.md](docs/FRONTEND_AI_CONTRACT.md): generated import-ready backend contract for a separate first-party frontend repository's AI agents

See [AGENTS.md](AGENTS.md) Documents Map for the full set of `.agents/references/*` guides, plan files, and archived material.

## Notes

- The generated API docs are served by the application at `/docs`.
- Released history belongs in [CHANGELOG.md](CHANGELOG.md).
