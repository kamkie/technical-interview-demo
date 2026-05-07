# Plan: Post-Compaction AI Guidelines Evaluation

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Planning |
| Status | Blocked |

## Summary
- Evaluate and grade this repository's AI guideline set after two prerequisite plans have been implemented:
  - `ai/PLAN_workflow_on_demand_split.md`
  - `ai/PLAN_pskoett_ai_skill_guidance_adoption.md`
- Use `C:\Users\kamki\AppData\Roaming\JetBrains\IntelliJIdea2026.1\scratches\evaluate_and_grade_ai_guidelines_in_this.md` as source input, but rebaseline against the actual post-prerequisite repository state instead of copying its current grades forward.
- Produce a concise post-compaction evaluation that scores each standing AI guidance file, measures practical context-load impact, and ranks any remaining follow-up opportunities.
- Roadmap tracking: `ROADMAP.md` tracks this under `Ordered Plan` / `Moving to 2.0` / `AI Workflow Guidance` as a blocked follow-up after the workflow split and pskoett guidance adoption.
- Success means maintainers can see which gains were actually realized, which recommendations are now obsolete, and which remaining compaction ideas deserve separate execution plans.

## Scope
### In scope
- Wait until both prerequisite plans are implemented or otherwise closed with a clear final state.
- Re-read the final post-prerequisite versions of the standing AI guidance set before grading.
- Recompute or estimate standing-file size and default-read-set impact after the workflow split and pskoett guidance changes.
- Grade the standing AI guidance files using a consistent rubric:
  - owner clarity
  - default-load necessity
  - trigger clarity for on-demand material
  - duplication or policy drift
  - execution usefulness
  - validation and review routing
- Compare final state against the scratch note's recommendations:
  - workflow split
  - descriptive-doc on-demand policy
  - release-guide split
  - `PLAN.md` slimming
  - phase-to-guide context map
  - execution reading-list tightening
  - context-drop markers in plans
- Produce one on-demand evaluation report if the findings need to survive beyond this active plan, preferably `ai/references/AI_GUIDELINES_POST_COMPACTION_EVALUATION.md`.
- Recommend follow-up work ordering, but create new implementation plans only if the user explicitly asks or the evaluation itself reveals a concrete must-fix doc contradiction.

### Out of scope
- Executing before `ai/PLAN_workflow_on_demand_split.md` and `ai/PLAN_pskoett_ai_skill_guidance_adoption.md` have landed.
- Implementing the remaining compaction opportunities during this evaluation.
- Changing application source code, public API behavior, tests, REST Docs, OpenAPI, HTTP examples, README contract wording, setup instructions, build scripts, or release versioning.
- Rewriting archived plans just to update old workflow terminology.
- Treating the scratch note's `B+` grade or KB estimates as authoritative after the prerequisite plans change the docs.

## Current State
- The scratch file grades the pre-compaction AI guidance set as `B+` and identifies `ai/WORKFLOW.md` as the highest-ROI split candidate.
- `ai/PLAN_workflow_on_demand_split.md` is already planned to address the highest-ROI workflow recommendation by shrinking `ai/WORKFLOW.md`, moving fanout mechanics into on-demand references, adding a mode-selection gate, and renaming modes by plan topology.
- `ai/PLAN_pskoett_ai_skill_guidance_adoption.md` is already planned to add execution-quality checkpoints in `AGENTS.md` and `ai/EXECUTION.md`.
- Those prerequisite changes will materially affect the grading baseline, the default-read-set estimate, and the remaining opportunity list.
- The evaluation source file is outside the repository in the IntelliJ scratches directory. It should be treated as input material, not as a tracked repository artifact.

## Requirement Gaps And Open Questions
- Execution is blocked until both prerequisite plans have been implemented or intentionally closed.
- The exact post-prerequisite mode names and file layout must be read from repo truth at execution time.
- The user has not specified whether the final evaluation report should be committed as an on-demand reference or only summarized in this plan.
  Fallback: create `ai/references/AI_GUIDELINES_POST_COMPACTION_EVALUATION.md` when the evaluation contains reusable grading detail; otherwise keep the final summary in this plan's `Validation Results`.
- The user has not requested implementation of any remaining recommendations.
  Fallback: rank follow-up opportunities and stop before implementation-plan creation unless a contradiction must be fixed to make the evaluation accurate.

## Locked Decisions And Assumptions
- Execute this plan only after:
  - `ai/PLAN_workflow_on_demand_split.md` reaches `Phase=Integration` / `Status=Implemented`, `Closed`, or an equivalent final state recorded in its validation results.
  - `ai/PLAN_pskoett_ai_skill_guidance_adoption.md` reaches `Phase=Integration` / `Status=Implemented`, `Closed`, or an equivalent final state recorded in its validation results.
- If either prerequisite changes scope substantially, revise this plan before grading.
- Treat `AGENTS.md`, owner guides under `ai/`, prompt indexes, prompt bodies, and on-demand references as the evaluation surface.
- Keep the final grading report on demand; do not add a new standing top-level AI guide for evaluation history.
- Do not implement follow-up compaction in this plan. Separate implementation plans are required for accepted follow-up changes such as a release-guide split or `PLAN.md` slimming.

## Execution Mode Fit
- Recommended execution mode: current `Single Branch`, expected to be renamed to `Linear Plan` after `ai/PLAN_workflow_on_demand_split.md`.
- This is a documentation-only evaluation with shared ownership across AI guidance files; worker fanout would create terminology and scoring drift.
- Coordinator-owned files:
  - `ai/PLAN_ai_guidelines_post_compaction_evaluation.md`
  - `ROADMAP.md`
  - optional `ai/references/AI_GUIDELINES_POST_COMPACTION_EVALUATION.md`
- Candidate worker boundaries: none recommended. If the final evaluation is delegated for review, keep the scoring rubric, report, roadmap, and plan updates coordinator-owned.

## Affected Artifacts
- Planning and roadmap:
  - `ai/PLAN_ai_guidelines_post_compaction_evaluation.md`
  - `ROADMAP.md`
- Source input outside the repository:
  - `C:\Users\kamki\AppData\Roaming\JetBrains\IntelliJIdea2026.1\scratches\evaluate_and_grade_ai_guidelines_in_this.md`
- Likely evaluation output during execution:
  - `ai/references/AI_GUIDELINES_POST_COMPACTION_EVALUATION.md`
- AI guidance reviewed during execution, without assuming edits:
  - `AGENTS.md`
  - standing owner guides under `ai/*.md` excluding active and archived `ai/PLAN_*.md`
  - `ai/prompts/index.json`
  - relevant prompt bodies under `ai/prompts/bodies/`
  - relevant references under `ai/references/`
- Application specs and contracts:
  - no tests, REST Docs, OpenAPI, HTTP examples, README, SETUP, source files, or build scripts should change.

## Execution Milestones
### Milestone 1: Plan And Roadmap
- goal: record the post-prerequisite evaluation as a concrete blocked follow-up plan.
- owned files or packages:
  - `ai/PLAN_ai_guidelines_post_compaction_evaluation.md`
  - `ROADMAP.md`
- shared files that a fanout worker must leave to the coordinator:
  - not applicable in the default single-agent mode
- behavior to preserve:
  - no AI guidance implementation changes
  - no application or public-contract changes
- exact deliverables:
  - blocked plan with dependency gates, scope, milestones, validation, and user validation
  - roadmap entry after the two prerequisite AI workflow plans
- validation checkpoint:
  - `git diff --check`
  - `./build.ps1 build` lightweight-file shortcut or full build if the wrapper requires it
- commit checkpoint:
  - `docs: plan post-compaction ai guideline evaluation`

### Milestone 2: Prerequisite Gate And Rebaseline
- goal: confirm the two prerequisite plans are complete enough for this evaluation to be meaningful.
- owned files or packages:
  - this plan's `Lifecycle`, `Current State`, and `Validation Results`
- shared files that a fanout worker must leave to the coordinator:
  - this plan and `ROADMAP.md`
- behavior to preserve:
  - do not grade stale pre-compaction docs
  - do not force prerequisite implementation inside this plan
- exact deliverables:
  - verify final states and validation results for:
    - `ai/PLAN_workflow_on_demand_split.md`
    - `ai/PLAN_pskoett_ai_skill_guidance_adoption.md`
  - update this plan from `Planning` / `Blocked` to `Planning` / `Ready` or `Implementation` / `In Progress` only after the gate passes
  - revise terminology in this plan if workflow mode names changed differently than expected
  - list the final AI guidance files and references to grade
- validation checkpoint:
  - targeted roadmap and plan-state review
  - `rg` search for prerequisite plan lifecycle/status lines
- commit checkpoint:
  - `docs: unblock ai guideline evaluation`

### Milestone 3: Score The Post-Prerequisite AI Guidance Set
- goal: produce a current-state grade and evidence-backed file-by-file assessment.
- owned files or packages:
  - optional `ai/references/AI_GUIDELINES_POST_COMPACTION_EVALUATION.md`
  - this plan's `Validation Results`
- shared files that a fanout worker must leave to the coordinator:
  - this plan and `ROADMAP.md`
- behavior to preserve:
  - active owner docs remain role-distinct
  - on-demand references are not bulk-loaded unless needed for cross-reference checks
- exact deliverables:
  - measure standing guidance sizes and note which files belong to the default read set, phase-specific read set, or on-demand-only set
  - grade each standing guidance file with short rationale
  - identify realized gains from the workflow split and pskoett adoption
  - identify any regressions, such as `AGENTS.md` bloat, duplicated mode policy, prompt bodies growing into policy dumps, or missing load triggers
  - compare current grades against the scratch file's pre-compaction expectations
- validation checkpoint:
  - manual consistency review against `AGENTS.md`, `ai/DOCUMENTATION.md`, and `ai/REVIEWS.md`
  - targeted searches for duplicate or stale workflow terminology, excluding `ai/archive/` unless historical context is intentionally inspected
- commit checkpoint:
  - `docs: grade post-compaction ai guidelines`

### Milestone 4: Rank Follow-Up Opportunities
- goal: turn evaluation findings into a short, actionable backlog without implementing it.
- owned files or packages:
  - optional `ai/references/AI_GUIDELINES_POST_COMPACTION_EVALUATION.md`
  - this plan's `Validation Results`
- shared files that a fanout worker must leave to the coordinator:
  - `ROADMAP.md` unless the user approves adding selected follow-up roadmap entries
- behavior to preserve:
  - no hidden implementation work
  - no new active plans unless explicitly requested or needed to fix a blocking contradiction
- exact deliverables:
  - classify scratch recommendations as:
    - already handled by prerequisites
    - still high ROI
    - lower ROI or not worth doing now
    - obsolete after implementation
  - likely candidates to evaluate include:
    - splitting release runbook details from `ai/RELEASES.md`
    - slimming `ai/PLAN.md` by pushing skeleton detail to `ai/templates/PLAN_TEMPLATE.md`
    - adding a phase-to-guide-set map without bloating `AGENTS.md`
    - tightening `ai/EXECUTION.md` conditional read triggers
    - clarifying when descriptive docs such as `ai/ARCHITECTURE.md`, `ai/BUSINESS_MODULES.md`, `ai/DESIGN.md`, and `ai/LEARNINGS.md` should be loaded
    - adding per-milestone context requirements only if they reduce real execution noise
  - recommend execution order for any accepted follow-up plans
- validation checkpoint:
  - confirm recommendations route standing policy to the correct owning documents
  - confirm no follow-up is represented as completed work
- commit checkpoint:
  - `docs: rank ai guideline follow-up opportunities`

### Milestone 5: Final Validation And Closeout
- goal: validate the documentation-only evaluation and leave a clear handoff.
- owned files or packages:
  - all files touched during execution
  - this plan's `Lifecycle` and `Validation Results`
- shared files that a fanout worker must leave to the coordinator:
  - not applicable in the default single-agent mode
- behavior to preserve:
  - release work remains out of scope
  - no application behavior changes
- exact deliverables:
  - update this plan to the appropriate final non-release state
  - update `ROADMAP.md` if the plan moves from blocked to active or is completed
  - summarize final grade, realized gains, and top follow-up recommendations
- validation checkpoint:
  - `git diff --check`
  - `./build.ps1 build`
  - manual documentation-owner and cross-reference review
- commit checkpoint:
  - `docs: close post-compaction ai guideline evaluation`

## Edge Cases And Failure Modes
- Running this plan before the prerequisites land would double-count stale recommendations and misgrade `ai/WORKFLOW.md`.
- The workflow split may rename mode terminology differently than expected. Rebaseline from actual files before any search or grading.
- The pskoett plan may add helpful safeguards but also grow `AGENTS.md` or `ai/EXECUTION.md`; grade both benefit and context cost.
- Measuring raw file size can overstate or understate practical context savings. Pair size data with load-trigger analysis.
- A grading report can become stale quickly. Keep it on demand and current-state oriented; do not turn it into a new standing policy file.
- Follow-up recommendations can accidentally become hidden scope. Stop at ranking unless the user explicitly asks for new implementation plans.

## Validation Plan
- Plan creation validation:
  - `git diff --check`
  - `./build.ps1 build`
- Execution validation after prerequisites:
  - confirm prerequisite plan states and validation results
  - measure current standing guidance sizes, for example with PowerShell `Get-ChildItem` and `Measure-Object`
  - run targeted `rg` searches for stale workflow names, duplicated policy, and moved-reference mentions, excluding `ai/archive/` by default
  - run prompt-loader smoke checks only if the evaluation touches prompt titles or prompt metadata
  - run `git diff --check`
  - run `./build.ps1 build`
- Manual review:
  - verify owner alignment through `ai/DOCUMENTATION.md`
  - verify review lens through `ai/REVIEWS.md`
  - verify the report does not duplicate standing policy that belongs in an owner guide

## Testing Strategy
- Unit tests: not applicable; no executable logic changes.
- Integration tests: not applicable; no database, service, HTTP, OAuth, or runtime behavior changes.
- Contract tests: not applicable; no REST Docs, OpenAPI, HTTP examples, README public contract, or public API behavior changes.
- Smoke or benchmark tests: not applicable; no runtime or performance behavior changes.
- Prompt smoke checks: conditional, only if execution changes prompt titles, prompt index metadata, or prompt bodies.
- Manual documentation checks: required for grading quality, cross-reference consistency, owner alignment, and post-prerequisite dependency handling.

## Better Engineering Notes
- This is an evaluation plan, not a compaction implementation plan. It should convert a broad scratch critique into a measured post-change report and a small ranked backlog.
- The strongest likely value is preventing the first two AI-guidance plans from being treated as enough without measuring whether the default context load actually improved.
- If the evaluation finds one obvious next implementation target, create one narrow follow-up plan instead of bundling several compactions into a single large edit.

## Validation Results
- 2026-05-07 plan creation:
  - `git diff --check` passed.
  - `./build.ps1 build` passed through the lightweight-file shortcut, reporting that only `ai/PLAN_ai_guidelines_post_compaction_evaluation.md` and `ROADMAP.md` changed and that the Gradle build was skipped.
  - Manual plan-shape review confirmed all required sections from `ai/PLAN.md` are present.
- Execution is blocked until `ai/PLAN_workflow_on_demand_split.md` and `ai/PLAN_pskoett_ai_skill_guidance_adoption.md` are implemented or otherwise closed.

## User Validation
- Before execution, confirm both prerequisite plans have landed and their final states are reflected in `ROADMAP.md`.
- After execution, review the final grade, realized-gains summary, and ranked follow-up list.
- Confirm whether any recommended follow-up should become a new concrete `ai/PLAN_*.md`.
