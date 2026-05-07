# Plan: Post-Compaction AI Guidelines Evaluation

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Planning |
| Status | Ready |

## Summary
- Evaluate and grade this repository's AI guideline set after the two prerequisite AI-guidance plans landed:
  - `ai/PLAN_workflow_on_demand_split.md`
  - `ai/PLAN_pskoett_ai_skill_guidance_adoption.md`
- Use `C:\Users\kamki\AppData\Roaming\JetBrains\IntelliJIdea2026.1\scratches\evaluate_and_grade_ai_guidelines_in_this.md` as historical source input, but score against current repository truth instead of copying its old `B+` grade or size estimates.
- Replan result: the prerequisite gate has passed, the plan is no longer blocked, and the remaining work is a focused documentation-only evaluation report plus ranked follow-up backlog.
- Roadmap tracking: `ROADMAP.md` tracks this under `Ordered Plan` / `Moving to 2.0` / `AI Workflow Guidance` as selected AI-guideline evaluation work.
- Success means maintainers can see which compaction gains were actually realized, which scratch recommendations are obsolete, and which remaining opportunities deserve separate implementation plans.

## Scope
### In scope
- Re-read the current standing AI guidance set before grading.
- Create a reusable on-demand report at `ai/references/AI_GUIDELINES_POST_COMPACTION_EVALUATION.md`.
- Measure standing-file size and practical default-read-set impact after the workflow split and pskoett guidance changes.
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
- Rank follow-up work, but stop before implementation-plan creation unless the user explicitly asks or the evaluation reveals a blocking contradiction.
- Keep this plan, `ROADMAP.md`, and unreleased history aligned during execution.

### Out of scope
- Implementing any remaining compaction opportunities during this evaluation.
- Creating new `ai/PLAN_*.md` implementation plans unless explicitly requested after the evaluation.
- Changing application source code, public API behavior, tests, REST Docs, OpenAPI, HTTP examples, README contract wording, setup instructions, build scripts, or release versioning.
- Rewriting archived plans just to update old workflow terminology.
- Treating the scratch note's `B+` grade, default-context target, or KB estimates as authoritative after the prerequisite plans changed the docs.

## Current State
- `ai/PLAN_workflow_on_demand_split.md` is `Phase=Integration` / `Status=Implemented`; it moved fanout mechanics into `ai/references/WORKFLOW_SINGLE_PLAN_FANOUT.md` and `ai/references/WORKFLOW_MULTI_PLAN_FANOUT.md`, and active docs now use `Linear Plan`, `Single-Plan Fanout`, and `Multi-Plan Fanout`.
- `ai/PLAN_pskoett_ai_skill_guidance_adoption.md` is `Phase=Integration` / `Status=Implemented`; it added targeted relevance scanning, context-quality degradation checkpoints, per-milestone scope checks, and post-validation review triggers.
- The scratch recommendation to split `ai/WORKFLOW.md` is now handled in repo-native form, though the final router is about 9.1 KB / 151 lines rather than the scratch's smaller target.
- A rebaseline size check on 2026-05-07 measured current standing guidance files at roughly:
  - `AGENTS.md`: 14.7 KB / 168 lines
  - `ai/WORKFLOW.md`: 9.1 KB / 151 lines
  - `ai/RELEASES.md`: 8.3 KB / 88 lines
  - `ai/PLAN.md`: 6.7 KB / 120 lines
  - `ai/EXECUTION.md`: 6.7 KB / 87 lines
  - `ai/DESIGN.md`: 6.4 KB / 107 lines
  - remaining standing owner guides: each about 2.1 KB to 5.0 KB
- Practical minimum read-set estimates are now more useful than the scratch's old total standing-payload estimate:
  - planning minimum: `AGENTS.md` + `ai/PLAN.md`, about 21.4 KB
  - implementation minimum: `AGENTS.md` + `ai/EXECUTION.md`, about 21.4 KB
  - workflow selection: `AGENTS.md` + `ai/WORKFLOW.md` + `ai/EXECUTION.md`, about 30.5 KB
  - verification: `AGENTS.md` + `ai/TESTING.md` + `ai/DOCUMENTATION.md` + `ai/REVIEWS.md`, about 27.1 KB
  - release: `AGENTS.md` + `ai/RELEASES.md`, about 23.0 KB
- Targeted stale-terminology search found old workflow names only in intentional migration notes inside `ai/PLAN_workflow_on_demand_split.md`, not in active owner guides or prompt titles.
- The scratch file remains useful for comparison, but the final report must evaluate current artifacts directly.

## Requirement Gaps And Open Questions
- No blocker remains: both prerequisite plans are implemented.
- The final evaluation report should be committed as an on-demand reference because the grading detail and size baseline are reusable but should not become standing policy.
- The user has not requested implementation of any follow-up recommendations.
  Fallback: rank follow-up opportunities and stop; create new plans only after explicit user direction.
- If evaluation finds a concrete contradiction in standing guidance, record it as a finding and recommend a follow-up plan unless the contradiction makes the report itself inaccurate.

## Locked Decisions And Assumptions
- Execute in `Linear Plan`; worker fanout would create scoring drift for a small documentation evaluation.
- Treat `AGENTS.md`, standing owner guides under `ai/*.md`, prompt indexes, prompt bodies, and on-demand references as the evaluation surface.
- Keep the final grading report on demand at `ai/references/AI_GUIDELINES_POST_COMPACTION_EVALUATION.md`; do not add a new top-level AI guide for evaluation history.
- Do not implement follow-up compaction in this plan. Separate implementation plans are required for accepted follow-up changes such as a release-guide split, `PLAN.md` slimming, or AGENTS load-policy compaction.
- Exclude `ai/archive/` from routine terminology and duplication searches unless historical context is intentionally being inspected.

## Execution Mode Fit
- Selected execution mode: `Linear Plan`.
- This is a documentation-only evaluation with shared ownership across AI guidance files; worker fanout would add coordination cost and inconsistent scoring risk.
- Coordinator-owned files:
  - `ai/PLAN_ai_guidelines_post_compaction_evaluation.md`
  - `ROADMAP.md`
  - `CHANGELOG.md`
  - `ai/references/AI_GUIDELINES_POST_COMPACTION_EVALUATION.md`
- Read-only evaluation inputs unless a contradiction is discovered:
  - `AGENTS.md`
  - standing owner guides under `ai/*.md` excluding active and archived `ai/PLAN_*.md`
  - `ai/prompts/index.json`
  - relevant prompt bodies under `ai/prompts/bodies/`
  - relevant references under `ai/references/`
- Candidate worker boundaries: none recommended. If delegated review is later requested, keep scoring rubric, report, roadmap, changelog, and plan updates coordinator-owned.

## Affected Artifacts
- Planning and roadmap:
  - `ai/PLAN_ai_guidelines_post_compaction_evaluation.md`
  - `ROADMAP.md`
- Evaluation report:
  - `ai/references/AI_GUIDELINES_POST_COMPACTION_EVALUATION.md`
- Unreleased history during execution:
  - `CHANGELOG.md`
- Source input outside the repository:
  - `C:\Users\kamki\AppData\Roaming\JetBrains\IntelliJIdea2026.1\scratches\evaluate_and_grade_ai_guidelines_in_this.md`
- Application specs and contracts:
  - no tests, REST Docs, OpenAPI, HTTP examples, README, SETUP, source files, or build scripts should change.

## Execution Milestones
### Milestone 0: Plan Creation And Replan Baseline
- status: complete after this replan is validated and committed.
- goal: unblock the evaluation after the two prerequisite plans landed and revise the remaining work against current repo truth.
- owned files or packages:
  - `ai/PLAN_ai_guidelines_post_compaction_evaluation.md`
  - `ROADMAP.md`
- shared files that a `Single-Plan Fanout` worker must leave to the coordinator:
  - not applicable in `Linear Plan`
- behavior to preserve:
  - no AI-guidance implementation changes
  - no application or public-contract changes
- exact deliverables:
  - lifecycle moved from `Planning` / `Blocked` to `Planning` / `Ready`
  - current-state rebaseline records prerequisite completion, current size measurements, and stale-terminology scan result
  - roadmap entry points to this plan as ready selected work
- validation checkpoint:
  - manual consistency check against `AGENTS.md`, `ai/PLAN.md`, `ai/DOCUMENTATION.md`, and prerequisite plan lifecycle states
  - `git diff --check`
  - `./build.ps1 build` lightweight-file shortcut or full build if the wrapper requires it
- commit checkpoint:
  - `docs: replan post-compaction ai guideline evaluation`

### Milestone 1: Current-State Evaluation Report
- goal: produce the evidence-backed post-compaction grade and file-by-file assessment.
- owned files or packages:
  - `ai/references/AI_GUIDELINES_POST_COMPACTION_EVALUATION.md`
  - this plan's `Validation Results`
- shared files that a `Single-Plan Fanout` worker must leave to the coordinator:
  - this plan, `ROADMAP.md`, and `CHANGELOG.md`
- behavior to preserve:
  - active owner docs remain role-distinct
  - on-demand references are opened only when needed for cross-reference checks
  - the report is evaluation history, not standing policy
- exact deliverables:
  - measure standing guidance sizes and classify each file as default read, phase-specific read, or on-demand-only
  - grade each standing guidance file with short rationale
  - identify realized gains from the workflow split and pskoett adoption
  - identify regressions or remaining costs, especially `AGENTS.md` size, duplicated policy, prompt bodies growing into policy dumps, stale triggers, or unclear default read sets
  - compare current grades against the scratch file's pre-compaction expectations
- validation checkpoint:
  - manual consistency review against `AGENTS.md`, `ai/DOCUMENTATION.md`, and `ai/REVIEWS.md`
  - targeted searches for duplicate or stale workflow terminology, excluding `ai/archive/` unless historical context is intentionally inspected
  - `git diff --check`
- commit checkpoint:
  - `docs: grade post-compaction ai guidelines`

### Milestone 2: Follow-Up Ranking And Closeout
- goal: turn the evaluation into a short, actionable backlog and leave execution status clear.
- owned files or packages:
  - `ai/references/AI_GUIDELINES_POST_COMPACTION_EVALUATION.md`
  - this plan's `Lifecycle` and `Validation Results`
  - `ROADMAP.md`
  - `CHANGELOG.md`
- shared files that a `Single-Plan Fanout` worker must leave to the coordinator:
  - not applicable in `Linear Plan`
- behavior to preserve:
  - no hidden implementation work
  - no new active implementation plans unless explicitly requested
  - release work remains out of scope
- exact deliverables:
  - classify scratch recommendations as already handled, still high ROI, lower ROI, or obsolete
  - likely candidates to rank include:
    - reducing `AGENTS.md` standing load without moving repo-level rules out of their owner
    - splitting release runbook details from `ai/RELEASES.md`
    - slimming `ai/PLAN.md` by pushing skeleton detail to `ai/templates/PLAN_TEMPLATE.md`
    - clarifying phase-to-guide read sets without bloating `AGENTS.md`
    - tightening `ai/EXECUTION.md` conditional read triggers
    - clarifying when descriptive docs such as `ai/ARCHITECTURE.md`, `ai/BUSINESS_MODULES.md`, `ai/DESIGN.md`, and `ai/LEARNINGS.md` should be loaded
    - adding per-milestone context requirements only if they reduce real execution noise
  - update `CHANGELOG.md` under `## [Unreleased]`
  - update this plan to the final non-release execution state, normally `Phase=Integration` / `Status=Implemented`
  - update `ROADMAP.md` to match the plan state
- validation checkpoint:
  - confirm recommendations route standing policy to the correct owning documents
  - confirm no follow-up is represented as completed work
  - `git diff --check`
  - `./build.ps1 build`
- commit checkpoint:
  - `docs: close post-compaction ai guideline evaluation`

## Edge Cases And Failure Modes
- Reusing the scratch grade would double-count stale recommendations and misgrade the current `ai/WORKFLOW.md`.
- Measuring raw file size can overstate or understate practical context savings. Pair size data with load-trigger analysis.
- A grading report can become stale quickly. Keep it on demand and current-state oriented; do not turn it into a standing policy file.
- Follow-up recommendations can accidentally become hidden implementation scope. Stop at ranking unless the user explicitly asks for new implementation plans.
- Adding a phase-to-guide map can bloat `AGENTS.md`; evaluate whether the better owner is `ai/PROMPTS.md`, `ai/DOCUMENTATION.md`, or a reference before recommending implementation.
- Shrinking `AGENTS.md` must not hide repo-level rules that all agents need before selecting a phase-specific guide.

## Validation Plan
- Replan validation:
  - confirm prerequisite plan states and validation results
  - confirm `ROADMAP.md` reflects the unblocked plan path and current status
  - run `git diff --check`
  - run `./build.ps1 build`
- Execution validation:
  - measure current standing guidance sizes with PowerShell `Get-ChildItem` and line counts
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
- The strongest remaining value is evaluating whether `AGENTS.md`, `ai/RELEASES.md`, and `ai/PLAN.md` now dominate the standing read set after the workflow split.
- If the evaluation finds one obvious next implementation target, create one narrow follow-up plan only after the user approves that follow-up.

## Validation Results
- 2026-05-07 plan creation:
  - `git diff --check` passed.
  - `./build.ps1 build` passed through the lightweight-file shortcut, reporting that only `ai/PLAN_ai_guidelines_post_compaction_evaluation.md` and `ROADMAP.md` changed and that the Gradle build was skipped.
  - Manual plan-shape review confirmed all required sections from `ai/PLAN.md` are present.
- 2026-05-07 replan:
  - Prerequisite gate passed: `ai/PLAN_workflow_on_demand_split.md` and `ai/PLAN_pskoett_ai_skill_guidance_adoption.md` are both `Phase=Integration` / `Status=Implemented`.
  - Source scratch input was read from `C:\Users\kamki\AppData\Roaming\JetBrains\IntelliJIdea2026.1\scratches\evaluate_and_grade_ai_guidelines_in_this.md` and treated as historical input, not authority.
  - Standing-guide size and practical read-set estimates were recomputed for the current repository state and recorded in `Current State`.
  - Targeted stale-terminology search found old workflow names only in intentional migration notes inside `ai/PLAN_workflow_on_demand_split.md`.
  - Manual consistency check passed against `AGENTS.md`, `ai/PLAN.md`, `ai/DOCUMENTATION.md`, and prerequisite plan lifecycle states.
  - `git diff --check` passed.
  - `./build.ps1 build` passed through the lightweight-file shortcut, reporting docs-only changes to this plan and `ROADMAP.md` and skipping the Gradle build.

## User Validation
- Review this replan and confirm the remaining scope is only the evaluation report plus ranked follow-up backlog.
- After execution, review the final grade, realized-gains summary, and ranked follow-up list.
- Confirm whether any recommended follow-up should become a new concrete `ai/PLAN_*.md`.
