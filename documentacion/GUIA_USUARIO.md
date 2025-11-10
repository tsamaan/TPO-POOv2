# 🎮 Guía de Usuario - eScrims Plataforma Interactiva

## 🚀 Cómo Ejecutar

### Opción 1: Usar el script (Windows)
```bash
.\run.bat
```

### Opción 2: Compilar y ejecutar manualmente
```bash
cd src
javac -encoding UTF-8 models/*.java states/*.java strategies/*.java notifiers/*.java auth/*.java service/*.java context/*.java interfaces/*.java main/*.java
java main.Main
```

---

## 📋 Flujo de Usuario

### 1️⃣ LOGIN
Al iniciar la aplicación, se te pedirá que ingreses:

```
[>] Ingresa tu nombre de usuario: 
[>] Ingresa tu email: 
[>] Ingresa tu contraseña: 
```

**Ejemplo:**
```
Nombre: ProGamer123
Email: gamer@esports.com
Contraseña: mipassword
```

El sistema utiliza el **Patrón ADAPTER** para autenticarte.

---

### 2️⃣ MENÚ PRINCIPAL

Después del login, verás:

```
[!] MENU PRINCIPAL - ProGamer123
───────────────────────────────────

[1] Buscar Partida (Scrim)
[2] Salir

[>] Selecciona una opción: 
```

---

### 3️⃣ BUSCAR PARTIDA

Si seleccionas **[1] Buscar Partida**:

#### Paso 1: Seleccionar Rol
```
[!] Selecciona tu rol preferido:

[1] Duelist
[2] Support
[3] Controller
[4] Initiator
[5] Sentinel

[>] Ingresa el número de tu rol:
```

#### Paso 2: Búsqueda de Jugadores
El sistema buscará automáticamente 7 jugadores más (bots simulados):

```
[!] BUSCANDO JUGADORES... (se necesitan 8 jugadores en total)

[1/8] Jugador encontrado: ShadowBlade (Duelist)
[2/8] Jugador encontrado: PhoenixFire (Support)
[3/8] Jugador encontrado: IceQueen (Controller)
...
[8/8] Jugador encontrado: MysticWizard (Sentinel)
```

**Tiempo estimado:** ~12 segundos (1.5 segundos por jugador)

El sistema utiliza el **Patrón STATE** para gestionar la búsqueda.

---

### 4️⃣ MATCHMAKING

Una vez encontrados los 8 jugadores:

```
[!] ¡LOBBY COMPLETO! Ejecutando matchmaking...

[*] Aplicando algoritmo de emparejamiento por MMR...
```

El sistema utiliza el **Patrón STRATEGY** para emparejar jugadores.

---

### 5️⃣ FORMACIÓN DE EQUIPOS

Los 8 jugadores se dividen en 2 equipos de 4:

```
╔═══════════════════════════════════════════════════════════════╗
║                      EQUIPOS FORMADOS                         ║
╠═══════════════════════════════════════════════════════════════╣
║                                                               ║
║  Team Azure                                                   ║
║   ★ ProGamer123                                               ║  <- TÚ
║     ShadowBlade                                               ║
║     PhoenixFire                                               ║
║     IceQueen                                                  ║
║                                                               ║
╠═══════════════════════════════════════════════════════════════╣
║                                                               ║
║  Team Crimson                                                 ║
║     ThunderStrike                                             ║
║     NightHawk                                                 ║
║     DragonSlayer                                              ║
║     SilentAssassin                                            ║
║                                                               ║
╚═══════════════════════════════════════════════════════════════╝

[★] Indica tu posición en el equipo
```

El **símbolo ★** marca tu posición en el equipo.

---

### 6️⃣ CONFIRMACIÓN

El sistema te pedirá que confirmes tu participación:

```
[!] FASE DE CONFIRMACIÓN

[>] ¿Confirmas tu participación? (S/N): 
```

**Opciones:**
- Presiona **S** o **Enter** para confirmar
- Presiona **N** para rechazar

Los demás jugadores (bots) confirmarán automáticamente.

El sistema utiliza la clase **Confirmacion** del modelo de dominio.

---

### 7️⃣ INICIO DE PARTIDA

Si todos confirman:

```
[!] INICIANDO PARTIDA...

[+] Estado: EstadoConfirmado
[*] La partida está en curso...
[*] Duración estimada: 25-30 minutos
[+] Estado: EstadoEnJuego
```

El sistema utiliza el **Patrón OBSERVER** para notificar cambios de estado.

---

### 8️⃣ ESTADÍSTICAS POST-PARTIDA

Al finalizar la partida, verás las estadísticas:

```
╔═══════════════════╦═══════╦═══════╦═══════╦════════════╗
║ Jugador           ║ Kills ║ Death ║ Asist ║ KDA Ratio  ║
╠═══════════════════╬═══════╬═══════╬═══════╬════════════╣
║ ProGamer123       ║    18 ║    12 ║    15 ║       2.75 ║
║ ShadowBlade       ║    14 ║    10 ║    12 ║       2.60 ║
║ PhoenixFire       ║    10 ║    15 ║     8 ║       1.20 ║
...
╚═══════════════════╩═══════╩═══════╩═══════╩════════════╝

[★] MVP: ProGamer123
    EXCELENTE (KDA: 2.75 | K/D/A: 18/12/15)

[!] RESULTADO FINAL:
    Team Azure: 52 kills
    Team Crimson: 48 kills

[★] GANADOR: Team Azure
```

El sistema utiliza la clase **Estadistica** para calcular KDA y rendimiento.

---

## 🎯 Patrones de Diseño Implementados

Durante el flujo interactivo, se utilizan:

| Patrón | Uso en el Sistema |
|--------|------------------|
| **ADAPTER** | Autenticación del usuario (login local) |
| **ABSTRACT FACTORY** | Creación de notificadores (Email, Discord, Push) |
| **STATE** | Gestión de estados del Scrim (Buscando → Lobby → Confirmado → EnJuego → Finalizado) |
| **STRATEGY** | Algoritmos de matchmaking (por MMR, por latencia) |
| **OBSERVER** | Notificaciones automáticas en cambios de estado |

---

## 📊 Clases del Modelo de Dominio

Las siguientes clases se utilizan:

- **Usuario**: Jugadores autenticados
- **Scrim**: Partida de práctica
- **Postulacion**: Registro de jugadores en la cola
- **Equipo**: Formación de Team Azure y Team Crimson
- **Confirmacion**: Sistema de confirmación de participación
- **Estadistica**: Registro de rendimiento post-partida (K/D/A, KDA)
- **Notificacion**: Mensajes del sistema

---

## 🔄 Ciclo de Estados del Scrim

```
EstadoBuscandoJugadores
         ↓
  (8 jugadores encontrados)
         ↓
   EstadoLobbyCompleto
         ↓
    (matchmaking)
         ↓
   EstadoConfirmado
         ↓
   (todos confirman)
         ↓
    EstadoEnJuego
         ↓
   (partida finaliza)
         ↓
   EstadoFinalizado
```

---

## 💡 Consejos

1. **Login rápido**: Solo presiona Enter en cada campo para usar valores por defecto
2. **Confirmación rápida**: Presiona Enter para confirmar automáticamente
3. **Experiencia completa**: Ingresa tus datos reales para una experiencia personalizada
4. **Múltiples partidas**: Puedes jugar varias partidas sin cerrar la aplicación

---

## 🐛 Solución de Problemas

### Error de compilación
```bash
cd src
javac -encoding UTF-8 models/*.java states/*.java strategies/*.java notifiers/*.java auth/*.java service/*.java context/*.java interfaces/*.java main/*.java
```

### La aplicación no responde
- Verifica que hayas presionado **Enter** después de cada input
- Asegúrate de estar en la ventana de terminal correcta

### Caracteres especiales no se ven
```bash
chcp 65001
```

---

## 📝 Ejemplo de Sesión Completa

```
[>] Ingresa tu nombre de usuario: GamerPro
[>] Ingresa tu email: gamer@test.com
[>] Ingresa tu contraseña: pass123

[+] ¡Bienvenido, GamerPro!

[1] Buscar Partida (Scrim)
[2] Salir

[>] Selecciona una opción: 1

[>] Ingresa el número de tu rol: 1

[!] BUSCANDO JUGADORES...
[1/8] Jugador encontrado: ShadowBlade (Duelist)
...
[8/8] Jugador encontrado: MysticWizard (Sentinel)

[!] ¡LOBBY COMPLETO!

[EQUIPOS FORMADOS]
★ GamerPro en Team Azure

[>] ¿Confirmas tu participación? (S/N): S

[!] INICIANDO PARTIDA...
[!] ESTADÍSTICAS POST-PARTIDA
[★] MVP: GamerPro
[★] GANADOR: Team Azure

[+] Volviendo al menú principal...
```

---

**¡Disfruta jugando eScrims! 🎮🏆**
