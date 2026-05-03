param(
    [Parameter(Mandatory = $true)]
    [string]$PreviousReleaseTag,

    [string]$CurrentRef = "HEAD",

    [string]$OutputJsonPath
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$metadataDirectory = "src/main/resources/db/migration/metadata"
$migrationDirectory = "src/main/resources/db/migration"
$allowedRolloutCategories = @("expand", "contract", "backfill", "breaking")
$allowedDeploymentOrders = @("db-first", "app-first", "out-of-band")
$allowedRollbackPostures = @("image-only", "forward-fix-or-restore")

function Invoke-Git {
    param(
        [Parameter(Mandatory = $true)]
        [string[]]$Arguments
    )

    $output = & git @Arguments 2>&1
    if ($LASTEXITCODE -ne 0) {
        throw "git $($Arguments -join ' ') failed.`n$($output -join [Environment]::NewLine)"
    }
    return @($output)
}

function Get-RequiredMetadataValue {
    param(
        [Parameter(Mandatory = $true)]
        [hashtable]$Metadata,

        [Parameter(Mandatory = $true)]
        [string]$Key,

        [Parameter(Mandatory = $true)]
        [string]$MigrationPath
    )

    if (-not $Metadata.ContainsKey($Key)) {
        throw "Missing required metadata key '$Key' for migration '$MigrationPath'."
    }
    $value = $Metadata[$Key]
    if ($value -is [string]) {
        $value = $value.Trim()
    }
    if ($null -eq $value -or ($value -is [string] -and [string]::IsNullOrWhiteSpace($value))) {
        throw "Metadata key '$Key' for migration '$MigrationPath' must not be blank."
    }
    return $value
}

function Assert-AllowedValue {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Key,

        [Parameter(Mandatory = $true)]
        [string]$Value,

        [Parameter(Mandatory = $true)]
        [string[]]$AllowedValues,

        [Parameter(Mandatory = $true)]
        [string]$MigrationPath
    )

    if ($Value -notin $AllowedValues) {
        throw "Metadata key '$Key' for migration '$MigrationPath' must be one of: $($AllowedValues -join ', ')."
    }
}

$null = Invoke-Git -Arguments @("rev-parse", "--verify", "$PreviousReleaseTag^{commit}")
$null = Invoke-Git -Arguments @("rev-parse", "--verify", "$CurrentRef^{commit}")

$changedMigrationPaths = @(
    Invoke-Git -Arguments @(
        "diff",
        "--name-only",
        "--diff-filter=AM",
        "$PreviousReleaseTag..$CurrentRef",
        "--",
        "$migrationDirectory/*.sql"
    ) | Where-Object { -not [string]::IsNullOrWhiteSpace($_) } | Sort-Object -Unique
)

if ($changedMigrationPaths.Count -eq 0) {
    $noneResult = [ordered]@{
        previousReleaseTag = $PreviousReleaseTag
        currentRef = $CurrentRef
        impact = "none"
        migrations = @()
    }
    if (-not [string]::IsNullOrWhiteSpace($OutputJsonPath)) {
        $outputDirectory = Split-Path -Path $OutputJsonPath -Parent
        if (-not [string]::IsNullOrWhiteSpace($outputDirectory)) {
            New-Item -ItemType Directory -Path $outputDirectory -Force | Out-Null
        }
        $noneResult | ConvertTo-Json -Depth 5 | Set-Content -LiteralPath $OutputJsonPath
    }
    Write-Host "No migration SQL files changed between '$PreviousReleaseTag' and '$CurrentRef'."
    Write-Output "none"
    return
}

$migrationSummaries = foreach ($migrationPath in $changedMigrationPaths) {
    $migrationFileName = [System.IO.Path]::GetFileNameWithoutExtension($migrationPath)
    $metadataPath = "$metadataDirectory/$migrationFileName.json"
    $metadataJson = Invoke-Git -Arguments @("show", "${CurrentRef}:$metadataPath") | Out-String

    if ([string]::IsNullOrWhiteSpace($metadataJson)) {
        throw "Missing metadata sidecar '$metadataPath' for changed migration '$migrationPath'."
    }

    try {
        $metadata = $metadataJson | ConvertFrom-Json -AsHashtable
    } catch {
        throw "Failed to parse JSON metadata from '$metadataPath' for migration '$migrationPath'. $_"
    }

    $summary = [string](Get-RequiredMetadataValue -Metadata $metadata -Key "summary" -MigrationPath $migrationPath)
    $rolloutCategory = [string](Get-RequiredMetadataValue -Metadata $metadata -Key "rolloutCategory" -MigrationPath $migrationPath)
    $deploymentOrder = [string](Get-RequiredMetadataValue -Metadata $metadata -Key "deploymentOrder" -MigrationPath $migrationPath)
    $rollingCompatible = Get-RequiredMetadataValue -Metadata $metadata -Key "rollingCompatible" -MigrationPath $migrationPath
    $rollbackPosture = [string](Get-RequiredMetadataValue -Metadata $metadata -Key "rollbackPosture" -MigrationPath $migrationPath)

    Assert-AllowedValue -Key "rolloutCategory" -Value $rolloutCategory -AllowedValues $allowedRolloutCategories -MigrationPath $migrationPath
    Assert-AllowedValue -Key "deploymentOrder" -Value $deploymentOrder -AllowedValues $allowedDeploymentOrders -MigrationPath $migrationPath
    Assert-AllowedValue -Key "rollbackPosture" -Value $rollbackPosture -AllowedValues $allowedRollbackPostures -MigrationPath $migrationPath

    if ($rollingCompatible -isnot [bool]) {
        throw "Metadata key 'rollingCompatible' for migration '$migrationPath' must be true or false."
    }

    [pscustomobject]@{
        migrationPath = $migrationPath
        metadataPath = $metadataPath
        summary = $summary
        rolloutCategory = $rolloutCategory
        deploymentOrder = $deploymentOrder
        rollingCompatible = [bool]$rollingCompatible
        rollbackPosture = $rollbackPosture
    }
}

$impact = if ($migrationSummaries | Where-Object {
        -not $_.rollingCompatible -or $_.rollbackPosture -ne "image-only"
    }) {
    "restore-sensitive"
} else {
    "rolling-compatible"
}

$result = [ordered]@{
    previousReleaseTag = $PreviousReleaseTag
    currentRef = $CurrentRef
    impact = $impact
    migrations = @($migrationSummaries)
}

if (-not [string]::IsNullOrWhiteSpace($OutputJsonPath)) {
    $outputDirectory = Split-Path -Path $OutputJsonPath -Parent
    if (-not [string]::IsNullOrWhiteSpace($outputDirectory)) {
        New-Item -ItemType Directory -Path $outputDirectory -Force | Out-Null
    }
    $result | ConvertTo-Json -Depth 5 | Set-Content -LiteralPath $OutputJsonPath
}

Write-Host "Migration impact between '$PreviousReleaseTag' and '$CurrentRef': $impact"
foreach ($migration in $migrationSummaries) {
    Write-Host "- $($migration.migrationPath)"
    Write-Host "  summary: $($migration.summary)"
    Write-Host "  rolloutCategory: $($migration.rolloutCategory)"
    Write-Host "  deploymentOrder: $($migration.deploymentOrder)"
    Write-Host "  rollingCompatible: $($migration.rollingCompatible)"
    Write-Host "  rollbackPosture: $($migration.rollbackPosture)"
}

Write-Output $impact
