# Measure AI Context

Prepare a report measuring how much context the repository AI instructions consume over time.

Run the checked-in report script:

```powershell
./scripts/ai/context-report.ps1
```

Use `-Range START..END` when the user provides a range. Otherwise let the script compare the previous first-parent commit with `HEAD`.

Use `-Mode endpoint` by default and `-Mode stepwise` only when the user asks for commit-by-commit movement.

The script owns measurement details, scenario file sets, table shape, temporary worktree handling, report format, and warning-only guardrail status.
Use guardrail tuning flags only when the user asks to tune or explain thresholds.

Do not run the build, tests, or validation checks.

After the report is written, confirm the temporary git worktree was removed and leave the report file in place under gitignored `temp/`.
