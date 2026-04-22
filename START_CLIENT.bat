@echo off
title Hospital Management System - Client
color 0B
cd /d "%~dp0"
echo ============================================
echo   HOSPITAL MANAGEMENT SYSTEM - CLIENT
echo ============================================
echo.
echo Rebuilding JAR from current source...
call dev\COMPILE.bat nopause
if errorlevel 1 (
    echo.
    echo Build failed. Fix compile errors above, then run this script again.
    pause
    exit /b 1
)
echo.
echo Starting application...
echo.
echo Login Credentials:
echo Username: admin
echo Password: admin123
echo.
java -cp "dist/HospitalManagementSystem.jar;lib/*" com.hospital.gui.LoginFrame
REM Window will auto-close when application exits
exit
