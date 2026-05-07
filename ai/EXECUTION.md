# Execution Guide For AI Agents

`ai/EXECUTION.md` owns the common milestone execution loop after a plan is approved.

Use this file when the user asks to implement `ai/PLAN_*.md`, complete a named milestone, or carry out planned work without releasing it yet.
Use `ai/WORKFLOW.md` to choose the execution mode and branch/worktree topology.
Use `ai/RELEASES.md` only for explicit release work after implementation is integrated; release preconditions live there.

## Execution Goal

Execution in this repository means:

- implement the smallest spec-driven change that satisfies the target plan
- preserve the current public contract unless the plan intentionally changes it
- keep specs, docs, validation, and mode-specific tracking artifacts aligned as work lands
- complete work as milestone checkpoints that can each be reviewed and committed
- finish local implementation and validation before any push or PR handoff
- leave release sequencing to `ai/RELEASES.md` unless explicit release work is in scope after integration

## Before You Implement

Start from the lifecycle owner map in `AGENTS.md` and keep the execution read set small:

- `AGENTS.md`
- `ai/EXECUTION.md`
- the target `ai/PLAN_*.md`, when executing planned work
- the user request or approved source artifact, when executing ad hoc documentation or maintenance work
- the governing specs, docs, examples, and source files named by the task

Load additional owner guides only when the selected mode, changed files, validation scope, review lens, or documentation routing requires them.
`ai/DOCUMENTATION.md` owns artifact routing, `ai/TESTING.md` owns validation scope, and `ai/WORKFLOW.md` owns branch, worktree, delegation, and integration mechanics.

Before writing code or docs:

- confirm the plan is decision-complete enough to execute without inventing product behavior
- confirm whether you are executing the whole plan or only one milestone
- confirm which mode you are in: `Linear Plan`, `Single-Plan Fanout`, or `Multi-Plan Fanout`
- if execution uncovered a real plan gap, revise the plan before coding instead of filling the gap ad hoc

When local Gradle commands are needed, use `ai/ENVIRONMENT_QUICK_REF.md` for wrapper syntax and behavior.

## Common Milestone Loop

Every execution mode follows the same milestone loop.
A milestone is not done until the implementation, validation, tracking artifacts, and commit are all in place.

1. Re-read the target scope, in-scope outcome, locked decisions, non-goals, the current milestone checkpoint, and that milestone's `context required before execution` field; load only the extra context named there before starting the milestone.
2. Update the governing spec first when behavior is intentionally changing.
3. Implement the smallest coherent code or documentation change that satisfies that milestone.
4. Keep artifact routing aligned through `ai/DOCUMENTATION.md`.
5. Run the milestone validation named by the plan, then any broader validation required by `ai/TESTING.md`; if validation fails, load `ai/references/TROUBLESHOOTING.md` before choosing the recovery path.
6. Review the validated diff using the priority order in `ai/REVIEWS.md`; apply the security review lens only when that guide's security triggers apply.
7. Re-check the plan's in-scope outcome, locked decisions, and non-goals before claiming the checkpoint is done or moving to the next milestone. Record meaningful pivots as plan amendments, worker-log updates, or validation notes before continuing.
8. Update the tracking artifacts required by the active mode in `ai/WORKFLOW.md`.
9. Create a normal non-interactive commit for the completed milestone.
10. Repeat for the next milestone or stop and report status if the requested scope is complete.

## Milestone Commit Rules

Commit discipline is mandatory in all execution modes.

- create at least one commit after each completed milestone
- do not defer milestone commits until the end of the plan
- include the milestone's required tracking artifact updates in the same checkpoint that marks the milestone complete
- if a milestone is blocked or only partially implemented, record the blocker but do not mark the milestone complete
- if a milestone needs follow-up fixes before the next milestone starts, finish them before claiming the checkpoint is done

The expected milestone checkpoint contents are:

- implementation or documentation changes for that milestone
- updated validation evidence for that milestone
- the plan, changelog, or worker-log updates required by the active workflow mode

## Tracking Artifact Ownership

`ai/WORKFLOW.md` owns which files are editable in each mode.
During execution, use that file to decide whether validation evidence and changelog text belong in the canonical plan, the canonical `CHANGELOG.md`, a worker log, or a private `CHANGELOG_<topic>.md`.

Do not duplicate those mode rules in plans or prompts.
Name the active mode and then update the artifacts that mode owns.

## Milestone-Only Execution

When the user asks for only one milestone:

- implement only the named milestone
- preserve the remaining plan structure for later execution
- do not start later milestones or cleanup implicitly
- update only the tracking artifacts that correspond to the completed milestone
- record only the validation actually run for that milestone
- still create the milestone commit before stopping
- do not prepare or cut a release
- if the milestone is being executed in a git worktree, push the finished branch and open the PR after local validation only when `ai/WORKFLOW.md` or the user explicitly requires that remote handoff

## Guardrails

- use `ai/DOCUMENTATION.md` to choose which contract or maintainer artifacts must move
- use `ai/TESTING.md` to choose the required validation and any benchmark or compatibility extras
- evaluate the in-flight learning triggers in `ai/LEARNINGS.md` when a task uncovers a recurring repo-wide lesson
- follow `ai/WORKFLOW.md` whenever the mode changes who can edit the plan file, changelog files, worker logs, or other shared artifacts
- if required validation cannot run, record that explicitly in the plan or worker log and in the final status report
- if the plan stops matching repo reality, revise it before continuing instead of improvising new scope
- do not edit files reserved to the coordinator or another worker by the active workflow mode

## Completion Criteria

Execution work is complete when:

- the targeted plan scope is implemented
- every completed milestone has a corresponding commit checkpoint
- the target plan `Lifecycle` reflects the final non-release execution state, typically `Phase=Integration` and `Status=Implemented`
- required specs and documentation artifacts are aligned
- the plan `Validation Results` or worker log reflects actual execution
- unreleased history is recorded in the correct changelog artifact for the execution mode in use
- the required validation from `ai/TESTING.md` passed, or any blocker is explicitly recorded
- any required or requested push or PR happened only after local execution was complete
- release work remains undone unless the user explicitly asked for it
