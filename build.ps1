# Wrapper for gradlew.bat that auto-loads .env before execution
#
# Usage:
#   Run this script instead of gradlew.bat:
#     . build.ps1 build
#     . build.ps1 bootRun
#
# Or add an alias in your PowerShell profile:
#     Set-Alias build "path/to/repo/build.ps1"
#
# Benefits:
#   - Automatically loads .env if it exists
#   - Works identically to gradlew.bat
#   - No manual environment setup needed per session
#   - Faster AI instruction execution (no env discovery steps)

param (
    [Parameter(ValueFromRemainingArguments=$true)]
    [string[]]$GradleArgs
)

$ErrorActionPreference = "Stop"

# Step 1: Auto-load .env if it exists
$envPath = Join-Path (Get-Location) ".env"
if (Test-Path -LiteralPath $envPath) {
    try {
        $scriptDir = Split-Path -Parent $PSCommandPath
        $dotenvScript = Join-Path $scriptDir "scripts\load-dotenv.ps1"
        if (Test-Path -LiteralPath $dotenvScript) {
            & $dotenvScript -Path $envPath -Quiet
        }
    }
    catch {
        Write-Warning "Could not load .env: $_"
    }
}

# Step 2: Call gradlew.bat with all arguments
$gradlewPath = Join-Path (Get-Location) "gradlew.bat"
if (-not (Test-Path -LiteralPath $gradlewPath)) {
    throw "gradlew.bat not found in current directory or parent"
}

& $gradlewPath @GradleArgs
