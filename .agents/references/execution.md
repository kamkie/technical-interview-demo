# Task And Milestone Execution Guide For AI Agents

`.agents/references/execution.md` owns ad hoc task execution and individual plan-milestone execution.
Use this file when the user asks for one bounded change, one named milestone from an active plan, or unplanned maintenance work that does not yet need a full plan.

Use `.agents/references/plan-execution.md` when the user asks to execute a whole active plan across milestones.
Use `.agents/references/workflow.md` only when branch, worktree, delegation, worker-log, or integration mechanics are needed.
Use `.agents/references/planning.md` when the task is not clear enough to execute without first creating or revising a plan.

## Task Gate

Before editing, decide whether the work can stay as a bounded task.
Promote the work into a plan when any of these are true:

- product intent, compatibility, rollout, or acceptance criteria are unclear
- public API behavior or published contract artifacts will change
- the work needs multiple commit-sized milestones
- several people or agents may need to own disjoint slices
- roadmap state must track the work before implementation
- validation cannot be expressed as a small checkpoint

If the task stays ad hoc, write down the behavior being changed, the governing spec or doc, and the validation target in your own working context before editing.

## Minimal Read Set

Start with:

- `AGENTS.md`
- this file
- the user's request
- the specific files or specs that govern the behavior

Add only what the task needs:

- the target active plan when executing one named milestone
- `.agents/references/documentation.md` when artifact routing or AI-document maintenance is involved
- `.agents/references/testing.md` for validation scope
- `.agents/references/reviews.md` before finalizing
- `.agents/references/workflow.md` when worktrees, delegation, worker logs, or integration handoff are in scope

## Execution Loop

For ad hoc tasks and single milestones:

1. Identify the behavior being changed and the spec or document that owns it.
2. Re-read any locked decisions, non-goals, and the milestone context field if a plan milestone is involved.
3. Update the governing spec first when behavior intentionally changes.
4. Make the smallest coherent implementation or documentation change.
5. Keep artifact routing aligned through `.agents/references/documentation.md`.
6. Run the smallest sufficient validation from `.agents/references/testing.md`.
7. If validation fails, load `.agents/references/troubleshooting.md` before choosing the recovery path.
8. Review the diff using `.agents/references/reviews.md`; apply the security review activity when its triggers match.
9. Record validation evidence in the plan, worker log, or final response as appropriate.
10. Commit when the task or milestone is complete and the active workflow expects a checkpoint commit. Use the Conventional Commits style from `CONTRIBUTING.md`, and put plan paths, milestone names, task titles, validation context, or user-request context in the optional body or a `Refs:` footer when that context matters.

## Context Switching

Keep context efficient:

- load package, guide, or reference context only when it is needed for the current checkpoint
- drop files that belonged only to a completed checkpoint
- summarize long findings into the plan, worker log, or final response instead of repeatedly reopening raw logs
- if assumptions become unstable, stop and clarify or write a short checkpoint before continuing

## Milestone-Only Rules

When the user asks for only one milestone:

- implement only that milestone
- preserve the remaining plan structure for later execution
- update only the tracking artifacts that correspond to the completed milestone
- record only the validation actually run
- do not start later milestones or cleanup implicitly
- do not prepare or cut a release

If the milestone exposes a real plan gap, revise the active plan before coding beyond the approved scope.

## Guardrails

- public behavior changes must keep executable specs and published contract artifacts aligned
- internal refactors should preserve existing specs without contract churn
- setup and environment changes route through `SETUP.md` and, when AI command guidance changes, `.agents/references/environment-quick-ref.md`
- AI-guidance changes route to the owning `.agents/references/` guide through `.agents/references/documentation.md`
- required validation that cannot run must be recorded explicitly
- never edit files reserved to another worker or coordinator without first resolving ownership through `.agents/references/workflow.md`

## Completion

A task or single milestone is complete when:

- the requested behavior or documentation change is in place
- specs and docs agree with the implementation
- validation evidence is recorded
- review found no unresolved blocking risk
- any active plan or roadmap tracking that needed to move has been updated
- release work remains undone unless explicitly requested
