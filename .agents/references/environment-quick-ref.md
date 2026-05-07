# AI Environment Quick Reference

`.agents/references/environment-quick-ref.md` owns the AI-facing shortcut for running local Gradle commands.
Use `SETUP.md` for human setup, prerequisites, and troubleshooting detail.
Use `.agents/references/testing.md` to decide which validation is required.
Use `.agents/references/gradle-task-graph.md` when choosing between overlapping Gradle tasks.

## Preferred Commands

PowerShell (`pwsh`):

```powershell
./build.ps1 compileJava
./build.ps1 checkFormat
./build.ps1 format
./build.ps1 build
./build.ps1 -FullBuild build
./build.ps1 -SkipTests -SkipChecks build
./build.ps1 test
./build.ps1 bootRun
./build.ps1 imageVulnerabilityScan
./build.ps1 vulnerabilityScan
./build.ps1 gatlingBenchmark
```

Bash-compatible shells:

```bash
# build.ps1 is the repository wrapper; from Bash, invoke it through PowerShell.
pwsh ./build.ps1 compileJava
pwsh ./build.ps1 checkFormat
pwsh ./build.ps1 format
pwsh ./build.ps1 build
pwsh ./build.ps1 -FullBuild build
pwsh ./build.ps1 -SkipTests -SkipChecks build
pwsh ./build.ps1 test
pwsh ./build.ps1 bootRun
pwsh ./build.ps1 imageVulnerabilityScan
pwsh ./build.ps1 vulnerabilityScan
pwsh ./build.ps1 gatlingBenchmark
```

## What The Wrapper Does

- load root `.env` automatically when it exists
- pass every Gradle argument through to the Gradle wrapper
- let `./build.ps1 build` skip Gradle when the uncommitted change set is lightweight-only
- let `./build.ps1 -FullBuild build` force the full Gradle build
- let `./build.ps1 -SkipTests build` exclude test-dependent tasks for local loops
- let `./build.ps1 -SkipChecks build` exclude formatting, PMD, SpotBugs, Error Prone, coverage verification, the build-wired dependency vulnerability scan, explicit vulnerability scan tasks, and SBOM checks for local loops
- keep direct `gradlew` usage available when the shell is already configured
- let Gradle's toolchain checks report Java misconfiguration clearly

## Instruction-Writing Rule

Plans, task starters, and worker logs should name the wrapper command directly.
Do not add preliminary steps to discover `JAVA_HOME`, dot-source `scripts/load-dotenv.ps1`, or inspect local Java paths unless the wrapper command actually fails and the next task is troubleshooting.

Use `.env.example` only as the template for expected local variable names.
Never treat `.env.example` as evidence of the user's local values.
