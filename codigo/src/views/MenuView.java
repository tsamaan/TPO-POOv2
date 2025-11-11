package views;

import models.Usuario;
import java.util.List;
import java.util.Map;

/**
 * CAPA VIEW (MVC) - Menús y opciones
 *
 * Responsable de mostrar menús y capturar selecciones
 * Usa ConsoleView para presentación
 *
 * @pattern MVC - View Layer (Menu Specialist)
 */
public class MenuView {

    private ConsoleView consoleView;

    // Roles específicos por juego
    private static final Map<String, String[]> ROLES_POR_JUEGO = Map.of(
        "Valorant", new String[]{"Duelist", "Controller", "Initiator", "Sentinel"},
        "League of Legends", new String[]{"Top", "Jungle", "Mid", "ADC", "Support"},
        "CS:GO", new String[]{"Entry Fragger", "AWPer", "Support", "Lurker", "IGL"}
    );

    private static final String[] ROLES_DEFAULT = {"Top", "Jungle", "Mid", "ADC", "Support"};

    public MenuView(ConsoleView consoleView) {
        this.consoleView = consoleView;
    }

    // ============================================
    // MENU PRINCIPAL
    // ============================================

    /**
     * Muestra el menú principal y retorna la opción seleccionada
     */
    public int mostrarMenuPrincipal(Usuario usuario) {
        consoleView.mostrarSubtitulo("MENU PRINCIPAL - " + usuario.getUsername());

        System.out.println("[1] Juego Rápido (Matchmaking automático)");
        System.out.println("[2] Buscar Salas Disponibles");
        System.out.println("[3] Ver Mi Perfil");
        System.out.println("[4] Editar Perfil");
        System.out.println("[5] Salir");

        return consoleView.solicitarNumero("Selecciona una opción", 1, 5);
    }

    // ============================================
    // SELECCIÓN DE JUEGO
    // ============================================

    /**
     * Muestra menú de selección de juego
     */
    public String seleccionarJuego() {
        consoleView.mostrarInfo("¿Qué juego quieres jugar?");
        System.out.println("  [1] Valorant");
        System.out.println("  [2] League of Legends");
        System.out.println("  [3] CS:GO");

        int opcion = consoleView.solicitarNumero("Selecciona tu juego", 1, 3);

        switch (opcion) {
            case 1: return "Valorant";
            case 2: return "League of Legends";
            case 3: return "CS:GO";
            default:
                consoleView.mostrarAdvertencia("Opción inválida. Seleccionando Valorant por defecto.");
                return "Valorant";
        }
    }

    /**
     * Muestra lista de juegos disponibles y retorna selección
     */
    public String seleccionarJuegoDesdeList(List<String> juegosDisponibles) {
        consoleView.mostrarInfo("Selecciona un juego:");
        for (int i = 0; i < juegosDisponibles.size(); i++) {
            System.out.println("  [" + (i + 1) + "] " + juegosDisponibles.get(i));
        }

        int indice = consoleView.solicitarNumero("Selecciona tu opción", 1, juegosDisponibles.size()) - 1;
        return juegosDisponibles.get(indice);
    }

    // ============================================
    // SELECCIÓN DE ROL
    // ============================================

    /**
     * Muestra menú de selección de rol según el juego
     */
    public String seleccionarRol(String juego) {
        String[] rolesDisponibles = ROLES_POR_JUEGO.getOrDefault(juego, ROLES_DEFAULT);

        consoleView.mostrarInfo("Selecciona tu rol preferido (" + juego + "):");
        System.out.println();

        for (int i = 0; i < rolesDisponibles.length; i++) {
            System.out.println("[" + (i + 1) + "] " + rolesDisponibles[i]);
        }

        int seleccion = consoleView.solicitarNumero("Ingresa el número de tu rol", 1, rolesDisponibles.length);
        return rolesDisponibles[seleccion - 1];
    }

    /**
     * Obtiene array de roles disponibles para un juego
     */
    public String[] getRolesDisponibles(String juego) {
        return ROLES_POR_JUEGO.getOrDefault(juego, ROLES_DEFAULT);
    }

    // ============================================
    // GESTIÓN DE ROLES (COMMAND PATTERN)
    // ============================================

    /**
     * Muestra menú de gestión de roles
     */
    public int mostrarMenuGestionRoles() {
        consoleView.mostrarSubtitulo("GESTIÓN DE ROLES (Patrón COMMAND)");

        consoleView.mostrarInfo("Como organizador, puedes ajustar los roles antes de confirmar.");
        consoleView.mostrarInfo("Los cambios se pueden deshacer con UNDO.\n");

        System.out.println("[1] Cambiar rol de un jugador");
        System.out.println("[2] Intercambiar roles entre dos jugadores");
        System.out.println("[3] Deshacer último cambio");
        System.out.println("[4] Continuar a confirmación");

        return consoleView.solicitarNumero("Selecciona una opción", 1, 4);
    }

    /**
     * Muestra lista de jugadores para selección
     */
    public int seleccionarJugador(List<Usuario> jugadores, String prompt) {
        consoleView.mostrarInfo(prompt);
        for (int i = 0; i < jugadores.size(); i++) {
            Usuario j = jugadores.get(i);
            System.out.println("  [" + (i + 1) + "] " + j.getUsername() +
                             (j.getRol() != null ? " - Rol actual: " + j.getRol() : ""));
        }

        return consoleView.solicitarNumero("Selecciona jugador", 1, jugadores.size()) - 1;
    }

    /**
     * Muestra roles actuales de todos los jugadores
     */
    public void mostrarRolesActuales(List<Usuario> jugadores) {
        consoleView.mostrarInfo("Roles actuales:");
        for (Usuario j : jugadores) {
            System.out.println("  • " + j.getUsername() + ": " + j.getRol());
        }
    }

    // ============================================
    // CONFIRMACIONES
    // ============================================

    /**
     * Muestra progreso de confirmaciones
     */
    public void mostrarProgresoConfirmaciones(int confirmados, int total) {
        consoleView.mostrarProgreso("Confirmaciones", confirmados, total);
    }

    // ============================================
    // SELECCIÓN DE SALA
    // ============================================

    /**
     * Solicita selección de sala de una lista
     */
    public int seleccionarSala(int totalSalas) {
        return consoleView.solicitarNumero("Selecciona una sala para unirte (0 para cancelar)", 0, totalSalas);
    }

    // ============================================
    // MENSAJES DE ESTADO
    // ============================================

    /**
     * Muestra mensaje de acceso denegado
     */
    public void mostrarAccesoDenegado(int rangoUsuario, int rangoMin, int rangoMax) {
        consoleView.mostrarSubtitulo("ACCESO DENEGADO");
        consoleView.mostrarError("No puedes unirte a esta sala.");
        consoleView.mostrarError("Tu rango: " + rangoUsuario);
        consoleView.mostrarError("Rango requerido: " + rangoMin + " - " + rangoMax);
        consoleView.mostrarInfo("Mejora tu rango o busca otra sala.");
    }

    /**
     * Muestra mensaje de acceso concedido
     */
    public void mostrarAccesoConcedido(String salaId) {
        consoleView.mostrarSubtitulo("ACCESO CONCEDIDO");
        consoleView.mostrarExito("¡Te has unido a la sala #" + salaId + "!");
    }

    /**
     * Muestra mensaje de búsqueda cancelada
     */
    public void mostrarBusquedaCancelada() {
        consoleView.mostrarInfo("Búsqueda cancelada.");
    }

    /**
     * Muestra mensaje de salida
     */
    public void mostrarDespedida(String username) {
        consoleView.mostrarInfo("Saliendo de eScrims...");
        consoleView.mostrarExito("¡Hasta pronto, " + username + "!");
    }
}
