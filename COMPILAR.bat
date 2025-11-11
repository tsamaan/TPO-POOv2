@echo off
echo.
echo ========================================
echo   COMPILANDO eScrims Platform
echo ========================================
echo.

cd codigo

echo Limpiando directorio bin...
if exist bin rmdir /s /q bin
mkdir bin

echo.
echo Compilando todos los archivos Java...
javac -d bin -sourcepath src src/main/Main.java

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo   COMPILACION EXITOSA
    echo ========================================
    echo.
    echo Archivos compilados en: codigo\bin\
    echo.
    echo Para ejecutar:
    echo   - Doble click en EJECUTAR.bat
    echo   - O ejecuta: java -cp bin main.Main
    echo.
) else (
    echo.
    echo ========================================
    echo   ERROR EN COMPILACION
    echo ========================================
    echo.
    echo Revisa los errores arriba.
    echo.
)

pause
