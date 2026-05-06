# Plan: Adopt Selected Pskoett AI Skill Guidance

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Planning |
| Status | Ready |

## Summary
- Selectively fold the useful guidance patterns from `pskoett/pskoett-ai-skills` into this repository's existing AI workflow documents.
- The change should improve session-start relevance scanning, context-quality degradation handling, and per-milestone scope or hardening checks without importing external skill directories, hooks, or per-incident learning files.
- Success is measured by concise updates to the owning AI guides, no new skill framework or history directories, and lightweight documentation validation passing.

## Scope
### In scope
- Add a pre-flight relevance scan rule to `AGENTS.md` onboarding guidance.
- Add a context-quality degradation checkpoint to `AGENTS.md` context hygiene guidance.
- Add a per-milestone scope-drift check to `ai/EXECUTION.md`.
- Add a post-validation hardening review reminder to `ai/EXECUTION.md`, routed through `ai/REVIEWS.md`.
- Keep this plan and `ROADMAP.md` aligned while the work is active.

### Out of scope
- Installing or vendoring `pskoett/pskoett-ai-skills`.
- Adding `.learnings/`, `.evals/`, `.context-surfing/`, plugin manifests, hooks, or skill installer instructions.
- Changing application source code, public API behavior, OpenAPI, REST Docs, HTTP examples, setup docs, release workflow, or build scripts.
- Replacing this repo's `ai/skills/`, `ai/PROMPTS.md`, `ai/LEARNINGS.md`, `ai/TESTING.md`, or `./build.ps1` model with the external skill pipeline.

## Current State
- `AGENTS.md` already defines onboarding, AI instruction load policy, and context hygiene, but it does not explicitly require a task-area pre-flight scan of `ai/LEARNINGS.md` and active plans.
- `AGENTS.md` context hygiene covers stale context and bulk-loading control, but it does not explicitly name response-quality degradation such as contradiction, repeated re-derivation, or unjustified hedging as a checkpoint.
- `ai/EXECUTION.md` defines the milestone loop and validation path, but it does not explicitly require a scope-drift check between milestones.
- `ai/EXECUTION.md` already routes review expectations through `ai/REVIEWS.md`, but the milestone loop can make the post-validation hardening pass more explicit before a checkpoint commit.
- `ai/LEARNINGS.md` already owns curated durable lessons, including in-flight learning triggers, so the external `.learnings/` aggregation model is redundant and should not be copied.
- `ai/TESTING.md` and `./build.ps1 build` already provide the verify gate; importing the external `verify-gate` skill would weaken the repo-owned validation invariant.

## Requirement Gaps And Open Questions
- No material product or workflow ambiguity blocks planning.
- The scratch recommendation names four small guidance bullets. This plan treats all four as desired unless execution finds overlap that would make one redundant.
- If implementation discovers the intended wording duplicates recently committed guidance, compact into the single best owner instead of adding a second copy.

## Locked Decisions And Assumptions
- Use the external skill repository only as source inspiration; do not install or import it.
- Keep standing policy in the current owning documents: onboarding and context hygiene in `AGENTS.md`, execution checkpoints in `ai/EXECUTION.md`, review priorities in `ai/REVIEWS.md`, durable lessons in `ai/LEARNINGS.md`.
- Do not create new top-level AI guide files or hidden workflow directories for this work.
- Keep the guidance current-state and concise; do not add historical rationale about the external skill evaluation to standing files.

## Execution Mode Fit
- Recommended default mode: `Single Branch`.
- The work is a small documentation-only AI-guidance update with overlapping ownership in `AGENTS.md` and `ai/EXECUTION.md`; worker fanout would add coordination cost.
- Coordinator-owned files:
  - `ai/PLAN_pskoett_ai_skill_guidance_adoption.md`
  - `ROADMAP.md`
  - `AGENTS.md`
  - `ai/EXECUTION.md`

## Affected Artifacts
- AI guidance:
  - `AGENTS.md`
  - `ai/EXECUTION.md`
  - this plan file
- Roadmap:
  - `ROADMAP.md`
- No application tests, REST Docs, OpenAPI baseline, HTTP examples, README, SETUP, build scripts, or source files should change.

## Execution Milestones
### Milestone 1: Plan And Roadmap
- goal: record the selected external-skill recommendations as a repo-native execution plan.
- owned files or packages:
  - `ai/PLAN_pskoett_ai_skill_guidance_adoption.md`
  - `ROADMAP.md`
- shared files that a `Shared Plan` worker must leave to the coordinator:
  - not applicable in `Single Branch`
- behavior to preserve:
  - no application or public-contract changes
  - no external skill import
- exact deliverables:
  - ready plan with scope, non-goals, milestones, validation, and roadmap tracking
  - roadmap entry pointing to this plan and current lifecycle
- validation checkpoint:
  - `git diff --check`
  - `./build.ps1 build` lightweight-file shortcut or full build if the wrapper requires it
- commit checkpoint:
  - `docs: plan selected pskoett ai guidance adoption`

### Milestone 2: Onboarding And Context Hygiene
- goal: make agents surface relevant existing guidance before work and stop when context quality visibly degrades.
- owned files or packages:
  - `AGENTS.md`
- shared files that a `Shared Plan` worker must leave to the coordinator:
  - this plan and `ROADMAP.md`
- behavior to preserve:
  - the load policy remains on-demand and does not require bulk-loading plans or references
- exact deliverables:
  - add a pre-flight scan step after onboarding quick-start step 6 that asks agents to scan `ai/LEARNINGS.md` and active `ai/PLAN_*.md` files only for task-overlapping entries
  - add a context-quality degradation bullet under `Context Hygiene` requiring a short handoff summary in the active plan or final/update note before continuing after contradiction, unjustified hedging, or repeated re-derivation
- validation checkpoint:
  - manual consistency check against `AGENTS.md` load policy
  - `git diff --check`
- commit checkpoint:
  - `docs: add ai preflight and context quality checkpoints`

### Milestone 3: Execution Scope And Hardening Checks
- goal: make milestone transitions more explicit about scope drift and post-validation review.
- owned files or packages:
  - `ai/EXECUTION.md`
- shared files that a `Shared Plan` worker must leave to the coordinator:
  - this plan and `ROADMAP.md`
- behavior to preserve:
  - milestone validation and commit discipline remain as currently defined
  - review rules stay owned by `ai/REVIEWS.md`
- exact deliverables:
  - add a milestone-loop or guardrail bullet requiring the agent to re-check the plan's in-scope outcome before starting the next milestone and record pivots as plan amendments or validation notes
  - add a post-validation hardening review reminder that uses `ai/REVIEWS.md` priority order before committing a completed milestone
- validation checkpoint:
  - manual consistency check against `ai/EXECUTION.md`, `ai/REVIEWS.md`, and `ai/DOCUMENTATION.md`
  - `git diff --check`
  - `./build.ps1 build`
- commit checkpoint:
  - `docs: add ai execution scope and hardening checkpoints`

## Edge Cases And Failure Modes
- Pre-flight wording can accidentally require agents to bulk-load all plans. Keep the rule relevance-filtered and targeted.
- Context-quality wording can create unnecessary ceremony for small tasks. Keep it tied to concrete degradation signals.
- Scope-drift wording can conflict with legitimate plan amendments. Allow pivots when they are recorded explicitly.
- Hardening wording can duplicate `ai/REVIEWS.md`. Keep `ai/EXECUTION.md` as a trigger and `ai/REVIEWS.md` as the authority.
- Adding external skill install instructions would fragment the repo's skill model and is explicitly out of scope.

## Validation Plan
- Run `git diff --check` after plan creation and after implementation.
- Run `./build.ps1 build`; for docs-only changes the lightweight-file shortcut is acceptable unless a full build is explicitly requested.
- Manually review `AGENTS.md`, `ai/EXECUTION.md`, `ai/REVIEWS.md`, and `ai/DOCUMENTATION.md` for overlap, contradictions, and owner drift.
- Confirm no `.learnings/`, `.evals/`, `.context-surfing/`, plugin, hook, source-code, OpenAPI, REST Docs, or HTTP example changes were introduced.

## Testing Strategy
- Unit tests: not applicable; no executable logic changes.
- Integration tests: not applicable; no database, service, or API behavior changes.
- Contract tests: not applicable; no REST API, OpenAPI, REST Docs, HTTP examples, or README contract changes.
- Smoke or benchmark tests: not applicable; no runtime or performance behavior changes.
- Manual checks: required for documentation ownership, cross-reference consistency, and ensuring guidance stays concise and current-state.

## Better Engineering Notes
- The external skills are built for agents with little repo-specific guidance; this repo already has stronger standing guidance for planning, execution, testing, review, and curated learnings.
- If these bullets later prove noisy in practice, compact them into the single best owner instead of adding more prompt-level exceptions.
- Do not add a reusable prompt or local skill until the workflow repeats often enough to justify one.

## Validation Results
- 2026-05-07 plan creation:
  - `git diff --check` passed.
  - `./build.ps1 build` passed through the lightweight-file shortcut, reporting that only `ai/PLAN_pskoett_ai_skill_guidance_adoption.md` and `ROADMAP.md` changed and that the Gradle build was skipped.
  - Manual plan-shape review confirmed all required sections from `ai/PLAN.md` are present.

## User Validation
- Review the resulting `AGENTS.md` and `ai/EXECUTION.md` changes.
- Confirm the four intended improvements are visible:
  - task-relevant pre-flight scan
  - context-quality degradation checkpoint
  - per-milestone scope-drift check
  - post-validation hardening review trigger
