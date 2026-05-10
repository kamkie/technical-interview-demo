# 0006 Split Human-Facing Docs And Expose Spec-Driven Entry Points

## Status

Proposed on 2026-05-10.

## Date

2026-05-10

## Context

`README.md` is a thin index, but the three companion human-facing files have grown past their stated scopes:

- `SETUP.md` is 972 lines and bundles three distinct audiences: local onboarding (Java, `.env`, IDE, OAuth local flow), CI reproduction (workflow contract, classifier, Codecov, image scan, release workflow), and deploy/operate runbook (Kubernetes, Helm, monitoring, scheduled post-deploy smoke, upgrade-and-rollback, restore drill, Cosign verification, deploy-specific troubleshooting). A first-time contributor wades through Cosign verification; an operator searching for rollback wades through `JAVA_HOME` setup.
- `CONTRIBUTING.md` is 293 lines and duplicates content owned elsewhere: an AI working model already owned by `WORKING_WITH_AI.md`, validation commands already owned by `SETUP.md` and `.agents/references/testing.md`, release-prep steps already owned by `.agents/references/releases.md`, and commit-footer rules already owned by `.gitmessage` plus `.agents/references/execution.md`.
- `WORKING_WITH_AI.md` (247 lines) is well-shaped as a lifecycle index, but it is not surfaced from `README.md` strongly enough, and the spec-driven entry points it depends on are also not surfaced.

At the same time, several artifacts that are core to how this repository works are effectively hidden from the human entry points:

- `docs/DESIGN.md` (product intent and non-goals) is referenced once in `README.md` and not promoted as part of the spec-priority story.
- `.agents/references/application-lifecycle.md` (phase plus activity vocabulary) lives under `.agents/`, so a contributor must enter the AI guidance tree to learn the lifecycle that `WORKING_WITH_AI.md` and `AGENTS.md` keep referencing.
- `docs/decisions/`, `docs/requirements/`, and `docs/specs/` already contain `ADR_TEMPLATE.md`, `PRD_TEMPLATE.md`, and `SPEC_TEMPLATE.md`, but the directories have no `README.md`, so GitHub's directory view does not explain what each folder is for, when to add an entry, or where the template is.
- The planning workflow (`.agents/plans/PLAN_*.md`, `.agents/references/planning.md`, `.agents/references/plan-execution.md`) is reachable only through `WORKING_WITH_AI.md` and `AGENTS.md`.

This decision needs to record *which shape* the human-facing documentation set takes and *which migration order* avoids breaking existing cross-links and validation (`pwsh ./scripts/docs/audit-docs.ps1`).

This decision is documentation architecture, not product runtime behavior.
It does not change application code, public APIs, release sequencing, or validation requirements by itself.
It deliberately does not undefer ADR 0005; it gives the deferred AI owner guide (`.agents/references/operations.md`) a human-facing partner to point at when activated.

## Decision

Adopt the following human-facing documentation file map and expose the spec-driven entry points from `README.md`.

Target file map:

- `README.md` — thin index, restructured into three labelled groups: "Start Here" (operational entry points), "How We Work" (spec-driven artifacts), and "Working With AI" (AI guidance entry points).
- `SETUP.md` — onboarding only: prerequisites, `.env`, quick start, IDE, database modes, running the application, basic test loop, OAuth local examples, and local-only troubleshooting.
- `docs/RUNBOOK.md` (new) — deploy and operate: deployment contract, container smoke validation, scheduled post-deploy smoke, healthy runtime expectations, upgrade and rollback (including restore drill), Kubernetes deployment, Helm deployment, monitoring and alerting, verifying published release artifacts, deploy-specific troubleshooting.
- `docs/CI.md` (new) — CI and release pipeline contract: CI workflow, lightweight-change classifier, Codecov, image vulnerability scan, release workflow, post-deploy smoke workflow, release-note rendering.
- `CONTRIBUTING.md` — contributor workflow only: ground rules, spec-driven loop (linking out to `docs/DESIGN.md` and `docs/LIFECYCLE.md`), branches and commits (linking out to `.gitmessage` and `.agents/references/execution.md`), pull-request expectations, worktrees and integration, one documentation routing table.
- `WORKING_WITH_AI.md` — unchanged in scope, with two minor additions described below.
- `docs/DESIGN.md` — unchanged content; promoted in `README.md`.
- `docs/LIFECYCLE.md` (new, thin) — human-readable summary of `.agents/references/application-lifecycle.md`: phase definitions, when to write an ADR versus PRD versus spec versus plan, pointer table to owner guides, explicit statement that the AI owner is `.agents/references/application-lifecycle.md`.
- `docs/decisions/README.md` (new, thin) — one paragraph describing when to add an ADR plus a link to `ADR_TEMPLATE.md` and an index of accepted ADRs.
- `docs/requirements/README.md` (new, thin) — same pattern for PRDs.
- `docs/specs/README.md` (new, thin) — same pattern for standalone specs.

`SETUP.md` section routing:

- Keep in `SETUP.md`: Choose A Workflow, Prerequisites, AI Workflow Helper Tools, Quick Start, Environment Variables, Optional Git Commit Template, IDE Setup, Database Modes, Running The Application, Running Tests And Quality Checks (basic loop only), OAuth Setup (local examples), Troubleshooting (Java 11, missing `/docs`, Postgres won't start, Testcontainers, OAuth start, port-in-use, IDE format).
- Move to `docs/RUNBOOK.md`: Deployment Contract, "2.0 UI Integration References" deploy half, Container Smoke Validation, Scheduled Post-Deploy Smoke, Healthy Runtime Expectations, Upgrade And Rollback (including restore drill), Kubernetes Deployment, Helm Deployment, Monitoring And Alerting, Verifying Published Release Artifacts, deploy-specific Troubleshooting (`externalSmokeTest` failure, k8s never ready, Prometheus not scraping).
- Move to `docs/CI.md`: Reproducing CI Locally, the CI/Release/Post-Deploy-Smoke workflow contracts currently inside SETUP, and the Codecov/SBOM/scan artifact bundle paragraph.

`CONTRIBUTING.md` slim-down:

- Replace the "Working With AI" subsection with a single one-line pointer to `WORKING_WITH_AI.md`.
- Replace "Validation Expectations" with a short paragraph plus a pointer to `SETUP.md#running-tests-and-quality-checks` and `.agents/references/testing.md`; keep only the "run `./build.ps1 build` before review" rule and the lightweight-classifier exception.
- Replace "Release And Maintainer Expectations" with one paragraph plus a pointer to `.agents/references/releases.md` and `docs/RUNBOOK.md`.
- Replace "Formatting Expectations" with one paragraph plus a pointer to the `SETUP.md` IDE section.
- Keep Project Ground Rules, Spec-Driven Development (short), Branches And Commit Messages (short, do not restate footer fields), Pull Request Expectations, Worktrees And Integration (short), Documentation Expectations (one routing table only).

`WORKING_WITH_AI.md` adjustments:

- Add a "What this repo treats as a spec" section near the top pointing at `docs/DESIGN.md`, `docs/decisions/`, `docs/requirements/`, `docs/specs/`, and the executable-spec locations, mirroring `AGENTS.md` Spec Priority.
- Promote the link to `.agents/references/application-lifecycle.md` to the top of the file, and add `docs/LIFECYCLE.md` as the human-facing companion so contributors do not have to enter `.agents/` to learn lifecycle vocabulary.

`README.md` Project Map replacement:

```
### Start Here
- SETUP.md            local environment + run
- CONTRIBUTING.md     contributor workflow
- docs/RUNBOOK.md     deploy, operate, monitor, rollback
- docs/CI.md          CI/release pipeline contract

### How We Work (spec-driven)
- docs/DESIGN.md                       product intent + non-goals
- docs/LIFECYCLE.md                    phases + activities (human view)
- ROADMAP.md                           active and planned work
- docs/decisions/    (ADRs)            durable architecture/workflow decisions
- docs/requirements/ (PRDs)            broad/ambiguous user-facing intent
- docs/specs/                          standalone behavior specs
- .agents/plans/                       active execution plans

### Working With AI
- WORKING_WITH_AI.md                                  human-facing AI lifecycle guide
- AGENTS.md                                           repo-local AI rules + Documents Map
- .agents/references/application-lifecycle.md         AI lifecycle reference
- .agents/skills/README.md                            skills catalog
```

Migration order (each step is a separate focused change):

1. Add `docs/LIFECYCLE.md`, `docs/decisions/README.md`, `docs/requirements/README.md`, `docs/specs/README.md`, and update `README.md` Project Map. Pure additions; lowest risk.
2. Carve `docs/CI.md` out of `SETUP.md`. Smallest extraction, fewest cross-links.
3. Carve `docs/RUNBOOK.md` out of `SETUP.md`; rewrite the `SETUP.md` intro and table of contents.
4. Slim `CONTRIBUTING.md` by replacing duplicated sections with pointers.
5. Apply the two `WORKING_WITH_AI.md` tweaks.
6. After each step, run `pwsh ./scripts/docs/audit-docs.ps1` and update any failing local links.

Constraints carried by this decision:

- Do not duplicate content; each topic has one owner and the rest link to it.
- Do not move AI guidance into human-facing files; `AGENTS.md` and `.agents/references/*` remain authoritative for AI rules.
- Do not undefer ADR 0005; `docs/RUNBOOK.md` is the human-facing partner, not the AI owner.
- Keep `.gitmessage` and `.agents/references/execution.md` as the only owners of AI commit-message rules.
- Keep `pwsh ./scripts/docs/audit-docs.ps1` green at every migration step.

## Consequences

Benefits:

- Each human-facing file has one audience and one job: onboarding, contributing, deploying and operating, or CI pipeline contract.
- Spec-driven entry points (`docs/DESIGN.md`, lifecycle, ADR, PRD, spec, planning) are visible from `README.md` and from the relevant directory views, without forcing readers into `.agents/`.
- `CONTRIBUTING.md` shrinks from 293 lines to roughly 120–150 by removing duplication, which makes it actually readable by new contributors.
- `SETUP.md` shrinks from 972 lines to roughly 250–350, restoring its onboarding focus.
- Operators get `docs/RUNBOOK.md` as a single page for deploy, monitor, and rollback, which becomes the natural target for the deferred `.agents/references/operations.md` (ADR 0005) when activated.
- The migration is staged, so each step lands and validates independently.

Costs or risks:

- Many internal cross-links currently point at `SETUP.md` anchors that move; the migration must update those references and re-run `audit-docs.ps1` at each step.
- `docs/RUNBOOK.md` and `docs/CI.md` are net-new files that need to be discoverable; this depends on the `README.md` Project Map update landing in step 1 before the extractions in steps 2 and 3.
- `docs/LIFECYCLE.md` introduces a second lifecycle document; without the explicit "AI owner is `.agents/references/application-lifecycle.md`" statement, the two could drift. The new file must stay thin and mirror, not redefine, the AI reference.
- Slimming `CONTRIBUTING.md` by pointer-only sections can feel sparse; reviewers must accept that pointer-style sections are intentional.

Required follow-up changes:

- After step 1: update any external bookmarks or task prompts that route through the old flat Project Map.
- After step 3: revisit `.agents/references/documentation.md` `## Artifact Ownership` to register `docs/RUNBOOK.md`, `docs/CI.md`, and `docs/LIFECYCLE.md` with their scopes.
- When ADR 0005 activates: add a one-line pointer from `.agents/references/operations.md` to `docs/RUNBOOK.md`, and add a Deployment-and-Operations row to `application-lifecycle.md` `## Owner-Guide Adoption` covering both the AI owner and the human owner.

## Alternatives Considered

### Single mega-page kept as-is

Rejected. `SETUP.md` already crosses 970 lines and mixes three audiences; further growth (the deferred `operations.md` content, future deploy targets) would make it harder, not easier, to navigate.

### Split `SETUP.md` into many small files (one per topic)

Rejected. A page per topic (`KUBERNETES.md`, `HELM.md`, `MONITORING.md`, `ROLLBACK.md`, `POST_DEPLOY_SMOKE.md`, `CI.md`, `RELEASE_VERIFICATION.md`) would over-fragment the deploy-and-operate story for a small demo repository, and the topics are read together during a real deploy or rollback. Two extractions (`docs/RUNBOOK.md` plus `docs/CI.md`) match how the content is actually used.

### Move spec-driven artifacts into `WORKING_WITH_AI.md` instead of `README.md`

Rejected. Spec-driven development is the project's core working model for both human and AI contributors; hiding it behind the AI-facing entry point keeps it invisible to first-time human readers. `README.md` is the right surface.

### Fold `docs/LIFECYCLE.md` content into `docs/DESIGN.md`

Rejected. `docs/DESIGN.md` is product intent and non-goals; lifecycle is workflow vocabulary. Mixing them would inflate `DESIGN.md` and would not fix the "AI guidance hidden under `.agents/`" problem because `application-lifecycle.md` would still be the only authoritative source.

### Replace `CONTRIBUTING.md` entirely with pointers

Rejected. `CONTRIBUTING.md` is the standard GitHub entry point for contributors and is consumed by GitHub's UI directly; a pointer-only file would violate that convention. The slim-down keeps the contributor-specific content (ground rules, branches, PR expectations, worktrees) and points out only the duplicated sections.

## Confirmation

This decision is reflected in the repository when:

- `README.md` Project Map is restructured into "Start Here", "How We Work (spec-driven)", and "Working With AI" sections covering `docs/DESIGN.md`, `docs/LIFECYCLE.md`, `ROADMAP.md`, `docs/decisions/`, `docs/requirements/`, `docs/specs/`, and `.agents/plans/`.
- `docs/LIFECYCLE.md`, `docs/decisions/README.md`, `docs/requirements/README.md`, and `docs/specs/README.md` exist and stay thin (each well under 100 lines and pointing back to its template plus the AI owner guide).
- `SETUP.md` no longer contains deploy, operate, monitor, rollback, post-deploy smoke, restore drill, Helm, Kubernetes, or release-artifact-verification sections, and links those topics out to `docs/RUNBOOK.md` and `docs/CI.md`.
- `docs/RUNBOOK.md` and `docs/CI.md` exist, own their respective scopes, and are linked from the `README.md` Start Here group.
- `CONTRIBUTING.md` no longer restates AI working model, validation commands, release-prep steps, or commit footer fields; those sections are pointer-only.
- `WORKING_WITH_AI.md` exposes the "what this repo treats as a spec" section and a top-of-file lifecycle pointer.
- `pwsh ./scripts/docs/audit-docs.ps1` passes after each migration step.

Until all migration steps land, partial confirmation is acceptable: each completed step independently passes `audit-docs.ps1` and leaves the documentation set internally consistent.

## Links

- `README.md`
- `SETUP.md`
- `CONTRIBUTING.md`
- `WORKING_WITH_AI.md`
- `docs/DESIGN.md`
- `ROADMAP.md`
- `.agents/references/application-lifecycle.md`
- `.agents/references/documentation.md`
- `.agents/references/planning.md`
- `.agents/references/plan-execution.md`
- `.agents/references/execution.md`
- `.agents/references/releases.md`
- `.agents/references/testing.md`
- `.agents/references/references-rules.md`
- `docs/decisions/ADR_TEMPLATE.md`
- `docs/requirements/PRD_TEMPLATE.md`
- `docs/specs/SPEC_TEMPLATE.md`
- `docs/decisions/0005-adopt-operations-and-deployment-owner-guide.md`
- `scripts/docs/audit-docs.ps1`
