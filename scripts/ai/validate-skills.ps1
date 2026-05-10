param(
    [string]$SkillsRoot = ".agents/skills",
    [string[]]$Skill = @()
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function Resolve-LocalPath
{
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path
    )

    if ([System.IO.Path]::IsPathRooted($Path))
    {
        return [System.IO.Path]::GetFullPath($Path)
    }

    return [System.IO.Path]::GetFullPath((Join-Path (Get-Location).Path $Path))
}

function Get-FrontMatter
{
    param(
        [Parameter(Mandatory = $true)]
        [string]$Text
    )

    $lines = @($Text -split "`r?`n")
    if ($lines.Count -lt 3 -or $lines[0].Trim() -ne "---")
    {
        return $null
    }

    for ($index = 1; $index -lt $lines.Count; $index++)
    {
        if ($lines[$index].Trim() -eq "---")
        {
            $frontMatterLines = @()
            if ($index -gt 1)
            {
                $frontMatterLines = @($lines[1..($index - 1)])
            }

            return [pscustomobject]@{
                Lines = $frontMatterLines
                EndIndex = $index
            }
        }
    }

    return $null
}

function Get-ScalarValue
{
    param(
        [Parameter(Mandatory = $true)]
        [string[]]$Lines,

        [Parameter(Mandatory = $true)]
        [string]$Key
    )

    $pattern = "^\s*$([regex]::Escape($Key))\s*:\s*(.+?)\s*$"
    foreach ($line in $Lines)
    {
        if ($line -match $pattern)
        {
            return $Matches[1].Trim().Trim('"').Trim("'")
        }
    }

    return $null
}

function Test-Heading
{
    param(
        [Parameter(Mandatory = $true)]
        [string]$Text,

        [Parameter(Mandatory = $true)]
        [string]$Heading
    )

    return [regex]::IsMatch($Text, "(?m)^$([regex]::Escape($Heading))\s*$")
}

$root = Resolve-LocalPath -Path $SkillsRoot
if (-not (Test-Path -LiteralPath $root -PathType Container))
{
    throw "Skills root '$SkillsRoot' does not exist."
}

$skillDirectories = @()
if ($Skill.Count -gt 0)
{
    foreach ($skillName in $Skill)
    {
        $skillPath = Join-Path $root $skillName
        if (-not (Test-Path -LiteralPath $skillPath -PathType Container))
        {
            throw "Skill '$skillName' does not exist under '$SkillsRoot'."
        }
        $skillDirectories += Get-Item -LiteralPath $skillPath
    }
}
else
{
    $skillDirectories = @(Get-ChildItem -LiteralPath $root -Directory | Sort-Object Name)
}

$errors = [System.Collections.Generic.List[string]]::new()
$seenFrontMatterNames = @{}
$requiredHeadings = @(
    "# ",
    "## Overview",
    "## Read Set",
    "## Inputs",
    "## Workflow",
    "## Stop Conditions",
    "## Output"
)

foreach ($skillDirectory in $skillDirectories)
{
    $directoryName = $skillDirectory.Name
    $skillFile = Join-Path $skillDirectory.FullName "SKILL.md"

    if (-not (Test-Path -LiteralPath $skillFile -PathType Leaf))
    {
        [void]$errors.Add("[$directoryName] missing SKILL.md")
        continue
    }

    $content = Get-Content -LiteralPath $skillFile -Raw
    $frontMatter = Get-FrontMatter -Text $content
    if ($null -eq $frontMatter)
    {
        [void]$errors.Add("[$directoryName] missing frontmatter block")
        continue
    }

    $frontMatterName = Get-ScalarValue -Lines $frontMatter.Lines -Key "name"
    $description = Get-ScalarValue -Lines $frontMatter.Lines -Key "description"

    if ([string]::IsNullOrWhiteSpace($frontMatterName))
    {
        [void]$errors.Add("[$directoryName] missing frontmatter name")
    }
    elseif ($frontMatterName -ne $directoryName)
    {
        [void]$errors.Add("[$directoryName] frontmatter name '$frontMatterName' does not match directory name")
    }
    elseif ($seenFrontMatterNames.ContainsKey($frontMatterName))
    {
        [void]$errors.Add("[$directoryName] duplicate frontmatter name '$frontMatterName'")
    }
    else
    {
        $seenFrontMatterNames[$frontMatterName] = $true
    }

    if ([string]::IsNullOrWhiteSpace($description))
    {
        [void]$errors.Add("[$directoryName] missing frontmatter description")
    }

    foreach ($heading in $requiredHeadings)
    {
        if ($heading -eq "# ")
        {
            if (-not [regex]::IsMatch($content, "(?m)^#\s+\S"))
            {
                [void]$errors.Add("[$directoryName] missing H1 heading")
            }
            continue
        }

        if (-not (Test-Heading -Text $content -Heading $heading))
        {
            [void]$errors.Add("[$directoryName] missing required heading '$heading'")
        }
    }

    if ($content -notmatch "\.agents/references/")
    {
        [void]$errors.Add("[$directoryName] does not reference an owner guide under .agents/references/")
    }

    if ([regex]::IsMatch($content, "(?im)\b(TODO|TBD|FIXME)\b"))
    {
        [void]$errors.Add("[$directoryName] contains unresolved placeholder text")
    }

    $openAiManifest = Join-Path $skillDirectory.FullName "agents/openai.yaml"
    if (Test-Path -LiteralPath $openAiManifest -PathType Leaf)
    {
        $manifest = Get-Content -LiteralPath $openAiManifest -Raw
        foreach ($field in @("interface:", "display_name:", "short_description:", "default_prompt:"))
        {
            if ($manifest -notmatch "(?m)^\s*$([regex]::Escape($field))")
            {
                [void]$errors.Add("[$directoryName] agents/openai.yaml missing '$field'")
            }
        }

        $defaultPromptSkillPattern = [regex]::Escape("`$$directoryName")
        if ($manifest -notmatch "$defaultPromptSkillPattern\b")
        {
            [void]$errors.Add("[$directoryName] agents/openai.yaml default prompt does not reference `$$directoryName")
        }
    }
}

if ($errors.Count -gt 0)
{
    throw "Skill validation failed with $($errors.Count) issue(s):`n$($errors -join [Environment]::NewLine)"
}

Write-Output "Validated $($skillDirectories.Count) skill(s) under '$SkillsRoot'."
