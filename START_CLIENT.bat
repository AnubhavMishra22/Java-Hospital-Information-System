@echo off
title Hospital Management System - Client
color 0B
echo ============================================
echo   HOSPITAL MANAGEMENT SYSTEM - CLIENT
echo ============================================
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
