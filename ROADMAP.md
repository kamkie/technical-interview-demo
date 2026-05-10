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
| Immediate Next Action | Review integrated post-`v2.0.1` workstreams; release work remains unrequested. |

## Roadmap Rules

- Keep only active, planned, selected, or intentionally deferred work here.
- Use `Status` values instead of checkbox semantics.
- Remove work once it is released, intentionally dropped, or no longer active.
- Keep detailed active plan tasks, validation notes, and implementation history in `.agents/plans/PLAN_*.md`, not in this file; released plan history belongs under `.agents/archive/`.

| Status | Meaning |
| --- | --- |
| Candidate | Rough work in Conceptualization, Analysis, or Triage that needs refinement before planning |
| Planned | Known upcoming work, not yet selected for execution |
| Selected | Approved for current planning or development |
| In Progress | Execution or validation is underway |
| Implemented | Execution is done but integration, release, or cleanup still keeps the item active |
| Deferred | Intentionally postponed until the named trigger |

## Active Release Track

| Status | Workstream | Plan Or Artifact | Notes |
| --- | --- | --- | --- |

## Planned Work

Use this section for plan-backed work that is not selected as the immediate release-track action. Notes must state when a plan is not ready for execution yet.

| Status | Workstream | Plan Or Artifact | Notes |
| --- | --- | --- | --- |
| Implemented | Multi-agent roles, skills, and mode labels | `.agents/plans/PLAN_multi_agent_roles_and_skills.md`; `docs/decisions/0004-adopt-skill-first-multi-agent-workflow.md`; `docs/decisions/0003-adopt-multi-agent-roles-and-skill-catalog.md` | ADRs are accepted; workflow labels, role roster, context state, skill catalog, platform guidance, smoke-test evidence, and live guidance sweep are implemented; release/archive cleanup remains out of scope until requested. |
| Implemented | Pre-planning artifact and lifecycle vocabulary adoption | `.agents/plans/PLAN_pre_planning_lifecycle_artifacts.md`; `docs/decisions/0001-adopt-pre-planning-artifacts.md`; `docs/decisions/0002-align-lifecycle-vocabulary-with-industry-practice.md` | ADRs, templates, lifecycle guidance, planning guidance, human-facing guidance, roadmap, and unreleased history are aligned; release/archive cleanup remains out of scope until requested. |
| Implemented | Multi-agent workflow consolidation | `.agents/plans/PLAN_multi_agent_workflow_consolidation.md` | Workflow guidance is consolidated in `.agents/references/workflow.md`, live routing uses `.agents/context/*` workflow state, and release/archive cleanup remains out of scope until requested |
| Implemented | Frontend AI contract | `.agents/plans/PLAN_frontend_ai_contract.md` | Backend-owned source contract is implemented; external frontend-repo copy deferred until destination and instruction path are known |

## Triage

No loose triage items are active. The pre-planning artifact and lifecycle vocabulary work is tracked by `.agents/plans/PLAN_pre_planning_lifecycle_artifacts.md`.

## Deferred Work

### Optional Future Enhancements

| Status | Area | Candidate Work | Trigger |
| --- | --- | --- | --- |
| Deferred | Batch processing | Add Spring Batch and jobs for book import, export, or audit cleanup | Bulk import, export, or scheduled cleanup becomes a real requirement |
| Deferred | Async message processing | Add RabbitMQ or Kafka and move suitable notification or audit fan-out flows async | Event-driven processing becomes necessary |
| Deferred | Full-text search | Add Elasticsearch, index books and localization messages, and expose advanced search endpoints | Relational search no longer satisfies product needs |
| Deferred | GraphQL API | Add Spring GraphQL with schemas for books, users, and localization data | A real client needs GraphQL queries or mutations |
