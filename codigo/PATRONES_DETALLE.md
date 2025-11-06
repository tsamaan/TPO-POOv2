# Patrones de Diseño Implementados - Detalles Técnicos

## 1. 🔵 PATRÓN STATE

### Propósito
Permite que un objeto altere su comportamiento cuando su estado interno cambia, pareciendo que cambia su clase.

### Estructura
```
┌─────────────────┐
│  ScrimContext   │
├─────────────────┤
│ - scrim: Scrim  │     ┌──────────────────┐
│ - estado: State │────>│ «interface»      │
├─────────────────┤     │  ScrimState      │
│ + postular()    │     ├──────────────────┤
│ + cambiarEstado()│    │ + postular(ctx)  │
└─────────────────┘     │ + iniciar(ctx)   │
                        │ + cancelar(ctx)  │
                        └──────────────────┘
                               △
                ┌──────────────┼──────────────┐
                │              │              │
   ┌────────────┴──────┐  ┌───┴────────┐  ┌──┴──────────┐
   │EstadoBuscando     │  │EstadoLobby │  │Estado       │
   │Jugadores          │  │Completo    │  │Confirmado   │
   └───────────────────┘  └────────────┘  └─────────────┘
```

### Implementación
- **Context**: `ScrimContext` - Mantiene referencia al estado actual
- **State Interface**: `ScrimState` - Define métodos comunes
- **Concrete States**: 6 clases (Buscando, LobbyCompleto, Confirmado, EnJuego, Finalizado, Cancelado)

### Ventajas
✅ Elimina condicionales complejos (if/switch)  
✅ Cada estado encapsula su propio comportamiento  
✅ Fácil agregar nuevos estados  
✅ Cumple con Open/Closed Principle  

### Código de Ejemplo
```java
// Estado controla el comportamiento
scrim.getEstado().postular(scrim);  // Comportamiento según estado actual
scrim.getEstado().iniciar(scrim);   // Transición controlada por el estado
```

---

## 2. 🟢 PATRÓN STRATEGY

### Propósito
Define una familia de algoritmos, encapsula cada uno y los hace intercambiables. Permite que el algoritmo varíe independientemente de los clientes que lo usan.

### Estructura
```
┌──────────────────────────┐
│  MatchmakingService      │
├──────────────────────────┤        ┌─────────────────────────┐
│ - estrategia: Strategy   │───────>│ «interface»             │
├──────────────────────────┤        │ IMatchMakingStrategy    │
│ + ejecutarEmparejamiento()│       ├─────────────────────────┤
└──────────────────────────┘        │ + ejecutarEmparejamiento│
                                    │   (scrim)               │
                                    └─────────────────────────┘
                                               △
                                ┌──────────────┴──────────────┐
                                │                             │
                     ┌──────────┴──────────┐    ┌────────────┴───────────┐
                     │  ByMMRStrategy      │    │  ByLatencyStrategy     │
                     │                     │    │                        │
                     │ + ejecutar...()     │    │ + ejecutar...()        │
                     └─────────────────────┘    └────────────────────────┘
```

### Implementación
- **Context**: `MatchmakingService` - Usa la estrategia
- **Strategy Interface**: `IMatchMakingStrategy` - Define algoritmo
- **Concrete Strategies**: `ByMMRStrategy`, `ByLatencyStrategy`

### Ventajas
✅ Algoritmos intercambiables en runtime  
✅ Elimina condicionales para seleccionar algoritmo  
✅ Fácil agregar nuevas estrategias  
✅ Cumple con Single Responsibility Principle  

### Código de Ejemplo
```java
// Cambiar estrategia en runtime
MatchmakingService service = new MatchmakingService(new ByMMRStrategy());
service.ejecutarEmparejamiento(scrim);  // Usa estrategia MMR

service = new MatchmakingService(new ByLatencyStrategy());
service.ejecutarEmparejamiento(scrim);  // Usa estrategia Latencia
```

---

## 3. 🔴 PATRÓN ABSTRACT FACTORY

### Propósito
Proporciona una interfaz para crear familias de objetos relacionados sin especificar sus clases concretas.

### Estructura
```
                           ┌─────────────────────────┐
                           │ «abstract»              │
                           │  NotifierFactory        │
                           ├─────────────────────────┤
                           │ + createEmailNotifier() │
                           │ + createDiscordNotifier()│
                           │ + createPushNotifier()  │
                           └─────────────────────────┘
                                      △
                                      │
                           ┌──────────┴──────────────┐
                           │ SimpleNotifierFactory   │
                           ├─────────────────────────┤
                           │ + createEmailNotifier() │───> EmailNotifier
                           │ + createDiscordNotifier()│──> DiscordNotifier
                           │ + createPushNotifier()  │───> PushNotifier
                           └─────────────────────────┘
```

### Implementación
- **Abstract Factory**: `NotifierFactory` - Define métodos de creación
- **Concrete Factory**: `SimpleNotifierFactory` - Implementa creación
- **Products**: `EmailNotifier`, `DiscordNotifier`, `PushNotifier`
- **Product Interface**: `INotifier`

### Ventajas
✅ Garantiza que productos relacionados se usen juntos  
✅ Aísla clases concretas  
✅ Facilita intercambio de familias de productos  
✅ Promueve consistencia entre productos  

### Código de Ejemplo
```java
NotifierFactory factory = new SimpleNotifierFactory();
INotifier email = factory.createEmailNotifier();
INotifier discord = factory.createDiscordNotifier();
INotifier push = factory.createPushNotifier();
// Todos creados por la misma factory, garantizando consistencia
```

---

## 4. 🟣 PATRÓN ADAPTER

### Propósito
Convierte la interfaz de una clase en otra interfaz que los clientes esperan. Permite que clases incompatibles trabajen juntas.

### Estructura
```
┌─────────────────┐       ┌──────────────────────┐
│  AuthService    │──────>│ «interface»          │
│                 │       │  AuthProvider        │
│ - providers:    │       ├──────────────────────┤
│   Map<Provider> │       │ + authenticate(creds)│
└─────────────────┘       └──────────────────────┘
                                     △
                      ┌──────────────┴──────────────┐
                      │                             │
           ┌──────────┴──────────┐      ┌──────────┴──────────┐
           │ LocalAuthAdapter    │      │ GoogleAuthAdapter   │
           ├─────────────────────┤      ├─────────────────────┤
           │ + authenticate()    │      │ + authenticate()    │
           │   [local DB logic]  │      │   [Google API call] │
           └─────────────────────┘      └─────────────────────┘
```

### Implementación
- **Target Interface**: `AuthProvider` - Interfaz común
- **Adapters**: `LocalAuthAdapter`, `GoogleAuthAdapter` - Adaptan sistemas externos
- **Client**: `AuthService` - Usa la interfaz común

### Ventajas
✅ Permite integrar sistemas incompatibles  
✅ Single Responsibility - adapter maneja adaptación  
✅ Open/Closed - agregar adapters sin modificar código existente  
✅ Desacopla cliente de implementaciones específicas  

### Código de Ejemplo
```java
AuthService service = new AuthService();
// Usa cualquier provider de forma transparente
Usuario user1 = service.loginWithProvider("local", credentials);
Usuario user2 = service.loginWithProvider("google", token);
// AuthService no conoce detalles de implementación
```

---

## 5. 👁️ PATRÓN OBSERVER (Implícito)

### Propósito
Define una dependencia uno-a-muchos entre objetos, de modo que cuando un objeto cambia su estado, todos sus dependientes son notificados automáticamente.

### Estructura
```
┌─────────────────────────┐
│  Scrim (Subject)        │
├─────────────────────────┤
│ - notifiers: List       │
│ - estado: State         │
├─────────────────────────┤         ┌─────────────────┐
│ + addNotifier(n)        │         │ «interface»     │
│ + notificarCambio(msg)  │────────>│  INotifier      │
│ + cambiarEstado(s)      │         ├─────────────────┤
└─────────────────────────┘         │ + sendNotif...()│
                                    └─────────────────┘
                                           △
                        ┌──────────────────┼──────────────────┐
                        │                  │                  │
               ┌────────┴────────┐ ┌──────┴──────┐ ┌────────┴────────┐
               │ EmailNotifier   │ │Discord...   │ │ PushNotifier    │
               └─────────────────┘ └─────────────┘ └─────────────────┘
```

### Implementación
- **Subject**: `Scrim` - Mantiene lista de observers
- **Observer Interface**: `INotifier` - Define método de notificación
- **Concrete Observers**: Email, Discord, Push notifiers

### Ventajas
✅ Desacoplamiento entre subject y observers  
✅ Broadcast de notificaciones  
✅ Observers dinámicos (agregar/quitar en runtime)  
✅ Cumple con Open/Closed Principle  

### Código de Ejemplo
```java
Scrim scrim = new Scrim(estado);
scrim.addNotifier(emailNotifier);
scrim.addNotifier(discordNotifier);

// Cuando cambia el estado, todos son notificados automáticamente
scrim.getEstado().iniciar(scrim);
// -> EmailNotifier recibe notificación
// -> DiscordNotifier recibe notificación
```

---

## 📊 Comparación de Patrones

| Patrón | Categoría | Propósito Principal | Flexibilidad |
|--------|-----------|---------------------|--------------|
| **State** | Behavioral | Cambiar comportamiento según estado | Runtime |
| **Strategy** | Behavioral | Intercambiar algoritmos | Runtime |
| **Abstract Factory** | Creational | Crear familias de objetos | Design-time |
| **Adapter** | Structural | Compatibilizar interfaces | Design-time |
| **Observer** | Behavioral | Notificación uno-a-muchos | Runtime |

---

## 🔗 Interacciones Entre Patrones

```
Usuario autenticado (ADAPTER)
    │
    ├──> Postula a Scrim
    │
    └──> Scrim cambia estado (STATE)
              │
              ├──> Notifica observers (OBSERVER)
              │         │
              │         └──> Usa notifiers de Factory (ABSTRACT FACTORY)
              │
              └──> Matchmaking ejecuta (STRATEGY)
                        │
                        └──> Puede cambiar estado nuevamente (STATE)
```

---

## 💡 Beneficios de la Arquitectura

### Mantenibilidad
- Cada patrón encapsula un aspecto del cambio
- Modificaciones localizadas sin efectos en cascada
- Código autodocumentado por estructura de patrones

### Extensibilidad
- Nuevos estados: agregar clase que implemente `ScrimState`
- Nuevas estrategias: agregar clase que implemente `IMatchMakingStrategy`
- Nuevos notificadores: agregar producto y método en factory
- Nuevos providers: agregar adapter que implemente `AuthProvider`

### Testabilidad
- Cada componente puede testearse en aislamiento
- Fácil crear mocks de interfaces
- Estrategias y estados testeables sin dependencias

### Reusabilidad
- Componentes desacoplados y cohesivos
- Interfaces claras y bien definidas
- Fácil reutilizar en otros contextos

---

## ⚙️ Principios SOLID Cumplidos

✅ **S**ingle Responsibility: Cada clase tiene una única razón para cambiar  
✅ **O**pen/Closed: Abierto a extensión, cerrado a modificación  
✅ **L**iskov Substitution: Subtipos intercambiables sin romper funcionalidad  
✅ **I**nterface Segregation: Interfaces específicas, no genéricas  
✅ **D**ependency Inversion: Depende de abstracciones, no de concreciones  

---

**Conclusión**: La combinación de estos 5 patrones crea una arquitectura robusta, flexible y mantenible para el sistema eScrims.
