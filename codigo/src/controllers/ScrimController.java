package controllers;

import models.*;
import states.*;
import views.*;
import service.SalaManager;
import service.ScrimSearchService;
import context.ScrimContext;

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

        // Crear contexto y postular
        ScrimContext context = new ScrimContext(scrim, scrim.getEstado());
        context.postular(usuario, rolSeleccionado);

        consoleView.mostrarExito("Rol seleccionado: " + rolSeleccionado);
        consoleView.mostrarInfo("Esperando a que se completen los cupos...");

        // Simular otros jugadores uniéndose
        simularJugadoresUniendo(context, scrim, juego, 7);

        // Continuar con flujo de juego
        ejecutarFlujoLobby(context, scrim);
    }

    /**
     * Simula jugadores uniéndose a un scrim
     */
    private void simularJugadoresUniendo(ScrimContext context, Scrim scrim, String juego, int cantidad) {
        gameView.mostrarOtrosUniendose();

        Random random = new Random();
        String[] nombresBot = {"Alpha", "Beta", "Gamma", "Delta", "Epsilon", "Zeta", "Eta", "Theta"};
        String[] rolesDisponibles = menuView.getRolesDisponibles(juego);

        for (int i = 0; i < cantidad && i < nombresBot.length; i++) {
            consoleView.delay(600);

            int rangoBot = scrim.getRangoMin() +
                          random.nextInt(scrim.getRangoMax() - scrim.getRangoMin() + 1);

            Usuario bot = new Usuario(i + 200, nombresBot[i] + random.nextInt(100),
                                     "bot" + (i+1) + "@escrims.com");
            bot.getRangoPorJuego().put(juego, rangoBot);

            String rolBot = rolesDisponibles[random.nextInt(rolesDisponibles.length)];
            context.postular(bot, rolBot);

            gameView.mostrarJugadorUnido(bot.getUsername(), rangoBot);
        }
    }

    /**
     * Ejecuta flujo de lobby completo → confirmado → en juego → finalizado
     */
    private void ejecutarFlujoLobby(ScrimContext context, Scrim scrim) {
        consoleView.mostrarExito("¡Sala completa! Iniciando partida...");

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
        gameView.mostrarEstadoActual("¡Partida en curso! " + scrim.getEstado().getClass().getSimpleName());

        // Esperar finalización
        gameView.esperarFinalizacion();

        // Transición: EnJuego → Finalizado
        context.cambiarEstado(new EstadoFinalizado());
        gameView.mostrarFinPartida();
        gameView.mostrarEstadoActual(scrim.getEstado().getClass().getSimpleName());
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
