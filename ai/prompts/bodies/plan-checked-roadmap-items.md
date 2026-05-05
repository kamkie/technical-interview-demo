# Plan Checked Roadmap Items

Category: Planning
Placeholders: topic

## Category Guidance


For planning prompts, use the lifecycle vocabulary from `ai/PLAN.md`.
Do not force a plan into `Phase=Planning` if the work should still be `Discovery` or `Needs Input`.


## Prompt Body

```markdown
Create one coherent `ai/PLAN_<topic>.md` from every checklist item marked `[x]` in `ROADMAP.md`.

Use only the checked items unless the roadmap text makes a dependency explicit.
Restate exactly which checked items were included.
If the checked items do not form one coherent executable plan, stop and explain the gap instead of guessing.
Record unresolved requirement gaps and fallback assumptions explicitly.
Set the lifecycle state from `ai/PLAN.md` based on actual planning readiness.
```
