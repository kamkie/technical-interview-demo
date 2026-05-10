# Technical Interview Demo

[![CI](https://github.com/kamkie/technical-interview-demo/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/kamkie/technical-interview-demo/actions/workflows/ci.yml)
[![Codecov](https://codecov.io/gh/kamkie/technical-interview-demo/graph/badge.svg)](https://codecov.io/gh/kamkie/technical-interview-demo)
[![Latest release](https://img.shields.io/github/v/release/kamkie/technical-interview-demo?sort=semver)](https://github.com/kamkie/technical-interview-demo/releases)
[![Java](https://img.shields.io/badge/java-25-blue)](build.gradle.kts)

Technical Interview Demo is a small, spec-driven Spring Boot application built for interview exercises. It keeps the public `/api/**` surface intentionally compact while demonstrating session-based security, PostgreSQL persistence with Flyway, Spring Session JDBC, generated REST Docs, OpenAPI compatibility checks, and integration-test-driven development.

> This repo is **spec-driven**. Truth lives in executable specs, approved OpenAPI, and published contract docs — in that order. See [AGENTS.md](AGENTS.md) (Spec Priority) and [docs/DEVELOPMENT_LIFECYCLE.md](docs/DEVELOPMENT_LIFECYCLE.md).

## Quick Start

1. Install prerequisites — see [SETUP.md](SETUP.md).
2. Start the app and database with the canonical wrapper:

   ```
   ./build.ps1 run
   ```

3. Open `http://localhost:8080/docs` for the generated API documentation.

Full local workflow (test loops, CI reproduction, troubleshooting): [docs/LOCAL_DEVELOPMENT.md](docs/LOCAL_DEVELOPMENT.md).

## Included Scope

- public read APIs for books, categories, and localization data
- authenticated account and session endpoints
- admin-only audit, operator-surface, and user-management APIs
- PostgreSQL runtime profiles, Flyway migrations, and Spring Session JDBC
- REST Docs, approved OpenAPI baseline checks, and Gatling benchmark coverage

See [docs/DESIGN.md](docs/DESIGN.md) for product intent and non-goals.

## Project Map

### Documentation

- [docs/README.md](docs/README.md) — human-facing documentation index
- [SETUP.md](SETUP.md) — environment setup for the dev container or local shell
- [docs/LOCAL_DEVELOPMENT.md](docs/LOCAL_DEVELOPMENT.md) — local run, test, CI reproduction, and troubleshooting workflows
- [docs/OPERATIONS.md](docs/OPERATIONS.md) — deployment, runtime, smoke, rollback, Kubernetes, Helm, monitoring, OAuth, and operations troubleshooting runbooks
- [CONTRIBUTING.md](CONTRIBUTING.md) — contributor workflow, validation expectations, and PR or release handoff
- [docs/DESIGN.md](docs/DESIGN.md) — product intent, non-goals, and contract direction
- [docs/DEVELOPMENT_LIFECYCLE.md](docs/DEVELOPMENT_LIFECYCLE.md) — human-facing lifecycle and artifact-routing summary
- [docs/WORKING_WITH_AI.md](docs/WORKING_WITH_AI.md) — human-facing AI collaboration guide
- [docs/LEARNINGS.md](docs/LEARNINGS.md) — curated durable repository lessons

### Spec-Driven Artifacts

- [ROADMAP.md](ROADMAP.md) — active and planned work, current release phase
- [docs/decisions/](docs/decisions/) — ADRs for durable architecture, workflow, contract-policy, security, documentation-ownership, or process decisions
- [docs/requirements/](docs/requirements/) — PRDs for broad or ambiguous user-facing product intent
- [docs/specs/](docs/specs/) — standalone specs for behavior or contract truth not already clear in executable specs or published docs
- [.agents/plans/](.agents/plans/) — active execution plans
- [CHANGELOG.md](CHANGELOG.md) — released history

### Contract And Source

- [src/docs/asciidoc/](src/docs/asciidoc/) — published REST Docs sources
- [src/test/resources/openapi/approved-openapi.json](src/test/resources/openapi/approved-openapi.json) — approved machine-readable API contract
- [docs/FRONTEND_AI_CONTRACT.md](docs/FRONTEND_AI_CONTRACT.md) — generated import-ready backend contract for a separate first-party frontend repository's AI agents
- [src/manualTests/http/](src/manualTests/http/) — reviewer-focused HTTP examples and manual-regression suites

### Infrastructure And Tooling

- `infra/` — checked-in Helm, Kubernetes, and monitoring assets
- `tooling/` — build-tool rules, static-analysis config, and scan suppressions
- `temp/` — throwaway agent and manual-regression artifacts (not part of the released contract)

### AI Guidance

- [AGENTS.md](AGENTS.md) — repository-local AI rules, spec priority, definition of done, and the full Documents Map
- [.agents/skills/README.md](.agents/skills/README.md) — repo-local skills catalog and the standard M0–M4 workflow chain
- See also [docs/WORKING_WITH_AI.md](docs/WORKING_WITH_AI.md) and [docs/FRONTEND_AI_CONTRACT.md](docs/FRONTEND_AI_CONTRACT.md) above.

## How To Contribute

1. Set up your environment ([SETUP.md](SETUP.md)) and learn the local loops ([docs/LOCAL_DEVELOPMENT.md](docs/LOCAL_DEVELOPMENT.md)).
2. Pick or open work in [ROADMAP.md](ROADMAP.md); when intent is unclear, add an ADR ([docs/decisions/](docs/decisions/)), PRD ([docs/requirements/](docs/requirements/)), or standalone spec ([docs/specs/](docs/specs/)) first — see [docs/DEVELOPMENT_LIFECYCLE.md](docs/DEVELOPMENT_LIFECYCLE.md) for routing.
3. Make spec-first changes (update governing tests, docs, or contract artifacts before or alongside implementation); validate per [CONTRIBUTING.md](CONTRIBUTING.md).
4. Need AI help with planning, implementation, verification, or release prep? See [docs/WORKING_WITH_AI.md](docs/WORKING_WITH_AI.md).
5. Deploying or operating a release? Use [docs/OPERATIONS.md](docs/OPERATIONS.md).

## Notes

- `./build.ps1` is the canonical wrapper for build, test, and run commands; see [docs/LOCAL_DEVELOPMENT.md](docs/LOCAL_DEVELOPMENT.md) for the full command catalog.
- The generated API docs are served by the application at `/docs`.
- Released history belongs in [CHANGELOG.md](CHANGELOG.md).
