#  Explicación Detallada de Patrones Implementados

## Proyecto: eScrims - Plataforma de Matchmaking para eSports

Este documento explica en detalle cada uno de los 5 patrones de diseño implementados en el proyecto, respondiendo:
- **¿Cómo se implementó?**
- **¿Dónde se implementó?**
- **¿Por qué lo implementamos de esa manera?**
- **Comentarios adicionales**

---

##  Índice de Patrones

1. [Patrón STATE](#1-patrón-state)
2. [Patrón STRATEGY](#2-patrón-strategy)
3. [Patrón ABSTRACT FACTORY](#3-patrón-abstract-factory)
4. [Patrón ADAPTER](#4-patrón-adapter)
5. [Patrón OBSERVER](#5-patrón-observer)

---

# 1. Patrón STATE

## ¿Qué problema resuelve?

Un **Scrim** (partida de práctica) pasa por múltiples estados durante su ciclo de vida: desde que se crea, se llenan los cupos, se confirman los jugadores, se juega la partida, hasta que finaliza o se cancela. Cada estado tiene comportamientos diferentes ante las mismas acciones.

**Problema sin el patrón**: Tendríamos un código lleno de `if/else` o `switch/case` verificando el estado actual en cada método, haciendo el código difícil de mantener y extender.

---

##  ¿Cómo se implementó?

### Estructura de Archivos

```
src/states/
├── ScrimState.java                    ← Interfaz base
├── EstadoBuscandoJugadores.java       ← Estado inicial
├── EstadoLobbyCompleto.java           ← Lobby lleno
├── EstadoConfirmado.java              ← Todos confirmaron
├── EstadoEnJuego.java                 ← Partida en curso
├── EstadoFinalizado.java              ← Partida terminada
└── EstadoCancelado.java               ← Scrim cancelado

src/context/
└── ScrimContext.java                  ← Context (maneja transiciones)

src/models/
└── Scrim.java                         ← Modelo que usa los estados
```

### Implementación Técnica

#### 1. **Interfaz `ScrimState`** (Contrato común)
```java
package states;

import models.Scrim;

public interface ScrimState {
    void postular(Scrim ctx);
    void iniciar(Scrim ctx);
    void cancelar(Scrim ctx);
}
```

**Explicación**: Define las operaciones que todos los estados deben manejar. Cada estado implementa estas operaciones de manera diferente.

---

#### 2. **Estados Concretos** (Ejemplo: `EstadoBuscandoJugadores`)
```java
package states;

import models.Scrim;

public class EstadoBuscandoJugadores implements ScrimState {
    @Override
    public void postular(Scrim ctx) {
        System.out.println("Postulación aceptada. Estado: Buscando jugadores");
        // Lógica para aceptar postulaciones
    }

    @Override
    public void iniciar(Scrim ctx) {
        System.out.println("ERROR: No se puede iniciar, faltan jugadores");
    }

    @Override
    public void cancelar(Scrim ctx) {
        ctx.cambiarEstado(new EstadoCancelado());
        System.out.println("Scrim cancelado desde estado Buscando Jugadores");
    }
}
```

**Explicación**: 
- En este estado, `postular()` es válido ✅
- `iniciar()` no es válido porque faltan jugadores ❌
- `cancelar()` cambia al estado `EstadoCancelado` ✅

---

#### 3. **Context `ScrimContext`** (Coordinador)
```java
package context;

import models.Scrim;
import states.ScrimState;

public class ScrimContext {
    private Scrim scrim;
    private ScrimState estado;

    public ScrimContext(Scrim scrim, ScrimState estadoInicial) {
        this.scrim = scrim;
        this.estado = estadoInicial;
    }

    public void postular(Usuario usuario, String rol) {
        Postulacion p = new Postulacion(usuario, rol);
        scrim.addPostulacion(p);
        estado.postular(scrim);  // Delega al estado actual
    }

    public void cambiarEstado(ScrimState nuevoEstado) {
        this.estado = nuevoEstado;
        scrim.cambiarEstado(nuevoEstado);
    }
}
```

**Explicación**: 
- El `ScrimContext` mantiene una referencia al estado actual
- Delega las operaciones al estado correspondiente
- Coordina los cambios de estado

---

## 📍 ¿Dónde se implementó?

| Componente | Ubicación | Rol |
|------------|-----------|-----|
| Interfaz | `states/ScrimState.java` | Define el contrato |
| Estados concretos | `states/Estado*.java` (7 archivos) | Implementan comportamientos específicos |
| Context | `context/ScrimContext.java` | Coordina transiciones |
| Modelo | `models/Scrim.java` | Mantiene el estado actual |

---

## 💡 ¿Por qué lo implementamos así?

### Ventajas de esta implementación:

1. **Sin condicionales complejos**: No hay `if/else` gigantes verificando estados
   ```java
   // ❌ SIN patrón State (malo)
   public void postular(Usuario u) {
       if (estado == "BUSCANDO") {
           // aceptar
       } else if (estado == "COMPLETO") {
           // rechazar
       } else if (estado == "CANCELADO") {
           // error
       }
       // ... más condicionales
   }
   
   // ✅ CON patrón State (bueno)
   public void postular(Usuario u) {
       estado.postular(this); // El estado decide qué hacer
   }
   ```

2. **Fácil de extender**: Agregar un nuevo estado (ej: "En Pausa") solo requiere crear una nueva clase
3. **Responsabilidad única**: Cada clase de estado solo maneja su propio comportamiento
4. **Transiciones explícitas**: Los cambios de estado están claramente definidos

### Decisiones de diseño:

- **7 estados**: Cubrimos todo el ciclo de vida del scrim
- **Interfaz común**: Garantiza que todos los estados implementen las operaciones básicas
- **Context separado**: Separa la lógica de coordinación de la lógica de negocio

---

##  Comentarios Adicionales

### Diagrama de Transiciones
```
[Buscando Jugadores] 
       ↓ (cupos llenos)
[Lobby Completo]
       ↓ (todos confirmaron)
[Confirmado]
       ↓ (hora de inicio)
[En Juego]
       ↓ (partida termina)
[Finalizado]

Desde cualquier estado (excepto En Juego y Finalizado):
       ↓ (cancelación)
[Cancelado]
```

### Ejemplo de uso real:
```java
// En Main.java
Scrim scrim = new Scrim(new EstadoBuscandoJugadores());
ScrimContext ctx = new ScrimContext(scrim, scrim.getEstado());

// Usuario se postula
ctx.postular(usuario1, "Duelist");  // ✅ Aceptado
ctx.postular(usuario2, "Support");  // ✅ Aceptado
ctx.postular(usuario3, "Tank");     // ✅ Aceptado
ctx.postular(usuario4, "DPS");      // ✅ Aceptado

// Estado cambia automáticamente a LobbyCompleto
ctx.postular(usuario5, "Healer");   // ❌ Rechazado (lobby lleno)
```

---

# 2. Patrón STRATEGY

## ¿Qué problema resuelve?

El sistema necesita **emparejar jugadores** para los scrims, pero existen diferentes criterios de emparejamiento:
- Por **nivel de habilidad (MMR)**: Juntar jugadores de rangos similares
- Por **latencia/región**: Priorizar conexiones óptimas
- Futuros: Por idioma, horarios, roles complementarios, etc.

**Problema sin el patrón**: Tendríamos que modificar el servicio de matchmaking cada vez que queremos cambiar el algoritmo, violando el principio Open/Closed.

---

##  ¿Cómo se implementó?

### Estructura de Archivos

```
src/strategies/
├── ByMMRStrategy.java           ← Estrategia por habilidad
└── ByLatencyStrategy.java       ← Estrategia por latencia

src/interfaces/
└── IMatchMakingStrategy.java    ← Interfaz de estrategia

src/service/
└── MatchmakingService.java      ← Context (usa estrategias)
```

### Implementación Técnica

#### 1. **Interfaz `IMatchMakingStrategy`** (Contrato)
```java
package interfaces;

import models.Scrim;

public interface IMatchMakingStrategy {
    void ejecutarEmparejamiento(Scrim scrim);
}
```

**Explicación**: Define el método que todas las estrategias deben implementar.

---

#### 2. **Estrategias Concretas**

**Estrategia por MMR**:
```java
package strategies;

import interfaces.IMatchMakingStrategy;
import models.Scrim;

public class ByMMRStrategy implements IMatchMakingStrategy {

    @Override
    public void ejecutarEmparejamiento(Scrim scrim) {
        System.out.println("Ejecutando emparejamiento por MMR");
        
        // Lógica: Si hay suficientes postulaciones, marcar lobby completo
        if (scrim.getPostulaciones().size() >= 4) {
            scrim.cambiarEstado(new states.EstadoLobbyCompleto());
            System.out.println("Lobby completo - Jugadores emparejados por MMR");
        }
    }
}
```

**Estrategia por Latencia**:
```java
package strategies;

import interfaces.IMatchMakingStrategy;
import models.Scrim;

public class ByLatencyStrategy implements IMatchMakingStrategy {

    @Override
    public void ejecutarEmparejamiento(Scrim scrim) {
        System.out.println("Ejecutando emparejamiento por latencia");
        
        // Lógica: Priorizar jugadores con menor ping
        if (scrim.getPostulaciones().size() >= 4) {
            scrim.cambiarEstado(new states.EstadoLobbyCompleto());
            System.out.println("Lobby completo - Jugadores emparejados por latencia");
        }
    }
}
```

---

#### 3. **Context `MatchmakingService`** (Usa estrategias)
```java
package service;

import interfaces.IMatchMakingStrategy;
import models.Scrim;

public class MatchmakingService {
    private IMatchMakingStrategy estrategia;

    // Se inyecta la estrategia en el constructor
    public MatchmakingService(IMatchMakingStrategy estrategia) {
        this.estrategia = estrategia;
    }

    // Delega la ejecución a la estrategia actual
    public void ejecutarEmparejamiento(Scrim scrim) {
        estrategia.ejecutarEmparejamiento(scrim);
    }

    // Permite cambiar la estrategia en tiempo de ejecución
    public void setEstrategia(IMatchMakingStrategy nuevaEstrategia) {
        this.estrategia = nuevaEstrategia;
    }
}
```

---

## ¿Dónde se implementó?

| Componente | Ubicación | Rol |
|------------|-----------|-----|
| Interfaz | `interfaces/IMatchMakingStrategy.java` | Define el algoritmo abstracto |
| Estrategia MMR | `strategies/ByMMRStrategy.java` | Empareja por habilidad |
| Estrategia Latencia | `strategies/ByLatencyStrategy.java` | Empareja por ping |
| Context/Servicio | `service/MatchmakingService.java` | Ejecuta la estrategia seleccionada |

---

## ¿Por qué lo implementamos así?

### Ventajas de esta implementación:

1. **Intercambiable en tiempo de ejecución**: Puedes cambiar el algoritmo sin detener el sistema
   ```java
   MatchmakingService mm = new MatchmakingService(new ByMMRStrategy());
   mm.ejecutarEmparejamiento(scrim1);  // Usa MMR
   
   mm.setEstrategia(new ByLatencyStrategy());
   mm.ejecutarEmparejamiento(scrim2);  // Usa latencia
   ```

2. **Fácil de extender**: Agregar un nuevo algoritmo no modifica código existente
   ```java
   // Solo creas una nueva clase
   public class ByRoleStrategy implements IMatchMakingStrategy {
       // Empareja por roles complementarios
   }
   ```

3. **Testeable**: Cada estrategia se prueba independientemente
4. **Sin condicionales**: No necesitas `if/else` para elegir el algoritmo

### Decisiones de diseño:

- **2 estrategias iniciales**: MMR (más común en eSports) y Latencia (importante para experiencia)
- **Inyección de dependencias**: La estrategia se pasa en el constructor, promoviendo bajo acoplamiento
- **Interfaz única**: Todas las estrategias tienen la misma firma, facilitando el polimorfismo

---

##  Comentarios Adicionales

### Comparación de estrategias:

| Criterio | ByMMRStrategy | ByLatencyStrategy |
|----------|---------------|-------------------|
| **Prioridad** | Nivel de habilidad | Velocidad de conexión |
| **Uso ideal** | Partidas competitivas | Scrims casuales |
| **Ventaja** | Partidas balanceadas | Mejor experiencia de juego |
| **Desventaja** | Puede ignorar lag | Puede crear desbalance de nivel |

### Ejemplo de uso real:
```java
// En Main.java

// Crear scrim competitivo (usa MMR)
Scrim scrimRanked = new Scrim(new EstadoBuscandoJugadores());
MatchmakingService mmCompetitivo = new MatchmakingService(new ByMMRStrategy());
mmCompetitivo.ejecutarEmparejamiento(scrimRanked);

// Crear scrim casual (usa latencia)
Scrim scrimCasual = new Scrim(new EstadoBuscandoJugadores());
MatchmakingService mmCasual = new MatchmakingService(new ByLatencyStrategy());
mmCasual.ejecutarEmparejamiento(scrimCasual);
```

### Extensibilidad futura:
```java
// Fácil agregar nuevas estrategias:
public class ByRoleBalanceStrategy implements IMatchMakingStrategy { }
public class ByLanguageStrategy implements IMatchMakingStrategy { }
public class HybridStrategy implements IMatchMakingStrategy { }
```

---

# 3. Patrón ABSTRACT FACTORY

##  ¿Qué problema resuelve?

El sistema necesita enviar **notificaciones** a los usuarios por diferentes canales (Email, Discord, Push), y estos notificadores podrían tener variantes según el entorno (producción, testing, desarrollo).

**Problema sin el patrón**: 
- Crear notificadores manualmente con `new EmailNotifier()` acopla el código
- Dificulta crear familias consistentes de objetos relacionados
- Complicado cambiar entre configuraciones (ej: testing vs producción)

---

##  ¿Cómo se implementó?

### Estructura de Archivos

```
src/notifiers/
├── NotifierFactory.java           ← Abstract Factory (interfaz)
├── SimpleNotifierFactory.java     ← Concrete Factory
├── EmailNotifier.java             ← Producto concreto
├── DiscordNotifier.java           ← Producto concreto
└── PushNotifier.java              ← Producto concreto

src/interfaces/
└── INotifier.java                 ← Interfaz de productos
```

### Implementación Técnica

#### 1. **Interfaz de Producto `INotifier`**
```java
package interfaces;

import models.Notificacion;

public interface INotifier {
    void sendNotification(Notificacion notificacion);
}
```

**Explicación**: Todos los notificadores implementan esta interfaz, garantizando un contrato común.

---

#### 2. **Productos Concretos**

**EmailNotifier**:
```java
package notifiers;

import interfaces.INotifier;
import models.Notificacion;

public class EmailNotifier implements INotifier {
    
    @Override
    public void sendNotification(Notificacion notificacion) {
        System.out.println("[EMAIL] Enviando a: " + notificacion.getDestinatario());
        System.out.println("Asunto: " + notificacion.getMensaje());
    }
}
```

**DiscordNotifier**:
```java
package notifiers;

import interfaces.INotifier;
import models.Notificacion;

public class DiscordNotifier implements INotifier {
    
    @Override
    public void sendNotification(Notificacion notificacion) {
        System.out.println("[DISCORD] Webhook a servidor");
        System.out.println("Mensaje: " + notificacion.getMensaje());
    }
}
```

**PushNotifier**:
```java
package notifiers;

import interfaces.INotifier;
import models.Notificacion;

public class PushNotifier implements INotifier {
    
    @Override
    public void sendNotification(Notificacion notificacion) {
        System.out.println("[PUSH] Notificación móvil");
        System.out.println("Usuario: " + notificacion.getDestinatario());
    }
}
```

---

#### 3. **Abstract Factory**
```java
package notifiers;

import interfaces.INotifier;

public abstract class NotifierFactory {

    // Métodos abstractos para crear cada tipo de notificador
    public abstract INotifier createEmailNotifier();
    public abstract INotifier createDiscordNotifier();
    public abstract INotifier createPushNotifier();
}
```

**Explicación**: Define la interfaz para crear familias de objetos relacionados (notificadores).

---

#### 4. **Concrete Factory**
```java
package notifiers;

import interfaces.INotifier;

public class SimpleNotifierFactory extends NotifierFactory {

    @Override
    public INotifier createEmailNotifier() {
        return new EmailNotifier();
    }

    @Override
    public INotifier createDiscordNotifier() {
        return new DiscordNotifier();
    }

    @Override
    public INotifier createPushNotifier() {
        return new PushNotifier();
    }
}
```

**Explicación**: Implementación concreta que crea los notificadores reales. Podrías tener otras factories (ej: `TestNotifierFactory`, `ProductionNotifierFactory`).

---

## ¿Dónde se implementó?

| Componente           | Ubicación                              | Rol                                   |
| -------------------- | -------------------------------------- | ------------------------------------- |
| Interfaz de producto | `interfaces/INotifier.java`            | Contrato para todos los notificadores |
| Productos concretos  | `notifiers/EmailNotifier.java`         | Implementa Email                      |
|                      | `notifiers/DiscordNotifier.java`       | Implementa Discord                    |
|                      | `notifiers/PushNotifier.java`          | Implementa Push                       |
| Abstract Factory     | `notifiers/NotifierFactory.java`       | Define métodos de creación            |
| Concrete Factory     | `notifiers/SimpleNotifierFactory.java` | Crea notificadores reales             |

---

## ¿Por qué lo implementamos así?

### Ventajas de esta implementación:

1. **Desacoplamiento total**: El código cliente no depende de clases concretas
   ```java
   // ❌ SIN Abstract Factory (acoplado)
   EmailNotifier email = new EmailNotifier();
   DiscordNotifier discord = new DiscordNotifier();
   
   // ✅ CON Abstract Factory (desacoplado)
   NotifierFactory factory = new SimpleNotifierFactory();
   INotifier email = factory.createEmailNotifier();
   INotifier discord = factory.createDiscordNotifier();
   ```

2. **Familias consistentes**: Garantiza que todos los notificadores son compatibles
3. **Fácil cambiar configuración**: Solo cambias la factory
   ```java
   // Desarrollo
   NotifierFactory factory = new SimpleNotifierFactory();
   
   // Testing (podría crear mocks)
   NotifierFactory factory = new MockNotifierFactory();
   
   // Producción (con lógica real)
   NotifierFactory factory = new ProductionNotifierFactory();
   ```

4. **Extensible**: Agregar un nuevo canal (ej: Telegram) solo requiere:
   - Crear `TelegramNotifier implements INotifier`
   - Agregar `createTelegramNotifier()` a la factory

### Decisiones de diseño:

- **3 canales iniciales**: Email (tradicional), Discord (común en gaming), Push (móvil)
- **Factory abstracta**: Permite múltiples implementaciones de factories
- **Interfaz común**: Todos los notificadores son intercambiables

---

##  Comentarios Adicionales

### Diagrama de la estructura:
```
                    ┌─────────────────────┐
                    │  NotifierFactory    │ (Abstract)
                    ├─────────────────────┤
                    │ +createEmail()      │
                    │ +createDiscord()    │
                    │ +createPush()       │
                    └──────────▲──────────┘
                               │
                               │ hereda
                               │
                    ┌──────────┴──────────┐
                    │ SimpleNotifier      │ (Concrete)
                    │ Factory             │
                    └─────────────────────┘
                               │
                ┌──────────────┼──────────────┐
                │              │              │
                ▼              ▼              ▼
         ┌──────────┐   ┌──────────┐  ┌──────────┐
         │  Email   │   │ Discord  │  │   Push   │
         │ Notifier │   │ Notifier │  │ Notifier │
         └──────────┘   └──────────┘  └──────────┘
```

### Ejemplo de uso real:
```java
// En Main.java

// 1. Crear la factory
NotifierFactory factory = new SimpleNotifierFactory();

// 2. Crear los notificadores
INotifier emailNotifier = factory.createEmailNotifier();
INotifier discordNotifier = factory.createDiscordNotifier();
INotifier pushNotifier = factory.createPushNotifier();

// 3. Configurar el scrim con notificadores
scrim.addNotifier(emailNotifier);
scrim.addNotifier(discordNotifier);
scrim.addNotifier(pushNotifier);

// 4. Cuando cambie el estado, notifica por todos los canales
scrim.notificarCambio(new Notificacion("usuario@email.com", "Lobby completo!"));
// Salida:
// [EMAIL] Enviando a: usuario@email.com
// [DISCORD] Webhook a servidor
// [PUSH] Notificación móvil
```

### Extensión futura (ejemplo):
```java
// Nueva factory para testing que no envía notificaciones reales
public class MockNotifierFactory extends NotifierFactory {
    @Override
    public INotifier createEmailNotifier() {
        return new MockEmailNotifier(); // Solo loguea, no envía
    }
    // ... otros métodos
}
```

---

# 4. Patrón ADAPTER

## ¿Qué problema resuelve?

El sistema necesita soportar **múltiples sistemas de autenticación** (local, Google OAuth, potencialmente Steam, Discord, etc.), pero cada uno tiene una interfaz diferente e incompatible.

**Problema sin el patrón**: 
- El código cliente tendría que conocer y manejar cada sistema de autenticación específicamente
- Agregar un nuevo proveedor requeriría modificar todo el código de autenticación
- Violación del principio de inversión de dependencias

---

## ¿Cómo se implementó?

### Estructura de Archivos

```
src/auth/
├── AuthProvider.java           ← Interfaz target (común)
├── LocalAuthAdapter.java       ← Adapter para auth local
├── GoogleAuthAdapter.java      ← Adapter para Google OAuth
├── AuthService.java            ← Servicio que usa adapters
└── AuthController.java         ← Controlador de autenticación
```

### Implementación Técnica

#### 1. **Interfaz Target `AuthProvider`** (Interfaz común)
```java
package auth;

import models.Usuario;

public interface AuthProvider {
    Usuario authenticate(Object credentials);
}
```

**Explicación**: Define la interfaz común que todos los sistemas de autenticación deben cumplir. Es la interfaz que el cliente espera.

---

#### 2. **Adapters Concretos**

**LocalAuthAdapter** (Autenticación local):
```java
package auth;

import models.Usuario;

public class LocalAuthAdapter implements AuthProvider {

    @Override
    public Usuario authenticate(Object credentials) {
        // Adaptee: Sistema de auth local (puede ser un hash de contraseña)
        if (credentials instanceof String) {
            String username = (String) credentials;
            
            // Simulación: validar contra BD local
            System.out.println("[LOCAL AUTH] Autenticando usuario: " + username);
            
            // Retorna usuario si es válido
            return new Usuario(1, username, "local@example.com");
        }
        
        System.out.println("[LOCAL AUTH] Credenciales inválidas");
        return null;
    }
}
```

**GoogleAuthAdapter** (Google OAuth):
```java
package auth;

import models.Usuario;

public class GoogleAuthAdapter implements AuthProvider {

    @Override
    public Usuario authenticate(Object credentials) {
        // Adaptee: Google OAuth SDK (interfaz diferente)
        if (credentials instanceof String) {
            String googleToken = (String) credentials;
            
            // Simulación: validar token con Google
            System.out.println("[GOOGLE OAUTH] Validando token con Google...");
            
            // En realidad llamarías: GoogleAuthLibrary.verifyToken(googleToken)
            // Aquí solo simulamos
            
            System.out.println("[GOOGLE OAUTH] Token válido");
            return new Usuario(2, "GoogleUser", "user@gmail.com");
        }
        
        System.out.println("[GOOGLE OAUTH] Token inválido");
        return null;
    }
}
```

---

#### 3. **AuthService** (Usa los adapters)
```java
package auth;

import models.Usuario;

public class AuthService {
    private AuthProvider provider;

    public AuthService(AuthProvider provider) {
        this.provider = provider;
    }

    public Usuario login(Object credentials) {
        System.out.println("\n=== Iniciando proceso de autenticación ===");
        Usuario user = provider.authenticate(credentials);
        
        if (user != null) {
            System.out.println("✅ Login exitoso: " + user.getNombre());
        } else {
            System.out.println("❌ Login fallido");
        }
        
        return user;
    }

    public void setProvider(AuthProvider newProvider) {
        this.provider = newProvider;
    }
}
```

---

#### 4. **AuthController** (Controlador de alto nivel)
```java
package auth;

import models.Usuario;

public class AuthController {
    private AuthService authService;

    public AuthController() {
        // Por defecto usa auth local
        this.authService = new AuthService(new LocalAuthAdapter());
    }

    public Usuario loginLocal(String username, String password) {
        authService.setProvider(new LocalAuthAdapter());
        return authService.login(username);
    }

    public Usuario loginGoogle(String googleToken) {
        authService.setProvider(new GoogleAuthAdapter());
        return authService.login(googleToken);
    }
}
```

---

##  ¿Dónde se implementó?

| Componente      | Ubicación                     | Rol                                 |
| --------------- | ----------------------------- | ----------------------------------- |
| Interfaz Target | `auth/AuthProvider.java`      | Interfaz común esperada por cliente |
| Adapter Local   | `auth/LocalAuthAdapter.java`  | Adapta sistema de auth local        |
| Adapter Google  | `auth/GoogleAuthAdapter.java` | Adapta Google OAuth                 |
| Cliente         | `auth/AuthService.java`       | Usa la interfaz común               |
| Controlador     | `auth/AuthController.java`    | Coordina la autenticación           |

---

##  ¿Por qué lo implementamos así?

### Ventajas de esta implementación:

1. **Interfaz unificada**: El cliente solo conoce `AuthProvider`, no los sistemas específicos
   ```java
   // El cliente solo ve esto:
   AuthProvider provider = new LocalAuthAdapter();
   Usuario user = provider.authenticate(credentials);
   
   // No necesita saber:
   // - Cómo funciona la BD local
   // - Cómo funciona OAuth de Google
   // - Detalles de implementación
   ```

2. **Intercambiable en tiempo de ejecución**: Cambias de proveedor sin modificar código cliente
   ```java
   authService.setProvider(new LocalAuthAdapter());    // Auth local
   authService.setProvider(new GoogleAuthAdapter());   // Auth Google
   authService.setProvider(new SteamAuthAdapter());    // Fácil agregar Steam
   ```

3. **Extensible**: Agregar un nuevo proveedor es trivial
   ```java
   public class DiscordAuthAdapter implements AuthProvider {
       @Override
       public Usuario authenticate(Object credentials) {
           // Lógica específica de Discord OAuth
       }
   }
   ```

4. **Testeable**: Puedes crear un `MockAuthAdapter` para testing
   ```java
   public class MockAuthAdapter implements AuthProvider {
       @Override
       public Usuario authenticate(Object credentials) {
           return new Usuario(999, "TestUser", "test@test.com");
       }
   }
   ```

### Decisiones de diseño:

- **2 adapters iniciales**: Local (más simple) y Google (OAuth real)
- **Parámetro genérico**: `authenticate(Object credentials)` acepta cualquier tipo de credencial
  - Local: `String` (username)
  - Google: `String` (token JWT)
  - Futuro: Podría ser un objeto complejo
- **Separación de responsabilidades**: 
  - `AuthProvider`: Interfaz
  - Adapters: Adaptación específica
  - `AuthService`: Lógica de negocio
  - `AuthController`: Coordinación

---

## Comentarios Adicionales

### Diagrama de la estructura:
```
┌──────────────┐
│AuthController│
└──────┬───────┘
       │ usa
       ▼
┌──────────────┐      ┌─────────────┐
│ AuthService  │─────>│AuthProvider │ (Target Interface)
└──────────────┘ usa  └──────▲──────┘
                             │
                   ┌─────────┴─────────┐
                   │                   │
            ┌──────┴────────┐   ┌─────┴────────┐
            │LocalAuth      │   │GoogleAuth    │
            │Adapter        │   │Adapter       │
            └───────────────┘   └──────────────┘
                   │                   │
                   ▼                   ▼
            ┌──────────────┐   ┌──────────────┐
            │Sistema Local │   │Google OAuth  │
            │(Adaptee)     │   │API (Adaptee) │
            └──────────────┘   └──────────────┘
```

### Comparación de sistemas adaptados:

| Característica | LocalAuthAdapter | GoogleAuthAdapter |
|----------------|------------------|-------------------|
| **Sistema original** | Base de datos local | Google OAuth API |
| **Credencial** | Username + Password | Token JWT |
| **Complejidad** | Baja | Media |
| **Ventaja** | Simple, rápido | Seguro, sin gestionar contraseñas |
| **Uso ideal** | Desarrollo, testing | Producción |

### Ejemplo de uso real:
```java
// En Main.java

AuthController authController = new AuthController();

// Opción 1: Login local
System.out.println("\n--- Login Local ---");
Usuario user1 = authController.loginLocal("jugador1", "pass123");

// Opción 2: Login con Google
System.out.println("\n--- Login con Google ---");
Usuario user2 = authController.loginGoogle("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...");

// Salida:
// --- Login Local ---
// === Iniciando proceso de autenticación ===
// [LOCAL AUTH] Autenticando usuario: jugador1
// ✅ Login exitoso: jugador1
//
// --- Login con Google ---
// === Iniciando proceso de autenticación ===
// [GOOGLE OAUTH] Validando token con Google...
// [GOOGLE OAUTH] Token válido
// ✅ Login exitoso: GoogleUser
```

### Extensión futura (ejemplo):
```java
// Fácil agregar Steam Auth
public class SteamAuthAdapter implements AuthProvider {
    @Override
    public Usuario authenticate(Object credentials) {
        String steamId = (String) credentials;
        // Llamada a Steam Web API
        // SteamAPI.verifyTicket(steamId)
        return new Usuario(3, "SteamUser", "steam@valve.com");
    }
}

// Uso:
authController.loginSteam("STEAM_0:1:12345678");
```

---

# 5. Patrón OBSERVER

## ¿Qué problema resuelve?

Cuando un **Scrim cambia de estado** (ej: de "Buscando Jugadores" a "Lobby Completo"), múltiples partes del sistema necesitan ser notificadas:
- Usuarios suscritos deben recibir notificaciones
- Sistema de estadísticas debe actualizar métricas
- Logs deben registrar el cambio
- Frontend debe actualizar la UI

**Problema sin el patrón**: 
- El `Scrim` tendría que conocer y llamar manualmente a cada componente interesado
- Alto acoplamiento entre el sujeto y los observadores
- Difícil agregar nuevos observadores sin modificar `Scrim`

---

## ¿Cómo se implementó?

### Estructura de Archivos

```
src/models/
└── Scrim.java                  ← Subject (Observable)

src/interfaces/
└── INotifier.java              ← Observer interface

src/notifiers/
├── EmailNotifier.java          ← Observer concreto
├── DiscordNotifier.java        ← Observer concreto
└── PushNotifier.java           ← Observer concreto

src/models/
└── Notificacion.java           ← Objeto que se pasa a observers
```

### Implementación Técnica

#### 1. **Subject `Scrim`** (Observable)
```java
package models;

import java.util.ArrayList;
import java.util.List;
import states.ScrimState;
import interfaces.INotifier;

public class Scrim {
    private ScrimState estado;
    private List<Postulacion> postulaciones = new ArrayList<>();
    private List<INotifier> notifiers = new ArrayList<>();  // ← Lista de observadores

    public Scrim(ScrimState estadoInicial) {
        this.estado = estadoInicial;
    }

    // Métodos para gestionar observadores
    public void addNotifier(INotifier notifier) {
        notifiers.add(notifier);
        System.out.println("✅ Notificador agregado: " + notifier.getClass().getSimpleName());
    }

    public void removeNotifier(INotifier notifier) {
        notifiers.remove(notifier);
    }

    // Método que notifica a todos los observadores
    public void notificarCambio(Notificacion notificacion) {
        System.out.println("\n📢 Notificando cambio de estado a " + notifiers.size() + " canales:");
        for (INotifier notifier : notifiers) {
            notifier.sendNotification(notificacion);
        }
    }

    // Cuando cambia el estado, notifica automáticamente
    public void cambiarEstado(ScrimState nuevoEstado) {
        this.estado = nuevoEstado;
        
        // Crear notificación del cambio
        String mensaje = "Estado cambiado a: " + nuevoEstado.getClass().getSimpleName();
        Notificacion notif = new Notificacion("todos", mensaje);
        
        // Notificar a todos los observadores
        notificarCambio(notif);
    }

    // ... otros métodos
}
```

**Explicación**: 
- `Scrim` mantiene una lista de observadores (`notifiers`)
- Método `addNotifier()` para suscribir observadores
- Método `notificarCambio()` recorre la lista y notifica a cada uno
- `cambiarEstado()` automáticamente dispara las notificaciones

---

#### 2. **Observer Interface `INotifier`**
```java
package interfaces;

import models.Notificacion;

public interface INotifier {
    void sendNotification(Notificacion notificacion);
}
```

**Explicación**: Define el método que todos los observadores deben implementar.

---

#### 3. **Observers Concretos** (Ya vistos en Abstract Factory)

**EmailNotifier**:
```java
public class EmailNotifier implements INotifier {
    @Override
    public void sendNotification(Notificacion notificacion) {
        System.out.println("[EMAIL] ✉️  Enviando a: " + notificacion.getDestinatario());
        System.out.println("         Mensaje: " + notificacion.getMensaje());
    }
}
```

**DiscordNotifier**:
```java
public class DiscordNotifier implements INotifier {
    @Override
    public void sendNotification(Notificacion notificacion) {
        System.out.println("[DISCORD] 💬 Enviando webhook");
        System.out.println("          Mensaje: " + notificacion.getMensaje());
    }
}
```

**PushNotifier**:
```java
public class PushNotifier implements INotifier {
    @Override
    public void sendNotification(Notificacion notificacion) {
        System.out.println("[PUSH] 📱 Notificación push");
        System.out.println("       Usuario: " + notificacion.getDestinatario());
    }
}
```

---

#### 4. **Modelo `Notificacion`** (Datos del evento)
```java
package models;

public class Notificacion {
    private String destinatario;
    private String mensaje;

    public Notificacion(String destinatario, String mensaje) {
        this.destinatario = destinatario;
        this.mensaje = mensaje;
    }

    public String getDestinatario() { return destinatario; }
    public String getMensaje() { return mensaje; }
}
```

---

## ¿Dónde se implementó?

| Componente | Ubicación | Rol |
|------------|-----------|-----|
| Subject | `models/Scrim.java` | Observable que mantiene lista de observers |
| Observer Interface | `interfaces/INotifier.java` | Define el contrato para observers |
| Observers concretos | `notifiers/*.java` (3 archivos) | Implementan las notificaciones |
| Evento | `models/Notificacion.java` | Objeto que se pasa en las notificaciones |

---

## ¿Por qué lo implementamos así?

### Ventajas de esta implementación:

1. **Bajo acoplamiento**: `Scrim` no necesita conocer los detalles de cada notificador
   ```java
   // Scrim solo sabe que tiene observadores, no qué hacen
   public void notificarCambio(Notificacion n) {
       for (INotifier notifier : notifiers) {
           notifier.sendNotification(n);  // No sabe si es email, Discord, etc.
       }
   }
   ```

2. **Dinámica**: Puedes agregar/quitar observadores en tiempo de ejecución
   ```java
   scrim.addNotifier(emailNotifier);      // Agregar
   scrim.addNotifier(discordNotifier);    // Agregar
   scrim.removeNotifier(emailNotifier);   // Quitar
   ```

3. **Extensible**: Agregar un nuevo canal de notificación no modifica `Scrim`
   ```java
   // Solo crear la clase
   public class SlackNotifier implements INotifier {
       public void sendNotification(Notificacion n) {
           // Enviar a Slack
       }
   }
   
   // Usar
   scrim.addNotifier(new SlackNotifier());  // No modificas Scrim
   ```

4. **One-to-Many**: Un cambio en `Scrim` notifica automáticamente a N observadores

### Decisiones de diseño:

- **Notificadores como observers**: Los notificadores son los observadores naturales del sistema
- **Notificación automática**: `cambiarEstado()` dispara notificaciones automáticamente
- **Lista de observers**: Usamos `ArrayList` para flexibilidad (agregar/quitar dinámicamente)
- **Objeto Notificacion**: Encapsula los datos del evento (destinatario + mensaje)

---
##  Comentarios Adicionales

### Diagrama de la estructura:
```
┌─────────────────────┐
│    Scrim            │ (Subject/Observable)
├─────────────────────┤
│ -notifiers: List    │
├─────────────────────┤
│ +addNotifier()      │
│ +removeNotifier()   │
│ +notificarCambio()  │
│ +cambiarEstado()    │
└──────────┬──────────┘
           │
           │ notifica a
           │
           ▼
┌─────────────────────┐
│    INotifier        │ (Observer Interface)
├─────────────────────┤
│ +sendNotification() │
└──────────▲──────────┘
           │
    ┌──────┴───────┬──────────┐
    │              │          │
┌───┴───┐     ┌────┴────┐  ┌──┴────┐
│ Email │     │ Discord │  │ Push  │
└───────┘     └─────────┘  └───────┘
```

### Flujo de notificación:
```
1. Usuario postula a scrim
        ↓
2. Scrim llega a capacidad máxima
        ↓
3. Estado cambia: BuscandoJugadores → LobbyCompleto
        ↓
4. cambiarEstado() llama a notificarCambio()
        ↓
5. Se recorre lista de notifiers
        ↓
6. Cada notifier.sendNotification() ejecuta
        ↓
7. Se envían notificaciones por todos los canales
```

### Ejemplo de uso real:
```java
// En Main.java

// 1. Crear scrim
Scrim scrim = new Scrim(new EstadoBuscandoJugadores());

// 2. Crear factory de notificadores
NotifierFactory factory = new SimpleNotifierFactory();

// 3. Suscribir observadores (notificadores)
scrim.addNotifier(factory.createEmailNotifier());
scrim.addNotifier(factory.createDiscordNotifier());
scrim.addNotifier(factory.createPushNotifier());

// 4. Cuando el scrim cambia de estado...
scrim.cambiarEstado(new EstadoLobbyCompleto());

// Salida automática:
// 📢 Notificando cambio de estado a 3 canales:
// [EMAIL] ✉️  Enviando a: todos
//          Mensaje: Estado cambiado a: EstadoLobbyCompleto
// [DISCORD] 💬 Enviando webhook
//           Mensaje: Estado cambiado a: EstadoLobbyCompleto
// [PUSH] 📱 Notificación push
//        Usuario: todos
```

### Extensión futura (ejemplo):
```java
// Agregar sistema de logs como observer
public class LoggerNotifier implements INotifier {
    @Override
    public void sendNotification(Notificacion n) {
        // Escribir en archivo de log
        System.out.println("[LOG] " + LocalDateTime.now() + " - " + n.getMensaje());
    }
}

// Agregar sistema de métricas
public class MetricsNotifier implements INotifier {
    @Override
    public void sendNotification(Notificacion n) {
        // Actualizar métricas/estadísticas
        System.out.println("[METRICS] Evento registrado: " + n.getMensaje());
    }
}

// Uso:
scrim.addNotifier(new LoggerNotifier());
scrim.addNotifier(new MetricsNotifier());
```

### Ventaja de la integración con Abstract Factory:
El patrón Observer se integra perfectamente con Abstract Factory:
- **Abstract Factory**: Crea familias de notificadores
- **Observer**: Los usa como observadores del Scrim

Esto crea una arquitectura limpia donde:
1. Factory crea los notificadores
2. Scrim los usa como observers
3. Cambios de estado disparan notificaciones automáticamente

---

#  Conclusión General

## Resumen de los 5 Patrones

| Patrón | Problema que Resuelve | Beneficio Principal |
|--------|----------------------|---------------------|
| **STATE** | Múltiples estados con comportamientos diferentes | Elimina condicionales complejos |
| **STRATEGY** | Diferentes algoritmos intercambiables | Cambiar comportamiento en runtime |
| **ABSTRACT FACTORY** | Crear familias de objetos relacionados | Desacoplamiento total de creación |
| **ADAPTER** | Integrar sistemas con interfaces incompatibles | Interfaz unificada |
| **OBSERVER** | Notificar cambios a múltiples interesados | Bajo acoplamiento, extensible |

---

## Interacción entre Patrones

Los patrones no están aislados, trabajan juntos:

```
┌──────────────────────────────────────────────────┐
│              FLUJO COMPLETO                      │
└──────────────────────────────────────────────────┘

1. Usuario se autentica
   └─> ADAPTER (LocalAuthAdapter o GoogleAuthAdapter)

2. Crea/busca scrim
   └─> STATE (EstadoBuscandoJugadores)

3. Sistema ejecuta matchmaking
   └─> STRATEGY (ByMMRStrategy o ByLatencyStrategy)

4. Scrim cambia de estado
   └─> STATE (cambio a EstadoLobbyCompleto)
   
5. Se crean notificadores
   └─> ABSTRACT FACTORY (SimpleNotifierFactory)

6. Se envían notificaciones
   └─> OBSERVER (notifiers reciben el evento)
```

---

## Beneficios de esta Arquitectura

1. **Mantenibilidad**: Código organizado y fácil de entender
2. **Extensibilidad**: Agregar funcionalidad sin modificar lo existente
3. **Testabilidad**: Cada componente se prueba independientemente
4. **Reutilización**: Componentes reutilizables en diferentes contextos
5. **Bajo acoplamiento**: Módulos independientes con interfaces claras

---

## Para la Exposición

**Puntos clave a mencionar**:

 Identificamos 5 problemas reales del dominio  
 Aplicamos el patrón más adecuado para cada problema  
 Los patrones trabajan juntos, no aislados  
 Código limpio, extensible y mantenible  
 Principios SOLID respetados en toda la implementación  

**Demostración práctica**: Mostrar `Main.java` ejecutándose con todos los patrones funcionando en conjunto.

---

## Referencias de Código

- **Main.java**: Demo completa con los 5 patrones
- **MAPEO_DIAGRAMA.md**: Correspondencia con diagrama UML
- **GUIA_USO.md**: Ejemplos de uso detallados
- **run.bat**: Para compilar y ejecutar

---
