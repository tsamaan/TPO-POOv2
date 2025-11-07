# Mapeo Diagrama UML → Código Java

## Componentes del Diagrama

### 🔵 **PATRON STATE** (Azul en diagrama)

| Elemento del Diagrama | Clase Java | Ubicación |
|------------------------|------------|-----------|
| `«interface» ScrimState` | `ScrimState.java` | `states/ScrimState.java` |
| `EstadoBuscandoJugadores` | `EstadoBuscandoJugadores.java` | `states/` |
| `EstadoLobbyCompleto` | `EstadoLobbyCompleto.java` | `states/` |
| `EstadoConfirmado` | `EstadoConfirmado.java` | `states/` |
| `EstadoEnJuego` | `EstadoEnJuego.java` | `states/` |
| `EstadoFinalizado` | `EstadoFinalizado.java` | `states/` |
| `EstadoCancelado` | `EstadoCancelado.java` | `states/` |
| `ScrimContext` | `ScrimContext.java` | `context/ScrimContext.java` |

**Métodos implementados**:
- `postular(Scrim ctx)` ✅
- `iniciar(Scrim ctx)` ✅
- `cancelar(Scrim ctx)` ✅

---

### 🟢 **PATRON STRATEGY** (Verde en diagrama)

| Elemento del Diagrama | Clase Java | Ubicación |
|------------------------|------------|-----------|
| `«interface» MatchmakingStrategy` | `IMatchMakingStrategy.java` | `interfaces/` |
| `ByMMRStrategy` | `ByMMRStrategy.java` | `strategies/` |
| `ByLatencyStrategy` | `ByLatencyStrategy.java` | `strategies/` |
| `MatchmakingService` | `MatchmakingService.java` | `service/` |

**Métodos implementados**:
- `ejecutarEmparejamiento(Scrim scrim)` ✅

---

### 🔴 **PATRON ABSTRACT FACTORY** (Rojo/Rosa en diagrama)

| Elemento del Diagrama | Clase Java | Ubicación |
|------------------------|------------|-----------|
| `«interface» Notifier` | `INotifier.java` | `interfaces/` |
| `EmailNotifier` | `EmailNotifier.java` | `notifiers/` |
| `DiscordNotifier` | `DiscordNotifier.java` | `notifiers/` |
| `PushNotifier` | `PushNotifier.java` | `notifiers/` |
| `NotifierFactory` | `NotifierFactory.java` (abstract) | `notifiers/` |
| (concrete factory) | `SimpleNotifierFactory.java` | `notifiers/` |

**Métodos implementados**:
- `sendNotification(Notificacion)` ✅
- `createEmailNotifier()` ✅
- `createDiscordNotifier()` ✅
- `createPushNotifier()` ✅

---

### 🟣 **PATRON ADAPTER** (Púrpura en diagrama)

| Elemento del Diagrama | Clase Java | Ubicación |
|------------------------|------------|-----------|
| `«interface» AuthProvider` | `AuthProvider.java` | `auth/` |
| `LocalAuthAdapter` | `LocalAuthAdapter.java` | `auth/` |
| `GoogleAuthAdapter` | `GoogleAuthAdapter.java` | `auth/` |
| `AuthService` | `AuthService.java` | `auth/` |
| `AuthController` | `AuthController.java` | `auth/` |

**Métodos implementados**:
- `authenticate(credentials)` ✅
- `registerUser(...)` ✅
- `loginUser(email, password)` ✅
- `loginWithProvider(providerName, credentials)` ✅

---

### 🟠 **MODELOS DE DOMINIO** (Naranja en diagrama)

| Elemento del Diagrama | Clase Java | Ubicación |
|------------------------|------------|-----------|
| `Usuario` | `Usuario.java` | `models/` |
| `Postulacion` | `Postulacion.java` | `models/` |
| `Scrim` | `Scrim.java` | `models/` |
| `Notificacion` | `Notificacion.java` | `models/` |

**Atributos y métodos de Usuario**:
- `- id: int` ✅
- `- username: String` ✅
- `- email: String` ✅
- `+ rangoPorJuego(): Map` ✅

**Atributos de Postulacion**:
- `- rolDeseado: String` ✅
- `- estado: String` ✅

**Atributos de Scrim**:
- `- estado: ScrimState` ✅
- `- postulaciones: List<Postulacion>` ✅
- `- notifiers: List<INotifier>` ✅

---

## Relaciones Implementadas

### Composición y Agregación
- ✅ `ScrimContext` contiene `Scrim` y `ScrimState`
- ✅ `Scrim` contiene lista de `Postulacion`
- ✅ `Scrim` contiene lista de `INotifier` (Observer pattern)
- ✅ `MatchmakingService` contiene `IMatchMakingStrategy`
- ✅ `AuthService` contiene `Map<String, AuthProvider>`

### Implementación de Interfaces
- ✅ Todos los estados implementan `ScrimState`
- ✅ Todas las estrategias implementan `IMatchMakingStrategy`
- ✅ Todos los notifiers implementan `INotifier`
- ✅ Todos los adapters implementan `AuthProvider`

### Uso (Dependencies)
- ✅ `ScrimContext` usa `Scrim` y estados
- ✅ `AuthController` usa `AuthService`
- ✅ `AuthService` usa `Usuario` y `AuthProvider`
- ✅ Estados usan `Scrim` para cambiar estado

---

## Patrones de Diseño Verificados

| Patrón | Estado | Evidencia |
|--------|--------|-----------|
| **State** | ✅ Implementado | 6 estados concretos + interfaz + context |
| **Strategy** | ✅ Implementado | 2 estrategias + service que las usa |
| **Abstract Factory** | ✅ Implementado | Factory abstracta + concrete factory + 3 products |
| **Adapter** | ✅ Implementado | 2 adapters + interfaz común + service |
| **Observer** | ✅ Implementado | Notificadores suscritos a Scrim |

---

## Ejecución de Demo

El archivo `Main.java` demuestra todos los patrones en acción:

1. ✅ Autenticación con diferentes adapters
2. ✅ Creación de notificadores con factory
3. ✅ Cambios de estado del Scrim
4. ✅ Uso de diferentes estrategias de matchmaking
5. ✅ Notificaciones automáticas (observer)

**Salida verificada**: Ver output de `run.bat`

---

## Requisitos del TP Cubiertos

| Requisito | Estado | Implementación |
|-----------|--------|----------------|
| Gestión de usuarios | ✅ | `Usuario`, `AuthService`, `AuthController` |
| Autenticación multi-proveedor | ✅ | Adapter pattern en `auth/` |
| Sistema de scrims | ✅ | `Scrim`, estados, postulaciones |
| Matchmaking flexible | ✅ | Strategy pattern en `strategies/` |
| Notificaciones | ✅ | Abstract Factory en `notifiers/` |
| Roles de jugadores | ✅ | `Postulacion` con `rolDeseado` |
| Estados del scrim | ✅ | State pattern con 6 estados |

---

**Conclusión**: Todos los elementos del diagrama UML están implementados en código Java funcional.
