# Plan: AI Guideline Plan C Trigger Tightening

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Closed |
| Status | Released |

## Summary
- Implement Option C from `ai/references/AI_GUIDELINES_REMIX_EVALUATION.md`: keep the current AI owner-file set and tighten section-level load triggers instead of splitting or bundling files.
- Add lookup-only entry-point guidance to `ai/DOCUMENTATION.md` and `ai/ARCHITECTURE.md`.
- Tighten `AGENTS.md` lifecycle routing so descriptive guides stay out of routine implementation and review loads unless their trigger conditions apply.
- Success means agents can use smaller read sets for pure ownership lookup, structural-only code reading, and design-sensitive work without adding new files or cross-reference debt.

## Scope
### In scope
- Add a short lookup-only entry point near the top of `ai/DOCUMENTATION.md`.
- Add a short structural-read entry point near the top of `ai/ARCHITECTURE.md`.
- Tighten the `AGENTS.md` `## Lifecycle Owner Map` wording so `ai/ARCHITECTURE.md`, `ai/DESIGN.md`, and `ai/LEARNINGS.md` remain explicitly conditional.
- Preserve `ai/DOCUMENTATION.md` as the artifact-routing and AI-document maintenance owner.
- Keep the current owner-guide file set intact.
- Update `ROADMAP.md` so this plan is visible as active planned work.

### Out of scope
- Creating `_REF.md`, `_EDIT.md`, or lifecycle-bundled AI guidance files.
- Restructuring `ai/CODE_STYLE.md`, `ai/DESIGN.md`, `ai/LEARNINGS.md`, or imperative phase guides.
- Rewriting archived plans, historical release notes, or unrelated prompt bodies.
- Changing application source, public API behavior, tests, REST Docs, OpenAPI, HTTP examples, setup behavior, build logic, or release versioning.
- Implementing broader AI-guidance compaction beyond Option C.

## Current State
- `ai/references/AI_GUIDELINES_REMIX_EVALUATION.md` recommends Option C because common lifecycle phases do not benefit materially from reference/edit file splits or lifecycle bundles.
- `AGENTS.md` is the default AI entry point and already has a lifecycle owner map, but descriptive guide triggers can be sharper.
- `ai/DOCUMENTATION.md` is already organized around artifact ownership, change-type routing, alignment, common routing, and cross-references.
- `ai/ARCHITECTURE.md` contains both descriptive structure and architecture-sensitive change rules. The evaluation report's `System Snapshot` and `Package Layout` wording maps to the current `## System Purpose` and `## Package Shape` headings.
- `ai/DESIGN.md` and `ai/LEARNINGS.md` are already conditional descriptive guides and should remain on demand.
- No application behavior or public contract is affected by this plan.

## Requirement Gaps And Open Questions
- No material user input blocks planning.
- Exact final wording is not locked.
  Fallback: use minimal pointer paragraphs that name the narrow sections to load and preserve the existing owner-file model.
- If implementation finds stale cross-references caused by the trigger wording, update only the affected active docs in the same change.

## Locked Decisions And Assumptions
- Adopt Option C from `ai/references/AI_GUIDELINES_REMIX_EVALUATION.md`.
- Do not create new AI owner files or bundle existing owner files by lifecycle.
- Keep `AGENTS.md` as the only default AI guidance file.
- Keep `ai/DOCUMENTATION.md` as the owner for artifact routing and AI-document maintenance.
- Keep `ai/ARCHITECTURE.md`, `ai/DESIGN.md`, and `ai/LEARNINGS.md` descriptive and conditional.
- Use current-state guidance, not historical explanation, in the modified owner files.

## Execution Mode Fit
- Selected execution mode: `Linear Plan`.
- This is a small documentation-only AI-guidance change across shared owner files; fanout would add coordination cost and increase wording drift risk.
- Coordinator-owned files:
  - `ai/PLAN_ai_guidelines_plan_c_triggers.md`
  - `ROADMAP.md`
  - `AGENTS.md`
  - `ai/DOCUMENTATION.md`
  - `ai/ARCHITECTURE.md`
- Candidate worker boundaries: none recommended. If delegation is later forced, keep all owned files above coordinator-owned.

## Affected Artifacts
- Planning and roadmap:
  - `ai/PLAN_ai_guidelines_plan_c_triggers.md`
  - `ROADMAP.md`
- AI guidance implementation files:
  - `AGENTS.md`
  - `ai/DOCUMENTATION.md`
  - `ai/ARCHITECTURE.md`
- Source recommendation:
  - `ai/references/AI_GUIDELINES_REMIX_EVALUATION.md`
- Application specs and contracts:
  - none; no source, tests, REST Docs, OpenAPI, HTTP examples, README contract, benchmark, or runtime behavior changes are planned.

## Execution Milestones
### Milestone 1: Plan And Roadmap Tracking
- goal: capture Option C as a concrete repo-native execution plan and track it as active planned work.
- owned files or packages:
  - `ai/PLAN_ai_guidelines_plan_c_triggers.md`
  - `ROADMAP.md`
- shared files that a `Single-Plan Fanout` worker must leave to the coordinator:
  - not applicable in `Linear Plan`
- context required before execution:
  - `AGENTS.md`, `ai/PLAN.md`, `ai/DOCUMENTATION.md`, `ROADMAP.md`, and `ai/references/AI_GUIDELINES_REMIX_EVALUATION.md`
- behavior to preserve:
  - no implementation of the trigger changes in this milestone
  - no application or public-contract changes
- exact deliverables:
  - this plan exists under `ai/`
  - `ROADMAP.md` points to this plan with a concise active-work row
- validation checkpoint:
  - manual plan-shape check against `ai/PLAN.md`
  - `git diff --check -- ai/PLAN_ai_guidelines_plan_c_triggers.md ROADMAP.md`
- commit checkpoint:
  - `docs: plan ai guideline plan c trigger tightening`

### Milestone 2: Documentation And Architecture Entry Points
- goal: add section-level triggers to the split-friendly owner files without creating new files.
- owned files or packages:
  - `ai/DOCUMENTATION.md`
  - `ai/ARCHITECTURE.md`
- shared files that a `Single-Plan Fanout` worker must leave to the coordinator:
  - all owned files; fanout is not recommended
- context required before execution:
  - `ai/DOCUMENTATION.md`, `ai/ARCHITECTURE.md`, this plan, and Option C in `ai/references/AI_GUIDELINES_REMIX_EVALUATION.md`
- behavior to preserve:
  - `ai/DOCUMENTATION.md` remains the artifact-routing owner
  - `ai/ARCHITECTURE.md` remains the compact descriptive repository snapshot and structural guidance owner
  - detailed maintenance rules stay on demand
- exact deliverables:
  - `ai/DOCUMENTATION.md` tells agents that pure artifact-location lookup can read only `## Artifact Ownership`
  - `ai/DOCUMENTATION.md` tells agents that AI-document edits should use its AI-document maintenance subsection before editing AI guidance files
  - `ai/ARCHITECTURE.md` tells agents that structural-only reads can load only `## System Purpose` and `## Package Shape`
  - `ai/ARCHITECTURE.md` tells agents to continue into architecture rules only for architecture-sensitive edits
- validation checkpoint:
  - `rg -n "^## |^### |lookup-only|structural" ai/DOCUMENTATION.md ai/ARCHITECTURE.md`
  - `git diff --check -- ai/DOCUMENTATION.md ai/ARCHITECTURE.md`
- commit checkpoint:
  - `docs: add section-level ai guide entry points`

### Milestone 3: Lifecycle Owner Map Trigger Tightening
- goal: make `AGENTS.md` explicitly keep descriptive guides out of routine loads unless their trigger applies.
- owned files or packages:
  - `AGENTS.md`
- shared files that a `Single-Plan Fanout` worker must leave to the coordinator:
  - all owned files; fanout is not recommended
- context required before execution:
  - `AGENTS.md`, this plan, and Option C in `ai/references/AI_GUIDELINES_REMIX_EVALUATION.md`
- behavior to preserve:
  - `AGENTS.md` remains concise enough for default loading
  - lifecycle routing still tells agents which owner guide to load for planning, execution, review, integration, and release work
  - `ai/DOCUMENTATION.md` remains the detailed owner for artifact routing
- exact deliverables:
  - routine implementation guidance does not imply default loading of `ai/ARCHITECTURE.md`, `ai/DESIGN.md`, or `ai/LEARNINGS.md`
  - discovery and roadmap intake wording loads `ai/DESIGN.md` only for user-visible behavior, supported scope, security posture, or roadmap tradeoffs
  - structural-code wording loads `ai/ARCHITECTURE.md` only when structure or architecture-sensitive changes matter
  - `ai/LEARNINGS.md` remains tied to targeted relevance scans or known recurring lessons
- validation checkpoint:
  - `rg -n "ARCHITECTURE|DESIGN|LEARNINGS|routine|conditional|Lifecycle Owner Map" AGENTS.md`
  - `git diff --check -- AGENTS.md`
- commit checkpoint:
  - `docs: tighten ai lifecycle owner triggers`

### Milestone 4: Final Consistency Check
- goal: prove Option C was implemented without file-set churn or duplicated policy.
- owned files or packages:
  - `ai/PLAN_ai_guidelines_plan_c_triggers.md`
  - `ROADMAP.md`
  - `AGENTS.md`
  - `ai/DOCUMENTATION.md`
  - `ai/ARCHITECTURE.md`
- shared files that a `Single-Plan Fanout` worker must leave to the coordinator:
  - not applicable in `Linear Plan`
- context required before execution:
  - `ai/DOCUMENTATION.md`, `ai/TESTING.md`, `ai/REVIEWS.md`, touched docs, and this plan
- behavior to preserve:
  - no `_REF.md`, `_EDIT.md`, or lifecycle bundle files are added
  - no completed work is represented as released
  - no application or public-contract artifacts change
- exact deliverables:
  - this plan records final validation results
  - `ROADMAP.md` reflects the post-implementation active status
  - final status summarizes the tightened triggers and confirms no new owner files were created
- validation checkpoint:
  - `rg --files ai | rg "_REF\\.md$|_EDIT\\.md$|CONTEXT\\.md$"`
  - `rg -n "^## |^### |lookup-only|structural|Lifecycle Owner Map" AGENTS.md ai/DOCUMENTATION.md ai/ARCHITECTURE.md`
  - `git diff --check -- AGENTS.md ai/DOCUMENTATION.md ai/ARCHITECTURE.md ai/PLAN_ai_guidelines_plan_c_triggers.md ROADMAP.md`
  - `./build.ps1 build` if `ai/TESTING.md` or the wrapper requires validation beyond docs-only diff checks
- commit checkpoint:
  - `docs: implement ai guideline plan c trigger tightening`

## Edge Cases And Failure Modes
- Adding too much detail to `AGENTS.md` would increase default context load and undermine Option C.
- Creating new files would accidentally implement Option A or Option B, not Option C.
- Duplicating the same trigger wording across multiple guides would reintroduce the drift risk this plan is meant to avoid.
- Moving `ai/DOCUMENTATION.md` maintenance details back into `AGENTS.md` would make on-demand loading worse.
- Over-tightening triggers could hide required guidance from agents doing architecture-sensitive or design-sensitive changes.

## Validation Plan
- Use targeted heading and trigger searches to confirm the intended entry points exist.
- Use `git diff --check` for modified markdown files.
- Confirm no `_REF.md`, `_EDIT.md`, or lifecycle bundle files were introduced.
- Run `./build.ps1 build` only if the repo's current testing guidance or wrapper requires it for docs-only AI-guidance edits.

## Testing Strategy
- Unit tests: not applicable; no application logic changes.
- Integration tests: not applicable; no runtime behavior changes.
- Contract tests: not applicable; no public API, REST Docs, OpenAPI, or HTTP example changes.
- Documentation verification: targeted heading, trigger, and stale-file searches plus whitespace checks.
- Manual verification: read the modified top sections and lifecycle map to confirm the trigger rules are clear and non-duplicative.

## Better Engineering Notes
- This plan intentionally targets the low-risk improvement from the evaluation report instead of optimizing for rare lookup-only savings with extra files.
- Re-evaluate larger remix ideas only after active plan archival and release cleanup reduce the larger context drivers.
- Keep any future wording changes short; the point is section-level routing, not another standing runbook.

## Validation Results
- 2026-05-07 Milestone 1:
  - Created this plan under `ai/`.
  - Confirmed `ROADMAP.md` tracks `ai/PLAN_ai_guidelines_plan_c_triggers.md` in the active release track.
  - Ran `git diff --check -- ai/PLAN_ai_guidelines_plan_c_triggers.md ROADMAP.md`; passed.
- 2026-05-07 Milestone 2:
  - Added entry points to `ai/DOCUMENTATION.md` for artifact-location lookup, AI-document edits, and broader documentation changes.
  - Added entry points to `ai/ARCHITECTURE.md` for structural-only reads and architecture-sensitive edits.
  - Updated this plan to use the current `ai/ARCHITECTURE.md` headings: `## System Purpose` and `## Package Shape`.
  - Ran `rg -n "^## |^### |Entry points|artifact-location|structural|architecture-sensitive" ai/DOCUMENTATION.md ai/ARCHITECTURE.md`; passed.
  - Ran `git diff --check -- ai/DOCUMENTATION.md ai/ARCHITECTURE.md ai/PLAN_ai_guidelines_plan_c_triggers.md`; passed.
- 2026-05-07 Milestone 3:
  - Tightened `AGENTS.md` lifecycle owner map wording so descriptive guides remain conditional during routine implementation and review.
  - Added explicit conditional triggers for `ai/ARCHITECTURE.md`, `ai/DESIGN.md`, and `ai/LEARNINGS.md`.
  - Ran `rg -n "ARCHITECTURE|DESIGN|LEARNINGS|routine|conditional|Lifecycle Owner Map|Conditional descriptive" AGENTS.md`; passed.
  - Ran `git diff --check -- AGENTS.md`; passed.
- 2026-05-07 Milestone 4:
  - Marked this plan `Phase=Integration` / `Status=Implemented`.
  - Marked the roadmap row for this plan `Implemented`.
  - Recorded the unreleased AI-guidance change in `CHANGELOG.md`.
  - Ran `rg -n "^## |^### |Entry points|artifact-location|structural|Lifecycle Owner Map|Conditional descriptive" AGENTS.md ai/DOCUMENTATION.md ai/ARCHITECTURE.md`; passed.
  - Ran a refined no-split-file check excluding the pre-existing `ai/ENVIRONMENT_QUICK_REF.md`; no new `_REF.md`, `_EDIT.md`, or `CONTEXT.md` files were found.
  - Ran `git diff --check -- AGENTS.md ai/DOCUMENTATION.md ai/ARCHITECTURE.md ai/PLAN_ai_guidelines_plan_c_triggers.md ROADMAP.md CHANGELOG.md`; passed.
  - Ran `./build.ps1 build`; passed via the lightweight-only shortcut with message `Only lightweight files changed; skipping Gradle build.`

## User Validation
- Verify that `AGENTS.md` no longer implies descriptive guides are part of routine implementation or review loads.
- Verify that `ai/DOCUMENTATION.md` has a clear artifact-lookup entry point and AI-document maintenance trigger.
- Verify that `ai/ARCHITECTURE.md` has a clear structural-read entry point.
- Verify that no new AI owner files were created.

## Required Content Checklist
- behavior changing and why: Option C tightens section-level AI-guidance load triggers to reduce context use without file-set churn.
- roadmap tracking: `ROADMAP.md` active release track points to `ai/PLAN_ai_guidelines_plan_c_triggers.md`.
- out of scope: no new split or bundle files, no application behavior, no public contract changes.
- governing artifacts: `ai/references/AI_GUIDELINES_REMIX_EVALUATION.md`, `AGENTS.md`, `ai/DOCUMENTATION.md`, `ai/ARCHITECTURE.md`, and `ROADMAP.md`.
- likely files: `AGENTS.md`, `ai/DOCUMENTATION.md`, `ai/ARCHITECTURE.md`, this plan, and `ROADMAP.md`.
- compatibility promises: preserve default `AGENTS.md` entry point, owner clarity, and current AI file set.
- risks: default-load growth, duplicate trigger wording, accidental file-set split, stale cross-references.
- requirement gaps: no blocking gaps; fallback is minimal pointer wording.
- execution mode: `Linear Plan`.
- coordinator-owned files: all affected files listed in `Execution Mode Fit`.
- per-milestone context: named explicitly in each milestone.
- specs and docs: AI guidance and roadmap only; no app specs or public contract artifacts.
- validation: targeted `rg` checks, `git diff --check`, and build only if required for docs-only edits.
- user verification: review trigger wording and confirm no new owner files.
