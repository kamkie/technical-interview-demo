# Plan: <title>

## Lifecycle
| Status | Current |
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
