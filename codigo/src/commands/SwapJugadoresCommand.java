package commands;

import models.Scrim;
import models.Usuario;
import java.time.LocalDateTime;

/**
 * RF6: Command para intercambiar jugadores entre equipos.
 * 
 * Casos de uso:
 * - Balanceo de equipos tras cancelación
 * - Ajuste de skill rating
 * - Resolver conflictos de disponibilidad
 * 
 * @pattern Command
 */
public class SwapJugadoresCommand implements ScrimCommand {
    
    private Scrim scrim;
    private Usuario jugador1;
    private Usuario jugador2;
    private LocalDateTime timestamp;
    private boolean ejecutado;
    
    public SwapJugadoresCommand(Scrim scrim, Usuario jugador1, Usuario jugador2) {
        this.scrim = scrim;
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.timestamp = LocalDateTime.now();
        this.ejecutado = false;
    }
    
    @Override
    public void ejecutar() {
        if (ejecutado) {
            System.out.println("⚠️ [COMMAND] Ya ejecutado, use deshacer() primero");
            return;
        }
        
        // Validar que ambos jugadores estén en el scrim
        if (!validarJugadoresEnScrim()) {
            System.out.println("❌ [COMMAND] Jugadores no pertenecen al scrim");
            return;
        }
        
        // Realizar swap
        intercambiarJugadores();
        ejecutado = true;
        
        System.out.println("🔄 [COMMAND] Jugadores intercambiados:");
        System.out.println("   " + jugador1.getUsername() + " ↔ " + jugador2.getUsername());
        System.out.println("   Scrim: " + scrim.getId());
        System.out.println("   Timestamp: " + timestamp);
    }
    
    @Override
    public void deshacer() {
        if (!ejecutado) {
            System.out.println("⚠️ [UNDO] Comando no ejecutado, nada que deshacer");
            return;
        }
        
        // El swap es simétrico, solo volvemos a intercambiar
        intercambiarJugadores();
        ejecutado = false;
        
        System.out.println("↩️ [UNDO] Swap revertido:");
        System.out.println("   " + jugador1.getUsername() + " ↔ " + jugador2.getUsername());
    }
    
    @Override
    public String getDescripcion() {
        return String.format("[SwapJugadores] %s ↔ %s en Scrim %s (%s)",
            jugador1.getUsername(), jugador2.getUsername(), 
            scrim.getId().toString().substring(0, 8), timestamp);
    }
    
    /**
     * Valida que ambos jugadores estén en el scrim.
     */
    private boolean validarJugadoresEnScrim() {
        // En producción verificaría:
        // - scrim.getEquipo1().contieneJugador(jugador1) || scrim.getEquipo2().contieneJugador(jugador1)
        // - scrim.getEquipo1().contieneJugador(jugador2) || scrim.getEquipo2().contieneJugador(jugador2)
        
        // Simulación
        return true;
    }
    
    /**
     * Intercambia los jugadores entre equipos.
     */
    private void intercambiarJugadores() {
        // En producción:
        // 1. Detectar en qué equipo está cada jugador
        // 2. Remover de sus equipos actuales
        // 3. Agregar al equipo contrario
        // 4. Mantener roles si es posible
        // 5. Recalcular balance de equipos
        
        // Simulación
        System.out.println("   [DB] Equipos actualizados en base de datos");
        System.out.println("   [STATS] Balance recalculado: " + calcularBalance() + "%");
    }
    
    /**
     * Calcula el balance de equipos tras el swap.
     */
    private int calcularBalance() {
        // Simulación: retornar balance entre 0-100%
        return (int) (Math.random() * 30) + 70; // 70-100%
    }
}
