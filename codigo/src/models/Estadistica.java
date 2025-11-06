package models;

import java.util.UUID;

/**
 * Clase Estadistica - Registra estadísticas de rendimiento de un jugador en un Scrim
 * Calcula KDA y proporciona análisis de rendimiento
 */
public class Estadistica {
    private UUID id;
    private Usuario usuario;
    private Scrim scrim;
    private int kills;
    private int deaths;
    private int assists;
    private double kda;

    public Estadistica(Usuario usuario, Scrim scrim) {
        this.id = UUID.randomUUID();
        this.usuario = usuario;
        this.scrim = scrim;
        this.kills = 0;
        this.deaths = 0;
        this.assists = 0;
        this.kda = 0.0;
    }

    public Estadistica(Usuario usuario, Scrim scrim, int kills, int deaths, int assists) {
        this.id = UUID.randomUUID();
        this.usuario = usuario;
        this.scrim = scrim;
        this.kills = kills;
        this.deaths = deaths;
        this.assists = assists;
        this.kda = calcularKDA();
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

    @Override
    public String toString() {
        return "Estadistica{" +
               "usuario=" + usuario.getUsername() +
               ", K/D/A=" + kills + "/" + deaths + "/" + assists +
               ", KDA=" + String.format("%.2f", kda) +
               ", rendimiento=" + obtenerRendimiento() +
               '}';
    }
}
