# Replaces fragile javac @sources.txt on paths with spaces + backslashes (OneDrive, etc.)
# Lives under dev/; builds against repo root (parent directory).
$ErrorActionPreference = 'Stop'
$ScriptDir = $PSScriptRoot
$Root = Split-Path $ScriptDir -Parent
Set-Location $Root

$buildClasses = Join-Path $Root 'build\classes'
$distDir = Join-Path $Root 'dist'
if (-not (Test-Path $buildClasses)) { New-Item -ItemType Directory -Path $buildClasses | Out-Null }
if (-not (Test-Path $distDir)) { New-Item -ItemType Directory -Path $distDir | Out-Null }

$libDir = Join-Path $Root 'lib'
$cp = (Get-ChildItem -Path $libDir -Filter '*.jar' -ErrorAction SilentlyContinue | ForEach-Object { $_.FullName }) -join ';'
if (-not $cp) { $cp = '.' }

$srcDir = Join-Path $Root 'src'
$files = @(Get-ChildItem -Path $srcDir -Filter '*.java' -Recurse | ForEach-Object { $_.FullName })
if ($files.Count -eq 0) {
    Write-Error 'No .java files under src'
    exit 1
}

$javac = 'javac'
& $javac -encoding UTF-8 -d $buildClasses -cp $cp @files
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Push-Location $buildClasses
try {
    $manifest = Join-Path $ScriptDir 'manifest.txt'
    $jarOut = Join-Path $Root 'dist\HospitalManagementSystem.jar'
    & jar cvfm $jarOut $manifest com\
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
} finally {
    Pop-Location
}

exit 0
