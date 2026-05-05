# Context Report Prompt

Prepare a report measuring how much context the repository AI instructions consume over time.

Use the previous simple before/after report as a style and measurement reference if it exists:

`C:\Users\kamki\AppData\Local\Temp\technical-interview-demo-ai-context-before-after-20260505-233853.md`

Do not overwrite that previous report.

Use a temporary git worktree. Analyze commits from `baa23259` through current `HEAD`, in chronological first-parent order.

Measure context size in:

- characters
- bytes
- lines
- words
- estimated tokens using `ceiling(chars / 4)`, matching the previous simple report

Count only repository AI instruction material:

- `AGENTS.md`
- files under `ai/`

Use the same broad buckets as the previous simple report where they apply:

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
2. Short prompt: use this exact prompt text: `[insert prompt]`.
3. Generic lifecycle prompts: measure one generic prompt for each phase:
   - planning
   - implementation
   - testing/verification
   - code review
   - release

Use the relevant loader rules from the repo docs when deciding which AI files each prompt would load.

Create two markdown tables.

Table 1: Summary by commit

Columns:

- commit
- commit date
- subject
- default load size
- short prompt size
- planning prompt size
- implementation prompt size
- testing prompt size
- review prompt size
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
- the total measured AI markdown inventory trend from `baa23259` to `HEAD`
- the most important finding in one or two sentences

Keep the summary practical and similar in tone to the previous simple report.

Add a statistics section after the summary.

The statistics section should include:

- commit with the smallest context use for each measured scenario
- commit with the biggest context use for each measured scenario
- oldest-to-newest improvement for each measured scenario, reported as absolute character delta, absolute estimated-token delta, and percentage change
- oldest-to-newest improvement for total measured AI instruction inventory, reported as absolute character delta, absolute estimated-token delta, and percentage change

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
