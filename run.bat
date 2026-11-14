@echo off
REM ==============================================================================
REM Library Management System — Build & Run Script (Windows)
REM ==============================================================================

setlocal enabledelayedexpansion

cd /d "%~dp0"

echo ==================================================
echo   BookSphere — Library Management System
echo ==================================================

REM Check Java installation
where javac >nul 2>nul
if %errorlevel% neq 0 (
    echo Error: Java (JDK 17+) is required but not found in PATH.
    pause
    exit /b 1
)

if not exist out mkdir out

echo [1/2] Compiling Java source files...
dir /s /b src\*.java > sources.txt
javac -cp "lib/*" -d out @sources.txt
del sources.txt

if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)
echo [OK] Compilation successful.

if "%1"=="demo" (
    echo [2/2] Running Standalone OOP Demo (No Database Required)...
    echo.
    java -cp "out;lib/*" com.library.demo.OopDemo
    goto end
)

echo [2/2] Starting Library Server...
echo Server running at http://localhost:8080
echo Press Ctrl+C to stop.
echo.

java -cp "out;lib/*" com.library.Main

:end
