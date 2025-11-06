package notifiers;

import interfaces.INotifier;
import models.Notificacion;

public class PushNotifier implements INotifier {

    @Override
    public void sendNotification(Notificacion notificacion) {
        System.out.println("Enviando push: " + notificacion.getMensaje());
    }
}
