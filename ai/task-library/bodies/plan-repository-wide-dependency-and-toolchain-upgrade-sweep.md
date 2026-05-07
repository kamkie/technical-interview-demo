Create `ai/plans/active/PLAN_<topic>.md` for a repository-wide dependency and toolchain upgrade sweep.

Read `AGENTS.md`, `ai/PLANNING.md`, `ai/DOCUMENTATION.md`, `ai/TESTING.md`, the relevant build files, Dockerfiles, workflow files, and any current alert, scan, or dependency-report output first.
Inventory all directly owned version surfaces before planning changes, including application dependencies, Gradle plugins, the Gradle wrapper, `buildSrc`, Docker base images, GitHub Actions, and other checked-in build or packaging tools.
Propose the smallest reviewable execution shape that still upgrades the full owned surface, and say explicitly if the work is too broad for one plan and should be split into multiple `ai/plans/active/PLAN_*.md` files.
For each planned batch, call out compatibility risk, rollback or migration concerns, the exact resolved-version proof expected during execution, and the validation needed to keep the upgraded repo release-ready.
