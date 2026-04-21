#!/bin/bash
# Compile script for Hospital Management System (lives in dev/; always builds from repo root)

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$ROOT"

echo "================================"
echo "Hospital Management System"
echo "Compilation Script"
echo "================================"

# Create directories
mkdir -p build/classes
mkdir -p dist

# Compile Java files
echo "Compiling Java source files..."
javac -d build/classes -cp "lib/*" $(find src -name "*.java")

if [ $? -eq 0 ]; then
    echo "Compilation successful!"

    # Create JAR file
    echo "Creating JAR file..."
    cd build/classes
    jar cvfm ../../dist/HospitalManagementSystem.jar ../../dev/manifest.txt com/
    cd ../..

    echo "================================"
    echo "Build completed successfully!"
    echo "JAR file created: dist/HospitalManagementSystem.jar"
    echo "================================"
else
    echo "Compilation failed!"
    exit 1
fi
