# Quick Reference: AI Environment Setup

## For AI Agents (Short Version)

### Run Gradle Commands
```powershell
# Use the wrapper scripts - they auto-load .env
. ./build.ps1 build
. ./build.ps1 test
. ./build.ps1 bootRun
. ./build.ps1 gatlingBenchmark
```

### What Happens Automatically
✅ `.env` is loaded (if it exists in repo root)
✅ `JAVA_HOME` is discovered from environment or common paths
✅ Java version is validated (Java 25 required)
✅ Clear error messages appear if anything is wrong

### No Need To
❌ Manually discover or set `JAVA_HOME`
❌ Run `load-dotenv.ps1` separately each time
❌ Add environment setup steps to instructions
❌ Include Java path diagnostics in plan files

### If Environment Setup Still Fails
The Gradle initialization hook (`gradle/init.gradle.kts`) will show:
```
❌ Java toolchain not found!

To fix:
  1. Install Java 25 (or set JAVA_HOME to existing Java 25)
  2. Copy .env.example to .env and fill in JAVA_HOME
  3. Run: . ./scripts/load-dotenv.ps1 -Quiet
  4. Try again: .\gradlew.bat build
```

### Instruction Writing Changes

**Before (with environment setup):**
```text
Milestone: Build the application
1. Discover JAVA_HOME location
2. Set environment variables
3. Load .env if available
4. Run ./gradlew build
5. Validate build succeeded
```

**After (automatic):**
```text
Milestone: Build the application
1. Run . ./build.ps1 build
2. Validate build succeeded
```

---

**TL;DR**: Use `./build.ps1` instead of `./gradlew.bat`. Everything else happens automatically.
