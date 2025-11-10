# 🔗 Relaciones de la Clase `Notificacion` en el Diagrama UML

## ❓ Pregunta: ¿Notificacion depende de Notifiers? ¿Usuario tiene muchas Notificaciones?

---

## 📊 Respuesta Rápida

| **Relación** | **¿Es Correcta?** | **Tipo de Relación** | **Cardinalidad** |
|---|---|---|---|
| `Notificacion` → `Notifiers` | ❌ **NO** | Ninguna (opuesta) | N/A |
| `Notifiers` → `Notificacion` | ✅ **SÍ** | **Dependencia** (`«use»`) | N/A |
| `Usuario` → `Notificacion` | ✅ **SÍ** | **Asociación** | `1` a `0..*` |

---

## 🔄 **Relación 1: Notificacion vs Notifiers**

### ❌ **INCORRECTO:**
```
┌─────────────┐              ┌─────────────┐
│Notificacion │─────────────>│EmailNotifier│
│             │   depende    │             │
└─────────────┘              └─────────────┘
```
**NO, esto está AL REVÉS.**

---

### ✅ **CORRECTO:**
```
┌─────────────┐              ┌─────────────┐
│EmailNotifier│─────────────>│Notificacion │
│             │   «use»      │             │
└─────────────┘              └─────────────┘
```

**Explicación:**
- `Notificacion` es un **modelo de datos** (DTO)
- `EmailNotifier` es un **servicio** que **USA** ese DTO
- La **dependencia va** de `EmailNotifier` → `Notificacion`
- **NO** de `Notificacion` → `EmailNotifier`

---

## 🔍 Análisis del Código

### ❌ **Si Notificacion dependiera de Notifiers (INCORRECTO):**

```java
// ❌ ANTI-PATRÓN - Notificacion.java
package models;

import notifiers.EmailNotifier;  // ← MAL: modelo importa servicio
import notifiers.DiscordNotifier;

public class Notificacion {
    private String mensaje;
    
    // ❌ MAL: Modelo tiene lógica de envío
    public void enviar() {
        EmailNotifier email = new EmailNotifier();
        email.send(this);  // ← Acoplamiento fuerte
    }
}
```

**Problemas:**
- 🔴 Viola SRP (Single Responsibility)
- 🔴 Modelo depende de servicios (inversión de dependencias)
- 🔴 Difícil de testear
- 🔴 No puedes cambiar notifiers sin tocar el modelo

---

### ✅ **La realidad (CORRECTO):**

```java
// ✅ CORRECTO - Notificacion.java
package models;

// ✅ NO importa notifiers
public class Notificacion {
    private String titulo;
    private String mensaje;
    private Usuario destinatario;
    
    // ✅ Solo datos y estado
    public void marcarComoEnviada() { ... }
}
```

```java
// ✅ CORRECTO - EmailNotifier.java
package notifiers;

import models.Notificacion;  // ← BIEN: servicio importa modelo

public class EmailNotifier implements Notifier {
    @Override
    public void send(Notificacion notificacion) {  // ← USA Notificacion
        String titulo = notificacion.getTitulo();
        String mensaje = notificacion.getMensaje();
        // ... enviar por SMTP
    }
}
```

**Por qué está bien:**
- ✅ Modelo NO sabe de servicios
- ✅ Servicio SÍ conoce el modelo (dependencia correcta)
- ✅ Puedes cambiar notifiers sin tocar Notificacion
- ✅ Fácil de testear (mock de notifiers)

---

## 🎯 **Dirección de la Dependencia**

### Regla General:
```
┌────────────────────────────────────────────────┐
│                                                 │
│  Las dependencias van de:                      │
│                                                 │
│  Capa de Servicio → Capa de Modelo            │
│  (Alta)              (Baja)                     │
│                                                 │
│  EmailNotifier → Notificacion                  │
│  (Servicio)      (Modelo)                       │
│                                                 │
└────────────────────────────────────────────────┘
```

**Nunca al revés:**
- ❌ Modelo NO debe depender de Servicios
- ❌ Modelo NO debe depender de Infraestructura
- ❌ Notificacion NO debe importar EmailNotifier

---

## 🔗 **Relación 2: Usuario vs Notificacion**

### ✅ **CORRECTO:**
```
┌─────────────┐              ┌─────────────┐
│  Usuario    │ 1         *  │Notificacion │
│             │─────────────>│             │
│ - username  │ destinatario │ - titulo    │
│ - email     │              │ - mensaje   │
└─────────────┘              └─────────────┘
```

**Cardinalidad:** `1` a `0..*`
- Un `Usuario` puede tener **0 o muchas** notificaciones
- Una `Notificacion` tiene **exactamente 1** destinatario (Usuario)

---

## 📝 Código Real

```java
// Usuario.java
public class Usuario {
    private String username;
    private String email;
    
    // ✅ Puede tener lista de notificaciones (opcional)
    private List<Notificacion> notificaciones;  // 1 → 0..*
    
    public void agregarNotificacion(Notificacion notif) {
        this.notificaciones.add(notif);
    }
}
```

```java
// Notificacion.java
public class Notificacion {
    private Usuario destinatario;  // * → 1
    
    public Notificacion(String titulo, String mensaje, Usuario destinatario) {
        this.destinatario = destinatario;  // ✅ Referencia al Usuario
    }
}
```

---

## 📊 **Diagrama UML Completo de Relaciones**

```
┌──────────────────────────────────────────────────────────────────┐
│                    RELACIONES DE NOTIFICACION                     │
└──────────────────────────────────────────────────────────────────┘

                    ┌─────────────────┐
                    │    Usuario      │
                    ├─────────────────┤
                    │ - username      │
                    │ - email         │
                    │ - notificaciones│───┐
                    └─────────────────┘   │
                            │              │
                            │ 1            │ tiene
                            │              │
                            ▼              │
                    ┌─────────────────┐   │
                    │  Notificacion   │◀──┘
                    ├─────────────────┤   0..*
                    │ - id            │
                    │ - titulo        │
                    │ - mensaje       │
                    │ - destinatario ─┼──┐ (referencia a Usuario)
                    │ - canal         │  │
                    │ - estado        │  │
                    └─────────────────┘  │
                            ▲             │
                            │             │
                            │             │
                  ┌─────────┼─────────┬───┘
                  │ «use»   │ «use»   │ «use»
                  │         │         │
            ┌─────┴────┐ ┌──┴──────┐ ┌┴─────────┐
            │  Email   │ │ Discord │ │   Push   │
            │ Notifier │ │ Notifier│ │ Notifier │
            └──────────┘ └─────────┘ └──────────┘
                  ▲           ▲           ▲
                  │           │           │
                  └───────────┴───────────┘
                            │
                    ┌───────┴────────┐
                    │ <<interface>>  │
                    │    Notifier    │
                    ├────────────────┤
                    │ + send(Notif)  │
                    └────────────────┘
```

---

## 📐 **Tabla de Relaciones UML**

| **Desde** | **Hacia** | **Tipo** | **Cardinalidad** | **Nombre** | **Dirección** |
|---|---|---|---|---|---|
| `Usuario` | `Notificacion` | **Asociación** | `1` → `0..*` | `tiene` | Usuario → Notificacion |
| `Notificacion` | `Usuario` | **Asociación** | `*` → `1` | `destinatario` | Notificacion → Usuario |
| `EmailNotifier` | `Notificacion` | **Dependencia** | N/A | `«use»` | EmailNotifier → Notificacion |
| `DiscordNotifier` | `Notificacion` | **Dependencia** | N/A | `«use»` | DiscordNotifier → Notificacion |
| `PushNotifier` | `Notificacion` | **Dependencia** | N/A | `«use»` | PushNotifier → Notificacion |
| `CompositeNotifier` | `Notificacion` | **Dependencia** | N/A | `«use»` | CompositeNotifier → Notificacion |

---

## 🎯 **Tipos de Relaciones UML**

### 1️⃣ **Asociación (línea continua con flecha)**
```
Usuario ────────> Notificacion
   1              0..*
```
- **Significado:** Usuario "tiene" notificaciones
- **En código:** Atributo `List<Notificacion> notificaciones` en Usuario
- **Navegación:** Desde Usuario puedes acceder a sus Notificaciones

---

### 2️⃣ **Dependencia (línea punteada con flecha)**
```
EmailNotifier ·······> Notificacion
           «use»
```
- **Significado:** EmailNotifier "usa" Notificacion como parámetro
- **En código:** `public void send(Notificacion notif)`
- **Navegación:** Solo en tiempo de ejecución (parámetro de método)

---

## 🔍 **¿Cómo Identificar la Dirección?**

### Pregunta Clave: "¿Quién importa a quién?"

```java
// EmailNotifier.java
package notifiers;

import models.Notificacion;  // ← EmailNotifier IMPORTA Notificacion

public class EmailNotifier {
    public void send(Notificacion notif) {  // ← RECIBE Notificacion
        // usa notif...
    }
}
```

**Conclusión:** La dependencia va de `EmailNotifier` → `Notificacion`

---

```java
// Notificacion.java
package models;

// ❌ NO importa notifiers

public class Notificacion {
    // NO usa EmailNotifier en ningún método
}
```

**Conclusión:** Notificacion NO depende de EmailNotifier

---

## ✅ **Respuesta Final a tus Preguntas**

### 1️⃣ **"¿Notificacion depende de Notifiers?"**
**❌ NO**
- La dependencia es **al revés**
- `EmailNotifier`, `DiscordNotifier`, `PushNotifier` **dependen** de `Notificacion`
- `Notificacion` NO conoce a los Notifiers

---

### 2️⃣ **"¿Usuario tiene muchas Notificaciones?"**
**✅ SÍ**
- Relación: **Asociación** `1` a `0..*`
- Un Usuario puede tener 0, 1, 2, 3... N notificaciones
- Una Notificacion tiene exactamente 1 destinatario (Usuario)

---

## 📐 **XML para el Diagrama**

### Relación: Usuario → Notificacion (Asociación)
```xml
<!-- Asociación bidireccional Usuario-Notificacion -->
<mxCell id="Usuario-Notificacion" 
        value="tiene" 
        style="endArrow=open;html=1;endSize=12;startArrow=none;startSize=0;" 
        edge="1" 
        parent="1" 
        source="Usuario" 
        target="Notificacion">
    <mxGeometry relative="1" as="geometry"/>
</mxCell>

<!-- Multiplicidad 1 (Usuario) -->
<mxCell id="Usuario-Notificacion-mult1" 
        value="1" 
        style="edgeLabel;resizable=0;html=1;align=right;verticalAlign=top;" 
        connectable="0" 
        vertex="1" 
        parent="Usuario-Notificacion">
    <mxGeometry x="-1" relative="1" as="geometry">
        <mxPoint x="-10" y="-10" as="offset"/>
    </mxGeometry>
</mxCell>

<!-- Multiplicidad 0..* (Notificacion) -->
<mxCell id="Usuario-Notificacion-mult2" 
        value="0..*" 
        style="edgeLabel;resizable=0;html=1;align=left;verticalAlign=top;" 
        connectable="0" 
        vertex="1" 
        parent="Usuario-Notificacion">
    <mxGeometry x="1" relative="1" as="geometry">
        <mxPoint x="10" y="-10" as="offset"/>
    </mxGeometry>
</mxCell>
```

### Relación: EmailNotifier → Notificacion (Dependencia)
```xml
<!-- Dependencia EmailNotifier usa Notificacion -->
<mxCell id="EmailNotifier-Notificacion" 
        value="&lt;&lt;use&gt;&gt;" 
        style="endArrow=open;html=1;endSize=12;dashed=1;dashPattern=1 2;" 
        edge="1" 
        parent="1" 
        source="EmailNotifier" 
        target="Notificacion">
    <mxGeometry relative="1" as="geometry"/>
</mxCell>
```

### Relación: DiscordNotifier → Notificacion (Dependencia)
```xml
<!-- Dependencia DiscordNotifier usa Notificacion -->
<mxCell id="DiscordNotifier-Notificacion" 
        value="&lt;&lt;use&gt;&gt;" 
        style="endArrow=open;html=1;endSize=12;dashed=1;dashPattern=1 2;" 
        edge="1" 
        parent="1" 
        source="DiscordNotifier" 
        target="Notificacion">
    <mxGeometry relative="1" as="geometry"/>
</mxCell>
```

### Relación: PushNotifier → Notificacion (Dependencia)
```xml
<!-- Dependencia PushNotifier usa Notificacion -->
<mxCell id="PushNotifier-Notificacion" 
        value="&lt;&lt;use&gt;&gt;" 
        style="endArrow=open;html=1;endSize=12;dashed=1;dashPattern=1 2;" 
        edge="1" 
        parent="1" 
        source="PushNotifier" 
        target="Notificacion">
    <mxGeometry relative="1" as="geometry"/>
</mxCell>
```

---

## 🎯 **Resumen Visual**

```
┌──────────────────────────────────────────────────┐
│                                                   │
│  ❌ INCORRECTO:                                  │
│  Notificacion ──depende──> EmailNotifier         │
│                                                   │
│  ✅ CORRECTO:                                    │
│  EmailNotifier ──«use»──> Notificacion          │
│                                                   │
├──────────────────────────────────────────────────┤
│                                                   │
│  ✅ CORRECTO:                                    │
│  Usuario ──tiene──> Notificacion                 │
│     1               0..*                          │
│                                                   │
└──────────────────────────────────────────────────┘
```

---

**Fecha:** 2025-11-10  
**Versión:** 1.0  
**Estado:** ✅ Completo
