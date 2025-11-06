# Guía de Uso - eScrims Platform

## 🚀 Inicio Rápido

### Opción 1: Usar el script (Windows)
```bash
cd codigo
.\run.bat
```

### Opción 2: Compilar manualmente
```bash
cd codigo

# Compilar
javac -d bin -sourcepath src src/main/Main.java

# Ejecutar
java -cp bin main.Main
```

---

## 📖 Uso de Componentes

### 1. Autenticación (Adapter Pattern)

```java
// Crear servicio de autenticación
AuthService authService = new AuthService();
AuthController authController = new AuthController(authService);

// Login local
Usuario user = authController.login("email@test.com", "password");

// Login con Google
Usuario googleUser = authController.loginWithProvider("google", "google-token");

// Registrar nuevo usuario
Usuario newUser = authController.register("username", "email", "password");
```

**Extensión**: Para agregar un nuevo proveedor (ej: Discord):
1. Crear `DiscordAuthAdapter implements AuthProvider`
2. Registrarlo en `AuthService`: `providers.put("discord", new DiscordAuthAdapter())`

---

### 2. Creación de Notificadores (Abstract Factory Pattern)

```java
// Usar factory para crear notificadores
NotifierFactory factory = new SimpleNotifierFactory();

INotifier emailNotifier = factory.createEmailNotifier();
INotifier discordNotifier = factory.createDiscordNotifier();
INotifier pushNotifier = factory.createPushNotifier();

// Enviar notificación
Notificacion notif = new Notificacion("Mensaje importante");
emailNotifier.sendNotification(notif);
```

**Extensión**: Para agregar un nuevo tipo de notificador (ej: SMS):
1. Crear `SmsNotifier implements INotifier`
2. Agregar método `createSmsNotifier()` en `NotifierFactory`
3. Implementarlo en `SimpleNotifierFactory`

---

### 3. Gestión de Scrims (State Pattern)

```java
// Crear scrim con estado inicial
ScrimState estadoInicial = new EstadoBuscandoJugadores();
Scrim scrim = new Scrim(estadoInicial);

// Agregar notificadores al scrim
scrim.addNotifier(emailNotifier);
scrim.addNotifier(discordNotifier);

// Crear contexto
ScrimContext context = new ScrimContext(scrim, estadoInicial);

// Postular jugadores
Usuario jugador = new Usuario(1, "Player1", "player@test.com");
context.postular(jugador, "Support");

// Cambiar estado
scrim.getEstado().iniciar(scrim);  // BuscandoJugadores → Confirmado
```

**Estados disponibles**:
- `EstadoBuscandoJugadores`: Acepta postulaciones
- `EstadoLobbyCompleto`: No acepta más jugadores
- `EstadoConfirmado`: Listo para iniciar
- `EstadoEnJuego`: Partida en curso
- `EstadoFinalizado`: Partida terminada
- `EstadoCancelado`: Scrim cancelado

**Transiciones permitidas**:
```
BuscandoJugadores → LobbyCompleto (cuando hay suficientes jugadores)
LobbyCompleto → Confirmado (con iniciar())
Confirmado → EnJuego (con iniciar())
EnJuego → Finalizado (con cancelar())
Cualquier estado → Cancelado (con cancelar())
```

---

### 4. Matchmaking (Strategy Pattern)

```java
// Crear servicio con estrategia por MMR
MatchmakingService mmService = new MatchmakingService(new ByMMRStrategy());
mmService.ejecutarEmparejamiento(scrim);

// Cambiar a estrategia por latencia
mmService = new MatchmakingService(new ByLatencyStrategy());
mmService.ejecutarEmparejamiento(scrim);
```

**Extensión**: Para agregar nueva estrategia (ej: por horarios):
1. Crear `ByScheduleStrategy implements IMatchMakingStrategy`
2. Implementar `ejecutarEmparejamiento(Scrim scrim)`
3. Usar en `MatchmakingService`

---

## 🔧 Casos de Uso Completos

### Caso 1: Crear un scrim completo

```java
// 1. Autenticar usuarios
AuthService authService = new AuthService();
Usuario player1 = authService.loginUser("p1@test.com", "pass");
Usuario player2 = authService.loginUser("p2@test.com", "pass");

// 2. Crear scrim
Scrim scrim = new Scrim(new EstadoBuscandoJugadores());

// 3. Configurar notificadores
NotifierFactory factory = new SimpleNotifierFactory();
scrim.addNotifier(factory.createEmailNotifier());
scrim.addNotifier(factory.createDiscordNotifier());

// 4. Postular jugadores
ScrimContext context = new ScrimContext(scrim, scrim.getEstado());
context.postular(player1, "Support");
context.postular(player2, "ADC");

// 5. Ejecutar matchmaking
MatchmakingService mmService = new MatchmakingService(new ByMMRStrategy());
mmService.ejecutarEmparejamiento(scrim);

// 6. Confirmar e iniciar
scrim.getEstado().iniciar(scrim);  // → Confirmado
scrim.getEstado().iniciar(scrim);  // → EnJuego
```

### Caso 2: Cancelar un scrim

```java
// En cualquier momento antes de finalizar
scrim.getEstado().cancelar(scrim);
// Esto notificará automáticamente a todos los suscriptores
```

---

## 📊 Estructura de Datos

### Usuario
```java
Usuario user = new Usuario(id, username, email);
Map<String, Integer> rangos = new HashMap<>();
rangos.put("League of Legends", 1800);  // MMR
user.setRangoPorJuego(rangos);
```

### Postulación
```java
Postulacion post = new Postulacion(usuario, "Mid");
// Estados: "PENDIENTE", "ACEPTADA", "RECHAZADA"
post.setEstado("ACEPTADA");
```

### Roles disponibles
- Support
- ADC (Attack Damage Carry)
- Mid
- Jungle
- Top

---

## 🎯 Testing

Para probar los componentes individualmente:

### Test de Estados
```java
Scrim scrim = new Scrim(new EstadoBuscandoJugadores());
scrim.getEstado().postular(scrim);  // Permitido
scrim.cambiarEstado(new EstadoCancelado());
scrim.getEstado().postular(scrim);  // No permitido
```

### Test de Estrategias
```java
MatchmakingService mmr = new MatchmakingService(new ByMMRStrategy());
MatchmakingService latency = new MatchmakingService(new ByLatencyStrategy());

mmr.ejecutarEmparejamiento(scrim);
latency.ejecutarEmparejamiento(scrim);
```

### Test de Notificadores
```java
NotifierFactory factory = new SimpleNotifierFactory();
INotifier notifier = factory.createEmailNotifier();
notifier.sendNotification(new Notificacion("Test"));
```

---

## ⚠️ Consideraciones Importantes

1. **Estados**: Los cambios de estado notifican automáticamente a todos los observadores
2. **Estrategias**: Se pueden cambiar en tiempo de ejecución sin afectar el servicio
3. **Notificadores**: Múltiples notificadores pueden estar suscritos a un mismo scrim
4. **Autenticación**: Los providers se registran al inicializar `AuthService`

---

## 🐛 Debugging

Si encuentras problemas:

1. **Compilación**: Verifica que todas las clases estén en los paquetes correctos
2. **Ejecución**: Asegúrate de ejecutar desde el directorio `codigo/`
3. **Estados**: Verifica que las transiciones sean válidas según el diagrama
4. **Notificaciones**: Los notificadores deben agregarse antes de cambiar estados

---

## 📝 Logging

La implementación actual usa `System.out.println` para logging. En producción, considera:
- Usar `java.util.logging` o Log4j
- Niveles de log (DEBUG, INFO, WARN, ERROR)
- Archivos de log persistentes

---

## 🔄 Próximos Pasos (Extensiones Posibles)

1. **Persistencia**: Agregar base de datos para usuarios y scrims
2. **REST API**: Exponer servicios como endpoints HTTP
3. **WebSockets**: Notificaciones en tiempo real
4. **Más estrategias**: Matchmaking por región, idioma, nivel
5. **Estadísticas**: Sistema de tracking de partidas
6. **Ranking**: Sistema de ELO/MMR dinámico

---

**¡El sistema está listo para usar!** Ejecuta `Main.java` para ver una demo completa.
