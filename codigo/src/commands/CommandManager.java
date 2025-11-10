package commands;

import interfaces.IScrimCommand;
import context.ScrimContext;
import java.util.Stack;

/**
 * Invoker del patrón COMMAND
 * Gestiona la ejecución y el historial de comandos con capacidad de undo
 */
public class CommandManager {
    
    private Stack<IScrimCommand> historial;
    private ScrimContext context;
    
    /**
     * Constructor del gestor de comandos
     * @param context Contexto del scrim sobre el que se ejecutarán los comandos
     */
    public CommandManager(ScrimContext context) {
        this.historial = new Stack<>();
        this.context = context;
    }
    
    /**
     * Ejecuta un comando y lo guarda en el historial
     * @param command Comando a ejecutar
     */
    public void ejecutarComando(IScrimCommand command) {
        command.execute(context);
        historial.push(command);
        System.out.println("[COMMAND MANAGER] Comando ejecutado y guardado en historial");
    }
    
    /**
     * Deshace el último comando ejecutado
     */
    public void deshacerUltimo() {
        if (!historial.isEmpty()) {
            IScrimCommand command = historial.pop();
            command.undo(context);
            System.out.println("[COMMAND MANAGER] Último comando deshecho");
        } else {
            System.out.println("[COMMAND MANAGER] No hay comandos para deshacer");
        }
    }
    
    /**
     * Deshace todos los comandos ejecutados
     */
    public void deshacerTodos() {
        int count = 0;
        while (!historial.isEmpty()) {
            deshacerUltimo();
            count++;
        }
        System.out.println("[COMMAND MANAGER] Total de comandos deshechos: " + count);
    }
    
    /**
     * Verifica si hay comandos en el historial
     * @return true si hay comandos, false si está vacío
     */
    public boolean tieneHistorial() {
        return !historial.isEmpty();
    }
    
    /**
     * Obtiene la cantidad de comandos en el historial
     * @return Número de comandos en el historial
     */
    public int tamañoHistorial() {
        return historial.size();
    }
    
    /**
     * Limpia el historial de comandos
     */
    public void limpiarHistorial() {
        historial.clear();
        System.out.println("[COMMAND MANAGER] Historial limpiado");
    }
}
