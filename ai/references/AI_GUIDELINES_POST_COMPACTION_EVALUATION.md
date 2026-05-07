# Post-Compaction AI Guidelines Evaluation

Evaluation date: 2026-05-07

## Summary
- Overall grade after follow-up compaction: **A**.
- The current guidance set is materially stronger than the historical scratch-note `B+`: workflow fanout mechanics, release runbooks, prompt bodies, detailed planning examples, and plan skeleton detail are now on demand, and business-feature ownership has been folded into `ai/ARCHITECTURE.md`.
- The remaining cost is concentrated in the unavoidable `AGENTS.md` default floor, the workflow router, and descriptive docs that must stay easy to find without becoming default context.
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
| `AGENTS.md` | 13.3 | 146 | Default entry |
| `ai/ARCHITECTURE.md` | 6.2 | 96 | Conditional descriptive |
| `ai/CODE_STYLE.md` | 3.4 | 40 | Phase-specific |
| `ai/DESIGN.md` | 6.4 | 107 | Conditional descriptive |
| `ai/DOCUMENTATION.md` | 6.6 | 80 | Phase-specific |
| `ai/ENVIRONMENT_QUICK_REF.md` | 2.1 | 45 | Phase-specific |
| `ai/EXECUTION.md` | 7.5 | 94 | Phase-specific |
| `ai/LEARNINGS.md` | 3.2 | 47 | Conditional descriptive |
| `ai/PLAN.md` | 5.6 | 91 | Phase-specific |
| `ai/PROMPTS.md` | 4.2 | 87 | Conditional index |
| `ai/RELEASES.md` | 3.4 | 43 | Phase-specific |
| `ai/REVIEWS.md` | 2.6 | 45 | Phase-specific |
| `ai/TESTING.md` | 5.0 | 62 | Phase-specific |
| `ai/WORKFLOW.md` | 9.4 | 152 | Phase-specific router |

Practical read-set estimates:

| Scenario | Files | KB | Estimated Tokens |
| --- | ---: | ---: | ---: |
| Standing root plus top-level owner guides | 14 | 78.9 | 20,202 |
| Planning minimum: `AGENTS.md` + `ai/PLAN.md` | 2 | 19.1 | 4,889 |
| Implementation minimum: `AGENTS.md` + `ai/EXECUTION.md` | 2 | 21.0 | 5,380 |
| Implementation broad conditional set | 5 | 35.3 | 9,030 |
| Workflow selection: `AGENTS.md` + `ai/WORKFLOW.md` + `ai/EXECUTION.md` | 3 | 30.4 | 7,779 |
| Verification: `AGENTS.md` + `ai/TESTING.md` + `ai/DOCUMENTATION.md` + `ai/REVIEWS.md` | 4 | 27.7 | 7,095 |
| Release policy: `AGENTS.md` + `ai/RELEASES.md` | 2 | 16.9 | 4,327 |
| Release policy plus on-demand refs | 4 | 22.6 | 5,793 |
| Conditional descriptive docs only | 3 | 15.8 | 4,032 |
| Workflow fanout references now on demand | 2 | 7.3 | 1,861 |

The total standing size is below the historical scratch estimate, and the practical phase read sets are smaller than the raw standing total. `AGENTS.md` still defines the default floor, while detailed workflow and release mechanics now sit behind explicit reference triggers.

## Rubric Findings
- Owner clarity: **A**. `ai/DOCUMENTATION.md` cleanly routes ownership, and most guides state their role in the first paragraph.
- Default-load necessity: **A-**. The load policy is explicit and `AGENTS.md` is smaller, though it remains the default floor for every task.
- On-demand trigger clarity: **A**. Workflow, release, planning detail, troubleshooting, Gradle task graph, prompt bodies, templates, skills, and references have explicit load triggers. Descriptive-doc triggers are now centralized in the prompt read-set map.
- Duplication or policy drift: **A-**. No active stale workflow terminology was found outside intentional migration notes, and detailed change-type routing now lives in `ai/DOCUMENTATION.md`.
- Execution usefulness: **A**. The milestone loop, workflow modes, validation routing, and commit discipline are actionable, and implementation reads are now conditional instead of broad by default.
- Validation and review routing: **A**. `ai/TESTING.md`, `ai/DOCUMENTATION.md`, and `ai/REVIEWS.md` form a clear validation/review path, including documentation-only and lightweight-file handling.

## File Grades
| File | Grade | Rationale |
| --- | --- | --- |
| `AGENTS.md` | A- | Excellent entry point and spec-priority owner. Change-type routing is now compact and delegates detail to `ai/DOCUMENTATION.md`, though the file remains the default context floor. |
| `ai/ARCHITECTURE.md` | A | Clear compact map with feature ownership folded in and the detailed map on demand. Correctly descriptive rather than authoritative. |
| `ai/CODE_STYLE.md` | A | Short, focused edit-shaping guidance with clear cross-references. |
| `ai/DESIGN.md` | B+ | Useful product-direction context, but relatively large for a descriptive guide and should remain conditional. |
| `ai/DOCUMENTATION.md` | A | Strong artifact-routing owner. It now also owns detailed change-type routing that was previously duplicated in `AGENTS.md`. |
| `ai/ENVIRONMENT_QUICK_REF.md` | A | Small, concrete, and well-scoped to wrapper command usage. |
| `ai/EXECUTION.md` | A- | Strong milestone loop and tracking rules. The `Before You Implement` section now uses a small base read set plus explicit conditional triggers. |
| `ai/LEARNINGS.md` | A- | Durable lessons are concise and scoped. Relevance-scan and learning-trigger rules make it conditional instead of default. |
| `ai/PLAN.md` | A- | Solid standing planning rules with exhaustive skeleton/checklist detail moved to the on-demand template. |
| `ai/PROMPTS.md` | A | Lean command index with raw bodies on demand and a compact phase-to-guide read-set map. |
| `ai/RELEASES.md` | A- | Release policy, preconditions, version choice, and safety rules remain standing; checklist and artifact verification moved to on-demand references. |
| `ai/REVIEWS.md` | A | Short, practical, and clearly owns review/security-review expectations. |
| `ai/TESTING.md` | A | Focused validation guidance with good lightweight-change and on-demand troubleshooting rules. |
| `ai/WORKFLOW.md` | A- | The workflow split landed successfully: fanout detail moved to on-demand references and active names are current. It remains a large router because shared branch/worktree and coordinator rules stay in the standing file. |

## Realized Gains
- The historical recommendation to split workflow mechanics is handled in repo-native form: `Single-Plan Fanout` and `Multi-Plan Fanout` details moved into `ai/references/WORKFLOW_SINGLE_PLAN_FANOUT.md` and `ai/references/WORKFLOW_MULTI_PLAN_FANOUT.md`.
- Active owner guides and prompt titles use `Linear Plan`, `Single-Plan Fanout`, and `Multi-Plan Fanout`; old names appear only in intentional migration notes in `ai/PLAN_workflow_on_demand_split.md`.
- The prompt index remains lean, and raw prompt bodies are still on demand. The largest body, `context-report.md`, is a maintenance report prompt rather than standing policy.
- Targeted relevance scanning, context-quality checkpoints, per-milestone scope checks, and post-validation review triggers are now in owner guides instead of external skill instructions.
- Business-feature ownership moved from the separate `ai/BUSINESS_MODULES.md` guide into `ai/ARCHITECTURE.md`, removing one overlapping descriptive file from the standing guide set.
- The evaluation report itself is correctly on demand under `ai/references/` rather than becoming another standing owner guide.

## Remaining Costs
- `AGENTS.md` still dominates every read set. That is expected for the entry point, but it creates a hard floor of about 13.5 KB before any phase-specific guide is loaded.
- `ai/DOCUMENTATION.md` grew because it now owns detailed change-type routing. That is a deliberate tradeoff: the routing detail moved out of default context and into the owning guide.
- `ai/WORKFLOW.md` remains the largest phase-specific router because it owns branch, worktree, coordinator, and integration invariants.
- Descriptive docs remain a meaningful optional load at about 15.8 KB combined, so agents should use the phase-to-guide trigger map instead of loading them by default.

## Scratch Comparison
| Scratch Recommendation | Current Result |
| --- | --- |
| Split `ai/WORKFLOW.md` | Handled. The final router is larger than the scratch target, but the highest-risk fanout mechanics are on demand. |
| Make descriptive docs on demand | Handled. The docs are labeled descriptive and non-authoritative, and `ai/PROMPTS.md` now has explicit trigger guidance. |
| Split `ai/RELEASES.md` | Handled. Checklist and artifact-verification mechanics moved to on-demand release references. |
| Slim `ai/PLAN.md` | Handled. Required-content checklist detail moved to `ai/templates/PLAN_TEMPLATE.md`. |
| Add phase-to-guide context map | Handled. `ai/PROMPTS.md` now has a compact phase-to-guide read-set table and descriptive-doc triggers. |
| Tighten `ai/EXECUTION.md` reading list | Handled. Implementation reads are now base-plus-conditional instead of a broad standing list. |
| Add context-drop markers in plans | Partly handled by `AGENTS.md` context hygiene and execution per-milestone checks, but not standardized as a plan-section requirement. |

## Follow-Up Ranking
This section records the original ranking plus the current follow-up state.

### Already Handled
- Workflow fanout split: handled by the current `ai/WORKFLOW.md` router plus `ai/references/WORKFLOW_SINGLE_PLAN_FANOUT.md` and `ai/references/WORKFLOW_MULTI_PLAN_FANOUT.md`.
- Current workflow terminology: handled in active prompt titles and owner guides. Remaining old terms are migration notes in the implemented workflow-split plan.
- Prompt-body on-demand model: mostly handled. `ai/PROMPTS.md` is a lean index, and raw bodies remain under `ai/prompts/bodies/`.
- Context-quality and relevance-scan adoption: handled by the pskoett-guidance adoption plan through `AGENTS.md`, `ai/EXECUTION.md`, `ai/REVIEWS.md`, and `ai/LEARNINGS.md`.
- `ai/EXECUTION.md` conditional reads: implemented in the follow-up compaction.
- `AGENTS.md` default-load compaction: implemented by compacting change-type routing and keeping details in `ai/DOCUMENTATION.md`.
- `ai/RELEASES.md` runbook split: implemented through `ai/references/RELEASE_CHECKLIST.md` and `ai/references/RELEASE_ARTIFACT_VERIFICATION.md`.
- `ai/PLAN.md` slimming: implemented by moving exhaustive checklist detail to `ai/templates/PLAN_TEMPLATE.md`.
- Phase-to-guide and descriptive-doc triggers: implemented in `ai/PROMPTS.md`.

### Remaining Lower ROI
1. Add explicit per-milestone context requirements to every plan.
   - Why: context-drop markers could help long plans, but they would add boilerplate to small plans and may duplicate milestone validation notes.
   - Suggested approach: try only on large or fanout-prone plans first.
2. Split more from `ai/WORKFLOW.md`.
   - Why: the largest workflow win already landed. Further cuts may be possible, but the remaining router still owns branch/worktree and coordinator invariants that agents need before choosing a mode.
   - Suggested approach: revisit only if workflow-selection reads remain too large in practice.

### Obsolete Or Not Recommended
- Recreate transitional workflow reference names from before the split: obsolete. The repo intentionally adopted `Single-Plan Fanout` and `Multi-Plan Fanout` reference names.
- Copy the scratch `B+` grade or old size estimates into standing docs: obsolete. Current scoring should use live repository measurements.
- Bulk-load archived plans to evaluate active guidance: not recommended. Active guidance can be checked with targeted searches, and archive content is historical.

## Conclusion
The current AI guideline set deserves **A**: it is coherent, owner-routed, and substantially more on-demand than the scratch baseline. Remaining improvements are optional narrow compaction work, not emergency repairs.
