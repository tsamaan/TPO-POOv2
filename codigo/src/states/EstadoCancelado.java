package states;

import models.Scrim;

public class EstadoCancelado implements ScrimState {

    @Override
    public void postular(Scrim ctx) {
        System.out.println("Scrim cancelado: no se puede postular");
    }

    @Override
    public void iniciar(Scrim ctx) {
        System.out.println("Scrim cancelado: no se puede iniciar");
    }

    @Override
    public void cancelar(Scrim ctx) {
        System.out.println("Ya está cancelado");
    }
}
