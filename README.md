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
- [ai/ARCHITECTURE.md](ai/ARCHITECTURE.md): compact codebase map, package boundaries, feature ownership, and current API shape
- [ai/CODE_STYLE.md](ai/CODE_STYLE.md): repo-local code style and change-shaping guidance
- [ai/DESIGN.md](ai/DESIGN.md): product intent, tradeoffs, and non-goals
- [ai/DOCUMENTATION.md](ai/DOCUMENTATION.md): documentation ownership and when to update which docs
- [ai/ENVIRONMENT_QUICK_REF.md](ai/ENVIRONMENT_QUICK_REF.md): AI shortcut for local Gradle wrapper commands
- [ai/PLAN_EXECUTION.md](ai/PLAN_EXECUTION.md): how AI should execute a whole active plan across milestones
- [ai/EXECUTION.md](ai/EXECUTION.md): how AI should execute ad hoc tasks or one plan milestone
- [ai/LEARNINGS.md](ai/LEARNINGS.md): durable engineering lessons worth carrying through refactors
- [ai/PLANNING.md](ai/PLANNING.md): compact guide for creating or revising execution plans
- [ai/TASK_LIBRARY.md](ai/TASK_LIBRARY.md): reusable task-title index backed by machine-readable task metadata and loader
- `ai/skills/`: repo-local skills
- [ai/REVIEWS.md](ai/REVIEWS.md): code-review and security-review guidance
- [ai/RELEASES.md](ai/RELEASES.md): release-preparation and release workflow guidance
- [ai/TESTING.md](ai/TESTING.md): validation rules and change-sensitive checks
- [ai/WORKFLOW.md](ai/WORKFLOW.md): branch, worktree, delegation, worker-log, integration, and remote-handoff mechanics
- `ai/plans/active/PLAN_*.md`: task-specific execution plans for active work
- `ai/task-library/`, `ai/references/`, and `ai/templates/`: machine-readable task metadata, on-demand raw task bodies, detailed references, and templates
- `ai/archive/`: archived plan files for completed or released work

## Notes

- The generated API docs are served by the application at `/docs`.
- Released history belongs in [CHANGELOG.md](CHANGELOG.md).
