# Planning Guide For AI Agents

`ai/PLAN.md` explains how AI agents should produce implementation plans in this repository.

Use this file when the user asks for a plan, milestone, execution document, milestone breakdown, or a detailed change strategy.
Do not use `ROADMAP.md` as a substitute for a real plan. `ROADMAP.md` is the roadmap. A plan is a self-contained handoff document for a concrete piece of work.

## Planning Goals

A good plan for this repository is:

- spec-driven before implementation-driven
- explicit about the behavior being changed
- grounded in current repo truth, not guesses
- narrow enough to preserve the demo nature of the project
- detailed enough that another agent can execute it without inventing missing decisions
- validation-heavy, with clear build, test, and contract checks

The application goals matter when planning:

- keep the codebase small, readable, and easy to reason about
- keep the public API stable unless the task explicitly changes it
- preserve the demo character of the project instead of over-engineering it
- treat tests, REST Docs, OpenAPI compatibility, and benchmark gates as part of the product contract

## Before You Plan

Before writing a plan, read the repository guidance and the relevant specs.
At minimum, inspect:

- `AGENTS.md`
- `README.md`
- `ROADMAP.md` if the work touches roadmap sequencing
- relevant tests under `src/test/java/`
- relevant docs under `src/docs/asciidoc/`
- `src/test/resources/openapi/approved-openapi.json` if API behavior may change
- relevant HTTP examples under `src/test/resources/http/`
- implementation code in `src/main/java/`

If the user referenced another document, ticket, PR, issue, API example, or web page, read it before planning.

## How To Plan

1. Establish the real change.
   Write down what behavior is changing, who uses it, and whether it is public API, internal behavior, setup, or roadmap-only work.

2. Find the governing spec artifacts.
   Identify which tests, docs, OpenAPI baseline, HTTP examples, README sections, or roadmap entries define the current behavior.

3. Research before asking.
   Search the repo and inspect likely source files before asking the user questions. Do not ask questions whose answers are already in the codebase, docs, configs, or tests.

4. Ask only high-value questions.
   Ask the user when ambiguity affects product intent, scope, compatibility, rollout, or tradeoffs. If you ask, present concrete options and recommend one.

5. Lock assumptions.
   If the user does not answer a non-blocking preference question, proceed with a reasonable default and record it explicitly in the plan as an assumption.

6. Keep the plan decision-complete.
   The implementer should not need to decide what files to touch, what behavior to preserve, what tests to add, or what validation proves completion.

7. Prefer the smallest coherent change.
   Favor direct Spring MVC, Spring Data, and `@Service`-level changes over new abstraction layers unless the user explicitly wants broader architecture work.

8. Call out better-engineering blockers.
   If the requested change is poorly framed because a more fundamental problem must be solved first, say so in the plan. Small prerequisite cleanup can be included as an early milestone. Large prerequisite work should be called out as a separate plan.

9. Make validation concrete.
   Every plan must explain exactly how the executor will prove correctness. Include repository-specific commands, tests, compatibility checks, and any manual verification steps.

## Required Planning Questions

A plan is not ready until it answers all of these:

- What behavior is changing?
- Why is the change needed?
- What is explicitly out of scope?
- Which spec artifacts currently define this behavior?
- Which source files will likely change?
- What compatibility promises must be preserved?
- What edge cases or failure modes matter?
- What validation proves the work is complete?
- Does the task require docs, OpenAPI, HTTP example, or benchmark updates?
- Is there a smaller or cleaner way to achieve the same goal?

## Repo-Specific Rules For Planning

### Public API changes

If the plan changes request handling, response shape, documented errors, security requirements, pagination, filtering, or endpoint behavior, the plan must account for all affected artifacts:

- implementation code
- integration tests
- REST Docs tests
- Asciidoc pages when applicable
- approved OpenAPI baseline after intentional contract review
- HTTP examples under `src/test/resources/http/`
- `README.md` if the supported contract changed

The plan must also state whether benchmark reruns are required. In this repository, rerun `./scripts/run-phase-9-benchmarks.ps1` when changing:

- book list or search behavior
- localization lookup behavior
- OAuth or session startup behavior

### Internal refactors

If the work is a refactor with no contract change, the plan should preserve existing specs and avoid unnecessary doc or OpenAPI edits.

### Setup or environment changes

If the work changes onboarding, tools, Java, Docker, formatter setup, or local runbooks, update `SETUP.md`. Do not move setup detail into `AGENTS.md` or `ai/PLAN.md`.

### Roadmap work

If the task is only reprioritization or scope management, update `ROADMAP.md`. Do not write a fake execution plan when the work is not ready to implement.

## Plan Output Format

When the user asks for a concrete execution plan, create a new file under `ai/` named `PLAN_<topic>.md`.
Use short lowercase topic names with underscores, for example `PLAN_book_api_error_shape.md`.

Use this structure:

```md
# Plan: <title>

## Summary
- What will change
- Why it matters
- How success will be measured

## Scope
- In scope
- Out of scope

## Current State
- Current behavior
- Current constraints
- Relevant existing specs and code

## Locked Decisions And Assumptions
- User decisions
- Planning assumptions that the executor should not revisit

## Affected Artifacts
- Tests
- Docs
- OpenAPI
- HTTP examples
- Source files
- Build or benchmark checks

## Execution Milestones
### Milestone 1: <name>
- goal
- files to update
- behavior to preserve
- exact deliverables

### Milestone 2: <name>
- goal
- files to update
- exact deliverables

## Edge Cases And Failure Modes
- important error cases
- compatibility risks
- migration or rollout concerns

## Validation Plan
- commands to run
- tests to add or update
- docs or contract checks
- manual verification steps

## Better Engineering Notes
- prerequisite cleanup included in the plan
- deferred follow-up work that should not be hidden

## Validation Results
- To be filled in during execution

## User Validation
- Short walkthrough for the user to verify the delivered behavior
```

## What Good Planning Looks Like In This Repo

Good plans in this repository usually:

- start from tests and contract docs instead of starting from controllers
- name exact files or packages instead of vague areas
- distinguish public contract work from internal cleanup
- say whether OpenAPI baseline refresh is expected or forbidden
- say whether HTTP example files must change
- state whether `README.md` changes are required
- include the final `./gradlew.bat build` step
- include benchmark reruns when search, localization lookup, or session startup changes

Poor plans in this repository usually:

- describe implementation without identifying the spec first
- change public behavior without listing docs and OpenAPI consequences
- propose new abstractions that fight the demo scope
- use vague language like "update tests as needed" instead of naming which tests must change
- forget manual user verification for visible API behavior

## Example Planning Frames

### Example 1: Public API change

Request: add a new supported filter to `GET /api/books`.

A good plan would:

- identify the current `Book` API tests and search behavior specs first
- define request parameter semantics, validation rules, and sort compatibility before coding
- update integration tests, REST Docs tests, Asciidoc, OpenAPI baseline, HTTP examples, and `README.md` if the filter is part of the supported contract
- rerun the benchmark script because book list/search behavior changed
- finish with `./gradlew.bat build`

### Example 2: Internal cleanup

Request: move duplicate mapping logic from a controller into a service helper without changing API behavior.

A good plan would:

- treat the current tests as the contract to preserve
- keep OpenAPI, REST Docs, and HTTP examples unchanged unless behavior really changes
- list the exact classes to refactor
- validate with the existing test suite and `./gradlew.bat build`

### Example 3: Release-readiness planning

Request: standardize `401` and `403` responses before `1.0`.

A good plan would:

- identify where current behavior is specified or missing
- decide whether the change alters the supported public contract
- name the error-handling tests, documentation, OpenAPI, and README sections that must move together
- call out compatibility risk because clients may already depend on current response shapes
- define user-visible verification steps against the affected endpoints

## Execution Expectations For Agents

If the user later asks you to execute a plan:

1. Re-read `ai/PLAN.md`, `AGENTS.md`, and the target `ai/PLAN_<topic>.md` file.
2. Implement the smallest change that satisfies the plan.
3. After each completed task in the plan, update the `## [Unreleased]` section in `CHANGELOG.md` with the newly completed work that should be reflected in the upcoming release.
4. After each completed task in the plan, create a commit for that finished work. Do not wait and batch the entire plan into one final commit.
5. If execution happens in a git worktree or another branch, integrate the completed changes onto `main` before calling the task done.
6. Update the `Validation Results` section in the plan document with what you actually ran and what passed or failed.
7. After the whole plan is implemented and validated, make a release by following `ai/RELEASES.md`.
8. If execution discovers a missing decision, stop and ask unless the plan already recorded an explicit fallback assumption.
9. If execution discovers a better-engineering prerequisite that invalidates the plan, stop and explain why the plan must be revised.

## Final Planning Check

Before presenting a plan to the user, verify that:

- the plan is self-contained
- the plan names the governing specs
- the plan separates scope from non-goals
- the plan names the likely files to change
- the plan includes repo-specific validation
- the plan respects the demo scope of the application
- the plan does not hide compatibility or benchmark consequences
