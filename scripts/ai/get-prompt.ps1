param(
    [string]$Name,
    [switch]$List,
    [switch]$Json
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$repoRoot = Resolve-Path -LiteralPath (Join-Path $PSScriptRoot "../..")
$indexPath = Join-Path $repoRoot "ai/prompts/index.json"

if (-not (Test-Path -LiteralPath $indexPath)) {
    throw "Prompt index '$indexPath' was not found."
}

$index = Get-Content -Raw -LiteralPath $indexPath | ConvertFrom-Json
$prompts = @($index.prompts)

function Normalize-PromptName {
    param([Parameter(Mandatory = $true)][string]$Value)

    return ($Value.Trim().ToLowerInvariant() -replace "[^a-z0-9]+", "-").Trim("-")
}

if ($List) {
    if ($Json) {
        [pscustomobject]@{
            schemaVersion = $index.schemaVersion
            bodyFormat = $index.bodyFormat
            prompts = @(
                $prompts |
                    Sort-Object category, title |
                    ForEach-Object {
                        [pscustomobject]@{
                            category = $_.category
                            title = $_.title
                            slug = $_.slug
                            path = $_.path
                            placeholders = @($_.placeholders)
                        }
                    }
            )
        } | ConvertTo-Json -Depth 10
    } else {
        $rows = $prompts |
            Sort-Object category, title |
            Select-Object category, title, slug, path, @{Name = "placeholders"; Expression = { ($_.placeholders -join ", ") } }

        $rows | Format-Table -AutoSize
    }
    exit 0
}

if ([string]::IsNullOrWhiteSpace($Name)) {
    throw "Provide -Name '<prompt title>' or use -List."
}

$normalizedName = Normalize-PromptName -Value $Name
$exactMatches = @(
    $prompts | Where-Object {
        $_.title.Equals($Name, [System.StringComparison]::OrdinalIgnoreCase) -or
        $_.slug.Equals($normalizedName, [System.StringComparison]::OrdinalIgnoreCase)
    }
)

if ($exactMatches.Count -eq 1) {
    $selected = $exactMatches[0]
} else {
    $term = [Regex]::Escape($Name.Trim())
    $slugTerm = [Regex]::Escape($normalizedName)
    $candidates = @(
        $prompts | Where-Object {
            $_.title -match $term -or $_.slug -match $slugTerm
        }
    )

    if ($candidates.Count -eq 0) {
        $available = ($prompts | Sort-Object title | Select-Object -ExpandProperty title) -join "', '"
        throw "No prompt matched '$Name'. Available prompts: '$available'."
    }

    if ($candidates.Count -gt 1) {
        $candidateTitles = ($candidates | Sort-Object title | Select-Object -ExpandProperty title) -join "', '"
        throw "Prompt name '$Name' is ambiguous. Matches: '$candidateTitles'."
    }

    $selected = $candidates[0]
}

$bodyPath = Join-Path $repoRoot $selected.path
if (-not (Test-Path -LiteralPath $bodyPath)) {
    throw "Prompt body '$bodyPath' for '$($selected.title)' was not found."
}

$body = Get-Content -Raw -LiteralPath $bodyPath

if ($Json) {
    [pscustomobject]@{
        title = $selected.title
        slug = $selected.slug
        category = $selected.category
        path = $selected.path
        bodyFormat = $index.bodyFormat
        placeholders = @($selected.placeholders)
        body = $body
    } | ConvertTo-Json -Depth 10
} else {
    $body
}
