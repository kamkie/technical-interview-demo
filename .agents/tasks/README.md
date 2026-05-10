# Repository Task Prompts

`.agents/tasks/README.md` owns the catalog and maintenance rules for standalone repository task prompts under `.agents/tasks/`.

Use these prompts only when the user asks for a named repository-specific task or a reusable prompt that is more concrete than an existing owner guide.
For ordinary planning, execution, workflow, validation, review, or release work, load the owning `.agents/references/*.md` guide directly instead of looking for a task prompt.

## What Belongs Here

- keep prompts that add task-specific instructions, inventories, measurements, or decision criteria not already owned by an existing guide
- do not keep prompts that only say to follow an existing planning, execution, workflow, testing, review, or release guide
- keep durable standing rules in the best owning guide under `.agents/references/`, not in task prompts
- keep each prompt self-contained enough to use after loading this README, the prompt file, and the repo artifacts it names
- task prompts may name required source references, inputs, outputs, validation, and generated artifacts
- keep prompt names lower-kebab-case and aligned with their filename

## Current Prompts

| Task | Use When |
| --- | --- |
| [Roadmap Decision Review](roadmap-decision-review.md) | Identify roadmap-framing decisions that block confident planning or sequencing. |
| [Compact AI Guidance](compact-ai-guidance.md) | Compact standing AI instruction files without bulk-loading archives or unrelated guides. |
| [Measure AI Context](measure-ai-context.md) | Run the checked-in context measurement report script and summarize its output. |
| [Evaluate AI Guidance](evaluate-ai-guidance.md) | Create a deep AI-guidance lifecycle and context-load assessment report. |
| [Prioritize Security And Quality Roadmap](prioritize-security-quality-roadmap.md) | Inspect GitHub security and quality alerts and prioritize roadmap entries. |
| [Toolchain Upgrade](toolchain-upgrade.md) | Plan upgrades for CI actions, Java dependencies, Gradle plugins/tools, and Docker images. |

## Maintenance

- update this README when adding, renaming, moving, or removing task prompts
- remove prompts that become thin wrappers after owner guides gain the same instructions
- when a task prompt changes workflow policy, move the policy into the owning reference guide and leave only task-specific prompt text here
- update live references in `AGENTS.md`, `.agents/references/`, and other non-archived `.agents/` files when this directory changes
