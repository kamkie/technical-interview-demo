# AI Project Instructions

`README.md` is the human-facing counterpart of this file. `SETUP.md` is the environment and onboarding guide. The `docs/` directory contains shared project knowledge, and `.agents/` contains Codex-specific support documents. Keep these sources aligned when their scopes overlap, but do not duplicate setup/runbook detail from `SETUP.md` here.

## Core Approach: Spec-Driven Development

This repository should be changed using Spec-Driven Development.

Principle:

1. Identify the behavior being changed.
2. Identify the spec artifact that defines that behavior.
3. Update or add the spec first.
4. Implement the smallest code change that satisfies the updated spec.
5. Verify the executable and published specs remain aligned.

If the intended behavior is not clear enough to express as a spec, stop and clarify it before implementing.

## Spec Priority

When resolving truth, use this order:

1. explicit user request in the current task
2. executable specs and checked contract baselines: integration tests, REST Docs tests, OpenAPI compatibility tests, `src/test/resources/openapi/approved-openapi.json`, benchmark checks
3. published contract docs: `README.md`, `src/docs/asciidoc/`
4. `ROADMAP.md` `## Current Project State` for the active release phase, breaking-change policy, and next target version
5. active planning in `ROADMAP.md` ordered plan sections
6. historical release notes in `CHANGELOG.md`
7. topic-owning AI or human guidance documents

## Definition Of Done

Use these completion rules for AI work in this repository:

- Do not call a task, milestone, plan, delegated run, branch, or release complete while requested scope is unfinished; record blocked, skipped, failed, or cancelled work explicitly.
- Represent the intended behavior or documentation change in the owning spec, contract, or guidance artifact before or alongside the implementation.
- Keep implementation, executable specs, published contract artifacts, human-facing docs, AI guidance, and generated references aligned for the actual change through `.agents/references/documentation.md`.
- Treat public behavior changes as incomplete until governing specs and published contract artifacts move together; treat internal refactors as incomplete if they create unnecessary contract churn.
- Run the required validation from `.agents/references/testing.md` for the correct diff boundary. If required validation cannot run or is explicitly out of scope, record the exact reason and remaining risk.
- Complete final review through `.agents/references/reviews.md`; do not hand off with unresolved blocking drift, missing security review for security-sensitive changes, or hidden scope expansion.
- Treat edits to `.agents/references/*.md` as incomplete until the edited reference documents satisfy `.agents/references/references-rules.md`; keep `references-rules.md` itself as the current rule set that other reference files are measured against, never as a changelog or record of completed edits.
- Keep active plans, progress trackers, validation ledgers, worker logs, roadmap entries, and changelog entries aligned with the actual state; do not rely on final-response memory for durable status.
- Commit every completed task or milestone that changed tracked files with the required AI commit-message format before handoff or unrelated work starts. During an explicitly ongoing interactive session, uncommitted work remains in progress until the user asks for handoff.
- For work done outside `main`, finish only from an integrated state: push the finished branch and open or merge the pull request, unless the user explicitly chose a no-PR flow already on `main`.
- For delegated or coordinated work, wait for every worker to reach a terminal state and record integration status before declaring the run complete.
- Leave release work undone unless explicitly requested. If release work is requested, tag only the validated release candidate on `main`, keep release notes and roadmap cleanup aligned, and archive released plans through `.agents/references/releases.md`.

## Working Context And Guidance Loading

Read `AGENTS.md` first, then load only the source artifacts and owner guides that match the current task.
Do not treat `docs/` or `.agents/` guidance as higher-priority truth than executable specs, published contract docs, or the human-facing artifact that owns the topic.

Use repository artifacts by ownership:

- behavior and public API truth: `src/test/java/`, `src/docs/asciidoc/`, `src/test/resources/openapi/approved-openapi.json`, and `README.md`
- active work and release state: `ROADMAP.md` for current planning and `CHANGELOG.md` for released history
- setup and troubleshooting: `SETUP.md`
- generated frontend import guidance: `docs/FRONTEND_AI_CONTRACT.md`
- artifact routing and repository knowledge layout: `.agents/references/documentation.md`
- reference-document maintenance rules: `.agents/references/references-rules.md`

Use workflow guides on demand:

- planning or roadmap intake: `.agents/references/planning.md`
- whole-plan execution: `.agents/references/plan-execution.md`
- ad hoc implementation or a single milestone: `.agents/references/execution.md`
- local Gradle wrapper and AI command syntax: `.agents/references/environment-quick-ref.md`
- validation: `.agents/references/testing.md`, and `.agents/references/troubleshooting.md` only after a validation failure
- review: `.agents/references/reviews.md`
- branch, worktree, delegation, integration, or remote handoff: `.agents/references/workflow.md`
- release: `.agents/references/releases.md`

Load descriptive or deep references only when the task needs them:

- `docs/ARCHITECTURE.md` for structural code reading, architecture-sensitive changes, or package ownership questions
- `docs/DESIGN.md` for user-visible behavior, supported scope, security posture, or roadmap tradeoffs
- `docs/specs/application-lifecycle-spec.md` and `docs/specs/lifecycle-phase-activities.md` when lifecycle phase, activity, owner-guide mapping, or loop vocabulary changes
- `.agents/references/LEARNINGS.md` only from a targeted relevance scan or a known recurring repo lesson
- active `.agents/plans/PLAN_*.md` files only when planning, executing, verifying, or releasing that plan
- task files, templates, detailed workflow references, skill bodies, archived plans, and reports only when directly invoked or required

Keep working context narrow. Treat the ownership lists above as routing aids, not a standing pre-flight bulk-load list; once work enters execution, follow `.agents/references/execution.md` or `.agents/references/plan-execution.md` for context switching and checkpoint summaries.

For AI-document maintenance and required updates by change type, use `.agents/references/documentation.md`; for `.agents/references/*.md` edits, also use `.agents/references/references-rules.md`.
When a request introduces or changes requirements for AI documents, update `.agents/references/references-rules.md` in the same change before editing the affected AI documents, and express the update as the standing rule future reference documents must satisfy.

## Integration And Release Invariants

`main` is the integration branch for completed work.
Do not cut releases from unintegrated side branches, worktrees, detached tips, or changes that have not landed on `main`.

Use `.agents/references/workflow.md` for branch, worktree, delegation, integration, and remote-handoff mechanics.
Use `.agents/references/releases.md` for release sequencing after implementation is integrated.
