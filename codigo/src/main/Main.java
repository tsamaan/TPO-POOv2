package main;

import models.*;
import states.*;
import notifiers.*;
import strategies.*;
import service.*;
import auth.*;
import context.ScrimContext;
import interfaces.INotifier;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    
    // Constantes para mejorar la presentacion
    private static final String SEPARATOR = "═══════════════════════════════════════════════════════════════════════════════";
    private static final String LINE = "───────────────────────────────────────────────────────────────────────────────";
    
    // Scanner global para input del usuario
    private static Scanner scanner = new Scanner(System.in);
    
    // Lista de jugadores bot para simular matchmaking
    private static final String[] BOT_NAMES = {
        "ShadowBlade", "PhoenixFire", "IceQueen", "ThunderStrike", 
        "NightHawk", "DragonSlayer", "SilentAssassin", "MysticWizard",
        "CyberNinja", "StormRider", "LunarEclipse", "BlazeFury",
        "FrostBite", "VenomStrike", "GhostRecon", "ZenMaster"
    };
    
    private static final String[] ROLES = {
        "Top", "Support", "ADC", "Jungla", "Mid"
    };
    
    public static void main(String[] args) {
        printHeader();
        
        // Modo interactivo: Login y matchmaking
        interactiveMatchmaking();
        
        scanner.close();
    }
    
    /**
     * Imprime el encabezado principal de la aplicacion
     */
    private static void printHeader() {
        System.out.println("\n" + SEPARATOR);
        System.out.println("╔═════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                                             ║");
        System.out.println("║                   eScrims - Plataforma de eSports                           ║");
        System.out.println("║                   Demostracion de Patrones de Diseño                        ║");
        System.out.println("║                                                                             ║");
        System.out.println("╚═════════════════════════════════════════════════════════════════════════════╝");
        System.out.println(SEPARATOR + "\n");
    }
    
    /**
     * Sistema interactivo de matchmaking
     */
    private static void interactiveMatchmaking() {
        // 1. Login del usuario
        Usuario usuarioActual = loginUsuario();
        System.out.println();
        
        // 2. Menu principal
        boolean enJuego = true;
        while (enJuego) {
            mostrarMenuPrincipal(usuarioActual);
            String opcion = scanner.nextLine().trim();
            
            switch (opcion) {
                case "1":
                    buscarPartida(usuarioActual);
                    break;
                case "2":
                    ejecutarDemoCompleta();
                    break;
                case "3":
                    System.out.println("\n[!] Saliendo de eScrims...");
                    System.out.println("[+] ¡Hasta pronto, " + usuarioActual.getUsername() + "!");
                    enJuego = false;
                    break;
                default:
                    System.out.println("\n[!] Opción inválida. Intenta nuevamente.");
                    break;
            }
        }
    }
    
    /**
     * Login interactivo del usuario
     */
    private static Usuario loginUsuario() {
        System.out.println(LINE);
        System.out.println("[!] LOGIN - Sistema de Autenticación");
        System.out.println(LINE + "\n");
        
        String username = "";
        String email = "";
        String password = "";
        
        // Solicitar nombre de usuario (obligatorio)
        while (username.isEmpty()) {
            System.out.print("[>] Ingresa tu nombre de usuario: ");
            username = scanner.nextLine().trim();
            if (username.isEmpty()) {
                System.out.println("[!] El nombre de usuario no puede estar vacío. Intenta nuevamente.");
            }
        }
        
        // Solicitar email (obligatorio)
        while (email.isEmpty()) {
            System.out.print("[>] Ingresa tu email: ");
            email = scanner.nextLine().trim();
            if (email.isEmpty()) {
                System.out.println("[!] El email no puede estar vacío. Intenta nuevamente.");
            }
        }
        
        // Solicitar contraseña (obligatorio)
        while (password.isEmpty()) {
            System.out.print("[>] Ingresa tu contraseña: ");
            password = scanner.nextLine().trim();
            if (password.isEmpty()) {
                System.out.println("[!] La contraseña no puede estar vacía. Intenta nuevamente.");
            }
        }
        
        // Autenticar usando el patrón Adapter
        AuthService authService = new AuthService();
        AuthController authController = new AuthController(authService);
        authController.login(email, password);
        
        // Crear usuario con los datos ingresados
        Usuario usuario = new Usuario(1, username, email);
        
        System.out.println("\n[+] ¡Bienvenido, " + usuario.getUsername() + "!");
        System.out.println("[+] Email: " + usuario.getEmail());
        
        return usuario;
    }
    
    /**
     * Muestra el menu principal
     */
    private static void mostrarMenuPrincipal(Usuario usuario) {
        System.out.println("\n" + LINE);
        System.out.println("[!] MENU PRINCIPAL - " + usuario.getUsername());
        System.out.println(LINE);
        System.out.println("\n[1] Buscar Partida (Scrim)");
        System.out.println("[2] Ver Demo Completa de Patrones");
        System.out.println("[3] Salir");
        System.out.print("\n[>] Selecciona una opción: ");
    }
    
    /**
     * Sistema de búsqueda de partida con matchmaking
     */
    private static void buscarPartida(Usuario usuarioActual) {
        System.out.println("\n" + SEPARATOR);
        System.out.println("[!] BUSCANDO PARTIDA...");
        System.out.println(SEPARATOR + "\n");
        
        // Seleccionar rol
        String rolSeleccionado = seleccionarRol();
        
        // Crear sistema de notificaciones (Patrón Abstract Factory)
        System.out.println("\n[*] Inicializando sistema de notificaciones...");
        NotifierFactory factory = new SimpleNotifierFactory();
        INotifier emailNotifier = factory.createEmailNotifier();
        INotifier discordNotifier = factory.createDiscordNotifier();
        INotifier pushNotifier = factory.createPushNotifier();
        System.out.println("[+] Sistema de notificaciones activo");
        
        // Crear nuevo scrim usando BUILDER (Patrón Builder)
        System.out.println("\n[*] Creando nueva sala de matchmaking usando Builder...");
        ScrimState estadoInicial = new EstadoBuscandoJugadores();
        Scrim scrim = new Scrim.Builder(estadoInicial)
            .juego("Valorant")
            .formato("5v5")
            .region("SA")
            .modalidad("ranked")
            .rangoMin(1000)
            .rangoMax(2000)
            .latenciaMax(80)
            .build();
        
        // Agregar notificadores (Patrón Observer)
        scrim.addNotifier(emailNotifier);
        scrim.addNotifier(discordNotifier);
        scrim.addNotifier(pushNotifier);
        
        ScrimContext context = new ScrimContext(scrim, estadoInicial);
        System.out.println("[+] Sala creada - Estado: " + scrim.getEstado().getClass().getSimpleName());
        
        // Postular al usuario actual
        System.out.println("\n[*] Registrando tu postulación...");
        context.postular(usuarioActual, rolSeleccionado);
        System.out.println("[+] ¡Te has unido a la cola de matchmaking!");
        System.out.println("[+] Rol seleccionado: " + rolSeleccionado);
        
        // Simular búsqueda de otros jugadores
        System.out.println("\n" + LINE);
        System.out.println("[!] BUSCANDO JUGADORES... (se necesitan 8 jugadores en total)");
        System.out.println(LINE + "\n");
        
        List<Usuario> jugadoresEncontrados = new ArrayList<>();
        List<String> rolesAsignados = new ArrayList<>();
        
        jugadoresEncontrados.add(usuarioActual);
        rolesAsignados.add(rolSeleccionado);
        
        Random random = new Random();
        int jugadoresNecesarios = 8;
        
        // Simular la búsqueda progresiva
        for (int i = 1; i < jugadoresNecesarios; i++) {
            try {
                Thread.sleep(1500); // Simular tiempo de búsqueda
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Crear jugador bot
            String botName = BOT_NAMES[i];
            String botEmail = botName.toLowerCase() + "@esports.com";
            Usuario bot = new Usuario(i + 1, botName, botEmail);
            
            // Asignar rol asegurando balance (máximo 2 por rol - 1 por equipo)
            String botRol = asignarRolBalanceado(rolesAsignados, random);
            rolesAsignados.add(botRol);
            
            jugadoresEncontrados.add(bot);
            context.postular(bot, botRol);
            
            System.out.println("[" + (i + 1) + "/8] Jugador encontrado: " + botName + " (" + botRol + ")");
        }
        
        // Mostrar resumen de roles
        System.out.println("\n[*] Resumen de roles en el lobby:");
        int[] contadorRoles = new int[ROLES.length];
        for (String rol : rolesAsignados) {
            for (int i = 0; i < ROLES.length; i++) {
                if (ROLES[i].equals(rol)) {
                    contadorRoles[i]++;
                    break;
                }
            }
        }
        for (int i = 0; i < ROLES.length; i++) {
            System.out.println("    • " + ROLES[i] + ": " + contadorRoles[i] + " jugador(es)");
        }
        
        // Ejecutar matchmaking (Patrón Strategy)
        System.out.println("\n" + LINE);
        System.out.println("[!] ¡LOBBY COMPLETO! Ejecutando matchmaking...");
        System.out.println(LINE + "\n");
        
        System.out.println("[*] Aplicando algoritmo de emparejamiento por MMR...");
        MatchmakingService mmService = new MatchmakingService(new ByMMRStrategy());
        mmService.ejecutarEmparejamiento(scrim);
        System.out.println("[+] Estado actual: " + scrim.getEstado().getClass().getSimpleName());
        
        // Formar equipos
        System.out.println("\n" + SEPARATOR);
        System.out.println("[!] FORMANDO EQUIPOS");
        System.out.println(SEPARATOR + "\n");
        
        Equipo equipoAzul = new Equipo("Team Azure");
        Equipo equipoRojo = new Equipo("Team Crimson");
        
        // Asignar jugadores a equipos
        for (int i = 0; i < jugadoresEncontrados.size(); i++) {
            if (i < 4) {
                equipoAzul.asignarJugador(jugadoresEncontrados.get(i));
            } else {
                equipoRojo.asignarJugador(jugadoresEncontrados.get(i));
            }
        }
        
        // Mostrar equipos
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
            String rol = rolesAsignados.get(jugadoresEncontrados.indexOf(jugador));
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
            String rol = rolesAsignados.get(jugadoresEncontrados.indexOf(jugador));
            String marker = jugador.getUsername().equals(usuarioActual.getUsername()) ? "*" : " ";
            String nombre = String.format("%-50s", marker + " " + jugador.getUsername());
            String rolFormato = String.format("%-10s", rol);
            System.out.println("║   " + nombre + "  " + rolFormato + "      ║");
        }
        System.out.println("║                                                                       ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════════╝");
        System.out.println("\n[★] Indica tu posición en el equipo\n");
        
        // Gestión de roles con patrón COMMAND
        gestionarRolesConComandos(usuarioActual, jugadoresEncontrados, rolesAsignados, context);
        
        // Proceso de confirmación
        procesarConfirmaciones(usuarioActual, jugadoresEncontrados, scrim);
        
        // Iniciar partida
        iniciarPartida(scrim, equipoAzul, equipoRojo, jugadoresEncontrados);
    }
    
    /**
     * Permite al usuario seleccionar su rol
     */
    private static String seleccionarRol() {
        System.out.println("\n[!] Selecciona tu rol preferido:");
        System.out.println();
        for (int i = 0; i < ROLES.length; i++) {
            System.out.println("[" + (i + 1) + "] " + ROLES[i]);
        }
        System.out.print("\n[>] Ingresa el número de tu rol: ");
        
        int seleccion = -1;
        try {
            seleccion = Integer.parseInt(scanner.nextLine().trim());
            if (seleccion >= 1 && seleccion <= ROLES.length) {
                return ROLES[seleccion - 1];
            }
        } catch (NumberFormatException e) {
            // Continuar con rol por defecto
        }
        
        // Rol por defecto si la selección es inválida
        System.out.println("[!] Selección inválida, asignando rol: " + ROLES[0]);
        return ROLES[0];
    }
    
    /**
     * Asigna un rol de forma balanceada (máximo 2 jugadores por rol - 1 por equipo)
     */
    private static String asignarRolBalanceado(List<String> rolesAsignados, Random random) {
        // Contar cuántos jugadores hay de cada rol
        int[] contadorRoles = new int[ROLES.length];
        for (String rol : rolesAsignados) {
            for (int i = 0; i < ROLES.length; i++) {
                if (ROLES[i].equals(rol)) {
                    contadorRoles[i]++;
                    break;
                }
            }
        }
        
        // Crear lista de roles disponibles (que tengan menos de 2 jugadores)
        List<String> rolesDisponibles = new ArrayList<>();
        for (int i = 0; i < ROLES.length; i++) {
            if (contadorRoles[i] < 2) {
                rolesDisponibles.add(ROLES[i]);
            }
        }
        
        // Si todos los roles tienen 2 jugadores pero aún faltan jugadores
        // (caso de más de 10 jugadores), permitir roles con 2
        if (rolesDisponibles.isEmpty()) {
            for (int i = 0; i < ROLES.length; i++) {
                if (contadorRoles[i] == 2) {
                    rolesDisponibles.add(ROLES[i]);
                }
            }
        }
        
        // Seleccionar un rol aleatorio de los disponibles
        return rolesDisponibles.get(random.nextInt(rolesDisponibles.size()));
    }
    
    /**
     * Gestiona roles usando el patrón COMMAND
     * Permite al usuario ajustar roles con capacidad de undo
     */
    private static void gestionarRolesConComandos(Usuario usuarioActual, List<Usuario> jugadores, 
                                                   List<String> rolesAsignados, ScrimContext context) {
        System.out.println(LINE);
        System.out.println("[!] GESTIÓN DE ROLES (Patrón COMMAND)");
        System.out.println(LINE + "\n");
        
        // Asignar los roles guardados a los usuarios
        for (int i = 0; i < jugadores.size(); i++) {
            jugadores.get(i).setRol(rolesAsignados.get(i));
        }
        
        // Crear el gestor de comandos (Invoker)
        commands.CommandManager commandManager = new commands.CommandManager(context);
        
        System.out.println("[*] Como organizador, puedes ajustar los roles antes de confirmar.");
        System.out.println("[*] Los cambios se pueden deshacer con UNDO.\n");
        
        boolean gestionando = true;
        while (gestionando) {
            System.out.println("\n[?] Opciones:");
            System.out.println("  [1] Cambiar rol de un jugador");
            System.out.println("  [2] Intercambiar roles entre dos jugadores");
            System.out.println("  [3] Deshacer último cambio");
            System.out.println("  [4] Continuar a confirmación");
            System.out.print("\n[>] Selecciona una opción: ");
            
            String opcion = scanner.nextLine().trim();
            
            switch (opcion) {
                case "1":
                    // Asignar nuevo rol
                    cambiarRolJugador(jugadores, commandManager);
                    break;
                    
                case "2":
                    // Swap de roles
                    intercambiarRoles(jugadores, commandManager);
                    break;
                    
                case "3":
                    // Undo
                    commandManager.deshacerUltimo();
                    mostrarRolesActuales(jugadores);
                    break;
                    
                case "4":
                    // Continuar
                    System.out.println("\n[+] Roles finalizados. Continuando a confirmación...");
                    gestionando = false;
                    break;
                    
                default:
                    System.out.println("[!] Opción inválida");
                    break;
            }
        }
        
        // Actualizar la lista de roles asignados con los cambios finales
        for (int i = 0; i < jugadores.size(); i++) {
            rolesAsignados.set(i, jugadores.get(i).getRol());
        }
        
        System.out.println();
    }
    
    /**
     * Cambia el rol de un jugador usando AsignarRolCommand
     */
    private static void cambiarRolJugador(List<Usuario> jugadores, commands.CommandManager commandManager) {
        System.out.println("\n[*] Jugadores disponibles:");
        for (int i = 0; i < jugadores.size(); i++) {
            Usuario j = jugadores.get(i);
            System.out.println("  [" + (i + 1) + "] " + j.getUsername() + " - Rol actual: " + j.getRol());
        }
        
        System.out.print("\n[>] Selecciona jugador (número): ");
        try {
            int indice = Integer.parseInt(scanner.nextLine().trim()) - 1;
            
            if (indice >= 0 && indice < jugadores.size()) {
                Usuario jugador = jugadores.get(indice);
                
                System.out.println("\n[*] Roles disponibles:");
                for (int i = 0; i < ROLES.length; i++) {
                    System.out.println("  [" + (i + 1) + "] " + ROLES[i]);
                }
                
                System.out.print("\n[>] Selecciona nuevo rol (número): ");
                int rolIndice = Integer.parseInt(scanner.nextLine().trim()) - 1;
                
                if (rolIndice >= 0 && rolIndice < ROLES.length) {
                    // Crear y ejecutar comando
                    commands.AsignarRolCommand comando = new commands.AsignarRolCommand(jugador, ROLES[rolIndice]);
                    commandManager.ejecutarComando(comando);
                    mostrarRolesActuales(jugadores);
                } else {
                    System.out.println("[!] Rol inválido");
                }
            } else {
                System.out.println("[!] Jugador inválido");
            }
        } catch (NumberFormatException e) {
            System.out.println("[!] Entrada inválida");
        }
    }
    
    /**
     * Intercambia roles entre dos jugadores usando SwapJugadoresCommand
     */
    private static void intercambiarRoles(List<Usuario> jugadores, commands.CommandManager commandManager) {
        System.out.println("\n[*] Jugadores disponibles:");
        for (int i = 0; i < jugadores.size(); i++) {
            Usuario j = jugadores.get(i);
            System.out.println("  [" + (i + 1) + "] " + j.getUsername() + " - Rol: " + j.getRol());
        }
        
        try {
            System.out.print("\n[>] Primer jugador (número): ");
            int indice1 = Integer.parseInt(scanner.nextLine().trim()) - 1;
            
            System.out.print("[>] Segundo jugador (número): ");
            int indice2 = Integer.parseInt(scanner.nextLine().trim()) - 1;
            
            if (indice1 >= 0 && indice1 < jugadores.size() && 
                indice2 >= 0 && indice2 < jugadores.size() && 
                indice1 != indice2) {
                
                // Crear y ejecutar comando
                commands.SwapJugadoresCommand comando = new commands.SwapJugadoresCommand(
                    jugadores.get(indice1), 
                    jugadores.get(indice2)
                );
                commandManager.ejecutarComando(comando);
                mostrarRolesActuales(jugadores);
                
            } else {
                System.out.println("[!] Jugadores inválidos o iguales");
            }
        } catch (NumberFormatException e) {
            System.out.println("[!] Entrada inválida");
        }
    }
    
    /**
     * Muestra los roles actuales de todos los jugadores
     */
    private static void mostrarRolesActuales(List<Usuario> jugadores) {
        System.out.println("\n[*] Roles actuales:");
        for (Usuario j : jugadores) {
            System.out.println("  • " + j.getUsername() + ": " + j.getRol());
        }
    }
    
    /**
     * Procesa las confirmaciones de los jugadores
     */
    private static void procesarConfirmaciones(Usuario usuarioActual, List<Usuario> jugadores, Scrim scrim) {
        System.out.println(LINE);
        System.out.println("[!] FASE DE CONFIRMACIÓN");
        System.out.println(LINE + "\n");
        
        System.out.println("[*] Los jugadores están confirmando su participación...\n");
        
        List<Confirmacion> confirmaciones = new ArrayList<>();
        Random random = new Random();
        
        for (Usuario jugador : jugadores) {
            Confirmacion conf = new Confirmacion(jugador, scrim);
            
            if (jugador.getUsername().equals(usuarioActual.getUsername())) {
                // El usuario actual confirma manualmente
                System.out.print("[>] ¿Confirmas tu participación? (S/N): ");
                String respuesta = scanner.nextLine().trim().toUpperCase();
                
                if (respuesta.equals("S") || respuesta.equals("SI") || respuesta.isEmpty()) {
                    conf.confirmar();
                } else {
                    conf.rechazar();
                }
            } else {
                // Los bots confirman automáticamente (95% de probabilidad)
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                if (random.nextInt(100) < 95) {
                    conf.confirmar();
                } else {
                    conf.rechazar();
                }
                System.out.println("   [+] " + jugador.getUsername() + ": " + 
                                 (conf.isConfirmado() ? "CONFIRMADO" : "RECHAZADO"));
            }
            
            confirmaciones.add(conf);
        }
        
        // Resumen
        long confirmados = confirmaciones.stream().filter(Confirmacion::isConfirmado).count();
        System.out.println("\n[!] Confirmaciones: " + confirmados + "/" + jugadores.size());
        
        if (confirmados == jugadores.size()) {
            System.out.println("[+] ¡Todos los jugadores confirmaron! Iniciando partida...");
        }
    }
    
    /**
     * Inicia la partida y muestra estadísticas finales
     */
    private static void iniciarPartida(Scrim scrim, Equipo equipoAzul, Equipo equipoRojo, List<Usuario> jugadores) {
        System.out.println("\n" + SEPARATOR);
        System.out.println("[!] INICIANDO PARTIDA...");
        System.out.println(SEPARATOR + "\n");
        
        // Transiciones de estado
        scrim.getEstado().iniciar(scrim);
        System.out.println("[+] Estado: " + scrim.getEstado().getClass().getSimpleName());
        
        System.out.println("\n[*] La partida está en curso...");
        System.out.println("[*] Duración estimada: 30-90 minutos");
        
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        scrim.getEstado().iniciar(scrim);
        System.out.println("[+] Estado: " + scrim.getEstado().getClass().getSimpleName());
        
        // Simular fin de partida
        System.out.println("\n[*] Simulando fin de partida...\n");
        
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        scrim.getEstado().cancelar(scrim);
        System.out.println("[+] Estado: " + scrim.getEstado().getClass().getSimpleName());
        
        // Generar estadísticas
        mostrarEstadisticas(jugadores, scrim, equipoAzul, equipoRojo);
    }
    
    /**
     * Muestra las estadísticas post-partida
     */
    private static void mostrarEstadisticas(List<Usuario> jugadores, Scrim scrim, Equipo equipoAzul, Equipo equipoRojo) {
        System.out.println("\n" + SEPARATOR);
        System.out.println("[!] ESTADÍSTICAS POST-PARTIDA");
        System.out.println(SEPARATOR + "\n");
        
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
        
        // Mostrar tabla
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
        
        // Determinar MVP
        Estadistica mvp = estadisticas.stream()
            .max((a, b) -> Double.compare(a.getKda(), b.getKda()))
            .orElse(estadisticas.get(0));
        
        System.out.println("\n[★] MVP: " + mvp.getUsuario().getUsername());
        System.out.println("    " + mvp.obtenerRendimiento());
        
        // Determinar ganador
        int killsAzul = 0, killsRojo = 0;
        for (int i = 0; i < estadisticas.size(); i++) {
            if (i < 4) {
                killsAzul += estadisticas.get(i).getKills();
            } else {
                killsRojo += estadisticas.get(i).getKills();
            }
        }
        
        System.out.println("\n[!] RESULTADO FINAL:");
        System.out.println("    " + equipoAzul.getLado() + ": " + killsAzul + " kills");
        System.out.println("    " + equipoRojo.getLado() + ": " + killsRojo + " kills");
        System.out.println("\n[★] GANADOR: " + (killsAzul > killsRojo ? equipoAzul.getLado() : equipoRojo.getLado()));
        
        System.out.println("\n[+] Partida finalizada. Volviendo al menú principal...");
    }
    
    /**
     * Ejecuta una demostracion completa de todos los patrones de diseño
     */
    private static void ejecutarDemoCompleta() {
        System.out.println("\n" + SEPARATOR);
        System.out.println("╔═══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                                               ║");
        System.out.println("║                    DEMOSTRACION COMPLETA DE PATRONES                          ║");
        System.out.println("║                                                                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════════════════╝");
        System.out.println(SEPARATOR + "\n");
        
        pause();
        
        // 1. Adapter Pattern
        demonstrateAdapterPattern();
        pause();
        
        // 2. Abstract Factory Pattern  
        NotifierFactory factory = demonstrateAbstractFactoryPattern();
        pause();
        
        // 3. State & Observer Pattern
        Scrim scrim = demonstrateStatePattern(factory);
        pause();
        
        // 4. Strategy Pattern
        demonstrateStrategyPattern(scrim);
        pause();
        
        // 5. State Transitions
        demonstrateStateTransitions(scrim);
        pause();
        
        // 6. Builder Pattern
        demonstrateBuilderPattern();
        pause();
        
        // 7. Composite Pattern
        demonstrateCompositePattern();
        pause();
        
        // 8. Domain Model
        demonstrateDomainModel(scrim);
        
        // Footer
        printFooter();
    }

    /**
     * Imprime el pie de pagina
     */
    private static void printFooter() {
        System.out.println("\n" + SEPARATOR);
        System.out.println("╔═══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                                               ║");
        System.out.println("║                 DEMOSTRACION COMPLETADA EXITOSAMENTE                          ║");
        System.out.println("║                                                                               ║");
        System.out.println("║              ✅ Todos los patrones implementados (8 total):                   ║");
        System.out.println("║                                                                               ║");
        System.out.println("║              1. Patron ADAPTER        - Autenticacion multiple                ║");
        System.out.println("║              2. Patron ABSTRACT FACTORY - Creacion de notificadores           ║");
        System.out.println("║              3. Patron STATE          - Estados del scrim                     ║");
        System.out.println("║              4. Patron STRATEGY       - Algoritmos de matchmaking             ║");
        System.out.println("║              5. Patron OBSERVER       - Sistema de notificaciones             ║");
        System.out.println("║              6. Patron COMMAND        - Acciones reversibles                  ║");
        System.out.println("║              7. Patron BUILDER        - Construccion de scrims                ║");
        System.out.println("║              8. Patron COMPOSITE      - Grupos de notificadores               ║");
        System.out.println("║                                                                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════════════════╝");
        System.out.println(SEPARATOR + "\n");
    }
    
    /**
     * Imprime el titulo de una seccion
     */
    private static void printSectionTitle(String patternNumber, String patternName, String description) {
        System.out.println("\n" + LINE);
        System.out.println("[!!] DEMOSTRACION " + patternNumber + ": PATRON " + patternName);
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
     * Demuestra el Patron ADAPTER - Autenticacion multiple
     */
    private static void demonstrateAdapterPattern() {
        printSectionTitle("1", "ADAPTER", "Integracion de multiples sistemas de autenticacion");
        
        AuthService authService = new AuthService();
        AuthController authController = new AuthController(authService);
        
        System.out.println("[!] Autenticando usuarios con diferentes proveedores...\n");
        
        // Autenticacion local
        System.out.println("[*] Autenticacion LOCAL:");
        Usuario user1 = authController.login("shadowblade@esports.com", "pass123");
        System.out.println("   [+] Usuario autenticado: " + user1.getUsername());
        System.out.println("   [+] Email: " + user1.getEmail());
        System.out.println();
        
        Usuario user2 = authController.login("phoenixfire@gaming.com", "pass456");
        System.out.println("   [+] Usuario autenticado: " + user2.getUsername());
        System.out.println("   [+] Email: " + user2.getEmail());
        System.out.println();
        
        // Autenticacion con Google
        System.out.println("[*] Autenticacion GOOGLE OAuth:");
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
     * Demuestra el Patron ABSTRACT FACTORY - Creacion de notificadores
     */
    private static NotifierFactory demonstrateAbstractFactoryPattern() {
        printSectionTitle("2", "ABSTRACT FACTORY", "Creacion de familias de notificadores");
        
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
     * Demuestra el Patron STATE - Estados del scrim
     */
    private static Scrim demonstrateStatePattern(NotifierFactory factory) {
        printSectionTitle("3", "STATE & OBSERVER", "Gestion de estados y notificaciones del Scrim");
        
        System.out.println("[!] Creando nuevo Scrim (Partida de practica)...\n");
        
        // Crear scrim con estado inicial
        ScrimState estadoInicial = new EstadoBuscandoJugadores();
        Scrim scrim = new Scrim(estadoInicial);
        System.out.println("[*] Estado inicial: " + estadoInicial.getClass().getSimpleName());
        System.out.println();
        
        // Agregar notificadores (patron OBSERVER)
        System.out.println("[*] Suscribiendo observadores (notificadores) al Scrim:");
        scrim.addNotifier(factory.createEmailNotifier());
        scrim.addNotifier(factory.createDiscordNotifier());
        scrim.addNotifier(factory.createPushNotifier());
        System.out.println();
        
        // Crear context para gestionar el scrim
        ScrimContext context = new ScrimContext(scrim, estadoInicial);
        
        // Crear jugadores
        System.out.println("[!] Jugadores registrandose para Scrim 5v5:\n");
        
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
     * Demuestra el Patron STRATEGY - Matchmaking con diferentes estrategias
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
        
        System.out.println("[!] Matchmaking completado - Estrategia aplicada dinamicamente");
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
        
        // Transicion 1: Iniciar
        System.out.println("[*] ACCION: Iniciar scrim");
        System.out.println("   Todos los jugadores confirmaron su participacion");
        scrim.getEstado().iniciar(scrim);
        System.out.println("   [+] Nuevo estado: " + scrim.getEstado().getClass().getSimpleName());
        System.out.println();
        
        // Transicion 2: Iniciar nuevamente (partida en curso)
        System.out.println("[*] ACCION: La partida esta en curso");
        System.out.println("   Los jugadores estan compitiendo...");
        scrim.getEstado().iniciar(scrim);
        System.out.println("   [+] Estado: " + scrim.getEstado().getClass().getSimpleName());
        System.out.println();
        
        // Transicion 3: Intentar cancelar (desde EnJuego)
        System.out.println("[*] ACCION: Intentar cancelar partida en curso");
        System.out.println("   (No se puede cancelar una partida activa)");
        scrim.getEstado().cancelar(scrim);
        System.out.println("   [+] Estado final: " + scrim.getEstado().getClass().getSimpleName());

        System.out.println("\n[!] Transiciones de estado manejadas correctamente mediante patron STATE");
    }
    
    /**
     * Demuestra el patron BUILDER para creacion de Scrims
     */
    private static void demonstrateBuilderPattern() {
        printSectionTitle("6", "BUILDER", "Construccion fluida de objetos complejos");
        
        System.out.println("[!] Creando Scrims con diferentes configuraciones usando Builder...\n");
        
        // Scrim 1: Ranked competitivo
        System.out.println("[*] Scrim #1: Partida Ranked Competitiva");
        Scrim scrimRanked = new Scrim.Builder(new EstadoBuscandoJugadores())
            .juego("Valorant")
            .formato("5v5")
            .region("SA")
            .modalidad("ranked")
            .rangoMin(1500)
            .rangoMax(2000)
            .latenciaMax(50)
            .build();
        
        System.out.println("   " + scrimRanked);
        System.out.println();
        
        // Scrim 2: Casual relajado
        System.out.println("[*] Scrim #2: Partida Casual");
        Scrim scrimCasual = new Scrim.Builder(new EstadoBuscandoJugadores())
            .juego("League of Legends")
            .formato("5v5")
            .region("NA")
            .modalidad("casual")
            .rangoMin(0)
            .rangoMax(3000)
            .latenciaMax(100)
            .build();
        
        System.out.println("   " + scrimCasual);
        System.out.println();
        
        // Scrim 3: Torneo con restricciones estrictas
        System.out.println("[*] Scrim #3: Torneo Pro");
        Scrim scrimTorneo = new Scrim.Builder(new EstadoBuscandoJugadores())
            .juego("CS:GO")
            .formato("5v5")
            .region("EU")
            .modalidad("tournament")
            .rangoMin(2500)
            .rangoMax(3000)
            .latenciaMax(30)
            .build();
        
        System.out.println("   " + scrimTorneo);
        System.out.println();
        
        System.out.println("[!] Ventajas del Builder:");
        System.out.println("   • Construccion legible y fluida");
        System.out.println("   • Validaciones integradas");
        System.out.println("   • Parametros opcionales con valores por defecto");
        System.out.println("   • Codigo mas mantenible y extensible");
    }
    
    /**
     * Demuestra el patron COMPOSITE para grupos de notificaciones
     */
    private static void demonstrateCompositePattern() {
        printSectionTitle("7", "COMPOSITE", "Jerarquias de objetos con tratamiento uniforme");
        
        System.out.println("[!] Creando grupos de notificadores (Composite)...\n");
        
        // Crear notificadores individuales (leafs)
        interfaces.INotificationComponent email = new EmailNotifier();
        interfaces.INotificationComponent discord = new DiscordNotifier();
        interfaces.INotificationComponent push = new PushNotifier();
        
        System.out.println("[*] Notificadores individuales creados:");
        System.out.println("   • " + email.getName());
        System.out.println("   • " + discord.getName());
        System.out.println("   • " + push.getName());
        System.out.println();
        
        // Crear grupos (composites)
        NotificationGroup allChannels = new NotificationGroup("AllChannels");
        allChannels.add(email);
        allChannels.add(discord);
        allChannels.add(push);
        System.out.println();
        
        NotificationGroup criticalOnly = new NotificationGroup("CriticalOnly");
        criticalOnly.add(discord);
        criticalOnly.add(push);
        System.out.println();
        
        // Demostrar envio con composite
        System.out.println("[*] DEMOSTRACION: Enviando notificacion a todos los canales");
        Notificacion notif1 = new Notificacion("¡Scrim confirmado! La partida comienza en 5 minutos.");
        allChannels.sendNotification(notif1);
        System.out.println();
        
        System.out.println("[*] DEMOSTRACION: Enviando alerta critica solo a Discord y Push");
        Notificacion notif2 = new Notificacion("⚠️ ALERTA: Jugador desconectado, buscando reemplazo...");
        criticalOnly.sendNotification(notif2);
        System.out.println();
        
        System.out.println("[!] Ventajas del Composite:");
        System.out.println("   • Trata objetos individuales y grupos de forma uniforme");
        System.out.println("   • Permite crear jerarquias complejas de notificaciones");
        System.out.println("   • Facil agregar nuevos canales sin modificar codigo existente");
        System.out.println("   • Perfecto para configuraciones dinamicas (dev/prod/testing)");
    }

    /**
     * Demuestra el modelo de dominio completo
     */
    private static void demonstrateDomainModel(Scrim scrim) {
        printSectionTitle("8", "DOMAIN MODEL", "Equipos, Confirmaciones y Estadisticas");
        
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
        System.out.println("FORMACION DE EQUIPOS\n");
        
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
        System.out.println("[+] CONFIRMACIONES DE PARTICIPACION\n");
        
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
        
        // === ESTADiSTICAS ===
        System.out.println(LINE);
        System.out.println("[!] ESTADISTICAS POST-PARTIDA\n");
        
        // Datos de estadisticas mas realistas
        int[][] stats = {
            {18, 12, 7},   // ShadowBlade - MVP
            {14, 15, 9},   // PhoenixFire
            {10, 18, 12},  // IceQueen - Mas muertes
            {22, 10, 15},  // ThunderStrike - Mas kills
            {16, 14, 20},  // NightHawk - Mas asistencias
            {8, 16, 6},    // DragonSlayer
            {12, 13, 11},  // SilentAssassin
            {11, 14, 8}    // MysticWizard
        };
        
        Estadistica[] estadisticas = new Estadistica[8];
        
        System.out.println("[*] Rendimiento individual de jugadores:\n");
        
        // Crear y mostrar estadisticas
        for (int i = 0; i < 8; i++) {
            estadisticas[i] = new Estadistica(players[i], scrim, stats[i][0], stats[i][1], stats[i][2]);
        }
        
        // Mostrar tabla de estadisticas
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
        System.out.println("   [+] Calificacion de rendimiento por jugador:");
        for (int i = 0; i < 8; i++) {
            System.out.println("      • " + players[i].getUsername() + ": " + estadisticas[i].obtenerRendimiento());
        }
        System.out.println();
        
        // Encontrar MVP (usando el KDA numerico)
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
        
        System.out.println("[!] Modelo de dominio completo demostrado (Equipos, Confirmaciones, Estadisticas)");
    }
}
