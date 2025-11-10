# 🎨 GUÍA: Actualización del Diagrama UML

## 📍 UBICACIÓN DEL ARCHIVO
- **Archivo:** `codigo/TPO-POOv2.xml`
- **Editor:** [draw.io](https://app.diagrams.net) o VS Code con extensión "Draw.io Integration"

---

## 🆕 CLASES NUEVAS A AGREGAR (15 clases)

### 1️⃣ **Package: `models`**

#### ✅ ReporteConducta
```
┌─────────────────────────────────┐
│   <<model>>                      │
│   ReporteConducta                │
├─────────────────────────────────┤
│ - id: String                     │
│ - reportanteId: String           │
│ - reportadoId: String            │
│ - scrimId: String                │
│ - tipo: TipoReporte              │
│ - severidad: SeveridadReporte    │
│ - estado: EstadoReporte          │
│ - descripcion: String            │
│ - evidencia: String              │
│ - fechaCreacion: LocalDateTime   │
│ - fechaResolucion: LocalDateTime │
│ - moderadorId: String            │
│ - resolucion: String             │
│ - autoResuelto: boolean          │
├─────────────────────────────────┤
│ + resolver()                     │
│ + marcarEnRevision()             │
│ + marcarAutoResuelto()           │
└─────────────────────────────────┘
```

**Enums internos:**
- `TipoReporte`: LENGUAJE_OFENSIVO, ABANDONO_INJUSTIFICADO, TRAMPA, COMPORTAMIENTO_ANTISPORTIVO, SPAM, OTRO
- `SeveridadReporte`: BAJA, MEDIA, ALTA, CRITICA
- `EstadoReporte`: PENDIENTE, EN_REVISION, RESUELTO, RECHAZADO

---

### 2️⃣ **Package: `strategies`**

#### ✅ ByHistoryStrategy
```
┌───────────────────────────────────┐
│   <<strategy>>                     │
│   ByHistoryStrategy                │
├───────────────────────────────────┤
│                                    │
├───────────────────────────────────┤
│ + ejecutarEmparejamiento(scrim)   │
│ - calcularScoreCompatibilidad()   │
│ - cumpleRequisitosMinimosFairPlay()│
│ - calcularSinergia()              │
└───────────────────────────────────┘
```

**Relaciones:**
- Implementa `IMatchMakingStrategy`

---

### 3️⃣ **Package: `moderators`** (Chain of Responsibility)

#### ✅ ModerationHandler (Abstract)
```
┌─────────────────────────────────┐
│   <<pattern: Chain of           │
│    Responsibility>>              │
│   ModerationHandler              │
├─────────────────────────────────┤
│ # siguiente: ModerationHandler  │
├─────────────────────────────────┤
│ + setSiguiente(handler)          │
│ + procesar(reporte) {abstract}  │
│ # pasarAlSiguiente(reporte)      │
└─────────────────────────────────┘
```

#### ✅ AutoResolverHandler
```
┌─────────────────────────────────┐
│   AutoResolverHandler            │
├─────────────────────────────────┤
│                                  │
├─────────────────────────────────┤
│ + procesar(reporte)              │
│ - esAutoResolvible()             │
│ - generarResolucionAutomatica()  │
└─────────────────────────────────┘
```

#### ✅ BotModeradorHandler
```
┌─────────────────────────────────┐
│   BotModeradorHandler            │
├─────────────────────────────────┤
│                                  │
├─────────────────────────────────┤
│ + procesar(reporte)              │
│ - analizarEvidencia()            │
│ - aplicarSancionMedia()          │
└─────────────────────────────────┘
```

#### ✅ ModeradorHumanoHandler
```
┌─────────────────────────────────┐
│   ModeradorHumanoHandler         │
├─────────────────────────────────┤
│ - moderadorId: String            │
├─────────────────────────────────┤
│ + procesar(reporte)              │
│ - tomarDecision()                │
└─────────────────────────────────┘
```

**Relaciones:**
- `AutoResolverHandler` extends `ModerationHandler`
- `BotModeradorHandler` extends `ModerationHandler`
- `ModeradorHumanoHandler` extends `ModerationHandler`

---

### 4️⃣ **Package: `commands`** (Command Pattern)

#### ✅ ScrimCommand (Interface)
```
┌─────────────────────────────────┐
│   <<interface>>                  │
│   <<pattern: Command>>           │
│   ScrimCommand                   │
├─────────────────────────────────┤
│ + ejecutar()                     │
│ + deshacer()                     │
│ + getDescripcion(): String       │
└─────────────────────────────────┘
```

#### ✅ AsignarRolCommand
```
┌─────────────────────────────────┐
│   AsignarRolCommand              │
├─────────────────────────────────┤
│ - scrim: Scrim                   │
│ - jugador: Usuario               │
│ - nuevoRol: String               │
│ - rolAnterior: String            │
│ - timestamp: LocalDateTime       │
├─────────────────────────────────┤
│ + ejecutar()                     │
│ + deshacer()                     │
│ + getDescripcion(): String       │
│ - obtenerRolActual(): String     │
│ - asignarRol(rol)                │
└─────────────────────────────────┘
```

#### ✅ SwapJugadoresCommand
```
┌─────────────────────────────────┐
│   SwapJugadoresCommand           │
├─────────────────────────────────┤
│ - scrim: Scrim                   │
│ - jugador1: Usuario              │
│ - jugador2: Usuario              │
│ - timestamp: LocalDateTime       │
│ - ejecutado: boolean             │
├─────────────────────────────────┤
│ + ejecutar()                     │
│ + deshacer()                     │
│ + getDescripcion(): String       │
│ - validarJugadoresEnScrim(): boolean│
│ - intercambiarJugadores()        │
│ - calcularBalance(): int         │
└─────────────────────────────────┘
```

**Relaciones:**
- `AsignarRolCommand` implements `ScrimCommand`
- `SwapJugadoresCommand` implements `ScrimCommand`

---

### 5️⃣ **Package: `adapters`** (Adapter Pattern)

#### ✅ ICalendarAdapter
```
┌─────────────────────────────────┐
│   <<pattern: Adapter>>           │
│   ICalendarAdapter               │
├─────────────────────────────────┤
│ - ICAL_VERSION: String = "2.0"  │
│ - PRODID: String                 │
├─────────────────────────────────┤
│ + toICalendar(scrim): String     │
│ + guardarArchivo(scrim, ruta)    │
│ - formatoICalendar(date): String │
│ - generarResumen(scrim): String  │
│ - generarDescripcion(scrim): String│
│ - convertirEstado(estado): String│
│ - calcularPrioridad(scrim): int  │
│ - escaparTexto(texto): String    │
└─────────────────────────────────┘
```

---

### 6️⃣ **Package: `validators`** (Template Method)

#### ✅ GameValidator (Abstract)
```
┌─────────────────────────────────┐
│   <<pattern: Template Method>>  │
│   GameValidator                  │
├─────────────────────────────────┤
│                                  │
├─────────────────────────────────┤
│ + validarScrim(): boolean {final}│
│ # getNombreJuego(): String {abstract}│
│ # validarNumeroJugadores(): boolean {abstract}│
│ # validarRoles(): boolean {abstract}│
│ # validarModalidad(): boolean {abstract}│
│ # validarMapa(): boolean {abstract}│
│ # validacionesAdicionales(): boolean {hook}│
└─────────────────────────────────┘
```

#### ✅ LoLValidator
```
┌─────────────────────────────────┐
│   LoLValidator                   │
├─────────────────────────────────┤
│ - JUGADORES_POR_EQUIPO: int = 5 │
│ - ROLES_VALIDOS: String[]        │
│ - MODALIDADES_VALIDAS: String[]  │
│ - MAPAS_VALIDOS: String[]        │
├─────────────────────────────────┤
│ # getNombreJuego(): String       │
│ # validarNumeroJugadores(): boolean│
│ # validarRoles(): boolean        │
│ # validarModalidad(): boolean    │
│ # validarMapa(): boolean         │
│ # validacionesAdicionales(): boolean│
└─────────────────────────────────┘
```

#### ✅ ValorantValidator
```
┌─────────────────────────────────┐
│   ValorantValidator              │
├─────────────────────────────────┤
│ - JUGADORES_POR_EQUIPO: int = 5 │
│ - ROLES_VALIDOS: String[]        │
│ - MODALIDADES_VALIDAS: String[]  │
│ - MAPAS_VALIDOS: String[]        │
├─────────────────────────────────┤
│ # getNombreJuego(): String       │
│ # validarNumeroJugadores(): boolean│
│ # validarRoles(): boolean        │
│ # validarModalidad(): boolean    │
│ # validarMapa(): boolean         │
│ # validacionesAdicionales(): boolean│
└─────────────────────────────────┘
```

**Relaciones:**
- `LoLValidator` extends `GameValidator`
- `ValorantValidator` extends `GameValidator`

---

## 🔗 RELACIONES A AGREGAR

### Asociaciones entre clases

1. **ReporteConducta → Usuario**
   ```
   ReporteConducta "1" ────── "1" Usuario : reportante
   ReporteConducta "1" ────── "1" Usuario : reportado
   ```

2. **ReporteConducta → Scrim**
   ```
   ReporteConducta "*" ────── "1" Scrim
   ```

3. **ModerationHandler → ReporteConducta**
   ```
   ModerationHandler -----> ReporteConducta : <<procesa>>
   ```

4. **ScrimCommand → Scrim**
   ```
   AsignarRolCommand -----> Scrim : <<opera sobre>>
   SwapJugadoresCommand -----> Scrim : <<opera sobre>>
   ```

5. **ScrimCommand → Usuario**
   ```
   AsignarRolCommand -----> Usuario
   SwapJugadoresCommand -----> Usuario
   ```

6. **ICalendarAdapter → Scrim**
   ```
   ICalendarAdapter -----> Scrim : <<exporta>>
   ```

7. **GameValidator → Scrim**
   ```
   GameValidator -----> Scrim : <<valida>>
   ```

8. **ByHistoryStrategy → IMatchMakingStrategy**
   ```
   ByHistoryStrategy ..|> IMatchMakingStrategy : <<implements>>
   ```

9. **Chain of Responsibility (siguiente)**
   ```
   ModerationHandler "siguiente" ──┐
                                   ↓
   AutoResolverHandler ──> BotModeradorHandler ──> ModeradorHumanoHandler
   ```

---

## 📐 LAYOUT SUGERIDO

### Organización por patrones:

```
┌────────────────────────────────────────────────────────────┐
│                    EXISTING DIAGRAM                         │
│  (State, Strategy, Observer, Factory, Composite patterns)   │
└────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────┐
│              CHAIN OF RESPONSIBILITY PATTERN                │
│                                                              │
│  ModerationHandler                                          │
│       ↓                                                      │
│  AutoResolverHandler → BotModeradorHandler → ModeradorHumanoHandler
│       ↓                                                      │
│  ReporteConducta                                            │
└────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────┐
│                    COMMAND PATTERN                          │
│                                                              │
│          ScrimCommand (interface)                           │
│                ↓                ↓                            │
│    AsignarRolCommand    SwapJugadoresCommand                │
└────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────┐
│                  TEMPLATE METHOD PATTERN                    │
│                                                              │
│              GameValidator (abstract)                       │
│                  ↓              ↓                            │
│           LoLValidator    ValorantValidator                 │
└────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────┐
│                    ADAPTER PATTERN                          │
│                                                              │
│              ICalendarAdapter                               │
│                    ↓                                         │
│                  Scrim                                      │
└────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────┐
│              STRATEGY PATTERN (EXTENDED)                    │
│                                                              │
│         IMatchMakingStrategy (interface)                    │
│           ↓          ↓            ↓                          │
│    ByRanking   ByLatency   ByHistoryStrategy (NEW)         │
└────────────────────────────────────────────────────────────┘
```

---

## 🎨 COLORES SUGERIDOS

- **Chain of Responsibility:** 🟣 Violeta (#9B59B6)
- **Command:** 🟠 Naranja (#E67E22)
- **Template Method:** 🔵 Azul (#3498DB)
- **Adapter:** 🟢 Verde (#2ECC71)
- **Strategy (nuevo):** 🔴 Rojo (#E74C3C)

---

## ✅ CHECKLIST DE ACTUALIZACIÓN

- [ ] Agregar `ReporteConducta` en package `models`
- [ ] Agregar 3 enums dentro de `ReporteConducta`
- [ ] Agregar `ByHistoryStrategy` en package `strategies`
- [ ] Relacionar `ByHistoryStrategy` con `IMatchMakingStrategy`
- [ ] Crear package `moderators`
- [ ] Agregar `ModerationHandler` (abstract)
- [ ] Agregar `AutoResolverHandler`, `BotModeradorHandler`, `ModeradorHumanoHandler`
- [ ] Relacionar handlers (herencia + chain)
- [ ] Crear package `commands`
- [ ] Agregar `ScrimCommand` (interface)
- [ ] Agregar `AsignarRolCommand`, `SwapJugadoresCommand`
- [ ] Relacionar commands (implements)
- [ ] Crear package `adapters`
- [ ] Agregar `ICalendarAdapter`
- [ ] Crear package `validators`
- [ ] Agregar `GameValidator` (abstract)
- [ ] Agregar `LoLValidator`, `ValorantValidator`
- [ ] Relacionar validators (herencia)
- [ ] Agregar todas las asociaciones con otras clases
- [ ] Agregar estereotipos de patrones
- [ ] Organizar layout por patrones
- [ ] Colorear por patrón
- [ ] Exportar como imagen PNG/SVG
- [ ] Guardar XML actualizado

---

## 📤 EXPORTACIÓN FINAL

1. **Exportar como PNG:**
   - File → Export as → PNG
   - Resolución: 300 DPI
   - Nombre: `TPO-POOv2-Diagrama-Final.png`

2. **Exportar como SVG:**
   - File → Export as → SVG
   - Nombre: `TPO-POOv2-Diagrama-Final.svg`

3. **Guardar XML:**
   - File → Save
   - Nombre: `codigo/TPO-POOv2.xml`

---

## 🚀 PRÓXIMO PASO

Después de actualizar el diagrama, ejecutar:
```bash
# Generar JavaDoc
javadoc -d docs -sourcepath src -subpackages . -encoding UTF-8

# Crear README.md
# Comprimir proyecto
# Subir a plataforma
```

**¡El TP estará 100% completo!** ✅
