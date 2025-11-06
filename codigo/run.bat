@echo off
REM Script para compilar y ejecutar el proyecto eScrims

echo ========================================
echo Compilando proyecto eScrims...
echo ========================================

if not exist "bin" mkdir bin

javac -d bin -sourcepath src src\main\Main.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Compilacion exitosa!
    echo Ejecutando Main...
    echo ========================================
    echo.
    java -cp bin main.Main
) else (
    echo.
    echo Error en la compilacion!
    exit /b 1
)
