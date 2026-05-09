# Junie Project Guidelines

Follow the repository-level instructions in `../AGENTS.md` as the primary source of truth for this project. This file exists so Junie discovers those rules from its preferred project-local guidance location.

## Start Here

- Read `../AGENTS.md` before making changes.
- For ad hoc implementation tasks, also read `../.agents/references/execution.md`.
- For validation scope and commands, read `../.agents/references/testing.md`.
- For setup, tooling, or troubleshooting, read `../SETUP.md` instead of duplicating setup details here.

## Working Rules

- Use spec-driven development: identify the behavior and governing spec or doc, update the spec first when behavior changes, then implement the smallest matching change.
- Keep public behavior, executable specs, and published contract docs aligned.
- Do not use `.junie/` for temporary files; keep it limited to Junie guidance and configuration.
- Prefer targeted context loading over bulk-reading the repository.
- Validate ordinary implementation changes with `./build.ps1 build`; use `./build.ps1 -FullBuild build` when proving cumulative committed work, whole plans, or release candidates.