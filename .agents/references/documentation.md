# Documentation Guide For AI Agents

`.agents/references/documentation.md` owns artifact routing and cross-file alignment for repository docs and AI guidance.

Use this file to decide which artifact moves when behavior, setup, workflow, roadmap, release, or AI guidance changes.
Other guides should link here instead of re-listing ownership rules.

Entry points:

- artifact-location lookup only: read `## Artifact Ownership`
- AI-document edits: read `## Artifact Ownership` and `### AI Document Maintenance`
- documentation changes: continue into change-type routing, alignment rules, and common routing as needed

## Artifact Ownership

Update the artifact that owns the truth being changed:

- runtime behavior and public API contract: executable tests, `src/docs/asciidoc/`, `src/manualTests/http/examples/`, `src/manualTests/http/suites/`, `src/test/resources/openapi/approved-openapi.json`, and `README.md`
- human-facing guide for developers using AI through the application lifecycle: `WORKING_WITH_AI.md`
- AI repository rules and AI-document inventory: `AGENTS.md`
- repository knowledge layout and file ownership: this guide, especially `## Artifact Ownership` and `### AI Document Maintenance`
- AI local command wrapper shortcut: `.agents/references/environment-quick-ref.md`
- local setup, tools, troubleshooting, and onboarding: `SETUP.md`
- contributor workflow and maintainer expectations: `CONTRIBUTING.md`
- active planned work: `ROADMAP.md`
- released history: `CHANGELOG.md`
- commit message template and AI commit rules: `.gitmessage`
- planning process and plan-file shape: `.agents/references/planning.md`
- whole active-plan execution across milestones: `.agents/references/plan-execution.md`
- ad hoc task and single-milestone execution: `.agents/references/execution.md`
- branch, worktree, delegation, worker-log, integration, and remote-handoff mechanics: `.agents/references/workflow.md`; detailed delegated-work mechanics: `.agents/references/workflow-delegated-plan.md` and `.agents/references/workflow-coordinated-plans.md`
- release sequencing and tagging: `.agents/references/releases.md`; detailed release checklist and artifact verification: `.agents/references/release-checklist.md` and `.agents/references/release-artifact-verification.md`
- reusable task starter skill: `.agents/skills/repo-task/`; `references/spec.md` owns the dispatcher, index, and task schema, and each task file owns its title, placeholders, and task text
- repo-local reusable workflow wrappers: `.agents/skills/`; Codex plugin marketplace configuration: `.agents/plugins/marketplace.json` when intentionally introducing a repo-scoped plugin
- compact codebase map, structural guidance, and business feature ownership: `docs/ARCHITECTURE.md`; deeper references: `.agents/references/`
- historical AI-analysis reports, evaluations, comparisons, and retired specs: `.agents/archive/`
- product and contract direction: `docs/DESIGN.md`
- durable repo-wide lessons: `.agents/references/LEARNINGS.md`
- edit-shaping rules: `.agents/references/code-style.md`
- validation scope and commands: `.agents/references/testing.md`
- review and security-review expectations: `.agents/references/reviews.md`

If ownership is unclear, decide that before editing multiple docs.

### AI Document Maintenance

Load this section on demand before changing `AGENTS.md`, `.agents/references/*.md`, task-skill files, templates, skills, references, or archived plans or reports.

Rules for maintaining the `.agents/` documents:

- keep the role of each file distinct; do not collapse architecture, code style, design, documentation ownership, execution, planning, release workflow, review guidance, testing guidance, workflow guidance, and learnings into one document
- keep AI instruction markdown files under `.agents/references/` by default; `AGENTS.md` is the only standing exception
- update the relevant `.agents/references/` file in the same change when architecture, code-style expectations, design intent, documentation ownership, durable engineering guidance, release workflow, review/security review guidance, testing/validation guidance, workflow guidance, or an execution plan materially changes
- keep `.agents/skills/repo-task/` as a task dispatcher and task-reference store, not standing policy; task files may include procedural starters, but durable rules belong in the best owning AI document
- treat the task files listed in `.agents/skills/repo-task/references/index.md` as reusable commands, following `.agents/skills/repo-task/references/spec.md` for exact-slug, ambiguous-request, placeholder, index, and task-schema rules
- keep current detailed examples, templates, and deep references in `.agents/templates/` or `.agents/references/` instead of the standing top-level AI files
- keep retired report-like AI analysis artifacts under `.agents/archive/` instead of `.agents/references/`
- write AI-guidance changes as current-state rules; route any still-useful historical context using this guide
- keep repo-local skills narrow and workflow-oriented; use them to accelerate repeated entry tasks or focused triage, not to replace the owner guides
- create `.agents/plugins/marketplace.json` and a plugin bundle only when a workflow needs Codex plugin distribution or install-time discovery; keep ordinary reusable starters in `.agents/skills/repo-task/`
- keep standing code-style, testing, review, and documentation guidance in their focused owning files instead of redistributing it across task starters or workflow docs
- when a repo-local skill wraps a workflow owned by another guide, update the skill and the owning guide together if that workflow changes
- when AI instruction files accumulate overlap, compact them by moving duplicated guidance into the single best owning file and updating cross-references in the same change
- archive executed `.agents/plans/PLAN_*.md` files under `.agents/archive/` as part of the release cleanup once that work has been released
- archive retired AI-guidance reports, evaluations, comparisons, and similar analysis artifacts under `.agents/archive/`
- treat `docs/ARCHITECTURE.md`, `docs/DESIGN.md`, and `.agents/references/LEARNINGS.md` as descriptive guidance, not executable spec authority
- if an interrupted tool or IDE run leaves an `.agents/` document incomplete, finish it or clearly mark the gaps instead of leaving misleading partial content
- when moving or renaming AI documents, update references in `AGENTS.md` and other `.agents/` files in the same change
- when adding or moving repository knowledge files, use this guide's artifact ownership rules first and update this guide when no existing owner fits

## Change-Type Routing

### Architecture, design, or AI guidance change

- update the relevant owner file under `.agents/references/`
- keep `AGENTS.md` aligned when the role or maintenance rules for `.agents/` documents change
- do not update `README.md` unless the human-facing contract or project description changed

### Public API change

Update all affected artifacts in the same change:

- controller or service implementation
- integration tests
- REST Docs tests and Asciidoc pages when public behavior is documented there
- approved OpenAPI baseline if the contract intentionally changed
- HTTP example files under `src/manualTests/http/examples/` and manual-regression HTTP Client suite scripts under `src/manualTests/http/suites/`
- `README.md` if the supported contract changed
- `CHANGELOG.md` only when the change is being released

### Internal refactor with no contract change

- keep existing specs green without unnecessary contract edits
- avoid changing OpenAPI, README, or HTTP examples unless behavior actually changed
- prefer renames and moves that reduce exceptions in naming and packaging

### Setup or environment change

- update `SETUP.md` for human setup, tool, and troubleshooting changes
- update `.agents/references/environment-quick-ref.md` when AI-facing command-wrapper guidance changes
- only touch `README.md` or `AGENTS.md` when the high-level contract or rules changed, not for walkthrough duplication

### Roadmap change

- update `ROADMAP.md`
- keep `ROADMAP.md` `## Current Project State` aligned with the active release phase, breaking-change policy, and next target version whenever roadmap sequencing or release targeting changes
- use `ROADMAP.md` status values for active-work state; if checkbox items appear, treat `[x]` as selected for active planning or development, not as completed history
- remove completed items instead of archiving them elsewhere
- do not recreate a second human history file; released history belongs in `CHANGELOG.md`

## Alignment Rules

- update overlapping human-facing and AI-facing docs in the same change
- use `### AI Document Maintenance` in this guide for task-skill files, skills, templates, references, top-level AI guides, and archived plans
- keep setup detail out of planning, workflow, and release guides
- keep active or selected work in `ROADMAP.md` and released history in `CHANGELOG.md`
- keep REST Docs AsciiDoc files formatter-managed; write unordered lists with explicit AsciiDoc marker depth (`*`, `**`) so IntelliJ formatting cannot flatten indentation-only nesting
- keep migration SQL documentation and examples aligned with hand-formatted Flyway scripts; do not normalize migration SQL through IntelliJ reformatting when documenting schema changes

## Common Routing

- public behavior change: update the governing spec artifacts first, then the published contract artifacts they drive
- human-facing AI collaboration workflow change: update `WORKING_WITH_AI.md`, and update overlapping AI-facing guides in the same change when the underlying repository workflow also changed
- workflow or AI-guidance change: update the owning AI guide first; touch `AGENTS.md` only when the AI-document set or maintenance rules changed
- repo-local skill change: update the skill plus the owning AI guide when the skill wraps a workflow whose rules changed
- local command-wrapper guidance for AI agents: update `.agents/references/environment-quick-ref.md`, and update `SETUP.md` only when human setup or troubleshooting behavior changed
- setup or tooling change: update `SETUP.md`, not `README.md`, `AGENTS.md`, or workflow guides unless their inventories or high-level rules changed
- plan creation or material plan revision: update the concrete `.agents/plans/PLAN_*.md` file and `ROADMAP.md` together so active work points to the plan path and current status
- roadmap reprioritization: update `ROADMAP.md`, and keep `## Current Project State` aligned when the active release phase, breaking-change policy, or next target version changes
- released history: update `CHANGELOG.md`

## Cross-References

- use `.agents/references/testing.md` for required validation once the right artifact set is identified
- use `.agents/references/reviews.md` for contradiction and drift checks before finalizing doc-heavy changes
