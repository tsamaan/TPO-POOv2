package states;

import models.Scrim;

public class EstadoConfirmado implements ScrimState {

    @Override
    public void postular(Scrim ctx) {
        System.out.println("No se puede postular: Scrim confirmado");
    }

    @Override
    public void iniciar(Scrim ctx) {
        ctx.cambiarEstado(new EstadoEnJuego());
        ctx.notificarCambio(new models.Notificacion("Scrim en juego"));
    }

    @Override
    public void cancelar(Scrim ctx) {
        ctx.cambiarEstado(new EstadoCancelado());
        ctx.notificarCambio(new models.Notificacion("Scrim cancelado"));
    }
}
