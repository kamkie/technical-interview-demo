# Execution Guide For AI Agents

`ai/EXECUTION.md` owns the common milestone execution loop after a plan is approved.

Use this file when the user asks to implement `ai/PLAN_*.md`, complete a named milestone, or carry out planned work without releasing it yet.
Use `ai/WORKFLOW.md` to choose the execution mode and branch/worktree topology.
Use `ai/RELEASES.md` only after the approved implementation PR has been merged onto `main`.

## Execution Goal

Execution in this repository means:

- implement the smallest spec-driven change that satisfies the target plan
- preserve the current public contract unless the plan intentionally changes it
- keep plan state, changelog artifacts, worker logs, docs, and validation aligned as work lands
- complete work as milestone checkpoints that can each be reviewed and committed
- finish local implementation and validation before any push or PR handoff
- stop before release unless the user explicitly asked for release work

## Before You Implement

Read these before editing:

- `AGENTS.md`
- `ai/PLAN.md`
- the target `ai/PLAN_*.md`
- `ai/WORKFLOW.md` to confirm the execution mode
- the focused guides the change needs, usually `ai/CODE_STYLE.md`, `ai/DOCUMENTATION.md`, `ai/TESTING.md`, and `ai/REVIEWS.md`
- the governing specs, docs, examples, and source files named by the plan

Before writing code or docs:

- confirm the plan is decision-complete enough to execute without inventing product behavior
- confirm whether you are executing the whole plan or only one milestone
- confirm which mode you are in: `Single Branch`, `Shared Plan`, or `Parallel Plans`
- if execution uncovered a real plan gap, revise the plan before coding instead of filling the gap ad hoc

When commands depend on local environment variables:

- check the local `.env` file first when it exists
- in PowerShell, prefer dot-sourcing `./scripts/load-dotenv.ps1` before env-dependent commands
- use `.env.example` only as the template for expected variable names, not as proof of local values
- if `.env` is missing or incomplete, state the fallback you used
- never commit local machine-specific `.env` values unless the user explicitly asks for that

## Common Milestone Loop

Every execution mode follows the same milestone loop.
A milestone is not done until the implementation, validation, tracking artifacts, and commit are all in place.

1. Re-read the target scope, locked decisions, non-goals, and the current milestone checkpoint.
2. Update the governing spec first when behavior is intentionally changing.
3. Implement the smallest coherent code or documentation change that satisfies that milestone.
4. Keep artifact routing aligned through `ai/DOCUMENTATION.md`.
5. Run the milestone validation named by the plan, then any broader validation required by `ai/TESTING.md`.
6. Update the tracking artifacts for the active mode:
   - `Single Branch`: update the canonical plan `Lifecycle`, `Validation Results`, and `CHANGELOG.md`
   - `Shared Plan` worker: update only the worker log and any worker-owned files; leave shared files to the coordinator
   - `Shared Plan` coordinator: integrate accepted worker output into the canonical plan and `CHANGELOG.md`
   - `Parallel Plans` worker: update the owned plan file, the private `CHANGELOG_<topic>.md`, and the worker log
7. Create a normal non-interactive commit for the completed milestone.
8. Repeat for the next milestone or stop and report status if the requested scope is complete.

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
- the correct changelog artifact for the active mode
- the correct plan or worker-log update for the active mode

## Mode-Specific Tracking Summary

`ai/WORKFLOW.md` owns the full mode rules.
Use this section as the quick execution summary.

### Single Branch

- update the canonical `ai/PLAN_*.md`
- update `CHANGELOG.md` under `## [Unreleased]`
- commit after each completed milestone

### Shared Plan

- workers do not update shared files
- workers update the temporary worker log after each completed milestone and commit it with the worker-owned changes
- the coordinator integrates accepted worker output into the canonical plan and `CHANGELOG.md`, then commits the integration checkpoint

### Parallel Plans

- each worker owns its own `ai/PLAN_*.md` file or explicitly grouped plan set
- each worker maintains a private `CHANGELOG_<topic>.md` copy instead of editing `CHANGELOG.md`
- each worker updates the temporary worker log after each completed milestone and commits it with the milestone

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
- follow `ai/WORKFLOW.md` whenever the mode changes who can edit the plan file, `CHANGELOG.md`, or other shared artifacts
- if required validation cannot run, record that explicitly in the plan or worker log and in the final status report
- if the plan stops matching repo reality, revise it before continuing instead of improvising new scope
- if the active mode is `Shared Plan`, do not edit coordinator-owned shared files from a worker branch
- if the active mode is `Parallel Plans`, keep changelog edits private to the worker branch until the coordinator is ready to fold them back into `CHANGELOG.md`

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
