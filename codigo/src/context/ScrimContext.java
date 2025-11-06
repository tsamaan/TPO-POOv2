package context;

import models.Scrim;
import models.Usuario;
import models.Postulacion;
import states.ScrimState;

public class ScrimContext {
    private Scrim scrim;
    private ScrimState estado;

    public ScrimContext(Scrim scrim, ScrimState estadoInicial) {
        this.scrim = scrim;
        this.estado = estadoInicial;
    }

    public void postular(Usuario usuario, String rol) {
        Postulacion p = new Postulacion(usuario, rol);
        scrim.addPostulacion(p);
        estado.postular(scrim);
    }

    public void cambiarEstado(ScrimState nuevoEstado) {
        this.estado = nuevoEstado;
        scrim.cambiarEstado(nuevoEstado);
    }

    public void notificarCambio(models.Notificacion notificacion) {
        scrim.notificarCambio(notificacion);
    }

    public ScrimState getEstado() {
        return estado;
    }
}
