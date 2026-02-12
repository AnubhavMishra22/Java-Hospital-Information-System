@echo off
echo ========================================
echo Hospital Management System
echo Database Setup Script
echo ========================================
echo.
set /p MYSQL_PASSWORD="Enter your MySQL root password: "
echo.
echo Importing database schema...
mysql -u root -p%MYSQL_PASSWORD% < resources\sql\hospital_schema.sql
if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo SUCCESS! Database created successfully!
    echo ========================================
    echo.
    echo Database: hospital_management
    echo Tables created:
    echo   - users
    echo   - patients
    echo   - doctors
    echo   - departments
    echo   - appointments
    echo   - diagnoses
    echo   - prescriptions
    echo   - messages
    echo   - file_transfers
    echo   - billing
    echo.
) else (
    echo.
    echo ========================================
    echo ERROR: Failed to create database!
    echo ========================================
    echo.
    echo Please check:
    echo   1. MySQL password is correct
    echo   2. MySQL service is running
    echo.
)
pause
