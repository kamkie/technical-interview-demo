# Plan Execution Guide For AI Agents

`.agents/references/plan-execution.md` owns execution of a whole active plan across milestones.
Use it when the user asks to implement an entire `.agents/plans/PLAN_*.md` file, run all remaining milestones in a plan, or resume a partially implemented active plan.

Use `.agents/references/execution.md` for ad hoc tasks and one-milestone work.
Use `.agents/references/workflow.md` only when branch, worktree, delegation, worker-log, or integration mechanics are needed.
Use `.agents/references/releases.md` only for explicit release work after implementation is integrated.

## Execution Goal

Whole-plan execution means:

- implement the smallest spec-driven change that satisfies the approved plan
- preserve the current public contract unless the plan intentionally changes it
- execute milestones in order unless the plan explicitly allows a different order
- keep specs, docs, validation evidence, roadmap state, and changelog text aligned as work lands
- create a normal commit after each completed milestone
- finish local validation before any push or pull-request handoff
- leave release sequencing undone unless the user explicitly asks for release work

## Before Starting

Load the smallest useful read set:

- `AGENTS.md`
- this file
- the target active plan
- `.agents/references/documentation.md` when artifact routing is part of the work
- `.agents/references/testing.md` for validation scope
- `.agents/references/reviews.md` before finalizing a milestone
- `.agents/references/workflow.md` only when delegation, worktrees, worker logs, or integration mechanics are in scope

Before editing:

- confirm the user wants the whole plan, not only one named milestone
- confirm the plan lifecycle is ready enough to execute
- re-read the plan summary, scope, locked decisions, non-goals, current milestone, and that milestone's context field
- if a real requirement gap appears, revise the plan before implementation instead of filling the gap ad hoc
- if the work shape changes who may edit shared files, update the plan before splitting work

## Whole-Plan Loop

For each milestone:

1. Load only the extra context named by that milestone.
2. Update the governing spec first when behavior intentionally changes.
3. Implement the smallest coherent code or documentation change for that milestone.
4. Keep artifact routing aligned through `.agents/references/documentation.md`.
5. Run the milestone validation named by the plan, then any broader validation required by `.agents/references/testing.md`.
6. If validation fails, load `.agents/references/troubleshooting.md` before choosing a recovery path.
7. Review the validated diff using `.agents/references/reviews.md`.
8. Re-check the plan's scope, locked decisions, non-goals, and milestone deliverables.
9. Record validation evidence, blockers, pivots, or follow-up notes in the plan or worker log owned by the current work shape.
10. Commit the completed milestone before starting the next one. Include a `Trigger: <category> - <source>` body line, normally `Trigger: plan milestone - <plan path> <milestone name>`; use `whole-plan implementation` only for a commit that intentionally covers a whole active plan checkpoint rather than one milestone.

Do not defer milestone commits until the end of the plan.
If a milestone is blocked or only partially implemented, record the blocker but do not mark it complete.

## Context Switching

Keep plan execution context narrow as the work moves:

- stop referencing milestone-specific files once that milestone is committed
- use the plan's current state and validation notes as the durable summary instead of re-reading long logs
- when switching to a new package, doc family, or validation layer, load the owner guide for that slice only when it becomes relevant
- if repeated re-derivation or contradictions appear, write a short current-state checkpoint in the plan before continuing

## Tracking Updates

During whole-plan execution:

- keep the plan `Lifecycle` current, normally `Phase=Implementation` and `Status=In Progress` while work is underway
- update `Validation Results` with exact commands and outcomes
- update `ROADMAP.md` when active-work state changes
- update `CHANGELOG.md` under `## [Unreleased]` when the plan records unreleased user-facing, maintainer-facing, or AI-guidance changes
- follow `.agents/references/workflow.md` for worker logs and integration notes only when work has actually been split

## Completion

Whole-plan execution is complete when:

- every requested milestone is implemented or explicitly recorded as blocked
- every completed milestone has a commit
- required specs and documentation artifacts are aligned
- required validation from `.agents/references/testing.md` passed, or any inability to run it is recorded
- final review found no unresolved blocking drift
- the active plan reflects the final non-release state, normally `Phase=Integration` and `Status=Implemented`
- roadmap and changelog entries match the actual state
- release work remains undone unless the user explicitly requested it
