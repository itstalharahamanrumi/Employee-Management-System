@echo off
title Employee Management System
color 0A

echo.
echo  =====================================================
echo   Employee Management System
echo   Starting up...
echo  =====================================================
echo.

:: Paths
set BASE=%~dp0
set OUT=%BASE%out
set CP=%OUT%;%BASE%mysql-connector-java-8.0.28.jar;%BASE%jcalendar-tz-1.3.3-4.jar;%BASE%ResultSet2xml.jar

:: Step 1 — Compile all Java files
echo [1/3] Compiling source files...
for /r "%BASE%src" %%f in (*.java) do set JAVA_FILES=!JAVA_FILES! "%%f"

setlocal enabledelayedexpansion
set JAVA_FILES=
for /r "%BASE%src\employee\management\system" %%f in (*.java) do (
    set JAVA_FILES=!JAVA_FILES! "%%f"
)

if not exist "%OUT%" mkdir "%OUT%"
javac -cp "%BASE%src;%BASE%mysql-connector-java-8.0.28.jar;%BASE%jcalendar-tz-1.3.3-4.jar;%BASE%ResultSet2xml.jar" -d "%OUT%" !JAVA_FILES! 2>nul

if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed. Make sure Java JDK is installed.
    pause
    exit /b 1
)
echo [1/3] Compile OK

:: Step 2 — Copy icons to output
echo [2/3] Copying resources...
if not exist "%OUT%\icons" mkdir "%OUT%\icons"
xcopy /s /q /y "%BASE%src\icons\*" "%OUT%\icons\" >nul
echo [2/3] Resources OK

:: Step 3 — Run
echo [3/3] Launching application...
echo.
start "" java -cp "%CP%" employee.management.system.Splash
echo  App started! Login with:  admin / admin
echo.
