#!/bin/bash
# Run Hospital Management System Server

echo "================================"
echo "Starting Hospital Server"
echo "================================"

# Check if JAR exists
if [ -f "dist/HospitalManagementSystem.jar" ]; then
    java -cp "dist/HospitalManagementSystem.jar:lib/*" com.hospital.server.HospitalServer
else
    # Compile if JAR doesn't exist
    echo "JAR not found. Compiling first..."
    ./compile.sh
    if [ $? -eq 0 ]; then
        java -cp "dist/HospitalManagementSystem.jar:lib/*" com.hospital.server.HospitalServer
    fi
fi
