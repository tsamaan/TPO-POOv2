package states;

import models.Scrim;

public class EstadoLobbyCompleto implements ScrimState {

    @Override
    public void postular(Scrim ctx) {
        System.out.println("Lobby completo: no se aceptan más postulaciones");
    }

    @Override
    public void iniciar(Scrim ctx) {
        ctx.cambiarEstado(new EstadoConfirmado());
        ctx.notificarCambio(new models.Notificacion("Scrim confirmado desde LobbyCompleto"));
    }

    @Override
    public void cancelar(Scrim ctx) {
        ctx.cambiarEstado(new EstadoCancelado());
        ctx.notificarCambio(new models.Notificacion("Scrim cancelado"));
    }
}
