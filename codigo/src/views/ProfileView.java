package views;

import models.Usuario;
import models.EstadoEmail;
import java.util.Map;

/**
 * CAPA VIEW (MVC) - Vista de perfil de usuario
 *
 * Responsable de:
 * - Mostrar perfil completo del usuario
 * - Formulario de edición de perfil
 * - Mostrar cambios realizados
 *
 * @pattern MVC - View Layer (Profile Specialist)
 */
public class ProfileView {

    private ConsoleView consoleView;

    public ProfileView(ConsoleView consoleView) {
        this.consoleView = consoleView;
    }

    // ============================================
    // VER PERFIL
    // ============================================

    /**
     * Muestra el perfil completo del usuario
     */
    public void mostrarPerfil(Usuario usuario) {
        consoleView.mostrarTitulo("PERFIL DE USUARIO");

        System.out.println("╔═══════════════════════════════════════════════════════════════════╗");
        System.out.println("║                       INFORMACIÓN DE PERFIL                       ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════════╣");
        System.out.println("║                                                                   ║");
        System.out.println("║  Nombre de usuario:  " + formatCampo(usuario.getUsername(), 43) + "  ║");
        System.out.println("║  Email:              " + formatCampo(usuario.getEmail(), 43) + "  ║");
        System.out.println("║  Estado email:       " + formatCampo(getEstadoEmailStr(usuario), 43) + "  ║");
        System.out.println("║  Tipo autenticación: " + formatCampo(usuario.getTipoAuth().toString(), 43) + "  ║");
        System.out.println("║                                                                   ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════════╣");
        System.out.println("║                       PREFERENCIAS DE JUEGO                       ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════════╣");
        System.out.println("║                                                                   ║");
        System.out.println("║  Juego principal:    " + formatCampo(usuario.getJuegoPrincipal(), 43) + "  ║");
        System.out.println("║  Región:             " + formatCampo(usuario.getRegion(), 43) + "  ║");
        System.out.println("║  Disponibilidad:     " + formatCampo(usuario.getDisponibilidadHoraria(), 43) + "  ║");
        System.out.println("║                                                                   ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════╝");
        System.out.println();

        // Mostrar rangos por juego
        if (!usuario.getRangoPorJuego().isEmpty()) {
            consoleView.mostrarInfo("Rangos configurados:");
            for (Map.Entry<String, Integer> entry : usuario.getRangoPorJuego().entrySet()) {
                System.out.println("  • " + entry.getKey() + ": " + entry.getValue() +
                                 " (" + convertirMMRaRango(entry.getValue()) + ")");
            }
            System.out.println();
        }

        // Mostrar roles preferidos
        if (!usuario.getRolesPreferidos().isEmpty()) {
            consoleView.mostrarInfo("Roles preferidos:");
            for (String rol : usuario.getRolesPreferidos()) {
                System.out.println("  • " + rol);
            }
            System.out.println();
        } else {
            consoleView.mostrarAdvertencia("No has configurado roles preferidos aún");
            System.out.println();
        }
    }

    // ============================================
    // EDITAR PERFIL
    // ============================================

    /**
     * Muestra menú de edición de perfil
     * @return Opción seleccionada (1-6)
     */
    public int mostrarMenuEditarPerfil() {
        consoleView.mostrarSubtitulo("EDITAR PERFIL");

        System.out.println("[1] Cambiar juego principal");
        System.out.println("[2] Cambiar región/servidor");
        System.out.println("[3] Configurar roles preferidos");
        System.out.println("[4] Configurar disponibilidad horaria");
        System.out.println("[5] Configurar rango para un juego");
        System.out.println("[6] Volver al menú principal");
        System.out.println();

        return consoleView.solicitarNumero("Selecciona qué editar", 1, 6);
    }

    /**
     * Solicita nuevo juego principal
     */
    public String solicitarJuegoPrincipal() {
        consoleView.mostrarInfo("Juegos disponibles:");
        System.out.println("  [1] Valorant");
        System.out.println("  [2] League of Legends");
        System.out.println("  [3] CS:GO");

        int opcion = consoleView.solicitarNumero("Selecciona tu juego principal", 1, 3);

        switch (opcion) {
            case 1: return "Valorant";
            case 2: return "League of Legends";
            case 3: return "CS:GO";
            default: return "Valorant";
        }
    }

    /**
     * Solicita nueva región
     */
    public String solicitarRegion() {
        consoleView.mostrarInfo("Regiones disponibles:");
        System.out.println("  [1] SA (South America)");
        System.out.println("  [2] NA (North America)");
        System.out.println("  [3] EU (Europe)");
        System.out.println("  [4] AS (Asia)");

        int opcion = consoleView.solicitarNumero("Selecciona tu región", 1, 4);

        switch (opcion) {
            case 1: return "SA";
            case 2: return "NA";
            case 3: return "EU";
            case 4: return "AS";
            default: return "SA";
        }
    }

    /**
     * Solicita disponibilidad horaria
     */
    public String solicitarDisponibilidad() {
        consoleView.mostrarInfo("Configura tu disponibilidad horaria:");
        System.out.println("  Ejemplos: '18:00-23:00 UTC-3', 'Tardes/Noches', '14:00-20:00'");

        return consoleView.solicitarInput("Disponibilidad horaria");
    }

    /**
     * Solicita rango para un juego específico
     */
    public int solicitarRangoParaJuego(String juego) {
        consoleView.mostrarInfo("Configurando rango para: " + juego);
        System.out.println("  Rangos:");
        System.out.println("    0-400:    Iron/Bronze");
        System.out.println("    400-800:  Silver");
        System.out.println("    800-1200: Gold");
        System.out.println("    1200-1600: Platinum");
        System.out.println("    1600-2000: Diamond");
        System.out.println("    2000-2400: Master");
        System.out.println("    2400+:    Grandmaster/Challenger");

        return consoleView.solicitarNumero("Ingresa tu rango MMR", 0, 3000);
    }

    /**
     * Solicita roles preferidos
     */
    public String[] solicitarRolesPreferidos(String juego) {
        consoleView.mostrarInfo("Selecciona tus roles preferidos para " + juego);
        consoleView.mostrarInfo("(Puedes seleccionar múltiples roles)");
        System.out.println();

        String[] rolesDisponibles = getRolesPorJuego(juego);

        // Mostrar roles
        for (int i = 0; i < rolesDisponibles.length; i++) {
            System.out.println("  [" + (i+1) + "] " + rolesDisponibles[i]);
        }
        System.out.println();

        consoleView.mostrarInfo("Ingresa los números separados por comas (ej: 1,2,3)");
        String seleccion = consoleView.solicitarInput("Roles");

        // Parsear selección
        String[] indices = seleccion.split(",");
        java.util.List<String> rolesSeleccionados = new java.util.ArrayList<>();

        for (String indiceStr : indices) {
            try {
                int indice = Integer.parseInt(indiceStr.trim()) - 1;
                if (indice >= 0 && indice < rolesDisponibles.length) {
                    rolesSeleccionados.add(rolesDisponibles[indice]);
                }
            } catch (NumberFormatException e) {
                // Ignorar entrada inválida
            }
        }

        return rolesSeleccionados.toArray(new String[0]);
    }

    /**
     * Obtiene roles disponibles por juego
     */
    private String[] getRolesPorJuego(String juego) {
        if (juego == null) juego = "Valorant";

        switch (juego) {
            case "Valorant":
                return new String[]{"Duelist", "Controller", "Initiator", "Sentinel"};
            case "League of Legends":
                return new String[]{"Top", "Jungle", "Mid", "ADC", "Support"};
            case "CS:GO":
                return new String[]{"Entry Fragger", "AWPer", "Support", "Lurker", "IGL"};
            default:
                return new String[]{"Flex"};
        }
    }

    /**
     * Muestra confirmación de cambio
     */
    public void mostrarCambioRealizado(String campo, String valor) {
        consoleView.mostrarExito("Campo actualizado: " + campo + " → " + valor);
    }

    // ============================================
    // UTILIDADES
    // ============================================

    /**
     * Formatea un campo para ajustarse al ancho de la tabla
     */
    private String formatCampo(String valor, int ancho) {
        if (valor == null) valor = "No configurado";
        if (valor.length() > ancho) {
            return valor.substring(0, ancho - 3) + "...";
        }
        return String.format("%-" + ancho + "s", valor);
    }

    /**
     * Obtiene string descriptivo del estado de email
     */
    private String getEstadoEmailStr(Usuario usuario) {
        EstadoEmail estado = usuario.getEstadoEmail();
        if (estado == null) return "Desconocido";

        switch (estado) {
            case VERIFICADO:
                return "✓ Verificado";
            case PENDIENTE:
                return "⚠ Pendiente de verificación";
            default:
                return estado.toString();
        }
    }

    /**
     * Convierte MMR numérico a rango string
     */
    private String convertirMMRaRango(int mmr) {
        if (mmr >= 2400) return "Challenger/Grandmaster";
        if (mmr >= 2000) return "Master";
        if (mmr >= 1600) return "Diamond";
        if (mmr >= 1200) return "Platinum";
        if (mmr >= 800) return "Gold";
        if (mmr >= 400) return "Silver";
        if (mmr >= 100) return "Bronze";
        return "Iron";
    }

    /**
     * Muestra mensaje de volviendo al menú
     */
    public void mostrarVolviendoMenu() {
        consoleView.pausaVisual();
        consoleView.mostrarInfo("Volviendo al menú principal...");
        consoleView.pausaVisual();
    }
}
