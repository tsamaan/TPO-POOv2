# 🚀 Guía de Ejecución - eScrims Platform

**Versión**: 2.0-MVC
**Sistema**: Windows
**Java**: JDK 8+

---

## ⚡ Ejecución Rápida (Recomendado)

### Opción 1: Ejecutar Tests Automatizados

**Doble click en**: `RUN-TESTS.bat`

**Qué hace**:
1. Compila el proyecto completo
2. Ejecuta 8 tests automatizados
3. Muestra resultados

**Tiempo**: ~10 segundos

**Output esperado**:
```
✓ TEST 1: Creación de Views
✓ TEST 2: Métodos de ConsoleView
✓ TEST 3: Creación de Controllers
✓ TEST 4: Strategy Pattern corregido
✓ TEST 5: Filtrado por MMR
✓ TEST 6: Filtrado por Latencia
✓ TEST 7: Integración MVC
✓ TEST 8: Flujo completo

Tests ejecutados: 8
Tests exitosos: 8
Porcentaje: 100%

✓✓✓ TODOS LOS TESTS PASARON ✓✓✓
```

---

### Opción 2: Ejecutar Programa Interactivo

**Doble click en**: `RUN-APP.bat`

**Qué hace**:
1. Compila el proyecto
2. Ejecuta programa principal (Main.java)
3. Modo interactivo con menú

**Tiempo**: Interactivo (tú controlas)

**Flow del programa**:
```
1. Login
   → Ingresas usuario, email, password

2. Menú Principal
   [1] Juego Rápido (matchmaking automático)
   [2] Buscar Salas (navegación manual)
   [3] Demo de Patrones
   [4] Salir

3. Gameplay interactivo
```

---

## 📋 Ejecución Manual (Alternativa)

### Desde Línea de Comandos

```bash
# 1. Abrir CMD o PowerShell
# 2. Navegar al proyecto
cd G:\TPO-POOv2\codigo

# 3. Compilar
javac -d bin -sourcepath src src/main/Main.java

# 4a. Ejecutar programa principal
java -cp bin main.Main

# O 4b. Ejecutar tests
java -cp bin test.MVCIntegrationTest
```

---

## 🎮 Uso del Programa Interactivo

### Ejemplo de Sesión

```
╔═════════════════════════════════════════════════════════╗
║           eScrims - Plataforma de eSports               ║
║           Arquitectura MVC Refactorizada                ║
╚═════════════════════════════════════════════════════════╝

───────────────────────────────────────────────────────────
[!] LOGIN - Sistema de Autenticación
───────────────────────────────────────────────────────────

[>] Ingresa tu nombre de usuario: ProPlayer
[>] Ingresa tu email: pro@email.com
[>] Ingresa tu contraseña: password123

[+] ¡Bienvenido, ProPlayer!
[+] Email: pro@email.com

───────────────────────────────────────────────────────────
[!] MENU PRINCIPAL - ProPlayer
───────────────────────────────────────────────────────────

[1] Juego Rápido (Matchmaking automático)
[2] Buscar Salas Disponibles
[3] Ver Demo Completa de Patrones
[4] Salir

[>] Selecciona una opción (1-4): _
```

---

### Opción 1: Juego Rápido

**Qué hace**: Matchmaking automático basado en tu rango

**Flujo**:
```
1. Seleccionas juego (Valorant/LoL/CS:GO)
2. Configuras tu rango (0-3000)
3. Seleccionas tu rol (según el juego)
4. Sistema busca jugadores automáticamente
5. Forma equipos balanceados
6. Simula partida completa
7. Muestra estadísticas finales
```

**Tiempo**: ~30-60 segundos (automático con delays visuales)

---

### Opción 2: Buscar Salas

**Qué hace**: Navegas salas manualmente y valida tu rango

**Flujo**:
```
1. Seleccionas juego
2. Sistema muestra salas disponibles
   - Muestra si puedes unirte [✓] o no [✗] según tu rango
3. Intentas unirte a una sala
4. Si tu rango NO cumple → ACCESO DENEGADO
5. Si tu rango SÍ cumple → Te unes y juega
```

**Tiempo**: Interactivo (tú controlas)

**Ejemplo de validación**:
```
[1] ━━━━━━━━━━━━━━━━━━━━━━━
    Juego:      Valorant
    Rango:      1000 - 1800
    Estado:     [✓] Puedes unirte  ← Tu rango: 1500 ✅

[2] ━━━━━━━━━━━━━━━━━━━━━━━
    Juego:      Valorant
    Rango:      2000 - 3000
    Estado:     [✗] Rango incompatible  ← Tu rango: 1500 ❌
```

---

### Opción 3: Demo de Patrones

**Qué hace**: Muestra listado de los 9 patrones implementados

**Output**:
```
1. STATE PATTERN       - Estados del Scrim (6 estados)
2. STRATEGY PATTERN    - Algoritmos de Matchmaking (3 estrategias)
3. OBSERVER PATTERN    - Sistema de Notificaciones
4. ABSTRACT FACTORY    - Creación de Notifiers
5. COMMAND PATTERN     - Gestión de Roles (Undo/Redo)
6. CHAIN OF RESP.      - Moderación de Reportes
7. COMPOSITE PATTERN   - Grupos de Notificadores
8. TEMPLATE METHOD     - Validadores por Juego
9. ADAPTER PATTERN     - iCalendar + OAuth

Arquitectura: MVC con separación de capas
  → View Layer:       views/ (ConsoleView, MenuView, GameView)
  → Controller Layer: controllers/ (User, Scrim, Matchmaking)
  → Service Layer:    service/ (Matchmaking, Notification, Search)
  → Model Layer:      models/ (Usuario, Scrim, Equipo, etc.)
```

---

## 🎯 Comandos Directos

### Ejecutar Tests (Ver Validación)

```bash
# Opción A: Doble click
RUN-TESTS.bat

# Opción B: Desde CMD
cd codigo
java -cp bin test.MVCIntegrationTest
```

**Muestra**: 8 tests con validaciones de MVC y Strategy Pattern

---

### Ejecutar Programa (Jugar/Interactuar)

```bash
# Opción A: Doble click
RUN-APP.bat

# Opción B: Desde CMD
cd codigo
java -cp bin main.Main
```

**Muestra**: Programa interactivo con login y matchmaking

---

### Ejecutar Tests Originales (Opcionales)

```bash
cd codigo

# Test de State Pattern
java -cp bin test.ScrimStateTransitionsTest

# Test de Strategy Pattern
java -cp bin test.ByMMRStrategyTest

# Test de Factory Pattern
java -cp bin test.NotifierFactoryTest
```

---

## 🔍 Qué Observar en los Tests

### En MVCIntegrationTest

**TEST 4**: Verifica que Strategy retorna `List<Usuario>` (no void) ✅

**TEST 5**: Verifica filtrado REAL:
```
Candidatos: [1200, 1500, 1800, 500, 2500]
Rango scrim: 1200-1800
Resultado: Selecciona SOLO [1200, 1500, 1800] ✅
```

**TEST 8**: **CRÍTICO** - Verifica que Strategy NO modifica estado:
```
Estado antes de Strategy:  EstadoBuscandoJugadores
Strategy ejecuta selección
Estado después de Strategy: EstadoBuscandoJugadores ✅ (NO cambió)
```

---

## 🎮 Qué Observar en el Programa

### En Juego Rápido

**Arquitectura MVC en acción**:
```
1. MenuView.seleccionarJuego() ← VIEW captura input
   ↓
2. UserController.configurarRango() ← CONTROLLER valida
   ↓
3. ScrimController.crearScrimAutomatico() ← CONTROLLER crea
   ↓
4. MatchmakingService.ejecutar() ← SERVICE ejecuta lógica
   ↓
5. GameView.mostrarProgreso() ← VIEW presenta resultado
```

**Strategy Pattern**:
```
[STRATEGY - MMR] Filtrando jugadores por rango 1300-1700
[+] Jugador encontrado: Shadow42 (Rango: 1450)
[+] Jugador encontrado: Phoenix89 (Rango: 1520)
...
```

**State Pattern**:
```
[+] Estado: EstadoBuscandoJugadores
[+] Estado: EstadoLobbyCompleto
[+] Estado: EstadoConfirmado
[+] ¡Partida en curso! Estado: EstadoEnJuego
```

---

## 📝 Resumen de Comandos

| Qué Quieres | Comando | Tiempo |
|-------------|---------|--------|
| **Ver tests automatizados** | `RUN-TESTS.bat` | 10 seg |
| **Jugar/Interactuar** | `RUN-APP.bat` | Variable |
| **Solo compilar** | `cd codigo && javac -d bin -sourcepath src src/main/Main.java` | 5 seg |
| **Test State Pattern** | `cd codigo && java -cp bin test.ScrimStateTransitionsTest` | 5 seg |

---

## ✅ Resultados Esperados

### RUN-TESTS.bat

✅ **8/8 tests pasados**
✅ **Strategy Pattern: CORREGIDO**
✅ **MVC: FUNCIONAL**
✅ **Listo para entrega**

### RUN-APP.bat

✅ **Login funciona**
✅ **Juego rápido funciona**
✅ **Búsqueda de salas funciona**
✅ **Demo de patrones funciona**

---

## 🎯 Siguiente Paso

**Ejecuta ahora**:

1. **Para ver validación**: Doble click en `RUN-TESTS.bat`
2. **Para probar programa**: Doble click en `RUN-APP.bat`

**Todo está listo y funcionando** ✅

---

**¿Qué script ejecutar?**
- **RUN-TESTS.bat** → Ver que todo funciona (8 tests)
- **RUN-APP.bat** → Jugar e interactuar con el programa
