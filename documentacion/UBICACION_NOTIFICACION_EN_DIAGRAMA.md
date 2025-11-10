# 📐 Ubicación de la Clase `Notificacion` en el Diagrama UML

## 🎯 ¿Dónde debería ir la clase `Notificacion` en el diagrama?

La clase `Notificacion` debería ir en la **zona del Modelo de Dominio**, específicamente:

---

## 📊 Estructura del Diagrama Actual

Tu diagrama tiene estas zonas organizadas por patrones:

```
┌─────────────────────────────────────────────────────────────────┐
│                    DIAGRAMA UML TPO-POOv2                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  🟦 ZONA 1: MODELO DE DOMINIO (Entities)                        │
│     - Usuario                                                     │
│     - Scrim                                                       │
│     - Equipo                                                      │
│     - Postulacion                                                 │
│     - Confirmacion                                                │
│     - Estadistica                                                 │
│     - Notificacion  ← ✅ AQUÍ DEBERÍA IR                         │
│     - ReporteConducta                                             │
│                                                                   │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  🟨 ZONA 2: PATRÓN STATE                                         │
│     - ScrimContext                                                │
│     - ScrimState (interface)                                      │
│     - EstadoBuscandoJugadores                                     │
│     - EstadoLobbyCompleto                                         │
│     - EstadoConfirmado                                            │
│     - EstadoEnJuego                                               │
│     - EstadoFinalizado                                            │
│     - EstadoCancelado                                             │
│                                                                   │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  🟩 ZONA 3: PATRÓN STRATEGY                                      │
│     - MatchmakingStrategy (interface)                             │
│     - ByMMRStrategy                                               │
│     - ByLatencyStrategy                                           │
│                                                                   │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  🟧 ZONA 4: PATRÓN OBSERVER                                      │
│     - DomainEventBus (Subject)                                    │
│     - Subscriber (interface)                                      │
│     - NotificationSubscriber (Observer)                           │
│                                                                   │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  🟪 ZONA 5: PATRÓN ABSTRACT FACTORY                              │
│     - NotifierFactory (interface)                                 │
│     - DevNotifierFactory                                          │
│     - ProdNotifierFactory                                         │
│                                                                   │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  🟥 ZONA 6: IMPLEMENTACIONES DE NOTIFIERS                        │
│     - Notifier (interface)                                        │
│     - EmailNotifier                                               │
│     - DiscordNotifier                                             │
│     - PushNotifier                                                │
│     - CompositeNotifier                                           │
│                                                                   │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ⬜ ZONA 7: SERVICIOS Y AUTENTICACIÓN                            │
│     - ScrimService                                                │
│     - AuthProvider                                                │
│     - OAuthAdapter                                                │
│                                                                   │
└─────────────────────────────────────────────────────────────────┘
```

---

## ✅ **Respuesta: La clase `Notificacion` va en la ZONA 1 (Modelo de Dominio)**

### 📍 Ubicación Exacta:

```
┌──────────────────────────────────────────────┐
│      🟦 MODELO DE DOMINIO (Entities)         │
├──────────────────────────────────────────────┤
│                                              │
│  ┌─────────────┐      ┌─────────────┐      │
│  │  Usuario    │      │   Scrim     │      │
│  └─────────────┘      └─────────────┘      │
│         │                    │              │
│         │                    │              │
│         ▼                    ▼              │
│  ┌─────────────┐      ┌─────────────┐      │
│  │ Postulacion │      │   Equipo    │      │
│  └─────────────┘      └─────────────┘      │
│         │                    │              │
│         ▼                    ▼              │
│  ┌─────────────┐      ┌─────────────┐      │
│  │Confirmacion │      │ Estadistica │      │
│  └─────────────┘      └─────────────┘      │
│                                              │
│  ┌───────────────────────────────┐          │
│  │      Notificacion             │ ← AQUÍ   │
│  ├───────────────────────────────┤          │
│  │ - id: UUID                    │          │
│  │ - tipo: String                │          │
│  │ - titulo: String              │          │
│  │ - mensaje: String             │          │
│  │ - destinatario: Usuario       │          │
│  │ - canal: String               │          │
│  │ - estado: EstadoNotificacion  │          │
│  │ - fechaCreacion: LocalDateTime│          │
│  │ - fechaEnvio: LocalDateTime   │          │
│  │ - intentosEnvio: int          │          │
│  ├───────────────────────────────┤          │
│  │ + marcarComoEnviada()         │          │
│  │ + marcarComoFallida(error)    │          │
│  │ + puedeReintentar(): boolean  │          │
│  └───────────────────────────────┘          │
│         ▲                                    │
│         │ usa                                │
│         │                                    │
│  ┌─────────────┐                            │
│  │ReporteConducta│                           │
│  └─────────────┘                            │
│                                              │
└──────────────────────────────────────────────┘
```

---

## 🔗 Relaciones con Otras Clases

La clase `Notificacion` se relaciona con:

### 1️⃣ **Con Usuario** (Asociación)
```
┌─────────────┐              ┌─────────────┐
│  Usuario    │ 1         *  │Notificacion │
│             │─────────────>│             │
│ - username  │ destinatario │ - titulo    │
│ - email     │              │ - mensaje   │
└─────────────┘              └─────────────┘
```
- Una `Notificacion` tiene **1 destinatario** (Usuario)
- Un `Usuario` puede tener **muchas notificaciones**

---

### 2️⃣ **Con EmailNotifier, DiscordNotifier, PushNotifier** (Dependencia)
```
┌─────────────┐              ┌─────────────┐
│Notificacion │              │EmailNotifier│
│             │─────────────>│             │
│ - mensaje   │   usa        │ + send()    │
│ - canal     │              │             │
└─────────────┘              └─────────────┘
```
- Los Notifiers **reciben** objetos `Notificacion` como parámetro
- La `Notificacion` es un **DTO** (Data Transfer Object)

---

### 3️⃣ **Con NotificationSubscriber** (Dependencia)
```
┌─────────────────────┐       ┌─────────────┐
│NotificationSubscriber│       │Notificacion │
│                     │──────>│             │
│ + onEvent()         │ crea  │             │
│                     │       │             │
└─────────────────────┘       └─────────────┘
```
- El `NotificationSubscriber` **crea** objetos `Notificacion`
- Los pasa a los `Notifier`s para envío

---

## 🎨 Formato Visual en Draw.io / XML

### Propiedades Sugeridas:

| **Propiedad** | **Valor** |
|---|---|
| **Color de fondo** | `#E1F5FE` (azul claro, como otras entidades) |
| **Borde** | `#0277BD` (azul oscuro) |
| **Estereotipo** | `«entity»` |
| **Posición** | Debajo de `Confirmacion` y `Estadistica` |
| **Tamaño** | Ancho: 200px, Alto: 250px |

---

## 📝 XML para Agregar al Diagrama

Si querés agregarlo manualmente al archivo `TPO-POOv2.xml`, deberías agregar:

```xml
<mxCell id="Notificacion" value="&lt;&lt;entity&gt;&gt;&#xa;Notificacion" style="swimlane;fontStyle=1;align=center;verticalAlign=top;childLayout=stackLayout;horizontal=1;startSize=40;horizontalStack=0;resizeParent=1;resizeParentMax=0;resizeLast=0;collapsible=1;marginBottom=0;fillColor=#E1F5FE;strokeColor=#0277BD;" vertex="1" parent="1">
    <mxGeometry x="100" y="800" width="220" height="280" as="geometry"/>
</mxCell>

<mxCell id="Notificacion-attrs" value="- id: UUID&#xa;- tipo: String&#xa;- titulo: String&#xa;- mensaje: String&#xa;- destinatario: Usuario&#xa;- canal: String&#xa;- estado: EstadoNotificacion&#xa;- fechaCreacion: LocalDateTime&#xa;- fechaEnvio: LocalDateTime&#xa;- intentosEnvio: int&#xa;- errorMensaje: String" style="text;strokeColor=none;fillColor=none;align=left;verticalAlign=top;spacingLeft=4;spacingRight=4;overflow=hidden;rotatable=0;points=[[0,0.5],[1,0.5]];portConstraint=eastwest;" vertex="1" parent="Notificacion">
    <mxGeometry y="40" width="220" height="160" as="geometry"/>
</mxCell>

<mxCell id="Notificacion-separator" value="" style="line;strokeWidth=1;fillColor=none;align=left;verticalAlign=middle;spacingTop=-1;spacingLeft=3;spacingRight=3;rotatable=0;labelPosition=right;points=[];portConstraint=eastwest;" vertex="1" parent="Notificacion">
    <mxGeometry y="200" width="220" height="8" as="geometry"/>
</mxCell>

<mxCell id="Notificacion-methods" value="+ marcarComoEnviada(): void&#xa;+ marcarComoFallida(error: String): void&#xa;+ puedeReintentar(): boolean&#xa;+ toString(): String" style="text;strokeColor=none;fillColor=none;align=left;verticalAlign=top;spacingLeft=4;spacingRight=4;overflow=hidden;rotatable=0;points=[[0,0.5],[1,0.5]];portConstraint=eastwest;" vertex="1" parent="Notificacion">
    <mxGeometry y="208" width="220" height="72" as="geometry"/>
</mxCell>
```

---

## 🔗 Relaciones a Agregar

### 1. Asociación Usuario → Notificacion
```xml
<mxCell id="Usuario-Notificacion" value="destinatario" style="endArrow=open;html=1;endSize=12;startArrow=diamondThin;startSize=14;startFill=0;edgeStyle=orthogonalEdgeStyle;" edge="1" parent="1" source="Usuario" target="Notificacion">
    <mxGeometry relative="1" as="geometry"/>
</mxCell>
<mxCell id="Usuario-Notificacion-multiplicity" value="1" style="edgeLabel;resizable=0;html=1;align=right;verticalAlign=top;" connectable="0" vertex="1" parent="Usuario-Notificacion">
    <mxGeometry x="-1" relative="1" as="geometry"/>
</mxCell>
<mxCell id="Usuario-Notificacion-multiplicity2" value="0..*" style="edgeLabel;resizable=0;html=1;align=left;verticalAlign=top;" connectable="0" vertex="1" parent="Usuario-Notificacion">
    <mxGeometry x="1" relative="1" as="geometry"/>
</mxCell>
```

### 2. Dependencia EmailNotifier → Notificacion
```xml
<mxCell id="EmailNotifier-Notificacion" value="&lt;&lt;use&gt;&gt;" style="endArrow=open;html=1;endSize=12;dashed=1;dashPattern=1 2;" edge="1" parent="1" source="EmailNotifier" target="Notificacion">
    <mxGeometry relative="1" as="geometry"/>
</mxCell>
```

### 3. Dependencia DiscordNotifier → Notificacion
```xml
<mxCell id="DiscordNotifier-Notificacion" value="&lt;&lt;use&gt;&gt;" style="endArrow=open;html=1;endSize=12;dashed=1;dashPattern=1 2;" edge="1" parent="1" source="DiscordNotifier" target="Notificacion">
    <mxGeometry relative="1" as="geometry"/>
</mxCell>
```

### 4. Dependencia PushNotifier → Notificacion
```xml
<mxCell id="PushNotifier-Notificacion" value="&lt;&lt;use&gt;&gt;" style="endArrow=open;html=1;endSize=12;dashed=1;dashPattern=1 2;" edge="1" parent="1" source="PushNotifier" target="Notificacion">
    <mxGeometry relative="1" as="geometry"/>
</mxCell>
```

---

## ✅ Resumen: ¿Dónde va `Notificacion` en el diagrama?

| **Aspecto** | **Ubicación** |
|---|---|
| **Zona del diagrama** | 🟦 Modelo de Dominio (Entities) |
| **Junto a** | Usuario, Scrim, Equipo, Confirmacion, Estadistica |
| **Estereotipo** | `«entity»` |
| **Color** | Azul claro (`#E1F5FE`) como otras entidades |
| **Posición sugerida** | Debajo de Confirmacion y Estadistica |
| **Relaciones** | → Usuario (asociación), ← Notifiers (dependencia) |

---

## 🎯 ¿Por qué en Modelo de Dominio?

1. ✅ **Es una entidad del negocio** (representa un concepto real)
2. ✅ **Tiene identidad** (UUID)
3. ✅ **Tiene estado** (PENDIENTE, ENVIADA, FALLIDA)
4. ✅ **Puede persistirse en BD** (como Usuario, Scrim, etc.)
5. ✅ **No es un patrón de diseño** (no es State, Strategy, etc.)

---

**Fecha:** 2025-11-10  
**Versión:** 1.0  
**Estado:** ✅ Completo
