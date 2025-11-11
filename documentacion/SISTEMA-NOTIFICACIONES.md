# 🔔 Sistema de Notificaciones - eScrims

## 📋 Canales de Notificación

El sistema ahora tiene **2 canales** de notificación:

### **1. 📧 Email (EmailNotifier)**

**¿Qué es?**  
Envío de correos electrónicos reales a la bandeja de entrada del usuario.

**Cuándo se usa:**
- Usuario se registra → Email de bienvenida
- Nuevo scrim disponible → Email con detalles
- Lobby completo (10/10) → Email confirmando
- Scrim confirmado → Email recordatorio
- Partida finalizada → Email con resultados

**Ejemplo:**
```
Para: jugador@gmail.com
Asunto: 🎮 Nuevo Scrim Disponible

¡Nuevo scrim de Valorant!

Detalles:
- Rango: 1500-1700 MMR
- Fecha: 2025-11-11 20:00
- Jugadores: 8/10

¡Postúlate ahora!
```

**Tecnología:**
- Endpoint: `https://send-email-zeta.vercel.app/send-email`
- Método: HTTP POST
- Formato: JSON

---

### **2. 📱 Push (PushNotifier)**

**¿Qué es?**  
Notificaciones emergentes que aparecen en el dispositivo del usuario (celular o computadora), incluso si la app está cerrada.

**Cuándo se usa:**
- Notificaciones urgentes/inmediatas
- Recordatorios 15 minutos antes del scrim
- Lobby completo (todos listos)
- Partida iniciada

**Ejemplos en la vida real:**

🎮 **WhatsApp:**
```
┌─────────────────────────────┐
│ 📱 WhatsApp                 │
│ Juan: Hola, ¿cómo estás?   │
└─────────────────────────────┘
```

⚽ **Apps deportivas:**
```
┌─────────────────────────────┐
│ 🏆 ESPN                     │
│ ¡GOL! Argentina 2-1 Brasil │
└─────────────────────────────┘
```

🎮 **En eScrims:**
```
┌─────────────────────────────┐
│ 🎮 eScrims                  │
│ ¡Lobby completo! (10/10)   │
│ La partida inicia en 5 min │
└─────────────────────────────┘
```

**Estado actual:**
- Implementado como **simulación** (muestra en consola)
- Para producción real se usaría:
  - Firebase Cloud Messaging (FCM) - Android/iOS
  - Apple Push Notification Service (APNs) - iOS
  - Web Push API - Navegadores

**Código actual (simulación):**
```java
public class PushNotifier implements INotifier {
    @Override
    public void sendNotification(Notificacion notificacion) {
        System.out.println("📱 [PUSH] " + notificacion.getMensaje());
        // En producción: enviaría via FCM/APNs
    }
}
```

---

## ❌ Canales Eliminados

### **Discord (ELIMINADO)**

**Razón:** No es necesario para el sistema actual.

Discord era un canal que enviaba mensajes a servidores de Discord, pero se decidió mantener solo **Email** (principal) y **Push** (urgente).

---

## 🏗️ Arquitectura del Sistema

### **Patrón Observer + Factory**

```
NotificationService (Observable)
        │
        ├─ EmailNotifier (Observer) ← ✅ EMAILS REALES
        └─ PushNotifier (Observer)  ← 📱 SIMULADO (para producción)
```

### **Flujo de Notificación:**

```
1. Evento ocurre (ej: scrim creado)
        ↓
2. NotificationService.notificarJugadores()
        ↓
3. Se crea Notificacion con:
   - Tipo: SCRIM_CREADO
   - Mensaje: "Nuevo scrim de Valorant..."
   - Destinatario: Usuario
        ↓
4. Se envía por TODOS los canales:
        ├─ EmailNotifier → Envía email real vía HTTP
        └─ PushNotifier → Muestra en consola (simulación)
```

---

## 📊 Comparación de Canales

| Característica | Email 📧 | Push 📱 |
|----------------|----------|---------|
| **Urgencia** | Media | Alta |
| **Visibilidad** | Bandeja de entrada | Notificación emergente |
| **Requiere conexión** | Sí | Sí |
| **Persistencia** | Permanente (inbox) | Temporal (desaparece) |
| **Mejor para** | Info detallada | Alertas urgentes |
| **Implementación** | ✅ REAL (Vercel) | 🔨 SIMULADA (consola) |

---

## 🎯 Tipos de Notificaciones

### **Cada tipo tiene título y comportamiento específico:**

| Tipo | Título | Email | Push | Uso |
|------|--------|-------|------|-----|
| `SCRIM_CREADO` | 🎮 Nuevo Scrim Disponible | ✅ | ✅ | Scrim disponible |
| `LOBBY_COMPLETO` | ✅ Lobby Completo - 10/10 | ✅ | ✅ | Todos los slots llenos |
| `CONFIRMADO` | 🎯 Scrim Confirmado | ✅ | ✅ | Todos confirmaron |
| `EN_JUEGO` | ⚔️ Partida Iniciada | ✅ | ✅ | Partida en curso |
| `FINALIZADO` | 🏆 Partida Finalizada | ✅ | ❌ | Resultados finales |
| `CANCELADO` | ❌ Scrim Cancelado | ✅ | ✅ | Scrim cancelado |
| `RECORDATORIO` | ⏰ Recordatorio de Scrim | ✅ | ✅ | 15 min antes |

---

## 💻 Código de Ejemplo

### **Enviar notificación a un usuario:**

```java
// 1. Crear el servicio
NotificationService notificationService = new NotificationService();

// 2. Crear usuario destinatario
Usuario jugador = new Usuario(1, "Teo", "teo@gmail.com");

// 3. Enviar notificación
notificationService.enviarNotificacion(
    jugador,
    "SCRIM_CREADO",
    "Nuevo scrim de Valorant disponible!\n" +
    "Rango: 1500-1700 MMR\n" +
    "Fecha: Hoy 20:00"
);
```

**Resultado:**
```
✅ [EMAIL] Enviado a: teo@gmail.com
   Asunto: 🎮 Nuevo Scrim Disponible
📱 [PUSH] Nuevo scrim de Valorant disponible!
```

### **Notificar a múltiples jugadores:**

```java
List<Usuario> jugadores = scrim.getJugadores();

notificationService.notificarJugadores(
    jugadores,
    "LOBBY_COMPLETO",
    "¡Lobby completo! (10/10 jugadores)\n" +
    "La partida inicia en 5 minutos."
);
```

**Resultado:**
```
[ENVIANDO A 10 JUGADORES]

✅ [EMAIL] Enviado a: jugador1@gmail.com
📱 [PUSH] ¡Lobby completo! (10/10 jugadores)

✅ [EMAIL] Enviado a: jugador2@gmail.com
📱 [PUSH] ¡Lobby completo! (10/10 jugadores)

... (x10)
```

---

## 🔧 Implementación Técnica

### **Factory Pattern:**

```java
// NotifierFactory (Abstract Factory)
public abstract class NotifierFactory {
    public abstract INotifier createEmailNotifier();
    public abstract INotifier createPushNotifier();
}

// SimpleNotifierFactory (Concrete Factory)
public class SimpleNotifierFactory extends NotifierFactory {
    @Override
    public INotifier createEmailNotifier() {
        return new EmailNotifier(); // ← Email real
    }
    
    @Override
    public INotifier createPushNotifier() {
        return new PushNotifier(); // ← Push simulado
    }
}
```

### **Observer Pattern:**

```java
public class NotificationService {
    private List<INotifier> notifiers; // ← Lista de observers
    
    public NotificationService() {
        this.notifiers = new ArrayList<>();
        
        // Registrar observers
        this.notifiers.add(factory.createEmailNotifier());
        this.notifiers.add(factory.createPushNotifier());
    }
    
    // Notificar a todos los observers
    public void enviarNotificacion(Usuario usuario, String tipo, String mensaje) {
        Notificacion notif = new Notificacion(tipo, mensaje, usuario);
        
        for (INotifier notifier : notifiers) {
            notifier.sendNotification(notif); // ← Cada observer envía
        }
    }
}
```

---

## 🚀 Para Producción Real

### **Email: ✅ YA FUNCIONA**
```
Endpoint: https://send-email-zeta.vercel.app/send-email
Estado: PRODUCCIÓN
```

### **Push: 🔨 IMPLEMENTAR**

Para implementar Push real, se necesitaría:

**1. Backend (Firebase/APNs):**
```java
public class PushNotifier implements INotifier {
    private FirebaseMessaging firebaseMessaging;
    
    @Override
    public void sendNotification(Notificacion notificacion) {
        Message message = Message.builder()
            .setNotification(Notification.builder()
                .setTitle(notificacion.getTitulo())
                .setBody(notificacion.getMensaje())
                .build())
            .setToken(usuario.getDeviceToken()) // Token del dispositivo
            .build();
        
        firebaseMessaging.send(message);
    }
}
```

**2. Frontend (registrar dispositivo):**
```java
// Guardar token del dispositivo al registrar usuario
usuario.setDeviceToken("fcm-token-123..."); 
```

**3. Permisos:**
- AndroidManifest.xml (Android)
- Info.plist (iOS)
- Service Worker (Web)

---

## 📝 Resumen

| Aspecto | Detalle |
|---------|---------|
| **Canales activos** | Email (real) + Push (simulado) |
| **Canales eliminados** | Discord ❌ |
| **Email** | ✅ Funcional vía Vercel endpoint |
| **Push** | 🔨 Simulado (consola), listo para producción |
| **Patrones** | Observer + Abstract Factory + Composite |
| **Tipos** | 11 tipos de notificaciones con emojis |

---

## 🎮 Diferencias Clave

### **Email vs Push:**

**Email:**
- 📧 Permanente (queda en bandeja)
- 📝 Mejor para información detallada
- ⏱️ No es urgente
- 💾 Se puede leer después
- ✅ **YA FUNCIONA EN PRODUCCIÓN**

**Push:**
- 📱 Temporal (notificación emergente)
- ⚡ Mejor para alertas urgentes
- 🔔 Llama la atención inmediatamente
- ⏰ Ideal para recordatorios
- 🔨 **SIMULADO (para implementar después)**

---

**Última actualización:** 10/11/2025  
**Estado:** ✅ SISTEMA SIMPLIFICADO (Solo Email + Push)
