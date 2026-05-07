Compact the standing AI instruction files.

## Scope

- Read `AGENTS.md`, `ai/DOCUMENTATION.md`, `ai/PROMPTS.md`, and standing top-level owner guides under `ai/` first; exclude active `ai/plans/active/PLAN_*.md` files unless they are relevant to the compaction target.
- Do not bulk-load `ai/archive/`, `ai/references/`, `ai/prompts/bodies/`, `ai/templates/`, or `ai/skills/`; open them only when a cross-reference or overlapping policy points there.
- Check `WORKING_WITH_AI.md` only when human-facing workflow wording overlaps with the AI guidance being changed.

## Compaction Targets

Look for any of the following, not only verbatim duplicates:

- exact or near-duplicate sentences across standing files
- overlapping or restated policies (same intent, different wording)
- guidance placed outside its owning file (per `ai/DOCUMENTATION.md` ownership rules)
- stale references (renamed/moved files, archived plans, retired prompts)
- verbose phrasing, redundant lists, or examples that belong in `ai/references/` or `ai/templates/`
- orphaned cross-references and broken anchors
- stale workflow references and anchors after delegated-work mechanics move between `ai/WORKFLOW.md` and `ai/references/`, checked with targeted `rg` searches rather than bulk-loading all references
- accumulated history inside standing guideline files: changelog notes, "previously…/now…" wording, migration narratives, dated decisions, deprecation traces, user-request history, prior directive wording, or rationale about past states rather than current rules
- misplaced historical context: released history belongs in `CHANGELOG.md`, durable lessons in `ai/LEARNINGS.md`, completed plans in `ai/archive/`, and still-useful guideline history in an on-demand reference

Use targeted project searches, preferably `rg`, with 1–3 distinctive keywords per policy before opening large files.

## Rules

- Keep each file role-distinct; do not collapse roles.
- Move guidance to its single best owning file and replace the other locations with a short cross-reference.
- Preserve normative wording (MUST/SHOULD-style rules); compact only structure, examples, and restatements.
- Do not delete guidance whose owner is unclear — flag it in the summary instead.
- When removing accumulated history, keep only the current rule; relocate any still-useful lesson to `ai/LEARNINGS.md`, released-history wording to `CHANGELOG.md`, and guideline-history context to an on-demand reference instead of dropping it silently.
- Propose new files only if the current owners cannot stay role-distinct without them.
- Do not touch `ai/archive/` content.

## Deliverables

- Tightened standing files with overlap removed and cross-references updated.
- A short summary listing: files changed, guidance relocated (from → to), references fixed, and any flagged-but-not-changed items with rationale.
