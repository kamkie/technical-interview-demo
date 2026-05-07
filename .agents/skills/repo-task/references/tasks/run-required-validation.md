# Run Required Validation

Category: Implementation Verification
Slug: `run-required-validation`
Placeholders: <change>, <plan_file>

Run only the required validation for `<plan_file>` or `<change>`.
Do not edit files.

Use `.agents/references/testing.md` and `.agents/references/documentation.md`.
Use `./build.ps1 compileJava` or a similarly focused task for quick loop checks, and use `./build.ps1 build` for final verification.
Do not run overlapping Gradle validation tasks in parallel, including `build` with `gatlingBenchmark`, `externalSmokeTest`, `externalDeploymentCheck`, or `scheduledExternalCheck`.
Use `pwsh ./scripts/classify-changed-files.ps1` directly only when another diff boundary is explicit.
Summarize what ran, what passed, what failed, what was skipped, and what artifacts would likely need updates.
