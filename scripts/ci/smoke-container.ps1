[CmdletBinding()]
param(
    [string]$ImageName = "technical-interview-demo",
    [string]$PostgresImage = "postgres:16-alpine",
    [string]$DatabaseName = "technical_interview_demo",
    [string]$DatabaseUser = "postgres",
    [string]$DatabasePassword = "changeme",
    [string]$NetworkName = "technical-interview-demo-smoke-network",
    [string]$PostgresContainerName = "technical-interview-demo-smoke-postgres",
    [string]$AppContainerName = "technical-interview-demo-smoke-app",
    [int]$HostPort = 18080,
    [int]$TimeoutSeconds = 120
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$scriptDirectory = Split-Path -Parent $PSCommandPath
$repositoryRoot = Split-Path -Parent (Split-Path -Parent $scriptDirectory)

Push-Location $repositoryRoot
try {
    Write-Host "Delegating to .\\gradlew.bat externalSmokeTest"
    $arguments = @(
        'externalSmokeTest',
        "-PexternalSmokeImageName=$ImageName",
        "-PdockerImageName=$ImageName",
        "-PexternalSmoke.postgresImage=$PostgresImage",
        "-PexternalSmoke.databaseName=$DatabaseName",
        "-PexternalSmoke.databaseUser=$DatabaseUser",
        "-PexternalSmoke.databasePassword=$DatabasePassword",
        "-PexternalSmoke.networkName=$NetworkName",
        "-PexternalSmoke.postgresContainerName=$PostgresContainerName",
        "-PexternalSmoke.appContainerName=$AppContainerName",
        "-PexternalSmoke.hostPort=$HostPort",
        "-PexternalSmoke.timeoutSeconds=$TimeoutSeconds",
        '--no-daemon'
    )
    & .\gradlew.bat @arguments | Out-Host
    if ($LASTEXITCODE -ne 0) {
        throw "externalSmokeTest failed."
    }
}
finally {
    Pop-Location
}
