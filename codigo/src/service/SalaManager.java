package service;

import models.Scrim;
import models.Usuario;
import states.EstadoBuscandoJugadores;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Gestor de salas de Scrim
 * Administra la creación y búsqueda de salas disponibles
 */
public class SalaManager {
    private List<Scrim> salasDisponibles;
    private static SalaManager instance;
    
    private SalaManager() {
        this.salasDisponibles = new ArrayList<>();
        inicializarSalasPredeterminadas();
    }
    
    public static SalaManager getInstance() {
        if (instance == null) {
            instance = new SalaManager();
        }
        return instance;
    }
    
    /**
     * Inicializa salas predeterminadas para diferentes juegos
     */
    private void inicializarSalasPredeterminadas() {
        // Salas de Valorant
        salasDisponibles.add(new Scrim.Builder(new EstadoBuscandoJugadores())
            .juego("Valorant")
            .formato("5v5")
            .region("SA")
            .modalidad("ranked")
            .rangoMin(1000)
            .rangoMax(1500)
            .latenciaMax(80)
            .build());
            
        salasDisponibles.add(new Scrim.Builder(new EstadoBuscandoJugadores())
            .juego("Valorant")
            .formato("5v5")
            .region("SA")
            .modalidad("casual")
            .rangoMin(500)
            .rangoMax(2000)
            .latenciaMax(100)
            .build());
            
        salasDisponibles.add(new Scrim.Builder(new EstadoBuscandoJugadores())
            .juego("Valorant")
            .formato("5v5")
            .region("SA")
            .modalidad("ranked")
            .rangoMin(1500)
            .rangoMax(2500)
            .latenciaMax(60)
            .build());
        
        // Salas de League of Legends
        salasDisponibles.add(new Scrim.Builder(new EstadoBuscandoJugadores())
            .juego("League of Legends")
            .formato("5v5")
            .region("SA")
            .modalidad("ranked")
            .rangoMin(800)
            .rangoMax(1200)
            .latenciaMax(80)
            .build());
            
        salasDisponibles.add(new Scrim.Builder(new EstadoBuscandoJugadores())
            .juego("League of Legends")
            .formato("5v5")
            .region("SA")
            .modalidad("casual")
            .rangoMin(0)
            .rangoMax(3000)
            .latenciaMax(100)
            .build());
            
        salasDisponibles.add(new Scrim.Builder(new EstadoBuscandoJugadores())
            .juego("League of Legends")
            .formato("5v5")
            .region("SA")
            .modalidad("ranked")
            .rangoMin(1800)
            .rangoMax(2500)
            .latenciaMax(50)
            .build());
        
        // Salas de CS:GO
        salasDisponibles.add(new Scrim.Builder(new EstadoBuscandoJugadores())
            .juego("CS:GO")
            .formato("5v5")
            .region("SA")
            .modalidad("ranked")
            .rangoMin(1200)
            .rangoMax(1800)
            .latenciaMax(70)
            .build());
            
        salasDisponibles.add(new Scrim.Builder(new EstadoBuscandoJugadores())
            .juego("CS:GO")
            .formato("5v5")
            .region("SA")
            .modalidad("casual")
            .rangoMin(0)
            .rangoMax(3000)
            .latenciaMax(100)
            .build());
    }
    
    /**
     * Obtiene todas las salas disponibles
     */
    public List<Scrim> getSalasDisponibles() {
        return new ArrayList<>(salasDisponibles);
    }
    
    /**
     * Filtra salas por juego
     */
    public List<Scrim> getSalasPorJuego(String juego) {
        List<Scrim> salasFiltradas = new ArrayList<>();
        for (Scrim sala : salasDisponibles) {
            if (sala.getJuego().equalsIgnoreCase(juego)) {
                salasFiltradas.add(sala);
            }
        }
        return salasFiltradas;
    }
    
    /**
     * Obtiene lista de juegos disponibles (sin duplicados)
     */
    public List<String> getJuegosDisponibles() {
        List<String> juegos = new ArrayList<>();
        for (Scrim sala : salasDisponibles) {
            if (!juegos.contains(sala.getJuego())) {
                juegos.add(sala.getJuego());
            }
        }
        return juegos;
    }
    
    /**
     * Verifica si un usuario puede unirse a una sala según su rango
     */
    public boolean puedeUnirse(Usuario usuario, Scrim sala) {
        String juego = sala.getJuego();
        Map<String, Integer> rangos = usuario.getRangoPorJuego();
        
        if (!rangos.containsKey(juego)) {
            return false; // Usuario no tiene rango en este juego
        }
        
        int rangoUsuario = rangos.get(juego);
        return rangoUsuario >= sala.getRangoMin() && rangoUsuario <= sala.getRangoMax();
    }
    
    /**
     * Agrega una nueva sala
     */
    public void agregarSala(Scrim sala) {
        salasDisponibles.add(sala);
    }
    
    /**
     * Elimina una sala (cuando se llena o se cancela)
     */
    public void eliminarSala(Scrim sala) {
        salasDisponibles.remove(sala);
    }
}
