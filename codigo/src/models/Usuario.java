package models;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * RF1: Modelo de Usuario completo
 *
 * Campos según especificación:
 * - Identificación: id, username, email
 * - Autenticación: passwordHash, tipoAuth, estadoEmail
 * - Perfil editable: juegoPrincipal, rangoPorJuego, rolesPreferidos, region, disponibilidad
 * - Sistema: notificaciones, rol temporal
 *
 * @see EstadoEmail
 * @see TipoAutenticacion
 */
public class Usuario {
    // ============================================
    // IDENTIFICACIÓN
    // ============================================
    private int id;
    private String username;
    private String email;

    // ============================================
    // AUTENTICACIÓN (RF1)
    // ============================================
    private String passwordHash;                // Hash bcrypt del password
    private TipoAutenticacion tipoAuth;         // LOCAL, STEAM, RIOT, DISCORD
    private EstadoEmail estadoEmail;            // PENDIENTE, VERIFICADO

    // ============================================
    // PERFIL EDITABLE (RF1)
    // ============================================
    private String juegoPrincipal;              // ej: "Valorant", "League of Legends"
    private Map<String, Integer> rangoPorJuego; // ej: {"Valorant": 1500, "LoL": 1200}
    private List<String> rolesPreferidos;       // ej: ["Duelist", "Controller"]
    private String region;                      // ej: "SA", "NA", "EU"
    private String disponibilidadHoraria;       // ej: "18:00-23:00 UTC-3"

    // ============================================
    // SISTEMA
    // ============================================
    private String rol;                         // Rol asignado temporalmente en scrim actual
    private List<Notificacion> notificaciones;  // Notificaciones recibidas
    
    // ============================================
    // SISTEMA DE SANCIONES (RF4)
    // ============================================
    private int sancionesActivas;               // Número de sanciones activas
    private java.time.LocalDateTime banHasta;   // Fecha hasta la cual está baneado (null si no está baneado)

    // ============================================
    // CONSTRUCTORES
    // ============================================

    /**
     * Constructor completo para nuevo registro
     */
    public Usuario(int id, String username, String email, String passwordHash,
                  TipoAutenticacion tipoAuth) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.tipoAuth = tipoAuth;
        this.estadoEmail = EstadoEmail.PENDIENTE; // Por defecto pendiente

        // Inicializar colecciones
        this.rangoPorJuego = new HashMap<>();
        this.rolesPreferidos = new ArrayList<>();
        this.notificaciones = new ArrayList<>();

        // Valores por defecto
        this.rol = null;
        this.region = "SA"; // Default South America
        this.disponibilidadHoraria = "18:00-23:00 UTC-3"; // Default
        
        // Sistema de sanciones
        this.sancionesActivas = 0;
        this.banHasta = null;
    }

    /**
     * Constructor simplificado (para compatibilidad con código existente)
     */
    public Usuario(int id, String username, String email) {
        this(id, username, email, "default_hash", TipoAutenticacion.LOCAL);
    }

    // ============================================
    // GETTERS Y SETTERS - IDENTIFICACIÓN
    // ============================================

    public int getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // ============================================
    // GETTERS Y SETTERS - AUTENTICACIÓN
    // ============================================

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public TipoAutenticacion getTipoAuth() { return tipoAuth; }
    public void setTipoAuth(TipoAutenticacion tipoAuth) { this.tipoAuth = tipoAuth; }

    public EstadoEmail getEstadoEmail() { return estadoEmail; }
    public void setEstadoEmail(EstadoEmail estadoEmail) { this.estadoEmail = estadoEmail; }

    /**
     * Verifica el email del usuario
     */
    public void verificarEmail() {
        this.estadoEmail = EstadoEmail.VERIFICADO;
    }

    /**
     * Verifica si el email está verificado
     */
    public boolean isEmailVerificado() {
        return this.estadoEmail == EstadoEmail.VERIFICADO;
    }

    // ============================================
    // GETTERS Y SETTERS - PERFIL
    // ============================================

    public String getJuegoPrincipal() { return juegoPrincipal; }
    public void setJuegoPrincipal(String juegoPrincipal) { this.juegoPrincipal = juegoPrincipal; }

    public Map<String, Integer> getRangoPorJuego() { return rangoPorJuego; }
    public void setRangoPorJuego(Map<String, Integer> rangoPorJuego) { this.rangoPorJuego = rangoPorJuego; }

    public List<String> getRolesPreferidos() { return rolesPreferidos; }
    public void setRolesPreferidos(List<String> rolesPreferidos) { this.rolesPreferidos = rolesPreferidos; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getDisponibilidadHoraria() { return disponibilidadHoraria; }
    public void setDisponibilidadHoraria(String disponibilidadHoraria) {
        this.disponibilidadHoraria = disponibilidadHoraria;
    }

    // ============================================
    // GETTERS Y SETTERS - SISTEMA
    // ============================================

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public void agregarNotificacion(Notificacion notificacion) {
        this.notificaciones.add(notificacion);
    }

    public List<Notificacion> getNotificaciones() {
        return notificaciones;
    }

    // ============================================
    // MÉTODOS DE UTILIDAD
    // ============================================

    /**
     * Obtiene rango para un juego específico o 0 si no está configurado
     */
    public int getRangoParaJuego(String juego) {
        return rangoPorJuego.getOrDefault(juego, 0);
    }

    /**
     * Agrega un rol a las preferencias si no existe
     */
    public void agregarRolPreferido(String rol) {
        if (!rolesPreferidos.contains(rol)) {
            rolesPreferidos.add(rol);
        }
    }

    // ============================================
    // SISTEMA DE SANCIONES
    // ============================================

    /**
     * Agrega una sanción al usuario por rechazar una confirmación
     */
    public void agregarSancion() {
        this.sancionesActivas++;
        
        // Calcular tiempo de ban según número de sanciones
        int minutosBan = calcularTiempoBan();
        this.banHasta = java.time.LocalDateTime.now().plusMinutes(minutosBan);
        
        System.out.println("⚠️ Usuario " + username + " sancionado. Sanciones totales: " + sancionesActivas);
        System.out.println("   Baneado hasta: " + banHasta);
    }

    /**
     * Calcula el tiempo de ban en minutos según el número de sanciones
     * Escalado progresivo: 5min, 15min, 30min, 60min, etc.
     */
    private int calcularTiempoBan() {
        switch (sancionesActivas) {
            case 1: return 5;   // Primera sanción: 5 minutos
            case 2: return 15;  // Segunda: 15 minutos
            case 3: return 30;  // Tercera: 30 minutos
            case 4: return 60;  // Cuarta: 1 hora
            default: return 120; // 5+ sanciones: 2 horas
        }
    }

    /**
     * Verifica si el usuario está actualmente baneado
     */
    public boolean estaBaneado() {
        if (banHasta == null) return false;
        
        boolean baneado = java.time.LocalDateTime.now().isBefore(banHasta);
        
        // Si el ban expiró, limpiar
        if (!baneado) {
            banHasta = null;
        }
        
        return baneado;
    }

    /**
     * Obtiene el tiempo restante del ban en minutos
     */
    public long getMinutosRestantesBan() {
        if (banHasta == null || !estaBaneado()) return 0;
        
        return java.time.Duration.between(
            java.time.LocalDateTime.now(), 
            banHasta
        ).toMinutes();
    }

    /**
     * Limpia todas las sanciones (admin action)
     */
    public void limpiarSanciones() {
        this.sancionesActivas = 0;
        this.banHasta = null;
        System.out.println("✅ Sanciones limpiadas para usuario: " + username);
    }

    public int getSancionesActivas() {
        return sancionesActivas;
    }

    public java.time.LocalDateTime getBanHasta() {
        return banHasta;
    }

    @Override
    public String toString() {
        return String.format("Usuario[%s | %s | %s | Juego: %s | Región: %s | Email: %s]",
            id, username, email, juegoPrincipal, region, estadoEmail);
    }
}

