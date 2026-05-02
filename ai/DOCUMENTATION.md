# Documentation Guide For AI Agents

`ai/DOCUMENTATION.md` owns standing AI guidance for documentation scope, owning-file selection, and cross-file alignment in this repository.

Use this file when the task changes repository docs, AI instruction files, reviewer examples, generated contract artifacts, roadmap entries, or release notes.

## Choose The Owning Artifact

Update the artifact that owns the kind of truth being changed:

- behavior and API contract: executable tests, `src/docs/asciidoc/`, `src/test/resources/http/`, `src/test/resources/openapi/approved-openapi.json`, and `README.md`
- AI repository rules and AI-document inventory: `AGENTS.md`
- local setup, tools, troubleshooting, and onboarding: `SETUP.md`
- contributor workflow and maintainer expectations: `CONTRIBUTING.md`
- active planned work only: `ROADMAP.md`
- released history only: `CHANGELOG.md`
- planning guidance: `ai/PLAN.md`
- single-agent implementation guidance: `ai/EXECUTION.md`
- delegated or worktree-based execution guidance: `ai/WORKFLOW.md`
- release sequencing and tagging: `ai/RELEASES.md`
- prompt starters only: `ai/PROMPTS.md`
- descriptive codebase map, API shape summary, and structural guidance: `ai/ARCHITECTURE.md`
- descriptive business-feature package map and ownership notes: `ai/BUSINESS_MODULES.md`
- code-shaping rules: `ai/CODE_STYLE.md`
- validation scope and commands: `ai/TESTING.md`
- review and security-review expectations: `ai/REVIEWS.md`

If the change does not clearly fit one owner, stop and decide that ownership before editing multiple docs.

## Alignment Rules

- update overlapping human-facing and AI-facing docs in the same change
- keep prompts short and point to the owning long-form guide instead of restating policy
- move repeated standing guidance into the best owner instead of maintaining parallel copies
- when adding, moving, or renaming AI docs, update discoverability references in `AGENTS.md` and any affected cross-references in the same change
- keep setup detail out of workflow docs and keep release history out of roadmap files

## Update Patterns

When public behavior changes:

- update the governing spec artifacts first
- keep tests, REST Docs, HTTP examples, approved OpenAPI, and `README.md` aligned where applicable
- use `ai/TESTING.md` to confirm the required validation scope

When workflow or maintainer guidance changes:

- update the owning AI guide first
- update `README.md` and `CONTRIBUTING.md` when their human-facing workflow overlaps
- update `AGENTS.md` only when the AI-document set or maintenance rules changed

When setup changes:

- update `SETUP.md`
- do not duplicate the same setup instructions into `README.md`, `AGENTS.md`, or `ai/` workflow docs

When roadmap or release history changes:

- keep unreleased planning in `ROADMAP.md`
- keep released history in `CHANGELOG.md`
- do not invent another archive for human-facing completion notes

## Cross-References

- use `ai/TESTING.md` to decide whether doc-only work still needs contract-artifact or build verification
- use `ai/REVIEWS.md` for contradiction and drift checks before finalizing documentation changes
