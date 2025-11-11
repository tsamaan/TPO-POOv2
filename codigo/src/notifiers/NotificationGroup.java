package notifiers;

import interfaces.INotificationComponent;
import models.Notificacion;
import java.util.ArrayList;
import java.util.List;

/**
 * Grupo de notificadores (Composite en patrón COMPOSITE)
 * Permite agrupar múltiples notificadores y tratarlos como uno solo
 * 
 * Ejemplo de uso:
 * - AllChannels: Email + Push
 * - CriticalOnly: Solo Push
 * - DevMode: Solo consola
 */
public class NotificationGroup implements INotificationComponent {
    private String name;
    private List<INotificationComponent> components;

    public NotificationGroup(String name) {
        this.name = name;
        this.components = new ArrayList<>();
    }

    /**
     * Agrega un notificador o grupo al composite
     */
    public void add(INotificationComponent component) {
        if (component == null) {
            throw new IllegalArgumentException("El componente no puede ser null");
        }
        components.add(component);
        System.out.println("[+] " + component.getName() + " agregado al grupo '" + name + "'");
    }

    /**
     * Remueve un notificador o grupo del composite
     */
    public void remove(INotificationComponent component) {
        if (components.remove(component)) {
            System.out.println("[-] " + component.getName() + " removido del grupo '" + name + "'");
        }
    }

    /**
     * Obtiene todos los componentes del grupo
     */
    public List<INotificationComponent> getComponents() {
        return new ArrayList<>(components);
    }

    /**
     * Envía la notificación a todos los componentes del grupo
     * (operación composite)
     */
    @Override
    public void sendNotification(Notificacion notificacion) {
        System.out.println("\n🔔 [GRUPO: " + name + "] Enviando a " + components.size() + " canales:");
        for (INotificationComponent component : components) {
            component.sendNotification(notificacion);
        }
    }

    @Override
    public String getName() {
        return "NotificationGroup[" + name + "]";
    }

    /**
     * Retorna información detallada del grupo
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("NotificationGroup '").append(name).append("' (")
          .append(components.size()).append(" componentes):\n");
        for (INotificationComponent comp : components) {
            sb.append("  - ").append(comp.getName()).append("\n");
        }
        return sb.toString();
    }
}
