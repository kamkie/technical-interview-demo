# Toolchain Upgrade

Create `.agents/plans/PLAN_<topic>.md` for a toolchain upgrade batch.

Scope this task to these owned upgrade surfaces only:

- CI job actions
- Java dependencies
- Gradle plugins and tools
- Docker images

Read `AGENTS.md`, `.agents/references/planning.md`, `.agents/references/documentation.md`, `.agents/references/testing.md`, relevant build/workflow/Docker files, and the alert, version target, scan, dependency report, or tool output that motivates the upgrade first.

Before writing the plan, identify where each requested version is owned, including workflow action pins, direct Java dependencies, transitive constraints or overrides, Gradle plugin versions, Gradle wrapper versions, `buildSrc`, Docker base images, and checked-in Gradle or packaging tools.
Keep the plan narrow enough to review.
Inventory the owned version surfaces in scope, call out compatibility risk, rollback or migration concerns, resolved-version proof, and validation needed to keep the repository release-ready.
Say explicitly whether the requested upgrades should stay one batch or split into smaller plans.
Do not implement upgrades from this task unless the user separately asks to execute the resulting plan.
