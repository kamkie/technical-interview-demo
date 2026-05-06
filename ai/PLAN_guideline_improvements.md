# Plan: Implement AI Guideline Improvements

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Integration |
| Status | Implemented |

## Summary
- Update AI guidance documents to incorporate findings from the recent evaluation.
- Improve cross-platform compatibility, reinforce spec-driven development, and optimize for agent context management.

## Scope
### In scope
- Updates to `AGENTS.md`, `ai/RELEASES.md`, `ai/ENVIRONMENT_QUICK_REF.md`, `ai/REVIEWS.md`, `ai/PLAN.md`, and `ai/templates/PLAN_TEMPLATE.md`.
- Validation updates to this plan file.
- Adding "Context Hygiene" and "Quick Start" to `AGENTS.md`.
- Adding "Learnings Loop" to `ai/RELEASES.md`.
- Adding Bash/Sh equivalents to `ai/ENVIRONMENT_QUICK_REF.md`.
- Strengthening "Testing Strategy" and "Spec-Driven Development" across relevant files.

### Out of scope
- Changes to non-AI documentation (README, SETUP, etc.) unless strictly necessary for alignment.
- Changes to implementation code or actual build scripts.
- Roadmap updates unless this one-off guideline cleanup becomes active roadmap work again.

## Current State
- The guidelines are high quality (Grade A+) but have some identified gaps:
    - Heavily skewed towards PowerShell.
    - No explicit "learnings loop" after releases.
    - Context management is mostly implicit.
    - Testing strategy in plans could be more structured.
- The implementation already landed in commit `593c6bb` as AI-guidance documentation changes only.

## Requirement Gaps And Open Questions
- The original evaluation details are not stored as a repository artifact; this plan relies on the summarized findings in `Current State`.
- No product behavior, public API contract, setup behavior, or build-script behavior is intended to change.
- `ROADMAP.md` does not track this one-off guideline cleanup. That is a process deviation from the current `ai/PLAN.md` rule for concrete plans, but adding completed guidance-only cleanup to the active roadmap now would create stale roadmap work.

## Locked Decisions And Assumptions
- Keep the change AI-facing; do not update `README.md` or `SETUP.md` unless validation finds overlap that changes human-facing guidance.
- Keep `build.ps1` as the only repository command wrapper. Bash-compatible examples should call it through `pwsh` rather than introducing a nonexistent Bash wrapper.
- Keep prompt-body edits out of this plan unless the prompt index or loader behavior changes.
- Treat this as a docs-only validation scope with manual consistency checks and lightweight text validation.

## Execution Mode Fit
- Recommended default mode: `Single Branch`.
- This is a documentation-only task that can be handled sequentially.

## Affected Artifacts
- `AGENTS.md`
- `ai/PLAN.md`
- `ai/RELEASES.md`
- `ai/REVIEWS.md`
- `ai/ENVIRONMENT_QUICK_REF.md`
- `ai/templates/PLAN_TEMPLATE.md`
- `ai/PLAN_guideline_improvements.md`

## Execution Milestones

### Milestone 1: Context and Onboarding (AGENTS.md)
- Add "Context Hygiene" policy to optimize token usage.
- Add "Agent Onboarding Quick Start" to help new agents get started faster.
- **Deliverables**: Updated `AGENTS.md`.
- **Validation**: Manual review of clarity and accuracy.

### Milestone 2: Planning and Strategy (ai/PLAN.md, ai/templates/PLAN_TEMPLATE.md)
- Add explicit "Testing Strategy" section requirement.
- Update `ai/PLAN.md` to explain the importance of distinguishing between unit, integration, and contract tests.
- **Deliverables**: Updated `ai/PLAN.md` and `ai/templates/PLAN_TEMPLATE.md`.
- **Validation**: Manual review.

### Milestone 3: Environment and Portability (ai/ENVIRONMENT_QUICK_REF.md)
- Add Bash/Sh equivalents for all PowerShell commands.
- Ensure cross-platform clarity.
- **Deliverables**: Updated `ai/ENVIRONMENT_QUICK_REF.md`.
- **Validation**: Verify shell commands are correct for Bash.

### Milestone 4: Release and Review (ai/RELEASES.md, ai/REVIEWS.md)
- Add "Post-Release Learnings Loop" to `ai/RELEASES.md`.
- Add "Spec-Driven Development Verification" to `ai/REVIEWS.md`.
- **Deliverables**: Updated `ai/RELEASES.md` and `ai/REVIEWS.md`.
- **Validation**: Manual review.

## Edge Cases And Failure Modes
- Onboarding guidance can accidentally contradict the AI load policy by making `README.md` or `SETUP.md` mandatory for every task.
- Cross-shell command guidance can imply a wrapper that does not exist or add setup instructions that belong in `SETUP.md`.
- Review, release, planning, and template guidance can drift if the same standing policy is repeated in multiple owner files.
- A concrete plan can fail its own newly tightened plan-shape rules if required sections are missing.

## Validation Plan
- Manual review of all changed documents for internal consistency.
- Verify that all cross-references between the updated files remain valid.
- No automated tests required as these are markdown changes.

## Testing Strategy
- Unit tests: not applicable; no executable application logic changes.
- Integration tests: not applicable; no database, container, or service behavior changes.
- Contract tests: not applicable; no REST API, OpenAPI, REST Docs, or HTTP examples changed.
- Smoke or benchmark tests: not applicable; no runtime or performance behavior changed.
- Manual checks: required-section audit for this plan, cross-reference review across the touched AI guides, `git diff --check`, and targeted text search for the new guidance.

## Better Engineering Notes
- Keep this guideline cleanup separate from the unrelated `ai/prompts/bodies/context-report.md` prompt-body change.
- Do not broaden this plan into a repository-wide AI-document rewrite; route future durable changes to the owning guide named in `ai/DOCUMENTATION.md`.
- Consider archiving this plan during the next release cleanup if it no longer represents active work.

## Validation Results
- 2026-05-06 validation found the plan was missing required top-level sections now listed by `ai/PLAN.md`: `Requirement Gaps And Open Questions`, `Locked Decisions And Assumptions`, `Edge Cases And Failure Modes`, `Testing Strategy`, and `Better Engineering Notes`.
- 2026-05-06 validation found the onboarding quick start in `AGENTS.md` needed narrower wording so it does not require broad `README.md` or `SETUP.md` loading for every task.
- 2026-05-06 validation found the Bash-compatible examples in `ai/ENVIRONMENT_QUICK_REF.md` should describe invoking `build.ps1` through `pwsh`, without implying a separate Bash wrapper or executable-bit setup.
- 2026-05-06 validation tightened `ai/PLAN.md` so docs-only plans explicitly state which testing layers do not apply and what manual checks replace them.
- 2026-05-06 required-section audit passed for this plan after adding the missing sections.
- 2026-05-06 `git diff --check` passed for `AGENTS.md`, `ai/ENVIRONMENT_QUICK_REF.md`, `ai/PLAN.md`, `ai/PLAN_guideline_improvements.md`, `ai/RELEASES.md`, `ai/REVIEWS.md`, and `ai/templates/PLAN_TEMPLATE.md`.

## User Validation
- Walkthrough of the updated documents to confirm they address the evaluation findings.
