# Contributing Guide

Technical Interview Demo is a small, spec-driven Spring Boot application for interview exercises. Contributions should keep it readable, direct, and easy to reason about.

## Quick Checklist

1. Pick or open work in [ROADMAP.md](ROADMAP.md). If intent is unclear, add an ADR, PRD, or standalone spec first — see [docs/DEVELOPMENT_LIFECYCLE.md](docs/DEVELOPMENT_LIFECYCLE.md).
2. Branch as `<type>/<short-kebab-description>`.
3. Make spec-first changes (update tests, REST Docs, or OpenAPI alongside the code).
4. Run `./build.ps1 build`; if user-facing docs changed, also run `pwsh ./scripts/docs/audit-docs.ps1`.
5. Commit using the [.gitmessage](.gitmessage) shape, open a PR, and address review.

## Read This First

Before contributing, skim:

- [README.md](README.md) — project overview and supported contract.
- [docs/README.md](docs/README.md) — human-facing documentation index; start here for any document not linked below.
- [docs/DEVELOPMENT_LIFECYCLE.md](docs/DEVELOPMENT_LIFECYCLE.md) — when to write an ADR, PRD, standalone spec, or plan, and which artifact owns which change.
- [AGENTS.md](AGENTS.md) — repository-local AI rules and spec priority (only if you will use AI in this repo).

## Project Ground Rules

Keep these repository constraints intact unless the change explicitly redefines them:

- Keep the demo small, readable, and easy to reason about.
- Prefer direct Spring MVC, Spring Data JPA, and `@Service` code over extra abstraction.
- Keep package names under `team.jit.technicalinterviewdemo`.
- Use PostgreSQL for runtime work and keep the local developer path Docker-friendly.
- Keep the public application contract centered on `/api/**`.
- Treat `/`, `/hello`, `/docs`, `/v3/api-docs`, `/v3/api-docs.yaml`, and `/actuator/**` as internal or deployment-scoped surfaces unless a reviewed contract change says otherwise.
- Treat executable specs, REST Docs, and the approved OpenAPI baseline as product evidence; keep reviewer HTTP examples and suites as convenience tools aligned with that behavior.

When documentation changes, follow the artifact-routing table in [docs/DEVELOPMENT_LIFECYCLE.md](docs/DEVELOPMENT_LIFECYCLE.md).

## Spec-Driven Development

Contribute spec-first, not implementation-first.

Normal flow:

1. Identify the behavior being changed.
2. Identify the governing spec or contract artifact.
3. Update or add the spec first when behavior is intentionally changing.
4. Implement the smallest coherent change that satisfies the updated spec.
5. Verify the executable and published artifacts still agree.

For the authoritative artifact map and change-routing rules, see [docs/DEVELOPMENT_LIFECYCLE.md](docs/DEVELOPMENT_LIFECYCLE.md) and [AGENTS.md](AGENTS.md) (Spec Priority).

## Branches And Commit Messages

Use short, descriptive branch names of the form `<type>/<short-kebab-description>`:

- `feat/book-search`
- `fix/session-bootstrap`
- `docs/contributing-refresh`
- `refactor/localization-cache`

Suggested branch types: `feat`, `fix`, `docs`, `chore`, `refactor`, `test`.

Commit messages follow Conventional Commits 1.0.0 with the AI trailer block defined in [.gitmessage](.gitmessage). AI-created commits must also follow the rules in [.agents/references/execution.md](.agents/references/execution.md), which owns the full footer schema, validation provenance, and per-source guidance. Enable the local template once with:

```powershell
git config commit.template .gitmessage
```

Breaking changes are disallowed while [ROADMAP.md](ROADMAP.md) says so.

## Validation Expectations

Run the appropriate quality gate before asking for review:

```powershell
./build.ps1 build
```

`./build.ps1 build` short-circuits to a lightweight check when the diff is documentation-only or otherwise lightweight; use `./build.ps1 -FullBuild build` for cumulative branches, release candidates, or any code change that needs the full gate. CI applies the same classifier.

Run [scripts/docs/audit-docs.ps1](scripts/docs/audit-docs.ps1) when user-facing Markdown or AsciiDoc changes.

For the full command catalog (focused tasks, Gatling, OpenAPI baseline refresh, parallel-task warnings, skip flags) see [docs/LOCAL_DEVELOPMENT.md](docs/LOCAL_DEVELOPMENT.md). For Helm and Kubernetes asset checks see [docs/OPERATIONS.md](docs/OPERATIONS.md).

## Formatting Expectations

Run `./build.ps1 format` before review and `./build.ps1 checkFormat` to verify. Java formatting is owned by Palantir Java Format (Gradle plus the IntelliJ plugin); Kotlin, Gradle Kotlin DSL, and selected support-file whitespace are handled by Spotless. Full details: [docs/LOCAL_DEVELOPMENT.md](docs/LOCAL_DEVELOPMENT.md).

## Pull Request Expectations

Keep pull requests narrow enough to review quickly.

Before opening a PR:

1. Finish the intended local implementation scope first.
2. Run the required local validation for the actual change type.
3. Rebase or merge so the branch reflects the current target branch.
4. Update the right specs and docs when behavior or workflow changed.

Opening a PR is the handoff after local execution is complete. It is not a substitute for local validation, review, or documentation updates.

Each PR should include:

- A short summary of what changed.
- The reason for the change.
- The commands you ran to validate it.
- Any follow-up work intentionally left out of scope.
- Any security-sensitive changes such as auth, secrets, workflow permissions, logging of sensitive data, or container publication behavior.

Self-review and reviewer focus should stay on bugs, regressions, spec drift, missing validation, and security-sensitive changes before style-only cleanup.

If the change affects public API behavior, include example requests or responses, or point reviewers to the updated REST Docs and OpenAPI change.

Default-branch expectations:

- Require the `CI` workflow to pass.
- Require at least one reviewer.
- Prefer squash merges or another linear-history policy.
- Keep release tagging with maintainers who also own release validation.

## Documentation Expectations

Documentation is part of the change. Update the owning artifact named in [docs/DEVELOPMENT_LIFECYCLE.md](docs/DEVELOPMENT_LIFECYCLE.md) instead of spreading partial updates across unrelated files. Run `pwsh ./scripts/docs/audit-docs.ps1` when user-facing Markdown or AsciiDoc changes.

## Working With AI

If you use AI in this repository, start with [docs/WORKING_WITH_AI.md](docs/WORKING_WITH_AI.md). The human still owns scope and product intent, assumptions and tradeoffs, diff review, validation choices, and release decisions.

## Worktrees, Plans, And Integration

Treat `main` as the integration branch for completed work.

If you are executing a multi-step plan in a branch or git worktree:

- Keep the work there until the full planned scope is complete.
- Push that branch and open a PR instead of trying to release directly from a worktree-only branch tip.
- Consider worktree-based execution complete only when the finished branch has been pushed and the PR is open or already merged onto `main`.

Do not cut a release from unmerged worktree state.

## Release And Maintainer Expectations

*Maintainers only.*

Release preparation starts only after the approved implementation PR has been merged onto `main`. Cut a release only from `main`, never from an unmerged worktree.

For the full release workflow (migration classification, restore-drill evidence, full-build gate, post-deploy smoke, `CHANGELOG.md` and `ROADMAP.md` updates, tag verification) follow [.agents/references/releases.md](.agents/references/releases.md). For deployment, post-release verification, rollback, and incident response follow [docs/OPERATIONS.md](docs/OPERATIONS.md) and [.agents/references/operations.md](.agents/references/operations.md).
