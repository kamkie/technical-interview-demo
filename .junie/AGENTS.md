# Junie Project Guidelines

Follow the repository-level instructions in `../AGENTS.md` as the primary source of truth for this project. This file exists so Junie discovers those rules from its preferred project-local guidance location.

## Start Here

- Read `../AGENTS.md` before making changes.
- For ad hoc implementation tasks, also read `../.agents/references/execution.md`.
- For validation scope and commands, read `../.agents/references/testing.md`.
- For multi-agent modes, role mapping, delegation, or workflow state, read `../.agents/references/workflow.md`.
- For setup, tooling, or troubleshooting, read `../SETUP.md` instead of duplicating setup details here.

## Working Rules

- Use spec-driven development: identify the behavior and governing spec or doc, update the spec first when behavior changes, then implement the smallest matching change.
- Keep public behavior, executable specs, and published contract docs aligned.
- Do not use `.junie/` for temporary files; keep it limited to Junie guidance and configuration.
- Prefer targeted context loading over bulk-reading the repository.
- Validate ordinary implementation changes with `./build.ps1 build`; use `./build.ps1 -FullBuild build` when proving cumulative committed work, whole plans, or release candidates.

## Mode To Role Mapping

- `M0: direct`: one agent handles the work directly.
- `M1: assisted`: Coordinator plus read-only Reviewer, Verifier, or Specialist.
- `M2: delegated`: Coordinator plus one Worker with one bounded write scope.
- `M3: parallel`: Coordinator plus Workers on disjoint write scopes.
- `M4: gated`: Coordinator plus Workers and independent Reviewer, Verifier, or Specialist gates.

Repo-local skills live under `../.agents/skills/`.
Use them as tactical wrappers that point back to the owner guides, not as policy replacements.

## Junie-Specific Notes

- Junie sessions are single-agent; treat all work as `M0: direct`. The `M1`–`M4` modes and Coordinator/Worker/Reviewer/Verifier/Specialist roles in `../.agents/references/workflow.md` describe multi-agent orchestration and do not apply to a Junie session in isolation.
- Junie's interaction mode (e.g., `[CODE]`, `[FAST_CODE]`, `[ADVANCED_CHAT]`, `[RUN_VERIFY]`, `[SETUP]`, `[CHAT]`, `[NICHE]`) is authoritative for workflow shape. The execution loop in `../.agents/references/execution.md` extends the chosen Junie mode; it does not replace it.
- Commit only when the user explicitly requests a handoff or commit. The repo rule "commit every completed task before handoff" in `../.agents/references/execution.md` applies to autonomous and CLI agents; interactive Junie sessions stay uncommitted by default and the user drives commits.
- Trivial non-behavioral edits (typos, formatting, comments, log strings, internal renames with no public surface) do not require a spec update. Spec-first applies to behavior changes and to any change visible to executable specs or published contract artifacts.
- Use PowerShell syntax for all terminal commands: `;` for chaining, backslashes in paths, `./build.ps1` instead of POSIX equivalents, no Bash-only constructs.
- Default minimal read set for a bounded code edit: this file, root `../AGENTS.md`, `../.agents/references/execution.md`, `../.agents/references/code-style.md`, `../.agents/references/testing.md`, plus the governing spec or source files. Do not bulk-load `../.agents/references/`; add other guides only when a concrete trigger from the Documents Map in `../AGENTS.md` applies.
