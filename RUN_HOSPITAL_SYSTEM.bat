@echo off
cd /d "%~dp0"
title Hospital Management System
color 0E
echo ============================================
echo    HOSPITAL MANAGEMENT SYSTEM
echo    Complete Startup
echo ============================================
echo.
echo This will start both SERVER and CLIENT
echo.
echo Step 1: Starting Server...
start "Hospital Server" START_SERVER.bat
timeout /t 3 /nobreak >nul
echo.
echo Step 2: Starting Client...
timeout /t 2 /nobreak >nul
start "Hospital Client" START_CLIENT.bat
echo.
echo ============================================
echo Both components started!
echo ============================================
echo.
echo Login with: admin / admin123
echo.
echo Keep the Server window open while using the application!
echo This launcher window will close automatically in 5 seconds...
echo.
timeout /t 5 /nobreak >nul
echo Closing launcher window...
exit
