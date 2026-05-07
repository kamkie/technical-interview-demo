# Task Library For This Repository

`ai/TASK_LIBRARY.md` is the lean command index for reusable task starters.
The machine-readable task index lives in `ai/task-library/index.json`, and raw task bodies live under `ai/task-library/bodies/`.
Use `pwsh ./scripts/ai/get-task.ps1 -Name "<task title>"` to load one task body.

This file does not own standing policy.
Use the owner guides instead:

- planning: `ai/PLANNING.md`
- whole-plan execution: `ai/PLAN_EXECUTION.md`
- ad hoc task or one-milestone execution: `ai/EXECUTION.md`
- workflow delegation and integration: `ai/WORKFLOW.md`
- validation: `ai/TESTING.md`
- artifact routing: `ai/DOCUMENTATION.md`
- review: `ai/REVIEWS.md`
- release: `ai/RELEASES.md`
- local command wrapper usage: `ai/ENVIRONMENT_QUICK_REF.md`

## Title Shorthand Rules

- the task names listed below are reusable commands
- use the exact title or an unmistakably close reference
- include required placeholders such as `<topic>`, `<plan_file>`, `<milestone_name>`, `<task>`, or `<constraint>`
- if the title or required context is ambiguous, ask a targeted clarification question
- when a task name is invoked, load only the matching raw task body with `scripts/ai/get-task.ps1`

## Task Context Selection

Task titles do not own read sets.
Use the lifecycle owner map in `AGENTS.md` to choose the minimum guide set, then load only the matching raw task body after a title is invoked.

Add task-specific files only when the task library itself is being used or changed:

- `ai/task-library/index.json` and `ai/task-library/bodies/` for task metadata or body maintenance
- `scripts/ai/get-task.ps1` for task-loader behavior
- `ai/templates/` or `ai/references/` only when the selected task or owner guide requires that on-demand material

## Task List

- Discovery and roadmap
  - `Clarify Roadmap Decisions`
  - `Refine Roadmap Intake`
  - `Pick Next Roadmap Workstream`
  - `Prioritize Open Security And Quality Issues In Roadmap`
  - `Review Roadmap Item`
- Planning
  - `Create Plan`
  - `Plan From Roadmap`
  - `Plan Checked Roadmap Items`
  - `Split Checked Roadmap Items Into Plans`
  - `Plan Dependency And Build Tool Upgrade`
  - `Plan Repository-Wide Dependency And Toolchain Upgrade Sweep`
  - `Revise Plan`
- Plan verification
  - `Review Plan Readiness`
  - `Choose Execution Shape`
- Implementation
  - `Implement Plan`
  - `Implement Milestone`
- Workflow execution
  - `Run Plan With Chosen Shape`
  - `Execute Plan Locally`
  - `Delegate Plan Slices`
  - `Coordinate Multiple Plans`
  - `Run All Ready Plans`
  - `Run All Unfinished Plans`
  - `Check Worker Status`
  - `Check Active Workers`
- Implementation integration
  - `Integrate Delegated Plan Work`
  - `Integrate Multiple Plan Workstreams`
  - `Integrate All Open PRs`
- Implementation verification
  - `Run Required Validation`
  - `Check Contract Impact`
  - `Verify Milestone`
  - `Review Diff Risks`
- Preparing release
  - `Check Release Readiness`
  - `Prepare Release`
- Releasing
  - `Push Prepared Release`
  - `Release All Merged Work`
  - `Check Published Release`
- Lifecycle and maintenance
  - `Implement Then Release`
  - `Clean Worktrees And Stale Local Branches`
  - `Evaluate AI Guidelines`
  - `Summarize Lifecycle State`
  - `Triage Validation Failure`
  - `Upgrade Dependencies And Build Tools`
  - `Compact AI Docs`
  - `Context Report`
