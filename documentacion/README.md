# eScrims - Plataforma de Scrims para eSports

## 📋 Descripción
Implementación en Java de una plataforma de matchmaking para scrims (partidas de práctica) en eSports, utilizando patrones de diseño para crear una arquitectura flexible y mantenible.

## 🏗️ Arquitectura y Patrones de Diseño

### 1. **Patrón STATE** 
- **Ubicación**: `states/`
- **Propósito**: Gestionar los diferentes estados del ciclo de vida de un scrim
- **Estados Implementados**:
  - `EstadoBuscandoJugadores`: Acepta postulaciones
  - `EstadoLobbyCompleto`: Lobby lleno, no acepta más jugadores
  - `EstadoConfirmado`: Jugadores confirmados, listo para iniciar
  - `EstadoEnJuego`: Partida en curso
  - `EstadoFinalizado`: Partida finalizada
  - `EstadoCancelado`: Scrim cancelado

**Clase Context**: `ScrimContext` maneja las transiciones entre estados.

### 2. **Patrón STRATEGY**
- **Ubicación**: `strategies/`
- **Propósito**: Permitir diferentes algoritmos de matchmaking intercambiables
- **Estrategias Implementadas**:
  - `ByMMRStrategy`: Empareja por nivel de habilidad (MMR)
  - `ByLatencyStrategy`: Empareja por latencia/región

**Servicio**: `MatchmakingService` utiliza la estrategia seleccionada.

### 3. **Patrón ABSTRACT FACTORY**
- **Ubicación**: `notifiers/`
- **Propósito**: Crear familias de objetos relacionados (notificadores)
- **Implementaciones**:
  - `EmailNotifier`: Notificaciones por email
  - `DiscordNotifier`: Notificaciones por Discord
  - `PushNotifier`: Notificaciones push
  
**Factory**: `NotifierFactory` (abstract) → `SimpleNotifierFactory` (concrete)

### 4. **Patrón ADAPTER**
- **Ubicación**: `auth/`
- **Propósito**: Integrar diferentes sistemas de autenticación con una interfaz común
- **Adaptadores Implementados**:
  - `LocalAuthAdapter`: Autenticación local
  - `GoogleAuthAdapter`: Autenticación con Google OAuth

**Interfaz**: `AuthProvider` define el contrato común.

### 5. **Patrón OBSERVER** (Implícito)
- **Ubicación**: `models/Scrim.java`
- **Propósito**: Notificar a múltiples observadores (notificadores) de cambios de estado
- Los notificadores se suscriben al Scrim y reciben notificaciones automáticamente.

## 📁 Estructura del Proyecto

```
src/
├── auth/                    # Autenticación (Adapter)
│   ├── AuthProvider.java
│   ├── LocalAuthAdapter.java
│   ├── GoogleAuthAdapter.java
│   ├── AuthService.java
│   └── AuthController.java
├── context/                 # Context para State
│   └── ScrimContext.java
├── interfaces/              # Contratos
│   ├── IMatchMakingStrategy.java
│   ├── INotifier.java
│   └── IScreamState.java
├── models/                  # Modelos de dominio
│   ├── Notificacion.java
│   ├── Postulacion.java
│   ├── Scrim.java
│   └── Usuario.java
├── notifiers/               # Abstract Factory
│   ├── EmailNotifier.java
│   ├── DiscordNotifier.java
│   ├── PushNotifier.java
│   ├── NotifierFactory.java
│   └── SimpleNotifierFactory.java
├── service/                 # Servicios de negocio
│   └── MatchmakingService.java
├── states/                  # State Pattern
│   ├── ScrimState.java
│   ├── EstadoBuscandoJugadores.java
│   ├── EstadoLobbyCompleto.java
│   ├── EstadoConfirmado.java
│   ├── EstadoEnJuego.java
│   ├── EstadoFinalizado.java
│   └── EstadoCancelado.java
├── strategies/              # Strategy Pattern
│   ├── ByMMRStrategy.java
│   └── ByLatencyStrategy.java
└── main/
    └── Main.java           # Demo completa
```

## 🚀 Cómo Ejecutar

### Compilar
```bash
cd codigo
javac -d bin src/**/*.java src/**/**/*.java
```

### Ejecutar
```bash
java -cp bin main.Main
```

## 💡 Características Implementadas

✅ **Gestión de Estados**: Transiciones automáticas basadas en el ciclo de vida del scrim  
✅ **Matchmaking Flexible**: Estrategias intercambiables de emparejamiento  
✅ **Notificaciones Multi-canal**: Email, Discord, Push  
✅ **Autenticación Multi-proveedor**: Local, Google (extensible a más)  
✅ **Postulaciones por Rol**: Los jugadores se postulan para roles específicos  
✅ **Observers Automáticos**: Notificaciones automáticas en cambios de estado  

## 🎯 Requisitos Cubiertos

Los siguientes requisitos del TP están implementados:

1. **Gestión de usuarios**: Login, registro, autenticación multi-proveedor
2. **Sistema de scrims**: Creación, estados, postulaciones
3. **Matchmaking**: Algoritmos intercambiables (MMR, latencia)
4. **Notificaciones**: Sistema extensible de notificaciones
5. **Roles**: Postulación por roles específicos (Support, ADC, Mid, Jungle, Top)

## 📊 Diagrama UML

El diagrama UML completo está disponible en `TPO-POOv2.xml` (formato draw.io).

## 👥 Patrones de Diseño - Resumen

| Patrón | Beneficio | Ubicación |
|--------|-----------|-----------|
| **State** | Gestión clara de transiciones de estado | `states/` |
| **Strategy** | Algoritmos de matchmaking intercambiables | `strategies/` |
| **Abstract Factory** | Creación consistente de notificadores | `notifiers/` |
| **Adapter** | Integración de múltiples sistemas auth | `auth/` |
| **Observer** | Notificaciones automáticas de cambios | `models/Scrim.java` |

## 🔧 Extensibilidad

El diseño permite fácilmente:
- Agregar nuevos estados del scrim
- Implementar nuevas estrategias de matchmaking
- Añadir más canales de notificación
- Integrar más proveedores de autenticación
- Extender roles y tipos de juegos

---

**Trabajo Práctico - Patrones de Diseño**  
*Proceso de Desarrollo de Software - UADE*
