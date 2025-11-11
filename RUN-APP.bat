@echo off
chcp 65001 >nul
echo ========================================
echo  eScrims Platform - Ejecutar Programa
echo ========================================
echo.

cd codigo

echo [1/2] Compilando proyecto...
javac -d bin -sourcepath src src/main/Main.java 2>compile_errors.txt
if %errorlevel% neq 0 (
    echo ERROR: Compilacion fallida
    type compile_errors.txt
    del compile_errors.txt
    pause
    exit /b 1
)
del compile_errors.txt 2>nul
echo     Compilacion exitosa!
echo.

echo [2/2] Ejecutando programa principal...
echo.
echo ========================================
echo.

java -Dfile.encoding=UTF-8 -cp bin main.Main

echo.
echo ========================================
echo  Programa finalizado
echo ========================================
pause
