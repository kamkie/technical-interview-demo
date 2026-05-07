# Evaluate AI Guidelines

Category: Lifecycle And Maintenance
Slug: `evaluate-ai-guidelines`
Placeholders: none

Evaluate the live repository AI guideline set, including deep context-load analysis and full lifecycle conformance against `docs/specs/application-lifecycle-spec.md`, then write a timestamped report under gitignored `temp/`.
Use this as an evaluation/reporting task, not as permission to implement follow-up recommendations.

## Prompt Examples

These prompts should resolve to this task:

- `evaluate-ai-guidelines`
- `Evaluate AI Guidelines`
- `Run a deep AI guidelines assessment.`
- `Grade the current AI guidance against application-lifecycle-spec.md.`
- `Assess AGENTS.md and .agents guidance for lifecycle conformance and context load.`
- `Find AI-guidance contradictions, active-plan bloat, stale references, and lifecycle gaps.`
- `Create a thorough AI guideline report with phases, activities, loops, triggers, artifact owners, and recommendations.`
- `Create a durable tracked AI-guidelines snapshot.` Only this form allows a tracked report; otherwise write generated output under `temp/`.

## Read Set

Start with:

- `AGENTS.md`
- `.agents/references/documentation.md`
- `.agents/skills/repo-task/references/spec.md`
- `.agents/skills/repo-task/references/index.md`
- this task file
- `docs/specs/application-lifecycle-spec.md`

Then load:

- standing owner guides under `.agents/references/`
- `docs/ARCHITECTURE.md` and `docs/DESIGN.md` only for descriptive context needed by lifecycle or owner mapping
- active `.agents/plans/PLAN_*.md` files only for lifecycle state, active-read-set impact, roadmap cleanup, and stale live references
- `ROADMAP.md`, `CHANGELOG.md`, `README.md`, `SETUP.md`, `CONTRIBUTING.md`, and `WORKING_WITH_AI.md` when checking ownership, lifecycle routing, human/AI alignment, or release-state truth
- task files, templates, on-demand references, repo-local skills, archived reports, or archived plans only when targeted searches or measurements identify a concrete reason

Do not bulk-load `.agents/archive/`.

## Required Measurements

Generate or reuse a same-session context report when it covers current `HEAD` and relevant uncommitted AI-guidance changes.
Use `scripts/ai/context-report.ps1` through the `context-report` task method when generating a new report.

Measure or extract:

- default load
- standing owner-guide size
- practical read sets for discovery, planning, implementation, testing, review, integration, and release
- active-plan inventory and largest active-plan files
- on-demand references, templates, repo-task tasks, repo-local skill entrypoints, and large skill references
- archived inventory size and largest archived contributors
- total tracked AI instruction inventory
- context density and bloat factor when the context report provides them
- guardrail status for default-load and total-inventory growth

## Lifecycle Model To Evaluate

Use `docs/specs/application-lifecycle-spec.md` as the lifecycle reference.
The report must explicitly evaluate every lifecycle element below.

### Definitions And Mechanics

Check that live guidance has clear owner concepts or deliberate mappings for:

- `Phase`: one current coarse lifecycle stage per work item with explicit entry and exit criteria
- `Activity`: focused work inside a phase, with the question it answers, owner artifact, and exit condition
- `Switch`: explicit transition between activities or phases, with context hygiene before loading the next working set
- `Trigger`: planned or conditional signal that mandates a switch
- `Loop`: activity or phase sequence that iterates until a defined exit condition
- `Artifact`: durable spec, contract, plan, log, or guide
- `Owner`: exactly one authoritative artifact or role for a rule, phase, or activity
- `Gate`: executable check where possible, otherwise named human approval

### Phases

| # | Phase | Entry | Exit / Gate |
| --- | --- | --- | --- |
| 1 | Discovery | request, idea, or signal arrives | scope and ambiguity surfaced; rejected or promoted to Roadmap |
| 2 | Roadmap Intake | promoted Discovery item | item sequenced and prioritized in active-work tracking |
| 3 | Planning | roadmap item is picked up | decision-complete plan exists and is approved |
| 4 | Implementation | approved plan exists | smallest spec-driven change exists locally and self-validates |
| 5 | Testing | implementation is locally complete | required validation passes |
| 6 | Review | validated change exists | reviewer approval, or return to Implementation/Testing |
| 7 | Integration | review is approved | change lands on integration branch and post-merge checks pass |
| 8 | Release | integrated change is release-ready | versioned artifact is published and release notes exist |
| 9 | Deployment | released artifact exists | artifact runs in target environment and verifies green |
| 10 | Operations | artifact is live | signals are observed; incidents triaged or scheduled |
| 11 | Continuous Improvement | release closed or recurring signal observed | learnings captured; next-cycle Roadmap updated |

`Closed` is a terminal plan status, not a phase.
Check that phase transitions are explicit, cannot be skipped unless the change class allows it, can re-enter upstream phases when gates fail, and use executable or named-approval gates.

### Phase Activities

Evaluate activity ownership and coverage for:

- Discovery and Framing: `Scan`, `Frame`, `Clarify?`, `Capture?`
- Roadmap and Requirements: `Intake`, `Refine`, `Prioritize`, `Sequence`, `Sync`
- Design, Spec, and Planning: `Design`, `Spec`, `Decompose`, `Validate-Plan`, `Replan?`
- Implementation: `Code`, `Docs`, `Commit`, `Handoff`
- Testing and Verification: `Plan-Tests`, `Author-Tests`, `Run`, `Diagnose?`, `Fix?`, `Re-run`, `Record`
- Review: `Self-Review`, `Code Review`, `Security Review?`, `Docs Review?`, `Decide`
- Integration: `Re-validate`, `Resolve-Conflicts?`, `Merge`, `Post-Merge-Verify`
- Release: `Gate`, `Tag`, `Notes`, `Publish`, `Post-Release-Cleanup`
- Deployment: `Stage`, `Smoke`, `Promote`, `Verify`, `Rollback?`
- Operations: `Observe`, `Triage`, `Hotfix?`, `Patch?`, `Backport?`, `Deprecate?`
- Continuous Improvement: `Retrospect`, `Capture-Learning`, `Refactor?`, `Tech-Debt-Plan?`, `Sync`

### In-Order Activity Sequences

Check that owner guidance supports these sequences:

- Discovery: `Scan` -> `Frame` -> `Clarify?` -> `Capture?`
- Roadmap Intake: `Intake` -> `Refine` -> `Prioritize` -> `Sequence` -> `Sync`
- Planning: `Frame` -> `Design` -> `Spec` -> `Decompose` -> `Validate-Plan` -> `Sync` -> `Replan?`
- Implementation: `Spec` -> `Code` -> `Docs` -> `Run` -> `Replan?` -> `Self-Review` -> `Code Review` -> `Security Review?` -> `Commit` -> `Handoff`
- Testing: `Plan-Tests` -> `Author-Tests` -> `Run` -> `Diagnose?` -> `Fix?` -> `Re-run` -> `Record`
- Review: `Self-Review` -> `Code Review` -> `Security Review?` -> `Docs Review?` -> `Decide`
- Integration: `Re-validate` -> `Resolve-Conflicts?` -> `Merge` -> `Post-Merge-Verify`
- Release: `Gate` -> `Tag` -> `Notes` -> `Publish` -> `Post-Release-Cleanup`
- Deployment: `Stage` -> `Smoke` -> `Promote` -> `Verify` -> `Rollback?`
- Operations: `Observe` -> `Triage` -> `Hotfix?` -> `Patch?` -> `Backport?` -> `Deprecate?`
- Continuous Improvement: `Retrospect` -> `Capture-Learning` -> `Refactor?` -> `Tech-Debt-Plan?` -> `Sync`

### Loops

Evaluate whether guidance defines entry, cadence, exit, and owner artifacts for:

- nested loop relationship: Outer Product Loop contains the Operate-and-Improve Loop and Plan Loop; Plan Loop contains the Milestone Execution Loop; Milestone Execution Loop contains the Red-Green Loop and Review Loop
- Outer Product Loop: roadmap `Sync` -> planning -> implementation -> release -> `Retrospect` -> `Capture-Learning` -> `Sync`; cadence per release
- Plan Loop: `Frame` -> `Design` -> `Spec` -> `Decompose` -> `Validate-Plan` -> `Replan?` -> `Validate-Plan`; cadence per plan until decision-complete
- Milestone Execution Loop: `Spec` -> `Code` -> `Docs` -> `Run` -> `Replan?` -> `Self-Review` -> `Code Review` -> `Security Review?` -> `Commit` -> `Handoff`; cadence per milestone
- Red-Green Loop: `Run` -> `Diagnose` -> `Fix` -> `Re-run`; cadence per failing validation
- Review Loop: `Self-Review` -> `Code Review` -> `Security Review?` -> `Docs Review?` -> `Decide`, then back to `Code` or `Run` if changes are requested
- Operate-and-Improve Loop: `Observe` -> `Triage` -> `Hotfix?` or `Patch?` -> `Capture-Learning` -> `Sync`; cadence post-release and continuous

### Cross-Cutting Triggers

Evaluate trigger ownership and clarity for:

- `Replan`
- `Security Review`
- `Sync`
- `Capture-Learning`
- `Docs-Routing`
- `Context-Hygiene`
- `Rollback`
- `Hotfix`

### Required Artifact Roles

Map each role to current repo artifacts, identify gaps, and note whether the owner is human-facing, AI-facing, executable, or published:

- Project Charter
- Setup Guide
- Roadmap
- Release History
- Plan
- Executable Spec
- Published Contract
- Engineering Rules
- Phase Owner Guides
- Learnings
- Architecture Snapshot

### Spec-Driven Rules And Gates

Evaluate whether live guidance enforces:

- spec-first behavior changes
- truth priority: current user request, executable specs, published contract docs, roadmap current-cycle state, active planning, release history, AI/human guidance
- definition of done: intended behavior in spec, implementation/spec agreement, public contract updates, required validation recorded, integration branch or PR state, active-work tracking, release artifact and notes when released
- branch/worktree invariants: integration branch as completed-work truth, side-branch isolation, merge preference, cherry-pick reason, no releases from unintegrated state
- lifecycle roles: Requester, Planner, Implementer, Tester, Reviewer, Integrator, Releaser, Operator, Curator
- adoption/conformance coverage: artifact set, activity-owner mapping, workflow-mode support, activity tags when applicable, change-class routing, validation table, gates, cross-cutting triggers, roadmap gaps, and declared conformance level
- spec versioning and compatibility coverage: pinned lifecycle spec version or equivalent declared baseline, handling for breaking lifecycle changes, additive lifecycle changes, clarifications, and rename maps
- lifecycle non-goal boundaries: no false claim that the lifecycle spec mandates a specific branch model, issue tracker, CI system, hosting provider, file layout, agile ceremony, team size, sprint length, release cadence, or replacement for product/security/compliance policy

## Evaluation Rubric

Grade the live guide set for:

- owner clarity and single-source ownership
- lifecycle conformance and activity coverage
- default-load discipline and practical read-set cost
- on-demand trigger clarity
- repo-task index and task-file quality
- active-plan lifecycle hygiene
- skill, reference, template, report, and archive containment
- duplication, contradiction, and stale-reference risk
- execution, testing, review, documentation, workflow, release, deployment, operations, and improvement usefulness
- recommendation actionability

Use current measurements and live-file evidence.
Treat archived reports as historical comparison only when they materially answer the question.

## Required Output

Write `temp/evaluate-ai-guidelines-<timestamp>.md`.

The report must include:

- assessment date, commit boundary, branch, and whether uncommitted changes were included
- executive summary with overall grade and top risks
- method and evidence sources, including the context report path when used
- current size baseline and practical read-set estimates
- lifecycle definition and mechanics findings for phase, activity, switch, trigger, loop, artifact, owner, and gate concepts
- lifecycle conformance summary with a phase-by-phase matrix for all eleven phases
- activity coverage matrix for all phase activities
- loop coverage matrix for all six lifecycle loops
- cross-cutting trigger coverage matrix
- required artifact-role mapping and gaps
- spec-driven development, truth-priority, definition-of-done, branch/worktree, role, adoption, conformance-level, spec-versioning, and lifecycle non-goal findings
- scorecard with category grades and concise rationale
- file-by-file grades for standing owner guides and important task/skill files
- active-plan lifecycle and read-set hygiene findings
- top contradictions, stale references, and policy-duplication risks with file references
- realized gains, remaining costs, and obsolete recommendations
- ranked recommendations with owner file, expected benefit, estimated context impact, implementation risk, and validation needed
- `do now`, `defer`, and `do not do` sections
- caveats about approximate token estimates, inferred load behavior, and archived historical material

If the user explicitly asks for a durable tracked snapshot, place the report under `.agents/archive/` and update `CHANGELOG.md`.
Otherwise, keep generated output under gitignored `temp/`.

## Guardrails

- Do not implement recommendations unless explicitly asked.
- Keep standing policy in the owning guide named by `.agents/references/documentation.md`.
- Keep archived content historical; do not rewrite `.agents/archive/` only to remove old terminology.
- Prefer `temp/` for generated evaluation output.
- If a live contradiction makes grading unreliable, list it as a blocker before recommendations.

## Validation

For an evaluation that only writes a report under `temp/`, do not run build, tests, or heavyweight validation.

If tracked files are edited while running this task, run:

```powershell
git diff --check
./build.ps1 build
```

If the wrapper takes the lightweight-file shortcut, record that Gradle execution was skipped and manual consistency review was the relevant validation.

Final response: overall grade, highest-risk lifecycle or context issue, most important size/load change, top three recommendations, report path, tracked files changed, and validation results.
