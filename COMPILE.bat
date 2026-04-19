@echo off
setlocal EnableDelayedExpansion
REM Pass "nopause" as first argument to skip pause at end (used by START_CLIENT.bat).
set "NOPAUSE="
if /i "%~1"=="nopause" set "NOPAUSE=1"

cd /d "%~dp0"
title Compiling Hospital Management System
color 0E
echo ================================
echo Hospital Management System
echo Compilation Script
echo ================================
echo.

REM Create directories
if not exist "build\classes" mkdir build\classes
if not exist "dist" mkdir dist

echo Compiling Java source files and creating JAR...
echo.

REM PowerShell avoids javac @argfile bugs with OneDrive paths (backslashes + spaces).
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0compile.ps1"
if errorlevel 1 goto :build_failed

echo.
echo Compilation successful.
echo.
echo ================================
echo Build completed successfully.
echo JAR file created: dist\HospitalManagementSystem.jar
echo ================================
echo.
echo You can now run the application.
echo.
if not defined NOPAUSE pause
endlocal
exit /b 0

:build_failed
echo.
echo ================================
echo Build FAILED (compile.ps1 / javac / jar).
echo Check the errors above.
echo ================================
echo.
if not defined NOPAUSE pause
endlocal
exit /b 1
