---
name: repo-task
description: Dispatch repository-local reusable task starters from saved task reference files. Use when the user invokes a task slug or reusable task starter for this repository, asks for a task from the repository task catalog, gives an ambiguous task title that must be resolved to one saved workflow, or asks to maintain the repo-task dispatcher, index, task schema, or task files.
---

# Repo Task

Use this skill to load one repository task workflow at a time. Keep standing policy in `AGENTS.md` and the focused `.agents/references/` owner guides; task files are only reusable starters.

## Resolve A Task

1. If the user gives an exact slug, load only `references/tasks/<slug>.md`.
2. If the request is ambiguous, load `references/index.md`, choose one slug, then load only that matching task file.
3. If the user asks to add, update, validate, or restructure repo-task itself, load `references/spec.md` before changing `SKILL.md`, `agents/openai.yaml`, `references/index.md`, or any task file.
4. Ask a targeted clarification question when the index does not resolve the requested task confidently or required placeholders are missing.

Do not load every task body for normal execution.

## Invocation Examples

- Exact slug: `Use repo-task create-plan for OAuth audit follow-up.`
- Task title: `Use repo-task to run Create Plan for OAuth audit follow-up.`
- Ambiguous request: `Use repo-task for the release readiness task.`
- Maintenance: `Add a repo-task starter for dependency triage.`

## Maintenance

Follow `references/spec.md` for the file layout, task schema, index contract, dispatcher contract, and validation commands.
