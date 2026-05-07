# Plan: Split Workflow Guidance Into On-Demand Mode References

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Implementation |
| Status | In Progress |

## Summary
- Shrink `ai/WORKFLOW.md` into a concise workflow router that keeps default execution rules, mode selection, and common delegation invariants in the standing read set.
- Move detailed fanout mechanics into on-demand reference files and rename execution modes so the names all describe plan topology.
- Add an explicit mode-selection gate that records the chosen mode in each plan before non-default workflow mechanics are loaded.
- Roadmap tracking: `ROADMAP.md` tracks this under `Ordered Plan` / `Moving to 2.0` / `AI Workflow Guidance` as selected AI-guidance compaction work.
- Success means routine single-agent work can read only the compact workflow router, fanout work can load the relevant reference file on demand, and active docs/prompts use one consistent mode vocabulary.

## Scope
### In scope
- Adopt the tiered split proposed in the scratch note:
  - `ai/WORKFLOW.md` remains the owner and compact router.
  - `ai/references/WORKFLOW_SINGLE_PLAN_FANOUT.md` owns the detailed mechanics for fanout inside one plan.
  - `ai/references/WORKFLOW_MULTI_PLAN_FANOUT.md` owns the detailed mechanics for fanout across multiple plans.
- Rename the supported modes by plan topology:
  - current `Single Branch` -> `Linear Plan`
  - current `Shared Plan` -> `Single-Plan Fanout`
  - current `Parallel Plans` -> `Multi-Plan Fanout`
- Add a `Mode Selection Gate` to `ai/WORKFLOW.md` with clear triggers, user-approval requirements, and a rule to record the chosen mode in `Execution Mode Fit`.
- Update cross-references and prompt language so active AI docs, active plans, reusable prompt titles, prompt bodies, and prompt index metadata use the new mode vocabulary.
- Update `compact-ai-docs` guidance so it checks stale references into moved workflow reference files without bulk-loading `ai/references/`.
- Keep this plan and `ROADMAP.md` aligned while the work is active.

### Out of scope
- Changing application source code, API behavior, REST Docs, OpenAPI, HTTP examples, README contract wording, setup instructions, build scripts, or release versioning.
- Creating top-level workflow sibling files such as `ai/WORKFLOW_SHARED.md` or `ai/WORKFLOW_PARALLEL.md`.
- Creating a separate delegation-quality reference file unless later implementation finds a hard readability blocker; the current plan keeps common delegation rules in `ai/WORKFLOW.md`.
- Updating `ai/archive/` historical plans just to rewrite old mode names.
- Replacing the repo's existing prompt loader or adding a new skill.

## Current State
- `AGENTS.md` declares `ai/WORKFLOW.md` on demand, but the current workflow file combines default `Single Branch` rules, `Shared Plan` fanout mechanics, `Parallel Plans` orchestration, worktree rules, worker-log schema, coordinator gates, and reporting rules in one standing file.
- `ai/WORKFLOW.md` says to choose the mode before execution starts, but it does not define an explicit gate for when an agent may leave the default mode or how that decision is recorded.
- The current mode names describe mixed axes: branch shape, planning artifact shape, and scheduling pattern.
- No workflow mode reference files currently exist under `ai/references/`.
- `ai/PLAN.md`, `ai/EXECUTION.md`, `ai/templates/PLAN_TEMPLATE.md`, active plans, prompt titles, prompt bodies, and `ai/RELEASES.md` still use the current mode names.
- `ai/prompts/bodies/compact-ai-docs.md` already checks stale references, but its scope language emphasizes top-level standing files first and does not explicitly call out stale anchors after workflow mechanics move into `ai/references/`.

## Requirement Gaps And Open Questions
- No material user input blocks planning. The scratch note already selects Shape A as the recommended split and proposes the topology-based mode names.
- The user did not specify whether old mode names should remain as permanent aliases.
  Fallback: use old names only as migration context while this plan is executed; final active docs and prompts should use the new names. Archived plans may continue to contain old names.
- The exact final line count of `ai/WORKFLOW.md` is not a hard requirement.
  Fallback: keep the router concise and on-demand, but preserve clarity over hitting the scratch note's approximate 60-80 line target.

## Locked Decisions And Assumptions
- Implement Shape A from the scratch note, not the inline-anchor-only or sibling-top-level-file alternatives.
- Use `Linear Plan`, `Single-Plan Fanout`, and `Multi-Plan Fanout` as the final active mode names.
- Create new reference files with the final names:
  - `ai/references/WORKFLOW_SINGLE_PLAN_FANOUT.md`
  - `ai/references/WORKFLOW_MULTI_PLAN_FANOUT.md`
- Do not create `ai/references/WORKFLOW_SHARED_PLAN.md` or `ai/references/WORKFLOW_PARALLEL_PLANS.md` as transitional files.
- Keep common delegation quality rules, coordinator completion rules, and the shared worker-log schema in `ai/WORKFLOW.md` unless implementation proves they must move together into one shared on-demand reference. Do not duplicate the worker-log schema between fanout references.
- Update active docs, active plan files, prompt titles, prompt bodies, and prompt index metadata in the same implementation so the new names do not coexist with old names in the active read set.
- Exclude `ai/archive/` from terminology rewrites unless a non-archived reference points into an archived plan and must be fixed.

## Execution Mode Fit
- Selected execution mode: `Linear Plan`.
- This is a documentation-only AI-guidance change with many shared files and cross-references; worker fanout would add coordination cost and raise the risk of inconsistent terminology.
- Coordinator-owned files:
  - `ai/PLAN_workflow_on_demand_split.md`
  - `ROADMAP.md`
  - `AGENTS.md`
  - `ai/WORKFLOW.md`
  - `ai/PLAN.md`
  - `ai/EXECUTION.md`
  - `ai/RELEASES.md`
  - `ai/templates/PLAN_TEMPLATE.md`
  - `ai/references/PLAN_DETAILED_GUIDE.md`
  - `ai/prompts/index.json`
  - `ai/PROMPTS.md`
  - affected prompt bodies under `ai/prompts/bodies/`
- Candidate worker boundaries: none recommended. If delegation is later forced, keep `ai/WORKFLOW.md`, `AGENTS.md`, `ROADMAP.md`, active plans, and prompt index metadata coordinator-owned.

## Affected Artifacts
- Planning and roadmap:
  - `ai/PLAN_workflow_on_demand_split.md`
  - `ROADMAP.md`
- Workflow guide and new references:
  - `ai/WORKFLOW.md`
  - `ai/references/WORKFLOW_SINGLE_PLAN_FANOUT.md`
  - `ai/references/WORKFLOW_MULTI_PLAN_FANOUT.md`
- AI document inventory and planning/execution guidance:
  - `AGENTS.md`
  - `ai/PLAN.md`
  - `ai/references/PLAN_DETAILED_GUIDE.md`
  - `ai/templates/PLAN_TEMPLATE.md`
  - `ai/EXECUTION.md`
  - `ai/RELEASES.md`
- Prompt inventory and prompt bodies:
  - `ai/PROMPTS.md`
  - `ai/prompts/index.json`
  - `ai/prompts/bodies/choose-execution-mode.md`
  - `ai/prompts/bodies/run-plan-with-inferred-mode.md`
  - `ai/prompts/bodies/run-linear-plan.md`
  - `ai/prompts/bodies/run-plan-as-single-plan-fanout.md`
  - `ai/prompts/bodies/run-plans-as-multi-plan-fanout.md`
  - `ai/prompts/bodies/run-all-ready-plans.md`
  - `ai/prompts/bodies/run-all-unfinished-plans.md`
  - `ai/prompts/bodies/integrate-single-plan-fanout-output.md`
  - `ai/prompts/bodies/integrate-multi-plan-fanout-output.md`
  - `ai/prompts/bodies/check-active-workers.md`
  - `ai/prompts/bodies/check-worker-status.md`
  - `ai/prompts/bodies/compact-ai-docs.md`
- Active plans:
  - `ai/PLAN_manual_regression_execution.md`
  - `ai/PLAN_pskoett_ai_skill_guidance_adoption.md`
  - this plan file
- Application specs and contracts:
  - no tests, REST Docs, OpenAPI, HTTP examples, README, or source files should change.

## Execution Milestones
### Milestone 1: Plan And Roadmap
- goal: turn the scratch proposal into a repo-native execution plan and track it as active work.
- owned files or packages:
  - `ai/PLAN_workflow_on_demand_split.md`
  - `ROADMAP.md`
- shared files that a fanout worker must leave to the coordinator:
  - not applicable in current `Linear Plan` execution
- behavior to preserve:
  - no application behavior or public contract changes
  - no implementation of the workflow split yet
- exact deliverables:
  - ready plan with scope, locked decisions, affected artifacts, milestones, validation, and user validation
  - roadmap entry pointing to this plan and current lifecycle
- validation checkpoint:
  - `git diff --check`
  - `./build.ps1 build` lightweight-file shortcut or full build if the wrapper requires it
- commit checkpoint:
  - `docs: plan workflow guidance on-demand split`

### Milestone 2: Workflow Router And References
- goal: split `ai/WORKFLOW.md` into a compact router plus mode-specific on-demand references.
- owned files or packages:
  - `ai/WORKFLOW.md`
  - `ai/references/WORKFLOW_SINGLE_PLAN_FANOUT.md`
  - `ai/references/WORKFLOW_MULTI_PLAN_FANOUT.md`
- shared files that a fanout worker must leave to the coordinator:
  - this plan and `ROADMAP.md`
- behavior to preserve:
  - `ai/WORKFLOW.md` remains the owner for execution-mode selection, branch/worktree topology, and coordinator or worker integration rules
  - default execution remains the smallest mode with the lowest coordination cost
  - non-default fanout still requires explicit ownership boundaries, validation scope, and integration order
- exact deliverables:
  - `ai/WORKFLOW.md` describes `Linear Plan`, `Single-Plan Fanout`, and `Multi-Plan Fanout`
  - `ai/WORKFLOW.md` contains a `Mode Selection Gate` with these rules:
    - default to `Linear Plan`
    - user-requested fanout, parallelism, sub-agents, or worktree splitting is a candidate for `Single-Plan Fanout` or `Multi-Plan Fanout` and still requires user approval
    - one active plan with at least two disjoint worker-safe slices is a candidate for `Single-Plan Fanout`
    - two or more approved disjoint plan files are a candidate for `Multi-Plan Fanout`
    - shared controller, service, test, REST Docs, OpenAPI, README, or tightly coupled milestone edits stay in `Linear Plan`
    - non-default modes require recording the selected mode in the plan's `Execution Mode Fit` section and loading the matching reference file before proceeding
  - detailed single-plan fanout topology, worker rules, coordinator rules, worktree rules, and reporting extras move to `WORKFLOW_SINGLE_PLAN_FANOUT.md`
  - detailed multi-plan fanout topology, private changelog rules, worker rules, coordinator rules, worktree rules, and reporting extras move to `WORKFLOW_MULTI_PLAN_FANOUT.md`
  - the common worker-log schema is present once, not duplicated between the two references
- validation checkpoint:
  - targeted `rg` search confirms old reference filenames were not introduced
  - manual consistency check confirms the router points to the correct references and references do not restate each other unnecessarily
- commit checkpoint:
  - `docs: split workflow fanout guidance into references`

### Milestone 3: Cross-Reference And Prompt Migration
- goal: update the rest of the active AI guidance set to use the new mode names and on-demand reference paths.
- owned files or packages:
  - `AGENTS.md`
  - `ai/PLAN.md`
  - `ai/references/PLAN_DETAILED_GUIDE.md`
  - `ai/templates/PLAN_TEMPLATE.md`
  - `ai/EXECUTION.md`
  - `ai/RELEASES.md`
  - `ai/PROMPTS.md`
  - `ai/prompts/index.json`
  - affected prompt bodies under `ai/prompts/bodies/`
  - active `ai/PLAN_*.md` files
- shared files that a fanout worker must leave to the coordinator:
  - all owned files in this milestone should stay coordinator-owned because terminology drift is the main risk
- behavior to preserve:
  - prompt loading still uses `scripts/ai/get-prompt.ps1`
  - `ai/PROMPTS.md` remains a lean index, not a policy owner
  - active plans keep their existing lifecycle and validation history except for vocabulary and guidance alignment
- exact deliverables:
  - `AGENTS.md` describes `ai/WORKFLOW.md` as the compact workflow router and lists the new on-demand workflow references where appropriate
  - planning and execution guides list `Linear Plan`, `Single-Plan Fanout`, and `Multi-Plan Fanout`
  - prompt titles and slugs are renamed consistently:
    - `Run Plan On Single Branch` -> `Run Linear Plan`
    - `Run Plan As Shared Plan` -> `Run Plan As Single-Plan Fanout`
    - `Run Plans In Parallel` -> `Run Plans As Multi-Plan Fanout`
    - `Integrate Shared Plan Output` -> `Integrate Single-Plan Fanout Output`
    - `Integrate Parallel Plan Output` -> `Integrate Multi-Plan Fanout Output`
  - related prompt bodies that infer, run, check, or integrate workflow modes use the new names
  - `compact-ai-docs` guidance explicitly checks stale workflow references and anchors after mode mechanics move into `ai/references/`
  - active non-archived plan files use the new names where they describe execution mode fit or fanout-worker shared files
- validation checkpoint:
  - prompt loader smoke checks for the renamed prompt titles
  - targeted `rg` search across active docs confirms old mode names remain only in this plan's historical mapping, archived plans, or clearly intentional migration notes
- commit checkpoint:
  - `docs: migrate workflow mode terminology`

### Milestone 4: Final Consistency Review And Validation
- goal: prove the split is internally consistent and remains lightweight documentation work.
- owned files or packages:
  - all files touched by Milestones 2 and 3
  - this plan's `Validation Results`
- shared files that a fanout worker must leave to the coordinator:
  - not applicable in current `Linear Plan` execution
- behavior to preserve:
  - no application build, runtime, API, or release behavior changes
  - no permanent duplicate mode guidance in standing docs
- exact deliverables:
  - update this plan's `Validation Results`
  - confirm `ROADMAP.md` lifecycle text still matches this plan
  - final summary of moved guidance and any intentionally remaining old-name references
- validation checkpoint:
  - `git diff --check`
  - `./build.ps1 build`
  - prompt loader smoke checks for renamed prompt titles
  - targeted `rg` consistency checks excluding `ai/archive/`
- commit checkpoint:
  - `docs: validate workflow guidance split`

## Edge Cases And Failure Modes
- Splitting too aggressively can hide common delegation rules. Keep the router complete enough to decide the mode before loading a reference.
- Leaving `Shared Plan` and `Parallel Plans` wording in active prompt titles can make prompt invocation disagree with `ai/WORKFLOW.md`.
- Removing old names from active docs while archived plans still contain them can create noisy search results. Exclude `ai/archive/` from terminology validation unless checking historical drift intentionally.
- Duplicating the worker-log schema in both fanout references can create future drift. Keep it in one place and reference it from mode-specific files.
- Updating active plan files can accidentally alter lifecycle or validation history. Restrict active-plan edits to terminology alignment unless the plan itself is being revised.
- The compact router can become too terse if it only points elsewhere. Preserve the mode-selection gate, default behavior, and common completion rules in `ai/WORKFLOW.md`.

## Validation Plan
- Run `git diff --check` after plan creation and after implementation.
- Run `./build.ps1 build`; for documentation-only and AI-guidance-only changes, the lightweight-file shortcut is acceptable unless the wrapper requires a full build.
- Run targeted searches, excluding `ai/archive/`, for old mode names and old reference names:

```powershell
rg -n "Single Branch|Shared Plan|Parallel Plans|WORKFLOW_SHARED_PLAN|WORKFLOW_PARALLEL_PLANS" AGENTS.md ROADMAP.md ai scripts -g "*.md" -g "*.json" -g "*.ps1" -g "!ai/archive/**"
```

- Smoke-check renamed prompt titles with `scripts/ai/get-prompt.ps1`, including:
  - `Run Linear Plan`
  - `Run Plan As Single-Plan Fanout`
  - `Run Plans As Multi-Plan Fanout`
  - `Integrate Single-Plan Fanout Output`
  - `Integrate Multi-Plan Fanout Output`
- Manually review `AGENTS.md`, `ai/WORKFLOW.md`, `ai/PLAN.md`, `ai/EXECUTION.md`, `ai/PROMPTS.md`, prompt index metadata, and the two new workflow references for owner drift and duplicate policy.

## Testing Strategy
- Unit tests: not applicable; no executable application logic changes.
- Integration tests: not applicable; no database, service, HTTP, OAuth, or runtime behavior changes.
- Contract tests: not applicable; no REST Docs, OpenAPI, HTTP examples, README public contract, or public API behavior changes.
- Smoke or benchmark tests: not applicable; no runtime or performance behavior changes.
- Prompt smoke checks: applicable for renamed prompt titles and prompt-index consistency.
- Manual documentation checks: required for cross-reference consistency, on-demand reference routing, mode-name consistency, and preserving `ai/WORKFLOW.md` as the owner.

## Better Engineering Notes
- The target is lower standing-context cost without losing execution safeguards. Do not sacrifice ownership clarity just to hit a line-count target.
- Keep the mode names focused on plan topology. Avoid mixing branch shape, artifact shape, and scheduling terms again.
- If fanout mechanics grow further after this split, prefer compacting within the mode-specific references before creating another reference file.
- This plan should not force updates to archived historical plans; archived content is useful as history, not standing guidance.

## Validation Results
- 2026-05-07 plan creation:
  - `git diff --check` passed.
  - `./build.ps1 build` passed through the lightweight-file shortcut, reporting that only `ai/PLAN_workflow_on_demand_split.md` and `ROADMAP.md` changed and that the Gradle build was skipped.
  - Manual plan-shape review confirmed all required sections from `ai/PLAN.md` are present.
- 2026-05-07 Milestone 2 workflow split:
  - Targeted `rg` search over `ai/WORKFLOW.md`, `ai/references/WORKFLOW_SINGLE_PLAN_FANOUT.md`, and `ai/references/WORKFLOW_MULTI_PLAN_FANOUT.md` found no old mode names or old workflow reference filenames.
  - Targeted `rg` search confirmed `ai/WORKFLOW.md` points to the two new fanout reference files and contains the `Mode Selection Gate`.
  - `git diff --check` passed.
  - Manual consistency review confirmed the common worker-log schema is present once in `ai/WORKFLOW.md`, with mode-specific mechanics in the two on-demand references.
- 2026-05-07 Milestone 3 terminology migration:
  - Prompt loader smoke checks passed for `Run Linear Plan`, `Run Plan As Single-Plan Fanout`, `Run Plans As Multi-Plan Fanout`, `Integrate Single-Plan Fanout Output`, and `Integrate Multi-Plan Fanout Output`.
  - Targeted `rg` search across active docs and scripts, excluding this plan's migration notes and `ai/archive/`, found no old mode names, old prompt titles, old prompt body slugs, or old workflow reference filenames.
  - Manual consistency review confirmed `AGENTS.md`, `README.md`, `WORKING_WITH_AI.md`, `ai/PLAN.md`, `ai/EXECUTION.md`, `ai/PROMPTS.md`, `ai/prompts/index.json`, prompt bodies, active plans, and the repo-local planning skill use the new mode vocabulary.

## User Validation
- Review `ai/WORKFLOW.md` after implementation and confirm the first screen is enough to choose a mode without reading the fanout references.
- Invoke the renamed prompts through `scripts/ai/get-prompt.ps1` and confirm the prompt bodies use the same mode names as `ai/WORKFLOW.md`.
- For a normal one-agent task, confirm no `ai/references/WORKFLOW_*_FANOUT.md` file is needed unless the task actually enters fanout.
