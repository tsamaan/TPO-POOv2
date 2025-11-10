package commands;

import interfaces.IScrimCommand;
import context.ScrimContext;
import models.Usuario;

/**
 * Comando concreto que asigna un rol a un usuario
 * Implementa el patrón COMMAND con capacidad de undo
 */
public class AsignarRolCommand implements IScrimCommand {
    
    private Usuario usuario;
    private String rol;
    private String rolAnterior;
    
    /**
     * Constructor del comando
     * @param usuario Usuario al que se le asignará el rol
     * @param rol Nuevo rol a asignar
     */
    public AsignarRolCommand(Usuario usuario, String rol) {
        this.usuario = usuario;
        this.rol = rol;
        this.rolAnterior = null; // Se guardará al ejecutar
    }
    
    @Override
    public void execute(ScrimContext ctx) {
        // Guardar el rol anterior para poder hacer undo
        this.rolAnterior = usuario.getRol();
        
        // Asignar el nuevo rol
        usuario.setRol(rol);
        
        System.out.println("[COMMAND] Rol asignado a " + usuario.getUsername() + 
                         ": " + rolAnterior + " → " + rol);
    }
    
    @Override
    public void undo(ScrimContext ctx) {
        if (rolAnterior != null) {
            // Restaurar el rol anterior
            usuario.setRol(rolAnterior);
            
            System.out.println("[COMMAND UNDO] Rol restaurado para " + usuario.getUsername() + 
                             ": " + rol + " → " + rolAnterior);
        } else {
            System.out.println("[COMMAND UNDO] No hay rol anterior para restaurar");
        }
    }
    
    // Getters para testing
    public Usuario getUsuario() {
        return usuario;
    }
    
    public String getRol() {
        return rol;
    }
    
    public String getRolAnterior() {
        return rolAnterior;
    }
}
