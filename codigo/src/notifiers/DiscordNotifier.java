package notifiers;

import interfaces.INotifier;
import models.Notificacion;

public class DiscordNotifier implements INotifier {

    @Override
    public void sendNotification(Notificacion notificacion) {
        System.out.println("Enviando Discord: " + notificacion.getMensaje());
    }
}
