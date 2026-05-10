# Evaluate AI Guidance

Evaluate the live repository AI guidance for lifecycle conformance, owner clarity, stale references, duplication, and context-load cost.
Write `temp/evaluate-ai-guidance-<timestamp>.md` unless the user explicitly asks for a tracked archive report.
Do not implement recommendations during this task.
When no additional arguments are passed, report only on the current repository state.
Do comparison or trend analysis only when the user provides explicit git references, such as commits, tags, branches, or a range.

Read first: `AGENTS.md`, `.agents/references/documentation.md`, this task, and `docs/specs/application-lifecycle-spec.md`.
Load `docs/specs/lifecycle-phase-activities.md`, owner guides, active plans, human-facing docs, skills, templates, or archives only when needed to answer a specific finding.
Do not bulk-load `.agents/archive/`.

Generate or reuse a same-session context report when it covers the requested boundary and relevant uncommitted AI-guidance changes.
Use `scripts/ai/context-report.ps1` through the `measure-ai-context` task when generating a new report.

Evaluate:

- ownership clarity and single-source guidance
- lifecycle phase, activity, loop, trigger, artifact, owner, and gate coverage against the lifecycle specs
- spec-driven development, truth priority, definition of done, branch/worktree, validation, review, integration, release, deployment, operations, and continuous-improvement coverage
- default-load discipline, practical read sets, `.agents/tasks/` prompt quality, active-plan hygiene, skill/template/archive containment, and stale-reference risk
- contradictions, policy duplication, missing routing, and recommendations with owner file, expected benefit, context impact, implementation risk, and validation needed

Report shape:

- assessment date, branch, commit boundary, and whether uncommitted changes were included
- comparison boundary only when explicit git references were provided
- method and evidence sources, including context report path when used
- executive summary with grade, top risks, and top recommendations
- lifecycle conformance matrix summarized from the spec, not copied wholesale
- context-size and read-set findings
- file-by-file findings for important owner guides and task prompts
- `do now`, `defer`, and `do not do` recommendations
- caveats about approximate token estimates, inferred load behavior, and historical archive material

For report-only work under `temp/`, do not run builds, tests, or heavyweight validation.
If tracked files are edited, record the documentation review performed and any skipped validation explicitly.
