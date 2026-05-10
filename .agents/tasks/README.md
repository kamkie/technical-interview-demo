# Repository Task Prompts

`.agents/tasks/README.md` owns the catalog and maintenance rules for standalone repository task prompts.
Use these prompts only for named repository-specific tasks that are more concrete than an existing `.agents/references/` guide.

## Rules

- keep prompts narrow, single-purpose, and self-contained
- name the smallest useful read set; avoid broad repository scans
- bound outputs by naming the expected report, summary, or artifact location
- keep durable policy in `.agents/references/`, not task prompts
- do not keep prompts that only invoke an existing planning, execution, workflow, testing, review, or release guide
- do not add metadata preambles; keep catalog metadata here
- update this README when adding, renaming, moving, or removing prompts

## Current Prompts

| Task | Use When |
| --- | --- |
| [Roadmap Decision Review](roadmap-decision-review.md) | Roadmap-framing decisions block planning or sequencing. |
| [Interactive Documentation Session](interactive-documentation-session.md) | An interactive docs-only session should stay focused on `AGENTS.md`, `.agents/references/`, and `docs/` without normal-change verification. |
| [Compact AI Guidance](compact-ai-guidance.md) | Standing AI instruction files have overlap, stale references, or accumulated history. |
| [Measure AI Context](measure-ai-context.md) | A context consumption report is needed. |
| [Evaluate AI Guidance](evaluate-ai-guidance.md) | A lifecycle and context-load assessment report is needed. |
| [Prioritize Security And Quality Roadmap](prioritize-security-quality-roadmap.md) | GitHub security or quality alerts need roadmap prioritization. |
| [Toolchain Upgrade](toolchain-upgrade.md) | CI actions, Java dependencies, Gradle plugins/tools, or Docker image upgrades need a plan. |
