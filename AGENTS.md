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
2. executable specs: integration tests, REST Docs tests, OpenAPI compatibility tests, benchmark checks
3. published contract docs: `README.md`, `src/docs/asciidoc/`
4. `ROADMAP.md` `## Current Project State` for the active release phase, breaking-change policy, and next target version
5. active planning in `ROADMAP.md` ordered plan sections
6. historical release notes in `CHANGELOG.md`

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

## Architecture Constraints

`docs/ARCHITECTURE.md` owns the compact descriptive repository snapshot, codebase map, current API shape, and structural guidance for this repository.
Use `.agents/references/architecture-detailed-map.md` only when the compact map is not enough.

When making architecture-sensitive changes:

- follow `docs/ARCHITECTURE.md`
- preserve the demo nature of the project and prefer direct code over abstraction
- keep `AGENTS.md` aligned only when repo-level architectural rules or AI-document ownership changed

## Workflow Invariants

`.agents/references/workflow.md` owns common branch, worktree, coordinator, worker, and integration rules; the on-demand workflow references own detailed delegated-work mechanics.

Repo-level invariants:

- treat `main` as the integration branch for completed work
- keep worktree or side-branch implementation isolated until the planned scope is complete and locally validated
- prefer merging accepted branches or pull requests; use cherry-pick only when the user asks for it, when accepting less than the full branch or pull request, or when a normal merge is not viable, and record the reason
- do not cut releases from unintegrated side branches, worktrees, detached tips, or changes that have not landed on `main`
- when creating a commit, follow the AI commit-message rules in `.agents/references/execution.md`; every completed task or milestone that changed tracked files must be committed before handoff or unrelated work starts

Specialized agents and repo-local skills may accelerate repeatable tasks when available and when the task clearly matches their scope.
Treat skills as workflow helpers that point back to the owner guides, not as higher-priority policy.
Read `.agents/skills/<skill>/SKILL.md` only when that skill is invoked or clearly applies.
Use `.agents/skills/repo-task/` for this repository's task starter dispatcher. Use `.agents/plugins/marketplace.json` only for Codex plugin marketplace configuration; Codex skills that need distribution belong inside a plugin bundle, for example `plugins/<plugin-name>/skills/<skill-name>/SKILL.md`.

## Definition Of Done

A change is complete when:

- the intended behavior exists in an appropriate spec artifact
- implementation and specs agree
- public contract artifacts are updated when behavior changed
- if the work was done in a git worktree or non-`main` branch, the finished branch has been pushed and a pull request is open or already merged onto `main`
- the required validation from `.agents/references/testing.md` passes; use `./build.ps1 build` for ordinary uncommitted implementation validation and `./build.ps1 -FullBuild build` when proving cumulative committed implementation work, whole plans, or release candidates
