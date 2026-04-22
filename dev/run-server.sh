#!/bin/bash
# Run Hospital Management System Server (script in dev/; runs from repo root)

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$ROOT"

echo "================================"
echo "Starting Hospital Server"
echo "================================"

# Check if JAR exists
if [ -f "dist/HospitalManagementSystem.jar" ]; then
    java -cp "dist/HospitalManagementSystem.jar:lib/*" com.hospital.server.HospitalServer
else
    # Compile if JAR doesn't exist
    echo "JAR not found. Compiling first..."
    bash "$SCRIPT_DIR/compile.sh"
    if [ $? -eq 0 ]; then
        java -cp "dist/HospitalManagementSystem.jar:lib/*" com.hospital.server.HospitalServer
    fi
fi
