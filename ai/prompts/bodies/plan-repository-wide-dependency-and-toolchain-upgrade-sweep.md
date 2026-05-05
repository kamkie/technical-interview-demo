# Plan Repository-Wide Dependency And Toolchain Upgrade Sweep

Category: Planning
Placeholders: topic

## Category Guidance


For planning prompts, use the lifecycle vocabulary from `ai/PLAN.md`.
Do not force a plan into `Phase=Planning` if the work should still be `Discovery` or `Needs Input`.


## Prompt Body

```markdown
Create `ai/PLAN_<topic>.md` for a repository-wide dependency and toolchain upgrade sweep.

Read `AGENTS.md`, `ai/PLAN.md`, `ai/DOCUMENTATION.md`, `ai/TESTING.md`, the relevant build files, Dockerfiles, workflow files, and any current alert, scan, or dependency-report output first.
Inventory all directly owned version surfaces before planning changes, including application dependencies, Gradle plugins, the Gradle wrapper, `buildSrc`, Docker base images, GitHub Actions, and other checked-in build or packaging tools.
Propose the smallest reviewable execution shape that still upgrades the full owned surface, and say explicitly if the work is too broad for one plan and should be split into multiple `ai/PLAN_*.md` files.
For each planned batch, call out compatibility risk, rollback or migration concerns, the exact resolved-version proof expected during execution, and the validation needed to keep the upgraded repo release-ready.
```
