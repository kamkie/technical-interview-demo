# Project Roadmap

This file tracks active and upcoming work for the technical-interview-demo Spring Boot application.
Keep it focused on work that is still planned, selected, in progress, or intentionally deferred.
Released history belongs in `CHANGELOG.md`.

## Current Project State

| Field | Current |
| --- | --- |
| Release Phase | Stable `v2.0.1` |
| Breaking Change Policy | Disallowed on the stable `2.x` line unless a later major-version plan explicitly changes it |
| Next Target Version | Not selected; default to `v2.0.2` for maintenance fixes or `v2.1.0` for backward-compatible feature work |
| Latest Stable Release | `v2.0.1` released on 2026-05-09 from the code review issue fixes plan archived in `.agents/archive/PLAN_code_review_issue_fixes.md` |
| Immediate Next Action | Review implemented `.agents/plans/PLAN_multi_agent_workflow_consolidation.md`; release work remains unrequested. |

## Roadmap Rules

- Keep only active, planned, selected, or intentionally deferred work here.
- Use `Status` values instead of checkbox semantics.
- Remove work once it is released, intentionally dropped, or no longer active.
- Keep detailed active milestones, validation notes, and implementation history in `.agents/plans/PLAN_*.md`, not in this file; released plan history belongs under `.agents/archive/`.

| Status | Meaning |
| --- | --- |
| Candidate | Rough work that needs refinement before planning |
| Planned | Known upcoming work, not yet selected for execution |
| Selected | Approved for current planning or development |
| In Progress | Execution or validation is underway |
| Implemented | Execution is done but integration, release, or cleanup still keeps the item active |
| Deferred | Intentionally postponed until the named trigger |

## Active Release Track

| Status | Workstream | Plan Or Artifact | Notes |
| --- | --- | --- | --- |

## Planned Work

Use this section for plan-backed work that is ready but not selected as the immediate release-track action.

| Status | Workstream | Plan Or Artifact | Notes |
| --- | --- | --- | --- |
| Implemented | Multi-agent workflow consolidation | `.agents/plans/PLAN_multi_agent_workflow_consolidation.md` | Workflow guidance is consolidated in `.agents/references/workflow.md`, live routing uses `.agents/context/*` workflow state, and release/archive cleanup remains out of scope until requested |
| Implemented | Frontend AI contract | `.agents/plans/PLAN_frontend_ai_contract.md` | Backend-owned source contract is implemented; external frontend-repo copy deferred until destination and instruction path are known |

## Intake

1. introduce adr to pre-planing workflow https://adr.github.io or https://github.com/adr/madr or https://github.com/architecture-decision-record/architecture-decision-record
2. redo workflow and template for roadmap docs to improve visibility and reduce noise. it should follow [.agents/references/application-lifecycle.md](.agents/references/application-lifecycle.md)
3. add new workflow and task to trigger interactive session:
   ```md
       we are starting interactive session of working only on documentation focusing AGENTS.md, .agents/references and /docs. do not run any test, builds or any king of verification meant for normal changes.
       in interactive session any handoff will be done explicitly at the end
   ```
4.

## Deferred Work

### Optional Future Enhancements

| Status | Area | Candidate Work | Trigger |
| --- | --- | --- | --- |
| Deferred | Batch processing | Add Spring Batch and jobs for book import, export, or audit cleanup | Bulk import, export, or scheduled cleanup becomes a real requirement |
| Deferred | Async message processing | Add RabbitMQ or Kafka and move suitable notification or audit fan-out flows async | Event-driven processing becomes necessary |
| Deferred | Full-text search | Add Elasticsearch, index books and localization messages, and expose advanced search endpoints | Relational search no longer satisfies product needs |
| Deferred | GraphQL API | Add Spring GraphQL with schemas for books, users, and localization data | A real client needs GraphQL queries or mutations |
