# Post-Compaction AI Guidelines Evaluation

Evaluation date: 2026-05-07

## Summary
- Overall grade: **A**.
- The standing AI guidance is coherent after the active-plan path migration, planning-guide rename, whole-plan execution split, task and milestone execution split, workflow-coordination narrowing, and reusable task-library migration.
- The default load remains `AGENTS.md` only: 13,251 characters, 13,251 bytes, 213 lines, 1,792 words, and 3,313 estimated tokens.
- The full standing owner-guide set is 80,900 characters / 20,230 estimated tokens across 15 files. That total includes the new `ai/PLAN_EXECUTION.md` and is still acceptable because lifecycle phases load narrow owner subsets instead of the whole standing set.
- The largest practical load risk is active-plan inventory, not standing policy. Two active tracked plans currently total 76,373 characters / 19,094 estimated tokens: this AI-guidance restructure plan and the manual regression execution plan.
- No blocking active-guidance contradiction was found. Removed workflow-mode wording and old reusable-starter storage references are not present in standing owner guides or task-library titles.

## Method
- Read `AGENTS.md`, `ai/DOCUMENTATION.md`, `ai/TASK_LIBRARY.md`, and the top-level owner guides under `ai/`.
- Excluded active `ai/plans/active/PLAN_*.md` files from the standing-guide baseline, then inspected active-plan inventory and lifecycle state separately.
- Read `ai/task-library/index.json` and representative large task bodies: `context-report.md`, `evaluate-ai-guidelines.md`, `compact-ai-docs.md`, `run-all-ready-plans.md`, and `integrate-all-open-prs.md`.
- Read on-demand references that affect practical load estimates: delegated-plan workflow mechanics, coordinated-plan workflow mechanics, release checklist, release artifact verification, and the plan template.
- Ran targeted searches for moved-file references, removed workflow terminology, repeated artifact-routing rules, task bodies growing into standing policy, and release, workflow, validation, and planning owner overlap.
- Compared this refresh to the previous contents of this report and kept the report as an on-demand reference under `ai/references/`.
- Size measurements use tracked repository files and the repository convention `ceiling(chars / 4)` for estimated tokens.

## Current Size Baseline
Standing owner files are `AGENTS.md` plus top-level `ai/*.md` files. Active plans under `ai/plans/active/` are measured separately.

| File | Chars | Bytes | Lines | Words | Est. Tokens | Load Class |
| --- | ---: | ---: | ---: | ---: | ---: | --- |
| `AGENTS.md` | 13,251 | 13,251 | 213 | 1,792 | 3,313 | Default entry |
| `ai/ARCHITECTURE.md` | 6,520 | 6,520 | 137 | 862 | 1,630 | Conditional descriptive |
| `ai/CODE_STYLE.md` | 3,420 | 3,420 | 54 | 486 | 855 | Phase-specific |
| `ai/DESIGN.md` | 6,569 | 6,569 | 160 | 894 | 1,643 | Conditional descriptive |
| `ai/DOCUMENTATION.md` | 9,441 | 9,441 | 129 | 1,243 | 2,361 | Phase-specific |
| `ai/ENVIRONMENT_QUICK_REF.md` | 2,152 | 2,152 | 57 | 276 | 538 | Phase-specific |
| `ai/EXECUTION.md` | 4,521 | 4,521 | 97 | 692 | 1,131 | Phase-specific |
| `ai/LEARNINGS.md` | 3,259 | 3,259 | 65 | 484 | 815 | Conditional descriptive |
| `ai/PLAN_EXECUTION.md` | 4,635 | 4,635 | 91 | 684 | 1,159 | Phase-specific |
| `ai/PLANNING.md` | 6,594 | 6,594 | 133 | 948 | 1,649 | Phase-specific |
| `ai/RELEASES.md` | 3,551 | 3,551 | 63 | 509 | 888 | Phase-specific |
| `ai/REVIEWS.md` | 2,649 | 2,649 | 68 | 381 | 663 | Phase-specific |
| `ai/TASK_LIBRARY.md` | 3,364 | 3,364 | 95 | 471 | 841 | Conditional index |
| `ai/TESTING.md` | 4,942 | 4,942 | 90 | 666 | 1,236 | Phase-specific |
| `ai/WORKFLOW.md` | 6,032 | 6,032 | 147 | 883 | 1,508 | Phase-specific |

Additional tracked inventory that must stay on demand:

| Inventory | Files | Chars | Bytes | Lines | Words | Est. Tokens |
| --- | ---: | ---: | ---: | ---: | ---: | ---: |
| Active plans | 2 | 76,373 | 76,389 | 876 | 9,835 | 19,094 |
| Archived plans | 45 | 889,494 | 889,564 | 10,920 | 105,084 | 222,391 |
| Task library inventory | 45 | 46,001 | 46,011 | 952 | 5,583 | 11,520 |
| Task bodies only | 44 | 34,245 | 34,255 | 558 | 4,806 | 8,581 |
| Workflow coordination references | 2 | 4,599 | 4,599 | 112 | 686 | 1,151 |
| Release references | 2 | 5,875 | 5,875 | 73 | 714 | 1,470 |
| Plan template | 1 | 3,378 | 3,378 | 104 | 547 | 845 |
| Repo skill entrypoints | 4 | 20,580 | 20,580 | 295 | 3,082 | 5,146 |
| Repo skill references | 10 | 390,418 | 392,099 | 8,967 | 49,647 | 97,608 |
| All tracked AI inventory | 143 | 1,682,469 | 1,685,050 | 27,224 | 208,615 | 420,675 |

Largest task bodies remain procedural and on demand:

| Task Body | Chars | Lines | Words | Est. Tokens | Assessment |
| --- | ---: | ---: | ---: | ---: | --- |
| `context-report.md` | 5,366 | 127 | 784 | 1,342 | Large but isolated maintenance report task |
| `evaluate-ai-guidelines.md` | 3,873 | 89 | 522 | 969 | This evaluation task; correctly on demand |
| `compact-ai-docs.md` | 2,866 | 39 | 375 | 717 | Maintenance task with criteria, not standing policy |
| `run-all-ready-plans.md` | 1,348 | 15 | 207 | 337 | Plan-execution starter that depends on exact invocation |
| `integrate-all-open-prs.md` | 1,297 | 10 | 192 | 325 | Coordinator starter; not preloaded |

## Practical Read-Set Estimates

| Scenario | Files | Chars | KB | Lines | Words | Est. Tokens |
| --- | ---: | ---: | ---: | ---: | ---: | ---: |
| Standing root plus top-level owner guides | 15 | 80,900 | 79.0 | 1,599 | 11,271 | 20,230 |
| Planning minimum: `AGENTS.md` + `ai/PLANNING.md` | 2 | 19,845 | 19.4 | 346 | 2,740 | 4,962 |
| Whole-plan execution minimum: `AGENTS.md` + `ai/PLAN_EXECUTION.md` | 2 | 17,886 | 17.5 | 304 | 2,476 | 4,472 |
| Ad hoc or milestone execution minimum: `AGENTS.md` + `ai/EXECUTION.md` | 2 | 17,772 | 17.4 | 310 | 2,484 | 4,444 |
| Broad implementation conditional set | 5 | 34,804 | 34.0 | 597 | 4,774 | 8,704 |
| Workflow coordination minimum: `AGENTS.md` + `ai/WORKFLOW.md` | 2 | 19,283 | 18.8 | 360 | 2,675 | 4,821 |
| Delegated plan execution: `AGENTS.md` + `ai/PLAN_EXECUTION.md` + `ai/WORKFLOW.md` | 3 | 23,918 | 23.4 | 451 | 3,359 | 5,980 |
| Verification: `AGENTS.md` + `ai/TESTING.md` + `ai/DOCUMENTATION.md` + `ai/REVIEWS.md` | 4 | 30,283 | 29.6 | 500 | 4,082 | 7,573 |
| Release policy: `AGENTS.md` + `ai/RELEASES.md` | 2 | 16,802 | 16.4 | 276 | 2,301 | 4,201 |
| Release policy plus release references | 4 | 22,677 | 22.1 | 349 | 3,015 | 5,671 |
| Conditional descriptive docs only | 3 | 16,348 | 16.0 | 362 | 2,240 | 4,088 |
| Workflow coordination references only | 2 | 4,599 | 4.5 | 112 | 686 | 1,151 |

## Rubric Findings
- Owner clarity: **A**. Planning, whole-plan execution, ad hoc or milestone execution, workflow coordination, task-library maintenance, validation, review, release, and documentation routing each have named owners.
- Default-load necessity: **A-**. `AGENTS.md` is still a meaningful default context floor, but the current 13,251-character size is below the 15,000-character guardrail recommended in prior evaluations.
- On-demand trigger clarity: **A**. Active plans, task bodies, workflow references, release references, templates, skills, archived plans, and descriptive guides have explicit load triggers.
- Duplication or policy drift: **A-**. No blocking duplication was found. `AGENTS.md` carries compact repo-level routing while `ai/DOCUMENTATION.md` owns detailed artifact ownership.
- Execution usefulness: **A**. `ai/PLAN_EXECUTION.md`, `ai/EXECUTION.md`, and `ai/WORKFLOW.md` now separate execution state, task loops, and coordination mechanics without requiring a broad standing read.
- Validation and review routing: **A**. `ai/TESTING.md`, `ai/REVIEWS.md`, and `ai/DOCUMENTATION.md` give a clear route for documentation-only, lightweight, implementation, contract, and security-sensitive changes.

## File Grades
| File | Grade | Rationale |
| --- | --- | --- |
| `AGENTS.md` | A- | Strong entry point with spec priority, lifecycle owner map, instruction load policy, and definition of done. New detail should continue routing elsewhere because this is the default file. |
| `ai/ARCHITECTURE.md` | A | Compact architecture and feature ownership map with deeper structural detail kept in on-demand references. |
| `ai/CODE_STYLE.md` | A | Short edit-shaping guidance with useful cross-references and little duplication. |
| `ai/DESIGN.md` | B+ | Useful product and release-phase intent. Its descriptive scope is broad, so it should remain conditional. |
| `ai/DOCUMENTATION.md` | A- | Clear artifact-routing owner. It is one of the larger phase guides, but the size is justified by centralizing ownership rules that would otherwise drift. |
| `ai/ENVIRONMENT_QUICK_REF.md` | A | Small, concrete wrapper command guide with a clear boundary from setup troubleshooting. |
| `ai/EXECUTION.md` | A | Focused ad hoc and single-milestone loop with promotion gates, context switching, validation, and completion rules. |
| `ai/LEARNINGS.md` | A- | Durable lessons are concise and scoped; the relevance-scan trigger keeps it out of default context. |
| `ai/PLAN_EXECUTION.md` | A | Whole-plan execution now has a small owner for milestone sequencing, plan validation notes, roadmap state, and completion handoff. |
| `ai/PLANNING.md` | A- | Good plan-creation policy with lifecycle state, roadmap synchronization, and per-milestone context requirements. Full skeleton detail stays in the template. |
| `ai/RELEASES.md` | A- | Release policy stays standing while release checklist and artifact verification remain on demand. Preconditions are appropriately strict. |
| `ai/REVIEWS.md` | A | Short, practical review and security-review guidance with findings-first reporting rules. |
| `ai/TASK_LIBRARY.md` | A | Lean task-title index with raw bodies and loader behavior kept out of default context. |
| `ai/TESTING.md` | A | Focused validation guide with the lightweight-file shortcut and clear escalation triggers. |
| `ai/WORKFLOW.md` | A | Smaller coordination owner after execution mechanics moved out. Branch, worktree, delegation, worker-log, and integration rules remain available when needed. |

## Realized Gains Since The Previous Report
- The overall **A** grade still holds after adding `ai/PLAN_EXECUTION.md`; the standing owner-guide total is slightly lower than the previous 14-file baseline while ownership is clearer.
- Whole-plan execution, one-milestone execution, and workflow coordination now have separate owners, which reduces just-in-case loading for ordinary implementation work.
- Active plans now live under `ai/plans/active/`, so `ai/PLAN_EXECUTION.md` no longer collides with active-plan inventory naming.
- Reusable starters are now task-library artifacts with deterministic loader behavior, and task bodies remain procedural instead of becoming standing policy.
- The optional ad hoc task skill was deferred, avoiding another policy surface while `ai/EXECUTION.md` and task-library starters cover the entry workflow.
- Removed workflow-mode wording and old reusable-starter storage references are absent from standing guides and task titles.

## Remaining Costs And Risks
- Active plans currently cost 76,373 characters / 19,094 estimated tokens. That is acceptable for plan execution, but completed plans should not remain in the active read set longer than their lifecycle requires.
- `AGENTS.md` remains the unavoidable default context floor. It is below the recommended 15,000-character warning point, but future repo-level rules should still route to owner guides when possible.
- `ai/DOCUMENTATION.md` is the largest phase guide because it owns artifact routing. That centralization is useful, but future changes should resist adding runbook detail there.
- Maintenance task bodies such as `context-report.md` and `evaluate-ai-guidelines.md` are large enough to need periodic drift review.
- Repo-local skill references are large by design and safe only while skill references stay strictly on demand.
- Total AI inventory is large because archived plans and skill references are retained. Inventory size is not itself a default-load problem, but broad loader mistakes would be expensive.

## Obsolete Recommendations Not Repeated
- Restoring the old reusable-starter loader or storage compatibility layer is not recommended; the task-library migration intentionally has no legacy alias.
- Reintroducing old workflow-mode labels is not recommended; current standing docs use direct ownership language for local execution, delegated one-plan work, and coordinated multi-plan work.
- Creating a generic ad hoc execution skill is not recommended until repeated entry friction proves `ai/EXECUTION.md` and the task library are insufficient.
- Moving the full plan skeleton back into `ai/PLANNING.md` is not recommended. `ai/templates/PLAN_TEMPLATE.md` remains the right home for full structure.
- Bulk-loading `ai/archive/` for active guidance review is not recommended. Archived plans are historical and should be opened only for targeted investigation.
- Treating old scratch-note grades or size targets as current authority is obsolete. Live repository measurements should drive future evaluations.

## Ranked Follow-Up Recommendations
1. Keep active-plan inventory lean after implementation handoff.
   - Why: active plans are the largest practical load set at 76,373 characters / 19,094 estimated tokens.
   - Route: move completed plans out of `ai/plans/active/` only at the lifecycle point named by `ai/DOCUMENTATION.md`, and keep `ROADMAP.md` aligned.
2. Add a tiny context-metrics helper or guard.
   - Why: repeated manual measurement is error-prone, and standing owner-guide context should not grow silently.
   - Suggested guard: warn when standing owner-guide context grows by more than 5% in one change, or when `AGENTS.md` exceeds 15,000 characters.
3. Keep task bodies under review during maintenance work.
   - Why: the largest task bodies are still procedural, but maintenance starters can easily turn into policy dumps.
   - Route: `ai/TASK_LIBRARY.md`, `ai/task-library/index.json`, and the owning guide named by `ai/DOCUMENTATION.md`.
4. Keep skill references strictly on demand.
   - Why: repo-local skill references account for 390,418 characters / 97,608 estimated tokens.
   - Route: skill entrypoints should stay narrow and point to references only after the skill is invoked and the reference matches the task.

## Conclusion
The current AI guideline set remains an **A**. The restructure improved owner boundaries without increasing the standing load, moved reusable starters to a task-library model, and kept high-cost plans, references, skills, and task bodies behind explicit triggers. Future efficiency work should focus on active-plan cleanup discipline, lightweight measurement automation, and preventing task bodies or skill references from becoming standing policy.
