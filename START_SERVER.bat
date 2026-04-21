@echo off
cd /d "%~dp0"
title Hospital Management System - Server
color 0A
echo ============================================
echo    HOSPITAL MANAGEMENT SYSTEM - SERVER
echo ============================================
echo.
echo Starting server on port 8888...
echo Press Ctrl+C to stop the server
echo.
java -cp "dist/HospitalManagementSystem.jar;lib/*" com.hospital.server.HospitalServer
REM Window will auto-close when server stops
exit
