# Planning Guide For AI Agents

`ai/PLAN.md` explains how AI agents should produce implementation plans in this repository.

Use this file when the user asks for a plan, milestone, execution document, milestone breakdown, or a detailed change strategy.
Do not use `ROADMAP.md` as a substitute for a real plan. `ROADMAP.md` is the roadmap. A plan is a self-contained handoff document for a concrete piece of work.
Use `ai/EXECUTION.md` for the common execution loop once a plan is approved.
Use `ai/WORKFLOW.md` for branch, worktree, coordinator, and worker execution modes.
Use `ai/DOCUMENTATION.md` for artifact ownership and `ai/TESTING.md` for validation scope instead of restating those rules in the plan.

## Lifecycle Metadata

Every `ai/PLAN_*.md` file should start with a small `Lifecycle` section immediately below the title.
Use a compact table so the current state is obvious at a glance:

```md
## Lifecycle
| Field | Value |
| --- | --- |
| Phase | Planning |
| Status | Draft |
```

Prefer this controlled vocabulary unless the user explicitly asks for another scheme.

Recommended `Phase` values:

- `Discovery`: repo research, framing, or pre-planning intake is still underway
- `Planning`: the plan is being written, reviewed, or finalized
- `Implementation`: approved work is actively being built
- `Integration`: implementation is done and the work is being validated, merged, or prepared for release
- `Closed`: no further active execution is expected for this plan

Recommended `Status` values:

- `Draft`: the plan is still being shaped
- `Needs Input`: unanswered user questions still block a decision-complete plan
- `Ready`: the plan is decision-complete and ready for execution
- `In Progress`: implementation or integration work is actively happening
- `Blocked`: execution cannot continue until an external blocker is resolved
- `Implemented`: implementation and validation are complete, but release or archive cleanup is still pending
- `Released`: the completed plan has shipped and been cleaned up

Use `Phase` for the coarse lifecycle stage and `Status` for the immediate execution state.
Keep the lifecycle block current as the plan moves from planning through implementation, integration, and eventual closure.

## Planning Goals

A good plan for this repository is:

- spec-driven before implementation-driven
- explicit about the behavior being changed
- grounded in current repo truth, not guesses
- narrow enough to preserve the demo nature of the project
- detailed enough that another agent can execute it without inventing missing decisions
- structured as milestone checkpoints that can be implemented, validated, and committed cleanly
- validation-heavy, with clear build, test, and contract checks

The application goals matter when planning:

- keep the codebase small, readable, and easy to reason about
- keep the public API stable unless the task explicitly changes it
- preserve the demo character of the project instead of over-engineering it
- treat tests, REST Docs, OpenAPI compatibility, and benchmark gates as part of the product contract

Plans in this repository must be executable in one of the three modes defined by `ai/WORKFLOW.md`:

- `Single Branch`
- `Shared Plan`
- `Parallel Plans`

That means every plan must be milestone-driven enough for one milestone to become one reviewable execution checkpoint.
When a plan might fan out, it must also make worker-safe ownership and shared-file boundaries explicit.

## Before You Plan

Before writing a plan, read the repository guidance and the relevant specs.
At minimum, inspect:

- `AGENTS.md`
- `README.md`
- the owning AI guide when the work changes durable repo guidance, usually `ai/ARCHITECTURE.md`, `ai/DESIGN.md`, or `ai/LEARNINGS.md`
- `ROADMAP.md` if the work touches roadmap sequencing
- relevant tests under `src/test/java/`
- relevant docs under `src/docs/asciidoc/`
- `src/test/resources/openapi/approved-openapi.json` if API behavior may change
- relevant HTTP examples under `src/test/resources/http/`
- implementation code in `src/main/java/`

If the user referenced another document, ticket, PR, issue, API example, or web page, read it before planning.
If the request leaves material gaps in scope, compatibility, rollout, acceptance criteria, or validation, resolve what repo truth can answer first and then ask targeted clarification questions before finalizing the plan. Do not hide material requirement holes inside silent assumptions.

## Milestone Design Rules

Every execution milestone should be a commit-sized checkpoint.
Design milestones so an executor can finish the milestone, run the planned validation, update the mode-specific tracking artifacts, and commit before moving on.

Use these rules:

- keep milestone scope coherent: one user-visible behavior slice, one refactor checkpoint, or one documentation/contract checkpoint
- include the spec, implementation, documentation, and validation work needed for that checkpoint instead of scattering one behavior across many vague milestones
- name exact files or packages when possible
- mark which files remain coordinator-owned if the plan is later executed as `Shared Plan`
- avoid milestones that require multiple workers to touch the same file set at the same time
- choose stable topic names when a plan might later become a standalone `Parallel Plans` branch with its own temporary changelog file

## How To Plan

1. Establish the real change.
   Write down what behavior is changing, who uses it, and whether it is public API, internal behavior, setup, or roadmap-only work.

2. Find the governing spec artifacts.
   Identify which tests, docs, OpenAPI baseline, HTTP examples, README sections, or roadmap entries define the current behavior.

3. Research before asking.
   Search the repo and inspect likely source files before asking the user questions. Do not ask questions whose answers are already in the codebase, docs, configs, or tests.

4. Ask only high-value questions.
   Ask the user when ambiguity affects product intent, scope, compatibility, rollout, acceptance criteria, validation, or other material tradeoffs. If you ask, present concrete options and recommend one.

5. Record requirement gaps explicitly.
   If a material question is still open after repo research, record what is missing, why it matters, and whether planning is blocked pending user input.

6. Lock assumptions carefully.
   If the user does not answer a non-blocking preference question, proceed with a reasonable default and record it explicitly in the plan as a fallback assumption. Do not convert missing product decisions about scope, compatibility, rollout, acceptance criteria, or validation into silent assumptions.

7. Keep the plan decision-complete.
   The implementer should not need to decide what files to touch, what behavior to preserve, what tests to add, what validation proves completion, or how milestone boundaries map to execution checkpoints.

8. Prefer the smallest coherent change.
   Favor direct Spring MVC, Spring Data, and `@Service`-level changes over new abstraction layers unless the user explicitly wants broader architecture work.

9. Choose the default execution mode.
   Default to `Single Branch`. Upgrade to `Shared Plan` only when one plan can be split into disjoint worker-owned slices. Upgrade to `Parallel Plans` only when separate plan files can move independently with their own validation and temporary changelog copies.

10. Mark shared and private artifacts.
    If the work could fan out, state which files stay coordinator-owned, which files can be worker-owned, and which artifacts must stay private to a worker branch until integration.

11. Call out better-engineering blockers.
    If the requested change is poorly framed because a more fundamental problem must be solved first, say so in the plan. Small prerequisite cleanup can be included as an early milestone. Large prerequisite work should be called out as a separate plan.

12. Make validation concrete.
    Every plan must explain exactly how the executor will prove correctness. Use `ai/TESTING.md` for the required commands and extra gates.

## Required Planning Questions

A plan is not ready until it answers all of these:

- What behavior is changing?
- Why is the change needed?
- What is explicitly out of scope?
- Which spec artifacts currently define this behavior?
- Which source files will likely change?
- What compatibility promises must be preserved?
- What edge cases or failure modes matter?
- What requirement gaps still need user input, and which of them block planning?
- Which execution mode is the default, and why?
- If the work fans out, which files stay coordinator-owned or otherwise shared?
- Are the milestones small enough to be validated and committed one checkpoint at a time?
- What validation proves the work is complete?
- Which documentation or contract artifacts must move according to `ai/DOCUMENTATION.md`?
- Is there a smaller or cleaner way to achieve the same goal?

## Repo-Specific Rules For Planning

### Public API changes

If the plan changes request handling, response shape, documented errors, security requirements, pagination, filtering, or endpoint behavior, the plan must name all affected contract artifacts required by `AGENTS.md` and routed through `ai/DOCUMENTATION.md`.
The plan must also state whether extra validation from `ai/TESTING.md` is required, especially benchmark reruns for book list/search, localization lookup, or OAuth/session startup changes.

### Durable AI guidance changes

If the work changes a cross-cutting architectural or product convention that should remain true after the task-specific plan is archived, the plan must name the owning AI guide update explicitly.
Examples include persistence or serialization conventions, package ownership changes, and durable design or engineering lessons.
Do not treat the temporary `ai/PLAN_*.md` file as a substitute for updating `ai/ARCHITECTURE.md`, `ai/DESIGN.md`, or `ai/LEARNINGS.md` when one of those guides is the durable owner.

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

## Lifecycle
| Field | Value |
| --- | --- |
| Phase | Planning |
| Status | Draft |

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

## Requirement Gaps And Open Questions
- Material questions still requiring user input
- Why each gap matters
- Whether planning is blocked or what fallback applies if the user does not answer

## Locked Decisions And Assumptions
- User decisions
- Requirement gaps resolved from repo truth
- Fallback assumptions that the executor should not revisit

## Execution Mode Fit
- Recommended default mode: `Single Branch`, `Shared Plan`, or `Parallel Plans`
- Why that mode fits best
- Coordinator-owned or otherwise shared files if the work fans out
- Candidate worker boundaries or plan splits if later delegation becomes necessary

## Affected Artifacts
- Tests
- Docs
- OpenAPI
- HTTP examples
- Source files
- Owning AI guide updates when durable repo guidance changes
- Build or benchmark checks

## Execution Milestones
### Milestone 1: <name>
- goal
- owned files or packages
- shared files that a `Shared Plan` worker must leave to the coordinator
- behavior to preserve
- exact deliverables
- validation checkpoint
- commit checkpoint

### Milestone 2: <name>
- goal
- owned files or packages
- shared files if any
- exact deliverables
- validation checkpoint
- commit checkpoint

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
- Worker logs may hold temporary per-milestone detail until the coordinator integrates it when `ai/WORKFLOW.md` says so

## User Validation
- Short walkthrough for the user to verify the delivered behavior
```

## What Good Planning Looks Like In This Repo

Good plans in this repository usually:

- start from tests and contract docs instead of starting from controllers
- include a simple lifecycle block that makes the current phase and execution status obvious
- name exact files or packages instead of vague areas
- distinguish public contract work from internal cleanup
- make material requirement gaps explicit instead of silently guessing
- point to `ai/DOCUMENTATION.md` for artifact ownership instead of improvising file routing
- point to `ai/TESTING.md` for required validation instead of hand-waving about tests
- define milestone checkpoints that can be implemented and committed cleanly
- call out shared-file boundaries early if worker fanout is realistic
- include manual user verification for visible behavior

Poor plans in this repository usually:

- describe implementation without identifying the spec first
- bury unresolved scope, compatibility, rollout, acceptance-criteria, or validation questions inside vague assumptions
- change public behavior without naming the contract and documentation consequences
- propose new abstractions that fight the demo scope
- use vague language like "update tests as needed" instead of naming which tests must change
- define milestones too vaguely to support commit-after-milestone execution
- leave shared-file ownership implicit when the work could be delegated

## Example Planning Frames

### Example 1: Public API change

Request: add a new supported filter to `GET /api/books`.

A good plan would:

- identify the current `Book` API tests and search behavior specs first
- define request parameter semantics, validation rules, and sort compatibility before coding
- name the contract artifacts that must move together through `ai/DOCUMENTATION.md`
- call out any benchmark rerun required by `ai/TESTING.md`
- make each milestone a real checkpoint that can be committed after validation

### Example 2: Internal cleanup

Request: move duplicate mapping logic from a controller into a service helper without changing API behavior.

A good plan would:

- treat the current tests as the contract to preserve
- keep OpenAPI, REST Docs, and HTTP examples unchanged unless behavior really changes
- list the exact classes to refactor
- validate with the existing test suite and standard repository checks
- stay small enough that `Single Branch` remains the obvious execution mode

### Example 3: Shared-plan refactor

Request: refactor one subsystem using a single approved plan and several workers.

A good plan would:

- keep one canonical `ai/PLAN_*.md` as the source of truth
- name which files stay coordinator-owned, at minimum the canonical plan file and `CHANGELOG.md`
- split worker ownership by package, test class, or other defensible file boundary
- make every worker milestone a standalone commit checkpoint with explicit validation
- leave shared-file integration to the coordinator instead of asking workers to improvise it

## Execution Hand-Off

Once a plan is approved, execution belongs in:

- `ai/EXECUTION.md` for the common milestone execution loop
- `ai/WORKFLOW.md` for `Single Branch`, `Shared Plan`, and `Parallel Plans` orchestration
- `ai/RELEASES.md` for the release step after implementation is complete

Keep `ai/PLAN.md` focused on plan quality and handoff completeness rather than repeating execution mechanics.

## Final Planning Check

Before presenting a plan to the user, verify that:

- the lifecycle block is present near the top and uses a clear phase/status pair
- the plan is self-contained
- the plan names the governing specs
- the plan separates scope from non-goals
- the plan names the likely files to change
- the plan records any remaining requirement gaps and fallback assumptions explicitly
- the plan identifies the default execution mode and any shared-file boundaries that matter
- the milestones are specific enough to validate and commit one checkpoint at a time
- the plan includes repo-specific validation
- the plan respects the demo scope of the application
- the plan does not hide compatibility or benchmark consequences
