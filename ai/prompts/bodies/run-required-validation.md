# Run Required Validation

Category: Implementation Verification
Placeholders: change, plan_file

## Prompt Body

```markdown
Run only the required validation for `<plan_file>` or `<change>`.
Do not edit files.

Use `ai/TESTING.md` and `ai/DOCUMENTATION.md`.
Start with `pwsh ./scripts/classify-changed-files.ps1 -Uncommitted` unless another diff boundary is explicit.
Summarize what ran, what passed, what failed, what was skipped, and what artifacts would likely need updates.
```
