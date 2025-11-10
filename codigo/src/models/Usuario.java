package models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Usuario {
    // Identificación y autenticación (RF1)
    private int id;
    private String username;
    private String email;
    private String passwordHash; // hash de la contraseña (nunca guardar en texto plano)
    
    // Estado de verificación (RF1 - Verificación de email)
    private EstadoVerificacion estadoVerificacion;
    private LocalDateTime fechaVerificacion;
    
    // Perfil editable (RF1)
    private String juegoPrincipal; // ej: "League of Legends"
    private Map<String, Integer> rangoPorJuego; // ej: {"LoL": 1200, "Valorant": 800}
    private List<String> rolesPreferidos; // ej: ["Mid", "Support"]
    private String region; // ej: "LAS", "NA", "EUW"
    private String servidor; // ej: "LAS1", "NA1"
    
    // Disponibilidad horaria (RF1)
    private Map<String, String> disponibilidadHoraria; // ej: {"Lunes": "18:00-23:00"}
    
    // Preferencias de búsqueda (RF2)
    private List<String> busquedasFavoritas; // JSON serializado de filtros
    
    // OAuth (opcional RF1)
    private String steamId;
    private String riotId;
    private String discordId;
    
    // Estadísticas y moderación (RF8, RF9)
    private int totalPartidas;
    private int abandonos;
    private int strikes; // penalizaciones
    private LocalDateTime cooldownHasta; // hasta cuándo está sancionado
    private double rating; // promedio de valoraciones
    
    // Notificaciones recibidas (RF7)
    private List<Notificacion> notificaciones;
    
    // Constructor completo
    public Usuario(int id, String username, String email, String passwordHash) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.estadoVerificacion = EstadoVerificacion.PENDIENTE;
        this.rangoPorJuego = new HashMap<>();
        this.rolesPreferidos = new ArrayList<>();
        this.disponibilidadHoraria = new HashMap<>();
        this.busquedasFavoritas = new ArrayList<>();
        this.notificaciones = new ArrayList<>();
        this.totalPartidas = 0;
        this.abandonos = 0;
        this.strikes = 0;
        this.rating = 0.0;
    }
    
    // Constructor simple (retrocompatibilidad)
    public Usuario(int id, String username, String email) {
        this(id, username, email, null);
    }

    // Getters básicos
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    
    // Verificación de email (RF1)
    public EstadoVerificacion getEstadoVerificacion() { return estadoVerificacion; }
    public void verificarEmail() {
        this.estadoVerificacion = EstadoVerificacion.VERIFICADO;
        this.fechaVerificacion = LocalDateTime.now();
    }
    public boolean estaVerificado() {
        return estadoVerificacion == EstadoVerificacion.VERIFICADO;
    }
    
    // Perfil editable (RF1)
    public String getJuegoPrincipal() { return juegoPrincipal; }
    public void setJuegoPrincipal(String juego) { this.juegoPrincipal = juego; }
    
    public Map<String, Integer> getRangoPorJuego() { return rangoPorJuego; }
    public void setRangoPorJuego(Map<String, Integer> rangoPorJuego) { 
        this.rangoPorJuego = rangoPorJuego; 
    }
    public void setRango(String juego, int rango) {
        this.rangoPorJuego.put(juego, rango);
    }
    public Integer getRango(String juego) {
        return rangoPorJuego.getOrDefault(juego, 0);
    }
    
    public List<String> getRolesPreferidos() { return rolesPreferidos; }
    public void setRolesPreferidos(List<String> roles) { this.rolesPreferidos = roles; }
    public void addRolPreferido(String rol) { this.rolesPreferidos.add(rol); }
    
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    
    public String getServidor() { return servidor; }
    public void setServidor(String servidor) { this.servidor = servidor; }
    
    public Map<String, String> getDisponibilidadHoraria() { return disponibilidadHoraria; }
    public void setDisponibilidad(String dia, String horario) {
        this.disponibilidadHoraria.put(dia, horario);
    }
    
    // Búsquedas favoritas (RF2)
    public List<String> getBusquedasFavoritas() { return busquedasFavoritas; }
    public void agregarBusquedaFavorita(String filtros) {
        this.busquedasFavoritas.add(filtros);
    }
    
    // OAuth (RF1 opcional)
    public String getSteamId() { return steamId; }
    public void setSteamId(String steamId) { this.steamId = steamId; }
    
    public String getRiotId() { return riotId; }
    public void setRiotId(String riotId) { this.riotId = riotId; }
    
    public String getDiscordId() { return discordId; }
    public void setDiscordId(String discordId) { this.discordId = discordId; }
    
    // Estadísticas (RF8)
    public int getTotalPartidas() { return totalPartidas; }
    public void incrementarPartidas() { this.totalPartidas++; }
    
    public double getRating() { return rating; }
    public void actualizarRating(double nuevoRating) {
        // Promedio móvil simple
        this.rating = (this.rating * totalPartidas + nuevoRating) / (totalPartidas + 1);
    }
    
    // Moderación (RF9)
    public int getAbandonos() { return abandonos; }
    public void registrarAbandono() {
        this.abandonos++;
        this.strikes++;
        
        // Sistema de cooldown progresivo
        if (strikes >= 3) {
            this.cooldownHasta = LocalDateTime.now().plusDays(strikes);
        }
    }
    
    public int getStrikes() { return strikes; }
    public void resetearStrikes() { this.strikes = 0; }
    
    public boolean estaSancionado() {
        if (cooldownHasta == null) return false;
        return LocalDateTime.now().isBefore(cooldownHasta);
    }
    
    public LocalDateTime getCooldownHasta() { return cooldownHasta; }
    
    // Notificaciones (RF7)
    public List<Notificacion> getNotificaciones() { return notificaciones; }
    public void agregarNotificacion(Notificacion n) {
        this.notificaciones.add(n);
    }
    public List<Notificacion> getNotificacionesNoLeidas() {
        List<Notificacion> noLeidas = new ArrayList<>();
        for (Notificacion n : notificaciones) {
            if (!n.isLeida()) {
                noLeidas.add(n);
            }
        }
        return noLeidas;
    }
}

// Enum para estado de verificación (RF1)
enum EstadoVerificacion {
    PENDIENTE,
    VERIFICADO
}
