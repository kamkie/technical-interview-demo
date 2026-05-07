# Plan: Workflow Selection Hard Split

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Planning |
| Status | Needs Input |

## Summary
- Implement Variant 2 from `C:\Users\kamki\AppData\Roaming\JetBrains\IntelliJIdea2026.1\scratches\what_about_moving_workflow_selection_to.md`.
- Move execution-mode selection into `ai/PLAN.md`, split `ai/EXECUTION.md` into pre-loop and milestone-loop owner guides, delete `ai/WORKFLOW.md`, and move coordinator/worker rules into the existing fanout references.
- Success means `ai/PLAN.md` is the only standing guide that mentions execution-mode names, while milestone execution loads a smaller in-loop guide instead of the full pre-execution framing.

## Scope
- In scope:
  - make `ai/PLAN.md` the authoritative owner for choosing `Linear Plan`, `Single-Plan Fanout`, and `Multi-Plan Fanout`
  - replace `ai/EXECUTION.md` with `ai/PLAN_EXECUTION.md` and `ai/MILESTONE_EXECUTION.md`
  - delete `ai/WORKFLOW.md`
  - move common coordinator, worker, worktree, branch, worker-log, delegation-quality, and completion-gate rules into the appropriate fanout references or the new execution guides
  - update `AGENTS.md`, `ai/DOCUMENTATION.md`, `ai/PROMPTS.md`, prompt metadata/bodies, templates, repo-local skills, and human-facing AI workflow docs to the new owner model
  - add a targeted validation guard or script that fails when mode names appear in standing guides other than `ai/PLAN.md`
  - refresh the AI guideline evaluation report with before/after read-set measurements
  - record the implemented AI-guidance change in `CHANGELOG.md` during execution
- Out of scope:
  - turning Multi-Plan Fanout into a coordinator plan template
  - adding `ai/templates/PLAN_MULTI_FANOUT_TEMPLATE.md`
  - changing application source, runtime behavior, public API contracts, REST Docs, OpenAPI, HTTP examples, setup behavior, or release automation
  - rewriting archived plans for historical terminology

## Current State
- `ai/WORKFLOW.md` currently owns both mode selection and fanout/common coordination rules.
- `ai/EXECUTION.md` owns pre-implementation checks, the common milestone loop, commit discipline, tracking artifact ownership, and completion criteria in one file.
- `AGENTS.md` lifecycle routing names `ai/WORKFLOW.md` for workflow, delegation, or integration work.
- `ai/DOCUMENTATION.md`, `ai/PROMPTS.md`, `WORKING_WITH_AI.md`, prompt bodies, `ai/templates/PLAN_TEMPLATE.md`, and repo-local skills contain live references to `ai/WORKFLOW.md` or mode names.
- The scratch analysis projects Variant 2 as the best practical tradeoff: mode selection drops from about 7,770 to 5,225 estimated tokens, and hot-path milestone execution drops from about 5,371 to 4,475 estimated tokens, while rare fanout coordination grows slightly.

## Requirement Gaps And Open Questions
- Which variant should be implemented?
  Fallback: do not execute this plan until the user selects Variant 2 over Variant 1 and Variant 3.
- Should the split file names be exactly `ai/PLAN_EXECUTION.md` and `ai/MILESTONE_EXECUTION.md`?
  Fallback: use those scratch-proposed names because they describe pre-loop versus in-loop ownership directly.
- Should `ai/WORKFLOW.md` be deleted in the same milestone that creates the split guides, or left as a one-release tombstone?
  Fallback: delete it in this variant; a tombstone is Variant 1's design, not Variant 2's.
- Should mode-name ownership be enforced by a new CI task, a script checked by documentation validation, or only targeted `rg` commands?
  Fallback: add a small repo-owned script under `scripts/ai/` and reference it from validation; wire it into CI only if it is simple and stable.

## Locked Decisions And Assumptions
- This plan is an alternative candidate, not approved implementation work.
- If selected, this variant intentionally deletes `ai/WORKFLOW.md`.
- `ai/PLAN.md` is the only standing guide allowed to contain the mode names `Linear Plan`, `Single-Plan Fanout`, and `Multi-Plan Fanout`.
- `ai/PLAN_EXECUTION.md` owns pre-implementation framing, plan readiness checks, completion criteria, and handoff boundaries.
- `ai/MILESTONE_EXECUTION.md` owns the repeatable milestone loop, commit discipline, and in-loop guardrails.
- Fanout-specific mechanics stay on demand in `ai/references/WORKFLOW_SINGLE_PLAN_FANOUT.md` and `ai/references/WORKFLOW_MULTI_PLAN_FANOUT.md`.
- Archived plans remain historical and are excluded from active wording guards.

## Execution Mode Fit
- Recommended mode: `Linear Plan`.
- Reason: this is a broad but tightly coupled AI-document ownership refactor across standing guides, prompt bodies, templates, and skills. A single coordinator should preserve owner clarity and avoid partial split states.
- Coordinator-owned files:
  - `AGENTS.md`
  - `WORKING_WITH_AI.md`
  - top-level `ai/*.md`
  - `ai/references/WORKFLOW_SINGLE_PLAN_FANOUT.md`
  - `ai/references/WORKFLOW_MULTI_PLAN_FANOUT.md`
  - `ai/references/AI_GUIDELINES_POST_COMPACTION_EVALUATION.md`
  - `ai/prompts/index.json`
  - `ai/prompts/bodies/*.md`
  - `ai/templates/PLAN_TEMPLATE.md`
  - affected `ai/skills/**/SKILL.md`
  - any new guard script under `scripts/ai/`
  - `ROADMAP.md`
  - `CHANGELOG.md`
- Candidate worker boundaries: none recommended. If forced, use one read-only reviewer for prompt/template drift after the coordinator lands the structural split.

## Affected Artifacts
- Deleted:
  - `ai/WORKFLOW.md`
- Added:
  - `ai/PLAN_EXECUTION.md`
  - `ai/MILESTONE_EXECUTION.md`
  - optional `scripts/ai/check-workflow-mode-ownership.ps1`
- Updated AI guides:
  - `AGENTS.md`
  - `ai/PLAN.md`
  - `ai/DOCUMENTATION.md`
  - `ai/PROMPTS.md`
  - `ai/TESTING.md` only if validation wording references old guide names
  - `ai/REVIEWS.md` only if review wording references old guide names
  - `ai/RELEASES.md` only if release handoff wording references old guide names
- Updated on-demand references:
  - `ai/references/WORKFLOW_SINGLE_PLAN_FANOUT.md`
  - `ai/references/WORKFLOW_MULTI_PLAN_FANOUT.md`
  - `ai/references/AI_GUIDELINES_POST_COMPACTION_EVALUATION.md`
- Updated prompts, templates, and skills:
  - `ai/prompts/index.json`
  - workflow prompt bodies under `ai/prompts/bodies/`
  - `ai/templates/PLAN_TEMPLATE.md`
  - affected `ai/skills/**/SKILL.md`
- Updated tracking:
  - `ROADMAP.md`
  - `CHANGELOG.md`
- Not affected:
  - application source
  - test source
  - REST Docs
  - OpenAPI baseline
  - HTTP examples
  - setup walkthroughs unless a narrow live reference requires correction

## Execution Milestones
### Milestone 1: Baseline, Ownership Map, And Guard Design
- goal: make the split mechanically safe by mapping every current `ai/WORKFLOW.md` rule to its future owner before deleting anything.
- owned files or packages:
  - this plan's `Validation Results`
  - optional local analysis under `ai/tmp/`
- shared files that a `Single-Plan Fanout` worker must leave to the coordinator:
  - all future owner-guide edits
- context required before execution:
  - `AGENTS.md`, `ai/PLAN.md`, `ai/WORKFLOW.md`, `ai/EXECUTION.md`, `ai/DOCUMENTATION.md`, `ai/PROMPTS.md`, `ai/TESTING.md`, `ai/REVIEWS.md`, workflow fanout references, this plan, and the scratch source file named in `Summary`
- behavior to preserve:
  - no execution, fanout, branch, worker-log, validation, or review rule is orphaned
  - archived plans remain excluded from active drift checks
- exact deliverables:
  - current size/read-set baseline for the affected guides
  - rule-to-owner map for all sections of `ai/WORKFLOW.md` and `ai/EXECUTION.md`
  - live reference inventory for mode names and guide paths
  - guard design deciding script-only versus CI-integrated enforcement
- validation checkpoint:
  - `git diff --check`
  - reference inventory reviewed against `ai/DOCUMENTATION.md`
- commit checkpoint:
  - `docs: inventory workflow hard split ownership`

### Milestone 2: Create New Planning And Execution Owners
- goal: create the new owner-guide structure while preserving the current execution behavior.
- owned files or packages:
  - `ai/PLAN.md`
  - `ai/PLAN_EXECUTION.md`
  - `ai/MILESTONE_EXECUTION.md`
  - `ai/WORKFLOW.md` deletion
  - `AGENTS.md`
  - `ai/DOCUMENTATION.md`
- shared files that a `Single-Plan Fanout` worker must leave to the coordinator:
  - all top-level owner guides
- context required before execution:
  - Milestone 1 owner map, `ai/PLAN.md`, `ai/WORKFLOW.md`, `ai/EXECUTION.md`, `AGENTS.md`, `ai/DOCUMENTATION.md`, and this milestone
- behavior to preserve:
  - default execution remains single-agent linear unless the user approves fanout
  - milestone loop and commit discipline remain mandatory
  - release sequencing remains in `ai/RELEASES.md`
- exact deliverables:
  - `ai/PLAN.md` owns the complete mode-selection gate
  - `ai/PLAN_EXECUTION.md` owns plan-readiness, pre-loop execution framing, tracking-artifact handoff, and completion criteria
  - `ai/MILESTONE_EXECUTION.md` owns the common milestone loop and commit rules
  - `ai/WORKFLOW.md` is deleted
  - `AGENTS.md` lifecycle owner map points to the new guides
  - `ai/DOCUMENTATION.md` artifact ownership points to the new guides
- validation checkpoint:
  - `rg -n "ai/WORKFLOW.md|WORKFLOW.md" AGENTS.md ai -g "*.md" -g "*.json" -g "!ai/archive/**"` returns only intentional migration notes in this plan until later milestones remove them
  - `git diff --check`
- commit checkpoint:
  - `docs: split execution guides and remove workflow router`

### Milestone 3: Move Fanout Mechanics Into Fanout References
- goal: keep rare fanout coordination rules on demand after the standing workflow router is deleted.
- owned files or packages:
  - `ai/references/WORKFLOW_SINGLE_PLAN_FANOUT.md`
  - `ai/references/WORKFLOW_MULTI_PLAN_FANOUT.md`
  - `ai/PLAN_EXECUTION.md`
  - `ai/MILESTONE_EXECUTION.md`
- shared files that a `Single-Plan Fanout` worker must leave to the coordinator:
  - prompt, template, roadmap, and changelog edits
- context required before execution:
  - Milestone 1 owner map, both workflow fanout references, new execution guides, and this milestone
- behavior to preserve:
  - worker log schema remains available before any fanout work starts
  - delegation quality bar remains explicit
  - coordinator completion gate remains explicit
  - common validation batching remains routed to `ai/TESTING.md`
- exact deliverables:
  - single-plan fanout reference contains all single-plan-specific coordinator/worker mechanics
  - multi-plan fanout reference contains all multi-plan-specific coordinator/worker mechanics
  - common execution guides contain only rules that apply to all execution
  - no deleted `ai/WORKFLOW.md` rule remains ownerless
- validation checkpoint:
  - targeted `rg` confirms fanout references contain worker-log and coordinator-gate rules
  - `git diff --check`
- commit checkpoint:
  - `docs: relocate fanout mechanics to references`

### Milestone 4: Sweep Prompts, Templates, Skills, And Human Docs
- goal: remove stale references to the deleted workflow guide from all live entry points.
- owned files or packages:
  - `WORKING_WITH_AI.md`
  - `ai/PROMPTS.md`
  - `ai/prompts/index.json`
  - affected `ai/prompts/bodies/*.md`
  - `ai/templates/PLAN_TEMPLATE.md`
  - affected `ai/skills/**/SKILL.md`
  - optional guard script under `scripts/ai/`
- shared files that a `Single-Plan Fanout` worker must leave to the coordinator:
  - `CHANGELOG.md`, `ROADMAP.md`, and this plan
- context required before execution:
  - prompt index, prompt bodies found by Milestone 1, plan template, affected skills, `WORKING_WITH_AI.md`, new owner guides, and this milestone
- behavior to preserve:
  - reusable prompts remain discoverable and loadable
  - prompt bodies do not become standing policy dumps
  - plan template still captures mode choice, coordinator ownership, and milestone context
- exact deliverables:
  - prompt and skill wording points to `ai/PLAN.md`, `ai/PLAN_EXECUTION.md`, `ai/MILESTONE_EXECUTION.md`, and fanout references as appropriate
  - old `ai/WORKFLOW.md` references are removed from live docs
  - mode-name ownership guard exists if selected in Milestone 1
  - `CHANGELOG.md` records the AI-guidance restructure under `## [Unreleased]`
- validation checkpoint:
  - prompt loader smoke checks for touched or renamed prompt titles
  - mode-name ownership guard or equivalent `rg` check passes
  - `git diff --check`
- commit checkpoint:
  - `docs: align prompts and templates with workflow hard split`

### Milestone 5: Measurement, Review, And Final Validation
- goal: prove Variant 2 delivered the intended read-set savings without lowering execution safeguards.
- owned files or packages:
  - `ai/references/AI_GUIDELINES_POST_COMPACTION_EVALUATION.md`
  - this plan
  - `ROADMAP.md`
- shared files that a `Single-Plan Fanout` worker must leave to the coordinator:
  - all final validation artifacts
- context required before execution:
  - changed docs, `ai/references/AI_GUIDELINES_POST_COMPACTION_EVALUATION.md`, `ai/TESTING.md`, `ai/REVIEWS.md`, `ai/DOCUMENTATION.md`, and this milestone
- behavior to preserve:
  - report remains on demand
  - roadmap status reflects implementation state only after execution is complete
- exact deliverables:
  - refreshed before/after size and practical read-set estimates for Variant 2
  - final validation results in this plan
  - `ROADMAP.md` status updated to `Implemented` after execution, if selected and completed
- validation checkpoint:
  - mode-name ownership guard or targeted `rg` check passes
  - prompt loader smoke checks pass for touched prompts
  - `git diff --check`
  - `./build.ps1 build`
  - manual documentation/process review with `ai/REVIEWS.md`
- commit checkpoint:
  - `docs: validate workflow hard split`

## Edge Cases And Failure Modes
- Deleting `ai/WORKFLOW.md` can break live references in prompts, skills, or docs. Use a full active-reference sweep before final validation.
- Splitting execution guidance can create duplication between `PLAN_EXECUTION.md` and `MILESTONE_EXECUTION.md`. Keep pre-loop and in-loop ownership boundaries explicit.
- Moving worker-log schema into references can hide it from coordinators. Ensure `ai/PLAN.md` tells non-default mode executors to load the matching fanout reference before work starts.
- `AGENTS.md` can grow if it absorbs workflow detail. Keep only lifecycle routing and repo-level invariants there.
- A CI guard can become noisy if it scans archived plans or release notes. Exclude `ai/archive/**` and `CHANGELOG.md` unless intentionally checking history.
- This variant has broader churn than Variant 1; do not execute it during release preparation unless the user explicitly accepts that churn.

## Validation Plan
- Run live-reference searches excluding `ai/archive/**`.
- Run the mode-name ownership guard or equivalent targeted `rg` check.
- Run prompt loader smoke checks for all touched or renamed prompt titles.
- Run `git diff --check`.
- Run `./build.ps1 build`; the lightweight-only shortcut is acceptable for documentation-only AI-guidance changes if the wrapper chooses it.
- Manually review changed docs with `ai/DOCUMENTATION.md` and `ai/REVIEWS.md`.

## Testing Strategy
- Unit tests: not applicable unless a guard script is added; if added, test it with at least one passing and one failing fixture or controlled invocation.
- Integration tests: not applicable; no runtime behavior changes.
- Contract tests: not applicable; no REST Docs, OpenAPI, HTTP examples, README public contract, or public API behavior changes.
- Smoke or benchmark tests: not applicable; no runtime or performance behavior changes.
- Documentation verification: required through ownership guard, targeted searches, prompt smoke checks, `git diff --check`, wrapper build, and manual owner-alignment review.

## Better Engineering Notes
- Variant 2 is the recommended scratch option because it improves both mode-selection and milestone-loop read sets while avoiding Variant 3's new coordinator-plan vocabulary.
- Keep the guard small and boring. A simple PowerShell script that scans standing guides is preferable to complex Gradle wiring unless CI integration is clearly worth it.
- Preserve the old fanout reference names unless deleting or renaming them creates a measurable benefit; the goal is owner clarity, not path churn.

## Validation Results
- 2026-05-07 plan creation:
  - Read the scratch recommendation file at `C:\Users\kamki\AppData\Roaming\JetBrains\IntelliJIdea2026.1\scratches\what_about_moving_workflow_selection_to.md` and converted Variant 2 into this option plan.
  - Updated `ROADMAP.md` Intake with the `Workflow-selection hard split` candidate entry.
  - Ran `git diff --check`; passed.
  - Ran `rg -n "Workflow-selection|PLAN_workflow_selection|Variant 1|Variant 2|Variant 3" ROADMAP.md ai -g "PLAN_workflow_selection_*.md"`; passed and confirmed the three roadmap entries plus variant plan references.
  - Ran `./build.ps1 build`; passed through the lightweight-only shortcut with changed files limited to the three workflow-selection plans and `ROADMAP.md`, so Gradle was skipped.
  - Implementation remains blocked until the user selects exactly one workflow-selection variant.

## User Validation
- Review `ai/PLAN.md` and confirm it is enough to choose a mode.
- Review `ai/PLAN_EXECUTION.md` and `ai/MILESTONE_EXECUTION.md` and confirm the pre-loop versus in-loop boundary is obvious.
- Confirm no live guide points to deleted `ai/WORKFLOW.md`.
- Run the mode-name ownership guard and inspect the allowed results.

## Required Content Checklist
- Behavior changing: AI workflow mode ownership and execution-guide structure change.
- Roadmap tracking: `ROADMAP.md` / `Intake` / `Workflow guidance restructure variants` / Variant 2.
- Out of scope: multi-plan coordinator template, public API behavior, setup behavior, release automation, archived-plan rewrites.
- Governing artifacts: scratch recommendation file, `ai/PLAN.md`, `ai/WORKFLOW.md`, `ai/EXECUTION.md`, fanout references, `ai/DOCUMENTATION.md`, prompt/template/skill artifacts.
- Likely files: named in `Affected Artifacts`.
- Compatibility promises: default mode remains linear, fanout still needs user approval, app contract unchanged, archived plans remain historical.
- Risks: broken references, duplicated split-guide rules, hidden fanout mechanics, noisy guard, `AGENTS.md` growth.
- Requirement gaps: blocked until the user selects this variant; split file names, deletion timing, and guard type have fallbacks.
- Execution mode: `Linear Plan`.
- Coordinator-owned files: all shared AI owner guides, fanout references, prompt index/bodies, templates, skills, roadmap, changelog, evaluation report, and guard script.
- Per-milestone context: named explicitly in each milestone.
- Specs and docs: AI guidance and human-facing AI workflow docs only; no app specs or public contract artifacts.
- Validation: ownership guard/searches, prompt smoke checks, `git diff --check`, `./build.ps1 build`, manual owner-alignment review.
- User verification: inspect new owners and run guard.
