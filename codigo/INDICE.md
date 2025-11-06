# 🗂️ Índice Visual del Proyecto eScrims

## 📖 Comienza Aquí

Si es tu primera vez con este proyecto, sigue este orden:

```
1️⃣  RESUMEN.md              ← Empieza aquí (resumen ejecutivo)
    ↓
2️⃣  README.md               ← Overview del proyecto
    ↓
3️⃣  MAPEO_DIAGRAMA.md       ← Ver cómo el diagrama se traduce a código
    ↓
4️⃣  PATRONES_DETALLE.md     ← Entender los patrones en profundidad
    ↓
5️⃣  GUIA_USO.md             ← Aprender a usar el código
    ↓
6️⃣  run.bat                 ← Ejecutar el proyecto
```

---

## 📚 Documentación por Propósito

### 🎯 Para Revisar Rápidamente
- **`RESUMEN.md`** - 5 min de lectura, visión completa

### 🏗️ Para Entender la Arquitectura
- **`README.md`** - Estructura y patrones implementados
- **`MAPEO_DIAGRAMA.md`** - Correspondencia UML ↔ Código

### 🔬 Para Estudio Profundo
- **`PATRONES_DETALLE.md`** - Teoría y diagramas de cada patrón
- **Diagramas ASCII** incluidos para cada patrón

### 💻 Para Desarrollar/Extender
- **`GUIA_USO.md`** - Ejemplos de código y casos de uso
- **Sección de extensibilidad** en cada patrón

### 🚀 Para Ejecutar
- **`run.bat`** - Script todo-en-uno
- **`Main.java`** - Demo completa

---

## 🗺️ Navegación por Código

### Por Patrón de Diseño

#### 🔵 STATE Pattern
```
src/states/
├── ScrimState.java                    ← Interfaz
├── EstadoBuscandoJugadores.java       ← Estado inicial
├── EstadoLobbyCompleto.java           ← Lobby lleno
├── EstadoConfirmado.java              ← Confirmado
├── EstadoEnJuego.java                 ← En partida
├── EstadoFinalizado.java              ← Terminado
└── EstadoCancelado.java               ← Cancelado

src/context/
└── ScrimContext.java                  ← Context del pattern
```
**Lee**: `PATRONES_DETALLE.md` sección 1

---

#### 🟢 STRATEGY Pattern
```
src/strategies/
├── ByMMRStrategy.java                 ← Estrategia por habilidad
└── ByLatencyStrategy.java             ← Estrategia por latencia

src/interfaces/
└── IMatchMakingStrategy.java          ← Interfaz

src/service/
└── MatchmakingService.java            ← Context
```
**Lee**: `PATRONES_DETALLE.md` sección 2

---

#### 🔴 ABSTRACT FACTORY Pattern
```
src/notifiers/
├── NotifierFactory.java               ← Abstract Factory
├── SimpleNotifierFactory.java         ← Concrete Factory
├── EmailNotifier.java                 ← Product 1
├── DiscordNotifier.java               ← Product 2
└── PushNotifier.java                  ← Product 3

src/interfaces/
└── INotifier.java                     ← Product Interface
```
**Lee**: `PATRONES_DETALLE.md` sección 3

---

#### 🟣 ADAPTER Pattern
```
src/auth/
├── AuthProvider.java                  ← Target Interface
├── LocalAuthAdapter.java              ← Adapter 1
├── GoogleAuthAdapter.java             ← Adapter 2
├── AuthService.java                   ← Service
└── AuthController.java                ← Controller
```
**Lee**: `PATRONES_DETALLE.md` sección 4

---

#### 👁️ OBSERVER Pattern
```
src/models/
└── Scrim.java                         ← Subject (notifica a INotifiers)

src/interfaces/
└── INotifier.java                     ← Observer Interface

src/notifiers/
├── EmailNotifier.java                 ← Observer 1
├── DiscordNotifier.java               ← Observer 2
└── PushNotifier.java                  ← Observer 3
```
**Lee**: `PATRONES_DETALLE.md` sección 5

---

### Por Capa de Arquitectura

#### 📱 Presentación
```
src/auth/
└── AuthController.java

src/main/
└── Main.java
```

#### ⚙️ Servicios
```
src/auth/
└── AuthService.java

src/service/
└── MatchmakingService.java
```

#### 🎯 Lógica de Negocio
```
src/context/
└── ScrimContext.java

src/states/
└── (todos los estados)

src/strategies/
└── (todas las estrategias)
```

#### 📦 Modelos
```
src/models/
├── Usuario.java
├── Scrim.java
├── Postulacion.java
└── Notificacion.java
```

#### 🔌 Infraestructura
```
src/auth/
├── LocalAuthAdapter.java
└── GoogleAuthAdapter.java

src/notifiers/
└── (todos los notifiers)
```

---

## 🎓 Rutas de Aprendizaje

### 🌟 Principiante
1. Leer `RESUMEN.md`
2. Ejecutar `run.bat`
3. Ver output y comparar con `Main.java`
4. Leer comentarios en `Main.java`

### 🔥 Intermedio
1. Leer `README.md` completo
2. Revisar `MAPEO_DIAGRAMA.md`
3. Explorar código siguiendo el mapeo
4. Leer `GUIA_USO.md` secciones de código

### 🚀 Avanzado
1. Estudiar `PATRONES_DETALLE.md`
2. Analizar implementaciones específicas
3. Revisar interacciones entre patrones
4. Experimentar con extensiones

---

## 🔍 Búsqueda Rápida

### ¿Cómo se implementa...?
- **Cambios de estado**: Ver `states/EstadoBuscandoJugadores.java`
- **Matchmaking**: Ver `strategies/ByMMRStrategy.java`
- **Notificaciones**: Ver `notifiers/EmailNotifier.java`
- **Autenticación**: Ver `auth/LocalAuthAdapter.java`
- **Integración**: Ver `main/Main.java`

### ¿Dónde está la interfaz de...?
- **State**: `states/ScrimState.java`
- **Strategy**: `interfaces/IMatchMakingStrategy.java`
- **Observer**: `interfaces/INotifier.java`
- **Adapter**: `auth/AuthProvider.java`

### ¿Cómo extender...?
- **Nuevo estado**: `GUIA_USO.md` → Caso de Uso: Estados
- **Nueva estrategia**: `GUIA_USO.md` → Caso de Uso: Matchmaking
- **Nuevo notifier**: `GUIA_USO.md` → Extensiones: Factory
- **Nuevo auth provider**: `GUIA_USO.md` → Extensiones: Adapter

---

## 📊 Diagramas

### Diagrama UML Original
- **Archivo**: `../TPO-POOv2.xml`
- **Formato**: draw.io (editable)
- **Contenido**: Diagrama de clases completo

### Diagramas ASCII en Documentación
- **State Pattern**: `PATRONES_DETALLE.md` sección 1
- **Strategy Pattern**: `PATRONES_DETALLE.md` sección 2
- **Abstract Factory**: `PATRONES_DETALLE.md` sección 3
- **Adapter Pattern**: `PATRONES_DETALLE.md` sección 4
- **Observer Pattern**: `PATRONES_DETALLE.md` sección 5

---

## 🎯 Objetivos de Aprendizaje

Después de estudiar este proyecto, deberías poder:

✅ Explicar cuándo y por qué usar cada patrón  
✅ Identificar patrones en código existente  
✅ Implementar estos patrones desde cero  
✅ Extender el sistema con nuevas funcionalidades  
✅ Aplicar principios SOLID en tus proyectos  

---

## 🛠️ Herramientas Necesarias

### Para Ejecutar
- JDK 8 o superior
- PowerShell (Windows) o Bash (Linux/Mac)

### Para Editar
- Cualquier editor de texto
- Recomendado: VS Code, IntelliJ IDEA, Eclipse

### Para Ver Diagrama UML
- draw.io (https://app.diagrams.net)
- O importar en Visual Paradigm, StarUML, etc.

---

## 📞 Soporte

### Si tienes dudas sobre...

**Compilación/Ejecución**
→ Ver sección "Debugging" en `GUIA_USO.md`

**Patrones de Diseño**
→ Leer explicaciones en `PATRONES_DETALLE.md`

**Uso del Código**
→ Revisar ejemplos en `GUIA_USO.md`

**Mapeo UML-Código**
→ Consultar `MAPEO_DIAGRAMA.md`

---

## 🏆 Checklist de Revisión

Usa esto para verificar que entendiste todo:

### Conceptual
- [ ] Entiendo qué hace cada patrón
- [ ] Sé por qué se eligió cada patrón
- [ ] Puedo explicar las ventajas de cada patrón
- [ ] Conozco alternativas a cada patrón

### Práctico
- [ ] Compilé y ejecuté el proyecto
- [ ] Leí y entendí `Main.java`
- [ ] Exploré al menos 3 clases de cada patrón
- [ ] Identifiqué las relaciones entre clases

### Avanzado
- [ ] Puedo agregar un nuevo estado
- [ ] Puedo agregar una nueva estrategia
- [ ] Puedo agregar un nuevo notificador
- [ ] Puedo agregar un nuevo auth provider

---

## 📍 Mapa Mental del Proyecto

```
                    eScrims Platform
                          │
        ┌─────────────────┼─────────────────┐
        │                 │                 │
    Patrones          Arquitectura      Funcionalidades
        │                 │                 │
    ┌───┴───┐         ┌───┴───┐        ┌───┴───┐
    │       │         │       │        │       │
  State  Strategy   Capas  Paquetes  Auth  Matchmaking
    │       │         │       │        │       │
  6 Est  2 Alg      5 Lay   9 Pkg   2 Prov  2 Strat
```

---

## 🎨 Leyenda de Colores (en documentos)

- 🔵 **Azul** = State Pattern
- 🟢 **Verde** = Strategy Pattern
- 🔴 **Rojo** = Abstract Factory Pattern
- 🟣 **Púrpura** = Adapter Pattern
- 👁️ **Ojo** = Observer Pattern
- 🟠 **Naranja** = Domain Models

---

**Última actualización**: Noviembre 2025  
**Versión**: 1.0  
**Mantenedor**: Proyecto eScrims UADE
