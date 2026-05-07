Evaluate and grade the current repository AI guideline set, then refresh `ai/references/AI_GUIDELINES_POST_COMPACTION_EVALUATION.md`.

Use this as an evaluation and reporting task, not as an implementation request for the recommendations it discovers.

## Scope

- Read `AGENTS.md`, `ai/DOCUMENTATION.md`, `ai/TASK_LIBRARY.md`, and standing top-level owner guides under `ai/`.
- Exclude active `ai/plans/active/PLAN_*.md` files from the standing-guide baseline, but inspect active plans when they are relevant to lifecycle state, roadmap cleanup, or stale-reference checks.
- Read `ai/task-library/index.json` and representative large task bodies when checking task-policy drift.
- Read on-demand references only when a standing guide points to them, when the previous evaluation report names them, or when a targeted search finds a likely stale reference.
- Do not bulk-load `ai/archive/` unless the evaluation specifically needs historical context.

## Evaluation Tasks

1. Recompute current standing guidance sizes:
   - `AGENTS.md`
   - top-level `ai/*.md` owner guides excluding active `ai/plans/active/PLAN_*.md`
   - key on-demand references that affect practical load, such as workflow and release references
2. Recompute practical read-set estimates:
   - standing root plus top-level owner guides
   - planning minimum
   - implementation minimum
   - broad implementation conditional set, if still relevant
   - workflow selection
   - verification
   - release policy
   - release policy plus release references
   - descriptive docs only
3. Grade each standing guide using a consistent rubric:
   - owner clarity
   - default-load necessity
   - trigger clarity for on-demand material
   - duplication or policy drift
   - execution usefulness
   - validation and review routing
4. Check for stale or duplicated guidance:
   - retired file names or moved guide references
   - old workflow terminology outside intentional historical notes
   - task bodies growing into standing policy dumps
   - repeated artifact-routing rules outside `ai/DOCUMENTATION.md`
   - release, workflow, validation, and planning mechanics duplicated across owners
5. Compare the current state to the previous contents of `ai/references/AI_GUIDELINES_POST_COMPACTION_EVALUATION.md`.
6. Refresh the report so it describes current repository truth, not the state at the previous evaluation.

## Report Requirements

Update `ai/references/AI_GUIDELINES_POST_COMPACTION_EVALUATION.md` with:

- evaluation date
- overall grade
- method
- current size baseline
- practical read-set estimates
- rubric findings
- file-by-file grades with short rationale
- realized gains since the previous report
- remaining costs and risks
- obsolete recommendations that should not be repeated
- ranked follow-up recommendations

Keep the report on demand. Do not move the report into a standing top-level guide.

## Guardrails

- Do not implement follow-up compaction recommendations unless the user explicitly asks for implementation.
- If a concrete active-guidance contradiction makes the report inaccurate, fix only the narrow contradiction needed for report accuracy or stop and explain the blocker.
- Keep standing policy in the owning guide named by `ai/DOCUMENTATION.md`; do not copy full policy into the report.
- Keep archived-plan wording historical. Do not rewrite `ai/archive/` just to remove old terminology.
- Update `CHANGELOG.md` under `## [Unreleased]` when the report is refreshed.

## Validation

Run:

```powershell
git diff --check
./build.ps1 build
```

If the wrapper takes the lightweight-file shortcut, record that the Gradle build was skipped and manual consistency review was the relevant validation.

In the final response, summarize:

- overall grade
- most important size or load-set change
- top follow-up recommendation
- files changed
- validation commands and results
