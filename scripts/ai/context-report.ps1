param(
    [string]$Range,

    [ValidateSet("endpoint", "beginning-to-end", "range-summary", "stepwise", "commit-by-commit", "per-commit")]
    [string]$Mode = "endpoint",

    [string]$OutputPath,

    [double]$DefaultLoadWarningPercent = 5.0,

    [double]$TotalInventoryWarningPercent = 10.0,

    [string]$GrowthRationale,

    [switch]$SelfTest
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$utf8 = [System.Text.Encoding]::UTF8
$invariantCulture = [System.Globalization.CultureInfo]::InvariantCulture

$scenarioDefinitions = [ordered]@{
    default = @("AGENTS.md")
    short = @("AGENTS.md")
    discovery = @("AGENTS.md", ".agents/references/planning.md")
    planning = @("AGENTS.md", ".agents/references/planning.md")
    implementation = @(
        "AGENTS.md",
        ".agents/references/plan-execution.md",
        ".agents/references/testing.md",
        ".agents/references/reviews.md",
        ".agents/references/execution.md",
        ".agents/plans"
    )
    testing = @("AGENTS.md", ".agents/references/testing.md", ".agents/references/reviews.md")
    review = @("AGENTS.md", ".agents/references/reviews.md")
    integration = @("AGENTS.md", ".agents/references/workflow.md", ".agents/references/testing.md", ".agents/references/reviews.md")
    release = @("AGENTS.md", ".agents/references/releases.md")
}

$scenarioLabels = @{
    default = "default"
    short = "short request"
    discovery = "discovery task"
    planning = "planning task"
    implementation = "implementation task"
    testing = "testing task"
    review = "review task"
    integration = "integration task"
    release = "release task"
}

$standingOwnerGuides = @{
    ".agents/references/code-style.md" = $true
    ".agents/references/documentation.md" = $true
    ".agents/references/environment-quick-ref.md" = $true
    ".agents/references/plan-execution.md" = $true
    ".agents/references/execution.md" = $true
    ".agents/references/LEARNINGS.md" = $true
    ".agents/references/planning.md" = $true
    ".agents/references/reviews.md" = $true
    ".agents/references/releases.md" = $true
    ".agents/references/testing.md" = $true
    ".agents/references/workflow.md" = $true
}

function Invoke-GitBytes
{
    param(
        [Parameter(Mandatory = $true)]
        [string[]]$Arguments,

        [Parameter(Mandatory = $true)]
        [string]$WorkingDirectory
    )

    $processStartInfo = [System.Diagnostics.ProcessStartInfo]::new()
    $processStartInfo.FileName = "git"
    $processStartInfo.WorkingDirectory = $WorkingDirectory
    $processStartInfo.RedirectStandardOutput = $true
    $processStartInfo.RedirectStandardError = $true
    $processStartInfo.UseShellExecute = $false

    foreach ($argument in $Arguments)
    {
        [void]$processStartInfo.ArgumentList.Add($argument)
    }

    $process = [System.Diagnostics.Process]::new()
    $process.StartInfo = $processStartInfo
    [void]$process.Start()

    $stdout = [System.IO.MemoryStream]::new()
    $stderr = [System.IO.MemoryStream]::new()
    $process.StandardOutput.BaseStream.CopyTo($stdout)
    $process.StandardError.BaseStream.CopyTo($stderr)
    $process.WaitForExit()

    if ($process.ExitCode -ne 0)
    {
        $stderrText = $utf8.GetString($stderr.ToArray()).Trim()
        throw "git $( $Arguments -join ' ' ) failed.`n$stderrText"
    }

    return ,([byte[]]$stdout.ToArray())
}

function Invoke-GitText
{
    param(
        [Parameter(Mandatory = $true)]
        [string[]]$Arguments,

        [Parameter(Mandatory = $true)]
        [string]$WorkingDirectory
    )

    $bytes = Invoke-GitBytes -Arguments $Arguments -WorkingDirectory $WorkingDirectory
    return $utf8.GetString($bytes).TrimEnd("`r", "`n")
}

function New-Metrics
{
    param(
        [long]$Chars = 0,
        [long]$Bytes = 0,
        [long]$Lines = 0,
        [long]$Words = 0,
        [long]$Tokens = 0
    )

    return [pscustomobject]@{
        Chars = $Chars
        Bytes = $Bytes
        Lines = $Lines
        Words = $Words
        Tokens = $Tokens
    }
}

function Add-Metrics
{
    param(
        [Parameter(Mandatory = $true)]
        [pscustomobject]$Left,

        [Parameter(Mandatory = $true)]
        [pscustomobject]$Right
    )

    return New-Metrics `
        -Chars ($Left.Chars + $Right.Chars) `
        -Bytes ($Left.Bytes + $Right.Bytes) `
        -Lines ($Left.Lines + $Right.Lines) `
        -Words ($Left.Words + $Right.Words) `
        -Tokens ($Left.Tokens + $Right.Tokens)
}

function Get-TextMetrics
{
    param(
        [Parameter(Mandatory = $true)]
        [byte[]]$Bytes
    )

    $text = $utf8.GetString($Bytes)
    $chars = $text.Length
    $lines = 0
    if ($chars -gt 0)
    {
        $lines = [regex]::Matches($text, "`n").Count
        if (-not $text.EndsWith("`n"))
        {
            $lines++
        }
    }
    $words = [regex]::Matches($text, "\S+").Count
    $tokens = [long][Math]::Ceiling($chars / 4.0)

    return New-Metrics -Chars $chars -Bytes $Bytes.Length -Lines $lines -Words $words -Tokens $tokens
}

function Format-Metric
{
    param(
        [Parameter(Mandatory = $true)]
        [pscustomobject]$Metric
    )

    return [string]::Format($invariantCulture, "{0:N0}c/{1:N0}t", $Metric.Chars, $Metric.Tokens)
}

function Get-PercentDelta
{
    param(
        [long]$OldValue,
        [long]$NewValue
    )

    if ($OldValue -eq 0)
    {
        if ($NewValue -eq 0)
        {
            return 0.0
        }
        return 100.0
    }

    return (($NewValue - $OldValue) / $OldValue) * 100.0
}

function Format-Delta
{
    param(
        [long]$CharsDelta,
        [long]$TokensDelta,
        [double]$PercentDelta
    )

    $charsSign = if ($CharsDelta -ge 0) { "+" } else { "" }
    $tokensSign = if ($TokensDelta -ge 0) { "+" } else { "" }
    $percentSign = if ($PercentDelta -ge 0) { "+" } else { "" }

    return [string]::Format($invariantCulture, "{0}{1:N0} chars, {2}{3:N0} tokens, {4}{5:N1}%", $charsSign, $CharsDelta, $tokensSign, $TokensDelta, $percentSign, $PercentDelta)
}

function Normalize-ComparisonMode
{
    param(
        [Parameter(Mandatory = $true)]
        [string]$Mode
    )

    switch ($Mode)
    {
        { $_ -in @("endpoint", "beginning-to-end", "range-summary") } { return "endpoint" }
        { $_ -in @("stepwise", "commit-by-commit", "per-commit") } { return "stepwise" }
        default { throw "Unknown comparison mode '$Mode'." }
    }
}

function Get-AvailableOutputPath
{
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path
    )

    if (-not (Test-Path -LiteralPath $Path))
    {
        return $Path
    }

    $directory = Split-Path -Path $Path -Parent
    $fileName = [System.IO.Path]::GetFileNameWithoutExtension($Path)
    $extension = [System.IO.Path]::GetExtension($Path)
    $index = 2
    do
    {
        $candidate = Join-Path $directory "$fileName-$index$extension"
        $index++
    } while (Test-Path -LiteralPath $candidate)

    return $candidate
}

function Resolve-ReportCommits
{
    param(
        [string]$Range,

        [Parameter(Mandatory = $true)]
        [string]$WorkingDirectory
    )

    if ([string]::IsNullOrWhiteSpace($Range))
    {
        $start = Invoke-GitText -Arguments @("rev-parse", "HEAD~1") -WorkingDirectory $WorkingDirectory
        $end = Invoke-GitText -Arguments @("rev-parse", "HEAD") -WorkingDirectory $WorkingDirectory
    }
    elseif ($Range -match "\.\.\.")
    {
        throw "Use a first-parent two-dot range such as START..END; three-dot ranges are not supported."
    }
    elseif ($Range -match "^(?<start>.+)\.\.(?<end>.+)$")
    {
        $start = Invoke-GitText -Arguments @("rev-parse", $Matches.start.Trim()) -WorkingDirectory $WorkingDirectory
        $end = Invoke-GitText -Arguments @("rev-parse", $Matches.end.Trim()) -WorkingDirectory $WorkingDirectory
    }
    else
    {
        throw "Range must be blank or use START..END syntax."
    }

    $rangeCommitsText = Invoke-GitText -Arguments @("rev-list", "--first-parent", "--reverse", "$start..$end") -WorkingDirectory $WorkingDirectory
    $rangeCommits = @()
    if (-not [string]::IsNullOrWhiteSpace($rangeCommitsText))
    {
        $rangeCommits = @($rangeCommitsText -split "`n" | Where-Object { -not [string]::IsNullOrWhiteSpace($_) })
    }

    $commits = @($start) + @($rangeCommits | Where-Object { $_ -ne $start })
    if ($commits.Count -lt 2)
    {
        throw "The selected range must include at least two commits."
    }

    return $commits
}

function Get-CommitMeta
{
    param(
        [Parameter(Mandatory = $true)]
        [string]$Commit,

        [Parameter(Mandatory = $true)]
        [string]$WorkingDirectory
    )

    $raw = Invoke-GitText -Arguments @("show", "-s", "--format=%h%x09%ad%x09%s", "--date=short", $Commit) -WorkingDirectory $WorkingDirectory
    $parts = $raw -split "`t", 3
    if ($parts.Count -ne 3)
    {
        throw "Could not parse commit metadata for '$Commit'."
    }

    return [pscustomobject]@{
        Hash = $Commit
        Short = $parts[0]
        Date = $parts[1]
        Subject = $parts[2]
    }
}

function Get-AiFiles
{
    param(
        [Parameter(Mandatory = $true)]
        [string]$Commit,

        [Parameter(Mandatory = $true)]
        [string]$WorkingDirectory
    )

    $raw = Invoke-GitText -Arguments @("ls-tree", "-r", "--name-only", $Commit, "--", "AGENTS.md", ".agents") -WorkingDirectory $WorkingDirectory
    if ([string]::IsNullOrWhiteSpace($raw))
    {
        return @()
    }

    return @(
        $raw -split "`n" |
            ForEach-Object { $_.Trim().Replace("\", "/") } |
            Where-Object { $_ -eq "AGENTS.md" -or $_.StartsWith(".agents/") } |
            Sort-Object -Unique
    )
}

function Get-ScenarioPaths
{
    param(
        [Parameter(Mandatory = $true)]
        [string[]]$Files,

        [Parameter(Mandatory = $true)]
        [string[]]$Specs
    )

    $selected = [System.Collections.Generic.HashSet[string]]::new()
    $fileSet = [System.Collections.Generic.HashSet[string]]::new([string[]]$Files)

    foreach ($spec in $Specs)
    {
        if ($fileSet.Contains($spec))
        {
            [void]$selected.Add($spec)
            continue
        }

        $prefix = $spec.TrimEnd("/") + "/"
        foreach ($file in $Files)
        {
            if ($file.StartsWith($prefix))
            {
                [void]$selected.Add($file)
            }
        }
    }

    return @($selected | Sort-Object)
}

function Get-BucketName
{
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path
    )

    $parts = $Path -split "/"
    if ($Path -eq "AGENTS.md" -or ($Path.StartsWith(".agents/") -and $parts.Count -eq 2))
    {
        return "standing root and AI top-level files"
    }

    if ($Path.StartsWith(".agents/plans/") -and ([System.IO.Path]::GetFileName($Path)).StartsWith("PLAN_"))
    {
        return "active plan files"
    }

    if ($Path.StartsWith(".agents/archive/"))
    {
        return "archived plans"
    }

    if ($Path.StartsWith(".agents/reports/"))
    {
        return "archived report artifacts"
    }

    if ($Path.StartsWith(".agents/skills/repo-task/references/tasks/"))
    {
        return "on-demand tasks"
    }

    if ($Path.StartsWith(".agents/references/"))
    {
        if ($standingOwnerGuides.ContainsKey($Path))
        {
            return "standing root and AI top-level files"
        }
        return "on-demand references"
    }

    if ($Path.StartsWith(".agents/templates/"))
    {
        return "on-demand templates"
    }

    if ($Path.StartsWith(".agents/skills/"))
    {
        if ($Path -like ".agents/skills/*/SKILL.md" -or $Path -like ".agents/skills/*/agents/*")
        {
            return "repo skill entrypoints"
        }
        return "repo skill references"
    }

    return "standing root and AI top-level files"
}

function Get-ParentDirectories
{
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path
    )

    $parts = $Path -split "/"
    if ($parts.Count -lt 2)
    {
        return @()
    }

    $parents = @()
    for ($index = 1; $index -lt $parts.Count; $index++)
    {
        $parents += ($parts[0..($index - 1)] -join "/")
    }
    return $parents
}

function Add-HashMetric
{
    param(
        [Parameter(Mandatory = $true)]
        [hashtable]$Hash,

        [Parameter(Mandatory = $true)]
        [string]$Key,

        [Parameter(Mandatory = $true)]
        [pscustomobject]$Metric
    )

    if (-not $Hash.ContainsKey($Key))
    {
        $Hash[$Key] = New-Metrics
    }
    $Hash[$Key] = Add-Metrics -Left $Hash[$Key] -Right $Metric
}

function Measure-Commit
{
    param(
        [Parameter(Mandatory = $true)]
        [string]$Commit,

        [Parameter(Mandatory = $true)]
        [string]$WorkingDirectory
    )

    $meta = Get-CommitMeta -Commit $Commit -WorkingDirectory $WorkingDirectory
    $files = @(Get-AiFiles -Commit $Commit -WorkingDirectory $WorkingDirectory)
    $fileMetrics = @{}
    $dirMetrics = @{}
    $bucketMetrics = @{}
    $total = New-Metrics

    foreach ($path in $files)
    {
        $blob = Invoke-GitBytes -Arguments @("show", "$Commit`:$path") -WorkingDirectory $WorkingDirectory
        $metric = Get-TextMetrics -Bytes $blob
        $fileMetrics[$path] = $metric
        $total = Add-Metrics -Left $total -Right $metric

        $bucketName = Get-BucketName -Path $path
        Add-HashMetric -Hash $bucketMetrics -Key $bucketName -Metric $metric

        foreach ($directory in Get-ParentDirectories -Path $path)
        {
            Add-HashMetric -Hash $dirMetrics -Key $directory -Metric $metric
        }
    }

    $scenarioMetrics = @{}
    foreach ($entry in $scenarioDefinitions.GetEnumerator())
    {
        $scenarioMetric = New-Metrics
        $scenarioPaths = @(Get-ScenarioPaths -Files $files -Specs $entry.Value)
        foreach ($path in $scenarioPaths)
        {
            $scenarioMetric = Add-Metrics -Left $scenarioMetric -Right $fileMetrics[$path]
        }
        $scenarioMetrics[$entry.Key] = $scenarioMetric
    }

    $standingContext = New-Metrics
    foreach ($path in $files)
    {
        if ($path -eq "AGENTS.md" -or $standingOwnerGuides.ContainsKey($path))
        {
            $standingContext = Add-Metrics -Left $standingContext -Right $fileMetrics[$path]
        }
    }

    return [pscustomobject]@{
        Meta = $meta
        Files = $files
        FileMetrics = $fileMetrics
        DirMetrics = $dirMetrics
        BucketMetrics = $bucketMetrics
        ScenarioMetrics = $scenarioMetrics
        StandingContext = $standingContext
        Total = $total
    }
}

function Get-ScenarioMinMax
{
    param(
        [Parameter(Mandatory = $true)]
        [pscustomobject[]]$CommitData,

        [Parameter(Mandatory = $true)]
        [string]$Scenario
    )

    $sorted = @($CommitData | Sort-Object { $_.ScenarioMetrics[$Scenario].Chars }, { $_.Meta.Short })
    return [pscustomobject]@{
        Smallest = $sorted[0]
        Biggest = $sorted[-1]
    }
}

function Get-TotalMinMax
{
    param(
        [Parameter(Mandatory = $true)]
        [pscustomobject[]]$CommitData
    )

    $sorted = @($CommitData | Sort-Object { $_.Total.Chars }, { $_.Meta.Short })
    return [pscustomobject]@{
        Smallest = $sorted[0]
        Biggest = $sorted[-1]
    }
}

function Get-AdjacentDeltas
{
    param(
        [Parameter(Mandatory = $true)]
        [pscustomobject[]]$CommitData
    )

    $deltas = @()
    for ($index = 1; $index -lt $CommitData.Count; $index++)
    {
        $previous = $CommitData[$index - 1]
        $current = $CommitData[$index]
        $deltas += [pscustomobject]@{
            Previous = $previous
            Current = $current
            DefaultCharsDelta = $current.ScenarioMetrics.default.Chars - $previous.ScenarioMetrics.default.Chars
            DefaultTokensDelta = $current.ScenarioMetrics.default.Tokens - $previous.ScenarioMetrics.default.Tokens
            TotalCharsDelta = $current.Total.Chars - $previous.Total.Chars
            TotalTokensDelta = $current.Total.Tokens - $previous.Total.Tokens
        }
    }
    return $deltas
}

function Get-ThresholdStatus
{
    param(
        [long]$OldChars,

        [long]$NewChars,

        [double]$ThresholdPercent,

        [bool]$AllowRationale = $false,

        [bool]$HasRationale = $false
    )

    $deltaChars = $NewChars - $OldChars
    $deltaPercent = Get-PercentDelta -OldValue $OldChars -NewValue $NewChars
    $crossedThreshold = $deltaChars -gt 0 -and $deltaPercent -gt $ThresholdPercent
    $status = "Passed"

    if ($crossedThreshold)
    {
        if ($AllowRationale -and $HasRationale)
        {
            $status = "Rationalized"
        }
        else
        {
            $status = "Warning"
        }
    }

    return [pscustomobject]@{
        Status = $status
        DeltaChars = $deltaChars
        DeltaPercent = $deltaPercent
        ThresholdPercent = $ThresholdPercent
        CrossedThreshold = $crossedThreshold
    }
}

function Get-RangeChangedFiles
{
    param(
        [Parameter(Mandatory = $true)]
        [string]$OldCommit,

        [Parameter(Mandatory = $true)]
        [string]$NewCommit,

        [Parameter(Mandatory = $true)]
        [string]$WorkingDirectory
    )

    $raw = Invoke-GitText -Arguments @("diff", "--name-only", $OldCommit, $NewCommit, "--", "AGENTS.md", ".agents") -WorkingDirectory $WorkingDirectory
    if ([string]::IsNullOrWhiteSpace($raw))
    {
        return @()
    }

    return @($raw -split "`n" | ForEach-Object { $_.Trim().Replace("\", "/") } | Where-Object { -not [string]::IsNullOrWhiteSpace($_) })
}

function Get-GuardrailEvaluation
{
    param(
        [Parameter(Mandatory = $true)]
        [pscustomobject[]]$CommitData,

        [Parameter(Mandatory = $true)]
        [string[]]$ChangedFiles,

        [Parameter(Mandatory = $true)]
        [double]$DefaultThresholdPercent,

        [Parameter(Mandatory = $true)]
        [double]$TotalThresholdPercent,

        [string]$Rationale
    )

    $oldest = $CommitData[0]
    $newest = $CommitData[-1]
    $archiveOrReportFiles = @($ChangedFiles | Where-Object { $_.StartsWith(".agents/archive/") -or $_.StartsWith(".agents/reports/") })
    $hasTextRationale = -not [string]::IsNullOrWhiteSpace($Rationale)
    $hasInventoryRationale = $archiveOrReportFiles.Count -gt 0 -or $hasTextRationale

    $defaultStatus = Get-ThresholdStatus `
        -OldChars $oldest.ScenarioMetrics.default.Chars `
        -NewChars $newest.ScenarioMetrics.default.Chars `
        -ThresholdPercent $DefaultThresholdPercent

    $totalStatus = Get-ThresholdStatus `
        -OldChars $oldest.Total.Chars `
        -NewChars $newest.Total.Chars `
        -ThresholdPercent $TotalThresholdPercent `
        -AllowRationale $true `
        -HasRationale $hasInventoryRationale

    $adjacentCrossings = @()
    foreach ($delta in Get-AdjacentDeltas -CommitData $CommitData)
    {
        $defaultDeltaPercent = Get-PercentDelta -OldValue $delta.Previous.ScenarioMetrics.default.Chars -NewValue $delta.Current.ScenarioMetrics.default.Chars
        $totalDeltaPercent = Get-PercentDelta -OldValue $delta.Previous.Total.Chars -NewValue $delta.Current.Total.Chars
        $defaultCrossed = $delta.DefaultCharsDelta -gt 0 -and $defaultDeltaPercent -gt $DefaultThresholdPercent
        $totalCrossed = $delta.TotalCharsDelta -gt 0 -and $totalDeltaPercent -gt $TotalThresholdPercent

        if ($defaultCrossed -or $totalCrossed)
        {
            $adjacentCrossings += [pscustomobject]@{
                Previous = $delta.Previous
                Current = $delta.Current
                DefaultCharsDelta = $delta.DefaultCharsDelta
                DefaultTokensDelta = $delta.DefaultTokensDelta
                DefaultPercentDelta = $defaultDeltaPercent
                TotalCharsDelta = $delta.TotalCharsDelta
                TotalTokensDelta = $delta.TotalTokensDelta
                TotalPercentDelta = $totalDeltaPercent
                DefaultCrossed = $defaultCrossed
                TotalCrossed = $totalCrossed
            }
        }
    }

    return [pscustomobject]@{
        Default = $defaultStatus
        Total = $totalStatus
        ArchiveOrReportFiles = $archiveOrReportFiles
        TextRationale = $Rationale
        HasInventoryRationale = $hasInventoryRationale
        AdjacentCrossings = $adjacentCrossings
    }
}

function Invoke-GuardrailSelfTest
{
    $defaultWarning = Get-ThresholdStatus -OldChars 1000 -NewChars 1060 -ThresholdPercent 5.0
    if ($defaultWarning.Status -ne "Warning")
    {
        throw "Expected default threshold crossing to warn."
    }

    $totalWarning = Get-ThresholdStatus -OldChars 1000 -NewChars 1110 -ThresholdPercent 10.0 -AllowRationale $true -HasRationale $false
    if ($totalWarning.Status -ne "Warning")
    {
        throw "Expected unrationalized total threshold crossing to warn."
    }

    $totalRationalized = Get-ThresholdStatus -OldChars 1000 -NewChars 1110 -ThresholdPercent 10.0 -AllowRationale $true -HasRationale $true
    if ($totalRationalized.Status -ne "Rationalized")
    {
        throw "Expected rationalized total threshold crossing to be marked rationalized."
    }

    $passed = Get-ThresholdStatus -OldChars 1000 -NewChars 1040 -ThresholdPercent 5.0
    if ($passed.Status -ne "Passed")
    {
        throw "Expected below-threshold growth to pass."
    }

    Write-Output "Context report guardrail self-test passed."
}

function Escape-MarkdownCell
{
    param(
        [string]$Value
    )

    return ($Value -replace "\|", "\|").Replace("`r", " ").Replace("`n", " ")
}

function Get-RowMetric
{
    param(
        [Parameter(Mandatory = $true)]
        [pscustomobject]$CommitData,

        [Parameter(Mandatory = $true)]
        [string]$RowName
    )

    if ($RowName.EndsWith("/"))
    {
        $key = $RowName.TrimEnd("/")
        if ($CommitData.DirMetrics.ContainsKey($key))
        {
            return $CommitData.DirMetrics[$key]
        }
        return $null
    }

    if ($CommitData.FileMetrics.ContainsKey($RowName))
    {
        return $CommitData.FileMetrics[$RowName]
    }

    return $null
}

function Build-Report
{
    param(
        [Parameter(Mandatory = $true)]
        [pscustomobject[]]$CommitData,

        [Parameter(Mandatory = $true)]
        [int]$SelectedCommitCount,

        [Parameter(Mandatory = $true)]
        [string]$ComparisonMode,

        [Parameter(Mandatory = $true)]
        [pscustomobject]$GuardrailEvaluation
    )

    $oldest = $CommitData[0]
    $newest = $CommitData[-1]
    $commitSteps = [Math]::Max(1, $SelectedCommitCount - 1)
    $omittedCommitCount = [Math]::Max(0, $SelectedCommitCount - $CommitData.Count)

    $defaultDeltaChars = $newest.ScenarioMetrics.default.Chars - $oldest.ScenarioMetrics.default.Chars
    $defaultDeltaTokens = $newest.ScenarioMetrics.default.Tokens - $oldest.ScenarioMetrics.default.Tokens
    $defaultDeltaPercent = Get-PercentDelta -OldValue $oldest.ScenarioMetrics.default.Chars -NewValue $newest.ScenarioMetrics.default.Chars
    $totalDeltaChars = $newest.Total.Chars - $oldest.Total.Chars
    $totalDeltaTokens = $newest.Total.Tokens - $oldest.Total.Tokens
    $totalDeltaPercent = Get-PercentDelta -OldValue $oldest.Total.Chars -NewValue $newest.Total.Chars
    $standingDensity = if ($newest.Total.Chars -eq 0) { 0.0 } else { $newest.StandingContext.Chars / $newest.Total.Chars }
    $growthChars = $totalDeltaChars / $commitSteps
    $growthTokens = $totalDeltaTokens / $commitSteps

    $activePlan = if ($newest.BucketMetrics.ContainsKey("active plan files")) { $newest.BucketMetrics["active plan files"] } else { New-Metrics }
    $onDemandTask = if ($newest.BucketMetrics.ContainsKey("on-demand tasks")) { $newest.BucketMetrics["on-demand tasks"] } else { New-Metrics }
    $bloatChars = $activePlan.Chars + $onDemandTask.Chars
    $defaultChars = $newest.ScenarioMetrics.default.Chars
    $bloatFactor = if ($defaultChars -eq 0) { 0.0 } else { ($bloatChars / $defaultChars) * 100.0 }

    $bucketSorted = @($newest.BucketMetrics.GetEnumerator() | Sort-Object { $_.Value.Chars } -Descending)
    $largestFiles = @($newest.FileMetrics.GetEnumerator() | Sort-Object { $_.Value.Chars } -Descending | Select-Object -First 8)
    $adjacentDeltas = @(Get-AdjacentDeltas -CommitData $CommitData)

    $lines = [System.Collections.Generic.List[string]]::new()
    $lines.Add("# AI Instruction Context Report")
    $lines.Add("")
    $lines.Add("Generated by ``scripts/ai/context-report.ps1`` from committed Git objects with a temporary git worktree.")
    $lines.Add("")
    $lines.Add("Comparison mode: ``$ComparisonMode``.")
    if ($ComparisonMode -eq "endpoint" -and $omittedCommitCount -gt 0)
    {
        $lines.Add("")
        $lines.Add("Endpoint mode measured only the oldest and newest commits in the selected range; $omittedCommitCount interior commit(s) were omitted from the tables.")
    }
    $lines.Add("")
    $lines.Add("## Summary")
    $lines.Add("")
    $lines.Add("- Oldest commit analyzed: ``$($oldest.Meta.Short)`` ($($oldest.Meta.Date)) $($oldest.Meta.Subject).")
    $lines.Add("- Newest commit analyzed: ``$($newest.Meta.Short)`` ($($newest.Meta.Date)) $($newest.Meta.Subject).")

    if ($defaultDeltaChars -eq 0)
    {
        $lines.Add("- Standing default load stayed stable at $(Format-Metric -Metric $newest.ScenarioMetrics.default).")
    }
    elseif ($defaultDeltaChars -lt 0)
    {
        $lines.Add("- Standing default load decreased from $(Format-Metric -Metric $oldest.ScenarioMetrics.default) to $(Format-Metric -Metric $newest.ScenarioMetrics.default) ($(Format-Delta -CharsDelta $defaultDeltaChars -TokensDelta $defaultDeltaTokens -PercentDelta $defaultDeltaPercent)).")
    }
    else
    {
        $lines.Add("- Standing default load increased from $(Format-Metric -Metric $oldest.ScenarioMetrics.default) to $(Format-Metric -Metric $newest.ScenarioMetrics.default) ($(Format-Delta -CharsDelta $defaultDeltaChars -TokensDelta $defaultDeltaTokens -PercentDelta $defaultDeltaPercent)).")
    }

    $lines.Add("- Total measured AI instruction inventory changed from $(Format-Metric -Metric $oldest.Total) to $(Format-Metric -Metric $newest.Total) ($(Format-Delta -CharsDelta $totalDeltaChars -TokensDelta $totalDeltaTokens -PercentDelta $totalDeltaPercent)).")
    if ($defaultDeltaChars -le 0 -and $totalDeltaChars -le 0)
    {
        $lines.Add("- Most important finding: default context is not growing, and total AI inventory is stable or smaller across the measured range.")
    }
    elseif ($defaultDeltaChars -eq 0)
    {
        $lines.Add("- Most important finding: default context stayed stable while total inventory changed; review on-demand material and active plans before changing ``AGENTS.md``.")
    }
    else
    {
        $lines.Add("- Most important finding: default context grew, which affects every new-agent load and should be reviewed before adding more always-on guidance.")
    }
    $lines.Add("")
    $lines.Add("Measured dimensions were characters, UTF-8 bytes, lines, words, and estimated tokens (``ceiling(chars / 4)``). Tables use compact ``chars/tokens`` cells.")
    $lines.Add("")
    $lines.Add("## Statistics")
    $lines.Add("")
    $lines.Add("### Baseline Metrics")
    $lines.Add("")

    foreach ($scenario in $scenarioDefinitions.Keys)
    {
        $minMax = Get-ScenarioMinMax -CommitData $CommitData -Scenario $scenario
        $small = $minMax.Smallest
        $big = $minMax.Biggest
        $lines.Add("- $($scenarioLabels[$scenario]): smallest ``$($small.Meta.Short)`` ($($small.Meta.Date), $($small.Meta.Subject)) $(Format-Metric -Metric $small.ScenarioMetrics[$scenario]); biggest ``$($big.Meta.Short)`` ($($big.Meta.Date), $($big.Meta.Subject)) $(Format-Metric -Metric $big.ScenarioMetrics[$scenario]).")
    }

    $totalMinMax = Get-TotalMinMax -CommitData $CommitData
    $lines.Add("- total inventory: smallest ``$($totalMinMax.Smallest.Meta.Short)`` ($($totalMinMax.Smallest.Meta.Date), $($totalMinMax.Smallest.Meta.Subject)) $(Format-Metric -Metric $totalMinMax.Smallest.Total); biggest ``$($totalMinMax.Biggest.Meta.Short)`` ($($totalMinMax.Biggest.Meta.Date), $($totalMinMax.Biggest.Meta.Subject)) $(Format-Metric -Metric $totalMinMax.Biggest.Total).")
    $lines.Add("")
    $lines.Add("### Improvement Trend")
    $lines.Add("")

    foreach ($scenario in $scenarioDefinitions.Keys)
    {
        $oldMetric = $oldest.ScenarioMetrics[$scenario]
        $newMetric = $newest.ScenarioMetrics[$scenario]
        $charsDelta = $newMetric.Chars - $oldMetric.Chars
        $tokensDelta = $newMetric.Tokens - $oldMetric.Tokens
        $percentDelta = Get-PercentDelta -OldValue $oldMetric.Chars -NewValue $newMetric.Chars
        $lines.Add("- $($scenarioLabels[$scenario]): $(Format-Delta -CharsDelta $charsDelta -TokensDelta $tokensDelta -PercentDelta $percentDelta).")
    }
    $lines.Add("- total inventory: $(Format-Delta -CharsDelta $totalDeltaChars -TokensDelta $totalDeltaTokens -PercentDelta $totalDeltaPercent).")

    if ($ComparisonMode -eq "stepwise")
    {
        $lines.Add("")
        $lines.Add("### Adjacent Deltas")
        $lines.Add("")
        foreach ($delta in $adjacentDeltas)
        {
            $defaultPercent = Get-PercentDelta -OldValue $delta.Previous.ScenarioMetrics.default.Chars -NewValue $delta.Current.ScenarioMetrics.default.Chars
            $totalPercent = Get-PercentDelta -OldValue $delta.Previous.Total.Chars -NewValue $delta.Current.Total.Chars
            $lines.Add("- ``$($delta.Previous.Meta.Short)`` -> ``$($delta.Current.Meta.Short)``: default $(Format-Delta -CharsDelta $delta.DefaultCharsDelta -TokensDelta $delta.DefaultTokensDelta -PercentDelta $defaultPercent); total $(Format-Delta -CharsDelta $delta.TotalCharsDelta -TokensDelta $delta.TotalTokensDelta -PercentDelta $totalPercent).")
        }
    }

    $lines.Add("")
    $lines.Add("### Guardrail Status")
    $lines.Add("")
    $lines.Add(([string]::Format($invariantCulture, "- Default load threshold: {0:0.##}% growth. Status: **{1}**. Endpoint delta: {2}.", $GuardrailEvaluation.Default.ThresholdPercent, $GuardrailEvaluation.Default.Status, (Format-Delta -CharsDelta $GuardrailEvaluation.Default.DeltaChars -TokensDelta ($newest.ScenarioMetrics.default.Tokens - $oldest.ScenarioMetrics.default.Tokens) -PercentDelta $GuardrailEvaluation.Default.DeltaPercent))))
    $lines.Add(([string]::Format($invariantCulture, "- Total inventory threshold: {0:0.##}% growth. Status: **{1}**. Endpoint delta: {2}.", $GuardrailEvaluation.Total.ThresholdPercent, $GuardrailEvaluation.Total.Status, (Format-Delta -CharsDelta $GuardrailEvaluation.Total.DeltaChars -TokensDelta ($newest.Total.Tokens - $oldest.Total.Tokens) -PercentDelta $GuardrailEvaluation.Total.DeltaPercent))))

    if ($GuardrailEvaluation.HasInventoryRationale)
    {
        if ($GuardrailEvaluation.ArchiveOrReportFiles.Count -gt 0)
        {
            $lines.Add("- Inventory-growth rationale detected from archive/report changes: " + (@($GuardrailEvaluation.ArchiveOrReportFiles | ForEach-Object { "``$_``" }) -join ", ") + ".")
        }
        if (-not [string]::IsNullOrWhiteSpace($GuardrailEvaluation.TextRationale))
        {
            $lines.Add("- Inventory-growth rationale note: $($GuardrailEvaluation.TextRationale)")
        }
    }
    else
    {
        $lines.Add("- No archive/report rationale or explicit growth-rationale note was detected.")
    }

    if ($ComparisonMode -eq "stepwise")
    {
        $lines.Add("")
        $lines.Add("#### Adjacent Guardrail Crossings")
        $lines.Add("")
        if ($GuardrailEvaluation.AdjacentCrossings.Count -eq 0)
        {
            $lines.Add("- None.")
        }
        else
        {
            foreach ($crossing in $GuardrailEvaluation.AdjacentCrossings)
            {
                $triggerParts = @()
                if ($crossing.DefaultCrossed)
                {
                    $triggerParts += "default"
                }
                if ($crossing.TotalCrossed)
                {
                    $triggerParts += "total"
                }
                $lines.Add("- ``$($crossing.Previous.Meta.Short)`` -> ``$($crossing.Current.Meta.Short)`` crossed " + ($triggerParts -join " and ") + " threshold(s): default $(Format-Delta -CharsDelta $crossing.DefaultCharsDelta -TokensDelta $crossing.DefaultTokensDelta -PercentDelta $crossing.DefaultPercentDelta); total $(Format-Delta -CharsDelta $crossing.TotalCharsDelta -TokensDelta $crossing.TotalTokensDelta -PercentDelta $crossing.TotalPercentDelta).")
            }
        }
    }

    $lines.Add("")
    $lines.Add("### Context Density")
    $lines.Add("")
    $lines.Add("- Standing Context (``AGENTS.md`` plus standing ``.agents/references/*.md`` owner guides): $(Format-Metric -Metric $newest.StandingContext).")
    $lines.Add("- Total AI Inventory: $(Format-Metric -Metric $newest.Total).")
    $lines.Add(([string]::Format($invariantCulture, "- Density: {0:P1}.", $standingDensity)))
    $lines.Add("")
    $lines.Add("### Growth Velocity")
    $lines.Add("")
    $lines.Add(([string]::Format($invariantCulture, "- Average total inventory change per selected commit step: {0:+#,##0;-#,##0;0} chars and {1:+#,##0;-#,##0;0} estimated tokens.", $growthChars, $growthTokens)))
    $lines.Add("")
    $lines.Add("### Bloat Factor")
    $lines.Add("")
    $lines.Add(([string]::Format($invariantCulture, "- Active plans plus on-demand tasks add {0:N0} chars over a {1:N0}-char default load, or {2:N1}% of default-load size.", $bloatChars, $defaultChars, $bloatFactor)))
    $lines.Add("")
    $lines.Add("## Interpretation")
    $lines.Add("")

    if ($defaultDeltaChars -eq 0)
    {
        $lines.Add("- Standing default context is mostly stable because ``AGENTS.md`` did not change across the analyzed endpoints.")
    }
    elseif ($defaultDeltaChars -lt 0)
    {
        $lines.Add("- Standing default context is decreasing, which directly improves every new-agent load.")
    }
    else
    {
        $lines.Add("- Standing default context is increasing, which should be reviewed because it affects every new-agent load.")
    }

    $bucketText = @($bucketSorted | Select-Object -First 5 | ForEach-Object { "$($_.Key): $(Format-Metric -Metric $_.Value)" }) -join "; "
    $lines.Add("- Largest current bucket contributors: $bucketText.")
    $fileText = @($largestFiles | Select-Object -First 5 | ForEach-Object { "``$($_.Key)`` $(Format-Metric -Metric $_.Value)" }) -join "; "
    $lines.Add("- Largest current files: $fileText.")

    if ($adjacentDeltas.Count -gt 0)
    {
        $largestIncrease = @($adjacentDeltas | Sort-Object TotalCharsDelta -Descending)[0]
        $largestReduction = @($adjacentDeltas | Sort-Object TotalCharsDelta)[0]
        $comparisonLabel = if ($ComparisonMode -eq "stepwise") { "adjacent stepwise comparison" } else { "endpoint comparison" }
        $lines.Add(([string]::Format($invariantCulture, "- Largest total inventory increase in this range ($comparisonLabel): ``$($largestIncrease.Previous.Meta.Short)`` -> ``$($largestIncrease.Current.Meta.Short)`` ({0:+#,##0;-#,##0;0} chars).", $largestIncrease.TotalCharsDelta)))
        $lines.Add(([string]::Format($invariantCulture, "- Largest total inventory reduction in this range ($comparisonLabel): ``$($largestReduction.Previous.Meta.Short)`` -> ``$($largestReduction.Current.Meta.Short)`` ({0:+#,##0;-#,##0;0} chars).", $largestReduction.TotalCharsDelta)))
    }
    $lines.Add("- The repo is moving toward better on-demand loading when default context stays stable while details move into task starters, plans, reports, or on-demand references.")
    $lines.Add("- Caveats: token counts are approximate, task-load behavior is inferred from the owner map and exact starter text, and archived material is counted in total inventory but not in default or generic task loads.")
    $lines.Add("")
    $lines.Add("## Recommendations")
    $lines.Add("")
    $lines.Add("- Keep ``AGENTS.md`` stable unless a rule genuinely needs default-load visibility; route detailed workflow rules to ``.agents/references/`` and task bodies.")
    $lines.Add("- Review active plans for archival once released, since ``.agents/plans/`` is a large contributor to implementation task load when a request names active plans generically.")
    $lines.Add("- Keep repo-task task files compact and self-contained; they can grow unnoticed because they do not affect default load.")
    $lines.Add("- Use stepwise mode when trying to identify the exact commit that introduced context growth; use endpoint mode for routine range summaries.")
    $lines.Add("- Consider adding warning thresholds for default load and total ``.agents/`` inventory when this report becomes part of regular maintenance.")
    $lines.Add("")
    $lines.Add("## Table 1: Summary By Commit")
    $lines.Add("")

    $summaryHeaders = @(
        "commit",
        "commit date",
        "subject",
        "default load size",
        "short request size",
        "discovery task size",
        "planning task size",
        "implementation task size",
        "testing task size",
        "review task size",
        "integration task size",
        "release task size",
        "total AI instruction size"
    )
    $lines.Add("| " + ($summaryHeaders -join " | ") + " |")
    $lines.Add("| " + (@($summaryHeaders | ForEach-Object { "---" }) -join " | ") + " |")

    foreach ($data in $CommitData)
    {
        $row = @(
            "``$($data.Meta.Short)``",
            $data.Meta.Date,
            (Escape-MarkdownCell -Value $data.Meta.Subject)
        )
        foreach ($scenario in $scenarioDefinitions.Keys)
        {
            $row += Format-Metric -Metric $data.ScenarioMetrics[$scenario]
        }
        $row += Format-Metric -Metric $data.Total
        $lines.Add("| " + ($row -join " | ") + " |")
    }

    $lines.Add("")
    $lines.Add("## Table 2: File/Directory Size By Commit")
    $lines.Add("")
    $lines.Add("The `total` column is the sum of displayed character/token values across analyzed commits for that row. Directory rows end with `/` and use recursive totals.")
    $lines.Add("")

    $allRows = [System.Collections.Generic.HashSet[string]]::new()
    foreach ($data in $CommitData)
    {
        foreach ($file in $data.Files)
        {
            [void]$allRows.Add($file)
        }
        foreach ($directory in $data.DirMetrics.Keys)
        {
            [void]$allRows.Add("$directory/")
        }
    }

    $preferredDirectoryRows = @(
        ".agents/",
        ".agents/plans/",
        ".agents/archive/",
        ".agents/reports/",
        ".agents/references/",
        ".agents/templates/",
        ".agents/skills/",
        ".agents/skills/repo-task/",
        ".agents/skills/repo-task/references/",
        ".agents/skills/repo-task/references/tasks/"
    )
    foreach ($directory in $preferredDirectoryRows)
    {
        [void]$allRows.Add($directory)
    }

    $commitColumns = @($CommitData | ForEach-Object { "``$($_.Meta.Short)``" })
    $lines.Add("| path | " + ($commitColumns -join " | ") + " | total |")
    $lines.Add("| " + (@(1..($CommitData.Count + 2) | ForEach-Object { "---" }) -join " | ") + " |")

    $sortedRows = @($allRows | Sort-Object { ($_ -split "/").Count }, { $_ })
    foreach ($rowName in $sortedRows)
    {
        $cells = @()
        $rowTotal = New-Metrics
        $hasValue = $false

        foreach ($data in $CommitData)
        {
            $metric = Get-RowMetric -CommitData $data -RowName $rowName
            if ($null -eq $metric)
            {
                $cells += ""
                continue
            }

            $hasValue = $true
            $cells += Format-Metric -Metric $metric
            $rowTotal = Add-Metrics -Left $rowTotal -Right $metric
        }

        if ($hasValue)
        {
            $lines.Add("| ``$(Escape-MarkdownCell -Value $rowName)`` | " + ($cells -join " | ") + " | $(Format-Metric -Metric $rowTotal) |")
        }
    }

    return ($lines -join [Environment]::NewLine) + [Environment]::NewLine
}

if ($SelfTest)
{
    Invoke-GuardrailSelfTest
    return
}

if ($DefaultLoadWarningPercent -lt 0)
{
    throw "DefaultLoadWarningPercent must be zero or greater."
}

if ($TotalInventoryWarningPercent -lt 0)
{
    throw "TotalInventoryWarningPercent must be zero or greater."
}

$comparisonMode = Normalize-ComparisonMode -Mode $Mode
$repoRoot = Invoke-GitText -Arguments @("rev-parse", "--show-toplevel") -WorkingDirectory (Get-Location).Path
$repoName = Split-Path -Path $repoRoot -Leaf
$timestamp = Get-Date -Format "yyyy-MM-dd-HHmmss-fff"
$runId = [Guid]::NewGuid().ToString("N").Substring(0, 8)
$worktreeParent = Split-Path -Path $repoRoot -Parent
$worktreePath = Join-Path $worktreeParent "$repoName-context-report-$timestamp-$runId"
$suffix = 2
while (Test-Path -LiteralPath $worktreePath)
{
    $worktreePath = Join-Path $worktreeParent "$repoName-context-report-$timestamp-$suffix"
    $suffix++
}

if ([string]::IsNullOrWhiteSpace($OutputPath))
{
    $outputDirectory = Join-Path $repoRoot "temp"
    $OutputPath = Get-AvailableOutputPath -Path (Join-Path $outputDirectory "context-report-$timestamp-$runId.md")
}
else
{
    if (-not [System.IO.Path]::IsPathRooted($OutputPath))
    {
        $OutputPath = Join-Path $repoRoot $OutputPath
    }
}

$outputParent = Split-Path -Path $OutputPath -Parent
if (-not [string]::IsNullOrWhiteSpace($outputParent))
{
    New-Item -ItemType Directory -Path $outputParent -Force | Out-Null
}

$worktreeCreated = $false
try
{
    Invoke-GitText -Arguments @("worktree", "add", "--detach", $worktreePath, "HEAD") -WorkingDirectory $repoRoot | Out-Null
    $worktreeCreated = $true

    $selectedCommits = @(Resolve-ReportCommits -Range $Range -WorkingDirectory $worktreePath)
    $commitsToMeasure = if ($comparisonMode -eq "endpoint")
    {
        @($selectedCommits[0], $selectedCommits[-1])
    }
    else
    {
        $selectedCommits
    }

    $commitData = @()
    foreach ($commit in $commitsToMeasure)
    {
        $commitData += Measure-Commit -Commit $commit -WorkingDirectory $worktreePath
    }

    $changedFiles = @(Get-RangeChangedFiles -OldCommit $selectedCommits[0] -NewCommit $selectedCommits[-1] -WorkingDirectory $worktreePath)
    $guardrailEvaluation = Get-GuardrailEvaluation `
        -CommitData $commitData `
        -ChangedFiles $changedFiles `
        -DefaultThresholdPercent $DefaultLoadWarningPercent `
        -TotalThresholdPercent $TotalInventoryWarningPercent `
        -Rationale $GrowthRationale

    $report = Build-Report -CommitData $commitData -SelectedCommitCount $selectedCommits.Count -ComparisonMode $comparisonMode -GuardrailEvaluation $guardrailEvaluation
    Set-Content -LiteralPath $OutputPath -Value $report -Encoding utf8
    Write-Output "Context report written to $OutputPath"

    if ($guardrailEvaluation.Default.Status -eq "Warning")
    {
        Write-Warning "Default load grew by $([string]::Format($invariantCulture, '{0:N1}', $guardrailEvaluation.Default.DeltaPercent))%, above the $DefaultLoadWarningPercent% warning threshold."
    }
    if ($guardrailEvaluation.Total.Status -eq "Warning")
    {
        Write-Warning "Total AI inventory grew by $([string]::Format($invariantCulture, '{0:N1}', $guardrailEvaluation.Total.DeltaPercent))%, above the $TotalInventoryWarningPercent% warning threshold without an archive/report rationale."
    }
    if ($comparisonMode -eq "stepwise")
    {
        foreach ($crossing in $guardrailEvaluation.AdjacentCrossings)
        {
            Write-Warning "Adjacent context guardrail crossing: $($crossing.Previous.Meta.Short) -> $($crossing.Current.Meta.Short)."
        }
    }
}
finally
{
    if ($worktreeCreated)
    {
        try
        {
            Invoke-GitText -Arguments @("worktree", "remove", "--force", $worktreePath) -WorkingDirectory $repoRoot | Out-Null
        }
        catch
        {
            Write-Warning "Failed to remove temporary worktree '$worktreePath': $_"
        }
    }
}
