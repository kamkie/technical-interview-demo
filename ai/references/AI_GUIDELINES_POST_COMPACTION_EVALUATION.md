# Post-Compaction AI Guidelines Evaluation

Evaluation date: 2026-05-07

## Summary
- Overall grade: **A-**.
- The standing AI guidance remains coherent after the active-plan path migration, planning-guide rename, whole-plan execution split, task and milestone execution split, workflow-coordination narrowing, and reusable task-library simplification.
- The default load remains `AGENTS.md` only: 13,807 characters, 13,807 bytes, 214 lines, 1,865 words, and 3,452 estimated tokens.
- The full top-level owner-guide set is now 123,553 characters / 30,892 estimated tokens across 15 files because `ai/TASK_LIBRARY.md` is self-contained. That is acceptable only while agents follow heading-search loading and avoid bulk-loading all top-level guides.
- The largest practical load risk is split between active-plan inventory and the self-contained task catalog. Two active tracked plans total 81,264 characters / 20,316 estimated tokens; `ai/TASK_LIBRARY.md` is 44,811 characters / 11,203 estimated tokens.
- No blocking active-guidance contradiction was found. The old task-body directory, JSON index, and custom task loader are no longer current live mechanisms.

## Method
- Read `AGENTS.md`, `ai/DOCUMENTATION.md`, `ai/TASK_LIBRARY.md`, and the top-level owner guides under `ai/`.
- Excluded active `ai/plans/active/PLAN_*.md` files from the standing-guide baseline, then inspected active-plan inventory and lifecycle state separately.
- Inspected representative large task sections in `ai/TASK_LIBRARY.md`: `Context Report`, `Evaluate AI Guidelines`, `Compact AI Docs`, `Run All Ready Plans`, and `Integrate All Open PRs`.
- Read on-demand references that affect practical load estimates: delegated-plan workflow mechanics, coordinated-plan workflow mechanics, release checklist, release artifact verification, and the plan template.
- Ran targeted searches for moved-file references, removed workflow terminology, repeated artifact-routing rules, task sections growing into standing policy, and release, workflow, validation, and planning owner overlap.
- Compared this refresh to the previous contents of this report and kept the report as an on-demand reference under `ai/references/`.
- Size measurements use tracked repository files and the repository convention `ceiling(chars / 4)` for estimated tokens.

## Current Size Baseline
Standing owner files are `AGENTS.md` plus top-level `ai/*.md` files. Active plans under `ai/plans/active/` are measured separately.

| File | Chars | Bytes | Lines | Words | Est. Tokens | Load Class |
| --- | ---: | ---: | ---: | ---: | ---: | --- |
| `AGENTS.md` | 13,807 | 13,807 | 214 | 1,865 | 3,452 | Default entry |
| `ai/ARCHITECTURE.md` | 6,520 | 6,520 | 137 | 862 | 1,630 | Conditional descriptive |
| `ai/CODE_STYLE.md` | 3,420 | 3,420 | 54 | 486 | 855 | Phase-specific |
| `ai/DESIGN.md` | 6,569 | 6,569 | 160 | 894 | 1,643 | Conditional descriptive |
| `ai/DOCUMENTATION.md` | 9,654 | 9,654 | 129 | 1,272 | 2,414 | Phase-specific |
| `ai/ENVIRONMENT_QUICK_REF.md` | 2,152 | 2,152 | 57 | 276 | 538 | Phase-specific |
| `ai/EXECUTION.md` | 4,700 | 4,700 | 97 | 720 | 1,175 | Phase-specific |
| `ai/LEARNINGS.md` | 3,259 | 3,259 | 65 | 484 | 815 | Conditional descriptive |
| `ai/PLAN_EXECUTION.md` | 4,879 | 4,879 | 91 | 719 | 1,220 | Phase-specific |
| `ai/PLANNING.md` | 6,594 | 6,594 | 133 | 948 | 1,649 | Phase-specific |
| `ai/RELEASES.md` | 3,551 | 3,551 | 63 | 509 | 888 | Phase-specific |
| `ai/REVIEWS.md` | 2,663 | 2,663 | 67 | 381 | 666 | Phase-specific |
| `ai/TASK_LIBRARY.md` | 44,811 | 44,821 | 929 | 5,916 | 11,203 | Conditional task catalog |
| `ai/TESTING.md` | 4,942 | 4,942 | 90 | 666 | 1,236 | Phase-specific |
| `ai/WORKFLOW.md` | 6,032 | 6,032 | 147 | 883 | 1,508 | Phase-specific |

Additional tracked inventory that must stay on demand:

| Inventory | Files | Chars | Bytes | Lines | Words | Est. Tokens |
| --- | ---: | ---: | ---: | ---: | ---: | ---: |
| Active plans | 2 | 81,264 | 81,280 | 900 | 10,416 | 20,316 |
| Archived plans | 45 | 889,494 | 889,564 | 10,875 | 105,084 | 222,391 |
| Task catalog | 1 | 44,811 | 44,821 | 929 | 5,916 | 11,203 |
| Workflow coordination references | 2 | 4,599 | 4,599 | 112 | 686 | 1,151 |
| Release references | 2 | 5,875 | 5,875 | 73 | 714 | 1,470 |
| Plan template | 1 | 3,378 | 3,378 | 104 | 547 | 845 |
| Repo skill entrypoints | 4 | 20,580 | 20,580 | 295 | 3,082 | 5,146 |
| Repo skill references | 10 | 390,418 | 392,099 | 8,967 | 49,647 | 97,608 |
| All tracked AI inventory | 99 | 1,698,053 | 1,700,634 | 27,256 | 211,259 | 424,551 |

Largest task sections remain procedural and on demand:

| Task Section | Chars | Lines | Words | Est. Tokens | Assessment |
| --- | ---: | ---: | ---: | ---: | --- |
| `Context Report` | 5,611 | 133 | 796 | 1,403 | Large but isolated maintenance report task |
| `Evaluate AI Guidelines` | 4,048 | 95 | 531 | 1,012 | This evaluation task; correctly on demand |
| `Compact AI Docs` | 2,972 | 45 | 382 | 743 | Maintenance task with criteria, not standing policy |
| `Run All Ready Plans` | 1,450 | 21 | 214 | 363 | Plan-execution starter that depends on exact invocation |
| `Integrate All Open PRs` | 1,405 | 16 | 199 | 352 | Coordinator starter; not preloaded |

## Practical Read-Set Estimates

| Scenario | Files | Chars | KB | Lines | Words | Est. Tokens |
| --- | ---: | ---: | ---: | ---: | ---: | ---: |
| Standing root plus top-level owner guides | 15 | 123,553 | 120.7 | 2,422 | 16,881 | 30,892 |
| Planning minimum: `AGENTS.md` + `ai/PLANNING.md` | 2 | 20,401 | 19.9 | 347 | 2,813 | 5,101 |
| Whole-plan execution minimum: `AGENTS.md` + `ai/PLAN_EXECUTION.md` | 2 | 18,686 | 18.2 | 305 | 2,584 | 4,672 |
| Ad hoc or milestone execution minimum: `AGENTS.md` + `ai/EXECUTION.md` | 2 | 18,507 | 18.1 | 311 | 2,585 | 4,627 |
| Broad implementation conditional set | 5 | 35,766 | 34.9 | 597 | 4,904 | 8,943 |
| Workflow coordination minimum: `AGENTS.md` + `ai/WORKFLOW.md` | 2 | 19,839 | 19.4 | 361 | 2,748 | 4,960 |
| Delegated plan execution: `AGENTS.md` + `ai/PLAN_EXECUTION.md` + `ai/WORKFLOW.md` | 3 | 24,718 | 24.1 | 452 | 3,467 | 6,180 |
| Verification: `AGENTS.md` + `ai/TESTING.md` + `ai/DOCUMENTATION.md` + `ai/REVIEWS.md` | 4 | 31,066 | 30.3 | 500 | 4,184 | 7,768 |
| Release policy: `AGENTS.md` + `ai/RELEASES.md` | 2 | 17,358 | 17.0 | 277 | 2,374 | 4,340 |
| Release policy plus release references | 4 | 23,233 | 22.7 | 350 | 3,088 | 5,810 |
| Conditional descriptive docs only | 3 | 16,348 | 16.0 | 362 | 2,240 | 4,088 |
| Task catalog search: `AGENTS.md` + `ai/TASK_LIBRARY.md` | 2 | 58,618 | 57.2 | 1,143 | 7,781 | 14,655 |
| Workflow coordination references only | 2 | 4,599 | 4.5 | 112 | 686 | 1,151 |

## Rubric Findings
- Owner clarity: **A**. Planning, whole-plan execution, ad hoc or milestone execution, workflow coordination, task-library maintenance, validation, review, release, and documentation routing each have named owners.
- Default-load necessity: **A-**. `AGENTS.md` is still a meaningful default context floor, and the current 13,807-character size is below the 15,000-character guardrail recommended in prior evaluations.
- On-demand trigger clarity: **A-**. Active plans, task sections, workflow references, release references, templates, skills, archived plans, and descriptive guides have explicit load triggers. The self-contained task catalog makes heading-search discipline more important.
- Duplication or policy drift: **A-**. No blocking duplication was found. `AGENTS.md` carries compact repo-level routing while `ai/DOCUMENTATION.md` owns detailed artifact ownership.
- Execution usefulness: **A**. `ai/PLAN_EXECUTION.md`, `ai/EXECUTION.md`, and `ai/WORKFLOW.md` now separate execution state, task loops, and coordination mechanics without requiring a broad standing read.
- Validation and review routing: **A**. `ai/TESTING.md`, `ai/REVIEWS.md`, and `ai/DOCUMENTATION.md` give a clear route for documentation-only, lightweight, implementation, contract, and security-sensitive changes.

## File Grades
| File | Grade | Rationale |
| --- | --- | --- |
| `AGENTS.md` | A- | Strong entry point with spec priority, phase owner map, instruction load policy, and definition of done. New detail should continue routing elsewhere because this is the default file. |
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
| `ai/TASK_LIBRARY.md` | B+ | Simpler single-file task catalog with no custom loader or body directory. It is easier to maintain but must stay strictly on demand because it is much larger than a lean index. |
| `ai/TESTING.md` | A | Focused validation guide with the lightweight-file shortcut and clear escalation triggers. |
| `ai/WORKFLOW.md` | A | Smaller coordination owner after execution mechanics moved out. Branch, worktree, delegation, worker-log, and integration rules remain available when needed. |

## Realized Gains Since The Previous Report
- The overall guidance grade is now **A-** because the task catalog is simpler but larger; the owner split still improved clarity after adding `ai/PLAN_EXECUTION.md`.
- Whole-plan execution, one-milestone execution, and workflow coordination now have separate owners, which reduces just-in-case loading for ordinary implementation work.
- Active plans now live under `ai/plans/active/`, so `ai/PLAN_EXECUTION.md` no longer collides with active-plan inventory naming.
- Reusable starters are now a self-contained task catalog with no `bodies/` directory, JSON index, or custom PowerShell loader.
- The optional ad hoc task skill was deferred, avoiding another policy surface while `ai/EXECUTION.md` and task-library starters cover the entry workflow.
- Removed workflow-mode wording and old reusable-starter storage references are absent from standing guides and task titles.

## Remaining Costs And Risks
- Active plans currently cost 81,264 characters / 20,316 estimated tokens. That is acceptable for plan execution, but completed plans should not remain in the active read set longer than their lifecycle requires.
- `AGENTS.md` remains the unavoidable default context floor. It is below the recommended 15,000-character warning point, but future repo-level rules should still route to owner guides when possible.
- `ai/DOCUMENTATION.md` is the largest phase guide because it owns artifact routing. That centralization is useful, but future changes should resist adding runbook detail there.
- Maintenance task sections such as `Context Report` and `Evaluate AI Guidelines` are large enough to need periodic drift review.
- Repo-local skill references are large by design and safe only while skill references stay strictly on demand.
- Total AI inventory is large because archived plans and skill references are retained. Inventory size is not itself a default-load problem, but broad task-catalog loading mistakes would be expensive.

## Obsolete Recommendations Not Repeated
- Restoring the old reusable-starter body directory, JSON index, or custom loader is not recommended; the single-file task catalog is enough unless a task becomes a Codex plugin-packaged skill.
- Reintroducing old workflow-mode labels is not recommended; current standing docs use direct ownership language for local execution, delegated one-plan work, and coordinated multi-plan work.
- Creating a generic ad hoc execution skill is not recommended until repeated entry friction proves `ai/EXECUTION.md` and the task library are insufficient.
- Moving the full plan skeleton back into `ai/PLANNING.md` is not recommended. `ai/templates/PLAN_TEMPLATE.md` remains the right home for full structure.
- Bulk-loading `ai/archive/` for active guidance review is not recommended. Archived plans are historical and should be opened only for targeted investigation.
- Treating old scratch-note grades or size targets as current authority is obsolete. Live repository measurements should drive future evaluations.

## Ranked Follow-Up Recommendations
1. Keep active-plan inventory lean after implementation handoff.
   - Why: active plans are the largest practical load set at 81,264 characters / 20,316 estimated tokens.
   - Route: move completed plans out of `ai/plans/active/` only at the lifecycle point named by `ai/DOCUMENTATION.md`, and keep `ROADMAP.md` aligned.
2. Add a tiny context-metrics helper or guard.
   - Why: repeated manual measurement is error-prone, and standing owner-guide context should not grow silently.
   - Suggested guard: warn when standing owner-guide context grows by more than 5% in one change, or when `AGENTS.md` exceeds 15,000 characters.
3. Keep task sections under review during maintenance work.
   - Why: the largest task sections are still procedural, but maintenance starters can easily turn into policy dumps inside the now self-contained catalog.
   - Route: `ai/TASK_LIBRARY.md` and the owning guide named by `ai/DOCUMENTATION.md`.
4. Keep skill references strictly on demand.
   - Why: repo-local skill references account for 390,418 characters / 97,608 estimated tokens.
   - Route: skill entrypoints should stay narrow and point to references only after the skill is invoked and the reference matches the task.

## Conclusion
The current AI guideline set is an **A-**. The owner boundaries remain strong and the task library is easier to maintain, but the top-level task catalog is much larger and now depends on strict heading-search loading. Future efficiency work should focus on active-plan cleanup discipline, lightweight measurement automation, and preventing task sections or skill references from becoming standing policy.
