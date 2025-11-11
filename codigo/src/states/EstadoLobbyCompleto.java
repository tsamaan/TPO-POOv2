package states;

import models.Scrim;
import models.Notificacion;

public class EstadoLobbyCompleto implements ScrimState {

    @Override
    public void postular(Scrim ctx) {
        System.out.println("Lobby completo: no se aceptan más postulaciones");
    }

    @Override
    public void iniciar(Scrim ctx) {
        ctx.cambiarEstado(new EstadoConfirmado());
        ctx.notificarATodos(Notificacion.TipoNotificacion.LOBBY_COMPLETO,
            "¡Lobby completo! 10/10 jugadores. El scrim está confirmado.");
    }

    @Override
    public void cancelar(Scrim ctx) {
        ctx.cambiarEstado(new EstadoCancelado());
        ctx.notificarATodos(Notificacion.TipoNotificacion.CANCELADO,
            "El scrim ha sido cancelado.");
    }
}
