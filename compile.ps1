# Replaces fragile javac @sources.txt on paths with spaces + backslashes (OneDrive, etc.)
$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot

if (-not (Test-Path 'build\classes')) { New-Item -ItemType Directory -Path 'build\classes' | Out-Null }
if (-not (Test-Path 'dist')) { New-Item -ItemType Directory -Path 'dist' | Out-Null }

$cp = (Get-ChildItem -Path 'lib' -Filter '*.jar' -ErrorAction SilentlyContinue | ForEach-Object { $_.FullName }) -join ';'
if (-not $cp) { $cp = '.' }

$files = @(Get-ChildItem -Path 'src' -Filter '*.java' -Recurse | ForEach-Object { $_.FullName })
if ($files.Count -eq 0) {
    Write-Error 'No .java files under src'
    exit 1
}

$javac = 'javac'
& $javac -encoding UTF-8 -d (Join-Path $PSScriptRoot 'build\classes') -cp $cp @files
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Push-Location (Join-Path $PSScriptRoot 'build\classes')
try {
    $manifest = Join-Path $PSScriptRoot 'manifest.txt'
    $jarOut = Join-Path $PSScriptRoot 'dist\HospitalManagementSystem.jar'
    & jar cvfm $jarOut $manifest com\
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
} finally {
    Pop-Location
}

exit 0
