# AI Project Instructions

`README.md` is the human-facing counterpart of this file. `SETUP.md` is the local environment and onboarding guide. The `docs/` directory contains shared project knowledge and human-facing guides, including operations runbooks, and `.agents/` contains AI workflow support documents. Keep these sources aligned when their scopes overlap, but do not duplicate setup or operations runbook detail here.

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

## Working AI Context

Use the smallest task-shaped context that can answer the request. Guidance in `docs/` and `.agents/` helps route work, but it does not outrank executable specs, published contract docs, or the human-facing artifact that owns the topic.

Treat cross-references in loaded guides as conditional pointers, not recursive load requirements. A loaded guide is terminal unless another guide's explicit entry condition applies to the current task. Add more context only when the task exposes a concrete trigger for it.

## Documents Map

### Initial Read Scope

Start with the narrowest matching scope. Do not bulk-load `.agents/references/`, active plans, archived plans, templates, task prompts, reports, or skill bodies as a pre-flight default.

- named task prompt: identify the exact prompt from `.agents/tasks/README.md` or a direct path, load only that prompt, then follow its declared read set
- documentation-only edit with clear ownership: target document plus the owning guide
- `.agents/references/*.md` edit: target reference plus `.agents/references/references-rules.md`
- bounded code, test, build, or workflow edit: `.agents/references/execution.md`, `.agents/references/code-style.md`, and the governing spec or source files
- whole-plan execution: `.agents/references/plan-execution.md`, the active plan, and only the current plan task's named context
- release work: `.agents/references/releases.md`; load detailed release references only when their phase begins

### Domain Guides

Add only guides whose domains match the current task.

- artifact routing, cross-file alignment, AI-document maintenance outside `.agents/references/*.md`, or repository knowledge ownership: `.agents/references/documentation.md`
- rules for `.agents/references/*.md` documents: `.agents/references/references-rules.md`
- planning, triage, plan revision, or readiness review: `.agents/references/planning.md`
- whole active-plan execution, plan-task context switching, or plan checkpoint summaries: `.agents/references/plan-execution.md`
- ad hoc execution, single plan task, bounded-work context switching, or checkpoint summaries: `.agents/references/execution.md`
- edit shape and repo-local code conventions: `.agents/references/code-style.md`
- architecture, structural placement, package ownership, or codebase map: `.agents/references/architecture.md`
- product direction, supported scope, security posture, or roadmap tradeoffs: `docs/DESIGN.md`
- human-facing documentation index, lifecycle summary, AI collaboration guide, or operations runbook: `docs/README.md`, `docs/DEVELOPMENT_LIFECYCLE.md`, `docs/WORKING_WITH_AI.md`, or `docs/OPERATIONS.md`; use `.agents/references/documentation.md` when ownership or cross-file alignment is unclear
- local Gradle wrapper and AI command syntax: `.agents/references/command-wrapper.md`
- validation scope and commands: `.agents/references/testing.md`
- diagnosis after validation failure: `.agents/references/troubleshooting.md`
- review or security review: `.agents/references/reviews.md`
- branch, worktree, delegation, multi-agent state, sidecar, integration, or remote handoff: `.agents/references/workflow.md`
- workflow mode labels, multi-agent role roster, per-role read sets, or skill chain selection: `.agents/references/workflow.md`; load `.agents/skills/<skill-name>/SKILL.md` only when a skill is invoked or clearly applies; see `.agents/skills/README.md` for the skill catalog and the standard `M0`–`M4` skill chain
- release sequencing: `.agents/references/releases.md`
- deployment, post-release verification, rollback, incident response, hotfix, patch, backport, or deprecation routing: `.agents/references/operations.md`
- commit message format, required AI trailer block, and non-interactive commit construction: `.gitmessage` (authoritative template) plus `.agents/references/execution.md` "Commits" rules
- application lifecycle phase, activity, owner-guide mapping, or loop vocabulary: `.agents/references/application-lifecycle.md`
- durable repo-wide lesson: `.agents/references/LEARNINGS.md`
- active plan content: the relevant `.agents/plans/PLAN_*.md`
- repository task prompt, template, archived plan, report, or skill body: only when directly invoked or required

## AI Guidance Maintenance

When AI guidance changes, update the focused owner instead of duplicating the rule in task prompts, plans, skills, workflow state files, or final responses.

- for `.agents/references/*.md` edits, follow the reference-document rules owner in the map above
- add the artifact-routing owner only when artifact ownership, cross-file alignment, or non-reference AI document maintenance is unclear
- when a request introduces or changes requirements for reference documents, update the reference-document rules owner in the same change before editing the affected references, and express the update as the standing rule future reference documents must satisfy

## Definition Of Done

Use these completion rules for AI work in this repository:

- Do not call a task, plan task, plan, delegated run, branch, or release complete while requested scope is unfinished; record blocked, skipped, failed, or cancelled work explicitly.
- Represent the intended behavior or documentation change in the owning spec, contract, or guidance artifact before or alongside the implementation.
- Keep implementation, executable specs, published contract artifacts, human-facing docs, AI guidance, and generated references aligned for the actual change; use `.agents/references/documentation.md` when artifact ownership or cross-file alignment is unclear.
- Treat public behavior changes as incomplete until governing specs and published contract artifacts move together; treat internal refactors as incomplete if they create unnecessary contract churn.
- Run the required validation from `.agents/references/testing.md` for the correct diff boundary. If required validation cannot run or is explicitly out of scope, record the exact reason and remaining risk.
- Complete final review through `.agents/references/reviews.md`; do not hand off with unresolved blocking drift, missing security review for security-sensitive changes, or hidden scope expansion.
- Treat edits to `.agents/references/*.md` as incomplete until the edited reference documents satisfy `.agents/references/references-rules.md`; keep `references-rules.md` itself as the current rule set that other reference files are measured against, never as a changelog or record of completed edits.
- Learn from agent mistakes, user corrections, failed assumptions, and avoidable rework; when the correction reveals durable guidance, update the focused owner guide or `.agents/references/LEARNINGS.md` using that file's learning loop.
- Keep durable status in the owning artifacts; do not rely on final-response memory for plan progress, validation evidence, blockers, roadmap state, or release history.
- Commit every completed task or plan task that changed tracked files with the required AI commit-message format before handoff or unrelated work starts. The format is owned by `.gitmessage` and the "Commits" rules in `.agents/references/execution.md`; load both before composing a commit, include every applicable `Project-*` footer plus `Co-authored-by` and `Validation`, keep the trailer block contiguous, and build the message via a commit message file or a single final paragraph rather than chained `-m` arguments. During an explicitly ongoing interactive session, uncommitted work remains in progress until the user asks for handoff.
- Leave release work undone unless explicitly requested.

## Integration And Release Invariants

`main` is the integration branch for completed work.
Use `.agents/references/workflow.md` for branch, worktree, delegation, multi-agent state, sidecar, integration, and remote-handoff mechanics.
Use `.agents/references/releases.md` for release sequencing only after the user requests release work and the candidate is integrated on `main`.
Use `.agents/references/operations.md` once a released artifact enters deployment, post-release verification, rollback, incident response, hotfix, patch, backport, or deprecation routing.
