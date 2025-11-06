package models;

import java.util.ArrayList;
import java.util.List;

import states.ScrimState;

public class Scrim {
    private ScrimState estado;
    private List<Postulacion> postulaciones = new ArrayList<>();
    private List<interfaces.INotifier> notifiers = new ArrayList<>();

    public Scrim(ScrimState estadoInicial) {
        this.estado = estadoInicial;
    }

    public ScrimState getEstado() { return estado; }
    public void cambiarEstado(ScrimState nuevo) {
        this.estado = nuevo;
    }

    public void addPostulacion(Postulacion p) { postulaciones.add(p); }
    public List<Postulacion> getPostulaciones() { return postulaciones; }

    public void addNotifier(interfaces.INotifier n) { notifiers.add(n); }
    public void notificarCambio(Notificacion notificacion){
        for (interfaces.INotifier n: notifiers) n.sendNotification(notificacion);
    }
}
