package strategies;

import interfaces.IMatchMakingStrategy;
import models.Scrim;

public class ByLatencyStrategy implements IMatchMakingStrategy {

    @Override
    public void ejecutarEmparejamiento(Scrim scrim) {
        System.out.println("Ejecutando emparejamiento por Latencia");
        if (scrim.getPostulaciones().size() >= 4) {
            scrim.cambiarEstado(new states.EstadoLobbyCompleto());
        }
    }
}
