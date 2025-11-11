# 📧 SISTEMA COMPLETO DE NOTIFICACIONES POR EMAIL

## 📨 ¿EN QUÉ MOMENTOS RECIBIRÁS UN EMAIL?

### **Durante el flujo de matchmaking y partida, recibirás emails en estos 5 momentos:**

---

### **1. 🔍 LOBBY COMPLETO** ✅

**Cuándo:**
- Cuando se encuentran los 10 jugadores (o el número según formato)
- Justo antes de que empiece la fase de confirmación

**Asunto del email:**
```
✅ Lobby Completo - 10/10 Jugadores
```

**Contenido:**
```
¡Lobby completo! 10/10 jugadores. El scrim está confirmado.

El matchmaking ha encontrado a todos los jugadores necesarios.
Por favor, confirma tu participación en los próximos segundos.

Juego: League of Legends
Formato: 5v5
```

**Código que lo envía:**
```java
// En EstadoLobbyCompleto.java
ctx.notificarATodos(Notificacion.TipoNotificacion.LOBBY_COMPLETO,
    "¡Lobby completo! 10/10 jugadores. El scrim está confirmado.");
```

---

### **2. ✅ PARTIDA CONFIRMADA** ✅

**Cuándo:**
- Después de que TÚ confirmes tu participación
- Solo si todos confirman (tú manualmente, bots automáticamente)

**Asunto del email:**
```
🎯 Scrim Confirmado - ¡A Jugar!
```

**Contenido:**
```
¡Scrim confirmado! Todos los jugadores están listos.

La partida comenzará en breve.
Prepárate para la batalla!

Equipo: Team Azure
Tu rol: Top
```

**Código que lo envía:**
```java
// En EstadoConfirmado.java (después de confirmaciones exitosas)
ctx.notificarATodos(Notificacion.TipoNotificacion.CONFIRMADO,
    "¡Scrim confirmado! Todos los jugadores están listos.");
```

---

### **3. ⚔️ PARTIDA INICIADA** ✅

**Cuándo:**
- Cuando la partida comienza (todos en juego)
- Inmediatamente después de la confirmación

**Asunto del email:**
```
⚔️ Partida Iniciada
```

**Contenido:**
```
¡La partida ha comenzado! Todos los jugadores en juego.

Buena suerte en la batalla!
Demuestra tus habilidades!

Juego: League of Legends
Modo: Ranked 5v5
```

**Código que lo envía:**
```java
// En EstadoEnJuego.java
ctx.notificarATodos(Notificacion.TipoNotificacion.EN_JUEGO,
    "¡La partida ha comenzado! Todos los jugadores en juego.");
```

---

### **4. 🏆 PARTIDA FINALIZADA CON ESTADÍSTICAS** ✅ **NUEVO**

**Cuándo:**
- Cuando la partida termina
- Este es el email MÁS IMPORTANTE con todas tus estadísticas

**Asunto del email:**
```
🏆 Partida Finalizada
```

**Contenido completo:**
```
¡Tu partida de League of Legends ha finalizado!

═══════════════════════════════════════════
📊 RESULTADO: VICTORIA
═══════════════════════════════════════════

🎯 TUS ESTADÍSTICAS:
├─ Kills: 12
├─ Deaths: 5
├─ Assists: 8
├─ KDA: 4.00
└─ Rendimiento: Excelente

🏆 MVP DE LA PARTIDA:
└─ Shadow42 (KDA: 5.20)

📈 MARCADOR FINAL:
├─ Equipo Azul: 45 kills
└─ Equipo Rojo: 32 kills

═══════════════════════════════════════════
Gracias por jugar en eScrims!
¡Nos vemos en la próxima partida! 🎮
```

**Código que lo envía:**
```java
// En MatchmakingController.java - al finalizar partida
enviarEmailEstadisticasFinales(scrim, usuarioReal, todosJugadores);
```

**Detalles incluidos:**
- ✅ Resultado (Victoria/Derrota)
- ✅ Tus kills, deaths, assists
- ✅ Tu KDA (Kill/Death/Assist ratio)
- ✅ Tu rendimiento (Excelente, Muy Bueno, Bueno, Regular, Pobre)
- ✅ MVP de la partida
- ✅ Marcador final de ambos equipos

---

### **5. ❌ PARTIDA CANCELADA** ⚠️

**Cuándo:**
- Si TÚ rechazas la confirmación
- Si estás baneado e intentas jugar
- Cualquier cancelación del scrim

**Asunto del email:**
```
❌ Scrim Cancelado
```

**Contenido:**
```
El scrim ha sido cancelado.

Motivo: No todos los jugadores confirmaron su participación.

Podrás unirte a un nuevo matchmaking en breve.
```

**Código que lo envía:**
```java
// En EstadoCancelado.java
ctx.notificarATodos(Notificacion.TipoNotificacion.CANCELADO,
    "El scrim ha sido cancelado.");
```

---

## 🎮 FLUJO COMPLETO DE EMAILS

### **Escenario Normal (Confirmas y Juegas):**

```
1. 🔍 Matchmaking inicia...
   └─ (sin email)

2. ✅ Lobby completo (10/10)
   └─ 📧 EMAIL 1: "Lobby Completo"

3. ⏰ Fase de confirmación
   └─ Te pregunta: ¿Confirmas? → Respondes "s"
   
4. ✅ Confirmado
   └─ 📧 EMAIL 2: "Scrim Confirmado"

5. ⚔️ Partida inicia
   └─ 📧 EMAIL 3: "Partida Iniciada"

6. 🎮 Jugando...
   └─ (sin email)

7. 🏆 Partida termina
   └─ 📧 EMAIL 4: "Partida Finalizada + ESTADÍSTICAS"

TOTAL: 4 EMAILS ✅
```

---

### **Escenario: Rechazas la Confirmación:**

```
1. 🔍 Matchmaking inicia...
   └─ (sin email)

2. ✅ Lobby completo (10/10)
   └─ 📧 EMAIL 1: "Lobby Completo"

3. ⏰ Fase de confirmación
   └─ Te pregunta: ¿Confirmas? → Respondes "n" ❌
   
4. 🚫 Sanción aplicada
   └─ Ban de 5-120 minutos (según historial)

5. ❌ Partida cancelada
   └─ 📧 EMAIL 2: "Scrim Cancelado"

TOTAL: 2 EMAILS
```

---

### **Escenario: Estás Baneado:**

```
1. 🔍 Matchmaking inicia...
   └─ (sin email)

2. ✅ Lobby completo (10/10)
   └─ 📧 EMAIL 1: "Lobby Completo"

3. ⏰ Fase de confirmación
   └─ Sistema detecta que estás baneado
   └─ Auto-rechaza tu participación

4. ❌ Partida cancelada
   └─ 📧 EMAIL 2: "Scrim Cancelado"
   └─ Mensaje en consola: "No puedes jugar mientras estés baneado"

TOTAL: 2 EMAILS
```

---

## 🆕 CAMBIOS IMPLEMENTADOS

### **1. Confirmación Solo para Ti** ✅

**ANTES:**
```
¿Galli confirma participación? (s/n): s
¿Shadow42 confirma participación? (s/n): s
¿Phoenix11 confirma participación? (s/n): s
... (pregunta por cada jugador)
```

**AHORA:**
```
[1/10] Galli
¿Confirmas tu participación? (s/n): s
✅ Galli confirmó (1/10)

[2/10] Shadow42
✅ Shadow42 confirmó (2/10)  ← Automático

[3/10] Phoenix11
✅ Phoenix11 confirmó (3/10)  ← Automático

... (todos los bots confirman automáticamente)
```

**Código:**
```java
// Solo pregunta al usuario real
if (jugador.getId() == usuarioReal.getId()) {
    confirma = consoleView.solicitarConfirmacion(
        "¿Confirmas tu participación? (s/n): "
    );
} else {
    // Bots confirman automáticamente
    confirma = true;
}
```

---

### **2. Sanción al Rechazar** ✅

**Si rechazas:**
```
❌ Has rechazado la partida

⚠️ SANCIÓN APLICADA:
🚫 Sancionado (1 sanciones totales)
   Ban de 5 minutos

💡 Los demás jugadores vuelven a la cola de matchmaking
```

**Código:**
```java
if (!confirma) {
    usuarioReal.agregarSancion();
    consoleView.mostrarError("🚫 Sancionado (" + 
        usuarioReal.getSancionesActivas() + " sanciones totales)");
    consoleView.mostrarInfo("   Ban de " + 
        usuarioReal.getMinutosRestantesBan() + " minutos");
    return false; // Cancela la partida
}
```

---

### **3. Email con Estadísticas Completas** ✅ **NUEVO**

**Contenido del email final:**

1. **Resultado:** Victoria o Derrota
2. **Tus estadísticas:**
   - Kills (bajas)
   - Deaths (muertes)
   - Assists (asistencias)
   - KDA (ratio)
   - Rendimiento (Excelente/Muy Bueno/Bueno/Regular/Pobre)

3. **MVP:** Mejor jugador de la partida

4. **Marcador:** Kills totales de cada equipo

**Formato:**
```
═══════════════════════════════════════════
📊 RESULTADO: VICTORIA
═══════════════════════════════════════════

🎯 TUS ESTADÍSTICAS:
├─ Kills: 12
├─ Deaths: 5
├─ Assists: 8
├─ KDA: 4.00
└─ Rendimiento: Excelente

🏆 MVP DE LA PARTIDA:
└─ Shadow42 (KDA: 5.20)

📈 MARCADOR FINAL:
├─ Equipo Azul: 45 kills
└─ Equipo Rojo: 32 kills
```

**Código:**
```java
private void enviarEmailEstadisticasFinales(Scrim scrim, Usuario usuarioReal, 
                                           List<Usuario> todosJugadores) {
    // Genera estadísticas
    // Calcula resultado
    // Construye mensaje formateado
    // Envía email con todas las stats
}
```

---

## 📊 RESUMEN DE EMAILS POR ESTADO

| Estado | Email | Asunto | Momento |
|--------|-------|--------|---------|
| **Buscando Jugadores** | ❌ No | - | Esperando matchmaking |
| **Lobby Completo** | ✅ Sí | ✅ Lobby Completo | Cuando se completa el lobby |
| **Confirmaciones** | ❌ No | - | Fase de confirmación |
| **Confirmado** | ✅ Sí | 🎯 Scrim Confirmado | Después de confirmar |
| **En Juego** | ✅ Sí | ⚔️ Partida Iniciada | Al comenzar la partida |
| **Finalizado** | ✅ Sí + Stats | 🏆 Partida Finalizada | Al terminar (con estadísticas) |
| **Cancelado** | ✅ Sí | ❌ Scrim Cancelado | Si se cancela |

---

## 🔔 TIPOS DE NOTIFICACIÓN

Todos los emails usan el enum `TipoNotificacion`:

```java
public enum TipoNotificacion {
    SCRIM_CREADO,       // 🎮 Nuevo Scrim Disponible
    LOBBY_COMPLETO,     // ✅ Lobby Completo - 10/10 Jugadores
    CONFIRMADO,         // 🎯 Scrim Confirmado - ¡A Jugar!
    EN_JUEGO,           // ⚔️ Partida Iniciada
    FINALIZADO,         // 🏆 Partida Finalizada
    CANCELADO,          // ❌ Scrim Cancelado
    // ... otros tipos
}
```

Cada tipo tiene su título y emoji personalizado automáticamente.

---

## 🎯 CONFIGURACIÓN ACTUAL

### **Emails activos:**
- ✅ Lobby Completo
- ✅ Scrim Confirmado
- ✅ Partida Iniciada
- ✅ Partida Finalizada (CON ESTADÍSTICAS) ← **NUEVO**
- ✅ Scrim Cancelado

### **Endpoint configurado:**
```
URL: https://send-email-zeta.vercel.app/send-email
Método: POST
Content-Type: application/json
```

### **Timeout:**
- Connect: 5 segundos
- Read: 5 segundos

---

## ✅ CHECKLIST DE FUNCIONALIDADES

### **Confirmaciones:**
- [x] Solo pregunta al usuario real
- [x] Bots confirman automáticamente
- [x] Sanción si usuario rechaza
- [x] Ban progresivo (5min → 120min)
- [x] Detección de usuario baneado

### **Emails:**
- [x] Email al completar lobby
- [x] Email al confirmar todos
- [x] Email al iniciar partida
- [x] Email al finalizar con estadísticas completas ← **NUEVO**
- [x] Email si se cancela

### **Estadísticas en Email Final:**
- [x] Resultado (Victoria/Derrota)
- [x] Kills del usuario
- [x] Deaths del usuario
- [x] Assists del usuario
- [x] KDA del usuario
- [x] Rendimiento del usuario
- [x] MVP de la partida
- [x] Marcador final de equipos

---

## 🧪 EJEMPLO COMPLETO DE FLUJO

### **Usuario: Galli (teosp2004@gmail.com)**

```
[PASO 1] Inicia Quick Match
├─ Juego: League of Legends
├─ Rol: Top
└─ Rango: 1500 MMR

[PASO 2] Matchmaking encuentra jugadores
├─ [1/10] Galli (tú)
├─ [2/10] Shadow42
├─ [3/10] Phoenix11
...
└─ [10/10] Storm88

📧 EMAIL 1 RECIBIDO:
   Asunto: ✅ Lobby Completo - 10/10 Jugadores
   Contenido: "¡Lobby completo! 10/10 jugadores..."

[PASO 3] Fase de confirmación
└─ ¿Confirmas tu participación? (s/n): s
   ✅ Confirmaste

📧 EMAIL 2 RECIBIDO:
   Asunto: 🎯 Scrim Confirmado - ¡A Jugar!
   Contenido: "¡Scrim confirmado! Todos listos..."

[PASO 4] Partida inicia

📧 EMAIL 3 RECIBIDO:
   Asunto: ⚔️ Partida Iniciada
   Contenido: "¡La partida ha comenzado!..."

[PASO 5] Jugando...
└─ (simulación de partida)

[PASO 6] Partida finaliza

📧 EMAIL 4 RECIBIDO:
   Asunto: 🏆 Partida Finalizada
   Contenido:
   ═══════════════════════════════════════════
   📊 RESULTADO: VICTORIA
   ═══════════════════════════════════════════
   
   🎯 TUS ESTADÍSTICAS:
   ├─ Kills: 15
   ├─ Deaths: 4
   ├─ Assists: 10
   ├─ KDA: 6.25
   └─ Rendimiento: Excelente
   
   🏆 MVP DE LA PARTIDA:
   └─ Galli (KDA: 6.25)  ← ¡ERES TÚ!
   
   📈 MARCADOR FINAL:
   ├─ Equipo Azul: 52 kills
   └─ Equipo Rojo: 38 kills
   
   ═══════════════════════════════════════════
   Gracias por jugar en eScrims!
   ¡Nos vemos en la próxima partida! 🎮

TOTAL: 4 EMAILS RECIBIDOS EN TU INBOX ✅
```

---

## 📝 ARCHIVOS MODIFICADOS

```
✅ controllers/MatchmakingController.java
   - Método procesarConfirmacionesJugadores() (+60 líneas)
     └─ Solo pregunta al usuario real
     └─ Aplica sanción si rechaza
   
   - Método ejecutarTransicionesEstado() (+5 líneas)
     └─ Llama a enviarEmailEstadisticasFinales()
   
   - Método enviarEmailEstadisticasFinales() (+95 líneas) ← NUEVO
     └─ Genera estadísticas
     └─ Calcula resultado
     └─ Construye email formateado
     └─ Envía notificación
```

---

## 🚀 RESULTADO FINAL

### **Antes:**
- ❌ Preguntaba a todos los jugadores (incluidos bots)
- ❌ No había email con estadísticas finales
- ❌ Solo emails básicos de estado

### **Ahora:**
- ✅ Solo pregunta al usuario real
- ✅ Bots confirman automáticamente
- ✅ Sanción si usuario rechaza
- ✅ Email completo con estadísticas al finalizar
- ✅ 4-5 emails por partida completa
- ✅ Información detallada de rendimiento

---

**Última actualización:** 10/11/2025  
**Estado:** ✅ IMPLEMENTADO Y COMPILADO  
**Testing:** Listo para pruebas

---

## 🎉 RESUMEN EJECUTIVO

**Recibirás un email en estos momentos:**

1. ✅ **Lobby completo** (10/10 jugadores encontrados)
2. ✅ **Scrim confirmado** (después de que confirmes)
3. ✅ **Partida iniciada** (al comenzar)
4. ✅ **Partida finalizada** (CON TODAS TUS ESTADÍSTICAS) ← **EL MÁS IMPORTANTE**
5. ❌ **Scrim cancelado** (si rechazas o hay problema)

**Total:** Entre 2 y 4 emails por partida, dependiendo del resultado.

El email más completo es el **#4 (Finalizado)** que incluye:
- 📊 Resultado
- 🎯 Tus stats completas
- 🏆 MVP
- 📈 Marcador final
