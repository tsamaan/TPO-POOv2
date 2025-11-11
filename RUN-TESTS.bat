@echo off
echo ========================================
echo  EJECUTANDO TESTS - eScrims Platform
echo ========================================
echo.

cd codigo

echo [1/2] Compilando proyecto...
javac -d bin -sourcepath src src/test/MVCIntegrationTest.java
if %errorlevel% neq 0 (
    echo ERROR: Compilacion fallida
    pause
    exit /b 1
)
echo     Compilacion exitosa!
echo.

echo [2/2] Ejecutando tests automatizados...
echo.
java -cp bin test.MVCIntegrationTest

echo.
echo ========================================
echo  TESTS COMPLETADOS
echo ========================================
pause
