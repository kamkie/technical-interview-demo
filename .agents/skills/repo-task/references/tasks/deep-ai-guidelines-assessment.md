# Deep AI Guidelines Assessment

Category: Lifecycle And Maintenance
Slug: `deep-ai-guidelines-assessment`
Placeholders: none

Deeply assess the live repository AI guideline set and write a standalone report under gitignored `temp/`.
Use this as an evidence task, not as permission to implement the recommendations it finds.

## Scope

- Read `AGENTS.md`, `.agents/references/documentation.md`, this task file, and the repo-task spec/index.
- Generate or reuse a same-session context report when it covers current `HEAD` and relevant uncommitted AI-guidance changes.
- Read standing owner guides under `.agents/references/` because the task grades the guideline set as a whole.
- Inspect active `.agents/plans/PLAN_*.md` files only for lifecycle state, active-read-set impact, and stale live references.
- Inspect task files, templates, repo-local skills, archived reports, or archived plans only when the context report or targeted searches identify a concrete reason.
- Do not bulk-load `.agents/archive/`.

## Assessment

Evaluate:

- owner clarity and single-source ownership
- default-load discipline and practical read-set cost
- on-demand trigger clarity
- repo-task index and task-file quality
- active-plan lifecycle hygiene
- skill, reference, template, report, and archive containment
- duplication, contradiction, and stale-reference risk
- usefulness of execution, testing, review, documentation, workflow, and release guidance

Use current measurements and live-file evidence. Treat archived reports as historical comparison only when they materially answer the question.

## Report

Write `temp/deep-ai-guidelines-assessment-<timestamp>.md` with:

- assessment date, commit boundary, and uncommitted-change note
- executive summary and overall grade
- evidence sources, including the context report path when used
- current size and load-set findings
- scorecard with concise rationale
- top risks and contradictions with file references
- ranked recommendations with owner file, benefit, context impact, risk, and validation
- `do now`, `defer`, and `do not do` sections
- caveats about approximate token estimates and inferred load behavior

## Guardrails

- Do not implement recommendations unless explicitly asked.
- Do not create tracked report artifacts unless the user explicitly asks for a durable report.
- Keep standing policy in the owning guide named by `.agents/references/documentation.md`.
- If a live contradiction makes grading unreliable, list it as a blocker before recommendations.

## Validation

Do not run build, tests, or heavyweight validation for the assessment itself.
Run `git diff --check` only if tracked files are edited.

Final response: overall grade, highest-risk issue, top three recommendations, report path, and validation results.
