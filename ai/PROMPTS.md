# Prompt Index For This Repository

`ai/PROMPTS.md` is the lean command index for reusable prompt starters.
The machine-readable prompt index lives in `ai/prompts/index.json`, and raw prompt bodies live under `ai/prompts/bodies/`.
Use `pwsh ./scripts/ai/get-prompt.ps1 -Name "<prompt title>"` to load one prompt body.

This file does not own standing policy.
Use the owner guides instead:

- planning: `ai/PLAN.md`
- implementation: `ai/EXECUTION.md`
- workflow modes and delegation: `ai/WORKFLOW.md`
- validation: `ai/TESTING.md`
- artifact routing: `ai/DOCUMENTATION.md`
- review: `ai/REVIEWS.md`
- release: `ai/RELEASES.md`
- local command wrapper usage: `ai/ENVIRONMENT_QUICK_REF.md`

## Title Shorthand Rules

- the prompt names listed below are reusable commands
- use the exact title or an unmistakably close reference
- include required placeholders such as `<topic>`, `<plan_file>`, `<milestone_name>`, `<task>`, or `<constraint>`
- if the title or required context is ambiguous, ask a targeted clarification question
- when a prompt name is invoked, load only the matching raw prompt body with `scripts/ai/get-prompt.ps1`

## Default Read Sets

- discovery and roadmap: `AGENTS.md`, `ROADMAP.md`, `ai/PLAN.md`; add design, architecture, or README only when relevant
- planning: `AGENTS.md`, `ai/PLAN.md`, governing specs, and the matching prompt body if a title is used
- implementation: `AGENTS.md`, `ai/EXECUTION.md`, target `ai/PLAN_*.md`, plus owner guides required by the changed files
- workflow execution or integration: `AGENTS.md`, `ai/WORKFLOW.md`, `ai/EXECUTION.md`, relevant plans or worker logs
- verification: `AGENTS.md`, `ai/TESTING.md`, `ai/DOCUMENTATION.md`, `ai/REVIEWS.md`
- release: `AGENTS.md`, `ai/RELEASES.md`, executed plans, and changed release artifacts

## Prompt List

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
  - `Choose Execution Mode`
- Implementation
  - `Implement Plan`
  - `Implement Milestone`
- Workflow execution
  - `Run Plan With Inferred Mode`
  - `Run Plan On Single Branch`
  - `Run Plan As Shared Plan`
  - `Run Plans In Parallel`
  - `Run All Ready Plans`
  - `Run All Unfinished Plans`
  - `Check Worker Status`
  - `Check Active Workers`
- Implementation integration
  - `Integrate Shared Plan Output`
  - `Integrate Parallel Plan Output`
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
  - `Summarize Lifecycle State`
  - `Triage Validation Failure`
  - `Upgrade Dependencies And Build Tools`
  - `Compact AI Docs`
