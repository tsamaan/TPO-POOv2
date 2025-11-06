package states;

import models.Scrim;

public class EstadoBuscandoJugadores implements ScrimState {

    @Override
    public void postular(Scrim ctx) {
        // permitir postulaciones
        System.out.println("Postulado en estado BuscandoJugadores");
    }

    @Override
    public void iniciar(Scrim ctx) {
        System.out.println("Iniciando desde BuscandoJugadores: cambiando a Confirmado");
        ctx.cambiarEstado(new EstadoConfirmado());
        ctx.notificarCambio(new models.Notificacion("Scrim confirmado"));
    }

    @Override
    public void cancelar(Scrim ctx) {
        ctx.cambiarEstado(new EstadoCancelado());
        ctx.notificarCambio(new models.Notificacion("Scrim cancelado"));
    }
}
