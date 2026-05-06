param(
    [Parameter(Mandatory = $true)]
    [string]$ChangelogPath,

    [Parameter(Mandatory = $true)]
    [string]$CurrentTag,

    [Parameter(Mandatory = $true)]
    [string]$PreviousPublishedTag,

    [Parameter(Mandatory = $true)]
    [string]$TagImageReference,

    [Parameter(Mandatory = $true)]
    [string]$ShaImageReference,

    [Parameter(Mandatory = $true)]
    [string]$PackagePageUrl,

    [Parameter(Mandatory = $true)]
    [string]$OutputPath
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

if ($CurrentTag -eq $PreviousPublishedTag)
{
    throw "Current tag '$CurrentTag' matches previous published release tag '$PreviousPublishedTag'."
}

if (-not (Test-Path -LiteralPath $ChangelogPath))
{
    throw "CHANGELOG file '$ChangelogPath' was not found."
}

$changelog = Get-Content -LiteralPath $ChangelogPath -Raw
$sectionHeadingMatches = [Regex]::Matches($changelog, "(?m)^## \[(?<tag>[^\]]+)\] - .+$")

if ($sectionHeadingMatches.Count -eq 0)
{
    throw "No version sections were found in '$ChangelogPath'."
}

function Get-UniqueSectionHeading
{
    param(
        [Parameter(Mandatory = $true)]
        [string]$Tag,

        [Parameter(Mandatory = $true)]
        [System.Text.RegularExpressions.MatchCollection]$HeadingMatches
    )

    $tagMatches = @($HeadingMatches | Where-Object { $_.Groups["tag"].Value -eq $Tag })

    if ($tagMatches.Count -eq 0)
    {
        throw "No CHANGELOG.md section matched tag '$Tag'."
    }

    if ($tagMatches.Count -gt 1)
    {
        throw "Multiple CHANGELOG.md sections matched tag '$Tag'."
    }

    return $tagMatches[0]
}

$currentHeading = Get-UniqueSectionHeading -Tag $CurrentTag -HeadingMatches $sectionHeadingMatches
$previousHeading = Get-UniqueSectionHeading -Tag $PreviousPublishedTag -HeadingMatches $sectionHeadingMatches

if ($currentHeading.Index -ge $previousHeading.Index)
{
    throw "Could not derive cumulative release range from '$PreviousPublishedTag' to '$CurrentTag' from CHANGELOG.md ordering."
}

$releaseSection = $changelog.Substring($currentHeading.Index, $previousHeading.Index - $currentHeading.Index).TrimEnd("`r", "`n")

if ( [string]::IsNullOrWhiteSpace($releaseSection))
{
    throw "Derived cumulative release section for '$CurrentTag' is empty."
}

$releaseNotes = @(
    $releaseSection
    ""
    "## Release Metadata"
    ""
    "- Container image: ``$TagImageReference``"
    "- Immutable image: ``$ShaImageReference``"
    "- Package page: [GitHub Container Registry package]($PackagePageUrl)"
) -join [Environment]::NewLine

$outputDirectory = Split-Path -Path $OutputPath -Parent
if (-not [string]::IsNullOrWhiteSpace($outputDirectory))
{
    New-Item -ItemType Directory -Path $outputDirectory -Force | Out-Null
}

Set-Content -LiteralPath $OutputPath -Value $releaseNotes
