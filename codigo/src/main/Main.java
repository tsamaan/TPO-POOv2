package main;

import models.*;
import states.*;
import notifiers.*;
import strategies.*;
import service.*;
import auth.*;
import context.ScrimContext;
import interfaces.INotifier;

public class Main {
    
    // Constantes para mejorar la presentación
    private static final String SEPARATOR = "═══════════════════════════════════════════════════════════════════════════════";
    private static final String LINE = "───────────────────────────────────────────────────────────────────────────────";
    
    public static void main(String[] args) {
        printHeader();
        
        // 1. Demostrar Patrón ADAPTER
        demonstrateAdapterPattern();
        pause();
        
        // 2. Demostrar Patrón ABSTRACT FACTORY
        NotifierFactory factory = demonstrateAbstractFactoryPattern();
        pause();
        
        // 3. Demostrar Patrón STATE y preparar el scrim
        Scrim scrim = demonstrateStatePattern(factory);
        pause();
        
        // 4. Demostrar Patrón STRATEGY
        demonstrateStrategyPattern(scrim);
        pause();
        
        // 5. Demostrar ciclo completo de estados
        demonstrateStateTransitions(scrim);
        pause();
        
        // 6. Demostrar modelo de dominio completo
        demonstrateDomainModel(scrim);
        
        printFooter();
    }
    
    /**
     * Imprime el encabezado principal de la aplicación
     */
    private static void printHeader() {
        System.out.println("\n" + SEPARATOR);
        System.out.println("╔═══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                                               ║");
        System.out.println("║                   eScrims - Plataforma de eSports                             ║");
        System.out.println("║                   Demostración de Patrones de Diseño                          ║");
        System.out.println("║                                                                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════════════════╝");
        System.out.println(SEPARATOR + "\n");
    }
    
    /**
     * Imprime el pie de página
     */
    private static void printFooter() {
        System.out.println("\n" + SEPARATOR);
        System.out.println("╔═══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                                               ║");
        System.out.println("║                 DEMOSTRACIÓN COMPLETADA EXITOSAMENTE                          ║");
        System.out.println("║                                                                               ║");
        System.out.println("║              Todos los patrones implementados:                                ║");
        System.out.println("║              • Patrón ADAPTER                                                 ║");
        System.out.println("║              • Patrón ABSTRACT FACTORY                                        ║");
        System.out.println("║              • Patrón STATE                                                   ║");
        System.out.println("║              • Patrón STRATEGY                                                ║");
        System.out.println("║              • Patrón OBSERVER                                                ║");
        System.out.println("║                                                                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════════════════╝");
        System.out.println(SEPARATOR + "\n");
    }
    
    /**
     * Imprime el título de una sección
     */
    private static void printSectionTitle(String patternNumber, String patternName, String description) {
        System.out.println("\n" + LINE);
        System.out.println("[!!] DEMOSTRACIÓN " + patternNumber + ": PATRÓN " + patternName);
        System.out.println("   " + description);
        System.out.println(LINE + "\n");
    }
    
    /**
     * Pausa visual entre secciones
     */
    private static void pause() {
        System.out.println("\n");
    }
    
    /**
     * Demuestra el Patrón ADAPTER - Autenticación múltiple
     */
    private static void demonstrateAdapterPattern() {
        printSectionTitle("1", "ADAPTER", "Integración de múltiples sistemas de autenticación");
        
        AuthService authService = new AuthService();
        AuthController authController = new AuthController(authService);
        
        System.out.println("[!] Autenticando usuarios con diferentes proveedores...\n");
        
        // Autenticación local
        System.out.println("[*] Autenticación LOCAL:");
        Usuario user1 = authController.login("shadowblade@esports.com", "pass123");
        System.out.println("   [+] Usuario autenticado: " + user1.getUsername());
        System.out.println("   [+] Email: " + user1.getEmail());
        System.out.println();
        
        Usuario user2 = authController.login("phoenixfire@gaming.com", "pass456");
        System.out.println("   [+] Usuario autenticado: " + user2.getUsername());
        System.out.println("   [+] Email: " + user2.getEmail());
        System.out.println();
        
        // Autenticación con Google
        System.out.println("[*] Autenticación GOOGLE OAuth:");
        Usuario user3 = authController.loginWithProvider("google", "google-token-abc123");
        System.out.println("   [+] Usuario Google: " + user3.getUsername());
        System.out.println("   [+] Email: " + user3.getEmail());
        System.out.println();
        
        Usuario user4 = authController.loginWithProvider("google", "google-token-xyz789");
        System.out.println("   [+] Usuario Google: " + user4.getUsername());
        System.out.println("   [+] Email: " + user4.getEmail());
        
        System.out.println("\n[!] 4 usuarios autenticados exitosamente usando diferentes adaptadores");
    }
    
    /**
     * Demuestra el Patrón ABSTRACT FACTORY - Creación de notificadores
     */
    private static NotifierFactory demonstrateAbstractFactoryPattern() {
        printSectionTitle("2", "ABSTRACT FACTORY", "Creación de familias de notificadores");
        
        System.out.println("[!] Creando notificadores mediante Abstract Factory...\n");
        
        NotifierFactory factory = new SimpleNotifierFactory();
        System.out.println("[*] Factory creada: " + factory.getClass().getSimpleName());
        System.out.println();
        
        System.out.println("[*] Creando productos (notificadores):");
        INotifier emailNotifier = factory.createEmailNotifier();
        System.out.println("   [+] " + emailNotifier.getClass().getSimpleName() + " creado");
        
        INotifier discordNotifier = factory.createDiscordNotifier();
        System.out.println("   [+] " + discordNotifier.getClass().getSimpleName() + " creado");
        
        INotifier pushNotifier = factory.createPushNotifier();
        System.out.println("   [+] " + pushNotifier.getClass().getSimpleName() + " creado");
        
        System.out.println("\n[!] Notificadores creados exitosamente");
        
        return factory;
    }
    
    /**
     * Demuestra el Patrón STATE - Estados del scrim
     */
    private static Scrim demonstrateStatePattern(NotifierFactory factory) {
        printSectionTitle("3", "STATE & OBSERVER", "Gestión de estados y notificaciones del Scrim");
        
        System.out.println("[!] Creando nuevo Scrim (Partida de práctica)...\n");
        
        // Crear scrim con estado inicial
        ScrimState estadoInicial = new EstadoBuscandoJugadores();
        Scrim scrim = new Scrim(estadoInicial);
        System.out.println("[*] Estado inicial: " + estadoInicial.getClass().getSimpleName());
        System.out.println();
        
        // Agregar notificadores (patrón OBSERVER)
        System.out.println("[*] Suscribiendo observadores (notificadores) al Scrim:");
        scrim.addNotifier(factory.createEmailNotifier());
        scrim.addNotifier(factory.createDiscordNotifier());
        scrim.addNotifier(factory.createPushNotifier());
        System.out.println();
        
        // Crear context para gestionar el scrim
        ScrimContext context = new ScrimContext(scrim, estadoInicial);
        
        // Crear jugadores
        System.out.println("[!] Jugadores registrándose para Scrim 5v5:\n");
        
        Usuario[] jugadores = {
            new Usuario(1, "ShadowBlade", "shadow@esports.com"),
            new Usuario(2, "PhoenixFire", "phoenix@esports.com"),
            new Usuario(3, "IceQueen", "ice@esports.com"),
            new Usuario(4, "ThunderStrike", "thunder@esports.com"),
            new Usuario(5, "NightHawk", "night@esports.com"),
            new Usuario(6, "DragonSlayer", "dragon@esports.com"),
            new Usuario(7, "SilentAssassin", "silent@esports.com"),
            new Usuario(8, "MysticWizard", "mystic@esports.com")
        };
        
        String[] roles = {"Duelist", "Support", "Controller", "Initiator", "Sentinel", 
                         "Duelist", "Support", "Controller"};
        
        // Simular postulaciones
        for (int i = 0; i < jugadores.length; i++) {
            System.out.println("   " + (i+1) + ". " + jugadores[i].getUsername() + 
                             " (" + roles[i] + ") - " + jugadores[i].getEmail());
            context.postular(jugadores[i], roles[i]);
        }
        
        System.out.println("\n[!] " + jugadores.length + " jugadores postulados exitosamente");
        
        return scrim;
    }
    
    /**
     * Demuestra el Patrón STRATEGY - Matchmaking con diferentes estrategias
     */
    private static void demonstrateStrategyPattern(Scrim scrim) {
        printSectionTitle("4", "STRATEGY", "Algoritmos de matchmaking intercambiables");
        
        System.out.println("[!] Ejecutando matchmaking con diferentes estrategias...\n");
        
        // Estrategia 1: Por MMR (habilidad)
        System.out.println("[*] ESTRATEGIA 1: Emparejamiento por MMR (Habilidad)");
        MatchmakingService mmService = new MatchmakingService(new ByMMRStrategy());
        System.out.println("   Criterio: Nivel de habilidad similar");
        System.out.println("   Prioridad: Balance competitivo");
        System.out.println();
        mmService.ejecutarEmparejamiento(scrim);
        System.out.println("   [+] Estado actual: " + scrim.getEstado().getClass().getSimpleName());
        System.out.println();
        
        // Estrategia 2: Por Latencia
        System.out.println("➤ ESTRATEGIA 2: Emparejamiento por Latencia");
        mmService = new MatchmakingService(new ByLatencyStrategy());
        System.out.println("   Criterio: Menor ping/latencia");
        System.out.println("   Prioridad: Mejor experiencia de juego");
        System.out.println();
        mmService.ejecutarEmparejamiento(scrim);
        System.out.println();
        
        System.out.println("[!] Matchmaking completado - Estrategia aplicada dinámicamente");
    }
    
    /**
     * Demuestra las transiciones de estado
     */
    private static void demonstrateStateTransitions(Scrim scrim) {
        printSectionTitle("5", "STATE TRANSITIONS", "Ciclo de vida completo del Scrim");
        
        System.out.println("[!] Simulando transiciones de estado del Scrim...\n");
        
        // Estado actual
        System.out.println("[*] Estado actual: " + scrim.getEstado().getClass().getSimpleName());
        System.out.println();
        
        // Transición 1: Iniciar
        System.out.println("[*] ACCIÓN: Iniciar scrim");
        System.out.println("   Todos los jugadores confirmaron su participación");
        scrim.getEstado().iniciar(scrim);
        System.out.println("   [+] Nuevo estado: " + scrim.getEstado().getClass().getSimpleName());
        System.out.println();
        
        // Transición 2: Iniciar nuevamente (partida en curso)
        System.out.println("[*] ACCIÓN: La partida está en curso");
        System.out.println("   Los jugadores están compitiendo...");
        scrim.getEstado().iniciar(scrim);
        System.out.println("   [+] Estado: " + scrim.getEstado().getClass().getSimpleName());
        System.out.println();
        
        // Transición 3: Intentar cancelar (desde EnJuego)
        System.out.println("[*] ACCIÓN: Intentar cancelar partida en curso");
        System.out.println("   (No se puede cancelar una partida activa)");
        scrim.getEstado().cancelar(scrim);
        System.out.println("   [+] Estado final: " + scrim.getEstado().getClass().getSimpleName());

        System.out.println("\n[!] Transiciones de estado manejadas correctamente mediante patrón STATE");
    }
    
    /**
     * Demuestra el modelo de dominio completo
     */
    private static void demonstrateDomainModel(Scrim scrim) {
        printSectionTitle("6", "DOMAIN MODEL", "Equipos, Confirmaciones y Estadísticas");
        
        // Crear jugadores para el modelo de dominio
        Usuario[] players = {
            new Usuario(1, "ShadowBlade", "shadow@esports.com"),
            new Usuario(2, "PhoenixFire", "phoenix@esports.com"),
            new Usuario(3, "IceQueen", "ice@esports.com"),
            new Usuario(4, "ThunderStrike", "thunder@esports.com"),
            new Usuario(5, "NightHawk", "night@esports.com"),
            new Usuario(6, "DragonSlayer", "dragon@esports.com"),
            new Usuario(7, "SilentAssassin", "silent@esports.com"),
            new Usuario(8, "MysticWizard", "mystic@esports.com")
        };
        
        // === EQUIPOS ===
        System.out.println("FORMACIÓN DE EQUIPOS\n");
        
        Equipo equipoAzul = new Equipo("[ («..») ] Team Azure");
        Equipo equipoRojo = new Equipo("[ (*,,°) ] Team Crimson");
        
        System.out.println("[*] Asignando jugadores a equipos:");
        System.out.println();
        
        // Equipo Azul
        for (int i = 0; i < 4; i++) {
            equipoAzul.asignarJugador(players[i]);
        }
        System.out.println(equipoAzul);
        System.out.println();
        
        // Equipo Rojo
        for (int i = 4; i < 8; i++) {
            equipoRojo.asignarJugador(players[i]);
        }
        System.out.println(equipoRojo);
        System.out.println();
        
        // === CONFIRMACIONES ===
        System.out.println(LINE);
        System.out.println("[+] CONFIRMACIONES DE PARTICIPACIÓN\n");
        
        Confirmacion[] confirmaciones = new Confirmacion[8];
        
        System.out.println("[*] Estado de confirmaciones de jugadores:");
        System.out.println();
        
        for (int i = 0; i < 8; i++) {
            confirmaciones[i] = new Confirmacion(players[i], scrim);
            
            // Simular que algunos confirman y otros rechazan
            if (i < 6) {
                confirmaciones[i].confirmar();
            } else {
                confirmaciones[i].rechazar();
            }
            
            String status = confirmaciones[i].toString();
            String icon = status.contains("Confirmada") ? "+" : "-";
            System.out.println("   " + icon + " " + confirmaciones[i]);
        }
        
        // Resumen de confirmaciones
        long confirmados = 6;
        long rechazados = 2;
        System.out.println();
        System.out.println("   [/] Resumen: " + confirmados + " confirmados, " + rechazados + " rechazados");
        System.out.println();
        
        // === ESTADÍSTICAS ===
        System.out.println(LINE);
        System.out.println("[!] ESTADÍSTICAS POST-PARTIDA\n");
        
        // Datos de estadísticas más realistas
        int[][] stats = {
            {18, 12, 7},   // ShadowBlade - MVP
            {14, 15, 9},   // PhoenixFire
            {10, 18, 12},  // IceQueen - Más muertes
            {22, 10, 15},  // ThunderStrike - Más kills
            {16, 14, 20},  // NightHawk - Más asistencias
            {8, 16, 6},    // DragonSlayer
            {12, 13, 11},  // SilentAssassin
            {11, 14, 8}    // MysticWizard
        };
        
        Estadistica[] estadisticas = new Estadistica[8];
        
        System.out.println("[*] Rendimiento individual de jugadores:\n");
        
        // Crear y mostrar estadísticas
        for (int i = 0; i < 8; i++) {
            estadisticas[i] = new Estadistica(players[i], scrim, stats[i][0], stats[i][1], stats[i][2]);
        }
        
        // Mostrar tabla de estadísticas
        System.out.println("   ╔═════════════════════╦═══════╦═══════╦═══════╦════════════╗");
        System.out.println("   ║ Jugador             ║ Kills ║ Death ║ Asist ║ KDA Ratio  ║");
        System.out.println("   ╠═════════════════════╬═══════╬═══════╬═══════╬════════════╣");
        
        for (int i = 0; i < 8; i++) {
            String nombre = String.format("%-19s", players[i].getUsername());
            String kills = String.format("%5d", stats[i][0]);
            String deaths = String.format("%5d", stats[i][1]);
            String assists = String.format("%5d", stats[i][2]);
            String kda = String.format("%10.2f", estadisticas[i].getKda());
            
            System.out.println("   ║ " + nombre + " ║ " + kills + " ║ " + deaths + " ║ " + assists + " ║ " + kda + " ║");
        }
        
        System.out.println("   ╚═════════════════════╩═══════╩═══════╩═══════╩════════════╝");
        System.out.println();
        
        // Mostrar calificaciones de rendimiento
        System.out.println("   [+] Calificación de rendimiento por jugador:");
        for (int i = 0; i < 8; i++) {
            System.out.println("      • " + players[i].getUsername() + ": " + estadisticas[i].obtenerRendimiento());
        }
        System.out.println();
        
        // Encontrar MVP (usando el KDA numérico)
        double maxKDA = 0;
        int mvpIndex = 0;
        for (int i = 0; i < 8; i++) {
            double kda = estadisticas[i].getKda();
            if (kda > maxKDA) {
                maxKDA = kda;
                mvpIndex = i;
            }
        }
        
        System.out.println("    MVP del partido: " + players[mvpIndex].getUsername());
        System.out.println("       Rendimiento: " + estadisticas[mvpIndex].obtenerRendimiento());
        System.out.println();
        
        // Mostrar resumen de equipos ganador
        int killsAzul = 0, killsRojo = 0;
        for (int i = 0; i < 4; i++) killsAzul += stats[i][0];
        for (int i = 4; i < 8; i++) killsRojo += stats[i][0];
        
        String ganador = killsAzul > killsRojo ? " Team Azure" : " Team Crimson";
        System.out.println("     Equipo ganador: " + ganador);
        System.out.println("       Score: Azure " + killsAzul + " - " + killsRojo + " Crimson");
        System.out.println();
        
        System.out.println("[!] Modelo de dominio completo demostrado (Equipos, Confirmaciones, Estadísticas)");
    }
}
