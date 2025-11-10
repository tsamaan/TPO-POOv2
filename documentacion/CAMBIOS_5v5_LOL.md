# ✅ Cambios Implementados: League of Legends 5v5

## 🎮 Resumen de Cambios

El sistema ha sido actualizado para simular **League of Legends** con formato **5v5** (5 jugadores por equipo).

---

## 📋 Cambios Realizados

### 1. **Número de Jugadores: 8 → 10**
- **Antes:** 8 jugadores (4v4)
- **Ahora:** 10 jugadores (5v5)

### 2. **Roles de League of Legends**
- **Antes:** Top, Support, ADC, Jungla, Mid (nombres genéricos)
- **Ahora:** Top, Jungle, Mid, ADC, Support (roles oficiales de LoL)

```java
private static final String[] ROLES = {
    "Top", "Jungle", "Mid", "ADC", "Support"
};
```

### 3. **Sistema de Balance de Roles**
Cada equipo debe tener exactamente:
- ✅ 1 Top
- ✅ 1 Jungle
- ✅ 1 Mid
- ✅ 1 ADC
- ✅ 1 Support

El sistema asigna automáticamente los roles para mantener el balance perfecto 5v5.

### 4. **Formación de Equipos**
- **Team Azure:** 5 jugadores
- **Team Crimson:** 5 jugadores

```java
// Asignar jugadores a equipos (5 por equipo)
for (int i = 0; i < jugadoresEncontrados.size(); i++) {
    if (i < 5) {
        equipoAzul.asignarJugador(jugadoresEncontrados.get(i));
    } else {
        equipoRojo.asignarJugador(jugadoresEncontrados.get(i));
    }
}
```

### 5. **Duración de Partida**
- **Antes:** 30-90 minutos (genérico)
- **Ahora:** 25-45 minutos (duración estándar de LoL)

### 6. **Mensajes del Sistema**
Todos los mensajes han sido actualizados:
- ✅ "se necesitan 10 jugadores en total para 5v5"
- ✅ "[X/10] Jugador encontrado"
- ✅ Resumen de roles muestra 5 roles de LoL

### 7. **Cálculo de Ganador**
Actualizado para considerar 5 jugadores por equipo:
```java
for (int i = 0; i < estadisticas.size(); i++) {
    if (i < 5) { // Primeros 5 jugadores son Team Azure
        killsAzul += estadisticas.get(i).getKills();
    } else { // Últimos 5 jugadores son Team Crimson
        killsRojo += estadisticas.get(i).getKills();
    }
}
```

---

## 🎯 Flujo del Juego (5v5 LoL)

### Paso 1: Login
Usuario ingresa sus credenciales

### Paso 2: Selección de Rol
```
[1] Top
[2] Jungle
[3] Mid
[4] ADC
[5] Support
```

### Paso 3: Búsqueda de Jugadores
```
[1/10] ProGamer123 (Support) - TÚ
[2/10] ShadowBlade (Top)
[3/10] PhoenixFire (Jungle)
[4/10] IceQueen (Mid)
[5/10] ThunderStrike (ADC)
[6/10] NightHawk (Support)
[7/10] DragonSlayer (Top)
[8/10] SilentAssassin (Jungle)
[9/10] MysticWizard (Mid)
[10/10] CyberNinja (ADC)
```

### Paso 4: Formación de Equipos
```
╔═══════════════════════════════════════════════════╗
║           EQUIPOS FORMADOS (5v5)                  ║
╠═══════════════════════════════════════════════════╣
║  Team Azure                                       ║
║  ───────────────────────────────────────────────  ║
║  * ProGamer123        Support                     ║
║    ShadowBlade        Top                         ║
║    PhoenixFire        Jungle                      ║
║    IceQueen           Mid                         ║
║    ThunderStrike      ADC                         ║
╠═══════════════════════════════════════════════════╣
║  Team Crimson                                     ║
║  ───────────────────────────────────────────────  ║
║    NightHawk          Support                     ║
║    DragonSlayer       Top                         ║
║    SilentAssassin     Jungle                      ║
║    MysticWizard       Mid                         ║
║    CyberNinja         ADC                         ║
╚═══════════════════════════════════════════════════╝
```

### Paso 5: Confirmación
Todos los 10 jugadores confirman su participación

### Paso 6: Partida en Curso
```
[*] La partida está en curso...
[*] Duración estimada: 25-45 minutos (partida estándar de LoL)
```

### Paso 7: Estadísticas Post-Partida
Tabla con K/D/A de los 10 jugadores:
- MVP identificado
- Ganador determinado por total de kills

---

## 🏆 Ejemplo de Resultado Final

```
╔═══════════════════╦═══════╦═══════╦═══════╦════════════╗
║ Jugador           ║ Kills ║ Death ║ Asist ║ KDA Ratio  ║
╠═══════════════════╬═══════╬═══════╬═══════╬════════════╣
║ ProGamer123       ║    18 ║    12 ║    15 ║       2.75 ║  <- Support (Azure)
║ ShadowBlade       ║    14 ║    10 ║    12 ║       2.60 ║  <- Top (Azure)
║ PhoenixFire       ║    10 ║    15 ║     8 ║       1.20 ║  <- Jungle (Azure)
║ IceQueen          ║    22 ║    10 ║    15 ║       3.70 ║  <- Mid (Azure)
║ ThunderStrike     ║    16 ║    14 ║    20 ║       2.57 ║  <- ADC (Azure)
║ NightHawk         ║     8 ║    16 ║     6 ║       0.87 ║  <- Support (Crimson)
║ DragonSlayer      ║    12 ║    13 ║    11 ║       1.77 ║  <- Top (Crimson)
║ SilentAssassin    ║    11 ║    14 ║     8 ║       1.36 ║  <- Jungle (Crimson)
║ MysticWizard      ║    15 ║    12 ║    10 ║       2.08 ║  <- Mid (Crimson)
║ CyberNinja        ║    13 ║    11 ║     9 ║       2.00 ║  <- ADC (Crimson)
╚═══════════════════╩═══════╩═══════╩═══════╩════════════╝

[★] MVP: IceQueen (Mid)
    EXCELENTE (KDA: 3.70 | K/D/A: 22/10/15)

[!] RESULTADO FINAL:
    Team Azure: 80 kills
    Team Crimson: 59 kills

[★] GANADOR: Team Azure
```

---

## ✅ Validaciones del Sistema

### Balance de Roles Automático
El sistema garantiza que cada equipo tenga exactamente:
- 1 Top laner
- 1 Jungler  
- 1 Mid laner
- 1 ADC
- 1 Support

Si un rol ya tiene 2 jugadores (1 por equipo), el sistema asigna automáticamente otro rol disponible.

### Ejemplo de Asignación Balanceada
```
Jugador 1: Top     → Team Azure Top
Jugador 2: Jungle  → Team Azure Jungle
Jugador 3: Mid     → Team Azure Mid
Jugador 4: ADC     → Team Azure ADC
Jugador 5: Support → Team Azure Support
Jugador 6: Top     → Team Crimson Top
Jugador 7: Jungle  → Team Crimson Jungle
Jugador 8: Mid     → Team Crimson Mid
Jugador 9: ADC     → Team Crimson ADC
Jugador 10: Support → Team Crimson Support
```

---

## 🔧 Archivos Modificados

### `src/main/Main.java`
- Línea 26-27: Array de BOT_NAMES actualizado (10 nombres)
- Línea 30-32: Array de ROLES actualizado con roles de LoL
- Línea 194: Cambio de 8 a 10 jugadores necesarios
- Línea 196: Comentario explicativo "5v5 como League of Legends"
- Línea 213: Cambio de mensajes "[X/8]" a "[X/10]"
- Línea 252-257: Asignación de equipos actualizada (5 por equipo)
- Línea 330-356: Función `asignarRolBalanceado()` actualizada con comentarios de LoL
- Línea 492: Duración actualizada a "25-45 minutos (partida estándar de LoL)"
- Línea 526-532: Cálculo de ganador actualizado para 5 jugadores por equipo

---

## 🚀 Cómo Ejecutar

### Compilar:
```bash
cd src
javac -encoding UTF-8 models/*.java states/*.java strategies/*.java notifiers/*.java auth/*.java service/*.java context/*.java interfaces/*.java main/*.java
```

### Ejecutar:
```bash
java main.Main
```

### O usar el script:
```bash
.\run.bat
```

---

## 📊 Impacto en el TP

Este cambio mejora el cumplimiento del TP porque:

1. ✅ **RF11: Multijuego** - Ahora simula específicamente League of Legends
2. ✅ **Modelo de Dominio** - Formato variable (antes 4v4, ahora 5v5)
3. ✅ **Realismo** - Roles y mecánicas auténticas de LoL
4. ✅ **Validaciones** - Sistema de balance de roles más robusto

---

**Fecha de cambios:** 2025-01-10  
**Versión:** 2.0 (League of Legends 5v5)  
**Estado:** ✅ Implementado y testeado
