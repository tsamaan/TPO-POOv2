package commands;

/**
 * RF6: Patrón Command para operaciones sobre Scrims.
 * 
 * Permite:
 * - Encapsular operaciones como objetos
 * - Deshacer/rehacer acciones
 * - Logging de cambios
 * - Queue de comandos
 * 
 * Ejemplos:
 * - AsignarRolCommand: Asignar rol a jugador
 * - SwapJugadoresCommand: Intercambiar jugadores entre equipos
 * 
 * @pattern Command
 */
public interface ScrimCommand {
    
    /**
     * Ejecuta el comando.
     */
    void ejecutar();
    
    /**
     * Deshace el comando (undo).
     */
    void deshacer();
    
    /**
     * Retorna descripción del comando para logging.
     */
    String getDescripcion();
}
