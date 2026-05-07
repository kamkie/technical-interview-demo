# Workflow Selection Variant Comparison

Comparison date: 2026-05-07

This report compares the three workflow-selection variants implemented in separate worktrees from the same `main` baseline.
It is an on-demand decision aid, not standing workflow policy.

## Compared Branches

| Variant | Branch | Final Commit | Worktree | Local Status |
| --- | --- | --- | --- | --- |
| Variant 1: Soft consolidation | `plan/workflow-selection-soft-consolidation-impl` | `f91a3fb` | `D:\Projects\Jit\technical-interview-demo-workflow-soft-consolidation` | clean |
| Variant 2: Hard split | `plan/workflow-selection-hard-split-impl` | `fcd3004` | `D:\Projects\Jit\technical-interview-demo-workflow-hard-split` | clean |
| Variant 3: Hard split with multi-plan template | `plan/workflow-selection-multi-plan-template-impl` | `015401e` | `D:\Projects\Jit\technical-interview-demo-workflow-multi-plan-template` | clean |

All three branches refreshed `ai/archive/reports/AI_GUIDELINES_POST_COMPACTION_EVALUATION.md` after implementation.

## Executive Recommendation

Select **Variant 2: Hard split** as the next integration candidate.

Reason:

- It delivers the main measurable win: the milestone execution read set drops from the old 21,481 characters / 5,371 estimated tokens to 16,153 characters / 4,039 estimated tokens.
- It removes the stale workflow-router owner instead of leaving a compatibility pointer that can grow back into a second source of truth.
- It avoids Variant 3's extra coordinator-plan vocabulary until recurring multi-plan coordination proves it is worth the template and prompt rename churn.
- Its main risk, `ai/PLAN_EXECUTION.md` matching the broad `PLAN_*.md` glob, is concrete and easy to mitigate in future plan-inventory tooling.

Do not merge multiple variants together. They are alternative owner models and intentionally conflict in `ai/PLAN.md`, workflow guidance, prompt wording, and evaluation output.

## Metrics Summary

Baseline from the pre-variant post-compaction report:

- standing owner-guide set: 81,477 characters / 20,375 estimated tokens
- workflow selection: 31,074 characters / 7,770 estimated tokens
- implementation minimum: 21,481 characters / 5,371 estimated tokens

| Metric | Soft Consolidation | Hard Split | Hard Split + Coordinator Template |
| --- | ---: | ---: | ---: |
| Standing owner-guide set | 75,446 chars / 18,866 tokens | 75,847 chars / 18,967 tokens | 75,830 chars / 18,962 tokens |
| Mode selection | 21,539 chars / 5,385 tokens | 21,701 chars / 5,426 tokens | 21,667 chars / 5,417 tokens |
| Implementation pre-loop | same as implementation minimum | 18,024 chars / 4,506 tokens | 18,024 chars / 4,506 tokens |
| Milestone loop | same as implementation minimum | 16,153 chars / 4,039 tokens | 16,153 chars / 4,039 tokens |
| Implementation minimum | 20,454 chars / 5,114 tokens | split across pre-loop and milestone guides | split across pre-loop and milestone guides |
| Single-plan fanout | 25,158 chars / 6,290 tokens | 27,180 chars / 6,796 tokens | 27,146 chars / 6,787 tokens |
| Multi-plan path | 25,356 chars / 6,340 tokens | 27,378 chars / 6,846 tokens | 27,525 chars / 6,883 tokens |
| Overall evaluation grade | A- | A- | A- |

Interpretation:

- All variants reduce the standing owner-guide inventory versus the current baseline.
- All variants move mode selection into `ai/PLAN.md`, so planning-mode reads become slightly larger than the old planning minimum.
- Variant 1 is the least disruptive but preserves most of the hot-path implementation cost.
- Variants 2 and 3 produce the same hot-path execution savings.
- Variant 3 makes multi-plan orchestration conceptually cleaner, but the recurring-use case is not yet proven.

## Churn Summary

| Variant | Diff Size | Main Shape |
| --- | --- | --- |
| Soft consolidation | 30 files, 192 insertions, 388 deletions | keeps `ai/EXECUTION.md`; shrinks `ai/WORKFLOW.md` to a compatibility pointer |
| Hard split | 39 files, 488 insertions, 523 deletions | deletes `ai/EXECUTION.md` and `ai/WORKFLOW.md`; adds `ai/PLAN_EXECUTION.md`, `ai/MILESTONE_EXECUTION.md`, and a mode-ownership guard |
| Hard split + coordinator template | 43 files, 576 insertions, 602 deletions | hard split plus `ai/templates/PLAN_MULTI_FANOUT_TEMPLATE.md` and renamed multi-plan prompts |

Variant 1 has the lowest churn, but it leaves a transitional file that future changes must police.
Variant 2 has moderate churn and the clearest long-term owner split.
Variant 3 has the highest churn and introduces a new abstraction.

## Validation Summary

| Variant | Validation |
| --- | --- |
| Soft consolidation | prompt loader smoke checks passed; stale ownership search found only expected new owner or historical plan context; `git diff --check` passed; `./build.ps1 build` used the lightweight-only shortcut |
| Hard split | ownership guard passed; prompt loader smoke checks passed; `git diff --check` passed; full `./build.ps1 build` passed with JBR 25 after the default shell JDK 11 failed Gradle's JVM 17+ requirement |
| Hard split + coordinator template | ownership guard passed; renamed prompt smoke checks passed; stale multi-plan-mode vocabulary search passed; `git diff --check` passed; lightweight wrapper build passed; forced full `./build.ps1 -FullBuild build` passed with JBR 25 |

The hard split and coordinator-template branches have stronger branch-level validation because they add scripts or rename prompt files.

## Variant Notes

### Variant 1: Soft Consolidation

Strengths:

- Lowest churn.
- Keeps the current file set and familiar `ai/EXECUTION.md`.
- Gives the mode-selection read-set win with minimal disruption.

Weaknesses:

- Leaves `ai/WORKFLOW.md` in place as a compatibility pointer.
- Does not meaningfully reduce the hot-path milestone execution guide.
- Requires future discipline so the pointer does not become a second owner.

Best fit:

- Use only if minimizing immediate doc churn matters more than cleaning up the owner model.

### Variant 2: Hard Split

Strengths:

- Best practical tradeoff.
- Reduces both mode-selection and milestone-loop read sets.
- Deletes the old workflow router instead of keeping a transitional owner.
- Adds a small script guard against mode-name drift in standing guides.

Weaknesses:

- Higher churn than Variant 1.
- `ai/PLAN_EXECUTION.md` can be mistaken for a concrete `ai/PLAN_*.md` file by broad inventory globs.
- Fanout references become larger because they now own worker-log and coordinator safeguards.

Best fit:

- Use as the default integration candidate.

### Variant 3: Hard Split With Multi-Plan Template

Strengths:

- Cleanest conceptual model if multi-plan coordination is frequent.
- Makes multi-plan orchestration a coordinator artifact with child plans, integration order, private changelog fan-in, validation fan-in, and completion gates.
- Prompt names better match the coordinator-plan model.

Weaknesses:

- Highest churn.
- Adds new vocabulary and a new template before repeated multi-plan use is proven.
- Has nearly the same measurable read-set performance as Variant 2 for normal work.

Best fit:

- Defer unless the repository expects recurring multi-plan coordination.

## Recommended Next Steps

1. Choose one variant for integration. Recommended: `plan/workflow-selection-hard-split-impl`.
2. Before merging, review the Variant 2 guard scope and decide whether `scripts/ai/check-workflow-mode-ownership.ps1` should remain script-only for one cycle.
3. Merge only the selected branch to `main`; do not cherry-pick pieces from the other variants unless a specific improvement is intentionally accepted and the reason is recorded.
4. After integration, run `./build.ps1 build` with JDK 17+ or JBR 25. The local default shell currently points at JDK 11, which is insufficient for Gradle.
5. Update or add any plan-inventory script or prompt that treats `PLAN_*.md` as active plans so it excludes `ai/PLAN_EXECUTION.md` when Variant 2 or Variant 3 is selected.
6. Close the unselected worktrees and branches after the selected branch is integrated or explicitly rejected.
7. Defer the Variant 3 coordinator template until there is concrete evidence of repeated multi-plan runs, such as several expected multi-plan executions in a release cycle.

## Decision

Recommended decision: integrate **Variant 2: Hard split**.

Fallback if immediate churn must be minimized: integrate **Variant 1: Soft consolidation** and schedule deletion of the compatibility pointer.

Do not integrate **Variant 3** yet unless multi-plan coordination is an expected recurring workflow, not just an occasional recovery path.
