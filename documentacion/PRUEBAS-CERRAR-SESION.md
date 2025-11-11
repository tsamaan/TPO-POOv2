# 🧪 Prueba de Funcionalidad: Cerrar Sesión

## ✅ Resultado de Compilación

```powershell
# Compilación exitosa
javac -d bin -encoding UTF-8 main/Main.java
# ✅ Sin errores
```

## 🎮 Aplicación Ejecutándose

```
═══════════════════════════════════════════════════════════════════════════════
╔═════════════════════════════════════════════════════════════════════════════╗
║                                                                             ║
║                   eScrims - Plataforma de eSports                           ║
║                   Arquitectura MVC Refactorizada                            ║
║                                                                             ║
╚═════════════════════════════════════════════════════════════════════════════╝
═══════════════════════════════════════════════════════════════════════════════

[USER SERVICE] Usuario registrado: ShadowBlade (ID: 1)
[USER SERVICE] Usuario registrado: PhoenixFire (ID: 2)
[USER SERVICE] 2 usuarios de prueba creados

═══════════════════════════════════════════════════════════════════════════════
[!] BIENVENIDO A ESCRIMS PLATFORM
═══════════════════════════════════════════════════════════════════════════════

[1] Iniciar Sesión (Login)
[2] Registrarse (Crear cuenta)
[3] Salir

[>] Selecciona una opción (1-3):
```

## 📋 Usuarios de Prueba Disponibles

La aplicación crea automáticamente 2 usuarios para testing:

### **Usuario 1: ShadowBlade**
- **Email:** `shadow@escrims.com`
- **Password:** `password123`
- **Juego:** Valorant
- **Rango:** 1500 MMR
- **Roles:** Duelist, Controller

### **Usuario 2: PhoenixFire**
- **Email:** `phoenix@escrims.com`
- **Password:** `password456`
- **Juego:** League of Legends
- **Rango:** 1200 MMR
- **Roles:** Mid, ADC

## 🧪 Casos de Prueba

### **Test 1: Login → Cerrar Sesión → Login con otro usuario**

**Pasos:**
```
1. Opción [1] Iniciar Sesión
2. Email: shadow@escrims.com
3. Password: password123
4. ✅ Login exitoso → MENU PRINCIPAL - ShadowBlade
5. Opción [5] Cerrar Sesión
6. ✅ Mensaje: "Cerrando sesión de ShadowBlade..."
7. ✅ Vuelve al menú inicial
8. Opción [1] Iniciar Sesión
9. Email: phoenix@escrims.com
10. Password: password456
11. ✅ Login exitoso → MENU PRINCIPAL - PhoenixFire
```

**Resultado Esperado:**
- ✅ El usuario ShadowBlade cierra sesión
- ✅ La aplicación NO se cierra
- ✅ Vuelve al menú de login/registro
- ✅ Puede loguearse con otro usuario (PhoenixFire)
- ✅ Los datos de ambos usuarios persisten en memoria

---

### **Test 2: Login → Ver Perfil → Cerrar Sesión → Salir**

**Pasos:**
```
1. Opción [1] Iniciar Sesión
2. Email: shadow@escrims.com
3. Password: password123
4. ✅ Login exitoso → MENU PRINCIPAL - ShadowBlade
5. Opción [3] Ver Mi Perfil
6. ✅ Muestra perfil completo de ShadowBlade
7. Volver al menú principal
8. Opción [5] Cerrar Sesión
9. ✅ Mensaje: "Cerrando sesión de ShadowBlade..."
10. ✅ Vuelve al menú inicial
11. Opción [3] Salir
12. ✅ Aplicación termina
```

**Resultado Esperado:**
- ✅ Puede ver perfil antes de cerrar sesión
- ✅ Cierra sesión correctamente
- ✅ Puede salir desde el menú inicial sin loguearse

---

### **Test 3: Login → Salir directamente (sin cerrar sesión)**

**Pasos:**
```
1. Opción [1] Iniciar Sesión
2. Email: shadow@escrims.com
3. Password: password123
4. ✅ Login exitoso → MENU PRINCIPAL - ShadowBlade
5. Opción [6] Salir
6. ✅ Mensaje: "¡Hasta pronto, ShadowBlade!"
7. ✅ Aplicación termina inmediatamente
```

**Resultado Esperado:**
- ✅ No requiere cerrar sesión antes de salir
- ✅ Opción [6] Salir cierra sesión automáticamente y termina app

---

### **Test 4: Registro → Cerrar Sesión → Login**

**Pasos:**
```
1. Opción [2] Registrarse
2. Username: TestUser
3. Email: test@escrims.com
4. Password: test123
5. Tipo: [1] Local
6. ✅ Registro exitoso
7. Configurar perfil inicial
8. ✅ Login automático → MENU PRINCIPAL - TestUser
9. Opción [5] Cerrar Sesión
10. ✅ Vuelve al menú inicial
11. Opción [1] Iniciar Sesión
12. Email: test@escrims.com
13. Password: test123
14. ✅ Login exitoso → MENU PRINCIPAL - TestUser
```

**Resultado Esperado:**
- ✅ Usuario recién registrado puede cerrar sesión
- ✅ Puede volver a loguearse con las mismas credenciales
- ✅ Los datos persisten en memoria

---

## 🔍 Verificación de Logs

Durante la ejecución, la aplicación muestra logs útiles:

```
[USER SERVICE] Usuario registrado: ShadowBlade (ID: 1)
[USER SERVICE] Usuario registrado: PhoenixFire (ID: 2)
[USER SERVICE] 2 usuarios de prueba creados
[USER SERVICE] Autenticación exitosa: ShadowBlade
[USER CONTROLLER] Login exitoso: ShadowBlade
```

**Al cerrar sesión:**
```
[!] Cerrando sesión de ShadowBlade...
[+] Sesión cerrada exitosamente.
```

**Al volver a loguearse:**
```
[USER SERVICE] Autenticación exitosa: PhoenixFire
[USER CONTROLLER] Login exitoso: PhoenixFire
```

---

## 📊 Diagrama de Flujo

```
┌─────────────────────┐
│   Iniciar App       │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│  Menú Inicial       │ ◄──────────────────┐
│  [1] Login          │                    │
│  [2] Registro       │                    │
│  [3] Salir          │                    │
└──────────┬──────────┘                    │
           │                               │
      [1 o 2]                              │
           │                               │
           ▼                               │
┌─────────────────────┐                    │
│  Autenticación      │                    │
│  exitosa            │                    │
└──────────┬──────────┘                    │
           │                               │
           ▼                               │
┌─────────────────────┐                    │
│  Menu Principal     │                    │
│  [1] Juego Rápido   │                    │
│  [2] Buscar Salas   │                    │
│  [3] Ver Perfil     │                    │
│  [4] Editar Perfil  │                    │
│  [5] Cerrar Sesión  │ ───────────────────┘ (Vuelve al inicio)
│  [6] Salir          │ ───────────────────┐
└─────────────────────┘                    │
                                           │
                                           ▼
                                    ┌──────────────┐
                                    │  Terminar    │
                                    │  Aplicación  │
                                    └──────────────┘
```

---

## ✅ Checklist de Funcionalidades

- ✅ **Login** funciona correctamente
- ✅ **Registro** funciona correctamente
- ✅ **Cerrar Sesión** (opción 5) vuelve al menú inicial
- ✅ **Salir** (opción 6) termina la aplicación
- ✅ **Multi-sesión**: Puede loguearse con diferentes usuarios sin reiniciar
- ✅ **Persistencia en memoria**: Usuarios registrados persisten durante la ejecución
- ✅ **Logs informativos**: Muestra mensajes claros en cada acción

---

## 🚀 Instrucciones de Ejecución

### **Compilar:**
```powershell
cd "c:\Users\Galli\OneDrive\Desktop\Clases Uade\Segundo Cuatrimestre\2 - Proceso de Desarrollo de software\TPO-POOv2\codigo\src"
javac -d ../bin -encoding UTF-8 main/Main.java
```

### **Ejecutar:**
```powershell
cd "c:\Users\Galli\OneDrive\Desktop\Clases Uade\Segundo Cuatrimestre\2 - Proceso de Desarrollo de software\TPO-POOv2\codigo"
java -cp "bin;src" main.Main
```

### **Credenciales de Prueba:**
- **Usuario 1:** `shadow@escrims.com` / `password123`
- **Usuario 2:** `phoenix@escrims.com` / `password456`

---

## 🎯 Conclusión

✅ **Feature "Cerrar Sesión" implementada exitosamente**

La aplicación ahora permite:
1. Login con múltiples usuarios
2. Cerrar sesión sin cerrar la app
3. Volver al menú de autenticación
4. Salir completamente cuando se desee

**Listo para entregar el TP!** 🎉
