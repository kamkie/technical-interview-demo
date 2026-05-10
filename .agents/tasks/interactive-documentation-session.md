# Interactive Documentation Session

Start an interactive documentation-only session focused on `AGENTS.md`, `.agents/references/`, and `docs/`.

Read first: `AGENTS.md`, `.agents/references/documentation.md`, and this task.
When editing `.agents/references/*.md`, also read `.agents/references/references-rules.md`.
Load only the target document and matching domain guides needed for the current user request.

Scope:

- edit only `AGENTS.md`, `.agents/references/*.md`, and `docs/**/*.md` unless the user explicitly expands scope
- keep changes documentation-only and current-state; do not implement code, build, workflow, release, setup, or generated-artifact changes
- preserve the user's latest direction during the interactive session, including wording preferences and rejected terminology
- ask for clarification when the requested documentation behavior is not clear enough to express as guidance

Allowed inspection:

- read files with targeted commands such as `Get-Content`, `Select-String`, `rg`, `git diff`, and `git status`
- use targeted searches to find stale references, duplicated wording, broken section names, or ownership drift
- review diffs manually and report remaining ambiguity or skipped checks

Do not run normal-change verification:

- no builds, tests, Gradle tasks, linters, formatters, static analysis, benchmarks, Docker commands, or release checks
- no `git diff --check` or standard validation commands from `.agents/references/testing.md`
- no automatic broad repository scans unless the user asks for one

When updating task prompts or reference guidance during the session, keep `.agents/tasks/README.md`, `AGENTS.md`, `.agents/references/documentation.md`, and `.agents/references/references-rules.md` aligned only when their ownership actually overlaps the change.

Before handoff, summarize changed documentation files, manual review performed, skipped normal verification, and any unrelated dirty worktree files observed.
