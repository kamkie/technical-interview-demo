# Run Plan On Single Branch

Category: Workflow Execution
Placeholders: plan_file

## Category Guidance


Use these prompts when one request should actively execute planned work while also choosing or coordinating the workflow mode from `ai/WORKFLOW.md`.


## Prompt Body

```markdown
Execute `<plan_file>` in `Single Branch` mode.

Use `ai/WORKFLOW.md` and `ai/EXECUTION.md`.
Treat the canonical plan file and `CHANGELOG.md` as directly editable on the active branch.
Do not push, open a PR, or release unless I ask.
```
