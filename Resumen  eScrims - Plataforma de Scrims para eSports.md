
### **Propósito Central**

Desarrollar un sistema completo  que permita a jugadores de eSports **organizar, encontrar y gestionar partidas amistosas (scrims)** con emparejamiento inteligente, gestión de estados y notificaciones multi-canal.

###  **Actores Principales**

1. **Jugador**: Usuario base que se postula a scrims, confirma participación y juega.
2. **Organizador/Capitán**: Crea scrims, gestiona equipos y asigna roles.
3. **Moderador**: Gestiona reportes de conducta y aplica penalidades.
4. **Sistema**: Actor automático que ejecuta emparejamiento, cambia estados y envía notificaciones.

###  **Arquitectura y Enfoque Técnico**

- **Arquitectura**: MVC con capa de dominio separada.
- **Patrones obligatorios**: State, Strategy, Observer, Abstract Factory.
- **Patrones opcionales**: Builder, Command, Adapter, Chain of Responsibility.
- **Persistencia**: ORM/JPA para base de datos.
- **Comunicación**: API REST para frontend y integraciones.

###  **Conceptos Clave del Dominio**

|Concepto|Descripción|Relaciones|
|---|---|---|
|**Scrim**|Partida amistosa con juego, formato, región, rangos y fecha|Tiene estados, recibe postulaciones|
|**Usuario**|Jugador con perfil (rango, roles, región, preferencias)|Se postula a scrims, recibe notificaciones|
|**Postulación**|Solicitud de un jugador para unirse a un scrim|Relaciona Usuario-Scrim con estado (Pendiente/Aceptada/Rechazada)|
|**Equipo**|Grupo de jugadores para un lado del scrim|Compuesto por Usuarios con roles asignados|
|**Confirmación**|Aceptación explícita para participar|Requerida para transicionar a estado "Confirmado"|

###  **Flujo Principal del Sistema**

1. **Registro** → Usuario crea perfil con sus rangos y roles preferidos
2. **Creación** → Organizador define scrim con parámetros específicos
3. **Postulación** → Jugadores se postulan y sistema empareja según estrategia
4. **Armado de Lobby** → Cuando se completan cupos, estado cambia a "Lobby armado"
5. **Confirmación** → Todos los jugadores deben confirmar participación
6. **Ejecución** → Scrim pasa a "En juego" automáticamente al llegar la fecha/hora    
7. **Finalización** → Se cargan resultados y estadísticas
8. **Feedback** → Sistema de rating y comentarios entre jugadores

### **Mecanismos Críticos**

#### 1. Gestión de Estados (Patrón State):
```java 
Buscando → Lobby armado → Confirmado → En juego → Finalizado
     ↓
  Cancelado (en cualquier estado antes de "En juego")
```
- **Transiciones automáticas** por tiempo y reglas de negocio
- **Cada estado** controla qué acciones son válidas (postular, confirmar, etc.)

#### **2. Emparejamiento Inteligente (Patrón Strategy)**

- **Estrategias intercambiables**:
    - Por MMR/Rango (diferencia máxima configurable)
    - Por Latencia (ping dentro de umbral)
    - Por Historial (compatibilidad de roles, conducta previa)

- **Configurable por scrim**: rangos mínimos/máximos, roles obligatorios

#### **3. Sistema de Notificaciones (Observer + Abstract Factory)**

- **Eventos que disparan notificaciones**
    - Scrim creado que coincide con preferencias
    - Lobby armado (cupo completo)
    - Confirmación por todos los jugadores
    - Cambios de estado (En juego, Finalizado, Cancelado)

- **Canales multi-plataforma**: Push, Email, Discord/Slack

- **Fábricas abstractas** para diferentes entornos (dev/prod)

### **Requerimientos Clave de Implementación**

#### Funcionales Esenciales

-  Búsqueda con filtros (juego, formato, rango, región, latencia)
-  Gestión completa del ciclo de vida del scrim
-  Sistema de confirmación obligatoria
-  Carga de estadísticas post-partida
-  Moderación de reportes con escalamiento

#### **No Funcionales Críticos**

-  Rendimiento: Emparejamiento en <2 segundos para 500 candidatos
-  Seguridad: Hashing de contraseñas, roles de usuario, rate limiting
-  Escalabilidad: Colas para notificaciones (RabbitMQ/Kafka simulado)
-  Testing: Tests unitarios, de integración y de estado

### **Estructura de Entregables**

1. **Diagramas UML** (clases + estados) con patrones marcados
2. **Modelo de dominio** documentado con casos de uso
3. **Código fuente** con arquitectura en capas
4. **Suite de tests** automatizados
5. **Video demo** ≤5 minutos mostrando patrones en acción

### **Puntos de Atención Especial**

- **Desacoplamiento**: Cada patrón debe verse claramente separado
- **Extensibilidad**: Poder añadir nuevas estrategias de emparejamiento y canales de notificación fácilmente
- **Manejo de errores**: Reintentos exponenciales para notificaciones fallidas
- **Validaciones**: Rangos coherentes, roles compatibles con el juego