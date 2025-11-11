# 🔧 FIX: Asignación de Roles Únicos por Equipo en LoL/Valorant

## 🐛 Problema Identificado

**Síntomas:**
- En partidas de LoL 5v5, se asignaban 2 supports en un equipo
- El usuario aparecía "sin rol"
- Roles duplicados en el mismo equipo
- Roles faltantes en equipos

**Causa Raíz:**
El método `buscarJugadoresConMMR()` asignaba roles **aleatoriamente** sin verificar:
1. Que no se repitieran roles en el mismo equipo
2. Que cada equipo tuviera exactamente 1 de cada rol

**Código problemático:**
```java
// ANTES - Rol aleatorio sin validación
String rolBot = rolesDisponibles[random.nextInt(rolesDisponibles.length)];
bot.setRol(rolBot);
```

---

## ✅ Solución Implementada

### **Nuevo Sistema de Asignación de Roles**

Se implementó un sistema que garantiza **1 de cada rol por equipo** en juegos como LoL y Valorant.

### **Cambios en `MatchmakingController.java`**

#### **1. Tracking de roles por equipo**

```java
// Listas para rastrear roles asignados a cada equipo
List<String> rolesEquipo1 = new ArrayList<>();
List<String> rolesEquipo2 = new ArrayList<>();

// El usuario actual va al equipo 1
rolesEquipo1.add(rolUsuario);
```

#### **2. Lógica de asignación inteligente**

```java
// Para LoL/Valorant: asignar roles únicos por equipo
int jugadoresEnEquipo1 = rolesEquipo1.size();
int jugadoresPorEquipo = jugadoresTotales / 2;

if (jugadoresEnEquipo1 < jugadoresPorEquipo) {
    // Asignar al equipo 1 - rol disponible que NO esté ya en el equipo
    rolBot = obtenerRolDisponible(rolesDisponibles, rolesEquipo1);
    rolesEquipo1.add(rolBot);
} else {
    // Asignar al equipo 2 - rol disponible que NO esté ya en el equipo
    rolBot = obtenerRolDisponible(rolesDisponibles, rolesEquipo2);
    rolesEquipo2.add(rolBot);
}
```

#### **3. Método helper: `obtenerRolDisponible()`**

```java
/**
 * Obtiene un rol disponible que no esté ya asignado en el equipo
 */
private String obtenerRolDisponible(String[] rolesDisponibles, List<String> rolesYaAsignados) {
    for (String rol : rolesDisponibles) {
        if (!rolesYaAsignados.contains(rol)) {
            return rol;  // Retorna el primer rol que no esté asignado
        }
    }
    // Si todos están asignados, devolver el primero (fallback - no debería pasar)
    return rolesDisponibles[0];
}
```

#### **4. Método helper: `esJuegoConRolesUnicos()`**

```java
/**
 * Verifica si el juego requiere roles únicos por equipo
 */
private boolean esJuegoConRolesUnicos(String juego) {
    String juegoLower = juego.toLowerCase();
    return juegoLower.contains("league") || juegoLower.contains("lol") || 
           juegoLower.contains("valorant");
}
```

---

## 📊 Ejemplo de Funcionamiento

### **Antes (Aleatorio) - INCORRECTO ❌**

```
Equipo Azul (Team Azure):
├─ Galli (Top)
├─ Shadow42 (Support)      ← Duplicado
├─ Phoenix11 (Support)     ← Duplicado
├─ Ghost88 (Mid)
└─ Ninja23 (ADC)

Equipo Rojo (Team Crimson):
├─ Hunter99 (Jungle)
├─ Viper44 (Mid)           ← Duplicado
├─ Storm77 (Mid)           ← Duplicado
├─ Blaze55 (Top)           ← Duplicado
└─ Frost66 (ADC)
```

**Problemas:**
- ❌ 2 Supports en Equipo Azul
- ❌ 3 Mids en total (1 en Azul, 2 en Rojo)
- ❌ 2 Tops en total
- ❌ Falta Jungle en Equipo Azul

---

### **Ahora (Único por equipo) - CORRECTO ✅**

```
Equipo Azul (Team Azure):
├─ Galli (Top)          ✅ Único
├─ Shadow42 (Jungle)    ✅ Único
├─ Phoenix11 (Mid)      ✅ Único
├─ Ghost88 (ADC)        ✅ Único
└─ Ninja23 (Support)    ✅ Único

Equipo Rojo (Team Crimson):
├─ Hunter99 (Top)       ✅ Único
├─ Viper44 (Jungle)     ✅ Único
├─ Storm77 (Mid)        ✅ Único
├─ Blaze55 (ADC)        ✅ Único
└─ Frost66 (Support)    ✅ Único
```

**Resultado:**
- ✅ Cada equipo tiene exactamente 1 Top
- ✅ Cada equipo tiene exactamente 1 Jungle
- ✅ Cada equipo tiene exactamente 1 Mid
- ✅ Cada equipo tiene exactamente 1 ADC
- ✅ Cada equipo tiene exactamente 1 Support

---

## 🎮 Juegos Afectados

### **Con roles únicos por equipo:**
- ✅ League of Legends (Top, Jungle, Mid, ADC, Support)
- ✅ Valorant (Duelist, Controller, Initiator, Sentinel, Flex)

### **Sin restricción (roles aleatorios):**
- ℹ️ CS:GO (Entry, AWPer, Rifler, etc. - pueden repetirse)
- ℹ️ Rocket League (roles flexibles)
- ℹ️ Otros juegos

---

## 🔍 Flujo de Asignación (LoL 5v5)

### **Paso a paso:**

1. **Usuario se une:**
   - Usuario: Galli, Rol: Top
   - `rolesEquipo1 = [Top]`

2. **Bot 1 (para Equipo 1):**
   - Roles disponibles: [Top, Jungle, Mid, ADC, Support]
   - Top ya está en equipo 1
   - Asigna: **Jungle**
   - `rolesEquipo1 = [Top, Jungle]`

3. **Bot 2 (para Equipo 1):**
   - Roles disponibles: [Top, Jungle, Mid, ADC, Support]
   - Top y Jungle ya están
   - Asigna: **Mid**
   - `rolesEquipo1 = [Top, Jungle, Mid]`

4. **Bot 3 (para Equipo 1):**
   - Asigna: **ADC**
   - `rolesEquipo1 = [Top, Jungle, Mid, ADC]`

5. **Bot 4 (para Equipo 1):**
   - Asigna: **Support**
   - `rolesEquipo1 = [Top, Jungle, Mid, ADC, Support]` ✅ Completo

6. **Bot 5 (para Equipo 2):**
   - Ahora empieza Equipo 2
   - Roles disponibles: [Top, Jungle, Mid, ADC, Support]
   - Ninguno asignado aún
   - Asigna: **Top**
   - `rolesEquipo2 = [Top]`

7. **Bots 6-9 (para Equipo 2):**
   - Asigna: Jungle, Mid, ADC, Support
   - `rolesEquipo2 = [Top, Jungle, Mid, ADC, Support]` ✅ Completo

**Resultado Final:**
- ✅ Equipo 1: 5 roles únicos
- ✅ Equipo 2: 5 roles únicos
- ✅ Sin duplicados en ningún equipo

---

## 🧪 Testing

### **Caso de prueba 1: LoL 5v5**

**Entrada:**
- Juego: League of Legends
- Formato: 5v5
- Usuario: Galli, Rol: Top

**Salida esperada:**
```
Equipo Azul: Top, Jungle, Mid, ADC, Support (sin duplicados)
Equipo Rojo: Top, Jungle, Mid, ADC, Support (sin duplicados)
```

### **Caso de prueba 2: Valorant 5v5**

**Entrada:**
- Juego: Valorant
- Formato: 5v5
- Usuario: Galli, Rol: Duelist

**Salida esperada:**
```
Equipo Azul: Duelist, Controller, Initiator, Sentinel, Flex (sin duplicados)
Equipo Rojo: Duelist, Controller, Initiator, Sentinel, Flex (sin duplicados)
```

### **Caso de prueba 3: CS:GO (sin restricción)**

**Entrada:**
- Juego: CS:GO
- Formato: 5v5
- Usuario: Galli, Rol: AWPer

**Salida esperada:**
```
Roles pueden repetirse (es válido tener 2 AWPers en un equipo)
```

---

## 📝 Archivos Modificados

```
✅ controllers/MatchmakingController.java
   - Método buscarJugadoresConMMR() (+40 líneas)
   - Método esJuegoConRolesUnicos() (nuevo)
   - Método obtenerRolDisponible() (nuevo)
```

---

## ✅ Checklist

- [x] Problema identificado (roles duplicados)
- [x] Solución implementada (tracking por equipo)
- [x] Método `obtenerRolDisponible()` creado
- [x] Método `esJuegoConRolesUnicos()` creado
- [x] Lógica de asignación actualizada
- [x] Compilación exitosa
- [x] Documentación creada
- [ ] Testing manual (PENDIENTE)

---

## 🚀 Próximos Pasos

### **Testing recomendado:**

1. **Ejecutar el proyecto:**
   ```bash
   cd codigo
   java -cp bin main.Main
   ```

2. **Crear usuario y configurar perfil:**
   - Registrarse
   - Configurar LoL como juego principal
   - Configurar rango (ej: 1500)

3. **Iniciar Quick Match:**
   - Seleccionar rol (ej: Top)
   - Esperar matchmaking
   - Verificar equipos formados

4. **Verificar resultado:**
   - ✅ Tu equipo debe tener: Top, Jungle, Mid, ADC, Support
   - ✅ Equipo contrario debe tener: Top, Jungle, Mid, ADC, Support
   - ✅ Sin roles duplicados en ningún equipo
   - ✅ Tú debes aparecer con tu rol (Top)

---

## 🎯 Resumen

### **Antes:**
- ❌ Roles aleatorios
- ❌ Duplicados en equipos
- ❌ Composiciones inválidas

### **Ahora:**
- ✅ Roles únicos por equipo
- ✅ Composiciones válidas (1 de cada rol)
- ✅ Compatible con LoL y Valorant
- ✅ Otros juegos mantienen flexibilidad

---

**Última actualización:** 10/11/2025  
**Estado:** ✅ IMPLEMENTADO Y COMPILADO  
**Testing:** Pendiente de prueba manual
