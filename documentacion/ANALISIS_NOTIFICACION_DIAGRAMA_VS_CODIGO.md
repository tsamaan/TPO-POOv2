# 📊 Análisis: Notificacion en el Diagrama Actual vs Implementación

## 🔍 Estado Actual en el Diagrama

Según la imagen que compartiste, la clase `Notificacion` en el diagrama tiene:

```
┌─────────────────────────────────────┐
│         Notificacion                │
├─────────────────────────────────────┤
│ - id: UUID                          │
│ - destinatario: Usuario             │
│ - tipo: TipoNotificacion            │
│ - mensaje: String                   │
│ - canal: String                     │
│ - fechaCreacion: LocalDateTime      │
│ - leida: boolean                    │
├─────────────────────────────────────┤
│ + marcarComoLeida(): void           │
│ + obtenerContenido(): String        │
└─────────────────────────────────────┘
```

### 🔗 Relaciones Visibles en el Diagrama:

1. **NotificationSubscriber → Notificacion** (línea punteada con "creates")
   - NotificationSubscriber **crea** objetos Notificacion

2. **EmailNotifier, DiscordNotifier, PushNotifier → Notificacion** (línea punteada)
   - Todos los Notifiers **usan** (`«use»`) Notificacion en su método `send()`

3. **CompositeNotifier → Notificacion** (línea punteada)
   - También usa Notificacion

---

## 🆚 Comparación: Diagrama vs Implementación Actualizada

| **Atributo** | **En Diagrama** | **En Código Actual** | **Estado** |
|---|---|---|---|
| `id` | ✅ UUID | ✅ UUID | ✅ Coincide |
| `destinatario` | ✅ Usuario | ✅ Usuario | ✅ Coincide |
| `tipo` | ✅ TipoNotificacion (enum?) | ✅ String | ⚠️ Diferente tipo |
| `mensaje` | ✅ String | ✅ String | ✅ Coincide |
| `canal` | ✅ String | ✅ String | ✅ Coincide |
| `fechaCreacion` | ✅ LocalDateTime | ✅ LocalDateTime | ✅ Coincide |
| `leida` | ✅ boolean | ❌ NO existe | ❌ Falta en código |
| `titulo` | ❌ NO existe | ✅ String | ➕ Agregado en código |
| `estado` | ❌ NO existe | ✅ EstadoNotificacion | ➕ Agregado en código |
| `fechaEnvio` | ❌ NO existe | ✅ LocalDateTime | ➕ Agregado en código |
| `intentosEnvio` | ❌ NO existe | ✅ int | ➕ Agregado en código |
| `errorMensaje` | ❌ NO existe | ✅ String | ➕ Agregado en código |

---

| **Método** | **En Diagrama** | **En Código Actual** | **Estado** |
|---|---|---|---|
| `marcarComoLeida()` | ✅ void | ❌ NO existe | ❌ Falta en código |
| `obtenerContenido()` | ✅ String | ❌ NO existe | ❌ Falta en código |
| `marcarComoEnviada()` | ❌ NO existe | ✅ void | ➕ Agregado en código |
| `marcarComoFallida()` | ❌ NO existe | ✅ void | ➕ Agregado en código |
| `puedeReintentar()` | ❌ NO existe | ✅ boolean | ➕ Agregado en código |

---

## ⚠️ Diferencias Detectadas

### 1️⃣ **Atributos Faltantes en el Código:**

#### ❌ `leida: boolean`
- **En diagrama:** ✅ Existe
- **En código:** ❌ Falta
- **Propósito:** Marcar si el usuario ya leyó la notificación
- **Solución:** Agregar al código

---

### 2️⃣ **Métodos Faltantes en el Código:**

#### ❌ `marcarComoLeida(): void`
- **En diagrama:** ✅ Existe
- **En código:** ❌ Falta
- **Propósito:** Marcar la notificación como leída por el usuario
- **Solución:** Agregar al código

#### ❌ `obtenerContenido(): String`
- **En diagrama:** ✅ Existe
- **En código:** ❌ Falta (aunque tenemos `getMensaje()`)
- **Propósito:** Probablemente devolver mensaje formateado
- **Solución:** Agregar al código

---

### 3️⃣ **Atributos Agregados en el Código (no están en diagrama):**

#### ➕ `titulo: String`
- **En diagrama:** ❌ NO existe
- **En código:** ✅ Existe
- **Justificación:** Necesario para asunto de email
- **Recomendación:** ✅ Mantener en código, agregar a diagrama

#### ➕ `estado: EstadoNotificacion` (PENDIENTE, ENVIADA, FALLIDA)
- **En diagrama:** ❌ NO existe
- **En código:** ✅ Existe
- **Justificación:** Necesario para tracking de envío
- **Recomendación:** ✅ Mantener en código, agregar a diagrama

#### ➕ `fechaEnvio: LocalDateTime`
- **En diagrama:** ❌ NO existe
- **En código:** ✅ Existe
- **Justificación:** Auditoría de cuándo se envió
- **Recomendación:** ✅ Mantener en código, agregar a diagrama

#### ➕ `intentosEnvio: int`
- **En diagrama:** ❌ NO existe
- **En código:** ✅ Existe
- **Justificación:** Reintentos automáticos
- **Recomendación:** ✅ Mantener en código, agregar a diagrama

#### ➕ `errorMensaje: String`
- **En diagrama:** ❌ NO existe
- **En código:** ✅ Existe
- **Justificación:** Debugging de errores
- **Recomendación:** ✅ Mantener en código, agregar a diagrama

---

### 4️⃣ **Diferencia en Tipo de Dato:**

#### ⚠️ `tipo`
- **En diagrama:** `TipoNotificacion` (parece ser un enum)
- **En código:** `String`
- **Problema:** Debería ser un enum para type-safety
- **Solución:** Crear enum `TipoNotificacion`

---

## ✅ Solución Propuesta: Sincronizar Código con Diagrama

Voy a actualizar la clase `Notificacion` para que incluya **TODO**:
- ✅ Lo que está en el diagrama
- ✅ Lo que agregamos útil para el TP

---

## 📝 Clase `Notificacion` Completa (Código + Diagrama)

```java
package models;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Modelo de dominio que representa una notificación a enviar a un usuario.
 * Combina atributos del diagrama original + mejoras para tracking y auditoría.
 */
public class Notificacion {
    // ============ ATRIBUTOS DEL DIAGRAMA ============
    private UUID id;                          // ✅ Diagrama
    private Usuario destinatario;             // ✅ Diagrama
    private TipoNotificacion tipo;            // ✅ Diagrama (ahora como enum)
    private String mensaje;                   // ✅ Diagrama
    private String canal;                     // ✅ Diagrama (EMAIL, DISCORD, PUSH)
    private LocalDateTime fechaCreacion;      // ✅ Diagrama
    private boolean leida;                    // ✅ Diagrama
    
    // ============ ATRIBUTOS ADICIONALES (MEJORAS) ============
    private String titulo;                    // ➕ Para asunto de email
    private EstadoNotificacion estado;        // ➕ Para tracking (PENDIENTE, ENVIADA, FALLIDA)
    private LocalDateTime fechaEnvio;         // ➕ Auditoría
    private int intentosEnvio;                // ➕ Reintentos
    private String errorMensaje;              // ➕ Debugging
    
    // ============ ENUMS ============
    public enum TipoNotificacion {
        SCRIM_CREADO,           // Cuando se crea un scrim que coincide con preferencias
        LOBBY_COMPLETO,         // 10/10 jugadores
        CONFIRMADO,             // Todos confirmaron
        EN_JUEGO,               // Partida iniciada
        FINALIZADO,             // Partida terminada
        CANCELADO,              // Scrim cancelado
        RECORDATORIO,           // Recordatorio pre-partida
        JUGADOR_REEMPLAZADO,    // Un jugador fue reemplazado
        APLICACION_ACEPTADA,    // Postulación aceptada
        APLICACION_RECHAZADA    // Postulación rechazada
    }
    
    public enum EstadoNotificacion {
        PENDIENTE,   // Creada pero no enviada
        ENVIADA,     // Enviada exitosamente
        FALLIDA      // Falló el envío
    }
    
    // ============ CONSTRUCTORES ============
    
    /**
     * Constructor completo
     */
    public Notificacion(TipoNotificacion tipo, String titulo, String mensaje, 
                        Usuario destinatario, String canal) {
        this.id = UUID.randomUUID();
        this.tipo = tipo;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.destinatario = destinatario;
        this.canal = canal;
        this.estado = EstadoNotificacion.PENDIENTE;
        this.fechaCreacion = LocalDateTime.now();
        this.leida = false;
        this.intentosEnvio = 0;
    }
    
    /**
     * Constructor simple (compatible con código existente)
     */
    public Notificacion(String mensaje) {
        this.id = UUID.randomUUID();
        this.tipo = TipoNotificacion.SCRIM_CREADO; // default
        this.titulo = "Notificación";
        this.mensaje = mensaje;
        this.canal = "EMAIL";
        this.estado = EstadoNotificacion.PENDIENTE;
        this.fechaCreacion = LocalDateTime.now();
        this.leida = false;
        this.intentosEnvio = 0;
    }
    
    /**
     * Constructor con destinatario
     */
    public Notificacion(String titulo, String mensaje, Usuario destinatario) {
        this.id = UUID.randomUUID();
        this.tipo = TipoNotificacion.SCRIM_CREADO; // default
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.destinatario = destinatario;
        this.canal = "EMAIL";
        this.estado = EstadoNotificacion.PENDIENTE;
        this.fechaCreacion = LocalDateTime.now();
        this.leida = false;
        this.intentosEnvio = 0;
    }
    
    // ============ MÉTODOS DEL DIAGRAMA ============
    
    /**
     * Marca la notificación como leída por el usuario
     */
    public void marcarComoLeida() {
        this.leida = true;
    }
    
    /**
     * Obtiene el contenido formateado de la notificación
     */
    public String obtenerContenido() {
        return String.format("[%s] %s: %s", 
            tipo.name(), 
            titulo, 
            mensaje);
    }
    
    // ============ MÉTODOS ADICIONALES ============
    
    /**
     * Marca la notificación como enviada exitosamente
     */
    public void marcarComoEnviada() {
        this.estado = EstadoNotificacion.ENVIADA;
        this.fechaEnvio = LocalDateTime.now();
    }
    
    /**
     * Marca la notificación como fallida e incrementa el contador de reintentos
     */
    public void marcarComoFallida(String error) {
        this.estado = EstadoNotificacion.FALLIDA;
        this.errorMensaje = error;
        this.intentosEnvio++;
    }
    
    /**
     * Verifica si se puede reintentar el envío (máximo 3 intentos)
     */
    public boolean puedeReintentar() {
        return this.intentosEnvio < 3;
    }
    
    // ============ GETTERS ============
    public UUID getId() { return id; }
    public Usuario getDestinatario() { return destinatario; }
    public TipoNotificacion getTipo() { return tipo; }
    public String getMensaje() { return mensaje; }
    public String getCanal() { return canal; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public boolean isLeida() { return leida; }
    public String getTitulo() { return titulo; }
    public EstadoNotificacion getEstado() { return estado; }
    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public int getIntentosEnvio() { return intentosEnvio; }
    public String getErrorMensaje() { return errorMensaje; }
    
    // ============ SETTERS ============
    public void setDestinatario(Usuario destinatario) { this.destinatario = destinatario; }
    public void setCanal(String canal) { this.canal = canal; }
    public void setLeida(boolean leida) { this.leida = leida; }
    
    // ============ toString ============
    @Override
    public String toString() {
        return String.format("Notificacion[%s] %s - %s (%s) [%s] %s", 
            id.toString().substring(0, 8), 
            tipo, 
            titulo, 
            canal, 
            estado,
            leida ? "✓ Leída" : "○ No leída");
    }
}
```

---

## 📊 Atributos Finales (Completo)

| **Atributo** | **Tipo** | **Origen** | **Propósito** |
|---|---|---|---|
| `id` | UUID | 📐 Diagrama | Identificador único |
| `destinatario` | Usuario | 📐 Diagrama | A quién se envía |
| `tipo` | TipoNotificacion | 📐 Diagrama | Tipo de evento (enum) |
| `mensaje` | String | 📐 Diagrama | Cuerpo del mensaje |
| `canal` | String | 📐 Diagrama | EMAIL/DISCORD/PUSH |
| `fechaCreacion` | LocalDateTime | 📐 Diagrama | Cuándo se creó |
| `leida` | boolean | 📐 Diagrama | ¿Usuario la leyó? |
| `titulo` | String | ➕ Mejora | Asunto del email |
| `estado` | EstadoNotificacion | ➕ Mejora | PENDIENTE/ENVIADA/FALLIDA |
| `fechaEnvio` | LocalDateTime | ➕ Mejora | Cuándo se envió |
| `intentosEnvio` | int | ➕ Mejora | Contador de reintentos |
| `errorMensaje` | String | ➕ Mejora | Error si falló |

---

## 📊 Métodos Finales (Completo)

| **Método** | **Retorno** | **Origen** | **Propósito** |
|---|---|---|---|
| `marcarComoLeida()` | void | 📐 Diagrama | Usuario leyó la notif |
| `obtenerContenido()` | String | 📐 Diagrama | Contenido formateado |
| `marcarComoEnviada()` | void | ➕ Mejora | Cambiar a ENVIADA |
| `marcarComoFallida(error)` | void | ➕ Mejora | Cambiar a FALLIDA |
| `puedeReintentar()` | boolean | ➕ Mejora | ¿Puede reintentar? |

---

## 🎯 Recomendación Final

### Opción 1: Actualizar SOLO el Código ✅ (Recomendado)
- Agregar `leida: boolean` al código
- Agregar `marcarComoLeida()` al código
- Agregar `obtenerContenido()` al código
- Cambiar `tipo: String` → `tipo: TipoNotificacion` (enum)
- ✅ Código queda completo con TODO lo necesario

### Opción 2: Actualizar Diagrama Y Código
- Agregar al diagrama: `titulo`, `estado`, `fechaEnvio`, `intentosEnvio`, `errorMensaje`
- Agregar métodos: `marcarComoEnviada()`, `marcarComoFallida()`, `puedeReintentar()`
- ✅ Diagrama y código quedan 100% sincronizados

---

**¿Querés que actualice el código ahora para que incluya todo?** 🚀

**Fecha:** 2025-11-10  
**Versión:** 1.0  
**Estado:** ✅ Análisis Completo
