package notifiers;

import interfaces.INotifier;
import interfaces.INotificationComponent;
import models.Notificacion;

/**
 * Notificador de Discord (Leaf en patrón COMPOSITE)
 */
public class DiscordNotifier implements INotifier, INotificationComponent {

    @Override
    public void sendNotification(Notificacion notificacion) {
        System.out.println("💬 [DISCORD] " + notificacion.getMensaje());
    }

    @Override
    public String getName() {
        return "DiscordNotifier";
    }
}
