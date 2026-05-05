# PowerShell shell initialization for the technical-interview-demo repository.
#
# Add this to your PowerShell profile ($PROFILE) to auto-load .env:
#   . (Resolve-Path "path/to/repo/scripts/init-shell-env.ps1")
#
# Or source it manually before running Gradle:
#   . ./scripts/init-shell-env.ps1

param(
    [switch]$Verbose
)

$repoRoot = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
$envPath = Join-Path $repoRoot ".env"

if (Test-Path -LiteralPath $envPath) {
    try {
        & "$PSScriptRoot/load-dotenv.ps1" -Path $envPath -Quiet
        if ($Verbose) {
            Write-Host "✓ Loaded $envPath" -ForegroundColor Green
        }
    }
    catch {
        Write-Warning "Failed to load $envPath : $_"
    }
}
else {
    if ($Verbose) {
        Write-Host "ℹ No .env file found. Using system environment." -ForegroundColor Gray
    }
}
