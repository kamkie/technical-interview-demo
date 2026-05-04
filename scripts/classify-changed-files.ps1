param(
    [Parameter(ParameterSetName = "Comparison")]
    [AllowEmptyString()]
    [string]$BaseRef,

    [Parameter(ParameterSetName = "Comparison")]
    [string]$HeadRef = "HEAD",

    [Parameter(ParameterSetName = "Comparison")]
    [switch]$UseMergeBase,

    [Parameter(ParameterSetName = "Uncommitted", Mandatory = $true)]
    [switch]$Uncommitted
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$zeroSha = "0000000000000000000000000000000000000000"
$lightweightPatterns = @(
    "*.md",
    ".editorconfig",
    ".gitattributes",
    ".gitignore",
    "*/.gitignore",
    ".aiignore",
    "Default.xml",
    ".run/*.run.xml",
    ".githooks/*.sample",
    ".env.example",
    "*/.env.example"
)

function Invoke-Git {
    param(
        [Parameter(Mandatory = $true)]
        [string[]]$Arguments
    )

    $output = & git @Arguments 2>&1
    if ($LASTEXITCODE -ne 0) {
        throw "git $($Arguments -join ' ') failed.`n$($output -join [Environment]::NewLine)"
    }

    return ,@($output)
}

function Test-GitRevision {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Revision
    )

    & git rev-parse --verify $Revision *> $null
    return $LASTEXITCODE -eq 0
}

function Get-NormalizedPaths {
    param(
        [string[]]$Paths = @()
    )

    if ($null -eq $Paths -or $Paths.Count -eq 0) {
        return @()
    }

    return @(
        $Paths |
            Where-Object { -not [string]::IsNullOrWhiteSpace($_) } |
            ForEach-Object { $_.Trim().Replace('\', '/') } |
            Sort-Object -Unique
    )
}

function Test-IsLightweightChange {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path
    )

    $normalizedPath = $Path.Replace('\', '/')
    foreach ($pattern in $lightweightPatterns) {
        if ($normalizedPath -like $pattern) {
            return $true
        }
    }
    return $false
}

function Get-ComparisonChangeSet {
    param(
        [AllowEmptyString()]
        [string]$BaseRef,

        [Parameter(Mandatory = $true)]
        [string]$HeadRef,

        [bool]$UseMergeBase
    )

    if ([string]::IsNullOrWhiteSpace($HeadRef)) {
        throw "HeadRef must not be blank."
    }

    $resolvedHeadRef = $HeadRef.Trim()
    $resolvedBaseRef = $null
    $changedFiles = @()

    if ($UseMergeBase -and [string]::IsNullOrWhiteSpace($BaseRef)) {
        throw "BaseRef is required when UseMergeBase is set."
    }

    if ([string]::IsNullOrWhiteSpace($BaseRef) -or $BaseRef -eq $zeroSha) {
        if (Test-GitRevision -Revision "$resolvedHeadRef^") {
            $resolvedBaseRef = (Invoke-Git -Arguments @("rev-parse", "$resolvedHeadRef^"))[0].Trim()
            $changedFiles = Invoke-Git -Arguments @("diff", "--name-only", $resolvedBaseRef, $resolvedHeadRef)
        } else {
            $changedFiles = Invoke-Git -Arguments @("show", "--pretty=", "--name-only", $resolvedHeadRef)
        }
    } else {
        $resolvedBaseRef = $BaseRef.Trim()
        if ($UseMergeBase) {
            $changedFiles = Invoke-Git -Arguments @("diff", "--name-only", "$resolvedBaseRef...$resolvedHeadRef")
        } else {
            $changedFiles = Invoke-Git -Arguments @("diff", "--name-only", $resolvedBaseRef, $resolvedHeadRef)
        }
    }

    return [pscustomobject]@{
        baseRef = $resolvedBaseRef
        headRef = $resolvedHeadRef
        useMergeBase = $UseMergeBase
        changedFiles = @(Get-NormalizedPaths -Paths $changedFiles)
    }
}

function Get-UncommittedChangeSet {
    $unstagedFiles = Invoke-Git -Arguments @("diff", "--name-only")
    $stagedFiles = Invoke-Git -Arguments @("diff", "--cached", "--name-only")
    $untrackedFiles = Invoke-Git -Arguments @("ls-files", "--others", "--exclude-standard")

    return [pscustomobject]@{
        changedFiles = @(Get-NormalizedPaths -Paths ($unstagedFiles + $stagedFiles + $untrackedFiles))
    }
}

$changeSet = if ($Uncommitted) {
    Get-UncommittedChangeSet
} else {
    Get-ComparisonChangeSet -BaseRef $BaseRef -HeadRef $HeadRef -UseMergeBase:$UseMergeBase
}

$lightweightFiles = @($changeSet.changedFiles | Where-Object { Test-IsLightweightChange -Path $_ })
$nonLightweightFiles = @($changeSet.changedFiles | Where-Object { -not (Test-IsLightweightChange -Path $_) })
$skipHeavyValidation = $changeSet.changedFiles.Count -gt 0 -and $nonLightweightFiles.Count -eq 0

[pscustomobject]@{
    mode = if ($Uncommitted) { "uncommitted" } else { "comparison" }
    baseRef = if ($Uncommitted) { $null } else { $changeSet.baseRef }
    headRef = if ($Uncommitted) { $null } else { $changeSet.headRef }
    useMergeBase = if ($Uncommitted) { $false } else { [bool]$changeSet.useMergeBase }
    changedFiles = @($changeSet.changedFiles)
    lightweightFiles = @($lightweightFiles)
    nonLightweightFiles = @($nonLightweightFiles)
    skipHeavyValidation = $skipHeavyValidation
}
