# Documentation Guide For AI Agents

`ai/DOCUMENTATION.md` owns artifact routing and cross-file alignment for repository docs and AI guidance.

Use this file to decide which artifact moves when behavior, setup, workflow, roadmap, release, or AI guidance changes.
Other guides should link here instead of re-listing ownership rules.

## Artifact Ownership

Update the artifact that owns the truth being changed:

- runtime behavior and public API contract: executable tests, `src/docs/asciidoc/`, `src/test/resources/http/`, `src/test/resources/openapi/approved-openapi.json`, and `README.md`
- human-facing guide for developers using AI through the application lifecycle: `WORKING_WITH_AI.md`
- AI repository rules and AI-document inventory: `AGENTS.md`
- AI local command wrapper shortcut: `ai/ENVIRONMENT_QUICK_REF.md`
- local setup, tools, troubleshooting, and onboarding: `SETUP.md`
- contributor workflow and maintainer expectations: `CONTRIBUTING.md`
- active planned work: `ROADMAP.md`
- released history: `CHANGELOG.md`
- planning process and plan-file shape: `ai/PLAN.md`
- single-agent execution flow: `ai/EXECUTION.md`
- delegation and worktree deviations: `ai/WORKFLOW.md`
- release sequencing and tagging: `ai/RELEASES.md`
- prompt-title index: `ai/PROMPTS.md`; prompt metadata and raw bodies: `ai/prompts/`; prompt loader script: `scripts/ai/get-prompt.ps1`
- repo-local reusable workflow wrappers: `ai/skills/`
- compact codebase map and structural guidance: `ai/ARCHITECTURE.md`; deeper references: `ai/references/`
- business feature ownership: `ai/BUSINESS_MODULES.md`
- product and contract direction: `ai/DESIGN.md`
- durable repo-wide lessons: `ai/LEARNINGS.md`
- edit-shaping rules: `ai/CODE_STYLE.md`
- validation scope and commands: `ai/TESTING.md`
- review and security-review expectations: `ai/REVIEWS.md`

If ownership is unclear, decide that before editing multiple docs.

## Alignment Rules

- update overlapping human-facing and AI-facing docs in the same change
- keep prompts lean; move standing policy back to the owning guide
- keep raw prompt bodies, full examples, templates, and deep references in on-demand files under `ai/prompts/`, `ai/templates/`, or `ai/references/`
- keep repo-local skills narrow; route standing policy back to the owning guide instead of duplicating it
- compact repeated standing guidance into the best owner instead of maintaining parallel copies
- keep setup detail out of planning, workflow, and release guides
- keep active or selected work in `ROADMAP.md` and released history in `CHANGELOG.md`
- when AI docs move or rename, update `AGENTS.md` and all affected cross-references in the same change
- keep REST Docs AsciiDoc files formatter-managed; write unordered lists with explicit AsciiDoc marker depth (`*`, `**`) so IntelliJ formatting cannot flatten indentation-only nesting

## Common Routing

- public behavior change: update the governing spec artifacts first, then the published contract artifacts they drive
- human-facing AI collaboration workflow change: update `WORKING_WITH_AI.md`, and update overlapping AI-facing guides in the same change when the underlying repository workflow also changed
- workflow or AI-guidance change: update the owning AI guide first; touch `AGENTS.md` only when the AI-document set or maintenance rules changed
- repo-local skill change: update the skill plus the owning AI guide when the skill wraps a workflow whose rules changed
- local command-wrapper guidance for AI agents: update `ai/ENVIRONMENT_QUICK_REF.md`, and update `SETUP.md` only when human setup or troubleshooting behavior changed
- setup or tooling change: update `SETUP.md`, not `README.md`, `AGENTS.md`, or workflow guides unless their inventories or high-level rules changed
- plan creation or material plan revision: update the concrete `ai/PLAN_*.md` file and `ROADMAP.md` together so active work points to the plan path and current status
- roadmap reprioritization: update `ROADMAP.md`, and keep `## Current Project State` aligned when the active release phase, breaking-change policy, or next target version changes
- released history: update `CHANGELOG.md`

## Cross-References

- use `ai/TESTING.md` for required validation once the right artifact set is identified
- use `ai/REVIEWS.md` for contradiction and drift checks before finalizing doc-heavy changes
