# Post-Compaction AI Guidelines Evaluation

Evaluation date: 2026-05-07

## Summary
- Overall grade: **A**.
- The standing AI guidance remains coherent, owner-routed, and mostly on-demand after the workflow, release, planning, prompt, and per-milestone context splits.
- The default load is stable at `AGENTS.md` only: 13,637 characters, 13,637 bytes, 146 lines, 1,852 words, and 3,410 estimated tokens.
- The full standing owner-guide set is 81,477 characters / 20,375 estimated tokens across 14 files. That is still acceptable because phase prompts load narrow subsets instead of the whole standing set.
- The largest practical cost is no longer standing policy drift; it is active plan inventory. After `v2.0.0-RC6` release cleanup, one top-level `ai/plans/active/PLAN_*.md` file remains active: 38,834 characters / 9,709 estimated tokens for the manual regression gate.
- No blocking active-guidance contradiction was found. Old workflow terminology appears only in historical or migration context inside active plans, not in standing owner guides or prompt titles.

## Method
- Read `AGENTS.md`, `ai/DOCUMENTATION.md`, `ai/PROMPTS.md`, and every top-level owner guide under `ai/` excluding active `ai/plans/active/PLAN_*.md` files from the standing-guide baseline.
- Read `ai/prompts/index.json` and representative large prompt bodies: `context-report.md`, `evaluate-ai-guidelines.md`, `compact-ai-docs.md`, `run-all-ready-plans.md`, and `integrate-all-open-prs.md`.
- Read key on-demand references that affect practical loads: workflow fanout references, release references, and the plan template.
- Checked active plans for lifecycle and stale-reference relevance without treating them as standing guides.
- Ran targeted searches for retired workflow names, moved-file references, repeated artifact-routing rules, prompt-body policy dumps, and release/workflow/validation duplication.
- Compared this refresh to the previous contents of this report and kept the report on demand under `ai/references/`.
- Token counts use the repository convention `ceiling(chars / 4)`.

## Current Size Baseline
Standing owner files are `AGENTS.md` plus top-level `ai/*.md` files other than active plans.

| File | Chars | Bytes | Lines | Words | Est. Tokens | Load Class |
| --- | ---: | ---: | ---: | ---: | ---: | --- |
| `AGENTS.md` | 13,637 | 13,637 | 146 | 1,852 | 3,410 | Default entry |
| `ai/ARCHITECTURE.md` | 6,300 | 6,300 | 96 | 830 | 1,575 | Conditional descriptive |
| `ai/CODE_STYLE.md` | 3,420 | 3,420 | 40 | 486 | 855 | Phase-specific |
| `ai/DESIGN.md` | 6,569 | 6,569 | 107 | 894 | 1,643 | Conditional descriptive |
| `ai/DOCUMENTATION.md` | 6,805 | 6,805 | 80 | 898 | 1,702 | Phase-specific |
| `ai/ENVIRONMENT_QUICK_REF.md` | 2,146 | 2,146 | 45 | 275 | 537 | Phase-specific |
| `ai/EXECUTION.md` | 7,844 | 7,844 | 94 | 1,160 | 1,961 | Phase-specific |
| `ai/LEARNINGS.md` | 3,259 | 3,259 | 47 | 484 | 815 | Conditional descriptive |
| `ai/PLANNING.md` | 6,282 | 6,282 | 96 | 905 | 1,571 | Phase-specific |
| `ai/PROMPTS.md` | 4,330 | 4,330 | 88 | 581 | 1,083 | Conditional index |
| `ai/RELEASES.md` | 3,526 | 3,526 | 43 | 507 | 882 | Phase-specific |
| `ai/REVIEWS.md` | 2,650 | 2,650 | 45 | 381 | 663 | Phase-specific |
| `ai/TESTING.md` | 5,116 | 5,116 | 62 | 690 | 1,279 | Phase-specific |
| `ai/WORKFLOW.md` | 9,593 | 9,593 | 152 | 1,442 | 2,399 | Phase-specific router |

Additional inventory that must stay on demand:

| Inventory | Files | Chars | Bytes | Lines | Words | Est. Tokens |
| --- | ---: | ---: | ---: | ---: | ---: | ---: |
| Active plans | 1 | 38,834 | 38,850 | 457 | 5,128 | 9,709 |
| Archived plans | 37 | 741,910 | 741,980 | 8,047 | 85,801 | 185,491 |
| Prompt inventory | 45 | 44,770 | 44,780 | 800 | 5,558 | 11,214 |
| Prompt bodies only | 44 | 33,572 | 33,582 | 407 | 4,777 | 8,414 |
| Workflow fanout references | 2 | 7,444 | 7,444 | 100 | 1,160 | 1,862 |
| Plan template | 1 | 3,436 | 3,436 | 87 | 553 | 859 |
| Repo skill entrypoints | 4 | 20,473 | 20,473 | 205 | 3,080 | 5,120 |
| Repo skill references | 18 | 444,605 | 446,286 | 6,936 | 55,686 | 111,158 |
| All AI inventory | 132 | 1,501,872 | 1,503,645 | 19,317 | 184,486 | 375,523 |

Largest prompt bodies remain procedural, not standing policy:

| Prompt Body | Chars | Lines | Words | Est. Tokens | Assessment |
| --- | ---: | ---: | ---: | ---: | --- |
| `context-report.md` | 5,366 | 87 | 783 | 1,342 | Large but isolated maintenance report prompt |
| `evaluate-ai-guidelines.md` | 3,842 | 71 | 522 | 961 | This evaluation prompt; correctly on demand |
| `compact-ai-docs.md` | 2,857 | 28 | 375 | 715 | Maintenance prompt with compaction criteria, not standing policy |
| `run-all-ready-plans.md` | 1,350 | 13 | 212 | 338 | Workflow execution prompt that depends on exact invocation |
| `integrate-all-open-prs.md` | 1,297 | 8 | 192 | 325 | Coordinator prompt; not preloaded |

## Practical Read-Set Estimates

| Scenario | Files | Chars | KB | Lines | Words | Est. Tokens |
| --- | ---: | ---: | ---: | ---: | ---: | ---: |
| Standing root plus top-level owner guides | 14 | 81,477 | 79.6 | 1,141 | 11,385 | 20,375 |
| Planning minimum: `AGENTS.md` + `ai/PLANNING.md` | 2 | 19,919 | 19.5 | 242 | 2,757 | 4,981 |
| Implementation minimum: `AGENTS.md` + `ai/EXECUTION.md` | 2 | 21,481 | 21.0 | 240 | 3,012 | 5,371 |
| Broad implementation conditional set | 5 | 36,822 | 36.0 | 422 | 5,086 | 9,207 |
| Workflow selection: `AGENTS.md` + `ai/WORKFLOW.md` + `ai/EXECUTION.md` | 3 | 31,074 | 30.3 | 392 | 4,454 | 7,770 |
| Verification: `AGENTS.md` + `ai/TESTING.md` + `ai/DOCUMENTATION.md` + `ai/REVIEWS.md` | 4 | 28,208 | 27.5 | 333 | 3,821 | 7,054 |
| Release policy: `AGENTS.md` + `ai/RELEASES.md` | 2 | 17,163 | 16.8 | 189 | 2,359 | 4,292 |
| Release policy plus release references | 4 | 23,025 | 22.5 | 243 | 3,073 | 5,758 |
| Conditional descriptive docs only | 3 | 16,128 | 15.8 | 250 | 2,208 | 4,033 |
| Workflow fanout references only | 2 | 7,444 | 7.3 | 100 | 1,160 | 1,862 |

## Rubric Findings
- Owner clarity: **A**. `ai/DOCUMENTATION.md` cleanly owns artifact routing, most guides declare their scope in the first paragraph, and `AGENTS.md` stays the repo-level entry point.
- Default-load necessity: **A-**. The default floor is still 13,637 characters, but it is stable and delegates details to owner guides instead of embedding runbooks.
- On-demand trigger clarity: **A**. Workflow fanout, release mechanics, planning examples, troubleshooting, Gradle task graph, prompt bodies, templates, skills, archives, and descriptive docs all have explicit load triggers.
- Duplication or policy drift: **A-**. No blocking duplication was found. `AGENTS.md` has compact high-level routing while `ai/DOCUMENTATION.md` owns the detailed routing; `ai/PLANNING.md` repeats only planning-specific consequences.
- Execution usefulness: **A**. Execution, workflow, validation, and review guides give concrete steps and stop conditions. The per-milestone context requirement is now actionable instead of aspirational.
- Validation and review routing: **A**. `ai/TESTING.md`, `ai/DOCUMENTATION.md`, and `ai/REVIEWS.md` form a clear route for documentation-only, lightweight, implementation, contract, and security-sensitive changes.

## File Grades
| File | Grade | Rationale |
| --- | --- | --- |
| `AGENTS.md` | A- | Strong entry point with spec priority, instruction load policy, and definition of done. It remains the default context floor, so new detail should continue routing elsewhere. |
| `ai/ARCHITECTURE.md` | A | Compact architecture and feature ownership map. It correctly points deeper structural detail to an on-demand reference. |
| `ai/CODE_STYLE.md` | A | Short, focused edit-shaping guidance with useful cross-references and little duplication. |
| `ai/DESIGN.md` | B+ | Useful product and release-phase intent. It is large for a descriptive guide, so it should remain conditional. |
| `ai/DOCUMENTATION.md` | A | Clear artifact-routing owner. Some routing concepts appear elsewhere, but this file is the authoritative detailed owner. |
| `ai/ENVIRONMENT_QUICK_REF.md` | A | Small, concrete wrapper command guide with an explicit boundary from setup troubleshooting. |
| `ai/EXECUTION.md` | A | The milestone loop, conditional read triggers, validation handoff, and commit discipline are clear and executable. |
| `ai/LEARNINGS.md` | A- | Durable lessons are concise and scoped. Its relevance-scan trigger keeps it out of default context. |
| `ai/PLANNING.md` | A- | Good minimum planning policy with full skeleton moved to the template. It necessarily repeats a few planning consequences from documentation routing. |
| `ai/PROMPTS.md` | A | Lean prompt-title index with a compact default read-set table and raw bodies kept on demand. |
| `ai/RELEASES.md` | A- | Release policy stays standing while checklist and artifact verification are on demand. Preconditions are appropriately strict. |
| `ai/REVIEWS.md` | A | Short, practical review and security-review guidance with useful findings-first reporting rules. |
| `ai/TESTING.md` | A | Focused validation guide with the lightweight-file shortcut and on-demand troubleshooting triggers. |
| `ai/WORKFLOW.md` | A- | Effective router for mode selection, delegation quality, and coordinator ownership. It is the largest phase guide because shared branch/worktree invariants still belong there. |

## Realized Gains Since The Previous Report
- The previous **A** grade still holds; no newly discovered contradiction requires downgrading the guidance set.
- The default load remained stable at `AGENTS.md` only, while the latest context-size reporting confirms new inventory is landing outside the default path.
- Per-milestone context requirements are now part of `ai/PLANNING.md`, `ai/templates/PLAN_TEMPLATE.md`, `ai/EXECUTION.md`, and active plans, giving implementers a concrete way to avoid broad just-in-case reads.
- The prompt set added stronger maintenance tooling, especially `Context Report`, while keeping raw prompt bodies under `ai/prompts/bodies/` and out of standing guidance.
- Workflow mode names remain current in standing guides and prompt titles: `Linear Plan`, `Single-Plan Fanout`, and `Multi-Plan Fanout`.
- The report itself remains an on-demand reference rather than becoming a top-level AI guide.

## Remaining Costs And Risks
- Active plans are now smaller after `v2.0.0-RC6` release cleanup, but the remaining manual regression plan is intentionally detailed because it is the stable-release gate.
- `ai/WORKFLOW.md` is still the largest phase-specific guide. Further splitting is possible, but the remaining content is mostly shared invariant and mode-selection policy.
- `ai/DESIGN.md` is useful but descriptive. Agents should load it only for user-visible behavior, supported scope, security posture, operational defaults, or roadmap tradeoffs.
- Prompt bodies are healthy today, but `context-report.md` and `evaluate-ai-guidelines.md` are large enough that future maintenance prompts should keep reports procedural instead of embedding full policy.
- Repo-local skill references are the largest non-archive inventory bucket. They are safe only while skill references stay strictly on demand.
- Total AI inventory is large because archived plans and skill references are intentionally retained. Inventory size is not itself a default-load problem, but loader mistakes would be expensive.

## Obsolete Recommendations Not Repeated
- Recreating pre-split workflow names or transitional workflow reference files is obsolete. The current names are `Linear Plan`, `Single-Plan Fanout`, and `Multi-Plan Fanout`.
- Restoring a separate business-module guide is not recommended. Feature ownership belongs in `ai/ARCHITECTURE.md`.
- Copying release checklist details back into `ai/RELEASES.md` is not recommended. The current release references are the right on-demand boundary.
- Moving the plan skeleton back into `ai/PLANNING.md` is not recommended. `ai/templates/PLAN_TEMPLATE.md` is the right home for full structure.
- Bulk-loading `ai/archive/` for active guidance review is not recommended. Archived plans are historical and should be opened only for targeted investigation.
- Treating old scratch-note grades or size targets as current authority is obsolete. Live repository measurements should drive future evaluations.

## Ranked Follow-Up Recommendations
1. Add a tiny context-metrics helper or guard.
   - Why: repeated manual measurement is error-prone, and `AGENTS.md` plus top-level `ai/*.md` should not grow silently.
   - Suggested guard: warn when standing owner-guide context grows by more than 5% in one commit, or when `AGENTS.md` exceeds 15,000 characters.
2. Keep prompt bodies under review during maintenance work.
   - Why: the largest prompt bodies are still procedural, but maintenance prompts can easily turn into policy dumps.
   - Route: `ai/PROMPTS.md`, `ai/prompts/index.json`, and the owning guide named by `ai/DOCUMENTATION.md`.
3. Revisit `ai/WORKFLOW.md` only if workflow-selection loads prove costly in practice.
   - Why: the high-value fanout split already landed; further splitting risks hiding invariants that are needed before mode selection.
   - Route: a new focused compaction plan only after concrete evidence of workflow-load friction.
4. Keep skill references strictly on demand.
   - Why: repo-local skill references account for 444,605 characters / 111,158 estimated tokens.
   - Route: skill entrypoints should stay narrow and point to references only after the skill is invoked and the reference matches the task.

## Conclusion
The current AI guideline set remains an **A**. The repo has moved the highest-cost mechanics behind explicit triggers, kept default context stable, and made practical phase read sets measurable. Future efficiency work should focus on lightweight measurement automation and keeping prompt and skill references on demand, not broad manual compaction of standing guides.
