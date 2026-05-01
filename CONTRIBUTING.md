# Contributing Guide

This repository is intentionally small. Contributions should preserve that quality: readable code, direct implementations, low ceremony, and spec-driven changes.

## Ground Rules

Follow these project-level constraints first:

- Keep the demo easy to reason about.
- Prefer straightforward Spring MVC, Spring Data JPA, and `@Service` code over extra abstraction.
- Keep package names under `team.jit.technicalinterviewdemo`.
- Do not remove the existing `hello` or `book` endpoints unless the change explicitly requires it.
- Use PostgreSQL for runtime work and keep the local developer path Docker-friendly.
- Keep `README.md`, `AGENTS.md`, and `SETUP.md` aligned when product contract, engineering rules, or setup guidance change.

`AGENTS.md` is the authoritative source for project-specific engineering rules. `SETUP.md` is the authoritative source for local environment and troubleshooting guidance.

## Spec-Driven Development

Contribute spec-first, not implementation-first.

Expected flow:

1. Identify the behavior being changed.
2. Update or add the relevant spec artifact.
3. Implement the smallest code change that satisfies the updated spec.
4. Verify build, docs, and compatibility gates stay aligned.

Relevant spec artifacts include:

- integration and documentation tests under `src/test/java/`
- Asciidoc sources under `src/docs/asciidoc/`
- approved OpenAPI baseline at `src/test/resources/openapi/approved-openapi.json`
- runnable HTTP examples under `src/test/resources/http/`
- `README.md` for public human-facing contract
- `ROADMAP.md` for active planned work
- `CHANGELOG.md` for released history

## Branch Naming

Use short, descriptive branch names:

- `feat/book-search`
- `fix/book-update-validation`
- `docs/spec-driven-docs`
- `chore/testcontainers-upgrade`

Recommended pattern:

```text
<type>/<short-kebab-description>
```

Suggested types:

- `feat`
- `fix`
- `docs`
- `chore`
- `refactor`
- `test`

## Commit Messages

Use concise, imperative subjects that describe the change clearly.

Good examples:

- `Add developer setup guide`
- `Flatten localization lookups onto collection filters`
- `Document spec-driven contribution workflow`

Keep the subject focused on one logical change.

## Pull Request Process

Keep pull requests narrow enough to review quickly.

Before opening a PR:

1. Rebase or merge your branch so it reflects the current target branch.
2. Make sure the change is scoped to one feature, fix, refactor, or documentation update.
3. Update tests and docs when behavior changed.

Each PR should include:

- a short summary of what changed
- the reason for the change
- the commands you ran to validate it
- any follow-up work that remains out of scope

If the change affects public API behavior, include example requests/responses or reference the updated generated docs and OpenAPI change.

Branch protection expectations for the default branch:

- require the `CI` workflow to pass
- require at least one reviewer
- prefer squash merges or another linear-history policy
- keep version-tag creation with maintainers who also own release validation

## Testing Requirements

Run the required quality gate before asking for review:

```powershell
.\gradlew.bat build
```

Additional expectations:

- Add or update tests when API behavior changes.
- Keep the aggregate `build` clean.
- Do not skip documentation generation or the Docker image step when using the standard verification flow.
- The `CI` workflow includes the OpenAPI compatibility gate as part of the full build.
- Pull requests should stay green on the `CI` workflow before review is requested.
- Review the JaCoCo HTML output at `build/reports/jacoco/test/html/index.html` or run `./gradlew jacocoCoverageSummary` when coverage-sensitive changes land.
- `check` and `build` enforce minimum JaCoCo bundle coverage of 90% line coverage and 70% branch coverage.
- Rerun `./scripts/run-phase-9-benchmarks.ps1` when changing book list/search behavior, localization lookup behavior, or the OAuth/session startup flow.
- Use `SETUP.md` for environment prerequisites and local tool configuration.

Deployment-oriented checks when those assets are part of the change:

- `helm lint helm/technical-interview-demo`
- `helm template technical-interview-demo helm/technical-interview-demo -f helm/technical-interview-demo/values-local.yaml`
- `kubectl kustomize k8s/overlays/local`
- `kubectl apply --dry-run=client -k k8s/overlays/local`
- `kubectl kustomize k8s/monitoring`
- `kubectl kustomize monitoring/grafana`

## Documentation Expectations

Documentation is part of the change.

Update the relevant files when behavior changes:

- `README.md` for supported human-facing behavior and contract changes
- `AGENTS.md` for engineering rules and AI-facing project constraints
- `SETUP.md` for onboarding, environment, and troubleshooting changes
- `ROADMAP.md` when active roadmap items are added, removed, or materially re-scoped
- `CHANGELOG.md` when preparing or documenting a release
- `src/docs/asciidoc/` and related REST Docs tests when public API behavior changes
- `src/test/resources/http/` when reviewer-facing request examples change

## Formatting Expectations

Spotless is the formatter entry point.

Java formatting uses IntelliJ IDEA's formatter when available. If the formatter is not configured, Java formatting is skipped instead of failing the build.

Use `SETUP.md` for formatter setup details and local formatter configuration options.
