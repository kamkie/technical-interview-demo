# Automatic AI Environment Setup

## Overview

The repository now includes a **multi-layer automatic environment setup** system that eliminates boilerplate and speeds up AI agent execution. This document explains what changed and how it works.

## What Changed

### 1. **New Wrapper Scripts** ✅
- **`build.ps1`** (PowerShell): Auto-loads `.env` before calling `gradlew`
- **`build.sh`** (Bash): Unix/Linux equivalent
- **`scripts/init-shell-env.ps1`**: Shell profile integration for permanent auto-loading

### 2. **Gradle Initialization Hook** ✅
- **`gradle/init.gradle.kts`**: Runs on every Gradle invocation to:
  - Auto-detect Java toolchain
  - Validate Java version meets requirements
  - Provide clear error messages if misconfigured

### 3. **Configuration Updates** ✅
- **`gradle.properties`**: Added Java toolchain configuration section
- **`.env.example`**: Enhanced with detailed JAVA_HOME examples
- **`AGENTS.md`**: Added "AI Execution Environment" section with agent-specific guidance
- **`SETUP.md`**: Updated to reference new automatic setup

## How It Works for AI Agents

### Zero-Setup Execution Path

Instead of:
```powershell
# OLD: Manual setup steps in AI instructions
$env:JAVA_HOME='C:\path\to\jdk-25'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
. ./scripts/load-dotenv.ps1 -Quiet
.\gradlew.bat build
```

Now just use:
```powershell
# NEW: Automatic setup
. ./build.ps1 build
```

**What happens automatically:**
1. `build.ps1` loads `.env` (if it exists)
2. `build.ps1` calls `gradlew`
3. `gradle/init.gradle.kts` validates Java toolchain
4. Build proceeds or fails with clear error messages

### Benefits for AI Instructions

✅ **Shorter instructions**: No environment discovery or setup steps needed
✅ **Faster execution**: Fewer commands = faster completion
✅ **Clearer failure messages**: `gradle/init.gradle.kts` explains what went wrong
✅ **Idempotent setup**: Running the command twice works the same way both times
✅ **Backward compatible**: Old `.\gradlew.bat` commands still work

## User Setup (One-Time)

For end users setting up the repo:

```powershell
# 1. Copy template
Copy-Item .env.example .env

# 2. Edit .env to set JAVA_HOME (or leave blank to auto-detect)
notepad .env

# 3. Use the wrapper going forward
. ./build.ps1 build
. ./build.ps1 test
. ./build.ps1 bootRun

# Optional: Add to PowerShell profile for permanent auto-loading
. ./scripts/init-shell-env.ps1
```

## For AI Agents

When running commands in this repository:

**Recommended approach:**
```powershell
. ./build.ps1 build
. ./build.ps1 test
. ./build.ps1 gatlingBenchmark
```

**Environment variables:**
- `.env` is loaded automatically if it exists
- No need for AI steps to discover or set `JAVA_HOME`
- `gradle/init.gradle.kts` handles validation

**In instructions:**
- Reference the wrapper scripts in execution steps
- Skip environment-variable setup sections
- Trust that Gradle will fail gracefully with clear messages if Java isn't available

## Files Created/Modified

| File | Type | Purpose |
|------|------|---------|
| `build.ps1` | New | PowerShell wrapper that auto-loads `.env` before Gradle |
| `build.sh` | New | Bash wrapper for Unix/Linux |
| `gradle/init.gradle.kts` | New | Gradle initialization hook for Java toolchain validation |
| `gradle.properties` | Modified | Added Java toolchain configuration section |
| `.env.example` | Modified | Enhanced with JAVA_HOME setup examples |
| `AGENTS.md` | Modified | Added "AI Execution Environment" section |
| `SETUP.md` | Modified | Updated to reference automatic wrapper scripts |
| `scripts/init-shell-env.ps1` | Existing | Already in place from previous update |

## Execution Flow (New)

```
User/AI runs: . ./build.ps1 build
    ↓
build.ps1 checks for .env
    ↓
If .env exists → load-dotenv.ps1 loads it into environment
    ↓
build.ps1 calls ./gradlew build
    ↓
gradle/init.gradle.kts validates Java toolchain
    ↓
If Java OK → build proceeds normally
If Java missing → gradle/init.gradle.kts shows helpful error message
```

## Backward Compatibility

All existing commands still work:
- `.\gradlew.bat build` ✅ Still works (just skips auto `.env` loading)
- `./gradlew build` (Bash) ✅ Still works
- Direct Gradle commands ✅ Still work

The wrapper scripts are **optional conveniences**, not requirements.

## Next Steps for AI Instructions

Update plan files and execution guidance to:
- Use `./build.ps1` instead of `.\gradlew.bat` for clarity
- Skip "discover JAVA_HOME" diagnostic steps in plans
- Trust that Gradle error messages will appear if environment setup fails
- Reduce environment-centered boilerplate in AI instructions

---

**Result**: AI can execute builds faster with shorter, clearer instructions while maintaining full backward compatibility with existing workflows.
