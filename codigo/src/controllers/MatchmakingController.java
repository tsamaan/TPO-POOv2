package controllers;

import models.*;
import states.*;
import strategies.*;
import notifiers.*;
import interfaces.INotifier;
import views.*;
import service.*;
import context.ScrimContext;
import commands.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * CAPA CONTROLLER (MVC) - Gestión de Matchmaking
 *
 * Responsabilidades:
 * - Orquestar flujo de juego rápido (matchmaking automático)
 * - Gestionar matchmaking con diferentes estrategias
 * - Formar equipos y gestionar roles
 * - Procesar confirmaciones
 * - Gestionar ciclo completo de partida
 *
 * @pattern MVC - Controller Layer
 */
public class MatchmakingController {

    private ConsoleView consoleView;
    private MenuView menuView;
    private GameView gameView;
    private ScrimController scrimController;

    public MatchmakingController(ConsoleView consoleView, MenuView menuView,
                                GameView gameView, ScrimController scrimController) {
        this.consoleView = consoleView;
        this.menuView = menuView;
        this.gameView = gameView;
        this.scrimController = scrimController;
    }

    // ============================================
    // JUEGO RÁPIDO (MATCHMAKING AUTOMÁTICO)
    // ============================================

    /**
     * Maneja el flujo completo de juego rápido
     */
    public void juegoRapido(Usuario usuario, UserController userController) {
        consoleView.mostrarTitulo("JUEGO RÁPIDO - MATCHMAKING AUTOMÁTICO");

        // Seleccionar juego - usar juego principal del usuario si está configurado
        String juegoSeleccionado;
        if (usuario.getJuegoPrincipal() != null && !usuario.getJuegoPrincipal().isEmpty()) {
            juegoSeleccionado = usuario.getJuegoPrincipal();
            consoleView.mostrarExito("Usando tu juego preferido: " + juegoSeleccionado);
        } else {
            juegoSeleccionado = menuView.seleccionarJuego();
        }
        
        // NUEVO: Usar formato específico del juego
        String formato = models.JuegoConfig.getFormatoDefault(juegoSeleccionado);
        consoleView.mostrarInfo("Formato: " + formato + " (" + 
            models.JuegoConfig.getJugadoresTotales(formato) + " jugadores)");

        // Configurar rango
        int rangoUsuario = userController.configurarRango(usuario, juegoSeleccionado);

        // Seleccionar rol
        String rolSeleccionado = userController.seleccionarRol(juegoSeleccionado);

        // Inicializar sistema de notificaciones
        gameView.mostrarInicializandoNotificaciones();
        NotifierFactory factory = new SimpleNotifierFactory();
        INotifier emailNotifier = factory.createEmailNotifier();
        // TODO: Activar PushNotifier en el futuro
        // INotifier pushNotifier = factory.createPushNotifier();
        gameView.mostrarNotificacionesActivas();

        // Crear scrim automático
        System.out.println();
        Scrim scrim = scrimController.crearScrimAutomatico(juegoSeleccionado, formato, rangoUsuario);

        // Agregar notificadores (Observer pattern)
        // Actualmente solo Email está activo
        scrim.addNotifier(emailNotifier);
        // TODO: Activar cuando se implemente PushNotifier
        // scrim.addNotifier(pushNotifier);

        ScrimContext context = new ScrimContext(scrim, scrim.getEstado());

        // Postular usuario actual
        consoleView.mostrarInfo("Uniéndote al matchmaking...");
        context.postular(usuario, rolSeleccionado);

        gameView.mostrarInicioMatchmaking(juegoSeleccionado, rolSeleccionado, rangoUsuario,
                                         scrim.getRangoMin(), scrim.getRangoMax());

        // Buscar jugadores con estrategia MMR
        List<Usuario> jugadoresEncontrados = buscarJugadoresConMMR(usuario, scrim, juegoSeleccionado, rolSeleccionado);

        // Ejecutar matchmaking
        MatchmakingService matchmakingService = new MatchmakingService(new ByMMRStrategy());
        consoleView.mostrarInfo("Aplicando algoritmo de matchmaking por MMR...");
        matchmakingService.ejecutarEmparejamiento(scrim);

        gameView.mostrarMatchEncontrado(jugadoresEncontrados.size());

        // Iniciar partida
        iniciarPartida(scrim, context, jugadoresEncontrados, usuario);
    }

    /**
     * Busca jugadores usando estrategia MMR
     * ACTUALIZADO: Usa el número correcto de jugadores según el formato
     * NUEVO: Garantiza 1 de cada rol por equipo en LoL (5v5)
     */
    private List<Usuario> buscarJugadoresConMMR(Usuario usuarioActual, Scrim scrim,
                                                String juego, String rolUsuario) {
        List<Usuario> jugadores = new ArrayList<>();
        jugadores.add(usuarioActual);
        
        // ASIGNAR ROL AL USUARIO ACTUAL
        usuarioActual.setRol(rolUsuario);

        // Calcular cuántos jugadores faltan según el formato del juego
        int jugadoresTotales = models.JuegoConfig.getJugadoresTotales(scrim.getFormato());
        int jugadoresFaltantes = jugadoresTotales - 1; // -1 porque ya agregamos al usuario actual

        Random random = new Random();
        String[] nombresBot = {"Shadow", "Phoenix", "Ghost", "Ninja", "Hunter", "Viper", "Storm", "Blaze", "Frost", "Thunder"};
        String[] rolesDisponibles = menuView.getRolesDisponibles(juego);

        int rangoUsuario = usuarioActual.getRangoPorJuego().get(juego);

        // Para LoL/Valorant 5v5: necesitamos asignar roles únicos por equipo
        List<String> rolesEquipo1 = new ArrayList<>();
        List<String> rolesEquipo2 = new ArrayList<>();
        
        // Agregar el rol del usuario al primer equipo
        rolesEquipo1.add(rolUsuario);

        for (int i = 0; i < jugadoresFaltantes; i++) {
            int rangoBot = rangoUsuario + random.nextInt(300) - 150;
            rangoBot = Math.max(scrim.getRangoMin(), Math.min(scrim.getRangoMax(), rangoBot));

            Usuario bot = new Usuario(i + 100, nombresBot[i % nombresBot.length] + random.nextInt(100),
                                     "bot" + (i+1) + "@escrims.com");
            bot.getRangoPorJuego().put(juego, rangoBot);

            // Asignar rol según el juego y formato
            String rolBot;
            if (esJuegoConRolesUnicos(juego)) {
                // Para LoL/Valorant: asignar roles únicos por equipo
                int jugadoresEnEquipo1 = rolesEquipo1.size();
                int jugadoresEnEquipo2 = rolesEquipo2.size();
                int jugadoresPorEquipo = jugadoresTotales / 2;

                if (jugadoresEnEquipo1 < jugadoresPorEquipo) {
                    // Asignar al equipo 1
                    rolBot = obtenerRolDisponible(rolesDisponibles, rolesEquipo1);
                    rolesEquipo1.add(rolBot);
                } else {
                    // Asignar al equipo 2
                    rolBot = obtenerRolDisponible(rolesDisponibles, rolesEquipo2);
                    rolesEquipo2.add(rolBot);
                }
            } else {
                // Para otros juegos: rol aleatorio
                rolBot = rolesDisponibles[random.nextInt(rolesDisponibles.length)];
            }
            
            bot.setRol(rolBot);

            ScrimContext tempContext = new ScrimContext(scrim, scrim.getEstado());
            tempContext.postular(bot, rolBot);

            jugadores.add(bot);

            gameView.mostrarJugadorEncontrado(bot.getUsername(), rangoBot, i + 2, jugadoresTotales);
        }

        return jugadores;
    }

    /**
     * Verifica si el juego requiere roles únicos por equipo
     */
    private boolean esJuegoConRolesUnicos(String juego) {
        String juegoLower = juego.toLowerCase();
        return juegoLower.contains("league") || juegoLower.contains("lol") || 
               juegoLower.contains("valorant");
    }

    /**
     * Obtiene un rol disponible que no esté ya asignado en el equipo
     */
    private String obtenerRolDisponible(String[] rolesDisponibles, List<String> rolesYaAsignados) {
        for (String rol : rolesDisponibles) {
            if (!rolesYaAsignados.contains(rol)) {
                return rol;
            }
        }
        // Si todos los roles están asignados, devolver el primero (fallback)
        return rolesDisponibles[0];
    }

    // ============================================
    // INICIO Y FLUJO DE PARTIDA
    // ============================================

    /**
     * Inicia y maneja el flujo completo de una partida
     */
    private void iniciarPartida(Scrim scrim, ScrimContext context,
                               List<Usuario> jugadores, Usuario usuarioActual) {

        gameView.mostrarInicioPartida();

        // Formar equipos
        Equipo[] equipos = formarEquipos(jugadores);
        Equipo equipoAzul = equipos[0];
        Equipo equipoRojo = equipos[1];

        // Obtener roles
        List<String> rolesAsignados = obtenerRolesAsignados(jugadores);

        // Generar estadísticas UNA SOLA VEZ (ANTES de transiciones)
        List<Estadistica> estadisticas = generarEstadisticas(jugadores, scrim);

        // PRIMERO: Transiciones de estado (incluye confirmación)
        // Si el usuario rechaza, la función retorna antes de mostrar los equipos
        boolean partidaConfirmada = ejecutarTransicionesEstado(scrim, context, usuarioActual, jugadores, estadisticas);
        
        if (!partidaConfirmada) {
            // Usuario rechazó la confirmación - no continuar
            return;
        }

        // DESPUÉS de confirmar: Mostrar equipos formados
        consoleView.mostrarSubtitulo("FORMANDO EQUIPOS");
        consoleView.mostrarEquipos(equipoAzul, equipoRojo, rolesAsignados, jugadores, usuarioActual);

        // Mostrar estadísticas en terminal (MISMAS que se enviaron por email)
        mostrarEstadisticasFinales(estadisticas, scrim, equipoAzul, equipoRojo, jugadores);

        gameView.mostrarVolviendoMenu();
    }

    /**
     * Forma dos equipos de forma equitativa
     * ACTUALIZADO: Soporta diferentes formatos (5v5, 3v3, 2v2, 1v1)
     */
    private Equipo[] formarEquipos(List<Usuario> jugadores) {
        Equipo equipoAzul = new Equipo("Team Azure");
        Equipo equipoRojo = new Equipo("Team Crimson");

        int mitad = jugadores.size() / 2;
        
        for (int i = 0; i < jugadores.size(); i++) {
            if (i < mitad) {
                equipoAzul.asignarJugador(jugadores.get(i));
            } else {
                equipoRojo.asignarJugador(jugadores.get(i));
            }
        }

        return new Equipo[]{equipoAzul, equipoRojo};
    }

    /**
     * Obtiene lista de roles asignados a jugadores
     */
    private List<String> obtenerRolesAsignados(List<Usuario> jugadores) {
        List<String> roles = new ArrayList<>();
        for (Usuario jugador : jugadores) {
            roles.add(jugador.getRol() != null ? jugador.getRol() : "Sin rol");
        }
        return roles;
    }

    /**
     * Ejecuta las transiciones de estado de la partida
     * NUEVA LÓGICA: Confirmación manual con sistema de sanciones
     * NUEVO: Envía email con estadísticas al finalizar
     * REFACTORIZADO: Recibe estadísticas ya generadas para evitar discrepancias
     * @return true si la partida se confirmó y completó, false si fue cancelada
     */
    private boolean ejecutarTransicionesEstado(Scrim scrim, ScrimContext context, 
                                           Usuario usuarioReal, List<Usuario> todosJugadores,
                                           List<Estadistica> estadisticas) {
        consoleView.mostrarSubtitulo("INICIANDO PARTIDA...");

        // Transición: Buscando → LobbyCompleto
        consoleView.delay(1000);
        context.cambiarEstado(new EstadoLobbyCompleto());
        gameView.mostrarEstadoActual(scrim.getEstado().getClass().getSimpleName());

        // NUEVA FASE: Confirmación Manual (solo usuario real)
        consoleView.delay(1000);
        boolean todosConfirmaron = procesarConfirmacionesJugadores(scrim, usuarioReal);
        
        if (!todosConfirmaron) {
            // Usuario rechazó o está baneado
            consoleView.mostrarError("❌ Partida cancelada");
            context.cancelar();
            return false;  // Retorna false para indicar cancelación
        }

        // Transición: LobbyCompleto → Confirmado (solo si confirmó)
        consoleView.delay(1000);
        context.cambiarEstado(new EstadoConfirmado());
        gameView.mostrarEstadoActual(scrim.getEstado().getClass().getSimpleName());

        // Transición: Confirmado → EnJuego
        consoleView.delay(1000);
        context.cambiarEstado(new EstadoEnJuego());
        consoleView.mostrarExito("¡Partida en curso! Estado: " + scrim.getEstado().getClass().getSimpleName());

        // Esperar finalización
        gameView.esperarFinalizacion();

        // Transición: EnJuego → Finalizado
        context.cambiarEstado(new EstadoFinalizado());
        gameView.mostrarFinPartida();
        gameView.mostrarEstadoActual(scrim.getEstado().getClass().getSimpleName());
        
        // NUEVO: Enviar email con estadísticas finales (MISMAS que se mostrarán en terminal)
        enviarEmailEstadisticasFinales(scrim, usuarioReal, todosJugadores, estadisticas);
        
        return true;  // Retorna true para indicar que la partida se completó
    }

    // ============================================
    // CONFIRMACIÓN MANUAL DE JUGADORES
    // ============================================

    /**
     * Procesa las confirmaciones manuales de todos los jugadores
     * NUEVO: Solo pregunta al usuario real, los bots confirman automáticamente
     * Si el usuario rechaza, es sancionado
     * 
     * @return true si todos confirmaron, false si usuario rechazó
     */
    private boolean procesarConfirmacionesJugadores(Scrim scrim, Usuario usuarioReal) {
        consoleView.mostrarSubtitulo("⏰ FASE DE CONFIRMACIÓN");
        consoleView.mostrarInfo("Debes confirmar tu participación en la partida");
        
        List<models.Postulacion> postulaciones = scrim.getPostulaciones();
        
        int confirmados = 0;
        int total = postulaciones.size();
        
        for (models.Postulacion postulacion : postulaciones) {
            Usuario jugador = postulacion.getUsuario();
            
            System.out.println("\n[" + (confirmados + 1) + "/" + total + "] " + jugador.getUsername());
            
            // Si el jugador ya está baneado, auto-rechazar
            if (jugador.estaBaneado()) {
                long minutosRestantes = jugador.getMinutosRestantesBan();
                consoleView.mostrarError("❌ " + jugador.getUsername() + 
                    " está baneado (quedan " + minutosRestantes + " minutos)");
                
                // Aplicar sanción al usuario real y cancelar
                if (jugador.getId() == usuarioReal.getId()) {
                    consoleView.mostrarError("⚠️ No puedes jugar mientras estés baneado");
                }
                return false;
            }
            
            // NUEVO: Solo preguntar al usuario real
            boolean confirma;
            if (jugador.getId() == usuarioReal.getId()) {
                // Usuario real - solicitar confirmación manual
                confirma = consoleView.solicitarConfirmacion(
                    "¿Confirmas tu participación? (s/n): "
                );
                
                if (!confirma) {
                    // Usuario rechazó - aplicar sanción
                    consoleView.mostrarError("❌ Has rechazado la partida");
                    usuarioReal.agregarSancion();
                    
                    System.out.println("\n⚠️ SANCIÓN APLICADA:");
                    consoleView.mostrarError("🚫 Sancionado (" + usuarioReal.getSancionesActivas() + " sanciones totales)");
                    consoleView.mostrarInfo("   Ban de " + usuarioReal.getMinutosRestantesBan() + " minutos");
                    System.out.println("\n💡 Los demás jugadores vuelven a la cola de matchmaking");
                    return false;
                }
            } else {
                // Bot - confirma automáticamente
                confirma = true;
                consoleView.delay(200); // Pequeña pausa para simular
            }
            
            if (confirma) {
                confirmados++;
                consoleView.mostrarExito("✅ " + jugador.getUsername() + " confirmó (" + confirmados + "/" + total + ")");
            }
        }
        
        // Todos confirmaron
        consoleView.mostrarExito("\n✅ ¡TODOS LOS JUGADORES CONFIRMARON! (" + confirmados + "/" + total + ")");
        return true;
    }

    /**
     * Envía email al usuario con estadísticas finales de la partida
     * REFACTORIZADO: Recibe las estadísticas ya generadas para evitar discrepancias
     */
    private void enviarEmailEstadisticasFinales(Scrim scrim, Usuario usuarioReal, 
                                               List<Usuario> todosJugadores, List<Estadistica> estadisticas) {
        
        // Encontrar estadística del usuario real
        Estadistica statUsuario = null;
        for (int i = 0; i < todosJugadores.size(); i++) {
            if (todosJugadores.get(i).getId() == usuarioReal.getId()) {
                statUsuario = estadisticas.get(i);
                break;
            }
        }
        
        if (statUsuario == null) return;
        
        // Encontrar MVP
        Estadistica mvpStat = estadisticas.stream()
            .max((a, b) -> Double.compare(a.getKda(), b.getKda()))
            .orElse(estadisticas.get(0));
        
        // Calcular resultado (equipos divididos por mitad)
        int mitad = todosJugadores.size() / 2;
        int killsEquipo1 = 0;
        int killsEquipo2 = 0;
        
        for (int i = 0; i < estadisticas.size(); i++) {
            if (i < mitad) {
                killsEquipo1 += estadisticas.get(i).getKills();
            } else {
                killsEquipo2 += estadisticas.get(i).getKills();
            }
        }
        
        boolean usuarioGano = (todosJugadores.indexOf(usuarioReal) < mitad) ? 
                              (killsEquipo1 > killsEquipo2) : (killsEquipo2 > killsEquipo1);
        String resultado = usuarioGano ? "VICTORIA" : "DERROTA";
        
        // Construir mensaje del email
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("¡Tu partida de ").append(scrim.getJuego()).append(" ha finalizado!\n\n");
        mensaje.append("═══════════════════════════════════════════\n");
        mensaje.append("📊 RESULTADO: ").append(resultado).append("\n");
        mensaje.append("═══════════════════════════════════════════\n\n");
        
        mensaje.append("🎯 TUS ESTADÍSTICAS:\n");
        mensaje.append("├─ Kills: ").append(statUsuario.getKills()).append("\n");
        mensaje.append("├─ Deaths: ").append(statUsuario.getDeaths()).append("\n");
        mensaje.append("├─ Assists: ").append(statUsuario.getAssists()).append("\n");
        mensaje.append("├─ KDA: ").append(String.format("%.2f", statUsuario.getKda())).append("\n");
        mensaje.append("└─ Rendimiento: ").append(statUsuario.obtenerRendimiento()).append("\n\n");
        
        mensaje.append("🏆 MVP DE LA PARTIDA:\n");
        mensaje.append("└─ ").append(mvpStat.getUsuario().getUsername());
        mensaje.append(" (KDA: ").append(String.format("%.2f", mvpStat.getKda())).append(")\n\n");
        
        mensaje.append("📈 MARCADOR FINAL:\n");
        mensaje.append("├─ Equipo Azul: ").append(killsEquipo1).append(" kills\n");
        mensaje.append("└─ Equipo Rojo: ").append(killsEquipo2).append(" kills\n\n");
        
        mensaje.append("═══════════════════════════════════════════\n");
        mensaje.append("Gracias por jugar en eScrims!\n");
        mensaje.append("¡Nos vemos en la próxima partida! 🎮");
        
        // Crear y enviar notificación por email
        models.Notificacion notificacion = new models.Notificacion(
            models.Notificacion.TipoNotificacion.FINALIZADO,
            mensaje.toString(),
            usuarioReal
        );
        
        // Enviar email
        notifiers.EmailNotifier emailNotifier = new notifiers.EmailNotifier();
        emailNotifier.sendNotification(notificacion);
        
        consoleView.mostrarExito("\n📧 Email enviado con tus estadísticas finales a: " + usuarioReal.getEmail());
    }

    // ============================================
    // ESTADÍSTICAS
    // ============================================

    /**
     * Genera estadísticas aleatorias para todos los jugadores
     * IMPORTANTE: Solo se genera UNA VEZ para evitar discrepancias entre terminal y email
     */
    private List<Estadistica> generarEstadisticas(List<Usuario> jugadores, Scrim scrim) {
        Random random = new Random();
        List<Estadistica> estadisticas = new ArrayList<>();

        // Generar estadísticas para cada jugador
        for (Usuario jugador : jugadores) {
            int kills = 5 + random.nextInt(18);
            int deaths = 8 + random.nextInt(12);
            int assists = 3 + random.nextInt(15);

            Estadistica stat = new Estadistica(jugador, scrim, kills, deaths, assists);
            estadisticas.add(stat);
        }

        return estadisticas;
    }

    /**
     * Muestra estadísticas finales en terminal
     * REFACTORIZADO: Recibe las estadísticas ya generadas
     */
    private void mostrarEstadisticasFinales(List<Estadistica> estadisticas, Scrim scrim,
                                           Equipo equipoAzul, Equipo equipoRojo, List<Usuario> jugadores) {

        // Encontrar MVP
        Estadistica mvpStat = estadisticas.stream()
            .max((a, b) -> Double.compare(a.getKda(), b.getKda()))
            .orElse(estadisticas.get(0));

        // Mostrar estadísticas
        consoleView.mostrarEstadisticas(estadisticas, mvpStat.getUsuario());
        gameView.mostrarMVP(mvpStat.getUsuario(), mvpStat.obtenerRendimiento());

        // Calcular ganador
        int killsAzul = calcularKillsEquipo(estadisticas, equipoAzul, jugadores);
        int killsRojo = calcularKillsEquipo(estadisticas, equipoRojo, jugadores);

        String ganador = killsAzul > killsRojo ? equipoAzul.getLado() : equipoRojo.getLado();
        gameView.mostrarResultadoFinal(ganador, killsAzul, killsRojo);
    }

    /**
     * Calcula total de kills de un equipo
     */
    private int calcularKillsEquipo(List<Estadistica> estadisticas, Equipo equipo, List<Usuario> todosJugadores) {
        int totalKills = 0;
        for (Usuario jugador : equipo.getJugadores()) {
            int indice = todosJugadores.indexOf(jugador);
            if (indice >= 0 && indice < estadisticas.size()) {
                totalKills += estadisticas.get(indice).getKills();
            }
        }
        return totalKills;
    }

    // ============================================
    // GESTIÓN DE ROLES CON COMMAND PATTERN
    // ============================================

    /**
     * Maneja gestión de roles con patrón Command
     */
    public void gestionarRolesConComandos(Usuario usuarioActual, List<Usuario> jugadores,
                                         List<String> rolesAsignados, ScrimContext context) {

        // Asignar roles guardados a usuarios
        for (int i = 0; i < jugadores.size(); i++) {
            jugadores.get(i).setRol(rolesAsignados.get(i));
        }

        // Crear command manager
        CommandManager commandManager = new CommandManager(context);

        boolean gestionando = true;
        while (gestionando) {
            int opcion = menuView.mostrarMenuGestionRoles();

            switch (opcion) {
                case 1:
                    cambiarRolJugador(jugadores, commandManager);
                    break;
                case 2:
                    intercambiarRoles(jugadores, commandManager);
                    break;
                case 3:
                    commandManager.deshacerUltimo();
                    menuView.mostrarRolesActuales(jugadores);
                    break;
                case 4:
                    consoleView.mostrarExito("Roles finalizados. Continuando a confirmación...");
                    gestionando = false;
                    break;
            }
        }

        // Actualizar roles asignados
        for (int i = 0; i < jugadores.size(); i++) {
            rolesAsignados.set(i, jugadores.get(i).getRol());
        }
    }

    /**
     * Cambia el rol de un jugador usando Command pattern
     */
    private void cambiarRolJugador(List<Usuario> jugadores, CommandManager commandManager) {
        int indice = menuView.seleccionarJugador(jugadores, "Jugadores disponibles:");

        consoleView.mostrarInfo("Roles disponibles:");
        String[] roles = menuView.getRolesDisponibles("League of Legends"); // Default
        for (int i = 0; i < roles.length; i++) {
            System.out.println("  [" + (i + 1) + "] " + roles[i]);
        }

        int rolIndice = consoleView.solicitarNumero("Selecciona nuevo rol", 1, roles.length) - 1;

        // Crear y ejecutar comando
        AsignarRolCommand comando = new AsignarRolCommand(jugadores.get(indice), roles[rolIndice]);
        commandManager.ejecutarComando(comando);
        menuView.mostrarRolesActuales(jugadores);
    }

    /**
     * Intercambia roles entre dos jugadores usando Command pattern
     */
    private void intercambiarRoles(List<Usuario> jugadores, CommandManager commandManager) {
        int indice1 = menuView.seleccionarJugador(jugadores, "Primer jugador:");
        int indice2 = menuView.seleccionarJugador(jugadores, "Segundo jugador:");

        if (indice1 != indice2) {
            SwapJugadoresCommand comando = new SwapJugadoresCommand(
                jugadores.get(indice1),
                jugadores.get(indice2)
            );
            commandManager.ejecutarComando(comando);
            menuView.mostrarRolesActuales(jugadores);
        } else {
            consoleView.mostrarError("No puedes intercambiar roles con el mismo jugador");
        }
    }
}
