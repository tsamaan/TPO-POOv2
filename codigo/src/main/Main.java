package main;

import models.Usuario;
import controllers.*;
import views.*;
import service.UserService;

/**
 * MAIN - Punto de entrada refactorizado con arquitectura MVC
 *
 * RF1 COMPLETO:
 * - Registro de usuarios (con verificación de email)
 * - Login con autenticación
 * - Ver perfil de usuario
 * - Editar perfil (juego, rango, roles, región, disponibilidad)
 *
 * Flujo:
 * 1. Menú inicial: Login / Registro / Salir
 * 2. Si Login → Validar credenciales → Dashboard
 * 3. Si Registro → Crear usuario → Verificar email → Configurar perfil → Dashboard
 * 4. Dashboard: Juego Rápido / Buscar Salas / Ver Perfil / Editar Perfil / Salir
 *
 * @pattern MVC - Main Orchestrator
 */
public class Main {

    public static void main(String[] args) {
        // Inicializar capas MVC
        ConsoleView consoleView = new ConsoleView();
        MenuView menuView = new MenuView(consoleView);
        GameView gameView = new GameView(consoleView);
        AuthView authView = new AuthView(consoleView);
        ProfileView profileView = new ProfileView(consoleView);

        // Inicializar services
        UserService userService = new UserService();

        // Crear usuarios de prueba (para testing)
        userService.crearUsuariosPrueba();

        // Inicializar controllers con dependencias
        UserController userController = new UserController(consoleView, menuView,
                                                          authView, profileView, userService);
        ScrimController scrimController = new ScrimController(consoleView, menuView, gameView);
        MatchmakingController matchmakingController = new MatchmakingController(
            consoleView, menuView, gameView, scrimController
        );

        // Mostrar header
        consoleView.mostrarHeader();

        // Main loop - Aplicación completa con autenticación
        boolean appRunning = true;
        
        while (appRunning) {
            // Menú inicial: Login / Registro
            Usuario usuarioActual = menuInicialAuth(userController, authView, consoleView);

            if (usuarioActual == null) {
                // Usuario eligió "Salir" en el menú inicial
                consoleView.mostrarInfo("Saliendo de eScrims...");
                appRunning = false;
                continue;
            }

            // Dashboard - Loop interno para usuario autenticado
            boolean sesionActiva = true;
            while (sesionActiva) {
                int opcion = menuView.mostrarMenuPrincipal(usuarioActual);

                switch (opcion) {
                    case 1:
                        // Juego Rápido (Matchmaking automático)
                        matchmakingController.juegoRapido(usuarioActual, userController);
                        break;

                    case 2:
                        // Buscar Salas Disponibles
                        scrimController.buscarSalasDisponibles(usuarioActual, userController);
                        break;

                    case 3:
                        // Ver Mi Perfil
                        userController.verPerfil(usuarioActual);
                        break;

                    case 4:
                        // Editar Perfil
                        userController.editarPerfil(usuarioActual);
                        break;

                    case 5:
                        // Cerrar Sesión
                        consoleView.mostrarInfo("\n[!] Cerrando sesión de " + usuarioActual.getUsername() + "...");
                        consoleView.mostrarInfo("[+] Sesión cerrada exitosamente.\n");
                        sesionActiva = false; // Volver al menú de login/registro
                        break;

                    case 6:
                        // Salir de la aplicación
                        menuView.mostrarDespedida(usuarioActual.getUsername());
                        sesionActiva = false;
                        appRunning = false;
                        break;

                    default:
                        consoleView.mostrarError("Opción inválida. Intenta nuevamente.");
                        break;
                }
            }
        }

        // Cleanup
        consoleView.cerrarScanner();
    }

    /**
     * Menú inicial: Login o Registro
     */
    private static Usuario menuInicialAuth(UserController userController,
                                           AuthView authView,
                                           ConsoleView consoleView) {
        Usuario usuario = null;
        boolean autenticado = false;

        while (!autenticado) {
            int opcion = authView.mostrarMenuInicial();

            switch (opcion) {
                case 1:
                    // Login
                    usuario = userController.login();
                    if (usuario != null) {
                        autenticado = true;
                    }
                    break;

                case 2:
                    // Registro
                    usuario = userController.registrar();
                    if (usuario != null) {
                        autenticado = true;
                    }
                    break;

                case 3:
                    // Salir
                    return null;

                default:
                    consoleView.mostrarError("Opción inválida");
                    break;
            }
        }

        return usuario;
    }
}

