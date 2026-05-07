# Plan: Workflow Selection Hard Split With Multi-Plan Template

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Planning |
| Status | Needs Input |

## Summary
- Implement Variant 3 from `C:\Users\kamki\AppData\Roaming\JetBrains\IntelliJIdea2026.1\scratches\what_about_moving_workflow_selection_to.md`.
- Apply the Variant 2 hard split, then stop treating `Multi-Plan Fanout` as a workflow mode and make it a coordinator plan created from a new template.
- Success means single-plan execution still uses planning-owned mode selection, while multi-plan coordination becomes a plan-shaped artifact with child plans, integration order, changelog merge rules, and completion gates.

## Scope
- In scope:
  - move execution-mode selection into `ai/PLAN.md`
  - split `ai/EXECUTION.md` into `ai/PLAN_EXECUTION.md` and `ai/MILESTONE_EXECUTION.md`
  - delete `ai/WORKFLOW.md`
  - keep `Single-Plan Fanout` as the only non-default execution mode
  - introduce `ai/templates/PLAN_MULTI_FANOUT_TEMPLATE.md` for coordinator plans that manage multiple child plans
  - shrink or fold `ai/references/WORKFLOW_MULTI_PLAN_FANOUT.md` into template usage guidance
  - update prompts, skills, templates, and live docs so "run plans as multi-plan fanout" becomes "create or execute a multi-plan coordinator plan"
  - add a targeted guard or validation script for mode-name and coordinator-template ownership
  - refresh the AI guideline evaluation report with before/after read-set measurements
  - record the implemented AI-guidance change in `CHANGELOG.md` during execution
- Out of scope:
  - keeping `ai/WORKFLOW.md` as a compatibility pointer
  - executing any existing child plan through the new coordinator template
  - changing application source, runtime behavior, public API contracts, REST Docs, OpenAPI, HTTP examples, setup behavior, or release automation
  - rewriting archived plans for historical terminology

## Current State
- `Multi-Plan Fanout` is currently one of three supported modes in `ai/WORKFLOW.md`.
- The multi-plan fanout reference owns detailed mechanics for separate plan files, private `CHANGELOG_<topic>.md` files, integration ordering, and release cleanup.
- `ai/templates/PLAN_TEMPLATE.md` is the only general plan template; there is no coordinator-plan template for multi-plan orchestration.
- Prompts currently expose multi-plan execution as workflow commands, including `Run Plans As Multi-Plan Fanout` and `Integrate Multi-Plan Fanout Output`.
- The scratch analysis says Variant 3 has the cleanest conceptual model when multi-plan fanout is recurring, but it adds vocabulary and template churn. It recommends this variant only if multi-plan runs are frequent enough to justify the abstraction.

## Requirement Gaps And Open Questions
- Which variant should be implemented?
  Fallback: do not execute this plan until the user selects Variant 3 over Variant 1 and Variant 2.
- Is Multi-Plan Fanout recurring enough to justify a coordinator-plan template?
  Fallback: require explicit user confirmation or evidence of at least three expected multi-plan runs per release cycle before execution.
- Should `ai/references/WORKFLOW_MULTI_PLAN_FANOUT.md` remain as a usage appendix or be folded fully into `ai/templates/PLAN_MULTI_FANOUT_TEMPLATE.md`?
  Fallback: keep a short reference appendix so the template stays usable without becoming a long tutorial.
- Should multi-plan prompt titles be renamed immediately?
  Fallback: yes; prompt names and bodies should use coordinator-plan language in the same change to avoid mixed mental models.
- Should the ownership guard forbid `Multi-Plan Fanout` outside historical files entirely?
  Fallback: allow historical references in `ai/archive/**` and `CHANGELOG.md`; live docs should use coordinator-plan wording except where explaining the migration inside this plan.

## Locked Decisions And Assumptions
- This plan is an alternative candidate, not approved implementation work.
- If selected, this variant includes all structural changes from Variant 2.
- After implementation, `ai/PLAN.md` owns mode selection for `Linear Plan` and `Single-Plan Fanout`.
- Multi-plan coordination is no longer described as a mode in standing guides; it is a coordinator plan shape.
- The coordinator plan template owns child plan inventory, integration order, private changelog merge rules, validation fan-in, and completion gates.
- Public API behavior and application runtime behavior remain unchanged.

## Execution Mode Fit
- Recommended mode: `Linear Plan`.
- Reason: this is a vocabulary and ownership migration across standing AI guides, prompt commands, templates, and references. It should be sequenced by one coordinator to avoid mixed mode/template terminology.
- Coordinator-owned files:
  - `AGENTS.md`
  - `WORKING_WITH_AI.md`
  - top-level `ai/*.md`
  - `ai/references/WORKFLOW_SINGLE_PLAN_FANOUT.md`
  - `ai/references/WORKFLOW_MULTI_PLAN_FANOUT.md`
  - `ai/templates/PLAN_TEMPLATE.md`
  - new `ai/templates/PLAN_MULTI_FANOUT_TEMPLATE.md`
  - `ai/prompts/index.json`
  - affected `ai/prompts/bodies/*.md`
  - affected `ai/skills/**/SKILL.md`
  - optional guard script under `scripts/ai/`
  - `ai/references/AI_GUIDELINES_POST_COMPACTION_EVALUATION.md`
  - `ROADMAP.md`
  - `CHANGELOG.md`
- Candidate worker boundaries: none recommended for implementation. A read-only reviewer can inspect final prompt/template vocabulary after the coordinator lands the migration.

## Affected Artifacts
- Deleted:
  - `ai/WORKFLOW.md`
- Added:
  - `ai/PLAN_EXECUTION.md`
  - `ai/MILESTONE_EXECUTION.md`
  - `ai/templates/PLAN_MULTI_FANOUT_TEMPLATE.md`
  - optional `scripts/ai/check-workflow-mode-ownership.ps1`
- Updated:
  - `AGENTS.md`
  - `WORKING_WITH_AI.md`
  - `ai/PLAN.md`
  - `ai/DOCUMENTATION.md`
  - `ai/PROMPTS.md`
  - `ai/references/WORKFLOW_SINGLE_PLAN_FANOUT.md`
  - `ai/references/WORKFLOW_MULTI_PLAN_FANOUT.md`
  - `ai/references/AI_GUIDELINES_POST_COMPACTION_EVALUATION.md`
  - `ai/templates/PLAN_TEMPLATE.md`
  - `ai/prompts/index.json`
  - affected prompt bodies
  - affected skill entrypoints
  - `ROADMAP.md`
  - `CHANGELOG.md`
- Not affected:
  - application source
  - app tests
  - REST Docs
  - OpenAPI baseline
  - HTTP examples
  - setup and release automation unless a narrow live workflow-reference pointer requires correction

## Execution Milestones
### Milestone 1: Baseline, Recurrence Check, And Ownership Map
- goal: verify Variant 3 is justified and map every current workflow and multi-plan rule before changing files.
- owned files or packages:
  - this plan's `Validation Results`
  - optional local analysis under `ai/tmp/`
- shared files that a `Single-Plan Fanout` worker must leave to the coordinator:
  - all planned AI owner-guide, prompt, template, and reference edits
- context required before execution:
  - `AGENTS.md`, `ai/PLAN.md`, `ai/WORKFLOW.md`, `ai/EXECUTION.md`, `ai/DOCUMENTATION.md`, `ai/PROMPTS.md`, both workflow fanout references, existing plan template, this plan, and the scratch source file named in `Summary`
- behavior to preserve:
  - no current fanout or integration safety rule is orphaned
  - archived plans remain historical
- exact deliverables:
  - evidence or explicit user confirmation that a multi-plan coordinator template is worth introducing
  - current size/read-set baseline
  - rule-to-owner map for `ai/WORKFLOW.md`, `ai/EXECUTION.md`, and `WORKFLOW_MULTI_PLAN_FANOUT.md`
  - live reference inventory for mode names, multi-plan prompt titles, and guide paths
- validation checkpoint:
  - `git diff --check`
  - owner map reviewed against `ai/DOCUMENTATION.md`
- commit checkpoint:
  - `docs: inventory multi-plan coordinator template migration`

### Milestone 2: Apply Hard Split For Planning And Milestone Execution
- goal: complete the Variant 2 structural foundation before changing the multi-plan mental model.
- owned files or packages:
  - `ai/PLAN.md`
  - `ai/PLAN_EXECUTION.md`
  - `ai/MILESTONE_EXECUTION.md`
  - `ai/WORKFLOW.md` deletion
  - `AGENTS.md`
  - `ai/DOCUMENTATION.md`
- shared files that a `Single-Plan Fanout` worker must leave to the coordinator:
  - templates, prompts, fanout references, changelog, and roadmap
- context required before execution:
  - Milestone 1 owner map, `ai/PLAN.md`, `ai/WORKFLOW.md`, `ai/EXECUTION.md`, `AGENTS.md`, `ai/DOCUMENTATION.md`, and this milestone
- behavior to preserve:
  - single-plan execution remains fully supported
  - default execution remains linear
  - milestone loop and commit discipline remain mandatory
- exact deliverables:
  - `ai/PLAN.md` owns selection between `Linear Plan` and `Single-Plan Fanout`
  - new execution guides exist with clear pre-loop and in-loop boundaries
  - deleted `ai/WORKFLOW.md` has no live references except temporary migration notes in this plan
  - `AGENTS.md` and `ai/DOCUMENTATION.md` point to the new owners
- validation checkpoint:
  - targeted `rg` for deleted guide references
  - `git diff --check`
- commit checkpoint:
  - `docs: split execution guides for coordinator template model`

### Milestone 3: Introduce Multi-Plan Coordinator Template
- goal: replace multi-plan-as-mode with a plan-shaped coordinator artifact.
- owned files or packages:
  - `ai/templates/PLAN_MULTI_FANOUT_TEMPLATE.md`
  - `ai/references/WORKFLOW_MULTI_PLAN_FANOUT.md`
  - `ai/PLAN.md`
  - `ai/templates/PLAN_TEMPLATE.md`
- shared files that a `Single-Plan Fanout` worker must leave to the coordinator:
  - prompt and skill vocabulary changes
- context required before execution:
  - `ai/templates/PLAN_TEMPLATE.md`, `ai/references/WORKFLOW_MULTI_PLAN_FANOUT.md`, Milestone 1 owner map, and this milestone
- behavior to preserve:
  - multi-plan work still tracks child plans, integration order, private changelog entries, validation fan-in, and release cleanup
  - coordinator completion still requires every child plan or worker to reach a terminal state
  - the general plan template remains suitable for normal one-plan work
- exact deliverables:
  - new coordinator template with lifecycle, child plan inventory, integration order, ownership matrix, private changelog merge rules, worker log expectations, validation fan-in, and completion gate
  - multi-plan reference reduced to short usage guidance or appendix
  - `ai/PLAN.md` tells agents to create a coordinator plan for multi-plan work instead of selecting a multi-plan mode
  - `ai/templates/PLAN_TEMPLATE.md` points to the coordinator template only when multiple approved plans must be orchestrated together
- validation checkpoint:
  - targeted search confirms live docs no longer present multi-plan as a normal execution mode
  - `git diff --check`
- commit checkpoint:
  - `docs: add multi-plan coordinator template`

### Milestone 4: Rename Prompts And Align Skills
- goal: make reusable commands and repo-local skills follow the coordinator-plan model.
- owned files or packages:
  - `ai/PROMPTS.md`
  - `ai/prompts/index.json`
  - affected prompt bodies under `ai/prompts/bodies/`
  - affected `ai/skills/**/SKILL.md`
  - optional ownership guard script under `scripts/ai/`
  - `WORKING_WITH_AI.md`
- shared files that a `Single-Plan Fanout` worker must leave to the coordinator:
  - `CHANGELOG.md`, `ROADMAP.md`, and this plan
- context required before execution:
  - prompt index, all prompt bodies with multi-plan or workflow-mode wording, affected skills, `WORKING_WITH_AI.md`, new coordinator template, and this milestone
- behavior to preserve:
  - prompt loader still resolves every listed prompt title
  - old prompt bodies are renamed or removed cleanly with index updates
  - skills do not duplicate coordinator-template policy
- exact deliverables:
  - multi-plan prompt titles and bodies use coordinator-plan language
  - integration prompt wording matches child-plan and coordinator-plan terminology
  - affected skills point to `ai/PLAN.md`, execution split guides, fanout reference, and coordinator template as appropriate
  - ownership guard exists if selected in Milestone 1
  - `CHANGELOG.md` records the AI-guidance restructure under `## [Unreleased]`
- validation checkpoint:
  - prompt loader smoke checks for renamed prompt titles
  - ownership guard or targeted `rg` check passes
  - `git diff --check`
- commit checkpoint:
  - `docs: align prompts with multi-plan coordinator model`

### Milestone 5: Measurement, Review, And Final Validation
- goal: prove Variant 3 is coherent, measurable, and does not add unnecessary standing-context cost.
- owned files or packages:
  - `ai/references/AI_GUIDELINES_POST_COMPACTION_EVALUATION.md`
  - this plan
  - `ROADMAP.md`
- shared files that a `Single-Plan Fanout` worker must leave to the coordinator:
  - all final validation artifacts
- context required before execution:
  - changed docs, coordinator template, `ai/references/AI_GUIDELINES_POST_COMPACTION_EVALUATION.md`, `ai/TESTING.md`, `ai/REVIEWS.md`, `ai/DOCUMENTATION.md`, and this milestone
- behavior to preserve:
  - evaluation report remains on demand
  - roadmap reflects implementation state only after execution completes
- exact deliverables:
  - refreshed Variant 3 read-set measurements
  - final plan validation results
  - `ROADMAP.md` status updated to `Implemented` after execution, if selected and completed
- validation checkpoint:
  - ownership guard or targeted `rg` check passes
  - prompt loader smoke checks pass for renamed prompts
  - `git diff --check`
  - `./build.ps1 build`
  - manual documentation/process review using `ai/REVIEWS.md`
- commit checkpoint:
  - `docs: validate multi-plan coordinator template model`

## Edge Cases And Failure Modes
- Variant 3 can introduce vocabulary churn without enough usage. Do not execute without user confirmation that the coordinator template is warranted.
- A coordinator template can become too long to be usable. Keep detailed examples in references and keep the template executable.
- Renaming prompts can break user muscle memory. Preserve obvious aliases only if the prompt loader supports them without ambiguity.
- Folding the multi-plan reference into the template can make the template a tutorial instead of a skeleton. Prefer a short appendix if needed.
- Ownership guards must exclude archived plans and historical release notes to avoid false positives.
- The conceptual cleanup can hide the fact that multi-plan work still needs coordinator discipline. The template must retain explicit completion gates.

## Validation Plan
- Run live-reference searches excluding `ai/archive/**`.
- Run ownership guard or targeted `rg` checks for mode and coordinator-template vocabulary.
- Run prompt loader smoke checks for all renamed prompt titles.
- Run `git diff --check`.
- Run `./build.ps1 build`; the lightweight-only shortcut is acceptable for documentation-only AI-guidance changes if the wrapper chooses it.
- Manually review changed docs with `ai/DOCUMENTATION.md` and `ai/REVIEWS.md`.

## Testing Strategy
- Unit tests: not applicable unless a guard script is added; if added, test it with controlled passing/failing invocations.
- Integration tests: not applicable; no runtime behavior changes.
- Contract tests: not applicable; no REST Docs, OpenAPI, HTTP examples, README public contract, or public API behavior changes.
- Smoke or benchmark tests: not applicable; no runtime or performance behavior changes.
- Documentation verification: required through ownership guard/searches, prompt smoke checks, template review, `git diff --check`, wrapper build, and manual owner-alignment review.

## Better Engineering Notes
- Variant 3 should be selected only if the repository expects recurring multi-plan coordination. Otherwise Variant 2 gives nearly the same read-set win with less vocabulary churn.
- Treat the coordinator template as an executable plan skeleton, not a reference essay.
- Keep the single-plan fanout reference separate; this plan changes only the multi-plan orchestration model.

## Validation Results
- 2026-05-07 plan creation:
  - Read the scratch recommendation file at `C:\Users\kamki\AppData\Roaming\JetBrains\IntelliJIdea2026.1\scratches\what_about_moving_workflow_selection_to.md` and converted Variant 3 into this option plan.
  - Updated `ROADMAP.md` Intake with the `Workflow-selection hard split with multi-plan template` candidate entry.
  - Ran `git diff --check`; passed.
  - Ran `rg -n "Workflow-selection|PLAN_workflow_selection|Variant 1|Variant 2|Variant 3" ROADMAP.md ai -g "PLAN_workflow_selection_*.md"`; passed and confirmed the three roadmap entries plus variant plan references.
  - Ran `./build.ps1 build`; passed through the lightweight-only shortcut with changed files limited to the three workflow-selection plans and `ROADMAP.md`, so Gradle was skipped.
  - Implementation remains blocked until the user selects exactly one workflow-selection variant and confirms the multi-plan coordinator-template abstraction is warranted.

## User Validation
- Review `ai/PLAN.md` and confirm only linear and single-plan fanout remain execution modes.
- Review `ai/templates/PLAN_MULTI_FANOUT_TEMPLATE.md` and confirm it captures child plans, integration order, changelog merge rules, validation fan-in, and completion gates.
- Run renamed prompt-loader smoke checks and confirm the prompt titles describe coordinator-plan work.
- Confirm live docs no longer present multi-plan work as a normal execution mode.

## Required Content Checklist
- Behavior changing: AI workflow mode ownership changes, execution guide splits, and multi-plan fanout becomes a coordinator-plan template.
- Roadmap tracking: `ROADMAP.md` / `Intake` / `Workflow guidance restructure variants` / Variant 3.
- Out of scope: executing child plans, public API behavior, setup behavior, release automation, archived-plan rewrites.
- Governing artifacts: scratch recommendation file, `ai/PLAN.md`, `ai/WORKFLOW.md`, `ai/EXECUTION.md`, fanout references, plan templates, prompt/template/skill artifacts.
- Likely files: named in `Affected Artifacts`.
- Compatibility promises: public app contract unchanged, default execution remains linear, single-plan fanout remains available, multi-plan coordination retains completion and changelog safeguards.
- Risks: premature abstraction, template bloat, prompt rename churn, hidden coordinator gates, noisy guard.
- Requirement gaps: blocked until the user selects this variant and confirms the multi-plan template is worth introducing.
- Execution mode: `Linear Plan`.
- Coordinator-owned files: all shared AI owner guides, fanout references, templates, prompts, skills, roadmap, changelog, evaluation report, and guard script.
- Per-milestone context: named explicitly in each milestone.
- Specs and docs: AI guidance, prompt/template artifacts, and human-facing AI workflow docs only; no app specs or public contract artifacts.
- Validation: ownership guard/searches, prompt smoke checks, template review, `git diff --check`, `./build.ps1 build`, manual owner-alignment review.
- User verification: inspect planning guide, coordinator template, prompt output, and mode vocabulary.
