package views;

import models.*;
import java.util.List;
import java.util.Scanner;

/**
 * CAPA VIEW (MVC) - Responsable SOLO de presentación
 *
 * Responsabilidades:
 * - Mostrar información al usuario (output)
 * - Capturar input del usuario
 * - NO contiene lógica de negocio
 * - NO modifica modelos directamente
 *
 * @pattern MVC - View Layer
 */
public class ConsoleView {

    private static final String SEPARATOR = "═══════════════════════════════════════════════════════════════════════════════";
    private static final String LINE = "───────────────────────────────────────────────────────────────────────────────";

    private Scanner scanner;

    public ConsoleView() {
        this.scanner = new Scanner(System.in);
    }

    // ============================================
    // MÉTODOS DE PRESENTACIÓN GENERAL
    // ============================================

    /**
     * Muestra el header principal de la aplicación
     */
    public void mostrarHeader() {
        System.out.println("\n" + SEPARATOR);
        System.out.println("╔═════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                                             ║");
        System.out.println("║                   eScrims - Plataforma de eSports                           ║");
        System.out.println("║                   Arquitectura MVC Refactorizada                            ║");
        System.out.println("║                                                                             ║");
        System.out.println("╚═════════════════════════════════════════════════════════════════════════════╝");
        System.out.println(SEPARATOR + "\n");
    }

    /**
     * Muestra un título de sección
     */
    public void mostrarTitulo(String titulo) {
        System.out.println("\n" + SEPARATOR);
        System.out.println("[!] " + titulo.toUpperCase());
        System.out.println(SEPARATOR + "\n");
    }

    /**
     * Muestra un subtítulo
     */
    public void mostrarSubtitulo(String subtitulo) {
        System.out.println("\n" + LINE);
        System.out.println("[!] " + subtitulo);
        System.out.println(LINE + "\n");
    }

    /**
     * Muestra un mensaje de éxito
     */
    public void mostrarExito(String mensaje) {
        System.out.println("[+] " + mensaje);
    }

    /**
     * Muestra un mensaje de error
     */
    public void mostrarError(String mensaje) {
        System.out.println("[!] ERROR: " + mensaje);
    }

    /**
     * Muestra un mensaje informativo
     */
    public void mostrarInfo(String mensaje) {
        System.out.println("[*] " + mensaje);
    }

    /**
     * Muestra un mensaje de advertencia
     */
    public void mostrarAdvertencia(String mensaje) {
        System.out.println("[⚠] " + mensaje);
    }

    /**
     * Pausa y espera que usuario presione ENTER
     */
    public void pausar() {
        System.out.println("\n[?] Presiona ENTER para continuar...");
        scanner.nextLine();
    }

    /**
     * Pausa breve visual
     */
    public void pausaVisual() {
        System.out.println();
    }

    /**
     * Simula delay para mejor UX
     */
    public void delay(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ============================================
    // MÉTODOS DE INPUT
    // ============================================

    /**
     * Solicita input de texto al usuario
     */
    public String solicitarInput(String prompt) {
        System.out.print("[>] " + prompt + ": ");
        return scanner.nextLine().trim();
    }

    /**
     * Solicita input numérico con validación
     */
    public int solicitarNumero(String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print("[>] " + prompt + " (" + min + "-" + max + "): ");
                int valor = Integer.parseInt(scanner.nextLine().trim());

                if (valor >= min && valor <= max) {
                    return valor;
                } else {
                    mostrarError("Valor fuera de rango. Debe estar entre " + min + " y " + max);
                }
            } catch (NumberFormatException e) {
                mostrarError("Entrada inválida. Ingresa un número.");
            }
        }
    }

    /**
     * Solicita confirmación S/N
     */
    public boolean solicitarConfirmacion(String prompt) {
        System.out.print("[>] " + prompt + " (S/N): ");
        String respuesta = scanner.nextLine().trim().toUpperCase();
        return respuesta.equals("S") || respuesta.equals("SI") || respuesta.isEmpty();
    }

    // ============================================
    // VISTAS DE SCRIM
    // ============================================

    /**
     * Muestra información detallada de un scrim
     */
    public void mostrarScrim(Scrim scrim) {
        System.out.println("╔═══════════════════════════════════════════════════════════════════╗");
        System.out.println("║                         SCRIM DETAILS                             ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════════╣");
        System.out.println("║ ID:           " + String.format("%-50s", scrim.getId()) + " ║");
        System.out.println("║ Juego:        " + String.format("%-50s", scrim.getJuego()) + " ║");
        System.out.println("║ Formato:      " + String.format("%-50s", scrim.getFormato()) + " ║");
        System.out.println("║ Modalidad:    " + String.format("%-50s", scrim.getModalidad()) + " ║");
        System.out.println("║ Región:       " + String.format("%-50s", scrim.getRegion()) + " ║");
        System.out.println("║ Rango:        " + String.format("%-50s", scrim.getRangoMin() + " - " + scrim.getRangoMax()) + " ║");
        System.out.println("║ Latencia Max: " + String.format("%-50s", scrim.getLatenciaMax() + " ms") + " ║");
        System.out.println("║ Estado:       " + String.format("%-50s", scrim.getEstado().getClass().getSimpleName()) + " ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════╝");
    }

    /**
     * Muestra lista de scrims disponibles
     */
    public void mostrarListaScrims(List<Scrim> scrims, Usuario usuario) {
        if (scrims.isEmpty()) {
            mostrarAdvertencia("No hay scrims disponibles");
            return;
        }

        mostrarInfo("Salas disponibles:");
        System.out.println();

        for (int i = 0; i < scrims.size(); i++) {
            Scrim scrim = scrims.get(i);
            System.out.println("[" + (i + 1) + "] ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("    Juego:      " + scrim.getJuego());
            System.out.println("    Modalidad:  " + scrim.getModalidad());
            System.out.println("    Formato:    " + scrim.getFormato());
            System.out.println("    Rango:      " + scrim.getRangoMin() + " - " + scrim.getRangoMax());
            System.out.println("    Latencia:   < " + scrim.getLatenciaMax() + " ms");
            System.out.println("    Región:     " + scrim.getRegion());

            // Indicar si puede unirse
            if (usuario != null) {
                boolean puedeUnirse = validarRangoUsuario(usuario, scrim);
                if (puedeUnirse) {
                    System.out.println("    Estado:     [✓] Puedes unirte");
                } else {
                    System.out.println("    Estado:     [✗] Rango incompatible");
                }
            }
            System.out.println();
        }
    }

    /**
     * Valida si usuario cumple requisitos de rango para un scrim
     */
    private boolean validarRangoUsuario(Usuario usuario, Scrim scrim) {
        if (!usuario.getRangoPorJuego().containsKey(scrim.getJuego())) {
            return false;
        }
        int rangoUsuario = usuario.getRangoPorJuego().get(scrim.getJuego());
        return rangoUsuario >= scrim.getRangoMin() && rangoUsuario <= scrim.getRangoMax();
    }

    // ============================================
    // VISTAS DE EQUIPOS
    // ============================================

    /**
     * Muestra los equipos formados
     */
    public void mostrarEquipos(Equipo equipoAzul, Equipo equipoRojo, List<String> rolesAsignados, List<Usuario> jugadores, Usuario usuarioActual) {
        System.out.println("╔═══════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                         EQUIPOS FORMADOS                              ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════════════╣");
        System.out.println("║                                                                       ║");
        System.out.println("║  " + String.format("%-67s", equipoAzul.getLado()) + "  ║");
        System.out.println("║  ───────────────────────────────────────────────────────────────────  ║");
        System.out.println("║     Jugador                                              Rol          ║");
        System.out.println("║  ───────────────────────────────────────────────────────────────────  ║");

        for (int i = 0; i < equipoAzul.getJugadores().size(); i++) {
            Usuario jugador = equipoAzul.getJugadores().get(i);
            String rol = rolesAsignados.get(jugadores.indexOf(jugador));
            String marker = jugador.getUsername().equals(usuarioActual.getUsername()) ? "*" : " ";
            String nombre = String.format("%-50s", marker + " " + jugador.getUsername());
            String rolFormato = String.format("%-10s", rol);
            System.out.println("║   " + nombre + "  " + rolFormato + "      ║");
        }

        System.out.println("║                                                                       ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════════════╣");
        System.out.println("║                                                                       ║");
        System.out.println("║  " + String.format("%-67s", equipoRojo.getLado()) + "  ║");
        System.out.println("║  ───────────────────────────────────────────────────────────────────  ║");
        System.out.println("║     Jugador                                              Rol          ║");
        System.out.println("║  ───────────────────────────────────────────────────────────────────  ║");

        for (int i = 0; i < equipoRojo.getJugadores().size(); i++) {
            Usuario jugador = equipoRojo.getJugadores().get(i);
            String rol = rolesAsignados.get(jugadores.indexOf(jugador));
            String marker = jugador.getUsername().equals(usuarioActual.getUsername()) ? "*" : " ";
            String nombre = String.format("%-50s", marker + " " + jugador.getUsername());
            String rolFormato = String.format("%-10s", rol);
            System.out.println("║   " + nombre + "  " + rolFormato + "      ║");
        }

        System.out.println("║                                                                       ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════════╝");
        System.out.println("\n[★] * indica tu posición en el equipo\n");
    }

    // ============================================
    // VISTAS DE ESTADÍSTICAS
    // ============================================

    /**
     * Muestra tabla de estadísticas post-partida
     */
    public void mostrarEstadisticas(List<Estadistica> estadisticas, Usuario mvp) {
        mostrarSubtitulo("ESTADÍSTICAS POST-PARTIDA");

        System.out.println("╔═══════════════════╦═══════╦═══════╦═══════╦════════════╗");
        System.out.println("║ Jugador           ║ Kills ║ Death ║ Asist ║ KDA Ratio  ║");
        System.out.println("╠═══════════════════╬═══════╬═══════╬═══════╬════════════╣");

        for (Estadistica stat : estadisticas) {
            String nombre = String.format("%-17s", stat.getUsuario().getUsername());
            String kills = String.format("%5d", stat.getKills());
            String deaths = String.format("%5d", stat.getDeaths());
            String assists = String.format("%5d", stat.getAssists());
            String kda = String.format("%10.2f", stat.getKda());

            System.out.println("║ " + nombre + " ║ " + kills + " ║ " + deaths + " ║ " + assists + " ║ " + kda + " ║");
        }

        System.out.println("╚═══════════════════╩═══════╩═══════╩═══════╩════════════╝");

        if (mvp != null) {
            System.out.println("\n[★] MVP: " + mvp.getUsername());
        }
    }

    // ============================================
    // VISTAS DE PROGRESO
    // ============================================

    /**
     * Muestra barra de progreso ASCII
     */
    public void mostrarProgreso(String mensaje, int actual, int total) {
        int porcentaje = (actual * 100) / total;
        int barras = porcentaje / 5;

        StringBuilder barra = new StringBuilder("[");
        for (int i = 0; i < 20; i++) {
            if (i < barras) {
                barra.append("█");
            } else {
                barra.append("░");
            }
        }
        barra.append("] " + porcentaje + "%");

        System.out.println("[*] " + mensaje + ": " + actual + "/" + total);
        System.out.println("    " + barra.toString());
    }

    /**
     * Muestra animación de búsqueda
     */
    public void mostrarBuscando(String mensaje) {
        System.out.print("[*] " + mensaje + " ");
        for (int i = 0; i < 3; i++) {
            System.out.print(".");
            delay(500);
        }
        System.out.println();
    }

    // ============================================
    // GETTER PARA CONTROLLERS
    // ============================================

    public Scanner getScanner() {
        return scanner;
    }

    public void cerrarScanner() {
        if (scanner != null) {
            scanner.close();
        }
    }
}
