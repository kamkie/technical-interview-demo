# Execution Guide For AI Agents

`ai/EXECUTION.md` explains how AI agents should execute an existing plan or implement a bounded plan milestone in this repository.

Use this file when the user asks to implement `ai/PLAN_*.md`, complete a plan milestone, or carry out planned work without releasing it yet.
Use `ai/WORKFLOW.md` when the user explicitly wants delegation or multi-agent execution. Use `ai/RELEASES.md` only after the implementation PR has been approved and merged onto `main`.

## Execution Goal

Execution in this repository means:

- implement the smallest spec-driven change that satisfies the target plan or milestone
- keep the plan, code, tests, docs, and validation state aligned as the work advances
- preserve the current public contract unless the plan explicitly changes it
- keep local implementation, validation, and integration-readiness work ahead of any remote push or PR creation
- stop before release unless the user explicitly asked for release work

## Before You Implement

Read these artifacts before editing:

- `AGENTS.md`
- `ai/PLAN.md`
- the target `ai/PLAN_*.md` file
- the governing tests, docs, HTTP examples, and source files named by the plan
- `README.md`, `src/docs/asciidoc/`, `src/test/resources/openapi/approved-openapi.json`, and `src/test/resources/http/` when the plan touches public behavior

Before writing code or docs:

- confirm the plan is decision-complete enough to execute without inventing product behavior
- confirm whether you are executing the whole plan or only a named milestone
- confirm which contract artifacts, benchmarks, or setup docs the plan says are affected
- identify any plan gaps that now block execution, and revise the plan before coding instead of filling those gaps with new product decisions

## Standard Single-Agent Flow

1. Re-read the target scope, locked decisions, and non-goals. Do not silently expand scope.
2. Update the governing spec artifact first when behavior is intentionally changing.
3. Implement the smallest code or documentation change that satisfies the updated spec.
4. Preserve current contract behavior unless the plan explicitly changes it.
5. Update all affected contract artifacts together when public behavior changes, and update the owning maintainer docs together when workflow guidance changes.
6. Keep execution state current as work lands:
   - update `CHANGELOG.md` under `## [Unreleased]` after each completed plan task or milestone
   - create a normal non-interactive commit after each completed plan task or milestone
   - update the plan's `Validation Results` section with what actually ran and what happened
7. Run the required validation before finishing.
8. Perform a final local review for correctness, contract alignment, and documentation drift before remote handoff.
9. If the user asked for remote collaboration, push the finished branch and create the PR only after steps 1 through 8 are complete. PR creation is the last execution step, not a substitute for local execution.
10. Stop after reporting execution status. Release work begins only after the approved PR has been merged onto `main`; if release is later requested, follow `ai/RELEASES.md` from merged `main`.

## Milestone-Only Execution

When the user asks for only one milestone:

- implement only the named milestone from the plan
- preserve the remaining plan structure for later execution
- do not start follow-on cleanup or later milestones implicitly
- record only the validation you actually ran for that milestone in `Validation Results`
- do not prepare or cut a release
- do not push or open a PR unless the user explicitly asked for that remote handoff for the completed milestone

## Contract And Artifact Rules

- public API changes require the full artifact set from `AGENTS.md`: implementation, tests, REST Docs, published docs, approved OpenAPI, HTTP examples, and `README.md` when applicable
- internal refactors should keep contract artifacts unchanged unless behavior actually changed
- setup or environment changes belong in `SETUP.md`
- roadmap-only changes belong in `ROADMAP.md`
- do not refresh the OpenAPI baseline unless the contract change was intentional and reviewed

## Validation Rules

Before finishing, run:

```powershell
.\gradlew.bat build
```

Also run `./gradlew gatlingBenchmark` when the plan changes:

- book list or search behavior
- localization lookup behavior
- OAuth or session startup behavior

Treat failing compatibility or benchmark checks as spec failures.

## Commit And Reporting Expectations

- keep commits aligned to completed plan tasks or milestones
- keep commit messages narrow and non-interactive
- if required validation cannot run, record that explicitly in the plan's `Validation Results` and in the final status report
- if the plan stops matching repo reality, revise the plan before continuing instead of improvising a new scope
- if a PR was not requested, report the work as locally complete or locally blocked rather than implying remote handoff happened

## Completion Criteria

Implementation work is complete when:

- the targeted plan scope is fully implemented
- required spec and contract artifacts are aligned
- the target plan's `Validation Results` reflects actual execution
- `CHANGELOG.md` under `## [Unreleased]` reflects the unreleased work
- `.\gradlew.bat build` passed
- any requested PR or push happened only after local execution was complete
- release work remains undone unless the user explicitly asked for it
