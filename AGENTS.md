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
- Keep implementation, executable specs, published contract artifacts, human-facing docs, AI guidance, and generated references aligned for the actual change; use `.agents/references/documentation.md` when artifact ownership or cross-file alignment is unclear.
- Treat public behavior changes as incomplete until governing specs and published contract artifacts move together; treat internal refactors as incomplete if they create unnecessary contract churn.
- Run the required validation from `.agents/references/testing.md` for the correct diff boundary. If required validation cannot run or is explicitly out of scope, record the exact reason and remaining risk.
- Complete final review through `.agents/references/reviews.md`; do not hand off with unresolved blocking drift, missing security review for security-sensitive changes, or hidden scope expansion.
- Treat edits to `.agents/references/*.md` as incomplete until the edited reference documents satisfy `.agents/references/references-rules.md`; keep `references-rules.md` itself as the current rule set that other reference files are measured against, never as a changelog or record of completed edits.
- Learn from agent mistakes, user corrections, failed assumptions, and avoidable rework; when the correction reveals durable guidance, update the focused owner guide or `.agents/references/LEARNINGS.md` using that file's learning loop.
- Keep durable status in the owning artifacts; do not rely on final-response memory for plan progress, validation evidence, blockers, roadmap state, or release history.
- Commit every completed task or milestone that changed tracked files with the required AI commit-message format before handoff or unrelated work starts. During an explicitly ongoing interactive session, uncommitted work remains in progress until the user asks for handoff.
- Leave release work undone unless explicitly requested.

## Working Context And Guidance Loading

Read `AGENTS.md` first, then load only the source artifacts and owner guides that match the current task.
Do not treat `docs/` or `.agents/` guidance as higher-priority truth than executable specs, published contract docs, or the human-facing artifact that owns the topic.

### Fast Loading Paths

Treat cross-references in owner guides as conditional pointers, not recursive load requirements.
A loaded guide is terminal unless the current task matches another guide's explicit entry condition.

Start common tasks with these read sets:

- documentation-only edit with clear ownership: `AGENTS.md`, the target document, and the owning guide; for `.agents/references/*.md`, also read `.agents/references/references-rules.md`; load `.agents/references/documentation.md` only when artifact ownership or cross-file alignment is unclear
- bounded code, test, build, or workflow edit: `AGENTS.md`, `.agents/references/execution.md`, `.agents/references/code-style.md`, and the governing spec or source files; load documentation, testing, and review guides at their checkpoints instead of during initial context loading
- whole-plan execution: `AGENTS.md`, `.agents/references/plan-execution.md`, the active plan, and only the current milestone's named context
- release work: `AGENTS.md` and `.agents/references/releases.md`; load detailed release references only when their phase begins

Use `.agents/references/documentation.md` only when a task needs artifact routing beyond the fast paths, cross-file alignment, AI-document maintenance outside `.agents/references/*.md`, or repository knowledge ownership decisions.

### Owner Guide Entry Points

Use workflow guides on demand:

- planning or roadmap intake: `.agents/references/planning.md`
- whole-plan execution: `.agents/references/plan-execution.md`
- ad hoc implementation or a single milestone: `.agents/references/execution.md`
- local Gradle wrapper and AI command syntax: `.agents/references/command-wrapper.md`
- validation: `.agents/references/testing.md`, and `.agents/references/troubleshooting.md` only after a validation failure
- review: `.agents/references/reviews.md`
- branch, worktree, delegation, integration, or remote handoff: `.agents/references/workflow.md`
- release: `.agents/references/releases.md`

### Descriptive And Deep References

Load descriptive or deep references only when the task needs them:

- `docs/ARCHITECTURE.md` for structural code reading, architecture-sensitive changes, or package ownership questions
- `docs/DESIGN.md` for user-visible behavior, supported scope, security posture, or roadmap tradeoffs
- `docs/specs/application-lifecycle-spec.md` and `docs/specs/lifecycle-phase-activities.md` when lifecycle phase, activity, owner-guide mapping, or loop vocabulary changes
- `.agents/references/LEARNINGS.md` only from a targeted relevance scan, a known recurring repo lesson, or a correction that exposes durable guidance
- active `.agents/plans/PLAN_*.md` files only when planning, executing, verifying, or releasing that plan
- repository task prompts under `.agents/tasks/`, templates, detailed workflow references, skill bodies, archived plans, and reports only when directly invoked or required

Keep working context narrow. Treat the ownership lists above as routing aids, not a standing pre-flight bulk-load list; once work enters execution, follow `.agents/references/execution.md` or `.agents/references/plan-execution.md` for context switching and checkpoint summaries.

### AI Document Maintenance

For AI-document maintenance outside `.agents/references/*.md`, use `.agents/references/documentation.md`.
For `.agents/references/*.md` edits, use `.agents/references/references-rules.md`; add `.agents/references/documentation.md` only when artifact ownership or cross-file alignment is unclear.
When a request introduces or changes requirements for `.agents/references/*.md` documents, update `.agents/references/references-rules.md` in the same change before editing the affected reference documents, and express the update as the standing rule future reference documents must satisfy.

## Integration And Release Invariants

`main` is the integration branch for completed work.
Use `.agents/references/workflow.md` for branch, worktree, delegation, integration, and remote-handoff mechanics.
Use `.agents/references/releases.md` for release sequencing only after the user requests release work and the candidate is integrated on `main`.
