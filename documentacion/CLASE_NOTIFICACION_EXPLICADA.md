# 📧 Clase `Notificacion` - Explicación Completa

## 🎯 ¿Qué es la clase `Notificacion`?

La clase `Notificacion` es el **modelo de datos** (DTO - Data Transfer Object) que representa un mensaje que se enviará a un usuario. Es como un "sobre" que contiene toda la información necesaria para enviar una notificación.

---

## 📦 Estructura de la Clase `Notificacion`

```java
package models;

import java.time.LocalDateTime;
import java.util.UUID;

public class Notificacion {
    // ============ ATRIBUTOS ============
    private UUID id;                          // Identificador único
    private String tipo;                      // Tipo: "LOBBY_COMPLETO", "CONFIRMADO", etc.
    private String titulo;                    // Asunto del email / Título de la notificación
    private String mensaje;                   // Cuerpo del mensaje
    private Usuario destinatario;             // A quién se le envía
    private String canal;                     // "EMAIL", "DISCORD", "PUSH"
    private EstadoNotificacion estado;        // PENDIENTE, ENVIADA, FALLIDA
    private LocalDateTime fechaCreacion;      // Cuándo se creó
    private LocalDateTime fechaEnvio;         // Cuándo se envió
    private int intentosEnvio;                // Contador de reintentos
    private String errorMensaje;              // Si falló, por qué
    
    // ============ CONSTRUCTOR ============
    public Notificacion(String tipo, String titulo, String mensaje, Usuario destinatario) {
        this.id = UUID.randomUUID();
        this.tipo = tipo;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.destinatario = destinatario;
        this.estado = EstadoNotificacion.PENDIENTE;
        this.fechaCreacion = LocalDateTime.now();
        this.intentosEnvio = 0;
    }
    
    // ============ MÉTODOS ============
    public void marcarComoEnviada() {
        this.estado = EstadoNotificacion.ENVIADA;
        this.fechaEnvio = LocalDateTime.now();
    }
    
    public void marcarComoFallida(String error) {
        this.estado = EstadoNotificacion.FALLIDA;
        this.errorMensaje = error;
        this.intentosEnvio++;
    }
    
    public boolean puedeReintentar() {
        return this.intentosEnvio < 3; // Máximo 3 reintentos
    }
    
    // Getters y setters...
}

enum EstadoNotificacion {
    PENDIENTE,   // Creada pero no enviada
    ENVIADA,     // Enviada exitosamente
    FALLIDA      // Falló el envío
}
```

---

## 🎯 ¿Para qué sirve la clase `Notificacion`?

### 1️⃣ **Separación de Responsabilidades**

En lugar de que cada `Notifier` (Email, Discord, Push) maneje sus propios datos, **todos usan el mismo objeto `Notificacion`**.

**Sin `Notificacion` (❌ Mal):**
```java
// Cada notifier recibe parámetros sueltos
emailNotifier.send("Lobby Completo", "El lobby está listo", usuario);
discordNotifier.send("Lobby Completo", "El lobby está listo", usuario);
pushNotifier.send("Lobby Completo", "El lobby está listo", usuario);
```

**Con `Notificacion` (✅ Bien):**
```java
// Todos reciben el mismo objeto
Notificacion notif = new Notificacion("LOBBY_COMPLETO", "Lobby Completo", "El lobby está listo", usuario);
emailNotifier.send(notif);
discordNotifier.send(notif);
pushNotifier.send(notif);
```

---

### 2️⃣ **Trazabilidad y Auditoría**

Puedes **guardar las notificaciones en base de datos** para:
- Ver historial de notificaciones enviadas
- Auditar qué se envió y cuándo
- Debugging (si un usuario dice "no recibí el mail")
- Estadísticas (cuántas notificaciones se envían por día)

**Ejemplo:**
```java
// Guardar en BD para auditoría
notificationRepository.save(notificacion);
```

---

### 3️⃣ **Reintentos Automáticos**

Si un email falla (servidor caído), puedes reintentar:

```java
public void enviarConReintentos(Notificacion notif) {
    while (notif.puedeReintentar()) {
        try {
            emailNotifier.send(notif);
            notif.marcarComoEnviada();
            break;
        } catch (Exception e) {
            notif.marcarComoFallida(e.getMessage());
            Thread.sleep(5000); // Esperar 5 segundos antes de reintentar
        }
    }
}
```

---

### 4️⃣ **Colas de Notificaciones**

Puedes poner notificaciones en una **cola** (como RabbitMQ/Kafka) para enviarlas de forma asíncrona:

```java
// En lugar de enviar inmediatamente
notificationQueue.enqueue(notificacion);

// Un worker aparte las procesa
while (true) {
    Notificacion notif = notificationQueue.dequeue();
    emailNotifier.send(notif);
}
```

---

### 5️⃣ **Personalización por Canal**

Cada canal puede adaptar el mismo mensaje:

```java
public class EmailNotifier implements Notifier {
    @Override
    public void send(Notificacion notif) {
        String emailHTML = generarHTML(notif);
        emailService.enviar(notif.getDestinatario().getEmail(), notif.getTitulo(), emailHTML);
    }
    
    private String generarHTML(Notificacion notif) {
        return """
        <html>
            <h1>%s</h1>
            <p>%s</p>
        </html>
        """.formatted(notif.getTitulo(), notif.getMensaje());
    }
}

public class DiscordNotifier implements Notifier {
    @Override
    public void send(Notificacion notif) {
        String discordMarkdown = generarMarkdown(notif);
        discordWebhook.enviar(discordMarkdown);
    }
    
    private String generarMarkdown(Notificacion notif) {
        return """
        **%s**
        %s
        """.formatted(notif.getTitulo(), notif.getMensaje());
    }
}
```

---

## 📧 ¿Qué cosas deberías notificar por MAIL?

Según el **Requerimiento Funcional #7** del TP, debes notificar estos **6 eventos obligatorios**:

### ✅ **1. Scrim Creado (que coincide con preferencias)**

**Cuándo enviar:**
- Un usuario tiene guardada una "alerta" de búsqueda (ej: "LoL 5v5, rango Oro-Platino, región LAS")
- Se crea un scrim que coincide con esos filtros

**Asunto del email:**
```
🎮 Nuevo Scrim Disponible - League of Legends 5v5
```

**Cuerpo del email:**
```html
<html>
<body>
    <h2>¡Nuevo Scrim Disponible!</h2>
    <p>Hola ProGamer,</p>
    
    <p>Un nuevo scrim coincide con tus preferencias guardadas:</p>
    
    <ul>
        <li><strong>Juego:</strong> League of Legends</li>
        <li><strong>Formato:</strong> 5v5 (Ranked)</li>
        <li><strong>Región:</strong> LAS (Latinoamérica Sur)</li>
        <li><strong>Rango:</strong> Oro - Platino</li>
        <li><strong>Fecha/Hora:</strong> 11/11/2025 20:00 hs</li>
        <li><strong>Cupos disponibles:</strong> 10/10</li>
    </ul>
    
    <p><a href="https://escrims.com/scrim/abc123">Ver Scrim y Postularme</a></p>
    
    <p>¡No esperes más! Los cupos se llenan rápido.</p>
    
    <hr>
    <p style="font-size: 12px; color: gray;">
        Recibiste este email porque tienes una alerta configurada.
        <a href="https://escrims.com/settings/alerts">Administrar Alertas</a>
    </p>
</body>
</html>
```

**Por qué es importante:**
- ✅ Usuario pidió que le avisen (alerta activa)
- ✅ No es spam (es relevante para él)
- ✅ Aumenta participación en scrims

---

### ✅ **2. Lobby Completo (cupo lleno)**

**Cuándo enviar:**
- Se completa el cupo de 10/10 jugadores
- **URGENTE:** El jugador debe confirmar en 5 minutos

**Asunto del email:**
```
✅ ACCIÓN REQUERIDA - Confirma tu participación (5 minutos)
```

**Cuerpo del email:**
```html
<html>
<body>
    <h2 style="color: #FF6B00;">⚠️ ACCIÓN REQUERIDA</h2>
    <p>Hola ProGamer,</p>
    
    <p><strong>El lobby está completo (10/10 jugadores).</strong></p>
    
    <div style="background: #FFF3E0; padding: 15px; border-left: 4px solid #FF6B00;">
        <p><strong>⏰ Tienes 5 minutos para confirmar tu participación.</strong></p>
        <p>Si no confirmas, serás reemplazado por un jugador de la lista de espera.</p>
    </div>
    
    <h3>Detalles del Scrim:</h3>
    <ul>
        <li><strong>Juego:</strong> League of Legends 5v5</li>
        <li><strong>Fecha/Hora:</strong> 11/11/2025 20:00 hs</li>
        <li><strong>Tu rol:</strong> Support</li>
    </ul>
    
    <p style="text-align: center; margin: 30px 0;">
        <a href="https://escrims.com/scrim/abc123/confirm" 
           style="background: #4CAF50; color: white; padding: 15px 40px; 
                  text-decoration: none; border-radius: 5px; font-size: 18px;">
            ✓ CONFIRMAR PARTICIPACIÓN
        </a>
    </p>
    
    <p style="text-align: center;">
        <a href="https://escrims.com/scrim/abc123/cancel" 
           style="color: #999; font-size: 14px;">
            Cancelar mi postulación
        </a>
    </p>
</body>
</html>
```

**Por qué es importante:**
- 🔴 **CRÍTICO:** Requiere acción inmediata del usuario
- ✅ Si no confirma, pierde el spot
- ✅ Evita que usuarios afk arruinen el lobby

---

### ✅ **3. Partida Confirmada (todos confirmaron)**

**Cuándo enviar:**
- Los 10 jugadores confirmaron su participación
- La partida está garantizada

**Asunto del email:**
```
🎉 Partida Confirmada - ¡Todos Listos!
```

**Cuerpo del email:**
```html
<html>
<body>
    <h2 style="color: #4CAF50;">🎉 ¡Partida Confirmada!</h2>
    <p>Hola ProGamer,</p>
    
    <p><strong>Todos los jugadores han confirmado.</strong> La partida comenzará automáticamente a las 20:00 hs.</p>
    
    <h3>📋 Detalles de la Partida:</h3>
    <ul>
        <li><strong>Juego:</strong> League of Legends 5v5</li>
        <li><strong>Fecha/Hora:</strong> 11/11/2025 20:00 hs</li>
        <li><strong>Servidor:</strong> LAS</li>
        <li><strong>Tu equipo:</strong> Team Azul</li>
        <li><strong>Tu rol:</strong> Support</li>
    </ul>
    
    <h3>🎮 Composición de Equipos:</h3>
    <div style="display: flex; gap: 20px;">
        <div style="flex: 1; background: #E3F2FD; padding: 10px;">
            <h4>Team Azul (TÚ)</h4>
            <ul>
                <li>ProGamer (Support) ⭐</li>
                <li>Shadow (Jungle)</li>
                <li>Phoenix (Mid)</li>
                <li>Thunder (ADC)</li>
                <li>Dragon (Top)</li>
            </ul>
        </div>
        <div style="flex: 1; background: #FFEBEE; padding: 10px;">
            <h4>Team Rojo</h4>
            <ul>
                <li>Night (Support)</li>
                <li>Silent (Jungle)</li>
                <li>Mystic (Mid)</li>
                <li>Cyber (ADC)</li>
                <li>Iron (Top)</li>
            </ul>
        </div>
    </div>
    
    <div style="background: #FFF9C4; padding: 15px; margin: 20px 0;">
        <p><strong>⏰ Recordatorio:</strong> Te avisaremos 15 minutos antes de que inicie la partida.</p>
    </div>
    
    <p><a href="https://discord.gg/scrim-abc123">💬 Unirse al Canal de Discord</a></p>
    <p><a href="https://escrims.com/scrim/abc123">📊 Ver Detalles Completos</a></p>
</body>
</html>
```

**Por qué es importante:**
- ✅ Confirma que la partida es segura
- ✅ Muestra con quién jugará
- ✅ Da tiempo para prepararse

---

### ✅ **4. Partida Iniciada (en juego)**

**Cuándo enviar:**
- La fecha/hora programada se alcanza
- La partida comienza automáticamente

**Asunto del email:**
```
🚀 Tu Partida ha Comenzado - ¡Conéctate Ahora!
```

**Cuerpo del email:**
```html
<html>
<body>
    <h2 style="color: #FF6B00;">🚀 ¡Tu Partida ha Comenzado!</h2>
    <p>Hola ProGamer,</p>
    
    <p><strong>Tu scrim de League of Legends está en curso.</strong></p>
    
    <div style="background: #E8F5E9; padding: 20px; border-radius: 5px;">
        <h3>🎮 Información de Conexión:</h3>
        <ul>
            <li><strong>Servidor:</strong> LAS (Latinoamérica Sur)</li>
            <li><strong>Sala:</strong> SCRIM-ABC123</li>
            <li><strong>Contraseña:</strong> LOL2025</li>
            <li><strong>Duración estimada:</strong> 25-45 minutos</li>
        </ul>
    </div>
    
    <h3>👥 Tu Equipo (Team Azul):</h3>
    <ul>
        <li>ProGamer (Support) - TÚ ⭐</li>
        <li>Shadow (Jungle)</li>
        <li>Phoenix (Mid)</li>
        <li>Thunder (ADC)</li>
        <li>Dragon (Top)</li>
    </ul>
    
    <p style="text-align: center; margin: 30px 0;">
        <a href="https://discord.gg/scrim-abc123" 
           style="background: #7289DA; color: white; padding: 15px 40px; 
                  text-decoration: none; border-radius: 5px;">
            💬 Unirse al Canal de Voz (Discord)
        </a>
    </p>
    
    <p style="font-size: 18px; text-align: center;">
        <strong>¡Buena suerte! 🍀</strong>
    </p>
</body>
</html>
```

**Por qué es importante:**
- 🔴 **URGENTE:** El jugador debe conectarse YA
- ✅ Provee datos de conexión (sala, password)
- ✅ Evita que se olvide de entrar

---

### ✅ **5. Partida Finalizada**

**Cuándo enviar:**
- La partida termina (manual o automático)
- Se habilita carga de estadísticas

**Asunto del email:**
```
🏆 Partida Finalizada - Carga tus Estadísticas
```

**Cuerpo del email:**
```html
<html>
<body>
    <h2 style="color: #4CAF50;">🏆 Partida Finalizada</h2>
    <p>Hola ProGamer,</p>
    
    <p>Tu scrim de League of Legends ha finalizado.</p>
    
    <h3>📊 Resultado:</h3>
    <div style="background: #E3F2FD; padding: 15px; border-radius: 5px;">
        <p style="font-size: 24px; text-align: center; margin: 0;">
            <strong>Team Azul 28 - 15 Team Rojo</strong>
        </p>
        <p style="text-align: center; margin: 10px 0 0 0;">
            🎖️ MVP: ProGamer (12/2/8)
        </p>
    </div>
    
    <h3>📝 Próximos Pasos:</h3>
    <ol>
        <li>Carga tus estadísticas (K/D/A)</li>
        <li>Califica a tus compañeros (opcional)</li>
        <li>Reporta problemas de conducta (si aplica)</li>
    </ol>
    
    <div style="background: #FFF3E0; padding: 15px; border-radius: 5px; margin: 20px 0;">
        <h4>Tu Rendimiento:</h4>
        <ul>
            <li><strong>Rol:</strong> Support</li>
            <li><strong>K/D/A:</strong> 2/5/18 (KDA: 4.0)</li>
            <li><strong>Rating sugerido:</strong> ⭐⭐⭐⭐☆</li>
        </ul>
    </div>
    
    <p style="text-align: center; margin: 30px 0;">
        <a href="https://escrims.com/scrim/abc123/stats" 
           style="background: #2196F3; color: white; padding: 15px 40px; 
                  text-decoration: none; border-radius: 5px;">
            📊 Cargar Estadísticas
        </a>
    </p>
</body>
</html>
```

**Por qué es importante:**
- ✅ Cierra el ciclo del scrim
- ✅ Solicita feedback y estadísticas
- ✅ Mejora la calidad de datos de la plataforma

---

### ✅ **6. Scrim Cancelado**

**Cuándo enviar:**
- El organizador cancela antes de que inicie
- Un jugador clave se retira sin reemplazo

**Asunto del email:**
```
⚠️ Scrim Cancelado - Reembolso Procesado
```

**Cuerpo del email:**
```html
<html>
<body>
    <h2 style="color: #F44336;">⚠️ Scrim Cancelado</h2>
    <p>Hola ProGamer,</p>
    
    <p>Lamentamos informarte que el scrim ha sido cancelado.</p>
    
    <div style="background: #FFEBEE; padding: 15px; border-radius: 5px;">
        <p><strong>🚫 Razón:</strong> Jugador clave desconectado sin reemplazo disponible</p>
    </div>
    
    <h3>📋 Detalles del Scrim Cancelado:</h3>
    <ul>
        <li><strong>Juego:</strong> League of Legends 5v5</li>
        <li><strong>Fecha programada:</strong> 11/11/2025 20:00 hs</li>
        <li><strong>Estado anterior:</strong> Confirmado</li>
    </ul>
    
    <div style="background: #E8F5E9; padding: 15px; border-radius: 5px; margin: 20px 0;">
        <h4>💰 Reembolso:</h4>
        <p>Si habías pagado una entrada, será reembolsada automáticamente en 24-48 horas.</p>
    </div>
    
    <h3>🔍 Scrims Similares Disponibles:</h3>
    <ul>
        <li><a href="https://escrims.com/scrim/def456">LoL 5v5 - Oro/Platino - Mañana 21:00</a></li>
        <li><a href="https://escrims.com/scrim/ghi789">LoL 5v5 - Platino - Pasado mañana 19:00</a></li>
    </ul>
    
    <p style="text-align: center; margin: 30px 0;">
        <a href="https://escrims.com/search" 
           style="background: #4CAF50; color: white; padding: 15px 40px; 
                  text-decoration: none; border-radius: 5px;">
            🔍 Buscar Otros Scrims
        </a>
    </p>
</body>
</html>
```

**Por qué es importante:**
- ✅ Evita que el usuario pierda tiempo esperando
- ✅ Informa sobre reembolsos
- ✅ Sugiere alternativas para retener al usuario

---

## 📊 Resumen: ¿Qué Notificar por Mail?

| **Evento** | **Prioridad** | **Requiere Acción** | **Frecuencia Esperada** |
|---|---|---|---|
| 1. Scrim creado (alertas) | 🟡 Media | No | Diaria (si tiene alertas) |
| 2. Lobby completo | 🔴 Alta | ✅ SÍ (confirmar en 5 min) | Por scrim |
| 3. Partida confirmada | 🟢 Normal | No | Por scrim |
| 4. Partida iniciada | 🔴 Alta | ✅ SÍ (conectarse YA) | Por scrim |
| 5. Partida finalizada | 🟢 Normal | No (pero recomendado) | Por scrim |
| 6. Scrim cancelado | 🔴 Alta | No | Ocasional |

---

## 🎯 Respuesta a tus Preguntas

### **1. ¿Qué es la clase `Notificacion` y en qué nos aporta?**

**Respuesta:**
- Es el **modelo de datos** que representa un mensaje a enviar
- **Aporta:**
  - ✅ Consistencia (todos los notifiers usan el mismo objeto)
  - ✅ Trazabilidad (puedes guardarla en BD)
  - ✅ Reintentos (si falla, puedes reintentar)
  - ✅ Auditoría (sabes qué se envió y cuándo)
  - ✅ Escalabilidad (puedes ponerla en cola)

---

### **2. ¿Qué cosas debería notificar por mail?**

**Respuesta:**
Según el TP, estos **6 eventos obligatorios**:
1. ✅ Scrim creado (con alertas)
2. ✅ Lobby completo (URGENTE - confirmar)
3. ✅ Partida confirmada
4. ✅ Partida iniciada (URGENTE - conectarse)
5. ✅ Partida finalizada
6. ✅ Scrim cancelado

**Todos estos ya están soportados por tu diagrama** con el sistema Observer + NotifierFactory. 🎉

---

**Fecha:** 2025-11-10  
**Versión:** 1.0  
**Estado:** ✅ Completo
