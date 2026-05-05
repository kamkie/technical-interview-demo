# Revise Plan

Category: Planning
Placeholders: constraint, plan_file

## Category Guidance


For planning prompts, use the lifecycle vocabulary from `ai/PLAN.md`.
Do not force a plan into `Phase=Planning` if the work should still be `Discovery` or `Needs Input`.


## Prompt Body

```markdown
Revise `<plan_file>` for this new requirement or constraint:
<constraint>

Keep the plan self-contained and follow `ai/PLAN.md`.
Update the lifecycle state only as far as the revision justifies.
```
