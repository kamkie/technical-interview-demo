# Wrapper for the Gradle wrapper that auto-loads .env before execution
#
# Usage:
#   Run this script instead of gradlew.bat:
#     ./build.ps1 build
#     ./build.ps1 -FullBuild build
#     ./build.ps1 -SkipTests -SkipChecks build
#     ./build.ps1 bootRun
#
# Or add an alias in your PowerShell profile:
#     Set-Alias build "path/to/repo/build.ps1"
#
# Benefits:
#   - Automatically loads .env if it exists
#   - Skips the Gradle build for lightweight-only uncommitted changes
#   - Allows forcing the full Gradle build with -FullBuild
#   - Supports local-loop shortcuts with -SkipTests and -SkipChecks
#   - No manual environment setup needed per session
#   - Faster AI instruction execution (no env discovery steps)

param (
    [Alias("ForceFullBuild")]
    [switch]$FullBuild,

    [switch]$SkipTests,

    [switch]$SkipChecks,

    [Parameter(ValueFromRemainingArguments=$true)]
    [string[]]$GradleArgs = @()
)

$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

$repoRoot = if ([string]::IsNullOrWhiteSpace($PSScriptRoot)) {
    (Get-Location).Path
} else {
    $PSScriptRoot
}

function Get-RequestedGradleTasks {
    param(
        [string[]]$Arguments = @()
    )

    $optionsWithValues = @(
        "-b",
        "--build-file",
        "-c",
        "--settings-file",
        "-g",
        "--gradle-user-home",
        "-I",
        "--init-script",
        "-p",
        "--project-dir",
        "-x",
        "--exclude-task",
        "--include-build",
        "--project-cache-dir",
        "--tests"
    )

    $tasks = @()
    $skipNext = $false
    foreach ($argument in $Arguments) {
        if ($skipNext) {
            $skipNext = $false
            continue
        }

        if ([string]::IsNullOrWhiteSpace($argument)) {
            continue
        }

        if ($argument.StartsWith("-")) {
            if ($optionsWithValues -contains $argument) {
                $skipNext = $true
            }
            continue
        }

        $tasks += $argument
    }

    return $tasks
}

function Test-IsBuildOnlyInvocation {
    param(
        [string[]]$Arguments = @()
    )

    $requestedTasks = @(Get-RequestedGradleTasks -Arguments $Arguments)
    if ($requestedTasks.Count -ne 1) {
        return $false
    }

    return $requestedTasks[0] -eq "build" -or $requestedTasks[0] -eq ":build"
}

function Test-ShouldSkipBuildForLightweightChanges {
    param(
        [string]$Root,
        [string[]]$Arguments = @()
    )

    if ($FullBuild -or -not (Test-IsBuildOnlyInvocation -Arguments $Arguments)) {
        return $false
    }

    $classifierScript = Join-Path $Root "scripts\classify-changed-files.ps1"
    if (-not (Test-Path -LiteralPath $classifierScript)) {
        Write-Warning "Could not find scripts\classify-changed-files.ps1; running Gradle build."
        return $false
    }

    try {
        $classification = & $classifierScript -Uncommitted
    }
    catch {
        Write-Warning "Could not classify changed files: $_. Running Gradle build."
        return $false
    }

    if (-not [bool]$classification.skipHeavyValidation) {
        return $false
    }

    $changedFiles = @($classification.changedFiles)
    Write-Host "Only lightweight files changed; skipping Gradle build."
    Write-Host "Manual consistency review is sufficient for these uncommitted changes."
    Write-Host "Use './build.ps1 -FullBuild build' to force the full Gradle build."

    if ($changedFiles.Count -gt 0) {
        Write-Host "Changed files:"
        $changedFiles | ForEach-Object { Write-Host "  $_" }
    }

    return $true
}

function Get-EffectiveGradleArgs {
    param(
        [string[]]$Arguments = @()
    )

    $effectiveArguments = @($Arguments)

    if ($SkipTests) {
        $effectiveArguments += @(
            "-x",
            "test",
            "-x",
            "jacocoTestReport",
            "-x",
            "jacocoCoverageSummary",
            "-x",
            "jacocoTestCoverageVerification",
            "-x",
            "asciidoctor"
        )
    }

    if ($SkipChecks) {
        $checkTasks = @(
            "jacocoTestCoverageVerification",
            "pmdMain",
            "pmdTest",
            "pmdExternalTest",
            "pmdGatling",
            "spotbugsMain",
            "spotbugsTest",
            "spotbugsExternalTest",
            "spotbugsGatling",
            "spotlessCheck",
            "spotlessKotlinGradleCheck",
            "spotlessMiscCheck",
            "staticSecurityScan",
            "dependencyVulnerabilityScan",
            "imageVulnerabilityScan",
            "vulnerabilityScan",
            "applicationSbom",
            "imageSbom",
            "sbom"
        )

        $effectiveArguments += "-PskipChecks=true"
        foreach ($task in $checkTasks) {
            $effectiveArguments += @("-x", $task)
        }
    }

    return $effectiveArguments
}

# Step 1: Auto-load .env if it exists
$envPath = Join-Path $repoRoot ".env"
if (Test-Path -LiteralPath $envPath) {
    try {
        $dotenvScript = Join-Path $repoRoot "scripts\load-dotenv.ps1"
        if (Test-Path -LiteralPath $dotenvScript) {
            & $dotenvScript -Path $envPath -Quiet
        }
    }
    catch {
        Write-Warning "Could not load .env: $_"
    }
}

$gradleExitCode = 0
$effectiveGradleArgs = @(Get-EffectiveGradleArgs -Arguments $GradleArgs)
Push-Location $repoRoot
try {
    # Step 2: Skip the default validation build when only lightweight files changed
    if (Test-ShouldSkipBuildForLightweightChanges -Root $repoRoot -Arguments $effectiveGradleArgs) {
        return
    }

    # Step 3: Call the platform Gradle wrapper with all effective Gradle arguments
    $runningOnWindows = [System.Runtime.InteropServices.RuntimeInformation]::IsOSPlatform(
        [System.Runtime.InteropServices.OSPlatform]::Windows
    )
    $gradlewFileName = if ($runningOnWindows) { "gradlew.bat" } else { "gradlew" }
    $gradlewPath = Join-Path $repoRoot $gradlewFileName
    if (-not (Test-Path -LiteralPath $gradlewPath)) {
        throw "$gradlewFileName not found in repository root: $repoRoot"
    }

    & $gradlewPath @effectiveGradleArgs
    $gradleExitCode = $LASTEXITCODE
}
finally {
    Pop-Location
}

if ($gradleExitCode -ne 0) {
    exit $gradleExitCode
}
