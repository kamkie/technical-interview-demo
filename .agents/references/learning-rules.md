# Learning Rules For AI Agents

`.agents/references/learning-rules.md` owns when and how AI agents decide whether to record durable repo-wide lessons.
It does not store the lessons themselves; `.agents/references/LEARNINGS.md` stores approved durable lessons.

Use this file when a user correction, agent mistake, failed assumption, avoidable rework, repeated operational signal, release retrospective, or troubleshooting pattern may need durable learning.

## Scope Ladder

Use this ladder when deciding where a new insight belongs:

- **Focused owner rule:** update the owning guide, spec, API, name, test, or workflow when the lesson belongs to a specific domain.
- **Repo-wide and durable:** add it to `.agents/references/LEARNINGS.md` only when it is recurring, reusable across tasks, and likely to survive refactors.
- **Current structure or ownership:** put AI-facing guidance in `.agents/references/architecture.md`.
- **Product or contract direction:** put it in `docs/DESIGN.md`.
- **Public behavior or payload shape:** put it in the governing specs, published docs, and approved OpenAPI.
- **Symbol-local behavior:** put it in code near the symbol.
- **Naming or API confusion:** prefer renaming or reshaping the API over adding more prose.
- **Active work or follow-up:** put it in `ROADMAP.md`, an active plan, workflow state, or the final response when it is not a durable lesson yet.

## Learning Loop

Treat agent mistakes, user corrections, failed assumptions, and avoidable rework as signals that current guidance may be incomplete.
When a correction exposes a durable rule, update the focused owning artifact in the same change instead of relying on final-response memory.

Use this loop:

1. Name the mismatch between the assumption or action and the repository truth, user request, executable spec, or owner guide.
2. Decide whether the lesson is durable enough to help future agents avoid the same mistake.
3. Prefer fixing the owning guide, spec, API, name, or workflow over adding a broad lesson.
4. Add to `.agents/references/LEARNINGS.md` only when the lesson is repo-wide, recurring, and likely to survive refactors.
5. Write the lesson as a reusable rule or habit, not as an apology, incident timeline, or list of files changed.
6. If the lesson is real but cannot be safely recorded during the task, record the blocker or follow-up in the active plan or final response.

## When To Consider A Learning

Evaluate whether a durable repo-wide lesson should be added during the task, not only during release cleanup, when:

- a command, tool, or validation step fails in a way future agents could avoid
- the user corrects a repo assumption, workflow interpretation, or implementation direction
- a requested capability or workflow does not exist and the gap is likely to recur
- an external API, dependency, build tool, or platform behaves differently than expected
- an assumption is outdated compared with current repo truth, tool behavior, or supported contract
- existing AI guidance proves wrong, contradictory, incomplete, or requires an execution-time policy decision
- a better approach is discovered for a recurring task, diagnosis path, or validation loop

Add only lessons that should survive refactors.
Do not accumulate per-incident history, one-off mistakes, or temporary workaround notes in `.agents/references/LEARNINGS.md`.

## Cross-References

- `.agents/references/LEARNINGS.md`: durable repo-wide lesson storage.
- `.agents/references/documentation.md`: artifact ownership and cross-file alignment.
- `.agents/references/references-rules.md`: rules for creating, editing, splitting, and retiring reference documents.
