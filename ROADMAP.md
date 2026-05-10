# Project Roadmap

This file tracks active and upcoming work for the technical-interview-demo Spring Boot application.
Keep it focused on work that is still planned, selected, in progress, or intentionally deferred.
Released history belongs in `CHANGELOG.md`.

Section ordering follows the lifecycle vocabulary in `.agents/references/application-lifecycle.md`: rough capture lands in `## Conceptualization`, structured requirements work in `## Analysis`, durable decisions in `## Decisions`, accept/defer/prioritize calls in `## Triage`, and committed execution in `## Active Release Track` and `## Planned Work`.

## Table Of Contents

- [Current Project State](#current-project-state)
- [Roadmap Rules](#roadmap-rules)
- [Conceptualization](#conceptualization)
- [Analysis](#analysis)
- [Decisions](#decisions)
- [Triage](#triage)
- [Active Release Track](#active-release-track)
- [Planned Work](#planned-work)
- [Deferred Work](#deferred-work)

## Current Project State

| Field | Current |
| --- | --- |
| Release Phase | Stable `v2.0.2` |
| Breaking Change Policy | Disallowed on the stable `2.x` line unless a later major-version plan explicitly changes it |
| Next Target Version | Not selected; default to `v2.0.3` for maintenance fixes or `v2.1.0` for backward-compatible feature work |
| Latest Stable Release | `v2.0.2` released on 2026-05-10 from the AI-guidance and multi-agent workflow plans archived under `.agents/archive/PLAN_frontend_ai_contract.md`, `.agents/archive/PLAN_multi_agent_roles_and_skills.md`, `.agents/archive/PLAN_multi_agent_workflow_consolidation.md`, and `.agents/archive/PLAN_pre_planning_lifecycle_artifacts.md` |
| Immediate Next Action | Select the next post-`v2.0.2` workstream; release work remains unrequested. |

## Roadmap Rules

- Keep only active, planned, selected, or intentionally deferred work here.
- Use `Status` values instead of checkbox semantics.
- Remove work once it is released, intentionally dropped, or no longer active.
- Keep detailed active plan tasks, validation notes, and implementation history in `.agents/plans/PLAN_*.md`, not in this file; released plan history belongs under `.agents/archive/`.
- Lifecycle phase names follow `.agents/references/application-lifecycle.md`; pre-planning artifact roles (ADR, PRD, spec, plan) follow `docs/decisions/0001-adopt-pre-planning-artifacts.md`.

| Status | Meaning | Typical phase |
| --- | --- | --- |
| Candidate | Rough capture or analysis in progress; no commitment yet | Conceptualization, Analysis, or Triage |
| Planned | Triage-accepted; not yet selected for execution | end of Triage |
| Selected | Approved for current planning or development | Planning |
| In Progress | Execution or validation underway | Implementation, Verification, or Review |
| Integrated | Landed on `main`; release, deployment, or cleanup still keeps the item active | Integration |
| Released | Versioned, published, and tracked in `CHANGELOG.md`; remove from the roadmap once cleanup completes | Release or later |
| Deferred | Intentionally postponed until a named trigger | any |

Pre-planning artifacts use their own status vocabulary in `## Decisions` and `## Analysis`:

| Artifact | Status values |
| --- | --- |
| ADR | `Proposed`, `Accepted`, `Superseded`, `Rejected` |
| PRD | `Draft`, `In Review`, `Approved`, `Withdrawn` |
| Spec | `Draft`, `Approved`, `Stale` |

### When To Skip Pre-Planning Artifacts

- Trivial, non-behavioral, or local-only work (typos, formatting, comments, internal renames with no public surface) does not require a Conceptualization, Analysis, Decisions, or Triage row; route directly to a plan or an ad hoc task.
- Skip Conceptualization when the idea is already structured enough for Analysis or Triage.
- Skip Analysis when behavior is already covered by an executable spec, an approved PRD, or an approved standalone spec.
- Skip Decisions when no durable architecture, workflow, contract, security, or process choice is being made.
- A roadmap row never needs more than the columns its phase requires; empty cells are valid.

## Conceptualization

Use this section for rough ideas, TODOs, maintenance signals, links, and early framing notes that are not yet ready for structured analysis or triage.

| Status | Idea | Linked Artifacts | Next Decision |
| --- | --- | --- | --- |

1. split LEARNINGS.md into owner rule file and `LEARNINGS.md` storage file
2. add a rule that generated adr, pdr, and plans have information/section of what ai agend did created it.

## Analysis

Use this section for structured requirements, product intent, behavior rules, constraints, non-goals, and acceptance criteria work that is not yet ready for execution planning. Rows usually link to a draft PRD or standalone spec under `docs/requirements/` or `docs/specs/`.

| Status | Topic | PRD / Spec | Open Questions | Exit Criteria |
| --- | --- | --- | --- | --- |

No active analysis items.

## Decisions

Use this section to surface durable decisions captured in `docs/decisions/`. The ADR file remains the source of truth; this table is a navigational index for the active set.

| Status | ADR | Date | Implementation |
| --- | --- | --- | --- |
| Accepted | `docs/decisions/0001-adopt-pre-planning-artifacts.md` | 2026-05-10 | `.agents/archive/PLAN_pre_planning_lifecycle_artifacts.md` |
| Accepted | `docs/decisions/0002-align-lifecycle-vocabulary-with-industry-practice.md` | 2026-05-10 | `.agents/archive/PLAN_pre_planning_lifecycle_artifacts.md` |
| Accepted | `docs/decisions/0003-adopt-multi-agent-roles-and-skill-catalog.md` | — | `.agents/archive/PLAN_multi_agent_roles_and_skills.md` |
| Accepted | `docs/decisions/0004-adopt-skill-first-multi-agent-workflow.md` | — | `.agents/archive/PLAN_multi_agent_roles_and_skills.md` |
| Accepted | `docs/decisions/0005-adopt-operations-and-deployment-owner-guide.md` | 2026-05-10 | `.agents/references/operations.md` |
| Proposed | `docs/decisions/0006-split-human-documentation-and-ai-workflow-guides.md` | 2026-05-10 | Not planned |

## Triage

Use this section to record accept/defer/reject/prioritize/sequence decisions and the next artifact each accepted item needs (plan, PRD, spec, or ADR).

| Status | Workstream | Source | Decision | Next Artifact |
| --- | --- | --- | --- | --- |

No active triage items.

## Active Release Track

| Status | Workstream | Phase | Plan | Linked ADR/PRD/Spec | Notes |
| --- | --- | --- | --- | --- | --- |

## Planned Work

Use this section for plan-backed work that is not selected as the immediate release-track action. Notes must state when a plan is not ready for execution yet.

| Status | Workstream | Phase | Plan | Linked ADR/PRD/Spec | Notes |
| --- | --- | --- | --- | --- | --- |

No active planned work.

## Deferred Work

### Optional Future Enhancements

| Status | Area | Candidate Work | Trigger |
| --- | --- | --- | --- |
| Deferred | Batch processing | Add Spring Batch and jobs for book import, export, or audit cleanup | Bulk import, export, or scheduled cleanup becomes a real requirement |
| Deferred | Async message processing | Add RabbitMQ or Kafka and move suitable notification or audit fan-out flows async | Event-driven processing becomes necessary |
| Deferred | Full-text search | Add Elasticsearch, index books and localization messages, and expose advanced search endpoints | Relational search no longer satisfies product needs |
| Deferred | GraphQL API | Add Spring GraphQL with schemas for books, users, and localization data | A real client needs GraphQL queries or mutations |
