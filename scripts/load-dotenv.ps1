param(
    [string]$Path = ".env",

    [switch]$Quiet,

    [switch]$PassThru
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function Convert-FromDotEnvWindowsPathEncoding {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Value
    )

    if ($Value -match '^[A-Za-z]:\\\\' -or $Value -match '^\\\\\\\\') {
        return $Value.Replace('\\', '\')
    }

    return $Value
}

if (-not (Test-Path -LiteralPath $Path)) {
    if ($Quiet) {
        return
    }
    throw "Could not find dotenv file '$Path'."
}

$loadedVariables = [System.Collections.Generic.List[string]]::new()

foreach ($line in Get-Content -LiteralPath $Path) {
    $trimmed = $line.Trim()
    if ([string]::IsNullOrWhiteSpace($trimmed) -or $trimmed.StartsWith("#")) {
        continue
    }

    if ($trimmed -notmatch '^(?:export\s+)?(?<name>[A-Za-z_][A-Za-z0-9_]*)=(?<value>.*)$') {
        throw "Unsupported dotenv line '$line' in '$Path'. Expected KEY=VALUE."
    }

    $name = $Matches.name
    $value = $Matches.value.Trim()

    if ($value.Length -ge 2) {
        $firstCharacter = $value.Substring(0, 1)
        $lastCharacter = $value.Substring($value.Length - 1, 1)
        if (($firstCharacter -eq '"' -and $lastCharacter -eq '"') -or
            ($firstCharacter -eq "'" -and $lastCharacter -eq "'")) {
            $value = $value.Substring(1, $value.Length - 2)
        }
    }

    $value = Convert-FromDotEnvWindowsPathEncoding -Value $value

    Set-Item -Path "Env:$name" -Value $value
    $loadedVariables.Add($name) | Out-Null
}

if ($PassThru) {
    $loadedVariables
}
