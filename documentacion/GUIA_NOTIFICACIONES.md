# 📧 Guía Completa de Notificaciones - eScrims Platform

## 📋 Requisito del TP (RF7)

Según el **Requerimiento Funcional #7** del TP:

> **7. Notificaciones (Observer + Abstract Factory/Adapter)**
> - **Eventos que disparan notificaciones:**
>   1. Scrim creado que coincide con preferencias del usuario
>   2. Cambio a Lobby armado (cupo completo)
>   3. Confirmado por todos
>   4. Cambio a En juego, Finalizado o Cancelado
> - **Canales:** Push (Firebase), Email (JavaMail/SendGrid), Discord/Slack (webhook/bot)

---

## 🎯 Eventos que Disparan Notificaciones

### **1️⃣ Scrim Creado (Coincide con Preferencias)**

**Cuándo:**
- Un usuario crea un nuevo Scrim
- El sistema busca usuarios con alertas configuradas que coincidan con los filtros del scrim

**A quién notificar:**
- ✅ Usuarios con **búsquedas guardadas** que coincidan con:
  - Juego (ej: "League of Legends")
  - Región (ej: "LAS")
  - Rango mínimo/máximo (ej: Oro - Platino)
  - Formato (ej: 5v5)
  - Fecha/hora aproximada

**Canales:**
- 📧 **Email:** "Nuevo scrim disponible que coincide con tus preferencias"
- 💬 **Discord:** Mensaje en canal personal
- 📱 **Push:** Notificación móvil

**Contenido:**
```
Título: 🎮 Nuevo Scrim Disponible - League of Legends 5v5
Mensaje: 
  Un nuevo scrim coincide con tus preferencias:
  • Juego: League of Legends
  • Formato: 5v5 (Ranked)
  • Región: LAS
  • Rango: Oro - Platino
  • Fecha: 10/11/2025 20:00 hs
  • Cupos disponibles: 10/10
  
  ¡Postúlate ahora!
  [Ver Scrim] [Postularme]
```

**Implementación en código:**
```java
// En EstadoBuscandoJugadores.java (cuando se crea el scrim)
public void postularJugador(ScrimContext ctx, Usuario usuario, String rol) {
    // Al crear el scrim, buscar usuarios con alertas
    List<Usuario> usuariosInteresados = buscarUsuariosConAlertas(ctx.getScrim());
    
    for (Usuario usuarioInteresado : usuariosInteresados) {
        Notificacion notif = new Notificacion(
            "Nuevo Scrim Disponible",
            "Un scrim coincide con tus preferencias: " + ctx.getScrim().getJuego(),
            usuarioInteresado
        );
        ctx.notificarCambio(notif);
    }
}
```

---

### **2️⃣ Lobby Armado (Cupo Completo)**

**Cuándo:**
- El último jugador se postula y el scrim alcanza **10/10 jugadores**
- **Transición:** `EstadoBuscandoJugadores` → `EstadoLobbyCompleto`

**A quién notificar:**
- ✅ **Todos los 10 jugadores** postulados en el scrim

**Canales:**
- 📧 **Email:** "Lobby completo - Confirma tu participación"
- 💬 **Discord:** Mención urgente
- 📱 **Push:** Notificación con acción requerida

**Contenido:**
```
Título: ✅ Lobby Completo - Confirma tu Participación
Mensaje:
  El lobby está completo (10/10 jugadores).
  
  Por favor, CONFIRMA tu participación en los próximos 5 minutos.
  Si no confirmas, serás reemplazado por un jugador de la lista de espera.
  
  Scrim: League of Legends 5v5
  Fecha: 10/11/2025 20:00 hs
  Tu rol: Support
  
  [✓ CONFIRMAR] [✗ Cancelar]
```

**Implementación en código:**
```java
// En EstadoBuscandoJugadores.java
public void postularJugador(ScrimContext ctx, Usuario usuario, String rol) {
    ctx.getScrim().agregarJugador(usuario, rol);
    
    // Si se completa el lobby
    if (ctx.getScrim().getCuposDisponibles() == 0) {
        // Notificar a TODOS los jugadores
        List<Usuario> jugadores = ctx.getScrim().getJugadores();
        
        for (Usuario jugador : jugadores) {
            Notificacion notif = new Notificacion(
                "Lobby Completo - Confirma tu Participación",
                "El lobby está completo (10/10). Confirma en los próximos 5 minutos.",
                jugador
            );
            ctx.notificarCambio(notif);
        }
        
        // Cambiar estado
        ctx.cambiarEstado(new EstadoLobbyCompleto());
    }
}
```

---

### **3️⃣ Confirmado por Todos**

**Cuándo:**
- El último jugador confirma su participación
- **Todos los 10 jugadores** han confirmado
- **Transición:** `EstadoLobbyCompleto` → `EstadoConfirmado`

**A quién notificar:**
- ✅ **Todos los 10 jugadores** confirmados
- ✅ **Creador del scrim** (organizador)

**Canales:**
- 📧 **Email:** "Partida confirmada - Prepárate para jugar"
- 💬 **Discord:** Mensaje en canal del equipo
- 📱 **Push:** Recordatorio con countdown

**Contenido:**
```
Título: 🎉 Partida Confirmada - ¡Todos Listos!
Mensaje:
  ¡Todos los jugadores han confirmado!
  
  La partida comenzará automáticamente a las 20:00 hs.
  
  📋 Detalles:
  • Scrim: League of Legends 5v5
  • Fecha/Hora: 10/11/2025 20:00 hs
  • Tu equipo: Team Azul
  • Tu rol: Support
  
  🎮 Composición de equipos:
  Team Azul: ProGamer (Top), Shadow (Jungle), Phoenix (Mid), Thunder (ADC), TÚ (Support)
  Team Rojo: Dragon (Top), Silent (Jungle), Mystic (Mid), Cyber (ADC), Night (Support)
  
  ⏰ Recordatorio: Te avisaremos 15 minutos antes
  
  [Ver Equipos] [Ir a Discord]
```

**Implementación en código:**
```java
// En EstadoLobbyCompleto.java
public void confirmarJugador(ScrimContext ctx, Usuario usuario) {
    ctx.getScrim().confirmar(usuario);
    
    // Si todos confirmaron
    if (ctx.getScrim().todosConfirmaron()) {
        List<Usuario> jugadores = ctx.getScrim().getJugadores();
        
        for (Usuario jugador : jugadores) {
            Notificacion notif = new Notificacion(
                "Partida Confirmada - ¡Todos Listos!",
                "Todos confirmaron. La partida comenzará a las " + ctx.getScrim().getFechaHora(),
                jugador
            );
            ctx.notificarCambio(notif);
        }
        
        // Cambiar estado
        ctx.cambiarEstado(new EstadoConfirmado());
    }
}
```

---

### **4️⃣ En Juego (Partida Iniciada)**

**Cuándo:**
- La fecha/hora programada del scrim se alcanza
- **Transición automática (scheduler):** `EstadoConfirmado` → `EstadoEnJuego`

**A quién notificar:**
- ✅ **Todos los 10 jugadores** participantes

**Canales:**
- 📧 **Email:** "Tu partida ha comenzado"
- 💬 **Discord:** Mensaje con link a la sala
- 📱 **Push:** Notificación urgente

**Contenido:**
```
Título: 🚀 ¡Partida Iniciada!
Mensaje:
  Tu scrim de League of Legends ha comenzado.
  
  🎮 Información de la Partida:
  • Servidor: LAS
  • Sala: SCRIM-ABC123
  • Contraseña: LOL2025
  • Duración estimada: 25-45 minutos
  
  👥 Tu equipo (Team Azul):
  • ProGamer (Top)
  • Shadow (Jungle)
  • Phoenix (Mid)
  • Thunder (ADC)
  • TÚ (Support)
  
  💬 Discord: [Unirse al canal de voz]
  
  ¡Buena suerte! 🍀
```

**Implementación en código:**
```java
// En EstadoConfirmado.java (ejecutado por scheduler)
public void iniciarPartida(ScrimContext ctx) {
    List<Usuario> jugadores = ctx.getScrim().getJugadores();
    
    for (Usuario jugador : jugadores) {
        Notificacion notif = new Notificacion(
            "¡Partida Iniciada!",
            "Tu scrim ha comenzado. Sala: " + ctx.getScrim().getSala(),
            jugador
        );
        ctx.notificarCambio(notif);
    }
    
    // Cambiar estado
    ctx.cambiarEstado(new EstadoEnJuego());
}
```

---

### **5️⃣ Finalizado (Partida Terminada)**

**Cuándo:**
- La partida termina (cierre manual del organizador o automático por duración)
- **Transición:** `EstadoEnJuego` → `EstadoFinalizado`

**A quién notificar:**
- ✅ **Todos los 10 jugadores** participantes

**Canales:**
- 📧 **Email:** "Partida finalizada - Carga tus estadísticas"
- 💬 **Discord:** Resumen de la partida
- 📱 **Push:** Solicitud de feedback

**Contenido:**
```
Título: 🏆 Partida Finalizada
Mensaje:
  Tu scrim de League of Legends ha finalizado.
  
  📊 Resultado: Team Azul 28 - 15 Team Rojo
  🎖️ MVP: ProGamer (12/2/8)
  ⏱️ Duración: 32 minutos
  
  📝 Próximos pasos:
  1. Carga tus estadísticas (K/D/A)
  2. Califica a tus compañeros (opcional)
  3. Reporta problemas de conducta (si aplica)
  
  Tu rendimiento:
  • Rol: Support
  • K/D/A: 2/5/18 (KDA: 4.0)
  • Rating sugerido: ⭐⭐⭐⭐☆
  
  [Cargar Estadísticas] [Ver Detalle]
```

**Implementación en código:**
```java
// En EstadoEnJuego.java
public void finalizarPartida(ScrimContext ctx, Resultado resultado) {
    List<Usuario> jugadores = ctx.getScrim().getJugadores();
    
    for (Usuario jugador : jugadores) {
        Estadistica stats = resultado.getEstadisticasPara(jugador);
        
        Notificacion notif = new Notificacion(
            "Partida Finalizada",
            "Tu scrim ha terminado. Tu K/D/A: " + stats.getKDA(),
            jugador
        );
        ctx.notificarCambio(notif);
    }
    
    // Cambiar estado
    ctx.cambiarEstado(new EstadoFinalizado());
}
```

---

### **6️⃣ Cancelado (Scrim Cancelado)**

**Cuándo:**
- El organizador cancela el scrim antes de que inicie
- Un jugador clave se retira y no hay reemplazos
- **Transición:** `Cualquier estado` → `EstadoCancelado`

**A quién notificar:**
- ✅ **Todos los jugadores postulados/confirmados**
- ✅ **Usuarios en lista de espera**

**Canales:**
- 📧 **Email:** "Scrim cancelado - Razón y reembolso"
- 💬 **Discord:** Aviso urgente
- 📱 **Push:** Notificación crítica

**Contenido:**
```
Título: ⚠️ Scrim Cancelado
Mensaje:
  Lamentamos informarte que el scrim ha sido cancelado.
  
  🚫 Razón: Jugador clave desconectado sin reemplazo disponible
  
  📋 Detalles:
  • Scrim: League of Legends 5v5
  • Fecha programada: 10/11/2025 20:00 hs
  • Estado anterior: Confirmado
  
  💰 Reembolso:
  Si habías pagado una entrada, será reembolsada en 24-48 hs.
  
  🔍 Buscar scrims similares:
  Te recomendamos estos scrims disponibles en tu rango:
  • [Scrim #456] LoL 5v5 - Oro/Platino - Mañana 21:00
  • [Scrim #789] LoL 5v5 - Platino - Pasado mañana 19:00
  
  [Buscar Scrims] [Ver Historial]
```

**Implementación en código:**
```java
// En EstadoConfirmado.java (o cualquier estado)
public void cancelarScrim(ScrimContext ctx, String razon) {
    List<Usuario> jugadores = ctx.getScrim().getJugadores();
    
    for (Usuario jugador : jugadores) {
        Notificacion notif = new Notificacion(
            "Scrim Cancelado",
            "El scrim ha sido cancelado. Razón: " + razon,
            jugador
        );
        ctx.notificarCambio(notif);
    }
    
    // Cambiar estado
    ctx.cambiarEstado(new EstadoCancelado(razon));
}
```

---

## 🔔 Notificaciones Adicionales (Opcionales pero Recomendadas)

### **7️⃣ Recordatorio Pre-Partida**

**Cuándo:**
- 15 minutos antes de que inicie la partida

**A quién:**
- ✅ Todos los jugadores confirmados

**Contenido:**
```
Título: ⏰ Recordatorio - Tu partida comienza en 15 minutos
Mensaje:
  Tu scrim de League of Legends comenzará pronto.
  
  ⏰ Hora de inicio: 20:00 hs (en 15 minutos)
  🎮 Servidor: LAS
  💬 Discord: Únete al canal de voz ahora
  
  ✅ Checklist:
  □ Cliente del juego abierto
  □ Discord conectado
  □ Micrófono/auriculares probados
  
  [Ir a Discord] [Ver Detalles]
```

---

### **8️⃣ Jugador Reemplazado**

**Cuándo:**
- Un jugador no confirma a tiempo y es reemplazado

**A quién:**
- ✅ Jugador original (que no confirmó)
- ✅ Jugador de reemplazo (de lista de espera)

**Contenido (jugador original):**
```
Título: ⚠️ Has sido reemplazado
Mensaje:
  No confirmaste tu participación a tiempo.
  
  Has sido reemplazado por un jugador de la lista de espera.
  
  ⚠️ Esto cuenta como 1 strike. Con 3 strikes recibirás un cooldown de 24 horas.
```

**Contenido (reemplazo):**
```
Título: 🎉 ¡Te han asignado un scrim!
Mensaje:
  Un jugador no confirmó y ahora eres parte del scrim.
  
  Confirma tu participación en los próximos 2 minutos.
```

---

### **9️⃣ Postulación Aceptada/Rechazada**

**Cuándo:**
- El organizador acepta o rechaza manualmente una postulación

**Contenido (aceptada):**
```
Título: ✅ Postulación Aceptada
Mensaje: Has sido aceptado en el scrim. Confirma tu participación.
```

**Contenido (rechazada):**
```
Título: ❌ Postulación Rechazada
Mensaje: Tu postulación no fue aceptada. Razón: Rango no coincide.
```

---

## 🎨 Implementación del Patrón Observer

### **Flujo Completo:**

```
1. ScrimContext.cambiarEstado(nuevoEstado)
      ↓
2. ScrimContext.notificarCambio()
      ↓
3. DomainEventBus.publish(ScrimStateChangedEvent)
      ↓
4. NotificationSubscriber.onEvent(event)
      ↓
5. NotifierFactory.createNotifiers()
      ↓
6. CompositeNotifier.send(notificacion)
      ↓
7. EmailNotifier.send()
   DiscordNotifier.send()
   PushNotifier.send()
```

### **Código de Ejemplo:**

```java
// ScrimContext.java
public void cambiarEstado(ScrimState nuevoEstado) {
    this.estado = nuevoEstado;
    notificarCambio();
}

public void notificarCambio() {
    ScrimStateChangedEvent evento = new ScrimStateChangedEvent(
        this.scrim,
        this.estado.getClass().getSimpleName()
    );
    
    DomainEventBus.getInstance().publish(evento);
}

// NotificationSubscriber.java
public class NotificationSubscriber implements Subscriber {
    private CompositeNotifier notifier;
    
    public NotificationSubscriber() {
        NotifierFactory factory = new NotifierFactory();
        this.notifier = new CompositeNotifier();
        this.notifier.add(factory.createEmailNotifier());
        this.notifier.add(factory.createDiscordNotifier());
        this.notifier.add(factory.createPushNotifier());
    }
    
    @Override
    public void onEvent(DomainEvent evento) {
        if (evento instanceof ScrimStateChangedEvent) {
            ScrimStateChangedEvent scrimEvent = (ScrimStateChangedEvent) evento;
            
            String mensaje = generarMensajeSegunEstado(scrimEvent);
            
            Notificacion notif = new Notificacion(
                scrimEvent.getTitulo(),
                mensaje,
                scrimEvent.getDestinatarios()
            );
            
            notifier.send(notif);
        }
    }
}
```

---

## 📊 Resumen de Notificaciones por Estado

| **Estado** | **Evento** | **Destinatarios** | **Canales** | **Prioridad** |
|---|---|---|---|---|
| **Creado** | Scrim creado | Usuarios con alertas | Email, Push | 🟡 Media |
| **Buscando → LobbyCompleto** | Cupo completo | Todos los jugadores | Email, Discord, Push | 🔴 Alta |
| **LobbyCompleto → Confirmado** | Todos confirmaron | Todos los jugadores | Email, Discord, Push | 🔴 Alta |
| **Confirmado → EnJuego** | Partida iniciada | Todos los jugadores | Email, Discord, Push | 🔴 Crítica |
| **EnJuego → Finalizado** | Partida terminada | Todos los jugadores | Email, Discord, Push | 🟢 Normal |
| **Cualquiera → Cancelado** | Scrim cancelado | Todos postulados | Email, Discord, Push | 🔴 Alta |
| **(Opcional) Recordatorio** | 15 min antes | Todos confirmados | Discord, Push | 🟡 Media |
| **(Opcional) Reemplazo** | Jugador reemplazado | Original + Reemplazo | Email, Push | 🔴 Alta |

---

## ✅ Checklist de Implementación

- [ ] **1. Scrim Creado (alertas)**
  - [ ] Buscar usuarios con preferencias coincidentes
  - [ ] Enviar notificación multi-canal
  
- [ ] **2. Lobby Completo**
  - [ ] Detectar cupo completo (10/10)
  - [ ] Notificar a TODOS los jugadores
  - [ ] Incluir botón de confirmación
  
- [ ] **3. Todos Confirmaron**
  - [ ] Verificar que todos confirmaron
  - [ ] Mostrar composición de equipos
  - [ ] Programar recordatorio
  
- [ ] **4. Partida Iniciada**
  - [ ] Scheduler automático por fecha/hora
  - [ ] Enviar datos de conexión (sala, password)
  - [ ] Link a Discord
  
- [ ] **5. Partida Finalizada**
  - [ ] Enviar estadísticas individuales
  - [ ] Solicitar feedback
  - [ ] Habilitar carga de resultados
  
- [ ] **6. Scrim Cancelado**
  - [ ] Incluir razón de cancelación
  - [ ] Informar sobre reembolsos
  - [ ] Sugerir scrims alternativos
  
- [ ] **7. (Opcional) Recordatorio Pre-Partida**
  - [ ] Scheduler 15 min antes
  - [ ] Checklist de preparación
  
- [ ] **8. (Opcional) Reemplazos**
  - [ ] Notificar jugador original
  - [ ] Notificar reemplazo
  - [ ] Registrar strike

---

## 🎯 Cumplimiento del TP

### ✅ Patrones Implementados:
- **Observer:** DomainEventBus + NotificationSubscriber
- **Abstract Factory:** NotifierFactory crea EmailNotifier, DiscordNotifier, PushNotifier
- **State:** Estados del scrim disparan notificaciones automáticas

### ✅ Requisitos Funcionales Cubiertos:
- **RF7:** Notificaciones multi-canal implementadas
- **RF4:** Estados del scrim con transiciones automáticas
- **RF10:** Recordatorios automáticos

### ✅ Casos de Uso Implementados:
- **CU10:** Notificar eventos ✓

---

**Fecha:** 2025-11-10  
**Versión:** 1.0  
**Autor:** Sistema eScrims  
**Estado:** ✅ Completo según requisitos del TP
