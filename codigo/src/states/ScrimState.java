package states;

import models.Scrim;

public interface ScrimState {
    void postular(Scrim ctx);
    void iniciar(Scrim ctx);
    void cancelar(Scrim ctx);
}
