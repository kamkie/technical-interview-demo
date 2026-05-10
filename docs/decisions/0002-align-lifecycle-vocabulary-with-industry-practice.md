# 0002 Align Lifecycle Vocabulary With Industry Practice

## Status

Proposed

## Date

2026-05-10

## Context

The repository currently uses a compact lifecycle vocabulary in `.agents/references/application-lifecycle.md`:

- `Discovery`
- `Roadmap Intake`
- `Planning`
- `Implementation`
- `Testing`
- `Review`
- `Integration`
- `Release`
- `Deployment`
- `Operations`
- `Continuous Improvement`

This vocabulary is serviceable, but the early phases are not explicit enough about lightweight idea capture, requirements analysis, triage, and execution planning.
The result is that early uncertainty can be routed too quickly into an implementation plan, even when the real work is still rough problem capture, requirements gathering, product definition, or a process decision.

ADR 0001 proposes optional pre-planning artifacts: ADRs, PRDs, standalone specs, and plans.
This ADR defines the lifecycle wording that should make those artifacts understandable and consistent across AI guidance and human-facing docs.

There is no single universal industry-standard phase taxonomy for the software or systems development lifecycle.
Common SDLC references use phase names such as conceptualization, requirements analysis, design, construction, acceptance, deployment, maintenance, and decommission.
Other frameworks emphasize product discovery, backlog management, verification, secure development practices, or continuous improvement.

This repository should choose lightweight, industry-recognizable wording that maps to SDLC and modern product-delivery practices without adopting a full waterfall, Scrum, compliance, or enterprise governance lifecycle.

## Decision

Adopt this lifecycle vocabulary for repository guidance:

| Order | Proposed phase | Replaces / refines | Purpose |
| --- | --- | --- | --- |
| 1 | Conceptualization | `Discovery` | Lightweight capture of problems, ideas, TODOs, rough task candidates, maintenance signals, links, and early framing notes before deciding whether structure is needed. |
| 2 | Analysis | new explicit phase between conceptualization and triage | Elicit, analyze, document, and validate requirements, acceptance criteria, non-goals, constraints, and open questions when rough ideas need structured definition. |
| 3 | Triage | `Roadmap Intake` | Capture submitted or candidate work, then accept, defer, reject, prioritize, sequence, or decide whether it needs an ADR, PRD, spec, or plan next. |
| 4 | Planning | unchanged, narrowed to execution planning | Produce a decision-complete executable plan with plan tasks, validation, ownership, and handoff details. |
| 5 | Implementation | unchanged | Build the smallest spec-driven change. |
| 6 | Verification | `Testing` | Run automated, contract, manual, benchmark, or documentation checks appropriate to the change. |
| 7 | Review | unchanged | Review correctness, contract impact, security risk, and documentation drift. |
| 8 | Integration | unchanged | Land reviewed work on the integration branch and confirm the integrated state. |
| 9 | Release | unchanged | Prepare versioned artifacts and release notes. |
| 10 | Deployment | unchanged | Promote and verify released artifacts in the target runtime environment. |
| 11 | Operations | unchanged | Observe, triage, remediate, and schedule production or post-release signals. |
| 12 | Maintenance | `Continuous Improvement` | Capture learnings, maintenance signals, and feedback that feed the next product or engineering cycle. |

Use these artifact roles:

| Artifact | Phase fit | Purpose |
| --- | --- | --- |
| ADR | Conceptualization, Analysis, or Planning | Durable decision rationale for architecture, workflow, security, contract policy, documentation ownership, or process choices when rough capture exposes a decision. |
| PRD | Analysis | Product intent, users, goals, non-goals, requirements, and acceptance criteria for broad or ambiguous user-facing work. |
| Roadmap entry | Triage | Active, planned, selected, implemented, or deferred work tracking. |
| Spec | Analysis and Planning | Exact behavior, contract impact, acceptance criteria, and validation mapping. |
| Plan | Planning | Execution handoff with plan tasks, validation, ownership, blockers, and user verification. |

Use `Planning` for executable implementation plans.
Use `Conceptualization` for low-friction unordered capture of problems, ideas, TODOs, rough task candidates, and links.
Use `Analysis` for structured requirements work that is not ready to plan.
Use `Triage` for active-work tracking and accept/defer/reject/prioritize decisions, not for detailed requirements or execution steps.
Rough roadmap candidates can exist before a PRD or spec, but selected broad product work should reference product requirements and behavior specs when those artifacts are needed to make the work decision-complete.

Map the proposed repository terms to common SDLC terminology as follows:

| Proposed repository term | Common SDLC or product-delivery analogue |
| --- | --- |
| Conceptualization | Idea capture, conceptualization, problem framing, feasibility signals |
| Analysis | Requirements analysis, requirements engineering, acceptance-criteria definition |
| Triage | Proposal submission, intake, triage, prioritization, backlog intake, portfolio or roadmap sequencing |
| Planning | Design planning, implementation planning, sprint or iteration planning without adopting Scrum cadence |
| Implementation | Construction, coding, build |
| Verification | Acceptance, system testing, validation, verification |
| Review | Peer review, quality review, security review |
| Integration | Integration, merge, integration testing checkpoint |
| Release | Release preparation, packaging, release notes |
| Deployment | Deployment, rollout, promotion |
| Operations | Runtime operations, monitoring, incident triage, response |
| Maintenance | Maintenance, maintenance feedback, retrospective, continuous improvement, next-cycle improvement |

## Required Follow-Up Changes

If accepted, update these files together:

1. `.agents/references/application-lifecycle.md`
   - Rename phases in the phase table.
   - Add `Analysis` as an explicit requirements-focused phase.
   - Rename `Testing` to `Verification`.
   - Update phase entries, exits, loops, activity catalogue sections, required artifact roles, current gaps, and maintenance text.
   - Add or rename activities such as `Elicit`, `Analyze`, `Define-Requirements`, `Validate-Requirements`, `Intake`, `Prioritize`, and `Sequence`.

2. `.agents/references/planning.md`
   - Update valid plan phase values.
   - Route unstructured ideas to `Conceptualization` and structured requirement gaps to `Analysis` instead of immediately creating a plan.
   - Keep `Planning` as the phase for executable `.agents/plans/PLAN_*.md` creation and readiness review.
   - Teach plans to reference PRDs, ADRs, and specs when they exist.

3. `.agents/references/plan-template.md`
   - Update lifecycle placeholder guidance.
   - Add optional rows or prompts for linked ADR, PRD, and spec artifacts.

4. `.agents/references/documentation.md`
   - Add artifact ownership for `docs/requirements/*.md` and `docs/specs/*.md`.
   - Keep ADR ownership at `docs/decisions/*.md`.
   - Route lifecycle vocabulary changes to `.agents/references/application-lifecycle.md`.
   - Route product-intent documents to PRDs and behavior truth to specs.

5. `AGENTS.md`
   - Update lifecycle phase names only where the repo-level map or completion rules mention changed terms.
   - Keep detailed lifecycle rules in `.agents/references/application-lifecycle.md`.

6. `WORKING_WITH_AI.md`
   - Update the human-facing lifecycle section names and descriptions.
   - Explain when a developer should add an idea to Conceptualization, ask for Analysis, create an ADR, PRD, spec, or move into Planning.

7. `ROADMAP.md`
   - Decide whether `## Intake` should become `## Conceptualization / Analysis`, `## Triage`, or another accepted heading.
   - Update status meanings if they reference old phase names.
   - Remove or revise the roadmap intake items that this ADR resolves once implementation lands.

8. Templates or skeletons
   - Add minimal ADR, PRD, and spec templates only if ADR 0001 is accepted.
   - Keep templates short and skip-friendly.

9. Existing active plans and current docs
   - Update lifecycle phase labels only when the plan remains active and the label affects current execution.
   - Do not rewrite archived plans or historical changelog entries except where current guidance points to stale names.

## Consequences

Benefits:

- rough ideas can be captured cheaply while structured analysis gets its own vocabulary
- plans stop absorbing pre-planning decisions and unclear requirements
- PRDs, ADRs, specs, roadmap entries, and plans have distinct lifecycle roles
- `Verification` better covers tests, manual checks, contract checks, documentation checks, and benchmarks than `Testing`

Costs:

- phase-name changes touch multiple AI and human-facing guidance files
- active plans may need lifecycle label updates
- too much terminology can slow the repository down unless skip rules stay explicit

## Alternatives Considered

### Keep Current Lifecycle Names

This avoids churn and the existing names are understandable.
It does not solve the ambiguity between rough idea capture, requirements gathering, roadmap intake, and executable planning.

### Use Classic SDLC Names Directly

Classic names such as `Conceptualization`, `Requirements Analysis`, `Design`, `Construction`, `Acceptance`, `Deployment`, `Maintenance`, and `Decommission` are recognizable.
They are useful as reference points, but they do not fit this repo's spec-driven, roadmap-driven, and lightweight delivery workflow as directly as the proposed lightweight conceptualization, analysis, triage, and planning vocabulary.

### Use Scrum Terms Directly

Terms such as product backlog, sprint planning, sprint review, and retrospective are common.
The repository does not mandate Scrum, sprints, a product owner role, or a sprint cadence, so adopting Scrum vocabulary directly would imply a process the repo does not actually use.

### Add Analysis Without Renaming Other Phases

This would reduce churn.
It leaves the early lifecycle half-modernized and keeps `Planning` ambiguous between product planning and executable implementation planning.

## Confirmation

This ADR is accepted only when the lifecycle owner guide and overlapping guidance are updated consistently.
Acceptance should be verified by manual documentation review that:

- `.agents/references/application-lifecycle.md` is the canonical lifecycle vocabulary owner
- `.agents/references/planning.md` uses the accepted phase names
- `WORKING_WITH_AI.md` explains the lifecycle in developer-facing terms
- `ROADMAP.md` no longer contains unresolved intake items for this vocabulary change
- templates and artifact routing match ADR 0001 and this ADR
- historical docs are left unchanged unless they are live guidance

## Links

- ADR 0001: `docs/decisions/0001-adopt-pre-planning-artifacts.md`
- Systems development life cycle overview: https://en.wikipedia.org/wiki/Systems_development_life_cycle
- NIST Secure Software Development Framework: https://csrc.nist.gov/Projects/ssdf
- NIST SP 800-218: https://csrc.nist.gov/pubs/sp/800/218/final
- The Scrum Guide: https://scrumguides.org/
- ADR overview: https://adr.github.io/
