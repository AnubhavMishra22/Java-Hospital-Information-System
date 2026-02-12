@echo off
title Compiling Hospital Management System
color 0E
echo ================================
echo Hospital Management System
echo Simple Compilation Script
echo ================================
echo.

REM Create directories
if not exist build\classes mkdir build\classes
if not exist dist mkdir dist

echo Compiling Java source files...
echo This may take a minute...
echo.

REM Compile directly without using sources file
REM Note: Package paths are hardcoded. If new packages are added, update this script.
javac -encoding UTF-8 -d build\classes -cp "lib\*" ^
  src\com\hospital\model\*.java ^
  src\com\hospital\database\*.java ^
  src\com\hospital\client\*.java ^
  src\com\hospital\server\*.java ^
  src\com\hospital\gui\*.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ================================
    echo Compilation successful!
    echo ================================
    echo.

    echo Creating JAR file...
    cd build\classes
    jar cvfm ..\..\dist\HospitalManagementSystem.jar ..\..\manifest.txt com\ >nul 2>&1
    cd ..\..

    echo.
    echo ================================
    echo Build completed successfully!
    echo JAR file: dist\HospitalManagementSystem.jar
    echo ================================
    echo.
    echo You can now run RUN_HOSPITAL_SYSTEM.bat
    echo.
) else (
    echo.
    echo ================================
    echo Compilation FAILED!
    echo ================================
    echo.
    echo Common fixes:
    echo 1. Make sure JDK is installed
    echo 2. Check that lib folder has MySQL connector
    echo 3. Verify all source files are present
    echo.
)

pause
