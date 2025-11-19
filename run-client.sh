#!/bin/bash
# Run Hospital Management System Client

echo "================================"
echo "Starting Hospital Management System"
echo "================================"

# Check if JAR exists
if [ -f "dist/HospitalManagementSystem.jar" ]; then
    java -cp "dist/HospitalManagementSystem.jar:lib/*" com.hospital.gui.LoginFrame
else
    # Compile if JAR doesn't exist
    echo "JAR not found. Compiling first..."
    ./compile.sh
    if [ $? -eq 0 ]; then
        java -cp "dist/HospitalManagementSystem.jar:lib/*" com.hospital.gui.LoginFrame
    fi
fi
