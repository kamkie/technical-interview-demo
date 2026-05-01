[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [string]$Tag,

    [Parameter(Mandatory = $true)]
    [string]$TagImageReference,

    [Parameter(Mandatory = $true)]
    [string]$ShaImageReference,

    [Parameter(Mandatory = $true)]
    [string]$PackagePageUrl,

    [string]$ChangelogPath = "CHANGELOG.md",

    [string]$OutputPath
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

if (-not (Test-Path -LiteralPath $ChangelogPath)) {
    throw "CHANGELOG.md was not found at '$ChangelogPath'."
}

$changelog = Get-Content -LiteralPath $ChangelogPath -Raw
$headingPattern = "(?m)^## \[$([Regex]::Escape($Tag))\] - .+$"
$headingMatches = [Regex]::Matches($changelog, $headingPattern)

if ($headingMatches.Count -eq 0) {
    throw "No CHANGELOG.md section matched tag '$Tag'."
}

if ($headingMatches.Count -gt 1) {
    throw "Multiple CHANGELOG.md sections matched tag '$Tag'."
}

$sectionStart = $headingMatches[0].Index
$remainingChangelog = $changelog.Substring($sectionStart)
$sectionHeadingMatches = [Regex]::Matches(
    $remainingChangelog,
    "(?m)^## \[",
    [Text.RegularExpressions.RegexOptions]::Multiline,
    [TimeSpan]::FromSeconds(5)
)

if ($sectionHeadingMatches.Count -gt 1) {
    $releaseSection = $remainingChangelog.Substring(0, $sectionHeadingMatches[1].Index)
} else {
    $releaseSection = $remainingChangelog
}

$releaseSection = $releaseSection.TrimEnd("`r", "`n")
$releaseNotes = @(
    $releaseSection
    ""
    "## Release Metadata"
    ""
    "- Container image: ``$TagImageReference``"
    "- Immutable image: ``$ShaImageReference``"
    "- Package page: [GitHub Container Registry package]($PackagePageUrl)"
) -join [Environment]::NewLine

if ($PSBoundParameters.ContainsKey("OutputPath")) {
    Set-Content -LiteralPath $OutputPath -Value $releaseNotes
    return
}

$releaseNotes
