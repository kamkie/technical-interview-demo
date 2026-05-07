# Repo Task Skill Spec

## Purpose

`repo-task` provides a low-context replacement for copied reusable prompts. It dispatches one saved repository workflow at a time from task reference files.

## Context Loading Contract

- Default skill load: `SKILL.md` only.
- Exact slug request: load `references/tasks/<slug>.md` only.
- Ambiguous request: load `references/index.md`, choose one slug, then load only that matching task file.
- Maintenance request: load this spec before changing the dispatcher, index, or task schema.
- Never load every task body for normal execution.

## File Layout

```text
.agents/skills/repo-task/
  SKILL.md
  agents/openai.yaml
  references/
    spec.md
    index.md
    tasks/
      <slug>.md
```

## Task File Schema

Each task file must use this shape:

```md
# Task Name

Category: Category Name
Slug: `task-slug`
Placeholders: <placeholder>, <other_placeholder>

Task instructions...
```

Required rules:

- The filename must be `<slug>.md`.
- The `Slug` value must match the filename stem.
- `Category`, `Slug`, and `Placeholders` must appear near the top of the file.
- Placeholder names must match the names users are expected to provide.
- Task instructions must be self-contained enough to execute after loading only that task file and the repo files it names.
- Shared standing policy belongs in `AGENTS.md` or another owning guide, not duplicated across task files.

## Index Contract

`references/index.md` is only for slug discovery. It should stay compact and list:

- category
- slug
- task name
- placeholders

Do not copy full task instructions into the index.

## Dispatcher Contract

`SKILL.md` must stay small and should contain only:

- trigger description in YAML frontmatter
- workflow for resolving a task
- invocation examples
- pointer to this spec for maintenance

Detailed task behavior belongs in task files. Skill maintenance rules belong in this spec.

## Adding Or Updating Tasks

1. Create or edit `references/tasks/<slug>.md`.
2. Keep the task schema intact.
3. Update `references/index.md`.
4. Validate that each task file has `Category`, `Slug`, and `Placeholders`.
5. Run the skill validator after changing `SKILL.md` or `agents/openai.yaml`.

## Validation

Run:

```powershell
python "<skill-creator>/scripts/quick_validate.py" .agents/skills/repo-task
git diff --check
```

If `PyYAML` is unavailable, install it into a temporary directory outside the repository and set `PYTHONPATH` only for the validation run.

## Non-Goals

- This skill is not a build system, release tool, or task runner.
- This skill should not hide broad project policy inside task files.
- This skill should not preserve a root prompt-library file as a parallel source of truth.
