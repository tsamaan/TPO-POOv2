package controllers;

import models.*;
import views.*;
import service.UserService;
import auth.AuthService;
import auth.AuthController;
import java.util.Map;
import java.util.Optional;

/**
 * CAPA CONTROLLER (MVC) - Gestión de usuarios COMPLETO (RF1)
 *
 * Responsabilidades:
 * - RF1: Registro de nuevos usuarios
 * - RF1: Login y autenticación
 * - RF1: Ver perfil de usuario
 * - RF1: Editar perfil (juego, rango, roles, región, disponibilidad)
 * - Configurar rango de usuario por juego
 * - Seleccionar roles
 *
 * @pattern MVC - Controller Layer
 */
public class UserController {

    private ConsoleView consoleView;
    private MenuView menuView;
    private AuthView authView;
    private ProfileView profileView;
    private UserService userService;
    private AuthService authService;
    private AuthController authController;

    public UserController(ConsoleView consoleView, MenuView menuView,
                         AuthView authView, ProfileView profileView,
                         UserService userService) {
        this.consoleView = consoleView;
        this.menuView = menuView;
        this.authView = authView;
        this.profileView = profileView;
        this.userService = userService;
        this.authService = new AuthService();
        this.authController = new AuthController(authService);
    }

    // ============================================
    // REGISTRO (RF1 - NUEVO)
    // ============================================

    /**
     * RF1: Maneja el proceso completo de registro de nuevo usuario
     */
    public Usuario registrar() {
        // Capturar datos de registro
        AuthView.DatosRegistro datos = authView.solicitarDatosRegistro();

        // Validaciones
        if (!userService.validarEmail(datos.email)) {
            authView.mostrarErrorRegistro("Email inválido");
            return null;
        }

        if (!userService.validarPassword(datos.password)) {
            authView.mostrarErrorRegistro("Password debe tener al menos 6 caracteres");
            return null;
        }

        try {
            // Registrar usuario en UserService
            Usuario nuevoUsuario = userService.registrarUsuario(
                datos.username,
                datos.email,
                datos.password,
                datos.tipoAuth
            );

            // Mostrar éxito
            authView.mostrarRegistroExitoso(nuevoUsuario.getUsername());

            // Proceso de verificación de email
            userService.enviarEmailVerificacion(nuevoUsuario);
            authView.mostrarVerificacionEmail(nuevoUsuario.getEmail(), true);

            // Configuración inicial del perfil
            configurarPerfilInicial(nuevoUsuario);

            return nuevoUsuario;

        } catch (IllegalArgumentException e) {
            authView.mostrarErrorRegistro(e.getMessage());
            return null;
        }
    }

    /**
     * Configura perfil inicial después del registro
     */
    private void configurarPerfilInicial(Usuario usuario) {
        consoleView.mostrarInfo("Configuración inicial de perfil:");
        System.out.println();

        // Juego principal
        String juego = profileView.solicitarJuegoPrincipal();
        usuario.setJuegoPrincipal(juego);

        // Rango para ese juego
        int rango = profileView.solicitarRangoParaJuego(juego);
        usuario.getRangoPorJuego().put(juego, rango);

        // Región
        String region = profileView.solicitarRegion();
        usuario.setRegion(region);

        consoleView.mostrarExito("Perfil inicial configurado");
        consoleView.pausaVisual();
    }

    // ============================================
    // LOGIN (RF1 - MEJORADO)
    // ============================================

    /**
     * RF1: Maneja el proceso completo de login
     */
    public Usuario login() {
        // Capturar credenciales
        AuthView.DatosLogin datos = authView.solicitarDatosLogin();

        // Autenticar usando UserService
        Optional<Usuario> usuarioOpt = userService.autenticarUsuario(datos.email, datos.password);

        if (!usuarioOpt.isPresent()) {
            authView.mostrarErrorLogin("Email o contraseña incorrectos");
            return null;
        }

        Usuario usuario = usuarioOpt.get();

        // Verificar si email está verificado
        if (!usuario.isEmailVerificado()) {
            consoleView.mostrarAdvertencia("Tu email no está verificado");
            consoleView.mostrarInfo("Verificando automáticamente...");
            userService.verificarEmail(usuario);
        }

        // Mostrar bienvenida
        authView.mostrarLoginExitoso(usuario.getUsername());
        System.out.println();

        return usuario;
    }

    /**
     * Solicita username con validación
     */
    private String solicitarUsername() {
        String username = "";
        while (username.isEmpty()) {
            username = consoleView.solicitarInput("Ingresa tu nombre de usuario");
            if (username.isEmpty()) {
                consoleView.mostrarError("El nombre de usuario no puede estar vacío. Intenta nuevamente.");
            }
        }
        return username;
    }

    /**
     * Solicita email con validación
     */
    private String solicitarEmail() {
        String email = "";
        while (email.isEmpty()) {
            email = consoleView.solicitarInput("Ingresa tu email");
            if (email.isEmpty()) {
                consoleView.mostrarError("El email no puede estar vacío. Intenta nuevamente.");
            }
        }
        return email;
    }

    /**
     * Solicita password con validación
     */
    private String solicitarPassword() {
        String password = "";
        while (password.isEmpty()) {
            password = consoleView.solicitarInput("Ingresa tu contraseña");
            if (password.isEmpty()) {
                consoleView.mostrarError("La contraseña no puede estar vacía. Intenta nuevamente.");
            }
        }
        return password;
    }

    // ============================================
    // CONFIGURACIÓN DE RANGO
    // ============================================

    /**
     * Configura el rango del usuario para un juego específico
     * Retorna el rango configurado
     */
    public int configurarRango(Usuario usuario, String juego) {
        Map<String, Integer> rangos = usuario.getRangoPorJuego();

        // Si ya tiene rango, retornarlo
        if (rangos.containsKey(juego)) {
            int rangoExistente = rangos.get(juego);
            consoleView.mostrarInfo("Tu rango en " + juego + ": " + rangoExistente);
            return rangoExistente;
        }

        // Solicitar configuración de rango
        consoleView.mostrarInfo("No tienes rango configurado para " + juego);
        int rangoNuevo = consoleView.solicitarNumero("Ingresa tu rango", 0, 3000);

        // Guardar rango
        usuario.getRangoPorJuego().put(juego, rangoNuevo);
        consoleView.mostrarExito("Rango configurado: " + rangoNuevo);

        return rangoNuevo;
    }

    /**
     * Obtiene el rango del usuario para un juego (con manejo de default)
     */
    public int obtenerRango(Usuario usuario, String juego) {
        if (!usuario.getRangoPorJuego().containsKey(juego)) {
            // Configurar rango por defecto si no existe
            int rangoDefault = 1000;
            usuario.getRangoPorJuego().put(juego, rangoDefault);
            consoleView.mostrarAdvertencia("Usando rango 1000 por defecto para " + juego);
            return rangoDefault;
        }
        return usuario.getRangoPorJuego().get(juego);
    }

    // ============================================
    // SELECCIÓN DE ROL
    // ============================================

    /**
     * Permite al usuario seleccionar su rol para un juego
     */
    public String seleccionarRol(String juego) {
        return menuView.seleccionarRol(juego);
    }

    // ============================================
    // VALIDACIONES
    // ============================================

    /**
     * Valida si el usuario cumple requisitos de rango para un scrim
     */
    public boolean validarRangoParaScrim(Usuario usuario, String juego, int rangoMin, int rangoMax) {
        if (!usuario.getRangoPorJuego().containsKey(juego)) {
            return false;
        }

        int rangoUsuario = usuario.getRangoPorJuego().get(juego);
        return rangoUsuario >= rangoMin && rangoUsuario <= rangoMax;
    }

    /**
     * Muestra información del usuario
     */
    public void mostrarInfoUsuario(Usuario usuario) {
        consoleView.mostrarInfo("Usuario: " + usuario.getUsername());
        consoleView.mostrarInfo("Email: " + usuario.getEmail());

        if (!usuario.getRangoPorJuego().isEmpty()) {
            consoleView.mostrarInfo("Rangos configurados:");
            for (Map.Entry<String, Integer> entry : usuario.getRangoPorJuego().entrySet()) {
                System.out.println("  • " + entry.getKey() + ": " + entry.getValue());
            }
        }
    }

    // ============================================
    // GESTIÓN DE PERFIL (RF1 - NUEVO)
    // ============================================

    /**
     * RF1: Muestra el perfil completo del usuario
     */
    public void verPerfil(Usuario usuario) {
        profileView.mostrarPerfil(usuario);
        consoleView.pausar();
    }

    /**
     * RF1: Permite editar el perfil del usuario
     */
    public void editarPerfil(Usuario usuario) {
        boolean editando = true;

        while (editando) {
            int opcion = profileView.mostrarMenuEditarPerfil();

            switch (opcion) {
                case 1:
                    // Cambiar juego principal
                    String juego = profileView.solicitarJuegoPrincipal();
                    usuario.setJuegoPrincipal(juego);
                    profileView.mostrarCambioRealizado("Juego principal", juego);
                    break;

                case 2:
                    // Cambiar región
                    String region = profileView.solicitarRegion();
                    usuario.setRegion(region);
                    profileView.mostrarCambioRealizado("Región", region);
                    break;

                case 3:
                    // Configurar roles preferidos
                    String juegoRoles = usuario.getJuegoPrincipal() != null ?
                                       usuario.getJuegoPrincipal() : "Valorant";
                    String[] roles = profileView.solicitarRolesPreferidos(juegoRoles);

                    usuario.getRolesPreferidos().clear();
                    for (String rol : roles) {
                        usuario.agregarRolPreferido(rol);
                    }

                    profileView.mostrarCambioRealizado("Roles preferidos",
                                                      String.join(", ", roles));
                    break;

                case 4:
                    // Configurar disponibilidad
                    String disponibilidad = profileView.solicitarDisponibilidad();
                    usuario.setDisponibilidadHoraria(disponibilidad);
                    profileView.mostrarCambioRealizado("Disponibilidad", disponibilidad);
                    break;

                case 5:
                    // Configurar rango para un juego
                    String juegoRango = profileView.solicitarJuegoPrincipal();
                    int rango = profileView.solicitarRangoParaJuego(juegoRango);
                    userService.actualizarRango(usuario, juegoRango, rango);
                    profileView.mostrarCambioRealizado("Rango " + juegoRango,
                                                      String.valueOf(rango));
                    break;

                case 6:
                    // Volver
                    profileView.mostrarVolviendoMenu();
                    editando = false;
                    break;
            }
        }
    }
}

