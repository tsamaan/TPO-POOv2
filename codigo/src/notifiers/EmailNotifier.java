package notifiers;

import interfaces.INotifier;
import interfaces.INotificationComponent;
import models.Notificacion;

/**
 * Notificador de Email (Leaf en patrón COMPOSITE)
 */
public class EmailNotifier implements INotifier, INotificationComponent {

    @Override
    public void sendNotification(Notificacion notificacion) {
        System.out.println("📧 [EMAIL] " + notificacion.getMensaje());
    }

    @Override
    public String getName() {
        return "EmailNotifier";
    }
}
