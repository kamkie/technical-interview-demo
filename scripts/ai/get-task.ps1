param(
    [string]$Name,
    [switch]$List,
    [switch]$Json
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$repoRoot = Resolve-Path -LiteralPath (Join-Path $PSScriptRoot "../..")
$indexPath = Join-Path $repoRoot "ai/task-library/index.json"

if (-not (Test-Path -LiteralPath $indexPath))
{
    throw "Task-library index '$indexPath' was not found."
}

$index = Get-Content -Raw -LiteralPath $indexPath | ConvertFrom-Json
$tasks = @($index.tasks)

function Normalize-TaskName
{
    param([Parameter(Mandatory = $true)][string]$Value)

    return ($Value.Trim().ToLowerInvariant() -replace "[^a-z0-9]+", "-").Trim("-")
}

if ($List)
{
    if ($Json)
    {
        [pscustomobject]@{
            schemaVersion = $index.schemaVersion
            tasks = @(
            $tasks |
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
    }
    else
    {
        $rows = $tasks |
            Sort-Object category, title |
            Select-Object category, title, slug, path, @{ Name = "placeholders"; Expression = { ($_.placeholders -join ", ") } }

        $rows | Format-Table -AutoSize
    }
    exit 0
}

if ( [string]::IsNullOrWhiteSpace($Name))
{
    throw "Provide -Name '<task title>' or use -List."
}

$normalizedName = Normalize-TaskName -Value $Name
$exactMatches = @(
$tasks | Where-Object {
    $_.title.Equals($Name, [System.StringComparison]::OrdinalIgnoreCase) -or
        $_.slug.Equals($normalizedName, [System.StringComparison]::OrdinalIgnoreCase)
}
)

if ($exactMatches.Count -eq 1)
{
    $selected = $exactMatches[0]
}
else
{
    $term = [Regex]::Escape($Name.Trim())
    $slugTerm = [Regex]::Escape($normalizedName)
    $candidates = @(
    $tasks | Where-Object {
        $_.title -match $term -or $_.slug -match $slugTerm
    }
    )

    if ($candidates.Count -eq 0)
    {
        $available = ($tasks | Sort-Object title | Select-Object -ExpandProperty title) -join "', '"
        throw "No task matched '$Name'. Available tasks: '$available'."
    }

    if ($candidates.Count -gt 1)
    {
        $candidateTitles = ($candidates | Sort-Object title | Select-Object -ExpandProperty title) -join "', '"
        throw "Task name '$Name' is ambiguous. Matches: '$candidateTitles'."
    }

    $selected = $candidates[0]
}

$bodyPath = Join-Path $repoRoot $selected.path
if (-not (Test-Path -LiteralPath $bodyPath))
{
    throw "Task body '$bodyPath' for '$( $selected.title )' was not found."
}

$body = Get-Content -Raw -LiteralPath $bodyPath

if ($Json)
{
    [pscustomobject]@{
        title = $selected.title
        slug = $selected.slug
        category = $selected.category
        path = $selected.path
        placeholders = @($selected.placeholders)
        body = $body
    } | ConvertTo-Json -Depth 10
}
else
{
    $body
}
