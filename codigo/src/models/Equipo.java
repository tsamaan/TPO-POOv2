package models;

import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase Equipo - Representa un equipo dentro de un Scrim
 * Gestiona la asignación y eliminación de jugadores
 */
public class Equipo {
    private UUID id;
    private String lado; // "Equipo1" o "Equipo2"
    private List<Usuario> jugadores;

    public Equipo(String lado) {
        this.id = UUID.randomUUID();
        this.lado = lado;
        this.jugadores = new ArrayList<>();
    }

    public void asignarJugador(Usuario usuario) {
        if (!jugadores.contains(usuario)) {
            jugadores.add(usuario);
            System.out.println("Usuario " + usuario.getUsername() + " asignado al " + lado);
        } else {
            System.out.println("Usuario " + usuario.getUsername() + " ya está en el " + lado);
        }
    }

    public void eliminarJugador(Usuario usuario) {
        if (jugadores.remove(usuario)) {
            System.out.println("Usuario " + usuario.getUsername() + " eliminado del " + lado);
        } else {
            System.out.println("Usuario " + usuario.getUsername() + " no está en el " + lado);
        }
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public String getLado() {
        return lado;
    }

    public List<Usuario> getJugadores() {
        return new ArrayList<>(jugadores); // Retornar copia para evitar modificaciones externas
    }

    public int getCantidadJugadores() {
        return jugadores.size();
    }

    @Override
    public String toString() {
        return lado + " (" + jugadores.size() + " jugadores): " + 
               jugadores.stream()
                       .map(Usuario::getUsername)
                       .reduce((a, b) -> a + ", " + b)
                       .orElse("sin jugadores");
    }
}
