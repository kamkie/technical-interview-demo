# Plan: AI Guidance Execution And Reusable Task Library Restructure

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Planning |
| Status | Ready |

## Summary
- Restructure the AI guidance set so planning, whole-plan execution, task or milestone execution, workflow coordination, and reusable task starters have distinct owners.
- Remove the current named mode vocabulary from live standing guides, prompt/task starters, templates, skills, and active plan guidance.
- Rename the planning guide to `ai/PLANNING.md` instead of `ai/PLANING.md`, move active execution plans under `ai/plans/active/`, and redesign the current prompt storage into a reusable task library.
- Success means an agent can create a plan, execute a whole plan, execute an ad hoc task or one plan milestone, delegate or integrate work, and load reusable task starters without opening stale or overlapping guidance.

## Scope
- In scope:
  - regenerate `ai/WORKFLOW.md` as the owner for branch, worktree, delegation, worker, and integration coordination only
  - regenerate `ai/EXECUTION.md` as the owner for ad hoc task execution and individual plan-milestone execution
  - create `ai/PLAN_EXECUTION.md` as the owner for executing a whole active plan across milestones
  - rename `ai/PLAN.md` to `ai/PLANNING.md`; do not use the misspelled `ai/PLANING.md`
  - move active plans from top-level `ai/PLAN_*.md` paths to `ai/plans/active/PLAN_*.md`
  - update `AGENTS.md`, `WORKING_WITH_AI.md`, `ROADMAP.md`, `ai/DOCUMENTATION.md`, templates, references, skills, and scripts for the new paths and owners
  - replace the prompt index/body layout with a reusable task library while keeping loader behavior deterministic
  - create a narrow repo-local skill for ad hoc task execution if it can wrap the new `ai/EXECUTION.md` without copying policy
  - run the `Compact AI Docs` maintenance prompt after the structure lands
  - regenerate `ai/references/AI_GUIDELINES_POST_COMPACTION_EVALUATION.md`
  - record the implemented AI-guidance change in `CHANGELOG.md`
- Out of scope:
  - changing application runtime behavior, public API contracts, tests, REST Docs, OpenAPI, HTTP examples, deployment assets, or setup behavior
  - cutting a release
  - rewriting archived plans only to remove historical wording
  - merging prior workflow-selection variant branches or cherry-picking from them without a separate explicit decision

## Current State
- `ai/PLAN.md`, `ai/EXECUTION.md`, and `ai/WORKFLOW.md` currently share responsibilities for plan creation, plan execution, milestone execution, branch/worktree rules, delegation, and integration.
- Top-level active plan files use the same `PLAN_*.md` prefix family that proposed new guide names such as `PLAN_EXECUTION.md` would also match.
- `ai/PROMPTS.md`, `ai/prompts/index.json`, `ai/prompts/bodies/`, and `scripts/ai/get-prompt.ps1` currently describe reusable starters as prompts rather than reusable task definitions.
- Live docs, task starters, templates, references, and repo-local skills contain current mode-oriented wording that will be stale after this redesign.
- `ROADMAP.md` now points at this plan instead of the three narrower workflow-selection candidate rows.
- The repository still contains those three top-level option plan files; implementation should close, move, or archive them under the new active-plan inventory rules.
- The untracked `ai/references/WORKFLOW_SELECTION_VARIANT_COMPARISON.md` is a useful decision aid, but it is not standing policy and should not be edited unless the user wants to keep it.

## Requirement Gaps And Open Questions
- Should any backward-compatible prompt loader alias remain after the task-library migration?
  Fallback: keep `scripts/ai/get-prompt.ps1` as a thin compatibility wrapper for one release cycle, but route active docs to the new task-library loader.
- Should old top-level workflow-selection candidate plans be archived, moved to the new active-plan subdirectory as superseded plans, or deleted?
  Fallback: move them to `ai/plans/active/` with lifecycle set to `Closed` only if active-guide references still need them during migration; otherwise archive them under the existing archive policy.
- Should the ad hoc task execution skill be created immediately or deferred?
  Fallback: create a small `ai/skills/repo-ad-hoc-task-executor/SKILL.md` only if it stays under one screen and points back to `ai/EXECUTION.md`, `ai/DOCUMENTATION.md`, and `ai/TESTING.md`.
- Should the task library directory be `ai/tasks/`, `ai/task-library/`, or another name?
  Fallback: use `ai/task-library/` because it is explicit and does not collide with Gradle tasks or plan files.

## Locked Decisions And Assumptions
- The planning guide target name is `ai/PLANNING.md`; `ai/PLANING.md` is rejected as a typo.
- Active plans move to `ai/plans/active/` before `ai/PLAN_EXECUTION.md` is introduced, so broad `PLAN_*.md` scans can be retired or narrowed safely.
- `ai/PLAN_EXECUTION.md` owns whole-plan execution: readiness, plan scope, milestone sequencing, plan-level context switching, compaction checkpoints, validation rollup, roadmap status, and completion handoff.
- `ai/EXECUTION.md` owns ad hoc tasks and individual plan milestones: smallest useful read set, context switching for efficiency, spec-first changes, validation at the checkpoint, review, and when to promote ad hoc work into a plan.
- `ai/WORKFLOW.md` remains, but only for collaboration mechanics: branch/worktree hygiene, delegation boundaries, worker logs, integration, and remote handoff.
- The prompt system becomes a reusable task library, with standing policy kept in owner guides and task bodies kept procedural.
- Public application behavior remains unchanged.

## Execution Approach
- Use one coordinated documentation-maintenance run because the same owner-guide paths, task-library names, and cross-references are shared across the repo.
- Do not split implementation until the new owner map and path migration are complete.
- If delegation is later requested, use only read-only review or explicitly non-overlapping file ownership after the coordinator has updated the central owner map.

## Affected Artifacts
- Standing AI guides:
  - `AGENTS.md`
  - `WORKING_WITH_AI.md`
  - `ai/PLANNING.md`
  - `ai/PLAN_EXECUTION.md`
  - `ai/EXECUTION.md`
  - `ai/WORKFLOW.md`
  - `ai/DOCUMENTATION.md`
  - `ai/TESTING.md`
  - `ai/REVIEWS.md`
  - `ai/RELEASES.md`
  - `ai/ENVIRONMENT_QUICK_REF.md` only if command references change
- Plan inventory:
  - `ai/plans/active/`
  - current top-level `ai/PLAN_*.md` files
  - `ROADMAP.md`
  - release cleanup references under `ai/references/`
- Reusable task library:
  - replacement for `ai/PROMPTS.md`
  - replacement for `ai/prompts/index.json`
  - replacement for `ai/prompts/bodies/`
  - `scripts/ai/get-prompt.ps1` and any new loader script
- Templates, references, and skills:
  - `ai/templates/PLAN_TEMPLATE.md`
  - planning, workflow, release, troubleshooting, and evaluation references found by targeted search
  - `ai/skills/**/SKILL.md`
  - optional new ad hoc task execution skill
- Measurement and release notes:
  - `ai/references/AI_GUIDELINES_POST_COMPACTION_EVALUATION.md`
  - `CHANGELOG.md`
- Not affected:
  - application source
  - app tests
  - REST Docs AsciiDoc
  - approved OpenAPI baseline
  - manual HTTP examples
  - setup or deployment docs unless a narrow AI-workflow pointer requires correction

## Execution Milestones

### Milestone 1: Baseline Inventory And Owner Map
- goal: map every current planning, execution, workflow, prompt, task-library, and active-plan inventory rule before moving files.
- owned files or packages:
  - this plan's `Validation Results`
  - optional local notes under `ai/tmp/`
- context required before execution:
  - `AGENTS.md`, `ROADMAP.md`, `ai/DOCUMENTATION.md`, `ai/PLAN.md`, `ai/EXECUTION.md`, `ai/WORKFLOW.md`, `ai/PROMPTS.md`, `ai/TESTING.md`, `ai/REVIEWS.md`, `ai/templates/PLAN_TEMPLATE.md`, `scripts/ai/get-prompt.ps1`, this plan, and the untracked variant comparison if still present
- behavior to preserve:
  - no standing rule is deleted before the new owner is named
  - archived plans remain historical unless a path move would make an active instruction misleading
- exact deliverables:
  - rule-to-owner map for planning, whole-plan execution, task/milestone execution, collaboration workflow, reusable tasks, validation, review, and documentation routing
  - current active-plan inventory and every live reference to top-level `ai/PLAN_*.md`
  - current prompt/task starter inventory and proposed target names
  - list of exact legacy vocabulary to remove from live standing guidance
- validation checkpoint:
  - `git diff --check`
  - targeted reference inventory reviewed against `ai/DOCUMENTATION.md`
- commit checkpoint:
  - `docs: inventory ai guidance restructure`

### Milestone 2: Move Active Plans And Rename Planning Owner
- goal: eliminate top-level plan-file naming collisions before adding new execution-guide names.
- owned files or packages:
  - `ai/plans/active/`
  - `ai/PLANNING.md`
  - removed or replaced `ai/PLAN.md`
  - `AGENTS.md`
  - `ROADMAP.md`
  - `ai/DOCUMENTATION.md`
  - active plan references found by Milestone 1
- context required before execution:
  - Milestone 1 owner map, current top-level active plans, `ROADMAP.md`, `ai/PLAN.md`, `ai/DOCUMENTATION.md`, and this milestone
- behavior to preserve:
  - active release-gate plan status and roadmap ordering stay intact after the move
  - superseded workflow-selection candidate plans do not remain presented as current execution choices
- exact deliverables:
  - active plans live under `ai/plans/active/`
  - planning guide is available as `ai/PLANNING.md`
  - live references use `ai/plans/active/PLAN_*.md` and `ai/PLANNING.md`
  - any compatibility pointer is short and temporary, or omitted if all live references are updated cleanly
- validation checkpoint:
  - targeted search shows no active live reference still expects top-level `ai/PLAN_*.md`
  - targeted search shows no live reference uses `PLANING.md`
  - `git diff --check`
- commit checkpoint:
  - `docs: move active plans under active directory`

### Milestone 3: Regenerate Execution And Workflow Owners
- goal: create clear owner boundaries for whole-plan execution, task/milestone execution, and collaboration mechanics.
- owned files or packages:
  - `ai/PLAN_EXECUTION.md`
  - `ai/EXECUTION.md`
  - `ai/WORKFLOW.md`
  - `AGENTS.md`
  - `ai/DOCUMENTATION.md`
  - `ai/templates/PLAN_TEMPLATE.md`
  - affected references under `ai/references/`
- context required before execution:
  - Milestone 1 owner map, `ai/PLANNING.md`, current `ai/EXECUTION.md`, current `ai/WORKFLOW.md`, `ai/DOCUMENTATION.md`, `ai/TESTING.md`, `ai/REVIEWS.md`, and this milestone
- behavior to preserve:
  - spec-driven execution still updates specs before implementation when behavior changes
  - milestone validation and review remain mandatory
  - branch, worktree, delegation, and integration safety rules remain available before work is split or handed off
- exact deliverables:
  - `ai/PLAN_EXECUTION.md` includes context switching and compaction rules across a whole plan
  - `ai/EXECUTION.md` includes context switching rules for efficient ad hoc and milestone execution
  - `ai/WORKFLOW.md` contains no owner overlap with planning or execution guides
  - template sections match the new owner model and active-plan paths
- validation checkpoint:
  - targeted search confirms the old named mode vocabulary is absent from live standing guides, task starters, templates, and active plans except intentional migration notes in this plan
  - `git diff --check`
- commit checkpoint:
  - `docs: split plan and task execution guidance`

### Milestone 4: Replace Prompt Storage With Reusable Task Library
- goal: make reusable starters task-shaped, discoverable, and clearly separate from standing policy.
- owned files or packages:
  - `ai/TASK_LIBRARY.md` or chosen replacement for `ai/PROMPTS.md`
  - `ai/task-library/index.json`
  - `ai/task-library/bodies/*.md`
  - `scripts/ai/get-task.ps1`
  - `scripts/ai/get-prompt.ps1` compatibility wrapper if retained
  - task starter references in `AGENTS.md`, `WORKING_WITH_AI.md`, and owner guides
- context required before execution:
  - Milestone 1 task inventory, `ai/PROMPTS.md`, `ai/prompts/index.json`, representative touched prompt bodies, `scripts/ai/get-prompt.ps1`, `ai/DOCUMENTATION.md`, and this milestone
- behavior to preserve:
  - reusable starters remain listable and loadable by title or slug
  - task bodies remain procedural and do not become policy dumps
  - placeholder metadata remains machine-readable
- exact deliverables:
  - prompt index and body paths are migrated or replaced by the task-library layout
  - stale prompt names that encode removed mode vocabulary are renamed to task-oriented names
  - loader smoke checks cover old compatibility behavior if retained and new task-library behavior
  - `ai/DOCUMENTATION.md` owns the new task-library artifact routing
- validation checkpoint:
  - task loader list and single-task load smoke checks pass
  - compatibility loader smoke check passes if retained
  - targeted search confirms active docs no longer describe the library as prompt-owned policy
  - `git diff --check`
- commit checkpoint:
  - `docs: migrate prompts to reusable task library`

### Milestone 5: Add Ad Hoc Task Skill And Run Compaction
- goal: add the optional ad hoc execution wrapper only if it improves entry workflow, then compact duplicate standing guidance.
- owned files or packages:
  - optional `ai/skills/repo-ad-hoc-task-executor/SKILL.md`
  - affected skill metadata or agent files
  - standing top-level AI guides changed by compaction
  - `CHANGELOG.md`
- context required before execution:
  - new `ai/EXECUTION.md`, `ai/DOCUMENTATION.md`, `ai/TESTING.md`, existing repo-local skill entrypoints, `ai/prompts/bodies/compact-ai-docs.md`, and this milestone
- behavior to preserve:
  - skills remain narrow wrappers and do not replace owner guides
  - compaction moves guidance to the single best owner rather than deleting unclear rules
- exact deliverables:
  - ad hoc task skill is created or explicitly deferred with rationale recorded in this plan
  - `Compact AI Docs` prompt is loaded and applied to the changed standing AI documents
  - duplicated or stale guidance is removed or routed to the correct owner
  - `CHANGELOG.md` records the implemented AI-guidance restructure under `## [Unreleased]`
- validation checkpoint:
  - targeted duplicate/stale-reference searches from the compaction prompt pass or have recorded exceptions
  - `git diff --check`
- commit checkpoint:
  - `docs: compact restructured ai guidance`

### Milestone 6: Regenerate Evaluation Report And Final Validation
- goal: prove the new guidance set is coherent, measured, and ready for handoff.
- owned files or packages:
  - `ai/references/AI_GUIDELINES_POST_COMPACTION_EVALUATION.md`
  - this plan
  - `ROADMAP.md`
- context required before execution:
  - `ai/prompts/bodies/evaluate-ai-guidelines.md` or its task-library successor, changed standing guides, task-library inventory, active-plan inventory, `ai/TESTING.md`, `ai/REVIEWS.md`, `ai/DOCUMENTATION.md`, and this milestone
- behavior to preserve:
  - the evaluation report remains on demand
  - roadmap status changes only after implementation state actually changes
- exact deliverables:
  - refreshed evaluation date, grade, size baseline, practical read-set estimates, file grades, realized gains, risks, obsolete recommendations, and ranked follow-ups
  - final plan `Validation Results` updated with actual commands and outcomes
  - `ROADMAP.md` reflects the final active-work state
- validation checkpoint:
  - task loader smoke checks
  - targeted stale-reference searches
  - `git diff --check`
  - `./build.ps1 build`
  - manual documentation/process review with `ai/REVIEWS.md`
- commit checkpoint:
  - `docs: refresh ai guidance evaluation`

## Edge Cases And Failure Modes
- A compatibility pointer can grow into a second owner. Keep any pointer short, time-boxed, and free of copied policy.
- Moving active plans can break scripts or prompt/task starters that glob top-level `ai/PLAN_*.md`. Update those before adding new `PLAN_*.md` guide names.
- Renaming the planning guide can leave stale references in human-facing docs, templates, skills, and release references. Use targeted searches across live files.
- Task-library migration can break established prompt names. Use deterministic loader errors and optional compatibility only where it does not keep stale policy alive.
- A skill for ad hoc tasks can duplicate `ai/EXECUTION.md`. Create it only as a wrapper with narrow triggers and owner-guide pointers.
- The evaluation report can become stale if generated before compaction. Run compaction first, then regenerate the report.

## Validation Plan
- Run targeted reference searches over live files, excluding `ai/archive/**` except when checking release cleanup instructions.
- Run task loader smoke checks for list and single-task load paths; run compatibility prompt loader checks if retained.
- Run `git diff --check`.
- Run `./build.ps1 build`; for documentation-only AI-guidance changes, record whether the wrapper takes the lightweight-file shortcut or performs Gradle validation.
- Manually review changed docs with `ai/DOCUMENTATION.md` and `ai/REVIEWS.md`.

## Testing Strategy
- Unit tests: not applicable unless a new loader or guard script has testable logic; if added, run controlled positive and negative smoke checks.
- Integration tests: not applicable because runtime app behavior does not change.
- Contract tests: not applicable because REST Docs, OpenAPI, HTTP examples, and public API behavior do not change.
- Smoke or benchmark tests: not applicable because performance and deployment behavior do not change.
- Documentation verification: required through task loader smoke checks, targeted stale-reference searches, `git diff --check`, wrapper build, and manual owner-alignment review.

## Better Engineering Notes
- The prior workflow-selection variants are useful evidence but too narrow for this request. This plan should replace them rather than blend them.
- `ai/PLAN_EXECUTION.md` would have collided with top-level active-plan globs; moving active plans first removes that concrete risk.
- Prefer the reusable task-library language because these artifacts are operational starters, not just prompts.
- Do not optimize only for total character count. Measure practical read sets for planning, whole-plan execution, ad hoc or milestone execution, workflow coordination, task library use, verification, and release.

## Plan Readiness Evaluation
- No blocking requirement gap remains; the plan records fallbacks for compatibility aliases, old candidate-plan handling, ad hoc skill creation, and task-library directory naming.
- Lifecycle is accurate: `Phase=Planning`, `Status=Ready`.
- The plan is ready for implementation after user selection, with one caveat: implementation should not interrupt the active `v2.0.0-RC6` manual regression gate unless the user intentionally reprioritizes roadmap work.
- The highest-risk areas are path migration and reusable task-library loader compatibility; both have early inventory and smoke-check milestones.

## Validation Results
- 2026-05-07 plan creation:
  - Loaded `AGENTS.md`, `ai/DOCUMENTATION.md`, `ai/PLAN.md`, `ai/EXECUTION.md`, `ai/WORKFLOW.md`, `ai/PROMPTS.md`, `ai/TESTING.md`, `ai/REVIEWS.md`, `ROADMAP.md`, `ai/templates/PLAN_TEMPLATE.md`, `ai/prompts/bodies/compact-ai-docs.md`, `ai/prompts/bodies/evaluate-ai-guidelines.md`, `ai/prompts/bodies/create-plan.md`, `ai/prompts/bodies/review-plan-readiness.md`, the active workflow-selection option plans, and the current post-compaction evaluation report.
  - Read the repo-local `repo-plan-author` skill and applied it to this plan creation task.
  - Reviewed the untracked `ai/references/WORKFLOW_SELECTION_VARIANT_COMPARISON.md` as relevant context without editing it.
  - Updated `ROADMAP.md` Intake to point at this new plan and replace the older workflow-selection variant candidate rows.
  - Ran a targeted search for the retired mode-name vocabulary against this plan and `ROADMAP.md`; no matches before this validation note.
  - Ran `git diff --check`; passed.
  - Ran `./build.ps1 build`; passed through the lightweight-file shortcut and skipped Gradle. The wrapper reported changed files as this new plan, `ROADMAP.md`, and the pre-existing untracked `ai/references/WORKFLOW_SELECTION_VARIANT_COMPARISON.md`.

## User Validation
- Review this plan's owner split and confirm these names: `ai/PLANNING.md`, `ai/PLAN_EXECUTION.md`, `ai/EXECUTION.md`, `ai/WORKFLOW.md`, and `ai/task-library/`.
- Confirm that old workflow-selection variant plans should be superseded by this broader plan.
- Confirm implementation may wait until the active `v2.0.0-RC6` manual regression gate no longer owns the immediate next action.

## Required Content Checklist
- Behavior changing: AI guidance ownership, active plan locations, execution guidance structure, reusable task storage, and optional ad hoc execution skill.
- Roadmap tracking: `ROADMAP.md` / `Intake` / `AI guidance execution and reusable task library restructure`.
- Out of scope: application runtime, public API, setup behavior, deployment behavior, release cutting, and archived-plan history rewrites.
- Governing artifacts: `AGENTS.md`, `ai/DOCUMENTATION.md`, current planning/execution/workflow/prompt guides, task starters, templates, skills, roadmap, changelog, and evaluation report.
- Likely files: named in `Affected Artifacts`.
- Compatibility promises: public app contract unchanged; reusable starter loading remains deterministic; historical archive wording remains historical.
- Risks: path migration breaks globs, compatibility pointer drift, task-library rename churn, duplicated skill policy, stale evaluation timing.
- Requirement gaps: all have fallbacks.
- Execution approach: one coordinated documentation-maintenance run with optional read-only review or explicit non-overlapping delegation after the central owner map lands.
- Per-milestone context: named explicitly in each milestone.
- Specs and docs: AI guidance, human-facing AI workflow docs, task library artifacts, templates, skills, references, roadmap, changelog, and evaluation report.
- Validation: targeted searches, task loader smoke checks, `git diff --check`, `./build.ps1 build`, and manual owner-alignment review.
- User verification: inspect owner names, supersession of old variants, and roadmap priority.
