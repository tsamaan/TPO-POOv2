package models;

import java.util.UUID;

/**
 * Clase Estadistica - Registra estadísticas de rendimiento de un jugador en un Scrim
 * Calcula KDA y proporciona análisis de rendimiento
 * RF8: Estadísticas y feedback
 */
public class Estadistica {
    private UUID id;
    private Usuario usuario;
    private Scrim scrim;
    private int kills;
    private int deaths;
    private int assists;
    private double kda;
    
    // RF8: MVP y rating
    private boolean mvp;              // ¿Es el MVP de la partida?
    private double rating;            // Rating/valoración del jugador (0-10)
    private String comentario;        // Comentario opcional sobre el desempeño
    private EstadoComentario estadoComentario; // Estado de moderación del comentario

    // Enum para moderación de comentarios (RF8)
    public enum EstadoComentario {
        PENDIENTE,
        APROBADO,
        RECHAZADO
    }
    
    public Estadistica(Usuario usuario, Scrim scrim) {
        this.id = UUID.randomUUID();
        this.usuario = usuario;
        this.scrim = scrim;
        this.kills = 0;
        this.deaths = 0;
        this.assists = 0;
        this.kda = 0.0;
        this.mvp = false;
        this.rating = 0.0;
        this.estadoComentario = EstadoComentario.PENDIENTE;
    }

    public Estadistica(Usuario usuario, Scrim scrim, int kills, int deaths, int assists) {
        this.id = UUID.randomUUID();
        this.usuario = usuario;
        this.scrim = scrim;
        this.kills = kills;
        this.deaths = deaths;
        this.assists = assists;
        this.kda = calcularKDA();
        this.mvp = false;
        this.rating = 0.0;
        this.estadoComentario = EstadoComentario.PENDIENTE;
    }

    public double calcularKDA() {
        // KDA = (Kills + Assists) / Deaths
        // Si no hay deaths, se considera KDA perfecto
        if (deaths == 0) {
            this.kda = kills + assists;
        } else {
            this.kda = (double) (kills + assists) / deaths;
        }
        return this.kda;
    }

    public String obtenerRendimiento() {
        calcularKDA(); // Actualizar KDA antes de evaluar

        String rendimiento;
        if (kda >= 3.0) {
            rendimiento = "EXCELENTE";
        } else if (kda >= 2.0) {
            rendimiento = "MUY BUENO";
        } else if (kda >= 1.0) {
            rendimiento = "BUENO";
        } else if (kda >= 0.5) {
            rendimiento = "REGULAR";
        } else {
            rendimiento = "MALO";
        }

        return String.format("%s (KDA: %.2f | K/D/A: %d/%d/%d)", 
                           rendimiento, kda, kills, deaths, assists);
    }

    // Setters para actualizar estadísticas durante el juego
    public void setKills(int kills) {
        this.kills = kills;
        calcularKDA();
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
        calcularKDA();
    }

    public void setAssists(int assists) {
        this.assists = assists;
        calcularKDA();
    }

    public void incrementarKills() {
        this.kills++;
        calcularKDA();
    }

    public void incrementarDeaths() {
        this.deaths++;
        calcularKDA();
    }

    public void incrementarAssists() {
        this.assists++;
        calcularKDA();
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Scrim getScrim() {
        return scrim;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getAssists() {
        return assists;
    }

    public double getKda() {
        return kda;
    }
    
    // RF8: Getters y setters para MVP y rating
    public boolean isMvp() {
        return mvp;
    }
    
    public void setMvp(boolean mvp) {
        this.mvp = mvp;
    }
    
    public double getRating() {
        return rating;
    }
    
    public void setRating(double rating) {
        if (rating >= 0 && rating <= 10) {
            this.rating = rating;
        }
    }
    
    public String getComentario() {
        return comentario;
    }
    
    public void setComentario(String comentario) {
        this.comentario = comentario;
        this.estadoComentario = EstadoComentario.PENDIENTE; // Requiere moderación
    }
    
    public EstadoComentario getEstadoComentario() {
        return estadoComentario;
    }
    
    public void aprobarComentario() {
        this.estadoComentario = EstadoComentario.APROBADO;
    }
    
    public void rechazarComentario() {
        this.estadoComentario = EstadoComentario.RECHAZADO;
    }

    @Override
    public String toString() {
        return "Estadistica{" +
               "usuario=" + usuario.getUsername() +
               ", K/D/A=" + kills + "/" + deaths + "/" + assists +
               ", KDA=" + String.format("%.2f", kda) +
               ", MVP=" + (mvp ? "★" : "-") +
               ", Rating=" + String.format("%.1f", rating) +
               ", rendimiento=" + obtenerRendimiento() +
               '}';
    }
}
