param(
    [switch]$RequiredOnly,
    [switch]$Strict,
    [switch]$Json
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$repoRoot = [System.IO.Path]::GetFullPath((Join-Path $PSScriptRoot ".."))
$results = [System.Collections.Generic.List[object]]::new()

function Convert-OutputToText
{
    param(
        [object[]]$Output
    )

    $text = (($Output | ForEach-Object { $_.ToString() }) -join " ") -replace "\s+", " "
    return $text.Trim()
}

function Format-OutputDetail
{
    param(
        [object[]]$Output
    )

    $text = Convert-OutputToText -Output $Output
    $text = $text.Trim()
    if ($text.Length -gt 220)
    {
        return "$($text.Substring(0, 217))..."
    }

    return $text
}

function Add-CheckResult
{
    param(
        [Parameter(Mandatory = $true)]
        [string]$Category,

        [Parameter(Mandatory = $true)]
        [string]$Tool,

        [Parameter(Mandatory = $true)]
        [string]$Status,

        [Parameter(Mandatory = $true)]
        [string]$Detail,

        [bool]$Required = $false
    )

    [void]$script:results.Add([pscustomobject]@{
        Category = $Category
        Tool = $Tool
        Status = $Status
        Detail = $Detail
        Required = $Required
    })
}

function Invoke-Probe
{
    param(
        [Parameter(Mandatory = $true)]
        [scriptblock]$Probe
    )

    try
    {
        $global:LASTEXITCODE = 0
        $output = @(& $Probe 2>&1 | Select-Object -First 30)
        $exitCode = $global:LASTEXITCODE
        if ($null -eq $exitCode)
        {
            $exitCode = 0
        }

        return [pscustomobject]@{
            ExitCode = $exitCode
            RawDetail = Convert-OutputToText -Output $output
            Detail = Format-OutputDetail -Output $output
        }
    }
    catch
    {
        return [pscustomobject]@{
            ExitCode = 1
            RawDetail = $_.Exception.Message
            Detail = $_.Exception.Message
        }
    }
}

function Add-FileCheck
{
    param(
        [Parameter(Mandatory = $true)]
        [string]$Category,

        [Parameter(Mandatory = $true)]
        [string]$Tool,

        [Parameter(Mandatory = $true)]
        [string]$Path,

        [bool]$Required = $false
    )

    if (Test-Path -LiteralPath $Path -PathType Leaf)
    {
        Add-CheckResult -Category $Category -Tool $Tool -Status "ok" -Detail "found $Path" -Required $Required
    }
    else
    {
        Add-CheckResult -Category $Category -Tool $Tool -Status "missing" -Detail "missing $Path" -Required $Required
    }
}

function Add-CommandCheck
{
    param(
        [Parameter(Mandatory = $true)]
        [string]$Category,

        [Parameter(Mandatory = $true)]
        [string]$Tool,

        [Parameter(Mandatory = $true)]
        [string]$Command,

        [Parameter(Mandatory = $true)]
        [scriptblock]$Probe,

        [string]$ExpectedPattern = "",

        [string]$ExpectedDescription = "",

        [bool]$Required = $false,

        [switch]$WarnOnly
    )

    $commandInfo = Get-Command $Command -ErrorAction SilentlyContinue | Select-Object -First 1
    if (-not $commandInfo)
    {
        Add-CheckResult `
            -Category $Category `
            -Tool $Tool `
            -Status "missing" `
            -Detail "command '$Command' not found on PATH" `
            -Required $Required
        return
    }

    $probeResult = Invoke-Probe -Probe $Probe
    $status = if ($probeResult.ExitCode -eq 0) { "ok" } else { "error" }
    $detail = if ([string]::IsNullOrWhiteSpace($probeResult.Detail)) { "command completed" } else { $probeResult.Detail }

    if ($probeResult.ExitCode -ne 0)
    {
        $detail = "exit $($probeResult.ExitCode): $detail"
    }
    elseif (-not [string]::IsNullOrWhiteSpace($ExpectedPattern) -and $probeResult.RawDetail -notmatch $ExpectedPattern)
    {
        $status = if ($WarnOnly) { "warn" } else { "error" }
        $expected = if ([string]::IsNullOrWhiteSpace($ExpectedDescription)) { $ExpectedPattern } else { $ExpectedDescription }
        $detail = "expected $expected; got $detail"
    }

    Add-CheckResult -Category $Category -Tool $Tool -Status $status -Detail $detail -Required $Required
}

function Add-DockerComposeCheck
{
    $docker = Get-Command "docker" -ErrorAction SilentlyContinue | Select-Object -First 1
    $legacyCompose = Get-Command "docker-compose" -ErrorAction SilentlyContinue | Select-Object -First 1

    if ($docker)
    {
        $pluginResult = Invoke-Probe -Probe { docker compose version }
        if ($pluginResult.ExitCode -eq 0)
        {
            Add-CheckResult `
                -Category "conditional" `
                -Tool "Docker Compose" `
                -Status "ok" `
                -Detail $pluginResult.Detail
            return
        }
    }

    if ($legacyCompose)
    {
        $legacyResult = Invoke-Probe -Probe { docker-compose version }
        if ($legacyResult.ExitCode -eq 0)
        {
            Add-CheckResult `
                -Category "conditional" `
                -Tool "Docker Compose" `
                -Status "ok" `
                -Detail $legacyResult.Detail
            return
        }

        Add-CheckResult `
            -Category "conditional" `
            -Tool "Docker Compose" `
            -Status "error" `
            -Detail "exit $($legacyResult.ExitCode): $($legacyResult.Detail)"
        return
    }

    Add-CheckResult `
        -Category "conditional" `
        -Tool "Docker Compose" `
        -Status "missing" `
        -Detail "neither 'docker compose' nor 'docker-compose' is available"
}

function Find-IntelliJIdea
{
    foreach ($commandName in @("idea64.exe", "idea.exe"))
    {
        $commandInfo = Get-Command $commandName -ErrorAction SilentlyContinue | Select-Object -First 1
        if ($commandInfo)
        {
            return "$($commandInfo.Source) (PATH)"
        }
    }

    $basePaths = [System.Collections.Generic.List[string]]::new()
    $localAppData = [Environment]::GetEnvironmentVariable("LOCALAPPDATA")
    $programFiles = [Environment]::GetEnvironmentVariable("ProgramFiles")
    $programFilesX86 = [Environment]::GetEnvironmentVariable("ProgramFiles(x86)")

    if (-not [string]::IsNullOrWhiteSpace($localAppData))
    {
        [void]$basePaths.Add((Join-Path $localAppData "Programs"))
    }
    if (-not [string]::IsNullOrWhiteSpace($programFiles))
    {
        [void]$basePaths.Add((Join-Path $programFiles "JetBrains"))
    }
    if (-not [string]::IsNullOrWhiteSpace($programFilesX86))
    {
        [void]$basePaths.Add((Join-Path $programFilesX86 "JetBrains"))
    }

    foreach ($basePath in $basePaths)
    {
        if (-not (Test-Path -LiteralPath $basePath -PathType Container))
        {
            continue
        }

        $ideaDirs = @(Get-ChildItem -LiteralPath $basePath -Directory -Filter "IntelliJ IDEA*" -ErrorAction SilentlyContinue)
        foreach ($ideaDir in $ideaDirs)
        {
            foreach ($fileName in @("idea64.exe", "idea.exe"))
            {
                $candidate = Join-Path $ideaDir.FullName "bin\$fileName"
                if (Test-Path -LiteralPath $candidate -PathType Leaf)
                {
                    return $candidate
                }
            }
        }
    }

    return $null
}

function Import-RepoDotEnv
{
    $dotEnvPath = Join-Path $repoRoot ".env"
    $loaderPath = Join-Path $repoRoot "scripts\load-dotenv.ps1"

    if (-not (Test-Path -LiteralPath $dotEnvPath -PathType Leaf))
    {
        Add-CheckResult `
            -Category "diagnostic" `
            -Tool "Repo .env" `
            -Status "warn" `
            -Detail "no .env file found; checks use the current process environment"
        return
    }

    if (-not (Test-Path -LiteralPath $loaderPath -PathType Leaf))
    {
        Add-CheckResult `
            -Category "diagnostic" `
            -Tool "Repo .env" `
            -Status "error" `
            -Detail "found .env but missing $loaderPath"
        return
    }

    try
    {
        . $loaderPath -Path $dotEnvPath -Quiet
        if (-not [string]::IsNullOrWhiteSpace($env:JAVA_HOME))
        {
            $javaBin = Join-Path $env:JAVA_HOME "bin"
            if (Test-Path -LiteralPath $javaBin -PathType Container)
            {
                $env:Path = "$javaBin;$env:Path"
            }
        }

        Add-CheckResult -Category "diagnostic" -Tool "Repo .env" -Status "ok" -Detail "loaded $dotEnvPath"
    }
    catch
    {
        Add-CheckResult -Category "diagnostic" -Tool "Repo .env" -Status "error" -Detail $_.Exception.Message
    }
}

function Add-PythonYamlCheck
{
    $python = Get-Command "python" -ErrorAction SilentlyContinue | Select-Object -First 1
    if (-not $python)
    {
        Add-CheckResult `
            -Category "recommended" `
            -Tool "Python PyYAML" `
            -Status "missing" `
            -Detail "python command unavailable"
        return
    }

    $probeResult = Invoke-Probe -Probe { python -c "import yaml; print(yaml.__version__)" }
    if ($probeResult.ExitCode -eq 0)
    {
        Add-CheckResult -Category "recommended" -Tool "Python PyYAML" -Status "ok" -Detail "PyYAML $($probeResult.Detail)"
    }
    else
    {
        Add-CheckResult `
            -Category "recommended" `
            -Tool "Python PyYAML" `
            -Status "missing" `
            -Detail $probeResult.Detail
    }
}

$buildWrapper = Join-Path $repoRoot "build.ps1"
$gradleWrapper = Join-Path $repoRoot "gradlew.bat"

Add-CommandCheck `
    -Category "diagnostic" `
    -Tool "Ambient Java" `
    -Command "java" `
    -Probe { java -version } `
    -ExpectedPattern 'version "25\.|openjdk version "25\.' `
    -ExpectedDescription "Java 25 on PATH" `
    -WarnOnly

Import-RepoDotEnv

Add-FileCheck -Category "required" -Tool "Gradle wrapper" -Path $gradleWrapper -Required $true
Add-FileCheck -Category "required" -Tool "Repo build wrapper" -Path $buildWrapper -Required $true
Add-CommandCheck -Category "required" -Tool "Git" -Command "git" -Probe { git --version } -Required $true
Add-CommandCheck `
    -Category "required" `
    -Tool "PowerShell 7+" `
    -Command "pwsh" `
    -Probe { pwsh --version } `
    -ExpectedPattern "PowerShell\s+7\." `
    -ExpectedDescription "PowerShell 7+" `
    -Required $true
Add-CommandCheck `
    -Category "required" `
    -Tool "Repo Java/Gradle wrapper" `
    -Command "pwsh" `
    -Probe { pwsh -NoProfile -File $buildWrapper --version } `
    -ExpectedPattern "Launcher JVM:\s+25\." `
    -ExpectedDescription "Gradle launched with Java 25" `
    -Required $true

if (-not $RequiredOnly)
{
    Add-CommandCheck -Category "conditional" -Tool "Docker CLI" -Command "docker" -Probe { docker --version }
    Add-CommandCheck `
        -Category "conditional" `
        -Tool "Docker daemon" `
        -Command "docker" `
        -Probe { docker version --format "{{.Server.Version}}" }
    Add-DockerComposeCheck

    Add-CommandCheck -Category "recommended" -Tool "ripgrep" -Command "rg" -Probe { rg --version }
    Add-CommandCheck -Category "recommended" -Tool "GitHub CLI" -Command "gh" -Probe { gh --version }
    Add-CommandCheck -Category "recommended" -Tool "Helm" -Command "helm" -Probe { helm version --short }
    Add-CommandCheck -Category "recommended" -Tool "kubectl" -Command "kubectl" -Probe { kubectl version --client }
    Add-CommandCheck -Category "recommended" -Tool "Cosign" -Command "cosign" -Probe { cosign version }
    Add-CommandCheck -Category "recommended" -Tool "Trivy" -Command "trivy" -Probe { trivy --version }
    Add-CommandCheck `
        -Category "recommended" `
        -Tool "IntelliJ HTTP Client CLI" `
        -Command "ijhttp" `
        -Probe { ijhttp --version }
    Add-CommandCheck -Category "recommended" -Tool "Python" -Command "python" -Probe { python --version }
    Add-PythonYamlCheck
    Add-CommandCheck -Category "recommended" -Tool "VS Code CLI" -Command "code" -Probe { code --version }

    $ideaPath = Find-IntelliJIdea
    if ($ideaPath)
    {
        Add-CheckResult -Category "recommended" -Tool "IntelliJ IDEA" -Status "ok" -Detail $ideaPath
    }
    else
    {
        Add-CheckResult `
            -Category "recommended" `
            -Tool "IntelliJ IDEA" `
            -Status "missing" `
            -Detail "idea64.exe/idea.exe not found on PATH or common install locations"
    }
}

$categoryOrder = @{
    required = 0
    conditional = 1
    recommended = 2
    diagnostic = 3
}
$orderedResults = @(
    $results | Sort-Object `
        @{ Expression = { if ($categoryOrder.ContainsKey($_.Category)) { $categoryOrder[$_.Category] } else { 99 } } },
        Tool
)
$blockingIssues = @($orderedResults | Where-Object { $_.Required -and $_.Status -in @("missing", "error") })
$strictIssues = @($orderedResults | Where-Object {
        $_.Category -ne "diagnostic" -and $_.Status -in @("missing", "error")
    })
$nonBlockingIssues = @($orderedResults | Where-Object {
        $_.Category -ne "diagnostic" -and -not $_.Required -and $_.Status -in @("missing", "error", "warn")
    })

if ($Json)
{
    [pscustomobject]@{
        ok = $blockingIssues.Count -eq 0
        strictOk = $strictIssues.Count -eq 0
        strict = [bool]$Strict
        requiredOnly = [bool]$RequiredOnly
        results = $orderedResults
    } | ConvertTo-Json -Depth 4
}
else
{
    $orderedResults |
        Select-Object Category, Tool, Status, Detail |
        Format-Table -AutoSize

    Write-Host ""
    if ($blockingIssues.Count -eq 0)
    {
        Write-Host "Required tool check passed."
    }
    else
    {
        Write-Host "Required tool check failed: $($blockingIssues.Count) blocking issue(s)."
    }

    if ($nonBlockingIssues.Count -gt 0)
    {
        if ($Strict)
        {
            Write-Host "Strict mode failed: $($nonBlockingIssues.Count) conditional or recommended issue(s)."
        }
        else
        {
            Write-Host "Non-blocking issues found: $($nonBlockingIssues.Count). Use -Strict to make these fail the script."
        }
    }
}

if ($blockingIssues.Count -gt 0 -or ($Strict -and $strictIssues.Count -gt 0))
{
    exit 1
}

exit 0
