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
- Adding "Context Hygiene" and "Quick Start" to `AGENTS.md`.
- Adding "Learnings Loop" to `ai/RELEASES.md`.
- Adding Bash/Sh equivalents to `ai/ENVIRONMENT_QUICK_REF.md`.
- Strengthening "Testing Strategy" and "Spec-Driven Development" across relevant files.

### Out of scope
- Changes to non-AI documentation (README, SETUP, etc.) unless strictly necessary for alignment.
- Changes to implementation code or actual build scripts.

## Current State
- The guidelines are high quality (Grade A+) but have some identified gaps:
    - Heavily skewed towards PowerShell.
    - No explicit "learnings loop" after releases.
    - Context management is mostly implicit.
    - Testing strategy in plans could be more structured.

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

## Validation Plan
- Manual review of all changed documents for internal consistency.
- Verify that all cross-references between the updated files remain valid.
- No automated tests required as these are markdown changes.

## Validation Results
- To be filled in during execution

## User Validation
- Walkthrough of the updated documents to confirm they address the evaluation findings.
