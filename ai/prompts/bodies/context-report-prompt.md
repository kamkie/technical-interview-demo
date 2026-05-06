# Context Report Prompt

Prepare a report measuring how much context the repository AI instructions consume over time.

Use a temporary git worktree. Analyze the diff between the last commit and current HEAD, unless a specific commit range is provided as input (e.g., `START..END` or `START~1..HEAD`). Analyze commits in chronological first-parent order.

Measure context size in:

- characters
- bytes
- lines
- words
- estimated tokens using `ceiling(chars / 4)`

Count only repository AI instruction material:

- `AGENTS.md`
- files under `ai/`

Categorize measurements into these broad buckets:

- standing root and AI top-level files
- active plan files, meaning top-level `ai/PLAN_*.md`
- archived plans
- on-demand prompts
- on-demand references
- on-demand templates
- repo skill entrypoints
- repo skill references

Exclude archived material under `ai/archive/` from default and generic prompt loads unless explicitly loaded by a prompt. Still include archive size in total AI instruction inventory reporting.

For each commit, measure these scenarios:

1. Default load: `AGENTS.md` only.
2. Short prompt: use this exact prompt text: `Briefly summarize the current project state from AGENTS.md`.
3. Generic lifecycle prompts: measure these exact prompts for each phase:
   - discovery: `[DISCOVERY]: Research the current implementation of business logic and identify governing specs.`
   - planning: `[PLAN]: Create a detailed execution plan for a new business feature following AGENTS.md rules.`
   - implementation: `[EXECUTE]: Implement the core logic and tests for the feature defined in an active PLAN_*.md file.`
   - testing: `[TEST]: Verify the implementation with integration tests and negative scenarios based on TESTING.md.`
   - review: `[REVIEW]: Conduct a security and maintainability review of recent changes using REVIEWS.md.`
   - integration: `[INTEGRATE]: Merge the completed implementation, update the roadmap, and perform final validation.`
   - release: `[RELEASE]: Prepare the release artifacts and update the roadmap and changelog per RELEASES.md.`

Use the relevant loader rules from the repo docs when deciding which AI files each prompt would load.

Create two markdown tables.

Table 1: Summary by commit

Columns:

- commit
- commit date
- subject
- default load size
- short prompt size
- discovery prompt size
- planning prompt size
- implementation prompt size
- testing prompt size
- review prompt size
- integration prompt size
- release prompt size
- total AI instruction size

Include both character and estimated-token values. Use compact formatting if the table would otherwise become too wide.

Table 2: File/directory size by commit

Rows should include every AI file or directory that exists in any analyzed commit.

Columns should be one column per commit plus a final `total` column.

Use empty values where a file or directory did not exist on that commit.

For directories, report recursive totals.

Add a summary section before the tables.

The summary section should briefly state:

- the oldest commit and newest commit analyzed
- the overall standing-load trend
- the total measured AI markdown inventory trend over the analyzed range
- the most important finding in one or two sentences

Keep the summary practical and focused on actionable insights regarding context efficiency.

Add a statistics section after the summary.

The statistics section should include:

- **Baseline Metrics**: Smallest and biggest context use for each measured scenario.
- **Improvement Trend**: Oldest-to-newest improvement for each scenario and total inventory, reported as absolute character delta, absolute estimated-token delta, and percentage change.
- **Context Density**: Ratio of Standing Context (`AGENTS.md` + top-level `ai/*.md`) to Total AI Inventory.
- **Growth Velocity**: Average character and token change per analyzed commit.
- **Bloat Factor**: The percentage overhead added by active plans and on-demand prompts relative to the default load.

When reporting smallest and biggest context use, include the commit hash, commit date, subject, character count, and estimated-token count.

Add an interpretation section after the statistics section.

The interpretation section should explain:

- whether standing context is increasing, decreasing, or mostly stable
- which files or directories are the largest contributors to context use
- which commits caused the largest context increases or reductions
- whether the repo is moving toward better on-demand loading or toward larger default context
- any caveats in the measurement method, especially the approximate token estimate and inferred prompt-load behavior

Add a recommendations section after the interpretation section.

The recommendations section should provide concrete next actions, such as:

- files that should be compacted, split, archived, or moved to on-demand references
- loader-policy changes that would reduce default context
- prompt or AI-documentation changes that would make future context use easier to measure
- thresholds or guardrails worth adding for future AI instruction size growth

Do not run the build, tests, or validation checks.

Write the final report under a temporary directory outside the worktree, for example:

`temp/context-report.md`

After the report is written, delete the temporary git worktree. Leave the report file in place.
