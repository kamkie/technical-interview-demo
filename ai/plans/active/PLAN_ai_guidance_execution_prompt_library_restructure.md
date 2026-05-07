# Plan: AI Guidance Execution And Reusable Task Library Restructure

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Integration |
| Status | Implemented |

## Summary
- Restructure the AI guidance set so planning, whole-plan execution, task or milestone execution, workflow coordination, and reusable task starters have distinct owners.
- Remove the current named mode vocabulary from live standing guides, prompt/task starters, templates, skills, and active plan guidance.
- Rename the planning guide to `ai/PLANNING.md`, move active execution plans under `ai/plans/active/`, and redesign the current prompt storage into a reusable task library.
- Success means an agent can create a plan, execute a whole plan, execute an ad hoc task or one plan milestone, delegate or integrate work, and load reusable task starters without opening stale or overlapping guidance.

## Scope
- In scope:
  - regenerate `ai/WORKFLOW.md` as the owner for branch, worktree, delegation, worker, and integration coordination only
  - regenerate `ai/EXECUTION.md` as the owner for ad hoc task execution and individual plan-milestone execution
  - create `ai/PLAN_EXECUTION.md` as the owner for executing a whole active plan across milestones
  - rename the former planning guide to `ai/PLANNING.md`
  - move active plans from the former top-level plan locations to `ai/plans/active/PLAN_*.md`
  - update `AGENTS.md`, `WORKING_WITH_AI.md`, `ROADMAP.md`, `ai/DOCUMENTATION.md`, templates, references, skills, and scripts for the new paths and owners
  - replace the prompt index/body layout with a reusable task library while keeping loader behavior deterministic and without any backward-compatible prompt-loader alias
  - evaluate a narrow repo-local skill for ad hoc task execution, but defer creation unless it can wrap the new `ai/EXECUTION.md` without copying policy and passes the acceptance criteria in this plan
  - run the `Compact AI Docs` maintenance task after the structure lands
  - regenerate `ai/references/AI_GUIDELINES_POST_COMPACTION_EVALUATION.md`
  - record the implemented AI-guidance change in `CHANGELOG.md`
- Out of scope:
  - changing application runtime behavior, public API contracts, tests, REST Docs, OpenAPI, HTTP examples, deployment assets, or setup behavior
  - cutting a release
  - rewriting archived plans only to remove historical wording
  - merging prior workflow-selection variant branches or cherry-picking from them without a separate explicit decision

## Current State
- `ai/PLANNING.md`, `ai/EXECUTION.md`, and `ai/WORKFLOW.md` currently share responsibilities for plan creation, plan execution, milestone execution, branch/worktree rules, delegation, and integration.
- The former active plan locations used the same `PLAN_*.md` prefix family that proposed new guide names such as `PLAN_EXECUTION.md` would also match.
- `ai/PROMPTS.md`, `ai/prompts/index.json`, `ai/prompts/bodies/`, and `scripts/ai/get-prompt.ps1` currently describe reusable starters as prompts rather than reusable task definitions.
- Live docs, task starters, templates, references, and repo-local skills contain current mode-oriented wording that will be stale after this redesign.
- `ROADMAP.md` now points at this plan instead of the three narrower workflow-selection candidate rows.
- The repository still contains those three former option plan files; implementation must archive them as superseded historical plans under `ai/archive/`, not move them to the new active-plan subdirectory and not delete them.
- The untracked `ai/references/WORKFLOW_SELECTION_VARIANT_COMPARISON.md` is a useful decision aid, but it is not standing policy and should not be edited unless the user wants to keep it.

## Resolved Requirement Decisions And Evaluations
- Prompt loader compatibility:
  Decision: no backward-compatible prompt loader alias remains after the task-library migration. Active docs and scripts move to the new task loader, and `scripts/ai/get-prompt.ps1` is removed rather than retained as a wrapper or alias.
- Superseded workflow-selection candidate plans:
  Decision: archive `ai/archive/PLAN_workflow_selection_soft_consolidation.md`, `ai/archive/PLAN_workflow_selection_hard_split.md`, and `ai/archive/PLAN_workflow_selection_multi_plan_template.md` under `ai/archive/` during the plan-location migration. Do not move them into `ai/plans/active/` and do not delete their historical content.
- Ad hoc task execution skill:
  Evaluation: immediate creation would give users a short reusable entry point for unplanned tasks and single-milestone work, and it could reduce repeated prompt-body wording after the task-library migration. The drawbacks are stronger: a generic ad hoc executor can duplicate `ai/EXECUTION.md`, add another maintenance surface, increase loaded context, and encourage bypassing the plan-promotion decision for work that should be planned. Decision: defer by default. Create `ai/skills/repo-ad-hoc-task-executor/SKILL.md` only if post-split usage still needs a reusable wrapper, the skill stays under one screen, it links to owner guides instead of restating policy, and its trigger is narrower than normal task execution.
- Task library directory name:
  Evaluation: current agent ecosystems do not define one shared repo-local directory for reusable task starters. Claude Code uses `.claude/skills/<skill-name>/SKILL.md` for reusable procedures; GitHub Copilot uses `.github/prompts/*.prompt.md` for reusable prompt files and `.github/instructions/*.instructions.md` for scoped instructions; Junie prefers `.junie/AGENTS.md` for guidelines and supports root `AGENTS.md`. That means `ai/tasks/` and `ai/task-library/` are both repo-local choices, not industry-standard names. Decision: use `ai/task-library/` because it is explicit, matches this plan's "reusable task library" concept, and avoids confusion with Gradle tasks, roadmap tasks, and plan milestones.
- `ai/PLANNING.md` naming:
  Evaluation: `PLANNING.md` is not a cross-agent industry-standard filename; external agent conventions center on root instruction files and tool-specific folders. In this repository, top-level `ai/*.md` owner guides use uppercase process or responsibility names such as `DOCUMENTATION.md`, `EXECUTION.md`, `WORKFLOW.md`, `TESTING.md`, and `REVIEWS.md`. Decision: keep the planned `ai/PLANNING.md` rename because it is consistent with the repo's owner-guide convention and avoids confusion with concrete `PLAN_*.md` execution plan files.

## Locked Decisions And Assumptions
- The planning guide target name is `ai/PLANNING.md`; the common misspelling with one `n` is rejected as a typo.
- Active plans move to `ai/plans/active/` before `ai/PLAN_EXECUTION.md` is introduced, so broad `PLAN_*.md` scans can be retired or narrowed safely.
- Superseded workflow-selection candidate plans are archived under `ai/archive/` during the plan-location migration.
- `ai/PLAN_EXECUTION.md` owns whole-plan execution: readiness, plan scope, milestone sequencing, plan-level context switching, compaction checkpoints, validation rollup, roadmap status, and completion handoff.
- `ai/EXECUTION.md` owns ad hoc tasks and individual plan milestones: smallest useful read set, context switching for efficiency, spec-first changes, validation at the checkpoint, review, and when to promote ad hoc work into a plan.
- `ai/WORKFLOW.md` remains, but only for collaboration mechanics: branch/worktree hygiene, delegation boundaries, worker logs, integration, and remote handoff.
- The prompt system becomes a reusable task library, with standing policy kept in owner guides and task bodies kept procedural.
- `ai/TASK_LIBRARY.md` replaces `ai/PROMPTS.md` as the task-library index and usage guide.
- The task library lives under `ai/task-library/`.
- No old prompt-loader alias or compatibility wrapper remains after migration.
- The ad hoc task execution skill is deferred by default and can be created only if it meets the narrow-wrapper acceptance criteria in this plan.
- Public application behavior remains unchanged.

## Execution Shape And Shared Files
- Use one coordinated documentation-maintenance run because the same owner-guide paths, task-library names, and cross-references are shared across the repo.
- Do not split implementation until the new owner map and path migration are complete.
- Keep legacy workflow names out of this plan and the final live guidance. Describe the work shape in plain terms: coordinator-owned documentation run, optional read-only review, explicitly owned worker slice, or integration handoff.
- Coordinator-owned shared files:
  - `AGENTS.md`, `WORKING_WITH_AI.md`, `ROADMAP.md`, `CHANGELOG.md`
  - top-level `ai/*.md`
  - active plans under `ai/plans/active/`
  - `ai/templates/**`, `ai/references/**`, `ai/skills/**`
  - task-library metadata and bodies under `ai/task-library/**`
  - task-loader scripts under `scripts/ai/**`
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
  - former top-level active plan files
  - superseded workflow-selection candidate plans archived under `ai/archive/`
  - `ROADMAP.md`
  - release cleanup references under `ai/references/`
- Reusable task library:
  - `ai/TASK_LIBRARY.md` as the replacement for `ai/PROMPTS.md`
  - replacement for `ai/prompts/index.json`
  - replacement for `ai/prompts/bodies/`
  - `ai/task-library/index.json`
  - `ai/task-library/bodies/*.md`
  - new deterministic task loader, expected `scripts/ai/get-task.ps1`
  - removal of `scripts/ai/get-prompt.ps1`; no compatibility alias or wrapper remains
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
  - `AGENTS.md`, `ROADMAP.md`, `ai/DOCUMENTATION.md`, `ai/PLANNING.md`, `ai/EXECUTION.md`, `ai/WORKFLOW.md`, `ai/PROMPTS.md`, `ai/TESTING.md`, `ai/REVIEWS.md`, `ai/templates/PLAN_TEMPLATE.md`, `scripts/ai/get-prompt.ps1`, this plan, and the untracked variant comparison if still present
- behavior to preserve:
  - no standing rule is deleted before the new owner is named
  - archived plans remain historical unless a path move would make an active instruction misleading
- exact deliverables:
  - rule-to-owner map for planning, whole-plan execution, task/milestone execution, collaboration workflow, reusable tasks, validation, review, and documentation routing
  - current active-plan inventory and every live reference to the former top-level active-plan glob
  - current prompt/task starter inventory and proposed target names
  - list of exact legacy vocabulary to remove from live standing guidance
- validation checkpoint:
  - `git diff --check`
  - targeted reference inventory reviewed against `ai/DOCUMENTATION.md`
- commit checkpoint:
  - `docs: inventory ai guidance restructure`

### Milestone 2: Move Active Plans And Rename Planning Owner
- goal: eliminate former top-level plan-file naming collisions before adding new execution-guide names.
- owned files or packages:
  - `ai/plans/active/`
  - `ai/PLANNING.md`
  - removed or replaced `ai/PLANNING.md`
  - `AGENTS.md`
  - `ROADMAP.md`
  - `ai/DOCUMENTATION.md`
  - `ai/archive/`
  - active plan references found by Milestone 1
- context required before execution:
  - Milestone 1 owner map, former top-level active plans, `ROADMAP.md`, `ai/PLANNING.md`, `ai/DOCUMENTATION.md`, and this milestone
- behavior to preserve:
  - active release-gate plan status and roadmap ordering stay intact after the move
  - superseded workflow-selection candidate plans do not remain presented as current execution choices
- exact deliverables:
  - active plans live under `ai/plans/active/`
  - planning guide is available as `ai/PLANNING.md`
  - superseded workflow-selection candidate plans are archived under `ai/archive/`
  - live references use `ai/plans/active/PLAN_*.md` and `ai/PLANNING.md`
  - any compatibility pointer is short and temporary, or omitted if all live references are updated cleanly
- validation checkpoint:
  - targeted search shows no active live reference still expects the former top-level active-plan glob
  - targeted search shows the three superseded workflow-selection candidate plans no longer remain as active plans
  - targeted search shows no live reference uses the misspelled planning-guide filename
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
  - targeted search confirms legacy workflow names are absent from this plan, live standing guides, task starters, templates, and active plans; archived superseded plans may keep historical wording
  - `git diff --check`
- commit checkpoint:
  - `docs: split plan and task execution guidance`

### Milestone 4: Replace Prompt Storage With Reusable Task Library
- goal: make reusable starters task-shaped, discoverable, and clearly separate from standing policy.
- owned files or packages:
  - `ai/TASK_LIBRARY.md`
  - `ai/task-library/index.json`
  - `ai/task-library/bodies/*.md`
  - `scripts/ai/get-task.ps1`
  - removal of `scripts/ai/get-prompt.ps1`
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
  - loader smoke checks cover the new task-library behavior
  - old prompt invocations are not kept as aliases
  - `ai/DOCUMENTATION.md` owns the new task-library artifact routing
- validation checkpoint:
  - task loader list and single-task load smoke checks pass
  - task loader unknown-name check fails with a deterministic non-alias error
  - `Test-Path -LiteralPath scripts/ai/get-prompt.ps1` returns false
  - targeted search confirms no active docs or scripts still invoke `scripts/ai/get-prompt.ps1`
  - targeted search confirms active docs no longer describe the library as prompt-owned policy
  - `git diff --check`
- commit checkpoint:
  - `docs: migrate prompts to reusable task library`

### Milestone 5: Evaluate Ad Hoc Task Skill And Run Compaction
- goal: create the optional ad hoc execution wrapper only if it passes the narrow-wrapper acceptance criteria, then compact duplicate standing guidance.
- owned files or packages:
  - optional `ai/skills/repo-ad-hoc-task-executor/SKILL.md`
  - affected skill metadata or agent files
  - standing top-level AI guides changed by compaction
  - `CHANGELOG.md`
- context required before execution:
  - new `ai/EXECUTION.md`, `ai/DOCUMENTATION.md`, `ai/TESTING.md`, existing repo-local skill entrypoints, `ai/task-library/bodies/compact-ai-docs.md`, and this milestone
- behavior to preserve:
  - skills remain narrow wrappers and do not replace owner guides
  - compaction moves guidance to the single best owner rather than deleting unclear rules
- exact deliverables:
  - ad hoc task skill is deferred by default unless post-split usage shows repeated entry friction that `ai/EXECUTION.md` and the task library do not already solve
  - if created, the skill stays under one screen, links to `ai/EXECUTION.md`, `ai/DOCUMENTATION.md`, and `ai/TESTING.md`, avoids copied policy, and has a narrower trigger than normal task execution
  - if deferred, the plan records the rationale and no placeholder skill directory is created
  - `Compact AI Docs` task is loaded and applied to the changed standing AI documents
  - duplicated or stale guidance is removed or routed to the correct owner
  - `CHANGELOG.md` records the implemented AI-guidance restructure under `## [Unreleased]`
- validation checkpoint:
  - targeted duplicate/stale-reference searches from the compaction task pass or have recorded exceptions
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
  - `ai/task-library/bodies/evaluate-ai-guidelines.md`, changed standing guides, task-library inventory, active-plan inventory, `ai/TESTING.md`, `ai/REVIEWS.md`, `ai/DOCUMENTATION.md`, and this milestone
- behavior to preserve:
  - the evaluation report remains on demand
  - roadmap status changes only after implementation state actually changes
- exact deliverables:
  - refreshed evaluation date, grade, size baseline, practical read-set estimates, file grades, realized gains, risks, obsolete recommendations, and ranked follow-ups
  - final plan `Validation Results` updated with actual commands and outcomes
  - `ROADMAP.md` reflects the final active-work state
- validation checkpoint:
  - task loader smoke checks
  - task loader unknown-name check
  - old prompt-loader script nonexistence check
  - targeted stale-reference searches
  - `git diff --check`
  - `./build.ps1 build`
  - manual documentation/process review with `ai/REVIEWS.md`
- commit checkpoint:
  - `docs: refresh ai guidance evaluation`

## Edge Cases And Failure Modes
- Removing prompt-loader compatibility can break user muscle memory. Make active docs, task names, loader errors, and validation output point clearly to the new task loader instead of silently preserving old prompt names.
- Moving active plans can break scripts or prompt/task starters that glob the former top-level active-plan paths. Update those before adding new `PLAN_*.md` guide names.
- Renaming the planning guide can leave stale references in human-facing docs, templates, skills, and release references. Use targeted searches across live files.
- Task-library migration can break established prompt names. Use deterministic loader errors and updated active docs instead of compatibility aliases.
- A skill for ad hoc tasks can duplicate `ai/EXECUTION.md`. Defer it unless it clearly improves repeated entry workflow without becoming a second owner.
- The evaluation report can become stale if generated before compaction. Run compaction first, then regenerate the report.

## Validation Plan
- Run targeted reference searches over live files, excluding `ai/archive/**` except when checking release cleanup instructions.
- Run task loader smoke checks for list and single-task load paths.
- Run a task loader unknown-name check and confirm it fails deterministically instead of falling back to old prompt names.
- Confirm `scripts/ai/get-prompt.ps1` no longer exists.
- Confirm no active live file still invokes `scripts/ai/get-prompt.ps1`, `ai/prompts/`, or `ai/PROMPTS.md` after migration; only archived history and this plan's current-state or validation history may retain those strings.
- Run `git diff --check`.
- Run `./build.ps1 build`; for documentation-only AI-guidance changes, record whether the wrapper takes the lightweight-file shortcut or performs Gradle validation.
- Manually review changed docs with `ai/DOCUMENTATION.md` and `ai/REVIEWS.md`.

## Testing Strategy
- Unit tests: not applicable unless a new loader or guard script has testable logic; if added, run controlled positive and negative smoke checks.
- Integration tests: not applicable because runtime app behavior does not change.
- Contract tests: not applicable because REST Docs, OpenAPI, HTTP examples, and public API behavior do not change.
- Smoke or benchmark tests: not applicable because performance and deployment behavior do not change.
- Documentation verification: required through task loader positive and negative smoke checks, old-loader nonexistence check, targeted stale-reference searches, `git diff --check`, wrapper build, and manual owner-alignment review.

## Better Engineering Notes
- The prior workflow-selection variants are useful evidence but too narrow for this request. This plan should replace them rather than blend them.
- `ai/PLAN_EXECUTION.md` would have collided with broad active-plan globs; moving active plans first removes that concrete risk.
- Prefer the reusable task-library language because these artifacts are operational starters, not just prompts.
- External agent tooling does not provide a single standard `ai/tasks/`-style directory. Use the repository's `ai/task-library/` convention and route external tool discovery through `AGENTS.md` or tool-specific adapters only if that becomes a real need.
- `ai/PLANNING.md` is a repo-owner-guide name, not an external agent discovery file. Keep external compatibility in `AGENTS.md`; keep planning ownership under `ai/PLANNING.md`.
- Do not optimize only for total character count. Measure practical read sets for planning, whole-plan execution, ad hoc or milestone execution, workflow coordination, task library use, verification, and release.

## Plan Readiness Evaluation
- No blocking requirement gap remains. The plan locks no prompt-loader compatibility alias, archives old candidate plans, uses `ai/task-library/`, keeps `ai/PLANNING.md`, and defers the ad hoc execution skill unless acceptance criteria are met.
- Lifecycle is accurate: `Phase=Planning`, `Status=Ready`.
- The plan is ready for implementation after user selection, with one caveat: implementation should not interrupt the active `v2.0.0-RC6` manual regression gate unless the user intentionally reprioritizes roadmap work.
- The highest-risk areas are path migration and the non-compatible task-library loader cutover; both have early inventory, nonexistence checks, and positive and negative smoke-check milestones.

## Validation Results
- 2026-05-07 implementation Milestone 6 evaluation refresh and final validation:
  - Loaded the `Evaluate AI Guidelines` task with `pwsh ./scripts/ai/get-task.ps1 -Name "Evaluate AI Guidelines"`.
  - Regenerated `ai/references/AI_GUIDELINES_POST_COMPACTION_EVALUATION.md` for the new owner-guide and task-library structure, including `ai/PLAN_EXECUTION.md`, current active-plan inventory, task-library inventory, practical read-set estimates, file grades, realized gains, remaining risks, obsolete recommendations, and ranked follow-ups.
  - Recomputed the main measurement baselines: `AGENTS.md` is 13,251 characters / 3,313 estimated tokens; the 15-file standing owner-guide set is 80,900 characters / 20,230 estimated tokens; the two tracked active plans total 76,373 characters / 19,094 estimated tokens.
  - Added a focused `CHANGELOG.md` unreleased entry for the refreshed post-compaction AI guideline evaluation report.
  - Updated `ROADMAP.md` so the AI guidance restructure row is `Implemented`.
  - Corrected stale human-facing AI inventory references in `README.md` and `CONTRIBUTING.md` discovered by the tracked stale-reference search.
  - `pwsh ./scripts/ai/get-task.ps1 -List` passed and listed the task library.
  - `pwsh ./scripts/ai/get-task.ps1 -Name "Create Plan"` passed.
  - `pwsh ./scripts/ai/get-task.ps1 -Name "definitely not a task"` failed deterministically with `No task matched`.
  - `Test-Path -LiteralPath scripts/ai/get-prompt.ps1`, `Test-Path -LiteralPath ai/prompts`, and `Test-Path -LiteralPath ai/PROMPTS.md` all returned `False`.
  - Targeted tracked-file stale-reference searches, excluding `CHANGELOG.md`, `ai/archive/**`, this plan's historical notes, and the non-policy workflow-selection comparison, found no live references to the old task-loader path, old task-storage paths, old planning-guide path, old active-plan glob, misspelled planning-guide filename, legacy workflow-name strings, or prompt-owned library wording.
  - Manual documentation/process review using `ai/DOCUMENTATION.md` and `ai/REVIEWS.md` found no blocking owner contradiction, policy drift, or security-sensitive application behavior change.
  - `git diff --check` passed.
  - `./build.ps1 build` passed through the lightweight-file shortcut and skipped Gradle for the documentation/support-file change set.
- 2026-05-07 implementation Milestone 5 compaction and skill evaluation:
  - Loaded and applied the `Compact AI Docs` task from `ai/task-library/bodies/compact-ai-docs.md`.
  - Evaluated the optional `repo-ad-hoc-task-executor` skill and deferred creation because the new `ai/EXECUTION.md`, `ai/PLAN_EXECUTION.md`, and task library already cover the entry workflow without adding another policy surface.
  - Confirmed `Test-Path -LiteralPath ai/skills/repo-ad-hoc-task-executor` returned `False`.
  - Added an `## [Unreleased]` changelog entry for the AI guidance ownership and task-library restructure.
  - Fixed remaining stale task-library references in active standing guidance and the on-demand remix evaluation reference.
  - Targeted stale-reference searches found no live non-archived references to the old planning-guide path, old active-plan glob, misspelled planning-guide filename, old task-loader path, old task-storage path, prompt-owned library wording, or legacy workflow-name strings outside this plan's historical notes, archived plans, the non-policy workflow-selection comparison, and the stale post-compaction evaluation report scheduled for Milestone 6.
  - `git diff --check` passed.
- 2026-05-07 implementation Milestone 4 task-library migration:
  - Renamed `ai/PROMPTS.md` to `ai/TASK_LIBRARY.md`.
  - Moved `ai/prompts/` to `ai/task-library/` and rewrote `ai/task-library/index.json` to expose a `tasks` array with `ai/task-library/bodies/*.md` paths.
  - Renamed `scripts/ai/get-prompt.ps1` to `scripts/ai/get-task.ps1` and rewrote loader messages, list output, exact/slug matching, ambiguity errors, and unknown-name errors around tasks instead of prompts.
  - Updated `AGENTS.md`, `WORKING_WITH_AI.md`, `ai/DOCUMENTATION.md`, `ai/ENVIRONMENT_QUICK_REF.md`, `ai/REVIEWS.md`, `ai/TASK_LIBRARY.md`, task bodies, and the repo plan-author skill to use task-library ownership.
  - `pwsh ./scripts/ai/get-task.ps1 -List` passed.
  - `pwsh ./scripts/ai/get-task.ps1 -Name "Create Plan"` passed.
  - `pwsh ./scripts/ai/get-task.ps1 -Name "definitely not a task"` failed deterministically with `No task matched`.
  - `Test-Path -LiteralPath scripts/ai/get-prompt.ps1` returned `False`.
  - `Test-Path -LiteralPath ai/prompts` returned `False`.
  - `Test-Path -LiteralPath ai/PROMPTS.md` returned `False`.
  - Targeted stale-reference searches found no active non-archived docs or scripts still invoking the old loader path, old task-storage path, or old prompt-owned library wording; the plan's historical/current-state notes and stale evaluation report remain known exceptions until later milestones.
  - `git diff --check` passed, with Git warning that `ai/task-library/index.json` line endings will normalize on the next touch.
- 2026-05-07 implementation Milestone 3 owner split:
  - Added `ai/PLAN_EXECUTION.md` as the whole active-plan execution owner.
  - Regenerated `ai/EXECUTION.md` around ad hoc task and individual milestone execution, including task-promotion and context-switching rules.
  - Regenerated `ai/WORKFLOW.md` around branch, worktree, delegation, worker-log, integration, and remote-handoff mechanics only.
  - Updated `AGENTS.md`, `WORKING_WITH_AI.md`, `ai/DOCUMENTATION.md`, `ai/PLANNING.md`, `ai/templates/PLAN_TEMPLATE.md`, planning/workflow references, active plans, repo-local skill text, and reusable starter metadata/bodies for the new owner model.
  - Renamed the two detailed workflow references to `ai/references/WORKFLOW_DELEGATED_PLAN.md` and `ai/references/WORKFLOW_COORDINATED_PLANS.md`.
  - Renamed reusable starter titles that encoded retired workflow names to task-oriented names such as `Choose Execution Shape`, `Execute Plan Locally`, `Delegate Plan Slices`, and `Coordinate Multiple Plans`.
  - Targeted search found no legacy workflow-name strings in live non-archived guides, active plans, templates, task starters, or scripts. Historical archives, the non-policy workflow-selection comparison, and the stale evaluation report were excluded as planned.
  - `pwsh ./scripts/ai/get-prompt.ps1 -Name "Choose Execution Shape"` passed.
  - `pwsh ./scripts/ai/get-prompt.ps1 -Name "Delegate Plan Slices"` passed.
  - `git diff --check` passed, with Git warning that `ai/prompts/index.json` line endings will normalize on the next touch.
- 2026-05-07 implementation Milestone 2 path migration:
  - Renamed `ai/PLAN.md` to `ai/PLANNING.md`.
  - Moved active plans to `ai/plans/active/` and archived the three superseded workflow-selection candidate plans under `ai/archive/`.
  - Updated live references in standing docs, active plans, templates, references, skills, task starters, and roadmap rows to use `ai/plans/active/PLAN_*.md` and `ai/PLANNING.md`.
  - Targeted searches found no live non-archived reference to the old planning-guide path, old active-plan glob, old active plan paths, superseded workflow-selection plan paths, or the misspelled planning-guide filename. The non-policy workflow-selection variant comparison remains excluded by plan decision.
  - `git diff --check` passed.
- 2026-05-07 implementation Milestone 1 inventory:
  - Loaded the active execution plan, `AGENTS.md`, `ROADMAP.md`, `ai/DOCUMENTATION.md`, `ai/PLANNING.md`, `ai/EXECUTION.md`, `ai/WORKFLOW.md`, `ai/PROMPTS.md`, `ai/TESTING.md`, `ai/REVIEWS.md`, `ai/templates/PLAN_TEMPLATE.md`, `scripts/ai/get-prompt.ps1`, and the untracked workflow-selection variant comparison.
  - Completed the owner map for the target structure: `ai/PLANNING.md` owns plan creation and plan shape; `ai/PLAN_EXECUTION.md` owns whole active-plan execution; `ai/EXECUTION.md` owns ad hoc tasks and individual milestone execution; `ai/WORKFLOW.md` owns branch, worktree, delegation, worker-log, and integration coordination; `ai/TASK_LIBRARY.md` and `ai/task-library/` own reusable task starters; `ai/TESTING.md`, `ai/REVIEWS.md`, and `ai/DOCUMENTATION.md` keep validation, review, and artifact routing ownership.
  - Inventoried the pre-migration active plan files: the restructure plan and manual regression plan remain active, while the three workflow-selection candidate plans are superseded and should be archived during path migration.
  - Inventoried the current reusable starter set: 44 entries in `ai/prompts/index.json` with bodies under `ai/prompts/bodies/`; target migration is `ai/task-library/index.json`, `ai/task-library/bodies/*.md`, and `scripts/ai/get-task.ps1`.
  - Inventoried stale wording to remove from live standing guides, task starters, templates, skills, and active plans: the three named plan-mode labels, mode-fit section wording, prompt-owned library wording, old active-plan glob wording, and old prompt-loader path references.
  - Ran targeted reference searches for active-plan path assumptions, planning-guide references, prompt-loader references, and the stale mode vocabulary across live files, excluding archived history by default.
  - Ran `git diff --check`; passed before this validation entry was added.
- 2026-05-07 plan creation:
  - Loaded `AGENTS.md`, `ai/DOCUMENTATION.md`, `ai/PLANNING.md`, `ai/EXECUTION.md`, `ai/WORKFLOW.md`, `ai/PROMPTS.md`, `ai/TESTING.md`, `ai/REVIEWS.md`, `ROADMAP.md`, `ai/templates/PLAN_TEMPLATE.md`, `ai/prompts/bodies/compact-ai-docs.md`, `ai/prompts/bodies/evaluate-ai-guidelines.md`, `ai/prompts/bodies/create-plan.md`, `ai/prompts/bodies/review-plan-readiness.md`, the active workflow-selection option plans, and the current post-compaction evaluation report.
  - Read the repo-local `repo-plan-author` skill and applied it to this plan creation task.
  - Reviewed the untracked `ai/references/WORKFLOW_SELECTION_VARIANT_COMPARISON.md` as relevant context without editing it.
  - Updated `ROADMAP.md` Intake to point at this new plan and replace the older workflow-selection variant candidate rows.
  - Ran a targeted search for the retired mode-name vocabulary against this plan and `ROADMAP.md`; no matches before this validation note.
  - Ran `git diff --check`; passed.
  - Ran `./build.ps1 build`; passed through the lightweight-file shortcut and skipped Gradle. The wrapper reported changed files as this new plan, `ROADMAP.md`, and the pre-existing untracked `ai/references/WORKFLOW_SELECTION_VARIANT_COMPARISON.md`.
- 2026-05-07 plan refinement:
  - Loaded `AGENTS.md`, `ai/PLANNING.md`, `ai/DOCUMENTATION.md`, `ROADMAP.md`, `ai/PROMPTS.md`, `ai/EXECUTION.md`, `ai/WORKFLOW.md`, `ai/TESTING.md`, `ai/REVIEWS.md`, `ai/LEARNINGS.md`, the repo-local `repo-plan-author` skill, and the three active workflow-selection option plans.
  - Reviewed official current documentation for Claude Code, GitHub Copilot, and JetBrains Junie to evaluate task-library and planning-guide naming conventions.
  - Locked user decisions for no prompt-loader alias, archival of superseded workflow-selection plans, `ai/task-library/`, and `ai/PLANNING.md`; added defer-by-default acceptance criteria for the optional ad hoc task execution skill.
  - Updated `ROADMAP.md` Intake to reflect the refined ready plan and the no-alias task-library decision.
  - Ran a targeted stale-gap search for open-question fallback and retained compatibility-loader wording against this plan and `ROADMAP.md`; no matches.
  - Ran a targeted decision-reference search for the ready roadmap row, no-alias task-loader decision, `ai/task-library/`, defer-by-default skill wording, and archived workflow-selection plans; confirmed the refined decision references.
  - Ran `git diff --check`; passed.
  - Ran `./build.ps1 build`; passed through the lightweight-file shortcut and skipped Gradle. The wrapper reported changed files as this plan and `ROADMAP.md`.
- 2026-05-07 maturity hardening:
  - Moved the roadmap entry from Intake `Candidate` to `Planned Work` with status `Planned`.
  - Locked `ai/TASK_LIBRARY.md` as the replacement for `ai/PROMPTS.md`.
  - Replaced the execution-shape gap with `Execution Shape And Shared Files`, using coordinator-owned shared files and plain workflow-shape language instead of legacy execution-shape names.
  - Strengthened the no-alias validation with task-loader positive and negative checks, old-loader nonexistence checks, and stale-reference searches for old prompt-loader paths.
  - Ran a targeted search for exact legacy execution-shape names, retained compatibility-loader wording, the old roadmap candidate row, and unresolved task-library filename ambiguity against this plan and `ROADMAP.md`; no matches.
  - Ran a targeted decision-reference search for `Planned Work`, `ai/TASK_LIBRARY.md`, execution-shape ownership, task-loader unknown-name checks, and old-loader nonexistence checks; confirmed the hardened references.
  - Ran `git diff --check`; passed.
  - Ran `./build.ps1 build`; passed through the lightweight-file shortcut and skipped Gradle. The wrapper reported changed files as this plan, `ROADMAP.md`, and a pre-existing untracked lifecycle vocabulary reference.

## User Validation
- Review this plan's owner split and the locked names: `ai/PLANNING.md`, `ai/PLAN_EXECUTION.md`, `ai/EXECUTION.md`, `ai/WORKFLOW.md`, and `ai/task-library/`.
- Review the ad hoc skill defer-by-default acceptance criteria.
- Confirm implementation may wait until the active `v2.0.0-RC6` manual regression gate no longer owns the immediate next action.

## Required Content Checklist
- Behavior changing: AI guidance ownership, active plan locations, execution guidance structure, reusable task storage, and optional ad hoc execution skill.
- Roadmap tracking: `ROADMAP.md` / `Intake` / `AI guidance execution and reusable task library restructure`.
- Out of scope: application runtime, public API, setup behavior, deployment behavior, release cutting, and archived-plan history rewrites.
- Governing artifacts: `AGENTS.md`, `ai/DOCUMENTATION.md`, current planning/execution/workflow/prompt guides, task starters, templates, skills, roadmap, changelog, and evaluation report.
- Likely files: named in `Affected Artifacts`.
- Compatibility promises: public app contract unchanged; reusable starter loading remains deterministic; no legacy prompt-loader alias remains; historical archive wording remains historical.
- Risks: path migration breaks globs, task-library rename churn, removed prompt-loader muscle memory, duplicated skill policy, stale evaluation timing.
- Requirement gaps: resolved; ad hoc skill creation remains a conditional acceptance gate, not an open product decision.
- Execution shape: one coordinated documentation-maintenance run with coordinator-owned shared files, optional read-only review, or explicit non-overlapping delegation after the central owner map lands.
- Per-milestone context: named explicitly in each milestone.
- Specs and docs: AI guidance, human-facing AI workflow docs, task library artifacts, templates, skills, references, roadmap, changelog, and evaluation report.
- Validation: targeted searches, task loader positive and negative smoke checks, old-loader nonexistence check, `git diff --check`, `./build.ps1 build`, and manual owner-alignment review.
- User verification: inspect owner names, supersession of old variants, and roadmap priority.
