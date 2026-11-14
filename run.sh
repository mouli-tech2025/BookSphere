#!/usr/bin/env bash
# ==============================================================================
# Library Management System — Build & Run Script (Linux / macOS)
# ==============================================================================

set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR"

echo "=================================================="
echo "  LIBRO — Library Management System"
echo "=================================================="

# Check Java installation
if ! command -v javac &> /dev/null || ! command -v java &> /dev/null; then
    echo "Error: Java (JDK 17+) is required but not found in PATH."
    exit 1
fi

# Clean & create output directory
mkdir -p out

echo "[1/2] Compiling Java source files..."
find src -name "*.java" | xargs javac -cp "lib/*" -d out

if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi
echo "✓ Compilation successful."

# Check if user requested OOP Demo instead of full server
if [ "$1" == "demo" ]; then
    echo "[2/2] Running Standalone OOP Demo (No Database Required)..."
    echo ""
    java -cp "out:lib/*" com.library.demo.OopDemo
    exit 0
fi

echo "[2/2] Starting Library Server..."
echo "Server running at http://localhost:8080"
echo "Press Ctrl+C to stop."
echo ""

java -cp "out:lib/*" com.library.Main
