# Project Roadmap

This file tracks active and upcoming work for the technical-interview-demo Spring Boot application.
Keep it focused on work that is still planned, selected, in progress, or intentionally deferred.
Released history belongs in `CHANGELOG.md`.

## Current Project State

| Field | Current |
| --- | --- |
| Release Phase | Stable `v2.0.0` |
| Breaking Change Policy | Disallowed on the stable `2.x` line unless a later major-version plan explicitly changes it |
| Next Target Version | Not selected; default to `v2.0.1` for maintenance fixes or `v2.1.0` for backward-compatible feature work |
| Latest Stable Release | `v2.0.0` released on 2026-05-08 from accepted `v2.0.0-RC7`; manual-regression evidence is archived in `.agents/archive/PLAN_manual_regression_execution.md` |
| Immediate Next Action | Select and plan the next post-`2.0` workstream |

## Roadmap Rules

- Keep only active, planned, selected, or intentionally deferred work here.
- Use `Status` values instead of checkbox semantics.
- Remove work once it is released, intentionally dropped, or no longer active.
- Keep detailed active milestones, validation notes, and implementation history in `.agents/plans/PLAN_*.md`, not in this file; released plan history belongs under `.agents/archive/`.

| Status | Meaning |
| --- | --- |
| Selected | Approved for current planning or development |
| Planned | Known upcoming work, not yet selected for execution |
| In Progress | Execution or validation is underway |
| Implemented | Execution is done but integration, release, or cleanup still keeps the item active |
| Deferred | Intentionally postponed until the named trigger |
| Candidate | Rough work that needs refinement before planning |

## Active Release Track

No active release-track work is selected.

## Planned Work

Use this section for plan-backed work that is ready but not selected as the immediate release-track action.

| Status | Workstream | Plan Or Artifact | Notes |
| --- | --- | --- | --- |
| Planned | Frontend AI contract | To be planned | Generate a frontend-contract AI instruction file in this repository using Anthropic's `frontend-design` skill as source guidance, then copy it into the frontend repository as source input for that repo's AI instructions |

## Intake

Use this section for rough candidate tasks that are not specific enough for the active release track or deferred backlog.
Rewrite an item into a concrete roadmap row before moving it into active or deferred work.

| Status | Candidate | Notes |
| --- | --- | --- |

## Deferred Work

### Optional Future Enhancements

| Status | Area | Candidate Work | Trigger |
| --- | --- | --- | --- |
| Deferred | Batch processing | Add Spring Batch and jobs for book import, export, or audit cleanup | Bulk import, export, or scheduled cleanup becomes a real requirement |
| Deferred | Async message processing | Add RabbitMQ or Kafka and move suitable notification or audit fan-out flows async | Event-driven processing becomes necessary |
| Deferred | Full-text search | Add Elasticsearch, index books and localization messages, and expose advanced search endpoints | Relational search no longer satisfies product needs |
| Deferred | GraphQL API | Add Spring GraphQL with schemas for books, users, and localization data | A real client needs GraphQL queries or mutations |
