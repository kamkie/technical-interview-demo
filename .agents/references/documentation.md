# Documentation Guide For AI Agents

`.agents/references/documentation.md` owns artifact routing and cross-file alignment for repository docs and AI guidance.
`.agents/references/references-rules.md` owns the standing rules for `.agents/references/*.md` documents themselves.

Use this file to decide which artifact moves when behavior, setup, workflow, roadmap, release, or AI guidance changes.
Other guides should link here instead of re-listing ownership rules.

Entry points:

- artifact-location lookup only: read `## Artifact Ownership`
- AI-document edits: read `## Artifact Ownership` and `### AI Document Maintenance`; for `.agents/references/*.md` edits, also read `.agents/references/references-rules.md`
- documentation changes: continue into change-type routing, alignment rules, and common routing as needed

## Artifact Ownership

Update the artifact that owns the truth being changed:

- runtime behavior and public API contract: executable tests, `src/docs/asciidoc/`, `src/test/resources/openapi/approved-openapi.json`, and `README.md`
- generated import-ready backend contract, external skill references, OpenAPI source snapshot, and integration guidance for AI agents working in a separate first-party frontend repository: `docs/FRONTEND_AI_CONTRACT.md`
- proposed or accepted durable architecture, workflow, contract-policy, security, documentation-ownership, or repository-process decisions: `docs/decisions/*.md`; template: `docs/decisions/ADR_TEMPLATE.md`
- product intent, users, goals, non-goals, requirements, acceptance criteria, and product-scope open questions for broad or ambiguous user-facing work: `docs/requirements/*.md`; template: `docs/requirements/PRD_TEMPLATE.md`
- standalone behavior, contract, acceptance criteria, and validation mapping when executable specs or published contract docs do not already define the behavior clearly enough: `docs/specs/*.md`; template: `docs/specs/SPEC_TEMPLATE.md`
- human-facing documentation index: `docs/README.md`
- human-facing application lifecycle and artifact-routing summary derived from AI owner guides and roadmap rules: `docs/DEVELOPMENT_LIFECYCLE.md`
- human-facing guide for developers using AI through the application lifecycle: `docs/WORKING_WITH_AI.md`
- human-facing local development commands, CI reproduction, and local troubleshooting: `docs/LOCAL_DEVELOPMENT.md`
- human-facing deployment and runtime operations runbooks: `docs/OPERATIONS.md`
- application development lifecycle phase model, activity vocabulary, loops, triggers, and owner-guide mapping: `.agents/references/application-lifecycle.md`
- AI repository rules, spec priority, required spec-update policy, working AI context, Documents Map, completion rules, and integration/release invariants: `AGENTS.md`
- repository knowledge layout and artifact ownership: this guide, especially `## Artifact Ownership` and `### AI Document Maintenance`
- rules for creating, editing, compacting, moving, and retiring `.agents/references/*.md`: `.agents/references/references-rules.md`
- AI local command wrapper shortcut: `.agents/references/command-wrapper.md`
- local environment setup, dev-container setup, local shell prerequisites, `.env`, IDE baseline, local PostgreSQL environment, and optional Git commit-template setup: `SETUP.md`
- local development commands, wrapper behavior for humans, local app run workflow, local validation loops, CI reproduction, documentation health workflow, local contract/security/benchmark workflows, and local troubleshooting: `docs/LOCAL_DEVELOPMENT.md`
- contributor workflow and maintainer expectations: `CONTRIBUTING.md`
- user-facing documentation health checks, local link checks, stable-version agreement checks, and generated-contract summary drift checks: `scripts/docs/audit-docs.ps1`
- active planned work: `ROADMAP.md`
- released history: `CHANGELOG.md`
- commit message template and AI commit rules: `.gitmessage`
- planning process, plan-file rules, fill guidance, and readiness review shape: `.agents/references/planning.md`; canonical plan skeleton: `.agents/plans/PLAN_TEMPLATE.md`
- whole active-plan execution across plan tasks: `.agents/references/plan-execution.md`
- ad hoc task and single-plan-task execution: `.agents/references/execution.md`
- branch, worktree, delegation, multi-agent state, sidecar, integration, and remote-handoff mechanics: `.agents/references/workflow.md`
- release sequencing and tagging: `.agents/references/releases.md`; detailed release checklist and artifact verification: `.agents/references/release-checklist.md` and `.agents/references/release-artifact-verification.md`
- deployment, post-release verification, rollback, incident response, hotfix, patch, backport, and deprecation routing after a release artifact exists: `.agents/references/operations.md`
- standalone repository task prompts, catalog, and task loading rules: `.agents/tasks/README.md`; prompt files live under `.agents/tasks/` and must add task-specific instructions that are not thin wrappers around an existing domain guide
- repo-local reusable workflow wrappers: `.agents/skills/`; Codex plugin marketplace configuration: `.agents/plugins/marketplace.json` when intentionally introducing a repo-scoped plugin
- durable multi-agent workflow state: `.agents/context/handoffs/`, `.agents/context/workers/`, `.agents/context/reviews/`, `.agents/context/verifications/`, and `.agents/context/specialists/`; workflow state file shape and role ownership live in `.agents/references/workflow.md`
- retired multi-agent workflow state that no active agent needs: `.agents/archive/context/`; archive timing and role-directory shape live in `.agents/references/workflow.md`
- AI-facing architecture guidance, structural placement, detailed codebase map, and business feature ownership: `.agents/references/architecture.md`
- historical AI-analysis reports, evaluations, comparisons, and retired specs: `.agents/archive/`
- product and contract direction: `docs/DESIGN.md`
- durable learning decision rules, learning-loop routing, and criteria for recording lessons: `.agents/references/learning-rules.md`
- durable repo-wide lesson storage: `docs/LEARNINGS.md`
- edit-shaping rules: `.agents/references/code-style.md`
- validation scope and commands: `.agents/references/testing.md`
- review and security-review expectations: `.agents/references/reviews.md`
- reviewer/operator HTTP convenience tools: `src/manualTests/http/examples/` and `src/manualTests/http/suites/`

If ownership is unclear, decide that before editing multiple docs.

### AI Document Maintenance

Load this section on demand before changing `AGENTS.md`, task prompts, templates, skills, archived plans or reports, or repository knowledge ownership.
Before changing `.agents/references/*.md`, also load `.agents/references/references-rules.md`.

Rules for maintaining the `.agents/` documents:

- use `.agents/references/references-rules.md` for all rules that govern `.agents/references/*.md` documents
- keep `.agents/tasks/` as standalone reusable prompts, not standing policy; durable rules belong in the best owning AI document
- keep `.agents/tasks/README.md` current when adding, renaming, moving, or removing task prompts
- keep task-prompt loading rules in `.agents/tasks/README.md`; use `AGENTS.md` only for the repo-level entry point
- do not keep task prompts that only say to follow a workflow already owned by `.agents/references/*.md`; invoke the matching domain guide directly instead
- outside `.agents/references/*.md`, write AI-guidance changes as current-state rules; route any still-useful historical context using this guide
- keep repo-local skills narrow and workflow-oriented; use them to accelerate repeated entry tasks or focused triage, not to replace domain guides
- keep skills tactical; they must reference owner guides for standing rules instead of becoming governance documents
- keep visible artifact-level provenance in generated ADRs, PRDs, and execution plans; the `## Provenance` section must name the creating AI agent or human author, creation date, source request, and generation context, and AI commit metadata is supplemental rather than a substitute
- create `.agents/plugins/marketplace.json` and a plugin bundle only when a workflow needs Codex plugin distribution or install-time discovery; keep ordinary reusable prompts in `.agents/tasks/`
- when a repo-local skill wraps a workflow owned by a domain guide, update the skill and that guide together if the workflow changes
- archive executed concrete `.agents/plans/PLAN_*.md` files under `.agents/archive/` as part of the release cleanup once that work has been released; never archive `.agents/plans/PLAN_TEMPLATE.md` as active work
- archive retired AI-guidance reports, evaluations, comparisons, and similar analysis artifacts under `.agents/archive/`
- treat `docs/DESIGN.md`, `.agents/references/architecture.md`, and `docs/LEARNINGS.md` as descriptive guidance, not executable spec authority
- when moving or renaming non-reference AI documents, update references in `AGENTS.md` and other `.agents/` files in the same change
- when adding or moving repository knowledge files, use this guide's artifact ownership rules first and update this guide when no existing owner fits

## Change-Type Routing

### Architecture, design, or AI guidance change

- update the relevant domain guide under `.agents/references/`
- keep `AGENTS.md` aligned when the role or maintenance rules for `.agents/` documents change
- do not update `README.md` unless the human-facing contract or project description changed

### Pre-planning artifact change

- use an ADR for durable architecture, workflow, contract-policy, security, documentation-ownership, or repository-process decisions that outlive one plan
- use a PRD only when broad or ambiguous user-facing work needs its own product intent, goals, non-goals, requirements, acceptance criteria, and open questions
- use a standalone spec only when behavior or contract truth is not already clear in executable specs, published contract docs, or the target plan
- keep ADRs, PRDs, and standalone specs optional for routine maintenance where the plan, tests, or existing docs already provide enough truth
- once a pre-planning artifact becomes active input to execution, keep the concrete plan and `ROADMAP.md` linked to it without copying the artifact's full rationale

### Public API change

Update all affected artifacts in the same change:

- controller or service implementation
- integration tests
- REST Docs tests and Asciidoc pages when public behavior is documented there
- approved OpenAPI baseline if the contract intentionally changed
- HTTP convenience files under `src/manualTests/http/examples/` and `src/manualTests/http/suites/` when reviewer or manual-regression workflows should reflect the behavior
- `README.md` if the supported contract changed
- `CHANGELOG.md` only when the change is being released

### Internal refactor with no contract change

- keep existing specs green without unnecessary contract edits
- avoid changing OpenAPI or README unless behavior actually changed
- prefer renames and moves that reduce exceptions in naming and packaging

### Setup, environment, or operations change

- update `SETUP.md` for human environment setup changes
- update `docs/LOCAL_DEVELOPMENT.md` for local command, local validation, CI reproduction, and local troubleshooting changes
- update `docs/OPERATIONS.md` for deployment contract, runtime, smoke, rollback, Kubernetes, Helm, monitoring, OAuth runtime setup, or operations troubleshooting changes
- update `.agents/references/command-wrapper.md` when AI-facing command-wrapper guidance changes
- only touch `README.md` or `AGENTS.md` when the high-level contract or rules changed, not for walkthrough duplication

### Roadmap change

- update `ROADMAP.md`
- keep `ROADMAP.md` `## Current Project State` aligned with the active release phase, breaking-change policy, and next target version whenever roadmap sequencing or release targeting changes
- route rows by lifecycle phase: rough capture to `## Conceptualization`, structured requirements to `## Analysis`, durable decisions to `## Decisions`, accept/defer/prioritize calls to `## Triage`, and plan-backed execution to `## Active Release Track` or `## Planned Work`
- honor `ROADMAP.md` `### When To Skip Pre-Planning Artifacts`; trivial or local-only work does not need a pre-planning row
- use `ROADMAP.md` status values for active-work state; if checkbox items appear, treat `[x]` as selected for active planning or development, not as completed history
- remove completed items instead of archiving them elsewhere
- do not recreate a second human history file; released history belongs in `CHANGELOG.md`

## Alignment Rules

- update overlapping human-facing and AI-facing docs in the same change
- use `### AI Document Maintenance` in this guide for task prompts, skills, templates, top-level AI guides, and archived plans; use `.agents/references/references-rules.md` for `.agents/references/*.md`
- keep setup detail out of planning, workflow, and release guides
- keep deployment and runtime runbook detail in `docs/OPERATIONS.md`, not in `SETUP.md`, `docs/LOCAL_DEVELOPMENT.md`, or AI workflow guides
- keep active or selected work in `ROADMAP.md` and released history in `CHANGELOG.md`
- keep REST Docs AsciiDoc files formatter-managed; write unordered lists with explicit AsciiDoc marker depth (`*`, `**`) so IntelliJ formatting cannot flatten indentation-only nesting
- keep migration SQL documentation and examples aligned with hand-formatted Flyway scripts; do not normalize migration SQL through IntelliJ reformatting when documenting schema changes

## Common Routing

- public behavior change: update the governing spec artifacts first, then the published contract artifacts they drive
- human-facing AI collaboration workflow change: update `docs/WORKING_WITH_AI.md`, and update overlapping AI-facing guides in the same change when the underlying repository workflow also changed
- workflow or AI-guidance change: update the matching domain guide first; follow `.agents/references/references-rules.md` for reference-document edits; touch `AGENTS.md` only when the AI-document set or maintenance rules changed
- separate frontend AI contract source change: update `docs/FRONTEND_AI_CONTRACT.md`, keep it subordinate to executable specs, REST Docs, OpenAPI, and `README.md`, and update `README.md`, `docs/WORKING_WITH_AI.md`, `AGENTS.md`, and this guide when its discoverability or ownership changes
- repo-local skill change: update the skill plus the matching domain guide when the skill wraps a workflow whose rules changed
- local command-wrapper guidance for AI agents: update `.agents/references/command-wrapper.md`, and update `docs/LOCAL_DEVELOPMENT.md` only when the human-facing local command workflow changed
- environment setup change: update `SETUP.md`, not `README.md`, `AGENTS.md`, or workflow guides unless their inventories or high-level rules changed
- local development workflow change: update `docs/LOCAL_DEVELOPMENT.md`, not `SETUP.md`, unless environment setup steps also changed
- deployment or runtime operations runbook change: update `docs/OPERATIONS.md`, not `SETUP.md` or `docs/LOCAL_DEVELOPMENT.md`, unless local environment or local command steps also changed
- plan creation or material plan revision: update the concrete `.agents/plans/PLAN_*.md` file and `ROADMAP.md` together so active work points to the plan path and current status; `.agents/plans/PLAN_TEMPLATE.md` is only the skeleton
- roadmap reprioritization: update `ROADMAP.md`, and keep `## Current Project State` aligned when the active release phase, breaking-change policy, or next target version changes
- post-release deployment or operations signal: use `.agents/references/operations.md` to route rollback, hotfix, patch, backport, deprecation, roadmap, learning, and validation ownership before editing implementation or release artifacts
- pre-planning decision record: create or update the relevant ADR under `docs/decisions/`; when an ADR becomes accepted, update the owning standing guidance, templates, roadmap entries, and human-facing docs that the decision changes
- broad user-facing product intent gap: create or update the relevant PRD under `docs/requirements/` only when goals, users, requirements, non-goals, acceptance criteria, or product questions need their own artifact before planning
- standalone behavior definition gap: create or update the relevant spec under `docs/specs/` only when executable specs, published contract docs, or the active plan do not already define the intended behavior clearly enough
- released history: update `CHANGELOG.md`

## Cross-References

- use `.agents/references/testing.md` for required validation once the right artifact set is identified
- use `.agents/references/reviews.md` for contradiction and drift checks before finalizing doc-heavy changes
- use `.agents/references/references-rules.md` for rules that govern `.agents/references/*.md` files
