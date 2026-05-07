# Upgrade Dependencies And Build Tools

Category: Lifecycle And Maintenance
Slug: `upgrade-dependencies-and-build-tools`
Placeholders: <item 1>, <item 2>

Upgrade these dependencies or build tools:
- <item 1>
- <item 2>

Read `AGENTS.md`, `ai/EXECUTION.md`, `ai/DOCUMENTATION.md`, `ai/TESTING.md`, the governing plan if one exists, the relevant build files, and the exact alert, version target, or tool output first.
Confirm where each version is actually owned before editing anything, including direct dependencies, transitive constraints, Gradle plugins, wrapper versions, `buildSrc`, workflow actions, or other build tooling.
Prefer the smallest version or constraint change that satisfies the requested upgrade, keep unrelated version churn out of the diff, capture resolved-version evidence, and summarize the exact validation that proves the upgraded build still matches repo rules.
Do not push, open a PR, or release unless I ask.
