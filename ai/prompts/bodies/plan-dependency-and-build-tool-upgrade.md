Create `ai/PLAN_<topic>.md` for this dependency or build-tool upgrade batch:
- <item 1>
- <item 2>

Read `AGENTS.md`, `ai/PLAN.md`, `ai/DOCUMENTATION.md`, `ai/TESTING.md`, the relevant build files, and the exact alert, version target, or tool output that motivates the upgrade first.
Identify where each version is actually owned before planning changes, including direct dependencies, transitive overrides, Gradle plugins, wrapper updates, `buildSrc`, workflow actions, or other build tooling.
Keep the plan narrow, call out compatibility and validation risk explicitly, name the resolved-version proof expected during execution, and say whether the work should stay one reviewable batch or be split into smaller upgrade plans.
