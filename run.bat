@echo off
setlocal enabledelayedexpansion

set BASE=%~dp0
set OUT=%BASE%out
set SRC=%BASE%src\employee\management\system
set CP_COMPILE=%BASE%src;%BASE%mysql-connector-java-8.0.28.jar;%BASE%jcalendar-tz-1.3.3-4.jar;%BASE%ResultSet2xml.jar
set CP_RUN=%OUT%;%BASE%mysql-connector-java-8.0.28.jar;%BASE%jcalendar-tz-1.3.3-4.jar;%BASE%ResultSet2xml.jar

:: Compile
if not exist "%OUT%" mkdir "%OUT%"
set JAVA_FILES=
for /r "%SRC%" %%f in (*.java) do set JAVA_FILES=!JAVA_FILES! "%%f"
javac -cp "%CP_COMPILE%" -d "%OUT%" !JAVA_FILES! 2>nul

:: Copy icons
if not exist "%OUT%\icons" mkdir "%OUT%\icons"
xcopy /s /q /y "%BASE%src\icons\*" "%OUT%\icons\" >nul

:: Launch with javaw — NO console window, single window only
start "" javaw -cp "%CP_RUN%" employee.management.system.Splash
