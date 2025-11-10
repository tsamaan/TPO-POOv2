# 🎨 INSTRUCCIONES: Cómo Integrar las Nuevas Clases al Diagrama Principal

## 📁 ARCHIVOS CREADOS

1. **`NUEVAS-CLASES-DIAGRAMA.xml`** - Diagrama separado con las 8 clases nuevas (este archivo)
2. **`TPO-POOv2.xml`** - Tu diagrama principal existente

---

## 🚀 OPCIÓN 1: Copiar/Pegar en draw.io (RECOMENDADO - 10 minutos)

### Paso 1: Abrir ambos diagramas
```
1. Ve a https://app.diagrams.net
2. File → Open from → Device
3. Selecciona: NUEVAS-CLASES-DIAGRAMA.xml
4. Se abre en una pestaña nueva
```

### Paso 2: Abrir tu diagrama principal en otra pestaña
```
1. File → Open from → Device (en nueva pestaña)
2. Selecciona: TPO-POOv2.xml
```

### Paso 3: Copiar clases nuevas
```
En NUEVAS-CLASES-DIAGRAMA.xml:
1. Selecciona TODO (Ctrl+A)
2. Copia (Ctrl+C)

En TPO-POOv2.xml:
3. Pega (Ctrl+V)
4. Mueve las clases a su ubicación final
```

### Paso 4: Eliminar cajas de referencia punteadas
```
Elimina estas 3 cajas (solo son referencias):
- "ReporteConducta (ya existe en diagrama)"
- "Scrim (ya existe en diagrama)" (hay 2)
```

### Paso 5: Conectar a clases existentes
```
Ahora conecta las nuevas clases con las que YA EXISTEN:

1. ModerationHandler → ReporteConducta
   - Busca la clase ReporteConducta real en tu diagrama
   - Arrastra una flecha punteada desde ModerationHandler
   - Etiqueta: <<procesa>>

2. ICalendarAdapter → Scrim
   - Busca la clase Scrim real en tu diagrama
   - Arrastra una flecha punteada desde ICalendarAdapter
   - Etiqueta: <<exporta>>

3. GameValidator → Scrim
   - Arrastra una flecha punteada desde GameValidator a Scrim
   - Etiqueta: <<valida>>
```

### Paso 6: Organizar layout
```
1. Agrupa las clases por patrón
2. Organiza para que no se superpongan
3. Ajusta las flechas si es necesario
```

### Paso 7: Guardar
```
1. File → Save
2. Exporta como PNG: File → Export as → PNG
3. Nombre: TPO-POOv2-Completo.png
```

---

## 🔧 OPCIÓN 2: Importar como página adicional (5 minutos)

### Más simple pero menos integrado:

```
1. Abre TPO-POOv2.xml en draw.io
2. Click en "+" al final de las pestañas
3. File → Import → From Device
4. Selecciona NUEVAS-CLASES-DIAGRAMA.xml
5. Se agrega como página 2 del diagrama
6. Puedes tener ambos en el mismo archivo
```

---

## 📊 CONTENIDO DEL DIAGRAMA NUEVO

### **Chain of Responsibility Pattern (Violeta #E1D5E7):**
✅ `ModerationHandler` (abstract)
  - Atributos: `siguiente: ModerationHandler`
  - Métodos: `setSiguiente()`, `procesar()` (abstract), `pasarAlSiguiente()`

✅ `AutoResolverHandler` extends ModerationHandler
  - Métodos: `procesar()`, `esAutoResolvible()`, `generarResolucionAutomatica()`

✅ `BotModeradorHandler` extends ModerationHandler
  - Métodos: `procesar()`, `analizarEvidencia()`, `aplicarSancionMedia()`

✅ `ModeradorHumanoHandler` extends ModerationHandler
  - Atributos: `moderadorId: String`
  - Métodos: `procesar()`, `tomarDecision()`

**Conexiones:**
- ▷ Herencia: AutoResolver → ModerationHandler
- ▷ Herencia: BotModerador → ModerationHandler
- ▷ Herencia: ModeradorHumano → ModerationHandler
- → Asociación: ModerationHandler tiene `siguiente` (self-reference)
- - → Dependencia: ModerationHandler usa ReporteConducta

---

### **Adapter Pattern (Verde #D5E8D4):**
✅ `ICalendarAdapter`
  - Atributos: `ICAL_VERSION`, `PRODID`
  - Métodos: `toICalendar()`, `guardarArchivo()`, `formatoICalendar()`, etc.

**Conexiones:**
- - → Dependencia: ICalendarAdapter usa Scrim

---

### **Template Method Pattern (Azul #DAE8FC):**
✅ `GameValidator` (abstract)
  - Métodos: 
    * `validarScrim()` {final} - Template method
    * `getNombreJuego()` {abstract}
    * `validarNumeroJugadores()` {abstract}
    * `validarRoles()` {abstract}
    * `validarModalidad()` {abstract}
    * `validarMapa()` {abstract}
    * `validacionesAdicionales()` {hook}

✅ `LoLValidator` extends GameValidator
  - Atributos: constantes (JUGADORES, ROLES, MODALIDADES, MAPAS)
  - Implementa todos los métodos abstractos

✅ `ValorantValidator` extends GameValidator
  - Atributos: constantes (JUGADORES, ROLES, MODALIDADES, MAPAS)
  - Implementa todos los métodos abstractos

**Conexiones:**
- ▷ Herencia: LoLValidator → GameValidator
- ▷ Herencia: ValorantValidator → GameValidator
- - → Dependencia: GameValidator usa Scrim

---

## 🎨 COLORES Y CONVENCIONES

```
┌────────────────────────────────────────────┐
│ LEYENDA DE COLORES                         │
├────────────────────────────────────────────┤
│ #E1D5E7 (Violeta) - Chain of Responsibility│
│ #D5E8D4 (Verde)   - Adapter                │
│ #DAE8FC (Azul)    - Template Method        │
│ #FF9933 (Naranja) - Modelos de dominio     │
│ #D5E8D4 (Verde)   - Strategy Pattern       │
│ #FF9999 (Rosa)    - Factory Pattern        │
└────────────────────────────────────────────┘

┌────────────────────────────────────────────┐
│ LEYENDA DE FLECHAS                         │
├────────────────────────────────────────────┤
│ ─────▷ Herencia (extends)                  │
│ - - -→ Dependencia (usa)                   │
│ ────→ Asociación                           │
│ ····⊳ Implementa (interface)               │
└────────────────────────────────────────────┘
```

---

## ✅ CHECKLIST DE INTEGRACIÓN

Después de integrar, verifica que tengas:

**Chain of Responsibility:**
- [ ] ModerationHandler (abstract) agregado
- [ ] AutoResolverHandler agregado
- [ ] BotModeradorHandler agregado
- [ ] ModeradorHumanoHandler agregado
- [ ] Flechas de herencia (3) conectadas
- [ ] Asociación "siguiente" conectada
- [ ] Dependencia a ReporteConducta conectada

**Adapter:**
- [ ] ICalendarAdapter agregado
- [ ] Dependencia a Scrim conectada

**Template Method:**
- [ ] GameValidator (abstract) agregado
- [ ] LoLValidator agregado
- [ ] ValorantValidator agregado
- [ ] Flechas de herencia (2) conectadas
- [ ] Dependencia a Scrim conectada

**General:**
- [ ] Todas las clases tienen color correcto
- [ ] No hay clases aisladas (todas conectadas)
- [ ] Layout organizado por patrones
- [ ] Estereotipos de patrones visibles (<<pattern>>)
- [ ] Referencias punteadas eliminadas

---

## 📸 EXPORTACIÓN FINAL

Una vez integrado todo:

```bash
# En draw.io:
File → Export as → PNG
  - Nombre: TPO-POOv2-Diagrama-Completo.png
  - Resolución: 300 DPI
  - Tamaño: Border width 10

File → Export as → SVG
  - Nombre: TPO-POOv2-Diagrama-Completo.svg

File → Save
  - Nombre: TPO-POOv2.xml (sobrescribir)
```

---

## 🎯 RESULTADO ESPERADO

Tu diagrama final tendrá:

```
TOTAL DE PATRONES EN EL DIAGRAMA:
1. State Pattern ✅
2. Strategy Pattern ✅ (con ByHistoryStrategy NUEVO)
3. Observer Pattern ✅
4. Abstract Factory ✅
5. Composite ✅
6. ⭐ Chain of Responsibility ✅ NUEVO
7. ⭐ Command Pattern ✅ (ya existe)
8. ⭐ Template Method ✅ NUEVO
9. ⭐ Adapter ✅ NUEVO

TOTAL: 9 patrones (150% del requerido) 🎉
```

---

## 🆘 SOLUCIÓN DE PROBLEMAS

### "No puedo abrir el archivo XML"
```
Solución: Usa app.diagrams.net (online) o instala draw.io desktop
```

### "Las clases se superponen al pegar"
```
Solución: 
1. Después de pegar, presiona Esc
2. Arrastra las clases a un área vacía
3. Luego organiza
```

### "No encuentro ReporteConducta/Scrim en mi diagrama"
```
Solución:
1. Usa Ctrl+F para buscar en draw.io
2. O revisa la lista de clases en el panel izquierdo
```

### "Las flechas no se conectan bien"
```
Solución:
1. Elimina la flecha
2. Desde el menú lateral, selecciona el tipo correcto
3. Arrastra desde el círculo azul de una clase al círculo azul de otra
```

---

## 💡 TIPS PROFESIONALES

1. **Organiza por patrones:** Agrupa visualmente las clases del mismo patrón
2. **Usa colores consistentes:** Mantén el código de colores por patrón
3. **Etiqueta las relaciones:** Siempre pon estereotipos (<<procesa>>, <<exporta>>)
4. **Alinea las clases:** Usa las guías de alineación de draw.io (View → Guides)
5. **Exporta en alta calidad:** PNG 300 DPI para impresión

---

## 📞 PRÓXIMOS PASOS

1. ✅ Abrir NUEVAS-CLASES-DIAGRAMA.xml en draw.io
2. ✅ Copiar todo al diagrama principal
3. ✅ Eliminar referencias punteadas
4. ✅ Conectar a clases existentes
5. ✅ Organizar layout
6. ✅ Exportar PNG/SVG
7. ✅ ¡Listo para entregar!

---

**Tiempo estimado total: 10-15 minutos** ⏱️

**¡El diagrama estará completo y profesional!** 🎨✨
