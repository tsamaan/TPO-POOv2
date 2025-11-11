package views;

import models.*;
import java.util.List;

/**
 * CAPA VIEW (MVC) - Vistas de gameplay
 *
 * Responsable de mostrar información específica del juego:
 * - Progreso de matchmaking
 * - Estado de partidas
 * - Transiciones de estado
 *
 * @pattern MVC - View Layer (Game Specialist)
 */
public class GameView {

    private ConsoleView consoleView;

    public GameView(ConsoleView consoleView) {
        this.consoleView = consoleView;
    }

    // ============================================
    // MATCHMAKING
    // ============================================

    /**
     * Muestra inicio de matchmaking
     */
    public void mostrarInicioMatchmaking(String juego, String rol, int rangoUsuario, int rangoMin, int rangoMax) {
        consoleView.mostrarSubtitulo("BUSCANDO JUGADORES CON RANGO SIMILAR...");

        consoleView.mostrarExito("¡En cola de matchmaking!");
        consoleView.mostrarInfo("Juego: " + juego);
        consoleView.mostrarInfo("Rol: " + rol);
        consoleView.mostrarInfo("Tu rango: " + rangoUsuario);
        consoleView.mostrarInfo("Rango permitido: " + rangoMin + " - " + rangoMax);
        System.out.println();
    }

    /**
     * Muestra jugador encontrado en matchmaking
     */
    public void mostrarJugadorEncontrado(String username, int rango, int actual, int total) {
        consoleView.mostrarExito("[" + actual + "/" + total + "] Jugador encontrado: " + username + " (Rango: " + rango + ")");
        consoleView.delay(400);
    }

    /**
     * Muestra match encontrado
     */
    public void mostrarMatchEncontrado(int jugadoresEmparejados) {
        System.out.println();
        consoleView.mostrarExito("¡MATCH ENCONTRADO!");
        consoleView.mostrarInfo("Jugadores emparejados: " + jugadoresEmparejados);
    }

    // ============================================
    // ESTADOS DEL SCRIM
    // ============================================

    /**
     * Muestra transición de estado
     */
    public void mostrarTransicionEstado(String estadoAnterior, String estadoNuevo) {
        consoleView.mostrarInfo("Transición de estado: " + estadoAnterior + " → " + estadoNuevo);
    }

    /**
     * Muestra estado actual del scrim
     */
    public void mostrarEstadoActual(String estado) {
        consoleView.mostrarExito("Estado actual: " + estado);
    }

    /**
     * Muestra inicio de partida
     */
    public void mostrarInicioPartida() {
        consoleView.mostrarSubtitulo("INICIANDO PARTIDA...");
        consoleView.delay(1000);
    }

    /**
     * Muestra partida en curso
     */
    public void mostrarPartidaEnCurso() {
        consoleView.mostrarInfo("La partida está en curso...");
        consoleView.mostrarInfo("Duración estimada: 30-90 minutos");
    }

    /**
     * Muestra fin de partida
     */
    public void mostrarFinPartida() {
        consoleView.mostrarExito("Partida finalizada. ¡GG!");
    }

    // ============================================
    // SALAS Y LOBBY
    // ============================================

    /**
     * Muestra información de sala creada
     */
    public void mostrarSalaCreada(Scrim scrim) {
        consoleView.mostrarExito("Sala creada - Estado: " + scrim.getEstado().getClass().getSimpleName());
        consoleView.mostrarInfo("Rango permitido: " + scrim.getRangoMin() + " - " + scrim.getRangoMax());
    }

    /**
     * Muestra lobby completo
     */
    public void mostrarLobbyCompleto() {
        consoleView.mostrarSubtitulo("¡LOBBY COMPLETO!");
        consoleView.mostrarExito("¡Sala completa! Iniciando partida...");
    }

    /**
     * Muestra jugadores uniéndose a sala
     */
    public void mostrarJugadorUnido(String username, int rango) {
        consoleView.mostrarExito(username + " se ha unido (Rango: " + rango + ")");
    }

    /**
     * Muestra otros jugadores uniéndose
     */
    public void mostrarOtrosUniendose() {
        consoleView.mostrarInfo("Otros jugadores se están uniendo...");
    }

    // ============================================
    // CONFIRMACIONES
    // ============================================

    /**
     * Muestra fase de confirmación
     */
    public void mostrarFaseConfirmacion() {
        consoleView.mostrarSubtitulo("FASE DE CONFIRMACIÓN");
        consoleView.mostrarInfo("Los jugadores están confirmando su participación...\n");
    }

    /**
     * Muestra confirmación de jugador
     */
    public void mostrarConfirmacionJugador(String username, boolean confirmado) {
        String estado = confirmado ? "CONFIRMADO" : "RECHAZADO";
        System.out.println("   [" + (confirmado ? "+" : "-") + "] " + username + ": " + estado);
    }

    /**
     * Muestra todos confirmados
     */
    public void mostrarTodosConfirmados() {
        consoleView.mostrarExito("¡Todos los jugadores confirmaron! Iniciando partida...");
    }

    // ============================================
    // RESULTADOS Y ESTADÍSTICAS
    // ============================================

    /**
     * Muestra resultado final del partido
     */
    public void mostrarResultadoFinal(String equipoGanador, int killsAzul, int killsRojo) {
        consoleView.mostrarSubtitulo("RESULTADO FINAL");
        System.out.println("    Team Azure: " + killsAzul + " kills");
        System.out.println("    Team Crimson: " + killsRojo + " kills");
        System.out.println("\n[★] GANADOR: " + equipoGanador);
    }

    /**
     * Muestra MVP
     */
    public void mostrarMVP(Usuario mvp, String rendimiento) {
        System.out.println("\n[★] MVP: " + mvp.getUsername());
        System.out.println("    " + rendimiento);
    }

    // ============================================
    // RANGO Y CONFIGURACIÓN
    // ============================================

    /**
     * Solicita configuración de rango para un juego
     */
    public int solicitarRango(String juego) {
        consoleView.mostrarInfo("No tienes rango configurado para " + juego);
        return consoleView.solicitarNumero("Ingresa tu rango", 0, 3000);
    }

    /**
     * Muestra rango configurado
     */
    public void mostrarRangoConfigurado(String juego, int rango) {
        consoleView.mostrarExito("Rango configurado para " + juego + ": " + rango);
    }

    /**
     * Muestra sala siendo creada
     */
    public void mostrarCreandoSala(int rangoUsuario) {
        consoleView.mostrarInfo("Creando sala automática basada en tu rango (" + rangoUsuario + ")...");
    }

    // ============================================
    // SISTEMA DE NOTIFICACIONES
    // ============================================

    /**
     * Muestra sistema de notificaciones inicializándose
     */
    public void mostrarInicializandoNotificaciones() {
        consoleView.mostrarInfo("Inicializando sistema de notificaciones...");
    }

    /**
     * Muestra sistema de notificaciones activo
     */
    public void mostrarNotificacionesActivas() {
        consoleView.mostrarExito("Sistema de notificaciones activo");
    }

    // ============================================
    // UTILIDADES
    // ============================================

    /**
     * Muestra esperando entrada del usuario
     */
    public void esperarFinalizacion() {
        consoleView.mostrarInfo("Presiona ENTER para finalizar la partida...");
        consoleView.getScanner().nextLine();
    }

    /**
     * Muestra volviendo al menú
     */
    public void mostrarVolviendoMenu() {
        consoleView.mostrarExito("Volviendo al menú principal...");
    }

    /**
     * Muestra mensaje de error con contexto de juego
     */
    public void mostrarErrorJuego(String mensaje) {
        consoleView.mostrarError("Error en el juego: " + mensaje);
    }
}
