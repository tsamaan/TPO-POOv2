package models;

import java.util.Map;

public class Usuario {
    private int id;
    private String username;
    private String email;
    private Map<String, Integer> rangoPorJuego;

    public Usuario(int id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public Map<String, Integer> getRangoPorJuego() { return rangoPorJuego; }
    public void setRangoPorJuego(Map<String, Integer> rangoPorJuego) { this.rangoPorJuego = rangoPorJuego; }
}
