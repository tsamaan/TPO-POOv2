package controllers;

import models.*;
import states.*;
import views.*;
import service.SalaManager;
import service.ScrimSearchService;
import context.ScrimContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * CAPA CONTROLLER (MVC) - Gestión de Scrims
 *
 * Responsabilidades:
 * - Crear scrims
 * - Buscar scrims disponibles
 * - Postularse a scrims
 * - Gestionar salas y lobby
 *
 * @pattern MVC - Controller Layer
 */
public class ScrimController {

    private ConsoleView consoleView;
    private MenuView menuView;
    private GameView gameView;
    private SalaManager salaManager;
    private ScrimSearchService searchService;

    public ScrimController(ConsoleView consoleView, MenuView menuView, GameView gameView) {
        this.consoleView = consoleView;
        this.menuView = menuView;
        this.gameView = gameView;
        this.salaManager = SalaManager.getInstance();
        this.searchService = new ScrimSearchService();
    }

    // ============================================
    // CREACIÓN DE SCRIMS
    // ============================================

    /**
     * Crea un nuevo scrim con los parámetros especificados
     */
    public Scrim crearScrim(String juego, String formato, String region, String modalidad,
                           int rangoMin, int rangoMax, int latenciaMax) {

        ScrimState estadoInicial = new EstadoBuscandoJugadores();

        Scrim scrim = new Scrim.Builder(estadoInicial)
            .juego(juego)
            .formato(formato)
            .region(region)
            .modalidad(modalidad)
            .rangoMin(rangoMin)
            .rangoMax(rangoMax)
            .latenciaMax(latenciaMax)
            .build();

        gameView.mostrarSalaCreada(scrim);

        return scrim;
    }

    /**
     * Crea un scrim automático basado en el rango del usuario
     */
    public Scrim crearScrimAutomatico(String juego, String formato, int rangoUsuario) {
        // Calcular rango aceptable (±200 puntos)
        int rangoMin = Math.max(0, rangoUsuario - 200);
        int rangoMax = Math.min(3000, rangoUsuario + 200);

        gameView.mostrarCreandoSala(rangoUsuario);

        return crearScrim(juego, formato, "SA", "ranked", rangoMin, rangoMax, 80);
    }

    // ============================================
    // BÚSQUEDA DE SCRIMS
    // ============================================

    /**
     * Busca scrims disponibles con criterios
     */
    public List<Scrim> buscarScrims(String juego, String rangoMin, String rangoMax,
                                   String region, String formato) {

        return searchService.buscarScrims(juego, formato, rangoMin, rangoMax, region);
    }

    /**
     * Busca scrims por juego desde SalaManager
     */
    public List<Scrim> buscarScrimsPorJuego(String juego) {
        return salaManager.getSalasPorJuego(juego);
    }

    /**
     * Maneja el flujo completo de búsqueda de salas
     */
    public void buscarSalasDisponibles(Usuario usuario, UserController userController) {
        consoleView.mostrarTitulo("BUSCAR SALAS DISPONIBLES");

        // Seleccionar juego
        List<String> juegosDisponibles = salaManager.getJuegosDisponibles();
        String juegoSeleccionado = menuView.seleccionarJuegoDesdeList(juegosDisponibles);

        // Configurar rango
        int rangoUsuario = userController.configurarRango(usuario, juegoSeleccionado);

        // Obtener salas
        List<Scrim> salas = buscarScrimsPorJuego(juegoSeleccionado);

        if (salas.isEmpty()) {
            consoleView.mostrarAdvertencia("No hay salas disponibles para " + juegoSeleccionado);
            return;
        }

        // Mostrar salas
        consoleView.mostrarSubtitulo("SALAS DISPONIBLES PARA " + juegoSeleccionado.toUpperCase());
        consoleView.mostrarListaScrims(salas, usuario);

        // Seleccionar sala
        int indiceSala = menuView.seleccionarSala(salas.size());

        if (indiceSala == 0) {
            menuView.mostrarBusquedaCancelada();
            return;
        }

        Scrim salaSeleccionada = salas.get(indiceSala - 1);

        // Validar acceso
        if (!salaManager.puedeUnirse(usuario, salaSeleccionada)) {
            menuView.mostrarAccesoDenegado(rangoUsuario,
                                          salaSeleccionada.getRangoMin(),
                                          salaSeleccionada.getRangoMax());
            return;
        }

        // Unirse a la sala
        unirseASala(usuario, salaSeleccionada, juegoSeleccionado, userController);
    }

    /**
     * Maneja el proceso de unirse a una sala
     */
    private void unirseASala(Usuario usuario, Scrim scrim, String juego, UserController userController) {
        menuView.mostrarAccesoConcedido(scrim.getId().toString());

        // Seleccionar rol
        String rolSeleccionado = userController.seleccionarRol(juego);
        
        // ASIGNAR ROL AL USUARIO (FIX: usuario sin rol)
        usuario.setRol(rolSeleccionado);

        // Crear contexto y postular
        ScrimContext context = new ScrimContext(scrim, scrim.getEstado());
        context.postular(usuario, rolSeleccionado);

        consoleView.mostrarExito("Rol seleccionado: " + rolSeleccionado);
        consoleView.mostrarInfo("Esperando a que se completen los cupos...");

        // Calcular cuántos jugadores adicionales se necesitan
        int jugadoresTotales = scrim.getCuposMaximos(); // 5v5 = 10, 1v1 = 2, etc.
        int jugadoresActuales = scrim.getPostulaciones().size(); // Ya está el usuario
        int jugadoresNecesarios = jugadoresTotales - jugadoresActuales;
        
        // DEBUG: Mostrar información de cálculo
        consoleView.mostrarInfo(String.format("[DEBUG] Cupos máximos: %d | Jugadores actuales: %d | Jugadores a simular: %d",
            jugadoresTotales, jugadoresActuales, jugadoresNecesarios));
        
        // Simular otros jugadores uniéndose
        simularJugadoresUniendo(context, scrim, juego, jugadoresNecesarios);

        // Continuar con flujo de juego
        ejecutarFlujoLobby(context, scrim, usuario);
    }

    /**
     * Simula jugadores uniéndose a un scrim
     * ACTUALIZADO: Asigna roles únicos por equipo para LoL/Valorant
     */
    private void simularJugadoresUniendo(ScrimContext context, Scrim scrim, String juego, int cantidad) {
        gameView.mostrarOtrosUniendose();

        Random random = new Random();
        String[] nombresBot = {"Alpha", "Beta", "Gamma", "Delta", "Epsilon", "Zeta", "Eta", "Theta", "Omega"};
        String[] rolesDisponibles = menuView.getRolesDisponibles(juego);

        // Para LoL/Valorant: rastrear roles únicos por equipo
        List<String> rolesEquipo1 = new ArrayList<>();
        List<String> rolesEquipo2 = new ArrayList<>();
        
        // El usuario ya está en postulaciones, obtener su rol para el primer equipo
        List<models.Postulacion> postulacionesActuales = scrim.getPostulaciones();
        if (!postulacionesActuales.isEmpty()) {
            Usuario usuarioPrimero = postulacionesActuales.get(0).getUsuario();
            if (usuarioPrimero.getRol() != null) {
                rolesEquipo1.add(usuarioPrimero.getRol());
            }
        }

        int jugadoresTotales = scrim.getCuposMaximos();
        int jugadoresPorEquipo = jugadoresTotales / 2;

        for (int i = 0; i < cantidad && i < nombresBot.length; i++) {
            consoleView.delay(600);

            int rangoBot = scrim.getRangoMin() +
                          random.nextInt(scrim.getRangoMax() - scrim.getRangoMin() + 1);

            Usuario bot = new Usuario(i + 200, nombresBot[i] + random.nextInt(100),
                                     "bot" + (i+1) + "@escrims.com");
            bot.getRangoPorJuego().put(juego, rangoBot);

            // Asignar rol según el juego
            String rolBot;
            if (esJuegoConRolesUnicos(juego)) {
                // Para LoL/Valorant: asignar roles únicos por equipo
                int jugadoresEnEquipo1 = rolesEquipo1.size();
                int jugadoresEnEquipo2 = rolesEquipo2.size();

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
            context.postular(bot, rolBot);

            gameView.mostrarJugadorUnido(bot.getUsername(), rangoBot);
        }
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

    /**
     * Ejecuta flujo de lobby completo → confirmado → en juego → finalizado
     * Incluye confirmaciones de jugadores y emails de notificación
     */
    private void ejecutarFlujoLobby(ScrimContext context, Scrim scrim, Usuario usuarioActual) {
        consoleView.mostrarExito("¡Sala completa! Iniciando partida...");

        // Obtener todos los jugadores
        List<Usuario> todosJugadores = new ArrayList<>();
        for (models.Postulacion post : scrim.getPostulaciones()) {
            todosJugadores.add(post.getUsuario());
        }

        // Transición: Buscando → LobbyCompleto
        consoleView.delay(1000);
        context.cambiarEstado(new EstadoLobbyCompleto());
        gameView.mostrarEstadoActual(scrim.getEstado().getClass().getSimpleName());

        // FASE DE CONFIRMACIÓN
        consoleView.delay(1000);
        consoleView.mostrarInfo("\n───────────────────────────────────────────────────────────────────────────────");
        consoleView.mostrarInfo("[!] ⚡ FASE DE CONFIRMACIÓN");
        consoleView.mostrarInfo("───────────────────────────────────────────────────────────────────────────────");
        consoleView.mostrarInfo("Debes confirmar tu participación en la partida\n");

        // Procesar confirmaciones (solo pregunta al usuario real)
        boolean todosConfirmaron = procesarConfirmacionesJugadores(scrim, usuarioActual);
        
        if (!todosConfirmaron) {
            consoleView.mostrarError("Partida cancelada - Un jugador rechazó la confirmación");
            context.cambiarEstado(new EstadoCancelado());
            return;
        }

        // Transición: LobbyCompleto → Confirmado
        context.cambiarEstado(new EstadoConfirmado());
        gameView.mostrarEstadoActual(scrim.getEstado().getClass().getSimpleName());

        // FORMAR Y MOSTRAR EQUIPOS (después de confirmación)
        consoleView.delay(1000);
        consoleView.mostrarSubtitulo("FORMANDO EQUIPOS");
        Equipo[] equipos = formarEquipos(todosJugadores);
        Equipo equipoAzul = equipos[0];
        Equipo equipoRojo = equipos[1];
        List<String> rolesAsignados = obtenerRolesAsignados(todosJugadores);
        consoleView.mostrarEquipos(equipoAzul, equipoRojo, rolesAsignados, todosJugadores, usuarioActual);

        // Transición: Confirmado → EnJuego
        consoleView.delay(1000);
        context.cambiarEstado(new EstadoEnJuego());
        gameView.mostrarEstadoActual("¡Partida en curso! " + scrim.getEstado().getClass().getSimpleName());

        // Esperar finalización
        gameView.esperarFinalizacion();

        // Transición: EnJuego → Finalizado
        context.cambiarEstado(new EstadoFinalizado());
        gameView.mostrarFinPartida();
        gameView.mostrarEstadoActual(scrim.getEstado().getClass().getSimpleName());

        // Enviar email con estadísticas finales
        enviarEmailEstadisticasFinales(scrim, usuarioActual, todosJugadores);
        
        // LIMPIAR SALA para reutilización (FIX: salas acumulan jugadores)
        scrim.getPostulaciones().clear();
        context.cambiarEstado(new EstadoBuscandoJugadores());
    }

    /**
     * Procesa confirmaciones de jugadores (solo pregunta al usuario real)
     */
    private boolean procesarConfirmacionesJugadores(Scrim scrim, Usuario usuarioReal) {
        List<models.Postulacion> postulaciones = scrim.getPostulaciones();
        int confirmados = 0;
        int totalJugadores = postulaciones.size();

        for (models.Postulacion postulacion : postulaciones) {
            Usuario jugador = postulacion.getUsuario();
            boolean confirma;

            if (jugador.getId() == usuarioReal.getId()) {
                // Preguntar solo al usuario real
                consoleView.mostrarInfo("[" + (confirmados + 1) + "/" + totalJugadores + "] " + jugador.getUsername());
                confirma = consoleView.solicitarConfirmacion("¿Confirmas tu participación? (s/n): ");

                if (!confirma) {
                    consoleView.mostrarError("Has rechazado la partida");
                    usuarioReal.agregarSancion();
                    long minutosBan = usuarioReal.getMinutosRestantesBan();
                    consoleView.mostrarAdvertencia("SANCIÓN: Baneado por " + minutosBan + " minutos");
                    return false;
                }
            } else {
                // Bots auto-confirman
                confirma = true;
            }

            if (confirma) {
                confirmados++;
                consoleView.mostrarExito("✓ " + jugador.getUsername() + " confirmó (" + confirmados + "/" + totalJugadores + ")");
                consoleView.delay(300);
            }
        }

        consoleView.mostrarExito("\n✓ ¡TODOS LOS JUGADORES CONFIRMARON! (" + confirmados + "/" + totalJugadores + ")");
        return true;
    }

    /**
     * Forma dos equipos de forma equitativa
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
     * Muestra estadísticas en consola y envía email con estadísticas finales del match
     */
    private void enviarEmailEstadisticasFinales(Scrim scrim, Usuario usuarioReal, List<Usuario> todosJugadores) {
        Random random = new Random();
        
        // Generar estadísticas para todos los jugadores
        List<Estadistica> estadisticas = new ArrayList<>();
        for (Usuario jugador : todosJugadores) {
            int kills = 5 + random.nextInt(18);
            int deaths = 8 + random.nextInt(12);
            int assists = 3 + random.nextInt(15);
            Estadistica stat = new Estadistica(jugador, scrim, kills, deaths, assists);
            estadisticas.add(stat);
        }

        // Encontrar estadísticas del usuario real
        Estadistica statsUsuario = null;
        for (int i = 0; i < todosJugadores.size(); i++) {
            if (todosJugadores.get(i).getId() == usuarioReal.getId()) {
                statsUsuario = estadisticas.get(i);
                break;
            }
        }

        if (statsUsuario == null) return;

        // Encontrar MVP
        Estadistica mvp = estadisticas.stream()
            .max((a, b) -> Double.compare(a.getKda(), b.getKda()))
            .orElse(estadisticas.get(0));

        // MOSTRAR ESTADÍSTICAS EN CONSOLA
        consoleView.mostrarEstadisticas(estadisticas, mvp.getUsuario());
        gameView.mostrarMVP(mvp.getUsuario(), mvp.obtenerRendimiento());

        // Calcular marcador de equipos (asumiendo equipos balanceados)
        int killsEquipo1 = 0;
        int killsEquipo2 = 0;
        int mitad = todosJugadores.size() / 2;

        for (int i = 0; i < estadisticas.size(); i++) {
            if (i < mitad) {
                killsEquipo1 += estadisticas.get(i).getKills();
            } else {
                killsEquipo2 += estadisticas.get(i).getKills();
            }
        }

        // MOSTRAR RESULTADO EN CONSOLA
        String ganador = killsEquipo1 > killsEquipo2 ? "Team Azure" : "Team Crimson";
        gameView.mostrarResultadoFinal(ganador, killsEquipo1, killsEquipo2);

        // Determinar si el usuario ganó (está en el equipo con más kills)
        boolean usuarioEnEquipo1 = todosJugadores.indexOf(usuarioReal) < mitad;
        boolean gano = (usuarioEnEquipo1 && killsEquipo1 > killsEquipo2) ||
                      (!usuarioEnEquipo1 && killsEquipo2 > killsEquipo1);

        // Construir mensaje del email
        StringBuilder mensajeEmail = new StringBuilder();
        mensajeEmail.append("═══════════════════════════════════════════\n");
        mensajeEmail.append("📊 RESULTADO: ").append(gano ? "VICTORIA" : "DERROTA").append("\n");
        mensajeEmail.append("═══════════════════════════════════════════\n\n");

        mensajeEmail.append("🎯 TUS ESTADÍSTICAS:\n");
        mensajeEmail.append("├─ Kills: ").append(statsUsuario.getKills()).append("\n");
        mensajeEmail.append("├─ Deaths: ").append(statsUsuario.getDeaths()).append("\n");
        mensajeEmail.append("├─ Assists: ").append(statsUsuario.getAssists()).append("\n");
        mensajeEmail.append("├─ KDA: ").append(String.format("%.2f", statsUsuario.getKda())).append("\n");
        
        String rendimiento;
        double kda = statsUsuario.getKda();
        if (kda >= 3.0) rendimiento = "Excelente";
        else if (kda >= 2.0) rendimiento = "Muy Bueno";
        else if (kda >= 1.5) rendimiento = "Bueno";
        else if (kda >= 1.0) rendimiento = "Regular";
        else rendimiento = "Necesita Mejorar";
        
        mensajeEmail.append("└─ Rendimiento: ").append(rendimiento).append("\n\n");

        mensajeEmail.append("🏆 MVP: ").append(mvp.getUsuario().getUsername());
        mensajeEmail.append(" (KDA: ").append(String.format("%.2f", mvp.getKda())).append(")\n\n");

        mensajeEmail.append("📈 MARCADOR FINAL:\n");
        mensajeEmail.append("├─ Equipo Azul: ").append(killsEquipo1).append(" kills\n");
        mensajeEmail.append("└─ Equipo Rojo: ").append(killsEquipo2).append(" kills\n");

        // Crear y enviar notificación por email
        models.Notificacion notificacion = new models.Notificacion(
            models.Notificacion.TipoNotificacion.FINALIZADO,
            mensajeEmail.toString(),
            usuarioReal
        );
        
        // Enviar email
        notifiers.EmailNotifier emailNotifier = new notifiers.EmailNotifier();
        emailNotifier.sendNotification(notificacion);

        consoleView.mostrarExito("\n📧 Email enviado con tus estadísticas finales a: " + usuarioReal.getEmail());
    }

    // ============================================
    // POSTULACIÓN
    // ============================================

    /**
     * Postula un usuario a un scrim
     */
    public void postularse(Usuario usuario, Scrim scrim, String rol) {
        ScrimContext context = new ScrimContext(scrim, scrim.getEstado());
        context.postular(usuario, rol);
        consoleView.mostrarExito("Postulación registrada para " + usuario.getUsername());
    }

    // ============================================
    // CANCELACIÓN
    // ============================================

    /**
     * Cancela un scrim
     */
    public void cancelarScrim(Scrim scrim) {
        scrim.getEstado().cancelar(scrim);
        consoleView.mostrarInfo("Scrim cancelado");
    }
}
