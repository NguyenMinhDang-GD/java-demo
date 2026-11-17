@echo off
cd /d "%~dp0"
if not exist out mkdir out
javac -encoding UTF-8 -d out src\vetcare\model\*.java src\vetcare\service\*.java src\vetcare\ui\*.java src\vetcare\TestRunner.java
if %errorlevel% neq 0 (
  echo Compilation failed!
  pause
  exit /b %errorlevel%
)
start "" javaw -cp out vetcare.ui.VetCareSwingApp
