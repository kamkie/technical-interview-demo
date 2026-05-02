# Documentation Guide For AI Agents

`ai/DOCUMENTATION.md` owns standing AI guidance for documentation scope, ownership, and cross-file alignment in this repository.

Use this file when the task changes any repository documentation, generated contract artifact, reviewer example, roadmap entry, release note, or AI instruction file.

## Documentation Ownership

Use the owning document for the matching concern:

- `README.md`: human-facing product contract, release model, project map, and quality model
- `AGENTS.md`: repository rules for AI agents, authoritative spec locations, and AI-document inventory or maintenance rules
- `SETUP.md`: onboarding, environment setup, local tooling, and troubleshooting
- `CONTRIBUTING.md`: contributor PR flow, review expectations, and testing expectations
- `ROADMAP.md`: active planned work only
- `CHANGELOG.md`: released history only
- `src/docs/asciidoc/`: published REST Docs structure
- `src/test/resources/http/`: reviewer-facing runnable examples
- `src/test/resources/openapi/approved-openapi.json`: reviewed machine-readable public API contract
- `ai/PLAN.md`: planning rules
- `ai/EXECUTION.md`: single-agent execution rules
- `ai/WORKFLOW.md`: delegated or multi-agent workflow
- `ai/RELEASES.md`: release workflow
- `ai/PROMPTS.md`: lean prompt starters, not standing policy
- `ai/CODE_STYLE.md`: standing code-style and change-shaping guidance for AI edits
- `ai/TESTING.md`: standing testing and validation guidance for AI edits
- `ai/REVIEWS.md`: standing code-review and security-review guidance for AI edits
- `ai/DOCUMENTATION.md`: standing documentation ownership and cross-reference guidance for AI edits

## Alignment Rules

- update human-facing and AI-facing docs together when their scopes overlap
- keep prompts lean by linking to the owning long-form guide instead of restating policy
- compact overlapping guidance into the single best owning file when docs begin to drift together
- when adding, moving, or renaming AI documents, update discoverability references in `AGENTS.md`, `README.md`, and any other affected docs in the same change
- do not move setup detail into AI workflow docs or release history into roadmap files

## Ownership Rebalance In This Repo

This repo now keeps recurring guidance in focused AI guides so workflow docs can stay narrow:

- code-style and change-shaping guidance moved into `ai/CODE_STYLE.md`
- testing and validation guidance moved into `ai/TESTING.md`
- code-review and security-review guidance moved into `ai/REVIEWS.md`
- documentation ownership and cross-reference guidance moved into `ai/DOCUMENTATION.md`

Keep future edits in those owning files instead of re-scattering the same rules across `ai/PROMPTS.md`, workflow docs, README, or contributing guidance.

## Documentation Update Patterns

When behavior changes:

- update the governing spec artifacts first
- update all affected contract docs together
- keep README, generated docs, HTTP examples, and approved OpenAPI aligned where applicable

When workflow or maintainer guidance changes:

- update the owning AI docs
- update README and CONTRIBUTING where their human-facing workflow overlaps
- update AGENTS when the AI-document inventory or maintenance rules changed

When setup changes:

- update `SETUP.md`
- avoid duplicating setup detail into `README.md`, `AGENTS.md`, or AI workflow files

## Cross-References

- use `ai/TESTING.md` to decide whether documentation-only work still needs contract-artifact or build verification
- use `ai/REVIEWS.md` for final contradiction and drift checks before finalizing doc changes
