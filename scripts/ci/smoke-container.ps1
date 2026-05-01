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

$ErrorActionPreference = "Stop"
$ProgressPreference = "SilentlyContinue"

function Remove-DockerResource {
    param(
        [Parameter(Mandatory = $true)]
        [string[]]$Command
    )

    & docker @Command *> $null
}

function Wait-ForCommand {
    param(
        [Parameter(Mandatory = $true)]
        [scriptblock]$Condition,
        [Parameter(Mandatory = $true)]
        [string]$FailureMessage
    )

    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    while ((Get-Date) -lt $deadline) {
        if (& $Condition) {
            return
        }
        Start-Sleep -Seconds 2
    }

    throw $FailureMessage
}

function Write-ContainerLogs {
    param(
        [Parameter(Mandatory = $true)]
        [string]$ContainerName
    )

    if ((& docker ps -a --format "{{.Names}}" | Select-String -SimpleMatch $ContainerName)) {
        Write-Host "Logs for ${ContainerName}:"
        & docker logs $ContainerName
    }
}

Remove-DockerResource -Command @("rm", "-f", $AppContainerName)
Remove-DockerResource -Command @("rm", "-f", $PostgresContainerName)
Remove-DockerResource -Command @("network", "rm", $NetworkName)

try {
    & docker network create $NetworkName | Out-Null

    $postgresRunOutput = & docker run `
        --detach `
        --name $PostgresContainerName `
        --network $NetworkName `
        --env "POSTGRES_DB=$DatabaseName" `
        --env "POSTGRES_USER=$DatabaseUser" `
        --env "POSTGRES_PASSWORD=$DatabasePassword" `
        $PostgresImage 2>&1
    if ($LASTEXITCODE -ne 0) {
        throw "PostgreSQL container failed to start: $($postgresRunOutput -join [Environment]::NewLine)"
    }

    Wait-ForCommand `
        -Condition {
            $null = & docker exec $PostgresContainerName pg_isready -U $DatabaseUser -d $DatabaseName
            $LASTEXITCODE -eq 0
        } `
        -FailureMessage "PostgreSQL did not become ready within ${TimeoutSeconds}s."

    $appRunOutput = & docker run `
        --detach `
        --name $AppContainerName `
        --network $NetworkName `
        --publish "${HostPort}:8080" `
        --env "SPRING_PROFILES_ACTIVE=prod" `
        --env "DATABASE_HOST=$PostgresContainerName" `
        --env "DATABASE_PORT=5432" `
        --env "DATABASE_NAME=$DatabaseName" `
        --env "DATABASE_USER=$DatabaseUser" `
        --env "DATABASE_PASSWORD=$DatabasePassword" `
        --env "SESSION_COOKIE_SECURE=false" `
        $ImageName 2>&1
    if ($LASTEXITCODE -ne 0) {
        throw "Application container failed to start: $($appRunOutput -join [Environment]::NewLine)"
    }

    $publishedPort = ((& docker port $AppContainerName 8080) -join [Environment]::NewLine).Trim()
    if ([string]::IsNullOrWhiteSpace($publishedPort)) {
        throw "Application container did not publish port 8080."
    }

    Wait-ForCommand `
        -Condition {
            try {
                $response = Invoke-WebRequest -Uri "http://127.0.0.1:${HostPort}/actuator/health/readiness" -TimeoutSec 5
                if ($response.StatusCode -ne 200) {
                    return $false
                }

                $responseContent = if ($response.Content -is [byte[]]) {
                    [System.Text.Encoding]::UTF8.GetString($response.Content)
                } else {
                    [string]$response.Content
                }
                $payload = $responseContent | ConvertFrom-Json
                return $payload.status -eq "UP"
            } catch {
                return $false
            }
        } `
        -FailureMessage "Application readiness probe did not return HTTP 200 with status UP within ${TimeoutSeconds}s."

    $flywayTableCount = (
        & docker exec $PostgresContainerName psql -U $DatabaseUser -d $DatabaseName -tAc `
            "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'flyway_schema_history';"
    ).Trim()
    if ($flywayTableCount -ne "1") {
        throw "Flyway schema history table was not created in PostgreSQL."
    }

    $flywaySuccessCount = (
        & docker exec $PostgresContainerName psql -U $DatabaseUser -d $DatabaseName -tAc `
            "SELECT COUNT(*) FROM flyway_schema_history WHERE success = true;"
    ).Trim()
    if ([int]$flywaySuccessCount -lt 1) {
        throw "Flyway did not record any successful migrations."
    }

    Write-Host "Container smoke validation passed for image '$ImageName'."
} catch {
    Write-ContainerLogs -ContainerName $PostgresContainerName
    Write-ContainerLogs -ContainerName $AppContainerName
    throw
} finally {
    Remove-DockerResource -Command @("rm", "-f", $AppContainerName)
    Remove-DockerResource -Command @("rm", "-f", $PostgresContainerName)
    Remove-DockerResource -Command @("network", "rm", $NetworkName)
}
