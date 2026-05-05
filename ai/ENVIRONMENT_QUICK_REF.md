# AI Environment Quick Reference

`ai/ENVIRONMENT_QUICK_REF.md` owns the AI-facing shortcut for running local Gradle commands.
Use `SETUP.md` for human setup, prerequisites, and troubleshooting detail.
Use `ai/TESTING.md` to decide which validation is required.

## Preferred Commands

PowerShell:

```powershell
./build.ps1 build
./build.ps1 -FullBuild build
./build.ps1 test
./build.ps1 bootRun
./build.ps1 gatlingBenchmark
```

Bash:

```bash
./build.sh build
./build.sh test
./build.sh bootRun
./build.sh gatlingBenchmark
```

## What The Wrappers Do

- load root `.env` automatically when it exists
- pass every Gradle argument through to the Gradle wrapper
- in PowerShell, let `./build.ps1 build` skip Gradle when the uncommitted change set is lightweight-only
- in PowerShell, let `./build.ps1 -FullBuild build` force the full Gradle build
- keep direct `gradlew` usage available when the shell is already configured
- let Gradle's toolchain checks report Java misconfiguration clearly

## Instruction-Writing Rule

Plans, prompts, and worker logs should name the wrapper command directly.
Do not add preliminary steps to discover `JAVA_HOME`, dot-source `scripts/load-dotenv.ps1`, or inspect local Java paths unless the wrapper command actually fails and the next task is troubleshooting.

Use `.env.example` only as the template for expected local variable names.
Never treat `.env.example` as evidence of the user's local values.
