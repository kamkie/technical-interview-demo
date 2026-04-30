[CmdletBinding()]
param(
    [string]$BaseUrl = "http://localhost:8080",
    [string]$BaselineOutput = "performance/baselines/phase-9-local.json",
    [string]$GithubClientId = "benchmark-client-id",
    [string]$GithubClientSecret = "benchmark-client-secret",
    [switch]$SkipBootJar
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$scriptDirectory = Split-Path -Parent $PSCommandPath
$repositoryRoot = Split-Path -Parent $scriptDirectory

Push-Location $repositoryRoot

function Get-LatestBootJar {
    $bootJar = Get-ChildItem -Path "build/libs/*-boot.jar" -File | Sort-Object LastWriteTime -Descending | Select-Object -First 1
    if ($null -eq $bootJar) {
        throw "No boot jar found under build/libs. Run .\gradlew.bat bootJar first."
    }
    return $bootJar.FullName
}

function Wait-ForReadiness {
    param(
        [Parameter(Mandatory = $true)]
        [string]$HealthUrl,
        [int]$TimeoutSeconds = 90
    )

    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    while ((Get-Date) -lt $deadline) {
        Start-Sleep -Seconds 2
        try {
            $response = Invoke-RestMethod -Uri $HealthUrl -TimeoutSec 5
            if ($response.status -eq "UP") {
                return
            }
        } catch {
        }
    }

    throw "Application did not become ready at $HealthUrl within $TimeoutSeconds seconds."
}

function Get-NewSimulationReportDirectory {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Prefix,
        [Parameter(Mandatory = $true)]
        [hashtable]$ExistingDirectories
    )

    $reportDirectory = Get-ChildItem -Path "build/reports/gatling" -Directory -Filter "$Prefix*" |
        Where-Object { -not $ExistingDirectories.ContainsKey($_.FullName) } |
        Sort-Object LastWriteTime -Descending |
        Select-Object -First 1

    if ($null -eq $reportDirectory) {
        throw "Could not locate a newly generated Gatling report directory for prefix '$Prefix'."
    }

    return $reportDirectory
}

function Get-RequestStatsFromIndex {
    param(
        [Parameter(Mandatory = $true)]
        [string]$IndexPath,
        [Parameter(Mandatory = $true)]
        [string]$RequestName
    )

    $content = Get-Content -Path $IndexPath -Raw
    $escapedRequestName = [regex]::Escape($RequestName)
    $rowPattern = "(?s)<tr id=""req_[^""]+"" data-parent=""ROOT"">.*?<span[^>]*class=""ellipsed-name"">$escapedRequestName</span>.*?</tr>"
    $rowMatch = [regex]::Match($content, $rowPattern)
    if (-not $rowMatch.Success) {
        throw "Could not parse request row '$RequestName' from $IndexPath."
    }

    $cellMatches = [regex]::Matches($rowMatch.Value, '<td class="value [^"]+ col-(?<column>\d+)">(?<value>[^<]+)</td>')
    $valuesByColumn = @{}
    foreach ($cellMatch in $cellMatches) {
        $valuesByColumn[$cellMatch.Groups["column"].Value] = $cellMatch.Groups["value"].Value
    }

    $requiredColumns = @("2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14")
    foreach ($requiredColumn in $requiredColumns) {
        if (-not $valuesByColumn.ContainsKey($requiredColumn)) {
            throw "Request row '$RequestName' in $IndexPath is missing column $requiredColumn."
        }
    }

    $total = [int]$valuesByColumn["2"]
    $ok = [int]$valuesByColumn["3"]
    $ko = [int]$valuesByColumn["4"]
    $successfulRequestPercentage = if ($total -eq 0) { 100.0 } else { [math]::Round(($ok * 100.0) / $total, 2) }

    return [ordered]@{
        requestName = $RequestName
        requestCount = $total
        okCount = $ok
        koCount = $ko
        koPercentage = [double]$valuesByColumn["5"]
        successfulRequestPercentage = $successfulRequestPercentage
        countPerSecond = [double]$valuesByColumn["6"]
        minMs = [int]$valuesByColumn["7"]
        p50Ms = [int]$valuesByColumn["8"]
        p75Ms = [int]$valuesByColumn["9"]
        p95Ms = [int]$valuesByColumn["10"]
        p99Ms = [int]$valuesByColumn["11"]
        maxMs = [int]$valuesByColumn["12"]
        meanMs = [int]$valuesByColumn["13"]
        stdDevMs = [int]$valuesByColumn["14"]
    }
}

function Invoke-GatlingSimulation {
    param(
        [Parameter(Mandatory = $true)]
        [string]$SimulationClass,
        [Parameter(Mandatory = $true)]
        [string]$ReportPrefix
    )

    $existingDirectories = @{}
    if (Test-Path "build/reports/gatling") {
        Get-ChildItem -Path "build/reports/gatling" -Directory | ForEach-Object {
            $existingDirectories[$_.FullName] = $true
        }
    }

    & .\gradlew.bat "-Dapp.baseUrl=$BaseUrl" gatlingRun --simulation $SimulationClass --non-interactive | Out-Host
    if ($LASTEXITCODE -ne 0) {
        throw "Gatling simulation failed: $SimulationClass"
    }

    return Get-NewSimulationReportDirectory -Prefix $ReportPrefix -ExistingDirectories $existingDirectories
}

try {
    $baseUri = [Uri]$BaseUrl
    $port = if ($baseUri.IsDefaultPort) {
        if ($baseUri.Scheme -eq "https") { 443 } else { 80 }
    } else {
        $baseUri.Port
    }

    $listeningConnection = Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($null -ne $listeningConnection) {
        throw "Port $port is already in use by PID $($listeningConnection.OwningProcess). Stop that process before running the benchmark script."
    }

    docker-compose up -d | Out-Host

    $env:JAVA_HOME = "C:\Users\kamki\.jdks\azul-25.0.3"
    $env:Path = "$env:JAVA_HOME\bin;$env:Path"
    $env:SPRING_PROFILES_ACTIVE = "local,oauth"
    $env:GITHUB_CLIENT_ID = $GithubClientId
    $env:GITHUB_CLIENT_SECRET = $GithubClientSecret

    if (-not $SkipBootJar) {
        & .\gradlew.bat bootJar
        if ($LASTEXITCODE -ne 0) {
            throw "bootJar failed."
        }
    }

    New-Item -ItemType Directory -Force -Path "build/performance" | Out-Null
    $bootJarPath = Get-LatestBootJar
    Write-Host "Starting application from $bootJarPath"
    $appProcess = Start-Process -FilePath "java" `
        -ArgumentList @("-jar", $bootJarPath) `
        -WorkingDirectory $repositoryRoot `
        -RedirectStandardOutput "build/performance/app.out.log" `
        -RedirectStandardError "build/performance/app.err.log" `
        -PassThru

    try {
        Wait-ForReadiness -HealthUrl "$BaseUrl/actuator/health/readiness"

        $publicReportDirectory = Invoke-GatlingSimulation `
            -SimulationClass "team.jit.technicalinterviewdemo.performance.PublicApiSimulation" `
            -ReportPrefix "publicapisimulation-"
        $authRedirectReportDirectory = Invoke-GatlingSimulation `
            -SimulationClass "team.jit.technicalinterviewdemo.performance.AuthenticationRedirectSimulation" `
            -ReportPrefix "authenticationredirectsimulation-"

        $baseline = [ordered]@{
            capturedAt = (Get-Date).ToString("o")
            gitCommit = (git rev-parse HEAD).Trim()
            baseUrl = $BaseUrl
            javaVersion = ((& java -version 2>&1) | Select-Object -First 1).ToString().Trim()
            activeProfiles = @("local", "oauth")
            simulations = @(
                [ordered]@{
                    simulation = "PublicApiSimulation"
                    description = "List books, search books, and localization lookup against the public API."
                    injectionProfile = "5 users at once, ramp 1->6 users/s for 20s, then hold 6 users/s for 20s"
                },
                [ordered]@{
                    simulation = "AuthenticationRedirectSimulation"
                    description = "Start the GitHub OAuth redirect flow without following the redirect."
                    injectionProfile = "3 users at once, ramp 1->4 users/s for 15s, then hold 4 users/s for 15s"
                }
            )
            requests = @(
                (Get-RequestStatsFromIndex -IndexPath (Join-Path $publicReportDirectory.FullName "index.html") -RequestName "list-books"),
                (Get-RequestStatsFromIndex -IndexPath (Join-Path $publicReportDirectory.FullName "index.html") -RequestName "search-books"),
                (Get-RequestStatsFromIndex -IndexPath (Join-Path $publicReportDirectory.FullName "index.html") -RequestName "lookup-localization-message"),
                (Get-RequestStatsFromIndex -IndexPath (Join-Path $authRedirectReportDirectory.FullName "index.html") -RequestName "oauth2-github-redirect")
            )
            notes = @(
                "This baseline is captured against a local PostgreSQL instance started with docker-compose.",
                "The OAuth benchmark uses dummy GitHub credentials because the redirect endpoint only needs a configured client registration.",
                "AuthenticatedUserProfileSimulation is intentionally excluded from the automated baseline because it requires a real technical-interview-demo-session cookie from an interactive login."
            )
        }

        $baselineOutputPath = Join-Path $repositoryRoot $BaselineOutput
        $baselineDirectory = Split-Path -Parent $baselineOutputPath
        New-Item -ItemType Directory -Force -Path $baselineDirectory | Out-Null
        $baseline | ConvertTo-Json -Depth 6 | Set-Content -Path $baselineOutputPath

        Write-Host "Baseline written to $baselineOutputPath"
    } finally {
        if ($null -ne $appProcess -and -not $appProcess.HasExited) {
            Stop-Process -Id $appProcess.Id -Force
        }
    }
} finally {
    Pop-Location
}
