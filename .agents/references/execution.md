# Task And Milestone Execution Guide For AI Agents

`.agents/references/execution.md` owns ad hoc task execution, individual plan-milestone execution, and AI-created commit-message rules.
Use this file when the user asks for one bounded change, one named milestone from an active plan, or unplanned maintenance work that does not yet need a full plan.

Use `.agents/references/plan-execution.md` when the user asks to execute a whole active plan across milestones.
Use `.agents/references/workflow.md` only when branch, worktree, delegation, worker-log, or integration mechanics are needed.
Use `.agents/references/planning.md` when the task is not clear enough to execute without first creating or revising a plan.
Direct prompts and ad hoc user requests still use this guide unless they are read-only questions or are promoted into a concrete plan.

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
- `.agents/references/references-rules.md` when changing `.agents/references/*.md`
- `.agents/references/testing.md` for validation scope
- `.agents/references/reviews.md` before finalizing
- `.agents/references/workflow.md` when worktrees, delegation, worker logs, or integration handoff are in scope

## Execution Loop

For ad hoc tasks and single milestones:

1. Identify the behavior being changed and the spec or document that owns it.
2. Re-read any decision log or locked decision entries, non-goals, progress tracker row when present, and the milestone context field if a plan milestone is involved.
3. Update the governing spec first when behavior intentionally changes.
4. Make the smallest coherent implementation or documentation change.
5. Keep artifact routing aligned through `.agents/references/documentation.md`.
6. If existing AI guidance proves wrong, incomplete, contradictory, or requires an execution-time policy decision, classify the owner through `.agents/references/documentation.md`; update the owning guide in the same change, or record the blocker or follow-up in the active plan before continuing. For ad hoc work without a plan, stop and ask when the owning guide cannot be updated safely; do not rely on final-response memory.
7. Run the smallest sufficient validation from `.agents/references/testing.md`.
8. If validation fails, load `.agents/references/troubleshooting.md` before choosing the recovery path.
9. Review the diff using `.agents/references/reviews.md`; apply the security review activity when its triggers match.
10. Record validation evidence in the plan, worker log, or final response as appropriate.
11. If executing a plan milestone, update the milestone detail and `Progress Tracker` with status, commit checkpoint, validation result, and any blocker or skip note.
12. Commit every completed task or milestone that changed tracked files before handing off or starting unrelated work. Follow the rules in `## AI Commit Message Rules` below.

## AI Commit Message Rules

AI-created commits use Conventional Commits 1.0.0 style plus repository project metadata footers.

Use the `.gitmessage` file in the repository root as the authoritative template, rule set, and example source for all AI-created commits.

## Context Switching

Keep context efficient:

- prefer targeted searches and structure checks over opening every file in a package
- load package, guide, or reference context only when it is needed for the current checkpoint
- drop files that belonged only to a completed checkpoint
- summarize long or complex findings into the plan, worker log, validation notes, or final response instead of repeatedly reopening raw logs
- if contradictions, unstable assumptions, unjustified hedging, or repeated re-derivation appear, write a short current-state summary before continuing; put it in the active plan, worker log, validation notes, or final response as appropriate

## Milestone-Only Rules

When the user asks for only one milestone:

- implement only that milestone
- preserve the remaining plan structure for later execution
- update only the lifecycle, progress tracker, milestone detail, blocker table, and validation artifacts that correspond to the completed milestone
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
- tracked file changes are committed with the required AI commit-message format
- release work remains undone unless explicitly requested
