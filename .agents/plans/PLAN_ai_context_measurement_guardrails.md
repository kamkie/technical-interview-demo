# Plan: AI Instruction Context Measurement Guardrails

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Planning |
| Status | Ready |

## Summary
- Add repeatable tooling that measures repository AI-instruction context size the same way each time instead of relying on one-off generated reports.
- Add a recurring guardrail that warns when default AI load or total `.agents/` inventory grows past configured thresholds without an explicit archive/report rationale.
- Support both endpoint comparison for oldest-to-newest range summaries and stepwise comparison for commit-by-commit movement.
- Success means maintainers and AI agents can run one command to generate the context report under gitignored `temp/`, compare a commit range, and see actionable warnings for context bloat.
- Roadmap tracking: `ROADMAP.md` tracks this under Planned Work as AI context measurement guardrails.

## Scope
- In scope:
  - a committed script for AI-instruction context measurement and report generation
  - deterministic scenario definitions for default load, short request, generic lifecycle task loads, and total AI inventory
  - a comparison-mode option for endpoint range summaries versus stepwise commit-by-commit analysis
  - guardrail thresholds for `AGENTS.md` default-load growth and total `.agents/` inventory growth
  - warning output when growth crosses thresholds without a clear archive/report rationale
  - updates to the `context-report` repo-task starter so it delegates measurement to the script instead of restating report mechanics
  - lightweight documentation in the owning AI guide or task reference only where needed to make recurring use discoverable
- Out of scope:
  - changing application runtime behavior, public API contracts, OpenAPI, REST Docs, HTTP examples, or setup behavior
  - enforcing the guardrail as a hard CI failure by default
  - committing generated context reports from `temp/`
  - rewriting archived plans or reports to reduce historical inventory
  - replacing the repository knowledge layout or repo-task dispatcher model

## Current State
- The `context-report` repo-task starter can generate a report manually, but the measurement logic currently lives in the executing agent's ad hoc implementation rather than a committed script.
- Generated reports are written under repo-root `temp/`, which is already gitignored.
- The latest generated report recommended two follow-ups: add a recurring guardrail for default load and total `.agents/` inventory, and add a reusable script so future reports use the same inferred scenario file sets.
- `.agents/skills/repo-task/references/tasks/context-report.md` owns the reusable task entry point for report generation.
- `.agents/references/repository-knowledge-spec.md` assigns active execution plans to `.agents/plans/`, generated AI reports to `.agents/reports/`, and local generated output to `.agents/tmp/` or `temp/` when explicitly required by a workflow.

## Requirement Gaps And Open Questions
- Should the guardrail later run in CI, a Git hook, or only as an explicit maintainer command?
  Fallback: implement it as an explicit local command first and leave CI integration as a documented future option.
- What counts as an acceptable "archive/report rationale" for total inventory growth above the threshold?
  Fallback: treat a growth-crossing commit as rationalized when the commit range adds or modifies files under `.agents/archive/` or `.agents/reports/`, or when a report summary explicitly names the growth source.
- Should active plan growth be treated as bloat?
  Fallback: include active plans in total inventory and bloat-factor reporting, but do not warn solely on active-plan growth unless the total inventory threshold is crossed.
- Should the script support non-Windows shells?
  Fallback: implement a PowerShell entry point first because repository local automation already uses PowerShell, while keeping parsing logic simple enough to port later if needed.

## Locked Decisions And Assumptions
- Default load means `AGENTS.md` only.
- Total AI inventory means `AGENTS.md` plus all files under `.agents/`.
- Estimated tokens use `ceiling(chars / 4)`.
- The first guardrail threshold is a warning when `AGENTS.md` grows by more than 5% over the selected comparison base.
- The second guardrail threshold is a warning when total AI inventory grows by more than 10% over the selected comparison base without a rationale from archive/report movement or an explicit report note.
- Generated context reports and temporary worktree material must stay out of commits.
- The reusable script should default to comparing the previous first-parent commit with `HEAD`, while accepting an explicit commit range.
- Comparison mode defaults to `endpoint`, meaning tables and improvement statistics compare only the oldest and newest commits in the selected range.
- `stepwise` comparison mode measures every selected commit and reports adjacent commit-to-commit deltas, including largest increases and reductions.
- Context report generation must keep using a temporary git worktree or Git object reads so dirty working-tree files do not corrupt historical measurements.

## Execution Shape And Shared Files
- Recommended shape: one local branch, implemented as two small milestones.
- This work does not need delegation because the script, task starter, and roadmap row are tightly coupled.
- If later delegation is used, keep `ROADMAP.md`, `.agents/skills/repo-task/references/tasks/context-report.md`, and final validation coordinator-owned.

## Affected Artifacts
- Likely new source file: `scripts/ai/context-report.ps1` or another narrow script path chosen during implementation.
- Likely updated task starter: `.agents/skills/repo-task/references/tasks/context-report.md`.
- Likely updated AI guidance: `.agents/references/testing.md`, `.agents/references/documentation.md`, or `.agents/references/execution.md` only if recurring guardrail usage becomes durable standing policy.
- Likely updated roadmap tracking: `ROADMAP.md`.
- Optional changelog entry if the implementation changes maintainer-facing AI workflow, not for this planning-only spec.
- No application source, tests, REST Docs, OpenAPI, HTTP examples, README, or setup docs are expected to change.

## Execution Milestones
### Milestone 1: Reusable Context Report Script
- Goal: move the current context-report measurement mechanics into a committed, repeatable script.
- Owned files or packages:
  - `scripts/ai/context-report.ps1` or the implementation-chosen script path
  - `.agents/skills/repo-task/references/tasks/context-report.md`
- Shared files reserved to the coordinator:
  - `ROADMAP.md`
  - `CHANGELOG.md`, if implementation records a maintainer-facing unreleased change
- Context required before execution:
  - `AGENTS.md`
  - `.agents/references/execution.md`
  - `.agents/references/documentation.md`
  - `.agents/references/testing.md`
  - `.agents/skills/repo-task/SKILL.md`
  - `.agents/skills/repo-task/references/tasks/context-report.md`
  - this plan
- Behavior to preserve:
  - reports are generated under gitignored `temp/`
  - temporary git worktrees are deleted after report generation
  - archived material is counted in total inventory but not loaded by default or generic task scenarios
  - no build, tests, or validation commands are run by the report task itself
- Exact deliverables:
  - script accepts an optional commit range and output path
  - script accepts an optional comparison mode with at least `endpoint` and `stepwise` values
  - script writes the same report sections as the current task: summary, statistics, interpretation, recommendations, summary-by-commit table, and file/directory table
  - script reports characters, bytes, lines, words, and estimated tokens
  - script uses stable scenario file sets that match `AGENTS.md` owner-map behavior
  - endpoint mode includes only oldest and newest commit columns while noting omitted interior commits
  - stepwise mode includes every selected commit and adjacent-delta reporting
  - `context-report` task starter calls the script instead of embedding implementation details
- Validation checkpoint:
  - run the script for `HEAD~1..HEAD` in endpoint mode and confirm it writes a report under `temp/`
  - run the script for a multi-commit range in stepwise mode and confirm commit-by-commit deltas appear
  - confirm the temporary worktree is removed
  - run `git diff --check`
  - run `./build.ps1 build` and record whether it takes the lightweight shortcut
- Commit checkpoint:
  - commit after the script and task starter are validated.

### Milestone 2: Growth Guardrail Warnings
- Goal: add warning logic for context growth thresholds so report output calls out likely AI-instruction bloat.
- Owned files or packages:
  - context-report script from Milestone 1
  - `.agents/skills/repo-task/references/tasks/context-report.md` if task usage text changes
  - owning AI guide only if durable recurring-use policy is added
- Shared files reserved to the coordinator:
  - `ROADMAP.md`
  - `CHANGELOG.md`, if implementation records a maintainer-facing unreleased change
- Context required before execution:
  - `AGENTS.md`
  - `.agents/references/execution.md`
  - `.agents/references/documentation.md`
  - `.agents/references/testing.md`
  - this plan
- Behavior to preserve:
  - guardrails warn by default; they do not fail CI or block commits unless a later task explicitly asks for enforcement
  - endpoint threshold checks compare oldest-to-newest commits in the selected range
  - stepwise threshold checks report both endpoint threshold status and adjacent commits that cross thresholds
  - reports remain deterministic for the same commit range
- Exact deliverables:
  - warning when default load grows by more than 5%
  - warning when total AI inventory grows by more than 10% and no archive/report rationale is detected
  - report section that shows threshold values, measured deltas, and whether each guardrail passed, warned, or was not applicable
  - command-line flags or documented variables that allow threshold tuning without editing the script
  - tests or script-level self-checks for threshold calculations if the implementation language supports them cleanly
- Validation checkpoint:
  - run the script on a range that does not cross thresholds and confirm no false warning
  - run a deterministic local synthetic or parameterized check that proves warning formatting for threshold crossings
  - run `git diff --check`
  - run `./build.ps1 build` and record whether it takes the lightweight shortcut
- Commit checkpoint:
  - commit after warning behavior and docs are validated.

## Edge Cases And Failure Modes
- Missing or invalid commit range should produce a clear usage error without leaving a worktree behind.
- Re-running the script should not overwrite an existing report unless an explicit output path is supplied.
- Binary files under `.agents/` should be counted by bytes and decoded with replacement only for character, line, and word estimates.
- File names containing spaces or unusual characters should render safely in Markdown tables.
- If `git worktree add` fails, the script should stop before producing a partial report.
- If cleanup fails, the script should print the worktree path so a maintainer can remove it manually.
- Dirty working-tree changes must not be included unless the script explicitly supports a separate working-tree mode.
- Large archive/report additions should still appear in total inventory and table output even when they are accepted as rationalized growth.

## Validation Plan
- For this planning spec:
  - run `git diff --check`
  - run `./build.ps1 build` and record the wrapper result
- For future implementation:
  - run the context report script against `HEAD~1..HEAD` in endpoint mode
  - run the context report script against a multi-commit range in stepwise mode
  - inspect the generated `temp/context-report-*.md`
  - verify temporary worktree cleanup with `git worktree list`
  - verify guardrail warning behavior with a deterministic threshold-crossing check
  - run `git diff --check`
  - run `./build.ps1 build`

## Testing Strategy
- Unit tests: optional if the script is PowerShell-only; prefer small pure calculation helpers that can be exercised by a script self-check.
- Integration tests: not applicable to application behavior.
- Contract tests: not applicable; no public API or OpenAPI behavior changes.
- Smoke checks: run the script on a real commit range and inspect report creation plus cleanup.
- Negative scenarios: invalid range, invalid comparison mode, missing output directory, threshold crossing, and rationalized archive/report growth.

## Better Engineering Notes
- Prefer Git object reads over copying entire worktrees when possible; use temporary worktrees only for paths or commands that require a checkout.
- Keep scenario definitions and comparison-mode definitions in one data structure so `context-report` task wording, report generation, and guardrail calculations cannot drift.
- The guardrail should make bloat visible without blocking legitimate archival or report-generation work.
- If this script proves useful in CI, add enforcement in a later milestone or plan rather than silently changing this plan's warning-only boundary.

## Validation Results
- 2026-05-07 planning-spec validation:
  - `git diff --check` - passed
  - `./build.ps1 build` - lightweight shortcut; Gradle build skipped because only `.agents/plans/PLAN_ai_context_measurement_guardrails.md` and `ROADMAP.md` changed
- 2026-05-07 comparison-mode revision validation:
  - `git diff --check` - passed
  - `./build.ps1 build` - lightweight shortcut; Gradle build skipped because only `.agents/plans/PLAN_ai_context_measurement_guardrails.md`, `.agents/skills/repo-task/references/tasks/context-report.md`, and `CHANGELOG.md` changed

## User Validation
- Run the implemented command and confirm it creates a report under `temp/`.
- Confirm the report includes guardrail status and recommendations.
- Confirm `git status --short` does not show generated report output.

## Required Content Checklist
- Behavior changing: repeatable AI-instruction context measurement plus warning guardrails.
- Roadmap entry: `ROADMAP.md` Planned Work row for AI context measurement guardrails.
- Out of scope: app runtime behavior, public API contracts, hard CI enforcement, committed generated reports.
- Governing specs/artifacts: this plan, `AGENTS.md`, `.agents/references/repository-knowledge-spec.md`, `.agents/skills/repo-task/references/tasks/context-report.md`.
- Likely source files: a new script under `scripts/ai/`, context-report task starter, and possibly a focused AI reference if durable policy is needed.
- Compatibility: existing `context-report` task behavior remains available with less ad hoc implementation.
- Edge cases: invalid ranges, cleanup failure, binary files, dirty worktree isolation, threshold rationale detection.
- Requirement gaps: CI versus local-only, rationale definition, active-plan growth treatment, cross-shell support; all have fallbacks.
- Execution shape: one local branch, no delegation.
- Coordinator-owned files if delegation appears: `ROADMAP.md`, `CHANGELOG.md`, and final validation evidence.
- Per-milestone context: named explicitly in each milestone.
- Tests/docs/artifacts: script smoke checks, threshold checks, task starter update, no app contract artifacts.
- Completion proof: report generation, worktree cleanup, guardrail warnings, `git diff --check`, and `./build.ps1 build`.
- User verification: run command, inspect report, confirm generated output remains ignored.
