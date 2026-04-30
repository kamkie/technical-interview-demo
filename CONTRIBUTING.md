# Contributing Guide

This repository is intentionally small. Contributions should preserve that quality: readable code, direct implementations, and low ceremony.

## Ground Rules

Follow these project-level constraints first:

- Keep the demo easy to reason about
- Prefer straightforward Spring MVC, Spring Data JPA, and `@Service` code over extra abstraction
- Keep package names under `team.jit.technicalinterviewdemo`
- Do not remove the existing `hello` or `book` endpoints unless the change explicitly requires it
- Use PostgreSQL for runtime work and keep the local developer path Docker-friendly
- Keep `README.md` and `AGENTS.md` aligned when project setup, API behavior, formatter usage, logging/tracing behavior, or quality gates change

`AGENTS.md` is the authoritative source for technical constraints and architecture expectations. If this file and `AGENTS.md` ever conflict, update this file to match `AGENTS.md`.

## Branch Naming

Use short, descriptive branch names:

- `feat/book-search`
- `fix/book-update-validation`
- `docs/setup-guide`
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
- `Add book filtering by title and author`
- `Update README for PostgreSQL profile`

Keep the subject focused on one logical change. If you are working from `TODO.md`, include the roadmap item in the commit body when that context is useful, but keep the subject line readable on its own.

## Pull Request Process

Keep pull requests narrow enough to review quickly.

Before opening a PR:

1. Rebase or merge your branch so it reflects the current target branch.
2. Make sure the change is scoped to one feature, fix, or documentation update.
3. Update tests and docs when behavior changed.

Each PR should include:

- a short summary of what changed
- the reason for the change
- the commands you ran to validate it
- any follow-up work that remains out of scope

If the change affects API behavior, include example requests/responses or reference the updated generated docs.

## Testing Requirements

Run the required quality gates before asking for review:

```powershell
$env:JAVA_HOME='C:\Users\kamki\.jdks\azul-25.0.3'
$env:Path="$env:JAVA_HOME\bin;$env:Path"

.\gradlew.bat build
```

Additional expectations:

- Add or update tests when API behavior changes
- Keep the aggregate `build` clean
- Do not skip documentation generation or the Docker image step when using the standard verification flow
- Pull requests also run the dedicated OpenAPI compatibility GitHub Actions workflow against the approved baseline
- Review the JaCoCo HTML output at `build/reports/jacoco/test/html/index.html` or run `.\gradlew.bat jacocoCoverageSummary` when coverage-sensitive changes land
- `check` and `build` enforce minimum JaCoCo bundle coverage of 90% line coverage and 70% branch coverage
- If a change legitimately needs a lower threshold, raise it explicitly in review instead of weakening the gate silently

## Documentation Expectations

Documentation is part of the change, not cleanup work for later.

Update the relevant files when you change behavior:

- `README.md` for human-facing setup and runtime behavior
- `AGENTS.md` for AI-facing constraints and technical rules
- `SETUP.md` for developer onboarding changes
- `TODO.md` when a roadmap item is completed or materially re-scoped
- `src/docs/asciidoc/index.adoc` and generated REST Docs tests when public API behavior changes

## Formatting Expectations

Spotless is the formatter entry point.

Java formatting uses IntelliJ IDEA's formatter when available. If the formatter is not configured, Java formatting is skipped instead of failing the build.

Provide the formatter through one of:

```powershell
$env:IDEA_FORMATTER_BINARY='C:\Path\To\IntelliJ IDEA\bin\idea64.exe'
```

```powershell
$env:IDEA_HOME='C:\Path\To\IntelliJ IDEA'
```

```powershell
.\gradlew.bat spotlessApply -PideaFormatterBinary='C:\Path\To\IntelliJ IDEA\bin\idea64.exe'
```

## Pre-Commit Hooks

Repository-managed hooks are not enforced, but an optional sample hook is available at `.githooks/pre-commit.sample`.

To enable it locally:

```bash
git config core.hooksPath .githooks
cp .githooks/pre-commit.sample .githooks/pre-commit
chmod +x .githooks/pre-commit
```

The sample runs:

- `./gradlew build`

Keep hooks developer-local unless the team explicitly chooses to standardize them as a required workflow.
