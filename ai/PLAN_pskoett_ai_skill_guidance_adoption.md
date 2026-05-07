# Plan: Adopt Selected Pskoett AI Skill Guidance

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Integration |
| Status | Implemented |

## Summary
- Selectively fold the useful guidance patterns from `pskoett/pskoett-ai-skills` into this repository's existing AI workflow documents.
- The change should improve session-start relevance scanning, context-quality degradation handling, and per-milestone scope or review checks without importing external skill directories, hooks, or per-incident learning files.
- Roadmap tracking: `ROADMAP.md` tracks this under `Ordered Plan` / `Moving to 2.0` / `AI Workflow Guidance` as selected AI workflow guidance work.
- Replan result: keep the plan in `Linear Plan`, treat the initial plan and roadmap baseline as complete, and execute the remaining guidance changes in two small documentation milestones.
- Success is measured by concise updates to the owning AI guides, no new skill framework or history directories, and lightweight documentation validation passing.

## Scope
### In scope
- Add a search-first pre-flight relevance scan rule to `AGENTS.md` onboarding guidance.
- Add a context-quality degradation checkpoint to `AGENTS.md` context hygiene guidance.
- Tighten the per-milestone scope-drift check in `ai/EXECUTION.md` without duplicating existing scope and plan-mismatch guardrails.
- Add a post-validation review reminder to `ai/EXECUTION.md`, routed through `ai/REVIEWS.md`.
- Keep this plan and `ROADMAP.md` aligned while the work is active.

### Out of scope
- Installing or vendoring `pskoett/pskoett-ai-skills`.
- Adding `.learnings/`, `.evals/`, `.context-surfing/`, plugin manifests, hooks, or skill installer instructions.
- Changing application source code, public API behavior, OpenAPI, REST Docs, HTTP examples, setup docs, release workflow, or build scripts.
- Replacing this repo's `ai/skills/`, `ai/PROMPTS.md`, `ai/LEARNINGS.md`, `ai/TESTING.md`, or `./build.ps1` model with the external skill pipeline.

## Current State
- The external skill source includes plugin, hook, `.learnings/`, `.evals/`, pre-flight, context-surfing, intent-framing, verify-gate, and simplify/harden workflows. This repository should adopt only the selected lightweight guidance patterns because it already has repo-owned AI guides, validation wrappers, plans, and learning rules.
- `AGENTS.md` already defines onboarding, AI instruction load policy, and context hygiene, but it does not explicitly require a task-area pre-flight scan of `ai/LEARNINGS.md` and active plans.
- `AGENTS.md` also says AI guidance should be loaded on demand, so the pre-flight addition must be a targeted search/read rule, not a standing instruction to open every active plan or reference.
- `AGENTS.md` context hygiene covers stale context and bulk-loading control, but it does not explicitly name response-quality degradation such as contradiction, repeated re-derivation, or unjustified hedging as a checkpoint.
- `ai/EXECUTION.md` already starts each milestone by re-reading target scope and already says to revise the plan when it stops matching repo reality. The implementation should make the between-milestone scope check explicit by refining those rules, not by adding a competing workflow.
- `ai/EXECUTION.md` already routes review expectations through `ai/REVIEWS.md`, but the milestone loop can make the post-validation review pass more explicit before a checkpoint commit.
- `ai/REVIEWS.md` already owns the actual review priority order, including documentation/process review and security review triggers.
- `ai/DOCUMENTATION.md` confirms the routing: AI repository rules belong in `AGENTS.md`, single-agent execution flow belongs in `ai/EXECUTION.md`, active planned work belongs in `ROADMAP.md`.
- `ai/LEARNINGS.md` already owns curated durable lessons, including in-flight learning triggers, so the external `.learnings/` aggregation model is redundant and should not be copied.
- `ai/TESTING.md` and `./build.ps1 build` already provide the verify gate; importing the external `verify-gate` skill would weaken the repo-owned validation invariant.

## Requirement Gaps And Open Questions
- No material product or workflow ambiguity blocks planning.
- The scratch recommendation names four small guidance bullets. This plan treats all four as desired unless execution finds overlap that would make one redundant.
- If implementation discovers the intended wording duplicates recently committed guidance, compact into the single best owner instead of adding a second copy.
- The external source should not be re-imported, installed, or treated as higher-priority than this repository's owner guides during execution.

## Locked Decisions And Assumptions
- Use the external skill repository only as source inspiration; do not install or import it.
- Keep standing policy in the current owning documents: onboarding and context hygiene in `AGENTS.md`, execution checkpoints in `ai/EXECUTION.md`, review priorities in `ai/REVIEWS.md`, durable lessons in `ai/LEARNINGS.md`.
- Do not create new top-level AI guide files or hidden workflow directories for this work.
- Keep the guidance current-state and concise; do not add historical rationale about the external skill evaluation to standing files.
- Prefer search-before-read wording for task-relevant learning and active-plan checks so the new rule preserves the existing on-demand load policy.

## Execution Mode Fit
- Recommended default mode: `Linear Plan`.
- The work is a small documentation-only AI-guidance update with overlapping ownership in `AGENTS.md` and `ai/EXECUTION.md`; worker fanout would add coordination cost.
- Coordinator-owned files:
  - `ai/PLAN_pskoett_ai_skill_guidance_adoption.md`
  - `ROADMAP.md`
  - `AGENTS.md`
  - `ai/EXECUTION.md`
- Read-only alignment references unless a contradiction is discovered:
  - `ai/REVIEWS.md`
  - `ai/DOCUMENTATION.md`
  - `ai/LEARNINGS.md`

## Affected Artifacts
- AI guidance:
  - `AGENTS.md`
  - `ai/EXECUTION.md`
  - this plan file
- Read-only alignment references:
  - `ai/REVIEWS.md`
  - `ai/DOCUMENTATION.md`
  - `ai/LEARNINGS.md`
- Roadmap:
  - `ROADMAP.md`
- Unreleased history:
  - `CHANGELOG.md`
- No application tests, REST Docs, OpenAPI baseline, HTTP examples, README, SETUP, build scripts, or source files should change.

## Execution Milestones
### Milestone 0: Plan And Roadmap Baseline
- status: complete; this milestone covers the original plan creation plus this replan.
- goal: record the selected external-skill recommendations as a repo-native execution plan and keep the roadmap entry current.
- owned files or packages:
  - `ai/PLAN_pskoett_ai_skill_guidance_adoption.md`
  - `ROADMAP.md`
- shared files that a `Single-Plan Fanout` worker must leave to the coordinator:
  - not applicable in `Linear Plan`
- context required before execution:
  - `AGENTS.md`, `ai/PLAN.md`, `ai/DOCUMENTATION.md`, `ROADMAP.md`, and this plan
- behavior to preserve:
  - no application or public-contract changes
  - no external skill import
- exact deliverables:
  - ready plan with scope, non-goals, milestones, validation, and roadmap tracking
  - roadmap entry pointing to this plan and current lifecycle
  - replan that narrows the remaining work to targeted `AGENTS.md` and `ai/EXECUTION.md` guidance edits
- validation checkpoint:
  - `git diff --check`
  - `./build.ps1 build` lightweight-file shortcut or full build if the wrapper requires it
- commit checkpoint:
  - `docs: plan selected pskoett ai guidance adoption`
  - `docs: replan selected pskoett ai guidance adoption`

### Milestone 1: Onboarding And Context Hygiene
- goal: make agents surface relevant existing guidance before work and stop when context quality visibly degrades.
- owned files or packages:
  - `AGENTS.md`
- shared files that a `Single-Plan Fanout` worker must leave to the coordinator:
  - this plan and `ROADMAP.md`
- context required before execution:
  - `AGENTS.md`, `ai/DOCUMENTATION.md`, `ai/LEARNINGS.md`, and this milestone
- behavior to preserve:
  - the load policy remains on-demand and does not require bulk-loading plans or references
- exact deliverables:
  - add a pre-flight relevance step after onboarding quick-start step 6 or in the adjacent load-policy section
  - the pre-flight wording must tell agents to use task terms to search `ai/LEARNINGS.md` and active `ai/PLAN_*.md` files, then open only task-overlapping matches
  - the pre-flight wording must explicitly avoid reading all active plans, archived plans, prompts, references, templates, or skills by default
  - add a context-quality degradation bullet under `Context Hygiene` requiring a short state summary before continuing after contradiction, unstable assumptions, unjustified hedging, or repeated re-derivation
  - the state summary belongs in the active plan, worker log, or validation notes when such an artifact exists; otherwise use a concise user update or final note
- validation checkpoint:
  - manual consistency check against `AGENTS.md` load policy
  - `git diff --check`
- commit checkpoint:
  - `docs: add ai preflight and context quality checkpoints`

### Milestone 2: Execution Scope And Review Checks
- goal: make milestone transitions more explicit about scope drift and post-validation review without moving review policy out of `ai/REVIEWS.md`.
- owned files or packages:
  - `ai/EXECUTION.md`
- shared files that a `Single-Plan Fanout` worker must leave to the coordinator:
  - this plan and `ROADMAP.md`
- context required before execution:
  - `ai/EXECUTION.md`, `ai/REVIEWS.md`, `ai/DOCUMENTATION.md`, and this milestone
- behavior to preserve:
  - milestone validation and commit discipline remain as currently defined
  - review rules stay owned by `ai/REVIEWS.md`
- exact deliverables:
  - refine the common milestone loop so the agent re-checks the plan's in-scope outcome, locked decisions, and non-goals before moving to the next milestone or claiming the current checkpoint is done
  - record meaningful pivots as plan amendments, worker-log updates, or validation notes before continuing
  - add a post-validation review reminder before the checkpoint commit that uses `ai/REVIEWS.md` priority order
  - keep security review conditional on the `ai/REVIEWS.md` triggers; do not imply every documentation-only milestone needs a full security pass
- validation checkpoint:
  - manual consistency check against `ai/EXECUTION.md`, `ai/REVIEWS.md`, and `ai/DOCUMENTATION.md`
  - `git diff --check`
  - `./build.ps1 build`
- commit checkpoint:
  - `docs: add ai execution scope and review checkpoints`

## Edge Cases And Failure Modes
- Pre-flight wording can accidentally require agents to bulk-load all plans. Keep the rule relevance-filtered and targeted.
- Context-quality wording can create unnecessary ceremony for small tasks. Keep it tied to concrete degradation signals.
- Scope-drift wording can conflict with legitimate plan amendments. Allow pivots when they are recorded explicitly.
- Review wording can duplicate `ai/REVIEWS.md`. Keep `ai/EXECUTION.md` as a trigger and `ai/REVIEWS.md` as the authority.
- Security-review wording can over-apply to documentation-only milestones. Keep the security pass conditional on actual security-sensitive changes.
- Adding external skill install instructions would fragment the repo's skill model and is explicitly out of scope.
- `ROADMAP.md` uses `[x]` for selected active work, not completed history. Do not remove this plan's roadmap entry until execution is implemented or otherwise closed.

## Validation Plan
- Run `git diff --check` after plan creation and after implementation.
- Run `./build.ps1 build`; for docs-only changes the lightweight-file shortcut is acceptable unless a full build is explicitly requested.
- Manually review `AGENTS.md`, `ai/EXECUTION.md`, `ai/REVIEWS.md`, `ai/DOCUMENTATION.md`, and `ai/LEARNINGS.md` for overlap, contradictions, and owner drift.
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
- 2026-05-07 replan:
  - `git diff --check` passed.
  - `./build.ps1 build` passed through the lightweight-file shortcut, reporting docs-only changes and skipping the Gradle build; the changed-file list also included an unrelated pre-existing edit to `ai/PLAN_manual_regression_execution.md`.
  - Manual consistency review confirmed the replan keeps `AGENTS.md`, `ai/EXECUTION.md`, `ai/REVIEWS.md`, `ai/DOCUMENTATION.md`, and `ai/LEARNINGS.md` ownership boundaries intact.
  - evaluation kept the selected four recommendations but tightened them to avoid bulk-loading active plans and duplicating `ai/EXECUTION.md` or `ai/REVIEWS.md` policy.
- 2026-05-07 Milestone 1:
  - Manual consistency check passed against `AGENTS.md` load policy: the relevance scan uses task terms, opens only task-overlapping matches, and preserves the no-bulk-load rule.
  - Manual context-hygiene review passed: degraded context-quality summaries route to the active plan, worker log, validation notes, or concise user-facing notes without creating new artifacts.
  - `git diff --check` passed.
- 2026-05-07 Milestone 2:
  - Manual consistency check passed against `ai/EXECUTION.md`, `ai/REVIEWS.md`, and `ai/DOCUMENTATION.md`: the milestone loop triggers post-validation review through the review guide, keeps the security lens conditional on review-guide triggers, and records meaningful pivots in existing tracking artifacts.
  - Manual no-import review confirmed no `.learnings/`, `.evals/`, `.context-surfing/`, plugin, hook, source-code, OpenAPI, REST Docs, or HTTP example changes were introduced.
  - `git diff --check` passed.
  - `./build.ps1 build` passed through the lightweight-file shortcut, reporting docs-only changes and skipping the Gradle build.

## User Validation
- Review the resulting `AGENTS.md` and `ai/EXECUTION.md` changes.
- Confirm the four intended improvements are visible:
  - search-first task-relevant pre-flight scan
  - context-quality degradation checkpoint
  - per-milestone scope-drift check
  - post-validation review trigger
