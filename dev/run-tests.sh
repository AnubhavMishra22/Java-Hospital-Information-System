#!/bin/bash
# Run all unit tests for Hospital Management System (script in dev/; runs from repo root)

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$ROOT"

echo "========================================"
echo "Hospital Management System - Unit Tests"
echo "========================================"

# Check if JUnit JAR exists
if [ ! -f "lib/junit-4.13.2.jar" ]; then
    echo "❌ JUnit library not found!"
    echo "Downloading JUnit 4.13.2..."
    mkdir -p lib
    wget -q -O lib/junit-4.13.2.jar https://repo1.maven.org/maven2/junit/junit/4.13.2/junit-4.13.2.jar
    wget -q -O lib/hamcrest-core-1.3.jar https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar
    echo "✅ JUnit libraries downloaded"
fi

# Create test build directory
mkdir -p build/test-classes

# Compile source code first
echo "Compiling source code..."
javac -d build/classes -cp "lib/*" $(find src -name "*.java") 2>&1 | head -10

if [ ${PIPESTATUS[0]} -ne 0 ]; then
    echo "❌ Source compilation failed!"
    exit 1
fi

echo "✅ Source code compiled"

# Compile test code
echo "Compiling test code..."
javac -d build/test-classes -cp "lib/*:build/classes" $(find test -name "*.java") 2>&1 | head -10

if [ ${PIPESTATUS[0]} -ne 0 ]; then
    echo "❌ Test compilation failed!"
    exit 1
fi

echo "✅ Test code compiled"

# Run tests
echo ""
echo "Running tests..."
echo ""
java -cp "build/test-classes:build/classes:lib/*" com.hospital.TestRunner

exit $?
