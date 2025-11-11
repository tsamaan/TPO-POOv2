# 🔐 Feature: Cerrar Sesión

## 📋 Descripción

Se agregó la funcionalidad de **cerrar sesión** que permite al usuario volver al menú de login/registro sin cerrar la aplicación.

---

## ✨ Cambios Realizados

### **1. Main.java - Doble Loop**

Se implementó una **arquitectura de doble loop** para manejar sesiones:

```java
// Loop externo: Aplicación completa
boolean appRunning = true;

while (appRunning) {
    // Menú de autenticación (Login/Registro)
    Usuario usuarioActual = menuInicialAuth(...);
    
    if (usuarioActual == null) {
        // Usuario eligió "Salir" → Cerrar app
        appRunning = false;
        continue;
    }
    
    // Loop interno: Sesión de usuario autenticado
    boolean sesionActiva = true;
    
    while (sesionActiva) {
        // Dashboard con opciones
        int opcion = menuView.mostrarMenuPrincipal(usuarioActual);
        
        switch (opcion) {
            case 1: // Juego Rápido
            case 2: // Buscar Salas
            case 3: // Ver Perfil
            case 4: // Editar Perfil
            
            case 5: // NUEVO: Cerrar Sesión
                consoleView.mostrarInfo("Cerrando sesión de " + usuarioActual.getUsername());
                sesionActiva = false;  // ← Sale del loop interno
                break;
            
            case 6: // NUEVO: Salir de la app
                menuView.mostrarDespedida(usuarioActual.getUsername());
                sesionActiva = false;  // ← Sale del loop interno
                appRunning = false;    // ← Y también del externo
                break;
        }
    }
}
```

### **2. MenuView.java - Opción adicional**

Se actualizó el menú principal para incluir las opciones de cerrar sesión y salir:

**ANTES:**
```java
System.out.println("[1] Juego Rápido");
System.out.println("[2] Buscar Salas");
System.out.println("[3] Ver Mi Perfil");
System.out.println("[4] Editar Perfil");
System.out.println("[5] Salir");

return consoleView.solicitarNumero("Selecciona una opción", 1, 5);
```

**DESPUÉS:**
```java
System.out.println("[1] Juego Rápido");
System.out.println("[2] Buscar Salas");
System.out.println("[3] Ver Mi Perfil");
System.out.println("[4] Editar Perfil");
System.out.println("[5] Cerrar Sesión");  // ← NUEVO
System.out.println("[6] Salir");           // ← MOVIDO

return consoleView.solicitarNumero("Selecciona una opción", 1, 6);
```

---

## 🎮 Flujo de Usuario

### **Escenario 1: Usuario cierra sesión**

```
┌─────────────────────────────────────────┐
│ MENU INICIAL                           │
├─────────────────────────────────────────┤
│ [1] Iniciar Sesión                     │
│ [2] Registrarse                        │
│ [3] Salir                              │
│                                         │
│ [>] Opción: 1                          │
└─────────────────────────────────────────┘
          ↓
┌─────────────────────────────────────────┐
│ LOGIN                                  │
├─────────────────────────────────────────┤
│ Email: shadow@escrims.com              │
│ Password: ******                       │
│                                         │
│ [+] Login exitoso!                     │
└─────────────────────────────────────────┘
          ↓
┌─────────────────────────────────────────┐
│ MENU PRINCIPAL - ShadowBlade           │
├─────────────────────────────────────────┤
│ [1] Juego Rápido                       │
│ [2] Buscar Salas                       │
│ [3] Ver Mi Perfil                      │
│ [4] Editar Perfil                      │
│ [5] Cerrar Sesión          ← NUEVO     │
│ [6] Salir                              │
│                                         │
│ [>] Opción: 5                          │
└─────────────────────────────────────────┘
          ↓
┌─────────────────────────────────────────┐
│ [!] Cerrando sesión de ShadowBlade...  │
│ [+] Sesión cerrada exitosamente.       │
└─────────────────────────────────────────┘
          ↓
┌─────────────────────────────────────────┐
│ MENU INICIAL                           │  ← VUELVE AL INICIO
├─────────────────────────────────────────┤
│ [1] Iniciar Sesión                     │
│ [2] Registrarse                        │
│ [3] Salir                              │
│                                         │
│ [>] Opción: _                          │
└─────────────────────────────────────────┘
```

### **Escenario 2: Usuario sale de la app**

```
┌─────────────────────────────────────────┐
│ MENU PRINCIPAL - ShadowBlade           │
├─────────────────────────────────────────┤
│ [1] Juego Rápido                       │
│ [2] Buscar Salas                       │
│ [3] Ver Mi Perfil                      │
│ [4] Editar Perfil                      │
│ [5] Cerrar Sesión                      │
│ [6] Salir                  ← NUEVO     │
│                                         │
│ [>] Opción: 6                          │
└─────────────────────────────────────────┘
          ↓
┌─────────────────────────────────────────┐
│ ╔════════════════════════════════════╗ │
│ ║  ¡Hasta pronto, ShadowBlade!       ║ │
│ ║  Gracias por usar eScrims          ║ │
│ ╚════════════════════════════════════╝ │
│                                         │
│ [APP TERMINADA]                        │
└─────────────────────────────────────────┘
```

---

## 🔄 Diferencias entre Opción 5 y 6

| Opción | Acción | Loop Interno | Loop Externo | Resultado |
|--------|--------|--------------|--------------|-----------|
| **[5] Cerrar Sesión** | `sesionActiva = false` | ❌ Sale | ✅ Continúa | Vuelve al menú login/registro |
| **[6] Salir** | `sesionActiva = false` + `appRunning = false` | ❌ Sale | ❌ Sale | Cierra la aplicación |

---

## 🧪 Testing

### **Test 1: Cerrar sesión y volver a loguearse**
```
1. Login con usuario A
2. Opción [5] Cerrar Sesión
3. Vuelve al menú inicial ✅
4. Login con usuario B
5. Éxito ✅
```

### **Test 2: Cerrar sesión y registrar nuevo usuario**
```
1. Login con usuario existente
2. Opción [5] Cerrar Sesión
3. Vuelve al menú inicial ✅
4. Opción [2] Registrarse
5. Crear nuevo usuario ✅
6. Éxito ✅
```

### **Test 3: Salir directamente**
```
1. Login con usuario A
2. Opción [6] Salir
3. Aplicación termina ✅
```

### **Test 4: Salir desde menú inicial**
```
1. Menú inicial
2. Opción [3] Salir
3. Aplicación termina sin login ✅
```

---

## 📝 Notas Técnicas

### **Arquitectura de Loops**

```java
// LOOP EXTERNO: Aplicación
while (appRunning) {
    
    // Fase 1: Autenticación
    Usuario usuario = menuInicialAuth(...);
    
    if (usuario == null) {
        appRunning = false;  // ← Salir completamente
        continue;
    }
    
    // LOOP INTERNO: Sesión de usuario
    while (sesionActiva) {
        
        // Fase 2: Dashboard
        int opcion = menuPrincipal(usuario);
        
        // Opción 5: Cerrar sesión
        if (opcion == 5) {
            sesionActiva = false;  // ← Volver a Fase 1
        }
        
        // Opción 6: Salir
        if (opcion == 6) {
            sesionActiva = false;  // ← Salir de loop interno
            appRunning = false;    // ← Salir de loop externo
        }
    }
}
```

### **Variables de Control**

- **`appRunning`**: Controla si la aplicación sigue ejecutándose
  - `true` → App activa
  - `false` → Cerrar app y ejecutar cleanup

- **`sesionActiva`**: Controla si hay un usuario autenticado
  - `true` → Usuario logueado, mostrar dashboard
  - `false` → Volver al menú de login/registro

---

## ✅ Beneficios

1. **Multi-usuario**: Permite que diferentes usuarios usen la app sin reiniciarla
2. **Testing**: Facilita probar diferentes cuentas rápidamente
3. **UX mejorado**: Usuario puede cerrar sesión sin cerrar la app
4. **Seguridad**: Usuario puede salir de su cuenta en dispositivos compartidos

---

## 🚀 Próximas Mejoras

1. **Timeout automático**: Cerrar sesión después de X minutos de inactividad
2. **Confirmación**: Preguntar "¿Seguro que quieres cerrar sesión?"
3. **Guardar preferencias**: Recordar último juego seleccionado por usuario
4. **Estadísticas de sesión**: Mostrar tiempo jugado al cerrar sesión

---

## 📦 Archivos Modificados

- ✅ `codigo/src/main/Main.java` - Doble loop de autenticación/sesión
- ✅ `codigo/src/views/MenuView.java` - Menú con opción "Cerrar Sesión"

---

## 🎯 Entregable TP

Esta feature demuestra:
- ✅ **RF1 completo**: Login, Registro, Perfil, **Cerrar Sesión**
- ✅ **Arquitectura MVC** bien implementada
- ✅ **Control de flujo** con loops anidados
- ✅ **UX profesional** con opciones claras

¡Listo para entregar! 🎉
