package models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import states.ScrimState;

public class Scrim {
    // Identificación
    private UUID id;
    private Usuario creador;
    
    // RF3a: Juego y formato
    private String juego; // ej: "League of Legends", "Valorant", "CS2"
    private String formato; // ej: "5v5", "3v3", "1v1"
    
    // RF3b: Cantidad y roles
    private int cantidadJugadoresPorLado; // ej: 5 para 5v5
    private int cuposTotales; // calculado: cantidadJugadoresPorLado * 2
    private List<String> rolesRequeridos; // ej: ["Top", "Jungle", "Mid", "ADC", "Support"]
    
    // RF3c: Región y límites
    private String region; // ej: "LAS", "NA", "EUW"
    private String rangoMinimo; // ej: "Iron", "Bronze", "Silver"
    private String rangoMaximo; // ej: "Diamond", "Master", "Challenger"
    private int latenciaMaxima; // en ms, ej: 50
    
    // RF3d: Fecha/hora y modalidad
    private LocalDateTime fechaHora; // cuándo se jugará
    private int duracionEstimada; // en minutos
    private String modalidad; // "ranked-like", "casual", "practica"
    
    // Estado y gestión
    private ScrimState estado;
    private List<Postulacion> postulaciones = new ArrayList<>();
    private List<interfaces.INotifier> notifiers = new ArrayList<>();
    private List<Usuario> listaEspera = new ArrayList<>();
    
    // Constructor completo
    public Scrim(Usuario creador, String juego, String formato, 
                 int cantidadJugadoresPorLado, String region,
                 String rangoMin, String rangoMax, int latenciaMax,
                 LocalDateTime fechaHora, int duracion, String modalidad,
                 ScrimState estadoInicial) {
        this.id = UUID.randomUUID();
        this.creador = creador;
        this.juego = juego;
        this.formato = formato;
        this.cantidadJugadoresPorLado = cantidadJugadoresPorLado;
        this.cuposTotales = cantidadJugadoresPorLado * 2;
        this.region = region;
        this.rangoMinimo = rangoMin;
        this.rangoMaximo = rangoMax;
        this.latenciaMaxima = latenciaMax;
        this.fechaHora = fechaHora;
        this.duracionEstimada = duracion;
        this.modalidad = modalidad;
        this.estado = estadoInicial;
        this.rolesRequeridos = new ArrayList<>();
    }
    
    // Constructor simple (retrocompatibilidad con Main.java existente)
    public Scrim(ScrimState estadoInicial) {
        this.id = UUID.randomUUID();
        this.estado = estadoInicial;
        this.rolesRequeridos = new ArrayList<>();
        this.cuposTotales = 10; // default para LoL 5v5
    }

    // Getters estado y gestión básica
    public UUID getId() { return id; }
    public Usuario getCreador() { return creador; }
    public ScrimState getEstado() { return estado; }
    
    public void cambiarEstado(ScrimState nuevo) {
        this.estado = nuevo;
    }

    // Getters juego y formato (RF3a)
    public String getJuego() { return juego; }
    public String getFormato() { return formato; }
    
    // Getters cantidad y roles (RF3b)
    public int getCantidadJugadoresPorLado() { return cantidadJugadoresPorLado; }
    public int getCuposTotales() { return cuposTotales; }
    public List<String> getRolesRequeridos() { return rolesRequeridos; }
    public void setRolesRequeridos(List<String> roles) { this.rolesRequeridos = roles; }
    
    // Getters región y límites (RF3c)
    public String getRegion() { return region; }
    public String getRangoMinimo() { return rangoMinimo; }
    public String getRangoMaximo() { return rangoMaximo; }
    public int getLatenciaMaxima() { return latenciaMaxima; }
    
    // Getters fecha/hora y modalidad (RF3d)
    public LocalDateTime getFechaHora() { return fechaHora; }
    public int getDuracionEstimada() { return duracionEstimada; }
    public String getModalidad() { return modalidad; }
    
    // Setters (para modificación post-creación)
    public void setJuego(String juego) { this.juego = juego; }
    public void setFormato(String formato) { this.formato = formato; }
    public void setRegion(String region) { this.region = region; }
    public void setRangoMinimo(String min) { this.rangoMinimo = min; }
    public void setRangoMaximo(String max) { this.rangoMaximo = max; }
    public void setLatenciaMaxima(int ms) { this.latenciaMaxima = ms; }
    public void setFechaHora(LocalDateTime fecha) { this.fechaHora = fecha; }
    public void setDuracionEstimada(int minutos) { this.duracionEstimada = minutos; }
    public void setModalidad(String mod) { this.modalidad = mod; }

    // Gestión de postulaciones
    public void addPostulacion(Postulacion p) { postulaciones.add(p); }
    public List<Postulacion> getPostulaciones() { return postulaciones; }
    
    // Lista de espera (RF6 - suplentes)
    public void addListaEspera(Usuario u) { listaEspera.add(u); }
    public List<Usuario> getListaEspera() { return listaEspera; }

    // Notificaciones (RF7)
    public void addNotifier(interfaces.INotifier n) { notifiers.add(n); }
    public void notificarCambio(Notificacion notificacion){
        for (interfaces.INotifier n: notifiers) n.sendNotification(notificacion);
    }
    
    // Métodos de negocio
    public boolean cumpleRequisitos(Usuario usuario) {
        // Validar región
        if (this.region != null && usuario.getRegion() != null 
            && !this.region.equals(usuario.getRegion())) {
            return false;
        }
        
        // Validar rango (simplificado - en producción sería más complejo)
        // TODO: Implementar comparación real de rangos
        
        return true;
    }
    
    public boolean estaLleno() {
        return postulaciones.size() >= cuposTotales;
    }
    
    public int cuposDisponibles() {
        return cuposTotales - postulaciones.size();
    }
}
