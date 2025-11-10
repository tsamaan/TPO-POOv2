package models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import states.ScrimState;

/**
 * Clase Scrim - Representa una partida de práctica (Scrim)
 * Utiliza el patrón STATE para gestionar su ciclo de vida
 * Utiliza el patrón OBSERVER para notificar cambios
 */
public class Scrim {
    // Identificadores
    private UUID id;
    
    // Estado (Patrón STATE)
    private ScrimState estado;
    
    // Configuración del scrim
    private String juego;           // "Valorant", "League of Legends", etc.
    private String formato;         // "5v5", "3v3", etc.
    private String region;          // "SA" (South America), "NA", "EU", etc.
    private String modalidad;       // "ranked", "casual", "tournament"
    
    // Restricciones de matchmaking
    private int rangoMin;           // MMR mínimo permitido
    private int rangoMax;           // MMR máximo permitido
    private int latenciaMax;        // Latencia máxima en ms
    
    // Gestión de jugadores
    private List<Postulacion> postulaciones = new ArrayList<>();
    private int cuposMaximos;       // Número máximo de jugadores
    
    // Observadores (Patrón OBSERVER)
    private List<interfaces.INotifier> notifiers = new ArrayList<>();

    /**
     * Constructor público para compatibilidad con código existente
     */
    public Scrim(ScrimState estadoInicial) {
        this.id = UUID.randomUUID();
        this.estado = estadoInicial;
        this.cuposMaximos = 10; // Default 5v5
    }

    /**
     * Constructor privado usado por Builder
     */
    private Scrim(Builder builder) {
        this.id = UUID.randomUUID();
        this.estado = builder.estado;
        this.juego = builder.juego;
        this.formato = builder.formato;
        this.region = builder.region;
        this.modalidad = builder.modalidad;
        this.rangoMin = builder.rangoMin;
        this.rangoMax = builder.rangoMax;
        this.latenciaMax = builder.latenciaMax;
        this.cuposMaximos = builder.cuposMaximos;
    }

    // === Métodos de Estado (STATE Pattern) ===
    
    public ScrimState getEstado() { 
        return estado; 
    }
    
    public void cambiarEstado(ScrimState nuevo) {
        this.estado = nuevo;
    }

    // === Métodos de Postulaciones ===
    
    public void addPostulacion(Postulacion p) { 
        postulaciones.add(p); 
    }
    
    public List<Postulacion> getPostulaciones() { 
        return postulaciones; 
    }

    // === Métodos de Notificación (OBSERVER Pattern) ===
    
    public void addNotifier(interfaces.INotifier n) { 
        notifiers.add(n); 
    }
    
    public void notificarCambio(Notificacion notificacion){
        for (interfaces.INotifier n: notifiers) {
            n.sendNotification(notificacion);
        }
    }

    // === Getters de Configuración ===
    
    public UUID getId() { return id; }
    public String getJuego() { return juego; }
    public String getFormato() { return formato; }
    public String getRegion() { return region; }
    public String getModalidad() { return modalidad; }
    public int getRangoMin() { return rangoMin; }
    public int getRangoMax() { return rangoMax; }
    public int getLatenciaMax() { return latenciaMax; }
    public int getCuposMaximos() { return cuposMaximos; }

    // === Setters (para compatibilidad) ===
    
    public void setJuego(String juego) { this.juego = juego; }
    public void setFormato(String formato) { this.formato = formato; }
    public void setRegion(String region) { this.region = region; }
    public void setModalidad(String modalidad) { this.modalidad = modalidad; }

    /**
     * Builder interno para construcción fluida de Scrim
     * Patrón BUILDER
     */
    public static class Builder {
        // Obligatorios
        private ScrimState estado;
        
        // Opcionales con valores por defecto
        private String juego = "Valorant";
        private String formato = "5v5";
        private String region = "SA";
        private String modalidad = "casual";
        private int rangoMin = 0;
        private int rangoMax = 3000;
        private int latenciaMax = 100;
        private int cuposMaximos = 10;

        public Builder(ScrimState estadoInicial) {
            this.estado = estadoInicial;
        }

        public Builder juego(String juego) {
            this.juego = juego;
            return this;
        }

        public Builder formato(String formato) {
            this.formato = formato;
            // Ajustar cupos según formato
            if (formato.equals("5v5")) this.cuposMaximos = 10;
            else if (formato.equals("3v3")) this.cuposMaximos = 6;
            else if (formato.equals("1v1")) this.cuposMaximos = 2;
            return this;
        }

        public Builder region(String region) {
            this.region = region;
            return this;
        }

        public Builder modalidad(String modalidad) {
            this.modalidad = modalidad;
            return this;
        }

        public Builder rangoMin(int rangoMin) {
            if (rangoMin < 0) throw new IllegalArgumentException("Rango mínimo no puede ser negativo");
            this.rangoMin = rangoMin;
            return this;
        }

        public Builder rangoMax(int rangoMax) {
            if (rangoMax < 0) throw new IllegalArgumentException("Rango máximo no puede ser negativo");
            this.rangoMax = rangoMax;
            return this;
        }

        public Builder latenciaMax(int latenciaMax) {
            if (latenciaMax <= 0) throw new IllegalArgumentException("Latencia debe ser positiva");
            this.latenciaMax = latenciaMax;
            return this;
        }

        public Builder cuposMaximos(int cupos) {
            if (cupos <= 0) throw new IllegalArgumentException("Cupos debe ser positivo");
            this.cuposMaximos = cupos;
            return this;
        }

        /**
         * Construye el Scrim validando restricciones
         */
        public Scrim build() {
            // Validaciones
            if (rangoMin > rangoMax) {
                throw new IllegalStateException("Rango mínimo no puede ser mayor que rango máximo");
            }
            
            return new Scrim(this);
        }
    }

    @Override
    public String toString() {
        return String.format("Scrim[%s | %s | %s | %s | Rango: %d-%d | Latencia: <%dms | Estado: %s]",
            juego, formato, region, modalidad, rangoMin, rangoMax, latenciaMax, 
            estado != null ? estado.getClass().getSimpleName() : "null");
    }
}
