# Documentation Guide For AI Agents

`ai/DOCUMENTATION.md` owns artifact routing and cross-file alignment for repository docs and AI guidance.

Use this file to decide which artifact moves when behavior, setup, workflow, roadmap, release, or AI guidance changes.
Other guides should link here instead of re-listing ownership rules.

Entry points:

- artifact-location lookup only: read `## Artifact Ownership`
- AI-document edits: read `## Artifact Ownership` and `### AI Document Maintenance`
- documentation changes: continue into change-type routing, alignment rules, and common routing as needed

## Artifact Ownership

Update the artifact that owns the truth being changed:

- runtime behavior and public API contract: executable tests, `src/docs/asciidoc/`, `src/manualTests/resources/http/`, `src/test/resources/openapi/approved-openapi.json`, and `README.md`
- human-facing guide for developers using AI through the application lifecycle: `WORKING_WITH_AI.md`
- AI repository rules and AI-document inventory: `AGENTS.md`
- AI local command wrapper shortcut: `ai/ENVIRONMENT_QUICK_REF.md`
- local setup, tools, troubleshooting, and onboarding: `SETUP.md`
- contributor workflow and maintainer expectations: `CONTRIBUTING.md`
- active planned work: `ROADMAP.md`
- released history: `CHANGELOG.md`
- planning process and plan-file shape: `ai/PLANNING.md`
- whole active-plan execution across milestones: `ai/PLAN_EXECUTION.md`
- ad hoc task and single-milestone execution: `ai/EXECUTION.md`
- branch, worktree, delegation, worker-log, integration, and remote-handoff mechanics: `ai/WORKFLOW.md`; detailed delegated-work mechanics: `ai/references/WORKFLOW_DELEGATED_PLAN.md` and `ai/references/WORKFLOW_COORDINATED_PLANS.md`
- release sequencing and tagging: `ai/RELEASES.md`; detailed release checklist and artifact verification: `ai/references/RELEASE_CHECKLIST.md` and `ai/references/RELEASE_ARTIFACT_VERIFICATION.md`
- reusable task catalog: `ai/TASK_LIBRARY.md`; each task section owns its title, placeholders, and task text
- repo-local reusable workflow wrappers: `ai/skills/`; Codex plugin marketplace configuration: `.agents/plugins/marketplace.json` when intentionally introducing a repo-scoped plugin
- compact codebase map, structural guidance, and business feature ownership: `ai/ARCHITECTURE.md`; deeper references: `ai/references/`
- product and contract direction: `ai/DESIGN.md`
- durable repo-wide lessons: `ai/LEARNINGS.md`
- edit-shaping rules: `ai/CODE_STYLE.md`
- validation scope and commands: `ai/TESTING.md`
- review and security-review expectations: `ai/REVIEWS.md`

If ownership is unclear, decide that before editing multiple docs.

### AI Document Maintenance

Load this section on demand before changing `AGENTS.md`, top-level `ai/*.md`, task-library files, templates, skills, references, or archived plans.

Rules for maintaining the `ai/` documents:

- keep the role of each file distinct; do not collapse architecture, code style, design, documentation ownership, execution, planning, release workflow, review guidance, testing guidance, workflow guidance, and learnings into one document
- keep AI instruction markdown files under `ai/` by default; `AGENTS.md` is the only standing exception
- update the relevant `ai/` file in the same change when architecture, code-style expectations, design intent, documentation ownership, durable engineering guidance, release workflow, review/security review guidance, testing/validation guidance, workflow guidance, or an execution plan materially changes
- keep `ai/TASK_LIBRARY.md` as a catalog, not standing policy; task sections may include procedural starters, but durable rules belong in the best owning AI document
- treat the listed task names in `ai/TASK_LIBRARY.md` as reusable commands, following `ai/TASK_LIBRARY.md` for exact-match, placeholder, heading-search, and ambiguity rules
- keep detailed examples, templates, historical explanations, and deep references in `ai/templates/` or `ai/references/` instead of the standing top-level AI files
- write AI-guidance changes as current-state rules; route any still-useful historical context using this guide
- keep repo-local skills narrow and workflow-oriented; use them to accelerate repeated entry tasks or focused triage, not to replace the owner guides
- create `.agents/plugins/marketplace.json` and a plugin bundle only when a workflow needs Codex plugin distribution or install-time discovery; keep ordinary reusable starters in `ai/TASK_LIBRARY.md`
- keep standing code-style, testing, review, and documentation guidance in their focused owning files instead of redistributing it across task starters or workflow docs
- when a repo-local skill wraps a workflow owned by another guide, update the skill and the owning guide together if that workflow changes
- when AI instruction files accumulate overlap, compact them by moving duplicated guidance into the single best owning file and updating cross-references in the same change
- archive executed `ai/plans/active/PLAN_*.md` files under `ai/archive/` as part of the release cleanup once that work has been released
- treat `ai/ARCHITECTURE.md`, `ai/DESIGN.md`, and `ai/LEARNINGS.md` as descriptive guidance, not executable spec authority
- if an interrupted tool or IDE run leaves an `ai/` document incomplete, finish it or clearly mark the gaps instead of leaving misleading partial content
- when moving or renaming AI documents, update references in `AGENTS.md` and other `ai/` files in the same change

## Change-Type Routing

### Architecture, design, or AI guidance change

- update the relevant owner file under `ai/`
- keep `AGENTS.md` aligned when the role or maintenance rules for `ai/` documents change
- do not update `README.md` unless the human-facing contract or project description changed

### Public API change

Update all affected artifacts in the same change:

- controller or service implementation
- integration tests
- REST Docs tests and Asciidoc pages when public behavior is documented there
- approved OpenAPI baseline if the contract intentionally changed
- HTTP example files under `src/manualTests/resources/http/`
- `README.md` if the supported contract changed
- `CHANGELOG.md` only when the change is being released

### Internal refactor with no contract change

- keep existing specs green without unnecessary contract edits
- avoid changing OpenAPI, README, or HTTP examples unless behavior actually changed
- prefer renames and moves that reduce exceptions in naming and packaging

### Setup or environment change

- update `SETUP.md` for human setup, tool, and troubleshooting changes
- update `ai/ENVIRONMENT_QUICK_REF.md` when AI-facing command-wrapper guidance changes
- only touch `README.md` or `AGENTS.md` when the high-level contract or rules changed, not for walkthrough duplication

### Roadmap change

- update `ROADMAP.md`
- keep `ROADMAP.md` `## Current Project State` aligned with the active release phase, breaking-change policy, and next target version whenever roadmap sequencing or release targeting changes
- use `ROADMAP.md` status values for active-work state; if checkbox items appear, treat `[x]` as selected for active planning or development, not as completed history
- remove completed items instead of archiving them elsewhere
- do not recreate a second human history file; released history belongs in `CHANGELOG.md`

## Alignment Rules

- update overlapping human-facing and AI-facing docs in the same change
- use `### AI Document Maintenance` in this guide for task-library files, skills, templates, references, top-level AI guides, and archived plans
- keep setup detail out of planning, workflow, and release guides
- keep active or selected work in `ROADMAP.md` and released history in `CHANGELOG.md`
- keep REST Docs AsciiDoc files formatter-managed; write unordered lists with explicit AsciiDoc marker depth (`*`, `**`) so IntelliJ formatting cannot flatten indentation-only nesting
- keep migration SQL documentation and examples aligned with hand-formatted Flyway scripts; do not normalize migration SQL through IntelliJ reformatting when documenting schema changes

## Common Routing

- public behavior change: update the governing spec artifacts first, then the published contract artifacts they drive
- human-facing AI collaboration workflow change: update `WORKING_WITH_AI.md`, and update overlapping AI-facing guides in the same change when the underlying repository workflow also changed
- workflow or AI-guidance change: update the owning AI guide first; touch `AGENTS.md` only when the AI-document set or maintenance rules changed
- repo-local skill change: update the skill plus the owning AI guide when the skill wraps a workflow whose rules changed
- local command-wrapper guidance for AI agents: update `ai/ENVIRONMENT_QUICK_REF.md`, and update `SETUP.md` only when human setup or troubleshooting behavior changed
- setup or tooling change: update `SETUP.md`, not `README.md`, `AGENTS.md`, or workflow guides unless their inventories or high-level rules changed
- plan creation or material plan revision: update the concrete `ai/plans/active/PLAN_*.md` file and `ROADMAP.md` together so active work points to the plan path and current status
- roadmap reprioritization: update `ROADMAP.md`, and keep `## Current Project State` aligned when the active release phase, breaking-change policy, or next target version changes
- released history: update `CHANGELOG.md`

## Cross-References

- use `ai/TESTING.md` for required validation once the right artifact set is identified
- use `ai/REVIEWS.md` for contradiction and drift checks before finalizing doc-heavy changes
