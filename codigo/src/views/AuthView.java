package views;

import models.TipoAutenticacion;

/**
 * CAPA VIEW (MVC) - Vista de autenticación
 *
 * Responsable de:
 * - Mostrar menú inicial (Login/Registro/Salir)
 * - Formulario de registro
 * - Formulario de login
 * - Mensajes de verificación de email
 *
 * @pattern MVC - View Layer (Auth Specialist)
 */
public class AuthView {

    private ConsoleView consoleView;

    public AuthView(ConsoleView consoleView) {
        this.consoleView = consoleView;
    }

    // ============================================
    // MENÚ INICIAL
    // ============================================

    /**
     * Muestra menú inicial (antes de autenticarse)
     * @return 1=Login, 2=Registro, 3=Salir
     */
    public int mostrarMenuInicial() {
        consoleView.mostrarTitulo("BIENVENIDO A eSCRIMS PLATFORM");

        System.out.println("[1] Iniciar Sesión (Login)");
        System.out.println("[2] Registrarse (Crear cuenta)");
        System.out.println("[3] Salir");
        System.out.println();

        return consoleView.solicitarNumero("Selecciona una opción", 1, 3);
    }

    // ============================================
    // REGISTRO
    // ============================================

    /**
     * Muestra formulario de registro y captura datos
     */
    public DatosRegistro solicitarDatosRegistro() {
        consoleView.mostrarSubtitulo("REGISTRO DE NUEVO USUARIO");

        // Datos básicos
        String username = consoleView.solicitarInput("Nombre de usuario");
        String email = consoleView.solicitarInput("Email");
        String password = consoleView.solicitarInput("Contraseña (mínimo 6 caracteres)");

        // Tipo de autenticación (por ahora solo LOCAL)
        TipoAutenticacion tipoAuth = TipoAutenticacion.LOCAL;
        consoleView.mostrarInfo("Tipo de autenticación: LOCAL (email/password)");

        return new DatosRegistro(username, email, password, tipoAuth);
    }

    /**
     * Solicita tipo de autenticación
     * 
     * @deprecated Actualmente solo se usa LOCAL. Este método está preparado
     *             para futuras implementaciones de OAuth (Steam, Riot, Discord, Google)
     */
    @Deprecated
    private TipoAutenticacion solicitarTipoAuth() {
        System.out.println();
        consoleView.mostrarInfo("Tipo de autenticación:");
        System.out.println("  [1] Local (usuario y contraseña)");
        System.out.println("  [2] Steam (OAuth) - No disponible");
        System.out.println("  [3] Riot Games (OAuth) - No disponible");
        System.out.println("  [4] Discord (OAuth) - No disponible");

        int opcion = consoleView.solicitarNumero("Selecciona tipo de autenticación", 1, 4);

        switch (opcion) {
            case 1: return TipoAutenticacion.LOCAL;
            case 2: return TipoAutenticacion.STEAM;
            case 3: return TipoAutenticacion.RIOT;
            case 4: return TipoAutenticacion.DISCORD;
            default: return TipoAutenticacion.LOCAL;
        }
    }

    /**
     * Muestra mensaje de registro exitoso
     */
    public void mostrarRegistroExitoso(String username) {
        consoleView.mostrarExito("¡Registro exitoso!");
        consoleView.mostrarInfo("Usuario creado: " + username);
    }

    /**
     * Muestra mensaje de verificación de email
     */
    public void mostrarVerificacionEmail(String email, boolean autoVerificar) {
        System.out.println();
        consoleView.mostrarInfo("Verificación de email:");
        System.out.println("  Se ha enviado un email de verificación a: " + email);

        if (autoVerificar) {
            consoleView.mostrarInfo("  (Simulando verificación automática para demo...)");
            consoleView.delay(1000);
            consoleView.mostrarExito("  ✓ Email verificado correctamente");
        } else {
            consoleView.mostrarAdvertencia("  Por favor verifica tu email antes de continuar");
        }
        System.out.println();
    }

    /**
     * Muestra error de registro
     */
    public void mostrarErrorRegistro(String mensaje) {
        consoleView.mostrarError("Error en registro: " + mensaje);
    }

    // ============================================
    // LOGIN
    // ============================================

    /**
     * Muestra formulario de login y captura credenciales
     */
    public DatosLogin solicitarDatosLogin() {
        consoleView.mostrarSubtitulo("INICIAR SESIÓN");

        String email = consoleView.solicitarInput("Email");
        String password = consoleView.solicitarInput("Contraseña");

        return new DatosLogin(email, password);
    }

    /**
     * Muestra mensaje de login exitoso
     */
    public void mostrarLoginExitoso(String username) {
        consoleView.mostrarExito("¡Bienvenido de vuelta, " + username + "!");
    }

    /**
     * Muestra error de login
     */
    public void mostrarErrorLogin(String mensaje) {
        consoleView.mostrarError("Error de autenticación: " + mensaje);
        consoleView.mostrarInfo("Por favor verifica tus credenciales e intenta nuevamente.");
    }

    // ============================================
    // CLASES INTERNAS (DTOs)
    // ============================================

    /**
     * DTO para datos de registro
     */
    public static class DatosRegistro {
        public final String username;
        public final String email;
        public final String password;
        public final TipoAutenticacion tipoAuth;

        public DatosRegistro(String username, String email, String password,
                           TipoAutenticacion tipoAuth) {
            this.username = username;
            this.email = email;
            this.password = password;
            this.tipoAuth = tipoAuth;
        }
    }

    /**
     * DTO para datos de login
     */
    public static class DatosLogin {
        public final String email;
        public final String password;

        public DatosLogin(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }
}
