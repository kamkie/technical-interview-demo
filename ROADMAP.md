# Project TODO & Roadmap

This file tracks active and upcoming work for the technical-interview-demo Spring Boot application.
Keep this file focused on work that is still planned or in progress.

## Current Project State

| Status | Current |
| --- | --- |
| Release Phase | Prerelease |
| Breaking Change Policy | Disallowed |
| Next Target Version | `v2.0.0` |

## How To Use This File

- Keep only active or planned work here.
- Use `[ ]` for planned work that is not yet selected.
- Use `[x]` for work explicitly selected for current planning or development.
- Do not use `[x]` to mean completed; remove an item once it is no longer active roadmap work.
- Keep items short so they are easy to reorder and edit.
- Remove completed items instead of turning this file into a historical archive.
- Use `CHANGELOG.md` for released history, not `ROADMAP.md`.

## Current Priorities

- Use the manual regression pass as the gate from the final RC, currently `v2.0.0-RC5`, to stable `v2.0.0`; if another RC is cut for other plans, replan the manual regression pass to that next RC.

## Not Yet Refined

Keep rough candidate tasks here for manual editing before they are promoted into real roadmap entries.

- Use this section as an intake list for ideas that are not yet specific enough for `Current Priorities` or `Ordered Plan`.
- Rewrite an item into a concrete roadmap entry before moving it below.
- Remove an item from this section once it is promoted, merged into another entry, or intentionally dropped.

### Rough Tasks

Add new rough tasks below.

## Ordered Plan

### Moving to `2.0`

Status: Planned

Goal: use manual regression coverage as the RC-to-stable gate, then finish the stable `2.0` line after the final release candidate is accepted.

#### Release Confidence

- [x] Execute `ai/PLAN_manual_regression_execution.md` as the manual business-functionality regression gate for the final `2.0` release candidate, currently `v2.0.0-RC5`; replan it to the next RC if another RC is prepared first. Plan lifecycle: `Planning` / `Ready`.
- [ ] Release stable `v2.0.0`, update `CHANGELOG.md`, and remove the completed `2.0` track from `ROADMAP.md`.

#### AI Workflow Guidance

- [x] Execute `ai/PLAN_pskoett_ai_skill_guidance_adoption.md` to selectively adopt search-first session-start, context-quality, scope-drift, and post-validation review guidance from `pskoett/pskoett-ai-skills` without importing its external skill framework. Plan lifecycle: `Integration` / `Implemented`; implemented 2026-05-07.
- [x] Execute `ai/PLAN_workflow_on_demand_split.md` to shrink `ai/WORKFLOW.md` into a compact mode router, move fanout mechanics into on-demand references, and rename execution modes by plan topology. Plan lifecycle: `Integration` / `Implemented`.
- [x] Execute `ai/PLAN_ai_guidelines_post_compaction_evaluation.md` to produce a current post-compaction AI-guidance grade and rank any remaining compaction opportunities. Plan lifecycle: `Planning` / `Ready`; replanned 2026-05-07 after prerequisites landed.

## Deferred

### Post-`2.0` Frontend AI Contract

Status: Deferred until stable `2.0` is released

- [ ] Generate a frontend-contract AI instruction file in this repository using the `frontend-design` skill from Anthropic's `skills` repository as source guidance, then copy it into the frontend repository as source input for the AI agent there to generate that repo's AI instructions.

### Optional Future Enhancements

Status: Deferred until the core roadmap is complete

#### Batch Processing

- [ ] Add Spring Batch if bulk import/export becomes necessary
- [ ] Add jobs for book import or audit cleanup

#### Async Message Processing

- [ ] Add RabbitMQ or Kafka if event-driven flows become necessary
- [ ] Move notifications or audit fan-out to async processing

#### Full-Text Search

- [ ] Add Elasticsearch if search requirements outgrow the relational model
- [ ] Index books and localization messages
- [ ] Expose advanced search endpoints

#### GraphQL API

- [ ] Add Spring GraphQL only if there is a real client need
- [ ] Define schema for books, users, and localization data
- [ ] Implement queries and mutations
