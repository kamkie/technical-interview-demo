# AI Owner-File Remix Evaluation

Evaluation date: 2026-05-07
Status: on-demand reference; not part of the default read set.

## Scope

Evaluate whether the AI owner files under `ai/` can be remixed to lower context usage, with focus on:

- splitting `ai/DOCUMENTATION.md` and `ai/ARCHITECTURE.md` into a "reference" part (load when reading the repo) and a "modification instructions" part (load when editing in that lifecycle phase);
- the same question for `ai/CODE_STYLE.md` and the other owner files;
- mixing `ai/DOCUMENTATION.md`, `ai/ARCHITECTURE.md`, and `ai/DESIGN.md` into different lifecycle-bundled files.

The estimation method, token formula, and load-class language follow `ai/references/AI_GUIDELINES_POST_COMPACTION_EVALUATION.md`.

## Current Baseline

Live sizes of standing owner files (`ceiling(chars / 4)` for tokens):

| File | Chars | Est. Tokens | Current Load Class |
| --- | ---: | ---: | --- |
| `AGENTS.md` | 14,791 | 3,698 | Default entry |
| `ai/WORKFLOW.md` | 9,631 | 2,408 | Phase-specific router |
| `ai/EXECUTION.md` | 7,170 | 1,793 | Phase-specific |
| `ai/DOCUMENTATION.md` | 6,863 | 1,716 | Phase-specific |
| `ai/DESIGN.md` | 6,569 | 1,643 | Conditional descriptive |
| `ai/PLANNING.md` | 6,416 | 1,604 | Phase-specific |
| `ai/ARCHITECTURE.md` | 6,300 | 1,575 | Conditional descriptive |
| `ai/TESTING.md` | 4,942 | 1,236 | Phase-specific |
| `ai/RELEASES.md` | 3,526 | 882 | Phase-specific |
| `ai/CODE_STYLE.md` | 3,420 | 855 | Phase-specific |
| `ai/TASK_LIBRARY.md` | 3,312 | 828 | Conditional index |
| `ai/LEARNINGS.md` | 3,259 | 815 | Conditional descriptive |
| `ai/REVIEWS.md` | 2,650 | 663 | Phase-specific |
| `ai/ENVIRONMENT_QUICK_REF.md` | 2,146 | 537 | Phase-specific |
| **Standing owner-guide set** | **80,995** | **20,253** |  |

Per `AGENTS.md` "AI Instruction Load Policy", only `AGENTS.md` is in the default load. Everything else is already on demand. Practical phase loads measured in the post-compaction report range from ~5k tokens (planning minimum) to ~9k tokens (broad implementation). Any remix must be judged against those phase loads, not against the full standing set.

## Per-File Reference-vs-Modification Audit

### `ai/DOCUMENTATION.md` (6,863 chars)

Already structurally split:

- §"Artifact Ownership" (lines 8-33, ~2,100 chars) is a pure lookup table — "which artifact owns truth X". This is reference content.
- §"Change-Type Routing", §"Alignment Rules", §"Common Routing", §"Cross-References" (lines 35-105, ~4,700 chars) are imperative — "when you change X, update Y in the same commit". This is modification content.

Remix feasibility: **high**. Splittable into `ai/DOCUMENTATION_OWNERSHIP.md` (reference, ~2.2k) and `ai/DOCUMENTATION_ROUTING.md` (modification, ~4.7k) with low semantic loss; modification phases would still load both because the routing rules cite ownership rows.

### `ai/ARCHITECTURE.md` (6,300 chars)

- §"System Snapshot", §"Package Layout", §"Build Layout", §"Implementation Surface", §"API Surface" (lines 10-82, ~5,200 chars) are descriptive reference.
- §"Architecture Rules", §"Constraints For Changes", §"Change Rules" (lines 83-end, ~1,100 chars) are modification rules.

Remix feasibility: **high in form, low in benefit**. The descriptive part dominates by ~5x; a split would leave a tiny modification file that is almost always loaded together with the descriptive reference when actually changing structural code.

### `ai/DESIGN.md` (6,569 chars)

Sections are mostly descriptive (Principles, Decisions, Non-Functional, Supported Scope, Public Behavior, Resilience, Security, Data, Domain Vocabulary, Roadmap Alignment). Only "Decision Hooks" (~600 chars) acts as modification guidance. Remix feasibility: **low**. Splitting yields a 5,900-char reference + a 600-char fragment that is too small to justify a separate file.

### `ai/CODE_STYLE.md` (3,420 chars)

The whole file is modification guidance — "Change Discipline", "Reuse First", "Edit Discipline", "Naming", "Cross-Refs". There is no reference content to split out. Remix feasibility: **none useful**.

### `ai/EXECUTION.md`, `ai/PLANNING.md`, `ai/WORKFLOW.md`, `ai/RELEASES.md`, `ai/TESTING.md`, `ai/REVIEWS.md`

These are by construction "imperative for the active lifecycle phase". They already follow the pattern "load only in that phase" and have already been compacted by routing deep mechanics into `ai/references/`. Re-splitting along reference/modification lines does not match their content shape.

### `ai/LEARNINGS.md`, `ai/ENVIRONMENT_QUICK_REF.md`, `ai/TASK_LIBRARY.md`

Each is small (≤3.3k chars) and already on demand. The fixed cost of an extra file (header, cross-references, loader entry) likely exceeds the savings.

## Remix Options Considered

### Option A — Reference / Modification Split Per File

Split each owner guide into `_REF.md` (descriptive lookup) and `_EDIT.md` (modification rules). Apply only where content cleanly separates: `ai/DOCUMENTATION.md` → ownership vs routing; `ai/ARCHITECTURE.md` → snapshot vs change rules.

Estimated post-split sizes (with ~8% header/cross-ref overhead):

| New file | Chars | Tokens |
| --- | ---: | ---: |
| `ai/DOCUMENTATION_OWNERSHIP.md` | ~2,300 | ~575 |
| `ai/DOCUMENTATION_ROUTING.md` | ~4,900 | ~1,225 |
| `ai/ARCHITECTURE_SNAPSHOT.md` | ~5,400 | ~1,350 |
| `ai/ARCHITECTURE_CHANGE_RULES.md` | ~1,200 | ~300 |

Phase-load impact assuming new triggers can route precisely:

| Phase | Today | After split | Δ tokens | Δ % |
| --- | ---: | ---: | ---: | ---: |
| "Where does X live?" lookup | DOCUMENTATION (1,716) | OWNERSHIP only (575) | −1,141 | −66% |
| "I'm reading code structure" | ARCHITECTURE (1,575) | SNAPSHOT only (1,350) | −225 | −14% |
| Public-API change (must read both halves of DOCUMENTATION) | 1,716 | 575 + 1,225 = 1,800 | **+84** | **+5%** |
| Architecture change (both halves) | 1,575 | 1,350 + 300 = 1,650 | **+75** | **+5%** |
| Implementation phase implied conditional set | ~9,200 | ~9,300 | +100 | +1% |

Net: large savings only on "pure lookup" reads (rare in real lifecycles) and small *regression* on combined-load phases (the common case for change work). The split also doubles the artifact count and the surface for stale cross-references.

### Option B — Lifecycle-Bundled Mix

Merge fragments of `ARCHITECTURE`, `DESIGN`, and `DOCUMENTATION` into per-lifecycle bundles, e.g.:

- `ai/IMPLEMENTATION_CONTEXT.md` (architecture snapshot + code-style + documentation routing)
- `ai/DISCOVERY_CONTEXT.md` (design intent + architecture snapshot + roadmap notes)
- `ai/REVIEW_CONTEXT.md` (documentation routing + review + testing)

Estimated bundles (with ~10% bundling overhead and minor de-duplication of cross-refs):

| Bundle | Chars | Tokens |
| --- | ---: | ---: |
| `IMPLEMENTATION_CONTEXT.md` (ARCH snapshot 5,400 + CODE_STYLE 3,420 + DOC routing 4,900) | ~14,900 | ~3,725 |
| `DISCOVERY_CONTEXT.md` (DESIGN 6,569 + ARCH snapshot 5,400 + LEARNINGS 3,259) | ~16,800 | ~4,200 |
| `REVIEW_CONTEXT.md` (DOC routing 4,900 + REVIEWS 2,650 + TESTING 4,942) | ~13,500 | ~3,375 |

Phase-load impact:

| Phase | Today (focused minimum) | Bundled |
| --- | ---: | ---: |
| Implementation minimum (`AGENTS` + `EXECUTION`) | 5,371 | 5,371 (unchanged) |
| Implementation broad conditional (5 files today) | 9,207 | `AGENTS` + `EXECUTION` + `IMPLEMENTATION_CONTEXT` = ~9,200 (≈ same) |
| Implementation when only architecture is needed | `AGENTS` + `EXECUTION` + `ARCHITECTURE` = 7,346 | `AGENTS` + `EXECUTION` + `IMPLEMENTATION_CONTEXT` = ~9,200 (**+1,850, +25%**) |
| Pure documentation routing question | `AGENTS` + `DOCUMENTATION` = 5,114 | `AGENTS` + `IMPLEMENTATION_CONTEXT` = ~7,400 (**+2,300, +45%**) |

Bundling **regresses** narrow loads because the agent now pays for code-style and routing tokens even when reading only the architecture snapshot, and pays for architecture and code-style tokens when the question is purely about documentation routing. Bundling also loses the clean owner story that `ai/DOCUMENTATION.md` is the single artifact-ownership authority.

### Option C — Status Quo with Tighter Triggers

Keep the current 14-file owner set. Strengthen on-demand triggers in `AGENTS.md` "Lifecycle Owner Map" and in each guide's first paragraph so that:

- "lookup where artifact X lives" reads only `ai/DOCUMENTATION.md` §"Artifact Ownership" (already a self-contained section);
- "structural code reading" reads only `ai/ARCHITECTURE.md` §"System Snapshot" + §"Package Layout";
- design context loads only when user-visible behavior, supported scope, security posture, or roadmap tradeoffs are touched.

Implementation cost: a small pointer paragraph at the top of each split-friendly file telling the agent which section to read in lookup-only scenarios. Estimated savings: similar to Option A's lookup case (≈1,000-1,400 tokens on rare pure-lookup phases) without adding new files or new cross-reference debt.

## Context-Usage Summary

| Scenario | Today | Option A | Option B | Option C |
| --- | ---: | ---: | ---: | ---: |
| Default load (`AGENTS.md`) | 3,698 | 3,698 | 3,698 | 3,698 |
| Implementation broad set | 9,207 | ~9,300 (+1%) | ~9,200 (≈) | 9,207 |
| Implementation, architecture-only need | 7,346 | 7,121 (−3%) | ~9,200 (+25%) | ≤7,346 (often less with section pointer) |
| Public-API change (full DOC + ARCH) | 4,989 | ~5,200 (+4%) | ~5,000 (≈) | 4,989 |
| Pure ownership lookup | 5,414 | 4,273 (−21%) | ~7,400 (+37%) | ~4,400 (−19%) |
| Structural-only reading | 5,273 | 5,048 (−4%) | ~7,400 (+40%) | ~4,800 (−9%) |
| Doc-set maintenance count | 14 files | 16 files | 11 files | 14 files |

Numbers are estimates rounded to the nearest 100 tokens; assumptions are stated under each option.

## Risks Of Remixing

- **Cross-reference drift**: every split or bundle re-points existing cross-references in `AGENTS.md`, `ai/DOCUMENTATION.md`, `ai/EXECUTION.md`, `ai/PLANNING.md`, task bodies, and skill references. The post-compaction report already grades the current set "A-" on duplication; reshuffling raises that risk.
- **Dual-load regression**: most modification phases want the descriptive *and* the rule content together. Splitting forces agents to load both halves, paying the file-overhead twice.
- **Bundling forfeits owner clarity**: today `ai/DOCUMENTATION.md` is the single owner of artifact routing. Bundling distributes that authority into per-lifecycle files, which contradicts the "owner clarity" principle that earned the current set its A grade.
- **Marginal gains**: the largest realistic win (Option A on `ai/DOCUMENTATION.md`) is ~1,100 tokens per pure-lookup task. The default load is unchanged in every option because `AGENTS.md` is the only standing file.
- **Active-plan inventory dwarfs owner-file size**: the post-compaction report shows active plans at 23,211 tokens and skill references at 111,158 tokens. Optimizing 14 owner files for ~1k-token wins is local-optimum work while the bigger lever is timely plan archival and strict skill-reference gating.

## Recommendation

**Adopt Option C (status quo with tighter triggers).** Concrete steps:

1. Add a one-line "lookup-only entry point" pointer at the top of `ai/DOCUMENTATION.md` and `ai/ARCHITECTURE.md` directing agents to the specific section to read when the task is a pure ownership lookup or a pure structural read.
2. Tighten the `AGENTS.md` "Lifecycle Owner Map" rows so descriptive guides (`ARCHITECTURE`, `DESIGN`, `LEARNINGS`) explicitly stay out of routine implementation and routine review loads.
3. **Do not** create `_REF.md` / `_EDIT.md` siblings or per-lifecycle bundles. The savings are marginal (≤5% on common phases, sometimes negative) and the cross-reference cost is real.
4. **Do not** restructure `ai/CODE_STYLE.md`, `ai/DESIGN.md`, `ai/LEARNINGS.md`, or any of the imperative phase guides; their content shape does not benefit from a reference/modification split.
5. Re-evaluate only after the next round of release cleanup retires implemented active plans, since active-plan inventory currently dominates per-task context far more than owner-file shape.

## Conclusion

The current owner-file layout already loads conditionally and is graded **A** in the post-compaction evaluation. A reference/modification split is structurally feasible only for `ai/DOCUMENTATION.md` and `ai/ARCHITECTURE.md`, and even there it produces ≤5% phase-load savings on common change work while introducing cross-reference debt. Lifecycle bundling actively regresses narrow loads. The cheapest improvement is sharper section-level triggers inside today's files, not a remix of the file set.
