package service;

import interfaces.IMatchMakingStrategy;
import models.Scrim;

public class MatchmakingService {
    private IMatchMakingStrategy estrategia;

    public MatchmakingService(IMatchMakingStrategy estrategia) {
        this.estrategia = estrategia;
    }

    public void ejecutarEmparejamiento(Scrim scrim) {
        estrategia.ejecutarEmparejamiento(scrim);
    }
}
