# Context Report

Category: Lifecycle And Maintenance
Slug: `context-report`
Placeholders: none

Prepare a report measuring how much context the repository AI instructions consume over time.

Run the checked-in report script:

```powershell
./scripts/ai/context-report.ps1
```

Use `-Range START..END` when the user provides a range. Otherwise let the script compare the previous first-parent commit with `HEAD`.

Use `-Mode endpoint` for compact oldest-to-newest range summaries. This is the default.
Use `-Mode stepwise` when the user asks for commit-by-commit or per-commit movement.

The script owns the measurement details, scenario file sets, table shape, temporary worktree handling, report format, and warning-only guardrail status.
Use `-DefaultLoadWarningPercent`, `-TotalInventoryWarningPercent`, and `-GrowthRationale` only when the user asks to tune or explain guardrail thresholds.

Do not run the build, tests, or validation checks.

After the report is written, confirm the temporary git worktree was removed and leave the report file in place under gitignored `temp/`.
