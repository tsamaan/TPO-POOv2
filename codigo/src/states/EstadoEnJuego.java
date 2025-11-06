package states;

import models.Scrim;

public class EstadoEnJuego implements ScrimState {

    @Override
    public void postular(Scrim ctx) {
        System.out.println("No se puede postular: Scrim en juego");
    }

    @Override
    public void iniciar(Scrim ctx) {
        System.out.println("Ya está en juego");
    }

    @Override
    public void cancelar(Scrim ctx) {
        ctx.cambiarEstado(new EstadoFinalizado());
        ctx.notificarCambio(new models.Notificacion("Scrim finalizado"));
    }
}
