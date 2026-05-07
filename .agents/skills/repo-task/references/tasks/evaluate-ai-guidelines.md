# Evaluate AI Guidelines

Category: Lifecycle And Maintenance
Slug: `evaluate-ai-guidelines`
Placeholders: none

Evaluate and grade the current repository AI guideline set, then write a timestamped report under gitignored `temp/`.
Use this as an evaluation/reporting task, not as permission to implement follow-up recommendations.

## Scope

- Read `AGENTS.md`, `.agents/references/documentation.md`, this task file, and the repo-task spec/index.
- Read the standing owner guides under `.agents/references/`.
- Exclude active `.agents/plans/PLAN_*.md` files from the standing-guide baseline; inspect them only for lifecycle hygiene, roadmap cleanup, and stale-reference checks.
- Inspect large task files, templates, on-demand references, repo-local skills, or archived reports only when targeted searches or current measurements show likely drift.
- Do not bulk-load `.agents/archive/`.

## Evaluation

Recompute or estimate:

- default load and standing owner-guide size
- practical read sets for planning, implementation, verification, workflow, and release
- active-plan inventory and largest on-demand contributors
- stale references, duplicated policy, and task-policy drift

Grade the live guide set for owner clarity, default-load necessity, trigger clarity, duplication risk, execution usefulness, and validation/review routing.
Use archived evaluations only as historical comparison when they are specifically useful.

## Report

Write `temp/evaluate-ai-guidelines-<timestamp>.md` with:

- evaluation date and commit boundary
- overall grade
- method and evidence sources
- current size baseline and practical read-set estimates
- rubric findings and file-by-file grades
- realized gains, remaining costs, obsolete recommendations, and ranked follow-ups

If the user explicitly asks for a durable tracked snapshot, place it under `.agents/archive/` and update `CHANGELOG.md`.

## Guardrails

- Do not implement recommendations unless explicitly asked.
- Keep standing policy in the owning guide named by `.agents/references/documentation.md`.
- Keep archived content historical; do not rewrite `.agents/archive/` only to remove old terminology.
- Prefer `temp/` for generated evaluation output.

## Validation

Run:

```powershell
git diff --check
./build.ps1 build
```

If the wrapper takes the lightweight-file shortcut, record that Gradle execution was skipped and manual consistency review was the relevant validation.

Final response: overall grade, most important size/load change, top follow-up recommendation, report path, files changed, and validation results.
