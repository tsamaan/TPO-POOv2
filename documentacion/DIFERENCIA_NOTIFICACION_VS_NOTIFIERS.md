# 🔍 Diferencia entre `Notificacion` y los `Notifiers`

## ❓ La Confusión Común

**Pregunta:** Si ya tengo `EmailNotifier`, `DiscordNotifier` y `PushNotifier`, ¿para qué necesito la clase `Notificacion`?

---

## 🎯 Respuesta Corta

| **Concepto** | **¿Qué es?** | **Responsabilidad** |
|---|---|---|
| **`Notificacion`** | 📦 **DATOS** (DTO) | **QUÉ** enviar (mensaje, título, destinatario) |
| **`EmailNotifier`** | ⚙️ **SERVICIO** (comportamiento) | **CÓMO** enviar por email |
| **`DiscordNotifier`** | ⚙️ **SERVICIO** (comportamiento) | **CÓMO** enviar por Discord |
| **`PushNotifier`** | ⚙️ **SERVICIO** (comportamiento) | **CÓMO** enviar por push |

---

## 📊 Analogía del Mundo Real

### 🚚 **Sistema de Envío de Paquetes**

Imagina que querés enviar un paquete:

| **Concepto en Sistema de Paquetes** | **Equivalente en nuestro Sistema** |
|---|---|
| 📦 **El paquete** (caja con contenido, dirección, remitente) | `Notificacion` (datos: mensaje, destinatario, título) |
| 🚚 **Empresa de transporte: Correo Argentino** | `EmailNotifier` (envía por email) |
| ✈️ **Empresa de transporte: FedEx** | `DiscordNotifier` (envía por Discord) |
| 🏍️ **Empresa de transporte: Moto mensajero** | `PushNotifier` (envía por push) |

**El paquete (Notificacion)** siempre tiene:
- 📝 Contenido (mensaje)
- 📍 Destinatario (usuario)
- 🏷️ Etiqueta (título)

**Las empresas de transporte (Notifiers)** saben **CÓMO** llevar el paquete:
- 🚚 Correo Argentino: camión + correo físico
- ✈️ FedEx: avión + seguimiento online
- 🏍️ Moto mensajero: moto + entrega inmediata

---

## 🔬 Análisis Técnico Detallado

### 1️⃣ **`Notificacion` es un MODELO DE DATOS (DTO)**

```java
// Notificacion.java - SOLO DATOS
public class Notificacion {
    // ✅ TIENE datos
    private String titulo;
    private String mensaje;
    private Usuario destinatario;
    private String canal;
    private EstadoNotificacion estado;
    
    // ✅ TIENE estado (PENDIENTE, ENVIADA, FALLIDA)
    public void marcarComoEnviada() { ... }
    public void marcarComoFallida(String error) { ... }
    
    // ❌ NO SABE cómo enviar
    // ❌ NO tiene conexión a servidores SMTP/Discord/Firebase
    // ❌ NO maneja protocolos de comunicación
}
```

**Responsabilidad:**
- ✅ Almacenar información del mensaje
- ✅ Rastrear estado (enviado/fallido)
- ✅ Persistirse en base de datos
- ❌ **NO envía nada**

---

### 2️⃣ **`EmailNotifier` es un SERVICIO (Behavior)**

```java
// EmailNotifier.java - COMPORTAMIENTO
public class EmailNotifier implements Notifier {
    // ✅ SABE cómo enviar por email
    @Override
    public void send(Notificacion notificacion) {
        // 📧 Conexión SMTP
        String smtpServer = "smtp.gmail.com";
        int port = 587;
        
        // 🔐 Autenticación
        String username = "escrims@gmail.com";
        String password = "secreto123";
        
        // 📝 Construcción del email
        String asunto = notificacion.getTitulo();
        String cuerpo = generarHTML(notificacion.getMensaje());
        String destinatario = notificacion.getDestinatario().getEmail();
        
        // 🚀 ENVÍO REAL
        JavaMailSender.send(destinatario, asunto, cuerpo);
        
        // ✅ Actualizar estado
        notificacion.marcarComoEnviada();
    }
    
    private String generarHTML(String mensaje) {
        return "<html><body>" + mensaje + "</body></html>";
    }
}
```

**Responsabilidad:**
- ✅ Conectarse al servidor SMTP
- ✅ Autenticarse
- ✅ Formatear email como HTML
- ✅ Enviar el mensaje
- ✅ Manejar errores de conexión

---

### 3️⃣ **`DiscordNotifier` es un SERVICIO (Behavior)**

```java
// DiscordNotifier.java - COMPORTAMIENTO
public class DiscordNotifier implements Notifier {
    // ✅ SABE cómo enviar por Discord
    @Override
    public void send(Notificacion notificacion) {
        // 🌐 Webhook de Discord
        String webhookUrl = "https://discord.com/api/webhooks/123456789";
        
        // 📝 Construcción del mensaje Discord
        String mensajeMarkdown = generarMarkdown(notificacion);
        
        // 📦 JSON payload
        String json = """
        {
            "content": "%s",
            "embeds": [{
                "title": "%s",
                "description": "%s",
                "color": 3447003
            }]
        }
        """.formatted(
            notificacion.getTitulo(),
            notificacion.getTitulo(),
            notificacion.getMensaje()
        );
        
        // 🚀 ENVÍO REAL vía HTTP POST
        HttpClient.post(webhookUrl, json);
        
        // ✅ Actualizar estado
        notificacion.marcarComoEnviada();
    }
    
    private String generarMarkdown(Notificacion notif) {
        return "**" + notif.getTitulo() + "**\n" + notif.getMensaje();
    }
}
```

**Responsabilidad:**
- ✅ Conectarse al webhook de Discord
- ✅ Formatear mensaje como Markdown
- ✅ Crear JSON payload con embeds
- ✅ Enviar vía HTTP POST
- ✅ Manejar errores de API

---

### 4️⃣ **`PushNotifier` es un SERVICIO (Behavior)**

```java
// PushNotifier.java - COMPORTAMIENTO
public class PushNotifier implements Notifier {
    // ✅ SABE cómo enviar notificaciones push
    @Override
    public void send(Notificacion notificacion) {
        // 🔥 Firebase Cloud Messaging
        String serverKey = "AIzaSyD...";
        String fcmUrl = "https://fcm.googleapis.com/fcm/send";
        
        // 📱 Token del dispositivo del usuario
        String deviceToken = notificacion.getDestinatario().getDeviceToken();
        
        // 📦 JSON payload para Firebase
        String json = """
        {
            "to": "%s",
            "notification": {
                "title": "%s",
                "body": "%s",
                "icon": "ic_notification",
                "sound": "default"
            },
            "priority": "high"
        }
        """.formatted(
            deviceToken,
            notificacion.getTitulo(),
            notificacion.getMensaje()
        );
        
        // 🚀 ENVÍO REAL a Firebase
        HttpClient.post(fcmUrl, json, headers("Authorization", serverKey));
        
        // ✅ Actualizar estado
        notificacion.marcarComoEnviada();
    }
}
```

**Responsabilidad:**
- ✅ Conectarse a Firebase Cloud Messaging
- ✅ Obtener device token del usuario
- ✅ Crear payload JSON para Firebase
- ✅ Enviar vía HTTP POST con autenticación
- ✅ Manejar errores de Firebase

---

## 🎭 Separación de Responsabilidades (SOLID - Single Responsibility)

### ❌ **MAL - Todo en una sola clase**

```java
// ❌ ANTI-PATRÓN: Clase Dios (God Object)
public class Notificacion {
    private String mensaje;
    private Usuario destinatario;
    
    // ❌ MEZCLA datos con comportamiento
    public void enviarPorEmail() {
        // Código SMTP aquí...
        JavaMailSender.send(...);
    }
    
    public void enviarPorDiscord() {
        // Código Discord aquí...
        HttpClient.post(...);
    }
    
    public void enviarPorPush() {
        // Código Firebase aquí...
        FirebaseMessaging.send(...);
    }
}
```

**Problemas:**
- 🔴 Viola SRP (Single Responsibility Principle)
- 🔴 Difícil de testear
- 🔴 Si cambia SMTP, debes tocar la clase de datos
- 🔴 No puedes cambiar estrategia de envío fácilmente

---

### ✅ **BIEN - Separación de responsabilidades**

```java
// ✅ Notificacion - SOLO DATOS
public class Notificacion {
    private String titulo;
    private String mensaje;
    private Usuario destinatario;
    // NO tiene métodos de envío
}

// ✅ EmailNotifier - SOLO ENVÍO POR EMAIL
public class EmailNotifier implements Notifier {
    public void send(Notificacion notif) { /* SMTP */ }
}

// ✅ DiscordNotifier - SOLO ENVÍO POR DISCORD
public class DiscordNotifier implements Notifier {
    public void send(Notificacion notif) { /* Webhook */ }
}

// ✅ PushNotifier - SOLO ENVÍO POR PUSH
public class PushNotifier implements Notifier {
    public void send(Notificacion notif) { /* Firebase */ }
}
```

**Beneficios:**
- ✅ Cumple SRP (cada clase una responsabilidad)
- ✅ Fácil de testear (mock de Notifiers)
- ✅ Puedes cambiar SMTP sin tocar Notificacion
- ✅ Puedes agregar nuevos canales (SMSNotifier, WhatsAppNotifier)

---

## 🔄 Flujo Completo: ¿Cómo Trabajan Juntos?

```
┌─────────────────────────────────────────────────────────────┐
│                    FLUJO DE NOTIFICACIÓN                     │
└─────────────────────────────────────────────────────────────┘

1️⃣ Se cambia el estado del Scrim (ej: Lobby completo)
   │
   ▼
┌──────────────────────────────────────┐
│ ScrimContext.notificarCambio()       │
│ - Detecta cambio de estado           │
└──────────────────────────────────────┘
   │
   ▼
2️⃣ Publica evento en el bus
   │
   ▼
┌──────────────────────────────────────┐
│ DomainEventBus.publish(event)        │
│ - Notifica a todos los subscribers   │
└──────────────────────────────────────┘
   │
   ▼
3️⃣ El subscriber recibe el evento
   │
   ▼
┌──────────────────────────────────────┐
│ NotificationSubscriber.onEvent()     │
│ - Crea objeto Notificacion           │ ← 📦 CREA LOS DATOS
└──────────────────────────────────────┘
   │
   │ crea ↓
   │
   ▼
┌──────────────────────────────────────┐
│ Notificacion                         │
│ ├─ titulo: "Lobby Completo"          │ ← 📦 DATOS
│ ├─ mensaje: "10/10 jugadores"        │
│ ├─ destinatario: Usuario             │
│ ├─ canal: "EMAIL"                    │
│ └─ estado: PENDIENTE                 │
└──────────────────────────────────────┘
   │
   │ pasa a ↓
   │
4️⃣ Decide qué notifier usar según canal
   │
   ├─────────┬─────────┬─────────┐
   ▼         ▼         ▼         ▼
┌──────┐ ┌────────┐ ┌──────┐ ┌─────────┐
│Email │ │Discord │ │Push  │ │Composite│ ← ⚙️ SERVICIOS
│Notif │ │Notif   │ │Notif │ │Notif    │
└──────┘ └────────┘ └──────┘ └─────────┘
   │         │         │         │
   ▼         ▼         ▼         ▼
  SMTP    Webhook   Firebase  (Todos)   ← 🚀 ENVÍO REAL
   │         │         │
   └─────────┴─────────┘
             │
5️⃣ Actualiza estado de Notificacion
             ▼
   ┌──────────────────────────┐
   │ notificacion             │
   │ .marcarComoEnviada()     │ ← 📦 ACTUALIZA DATOS
   │                          │
   │ estado: ENVIADA ✅       │
   │ fechaEnvio: 2025-11-10   │
   └──────────────────────────┘
```

---

## 🎯 Ejemplo Práctico Completo

```java
// ============ PASO 1: Crear la notificación (DATOS) ============
Notificacion notif = new Notificacion(
    "LOBBY_COMPLETO",                           // tipo
    "🎮 Lobby Completo - Confirma tu participación",  // titulo
    "El lobby está completo (10/10 jugadores). Tienes 5 minutos para confirmar.", // mensaje
    usuario,                                    // destinatario
    "EMAIL"                                     // canal
);

System.out.println("Estado inicial: " + notif.getEstado()); 
// → PENDIENTE

// ============ PASO 2: Enviar por email (SERVICIO) ============
EmailNotifier emailNotifier = new EmailNotifier();
emailNotifier.send(notif);

// Dentro de EmailNotifier.send():
// - Se conecta a SMTP
// - Genera HTML: <html><body>El lobby está completo...</body></html>
// - Envía a usuario.getEmail()
// - Llama a notif.marcarComoEnviada()

System.out.println("Estado después de enviar: " + notif.getEstado()); 
// → ENVIADA

// ============ PASO 3: Si queremos enviar por otro canal ============
// MISMO objeto Notificacion, DIFERENTE servicio

DiscordNotifier discordNotifier = new DiscordNotifier();
discordNotifier.send(notif);

// Dentro de DiscordNotifier.send():
// - Genera Markdown: **🎮 Lobby Completo**\nEl lobby está completo...
// - Crea JSON con embeds
// - POST a webhook de Discord

PushNotifier pushNotifier = new PushNotifier();
pushNotifier.send(notif);

// Dentro de PushNotifier.send():
// - Obtiene deviceToken del usuario
// - Crea JSON para Firebase
// - POST a Firebase Cloud Messaging

// ============ PASO 4: Enviar por TODOS los canales ============
CompositeNotifier allNotifiers = new CompositeNotifier();
allNotifiers.addNotifier(emailNotifier);
allNotifiers.addNotifier(discordNotifier);
allNotifiers.addNotifier(pushNotifier);

allNotifiers.send(notif);
// → Envía por Email, Discord Y Push al mismo tiempo
```

---

## 📊 Tabla Comparativa Final

| **Aspecto** | **`Notificacion`** | **`EmailNotifier`** | **`DiscordNotifier`** | **`PushNotifier`** |
|---|---|---|---|---|
| **Tipo de clase** | 📦 Modelo/DTO | ⚙️ Servicio | ⚙️ Servicio | ⚙️ Servicio |
| **Responsabilidad** | Almacenar datos | Enviar por email | Enviar por Discord | Enviar por push |
| **Tiene datos** | ✅ Sí | ❌ No | ❌ No | ❌ No |
| **Tiene comportamiento de envío** | ❌ No | ✅ Sí (SMTP) | ✅ Sí (Webhook) | ✅ Sí (Firebase) |
| **Persiste en BD** | ✅ Sí | ❌ No | ❌ No | ❌ No |
| **Implementa interfaz** | ❌ No | ✅ Notifier | ✅ Notifier | ✅ Notifier |
| **Puede cambiar estado** | ✅ Sí (PENDIENTE→ENVIADA) | ❌ No | ❌ No | ❌ No |
| **Sabe conectarse a servidores** | ❌ No | ✅ Sí (SMTP) | ✅ Sí (Discord API) | ✅ Sí (Firebase) |
| **Maneja reintentos** | ✅ Sí (tracking) | ❌ No | ❌ No | ❌ No |
| **Puede ser testeada con mocks** | ✅ Sí (solo datos) | ✅ Sí (mock SMTP) | ✅ Sí (mock HTTP) | ✅ Sí (mock Firebase) |

---

## 🎯 Resumen Ultra-Corto

```
┌─────────────────────────────────────────────────────────┐
│                                                          │
│  Notificacion = 📦 "QUÉ enviar" (DATOS)                │
│                                                          │
│  EmailNotifier = ⚙️ "CÓMO enviar por email" (SERVICIO) │
│  DiscordNotifier = ⚙️ "CÓMO enviar por Discord"        │
│  PushNotifier = ⚙️ "CÓMO enviar por push"              │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

**Analogía final:**
- 📦 `Notificacion` = **La carta** (con contenido, destinatario, remitente)
- ⚙️ `EmailNotifier` = **Correo Argentino** (servicio que ENTREGA la carta)
- ⚙️ `DiscordNotifier` = **Mensajero privado** (servicio alternativo)
- ⚙️ `PushNotifier` = **Drone de entrega** (servicio ultra-rápido)

**La carta NO se envía a sí misma. Necesita un servicio de transporte.**

---

**Fecha:** 2025-11-10  
**Versión:** 1.0  
**Estado:** ✅ Completo
