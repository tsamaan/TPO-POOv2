package states;

import models.Scrim;
import models.Notificacion;

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
        ctx.notificarATodos(Notificacion.TipoNotificacion.FINALIZADO,
            "¡La partida ha finalizado! Gracias por jugar.");
    }
}
