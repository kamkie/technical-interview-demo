param(
    [Parameter(Mandatory = $true)]
    [string]$ImageReference,

    [Parameter(Mandatory = $true)]
    [string]$BaseUrl,

    [Parameter(Mandatory = $true)]
    [string]$JdbcUrl,

    [Parameter(Mandatory = $true)]
    [string]$JdbcUser,

    [Parameter(Mandatory = $true)]
    [string]$JdbcPassword,

    [string]$ExpectedBuildVersion,

    [string]$ExpectedShortCommitId,

    [string]$ExpectedActiveProfile = "prod",

    [string]$ExpectedSessionStoreType = "jdbc",

    [string]$ExpectedSessionTimeout = "15m",

    [string]$DatabaseHostForContainer,

    [string]$ContainerName = "technical-interview-demo-restore-drill",

    [int]$TimeoutSeconds = 120
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function Invoke-Docker {
    param(
        [Parameter(Mandatory = $true)]
        [string[]]$Arguments,

        [switch]$AllowFailure
    )

    $output = & docker @Arguments 2>&1
    $exitCode = $LASTEXITCODE
    if (-not $AllowFailure -and $exitCode -ne 0) {
        throw "docker $($Arguments -join ' ') failed.`n$($output -join [Environment]::NewLine)"
    }
    return [pscustomobject]@{
        ExitCode = $exitCode
        Output = @($output)
    }
}

function Parse-JdbcUrl {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Value
    )

    if ($Value -notmatch '^jdbc:postgresql://(?<host>[^/:?]+)(:(?<port>\d+))?/(?<database>[^?]+)(\?.*)?$') {
        throw "Only jdbc:postgresql://host[:port]/database URLs are supported. Received '$Value'."
    }

    return [pscustomobject]@{
        Host = $Matches.host
        Port = if ([string]::IsNullOrWhiteSpace($Matches.port)) { 5432 } else { [int]$Matches.port }
        Database = $Matches.database
    }
}

function Resolve-ContainerDatabaseHost {
    param(
        [Parameter(Mandatory = $true)]
        [string]$ConfiguredHost,

        [string]$Override
    )

    if (-not [string]::IsNullOrWhiteSpace($Override)) {
        return $Override.Trim()
    }

    switch ($ConfiguredHost.ToLowerInvariant()) {
        "localhost" { return "host.docker.internal" }
        "127.0.0.1" { return "host.docker.internal" }
        "::1" { return "host.docker.internal" }
        default { return $ConfiguredHost }
    }
}

function Get-ImageTag {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Reference
    )

    $withoutDigest = $Reference.Split("@")[0]
    $lastSlash = $withoutDigest.LastIndexOf("/")
    $lastColon = $withoutDigest.LastIndexOf(":")
    if ($lastColon -le $lastSlash) {
        return $null
    }
    return $withoutDigest.Substring($lastColon + 1)
}

function Wait-ForReadiness {
    param(
        [Parameter(Mandatory = $true)]
        [string]$TargetBaseUrl,

        [Parameter(Mandatory = $true)]
        [int]$TimeoutSeconds
    )

    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    $readinessUrl = "$($TargetBaseUrl.TrimEnd('/'))/actuator/health/readiness"

    while ((Get-Date) -lt $deadline) {
        try {
            $response = Invoke-RestMethod -Uri $readinessUrl -TimeoutSec 5
            if ($response.status -eq "UP") {
                return
            }
        } catch {
        }
        Start-Sleep -Seconds 2
    }

    throw "Timed out waiting for readiness at '$readinessUrl'."
}

$repoRoot = (& git rev-parse --show-toplevel 2>$null).Trim()
if ([string]::IsNullOrWhiteSpace($repoRoot)) {
    throw "Could not determine the repository root."
}

$baseUri = [Uri]$BaseUrl
if (-not $baseUri.IsAbsoluteUri) {
    throw "BaseUrl must be an absolute URL."
}
if ($baseUri.Port -lt 1) {
    throw "BaseUrl must include an explicit port so Docker can bind the restore-drill app container."
}

$jdbc = Parse-JdbcUrl -Value $JdbcUrl
$containerDatabaseHost = Resolve-ContainerDatabaseHost -ConfiguredHost $jdbc.Host -Override $DatabaseHostForContainer
$imageTag = Get-ImageTag -Reference $ImageReference

if ([string]::IsNullOrWhiteSpace($ExpectedBuildVersion) -and -not [string]::IsNullOrWhiteSpace($imageTag) -and $imageTag -notmatch '^sha-[0-9a-f]{12}$') {
    $ExpectedBuildVersion = $imageTag
}
if ([string]::IsNullOrWhiteSpace($ExpectedShortCommitId) -and -not [string]::IsNullOrWhiteSpace($imageTag) -and $imageTag -match '^sha-(?<commit>[0-9a-f]{12})$') {
    $ExpectedShortCommitId = $Matches.commit
}
if (([string]::IsNullOrWhiteSpace($ExpectedBuildVersion)) -ne ([string]::IsNullOrWhiteSpace($ExpectedShortCommitId))) {
    throw "ExpectedBuildVersion and ExpectedShortCommitId must be provided together when release identity assertions are enabled."
}

Push-Location $repoRoot
try {
    . (Join-Path $repoRoot "scripts/load-dotenv.ps1") -Path (Join-Path $repoRoot ".env") -Quiet

    Invoke-Docker -Arguments @("rm", "-f", $ContainerName) -AllowFailure | Out-Null

    Write-Host "Starting restore-drill container '$ContainerName' from '$ImageReference'."
    Invoke-Docker -Arguments @(
        "run",
        "--detach",
        "--name",
        $ContainerName,
        "--publish",
        "$($baseUri.Port):8080",
        "--env",
        "SPRING_PROFILES_ACTIVE=prod",
        "--env",
        "DATABASE_HOST=$containerDatabaseHost",
        "--env",
        "DATABASE_PORT=$($jdbc.Port)",
        "--env",
        "DATABASE_NAME=$($jdbc.Database)",
        "--env",
        "DATABASE_USER=$JdbcUser",
        "--env",
        "DATABASE_PASSWORD=$JdbcPassword",
        $ImageReference
    ) | Out-Null

    Write-Host "Waiting for readiness at '$BaseUrl'."
    Wait-ForReadiness -TargetBaseUrl $BaseUrl -TimeoutSeconds $TimeoutSeconds
    Write-Host "Runtime posture validation will confirm the published CSRF and edge abuse-protection metadata from GET /, then prove GET /api/session plus the CSRF-backed smoke write."

    $gradleCommand = Join-Path $repoRoot "gradlew.bat"
    $gradleArguments = @(
        "externalDeploymentCheck",
        "--no-daemon",
        "-PexternalCheck.baseUrl=$BaseUrl",
        "-PexternalCheck.jdbcUrl=$JdbcUrl",
        "-PexternalCheck.jdbcUser=$JdbcUser",
        "-PexternalCheck.jdbcPassword=$JdbcPassword",
        "-PexternalCheck.expectedActiveProfile=$ExpectedActiveProfile",
        "-PexternalCheck.expectedSessionStoreType=$ExpectedSessionStoreType",
        "-PexternalCheck.expectedSessionTimeout=$ExpectedSessionTimeout"
    )
    if (-not [string]::IsNullOrWhiteSpace($ExpectedBuildVersion)) {
        $gradleArguments += "-PexternalCheck.expectedBuildVersion=$ExpectedBuildVersion"
        $gradleArguments += "-PexternalCheck.expectedShortCommitId=$ExpectedShortCommitId"
    }

    Write-Host "Running externalDeploymentCheck against the restored database target."
    & $gradleCommand @gradleArguments
    if ($LASTEXITCODE -ne 0) {
        throw "externalDeploymentCheck failed during the restore drill."
    }

    Write-Host "Restore drill completed successfully with the documented session bootstrap and CSRF-backed write flow."
} catch {
    Write-Warning $_
    Write-Host "Container logs for '$ContainerName':"
    Invoke-Docker -Arguments @("logs", $ContainerName) -AllowFailure | ForEach-Object {
        if ($_.Output) {
            $_.Output | ForEach-Object { Write-Host $_ }
        }
    }
    throw
} finally {
    Invoke-Docker -Arguments @("rm", "-f", $ContainerName) -AllowFailure | Out-Null
    Pop-Location
}
