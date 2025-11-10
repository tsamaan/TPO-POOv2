package models;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class Usuario {
    // Identificación y autenticación
    private int id;
    private String username;
    private String email;
    private Map<String, Integer> rangoPorJuego;
    private String rol; // Rol asignado en el scrim (para patrón Command)
    private List<Notificacion> notificaciones; // Lista de notificaciones recibidas

    public Usuario(int id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.rol = null; // Sin rol asignado inicialmente
        this.rangoPorJuego = new HashMap<>();
        this.notificaciones = new ArrayList<>();
    }

    // Getters básicos
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    
    public Map<String, Integer> getRangoPorJuego() { return rangoPorJuego; }
    public void setRangoPorJuego(Map<String, Integer> rangoPorJuego) { this.rangoPorJuego = rangoPorJuego; }
    
    // Métodos para gestión de rol (patrón Command)
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    
    // Métodos para gestión de notificaciones (RF7)
    public void agregarNotificacion(Notificacion notificacion) {
        this.notificaciones.add(notificacion);
    }
    
    public List<Notificacion> getNotificaciones() {
        return notificaciones;
    }
}
