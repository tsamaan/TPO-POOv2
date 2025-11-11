package states;

import models.Scrim;
import models.Notificacion;

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
        ctx.notificarATodos(Notificacion.TipoNotificacion.CONFIRMADO, 
            "¡Scrim confirmado! Todos los jugadores están listos.");
    }

    @Override
    public void cancelar(Scrim ctx) {
        ctx.cambiarEstado(new EstadoCancelado());
        ctx.notificarATodos(Notificacion.TipoNotificacion.CANCELADO,
            "El scrim ha sido cancelado.");
    }
}
