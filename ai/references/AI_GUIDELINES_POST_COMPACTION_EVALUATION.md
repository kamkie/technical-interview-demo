# Post-Compaction AI Guidelines Evaluation

Evaluation date: 2026-05-07

## Summary
- Overall grade: **A-**.
- The current guidance set is materially stronger than the historical scratch-note `B+`: workflow fanout mechanics are now on demand, prompt titles use the current execution-mode names, and context-quality/relevance-scan rules are in the right owner files.
- The remaining cost is no longer broad workflow drift. It is concentrated in `AGENTS.md` size, `ai/RELEASES.md` runbook density, `ai/PLAN.md` checklist density, and `ai/EXECUTION.md` reading-list breadth.
- No blocking contradiction was found in the active standing guidance. Old workflow names remain only as intentional migration notes inside the implemented workflow-split plan.

## Method
- Read the active standing AI guides: `AGENTS.md` and top-level `ai/*.md` owner guides excluding active `ai/PLAN_*.md` files.
- Checked on-demand workflow references, the plan template, prompt index, and representative large prompt bodies.
- Treated `C:\Users\kamki\AppData\Roaming\JetBrains\IntelliJIdea2026.1\scratches\evaluate_and_grade_ai_guidelines_in_this.md` as historical input, not current authority.
- Scored against this rubric:
  - owner clarity
  - default-load necessity
  - trigger clarity for on-demand material
  - duplication or policy drift
  - execution usefulness
  - validation and review routing

## Size Baseline
Standing owner files are `AGENTS.md` plus top-level `ai/*.md` files other than active plans.

| File | KB | Lines | Load Class |
| --- | ---: | ---: | --- |
| `AGENTS.md` | 14.7 | 168 | Default entry |
| `ai/ARCHITECTURE.md` | 5.0 | 84 | Conditional descriptive |
| `ai/BUSINESS_MODULES.md` | 2.8 | 55 | Conditional descriptive |
| `ai/CODE_STYLE.md` | 3.4 | 40 | Phase-specific |
| `ai/DESIGN.md` | 6.4 | 107 | Conditional descriptive |
| `ai/DOCUMENTATION.md` | 4.8 | 53 | Phase-specific |
| `ai/ENVIRONMENT_QUICK_REF.md` | 2.1 | 45 | Phase-specific |
| `ai/EXECUTION.md` | 6.7 | 87 | Phase-specific |
| `ai/LEARNINGS.md` | 3.2 | 47 | Conditional descriptive |
| `ai/PLAN.md` | 6.7 | 120 | Phase-specific |
| `ai/PROMPTS.md` | 3.3 | 81 | Conditional index |
| `ai/RELEASES.md` | 8.3 | 88 | Phase-specific |
| `ai/REVIEWS.md` | 2.6 | 45 | Phase-specific |
| `ai/TESTING.md` | 5.0 | 62 | Phase-specific |
| `ai/WORKFLOW.md` | 9.1 | 151 | Phase-specific router |

Practical read-set estimates:

| Scenario | Files | KB | Estimated Tokens |
| --- | ---: | ---: | ---: |
| Standing root plus top-level owner guides | 15 | 84.0 | 21,508 |
| Planning minimum: `AGENTS.md` + `ai/PLAN.md` | 2 | 21.4 | 5,477 |
| Implementation minimum: `AGENTS.md` + `ai/EXECUTION.md` | 2 | 21.4 | 5,481 |
| Implementation broad list from `ai/EXECUTION.md` | 9 | 55.0 | 14,084 |
| Workflow selection: `AGENTS.md` + `ai/WORKFLOW.md` + `ai/EXECUTION.md` | 3 | 30.5 | 7,809 |
| Verification: `AGENTS.md` + `ai/TESTING.md` + `ai/DOCUMENTATION.md` + `ai/REVIEWS.md` | 4 | 27.1 | 6,929 |
| Release: `AGENTS.md` + `ai/RELEASES.md` | 2 | 23.0 | 5,886 |
| Conditional descriptive docs only | 4 | 17.4 | 4,463 |
| Workflow fanout references now on demand | 2 | 7.3 | 1,861 |

The total standing size is close to the historical scratch estimate, but the shape has changed. `AGENTS.md` is much larger than the scratch assumed, while `ai/WORKFLOW.md` is smaller and detailed fanout mechanics now sit behind explicit reference triggers.

## Rubric Findings
- Owner clarity: **A**. `ai/DOCUMENTATION.md` cleanly routes ownership, and most guides state their role in the first paragraph.
- Default-load necessity: **B+**. The load policy is explicit, but `AGENTS.md` is now the largest standing default file and `ai/EXECUTION.md` still names a broad pre-edit reading list.
- On-demand trigger clarity: **A-**. Workflow, planning, troubleshooting, Gradle task graph, prompt bodies, templates, skills, and references generally have clear load triggers. Descriptive docs are marked descriptive but could have a tighter phase-to-guide trigger map.
- Duplication or policy drift: **B+**. No active stale workflow terminology was found outside intentional migration notes, but `AGENTS.md`, `ai/DOCUMENTATION.md`, `ai/PROMPTS.md`, and `ai/EXECUTION.md` repeat some read-set and artifact-routing concepts.
- Execution usefulness: **A-**. The milestone loop, workflow modes, validation routing, and commit discipline are actionable. The main cost is that the broad reading list can inflate simple implementation tasks.
- Validation and review routing: **A**. `ai/TESTING.md`, `ai/DOCUMENTATION.md`, and `ai/REVIEWS.md` form a clear validation/review path, including documentation-only and lightweight-file handling.

## File Grades
| File | Grade | Rationale |
| --- | --- | --- |
| `AGENTS.md` | B+ | Excellent entry point and spec-priority owner, but 14.7 KB is high for the default read. Some change-type routing overlaps with `ai/DOCUMENTATION.md`. |
| `ai/ARCHITECTURE.md` | A- | Clear compact map with the detailed map on demand. Correctly descriptive rather than authoritative. |
| `ai/BUSINESS_MODULES.md` | A- | Lean ownership guide for feature placement. Its trigger is clear for business-package changes. |
| `ai/CODE_STYLE.md` | A | Short, focused edit-shaping guidance with clear cross-references. |
| `ai/DESIGN.md` | B+ | Useful product-direction context, but relatively large for a descriptive guide and should remain conditional. |
| `ai/DOCUMENTATION.md` | A | Strong artifact-routing owner; it reduces drift by keeping ownership decisions centralized. |
| `ai/ENVIRONMENT_QUICK_REF.md` | A | Small, concrete, and well-scoped to wrapper command usage. |
| `ai/EXECUTION.md` | B+ | Strong milestone loop and tracking rules. The `Before You Implement` section still encourages loading more guides than many implementation tasks need. |
| `ai/LEARNINGS.md` | A- | Durable lessons are concise and scoped. Relevance-scan and learning-trigger rules make it conditional instead of default. |
| `ai/PLAN.md` | B+ | Solid standing planning rules, but required-content and output-format detail could move further into the template or detailed reference. |
| `ai/PROMPTS.md` | A- | Lean command index with raw bodies on demand. The default read-set section is useful but slightly overlaps with onboarding and execution guides. |
| `ai/RELEASES.md` | B | Correctly phase-specific, but release checklist and artifact-verification mechanics are still dense enough to justify a future reference split. |
| `ai/REVIEWS.md` | A | Short, practical, and clearly owns review/security-review expectations. |
| `ai/TESTING.md` | A | Focused validation guidance with good lightweight-change and on-demand troubleshooting rules. |
| `ai/WORKFLOW.md` | A- | The workflow split landed successfully: fanout detail moved to on-demand references and active names are current. It remains a large router because shared branch/worktree and coordinator rules stay in the standing file. |

## Realized Gains
- The historical recommendation to split workflow mechanics is handled in repo-native form: `Single-Plan Fanout` and `Multi-Plan Fanout` details moved into `ai/references/WORKFLOW_SINGLE_PLAN_FANOUT.md` and `ai/references/WORKFLOW_MULTI_PLAN_FANOUT.md`.
- Active owner guides and prompt titles use `Linear Plan`, `Single-Plan Fanout`, and `Multi-Plan Fanout`; old names appear only in intentional migration notes in `ai/PLAN_workflow_on_demand_split.md`.
- The prompt index remains lean, and raw prompt bodies are still on demand. The largest body, `context-report.md`, is a maintenance report prompt rather than standing policy.
- Targeted relevance scanning, context-quality checkpoints, per-milestone scope checks, and post-validation review triggers are now in owner guides instead of external skill instructions.
- The evaluation report itself is correctly on demand under `ai/references/` rather than becoming another standing owner guide.

## Remaining Costs
- `AGENTS.md` now dominates every read set. That is expected for the entry point, but it creates a hard floor of about 14.7 KB before any phase-specific guide is loaded.
- `ai/EXECUTION.md` still lists `ai/PLAN.md`, `ai/WORKFLOW.md`, and several focused guides under "Read these before editing." In practice, that can turn a small implementation into a 55.0 KB broad read set.
- `ai/RELEASES.md` remains a combined policy, checklist, and artifact-verification runbook. Since it is release-only, the cost is phase-contained, but it is still a high-ROI split candidate.
- `ai/PLAN.md` and `ai/templates/PLAN_TEMPLATE.md` intentionally overlap on required plan shape. The overlap is not contradictory, but it is still standing bytes that could move into the template.
- Descriptive docs are correctly marked non-authoritative, but the exact phase triggers for loading `ai/ARCHITECTURE.md`, `ai/BUSINESS_MODULES.md`, `ai/DESIGN.md`, and `ai/LEARNINGS.md` are spread across several files.

## Scratch Comparison
| Scratch Recommendation | Current Result |
| --- | --- |
| Split `ai/WORKFLOW.md` | Mostly handled. The final router is 9.1 KB, not the scratch's smaller target, but the highest-risk fanout mechanics are on demand. |
| Make descriptive docs on demand | Partly handled. The docs are labeled descriptive and non-authoritative, but a compact phase-to-guide trigger map would reduce ambiguity. |
| Split `ai/RELEASES.md` | Still open. Release work is phase-specific, but checklist and verification mechanics remain in the standing release guide. |
| Slim `ai/PLAN.md` | Partly handled. A detailed reference and template exist, but required-content detail remains in the standing guide. |
| Add phase-to-guide context map | Partly handled. `ai/PROMPTS.md` has default read sets, but `AGENTS.md` and `ai/EXECUTION.md` do not yet give a single compact conditional map. |
| Tighten `ai/EXECUTION.md` reading list | Still open. This is the most important remaining source of avoidable broad reads during implementation. |
| Add context-drop markers in plans | Partly handled by `AGENTS.md` context hygiene and execution per-milestone checks, but not standardized as a plan-section requirement. |

## Conclusion
The current AI guideline set deserves **A-**: it is coherent, owner-routed, and substantially more on-demand than the scratch baseline. The remaining improvements should be narrow compaction plans, not emergency repairs. The best next work is to reduce `AGENTS.md` and implementation-phase standing load while preserving the current owner model.
