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

        // Seleccionar juego
        String juegoSeleccionado = menuView.seleccionarJuego();
        String formato = "5v5"; // Default

        // Configurar rango
        int rangoUsuario = userController.configurarRango(usuario, juegoSeleccionado);

        // Seleccionar rol
        String rolSeleccionado = userController.seleccionarRol(juegoSeleccionado);

        // Inicializar sistema de notificaciones
        gameView.mostrarInicializandoNotificaciones();
        NotifierFactory factory = new SimpleNotifierFactory();
        INotifier emailNotifier = factory.createEmailNotifier();
        INotifier discordNotifier = factory.createDiscordNotifier();
        gameView.mostrarNotificacionesActivas();

        // Crear scrim automático
        System.out.println();
        Scrim scrim = scrimController.crearScrimAutomatico(juegoSeleccionado, formato, rangoUsuario);

        // Agregar notificadores (Observer pattern)
        scrim.addNotifier(emailNotifier);
        scrim.addNotifier(discordNotifier);

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
     */
    private List<Usuario> buscarJugadoresConMMR(Usuario usuarioActual, Scrim scrim,
                                                String juego, String rolUsuario) {
        List<Usuario> jugadores = new ArrayList<>();
        jugadores.add(usuarioActual);

        Random random = new Random();
        String[] nombresBot = {"Shadow", "Phoenix", "Ghost", "Ninja", "Hunter", "Viper", "Storm"};
        String[] rolesDisponibles = menuView.getRolesDisponibles(juego);

        int rangoUsuario = usuarioActual.getRangoPorJuego().get(juego);

        for (int i = 0; i < 7; i++) {
            int rangoBot = rangoUsuario + random.nextInt(300) - 150;
            rangoBot = Math.max(scrim.getRangoMin(), Math.min(scrim.getRangoMax(), rangoBot));

            Usuario bot = new Usuario(i + 100, nombresBot[i] + random.nextInt(100),
                                     "bot" + (i+1) + "@escrims.com");
            bot.getRangoPorJuego().put(juego, rangoBot);

            String rolBot = rolesDisponibles[random.nextInt(rolesDisponibles.length)];
            bot.setRol(rolBot);

            ScrimContext tempContext = new ScrimContext(scrim, scrim.getEstado());
            tempContext.postular(bot, rolBot);

            jugadores.add(bot);

            gameView.mostrarJugadorEncontrado(bot.getUsername(), rangoBot, i + 2, 8);
        }

        return jugadores;
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

        // Mostrar equipos
        consoleView.mostrarSubtitulo("FORMANDO EQUIPOS");
        consoleView.mostrarEquipos(equipoAzul, equipoRojo, rolesAsignados, jugadores, usuarioActual);

        // Transiciones de estado
        ejecutarTransicionesEstado(scrim, context);

        // Generar y mostrar estadísticas
        mostrarEstadisticasFinales(jugadores, scrim, equipoAzul, equipoRojo);

        gameView.mostrarVolviendoMenu();
    }

    /**
     * Forma dos equipos de forma equitativa
     */
    private Equipo[] formarEquipos(List<Usuario> jugadores) {
        Equipo equipoAzul = new Equipo("Team Azure");
        Equipo equipoRojo = new Equipo("Team Crimson");

        for (int i = 0; i < jugadores.size(); i++) {
            if (i < 4) {
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
     */
    private void ejecutarTransicionesEstado(Scrim scrim, ScrimContext context) {
        consoleView.mostrarSubtitulo("INICIANDO PARTIDA...");

        // Transición: Buscando → LobbyCompleto
        consoleView.delay(1000);
        context.cambiarEstado(new EstadoLobbyCompleto());
        gameView.mostrarEstadoActual(scrim.getEstado().getClass().getSimpleName());

        // Transición: LobbyCompleto → Confirmado
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
    }

    // ============================================
    // ESTADÍSTICAS
    // ============================================

    /**
     * Genera y muestra estadísticas finales
     */
    private void mostrarEstadisticasFinales(List<Usuario> jugadores, Scrim scrim,
                                           Equipo equipoAzul, Equipo equipoRojo) {

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
