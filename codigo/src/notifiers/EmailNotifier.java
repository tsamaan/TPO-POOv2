package notifiers;

import interfaces.INotifier;
import models.Notificacion;

public class EmailNotifier implements INotifier {

    @Override
    public void sendNotification(Notificacion notificacion) {
        System.out.println("Enviando email: " + notificacion.getMensaje());
    }
}
