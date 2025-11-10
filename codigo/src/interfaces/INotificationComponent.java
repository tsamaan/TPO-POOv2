package interfaces;

import models.Notificacion;

/**
 * Interfaz común para el patrón COMPOSITE
 * Permite tratar notificadores individuales y grupos de forma uniforme
 */
public interface INotificationComponent {
    /**
     * Envía una notificación
     * Para notificadores simples: envía por su canal
     * Para grupos (composite): envía por todos los canales del grupo
     */
    void sendNotification(Notificacion notificacion);
    
    /**
     * Retorna el nombre descriptivo del componente
     */
    String getName();
}
