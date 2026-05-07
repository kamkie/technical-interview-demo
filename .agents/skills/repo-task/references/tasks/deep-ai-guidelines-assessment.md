# Deep AI Guidelines Assessment

Category: Lifecycle And Maintenance
Slug: `deep-ai-guidelines-assessment`
Placeholders: none

Deeply analyze, evaluate, and grade the current repository AI guideline set, then provide prioritized recommendations for improvement.

Use this as an evidence-based assessment task, not as an implementation request for the recommendations it discovers.
Base the assessment on the current `Context Report` measurement method and the latest archived AI-guideline evaluation under `ai/archive/reports/`, when one exists.

#### Scope

- Read `AGENTS.md`, `ai/DOCUMENTATION.md`, `ai/TASK_LIBRARY.md`, and, if present, the latest matching archived AI-guideline evaluation under `ai/archive/reports/` first.
- Generate a fresh context report using the `Context Report` task method, or reuse a same-session context report only if it clearly covers the current HEAD and uncommitted AI-guidance changes.
- Read top-level owner guides under `ai/` because the task grades the current guideline set as a whole.
- Inspect active plans only for lifecycle state, context-load impact, stale references, and active-read-set hygiene.
- Inspect on-demand references, templates, task sections, and repo-local skills only when the context report, archived evaluation report, or targeted searches identify them as important load, drift, duplication, or stale-reference contributors.
- Do not bulk-load `ai/archive/`; read only matching report artifacts under `ai/archive/reports/`, and sample archived plans only when the assessment has a specific historical or stale-reference question.

#### Assessment Tasks

1. Recompute or verify the context-load evidence from the context report:
   - default load
   - phase-specific practical read sets
   - task-catalog load
   - active-plan inventory
   - archived inventory
   - on-demand references, templates, and skills
   - total tracked AI instruction inventory
2. Compare the current evidence with the latest archived AI-guideline evaluation report, when one exists.
3. Grade the guideline set using a consistent rubric:
   - owner clarity and single-source ownership
   - context efficiency and default-load discipline
   - on-demand trigger clarity
   - task-library quality and heading-search safety
   - active-plan lifecycle hygiene
   - skill, reference, template, and archive containment
   - duplication, contradiction, and stale-reference risk
   - execution, testing, review, documentation, workflow, and release usefulness
   - recommendation actionability
4. Identify concrete improvement opportunities, including:
   - guidance to compact, split, relocate, archive, or turn into an on-demand reference
   - task sections that are growing into policy dumps
   - owner-map or task-index changes that would reduce accidental loading
   - thresholds or guardrails for future context growth
   - reusable measurement or validation helpers worth adding
   - obsolete recommendations that should be retired
5. Distinguish true blockers from optional cleanup and preference-only edits.

#### Report Requirements

Write a standalone markdown report under a temporary directory outside the worktree, for example:

`temp/deep-ai-guidelines-assessment-<date>.md`

The report must include:

- assessment date, commit range or working-tree boundary, and whether uncommitted changes were included
- executive summary with overall grade
- evidence sources, including the context report path and the evaluation report path
- current size and load-set findings
- scorecard with category grades and concise rationale
- top risks and contradictions, with file references
- comparison with the previous evaluation report
- ranked recommendations with owner file, expected benefit, estimated context impact, implementation risk, and validation needed
- "do now", "defer", and "do not do" sections
- caveats about approximate token estimates and inferred load behavior

#### Guardrails

- Do not implement recommendations unless I explicitly ask.
- Do not move archived reports back into `ai/references/`; use `Evaluate AI Guidelines` only when a fresh evaluation snapshot is explicitly needed.
- Do not edit `CHANGELOG.md` unless a tracked report or AI-guidance file is intentionally updated.
- Keep standing policy in the owning guide named by `ai/DOCUMENTATION.md`; do not copy full policy into the assessment report.
- If a live contradiction makes grading unreliable, list it as a blocker before recommendations instead of smoothing over it.

#### Validation

Do not run the build, tests, or heavyweight validation checks for the assessment itself.
Run `git diff --check` only if the task edits tracked repository files.

In the final response, summarize:

- overall grade
- highest-risk context or guideline issue
- top three recommendations
- report path
- validation commands and results
