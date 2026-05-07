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

## Project Map

- [SETUP.md](SETUP.md): local environment, prerequisites, and run commands
- [CONTRIBUTING.md](CONTRIBUTING.md): contributor workflow, validation expectations, and PR or release handoff
- [ROADMAP.md](ROADMAP.md): active and planned work only
- [WORKING_WITH_AI.md](WORKING_WITH_AI.md): how to use AI effectively in this repository
- `infra/`: checked-in Helm, Kubernetes, and monitoring assets
- `tooling/`: build-tool rules, static-analysis config, and scan suppressions
- `src/docs/asciidoc/`: published API documentation sources
- `src/test/resources/openapi/approved-openapi.json`: approved machine-readable API contract

## How To Contribute To This Repo

- start with [SETUP.md](SETUP.md) to get Java 25, Docker, and local commands working
- use [CONTRIBUTING.md](CONTRIBUTING.md) for branch, validation, PR, and release expectations
- make changes spec-first: update the governing tests, docs, or contract artifacts before or alongside implementation
- use [WORKING_WITH_AI.md](WORKING_WITH_AI.md) when you want AI help with planning, implementation, verification, or release preparation

## AI Guidance Files

Use these files when you want to work with AI in this repository or understand what the AI is expected to follow:

- [WORKING_WITH_AI.md](WORKING_WITH_AI.md): human-facing guide for using AI across discovery, planning, implementation, verification, and release
- [AGENTS.md](AGENTS.md): repository-local AI rules, spec priority, required contract updates, and definition of done
- [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md): compact codebase map, package boundaries, feature ownership, and current API shape
- [.agents/references/code-style.md](.agents/references/code-style.md): repo-local code style and change-shaping guidance
- [docs/DESIGN.md](docs/DESIGN.md): product intent, tradeoffs, and non-goals
- [.agents/references/documentation.md](.agents/references/documentation.md): documentation ownership and when to update which docs
- [.agents/references/environment-quick-ref.md](.agents/references/environment-quick-ref.md): AI shortcut for local Gradle wrapper commands
- [.agents/references/plan-execution.md](.agents/references/plan-execution.md): how AI should execute a whole active plan across milestones
- [.agents/references/execution.md](.agents/references/execution.md): how AI should execute ad hoc tasks or one plan milestone
- [.agents/references/LEARNINGS.md](.agents/references/LEARNINGS.md): durable engineering lessons worth carrying through refactors
- [.agents/references/planning.md](.agents/references/planning.md): compact guide for creating or revising execution plans
- [.agents/skills/repo-task/](.agents/skills/repo-task/): reusable task starters with task titles, placeholders, and task text
- `.agents/skills/`: repo-local skills
- [.agents/references/reviews.md](.agents/references/reviews.md): code-review and security-review guidance
- [.agents/references/releases.md](.agents/references/releases.md): release-preparation and release workflow guidance
- [.agents/references/testing.md](.agents/references/testing.md): validation rules and change-sensitive checks
- [.agents/references/workflow.md](.agents/references/workflow.md): branch, worktree, delegation, worker-log, integration, and remote-handoff mechanics
- `.agents/plans/PLAN_*.md`: task-specific execution plans for active work
- `.agents/references/` and `.agents/templates/`: on-demand detailed references and templates
- `.agents/reports/`: generated AI analysis reports only when a task intentionally creates a tracked report
- `.agents/archive/`: archived plan files for completed or released work

## Notes

- The generated API docs are served by the application at `/docs`.
- Released history belongs in [CHANGELOG.md](CHANGELOG.md).
