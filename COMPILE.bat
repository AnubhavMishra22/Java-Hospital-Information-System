@echo off
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

echo Compiling Java source files...
echo.

REM Find all Java files and compile them with proper path handling
dir /s /b src\*.java > sources_temp.txt

REM Quote each path in sources.txt to handle spaces
echo. > sources.txt
for /f "delims=" %%i in (sources_temp.txt) do echo "%%i" >> sources.txt

javac -d build\classes -cp "lib\*" @sources.txt

REM Clean up temporary files
del sources_temp.txt sources.txt

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Compilation successful!
    echo.

    REM Create JAR file
    echo Creating JAR file...
    cd build\classes
    jar cvfm ..\..\dist\HospitalManagementSystem.jar ..\..\manifest.txt com\
    cd ..\..

    echo.
    echo ================================
    echo Build completed successfully!
    echo JAR file created: dist\HospitalManagementSystem.jar
    echo ================================
    echo.
    echo You can now run the application!
    echo.
    pause
) else (
    echo.
    echo ================================
    echo Compilation FAILED!
    echo Check the errors above.
    echo ================================
    echo.
    pause
)
