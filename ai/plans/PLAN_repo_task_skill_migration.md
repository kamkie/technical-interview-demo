# Plan: Repo Task Skill Migration

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Planning |
| Status | Draft |

## Summary
- Replace `ai/TASK_LIBRARY.md` with the low-context `repo-task` skill described by the inline `## Repo Task Skill Spec` section in this plan.
- Move the current reusable task starters into individual skill task files under `.agents/skills/repo-task/references/tasks/`, with `references/index.md` used only for slug discovery.
- Update live AI guidance so the skill becomes the single source of truth for reusable tasks and `ai/TASK_LIBRARY.md` is not preserved as a parallel catalog.
- Success means exact task requests load only `SKILL.md` plus one task file, ambiguous requests load the compact index plus one task file, maintenance requests load `references/spec.md`, and validation passes.

## Scope
- In scope:
  - promote the inline `## Repo Task Skill Spec` contract into `.agents/skills/repo-task/references/spec.md`
  - create `.agents/skills/repo-task/SKILL.md`, `.agents/skills/repo-task/agents/openai.yaml`, `.agents/skills/repo-task/references/index.md`, and `.agents/skills/repo-task/references/tasks/*.md`
  - migrate all current `ai/TASK_LIBRARY.md` task definitions into one file per slug using the spec schema
  - remove `ai/TASK_LIBRARY.md` after live references point at the skill and no task text remains there
  - update `AGENTS.md`, `ai/DOCUMENTATION.md`, planning/execution guidance, repo-local skills, templates, references, active plans, and `ROADMAP.md` where they still name `ai/TASK_LIBRARY.md`
  - validate the new skill, task schema, cross-references, and documentation-only build path
- Out of scope:
  - changing application runtime behavior, public REST behavior, OpenAPI, REST Docs, HTTP examples, database migrations, or release artifacts
  - creating a general task runner or build wrapper around the skill
  - preserving `ai/TASK_LIBRARY.md` as a compatibility alias or second task source of truth
  - introducing `.agents/plugins/marketplace.json` unless a later explicit plugin-distribution requirement appears

## Current State
- `ai/TASK_LIBRARY.md` is the current repository-local catalog and contains 44 task definitions grouped by category.
- The inline `## Repo Task Skill Spec` section defines the target `repo-task` skill layout, dispatcher behavior, index contract, task file schema, maintenance rules, validation command, and non-goals.
- `AGENTS.md`, `ai/DOCUMENTATION.md`, `ai/PLANNING.md`, and `ai/skills/repo-plan-author/SKILL.md` currently mention `ai/TASK_LIBRARY.md` as the reusable task catalog or task-title source.
- `ai/plans/PLAN_ai_guidance_execution_prompt_library_restructure.md` records the earlier implemented decision to keep reusable starters in `ai/TASK_LIBRARY.md`; this plan intentionally supersedes that storage decision for live guidance.
- No `.agents/` directory is currently present in the repository. Existing repo-local skills live under `ai/skills/`.
- The worktree was clean before this plan was created.

## Repo Task Skill Spec
This section inlines the current contents of `temp/spec.md`. During implementation, copy this contract into `.agents/skills/repo-task/references/spec.md` and then treat that skill-local file as the maintenance spec.

### Purpose
`repo-task` provides a low-context replacement for copied reusable prompts. It dispatches one saved repository workflow at a time from task reference files.

### Context Loading Contract
- Default skill load: `SKILL.md` only.
- Exact slug request: load `references/tasks/<slug>.md` only.
- Ambiguous request: load `references/index.md`, choose one slug, then load only that matching task file.
- Maintenance request: load this spec before changing the dispatcher, index, or task schema.
- Never load every task body for normal execution.

### File Layout
```text
.agents/skills/repo-task/
  SKILL.md
  agents/openai.yaml
  references/
    spec.md
    index.md
    tasks/
      <slug>.md
```

### Task File Schema
Each task file must use this shape:

```md
# Task Name

Category: Category Name
Slug: `task-slug`
Placeholders: <placeholder>, <other_placeholder>

Task instructions...
```

Required rules:

- The filename must be `<slug>.md`.
- The `Slug` value must match the filename stem.
- `Category`, `Slug`, and `Placeholders` must appear near the top of the file.
- Placeholder names must match the names users are expected to provide.
- Task instructions must be self-contained enough to execute after loading only that task file and the repo files it names.
- Shared standing policy belongs in `AGENTS.md` or another owning guide, not duplicated across task files.

### Index Contract
`references/index.md` is only for slug discovery. It should stay compact and list:

- category
- slug
- task name
- placeholders

Do not copy full task instructions into the index.

### Dispatcher Contract
`SKILL.md` must stay small and should contain only:

- trigger description in YAML frontmatter
- workflow for resolving a task
- invocation examples
- pointer to this spec for maintenance

Detailed task behavior belongs in task files. Skill maintenance rules belong in this spec.

### Adding Or Updating Tasks
1. Create or edit `references/tasks/<slug>.md`.
2. Keep the task schema intact.
3. Update `references/index.md`.
4. Validate that each task file has `Category`, `Slug`, and `Placeholders`.
5. Run the skill validator after changing `SKILL.md` or `agents/openai.yaml`.

### Validation
Run:

```powershell
python "<skill-creator>/scripts/quick_validate.py" .agents/skills/repo-task
git diff --check
```

If `PyYAML` is unavailable, install it into a temporary directory outside the repository and set `PYTHONPATH` only for the validation run.

### Non-Goals
- This skill is not a build system, release tool, or task runner.
- This skill should not hide broad project policy inside task files.
- This skill should not preserve a root prompt-library file as a parallel source of truth.

## Requirement Gaps And Open Questions
- No blocking requirement gap remains for planning.
- The target location `.agents/skills/repo-task/` conflicts with earlier live guidance that reserved `.agents/` for plugin marketplace configuration. The explicit user request and inline `## Repo Task Skill Spec` resolve this for the plan; execution must update the live guidance instead of preserving the old rule.
- The implementation should delete `temp/spec.md` after verifying this plan has inlined its content and after copying the inline spec to `references/spec.md`, unless the executor finds a separate temporary-file retention rule.

## Locked Decisions And Assumptions
- The skill name and folder are `repo-task` and `.agents/skills/repo-task/`.
- `references/spec.md` becomes the governing maintenance spec for the skill after migration.
- `ai/TASK_LIBRARY.md` is removed, not converted into a forwarding catalog.
- Every migrated task file keeps the current task name, category, slug, placeholders, and instructions unless a schema fix is required.
- Shared standing policy remains in `AGENTS.md` or the focused `ai/*.md` owner guide; task files stay procedural and self-contained for their specific starter.
- The index lists only category, slug, task name, and placeholders.
- The skill does not require `.agents/plugins/marketplace.json` for this migration.
- Public application behavior remains unchanged.

## Execution Shape And Shared Files
- Recommended shape: one local documentation-maintenance branch or working run.
- This is mostly mechanical but touches shared AI guidance and task ownership, so a single coordinator should own the final cross-reference updates and validation rollup.
- If delegation is explicitly requested later, a worker can own only the mechanical task-file split under `.agents/skills/repo-task/references/tasks/` after the coordinator locks `references/spec.md`; the coordinator should reserve `SKILL.md`, `references/index.md`, `AGENTS.md`, top-level `ai/*.md`, active plans, and `ROADMAP.md`.
- Do not use subagent forward-testing unless the user explicitly asks for delegated agent work.

## Affected Artifacts
- New skill artifacts:
  - `.agents/skills/repo-task/SKILL.md`
  - `.agents/skills/repo-task/agents/openai.yaml`
  - `.agents/skills/repo-task/references/spec.md`
  - `.agents/skills/repo-task/references/index.md`
  - `.agents/skills/repo-task/references/tasks/*.md`
- Removed or demoted artifacts:
  - `ai/TASK_LIBRARY.md`
  - `temp/spec.md`, after the inline plan copy is verified and the authoritative skill spec lands
- Live guidance and references to check:
  - `AGENTS.md`
  - `WORKING_WITH_AI.md`
  - `ai/DOCUMENTATION.md`
  - `ai/PLANNING.md`
  - `ai/PLAN_EXECUTION.md`
  - `ai/EXECUTION.md`
  - `ai/WORKFLOW.md`
  - `ai/TESTING.md`
  - `ai/REVIEWS.md`
  - `ai/ENVIRONMENT_QUICK_REF.md`
  - `ai/templates/**`
  - `ai/references/**`
  - `ai/skills/**/SKILL.md`
  - active `ai/plans/PLAN_*.md`
  - `ROADMAP.md`
- Tests and contract artifacts:
  - no application tests, REST Docs, OpenAPI baseline, HTTP examples, or README contract changes are expected
  - validation is documentation and skill validation plus the standard wrapper check

## Execution Milestones
### Milestone 1: Establish Skill Spec And Ownership
- goal: make the user-provided skill spec the repository-owned maintenance contract and update the AI-document ownership model
- owned files or packages: `.agents/skills/repo-task/references/spec.md`, `temp/spec.md`, `AGENTS.md`, `ai/DOCUMENTATION.md`, this plan if execution notes are needed
- shared files reserved to the coordinator: `ROADMAP.md`
- context required before execution: `AGENTS.md`, `ai/EXECUTION.md`, this plan, `ai/DOCUMENTATION.md`, and the `skill-creator` guidance
- behavior to preserve: spec-driven development, owner-guide separation, and no duplication of standing policy into task files
- exact deliverables:
  - create `.agents/skills/repo-task/references/spec.md` from this plan's inline `## Repo Task Skill Spec` section
  - update live ownership guidance so `.agents/skills/repo-task/` is the replacement task-skill location
  - remove or mark `temp/spec.md` as no longer authoritative, preferably by deleting it after the copy is verified
- validation checkpoint: compare the new spec to this plan's inline spec before deleting the temporary copy, then run `git diff --check`
- commit checkpoint: commit-ready doc/spec ownership change

### Milestone 2: Build The Skill Dispatcher
- goal: create the minimal `repo-task` dispatcher and UI metadata required by the spec
- owned files or packages: `.agents/skills/repo-task/SKILL.md`, `.agents/skills/repo-task/agents/openai.yaml`
- shared files reserved to the coordinator: `.agents/skills/repo-task/references/index.md`
- context required before execution: `AGENTS.md`, `ai/EXECUTION.md`, this plan, `.agents/skills/repo-task/references/spec.md`, and the `skill-creator` guidance
- behavior to preserve: default skill load remains `SKILL.md` only; detailed task behavior stays outside `SKILL.md`
- exact deliverables:
  - create a concise `SKILL.md` with YAML frontmatter, task-resolution workflow, examples, and a maintenance pointer to `references/spec.md`
  - create or regenerate `agents/openai.yaml` so it matches the dispatcher
  - avoid copying full task instructions into the dispatcher
- validation checkpoint: run `python "<skill-creator>/scripts/quick_validate.py" .agents/skills/repo-task`
- commit checkpoint: commit-ready skill dispatcher shell

### Milestone 3: Migrate Task Bodies And Index
- goal: convert the 44 task definitions from the monolithic task library into schema-valid task files and a compact index
- owned files or packages: `.agents/skills/repo-task/references/tasks/*.md`, `.agents/skills/repo-task/references/index.md`, `ai/TASK_LIBRARY.md`
- shared files reserved to the coordinator: `.agents/skills/repo-task/SKILL.md`
- context required before execution: `AGENTS.md`, `ai/EXECUTION.md`, this plan, `.agents/skills/repo-task/references/spec.md`, and `ai/TASK_LIBRARY.md`
- behavior to preserve: task names, categories, slugs, placeholders, and task instructions remain equivalent to the current catalog
- exact deliverables:
  - create one task file per `Slug` with filename `<slug>.md`
  - ensure each task file starts with task name, `Category`, `Slug`, and `Placeholders`
  - build `references/index.md` with category, slug, task name, and placeholders only
  - delete `ai/TASK_LIBRARY.md` after task migration and reference updates are ready
  - use a one-off mechanical splitter only if it reduces transcription risk; do not keep a migration script unless it becomes intentional support tooling
- validation checkpoint:
  - check every task filename stem matches its `Slug`
  - check every task file has `Category`, `Slug`, and `Placeholders` near the top
  - check the index task count matches the task-file count
- commit checkpoint: commit-ready task migration

### Milestone 4: Update Live References
- goal: remove stale task-library references and make the new task skill discoverable from live guidance
- owned files or packages: `AGENTS.md`, `WORKING_WITH_AI.md`, top-level `ai/*.md`, `ai/templates/**`, `ai/references/**`, `ai/skills/**/SKILL.md`, active `ai/plans/PLAN_*.md`, `ROADMAP.md`
- shared files reserved to the coordinator: `AGENTS.md`, `ai/DOCUMENTATION.md`, `ROADMAP.md`
- context required before execution: `AGENTS.md`, `ai/EXECUTION.md`, this plan, `ai/DOCUMENTATION.md`, `ai/PLANNING.md`, `.agents/skills/repo-task/references/spec.md`, and the specific files found by targeted searches
- behavior to preserve: owner guides remain the source for durable policy; the skill is only the task-dispatch wrapper and task-body store
- exact deliverables:
  - replace live references to `ai/TASK_LIBRARY.md` with the `repo-task` skill path and loading contract where appropriate
  - update the prior active AI-guidance plan only enough to mark the old task-library storage decision as superseded by this plan
  - keep archived historical references unchanged unless they are still presented as live instructions
  - keep `ROADMAP.md` pointed at this plan with accurate lifecycle status
- validation checkpoint:
  - run targeted `rg` searches for `TASK_LIBRARY`, `Task Library`, `task-library`, `repo-task`, `.agents/skills`, and stale loader paths
  - inspect each live match and update or justify it
- commit checkpoint: commit-ready guidance alignment

### Milestone 5: Validate And Handoff
- goal: prove the skill, schema, references, and docs-only repository validation are aligned
- owned files or packages: validation notes in this plan
- shared files reserved to the coordinator: none beyond this plan
- context required before execution: `AGENTS.md`, `ai/EXECUTION.md`, this plan, `ai/TESTING.md`, `.agents/skills/repo-task/references/spec.md`
- behavior to preserve: no application behavior or public contract changes
- exact deliverables:
  - run skill validation
  - run schema/index checks for task files
  - run stale-reference searches
  - run `git diff --check`
  - run `./build.ps1 build` and record whether it follows the lightweight-only path
  - perform a manual progressive-disclosure review for one exact slug request and one ambiguous request by opening only the files allowed by the spec
- validation checkpoint: all required commands pass or failures are recorded with the first real blocker
- commit checkpoint: final commit-ready migration

## Edge Cases And Failure Modes
- A task file with a mismatched filename and `Slug` would break exact-slug dispatch.
- A verbose index would recreate the context-loading problem the skill is meant to solve.
- Leaving `ai/TASK_LIBRARY.md` in place as a catalog would create two task sources of truth.
- Updating `.agents/skills` guidance without removing older `.agents/plugins` restrictions would leave contradictory instructions.
- Mechanical splitting can silently drop task text, especially for task bodies with nested headings such as `Compact AI Docs` and `Evaluate AI Guidelines`.
- `agents/openai.yaml` can drift from `SKILL.md` if generated before the dispatcher wording stabilizes.
- Archived plans may contain historical references to `ai/TASK_LIBRARY.md`; these are acceptable only when clearly historical and not active instructions.

## Validation Plan
- Skill validation:
  - `python "<skill-creator>/scripts/quick_validate.py" .agents/skills/repo-task`
  - if `PyYAML` is missing, install it into a temporary directory outside the repository and set `PYTHONPATH` only for that validation run
- Schema and index checks:
  - verify each `.agents/skills/repo-task/references/tasks/*.md` file has `Category`, `Slug`, and `Placeholders`
  - verify each `Slug` matches the filename stem
  - verify `references/index.md` lists every task exactly once and does not copy full instructions
- Cross-reference checks:
  - run targeted `rg` searches for `TASK_LIBRARY`, `Task Library`, `task-library`, `repo-task`, `.agents/skills`, `.agents/plugins/marketplace.json`, and old task-loader wording
- Repository checks:
  - `git diff --check`
  - `./build.ps1 build`
- Manual checks:
  - exact slug path: confirm the dispatcher points directly to `references/tasks/<slug>.md`
  - ambiguous path: confirm the dispatcher uses `references/index.md` only to choose one task file
  - maintenance path: confirm the dispatcher points maintainers to `references/spec.md`

## Testing Strategy
- Unit tests: not applicable; this is AI-guidance and skill packaging work.
- Integration tests: not applicable unless the standard wrapper build detects generated-doc or formatting issues.
- Contract tests: not applicable because no REST Docs, OpenAPI, HTTP examples, or public API behavior changes are planned.
- Smoke or benchmark tests: not applicable.
- Documentation and skill checks: required, using quick validation, schema/index checks, stale-reference searches, `git diff --check`, and the standard wrapper build.

## Better Engineering Notes
- Prefer a deterministic mechanical split of `ai/TASK_LIBRARY.md` over hand-copying 44 task bodies, but do not retain one-off migration tooling unless it is intentionally useful after the migration.
- Keep task bodies self-contained for their starter while routing shared rules back to owner guides.
- Keep `SKILL.md` small enough that normal triggering does not erase the context savings from removing the monolithic task library.
- Record any intentionally retained historical `TASK_LIBRARY` references during validation so future agents do not treat them as missed live guidance.

## Validation Results
- To be filled in during execution.

## User Validation
- Review `.agents/skills/repo-task/SKILL.md` and `.agents/skills/repo-task/references/spec.md` for the intended loading contract.
- Spot-check a migrated exact-slug task file and the corresponding `references/index.md` row.
- In a fresh agent session, request one exact task slug and one ambiguous task name, then confirm the agent does not load every task body.

## Required Content Checklist
- Behavior changing: reusable AI task starters move from `ai/TASK_LIBRARY.md` into the `repo-task` skill.
- Roadmap tracking: `ROADMAP.md` Planned Work points to `ai/plans/PLAN_repo_task_skill_migration.md`.
- Out of scope: application behavior, public API contracts, release work, and general task-runner tooling.
- Governing specs: current user request, this plan's inline `## Repo Task Skill Spec` section until migrated, then `.agents/skills/repo-task/references/spec.md`; live AI owner guides define policy boundaries.
- Likely files to change: listed in `## Affected Artifacts`.
- Compatibility promise: no `ai/TASK_LIBRARY.md` compatibility catalog remains; no application contract changes occur.
- Risks: schema drift, duplicate sources of truth, stale live references, oversized index, and dropped task text during migration.
- Requirement gaps: none blocking; `.agents/skills` ownership conflict is resolved by the explicit spec and must be reflected in live guidance.
- Execution shape: one coordinated documentation-maintenance run, with optional mechanical split delegation only if explicitly requested.
- Coordinator-owned files: `SKILL.md`, `references/index.md`, `AGENTS.md`, top-level `ai/*.md`, active plans, and `ROADMAP.md`.
- Context per milestone: listed explicitly in each milestone.
- Artifact routing: AI guidance and repo-local skill artifacts move; application specs and contracts do not.
- Validation: quick skill validation, schema/index checks, stale-reference searches, `git diff --check`, and `./build.ps1 build`.
- User verification: review the skill contract and spot-check exact, ambiguous, and maintenance loading flows.
