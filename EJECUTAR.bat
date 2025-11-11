@echo off
chcp 65001 >nul 2>&1
cls
echo.
echo ========================================
echo   eScrims Platform v2.0-MVC
echo ========================================
echo.

cd codigo

if not exist bin\main\Main.class (
    echo ERROR: Proyecto no compilado
    echo.
    echo Por favor ejecuta COMPILAR.bat primero
    echo.
    pause
    exit /b 1
)

echo Ejecutando programa...
echo.
echo ========================================
echo.

java -Dfile.encoding=UTF-8 -cp bin main.Main

echo.
echo ========================================
echo   Programa finalizado
echo ========================================
echo.
pause
