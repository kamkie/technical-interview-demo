[CmdletBinding()]
param(
    [string]$BaseUrl = "http://127.0.0.1:18080",
    [string]$BaselineOutput = "performance/baselines/phase-9-local.json",
    [string]$GithubClientId = "benchmark-client-id",
    [string]$GithubClientSecret = "benchmark-client-secret",
    [switch]$UpdateBaseline,
    [switch]$SkipBootJar
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$scriptDirectory = Split-Path -Parent $PSCommandPath
$repositoryRoot = Split-Path -Parent $scriptDirectory

Push-Location $repositoryRoot
try {
    if ($SkipBootJar) {
        Write-Host "SkipBootJar is ignored. gatlingBenchmark always runs against the packaged Docker image."
    }

    Write-Host "Delegating to .\\gradlew.bat gatlingBenchmark"
    $arguments = @(
        'gatlingBenchmark',
        "-Pbenchmark.baseUrl=$BaseUrl",
        "-Pbenchmark.baselineFile=$BaselineOutput",
        "-Pbenchmark.githubClientId=$GithubClientId",
        "-Pbenchmark.githubClientSecret=$GithubClientSecret",
        '--no-daemon'
    )
    if ($UpdateBaseline) {
        $arguments += '-Pbenchmark.updateBaseline=true'
    }

    & .\gradlew.bat @arguments | Out-Host
    if ($LASTEXITCODE -ne 0) {
        throw "gatlingBenchmark failed."
    }
}
finally {
    Pop-Location
}
