package commands;

import interfaces.IScrimCommand;
import context.ScrimContext;
import models.Usuario;

/**
 * Comando concreto que intercambia los roles de dos jugadores
 * Implementa el patrón COMMAND con capacidad de undo
 */
public class SwapJugadoresCommand implements IScrimCommand {
    
    private Usuario jugador1;
    private Usuario jugador2;
    
    /**
     * Constructor del comando
     * @param jugador1 Primer jugador del intercambio
     * @param jugador2 Segundo jugador del intercambio
     */
    public SwapJugadoresCommand(Usuario jugador1, Usuario jugador2) {
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
    }
    
    @Override
    public void execute(ScrimContext ctx) {
        // Intercambiar roles entre los dos jugadores
        String rolTemp = jugador1.getRol();
        jugador1.setRol(jugador2.getRol());
        jugador2.setRol(rolTemp);
        
        System.out.println("[COMMAND] Roles intercambiados:");
        System.out.println("  " + jugador1.getUsername() + " → " + jugador1.getRol());
        System.out.println("  " + jugador2.getUsername() + " → " + jugador2.getRol());
    }
    
    @Override
    public void undo(ScrimContext ctx) {
        // Deshacer es simplemente volver a intercambiar
        String rolTemp = jugador1.getRol();
        jugador1.setRol(jugador2.getRol());
        jugador2.setRol(rolTemp);
        
        System.out.println("[COMMAND UNDO] Roles restaurados:");
        System.out.println("  " + jugador1.getUsername() + " → " + jugador1.getRol());
        System.out.println("  " + jugador2.getUsername() + " → " + jugador2.getRol());
    }
    
    // Getters para testing
    public Usuario getJugador1() {
        return jugador1;
    }
    
    public Usuario getJugador2() {
        return jugador2;
    }
}
