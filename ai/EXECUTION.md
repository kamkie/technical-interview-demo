# Execution Guide For AI Agents

`ai/EXECUTION.md` owns single-agent plan execution after a plan is approved.

Use this file when the user asks to implement `ai/PLAN_*.md`, complete a named milestone, or carry out planned work without releasing it yet.
Use `ai/WORKFLOW.md` only when the user explicitly wants delegation or worktrees. Use `ai/RELEASES.md` only after the approved implementation PR has been merged onto `main`.

## Execution Goal

Execution in this repository means:

- implement the smallest spec-driven change that satisfies the target plan
- keep plan state, docs, changelog, and validation aligned as work lands
- preserve the current public contract unless the plan intentionally changes it
- finish local implementation and validation before any push or PR handoff
- stop before release unless the user explicitly asked for release work

## Before You Implement

Read these before editing:

- `AGENTS.md`
- `ai/PLAN.md`
- the target `ai/PLAN_*.md`
- the focused guides the change needs, usually `ai/CODE_STYLE.md`, `ai/DOCUMENTATION.md`, `ai/TESTING.md`, and `ai/REVIEWS.md`
- the governing specs, docs, examples, and source files named by the plan

Before writing code or docs:

- confirm the plan is decision-complete enough to execute without inventing product behavior
- confirm whether you are executing the whole plan or only one milestone
- if execution uncovered a real plan gap, revise the plan before coding instead of filling the gap ad hoc

When commands depend on local environment variables:

- check the local `.env` file first when it exists
- in PowerShell, prefer dot-sourcing `./scripts/load-dotenv.ps1` before env-dependent commands
- use `.env.example` only as the template for expected variable names, not as proof of local values
- if `.env` is missing or incomplete, state the fallback you used
- never commit local machine-specific `.env` values unless the user explicitly asks for that

## Standard Single-Agent Flow

1. Re-read the target scope, locked decisions, and non-goals.
2. Update the governing spec first when behavior is intentionally changing.
3. Implement the smallest coherent code or documentation change that satisfies the updated spec.
4. Keep artifact routing aligned through `ai/DOCUMENTATION.md`.
5. Keep execution state current as work lands:
   - update the plan `Lifecycle` section so `Phase` and `Status` stay accurate
   - update `CHANGELOG.md` under `## [Unreleased]` after each completed task or milestone unless `ai/WORKFLOW.md` overrides that for delegated execution
   - create a normal non-interactive commit after each completed task or milestone
   - update the plan `Validation Results` section with what actually ran
6. Run the required validation from `ai/TESTING.md`.
7. Perform a final local review using `ai/REVIEWS.md`.
8. If execution is happening in a git worktree, push the finished branch and open the PR only after steps 1 through 7 are complete. Outside worktree-based execution, do that only if the user asked for remote collaboration.
9. Stop after reporting execution status. Release work begins only after the approved PR has been merged onto `main`.

## Milestone-Only Execution

When the user asks for only one milestone:

- implement only the named milestone
- preserve the remaining plan structure for later execution
- do not start later milestones or cleanup implicitly
- update the plan `Lifecycle` section only as far as the completed milestone justifies
- record only the validation actually run for that milestone in `Validation Results`
- do not prepare or cut a release
- if the milestone is being executed in a git worktree, push the finished branch and open the PR after local validation; otherwise do not push or open a PR unless the user explicitly asked for that remote handoff

## Guardrails

- use `ai/DOCUMENTATION.md` to choose which contract or maintainer artifacts must move
- use `ai/TESTING.md` to choose the required validation and any benchmark or compatibility extras
- if execution is delegated, follow `ai/WORKFLOW.md` for worker-specific plan, changelog, and progress-file deviations
- if required validation cannot run, record that explicitly in the plan and in the final status report
- if the plan stops matching repo reality, revise it before continuing instead of improvising new scope

## Completion Criteria

Execution work is complete when:

- the targeted plan scope is implemented
- the target plan `Lifecycle` reflects the final non-release execution state, typically `Phase=Integration` and `Status=Implemented`
- required specs and documentation artifacts are aligned
- the target plan `Validation Results` reflects actual execution
- unreleased history is recorded in the correct changelog artifact for the execution mode in use
- the required validation from `ai/TESTING.md` passed, or any blocker is explicitly recorded
- any required or requested push or PR happened only after local execution was complete
- release work remains undone unless the user explicitly asked for it
