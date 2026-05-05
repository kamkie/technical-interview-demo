Run only the required validation for `<plan_file>` or `<change>`.
Do not edit files.

Use `ai/TESTING.md` and `ai/DOCUMENTATION.md`.
For local PowerShell validation, start with `./build.ps1 build`; it handles the uncommitted changed-file shortcut.
Use `pwsh ./scripts/classify-changed-files.ps1` directly only when another diff boundary is explicit.
Summarize what ran, what passed, what failed, what was skipped, and what artifacts would likely need updates.
