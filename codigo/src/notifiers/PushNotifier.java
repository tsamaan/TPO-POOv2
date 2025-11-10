package notifiers;

import interfaces.INotifier;
import interfaces.INotificationComponent;
import models.Notificacion;

/**
 * Notificador Push (Leaf en patrón COMPOSITE)
 */
public class PushNotifier implements INotifier, INotificationComponent {

    @Override
    public void sendNotification(Notificacion notificacion) {
        System.out.println("📱 [PUSH] " + notificacion.getMensaje());
    }

    @Override
    public String getName() {
        return "PushNotifier";
    }
}
