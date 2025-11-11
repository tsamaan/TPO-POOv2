package service;

import models.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * RF1: Servicio de gestión de usuarios
 *
 * Responsabilidades:
 * - Registro de nuevos usuarios
 * - Autenticación de usuarios existentes
 * - Gestión de perfiles
 * - Verificación de email
 * - Almacenamiento in-memory de usuarios
 *
 * @pattern Service Layer (MVC)
 */
public class UserService {

    // "Base de datos" in-memory de usuarios
    private List<Usuario> usuarios;
    private int nextId;

    public UserService() {
        this.usuarios = new ArrayList<>();
        this.nextId = 1;
    }

    // ============================================
    // REGISTRO (RF1)
    // ============================================

    /**
     * Registra un nuevo usuario en el sistema
     *
     * @param username Nombre de usuario
     * @param email Email del usuario
     * @param password Password en texto plano (se hashea internamente)
     * @param tipoAuth Tipo de autenticación (LOCAL, STEAM, RIOT, DISCORD)
     * @return Usuario creado
     * @throws IllegalArgumentException si email ya existe
     */
    public Usuario registrarUsuario(String username, String email, String password,
                                    TipoAutenticacion tipoAuth) {

        // Validar que email no exista
        if (existeEmail(email)) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Validar que username no exista
        if (existeUsername(username)) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso");
        }

        // Hashear password
        String passwordHash = hashPassword(password);

        // Crear usuario
        Usuario nuevoUsuario = new Usuario(nextId++, username, email, passwordHash, tipoAuth);

        // Agregar a lista
        usuarios.add(nuevoUsuario);

        System.out.println("[USER SERVICE] Usuario registrado: " + username +
                         " (ID: " + nuevoUsuario.getId() + ")");

        return nuevoUsuario;
    }

    // ============================================
    // AUTENTICACIÓN (RF2)
    // ============================================

    /**
     * Autentica un usuario con email y password
     *
     * @param email Email del usuario
     * @param password Password en texto plano
     * @return Optional<Usuario> si autenticación exitosa, Optional.empty() si falla
     */
    public Optional<Usuario> autenticarUsuario(String email, String password) {
        // Buscar usuario por email
        Optional<Usuario> usuarioOpt = buscarPorEmail(email);

        if (!usuarioOpt.isPresent()) {
            System.out.println("[USER SERVICE] Usuario no encontrado: " + email);
            return Optional.empty();
        }

        Usuario usuario = usuarioOpt.get();

        // Verificar password
        String passwordHash = hashPassword(password);
        if (!usuario.getPasswordHash().equals(passwordHash)) {
            System.out.println("[USER SERVICE] Password incorrecto para: " + email);
            return Optional.empty();
        }

        System.out.println("[USER SERVICE] Autenticación exitosa: " + usuario.getUsername());
        return Optional.of(usuario);
    }

    // ============================================
    // VERIFICACIÓN DE EMAIL (RF1)
    // ============================================

    /**
     * Verifica el email de un usuario
     */
    public void verificarEmail(Usuario usuario) {
        usuario.verificarEmail();
        System.out.println("[USER SERVICE] Email verificado para: " + usuario.getUsername());
    }

    /**
     * Simula envío de email de verificación
     */
    public void enviarEmailVerificacion(Usuario usuario) {
        System.out.println("[USER SERVICE] Email de verificación enviado a: " + usuario.getEmail());
        System.out.println("                (En producción se enviaría link real)");

        // Para demo, auto-verificar después de 1 segundo
        System.out.println("                Simulando verificación automática...");
        verificarEmail(usuario);
    }

    // ============================================
    // GESTIÓN DE PERFIL (RF1)
    // ============================================

    /**
     * Actualiza el perfil de un usuario
     */
    public void actualizarPerfil(Usuario usuario, String campo, String valor) {
        switch (campo.toLowerCase()) {
            case "juego":
            case "juego_principal":
                usuario.setJuegoPrincipal(valor);
                break;
            case "region":
            case "servidor":
                usuario.setRegion(valor);
                break;
            case "disponibilidad":
            case "horario":
                usuario.setDisponibilidadHoraria(valor);
                break;
            default:
                throw new IllegalArgumentException("Campo desconocido: " + campo);
        }

        System.out.println("[USER SERVICE] Perfil actualizado: " + campo + " = " + valor);
    }

    /**
     * Actualiza el rango de un usuario para un juego
     */
    public void actualizarRango(Usuario usuario, String juego, int rango) {
        usuario.getRangoPorJuego().put(juego, rango);
        System.out.println("[USER SERVICE] Rango actualizado: " + juego + " = " + rango);
    }

    /**
     * Agrega un rol preferido al perfil
     */
    public void agregarRolPreferido(Usuario usuario, String rol) {
        usuario.agregarRolPreferido(rol);
        System.out.println("[USER SERVICE] Rol preferido agregado: " + rol);
    }

    // ============================================
    // BÚSQUEDA Y VALIDACIÓN
    // ============================================

    /**
     * Busca un usuario por email
     */
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarios.stream()
            .filter(u -> u.getEmail().equalsIgnoreCase(email))
            .findFirst();
    }

    /**
     * Busca un usuario por username
     */
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarios.stream()
            .filter(u -> u.getUsername().equalsIgnoreCase(username))
            .findFirst();
    }

    /**
     * Verifica si existe un email
     */
    public boolean existeEmail(String email) {
        return buscarPorEmail(email).isPresent();
    }

    /**
     * Verifica si existe un username
     */
    public boolean existeUsername(String username) {
        return buscarPorUsername(username).isPresent();
    }

    /**
     * Obtiene todos los usuarios registrados
     */
    public List<Usuario> obtenerTodos() {
        return new ArrayList<>(usuarios);
    }

    /**
     * Obtiene cantidad de usuarios registrados
     */
    public int contarUsuarios() {
        return usuarios.size();
    }

    // ============================================
    // UTILIDADES DE SEGURIDAD
    // ============================================

    /**
     * Hashea un password usando SHA-256
     * (En producción usar bcrypt, esta es versión simplificada)
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            // Fallback simple si SHA-256 no disponible
            return "hash_" + password.hashCode();
        }
    }

    /**
     * Valida formato de email (validación básica)
     */
    public boolean validarEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    /**
     * Valida fortaleza de password (validación básica)
     */
    public boolean validarPassword(String password) {
        return password != null && password.length() >= 6;
    }

    // ============================================
    // DATOS DE PRUEBA
    // ============================================

    /**
     * Crea usuarios de prueba para testing
     */
    public void crearUsuariosPrueba() {
        // Usuario 1: Jugador de Valorant
        Usuario u1 = registrarUsuario("ShadowBlade", "shadow@escrims.com",
                                      "password123", TipoAutenticacion.LOCAL);
        u1.setJuegoPrincipal("Valorant");
        u1.getRangoPorJuego().put("Valorant", 1500);
        u1.agregarRolPreferido("Duelist");
        u1.agregarRolPreferido("Controller");
        u1.setRegion("SA");
        u1.verificarEmail();

        // Usuario 2: Jugador de LoL
        Usuario u2 = registrarUsuario("PhoenixFire", "phoenix@escrims.com",
                                      "password456", TipoAutenticacion.LOCAL);
        u2.setJuegoPrincipal("League of Legends");
        u2.getRangoPorJuego().put("League of Legends", 1200);
        u2.agregarRolPreferido("Mid");
        u2.agregarRolPreferido("Support");
        u2.setRegion("NA");
        u2.verificarEmail();

        System.out.println("[USER SERVICE] 2 usuarios de prueba creados");
    }
}
