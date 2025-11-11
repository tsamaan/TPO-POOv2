# 📚 Cómo Funciona el Login y Registro en eScrims

## 🎯 Resumen Ejecutivo

El sistema de autenticación está implementado usando **arquitectura MVC** con las siguientes capas:

- **Model:** `Usuario` (con todos los campos de RF1)
- **View:** `AuthView` (captura datos del usuario)
- **Controller:** `UserController` (lógica de negocio)
- **Service:** `UserService` (persistencia en memoria)

## 📊 Arquitectura del Sistema

```
┌─────────────┐
│    Main     │ ← Punto de entrada
└──────┬──────┘
       │
       ├──→ UserController ←─── (MVC Controller)
       │         │
       │         ├──→ AuthView (captura datos)
       │         ├──→ UserService (almacenamiento)
       │         └──→ ProfileView (configuración)
       │
       └──→ AuthService (autenticación)
```

---

## 🔐 Flujo de REGISTRO (RF1)

### **Paso 1: Usuario elige "Registrarse"**

```java
// Main.java
Usuario usuario = userController.registrar();
```

### **Paso 2: UserController captura datos**

```java
// UserController.java - línea 51
public Usuario registrar() {
    // 1. Solicitar datos al usuario
    AuthView.DatosRegistro datos = authView.solicitarDatosRegistro();
    
    // Datos capturados:
    // - datos.username    (ej: "ShadowBlade")
    // - datos.email       (ej: "shadow@escrims.com")
    // - datos.password    (ej: "miPassword123")
    // - datos.tipoAuth    (LOCAL, STEAM, RIOT, DISCORD)
```

### **Paso 3: Validaciones**

```java
    // 2. Validar email
    if (!userService.validarEmail(datos.email)) {
        authView.mostrarErrorRegistro("Email inválido");
        return null;
    }

    // 3. Validar password (mínimo 6 caracteres)
    if (!userService.validarPassword(datos.password)) {
        authView.mostrarErrorRegistro("Password debe tener al menos 6 caracteres");
        return null;
    }
```

### **Paso 4: Crear usuario en UserService**

```java
    // 4. Registrar usuario
    Usuario nuevoUsuario = userService.registrarUsuario(
        datos.username,
        datos.email,
        datos.password,
        datos.tipoAuth
    );
```

**¿Qué hace `registrarUsuario()`?** (UserService.java línea 48)

```java
public Usuario registrarUsuario(String username, String email, String password,
                                TipoAutenticacion tipoAuth) {

    // 1. Verificar que email no exista
    if (existeEmail(email)) {
        throw new IllegalArgumentException("El email ya está registrado");
    }

    // 2. Verificar que username no exista
    if (existeUsername(username)) {
        throw new IllegalArgumentException("El nombre de usuario ya está en uso");
    }

    // 3. HASHEAR el password (SHA-256)
    String passwordHash = hashPassword(password);
    // Ejemplo: "miPassword123" → "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8"

    // 4. CREAR el usuario
    Usuario nuevoUsuario = new Usuario(
        nextId++,        // ID auto-incremental (1, 2, 3...)
        username,        // "ShadowBlade"
        email,           // "shadow@escrims.com"
        passwordHash,    // Hash generado
        tipoAuth         // LOCAL
    );

    // 5. GUARDAR en la lista in-memory
    usuarios.add(nuevoUsuario);
    
    // 6. LOG
    System.out.println("[USER SERVICE] Usuario registrado: " + username + 
                     " (ID: " + nuevoUsuario.getId() + ")");

    return nuevoUsuario;
}
```

### **Paso 5: Verificación de Email**

```java
    // 5. Simular envío de email de verificación
    userService.enviarEmailVerificacion(nuevoUsuario);
    authView.mostrarVerificacionEmail(nuevoUsuario.getEmail(), true);
    
    // En producción: enviaría link real
    // Para demo: auto-verifica inmediatamente
```

**¿Qué hace `enviarEmailVerificacion()`?** (UserService.java línea 127)

```java
public void enviarEmailVerificacion(Usuario usuario) {
    System.out.println("[USER SERVICE] Email de verificación enviado a: " + usuario.getEmail());
    System.out.println("                (En producción se enviaría link real)");
    
    // Para demo: auto-verificar
    System.out.println("                Simulando verificación automática...");
    verificarEmail(usuario);  // ← Cambia estadoEmail a VERIFICADO
}

public void verificarEmail(Usuario usuario) {
    usuario.verificarEmail();  // ← Llama a Usuario.setEstadoEmail(VERIFICADO)
    System.out.println("[USER SERVICE] Email verificado para: " + usuario.getUsername());
}
```

### **Paso 6: Configuración inicial del perfil**

```java
    // 6. Configurar perfil inicial
    configurarPerfilInicial(nuevoUsuario);
    
    // Solicita:
    // - Juego principal
    // - Rango/MMR
    // - Roles preferidos
    // - Región
    // - Disponibilidad horaria
    
    return nuevoUsuario;
}
```

---

## 🔓 Flujo de LOGIN (RF1)

### **Paso 1: Usuario elige "Iniciar Sesión"**

```java
// Main.java
Usuario usuario = userController.login();
```

### **Paso 2: UserController captura credenciales**

```java
// UserController.java - línea 142
public Usuario login() {
    // 1. Solicitar email y password
    AuthView.DatosLogin datos = authView.solicitarDatosLogin();
    
    // Datos capturados:
    // - datos.email     (ej: "shadow@escrims.com")
    // - datos.password  (ej: "miPassword123")
```

### **Paso 3: Autenticar con UserService**

```java
    // 2. Autenticar
    Optional<Usuario> usuarioOpt = userService.autenticarUsuario(
        datos.email, 
        datos.password
    );
    
    if (!usuarioOpt.isPresent()) {
        authView.mostrarErrorLogin("Email o password incorrectos");
        return null;
    }
```

**¿Qué hace `autenticarUsuario()`?** (UserService.java línea 86)

```java
public Optional<Usuario> autenticarUsuario(String email, String password) {
    // 1. Buscar usuario por email
    Optional<Usuario> usuarioOpt = buscarPorEmail(email);
    
    if (!usuarioOpt.isPresent()) {
        System.out.println("[USER SERVICE] Usuario no encontrado: " + email);
        return Optional.empty();
    }
    
    Usuario usuario = usuarioOpt.get();
    
    // 2. VERIFICAR PASSWORD
    String passwordHash = hashPassword(password);  // ← Hashea el password ingresado
    
    if (!usuario.getPasswordHash().equals(passwordHash)) {
        // ← Compara el hash guardado con el hash del password ingresado
        System.out.println("[USER SERVICE] Password incorrecto para: " + email);
        return Optional.empty();
    }
    
    // 3. LOGIN EXITOSO
    System.out.println("[USER SERVICE] Autenticación exitosa: " + usuario.getUsername());
    return Optional.of(usuario);
}
```

### **Paso 4: Login exitoso**

```java
    // 3. Usuario autenticado
    Usuario usuario = usuarioOpt.get();
    authView.mostrarLoginExitoso(usuario.getUsername());
    
    System.out.println("[USER CONTROLLER] Login exitoso: " + usuario.getUsername());
    return usuario;
}
```

---

## 💾 ¿Dónde se GUARDAN los Datos?

### **Actualmente: In-Memory Storage**

```java
// UserService.java
public class UserService {
    
    // "Base de datos" en memoria
    private List<Usuario> usuarios;  // ← Lista de usuarios registrados
    private int nextId;              // ← ID auto-incremental
    
    public UserService() {
        this.usuarios = new ArrayList<>();
        this.nextId = 1;
    }
    
    public Usuario registrarUsuario(...) {
        // ...
        usuarios.add(nuevoUsuario);  // ← GUARDAR en la lista
        // ...
    }
}
```

### **Características:**

✅ **Ventajas:**
- Simple para demo/prototipo
- No requiere base de datos
- Rápido desarrollo

❌ **Limitaciones:**
- **Los datos se PIERDEN al cerrar la aplicación**
- No hay persistencia
- Máximo ~10,000 usuarios en memoria

---

## 🗄️ ¿Cómo Agregar Persistencia REAL?

### **Opción 1: Archivo JSON** (Más simple)

```java
// UserService.java
import com.google.gson.Gson;
import java.io.*;

public class UserService {
    private static final String DB_FILE = "usuarios.json";
    private List<Usuario> usuarios;
    
    public UserService() {
        this.usuarios = cargarDesdeArchivo();
    }
    
    private List<Usuario> cargarDesdeArchivo() {
        try {
            Gson gson = new Gson();
            Reader reader = new FileReader(DB_FILE);
            List<Usuario> usuarios = gson.fromJson(reader, 
                new TypeToken<List<Usuario>>(){}.getType());
            reader.close();
            return usuarios != null ? usuarios : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
    
    private void guardarEnArchivo() {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Writer writer = new FileWriter(DB_FILE);
            gson.toJson(usuarios, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public Usuario registrarUsuario(...) {
        // ...
        usuarios.add(nuevoUsuario);
        guardarEnArchivo();  // ← PERSISTIR después de cada cambio
        return nuevoUsuario;
    }
}
```

**Archivo generado:** `usuarios.json`
```json
[
  {
    "id": 1,
    "username": "ShadowBlade",
    "email": "shadow@escrims.com",
    "passwordHash": "5e884898da2804...",
    "tipoAuth": "LOCAL",
    "estadoEmail": "VERIFICADO",
    "juegoPrincipal": "Valorant",
    "rangoPorJuego": {
      "Valorant": 1500,
      "League of Legends": 1200
    },
    "rolesPreferidos": ["Duelist", "Controller"],
    "region": "SA"
  }
]
```

### **Opción 2: Base de Datos (H2, SQLite)** (Producción)

```java
// UserRepository.java
import java.sql.*;

public class UserRepository {
    private Connection connection;
    
    public UserRepository() throws SQLException {
        // Conectar a base de datos SQLite
        connection = DriverManager.getConnection("jdbc:sqlite:escrims.db");
        crearTablas();
    }
    
    private void crearTablas() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                email TEXT UNIQUE NOT NULL,
                password_hash TEXT NOT NULL,
                tipo_auth TEXT NOT NULL,
                estado_email TEXT NOT NULL,
                juego_principal TEXT,
                region TEXT,
                disponibilidad TEXT,
                fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;
        connection.createStatement().execute(sql);
    }
    
    public Usuario guardarUsuario(Usuario usuario) throws SQLException {
        String sql = """
            INSERT INTO usuarios (username, email, password_hash, tipo_auth, estado_email)
            VALUES (?, ?, ?, ?, ?)
        """;
        
        PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, usuario.getUsername());
        stmt.setString(2, usuario.getEmail());
        stmt.setString(3, usuario.getPasswordHash());
        stmt.setString(4, usuario.getTipoAuth().toString());
        stmt.setString(5, usuario.getEstadoEmail().toString());
        stmt.executeUpdate();
        
        // Obtener ID generado
        ResultSet keys = stmt.getGeneratedKeys();
        if (keys.next()) {
            usuario.setId(keys.getInt(1));
        }
        
        return usuario;
    }
    
    public Optional<Usuario> buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, email);
        
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            Usuario usuario = new Usuario(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password_hash"),
                TipoAutenticacion.valueOf(rs.getString("tipo_auth"))
            );
            usuario.setEstadoEmail(EstadoEmail.valueOf(rs.getString("estado_email")));
            return Optional.of(usuario);
        }
        
        return Optional.empty();
    }
}
```

---

## 🔒 Seguridad del Password

### **Hash SHA-256**

```java
// UserService.java - línea 228
private String hashPassword(String password) {
    try {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException("Error hasheando password", e);
    }
}
```

**Ejemplo:**
```
Input:  "miPassword123"
Output: "cGFzc3dvcmQxMjM="  (Base64 del hash SHA-256)
```

### **⚠️ Para Producción: Usar BCrypt**

```java
// Agregar dependencia: org.mindrot.jbcrypt
import org.mindrot.jbcrypt.BCrypt;

private String hashPassword(String password) {
    return BCrypt.hashpw(password, BCrypt.gensalt(12));
}

private boolean verificarPassword(String password, String hash) {
    return BCrypt.checkpw(password, hash);
}
```

---

## 📝 Modelo de Usuario Completo

```java
// Usuario.java
public class Usuario {
    // IDENTIFICACIÓN
    private int id;
    private String username;
    private String email;
    
    // AUTENTICACIÓN
    private String passwordHash;
    private TipoAutenticacion tipoAuth;  // LOCAL, STEAM, RIOT, DISCORD
    private EstadoEmail estadoEmail;     // PENDIENTE, VERIFICADO
    
    // PERFIL EDITABLE
    private String juegoPrincipal;
    private Map<String, Integer> rangoPorJuego;
    private List<String> rolesPreferidos;
    private String region;
    private String disponibilidadHoraria;
    
    // SISTEMA
    private String rol;                  // Rol temporal en scrim
    private List<Notificacion> notificaciones;
}
```

---

## 🎮 Flujo Completo - Ejemplo Práctico

### **Escenario: Nuevo usuario se registra**

```
┌────────────────────────────────────────────────────────────┐
│ TERMINAL                                                   │
├────────────────────────────────────────────────────────────┤
│ [1] Iniciar Sesión (Login)                               │
│ [2] Registrarse (Crear cuenta)                           │
│ [3] Salir                                                 │
│                                                            │
│ [>] Selecciona una opción: 2                             │
│                                                            │
│ ═══════════════════════════════════════════════════════   │
│ [!] REGISTRO - Crear Nueva Cuenta                        │
│ ═══════════════════════════════════════════════════════   │
│                                                            │
│ [?] Username: ShadowBlade                                │
│ [?] Email: shadow@escrims.com                            │
│ [?] Password: ******                                     │
│                                                            │
│ [?] Tipo de autenticación:                               │
│     [1] Local (email/password)                           │
│     [2] Steam                                             │
│     [3] Riot Games                                        │
│     [4] Discord                                           │
│ [>] Opción: 1                                            │
│                                                            │
│ [+] ¡Registro exitoso!                                   │
│ [+] Usuario creado: ShadowBlade                          │
│                                                            │
│ [USER SERVICE] Usuario registrado: ShadowBlade (ID: 1)   │
│ [USER SERVICE] Email de verificación enviado a:          │
│                shadow@escrims.com                         │
│                (En producción se enviaría link real)      │
│                Simulando verificación automática...       │
│ [USER SERVICE] Email verificado para: ShadowBlade        │
│                                                            │
│ ═══════════════════════════════════════════════════════   │
│ [!] Configuración Inicial de Perfil                      │
│ ═══════════════════════════════════════════════════════   │
│                                                            │
│ [?] Juego principal: Valorant                            │
│ [?] Rango/MMR en Valorant: 1500                          │
│ [?] Roles preferidos (separados por coma): Duelist,Controller│
│ [?] Región: SA                                           │
│                                                            │
│ [+] ¡Perfil configurado!                                 │
│ [+] Bienvenido a eScrims, ShadowBlade!                   │
└────────────────────────────────────────────────────────────┘
```

### **¿Qué pasó internamente?**

1. **Usuario ingresa datos** → `AuthView.solicitarDatosRegistro()`
2. **Validaciones** → `UserService.validarEmail()`, `validarPassword()`
3. **Crear usuario** → `UserService.registrarUsuario()`
   - Hash password: `"miPassword"` → `"5e884898da280..."`
   - Generar ID: `nextId++` → `1`
   - **GUARDAR:** `usuarios.add(nuevoUsuario)` ← **AQUÍ SE GUARDA EN MEMORIA**
4. **Verificar email** → `userService.enviarEmailVerificacion()`
   - Cambiar estado: `PENDIENTE` → `VERIFICADO`
5. **Configurar perfil** → `configurarPerfilInicial()`
6. **Login automático** → Usuario listo para usar la app

---

## 📦 Datos Almacenados en Memoria

```
UserService.usuarios = [
  Usuario {
    id: 1,
    username: "ShadowBlade",
    email: "shadow@escrims.com",
    passwordHash: "cGFzc3dvcmQxMjM=",
    tipoAuth: LOCAL,
    estadoEmail: VERIFICADO,
    juegoPrincipal: "Valorant",
    rangoPorJuego: {Valorant: 1500},
    rolesPreferidos: ["Duelist", "Controller"],
    region: "SA",
    disponibilidadHoraria: "18:00-23:00 UTC-3",
    notificaciones: []
  }
]
```

---

## 🚀 Próximos Pasos para Mejorar

### **1. Agregar persistencia con JSON**
```bash
# Agregar dependencia Gson al pom.xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.10.1</version>
</dependency>
```

### **2. Implementar base de datos SQLite**
```bash
# Agregar dependencia SQLite al pom.xml
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.44.1.0</version>
</dependency>
```

### **3. Mejorar seguridad con BCrypt**
```bash
# Agregar dependencia BCrypt
<dependency>
    <groupId>org.mindrot</groupId>
    <artifactId>jbcrypt</artifactId>
    <version>0.4</version>
</dependency>
```

---

## ❓ Preguntas Frecuentes

### **P: ¿Los usuarios se guardan al cerrar la app?**
**R:** No, actualmente se almacenan en memoria (`List<Usuario>`) y se pierden al cerrar.

### **P: ¿Cómo hacer que persistan?**
**R:** Implementar guardado en JSON (opción simple) o base de datos SQLite (opción robusta).

### **P: ¿El password está seguro?**
**R:** Usa SHA-256 + Base64 (básico). Para producción usar BCrypt.

### **P: ¿Puedo crear usuarios de prueba?**
**R:** Sí, `UserService.crearUsuariosPrueba()` crea 2 usuarios predefinidos:
- `shadow@escrims.com` / `password123`
- `phoenix@escrims.com` / `password456`

### **P: ¿Cómo verifico si un usuario existe?**
**R:** `UserService.existeEmail(email)` o `buscarPorEmail(email)`

---

## 📊 Diagrama de Clases Simplificado

```
┌─────────────┐
│    Main     │
└──────┬──────┘
       │
       ├─────────────────────────────────┐
       │                                 │
┌──────▼──────────┐              ┌──────▼──────────┐
│ UserController  │              │  UserService    │
├─────────────────┤              ├─────────────────┤
│ + registrar()   │─────────────→│ + registrarUsuario()
│ + login()       │              │ + autenticarUsuario()
│ + verPerfil()   │              │ + validarEmail()
│ + editarPerfil()│              │ + hashPassword()
└────────┬────────┘              │ - usuarios: List
         │                        │ - nextId: int
         ↓                        └─────────────────┘
┌─────────────────┐
│    AuthView     │
├─────────────────┤
│ + solicitarDatosRegistro()
│ + solicitarDatosLogin()
│ + mostrarRegistroExitoso()
└─────────────────┘
```

---

## ✅ Resumen

- **Registro:** `UserController.registrar()` → `UserService.registrarUsuario()` → Guarda en `List<Usuario>`
- **Login:** `UserController.login()` → `UserService.autenticarUsuario()` → Busca en lista y verifica hash
- **Almacenamiento:** In-memory (`ArrayList`) - se pierde al cerrar app
- **Seguridad:** SHA-256 + Base64 (básico, mejorar con BCrypt)
- **Para persistir:** Implementar guardado en JSON o base de datos

¿Necesitas ayuda implementando la persistencia? 🚀
