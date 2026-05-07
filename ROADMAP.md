# Project Roadmap

This file tracks active and upcoming work for the technical-interview-demo Spring Boot application.
Keep it focused on work that is still planned, selected, in progress, or intentionally deferred.
Released history belongs in `CHANGELOG.md`.

## Current Project State

| Field | Current |
| --- | --- |
| Release Phase | Prerelease |
| Breaking Change Policy | Disallowed |
| Next Target Version | `v2.0.0` |
| Stable Release Gate | Manual regression pass on the final release candidate, currently `v2.0.0-RC6`, after the harness-output logging requirements are implemented |
| Immediate Next Action | Implement Milestone 0 in `.agents/plans/PLAN_manual_regression_execution.md`, then execute the plan against `v2.0.0-RC6` |

## Roadmap Rules

- Keep only active, planned, selected, or intentionally deferred work here.
- Use `Status` values instead of checkbox semantics.
- Remove work once it is released, intentionally dropped, or no longer active.
- Keep detailed milestones, validation notes, and implementation history in `.agents/plans/PLAN_*.md`, not in this file.

| Status | Meaning |
| --- | --- |
| Selected | Approved for current planning or development |
| Planned | Known upcoming work, not yet selected for execution |
| In Progress | Execution or validation is underway |
| Implemented | Execution is done but integration, release, or cleanup still keeps the item active |
| Deferred | Intentionally postponed until the named trigger |
| Candidate | Rough work that needs refinement before planning |

## Active Release Track

### Moving To `2.0`

Goal: use manual regression coverage as the RC-to-stable gate, then finish the stable `2.0` line after the final release candidate is accepted.

| Order | Status | Workstream | Plan Or Artifact | Exit Criteria |
| --- | --- | --- | --- | --- |
| 1 | In Progress | Manual regression gate for the final `2.0` release candidate | `.agents/plans/PLAN_manual_regression_execution.md` | Harness has landed, but new `/temp` output, heavy request/response logging, execution-log, and example-report requirements must land before manual business-functionality regression runs against final RC `v2.0.0-RC6`; if another RC is cut first, the plan is replanned to that RC |
| 2 | Implemented | Repository knowledge layout migration for the next RC | `.agents/plans/PLAN_repository_knowledge_layout_migration.md` | Durable repository knowledge follows the `docs/` plus `.agents/` layout; if this lands before stable, cut the next RC, expected `v2.0.0-RC7` |
| 3 | Planned | Stable `v2.0.0` release | `CHANGELOG.md`, `ROADMAP.md`, release artifacts | Stable `v2.0.0` is released after the final accepted RC, changelog and roadmap are updated, and the completed `2.0` track is removed from this file |

## Planned Work

Use this section for plan-backed work that is ready but not selected as the immediate release-track action.

| Status | Workstream | Plan Or Artifact | Notes |
| --- | --- | --- | --- |
| Planned | AI context measurement guardrails | `.agents/plans/PLAN_ai_context_measurement_guardrails.md` | Add a reusable context-report script and warning thresholds for default-load and total `.agents/` inventory growth |
| Implemented | Repo task skill migration | `.agents/plans/PLAN_repo_task_skill_migration.md` | Replaced the former reusable-task catalog with the low-context `repo-task` skill; release cleanup can remove this active roadmap row when the AI-guidance changes are released |
| Implemented | AI guidance execution and reusable task library restructure | `.agents/plans/PLAN_ai_guidance_execution_prompt_library_restructure.md` | Implementation and final validation are complete; release cleanup can remove this active roadmap row when the AI-guidance changes are released |

## Intake

Use this section for rough candidate tasks that are not specific enough for the active release track or deferred backlog.
Rewrite an item into a concrete roadmap row before moving it into active or deferred work.

| Status | Candidate | Notes |
| --- | --- | --- |

## Deferred Work

### Post-`2.0`

| Status | Workstream | Trigger | Notes |
| --- | --- | --- | --- |
| Deferred | Frontend AI contract | Stable `v2.0.0` is released | Generate a frontend-contract AI instruction file in this repository using Anthropic's `frontend-design` skill as source guidance, then copy it into the frontend repository as source input for that repo's AI instructions |

### Optional Future Enhancements

| Status | Area | Candidate Work | Trigger |
| --- | --- | --- | --- |
| Deferred | Batch processing | Add Spring Batch and jobs for book import, export, or audit cleanup | Bulk import, export, or scheduled cleanup becomes a real requirement |
| Deferred | Async message processing | Add RabbitMQ or Kafka and move suitable notification or audit fan-out flows async | Event-driven processing becomes necessary |
| Deferred | Full-text search | Add Elasticsearch, index books and localization messages, and expose advanced search endpoints | Relational search no longer satisfies product needs |
| Deferred | GraphQL API | Add Spring GraphQL with schemas for books, users, and localization data | A real client needs GraphQL queries or mutations |
