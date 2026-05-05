---
name: repo-prompt-loader
description: Load one repository-local reusable prompt by title without reading the full prompt library. Use when the user invokes a prompt title from `ai/PROMPTS.md`, asks to use a repo prompt starter, needs exact reusable prompt body text, or needs to list available repository prompt starters.
---

# Repo Prompt Loader

Use this skill to retrieve focused prompt starters from the machine-readable prompt index.
Do not read all prompt body files unless the user explicitly asks for a full audit of the prompt library.

## Workflow

1. Read `AGENTS.md` and `ai/PROMPTS.md` first.
2. Use `scripts/ai/get-prompt.ps1` to list or load prompts:
   - list prompts: `pwsh ./scripts/ai/get-prompt.ps1 -List`
   - load one prompt: `pwsh ./scripts/ai/get-prompt.ps1 -Name "<prompt title>"`
   - emit structured output: add `-Json`
3. If the loader reports no match or an ambiguous match, ask a targeted clarification question and include the matching titles when available.
4. After loading the matching prompt body, follow that prompt plus the owner guides named by it.

## Guardrails

- Treat `ai/prompts/index.json` as the routing index and `ai/prompts/bodies/*.md` as on-demand prompt bodies.
- Keep `ai/PROMPTS.md` as the human-readable prompt list and command index.
- Do not copy standing policy into prompt bodies; route durable rules back to the owning AI guide.
- Using a prompt starter does not reduce validation, review, documentation, release, or workflow requirements.
