# Plan: AI Guideline Duplication Compaction

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Planning |
| Status | Ready |

## Summary
- Compact repeated AI-guidance policy found across `AGENTS.md`, `WORKING_WITH_AI.md`, and the top-level `ai/*.md` owner guides.
- Preserve the current on-demand guidance model: `AGENTS.md` stays the default AI entry point, owner guides keep phase-specific detail, and deep references remain on demand.
- Success means the same rules are stated once in the best owner, repeated sections become links or short reminders, and agents still know which file to load for each lifecycle phase.

## Scope
### In scope
- Add or refine a compact lifecycle-to-owner-guide map in `AGENTS.md` if it can replace repeated read-set guidance elsewhere without growing the default load materially.
- Slim `WORKING_WITH_AI.md` so it remains a human-facing guide and stops restating full prompt lists, workflow-mode details, validation details, and release mechanics already owned by AI guides.
- Reduce repeated artifact-routing prose in phase guides by pointing to `ai/DOCUMENTATION.md` as the detailed owner.
- Reduce repeated wrapper and validation mechanics by keeping wrapper behavior in `ai/ENVIRONMENT_QUICK_REF.md` and required proof in `ai/TESTING.md`.
- Reduce repeated release and `main` branch boundary prose by keeping repo-level invariants in `AGENTS.md`, workflow handoff in `ai/WORKFLOW.md`, and release preconditions in `ai/RELEASES.md`.
- Update cross-references, prompt metadata, or README/CONTRIBUTING pointers only if the compaction changes an owning document role or link target.
- Update `CHANGELOG.md` under `## [Unreleased]` after the compaction is implemented.

### Out of scope
- Application source, tests, REST Docs, OpenAPI, HTTP examples, runtime behavior, setup walkthroughs, build scripts, or release versioning changes.
- Rewriting archived plans or historical release notes to match the compacted wording.
- Moving detailed prompt bodies, release references, workflow fanout mechanics, or plan templates into `AGENTS.md`.
- Changing prompt titles or workflow mode names unless a stale reference is found during validation.
- Creating new top-level AI owner guides.

## Current State
- The current guidance set is coherent and has no blocking contradictions.
- `AGENTS.md` is the always-loaded AI entry point and already owns spec priority, document inventory, instruction load policy, high-level artifact update rules, branch and release invariants, and definition of done.
- The main duplication clusters are:
  - phase and read-set routing repeated in `AGENTS.md`, `WORKING_WITH_AI.md`, `ai/PROMPTS.md`, and `ai/EXECUTION.md`
  - artifact-routing and public-behavior-change rules repeated in `AGENTS.md`, `ai/DOCUMENTATION.md`, `ai/PLAN.md`, `ai/TESTING.md`, and `ai/EXECUTION.md`
  - prompt-title and lifecycle workflow detail repeated between `WORKING_WITH_AI.md` and `ai/PROMPTS.md`
  - validation wrapper mechanics repeated across `AGENTS.md`, `ai/ENVIRONMENT_QUICK_REF.md`, `ai/TESTING.md`, `ai/EXECUTION.md`, and `WORKING_WITH_AI.md`
  - release and `main` branch boundaries repeated across `AGENTS.md`, `ai/WORKFLOW.md`, `ai/RELEASES.md`, `ai/EXECUTION.md`, and `WORKING_WITH_AI.md`
- `ai/references/AI_GUIDELINES_POST_COMPACTION_EVALUATION.md` already records the broader post-compaction baseline and should remain an on-demand reference.
- `ROADMAP.md` tracks this plan under `Ordered Plan` / `Moving to 2.0` / `AI Workflow Guidance`.

## Requirement Gaps And Open Questions
- The user has not requested implementation yet, only this plan.
  Fallback: keep this plan in `Planning` / `Ready` and stop after plan creation and roadmap tracking.
- Exact wording for the future compact lifecycle map is not locked.
  Fallback: implement the smallest map that lets other files remove repeated read-set prose without adding detailed runbook content to `AGENTS.md`.
- If compaction reveals a rule that exists only in a repeated section, preserve it in the best owning guide before deleting the duplicate.
- If a cross-reference target changes in a way that affects human-facing docs, update the human-facing doc in the same implementation change.

## Locked Decisions And Assumptions
- `AGENTS.md` remains the only default AI guidance file.
- `ai/DOCUMENTATION.md` remains the detailed owner for artifact routing and cross-file alignment.
- `ai/PROMPTS.md` remains the complete prompt-title index; `WORKING_WITH_AI.md` may show examples but should not duplicate the whole prompt catalog.
- `ai/ENVIRONMENT_QUICK_REF.md` owns wrapper behavior; `ai/TESTING.md` owns validation scope.
- `ai/WORKFLOW.md` owns branch, worktree, delegation, and integration mechanics; `ai/RELEASES.md` owns intentional release sequencing after work is on `main`.
- Detailed examples, prompt bodies, templates, historical analysis, archived plans, release references, and workflow fanout references remain on demand.

## Execution Mode Fit
- Selected execution mode: `Linear Plan`.
- The work is documentation-only with overlapping shared files, so fanout would add coordination cost and increase wording drift risk.
- Coordinator-owned files:
  - `ai/PLAN_ai_guideline_duplication_compaction.md`
  - `ROADMAP.md`
  - `CHANGELOG.md`
  - `AGENTS.md`
  - `WORKING_WITH_AI.md`
  - affected top-level `ai/*.md` owner guides
- Candidate worker boundaries: none recommended. If the user later requests delegation, keep `AGENTS.md`, `WORKING_WITH_AI.md`, `ai/DOCUMENTATION.md`, `ai/PROMPTS.md`, `ROADMAP.md`, `CHANGELOG.md`, and this plan coordinator-owned.

## Affected Artifacts
- Planning and roadmap:
  - `ai/PLAN_ai_guideline_duplication_compaction.md`
  - `ROADMAP.md`
- Likely AI-guidance implementation files:
  - `AGENTS.md`
  - `WORKING_WITH_AI.md`
  - `ai/PROMPTS.md`
  - `ai/EXECUTION.md`
  - `ai/PLAN.md`
  - `ai/TESTING.md`
  - `ai/WORKFLOW.md`
  - `ai/RELEASES.md`
  - `ai/DOCUMENTATION.md`
  - `ai/ENVIRONMENT_QUICK_REF.md`
- Possible human-facing pointer updates:
  - `README.md`
  - `CONTRIBUTING.md`
- Unreleased history during implementation:
  - `CHANGELOG.md`
- Application specs and contracts:
  - none; no public API, REST Docs, OpenAPI, HTTP example, source, build, benchmark, or runtime behavior changes are planned.

## Execution Milestones
### Milestone 0: Plan Creation And Roadmap Tracking
- status: complete when this plan and `ROADMAP.md` are updated.
- goal: capture the duplication findings as a concrete implementation plan.
- owned files or packages:
  - `ai/PLAN_ai_guideline_duplication_compaction.md`
  - `ROADMAP.md`
- shared files that a `Single-Plan Fanout` worker must leave to the coordinator:
  - not applicable in `Linear Plan`
- context required before execution:
  - `AGENTS.md`, `ai/PLAN.md`, `ai/DOCUMENTATION.md`, `ROADMAP.md`, and the user's duplication findings request
- behavior to preserve:
  - no AI-guidance implementation changes in this milestone
  - no application or public-contract changes
- exact deliverables:
  - plan file exists under `ai/`
  - roadmap entry points to this plan and its lifecycle state
- validation checkpoint:
  - manual plan-shape check against `ai/PLAN.md`
  - `git diff --check`
  - `./build.ps1 build` lightweight-file shortcut or full build if the wrapper requires it
- commit checkpoint:
  - `docs: plan ai guideline duplication compaction`

### Milestone 1: Owner Map And Read-Set Compaction
- goal: make one compact owner map authoritative enough that phase guides can stop repeating broad read-set lists.
- owned files or packages:
  - `AGENTS.md`
  - `ai/PROMPTS.md`
  - `ai/EXECUTION.md`
  - this plan's validation results
- shared files that a `Single-Plan Fanout` worker must leave to the coordinator:
  - all owned files; fanout is not recommended
- context required before execution:
  - `AGENTS.md`, `ai/PROMPTS.md`, `ai/EXECUTION.md`, `ai/DOCUMENTATION.md`, and this plan's `Current State`
- behavior to preserve:
  - `AGENTS.md` remains concise and does not absorb detailed runbooks
  - `ai/PROMPTS.md` remains the prompt index, not a policy dump
  - `ai/EXECUTION.md` still names the smallest useful execution read set
- exact deliverables:
  - one compact lifecycle-to-owner-guide map exists in the best owner, preferably `AGENTS.md`
  - repeated phase/read-set prose in `ai/PROMPTS.md` and `ai/EXECUTION.md` is reduced to links or narrow triggers where possible
  - cross-references still make clear when descriptive docs and on-demand references should be loaded
- validation checkpoint:
  - targeted search for repeated read-set lists and stale guide names
  - manual consistency check against `ai/DOCUMENTATION.md`
  - `git diff --check`
- commit checkpoint:
  - `docs: compact ai owner read-set guidance`

### Milestone 2: Human-Facing Workflow Slimming
- goal: keep `WORKING_WITH_AI.md` useful for developers while removing duplicated AI-owner policy.
- owned files or packages:
  - `WORKING_WITH_AI.md`
  - possible pointer updates in `README.md` or `CONTRIBUTING.md`
  - this plan's validation results
- shared files that a `Single-Plan Fanout` worker must leave to the coordinator:
  - all owned files; fanout is not recommended
- context required before execution:
  - `WORKING_WITH_AI.md`, `ai/PROMPTS.md`, `ai/WORKFLOW.md`, `ai/TESTING.md`, `ai/RELEASES.md`, `README.md`, `CONTRIBUTING.md`, and this milestone
- behavior to preserve:
  - human maintainers still have a clear entry point for using AI
  - full prompt catalog remains in `ai/PROMPTS.md`
  - workflow, validation, and release details remain in their AI owner guides
- exact deliverables:
  - `WORKING_WITH_AI.md` uses links and examples instead of duplicating full prompt lists and phase mechanics
  - any changed human-facing pointers stay aligned with the new wording
- validation checkpoint:
  - targeted search for duplicated prompt-title lists and workflow-mode summaries
  - manual human-facing readability check
  - `git diff --check`
- commit checkpoint:
  - `docs: slim human-facing ai workflow guide`

### Milestone 3: Routing, Validation, And Release Boundary Deduplication
- goal: remove repeated artifact-routing, wrapper, validation, branch, and release rules from files that do not own them.
- owned files or packages:
  - `ai/DOCUMENTATION.md`
  - `ai/PLAN.md`
  - `ai/TESTING.md`
  - `ai/EXECUTION.md`
  - `ai/WORKFLOW.md`
  - `ai/RELEASES.md`
  - `ai/ENVIRONMENT_QUICK_REF.md`
  - `AGENTS.md` only if a repo-level invariant needs compact adjustment
  - this plan's validation results
- shared files that a `Single-Plan Fanout` worker must leave to the coordinator:
  - all owned files; fanout is not recommended
- context required before execution:
  - `ai/DOCUMENTATION.md`, `ai/TESTING.md`, `ai/ENVIRONMENT_QUICK_REF.md`, `ai/WORKFLOW.md`, `ai/RELEASES.md`, `ai/EXECUTION.md`, `AGENTS.md`, and this milestone
- behavior to preserve:
  - no loss of required validation or release preconditions
  - `ai/DOCUMENTATION.md` remains the detailed artifact-routing owner
  - wrapper syntax and wrapper behavior stay in `ai/ENVIRONMENT_QUICK_REF.md`
  - release work remains gated on merged `main`
- exact deliverables:
  - repeated public-behavior routing lists are replaced with owner links except where a phase-specific consequence is necessary
  - repeated wrapper mechanics are replaced with references to the wrapper and validation owners
  - repeated release/main-branch prose is reduced to short boundary reminders
- validation checkpoint:
  - targeted searches for duplicated public-behavior artifact lists, wrapper behavior, and release-main gates
  - manual contradiction check with `ai/REVIEWS.md` documentation/process lens
  - `git diff --check`
- commit checkpoint:
  - `docs: deduplicate ai routing validation release rules`

### Milestone 4: Final Consistency, Tracking, And Closeout
- goal: prove the compacted guidance still routes agents correctly and close the plan's implementation state.
- owned files or packages:
  - this plan's lifecycle and validation results
  - `ROADMAP.md`
  - `CHANGELOG.md`
  - touched AI and human-facing docs from earlier milestones
- shared files that a `Single-Plan Fanout` worker must leave to the coordinator:
  - not applicable in `Linear Plan`
- context required before execution:
  - `ai/DOCUMENTATION.md`, `ai/REVIEWS.md`, `ai/TESTING.md`, `ROADMAP.md`, `CHANGELOG.md`, touched docs, and this milestone
- behavior to preserve:
  - no hidden application or public-contract changes
  - no completed work is represented as released
  - roadmap remains active-work-only
- exact deliverables:
  - `CHANGELOG.md` records the AI-guidance compaction under `## [Unreleased]`
  - `ROADMAP.md` reflects the final active state until release cleanup
  - this plan moves to `Phase=Integration` / `Status=Implemented`
  - final status summarizes what was compacted and where each policy now lives
- validation checkpoint:
  - targeted stale-reference and duplicate-policy searches across active docs, excluding `ai/archive/`
  - prompt loader smoke checks only if prompt metadata or titles changed
  - `git diff --check`
  - `./build.ps1 build`
- commit checkpoint:
  - `docs: close ai guideline duplication compaction`

## Edge Cases And Failure Modes
- Compaction can accidentally delete the only copy of a rule. Before removing repeated prose, confirm the rule exists in the owner file named by `ai/DOCUMENTATION.md`.
- Moving too much into `AGENTS.md` can increase the default load. Add only compact selectors and repo-level invariants there.
- Slimming `WORKING_WITH_AI.md` too aggressively can make the human-facing workflow less usable. Preserve examples and direct links even when removing full policy repeats.
- Prompt titles, anchors, and cross-reference targets can drift after wording changes. Validate links and prompt loader behavior if prompt metadata changes.
- Active plans may retain old wording as historical execution context. Do not rewrite active or archived plans unless the wording is current guidance that would mislead execution.

## Validation Plan
- Run targeted searches before and after implementation for:
  - repeated phase/read-set lists
  - repeated public-behavior artifact routing lists
  - repeated wrapper behavior text
  - repeated release/main-branch gate wording
  - stale prompt titles, workflow mode names, or moved anchors
- Run `git diff --check` after each milestone.
- Run `./build.ps1 build` for final validation; the lightweight-file shortcut is acceptable for docs-only and AI-guidance-only changes unless the wrapper requires a full build.
- Run prompt loader smoke checks only if prompt titles, prompt metadata, or prompt body filenames change.
- Manually review touched docs with `ai/DOCUMENTATION.md` and `ai/REVIEWS.md` to confirm owner alignment and no contradictory process guidance.

## Testing Strategy
- Unit tests: not applicable; no executable code changes.
- Integration tests: not applicable; no runtime, database, HTTP, OAuth, or service behavior changes.
- Contract tests: not applicable; no public API, REST Docs, OpenAPI, HTTP examples, or README contract behavior changes.
- Smoke or benchmark tests: not applicable; no runtime or performance behavior changes.
- Documentation validation: required through targeted search, cross-reference review, `git diff --check`, and the wrapper build's lightweight classifier.
- Prompt smoke checks: conditional, only if prompt invocation metadata changes.

## Better Engineering Notes
- The highest-value target is `WORKING_WITH_AI.md` because it is large, human-facing, and repeats prompt and lifecycle mechanics already owned elsewhere.
- `AGENTS.md` should get only compact universal routing if that lets other always-read or phase-read files delete duplicated lists.
- Keep `ai/references/AI_GUIDELINES_POST_COMPACTION_EVALUATION.md` on demand; do not turn the evaluation report into standing policy.
- If implementation finds a separate high-risk contradiction, stop and record it before continuing with cosmetic compaction.

## Validation Results
- 2026-05-07 plan creation:
  - Created this plan and added the matching selected active-work entry to `ROADMAP.md`.
  - Manual plan-shape review passed against `ai/PLAN.md`.
  - `git diff --check` passed.
  - `./build.ps1 build` passed through the lightweight-file shortcut, reporting only this plan and `ROADMAP.md` changed and skipping the Gradle build.

## User Validation
- Review the compacted `AGENTS.md` first to confirm the default AI entry point still gives enough routing context.
- Review `WORKING_WITH_AI.md` as a developer-facing guide and confirm it is easier to scan without losing useful examples.
- Spot-check the owner guides named in this plan to confirm each repeated rule now has one clear home.

## Required Content Checklist
- Behavior changing: AI-guidance structure and wording only; no application behavior changes.
- Roadmap tracking: `ROADMAP.md` / `Ordered Plan` / `Moving to 2.0` / `AI Workflow Guidance`.
- Out of scope: application source, public contract, setup runbooks, release versioning, archived historical plans, and new top-level AI owner guides.
- Specs or contract artifacts: AI-guidance owner files and human-facing AI collaboration docs; no executable application specs.
- Likely files: `AGENTS.md`, `WORKING_WITH_AI.md`, selected top-level `ai/*.md`, `ROADMAP.md`, and `CHANGELOG.md`.
- Compatibility promises: default AI load remains `AGENTS.md`; on-demand references remain on demand; public API contract is unchanged.
- Edge cases: rule deletion, `AGENTS.md` bloat, human-facing guide over-slimming, stale anchors, stale prompt metadata.
- Requirement gaps: implementation not requested yet; fallback is stop after plan creation.
- Execution mode: `Linear Plan`.
- Worker ownership: fanout not recommended; shared guidance files stay coordinator-owned if delegation is forced.
- Per-milestone context: named explicitly in each milestone.
- Artifacts to move: AI guidance and human-facing AI workflow docs only.
- Validation: targeted searches, manual owner-alignment review, `git diff --check`, final `./build.ps1 build`, prompt smoke checks only if prompt metadata changes.
- User verification: review default AI entry point, human-facing workflow guide, and owner-guide rule homes.
