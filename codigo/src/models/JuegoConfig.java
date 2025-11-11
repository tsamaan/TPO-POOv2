package models;

/**
 * Configuración de formatos de equipo por juego
 * Cada juego tiene su formato específico (5v5, 1v1, 2v2, etc.)
 */
public enum JuegoConfig {
    // League of Legends - Solo 5v5
    LEAGUE_OF_LEGENDS("League of Legends", "5v5", 10),
    LOL("LoL", "5v5", 10),
    
    // Valorant - Soporta múltiples formatos
    VALORANT("Valorant", "5v5", 10),
    VALORANT_DEATHMATCH("Valorant", "1v1", 2),
    VALORANT_SPIKE_RUSH("Valorant", "5v5", 10),
    
    // Counter-Strike: Global Offensive - Múltiples formatos
    CSGO("CS:GO", "5v5", 10),
    CSGO_WINGMAN("CS:GO", "2v2", 4),
    CSGO_1V1("CS:GO", "1v1", 2),
    
    // Rocket League - Múltiples formatos
    ROCKET_LEAGUE("Rocket League", "3v3", 6),
    ROCKET_LEAGUE_2V2("Rocket League", "2v2", 4),
    ROCKET_LEAGUE_1V1("Rocket League", "1v1", 2);

    private final String nombreJuego;
    private final String formato;
    private final int jugadoresTotales;

    JuegoConfig(String nombreJuego, String formato, int jugadoresTotales) {
        this.nombreJuego = nombreJuego;
        this.formato = formato;
        this.jugadoresTotales = jugadoresTotales;
    }

    public String getNombreJuego() {
        return nombreJuego;
    }

    public String getFormato() {
        return formato;
    }

    public int getJugadoresTotales() {
        return jugadoresTotales;
    }

    /**
     * Obtiene el formato por defecto de un juego
     */
    public static String getFormatoDefault(String juego) {
        if (juego == null) return "5v5";
        
        String juegoLower = juego.toLowerCase().trim();
        
        if (juegoLower.contains("league") || juegoLower.equals("lol")) {
            return "5v5";
        } else if (juegoLower.contains("valorant")) {
            return "5v5";
        } else if (juegoLower.contains("cs") || juegoLower.contains("counter")) {
            return "5v5";
        } else if (juegoLower.contains("rocket")) {
            return "3v3";
        }
        
        return "5v5"; // Default genérico
    }

    /**
     * Obtiene el número de jugadores totales para un formato
     */
    public static int getJugadoresTotales(String formato) {
        if (formato == null) return 10;
        
        switch (formato.toLowerCase()) {
            case "5v5": return 10;
            case "3v3": return 6;
            case "2v2": return 4;
            case "1v1": return 2;
            default: return 10;
        }
    }

    /**
     * Verifica si un formato es válido para un juego específico
     */
    public static boolean isFormatoValido(String juego, String formato) {
        if (juego == null || formato == null) return false;
        
        String juegoLower = juego.toLowerCase().trim();
        String formatoLower = formato.toLowerCase().trim();
        
        // League of Legends - Solo 5v5
        if (juegoLower.contains("league") || juegoLower.equals("lol")) {
            return formatoLower.equals("5v5");
        }
        
        // Valorant - 5v5 y 1v1 (Deathmatch)
        if (juegoLower.contains("valorant")) {
            return formatoLower.equals("5v5") || formatoLower.equals("1v1");
        }
        
        // CS:GO - 5v5, 2v2 (Wingman), 1v1
        if (juegoLower.contains("cs") || juegoLower.contains("counter")) {
            return formatoLower.equals("5v5") || formatoLower.equals("2v2") || formatoLower.equals("1v1");
        }
        
        // Rocket League - 3v3, 2v2, 1v1
        if (juegoLower.contains("rocket")) {
            return formatoLower.equals("3v3") || formatoLower.equals("2v2") || formatoLower.equals("1v1");
        }
        
        return false;
    }

    /**
     * Obtiene los formatos disponibles para un juego
     */
    public static String[] getFormatosDisponibles(String juego) {
        if (juego == null) return new String[]{"5v5"};
        
        String juegoLower = juego.toLowerCase().trim();
        
        if (juegoLower.contains("league") || juegoLower.equals("lol")) {
            return new String[]{"5v5"};
        } else if (juegoLower.contains("valorant")) {
            return new String[]{"5v5", "1v1"};
        } else if (juegoLower.contains("cs") || juegoLower.contains("counter")) {
            return new String[]{"5v5", "2v2", "1v1"};
        } else if (juegoLower.contains("rocket")) {
            return new String[]{"3v3", "2v2", "1v1"};
        }
        
        return new String[]{"5v5"};
    }
}
