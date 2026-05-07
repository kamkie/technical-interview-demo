# Plan: Workflow Selection Soft Consolidation

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Planning |
| Status | Needs Input |

## Summary
- Implement Variant 1 from `C:\Users\kamki\AppData\Roaming\JetBrains\IntelliJIdea2026.1\scratches\what_about_moving_workflow_selection_to.md`.
- Move execution-mode selection from `ai/WORKFLOW.md` into `ai/PLAN.md`, while keeping `ai/EXECUTION.md` whole and retaining `ai/WORKFLOW.md` as a temporary thin compatibility pointer.
- Success means planning is the single place agents decide between `Linear Plan`, `Single-Plan Fanout`, and `Multi-Plan Fanout`, with no broad workflow-router load required for normal plan creation.

## Scope
- In scope:
  - move the mode-selection gate and "when to stay linear" decision rules into `ai/PLAN.md`
  - reduce `ai/WORKFLOW.md` to a short legacy pointer that routes mode choice to `ai/PLAN.md` and fanout mechanics to the existing workflow references
  - update live AI guides, prompt metadata/bodies, templates, repo-local skills, and human-facing AI workflow docs so they no longer treat `ai/WORKFLOW.md` as the mode-selection owner
  - preserve the existing fanout reference files without moving their detailed mechanics
  - update the AI guideline evaluation report with before/after read-set measurements
  - record the implemented AI-guidance change in `CHANGELOG.md` during execution
- Out of scope:
  - splitting `ai/EXECUTION.md`
  - deleting `ai/WORKFLOW.md`
  - introducing `ai/PLAN_EXECUTION.md`, `ai/MILESTONE_EXECUTION.md`, or a multi-plan coordinator template
  - changing application source, tests, REST Docs, OpenAPI, HTTP examples, setup behavior, release automation, or public contract behavior
  - rewriting archived plans beyond targeted reference updates required by moved files

## Current State
- `ai/WORKFLOW.md` owns mode selection, branch/worktree topology, worker log schema, delegation quality, coordinator ownership, task slicing, and reporting expectations.
- `ai/PLAN.md` already tells plan authors to prefer `Linear Plan` and when to use fanout, but it still points to `ai/WORKFLOW.md` for the full mode-selection and execution-topology rules.
- `ai/EXECUTION.md` loads `ai/WORKFLOW.md` when mode selection changes shared tracking artifacts.
- `ai/templates/PLAN_TEMPLATE.md`, `ai/PROMPTS.md`, prompt bodies, repo-local skills, and human-facing workflow docs contain live references to workflow modes and workflow ownership.
- The scratch analysis estimates Variant 1 reduces the workflow/exec standing inventory from about 44,800 to 38,188 characters, while mode selection can use `AGENTS.md` plus `ai/PLAN.md` instead of `AGENTS.md` plus `ai/WORKFLOW.md` plus `ai/EXECUTION.md`.

## Requirement Gaps And Open Questions
- Which variant should be implemented?
  Fallback: do not execute this plan until the user selects Variant 1 over Variant 2 and Variant 3.
- Should the retained `ai/WORKFLOW.md` pointer be kept indefinitely or scheduled for deletion after one release?
  Fallback: keep it as a compatibility pointer through at least one release cycle, then reassess.
- Should prompt titles that currently include mode names be renamed in Variant 1?
  Fallback: keep prompt titles stable unless prompt bodies would otherwise contradict the new owner model; route prompt body wording to `ai/PLAN.md`.
- Should this variant add a CI guard that enforces mode-name ownership?
  Fallback: use targeted `rg` validation only; a hard CI guard is deferred because Variant 1 intentionally keeps a `WORKFLOW.md` compatibility pointer.

## Locked Decisions And Assumptions
- This plan is an alternative candidate, not approved implementation work.
- If selected, this variant preserves the current file set except for content changes; it does not add or delete top-level AI owner guides.
- `ai/PLAN.md` becomes the owner of mode-selection rules.
- `ai/WORKFLOW.md` remains as a thin compatibility pointer and may still mention mode names only to route readers to `ai/PLAN.md`.
- Fanout mechanics stay in `ai/references/WORKFLOW_SINGLE_PLAN_FANOUT.md` and `ai/references/WORKFLOW_MULTI_PLAN_FANOUT.md`.
- Public API behavior and application runtime behavior remain unchanged.

## Execution Mode Fit
- Recommended mode: `Linear Plan`.
- Reason: the work is a tightly coupled AI-guidance ownership change across shared docs, prompts, and templates; splitting would create high coordination cost and cross-reference drift.
- Coordinator-owned files:
  - `AGENTS.md`
  - `WORKING_WITH_AI.md`
  - top-level `ai/*.md`
  - `ai/prompts/index.json`
  - `ai/prompts/bodies/*.md`
  - `ai/templates/PLAN_TEMPLATE.md`
  - `ai/skills/**/SKILL.md`
  - `ai/references/AI_GUIDELINES_POST_COMPACTION_EVALUATION.md`
  - `ROADMAP.md`
  - `CHANGELOG.md`
- Candidate worker boundaries: none recommended. If the user explicitly requests delegation, use read-only reviewers after the coordinator completes the cross-reference sweep.

## Affected Artifacts
- AI planning and workflow guides:
  - `ai/PLAN.md`
  - `ai/WORKFLOW.md`
  - `ai/EXECUTION.md`
  - `ai/DOCUMENTATION.md`
  - `ai/PROMPTS.md`
- AI entry points and human-facing AI docs:
  - `AGENTS.md`
  - `WORKING_WITH_AI.md`
- Prompt and template artifacts:
  - `ai/prompts/index.json`
  - workflow-related prompt bodies under `ai/prompts/bodies/`
  - `ai/templates/PLAN_TEMPLATE.md`
- Repo-local skills:
  - any `ai/skills/**/SKILL.md` file that tells agents where to choose or execute workflow modes
- Measurement and tracking:
  - `ai/references/AI_GUIDELINES_POST_COMPACTION_EVALUATION.md`
  - `ROADMAP.md`
  - `CHANGELOG.md`
- Not affected:
  - application Java/Kotlin source
  - REST Docs AsciiDoc
  - approved OpenAPI baseline
  - HTTP examples
  - setup or deployment docs unless a live AI-workflow reference requires a narrow pointer update

## Execution Milestones
### Milestone 1: Baseline And Reference Inventory
- goal: measure current guidance sizes and identify every live mode-selection reference before editing.
- owned files or packages:
  - this plan's `Validation Results`
  - optional scratch notes under `ai/tmp/` if needed
- shared files that a `Single-Plan Fanout` worker must leave to the coordinator:
  - all planned owner-guide edits
- context required before execution:
  - `AGENTS.md`, `ai/PLAN.md`, `ai/DOCUMENTATION.md`, `ai/WORKFLOW.md`, `ai/EXECUTION.md`, `ai/PROMPTS.md`, `ai/TESTING.md`, `ai/REVIEWS.md`, this plan, and the scratch source file named in `Summary`
- behavior to preserve:
  - no current workflow rule is deleted before its new owner is identified
  - archived plans remain historical unless a moved-path reference would become actively misleading
- exact deliverables:
  - current size/read-set baseline for affected standing guides
  - `rg` inventory of live references to `Linear Plan`, `Single-Plan Fanout`, `Multi-Plan Fanout`, and `ai/WORKFLOW.md`, excluding `ai/archive/**` by default
  - final owner map for every rule currently in `ai/WORKFLOW.md`
- validation checkpoint:
  - `git diff --check`
  - reference inventory reviewed against `ai/DOCUMENTATION.md`
- commit checkpoint:
  - `docs: inventory workflow selection soft consolidation`

### Milestone 2: Move Mode Selection To Planning
- goal: make `ai/PLAN.md` the authoritative mode-selection owner while preserving the existing execution and fanout mechanics.
- owned files or packages:
  - `ai/PLAN.md`
  - `ai/WORKFLOW.md`
  - `ai/EXECUTION.md`
  - `AGENTS.md` only if the lifecycle owner map needs a compact pointer update
- shared files that a `Single-Plan Fanout` worker must leave to the coordinator:
  - `ai/PLAN.md`, `ai/WORKFLOW.md`, and `ai/EXECUTION.md`
- context required before execution:
  - `ai/PLAN.md`, `ai/WORKFLOW.md`, `ai/EXECUTION.md`, `ai/DOCUMENTATION.md`, and this milestone
- behavior to preserve:
  - default mode remains `Linear Plan`
  - fanout still requires explicit user approval
  - fanout references remain the detailed mechanics for non-default modes
- exact deliverables:
  - `ai/PLAN.md` contains the complete mode-selection gate and concise fanout-reference routing
  - `ai/WORKFLOW.md` is reduced to a temporary compatibility pointer and no longer owns selection logic
  - `ai/EXECUTION.md` points to `ai/PLAN.md` for mode selection and to the fanout references only through the selected plan mode
- validation checkpoint:
  - targeted `rg` confirms no duplicate full mode-selection gate remains outside `ai/PLAN.md`
  - `git diff --check -- AGENTS.md ai/PLAN.md ai/WORKFLOW.md ai/EXECUTION.md`
- commit checkpoint:
  - `docs: move workflow selection into planning guide`

### Milestone 3: Sweep Live Prompts, Templates, Skills, And Human Docs
- goal: align every live workflow entry point with the new planning-owned mode-selection model.
- owned files or packages:
  - `WORKING_WITH_AI.md`
  - `ai/PROMPTS.md`
  - `ai/prompts/index.json`
  - workflow prompt bodies under `ai/prompts/bodies/`
  - `ai/templates/PLAN_TEMPLATE.md`
  - affected `ai/skills/**/SKILL.md`
- shared files that a `Single-Plan Fanout` worker must leave to the coordinator:
  - `CHANGELOG.md`, `ROADMAP.md`, and this plan
- context required before execution:
  - `WORKING_WITH_AI.md`, `ai/PROMPTS.md`, prompt bodies found by Milestone 1, `ai/templates/PLAN_TEMPLATE.md`, affected skill entrypoints, and this milestone
- behavior to preserve:
  - prompt names remain invokable unless intentionally renamed with index/body updates in the same change
  - plan template still captures execution-mode fit and coordinator-owned files
  - human-facing AI docs remain concise and do not duplicate the full planning guide
- exact deliverables:
  - live prompt bodies route mode choice through `ai/PLAN.md`
  - template wording matches the new owner model
  - affected skills point to the new owner without embedding duplicate policy
  - `CHANGELOG.md` records the AI-guidance ownership change under `## [Unreleased]`
- validation checkpoint:
  - prompt loader smoke checks for any prompt titles changed or touched
  - `rg` over active docs and prompt bodies for stale `ai/WORKFLOW.md` ownership wording
  - `git diff --check`
- commit checkpoint:
  - `docs: align workflow selection entry points`

### Milestone 4: Measurement, Review, And Final Validation
- goal: prove the soft consolidation achieved the intended read-set shift without losing owner clarity.
- owned files or packages:
  - `ai/references/AI_GUIDELINES_POST_COMPACTION_EVALUATION.md`
  - this plan
  - `ROADMAP.md`
- shared files that a `Single-Plan Fanout` worker must leave to the coordinator:
  - all final validation and release-history artifacts
- context required before execution:
  - `ai/references/AI_GUIDELINES_POST_COMPACTION_EVALUATION.md`, `ai/TESTING.md`, `ai/REVIEWS.md`, `ai/DOCUMENTATION.md`, changed docs, and this milestone
- behavior to preserve:
  - the report remains on demand
  - the plan remains active until implementation is released
- exact deliverables:
  - refreshed before/after read-set estimates for Variant 1
  - final `Validation Results` in this plan
  - `ROADMAP.md` status updated to `Implemented` after execution, if selected and completed
- validation checkpoint:
  - `git diff --check`
  - `./build.ps1 build`
  - manual documentation/process review using `ai/REVIEWS.md`
- commit checkpoint:
  - `docs: validate workflow selection soft consolidation`

## Edge Cases And Failure Modes
- A compatibility `ai/WORKFLOW.md` pointer can grow back into a second owner. Keep it short and search for duplicated gate wording.
- Prompt titles can drift from prompt bodies if mode vocabulary changes. Smoke-check every touched prompt title.
- `AGENTS.md` can become larger if this plan moves too much detail into the default entry point. Keep only a compact pointer there.
- Archived plans will continue to mention the old owner model. Exclude them from active drift checks unless a search is explicitly historical.
- This variant creates less conceptual cleanup than Variant 2 or Variant 3; the retained pointer is intentional, not incomplete work.

## Validation Plan
- Run targeted reference searches excluding `ai/archive/**`.
- Run prompt loader smoke checks for touched or renamed prompt titles.
- Run `git diff --check`.
- Run `./build.ps1 build`; the lightweight-only shortcut is acceptable for documentation-only AI-guidance changes if the wrapper chooses it.
- Manually review changed docs with `ai/DOCUMENTATION.md` and `ai/REVIEWS.md` to confirm owner clarity and no contradictory process guidance.

## Testing Strategy
- Unit tests: not applicable; no application logic changes.
- Integration tests: not applicable; no runtime, database, HTTP, OAuth, or service behavior changes.
- Contract tests: not applicable; no REST Docs, OpenAPI, HTTP examples, README public contract, or public API behavior changes.
- Smoke or benchmark tests: not applicable; no runtime or performance behavior changes.
- Documentation verification: required through targeted search, prompt smoke checks when prompts move, `git diff --check`, wrapper build, and manual owner-alignment review.

## Better Engineering Notes
- Variant 1 is the lowest-risk migration path and can be used as an intermediate release-cycle step before deciding whether `ai/WORKFLOW.md` can be deleted.
- Do not optimize only for raw total characters; measure practical read sets for planning, mode selection, and milestone execution.
- If implementation repeatedly needs to preserve detailed workflow rules in the pointer, stop and reconsider Variant 2 instead of growing `ai/WORKFLOW.md` again.

## Validation Results
- 2026-05-07 plan creation:
  - Read the scratch recommendation file at `C:\Users\kamki\AppData\Roaming\JetBrains\IntelliJIdea2026.1\scratches\what_about_moving_workflow_selection_to.md` and converted Variant 1 into this option plan.
  - Updated `ROADMAP.md` Intake with the `Workflow-selection soft consolidation` candidate entry.
  - Ran `git diff --check`; passed.
  - Ran `rg -n "Workflow-selection|PLAN_workflow_selection|Variant 1|Variant 2|Variant 3" ROADMAP.md ai -g "PLAN_workflow_selection_*.md"`; passed and confirmed the three roadmap entries plus variant plan references.
  - Ran `./build.ps1 build`; passed through the lightweight-only shortcut with changed files limited to the three workflow-selection plans and `ROADMAP.md`, so Gradle was skipped.
  - Implementation remains blocked until the user selects exactly one workflow-selection variant.

## User Validation
- Review `ai/PLAN.md` and confirm an agent can choose between linear and fanout modes without opening `ai/WORKFLOW.md`.
- Review the retained `ai/WORKFLOW.md` pointer and confirm it is clearly transitional and not a second rule owner.
- Run or inspect prompt-loader output for touched workflow prompts and confirm they route mode choice through planning.

## Required Content Checklist
- Behavior changing: AI workflow selection ownership moves from `ai/WORKFLOW.md` to `ai/PLAN.md`.
- Roadmap tracking: `ROADMAP.md` / `Intake` / `Workflow guidance restructure variants` / Variant 1.
- Out of scope: execution-guide split, workflow-guide deletion, multi-plan template, public API behavior, setup behavior, release automation.
- Governing artifacts: scratch recommendation file, `ai/PLAN.md`, `ai/WORKFLOW.md`, `ai/EXECUTION.md`, `ai/DOCUMENTATION.md`, prompt/template/skill artifacts.
- Likely files: named in `Affected Artifacts`.
- Compatibility promises: default mode stays `Linear Plan`, fanout requires user approval, fanout references stay on demand, public contract unchanged.
- Risks: duplicate owners, stale prompt references, `AGENTS.md` growth, archived-plan search noise.
- Requirement gaps: blocked until the user selects this variant; retained pointer lifetime and prompt title rename policy are open with fallbacks.
- Execution mode: `Linear Plan`.
- Coordinator-owned files: all shared AI owner guides, prompt index/bodies, templates, skills, roadmap, changelog, and evaluation report.
- Per-milestone context: named explicitly in each milestone.
- Specs and docs: AI guidance and human-facing AI workflow docs only; no app specs or public contract artifacts.
- Validation: targeted searches, prompt smoke checks when needed, `git diff --check`, `./build.ps1 build`, manual owner-alignment review.
- User verification: inspect planning guide, workflow pointer, and prompt output.
