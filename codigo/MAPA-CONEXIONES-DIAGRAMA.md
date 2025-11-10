# 🔗 MAPA DE CONEXIONES - Diagrama eScrims Platform

## 📊 VISTA GENERAL DE CONEXIONES

```
┌─────────────────────────────────────────────────────────────────────┐
│                    DIAGRAMA COMPLETO - eScrims                      │
│                    41 clases + 9 patrones de diseño                 │
└─────────────────────────────────────────────────────────────────────┘

┌───────────────────────────────────────┐
│  CHAIN OF RESPONSIBILITY PATTERN      │
│  (Moderación - Color Violeta)         │
├───────────────────────────────────────┤
│                                       │
│   ModerationHandler (abstract) ◄──┐  │
│           ▲          ▲         ▲   │  │
│           │          │         │   │  │  
│           │          │         │   └──┼─ siguiente (auto-referencia)
│           │          │         │      │
│   ┌───────┴──┬───────┴───┬─────┴────┐│
│   │          │           │          ││
│ AutoResolver BotModerador ModHumano ││
│ Handler      Handler      Handler   ││
│                                      ││
│              │                       ││
│              └───────────────────────┼┼─────┐
│                                      ││     │
└──────────────────────────────────────┘│     │
                                        │     │
                                        │     ▼
┌───────────────────────────────────────┼─────────────┐
│  MODELOS DE DOMINIO                   │             │
│  (Color Naranja)                      │             │
├───────────────────────────────────────┼─────────────┤
│                                       │             │
│   ReporteConducta ◄───────────────────┘             │
│         │                                           │
│         ├── reportante ──────► Usuario              │
│         ├── reportado ───────► Usuario              │
│         └── scrimId ──────────► Scrim               │
│                                   ▲                 │
│                                   │                 │
│   Scrim ◄─────────────────────────┼─────┐           │
│     │                             │     │           │
│     ├── creador ─────► Usuario    │     │           │
│     ├── postulaciones ► Postulacion│    │           │
│     └── estrategia ───┐            │     │           │
│                       │            │     │           │
└───────────────────────┼────────────┼─────┼───────────┘
                        │            │     │
                        │            │     │
┌───────────────────────┼────────────┼─────┼───────────┐
│  STRATEGY PATTERN                  │     │           │
│  (Matchmaking - Color Verde)       │     │           │
├────────────────────────────────────┼─────┼───────────┤
│                                    │     │           │
│   IMatchMakingStrategy ◄───────────┘     │           │
│           ▲          ▲         ▲         │           │
│           │          │         │         │           │
│   ┌───────┴──┬───────┴───┬─────┴────┐   │           │
│   │          │           │          │   │           │
│ ByRanking  ByLatency  ByHistory    │   │           │
│ Strategy   Strategy   Strategy ⭐   │   │           │
│                                     │   │           │
└─────────────────────────────────────┼───┼───────────┘
                                      │   │
                                      │   │
┌─────────────────────────────────────┼───┼───────────┐
│  ADAPTER PATTERN                    │   │           │
│  (iCalendar - Color Verde)          │   │           │
├─────────────────────────────────────┼───┼───────────┤
│                                     │   │           │
│   ICalendarAdapter ─────────────────┼───┘           │
│         │                           │               │
│         └── toICalendar() ──────────┘               │
│         └── guardarArchivo()                        │
│                                                     │
└─────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────┐
│  TEMPLATE METHOD PATTERN                            │
│  (Validadores - Color Azul)                         │
├─────────────────────────────────────────────────────┤
│                                                     │
│   GameValidator (abstract) ──────────┐             │
│           ▲          ▲               │             │
│           │          │               │             │
│   ┌───────┴──┬───────┴───┐          │             │
│   │          │           │          │             │
│ LoL      Valorant       │          │             │
│ Validator Validator     │          │             │
│                         │          │             │
│                         └──────────┼─────┐       │
└─────────────────────────────────────┼─────┼───────┘
                                      │     │
                                      ▼     │
                                    Scrim ◄─┘


┌─────────────────────────────────────────────────────┐
│  COMMAND PATTERN                                    │
│  (Operaciones - Color Violeta)                      │
├─────────────────────────────────────────────────────┤
│                                                     │
│   ScrimCommand (interface)                          │
│           ▲          ▲                              │
│           │          │                              │
│   ┌───────┴──┬───────┴──────┐                      │
│   │          │              │                      │
│ AsignarRol SwapJugadores   │                      │
│ Command    Command         │                      │
│   │          │              │                      │
│   └──────────┴──────────────┼──────► Scrim        │
│                             │                      │
│                             └──────► Usuario       │
└─────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────┐
│  STATE PATTERN                                      │
│  (Estados del Scrim - Color Azul)                   │
├─────────────────────────────────────────────────────┤
│                                                     │
│   ScrimState (interface)                            │
│           ▲    ▲    ▲    ▲    ▲    ▲               │
│           │    │    │    │    │    │               │
│   Buscando Confirmado EnCurso Finalizado ...       │
│   State    State    State  State                   │
│                                                     │
│   ScrimContext ────► ScrimState                    │
│        │                                            │
│        └────────────► Scrim                        │
└─────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────┐
│  OBSERVER PATTERN                                   │
│  (Notificaciones - Color Rojo)                      │
├─────────────────────────────────────────────────────┤
│                                                     │
│   INotifier (interface)                             │
│           ▲    ▲    ▲    ▲                          │
│           │    │    │    │                          │
│   Email SMS Push Discord                           │
│   Notif Notif Notif Notif                          │
│                                                     │
│   MultiChannelNotifier (Composite)                  │
│           │                                         │
│           └────► List<INotifier>                   │
│                                                     │
│   NotificationService ──► INotifier                │
│           │                                         │
│           └────────────► Usuario                   │
└─────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────┐
│  FACTORY PATTERN                                    │
│  (Creación de Notificadores - Color Rosa)           │
├─────────────────────────────────────────────────────┤
│                                                     │
│   NotifierFactory (abstract)                        │
│           ▲    ▲    ▲    ▲                          │
│           │    │    │    │                          │
│   Email SMS Push Discord                           │
│   Factory Factory Factory Factory                  │
│      │     │     │     │                           │
│      └─────┴─────┴─────┴──────► INotifier         │
│                                                     │
└─────────────────────────────────────────────────────┘
```

---

## 🔗 TABLA DE RELACIONES COMPLETAS

| Desde | Relación | Hacia | Tipo | Descripción |
|-------|----------|-------|------|-------------|
| **Chain of Responsibility** |
| AutoResolverHandler | extends | ModerationHandler | Herencia | Primer handler de la cadena |
| BotModeradorHandler | extends | ModerationHandler | Herencia | Segundo handler (IA) |
| ModeradorHumanoHandler | extends | ModerationHandler | Herencia | Último handler (manual) |
| ModerationHandler | siguiente | ModerationHandler | Asociación | Auto-referencia para cadena |
| ModerationHandler | procesa | ReporteConducta | Dependencia | Procesa reportes |
| **Adapter** |
| ICalendarAdapter | exporta | Scrim | Dependencia | Convierte a .ics |
| **Template Method** |
| LoLValidator | extends | GameValidator | Herencia | Validador League of Legends |
| ValorantValidator | extends | GameValidator | Herencia | Validador Valorant |
| GameValidator | valida | Scrim | Dependencia | Valida configuración |
| **Strategy** |
| ByHistoryStrategy ⭐ | implements | IMatchMakingStrategy | Implementación | Matchmaking por historial |
| ByRankingStrategy | implements | IMatchMakingStrategy | Implementación | Matchmaking por ranking |
| ByLatencyStrategy | implements | IMatchMakingStrategy | Implementación | Matchmaking por latencia |
| MatchmakingService | usa | IMatchMakingStrategy | Dependencia | Ejecuta estrategia |
| **Command** |
| AsignarRolCommand | implements | ScrimCommand | Implementación | Asigna rol con undo |
| SwapJugadoresCommand | implements | ScrimCommand | Implementación | Intercambia jugadores |
| AsignarRolCommand | opera sobre | Scrim | Dependencia | Modifica scrim |
| SwapJugadoresCommand | opera sobre | Scrim | Dependencia | Modifica scrim |
| **Modelos** |
| ReporteConducta | reportante | Usuario | Asociación | Quien reporta |
| ReporteConducta | reportado | Usuario | Asociación | Quien es reportado |
| ReporteConducta | scrimId | Scrim | Asociación | Scrim donde ocurrió |
| Scrim | creador | Usuario | Asociación | Creador del scrim |
| Scrim | postulaciones | Postulacion | Composición | Lista de postulaciones |
| Postulacion | usuario | Usuario | Asociación | Jugador postulado |

---

## 📈 ESTADÍSTICAS DEL DIAGRAMA COMPLETO

```
┌────────────────────────────────────────┐
│ MÉTRICAS DEL DIAGRAMA                  │
├────────────────────────────────────────┤
│ Total de Clases:        41             │
│ Total de Interfaces:     4             │
│ Total de Patrones:       9             │
│ Clases Abstractas:       3             │
│ Relaciones Herencia:    22             │
│ Relaciones Dependencia: 15             │
│ Relaciones Asociación:  12             │
│ Clases Nuevas Hoy:       8             │
└────────────────────────────────────────┘

┌────────────────────────────────────────┐
│ COBERTURA DE PATRONES                  │
├────────────────────────────────────────┤
│ ✅ State             - 7 clases        │
│ ✅ Strategy          - 4 clases        │
│ ✅ Observer          - 6 clases        │
│ ✅ Factory           - 6 clases        │
│ ✅ Composite         - 1 clase         │
│ ⭐ Chain of Resp.   - 4 clases (NEW)  │
│ ⭐ Command          - 3 clases         │
│ ⭐ Template Method  - 3 clases (NEW)  │
│ ⭐ Adapter          - 1 clase  (NEW)  │
├────────────────────────────────────────┤
│ TOTAL: 35 clases en patrones           │
│ (85% del proyecto)                     │
└────────────────────────────────────────┘
```

---

## 🎯 PUNTOS CLAVE DE CONEXIÓN

### 1️⃣ **Hub Central: Scrim**
La clase `Scrim` es el centro del diagrama, conectada a:
- ✅ ReporteConducta (reportes sobre scrims)
- ✅ ICalendarAdapter (exportación)
- ✅ GameValidator (validación)
- ✅ ScrimCommand (operaciones)
- ✅ IMatchMakingStrategy (matchmaking)
- ✅ ScrimState (estados)
- ✅ Usuario (creador)
- ✅ Postulacion (lista de postulados)

### 2️⃣ **Hub Secundario: Usuario**
La clase `Usuario` conecta:
- ✅ ReporteConducta (reportante y reportado)
- ✅ Scrim (creador)
- ✅ Postulacion (quien se postula)
- ✅ ScrimCommand (afectado por comandos)
- ✅ NotificationService (recibe notificaciones)

### 3️⃣ **Nuevos Patrones Independientes**
- ✅ Chain of Responsibility: Cadena completa con 4 clases
- ✅ Template Method: Jerarquía de 3 clases
- ✅ Adapter: Clase única conectada a Scrim

---

## 🎨 ORGANIZACIÓN VISUAL SUGERIDA

```
┌─────────────────────────────────────────────────────────────┐
│                                                             │
│  [State Pattern]        [Strategy Pattern]                 │
│  Estados (Azul)         Matchmaking (Verde)                │
│        │                        │                           │
│        └────────────────────────┼────► [SCRIM] ◄───┐      │
│                                 │         ▲         │      │
│                                 │         │         │      │
│  [Chain of Resp.]               │         │    [Adapter]  │
│  Moderación (Violeta)           │         │    iCal (Verde)│
│        │                        │         │                │
│        └──► [ReporteConducta]   │         │                │
│                                 │         │                │
│                                 │    [Template Method]     │
│                                 │    Validadores (Azul)    │
│                                 │                           │
│  [Command Pattern]              │                           │
│  Operaciones (Violeta)          │                           │
│        │                        │                           │
│        └────────────────────────┘                           │
│                                                             │
│  [Observer + Factory]                                       │
│  Notificaciones (Rojo + Rosa)                               │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## ✅ CHECKLIST FINAL DE CONEXIONES

Usa este checklist para verificar que TODO esté conectado:

### Modelos de Dominio
- [ ] Scrim conectado a Usuario (creador)
- [ ] Scrim conectado a Postulacion (composición)
- [ ] Postulacion conectado a Usuario
- [ ] ReporteConducta → Usuario (reportante)
- [ ] ReporteConducta → Usuario (reportado)
- [ ] ReporteConducta → Scrim (scrimId)

### Chain of Responsibility (NUEVO)
- [ ] AutoResolverHandler → ModerationHandler (herencia)
- [ ] BotModeradorHandler → ModerationHandler (herencia)
- [ ] ModeradorHumanoHandler → ModerationHandler (herencia)
- [ ] ModerationHandler → siguiente (auto-referencia)
- [ ] ModerationHandler → ReporteConducta (dependencia)

### Adapter (NUEVO)
- [ ] ICalendarAdapter → Scrim (dependencia)

### Template Method (NUEVO)
- [ ] LoLValidator → GameValidator (herencia)
- [ ] ValorantValidator → GameValidator (herencia)
- [ ] GameValidator → Scrim (dependencia)

### Strategy
- [ ] ByHistoryStrategy → IMatchMakingStrategy (implementa) ⭐
- [ ] ByRankingStrategy → IMatchMakingStrategy (implementa)
- [ ] ByLatencyStrategy → IMatchMakingStrategy (implementa)
- [ ] MatchmakingService → IMatchMakingStrategy (usa)
- [ ] IMatchMakingStrategy → Scrim (dependencia)

### Command
- [ ] AsignarRolCommand → ScrimCommand (implementa)
- [ ] SwapJugadoresCommand → ScrimCommand (implementa)
- [ ] AsignarRolCommand → Scrim (dependencia)
- [ ] SwapJugadoresCommand → Scrim (dependencia)
- [ ] AsignarRolCommand → Usuario (dependencia)
- [ ] SwapJugadoresCommand → Usuario (dependencia)

### State
- [ ] Todos los estados → ScrimState (implementa)
- [ ] ScrimContext → ScrimState (usa)
- [ ] ScrimContext → Scrim (composición)

### Observer + Factory
- [ ] Todos los notificadores → INotifier (implementa)
- [ ] Todas las factories → NotifierFactory (herencia)
- [ ] NotificationService → INotifier (usa)
- [ ] MultiChannelNotifier → INotifier (composición)

---

## 🚀 ¡DIAGRAMA COMPLETO!

Cuando termines de integrar, tendrás un diagrama UML profesional con:

✅ **41 clases** organizadas  
✅ **9 patrones** de diseño  
✅ **~50 relaciones** bien conectadas  
✅ **0 clases aisladas** - todo conectado lógicamente  
✅ **Layout profesional** por patrones  

**¡Listo para entregar y sacar 10!** 🎓⭐
