# Technical Interview Demo

Technical Interview Demo is a small, spec-driven Spring Boot application for interview exercises. It keeps the public `/api/**` surface intentionally compact while demonstrating session-based security, PostgreSQL-backed persistence, generated REST Docs, OpenAPI compatibility checks, and integration-test-driven development.

## Current Release Phase

- Current status: pre-`2.0` prerelease on the `v2.0.0-M*` milestone line.
- Intentional breaking changes are still allowed while finishing the `2.0` contract before `v2.0.0-RC1`.
- The current pre-RC contract workstream includes standardizing app-owned persisted API timestamps on UTC instants with a trailing `Z`.
- The next phase is `v2.0.0-RC1`, which serves as the `2.0` contract-freeze milestone, matching [ROADMAP.md](ROADMAP.md).
- After the stable `v2.0.0` release, the `2.x` line is treated as stable and breaking changes are no longer allowed as routine maintenance work.

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
- `ai/ARCHITECTURE.md`: codebase map, package boundaries, and current API shape
- `ai/BUSINESS_MODULES.md`: feature ownership and business-module boundaries
- `ai/CODE_STYLE.md`: repo-local code style and change-shaping guidance
- `ai/DESIGN.md`: product intent, tradeoffs, and non-goals
- `ai/DOCUMENTATION.md`: documentation ownership and when to update which docs
- `ai/EXECUTION.md`: how AI should implement an approved plan or milestone
- `ai/LEARNINGS.md`: durable engineering lessons worth carrying through refactors
- `ai/PLAN.md`: how to create or revise execution plans
- `ai/PROMPTS.md`: reusable prompt starters that match this repository's workflow
- `ai/skills/`: repo-local skills for repetitive planning and validation entry workflows
- `ai/REVIEWS.md`: code-review and security-review guidance
- `ai/RELEASES.md`: release-preparation and release workflow guidance
- `ai/TESTING.md`: validation rules and change-sensitive checks
- `ai/WORKFLOW.md`: single-branch, shared-plan, and parallel execution guidance
- `ai/PLAN_*.md` when present: task-specific execution plans for active work
- `ai/archive/`: archived plan files for completed or released work

## Notes

- The generated API docs are served by the application at `/docs`.
- Released history belongs in [CHANGELOG.md](CHANGELOG.md).
