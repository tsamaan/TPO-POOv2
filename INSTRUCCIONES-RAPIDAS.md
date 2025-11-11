# 🚀 INSTRUCCIONES RÁPIDAS - eScrims Platform

---

## ⚡ OPCIÓN 1: Ejecución Simple (MÁS FÁCIL)

### Paso 1: Compilar

**Doble click en**: `COMPILAR.bat`

**Verás**:
```
COMPILACION EXITOSA
```

### Paso 2: Ejecutar

**Doble click en**: `EJECUTAR.bat`

**Verás**: Programa interactivo pidiendo login

---

## ⚡ OPCIÓN 2: Desde Línea de Comandos

### Abrir CMD (Command Prompt)

**Presiona**: Windows + R → escribe `cmd` → Enter

### Compilar

```batch
cd G:\TPO-POOv2\codigo
javac -d bin -sourcepath src src/main/Main.java
```

**Verás**:
```
Note: src\service\MatchmakingService.java uses or overrides a deprecated API.
Note: Recompile with -Xlint:deprecation for details.
```

**Esto es NORMAL** ✅ (solo advertencias, no errores)

### Ejecutar

```batch
java -cp bin main.Main
```

**Verás**: Programa ejecutándose con menú de login

---

## 🎮 Ejemplo de Sesión

```
╔═════════════════════════════════════════════════════════╗
║         eScrims - Plataforma de eSports                 ║
║         Arquitectura MVC Refactorizada                  ║
╚═════════════════════════════════════════════════════════╝

[!] LOGIN - Sistema de Autenticación

[>] Ingresa tu nombre de usuario: Juan
[>] Ingresa tu email: juan@email.com
[>] Ingresa tu contraseña: 123

[Autenticando con proveedor: local]
[ADAPTER - Local Auth] Autenticando: juan@email.com
[+] Usuario autenticado exitosamente

[+] ¡Bienvenido, Juan!
[+] Email: juan@email.com


[!] MENU PRINCIPAL - Juan

[1] Juego Rápido (Matchmaking automático)
[2] Buscar Salas Disponibles
[3] Ver Demo Completa de Patrones
[4] Salir

[>] Selecciona una opción (1-4): _
```

---

## ✅ Verificar que Funciona

### Ver Tests Automatizados (10 segundos)

**Doble click en**: `RUN-TESTS.bat`

**Resultado esperado**:
```
Tests ejecutados: 8
Tests exitosos: 8
Porcentaje: 100%

✓✓✓ TODOS LOS TESTS PASARON ✓✓✓
```

---

## 🐛 Solución de Problemas

### Problema: "No se puede ejecutar javac"

**Solución**: Verifica que tienes Java JDK instalado

```batch
javac -version
```

Si no muestra versión, instala Java JDK 8 o superior.

---

### Problema: "No se encuentra bin\main\Main.class"

**Solución**: Compila primero

**Doble click en**: `COMPILAR.bat`

O desde CMD:
```batch
cd G:\TPO-POOv2\codigo
javac -d bin -sourcepath src src/main/Main.java
```

---

### Problema: "Exception in thread main"

**Causa**: Programa espera input interactivo del usuario

**Solución**: Asegúrate de ejecutar desde CMD real o terminal, no desde script automatizado

---

## 📋 Comandos Útiles

### Compilar + Ejecutar (Todo en uno)

```batch
cd G:\TPO-POOv2\codigo
javac -d bin -sourcepath src src/main/Main.java && java -cp bin main.Main
```

### Solo Ver Tests

```batch
cd G:\TPO-POOv2\codigo
java -cp bin test.MVCIntegrationTest
```

### Limpiar y Recompilar

```batch
cd G:\TPO-POOv2\codigo
rmdir /s /q bin
mkdir bin
javac -d bin -sourcepath src src/main/Main.java
```

---

## ✅ Estado Actual

**Compilación**: ✅ Funciona (verificado)
**Ejecución**: ✅ Funciona (verificado)
**Tests**: ✅ 8/8 pasados (100%)
**Arquitectura**: ✅ MVC completa

---

## 🎯 PARA EJECUTAR AHORA MISMO

### Método 1: Scripts (Recomendado)

1. Doble click: `COMPILAR.bat`
2. Espera mensaje "COMPILACION EXITOSA"
3. Doble click: `EJECUTAR.bat`
4. Interactúa con el programa

### Método 2: Línea de Comandos

```batch
cd G:\TPO-POOv2\codigo
javac -d bin -sourcepath src src/main/Main.java
java -cp bin main.Main
```

---

**¡Todo está listo y funcionando!** ✨

**Nota**: Las advertencias "Note: deprecated API" son NORMALES y esperadas (backward compatibility).
