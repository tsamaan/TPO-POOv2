package interfaces;

import context.ScrimContext;

/**
 * Patrón COMMAND
 * Interfaz que define las operaciones para comandos ejecutables
 * con capacidad de deshacer (undo)
 */
public interface IScrimCommand {
    
    /**
     * Ejecuta el comando
     * @param ctx Contexto del scrim sobre el cual se ejecuta el comando
     */
    void execute(ScrimContext ctx);
    
    /**
     * Deshace el comando previamente ejecutado
     * @param ctx Contexto del scrim sobre el cual se deshace el comando
     */
    void undo(ScrimContext ctx);
}
