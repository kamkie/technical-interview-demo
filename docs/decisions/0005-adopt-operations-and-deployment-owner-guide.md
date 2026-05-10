# 0005 Adopt Operations And Deployment Owner Guide

## Status

Proposed on 2026-05-10. Deferred until real Deployment or Operations behavior ships.

## Date

2026-05-10

## Context

`.agents/references/application-lifecycle.md` lists Deployment and Operations as phases without owners and marks the cross-cutting `Rollback`, `Hotfix`, `Patch`, `Backport`, and `Deprecate` triggers as "gap until Deployment/Operations guidance exists".
`.agents/references/releases.md` stops at "candidate is integrated on `main`" and explicitly hands off; nothing in the AI guidance set owns what happens after artifacts ship.

`AGENTS.md` `## Documents Map` `### Domain Guides` therefore cannot route post-release work, and `.agents/references/documentation.md` `## Artifact Ownership` cannot register an owner for the cross-cutting triggers.
The result is a known routing hole: agents reaching a deploy, post-deploy verification, rollback, or hotfix activity have no single guide to load and fall back to ad hoc reasoning.

The repository currently has no live deploy target, no observability stack wired to the AI workflow, and no incident history to encode.
Writing the owner guide before any of that exists would either invent policy speculatively or stay so abstract it adds context-load cost without adding routing value.

This decision needs to record *which shape* the future owner guide will take and *which trigger* moves it from deferred to active, so the gap is closed by intent now and by content later without re-deciding the shape.

This decision is AI workflow architecture, not product runtime behavior.
It does not change application code, public APIs, release sequencing, or validation requirements by itself.

## Decision

Adopt a single combined owner guide at `.agents/references/operations.md` for the Deployment and Operations phases of `application-lifecycle.md` and for the cross-cutting `Rollback`, `Hotfix`, `Patch`, `Backport`, and `Deprecate` triggers.

Scope and behavior of the future guide:

- Owner statement on line 1 declaring scope per `.agents/references/references-rules.md`.
- Loaded on demand only when a task enters Deployment or Operations activities, or one of the named cross-cutting triggers.
- Not loaded during integration or release authoring; that work stays with `releases.md`, `release-checklist.md`, and `release-artifact-verification.md`.
- Names which validation applies to each activity but defers command shape to `.agents/references/testing.md`.
- Keeps branch mechanics in `.agents/references/workflow.md`; `operations.md` owns *when* to invoke hotfix or rollback flows.
- Routes durable lessons to `.agents/references/LEARNINGS.md` via a Capture-Learning step in the Operations loop.

Recommended skeleton (≈150–220 lines):

- `## When To Load` — Deployment, Operations, and cross-cutting trigger entry conditions; explicit non-load list for integration and release authoring.
- `## Deployment Loop` — `Stage -> Smoke -> Promote -> Verify -> Rollback?` with owners and exit criteria per step.
- `## Operations Loop` — `Observe -> Operational-Triage -> Hotfix? -> Capture-Learning` with signal sources, severity ladder, and escalation.
- `## Cross-Cutting Triggers` — decision rules for `Hotfix` vs `Patch` vs `Backport` vs `Deprecate` and the spec or contract artifacts each one must touch.
- `## Validation Expectations` — per-activity validation pointers into `testing.md`.
- `## Cross-References` — `application-lifecycle.md`, `releases.md`, `workflow.md`, `testing.md`, `reviews.md`, `LEARNINGS.md`.

Activation trigger: undefer this decision when the first roadmap item crosses the current `releases.md` handoff line — for example, the first time a release is staged to an environment the agent is expected to verify, or the first user-requested hotfix path.
Until that trigger fires, this ADR remains Proposed and no `.agents/references/operations.md` file is created.

When activated, land the new owner guide and the ripple edits in a single focused change:

- `AGENTS.md` `## Documents Map` `### Domain Guides`: add a routing bullet for deployment, post-release verification, rollback, incident response, and hotfix or patch or backport decisions pointing to `operations.md`.
- `AGENTS.md` `## Integration And Release Invariants`: extend with a sentence pointing post-release work at `operations.md`.
- `.agents/references/documentation.md` `## Artifact Ownership`: register `operations.md` with its scope, including a routing row for the cross-cutting triggers.
- `.agents/references/application-lifecycle.md`:
  - `## Owner-Guide Adoption`: assign Deployment and Operations rows to `operations.md`.
  - `## Current Gaps`: drop the two phase-gap bullets and the `Hotfix` or `Rollback` "gap until …" markers.
  - `## Cross-Cutting Triggers`: replace the gap markers with owner pointers to `operations.md`.
- `.agents/references/releases.md`: add a one-line "Handoff to `operations.md` once promotion starts" pointer at the end of the release sequence.
- `.agents/references/workflow.md`: if hotfix branching is named, add a one-line pointer to `operations.md` from the branch-rules section.
- `.agents/references/references-rules.md`: no change expected; existing placement and naming rules already cover the new file.
- `CHANGELOG.md`: no entry; AI-guidance-only changes do not ship in the product changelog per current convention.

## Consequences

Benefits:

- Closes the open lifecycle gap so post-release work has a single, on-demand owner.
- Reduces `application-lifecycle.md` `## Current Gaps` from five items to one (the genuinely cross-cutting Outer Product Loop).
- Gives every cross-cutting trigger (`Rollback`, `Hotfix`, `Patch`, `Backport`, `Deprecate`) a registered owner in `documentation.md`.
- Preserves separation of concerns: `releases.md` owns publication, `operations.md` owns post-publication, `workflow.md` keeps branch mechanics, `testing.md` keeps validation commands.
- Decision is recorded now while content stays deferred, so the shape will not be re-litigated when behavior actually ships.

Costs or risks:

- Until activation, the gap remains visible in `application-lifecycle.md` and the routing bullet in `AGENTS.md` is still missing.
- Combined ownership risks a single guide growing beyond its useful size if Deployment and Operations both expand quickly; mitigated by the activation trigger and by the option to split later (see Alternative B).
- Six ripple edits across high-traffic guidance files mean the activation change is medium risk rather than a one-file edit.

Required follow-up changes:

- None until the activation trigger fires.
- On activation: create `.agents/references/operations.md` and apply the ripple edits listed under Decision.

## Alternatives Considered

### Shape B — split into `.agents/references/deployment.md` plus `.agents/references/operations.md`

Same total content, split along the phase boundary.
Rejected because the current demo scope does not justify two files; splitting now would over-fragment guidance and force agents loading post-release context to load both anyway.
This ADR does not preclude a future split when each side independently exceeds roughly 10 KB; that would be a follow-up ADR superseding this one.

### Status quo — leave Deployment and Operations unowned

Rejected because `application-lifecycle.md` already advertises these phases and the cross-cutting triggers, and the gap markers are visible.
Leaving them unowned forces every post-release task to invent routing, which contradicts the spec-priority and artifact-ownership rules in `AGENTS.md`.

### Fold post-release content into `releases.md`

Rejected because `releases.md` is intentionally scoped to publication sequencing and is loaded only for release authoring.
Mixing post-release operations into it would either inflate its load cost during release work or hide operations content behind a guide most operations tasks should not be loading.

## Confirmation

This decision is reflected in the repository when the activation trigger fires and:

- `.agents/references/operations.md` exists with the owner statement, on-demand load rules, Deployment loop, Operations loop, cross-cutting trigger rules, validation pointers, and cross-references listed under Decision.
- `AGENTS.md` `## Documents Map` `### Domain Guides` routes deployment, post-release verification, rollback, incident response, and hotfix or patch or backport decisions to `operations.md`.
- `.agents/references/documentation.md` `## Artifact Ownership` registers `operations.md` and maps the cross-cutting triggers to it.
- `.agents/references/application-lifecycle.md` `## Owner-Guide Adoption`, `## Current Gaps`, and `## Cross-Cutting Triggers` reference `operations.md` instead of gap markers.
- `.agents/references/releases.md` ends the release sequence with a handoff pointer to `operations.md`.

Until the activation trigger fires, the only confirmation expected is the presence of this ADR with status Proposed and Deferred.

## Links

- `.agents/references/application-lifecycle.md`
- `.agents/references/releases.md`
- `.agents/references/workflow.md`
- `.agents/references/documentation.md`
- `.agents/references/references-rules.md`
- `.agents/references/testing.md`
- `.agents/references/reviews.md`
- `.agents/references/LEARNINGS.md`
- `docs/decisions/0002-align-lifecycle-vocabulary-with-industry-practice.md`
- `docs/decisions/0004-adopt-skill-first-multi-agent-workflow.md`
