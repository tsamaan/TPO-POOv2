package strategies;

import interfaces.IMatchMakingStrategy;
import models.Scrim;

public class ByMMRStrategy implements IMatchMakingStrategy {

    @Override
    public void ejecutarEmparejamiento(Scrim scrim) {
        System.out.println("Ejecutando emparejamiento por MMR");
        // implementación de ejemplo: marcar lobby completo si hay >4 postulaciones
        if (scrim.getPostulaciones().size() >= 4) {
            scrim.cambiarEstado(new states.EstadoLobbyCompleto());
        }
    }
}
