# Repository Knowledge Spec

## Purpose

This spec defines where durable repository knowledge belongs so human documentation, Codex instructions, reusable workflows, generated reports, and historical material do not compete for the same files.

## Directory Ownership

### Root

- `AGENTS.md`: always-on Codex instructions, repository rules, validation commands, and a short on-demand reference map.
- `README.md`: human entry point for the project.
- `SETUP.md`: human local environment, setup, tooling, and troubleshooting guide.
- `CONTRIBUTING.md`: human contributor workflow and maintainer expectations.
- `CHANGELOG.md`: released history.
- `ROADMAP.md`: active release phase, roadmap sequencing, and selected planned work.
- `WORKING_WITH_AI.md`: human-facing guide for working with AI through the application lifecycle.
- Build, package, and tool files: keep at the conventional root paths for the selected stack.

Keep root markdown minimal. If a file is not meant to be read on every task, put it under `docs/` or `.agents/` and link to it from `AGENTS.md` only when useful.
Agents do not load `WORKING_WITH_AI.md` by default; load it only when the user asks about the human AI workflow or when the file itself is being updated.

### `docs/`

Use `docs/` for durable project knowledge that is useful to humans and agents.

Recommended files:

- `docs/ARCHITECTURE.md`: current system architecture, module boundaries, data flow, external integrations, and non-obvious constraints.
- `docs/DESIGN.md`: product or UX decisions, domain terminology, and behavior-level design notes.
- `docs/SECURITY.md`: security model, threat assumptions, sensitive data handling, and security validation.
- `docs/TESTING.md`: test strategy, required commands, fixtures, and validation expectations.
- `docs/OPERATIONS.md`: local runbook, deployment, observability, incident, backup, and recovery guidance.
- `docs/API.md`: public API contracts when they are not generated from source.
- `docs/LEARNINGS.md`: durable engineering lessons that should be visible to humans.

### `docs/adr/`

Use `docs/adr/` for Architecture Decision Records.

Use one file per decision:

```text
docs/adr/0001-short-title.md
```

Each ADR should capture status, context, decision, consequences, and follow-up work. ADRs should not be rewritten into generic architecture prose; summarize current state in `docs/ARCHITECTURE.md` and link to the ADR when the decision history matters.

### `.agents/`

Use `.agents/` for Codex-specific material that is not ordinary human project documentation.

Recommended files and folders:

- `.agents/skills/`: repo-local Codex skills.
- `.agents/references/`: on-demand AI references that should not load by default.
- `.agents/plans/`: active AI execution plans.
- `.agents/templates/`: AI templates used to create new plans, reviews, tasks, or other agent artifacts.
- `.agents/reports/`: generated AI analysis reports that may be useful later but are not standing policy.
- `.agents/archive/`: historical AI artifacts that should not be loaded unless explicitly requested.
- `.agents/tmp/`: local generated AI work logs and scratch output that should not be committed unless a specific workflow says otherwise.

### `.agents/references/`

Use `.agents/references/` for AI-only guidance and context.

Recommended files:

- `.agents/references/LEARNINGS.md`: lessons about how agents should work in this repo, recurring mistakes, prompt/workflow lessons, and repository-specific AI operating notes.
- `.agents/references/CONTEXT.md`: compact project state for agents when `AGENTS.md` should stay small.
- `.agents/references/repository-knowledge-spec.md`: this ownership spec.

Do not put human-facing architecture, testing, security, or operations docs here unless the content is only useful to agents.

### `.agents/plans/`

Use `.agents/plans/` for active execution plans that coordinate planned AI work.

Plan files own task-specific lifecycle, scope, milestones, validation notes, and user-verification prompts. Keep active plans out of archive paths until the related work has been released or intentionally retired.

### `.agents/templates/`

Use `.agents/templates/` for reusable AI artifact templates.

Templates provide starting structure only. They should not become standing policy; durable rules belong in `AGENTS.md`, `docs/`, or `.agents/references/`.

### `.agents/skills/`

Use `.agents/skills/` for reusable Codex workflows.

Each skill owns its own `SKILL.md`, optional `agents/openai.yaml`, and optional bundled `references/`, `scripts/`, or `assets/`.

Skill references should describe how to run that skill, not broad repository policy. Broad policy belongs in `AGENTS.md`, `docs/`, or `.agents/references/`.

### `.agents/tmp/`

Use `.agents/tmp/` for local AI scratch output such as worker logs, manual-regression run logs, and temporary workflow evidence.

Do not commit `.agents/tmp/` content by default. If temporary evidence must become durable, summarize it in the owning plan, report, or release artifact and keep the scratch file ignored.

## Placement Rules

- If humans should read it, prefer `docs/`.
- If Codex should always follow it, put the shortest possible rule in `AGENTS.md`.
- If Codex should sometimes read it, put it in `.agents/references/` and link it from `AGENTS.md`.
- If it is an active execution plan, put it in `.agents/plans/`.
- If it is an AI artifact template, put it in `.agents/templates/`.
- If it is a reusable task workflow, put it in `.agents/skills/`.
- If it records generated analysis, put it in `.agents/reports/`.
- If it is historical and not normally active, put it in `.agents/archive/`.
- If it is local scratch output, put it in `.agents/tmp/` and keep it out of commits by default.
- If it explains a durable architectural decision, put it in `docs/adr/` and summarize current state in `docs/ARCHITECTURE.md`.

## Naming Rules

- Use lowercase directory names.
- Use uppercase conventional markdown filenames for broad human docs, such as `ARCHITECTURE.md` and `TESTING.md`.
- Use lowercase hyphenated filenames for AI specs and references under `.agents/references/`.
- Use numbered ADR filenames: `0001-short-title.md`.
- Avoid duplicate owners, such as both `docs/LEARNINGS.md` and `.agents/references/LEARNINGS.md`, unless they intentionally serve different audiences.

## AGENTS.md Reference Map

Keep `AGENTS.md` as a small map, not a copy of the referenced documents.

Suggested entries:

```md
## On-Demand References

- `docs/ARCHITECTURE.md`: read before architectural or cross-module changes.
- `docs/TESTING.md`: read before adding or changing validation strategy.
- `.agents/references/LEARNINGS.md`: read when updating agent workflow or investigating repeated agent mistakes.
- `.agents/references/repository-knowledge-spec.md`: read before adding or moving repository knowledge files.
```

## Migration Rule

When adding a new repository knowledge file, first decide its owner using this spec. If the file does not fit any owner, add the smallest necessary owner rule here before creating the file.
