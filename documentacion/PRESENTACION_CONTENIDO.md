# Contenido para Presentación PowerPoint - eScrims Platform

## Estructura Visual y Narrativa para PPTX

---

## 🎯 DIAPOSITIVA 1: INTRODUCCIÓN AL PROYECTO E-SCRIM

### Título de la diapositiva:
**"eScrims: Cuando los Jugadores se Encuentran"**

### Contenido (máximo 4 bullets):

**El Problema:**
- Miles de jugadores de eSports buscan partidas de práctica
- Sistemas caóticos, matchmaking deficiente, experiencia frustrante

**Nuestra Solución:**
- Plataforma de matchmaking inteligente para Scrims (partidas de práctica)
- Autenticación múltiple, equipos balanceados, notificaciones en tiempo real

**¿Por qué es especial?**
- 6 patrones de diseño trabajando en armonía
- Arquitectura escalable, extensible y mantenible

### Notas del presentador:
"Imaginen un jugador competitivo que quiere practicar. Abre la plataforma, se autentica con Google, selecciona su rol preferido, y en segundos está en un equipo balanceado recibiendo notificaciones por Discord, email y push. Esto es eScrims, y detrás hay una arquitectura cuidadosamente diseñada con patrones que resuelven problemas reales."

---

## ⚙️ DIAPOSITIVA 2: PATRONES STATE Y STRATEGY

### Título de la diapositiva:
**"State & Strategy: El Cerebro del Matchmaking"**

### SUB-SECCIÓN: Patrón STATE

#### Contenido:

**¿Qué resuelve?**
- Un Scrim tiene 6 estados: Buscando → Lobby Completo → Confirmado → En Juego → Finalizado (+ Cancelado)
- Sin State: código lleno de `if/else`, imposible de mantener

**¿Por qué lo usamos?**
- Cada estado define su propio comportamiento
- Transiciones claras y automáticas

**Sin State, tendríamos:**
```
❌ Código espagueti con 50+ condicionales
❌ Bug al agregar un nuevo estado
❌ Comportamiento impredecible
```

**Con State:**
```
✅ Cada estado = 1 clase responsable
✅ Agregar estado = crear nueva clase
✅ Transiciones explícitas
```

**Dónde está en el código:**
- `states/ScrimState.java` (interfaz)
- `states/EstadoBuscandoJugadores.java`
- `states/EstadoLobbyCompleto.java`
- `states/EstadoConfirmado.java`
- `states/EstadoEnJuego.java`
- `states/EstadoFinalizado.java`
- `states/EstadoCancelado.java`
- `context/ScrimContext.java` (coordinador)

[AQUÍ VA CAPTURA: Carpeta states/ con los 6 estados + interfaz ScrimState]

---

### SUB-SECCIÓN: Patrón STRATEGY

#### Contenido:

**¿Qué resuelve?**
- Diferentes algoritmos de matchmaking (por habilidad, por ping, por región...)
- Sin Strategy: cambiar algoritmo = reescribir código

**¿Por qué lo usamos?**
- Algoritmos intercambiables en tiempo de ejecución
- Partidas competitivas → MMR Strategy
- Partidas casuales → Latency Strategy

**Sin Strategy:**
```
❌ if (tipoPartida == "ranked") { código MMR }
❌ else if (tipoPartida == "casual") { código latencia }
❌ Modificar servicio para cada nuevo algoritmo
```

**Con Strategy:**
```
✅ matchmaking.setStrategy(new ByMMRStrategy());
✅ matchmaking.setStrategy(new ByLatencyStrategy());
✅ Nuevos algoritmos sin tocar código existente
```

**Dónde está en el código:**
- `interfaces/IMatchMakingStrategy.java`
- `strategies/ByMMRStrategy.java`
- `strategies/ByLatencyStrategy.java`
- `service/MatchmakingService.java`

[AQUÍ VA CAPTURA: MatchmakingService con las estrategias]

### Notas del presentador:
"State es el ciclo de vida del Scrim: desde que se crea hasta que termina. Strategy es cómo emparejamos jugadores: ¿priorizamos habilidad similar o mejor ping? Estos dos patrones son el corazón del matchmaking."

---

## 🏭 DIAPOSITIVA 3: PATRÓN ABSTRACT FACTORY

### Título de la diapositiva:
**"Abstract Factory: La Fábrica de Notificadores"**

#### Contenido:

**¿Qué resuelve?**
- Crear familias de objetos relacionados (Email, Discord, Push Notifiers)
- Sin Factory: código acoplado a implementaciones concretas

**¿Por qué lo usamos?**
- Desacoplamiento total: el código no conoce las clases concretas
- Cambiar entre configuraciones (Dev/Testing/Prod) con 1 línea
- Extensible: agregar Telegram/Slack = 1 nueva clase

**Sin Abstract Factory:**
```
❌ EmailNotifier email = new EmailNotifier();
❌ DiscordNotifier discord = new DiscordNotifier();
❌ Código acoplado a clases concretas
❌ Difícil mockear en tests
```

**Con Abstract Factory:**
```
✅ NotifierFactory factory = new SimpleNotifierFactory();
✅ INotifier email = factory.createEmailNotifier();
✅ Código depende de abstracciones, no implementaciones
✅ Fácil crear MockNotifierFactory para tests
```

**Lo que pasaría sin este patrón:**
- Cada vez que agregamos un canal, modificamos 10 clases
- Testing complicado (emails reales en tests)
- Imposible cambiar implementaciones dinámicamente

**Dónde está en el código:**
- `notifiers/NotifierFactory.java` (abstract)
- `notifiers/SimpleNotifierFactory.java` (concrete)
- `notifiers/EmailNotifier.java`
- `notifiers/DiscordNotifier.java`
- `notifiers/PushNotifier.java`

[AQUÍ VA CAPTURA: NotifierFactory y sus productos]

### Notas del presentador:
"Necesitamos notificar por email, Discord y push. Abstract Factory crea estas familias de objetos sin que el código sepa los detalles. ¿Quieres agregar Telegram? Una clase nueva, sin tocar nada más. ¿Tests con mocks? Cambias la factory, listo."

---

## 🔌 DIAPOSITIVA 4: PATRÓN ADAPTER

### Título de la diapositiva:
**"Adapter: Traduciendo el Lenguaje de la Autenticación"**

#### Contenido:

**¿Qué resuelve?**
- Integrar sistemas con interfaces incompatibles
- Autenticación local (username/password) vs Google OAuth (tokens JWT)
- Sin Adapter: el código debe conocer cada sistema específico

**¿Por qué lo usamos?**
- Interfaz unificada: `AuthProvider.authenticate()`
- El cliente no sabe si es local, Google, Steam o Discord
- Intercambiable en tiempo de ejecución

**Sin Adapter:**
```
❌ if (provider == "local") {
❌     LocalAuth.validatePassword(user, pass);
❌ } else if (provider == "google") {
❌     GoogleAPI.verifyToken(token);
❌ } // Código acoplado a cada sistema
```

**Con Adapter:**
```
✅ AuthProvider provider = new GoogleAuthAdapter();
✅ Usuario user = provider.authenticate(credentials);
✅ // Mismo código para todos los proveedores
```

**Lo que pasaría sin este patrón:**
- Agregar Steam Auth = reescribir todo el sistema
- Código cliente acoplado a 5+ sistemas diferentes
- Violación del principio de inversión de dependencias

**Dónde está en el código:**
- `auth/AuthProvider.java` (interfaz target)
- `auth/LocalAuthAdapter.java`
- `auth/GoogleAuthAdapter.java`
- `auth/AuthService.java` (cliente)
- `auth/AuthController.java` (coordinador)

[AQUÍ VA CAPTURA: AuthProvider con los dos adapters]

### Notas del presentador:
"Dos sistemas de autenticación completamente diferentes: uno usa contraseñas, otro tokens de Google. Adapter los traduce a un lenguaje común. El resto del código no sabe la diferencia. ¿Agregar Discord OAuth? Un nuevo adapter, sin modificar el AuthService."

---

## 📡 DIAPOSITIVA 5: PATRÓN OBSERVER

### Título de la diapositiva:
**"Observer: Cuando Todo el Mundo Debe Saberlo"**

#### Contenido:

**¿Qué resuelve?**
- Cuando el Scrim cambia de estado, múltiples componentes deben saberlo
- Notificadores, logs, métricas, frontend... todos necesitan reaccionar
- Sin Observer: el Scrim debe conocer y llamar a cada uno manualmente

**¿Por qué lo usamos?**
- One-to-Many: 1 cambio notifica a N observadores
- Bajo acoplamiento: Scrim no conoce los detalles de cada notificador
- Dinámico: agregar/quitar observadores en runtime

**Sin Observer:**
```
❌ public void cambiarEstado() {
❌     emailService.send();
❌     discordService.send();
❌     pushService.send();
❌     logService.write();
❌     // Scrim acoplado a 20+ clases
❌ }
```

**Con Observer:**
```
✅ public void cambiarEstado() {
✅     for (INotifier n : notifiers) {
✅         n.sendNotification(evento);
✅     }
✅ } // Scrim no sabe qué notificadores hay
```

**Lo que pasaría sin este patrón:**
- Agregar Slack notifications = modificar clase Scrim
- Scrim conoce y depende de 15+ clases
- Imposible agregar observadores dinámicamente

**Dónde está en el código:**
- `models/Scrim.java` (subject/observable)
  - `addNotifier()`, `removeNotifier()`, `notificarCambio()`
- `interfaces/INotifier.java` (observer)
- `notifiers/EmailNotifier.java` (observer concreto)
- `notifiers/DiscordNotifier.java` (observer concreto)
- `notifiers/PushNotifier.java` (observer concreto)

[AQUÍ VA CAPTURA: Scrim.java con la lista de notifiers y el método notificarCambio()]

### Notas del presentador:
"El Scrim cambia a 'Lobby Completo'. En ese momento, se envían emails, webhooks de Discord, notificaciones push, se actualiza el log, se registran métricas. Observer hace que esto sea automático y desacoplado. El Scrim solo dice 'cambié de estado', y todos los interesados reaccionan."

---

## ⚡ DIAPOSITIVA 6: PATRÓN COMMAND

### Título de la diapositiva:
**"Command: Acciones Reversibles con Historial"**

#### Contenido:

**¿Qué resuelve?**
- El organizador necesita ajustar roles antes de iniciar el scrim
- Cambiar rol de un jugador, intercambiar roles entre dos jugadores
- Sin Command: cambios directos sin posibilidad de deshacer

**¿Por qué lo usamos?**
- Encapsula acciones como objetos ejecutables
- Historial de comandos con capacidad de UNDO
- Separación entre quien solicita la acción y quien la ejecuta

**Sin Command:**
```
❌ jugador.setRol("Tank"); // No hay forma de deshacer
❌ if (errorEnFormacion) { 
❌     // ¿Cómo restauro el estado anterior?
❌ }
```

**Con Command:**
```
✅ AsignarRolCommand cmd = new AsignarRolCommand(jugador, "Tank");
✅ commandManager.ejecutarComando(cmd); // Ejecuta y guarda
✅ commandManager.deshacerUltimo(); // Restaura automáticamente
```

**Lo que pasaría sin este patrón:**
- Imposible deshacer cambios de configuración
- Código acoplado entre UI y lógica de negocio
- No hay registro de acciones realizadas

**Dónde está en el código:**
- `interfaces/IScrimCommand.java` (interfaz command)
  - `execute(ctx)`, `undo(ctx)`
- `commands/AsignarRolCommand.java` (comando concreto)
- `commands/SwapJugadoresCommand.java` (comando concreto)
- `commands/CommandManager.java` (invoker con historial)
- `models/Usuario.java` - agregado atributo `rol`

[AQUÍ VA CAPTURA: IScrimCommand con los dos métodos + AsignarRolCommand]

### Notas del presentador:
"Imaginen que el organizador asigna roles: 'Tú Tank, tú Support'. Pero se equivoca. Con Command, solo presiona 'Deshacer' y todo vuelve al estado anterior. Cada acción es un objeto con memoria. Esto es especialmente útil antes de la fase de confirmación, donde los ajustes son críticos. El CommandManager mantiene un stack de todas las acciones, permitiendo deshacer una por una o todas a la vez."

---

## 🎬 DIAPOSITIVA 7: DEMO EN VIVO

### Título de la diapositiva:
**"Ver es Creer: Los 6 Patrones en Acción"**

#### Contenido minimalista (solo bullets visuales):

**Lo que verás:**

1. 🔐 **Login** → Adapter traduce autenticación
2. 🎮 **Selección de rol** → State gestiona postulación
3. 🤖 **Matchmaking automático** → Strategy empareja jugadores
4. 👥 **Equipos formados** → State transiciona a Lobby Completo
5. ⚡ **Ajuste de roles** → Command permite cambios con undo
6. 📢 **Notificaciones** → Observer/Factory envían por 3 canales
7. ⚔️ **Partida en juego** → State gestiona ciclo completo
8. 📊 **Estadísticas finales** → State finaliza y muestra resultados

**¿Qué hace especial esta demo?**
- 6 patrones trabajando juntos, no aislados
- Arquitectura real, no juguete académico
- Extensible: agregar features sin romper código

### Notas del presentador:
"Ahora vamos a ver todo esto en acción. Voy a ejecutar Main.java y veremos cómo un usuario se autentica, busca partida, el sistema encuentra jugadores, forma equipos balanceados, todos confirman, juegan la partida y vemos las estadísticas. En cada paso, los patrones están trabajando silenciosamente."

[AQUÍ VA: Demo en vivo - sin captura]

---

## 🌟 DIAPOSITIVA 7: CONCLUSIÓN - "EL PODER DE LOS PATRONES"

### Título de la diapositiva:
**"De Jugadores Frustrados a Plataforma Escalable"**

#### Contenido narrativo:

**El Viaje:**

🎯 **Comenzamos con un problema real**
- Jugadores necesitan matchmaking confiable
- Sistema debe ser flexible y extensible

⚙️ **Identificamos 6 desafíos técnicos**
- Estados complejos → STATE
- Algoritmos variables → STRATEGY  
- Crear notificadores → ABSTRACT FACTORY
- Integrar auth sistemas → ADAPTER
- Notificar cambios → OBSERVER
- Acciones reversibles → COMMAND

🏗️ **Construimos una arquitectura sólida**
- Cada patrón resuelve 1 problema específico
- Los patrones se complementan entre sí
- Código limpio, testeable y mantenible

**¿Qué ganamos?**

✅ **Extensibilidad**: Nuevos features sin modificar existente  
✅ **Mantenibilidad**: Código organizado y comprensible  
✅ **Escalabilidad**: De 100 a 10,000 usuarios sin cambios  
✅ **Testabilidad**: Cada componente se prueba independientemente  

**El Resultado:**

> "No construimos solo una aplicación de matchmaking.
> Construimos un sistema que evoluciona con las necesidades de los jugadores."

**¿Qué sigue?**
- Implementar ranking global
- Machine Learning para matchmaking predictivo
- Integración con más plataformas (Steam, Epic Games)

**La arquitectura ya está lista para crecer.**

---

### Slide final (cierre emocional):

**"Los patrones de diseño no son reglas académicas..."**

**"Son herramientas que transforman código frágil en sistemas robustos."**

**"Son la diferencia entre una app que funciona hoy y una plataforma que escala mañana."**

**"Esto es eScrims. Esto es ingeniería de software real."**

---

## 📝 NOTAS GENERALES PARA EL PRESENTADOR

### Timing sugerido (25-30 minutos):

1. **Introducción** (2 min)
2. **State & Strategy** (4 min)
3. **Abstract Factory** (3 min)
4. **Adapter** (3 min)
5. **Observer** (3 min)
6. **Command** (3 min)
7. **Demo en vivo** (5-7 min)
8. **Conclusión** (2-3 min)
9. **Preguntas** (resto)

### Tips de presentación:

**Storytelling:**
- Comienza cada patrón con el problema (dolor del jugador/desarrollador)
- Muestra el "mundo sin el patrón" (caos, bugs, acoplamiento)
- Revela la solución (elegancia, simplicidad)
- Termina con el código real (prueba tangible)

**Engagement:**
- Usa analogías: "State es como un semáforo, Strategy como diferentes rutas en GPS"
- Pregunta retórica: "¿Qué pasa si queremos agregar autenticación con Steam?"
- Pausa dramática antes de la demo

**Técnicas visuales:**
- Colores consistentes por patrón (ej: State=azul, Strategy=verde)
- Íconos claros (🔐 auth, 🎮 juego, 📢 notificación)
- Animaciones simples en bullets (aparecer uno por uno)

**Manejo de preguntas:**
- "¿Por qué no usar if/else en State?" → Mostrar código espagueti vs código limpio
- "¿Cuándo NO usar estos patrones?" → Proyecto simple, sin necesidad de extensión
- "¿Cómo testean cada patrón?" → Mencionar mocks en Factory, tests unitarios por estado

---

## 🎨 SUGERENCIAS VISUALES PARA PPTX

### Paleta de colores por patrón:
- **STATE**: Azul (#2196F3) - "flujo, transición"
- **STRATEGY**: Verde (#4CAF50) - "decisión, elección"
- **ABSTRACT FACTORY**: Naranja (#FF9800) - "creación, construcción"
- **ADAPTER**: Morado (#9C27B0) - "conexión, puente"
- **OBSERVER**: Rojo (#F44336) - "alerta, notificación"
- **COMMAND**: Amarillo (#FFC107) - "acción, historial"

### Íconos recomendados:
- 🎯 Objetivo/Problema
- ⚙️ Solución técnica
- ❌ Sin patrón (consecuencias)
- ✅ Con patrón (beneficios)
- 📍 Ubicación en código
- 🔐 Autenticación
- 🎮 Gaming/eSports
- 📢 Notificaciones
- 👥 Equipos/Usuarios
- 🏆 Competitivo/Matchmaking
- ⚡ Comando/Acción
- ⏮️ Undo/Redo

### Tipos de diapositivas:
1. **Título grande + imagen de fondo** (Intro, Conclusión)
2. **Dos columnas** (Sin patrón vs Con patrón)
3. **Lista con íconos** (Patrones individuales)
4. **Captura de código centrada** (Dónde está implementado)
5. **Diagrama simple** (Flujo/Arquitectura)

---

## 📸 CAPTURAS DE CÓDIGO SUGERIDAS

### Para cada patrón, capturar:

**STATE:**
1. Carpeta `states/` con los 6 estados (+ interfaz ScrimState)
2. Método `cambiarEstado()` en `ScrimState.java`
3. Implementación de `EstadoBuscandoJugadores.java`

**STRATEGY:**
1. Interfaz `IMatchMakingStrategy.java`
2. Constructor de `MatchmakingService` con inyección de estrategia
3. Comparación lado a lado de `ByMMRStrategy` y `ByLatencyStrategy`

**ABSTRACT FACTORY:**
1. Abstract class `NotifierFactory.java` con los 3 métodos
2. Implementación de `SimpleNotifierFactory.java`
3. Los 3 notificadores en una vista de carpeta

**ADAPTER:**
1. Interfaz `AuthProvider.java`
2. Implementación de `GoogleAuthAdapter.java` (método authenticate)
3. Uso en `AuthController.java` cambiando providers

**OBSERVER:**
1. Lista de notifiers en `Scrim.java`
2. Método `notificarCambio()` con el loop
3. Implementación de un notificador (ej: `DiscordNotifier.java`)

---

## 🎤 FRASES DE CIERRE PODEROSAS

Opciones para terminar con impacto:

**Opción 1 (Reflexiva):**
"Hace unos meses, teníamos un problema de matchmaking. Hoy, tenemos una arquitectura que puede escalar a millones de usuarios. Los patrones de diseño no son teoría, son la diferencia."

**Opción 2 (Inspiradora):**
"Cada línea de código cuenta una historia. Con estos 5 patrones, contamos la historia de un sistema que se adapta, que crece, que nunca deja de evolucionar."

**Opción 3 (Práctica):**
"Cuando un nuevo desarrollador se une al equipo, no necesita 3 semanas para entender el código. Necesita 3 horas. Eso es lo que los patrones bien aplicados te dan: claridad instantánea."

**Opción 4 (Técnica con corazón):**
"Al final del día, no estamos construyendo software por construir. Estamos creando experiencias para jugadores. Y la única forma de hacerlo sosteniblemente es con una arquitectura que respete tanto al usuario como al desarrollador."

**Recomendación:** Usa **Opción 4** para conectar lo técnico con lo humano.

---

## ✅ CHECKLIST FINAL ANTES DE PRESENTAR

- [ ] Compilar y probar Main.java (¡que funcione sin errores!)
- [ ] Tener terminal lista con comando de ejecución
- [ ] Capturas de código bien iluminadas y legibles
- [ ] Practicar transiciones entre diapositivas
- [ ] Tiempo de demo controlado (5-7 min max)
- [ ] Respuestas preparadas para preguntas comunes
- [ ] Backup plan si falla la demo (video grabado)
- [ ] Agua/café a mano
- [ ] Respirar profundo antes de comenzar 😊

---

**¡Éxito en tu presentación! 🚀**

