# 0006 Split Human Documentation And AI Workflow Guides

## Status

Proposed on 2026-05-10

## Date

2026-05-10

## Context

The top-level documentation now has good entry-point coverage in `README.md`, but the detailed human-facing guides do not have a clean enough split.

The current shape creates several discoverability and ownership problems:

- `SETUP.md` starts as a local environment guide but also carries deployment contract, release-artifact verification, Docker image operation, container smoke, post-deploy smoke, healthy runtime, upgrade and rollback, Kubernetes, Helm, monitoring, OAuth, and troubleshooting material.
- `CONTRIBUTING.md` owns contributor workflow and maintainer expectations, but it also points at lifecycle and AI execution details that are easier to understand when they have a dedicated human-facing lifecycle entry point.
- `WORKING_WITH_AI.md` is the human guide for AI collaboration, but it currently carries lifecycle, ADR, PRD, standalone spec, plan, workflow mode, skill, and prompt guidance in one file.
- `docs/DESIGN.md`, ADRs, PRDs, standalone specs, and execution plans are already present, but their relationship is mostly discoverable through `README.md`, `WORKING_WITH_AI.md`, `ROADMAP.md`, and `.agents/references/*` rather than through a concise human-facing docs index.
- `.agents/references/application-lifecycle.md` and `.agents/references/planning.md` define the detailed AI lifecycle and planning workflow. Those files should remain AI owner guides, not become the first human entry point for understanding the application development lifecycle.

The repository uses `PRD` for product requirements documents.

## Decision

Adopt a clearer human-facing documentation split while keeping `README.md` as the concise project entry point.

Target document roles:

- `README.md`: short project summary, supported scope, contract map, and top-level links.
- `docs/README.md`: human-facing documentation index for design, lifecycle, ADRs, PRDs, standalone specs, roadmap, operations, and frontend contract material.
- `docs/DEVELOPMENT_LIFECYCLE.md`: human-facing explanation of the repository lifecycle from Conceptualization through Maintenance, including when to use an ADR, PRD, standalone spec, roadmap entry, or `.agents/plans/PLAN_*.md`.
- `docs/DESIGN.md`: product and contract intent, non-goals, public contract direction, security and deployment direction, and roadmap direction.
- `SETUP.md`: local developer setup only: prerequisites, quick start, environment variables, IDE setup, local database, running the app, running tests, local CI reproduction, and local troubleshooting.
- `docs/OPERATIONS.md` or `OPERATIONS.md`: deployment and operations runbooks: deployment contract, release-artifact verification, image build and smoke, post-deploy smoke, healthy runtime expectations, upgrade and rollback, Kubernetes, Helm, monitoring, and deployment troubleshooting.
- `CONTRIBUTING.md`: contributor workflow, spec-driven development expectations, branch and commit rules, PR expectations, validation expectations, documentation expectations, and release handoff expectations.
- `WORKING_WITH_AI.md`: human-facing guide for asking AI to help with planning, implementation, validation, review, and release preparation. It should link to lifecycle and workflow owner guides instead of restating the full AI runbooks.
- `.agents/references/*`: AI-facing owner guides for detailed lifecycle vocabulary, planning, execution, workflow coordination, validation, review, release, documentation routing, and code style.

Implementation should avoid duplicating runbook detail across these files.
When a section moves, the old location should keep only a short pointer if users still need a migration path.

The first implementation plan should decide whether operations content belongs at repository root as `OPERATIONS.md` or under `docs/OPERATIONS.md`.
Until that plan is accepted, this ADR records the split and leaves the exact operations path open.

## Consequences

Benefits:

- Developers get a clear distinction between local setup, contribution workflow, lifecycle/artifact routing, AI collaboration, and deployment operations.
- ADR, PRD, standalone spec, and execution-plan roles become visible without requiring readers to open `.agents/references/*` first.
- `SETUP.md` becomes shorter and more useful for onboarding.
- `WORKING_WITH_AI.md` becomes easier to use as an AI collaboration guide rather than a broad lifecycle reference.
- `.agents/references/*` remains the detailed AI runbook layer instead of leaking into the primary human documentation path.

Costs or risks:

- The first split will touch several high-traffic documentation files and can create stale links if not validated.
- Moving operational sections out of `SETUP.md` may inconvenience users who currently expect every command in one file.
- Adding `docs/DEVELOPMENT_LIFECYCLE.md` risks duplicating `.agents/references/application-lifecycle.md` unless it stays human-facing and summary-level.
- The operations document location must be chosen deliberately so `README.md`, `CONTRIBUTING.md`, `WORKING_WITH_AI.md`, `AGENTS.md`, and `.agents/references/documentation.md` do not point at different owners.

Required follow-up changes if accepted:

1. Create `docs/README.md` as the documentation index.
2. Create `docs/DEVELOPMENT_LIFECYCLE.md` with a concise human-facing lifecycle and artifact-routing explanation.
3. Split operations and deployment runbooks out of `SETUP.md` into the selected operations document path.
4. Narrow `SETUP.md`, `CONTRIBUTING.md`, and `WORKING_WITH_AI.md` to their accepted roles.
5. Update `README.md`, `docs/DESIGN.md`, `AGENTS.md`, and `.agents/references/documentation.md` only where their ownership maps or discoverability links change.
6. Update `ROADMAP.md` and any active plan created for the documentation split.
7. Run `pwsh ./scripts/docs/audit-docs.ps1` after the documentation move.

## Alternatives Considered

### Keep The Current Files And Add More Cross-Links

This is the smallest change, but it does not solve the overloaded `SETUP.md` and `WORKING_WITH_AI.md` responsibilities.
More links would make the hidden lifecycle easier to find, but not easier to understand.

### Move Lifecycle Detail Into `CONTRIBUTING.md`

This would make contributor expectations visible, but it would turn `CONTRIBUTING.md` into a process manual.
Contributor workflow should reference lifecycle and artifact routing without owning the full explanation.

### Move Lifecycle Detail Into `WORKING_WITH_AI.md`

This matches the current direction but makes lifecycle appear AI-specific.
The lifecycle, ADR, PRD, standalone spec, and plan model applies to human and AI work, so the human-facing explanation should not live only in the AI guide.

### Make `.agents/references/application-lifecycle.md` The Human Lifecycle Guide

That file is intentionally an AI owner guide with phase activities, loops, triggers, and owner-guide mapping.
It should remain authoritative for AI behavior, while `docs/DEVELOPMENT_LIFECYCLE.md` should be a short human-facing explanation.

### Put Operations Under `SETUP.md`

Keeping all commands in one setup file is convenient for search, but it makes onboarding harder and blurs local setup with deployment operation.
The preferred split keeps local setup in `SETUP.md` and deployment or runtime runbooks in an operations document.

## Confirmation

This decision is reflected in the repository when:

- `docs/README.md` exists and points humans to design, lifecycle, ADRs, PRDs, standalone specs, roadmap, operations, setup, contributing, AI collaboration, and frontend contract material.
- `docs/DEVELOPMENT_LIFECYCLE.md` explains the lifecycle and artifact routing without copying the detailed AI runbook from `.agents/references/application-lifecycle.md`.
- `SETUP.md` is focused on local setup and local troubleshooting.
- The selected operations document owns deployment, runtime, smoke, rollback, Kubernetes, Helm, and monitoring runbooks.
- `CONTRIBUTING.md` and `WORKING_WITH_AI.md` are narrowed to their accepted roles and link to lifecycle and operations docs instead of duplicating them.
- `README.md`, `docs/DESIGN.md`, `AGENTS.md`, and `.agents/references/documentation.md` agree on the ownership map.
- `pwsh ./scripts/docs/audit-docs.ps1` passes after the documentation move.

## Links

- `README.md`
- `SETUP.md`
- `CONTRIBUTING.md`
- `WORKING_WITH_AI.md`
- `ROADMAP.md`
- `docs/DESIGN.md`
- `docs/decisions/0001-adopt-pre-planning-artifacts.md`
- `docs/decisions/0002-align-lifecycle-vocabulary-with-industry-practice.md`
- `.agents/references/application-lifecycle.md`
- `.agents/references/documentation.md`
- `.agents/references/planning.md`
