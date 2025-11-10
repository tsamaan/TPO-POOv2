package commands;

import models.Scrim;
import models.Usuario;
import java.time.LocalDateTime;

/**
 * RF6: Command para asignar roles a jugadores en un Scrim.
 * 
 * Ejemplo: En LoL asignar Top, Jungle, Mid, ADC, Support
 * 
 * @pattern Command
 */
public class AsignarRolCommand implements ScrimCommand {
    
    private Scrim scrim;
    private Usuario jugador;
    private String nuevoRol;
    private String rolAnterior; // Para undo
    private LocalDateTime timestamp;
    
    public AsignarRolCommand(Scrim scrim, Usuario jugador, String nuevoRol) {
        this.scrim = scrim;
        this.jugador = jugador;
        this.nuevoRol = nuevoRol;
        this.timestamp = LocalDateTime.now();
    }
    
    @Override
    public void ejecutar() {
        // Guardar rol anterior para undo
        this.rolAnterior = obtenerRolActual();
        
        // Asignar nuevo rol
        asignarRol(nuevoRol);
        
        // Log del cambio
        System.out.println("✅ [COMMAND] Rol asignado: " + jugador.getUsername() + " → " + nuevoRol);
        System.out.println("   Scrim: " + scrim.getId());
        System.out.println("   Timestamp: " + timestamp);
    }
    
    @Override
    public void deshacer() {
        if (rolAnterior != null) {
            asignarRol(rolAnterior);
            System.out.println("↩️ [UNDO] Rol restaurado: " + jugador.getUsername() + " → " + rolAnterior);
        } else {
            System.out.println("⚠️ [UNDO] No hay rol anterior para restaurar");
        }
    }
    
    @Override
    public String getDescripcion() {
        return String.format("[AsignarRol] %s → %s en Scrim %s (%s)",
            jugador.getUsername(), nuevoRol, scrim.getId().toString().substring(0, 8), timestamp);
    }
    
    /**
     * Obtiene el rol actual del jugador en el scrim.
     * Simula búsqueda en la estructura del scrim.
     */
    private String obtenerRolActual() {
        // En producción buscaría en scrim.getEquipo1().getJugadores() o scrim.getRolesAsignados()
        // Por ahora simular
        return "Sin Rol";
    }
    
    /**
     * Asigna el rol al jugador.
     * En producción actualizaría la estructura interna del Scrim.
     */
    private void asignarRol(String rol) {
        // Aquí se actualizaría:
        // - scrim.getRolesAsignados().put(jugador.getId(), rol)
        // - O scrim.getEquipo1().asignarRol(jugador, rol)
        
        // Simulación
        System.out.println("   [DB] Rol actualizado en base de datos");
    }
}
