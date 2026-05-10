param(
    [switch]$Json
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$operationMethods = @("get", "post", "put", "delete", "patch", "head", "options", "trace")
$failures = [System.Collections.Generic.List[object]]::new()
$localLinksChecked = 0

function Add-Failure
{
    param(
        [Parameter(Mandatory = $true)]
        [string]$Category,

        [Parameter(Mandatory = $true)]
        [string]$File,

        [Parameter(Mandatory = $true)]
        [string]$Message
    )

    $failures.Add([pscustomobject]@{
            Category = $Category
            File = $File
            Message = $Message
        })
}

function Invoke-Git
{
    param(
        [Parameter(Mandatory = $true)]
        [string[]]$Arguments
    )

    $stderrPath = [System.IO.Path]::GetTempFileName()
    try
    {
        $output = & git @Arguments 2> $stderrPath
        if ($LASTEXITCODE -ne 0)
        {
            $errorOutput = Get-Content -LiteralPath $stderrPath -Raw
            throw "git $( $Arguments -join ' ' ) failed.`n$($errorOutput.Trim())"
        }

        return @($output)
    }
    finally
    {
        if (Test-Path -LiteralPath $stderrPath)
        {
            Remove-Item -LiteralPath $stderrPath -Force
        }
    }
}

function Get-Text
{
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path
    )

    return Get-Content -LiteralPath $Path -Raw -Encoding UTF8
}

function Test-UserFacingDocPath
{
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path
    )

    $normalizedPath = $Path.Replace('\', '/')
    $topLevelDocs = @(
        "README.md",
        "SETUP.md",
        "CONTRIBUTING.md",
        "WORKING_WITH_AI.md",
        "ROADMAP.md",
        "CHANGELOG.md"
    )

    return $topLevelDocs.Contains($normalizedPath) `
        -or $normalizedPath -match '^docs/.+\.md$' `
        -or $normalizedPath -match '^src/docs/asciidoc/[^/]+\.adoc$' `
        -or $normalizedPath -eq "src/manualTests/resources/README.md" `
        -or $normalizedPath -eq "src/manualTests/http/suites/README.md" `
        -or $normalizedPath -eq "infra/k8s/edge/README.md" `
        -or $normalizedPath -like ".devcontainer/*.md"
}

function Test-ExternalLink
{
    param(
        [Parameter(Mandatory = $true)]
        [string]$Target
    )

    return $Target -match '^[a-zA-Z][a-zA-Z0-9+.-]*:' -or $Target.StartsWith("mailto:")
}

function Resolve-DocTarget
{
    param(
        [Parameter(Mandatory = $true)]
        [string]$SourcePath,

        [Parameter(Mandatory = $true)]
        [string]$Target
    )

    $trimmedTarget = $Target.Trim().Trim("<", ">")
    if ([string]::IsNullOrWhiteSpace($trimmedTarget) -or $trimmedTarget.StartsWith("#"))
    {
        return $null
    }
    if (Test-ExternalLink -Target $trimmedTarget)
    {
        return $null
    }

    $pathOnly = ($trimmedTarget -split '#', 2)[0].Trim()
    if ([string]::IsNullOrWhiteSpace($pathOnly))
    {
        return $null
    }

    $sourceDirectory = Split-Path -Parent $SourcePath
    return Join-Path $sourceDirectory $pathOnly
}

function Test-MarkdownLinks
{
    param(
        [Parameter(Mandatory = $true)]
        [string]$RelativePath
    )

    $absolutePath = Join-Path $repoRoot $RelativePath
    $content = Get-Text -Path $absolutePath
    $matches = [regex]::Matches($content, '(?<!\!)\[[^\]]+\]\(([^)]+)\)')
    foreach ($match in $matches)
    {
        $target = $match.Groups[1].Value
        $resolvedTarget = Resolve-DocTarget -SourcePath $absolutePath -Target $target
        if ($null -eq $resolvedTarget)
        {
            continue
        }

        $script:localLinksChecked++
        if (-not (Test-Path -LiteralPath $resolvedTarget))
        {
            Add-Failure -Category "link" -File $RelativePath -Message "Broken Markdown link target: $target"
        }
    }
}

function Test-AsciiDocXrefs
{
    param(
        [Parameter(Mandatory = $true)]
        [string]$RelativePath
    )

    $absolutePath = Join-Path $repoRoot $RelativePath
    $content = Get-Text -Path $absolutePath
    $matches = [regex]::Matches($content, 'xref:([^\[\s]+)\[')
    foreach ($match in $matches)
    {
        $target = $match.Groups[1].Value
        $resolvedTarget = Resolve-DocTarget -SourcePath $absolutePath -Target $target
        if ($null -eq $resolvedTarget)
        {
            continue
        }

        $script:localLinksChecked++
        if (-not (Test-Path -LiteralPath $resolvedTarget))
        {
            Add-Failure -Category "link" -File $RelativePath -Message "Broken AsciiDoc xref target: $target"
        }
    }
}

function Test-StaleDocSignals
{
    param(
        [Parameter(Mandatory = $true)]
        [string[]]$TrackedFiles,

        [Parameter(Mandatory = $true)]
        [string[]]$UserDocs
    )

    $todoExists = Test-Path -LiteralPath (Join-Path $repoRoot "TODO.md")
    foreach ($relativePath in $UserDocs)
    {
        $content = Get-Text -Path (Join-Path $repoRoot $relativePath)
        if (-not $todoExists -and $content.Contains("TODO.md"))
        {
            Add-Failure -Category "stale-reference" -File $relativePath -Message "References TODO.md, but TODO.md is not tracked."
        }

        if ($relativePath -like ".devcontainer/*")
        {
            $stalePatterns = @(
                "Implementation Complete",
                "COMPLETION_REPORT",
                "Happy coding",
                "production-ready development container",
                "auto-starts"
            )
            foreach ($pattern in $stalePatterns)
            {
                if ($content.Contains($pattern))
                {
                    Add-Failure `
                        -Category "stale-reference" `
                        -File $relativePath `
                        -Message "Contains generated or over-promising dev-container wording: $pattern"
                }
            }
        }
    }

    $devContainerFiles = @($TrackedFiles | Where-Object {
            $_ -like ".devcontainer/*.md" -or $_ -like ".devcontainer/*.sh"
        })
    foreach ($relativePath in $devContainerFiles)
    {
        $content = Get-Text -Path (Join-Path $repoRoot $relativePath)
        if ($content.Contains("./gradlew.bat"))
        {
            Add-Failure -Category "command" -File $relativePath -Message "Use ./gradlew inside the Linux dev container, not ./gradlew.bat."
        }
    }
}

function Test-VersionAgreement
{
    $stableTags = @(Invoke-Git -Arguments @("tag", "--list") |
        Where-Object { $_ -match '^v\d+\.\d+\.\d+$' } |
        Sort-Object { [version]($_.Substring(1)) } -Descending)

    if ($stableTags.Count -eq 0)
    {
        Add-Failure -Category "version" -File "git tags" -Message "No stable semantic version tags found."
        return
    }

    $latestStable = $stableTags[0]
    $roadmap = Get-Text -Path (Join-Path $repoRoot "ROADMAP.md")
    $changelog = Get-Text -Path (Join-Path $repoRoot "CHANGELOG.md")

    $expectedRoadmapPhase = "Stable ``$latestStable``"
    $expectedLatestRelease = "Latest Stable Release | ``$latestStable``"
    $expectedChangelogHeading = "## [$latestStable]"

    if (-not $roadmap.Contains($expectedRoadmapPhase))
    {
        Add-Failure -Category "version" -File "ROADMAP.md" -Message "Current release phase does not name $latestStable."
    }
    if (-not $roadmap.Contains($expectedLatestRelease))
    {
        Add-Failure -Category "version" -File "ROADMAP.md" -Message "Latest stable release row does not name $latestStable."
    }
    if (-not $changelog.Contains($expectedChangelogHeading))
    {
        Add-Failure -Category "version" -File "CHANGELOG.md" -Message "Missing changelog heading for latest stable tag $latestStable."
    }
}

function Test-OpenApiSummaryAgreement
{
    $openApiPath = Join-Path $repoRoot "src/test/resources/openapi/approved-openapi.json"
    $frontendContractPath = Join-Path $repoRoot "docs/FRONTEND_AI_CONTRACT.md"
    if (-not (Test-Path -LiteralPath $openApiPath) -or -not (Test-Path -LiteralPath $frontendContractPath))
    {
        return
    }

    $openApi = Get-Text -Path $openApiPath | ConvertFrom-Json
    $pathProperties = @($openApi.paths.PSObject.Properties)
    $schemaProperties = @($openApi.components.schemas.PSObject.Properties)
    $pathCount = $pathProperties.Count
    $operationCount = 0
    foreach ($path in $pathProperties)
    {
        foreach ($operation in $path.Value.PSObject.Properties)
        {
            if ($operationMethods.Contains($operation.Name))
            {
                $operationCount++
            }
        }
    }
    $schemaCount = $schemaProperties.Count
    $frontendContract = Get-Text -Path $frontendContractPath

    $expectedLines = @(
        @{ Label = "Path templates"; Count = $pathCount },
        @{ Label = "Operations"; Count = $operationCount },
        @{ Label = "Component schemas"; Count = $schemaCount }
    )

    foreach ($expectedLine in $expectedLines)
    {
        $expectedText = '{0}: `{1}`' -f $expectedLine.Label, $expectedLine.Count
        if (-not $frontendContract.Contains($expectedText))
        {
            Add-Failure `
                -Category "contract-summary" `
                -File "docs/FRONTEND_AI_CONTRACT.md" `
                -Message "Expected approved OpenAPI summary line '$expectedText'."
        }
    }
}

function Test-LanguageSummaryAgreement
{
    $seedDataPath = Join-Path $repoRoot "src/main/java/team/jit/technicalinterviewdemo/business/localization/seed/LocalizationSeedData.java"
    if (-not (Test-Path -LiteralPath $seedDataPath))
    {
        return
    }

    $seedData = Get-Text -Path $seedDataPath
    $languageMatch = [regex]::Match($seedData, 'SUPPORTED_LANGUAGES\s*=\s*List\.of\(([^)]+)\)')
    if (-not $languageMatch.Success)
    {
        return
    }

    $languages = @([regex]::Matches($languageMatch.Groups[1].Value, '"([^"]+)"') |
        ForEach-Object { $_.Groups[1].Value })
    $docsWithLanguageSummary = @(
        "src/docs/asciidoc/index.adoc",
        "src/docs/asciidoc/book-controller.adoc",
        "src/docs/asciidoc/localization-controller.adoc",
        "docs/FRONTEND_AI_CONTRACT.md"
    )

    foreach ($relativePath in $docsWithLanguageSummary)
    {
        $absolutePath = Join-Path $repoRoot $relativePath
        if (-not (Test-Path -LiteralPath $absolutePath))
        {
            continue
        }

        $content = Get-Text -Path $absolutePath
        foreach ($language in $languages)
        {
            if (-not $content.Contains("``$language``"))
            {
                Add-Failure -Category "language-summary" -File $relativePath -Message "Missing supported language `$language`."
            }
        }
    }
}

$repoRoot = @(Invoke-Git -Arguments @("rev-parse", "--show-toplevel"))[0].Trim()
Set-Location -LiteralPath $repoRoot

$trackedFiles = @(Invoke-Git -Arguments @("ls-files") |
    ForEach-Object { $_.Replace('\', '/') } |
    Where-Object { Test-Path -LiteralPath (Join-Path $repoRoot $_) })
$userDocs = @($trackedFiles | Where-Object { Test-UserFacingDocPath -Path $_ } | Sort-Object)

foreach ($relativePath in $userDocs)
{
    if ($relativePath.EndsWith(".md"))
    {
        Test-MarkdownLinks -RelativePath $relativePath
    }
    elseif ($relativePath.EndsWith(".adoc"))
    {
        Test-AsciiDocXrefs -RelativePath $relativePath
    }
}

Test-StaleDocSignals -TrackedFiles $trackedFiles -UserDocs $userDocs
Test-VersionAgreement
Test-OpenApiSummaryAgreement
Test-LanguageSummaryAgreement

$summary = [pscustomobject]@{
    DocumentsAudited = $userDocs.Count
    LocalLinksChecked = $localLinksChecked
    Failures = @($failures)
}

if ($Json)
{
    $summary | ConvertTo-Json -Depth 6
}
else
{
    Write-Host "User-facing documents audited: $($summary.DocumentsAudited)"
    Write-Host "Local document links checked: $($summary.LocalLinksChecked)"
    if ($failures.Count -gt 0)
    {
        Write-Host ""
        Write-Host "Documentation health failures:"
        $failures | Format-Table -AutoSize
    }
    else
    {
        Write-Host "Documentation health check passed."
    }
}

if ($failures.Count -gt 0)
{
    exit 1
}
